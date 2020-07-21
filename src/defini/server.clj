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

;; (if (and (= kk :id) (empty? vv)) nil vv)
;; That cond seems ugly. It has an implicit fall-through, which can't be idiomatic Clojure.
(defn clean-params
  "Make some systemic updates to params. Universal type conversions go here."
  [kk vv]
  (cond (= kk :action)
        {kk (keyword (clojure.string/trim vv))}
        (and (= kk :id) (re-matches #"\d+" vv))
        {kk (Integer/parseInt vv)}
        (= kk :id)
        {kk nil}
        :else
        {kk vv}))

;; 2020-07-03 We used to check (if (empty? local-params) and return nil, but I'm not sure that made sense then or now.

;; Seems like we should only parse params we expect, and drop the others on the floor (or log them).
;; Having to maintain a known param list creates work for the devs, but it also makes the app more secure.
;; Why not return value from engine/run?

(defn handler 
  "App handler Get request params, call the request action, and call the reply action which should render a web page."
  [request]
  (let [params (reduce-kv
                (fn [acc kk vv]
                  (let [trim-vv (clojure.string/trim vv)
                        kword (keyword kk)]
                    (merge acc (clean-params kword trim-vv))))
                {}
                (:params request))]
    (reset! defini.engine/params params)
    (engine/run defini.engine/s-table)
    @defini.engine/output))


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


