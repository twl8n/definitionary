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
    (let [starting-id (or (:id @params)  1)
          next-id (+ starting-id 10)
          word-def-list (sql/word-def-list starting-id)]
      (reset! output
              {:status 200
               :headers {"Content-Type" "text/html"}
               :body (render-any (assoc @params
                                        :sys-msg "trying all-language"
                                        :next-id next-id
                                        :word-def-list word-def-list) "resources/html/list.html")})
      true)
    false))

(defn action-edit []
  (if (= :edit (:action @params))
    (do
      (reset! output
              {:status 200
               :headers {"Content-Type" "text/html"}
               :body (render-any (merge
                                  @params
                                  (sql/get-defini (select-keys @params [:id :lang]))
                                  {:sys-msg "trying all-language"
                                   :all-language (map (fn [xx]
                                                        (assoc xx :selected (= (str (:id xx)) (str (:lang @params)))))
                                                      (sql/all-language))})
                                 "resources/html/new-defini.html")})
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
                  "<html><body>Unknown command: %s You probably want: <a href=\"app?action=edit\">Edit</a></body</html>"
                  (name (or (:action @params) "<none>")))})
  true)

(defn wait [] (prn wait))

(defn action-savedefini []
  (if (= :savedefini (:action @params))
    true
    false))

(defn action-overwrite-ok []
  (if (= :overwrite-ok (:action @params))
    true
    false))

(defn id-lang-exists
  "Check to see if we have an existing id+lang."
  []
  (sql/id-lang-exists (select-keys @params [:id :lang])))
  
(defn insert-defini
  "id is nil, or id+lang doesn't exist."
  []
  (let [[inserted-id msg] (sql/insert-defini (select-keys @params [:id :lang :myword :phrase]))]
    (reset! params (merge @params
                          (sql/get-defini {:id inserted-id :lang (:lang @params)})
                          {:sys-msg (str (:sys-msg @params) msg)}))
    true))


(defn update-defini
  "id+lang exists and we've been given the ok to overwrite"
  []
  (let [[id msg] (sql/update-defini (select-keys @params [:id :lang :myword :phrase]))]
    (reset! params (merge @params
                          {:sys-msg msg}
                          (sql/get-defini (select-keys @params [:id :lang]))))
      true))

(defn edit-defini
  "Draw a web page with the current entry."
  []
  (reset! output
          {:status 200
           :headers {"Content-Type" "text/html"}
           :body (render-any (assoc @params
                                    :all-language
                                    (map (fn [xx]
                                           (assoc xx :selected (= (str (:id xx)) (str (:lang @params)))))
                                         (sql/all-language))) "resources/html/new-defini.html")})
  true)

(defn ask-overwrite-ok
  "id+lang exists, so ask if the user really wants to overwrite."
  []
  (let [page-data (assoc @params
                         :langname (sql/langname (select-keys @params [:lang]))
                         :sys-msg "Warning: overwrite"
                         :curr-word-def (sql/get-defini (select-keys @params [:id :lang]))
                         :all-language
                         (map (fn [xx]
                                (assoc xx :selected (= (str (:id xx)) (str (:lang @params)))))
                              (sql/all-language)))]
    (reset! output
            {:status 200
             :headers {"Content-Type" "text/html"}
             :body (render-any page-data "resources/html/overwrite-ok.html")}))
  true)



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
  (test-run s-table)
  (check-tf-states s-table)
  )

;; elisp to align the s-table
;; (defun align-st (beg end)
;;   (interactive "r")
;;   (align-regexp beg end "\\(\\s-*\\)[{:]" 1 1 t))

;; This is the entire logic for the definitionary behavior.
(def s-table
  {
   :start             {:fn have-params       true :action-edit     false :render-help}
   :action-edit       {:fn action-edit       true :wait            false :action-savedefini}
   :action-savedefini {:fn action-savedefini true :id-lang-exists   false :action-overwrite-ok}
   :id-lang-exists    {:fn id-lang-exists    true :ask-overwrite-ok false :insert-defini}
   :insert-defini     {:fn insert-defini     true :edit-defini     false :edit-defini} ;; false should be error page
   :ask-overwrite-ok  {:fn ask-overwrite-ok  true :wait            false :wait}
   :action-overwrite-ok {:fn action-overwrite-ok   true :update-defini     false :action-list}
   :update-defini     {:fn update-defini     true :edit-defini     false :wait} ;; false should be error page
   :edit-defini       {:fn edit-defini       true :wait            false :wait} ;; false should be error page
   :action-list       {:fn action-list       true :wait            false :action-ext}
   :action-ext        {:fn action-ext        true :wait            false :render-help}
   :render-help       {:fn render-help       true :wait            false :wait}
   :wait              {:fn wait              true :wait            false :wait}
   })


