(ns baredom.components.x-confetti.model)

(def tag-name "x-confetti")

;; ── Attribute names ──────────────────────────────────────────────────────────
(def attr-mode      "mode")
(def attr-origin    "origin")
(def attr-count     "count")
(def attr-spread    "spread")
(def attr-velocity  "velocity")
(def attr-gravity   "gravity")
(def attr-duration  "duration")
(def attr-colors    "colors")
(def attr-shapes    "shapes")
(def attr-auto-fire "auto-fire")
(def attr-disabled  "disabled")

(def observed-attributes
  #js [attr-mode
       attr-origin
       attr-count
       attr-spread
       attr-velocity
       attr-gravity
       attr-duration
       attr-colors
       attr-shapes
       attr-auto-fire
       attr-disabled])

;; ── Event names ──────────────────────────────────────────────────────────────
(def event-fire "x-confetti-fire")
(def event-end  "x-confetti-end")

;; ── Enums and ranges ─────────────────────────────────────────────────────────
(def mode-values   #{"overlay" "inline"})
(def origin-values #{"top" "center" "bottom" "point"})
(def shape-values  #{"square" "circle" "ribbon" "star"})

(def default-mode     "overlay")
(def default-origin   "top")
(def default-count    80)
(def default-spread   60)
(def default-velocity 14)
(def default-gravity  0.25)
(def default-duration 4000)
(def default-shapes   "square,ribbon")

(def count-min    1)
(def count-max    300)
(def spread-min   0)
(def spread-max   180)
(def velocity-min 1)
(def velocity-max 60)
(def gravity-min  -2)
(def gravity-max  2)
(def duration-min 200)
(def duration-max 20000)

;; ── Default theme palette ────────────────────────────────────────────────────
;; Used when the `colors` attribute is empty or invalid.
(def default-palette
  ["var(--x-color-primary,#2563eb)"
   "var(--x-color-accent,#8b5cf6)"
   "var(--x-color-success,#16a34a)"
   "var(--x-color-warning,#f59e0b)"
   "var(--x-color-info,#0ea5e9)"])

;; ── Property API (Tier 0) ────────────────────────────────────────────────────
(def property-api
  {:mode      {:type 'string  :reflects-attribute attr-mode      :default default-mode}
   :origin    {:type 'string  :reflects-attribute attr-origin    :default default-origin}
   :count     {:type 'number  :reflects-attribute attr-count     :default default-count}
   :spread    {:type 'number  :reflects-attribute attr-spread    :default default-spread}
   :velocity  {:type 'number  :reflects-attribute attr-velocity  :default default-velocity}
   :gravity   {:type 'number  :reflects-attribute attr-gravity   :default default-gravity}
   :duration  {:type 'number  :reflects-attribute attr-duration  :default default-duration}
   :colors    {:type 'string  :reflects-attribute attr-colors    :default ""}
   :shapes    {:type 'string  :reflects-attribute attr-shapes    :default default-shapes}
   :autoFire  {:type 'boolean :reflects-attribute attr-auto-fire}
   :disabled  {:type 'boolean :reflects-attribute attr-disabled}})

;; ── Event schema ─────────────────────────────────────────────────────────────
(def event-schema
  {event-fire {:detail {:count 'number :origin 'string} :cancelable false}
   event-end  {:detail {:duration 'number}              :cancelable false}})

;; ── Method API ───────────────────────────────────────────────────────────────
(def method-api
  {:fire {:args [{:name "options" :type 'object}] :returns 'void}})

;; ── Pure parsing / normalization ────────────────────────────────────────────
(defn- valid-enum [value allowed fallback]
  (if (and (string? value) (contains? allowed value)) value fallback))

(defn parse-mode   [s] (valid-enum s mode-values   default-mode))
(defn parse-origin [s] (valid-enum s origin-values default-origin))

(defn- clamp-num [n lo hi]
  (js/Math.min hi (js/Math.max lo n)))

(defn- parse-num
  "Parse a numeric attribute string, clamp to [lo, hi].
  Returns `default-val` when the input is missing or non-numeric."
  [s default-val lo hi]
  (if (string? s)
    (let [n (js/parseFloat s)]
      (if (js/isNaN n) default-val (clamp-num n lo hi)))
    default-val))

(defn parse-count [s]
  (let [n (parse-num s default-count count-min count-max)]
    (js/Math.round n)))

(defn parse-spread   [s] (parse-num s default-spread   spread-min   spread-max))
(defn parse-velocity [s] (parse-num s default-velocity velocity-min velocity-max))
(defn parse-gravity  [s] (parse-num s default-gravity  gravity-min  gravity-max))
(defn parse-duration [s] (parse-num s default-duration duration-min duration-max))

(defn- split-csv [s]
  (when (string? s)
    (->> (.split s ",")
         (map (fn [^js part] (.trim part)))
         (remove (fn [^js part] (= "" part))))))

(defn parse-colors
  "Parse the `colors` attribute into a vector of CSS color strings.
  Empty / missing → returns `default-palette`."
  [s]
  (let [parts (vec (split-csv s))]
    (if (seq parts) parts default-palette)))

(defn parse-shapes
  "Parse the `shapes` attribute into a vector of allowed shape keywords.
  Filters out unknown values; falls back to defaults when the result is empty."
  [s]
  (let [raw   (or (split-csv s) (split-csv default-shapes))
        valid (filter shape-values raw)]
    (if (seq valid) (vec valid) (vec (split-csv default-shapes)))))

(defn normalize
  "Normalise raw attribute strings into a stable view-model map.

  Input keys:
    :mode-raw, :origin-raw, :count-raw, :spread-raw, :velocity-raw,
    :gravity-raw, :duration-raw, :colors-raw, :shapes-raw,
    :auto-fire? (bool), :disabled? (bool)

  Output keys:
    :mode, :origin, :count, :spread, :velocity, :gravity, :duration,
    :colors (vec), :shapes (vec), :auto-fire?, :disabled?"
  [{:keys [mode-raw origin-raw count-raw spread-raw velocity-raw
           gravity-raw duration-raw colors-raw shapes-raw
           auto-fire? disabled?]}]
  {:mode       (parse-mode mode-raw)
   :origin     (parse-origin origin-raw)
   :count      (parse-count count-raw)
   :spread     (parse-spread spread-raw)
   :velocity   (parse-velocity velocity-raw)
   :gravity    (parse-gravity gravity-raw)
   :duration   (parse-duration duration-raw)
   :colors     (parse-colors colors-raw)
   :shapes     (parse-shapes shapes-raw)
   :auto-fire? (boolean auto-fire?)
   :disabled?  (boolean disabled?)})

;; ── Burst geometry helpers (pure) ────────────────────────────────────────────
(defn origin-point
  "Return the [x y] origin pixel for a burst, given canvas dimensions and
   the requested origin mode. For `point`, falls back to canvas center if
   the supplied coordinates are nil."
  [origin w h ox oy]
  (case origin
    "top"    [(* w 0.5) 0]
    "center" [(* w 0.5) (* h 0.5)]
    "bottom" [(* w 0.5) h]
    "point"  [(if (number? ox) ox (* w 0.5))
              (if (number? oy) oy (* h 0.5))]
    [(* w 0.5) 0]))

(defn launch-angle-rad
  "Compute the launch angle (radians) for a particle, given the burst origin
   and a uniform random in [0,1]. The base direction depends on `origin`:
   top → downward, center → omni, bottom → upward, point → omni.

   `spread-deg` is the half-angle (so 60 means ±60°)."
  [origin spread-deg r]
  (let [spread (* (/ spread-deg 180.0) js/Math.PI)
        ;; Centre direction in radians, measured CW from +x axis
        ;; Canvas y grows downward.
        base   (case origin
                 "top"     (* 0.5 js/Math.PI)               ;; +y (downward)
                 "bottom"  (* -0.5 js/Math.PI)              ;; -y (upward)
                 "center"  (* 2.0 js/Math.PI r)             ;; full circle, ignores spread
                 "point"   (* 2.0 js/Math.PI r)
                 (* 0.5 js/Math.PI))]
    (case origin
      ("center" "point") base
      (+ base (* spread (- (* 2.0 r) 1.0))))))
