(ns baredom.components.x-popover.model)

(def tag-name "x-popover")

;; Attribute name constants
(def attr-open        "open")
(def attr-placement   "placement")
(def attr-heading     "heading")
(def attr-close-label "close-label")
(def attr-no-close    "no-close")
(def attr-disabled    "disabled")
(def attr-portal      "portal")

;; Event name constants
(def event-toggle "x-popover-toggle")
(def event-change "x-popover-change")

;; Placement constants
(def allowed-placements #{"bottom-start" "bottom-end" "top-start" "top-end"})
(def default-placement "bottom-start")
(def default-close-label "Close")

(def observed-attributes
  #js [attr-open attr-placement attr-heading attr-close-label attr-no-close attr-disabled attr-portal])

(def property-api
  {:open       {:type 'boolean :reflects-attribute attr-open}
   :placement  {:type 'string  :reflects-attribute attr-placement}
   :heading    {:type 'string  :reflects-attribute attr-heading}
   :closeLabel {:type 'string  :reflects-attribute attr-close-label}
   :noClose    {:type 'boolean :reflects-attribute attr-no-close}
   :disabled   {:type 'boolean :reflects-attribute attr-disabled}
   :portal     {:type 'boolean :reflects-attribute attr-portal}})

(def event-schema
  {event-toggle {:cancelable true
                 :detail     {:open   'boolean
                              :source 'string}}
   event-change {:cancelable false
                 :detail     {:open 'boolean}}})

(defn normalize-placement
  "Normalizes raw placement string. Falls back to default-placement if invalid or nil."
  [s]
  (if (contains? allowed-placements s) s default-placement))

(defn normalize
  "Derives a complete view-model map from raw attribute presence/values."
  [{:keys [open-present? disabled-present? no-close-present?
           portal-present? heading-raw placement-raw close-label-raw]}]
  {:open?       (boolean open-present?)
   :disabled?   (boolean disabled-present?)
   :no-close?   (boolean no-close-present?)
   :portal?     (boolean portal-present?)
   :heading     (or heading-raw "")
   :close-label (or close-label-raw default-close-label)
   :placement   (normalize-placement placement-raw)})

(defn toggle-detail
  "Produces the CustomEvent detail object for toggle/change events."
  [open? source]
  #js {:open open? :source source})

;; ---------------------------------------------------------------------------
;; Portal positioning (viewport-based)
;; ---------------------------------------------------------------------------
(def default-offset 4)
(def default-margin 8)

(defn- opposite-placement [placement]
  (case placement
    "bottom-start" "top-start"
    "bottom-end"   "top-end"
    "top-start"    "bottom-start"
    "top-end"      "bottom-end"
    placement))

(defn- candidate-xy
  [placement offset anchor-rect panel-size]
  (let [{ax :x ay :y aw :width ah :height} anchor-rect
        {pw :width ph :height} panel-size]
    (case placement
      "bottom-start" {:x ax                 :y (+ ay ah offset)}
      "bottom-end"   {:x (- (+ ax aw) pw)  :y (+ ay ah offset)}
      "top-start"    {:x ax                 :y (- ay ph offset)}
      "top-end"      {:x (- (+ ax aw) pw)  :y (- ay ph offset)}
      {:x ax :y (+ ay ah offset)})))

(defn- fits? [edge size limit margin]
  (and (>= edge margin) (<= (+ edge size) (- limit margin))))

(defn compute-position
  "Computes viewport-relative position for the popover panel.
   Returns {:x :y :final-placement :max-height}."
  [placement offset anchor-rect panel-size viewport-size margin]
  (let [{vw :width vh :height} viewport-size
        {pw :width ph :height} panel-size
        try-placement
        (fn [p]
          (let [{cx :x cy :y} (candidate-xy p offset anchor-rect panel-size)
                x (-> cx (max margin) (min (- vw pw margin)))
                y (-> cy (max margin) (min (- vh ph margin)))
                max-h (- vh y margin)]
            {:x x :y y
             :final-placement p
             :fits (and (fits? cx pw vw margin) (fits? cy ph vh margin))
             :max-height (max 40 max-h)}))
        primary (try-placement placement)
        flipped (try-placement (opposite-placement placement))]
    (if (or (:fits primary) (not (:fits flipped)))
      primary
      flipped)))

(def method-api nil)
