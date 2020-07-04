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
            (nil? (some #{action} [:catreport :ext])))
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
        (= :ext action)
        {:status 200
         :headers {"Content-Type" "text/html"}
         :body (render-any (assoc rmap
                                  :sys-msg "trying ext"
                                  :all-language (load-file "src/defini/ext.clj")) "resources/html/new-defini.html")}
        :else
        {:status 200
         :headers {"Content-Type" "text/html"}
         :body (format
                "<html><body>No command: %s Try: <a href=\"app?action=catreport\">Cat report</a></body</html>"
                (name action))}))

(defn handler 
  "This is the original handler. It is the app handler which gets the http request params, call the request action, and call the reply action which should render a web page."
  [request]
  ;; (println (with-out-str (pp/pprint request)))
  (if (empty? (:params request))
    (do
      nil)
    (let [temp-params (reduce-kv #(assoc %1 (keyword %2) (keyword (clojure.string/trim %3)))  {} (:params request))
          action (get temp-params :action "no-action")
          ras  request
          ;; rmap is a list of records from the db, will full category data
          ;; If there was no :action this merge will add one. If there was an action, this overwrites it.
          working-params (merge temp-params {:action action :test "this is a test"})
          rmap (request-action working-params action)]
      (reply-action rmap action working-params))))
