(defproject definitionary ""
  :description "Inverse dictionary"
  :url "https://github.com/twl8n/definitionary"
  :license {:name "GNU Lesser General Public License v3.0"
            :url "https://github.com/twl8n/definitionary/blob/main/LICENSE"}
  :plugins [[lein-ring "0.8.10"]
            [lein-ancient "0.6.10"]]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "0.3.443"]
                 [clj-http "3.7.0"] ;; used in http request experiments
                 [clj-time "0.15.0"]
                 [org.clojure/java.jdbc "0.7.0-alpha3"]
                 ;; Whereever org.clojure/java.jdbc "0.3.5" came from, it is more than 2 years out of date.
                 ;; [org.clojure/java.jdbc "0.3.5"]
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [de.ubercode.clostache/clostache "1.4.0"]
                 ;; [cljstache "2.0.0"]
                 [org.clojure/tools.namespace "0.2.11"]
                 [ring "1.5.0"]
                 [ring/ring-core "1.2.1"]
                 [ring/ring-jetty-adapter "1.2.1"]]
  ;; You need a :ring with lein ring or lein ring server-headless
  ;; New: Use :init to start up jetty with the handlers.
  ;; Old: However, you must send the request through the function that wraps the handler with
  ;; with wrap-params, and any other wrap-* decorators.
  ;; :ring {:handler defini.server/app
  ;;        :init defini.server/ds}

  :main ^:skip-aot defini.server
  :uberjar-name "defini-standalone.jar"
  :jar-name "definit.jar"
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
