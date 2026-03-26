(ns app.components.x-switch.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [app.components.x-switch.model :as model]))

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

(deftest switch-value-test
  (testing "nil yields \"on\""
    (is (= "on" (model/switch-value nil))))
  (testing "non-nil is returned as-is"
    (is (= "agreed" (model/switch-value "agreed")))
    (is (= ""        (model/switch-value "")))))

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
      (is (= false   (:checked? m)))
      (is (= false   (:disabled? m)))
      (is (= false   (:readonly? m)))
      (is (= false   (:required? m)))
      (is (= "on"    (:value m)))
      (is (= "false" (:aria-checked m)))
      (is (= true    (:valid? m)))))

  (testing "checked? true sets aria-checked to \"true\""
    (let [m (model/normalize {:checked-present? true})]
      (is (= true   (:checked? m)))
      (is (= "true" (:aria-checked m)))))

  (testing "value is passed through"
    (let [m (model/normalize {:value-raw "enabled"})]
      (is (= "enabled" (:value m)))))

  (testing "aria- fields are forwarded"
    (let [m (model/normalize {:aria-label-raw       "Dark mode"
                              :aria-describedby-raw "hint-2"
                              :aria-labelledby-raw  "lbl-2"})]
      (is (= "Dark mode" (:aria-label m)))
      (is (= "hint-2"    (:aria-describedby m)))
      (is (= "lbl-2"     (:aria-labelledby m)))))

  (testing "required + unchecked → valid? false"
    (let [m (model/normalize {:required-present? true})]
      (is (= false (:valid? m)))))

  (testing "required + checked → valid? true"
    (let [m (model/normalize {:required-present? true
                              :checked-present?  true})]
      (is (= true (:valid? m))))))
