(ns task-tracker.client-side.utils
  (:require [enfocus.core :as ef]
            [enfocus.effects :as effects]
            [enfocus.events :as ev]
            [enfocus.bind :as bind]
            ;;[goog.dom :as dom]
            [ajax.core :as x])
  	(:require-macros [enfocus.macros :as em]))



;; Login helpers
;;(def secret-hash "$2a$10$mlJUX2qOS6jGxwv7y39Y4OJsIPUtbTkIV6GU1bODoR9auVM96QUpu")



(defn scroll-to []
  (fn [nod]
    (. nod (scrollIntoView))))

(defn reset-scroll []
  (fn [nod]
    (set! (.-scrollTop nod) 0)))

(defn navigate [page]
  (page)
  (.scrollTo js/window 0 0)
  )
