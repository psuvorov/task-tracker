(ns task-tracker.server-side.server
  (:use [compojure.core]
        [task-tracker.server-side.utils]
        [task-tracker.server-side.tasks]
        [task-tracker.server-side.interchange]
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
  (POST "/get-tasks-data" [request] (user-tasks-response (params/wrap-params request) (do (println "aaaaa")
                                                                                        ;;(session/put! :conn-data (mg/connect-via-uri "mongodb://madcat:!clojure!@oceanic.mongohq.com:10047/task_tracker"))
                                                                                        (session/get :conn-data))))




  ;; Service requests
  (GET "/state" [request] (check-state))
  (route/resources "/")
  (route/not-found (ring-resp/resource-response "public/views/not-found.html"))





  )


(defn app-init [handler]
  (fn [req]
    (session/put! :conn-data (mg/connect-via-uri "mongodb://madcat:x@oceanic.mongohq.com:10047/task_tracker"))
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
