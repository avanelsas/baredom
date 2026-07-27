(ns barereplay.label-test
  (:require [cljs.test :refer-macros [deftest is]]
            [barereplay.label :as label]
            [clojure.string :as str]))

(deftest labels-and-readout
  (let [entries [{:event [:connected {}]}
                 {:event [:intent-patch {:query-patch {:sort "owner"}}]}]]
    (is (= "response accepted" (label/event->label [:response {:outcome :accepted}])))
    (is (= "write delete #7"  (label/event->label [:submit-write {:op :delete :id 7}])))
    (is (str/starts-with? (label/readout entries 2) "LIVE 2 / 2"))
    (is (str/starts-with? (label/readout entries 1) "REPLAYING 1 / 2"))
    (is (= "REPLAYING 0 / 2" (label/readout entries 0)))))
