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

(deftest normalize-defaults
  (let [state (model/normalize {})]
    (is (= false (:open state)))
    (is (= "bottom-start" (:placement state)))
    (is (= "" (:label state)))))

(deftest normalize-open-flag
  (is (= true  (:open (model/normalize {:open true}))))
  (is (= false (:open (model/normalize {:open false}))))
  (is (= false (:open (model/normalize {:open nil})))))

(deftest normalize-label
  (is (= "My Menu" (:label (model/normalize {:label "My Menu"}))))
  (is (= "" (:label (model/normalize {:label nil})))))

(deftest normalize-normalizes-placement
  (is (= "top-end"      (:placement (model/normalize {:placement "top-end"}))))
  (is (= "bottom-start" (:placement (model/normalize {:placement "invalid"})))))
