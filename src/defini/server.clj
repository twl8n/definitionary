(ns defini.server
  (:require [defini.sql :as sql]
            [defini.engine :as engine]
            [clj-time.core :as time]
            [clj-time.format :as format]
            [clojure.string :as str]
            [clojure.pprint :as pp]
            [clostache.parser :refer [render]]
            [ring.adapter.jetty :as ringa]
            [ring.util.response :as ringu]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.multipart-params :refer [wrap-multipart-params]]
            [ring.middleware.basic-authentication :refer [wrap-basic-authentication]])
  (:gen-class))

;; This throws an exception if action is nil. Work around by setting a default :action.
;; Merging temp-params may be bad. We shouldn't trust the temp-params, but we should explicitly
;; parse out the params we know we want. 

;; (reduce-kv #(assoc %1 (keyword %2) (clojure.string/trim %3))  {} local-params)

;; 2020-07-03 We used to check (if (empty? local-params) and return nil, but I'm not sure that made sense then or now.

;; Why not return value from engine/run?
(defn handler 
  "App handler Get request params, call the request action, and call the reply action which should render a web page."
  [request]
  (reset! defini.engine/params
          (reduce-kv
           (fn [acc kk vv]
             (let [trim-vv (clojure.string/trim vv)
                   kword (keyword kk)]
               (assoc acc kword
                      (if (= kword :action)
                        (keyword trim-vv)
                        trim-vv))))
           {} (:params request)))
  (prn @defini.engine/params)
  (engine/run defini.engine/s-table)
  @defini.engine/output)


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


