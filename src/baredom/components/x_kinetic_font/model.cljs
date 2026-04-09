(ns baredom.components.x-kinetic-font.model)

;; ── Tag name ────────────────────────────────────────────────────────────────
(def tag-name "x-kinetic-font")

;; ── Attribute names ─────────────────────────────────────────────────────────
(def attr-text        "text")
(def attr-trigger     "trigger")
(def attr-mode        "mode")
(def attr-per-char    "per-char")
(def attr-mass        "mass")
(def attr-tension     "tension")
(def attr-friction    "friction")
(def attr-intensity   "intensity")
(def attr-radius      "radius")
(def attr-font-family "font-family")

(def observed-attributes
  #js [attr-text attr-trigger attr-mode attr-per-char
       attr-mass attr-tension attr-friction attr-intensity
       attr-radius attr-font-family])

;; ── Event names ─────────────────────────────────────────────────────────────
(def event-spring-activate "x-kinetic-font-spring-activate")
(def event-spring-settle   "x-kinetic-font-spring-settle")

;; ── CSS custom property names ───────────────────────────────────────────────
(def css-color      "--x-kinetic-font-color")
(def css-family     "--x-kinetic-font-family")
(def css-size       "--x-kinetic-font-size")
(def css-wght-min   "--x-kinetic-font-weight-min")
(def css-wght-max   "--x-kinetic-font-weight-max")
(def css-wdth-min   "--x-kinetic-font-width-min")
(def css-wdth-max   "--x-kinetic-font-width-max")
(def css-slnt-min   "--x-kinetic-font-slant-min")
(def css-slnt-max   "--x-kinetic-font-slant-max")
(def css-opsz-min   "--x-kinetic-font-opsz-min")
(def css-opsz-max   "--x-kinetic-font-opsz-max")
(def css-skew-max   "--x-kinetic-font-skew-max")

;; ── Property API ────────────────────────────────────────────────────────────
(def property-api
  {:text       {:type 'string}
   :trigger    {:type 'string}
   :mode       {:type 'string}
   :perChar    {:type 'boolean}
   :mass       {:type 'number}
   :tension    {:type 'number}
   :friction   {:type 'number}
   :intensity  {:type 'number}
   :radius     {:type 'number}
   :fontFamily {:type 'string}})

;; ── Event schema ────────────────────────────────────────────────────────────
(def event-schema
  {event-spring-activate {:detail {} :cancelable false}
   event-spring-settle   {:detail {} :cancelable false}})

;; ── Allowed values ──────────────────────────────────────────────────────────
(def ^:private allowed-triggers #{"cursor" "scroll" "both"})
(def ^:private allowed-modes    #{"bulge" "lean" "stretch" "breathe"})

;; ── Defaults ────────────────────────────────────────────────────────────────
(def ^:private default-trigger   "cursor")
(def ^:private default-mode-set  #{"bulge"})
(def ^:private default-mass      1.0)
(def ^:private default-tension   170.0)
(def ^:private default-friction  26.0)
(def ^:private default-intensity 0.5)
(def ^:private default-radius    200.0)

;; ── CSS custom property defaults (for axis ranges) ──────────────────────────
(def default-wght-min 100.0)
(def default-wght-max 900.0)
(def default-wdth-min 75.0)
(def default-wdth-max 125.0)
(def default-slnt-min -12.0)
(def default-slnt-max 0.0)
(def default-opsz-min 8.0)
(def default-opsz-max 144.0)
(def default-skew-max -15.0)

;; ── Parse functions ─────────────────────────────────────────────────────────

(defn- parse-float-clamped
  "Parse a string to float, clamp to [lo, hi], return default on nil/NaN."
  [s default-val lo hi]
  (if (string? s)
    (let [v (.trim s)]
      (if (= v "")
        default-val
        (let [n (js/parseFloat v)]
          (if (js/isNaN n)
            default-val
            (js/Math.min hi (js/Math.max lo n))))))
    default-val))

(defn parse-trigger
  "Parse trigger attribute. Returns \"cursor\", \"scroll\", or \"both\"."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-triggers v) v default-trigger)))

(defn parse-modes
  "Parse space-separated mode tokens. Returns a set of valid mode strings."
  [s]
  (if (string? s)
    (let [tokens (.split (.toLowerCase (.trim s)) #"\s+")
          valid  (into #{} (filter #(contains? allowed-modes %)) tokens)]
      (if (empty? valid) default-mode-set valid))
    default-mode-set))

(defn parse-per-char
  "Parse per-char boolean attribute. Present (any value) → true, nil → false."
  [s]
  (some? s))

(defn parse-mass      [s] (parse-float-clamped s default-mass      0.1   10.0))
(defn parse-tension   [s] (parse-float-clamped s default-tension   10.0  500.0))
(defn parse-friction  [s] (parse-float-clamped s default-friction  1.0   100.0))
(defn parse-intensity [s] (parse-float-clamped s default-intensity 0.0   1.0))
(defn parse-radius    [s] (parse-float-clamped s default-radius    20.0  1000.0))

(defn normalize-text
  "Normalise text attribute. Returns string (possibly empty)."
  [s]
  (if (string? s) s ""))

(defn normalize-font-family
  "Normalise font-family attribute. Returns trimmed string or nil."
  [s]
  (when (string? s)
    (let [v (.trim s)]
      (when-not (= v "") v))))

;; ── derive-state ────────────────────────────────────────────────────────────

(defn derive-state
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :text-raw         string | nil
    :trigger-raw      string | nil
    :mode-raw         string | nil
    :per-char-attr    string | nil  (nil when absent)
    :mass-raw         string | nil
    :tension-raw      string | nil
    :friction-raw     string | nil
    :intensity-raw    string | nil
    :radius-raw       string | nil
    :font-family-raw  string | nil

  Output keys:
    :text         string
    :trigger      string
    :modes        #{string}
    :per-char?    boolean
    :mass         number
    :tension      number
    :friction     number
    :intensity    number
    :radius       number
    :font-family  string | nil"
  [{:keys [text-raw trigger-raw mode-raw per-char-attr
           mass-raw tension-raw friction-raw intensity-raw
           radius-raw font-family-raw]}]
  {:text        (normalize-text text-raw)
   :trigger     (parse-trigger trigger-raw)
   :modes       (parse-modes mode-raw)
   :per-char?   (parse-per-char per-char-attr)
   :mass        (parse-mass mass-raw)
   :tension     (parse-tension tension-raw)
   :friction    (parse-friction friction-raw)
   :intensity   (parse-intensity intensity-raw)
   :radius      (parse-radius radius-raw)
   :font-family (normalize-font-family font-family-raw)})

;; ── Spring physics (pure) ───────────────────────────────────────────────────

(defn spring-step
  "Single step of damped spring physics (1D) with mass.
   Returns #js [new-displacement new-velocity].
   Acceleration = (tension * (target - current) - friction * velocity) / mass"
  [current target velocity dt mass tension friction]
  (let [accel   (/ (- (* tension (- target current)) (* friction velocity)) mass)
        new-vel (+ velocity (* accel dt))
        new-pos (+ current (* new-vel dt))]
    #js [new-pos new-vel]))

(defn spring-settled?
  "Returns true when displacement and velocity are below threshold."
  [displacement velocity]
  (and (< (js/Math.abs displacement) 0.001)
       (< (js/Math.abs velocity) 0.01)))

;; ── Force computation (pure) ────────────────────────────────────────────────

(defn compute-cursor-force
  "Compute normalised force (0–1) from cursor distance with quadratic falloff.
   Returns 0.0 when distance >= radius."
  [distance radius]
  (if (>= distance radius)
    0.0
    (let [t (- 1.0 (/ distance radius))]
      (* t t))))

(defn compute-scroll-force
  "Normalise scroll delta to a 0–1 force value."
  [scroll-delta]
  (js/Math.min 1.0 (/ (js/Math.abs scroll-delta) 100.0)))

;; ── Axis mapping (pure) ─────────────────────────────────────────────────────

(defn map-force-to-axes
  "Map a force value (0–1) to font variation axis values based on active modes.
   Returns #js [wght wdth slnt opsz]."
  [force modes intensity
   wght-min wght-max wdth-min wdth-max
   slnt-min slnt-max opsz-min opsz-max]
  (let [scaled (* force intensity)
        wght   (if (contains? modes "bulge")
                 (+ wght-min (* scaled (- wght-max wght-min)))
                 wght-min)
        wdth   (if (contains? modes "stretch")
                 (+ wdth-min (* scaled (- wdth-max wdth-min)))
                 wdth-min)
        ;; slnt: rest is slnt-max (0), excited is slnt-min (-12)
        slnt   (if (contains? modes "lean")
                 (+ slnt-max (* scaled (- slnt-min slnt-max)))
                 slnt-max)
        opsz   (if (contains? modes "breathe")
                 (+ opsz-min (* scaled (- opsz-max opsz-min)))
                 opsz-min)]
    #js [wght wdth slnt opsz]))

(defn build-variation-string
  "Build a CSS font-variation-settings string from axis values.
   axes is #js [wght wdth slnt opsz]."
  [^js axes]
  (str "'wght' " (.toFixed (aget axes 0) 1)
       ", 'wdth' " (.toFixed (aget axes 1) 1)
       ", 'slnt' " (.toFixed (aget axes 2) 1)
       ", 'opsz' " (.toFixed (aget axes 3) 1)))
