(ns lanql.db
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [com.stuartsierra.component :as component]
    [clojure.java.jdbc :as jdbc]
    [honeysql.helpers :refer [select from join where limit]]
    [honeysql.core :as sql]))

(def db-config
  {:dbtype "postgresql"
   :host "localhost"
   :dbname "postgres"
   :user "postgres"
   :port 25432})

(defn insert-user
  [user]
  (jdbc/insert! db-config :users_user user))

(defn delete-user-by-id
  [user-id]
  (jdbc/delete! db-config :users_user ["id = ?" user-id]))

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
  (first (jdbc/query db-config (sql/format {:select [:*]
                                            :from [:users_user]
                                            :where [:= :id user-id]}))))

(defn list-all-applications
  []
  (jdbc/query
    db-config
    (sql/format {:select [:*]
                 :from [:applications_application]})))

(defn list-application-reviews
  [semester-id]
  (jdbc/query
    db-config
    (-> (select :*)
        (from :application_reviews)
        (where := :semester_id semester-id))))

(defn insert-application
  [application]
  (jdbc/insert! db-config :applications_application application))

(defn delete-application-by-id
  [application-id]
  (jdbc/delete! db-config :applications_application ["id = ?" application-id]))

(defn insert-semester
  [semester]
  (jdbc/insert! db-config :semester semester))

(defn delete-semester-by-id
  [semester-id]
  (jdbc/delete! db-config :semester ["id = ?" semester-id]))

(defn assoc-user-and-semester
  [user-id semester-id]
  (jdbc/insert! db-config :users_user_active_semesters {:user_id user-id :activesemester_id semester-id}))

(defn dissoc-user-and-semester
  [user-id semester-id]
  (jdbc/delete! db-config :users_user_active_semesters ["user_id = ? and activesemester_id = ?" user-id semester-id]))
