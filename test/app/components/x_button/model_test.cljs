(ns app.components.x-button.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [app.components.x-button.model :as model]))

(deftest normalize-type-test
  (testing "normalize-type preserves valid values"
    (is (= "button" (model/normalize-type "button")))
    (is (= "submit" (model/normalize-type "submit")))
    (is (= "reset" (model/normalize-type "reset"))))
  (testing "normalize-type falls back"
    (is (= model/default-type (model/normalize-type nil)))
    (is (= model/default-type (model/normalize-type "oops")))))

(deftest normalize-variant-test
  (testing "normalize-variant preserves valid values"
    (is (= "primary" (model/normalize-variant "primary")))
    (is (= "danger" (model/normalize-variant "danger"))))
  (testing "normalize-variant falls back"
    (is (= model/default-variant (model/normalize-variant nil)))
    (is (= model/default-variant (model/normalize-variant "weird")))))

(deftest normalize-size-test
  (testing "normalize-size preserves valid values"
    (is (= "sm" (model/normalize-size "sm")))
    (is (= "md" (model/normalize-size "md")))
    (is (= "lg" (model/normalize-size "lg"))))
  (testing "normalize-size falls back"
    (is (= model/default-size (model/normalize-size nil)))
    (is (= model/default-size (model/normalize-size "xl")))))

(deftest interactive-test
  (is (= true (model/interactive? {:disabled false :loading false})))
  (is (= false (model/interactive? {:disabled true :loading false})))
  (is (= false (model/interactive? {:disabled false :loading true})))
  (is (= false (model/interactive? {:disabled true :loading true}))))

(deftest aria-derived-values-test
  (is (= "true" (model/aria-busy {:loading true})))
  (is (= nil (model/aria-busy {:loading false}))))

(deftest aria-label-test
  (is (= "Close"
         (model/aria-label {:label "Close" :has-default-text? false})))
  (is (= nil
         (model/aria-label {:label "Close" :has-default-text? true})))
  (is (= nil
         (model/aria-label {:label "" :has-default-text? false})))
  (is (= nil
         (model/aria-label {:label nil :has-default-text? false}))))
