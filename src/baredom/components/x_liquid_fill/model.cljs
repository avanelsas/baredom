(ns baredom.components.x-liquid-fill.model)

;; ── Tag name ────────────────────────────────────────────────────────────────
(def tag-name "x-liquid-fill")

;; ── Attribute name constants ────────────────────────────────────────────────
(def attr-target           "target")
(def attr-orientation      "orientation")
(def attr-mode             "mode")
(def attr-theme            "theme")
(def attr-wave-intensity   "wave-intensity")
(def attr-splash-intensity "splash-intensity")
(def attr-layers           "layers")
(def attr-disabled         "disabled")

(def observed-attributes
  #js [attr-target attr-orientation attr-mode attr-theme
       attr-wave-intensity attr-splash-intensity attr-layers attr-disabled])

;; ── Event name constants ────────────────────────────────────────────────────
(def event-progress "x-liquid-fill-progress")

;; ── CSS custom property names ───────────────────────────────────────────────
(def css-color-1       "--x-liquid-fill-color-1")
(def css-color-2       "--x-liquid-fill-color-2")
(def css-color-3       "--x-liquid-fill-color-3")
(def css-specular      "--x-liquid-fill-specular-color")
(def css-opacity       "--x-liquid-fill-opacity")
(def css-wave-speed    "--x-liquid-fill-wave-speed")
(def css-bg            "--x-liquid-fill-bg")
(def css-border        "--x-liquid-fill-border")
(def css-radius        "--x-liquid-fill-radius")
(def css-bar-height    "--x-liquid-fill-bar-height")

;; ── Property API ────────────────────────────────────────────────────────────
(def property-api
  {:target          {:type 'string}
   :orientation     {:type 'string}
   :mode            {:type 'string}
   :theme           {:type 'string}
   :waveIntensity   {:type 'number}
   :splashIntensity {:type 'number}
   :layers          {:type 'number}
   :disabled        {:type 'boolean}
   :progress        {:type 'number :read-only true}})

;; ── Event schema ────────────────────────────────────────────────────────────
(def event-schema
  {event-progress {:detail     {:progress 'number :velocity 'number}
                   :cancelable false}})

;; ── Allowed enum values ─────────────────────────────────────────────────────
(def ^:private allowed-orientations #{"vertical" "horizontal"})
(def ^:private allowed-modes        #{"fill" "bar"})
(def ^:private allowed-themes       #{"gold" "water" "lava" "custom"})

;; ── Theme color presets ─────────────────────────────────────────────────────
(def theme-colors
  {"gold"  {:color-1 "#B8860B" :color-2 "#D4AF37" :color-3 "#F9F295"
            :specular "rgba(255,255,240,0.6)"}
   "water" {:color-1 "#0077B6" :color-2 "#00B4D8" :color-3 "#90E0EF"
            :specular "rgba(255,255,255,0.5)"}
   "lava"  {:color-1 "#8B0000" :color-2 "#FF4500" :color-3 "#FF8C00"
            :specular "rgba(255,255,200,0.7)"}})

;; ── Parse functions ─────────────────────────────────────────────────────────

(defn parse-orientation
  "Normalise orientation attribute. Unknown/nil → \"vertical\"."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-orientations v) v "vertical")))

(defn parse-mode
  "Normalise mode attribute. Unknown/nil → \"fill\"."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-modes v) v "fill")))

(defn parse-theme
  "Normalise theme attribute. Unknown/nil → \"gold\"."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-themes v) v "gold")))

(defn- parse-float-clamped
  "Parse string to float, clamp to [lo, hi], return default on nil/NaN."
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

(defn parse-wave-intensity   [s] (parse-float-clamped s 0.5 0.0 1.0))
(defn parse-splash-intensity [s] (parse-float-clamped s 0.7 0.0 1.0))

(defn parse-layers
  "Parse layers attribute to integer 2-5, default 3."
  [s]
  (if (string? s)
    (let [v (.trim s)]
      (if (= v "")
        3
        (let [n (js/parseInt v 10)]
          (if (js/isNaN n) 3
              (js/Math.min 5 (js/Math.max 2 n))))))
    3))

(defn parse-disabled
  "Parse disabled attribute. Present (any value) → true, nil → false."
  [s]
  (some? s))

;; ── Normalise (derive view-model) ───────────────────────────────────────────

(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :target-raw           string | nil
    :orientation-raw      string | nil
    :mode-raw             string | nil
    :theme-raw            string | nil
    :wave-intensity-raw   string | nil
    :splash-intensity-raw string | nil
    :layers-raw           string | nil
    :disabled-attr        string | nil

  Output keys:
    :target            string | nil
    :orientation       string
    :mode              string
    :theme             string
    :wave-intensity    number
    :splash-intensity  number
    :layers            integer
    :disabled?         boolean"
  [{:keys [target-raw orientation-raw mode-raw theme-raw
           wave-intensity-raw splash-intensity-raw layers-raw disabled-attr]}]
  {:target           (when (and (string? target-raw) (not= (.trim target-raw) ""))
                       (.trim target-raw))
   :orientation      (parse-orientation orientation-raw)
   :mode             (parse-mode mode-raw)
   :theme            (parse-theme theme-raw)
   :wave-intensity   (parse-wave-intensity wave-intensity-raw)
   :splash-intensity (parse-splash-intensity splash-intensity-raw)
   :layers           (parse-layers layers-raw)
   :disabled?        (parse-disabled disabled-attr)})

;; ── Golden angle for decorrelation ──────────────────────────────────────────
(def ^:private golden-angle 2.39996)

;; ── Layer parameters ────────────────────────────────────────────────────────

(defn layer-params
  "Compute per-layer rendering and physics parameters.
   i = layer index (0 = back, n-1 = front), n = total layers."
  [i n]
  (let [t (if (> n 1) (/ (double i) (double (dec n))) 1.0)]
    {:phase-offset (* i golden-angle)
     :speed-mult   (+ 0.7 (* 0.6 t))       ;; back slower, front faster
     :amp-mult     (+ 0.6 (* 0.8 t))        ;; back smaller, front larger
     :stiffness    (- 200.0 (* i 30.0))      ;; front stiffer
     :damping      (+ 10.0 (* i 3.0))        ;; back more damped
     :opacity      1.0}))                      ;; all layers opaque (depth via phase/speed)

;; ── Scroll computation (pure) ───────────────────────────────────────────────

(defn compute-scroll-progress
  "Compute scroll progress [0,1] for a scroll container."
  [scroll-top scroll-height client-height]
  (let [max-scroll (- scroll-height client-height)]
    (if (<= max-scroll 0) 0.0
        (js/Math.min 1.0 (js/Math.max 0.0 (/ scroll-top max-scroll))))))

(defn compute-scroll-velocity
  "Compute normalised scroll velocity [0,1].
   0 = stationary, 1 = fast scrolling (~2000 px/s)."
  [current-scroll last-scroll dt]
  (if (<= dt 0.001) 0.0
      (let [raw-vel (/ (js/Math.abs (- current-scroll last-scroll)) dt)]
        (js/Math.min 1.0 (/ raw-vel 2000.0)))))

;; ── Wave surface (pure) ─────────────────────────────────────────────────────

(def ^:private two-pi 6.283185307179586)

(defn wave-y
  "Compute wave surface Y displacement at normalised position x [0,1].
   Uses sum-of-sines with integer-cycle frequencies so the wave is balanced
   (same height at left and right edges). Amplitude in pixels.
   t = accumulated time, phase = layer phase offset."
  [x t amplitude phase]
  (* amplitude
     (+ (* 0.5  (js/Math.sin (+ (* x two-pi 3.0) t       phase)))
        (* 0.3  (js/Math.sin (+ (* x two-pi 5.0) (* t 1.3) (* phase 1.7))))
        (* 0.2  (js/Math.sin (+ (* x two-pi 7.0) (* t 0.7) (* phase 2.3)))))))

;; ── SVG path generation (pure) ──────────────────────────────────────────────

(def ^:private wave-points
  "Number of control points along the wave surface."
  10)

(defn wave-path-d
  "Generate an SVG path d-string for a single wave layer (vertical fill).
   fill-y = Y coordinate of the liquid surface at rest (0 = top, height = bottom).
   width, height = SVG viewport dimensions.
   t, amplitude, phase = wave animation parameters."
  [fill-y width height t amplitude phase]
  (let [sb #js []
        n  wave-points]
    ;; Start at left edge (x=0)
    (let [y0 (+ fill-y (wave-y 0.0 t amplitude phase))]
      (.push sb (str "M0," (.toFixed y0 2))))
    ;; Quadratic bezier segments across the full width (x=0 to x=1)
    (dotimes [i n]
      (let [x-cp (/ (+ i 0.5) n)
            x-ep (/ (+ i 1.0) n)
            cpx  (* x-cp width)
            cpy  (+ fill-y (wave-y x-cp t amplitude phase))
            ex   (* x-ep width)
            ey   (+ fill-y (wave-y x-ep t amplitude phase))]
        (.push sb (str "Q" (.toFixed cpx 2) "," (.toFixed cpy 2)
                       " " (.toFixed ex 2) "," (.toFixed ey 2)))))
    ;; Close: right side down, bottom across, left side up
    (.push sb (str "L" (.toFixed width 2) "," (.toFixed height 2)))
    (.push sb (str "L0," (.toFixed height 2)))
    (.push sb "Z")
    (.join sb "")))

(defn horizontal-wave-path-d
  "Generate SVG path d-string for horizontal fill (fills left to right).
   fill-x = X coordinate of the liquid surface at rest.
   width, height = SVG viewport dimensions."
  [fill-x _width height t amplitude phase]
  (let [sb #js []
        n  wave-points]
    ;; Start at top edge
    (let [x0 (+ fill-x (wave-y 0.0 t amplitude phase))]
      (.push sb (str "M" (.toFixed x0 2) ",0")))
    ;; Quadratic bezier segments down the full height (y=0 to y=1)
    (dotimes [i n]
      (let [y-cp (/ (+ i 0.5) n)
            y-ep (/ (+ i 1.0) n)
            cpy  (* y-cp height)
            cpx  (+ fill-x (wave-y y-cp t amplitude phase))
            ey   (* y-ep height)
            ex   (+ fill-x (wave-y y-ep t amplitude phase))]
        (.push sb (str "Q" (.toFixed cpx 2) "," (.toFixed cpy 2)
                       " " (.toFixed ex 2) "," (.toFixed ey 2)))))
    ;; Close: bottom to left, left side up, top across
    (.push sb (str "L0," (.toFixed height 2)))
    (.push sb "L0,0")
    (.push sb "Z")
    (.join sb "")))

;; ── Fill level computation ──────────────────────────────────────────────────

(defn progress->fill-y
  "Convert progress [0,1] to fill-y (SVG Y coordinate for liquid surface).
   progress 0 = empty (fill-y = height), progress 1 = full (fill-y = 0)."
  [progress height]
  (* height (- 1.0 progress)))

(defn progress->fill-x
  "Convert progress [0,1] to fill-x for horizontal orientation.
   progress 0 = empty (fill-x = 0), progress 1 = full (fill-x = width)."
  [progress width]
  (* width progress))

;; ── Spring physics (pure) ───────────────────────────────────────────────────

(defn spring-step
  "Single step of damped spring physics (1D).
   Returns #js [new-position new-velocity]."
  [current target velocity dt stiffness damping]
  (let [force   (- (* stiffness (- target current)) (* damping velocity))
        new-vel (+ velocity (* force dt))
        new-pos (+ current (* new-vel dt))]
    #js [new-pos new-vel]))

;; ── Lerp ────────────────────────────────────────────────────────────────────

(defn lerp
  "Linear interpolation from current toward target at given speed (0-1)."
  [current target speed]
  (+ current (* (- target current) speed)))

;; ── Event detail builder ────────────────────────────────────────────────────

(defn progress-detail
  "Build event detail map for x-liquid-fill-progress."
  [progress velocity]
  {:progress progress :velocity velocity})

(def method-api nil)
