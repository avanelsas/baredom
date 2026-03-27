(ns baredom.components.x-grid.model-test
  (:require
   [cljs.test :refer-macros [deftest is]]
   [baredom.components.x-grid.model :as model]))

(deftest gap-normalization-test
  (is (= "md" (model/normalize-gap "bad"))))
