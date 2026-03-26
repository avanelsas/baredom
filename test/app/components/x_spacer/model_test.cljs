(ns app.components.x-spacer.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [app.components.x-spacer.model :as model]))

;; ── parse-axis ────────────────────────────────────────────────────────────
(deftest parse-axis-valid-test
  (is (= "vertical"   (model/parse-axis "vertical")))
  (is (= "horizontal" (model/parse-axis "horizontal"))))

(deftest parse-axis-default-test
  (is (= "vertical" (model/parse-axis nil)))
  (is (= "vertical" (model/parse-axis "")))
  (is (= "vertical" (model/parse-axis "diagonal"))))

(deftest parse-axis-case-insensitive-test
  (is (= "vertical"   (model/parse-axis "VERTICAL")))
  (is (= "horizontal" (model/parse-axis "Horizontal"))))

;; ── parse-grow ────────────────────────────────────────────────────────────
(deftest parse-grow-absent-test
  (is (false? (model/parse-grow nil))))

(deftest parse-grow-present-test
  (is (true? (model/parse-grow "")))
  (is (true? (model/parse-grow "true")))
  (is (true? (model/parse-grow "grow"))))

(deftest parse-grow-explicit-false-test
  (is (false? (model/parse-grow "false")))
  (is (false? (model/parse-grow "FALSE")))
  (is (false? (model/parse-grow "  false  "))))

;; ── parse-size ────────────────────────────────────────────────────────────
(deftest parse-size-valid-test
  (is (= "2rem"  (model/parse-size "2rem")))
  (is (= "16px"  (model/parse-size "16px")))
  (is (= "1.5em" (model/parse-size "1.5em")))
  (is (= "10%"   (model/parse-size "10%"))))

(deftest parse-size-default-test
  (is (= "1rem" (model/parse-size nil)))
  (is (= "1rem" (model/parse-size "")))
  (is (= "1rem" (model/parse-size "   "))))

(deftest parse-size-trims-whitespace-test
  (is (= "2rem" (model/parse-size "  2rem  "))))

;; ── normalize ─────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= "1rem"     (:size m)))
    (is (= "vertical" (:axis m)))
    (is (false?       (:grow? m)))))

(deftest normalize-full-test
  (let [m (model/normalize
           {:size-raw "2rem"
            :axis-raw "horizontal"
            :grow-raw ""})]
    (is (= "2rem"       (:size m)))
    (is (= "horizontal" (:axis m)))
    (is (true?          (:grow? m)))))

(deftest normalize-invalid-axis-falls-to-default-test
  (let [m (model/normalize {:axis-raw "sideways"})]
    (is (= "vertical" (:axis m)))))

(deftest normalize-grow-false-when-absent-test
  (let [m (model/normalize {:grow-raw nil})]
    (is (false? (:grow? m)))))
