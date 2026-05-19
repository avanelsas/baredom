(ns baredom.components.x-menu-item.model)

(def tag-name "x-menu-item")

(def attr-value "value")
(def attr-disabled "disabled")
(def attr-variant "variant")
(def attr-type "type")

(def observed-attributes
  #js [attr-value attr-disabled attr-variant attr-type])

(def variant-values #{"" "danger"})
(def type-values #{"" "divider"})

(def event-item-select "x-menu-item-select")

(def property-api
  {:value    {:type 'string  :reflects-attribute attr-value}
   :disabled {:type 'boolean :reflects-attribute attr-disabled}
   :variant  {:type 'string  :reflects-attribute attr-variant}
   :type     {:type 'string  :reflects-attribute attr-type}})

(def event-schema
  {event-item-select {:detail {:value 'string}}})

(defn valid-enum [v allowed fallback]
  (if (contains? allowed v) v fallback))

(defn normalize-variant [v]
  (valid-enum (or v "") variant-values ""))

(defn normalize-type [v]
  (valid-enum (or v "") type-values ""))

(defn normalize
  [{:keys [value disabled variant type]}]
  {:value (or value "")
   :disabled (boolean disabled)
   :variant (normalize-variant variant)
   :type (normalize-type type)})

(def method-api {})
