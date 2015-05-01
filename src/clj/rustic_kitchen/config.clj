(ns rustic-kitchen.config
  (:require [cheshire.core :as json]
            [clojure.walk :refer [keywordize-keys]]))

(defn load-config
  [file]
  (-> file
      slurp
      json/parse-string
      keywordize-keys))
