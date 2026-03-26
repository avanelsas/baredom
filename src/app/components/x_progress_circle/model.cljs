(ns app.components.x-progress-circle.model)

(def tag-name "x-progress-circle")

(def attr-value         "value")
(def attr-max           "max")
(def attr-variant       "variant")
(def attr-size          "size")
(def attr-label         "label")
(def attr-show-value    "show-value")
(def attr-indeterminate "indeterminate")

(def event-complete "x-progress-circle-complete")

(def default-variant "default")
(def default-size    "md")
(def default-value   0)
(def default-max     100)

(def allowed-variants #{"default" "success" "warning" "danger"})
(def allowed-sizes    #{"sm" "md" "lg"})

(def observed-attributes
  #js [attr-value
       attr-max
       attr-variant
       attr-size
       attr-label
       attr-show-value
       attr-indeterminate])

(def property-api
  {:value         {:type 'string  :reflects-attribute attr-value}
   :max           {:type 'string  :reflects-attribute attr-max}
   :indeterminate {:type 'boolean :reflects-attribute attr-indeterminate}
   :showValue     {:type 'boolean :reflects-attribute attr-show-value}})

(def event-schema
  {event-complete {:detail {:value 'number :max 'number}}})

(defn normalize-number
  [s default-val]
  (if (string? s)
    (let [n (js/parseFloat s)]
      (if (js/isNaN n) default-val n))
    default-val))

(defn normalize-value
  [value-str max-val]
  (let [v (normalize-number value-str default-value)]
    (-> v (max 0) (min max-val))))

(defn normalize-max
  [max-str]
  (let [m (normalize-number max-str default-max)]
    (if (> m 0) m default-max)))

(defn normalize-variant
  [value]
  (if (and (string? value) (contains? allowed-variants value))
    value
    default-variant))

(defn normalize-size
  [value]
  (if (and (string? value) (contains? allowed-sizes value))
    value
    default-size))

(defn derive-state
  [{:keys [value variant size label show-value indeterminate] max-attr :max}]
  (let [norm-max     (normalize-max max-attr)
        norm-value   (normalize-value value norm-max)
        norm-variant (normalize-variant variant)
        norm-size    (normalize-size size)
        indet?       (boolean indeterminate)
        show-val?    (boolean show-value)
        percent      (if indet?
                       50
                       (-> (* 100.0 (/ norm-value norm-max))
                           (max 0)
                           (min 100)))]
    {:value          norm-value
     :max            norm-max
     :percent        percent
     :variant        norm-variant
     :size           norm-size
     :label          (when (and (string? label) (not= "" label)) label)
     :show-value     show-val?
     :indeterminate  indet?
     :aria-valuetext (if indet? "Loading\u2026" (str (js/Math.round percent) "%"))}))
