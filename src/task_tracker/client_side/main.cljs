(ns task-tracker.client-side.main
  (:require [enfocus.core :as ef]
            [enfocus.effects :as effects]
            [enfocus.events :as ev]
            [enfocus.bind :as bind]
            ;;[goog.dom :as dom]
            [ajax.core :as x]
            ;;[cljs-bcrypt-wrapper.core :as bcrypt]
            [task-tracker.client-side.utils :as client-utils]
            )
  (:require-macros [enfocus.macros :as em]))




(declare
 about-tmpl-page
 authenticate-tmpl-page
 contact-tmpl-page
 tasks-tmpl-page-fn
 task-tmpl-page-fn
 denied-tmpl-page);;

(def user-login (atom "")) ;;


(em/defaction logged-in-button-invis [] "#authenticate_button" (ef/add-class "invis") "#logout_button" (ef/remove-class "invis"))
(em/defaction logged-out-button-invis [] "#authenticate_button" (ef/remove-class "invis") "#logout_button" (ef/add-class "invis"))


(em/defaction clear-active-menu-item-class []
  ".navbar-nav li" (ef/remove-class "active"))

(em/defaction add-active-menu-class [menu-item]
  (str "#" menu-item "_button") (ef/add-class "active"))

(defn navigation-watcher [ky atm oval nval]
  (cond
    (= "" nval)  (set!  (.-location js/document) "/") ;;index.html
    (= "about" nval) (client-utils/navigate about-tmpl-page)
    (= "authenticate" nval) (client-utils/navigate authenticate-tmpl-page)
    (= "contact" nval) (client-utils/navigate contact-tmpl-page)
    (= "tasks" nval) (client-utils/navigate tasks-tmpl-page-fn)
    (apply = (map first [nval "task_"])) (client-utils/navigate (task-tmpl-page-fn (.substring nval (inc (.indexOf nval "_")) (count nval))))
    :else (ef/log-debug (pr-str "ERROR IN NAVIGATION" oval nval))))

(def url-hash (atom ""))

(add-watch url-hash :nav navigation-watcher)




(defn update-hash [hash-key]
  (fn [event]
    (.preventDefault event)
    (clear-active-menu-item-class)
    (add-active-menu-class (name hash-key))
    (set! (.-hash (.-location js/document)) (str "#" (name hash-key)))
    ))

(.setInterval
 js/window
 (fn []
   (let [ohash (.-hash (.-location js/document))
         hash (if ohash (.substring ohash 1) "")]
     (when (not (= @url-hash hash))
       (reset! url-hash hash))))
 50)


;; define actions
(em/defaction setup-menu-actions []
              "#authenticate_button" (ev/listen :click (update-hash :authenticate))
              "#about_button" (ev/listen :click (update-hash :about))
              "#contact_button" (ev/listen :click (update-hash :contact))
              "#tasks_button" (ev/listen :click (update-hash :tasks))
              ;;"#task_button" (ev/listen :click (update-hash :task))
              )







;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; AJAX processing           ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn error-handler [{:keys [status status-text]}]
  (js/alert (str "something bad has happened: " status " " status-text)))





;; Login handler
(defn login-handler [response]
  (do
    (if-not (= "" response)
      (do
        (clear-active-menu-item-class)
        (add-active-menu-class (name :tasks))
        (set! user-login response)
        ;;(js/alert (str user-login "**" (string? user-login)))
        (logged-in-button-invis)
        (set! (.-location js/document) (str "#" (name :tasks))))
      (js/alert "Unknown user!"))))

(defn login-attempt-message-sender [login password]

    (x/POST "/login" {
                    :handler login-handler
                    :error-handler error-handler
                    :params {:login login
                             :password password}}))


;; Logout handler

(defn logout-attempt-message-sender []

    (x/GET "/logout" {
                    :error-handler error-handler
                    :params {}}))





(defn check-auth-handler [response]

    (if (or (string? user-login) (not (= response nil)))
    (do (set! user-login (str response))(logged-in-button-invis)
      ;;(js/alert "logged-in-button-invis!")
      )
    (logged-out-button-invis))
)
(defn check-auth-message-sender []

    (x/POST "/check-auth" {
                    :handler check-auth-handler
                    :error-handler error-handler
                    :params {}}))


(em/deftemplate denied-tmpl :compiled "public/templates/denied.html" [])


;; Index templates
(em/deftemplate authenticate-tmpl :compiled "public/templates/authenticate.html" [])
(em/defaction authenticate-tmpl-page []

              "#container_stage" (ef/do->
                      (ef/content (authenticate-tmpl))
                      (client-utils/reset-scroll))

              "#login-btn" (ev/listen
                 :click
                 #(ef/at ;;(.-currentTarget %)
                         (login-attempt-message-sender (str (ef/from "#login" (ef/read-form-input)) )
                                                       (str (ef/from "#password" (ef/read-form-input))))))

              "#cancell-btn" (ev/listen
                 :click
                 #(ef/at (.-currentTarget %)
                         (set! (.-location js/document) "/"))))

(em/deftemplate about-tmpl :compiled "public/templates/about.html" [])
(em/defaction about-tmpl-page []

              "#container_stage" (ef/do->
                      (ef/content (about-tmpl))
                      (client-utils/reset-scroll)))

(em/deftemplate contact-tmpl :compiled "public/templates/contact.html" [])
(em/defaction contact-tmpl-page []

              "#container_stage" (ef/do->
                      (ef/content (contact-tmpl))
                      (client-utils/reset-scroll)))




;; Tasks template
(em/deftemplate tasks-tmpl :compiled "public/templates/tasks.html" [data]
                "#title" (ef/content (str user-login ", your tasks are:"))
                 "#task_table" (ef/content (#(apply str (for [r %]
                                               (str "<tr><td><a href=\"#task_" (:_id r) "\">" (:task_name r) "</a></td><td>" (:created_by r) "</td><td>"
                                                    (:date_start r) "</td><td>" (:date_end r) "</td></tr>"))) data))
                )

(defn tasks-handler [response]
    (if (= "denied" response)
      (ef/at "#container_stage" (ef/content (denied-tmpl)))
      (ef/at "#container_stage" (ef/content (tasks-tmpl response)))))

(defn tasks-tmpl-page-fn []
                (x/POST "/get-tasks-data" {
                    :handler tasks-handler
                    :error-handler error-handler
                    :params {}}))



;; Task template ;;
(em/deftemplate task-tmpl :compiled "public/templates/task.html" [data]
                "#task" (ef/content (#(apply str (for [r %]
                                               (str "<div class='task_desc'>
                                                    <h3>" (:task_name r) "</h3>
                                                    <p>Created by: " (:created_by r) ", date start: " (:date_start r) ", date end: " (:date_end r) "   </p><hr />"
                                                    (apply str (for [t (:task_posts r)]
                                                                 (str "<div class='task_post'><div class='task_head'>Written by: " (:user t) ", at " (:creation_date t) " </div>" (:text t) "</div>")))
                                                    "</div>"))) data)))

(defn task-handler [response]
    (if (= "denied" response)
      (ef/at "#container_stage" (ef/content (denied-tmpl)))
      (ef/at "#container_stage" (ef/content (task-tmpl response)))
      ))

(defn task-tmpl-page-fn [task-id]
  (x/POST "/get-task-data" {
                    :handler task-handler
                    :error-handler error-handler
                    :params {:task-id task-id}}))



;; onload event
(set! (.-onload js/window)
  (do
    (check-auth-message-sender)
    ;;(js/alert "loaded!")
    ;;(server-calls/is-auth-user-message-sender)
    ;;(setup-menu-links)
    (setup-menu-actions)
    ))
