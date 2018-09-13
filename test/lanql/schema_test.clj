(ns lanql.schema-test
  (:require [clojure.test :refer :all]
            [lanql.db :as db]
            [lanql.test-helpers :refer :all]
            [lanql.factory :refer :all]
            [lanql.test-helpers :refer [query-test]]
            [factory-time.core :refer [build create!]]
            [lanql.schema :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))

(deftest query
  (testing "User query by id"
    (let [user (create! :user {:id 9999 :username "test_username"})
          response (:data (query-test "{ user_by_id(id: 9999) { id username }}"))
          user-from-response (:user_by_id response)]
      (is (= "test_username" (:username user-from-response)))
      (is (= 9999 (:id user-from-response)))
      (db/delete-user-by-id (:id user))))
  (testing "Query all applications"
    (let [user (create! :user {:id 9999 })
          application (create! :application { :applicant_user_id (:id user) })
          response (:data (query-test "{ applications { applicant_user_id question_1}}"))
          applications (:applications response)]
      (is (= (:applicant_user_id (first applications)) (:id user)))
      (is (= (:question_1 (first applications)) "Why do you want to rush Lambda Alpha Nu?"))
      (db/delete-application-by-id (:id application))
      (db/delete-user-by-id (:id user)))))

(defn setup []
  (start))

(defn teardown []
  (stop))

(defn once-fixture [f]
  (setup)
  (f)
  (teardown))

(use-fixtures :once once-fixture)
