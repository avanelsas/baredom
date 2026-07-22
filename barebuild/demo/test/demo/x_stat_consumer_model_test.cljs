(ns demo.x-stat-consumer-model-test
  "project-stat: the non-collection projection — an accepted response to a scalar string."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [demo.x-stat-consumer.model :as model]))

(deftest project-stat-reads-server-total-count
  (testing "the scalar comes from the server's page-info, not from the visible rows"
    (is (= "40" (model/project-stat {:page-info {:total-count 40 :page 2 :page-size 10}
                                     :value [{"id" 11} {"id" 12}]}))))
  (testing "absent page-info yields an empty string (nothing to show)"
    (is (= "" (model/project-stat {:value []})))))
