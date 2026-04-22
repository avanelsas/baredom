(ns baredom.components.x-combobox.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-combobox.model :as model]))

;; ── normalize-placement ─────────────────────────────────────────────────────
(deftest normalize-placement-valid-test
  (is (= "bottom-start" (model/normalize-placement "bottom-start")))
  (is (= "bottom-end"   (model/normalize-placement "bottom-end")))
  (is (= "top-start"    (model/normalize-placement "top-start")))
  (is (= "top-end"      (model/normalize-placement "top-end"))))

(deftest normalize-placement-invalid-test
  (is (= "bottom-start" (model/normalize-placement nil)))
  (is (= "bottom-start" (model/normalize-placement "")))
  (is (= "bottom-start" (model/normalize-placement "left"))))

;; ── normalize ───────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= ""             (:value m)))
    (is (= ""             (:placeholder m)))
    (is (= ""             (:name m)))
    (is (false?           (:disabled? m)))
    (is (false?           (:required? m)))
    (is (false?           (:open? m)))
    (is (= "bottom-start" (:placement m)))))

(deftest normalize-value-test
  (is (= "us" (:value (model/normalize {:value-raw "us"})))))

(deftest normalize-disabled-test
  (is (true? (:disabled? (model/normalize {:disabled-present? true})))))

(deftest normalize-placement-test
  (is (= "top-end" (:placement (model/normalize {:placement-raw "top-end"})))))

;; ── filter-options ──────────────────────────────────────────────────────────
(def test-options
  [{:value "us" :label "United States"}
   {:value "uk" :label "United Kingdom"}
   {:value "nl" :label "Netherlands"}
   {:value "de" :label "Germany"}])

(deftest filter-options-nil-query-returns-all-test
  (is (= 4 (count (model/filter-options test-options nil)))))

(deftest filter-options-empty-query-returns-all-test
  (is (= 4 (count (model/filter-options test-options "")))))

(deftest filter-options-substring-match-test
  (is (= 2 (count (model/filter-options test-options "United")))))

(deftest filter-options-case-insensitive-test
  (is (= 2 (count (model/filter-options test-options "united")))))

(deftest filter-options-no-match-test
  (is (= 0 (count (model/filter-options test-options "xyz")))))

(deftest filter-options-partial-match-test
  (is (= 1 (count (model/filter-options test-options "Nether")))))

;; ── find-option-by-value ────────────────────────────────────────────────────
(deftest find-option-by-value-found-test
  (is (= {:value "nl" :label "Netherlands"}
         (model/find-option-by-value test-options "nl"))))

(deftest find-option-by-value-not-found-test
  (is (nil? (model/find-option-by-value test-options "fr"))))

(deftest find-option-by-value-nil-test
  (is (nil? (model/find-option-by-value test-options nil))))

;; ── next-active-idx / prev-active-idx ───────────────────────────────────────
(deftest next-active-idx-from-start-test
  (is (= 0 (model/next-active-idx 4 nil))))

(deftest next-active-idx-wraps-test
  (is (= 0 (model/next-active-idx 4 3))))

(deftest next-active-idx-middle-test
  (is (= 2 (model/next-active-idx 4 1))))

(deftest next-active-idx-empty-list-test
  (is (= -1 (model/next-active-idx 0 nil))))

(deftest prev-active-idx-from-start-test
  (is (= 3 (model/prev-active-idx 4 0))))

(deftest prev-active-idx-wraps-test
  (is (= 3 (model/prev-active-idx 4 nil))))

(deftest prev-active-idx-middle-test
  (is (= 1 (model/prev-active-idx 4 2))))

(deftest prev-active-idx-empty-list-test
  (is (= -1 (model/prev-active-idx 0 nil))))

;; ── highlight-match ─────────────────────────────────────────────────────────
(deftest highlight-match-at-start-test
  (is (= {:before "" :match "Uni" :after "ted States"}
         (model/highlight-match "United States" "Uni"))))

(deftest highlight-match-in-middle-test
  (is (= {:before "United " :match "Sta" :after "tes"}
         (model/highlight-match "United States" "sta"))))

(deftest highlight-match-at-end-test
  (is (= {:before "United Stat" :match "es" :after ""}
         (model/highlight-match "United States" "es"))))

(deftest highlight-match-no-match-test
  (is (nil? (model/highlight-match "United States" "xyz"))))

(deftest highlight-match-nil-query-test
  (is (nil? (model/highlight-match "United States" nil))))

(deftest highlight-match-empty-query-test
  (is (nil? (model/highlight-match "United States" ""))))
