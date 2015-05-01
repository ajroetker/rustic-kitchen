(ns rustic-kitchen.storage
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core :as sql]
            [honeysql.helpers :refer [select from where]]
            [cheshire.core :as json]
            [schema.core :as s])
  (:import [org.postgresql.util PGobject]))

(s/defn clj->PGobject :- PGobject
  [type :- s/Str
   value]
  (doto (PGobject.)
    (.setType type)
    (.setValue value)))

(s/defn json->PGobject :- PGobject
  [value :- {s/Any s/Any}]
  (clj->PGobject "jsonb" (json/generate-string value {:pretty true})))

(extend-protocol jdbc/ISQLValue
  clojure.lang.IPersistentMap
  (sql-value [value] (json->PGobject value))

  clojure.lang.IPersistentVector
  (sql-value [value] (json->PGobject value)))

(extend-protocol jdbc/IResultSetReadColumn
  PGobject
  (result-set-read-column [pgobj metadata idx]
    (let [type  (.getType pgobj)
          value (.getValue pgobj)]
      (case type
        "jsonb" (json/parse-string value true)
        :else value))))

(s/defn store-entity!
  [db
   identity :- s/Str
   kind :- s/Str
   entity :- {s/Any s/Any}]
  (->> {:identity identity :kind kind :entity entity}
       (jdbc/insert! db :entities)
       first))

(s/defn entities-query :- {s/Any s/Any}
  []
  (-> (select :*)
      (from :entities)))

(s/defrecord EntityRow
    [identity :- s/Str
     entity :- {s/Any s/Any}
     kind :- s/Str])

(s/defn get-entity-row :- EntityRow
  [db
   identity :- s/Str]
  (as-> (entities-query) $
        (where $ [:= :identity identity])
        (sql/format $)
        (jdbc/query db $)
        (first $)))

(s/defn list-entity-rows :- [EntityRow]
  [db]
  (->> (entities-query)
       sql/format
       (jdbc/query db)))

(s/defn entity-row->string :- s/Str
  [entity-row :- EntityRow]
  (let [{:keys [identity entity kind]} entity-row]
    (format "%s :- %s\n%s" identity kind entity)))

(s/defn delete-entity!
  [db
   identity :- s/Str]
  (jdbc/delete! db :entities ["identity=?" identity]))
