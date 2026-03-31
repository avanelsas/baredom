(ns baredom.components.x-tabs.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-tabs.model :as model]))

;; ---------- valid-enum ----------

(deftest valid-enum-returns-value-when-in-allowed-set
  (is (= "horizontal" (model/valid-enum "horizontal" #{"horizontal" "vertical"} "horizontal")))
  (is (= "vertical" (model/valid-enum "vertical" #{"horizontal" "vertical"} "horizontal"))))

(deftest valid-enum-returns-fallback-for-unknown
  (is (= "horizontal" (model/valid-enum "bad" #{"horizontal" "vertical"} "horizontal"))))

(deftest valid-enum-returns-fallback-for-nil
  (is (= "auto" (model/valid-enum nil #{"auto" "manual"} "auto"))))

(deftest valid-enum-returns-fallback-for-empty-string
  (is (= "horizontal" (model/valid-enum "" #{"horizontal" "vertical"} "horizontal"))))

;; ---------- normalize-orientation ----------

(deftest orientation-accepts-valid-values
  (is (= "horizontal" (model/normalize-orientation "horizontal")))
  (is (= "vertical" (model/normalize-orientation "vertical"))))

(deftest orientation-falls-back-on-invalid
  (is (= "horizontal" (model/normalize-orientation "bad")))
  (is (= "horizontal" (model/normalize-orientation nil)))
  (is (= "horizontal" (model/normalize-orientation ""))))

;; ---------- normalize-activation ----------

(deftest activation-accepts-valid-values
  (is (= "auto" (model/normalize-activation "auto")))
  (is (= "manual" (model/normalize-activation "manual"))))

(deftest activation-falls-back-on-invalid
  (is (= "auto" (model/normalize-activation "bad")))
  (is (= "auto" (model/normalize-activation nil)))
  (is (= "auto" (model/normalize-activation ""))))

;; ---------- derive-state ----------

(deftest derive-state-defaults
  (let [state (model/derive-state {})]
    (is (nil? (:value state)))
    (is (= "horizontal" (:orientation state)))
    (is (= "auto" (:activation state)))
    (is (nil? (:label state)))
    (is (= false (:loop state)))))

(deftest derive-state-passes-through-value-and-label
  (let [state (model/derive-state {:value "overview" :label "Main tabs"})]
    (is (= "overview" (:value state)))
    (is (= "Main tabs" (:label state)))))

(deftest derive-state-normalizes-enums
  (let [state (model/derive-state {:orientation "bad" :activation "bad"})]
    (is (= "horizontal" (:orientation state)))
    (is (= "auto" (:activation state)))))

(deftest derive-state-passes-through-valid-enums
  (let [state (model/derive-state {:orientation "vertical" :activation "manual"})]
    (is (= "vertical" (:orientation state)))
    (is (= "manual" (:activation state)))))

(deftest derive-state-coerces-loop-to-boolean
  (is (= true (:loop (model/derive-state {:loop true}))))
  (is (= true (:loop (model/derive-state {:loop "yes"}))))
  (is (= false (:loop (model/derive-state {:loop nil}))))
  (is (= false (:loop (model/derive-state {:loop false}))))
  (is (= false (:loop (model/derive-state {})))))
