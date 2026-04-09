(ns baredom.components.x-kinetic-font.model-test
  (:require
   [cljs.test :refer-macros [deftest is testing]]
   [baredom.components.x-kinetic-font.model :as model]))

;; ── parse-trigger ───────────────────────────────────────────────────────────

(deftest parse-trigger-default-test
  (is (= "cursor" (model/parse-trigger nil)))
  (is (= "cursor" (model/parse-trigger "")))
  (is (= "cursor" (model/parse-trigger "invalid"))))

(deftest parse-trigger-valid-test
  (is (= "cursor" (model/parse-trigger "cursor")))
  (is (= "scroll" (model/parse-trigger "scroll")))
  (is (= "both"   (model/parse-trigger "both"))))

(deftest parse-trigger-case-insensitive-test
  (is (= "scroll" (model/parse-trigger "SCROLL")))
  (is (= "both"   (model/parse-trigger "Both"))))

;; ── parse-modes ─────────────────────────────────────────────────────────────

(deftest parse-modes-default-test
  (is (= #{"bulge"} (model/parse-modes nil)))
  (is (= #{"bulge"} (model/parse-modes "")))
  (is (= #{"bulge"} (model/parse-modes "invalid"))))

(deftest parse-modes-single-test
  (is (= #{"bulge"}   (model/parse-modes "bulge")))
  (is (= #{"lean"}    (model/parse-modes "lean")))
  (is (= #{"stretch"} (model/parse-modes "stretch")))
  (is (= #{"breathe"} (model/parse-modes "breathe"))))

(deftest parse-modes-multiple-test
  (is (= #{"bulge" "lean"} (model/parse-modes "bulge lean")))
  (is (= #{"stretch" "breathe"} (model/parse-modes "stretch breathe")))
  (is (= #{"bulge" "lean" "stretch" "breathe"}
         (model/parse-modes "bulge lean stretch breathe"))))

(deftest parse-modes-filters-invalid-test
  (is (= #{"bulge"} (model/parse-modes "bulge invalid")))
  (is (= #{"lean"}  (model/parse-modes "foo lean bar"))))

(deftest parse-modes-case-insensitive-test
  (is (= #{"bulge" "lean"} (model/parse-modes "BULGE Lean"))))

;; ── parse-per-char ──────────────────────────────────────────────────────────

(deftest parse-per-char-test
  (is (false? (model/parse-per-char nil)))
  (is (true?  (model/parse-per-char "")))
  (is (true?  (model/parse-per-char "true")))
  (is (true?  (model/parse-per-char "per-char"))))

;; ── parse-mass ──────────────────────────────────────────────────────────────

(deftest parse-mass-default-test
  (is (= 1.0 (model/parse-mass nil)))
  (is (= 1.0 (model/parse-mass "")))
  (is (= 1.0 (model/parse-mass "abc"))))

(deftest parse-mass-valid-test
  (is (= 5.0 (model/parse-mass "5")))
  (is (= 0.5 (model/parse-mass "0.5"))))

(deftest parse-mass-clamps-test
  (is (= 0.1 (model/parse-mass "0.01")))
  (is (= 10.0 (model/parse-mass "20"))))

;; ── parse-tension ───────────────────────────────────────────────────────────

(deftest parse-tension-default-test
  (is (= 170.0 (model/parse-tension nil)))
  (is (= 170.0 (model/parse-tension "abc"))))

(deftest parse-tension-valid-test
  (is (= 200.0 (model/parse-tension "200"))))

(deftest parse-tension-clamps-test
  (is (= 10.0 (model/parse-tension "5")))
  (is (= 500.0 (model/parse-tension "999"))))

;; ── parse-friction ──────────────────────────────────────────────────────────

(deftest parse-friction-default-test
  (is (= 26.0 (model/parse-friction nil)))
  (is (= 26.0 (model/parse-friction "abc"))))

(deftest parse-friction-valid-test
  (is (= 15.0 (model/parse-friction "15"))))

(deftest parse-friction-clamps-test
  (is (= 1.0 (model/parse-friction "0.5")))
  (is (= 100.0 (model/parse-friction "200"))))

;; ── parse-intensity ─────────────────────────────────────────────────────────

(deftest parse-intensity-default-test
  (is (= 0.5 (model/parse-intensity nil))))

(deftest parse-intensity-valid-test
  (is (= 0.8 (model/parse-intensity "0.8"))))

(deftest parse-intensity-clamps-test
  (is (= 0.0 (model/parse-intensity "-1")))
  (is (= 1.0 (model/parse-intensity "5"))))

;; ── parse-radius ────────────────────────────────────────────────────────────

(deftest parse-radius-default-test
  (is (= 200.0 (model/parse-radius nil))))

(deftest parse-radius-valid-test
  (is (= 300.0 (model/parse-radius "300"))))

(deftest parse-radius-clamps-test
  (is (= 20.0 (model/parse-radius "5")))
  (is (= 1000.0 (model/parse-radius "2000"))))

;; ── normalize-text ──────────────────────────────────────────────────────────

(deftest normalize-text-test
  (is (= "" (model/normalize-text nil)))
  (is (= "" (model/normalize-text "")))
  (is (= "Hello" (model/normalize-text "Hello"))))

;; ── normalize-font-family ───────────────────────────────────────────────────

(deftest normalize-font-family-test
  (is (nil? (model/normalize-font-family nil)))
  (is (nil? (model/normalize-font-family "")))
  (is (nil? (model/normalize-font-family "   ")))
  (is (= "Inter Variable" (model/normalize-font-family "Inter Variable")))
  (is (= "Inter Variable" (model/normalize-font-family "  Inter Variable  "))))

;; ── derive-state ────────────────────────────────────────────────────────────

(deftest derive-state-defaults-test
  (let [m (model/derive-state {})]
    (is (= "" (:text m)))
    (is (= "cursor" (:trigger m)))
    (is (= #{"bulge"} (:modes m)))
    (is (false? (:per-char? m)))
    (is (= 1.0 (:mass m)))
    (is (= 170.0 (:tension m)))
    (is (= 26.0 (:friction m)))
    (is (= 0.5 (:intensity m)))
    (is (= 200.0 (:radius m)))
    (is (nil? (:font-family m)))))

(deftest derive-state-overrides-test
  (let [m (model/derive-state {:text-raw "Hello"
                               :trigger-raw "scroll"
                               :mode-raw "lean stretch"
                               :per-char-attr ""
                               :mass-raw "2"
                               :tension-raw "200"
                               :friction-raw "30"
                               :intensity-raw "0.8"
                               :radius-raw "300"
                               :font-family-raw "Inter Variable"})]
    (is (= "Hello" (:text m)))
    (is (= "scroll" (:trigger m)))
    (is (= #{"lean" "stretch"} (:modes m)))
    (is (true? (:per-char? m)))
    (is (= 2.0 (:mass m)))
    (is (= 200.0 (:tension m)))
    (is (= 30.0 (:friction m)))
    (is (= 0.8 (:intensity m)))
    (is (= 300.0 (:radius m)))
    (is (= "Inter Variable" (:font-family m)))))

;; ── spring-step ─────────────────────────────────────────────────────────────

(deftest spring-step-converges-test
  (testing "spring converges toward target after many steps"
    (loop [pos 0.0 vel 0.0 i 0]
      (if (>= i 300)
        (is (< (js/Math.abs (- pos 1.0)) 0.01))
        (let [result (model/spring-step pos 1.0 vel 0.016 1.0 170.0 26.0)]
          (recur (aget result 0) (aget result 1) (inc i)))))))

(deftest spring-step-at-rest-test
  (testing "spring at target with zero velocity stays put"
    (let [result (model/spring-step 1.0 1.0 0.0 0.016 1.0 170.0 26.0)]
      (is (< (js/Math.abs (- (aget result 0) 1.0)) 0.001))
      (is (< (js/Math.abs (aget result 1)) 0.001)))))

(deftest spring-step-mass-affects-speed-test
  (testing "higher mass produces slower initial movement"
    (let [light (model/spring-step 0.0 1.0 0.0 0.016 0.5 170.0 26.0)
          heavy (model/spring-step 0.0 1.0 0.0 0.016 5.0 170.0 26.0)]
      (is (> (aget light 0) (aget heavy 0))))))

;; ── spring-settled? ─────────────────────────────────────────────────────────

(deftest spring-settled-test
  (is (true? (model/spring-settled? 0.0 0.0)))
  (is (true? (model/spring-settled? 0.0005 0.005)))
  (is (false? (model/spring-settled? 0.01 0.0)))
  (is (false? (model/spring-settled? 0.0 0.1))))

;; ── compute-cursor-force ────────────────────────────────────────────────────

(deftest compute-cursor-force-outside-radius-test
  (is (= 0.0 (model/compute-cursor-force 200.0 200.0)))
  (is (= 0.0 (model/compute-cursor-force 300.0 200.0))))

(deftest compute-cursor-force-at-center-test
  (is (= 1.0 (model/compute-cursor-force 0.0 200.0))))

(deftest compute-cursor-force-intermediate-test
  (let [f (model/compute-cursor-force 100.0 200.0)]
    (is (> f 0.0))
    (is (< f 1.0))
    ;; quadratic: (1 - 100/200)^2 = 0.25
    (is (< (js/Math.abs (- f 0.25)) 0.001))))

;; ── compute-scroll-force ────────────────────────────────────────────────────

(deftest compute-scroll-force-test
  (is (= 0.0 (model/compute-scroll-force 0.0)))
  (is (= 0.5 (model/compute-scroll-force 50.0)))
  (is (= 1.0 (model/compute-scroll-force 100.0)))
  (is (= 1.0 (model/compute-scroll-force 500.0)))
  (is (= 0.5 (model/compute-scroll-force -50.0))))

;; ── map-force-to-axes ───────────────────────────────────────────────────────

(deftest map-force-to-axes-zero-force-test
  (let [axes (model/map-force-to-axes 0.0 #{"bulge" "lean" "stretch" "breathe"} 1.0
               100.0 900.0 75.0 125.0 -12.0 0.0 8.0 144.0)]
    (is (= 100.0 (aget axes 0)))   ;; wght at rest
    (is (= 75.0  (aget axes 1)))   ;; wdth at rest
    (is (= 0.0   (aget axes 2)))   ;; slnt at rest
    (is (= 8.0   (aget axes 3))))) ;; opsz at rest

(deftest map-force-to-axes-full-force-test
  (let [axes (model/map-force-to-axes 1.0 #{"bulge" "lean" "stretch" "breathe"} 1.0
               100.0 900.0 75.0 125.0 -12.0 0.0 8.0 144.0)]
    (is (= 900.0  (aget axes 0)))  ;; wght excited
    (is (= 125.0  (aget axes 1)))  ;; wdth excited
    (is (= -12.0  (aget axes 2)))  ;; slnt excited
    (is (= 144.0  (aget axes 3)))));; opsz excited

(deftest map-force-to-axes-partial-modes-test
  (testing "inactive modes stay at rest values"
    (let [axes (model/map-force-to-axes 1.0 #{"bulge"} 1.0
                 100.0 900.0 75.0 125.0 -12.0 0.0 8.0 144.0)]
      (is (= 900.0 (aget axes 0)))  ;; bulge active
      (is (= 75.0  (aget axes 1)))  ;; stretch inactive
      (is (= 0.0   (aget axes 2)))  ;; lean inactive
      (is (= 8.0   (aget axes 3))))));; breathe inactive

(deftest map-force-to-axes-intensity-scales-test
  (let [half (model/map-force-to-axes 1.0 #{"bulge"} 0.5
               100.0 900.0 75.0 125.0 -12.0 0.0 8.0 144.0)
        full (model/map-force-to-axes 1.0 #{"bulge"} 1.0
               100.0 900.0 75.0 125.0 -12.0 0.0 8.0 144.0)]
    ;; half intensity: 100 + 0.5 * 800 = 500
    (is (< (js/Math.abs (- (aget half 0) 500.0)) 0.1))
    (is (= 900.0 (aget full 0)))))

;; ── build-variation-string ──────────────────────────────────────────────────

(deftest build-variation-string-test
  (let [axes #js [400.0 100.0 0.0 24.0]
        s (model/build-variation-string axes)]
    (is (= "'wght' 400.0, 'wdth' 100.0, 'slnt' 0.0, 'opsz' 24.0" s))))
