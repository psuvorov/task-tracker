(ns task-tracker.server-side.tasks
  (:require
   [ring.middleware.params :as params]
   [noir.session :as session]
   [ring.middleware.session.memory :refer [memory-store]]
   [monger.core :as mg]
   [monger.collection :as mc]
   [monger.conversion :refer [from-db-object]])
  (:import [com.mongodb MongoOptions ServerAddress]
           (org.bson.types ObjectId)))



;; user-login - user for that tasks to be returned
(defn get-tasks-list-for-current-user [user-login connection-data]
  (let [res (mc/find-maps (get connection-data :db) "tasks" {:responsible user-login})]
     (for [entry res
           :let [ne (assoc entry :_id (str (:_id entry)))]] ne)))

;;(get-tasks-list-for-current-user "user1" (mg/connect-via-uri "mongodb://madcat:[pass]@oceanic.mongohq.com:10047/task_tracker"))

