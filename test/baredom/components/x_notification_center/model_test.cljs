(ns baredom.components.x-notification-center.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-notification-center.model :as model]))

;; ── parse-position ────────────────────────────────────────────────────────────
(deftest parse-position-all-valid-values-test
  (is (= "top-right"     (model/parse-position "top-right")))
  (is (= "top-left"      (model/parse-position "top-left")))
  (is (= "bottom-right"  (model/parse-position "bottom-right")))
  (is (= "bottom-left"   (model/parse-position "bottom-left")))
  (is (= "top-center"    (model/parse-position "top-center")))
  (is (= "bottom-center" (model/parse-position "bottom-center"))))

(deftest parse-position-case-insensitive-test
  (is (= "top-right"    (model/parse-position "TOP-RIGHT")))
  (is (= "bottom-left"  (model/parse-position "Bottom-Left")))
  (is (= "top-center"   (model/parse-position "TOP-CENTER"))))

(deftest parse-position-unknown-falls-back-test
  (is (= "top-right" (model/parse-position nil)))
  (is (= "top-right" (model/parse-position "")))
  (is (= "top-right" (model/parse-position "center")))
  (is (= "top-right" (model/parse-position "middle"))))

;; ── parse-max ─────────────────────────────────────────────────────────────────
(deftest parse-max-valid-positive-integers-test
  (is (= 1  (model/parse-max "1")))
  (is (= 5  (model/parse-max "5")))
  (is (= 10 (model/parse-max "10")))
  (is (= 99 (model/parse-max "99"))))

(deftest parse-max-fallback-test
  (testing "nil → 5"
    (is (= 5 (model/parse-max nil))))
  (testing "zero → 5"
    (is (= 5 (model/parse-max "0"))))
  (testing "negative → 5"
    (is (= 5 (model/parse-max "-1"))))
  (testing "NaN string → 5"
    (is (= 5 (model/parse-max "abc"))))
  (testing "empty string → 5"
    (is (= 5 (model/parse-max "")))))
