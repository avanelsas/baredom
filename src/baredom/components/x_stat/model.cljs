(ns baredom.components.x-stat.model)

(def tag-name "x-stat")

(def attr-variant "variant")
(def attr-align "align")
(def attr-size "size")
(def attr-emphasis "emphasis")
(def attr-trend "trend")
(def attr-loading "loading")
(def attr-label "label")
(def attr-value "value")
(def attr-hint "hint")

(def observed-attributes
  #js [attr-variant
       attr-align
       attr-size
       attr-emphasis
       attr-trend
       attr-loading
       attr-label
       attr-value
       attr-hint])

(def variant-values #{"default" "subtle" "positive" "warning" "danger"})
(def align-values #{"start" "center" "end"})
(def size-values #{"sm" "md" "lg"})
(def emphasis-values #{"normal" "high"})
(def trend-values #{"up" "down" "neutral"})

(def default-variant "default")
(def default-align "start")
(def default-size "md")
(def default-emphasis "normal")
(def default-trend "neutral")

(def property-api
  {:variant
   {:attribute attr-variant
    :type 'string
    :default default-variant
    :reflects-to-attribute true}
   :align
   {:attribute attr-align
    :type 'string
    :default default-align
    :reflects-to-attribute true}
   :size
   {:attribute attr-size
    :type 'string
    :default default-size
    :reflects-to-attribute true}
   :emphasis
   {:attribute attr-emphasis
    :type 'string
    :default default-emphasis
    :reflects-to-attribute true}
   :trend
   {:attribute attr-trend
    :type 'string
    :default default-trend
    :reflects-to-attribute true}
   :loading
   {:attribute attr-loading
    :type 'boolean
    :default false
    :reflects-to-attribute true}
   :label
   {:attribute attr-label
    :type 'string
    :default ""
    :reflects-to-attribute true}
   :value
   {:attribute attr-value
    :type 'string
    :default ""
    :reflects-to-attribute true}
   :hint
   {:attribute attr-hint
    :type 'string
    :default ""
    :reflects-to-attribute true}})

(def event-schema {})

(defn valid-enum
  [value allowed fallback]
  (if (contains? allowed value) value fallback))

(defn normalize-variant [v] (valid-enum v variant-values default-variant))
(defn normalize-align [v] (valid-enum v align-values default-align))
(defn normalize-size [v] (valid-enum v size-values default-size))
(defn normalize-emphasis [v] (valid-enum v emphasis-values default-emphasis))
(defn normalize-trend [v] (valid-enum v trend-values default-trend))

(defn normalize-bool [v] (boolean v))

(defn normalize-text [v]
  (when (and v (not= "" v)) v))

(defn derive-state
  [{:keys [variant align size emphasis trend loading label value hint]}]

  (let [variant* (normalize-variant variant)
        align* (normalize-align align)
        size* (normalize-size size)
        emphasis* (normalize-emphasis emphasis)
        trend* (normalize-trend trend)
        loading* (normalize-bool loading)]

    {:variant variant*
     :align align*
     :size size*
     :emphasis emphasis*
     :trend trend*
     :loading loading*
     :aria-busy (when loading* "true")
     :label (normalize-text label)
     :value (normalize-text value)
     :hint (normalize-text hint)}))

(def method-api nil)
