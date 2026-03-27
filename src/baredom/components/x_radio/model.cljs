(ns baredom.components.x-radio.model)

(def tag-name "x-radio")

;; Attribute name constants
(def attr-checked        "checked")
(def attr-disabled       "disabled")
(def attr-readonly       "readonly")
(def attr-required       "required")
(def attr-name           "name")
(def attr-value          "value")
(def attr-aria-label     "aria-label")
(def attr-aria-describedby "aria-describedby")
(def attr-aria-labelledby  "aria-labelledby")

;; Event name constants
(def event-change-request "x-radio-change-request")
(def event-change         "x-radio-change")

(def observed-attributes
  #js [attr-checked
       attr-disabled
       attr-readonly
       attr-required
       attr-name
       attr-value
       attr-aria-label
       attr-aria-describedby
       attr-aria-labelledby])

(def property-api
  {:checked  {:type 'boolean :reflects-attribute attr-checked}
   :disabled {:type 'boolean :reflects-attribute attr-disabled}
   :readOnly {:type 'boolean :reflects-attribute attr-readonly}
   :required {:type 'boolean :reflects-attribute attr-required}
   :name     {:type 'string  :reflects-attribute attr-name}
   :value    {:type 'string  :reflects-attribute attr-value}})

(def event-schema
  {event-change-request {:cancelable true
                         :detail {:value          'string
                                  :previousChecked 'boolean
                                  :nextChecked     'boolean}}
   event-change         {:cancelable false
                         :detail {:value   'string
                                  :checked 'boolean}}})

(defn parse-bool-attr
  "Returns true if the attribute is present and not equal to \"false\"."
  [s]
  (and (some? s) (not= s "false")))

(defn radio-value
  "Returns the raw value string, or \"on\" if nil."
  [raw]
  (if (some? raw) raw "on"))

(defn valid?
  "Returns true when the field satisfies its required constraint."
  [required? checked?]
  (if required? checked? true))

(defn normalize
  "Derives a complete view-model map from raw attribute presence/values."
  [{:keys [checked-present?
           disabled-present?
           readonly-present?
           required-present?
           name-raw
           value-raw
           aria-label-raw
           aria-describedby-raw
           aria-labelledby-raw]}]
  (let [checked?  (boolean checked-present?)
        disabled? (boolean disabled-present?)
        readonly? (boolean readonly-present?)
        required? (boolean required-present?)
        value     (radio-value value-raw)]
    {:checked?  checked?
     :disabled? disabled?
     :readonly? readonly?
     :required? required?
     :valid?    (valid? required? checked?)
     :name      name-raw
     :value     value
     :aria-label       aria-label-raw
     :aria-describedby aria-describedby-raw
     :aria-labelledby  aria-labelledby-raw
     :aria-checked     (if checked? "true" "false")}))
