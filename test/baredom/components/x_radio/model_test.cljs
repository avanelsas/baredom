(ns baredom.components.x-radio.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-radio.model :as model]
            [baredom.utils.model :as mu]))

(deftest parse-bool-attr-test
  (testing "nil returns false"
    (is (= false (mu/parse-bool-attr nil))))
  (testing "\"false\" string still true (presence semantics)"
    (is (= false (mu/parse-bool-attr "false"))))
  (testing "empty string returns true"
    (is (= true (mu/parse-bool-attr ""))))
  (testing "any other string returns true"
    (is (= true (mu/parse-bool-attr "true")))
    (is (= true (mu/parse-bool-attr "yes")))))

(deftest radio-value-test
  (testing "nil yields \"on\""
    (is (= "on" (model/radio-value nil))))
  (testing "non-nil is returned as-is"
    (is (= "agree" (model/radio-value "agree")))
    (is (= ""      (model/radio-value "")))))

(deftest valid-test
  (testing "not required is always valid"
    (is (= true (model/valid? false false)))
    (is (= true (model/valid? false true))))
  (testing "required and unchecked is invalid"
    (is (= false (model/valid? true false))))
  (testing "required and checked is valid"
    (is (= true (model/valid? true true)))))

(deftest normalize-test
  (testing "defaults when no attrs present"
    (let [m (model/normalize {})]
      (is (= false (:checked? m)))
      (is (= false (:disabled? m)))
      (is (= false (:readonly? m)))
      (is (= false (:required? m)))
      (is (= "on"    (:value m)))
      (is (= "false" (:aria-checked m)))))

  (testing "checked? true sets aria-checked to \"true\""
    (let [m (model/normalize {:checked-present? true})]
      (is (= true   (:checked? m)))
      (is (= "true" (:aria-checked m)))))

  (testing "unchecked sets aria-checked to \"false\""
    (let [m (model/normalize {:checked-present? false})]
      (is (= false   (:checked? m)))
      (is (= "false" (:aria-checked m)))))

  (testing "value is passed through"
    (let [m (model/normalize {:value-raw "yes"})]
      (is (= "yes" (:value m)))))

  (testing "aria- fields are forwarded"
    (let [m (model/normalize {:aria-label-raw       "Option A"
                              :aria-describedby-raw "hint-1"
                              :aria-labelledby-raw  "lbl-1"})]
      (is (= "Option A" (:aria-label m)))
      (is (= "hint-1"   (:aria-describedby m)))
      (is (= "lbl-1"    (:aria-labelledby m)))))

  (testing "required + unchecked → valid? false"
    (let [m (model/normalize {:required-present? true})]
      (is (= false (:valid? m)))))

  (testing "required + checked → valid? true"
    (let [m (model/normalize {:required-present? true
                              :checked-present?  true})]
      (is (= true (:valid? m))))))
