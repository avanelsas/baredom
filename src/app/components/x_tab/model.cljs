(ns app.components.x-tab.model)

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

(def property-api
  {:selected {:type 'boolean}
   :disabled {:type 'boolean}
   :value {:type 'string}})

(def event-schema
  {event-tab-select {:detail {:value 'string}}})

(defn valid-enum [v allowed fallback]
  (if (contains? allowed v) v fallback))

(defn normalize-orientation [v]
  (valid-enum v orientation-values "horizontal"))

(defn normalize-size [v]
  (valid-enum v size-values "md"))

(defn normalize-variant [v]
  (valid-enum v variant-values "default"))

(defn derive-state
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
