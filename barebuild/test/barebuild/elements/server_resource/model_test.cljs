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

(deftest normalize-headers-lowercases-the-names
  (testing "HTTP header names are case-insensitive but a CLJS map is not: without lowercasing,
            an author's Authorization and a BareBuild-set one would both be sent"
    (is (= {"authorization" "Bearer t" "x-api-key" "k"}
           (model/normalize-headers {"Authorization" "Bearer t" "X-API-Key" "k"}))))
  (testing "surrounding whitespace in a name is not part of it"
    (is (= {"x-api-key" "k"} (model/normalize-headers {"  X-Api-Key  " "k"}))))
  (testing "the value is left exactly as written, case included"
    (is (= {"authorization" "Bearer AbC"}
           (model/normalize-headers {"authorization" "Bearer AbC"})))))

(deftest normalize-headers-drops-blank-entries
  (testing "a nil or empty value is not a header, it is an absent one"
    (is (= {"x-api-key" "k"}
           (model/normalize-headers {"x-api-key" "k" "x-empty" "" "x-nil" nil}))))
  (testing "a blank name has nothing to send under"
    (is (nil? (model/normalize-headers {"   " "k"}))))
  (testing "non-string values stringify rather than being dropped"
    (is (= {"x-tenant" "7" "x-beta" "false"}
           (model/normalize-headers {"x-tenant" 7 "x-beta" false})))))

(deftest normalize-headers-nil-when-nothing-survives
  (testing "nil rather than {} so an unconfigured resource carries no header entry at all"
    (is (nil? (model/normalize-headers {})))
    (is (nil? (model/normalize-headers nil))))
  (testing "a headers attribute holding valid JSON that is not an object is not headers"
    (is (nil? (model/normalize-headers [1 2])))
    (is (nil? (model/normalize-headers "x-api-key")))))

;; --- the request budget ----------------------------------------------------

(deftest resolve-timeout-defaults-when-the-attribute-is-absent
  (testing "every resource gets a budget without asking, so a request that never settles cannot
            wedge the resource for the life of the element"
    (is (= 60000 (model/resolve-timeout nil)))
    (is (= 60000 (model/resolve-timeout "")))
    (is (= 60000 (model/resolve-timeout "   ")))
    (is (= model/default-timeout-ms (model/resolve-timeout nil)))))

(deftest resolve-timeout-takes-a-positive-value
  (is (= 5000 (model/resolve-timeout "5000")))
  (testing "surrounding whitespace is not part of the number"
    (is (= 5000 (model/resolve-timeout " 5000 ")))))

(deftest resolve-timeout-treats-zero-as-no-budget
  (testing "0 removes the limit, the same spelling XMLHttpRequest.timeout uses, so an endpoint
            that is legitimately slow has an escape hatch"
    (is (nil? (model/resolve-timeout "0")))))

(deftest resolve-timeout-keeps-the-default-for-an-unusable-value
  (testing "a typo must not silently remove the budget, which is what falling back to nil
            would do"
    (is (= 60000 (model/resolve-timeout "10 seconds")))
    (is (= 60000 (model/resolve-timeout "abc")))
    (is (= 60000 (model/resolve-timeout "5.5")))
    (is (= 60000 (model/resolve-timeout "-1")))))

(deftest valid-timeout?-is-what-decides-whether-to-report
  (testing "absent and well-formed values are silent"
    (is (true? (model/valid-timeout? nil)))
    (is (true? (model/valid-timeout? "")))
    (is (true? (model/valid-timeout? "0")))
    (is (true? (model/valid-timeout? "5000"))))
  (testing "anything that cannot be read as milliseconds is reported"
    (is (false? (model/valid-timeout? "10 seconds")))
    (is (false? (model/valid-timeout? "5.5")))
    (is (false? (model/valid-timeout? "-1")))))
