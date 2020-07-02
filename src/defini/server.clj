(ns defini.server
  (:require [defini.sql :as sql]
            [clj-time.core :as time]
            [clj-time.format :as format]
            [clojure.string :as str]
            [clojure.pprint :as pp]
            [clostache.parser :refer [render]]
            [ring.adapter.jetty :as ringa]
            [ring.util.response :as ringu]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]])
  (:gen-class))


(defn pq [xx] (java.util.regex.Pattern/quote xx))

(defn render-any
  "Render rseq to the template file template-name."
  [rseq template-name]
  (let [template (slurp template-name)]
    (render template rseq)))

(defn request-action
  [working-params action]
  (cond (= "show" action)
        [{:foo 1}]
        :else
        {}))

;; Routing happens here. Note two (or) clauses. The first is a safety net, the second is the real thing.
;; The safety net isn't too safe. It currently must have a known action or it returns a 404.

(defn reply-action
  "Generate a response for some request. Params is working-params, which has injected params for the current request."
  [rmap action params]
  (cond (or (nil? rmap)
            (nil? (some #{action} [:catreport])))
        ;; A redirect would make sense, maybe.
        {:status 200
         :headers {"Content-Type" "text/html"}
         :body (format
                "<html><body>Unknown command: %s You probably want: <a href=\"app?action=catreport\">Cat report</a></body</html>"
                (name action))}
        (= :catreport action)
        {:status 200
         :headers {"Content-Type" "text/html"}
         :body (render-any (assoc rmap
                                  :sys-msg "trying all-language"
                                  :all-language (sql/all-language)) "resources/html/new-defini.html")}
        :else
        {:status 200
         :headers {"Content-Type" "text/html"}
         :body (format
                "<html><body>No command: %s Try: <a href=\"app?action=catreport\">Cat report</a></body</html>"
                (name action))}))

;; This throws an exception if action is nil. Work around by setting a default :action.
;; Merging temp-params may be bad. We shouldn't trust the temp-params, but we should explicitly
;; parse out the params we know we want. 

(defn handler 
  "App handler Get request params, call the request action, and call the reply action which should render a web page."
  [request]
  ;; (println (with-out-str (pp/pprint request)))
  (if (empty? (:params request))
    (do
      (println "got an empty request")
      nil)
    (let [temp-params (reduce-kv #(assoc %1 (keyword %2) (keyword (clojure.string/trim %3)))  {} (:params request))
          _ (prn "tp: " temp-params)
          action (get temp-params :action "no-action")
          ras  request
          ;; rmap is a list of records from the db, will full category data
          ;; If there was no :action this merge will add one. If there was an action, this overwrites it.
          working-params (merge temp-params {:action action :test "this is a test"})
          rmap (request-action working-params action)]
      (prn "wp: " working-params)
      (reply-action rmap action working-params))))

;; This def "app" is the only current endpoint. ;; http://localhost:8080/app?whatever
(def app
  (wrap-multipart-params (wrap-params handler)))

;; https://stackoverflow.com/questions/2706044/how-do-i-stop-jetty-server-in-clojure
;; Unclear how defonce and lein ring server headless will play together.

(defn ds []
  (defonce server (ringa/run-jetty app {:port 8080 :join? false})))

;; Need -main for 'lien run', but it is ignored by 'lein ring'.
;; http://localhost:8080/app?action=catreport
(defn -main []
  (ds))




(defn makefresh []
  (.stop server)
  (ds)
  (.start server)
  )

