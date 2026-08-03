(ns barebuild.elements.server-resource.model-test
  "The server-resource element's pure decisions: how it resolves its id, when an intent is
   cross-resource coordination vs a self-drive, and how its transport attributes become the
   config value a request carries."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [barebuild.elements.server-resource.model :as model]))

(deftest resolve-resource-id-nil-when-unnamed
  (testing "an explicit id is kept"
    (is (= "projects" (model/resolve-resource-id "projects"))))
  (testing "absent or blank means unnamed: nil, so the resource owns the URL's root namespace"
    (is (nil? (model/resolve-resource-id nil)))
    (is (nil? (model/resolve-resource-id "")))
    (is (nil? (model/resolve-resource-id "   ")))))

(deftest targets-sibling?-with-an-unnamed-resource
  (testing "an unnamed resource driving itself is not a sibling hop"
    (is (false? (model/targets-sibling? nil nil))))
  (testing "an unnamed resource naming a sibling still coordinates"
    (is (true? (model/targets-sibling? nil "tasks")))))

(deftest targets-sibling?-only-when-naming-another-resource
  (testing "no target -> drive self"
    (is (false? (model/targets-sibling? "tasks" nil))))
  (testing "target equal to own id -> drive self, not a sibling hop"
    (is (false? (model/targets-sibling? "tasks" "tasks"))))
  (testing "a different named target -> cross-resource coordination"
    (is (true? (model/targets-sibling? "projects" "tasks")))))

;; --- transport config ------------------------------------------------------

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

(deftest transport-is-nil-when-nothing-is-configured
  (testing "nothing configured and no budget -> no transport at all"
    (is (nil? (model/transport nil nil nil))))
  (testing "any one alone is enough to carry"
    (is (= {:credentials "include"} (model/transport "include" nil nil)))
    (is (= {:headers {"x-api-key" "k"}} (model/transport nil {"x-api-key" "k"} nil)))
    (is (= {:timeout 60000} (model/transport nil nil 60000))))
  (testing "all three together"
    (is (= {:credentials "omit" :headers {"x-api-key" "k"} :timeout 5000}
           (model/transport "omit" {"x-api-key" "k"} 5000)))))

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
