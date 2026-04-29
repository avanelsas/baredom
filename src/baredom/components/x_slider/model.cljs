(ns baredom.components.x-slider.model)

(def tag-name "x-slider")

;; ---------------------------------------------------------------------------
;; Attribute name constants
;; ---------------------------------------------------------------------------
(def attr-value            "value")
(def attr-min              "min")
(def attr-max              "max")
(def attr-step             "step")
(def attr-disabled         "disabled")
(def attr-readonly         "readonly")
(def attr-name             "name")
(def attr-label            "label")
(def attr-show-value       "show-value")
(def attr-size             "size")
(def attr-aria-label       "aria-label")
(def attr-aria-labelledby  "aria-labelledby")
(def attr-aria-describedby "aria-describedby")

;; ---------------------------------------------------------------------------
;; Event name constants
;; ---------------------------------------------------------------------------
(def event-input  "x-slider-input")
(def event-change "x-slider-change")

;; ---------------------------------------------------------------------------
;; Defaults and allowed values
;; ---------------------------------------------------------------------------
(def default-value 0)
(def default-min   0)
(def default-max   100)
(def default-step  "1")
(def default-size  "md")

(def allowed-sizes #{"sm" "md" "lg"})

;; ---------------------------------------------------------------------------
;; Observed attributes
;; ---------------------------------------------------------------------------
(def observed-attributes
  #js [attr-value
       attr-min
       attr-max
       attr-step
       attr-disabled
       attr-readonly
       attr-name
       attr-label
       attr-show-value
       attr-size
       attr-aria-label
       attr-aria-labelledby
       attr-aria-describedby])

;; ---------------------------------------------------------------------------
;; API metadata
;; ---------------------------------------------------------------------------
(def property-api
  {:value     {:type 'string  :reflects-attribute attr-value}
   :min       {:type 'string  :reflects-attribute attr-min}
   :max       {:type 'string  :reflects-attribute attr-max}
   :step      {:type 'string  :reflects-attribute attr-step}
   :disabled  {:type 'boolean :reflects-attribute attr-disabled}
   :readOnly  {:type 'boolean :reflects-attribute attr-readonly}
   :showValue {:type 'boolean :reflects-attribute attr-show-value}
   :name      {:type 'string  :reflects-attribute attr-name}
   :label     {:type 'string  :reflects-attribute attr-label}
   :size      {:type 'string  :reflects-attribute attr-size}})

(def event-schema
  {event-input  {:detail {:value 'number :min 'number :max 'number}}
   event-change {:detail {:value 'number :min 'number :max 'number}}})

;; ---------------------------------------------------------------------------
;; Normalization helpers
;; ---------------------------------------------------------------------------
(defn normalize-number
  [s default-val]
  (if (string? s)
    (let [n (js/parseFloat s)]
      (if (js/isNaN n) default-val n))
    default-val))

(defn normalize-min
  [s]
  (normalize-number s default-min))

(defn normalize-max
  [s]
  (normalize-number s default-max))

(defn normalize-value
  [s min-val max-val]
  (let [v (normalize-number s default-value)]
    (-> v (max min-val) (min max-val))))

(defn normalize-step
  [s]
  (if (= s "any")
    "any"
    (let [n (if (string? s) (js/parseFloat s) js/NaN)]
      (if (and (not (js/isNaN n)) (> n 0))
        (str n)
        default-step))))

(defn normalize-size
  [s]
  (if (and (string? s) (contains? allowed-sizes s))
    s
    default-size))

;; ---------------------------------------------------------------------------
;; Fill percentage
;; ---------------------------------------------------------------------------
(defn fill-percent
  [value min-val max-val]
  (if (> max-val min-val)
    (-> (* 100.0 (/ (- value min-val) (- max-val min-val)))
        (max 0.0)
        (min 100.0))
    0.0))

;; ---------------------------------------------------------------------------
;; Derive state
;; ---------------------------------------------------------------------------
(defn derive-state
  [{:keys [value min max step disabled readonly name label show-value size
           aria-label aria-labelledby aria-describedby]}]
  (let [norm-min   (normalize-min min)
        norm-max   (normalize-max max)
        norm-value (normalize-value value norm-min norm-max)
        norm-step  (normalize-step step)
        norm-size  (normalize-size size)
        disabled?  (boolean disabled)
        readonly?  (boolean readonly)
        show-val?  (boolean show-value)
        pct        (fill-percent norm-value norm-min norm-max)]
    {:value            norm-value
     :min              norm-min
     :max              norm-max
     :step             norm-step
     :size             norm-size
     :disabled?        disabled?
     :readonly?        readonly?
     :name             (when (and (string? name) (not= "" name)) name)
     :label            (when (and (string? label) (not= "" label)) label)
     :show-value?      show-val?
     :fill-percent     pct
     :aria-label       (when (and (string? aria-label) (not= "" aria-label)) aria-label)
     :aria-labelledby  (when (and (string? aria-labelledby) (not= "" aria-labelledby)) aria-labelledby)
     :aria-describedby (when (and (string? aria-describedby) (not= "" aria-describedby)) aria-describedby)}))

(def method-api nil)
