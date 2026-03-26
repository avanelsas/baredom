(ns app.components.x-divider.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [app.components.x-divider.model :as model]))

;; ── parse-orientation ─────────────────────────────────────────────────────
(deftest parse-orientation-valid-test
  (is (= "horizontal" (model/parse-orientation "horizontal")))
  (is (= "vertical"   (model/parse-orientation "vertical"))))

(deftest parse-orientation-default-test
  (is (= "horizontal" (model/parse-orientation nil)))
  (is (= "horizontal" (model/parse-orientation "")))
  (is (= "horizontal" (model/parse-orientation "diagonal"))))

(deftest parse-orientation-case-insensitive-test
  (is (= "horizontal" (model/parse-orientation "HORIZONTAL")))
  (is (= "vertical"   (model/parse-orientation "Vertical"))))

;; ── parse-variant ─────────────────────────────────────────────────────────
(deftest parse-variant-valid-test
  (is (= "solid"  (model/parse-variant "solid")))
  (is (= "dashed" (model/parse-variant "dashed")))
  (is (= "dotted" (model/parse-variant "dotted"))))

(deftest parse-variant-default-test
  (is (= "solid" (model/parse-variant nil)))
  (is (= "solid" (model/parse-variant "")))
  (is (= "solid" (model/parse-variant "wavy"))))

(deftest parse-variant-case-insensitive-test
  (is (= "dashed" (model/parse-variant "DASHED")))
  (is (= "dotted" (model/parse-variant "Dotted"))))

;; ── parse-align ───────────────────────────────────────────────────────────
(deftest parse-align-valid-test
  (is (= "center" (model/parse-align "center")))
  (is (= "start"  (model/parse-align "start")))
  (is (= "end"    (model/parse-align "end"))))

(deftest parse-align-default-test
  (is (= "center" (model/parse-align nil)))
  (is (= "center" (model/parse-align "")))
  (is (= "center" (model/parse-align "left"))))

(deftest parse-align-case-insensitive-test
  (is (= "start" (model/parse-align "START")))
  (is (= "end"   (model/parse-align "END"))))

;; ── separator-role? ───────────────────────────────────────────────────────
(deftest separator-role-nil-test
  (is (true? (model/separator-role? nil))))

(deftest separator-role-separator-test
  (is (true? (model/separator-role? "separator"))))

(deftest separator-role-presentation-test
  (is (false? (model/separator-role? "presentation"))))

(deftest separator-role-none-test
  (is (false? (model/separator-role? "none"))))

(deftest separator-role-other-test
  (is (true? (model/separator-role? "region"))))

;; ── has-label? ────────────────────────────────────────────────────────────
(deftest has-label-valid-test
  (is (true? (model/has-label? "Section")))
  (is (true? (model/has-label? "  A  "))))

(deftest has-label-empty-test
  (is (false? (model/has-label? "")))
  (is (false? (model/has-label? "   ")))
  (is (false? (model/has-label? nil))))

(deftest has-label-non-string-test
  (is (false? (model/has-label? 42)))
  (is (false? (model/has-label? true))))

;; ── normalize ─────────────────────────────────────────────────────────────
(deftest normalize-defaults-test
  (let [m (model/normalize {})]
    (is (= "horizontal" (:orientation m)))
    (is (= "solid"      (:variant m)))
    (is (= "center"     (:align m)))
    (is (nil?           (:thickness m)))
    (is (nil?           (:color m)))
    (is (nil?           (:inset m)))
    (is (nil?           (:length m)))
    (is (nil?           (:label m)))
    (is (nil?           (:role m)))
    (is (nil?           (:aria-label m)))))

(deftest normalize-full-test
  (let [m (model/normalize
           {:orientation-raw "vertical"
            :variant-raw     "dashed"
            :align-raw       "start"
            :thickness-raw   "2px"
            :color-raw       "#f00"
            :inset-raw       "16px"
            :length-raw      "200px"
            :label-raw       "Section"
            :role-raw        "presentation"
            :aria-label-raw  "Divides sections"})]
    (is (= "vertical"       (:orientation m)))
    (is (= "dashed"         (:variant m)))
    (is (= "start"          (:align m)))
    (is (= "2px"            (:thickness m)))
    (is (= "#f00"           (:color m)))
    (is (= "16px"           (:inset m)))
    (is (= "200px"          (:length m)))
    (is (= "Section"        (:label m)))
    (is (= "presentation"   (:role m)))
    (is (= "Divides sections" (:aria-label m)))))

(deftest normalize-invalid-enums-fall-to-defaults-test
  (let [m (model/normalize {:orientation-raw "diagonal"
                            :variant-raw     "wavy"
                            :align-raw       "left"})]
    (is (= "horizontal" (:orientation m)))
    (is (= "solid"      (:variant m)))
    (is (= "center"     (:align m)))))
