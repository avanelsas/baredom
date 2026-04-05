(ns baredom.components.x-scroll-stack.model-test
  (:require
   [cljs.test :refer [deftest is testing]]
   [baredom.components.x-scroll-stack.model :as model]))

;; ── parse-positive-number ───────────────────────────────────────────────────
(deftest parse-positive-number-test
  (testing "valid positive numbers"
    (is (= 10 (model/parse-positive-number "10" 6)))
    (is (= 3.5 (model/parse-positive-number "3.5" 6))))
  (testing "nil returns default"
    (is (= 6 (model/parse-positive-number nil 6))))
  (testing "empty string returns default"
    (is (= 6 (model/parse-positive-number "" 6))))
  (testing "non-numeric returns default"
    (is (= 6 (model/parse-positive-number "abc" 6))))
  (testing "zero returns default (not positive)"
    (is (= 6 (model/parse-positive-number "0" 6))))
  (testing "negative returns default"
    (is (= 6 (model/parse-positive-number "-5" 6)))))

;; ── parse-bool-attr ─────────────────────────────────────────────────────────
(deftest parse-bool-attr-test
  (is (true? (model/parse-bool-attr "")))
  (is (true? (model/parse-bool-attr "anything")))
  (is (false? (model/parse-bool-attr nil))))

;; ── parse-align ─────────────────────────────────────────────────────────────
(deftest parse-align-test
  (testing "valid values"
    (is (= "top"    (model/parse-align "top")))
    (is (= "center" (model/parse-align "center")))
    (is (= "bottom" (model/parse-align "bottom"))))
  (testing "case-insensitive"
    (is (= "top"    (model/parse-align "TOP")))
    (is (= "center" (model/parse-align "Center")))
    (is (= "bottom" (model/parse-align "BOTTOM"))))
  (testing "whitespace trimmed"
    (is (= "top" (model/parse-align "  top  "))))
  (testing "invalid falls back to center"
    (is (= "center" (model/parse-align "left")))
    (is (= "center" (model/parse-align "")))
    (is (= "center" (model/parse-align "xyz"))))
  (testing "nil falls back to center"
    (is (= "center" (model/parse-align nil)))))

;; ── normalize ───────────────────────────────────────────────────────────────
(deftest normalize-test
  (testing "all defaults when inputs nil"
    (let [m (model/normalize {})]
      (is (= 6 (:peek m)))
      (is (= 3 (:rotation m)))
      (is (= 150 (:scroll-distance m)))
      (is (= "center" (:align m)))
      (is (false? (:disabled? m)))))
  (testing "custom values"
    (let [m (model/normalize {:peek-raw "10" :rotation-raw "5"
                              :scroll-distance-raw "200"
                              :align-raw "top"
                              :disabled-attr ""})]
      (is (= 10 (:peek m)))
      (is (= 5 (:rotation m)))
      (is (= 200 (:scroll-distance m)))
      (is (= "top" (:align m)))
      (is (true? (:disabled? m))))))

;; ── compute-card-progress ───────────────────────────────────────────────────
(deftest compute-card-progress-test
  (testing "before start"
    (is (= 0.0 (model/compute-card-progress 0 1 100))))
  (testing "at start"
    (is (= 0.0 (model/compute-card-progress 100 1 100))))
  (testing "midway"
    (is (= 0.5 (model/compute-card-progress 150 1 100))))
  (testing "at end"
    (is (= 1.0 (model/compute-card-progress 200 1 100))))
  (testing "beyond end (clamped)"
    (is (= 1.0 (model/compute-card-progress 300 1 100))))
  (testing "first card"
    (is (= 0.5 (model/compute-card-progress 50 0 100)))))

;; ── compute-overall-progress ────────────────────────────────────────────────
(deftest compute-overall-progress-test
  (testing "zero children"
    (is (= 0.0 (model/compute-overall-progress 100 0 150))))
  (testing "beginning"
    (is (= 0.0 (model/compute-overall-progress 0 3 100))))
  (testing "end"
    (is (= 1.0 (model/compute-overall-progress 300 3 100))))
  (testing "midway"
    (is (= 0.5 (model/compute-overall-progress 150 3 100)))))

;; ── compute-stacked-count ───────────────────────────────────────────────────
(deftest compute-stacked-count-test
  (testing "zero offset"
    (is (= 0 (model/compute-stacked-count 0 5 100))))
  (testing "partial (less than 1 card)"
    (is (= 0 (model/compute-stacked-count 50 5 100))))
  (testing "exactly 1 card"
    (is (= 1 (model/compute-stacked-count 100 5 100))))
  (testing "between 2 and 3"
    (is (= 2 (model/compute-stacked-count 250 5 100))))
  (testing "all stacked"
    (is (= 5 (model/compute-stacked-count 500 5 100))))
  (testing "beyond all (clamped)"
    (is (= 5 (model/compute-stacked-count 999 5 100)))))

;; ── card-rotation ───────────────────────────────────────────────────────────
(deftest card-rotation-test
  (testing "deterministic"
    (is (= (model/card-rotation 3 5) (model/card-rotation 3 5))))
  (testing "scales with max rotation"
    (let [r3 (model/card-rotation 2 3)
          r6 (model/card-rotation 2 6)]
      (is (< (js/Math.abs (- (* 2 r3) r6)) 0.001)))))

;; ── card-x-offset ───────────────────────────────────────────────────────────
(deftest card-x-offset-test
  (testing "deterministic"
    (is (= (model/card-x-offset 0) (model/card-x-offset 0))))
  (testing "bounded within +-2px"
    (is (<= (js/Math.abs (model/card-x-offset 5)) 2.0))))

;; ── stack-params ────────────────────────────────────────────────────────────
(deftest stack-params-test
  (let [sp (model/stack-params 2 6 3)]
    (is (= 12 (:stack-y sp)))
    (is (number? (:stack-rot sp)))
    (is (number? (:stack-x sp)))))

;; ── compute-stack-area-y ────────────────────────────────────────────────────
(deftest compute-stack-area-y-test
  (testing "center"
    (is (= 300.0 (model/compute-stack-area-y 800 200 "center"))))
  (testing "top"
    (is (= 32 (model/compute-stack-area-y 800 200 "top"))))
  (testing "bottom"
    (is (= 568 (model/compute-stack-area-y 800 200 "bottom")))))

;; ── Event detail builders ───────────────────────────────────────────────────
(deftest change-detail-test
  (let [d (model/change-detail 3 5 0.6)]
    (is (= 3 (:stackedCount d)))
    (is (= 5 (:totalCount d)))
    (is (= 0.6 (:progress d)))))

(deftest progress-detail-test
  (let [d (model/progress-detail 0.5 2 5)]
    (is (= 0.5 (:progress d)))
    (is (= 2 (:stackedCount d)))
    (is (= 5 (:totalCount d)))))
