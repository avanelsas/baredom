(ns baredom.components.x-stat.model-test
  (:require
   [cljs.test :refer-macros [deftest is testing]]
   [baredom.components.x-stat.model :as model]))

;; ── Enum normalization ──────────────────────────────────────────────────────

(deftest normalize-variant-test
  (testing "valid values are accepted"
    (doseq [v model/variant-values]
      (is (= v (model/normalize-variant v)))))
  (testing "invalid values fall back to default"
    (is (= "default" (model/normalize-variant "bad")))
    (is (= "default" (model/normalize-variant nil)))
    (is (= "default" (model/normalize-variant "")))))

(deftest normalize-align-test
  (testing "valid values are accepted"
    (doseq [v model/align-values]
      (is (= v (model/normalize-align v)))))
  (testing "invalid values fall back to default"
    (is (= "start" (model/normalize-align "bad")))
    (is (= "start" (model/normalize-align nil)))))

(deftest normalize-size-test
  (testing "valid values are accepted"
    (doseq [v model/size-values]
      (is (= v (model/normalize-size v)))))
  (testing "invalid values fall back to default"
    (is (= "md" (model/normalize-size "bad")))
    (is (= "md" (model/normalize-size nil)))))

(deftest normalize-emphasis-test
  (testing "valid values are accepted"
    (doseq [v model/emphasis-values]
      (is (= v (model/normalize-emphasis v)))))
  (testing "invalid values fall back to default"
    (is (= "normal" (model/normalize-emphasis "bad")))
    (is (= "normal" (model/normalize-emphasis nil)))))

(deftest normalize-trend-test
  (testing "valid values are accepted"
    (doseq [v model/trend-values]
      (is (= v (model/normalize-trend v)))))
  (testing "invalid values fall back to default"
    (is (= "neutral" (model/normalize-trend "bad")))
    (is (= "neutral" (model/normalize-trend nil)))))

;; ── normalize-bool ──────────────────────────────────────────────────────────

(deftest normalize-bool-test
  (is (true? (model/normalize-bool true)))
  (is (true? (model/normalize-bool "yes")))
  (is (false? (model/normalize-bool nil)))
  (is (false? (model/normalize-bool false))))

;; ── normalize-text ──────────────────────────────────────────────────────────

(deftest normalize-text-test
  (is (= "hello" (model/normalize-text "hello")))
  (is (nil? (model/normalize-text nil)))
  (is (nil? (model/normalize-text ""))))

;; ── derive-state ────────────────────────────────────────────────────────────

(deftest derive-state-defaults-test
  (testing "all nil inputs produce defaults"
    (let [state (model/derive-state {})]
      (is (= "default" (:variant state)))
      (is (= "start" (:align state)))
      (is (= "md" (:size state)))
      (is (= "normal" (:emphasis state)))
      (is (= "neutral" (:trend state)))
      (is (false? (:loading state)))
      (is (nil? (:aria-busy state)))
      (is (nil? (:label state)))
      (is (nil? (:value state)))
      (is (nil? (:hint state))))))

(deftest derive-state-valid-inputs-test
  (testing "valid inputs are preserved"
    (let [state (model/derive-state
                 {:variant "positive"
                  :align "center"
                  :size "lg"
                  :emphasis "high"
                  :trend "up"
                  :loading true
                  :label "Revenue"
                  :value "$128K"
                  :hint "QTD"})]
      (is (= "positive" (:variant state)))
      (is (= "center" (:align state)))
      (is (= "lg" (:size state)))
      (is (= "high" (:emphasis state)))
      (is (= "up" (:trend state)))
      (is (true? (:loading state)))
      (is (= "true" (:aria-busy state)))
      (is (= "Revenue" (:label state)))
      (is (= "$128K" (:value state)))
      (is (= "QTD" (:hint state))))))

(deftest derive-state-invalid-enums-fallback-test
  (testing "invalid enums fall back to defaults"
    (let [state (model/derive-state
                 {:variant "nope"
                  :align "nope"
                  :size "nope"
                  :emphasis "nope"
                  :trend "nope"})]
      (is (= "default" (:variant state)))
      (is (= "start" (:align state)))
      (is (= "md" (:size state)))
      (is (= "normal" (:emphasis state)))
      (is (= "neutral" (:trend state))))))

(deftest derive-state-loading-aria-busy-test
  (testing "loading false produces nil aria-busy"
    (is (nil? (:aria-busy (model/derive-state {:loading false})))))
  (testing "loading true produces aria-busy 'true'"
    (is (= "true" (:aria-busy (model/derive-state {:loading true}))))))

(deftest derive-state-text-normalization-test
  (testing "empty strings become nil"
    (let [state (model/derive-state {:label "" :value "" :hint ""})]
      (is (nil? (:label state)))
      (is (nil? (:value state)))
      (is (nil? (:hint state))))))
