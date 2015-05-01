(ns user
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [clojure.pprint :refer [pprint]]
            [clojure.repl :refer :all]
            [clojure.test :as test]
            [clojure.tools.namespace.repl :refer [refresh refresh-all]]
            [rustic-kitchen.system :as system]))

(def context nil)

(defn init
  "Constructs the current development system."
  []
  (alter-var-root #'context
                  (constantly (system/context))))

(defn start
  "Starts the current development system."
  []
  (alter-var-root #'context system/start))

(defn stop
  "Shuts down and destroys the current development system."
  []
  (alter-var-root #'context
                  (fn [s] (when s (system/stop s)))))

(defn go
  "Initializes the current development system and starts it running."
  []
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'user/go))
