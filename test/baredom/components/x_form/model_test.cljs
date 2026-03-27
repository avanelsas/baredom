(ns baredom.components.x-form.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-form.model :as model]))

(deftest bool-attr-test
  (testing "nil → false"
    (is (= false (model/bool-attr? nil))))
  (testing "empty string → true (presence)"
    (is (= true (model/bool-attr? ""))))
  (testing "any string value → true"
    (is (= true (model/bool-attr? "loading"))))
  (testing "literal \"false\" → false"
    (is (= false (model/bool-attr? "false")))))

(deftest normalize-autocomplete-test
  (testing "\"on\" passes through"
    (is (= "on" (model/normalize-autocomplete "on"))))
  (testing "\"off\" passes through"
    (is (= "off" (model/normalize-autocomplete "off"))))
  (testing "nil defaults to \"on\""
    (is (= "on" (model/normalize-autocomplete nil))))
  (testing "unknown value defaults to \"on\""
    (is (= "on" (model/normalize-autocomplete "email")))))

(deftest normalize-defaults-test
  (testing "all nil inputs produce safe defaults"
    (let [m (model/normalize {})]
      (is (= false  (:loading? m)))
      (is (= false  (:novalidate? m)))
      (is (= "on"   (:autocomplete m))))))

(deftest normalize-loading-test
  (testing "loading-raw present sets loading? true"
    (is (= true (:loading? (model/normalize {:loading-raw ""})))))
  (testing "loading-raw nil sets loading? false"
    (is (= false (:loading? (model/normalize {:loading-raw nil})))))
  (testing "loading-raw \"false\" sets loading? false"
    (is (= false (:loading? (model/normalize {:loading-raw "false"}))))))

(deftest normalize-novalidate-test
  (testing "novalidate-raw empty string (present) sets novalidate? true"
    (is (= true (:novalidate? (model/normalize {:novalidate-raw ""})))))
  (testing "novalidate-raw nil sets novalidate? false"
    (is (= false (:novalidate? (model/normalize {:novalidate-raw nil}))))))

(deftest normalize-autocomplete-in-map-test
  (testing "autocomplete passes through normalize"
    (is (= "off" (:autocomplete (model/normalize {:autocomplete-raw "off"})))))
  (testing "unknown autocomplete defaults to on"
    (is (= "on" (:autocomplete (model/normalize {:autocomplete-raw "email"}))))))
