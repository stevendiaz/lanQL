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

(defn find-game-by-id
  [db game-id]
  (->> db
       :data
       deref
       :games
       (filter #(= (str game-id) (:id %)))
       first))

(defn find-user-by-id
  [component user-id]
  (-> (jdbc/query
        (:conn component)
        (sql/format {:select [:*]
                     :from [:users_user]
                     :where [:= :id user-id]}))
      first))


(defn find-member-by-id
  [db member-id]
  (->> db
       :data
       deref
       :members
       (filter #(= (str member-id) (:id %)))
       first))

(defn list-designers-for-game
  [db game-id]
  (let [designers (:designers (find-game-by-id db game-id))]
    (->> db
         :data
         deref
         :designers
         (filter #(contains? designers (:id %))))))

(defn list-games-for-designer
  [db designer-id]
  (->> db
       :data
       deref
       :games
       (filter #(-> % :designers (contains? designer-id)))))

(defn list-ratings-for-game
  [db game-id]
  (->> db
       :data
       deref
       :ratings
       (filter #(= (str game-id) (:game_id %)))))

(defn list-ratings-for-member
  [db member-id]
  (->> db
       :data
       deref
       :ratings
       (filter #(= (str member-id) (:member_id %)))))

(defn ^:private apply-game-rating
  [game-ratings game-id member-id rating]
  (->> game-ratings
       (remove #(and (= game-id (:game_id %))
                     (= member-id (:member_id %))))
       (cons {:game_id game-id
              :member_id member-id
              :rating rating})))

(defn upsert-game-rating
  "Adds a new game rating, or changes the value of an existing game rating."
  [db game-id member-id rating]
  (-> db
      :data
      (swap! update :ratings apply-game-rating game-id member-id rating)))