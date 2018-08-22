(ns lanql.schema
  "Contains custom resolvers and a function to provide the full schema."
  (:require
    [clojure.java.io :as io]
    [com.walmartlabs.lacinia.util :as util]
    [com.walmartlabs.lacinia.schema :as schema]
    [com.walmartlabs.lacinia.resolve :refer [resolve-as]]
    [com.stuartsierra.component :as component]
    [lanql.db :as db]
    [clojure.edn :as edn]))

(defn user-by-id
  []
  (fn [_ args _]
    (db/find-user-by-id (:id args))))

(defn user-active-semesters
  []
  (fn [_ _ user]
    (db/list-semesters-for-user (:id user))))

(defn user-events-attended
  []
  (fn [_ _ user]
    (db/list-events-for-user (:id user))))

(defn resolver-map
  []
  {:query/user-by-id (user-by-id)
   :User/active-semesters (user-active-semesters)
   :User/events-attended (user-events-attended)})

(defn load-schema
  []
  (-> (io/resource "lan-schema.edn")
      slurp
      edn/read-string
      (util/attach-resolvers (resolver-map))
      schema/compile))

(defrecord SchemaProvider [schema]

  component/Lifecycle

  (start [this]
    (assoc this :schema (load-schema)))

  (stop [this]
    (assoc this :schema nil)))

(defn new-schema-provider
  []
  {:schema-provider (-> {}
                        map->SchemaProvider
                        (component/using [:db]))})
