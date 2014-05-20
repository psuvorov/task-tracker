(ns task-tracker.server-side.config
  (:require
   [monger.core :as mg]))



(def mongo-connection-data (mg/connect-via-uri "mongodb://madcat:!qwerty@oceanic.mongohq.com:10047/task_tracker"))
