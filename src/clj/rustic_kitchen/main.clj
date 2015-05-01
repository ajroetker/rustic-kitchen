(ns rustic-kitchen.main
  (:require [rustic-kitchen.nrepl :as nrepl]
            [rustic-kitchen.system :as system]
            [rustic-kitchen.config :as config]))

(defn -main
  [& args]
  ;; user may specify config as the first arg to the command line
  (let [path-to-user-config (first args)
        context (system/context (when (and path-to-user-config
                                           (.exists (clojure.java.io/as-file path-to-user-config)))
                                  (config/load-config path-to-user-config)))]
    ;; nrepl is special because
    ;; we don't want to restart it
    ;; when doing repl development
    ;; TODO attempt graceful shutdown of nrepl
    (nrepl/start context)))
