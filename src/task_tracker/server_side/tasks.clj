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




;; http://stackoverflow.com/questions/11737361/keeping-a-global-monger-connection-accessible-from-all-models
;; http://stackoverflow.com/questions/17950969/when-using-monger-do-i-need-to-supply-connection-each-request

;; user-login - user for that tasks to be returned
(defn get-tasks-list-for-current-user [user-login connection-data]
  (let [res (vec (mc/find-maps (get connection-data :db) "tasks" {:responsible user-login} ))]
    res))


(get-tasks-list-for-current-user "user1" (mg/connect-via-uri "mongodb://madcat:!clojure!@oceanic.mongohq.com:10047/task_tracker"))

