(ns app.components.x-sidebar.model
  (:require [clojure.string :as str]))

(def tag-name "x-sidebar")

(def attr-open "open")
(def attr-collapsed "collapsed")
(def attr-placement "placement")
(def attr-variant "variant")
(def attr-breakpoint "breakpoint")
(def attr-label "label")

(def event-toggle "toggle")
(def event-dismiss "dismiss")

(def part-backdrop "backdrop")
(def part-sidebar "sidebar")
(def part-panel "panel")

(def placement-left "left")
(def placement-right "right")

(def variant-docked "docked")
(def variant-overlay "overlay")
(def variant-modal "modal")

(def reason-escape "escape")
(def reason-backdrop "backdrop")

(def observed-attributes
  #js [attr-open
       attr-collapsed
       attr-placement
       attr-variant
       attr-breakpoint
       attr-label])

(def allowed-placement
  #{placement-left placement-right})

(def allowed-variant
  #{variant-docked variant-overlay variant-modal})

(def allowed-dismiss-reason
  #{reason-escape reason-backdrop})

(def defaults
  {:placement placement-left
   :variant variant-docked
   :breakpoint 768
   :label "Sidebar"
   :open false
   :collapsed false})

(def property-api
  {:open {:type 'boolean
          :reflects-attribute attr-open}
   :collapsed {:type 'boolean
               :reflects-attribute attr-collapsed}})

(def event-schema
  {event-toggle {:detail {:open 'boolean}}
   event-dismiss {:detail {:reason 'string}}})


(defn trim-or-nil
  [value]
  (let [s (when (some? value) (str/trim value))]
    (when (seq s) s)))

(defn parse-number
  [value fallback]
  (let [raw (some-> value str str/trim)]
    (if (seq raw)
      (let [n (js/Number raw)]
        (if (js/Number.isFinite n)
          n
          fallback))
      fallback)))

(defn normalize-placement
  [value]
  (if (contains? allowed-placement value)
    value
    (:placement defaults)))

(defn normalize-variant
  [value]
  (if (contains? allowed-variant value)
    value
    (:variant defaults)))

(defn normalize-breakpoint
  [value]
  (parse-number value (:breakpoint defaults)))

(defn normalize-label
  [value]
  (or (trim-or-nil value) (:label defaults)))

(defn compute-open
  [{:keys [open-attr]}]
  (boolean open-attr))

(defn compute-state
  [{:keys [open-attr
           collapsed-attr
           placement-attr
           variant-attr
           breakpoint-attr
           label-attr
           viewport-width
           prefers-reduced-motion]}]
  (let [placement (normalize-placement placement-attr)
        declared-variant (normalize-variant variant-attr)
        breakpoint (normalize-breakpoint breakpoint-attr)
        label (normalize-label label-attr)
        open (compute-open {:open-attr open-attr})
        collapsed-requested (boolean collapsed-attr)
        effective-variant (if (< viewport-width breakpoint)
                            variant-modal
                            declared-variant)
        is-modal (= effective-variant variant-modal)
        is-overlay (= effective-variant variant-overlay)
        is-docked (= effective-variant variant-docked)
        collapsed-applied (and is-docked collapsed-requested)
        show-backdrop (and open (or is-modal is-overlay))]
    {:placement placement
     :declared-variant declared-variant
     :effective-variant effective-variant
     :breakpoint breakpoint
     :label label
     :open open
     :collapsed-requested collapsed-requested
     :collapsed-applied collapsed-applied
     :is-modal is-modal
     :is-overlay is-overlay
     :is-docked is-docked
     :show-backdrop show-backdrop
     :aria-hidden (not open)
     :reduced-motion (boolean prefers-reduced-motion)}))

(defn toggle-event-detail
  [open]
  {:open (boolean open)})

(defn dismiss-event-detail
  [reason]
  {:reason reason})

(defn dismiss-reason?
  [reason]
  (contains? allowed-dismiss-reason reason))

(defn toggle-should-fire?
  [prev-state next-state]
  (not= (:open prev-state) (:open next-state)))

(defn entered-modal-open?
  [prev-state next-state]
  (and (:is-modal next-state)
       (:open next-state)
       (not (and (:is-modal prev-state) (:open prev-state)))))

(defn left-modal-open?
  [prev-state next-state]
  (and (:is-modal prev-state)
       (:open prev-state)
       (not (and (:is-modal next-state) (:open next-state)))))
