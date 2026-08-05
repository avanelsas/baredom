(ns barebuild.utils.query-test
  "The query value's normal form (§6.6): the single shape used at both edges, so a client intent
   and a normalizing server echo can compare equal."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [barebuild.utils.query :as query]))

(deftest canonicalize-query-keywordizes-and-stringifies
  (is (= {:sort "owner" :direction "asc" :page "2"}
         (query/canonicalize-query {"sort" "owner" "direction" "asc" "page" 2}))
      "string keys -> keywords, values -> strings"))


(deftest canonicalize-query-drops-nil-and-blank
  (testing "nil-valued entries are dropped: a cleared key is simply absent"
    (is (= {} (query/canonicalize-query {:sort nil :direction nil}))))
  (testing "blank-string values are dropped too"
    (is (= {:sort "owner"} (query/canonicalize-query {:sort "owner" :direction ""}))))
  (testing "a whitespace-only value carries nothing either, so it is dropped like an empty one.
            Kept, it would ride the url and the request as a key that means nothing, and a server
            normalizing it away would echo a query that never compares equal to the intent"
    (is (= {:sort "owner"} (query/canonicalize-query {:sort "owner" :direction "   "})))
    (is (= {} (query/canonicalize-query {:direction "\t\n"}))))
  (testing "an already-canonical query is unchanged (idempotent)"
    (is (= {:sort "owner" :direction "asc"}
           (query/canonicalize-query {:sort "owner" :direction "asc"})))))
