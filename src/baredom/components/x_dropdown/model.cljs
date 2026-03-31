(ns baredom.components.x-dropdown.model)

(def tag-name "x-dropdown")

;; Attribute name constants
(def attr-open      "open")
(def attr-disabled  "disabled")
(def attr-label     "label")
(def attr-placement "placement")

;; Event name constants
(def event-toggle "x-dropdown-toggle")
(def event-change "x-dropdown-change")

;; Placement constants
(def allowed-placements #{"bottom-start" "bottom-end" "top-start" "top-end"})
(def default-placement "bottom-start")

(def observed-attributes
  #js [attr-open attr-disabled attr-label attr-placement])

(def property-api
  {:open      {:type 'boolean :reflects-attribute attr-open}
   :disabled  {:type 'boolean :reflects-attribute attr-disabled}
   :label     {:type 'string  :reflects-attribute attr-label}
   :placement {:type 'string  :reflects-attribute attr-placement}})

(def event-schema
  {event-toggle {:cancelable true
                 :detail     {:open   'boolean
                              :source 'string}}
   event-change {:cancelable false
                 :detail     {:open 'boolean}}})

(defn normalize-placement
  "Normalizes raw placement string. Falls back to default-placement if invalid or nil."
  [s]
  (if (contains? allowed-placements s) s default-placement))

(defn normalize
  "Derives a complete view-model map from raw attribute presence/values."
  [{:keys [open-present? disabled-present? label-raw placement-raw]}]
  {:open?     (boolean open-present?)
   :disabled? (boolean disabled-present?)
   :label     (or label-raw "")
   :placement (normalize-placement placement-raw)})

(defn toggle-detail
  "Produces the CustomEvent detail object for toggle/change events."
  [open? source]
  #js {:open open? :source source})
