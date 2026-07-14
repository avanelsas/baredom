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

;; 40 rows so pagination is worth demoing.
(def tasks (mapv gen-task (range 1 41)))

;; Shape: structural consumption invariants only (§3.5). id is the identity key,
;; declared separately from the consumable display fields.
(def shape
  {:idKey  "id"
   :fields [{:key "owner"  :type "string"}
            {:key "start"  :type "date"}
            {:key "end"    :type "date"}
            {:key "status" :type "string"}]})

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
   "access-control-allow-methods" "GET,OPTIONS"
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
        filtered (filter-tasks tasks term)
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

      :else
      (json-response 404 {:error "not-found" :uri uri}))))

(defn -main [& _]
  (http/run-server handler {:port port})
  (println (str "BareBuild dev-server listening on http://localhost:" port))
  @(promise))
