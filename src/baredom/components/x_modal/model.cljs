(ns baredom.components.x-modal.model)

(def tag-name "x-modal")

;; Attribute name constants
(def attr-open  "open")
(def attr-size  "size")
(def attr-label "label")

;; Event name constants
(def event-toggle  "x-modal-toggle")
(def event-dismiss "x-modal-dismiss")

;; Part name constants
(def part-backdrop "backdrop")
(def part-dialog   "dialog")
(def part-header   "header")
(def part-body     "body")
(def part-footer   "footer")

;; Dismiss reason constants
(def reason-escape   "escape")
(def reason-backdrop "backdrop")

;; Default values
(def default-label "Modal")
(def default-size  "md")

;; Allowed size values
(def allowed-sizes #{"sm" "md" "lg" "xl" "full"})

;; ARIA / role constants
(def role-dialog   "dialog")
(def aria-modal    "aria-modal")
(def aria-label    "aria-label")

(def observed-attributes
  #js [attr-open attr-size attr-label])

(defn normalize-size
  "Normalize raw size attribute value. Falls back to default-size."
  [raw]
  (if (and (string? raw) (contains? allowed-sizes raw))
    raw
    default-size))

(defn normalize-label
  "Normalize raw label attribute value. Falls back to default-label."
  [raw]
  (if (and (string? raw) (not= raw ""))
    raw
    default-label))

(defn normalize
  "Produce a normalized view-model map from raw attribute values."
  [{:keys [open-present? size-raw label-raw]}]
  {:open?  (boolean open-present?)
   :size   (normalize-size size-raw)
   :label  (normalize-label label-raw)})

(defn toggle-event-detail
  "Build the x-modal-toggle CustomEvent detail."
  [open]
  #js {:open open})

(defn dismiss-event-detail
  "Build the x-modal-dismiss CustomEvent detail."
  [reason]
  #js {:reason reason})

(def property-api
  {:open  {:type 'boolean :reflects-attribute attr-open}
   :size  {:type 'string  :reflects-attribute attr-size}
   :label {:type 'string  :reflects-attribute attr-label}})

(def event-schema
  {event-toggle  {:cancelable false :detail {:open 'boolean}}
   event-dismiss {:cancelable false :detail {:reason 'string}}})
