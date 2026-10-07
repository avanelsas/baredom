(ns baredom.components.x-pagination.model)

;; ── Tag & attribute constants ──────────────────────────────────────────────
(def tag-name          "x-pagination")
(def attr-page         "page")
(def attr-total-pages  "total-pages")
(def attr-sibling-count  "sibling-count")
(def attr-boundary-count "boundary-count")
(def attr-size         "size")
(def attr-disabled     "disabled")
(def attr-label        "label")

;; ── Event & slot constants ─────────────────────────────────────────────────
(def event-change-request "page-change-request")
(def event-page-change    "page-change")
(def slot-prev         "prev")
(def slot-next         "next")

;; ── Observed attributes ───────────────────────────────────────────────────
(def observed-attributes
  #js [attr-page attr-total-pages attr-sibling-count
       attr-boundary-count attr-size attr-disabled attr-label])

;; ── Defaults ──────────────────────────────────────────────────────────────
(def default-page           1)
(def default-total-pages    1)
(def default-sibling-count  1)
(def default-boundary-count 1)
(def default-size           "md")
(def default-label          "Pagination")

(def allowed-sizes #{"sm" "md" "lg"})

;; ── Parse helpers ─────────────────────────────────────────────────────────
(defn parse-pos-int
  "Parse string to positive integer ≥ 1, returning fallback on failure."
  [s fallback]
  (if (string? s)
    (let [n (js/parseInt s 10)]
      (if (and (js/isFinite n) (pos? n)) n fallback))
    fallback))

(defn parse-total-pages [s]
  (parse-pos-int s default-total-pages))

(defn parse-page
  "Parse current page, clamped to [1, total]."
  [s total]
  (let [n (parse-pos-int s default-page)]
    (max 1 (min n total))))

(defn parse-sibling-count [s]
  (if (string? s)
    (let [n (js/parseInt s 10)]
      (if (and (js/isFinite n) (>= n 0)) n default-sibling-count))
    default-sibling-count))

(defn parse-boundary-count [s]
  (if (string? s)
    (let [n (js/parseInt s 10)]
      (if (and (js/isFinite n) (>= n 0)) n default-boundary-count))
    default-boundary-count))

(defn parse-size [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-sizes v) v default-size)))

;; ── Normalize ─────────────────────────────────────────────────────────────
(defn normalize
  [{:keys [page-raw total-pages-raw sibling-count-raw boundary-count-raw
           size-raw disabled-present? label-raw]}]
  (let [total (parse-total-pages total-pages-raw)]
    {:page           (parse-page page-raw total)
     :total-pages    total
     :sibling-count  (parse-sibling-count sibling-count-raw)
     :boundary-count (parse-boundary-count boundary-count-raw)
     :size           (parse-size size-raw)
     :disabled       (boolean disabled-present?)
     :label          (or label-raw default-label)}))

;; ── Page range algorithm ──────────────────────────────────────────────────
(defn- page-key
  "The key of the item for page `n`."
  [n]
  (str "page-" n))

(defn- gap-key
  "The key of the gap that ends before page `n`: the side of the current page it is on."
  [current n]
  (if (<= n current) "gap-before" "gap-after"))

(defn build-page-items
  "Returns a sequence of {:type :page :n N} and {:type :ellipsis} maps, each with a distinct `:key`.
   current  — 1-indexed current page
   total    — total page count
   siblings — pages shown on each side of current
   boundary — pages always shown at start and end"
  [current total siblings boundary]
  (if (<= total 0)
    []
    (let [left-end    (min boundary total)
          right-start (max (- total boundary -1) 1)
          mid-start   (max 1 (- current siblings))
          mid-end     (min total (+ current siblings))

          ;; Build sorted deduplicated set of page numbers
          pages (-> (sorted-set)
                    (into (range 1 (inc left-end)))
                    (into (range right-start (inc total)))
                    (into (range mid-start (inc mid-end))))
          pages-vec (vec pages)]

      ;; Walk pages, inserting :ellipsis where gap > 1
      (loop [result []
             i      0]
        (if (>= i (count pages-vec))
          result
          (let [n    (nth pages-vec i)
                prev (when (pos? i) (nth pages-vec (dec i)))]
            (recur
             (cond-> result
               (and prev (> n (inc prev)))
               (conj {:type :ellipsis :key (gap-key current n)})
               true
               (conj {:type :page :n n :key (page-key n)}))
             (inc i))))))))

;; ── Derived predicates ────────────────────────────────────────────────────
(defn prev-disabled?
  [{:keys [page disabled]}]
  (or disabled (<= page 1)))

(defn next-disabled?
  [{:keys [page total-pages disabled]}]
  (or disabled (>= page total-pages)))

;; ── Metadata ──────────────────────────────────────────────────────────────
(def property-api
  {:page           {:type 'number  :reflects-attribute attr-page}
   :totalPages     {:type 'number  :reflects-attribute attr-total-pages}
   :siblingCount   {:type 'number  :reflects-attribute attr-sibling-count}
   :boundaryCount  {:type 'number  :reflects-attribute attr-boundary-count}
   :size           {:type 'string  :reflects-attribute attr-size}
   :disabled       {:type 'boolean :reflects-attribute attr-disabled}
   :label          {:type 'string  :reflects-attribute attr-label}})

(def event-schema
  {event-change-request {:cancelable true
                         :requests   {attr-page :page}
                         :detail     {:page 'number :previousPage 'number}}
   event-page-change    {:cancelable false :detail {:page 'number}}})

(def method-api {})
