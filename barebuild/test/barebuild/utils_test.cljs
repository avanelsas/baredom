(ns barebuild.utils-test
  "canonicalize-query (§6.6): the single query form used at both edges so a client
   intent and a normalizing server echo can compare equal. Plus `request`: the one
   place a call's parts become the request value the executor performs, shared by
   reads and writes so neither edge hand-assembles a URL."
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

;; --- request: the shared request builder -----------------------------------

(deftest request-builds-a-collection-get-with-the-query
  (is (= {:method "GET" :url "/api/tasks?requestId=tasks:1&sort=owner&direction=asc"}
         (utils/request {:endpoint   "/api/tasks"
                         :method     "GET"
                         :request-id "tasks:1"
                         :query      {:sort "owner" :direction "asc"}}))
      "no segment -> the collection; requestId leads, the query follows"))

(deftest request-omits-an-empty-query
  (testing "an empty query contributes nothing — no dangling separator"
    (is (= "/api/tasks?requestId=tasks:1"
           (:url (utils/request {:endpoint "/api/tasks" :method "GET"
                                 :request-id "tasks:1" :query {}})))))
  (testing "an absent query behaves the same as an empty one"
    (is (= "/api/tasks?requestId=tasks:1"
           (:url (utils/request {:endpoint "/api/tasks" :method "GET"
                                 :request-id "tasks:1"}))))))

(deftest request-carries-the-request-id-in-every-shape
  (testing "the id is always in the URL — the server echoes it and installable? guards on it"
    (doseq [parts [{:endpoint "/api/tasks" :method "GET" :request-id "tasks:1"}
                   {:endpoint "/api/tasks" :method "GET" :request-id "tasks:1" :query {:page "2"}}
                   {:endpoint "/api/tasks" :method "DELETE" :request-id "tasks:w1" :segment 7}
                   {:endpoint "/api/tasks" :method "PUT" :request-id "tasks:w1" :segment 7
                    :body {"title" "x"}}]]
      (is (re-find #"requestId=tasks:" (:url (utils/request parts)))))))

(deftest request-appends-a-member-segment
  (is (= {:method "DELETE" :url "/api/tasks/7?requestId=tasks:w1"}
         (utils/request {:endpoint "/api/tasks" :segment 7
                         :method "DELETE" :request-id "tasks:w1"}))
      "a member op addresses /endpoint/id — and carries no body or headers"))

(deftest request-with-a-body-adds-the-content-type
  (let [record {"title" "Ship it" "status" "todo"}
        req    (utils/request {:endpoint "/api/tasks" :segment 7 :method "PUT"
                               :request-id "tasks:w1" :body record})]
    (is (= {:method  "PUT"
            :url     "/api/tasks/7?requestId=tasks:w1"
            :body    record
            :headers {"content-type" "application/json"}}
           req))
    (testing "the body passes through as CLJS — serialization belongs at the network edge,
              and an =-comparable value is what makes the whole write spine testable"
      (is (map? (:body req))))))

(deftest request-encodes-query-values
  (is (= "/api/tasks?requestId=tasks:1&search=a+b%26c"
         (:url (utils/request {:endpoint "/api/tasks" :method "GET"
                               :request-id "tasks:1" :query {:search "a b&c"}})))
      "values go through URLSearchParams, so separators in a term can't break the query"))

(deftest canonicalize-query-drops-nil-and-blank
  (testing "nil-valued entries are dropped: a cleared key is simply absent"
    (is (= {} (utils/canonicalize-query {:sort nil :direction nil}))))
  (testing "blank-string values are dropped too"
    (is (= {:sort "owner"} (utils/canonicalize-query {:sort "owner" :direction ""}))))
  (testing "an already-canonical query is unchanged (idempotent)"
    (is (= {:sort "owner" :direction "asc"}
           (utils/canonicalize-query {:sort "owner" :direction "asc"})))))
