(ns baredom.components.x-fieldset.model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.components.x-fieldset.model :as model]))

(deftest parse-bool-attr-test
  (testing "nil returns false"
    (is (= false (model/parse-bool-attr nil))))
  (testing "\"false\" returns false"
    (is (= false (model/parse-bool-attr "false"))))
  (testing "empty string returns true"
    (is (= true (model/parse-bool-attr ""))))
  (testing "any other string returns true"
    (is (= true (model/parse-bool-attr "true")))))

(deftest normalize-test
  (testing "defaults when no attrs present"
    (let [m (model/normalize {})]
      (is (= ""    (:legend m)))
      (is (= false (:legend-visible? m)))
      (is (= false (:disabled? m)))
      (is (= nil   (:aria-label m)))
      (is (= nil   (:aria-describedby m)))))

  (testing "legend is forwarded"
    (let [m (model/normalize {:legend-raw "Personal Info"})]
      (is (= "Personal Info" (:legend m)))
      (is (= true (:legend-visible? m)))))

  (testing "empty string legend yields legend-visible? false"
    (let [m (model/normalize {:legend-raw ""})]
      (is (= "" (:legend m)))
      (is (= false (:legend-visible? m)))))

  (testing "nil legend-raw treated as empty"
    (let [m (model/normalize {:legend-raw nil})]
      (is (= "" (:legend m)))
      (is (= false (:legend-visible? m)))))

  (testing "disabled-present? sets disabled?"
    (let [m (model/normalize {:disabled-present? true})]
      (is (= true (:disabled? m)))))

  (testing "disabled-present? nil/false yields disabled? false"
    (let [m (model/normalize {:disabled-present? nil})]
      (is (= false (:disabled? m)))))

  (testing "aria attrs are forwarded"
    (let [m (model/normalize {:aria-label-raw       "Form section"
                              :aria-describedby-raw "section-desc"})]
      (is (= "Form section"  (:aria-label m)))
      (is (= "section-desc"  (:aria-describedby m))))))
