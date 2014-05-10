(ns task-tracker.client-side.index
  (:require [enfocus.core :as ef]
            [enfocus.effects :as effects]
            [enfocus.events :as ev]
            [enfocus.bind :as bind]
            [goog.dom :as dom]
            [ajax.core :as x]
            [task-tracker.client-side.ajax :as server-calls])
  ;;(:use task-tracker.client-side.ajax)
  (:require-macros [enfocus.macros :as em]))

(declare
 ;;home
         about-tmpl-page
         authenticate-tmpl-page
         contact-tmpl-page
         )

(defn scroll-to []
  (fn [nod]
    (. nod (scrollIntoView))))

(defn reset-scroll []
  (fn [nod]
    (set! (.-scrollTop nod) 0)))

(em/defaction clear-active-menu-item-class []
  ".navbar-nav li" (ef/remove-class "active"))

(em/defaction add-active-menu-class [menu-item]
  (str "#" menu-item "_button") (ef/add-class "active"))



(defn navigate [page]
  (page)
  (.scrollTo js/window 0 0))


(defn navigation-watcher [ky atm oval nval]
  (condp = nval
    ""  (set!  (.-location js/document) "index.html") ;;??
    "about" (navigate about-tmpl-page)
    "authenticate" (navigate authenticate-tmpl-page)
    "contact" (navigate contact-tmpl-page)
    (ef/log-debug (pr-str "ERROR IN NAVIGATION" oval nval))))

(def url-hash (atom ""))

(add-watch url-hash :nav navigation-watcher)

(defn update-hash [hash-key]
  (fn [event]
    (.preventDefault event)
    ;;(js/alert hash-key)
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
              "#contact_button" (ev/listen :click (update-hash :contact)))

(em/defaction setup-menu-links [] "#main_menu"
  (ef/html-content
   "
   <li id='about_button'><a href='/#about'>About</a></li>
   <li id='authenticate_button'><a href='/#authenticate'>Login/Register</a></li>
   <li id='contact_button'><a href='/#contact'>Contact</a></li>

   "))


(defn handler [response]
  (.log js/console (str response)))

(defn error-handler [{:keys [status status-text]}]
  (.log js/console (str "something bad happened: " status " " status-text)))

(defn test-ajax []
  (x/GET "/tt"
        {:params {:message "Hello World"
                  :user    "Bob"}
         :handler handler
         :error-handler error-handler}))




(set! (.-onload js/window)
  (do
    (test-ajax)
    ;;(js/alert "load!")
    (setup-menu-links)
    (setup-menu-actions)
    ;;(update-hash :about)
    ))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Index templates           ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(em/deftemplate authenticate-tmpl :compiled "public/templates/authenticate.html" [])
(em/defaction authenticate-tmpl-page []
  "#container_stage" (ef/do->
                      (ef/content (authenticate-tmpl))
                      (reset-scroll)))

(em/deftemplate about-tmpl :compiled "public/templates/about.html" [])
(em/defaction about-tmpl-page []
  "#container_stage" (ef/do->
                      (ef/content (about-tmpl))
                      (reset-scroll)))

(em/deftemplate contact-tmpl :compiled "public/templates/contact.html" [])
(em/defaction contact-tmpl-page []
  "#container_stage" (ef/do->
                      (ef/content (contact-tmpl))
                      (reset-scroll)))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Custom events             ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;(ef/at "#login-btn" (ev/listen :click
;;                             (;;server-calls/login-attempt-message
;;                              ;;(js/alert (str (ef/from "#login" (ef/read-form)) " :: " (ef/from "#password" (ef/get-text))))
;;                              (server-calls/login-attempt-message "bbbbbbguhrty")
;;
;;                              ;;{:login (ef/from "#login" (ef/get-text))
;;                               ;;:password (ef/from "#password" (ef/get-text))}
;;                              )))

