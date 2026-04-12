(ns baredom.components.x-particle-button.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-particle-button.model :as model]))

(deftest normalize-type-test
  (testing "preserves valid values"
    (is (= "button" (model/normalize-type "button")))
    (is (= "submit" (model/normalize-type "submit")))
    (is (= "reset" (model/normalize-type "reset"))))
  (testing "falls back to default"
    (is (= model/default-type (model/normalize-type nil)))
    (is (= model/default-type (model/normalize-type "oops")))))

(deftest normalize-variant-test
  (testing "preserves valid values including extended variants"
    (is (= "primary" (model/normalize-variant "primary")))
    (is (= "secondary" (model/normalize-variant "secondary")))
    (is (= "tertiary" (model/normalize-variant "tertiary")))
    (is (= "ghost" (model/normalize-variant "ghost")))
    (is (= "danger" (model/normalize-variant "danger")))
    (is (= "success" (model/normalize-variant "success")))
    (is (= "warning" (model/normalize-variant "warning"))))
  (testing "falls back to default"
    (is (= model/default-variant (model/normalize-variant nil)))
    (is (= model/default-variant (model/normalize-variant "weird")))))

(deftest normalize-size-test
  (testing "preserves valid values"
    (is (= "sm" (model/normalize-size "sm")))
    (is (= "md" (model/normalize-size "md")))
    (is (= "lg" (model/normalize-size "lg"))))
  (testing "falls back to default"
    (is (= model/default-size (model/normalize-size nil)))
    (is (= model/default-size (model/normalize-size "xl")))))

(deftest normalize-mode-test
  (testing "preserves valid modes"
    (is (= "dust" (model/normalize-mode "dust")))
    (is (= "spark" (model/normalize-mode "spark")))
    (is (= "ember" (model/normalize-mode "ember")))
    (is (= "burst" (model/normalize-mode "burst")))
    (is (= "disperse" (model/normalize-mode "disperse"))))
  (testing "falls back to dust"
    (is (= "dust" (model/normalize-mode nil)))
    (is (= "dust" (model/normalize-mode "explode")))))

(deftest clamp-int-test
  (testing "returns default for nil"
    (is (= 40 (model/clamp-int nil 40 10 100))))
  (testing "returns default for non-numeric"
    (is (= 40 (model/clamp-int "abc" 40 10 100))))
  (testing "clamps below min"
    (is (= 10 (model/clamp-int "5" 40 10 100))))
  (testing "clamps above max"
    (is (= 100 (model/clamp-int "200" 40 10 100))))
  (testing "passes through valid values"
    (is (= 50 (model/clamp-int "50" 40 10 100))))
  (testing "handles boundary values"
    (is (= 10 (model/clamp-int "10" 40 10 100)))
    (is (= 100 (model/clamp-int "100" 40 10 100)))))

(deftest normalize-particle-count-test
  (is (= 40 (model/normalize-particle-count nil)))
  (is (= 40 (model/normalize-particle-count "abc")))
  (is (= 10 (model/normalize-particle-count "5")))
  (is (= 100 (model/normalize-particle-count "200")))
  (is (= 60 (model/normalize-particle-count "60"))))

(deftest normalize-intensity-test
  (is (= 50 (model/normalize-intensity nil)))
  (is (= 1 (model/normalize-intensity "0")))
  (is (= 100 (model/normalize-intensity "150")))
  (is (= 75 (model/normalize-intensity "75"))))

(deftest normalize-particle-size-test
  (is (= 3 (model/normalize-particle-size nil)))
  (is (= 1 (model/normalize-particle-size "0")))
  (is (= 8 (model/normalize-particle-size "20")))
  (is (= 5 (model/normalize-particle-size "5"))))

(deftest normalize-reassemble-speed-test
  (is (= 500 (model/normalize-reassemble-speed nil)))
  (is (= 100 (model/normalize-reassemble-speed "50")))
  (is (= 2000 (model/normalize-reassemble-speed "5000")))
  (is (= 800 (model/normalize-reassemble-speed "800"))))

(deftest interactive-test
  (is (= true (model/interactive? {:disabled false :loading false})))
  (is (= false (model/interactive? {:disabled true :loading false})))
  (is (= false (model/interactive? {:disabled false :loading true})))
  (is (= false (model/interactive? {:disabled true :loading true}))))

(deftest aria-busy-test
  (is (= "true" (model/aria-busy {:loading true})))
  (is (= nil (model/aria-busy {:loading false}))))

(deftest aria-label-test
  (is (= "Close"
         (model/aria-label {:label "Close" :has-default-text? false})))
  (is (= nil
         (model/aria-label {:label "Close" :has-default-text? true})))
  (is (= nil
         (model/aria-label {:label "" :has-default-text? false})))
  (is (= nil
         (model/aria-label {:label nil :has-default-text? false}))))

(deftest public-state-test
  (testing "defaults for empty input"
    (let [s (model/public-state {})]
      (is (= false (:disabled s)))
      (is (= false (:loading s)))
      (is (= false (:pressed s)))
      (is (= "button" (:type s)))
      (is (= "primary" (:variant s)))
      (is (= "md" (:size s)))
      (is (= nil (:label s)))
      (is (= "dust" (:mode s)))
      (is (= 40 (:particle-count s)))
      (is (= 50 (:intensity s)))
      (is (= 3 (:particle-size s)))
      (is (= 500 (:reassemble-speed s)))))
  (testing "all fields set"
    (let [s (model/public-state
             {:disabled true :loading true :pressed true
              :type "submit" :variant "danger" :size "lg"
              :label "Delete" :mode "spark"
              :particle-count "80" :intensity "90"
              :particle-size "5" :reassemble-speed "1000"})]
      (is (= true (:disabled s)))
      (is (= "submit" (:type s)))
      (is (= "danger" (:variant s)))
      (is (= "spark" (:mode s)))
      (is (= 80 (:particle-count s)))
      (is (= 90 (:intensity s))))))

;; --- Particle type selection ---
(deftest pick-particle-type-test
  (let [dust-cfg {:dust-ratio 0.70 :fragment-ratio 0.20 :streak-ratio 0.10}]
    (testing "low random gives dust"
      (is (= model/type-dust (model/pick-particle-type 0.0 dust-cfg)))
      (is (= model/type-dust (model/pick-particle-type 0.5 dust-cfg)))
      (is (= model/type-dust (model/pick-particle-type 0.69 dust-cfg))))
    (testing "mid random gives fragment"
      (is (= model/type-fragment (model/pick-particle-type 0.70 dust-cfg)))
      (is (= model/type-fragment (model/pick-particle-type 0.85 dust-cfg))))
    (testing "high random gives streak"
      (is (= model/type-streak (model/pick-particle-type 0.90 dust-cfg)))
      (is (= model/type-streak (model/pick-particle-type 0.99 dust-cfg))))))

;; --- Edge point geometry ---
(deftest random-edge-point-test
  (testing "top edge"
    (let [[x y _nx ny] (model/random-edge-point 100 50 0.1)]
      (is (> x 0))
      (is (< x 100))
      (is (= 0.0 y))
      (is (= -1.0 ny))))
  (testing "right edge"
    (let [[x _y nx _ny] (model/random-edge-point 100 50 0.4)]
      (is (= 100 x))
      (is (= 1.0 nx))))
  (testing "bottom edge"
    (let [[_x y _nx ny] (model/random-edge-point 100 50 0.6)]
      (is (= 50 y))
      (is (= 1.0 ny))))
  (testing "left edge"
    (let [[x _y nx _ny] (model/random-edge-point 100 50 0.9)]
      (is (= 0.0 x))
      (is (= -1.0 nx)))))

;; --- Press burst velocity ---
(deftest press-burst-velocity-test
  (testing "velocity points away from center when clicked at center"
    (let [[vx vy] (model/press-burst-velocity 50 25 100 50 5.0 1.0 0.5 0.5)]
      ;; At center, direction is ambiguous but speed should be nonzero
      (is (> (js/Math.sqrt (+ (* vx vx) (* vy vy))) 0))))
  (testing "velocity has reasonable magnitude"
    (let [[vx vy] (model/press-burst-velocity 80 10 100 50 5.0 0.5 0.5 0.5)
          speed (js/Math.sqrt (+ (* vx vx) (* vy vy)))]
      (is (> speed 2.0))
      (is (< speed 10.0)))))

;; --- Mode physics ---
(deftest mode-physics-test
  (testing "dust mode is subtle"
    (let [p (model/mode-physics "dust")]
      (is (= 0.6 (:hover-emit-rate p)))
      (is (= :edge-only (:hover-source p)))
      (is (= false (:surface-dissolve p)))
      (is (= 0.90 (:dust-ratio p)))
      (is (= false (:glow-render p)))))
  (testing "spark mode is energetic"
    (let [p (model/mode-physics "spark")]
      (is (= 2.5 (:hover-emit-rate p)))
      (is (= 6.0 (:press-base-speed p)))
      (is (= 0.35 (:streak-ratio p)))
      (is (= true (:additive-core p)))))
  (testing "ember mode is slow and warm"
    (let [p (model/mode-physics "ember")]
      (is (= 0.99 (:friction p)))
      (is (= -0.008 (:gravity p)))
      (is (= true (:glow-render p)))))
  (testing "burst mode has high particle count"
    (let [p (model/mode-physics "burst")]
      (is (= 30 (:press-burst-count-min p)))
      (is (= 50 (:press-burst-count-max p)))
      (is (= 7.0 (:press-base-speed p)))))
  (testing "disperse mode has reform and dissolve"
    (let [p (model/mode-physics "disperse")]
      (is (= 0.08 (:reform-spring p)))
      (is (= true (:surface-dissolve p)))))
  (testing "invalid mode falls back to dust"
    (let [p (model/mode-physics "invalid")]
      (is (= 0.6 (:hover-emit-rate p))))))

;; --- Variant visuals ---
(deftest variant-visuals-test
  (testing "danger has high glow"
    (is (= 1.5 (:glow-intensity (model/variant-visuals "danger")))))
  (testing "primary has default glow"
    (is (= 1.0 (:glow-intensity (model/variant-visuals "primary"))))))
