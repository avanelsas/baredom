(ns baredom.components.x-card.model)

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
  {:interactive {:type 'boolean :reflects-attribute attr-interactive}
   :disabled    {:type 'boolean :reflects-attribute attr-disabled}
   :variant     {:type 'string  :reflects-attribute attr-variant :default default-variant :enum variant-values}
   :padding     {:type 'string  :reflects-attribute attr-padding :default default-padding :enum padding-values}
   :radius      {:type 'string  :reflects-attribute attr-radius  :default default-radius :enum radius-values}
   :label       {:type 'string  :reflects-attribute attr-label   :default ""}})

(def event-press "press")

(def event-schema
  {event-press {:cancelable false :detail {}}})

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

(defn normalize
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

(defn interactive-active?
  [m]
  (and (:interactive m) (not (:disabled m))))

(def method-api {})
