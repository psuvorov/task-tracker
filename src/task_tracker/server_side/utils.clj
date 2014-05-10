(ns task-tracker.server-side.utils
  (:use [compojure.core]
        [task-tracker.server-side.utils]
        [ring.middleware.edn]
        [task-tracker.server-side.config])
  (:require [ring.adapter.jetty :as jetty]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.params :as params]
            [ring.util.response :as ring-resp]
            [noir.session :as session]
            [noir.util.crypt :as crypt]
            [ring.middleware.session.memory :refer [memory-store]]
            ))


(defn check-user [login pass]
  (= 1 (count (filter
               (fn [user]
                 (and
                  (= login (get user :login))
                  (noir.util.crypt/compare "s" pass))) ;; temporary; working with the db is coming
               users))))


(defn get-user-roles [user-login]
  (get (first (filter (fn [user] (= user-login (get user :login)))
          users)) :roles))


(defn check-state []
  (do
    (println (session/get :login))))




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Authorization handler     ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn login-handler [login password]
   (if (check-user login password)
    (let [roles (get-user-roles login)]
      (do
        (println "ok!!")
        (session/put! :login login)
        (session/put! :roles roles)
        {:status 200
         :headers {"Content-Type" "application/edn"}
         :body (pr-str "authorized")}))
    (do
      (println "bad!!" ":: " login " / " password)
      {:status 200
       :headers {"Content-Type" "application/edn"}
       :body (pr-str "unknown")})))

(defn login-response [{params :params}]
  (login-handler (get params :login) (get params :password)))





;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; User permission handler   ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn user-permission-handler [test]
   (do
     (println test)
     {:status 200
       :headers {"Content-Type" "application/edn"}
       :body (pr-str (session/get :login))}))

(defn user-permission-response [{params :params}]


    (user-permission-handler (get params :test))
    )


