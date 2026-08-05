(ns demo.consumer-form-test
  "The pure submit decision: write-plan turns a record, its shape, and a payload into a value,
  with no DOM in sight."
  (:require [cljs.test :refer-macros [deftest is]]
            [demo.consumer-form :as cf]))

;; The parsed shape carries keyword types (closed protocol vocabulary), string keys (opaque
;; domain), matching what the wire hands a consumer.
(def ^:private shape
  {:fields [{:key "title" :type :string :required true}
            {:key "owner" :type :string :required true}]})

(deftest write-plan-valid-record-yields-the-payload
  (let [payload {:op :create :record {"title" "Ship" "owner" "Zoe"}}]
    (is (= {:payload payload}
           (cf/write-plan {"title" "Ship" "owner" "Zoe"} shape payload))
        "a record that satisfies the shape produces the payload to dispatch")))

(deftest write-plan-invalid-record-yields-errors
  (let [plan (cf/write-plan {"title" "" "owner" "Zoe"} shape {:op :create})]
    (is (contains? plan :errors) "a shape violation produces errors, not a payload")
    (is (not (contains? plan :payload)))
    (is (= "title" (:field (first (:errors plan)))) "the error names the offending field")))

(def ^:private estimate-shape
  {:fields [{:key "title"    :type :string :required true}
            {:key "estimate" :type :number}]})

(deftest write-plan-sends-a-number-field-as-a-number
  (let [entered {"title" "Ship" "estimate" "5"}
        plan    (cf/write-plan entered estimate-shape {:op :create :record entered})]
    (is (= {:payload {:op :create :record {"title" "Ship" "estimate" 5}}} plan)
        "the payload carries the record as the shape declares it, so what reaches the wire is
         a JSON number rather than the string the form held. The server checks the parsed body
         against the same declared type and refuses the string")))

(deftest write-plan-reports-a-number-field-that-cannot-be-read
  (let [entered {"title" "Ship" "estimate" "abc"}
        plan    (cf/write-plan entered estimate-shape {:op :create :record entered})]
    (is (not (contains? plan :payload)) "nothing is dispatched")
    (is (= "estimate" (:field (first (:errors plan)))))
    (is (= :wrong-type (:code (first (:errors plan)))))))
