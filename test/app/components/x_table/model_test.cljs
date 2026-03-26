(ns app.components.x-table.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [app.components.x-table.model :as model]))

;; ── parse-columns ────────────────────────────────────────────────────────────
(deftest parse-columns-integer-test
  (testing "integer string → repeat(N,1fr)"
    (is (= "repeat(4,1fr)" (model/parse-columns "4")))
    (is (= "repeat(1,1fr)" (model/parse-columns "1")))
    (is (= "repeat(12,1fr)" (model/parse-columns "12")))))

(deftest parse-columns-css-string-test
  (testing "CSS string passed through"
    (is (= "2fr 1fr 1fr" (model/parse-columns "2fr 1fr 1fr")))
    (is (= "repeat(auto-fit,minmax(0,1fr))" (model/parse-columns "repeat(auto-fit,minmax(0,1fr))")))
    (is (= "200px 1fr" (model/parse-columns "200px 1fr")))))

(deftest parse-columns-nil-blank-test
  (testing "nil or blank → nil"
    (is (nil? (model/parse-columns nil)))
    (is (nil? (model/parse-columns "")))
    (is (nil? (model/parse-columns "   ")))))

(deftest parse-columns-fractional-not-integer-test
  (testing "fractional string is not treated as integer"
    (is (= "4.5" (model/parse-columns "4.5")))))

;; ── parse-selectable ─────────────────────────────────────────────────────────
(deftest parse-selectable-valid-test
  (is (= "none"   (model/parse-selectable "none")))
  (is (= "single" (model/parse-selectable "single")))
  (is (= "multi"  (model/parse-selectable "multi"))))

(deftest parse-selectable-case-insensitive-test
  (is (= "single" (model/parse-selectable "SINGLE")))
  (is (= "multi"  (model/parse-selectable "Multi"))))

(deftest parse-selectable-fallback-test
  (is (= "none" (model/parse-selectable nil)))
  (is (= "none" (model/parse-selectable "")))
  (is (= "none" (model/parse-selectable "bad"))))

;; ── parse-row-count ──────────────────────────────────────────────────────────
(deftest parse-row-count-valid-test
  (is (= 100 (model/parse-row-count "100")))
  (is (= 1   (model/parse-row-count "1"))))

(deftest parse-row-count-invalid-test
  (is (nil? (model/parse-row-count nil)))
  (is (nil? (model/parse-row-count "")))
  (is (nil? (model/parse-row-count "0")))
  (is (nil? (model/parse-row-count "-5")))
  (is (nil? (model/parse-row-count "abc"))))

;; ── role-for-selectable ──────────────────────────────────────────────────────
(deftest role-for-selectable-test
  (is (= "table" (model/role-for-selectable "none")))
  (is (= "grid"  (model/role-for-selectable "single")))
  (is (= "grid"  (model/role-for-selectable "multi"))))

;; ── aria-multiselectable ─────────────────────────────────────────────────────
(deftest aria-multiselectable-test
  (is (nil?     (model/aria-multiselectable "none")))
  (is (nil?     (model/aria-multiselectable "single")))
  (is (= "true" (model/aria-multiselectable "multi"))))

;; ── normalize ────────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (nil?     (:columns m)))
    (is (= ""     (:caption m)))
    (is (= "none" (:selectable m)))
    (is (false?   (:striped? m)))
    (is (false?   (:bordered? m)))
    (is (false?   (:full-width? m)))
    (is (false?   (:compact? m)))
    (is (nil?     (:row-count m)))))

(deftest normalize-columns-test
  (is (= "repeat(3,1fr)" (:columns (model/normalize {:columns-raw "3"}))))
  (is (= "2fr 1fr"       (:columns (model/normalize {:columns-raw "2fr 1fr"})))))

(deftest normalize-selectable-test
  (is (= "single" (:selectable (model/normalize {:selectable-raw "single"})))))

(deftest normalize-boolean-flags-test
  (let [m (model/normalize {:striped? true :bordered? true :full-width? true :compact? true})]
    (is (true? (:striped? m)))
    (is (true? (:bordered? m)))
    (is (true? (:full-width? m)))
    (is (true? (:compact? m)))))

(deftest normalize-row-count-test
  (is (= 50 (:row-count (model/normalize {:row-count-raw "50"})))))

;; ── sort-detail ──────────────────────────────────────────────────────────────
(deftest sort-detail-test
  (let [d (model/sort-detail 2 "asc" "none")]
    (is (= 2     (:colIndex d)))
    (is (= "asc" (:direction d)))
    (is (= "none" (:previousDirection d)))))

;; ── row-select-detail ────────────────────────────────────────────────────────
(deftest row-select-detail-test
  (let [d (model/row-select-detail 3 true "single")]
    (is (= 3        (:rowIndex d)))
    (is (true?      (:selected d)))
    (is (= "single" (:selectionMode d)))))

(deftest row-select-detail-nil-index-test
  (let [d (model/row-select-detail nil false "multi")]
    (is (= 0 (:rowIndex d)))))
