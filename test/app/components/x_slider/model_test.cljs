(ns app.components.x-slider.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [app.components.x-slider.model :as model]))

;; ---------------------------------------------------------------------------
;; normalize-number
;; ---------------------------------------------------------------------------

(deftest normalize-number-test
  (testing "valid float string"
    (is (= 42.5 (model/normalize-number "42.5" 0))))
  (testing "integer string"
    (is (= 10.0 (model/normalize-number "10" 0))))
  (testing "NaN string returns default"
    (is (= 0 (model/normalize-number "abc" 0))))
  (testing "nil returns default"
    (is (= 5 (model/normalize-number nil 5))))
  (testing "empty string returns default"
    (is (= 0 (model/normalize-number "" 0)))))

;; ---------------------------------------------------------------------------
;; normalize-min / normalize-max
;; ---------------------------------------------------------------------------

(deftest normalize-min-test
  (testing "valid string"
    (is (= 10.0 (model/normalize-min "10"))))
  (testing "nil falls back to default"
    (is (= model/default-min (model/normalize-min nil)))))

(deftest normalize-max-test
  (testing "valid string"
    (is (= 200.0 (model/normalize-max "200"))))
  (testing "nil falls back to default"
    (is (= model/default-max (model/normalize-max nil)))))

;; ---------------------------------------------------------------------------
;; normalize-value
;; ---------------------------------------------------------------------------

(deftest normalize-value-test
  (testing "within range"
    (is (= 50.0 (model/normalize-value "50" 0 100))))
  (testing "clamped to min"
    (is (= 0.0 (model/normalize-value "-10" 0 100))))
  (testing "clamped to max"
    (is (= 100.0 (model/normalize-value "150" 0 100))))
  (testing "nil defaults to 0 then clamps"
    (is (= 0.0 (model/normalize-value nil 0 100))))
  (testing "exactly at min"
    (is (= 20.0 (model/normalize-value "20" 20 80))))
  (testing "exactly at max"
    (is (= 80.0 (model/normalize-value "80" 20 80)))))

;; ---------------------------------------------------------------------------
;; normalize-step
;; ---------------------------------------------------------------------------

(deftest normalize-step-test
  (testing "\"any\" is passed through"
    (is (= "any" (model/normalize-step "any"))))
  (testing "valid positive number"
    (is (= "5" (model/normalize-step "5"))))
  (testing "decimal step"
    (is (= "0.5" (model/normalize-step "0.5"))))
  (testing "nil falls back to default"
    (is (= model/default-step (model/normalize-step nil))))
  (testing "non-numeric string falls back to default"
    (is (= model/default-step (model/normalize-step "abc"))))
  (testing "zero is invalid, falls back to default"
    (is (= model/default-step (model/normalize-step "0"))))
  (testing "negative is invalid, falls back to default"
    (is (= model/default-step (model/normalize-step "-1")))))

;; ---------------------------------------------------------------------------
;; normalize-size
;; ---------------------------------------------------------------------------

(deftest normalize-size-test
  (testing "\"sm\" is valid"
    (is (= "sm" (model/normalize-size "sm"))))
  (testing "\"md\" is valid"
    (is (= "md" (model/normalize-size "md"))))
  (testing "\"lg\" is valid"
    (is (= "lg" (model/normalize-size "lg"))))
  (testing "nil falls back to default"
    (is (= model/default-size (model/normalize-size nil))))
  (testing "unknown value falls back to default"
    (is (= model/default-size (model/normalize-size "xl")))))

;; ---------------------------------------------------------------------------
;; fill-percent
;; ---------------------------------------------------------------------------

(deftest fill-percent-test
  (testing "0% at min"
    (is (= 0.0 (model/fill-percent 0 0 100))))
  (testing "100% at max"
    (is (= 100.0 (model/fill-percent 100 0 100))))
  (testing "50% at midpoint"
    (is (= 50.0 (model/fill-percent 50 0 100))))
  (testing "custom range"
    (is (= 50.0 (model/fill-percent 150 100 200))))
  (testing "max <= min returns 0"
    (is (= 0.0 (model/fill-percent 50 100 50))))
  (testing "max == min returns 0"
    (is (= 0.0 (model/fill-percent 50 50 50)))))

;; ---------------------------------------------------------------------------
;; derive-state
;; ---------------------------------------------------------------------------

(deftest derive-state-defaults-test
  (testing "all nil inputs produce defaults"
    (let [m (model/derive-state {})]
      (is (= model/default-value (:value m)))
      (is (= model/default-min   (:min m)))
      (is (= model/default-max   (:max m)))
      (is (= model/default-step  (:step m)))
      (is (= model/default-size  (:size m)))
      (is (= false               (:disabled? m)))
      (is (= false               (:readonly? m)))
      (is (= false               (:show-value? m)))
      (is (nil?                  (:label m)))
      (is (nil?                  (:name m)))
      (is (= 0.0                 (:fill-percent m))))))

(deftest derive-state-value-clamping-test
  (testing "value is clamped within min/max"
    (let [m (model/derive-state {:value "150" :min "0" :max "100"})]
      (is (= 100.0 (:value m)))
      (is (= 100.0 (:fill-percent m)))))
  (testing "value below min is clamped to min"
    (let [m (model/derive-state {:value "-5" :min "0" :max "100"})]
      (is (= 0.0 (:value m)))
      (is (= 0.0 (:fill-percent m))))))

(deftest derive-state-fill-percent-test
  (testing "fill-percent is calculated correctly"
    (let [m (model/derive-state {:value "50" :min "0" :max "100"})]
      (is (= 50.0 (:fill-percent m))))))

(deftest derive-state-booleans-test
  (testing "disabled and readonly flags"
    (let [m (model/derive-state {:disabled true :readonly true})]
      (is (= true (:disabled? m)))
      (is (= true (:readonly? m))))))

(deftest derive-state-label-test
  (testing "empty string label is nil"
    (let [m (model/derive-state {:label ""})]
      (is (nil? (:label m)))))
  (testing "non-empty label is preserved"
    (let [m (model/derive-state {:label "Volume"})]
      (is (= "Volume" (:label m))))))

(deftest derive-state-aria-test
  (testing "aria attributes are forwarded"
    (let [m (model/derive-state {:aria-label       "Volume"
                                 :aria-labelledby  "lbl-1"
                                 :aria-describedby "hint-1"})]
      (is (= "Volume" (:aria-label m)))
      (is (= "lbl-1"  (:aria-labelledby m)))
      (is (= "hint-1" (:aria-describedby m)))))
  (testing "empty aria strings become nil"
    (let [m (model/derive-state {:aria-label ""})]
      (is (nil? (:aria-label m))))))
