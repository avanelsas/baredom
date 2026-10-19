(ns baredom.components.x-table.model
  (:require [baredom.components.x-table-row.model :as row-model]
            [baredom.utils.value-list :as vl]))

(def tag-name "x-table")

(def attr-columns    "columns")
(def attr-caption    "caption")
(def attr-selectable "selectable")
(def attr-striped    "striped")
(def attr-bordered   "bordered")
(def attr-full-width "full-width")
(def attr-compact    "compact")
(def attr-row-count  "row-count")
(def attr-value        "value")
(def attr-value-format "value-format")

(def observed-attributes
  #js [attr-columns attr-caption attr-selectable
       attr-striped attr-bordered attr-full-width
       attr-compact attr-row-count attr-value attr-value-format])

(def row-attr-stripe "data-stripe")

(def foreign-writes
  "The attributes x-table writes on its rows."
  [{:target row-model/tag-name :attributes #{row-model/attr-selected row-attr-stripe}}])

;; ── Event names ──────────────────────────────────────────────────────────────

(def event-sort       "x-table-sort")
(def event-row-select "x-table-row-select")

;; ── Enum sets ────────────────────────────────────────────────────────────────
(def selectable-none   "none")
(def selectable-single "single")
(def selectable-multi  "multi")
(def selectable-values #{selectable-none selectable-single selectable-multi})

;; ── Property API metadata ────────────────────────────────────────────────────
(def property-api
  {:columns    {:type 'string  :reflects-attribute attr-columns}
   :caption    {:type 'string  :reflects-attribute attr-caption}
   :selectable {:type 'string  :reflects-attribute attr-selectable :enum selectable-values}
   :striped    {:type 'boolean :reflects-attribute attr-striped}
   :bordered   {:type 'boolean :reflects-attribute attr-bordered}
   :fullWidth  {:type 'boolean :reflects-attribute attr-full-width}
   :compact    {:type 'boolean :reflects-attribute attr-compact}
   :rowCount   {:type 'number  :reflects-attribute attr-row-count}
   :value      {:type 'string  :reflects-attribute attr-value}
   :valueFormat {:type 'string :reflects-attribute attr-value-format :enum vl/allowed-value-formats}})

(def event-schema
  {event-sort
   {:detail     {:colIndex 'number :direction 'string :previousDirection 'string}
    :cancelable true}

   event-row-select
   {:detail     {:rowIndex 'number :selected 'boolean :selectionMode 'string :value 'string}
    :cancelable true
    :requests   {attr-value :value}}})

;; ── Parse helpers ────────────────────────────────────────────────────────────

(defn parse-columns
  "Parse `columns` attribute to a CSS grid-template-columns string.
  A positive integer string (e.g. \"4\") becomes \"repeat(4,minmax(0,1fr))\"
  — equal-width tracks that can shrink below their content's intrinsic
  minimum. Plain `1fr` keeps a `min-content` floor, so a wide/unbreakable
  cell makes its column bulge past its share and the columns come out
  uneven; `minmax(0,1fr)` is what authors actually mean by \"N equal
  columns\". For per-column sizing, pass an explicit template string instead.
  Any other non-empty string is returned as-is.
  Nil or blank → nil (no explicit template)."
  [s]
  (when (string? s)
    (let [t (.trim s)]
      (when (not= t "")
        (let [n (js/parseInt t 10)]
          (if (and (not (js/isNaN n))
                   (pos? n)
                   (= t (str n)))
            (str "repeat(" n ",minmax(0,1fr))")
            t))))))

(defn parse-selectable
  "Normalise selectable attribute. Unknown/nil → \"none\"."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? selectable-values v) v selectable-none)))

(defn holdable?
  "True when a list in `value-format` can hold the row value `row-value`. A comma-separated list
   cannot hold a value with a comma or outer whitespace."
  [value-format row-value]
  (or (= value-format vl/value-format-json)
      (empty? (vl/csv-unrepresentable [row-value]))))

(defn unholdable-message
  "The warning for a click on a row whose value a comma-separated `value` cannot hold."
  [row-value]
  (str "[x-table] The row value " (js/JSON.stringify row-value)
       " cannot be held in a comma-separated value. Set value-format=\"json\"."))

(defn invalid-json-message
  "The warning for a `value` that is not a JSON array of strings."
  [value-raw]
  (str "[x-table] The value " (js/JSON.stringify value-raw)
       " is not a JSON array of strings, so no row is selected."))

(defn parse-row-count
  "Parse row-count to a positive integer, or nil if absent/invalid."
  [s]
  (when (string? s)
    (let [n (js/parseInt (.trim s) 10)]
      (when (and (not (js/isNaN n)) (pos? n))
        (js/Math.floor n)))))

;; ── Normalisation ────────────────────────────────────────────────────────────

(defn normalize
  "Produce a canonical view-model map from raw attribute inputs.

  Input keys:
    :columns-raw     string | nil
    :caption-raw     string | nil
    :selectable-raw  string | nil
    :striped?        boolean
    :bordered?       boolean
    :full-width?     boolean
    :compact?        boolean
    :row-count-raw   string | nil
    :value-raw       string | nil
    :value-format-raw string | nil

  Output keys mirror the attribute semantics with parsed/normalised values."
  [{:keys [columns-raw caption-raw selectable-raw
           striped? bordered? full-width? compact? row-count-raw value-raw value-format-raw]}]
  (let [selectable   (parse-selectable selectable-raw)
        value-format (vl/normalize-value-format value-format-raw)
        parsed       (vl/parse-value-as value-format value-raw)]
    {:columns        (parse-columns columns-raw)
     :caption        (or caption-raw "")
     :selectable     selectable
     :striped?       (boolean striped?)
     :bordered?      (boolean bordered?)
     :full-width?    (boolean full-width?)
     :compact?       (boolean compact?)
     :row-count      (parse-row-count row-count-raw)
     :value-format   value-format
     :value-present? (some? value-raw)
     :value-warning  (when (:invalid? parsed) (invalid-json-message value-raw))
     :selection      (:value parsed)}))

;; ── Derived view-model functions ─────────────────────────────────────────────

(defn role-for-selectable
  "Return ARIA role string. \"none\" → \"table\"; any selection mode → \"grid\"."
  [selectable]
  (if (= selectable selectable-none) "table" "grid"))

(defn aria-multiselectable
  "Return \"true\" for multi-select, nil otherwise (attribute should be removed)."
  [selectable]
  (when (= selectable selectable-multi) "true"))

(defn sort-detail
  "Build event detail for x-table-sort."
  [col-index direction previous-direction]
  {:colIndex         col-index
   :direction        direction
   :previousDirection previous-direction})

(defn row-select-detail
  "Build event detail for x-table-row-select. `value` is the text of `value` that the click asks for."
  [row-index selected? selectable value]
  {:rowIndex      (or row-index 0)
   :selected      selected?
   :selectionMode selectable
   :value         value})

(defn next-selection
  "The row values selected after a click on the row with the value `row-value`. Single mode asks
   for that row alone, or for no row when it is selected."
  [selectable selection row-value]
  (cond
    (= selectable selectable-single) (if (contains? selection row-value) #{} #{row-value})
    (contains? selection row-value)  (disj selection row-value)
    :else                            (conj selection row-value)))

(defn seed-selection
  "The selection that the rows marked selected give, from their values in order. Single mode
   takes the first."
  [selectable marked-values]
  (set (if (= selectable selectable-single) (take 1 marked-values) marked-values)))

(defn selection-text
  "The text of `value` for the row values `selection`, as a list in `value-format`."
  [value-format selection]
  (vl/serialize-value-as value-format selection))

(defn row-stripe
  "Return \"even\" for every second row, and nil for the others. `index` counts from zero."
  [index]
  (when (odd? index) "even"))

(def method-api {})
