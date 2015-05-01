(ns rustic-kitchen.server
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.json :refer [wrap-json-params wrap-json-response]]
            [ring.middleware.params :refer [wrap-params]]
            [compojure.core :refer :all]
            [compojure.handler :as handler]
            [compojure.route :as route]
            [rustic-kitchen.storage :as storage :refer [entity-row->string]]
            [cheshire.core :as json]
            [puppetlabs.kitchensink.core :as ks]))

(defn identity-hash
  [entity]
  (-> entity
      ks/sort-nested-maps 
      json/generate-string
      ks/utf8-string->sha1))

(defn api-app-routes
  [db]
  (routes
   (GET "/list-entities" [] (->> (storage/list-entity-rows db)
                                 (map entity-row->string)))
   (POST "/:type" [type entity] (-> (storage/store-entity! db (identity-hash entity) type entity)
                                    entity-row->string))
   (GET "/:id" [id] (when id
                      (->> (storage/get-entity-row db id)
                           entity-row->string)))
   (DELETE "/:id" [id] (= 1 (storage/delete-entity! db id)))))

(defn api-handler
  [{:keys [db]}]
  (-> (api-app-routes db)
      wrap-json-params
      wrap-params))

;; defroutes macro defines a function that chains individual route
;; functions together. The request map is passed to each function in
;; turn, until a non-nil response is returned.
(defroutes cli-app-routes
  ;; to serve document root address
  (GET "/" [] "<p>Hello from compojure</p>")
  ;; to serve static pages saved in resources/public directory
  (route/resources "/")
  ;; if page is not found
  (route/not-found "Page not found"))

;; site function creates a handler suitable for a standard website,
;; adding a bunch of standard ring middleware to app-route:
(defn cli-handler
  [&_]
  (-> cli-app-routes
      wrap-params))

(defn handler
  [system]
  (routes (context "/api" [] (api-handler system))
          (context "/cli" [] (cli-handler system))))

(defn start
  [context]
  (let [handler (:handler context)]
    (update-in context [:server] #(run-jetty (handler context) %))))

(defn stop
  [context]
  (update-in context [:server] #(.stop %)))
