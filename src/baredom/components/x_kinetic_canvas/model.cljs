(ns baredom.components.x-kinetic-canvas.model
  (:require [baredom.utils.model :as mu]))

;; ── Tag name ────────────────────────────────────────────────────────────────
(def tag-name "x-kinetic-canvas")

;; ── Attribute name constants ────────────────────────────────────────────────
(def attr-type       "type")
(def attr-variant    "variant")
(def attr-speed      "speed")
(def attr-density    "density")
(def attr-fullscreen "fullscreen")
(def attr-paused     "paused")

(def observed-attributes
  #js [attr-type attr-variant attr-speed attr-density attr-fullscreen attr-paused])

;; ── CSS custom property names ───────────────────────────────────────────────
(def css-bg      "--x-kinetic-canvas-bg")
(def css-color-1 "--x-kinetic-canvas-color-1")
(def css-color-2 "--x-kinetic-canvas-color-2")
(def css-color-3 "--x-kinetic-canvas-color-3")
(def css-matrix-font-size "--x-kinetic-canvas-matrix-font-size")

;; ── Property API ────────────────────────────────────────────────────────────
(def property-api
  {:type       {:type 'string  :reflects-attribute attr-type}
   :variant    {:type 'string  :reflects-attribute attr-variant}
   :speed      {:type 'string  :reflects-attribute attr-speed}
   :density    {:type 'string  :reflects-attribute attr-density}
   :fullscreen {:type 'boolean :reflects-attribute attr-fullscreen}
   :paused     {:type 'boolean :reflects-attribute attr-paused}})

;; ── Event schema ────────────────────────────────────────────────────────────
(def event-schema {})

;; ── Method API ──────────────────────────────────────────────────────────────
(def method-api {})

;; ── Allowed enum values ─────────────────────────────────────────────────────
(def ^:private allowed-types #{"starfield" "bubbles" "matrix"})

(def ^:private starfield-variants #{"motion" "twinkle"})

(def ^:private named-speeds {"slow" 0.3 "medium" 1.0 "fast" 2.5})

(def ^:private named-densities {"low" 0.5 "medium" 1.0 "high" 2.0})

;; ── Parse functions ─────────────────────────────────────────────────────────

(defn parse-type
  "Normalise type attribute. Unknown/nil -> \"starfield\"."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-types v) v "starfield")))

(defn parse-variant
  "Normalise variant attribute. Type-aware: starfield accepts \"motion\"/\"twinkle\",
   other types return nil (no variant support yet)."
  [s anim-type]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (case anim-type
      "starfield" (if (contains? starfield-variants v) v "motion")
      nil)))

(defn parse-speed
  "Parse speed attribute. Accepts named strings (\"slow\"/\"medium\"/\"fast\") or
   numeric multiplier. Returns float, default 1.0."
  [s]
  (if (string? s)
    (let [v (.toLowerCase (.trim s))]
      (if-let [named (get named-speeds v)]
        named
        (let [n (js/parseFloat v)]
          (if (or (js/isNaN n) (<= n 0))
            1.0
            (js/Math.min 10.0 n)))))
    1.0))

(defn parse-density
  "Parse density attribute. Accepts named strings (\"low\"/\"medium\"/\"high\") or
   numeric multiplier. Returns float, default 1.0."
  [s]
  (if (string? s)
    (let [v (.toLowerCase (.trim s))]
      (if-let [named (get named-densities v)]
        named
        (let [n (js/parseFloat v)]
          (if (or (js/isNaN n) (<= n 0))
            1.0
            (js/Math.min 5.0 n)))))
    1.0))

;; ── Normalise (derive view-model) ───────────────────────────────────────────

(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :type-raw        string | nil
    :variant-raw     string | nil
    :speed-raw       string | nil
    :density-raw     string | nil
    :fullscreen-attr string | nil
    :paused-attr     string | nil

  Output keys:
    :type        string
    :variant     string | nil
    :speed       number
    :density     number
    :fullscreen? boolean
    :paused?     boolean"
  [{:keys [type-raw variant-raw speed-raw density-raw
           fullscreen-attr paused-attr]}]
  (let [anim-type (parse-type type-raw)]
    {:type        anim-type
     :variant     (parse-variant variant-raw anim-type)
     :speed       (parse-speed speed-raw)
     :density     (parse-density density-raw)
     :fullscreen? (mu/parse-bool-present fullscreen-attr)
     :paused?     (mu/parse-bool-present paused-attr)}))

;; ── Entity count calculation ────────────────────────────────────────────────

(def ^:private default-matrix-font-size 14)

(defn entity-count
  "Calculate the number of entities for a given type, density, and canvas size.
   `matrix-font-size` is only used for the \"matrix\" type."
  ([anim-type density-multiplier width height]
   (entity-count anim-type density-multiplier width height default-matrix-font-size))
  ([anim-type density-multiplier width height matrix-font-size]
   (let [area (* width height)
         scale (/ area 500000.0)]
     (case anim-type
       "starfield" (-> (* 150.0 density-multiplier scale)
                       (js/Math.max 50)
                       (js/Math.min 500)
                       js/Math.round)
       "bubbles"   (-> (* 25.0 density-multiplier scale)
                       (js/Math.max 10)
                       (js/Math.min 100)
                       js/Math.round)
       "matrix"    (-> (* (/ width (double matrix-font-size)) density-multiplier)
                       (js/Math.max 5)
                       (js/Math.min 200)
                       js/Math.round)
       50))))
