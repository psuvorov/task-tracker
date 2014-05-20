(ns task-tracker.server-side.tasks
  (:use
   task-tracker.server-side.config)
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
(defn get-tasks-list-for-current-user [user-login]
  (let [res (mc/find-maps (get mongo-connection-data :db) "tasks" {:responsible user-login})]
     (vec (for [entry res
           :let [ne (assoc entry :_id (str (:_id entry)))]] ne))))

;;(get-tasks-list-for-current-user "user1")

