(ns baredom.components.barebuild-data.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.barebuild-data.model :as model]))

(deftest phases-closed-set-test
  (is (= #{:idle :loading :loaded :error} model/phases)))

(deftest idle-state-test
  (is (= {:phase :idle} (model/idle-state))))

(deftest loading-state-test
  (is (= {:phase :loading} (model/loading-state))))

(deftest loaded-state-test
  (testing "carries data and http-status"
    (let [data #js {}
          s    (model/loaded-state data 200)]
      (is (= :loaded (:phase s)))
      (is (identical? data (:data s)))
      (is (= 200 (:http-status s)))))
  (testing "http-status omitted when nil (absent, not nil-valued)"
    (let [s (model/loaded-state #js {} nil)]
      (is (not (contains? s :http-status))))))

(deftest error-state-test
  (testing "carries message and http-status"
    (let [s (model/error-state "HTTP 500" 500)]
      (is (= :error (:phase s)))
      (is (= "HTTP 500" (:error s)))
      (is (= 500 (:http-status s)))))
  (testing "http-status omitted for transport-level failure"
    (let [s (model/error-state "boom" nil)]
      (is (= "boom" (:error s)))
      (is (not (contains? s :http-status)))))
  (testing "no :data key on error"
    (is (not (contains? (model/error-state "x" 404) :data)))))
