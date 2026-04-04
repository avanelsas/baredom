(ns baredom.components.x-organic-shape.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-organic-shape.model :as model]))

;; ── normalize-shape ──────────────────────────────────────────────────────
(deftest normalize-shape-valid-test
  (doseq [v ["blob-1" "blob-2" "blob-3" "pebble" "leaf" "droplet" "cloud" "wave"]]
    (is (= v (model/normalize-shape v)))))

(deftest normalize-shape-default-test
  (is (= "blob-1" (model/normalize-shape nil)))
  (is (= "blob-1" (model/normalize-shape "")))
  (is (= "blob-1" (model/normalize-shape "hexagon")))
  (is (= "blob-1" (model/normalize-shape 42))))

;; ── normalize-ratio ──────────────────────────────────────────────────────
(deftest normalize-ratio-valid-test
  (is (= "1/1"  (model/normalize-ratio "1/1")))
  (is (= "4/3"  (model/normalize-ratio "4/3")))
  (is (= "16/9" (model/normalize-ratio "16/9")))
  (is (= "auto" (model/normalize-ratio "auto"))))

(deftest normalize-ratio-default-test
  (is (= "1/1" (model/normalize-ratio nil)))
  (is (= "1/1" (model/normalize-ratio "")))
  (is (= "1/1" (model/normalize-ratio "   "))))

;; ── normalize-path ───────────────────────────────────────────────────────
(deftest normalize-path-valid-test
  (is (= "circle(50%)" (model/normalize-path "circle(50%)")))
  (is (= "polygon(50% 0%,100% 100%,0% 100%)" (model/normalize-path "polygon(50% 0%,100% 100%,0% 100%)"))))

(deftest normalize-path-nil-for-blanks-test
  (is (nil? (model/normalize-path nil)))
  (is (nil? (model/normalize-path "")))
  (is (nil? (model/normalize-path "   "))))

;; ── resolve-clip-path ────────────────────────────────────────────────────
(deftest resolve-clip-path-custom-path-overrides-shape-test
  (is (= "circle(50%)" (model/resolve-clip-path "circle(50%)" "blob-1"))))

(deftest resolve-clip-path-uses-preset-when-no-path-test
  (let [clip (model/resolve-clip-path nil "pebble")]
    (is (some? clip))
    (is (= (get-in model/shape-presets ["pebble" :clip]) clip))))

(deftest resolve-clip-path-defaults-to-blob-1-for-unknown-shape-test
  (let [clip (model/resolve-clip-path nil "hexagon")]
    (is (= (get-in model/shape-presets ["blob-1" :clip]) clip))))

;; ── resolve-clip-path-alt ────────────────────────────────────────────────
(deftest resolve-clip-path-alt-nil-when-custom-path-test
  (is (nil? (model/resolve-clip-path-alt "circle(50%)" "blob-1"))))

(deftest resolve-clip-path-alt-returns-morph-target-test
  (let [alt (model/resolve-clip-path-alt nil "leaf")]
    (is (some? alt))
    (is (= (get-in model/shape-presets ["leaf" :clip-alt]) alt))))

;; ── normalize-animation ───────────────────────────────────────────────
(deftest normalize-animation-valid-test
  (doseq [v ["none" "morph" "pulse" "float" "spin"]]
    (is (= v (model/normalize-animation v)))))

(deftest normalize-animation-default-test
  (is (= "none" (model/normalize-animation nil)))
  (is (= "none" (model/normalize-animation "")))
  (is (= "none" (model/normalize-animation "bounce")))
  (is (= "none" (model/normalize-animation 42))))

;; ── derive-state ─────────────────────────────────────────────────────────
(deftest derive-state-defaults-test
  (let [s (model/derive-state {})]
    (is (= "blob-1" (:shape s)))
    (is (nil?       (:path s)))
    (is (some?      (:clip s)))
    (is (some?      (:clip-alt s)))
    (is (= "none"   (:animation s)))
    (is (= "1/1"    (:ratio s)))))

(deftest derive-state-with-animation-test
  (doseq [anim ["morph" "pulse" "float" "spin"]]
    (let [s (model/derive-state {:animation anim :shape "leaf"})]
      (is (= anim (:animation s))))))

(deftest derive-state-morph-disabled-for-custom-path-test
  (let [s (model/derive-state {:animation "morph" :path "circle(50%)"})]
    (is (= "none" (:animation s)))
    (is (nil?     (:clip-alt s)))))

(deftest derive-state-non-morph-animations-work-with-custom-path-test
  (doseq [anim ["pulse" "float" "spin"]]
    (let [s (model/derive-state {:animation anim :path "circle(50%)"})]
      (is (= anim (:animation s))))))

(deftest derive-state-explicit-values-test
  (let [s (model/derive-state {:shape "cloud" :ratio "16/9"})]
    (is (= "cloud" (:shape s)))
    (is (= "16/9"  (:ratio s)))))

(deftest derive-state-invalid-shape-falls-back-test
  (let [s (model/derive-state {:shape "pentagon"})]
    (is (= "blob-1" (:shape s)))))

;; ── normalize-css-value ───────────────────────────────────────────────────
(deftest normalize-css-value-valid-test
  (is (= "100px"  (model/normalize-css-value "100px")))
  (is (= "50%"    (model/normalize-css-value "50%")))
  (is (= "2.5rem" (model/normalize-css-value "2.5rem"))))

(deftest normalize-css-value-nil-for-blanks-test
  (is (nil? (model/normalize-css-value nil)))
  (is (nil? (model/normalize-css-value "")))
  (is (nil? (model/normalize-css-value 0))))

;; ── derive-state with width/height ───────────────────────────────────────
(deftest derive-state-width-height-test
  (let [s (model/derive-state {:width "200px" :height "150px"})]
    (is (= "200px" (:width s)))
    (is (= "150px" (:height s)))))

(deftest derive-state-empty-width-height-become-nil-test
  (let [s (model/derive-state {:width "" :height ""})]
    (is (nil? (:width s)))
    (is (nil? (:height s)))))

(deftest derive-state-defaults-have-nil-width-height-test
  (let [s (model/derive-state {})]
    (is (nil? (:width s)))
    (is (nil? (:height s)))))

;; ── shape-presets completeness ───────────────────────────────────────────
(deftest all-presets-have-clip-and-clip-alt-test
  (doseq [[k v] model/shape-presets]
    (testing k
      (is (string? (:clip v)))
      (is (string? (:clip-alt v))))))
