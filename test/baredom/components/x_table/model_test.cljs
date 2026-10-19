(ns baredom.components.x-table.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-table.model :as model]))

;; ── parse-columns ────────────────────────────────────────────────────────────
(deftest parse-columns-integer-test
  (testing "integer string → repeat(N,minmax(0,1fr)) for truly equal columns"
    (is (= "repeat(4,minmax(0,1fr))" (model/parse-columns "4")))
    (is (= "repeat(1,minmax(0,1fr))" (model/parse-columns "1")))
    (is (= "repeat(12,minmax(0,1fr))" (model/parse-columns "12")))))

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
  (is (= "repeat(3,minmax(0,1fr))" (:columns (model/normalize {:columns-raw "3"}))))
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
  (let [d (model/row-select-detail 3 true "single" "a")]
    (is (= 3        (:rowIndex d)))
    (is (true?      (:selected d)))
    (is (= "single" (:selectionMode d)))
    (is (= "a"      (:value d)))))

(deftest row-select-detail-nil-index-test
  (let [d (model/row-select-detail nil false "multi" "")]
    (is (= 0 (:rowIndex d)))))

;; ── row-stripe ───────────────────────────────────────────────────────────────
(deftest row-stripe-test
  (is (= [nil "even" nil "even"] (mapv model/row-stripe (range 4)))))

;; ── selection ────────────────────────────────────────────────────────────────
(deftest normalize-reads-value-as-a-list-test
  (is (= #{"a" "b"} (:selection (model/normalize {:value-raw "a, b"}))))
  (is (= #{"a,1"} (:selection (model/normalize {:value-raw "[\"a,1\"]" :value-format-raw "json"}))))
  (is (= #{} (:selection (model/normalize {:value-raw "  "})))))

(deftest normalize-warns-about-an-invalid-json-value-test
  (is (string? (:value-warning (model/normalize {:value-raw "a" :value-format-raw "json"}))))
  (is (nil? (:value-warning (model/normalize {:value-raw "[\"a\"]" :value-format-raw "json"})))))

(deftest holdable?-test
  (is (true? (model/holdable? "csv" "a")))
  (is (false? (model/holdable? "csv" "a,b")))
  (is (false? (model/holdable? "csv" " a")))
  (is (true? (model/holdable? "json" "a,b"))))

(deftest next-selection-test
  (testing "single mode selects the row alone, or nothing when it was selected"
    (is (= #{"b"} (model/next-selection "single" #{"a"} "b")))
    (is (= #{} (model/next-selection "single" #{"a"} "a")))
    (is (= #{"c"} (model/next-selection "single" #{"a" "b"} "c")))
    (is (= #{} (model/next-selection "single" #{"a" "b"} "a"))))
  (testing "multi mode toggles the row"
    (is (= #{"a" "b"} (model/next-selection "multi" #{"a"} "b")))
    (is (= #{"b"} (model/next-selection "multi" #{"a" "b"} "a")))))

(deftest seed-selection-test
  (is (= #{"x"} (model/seed-selection "single" ["x" "y"])))
  (is (= #{"x" "y"} (model/seed-selection "multi" ["x" "y"]))))

(deftest selection-text-test
  (is (= "a" (model/selection-text "csv" #{"a"})))
  (is (= "" (model/selection-text "csv" #{})))
  (is (= "a,b" (model/selection-text "csv" #{"b" "a"})))
  (is (= "[\"a\",\"b\"]" (model/selection-text "json" #{"b" "a"}))))

(deftest normalize-reads-the-value-test
  (let [m (model/normalize {:selectable-raw "multi" :value-raw "a,b" :value-format-raw "bad"})]
    (is (true? (:value-present? m)))
    (is (= "csv" (:value-format m)))
    (is (= #{"a" "b"} (:selection m))))
  (is (false? (:value-present? (model/normalize {})))))
