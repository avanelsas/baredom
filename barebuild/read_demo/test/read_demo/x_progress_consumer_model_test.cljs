(ns read-demo.x-progress-consumer-model-test
  "project-progress: the bounded-numeric projection — page-info to an x-progress value/max pair."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [read-demo.x-progress-consumer.model :as model]))

(deftest project-progress-maps-page-onto-value-and-max
  (testing "page becomes the value, total-pages becomes the max"
    (is (= {:value 1 :max 4}
           (model/project-progress {:page 1 :total-pages 4 :page-size 10 :total-count 40}))))
  (testing "a later page moves the value while max stays the page count"
    (is (= {:value 3 :max 4}
           (model/project-progress {:page 3 :total-pages 4 :page-size 10 :total-count 40})))))

(deftest project-progress-reads-only-the-two-bounded-keys
  (testing "page-size and total-count are irrelevant to the bar's bounds"
    (is (= {:value 2 :max 7}
           (model/project-progress {:page 2 :total-pages 7})))))

(deftest project-progress-absent-page-info-yields-nil-bounds
  (testing "a missing page-info projects nil value/max (x-progress falls back to its defaults)"
    (is (= {:value nil :max nil}
           (model/project-progress nil)))))
