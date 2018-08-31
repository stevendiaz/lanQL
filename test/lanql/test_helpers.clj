(ns lanql.test-helpers
  (:require [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clj-time.format :as f]
            [clj-time.coerce :as c]))

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

