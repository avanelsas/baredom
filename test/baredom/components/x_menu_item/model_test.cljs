(ns baredom.components.x-menu-item.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-menu-item.model :as model]))

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

(deftest normalize-defaults
  (let [state (model/normalize {})]
    (is (= "" (:value state)))
    (is (= false (:disabled state)))
    (is (= "" (:variant state)))
    (is (= "" (:type state)))))

(deftest normalize-disabled-flag
  (is (= true (:disabled (model/normalize {:disabled true}))))
  (is (= false (:disabled (model/normalize {:disabled false}))))
  (is (= false (:disabled (model/normalize {:disabled nil})))))

(deftest normalize-normalizes-variant
  (is (= "danger" (:variant (model/normalize {:variant "danger"}))))
  (is (= "" (:variant (model/normalize {:variant "invalid"})))))

(deftest normalize-normalizes-type
  (is (= "divider" (:type (model/normalize {:type "divider"}))))
  (is (= "" (:type (model/normalize {:type "separator"})))))
