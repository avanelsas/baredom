(ns baredom.components.x-menu.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [baredom.components.x-menu.model :as model]))

(deftest normalize-placement-valid
  (is (= "bottom-start" (model/normalize-placement "bottom-start")))
  (is (= "bottom-end"   (model/normalize-placement "bottom-end")))
  (is (= "top-start"    (model/normalize-placement "top-start")))
  (is (= "top-end"      (model/normalize-placement "top-end"))))

(deftest normalize-placement-invalid
  (is (= "bottom-start" (model/normalize-placement "bad")))
  (is (= "bottom-start" (model/normalize-placement nil)))
  (is (= "bottom-start" (model/normalize-placement ""))))

(deftest derive-state-defaults
  (let [state (model/derive-state {})]
    (is (= false (:open state)))
    (is (= "bottom-start" (:placement state)))
    (is (= "" (:label state)))))

(deftest derive-state-open-flag
  (is (= true  (:open (model/derive-state {:open true}))))
  (is (= false (:open (model/derive-state {:open false}))))
  (is (= false (:open (model/derive-state {:open nil})))))

(deftest derive-state-label
  (is (= "My Menu" (:label (model/derive-state {:label "My Menu"}))))
  (is (= "" (:label (model/derive-state {:label nil})))))

(deftest derive-state-normalizes-placement
  (is (= "top-end"      (:placement (model/derive-state {:placement "top-end"}))))
  (is (= "bottom-start" (:placement (model/derive-state {:placement "invalid"})))))
