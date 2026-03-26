(ns app.components.x-spinner.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [app.components.x-spinner.model :as model]))

;; ── normalize-size ────────────────────────────────────────────────────────
(deftest normalize-size-valid-test
  (doseq [v ["xs" "sm" "md" "lg" "xl"]]
    (is (= v (model/normalize-size v)))))

(deftest normalize-size-default-test
  (is (= "md" (model/normalize-size nil)))
  (is (= "md" (model/normalize-size "")))
  (is (= "md" (model/normalize-size "invalid")))
  (is (= "md" (model/normalize-size "XL"))))

;; ── normalize-variant ─────────────────────────────────────────────────────
(deftest normalize-variant-valid-test
  (doseq [v ["default" "primary" "success" "warning" "danger"]]
    (is (= v (model/normalize-variant v)))))

(deftest normalize-variant-default-test
  (is (= "default" (model/normalize-variant nil)))
  (is (= "default" (model/normalize-variant "")))
  (is (= "default" (model/normalize-variant "info")))
  (is (= "default" (model/normalize-variant "PRIMARY"))))

;; ── normalize-label ───────────────────────────────────────────────────────
(deftest normalize-label-present-test
  (is (= "Saving"   (model/normalize-label "Saving")))
  (is (= "Please wait" (model/normalize-label "  Please wait  "))))

(deftest normalize-label-absent-test
  (is (= "Loading" (model/normalize-label nil)))
  (is (= "Loading" (model/normalize-label "")))
  (is (= "Loading" (model/normalize-label "   "))))

;; ── derive-state ──────────────────────────────────────────────────────────
(deftest derive-state-defaults-test
  (let [s (model/derive-state {})]
    (is (= "md"      (:size s)))
    (is (= "default" (:variant s)))
    (is (= "Loading" (:label s)))))

(deftest derive-state-explicit-values-test
  (let [s (model/derive-state {:size "lg" :variant "primary" :label "Uploading"})]
    (is (= "lg"        (:size s)))
    (is (= "primary"   (:variant s)))
    (is (= "Uploading" (:label s)))))

(deftest derive-state-invalid-values-fall-back-test
  (let [s (model/derive-state {:size "huge" :variant "info" :label ""})]
    (is (= "md"      (:size s)))
    (is (= "default" (:variant s)))
    (is (= "Loading" (:label s)))))
