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

(defn action-list []
  (if (= :list (:action @params))
    (do
      (reset! output
              {:status 200
               :headers {"Content-Type" "text/html"}
               :body (render-any (assoc @params
                                        :sys-msg "trying all-language"
                                        :next-id (+ (Integer/parseInt (:id @params)) 10)
                                        :word-def-list (sql/word-def-list id)) "resources/html/list.html")})
      true)
    false))

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
                  (name (or (:action @params) "<none>")))})
  true)

(defn wait [] (prn wait))

(defn action-savedefini []
  (if (= :savedefini (:action @params))
    (do
      (let [id (sql/save-defini (select-keys @params [:id :lang :myword :phrase]))]
        (reset! params (assoc @params :id id))
        (sql/get-defini (select-keys @params [:id :lang])))
      (reset! output
              {:status 200
               :headers {"Content-Type" "text/html"}
               :body (render-any (assoc @params
                                        :sys-msg (str "Saved" (:myword @params))
                                        :all-language
                                        (map (fn [xx]
                                               (assoc xx :selected (= (str (:id xx)) (str (:lang @params)))))
                                             (sql/all-language))) "resources/html/new-defini.html")})
      true)
    false))


(comment
  (let [curr-lang "1"]
    (map (fn [xx] (assoc xx :checked (= (str (:id xx)) (str curr-lang)))) (sql/all-language)))
  
  (select-keys {:action :savedefini
                :lang "2"
                :myword "foo"
                :phrase "foo is a thing" :Submit "Save word/defini"}
               [:lang :myword :phrase])

  ((juxt :lang :myword :phrase) {:action :savedefini
                                 :lang "2"
                                 :myword "foo"
                                 :phrase "foo is a thing" :Submit "Save word/defini"})

  (update {:action "foo"} :action keyword)

  (let [mm {:action "foo"}]
    (assoc mm :action (keyword (:action mm))))
  )


;; If the state is not found, this returns nil.
(defn find-state
  "Find a state in the state table, based on the state name. State names must be unique."
  [which-table which-state]
  (first (filter some? (map (fn [smap] (if (= which-state (:name smap)) smap nil)) which-table))))

;; Assumes that state :fn functions are side-effecty (although not necessarily). The state functions must
;; return a boolean true for success.

;; 2020-07-03 Suggest: return @output from fn run

(defn run
  "Run the state machine."
  [which-table]
  (loop [curr-state (:start which-table)]
    (let [result ((:fn curr-state))
          next-state (get curr-state result)]
      (when (not= :wait next-state)
        (recur (get which-table next-state))))))

(defn what-is
  "Prompt the user for function result. True is t or y. Anything else is false."
  [curr-fn]
  (println "What is the t/f (y/n) for function " (name curr-fn))
  (let [raw-input (read-line)]
    (some? (re-matches #"[ty]+" raw-input))))

(defn test-run
  "Like run, but prompt user for the result. This only exercises the state table, not any side effects of functions."
  [which-table]
  (loop [curr-state (:start which-table)]
    (let [result (what-is (str (:fn curr-state)))
          next-state (get curr-state result)]
      (if (= :wait next-state)
        (println "Hit wait")
        (recur (get which-table next-state))))))

(defn check-tf-states
  "Sanity check the state table."
  [which-table]
  (let [all-name (set (keys which-table))
        all-t (set (map :true (vals which-table)))
        all-f (set (map :false (vals which-table)))]
    (when (some? (first (data/diff all-t all-name)))
      (prn "Error: found states in true that are not known names: " (first (data/diff all-t all-name))))
    (when (some? (first (data/diff all-f all-name)))
      (prn "Error: found states in false that are not known names: " (first (data/diff all-f all-name))))
    (data/diff all-f all-name)))

(comment
  (check-tf-states s-table)
  )

;; elisp to align the s-table
;; (defun align-st (beg end)
;;   (interactive "r")
;;   (align-regexp beg end "\\(\\s-*\\)[{:]" 1 1 t))

;; This is the entire logic for the definitionary behavior.
(def s-table
  {
   :start             {:fn have-params       true :action-catreport false :render-help}
   :action-catreport  {:fn action-catreport  true :wait             false :action-savedefini}
   :action-savedefini {:fn action-savedefini true :wait             false :action-list}
   :action-list       {:fn action-list       true :wait             false :action-ext}
   :action-ext        {:fn action-ext        true :wait             false :render-help}
   :render-help       {:fn render-help       true :wait             false :wait}
   :wait              {:fn wait              true :wait             false :wait}
   })


