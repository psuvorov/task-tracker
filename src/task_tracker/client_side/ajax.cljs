(ns task-tracker.client-side.ajax
  (:require [enfocus.core :as ef]
            [enfocus.effects :as effects]
            [enfocus.events :as ev]
            [enfocus.bind :as bind]
            [goog.dom :as dom]
            [ajax.core :as x])
  	(:require-macros [enfocus.macros :as em]))


(defn error-handler [{:keys [status status-text]}]
  (js/alert (str "something bad has happened: " status " " status-text)))




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Login handler             ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn login-handler [response]
  (if (= "authorized" response)
    (set! (.-location js/document) "/tasks")
    (js/alert "Unknown user!")))

(defn login-attempt-message-sender [login password]
  (x/POST "/login" {
                    :handler login-handler
                    :error-handler error-handler
                    :params {:login login
                             :password password}}))




;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Authorization handler     ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn is-auth-user-handler [response]
  (.log js/console response))




(defn is-auth-user-message-sender []
  (x/POST "/is-auth-user" {
                    :handler is-auth-user-handler
                    :error-handler error-handler
                    :params {:test "param-test"
                             }}))
