(ns app.components.x-menu-item.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [app.components.x-menu-item.model :as model]))

(deftest normalize-variant-valid
  (is (= "danger" (model/normalize-variant "danger")))
  (is (= "" (model/normalize-variant "")))
  (is (= "" (model/normalize-variant nil))))

(deftest normalize-variant-invalid
  (is (= "" (model/normalize-variant "bad")))
  (is (= "" (model/normalize-variant "warning"))))

(deftest normalize-type-valid
  (is (= "divider" (model/normalize-type "divider")))
  (is (= "" (model/normalize-type "")))
  (is (= "" (model/normalize-type nil))))

(deftest normalize-type-invalid
  (is (= "" (model/normalize-type "separator")))
  (is (= "" (model/normalize-type "hr"))))

(deftest derive-state-defaults
  (let [state (model/derive-state {})]
    (is (= "" (:value state)))
    (is (= false (:disabled state)))
    (is (= "" (:variant state)))
    (is (= "" (:type state)))))

(deftest derive-state-disabled-flag
  (is (= true (:disabled (model/derive-state {:disabled true}))))
  (is (= false (:disabled (model/derive-state {:disabled false}))))
  (is (= false (:disabled (model/derive-state {:disabled nil})))))

(deftest derive-state-normalizes-variant
  (is (= "danger" (:variant (model/derive-state {:variant "danger"}))))
  (is (= "" (:variant (model/derive-state {:variant "invalid"})))))

(deftest derive-state-normalizes-type
  (is (= "divider" (:type (model/derive-state {:type "divider"}))))
  (is (= "" (:type (model/derive-state {:type "separator"})))))
