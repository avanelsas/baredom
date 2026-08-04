(ns barebuild.utils.request-test
  "The request value: the one place a call's parts become what the executor performs, shared by
   reads and writes so neither edge hand-assembles a URL."
  (:require [cljs.test :refer-macros [deftest is testing]]
            [barebuild.utils.request :as request]))

;; --- request: the shared request builder -----------------------------------

(deftest request-builds-a-collection-get-with-the-query
  (is (= {:method "GET" :url "/api/tasks?requestId=tasks:1&direction=asc&sort=owner"}
         (request/request {:endpoint   "/api/tasks"
                         :method     "GET"
                         :request-id "tasks:1"
                         :query      {:sort "owner" :direction "asc"}}))
      "no segment -> the collection; requestId leads, then the query, key-sorted"))

(deftest request-omits-an-empty-query
  (testing "an empty query contributes nothing — no dangling separator"
    (is (= "/api/tasks?requestId=tasks:1"
           (:url (request/request {:endpoint "/api/tasks" :method "GET"
                                 :request-id "tasks:1" :query {}})))))
  (testing "an absent query behaves the same as an empty one"
    (is (= "/api/tasks?requestId=tasks:1"
           (:url (request/request {:endpoint "/api/tasks" :method "GET"
                                 :request-id "tasks:1"}))))))

(deftest request-carries-the-request-id-in-every-shape
  (testing "the id is always in the URL — the server echoes it and installable? guards on it"
    (doseq [parts [{:endpoint "/api/tasks" :method "GET" :request-id "tasks:1"}
                   {:endpoint "/api/tasks" :method "GET" :request-id "tasks:1" :query {:page "2"}}
                   {:endpoint "/api/tasks" :method "DELETE" :request-id "tasks:w1" :segment 7}
                   {:endpoint "/api/tasks" :method "PUT" :request-id "tasks:w1" :segment 7
                    :body {"title" "x"}}]]
      (is (re-find #"requestId=tasks:" (:url (request/request parts)))))))

(deftest request-appends-a-member-segment
  (is (= {:method "DELETE" :url "/api/tasks/7?requestId=tasks:w1"}
         (request/request {:endpoint "/api/tasks" :segment 7
                         :method "DELETE" :request-id "tasks:w1"}))
      "a member op addresses /endpoint/id — and carries no body or headers"))

(deftest request-with-a-body-adds-the-content-type
  (let [record {"title" "Ship it" "status" "todo"}
        req    (request/request {:endpoint "/api/tasks" :segment 7 :method "PUT"
                               :request-id "tasks:w1" :body record})]
    (is (= {:method  "PUT"
            :url     "/api/tasks/7?requestId=tasks:w1"
            :body    record
            :headers {"content-type" "application/json"}}
           req))
    (testing "the body passes through as CLJS — serialization belongs at the network edge,
              and an =-comparable value is what makes the whole write spine testable"
      (is (map? (:body req))))))

(deftest request-without-config-is-unchanged
  (testing "an unconfigured resource builds exactly the request it built before request
            config existed, no empty :headers, no :credentials key"
    (is (= {:method "GET" :url "/api/tasks?requestId=tasks:1"}
           (request/request {:endpoint "/api/tasks" :method "GET" :request-id "tasks:1"
                           :credentials nil :headers nil :timeout nil})))
    (is (= {:method "GET" :url "/api/tasks?requestId=tasks:1"}
           (request/request {:endpoint "/api/tasks" :method "GET" :request-id "tasks:1"})))))

(deftest request-carries-what-goes-on-the-wire-and-how-long-to-wait
  (testing "static headers ride a bodiless read"
    (is (= {:method  "GET"
            :url     "/api/tasks?requestId=tasks:1"
            :headers {"x-api-key" "k"}}
           (request/request {:endpoint "/api/tasks" :method "GET" :request-id "tasks:1"
                           :headers {"x-api-key" "k"}}))))
  (testing "the credentials mode is a request key, not a header"
    (is (= {:method "GET" :url "/api/tasks?requestId=tasks:1" :credentials "include"}
           (request/request {:endpoint "/api/tasks" :method "GET" :request-id "tasks:1"
                           :credentials "include"}))))
  (testing "the budget rides along too, so the executor reads how long to wait off the request
            rather than deciding it. It is never sent, unlike the two above"
    (is (= {:method "GET" :url "/api/tasks?requestId=tasks:1" :timeout 5000}
           (request/request {:endpoint "/api/tasks" :method "GET" :request-id "tasks:1"
                           :timeout 5000})))))

(deftest request-content-type-wins-over-a-configured-one
  (testing "the write contract is plain JSON, so BareBuild owns content-type. Every other
            header is the author's and passes through beside it"
    (is (= {:method  "POST"
            :url     "/api/tasks?requestId=tasks:w1"
            :body    {"title" "x"}
            :headers {"x-api-key" "k" "content-type" "application/json"}}
           (request/request {:endpoint "/api/tasks" :method "POST" :request-id "tasks:w1"
                           :body {"title" "x"}
                           :headers {"x-api-key"    "k"
                                     "content-type" "text/plain"}}))))
  (testing "a bodiless request keeps the configured content-type, nothing is imposed"
    (is (= {"content-type" "text/plain"}
           (:headers (request/request {:endpoint "/api/tasks" :method "GET" :request-id "tasks:1"
                                     :headers {"content-type" "text/plain"}}))))))

(deftest merge-request-headers-lets-the-more-specific-win
  (let [req (request/request {:endpoint "/api/tasks" :method "GET" :request-id "tasks:1"
                            :headers {"x-api-key" "k" "authorization" "static"}})]
    (testing "extra headers override the ones already on the request, and add to them"
      (is (= {"x-api-key"     "k"
              "authorization" "Bearer fresh"
              "x-trace"       "1"}
             (:headers (request/merge-request-headers req {"authorization" "Bearer fresh"
                                                         "x-trace"       "1"})))))
    (testing "nothing to merge leaves the request as it was"
      (is (= req (request/merge-request-headers req nil))))))

(deftest merge-request-headers-still-yields-content-type-to-the-protocol
  (testing "a bodied write keeps its JSON content-type whatever is merged in, so a hook cannot
            break the write contract by accident"
    (let [req (request/request {:endpoint "/api/tasks" :method "POST" :request-id "tasks:w1"
                              :body {"title" "x"}})]
      (is (= {"content-type" "application/json" "authorization" "Bearer t"}
             (:headers (request/merge-request-headers req {"authorization"  "Bearer t"
                                                         "content-type"   "text/plain"}))))))
  (testing "a bodiless read has no protocol content-type to defend, so a merged one stands"
    (let [req (request/request {:endpoint "/api/tasks" :method "GET" :request-id "tasks:1"})]
      (is (= {"content-type" "text/plain"}
             (:headers (request/merge-request-headers req {"content-type" "text/plain"})))))))

(deftest request-encodes-query-values
  (is (= "/api/tasks?requestId=tasks:1&search=a+b%26c"
         (:url (request/request {:endpoint "/api/tasks" :method "GET"
                               :request-id "tasks:1" :query {:search "a b&c"}})))
      "values go through URLSearchParams, so separators in a term can't break the query"))

(deftest request-encodes-the-path-segment
  (testing "an id is opaque server data — a separator inside it must not restructure the URL"
    (is (= "/api/tasks/a%2Fb%3Fc?requestId=tasks:w1"
           (:url (request/request {:endpoint "/api/tasks" :segment "a/b?c"
                                 :method "DELETE" :request-id "tasks:w1"}))))
    (is (= "/api/tasks/a%20b%23c?requestId=tasks:w1"
           (:url (request/request {:endpoint "/api/tasks" :segment "a b#c"
                                 :method "DELETE" :request-id "tasks:w1"})))))
  (testing "an ordinary id is unchanged by encoding"
    (is (= "/api/tasks/7?requestId=tasks:w1"
           (:url (request/request {:endpoint "/api/tasks" :segment 7
                                 :method "DELETE" :request-id "tasks:w1"}))))))

