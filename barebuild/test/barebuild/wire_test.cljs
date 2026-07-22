(ns barebuild.wire-test
  "Conversion-1 parse tests: JS envelope -> CLJS value (or protocol-failure marker).
   Inputs are built with clj->js to mimic what response.json() hands the executor."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [barebuild.wire :as wire]))

(def accepted-js
  (clj->js
   {"outcome"   "accepted"
    "requestId" "req-1"
    "revision"  "tasks:v1"
    "query"     {:sort "owner"}
    "value"     [{"id" 1 "owner" "Alice" "start" "2026-01-05" "end" "2026-01-20" "status" "doing"}
                 {"id" 2 "owner" "Bob"   "start" "2026-01-08" "end" "2026-01-15" "status" "todo"}]
    "shape"     {"idKey"  "id"
                 "fields" [{"key" "owner"  "type" "string"}
                           {"key" "start"  "type" "date"}
                           {"key" "status" "type" "string"}]}}))

(def rejected-js
  (clj->js
   {"outcome"   "rejected"
    "requestId" "req-2"
    "revision"  "tasks:v1"
    "query"     {:sort "bogus"}
    "error"     {"code"    "invalid-query"
                 "message" "Sorting by the requested field is unavailable."
                 "details" {"field" "bogus"}}}))

(deftest accepted-envelope
  (let [r (wire/parse-envelope accepted-js)]
    (testing "protocol keys -> keywords; outcome value is a keyword"
      (is (= :accepted (:outcome r)))
      (is (= "req-1" (:request/id r)))
      (is (= "tasks:v1" (:revision r))))
    (testing "opaque row keys stay strings; raw values preserved"
      (is (= 1 (get-in r [:value 0 "id"])))
      (is (= "Alice" (get-in r [:value 0 "owner"])))
      (is (every? string? (keys (first (:value r)))) "no row key was keywordized"))
    (testing "shape: :id-key key, field :type keywordized, field keys stay strings"
      (is (= "id" (get-in r [:shape :id-key])))
      (is (= {:key "owner" :type :string} (get-in r [:shape :fields 0])))
      (is (= :date (get-in r [:shape :fields 1 :type]))))
    (testing "query is canonicalized: owned-param keyword keys, string values"
      (is (= {:sort "owner"} (:query r))))
    (testing "a valid accepted envelope is not a failure"
      (is (nil? (:protocol-failure r))))))

(deftest shape-carries-required-and-enum
  (let [js (clj->js
            {"outcome"   "accepted"
             "requestId" "req-9"
             "revision"  "tasks:v1"
             "query"     {}
             "value"     []
             "shape"     {"idKey"  "id"
                          "fields" [{"key" "owner"  "type" "string" "required" true}
                                    {"key" "status" "type" "string" "required" true
                                     "enum" ["todo" "done"]}
                                    {"key" "end"    "type" "date"}]}})
        r (wire/parse-envelope js)
        [owner status end] (get-in r [:shape :fields])]
    (testing "required and enum are carried onto the field when present"
      (is (= {:key "owner"  :type :string :required true} owner))
      (is (= {:key "status" :type :string :required true :enum ["todo" "done"]} status)))
    (testing "a field WITHOUT constraints stays bare — no nil :required/:enum keys (W2 decision #2)"
      (is (= {:key "end" :type :date} end)))))

(deftest revision-is-optional
  (let [r (wire/parse-envelope
           (clj->js {"outcome" "accepted"
                     "value"   []
                     "shape"   {"idKey" "id" "fields" []}}))]
    (is (= :accepted (:outcome r)) "missing revision does not fail the envelope")
    (is (nil? (:revision r)))))

(deftest rejected-envelope
  (let [r (wire/parse-envelope rejected-js)]
    (is (= :rejected (:outcome r)))
    (is (= :invalid-query (get-in r [:error :code])) "error code -> keyword")
    (is (= "Sorting by the requested field is unavailable." (get-in r [:error :message])))
    (is (= {"field" "bogus"} (get-in r [:error :details])) "details opaque -> string keys")
    (is (nil? (:protocol-failure r)) "a rejected response is NOT a protocol failure")))

(deftest protocol-failures
  (testing "nil / empty body"
    (is (= {:reason :empty-body} (:protocol-failure (wire/parse-envelope nil)))))
  (testing "unknown outcome"
    (let [r (wire/parse-envelope (clj->js {"outcome" "banana"}))]
      (is (= :unknown-outcome (get-in r [:protocol-failure :reason])))
      (is (= "banana" (get-in r [:protocol-failure :outcome])))))
  (testing "accepted missing required members (no shape)"
    (let [r (wire/parse-envelope (clj->js {"outcome" "accepted" "value" []}))]
      (is (= :missing-accepted-members (get-in r [:protocol-failure :reason])))))
  (testing "rejected missing required members (no error)"
    (let [r (wire/parse-envelope (clj->js {"outcome" "rejected"}))]
      (is (= :missing-rejected-members (get-in r [:protocol-failure :reason]))))))

;; --- W1b: write-ack parse (parse-ack) --------------------------------------
;; The ack carries NO value/shape (the client refetches). parse-ack must accept that
;; shape, where parse-envelope would reject it as a missing accepted member.

(def ack-accepted-js
  (clj->js {"outcome"   "accepted"
            "requestId" "w-1"
            "revision"  "tasks:v1"}))

(def ack-rejected-js
  (clj->js {"outcome"   "rejected"
            "requestId" "w-2"
            "revision"  "tasks:v1"
            "error"     {"code"    "conflict"
                         "message" "The task was modified by someone else."
                         "details" {"field" "revision"}}}))

(deftest ack-accepted-parses-without-value-or-shape
  (let [r (wire/parse-ack ack-accepted-js)]
    (testing "an accepted ack carries only outcome + echoed id + revision"
      (is (= :accepted (:outcome r)))
      (is (= "w-1" (:request/id r)))
      (is (= "tasks:v1" (:revision r))))
    (testing "no value/shape on an ack — the refetch carries the new state"
      (is (nil? (:value r)))
      (is (nil? (:shape r))))
    (testing "a valid ack is not a protocol failure"
      (is (nil? (:protocol-failure r))))))

(deftest ack-rejected-parses-like-a-read-rejection
  (let [r (wire/parse-ack ack-rejected-js)]
    (is (= :rejected (:outcome r)))
    (is (= :conflict (get-in r [:error :code])) "error code -> keyword")
    (is (= "The task was modified by someone else." (get-in r [:error :message])))
    (is (= {"field" "revision"} (get-in r [:error :details])) "details opaque -> string keys")
    (is (nil? (:protocol-failure r)) "a rejected ack is NOT a protocol failure")))

(deftest ack-protocol-failures
  (testing "nil / empty body"
    (is (= {:reason :empty-body} (:protocol-failure (wire/parse-ack nil)))))
  (testing "unknown outcome"
    (let [r (wire/parse-ack (clj->js {"outcome" "banana" "requestId" "w-3" "revision" "tasks:v1"}))]
      (is (= :unknown-outcome (get-in r [:protocol-failure :reason])))
      (is (= "banana" (get-in r [:protocol-failure :outcome])))))
  (testing "accepted ack missing revision -> not a valid ack"
    (let [r (wire/parse-ack (clj->js {"outcome" "accepted" "requestId" "w-4"}))]
      (is (= :missing-accepted-members (get-in r [:protocol-failure :reason])))))
  (testing "accepted ack missing requestId -> not a valid ack"
    (let [r (wire/parse-ack (clj->js {"outcome" "accepted" "revision" "tasks:v1"}))]
      (is (= :missing-accepted-members (get-in r [:protocol-failure :reason])))))
  (testing "rejected ack missing error"
    (let [r (wire/parse-ack (clj->js {"outcome" "rejected" "requestId" "w-5"}))]
      (is (= :missing-rejected-members (get-in r [:protocol-failure :reason]))))))
