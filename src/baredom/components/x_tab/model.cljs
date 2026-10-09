(ns baredom.components.x-tab.model)

(def tag-name "x-tab")

(def attr-value "value")
(def attr-selected "selected")
(def attr-disabled "disabled")
(def attr-orientation "orientation")
(def attr-size "size")
(def attr-variant "variant")
(def attr-label "label")
(def attr-controls "controls")

(def observed-attributes
  #js [attr-value
       attr-selected
       attr-disabled
       attr-orientation
       attr-size
       attr-variant
       attr-label
       attr-controls])

(def orientation-values #{"horizontal" "vertical"})
(def size-values #{"sm" "md" "lg"})
(def variant-values #{"default" "underline" "pill"})

(def event-tab-select "tab-select")

(def default-orientation "horizontal")
(def default-size "md")
(def default-variant "default")

(def property-api
  {:selected    {:type 'boolean :reflects-attribute attr-selected}
   :disabled    {:type 'boolean :reflects-attribute attr-disabled}
   :value       {:type 'string  :reflects-attribute attr-value}
   :orientation {:type 'string  :reflects-attribute attr-orientation :default default-orientation :enum orientation-values}
   :size        {:type 'string  :reflects-attribute attr-size        :default default-size :enum size-values}
   :variant     {:type 'string  :reflects-attribute attr-variant     :default default-variant :enum variant-values}
   :label       {:type 'string  :reflects-attribute attr-label       :default ""}
   :controls    {:type 'string  :reflects-attribute attr-controls    :default ""}})

(def event-schema
  {event-tab-select {:cancelable false :detail {:value 'string}}})

(defn valid-enum [v allowed fallback]
  (if (contains? allowed v) v fallback))

(defn normalize-orientation [v]
  (valid-enum v orientation-values default-orientation))

(defn normalize-size [v]
  (valid-enum v size-values default-size))

(defn normalize-variant [v]
  (valid-enum v variant-values default-variant))

(defn normalize
  [{:keys [selected disabled orientation size variant label controls]}]

  (let [selected* (boolean selected)
        disabled* (boolean disabled)

        tabindex (cond
                   disabled* "-1"
                   selected* "0"
                   :else "-1")]

    {:selected selected*
     :disabled disabled*
     :orientation (normalize-orientation orientation)
     :size (normalize-size size)
     :variant (normalize-variant variant)
     :label label
     :controls controls
     :tabindex tabindex}))

(def method-api {})
