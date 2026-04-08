(ns baredom.components.x-liquid-glass.model)

;; ── Tag name ────────────────────────────────────────────────────────────────
(def tag-name "x-liquid-glass")

;; ── Attribute names ─────────────────────────────────────────────────────────
(def attr-blobs              "blobs")
(def attr-speed              "speed")
(def attr-amplitude          "amplitude")
(def attr-blur               "blur")
(def attr-goo                "goo")
(def attr-tint               "tint")
(def attr-specular           "specular")
(def attr-specular-size      "specular-size")
(def attr-specular-intensity "specular-intensity")
(def attr-disabled           "disabled")
(def attr-mode               "mode")
(def attr-frost              "frost")
(def attr-color-1            "color-1")
(def attr-color-2            "color-2")

(def observed-attributes
  #js [attr-blobs attr-speed attr-amplitude attr-blur attr-goo
       attr-tint attr-specular attr-specular-size attr-specular-intensity
       attr-disabled attr-mode attr-frost attr-color-1 attr-color-2])

;; ── CSS custom property names ───────────────────────────────────────────────
(def css-tint             "--x-liquid-glass-tint")
(def css-border           "--x-liquid-glass-border")
(def css-border-width     "--x-liquid-glass-border-width")
(def css-shadow           "--x-liquid-glass-shadow")
(def css-padding          "--x-liquid-glass-padding")
(def css-specular-color   "--x-liquid-glass-specular-color")
(def css-gradient-1       "--x-liquid-glass-gradient-1")
(def css-gradient-2       "--x-liquid-glass-gradient-2")
(def css-blur             "--x-liquid-glass-blur")
(def css-spring           "--x-liquid-glass-spring")
(def css-spring-duration  "--x-liquid-glass-spring-duration")

;; ── Property API ────────────────────────────────────────────────────────────
(def property-api
  {:blobs            {:type 'number}
   :speed            {:type 'number}
   :amplitude        {:type 'number}
   :blur             {:type 'number}
   :goo              {:type 'number}
   :tint             {:type 'string}
   :specular         {:type 'boolean}
   :specularSize     {:type 'number}
   :specularIntensity {:type 'number}
   :disabled         {:type 'boolean}
   :mode             {:type 'string}
   :frost            {:type 'number}
   :color1           {:type 'string}
   :color2           {:type 'string}})

;; ── Event schema ────────────────────────────────────────────────────────────
(def event-schema {})

;; ── Spring easing ───────────────────────────────────────────────────────────
(def spring-easing
  "CSS linear() spring easing function for physically natural motion."
  (str "linear("
       "0, 0.006, 0.025, 0.058, 0.104, 0.163, 0.234, 0.315, 0.403, 0.496, "
       "0.591, 0.685, 0.774, 0.856, 0.926, 0.983, 1.025, 1.052, 1.065, "
       "1.063, 1.049, 1.025, 0.995, 0.961, 0.927, 0.896, 0.870, 0.850, "
       "0.837, 0.831, 0.832, 0.839, 0.851, 0.867, 0.885, 0.903, 0.921, "
       "0.937, 0.951, 0.962, 0.970, 0.976, 0.979, 0.981, 0.981, 0.980, "
       "0.979, 0.977, 0.976, 0.975, 0.975, 0.976, 0.977, 0.979, 0.981, "
       "0.983, 0.986, 0.989, 0.992, 0.995, 0.998, 1"
       ")"))

(def spring-duration "600ms")

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

(defn- parse-int-clamped
  "Parse a string to integer, clamp to [lo, hi], return default on nil/NaN."
  [s default-val lo hi]
  (if (string? s)
    (let [v (.trim s)]
      (if (= v "")
        default-val
        (let [n (js/parseInt v 10)]
          (if (js/isNaN n)
            default-val
            (js/Math.min hi (js/Math.max lo n))))))
    default-val))

(defn parse-blobs     [s] (parse-int-clamped s 5 3 8))
(defn parse-speed     [s] (parse-float-clamped s 0.3 0.05 2.0))
(defn parse-amplitude [s] (parse-float-clamped s 0.15 0.05 0.4))
(defn parse-blur      [s] (parse-float-clamped s 12.0 0.0 40.0))
(defn parse-goo       [s] (parse-float-clamped s 10.0 4.0 20.0))
(defn parse-frost     [s] (parse-float-clamped s 0.5 0.0 1.0))
(defn parse-specular-size      [s] (parse-float-clamped s 0.4 0.1 1.0))
(defn parse-specular-intensity [s] (parse-float-clamped s 0.3 0.0 1.0))

(defn parse-bool
  "Parse a boolean presence attribute. Present (any value) → true, nil → false."
  [s]
  (some? s))

(defn parse-tint
  "Parse tint attribute. Returns nil if absent or empty, otherwise the string."
  [s]
  (when (string? s)
    (let [v (.trim s)]
      (when (not= v "") v))))

(defn parse-color
  "Parse a color attribute. Returns nil if absent or empty, otherwise the string."
  [s]
  (when (string? s)
    (let [v (.trim s)]
      (when (not= v "") v))))

(def ^:private mode->kw {"surface" :surface "submerged" :submerged})

(defn parse-mode
  "Parse mode attribute. Returns :surface or :submerged."
  [s]
  (or (get mode->kw (when (string? s) (.toLowerCase (.trim s))))
      :surface))

;; ── Normalise ───────────────────────────────────────────────────────────────

(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :blobs-raw              string | nil
    :speed-raw              string | nil
    :amplitude-raw          string | nil
    :blur-raw               string | nil
    :goo-raw                string | nil
    :tint-raw               string | nil
    :specular-attr          string | nil  (nil when absent)
    :specular-size-raw      string | nil
    :specular-intensity-raw string | nil
    :disabled-attr          string | nil  (nil when absent)

  Output keys:
    :blobs              int
    :speed              number
    :amplitude          number
    :blur               number
    :goo                number
    :tint               string | nil
    :specular?          boolean
    :specular-size      number
    :specular-intensity number
    :disabled?          boolean"
  [{:keys [blobs-raw speed-raw amplitude-raw blur-raw goo-raw
           tint-raw specular-attr specular-size-raw specular-intensity-raw
           disabled-attr mode-raw frost-raw color-1-raw color-2-raw]}]
  {:blobs              (parse-blobs blobs-raw)
   :speed              (parse-speed speed-raw)
   :amplitude          (parse-amplitude amplitude-raw)
   :blur               (parse-blur blur-raw)
   :goo                (parse-goo goo-raw)
   :tint               (parse-tint tint-raw)
   :specular?          (parse-bool specular-attr)
   :specular-size      (parse-specular-size specular-size-raw)
   :specular-intensity (parse-specular-intensity specular-intensity-raw)
   :disabled?          (parse-bool disabled-attr)
   :mode               (parse-mode mode-raw)
   :frost              (parse-frost frost-raw)
   :color-1            (or (parse-color color-1-raw) "rgba(99,102,241,0.4)")
   :color-2            (or (parse-color color-2-raw) "rgba(244,114,182,0.35)")})

;; ── Pseudo-noise ────────────────────────────────────────────────────────────

(defn pseudo-noise
  "Organic-looking noise from sum of sines. Returns value in ~[-1, 1]."
  [x y t]
  (+ (* (js/Math.sin (+ (* x 1.7) (* t 0.3)))
        (js/Math.cos (+ (* y 2.3) (* t 0.5)))
        0.5)
     (* (js/Math.sin (- (* x 3.1) (* t 0.7)))
        (js/Math.cos (+ (* y 1.3) (* t 0.2)))
        0.25)
     (* (js/Math.sin (+ (* x 5.3) (* t 0.4)))
        (js/Math.cos (- (* y 4.7) (* t 0.6)))
        0.125)))

;; ── Satellite geometry ──────────────────────────────────────────────────────

(def ^:private golden-angle
  "Golden angle in radians — decorrelates adjacent satellite noise seeds."
  2.39996)

(defn satellite-rest-positions
  "Compute rest positions for n satellite ellipses evenly distributed
   around a central ellipse. Returns #js [xs ys] where xs/ys are Float64Arrays.

   cx, cy — center of the element
   rx, ry — semi-axes of the orbital path (where satellites sit)"
  [n cx cy rx ry]
  (let [xs (js/Float64Array. n)
        ys (js/Float64Array. n)]
    (dotimes [i n]
      (let [angle (* 2.0 js/Math.PI (/ i n))]
        (aset xs i (+ cx (* rx (js/Math.cos angle))))
        (aset ys i (+ cy (* ry (js/Math.sin angle))))))
    #js [xs ys]))

(defn displace-satellite
  "Compute displaced position for satellite i at time t.
   Returns #js [x y]."
  [rest-x rest-y i t speed amplitude]
  (let [seed (* i golden-angle)
        dx (* amplitude (pseudo-noise seed 0.0 (* t speed)))
        dy (* amplitude (pseudo-noise 0.0 seed (* t speed)))]
    #js [(+ rest-x dx) (+ rest-y dy)]))
