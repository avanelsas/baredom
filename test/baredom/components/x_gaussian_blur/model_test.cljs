(ns baredom.components.x-gaussian-blur.model-test
  (:require [cljs.test :refer [deftest testing is]]
            [baredom.components.x-gaussian-blur.model :as model]))

;; ── parse-colors ─────────────────────────────────────────────────────────

(deftest parse-colors-test
  (testing "splits comma-separated colors"
    (is (= ["#ff0000" "#00ff00" "#0000ff"]
           (model/parse-colors "#ff0000, #00ff00, #0000ff"))))

  (testing "trims whitespace"
    (is (= ["red" "green"] (model/parse-colors "  red , green  "))))

  (testing "filters empty segments"
    (is (= ["red" "blue"] (model/parse-colors "red,,blue,"))))

  (testing "nil returns defaults"
    (is (= 4 (count (model/parse-colors nil)))))

  (testing "empty string returns defaults"
    (is (= 4 (count (model/parse-colors ""))))))

;; ── parse-blur ───────────────────────────────────────────────────────────

(deftest parse-blur-test
  (testing "valid number"
    (is (= 40 (model/parse-blur "40"))))

  (testing "float"
    (is (= 25.5 (model/parse-blur "25.5"))))

  (testing "zero returns default"
    (is (= model/default-blur (model/parse-blur "0"))))

  (testing "negative returns default"
    (is (= model/default-blur (model/parse-blur "-10"))))

  (testing "non-numeric returns default"
    (is (= model/default-blur (model/parse-blur "abc"))))

  (testing "nil returns default"
    (is (= model/default-blur (model/parse-blur nil)))))

;; ── parse-speed ──────────────────────────────────────────────────────────

(deftest parse-speed-test
  (testing "preset slow"
    (is (= 18 (model/parse-speed "slow"))))

  (testing "preset medium"
    (is (= 10 (model/parse-speed "medium"))))

  (testing "preset fast"
    (is (= 5 (model/parse-speed "fast"))))

  (testing "raw number"
    (is (= 25 (model/parse-speed "25"))))

  (testing "nil returns default"
    (is (= model/default-speed-s (model/parse-speed nil))))

  (testing "invalid returns default"
    (is (= model/default-speed-s (model/parse-speed "xyz")))))

;; ── parse-count ──────────────────────────────────────────────────────────

(deftest parse-count-test
  (testing "valid count"
    (is (= 6 (model/parse-count "6"))))

  (testing "clamps to min 1"
    (is (= 1 (model/parse-count "0"))))

  (testing "clamps to max 12"
    (is (= 12 (model/parse-count "20"))))

  (testing "nil returns default"
    (is (= model/default-count (model/parse-count nil))))

  (testing "non-numeric returns default"
    (is (= model/default-count (model/parse-count "abc")))))

;; ── parse-size ───────────────────────────────────────────────────────────

(deftest parse-size-test
  (testing "preset small"
    (is (= 30 (model/parse-size "small"))))

  (testing "preset medium"
    (is (= 50 (model/parse-size "medium"))))

  (testing "preset large"
    (is (= 70 (model/parse-size "large"))))

  (testing "raw percentage"
    (is (= 60 (model/parse-size "60"))))

  (testing "clamps to min 10"
    (is (= 10 (model/parse-size "5"))))

  (testing "clamps to max 100"
    (is (= 100 (model/parse-size "150"))))

  (testing "nil returns default"
    (is (= model/default-size-pct (model/parse-size nil)))))

;; ── parse-opacity ────────────────────────────────────────────────────────

(deftest parse-opacity-test
  (testing "valid"
    (is (= 0.5 (model/parse-opacity "0.5"))))

  (testing "clamps to 0"
    (is (= 0.0 (model/parse-opacity "-1"))))

  (testing "clamps to 1"
    (is (= 1.0 (model/parse-opacity "2"))))

  (testing "nil returns default"
    (is (= model/default-opacity (model/parse-opacity nil)))))

;; ── normalize-animation ──────────────────────────────────────────────────

(deftest normalize-animation-test
  (testing "valid values"
    (is (= "float" (model/normalize-animation "float")))
    (is (= "pulse" (model/normalize-animation "pulse")))
    (is (= "none"  (model/normalize-animation "none"))))

  (testing "invalid returns default"
    (is (= model/default-animation (model/normalize-animation "bounce")))
    (is (= model/default-animation (model/normalize-animation nil)))))

;; ── normalize-blend ──────────────────────────────────────────────────────

(deftest normalize-blend-test
  (testing "valid values"
    (is (= "multiply"  (model/normalize-blend "multiply")))
    (is (= "screen"    (model/normalize-blend "screen")))
    (is (= "soft-light" (model/normalize-blend "soft-light"))))

  (testing "invalid returns default"
    (is (= model/default-blend (model/normalize-blend "difference")))
    (is (= model/default-blend (model/normalize-blend nil)))))

;; ── parse-paused ─────────────────────────────────────────────────────────

(deftest parse-paused-test
  (testing "nil = false (absent)"
    (is (false? (model/parse-paused nil))))

  (testing "empty string = true (present)"
    (is (true? (model/parse-paused ""))))

  (testing "any string = true"
    (is (true? (model/parse-paused "paused")))))

;; ── blob-layout ──────────────────────────────────────────────────────────

(deftest blob-layout-test
  (testing "produces correct count"
    (is (= 3 (count (model/blob-layout ["red" "green" "blue"] 3 50)))))

  (testing "colors cycle when count > colors"
    (let [blobs (model/blob-layout ["red" "green"] 4 50)]
      (is (= "red"   (:color (nth blobs 0))))
      (is (= "green" (:color (nth blobs 1))))
      (is (= "red"   (:color (nth blobs 2))))
      (is (= "green" (:color (nth blobs 3))))))

  (testing "deterministic — same inputs produce same output"
    (let [a (model/blob-layout ["#ff0000" "#00ff00"] 5 50)
          b (model/blob-layout ["#ff0000" "#00ff00"] 5 50)]
      (is (= a b))))

  (testing "each blob has required keys"
    (let [blob (first (model/blob-layout ["red"] 1 50))]
      (is (contains? blob :color))
      (is (contains? blob :x))
      (is (contains? blob :y))
      (is (contains? blob :size))
      (is (contains? blob :anim-index))
      (is (contains? blob :duration-factor))
      (is (contains? blob :delay-factor))))

  (testing "anim-index cycles 0-3"
    (let [blobs (model/blob-layout ["red"] 8 50)]
      (is (= [0 1 2 3 0 1 2 3] (mapv :anim-index blobs))))))

;; ── derive-state ─────────────────────────────────────────────────────────

(deftest derive-state-test
  (testing "defaults with all nil inputs"
    (let [s (model/derive-state {})]
      (is (= 4 (count (:colors s))))
      (is (= model/default-blur (:blur s)))
      (is (= model/default-speed-s (:speed-s s)))
      (is (= model/default-count (:count s)))
      (is (= model/default-size-pct (:size-pct s)))
      (is (= model/default-opacity (:opacity s)))
      (is (= model/default-animation (:animation s)))
      (is (= model/default-blend (:blend s)))
      (is (false? (:paused s)))
      (is (= model/default-count (count (:blobs s))))))

  (testing "custom values"
    (let [s (model/derive-state {:colors "red, blue"
                                 :blur "40"
                                 :speed "fast"
                                 :count "3"
                                 :size "large"
                                 :opacity "0.5"
                                 :animation "pulse"
                                 :blend "multiply"
                                 :paused ""})]
      (is (= ["red" "blue"] (:colors s)))
      (is (= 40 (:blur s)))
      (is (= 5 (:speed-s s)))
      (is (= 3 (:count s)))
      (is (= 70 (:size-pct s)))
      (is (= 0.5 (:opacity s)))
      (is (= "pulse" (:animation s)))
      (is (= "multiply" (:blend s)))
      (is (true? (:paused s)))
      (is (= 3 (count (:blobs s)))))))
