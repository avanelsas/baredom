(ns baredom.components.x-skeleton.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-skeleton.model :as model]))

;; ── normalize-variant ─────────────────────────────────────────────────────
(deftest normalize-variant-valid-test
  (doseq [v ["rect" "text" "circle"]]
    (is (= v (model/normalize-variant v)))))

(deftest normalize-variant-default-test
  (is (= "rect" (model/normalize-variant nil)))
  (is (= "rect" (model/normalize-variant "")))
  (is (= "rect" (model/normalize-variant "square")))
  (is (= "rect" (model/normalize-variant 42))))

;; ── normalize-animation ───────────────────────────────────────────────────
(deftest normalize-animation-valid-test
  (doseq [v ["pulse" "wave" "none"]]
    (is (= v (model/normalize-animation v)))))

(deftest normalize-animation-default-test
  (is (= "pulse" (model/normalize-animation nil)))
  (is (= "pulse" (model/normalize-animation "")))
  (is (= "pulse" (model/normalize-animation "shimmer")))
  (is (= "pulse" (model/normalize-animation false))))

;; ── normalize-css-value ───────────────────────────────────────────────────
(deftest normalize-css-value-valid-test
  (is (= "100px"  (model/normalize-css-value "100px")))
  (is (= "50%"    (model/normalize-css-value "50%")))
  (is (= "2.5rem" (model/normalize-css-value "2.5rem"))))

(deftest normalize-css-value-nil-for-blanks-test
  (is (nil? (model/normalize-css-value nil)))
  (is (nil? (model/normalize-css-value "")))
  (is (nil? (model/normalize-css-value 0))))

;; ── derive-state ──────────────────────────────────────────────────────────
(deftest derive-state-defaults-test
  (let [s (model/derive-state {})]
    (is (= "rect"  (:variant s)))
    (is (= "pulse" (:animation s)))
    (is (nil?      (:width s)))
    (is (nil?      (:height s)))))

(deftest derive-state-explicit-values-test
  (let [s (model/derive-state {:variant "circle" :animation "wave"
                                :width "40px" :height "40px"})]
    (is (= "circle" (:variant s)))
    (is (= "wave"   (:animation s)))
    (is (= "40px"   (:width s)))
    (is (= "40px"   (:height s)))))

(deftest derive-state-invalid-variant-falls-back-test
  (let [s (model/derive-state {:variant "oval" :animation "bounce"})]
    (is (= "rect"  (:variant s)))
    (is (= "pulse" (:animation s)))))

(deftest derive-state-empty-width-height-become-nil-test
  (let [s (model/derive-state {:width "" :height ""})]
    (is (nil? (:width s)))
    (is (nil? (:height s)))))
