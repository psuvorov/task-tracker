(ns task-tracker.server-side.utils
  (:use task-tracker.server-side.config
        noir.util.crypt
        ))


(defn check-user [login pass]
  (= 1 (count (filter
               (fn [user]
                 (and
                  (= login (get user :login))
                  (= pass (get user :password))))
               users))))


(defn get-user-roles [user-login]
  (get (first (filter (fn [user] (= user-login (get user :login)))
          users)) :roles))


(encrypt "aa")
(compare "aaa" (encrypt "aa"))
(get-user-roles "admin")
(check-user "admin" "secret")
