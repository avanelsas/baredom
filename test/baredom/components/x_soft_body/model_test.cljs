(ns baredom.components.x-soft-body.model-test
  (:require
   [cljs.test :refer-macros [deftest is testing]]
   [baredom.components.x-soft-body.model :as model]))

;; ── parse-stiffness ─────────────────────────────────────────────────────────

(deftest parse-stiffness-default-test
  (is (= 180.0 (model/parse-stiffness nil)))
  (is (= 180.0 (model/parse-stiffness "")))
  (is (= 180.0 (model/parse-stiffness "abc"))))

(deftest parse-stiffness-valid-test
  (is (= 200.0 (model/parse-stiffness "200")))
  (is (= 10.0 (model/parse-stiffness "10")))
  (is (= 500.0 (model/parse-stiffness "500"))))

(deftest parse-stiffness-clamps-test
  (is (= 10.0 (model/parse-stiffness "1")))
  (is (= 1000.0 (model/parse-stiffness "9999"))))

;; ── parse-damping ───────────────────────────────────────────────────────────

(deftest parse-damping-default-test
  (is (= 12.0 (model/parse-damping nil)))
  (is (= 12.0 (model/parse-damping "abc"))))

(deftest parse-damping-valid-test
  (is (= 8.0 (model/parse-damping "8")))
  (is (= 1.0 (model/parse-damping "1")))
  (is (= 50.0 (model/parse-damping "50"))))

(deftest parse-damping-clamps-test
  (is (= 1.0 (model/parse-damping "0.5")))
  (is (= 100.0 (model/parse-damping "200"))))

;; ── parse-radius ────────────────────────────────────────────────────────────

(deftest parse-radius-default-test
  (is (= 16.0 (model/parse-radius nil))))

(deftest parse-radius-valid-test
  (is (= 6.0 (model/parse-radius "6")))
  (is (= 24.0 (model/parse-radius "24"))))

(deftest parse-radius-clamps-test
  (is (= 6.0 (model/parse-radius "0")))
  (is (= 6.0 (model/parse-radius "-5")))
  (is (= 200.0 (model/parse-radius "999"))))

;; ── parse-intensity ─────────────────────────────────────────────────────────

(deftest parse-intensity-default-test
  (is (= 1.0 (model/parse-intensity nil))))

(deftest parse-intensity-valid-test
  (is (= 2.5 (model/parse-intensity "2.5"))))

(deftest parse-intensity-clamps-test
  (is (= 0.0 (model/parse-intensity "-1")))
  (is (= 5.0 (model/parse-intensity "10"))))

;; ── parse-grab-radius ───────────────────────────────────────────────────────

(deftest parse-grab-radius-default-test
  (is (= 80.0 (model/parse-grab-radius nil))))

(deftest parse-grab-radius-valid-test
  (is (= 120.0 (model/parse-grab-radius "120"))))

(deftest parse-grab-radius-clamps-test
  (is (= 10.0 (model/parse-grab-radius "5")))
  (is (= 500.0 (model/parse-grab-radius "1000"))))

;; ── parse-disabled ──────────────────────────────────────────────────────────

(deftest parse-disabled-test
  (is (false? (model/parse-disabled nil)))
  (is (true? (model/parse-disabled "")))
  (is (true? (model/parse-disabled "true")))
  (is (true? (model/parse-disabled "disabled"))))

;; ── derive-state ────────────────────────────────────────────────────────────

(deftest derive-state-defaults-test
  (let [m (model/derive-state {})]
    (is (= 180.0 (:stiffness m)))
    (is (= 12.0 (:damping m)))
    (is (= 16.0 (:radius m)))
    (is (= 1.0 (:intensity m)))
    (is (= 80.0 (:grab-radius m)))
    (is (false? (:disabled? m)))))

(deftest derive-state-overrides-test
  (let [m (model/derive-state {:stiffness-raw "300"
                               :damping-raw "20"
                               :radius-raw "32"
                               :intensity-raw "2"
                               :grab-radius-raw "100"
                               :disabled-attr ""})]
    (is (= 300.0 (:stiffness m)))
    (is (= 20.0 (:damping m)))
    (is (= 32.0 (:radius m)))
    (is (= 2.0 (:intensity m)))
    (is (= 100.0 (:grab-radius m)))
    (is (true? (:disabled? m)))))

;; ── generate-rest-points ────────────────────────────────────────────────────

(deftest generate-rest-points-count-test
  (let [result (model/generate-rest-points 300.0 200.0 16.0)
        ^js xs (aget result 0)
        ^js ys (aget result 1)]
    (is (= model/point-count (.-length xs)))
    (is (= model/point-count (.-length ys)))))

(deftest generate-rest-points-bounds-test
  (testing "all points lie within [0, w] × [0, h]"
    (let [w 300.0 h 200.0
          result (model/generate-rest-points w h 16.0)
          ^js xs (aget result 0)
          ^js ys (aget result 1)]
      (dotimes [i model/point-count]
        (is (>= (aget xs i) 0.0))
        (is (<= (aget xs i) w))
        (is (>= (aget ys i) 0.0))
        (is (<= (aget ys i) h))))))

(deftest generate-rest-points-radius-clamped-test
  (testing "radius larger than half-min-dimension gets clamped"
    (let [result (model/generate-rest-points 40.0 30.0 100.0)
          ^js xs (aget result 0)]
      ;; radius should be clamped to 15 (half of min(40,30))
      (is (= 15.0 (aget xs 0))))))

;; ── points->path-d ──────────────────────────────────────────────────────────

(deftest points-to-path-d-format-test
  (let [result (model/generate-rest-points 200.0 100.0 12.0)
        ^js xs (aget result 0)
        ^js ys (aget result 1)
        d (model/points->path-d xs ys model/point-count)]
    (is (string? d))
    (is (.startsWith d "M"))
    (is (.includes d "C"))
    (is (.endsWith d "Z"))))

;; ── static-rounded-rect-d ───────────────────────────────────────────────────

(deftest static-rounded-rect-d-format-test
  (let [d (model/static-rounded-rect-d 200.0 100.0 12.0)]
    (is (string? d))
    (is (.startsWith d "M"))
    (is (.endsWith d "Z"))
    (is (.includes d "L"))
    (is (.includes d "C"))))

(deftest static-rounded-rect-d-min-radius-test
  (let [d (model/static-rounded-rect-d 200.0 100.0 0.0)]
    (is (string? d))
    (is (.startsWith d "M6.00,0"))))

;; ── spring-step ─────────────────────────────────────────────────────────────

(deftest spring-step-converges-test
  (testing "spring converges toward target after many steps"
    (let [target 100.0]
      (loop [pos 0.0
             vel 0.0
             i   0]
        (if (>= i 200)
          (is (< (js/Math.abs (- pos target)) 1.0))
          (let [result (model/spring-step pos target vel 0.016 180.0 12.0)]
            (recur (aget result 0) (aget result 1) (inc i))))))))

(deftest spring-step-at-rest-test
  (testing "spring at target with zero velocity stays put"
    (let [result (model/spring-step 50.0 50.0 0.0 0.016 180.0 12.0)]
      (is (< (js/Math.abs (- (aget result 0) 50.0)) 0.01))
      (is (< (js/Math.abs (aget result 1)) 0.01)))))

;; ── compute-pointer-offset ──────────────────────────────────────────────────

(deftest compute-pointer-offset-far-away-test
  (testing "pointer outside grab-radius returns zero offset"
    (let [result (model/compute-pointer-offset 50.0 50.0 500.0 500.0 80.0 1.0 false)]
      (is (= 0.0 (aget result 0)))
      (is (= 0.0 (aget result 1))))))

(deftest compute-pointer-offset-nearby-test
  (testing "pointer inside grab-radius returns nonzero offset"
    (let [result (model/compute-pointer-offset 50.0 50.0 80.0 50.0 80.0 1.0 false)]
      (is (> (aget result 0) 0.0))
      (is (< (js/Math.abs (aget result 1)) 0.01)))))

(deftest compute-pointer-offset-grabbed-stronger-test
  (testing "grabbed mode produces larger offset than hover"
    (let [hover  (model/compute-pointer-offset 50.0 50.0 80.0 50.0 80.0 1.0 false)
          grab   (model/compute-pointer-offset 50.0 50.0 80.0 50.0 80.0 1.0 true)]
      (is (> (aget grab 0) (aget hover 0))))))

(deftest compute-pointer-offset-intensity-scales-test
  (testing "higher intensity produces larger offset"
    (let [lo (model/compute-pointer-offset 50.0 50.0 80.0 50.0 80.0 1.0 false)
          hi (model/compute-pointer-offset 50.0 50.0 80.0 50.0 80.0 3.0 false)]
      (is (> (aget hi 0) (aget lo 0))))))
