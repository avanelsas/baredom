(ns app.components.x-stat.model-test
  (:require
   [cljs.test :refer-macros [deftest is]]
   [app.components.x-stat.model :as model]))

(deftest enum-normalization-test

  (is (= "default" (model/normalize-variant "bad")))
  (is (= "start" (model/normalize-align "bad")))
  (is (= "md" (model/normalize-size "bad")))
  (is (= "normal" (model/normalize-emphasis "bad")))
  (is (= "neutral" (model/normalize-trend "bad"))))
