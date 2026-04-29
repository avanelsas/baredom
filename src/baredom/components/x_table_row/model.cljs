(ns baredom.components.x-table-row.model)

(def tag-name "x-table-row")

(def attr-selected    "selected")
(def attr-disabled    "disabled")
(def attr-interactive "interactive")
(def attr-row-index   "row-index")

(def observed-attributes
  #js [attr-selected attr-disabled attr-interactive attr-row-index])

;; ── Event names ──────────────────────────────────────────────────────────────

(def event-click        "x-table-row-click")
(def event-connected    "x-table-row-connected")
(def event-disconnected "x-table-row-disconnected")

;; ── Property API metadata ────────────────────────────────────────────────────

(def property-api
  {:selected    {:type 'boolean}
   :disabled    {:type 'boolean}
   :interactive {:type 'boolean}
   :rowIndex    {:type 'number}})

(def event-schema
  {event-click        {:detail     {:rowIndex 'number :selected 'boolean :disabled 'boolean}
                       :cancelable true}
   event-connected    {:detail     {:rowIndex 'number :selected 'boolean
                                   :disabled 'boolean :interactive 'boolean}
                       :cancelable false}
   event-disconnected {:detail     {}
                       :cancelable false}})

;; ── Parse helpers ────────────────────────────────────────────────────────────

(defn parse-row-index
  "Parse a row-index attribute string to a positive integer.
  Returns nil for absent, zero, negative, or non-numeric input."
  [s]
  (when (string? s)
    (let [n (js/parseInt (.trim s) 10)]
      (when (and (not (js/isNaN n)) (pos? n))
        (js/Math.floor n)))))

;; ── Normalisation ────────────────────────────────────────────────────────────

(defn normalize
  "Produce a canonical view-model map from raw attribute inputs.

  Input keys:
    :selected?      boolean
    :disabled?      boolean
    :interactive?   boolean
    :row-index-raw  string | nil

  Output keys mirror the attribute semantics with parsed/normalised values."
  [{:keys [selected? disabled? interactive? row-index-raw]}]
  {:selected?    (boolean selected?)
   :disabled?    (boolean disabled?)
   :interactive? (boolean interactive?)
   :row-index    (parse-row-index row-index-raw)})

;; ── Derived view-model functions ─────────────────────────────────────────────

(defn interactive-eligible?
  "Returns true when the row should respond to user interaction
  (interactive attribute set and not disabled)."
  [{:keys [interactive? disabled?]}]
  (and interactive? (not disabled?)))

(defn aria-selected-value
  "Returns the aria-selected string value or nil (to remove the attribute).

  Rules:
  - selected → \"true\"
  - interactive but not selected → \"false\" (row is in a selection context)
  - neither interactive nor selected → nil (row is not selectable, omit attribute)"
  [{:keys [selected? interactive?]}]
  (cond
    selected?    "true"
    interactive? "false"
    :else        nil))

(defn connected-detail
  "Build the event detail map for x-table-row-connected."
  [{:keys [selected? disabled? interactive? row-index]}]
  {:rowIndex    (or row-index 0)
   :selected    selected?
   :disabled    disabled?
   :interactive interactive?})

(defn click-detail
  "Build the event detail map for x-table-row-click."
  [{:keys [selected? disabled? row-index]}]
  {:rowIndex (or row-index 0)
   :selected selected?
   :disabled disabled?})

(def method-api nil)
