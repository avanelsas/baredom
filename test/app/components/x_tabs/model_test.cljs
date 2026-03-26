(ns app.components.x-tabs.model-test
  (:require [cljs.test :refer-macros [deftest is]]
            [app.components.x-tabs.model :as model]))

(deftest orientation-normalization
  (is (= "horizontal" (model/normalize-orientation "bad"))))

(deftest activation-normalization
  (is (= "auto" (model/normalize-activation "bad"))))
