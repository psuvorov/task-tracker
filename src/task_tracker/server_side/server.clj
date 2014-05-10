(ns task-tracker.server-side.server
  (:use [compojure.core]
        [task-tracker.server-side.utils]
        [ring.middleware.edn])
  (:require [ring.adapter.jetty :as jetty]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.params :as params]
            [ring.util.response :as ring-resp]
            [noir.session :as session]
            [ring.middleware.session.memory :refer [memory-store]]
            ))





(defroutes app-routes
  ;; Normal requests
  (GET "/" [] (ring-resp/resource-response "views/index.html" {:root "public"}))
  (GET "/tasks" []
       (if (session/get :login)
         (ring-resp/resource-response "views/tasks.html" {:root "public"})
         (ring-resp/resource-response "views/denied.html" {:root "public"})))

  ;; EDN requests
  (POST "/login" [request] (params/wrap-params login-response request))
  (POST "/is-auth-user" [request] (params/wrap-params user-permission-response request))


  ;; Service requests
  (GET "/state" [request] (check-state))
  (route/resources "/")
  (route/not-found (ring-resp/resource-response "views/not-found.html" {:root "public"}))





  )





(def app
  ( ->
    app-routes
    wrap-edn-params
    (session/wrap-noir-session
     {:store (memory-store)})

   ))

(defn -main [& args]
  (let [port (Integer/parseInt (get (System/getenv) "PORT" "5000"))]
    (jetty/run-jetty app {:port port :join? false})))
