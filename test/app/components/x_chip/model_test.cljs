(ns app.components.x-chip.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [app.components.x-chip.model :as model]))

;; ── parse-bool-default-true ───────────────────────────────────────────────
(deftest parse-bool-default-true-nil-test
  (is (true? (model/parse-bool-default-true nil))))

(deftest parse-bool-default-true-empty-test
  (is (true? (model/parse-bool-default-true ""))))

(deftest parse-bool-default-true-explicit-true-test
  (is (true? (model/parse-bool-default-true "true"))))

(deftest parse-bool-default-true-false-test
  (is (false? (model/parse-bool-default-true "false"))))

(deftest parse-bool-default-true-zero-test
  (is (false? (model/parse-bool-default-true "0"))))

(deftest parse-bool-default-true-other-values-test
  (is (true? (model/parse-bool-default-true "1")))
  (is (true? (model/parse-bool-default-true "yes")))
  (is (true? (model/parse-bool-default-true "on"))))

;; ── effective-value ───────────────────────────────────────────────────────
(deftest effective-value-uses-value-raw-test
  (is (= "chip-1" (model/effective-value "My Chip" "chip-1"))))

(deftest effective-value-falls-back-to-label-when-nil-test
  (is (= "My Chip" (model/effective-value "My Chip" nil))))

(deftest effective-value-falls-back-to-label-when-empty-test
  (is (= "My Chip" (model/effective-value "My Chip" ""))))

(deftest effective-value-label-is-used-as-value-when-no-value-test
  (is (= "Tag" (model/effective-value "Tag" nil))))

;; ── normalize ─────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= ""    (:label m)))
    (is (= ""    (:value m)))
    (is (true?   (:removable? m)))
    (is (false?  (:disabled? m)))))

(deftest normalize-label-test
  (let [m (model/normalize {:label-raw "Clojure"})]
    (is (= "Clojure" (:label m)))
    (is (= "Clojure" (:value m)))))  ; value falls back to label

(deftest normalize-value-overrides-label-test
  (let [m (model/normalize {:label-raw "Display" :value-raw "actual-val"})]
    (is (= "Display"    (:label m)))
    (is (= "actual-val" (:value m)))))

(deftest normalize-removable-false-test
  (let [m (model/normalize {:removable-raw "false"})]
    (is (false? (:removable? m)))))

(deftest normalize-removable-zero-test
  (let [m (model/normalize {:removable-raw "0"})]
    (is (false? (:removable? m)))))

(deftest normalize-removable-nil-defaults-true-test
  (let [m (model/normalize {:removable-raw nil})]
    (is (true? (:removable? m)))))

(deftest normalize-disabled-present-test
  (let [m (model/normalize {:disabled-present? true})]
    (is (true? (:disabled? m)))))

(deftest normalize-disabled-absent-test
  (let [m (model/normalize {:disabled-present? false})]
    (is (false? (:disabled? m)))))

;; ── removal-eligible? ─────────────────────────────────────────────────────
(deftest removal-eligible-removable-and-enabled-test
  (is (true? (model/removal-eligible? {:removable? true :disabled? false}))))

(deftest removal-eligible-removable-but-disabled-test
  (is (false? (model/removal-eligible? {:removable? true :disabled? true}))))

(deftest removal-eligible-not-removable-test
  (is (false? (model/removal-eligible? {:removable? false :disabled? false}))))

(deftest removal-eligible-neither-test
  (is (false? (model/removal-eligible? {:removable? false :disabled? true}))))

;; ── remove-detail ─────────────────────────────────────────────────────────
(deftest remove-detail-contains-value-and-label-test
  (let [d (model/remove-detail {:value "chip-1" :label "My Chip"})]
    (is (= "chip-1"  (.-value d)))
    (is (= "My Chip" (.-label d)))))

(deftest remove-detail-value-can-equal-label-test
  (let [d (model/remove-detail {:value "Foo" :label "Foo"})]
    (is (= "Foo" (.-value d)))
    (is (= "Foo" (.-label d)))))
