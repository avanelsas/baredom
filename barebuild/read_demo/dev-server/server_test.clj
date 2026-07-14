;; HTTP-level test for the dev-server: call the handler directly (no port bind)
;; and assert the §6.5 envelope. Run with `bb run test:server`.
(ns server-test
  (:require [clojure.test :refer [deftest is run-tests]]
            [clojure.string :as str]
            [cheshire.core :as json]
            [server]))

(defn- get-raw [uri qs]
  (server/handler {:request-method :get :uri uri :query-string qs}))

(defn- get-json [uri qs]
  (let [resp (get-raw uri qs)]
    [(:status resp) (json/parse-string (:body resp) true)]))

(defn- owners [body] (mapv :owner (:value body)))
(defn- starts [body] (mapv :start (:value body)))
(defn- ids [body] (mapv :id (:value body)))

(defn- non-decreasing? [xs]
  (every? #(<= (compare (first %) (second %)) 0) (partition 2 1 xs)))
(defn- non-increasing? [xs]
  (every? #(>= (compare (first %) (second %)) 0) (partition 2 1 xs)))

(deftest health-ok
  (let [resp (server/handler {:request-method :get :uri "/health"})]
    (is (= 200 (:status resp)))
    (is (= "ok" (:body resp)))))

(deftest get-tasks-returns-accepted-envelope
  (let [[status body] (get-json "/api/tasks" "requestId=test-1")]
    (is (= 200 status))
    (is (= "accepted" (:outcome body)))
    (is (= "test-1" (:requestId body)) "echoes the client request id")
    (is (= "tasks:v1" (:revision body)))
    (is (contains? body :value) "accepted envelope carries value")
    (is (contains? body :shape) "accepted envelope carries shape")
    (is (not (contains? body :error)) "accepted envelope has no error")
    (is (= "id" (get-in body [:shape :idKey])))
    (is (= ["owner" "start" "status"]
           (->> (get-in body [:shape :fields]) (map :key) (filter #{"owner" "start" "status"})))
        "shape declares the display fields")
    (is (= 10 (count (:value body))) "a page holds page-size rows")
    (is (every? #(contains? % :id) (:value body)) "every row has the id-key")
    (is (every? #(#{"todo" "doing" "done"} (:status %)) (:value body))
        "status is constrained to the known set")
    (is (= {:page 1 :pageSize 10 :totalPages 4 :totalCount 40} (:pageInfo body))
        "pageInfo carries the server's pagination state")))

(deftest empty-query-echoes-empty
  (let [[_ body] (get-json "/api/tasks" nil)]
    (is (= {} (:query body)) "no owned params (default page 1) -> empty query echo")
    (is (= (vec (range 1 11)) (ids body)) "no sort -> natural id order, first page")))

;; --- step 2a: sorting + normalized echo ------------------------------------

(deftest sort-owner-ascending
  (let [[_ body] (get-json "/api/tasks" "sort=owner&direction=asc")]
    (is (= {:sort "owner" :direction "asc"} (:query body)) "echoes the normalized query")
    (is (= 10 (count (owners body))) "one page")
    (is (= "Alice" (first (owners body))) "smallest owner first")
    (is (non-decreasing? (owners body)) "page is sorted ascending")))

(deftest missing-direction-normalizes-to-asc
  (let [[_ body] (get-json "/api/tasks" "sort=owner")]
    (is (= "asc" (get-in body [:query :direction])) "server fills in the default direction")
    (is (non-decreasing? (owners body)) "sorted ascending")))

(deftest sort-descending
  (let [[_ body] (get-json "/api/tasks" "sort=start&direction=desc")]
    (is (= {:sort "start" :direction "desc"} (:query body)))
    (is (non-increasing? (starts body)) "reverse chronological")))

(deftest invalid-direction-coerces-to-asc
  (let [[_ body] (get-json "/api/tasks" "sort=owner&direction=sideways")]
    (is (= "asc" (get-in body [:query :direction])) "unknown direction coerced to asc")
    (is (non-decreasing? (owners body)))))

;; --- step 3a: rejection ----------------------------------------------------

(deftest unsupported-sort-field-is-rejected
  (let [[status body] (get-json "/api/tasks" "sort=bogus&direction=asc&requestId=r9")]
    (is (= 200 status) "a rejection is a protocol response, not an HTTP error")
    (is (= "rejected" (:outcome body)))
    (is (= "r9" (:requestId body)) "echoes the client request id")
    (is (not (contains? body :value)) "rejected: no value")
    (is (not (contains? body :shape)) "rejected: no shape")
    (is (= "bogus" (get-in body [:query :sort])) "echoes the rejected query")
    (is (= "invalid-query" (get-in body [:error :code])))
    (is (string? (get-in body [:error :message])))
    (is (= "bogus" (get-in body [:error :details :field])))))

(deftest valid-sort-is-not-rejected
  (let [[_ body] (get-json "/api/tasks" "sort=owner")]
    (is (= "accepted" (:outcome body)) "a supported field is still accepted")))

;; --- step 4a: paging -------------------------------------------------------

(deftest second-page-returns-second-slice
  (let [[_ body] (get-json "/api/tasks" "page=2")]
    (is (= (vec (range 11 21)) (ids body)) "page 2 is the next page-size slice")
    (is (= "2" (get-in body [:query :page])) "page is echoed (as a string) when > 1")
    (is (= 2 (get-in body [:pageInfo :page])))))

(deftest out-of-range-page-clamps-to-last
  (let [[_ body] (get-json "/api/tasks" "page=99")]
    (is (= (vec (range 31 41)) (ids body)) "clamped to the last page")
    (is (= 4 (get-in body [:pageInfo :page])))
    (is (= "4" (get-in body [:query :page])) "echoes the clamped page")))

(deftest default-page-omitted-from-echo
  (let [[_ body] (get-json "/api/tasks" "page=1")]
    (is (nil? (get-in body [:query :page])) "the default page 1 is not echoed")
    (is (= 1 (get-in body [:pageInfo :page])))))

(deftest sort-and-page-compose
  (let [[_ body] (get-json "/api/tasks" "sort=id&direction=desc&page=1")]
    (is (= {:sort "id" :direction "desc"} (:query body)) "page 1 omitted; sort echoed")
    (is (= (vec (range 40 30 -1)) (ids body)) "sorted by id desc, first page")))

;; --- step 7a: search filtering + echo --------------------------------------

(deftest search-filters-and-is-echoed
  (let [[_ body] (get-json "/api/tasks" "search=alice")]
    (is (= {:search "alice"} (:query body)) "the term is echoed so it round-trips")
    (is (= [1 11 21 31] (ids body)) "only the Alice rows survive the filter")
    (is (every? #(= "Alice" %) (owners body)) "every surviving row matches")
    (is (= 4 (get-in body [:pageInfo :totalCount])) "pageInfo counts the filtered set")
    (is (= 1 (get-in body [:pageInfo :totalPages])) "four rows fit on one page")))

(deftest search-is-case-insensitive-echo-preserves-case
  (let [[_ body] (get-json "/api/tasks" "search=aLiCe")]
    (is (= {:search "aLiCe"} (:query body)) "echo keeps the term as typed")
    (is (= [1 11 21 31] (ids body)) "matching ignores case")))

(deftest blank-search-is-ignored
  (let [[_ body] (get-json "/api/tasks" "search=%20%20")]
    (is (= {} (:query body)) "a whitespace-only term is not a filter and is not echoed")
    (is (= 40 (get-in body [:pageInfo :totalCount])) "the full set is served")))

(deftest search-composes-with-sort
  (let [[_ body] (get-json "/api/tasks" "search=alice&sort=id&direction=desc")]
    (is (= {:search "alice" :sort "id" :direction "desc"} (:query body)) "both echoed")
    (is (= [31 21 11 1] (ids body)) "filter then sort: Alice rows, id descending")))

(deftest search-narrows-pagination-across-pages
  (let [[_ p1] (get-json "/api/tasks" "search=done")
        [_ p2] (get-json "/api/tasks" "search=done&page=2")]
    (is (= 13 (get-in p1 [:pageInfo :totalCount])) "thirteen done rows")
    (is (= 2 (get-in p1 [:pageInfo :totalPages])) "spanning two filtered pages")
    (is (= 10 (count (ids p1))) "first filtered page is full")
    (is (= 3 (count (ids p2))) "second filtered page holds the remainder")
    (is (= "2" (get-in p2 [:query :page])) "the filtered second page is echoed")))

(deftest search-clamps-out-of-range-page
  (let [[_ body] (get-json "/api/tasks" "search=alice&page=3")]
    (is (nil? (get-in body [:query :page])) "one filtered page -> clamped to 1, page omitted")
    (is (= 1 (get-in body [:pageInfo :page])))
    (is (= [1 11 21 31] (ids body)) "still the four Alice rows")))

;; --- step 5a: failure fixtures + SSR boot -----------------------------------

(deftest fixture-bad-outcome-is-unknown-outcome
  (let [[status body] (get-json "/api/tasks" "fixture=bad-outcome")]
    (is (= 200 status))
    (is (= "banana" (:outcome body)) "an unknown outcome -> a protocol failure for the client")
    (is (not (contains? body :value)))
    (is (not (contains? body :shape)))))

(deftest fixture-missing-shape-drops-a-required-member
  (let [[status body] (get-json "/api/tasks" "fixture=missing-shape")]
    (is (= 200 status))
    (is (= "accepted" (:outcome body)))
    (is (contains? body :value) "value still present")
    (is (not (contains? body :shape)) "shape missing -> protocol failure")))

(deftest fixture-unparseable-body-is-not-json
  (let [resp (get-raw "/api/tasks" "fixture=unparseable")]
    (is (= 200 (:status resp)))
    (is (thrown? Exception (json/parse-string (:body resp)))
        "the body is deliberately not valid JSON")))

(deftest fixture-contract-violates-the-shape
  (let [[status body] (get-json "/api/tasks" "fixture=contract")]
    (is (= 200 status))
    (is (= "accepted" (:outcome body)) "well-formed envelope...")
    (is (not (contains? (first (:value body)) :status))
        "...but the first row is missing the declared 'status' field")))

(deftest fixture-500-is-a-transport-error
  (is (= 500 (:status (get-raw "/api/tasks" "fixture=500")))
      "a non-2xx status the client reads as a network failure"))

(deftest boot-page-embeds-the-first-response
  (let [resp (get-raw "/demo/boot" nil)
        body (:body resp)]
    (is (= 200 (:status resp)))
    (is (str/includes? (get-in resp [:headers "content-type"]) "text/html"))
    (is (str/includes? body "type=\"application/json\"") "carries a JSON embed")
    (is (str/includes? body "\"outcome\":\"accepted\"") "the embed is an accepted envelope")))

(deftest dist-serving-rejects-traversal-and-missing
  (is (= 404 (:status (get-raw "/dist/../server.clj" nil))) "no path traversal")
  (is (= 404 (:status (get-raw "/dist/nope.js" nil))) "missing file -> 404"))

(defn run []
  (let [{:keys [fail error]} (run-tests 'server-test)]
    (System/exit (if (pos? (+ (or fail 0) (or error 0))) 1 0))))
