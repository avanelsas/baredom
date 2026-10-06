(ns baremirror.unit-test
  (:require [baremirror.unit :as unit]
            [clojure.test :refer [deftest is testing]]))

(deftest pieces-of-a-shell
  (testing "a shell with attributes"
    (is (= [:li {:class "row"} ["text" [:b]]]
           (unit/pieces [:li {:class "row"} "text" [:b]]))))
  (testing "a shell with no attributes"
    (is (= [:li nil ["text" [:b]]]
           (unit/pieces [:li "text" [:b]]))))
  (testing "a shell with attributes and no children"
    (is (= [:li {:class "row"} nil] (unit/pieces [:li {:class "row"}]))))
  (testing "a shell of a tag alone"
    (is (= [:li nil nil] (unit/pieces [:li])))))
