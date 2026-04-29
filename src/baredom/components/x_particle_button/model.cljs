(ns baredom.components.x-particle-button.model
  (:require [baredom.utils.model :as mu]))

(def tag-name "x-particle-button")

;; --- Attribute constants ---
(def attr-disabled "disabled")
(def attr-loading "loading")
(def attr-pressed "pressed")
(def attr-type "type")
(def attr-variant "variant")
(def attr-size "size")
(def attr-label "label")
(def attr-mode "mode")
(def attr-particle-count "particle-count")
(def attr-intensity "intensity")
(def attr-particle-size "particle-size")
(def attr-reassemble-speed "reassemble-speed")

;; --- Event constants ---
(def event-press "press")
(def event-press-start "press-start")
(def event-press-end "press-end")
(def event-hover-start "hover-start")
(def event-hover-end "hover-end")
(def event-focus-visible "focus-visible")
(def event-burst "burst")
(def event-reform "reform")

;; --- Slot constants ---
(def slot-default "default")
(def slot-icon-start "icon-start")
(def slot-icon-end "icon-end")
(def slot-spinner "spinner")

;; --- Enums ---
(def default-type "button")
(def default-variant "primary")
(def default-size "md")
(def default-mode "dust")

(def allowed-types #{"button" "submit" "reset"})
(def allowed-variants #{"primary" "secondary" "tertiary" "ghost" "danger" "success" "warning"})
(def allowed-sizes #{"sm" "md" "lg"})
(def allowed-modes #{"dust" "spark" "ember" "burst" "disperse"})

;; --- Particle defaults ---
(def default-particle-count 40)
(def default-intensity 50)
(def default-particle-size 3)
(def default-reassemble-speed 500)

;; --- Particle type constants ---
(def type-dust "d")
(def type-fragment "f")
(def type-streak "s")

;; --- Observed attributes ---
(def observed-attributes
  #js [attr-disabled
       attr-loading
       attr-pressed
       attr-type
       attr-variant
       attr-size
       attr-label
       attr-mode
       attr-particle-count
       attr-intensity
       attr-particle-size
       attr-reassemble-speed])

;; --- Property API ---
(def property-api
  {:disabled {:type 'boolean
              :reflects-attribute attr-disabled}
   :loading {:type 'boolean
             :reflects-attribute attr-loading}
   :pressed {:type 'boolean
             :reflects-attribute attr-pressed}})

;; --- Event schema ---
(def event-schema
  {event-press {:detail {:source 'string}}
   event-press-start {:detail {:source 'string}}
   event-press-end {:detail {:source 'string}}
   event-hover-start {:detail {}}
   event-hover-end {:detail {}}
   event-focus-visible {:detail {}}
   event-burst {:detail {:mode 'string :press-x 'number :press-y 'number}}
   event-reform {:detail {:mode 'string :duration 'number}}})

;; --- Normalization ---
(defn normalize-enum
  [value allowed default-value]
  (if (and (string? value) (contains? allowed value))
    value
    default-value))

(defn normalize-type [value]
  (normalize-enum value allowed-types default-type))

(defn normalize-variant [value]
  (normalize-enum value allowed-variants default-variant))

(defn normalize-size [value]
  (normalize-enum value allowed-sizes default-size))

(defn normalize-mode [value]
  (normalize-enum value allowed-modes default-mode))

(defn clamp-int
  [raw-str default-val min-val max-val]
  (if (nil? raw-str)
    default-val
    (let [n (js/parseInt raw-str 10)]
      (if (js/isNaN n)
        default-val
        (js/Math.min max-val (js/Math.max min-val n))))))

(defn normalize-particle-count [v]
  (clamp-int v default-particle-count 10 100))

(defn normalize-intensity [v]
  (clamp-int v default-intensity 1 100))

(defn normalize-particle-size [v]
  (clamp-int v default-particle-size 1 8))

(defn normalize-reassemble-speed [v]
  (clamp-int v default-reassemble-speed 100 2000))

;; --- Public state ---
(defn public-state
  [{:keys [disabled loading pressed type variant size label mode
           particle-count intensity particle-size reassemble-speed]}]
  {:disabled (boolean disabled)
   :loading (boolean loading)
   :pressed (boolean pressed)
   :type (normalize-type type)
   :variant (normalize-variant variant)
   :size (normalize-size size)
   :label (when (string? label) label)
   :mode (normalize-mode mode)
   :particle-count (normalize-particle-count particle-count)
   :intensity (normalize-intensity intensity)
   :particle-size (normalize-particle-size particle-size)
   :reassemble-speed (normalize-reassemble-speed reassemble-speed)})

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

;; --- Particle type selection ---
(defn pick-particle-type
  "Returns type-dust, type-fragment, or type-streak based on mode ratios."
  [rand-val mode-cfg]
  (let [dust-r (:dust-ratio mode-cfg)
        frag-r (+ dust-r (:fragment-ratio mode-cfg))]
    (cond
      (< rand-val dust-r) type-dust
      (< rand-val frag-r) type-fragment
      :else type-streak)))

;; --- Particle size ranges per type and mode ---
(defn particle-size-range
  "Returns [min-size max-size] for a particle type given base particle-size and mode."
  [ptype base-size mode]
  (case mode
    "ember" (case ptype
              "d" [(* base-size 0.4) (* base-size 0.8)]
              "f" [(* base-size 0.6) (* base-size 1.2)]
              "s" [(* base-size 0.3) (* base-size 0.7)]
              [(* base-size 0.5) (* base-size 1.0)])
    "spark" (case ptype
              "d" [(* base-size 0.2) (* base-size 0.5)]
              "f" [(* base-size 0.4) (* base-size 0.8)]
              "s" [(* base-size 0.2) (* base-size 0.4)]
              [(* base-size 0.3) (* base-size 0.6)])
    "burst" (case ptype
              "d" [(* base-size 0.3) (* base-size 0.8)]
              "f" [(* base-size 0.6) (* base-size 1.3)]
              "s" [(* base-size 0.3) (* base-size 0.7)]
              [(* base-size 0.5) (* base-size 1.0)])
    ;; dust, disperse, fallback
    (case ptype
      "d" [(* base-size 0.3) (* base-size 0.7)]
      "f" [(* base-size 0.7) (* base-size 1.3)]
      "s" [(* base-size 0.3) (* base-size 0.6)]
      [(* base-size 0.5) base-size])))

(defn particle-lifetime-range
  "Returns [min-ms max-ms] for a particle type and mode."
  [ptype mode]
  (case mode
    "ember" (case ptype
              "d" [600 1200]
              "f" [800 1600]
              "s" [1200 2400]
              [700 1400])
    "spark" (case ptype
              "d" [150 350]
              "f" [250 500]
              "s" [400 800]
              [200 400])
    "burst" (case ptype
              "d" [200 500]
              "f" [400 800]
              "s" [600 1200]
              [300 600])
    ;; dust, disperse, fallback
    (case ptype
      "d" [300 600]
      "f" [500 1000]
      "s" [800 1500]
      [400 800])))

(defn particle-opacity-range
  "Returns [min max] opacity for a particle type and mode."
  [ptype mode]
  (case mode
    "ember" (case ptype
              "d" [0.4 0.8]
              "f" [0.5 0.9]
              "s" [0.6 1.0]
              [0.5 0.9])
    "spark" (case ptype
              "d" [0.6 1.0]
              "f" [0.7 1.0]
              "s" [0.8 1.0]
              [0.7 1.0])
    ;; dust, burst, disperse, fallback
    (case ptype
      "d" [0.3 0.7]
      "f" [0.5 0.9]
      "s" [0.7 1.0]
      [0.4 0.8])))

;; --- Edge point geometry ---
(defn random-edge-point
  "Given button width w and height h and a random value [0,1),
   returns [x y nx ny] where (x,y) is on the perimeter and
   (nx,ny) is the outward normal."
  [w h rand-val]
  (let [perimeter (* 2 (+ w h))
        pos (* rand-val perimeter)]
    (cond
      ;; Top edge
      (< pos w)
      [pos 0.0 0.0 -1.0]

      ;; Right edge
      (< pos (+ w h))
      [w (- pos w) 1.0 0.0]

      ;; Bottom edge
      (< pos (+ w h w))
      [(- w (- pos (+ w h))) h 0.0 1.0]

      ;; Left edge
      :else
      [0.0 (- h (- pos (+ w h w))) -1.0 0.0])))

;; --- Press burst velocity ---
(defn press-burst-velocity
  "Compute [vx vy] for a particle emitted from (px,py) with
   base-speed, spread half-angle, and random values [0,1)."
  [px py bw bh base-speed spread rand-angle rand-speed]
  (let [;; Direction from press point toward nearest edge
        cx (/ bw 2.0)
        cy (/ bh 2.0)
        dx (- px cx)
        dy (- py cy)
        ;; Base angle: away from center (from center toward press point, reversed)
        base-angle (js/Math.atan2 (- dy) (- dx))
        ;; Add random spread
        angle (+ base-angle (* (- rand-angle 0.5) 2.0 spread))
        speed (* base-speed (+ 0.5 (* rand-speed 0.5)))]
    [(* speed (js/Math.cos angle))
     (* speed (js/Math.sin angle))]))

;; --- Mode physics configurations ---
(defn mode-physics
  "Returns physics configuration for a given mode."
  [mode]
  (case mode
    "dust"
    {:hover-emit-rate 0.6
     :hover-source :edge-only
     :hover-drift-vy -0.4
     :hover-drift-vx-range 0.2
     :press-burst-count-min 6
     :press-burst-count-max 10
     :press-base-speed 1.5
     :press-spread (/ js/Math.PI 3)
     :dust-ratio 0.90
     :fragment-ratio 0.08
     :streak-ratio 0.02
     :gravity -0.03
     :friction 0.97
     :reform-spring 0.0
     :surface-dissolve false
     :glow-render false
     :additive-core false}

    "spark"
    {:hover-emit-rate 2.5
     :hover-source :edge-and-surface
     :hover-drift-vy -1.2
     :hover-drift-vx-range 1.0
     :press-burst-count-min 20
     :press-burst-count-max 35
     :press-base-speed 6.0
     :press-spread (/ js/Math.PI 4)
     :dust-ratio 0.40
     :fragment-ratio 0.25
     :streak-ratio 0.35
     :gravity 0.0
     :friction 0.92
     :reform-spring 0.0
     :surface-dissolve false
     :glow-render false
     :additive-core true}

    "ember"
    {:hover-emit-rate 0.8
     :hover-source :edge-only
     :hover-drift-vy -0.15
     :hover-drift-vx-range 0.1
     :press-burst-count-min 8
     :press-burst-count-max 14
     :press-base-speed 0.8
     :press-spread (* js/Math.PI 0.6)
     :dust-ratio 0.50
     :fragment-ratio 0.35
     :streak-ratio 0.15
     :gravity -0.008
     :friction 0.99
     :reform-spring 0.0
     :surface-dissolve false
     :glow-render true
     :additive-core false}

    "burst"
    {:hover-emit-rate 0.4
     :hover-source :edge-only
     :hover-drift-vy -0.3
     :hover-drift-vx-range 0.2
     :press-burst-count-min 30
     :press-burst-count-max 50
     :press-base-speed 7.0
     :press-spread (/ js/Math.PI 2)
     :dust-ratio 0.45
     :fragment-ratio 0.35
     :streak-ratio 0.20
     :gravity 0.04
     :friction 0.94
     :reform-spring 0.0
     :surface-dissolve false
     :glow-render false
     :additive-core false}

    "disperse"
    {:hover-emit-rate 1.2
     :hover-source :edge-and-surface
     :hover-drift-vy -0.3
     :hover-drift-vx-range 0.3
     :press-burst-count-min 20
     :press-burst-count-max 30
     :press-base-speed 3.0
     :press-spread js/Math.PI
     :dust-ratio 0.30
     :fragment-ratio 0.55
     :streak-ratio 0.15
     :gravity 0.0
     :friction 0.96
     :reform-spring 0.08
     :surface-dissolve true
     :glow-render false
     :additive-core false}

    ;; fallback to dust
    (mode-physics "dust")))

;; --- Variant visual config (colors only, no particle behavior) ---
(defn variant-visuals
  "Returns visual-only configuration for a variant.
   Particle behavior is now controlled by mode, not variant."
  [variant]
  (case variant
    "danger"  {:glow-intensity 1.5 :shadow-depth 1.2}
    "success" {:glow-intensity 1.2 :shadow-depth 1.0}
    "warning" {:glow-intensity 1.3 :shadow-depth 1.1}
    {:glow-intensity 1.0 :shadow-depth 1.0}))

(def method-api nil)
