(ns baredom.components.x-kinetic-typography.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-kinetic-typography.model :as model]))

;; ── normalize-preset ─────────────────────────────────────────────────────
(deftest normalize-preset-valid-test
  (doseq [p ["wave" "circle" "arc" "infinity" "spiral" "sine" "line" "crawl"]]
    (is (= p (model/normalize-preset p)))))

(deftest normalize-preset-case-insensitive-test
  (is (= "wave" (model/normalize-preset "WAVE")))
  (is (= "circle" (model/normalize-preset "Circle")))
  (is (= "crawl" (model/normalize-preset "CRAWL"))))

(deftest normalize-preset-fallback-test
  (is (= "wave" (model/normalize-preset nil)))
  (is (= "wave" (model/normalize-preset "")))
  (is (= "wave" (model/normalize-preset "zigzag"))))

;; ── normalize-animation ──────────────────────────────────────────────────
(deftest normalize-animation-valid-test
  (doseq [a ["none" "scroll" "bounce" "oscillate"]]
    (is (= a (model/normalize-animation a)))))

(deftest normalize-animation-case-insensitive-test
  (is (= "scroll" (model/normalize-animation "SCROLL")))
  (is (= "bounce" (model/normalize-animation "Bounce"))))

(deftest normalize-animation-fallback-test
  (is (= "scroll" (model/normalize-animation nil)))
  (is (= "scroll" (model/normalize-animation "")))
  (is (= "scroll" (model/normalize-animation "spin"))))

;; ── normalize-direction ──────────────────────────────────────────────────
(deftest normalize-direction-valid-test
  (is (= "normal" (model/normalize-direction "normal")))
  (is (= "reverse" (model/normalize-direction "reverse"))))

(deftest normalize-direction-fallback-test
  (is (= "normal" (model/normalize-direction nil)))
  (is (= "normal" (model/normalize-direction "backwards"))))

;; ── parse-speed ──────────────────────────────────────────────────────────
(deftest parse-speed-valid-test
  (is (= 2.0 (model/parse-speed "2")))
  (is (= 0.5 (model/parse-speed "0.5")))
  (is (= 1.0 (model/parse-speed "1"))))

(deftest parse-speed-invalid-test
  (is (= 1.0 (model/parse-speed nil)))
  (is (= 1.0 (model/parse-speed "")))
  (is (= 1.0 (model/parse-speed "abc")))
  (is (= 1.0 (model/parse-speed "0")))
  (is (= 1.0 (model/parse-speed "-1"))))

;; ── parse-repeat ─────────────────────────────────────────────────────────
(deftest parse-repeat-valid-test
  (is (= 3 (model/parse-repeat "3")))
  (is (= 1 (model/parse-repeat "1"))))

(deftest parse-repeat-invalid-test
  (is (= 1 (model/parse-repeat nil)))
  (is (= 1 (model/parse-repeat "")))
  (is (= 1 (model/parse-repeat "0")))
  (is (= 1 (model/parse-repeat "-2")))
  (is (= 1 (model/parse-repeat "abc"))))

(deftest parse-repeat-truncates-float-test
  (is (= 2 (model/parse-repeat "2.7"))))

;; ── parse-echo-count ─────────────────────────────────────────────────────
(deftest parse-echo-count-valid-test
  (is (= 0 (model/parse-echo-count "0")))
  (is (= 3 (model/parse-echo-count "3")))
  (is (= 5 (model/parse-echo-count "5"))))

(deftest parse-echo-count-invalid-test
  (is (= 0 (model/parse-echo-count nil)))
  (is (= 0 (model/parse-echo-count "")))
  (is (= 0 (model/parse-echo-count "-1")))
  (is (= 0 (model/parse-echo-count "6")))
  (is (= 0 (model/parse-echo-count "abc"))))

;; ── parse-echo-delay ─────────────────────────────────────────────────────
(deftest parse-echo-delay-valid-test
  (is (= 0.5 (model/parse-echo-delay "0.5")))
  (is (= 1.0 (model/parse-echo-delay "1"))))

(deftest parse-echo-delay-invalid-test
  (is (= 0.3 (model/parse-echo-delay nil)))
  (is (= 0.3 (model/parse-echo-delay "0")))
  (is (= 0.3 (model/parse-echo-delay "-1"))))

;; ── parse-echo-opacity ───────────────────────────────────────────────────
(deftest parse-echo-opacity-valid-test
  (is (= 0.5 (model/parse-echo-opacity "0.5")))
  (is (= 1.0 (model/parse-echo-opacity "1"))))

(deftest parse-echo-opacity-invalid-test
  (is (= 0.5 (model/parse-echo-opacity nil)))
  (is (= 0.5 (model/parse-echo-opacity "0")))
  (is (= 0.5 (model/parse-echo-opacity "1.5")))
  (is (= 0.5 (model/parse-echo-opacity "-0.5"))))

;; ── parse-echo-scale ─────────────────────────────────────────────────────
(deftest parse-echo-scale-valid-test
  (is (= 0.85 (model/parse-echo-scale "0.85")))
  (is (= 0.5 (model/parse-echo-scale "0.5"))))

(deftest parse-echo-scale-invalid-test
  (is (= 0.85 (model/parse-echo-scale nil)))
  (is (= 0.85 (model/parse-echo-scale "0")))
  (is (= 0.85 (model/parse-echo-scale "1.5"))))

;; ── parse-effects ────────────────────────────────────────────────────────
(deftest parse-effects-single-test
  (is (= #{"opacity-wave"} (model/parse-effects "opacity-wave"))))

(deftest parse-effects-multiple-test
  (is (= #{"opacity-wave" "color-shift"}
         (model/parse-effects "opacity-wave color-shift"))))

(deftest parse-effects-filters-invalid-test
  (is (= #{"color-shift"}
         (model/parse-effects "color-shift invalid-effect"))))

(deftest parse-effects-empty-test
  (is (= #{} (model/parse-effects nil)))
  (is (= #{} (model/parse-effects "")))
  (is (= #{} (model/parse-effects "none"))))

(deftest parse-effects-case-insensitive-test
  (is (= #{"opacity-wave"} (model/parse-effects "OPACITY-WAVE"))))

(deftest parse-effects-color-wave-test
  (is (= #{"color-wave"} (model/parse-effects "color-wave"))))

;; ── resolve-path ─────────────────────────────────────────────────────────
(deftest resolve-path-preset-test
  (let [result (model/resolve-path nil "circle")]
    (is (= (:d (get model/path-presets "circle")) (:d result)))
    (is (some? (:view-box result)))))

(deftest resolve-path-custom-test
  (let [result (model/resolve-path "M0,0 L100,100" "circle")]
    (is (= "M0,0 L100,100" (:d result)))
    (is (= "0 0 800 200" (:view-box result)))))

(deftest resolve-path-crawl-returns-nil-test
  (is (nil? (model/resolve-path nil "crawl"))))

;; ── compute-duration-s ───────────────────────────────────────────────────
(deftest compute-duration-default-test
  (is (= 10.0 (model/compute-duration-s 1.0))))

(deftest compute-duration-fast-test
  (is (= 5.0 (model/compute-duration-s 2.0))))

(deftest compute-duration-slow-test
  (is (= 20.0 (model/compute-duration-s 0.5))))

;; ── derive-state ─────────────────────────────────────────────────────────
(deftest derive-state-defaults-test
  (let [s (model/derive-state {})]
    (is (= "" (:text s)))
    (is (= "wave" (:preset s)))
    (is (= false (:crawl? s)))
    (is (= "scroll" (:animation s)))
    (is (= "normal" (:direction s)))
    (is (= 1.0 (:speed s)))
    (is (= 1 (:repeat s)))
    (is (= #{} (:effects s)))
    (is (= 0 (:echo-count s)))
    (is (some? (:path-d s)))
    (is (some? (:view-box s)))))

(deftest derive-state-size-gradient-overrides-size-pulse-test
  (let [s (model/derive-state {:effect "size-gradient size-pulse"
                               :start-size "12px"
                               :end-size "48px"})]
    (is (contains? (:effects s) "size-gradient"))
    (is (not (contains? (:effects s) "size-pulse")))))

(deftest derive-state-color-wave-overrides-color-shift-test
  (let [s (model/derive-state {:effect "color-wave color-shift"})]
    (is (contains? (:effects s) "color-wave"))
    (is (not (contains? (:effects s) "color-shift")))))

(deftest derive-state-crawl-test
  (let [s (model/derive-state {:preset "crawl"})]
    (is (= true (:crawl? s)))
    (is (nil? (:path-d s)))
    (is (nil? (:view-box s)))
    (is (= 0 (:echo-count s)))))

(deftest derive-state-echo-defaults-test
  (let [s (model/derive-state {:echo-count "2"})]
    (is (= 2 (:echo-count s)))
    (is (= 0.3 (:echo-delay s)))
    (is (= 0.5 (:echo-opacity s)))
    (is (= 0.85 (:echo-scale s)))))
