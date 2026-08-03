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

(deftest build-scoped-url-unnamed-writes-bare-keys
  (testing "a blank id owns the root namespace: keys are written and replaced without a prefix"
    (is (= "/t?sort=owner"
           (utils/build-scoped-url "?sort=STALE" "/t" nil {:sort "owner"})))
    (is (= "/t?sort=owner" (utils/build-scoped-url "?sort=STALE" "/t" "" {:sort "owner"}))))
  (testing "an unnamed resource touches only bare keys, leaving a named sibling's keys intact"
    (is (= "/t?projects.name=x&sort=owner"
           (utils/build-scoped-url "?projects.name=x&sort=old" "/t" nil {:sort "owner"})))))

(deftest url-prefix-and-owned-keys
  (testing "a named resource prefixes with `<id>.`, an unnamed one uses no prefix"
    (is (= "tasks." (utils/url-prefix "tasks")))
    (is (= "" (utils/url-prefix nil)))
    (is (= "" (utils/url-prefix ""))))
  (testing "owned keys split bare from namespaced so root and named siblings never collide"
    (let [ks ["sort" "page" "projects.name" "tasks.sort"]]
      (is (= ["projects.name"] (utils/owned-url-keys "projects" ks)))
      (is (= ["sort" "page"] (utils/owned-url-keys nil ks))))))

;; --- request: the shared request builder -----------------------------------

(deftest request-builds-a-collection-get-with-the-query
  (is (= {:method "GET" :url "/api/tasks?requestId=tasks:1&direction=asc&sort=owner"}
         (utils/request {:endpoint   "/api/tasks"
                         :method     "GET"
                         :request-id "tasks:1"
                         :query      {:sort "owner" :direction "asc"}}))
      "no segment -> the collection; requestId leads, then the query, key-sorted"))

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

(deftest request-without-transport-is-unchanged
  (testing "an unconfigured resource builds exactly the request it built before transport
            config existed — no empty :headers, no :credentials key"
    (is (= {:method "GET" :url "/api/tasks?requestId=tasks:1"}
           (utils/request {:endpoint "/api/tasks" :method "GET" :request-id "tasks:1"
                           :transport nil})))
    (is (= {:method "GET" :url "/api/tasks?requestId=tasks:1"}
           (utils/request {:endpoint "/api/tasks" :method "GET" :request-id "tasks:1"})))))

(deftest request-carries-the-transport-config
  (testing "static headers ride a bodiless read"
    (is (= {:method  "GET"
            :url     "/api/tasks?requestId=tasks:1"
            :headers {"x-api-key" "k"}}
           (utils/request {:endpoint "/api/tasks" :method "GET" :request-id "tasks:1"
                           :transport {:headers {"x-api-key" "k"}}}))))
  (testing "the credentials mode is a request key, not a header"
    (is (= {:method "GET" :url "/api/tasks?requestId=tasks:1" :credentials "include"}
           (utils/request {:endpoint "/api/tasks" :method "GET" :request-id "tasks:1"
                           :transport {:credentials "include"}}))))
  (testing "the budget rides along too, so the executor reads how long to wait off the request
            rather than deciding it"
    (is (= {:method "GET" :url "/api/tasks?requestId=tasks:1" :timeout 5000}
           (utils/request {:endpoint "/api/tasks" :method "GET" :request-id "tasks:1"
                           :transport {:timeout 5000}})))))

(deftest request-content-type-wins-over-a-configured-one
  (testing "the write contract is plain JSON, so BareBuild owns content-type. Every other
            header is the author's and passes through beside it"
    (is (= {:method  "POST"
            :url     "/api/tasks?requestId=tasks:w1"
            :body    {"title" "x"}
            :headers {"x-api-key" "k" "content-type" "application/json"}}
           (utils/request {:endpoint "/api/tasks" :method "POST" :request-id "tasks:w1"
                           :body {"title" "x"}
                           :transport {:headers {"x-api-key"    "k"
                                                 "content-type" "text/plain"}}}))))
  (testing "a bodiless request keeps the configured content-type, nothing is imposed"
    (is (= {"content-type" "text/plain"}
           (:headers (utils/request {:endpoint "/api/tasks" :method "GET" :request-id "tasks:1"
                                     :transport {:headers {"content-type" "text/plain"}}}))))))

(deftest merge-request-headers-lets-the-more-specific-win
  (let [req (utils/request {:endpoint "/api/tasks" :method "GET" :request-id "tasks:1"
                            :transport {:headers {"x-api-key" "k" "authorization" "static"}}})]
    (testing "extra headers override the ones already on the request, and add to them"
      (is (= {"x-api-key"     "k"
              "authorization" "Bearer fresh"
              "x-trace"       "1"}
             (:headers (utils/merge-request-headers req {"authorization" "Bearer fresh"
                                                         "x-trace"       "1"})))))
    (testing "nothing to merge leaves the request as it was"
      (is (= req (utils/merge-request-headers req nil))))))

(deftest merge-request-headers-still-yields-content-type-to-the-protocol
  (testing "a bodied write keeps its JSON content-type whatever is merged in, so a hook cannot
            break the write contract by accident"
    (let [req (utils/request {:endpoint "/api/tasks" :method "POST" :request-id "tasks:w1"
                              :body {"title" "x"}})]
      (is (= {"content-type" "application/json" "authorization" "Bearer t"}
             (:headers (utils/merge-request-headers req {"authorization"  "Bearer t"
                                                         "content-type"   "text/plain"}))))))
  (testing "a bodiless read has no protocol content-type to defend, so a merged one stands"
    (let [req (utils/request {:endpoint "/api/tasks" :method "GET" :request-id "tasks:1"})]
      (is (= {"content-type" "text/plain"}
             (:headers (utils/merge-request-headers req {"content-type" "text/plain"})))))))

(deftest request-encodes-query-values
  (is (= "/api/tasks?requestId=tasks:1&search=a+b%26c"
         (:url (utils/request {:endpoint "/api/tasks" :method "GET"
                               :request-id "tasks:1" :query {:search "a b&c"}})))
      "values go through URLSearchParams, so separators in a term can't break the query"))

(deftest request-encodes-the-path-segment
  (testing "an id is opaque server data — a separator inside it must not restructure the URL"
    (is (= "/api/tasks/a%2Fb%3Fc?requestId=tasks:w1"
           (:url (utils/request {:endpoint "/api/tasks" :segment "a/b?c"
                                 :method "DELETE" :request-id "tasks:w1"}))))
    (is (= "/api/tasks/a%20b%23c?requestId=tasks:w1"
           (:url (utils/request {:endpoint "/api/tasks" :segment "a b#c"
                                 :method "DELETE" :request-id "tasks:w1"})))))
  (testing "an ordinary id is unchanged by encoding"
    (is (= "/api/tasks/7?requestId=tasks:w1"
           (:url (utils/request {:endpoint "/api/tasks" :segment 7
                                 :method "DELETE" :request-id "tasks:w1"}))))))

(deftest canonicalize-query-drops-nil-and-blank
  (testing "nil-valued entries are dropped: a cleared key is simply absent"
    (is (= {} (utils/canonicalize-query {:sort nil :direction nil}))))
  (testing "blank-string values are dropped too"
    (is (= {:sort "owner"} (utils/canonicalize-query {:sort "owner" :direction ""}))))
  (testing "an already-canonical query is unchanged (idempotent)"
    (is (= {:sort "owner" :direction "asc"}
           (utils/canonicalize-query {:sort "owner" :direction "asc"})))))
