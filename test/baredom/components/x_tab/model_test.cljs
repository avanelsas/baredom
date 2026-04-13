(ns baredom.components.x-tab.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-tab.model :as model]))

;; ---------- valid-enum ----------

(deftest valid-enum-returns-value-when-in-allowed-set
  (is (= "horizontal" (model/valid-enum "horizontal" #{"horizontal" "vertical"} "horizontal")))
  (is (= "vertical" (model/valid-enum "vertical" #{"horizontal" "vertical"} "horizontal"))))

(deftest valid-enum-returns-fallback-for-unknown
  (is (= "horizontal" (model/valid-enum "bad" #{"horizontal" "vertical"} "horizontal"))))

(deftest valid-enum-returns-fallback-for-nil
  (is (= "md" (model/valid-enum nil #{"sm" "md" "lg"} "md"))))

;; ---------- normalize-orientation ----------

(deftest orientation-accepts-valid-values
  (is (= "horizontal" (model/normalize-orientation "horizontal")))
  (is (= "vertical" (model/normalize-orientation "vertical"))))

(deftest orientation-falls-back-on-invalid
  (is (= "horizontal" (model/normalize-orientation "bad")))
  (is (= "horizontal" (model/normalize-orientation nil)))
  (is (= "horizontal" (model/normalize-orientation ""))))

;; ---------- normalize-size ----------

(deftest size-accepts-valid-values
  (is (= "sm" (model/normalize-size "sm")))
  (is (= "md" (model/normalize-size "md")))
  (is (= "lg" (model/normalize-size "lg"))))

(deftest size-falls-back-on-invalid
  (is (= "md" (model/normalize-size "bad")))
  (is (= "md" (model/normalize-size nil)))
  (is (= "md" (model/normalize-size ""))))

;; ---------- normalize-variant ----------

(deftest variant-accepts-valid-values
  (is (= "default" (model/normalize-variant "default")))
  (is (= "underline" (model/normalize-variant "underline")))
  (is (= "pill" (model/normalize-variant "pill"))))

(deftest variant-falls-back-on-invalid
  (is (= "default" (model/normalize-variant "bad")))
  (is (= "default" (model/normalize-variant nil)))
  (is (= "default" (model/normalize-variant ""))))

;; ---------- derive-state ----------

(deftest derive-state-defaults
  (let [state (model/derive-state {})]
    (is (= false (:selected state)))
    (is (= false (:disabled state)))
    (is (= "horizontal" (:orientation state)))
    (is (= "md" (:size state)))
    (is (= "default" (:variant state)))
    (is (= "-1" (:tabindex state)))
    (is (nil? (:label state)))
    (is (nil? (:controls state)))))

(deftest derive-state-selected-sets-tabindex-0
  (let [state (model/derive-state {:selected true})]
    (is (= true (:selected state)))
    (is (= "0" (:tabindex state)))))

(deftest derive-state-disabled-forces-tabindex-neg1
  (let [state (model/derive-state {:disabled true})]
    (is (= true (:disabled state)))
    (is (= "-1" (:tabindex state)))))

(deftest derive-state-selected-and-disabled-tabindex-neg1
  (let [state (model/derive-state {:selected true :disabled true})]
    (is (= true (:selected state)))
    (is (= true (:disabled state)))
    (is (= "-1" (:tabindex state)))))

(deftest derive-state-normalizes-enums
  (let [state (model/derive-state {:orientation "bad"
                                   :size "bad"
                                   :variant "bad"})]
    (is (= "horizontal" (:orientation state)))
    (is (= "md" (:size state)))
    (is (= "default" (:variant state)))))

(deftest derive-state-passes-through-valid-enums
  (let [state (model/derive-state {:orientation "vertical"
                                   :size "lg"
                                   :variant "pill"})]
    (is (= "vertical" (:orientation state)))
    (is (= "lg" (:size state)))
    (is (= "pill" (:variant state)))))

(deftest derive-state-passes-through-label-and-controls
  (let [state (model/derive-state {:label "My tab" :controls "panel-1"})]
    (is (= "My tab" (:label state)))
    (is (= "panel-1" (:controls state)))))
