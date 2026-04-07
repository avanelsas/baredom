(ns baredom.components.x-splash.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-splash.model :as model]))

;; ── normalize-variant ────────────────────────────────────────────────────────
(deftest normalize-variant-known-values-test
  (is (= "default" (model/normalize-variant "default")))
  (is (= "branded" (model/normalize-variant "branded")))
  (is (= "minimal" (model/normalize-variant "minimal"))))

(deftest normalize-variant-case-insensitive-test
  (is (= "default" (model/normalize-variant "DEFAULT")))
  (is (= "branded" (model/normalize-variant "Branded"))))

(deftest normalize-variant-fallback-test
  (is (= "default" (model/normalize-variant nil)))
  (is (= "default" (model/normalize-variant "")))
  (is (= "default" (model/normalize-variant "bad"))))

;; ── normalize-overlay ────────────────────────────────────────────────────────
(deftest normalize-overlay-known-values-test
  (is (= "solid"       (model/normalize-overlay "solid")))
  (is (= "blur"        (model/normalize-overlay "blur")))
  (is (= "transparent" (model/normalize-overlay "transparent"))))

(deftest normalize-overlay-case-insensitive-test
  (is (= "solid" (model/normalize-overlay "SOLID")))
  (is (= "blur"  (model/normalize-overlay "Blur"))))

(deftest normalize-overlay-fallback-test
  (is (= "solid" (model/normalize-overlay nil)))
  (is (= "solid" (model/normalize-overlay "")))
  (is (= "solid" (model/normalize-overlay "bad"))))

;; ── parse-progress ───────────────────────────────────────────────────────────
(deftest parse-progress-valid-test
  (is (= 0   (model/parse-progress "0")))
  (is (= 50  (model/parse-progress "50")))
  (is (= 100 (model/parse-progress "100")))
  (is (= 33.5 (model/parse-progress "33.5"))))

(deftest parse-progress-clamps-test
  (is (= 0   (model/parse-progress "-10")))
  (is (= 100 (model/parse-progress "200"))))

(deftest parse-progress-invalid-test
  (is (nil? (model/parse-progress nil)))
  (is (nil? (model/parse-progress "abc")))
  (is (nil? (model/parse-progress ""))))

;; ── parse-bool-default-true ──────────────────────────────────────────────────
(deftest parse-bool-default-true-test
  (testing "absent (nil) -> true"
    (is (true? (model/parse-bool-default-true nil))))
  (testing "empty string -> true"
    (is (true? (model/parse-bool-default-true ""))))
  (testing "\"true\" -> true"
    (is (true? (model/parse-bool-default-true "true"))))
  (testing "\"false\" -> false"
    (is (false? (model/parse-bool-default-true "false"))))
  (testing "\"FALSE\" -> false (case-insensitive)"
    (is (false? (model/parse-bool-default-true "FALSE"))))
  (testing "other values -> true"
    (is (true? (model/parse-bool-default-true "yes")))))

;; ── derive-state ─────────────────────────────────────────────────────────────
(deftest derive-state-defaults-test
  (let [m (model/derive-state {})]
    (is (false?  (:active? m)))
    (is (= "default" (:variant m)))
    (is (nil?    (:progress m)))
    (is (true?   (:spinner? m)))
    (is (= "solid" (:overlay m)))))

(deftest derive-state-active-test
  (is (true? (:active? (model/derive-state {:active-present? true})))))

(deftest derive-state-variant-test
  (is (= "minimal" (:variant (model/derive-state {:variant-raw "minimal"})))))

(deftest derive-state-progress-test
  (is (= 75 (:progress (model/derive-state {:progress-raw "75"})))))

(deftest derive-state-spinner-false-test
  (is (false? (:spinner? (model/derive-state {:spinner-attr "false"})))))

(deftest derive-state-overlay-test
  (is (= "blur" (:overlay (model/derive-state {:overlay-raw "blur"})))))
