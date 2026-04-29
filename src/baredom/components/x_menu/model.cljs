(ns baredom.components.x-menu.model)

(def tag-name "x-menu")

(def attr-open "open")
(def attr-placement "placement")
(def attr-label "label")

(def observed-attributes
  #js [attr-open attr-placement attr-label])

(def placement-values #{"bottom-start" "bottom-end" "top-start" "top-end"})

(def event-open "x-menu-open")
(def event-close "x-menu-close")
(def event-select "x-menu-select")

(def event-item-select "x-menu-item-select")

(def property-api
  {:open {:type 'boolean}
   :placement {:type 'string}
   :label {:type 'string}})

(def event-schema
  {event-open {:detail {}}
   event-close {:detail {}}
   event-select {:detail {:value 'string}}})

(defn valid-enum [v allowed fallback]
  (if (contains? allowed v) v fallback))

(defn normalize-placement [v]
  (valid-enum (or v "") placement-values "bottom-start"))

(defn derive-state
  [{:keys [open placement label]}]
  {:open (boolean open)
   :placement (normalize-placement placement)
   :label (or label "")})

(def method-api nil)
