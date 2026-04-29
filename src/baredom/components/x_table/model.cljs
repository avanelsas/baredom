(ns baredom.components.x-table.model)

(def tag-name "x-table")

(def attr-columns    "columns")
(def attr-caption    "caption")
(def attr-selectable "selectable")
(def attr-striped    "striped")
(def attr-bordered   "bordered")
(def attr-full-width "full-width")
(def attr-compact    "compact")
(def attr-row-count  "row-count")

(def observed-attributes
  #js [attr-columns attr-caption attr-selectable
       attr-striped attr-bordered attr-full-width
       attr-compact attr-row-count])

;; ── Event names ──────────────────────────────────────────────────────────────

(def event-sort       "x-table-sort")
(def event-row-select "x-table-row-select")

;; ── Property API metadata ────────────────────────────────────────────────────

(def property-api
  {:columns    {:type 'string}
   :caption    {:type 'string}
   :selectable {:type 'string}
   :striped    {:type 'boolean}
   :bordered   {:type 'boolean}
   :fullWidth  {:type 'boolean}
   :compact    {:type 'boolean}
   :rowCount   {:type 'number}})

(def event-schema
  {event-sort
   {:detail     {:colIndex 'number :direction 'string :previousDirection 'string}
    :cancelable true}

   event-row-select
   {:detail     {:rowIndex 'number :selected 'boolean :selectionMode 'string}
    :cancelable true}})

;; ── Private enum sets ────────────────────────────────────────────────────────

(def ^:private selectable-values #{"none" "single" "multi"})

;; ── Parse helpers ────────────────────────────────────────────────────────────

(defn parse-columns
  "Parse `columns` attribute to a CSS grid-template-columns string.
  A positive integer string (e.g. \"4\") becomes \"repeat(4,1fr)\".
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
            (str "repeat(" n ",1fr)")
            t))))))

(defn parse-selectable
  "Normalise selectable attribute. Unknown/nil → \"none\"."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? selectable-values v) v "none")))

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

  Output keys mirror the attribute semantics with parsed/normalised values."
  [{:keys [columns-raw caption-raw selectable-raw
           striped? bordered? full-width? compact? row-count-raw]}]
  {:columns    (parse-columns columns-raw)
   :caption    (or caption-raw "")
   :selectable (parse-selectable selectable-raw)
   :striped?   (boolean striped?)
   :bordered?  (boolean bordered?)
   :full-width? (boolean full-width?)
   :compact?   (boolean compact?)
   :row-count  (parse-row-count row-count-raw)})

;; ── Derived view-model functions ─────────────────────────────────────────────

(defn role-for-selectable
  "Return ARIA role string. \"none\" → \"table\"; any selection mode → \"grid\"."
  [selectable]
  (if (= selectable "none") "table" "grid"))

(defn aria-multiselectable
  "Return \"true\" for multi-select, nil otherwise (attribute should be removed)."
  [selectable]
  (when (= selectable "multi") "true"))

(defn sort-detail
  "Build event detail for x-table-sort."
  [col-index direction previous-direction]
  {:colIndex         col-index
   :direction        direction
   :previousDirection previous-direction})

(defn row-select-detail
  "Build event detail for x-table-row-select."
  [row-index selected? selectable]
  {:rowIndex      (or row-index 0)
   :selected      selected?
   :selectionMode selectable})

(def method-api nil)
