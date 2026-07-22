(ns barebuild.validation-test
  "validate-payload tests: a write record vs a shape (type + required + enum).
   Pure, =-asserted. The shape mirrors what wire/->accepted produces — :type is a
   keyword, and absent :required/:enum mean 'no constraint'."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [barebuild.validation :as validation]))

(def shape
  {:id-key "id"
   :fields [{:key "owner"  :type :string :required true}
            {:key "start"  :type :date   :required true}
            {:key "end"    :type :date}                                   ; optional, unconstrained
            {:key "status" :type :string :required true :enum ["todo" "doing" "done"]}]})

(defn- pairs
  "Reduce errors to [field code] tuples for order-sensitive =-assertions."
  [errs]
  (mapv (juxt :field :code) errs))

(def valid
  {"owner" "Alice" "start" "2026-01-05" "end" "2026-02-01" "status" "todo"})

(deftest valid-payload-has-no-errors
  (is (= [] (validation/validate-payload valid shape))))

(deftest optional-field-may-be-absent
  (testing "omitting the non-required, unconstrained :end field is fine"
    (is (= [] (validation/validate-payload (dissoc valid "end") shape)))))

(deftest optional-field-may-be-blank
  (testing "a blank optional field (form-associated inputs send \"\", not an absent key) is
            not a type error — mirrors the server, which skips the type check when blank"
    (is (= [] (validation/validate-payload (assoc valid "end" "") shape)))))

(deftest missing-required-field
  (testing "an absent required field is reported by its key"
    (is (= [["owner" :missing-required]]
           (pairs (validation/validate-payload (dissoc valid "owner") shape))))))

(deftest blank-required-field
  (testing "an empty-string required field counts as missing (present ≠ filled)"
    (is (= [["owner" :missing-required]]
           (pairs (validation/validate-payload (assoc valid "owner" "") shape))))))

(deftest wrong-type-field
  (testing "a present required field of the wrong type is a type error, not a required error"
    (is (= [["owner" :wrong-type]]
           (pairs (validation/validate-payload (assoc valid "owner" 42) shape))))))

(deftest value-not-in-enum
  (is (= [["status" :not-in-enum]]
         (pairs (validation/validate-payload (assoc valid "status" "archived") shape)))))

(deftest value-in-enum-passes
  (is (= [] (validation/validate-payload (assoc valid "status" "done") shape))))

(deftest absent-constraints-are-unconstrained
  (testing "a shape field with neither :required nor :enum accepts any correctly-typed value"
    (let [free-shape {:id-key "id" :fields [{:key "note" :type :string}]}]
      (is (= [] (validation/validate-payload {} free-shape)) "absent + not required -> ok")
      (is (= [] (validation/validate-payload {"note" "anything"} free-shape))
          "present, no enum -> no membership check"))))

(deftest errors-accumulate-in-field-order
  (testing "multiple bad fields surface together, ordered by the shape's :fields"
    (is (= [["owner" :missing-required] ["status" :not-in-enum]]
           (pairs (validation/validate-payload
                   (-> valid (dissoc "owner") (assoc "status" "nope"))
                   shape))))))
