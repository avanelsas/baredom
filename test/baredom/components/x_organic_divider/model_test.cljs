(ns baredom.components.x-organic-divider.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-organic-divider.model :as model]))

;; ── normalize-shape ──────────────────────────────────────────────────────
(deftest normalize-shape-valid-test
  (doseq [v ["wave" "waves" "blob-edge" "mountain" "drip" "slant" "scallop" "cloud"]]
    (is (= v (model/normalize-shape v)))))

(deftest normalize-shape-default-test
  (is (= "wave" (model/normalize-shape nil)))
  (is (= "wave" (model/normalize-shape "")))
  (is (= "wave" (model/normalize-shape "zigzag")))
  (is (= "wave" (model/normalize-shape 42))))

;; ── normalize-layers ─────────────────────────────────────────────────────
(deftest normalize-layers-valid-test
  (is (= 1 (model/normalize-layers "1")))
  (is (= 3 (model/normalize-layers "3")))
  (is (= 5 (model/normalize-layers "5"))))

(deftest normalize-layers-clamps-test
  (is (= 1 (model/normalize-layers "0")))
  (is (= 1 (model/normalize-layers "-1")))
  (is (= 5 (model/normalize-layers "10")))
  (is (= 5 (model/normalize-layers "99"))))

(deftest normalize-layers-default-test
  (is (= 1 (model/normalize-layers nil)))
  (is (= 1 (model/normalize-layers "")))
  (is (= 1 (model/normalize-layers "abc"))))

;; ── normalize-animation ──────────────────────────────────────────────────
(deftest normalize-animation-valid-test
  (doseq [v ["none" "drift" "morph"]]
    (is (= v (model/normalize-animation v)))))

(deftest normalize-animation-default-test
  (is (= "none" (model/normalize-animation nil)))
  (is (= "none" (model/normalize-animation "")))
  (is (= "none" (model/normalize-animation "spin")))
  (is (= "none" (model/normalize-animation 42))))

;; ── normalize-height ─────────────────────────────────────────────────────
(deftest normalize-height-valid-test
  (is (= "120px"  (model/normalize-height "120px")))
  (is (= "10vh"   (model/normalize-height "10vh")))
  (is (= "5rem"   (model/normalize-height "5rem"))))

(deftest normalize-height-default-test
  (is (= "120px" (model/normalize-height nil)))
  (is (= "120px" (model/normalize-height "")))
  (is (= "120px" (model/normalize-height "   "))))

;; ── normalize-path ───────────────────────────────────────────────────────
(deftest normalize-path-valid-test
  (is (= "M0,0 L100,0 L100,100 Z" (model/normalize-path "M0,0 L100,0 L100,100 Z"))))

(deftest normalize-path-nil-for-blanks-test
  (is (nil? (model/normalize-path nil)))
  (is (nil? (model/normalize-path "")))
  (is (nil? (model/normalize-path "   "))))

;; ── normalize-boolean ────────────────────────────────────────────────────
(deftest normalize-boolean-test
  (is (true?  (model/normalize-boolean "")))
  (is (true?  (model/normalize-boolean "flip")))
  (is (false? (model/normalize-boolean nil))))

;; ── resolve-path ─────────────────────────────────────────────────────────
(deftest resolve-path-custom-overrides-shape-test
  (is (= "M0,0 L100,100" (model/resolve-path "M0,0 L100,100" "wave"))))

(deftest resolve-path-uses-preset-test
  (let [p (model/resolve-path nil "mountain")]
    (is (some? p))
    (is (= (get-in model/shape-presets ["mountain" :path]) p))))

(deftest resolve-path-defaults-to-wave-for-unknown-test
  (let [p (model/resolve-path nil "zigzag")]
    (is (= (get-in model/shape-presets ["wave" :path]) p))))

;; ── resolve-path-alt ─────────────────────────────────────────────────────
(deftest resolve-path-alt-nil-when-custom-path-test
  (is (nil? (model/resolve-path-alt "M0,0 L100,100" "wave"))))

(deftest resolve-path-alt-returns-morph-target-test
  (let [alt (model/resolve-path-alt nil "scallop")]
    (is (some? alt))
    (is (= (get-in model/shape-presets ["scallop" :path-alt]) alt))))

;; ── derive-layer-transforms ──────────────────────────────────────────────
(deftest derive-layer-transforms-single-test
  (is (= [{:x-offset 0 :y-offset 0}]
         (model/derive-layer-transforms 1))))

(deftest derive-layer-transforms-multiple-test
  (let [ts (model/derive-layer-transforms 3)]
    (is (= 3 (count ts)))
    (is (= {:x-offset 0  :y-offset 0}  (nth ts 0)))
    (is (= {:x-offset 20 :y-offset 8}  (nth ts 1)))
    (is (= {:x-offset 40 :y-offset 16} (nth ts 2)))))

;; ── derive-state ─────────────────────────────────────────────────────────
(deftest derive-state-defaults-test
  (let [s (model/derive-state {})]
    (is (= "wave"  (:shape s)))
    (is (nil?      (:path s)))
    (is (some?     (:path-d s)))
    (is (some?     (:path-alt s)))
    (is (= "none"  (:animation s)))
    (is (= 1       (:layers s)))
    (is (= "120px" (:height s)))
    (is (false?    (:flip s)))
    (is (false?    (:mirror s)))))

(deftest derive-state-morph-disabled-for-custom-path-test
  (let [s (model/derive-state {:animation "morph" :path "M0,0 L100,100"})]
    (is (= "none" (:animation s)))
    (is (nil?     (:path-alt s)))))

(deftest derive-state-drift-works-with-custom-path-test
  (let [s (model/derive-state {:animation "drift" :path "M0,0 L100,100"})]
    (is (= "drift" (:animation s)))))

(deftest derive-state-explicit-values-test
  (let [s (model/derive-state {:shape "cloud" :layers "3" :height "80px"})]
    (is (= "cloud" (:shape s)))
    (is (= 3       (:layers s)))
    (is (= "80px"  (:height s)))))

(deftest derive-state-flip-mirror-test
  (let [s (model/derive-state {:flip "" :mirror ""})]
    (is (true? (:flip s)))
    (is (true? (:mirror s)))))

(deftest derive-state-invalid-shape-falls-back-test
  (let [s (model/derive-state {:shape "zigzag"})]
    (is (= "wave" (:shape s)))))

;; ── shape-presets completeness ───────────────────────────────────────────
(deftest all-presets-have-path-and-path-alt-test
  (doseq [[k v] model/shape-presets]
    (testing k
      (is (string? (:path v)))
      (is (string? (:path-alt v))))))

(deftest all-presets-have-drift-path-test
  (doseq [k (keys model/shape-presets)]
    (testing k
      (is (string? (get model/drift-presets k))))))
