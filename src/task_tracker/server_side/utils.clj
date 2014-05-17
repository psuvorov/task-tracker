(ns task-tracker.server-side.utils
  (:use [compojure.core]
        [task-tracker.server-side.utils]
        [task-tracker.server-side.auth]
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









;; for debuggin purposes
(defn check-state []
  (do
    (println (session/get :login))))







