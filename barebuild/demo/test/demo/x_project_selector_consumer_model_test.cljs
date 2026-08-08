(ns demo.x-project-selector-consumer-model-test
  "The selector's pure layer: the gesture out, and the options in."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [demo.x-project-selector-consumer.model :as model]))

(deftest translate-project-gesture-filters-by-project
  (is (= {:query-patch {:project "p-1"} :gesture-class :navigation}
         (model/translate-project-gesture "p-1"))
      "a project id becomes a tasks.project intent, pushed to history"))

(deftest translate-project-gesture-blank-clears
  (is (= {:query-patch {:project ""} :gesture-class :navigation}
         (model/translate-project-gesture model/all-projects-value))
      "the all-projects value is blank, so canonicalize drops it and unfilters"))

(deftest project-options-projects-the-value
  (testing "reads opaque string keys, keeps only id and name"
    (is (= [{:id "p-1" :name "Website Redesign"}
            {:id "p-2" :name "Mobile App"}]
           (model/project-options
            {:value [{"id" "p-1" "name" "Website Redesign" "description" "x"}
                     {"id" "p-2" "name" "Mobile App" "description" "y"}]}))))
  (testing "an empty projects value yields no options"
    (is (= [] (model/project-options {:value []})))))
