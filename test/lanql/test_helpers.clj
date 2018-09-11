(ns lanql.test-helpers
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clojure.walk :as walk]
            [lanql.system :as system]
            [com.walmartlabs.lacinia :as lacinia]
            [com.stuartsierra.component :as component]
            [clj-time.format :as f]
            [clj-time.coerce :as c])
  (:import (clojure.lang IPersistentMap)))

(def iso-datetime #"\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d([+-][0-2]\d:[0-5]\d|Z)")

(defn- is-datetime? [datetime] (and (string? datetime) (not (nil? (re-matches iso-datetime datetime)))))

(defn- to-date [date] (->> date (f/parse (f/formatter "yyyy-MM-dd'T'HH:mm:ss'Z'")) c/to-timestamp))

(defn- swap-to-datetime
  [values]
  (reduce-kv
    #(if (is-datetime? %3) (assoc %1 %2 (to-date %3)) (assoc %1 %2 %3))
    {}
    values))

(defn parse-datetimes
  [factories]
  (reduce-kv
    #(assoc %1 %2 (swap-to-datetime %3))
    {}
    factories))

(defn- simplify
  "Converts all ordered maps nested within the map into standard hash maps, and
   sequences into vectors, which makes for easier constants in the tests, and eliminates ordering problems."
  [m]
  (walk/postwalk
    (fn [node]
      (cond
        (instance? IPersistentMap node)
        (into {} node)

        (seq? node)
        (vec node)

        :else
        node))
    m))

(defonce system (system/new-system))

(defn start
  []
  (alter-var-root #'system component/start-system)
  :started)

(defn stop
  []
  (alter-var-root #'system component/stop-system)
  :stopped)

(defn query-test
  [query-string]
  (-> system
      :schema-provider
      :schema
      (lacinia/execute query-string nil nil)
      simplify))

