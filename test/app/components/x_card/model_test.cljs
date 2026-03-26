(ns app.components.x-card.model-test
  (:require
   [cljs.test :refer [deftest is testing]]
   [app.components.x-card.model :as model]))

(deftest enum-normalization-test
  (testing "variant normalization"
    (is (= "elevated" (model/normalize-variant nil)))
    (is (= "elevated" (model/normalize-variant "bad")))
    (is (= "outlined" (model/normalize-variant "outlined"))))

  (testing "padding normalization"
    (is (= "md" (model/normalize-padding nil)))
    (is (= "md" (model/normalize-padding "bad")))
    (is (= "lg" (model/normalize-padding "lg"))))

  (testing "radius normalization"
    (is (= "lg" (model/normalize-radius nil)))
    (is (= "lg" (model/normalize-radius "bad")))
    (is (= "xl" (model/normalize-radius "xl")))))

(deftest derive-state-test
  (testing "default state"
    (is (= {:variant "elevated"
            :padding "md"
            :radius "lg"
            :interactive false
            :disabled false
            :role nil
            :tabindex nil
            :aria-label nil
            :aria-disabled nil}
           (model/derive-state {}))))

  (testing "interactive enabled state"
    (is (= "button" (:role (model/derive-state {:interactive true}))))
    (is (= "0" (:tabindex (model/derive-state {:interactive true}))))
    (is (nil? (:aria-disabled (model/derive-state {:interactive true})))))

  (testing "interactive disabled state"
    (let [state (model/derive-state {:interactive true :disabled true :label "Card"})]
      (is (= "button" (:role state)))
      (is (= "-1" (:tabindex state)))
      (is (= "true" (:aria-disabled state)))
      (is (= "Card" (:aria-label state)))))

  (testing "empty label is omitted"
    (is (nil? (:aria-label (model/derive-state {:label ""}))))))
