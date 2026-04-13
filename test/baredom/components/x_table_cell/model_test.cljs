(ns baredom.components.x-table-cell.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-table-cell.model :as model]))

;; ── parse-type ───────────────────────────────────────────────────────────────
(deftest parse-type-known-values-test
  (is (= "data"   (model/parse-type "data")))
  (is (= "header" (model/parse-type "header"))))

(deftest parse-type-case-insensitive-test
  (is (= "data"   (model/parse-type "DATA")))
  (is (= "header" (model/parse-type "Header"))))

(deftest parse-type-unknown-falls-back-to-data-test
  (is (= "data" (model/parse-type nil)))
  (is (= "data" (model/parse-type "")))
  (is (= "data" (model/parse-type "td"))))

;; ── parse-scope ──────────────────────────────────────────────────────────────
(deftest parse-scope-known-values-test
  (is (= "col"      (model/parse-scope "col")))
  (is (= "row"      (model/parse-scope "row")))
  (is (= "colgroup" (model/parse-scope "colgroup")))
  (is (= "rowgroup" (model/parse-scope "rowgroup"))))

(deftest parse-scope-case-insensitive-test
  (is (= "col" (model/parse-scope "COL")))
  (is (= "row" (model/parse-scope "Row"))))

(deftest parse-scope-unknown-falls-back-to-col-test
  (is (= "col" (model/parse-scope nil)))
  (is (= "col" (model/parse-scope "")))
  (is (= "col" (model/parse-scope "auto"))))

;; ── parse-align ──────────────────────────────────────────────────────────────
(deftest parse-align-known-values-test
  (is (= "start"  (model/parse-align "start")))
  (is (= "center" (model/parse-align "center")))
  (is (= "end"    (model/parse-align "end"))))

(deftest parse-align-unknown-falls-back-to-start-test
  (is (= "start" (model/parse-align nil)))
  (is (= "start" (model/parse-align "")))
  (is (= "start" (model/parse-align "left"))))

;; ── parse-valign ─────────────────────────────────────────────────────────────
(deftest parse-valign-known-values-test
  (is (= "top"    (model/parse-valign "top")))
  (is (= "middle" (model/parse-valign "middle")))
  (is (= "bottom" (model/parse-valign "bottom"))))

(deftest parse-valign-unknown-falls-back-to-middle-test
  (is (= "middle" (model/parse-valign nil)))
  (is (= "middle" (model/parse-valign "")))
  (is (= "middle" (model/parse-valign "center"))))

;; ── parse-span ───────────────────────────────────────────────────────────────
(deftest parse-span-valid-test
  (is (= 1 (model/parse-span "1")))
  (is (= 2 (model/parse-span "2")))
  (is (= 5 (model/parse-span "5"))))

(deftest parse-span-truncates-floats-test
  (is (= 2 (model/parse-span "2.9"))))

(deftest parse-span-invalid-falls-back-to-1-test
  (is (= 1 (model/parse-span nil)))
  (is (= 1 (model/parse-span "")))
  (is (= 1 (model/parse-span "0")))
  (is (= 1 (model/parse-span "-1")))
  (is (= 1 (model/parse-span "abc"))))

;; ── parse-sticky ─────────────────────────────────────────────────────────────
(deftest parse-sticky-known-values-test
  (is (= "none"  (model/parse-sticky "none")))
  (is (= "start" (model/parse-sticky "start")))
  (is (= "end"   (model/parse-sticky "end"))))

(deftest parse-sticky-unknown-falls-back-to-none-test
  (is (= "none" (model/parse-sticky nil)))
  (is (= "none" (model/parse-sticky "")))
  (is (= "none" (model/parse-sticky "left"))))

;; ── parse-sort-direction ─────────────────────────────────────────────────────
(deftest parse-sort-direction-known-values-test
  (is (= "none" (model/parse-sort-direction "none")))
  (is (= "asc"  (model/parse-sort-direction "asc")))
  (is (= "desc" (model/parse-sort-direction "desc"))))

(deftest parse-sort-direction-case-insensitive-test
  (is (= "asc"  (model/parse-sort-direction "ASC")))
  (is (= "desc" (model/parse-sort-direction "Desc"))))

(deftest parse-sort-direction-unknown-falls-back-to-none-test
  (is (= "none" (model/parse-sort-direction nil)))
  (is (= "none" (model/parse-sort-direction "")))
  (is (= "none" (model/parse-sort-direction "up"))))

;; ── role-for-cell ────────────────────────────────────────────────────────────
(deftest role-for-cell-data-test
  (is (= "cell" (model/role-for-cell {:type "data" :scope "col"}))))

(deftest role-for-cell-header-col-test
  (is (= "columnheader" (model/role-for-cell {:type "header" :scope "col"}))))

(deftest role-for-cell-header-colgroup-test
  (is (= "columnheader" (model/role-for-cell {:type "header" :scope "colgroup"}))))

(deftest role-for-cell-header-row-test
  (is (= "rowheader" (model/role-for-cell {:type "header" :scope "row"}))))

(deftest role-for-cell-header-rowgroup-test
  (is (= "rowheader" (model/role-for-cell {:type "header" :scope "rowgroup"}))))

;; ── aria-sort-value ──────────────────────────────────────────────────────────
(deftest aria-sort-value-nil-when-not-applicable-test
  (is (nil? (model/aria-sort-value {:sortable? false :sort-direction "none"}))))

(deftest aria-sort-value-none-when-sortable-but-direction-none-test
  (is (= "none" (model/aria-sort-value {:sortable? true :sort-direction "none"}))))

(deftest aria-sort-value-ascending-test
  (is (= "ascending" (model/aria-sort-value {:sortable? true :sort-direction "asc"})))
  (is (= "ascending" (model/aria-sort-value {:sortable? false :sort-direction "asc"}))))

(deftest aria-sort-value-descending-test
  (is (= "descending" (model/aria-sort-value {:sortable? true :sort-direction "desc"})))
  (is (= "descending" (model/aria-sort-value {:sortable? false :sort-direction "desc"}))))

;; ── sort-btn-visible? ────────────────────────────────────────────────────────
(deftest sort-btn-visible-only-for-header-sortable-test
  (is (true?  (model/sort-btn-visible? {:type "header" :sortable? true})))
  (is (false? (model/sort-btn-visible? {:type "header" :sortable? false})))
  (is (false? (model/sort-btn-visible? {:type "data"   :sortable? true})))
  (is (false? (model/sort-btn-visible? {:type "data"   :sortable? false}))))

;; ── sort-btn-tabindex ────────────────────────────────────────────────────────
(deftest sort-btn-tabindex-test
  (is (= "0"  (model/sort-btn-tabindex {:type "header" :sortable? true  :disabled? false})))
  (is (= "-1" (model/sort-btn-tabindex {:type "header" :sortable? true  :disabled? true})))
  (is (= "-1" (model/sort-btn-tabindex {:type "header" :sortable? false :disabled? false})))
  (is (= "-1" (model/sort-btn-tabindex {:type "data"   :sortable? true  :disabled? false}))))

;; ── next-sort-direction ──────────────────────────────────────────────────────
(deftest next-sort-direction-cycle-test
  (is (= "asc"  (model/next-sort-direction "none")))
  (is (= "desc" (model/next-sort-direction "asc")))
  (is (= "none" (model/next-sort-direction "desc"))))

(deftest next-sort-direction-unknown-falls-to-asc-test
  (is (= "asc" (model/next-sort-direction "invalid"))))

;; ── sort-btn-aria-label ──────────────────────────────────────────────────────
(deftest sort-btn-aria-label-test
  (is (= "Sort ascending"  (model/sort-btn-aria-label "none")))
  (is (= "Sort descending" (model/sort-btn-aria-label "asc")))
  (is (= "Remove sort"     (model/sort-btn-aria-label "desc"))))

;; ── normalize ────────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= "data"   (:type m)))
    (is (= "col"    (:scope m)))
    (is (= "start"  (:align m)))
    (is (= "middle" (:valign m)))
    (is (= 1        (:col-span m)))
    (is (= 1        (:row-span m)))
    (is (false?     (:truncate? m)))
    (is (= "none"   (:sticky m)))
    (is (false?     (:sortable? m)))
    (is (= "none"   (:sort-direction m)))
    (is (false?     (:disabled? m)))))

(deftest normalize-type-propagates-test
  (is (= "header" (:type (model/normalize {:type-raw "header"})))))

(deftest normalize-col-span-propagates-test
  (is (= 3 (:col-span (model/normalize {:col-span-raw "3"})))))

(deftest normalize-truncate-propagates-test
  (is (true? (:truncate? (model/normalize {:truncate? true})))))

(deftest normalize-sortable-propagates-test
  (is (true? (:sortable? (model/normalize {:sortable? true})))))

(deftest normalize-sort-direction-propagates-test
  (is (= "asc" (:sort-direction (model/normalize {:sort-direction-raw "asc"})))))

(deftest normalize-disabled-propagates-test
  (is (true? (:disabled? (model/normalize {:disabled? true})))))

;; ── connected-detail ─────────────────────────────────────────────────────────
(deftest connected-detail-shape-test
  (let [m {:type "header" :scope "col" :col-span 2 :row-span 1 :align "start"}
        d (model/connected-detail m)]
    (is (= "header" (:type d)))
    (is (= "col"    (:scope d)))
    (is (= 2        (:colSpan d)))
    (is (= 1        (:rowSpan d)))
    (is (= "start"  (:align d)))))
