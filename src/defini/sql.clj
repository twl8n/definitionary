(ns defini.sql
  (:require [clojure.java.jdbc :as jdbc]))

(def db
  {:classname   "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname     "defini.db"
   })

(defn all-language []
  (jdbc/query db ["select bb.myword, bb.did as id
  from defini aa, dtext bb
  where
    aa.is_language
    and 
    bb.did =aa.id
    and
    bb.lang = aa.id"]))

;; jdbc/execute! returns number of rows as a list '(1) or '(0) and so on.
;; jdbc/query returns a list of hash where fields are keys ({:id 1})

(defn save-defini
  "Insert or update a definition. Return the definition id"
  [{:keys [id lang myword phrase]}]
  ;; insert new
  (if (or (nil? id) (= "" id))
    (do 
      (jdbc/with-db-transaction [dbh db]
        (jdbc/execute! dbh ["insert into defini (is_language) values (0)"])
        (let [id (:id (first (jdbc/query dbh ["select last_insert_rowid() as id"])))]
          (jdbc/execute! dbh ["insert into dtext (did,lang,myword,phrase) values (?,?,?,?)" id lang myword phrase])
          id)))
    (do
      (jdbc/with-db-transaction [dbh db]
        (when (= '(0) (jdbc/execute! dbh ["update dtext set myword=?,phrase=? where did=? and lang=?" myword phrase id lang]))
          (jdbc/execute! dbh ["insert into dtext (did,lang,myword,phrase) values (?,?,?,?)" id lang myword phrase])
          ))
      id)))

(defn get-defini
  [{:keys [id lang]}]
  (first (jdbc/query db ["select did as id,lang,myword,phrase from dtext where did=? and lang=?" id lang])))

(comment
  (get-defini {:id 9 :lang 2})
  (save-defini {:id nil :lang 1 :myword "foo" :phrase "foo like stuff"})
  (save-defini {:id 9 :lang 2 :myword "foo" :phrase "even more foo like stuff"})
  
  (= '(1) (jdbc/execute! db ["update dtext set myword=?,phrase=? where did=? and lang=?" "foo" "bar" "8" "1"]))
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
