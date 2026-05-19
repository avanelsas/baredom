(ns baredom.components.x-spotlight-card.model-test
  (:require
   [cljs.test :refer [deftest is testing]]
   [baredom.components.x-spotlight-card.model :as model]))

(deftest enum-normalization-test
  (testing "variant"
    (is (= "elevated" (model/normalize-variant nil)))
    (is (= "elevated" (model/normalize-variant "")))
    (is (= "elevated" (model/normalize-variant "bad")))
    (is (= "bordered" (model/normalize-variant "bordered"))))

  (testing "radius"
    (is (= "lg" (model/normalize-radius nil)))
    (is (= "lg" (model/normalize-radius "bad")))
    (is (= "xl" (model/normalize-radius "xl")))
    (is (= "none" (model/normalize-radius "none"))))

  (testing "padding"
    (is (= "md" (model/normalize-padding nil)))
    (is (= "md" (model/normalize-padding "bad")))
    (is (= "lg" (model/normalize-padding "lg"))))

  (testing "color"
    (is (= "primary" (model/normalize-color nil)))
    (is (= "primary" (model/normalize-color "bad")))
    (doseq [c ["primary" "secondary" "success" "warning"
               "danger" "info" "accent" "neutral"]]
      (is (= c (model/normalize-color c)))))

  (testing "intensity"
    (is (= "medium" (model/normalize-intensity nil)))
    (is (= "soft"   (model/normalize-intensity "soft")))
    (is (= "strong" (model/normalize-intensity "strong"))))

  (testing "size"
    (is (= "md" (model/normalize-size nil)))
    (is (= "sm" (model/normalize-size "sm")))
    (is (= "xl" (model/normalize-size "xl")))))

(deftest css-resolvers-test
  (testing "color-css-value resolves to a var(...) expression"
    (is (= "var(--x-color-primary, #2563eb)" (model/color-css-value "primary")))
    (is (= "var(--x-color-danger, #dc2626)"  (model/color-css-value "danger")))
    (is (= "var(--x-color-text, #111827)"    (model/color-css-value "neutral")))
    (is (= "var(--x-color-primary, #2563eb)" (model/color-css-value "bogus"))))

  (testing "intensity-css-value resolves to a numeric opacity string"
    (is (= "0.10" (model/intensity-css-value "soft")))
    (is (= "0.18" (model/intensity-css-value "medium")))
    (is (= "0.28" (model/intensity-css-value "strong")))
    (is (= "0.18" (model/intensity-css-value "bogus"))))

  (testing "size-css-value resolves to a px length"
    (is (= "140px" (model/size-css-value "sm")))
    (is (= "200px" (model/size-css-value "md")))
    (is (= "360px" (model/size-css-value "xl")))))

(deftest normalize-default-test
  (let [state (model/normalize {:motion-ok? true})]
    (is (= "elevated" (:variant state)))
    (is (= "lg"       (:radius state)))
    (is (= "md"       (:padding state)))
    (is (= "primary"  (:color state)))
    (is (= "medium"   (:intensity state)))
    (is (= "md"       (:size state)))
    (is (= false      (:static? state)))
    (is (= "var(--x-color-primary, #2563eb)" (:color-css state)))
    (is (= "0.18"  (:intensity-css state)))
    (is (= "200px" (:size-css state)))))

(deftest normalize-static-flag-test
  (testing "explicit static attribute forces static spotlight"
    (is (true? (:static? (model/normalize {:static? true :motion-ok? true})))))

  (testing "reduced motion forces static spotlight even without the attribute"
    (is (true? (:static? (model/normalize {:static? false :motion-ok? false})))))

  (testing "no static attribute and motion ok → not static"
    (is (false? (:static? (model/normalize {:static? false :motion-ok? true}))))))

(deftest normalize-resolves-css-strings-test
  (let [state (model/normalize {:color "danger"
                                   :intensity "strong"
                                   :size "lg"
                                   :motion-ok? true})]
    (is (= "var(--x-color-danger, #dc2626)" (:color-css state)))
    (is (= "0.28"  (:intensity-css state)))
    (is (= "280px" (:size-css state)))))
