(ns defini.sql
  (:require [clojure.java.jdbc :as jdbc]))

(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "defini.db"
   })

(defn all-language []
  (jdbc/query db ["select myword, id
  from defini
  where
    is_language
    and 
    lang = id"]))

(defn insert-defini
  "Insert a definition. Return vector [id msg]."
  [{:keys [id lang myword phrase]}]
  (jdbc/with-db-transaction [dbh db]
    (let [id (:id (first (jdbc/query dbh ["select max(id)+1 as id from defini"])))]
      (if (= '(1) (jdbc/execute! dbh ["insert into defini (id,lang,myword,phrase) values (?,?,?,?)" id lang myword phrase]))
        [id "Insert succeeded"]
        [id "Insert failed"]))))

(defn update-defini
  "Update a definition. Return vector [id msg]."
  [{:keys [id lang myword phrase]}]
  (jdbc/with-db-transaction [dbh db]
    [id (if (= '(1) (jdbc/execute! dbh ["update defini set myword=?,phrase=? where id=? and lang=?" myword phrase id lang]))
          "Update succeeded"
          "Update failed")]))


(defn id-lang-exists [{:keys [id lang]}]
  (let [result (jdbc/query db ["select * from defini where id=? and lang=?" id lang])]
    (if (empty? (first result))
      false
      true)))


;; jdbc/execute! returns number of rows as a list '(1) or '(0) and so on.
;; jdbc/query returns a list of hash where fields are keys ({:id 1})

(defn save-defini
  "Insert or update a definition. Return vector [id msg]."
  [{:keys [id lang myword phrase]}]
  ;; insert new
  (if (or (nil? id) (= "" id))
    (jdbc/with-db-transaction [dbh db]
      (let [id (:id (first (jdbc/query dbh ["select max(id)+1 as id from defini"])))]
        (if (= '(1) (jdbc/execute! dbh ["insert into defini (id,lang,myword,phrase) values (?,?,?,?)" id lang myword phrase]))
          [id "Insert succeeded"]
          [id "Insert failed"])))
    (jdbc/with-db-transaction [dbh db]
      [id (if (= '(1) (jdbc/execute! dbh ["update defini set myword=?,phrase=? where id=? and lang=?" myword phrase id lang]))
            "Update succeeded"
            (do
              (jdbc/execute! dbh ["insert into defini (id, lang, myword, phrase) values (?,?,?,?)" id lang myword phrase])
              "Insert new lang succeeded"))])))

(defn langname
  [{:keys [lang]}]
  (:langname (first (jdbc/query db ["select myword langname from defini where id=? and lang=?" lang lang]))))


(defn get-defini
  [{:keys [id lang]}]
  (first (jdbc/query db ["select id,lang,myword,phrase,(select myword from defini where id=xx.lang and lang=xx.lang) langname from defini xx where id=? and lang=?" id lang])))

(defn word-def-list [starting-id]
  (jdbc/query db ["select id,(select myword from defini where id=xx.lang and lang=xx.lang) langname, lang, myword, phrase from defini xx where id>=? and id-10<?" starting-id starting-id]))

(comment
  (let [starting-id 1] (jdbc/query db ["select id,lang,myword,phrase from defini where id>=? and id-10<?" starting-id starting-id]))
  (get-defini {:id 9 :lang 2})
  (save-defini {:id nil :lang 1 :myword "foo" :phrase "foo like stuff"})
  (save-defini {:id 9 :lang 2 :myword "foo" :phrase "even more foo like stuff"})
  
  (= '(1) (jdbc/execute! db ["update defini set myword=?,phrase=? where id=? and lang=?" "foo" "bar" "8" "1"]))
  )

;; https://stackoverflow.com/questions/39765943/clojure-java-jdbc-lazy-query
;; https://jdbc.postgresql.org/documentation/83/query.html#query-with-cursor
;; http://clojure-doc.org/articles/ecosystem/java_jdbc/using_sql.html#exception-handling-and-transaction-rollback
;; http://clojure-doc.org/articles/ecosystem/java_jdbc/using_sql.html#using-transactions

(defn ex-lazy-select
  []
  (jdbc/with-db-transaction [tx db] ;; originally connection
    (jdbc/query tx
                [(jdbc/prepare-statement (:connection tx)
                                         "select * from mytable"
                                         {:fetch-size 10})]
                {:result-set-fn (fn [result-set] result-set)})))

(defn demo-autocommit
  "Demo looping SQL without a transaction. Every execute will auto-commit, which is time consuming. This
  function takes 62x longer than doing these queries inside a single transaction."
  []
  (jdbc/execute! db ["delete from entry where title like 'demo transaction%'"])
  (loop [nseq (range 10000)]
    (let [num (first nseq)
          remainder (rest nseq)]
      (if (nil? num)
        nil
        (do
          (jdbc/execute! db ["insert into entry (title,stars) values (?,?)" (str "demo transaction" num) num])
          (recur remainder))))))

;; http://pesterhazy.karmafish.net/presumably/2015-05-25-getting-started-with-clojure-jdbc-and-sqlite.html
(defn demo-transaction
  "Demo looping SQL inside a transaction. This seems to lack an explicit commit, which makes it tricky to
commit every X SQL queries. Use doall or something to un-lazy inside with-db-transaction, if you need the
query results."
  []
  (jdbc/with-db-transaction [dbh db]
    (jdbc/execute! dbh ["delete from entry where title like 'demo transaction%'"])
    (loop [nseq (range 10000)]
      (let [num (first nseq)
            remainder (rest nseq)]
        (if (nil? num)
          nil
          (do
            (jdbc/execute! dbh ["insert into entry (title,stars) values (?,?)" (str "demo transaction" num) num])
          (recur remainder)))))))
