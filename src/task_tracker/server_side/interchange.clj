(ns task-tracker.server-side.interchange
  (:use [compojure.core]
        [task-tracker.server-side.auth]
        [task-tracker.server-side.tasks]
        [ring.middleware.edn])
  (:require [ring.adapter.jetty :as jetty]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.params :as params]
            [ring.util.response :as ring-resp]
            [noir.session :as session]))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Authorization handler     ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn login-handler [login password]
  (if (check-user login password)
    (let [roles (get-user-roles login)]
      (do
        (println (str "Hello, " login))
        (session/put! :login login)
        (session/put! :roles roles)
        (println (session/get :connection))
        {:status 200
         :headers {"Content-Type" "application/edn"}
         :body (pr-str "authorized")}))
    (do
      (println "bad!!" ":: " login " / " password)
      {:status 200
       :headers {"Content-Type" "application/edn"}
       :body (pr-str "Unknown user")})))

(defn login-response [{params :params}]
  (login-handler (get params :login) (get params :password)))





;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; User permission handler   ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn user-permission-handler []
   (do
     {:status 200
       :headers {"Content-Type" "application/edn"}
       :body (pr-str (session/get :login))}))

(defn user-permission-response [{params :params}]
    (user-permission-handler (get params :test)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; User tasks handler        ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn user-tasks-handler [request connection-data]
     (if (session/get :login)
       {:status 200
       :headers {"Content-Type" "application/edn"}
       :body (pr-str (get-tasks-list-for-current-user "user1" connection-data))} ;;(get-tasks-list-for-current-user "user1" connection-data)
       {:status 200
       :headers {"Content-Type" "application/edn"}
       :body (pr-str "denied")}
       ))
(defn user-tasks-response [request connection-data]
    (user-tasks-handler request connection-data))
