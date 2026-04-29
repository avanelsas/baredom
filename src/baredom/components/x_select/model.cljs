(ns baredom.components.x-select.model)

(def tag-name "x-select")

;; Attribute name constants
(def attr-disabled    "disabled")
(def attr-required    "required")
(def attr-size        "size")
(def attr-placeholder "placeholder")
(def attr-value       "value")
(def attr-name        "name")

;; Event name constants
(def event-select-change "select-change")

;; Size enum
(def default-size "md")
(def allowed-sizes #{"sm" "md" "lg"})

(def observed-attributes
  #js [attr-disabled
       attr-required
       attr-size
       attr-placeholder
       attr-value
       attr-name])

(def property-api
  {:disabled {:type 'boolean :reflects-attribute attr-disabled}
   :required {:type 'boolean :reflects-attribute attr-required}
   :value    {:type 'string  :reflects-attribute attr-value}})

(def event-schema
  {event-select-change {:cancelable false
                        :detail {:value 'string :label 'string}}})

(defn normalize-enum
  "Returns value if it is in allowed, otherwise default-value."
  [value allowed default-value]
  (if (and (string? value) (contains? allowed value)) value default-value))

(defn normalize-size [v] (normalize-enum v allowed-sizes default-size))

(defn normalize
  "Derives a complete view-model map from raw attribute presence/values."
  [{:keys [disabled-present? required-present? size-raw placeholder-raw value-raw name-raw]}]
  {:disabled?   (boolean disabled-present?)
   :required?   (boolean required-present?)
   :size        (normalize-size size-raw)
   :placeholder placeholder-raw
   :value       value-raw
   :name        name-raw})

(def method-api nil)
