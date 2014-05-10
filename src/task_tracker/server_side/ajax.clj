(ns task-tracker.server-side.ajax
  (:use [compojure.core]
        [task-tracker.server-side.utils]
        [ring.middleware.edn])
  (:require [ring.adapter.jetty :as jetty]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [ring.middleware.params :as params]
            [ring.util.response :as ring-resp]))
