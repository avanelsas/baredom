(ns baredom.components.x-search-field.model)

(def tag-name "x-search-field")

;; Attribute name constants
(def attr-name         "name")
(def attr-value        "value")
(def attr-placeholder  "placeholder")
(def attr-label        "label")
(def attr-disabled     "disabled")
(def attr-required     "required")
(def attr-autocomplete "autocomplete")
(def attr-debounce     "debounce")

;; Event name constants
(def event-input  "x-search-field-input")
(def event-change "x-search-field-change")
(def event-search "x-search-field-search")
(def event-clear  "x-search-field-clear")

(def observed-attributes
  #js [attr-name
       attr-value
       attr-placeholder
       attr-label
       attr-disabled
       attr-required
       attr-autocomplete
       attr-debounce])

(def allowed-autocomplete #{"on" "off"})

(defn normalize-autocomplete [raw]
  (if (and (string? raw) (contains? allowed-autocomplete raw))
    raw
    "off"))

(defn normalize-debounce
  "Parses the raw `debounce` attribute into a non-negative millisecond count.
   Absent, non-numeric, or negative input yields 0 (debounce disabled)."
  [raw]
  (if (string? raw)
    (let [n (js/parseFloat raw)]
      (if (or (js/isNaN n) (neg? n)) 0 n))
    0))

(def property-api
  {:value        {:type 'string  :reflects-attribute attr-value}
   :name         {:type 'string  :reflects-attribute attr-name}
   :placeholder  {:type 'string  :reflects-attribute attr-placeholder}
   :label        {:type 'string  :reflects-attribute attr-label}
   :autocomplete {:type 'string  :reflects-attribute attr-autocomplete}
   :disabled     {:type 'boolean :reflects-attribute attr-disabled}
   :required     {:type 'boolean :reflects-attribute attr-required}
   :debounce     {:type 'number  :reflects-attribute attr-debounce}})

(def event-schema
  {event-input  {:cancelable false :detail {:name 'string :value 'string}}
   event-change {:cancelable false :detail {:name 'string :value 'string}}
   event-search {:cancelable true  :detail {:name 'string :value 'string}}
   event-clear  {:cancelable false :detail {:name 'string}}})

(defn normalize
  "Derives a complete view-model map from raw attribute values."
  [{:keys [name-raw value-raw placeholder-raw label-raw
           disabled-present? required-present? autocomplete-raw debounce-raw]}]
  {:name         (or name-raw "")
   :value        (or value-raw "")
   :placeholder  (or placeholder-raw "")
   :label        (or label-raw "")
   :disabled?    (boolean disabled-present?)
   :required?    (boolean required-present?)
   :autocomplete (normalize-autocomplete autocomplete-raw)
   :debounce     (normalize-debounce debounce-raw)})

(def method-api
  {:checkValidity  {:args [] :returns 'boolean}
   :reportValidity {:args [] :returns 'boolean}})
