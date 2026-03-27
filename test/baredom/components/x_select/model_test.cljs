(ns baredom.components.x-select.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-select.model :as model]))

(deftest normalize-size-test
  (testing "valid size values pass through"
    (is (= "sm" (model/normalize-size "sm")))
    (is (= "md" (model/normalize-size "md")))
    (is (= "lg" (model/normalize-size "lg"))))
  (testing "nil yields default"
    (is (= "md" (model/normalize-size nil))))
  (testing "invalid value yields default"
    (is (= "md" (model/normalize-size "xl")))
    (is (= "md" (model/normalize-size "")))
    (is (= "md" (model/normalize-size "large")))))

(deftest normalize-test
  (testing "defaults when no attrs present"
    (let [m (model/normalize {})]
      (is (= false (:disabled? m)))
      (is (= false (:required? m)))
      (is (= "md"  (:size m)))
      (is (= nil   (:placeholder m)))
      (is (= nil   (:value m)))
      (is (= nil   (:name m)))))

  (testing "disabled-present? sets disabled?"
    (let [m (model/normalize {:disabled-present? true})]
      (is (= true (:disabled? m)))))

  (testing "required-present? sets required?"
    (let [m (model/normalize {:required-present? true})]
      (is (= true (:required? m)))))

  (testing "size-raw is normalized"
    (let [m (model/normalize {:size-raw "sm"})]
      (is (= "sm" (:size m))))
    (let [m (model/normalize {:size-raw "bad"})]
      (is (= "md" (:size m)))))

  (testing "string attrs are passed through"
    (let [m (model/normalize {:placeholder-raw "Pick one"
                              :value-raw       "b"
                              :name-raw        "country"})]
      (is (= "Pick one" (:placeholder m)))
      (is (= "b"        (:value m)))
      (is (= "country"  (:name m))))))
