#!/usr/bin/env bb
;; BareBuild reference dev-server (the contract oracle + demo backend).
;;
;; Emits JSON INDEPENDENTLY (cheshire over plain maps) — never via BareBuild's
;; wire bijection — so it is an honest second implementation of the §6.5 contract.
;; Keyword map keys serialize to the exact wire spelling (:idKey -> "idKey",
;; :requestId -> "requestId", row keys -> their opaque strings).
;;
;; Step 5a: on top of sort/direction (2a), rejection (3a) and paging (4a), the server can
;; now be asked — via `?fixture=…` on /api/tasks — to produce each FAILURE MODE the client
;; must handle (5b): a broken protocol envelope (`bad-outcome`/`missing-shape`/`unparseable`),
;; a contract-violating payload (`contract`), and a transport failure (`500`/`slow`). It also
;; serves an SSR boot page at /demo/boot (the first response embedded in the HTML, §7.4) plus
;; the compiled modules under /dist so that page runs same-origin. Start it with `bb run
;; server` (the bb.edn task); never auto-starts on require.
;;
;; Step 7a: /api/tasks now honors a `search` param — a case-insensitive substring over the
;; display fields — applied as filter -> sort -> slice, with pageInfo reflecting the FILTERED
;; count. The term is ALSO echoed in the normalized query: without the echo the client's
;; echo-adoption would treat the missing key as a correction and strip the term from the URL.
(ns server
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [org.httpkit.server :as http]
            [cheshire.core :as json]))

(def port 8090)

;; --- the fixed object with known behaviour ---------------------------------

(def revision "tasks:v1")

(def ^:private owners
  ["Alice" "Bob" "Carmen" "Dev" "Erin" "Frank" "Grace" "Heidi" "Ivan" "Judy"])

(def ^:private statuses ["todo" "doing" "done"])

(defn- gen-task
  "Deterministic demo task for 1-based id `i`."
  [i]
  (let [day (inc (mod (dec i) 28))]
    {:id     i
     :owner  (nth owners (mod (dec i) (count owners)))
     :start  (format "2026-01-%02d" day)
     :end    (format "2026-02-%02d" day)
     :status (nth statuses (mod (dec i) 3))}))

;; 40 rows so pagination is worth demoing. The set is mutable server state — a delete
;; (step W1a) removes a row — so it lives in an atom. This is honest backend state, NOT a
;; BareBuild value/step: the stateless-component rule governs the client runtime, not the
;; oracle. `initial-tasks` is the pristine set the tests reset to between cases.
(def ^:private initial-tasks (mapv gen-task (range 1 41)))
(def tasks (atom initial-tasks))

(defn reset-tasks!
  "Restore the pristine 40-row set. A test seam so mutating cases don't leak into the
   count-based read assertions regardless of run order."
  []
  (reset! tasks initial-tasks))

;; Shape: structural consumption invariants only (§3.5). id is the identity key,
;; declared separately from the consumable display fields.
;; `required`/`enum` are the write-side consumption invariants (W2): a field with no
;; `required` key is optional, one with no `enum` is unconstrained. They round-trip through
;; the read shape unchanged and drive local write validation (validate-payload) in both
;; directions. `end` is intentionally optional.
(def shape
  {:idKey  "id"
   :fields [{:key "owner"  :type "string" :required true}
            {:key "start"  :type "date"   :required true}
            {:key "end"    :type "date"}
            {:key "status" :type "string" :required true :enum ["todo" "doing" "done"]}]})

;; Fields the server can sort by — derived from the shape (id-key + display fields).
(def ^:private sortable-fields
  (into #{(:idKey shape)} (map :key (:fields shape))))

;; --- paging (step 4a) ------------------------------------------------------

(def ^:private page-size 10)

(defn- total-pages
  "Pages needed to hold `n` rows (at least 1). `n` is the count of the set actually being
   served — the full task set, or the filtered subset once a search narrows it (7a)."
  [n]
  (max 1 (int (Math/ceil (/ n (double page-size))))))

(defn- parse-page
  "1-based page number from `params`, clamped to [1, tp]. Absent or non-numeric -> 1.
   `tp` is the total-pages of the set being served, so a search that narrows the set to
   fewer pages clamps an out-of-range page down (7a)."
  [params tp]
  (let [raw (get params "page")
        n   (if raw (try (Integer/parseInt raw) (catch Exception _ 1)) 1)]
    (-> n (max 1) (min tp))))

(defn- paginate
  "The page-size slice of `ts` for 1-based `page`."
  [ts page]
  (->> ts (drop (* (dec page) page-size)) (take page-size) vec))

;; --- search (step 7a) ------------------------------------------------------

(defn- search-term
  "The trimmed `search` param, or nil when absent or blank. Case is preserved (matching is
   case-insensitive, but the term is echoed as given so it round-trips through the URL)."
  [params]
  (let [s (some-> (get params "search") str/trim)]
    (when-not (str/blank? s) s)))

(defn filter-tasks
  "Rows whose display fields contain `term` as a case-insensitive substring. A nil term
   leaves the set untouched. Applied before sort and slice."
  [ts term]
  (if term
    (let [needle (str/lower-case term)]
      (filterv (fn [t]
                 (some (fn [v] (str/includes? (str/lower-case (str v)) needle))
                       ((juxt :owner :start :end :status) t)))
               ts))
    ts))

;; --- query handling (steps 2a + 4a) ----------------------------------------

(defn normalize-query
  "Canonicalize the raw query into what the server actually honors. Single source of
   truth: `accepted-envelope` applies it AND echoes it, so the echo can never drift.

   A non-blank `search` term is kept (7a) — it is the reason `tp` is threaded in, so a
   narrowed set clamps `page` and echoes the clamped value. A valid `sort` field is kept
   with a `direction` that defaults to \"asc\" (any value other than \"desc\" is coerced to
   \"asc\"); an absent/unknown sort yields no sort keys. `page` is echoed only when it is
   not the default page 1 (kept as a string — the URL's value domain is strings)."
  [params tp]
  (let [sort (get params "sort")
        term (search-term params)
        page (parse-page params tp)]
    (cond-> {}
      term
      (assoc "search" term)

      (contains? sortable-fields sort)
      (assoc "sort" sort
             "direction" (if (= "desc" (get params "direction")) "desc" "asc"))

      (> page 1)
      (assoc "page" (str page)))))

(defn sort-tasks
  "Order the task set per a normalized query. Ties break on :id so the order is
   deterministic; an empty query leaves the natural (id) order untouched."
  [ts nq]
  (if-let [field (get nq "sort")]
    (let [asc (sort-by (juxt (keyword field) :id) ts)]
      (if (= "desc" (get nq "direction"))
        (vec (reverse asc))
        (vec asc)))
    ts))

;; --- rejection (step 3a) ---------------------------------------------------

(defn unsupported-sort?
  "True when the request asks to sort by a field the server does not support. Once the
   client can surface it (3b), rejecting is the honest answer instead of silently
   ignoring the field (the 2a placeholder)."
  [params]
  (let [sort (get params "sort")]
    (and (some? sort)
         (not (contains? sortable-fields sort)))))

(defn rejected-envelope
  "Build the §6.5 rejected envelope: it echoes the rejected query and a structured
   error, and — unlike accepted — carries no value/shape. It is still an HTTP 200
   protocol response, not a transport error."
  [params]
  (let [bad-field (get params "sort")]
    {:outcome   "rejected"
     :requestId (get params "requestId" "server-boot")
     :revision  revision
     :query     {"sort"      bad-field
                 "direction" (if (= "desc" (get params "direction")) "desc" "asc")}
     :error     {:code    "invalid-query"
                 :message (str "Sorting by \"" bad-field "\" is not supported.")
                 :details {:field bad-field}}}))

;; --- request/response plumbing ---------------------------------------------

(def ^:private cors-headers
  {"access-control-allow-origin"  "*"
   "access-control-allow-methods" "GET,POST,DELETE,OPTIONS"
   "access-control-allow-headers" "content-type"})

(defn- parse-query [qs]
  (if (str/blank? qs)
    {}
    (into {}
          (for [pair (str/split qs #"&")
                :let [[k v] (str/split pair #"=" 2)]]
            [(java.net.URLDecoder/decode k "UTF-8")
             (java.net.URLDecoder/decode (or v "") "UTF-8")]))))

(defn- json-response [status body]
  {:status  status
   :headers (merge {"content-type" "application/json"} cors-headers)
   :body    (json/generate-string body)})

(defn accepted-envelope
  "Build the complete accepted envelope: rows are filtered by the search term (7a), then
   sorted, then sliced to the requested page. The query echo is the server-normalized query,
   and `pageInfo` reflects the FILTERED set (current page, page size, total pages/rows) so
   the client's pagination and any count display follow the search."
  [params]
  (let [term     (search-term params)
        filtered (filter-tasks @tasks term)
        tp       (total-pages (count filtered))
        nq       (normalize-query params tp)
        page     (parse-page params tp)
        sorted   (sort-tasks filtered nq)]
    {:outcome   "accepted"
     :requestId (get params "requestId" "server-boot")
     :revision  revision
     :query     nq
     :value     (paginate sorted page)
     :shape     shape
     :pageInfo  {:page       page
                 :pageSize   page-size
                 :totalPages tp
                 :totalCount (count filtered)}}))

;; --- writes: acks (steps W1a / W3a) ----------------------------------------

(defn accepted-ack
  "The §8 accepted write ack: `outcome` + echoed `requestId` + `revision`. Shared by delete
   and create — it carries NO `value`/`shape`; the client refetches to observe the new state
   (WRITES-PLAN). `revision` is threaded now so opt-in concurrency is purely additive later;
   single-user last-write-wins ignores it."
  [params]
  {:outcome   "accepted"
   :requestId (get params "requestId" "server-boot")
   :revision  revision})

(defn write-rejected-ack
  "The §8 rejected write ack: the accepted fields plus a structured `error {code, message,
   details}`. Still HTTP 200 — the outcome carries the verdict; `details` names the offending
   field so the client can map it back onto the form."
  [params error]
  {:outcome   "rejected"
   :requestId (get params "requestId" "server-boot")
   :revision  revision
   :error     error})

;; --- writes: delete (step W1a) ---------------------------------------------

(defn- task-id-from-uri
  "The 1-based id in `/api/tasks/:id`, or nil when the tail is not an integer. A nil id
   simply matches no row, keeping delete a no-op rather than an error."
  [uri]
  (let [raw (subs uri (count "/api/tasks/"))]
    (try (Long/parseLong raw) (catch Exception _ nil))))

(defn delete-task
  "Drop the row whose id is `id` from `ts`. Idempotent: an absent id leaves the set
   unchanged (safe to retry — WRITES-PLAN decision #5)."
  [ts id]
  (filterv #(not= (:id %) id) ts))

;; --- writes: create (step W3a) ---------------------------------------------

(defn- request-record
  "Parse the JSON request body into a record map with opaque string keys. Handles both a
   real InputStream body (dev-server) and a String body (handler tests). nil body -> nil."
  [req]
  (when-let [b (:body req)]
    (json/parse-string (if (string? b) b (slurp b)))))

(defn- blank?
  "nil or blank string — the server's notion of an unfilled field."
  [v]
  (or (nil? v) (and (string? v) (str/blank? v))))

(defn- valid-date?
  "A wire date is an ISO-8601 local date the server can parse."
  [v]
  (and (string? v)
       (try (java.time.LocalDate/parse v) true (catch Exception _ false))))

(defn- type-ok?
  "Value matches the shape field's declared type. Unknown types don't block."
  [type v]
  (case type
    "string" (string? v)
    "date"   (valid-date? v)
    "number" (number? v)
    true))

(defn structural-error
  "First structural violation (required / type / enum) of a create record against `shape`, or
   nil. Defense-in-depth per WRITES-PLAN decision #7: the client runs the same checks locally
   via `validate-payload`, but the server is the authority and never trusts the client. Checked
   independently in Clojure — the oracle is an honest second implementation, not the bijection."
  [record]
  (some (fn [{:keys [key type required enum]}]
          (let [v (get record key)]
            (cond
              (and required (blank? v))
              {:code    "missing-required"
               :message (str "\"" key "\" is required.")
               :details {:field key}}

              (and (not (blank? v)) (not (type-ok? type v)))
              {:code    "invalid-type"
               :message (str "\"" key "\" is not a valid " type ".")
               :details {:field key}}

              (and enum (not (blank? v)) (not (contains? (set enum) v)))
              {:code    "invalid-value"
               :message (str "\"" key "\" must be one of: " (str/join ", " enum) ".")
               :details {:field key}})))
        (:fields shape)))

(defn- range-error
  "Server-only (semantic) validation the client cannot do locally: `end` must be on or after
   `start`. Only checked when both are present (end is optional). ISO date strings order
   lexicographically, so `compare` is the range check."
  [record]
  (let [start (get record "start")
        end   (get record "end")]
    (when (and (not (blank? start)) (not (blank? end)) (neg? (compare end start)))
      {:code    "invalid-range"
       :message "End date must be on or after the start date."
       :details {:field "end"}})))

(defn create-error
  "Validate a create record: structural (required/type/enum — defense-in-depth) first, then the
   cross-field range rule. Returns the first §6.5 error, or nil when acceptable."
  [record]
  (or (structural-error record) (range-error record)))

(defn- next-id
  "Server-assigned id for a new task: one past the current max (ids are 1-based)."
  [ts]
  (inc (reduce max 0 (map :id ts))))

(defn create-task
  "Append a task built from `record` (opaque string keys) with a server-minted id. Returns
   the updated set. Blank optional fields normalize to nil so the stored row stays read-contract
   valid (\"\" is not a valid :date; nil is). Duplicate-on-retry is an accepted edge for now
   (WRITES-PLAN decision #5)."
  [ts record]
  (let [field (fn [k] (let [v (get record k)] (when-not (blank? v) v)))]
    (conj ts {:id     (next-id ts)
              :owner  (field "owner")
              :start  (field "start")
              :end    (field "end")
              :status (field "status")})))

;; --- fixtures (step 5a): controlled failure modes for the client (5b) ------

(def ^:private slow-ms 3000)

(defn- raw-json-response
  "A response whose body is a literal string — so a fixture can send a body that is
   deliberately NOT a valid envelope (or not even valid JSON)."
  [status body-string]
  {:status  status
   :headers (merge {"content-type" "application/json"} cors-headers)
   :body    body-string})

(defn fixture-response
  "Produce a deliberately broken response for `?fixture=…` so the client can exercise
   each failure path. An unknown fixture falls through to a normal accepted response."
  [fixture params]
  (case fixture
    ;; protocol: the envelope itself is not a valid protocol value
    "bad-outcome"   (raw-json-response 200 (json/generate-string
                                            {:outcome "banana" :requestId (get params "requestId" "server-boot")}))
    "missing-shape" (raw-json-response 200 (json/generate-string (dissoc (accepted-envelope params) :shape)))
    "unparseable"   (raw-json-response 200 "{ this is not valid json")
    ;; contract: well-formed accepted envelope, but a row is missing a declared field
    "contract"      (raw-json-response 200 (json/generate-string
                                            (update-in (accepted-envelope params) [:value 0] dissoc :status)))
    ;; network: no valid HTTP response reaches the client
    "500"           {:status 500 :headers (merge {"content-type" "text/plain"} cors-headers) :body "boom"}
    "slow"          (do (Thread/sleep slow-ms) (json-response 200 (accepted-envelope params)))
    ;; unknown fixture: behave normally
    (json-response 200 (accepted-envelope params))))

;; --- SSR boot page + static modules (step 5a) ------------------------------

(defn boot-html
  "An HTML page with the first response embedded as a <script type=\"application/json\">
   child of <server-resource> (§7.4). Served same-origin so its module and API calls
   need no CORS. The embedded envelope is the same value /api/tasks would return."
  []
  (let [envelope (json/generate-string (accepted-envelope {}))]
    (str "<!DOCTYPE html>\n"
         "<html lang=\"en\"><head><meta charset=\"UTF-8\">"
         "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
         "<title>BareBuild — SSR boot</title></head>\n<body>\n"
         "  <h1>BareBuild — SSR boot demo</h1>\n"
         "  <server-resource resource-id=\"tasks\" src=\"/api/tasks\">\n"
         "    <script type=\"application/json\">" envelope "</script>\n"
         "    <x-table-consumer>\n"
         "      <x-table caption=\"Tasks\" bordered></x-table>\n"
         "    </x-table-consumer>\n"
         "  </server-resource>\n"
         "  <script type=\"module\">import { init } from \"/dist/demo.js\"; init();</script>\n"
         "</body></html>\n")))

(defn- content-type-for [path]
  (cond
    (str/ends-with? path ".js")  "text/javascript"
    (str/ends-with? path ".map") "application/json"
    :else                        "application/octet-stream"))

(defn- serve-dist
  "Serve a compiled module under dist/ so the boot page loads same-origin. Restricted to
   the dist/ prefix, no path traversal."
  [uri]
  (let [rel (subs uri 1)]                       ; strip leading /
    (if (and (str/starts-with? rel "dist/")
             (not (str/includes? rel ".."))
             (.exists (io/file rel)))
      {:status  200
       :headers (merge {"content-type" (content-type-for rel)} cors-headers)
       :body    (slurp (io/file rel))}
      (json-response 404 {:error "not-found" :uri uri}))))

(defn handler [req]
  (let [uri (:uri req)]
    (cond
      (= :options (:request-method req))
      {:status 204 :headers cors-headers}

      (= "/health" uri)
      {:status 200 :headers (merge {"content-type" "text/plain"} cors-headers) :body "ok"}

      (= "/demo/boot" uri)
      {:status 200 :headers (merge {"content-type" "text/html"} cors-headers) :body (boot-html)}

      (str/starts-with? uri "/dist/")
      (serve-dist uri)

      ;; POST /api/tasks — create a task from the JSON record body (step W3a). Server-only
      ;; semantic validation (end >= start) yields a rejected ack + field details; otherwise
      ;; the record is appended with a server-minted id and an accepted ack returned. Both are
      ;; HTTP 200; the client refetches to see the new row.
      (and (= :post (:request-method req)) (= "/api/tasks" uri))
      (let [params (parse-query (:query-string req))
            record (request-record req)]
        (if-let [error (create-error record)]
          (json-response 200 (write-rejected-ack params error))
          (do (swap! tasks create-task record)
              (json-response 200 (accepted-ack params)))))

      (= "/api/tasks" uri)
      (let [params  (parse-query (:query-string req))
            fixture (get params "fixture")]
        (cond
          ;; ?fixture=… produces a specific failure mode (5a).
          (some? fixture)            (fixture-response fixture params)
          ;; Otherwise: accepted / rejected — both HTTP 200, the outcome carries the
          ;; verdict (§3.1); a 4xx would read as a network failure to the client.
          (unsupported-sort? params) (json-response 200 (rejected-envelope params))
          :else                      (json-response 200 (accepted-envelope params))))

      ;; DELETE /api/tasks/:id — mutate the in-memory set, return the ack (step W1a).
      ;; The ack is HTTP 200 with an `accepted` outcome (mirroring reads); the client
      ;; refetches to observe the new state.
      (and (= :delete (:request-method req))
           (str/starts-with? uri "/api/tasks/"))
      (let [params (parse-query (:query-string req))]
        (swap! tasks delete-task (task-id-from-uri uri))
        (json-response 200 (accepted-ack params)))

      :else
      (json-response 404 {:error "not-found" :uri uri}))))

(defn -main [& _]
  (http/run-server handler {:port port})
  (println (str "BareBuild dev-server listening on http://localhost:" port))
  @(promise))
