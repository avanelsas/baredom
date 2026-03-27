(ns baredom.components.x-tabs.model)

(def tag-name "x-tabs")

(def attr-value "value")
(def attr-orientation "orientation")
(def attr-activation "activation")
(def attr-label "label")
(def attr-loop "loop")

(def observed-attributes
  #js [attr-value
       attr-orientation
       attr-activation
       attr-label
       attr-loop])

(def orientation-values #{"horizontal" "vertical"})
(def activation-values #{"auto" "manual"})

(def event-value-change "value-change")

(def property-api
  {:value {:type 'string}})

(def event-schema
  {event-value-change {:detail {:value 'string}}})

(defn valid-enum [v allowed fallback]
  (if (contains? allowed v) v fallback))

(defn normalize-orientation [v]
  (valid-enum v orientation-values "horizontal"))

(defn normalize-activation [v]
  (valid-enum v activation-values "auto"))

(defn derive-state
  [{:keys [value orientation activation label loop]}]
  {:value value
   :orientation (normalize-orientation orientation)
   :activation (normalize-activation activation)
   :label label
   :loop (boolean loop)})
