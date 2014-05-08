(ns task-tracker.server-side.config)

(def users
  [{:login "admin" :password "secret" :roles #{:admin}}
   {:login "user1" :password "secret" :roles #{:user}}
   {:login "user2" :password "secret" :roles #{:user}}
   {:login "user3" :password "secret" :roles #{:user}}
   {:login "user4" :password "secret" :roles #{:user}}])
