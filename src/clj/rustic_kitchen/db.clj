(ns rustic-kitchen.db
  (:require [jdbc.pool.c3p0 :as pool]
            [migratus.core :as migratus]))

(defn migrate [db]
  (migratus/migrate {:store :database
                     :migration-dir "migrations"
                     :db db}))

(defn start
  [context]
  (migrate (:db context))
  (update-in context [:db] pool/make-datasource-spec))

(defn stop
  [context]
  context)
