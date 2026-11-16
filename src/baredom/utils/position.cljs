(ns baredom.utils.position
  "Positions a panel next to an anchor, inside the viewport.")

(def placements
  "Each placement: the edge rule on each axis, the placement it flips to and the axis the flip moves.
   `:after` and `:before` put the panel past the anchor, `:start` and `:end` align it with the anchor."
  {"bottom-start" {:x :start  :y :after  :opposite "top-start"    :axis :y}
   "bottom-end"   {:x :end    :y :after  :opposite "top-end"      :axis :y}
   "top-start"    {:x :start  :y :before :opposite "bottom-start" :axis :y}
   "top-end"      {:x :end    :y :before :opposite "bottom-end"   :axis :y}
   "right-start"  {:x :after  :y :start  :opposite "left-start"   :axis :x}
   "left-start"   {:x :before :y :start  :opposite "right-start"  :axis :x}})

(def default-placement "bottom-start")

(def max-height-floor
  "The smallest max-height a panel gets, in pixels."
  40)

(def ^:private extent {:x :width :y :height})

(defn- edge
  "The panel's leading edge on one axis."
  [rule anchor-start anchor-size panel-size offset]
  (case rule
    :start  anchor-start
    :end    (- (+ anchor-start anchor-size) panel-size)
    :after  (+ anchor-start anchor-size offset)
    :before (- anchor-start panel-size offset)))

(defn- candidate
  "The panel's top-left corner at `placement`, before clamping."
  [{:keys [anchor panel offset]} placement]
  (let [rules (placements placement)]
    {:x (edge (:x rules) (:x anchor) (:width anchor) (:width panel) offset)
     :y (edge (:y rules) (:y anchor) (:height anchor) (:height panel) offset)}))

(defn- fits? [edge size limit margin]
  (and (>= edge margin) (<= (+ edge size) (- limit margin))))

(defn- fits-on-flip-axis?
  "True when the panel at `placement` fits on the axis its flip moves."
  [{:keys [panel viewport margin] :as request} placement]
  (let [axis (get-in placements [placement :axis])
        size (extent axis)]
    (fits? (axis (candidate request placement)) (size panel) (size viewport) margin)))

(defn- clamp [edge size limit margin]
  (-> edge (max margin) (min (- limit size margin))))

(defn- place
  "The panel's position at `placement`, clamped inside the viewport."
  [{:keys [panel viewport margin] :as request} placement]
  (let [{cx :x cy :y} (candidate request placement)
        y (clamp cy (:height panel) (:height viewport) margin)]
    {:x               (clamp cx (:width panel) (:width viewport) margin)
     :y               y
     :final-placement placement
     :max-height      (max max-height-floor (- (:height viewport) y margin))}))

(defn compute
  "Computes the viewport position of a panel next to its anchor.
   Takes {:placement :offset :anchor :panel :viewport :margin} and returns
   {:x :y :final-placement :max-height}. Flips when only the opposite placement fits."
  [{:keys [placement] :as request}]
  (let [preferred (if (contains? placements placement) placement default-placement)
        flipped   (get-in placements [preferred :opposite])
        fits      (partial fits-on-flip-axis? request)]
    (place request (if (and (not (fits preferred)) (fits flipped)) flipped preferred))))
