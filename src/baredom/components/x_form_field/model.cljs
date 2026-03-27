(ns baredom.components.x-form-field.model)

(def tag-name "x-form-field")

;; Attribute name constants
(def attr-label        "label")
(def attr-type         "type")
(def attr-name         "name")
(def attr-value        "value")
(def attr-placeholder  "placeholder")
(def attr-hint         "hint")
(def attr-error        "error")
(def attr-disabled     "disabled")
(def attr-readonly     "readonly")
(def attr-required     "required")
(def attr-autocomplete "autocomplete")

;; Event name constants
(def event-input  "x-form-field-input")
(def event-change "x-form-field-change")

(def observed-attributes
  #js [attr-label
       attr-type
       attr-name
       attr-value
       attr-placeholder
       attr-hint
       attr-error
       attr-disabled
       attr-readonly
       attr-required
       attr-autocomplete])

(def allowed-types
  #{"text" "email" "password" "url" "number" "tel"})

(defn normalize-type [raw]
  (if (and (string? raw) (contains? allowed-types raw))
    raw
    "text"))

(def property-api
  {:value    {:type 'string  :reflects-attribute attr-value}
   :disabled {:type 'boolean :reflects-attribute attr-disabled}
   :readonly {:type 'boolean :reflects-attribute attr-readonly}
   :required {:type 'boolean :reflects-attribute attr-required}})

(def event-schema
  {:input  {:name event-input  :cancelable false :detail #{:name :value}}
   :change {:name event-change :cancelable false :detail #{:name :value}}})

(defn normalize
  "Derives a complete view-model map from raw attribute values."
  [{:keys [label-raw type-raw name-raw value-raw placeholder-raw
           hint-raw error-raw disabled-present? readonly-present?
           required-present? autocomplete-raw]}]
  (let [error        (or error-raw "")
        hint         (or hint-raw "")
        label        (or label-raw "")]
    {:label        label
     :type         (normalize-type type-raw)
     :name         (or name-raw "")
     :value        (or value-raw "")
     :placeholder  (or placeholder-raw "")
     :hint         hint
     :error        error
     :disabled?    (boolean disabled-present?)
     :readonly?    (boolean readonly-present?)
     :required?    (boolean required-present?)
     :autocomplete (or autocomplete-raw "")
     :has-error?   (and (string? error-raw) (not= error-raw "") (some? error-raw))
     :has-hint?    (and (string? hint-raw)  (not= hint-raw "")  (some? hint-raw))
     :has-label?   (and (string? label-raw) (not= label-raw "") (some? label-raw))}))
