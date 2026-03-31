(ns baredom.components.x-select.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-select.model :as model]))

;; ── normalize-enum ──────────────────────────────────────────────────────────
(deftest normalize-enum-valid-values-test
  (is (= "sm" (model/normalize-enum "sm" #{"sm" "md" "lg"} "md")))
  (is (= "md" (model/normalize-enum "md" #{"sm" "md" "lg"} "md")))
  (is (= "lg" (model/normalize-enum "lg" #{"sm" "md" "lg"} "md"))))

(deftest normalize-enum-invalid-falls-back-test
  (is (= "md" (model/normalize-enum nil    #{"sm" "md" "lg"} "md")))
  (is (= "md" (model/normalize-enum ""     #{"sm" "md" "lg"} "md")))
  (is (= "md" (model/normalize-enum "xl"   #{"sm" "md" "lg"} "md")))
  (is (= "md" (model/normalize-enum 42     #{"sm" "md" "lg"} "md"))))

(deftest normalize-enum-case-sensitive-test
  (testing "enum lookup is case-sensitive (no implicit lowering)"
    (is (= "md" (model/normalize-enum "SM" #{"sm" "md" "lg"} "md")))
    (is (= "md" (model/normalize-enum "Lg" #{"sm" "md" "lg"} "md")))))

;; ── normalize-size ──────────────────────────────────────────────────────────
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

;; ── normalize ───────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= false (:disabled? m)))
    (is (= false (:required? m)))
    (is (= "md"  (:size m)))
    (is (= nil   (:placeholder m)))
    (is (= nil   (:value m)))
    (is (= nil   (:name m)))))

(deftest normalize-disabled-test
  (is (true?  (:disabled? (model/normalize {:disabled-present? true}))))
  (is (false? (:disabled? (model/normalize {:disabled-present? false}))))
  (is (false? (:disabled? (model/normalize {:disabled-present? nil})))))

(deftest normalize-required-test
  (is (true?  (:required? (model/normalize {:required-present? true}))))
  (is (false? (:required? (model/normalize {:required-present? false}))))
  (is (false? (:required? (model/normalize {:required-present? nil})))))

(deftest normalize-size-attr-test
  (is (= "sm" (:size (model/normalize {:size-raw "sm"}))))
  (is (= "md" (:size (model/normalize {:size-raw "bad"}))))
  (is (= "md" (:size (model/normalize {:size-raw nil})))))

(deftest normalize-string-attrs-pass-through-test
  (let [m (model/normalize {:placeholder-raw "Pick one"
                            :value-raw       "b"
                            :name-raw        "country"})]
    (is (= "Pick one" (:placeholder m)))
    (is (= "b"        (:value m)))
    (is (= "country"  (:name m)))))

(deftest normalize-empty-string-attrs-test
  (let [m (model/normalize {:placeholder-raw ""
                            :value-raw       ""
                            :name-raw        ""})]
    (is (= "" (:placeholder m)))
    (is (= "" (:value m)))
    (is (= "" (:name m)))))
