(ns baredom.components.x-liquid-dock.model
  (:require [baredom.utils.model :as mu]))

;; ── Tag name ────────────────────────────────────────────────────────────────
(def tag-name "x-liquid-dock")

;; ── Attribute names ─────────────────────────────────────────────────────────
(def attr-position        "position")
(def attr-gap             "gap")
(def attr-blur            "blur")
(def attr-threshold       "threshold")
(def attr-ripple-scale    "ripple-scale")
(def attr-ripple-speed    "ripple-speed")
(def attr-color           "color")
(def attr-magnet-radius   "magnet-radius")
(def attr-magnet-strength "magnet-strength")
(def attr-disabled        "disabled")
(def attr-bob-intensity   "bob-intensity")

(def observed-attributes
  #js [attr-position attr-gap attr-blur attr-threshold
       attr-ripple-scale attr-ripple-speed attr-color
       attr-magnet-radius attr-magnet-strength attr-disabled
       attr-bob-intensity])

;; ── CSS custom property names ───────────────────────────────────────────────
(def css-bg               "--x-liquid-dock-bg")
(def css-color            "--x-liquid-dock-color")
(def css-border           "--x-liquid-dock-border")
(def css-shadow           "--x-liquid-dock-shadow")
(def css-z-index          "--x-liquid-dock-z-index")
(def css-item-size        "--x-liquid-dock-item-size")
(def css-item-active-scale "--x-liquid-dock-item-active-scale")
(def css-radius           "--x-liquid-dock-radius")
(def css-padding          "--x-liquid-dock-padding")
(def css-gap              "--x-liquid-dock-gap")
(def css-glow-color       "--x-liquid-dock-glow-color")

;; ── Event names ─────────────────────────────────────────────────────────────
(def event-select "x-liquid-dock-select")

(def event-schema
  {event-select {:detail {:index  'number
                          :item   'Element
                          :source 'string}
                 :bubbles    true
                 :composed   true
                 :cancelable true}})

;; ── Property API ────────────────────────────────────────────────────────────
(def property-api
  {:position       {:type 'string}
   :gap            {:type 'number}
   :blur           {:type 'number}
   :threshold      {:type 'string}
   :rippleScale    {:type 'number}
   :rippleSpeed    {:type 'number}
   :color          {:type 'string}
   :magnetRadius   {:type 'number}
   :magnetStrength {:type 'number}
   :bobIntensity   {:type 'number}
   :disabled       {:type 'boolean}})

;; ── Defaults ────────────────────────────────────────────────────────────────
(def ^:private default-position        "bottom")
(def ^:private default-gap             8)
(def ^:private default-blur            10)
(def ^:private default-threshold       "1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 18 -7")
(def ^:private default-ripple-scale    8)
(def ^:private default-ripple-speed    0.03)
(def ^:private default-magnet-radius   150)
(def ^:private default-magnet-strength 0.6)
(def ^:private default-bob-intensity   1.0)

;; ── Allowed values ──────────────────────────────────────────────────────────
(def ^:private allowed-positions #{"bottom" "top" "left" "right"})

;; ── Parse functions ─────────────────────────────────────────────────────────

(defn parse-position
  "Parse position attribute to one of bottom/top/left/right. Default bottom."
  [s]
  (if (string? s)
    (let [v (.toLowerCase (.trim s))]
      (if (contains? allowed-positions v) v default-position))
    default-position))

(defn parse-float-clamped
  "Parse a string to a float clamped in [lo, hi]. Returns default-val on failure."
  [s default-val lo hi]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (js/isNaN n)
        default-val
        (js/Math.min hi (js/Math.max lo n))))
    default-val))

(defn parse-gap [s]
  (parse-float-clamped s default-gap 0 40))

(defn parse-blur [s]
  (parse-float-clamped s default-blur 2 30))

(defn parse-threshold
  "Parse threshold to a color-matrix values string."
  [s]
  (if (and (string? s) (pos? (.-length (.trim s))))
    (.trim s)
    default-threshold))

(defn parse-ripple-scale [s]
  (parse-float-clamped s default-ripple-scale 0 40))

(defn parse-ripple-speed [s]
  (parse-float-clamped s default-ripple-speed 0.005 0.15))

(defn parse-color
  "Parse color attribute. Any non-empty string passes through.
  Returns nil when no attribute is set so the CSS custom property
  (which may reference a theme token) takes effect."
  [s]
  (when (and (string? s) (pos? (.-length (.trim s))))
    (.trim s)))

(defn parse-magnet-radius [s]
  (parse-float-clamped s default-magnet-radius 40 400))

(defn parse-magnet-strength [s]
  (parse-float-clamped s default-magnet-strength 0.0 2.0))

(defn parse-bob-intensity [s]
  (parse-float-clamped s default-bob-intensity 0.0 2.0))

;; ── Normalize ───────────────────────────────────────────────────────────────

(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :position-raw        string | nil
    :gap-raw             string | nil
    :blur-raw            string | nil
    :threshold-raw       string | nil
    :ripple-scale-raw    string | nil
    :ripple-speed-raw    string | nil
    :color-raw           string | nil
    :magnet-radius-raw   string | nil
    :magnet-strength-raw string | nil
    :bob-intensity-raw   string | nil
    :disabled-attr       string | nil  (presence-based boolean)

  Output keys:
    :position        string
    :gap             number
    :blur            number
    :threshold       string
    :ripple-scale    number
    :ripple-speed    number
    :color           string
    :magnet-radius   number
    :magnet-strength number
    :bob-intensity   number
    :disabled?       boolean
    :vertical?       boolean"
  [{:keys [position-raw gap-raw blur-raw threshold-raw
           ripple-scale-raw ripple-speed-raw color-raw
           magnet-radius-raw magnet-strength-raw bob-intensity-raw
           disabled-attr]}]
  (let [pos (parse-position position-raw)]
    {:position        pos
     :gap             (parse-gap gap-raw)
     :blur            (parse-blur blur-raw)
     :threshold       (parse-threshold threshold-raw)
     :ripple-scale    (parse-ripple-scale ripple-scale-raw)
     :ripple-speed    (parse-ripple-speed ripple-speed-raw)
     :color           (parse-color color-raw)
     :magnet-radius   (parse-magnet-radius magnet-radius-raw)
     :magnet-strength (parse-magnet-strength magnet-strength-raw)
     :bob-intensity   (parse-bob-intensity bob-intensity-raw)
     :disabled?       (mu/parse-bool-present disabled-attr)
     :vertical?       (or (= pos "left") (= pos "right"))}))

;; ── Derived computations ────────────────────────────────────────────────────

(defn lerp
  "Linear interpolation: moves current toward target by speed fraction."
  [current target speed]
  (+ current (* (- target current) speed)))

(defn compute-item-influence
  "Given an item center (ix, iy) and mouse position (mx, my), compute the
   influence factor [0..1] based on distance and magnet-radius. Returns a map
   with :influence, :scale, :tx, :ty.

   - influence: 0 when dist >= magnet-radius, 1 at dist = 0
   - scale: 1.0 + influence^2 * magnet-strength * active-scale-boost
   - tx/ty: directional offset toward the cursor"
  [ix iy mx my magnet-radius magnet-strength active-scale]
  (let [dx         (- mx ix)
        dy         (- my iy)
        dist       (js/Math.sqrt (+ (* dx dx) (* dy dy)))
        influence  (js/Math.max 0.0 (- 1.0 (/ dist magnet-radius)))
        inf-sq     (* influence influence)
        scale      (+ 1.0 (* inf-sq (- active-scale 1.0)))
        lift       (* inf-sq magnet-strength 20.0)
        ;; Normalize direction for translate (avoid division by zero)
        inv-dist   (if (> dist 0.001) (/ 1.0 dist) 0.0)
        tx         (* dx inv-dist lift 0.3)
        ty         (* dy inv-dist lift 0.3)]
    {:influence influence
     :scale     scale
     :tx        tx
     :ty        ty}))

;; ── Magnetic Buoy computations ──────────────────────────────────────────────

(defn compute-phantom-offset
  "Compute phantom blob offset from rest position based on influence and dock
   position. The phantom rises away from the dock edge (up for bottom, down for
   top, etc.) and scales up to create the viscous neck effect.

   Returns {:dx :dy :scale} where dx/dy are pixel offsets and scale is the
   phantom blob scale factor."
  [influence position]
  (let [inf-sq   (* influence influence)
        lift     (* inf-sq 28.0)
        scale    (+ 0.6 (* inf-sq 0.9))]
    (case position
      "bottom" {:dx 0.0 :dy (- lift) :scale scale}
      "top"    {:dx 0.0 :dy lift     :scale scale}
      "left"   {:dx lift :dy 0.0     :scale scale}
      "right"  {:dx (- lift) :dy 0.0 :scale scale}
      ;; default (bottom)
      {:dx 0.0 :dy (- lift) :scale scale})))

(defn compute-bob-tilt
  "Compute icon bob (lift), tilt (rotation), and scale based on influence,
   cursor direction relative to the icon, dock position, and bob-intensity.

   - dx-sign: -1 if cursor is left/above, +1 if right/below (for tilt direction)
   - bob-intensity: 0 = icons don't move, 1 = normal, 2 = dramatic

   Returns {:lift :rotation :scale} where lift is pixels along dock-normal,
   rotation is degrees, scale is a multiplier."
  [influence dx-sign position bob-intensity]
  (let [inf-sq   (* influence influence)
        max-lift (* 12.0 bob-intensity)
        max-rot  (* 5.0 bob-intensity)
        max-scl  (* 0.12 bob-intensity)
        lift     (* inf-sq max-lift)
        rotation (* inf-sq max-rot dx-sign)
        scale    (+ 1.0 (* inf-sq max-scl))]
    {:lift     (case position
                 "bottom" (- lift)
                 "top"    lift
                 "left"   lift
                 "right"  (- lift)
                 (- lift))
     :rotation rotation
     :scale    scale}))

(defn select-detail
  "Build event detail map for x-liquid-dock-select."
  [index item source]
  {"index"  index
   "item"   item
   "source" source})

(def method-api nil)
