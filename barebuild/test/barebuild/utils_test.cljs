(ns barebuild.utils-test
  "canonicalize-query (§6.6): the single query form used at both edges so a client
   intent and a normalizing server echo can compare equal."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [barebuild.utils :as utils]))

(deftest canonicalize-query-keywordizes-and-stringifies
  (is (= {:sort "owner" :direction "asc" :page "2"}
         (utils/canonicalize-query {"sort" "owner" "direction" "asc" "page" 2}))
      "string keys -> keywords, values -> strings"))

(deftest build-scoped-url-reflects-the-new-params
  (testing "the new params win — the URL reflects the mutation, not the stale value (regression)"
    (is (= "/t?tasks.sort=owner"
           (utils/build-scoped-url "?tasks.sort=STALE" "/t" "tasks" {:sort "owner"}))))
  (testing "only this resource's prefixed params are replaced; others are preserved"
    (is (= "/tasks?other.x=1&tasks.sort=owner&tasks.direction=asc"
           (utils/build-scoped-url "?other.x=1&tasks.sort=old" "/tasks" "tasks"
                                   {:sort "owner" :direction "asc"}))))
  (testing "clearing all owned params yields just the pathname (no dangling ?)"
    (is (= "/t" (utils/build-scoped-url "?tasks.sort=owner" "/t" "tasks" {}))))
  (testing "empty starting search + new params"
    (is (= "/t?tasks.page=2" (utils/build-scoped-url "" "/t" "tasks" {:page "2"})))))

(deftest canonicalize-query-drops-nil-and-blank
  (testing "nil-valued entries are dropped: a cleared key is simply absent"
    (is (= {} (utils/canonicalize-query {:sort nil :direction nil}))))
  (testing "blank-string values are dropped too"
    (is (= {:sort "owner"} (utils/canonicalize-query {:sort "owner" :direction ""}))))
  (testing "an already-canonical query is unchanged (idempotent)"
    (is (= {:sort "owner" :direction "asc"}
           (utils/canonicalize-query {:sort "owner" :direction "asc"})))))
