(ns baredom.components.x-scroll-parallax.model)

;; ── Tag name ────────────────────────────────────────────────────────────────
(def tag-name "x-scroll-parallax")

;; ── Attribute name constants ────────────────────────────────────────────────
(def attr-direction "direction")
(def attr-source    "source")
(def attr-easing    "easing")
(def attr-disabled  "disabled")
(def attr-label     "label")

(def observed-attributes
  #js [attr-direction attr-source attr-easing attr-disabled attr-label])

;; ── Event name constants ────────────────────────────────────────────────────
(def event-enter    "x-scroll-parallax-enter")
(def event-leave    "x-scroll-parallax-leave")
(def event-progress "x-scroll-parallax-progress")

;; ── Data attribute name constants (read from children) ──────────────────────
(def data-speed   "data-speed")
(def data-offset  "data-offset")
(def data-opacity "data-opacity")
(def data-scale   "data-scale")

;; ── Property API ────────────────────────────────────────────────────────────
(def property-api
  {:direction {:type 'string}
   :source    {:type 'string}
   :easing    {:type 'string}
   :disabled  {:type 'boolean}
   :label     {:type 'string}
   :progress  {:type 'number :read-only true}})

;; ── Event schema ────────────────────────────────────────────────────────────
(def event-schema
  {event-enter    {:detail {:progress 'number} :cancelable false}
   event-leave    {:detail {:progress 'number} :cancelable false}
   event-progress {:detail {:progress 'number} :cancelable false}})

;; ── Allowed enum values ─────────────────────────────────────────────────────
(def ^:private allowed-directions #{"vertical" "horizontal"})
(def ^:private allowed-sources    #{"document"})
(def ^:private allowed-easings    #{"none" "smooth"})

;; ── Parsing functions ───────────────────────────────────────────────────────

(defn parse-direction
  "Normalise direction attribute to \"vertical\" or \"horizontal\".
  Unknown / nil values fall back to \"vertical\"."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-directions v) v "vertical")))

(defn parse-source
  "Normalise source attribute to \"document\".
  Unknown / nil values fall back to \"document\"."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-sources v) v "document")))

(defn parse-easing
  "Normalise easing attribute to \"none\" or \"smooth\".
  Unknown / nil values fall back to \"none\"."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-easings v) v "none")))

(defn parse-bool-attr
  "Standard HTML boolean attribute: present (non-nil) = true, absent (nil) = false."
  [s]
  (some? s))

(defn parse-speed
  "Parse a data-speed string to a float. Default 1.0."
  [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (js/isNaN n) 1.0 n))
    1.0))

(defn parse-offset
  "Parse a data-offset string to a float. Default 0."
  [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (js/isNaN n) 0.0 n))
    0.0))

;; ── Normalization ───────────────────────────────────────────────────────────

(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :direction-raw  string | nil
    :source-raw     string | nil
    :easing-raw     string | nil
    :disabled-attr  string | nil  (hasAttribute)
    :label-raw      string | nil

  Output keys:
    :direction  string — \"vertical\" | \"horizontal\"
    :source     string — \"document\"
    :easing     string — \"none\" | \"smooth\"
    :disabled?  boolean
    :label      string"
  [{:keys [direction-raw source-raw easing-raw disabled-attr label-raw]}]
  {:direction (parse-direction direction-raw)
   :source    (parse-source source-raw)
   :easing    (parse-easing easing-raw)
   :disabled? (parse-bool-attr disabled-attr)
   :label     (or label-raw "")})

;; ── Progress & parallax math ────────────────────────────────────────────────

(defn compute-progress
  "Compute scroll progress [0,1] given element rect top, element height,
  and viewport height.
  0 = element just entered at the bottom, 1 = element about to leave at the top."
  [element-top element-height viewport-height]
  (let [total   (+ viewport-height element-height)
        scrolled (- viewport-height element-top)]
    (max 0.0 (min 1.0 (/ scrolled total)))))

(defn compute-parallax-offset
  "Compute the parallax translation in pixels for a child.
  progress:      scroll progress [0,1]
  speed:         parallax speed factor (1 = normal, 0 = fixed, etc.)
  viewport-size: viewport dimension in pixels
  initial-offset: data-offset value in pixels"
  [progress speed viewport-size initial-offset]
  (+ (* (- progress 0.5) viewport-size (- speed 1.0))
     initial-offset))

(defn compute-fade-opacity
  "Compute opacity for a child with data-opacity=\"fade\".
  fade-range is a fraction of the viewport (e.g. 0.2 for 20%).
  Returns 1.0 when in the middle, fades to 0.0 at edges."
  [progress fade-range]
  (cond
    ;; Fade in from bottom
    (< progress fade-range)
    (/ progress fade-range)
    ;; Fade out towards top
    (> progress (- 1.0 fade-range))
    (/ (- 1.0 progress) fade-range)
    ;; Fully visible in the middle
    :else
    1.0))

(defn compute-scale
  "Compute scale for a child with data-scale=\"grow\".
  scale-min is the minimum scale (e.g. 0.85).
  Returns 1.0 when centered (progress=0.5), scale-min at edges."
  [progress scale-min]
  (let [distance (js/Math.abs (- progress 0.5))
        ;; distance ranges from 0 (center) to 0.5 (edge)
        t (* distance 2.0)]
    (+ scale-min (* (- 1.0 scale-min) (- 1.0 t)))))

;; ── Event detail builders ───────────────────────────────────────────────────

(defn progress-detail [progress]
  {:progress progress})
