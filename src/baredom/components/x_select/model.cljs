(ns baredom.components.x-select.model)

(def tag-name "x-select")

;; Attribute name constants
(def attr-disabled    "disabled")
(def attr-required    "required")
(def attr-size        "size")
(def attr-placeholder "placeholder")
(def attr-value       "value")
(def attr-name        "name")
(def attr-error       "error")

;; Event name constants
(def event-change-request "x-select-change-request")
(def event-select-change  "select-change")

;; Size enum
(def default-size "md")
(def allowed-sizes #{"sm" "md" "lg"})

(def observed-attributes
  #js [attr-disabled
       attr-required
       attr-size
       attr-placeholder
       attr-value
       attr-name
       attr-error])

(def property-api
  {:disabled {:type 'boolean :reflects-attribute attr-disabled}
   :required {:type 'boolean :reflects-attribute attr-required}
   :value    {:type 'string  :reflects-attribute attr-value}
   ;; `name` reflects the attribute like every other form control (and native
   ;; <select>), so `el.name` works and x-form can collect this field by name.
   :name     {:type 'string  :reflects-attribute attr-name}
   ;; `error` reflects the attribute so x-form's setFieldError can drive the
   ;; inline error message, mirroring x-form-field.
   :error    {:type 'string  :reflects-attribute attr-error}})

(def event-schema
  {event-change-request {:cancelable true
                         :detail {:value 'string :label 'string :previousValue 'string}}
   event-select-change  {:cancelable false
                         :detail {:value 'string :label 'string}}})

(defn normalize-enum
  "Returns value if it is in allowed, otherwise default-value."
  [value allowed default-value]
  (if (and (string? value) (contains? allowed value)) value default-value))

(defn normalize-size [v] (normalize-enum v allowed-sizes default-size))

(defn normalize
  "Derives a complete view-model map from raw attribute presence/values."
  [{:keys [disabled-present? required-present? size-raw placeholder-raw
           value-raw name-raw error-raw]}]
  {:disabled?   (boolean disabled-present?)
   :required?   (boolean required-present?)
   :size        (normalize-size size-raw)
   :placeholder placeholder-raw
   :value       value-raw
   :name        name-raw
   :error       (or error-raw "")
   ;; Coerce to a strict boolean: tests call normalize with sparse maps, so
   ;; `(and (string? nil) …)` must not leak nil into a has-error? predicate.
   :has-error?  (boolean (and (string? error-raw) (not= error-raw "")))})

(def method-api {})
