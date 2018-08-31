(ns lanql.factory
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [clj-time.format :as f]
            [clj-time.coerce :as c]
            [lanql.db :as db]
            [lanql.test-helpers :refer [parse-datetimes]]
            [factory-time.core :refer :all]))

(def base-factories
  (-> (io/resource "base-factory.edn")
      slurp
      edn/read-string
      parse-datetimes))

(defn save-user!
  [user]
  (db/insert-user user))

(deffactory :user (:user base-factories)
  :create! save-user!)

