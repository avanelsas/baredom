(ns app.components.x-tab.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [app.components.x-tab.model :as model]))

(deftest orientation-normalization-test
  (is (= "horizontal" (model/normalize-orientation "bad"))))

(deftest size-normalization-test
  (is (= "md" (model/normalize-size "bad"))))
