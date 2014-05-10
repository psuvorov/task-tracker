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




(defn login-handler [response]
  (js/alert (str "*::" response)))



(defn login-attempt-message [login password]
  (x/POST "/login" {
                    :handler login-handler
                    :error-handler error-handler
                    :params {:login login
                             :password password}}))
