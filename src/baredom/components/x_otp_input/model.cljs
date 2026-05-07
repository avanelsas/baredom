(ns baredom.components.x-otp-input.model
  (:require [clojure.string :as str]))

(def tag-name "x-otp-input")

;; ---------------------------------------------------------------------------
;; Attribute name constants
;; ---------------------------------------------------------------------------
(def attr-name        "name")
(def attr-value       "value")
(def attr-length      "length")
(def attr-type        "type")
(def attr-mask        "mask")
(def attr-disabled    "disabled")
(def attr-readonly    "readonly")
(def attr-required    "required")
(def attr-autofocus   "autofocus")
(def attr-label       "label")
(def attr-placeholder "placeholder")
(def attr-error       "error")

;; ---------------------------------------------------------------------------
;; Event name constants
;; ---------------------------------------------------------------------------
(def event-input    "x-otp-input-input")
(def event-change   "x-otp-input-change")
(def event-complete "x-otp-input-complete")

(def observed-attributes
  #js [attr-name attr-value attr-length attr-type attr-mask
       attr-disabled attr-readonly attr-required attr-autofocus
       attr-label attr-placeholder attr-error])

;; ---------------------------------------------------------------------------
;; Allowed values and defaults
;; ---------------------------------------------------------------------------
(def default-length 6)
(def min-length 1)
(def max-length-cap 12)

(def type-numeric      "numeric")
(def type-alpha        "alpha")
(def type-alphanumeric "alphanumeric")
(def allowed-types #{type-numeric type-alphanumeric type-alpha})
(def default-type type-numeric)

;; ---------------------------------------------------------------------------
;; Pure parsers / filters
;; ---------------------------------------------------------------------------

(defn clamp-length [n]
  (cond
    (< n min-length)     min-length
    (> n max-length-cap) max-length-cap
    :else                n))

(defn parse-length
  "Parse the `length` attribute. Empty / non-numeric falls back to default-length.
  Numeric values are clamped to [min-length, max-length-cap]."
  [raw]
  (if (string? raw)
    (let [trimmed (.trim raw)]
      (if (= "" trimmed)
        default-length
        (let [n (js/parseInt trimmed 10)]
          (if (or (js/isNaN n) (not (number? n)))
            default-length
            (clamp-length n)))))
    default-length))

(defn parse-type
  "Parse the `type` attribute. Unknown / nil falls back to default-type."
  [raw]
  (if (string? raw)
    (let [lower (.toLowerCase (.trim raw))]
      (if (contains? allowed-types lower)
        lower
        default-type))
    default-type))

(defn filter-chars
  "Remove characters that are not allowed for the given OTP type."
  [type-name s]
  (if (string? s)
    (cond
      (= type-name type-numeric)      (str/replace s #"[^0-9]" "")
      (= type-name type-alpha)        (str/replace s #"[^A-Za-z]" "")
      (= type-name type-alphanumeric) (str/replace s #"[^A-Za-z0-9]" "")
      :else "")
    ""))

(defn truncate
  "Trim a string to at most `n` characters."
  [s n]
  (let [s (or s "")]
    (if (> (count s) n)
      (.substring s 0 n)
      s)))

(defn normalize-value
  "Filter and truncate a raw value string for the given OTP type and length."
  [type-name length raw]
  (-> (filter-chars type-name raw)
      (truncate length)))

(defn complete?
  "True when value has exactly `length` characters."
  [value length]
  (= (count value) length))

(defn placeholder-char
  "Return the single-character placeholder, or empty string."
  [raw]
  (if (and (string? raw) (pos? (count raw)))
    (.substring raw 0 1)
    ""))

(defn slot-aria-label
  "Build the per-slot aria-label string."
  [type-name index length]
  (str (if (= type-name type-numeric) "Digit " "Character ")
       (inc index) " of " length))

(defn slot-inputmode
  "Map OTP `type` to the slot input `inputmode` attribute."
  [type-name]
  (if (= type-name type-numeric) "numeric" "text"))

;; ---------------------------------------------------------------------------
;; Component metadata
;; ---------------------------------------------------------------------------

(def property-api
  {:name        {:type 'string  :reflects-attribute attr-name        :default ""}
   :value       {:type 'string  :reflects-attribute attr-value       :default ""}
   :length      {:type 'number  :reflects-attribute attr-length      :default default-length}
   :type        {:type 'string  :reflects-attribute attr-type        :default default-type}
   :mask        {:type 'boolean :reflects-attribute attr-mask}
   :disabled    {:type 'boolean :reflects-attribute attr-disabled}
   :readonly    {:type 'boolean :reflects-attribute attr-readonly}
   :required    {:type 'boolean :reflects-attribute attr-required}
   :autofocus   {:type 'boolean :reflects-attribute attr-autofocus}
   :label       {:type 'string  :reflects-attribute attr-label       :default ""}
   :placeholder {:type 'string  :reflects-attribute attr-placeholder :default ""}
   :error       {:type 'string  :reflects-attribute attr-error       :default ""}})

(def event-schema
  {event-input    {:cancelable false
                   :detail {:name 'string :value 'string :complete 'boolean}}
   event-change   {:cancelable false
                   :detail {:name 'string :value 'string :complete 'boolean}}
   event-complete {:cancelable false
                   :detail {:name 'string :value 'string}}})

(def method-api
  {"focus"          {:args [] :returns 'void}
   "clear"          {:args [] :returns 'void}
   "checkValidity"  {:args [] :returns 'boolean}
   "reportValidity" {:args [] :returns 'boolean}})

;; ---------------------------------------------------------------------------
;; View-model normalization
;; ---------------------------------------------------------------------------

(defn normalize
  "Derive a complete view-model map from raw attribute values."
  [{:keys [name-raw value-raw length-raw type-raw
           mask-present? disabled-present? readonly-present? required-present?
           autofocus-present? label-raw placeholder-raw error-raw]}]
  (let [type-norm   (parse-type type-raw)
        length-norm (parse-length length-raw)
        value-norm  (normalize-value type-norm length-norm value-raw)]
    {:name        (or name-raw "")
     :value       value-norm
     :length      length-norm
     :type        type-norm
     :mask?       (boolean mask-present?)
     :disabled?   (boolean disabled-present?)
     :readonly?   (boolean readonly-present?)
     :required?   (boolean required-present?)
     :autofocus?  (boolean autofocus-present?)
     :label       (or label-raw "")
     :placeholder (placeholder-char placeholder-raw)
     :error       (or error-raw "")
     :has-error?  (and (some? error-raw) (not= error-raw ""))
     :complete?   (complete? value-norm length-norm)}))
