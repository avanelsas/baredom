(ns baredom.components.x-button.model
  (:require [baredom.utils.model :as mu]))

(def tag-name "x-button")

(def attr-disabled "disabled")
(def attr-loading "loading")
(def attr-pressed "pressed")
(def attr-type "type")
(def attr-variant "variant")
(def attr-size "size")
(def attr-label "label")

(def event-press "press")
(def event-press-start "press-start")
(def event-press-end "press-end")
(def event-hover-start "hover-start")
(def event-hover-end "hover-end")
(def event-focus-visible "focus-visible")

(def slot-default "default")
(def slot-icon-start "icon-start")
(def slot-icon-end "icon-end")
(def slot-spinner "spinner")

(def default-type "button")
(def default-variant "primary")
(def default-size "md")

(def allowed-types #{"button" "submit" "reset"})
(def allowed-variants #{"primary" "secondary" "tertiary" "ghost" "danger"})
(def allowed-sizes #{"sm" "md" "lg"})

(def observed-attributes
  #js [attr-disabled
       attr-loading
       attr-pressed
       attr-type
       attr-variant
       attr-size
       attr-label])

(def property-api
  {:disabled {:type 'boolean
              :reflects-attribute attr-disabled}
   :loading {:type 'boolean
             :reflects-attribute attr-loading}
   :pressed {:type 'boolean
             :reflects-attribute attr-pressed}})

(def event-schema
  {event-press {:detail {:source 'string}}
   event-press-start {:detail {:source 'string}}
   event-press-end {:detail {:source 'string}}
   event-hover-start {:detail {}}
   event-hover-end {:detail {}}
   event-focus-visible {:detail {}}})

(defn normalize-enum
  [value allowed default-value]
  (if (and (string? value) (contains? allowed value))
    value
    default-value))

(defn normalize-type
  [value]
  (normalize-enum value allowed-types default-type))

(defn normalize-variant
  [value]
  (normalize-enum value allowed-variants default-variant))

(defn normalize-size
  [value]
  (normalize-enum value allowed-sizes default-size))

(defn public-state
  [{:keys [disabled loading pressed type variant size label]}]
  {:disabled (boolean disabled)
   :loading (boolean loading)
   :pressed (boolean pressed)
   :type (normalize-type type)
   :variant (normalize-variant variant)
   :size (normalize-size size)
   :label (when (string? label) label)})

(defn interactive?
  [{:keys [disabled loading]}]
  (not (or disabled loading)))

(defn aria-busy
  [{:keys [loading]}]
  (when loading "true"))

(defn aria-label
  [{:keys [label has-default-text?]}]
  (when (and (not has-default-text?)
             (mu/non-empty-string? label))
    label))
