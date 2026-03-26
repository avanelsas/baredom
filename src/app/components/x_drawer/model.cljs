(ns app.components.x-drawer.model)

(def tag-name "x-drawer")

;; Attribute name constants
(def attr-open      "open")
(def attr-placement "placement")
(def attr-label     "label")

;; Event name constants
(def event-toggle  "x-drawer-toggle")
(def event-dismiss "x-drawer-dismiss")

;; Part name constants
(def part-backdrop "backdrop")
(def part-panel    "panel")
(def part-header   "header")
(def part-body     "body")
(def part-footer   "footer")

;; Dismiss reason constants
(def reason-escape   "escape")
(def reason-backdrop "backdrop")

;; Default values
(def default-label     "Drawer")
(def default-placement "right")

;; Allowed placement values
(def allowed-placements #{"left" "right" "top" "bottom"})

(def observed-attributes
  #js ["open" "placement" "label"])

(defn normalize-placement
  "Normalize raw placement attribute value. Falls back to default-placement."
  [raw]
  (if (and (string? raw) (contains? allowed-placements raw))
    raw
    default-placement))

(defn normalize-label
  "Normalize raw label attribute value. Falls back to default-label."
  [raw]
  (if (and (string? raw) (not= raw ""))
    raw
    default-label))

(defn normalize
  "Produce a normalized view-model map from raw attribute values."
  [{:keys [open-present? placement-raw label-raw]}]
  {:open?     (boolean open-present?)
   :placement (normalize-placement placement-raw)
   :label     (normalize-label label-raw)})

(defn toggle-event-detail
  "Build the x-drawer-toggle CustomEvent detail."
  [open]
  #js {:open open})

(defn dismiss-event-detail
  "Build the x-drawer-dismiss CustomEvent detail."
  [reason]
  #js {:reason reason})

(def property-api
  {:open      {:type 'boolean :reflects-attribute attr-open}
   :placement {:type 'string  :reflects-attribute attr-placement}
   :label     {:type 'string  :reflects-attribute attr-label}})

(def event-schema
  {event-toggle  {:cancelable false :detail {:open 'boolean}}
   event-dismiss {:cancelable false :detail {:reason 'string}}})
