(ns baredom.components.x-metaball-cursor.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-metaball-cursor.model :as model]
            [baredom.utils.model :as mu]))

;; ── parse-blob-count ────────────────────────────────────────────────────────
(deftest parse-blob-count-valid-test
  (is (= 5 (model/parse-blob-count "5")))
  (is (= 2 (model/parse-blob-count "2")))
  (is (= 10 (model/parse-blob-count "10"))))

(deftest parse-blob-count-default-test
  (is (= 5 (model/parse-blob-count nil)))
  (is (= 5 (model/parse-blob-count "")))
  (is (= 5 (model/parse-blob-count "abc")))
  (is (= 5 (model/parse-blob-count "-3"))))

(deftest parse-blob-count-clamping-test
  (is (= 2  (model/parse-blob-count "1")))
  (is (= 10 (model/parse-blob-count "20"))))

;; ── parse-blob-size ─────────────────────────────────────────────────────────
(deftest parse-blob-size-valid-test
  (is (= 40  (model/parse-blob-size "40")))
  (is (= 100 (model/parse-blob-size "100")))
  (is (= 10  (model/parse-blob-size "10"))))

(deftest parse-blob-size-default-test
  (is (= 40 (model/parse-blob-size nil)))
  (is (= 40 (model/parse-blob-size "")))
  (is (= 40 (model/parse-blob-size "abc")))
  (is (= 40 (model/parse-blob-size "-5"))))

(deftest parse-blob-size-clamping-test
  (is (= 10  (model/parse-blob-size "5")))
  (is (= 200 (model/parse-blob-size "300"))))

;; ── parse-color ─────────────────────────────────────────────────────────────
(deftest parse-color-valid-test
  (is (= "#ff0000" (model/parse-color "#ff0000")))
  (is (= "red" (model/parse-color "red")))
  (is (= "rgba(0,0,0,0.5)" (model/parse-color "rgba(0,0,0,0.5)"))))

(deftest parse-color-default-test
  (is (= "#6366f1" (model/parse-color nil)))
  (is (= "#6366f1" (model/parse-color "")))
  (is (= "#6366f1" (model/parse-color "   "))))

;; ── parse-bool-attr ─────────────────────────────────────────────────────────
(deftest parse-bool-attr-test
  (is (false? (mu/parse-bool-present nil)))
  (is (true? (mu/parse-bool-present "")))
  (is (true? (mu/parse-bool-present "true")))
  (is (true? (mu/parse-bool-present "anything"))))

;; ── parse-noise-scale ───────────────────────────────────────────────────────
(deftest parse-noise-scale-valid-test
  (is (= 3 (model/parse-noise-scale "3")))
  (is (= 10 (model/parse-noise-scale "10"))))

(deftest parse-noise-scale-default-test
  (is (= 3 (model/parse-noise-scale nil)))
  (is (= 3 (model/parse-noise-scale "")))
  (is (= 3 (model/parse-noise-scale "abc"))))

(deftest parse-noise-scale-clamping-test
  (is (= 0.5 (model/parse-noise-scale "0.1")))
  (is (= 20  (model/parse-noise-scale "50"))))

;; ── parse-noise-speed ───────────────────────────────────────────────────────
(deftest parse-noise-speed-valid-test
  (is (= 0.02 (model/parse-noise-speed "0.02")))
  (is (= 0.05 (model/parse-noise-speed "0.05"))))

(deftest parse-noise-speed-default-test
  (is (= 0.02 (model/parse-noise-speed nil)))
  (is (= 0.02 (model/parse-noise-speed "")))
  (is (= 0.02 (model/parse-noise-speed "abc"))))

(deftest parse-noise-speed-clamping-test
  (is (= 0.001 (model/parse-noise-speed "0.0001")))
  (is (= 0.1   (model/parse-noise-speed "0.5"))))

;; ── parse-noise-intensity ───────────────────────────────────────────────────
(deftest parse-noise-intensity-valid-test
  (is (= 6  (model/parse-noise-intensity "6")))
  (is (= 15 (model/parse-noise-intensity "15"))))

(deftest parse-noise-intensity-default-test
  (is (= 6 (model/parse-noise-intensity nil)))
  (is (= 6 (model/parse-noise-intensity "")))
  (is (= 6 (model/parse-noise-intensity "abc"))))

(deftest parse-noise-intensity-clamping-test
  (is (= 1  (model/parse-noise-intensity "0.5")))
  (is (= 30 (model/parse-noise-intensity "50"))))

;; ── parse-blur ──────────────────────────────────────────────────────────────
(deftest parse-blur-valid-test
  (is (= 12 (model/parse-blur "12")))
  (is (= 20 (model/parse-blur "20"))))

(deftest parse-blur-default-test
  (is (= 12 (model/parse-blur nil)))
  (is (= 12 (model/parse-blur "")))
  (is (= 12 (model/parse-blur "abc"))))

(deftest parse-blur-clamping-test
  (is (= 4  (model/parse-blur "2")))
  (is (= 40 (model/parse-blur "50"))))

;; ── parse-threshold ─────────────────────────────────────────────────────────
(deftest parse-threshold-valid-test
  (is (= "1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 30 -15"
         (model/parse-threshold "1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 30 -15"))))

(deftest parse-threshold-default-test
  (is (= "1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 20 -10"
         (model/parse-threshold nil)))
  (is (= "1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 20 -10"
         (model/parse-threshold ""))))

;; ── whitespace handling ─────────────────────────────────────────────────────
(deftest whitespace-trimmed-test
  (is (= 5  (model/parse-blob-count " 5 ")))
  (is (= 60 (model/parse-blob-size  " 60 ")))
  (is (= 8  (model/parse-blur       " 8 "))))

;; ── normalize ───────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= 5       (:blob-count m)))
    (is (= 40      (:blob-size m)))
    (is (= "#6366f1" (:color m)))
    (is (false?    (:noise? m)))
    (is (= 3       (:noise-scale m)))
    (is (= 0.02    (:noise-speed m)))
    (is (= 6       (:noise-intensity m)))
    (is (= 12      (:blur m)))))

(deftest normalize-custom-values-test
  (let [m (model/normalize {:blob-count-raw "8"
                            :blob-size-raw  "60"
                            :color-raw      "hotpink"
                            :noise-attr     ""
                            :noise-scale-raw "5"
                            :noise-speed-raw "0.05"
                            :noise-intensity-raw "10"
                            :blur-raw "20"
                            :threshold-raw "1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 30 -15"})]
    (is (= 8        (:blob-count m)))
    (is (= 60       (:blob-size m)))
    (is (= "hotpink" (:color m)))
    (is (true?      (:noise? m)))
    (is (= 5        (:noise-scale m)))
    (is (= 0.05     (:noise-speed m)))
    (is (= 10       (:noise-intensity m)))
    (is (= 20       (:blur m)))))

;; ── parse-palette ───────────────────────────────────────────────────────────
(deftest parse-palette-preset-test
  (is (= 5 (count (model/parse-palette "rainbow"))))
  (is (= 5 (count (model/parse-palette "ocean"))))
  (is (= 5 (count (model/parse-palette "sunset"))))
  (is (= 5 (count (model/parse-palette "neon"))))
  (is (= 5 (count (model/parse-palette "ember")))))

(deftest parse-palette-case-insensitive-test
  (is (= (model/parse-palette "rainbow")
         (model/parse-palette "Rainbow")))
  (is (= (model/parse-palette "ocean")
         (model/parse-palette "OCEAN"))))

(deftest parse-palette-custom-colors-test
  (is (= ["red" "blue" "green"] (model/parse-palette "red, blue, green")))
  (is (= ["#ff0000" "#00ff00"] (model/parse-palette "#ff0000, #00ff00")))
  (is (= ["hotpink" "cyan"] (model/parse-palette " hotpink , cyan "))))

(deftest parse-palette-nil-test
  (is (nil? (model/parse-palette nil)))
  (is (nil? (model/parse-palette "")))
  (is (nil? (model/parse-palette "   "))))

(deftest parse-palette-unknown-name-test
  (is (nil? (model/parse-palette "unknown"))))

;; ── blob-colors ─────────────────────────────────────────────────────────────
(deftest blob-colors-uniform-test
  (let [colors (model/blob-colors nil "#6366f1" 3)]
    (is (= 3 (.-length colors)))
    (is (= "#6366f1" (aget colors 0)))
    (is (= "#6366f1" (aget colors 1)))
    (is (= "#6366f1" (aget colors 2)))))

(deftest blob-colors-palette-exact-test
  (let [colors (model/blob-colors ["red" "blue" "green"] "#000" 3)]
    (is (= "red"   (aget colors 0)))
    (is (= "blue"  (aget colors 1)))
    (is (= "green" (aget colors 2)))))

(deftest blob-colors-palette-cycles-test
  (let [colors (model/blob-colors ["red" "blue"] "#000" 4)]
    (is (= 4 (.-length colors)))
    (is (= "red"  (aget colors 0)))
    (is (= "blue" (aget colors 1)))
    (is (= "red"  (aget colors 2)))
    (is (= "blue" (aget colors 3)))))

;; ── normalize with palette ──────────────────────────────────────────────────
(deftest normalize-palette-test
  (let [m (model/normalize {:palette-raw "ocean" :blob-count-raw "3"})]
    (is (some? (:palette m)))
    (is (= 3 (.-length (:colors m))))
    (is (not= (aget (:colors m) 0) (aget (:colors m) 1)))))

(deftest normalize-no-palette-uniform-test
  (let [m (model/normalize {:color-raw "hotpink" :blob-count-raw "3"})]
    (is (nil? (:palette m)))
    (is (= 3 (.-length (:colors m))))
    (is (= "hotpink" (aget (:colors m) 0)))
    (is (= "hotpink" (aget (:colors m) 1)))))

;; ── blob-speeds ─────────────────────────────────────────────────────────────
(deftest blob-speeds-count-test
  (is (= 5 (.-length (model/blob-speeds 5))))
  (is (= 3 (.-length (model/blob-speeds 3)))))

(deftest blob-speeds-lead-fastest-test
  (let [speeds (model/blob-speeds 5)]
    (is (> (aget speeds 0) (aget speeds 4)))
    (dotimes [i 5]
      (is (pos? (aget speeds i))))))

;; ── blob-sizes ──────────────────────────────────────────────────────────────
(deftest blob-sizes-count-test
  (is (= 5 (.-length (model/blob-sizes 40 5))))
  (is (= 3 (.-length (model/blob-sizes 40 3)))))

(deftest blob-sizes-lead-largest-test
  (let [sizes (model/blob-sizes 40 5)]
    (is (= 40 (aget sizes 0)))
    (is (> (aget sizes 0) (aget sizes 4)))))

(deftest blob-sizes-minimum-test
  (let [sizes (model/blob-sizes 40 10)]
    (dotimes [i 10]
      (is (>= (aget sizes i) (* 40 0.5))))))

;; ── lerp ────────────────────────────────────────────────────────────────────
(deftest lerp-midpoint-test
  (is (= 5.0 (model/lerp 0 10 0.5))))

(deftest lerp-at-target-test
  (is (= 10.0 (model/lerp 10 10 0.3))))

(deftest lerp-zero-speed-test
  (is (= 5.0 (model/lerp 5 10 0))))

(deftest lerp-full-speed-test
  (is (= 10.0 (model/lerp 5 10 1.0))))
