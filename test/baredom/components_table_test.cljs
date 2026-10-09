(ns baredom.components-table-test
  (:require [baredom.components-table :as table]
            [baredom.exports.x-trace-history :as x-trace-history]
            [baredom.registry :as registry]
            [cljs.test :refer-macros [deftest is]]))

(deftest the-table-holds-every-component-of-the-registry
  (is (= (set registry/all-registers)
         (conj (set (map first table/components)) x-trace-history/register!))))
