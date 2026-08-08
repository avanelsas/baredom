(ns demo.consumer-form-test
  "form-values: the submit event into a record. write-plan: a shape and a payload into a value."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [demo.consumer-form :as cf]
            [goog.object :as gobj]))

(defn- submit-event
  "An x-form-submit stand-in. Its values object is null-prototype, as x-form builds it, so a field
   named after an Object method cannot shadow one."
  [pairs]
  (let [values (js/Object.create nil)]
    (doseq [[k v] pairs] (gobj/set values k v))
    #js {:detail #js {:values values}}))

(deftest form-values-reads-the-null-prototype-object-x-form-hands-over
  (testing "a CLJS map, so the record can be merged and conformed"
    (let [record (cf/form-values (submit-event {"title" "Ship" "owner" "Zoe"}))]
      (is (map? record))
      (is (= {"title" "Ship" "owner" "Zoe"} record))
      (is (= {"title" "Ship" "owner" "Zoe" "projectId" "p-1"}
             (merge record {"projectId" "p-1"}))
          "an edit merges the fields the form never showed onto it")))
  (testing "an empty form is an empty record, not nil"
    (is (= {} (cf/form-values (submit-event {}))))))

;; Keyword types, string keys, as the wire hands a consumer.
(def ^:private shape
  {:fields [{:key "title" :type :string :required true}
            {:key "owner" :type :string :required true}]})

(deftest write-plan-valid-record-yields-the-payload
  (let [payload {:op :create :record {"title" "Ship" "owner" "Zoe"}}]
    (is (= {:payload payload}
           (cf/write-plan shape payload))
        "a valid record produces the payload")))

(deftest write-plan-invalid-record-yields-errors
  (let [plan (cf/write-plan shape {:op :create :record {"title" "" "owner" "Zoe"}})]
    (is (contains? plan :errors) "a shape violation produces errors, not a payload")
    (is (not (contains? plan :payload)))
    (is (= "title" (:field (first (:errors plan)))) "the error names the offending field")))

(def ^:private estimate-shape
  {:fields [{:key "title"    :type :string :required true}
            {:key "estimate" :type :number}]})

(deftest write-plan-sends-a-number-field-as-a-number
  (let [plan (cf/write-plan estimate-shape
                            {:op :create :record {"title" "Ship" "estimate" "5"}})]
    (is (= {:payload {:op :create :record {"title" "Ship" "estimate" 5}}} plan)
        "the wire gets a JSON number, not the string the form held")))

(deftest write-plan-reports-a-number-field-that-cannot-be-read
  (let [plan (cf/write-plan estimate-shape
                            {:op :create :record {"title" "Ship" "estimate" "abc"}})]
    (is (not (contains? plan :payload)) "nothing is dispatched")
    (is (= "estimate" (:field (first (:errors plan)))))
    (is (= :wrong-type (:code (first (:errors plan)))))))

(deftest write-plan-keeps-the-rest-of-the-payload
  (let [plan (cf/write-plan shape {:op :update :id "7" :record {"title" "Ship" "owner" "Zoe"}})]
    (is (= {:op :update :id "7" :record {"title" "Ship" "owner" "Zoe"}} (:payload plan))
        "the op and the id travel with the conformed record")))
