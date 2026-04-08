(ns baredom.components.x-liquid-fill.model-test
  (:require
   [cljs.test :refer-macros [deftest is testing]]
   [baredom.components.x-liquid-fill.model :as model]))

;; ── parse-orientation ───────────────────────────────────────────────────────

(deftest parse-orientation-defaults-test
  (is (= "vertical" (model/parse-orientation nil)))
  (is (= "vertical" (model/parse-orientation "")))
  (is (= "vertical" (model/parse-orientation "nonsense"))))

(deftest parse-orientation-valid-test
  (is (= "vertical" (model/parse-orientation "vertical")))
  (is (= "horizontal" (model/parse-orientation "horizontal")))
  (is (= "horizontal" (model/parse-orientation "  Horizontal  "))))

;; ── parse-mode ──────────────────────────────────────────────────────────────

(deftest parse-mode-defaults-test
  (is (= "fill" (model/parse-mode nil)))
  (is (= "fill" (model/parse-mode "xyz"))))

(deftest parse-mode-valid-test
  (is (= "fill" (model/parse-mode "fill")))
  (is (= "bar" (model/parse-mode "bar")))
  (is (= "bar" (model/parse-mode "  Bar  "))))

;; ── parse-theme ─────────────────────────────────────────────────────────────

(deftest parse-theme-defaults-test
  (is (= "gold" (model/parse-theme nil)))
  (is (= "gold" (model/parse-theme "unknown"))))

(deftest parse-theme-valid-test
  (is (= "gold" (model/parse-theme "gold")))
  (is (= "water" (model/parse-theme "water")))
  (is (= "lava" (model/parse-theme "lava")))
  (is (= "custom" (model/parse-theme "custom")))
  (is (= "lava" (model/parse-theme "  LAVA  "))))

;; ── parse-wave-intensity ────────────────────────────────────────────────────

(deftest parse-wave-intensity-defaults-test
  (is (= 0.5 (model/parse-wave-intensity nil)))
  (is (= 0.5 (model/parse-wave-intensity "")))
  (is (= 0.5 (model/parse-wave-intensity "abc"))))

(deftest parse-wave-intensity-valid-test
  (is (= 0.3 (model/parse-wave-intensity "0.3")))
  (is (= 0.0 (model/parse-wave-intensity "0")))
  (is (= 1.0 (model/parse-wave-intensity "1"))))

(deftest parse-wave-intensity-clamps-test
  (is (= 0.0 (model/parse-wave-intensity "-0.5")))
  (is (= 1.0 (model/parse-wave-intensity "2.0"))))

;; ── parse-splash-intensity ──────────────────────────────────────────────────

(deftest parse-splash-intensity-defaults-test
  (is (= 0.7 (model/parse-splash-intensity nil))))

(deftest parse-splash-intensity-clamps-test
  (is (= 0.0 (model/parse-splash-intensity "-1")))
  (is (= 1.0 (model/parse-splash-intensity "5"))))

;; ── parse-layers ────────────────────────────────────────────────────────────

(deftest parse-layers-defaults-test
  (is (= 3 (model/parse-layers nil)))
  (is (= 3 (model/parse-layers "")))
  (is (= 3 (model/parse-layers "abc"))))

(deftest parse-layers-valid-test
  (is (= 2 (model/parse-layers "2")))
  (is (= 4 (model/parse-layers "4")))
  (is (= 5 (model/parse-layers "5"))))

(deftest parse-layers-clamps-test
  (is (= 2 (model/parse-layers "0")))
  (is (= 2 (model/parse-layers "1")))
  (is (= 5 (model/parse-layers "10"))))

;; ── parse-disabled ──────────────────────────────────────────────────────────

(deftest parse-disabled-test
  (is (false? (model/parse-disabled nil)))
  (is (true? (model/parse-disabled "")))
  (is (true? (model/parse-disabled "disabled"))))

;; ── normalize ───────────────────────────────────────────────────────────────

(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (nil? (:target m)))
    (is (= "vertical" (:orientation m)))
    (is (= "fill" (:mode m)))
    (is (= "gold" (:theme m)))
    (is (= 0.5 (:wave-intensity m)))
    (is (= 0.7 (:splash-intensity m)))
    (is (= 3 (:layers m)))
    (is (false? (:disabled? m)))))

(deftest normalize-overrides-test
  (let [m (model/normalize
           {:target-raw "#sidebar"
            :orientation-raw "horizontal"
            :mode-raw "bar"
            :theme-raw "lava"
            :wave-intensity-raw "0.8"
            :splash-intensity-raw "0.3"
            :layers-raw "4"
            :disabled-attr ""})]
    (is (= "#sidebar" (:target m)))
    (is (= "horizontal" (:orientation m)))
    (is (= "bar" (:mode m)))
    (is (= "lava" (:theme m)))
    (is (= 0.8 (:wave-intensity m)))
    (is (= 0.3 (:splash-intensity m)))
    (is (= 4 (:layers m)))
    (is (true? (:disabled? m)))))

(deftest normalize-trims-target-test
  (is (= "#foo" (:target (model/normalize {:target-raw "  #foo  "}))))
  (is (nil? (:target (model/normalize {:target-raw "   "})))))

;; ── layer-params ────────────────────────────────────────────────────────────

(deftest layer-params-front-faster-test
  (let [back  (model/layer-params 0 3)
        front (model/layer-params 2 3)]
    (is (> (:speed-mult front) (:speed-mult back)))
    (is (> (:amp-mult front) (:amp-mult back)))
    (is (= (:opacity front) (:opacity back)) "all layers opaque")
    (is (> (:stiffness back) (:stiffness front)))))

(deftest layer-params-offsets-decorrelated-test
  (let [p0 (model/layer-params 0 3)
        p1 (model/layer-params 1 3)
        p2 (model/layer-params 2 3)]
    (is (not= (:phase-offset p0) (:phase-offset p1)))
    (is (not= (:phase-offset p1) (:phase-offset p2)))))

(deftest layer-params-single-layer-test
  (let [p (model/layer-params 0 1)]
    (is (= 1.0 (:opacity p)))))

;; ── compute-scroll-progress ─────────────────────────────────────────────────

(deftest scroll-progress-empty-test
  (is (= 0.0 (model/compute-scroll-progress 0 1000 1000))))

(deftest scroll-progress-top-test
  (is (= 0.0 (model/compute-scroll-progress 0 2000 1000))))

(deftest scroll-progress-bottom-test
  (is (= 1.0 (model/compute-scroll-progress 1000 2000 1000))))

(deftest scroll-progress-middle-test
  (is (= 0.5 (model/compute-scroll-progress 500 2000 1000))))

(deftest scroll-progress-clamps-test
  (is (= 0.0 (model/compute-scroll-progress -100 2000 1000)))
  (is (= 1.0 (model/compute-scroll-progress 5000 2000 1000))))

;; ── compute-scroll-velocity ─────────────────────────────────────────────────

(deftest scroll-velocity-stationary-test
  (is (= 0.0 (model/compute-scroll-velocity 100 100 0.016))))

(deftest scroll-velocity-slow-test
  (let [v (model/compute-scroll-velocity 116 100 0.016)]
    (is (> v 0.0))
    (is (<= v 0.5))))

(deftest scroll-velocity-fast-clamps-test
  (is (= 1.0 (model/compute-scroll-velocity 10000 100 0.016))))

(deftest scroll-velocity-zero-dt-test
  (is (= 0.0 (model/compute-scroll-velocity 200 100 0.0))))

;; ── wave-y ──────────────────────────────────────────────────────────────────

(deftest wave-y-zero-amplitude-test
  (is (= 0.0 (model/wave-y 0.5 1.0 0.0 0.0))))

(deftest wave-y-bounded-test
  (testing "wave-y is bounded by amplitude"
    (let [amp 20.0]
      (dotimes [_ 20]
        (let [x (js/Math.random)
              t (* (js/Math.random) 100.0)
              y (model/wave-y x t amp 0.0)]
          (is (<= (js/Math.abs y) amp)))))))

(deftest wave-y-different-phases-differ-test
  (let [y1 (model/wave-y 0.5 1.0 10.0 0.0)
        y2 (model/wave-y 0.5 1.0 10.0 2.0)]
    (is (not= y1 y2))))

;; ── wave-path-d ─────────────────────────────────────────────────────────────

(deftest wave-path-d-format-test
  (let [d (model/wave-path-d 50.0 200.0 100.0 0.0 5.0 0.0)]
    (is (string? d))
    (is (.startsWith d "M"))
    (is (.includes d "Q"))
    (is (.endsWith d "Z"))))

(deftest horizontal-wave-path-d-format-test
  (let [d (model/horizontal-wave-path-d 50.0 200.0 100.0 0.0 5.0 0.0)]
    (is (string? d))
    (is (.startsWith d "M"))
    (is (.includes d "Q"))
    (is (.endsWith d "Z"))))

;; ── progress->fill-y / fill-x ───────────────────────────────────────────────

(deftest fill-y-empty-test
  (is (= 100.0 (model/progress->fill-y 0.0 100.0))))

(deftest fill-y-full-test
  (is (= 0.0 (model/progress->fill-y 1.0 100.0))))

(deftest fill-y-half-test
  (is (= 50.0 (model/progress->fill-y 0.5 100.0))))

(deftest fill-x-empty-test
  (is (= 0.0 (model/progress->fill-x 0.0 200.0))))

(deftest fill-x-full-test
  (is (= 200.0 (model/progress->fill-x 1.0 200.0))))

;; ── spring-step ─────────────────────────────────────────────────────────────

(deftest spring-step-converges-test
  (testing "spring converges toward target after many steps"
    (let [target 100.0]
      (loop [pos 0.0 vel 0.0 i 0]
        (if (>= i 300)
          (is (< (js/Math.abs (- pos target)) 1.0))
          (let [result (model/spring-step pos target vel 0.016 200.0 10.0)]
            (recur (aget result 0) (aget result 1) (inc i))))))))

(deftest spring-step-at-rest-test
  (testing "spring at target with zero velocity stays put"
    (let [result (model/spring-step 50.0 50.0 0.0 0.016 200.0 10.0)]
      (is (< (js/Math.abs (- (aget result 0) 50.0)) 0.01))
      (is (< (js/Math.abs (aget result 1)) 0.01)))))

;; ── lerp ────────────────────────────────────────────────────────────────────

(deftest lerp-halfway-test
  (is (= 50.0 (model/lerp 0.0 100.0 0.5))))

(deftest lerp-no-change-test
  (is (= 10.0 (model/lerp 10.0 100.0 0.0))))

(deftest lerp-full-change-test
  (is (= 100.0 (model/lerp 10.0 100.0 1.0))))

;; ── progress-detail ─────────────────────────────────────────────────────────

(deftest progress-detail-shape-test
  (let [d (model/progress-detail 0.5 0.3)]
    (is (= 0.5 (:progress d)))
    (is (= 0.3 (:velocity d)))))
