(ns baredom.components.x-range-slider.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-range-slider.model :as model]))

;; ---------------------------------------------------------------------------
;; normalize-number / min / max
;; ---------------------------------------------------------------------------

(deftest normalize-number-test
  (testing "valid float string"
    (is (= 42.5 (model/normalize-number "42.5" 0))))
  (testing "integer string"
    (is (= 10 (model/normalize-number "10" 0))))
  (testing "NaN string returns default"
    (is (= 7 (model/normalize-number "abc" 7))))
  (testing "nil returns default"
    (is (= 5 (model/normalize-number nil 5)))))

(deftest normalize-min-max-test
  (is (= 10 (model/normalize-min "10")))
  (is (= model/default-min (model/normalize-min nil)))
  (is (= 200 (model/normalize-max "200")))
  (is (= model/default-max (model/normalize-max nil))))

;; ---------------------------------------------------------------------------
;; normalize-step / step-size
;; ---------------------------------------------------------------------------

(deftest normalize-step-test
  (testing "\"any\" passes through"
    (is (= "any" (model/normalize-step "any"))))
  (testing "valid positive number"
    (is (= "5" (model/normalize-step "5"))))
  (testing "nil falls back to default"
    (is (= model/default-step (model/normalize-step nil))))
  (testing "zero is invalid"
    (is (= model/default-step (model/normalize-step "0"))))
  (testing "negative is invalid"
    (is (= model/default-step (model/normalize-step "-1")))))

(deftest step-size-test
  (is (= 5 (model/step-size "5")))
  (is (= 0.5 (model/step-size "0.5")))
  (is (= 1 (model/step-size "1")))
  (is (nil? (model/step-size "any"))))

;; ---------------------------------------------------------------------------
;; normalize-size
;; ---------------------------------------------------------------------------

(deftest normalize-size-test
  (is (= "sm" (model/normalize-size "sm")))
  (is (= "md" (model/normalize-size "md")))
  (is (= "lg" (model/normalize-size "lg")))
  (is (= model/default-size (model/normalize-size nil)))
  (is (= model/default-size (model/normalize-size "xl"))))

;; ---------------------------------------------------------------------------
;; normalize-min-gap
;; ---------------------------------------------------------------------------

(deftest normalize-min-gap-test
  (is (= 5 (model/normalize-min-gap "5")))
  (is (= 0 (model/normalize-min-gap nil)))
  (is (= 0 (model/normalize-min-gap "-3")))
  (is (= 0 (model/normalize-min-gap "abc"))))

;; ---------------------------------------------------------------------------
;; snap-to-step
;; ---------------------------------------------------------------------------

(deftest snap-to-step-test
  (testing "snaps down to nearest step"
    (is (= 5 (model/snap-to-step 7 0 5))))
  (testing "snaps up to nearest step"
    (is (= 10 (model/snap-to-step 8 0 5))))
  (testing "step measured from min"
    (is (= 15 (model/snap-to-step 13 10 5))))
  (testing "nil step leaves value unchanged"
    (is (= 7.3 (model/snap-to-step 7.3 0 nil))))
  (testing "non-positive step leaves value unchanged"
    (is (= 7 (model/snap-to-step 7 0 0))))
  (testing "suppresses floating-point noise"
    (is (= 0.7 (model/snap-to-step 0.7 0 0.1)))))

;; ---------------------------------------------------------------------------
;; clamp / clamp-start / clamp-end
;; ---------------------------------------------------------------------------

(deftest clamp-test
  (is (= 50 (model/clamp 50 0 100)))
  (is (= 0 (model/clamp -10 0 100)))
  (is (= 100 (model/clamp 150 0 100)))
  (testing "degenerate hi < lo returns lo"
    (is (= 100 (model/clamp 50 100 50)))))

(deftest clamp-start-test
  (testing "clamps to end with no gap"
    (is (= 80 (model/clamp-start 90 0 80 0))))
  (testing "clamps to end minus gap"
    (is (= 70 (model/clamp-start 90 0 80 10))))
  (testing "value within range is unchanged"
    (is (= 40 (model/clamp-start 40 0 80 10))))
  (testing "degenerate range pins to min"
    (is (= 0 (model/clamp-start 50 0 5 10)))))

(deftest clamp-end-test
  (testing "clamps to start with no gap"
    (is (= 20 (model/clamp-end 5 20 100 0))))
  (testing "clamps to start plus gap"
    (is (= 30 (model/clamp-end 5 20 100 10))))
  (testing "value within range is unchanged"
    (is (= 60 (model/clamp-end 60 20 100 10))))
  (testing "clamps to max"
    (is (= 100 (model/clamp-end 200 20 100 0)))))

;; ---------------------------------------------------------------------------
;; client-x->ratio / client-x->value
;; ---------------------------------------------------------------------------

(deftest client-x->ratio-test
  (is (= 0.5 (model/client-x->ratio 100 0 200)))
  (testing "clamps below 0"
    (is (= 0.0 (model/client-x->ratio -50 0 200))))
  (testing "clamps above 1"
    (is (= 1.0 (model/client-x->ratio 300 0 200))))
  (testing "zero width returns 0"
    (is (= 0.0 (model/client-x->ratio 100 0 0)))))

(deftest client-x->value-test
  (is (= 50.0 (model/client-x->value 100 0 200 0 100)))
  (is (= 0 (model/client-x->value 0 0 200 0 100)))
  (is (= 100.0 (model/client-x->value 200 0 200 0 100)))
  (testing "custom range with rect offset"
    (is (= 30.0 (model/client-x->value 50 0 200 10 90)))))

;; ---------------------------------------------------------------------------
;; fill-percent / segment-geometry
;; ---------------------------------------------------------------------------

(deftest fill-percent-test
  (is (= 0.0 (model/fill-percent 0 0 100)))
  (is (= 100.0 (model/fill-percent 100 0 100)))
  (is (= 50.0 (model/fill-percent 50 0 100)))
  (is (= 50.0 (model/fill-percent 150 100 200)))
  (testing "max <= min returns 0"
    (is (= 0.0 (model/fill-percent 50 100 50)))))

(deftest segment-geometry-test
  (testing "left and width for a normal range"
    (let [g (model/segment-geometry 20 80 0 100)]
      (is (= 20.0 (:left g)))
      (is (= 60.0 (:width g)))))
  (testing "coincident thumbs give zero width"
    (let [g (model/segment-geometry 50 50 0 100)]
      (is (= 50.0 (:left g)))
      (is (= 0.0 (:width g)))))
  (testing "inverted pair never yields negative width"
    (is (= 0.0 (:width (model/segment-geometry 80 20 0 100))))))

;; ---------------------------------------------------------------------------
;; nearest-thumb
;; ---------------------------------------------------------------------------

(deftest nearest-thumb-test
  (is (= :start (model/nearest-thumb 10 20 80)))
  (is (= :end (model/nearest-thumb 70 20 80)))
  (testing "tie resolves to the thumb on the click side"
    (is (= :end (model/nearest-thumb 50 20 80))))
  (testing "coincident thumbs — click at or below the value picks start"
    (is (= :start (model/nearest-thumb 50 50 50))))
  (testing "coincident thumbs — click above the value picks end"
    (is (= :end (model/nearest-thumb 60 50 50)))))

;; ---------------------------------------------------------------------------
;; key-target
;; ---------------------------------------------------------------------------

(deftest key-target-test
  (is (= 55 (model/key-target "ArrowRight" false 50 0 100 5)))
  (is (= 45 (model/key-target "ArrowLeft" false 50 0 100 5)))
  (testing "shift multiplies the arrow step by ten"
    (is (= 100 (model/key-target "ArrowRight" true 50 0 100 5))))
  (testing "page keys move by ten steps"
    (is (= 100 (model/key-target "PageUp" false 50 0 100 5)))
    (is (= 0 (model/key-target "PageDown" false 50 0 100 5))))
  (testing "Home / End jump to the bounds"
    (is (= 0 (model/key-target "Home" false 50 0 100 5)))
    (is (= 100 (model/key-target "End" false 50 0 100 5))))
  (testing "unknown key leaves the value unchanged"
    (is (= 50 (model/key-target "Tab" false 50 0 100 5)))))

;; ---------------------------------------------------------------------------
;; resolve-range
;; ---------------------------------------------------------------------------

(deftest resolve-range-test
  (testing "ordered pair within bounds is unchanged"
    (is (= {:start 20 :end 80} (model/resolve-range 20 80 0 100 0))))
  (testing "start past end — end is pulled up to start (no swap)"
    (is (= {:start 90 :end 90} (model/resolve-range 90 20 0 100 0))))
  (testing "min-gap pushes end up"
    (is (= {:start 40 :end 50} (model/resolve-range 40 42 0 100 10))))
  (testing "at the ceiling, start gives way instead"
    (is (= {:start 90 :end 100} (model/resolve-range 95 100 0 100 10)))))

;; ---------------------------------------------------------------------------
;; normalize
;; ---------------------------------------------------------------------------

(deftest normalize-defaults-test
  (testing "empty input produces the widest range"
    (let [m (model/normalize {})]
      (is (= model/default-min (:min m)))
      (is (= model/default-max (:max m)))
      (is (= 0 (:start m)))
      (is (= 100 (:end m)))
      (is (= model/default-step (:step m)))
      (is (= 0 (:min-gap m)))
      (is (= model/default-size (:size m)))
      (is (= false (:disabled? m)))
      (is (= false (:readonly? m)))
      (is (= false (:show-value? m)))
      (is (nil? (:name m)))
      (is (nil? (:label m)))
      (is (= 0.0 (:start-pct m)))
      (is (= 100.0 (:end-pct m)))
      (is (= 0.0 (:fill-left m)))
      (is (= 100.0 (:fill-width m))))))

(deftest normalize-clamp-test
  (testing "start past end resolves both to the start value"
    (let [m (model/normalize {:start "90" :end "20"})]
      (is (= 90 (:start m)))
      (is (= 90 (:end m)))))
  (testing "values are clamped within min/max"
    (let [m (model/normalize {:start "-10" :end "150" :min "0" :max "100"})]
      (is (= 0 (:start m)))
      (is (= 100 (:end m))))))

(deftest normalize-min-gap-resolution-test
  (testing "end is pushed up to satisfy the gap"
    (let [m (model/normalize {:start "40" :end "42" :min-gap "10"})]
      (is (= 40 (:start m)))
      (is (= 50 (:end m)))))
  (testing "at the ceiling, start is pulled down"
    (let [m (model/normalize {:start "95" :end "100" :min-gap "10" :max "100"})]
      (is (= 90 (:start m)))
      (is (= 100 (:end m)))))
  (testing "an over-large gap is capped at the full range"
    (let [m (model/normalize {:min "0" :max "10" :min-gap "999"})]
      (is (= 10 (:min-gap m)))
      (is (= 0 (:start m)))
      (is (= 10 (:end m))))))

(deftest normalize-step-snapping-test
  (testing "both values snap to step"
    (let [m (model/normalize {:start "13" :end "87" :step "5"})]
      (is (= 15 (:start m)))
      (is (= 85 (:end m)))))
  (testing "step \"any\" leaves fractional values intact"
    (let [m (model/normalize {:step "any" :start "13.3" :end "88.8"})]
      (is (= 13.3 (:start m)))
      (is (= 88.8 (:end m))))))

(deftest normalize-fill-geometry-test
  (let [m (model/normalize {:start "25" :end "75"})]
    (is (= 25.0 (:start-pct m)))
    (is (= 75.0 (:end-pct m)))
    (is (= 25.0 (:fill-left m)))
    (is (= 50.0 (:fill-width m)))))

(deftest normalize-booleans-test
  (let [m (model/normalize {:disabled true :readonly true :show-value true})]
    (is (= true (:disabled? m)))
    (is (= true (:readonly? m)))
    (is (= true (:show-value? m)))))

(deftest normalize-label-test
  (testing "empty label becomes nil"
    (is (nil? (:label (model/normalize {:label ""})))))
  (testing "non-empty label is preserved"
    (is (= "Price" (:label (model/normalize {:label "Price"}))))))

(deftest normalize-aria-test
  (let [m (model/normalize {:aria-label "Price"
                            :aria-labelledby "lbl-1"
                            :aria-describedby "hint-1"})]
    (is (= "Price" (:aria-label m)))
    (is (= "lbl-1" (:aria-labelledby m)))
    (is (= "hint-1" (:aria-describedby m))))
  (testing "empty aria strings become nil"
    (is (nil? (:aria-label (model/normalize {:aria-label ""}))))))

;; ---------------------------------------------------------------------------
;; interactable? / value-text
;; ---------------------------------------------------------------------------

(deftest interactable?-test
  (is (true? (model/interactable? {})))
  (is (false? (model/interactable? {:disabled? true})))
  (is (false? (model/interactable? {:readonly? true}))))

(deftest value-text-test
  (is (= "20 – 80" (model/value-text 20 80))))
