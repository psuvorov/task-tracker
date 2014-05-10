(defproject enfocus-demp "2.0.0-SNAPSHOT"
  :description "enfocus documentation site"
  :url "http://ckirkendall.github.io/enfocus-site"
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [enfocus "2.1.0-SNAPSHOT"]
                 [compojure "1.1.5"]
                 [ring/ring-core "1.2.2"]
                 [ring/ring-jetty-adapter "1.2.2"]
                 [lib-noir "0.8.2"]
                 [cljs-ajax "0.2.3"]
                 [fogus/ring-edn "0.2.0"]
                 ;;[clj-bcrypt-wrapper "0.1.0"]
                 [cljs-bcrypt-wrapper "0.0.3"]
                 ;;[com.cemerick/friend "0.2.0"]
                 ]
  :plugins [[lein-cljsbuild "0.3.2"]
            [lein-ring "0.8.3"]]
  :cljsbuild {:builds {:prod {:source-paths ["src"],
                              :compiler {:output-to "resources/public/js/main.js"
                                         :optimizations :advanced
                                         :externs ["js/bcrypt-externs.js"]
                                         ;;:externs ["resources/public/js/lang-clj.js"]
                                         }}}}
  :profiles {:dev {:dependencies [[ring "1.2.2"]]}}
  :main task-tracker.server-side.server
  :ring {:handler task-tracker.server-side.server/app})
