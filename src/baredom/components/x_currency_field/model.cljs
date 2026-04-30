(ns baredom.components.x-currency-field.model)

(def tag-name "x-currency-field")

;; Attribute name constants
(def attr-name        "name")
(def attr-value       "value")
(def attr-currency    "currency")
(def attr-locale      "locale")
(def attr-min         "min")
(def attr-max         "max")
(def attr-placeholder "placeholder")
(def attr-label       "label")
(def attr-hint        "hint")
(def attr-error       "error")
(def attr-disabled    "disabled")
(def attr-required    "required")
(def attr-readonly    "readonly")

;; Event name constants
(def event-change-request "x-currency-field-change-request")
(def event-input          "x-currency-field-input")
(def event-change         "x-currency-field-change")

(def observed-attributes
  #js [attr-name
       attr-value
       attr-currency
       attr-locale
       attr-min
       attr-max
       attr-placeholder
       attr-label
       attr-hint
       attr-error
       attr-disabled
       attr-required
       attr-readonly])

(defn normalize-currency [raw]
  (let [s (-> (or raw "") str .trim .toUpperCase)]
    (if (= s "") "USD" s)))

(def property-api
  {:value       {:type 'string  :reflects-attribute attr-value}
   :name        {:type 'string  :reflects-attribute attr-name}
   :currency    {:type 'string  :reflects-attribute attr-currency}
   :locale      {:type 'string  :reflects-attribute attr-locale}
   :min         {:type 'string  :reflects-attribute attr-min}
   :max         {:type 'string  :reflects-attribute attr-max}
   :placeholder {:type 'string  :reflects-attribute attr-placeholder}
   :label       {:type 'string  :reflects-attribute attr-label}
   :hint        {:type 'string  :reflects-attribute attr-hint}
   :error       {:type 'string  :reflects-attribute attr-error}
   :disabled    {:type 'boolean :reflects-attribute attr-disabled}
   :readOnly    {:type 'boolean :reflects-attribute attr-readonly}
   :required    {:type 'boolean :reflects-attribute attr-required}})

(def event-schema
  {event-change-request {:cancelable true  :detail {:name 'string :value 'string :previousValue 'string}}
   event-input          {:cancelable false :detail {:name 'string :value 'string}}
   event-change         {:cancelable false :detail {:name 'string :value 'string}}})

(defn normalize
  "Derives a complete view-model map from raw attribute values."
  [{:keys [name-raw value-raw currency-raw locale-raw min-raw max-raw
           placeholder-raw label-raw hint-raw error-raw
           disabled-present? required-present? readonly-present?]}]
  (let [error    (or error-raw "")
        hint     (or hint-raw "")
        label    (or label-raw "")]
    {:name         (or name-raw "")
     :value        (or value-raw "")
     :currency     (normalize-currency currency-raw)
     :locale       (or locale-raw "")
     :min          (or min-raw "")
     :max          (or max-raw "")
     :placeholder  (or placeholder-raw "")
     :label        label
     :hint         hint
     :error        error
     :disabled?    (boolean disabled-present?)
     :required?    (boolean required-present?)
     :readonly?    (boolean readonly-present?)
     :has-error?   (and (string? error-raw) (not= error-raw ""))
     :has-hint?    (and (string? hint-raw)  (not= hint-raw ""))
     :has-label?   (and (string? label-raw) (not= label-raw ""))}))

(defn validation-message
  "Returns a computed validation error string or empty string.
   Priority: badInput > rangeUnderflow > rangeOverflow."
  [{:keys [value min max]}]
  (let [num      (js/parseFloat value)
        has-num? (and (not= value "") (not (js/isNaN num)))
        min-num  (when (not= min "") (js/parseFloat min))
        max-num  (when (not= max "") (js/parseFloat max))]
    (cond
      (and (not= value "") (not has-num?))
      "Please enter a valid number."

      (and has-num? min-num (not (js/isNaN min-num)) (< num min-num))
      (str "Value must be at least " min ".")

      (and has-num? max-num (not (js/isNaN max-num)) (> num max-num))
      (str "Value must be at most " max ".")

      :else "")))

(def method-api nil)
