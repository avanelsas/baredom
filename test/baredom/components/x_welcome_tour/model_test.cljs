(ns baredom.components.x-welcome-tour.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-welcome-tour.model :as model]))

;; ── parse-step ──────────────────────────────────────────────────────────────
(deftest parse-step-test
  (is (= 0 (model/parse-step nil)))
  (is (= 0 (model/parse-step "")))
  (is (= 0 (model/parse-step "abc")))
  (is (= 0 (model/parse-step "0")))
  (is (= 3 (model/parse-step "3")))
  (is (= 0 (model/parse-step "-1"))))

;; ── parse-connector ─────────────────────────────────────────────────────────
(deftest parse-connector-valid-test
  (is (= "arrow" (model/parse-connector "arrow")))
  (is (= "line"  (model/parse-connector "line")))
  (is (= "curve" (model/parse-connector "curve")))
  (is (= "none"  (model/parse-connector "none"))))

(deftest parse-connector-case-insensitive-test
  (is (= "arrow" (model/parse-connector "ARROW")))
  (is (= "curve" (model/parse-connector "Curve"))))

(deftest parse-connector-default-test
  (is (= "arrow" (model/parse-connector nil)))
  (is (= "arrow" (model/parse-connector "bad"))))

;; ── normalize ───────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (false? (:open? m)))
    (is (= 0 (:step m)))
    (is (= "arrow" (:connector m)))
    (is (= "Back" (:prev-label m)))
    (is (= "Next" (:next-label m)))
    (is (= "Done" (:done-label m)))
    (is (= "Skip" (:skip-label m)))
    (is (false? (:counter? m)))
    (is (false? (:dots? m)))))

(deftest normalize-with-values-test
  (let [m (model/normalize
           {:open? true
            :step-raw "2"
            :connector-raw "curve"
            :prev-label-raw "Previous"
            :next-label-raw "Continue"
            :done-label-raw "Finish"
            :skip-label-raw "Exit"
            :counter? true
            :dots? true})]
    (is (true? (:open? m)))
    (is (= 2 (:step m)))
    (is (= "curve" (:connector m)))
    (is (= "Previous" (:prev-label m)))
    (is (= "Continue" (:next-label m)))
    (is (= "Finish" (:done-label m)))
    (is (= "Exit" (:skip-label m)))
    (is (true? (:counter? m)))
    (is (true? (:dots? m)))))

;; ── Step navigation predicates ──────────────────────────────────────────────
(deftest first-step-test
  (is (true?  (model/first-step? 0)))
  (is (false? (model/first-step? 1))))

(deftest last-step-test
  (is (true?  (model/last-step? 2 3)))
  (is (true?  (model/last-step? 4 3)))
  (is (false? (model/last-step? 1 3))))

(deftest clamp-step-test
  (is (= 0 (model/clamp-step -1 5)))
  (is (= 4 (model/clamp-step 10 5)))
  (is (= 2 (model/clamp-step 2 5)))
  (is (= 0 (model/clamp-step 0 0))))

(deftest counter-text-test
  (is (= "1 / 5" (model/counter-text 0 5)))
  (is (= "3 / 5" (model/counter-text 2 5))))

;; ── Connector offset ────────────────────────────────────────────────────────
(deftest offset-for-connector-test
  (is (= 8  (model/offset-for-connector "arrow")))
  (is (= 8  (model/offset-for-connector "none")))
  (is (= 48 (model/offset-for-connector "line")))
  (is (= 48 (model/offset-for-connector "curve"))))

;; ── Cutout geometry ─────────────────────────────────────────────────────────
(deftest compute-cutout-test
  (let [rect {:x 100 :y 200 :width 50 :height 30}
        c    (model/compute-cutout rect 8 4)]
    (is (= 92  (:x c)))
    (is (= 192 (:y c)))
    (is (= 66  (:width c)))
    (is (= 46  (:height c)))
    (is (= 4   (:rx c)))
    (is (= 4   (:ry c)))))

;; ── Positioning ─────────────────────────────────────────────────────────────
(deftest compute-position-bottom-fits-test
  (let [anchor {:x 100 :y 100 :width 80 :height 40}
        pop    {:width 200 :height 100}
        vp     {:width 800 :height 600}
        result (model/compute-position "bottom" anchor pop vp "arrow")]
    (is (= "bottom" (:final-placement result)))
    (is (number? (:x result)))
    (is (number? (:y result)))))

(deftest compute-position-flips-when-no-room-test
  (let [anchor {:x 100 :y 10 :width 80 :height 40}
        pop    {:width 200 :height 100}
        vp     {:width 800 :height 600}
        result (model/compute-position "top" anchor pop vp "arrow")]
    ;; Top doesn't fit (y=10 - 100 - 8 < 0), should flip to bottom
    (is (= "bottom" (:final-placement result)))))

(deftest compute-position-left-right-test
  (let [anchor {:x 400 :y 300 :width 80 :height 40}
        pop    {:width 200 :height 100}
        vp     {:width 800 :height 600}]
    (is (= "left"  (:final-placement (model/compute-position "left" anchor pop vp "arrow"))))
    (is (= "right" (:final-placement (model/compute-position "right" anchor pop vp "arrow"))))))

(deftest compute-position-line-connector-larger-offset-test
  (let [anchor {:x 100 :y 100 :width 80 :height 40}
        pop    {:width 200 :height 100}
        vp     {:width 800 :height 600}
        arrow-result (model/compute-position "bottom" anchor pop vp "arrow")
        line-result  (model/compute-position "bottom" anchor pop vp "line")]
    ;; Line connector should push the popover further from the target
    (is (> (:y line-result) (:y arrow-result)))))

;; ── Connector geometry ──────────────────────────────────────────────────────
(deftest connector-anchor-points-bottom-test
  (let [target  {:x 100 :y 100 :width 80 :height 40}
        popover {:x 90 :y 160 :width 200 :height 100}
        {:keys [target-point popover-point]}
        (model/connector-anchor-points target popover "bottom")]
    (is (= 140 (:x target-point)))
    (is (= 140 (:y target-point)))
    (is (= 190 (:x popover-point)))
    (is (= 160 (:y popover-point)))))

(deftest connector-path-d-line-test
  (let [tp {:x 100 :y 100}
        pp {:x 200 :y 200}
        d  (model/connector-path-d "line" tp pp "bottom")]
    (is (= "M100,100 L200,200" d))))

(deftest connector-path-d-curve-vertical-test
  (let [tp {:x 100 :y 100}
        pp {:x 200 :y 200}
        d  (model/connector-path-d "curve" tp pp "bottom")]
    (is (string? d))
    (is (.startsWith d "M100,100"))
    (is (.includes d "C"))))

(deftest connector-path-d-arrow-returns-nil-test
  (is (nil? (model/connector-path-d "arrow" {:x 0 :y 0} {:x 0 :y 0} "bottom")))
  (is (nil? (model/connector-path-d "none" {:x 0 :y 0} {:x 0 :y 0} "bottom"))))

;; ── Arrow style ─────────────────────────────────────────────────────────────
(deftest arrow-style-bottom-points-at-target-center-test
  (let [target  {:x 100 :y 50 :width 80 :height 40}
        popover {:x 50 :y 100 :width 200 :height 120}
        s       (model/arrow-style "bottom" 8 target popover)]
    (is (= "-8px" (:top s)))
    ;; Arrow should be at target center X (140) minus popover left (50) = 90px
    (is (= "90px" (:left s)))))

(deftest arrow-style-left-points-at-target-center-test
  (let [target  {:x 300 :y 100 :width 80 :height 60}
        popover {:x 100 :y 80 :width 180 :height 200}
        s       (model/arrow-style "left" 8 target popover)]
    (is (= "-8px" (:right s)))
    ;; Arrow should be at target center Y (130) minus popover top (80) = 50px
    (is (= "50px" (:top s)))))

(deftest arrow-style-clamps-to-popover-edge-test
  (let [;; Target is far right, popover is far left — arrow can't reach target center
        target  {:x 500 :y 50 :width 80 :height 40}
        popover {:x 50 :y 100 :width 200 :height 120}
        s       (model/arrow-style "bottom" 8 target popover)]
    (is (= "-8px" (:top s)))
    ;; Arrow clamped to (200 - 16) = 184px from left edge
    (is (= "184px" (:left s)))))

;; ── Event detail builders ───────────────────────────────────────────────────
(deftest step-change-detail-test
  (let [d (model/step-change-detail 2 1)]
    (is (= 2 (:step d)))
    (is (= 1 (:previousStep d)))))

(deftest complete-detail-test
  (let [d (model/complete-detail 5)]
    (is (= 5 (:stepsCompleted d)))))

(deftest skip-detail-test
  (let [d (model/skip-detail 3)]
    (is (= 3 (:step d)))))
