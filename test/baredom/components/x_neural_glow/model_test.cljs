(ns baredom.components.x-neural-glow.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-neural-glow.model :as model]))

;; ── parse-orb-count ─────────────────────────────────────────────────────────
(deftest parse-orb-count-valid-test
  (is (= 15 (model/parse-orb-count "15")))
  (is (= 3  (model/parse-orb-count "3")))
  (is (= 50 (model/parse-orb-count "50"))))

(deftest parse-orb-count-default-test
  (is (= 15 (model/parse-orb-count nil)))
  (is (= 15 (model/parse-orb-count "")))
  (is (= 15 (model/parse-orb-count "abc")))
  (is (= 15 (model/parse-orb-count "-3"))))

(deftest parse-orb-count-clamping-test
  (is (= 3  (model/parse-orb-count "1")))
  (is (= 50 (model/parse-orb-count "100"))))

(deftest parse-orb-count-whitespace-test
  (is (= 10 (model/parse-orb-count " 10 "))))

;; ── parse-color-primary ─────────────────────────────────────────────────────
(deftest parse-color-primary-valid-test
  (is (= "#ff0000" (model/parse-color-primary "#ff0000")))
  (is (= "red" (model/parse-color-primary "red")))
  (is (= "rgba(0,0,0,0.5)" (model/parse-color-primary "rgba(0,0,0,0.5)"))))

(deftest parse-color-primary-default-test
  (is (= "#4f8bff" (model/parse-color-primary nil)))
  (is (= "#4f8bff" (model/parse-color-primary "")))
  (is (= "#4f8bff" (model/parse-color-primary "   "))))

;; ── parse-color-secondary ───────────────────────────────────────────────────
(deftest parse-color-secondary-default-test
  (is (= "#00e5cc" (model/parse-color-secondary nil)))
  (is (= "#00e5cc" (model/parse-color-secondary ""))))

(deftest parse-color-secondary-valid-test
  (is (= "cyan" (model/parse-color-secondary "cyan"))))

;; ── parse-color-background ──────────────────────────────────────────────────
(deftest parse-color-background-default-test
  (is (= "#050a18" (model/parse-color-background nil)))
  (is (= "#050a18" (model/parse-color-background ""))))

(deftest parse-color-background-valid-test
  (is (= "#000000" (model/parse-color-background "#000000"))))

;; ── parse-pulse-speed ───────────────────────────────────────────────────────
(deftest parse-pulse-speed-valid-test
  (is (= 1.0 (model/parse-pulse-speed "1.0")))
  (is (= 2.5 (model/parse-pulse-speed "2.5"))))

(deftest parse-pulse-speed-default-test
  (is (= 1.0 (model/parse-pulse-speed nil)))
  (is (= 1.0 (model/parse-pulse-speed "")))
  (is (= 1.0 (model/parse-pulse-speed "abc"))))

(deftest parse-pulse-speed-clamping-test
  (is (= 0.1 (model/parse-pulse-speed "0.01")))
  (is (= 5.0 (model/parse-pulse-speed "10"))))

;; ── parse-rest-rate ─────────────────────────────────────────────────────────
(deftest parse-rest-rate-valid-test
  (is (= 4.0 (model/parse-rest-rate "4")))
  (is (= 2.0 (model/parse-rest-rate "2"))))

(deftest parse-rest-rate-default-test
  (is (= 4.0 (model/parse-rest-rate nil)))
  (is (= 4.0 (model/parse-rest-rate ""))))

(deftest parse-rest-rate-clamping-test
  (is (= 1.0  (model/parse-rest-rate "0.5")))
  (is (= 10.0 (model/parse-rest-rate "20"))))

;; ── parse-connection-distance ───────────────────────────────────────────────
(deftest parse-connection-distance-valid-test
  (is (= 0.15 (model/parse-connection-distance "0.15")))
  (is (= 0.3  (model/parse-connection-distance "0.3"))))

(deftest parse-connection-distance-default-test
  (is (= 0.15 (model/parse-connection-distance nil)))
  (is (= 0.15 (model/parse-connection-distance ""))))

(deftest parse-connection-distance-clamping-test
  (is (= 0.05 (model/parse-connection-distance "0.01")))
  (is (= 0.5  (model/parse-connection-distance "1.0"))))

;; ── parse-orb-size ──────────────────────────────────────────────────────────
(deftest parse-orb-size-valid-test
  (is (= 40  (model/parse-orb-size "40")))
  (is (= 100 (model/parse-orb-size "100"))))

(deftest parse-orb-size-default-test
  (is (= 40 (model/parse-orb-size nil)))
  (is (= 40 (model/parse-orb-size "")))
  (is (= 40 (model/parse-orb-size "abc"))))

(deftest parse-orb-size-clamping-test
  (is (= 10  (model/parse-orb-size "5")))
  (is (= 200 (model/parse-orb-size "300"))))

;; ── parse-opacity ───────────────────────────────────────────────────────────
(deftest parse-opacity-valid-test
  (is (= 0.8 (model/parse-opacity "0.8")))
  (is (= 0.0 (model/parse-opacity "0")))
  (is (= 1.0 (model/parse-opacity "1"))))

(deftest parse-opacity-default-test
  (is (= 0.8 (model/parse-opacity nil)))
  (is (= 0.8 (model/parse-opacity "")))
  (is (= 0.8 (model/parse-opacity "abc"))))

(deftest parse-opacity-clamping-test
  (is (= 0.0 (model/parse-opacity "-0.5")))
  (is (= 1.0 (model/parse-opacity "2.0"))))

;; ── parse-interactive ───────────────────────────────────────────────────────
(deftest parse-interactive-default-true-test
  (is (true? (model/parse-interactive nil))
      "absent attribute → interactive is true"))

(deftest parse-interactive-false-test
  (is (false? (model/parse-interactive "false"))
      "interactive='false' → false"))

(deftest parse-interactive-explicit-true-test
  (is (true? (model/parse-interactive ""))
      "interactive (present, empty) → true")
  (is (true? (model/parse-interactive "true"))
      "interactive='true' → true"))

;; ── normalize ───────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= 15       (:orb-count m)))
    (is (= "#4f8bff" (:color-primary m)))
    (is (= "#00e5cc" (:color-secondary m)))
    (is (= "#050a18" (:color-background m)))
    (is (= 1.0      (:pulse-speed m)))
    (is (= 4.0      (:rest-rate m)))
    (is (= 0.15     (:connection-distance m)))
    (is (= 40       (:orb-size m)))
    (is (= 0.8      (:opacity m)))
    (is (true?      (:interactive? m)))))

(deftest normalize-custom-values-test
  (let [m (model/normalize
           {:orb-count-raw        "20"
            :color-primary-raw    "hotpink"
            :color-secondary-raw  "lime"
            :color-background-raw "#111"
            :pulse-speed-raw      "2.0"
            :rest-rate-raw        "3.0"
            :connection-distance-raw "0.25"
            :orb-size-raw         "60"
            :opacity-raw          "0.5"
            :interactive-raw      "false"
            :interactive-present? true})]
    (is (= 20        (:orb-count m)))
    (is (= "hotpink" (:color-primary m)))
    (is (= "lime"    (:color-secondary m)))
    (is (= "#111"    (:color-background m)))
    (is (= 2.0       (:pulse-speed m)))
    (is (= 3.0       (:rest-rate m)))
    (is (= 0.25      (:connection-distance m)))
    (is (= 60        (:orb-size m)))
    (is (= 0.5       (:opacity m)))
    (is (false?      (:interactive? m)))))

;; ── lerp ────────────────────────────────────────────────────────────────────
(deftest lerp-midpoint-test
  (is (= 5.0 (model/lerp 0 10 0.5))))

(deftest lerp-at-target-test
  (is (= 10.0 (model/lerp 10 10 0.3))))

(deftest lerp-zero-speed-test
  (is (= 5.0 (model/lerp 5 10 0))))

(deftest lerp-full-speed-test
  (is (= 10.0 (model/lerp 5 10 1.0))))

;; ── init-orb-positions ──────────────────────────────────────────────────────
(deftest init-orb-positions-count-test
  (is (= 10 (.-length (model/init-orb-positions 10))))
  (is (= 3  (.-length (model/init-orb-positions 3)))))

(deftest init-orb-positions-range-test
  (let [positions (model/init-orb-positions 20)]
    (dotimes [i 20]
      (let [p (aget positions i)]
        (is (<= 0.0 (aget p 0) 1.0) "x should be in [0,1]")
        (is (<= 0.0 (aget p 1) 1.0) "y should be in [0,1]")))))

;; ── init-orb-phases ─────────────────────────────────────────────────────────
(deftest init-orb-phases-count-test
  (is (= 10 (.-length (model/init-orb-phases 10))))
  (is (= 5  (.-length (model/init-orb-phases 5)))))

(deftest init-orb-phases-range-test
  (let [phases (model/init-orb-phases 20)
        two-pi (* 2.0 js/Math.PI)]
    (dotimes [i 20]
      (is (<= 0.0 (aget phases i) two-pi) "phase should be in [0, 2*PI]"))))
