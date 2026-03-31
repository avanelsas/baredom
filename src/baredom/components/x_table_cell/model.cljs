(ns baredom.components.x-table-cell.model)

(def tag-name "x-table-cell")

(def attr-type           "type")
(def attr-scope          "scope")
(def attr-align          "align")
(def attr-valign         "valign")
(def attr-col-span       "col-span")
(def attr-row-span       "row-span")
(def attr-truncate       "truncate")
(def attr-sticky         "sticky")
(def attr-sortable       "sortable")
(def attr-sort-direction "sort-direction")
(def attr-disabled       "disabled")

(def observed-attributes
  #js [attr-type attr-scope attr-align attr-valign
       attr-col-span attr-row-span attr-truncate
       attr-sticky attr-sortable attr-sort-direction attr-disabled])

;; Primary event
(def event-sort "x-table-cell-sort")

;; Future parent-coordination events
(def event-connected    "x-table-cell-connected")
(def event-disconnected "x-table-cell-disconnected")

;; Slot names
(def slot-sort-icon "sort-icon")

(def property-api
  {:type          {:type 'string}
   :scope         {:type 'string}
   :align         {:type 'string}
   :valign        {:type 'string}
   :colSpan       {:type 'number}
   :rowSpan       {:type 'number}
   :truncate      {:type 'boolean}
   :sticky        {:type 'string}
   :sortable      {:type 'boolean}
   :sortDirection {:type 'string}
   :disabled      {:type 'boolean}})

(def event-schema
  {event-sort       {:detail     {:direction 'string :previousDirection 'string}
                     :cancelable true}
   event-connected  {:detail {:type 'string :scope 'string
                               :colSpan 'number :rowSpan 'number :align 'string}}
   event-disconnected {:detail {}}})

;; ── Private enum sets ────────────────────────────────────────────────────────

(def ^:private type-values      #{"data" "header"})
(def ^:private scope-values     #{"col" "row" "colgroup" "rowgroup"})
(def ^:private align-values     #{"start" "center" "end"})
(def ^:private valign-values    #{"top" "middle" "bottom"})
(def ^:private sticky-values    #{"none" "start" "end"})
(def ^:private sort-dir-values  #{"none" "asc" "desc"})

;; ── Normalisation helpers ────────────────────────────────────────────────────

(defn- normalize-enum
  "Return s (lower-cased) if it is in allowed, otherwise fallback."
  [s allowed fallback]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed v) v fallback)))

(defn parse-type
  "Normalise type attribute. Unknown/nil → \"data\"."
  [s]
  (normalize-enum s type-values "data"))

(defn parse-scope
  "Normalise scope attribute. Unknown/nil → \"col\"."
  [s]
  (normalize-enum s scope-values "col"))

(defn parse-align
  "Normalise align attribute. Unknown/nil → \"start\"."
  [s]
  (normalize-enum s align-values "start"))

(defn parse-valign
  "Normalise valign attribute. Unknown/nil → \"middle\"."
  [s]
  (normalize-enum s valign-values "middle"))

(defn parse-span
  "Parse a col-span or row-span attribute to a positive integer. Invalid/nil → 1."
  [s]
  (if (string? s)
    (let [n (js/parseInt (.trim s) 10)]
      (if (and (not (js/isNaN n)) (pos? n))
        (js/Math.floor n)
        1))
    1))

(defn parse-sticky
  "Normalise sticky attribute. Unknown/nil → \"none\"."
  [s]
  (normalize-enum s sticky-values "none"))

(defn parse-sort-direction
  "Normalise sort-direction attribute. Unknown/nil → \"none\"."
  [s]
  (normalize-enum s sort-dir-values "none"))

;; ── Derived view-model functions ─────────────────────────────────────────────

(defn role-for-cell
  "Return the ARIA role string for the host element based on type and scope."
  [{:keys [type scope]}]
  (if (= type "header")
    (if (or (= scope "row") (= scope "rowgroup"))
      "rowheader"
      "columnheader")
    "cell"))

(defn aria-sort-value
  "Return aria-sort string or nil if not applicable.
  Set only when sortable is true or sort-direction is not \"none\"."
  [{:keys [sortable? sort-direction]}]
  (when (or sortable? (not= sort-direction "none"))
    (case sort-direction
      "asc"  "ascending"
      "desc" "descending"
      "none")))

(defn sort-btn-visible?
  "Sort button is visible only for header cells with sortable=true."
  [{:keys [type sortable?]}]
  (and (= type "header") sortable?))

(defn sort-btn-tabindex
  "Returns \"0\" when the sort button should be keyboard-focusable, else \"-1\"."
  [{:keys [type sortable? disabled?]}]
  (if (and (= type "header") sortable? (not disabled?))
    "0"
    "-1"))

(defn next-sort-direction
  "Cycle sort direction: none → asc → desc → none."
  [current]
  (case current
    "none" "asc"
    "asc"  "desc"
    "desc" "none"
    "asc"))

(defn sort-btn-aria-label
  "Return the aria-label for the sort button describing the action that will occur."
  [current-direction]
  (case current-direction
    "none" "Sort ascending"
    "asc"  "Sort descending"
    "desc" "Remove sort"
    "Sort ascending"))

(defn normalize
  "Produce a canonical view-model map from raw attribute inputs.

  Input keys:
    :type-raw           string | nil
    :scope-raw          string | nil
    :align-raw          string | nil
    :valign-raw         string | nil
    :col-span-raw       string | nil
    :row-span-raw       string | nil
    :truncate?          boolean
    :sticky-raw         string | nil
    :sortable?          boolean
    :sort-direction-raw string | nil
    :disabled?          boolean

  Output keys mirror the attribute semantics with parsed/normalised values."
  [{:keys [type-raw scope-raw align-raw valign-raw
           col-span-raw row-span-raw truncate?
           sticky-raw sortable? sort-direction-raw disabled?]}]
  {:type           (parse-type type-raw)
   :scope          (parse-scope scope-raw)
   :align          (parse-align align-raw)
   :valign         (parse-valign valign-raw)
   :col-span       (parse-span col-span-raw)
   :row-span       (parse-span row-span-raw)
   :truncate?      (boolean truncate?)
   :sticky         (parse-sticky sticky-raw)
   :sortable?      (boolean sortable?)
   :sort-direction (parse-sort-direction sort-direction-raw)
   :disabled?      (boolean disabled?)})

(defn connected-detail
  "Build the event detail map for x-table-cell-connected."
  [{:keys [type scope col-span row-span align]}]
  {:type    type
   :scope   scope
   :colSpan col-span
   :rowSpan row-span
   :align   align})
