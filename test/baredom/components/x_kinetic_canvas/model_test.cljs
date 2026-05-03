(ns baredom.components.x-kinetic-canvas.model-test
  (:require
   [cljs.test :refer-macros [deftest is]]
   [baredom.components.x-kinetic-canvas.model :as model]))

;; ── parse-type ──────────────────────────────────────────────────────────────

(deftest parse-type-defaults-test
  (is (= "starfield" (model/parse-type nil)))
  (is (= "starfield" (model/parse-type "")))
  (is (= "starfield" (model/parse-type "nonsense"))))

(deftest parse-type-valid-test
  (is (= "starfield" (model/parse-type "starfield")))
  (is (= "bubbles" (model/parse-type "bubbles")))
  (is (= "matrix" (model/parse-type "matrix")))
  (is (= "starfield" (model/parse-type "  Starfield  "))))

;; ── parse-variant ───────────────────────────────────────────────────────────

(deftest parse-variant-starfield-test
  (is (= "motion" (model/parse-variant nil "starfield")))
  (is (= "motion" (model/parse-variant "" "starfield")))
  (is (= "motion" (model/parse-variant "unknown" "starfield")))
  (is (= "motion" (model/parse-variant "motion" "starfield")))
  (is (= "twinkle" (model/parse-variant "twinkle" "starfield")))
  (is (= "twinkle" (model/parse-variant "  Twinkle  " "starfield"))))

(deftest parse-variant-other-types-test
  (is (nil? (model/parse-variant "motion" "bubbles")))
  (is (nil? (model/parse-variant "twinkle" "matrix")))
  (is (nil? (model/parse-variant nil "bubbles"))))

;; ── parse-speed ─────────────────────────────────────────────────────────────

(deftest parse-speed-defaults-test
  (is (= 1.0 (model/parse-speed nil)))
  (is (= 1.0 (model/parse-speed "")))
  (is (= 1.0 (model/parse-speed "abc"))))

(deftest parse-speed-named-test
  (is (= 0.3 (model/parse-speed "slow")))
  (is (= 1.0 (model/parse-speed "medium")))
  (is (= 2.5 (model/parse-speed "fast")))
  (is (= 0.3 (model/parse-speed "  SLOW  "))))

(deftest parse-speed-numeric-test
  (is (= 1.5 (model/parse-speed "1.5")))
  (is (= 0.1 (model/parse-speed "0.1")))
  (is (= 10.0 (model/parse-speed "20"))))  ;; clamped to 10

;; ── parse-density ───────────────────────────────────────────────────────────

(deftest parse-density-defaults-test
  (is (= 1.0 (model/parse-density nil)))
  (is (= 1.0 (model/parse-density "")))
  (is (= 1.0 (model/parse-density "xyz"))))

(deftest parse-density-named-test
  (is (= 0.5 (model/parse-density "low")))
  (is (= 1.0 (model/parse-density "medium")))
  (is (= 2.0 (model/parse-density "high")))
  (is (= 2.0 (model/parse-density "  HIGH  "))))

(deftest parse-density-numeric-test
  (is (= 1.5 (model/parse-density "1.5")))
  (is (= 5.0 (model/parse-density "10"))))  ;; clamped to 5

;; ── normalize ───────────────────────────────────────────────────────────────

(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= "starfield" (:type m)))
    (is (= "motion" (:variant m)))
    (is (= 1.0 (:speed m)))
    (is (= 1.0 (:density m)))
    (is (false? (:fullscreen? m)))
    (is (false? (:paused? m)))))

(deftest normalize-full-test
  (let [m (model/normalize
           {:type-raw        "bubbles"
            :variant-raw     "motion"
            :speed-raw       "fast"
            :density-raw     "high"
            :fullscreen-attr ""
            :paused-attr     ""})]
    (is (= "bubbles" (:type m)))
    (is (nil? (:variant m)))  ;; bubbles has no variants yet
    (is (= 2.5 (:speed m)))
    (is (= 2.0 (:density m)))
    (is (true? (:fullscreen? m)))
    (is (true? (:paused? m)))))

;; ── entity-count ────────────────────────────────────────────────────────────

(deftest entity-count-starfield-test
  (let [c (model/entity-count "starfield" 1.0 1000 500)]
    (is (>= c 50))
    (is (<= c 500))))

(deftest entity-count-bubbles-test
  (let [c (model/entity-count "bubbles" 1.0 1000 500)]
    (is (>= c 10))
    (is (<= c 100))))

(deftest entity-count-matrix-test
  (let [c (model/entity-count "matrix" 1.0 1000 500)]
    (is (>= c 5))
    (is (<= c 200))))

(deftest entity-count-density-scales-test
  (let [low  (model/entity-count "starfield" 0.5 1000 500)
        high (model/entity-count "starfield" 2.0 1000 500)]
    (is (< low high))))
