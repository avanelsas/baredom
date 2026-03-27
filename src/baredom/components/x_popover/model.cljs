(ns baredom.components.x-popover.model)

(def tag-name "x-popover")

;; Attribute name constants
(def attr-open        "open")
(def attr-placement   "placement")
(def attr-heading     "heading")
(def attr-close-label "close-label")
(def attr-no-close    "no-close")
(def attr-disabled    "disabled")

;; Event name constants
(def event-toggle "x-popover-toggle")
(def event-change "x-popover-change")

;; Placement constants
(def allowed-placements #{"bottom-start" "bottom-end" "top-start" "top-end"})
(def default-placement "bottom-start")
(def default-close-label "Close")

(def observed-attributes
  #js [attr-open attr-placement attr-heading attr-close-label attr-no-close attr-disabled])

(def property-api
  {:open       {:type 'boolean :reflects-attribute attr-open}
   :placement  {:type 'string  :reflects-attribute attr-placement}
   :heading    {:type 'string  :reflects-attribute attr-heading}
   :closeLabel {:type 'string  :reflects-attribute attr-close-label}
   :noClose    {:type 'boolean :reflects-attribute attr-no-close}
   :disabled   {:type 'boolean :reflects-attribute attr-disabled}})

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
  [{:keys [open-present? disabled-present? no-close-present?
           heading-raw placement-raw close-label-raw]}]
  {:open?       (boolean open-present?)
   :disabled?   (boolean disabled-present?)
   :no-close?   (boolean no-close-present?)
   :heading     (or heading-raw "")
   :close-label (or close-label-raw default-close-label)
   :placement   (normalize-placement placement-raw)})

(defn toggle-detail
  "Produces the CustomEvent detail object for toggle/change events."
  [open? source]
  #js {:open open? :source source})
