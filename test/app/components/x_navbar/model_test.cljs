(ns app.components.x-navbar.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [app.components.x-navbar.model :as model]))

(deftest normalize-orientation-test
  (testing "normalize-orientation preserves valid values and falls back for invalid ones"
    (is (= "horizontal" (model/normalize-orientation nil)))
    (is (= "horizontal" (model/normalize-orientation "horizontal")))
    (is (= "vertical" (model/normalize-orientation "vertical")))
    (is (= "horizontal" (model/normalize-orientation "oops")))))

(deftest normalize-variant-test
  (testing "normalize-variant preserves valid values and falls back for invalid ones"
    (is (= "default" (model/normalize-variant nil)))
    (is (= "default" (model/normalize-variant "default")))
    (is (= "subtle" (model/normalize-variant "subtle")))
    (is (= "inverted" (model/normalize-variant "inverted")))
    (is (= "transparent" (model/normalize-variant "transparent")))
    (is (= "default" (model/normalize-variant "oops")))))

(deftest normalize-breakpoint-test
  (testing "normalize-breakpoint preserves valid values and falls back for invalid ones"
    (is (= "md" (model/normalize-breakpoint nil)))
    (is (= "sm" (model/normalize-breakpoint "sm")))
    (is (= "md" (model/normalize-breakpoint "md")))
    (is (= "lg" (model/normalize-breakpoint "lg")))
    (is (= "xl" (model/normalize-breakpoint "xl")))
    (is (= "md" (model/normalize-breakpoint "tablet")))))

(deftest normalize-alignment-test
  (testing "normalize-alignment preserves valid values and falls back for invalid ones"
    (is (= "space-between" (model/normalize-alignment nil)))
    (is (= "start" (model/normalize-alignment "start")))
    (is (= "center" (model/normalize-alignment "center")))
    (is (= "space-between" (model/normalize-alignment "space-between")))
    (is (= "space-between" (model/normalize-alignment "between")))))

(deftest public-state-test
  (testing "public-state normalizes enums and booleans"
    (is (= {:label "Main navigation"
            :orientation "horizontal"
            :variant "default"
            :sticky false
            :elevated false
            :breakpoint "md"
            :alignment "space-between"}
           (model/public-state
            {:label "Main navigation"
             :orientation nil
             :variant nil
             :sticky nil
             :elevated nil
             :breakpoint nil
             :alignment nil})))

    (is (= {:label "Primary navigation"
            :orientation "vertical"
            :variant "subtle"
            :sticky true
            :elevated true
            :breakpoint "lg"
            :alignment "center"}
           (model/public-state
            {:label "Primary navigation"
             :orientation "vertical"
             :variant "subtle"
             :sticky true
             :elevated true
             :breakpoint "lg"
             :alignment "center"})))))

(deftest landmark-label-test
  (testing "landmark-label returns only non-empty strings"
    (is (= "Main navigation"
           (model/landmark-label {:label "Main navigation"})))
    (is (= nil
           (model/landmark-label {:label ""})))
    (is (= nil
           (model/landmark-label {:label nil})))))
