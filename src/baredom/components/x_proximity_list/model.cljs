(ns baredom.components.x-proximity-list.model
  (:require [baredom.utils.model :as mu]))

;; ── Tag name ────────────────────────────────────────────────────────────────
(def tag-name "x-proximity-list")

;; ── Attribute names ─────────────────────────────────────────────────────────
(def attr-direction "direction")
(def attr-radius    "radius")
(def attr-max-scale "max-scale")
(def attr-lift      "lift")
(def attr-gap       "gap")
(def attr-disabled  "disabled")

(def observed-attributes
  #js [attr-direction attr-radius attr-max-scale attr-lift attr-gap attr-disabled])

;; ── CSS custom property names (theming) ─────────────────────────────────────
(def css-gap        "--x-proximity-list-gap")
(def css-item-size  "--x-proximity-list-item-size")

;; Per-item runtime CSS variables (set as inline style on each child).
(def css-item-influence "--x-proximity-influence")
(def css-item-scale     "--x-proximity-scale")
(def css-item-lift      "--x-proximity-lift")

;; ── Event names ─────────────────────────────────────────────────────────────
(def event-select "x-proximity-list-select")

(def event-schema
  {event-select {:detail {:index  'number
                          :item   'Element
                          :source 'string}
                 :bubbles    true
                 :composed   true
                 :cancelable true}})

;; ── Property API ────────────────────────────────────────────────────────────
(def property-api
  {:direction {:type 'string  :reflects-attribute attr-direction :default "horizontal"}
   :radius    {:type 'number  :reflects-attribute attr-radius    :default 120}
   :maxScale  {:type 'number  :reflects-attribute attr-max-scale :default 1.5}
   :lift      {:type 'number  :reflects-attribute attr-lift      :default 0}
   :gap       {:type 'number  :reflects-attribute attr-gap       :default 8}
   :disabled  {:type 'boolean :reflects-attribute attr-disabled}})

(def method-api nil)

;; ── Defaults ────────────────────────────────────────────────────────────────
(def ^:private default-direction "horizontal")
(def ^:private default-radius    120)
(def ^:private default-max-scale 1.5)
(def ^:private default-lift      0)
(def ^:private default-gap       8)

;; Soft-sign reference distance (px). Within this band around an item's
;; cross-axis the lift direction ramps linearly through zero, avoiding a
;; visible jump as the cursor crosses the item centre.
(def ^:private lift-soft-band 30.0)

;; ── Allowed values ──────────────────────────────────────────────────────────
(def ^:private allowed-directions #{"horizontal" "vertical"})

;; ── Parse functions ─────────────────────────────────────────────────────────

(defn parse-direction
  "Parse direction attribute. Returns 'horizontal' (default) or 'vertical'."
  [s]
  (if (string? s)
    (let [v (.toLowerCase (.trim s))]
      (if (contains? allowed-directions v) v default-direction))
    default-direction))

(defn parse-float-clamped
  "Parse a string to a float clamped in [lo, hi]. Returns default-val on failure."
  [s default-val lo hi]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (js/isNaN n)
        default-val
        (js/Math.min hi (js/Math.max lo n))))
    default-val))

(defn parse-radius [s]
  (parse-float-clamped s default-radius 20 600))

(defn parse-max-scale [s]
  (parse-float-clamped s default-max-scale 1.0 3.0))

(defn parse-lift [s]
  (parse-float-clamped s default-lift 0 60))

(defn parse-gap [s]
  (parse-float-clamped s default-gap 0 64))

;; ── Normalize ───────────────────────────────────────────────────────────────

(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :direction-raw  string | nil
    :radius-raw     string | nil
    :max-scale-raw  string | nil
    :lift-raw       string | nil
    :gap-raw        string | nil
    :disabled-attr  string | nil  (presence-based boolean)

  Output keys:
    :direction  string   (\"horizontal\" | \"vertical\")
    :radius     number
    :max-scale  number
    :lift       number
    :gap        number
    :disabled?  boolean
    :vertical?  boolean"
  [{:keys [direction-raw radius-raw max-scale-raw lift-raw gap-raw disabled-attr]}]
  (let [direction (parse-direction direction-raw)]
    {:direction  direction
     :radius     (parse-radius radius-raw)
     :max-scale  (parse-max-scale max-scale-raw)
     :lift       (parse-lift lift-raw)
     :gap        (parse-gap gap-raw)
     :disabled?  (mu/parse-bool-present disabled-attr)
     :vertical?  (= direction "vertical")}))

;; ── Derived computations ────────────────────────────────────────────────────

(defn compute-influence
  "Linear distance falloff. Returns 1.0 when the cursor is at the item centre,
  0.0 at or beyond `radius`, and decays linearly between.

    (ix, iy)  item centre, in track-relative coordinates
    (mx, my)  cursor position, same coordinate space
    radius    influence radius in px"
  [ix iy mx my radius]
  (let [dx   (- mx ix)
        dy   (- my iy)
        dist (js/Math.sqrt (+ (* dx dx) (* dy dy)))]
    (js/Math.max 0.0 (- 1.0 (/ dist radius)))))

(defn compute-scale
  "Map an influence factor [0..1] to a scale factor in [1.0 .. max-scale].
  Uses an influence^2 ramp so items close to the cursor swell more, and items
  on the periphery stay nearer their base size."
  [influence max-scale]
  (+ 1.0 (* influence influence (- max-scale 1.0))))

(defn compute-lift
  "Cross-axis translation (px) toward the cursor.

    influence   [0..1]
    cross-delta cursor offset on the cross-axis from item centre
                (negative when cursor is before, positive when after)
    lift-max    capped magnitude (px)

  Sign ramps linearly through the soft band around 0 so the direction
  doesn't snap when the cursor crosses the item's cross-axis centre."
  [influence cross-delta lift-max]
  (let [inf-sq (* influence influence)
        soft   (cond
                 (>= cross-delta lift-soft-band)        1.0
                 (<= cross-delta (- lift-soft-band))   -1.0
                 :else                                  (/ cross-delta lift-soft-band))]
    (* lift-max inf-sq soft)))

(defn lerp
  "Linear interpolation: moves `current` toward `target` by `speed` (0..1).
  At speed=0 nothing moves; at speed=1 the result equals target."
  [current target speed]
  (+ current (* (- target current) speed)))

(defn select-detail
  "Build event detail map for x-proximity-list-select."
  [index item source]
  {"index"  index
   "item"   item
   "source" source})
