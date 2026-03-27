(ns baredom.components.x-container.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-container.model :as model]))

(deftest normalize-as-test
  (testing "normalize-as preserves valid values"
    (is (= "div" (model/normalize-as "div")))
    (is (= "section" (model/normalize-as "section")))
    (is (= "article" (model/normalize-as "article")))
    (is (= "main" (model/normalize-as "main")))
    (is (= "aside" (model/normalize-as "aside")))
    (is (= "header" (model/normalize-as "header")))
    (is (= "footer" (model/normalize-as "footer")))
    (is (= "nav" (model/normalize-as "nav"))))
  (testing "normalize-as falls back for invalid values"
    (is (= "div" (model/normalize-as nil)))
    (is (= "div" (model/normalize-as "")))
    (is (= "div" (model/normalize-as "span")))))

(deftest normalize-size-test
  (testing "normalize-size preserves valid values"
    (is (= "xs" (model/normalize-size "xs")))
    (is (= "sm" (model/normalize-size "sm")))
    (is (= "md" (model/normalize-size "md")))
    (is (= "lg" (model/normalize-size "lg")))
    (is (= "xl" (model/normalize-size "xl")))
    (is (= "full" (model/normalize-size "full"))))
  (testing "normalize-size falls back for invalid values"
    (is (= "lg" (model/normalize-size nil)))
    (is (= "lg" (model/normalize-size "")))
    (is (= "lg" (model/normalize-size "2xl")))))

(deftest normalize-padding-test
  (testing "normalize-padding preserves valid values"
    (is (= "none" (model/normalize-padding "none")))
    (is (= "sm" (model/normalize-padding "sm")))
    (is (= "md" (model/normalize-padding "md")))
    (is (= "lg" (model/normalize-padding "lg"))))
  (testing "normalize-padding falls back for invalid values"
    (is (= "md" (model/normalize-padding nil)))
    (is (= "md" (model/normalize-padding "")))
    (is (= "md" (model/normalize-padding "xl")))))

(deftest public-state-test
  (testing "public-state normalizes values and booleans"
    (is
     (= {:as "div"
         :size "lg"
         :padding "md"
         :center false
         :fluid false
         :label nil}
        (model/public-state
         {:as nil
          :size nil
          :padding nil
          :center nil
          :fluid nil
          :label nil})))

    (is
     (= {:as "section"
         :size "sm"
         :padding "lg"
         :center true
         :fluid true
         :label "Content region"}
        (model/public-state
         {:as "section"
          :size "sm"
          :padding "lg"
          :center true
          :fluid true
          :label "Content region"})))

    (is
     (= {:as "div"
         :size "lg"
         :padding "md"
         :center true
         :fluid false
         :label nil}
        (model/public-state
         {:as "weird"
          :size "huge"
          :padding "tiny"
          :center 1
          :fluid nil
          :label ""})))))
