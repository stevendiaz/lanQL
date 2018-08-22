(ns lanql.db
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [com.stuartsierra.component :as component]
    [clojure.java.jdbc :as jdbc]
    [honeysql.helpers :refer :all :as helpers]
    [honeysql.core :as sql]))

(def db-config
  {:dbtype "postgresql"
   :host "localhost"
   :dbname "postgres"
   :user "postgres"
   :port 25432})

(defn new-db
  []
  {:db (map->LanDb {})})

(defn list-events-for-user
  [user-id]
  (jdbc/query
    db-config
    (-> (select :*)
        (from [:events_event_attendees :attendees])
        (join [:events_event :events] [:= :attendees.event_id :events.id])
        (where := :attendees.user_id user-id)
        sql/format)))

(defn list-semesters-for-user
  [user-id]
  (jdbc/query
    db-config
    (-> (select :s.semester)
        (from [:users_user_active_semesters :u])
        (join [:semester :s] [:= :u.activesemester_id :s.id])
        (where := :u.user_id user-id)
        sql/format)))

(defn find-user-by-id
  [user-id]
  (first (jdbc/query
           db-config
           (sql/format {:select [:*]
                        :from [:users_user]
                        :where [:= :id user-id]}))))

