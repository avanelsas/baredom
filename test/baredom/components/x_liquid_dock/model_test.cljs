(ns baredom.components.x-liquid-dock.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-liquid-dock.model :as model]))

;; ── parse-position ──────────────────────────────────────────────────────────
(deftest parse-position-valid-test
  (is (= "bottom" (model/parse-position "bottom")))
  (is (= "top"    (model/parse-position "top")))
  (is (= "left"   (model/parse-position "left")))
  (is (= "right"  (model/parse-position "right"))))

(deftest parse-position-case-insensitive-test
  (is (= "bottom" (model/parse-position "Bottom")))
  (is (= "top"    (model/parse-position "TOP")))
  (is (= "left"   (model/parse-position " Left "))))

(deftest parse-position-default-test
  (is (= "bottom" (model/parse-position nil)))
  (is (= "bottom" (model/parse-position "")))
  (is (= "bottom" (model/parse-position "invalid"))))

;; ── parse-float-clamped ─────────────────────────────────────────────────────
(deftest parse-float-clamped-test
  (is (= 5.0  (model/parse-float-clamped "5" 10 0 20)))
  (is (= 0    (model/parse-float-clamped "-5" 10 0 20)))
  (is (= 20   (model/parse-float-clamped "25" 10 0 20)))
  (is (= 10   (model/parse-float-clamped nil 10 0 20)))
  (is (= 10   (model/parse-float-clamped "abc" 10 0 20))))

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
  (is (= 40 (model/parse-gap "100"))))

;; ── parse-blur ──────────────────────────────────────────────────────────────
(deftest parse-blur-valid-test
  (is (= 10 (model/parse-blur "10")))
  (is (= 20 (model/parse-blur "20"))))

(deftest parse-blur-default-test
  (is (= 10 (model/parse-blur nil)))
  (is (= 10 (model/parse-blur "abc"))))

(deftest parse-blur-clamping-test
  (is (= 2  (model/parse-blur "1")))
  (is (= 30 (model/parse-blur "50"))))

;; ── parse-threshold ─────────────────────────────────────────────────────────
(deftest parse-threshold-valid-test
  (is (= "1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 30 -15"
         (model/parse-threshold "1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 30 -15"))))

(deftest parse-threshold-default-test
  (is (= "1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 18 -7"
         (model/parse-threshold nil)))
  (is (= "1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 18 -7"
         (model/parse-threshold ""))))

;; ── parse-ripple-scale ──────────────────────────────────────────────────────
(deftest parse-ripple-scale-valid-test
  (is (= 8  (model/parse-ripple-scale "8")))
  (is (= 20 (model/parse-ripple-scale "20"))))

(deftest parse-ripple-scale-default-test
  (is (= 8 (model/parse-ripple-scale nil))))

(deftest parse-ripple-scale-clamping-test
  (is (= 0  (model/parse-ripple-scale "-5")))
  (is (= 40 (model/parse-ripple-scale "50"))))

;; ── parse-ripple-speed ──────────────────────────────────────────────────────
(deftest parse-ripple-speed-valid-test
  (is (= 0.03 (model/parse-ripple-speed "0.03")))
  (is (= 0.1  (model/parse-ripple-speed "0.1"))))

(deftest parse-ripple-speed-default-test
  (is (= 0.03 (model/parse-ripple-speed nil))))

(deftest parse-ripple-speed-clamping-test
  (is (= 0.005 (model/parse-ripple-speed "0.001")))
  (is (= 0.15  (model/parse-ripple-speed "0.5"))))

;; ── parse-color ─────────────────────────────────────────────────────────────
(deftest parse-color-valid-test
  (is (= "#ff0000" (model/parse-color "#ff0000")))
  (is (= "red"     (model/parse-color "red")))
  (is (= "rgba(0,0,0,0.5)" (model/parse-color "rgba(0,0,0,0.5)"))))

(deftest parse-color-default-test
  (is (= "#6366f1" (model/parse-color nil)))
  (is (= "#6366f1" (model/parse-color "")))
  (is (= "#6366f1" (model/parse-color "   "))))

;; ── parse-magnet-radius ─────────────────────────────────────────────────────
(deftest parse-magnet-radius-valid-test
  (is (= 150 (model/parse-magnet-radius "150")))
  (is (= 200 (model/parse-magnet-radius "200"))))

(deftest parse-magnet-radius-default-test
  (is (= 150 (model/parse-magnet-radius nil))))

(deftest parse-magnet-radius-clamping-test
  (is (= 40  (model/parse-magnet-radius "10")))
  (is (= 400 (model/parse-magnet-radius "500"))))

;; ── parse-magnet-strength ───────────────────────────────────────────────────
(deftest parse-magnet-strength-valid-test
  (is (= 0.6 (model/parse-magnet-strength "0.6")))
  (is (= 1.0 (model/parse-magnet-strength "1.0"))))

(deftest parse-magnet-strength-default-test
  (is (= 0.6 (model/parse-magnet-strength nil))))

(deftest parse-magnet-strength-clamping-test
  (is (= 0.0 (model/parse-magnet-strength "-1")))
  (is (= 2.0 (model/parse-magnet-strength "5"))))

;; ── parse-bool-attr ─────────────────────────────────────────────────────────
(deftest parse-bool-attr-test
  (is (false? (model/parse-bool-attr nil)))
  (is (true?  (model/parse-bool-attr "")))
  (is (true?  (model/parse-bool-attr "true")))
  (is (true?  (model/parse-bool-attr "anything"))))

;; ── parse-bob-intensity ──────────────────────────────────────────────────────
(deftest parse-bob-intensity-valid-test
  (is (= 1.0 (model/parse-bob-intensity "1.0")))
  (is (= 0.5 (model/parse-bob-intensity "0.5")))
  (is (= 2.0 (model/parse-bob-intensity "2.0"))))

(deftest parse-bob-intensity-default-test
  (is (= 1.0 (model/parse-bob-intensity nil)))
  (is (= 1.0 (model/parse-bob-intensity "abc"))))

(deftest parse-bob-intensity-clamping-test
  (is (= 0.0 (model/parse-bob-intensity "-1")))
  (is (= 2.0 (model/parse-bob-intensity "5"))))

;; ── normalize ───────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= "bottom"  (:position m)))
    (is (= 8         (:gap m)))
    (is (= 10        (:blur m)))
    (is (= 8         (:ripple-scale m)))
    (is (= 0.03      (:ripple-speed m)))
    (is (= "#6366f1" (:color m)))
    (is (= 150       (:magnet-radius m)))
    (is (= 0.6       (:magnet-strength m)))
    (is (= 1.0       (:bob-intensity m)))
    (is (false?      (:disabled? m)))
    (is (false?      (:vertical? m)))))

(deftest normalize-custom-values-test
  (let [m (model/normalize {:position-raw        "left"
                            :gap-raw             "12"
                            :blur-raw            "15"
                            :threshold-raw       "1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 25 -10"
                            :ripple-scale-raw    "12"
                            :ripple-speed-raw    "0.05"
                            :color-raw           "hotpink"
                            :magnet-radius-raw   "200"
                            :magnet-strength-raw "1.2"
                            :bob-intensity-raw   "1.5"
                            :disabled-attr       ""})]
    (is (= "left"    (:position m)))
    (is (= 12        (:gap m)))
    (is (= 15        (:blur m)))
    (is (= 12        (:ripple-scale m)))
    (is (= 0.05      (:ripple-speed m)))
    (is (= "hotpink" (:color m)))
    (is (= 200       (:magnet-radius m)))
    (is (= 1.2       (:magnet-strength m)))
    (is (= 1.5       (:bob-intensity m)))
    (is (true?       (:disabled? m)))
    (is (true?       (:vertical? m)))))

(deftest normalize-vertical-test
  (is (true?  (:vertical? (model/normalize {:position-raw "left"}))))
  (is (true?  (:vertical? (model/normalize {:position-raw "right"}))))
  (is (false? (:vertical? (model/normalize {:position-raw "top"}))))
  (is (false? (:vertical? (model/normalize {:position-raw "bottom"})))))

;; ── whitespace handling ─────────────────────────────────────────────────────
(deftest whitespace-trimmed-test
  (is (= 12 (model/parse-gap " 12 ")))
  (is (= 15 (model/parse-blur " 15 ")))
  (is (= 10 (model/parse-ripple-scale " 10 "))))

;; ── lerp ────────────────────────────────────────────────────────────────────
(deftest lerp-midpoint-test
  (is (= 5.0 (model/lerp 0 10 0.5))))

(deftest lerp-at-target-test
  (is (= 10.0 (model/lerp 10 10 0.3))))

(deftest lerp-zero-speed-test
  (is (= 5.0 (model/lerp 5 10 0))))

(deftest lerp-full-speed-test
  (is (= 10.0 (model/lerp 5 10 1.0))))

;; ── compute-item-influence ──────────────────────────────────────────────────
(deftest compute-item-influence-at-center-test
  (let [result (model/compute-item-influence 100 100 100 100 150 0.6 1.5)]
    (is (= 1.0 (:influence result)))
    (is (= 1.5 (:scale result)))))

(deftest compute-item-influence-out-of-range-test
  (let [result (model/compute-item-influence 100 100 500 500 150 0.6 1.5)]
    (is (= 0.0 (:influence result)))
    (is (= 1.0 (:scale result)))
    (is (= 0.0 (:tx result)))
    (is (= 0.0 (:ty result)))))

(deftest compute-item-influence-midrange-test
  (let [result (model/compute-item-influence 100 100 175 100 150 0.6 1.5)]
    (is (> (:influence result) 0.0))
    (is (< (:influence result) 1.0))
    (is (> (:scale result) 1.0))
    (is (< (:scale result) 1.5))))

;; ── compute-phantom-offset ───────────────────────────────────────────────────
(deftest compute-phantom-offset-no-influence-test
  (let [r (model/compute-phantom-offset 0.0 "bottom")]
    (is (= 0.0 (:dx r)))
    (is (= 0.0 (:dy r)))
    (is (= 0.6 (:scale r)))))

(deftest compute-phantom-offset-full-influence-bottom-test
  (let [r (model/compute-phantom-offset 1.0 "bottom")]
    (is (= 0.0 (:dx r)))
    (is (< (:dy r) 0.0) "should lift upward (negative y)")
    (is (> (:scale r) 1.0))))

(deftest compute-phantom-offset-full-influence-top-test
  (let [r (model/compute-phantom-offset 1.0 "top")]
    (is (= 0.0 (:dx r)))
    (is (> (:dy r) 0.0) "should push downward (positive y)")))

(deftest compute-phantom-offset-full-influence-left-test
  (let [r (model/compute-phantom-offset 1.0 "left")]
    (is (> (:dx r) 0.0) "should push rightward")
    (is (= 0.0 (:dy r)))))

(deftest compute-phantom-offset-full-influence-right-test
  (let [r (model/compute-phantom-offset 1.0 "right")]
    (is (< (:dx r) 0.0) "should push leftward")
    (is (= 0.0 (:dy r)))))

;; ── compute-bob-tilt ────────────────────────────────────────────────────────
(deftest compute-bob-tilt-no-influence-test
  (let [r (model/compute-bob-tilt 0.0 1.0 "bottom" 1.0)]
    (is (= 0.0 (:lift r)))
    (is (= 0.0 (:rotation r)))
    (is (= 1.0 (:scale r)))))

(deftest compute-bob-tilt-full-influence-bottom-test
  (let [r (model/compute-bob-tilt 1.0 1.0 "bottom" 1.0)]
    (is (< (:lift r) 0.0) "bottom dock: icons lift upward (negative)")
    (is (> (:rotation r) 0.0) "positive dx-sign = tilt right")
    (is (> (:scale r) 1.0))))

(deftest compute-bob-tilt-full-influence-top-test
  (let [r (model/compute-bob-tilt 1.0 -1.0 "top" 1.0)]
    (is (> (:lift r) 0.0) "top dock: icons lift downward (positive)")
    (is (< (:rotation r) 0.0) "negative dx-sign = tilt left")))

(deftest compute-bob-tilt-zero-intensity-test
  (let [r (model/compute-bob-tilt 1.0 1.0 "bottom" 0.0)]
    (is (= 0.0 (:lift r)))
    (is (= 0.0 (:rotation r)))
    (is (= 1.0 (:scale r)))))

(deftest compute-bob-tilt-double-intensity-test
  (let [r1 (model/compute-bob-tilt 1.0 1.0 "bottom" 1.0)
        r2 (model/compute-bob-tilt 1.0 1.0 "bottom" 2.0)]
    (is (< (:lift r2) (:lift r1)) "double intensity = more lift")
    (is (> (js/Math.abs (:rotation r2)) (js/Math.abs (:rotation r1))))))

;; ── select-detail ───────────────────────────────────────────────────────────
(deftest select-detail-test
  (let [item  #js {}
        d     (model/select-detail 2 item "pointer")]
    (is (= 2 (get d "index")))
    (is (= item (get d "item")))
    (is (= "pointer" (get d "source")))))
