(ns app.components.x-timeline.model)

(def tag-name "x-timeline")

;; ── Attribute name constants ──────────────────────────────────────────────────
(def attr-label    "label")
(def attr-position "position")
(def attr-striped  "striped")

(def observed-attributes
  #js [attr-label attr-position attr-striped])

;; ── Child component constants (no cross-namespace import) ─────────────────────
(def child-tag                "x-timeline-item")
(def child-event-connected    "x-timeline-item-connected")
(def child-event-disconnected "x-timeline-item-disconnected")
(def child-event-click        "x-timeline-item-click")

;; ── Event name constants ──────────────────────────────────────────────────────
(def event-select "x-timeline-select")

;; ── Public API metadata ───────────────────────────────────────────────────────
(def property-api
  {:label    {:type 'string}
   :position {:type 'string}
   :striped  {:type 'boolean}})

(def event-schema
  {event-select {:detail     {:index 'number :status 'string :label 'string}
                 :cancelable false}})

;; ── Private lookup ────────────────────────────────────────────────────────────
(def ^:private valid-positions #{"start" "end" "alternating"})

;; ── Parse helpers ─────────────────────────────────────────────────────────────
(defn parse-position
  "Normalise a raw position attribute string to \"start\", \"end\", or \"alternating\".
  Unknown/nil values fall back to \"start\"."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? valid-positions v) v "start")))

;; ── Derived functions ─────────────────────────────────────────────────────────
(defn item-position
  "Return the data-position string to assign to a child item.
  position — the coordinator's resolved position string
  index    — the child's 0-based index

  :start       → always \"start\"
  :end         → always \"end\"
  :alternating → even index → \"start\", odd index → \"end\""
  [position index]
  (case position
    "start"       "start"
    "end"         "end"
    "alternating" (if (zero? (mod index 2)) "start" "end")
    "start"))

;; ── Normalize ─────────────────────────────────────────────────────────────────
(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :label-raw     string | nil
    :position-raw  string | nil
    :striped?      boolean

  Output keys:
    :label     string
    :position  string  — \"start\" | \"end\" | \"alternating\"
    :striped?  boolean"
  [{:keys [label-raw position-raw striped?]}]
  {:label    (or label-raw "")
   :position (parse-position position-raw)
   :striped? (boolean striped?)})

;; ── Event detail builders ─────────────────────────────────────────────────────
(defn select-detail
  "Build the event detail map for an x-timeline-select event."
  [index status label]
  {:index  (or index 0)
   :status (or status "")
   :label  (or label "")})
