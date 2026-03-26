(ns app.components.x-progress.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [app.components.x-progress.model :as model]))

;; ── normalize-max ─────────────────────────────────────────────────────────
(deftest normalize-max-valid-test
  (is (= 100  (model/normalize-max "100")))
  (is (= 50   (model/normalize-max "50")))
  (is (= 200  (model/normalize-max "200"))))

(deftest normalize-max-defaults-test
  (is (= 100 (model/normalize-max nil)))
  (is (= 100 (model/normalize-max "")))
  (is (= 100 (model/normalize-max "abc")))
  (is (= 100 (model/normalize-max "0")))
  (is (= 100 (model/normalize-max "-5"))))

;; ── normalize-value ───────────────────────────────────────────────────────
(deftest normalize-value-valid-test
  (is (= 0   (model/normalize-value "0"   100)))
  (is (= 50  (model/normalize-value "50"  100)))
  (is (= 100 (model/normalize-value "100" 100))))

(deftest normalize-value-clamped-test
  (is (= 0   (model/normalize-value "-10" 100)))
  (is (= 100 (model/normalize-value "150" 100))))

(deftest normalize-value-defaults-test
  (is (= 0 (model/normalize-value nil 100)))
  (is (= 0 (model/normalize-value ""  100)))
  (is (= 0 (model/normalize-value "bad" 100))))

;; ── normalize-variant ─────────────────────────────────────────────────────
(deftest normalize-variant-valid-test
  (doseq [v ["default" "success" "warning" "danger"]]
    (is (= v (model/normalize-variant v)))))

(deftest normalize-variant-default-test
  (is (= "default" (model/normalize-variant nil)))
  (is (= "default" (model/normalize-variant "")))
  (is (= "default" (model/normalize-variant "invalid"))))

;; ── normalize-size ────────────────────────────────────────────────────────
(deftest normalize-size-valid-test
  (doseq [v ["sm" "md" "lg"]]
    (is (= v (model/normalize-size v)))))

(deftest normalize-size-default-test
  (is (= "md" (model/normalize-size nil)))
  (is (= "md" (model/normalize-size "xl"))))

;; ── derive-state ──────────────────────────────────────────────────────────
(deftest derive-state-defaults-test
  (let [s (model/derive-state {})]
    (is (= 0       (:value s)))
    (is (= 100     (:max s)))
    (is (= 0.0     (:percent s)))
    (is (= "default" (:variant s)))
    (is (= "md"    (:size s)))
    (is (nil?      (:label s)))
    (is (false?    (:show-value s)))
    (is (false?    (:indeterminate s)))
    (is (= "0%"    (:aria-valuetext s)))))

(deftest derive-state-percent-test
  (let [s (model/derive-state {:value "75" :max "100"})]
    (is (= 75  (:value s)))
    (is (= 100 (:max s)))
    (is (= 75.0 (:percent s)))
    (is (= "75%" (:aria-valuetext s)))))

(deftest derive-state-indeterminate-test
  (let [s (model/derive-state {:indeterminate true})]
    (is (true?  (:indeterminate s)))
    (is (= 50   (:percent s)))
    (is (= "Loading\u2026" (:aria-valuetext s)))))

(deftest derive-state-label-empty-string-test
  (is (nil? (:label (model/derive-state {:label ""}))))
  (is (= "My label" (:label (model/derive-state {:label "My label"})))))

(deftest derive-state-clamped-percent-test
  (let [s (model/derive-state {:value "200" :max "100"})]
    (is (= 100   (:value s)))
    (is (= 100.0 (:percent s)))))
