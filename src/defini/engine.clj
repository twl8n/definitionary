(ns defini.engine
  (:require [defini.sql :as sql]
            [clojure.data :as data]
            [clostache.parser :refer [render]]))

(def params (atom {}))
(def output (atom {}))

(defn render-any
  "Render rseq to the template file template-name."
  [rseq template-name]
  (let [template (slurp template-name)]
    (render template rseq)))

(defn have-params []
  (if (empty? @params)
    false
    true))

(defn action-catreport []
  (if (= :catreport (:action @params))
    (do
      (reset! output
              {:status 200
               :headers {"Content-Type" "text/html"}
               :body (render-any (assoc @params
                                        :sys-msg "trying all-language"
                                        :all-language (sql/all-language)) "resources/html/new-defini.html")})
      true)
    false))

(defn action-ext []
  (if (= :ext (:action @params))
    (do
      (reset! output
              {:status 200
               :headers {"Content-Type" "text/html"}
               :body (render-any (assoc @params
                                        :sys-msg "trying ext"
                                        :all-language (load-file "src/defini/ext.clj")) "resources/html/new-defini.html")})
      true)
    false))


(defn render-help []
  (reset! output
          {:status 200
           :headers {"Content-Type" "text/html"}
           :body (format
                  "<html><body>Unknown command: %s You probably want: <a href=\"app?action=catreport\">Cat report</a></body</html>"
                  (name (:action @params)))})
  true)

(defn wait [] (prn wait))

(defn action-savedefini []
  (prn @params)
  true)

;; If the state is not found, this returns nil.
(defn find-state
  "Find a state in the state table, based on the state name. State names must be unique."
  [which-table which-state]
  (first (filter some? (map (fn [smap] (if (= which-state (:name smap)) smap nil)) which-table))))

(defn run
  "Run the state machine."
  [which-table]
  (loop [curr-state (find-state which-table :start)]
    (let [result ((:fn curr-state))
          next-state (if result (:true curr-state) (:false curr-state))]
      (when (not= :wait next-state)
        (recur (find-state which-table next-state))))))

(defn what-is
  "Prompt the user for function result. True is t or y. Anything else is false."
  [curr-fn]
  (println "What is the t/f (y/n) for function " (name curr-fn))
  (let [raw-input (read-line)]
    (some? (re-matches #"[ty]+" raw-input))))

(defn test-run
  "Like run, but prompt user for the result. This only exercises the state table, not any side effects of functions."
  [which-table]
  (loop [curr-state (find-state which-table :start)]
    (let [result (what-is (str (:fn curr-state)))
          next-state (if result (:true curr-state) (:false curr-state))]
      (if (= :wait next-state)
        (println "Hit wait")
        (recur (find-state which-table next-state))))))

(defn check-tf-states
  "Sanity check the state table."
  [which-table]
  (let [all-name (set (map :name which-table))
        all-t (set (map :true which-table))
        all-f (set (map :false which-table))]
    (when (some? (first (data/diff all-t all-name)))
      (prn "Error: found states in true that are not known names: " (first (data/diff all-t all-name))))
    (when (some? (first (data/diff all-f all-name)))
      (prn "Error: found states in false that are not known names: " (first (data/diff all-f all-name))))
    (data/diff all-f all-name)))

;; This is the entire logic for the definitionary behavior.
(def s-table
  [
   {:name :start :fn have-params :true :action-catreport :false :render-help}
   {:name :action-catreport :fn action-catreport :true :wait :false :action-savedefini}
   {:name :action-savedefini :fn action-savedefini :true :wait :false :action-ext}
   {:name :action-ext :fn action-ext :true :wait :false :render-help}
   {:name :render-help :fn render-help :true :wait :false :wait}
   {:name :wait :fn wait :true :wait :false :wait}
   ])