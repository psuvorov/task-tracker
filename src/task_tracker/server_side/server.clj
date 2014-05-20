(ns task-tracker.server-side.server
  (:use [compojure.core]
        [task-tracker.server-side.utils]
        [task-tracker.server-side.tasks]
        [task-tracker.server-side.servercalls]
        [ring.middleware.edn]

        )
  (:require [ring.adapter.jetty :as jetty]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.params :as params]
            [ring.util.response :as ring-resp]
            [noir.session :as session]
            [noir.util.middleware :as middleware]
            [ring.middleware.session.memory :refer [memory-store]]
            [monger.core :as mg]
            ))





(defroutes app-routes
  ;; Normal requests
  (GET "/" [] (ring-resp/resource-response "public/views/index.html"))

  ;; EDN requests
  (POST "/login" [request] (params/wrap-params login-response request))
  (GET "/logout" [request] (params/wrap-params logout-response request))
  (POST "/check-auth" [request] (params/wrap-params check-auth-response request))
  (POST "/get-tasks-data" [request] (params/wrap-params user-tasks-response request))
  (POST "/get-task-data" [request] (params/wrap-params user-task-response request))

  ;; Service requests
  (GET "/state" [request] (check-state))
  ;;(GET "/denied" [] (ring-resp/resource-response "public/views/denied.html"))
  (route/resources "/")
  (route/not-found (ring-resp/redirect "#not-found")))


(defn app-init [handler]
  (fn [req]
    (println "Starting app...")
    (handler req)))




(def app (middleware/app-handler
          [(-> app-routes
               wrap-edn-params
               handler/site)]
          :session-options {:store (memory-store)}
          :middleware [app-init]))


(defn -main [& args]
  (let [port (Integer/parseInt (get (System/getenv) "PORT" "5000"))]
    (jetty/run-jetty app {:port port :join? false})))
