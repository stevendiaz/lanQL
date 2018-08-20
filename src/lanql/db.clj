(ns lanql.db
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [com.stuartsierra.component :as component]
    [clojure.java.jdbc :as jdbc]
    [honeysql.helpers :refer :all :as helpers]
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

(defn list-events-for-user
  [component user-id]
  (jdbc/query
    (:conn component)
    (-> (select :*)
        (from [:events_event_attendees :attendees])
        (join [:events_event :events] [:= :attendees.event_id :events.id])
        (where := :attendees.user_id user-id)
        sql/format)))

(defn list-semesters-for-user
  [component user-id]
  (jdbc/query
    (:conn component)
    (-> (select :s.semester)
        (from [:users_user_active_semesters :u])
        (join [:semester :s] [:= :u.activesemester_id :s.id])
        (where := :u.user_id user-id)
        sql/format)))

(defn find-user-by-id
  [component user-id]
  (first (jdbc/query
           (:conn component)
           (sql/format {:select [:*]
                        :from [:users_user]
                        :where [:= :id user-id]}))))

