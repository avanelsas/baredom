(ns baredom.components.x-organic-progress.model-test
  (:require
   [cljs.test :refer-macros [deftest is testing]]
   [baredom.components.x-organic-progress.model :as model]))

;; ── parse-progress ──────────────────────────────────────────────────────────

(deftest parse-progress-nil-test
  (is (nil? (model/parse-progress nil))))

(deftest parse-progress-empty-test
  (is (nil? (model/parse-progress ""))))

(deftest parse-progress-valid-test
  (is (= 50.0 (model/parse-progress "50")))
  (is (= 0.0 (model/parse-progress "0")))
  (is (= 100.0 (model/parse-progress "100"))))

(deftest parse-progress-clamps-test
  (is (= 0.0 (model/parse-progress "-10")))
  (is (= 100.0 (model/parse-progress "200"))))

(deftest parse-progress-invalid-test
  (is (nil? (model/parse-progress "abc"))))

(deftest parse-progress-float-test
  (is (= 33.5 (model/parse-progress "33.5"))))

;; ── parse-variant ───────────────────────────────────────────────────────────

(deftest parse-variant-valid-test
  (is (= "vine" (model/parse-variant "vine")))
  (is (= "honeycomb" (model/parse-variant "honeycomb"))))

(deftest parse-variant-case-insensitive-test
  (is (= "vine" (model/parse-variant "VINE")))
  (is (= "honeycomb" (model/parse-variant "Honeycomb"))))

(deftest parse-variant-default-test
  (is (= "vine" (model/parse-variant nil)))
  (is (= "vine" (model/parse-variant "unknown"))))

;; ── parse-bloom ─────────────────────────────────────────────────────────────

(deftest parse-bloom-absent-is-true-test
  (is (true? (model/parse-bloom nil))))

(deftest parse-bloom-false-test
  (is (false? (model/parse-bloom "false")))
  (is (false? (model/parse-bloom "FALSE"))))

(deftest parse-bloom-empty-is-true-test
  (is (true? (model/parse-bloom ""))))

;; ── parse-density ───────────────────────────────────────────────────────────

(deftest parse-density-valid-test
  (is (= "sparse" (model/parse-density "sparse")))
  (is (= "normal" (model/parse-density "normal")))
  (is (= "dense" (model/parse-density "dense"))))

(deftest parse-density-default-test
  (is (= "normal" (model/parse-density nil)))
  (is (= "normal" (model/parse-density "unknown"))))

;; ── parse-seed ──────────────────────────────────────────────────────────────

(deftest parse-seed-valid-test
  (is (= 7 (model/parse-seed "7")))
  (is (= 123 (model/parse-seed "123"))))

(deftest parse-seed-default-test
  (is (= 42 (model/parse-seed nil)))
  (is (= 42 (model/parse-seed "abc")))
  (is (= 42 (model/parse-seed "-5"))))

;; ── parse-label ─────────────────────────────────────────────────────────────

(deftest parse-label-valid-test
  (is (= "Uploading" (model/parse-label "Uploading"))))

(deftest parse-label-nil-test
  (is (nil? (model/parse-label nil)))
  (is (nil? (model/parse-label "")))
  (is (nil? (model/parse-label "   "))))

;; ── parse-color ─────────────────────────────────────────────────────────────

(deftest parse-color-valid-test
  (is (= "red" (model/parse-color "red")))
  (is (= "#ff0000" (model/parse-color "#ff0000"))))

(deftest parse-color-nil-test
  (is (nil? (model/parse-color nil)))
  (is (nil? (model/parse-color "")))
  (is (nil? (model/parse-color "   "))))

;; ── derive-state ────────────────────────────────────────────────────────────

(deftest derive-state-defaults-test
  (let [m (model/derive-state {})]
    (is (nil? (:progress m)))
    (is (true? (:indeterminate? m)))
    (is (false? (:complete? m)))
    (is (= "vine" (:variant m)))
    (is (true? (:bloom? m)))
    (is (= "normal" (:density m)))
    (is (= 42 (:seed m)))
    (is (= "Loading..." (:aria-valuetext m)))))

(deftest derive-state-determinate-test
  (let [m (model/derive-state {:progress-raw "75"})]
    (is (= 75.0 (:progress m)))
    (is (false? (:indeterminate? m)))
    (is (false? (:complete? m)))
    (is (= "75%" (:aria-valuetext m)))))

(deftest derive-state-complete-test
  (let [m (model/derive-state {:progress-raw "100"})]
    (is (true? (:complete? m)))
    (is (= "100%" (:aria-valuetext m)))))

;; ── Seeded PRNG ─────────────────────────────────────────────────────────────

(deftest rng-determinism-test
  (testing "same seed produces same sequence"
    (let [rng1 (model/make-rng 42)
          rng2 (model/make-rng 42)
          a1   (model/rng-next! rng1)
          a2   (model/rng-next! rng2)]
      (is (= a1 a2)))))

(deftest rng-different-seeds-test
  (testing "different seeds produce different values"
    (let [rng1 (model/make-rng 42)
          rng2 (model/make-rng 99)
          a1   (model/rng-next! rng1)
          a2   (model/rng-next! rng2)]
      (is (not= a1 a2)))))

(deftest rng-range-test
  (testing "values are in [0, 1)"
    (let [rng (model/make-rng 42)]
      (dotimes [_ 100]
        (let [v (model/rng-next! rng)]
          (is (>= v 0.0))
          (is (< v 1.0)))))))

;; ── Simplex noise ───────────────────────────────────────────────────────────

(deftest simplex-noise-determinism-test
  (is (= (model/simplex-noise-2d 1.0 2.0)
         (model/simplex-noise-2d 1.0 2.0))))

(deftest simplex-noise-range-test
  (testing "noise values within expected range"
    (let [v (model/simplex-noise-2d 0.5 0.5)]
      (is (>= v -1.5))
      (is (<= v 1.5)))))

(deftest simplex-noise-variation-test
  (testing "different inputs produce different outputs"
    (is (not= (model/simplex-noise-2d 0.3 0.7)
              (model/simplex-noise-2d 5.1 8.4)))))

;; ── L-system ────────────────────────────────────────────────────────────────

(deftest l-system-iterate-vine-1-test
  (is (= "F[+F]F[-F]F"
         (model/l-system-iterate "F" {"F" "F[+F]F[-F]F"} 1))))

(deftest l-system-iterate-0-test
  (is (= "F" (model/l-system-iterate "F" {"F" "F[+F]F[-F]F"} 0))))

(deftest l-system-segments-deterministic-test
  (testing "same inputs produce same segment count"
    (let [l-str   (model/l-system-iterate "F" {"F" "F[+F]F[-F]F"} 2)
          noise   (model/make-noise-fn 42)
          result1 (model/l-system->segments l-str noise 42 0.436 15.0 10.0 50.0)
          result2 (model/l-system->segments l-str noise 42 0.436 15.0 10.0 50.0)]
      (is (= (aget result1 1) (aget result2 1)))
      (is (pos? (aget result1 1))))))

;; ── Spring physics ──────────────────────────────────────────────────────────

(deftest spring-step-converges-test
  (testing "spring converges toward target after many steps"
    (let [target 100.0]
      (loop [pos 0.0
             vel 0.0
             i   0]
        (if (>= i 200)
          (is (< (js/Math.abs (- pos target)) 1.0))
          (let [result (model/spring-step pos target vel 0.016 120.0 12.0)]
            (recur (aget result 0) (aget result 1) (inc i))))))))

;; ── Easing ──────────────────────────────────────────────────────────────────

(deftest ease-out-cubic-endpoints-test
  (is (= 0.0 (model/ease-out-cubic 0.0)))
  (is (= 1.0 (model/ease-out-cubic 1.0))))

(deftest ease-out-cubic-monotonic-test
  (is (< (model/ease-out-cubic 0.25)
         (model/ease-out-cubic 0.5)
         (model/ease-out-cubic 0.75))))

;; ── Honeycomb lattice ──────────────────────────────────────────────────────

(deftest honeycomb-lattice-segments-test
  (testing "returns segments array with positive count"
    (let [result (model/honeycomb-lattice-segments 42 "normal" 800.0 200.0)
          ^js segs (aget result 0)
          cnt      (aget result 1)]
      (is (pos? cnt))
      (is (= cnt (.-length segs))))))

(deftest honeycomb-determinism-test
  (testing "same seed produces same segment count"
    (let [r1 (model/honeycomb-lattice-segments 42 "normal" 800.0 200.0)
          r2 (model/honeycomb-lattice-segments 42 "normal" 800.0 200.0)]
      (is (= (aget r1 1) (aget r2 1))))))

(deftest honeycomb-density-affects-count-test
  (testing "dense produces more segments than sparse"
    (let [sparse (aget (model/honeycomb-lattice-segments 42 "sparse" 800.0 200.0) 1)
          dense  (aget (model/honeycomb-lattice-segments 42 "dense" 800.0 200.0) 1)]
      (is (> dense sparse)))))
