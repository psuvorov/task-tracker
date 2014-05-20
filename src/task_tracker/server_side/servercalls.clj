(ns task-tracker.server-side.servercalls
  (:use [compojure.core]
        [task-tracker.server-side.auth]
        [task-tracker.server-side.tasks]
        [ring.middleware.edn])
  (:require [ring.adapter.jetty :as jetty]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.params :as params]
            [ring.util.response :as ring-resp]
            [noir.session :as session]
            [monger.core :as mg]
            [monger.collection :as mc]))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Authorization handler     ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn login-handler [login password]
  (do
    (println login "**" password)
    (if (check-user login password)
    (let [roles (get-user-roles login)]
      (do
        (println (str "Hello, " login))
        (session/put! :login login)
        (session/put! :roles roles)
        {:status 200
         :headers {"Content-Type" "application/edn"}
         :body (pr-str login)}))
    (do
      (println "bad!!" ":: " login " / " password)
      {:status 200
       :headers {"Content-Type" "application/edn"}
       :body (pr-str "")}))))

(defn login-response [{params :params}]
  (login-handler (get params :login) (get params :password)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Logout handler            ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn logout-handler []
  (do
    (session/put! :login nil)
    (ring-resp/redirect "/")))



(defn logout-response [request]
  (logout-handler))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Check auth handler        ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn check-auth-handler []
  {:status 200
   :headers {"Content-Type" "application/edn"}
   :body (pr-str (session/get :login))})

(defn check-auth-response [request]
    (check-auth-handler))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; User tasks handler        ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn user-tasks-handler [request]
     (if (session/get :login)
       {:status 200
       :headers {"Content-Type" "application/edn"}
       :body (pr-str (get-tasks-list-for-current-user (session/get :login)))}
       {:status 200
       :headers {"Content-Type" "application/edn"}
       :body (pr-str "denied")}
       ))
(defn user-tasks-response [request]
    (user-tasks-handler request))


