(ns lanql.system
  (:require
    [com.stuartsierra.component :as component]
    [lanql.schema :as schema]
    [lanql.server :as server]
    [lanql.db :as db]))

(defn new-system
  []
  (merge (component/system-map)
         (server/new-server)
         (schema/new-schema-provider)
         (db/new-db)))
