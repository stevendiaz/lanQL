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
          response (:data (query-test "{ applications { applicant_user_id question_1}}"))]
      (is (= (:id user) (-> response :applications first :applicant_user_id)))
      (is (= "Why do you want to rush Lambda Alpha Nu?" (-> response :applications first :question_1)))
      (db/delete-application-by-id (:id application))
      (db/delete-user-by-id (:id user))))
  (testing "Query active semesters for a user"
    (let [semester (create! :semester {:semester "Fall 2018"})
          user (create! :user {:id 9999 :semesters [semester]})
          response (:data (query-test "{ user_by_id(id: 9999) { active_semesters { semester }}}"))]
      (is (= "Fall 2018" (-> response :user_by_id :active_semesters first :semester)))
      (db/dissoc-user-and-semester (:id user) (:id semester))
      (db/delete-semester-by-id (:id semester))
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
