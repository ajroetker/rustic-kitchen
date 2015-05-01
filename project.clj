(defproject rustic-kitchen "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :pedantic? :abort
  ;; CLJ AND CLJS source code path
  :source-paths ["src/clj" "src/cljs" "src/brepl"]
  :dependencies [[org.clojure/clojure "1.7.0-beta2"]
                 [org.clojure/clojurescript "0.0-3211"]
                 [org.clojure/tools.nrepl "0.2.10"]
                 [prismatic/schema "0.4.2"]
                 [ring/ring-core "1.3.2"]
                 [ring/ring-json "0.3.1"]
                 [ring/ring-jetty-adapter "1.3.2"]
                 [compojure "1.3.3"]
                 [cheshire "5.4.0"]
                 [puppetlabs/kitchensink "1.1.0"]
                 [honeysql "0.5.2"]
                 [org.clojure/java.jdbc "0.3.6"]
                 [postgresql "9.3-1102.jdbc41"]
                 [clojure.jdbc/clojure.jdbc-c3p0 "0.3.2"]
                 [migratus "0.7.0"]]
  :main rustic-kitchen.main
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[org.clojure/tools.namespace "0.2.10"]
                                  [org.clojure/java.classpath "0.2.2"]]}}
  :clean-targets ^{:protect false} [:target-paths
                                    :compile-paths
                                    "resources/public/js"]
  ;; lein-cljsbuild plugin to build a CLJS project
  :plugins [[lein-cljsbuild "1.0.5"]
            [lein-ring "0.9.3" :exclusions [org.clojure/clojure]]]

  :ring {:handler rustic-kitchen.server/cli-handler}
  ;; cljsbuild options configuration
  :cljsbuild {:builds
              {:dev
               {;; clojurescript source code path
                :source-paths ["src/cljs" "src/brepl"]
                ;; Google Closure Compiler options
                :compiler {;; the name of emitted JS script file
                           :output-to "resources/public/js/rustic_dbg.js"
                           ;; minimum optimization
                           :optimizations :whitespace
                           ;; prettyfying emitted JS
                           :pretty-print true}}
               :prod
               {;; clojurescript source code path
                :source-paths ["src/cljs"]
                ;; Google Closure Compiler options
                :compiler {;; the name of emitted JS script file
                           :output-to "resources/public/js/rustic.js"
                           ;; advanced optimization
                           :optimizations :advanced
                           ;; no need prettyfication
                           :pretty-print false}}
               :pre-prod
               {;; clojurescript source code path
                :source-paths ["src/cljs" "src/brepl"]
                :compiler {;; different output name
                           :output-to "resources/public/js/rustic_pre.js"
                           ;; simple optimization
                           :optimizations :simple
                           ;; no need prettyfication
                           :pretty-print false}}}}) 
