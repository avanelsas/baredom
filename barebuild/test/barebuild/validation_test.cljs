(ns barebuild.validation-test
  "Both checkers over a declared shape, pure and =-asserted.
   validate-contract: an accepted envelope the server sent, errors located by a :path.
   validate-payload: a write record the client is about to send, errors located by a :field.
   Shapes mirror what wire/->accepted produces, :type is a keyword, and absent
   :required/:enum mean 'no constraint'."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [barebuild.validation :as validation]))

;; --- validate-contract: an accepted envelope vs the shape it declares -------

(def accepted
  "An accepted payload as wire/->accepted produces it. Only :shape and :value are read."
  {:shape {:id-key "id"
           :fields [{:key "owner"  :type :string}
                    {:key "status" :type :string}]}
   :value [{"id" 1 "owner" "Alice" "status" "todo"}
           {"id" 2 "owner" "Bob"   "status" "done"}]})

(defn- paths
  "Reduce contract errors to [path code] tuples for order-sensitive =-assertions."
  [errs]
  (mapv (juxt :path :code) errs))

(deftest valid-accepted-payload-has-no-errors
  (is (= [] (validation/validate-contract accepted))))

(deftest shape-must-declare-an-id-key
  (testing "without one, nothing says which member of a row names it"
    (is (= [[[:shape :id-key] :missing-id-key]]
           (paths (validation/validate-contract {:shape {:fields []} :value []}))))))

(deftest shape-must-declare-a-field-list
  (testing "no field list at all is a missing declaration"
    (is (= [[[:shape :fields] :missing-fields]]
           (paths (validation/validate-contract {:shape {:id-key "id"} :value []})))))
  (testing "an empty one is the opposite claim, that there is nothing to check"
    (is (= [] (validation/validate-contract {:shape {:id-key "id" :fields []} :value []})))))

(deftest value-must-be-a-list-of-maps
  (testing "a value that is not a list at all"
    (is (= [[[:value] :not-a-list]]
           (paths (validation/validate-contract (assoc accepted :value {"id" 1}))))))
  (testing "a list of things that are not rows"
    (is (= [[[:value] :not-maps]]
           (paths (validation/validate-contract (assoc accepted :value [1 2])))))))

(deftest rows-must-carry-a-unique-non-nil-id
  (testing "a row without the declared id key cannot be told apart from another"
    (is (= [[[:value] :missing-id]]
           (paths (validation/validate-contract
                   (assoc accepted :value [{"owner" "Alice" "status" "todo"}]))))))
  (testing "two rows sharing an id"
    (is (= [[[:value] :duplicate-id]]
           (paths (validation/validate-contract (assoc-in accepted [:value 1 "id"] 1)))))))

(deftest rows-must-carry-every-declared-field-at-its-declared-type
  (testing "a missing field is reported at the path of the row and key that lack it"
    (is (= [[[:value 0 "status"] :missing-field]]
           (paths (validation/validate-contract
                   (update-in accepted [:value 0] dissoc "status"))))))
  (testing "a declared field holding the wrong type"
    (is (= [[[:value 0 "owner"] :wrong-type]]
           (paths (validation/validate-contract
                   (assoc-in accepted [:value 0 "owner"] 42)))))))

(deftest a-declared-field-may-hold-null
  (testing "a null passes every type, so what the row check reports is absence rather than
            emptiness. The server is the authority on whether it should have sent one"
    (is (= [] (validation/validate-contract (assoc-in accepted [:value 0 "owner"] nil))))))

(deftest a-field-declaring-no-type-constrains-nothing
  (let [untyped (assoc-in accepted [:shape :fields 0] {:key "owner"})]
    (testing "an absent :type is an absent constraint, as an absent :required or :enum is. Before
              this, rendering the wrong-type message for an undeclared type threw, and the throw
              escaped step to be classified at the edge as an unreachable server"
      (is (= [] (validation/validate-contract (assoc-in untyped [:value 0 "owner"] 42)))))
    (testing "the field must still be present in the row, only the type check is skipped"
      (is (= [[[:value 0 "owner"] :missing-field]]
             (paths (validation/validate-contract
                     (update-in untyped [:value 0] dissoc "owner"))))))))

(deftest a-value-level-error-stops-the-id-and-row-checks
  (testing "there is no use looking inside a value that is not a list of rows, so those checks
            are skipped. The shape's own errors are independent of the value and still surface"
    (is (= [[[:shape :id-key] :missing-id-key]
            [[:value] :not-a-list]]
           (paths (validation/validate-contract {:shape {:fields []} :value "nope"}))))))

;; --- validate-payload: a write record vs the same shape ---------------------

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
            not a type error, mirroring the server, which skips the type check when blank"
    (is (= [] (validation/validate-payload (assoc valid "end" "") shape)))))

(deftest missing-required-field
  (testing "an absent required field is reported by its key"
    (is (= [["owner" :missing-required]]
           (pairs (validation/validate-payload (dissoc valid "owner") shape)))))
  (testing "and a null one, which the author never supplied. A read treats a null as a value the
            server chose to send, a write treats it as nothing filled in"
    (is (= [["owner" :missing-required]]
           (pairs (validation/validate-payload (assoc valid "owner" nil) shape))))))

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

(deftest a-field-declaring-no-type-is-unconstrained-on-write-too
  (testing "both checkers read an absent :type the same way, they share the type check"
    (let [untyped {:id-key "id" :fields [{:key "note"}]}]
      (is (= [] (validation/validate-payload {"note" 42} untyped))))))

(deftest errors-accumulate-in-field-order
  (testing "multiple bad fields surface together, ordered by the shape's :fields"
    (is (= [["owner" :missing-required] ["status" :not-in-enum]]
           (pairs (validation/validate-payload
                   (-> valid (dissoc "owner") (assoc "status" "nope"))
                   shape))))))

;; --- conform-payload: reading a form record as its shape declares it -------

(def ^:private estimate-shape
  {:id-key "id"
   :fields [{:key "title"    :type :string :required true}
            {:key "estimate" :type :number}]})

(deftest a-number-field-arrives-from-a-form-as-a-string-and-is-read-as-a-number
  (testing "the DOM holds every field as a string, so a number field submits as one. Validating
            without reading it first reported every number field as the wrong type, and the
            server, which checks the parsed body the same way, rejected what did get through"
    (let [{:keys [record errors]} (validation/conform-payload
                                   {"title" "Ship it" "estimate" "5"} estimate-shape)]
      (is (= [] errors))
      (is (= 5 (get record "estimate"))
          "the value that goes on the wire is a JSON number, not the string the form held")
      (is (number? (get record "estimate"))))))

(deftest a-number-that-cannot-be-read-is-left-alone-and-reported
  (testing "coercing to NaN would ship null, so an unreadable value stays as it came and the
            existing type check is what tells the user about it"
    (let [{:keys [record errors]} (validation/conform-payload
                                   {"title" "Ship it" "estimate" "abc"} estimate-shape)]
      (is (= "abc" (get record "estimate")) "untouched, so the error can describe what was typed")
      (is (= [["estimate" :wrong-type]] (pairs errors))))))

(deftest conforming-touches-only-what-the-shape-declares
  (testing "a field the record does not carry is not introduced, and a field of another type is
            passed through, so conforming is not a rewrite of the record"
    (let [{:keys [record]} (validation/conform-payload {"title" "Ship it"} estimate-shape)]
      (is (= {"title" "Ship it"} record) "no nil :estimate appears"))
    (let [{:keys [record]} (validation/conform-payload
                            {"title" "Ship it" "extra" "kept"} estimate-shape)]
      (is (= "kept" (get record "extra")) "an undeclared key rides along untouched"))))

(deftest a-number-already-numeric-conforms-unchanged
  (testing "the same function serves a record built in code rather than typed into a form"
    (let [{:keys [record errors]} (validation/conform-payload
                                   {"title" "Ship it" "estimate" 5} estimate-shape)]
      (is (= [] errors))
      (is (= 5 (get record "estimate"))))))
