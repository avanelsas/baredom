(ns app.components.x-text-area.model)

(def tag-name "x-text-area")

;; Attribute name constants
(def attr-label        "label")
(def attr-name         "name")
(def attr-value        "value")
(def attr-placeholder  "placeholder")
(def attr-hint         "hint")
(def attr-error        "error")
(def attr-disabled     "disabled")
(def attr-readonly     "readonly")
(def attr-required     "required")
(def attr-rows         "rows")
(def attr-maxlength    "maxlength")
(def attr-minlength    "minlength")
(def attr-autocomplete "autocomplete")
(def attr-resize       "resize")

;; Event name constants
(def event-input  "x-text-area-input")
(def event-change "x-text-area-change")

(def observed-attributes
  #js [attr-label
       attr-name
       attr-value
       attr-placeholder
       attr-hint
       attr-error
       attr-disabled
       attr-readonly
       attr-required
       attr-rows
       attr-maxlength
       attr-minlength
       attr-autocomplete
       attr-resize])

(def allowed-resize
  #{"none" "vertical" "horizontal" "both"})

(defn normalize-resize [raw]
  (if (and (string? raw) (contains? allowed-resize raw))
    raw
    "vertical"))

(defn parse-positive-int [raw]
  (when (string? raw)
    (let [n (js/parseInt raw 10)]
      (when (and (not (js/isNaN n)) (pos? n))
        n))))

(def property-api
  {:value        {:type 'string  :reflects-attribute attr-value}
   :name         {:type 'string  :reflects-attribute attr-name}
   :disabled     {:type 'boolean :reflects-attribute attr-disabled}
   :readOnly     {:type 'boolean :reflects-attribute attr-readonly}
   :required     {:type 'boolean :reflects-attribute attr-required}
   :rows         {:type 'number  :reflects-attribute attr-rows}
   :maxLength    {:type 'number  :reflects-attribute attr-maxlength}
   :minLength    {:type 'number  :reflects-attribute attr-minlength}
   :autocomplete {:type 'string  :reflects-attribute attr-autocomplete}})

(def event-schema
  {:input  {:name event-input  :cancelable false :detail #{:name :value}}
   :change {:name event-change :cancelable false :detail #{:name :value}}})

(defn normalize
  "Derives a complete view-model map from raw attribute values."
  [{:keys [label-raw name-raw value-raw placeholder-raw
           hint-raw error-raw disabled-present? readonly-present?
           required-present? rows-raw maxlength-raw minlength-raw
           autocomplete-raw resize-raw]}]
  (let [error (or error-raw "")
        hint  (or hint-raw "")
        label (or label-raw "")]
    {:label        label
     :name         (or name-raw "")
     :value        (or value-raw "")
     :placeholder  (or placeholder-raw "")
     :hint         hint
     :error        error
     :disabled?    (boolean disabled-present?)
     :readonly?    (boolean readonly-present?)
     :required?    (boolean required-present?)
     :rows         (or (parse-positive-int rows-raw) 3)
     :maxlength    (parse-positive-int maxlength-raw)
     :minlength    (parse-positive-int minlength-raw)
     :autocomplete (or autocomplete-raw "")
     :resize       (normalize-resize resize-raw)
     :has-error?   (and (string? error-raw) (not= error-raw "") (some? error-raw))
     :has-hint?    (and (string? hint-raw)  (not= hint-raw "")  (some? hint-raw))
     :has-label?   (and (string? label-raw) (not= label-raw "") (some? label-raw))}))
