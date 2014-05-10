(ns task-tracker.server-side.server
  (:use [compojure.core]
        [task-tracker.server-side.utils]
        [ring.middleware.edn])
  (:require [ring.adapter.jetty :as jetty]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.params :as params]
            [ring.util.response :as ring-resp]
            ;;[ring.middleware.session :as ring-session]
            [noir.session :as session]
            [ring.middleware.session.memory :refer [memory-store]]
            ))





(defn login-handler [{params :params}]
  (if (check-user (params "login") (params "password"))
    (let [login (params "login")
          roles (get-user-roles login)]
      (do
        (session/put! :login (params "login"))
        (session/put! :roles roles)
        (ring-resp/redirect "/tasks")))
    (do
      ;;(ring-resp/redirect "/")
      ;;(generate-response "nope!")
      )))

(defn check-state []
  (do
    ;;(session/put! :login "azaza")
    (println (session/get :login))
    ))


(defn generate-response [{params :params}]
  {:status 200
   :headers {"Content-Type" "application/edn"}
   :body (pr-str (str "Hello, " (get params :login) " :: " (get params :password)))
   })


(defroutes app-routes
  (GET "/" [] (ring-resp/resource-response "views/index.html" {:root "public"}))
  ;;(POST "/login" [request] (params/wrap-params login-handler request))
  (POST "/login" [request] (params/wrap-params generate-response request))
  (GET "/tasks" [] (ring-resp/resource-response "views/tasks.html" {:root "public"}))
  (GET "/tt" [request] (params/wrap-params generate-response request))
  (GET "/state" [request] (check-state))

  (route/resources "/")
  (route/not-found "Not Found"))

(def app
  ( ->
    ;;params/wrap-params
    app-routes
    ;;handler/site
    wrap-edn-params
    ;;ring-session/wrap-session
    (session/wrap-noir-session
     {:store(memory-store)})
    ))

(defn -main [& args]
  (let [port (Integer/parseInt (get (System/getenv) "PORT" "5000"))]
    (jetty/run-jetty app {:port port :join? false})))
