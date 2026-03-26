(ns app.components.x-checkbox.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [app.components.x-checkbox.model :as model]))

(deftest parse-bool-attr-test
  (testing "nil returns false"
    (is (= false (model/parse-bool-attr nil))))
  (testing "\"false\" returns false"
    (is (= false (model/parse-bool-attr "false"))))
  (testing "empty string returns true"
    (is (= true (model/parse-bool-attr ""))))
  (testing "any other string returns true"
    (is (= true (model/parse-bool-attr "true")))
    (is (= true (model/parse-bool-attr "yes")))))

(deftest checkbox-value-test
  (testing "nil yields \"on\""
    (is (= "on" (model/checkbox-value nil))))
  (testing "non-nil is returned as-is"
    (is (= "agree" (model/checkbox-value "agree")))
    (is (= ""      (model/checkbox-value "")))))

(deftest next-toggle-state-test
  (testing "indeterminate → checked"
    (is (= {:checked? true :indeterminate? false}
           (model/next-toggle-state false true))))
  (testing "checked → unchecked"
    (is (= {:checked? false :indeterminate? false}
           (model/next-toggle-state true false))))
  (testing "unchecked → checked"
    (is (= {:checked? true :indeterminate? false}
           (model/next-toggle-state false false)))))

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
      (is (= false (:indeterminate? m)))
      (is (= false (:disabled? m)))
      (is (= false (:readonly? m)))
      (is (= false (:required? m)))
      (is (= "on"  (:value m)))
      (is (= "false" (:aria-checked m)))))

  (testing "checked? true sets aria-checked to \"true\""
    (let [m (model/normalize {:checked-present? true})]
      (is (= true   (:checked? m)))
      (is (= "true" (:aria-checked m)))))

  (testing "indeterminate takes precedence in aria-checked"
    (let [m (model/normalize {:checked-present?       true
                              :indeterminate-present? true})]
      (is (= "mixed" (:aria-checked m)))))

  (testing "value is passed through"
    (let [m (model/normalize {:value-raw "yes"})]
      (is (= "yes" (:value m)))))

  (testing "aria- fields are forwarded"
    (let [m (model/normalize {:aria-label-raw       "Accept terms"
                              :aria-describedby-raw "hint-1"
                              :aria-labelledby-raw  "lbl-1"})]
      (is (= "Accept terms" (:aria-label m)))
      (is (= "hint-1"       (:aria-describedby m)))
      (is (= "lbl-1"        (:aria-labelledby m)))))

  (testing "required + unchecked → valid? false"
    (let [m (model/normalize {:required-present? true})]
      (is (= false (:valid? m)))))

  (testing "required + checked → valid? true"
    (let [m (model/normalize {:required-present? true
                              :checked-present?  true})]
      (is (= true (:valid? m))))))
