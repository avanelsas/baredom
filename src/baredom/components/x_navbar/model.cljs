(ns baredom.components.x-navbar.model
  (:require [baredom.utils.model :as mu]))

(def tag-name "x-navbar")

(def attr-label "label")
(def attr-orientation "orientation")
(def attr-variant "variant")
(def attr-sticky "sticky")
(def attr-elevated "elevated")
(def attr-breakpoint "breakpoint")
(def attr-alignment "alignment")

(def event-focus-visible "focus-visible")
(def event-navigate "navigate")
(def event-brand-activate "brand-activate")

(def slot-brand "brand")
(def slot-actions "actions")
(def slot-toggle "toggle")
(def slot-start "start")
(def slot-end "end")

(def default-orientation "horizontal")
(def default-variant "default")
(def default-breakpoint "md")
(def default-alignment "space-between")

(def allowed-orientations #{"horizontal" "vertical"})
(def allowed-variants #{"default" "subtle" "inverted" "transparent"})
(def allowed-breakpoints #{"sm" "md" "lg" "xl"})
(def allowed-alignments #{"start" "center" "space-between"})

(def observed-attributes
  #js [attr-label
       attr-orientation
       attr-variant
       attr-sticky
       attr-elevated
       attr-breakpoint
       attr-alignment])

(def property-api
  {:sticky      {:type 'boolean :reflects-attribute attr-sticky}
   :elevated    {:type 'boolean :reflects-attribute attr-elevated}
   :label       {:type 'string  :reflects-attribute attr-label}
   :variant     {:type 'string  :reflects-attribute attr-variant}
   :orientation {:type 'string  :reflects-attribute attr-orientation}
   :alignment   {:type 'string  :reflects-attribute attr-alignment}
   :breakpoint  {:type 'string  :reflects-attribute attr-breakpoint}})

(def event-schema
  {event-focus-visible {:detail {}}
   event-navigate {:detail {:href 'string
                            :source 'string}}
   event-brand-activate {:detail {:source 'string}}})

(def slot-names
  {:brand slot-brand
   :actions slot-actions
   :toggle slot-toggle
   :start slot-start
   :end slot-end
   :default "default"})

(defn normalize-enum
  [value allowed default-value]
  (if (and (string? value) (contains? allowed value))
    value
    default-value))

(defn normalize-orientation
  [value]
  (normalize-enum value allowed-orientations default-orientation))

(defn normalize-variant
  [value]
  (normalize-enum value allowed-variants default-variant))

(defn normalize-breakpoint
  [value]
  (normalize-enum value allowed-breakpoints default-breakpoint))

(defn normalize-alignment
  [value]
  (normalize-enum value allowed-alignments default-alignment))

(defn public-state
  [{:keys [label orientation variant sticky elevated breakpoint alignment]}]
  {:label (when (string? label) label)
   :orientation (normalize-orientation orientation)
   :variant (normalize-variant variant)
   :sticky (boolean sticky)
   :elevated (boolean elevated)
   :breakpoint (normalize-breakpoint breakpoint)
   :alignment (normalize-alignment alignment)})

(defn landmark-label
  [{:keys [label]}]
  (when (mu/non-empty-string? label)
    label))

(def method-api nil)
