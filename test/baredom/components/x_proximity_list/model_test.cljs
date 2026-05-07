(ns baredom.components.x-proximity-list.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-proximity-list.model :as model]
            [baredom.utils.model :as mu]))

;; ── parse-direction ─────────────────────────────────────────────────────────
(deftest parse-direction-valid-test
  (is (= "horizontal" (model/parse-direction "horizontal")))
  (is (= "vertical"   (model/parse-direction "vertical"))))

(deftest parse-direction-case-insensitive-test
  (is (= "horizontal" (model/parse-direction "Horizontal")))
  (is (= "vertical"   (model/parse-direction "VERTICAL")))
  (is (= "vertical"   (model/parse-direction " Vertical "))))

(deftest parse-direction-default-test
  (is (= "horizontal" (model/parse-direction nil)))
  (is (= "horizontal" (model/parse-direction "")))
  (is (= "horizontal" (model/parse-direction "diagonal"))))

;; ── parse-float-clamped ─────────────────────────────────────────────────────
(deftest parse-float-clamped-test
  (is (= 5.0  (model/parse-float-clamped "5" 10 0 20)))
  (is (= 0    (model/parse-float-clamped "-5" 10 0 20)))
  (is (= 20   (model/parse-float-clamped "25" 10 0 20)))
  (is (= 10   (model/parse-float-clamped nil 10 0 20)))
  (is (= 10   (model/parse-float-clamped "abc" 10 0 20))))

;; ── parse-radius ────────────────────────────────────────────────────────────
(deftest parse-radius-valid-test
  (is (= 120 (model/parse-radius "120")))
  (is (= 200 (model/parse-radius "200"))))

(deftest parse-radius-default-test
  (is (= 120 (model/parse-radius nil)))
  (is (= 120 (model/parse-radius "abc"))))

(deftest parse-radius-clamping-test
  (is (= 20  (model/parse-radius "10")))
  (is (= 600 (model/parse-radius "1000"))))

;; ── parse-max-scale ─────────────────────────────────────────────────────────
(deftest parse-max-scale-valid-test
  (is (= 1.5 (model/parse-max-scale "1.5")))
  (is (= 2.0 (model/parse-max-scale "2"))))

(deftest parse-max-scale-default-test
  (is (= 1.5 (model/parse-max-scale nil)))
  (is (= 1.5 (model/parse-max-scale "abc"))))

(deftest parse-max-scale-clamping-test
  (is (= 1.0 (model/parse-max-scale "0.2")))
  (is (= 3.0 (model/parse-max-scale "5"))))

;; ── parse-lift ──────────────────────────────────────────────────────────────
(deftest parse-lift-valid-test
  (is (= 0    (model/parse-lift "0")))
  (is (= 12.0 (model/parse-lift "12"))))

(deftest parse-lift-default-test
  (is (= 0 (model/parse-lift nil)))
  (is (= 0 (model/parse-lift "abc"))))

(deftest parse-lift-clamping-test
  (is (= 0  (model/parse-lift "-10")))
  (is (= 60 (model/parse-lift "999"))))

;; ── parse-gap ───────────────────────────────────────────────────────────────
(deftest parse-gap-valid-test
  (is (= 8  (model/parse-gap "8")))
  (is (= 16 (model/parse-gap "16")))
  (is (= 0  (model/parse-gap "0"))))

(deftest parse-gap-default-test
  (is (= 8 (model/parse-gap nil)))
  (is (= 8 (model/parse-gap "abc"))))

(deftest parse-gap-clamping-test
  (is (= 0  (model/parse-gap "-5")))
  (is (= 64 (model/parse-gap "999"))))

;; ── parse-bool-present ──────────────────────────────────────────────────────
(deftest parse-bool-present-test
  (is (false? (mu/parse-bool-present nil)))
  (is (true?  (mu/parse-bool-present "")))
  (is (true?  (mu/parse-bool-present "anything"))))

;; ── normalize ───────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= "horizontal" (:direction m)))
    (is (= 120          (:radius m)))
    (is (= 1.5          (:max-scale m)))
    (is (= 0            (:lift m)))
    (is (= 8            (:gap m)))
    (is (false?         (:disabled? m)))
    (is (false?         (:vertical? m)))))

(deftest normalize-custom-values-test
  (let [m (model/normalize {:direction-raw "vertical"
                            :radius-raw    "200"
                            :max-scale-raw "2.0"
                            :lift-raw      "20"
                            :gap-raw       "12"
                            :disabled-attr ""})]
    (is (= "vertical" (:direction m)))
    (is (= 200        (:radius m)))
    (is (= 2.0        (:max-scale m)))
    (is (= 20.0       (:lift m)))
    (is (= 12         (:gap m)))
    (is (true?        (:disabled? m)))
    (is (true?        (:vertical? m)))))

(deftest normalize-vertical-flag-test
  (is (true?  (:vertical? (model/normalize {:direction-raw "vertical"}))))
  (is (false? (:vertical? (model/normalize {:direction-raw "horizontal"}))))
  (is (false? (:vertical? (model/normalize {:direction-raw "garbage"})))))

;; ── whitespace handling ─────────────────────────────────────────────────────
(deftest whitespace-trimmed-test
  (is (= 16    (model/parse-gap " 16 ")))
  (is (= 200   (model/parse-radius " 200 ")))
  (is (= 2.0   (model/parse-max-scale " 2 ")))
  (is (= 12.0  (model/parse-lift " 12 "))))

;; ── compute-influence ───────────────────────────────────────────────────────
(deftest compute-influence-at-center-test
  (is (= 1.0 (model/compute-influence 100 100 100 100 120))))

(deftest compute-influence-at-edge-test
  (is (= 0.0 (model/compute-influence 100 100 220 100 120))))

(deftest compute-influence-out-of-range-test
  (is (= 0.0 (model/compute-influence 100 100 500 100 120))))

(deftest compute-influence-midrange-test
  (let [inf (model/compute-influence 100 100 160 100 120)]
    (is (> inf 0.0))
    (is (< inf 1.0))
    (is (= 0.5 inf) "60px out of 120 radius is 50% influence")))

;; ── compute-scale ───────────────────────────────────────────────────────────
(deftest compute-scale-no-influence-test
  (is (= 1.0 (model/compute-scale 0.0 1.5))))

(deftest compute-scale-full-influence-test
  (is (= 1.5 (model/compute-scale 1.0 1.5)))
  (is (= 2.0 (model/compute-scale 1.0 2.0))))

(deftest compute-scale-quadratic-test
  ;; Influence 0.5 with max-scale 2.0 → 1 + 0.25 * 1 = 1.25
  (is (= 1.25 (model/compute-scale 0.5 2.0))))

;; ── compute-lift ────────────────────────────────────────────────────────────
(deftest compute-lift-no-influence-test
  (is (= 0.0 (model/compute-lift 0.0 50 20))))

(deftest compute-lift-no-cross-delta-test
  (is (= 0.0 (model/compute-lift 1.0 0 20))))

(deftest compute-lift-positive-direction-test
  ;; cross-delta beyond soft band, full influence → +lift-max
  (is (= 20.0 (model/compute-lift 1.0 100 20))))

(deftest compute-lift-negative-direction-test
  (is (= -20.0 (model/compute-lift 1.0 -100 20))))

(deftest compute-lift-soft-band-test
  ;; cross-delta inside the 30px soft band → ramped linearly
  ;; influence=1, cross-delta=15 → 20 * 1 * (15/30) = 10
  (is (= 10.0 (model/compute-lift 1.0 15 20))))

(deftest compute-lift-quadratic-influence-test
  ;; influence=0.5, cross-delta=100 (>30) → 20 * 0.25 * 1 = 5
  (is (= 5.0 (model/compute-lift 0.5 100 20))))

;; ── lerp ────────────────────────────────────────────────────────────────────
(deftest lerp-midpoint-test
  (is (= 5.0 (model/lerp 0 10 0.5))))

(deftest lerp-zero-speed-test
  (is (= 5.0 (model/lerp 5 10 0))))

(deftest lerp-full-speed-test
  (is (= 10.0 (model/lerp 5 10 1.0))))

(deftest lerp-at-target-test
  (is (= 10.0 (model/lerp 10 10 0.3))))

;; ── select-detail ───────────────────────────────────────────────────────────
(deftest select-detail-test
  (let [item #js {}
        d    (model/select-detail 3 item "pointer")]
    (is (= 3 (get d "index")))
    (is (= item (get d "item")))
    (is (= "pointer" (get d "source")))))
