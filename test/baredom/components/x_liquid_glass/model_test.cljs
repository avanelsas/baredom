(ns baredom.components.x-liquid-glass.model-test
  (:require
   [cljs.test :refer-macros [deftest is testing]]
   [baredom.components.x-liquid-glass.model :as model]))

;; ── parse-blobs ─────────────────────────────────────────────────────────────

(deftest parse-blobs-default-test
  (is (= 5 (model/parse-blobs nil)))
  (is (= 5 (model/parse-blobs "")))
  (is (= 5 (model/parse-blobs "abc"))))

(deftest parse-blobs-valid-test
  (is (= 4 (model/parse-blobs "4")))
  (is (= 8 (model/parse-blobs "8"))))

(deftest parse-blobs-clamps-test
  (is (= 3 (model/parse-blobs "1")))
  (is (= 8 (model/parse-blobs "20"))))

;; ── parse-speed ─────────────────────────────────────────────────────────────

(deftest parse-speed-default-test
  (is (= 0.3 (model/parse-speed nil)))
  (is (= 0.3 (model/parse-speed "abc"))))

(deftest parse-speed-valid-test
  (is (= 1.0 (model/parse-speed "1")))
  (is (= 0.5 (model/parse-speed "0.5"))))

(deftest parse-speed-clamps-test
  (is (= 0.05 (model/parse-speed "0.01")))
  (is (= 2.0 (model/parse-speed "5"))))

;; ── parse-amplitude ─────────────────────────────────────────────────────────

(deftest parse-amplitude-default-test
  (is (= 0.15 (model/parse-amplitude nil))))

(deftest parse-amplitude-valid-test
  (is (= 0.2 (model/parse-amplitude "0.2"))))

(deftest parse-amplitude-clamps-test
  (is (= 0.05 (model/parse-amplitude "0.01")))
  (is (= 0.4 (model/parse-amplitude "0.9"))))

;; ── parse-blur ──────────────────────────────────────────────────────────────

(deftest parse-blur-default-test
  (is (= 12.0 (model/parse-blur nil))))

(deftest parse-blur-valid-test
  (is (= 8.0 (model/parse-blur "8"))))

(deftest parse-blur-clamps-test
  (is (= 0.0 (model/parse-blur "-5")))
  (is (= 40.0 (model/parse-blur "100"))))

;; ── parse-goo ───────────────────────────────────────────────────────────────

(deftest parse-goo-default-test
  (is (= 10.0 (model/parse-goo nil))))

(deftest parse-goo-valid-test
  (is (= 15.0 (model/parse-goo "15"))))

(deftest parse-goo-clamps-test
  (is (= 4.0 (model/parse-goo "1")))
  (is (= 20.0 (model/parse-goo "50"))))

;; ── parse-specular-size ─────────────────────────────────────────────────────

(deftest parse-specular-size-default-test
  (is (= 0.4 (model/parse-specular-size nil))))

(deftest parse-specular-size-clamps-test
  (is (= 0.1 (model/parse-specular-size "0.01")))
  (is (= 1.0 (model/parse-specular-size "5"))))

;; ── parse-specular-intensity ────────────────────────────────────────────────

(deftest parse-specular-intensity-default-test
  (is (= 0.3 (model/parse-specular-intensity nil))))

(deftest parse-specular-intensity-clamps-test
  (is (= 0.0 (model/parse-specular-intensity "-1")))
  (is (= 1.0 (model/parse-specular-intensity "5"))))

;; ── parse-bool ──────────────────────────────────────────────────────────────

(deftest parse-bool-test
  (is (false? (model/parse-bool nil)))
  (is (true? (model/parse-bool "")))
  (is (true? (model/parse-bool "true")))
  (is (true? (model/parse-bool "disabled"))))

;; ── parse-tint ──────────────────────────────────────────────────────────────

(deftest parse-tint-test
  (is (nil? (model/parse-tint nil)))
  (is (nil? (model/parse-tint "")))
  (is (nil? (model/parse-tint "  ")))
  (is (= "rgba(255,0,0,0.2)" (model/parse-tint "rgba(255,0,0,0.2)"))))

;; ── normalize ───────────────────────────────────────────────────────────────

(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= 5 (:blobs m)))
    (is (= 0.3 (:speed m)))
    (is (= 0.15 (:amplitude m)))
    (is (= 12.0 (:blur m)))
    (is (= 10.0 (:goo m)))
    (is (nil? (:tint m)))
    (is (false? (:specular? m)))
    (is (= 0.4 (:specular-size m)))
    (is (= 0.3 (:specular-intensity m)))
    (is (false? (:disabled? m)))))

(deftest normalize-overrides-test
  (let [m (model/normalize {:blobs-raw "7"
                            :speed-raw "1.0"
                            :amplitude-raw "0.25"
                            :blur-raw "20"
                            :goo-raw "15"
                            :tint-raw "red"
                            :specular-attr ""
                            :specular-size-raw "0.6"
                            :specular-intensity-raw "0.8"
                            :disabled-attr ""})]
    (is (= 7 (:blobs m)))
    (is (= 1.0 (:speed m)))
    (is (= 0.25 (:amplitude m)))
    (is (= 20.0 (:blur m)))
    (is (= 15.0 (:goo m)))
    (is (= "red" (:tint m)))
    (is (true? (:specular? m)))
    (is (= 0.6 (:specular-size m)))
    (is (= 0.8 (:specular-intensity m)))
    (is (true? (:disabled? m)))))

;; ── parse-frost ─────────────────────────────────────────────────────────────

(deftest parse-frost-default-test
  (is (= 0.5 (model/parse-frost nil)))
  (is (= 0.5 (model/parse-frost "")))
  (is (= 0.5 (model/parse-frost "abc"))))

(deftest parse-frost-valid-test
  (is (= 0.0 (model/parse-frost "0")))
  (is (= 0.3 (model/parse-frost "0.3")))
  (is (= 1.0 (model/parse-frost "1"))))

(deftest parse-frost-clamps-test
  (is (= 0.0 (model/parse-frost "-0.5")))
  (is (= 1.0 (model/parse-frost "2"))))

;; ── parse-mode ──────────────────────────────────────────────────────────────

(deftest parse-mode-default-test
  (is (= :surface (model/parse-mode nil)))
  (is (= :surface (model/parse-mode "")))
  (is (= :surface (model/parse-mode "nonsense"))))

(deftest parse-mode-valid-test
  (is (= :surface (model/parse-mode "surface")))
  (is (= :submerged (model/parse-mode "submerged")))
  (is (= :submerged (model/parse-mode "Submerged"))))

;; ── parse-color ─────────────────────────────────────────────────────────────

(deftest parse-color-test
  (is (nil? (model/parse-color nil)))
  (is (nil? (model/parse-color "")))
  (is (nil? (model/parse-color "  ")))
  (is (= "rgba(0,200,100,0.2)" (model/parse-color "rgba(0,200,100,0.2)")))
  (is (= "#ff0000" (model/parse-color " #ff0000 "))))

;; ── normalize with mode and colors ──────────────────────────────────────────

(deftest normalize-mode-defaults-test
  (let [m (model/normalize {})]
    (is (= :surface (:mode m)))
    (is (= "rgba(99,102,241,0.4)" (:color-1 m)))
    (is (= "rgba(244,114,182,0.35)" (:color-2 m)))))

(deftest normalize-mode-submerged-test
  (let [m (model/normalize {:mode-raw "submerged"
                            :color-1-raw "red"
                            :color-2-raw "blue"})]
    (is (= :submerged (:mode m)))
    (is (= "red" (:color-1 m)))
    (is (= "blue" (:color-2 m)))))

;; ── pseudo-noise ────────────────────────────────────────────────────────────

(deftest pseudo-noise-range-test
  (testing "pseudo-noise output is bounded approximately to [-1, 1]"
    (let [samples (for [x (range -5 5 0.7)
                        y (range -5 5 0.7)
                        t (range 0 5 0.5)]
                    (model/pseudo-noise x y t))]
      (doseq [v samples]
        (is (< v 1.5))
        (is (> v -1.5))))))

(deftest pseudo-noise-deterministic-test
  (is (= (model/pseudo-noise 1.0 2.0 3.0)
         (model/pseudo-noise 1.0 2.0 3.0))))

(deftest pseudo-noise-varies-test
  (is (not= (model/pseudo-noise 0.0 0.0 0.0)
            (model/pseudo-noise 1.0 0.0 0.0))))

;; ── satellite-rest-positions ────────────────────────────────────────────────

(deftest satellite-rest-positions-count-test
  (let [result (model/satellite-rest-positions 5 100.0 75.0 40.0 30.0)
        ^js xs (aget result 0)
        ^js ys (aget result 1)]
    (is (= 5 (.-length xs)))
    (is (= 5 (.-length ys)))))

(deftest satellite-rest-positions-first-point-test
  (testing "first satellite is at angle 0 → (cx+rx, cy)"
    (let [result (model/satellite-rest-positions 5 100.0 75.0 40.0 30.0)
          ^js xs (aget result 0)
          ^js ys (aget result 1)]
      (is (< (js/Math.abs (- (aget xs 0) 140.0)) 0.01))
      (is (< (js/Math.abs (- (aget ys 0) 75.0)) 0.01)))))

;; ── displace-satellite ──────────────────────────────────────────────────────

(deftest displace-satellite-returns-pair-test
  (let [result (model/displace-satellite 100.0 75.0 0 1.0 0.3 20.0)]
    (is (= 2 (.-length result)))
    (is (number? (aget result 0)))
    (is (number? (aget result 1)))))

(deftest displace-satellite-amplitude-zero-test
  (testing "zero amplitude returns rest position"
    (let [result (model/displace-satellite 100.0 75.0 0 1.0 0.3 0.0)]
      (is (= 100.0 (aget result 0)))
      (is (= 75.0 (aget result 1))))))

;; ── spring-easing ───────────────────────────────────────────────────────────

(deftest spring-easing-format-test
  (is (string? model/spring-easing))
  (is (.startsWith model/spring-easing "linear("))
  (is (.endsWith model/spring-easing ")")))
