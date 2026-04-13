(ns baredom.components.x-color-picker.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-color-picker.model :as model]))

;; ── parse-hex ───────────────────────────────────────────────────────────────
(deftest parse-hex-6-digit-test
  (let [c (model/parse-hex "#ff0000")]
    (is (= 255 (:r c)))
    (is (= 0   (:g c)))
    (is (= 0   (:b c)))
    (is (= 1.0 (:a c)))))

(deftest parse-hex-3-digit-test
  (let [c (model/parse-hex "#f00")]
    (is (= 255 (:r c)))
    (is (= 0   (:g c)))
    (is (= 0   (:b c)))))

(deftest parse-hex-8-digit-test
  (let [c (model/parse-hex "#ff000080")]
    (is (= 255 (:r c)))
    (is (= 0   (:g c)))
    (is (= 0   (:b c)))
    (is (< (js/Math.abs (- (:a c) 0.502)) 0.01)))
  (testing "full opacity"
    (let [c (model/parse-hex "#ff0000ff")]
      (is (< (js/Math.abs (- (:a c) 1.0)) 0.01))))
  (testing "zero opacity"
    (let [c (model/parse-hex "#ff000000")]
      (is (< (js/Math.abs (:a c)) 0.01)))))

(deftest parse-hex-case-insensitive-test
  (is (some? (model/parse-hex "#FF0000")))
  (is (some? (model/parse-hex "#aaBBcc"))))

(deftest parse-hex-whitespace-test
  (is (some? (model/parse-hex "  #ff0000  "))))

(deftest parse-hex-invalid-test
  (is (nil? (model/parse-hex nil)))
  (is (nil? (model/parse-hex "")))
  (is (nil? (model/parse-hex "ff0000")))
  (is (nil? (model/parse-hex "#xyz")))
  (is (nil? (model/parse-hex "#ff00")))
  (is (nil? (model/parse-hex "not-a-colour"))))

;; ── valid-hex? ──────────────────────────────────────────────────────────────
(deftest valid-hex-test
  (is (true?  (model/valid-hex? "#abc")))
  (is (true?  (model/valid-hex? "#aabbcc")))
  (is (true?  (model/valid-hex? "#aabbccdd")))
  (is (false? (model/valid-hex? "abc")))
  (is (false? (model/valid-hex? nil)))
  (is (false? (model/valid-hex? "#ab"))))

;; ── rgb->hsl / hsl->rgb round-trip ──────────────────────────────────────────
(defn- approx=
  "Check that two numbers are within tolerance."
  [a b tol]
  (< (js/Math.abs (- a b)) tol))

(deftest rgb-hsl-roundtrip-red-test
  (let [{:keys [h s l]} (model/rgb->hsl 255 0 0)]
    (is (approx= h 0 1))
    (is (approx= s 100 1))
    (is (approx= l 50 1)))
  (let [{:keys [r g b]} (model/hsl->rgb 0 100 50)]
    (is (= r 255))
    (is (= g 0))
    (is (= b 0))))

(deftest rgb-hsl-roundtrip-green-test
  (let [{:keys [h s l]} (model/rgb->hsl 0 255 0)]
    (is (approx= h 120 1))
    (is (approx= s 100 1))
    (is (approx= l 50 1)))
  (let [{:keys [r g b]} (model/hsl->rgb 120 100 50)]
    (is (= r 0))
    (is (= g 255))
    (is (= b 0))))

(deftest rgb-hsl-roundtrip-blue-test
  (let [{:keys [h s l]} (model/rgb->hsl 0 0 255)]
    (is (approx= h 240 1))
    (is (approx= s 100 1))
    (is (approx= l 50 1))))

(deftest rgb-hsl-white-test
  (let [{:keys [s l]} (model/rgb->hsl 255 255 255)]
    (is (= s 0))
    (is (approx= l 100 1))))

(deftest rgb-hsl-black-test
  (let [{:keys [s l]} (model/rgb->hsl 0 0 0)]
    (is (= s 0))
    (is (approx= l 0 1))))

(deftest rgb-hsl-gray-test
  (let [{:keys [s l]} (model/rgb->hsl 128 128 128)]
    (is (= s 0))
    (is (approx= l 50 2))))

(deftest hsl-rgb-roundtrip-arbitrary-test
  (let [{:keys [r g b]} (model/hsl->rgb 210 80 50)
        {:keys [h s l]} (model/rgb->hsl r g b)]
    (is (approx= h 210 2))
    (is (approx= s 80 2))
    (is (approx= l 50 2))))

;; ── rgb->hex ────────────────────────────────────────────────────────────────
(deftest rgb-hex-test
  (is (= "#ff0000" (model/rgb->hex 255 0 0)))
  (is (= "#00ff00" (model/rgb->hex 0 255 0)))
  (is (= "#000000" (model/rgb->hex 0 0 0)))
  (is (= "#ffffff" (model/rgb->hex 255 255 255))))

(deftest rgba-hex8-test
  (let [hex (model/rgba->hex8 255 0 0 0.5)]
    (is (= "#ff000080" hex))))

;; ── hsl <-> hsv ─────────────────────────────────────────────────────────────
(deftest hsl-hsv-roundtrip-test
  (let [{:keys [h s v]} (model/hsl->hsv 210 80 50)
        {:keys [h s l]} (model/hsv->hsl h s v)]
    (is (approx= h 210 1))
    (is (approx= s 80 2))
    (is (approx= l 50 2))))

(deftest hsl-hsv-black-test
  (let [{:keys [v]} (model/hsl->hsv 0 0 0)]
    (is (approx= v 0 1))))

(deftest hsl-hsv-white-test
  (let [{:keys [s v]} (model/hsl->hsv 0 0 100)]
    (is (approx= v 100 1))
    (is (approx= s 0 1))))

(deftest hsl-hsv-pure-hue-test
  (let [{:keys [s v]} (model/hsl->hsv 0 100 50)]
    (is (approx= s 100 1))
    (is (approx= v 100 1))))

;; ── coordinate math ─────────────────────────────────────────────────────────
(deftest sat-val-xy-roundtrip-test
  (let [{:keys [x y]} (model/sat-val->xy-pct 75 50)
        {:keys [sat val]} (model/xy-pct->sat-val x y)]
    (is (approx= sat 75 0.1))
    (is (approx= val 50 0.1))))

(deftest hue-pct-roundtrip-test
  (is (approx= (model/pct->hue (model/hue->pct 180)) 180 0.1)))

(deftest alpha-pct-roundtrip-test
  (is (approx= (model/pct->alpha (model/alpha->pct 0.75)) 0.75 0.01)))

;; ── normalize-value ─────────────────────────────────────────────────────────
(deftest normalize-value-valid-test
  (is (= "#ff0000" (model/normalize-value "#ff0000")))
  (is (= "#ff0000" (model/normalize-value "#f00"))))

(deftest normalize-value-invalid-test
  (is (= "#000000" (model/normalize-value nil)))
  (is (= "#000000" (model/normalize-value "")))
  (is (= "#000000" (model/normalize-value "not-a-colour"))))

;; ── normalize-mode ──────────────────────────────────────────────────────────
(deftest normalize-mode-test
  (is (= "inline"  (model/normalize-mode nil)))
  (is (= "inline"  (model/normalize-mode "inline")))
  (is (= "popover" (model/normalize-mode "popover")))
  (is (= "inline"  (model/normalize-mode "bad"))))

;; ── parse-swatches ──────────────────────────────────────────────────────────
(deftest parse-swatches-test
  (is (= [] (model/parse-swatches nil)))
  (is (= [] (model/parse-swatches "")))
  (is (= ["#ff0000"] (model/parse-swatches "#ff0000")))
  (is (= ["#ff0000" "#00ff00"] (model/parse-swatches "#ff0000,#00ff00")))
  (testing "invalid entries are filtered"
    (is (= ["#ff0000"] (model/parse-swatches "#ff0000,bad,#zzzzzz")))))

;; ── derive-state defaults ───────────────────────────────────────────────────
(deftest derive-state-defaults-test
  (let [s (model/derive-state {})]
    (is (= "#000000" (:hex s)))
    (is (= "inline"  (:mode s)))
    (is (false? (:disabled? s)))
    (is (false? (:readonly? s)))
    (is (false? (:alpha? s)))
    (is (false? (:open? s)))
    (is (= 1.0 (:a s)))))

(deftest derive-state-popover-open-test
  (let [s (model/derive-state {:mode "popover" :open ""})]
    (is (= "popover" (:mode s)))
    (is (true? (:open? s)))))

(deftest derive-state-popover-open-requires-popover-mode-test
  (let [s (model/derive-state {:mode "inline" :open ""})]
    (is (false? (:open? s)))))

;; ── color-value-text ────────────────────────────────────────────────────────
(deftest color-value-text-test
  (is (= "Hue 210, Saturation 80%, Lightness 50%"
         (model/color-value-text 210 80 50 1.0)))
  (is (= "Hue 0, Saturation 0%, Lightness 0%, Opacity 50%"
         (model/color-value-text 0 0 0 0.5))))

;; ── interactable? ───────────────────────────────────────────────────────────
(deftest interactable-test
  (is (true?  (model/interactable? {:disabled? false :readonly? false})))
  (is (false? (model/interactable? {:disabled? true  :readonly? false})))
  (is (false? (model/interactable? {:disabled? false :readonly? true}))))

;; ── edge cases — clamping ───────────────────────────────────────────────────
(deftest clamp-hue-edge-cases-test
  (testing "negative hue wraps"
    (is (approx= (model/clamp-hue -30) 330 0.1)))
  (testing "hue > 360 wraps"
    (is (approx= (model/clamp-hue 400) 40 0.1)))
  (testing "hue exactly 360 wraps to 0"
    (is (approx= (model/clamp-hue 360) 0 0.1))))

(deftest clamp-percent-edge-cases-test
  (is (= 0   (model/clamp-percent -10)))
  (is (= 100 (model/clamp-percent 150)))
  (is (= 50  (model/clamp-percent 50))))

(deftest clamp-alpha-edge-cases-test
  (is (= 0.0 (model/clamp-alpha -0.5)))
  (is (= 1.0 (model/clamp-alpha 1.5)))
  (is (approx= (model/clamp-alpha 0.75) 0.75 0.001)))

;; ── edge cases — rgb->hex clamping ──────────────────────────────────────────
(deftest rgb-hex-clamps-values-test
  (is (= "#ff0000" (model/rgb->hex 300 -10 0)))
  (is (= "#000000" (model/rgb->hex 0 0 0))))

;; ── derive-state with swatches ──────────────────────────────────────────────
(deftest derive-state-with-swatches-test
  (let [s (model/derive-state {:swatches "#ff0000,#00ff00"})]
    (is (= ["#ff0000" "#00ff00"] (:swatches s))))
  (testing "invalid swatches filtered"
    (let [s (model/derive-state {:swatches "#ff0000,bad"})]
      (is (= ["#ff0000"] (:swatches s)))))
  (testing "nil swatches → empty"
    (let [s (model/derive-state {})]
      (is (= [] (:swatches s))))))
