#!/usr/bin/env bb
;; BareBuild reference dev-server: the contract oracle and the demo backend.
;;
;; It emits JSON independently, cheshire over plain maps, never through BareBuild's own wire
;; bijection, so it is an honest second implementation of the contract in docs/server-contract.md.
;; Keyword map keys serialize to the exact wire spelling, so :idKey becomes "idKey", :requestId
;; becomes "requestId", and row keys stay their opaque strings.
;;
;; It serves three collections. /api/tasks is the paged, sortable, searchable one the table demo
;; reads and writes. /api/projects and /api/users are the small related collections the board demo
;; reads alongside it, and projects also accepts a create.
;;
;; /api/tasks also answers `?fixture=…` with each failure mode a client must handle: a broken
;; envelope (bad-outcome, missing-shape, unparseable), a payload that violates the declared shape
;; (contract), and a transport failure (500, slow). It serves an SSR boot page at /demo/boot with
;; the first response embedded in the HTML, plus the compiled modules under /dist so that page runs
;; same-origin. Start it with `bb run server`. It never auto-starts on require.
(ns server
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [org.httpkit.server :as http]
            [cheshire.core :as json]))

(def port 8090)

;; --- the fixed object with known behaviour ---------------------------------

(def tasks-revision "tasks:v1")

(def ^:private owners
  ["Alice" "Bob" "Carmen" "Dev" "Erin" "Frank" "Grace" "Heidi" "Ivan" "Judy"])

;; The status vocabulary, written down once. The shape's enum and options below derive from it, and
;; so does the check a move validates against, so the three can never disagree.
(def ^:private statuses ["todo" "doing" "done"])

(def ^:private status-set (set statuses))

(def ^:private status-labels
  {"todo" "To do" "doing" "In progress" "done" "Done"})

;; Title vocab, one per task so every task title is unique. Deliberately free of any owner name,
;; status word or date fragment as a substring, because `filter-tasks` searches titles too and a
;; collision here would silently change the result sets the search tests pin.
(def ^:private titles
  ["Audit build pipeline"    "Migrate legacy schema"   "Refactor parser core"
   "Write onboarding guide"  "Fix flaky test suite"    "Profile query latency"
   "Update API contract"     "Trim bundle size"        "Harden auth flow"
   "Document error codes"    "Batch import jobs"       "Retire unused flags"
   "Split monolith module"   "Cache lookup results"    "Tune retry backoff"
   "Chart usage metrics"     "Patch upload limits"     "Verify backup restore"
   "Seed staging fixtures"   "Polish empty states"     "Compress asset bundles"
   "Rotate access keys"      "Sanitize user input"     "Throttle write bursts"
   "Prune stale sessions"    "Validate schema types"   "Refresh cache layers"
   "Isolate flaky mocks"     "Compact log storage"     "Restructure module graph"
   "Capture slow traces"     "Warm lookup tables"      "Shard heavy queries"
   "Backfill missing rows"   "Simplify build steps"    "Escape shell args"
   "Merge feature flags"     "Trace memory leaks"      "Bump upstream libs"
   "Publish release notes"])

;; --- related collections: users and projects -------------------------------
;; Two server-owned collections the board demo projects alongside tasks, over one shared backend
;; with the flat table demo. A task references a user and a project by opaque string id, and the
;; server denormalizes their display names onto each task row on read, so a consumer renders a name
;; without joining across resources.

(def users-revision "users:v1")
(def projects-revision "projects:v1")

(def ^:private users
  [{:id "u-1" :name "Alice"  :email "alice@example.com"}
   {:id "u-2" :name "Bob"    :email "bob@example.com"}
   {:id "u-3" :name "Carmen" :email "carmen@example.com"}
   {:id "u-4" :name "Dev"    :email "dev@example.com"}
   {:id "u-5" :name "Erin"   :email "erin@example.com"}])

(def ^:private initial-projects
  [{:id "p-1" :name "Website Redesign" :description "Marketing site refresh"}
   {:id "p-2" :name "Mobile App"       :description "iOS and Android client"}
   {:id "p-3" :name "Data Platform"    :description "Warehouse and pipelines"}])

;; Projects are writable, since the board demo creates them, so they live in an atom like tasks.
;; Users stay read-only.
(def projects (atom initial-projects))

(defn reset-projects!
  "Restore the pristine three-project set, a test seam mirroring reset-tasks!."
  []
  (reset! projects initial-projects))

(def users-shape
  {:idKey  "id"
   :fields [{:key "name"  :type "string" :required true}
            {:key "email" :type "string"}]})

(def projects-shape
  {:idKey  "id"
   :fields [{:key "name"        :type "string" :required true}
            {:key "description" :type "string"}]})

(def ^:private user-ids (mapv :id users))
(def ^:private project-ids (mapv :id initial-projects))

(defn- name-index
  "The id to display-name index of a related collection."
  [rows]
  (into {} (map (juxt :id :name)) rows))

(def ^:private user-names (name-index users))

;; --- rank: the server-owned position within a column -----------------------
;; Rank is a card's position within its (projectId, status) column and it belongs to the server, so
;; the client never sends it in a record. Every mutation that adds a row, removes one, or moves one
;; between columns ends by re-densing the columns it touched, so ranks stay a clean 0..n-1.

(defn- task-by-id
  "The row whose id is `id`, or nil when the set holds none."
  [ts id]
  (some #(when (= (:id %) id) %) ts))

(defn- replace-row
  "`ts` with the row whose id is `id` replaced by `row`. An id matching no row leaves the set
  untouched."
  [ts id row]
  (mapv (fn [t] (if (= (:id t) id) row t)) ts))

(defn- column-of
  "The (projectId, status) column a task lives in. Rank is dense within it."
  [t]
  [(:projectId t) (:status t)])

(defn- column-members
  "The tasks in column `col`, in rank order."
  [ts col]
  (->> ts (filter #(= (column-of %) col)) (sort-by :rank) vec))

(defn- insert-at
  "`xs` with `x` inserted at index `i`, clamped to the ends."
  [xs x i]
  (let [i (max 0 (min (count xs) (int i)))]
    (vec (concat (subvec xs 0 i) [x] (subvec xs i)))))

(defn- dense-ranks
  "`ts` with every task in column `col` renumbered 0..n-1, in the order `ordered` gives."
  [ts col ordered]
  (let [rank-of (into {} (map-indexed (fn [idx t] [(:id t) idx]) ordered))]
    (mapv (fn [t] (if (= (column-of t) col) (assoc t :rank (rank-of (:id t))) t)) ts)))

(defn- place-and-dense
  "Renumber column `col` to a dense 0-based sequence. When `place-id` names a task in the column it
  is inserted at `place-rank` instead of at its stored rank, which is how a dropped card lands where
  it was released. With no `place-id` the stored order is kept, which is what closes the gap in a
  column a card has left."
  [ts col place-id place-rank]
  (let [members (column-members ts col)
        placed  (some #(when (= (:id %) place-id) %) members)
        ordered (if placed
                  (insert-at (filterv #(not= (:id %) place-id) members) placed place-rank)
                  members)]
    (dense-ranks ts col ordered)))

(defn reindex-columns
  "After task `id` has landed in a new place, re-dense its destination column, placing the task at
  its requested rank, and its old column, closing the gap it left behind."
  [ts id old-col]
  (let [moved    (task-by-id ts id)
        dest-col (column-of moved)]
    (cond-> (place-and-dense ts dest-col id (:rank moved))
      (not= old-col dest-col) (place-and-dense old-col nil nil))))

(defn- append-rank
  "The next rank at the bottom of column `col`: one past the current max, or 0 for an empty column.
  A card that arrives in a column it was not in lands below the others, and rank stays server-owned,
  minted here rather than sent by the client."
  [ts col]
  (let [ranks (->> ts (filter #(= (column-of %) col)) (keep :rank))]
    (if (seq ranks) (inc (reduce max ranks)) 0)))

(defn- gen-task
  "Deterministic demo task for 1-based id `i`. `projectId` runs on a different cycle from `status`
  so every project spans all three status columns rather than collapsing into one. `owner` and
  `status` keep their original cycles so the flat-demo counts hold."
  [i]
  (let [day (inc (mod (dec i) 28))]
    {:id         i
     :title      (nth titles (mod (dec i) (count titles)))
     :owner      (nth owners (mod (dec i) (count owners)))
     :start      (format "2026-01-%02d" day)
     :end        (format "2026-02-%02d" day)
     :est        (inc (mod (dec i) 8))
     :status     (nth statuses (mod (dec i) (count statuses)))
     :projectId  (nth project-ids (mod (quot (dec i) 3) (count project-ids)))
     :assigneeId (nth user-ids (mod (dec i) (count user-ids)))}))

(defn- seed-ranks
  "Assign a dense 0-based rank to each task within its column. The seed arrives in id order and
  re-densing keeps the order it finds, so re-densing every column is the whole job."
  [ts]
  (reduce (fn [acc col] (place-and-dense acc col nil nil)) ts (distinct (map column-of ts))))

;; 40 rows so pagination is worth demoing. The set is mutable server state, so it lives in an atom.
;; This is honest backend state rather than a BareBuild value: the stateless rule governs the client
;; runtime, not the oracle. `initial-tasks` is the pristine set the tests reset to.
(def ^:private initial-tasks (seed-ranks (mapv gen-task (range 1 41))))
(def tasks (atom initial-tasks))

(defn reset-tasks!
  "Restore the pristine 40-row set. A test seam so mutating cases do not leak into the count-based
  read assertions regardless of run order."
  []
  (reset! tasks initial-tasks))

;; The shape declares structural consumption invariants only. `id` is the identity key, declared
;; separately from the consumable display fields. `required` and `enum` are the write-side
;; invariants: a field with no `required` key is optional and one with no `enum` is unconstrained.
;; They round-trip through the read shape unchanged and drive the client's local write validation.
;; `end` is intentionally optional.
(def tasks-shape
  {:idKey  "id"
   :fields [{:key "title"  :type "string" :required true}
            {:key "owner"  :type "string" :required true}
            {:key "start"  :type "date"   :required true}
            {:key "end"    :type "date"}
            {:key "est"    :type "number"}
            {:key "status" :type "string" :required true :enum statuses
             :options (mapv (fn [s] {:value s :label (status-labels s)}) statuses)}]})

;; Fields the server can sort by, derived from the shape (id-key plus display fields). Each maps to
;; the row key it reads, so the whitelist and the accessor are one value and cannot drift.
(def ^:private sortable-fields
  (into {} (map (fn [k] [k (keyword k)]))
        (cons (:idKey tasks-shape) (map :key (:fields tasks-shape)))))

;; Declared fields the search does not scan. `est` holds a number, and a substring match against a
;; number answers a text search with arithmetic coincidences.
(def ^:private unsearchable-fields #{"est"})

;; Fields the search scans, derived from the shape like the sortable ones rather than restated, so a
;; field is searchable the day it is declared.
(def ^:private searchable-fields
  (into [] (comp (map :key) (remove unsearchable-fields) (map keyword)) (:fields tasks-shape)))

;; --- paging ----------------------------------------------------------------

(def ^:private page-size 10)

(defn- total-pages
  "Pages needed to hold `n` rows, at least 1. `n` is the count of the set actually being served,
  which is the full task set or the subset a search narrowed it to."
  [n]
  (max 1 (quot (+ n page-size -1) page-size)))

(defn- parse-page
  "1-based page number from `params`, clamped to [1, tp]. Absent or non-numeric yields 1. `tp` is
  the total-pages of the set being served, so a search that narrows the set to fewer pages clamps an
  out-of-range page down."
  [params tp]
  (-> (or (some-> (get params "page") parse-long) 1)
      (max 1)
      (min tp)))

(defn- paginate
  "The page-size slice of `ts` for 1-based `page`."
  [ts page]
  (->> ts (drop (* (dec page) page-size)) (take page-size) vec))

;; --- search ----------------------------------------------------------------

(defn- search-term
  "The trimmed `search` param, or nil when absent or blank. Case is preserved: matching is
  case-insensitive, but the term is echoed as given so it round-trips through the URL."
  [params]
  (let [s (some-> (get params "search") str/trim)]
    (when-not (str/blank? s) s)))

(defn filter-tasks
  "Rows whose display fields contain `term` as a case-insensitive substring. A nil term leaves the
  set untouched. Applied before sort and slice."
  [ts term]
  (if term
    (let [needle (str/lower-case term)]
      (filterv (fn [t]
                 (some (fn [k] (str/includes? (str/lower-case (str (get t k))) needle))
                       searchable-fields))
               ts))
    ts))

(defn- project-term
  "The trimmed `project` param, or nil when absent or blank. The opaque id of the project whose
  board is being viewed. A nil term leaves the task set unfiltered."
  [params]
  (let [p (some-> (get params "project") str/trim)]
    (when-not (str/blank? p) p)))

(defn filter-by-project
  "Rows whose :projectId is `proj`. A nil proj leaves the set untouched. Applied before search."
  [ts proj]
  (if proj (filterv #(= (:projectId %) proj) ts) ts))

;; --- query handling --------------------------------------------------------

(defn resolve-query
  "Everything the server honors from a raw query, read once, as one value: the project and search
  filters, and the sort field with its direction. A valid `sort` field is kept with a `direction`
  that defaults to \"asc\", so any value other than \"desc\" is coerced to \"asc\", and an absent or
  unknown sort yields no sort keys. The page is not here, since clamping it needs the count of the
  narrowed set, so the caller completes the value with :page once it has filtered."
  [params]
  (let [proj  (project-term params)
        term  (search-term params)
        field (get params "sort")]
    (cond-> {}
      proj
      (assoc :project proj)

      term
      (assoc :search term)

      (contains? sortable-fields field)
      (assoc :sort field
             :direction (if (= "desc" (get params "direction")) "desc" "asc")))))

(defn echo-query
  "The URL projection of a resolved query: the client's own string keys, and string values, because
  the URL's value domain is strings. The default page 1 is omitted, since the URL carries no page for
  it. The client adopts this echo as its canonical intent, so it is a projection of the same value
  the server filtered and sorted by rather than a second reading of the request."
  [{:keys [project search sort direction page] :or {page 1}}]
  (cond-> {}
    project    (assoc "project" project)
    search     (assoc "search" search)
    sort       (assoc "sort" sort "direction" direction)
    (> page 1) (assoc "page" (str page))))

(defn sort-tasks
  "Order the task set per a resolved query. Ties break on :id so the order is deterministic, and a
  descending sort reverses that tie-break along with the rest. An empty query leaves the natural id
  order untouched."
  [ts {:keys [sort direction]}]
  (if-let [k (sortable-fields sort)]
    (let [asc (vec (sort-by (juxt k :id) ts))]
      (if (= "desc" direction) (vec (rseq asc)) asc))
    ts))

;; --- rejection -------------------------------------------------------------

(defn unsupported-sort?
  "True when the request asks to sort by a field the server does not support. Rejecting is the
  honest answer instead of silently ignoring the field."
  [params]
  (let [sort (get params "sort")]
    (and (some? sort)
         (not (contains? sortable-fields sort)))))

(defn- echoed-query
  "The client's query as it arrived, without the transport-only request id. What a rejected envelope
  echoes: the server honored none of it, so it cannot answer with a normalized query, and echoing
  anything else would read to the client as a correction it must apply to the URL."
  [params]
  (dissoc params "requestId"))

(defn- request-id
  "The client's request id, echoed back on every envelope. A request carrying none is the server's
  own boot."
  [params]
  (get params "requestId" "server-boot"))

(defn rejected-envelope
  "The rejected envelope: the query echoed back exactly as it arrived, plus a structured error, and
  unlike an accepted one it carries no value or shape. Still an HTTP 200 protocol response rather
  than a transport error."
  [params]
  (let [bad-field (get params "sort")]
    {:outcome   "rejected"
     :requestId (request-id params)
     :revision  tasks-revision
     :query     (echoed-query params)
     :error     {:code    "invalid-query"
                 :message (str "Sorting by \"" bad-field "\" is not supported.")
                 :details {:field bad-field}}}))

;; --- request/response plumbing ---------------------------------------------

;; The demo pages are served from another port, so every request is cross-origin and any header
;; past the safelisted ones must be advertised here or the browser blocks the request.
(def ^:private allowed-request-headers
  ["content-type" "authorization" "x-demo-client"])

(def ^:private cors-headers
  {"access-control-allow-origin"  "*"
   "access-control-allow-methods" "GET,POST,PUT,PATCH,DELETE,OPTIONS"
   "access-control-allow-headers" (str/join "," allowed-request-headers)})

(defn- parse-query [qs]
  (if (str/blank? qs)
    {}
    (into {}
          (for [pair (str/split qs #"&")
                :let [[k v] (str/split pair #"=" 2)]]
            [(java.net.URLDecoder/decode k "UTF-8")
             (java.net.URLDecoder/decode (or v "") "UTF-8")]))))

(defn- response
  "One response value. Every reply the server sends is built here, so the CORS headers are attached
  in one place rather than at each branch."
  [status content-type body]
  {:status  status
   :headers (merge {"content-type" content-type} cors-headers)
   :body    body})

(defn- json-response [status body]
  (response status "application/json" (json/generate-string body)))

(defn- not-found
  "The 404, one value, since both the static path and the handler's fallthrough answer with it."
  [uri]
  (json-response 404 {:error "not-found" :uri uri}))

(defn- raw-json-response
  "A response whose body is a literal string, so a fixture can send a body that is deliberately not
  a valid envelope, or not even valid JSON."
  [status body-string]
  (response status "application/json" body-string))

(defn- denormalize
  "`rows` with the server-owned display names for each task's user and project references added, so a
  consumer renders a name without joining across resources. A nil reference yields a nil name, and
  the presentation layer decides any fallback. The project index is built once per response, so every
  row in one response reads one snapshot of a collection a concurrent create can grow."
  [rows]
  (let [project-names (name-index @projects)]
    (mapv (fn [t]
            (assoc t
                   :assigneeName (user-names (:assigneeId t))
                   :projectName  (project-names (:projectId t))))
          rows)))

(defn accepted-envelope
  "Build the complete accepted envelope for the task set `ts`. Rows are filtered by project for board
  reads and by the search term, then sorted, then sliced to the requested page for the flat view. A
  `?project=` read is the whole column set unpaginated, because a board places every card and paging
  it would hide some. The query echo is the resolved query projected back onto the URL, and
  `pageInfo` reflects the filtered set. Each returned row carries its denormalized user and project
  names."
  [ts params]
  (let [{:keys [project search] :as resolved} (resolve-query params)
        filtered (-> (filter-by-project ts project)
                     (filter-tasks search))
        paged?   (nil? project)
        tp       (if paged? (total-pages (count filtered)) 1)
        q        (assoc resolved :page (parse-page params tp))
        sorted   (sort-tasks filtered q)
        rows     (if paged? (paginate sorted (:page q)) sorted)]
    {:outcome   "accepted"
     :requestId (request-id params)
     :revision  tasks-revision
     :query     (echo-query q)
     :value     (denormalize rows)
     :shape     tasks-shape
     :pageInfo  {:page       (:page q)
                 :pageSize   (if paged? page-size (count rows))
                 :totalPages tp
                 :totalCount (count filtered)}}))

(defn collection-envelope
  "The read envelope for a small related collection such as users or projects: the full set, with no
  paging, search or sort. The same accepted shape as tasks, and the same argument order, the rows it
  answers with first, so one consumer mechanism drives all."
  [rows params rev shape]
  {:outcome   "accepted"
   :requestId (request-id params)
   :revision  rev
   :query     {}
   :value     rows
   :shape     shape
   :pageInfo  {:page 1 :pageSize (count rows) :totalPages 1 :totalCount (count rows)}})

(defn- users-envelope [rows params]
  (collection-envelope rows params users-revision users-shape))

(defn- projects-envelope [rows params]
  (collection-envelope rows params projects-revision projects-shape))

;; --- the read vocabulary, as data ------------------------------------------
;; A related collection answers a GET with its whole set and nothing else, so it is a row: the uri it
;; answers and the envelope that answers it. /api/tasks is not here, since its read carries the
;; paging, sorting, rejection and failure fixtures the small collections have no notion of.

(def ^:private read-ops
  {"/api/users"    (fn [params] (users-envelope users params))
   "/api/projects" (fn [params] (projects-envelope @projects params))})

;; --- the protected read ----------------------------------------------------
;; One route behind a bearer token, answering the same envelope /api/tasks does. The rule is
;; trivial and written down once: a token the demo minted opens it, the expired one does not.

(def ^:private token-prefix "demo-")

(def ^:private expired-token "demo-expired")

(defn bearer-token
  "The token an Authorization header carries, or nil when absent or not a bearer."
  [req]
  (when-let [header (get-in req [:headers "authorization"])]
    (second (re-matches #"(?i)\s*bearer\s+(\S+)\s*" header))))

(defn token-accepted?
  "Whether `token` opens the protected read."
  [token]
  (boolean (and token
                (str/starts-with? token token-prefix)
                (not= token expired-token))))

(defn- unauthorized
  "The 401 the protected read answers without an accepted token. Not an envelope: an HTTP failure
  reaches the client as a network failure, never as envelope parsing."
  []
  (json-response 401 {:error "unauthorized" :message "Bearer token missing or expired."}))

;; --- writes: acks ----------------------------------------------------------

;; An accepted write returns the full post-mutation envelope, shaped by the write's query, so the
;; client installs the new state directly with no follow-up read. A rejected write returns
;; `write-rejected-ack`, which carries no value or shape, just the structured error.

(defn write-rejected-ack
  "The rejected write ack: the envelope head plus a structured error of code, message and details.
  Still HTTP 200, because the outcome carries the verdict. The `error` it is handed names the
  offending field in its `details`, so the client can map the rejection back onto the form."
  [params error rev]
  {:outcome   "rejected"
   :requestId (request-id params)
   :revision  rev
   :error     error})

;; --- writes: delete --------------------------------------------------------

(defn delete-task
  "Drop the row whose id is `id` from `ts` and close the gap it leaves, so the column it left keeps
  its dense ranks. Idempotent, since an absent id leaves the set unchanged and the outcome it
  promises already holds."
  [ts id]
  (if-let [gone (task-by-id ts id)]
    (place-and-dense (filterv #(not= (:id %) id) ts) (column-of gone) nil nil)
    ts))

;; --- writes: create and update ---------------------------------------------

(defn- request-record
  "Parse the JSON request body into a record map with opaque string keys. A nil body yields nil."
  [req]
  (when-let [b (:body req)]
    (json/parse-string (slurp b))))

(defn- unfilled?
  "nil or a blank string, the server's notion of an unfilled field."
  [v]
  (or (nil? v) (and (string? v) (str/blank? v))))

(defn- valid-date?
  "A wire date is an ISO-8601 local date the server can parse."
  [v]
  (and (string? v)
       (try (java.time.LocalDate/parse v) true (catch Exception _ false))))

(defn- type-ok?
  "Value matches the shape field's declared type. An unknown type does not block."
  [type v]
  (case type
    "string" (string? v)
    "date"   (valid-date? v)
    "number" (number? v)
    true))

(defn structural-error
  "First structural violation of a write record against `shape`, checking required, type and enum,
  or nil. The client runs the same checks locally, but the server is the authority and never trusts
  the client. Checked independently in Clojure, since the oracle is an honest second implementation.
  Shape is a parameter so the same validator serves tasks and projects."
  [shape record]
  (some (fn [{:keys [key type required enum]}]
          (let [v (get record key)]
            (cond
              (and required (unfilled? v))
              {:code    "missing-required"
               :message (str "\"" key "\" is required.")
               :details {:field key}}

              (and (not (unfilled? v)) (not (type-ok? type v)))
              {:code    "invalid-type"
               :message (str "\"" key "\" is not a valid " type ".")
               :details {:field key}}

              (and enum (not (unfilled? v)) (not (some #{v} enum)))
              {:code    "invalid-value"
               :message (str "\"" key "\" must be one of: " (str/join ", " enum) ".")
               :details {:field key}})))
        (:fields shape)))

(defn- range-error
  "Server-only validation the client cannot do locally: `end` must be on or after `start`. Only
  checked when both are present, since end is optional. ISO date strings order lexicographically, so
  `compare` is the range check."
  [record]
  (let [start (get record "start")
        end   (get record "end")]
    (when (and (not (unfilled? start)) (not (unfilled? end)) (neg? (compare end start)))
      {:code    "invalid-range"
       :message "End date must be on or after the start date."
       :details {:field "end"}})))

(defn task-error
  "Validate a write record: structural first, then the cross-field range rule. Returns the first
  error, or nil when the record is acceptable. Create and update run the same checks, because an
  update is a full replace, so its payload is a complete record the shape validates exactly as it
  validates a create."
  [record]
  (or (structural-error tasks-shape record) (range-error record)))

(defn update-error
  "First error for an update of `id` in `ts` with `record`. A missing target comes first, because an
  update, unlike a delete, is not idempotent over an absent row: there is nothing to replace, so a
  rejection is the honest answer. That error names no field and carries the id in `details`, because
  the record is fine and the target is not, and a consumer with nothing to highlight surfaces it as a
  message instead. Otherwise the ordinary record validation applies."
  [ts id raw-id record]
  (if (task-by-id ts id)
    (task-error record)
    {:code    "not-found"
     :message (str "No task with id \"" raw-id "\".")
     :details {:id raw-id}}))

(defn- row-of
  "The client-owned fields of a write `record`, read by the opaque string keys `ks` and stored under
  their keywords. Blank optional fields normalize to nil so the row stays read-contract valid, since
  \"\" is not a valid date and nil is. The server-owned fields, id and rank, are never here."
  [record ks]
  (into {} (map (fn [k] [(keyword k) (let [v (get record k)] (when-not (unfilled? v) v))])) ks))

(def ^:private task-ref-keys
  "Reference fields a task write accepts that the read shape does not declare: the board's project
  and assignee. They are row data rather than display columns, so the flat table builds no column
  from them, and no field of the shape constrains what a write may put there."
  ["projectId" "assigneeId"])

(def ^:private task-row-keys (into (mapv :key (:fields tasks-shape)) task-ref-keys))

(def ^:private project-row-keys (mapv :key (:fields projects-shape)))

(defn- next-id
  "Server-assigned id for a new task: one past the current max, since ids are 1-based."
  [ts]
  (inc (reduce max 0 (map :id ts))))

(defn create-task
  "Append a task built from `record` with a server-minted id and a server-minted rank at the bottom
  of its column. Returns the updated set."
  [ts record]
  (let [row (row-of record task-row-keys)]
    (conj ts (merge {:id   (next-id ts)
                     :rank (append-rank ts (column-of row))}
                    row))))

(defn update-task
  "Replace the row whose id is `id` with `record`. PUT semantics, a full replace of the client-owned
  fields, so a key the client omitted becomes nil. The server owns id and rank: a row that stays in
  its column keeps its rank, and one whose new status or project moves it to another column lands at
  the bottom of that column, since a rank minted in one column places nothing in another. Both
  columns are re-densed. An id matching no row leaves the set untouched, and the handler rejects that
  case before calling this."
  [ts id record]
  (if-let [old (task-by-id ts id)]
    (let [row     (merge {:id id} (row-of record task-row-keys))
          old-col (column-of old)
          rank    (if (= old-col (column-of row)) (:rank old) (append-rank ts (column-of row)))]
      (reindex-columns (replace-row ts id (assoc row :rank rank)) id old-col))
    ts))

;; --- writes: the move op ---------------------------------------------------
;; A move carries only the destination, a status and an index. The server places the card there and
;; re-denses the affected columns.

(defn move-error
  "First error for a move of `id`: a missing target, since a move, like an update, is not idempotent
  over an absent row, or a destination status outside the vocabulary. The index is clamped by
  `place-and-dense`, so it needs no bound check."
  [ts id raw-id record]
  (cond
    (not (task-by-id ts id))
    {:code "not-found" :message (str "No task with id \"" raw-id "\".") :details {:id raw-id}}

    (not (contains? status-set (get record "status")))
    {:code    "invalid-value"
     :message (str "\"" (get record "status") "\" is not a status.")
     :details {:field "status"}}))

(defn- move-index
  "The 0-based destination index a move carries, or the bottom of the column when it carries none.
  `insert-at` clamps anything past the end, so the size of the whole set stands in for the bottom."
  [ts record]
  (or (get record "index") (count ts)))

(defn move-task
  "Reposition task `id` to the destination status and index the move record carries. Only status and
  rank change and every other field is kept. The destination and vacated columns are re-densed."
  [ts id record]
  (let [old (task-by-id ts id)
        row (assoc old :status (get record "status") :rank (move-index ts record))]
    (reindex-columns (replace-row ts id row) id (column-of old))))

;; --- writes: create project ------------------------------------------------
;; The board demo creates projects. The same write contract as tasks, validated against
;; projects-shape, and an accepted create returns the full post-mutation projects collection.

(defn project-error
  "First structural violation of a project write record against projects-shape, or nil. Projects
  carry no cross-field rule, so structural validation is the whole check."
  [record]
  (structural-error projects-shape record))

(def ^:private project-id-prefix "p-")

(defn- project-id-suffix
  "The number in a project id, or 0 for an id that does not carry one."
  [id]
  (or (parse-long (subs id (count project-id-prefix))) 0))

(defn- next-project-id
  "Server-assigned id for a new project: the prefix plus one past the current max suffix."
  [ps]
  (str project-id-prefix (inc (reduce max 0 (map (comp project-id-suffix :id) ps)))))

(defn create-project
  "Append a project built from `record` with a server-minted id. Returns the updated set."
  [ps record]
  (conj ps (merge {:id (next-project-id ps)} (row-of record project-row-keys))))

;; --- fixtures: controlled failure modes for the client ---------------------

(def ^:private slow-ms 3000)

(defn fixture-response
  "Produce a deliberately broken response for `?fixture=…` so the client can exercise each failure
  path. An unknown fixture falls through to a normal accepted response."
  [fixture ts params]
  (case fixture
    ;; protocol: the envelope itself is not a valid protocol value
    "bad-outcome"   (raw-json-response 200 (json/generate-string
                                            {:outcome "banana" :requestId (request-id params)}))
    "missing-shape" (raw-json-response 200 (json/generate-string
                                            (dissoc (accepted-envelope ts params) :shape)))
    "unparseable"   (raw-json-response 200 "{ this is not valid json")
    ;; contract: a well-formed accepted envelope, but a row is missing a declared field
    "contract"      (let [envelope (accepted-envelope ts params)]
                      (raw-json-response 200 (json/generate-string
                                              (cond-> envelope
                                                (seq (:value envelope))
                                                (update-in [:value 0] dissoc :status)))))
    ;; network: no valid HTTP response reaches the client
    "500"           (response 500 "text/plain" "boom")
    "slow"          (do (Thread/sleep slow-ms) (json-response 200 (accepted-envelope ts params)))
    ;; unknown fixture: behave normally
    (json-response 200 (accepted-envelope ts params))))

;; --- SSR boot page and static modules --------------------------------------

(defn boot-html
  "An HTML page with the first response embedded as a <script type=\"application/json\"> child of
  <server-resource>. Served same-origin so its module and API calls need no CORS. The embedded
  envelope is the same value /api/tasks would return."
  [ts]
  (let [envelope (json/generate-string (accepted-envelope ts {}))]
    (str "<!DOCTYPE html>\n"
         "<html lang=\"en\"><head><meta charset=\"UTF-8\">"
         "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
         "<title>BareBuild SSR boot</title></head>\n<body>\n"
         "  <h1>BareBuild SSR boot demo</h1>\n"
         "  <server-resource src=\"/api/tasks\">\n"
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
  "Serve a compiled module under dist/ so the boot page loads same-origin. Restricted to the dist/
  prefix, with no path traversal."
  [uri]
  (let [rel (subs uri 1)]
    (if (and (str/starts-with? rel "dist/")
             (not (str/includes? rel ".."))
             (.exists (io/file rel)))
      (response 200 (content-type-for rel) (slurp (io/file rel)))
      (not-found uri))))

;; --- the write vocabulary, as data -----------------------------------------
;; The client's op table in resource.cljs says a write is a method, a target and a body. This is the
;; same claim from the server's side: a row per op naming the path and method it answers, whether it
;; addresses a member, the collection it mutates, the check that can reject it and the mutation an
;; accepted one performs. The URL is the table's business, not the ops': `write-op` reads the member
;; id out of the path the row already declares and hands it over as a target, so an op never parses a
;; URL. Adding an op is a row here, never another branch in `handler`.

(def ^:private collections
  "The two writable collections: where the state lives, the revision its acks carry, how the id in a
  path reads as a row id, and the envelope that answers a write to it. A task write answers with the
  paged task envelope, a project write with its own collection envelope. Task ids are numbers, so an
  unparsable one reads as nil and matches no row."
  {:tasks    {:state tasks :revision tasks-revision :id parse-long :ack accepted-envelope}
   :projects {:state projects :revision projects-revision :id identity :ack projects-envelope}})

(def ^:private write-ops
  [{:path "/api/tasks" :method :post :member? false :collection :tasks
    :error  (fn [_ts {:keys [record]}] (task-error record))
    :mutate (fn [ts {:keys [record]}] (create-task ts record))}

   {:path "/api/tasks" :method :put :member? true :collection :tasks
    :error  (fn [ts {:keys [id raw-id record]}] (update-error ts id raw-id record))
    :mutate (fn [ts {:keys [id record]}] (update-task ts id record))}

   {:path "/api/tasks" :method :patch :member? true :collection :tasks
    :error  (fn [ts {:keys [id raw-id record]}] (move-error ts id raw-id record))
    :mutate (fn [ts {:keys [id record]}] (move-task ts id record))}

   ;; Delete is idempotent over an absent row, so it has nothing to reject.
   {:path "/api/tasks" :method :delete :member? true :collection :tasks
    :error  (fn [_ts _target] nil)
    :mutate (fn [ts {:keys [id]}] (delete-task ts id))}

   {:path "/api/projects" :method :post :member? false :collection :projects
    :error  (fn [_ps {:keys [record]}] (project-error record))
    :mutate (fn [ps {:keys [record]}] (create-project ps record))}])

(defn- write-op
  "The row answering this request, carrying the raw id its path addresses, or nil when this is not a
  write the server serves. The id segment is read from the row's own `:path` and URL-decoded, since
  an opaque id may contain any character."
  [method uri]
  (some (fn [{:keys [path] :as op}]
          (let [addressed? (str/starts-with? uri (str path "/"))]
            (when (and (= method (:method op))
                       (= addressed? (:member? op))
                       (or addressed? (= uri path)))
              (cond-> op
                addressed?
                (assoc :raw-id (java.net.URLDecoder/decode (subs uri (inc (count path))) "UTF-8"))))))
        write-ops))

(def ^:private write-lock (Object.))

(defn- perform-write!
  "Reject the write or apply it. Both verdicts are HTTP 200, because the outcome carries the verdict
  where a 4xx would read as a network failure to the client. The check and the mutation see one
  target value, the id the path addressed and the record the body carried. An accepted write answers
  with the full post-mutation envelope, shaped by the write's query, so the client installs it with
  no follow-up read. That query only shapes the answer and is never the thing being written, so an
  unsupported sort in it is normalized away here rather than rejected as a read would reject it: a
  write is not a URL the client is asking the server to honor. The check, the mutation and the
  answering envelope all see one snapshot of the collection, which is what the lock is for."
  [{:keys [error mutate collection raw-id]} params record]
  (let [{:keys [state revision ack] parse-id :id} (collections collection)
        target                                    {:id     (when raw-id (parse-id raw-id))
                                                   :raw-id raw-id
                                                   :record record}]
    (locking write-lock
      (let [rows @state]
        (if-let [e (error rows target)]
          (json-response 200 (write-rejected-ack params e revision))
          (json-response 200 (ack (reset! state (mutate rows target)) params)))))))

(defn handler [req]
  (let [uri        (:uri req)
        method     (:request-method req)
        params     (parse-query (:query-string req))
        write      (write-op method uri)
        collection (when (= :get method) (read-ops uri))
        fixture    (get params "fixture")]
    (cond
      (= :options method)
      {:status 204 :headers cors-headers}

      (= "/health" uri)
      (response 200 "text/plain" "ok")

      (= "/demo/boot" uri)
      (response 200 "text/html" (boot-html @tasks))

      (str/starts-with? uri "/dist/")
      (serve-dist uri)

      (some? write)
      (perform-write! write params (request-record req))

      (some? collection)
      (json-response 200 (collection params))

      (and (= :get method) (= "/api/secure/tasks" uri))
      (if (token-accepted? (bearer-token req))
        (json-response 200 (accepted-envelope @tasks params))
        (unauthorized))

      (and (= :get method) (= "/api/tasks" uri))
      (cond
        (some? fixture)            (fixture-response fixture @tasks params)
        (unsupported-sort? params) (json-response 200 (rejected-envelope params))
        :else                      (json-response 200 (accepted-envelope @tasks params)))

      :else
      (not-found uri))))

(defn -main [& _]
  (http/run-server handler {:port port})
  (println (str "BareBuild dev-server listening on http://localhost:" port))
  @(promise))
