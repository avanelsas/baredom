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

(def event-change-request "value-change-request")
(def event-value-change   "value-change")

(def default-orientation "horizontal")
(def default-activation "auto")

(def property-api
  {:value       {:type 'string  :reflects-attribute attr-value}
   :orientation {:type 'string  :reflects-attribute attr-orientation :default default-orientation :enum orientation-values}
   :activation  {:type 'string  :reflects-attribute attr-activation  :default default-activation :enum activation-values}
   :label       {:type 'string  :reflects-attribute attr-label       :default ""}
   :loop        {:type 'boolean :reflects-attribute attr-loop}})

(def event-schema
  {event-change-request {:cancelable true
                         :requests   {attr-value :value}
                         :detail     {:value 'string :previousValue 'string}}
   event-value-change   {:cancelable false :detail {:value 'string}}})

(defn valid-enum [v allowed fallback]
  (if (contains? allowed v) v fallback))

(defn normalize-orientation [v]
  (valid-enum v orientation-values default-orientation))

(defn normalize-activation [v]
  (valid-enum v activation-values default-activation))

(defn normalize
  [{:keys [value orientation activation label loop]}]
  {:value value
   :orientation (normalize-orientation orientation)
   :activation (normalize-activation activation)
   :label label
   :loop (boolean loop)})

(def method-api {})
