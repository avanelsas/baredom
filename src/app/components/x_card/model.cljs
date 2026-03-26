(ns app.components.x-card.model)

(def tag-name "x-card")

(def attr-variant "variant")
(def attr-padding "padding")
(def attr-radius "radius")
(def attr-interactive "interactive")
(def attr-disabled "disabled")
(def attr-label "label")

(def observed-attributes
  #js [attr-variant
       attr-padding
       attr-radius
       attr-interactive
       attr-disabled
       attr-label])

(def variant-values #{"elevated" "outlined" "filled" "ghost"})
(def padding-values #{"none" "sm" "md" "lg"})
(def radius-values #{"none" "sm" "md" "lg" "xl"})

(def default-variant "elevated")
(def default-padding "md")
(def default-radius "lg")

(def property-api
  {:interactive
   {:attribute attr-interactive
    :type 'boolean
    :default false
    :reflects-to-attribute true}

   :disabled
   {:attribute attr-disabled
    :type 'boolean
    :default false
    :reflects-to-attribute true}})

(def event-press "press")

(def event-schema
  {:press
   {:event-name "press"
    :detail {}
    :bubbles true
    :composed true}})

(defn valid-enum
  [value allowed fallback]
  (if (contains? allowed value) value fallback))

(defn normalize-variant
  [value]
  (valid-enum value variant-values default-variant))

(defn normalize-padding
  [value]
  (valid-enum value padding-values default-padding))

(defn normalize-radius
  [value]
  (valid-enum value radius-values default-radius))

(defn normalize-bool
  [value]
  (boolean value))

(defn normalize-label
  [value]
  (when (and (string? value) (not= "" value))
    value))

(defn derive-state
  [{:keys [variant padding radius interactive disabled label]}]
  (let [variant* (normalize-variant variant)
        padding* (normalize-padding padding)
        radius* (normalize-radius radius)
        interactive* (normalize-bool interactive)
        disabled* (normalize-bool disabled)
        role (when interactive* "button")
        tabindex (cond
                   (and interactive* disabled*) "-1"
                   interactive* "0"
                   :else nil)]
    {:variant variant*
     :padding padding*
     :radius radius*
     :interactive interactive*
     :disabled disabled*
     :role role
     :tabindex tabindex
     :aria-label (normalize-label label)
     :aria-disabled (when disabled* "true")}))
