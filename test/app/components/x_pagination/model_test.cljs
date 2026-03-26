(ns app.components.x-pagination.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [app.components.x-pagination.model :as model]))

;; ── parse-total-pages ─────────────────────────────────────────────────────
(deftest parse-total-pages-valid-test
  (is (= 1  (model/parse-total-pages "1")))
  (is (= 10 (model/parse-total-pages "10")))
  (is (= 99 (model/parse-total-pages "99"))))

(deftest parse-total-pages-invalid-test
  (is (= 1 (model/parse-total-pages nil)))
  (is (= 1 (model/parse-total-pages "")))
  (is (= 1 (model/parse-total-pages "0")))
  (is (= 1 (model/parse-total-pages "-5")))
  (is (= 1 (model/parse-total-pages "abc"))))

;; ── parse-page ────────────────────────────────────────────────────────────
(deftest parse-page-clamping-test
  (is (= 1  (model/parse-page nil 10)))
  (is (= 1  (model/parse-page "0"  10)))
  (is (= 1  (model/parse-page "-1" 10)))
  (is (= 5  (model/parse-page "5"  10)))
  (is (= 10 (model/parse-page "10" 10)))
  (is (= 10 (model/parse-page "99" 10))))

(deftest parse-page-single-page-test
  (is (= 1 (model/parse-page "1" 1)))
  (is (= 1 (model/parse-page "5" 1))))

;; ── parse-size ────────────────────────────────────────────────────────────
(deftest parse-size-valid-test
  (doseq [s ["sm" "md" "lg"]]
    (is (= s (model/parse-size s)))))

(deftest parse-size-default-test
  (is (= "md" (model/parse-size nil)))
  (is (= "md" (model/parse-size "xl")))
  (is (= "md" (model/parse-size ""))))

;; ── normalize ─────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= 1          (:page m)))
    (is (= 1          (:total-pages m)))
    (is (= 1          (:sibling-count m)))
    (is (= 1          (:boundary-count m)))
    (is (= "md"       (:size m)))
    (is (false?       (:disabled m)))
    (is (= "Pagination" (:label m)))))

(deftest normalize-parses-all-attrs-test
  (let [m (model/normalize {:page-raw           "3"
                             :total-pages-raw    "10"
                             :sibling-count-raw  "2"
                             :boundary-count-raw "1"
                             :size-raw           "lg"
                             :disabled-present?  true
                             :label-raw          "Go to page"})]
    (is (= 3          (:page m)))
    (is (= 10         (:total-pages m)))
    (is (= 2          (:sibling-count m)))
    (is (= 1          (:boundary-count m)))
    (is (= "lg"       (:size m)))
    (is (true?        (:disabled m)))
    (is (= "Go to page" (:label m)))))

(deftest normalize-clamps-page-test
  (let [m (model/normalize {:page-raw "99" :total-pages-raw "5"})]
    (is (= 5 (:page m)))
    (is (= 5 (:total-pages m)))))

;; ── build-page-items ──────────────────────────────────────────────────────
(deftest build-page-items-all-fit-test
  ;; 5 pages, boundary 1, siblings 1: set {1,2,3,4,5} — no ellipsis
  (let [items (model/build-page-items 3 5 1 1)]
    (is (= 5 (count (filter #(= :page (:type %)) items))))
    (is (= 0 (count (filter #(= :ellipsis (:type %)) items))))))

(deftest build-page-items-ellipsis-right-test
  ;; page 1 of 20, siblings 1, boundary 1 → left:[1], mid:[1,2], right:[20]
  ;; union: {1,2,20} → ellipsis between 2 and 20
  (let [items (model/build-page-items 1 20 1 1)
        pages (filter #(= :page (:type %)) items)
        ellipses (filter #(= :ellipsis (:type %)) items)]
    (is (= 1 (count ellipses)))
    (is (= 1 (:n (first pages))))))

(deftest build-page-items-ellipsis-left-test
  ;; page 20 of 20, siblings 1, boundary 1 → left:[1], mid:[19,20], right:[20]
  ;; union: {1,19,20} → ellipsis between 1 and 19
  (let [items (model/build-page-items 20 20 1 1)
        pages (filter #(= :page (:type %)) items)
        ellipses (filter #(= :ellipsis (:type %)) items)]
    (is (= 1 (count ellipses)))
    (is (= 20 (:n (last pages))))))

(deftest build-page-items-ellipsis-both-sides-test
  ;; page 10 of 20, siblings 1, boundary 1
  ;; union: {1, 9,10,11, 20} → ellipsis on both sides
  (let [items (model/build-page-items 10 20 1 1)
        ellipses (filter #(= :ellipsis (:type %)) items)]
    (is (= 2 (count ellipses)))))

(deftest build-page-items-larger-siblings-test
  ;; page 10 of 20, siblings 3, boundary 2
  ;; union: {1,2, 7,8,9,10,11,12,13, 19,20} → ellipsis on both sides
  (let [items (model/build-page-items 10 20 3 2)
        pages (filter #(= :page (:type %)) items)
        ellipses (filter #(= :ellipsis (:type %)) items)]
    (is (= 11 (count pages)))
    (is (= 2 (count ellipses)))))

(deftest build-page-items-single-page-test
  (let [items (model/build-page-items 1 1 1 1)]
    (is (= 1 (count items)))
    (is (= :page (:type (first items))))
    (is (= 1 (:n (first items))))))

(deftest build-page-items-zero-total-test
  (is (= [] (model/build-page-items 1 0 1 1))))

(deftest build-page-items-order-test
  ;; Pages should always be in ascending order
  (let [items (model/build-page-items 5 10 1 1)
        pages (->> items (filter #(= :page (:type %))) (map :n))]
    (is (= pages (sort pages)))))

;; ── prev-disabled? / next-disabled? ──────────────────────────────────────
(deftest prev-disabled-on-first-page-test
  (is (true?  (model/prev-disabled? {:page 1 :total-pages 5 :disabled false})))
  (is (false? (model/prev-disabled? {:page 2 :total-pages 5 :disabled false}))))

(deftest prev-disabled-when-disabled-test
  (is (true? (model/prev-disabled? {:page 3 :total-pages 5 :disabled true}))))

(deftest next-disabled-on-last-page-test
  (is (true?  (model/next-disabled? {:page 5 :total-pages 5 :disabled false})))
  (is (false? (model/next-disabled? {:page 4 :total-pages 5 :disabled false}))))

(deftest next-disabled-when-disabled-test
  (is (true? (model/next-disabled? {:page 3 :total-pages 5 :disabled true}))))
