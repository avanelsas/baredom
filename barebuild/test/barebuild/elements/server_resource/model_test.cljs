(ns barebuild.elements.server-resource.model-test
  "The server-resource element's pure decisions: how it resolves its id, and how its request
   attributes become the config a request carries. Whether an intent is cross-resource
   coordination or a self-drive is step's call, tested in barebuild.resource-test."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [barebuild.elements.server-resource.model :as model]))

(deftest resolve-resource-id-nil-when-unnamed
  (testing "an explicit id is kept"
    (is (= "projects" (model/resolve-resource-id "projects"))))
  (testing "absent or blank means unnamed: nil, so the resource owns the URL's root namespace"
    (is (nil? (model/resolve-resource-id nil)))
    (is (nil? (model/resolve-resource-id "")))
    (is (nil? (model/resolve-resource-id "   ")))))

(deftest resolve-resource-id-refuses-what-a-query-cannot-carry
  (testing "the id is written into the request query verbatim, so a character that would split
            that query is not an id at all"
    (is (nil? (model/resolve-resource-id "a&b")))
    (is (nil? (model/resolve-resource-id "a=b")))
    (is (nil? (model/resolve-resource-id "a?b")))
    (is (nil? (model/resolve-resource-id "a#b")))
    (is (nil? (model/resolve-resource-id "a+b")))
    (is (nil? (model/resolve-resource-id "a%2Fb")))
    (is (nil? (model/resolve-resource-id "a b"))))
  (testing "and everything an identifier normally is still passes, the colon the id format itself
            uses included"
    (is (= "tasks" (model/resolve-resource-id "tasks")))
    (is (= "my-board_2.left" (model/resolve-resource-id "my-board_2.left")))
    (is (= "ns:tasks" (model/resolve-resource-id "ns:tasks")))))

;; --- request config --------------------------------------------------------

(deftest resolve-credentials-accepts-only-the-three-fetch-modes
  (testing "each fetch mode is kept verbatim"
    (is (= "same-origin" (model/resolve-credentials "same-origin")))
    (is (= "include" (model/resolve-credentials "include")))
    (is (= "omit" (model/resolve-credentials "omit"))))
  (testing "surrounding whitespace is not part of the mode, as it is not for the other two
            transport attributes. A templated attribute that arrives padded should still work"
    (is (= "include" (model/resolve-credentials " include ")))
    (is (= "omit" (model/resolve-credentials "\n  omit\n"))))
  (testing "absent, blank or misspelled -> nil, so nothing is set and the browser default holds.
            Passing an unknown mode through would make fetch throw on every request"
    (is (nil? (model/resolve-credentials nil)))
    (is (nil? (model/resolve-credentials "")))
    (is (nil? (model/resolve-credentials "   ")))
    (is (nil? (model/resolve-credentials "same origin")))
    (is (nil? (model/resolve-credentials "INCLUDE")))
    (is (nil? (model/resolve-credentials "always")))))

;; --- the request budget ----------------------------------------------------

(defn- timeout-ms [attr-value] (:ms (model/parse-timeout attr-value)))
(defn- timeout-valid? [attr-value] (:valid? (model/parse-timeout attr-value)))

(deftest parse-timeout-defaults-when-the-attribute-is-absent
  (testing "every resource gets a budget without asking, so a request that never settles cannot
            wedge the resource for the life of the element"
    (is (= 60000 (timeout-ms nil)))
    (is (= 60000 (timeout-ms "")))
    (is (= 60000 (timeout-ms "   ")))
    (is (= model/default-timeout-ms (timeout-ms nil)))))

(deftest parse-timeout-takes-a-positive-value
  (is (= 5000 (timeout-ms "5000")))
  (testing "surrounding whitespace is not part of the number"
    (is (= 5000 (timeout-ms " 5000 ")))))

(deftest parse-timeout-treats-zero-as-no-budget
  (testing "0 removes the limit, the same spelling XMLHttpRequest.timeout uses, so an endpoint
            that is legitimately slow has an escape hatch"
    (is (nil? (timeout-ms "0")))))

(deftest parse-timeout-keeps-the-default-for-an-unusable-value
  (testing "a typo must not silently remove the budget, which is what falling back to nil
            would do"
    (is (= 60000 (timeout-ms "10 seconds")))
    (is (= 60000 (timeout-ms "abc")))
    (is (= 60000 (timeout-ms "5.5")))
    (is (= 60000 (timeout-ms "-1")))))

(deftest parse-timeout-validity-is-what-decides-whether-to-report
  (testing "absent and well-formed values are silent"
    (is (true? (timeout-valid? nil)))
    (is (true? (timeout-valid? "")))
    (is (true? (timeout-valid? "0")))
    (is (true? (timeout-valid? "5000"))))
  (testing "anything that cannot be read as milliseconds is reported"
    (is (false? (timeout-valid? "10 seconds")))
    (is (false? (timeout-valid? "5.5")))
    (is (false? (timeout-valid? "-1")))))

(deftest parse-timeout-reads-the-attribute-once
  (testing "the budget and whether it was readable are two facts about one parse, so they are
            answered together and cannot drift apart"
    (is (= {:ms 5000 :valid? true} (model/parse-timeout "5000")))
    (is (= {:ms nil :valid? true} (model/parse-timeout "0")))
    (is (= {:ms model/default-timeout-ms :valid? false} (model/parse-timeout "nope")))))
