(ns task-tracker.client-side.main
  (:require [enfocus.core :as ef]
            [enfocus.effects :as effects]
            [enfocus.events :as ev]
            [enfocus.bind :as bind]
            [goog.dom :as dom]
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
 task-tmpl-page)



(em/defaction clear-active-menu-item-class []
  ".navbar-nav li" (ef/remove-class "active"))

(em/defaction add-active-menu-class [menu-item]
  (str "#" menu-item "_button") (ef/add-class "active"))

(defn navigation-watcher [ky atm oval nval]
  (condp = nval
    ""  (set!  (.-location js/document) "index.html") ;;??
    "about" (client-utils/navigate about-tmpl-page)
    "authenticate" (client-utils/navigate authenticate-tmpl-page)
    "contact" (client-utils/navigate contact-tmpl-page)
    "tasks" (client-utils/navigate tasks-tmpl-page-fn)
    ;;"task" (client-utils/navigate task-tmpl-page)
    (ef/log-debug (pr-str "ERROR IN NAVIGATION" oval nval))))

(def url-hash (atom ""))

(add-watch url-hash :nav navigation-watcher)

(defn update-hash [hash-key]
  (fn [event]
    (.preventDefault event)
    (clear-active-menu-item-class)
    (add-active-menu-class (name hash-key))
    (set! (.-hash (.-location js/document)) (str "#" (name hash-key)))))

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



;; onload event
(set! (.-onload js/window)
  (do
    ;;(js/alert "loaded!")
    ;;(server-calls/is-auth-user-message-sender)
    ;;(setup-menu-links)
    (setup-menu-actions)
    ))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; AJAX processing           ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(defn error-handler [{:keys [status status-text]}]
  (js/alert (str "something bad has happened: " status " " status-text)))





;; Login handler
(defn login-handler [response]
  (do
    (if (= "authorized" response)

      (set! (.-location js/document) "/#tasks")
      (js/alert "Unknown user!"))))

(defn login-attempt-message-sender [login password]

    (x/POST "/login" {
                    :handler login-handler
                    :error-handler error-handler
                    :params {:login login
                             :password password}}))








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




;; Tasks templates
(em/deftemplate tasks-tmpl :compiled "public/templates/tasks.html" [title]
                "#title" (ef/content title))
(defn tasks-tmpl-page-fn []

                (x/POST "/get-tasks-data" {
                    :handler (fn [response] ((em/defaction tasks-tmpl-inner []


                                                           "#container_stage" (ef/do->

                                                                               (ef/content (tasks-tmpl (str response)))

                                                                               (client-utils/reset-scroll))))




                                  )
                    :error-handler error-handler
                    :params {}})










              )





