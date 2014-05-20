(ns task-tracker.server-side.auth
  (:use
   task-tracker.server-side.config)

  (:require
   [ring.middleware.params :as params]
   [noir.session :as session]
   [ring.middleware.session.memory :refer [memory-store]]
   [monger.core :as mg]
   [monger.collection :as mc]
   [noir.util.crypt :as crypt]
   [monger.conversion :refer [from-db-object]])
  (:import [com.mongodb MongoOptions ServerAddress]))





(defn- get-all-users-col []
  (let [res (map (fn [entry]
                        (dissoc entry :_id)) ;; exclude _id field
                      (mc/find-maps (get mongo-connection-data :db) "users" {}))]
    res))



(defn check-user [login pass]
    (do
      (println login)
      (println pass)
      (= 1 (count (filter
               (fn [user]
                 (and
                  (= login (get user :user_login))
                  (crypt/compare pass (get user :password_hash))))
               (get-all-users-col))))))




(defn get-user-roles [user-login]
  (get (first (filter (fn [user] (= user-login (get user :user_login)))
          (get-all-users-col))) :roles))



