(ns baredom.components.x-carousel.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-carousel.model :as model]))

;; ── parse-bool-default-true ─────────────────────────────────────────────────
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

;; ── parse-non-neg-int ───────────────────────────────────────────────────────
(deftest parse-non-neg-int-valid-test
  (is (= 0  (model/parse-non-neg-int "0" -1)))
  (is (= 5  (model/parse-non-neg-int "5" -1)))
  (is (= 42 (model/parse-non-neg-int " 42 " -1))))

(deftest parse-non-neg-int-invalid-test
  (is (= -1 (model/parse-non-neg-int nil -1)))
  (is (= -1 (model/parse-non-neg-int "" -1)))
  (is (= -1 (model/parse-non-neg-int "abc" -1)))
  (is (= -1 (model/parse-non-neg-int "-1" -1))))

;; ── parse-pos-int ───────────────────────────────────────────────────────────
(deftest parse-pos-int-valid-test
  (is (= 5000 (model/parse-pos-int "5000" 3000 100)))
  (is (= 100  (model/parse-pos-int "100" 3000 100)))
  (is (= 200  (model/parse-pos-int " 200 " 3000 100))))

(deftest parse-pos-int-invalid-test
  (is (= 3000 (model/parse-pos-int nil 3000 100)))
  (is (= 3000 (model/parse-pos-int "" 3000 100)))
  (is (= 3000 (model/parse-pos-int "50" 3000 100)))
  (is (= 3000 (model/parse-pos-int "abc" 3000 100))))

;; ── parse-transition ────────────────────────────────────────────────────────
(deftest parse-transition-test
  (is (= "slide" (model/parse-transition nil)))
  (is (= "slide" (model/parse-transition "")))
  (is (= "slide" (model/parse-transition "slide")))
  (is (= "slide" (model/parse-transition "SLIDE")))
  (is (= "fade"  (model/parse-transition "fade")))
  (is (= "fade"  (model/parse-transition "Fade")))
  (is (= "slide" (model/parse-transition "invalid"))))

;; ── parse-direction ─────────────────────────────────────────────────────────
(deftest parse-direction-test
  (is (= "horizontal" (model/parse-direction nil)))
  (is (= "horizontal" (model/parse-direction "")))
  (is (= "horizontal" (model/parse-direction "horizontal")))
  (is (= "horizontal" (model/parse-direction "HORIZONTAL")))
  (is (= "vertical"   (model/parse-direction "vertical")))
  (is (= "vertical"   (model/parse-direction "Vertical")))
  (is (= "horizontal" (model/parse-direction "invalid"))))

;; ── parse-peek ──────────────────────────────────────────────────────────────
(deftest parse-peek-test
  (is (= "0px"  (model/parse-peek nil)))
  (is (= "0px"  (model/parse-peek "")))
  (is (= "40px" (model/parse-peek "40px")))
  (is (= "2rem" (model/parse-peek "2rem")))
  (is (= "1.5em" (model/parse-peek "1.5em")))
  (is (= "10%"  (model/parse-peek "10%")))
  (is (= "0px"  (model/parse-peek "abc")))
  (is (= "0px"  (model/parse-peek "40"))))

;; ── clamp-index ─────────────────────────────────────────────────────────────
(deftest clamp-index-test
  (testing "zero count returns 0"
    (is (= 0 (model/clamp-index 3 0))))
  (testing "negative index clamps to 0"
    (is (= 0 (model/clamp-index -1 5))))
  (testing "over-range clamps to last"
    (is (= 4 (model/clamp-index 10 5))))
  (testing "in-range passes through"
    (is (= 2 (model/clamp-index 2 5))))
  (testing "single slide"
    (is (= 0 (model/clamp-index 0 1)))
    (is (= 0 (model/clamp-index 5 1)))))

;; ── can-go-prev? / can-go-next? ────────────────────────────────────────────
(deftest can-go-prev-test
  (testing "first slide, no loop"
    (is (false? (model/can-go-prev? {:current 0 :loop? false :slide-count 3 :disabled? false}))))
  (testing "first slide, loop"
    (is (true? (model/can-go-prev? {:current 0 :loop? true :slide-count 3 :disabled? false}))))
  (testing "middle slide"
    (is (true? (model/can-go-prev? {:current 1 :loop? false :slide-count 3 :disabled? false}))))
  (testing "disabled"
    (is (false? (model/can-go-prev? {:current 1 :loop? false :slide-count 3 :disabled? true}))))
  (testing "single slide"
    (is (false? (model/can-go-prev? {:current 0 :loop? true :slide-count 1 :disabled? false})))))

(deftest can-go-next-test
  (testing "last slide, no loop"
    (is (false? (model/can-go-next? {:current 2 :loop? false :slide-count 3 :disabled? false}))))
  (testing "last slide, loop"
    (is (true? (model/can-go-next? {:current 2 :loop? true :slide-count 3 :disabled? false}))))
  (testing "middle slide"
    (is (true? (model/can-go-next? {:current 1 :loop? false :slide-count 3 :disabled? false}))))
  (testing "disabled"
    (is (false? (model/can-go-next? {:current 1 :loop? false :slide-count 3 :disabled? true})))))

;; ── next-index / prev-index ─────────────────────────────────────────────────
(deftest next-index-test
  (is (= 1 (model/next-index {:current 0 :loop? false :slide-count 3})))
  (is (= 2 (model/next-index {:current 2 :loop? false :slide-count 3})))
  (is (= 0 (model/next-index {:current 2 :loop? true  :slide-count 3}))))

(deftest prev-index-test
  (is (= 0 (model/prev-index {:current 1 :loop? false :slide-count 3})))
  (is (= 0 (model/prev-index {:current 0 :loop? false :slide-count 3})))
  (is (= 2 (model/prev-index {:current 0 :loop? true  :slide-count 3}))))

;; ── snap-direction ──────────────────────────────────────────────────────────
(deftest snap-direction-test
  (testing "large positive delta -> prev"
    (is (= :prev (model/snap-direction 50 0))))
  (testing "large negative delta -> next"
    (is (= :next (model/snap-direction -50 0))))
  (testing "small delta -> stay"
    (is (= :stay (model/snap-direction 10 0))))
  (testing "high positive velocity overrides distance"
    (is (= :prev (model/snap-direction 5 0.5))))
  (testing "high negative velocity overrides distance"
    (is (= :next (model/snap-direction -5 -0.5)))))

;; ── Display predicates ──────────────────────────────────────────────────────
(deftest single-slide-test
  (is (true?  (model/single-slide? {:slide-count 0})))
  (is (true?  (model/single-slide? {:slide-count 1})))
  (is (false? (model/single-slide? {:slide-count 2}))))

(deftest show-arrows-test
  (is (true?  (model/show-arrows? {:arrows? true  :slide-count 3})))
  (is (false? (model/show-arrows? {:arrows? false :slide-count 3})))
  (is (false? (model/show-arrows? {:arrows? true  :slide-count 1}))))

(deftest show-dots-test
  (is (true?  (model/show-dots? {:dots? true  :slide-count 3})))
  (is (false? (model/show-dots? {:dots? false :slide-count 3})))
  (is (false? (model/show-dots? {:dots? true  :slide-count 1}))))

;; ── Keyboard helpers ────────────────────────────────────────────────────────
(deftest prev-key-test
  (is (true?  (model/prev-key? "horizontal" "ArrowLeft")))
  (is (false? (model/prev-key? "horizontal" "ArrowUp")))
  (is (true?  (model/prev-key? "vertical" "ArrowUp")))
  (is (false? (model/prev-key? "vertical" "ArrowLeft"))))

(deftest next-key-test
  (is (true?  (model/next-key? "horizontal" "ArrowRight")))
  (is (false? (model/next-key? "horizontal" "ArrowDown")))
  (is (true?  (model/next-key? "vertical" "ArrowDown")))
  (is (false? (model/next-key? "vertical" "ArrowRight"))))

;; ── normalize ───────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (false?  (:autoplay? m)))
    (is (= 5000  (:interval m)))
    (is (false?  (:loop? m)))
    (is (true?   (:arrows? m)))
    (is (true?   (:dots? m)))
    (is (false?  (:disabled? m)))
    (is (= 0     (:current m)))
    (is (= "slide" (:transition m)))
    (is (= "horizontal" (:direction m)))
    (is (= "0px" (:peek m)))
    (is (= 0     (:slide-count m)))
    (is (nil?    (:aria-label m)))))

(deftest normalize-custom-values-test
  (let [m (model/normalize
           {:autoplay-present? true
            :interval-raw "3000"
            :loop-present? true
            :arrows-raw "false"
            :dots-raw "false"
            :disabled-present? true
            :current-raw "2"
            :transition-raw "fade"
            :direction-raw "vertical"
            :peek-raw "40px"
            :aria-label-raw "Product gallery"
            :slide-count 5})]
    (is (true?  (:autoplay? m)))
    (is (= 3000 (:interval m)))
    (is (true?  (:loop? m)))
    (is (false? (:arrows? m)))
    (is (false? (:dots? m)))
    (is (true?  (:disabled? m)))
    (is (= 2    (:current m)))
    (is (= "fade" (:transition m)))
    (is (= "vertical" (:direction m)))
    (is (= "40px" (:peek m)))
    (is (= 5    (:slide-count m)))
    (is (= "Product gallery" (:aria-label m)))))

(deftest normalize-clamps-current-test
  (let [m (model/normalize {:current-raw "10" :slide-count 3})]
    (is (= 2 (:current m)))))
