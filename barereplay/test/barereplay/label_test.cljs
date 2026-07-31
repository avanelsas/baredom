(ns barereplay.label-test
  (:require [cljs.test :refer-macros [deftest is]]
            [barereplay.label :as label]))

(deftest labels
  (is (= "response accepted" (label/event->label [:response {:outcome :accepted}])))
  (is (= "write delete #7"  (label/event->label [:submit-write {:op :delete :id 7}]))))

(deftest detail-at-extracts-request-and-response
  (let [entries [{:event   [:submit-write {:op :delete :id 7}]
                  :effects [[:notify-consumers {}] [:write {:method "DELETE" :url "/api/tasks/7"}]]}
                 {:event   [:response {:outcome :accepted}]
                  :effects [[:notify-consumers {}]]}
                 {:event   [:connected {}]
                  :effects [[:notify-consumers {}]]}]]
    (is (nil? (label/detail-at entries 0)))
    (is (= {:request {:method "DELETE" :url "/api/tasks/7"}} (label/detail-at entries 1)))
    (is (= {:response {:outcome :accepted}} (label/detail-at entries 2)))
    (is (nil? (label/detail-at entries 3)))))

(deftest item-status-marks-past-present-future
  (is (= "complete" (label/item-status 1 3)))
  (is (= "active" (label/item-status 3 3)))
  (is (= "pending" (label/item-status 4 3))))

(deftest clamp-bounds
  (is (= 0 (label/clamp -2 0 5)))
  (is (= 5 (label/clamp 9 0 5)))
  (is (= 3 (label/clamp 3 0 5))))
