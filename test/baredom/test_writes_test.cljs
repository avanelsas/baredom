(ns baredom.test-writes-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [baredom.test-writes :as writes]))

(def ^:private declared
  [{:target "x-tab" :attributes #{"selected" "tabindex"}}
   {:target :any    :attributes #{"hidden"}}])

(deftest audit-test
  (testing "writes on a declared tag and on any element are declared and exercise the declaration"
    (is (= {:undeclared #{} :unexercised #{}}
           (writes/audit declared #{["x-tab" "selected"] ["x-tab" "tabindex"] ["div" "hidden"]}))))
  (testing "a declared attribute on another tag is undeclared"
    (is (= #{["div" "selected"]}
           (:undeclared (writes/audit declared #{["div" "selected"]})))))
  (testing "each declared attribute that no write shows is unexercised"
    (is (= #{["x-tab" "tabindex"] [:any "hidden"]}
           (:unexercised (writes/audit declared #{["x-tab" "selected"]})))))
  (testing "a write on another tag does not exercise a tag entry"
    (is (= #{["x-tab" "selected"] ["x-tab" "tabindex"]}
           (:unexercised (writes/audit declared #{["div" "selected"] ["div" "hidden"]}))))))
