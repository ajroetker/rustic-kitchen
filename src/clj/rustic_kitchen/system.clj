(ns rustic-kitchen.system
  (:require [rustic-kitchen.db :as db]
            [rustic-kitchen.server :as server]))

(defn context
  [& [{:keys [database webserver nrepl]}]]
  {:db (or database {:connection-uri "jdbc:postgresql://127.0.0.1:5432/rustic?user=rustic"})
   :server (merge {:host "localhost" :port 3000} webserver)
   :nrepl (merge {:host "0.0.0.0" :port 7888 :middlewares []} nrepl)
   :handler server/handler
   :cache (atom {})})

(defn start
  [context]
  (-> context
      db/start
      server/start))

(defn stop
  [context]
  (-> context
      server/stop
      db/stop))
