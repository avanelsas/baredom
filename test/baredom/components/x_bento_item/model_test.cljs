(ns baredom.components.x-bento-item.model-test
  (:require
   [cljs.test :refer-macros [deftest is testing]]
   [baredom.components.x-bento-item.model :as model]))

(deftest normalize-span-test
  (testing "valid integers"
    (is (= 1 (model/normalize-span "1")))
    (is (= 3 (model/normalize-span "3")))
    (is (= 6 (model/normalize-span "6"))))
  (testing "clamping"
    (is (= 1 (model/normalize-span "0")))
    (is (= 1 (model/normalize-span "-1")))
    (is (= 6 (model/normalize-span "7")))
    (is (= 6 (model/normalize-span "99"))))
  (testing "invalid/nil/empty fall back to 1"
    (is (= 1 (model/normalize-span nil)))
    (is (= 1 (model/normalize-span "")))
    (is (= 1 (model/normalize-span "abc")))))

(deftest normalize-order-test
  (testing "valid integers"
    (is (= 2 (model/normalize-order "2")))
    (is (= -1 (model/normalize-order "-1")))
    (is (= 0 (model/normalize-order "0"))))
  (testing "nil/empty/invalid return nil"
    (is (nil? (model/normalize-order nil)))
    (is (nil? (model/normalize-order "")))
    (is (nil? (model/normalize-order "abc")))))

(deftest normalize-defaults-test
  (let [state (model/normalize {})]
    (is (= 1 (:col-span state)))
    (is (= 1 (:row-span state)))
    (is (= "span 1" (:col-span-css state)))
    (is (= "span 1" (:row-span-css state)))
    (is (nil? (:order state)))))

(deftest normalize-spanning-test
  (let [state (model/normalize {:col-span "2" :row-span "3"})]
    (is (= 2 (:col-span state)))
    (is (= 3 (:row-span state)))
    (is (= "span 2" (:col-span-css state)))
    (is (= "span 3" (:row-span-css state)))))

(deftest normalize-includes-order-test
  (let [state (model/normalize {:col-span "1" :row-span "1" :order "5"})]
    (is (= 5 (:order state)))))

(deftest normalize-clamped-test
  (let [state (model/normalize {:col-span "10" :row-span "0"})]
    (is (= 6 (:col-span state)))
    (is (= 1 (:row-span state)))
    (is (= "span 6" (:col-span-css state)))
    (is (= "span 1" (:row-span-css state)))))
