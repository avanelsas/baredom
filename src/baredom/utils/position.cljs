(ns baredom.utils.position
  "Positions a panel next to an anchor, inside the viewport.")

(def placements
  "Each placement, with the placement it flips to and the axis the flip moves."
  {"bottom-start" {:opposite "top-start"    :axis :y}
   "bottom-end"   {:opposite "top-end"      :axis :y}
   "top-start"    {:opposite "bottom-start" :axis :y}
   "top-end"      {:opposite "bottom-end"   :axis :y}
   "right-start"  {:opposite "left-start"   :axis :x}
   "left-start"   {:opposite "right-start"  :axis :x}})

(def min-max-height
  "The smallest max-height a panel gets, in pixels."
  40)

(defn- fits? [edge size limit margin]
  (and (>= edge margin) (<= (+ edge size) (- limit margin))))

(defn- clamp [edge size limit margin]
  (-> edge (max margin) (min (- limit size margin))))

(defn- candidate-xy
  [placement offset anchor-rect panel-size]
  (let [{ax :x ay :y aw :width ah :height} anchor-rect
        {pw :width ph :height} panel-size]
    (case placement
      "bottom-start" {:x ax                 :y (+ ay ah offset)}
      "bottom-end"   {:x (- (+ ax aw) pw)  :y (+ ay ah offset)}
      "top-start"    {:x ax                 :y (- ay ph offset)}
      "top-end"      {:x (- (+ ax aw) pw)  :y (- ay ph offset)}
      "right-start"  {:x (+ ax aw offset)  :y ay}
      "left-start"   {:x (- ax pw offset)  :y ay}
      {:x ax :y (+ ay ah offset)})))

(defn- try-placement
  "The clamped position at `placement`, with `:fits` true when it fits on the axis its flip moves."
  [offset anchor-rect panel-size viewport-size margin placement]
  (let [{vw :width vh :height} viewport-size
        {pw :width ph :height} panel-size
        {cx :x cy :y} (candidate-xy placement offset anchor-rect panel-size)
        y (clamp cy ph vh margin)]
    {:x               (clamp cx pw vw margin)
     :y               y
     :final-placement placement
     :fits            (case (get-in placements [placement :axis])
                        :x (fits? cx pw vw margin)
                        (fits? cy ph vh margin))
     :max-height      (max min-max-height (- vh y margin))}))

(defn compute
  "Computes the viewport position of a panel at `placement` next to `anchor-rect`.
   Returns {:x :y :final-placement :max-height}, at the opposite placement when only that one fits."
  [placement offset anchor-rect panel-size viewport-size margin]
  (let [at       (partial try-placement offset anchor-rect panel-size viewport-size margin)
        primary  (at placement)
        flipped  (at (get-in placements [placement :opposite] placement))]
    (dissoc (if (or (:fits primary) (not (:fits flipped))) primary flipped)
            :fits)))
