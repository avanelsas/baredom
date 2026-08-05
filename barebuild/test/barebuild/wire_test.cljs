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

(deftest shape-carries-field-options
  (let [js (clj->js
            {"outcome"   "accepted"
             "requestId" "req-9"
             "query"     {}
             "value"     []
             "shape"     {"idKey"  "id"
                          "fields" [{"key"     "projectId" "type" "string"
                                     "options" [{"value" "p-1" "label" "Website Redesign"}
                                                {"value" "p-2" "label" "Mobile App"}]}
                                    {"key" "title" "type" "string"}]}})
        [project title] (get-in (wire/parse-envelope js) [:shape :fields])]
    (testing "options carry a value and a label, so a control can offer server-owned choices
              without a second resource. The value stays an opaque domain string, only the
              protocol's own vocabulary becomes keywords"
      (is (= {:key     "projectId"
              :type    :string
              :options [{:value "p-1" :label "Website Redesign"}
                        {:value "p-2" :label "Mobile App"}]}
             project)))
    (testing "a field without options stays bare, as it does for required and enum"
      (is (= {:key "title" :type :string} title)))))

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

(defn- accepted-with
  "A minimal accepted envelope carrying `extra`, for probing one member at a time."
  [extra]
  (clj->js (merge {"outcome" "accepted"
                   "value"   []
                   "shape"   {"idKey" "id" "fields" []}}
                  extra)))

(deftest fields-declared-empty-differ-from-fields-not-declared
  (testing "an empty list is a shape declaring there is nothing to check"
    (is (= [] (get-in (wire/parse-envelope (accepted-with {})) [:shape :fields]))))
  (testing "no list at all declares nothing, which the contract check reports as missing"
    (is (nil? (get-in (wire/parse-envelope
                       (clj->js {"outcome" "accepted" "value" [] "shape" {"idKey" "id"}}))
                      [:shape :fields]))))
  (testing "a fields member that is not a list is no declaration either, and does not throw"
    (is (nil? (get-in (wire/parse-envelope
                       (clj->js {"outcome" "accepted" "value" []
                                 "shape"   {"idKey" "id" "fields" {"owner" "string"}}}))
                      [:shape :fields])))))

(deftest a-member-of-the-wrong-kind-does-not-crash-the-parse
  (testing "page info that is not an object reads as none. A throw here would reject the fetch
            promise, and the edge classifies a rejected fetch as an unreachable server, so a
            server that answered would be reported as one that could not be reached"
    (is (nil? (:page-info (wire/parse-envelope (accepted-with {"pageInfo" 7}))))
        "nothing to read is nothing, not an empty bag the server never sent"))
  (testing "and page info the server did not send at all reads the same way"
    (is (nil? (:page-info (wire/parse-envelope (accepted-with {}))))))
  (testing "page info the server did send is read, with its keys kebabed"
    (is (= {:total-count 42 :page 2}
           (:page-info (wire/parse-envelope
                        (accepted-with {"pageInfo" {"totalCount" 42 "page" 2}}))))))
  (testing "options that are not a list leave the field bare, exactly as absent options do"
    (let [[field] (get-in (wire/parse-envelope
                           (clj->js {"outcome" "accepted" "value" []
                                     "shape"   {"idKey"  "id"
                                                "fields" [{"key"     "projectId"
                                                           "type"    "string"
                                                           "options" "p-1"}]}}))
                          [:shape :fields])]
      (is (= {:key "projectId" :type :string} field)))))

(deftest an-unreadable-envelope-member-is-a-protocol-failure
  (testing "a shape that is not an object carries no declaration to read"
    (is (= :malformed-shape
           (get-in (wire/parse-envelope
                    (clj->js {"outcome" "accepted" "value" [] "shape" "id"}))
                   [:protocol-failure :reason]))))
  (testing "an unreadable query echo fails the envelope rather than coercing to the empty query.
            The empty query would adopt as intent and rewrite the address bar on the strength of
            a broken response"
    (is (= :malformed-query
           (get-in (wire/parse-envelope (accepted-with {"query" 7}))
                   [:protocol-failure :reason])))
    (is (= :malformed-query
           (get-in (wire/parse-envelope
                    (clj->js {"outcome" "rejected" "error" {"code" "nope"} "query" [1 2]}))
                   [:protocol-failure :reason])))))

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
