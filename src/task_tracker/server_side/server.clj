(ns task-tracker.server-side.server
  (:use [compojure.core]
        [task-tracker.server-side.utils])
  (:require [ring.adapter.jetty :as jetty]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.params :as params]
            [ring.util.response :as ring-resp]))



(defn login-handler [{params :params}]
  (if (check-user (params "textinput") (params "passwordinput"))
    (ring-resp/redirect "/tasks")

   ))


(defroutes app-routes
  (GET "/" [] (ring-resp/resource-response "views/index.html" {:root "public"}))
  (POST "/login" request (params/wrap-params login-handler request))
  (GET "/tasks" [] (ring-resp/resource-response "views/tasks.html" {:root "public"}))

  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  app-routes)

(defn -main [& args]
  (let [port (Integer/parseInt (get (System/getenv) "PORT" "5000"))]
    (jetty/run-jetty app {:port port :join? false})))
