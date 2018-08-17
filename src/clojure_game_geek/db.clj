(ns clojure-game-geek.db
  (:require
    [clojure.edn :as edn]
    [clojure.java.io :as io]
    [com.stuartsierra.component :as component]
		[clojure.java.jdbc :as jdbc]
		[honeysql.core :as sql]))

;;(defrecord ClojureGameGeekDb [data]
;;
;;  component/Lifecycle
;;
;;  (start [this]
;;   (assoc this :data (-> (io/resource "cgg-data.edn")
;;                          slurp
;;                          edn/read-string
;;                          atom)))
;;
;;  (stop [this]
;;    (assoc this :data nil)))

(defrecord ClojureGameGeekDb [conn]

  component/Lifecycle

  (start [this]
    (assoc this
           :conn {:dbtype "postgresql"
                   :host "localhost"
                   :dbname "cggdb"
                   :user "cgg_role"
                   :password "lacinia"
                   :port 25432}))

  (stop [this]
    (assoc this :conn nil)))

(defn new-db
  []
  {:db (map->ClojureGameGeekDb {})})

;;(defn find-game-by-id
;;  [db game-id]
;;  (->> db
;;       :data
;;       deref
;;       :games
;;       (filter #(= (str game-id) (:id %)))
;;       first))

(defn find-game-by-id
  [component game-id]
  (-> (jdbc/query (:conn component)
                  (sql/format {:select [:*]
                               :from [:board_game]
                               :where [:= :game_id game-id]}))
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
