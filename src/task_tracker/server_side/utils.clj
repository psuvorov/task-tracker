(ns task-tracker.server-side.utils
  (:use task-tracker.server-side.config
        noir.util.crypt))

(defn check-user [login pass]
  (= 1 (count (filter
               (fn [user]
                 (and
                  (= login (get user :login))
                  (= pass (get user :password))))
               users))))




(compare "aaa" (encrypt "aa"))
