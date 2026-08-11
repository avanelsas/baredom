;; HTTP-level test for the dev-server: call the handler directly (no port bind)
;; and assert the envelope in docs/server-contract.md. Run with `bb run test:server`.
(ns server-test
  (:require [clojure.test :refer [deftest is testing run-tests use-fixtures]]
            [clojure.string :as str]
            [cheshire.core :as json]
            [server]))

;; The write tests mutate the in-memory sets; reset before each case so the count-based
;; read assertions hold regardless of run order.
(use-fixtures :each (fn [t] (server/reset-tasks!) (server/reset-projects!) (t)))

(defn- get-raw [uri qs]
  (server/handler {:request-method :get :uri uri :query-string qs}))

(defn- delete-raw [uri qs]
  (server/handler {:request-method :delete :uri uri :query-string qs}))

;; The server reads a request body as a stream, exactly as httpkit hands it one, so the tests send
;; one too rather than making the server accept a second body shape it never sees in production.
(defn- body-stream [s]
  (java.io.ByteArrayInputStream. (.getBytes ^String s "UTF-8")))

(defn- post-raw [uri qs body]
  (server/handler {:request-method :post :uri uri :query-string qs :body (body-stream body)}))

(defn- put-raw [uri qs body]
  (server/handler {:request-method :put :uri uri :query-string qs :body (body-stream body)}))

(defn- record-json [m] (json/generate-string m))

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
    (is (= ["title" "owner" "start" "status"]
           (->> (get-in body [:shape :fields]) (map :key) (filter #{"title" "owner" "start" "status"})))
        "shape declares the display fields")
    (is (= 10 (count (:value body))) "a page holds page-size rows")
    (is (every? #(contains? % :id) (:value body)) "every row has the id-key")
    (is (every? #(#{"todo" "doing" "done"} (:status %)) (:value body))
        "status is constrained to the known set")
    (is (= {:page 1 :pageSize 10 :totalPages 4 :totalCount 40} (:pageInfo body))
        "pageInfo carries the server's pagination state")))

(deftest shape-declares-required-and-status-enum
  (let [[_ body] (get-json "/api/tasks" nil)
        by-key   (into {} (map (juxt :key identity) (get-in body [:shape :fields])))]
    (is (true? (get-in by-key ["title" :required])) "title is required")
    (is (true? (get-in by-key ["owner" :required])) "owner is required")
    (is (true? (get-in by-key ["status" :required])) "status is required")
    (is (= ["todo" "doing" "done"] (get-in by-key ["status" :enum])) "status carries its enum")
    (is (nil? (get-in by-key ["end" :required])) "end is optional, no required key emitted")
    (is (nil? (get-in by-key ["owner" :enum])) "owner has no enum")))

(deftest empty-query-echoes-empty
  (let [[_ body] (get-json "/api/tasks" nil)]
    (is (= {} (:query body)) "no owned params (default page 1) -> empty query echo")
    (is (= (vec (range 1 11)) (ids body)) "no sort -> natural id order, first page")))

(deftest seeded-titles-are-unique
  (let [titles (map :title @server/tasks)]
    (is (= 40 (count titles)) "the full seeded set")
    (is (= (count titles) (count (distinct titles))) "every task has a unique title")))

;; --- sorting and the normalized echo ---------------------------------------

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

;; --- rejection -------------------------------------------------------------

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

;; The client adopts an accepted echo as the canonical intent and reverts the URL on a rejection
;; only when the rejected echo is the query it asked for. An echo that drops or invents a key reads
;; as a correction, so the whole query has to come back untouched.
(deftest rejection-echoes-the-whole-query-unchanged
  (let [[_ body] (get-json "/api/tasks" "search=audit&page=2&sort=bogus&direction=desc&requestId=r10")]
    (is (= {:search "audit" :page "2" :sort "bogus" :direction "desc"} (:query body))
        "every param the client sent comes back, and only those")))

(deftest rejection-invents-no-query-key
  (let [[_ body] (get-json "/api/tasks" "sort=bogus&requestId=r11")]
    (is (= {:sort "bogus"} (:query body))
        "a direction the client never sent is not added to the echo")))

;; --- paging ----------------------------------------------------------------

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

;; --- search filtering and its echo -----------------------------------------

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

;; --- relational: users, projects, denormalized tasks, project filter -------

(deftest get-users-returns-accepted-envelope
  (let [[status body] (get-json "/api/users" "requestId=u-1")]
    (is (= 200 status))
    (is (= "accepted" (:outcome body)))
    (is (= "u-1" (:requestId body)) "echoes the client request id")
    (is (= "users:v1" (:revision body)))
    (is (= "id" (get-in body [:shape :idKey])))
    (is (= ["name" "email"] (mapv :key (get-in body [:shape :fields]))))
    (is (= 5 (count (:value body))) "all users, unpaged")
    (is (every? #(contains? % :id) (:value body)) "every user row carries the id-key")
    (is (some #(= "Alice" (:name %)) (:value body)))))

(deftest get-projects-returns-accepted-envelope
  (let [[status body] (get-json "/api/projects" "requestId=p-1")]
    (is (= 200 status))
    (is (= "accepted" (:outcome body)))
    (is (= "projects:v1" (:revision body)))
    (is (= ["name" "description"] (mapv :key (get-in body [:shape :fields]))))
    (is (= 3 (count (:value body))) "all projects, unpaged")
    (is (some #(= "p-1" (:id %)) (:value body)))))

(deftest create-project-appends-and-returns-collection
  (let [resp (post-raw "/api/projects" "requestId=cp-1"
                       (record-json {"name" "Growth Experiments" "description" "A/B tests"}))
        body (json/parse-string (:body resp) true)]
    (is (= 200 (:status resp)) "a write response is HTTP 200")
    (is (= "accepted" (:outcome body)) "a valid create is accepted")
    (is (= "cp-1" (:requestId body)) "echoes the client request id")
    (is (= "projects:v1" (:revision body)) "carries the projects revision")
    (is (= 4 (count (:value body))) "the new project is in the returned collection")
    (let [made (last (:value body))]
      (is (= "p-4" (:id made)) "server mints the next project id")
      (is (= "Growth Experiments" (:name made))))))

(deftest create-project-mutates-the-set
  (post-raw "/api/projects" "requestId=cp-2" (record-json {"name" "Growth Experiments"}))
  (let [[_ body] (get-json "/api/projects" nil)]
    (is (= 4 (count (:value body))) "the created project is observable on a later read")
    (is (some #(= "Growth Experiments" (:name %)) (:value body)))))

(deftest create-project-with-blank-name-is-rejected
  (let [resp (post-raw "/api/projects" "requestId=cp-3" (record-json {"name" ""}))
        body (json/parse-string (:body resp) true)]
    (is (= 200 (:status resp)) "a rejected write is still HTTP 200")
    (is (= "rejected" (:outcome body)))
    (is (= "projects:v1" (:revision body)) "the rejection carries the projects revision")
    (is (= "missing-required" (get-in body [:error :code])))
    (is (= "name" (get-in body [:error :details :field])) "details name the offending field")
    (is (= 3 (get-in (second (get-json "/api/projects" nil)) [:pageInfo :totalCount]))
        "a rejected create does not mutate the set")))

(deftest flat-read-shape-declares-only-display-columns
  (let [[_ body] (get-json "/api/tasks" nil)
        declared (mapv :key (get-in body [:shape :fields]))]
    (is (= ["title" "owner" "start" "end" "est" "status"] declared)
        "the flat demo's declared columns")
    (is (empty? (filterv #{"projectId" "assigneeId" "rank" "assigneeName" "projectName"} declared))
        "the board demo's task keys stay row data rather than declarations. x-table-consumer
         builds its columns from :fields, so declaring one would put a raw id column in the
         flat table")))

(deftest tasks-carry-refs-and-denormalized-names
  (let [[_ body]  (get-json "/api/tasks" nil)
        [_ users] (get-json "/api/users" nil)
        row       (first (:value body))]
    (is (contains? row :projectId) "task carries its project ref")
    (is (contains? row :assigneeId) "task carries its assignee ref")
    (is (contains? row :rank) "task carries its rank")
    (is (contains? row :assigneeName) "server denormalizes the assignee name")
    (is (contains? row :projectName) "server denormalizes the project name")
    (is (= (:assigneeName row)
           (:name (first (filter #(= (:assigneeId row) (:id %)) (:value users)))))
        "the denormalized assignee name matches the referenced user")))

(deftest project-filter-returns-only-that-project-unpaginated
  (let [[status body] (get-json "/api/tasks" "project=p-1")
        n             (count (:value body))]
    (is (= 200 status))
    (is (= "accepted" (:outcome body)))
    (is (= {:project "p-1"} (:query body)) "the project term is echoed so it round-trips")
    (is (every? #(= "p-1" (:projectId %)) (:value body)) "only that project's tasks")
    (is (> n 10) "more than one flat page's worth, so the board read is not paged")
    (is (= n (get-in body [:pageInfo :totalCount])) "every matching row is returned")
    (is (= 1 (get-in body [:pageInfo :totalPages])) "served as a single unpaged response")))

(deftest project-filter-spans-all-status-columns
  (let [[_ body] (get-json "/api/tasks" "project=p-2")]
    (is (= #{"todo" "doing" "done"} (set (map :status (:value body))))
        "a project's tasks spread across every column, not collapsed into one")))

(deftest ranks-are-dense-within-each-project-column
  (let [[_ body]  (get-json "/api/tasks" "project=p-1")
        by-status (group-by :status (:value body))]
    (doseq [[_ rows] by-status]
      (is (= (set (range (count rows))) (set (map :rank rows)))
          "each status column carries dense 0..n-1 ranks"))))

(deftest create-without-refs-still-succeeds
  (let [resp (post-raw "/api/tasks" "requestId=w-compat"
                       (record-json {"title" "Legacy create" "owner" "Zoe"
                                     "start" "2026-03-01" "status" "todo"}))
        body (json/parse-string (:body resp) true)]
    (is (= "accepted" (:outcome body))
        "a flat-demo write with no project/assignee/rank is still accepted")))

;; --- failure fixtures and the SSR boot page --------------------------------

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

;; --- writes: delete --------------------------------------------------------

(deftest delete-returns-post-mutation-envelope
  (let [resp (delete-raw "/api/tasks/7" "requestId=w-1")
        body (json/parse-string (:body resp) true)]
    (is (= 200 (:status resp)) "a write response is HTTP 200")
    (is (= "accepted" (:outcome body)) "the write outcome")
    (is (= "w-1" (:requestId body)) "echoes the client request id")
    (is (= "tasks:v1" (:revision body)) "carries the revision for later concurrency")
    (is (contains? body :value) "an accepted write returns the full post-mutation state")
    (is (contains? body :shape) "including the shape, like a read")
    (is (not (some #{7} (ids body))) "the deleted row is gone from the returned value")
    (is (= 39 (get-in body [:pageInfo :totalCount])) "one fewer row, reflected in pageInfo")
    (is (not (contains? body :error)) "an accepted write has no error")))

(deftest delete-mutates-the-set
  (delete-raw "/api/tasks/7" "requestId=w-1")
  (let [[_ after] (get-json "/api/tasks" nil)]
    (is (not (some #{7} (ids after))) "the deleted id is gone from the read")
    (is (= 39 (get-in after [:pageInfo :totalCount])) "one fewer row in the set")))

(deftest delete-is-idempotent
  (let [r1 (delete-raw "/api/tasks/3" "requestId=a")
        r2 (delete-raw "/api/tasks/3" "requestId=b")]
    (is (= "accepted" (:outcome (json/parse-string (:body r1) true))))
    (is (= "accepted" (:outcome (json/parse-string (:body r2) true)))
        "deleting an already-absent id still accepts")
    (is (= 39 (get-in (second (get-json "/api/tasks" nil)) [:pageInfo :totalCount]))
        "only one row removed by two identical deletes")))

(deftest delete-of-unknown-id-is-a-noop
  (let [resp (delete-raw "/api/tasks/999" "requestId=w-9")
        body (json/parse-string (:body resp) true)]
    (is (= "accepted" (:outcome body)) "an absent id still accepts")
    (is (= 40 (get-in (second (get-json "/api/tasks" nil)) [:pageInfo :totalCount]))
        "the set is unchanged")))

(deftest delete-of-non-numeric-id-is-a-noop
  (let [resp (delete-raw "/api/tasks/abc" "requestId=w-x")
        body (json/parse-string (:body resp) true)]
    (is (= "accepted" (:outcome body)) "a non-integer id parses to nil, matching no row")
    (is (= 40 (get-in (second (get-json "/api/tasks" nil)) [:pageInfo :totalCount]))
        "the set is unchanged")))

(deftest member-id-is-url-decoded
  (let [body (json/parse-string (:body (delete-raw "/api/tasks/%37" "requestId=w-e1")) true)]
    (is (= "accepted" (:outcome body)))
    (is (not (some #{7} (ids body)))
        "the id in the path is URL-encoded, so an encoded 7 addresses row 7")))

(deftest options-advertises-write-verbs
  (let [resp (server/handler {:request-method :options :uri "/api/tasks/7"})]
    (is (= 204 (:status resp)) "preflight is a no-content response")
    (is (str/includes? (get-in resp [:headers "access-control-allow-methods"]) "DELETE")
        "the preflight advertises DELETE")
    (is (str/includes? (get-in resp [:headers "access-control-allow-methods"]) "POST")
        "the preflight advertises POST")
    (is (str/includes? (get-in resp [:headers "access-control-allow-methods"]) "PUT")
        "the preflight advertises PUT")
    (is (str/includes? (get-in resp [:headers "access-control-allow-methods"]) "PATCH")
        "the preflight advertises PATCH for :move")))

;; --- writes: create --------------------------------------------------------

(def ^:private new-task
  {"title" "Ship the release" "owner" "Zoe" "start" "2026-03-01" "end" "2026-03-10" "status" "todo"})

(deftest a-number-field-must-arrive-as-a-number
  (testing "the server checks the parsed body against the declared type, so a number field sent
            as the string a form holds is refused. This is what the client's conform step exists
            to prevent, checked here independently rather than taken on the client's word"
    (let [resp (post-raw "/api/tasks" "requestId=w-n1"
                         (record-json (assoc new-task "est" "5")))
          body (json/parse-string (:body resp) true)]
      (is (= "rejected" (:outcome body)))
      (is (= "invalid-type" (get-in body [:error :code])))
      (is (= "est" (get-in body [:error :details :field])))))
  (testing "the same value as a JSON number is accepted, and comes back as a number"
    (let [resp (post-raw "/api/tasks" "requestId=w-n2"
                         (record-json (assoc new-task "est" 5)))
          body (json/parse-string (:body resp) true)]
      (is (= "accepted" (:outcome body)))
      (let [[_ zoe] (get-json "/api/tasks" "search=Zoe")
            row     (first (:value zoe))]
        (is (= 5 (:est row)) "the written value round-trips as a JSON number")))))

(deftest an-updated-number-field-survives-the-replace
  (testing "update is a full replace of the client-owned fields, so a number field has to be
            carried through it as much as through a create. Reopening an edited row prefills
            from what the server holds, so a field the replace dropped reads as never set"
    (let [resp (put-raw "/api/tasks/1" "requestId=w-n3"
                        (record-json (assoc new-task "est" 7)))
          body (json/parse-string (:body resp) true)]
      (is (= "accepted" (:outcome body)))
      (let [[_ read] (get-json "/api/tasks" nil)
            row      (first (filter #(= 1 (:id %)) (:value read)))]
        (is (= 7 (:est row)) "the written estimate is what a later read returns")))))

(deftest create-appends-and-returns-post-mutation-envelope
  (let [resp (post-raw "/api/tasks" "requestId=w-c1" (record-json new-task))
        body (json/parse-string (:body resp) true)]
    (is (= 200 (:status resp)) "a write response is HTTP 200")
    (is (= "accepted" (:outcome body)) "a valid create is accepted")
    (is (= "w-c1" (:requestId body)) "echoes the client request id")
    (is (= "tasks:v1" (:revision body)))
    (is (contains? body :value) "an accepted create returns the full post-mutation state")
    (is (contains? body :shape))
    (is (= 41 (get-in body [:pageInfo :totalCount])) "the new row is counted in the returned state")
    (is (not (contains? body :error)) "an accepted write has no error")
    (testing "the new row is observable, with a server-minted id"
      (let [[_ zoe] (get-json "/api/tasks" "search=Zoe")
            row     (first (:value zoe))]
        (is (= "Ship the release" (:title row)))
        (is (= "Zoe" (:owner row)))
        (is (= 41 (:id row)) "server assigns the next id")))))

(deftest board-create-lands-at-bottom-of-its-column
  (let [before (count (:value (second (get-json "/api/tasks" "project=p-1"))))]
    (post-raw "/api/tasks" "requestId=w-b1"
              (record-json {"title" "New board card" "owner" "Zoe" "start" "2026-03-01"
                            "status" "todo" "projectId" "p-1"}))
    (let [[_ body] (get-json "/api/tasks" "project=p-1")
          todo     (filter #(= "todo" (:status %)) (:value body))
          made     (first (filter #(= "New board card" (:title %)) todo))]
      (is (= (inc before) (count (:value body))) "the card is added to the project board")
      (is (some? made) "the new card is in the To Do column")
      (is (= (dec (count todo)) (:rank made)) "it lands at the bottom of the column")
      (is (= (set (range (count todo))) (set (map :rank todo)))
          "the column ranks stay dense 0..n-1"))))

(deftest board-create-stores-the-typed-name-as-owner
  (post-raw "/api/tasks" "requestId=w-b2"
            (record-json {"title" "Named card" "owner" "Wendy" "start" "2026-03-01"
                          "status" "todo" "projectId" "p-1"}))
  (let [[_ body] (get-json "/api/tasks" "project=p-1")
        made     (first (filter #(= "Named card" (:title %)) (:value body)))]
    (is (nil? (:assigneeId made)) "the board create carries no user reference")
    (is (nil? (:assigneeName made))
        "with no user, the server honestly reports no assignee name")
    (is (= "Wendy" (:owner made))
        "the typed name is stored as the owner; the card view supplies the display fallback")))

(deftest create-with-end-before-start-is-rejected
  (let [bad  (assoc new-task "start" "2026-03-10" "end" "2026-03-01")
        resp (post-raw "/api/tasks" "requestId=w-c2" (record-json bad))
        body (json/parse-string (:body resp) true)]
    (is (= 200 (:status resp)) "a rejected write is still HTTP 200, not a transport error")
    (is (= "rejected" (:outcome body)))
    (is (= "w-c2" (:requestId body)) "echoes the client request id")
    (is (= "invalid-range" (get-in body [:error :code])))
    (is (string? (get-in body [:error :message])))
    (is (= "end" (get-in body [:error :details :field])) "details name the offending field")
    (is (not (contains? body :value)) "rejected: no value")
    (testing "a rejected create does not mutate the set"
      (is (= 40 (get-in (second (get-json "/api/tasks" nil)) [:pageInfo :totalCount]))))))

(deftest create-with-equal-start-and-end-is-accepted
  (let [same (assoc new-task "start" "2026-03-05" "end" "2026-03-05")
        body (json/parse-string (:body (post-raw "/api/tasks" "requestId=w-c3" (record-json same))) true)]
    (is (= "accepted" (:outcome body)) "end == start satisfies end >= start")))

;; --- writes: create structural validation (server-side, defense-in-depth) --

(defn- create-rejection [request-id record]
  (let [resp (post-raw "/api/tasks" (str "requestId=" request-id) (record-json record))
        body (json/parse-string (:body resp) true)]
    (is (= 200 (:status resp)) "a rejected create is still HTTP 200")
    (is (= "rejected" (:outcome body)))
    (is (= 40 (get-in (second (get-json "/api/tasks" nil)) [:pageInfo :totalCount]))
        "a rejected create does not mutate the set")
    (:error body)))

(deftest create-missing-required-is-rejected
  (let [error (create-rejection "w-c4" (assoc new-task "owner" ""))]
    (is (= "missing-required" (:code error)))
    (is (= "owner" (get-in error [:details :field])) "details name the offending field")))

(deftest create-invalid-type-is-rejected
  (let [error (create-rejection "w-c5" (assoc new-task "start" "not-a-date"))]
    (is (= "invalid-type" (:code error)))
    (is (= "start" (get-in error [:details :field])))))

(deftest create-value-not-in-enum-is-rejected
  (let [error (create-rejection "w-c6" (assoc new-task "status" "archived"))]
    (is (= "invalid-value" (:code error)))
    (is (= "status" (get-in error [:details :field])))))

(deftest create-with-blank-optional-end-is-accepted-and-reads-clean
  (let [resp (post-raw "/api/tasks" "requestId=w-c7" (record-json (assoc new-task "end" "")))
        body (json/parse-string (:body resp) true)]
    (is (= "accepted" (:outcome body)) "end is optional, so a blank end is not rejected")
    (testing "the stored row's blank end normalizes to nil (JSON null), not \"\", so reads stay valid"
      (let [[_ after] (get-json "/api/tasks" "search=Zoe")
            row       (first (:value after))]
        (is (nil? (:end row)) "blank optional end stored as null")))))

;; --- writes: update --------------------------------------------------------

(defn- update-rejection [uri request-id record]
  (let [resp (put-raw uri (str "requestId=" request-id) (record-json record))
        body (json/parse-string (:body resp) true)
        [_ after] (get-json "/api/tasks" nil)]
    (is (= 200 (:status resp)) "a rejected update is still HTTP 200")
    (is (= "rejected" (:outcome body)))
    (is (= request-id (:requestId body)) "echoes the client request id")
    (is (not (contains? body :value)) "rejected: no value")
    (is (= 40 (get-in after [:pageInfo :totalCount])) "a rejected update does not mutate the set")
    (is (= "Update API contract" (:title (first (filter #(= 7 (:id %)) (:value after)))))
        "the targeted row keeps its stored values")
    (:error body)))

(deftest update-replaces-the-row-and-returns-post-mutation-envelope
  (let [resp (put-raw "/api/tasks/7" "requestId=w-u1" (record-json new-task))
        body (json/parse-string (:body resp) true)
        row  (first (filter #(= 7 (:id %)) (:value body)))]
    (is (= 200 (:status resp)) "a write response is HTTP 200")
    (is (= "accepted" (:outcome body)) "a valid update is accepted")
    (is (= "w-u1" (:requestId body)) "echoes the client request id")
    (is (= "tasks:v1" (:revision body)))
    (is (contains? body :value) "an accepted update returns the full post-mutation state")
    (is (contains? body :shape))
    (is (not (contains? body :error)) "an accepted write has no error")
    (testing "the replaced row is in the returned value, in place, every field from the record"
      (is (= 40 (get-in body [:pageInfo :totalCount])) "an update does not change the count")
      (is (= (vec (range 1 11)) (ids body)) "the row keeps its id and its place in the set")
      (is (= "Ship the release" (:title row)) "every field comes from the record")
      (is (= "Zoe" (:owner row)))
      (is (= "2026-03-01" (:start row)))
      (is (= "todo" (:status row))))))

(deftest update-replaces-rather-than-merges
  (put-raw "/api/tasks/7" "requestId=w-u2" (record-json (dissoc new-task "end")))
  (let [[_ all] (get-json "/api/tasks" nil)
        row     (first (filter #(= 7 (:id %)) (:value all)))]
    (is (nil? (:end row))
        "PUT is a full replace: an omitted optional field is cleared, not carried over")
    (is (= "Ship the release" (:title row)) "the fields the record did carry are stored")))

(deftest update-with-blank-optional-end-reads-clean
  (put-raw "/api/tasks/7" "requestId=w-u3" (record-json (assoc new-task "end" "")))
  (let [[_ all] (get-json "/api/tasks" nil)
        row     (first (filter #(= 7 (:id %)) (:value all)))]
    (is (nil? (:end row)) "a blank optional end stores as null, not \"\", so reads stay valid")))

(deftest update-carrying-project-keeps-it-on-the-board
  (let [[_ before] (get-json "/api/tasks" "project=p-1")
        id         (:id (first (:value before)))]
    (put-raw (str "/api/tasks/" id) "requestId=w-u5"
             (record-json {"title" "Edited" "owner" "Alice" "start" "2026-01-01"
                           "status" "todo" "projectId" "p-1" "assigneeId" "u-1"}))
    (let [[_ after] (get-json "/api/tasks" "project=p-1")]
      (is (some #(= id (:id %)) (:value after))
          "a full-replace update that carries projectId keeps the task on its board")
      (is (= "p-1" (:projectId (first (filter #(= id (:id %)) (:value after)))))
          "the carried projectId is stored"))))

(deftest update-of-unknown-id-is-rejected
  (let [error (update-rejection "/api/tasks/999" "w-u4" new-task)]
    (is (= "not-found" (:code error)) "update is not idempotent over an absent row")
    (is (string? (:message error)))
    (is (= "999" (get-in error [:details :id])) "details name the id that matched no row")
    (is (nil? (get-in error [:details :field]))
        "no field is named, since the record is fine and the target is not")))

(deftest update-of-non-numeric-id-is-rejected
  (let [error (update-rejection "/api/tasks/abc" "w-u5" new-task)]
    (is (= "not-found" (:code error)) "a non-integer id matches no row")
    (is (= "abc" (get-in error [:details :id])) "the raw id is echoed as asked for")))

(deftest update-with-end-before-start-is-rejected
  (let [bad   (assoc new-task "start" "2026-03-10" "end" "2026-03-01")
        error (update-rejection "/api/tasks/7" "w-u6" bad)]
    (is (= "invalid-range" (:code error)) "the cross-field rule applies to updates too")
    (is (= "end" (get-in error [:details :field])) "details name the offending field")))

(deftest update-missing-required-is-rejected
  (let [error (update-rejection "/api/tasks/7" "w-u7" (assoc new-task "owner" ""))]
    (is (= "missing-required" (:code error)))
    (is (= "owner" (get-in error [:details :field])))))

(deftest update-invalid-type-is-rejected
  (let [error (update-rejection "/api/tasks/7" "w-u8" (assoc new-task "start" "not-a-date"))]
    (is (= "invalid-type" (:code error)))
    (is (= "start" (get-in error [:details :field])))))

(deftest update-value-not-in-enum-is-rejected
  (let [error (update-rejection "/api/tasks/7" "w-u9" (assoc new-task "status" "archived"))]
    (is (= "invalid-value" (:code error)))
    (is (= "status" (get-in error [:details :field])))))

;; --- writes: the :move op (server-owned rank) ------------------------------

(defn- patch-raw [uri qs body]
  (server/handler {:request-method :patch :uri uri :query-string qs :body (body-stream body)}))

(defn- project-tasks [project]
  (second (get-json "/api/tasks" (str "project=" project))))

(defn- column-ranks [body status]
  (->> (:value body) (filter #(= status (:status %))) (map :rank) sort vec))

;; p-1 seeds five tasks per status column, ranks 0..4. Task 1 is (p-1, todo, rank 0). A move
;; carries only {status, index}; rank is server-owned and never in a record.

(deftest move-places-card-and-redenses-columns
  (patch-raw "/api/tasks/1" "requestId=w-m1" (record-json {"status" "done" "index" 2}))
  (let [body  (project-tasks "p-1")
        moved (first (filter #(= 1 (:id %)) (:value body)))]
    (is (= "done" (:status moved)) "the card is in its new column")
    (is (= 2 (:rank moved)) "at the requested index")
    (is (= [0 1 2 3 4 5] (column-ranks body "done")) "destination column re-densed to 0..5")
    (is (= [0 1 2 3] (column-ranks body "todo")) "the vacated column closed its gap")))

(deftest move-reorders-within-a-column
  (patch-raw "/api/tasks/1" "requestId=w-m2" (record-json {"status" "todo" "index" 3}))
  (let [body (project-tasks "p-1")
        t1   (first (filter #(= 1 (:id %)) (:value body)))]
    (is (= "todo" (:status t1)))
    (is (= 3 (:rank t1)) "the card lands at its new index within the same column")
    (is (= [0 1 2 3 4] (column-ranks body "todo")) "the column stays dense 0..4")))

(deftest move-touches-only-status-and-position
  (let [before (first (filter #(= 1 (:id %)) (:value (project-tasks "p-1"))))]
    (patch-raw "/api/tasks/1" "requestId=w-m3" (record-json {"status" "doing" "index" 0}))
    (let [after (first (filter #(= 1 (:id %)) (:value (project-tasks "p-1"))))]
      (is (= "doing" (:status after)))
      (is (= (:title before) (:title after)) "the rest of the row is kept")
      (is (= (:assigneeId before) (:assigneeId after))))))

(deftest move-of-unknown-id-is-rejected
  (let [body (json/parse-string (:body (patch-raw "/api/tasks/999" "requestId=w-m4"
                                                  (record-json {"status" "done" "index" 0}))) true)]
    (is (= "rejected" (:outcome body)))
    (is (= "not-found" (get-in body [:error :code])))))

(deftest move-to-unknown-status-is-rejected
  (let [body (json/parse-string (:body (patch-raw "/api/tasks/1" "requestId=w-m5"
                                                  (record-json {"status" "archived" "index" 0}))) true)]
    (is (= "rejected" (:outcome body)))
    (is (= "invalid-value" (get-in body [:error :code])))))

(deftest update-preserves-server-owned-rank
  (let [before (:rank (first (filter #(= 1 (:id %)) (:value (project-tasks "p-1")))))]
    (put-raw "/api/tasks/1" "requestId=w-m6"
             (record-json {"title" "Renamed" "owner" "Alice" "start" "2026-01-01"
                           "status" "todo" "projectId" "p-1" "assigneeId" "u-1"}))
    (let [after (first (filter #(= 1 (:id %)) (:value (project-tasks "p-1"))))]
      (is (= "Renamed" (:title after)) "the edit applied")
      (is (= before (:rank after))
          "rank is server-owned, so an update inside one column leaves it untouched"))))

;; An edit form carries `status`, so a plain update can move a row between columns. The rank it
;; carried places nothing in the column it just joined, so the server re-ranks it there.
(deftest update-into-another-column-redenses-both
  (put-raw "/api/tasks/1" "requestId=w-m7"
           (record-json {"title" "Renamed" "owner" "Alice" "start" "2026-01-01"
                         "status" "done" "projectId" "p-1" "assigneeId" "u-1"}))
  (let [body  (project-tasks "p-1")
        moved (first (filter #(= 1 (:id %)) (:value body)))]
    (is (= "done" (:status moved)) "the edit applied")
    (is (= 5 (:rank moved)) "the row lands at the bottom of its new column")
    (is (= [0 1 2 3 4 5] (column-ranks body "done")) "the destination column stays dense")
    (is (= [0 1 2 3] (column-ranks body "todo")) "the vacated column closed its gap")))

(deftest delete-closes-the-gap-in-its-column
  (delete-raw "/api/tasks/1" "requestId=w-m8")
  (let [body (project-tasks "p-1")]
    (is (= [0 1 2 3] (column-ranks body "todo")) "the column re-denses after a delete")
    (is (= [0 1 2 3 4] (column-ranks body "done")) "an untouched column is left alone")))

;; --- the protected read ----------------------------------------------------

(defn- get-secure
  "GET the protected read with `auth` as the Authorization header, or none when nil."
  [auth]
  (server/handler (cond-> {:request-method :get :uri "/api/secure/tasks" :query-string nil}
                    auth (assoc :headers {"authorization" auth}))))

(deftest secure-read-without-a-token-is-401
  (let [resp (get-secure nil)]
    (is (= 401 (:status resp)))
    (is (= "unauthorized" (:error (json/parse-string (:body resp) true))))))

(deftest secure-read-with-a-minted-token-is-the-accepted-envelope
  (let [resp (get-secure "Bearer demo-1")
        body (json/parse-string (:body resp) true)]
    (is (= 200 (:status resp)))
    (is (= "accepted" (:outcome body)) "a credential opens the route, the envelope is unchanged")
    (is (seq (:value body)))
    (is (some? (:shape body)))))

(deftest secure-read-accepts-a-rotated-token
  (is (= 200 (:status (get-secure "Bearer demo-2"))) "any minted token opens it, not one fixed one")
  (is (= 200 (:status (get-secure "Bearer demo-99")))))

(deftest secure-read-with-the-expired-token-is-401
  (is (= 401 (:status (get-secure "Bearer demo-expired")))))

(deftest secure-read-rejects-a-foreign-or-malformed-token
  (testing "a token this demo never minted"
    (is (= 401 (:status (get-secure "Bearer nope")))))
  (testing "a scheme that is not bearer"
    (is (= 401 (:status (get-secure "Basic demo-1")))))
  (testing "a header with no token at all"
    (is (= 401 (:status (get-secure "Bearer"))))))

(deftest bearer-scheme-is-case-insensitive
  (is (= 200 (:status (get-secure "bearer demo-1"))) "schemes are case-insensitive per RFC 7235"))

(deftest the-query-echo-is-a-closed-vocabulary
  (testing "a field the server does not know never comes back"
    (let [[status body] (get-json "/api/tasks" "requestId=t-echo&attempt=2")]
      (is (= 200 status))
      (is (= {} (:query body))
          "so a client cannot carry a private field through a round trip, and two reads that
           differ only by one produce identical envelopes")))
  (testing "a field it does know is echoed"
    (let [[_ body] (get-json "/api/tasks" "requestId=t-echo2&sort=owner")]
      (is (= "owner" (get-in body [:query :sort]))))))

(deftest preflight-allows-every-header-the-demos-send
  (let [resp    (server/handler {:request-method :options :uri "/api/secure/tasks"})
        allowed (get-in resp [:headers "access-control-allow-headers"])]
    (is (= 204 (:status resp)))
    (testing "the decorator's bearer token"
      (is (str/includes? allowed "authorization")))
    (testing "the static header auth.html declares on its element"
      (is (str/includes? allowed "x-demo-client")
          "an unadvertised header is blocked by the browser before the request is sent"))
    (testing "the write content-type"
      (is (str/includes? allowed "content-type")))))

(defn run []
  (let [{:keys [fail error]} (run-tests 'server-test)]
    (System/exit (if (pos? (+ (or fail 0) (or error 0))) 1 0))))
