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
  [db]
  (fn [_ args _]
    (db/find-user-by-id db (:id args))))

(defn user-active-semesters
  [db]
  (fn [_ _ user]
    (db/list-semesters-for-user db (:id user))))

(defn user-events-attended
  [db]
  (fn [_ _ user]
    (db/list-events-for-user db (:id user))))

(defn resolver-map
  [component]
  (let [db (:db component)]
    {:query/user-by-id (user-by-id db)
     :User/active-semesters (user-active-semesters db)
     :User/events-attended (user-events-attended db)}))

(defn load-schema
  [component]
  (-> (io/resource "lan-schema.edn")
      slurp
      edn/read-string
      (util/attach-resolvers (resolver-map component))
      schema/compile))

(defrecord SchemaProvider [schema]

  component/Lifecycle

  (start [this]
    (assoc this :schema (load-schema this)))

  (stop [this]
    (assoc this :schema nil)))

(defn new-schema-provider
  []
  {:schema-provider (-> {}
                        map->SchemaProvider
                        (component/using [:db]))})
