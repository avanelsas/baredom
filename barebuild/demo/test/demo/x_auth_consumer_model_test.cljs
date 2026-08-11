(ns demo.x-auth-consumer-model-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [clojure.string :as str]
            [barebuild.resource :as resource]
            [demo.x-auth-consumer.model :as model]))

(def ^:private unauthorized
  {:cause :network :error {:kind :http-status :status 401}})

(deftest unauthorized?-recognises-a-refused-credential
  (is (true? (model/unauthorized? unauthorized))))

(deftest unauthorized?-is-false-for-every-other-failure
  (testing "another http status"
    (is (false? (model/unauthorized? {:cause :network
                                      :error {:kind :http-status :status 500}}))))
  (testing "a network failure that is not an http status"
    (is (false? (model/unauthorized? {:cause :network :error {:kind :offline}}))))
  (testing "a rejection, which never carries a status"
    (is (false? (model/unauthorized? {:cause :rejected :response {}}))))
  (testing "no failure at all"
    (is (false? (model/unauthorized? nil)))))

(deftest failure-message-covers-every-cause
  (is (= "The server sent an unexpected response." (model/failure-message {:cause :protocol})))
  (is (= "The server's data didn't match the expected format."
         (model/failure-message {:cause :contract})))
  (is (= "Nope" (model/failure-message {:cause :rejected :response {:error {:message "Nope"}}})))
  (is (= "Something went wrong." (model/failure-message {:cause :something-else}))))

(deftest failure-message-does-not-call-an-http-error-unreachable
  (testing "a status means the request arrived and the server answered with it"
    (is (= "The server answered with HTTP 500."
           (model/failure-message {:cause :network
                                   :error {:kind :http-status :status 500}})))
    (is (= "The server answered with HTTP 404."
           (model/failure-message {:cause :network
                                   :error {:kind :http-status :status 404}}))))
  (testing "only the absence of a status reads as unreachable"
    (is (= "The request did not reach the server."
           (model/failure-message {:cause :network :error {:kind :offline}})))
    (is (= "The request did not reach the server."
           (model/failure-message {:cause :network :error {:kind :timeout :after 60000}})))
    (is (= "The request did not reach the server." (model/failure-message {:cause :network})))))

;; --- the demo gesture against the real step ---------------------------------
;; Every button submits a refresh, which re-enters `step` as [:refresh]. Run here rather than
;; trusted: a gesture that reads correctly and moves nothing is the failure this demo already hit.

(def ^:private secure
  (resource/initial {:resource/id "secure"
                     :endpoint    "/api/secure/tasks"
                     :url-intent  {}}))

(defn- fetched?
  "Whether `step` answered `event` with a :fetch effect."
  [r event]
  (boolean (some (fn [[fx _]] (= :fetch fx)) (:effects (resource/step r event)))))

(deftest the-expire-then-reauthenticate-sequence-issues-both-reads
  (let [asked     (resource/step secure [:refresh])
        in-flight (:request/id (:active-request (:resource asked)))
        refused   (:resource (resource/step (:resource asked)
                                            [:network-failed {:request/id in-flight
                                                              :error {:kind :http-status
                                                                      :status 401}}]))]
    (testing "expiring the token and asking again opens a read"
      (is (some? in-flight)))
    (testing "the refusal leaves nothing in flight, and is the failure the consumer branches on"
      (is (nil? (:active-request refused)))
      (is (true? (model/unauthorized? (:last-failure refused)))))
    (testing "re-authenticating asks the same question again, which is what repaints the table"
      (is (true? (fetched? refused [:refresh]))
          "an intent a failure already answered is exactly what refresh exists to re-ask"))
    (testing "and none of it ever reached the URL"
      (is (= {} (:url-intent refused))
          "the URL carries what the read is about, and a retry is not about anything"))))

(deftest rows-reads-opaque-string-keys-in-column-order
  (let [accepted {:value [{"id" 1 "title" "Audit" "owner" "Alice" "status" "todo"}]}]
    (is (= [["Audit" "Alice" "todo"]] (model/rows accepted))
        "cells come back in column order, not as a map the caller re-orders"))
  (testing "a field the record does not carry is an empty cell, never a missing one"
    (is (= [["Audit" "" ""]] (model/rows {:value [{"title" "Audit"}]}))))
  (testing "no rows"
    (is (= [] (model/rows {:value []})))
    (is (= [] (model/rows {})))))

(deftest every-row-has-one-cell-per-column
  (let [accepted {:value [{"title" "a"} {"title" "b"}]}]
    (is (every? #(= (count model/columns) (count %)) (model/rows accepted))
        "the header and the cells are driven by one table, so they cannot disagree")))

(deftest token-label-flags-the-refused-token
  (is (= "Next request carries: demo-3"
         (model/token-label {:token "demo-3" :refused? false})))
  (is (= "Next request carries: demo-expired (the server refuses this one)"
         (model/token-label {:token "demo-expired" :refused? true}))))

(deftest token-label-is-empty-when-there-is-no-credential
  (is (= "" (model/token-label {})))
  (is (= "" (model/token-label {:token nil})))
  (is (= "" (model/token-label {:token "   "}))))

(deftest row-count-label-reads-naturally
  (is (= "no rows" (model/row-count-label 0)))
  (is (= "1 row" (model/row-count-label 1)))
  (is (= "12 rows" (model/row-count-label 12))))

(deftest unauthorized-banner-names-the-credential-and-the-status
  (let [text (model/unauthorized-banner "demo-expired")]
    (is (str/includes? text "demo-expired"))
    (is (str/includes? text "401"))))

(deftest log-line-records-an-accepted-exchange
  (is (= {:token "demo-1" :status "--" :outcome "accepted" :detail "2 rows"}
         (model/log-line "demo-1" {:accepted {:value [{"id" 1} {"id" 2}]}}))))

(deftest log-line-states-no-status-it-did-not-observe
  (testing "an accepted envelope carries no HTTP status, since a non-2xx never became one"
    (is (= "--" (:status (model/log-line "demo-1" {:accepted {:value []}})))
        "printing 200 here would be the page stating what it was never told")))

(deftest log-line-takes-the-refusal-status-from-the-failure
  (testing "not from a literal, so the log cannot disagree with what arrived"
    (is (= "401" (:status (model/log-line "demo-expired" {:failure unauthorized}))))
    (is (= "403" (:status (model/log-line "demo-1"
                                          {:failure {:cause :network
                                                     :error {:kind :http-status
                                                             :status 403}}}))))))

(deftest log-line-records-a-refused-credential
  (let [entry (model/log-line "demo-expired" {:failure unauthorized})]
    (is (= "demo-expired" (:token entry)))
    (is (= "401" (:status entry)))
    (is (= "refused" (:outcome entry)))))

(deftest log-line-records-other-failures-with-their-status
  (let [entry (model/log-line "demo-1" {:failure {:cause :network
                                                  :error {:kind :http-status :status 500}}})]
    (is (= "500" (:status entry)))
    (is (= "failed" (:outcome entry)))
    (is (= "The server answered with HTTP 500." (:detail entry))
        "the log line must not claim the request never arrived"))
  (testing "a failure with no status at all"
    (is (= "--" (:status (model/log-line "demo-1" {:failure {:cause :protocol}}))))))

(deftest log-line-prefers-the-failure-over-a-stale-answer
  (testing "a view still holding the last accepted answer alongside a fresh failure"
    (is (= "refused" (:outcome (model/log-line "demo-expired"
                                               {:accepted {:value [{"id" 1}]}
                                                :failure  unauthorized}))))))

(deftest log-line-is-nil-before-the-first-response
  (is (nil? (model/log-line "demo-1" {})))
  (is (nil? (model/log-line nil {}))))

(deftest log-line-names-a-missing-credential
  (is (= "none" (:token (model/log-line nil {:accepted {:value []}})))))
