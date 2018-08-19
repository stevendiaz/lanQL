(ns lanql.db
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [com.stuartsierra.component :as component]
		[clojure.java.jdbc :as jdbc]
		[honeysql.core :as sql]))

(defrecord LanDb [conn]

  component/Lifecycle

  (start [this]
    (assoc this
           :conn {:dbtype "postgresql"
                   :host "localhost"
                   :dbname "postgres"
                   :user "postgres"
                   :port 25432}))

  (stop [this]
    (assoc this :conn nil)))

(defn new-db
  []
  {:db (map->LanDb {})})

(defn find-user-by-id
  [component user-id]
  (-> (jdbc/query
        (:conn component)
        (sql/format {:select [:*]
                     :from [:users_user]
                     :where [:= :id user-id]}))
      first))
