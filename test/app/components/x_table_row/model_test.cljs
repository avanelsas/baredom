(ns app.components.x-table-row.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [app.components.x-table-row.model :as model]))

;; ── parse-row-index ──────────────────────────────────────────────────────────
(deftest parse-row-index-nil-test
  (is (nil? (model/parse-row-index nil))))

(deftest parse-row-index-empty-test
  (is (nil? (model/parse-row-index ""))))

(deftest parse-row-index-zero-test
  (is (nil? (model/parse-row-index "0"))))

(deftest parse-row-index-negative-test
  (is (nil? (model/parse-row-index "-1"))))

(deftest parse-row-index-non-numeric-test
  (is (nil? (model/parse-row-index "abc"))))

(deftest parse-row-index-valid-1-test
  (is (= 1 (model/parse-row-index "1"))))

(deftest parse-row-index-valid-5-test
  (is (= 5 (model/parse-row-index "5"))))

(deftest parse-row-index-valid-100-test
  (is (= 100 (model/parse-row-index "100"))))

(deftest parse-row-index-float-floors-test
  (is (= 2 (model/parse-row-index "2.9"))))

(deftest parse-row-index-whitespace-trims-test
  (is (= 3 (model/parse-row-index " 3 "))))

;; ── normalize ────────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (false? (:selected? m)))
    (is (false? (:disabled? m)))
    (is (false? (:interactive? m)))
    (is (nil? (:row-index m)))))

(deftest normalize-selected-propagates-test
  (let [m (model/normalize {:selected? true})]
    (is (true? (:selected? m)))))

(deftest normalize-disabled-propagates-test
  (let [m (model/normalize {:disabled? true})]
    (is (true? (:disabled? m)))))

(deftest normalize-interactive-propagates-test
  (let [m (model/normalize {:interactive? true})]
    (is (true? (:interactive? m)))))

(deftest normalize-row-index-propagates-test
  (let [m (model/normalize {:row-index-raw "3"})]
    (is (= 3 (:row-index m)))))

(deftest normalize-row-index-invalid-is-nil-test
  (let [m (model/normalize {:row-index-raw "0"})]
    (is (nil? (:row-index m)))))

;; ── interactive-eligible? ────────────────────────────────────────────────────
(deftest interactive-eligible-interactive-enabled-test
  (is (true? (model/interactive-eligible? {:interactive? true :disabled? false}))))

(deftest interactive-eligible-interactive-disabled-test
  (is (false? (model/interactive-eligible? {:interactive? true :disabled? true}))))

(deftest interactive-eligible-not-interactive-enabled-test
  (is (false? (model/interactive-eligible? {:interactive? false :disabled? false}))))

(deftest interactive-eligible-not-interactive-disabled-test
  (is (false? (model/interactive-eligible? {:interactive? false :disabled? true}))))

;; ── aria-selected-value ──────────────────────────────────────────────────────
(deftest aria-selected-selected-and-interactive-test
  (is (= "true" (model/aria-selected-value {:selected? true :interactive? true}))))

(deftest aria-selected-selected-not-interactive-test
  (is (= "true" (model/aria-selected-value {:selected? true :interactive? false}))))

(deftest aria-selected-not-selected-interactive-test
  (is (= "false" (model/aria-selected-value {:selected? false :interactive? true}))))

(deftest aria-selected-not-selected-not-interactive-test
  (is (nil? (model/aria-selected-value {:selected? false :interactive? false}))))

;; ── connected-detail ─────────────────────────────────────────────────────────
(deftest connected-detail-shape-test
  (let [d (model/connected-detail {:selected? true :disabled? false
                                   :interactive? true :row-index 2})]
    (is (= 2    (:rowIndex d)))
    (is (= true (:selected d)))
    (is (= false (:disabled d)))
    (is (= true  (:interactive d)))))

(deftest connected-detail-nil-row-index-defaults-to-zero-test
  (let [d (model/connected-detail {:selected? false :disabled? false
                                   :interactive? false :row-index nil})]
    (is (= 0 (:rowIndex d)))))

;; ── click-detail ─────────────────────────────────────────────────────────────
(deftest click-detail-shape-test
  (let [d (model/click-detail {:selected? false :disabled? false :row-index 5})]
    (is (= 5     (:rowIndex d)))
    (is (= false (:selected d)))
    (is (= false (:disabled d)))))

(deftest click-detail-nil-row-index-defaults-to-zero-test
  (let [d (model/click-detail {:selected? false :disabled? false :row-index nil})]
    (is (= 0 (:rowIndex d)))))
