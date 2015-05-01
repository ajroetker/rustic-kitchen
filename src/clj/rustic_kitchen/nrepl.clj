(ns rustic-kitchen.nrepl
  (:require [clojure.tools.nrepl.server :as nrepl]))

(defn process-middlewares
  [middlewares]
  (let [middlewares (map symbol middlewares)]
    (doseq [middleware (map #(symbol (namespace %)) middlewares)]
      (require middleware))
    (->> middlewares (map #(resolve %)) (apply nrepl/default-handler))))

(defn start
  [context]
  (let [{{:keys [port host middlewares]} :nrepl} context]
    (if port 
      (->> (process-middlewares middlewares)
           (nrepl/start-server :bind host :port port :handler)
           (assoc context :nrepl))
      context)))

(defn stop
  [context]
  (update-in context [:nrepl] nrepl/stop-server))
