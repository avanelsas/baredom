(ns baredom.components.x-morph-stack.model
  (:require [clojure.set :as set]))

;; ── Tag / slot / data attribute names ──────────────────────────────────────
(def tag-name "x-morph-stack")

(def slot-state            "state")
(def attr-data-state       "data-state")
(def attr-data-morph-id    "data-morph-id")
(def attr-data-active-state "data-active-state")

;; ── Observed attributes ────────────────────────────────────────────────────
(def attr-active-state "active-state")
(def attr-active-index "active-index")
(def attr-stiffness    "stiffness")
(def attr-damping      "damping")
(def attr-mass         "mass")
(def attr-variant      "variant")
(def attr-duration     "duration")
(def attr-disabled     "disabled")

;; Mirrored on the host so :host([data-variant='…']) CSS rules apply.
(def attr-data-variant "data-variant")

(def observed-attributes
  #js [attr-active-state
       attr-active-index
       attr-stiffness
       attr-damping
       attr-mass
       attr-variant
       attr-duration
       attr-disabled])

;; ── Variant presets ────────────────────────────────────────────────────────
(def variant-clean   "clean")
(def variant-organic "organic")
(def variant-liquid  "liquid")

(def default-variant variant-clean)

(def allowed-variants #{variant-clean variant-organic variant-liquid})

(defn parse-variant
  "Normalise a raw variant attribute string. Unknown / nil / empty → default."
  [s]
  (if (string? s)
    (let [v (.toLowerCase (.trim s))]
      (if (contains? allowed-variants v)
        v
        default-variant))
    default-variant))

(defn variant-uses-goo?
  "Whether a given variant string installs the SVG gooey filter."
  [variant]
  (or (= variant variant-organic) (= variant variant-liquid)))

;; ── Events ─────────────────────────────────────────────────────────────────
(def event-change   "x-morph-stack-change")
(def event-changed  "x-morph-stack-changed")

(def property-api
  {:activeState  {:type 'string}
   :activeIndex  {:type 'number}
   :stiffness    {:type 'number}
   :damping      {:type 'number}
   :mass         {:type 'number}
   :variant      {:type 'string :enum allowed-variants :default default-variant}
   :duration     {:type 'number :unit 'ms :nullable true}
   :disabled     {:type 'boolean}})

(def event-schema
  {event-change  {:detail {:from 'string :to 'string :reason 'string}
                  :cancelable true}
   event-changed {:detail {:from 'string :to 'string}
                  :cancelable false}})

;; ── Defaults ───────────────────────────────────────────────────────────────
(def ^:private default-stiffness 170.0)
(def ^:private default-damping    26.0)
(def ^:private default-mass        1.0)
(def default-goo-blur       10)
;; Alpha-channel multiplier in the gooey color-matrix. Larger values produce a
;; harder, more aggressive blob edge; the trailing -7 keeps the resulting alpha
;; biased toward fully opaque inside the blob.
(def default-goo-threshold  18)

(defn goo-matrix-values
  "Build the feColorMatrix `values` string for a given alpha multiplier.
  The alpha offset is derived linearly from the multiplier so that:
    - threshold = 18 → '0 0 0 18 -7' (the canonical gooey configuration)
    - threshold =  1 → '0 0 0  1  0' (the identity alpha row, no goo effect)
  This lets callers tween a single number toward 1 to fade the gooey filter
  smoothly into a no-op as the morph spring settles, avoiding a visual snap
  when the filtered ghost is replaced by the un-filtered real element."
  [threshold]
  (let [offset (* -7.0 (/ (- threshold 1.0) 17.0))]
    (str "1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 " threshold " " offset)))

;; ── Pure parsers ───────────────────────────────────────────────────────────
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

(defn parse-stiffness [s] (parse-float-clamped s default-stiffness 10.0 500.0))
(defn parse-damping   [s] (parse-float-clamped s default-damping    1.0 100.0))
(defn parse-mass      [s] (parse-float-clamped s default-mass       0.1  10.0))

(defn parse-active-index
  "Parse the active-index attribute to a non-negative integer, or nil."
  [s]
  (when (string? s)
    (let [v (.trim s)]
      (when (not= v "")
        (let [n (js/parseInt v 10)]
          (when (and (number? n) (not (js/isNaN n)) (>= n 0))
            n))))))

(defn parse-duration
  "Parse the duration attribute (positive number of ms) to a number, or nil.
  nil / empty / non-numeric / non-positive → nil, meaning the spring runs at
  its natural settle time."
  [s]
  (when (string? s)
    (let [v (.trim s)]
      (when (not= v "")
        (let [n (js/parseFloat v)]
          (when (and (number? n) (not (js/isNaN n)) (pos? n))
            n))))))

;; ── Spring duration estimation ─────────────────────────────────────────────
(defn natural-duration-ms
  "Estimate the spring's natural settle time in milliseconds from the current
  `stiffness`, `damping`, and `mass`. Combines two physical effects:

    ω₀ = √(k/m)          (undamped natural angular frequency, rad/s)
    ζ  = c / (2·√(k·m))  (damping ratio)

    ts ≈ 4 / (ζ·ω₀)  +  π / ω₀
       = envelope decay  +  rise time to first reach the target

  The envelope-decay term alone simplifies to 8m/c and is therefore
  insensitive to stiffness — that's mathematically right for the asymptotic
  decay of the displacement envelope, but it doesn't match the perceived
  duration because it ignores how fast the spring first arrives. Adding the
  rise-time term π/ω₀ (which is π·√(m/k)) brings stiffness back in, so a
  stiffer spring of the same damping is correctly estimated as faster.

  Inputs are clamped to safe positive ranges; ζ is clamped to [0.2, 5] so
  very low / very high damping doesn't blow the result up. The component
  divides this by an author-supplied `duration` to derive a per-tick time
  scale, so the exact constant matters less than its monotonic behaviour
  across spring tunings."
  [stiffness damping mass]
  (let [k  (max 0.0001 (or stiffness 0))
        c  (max 0.0001 (or damping 0))
        m  (max 0.0001 (or mass 0))
        ω0 (js/Math.sqrt (/ k m))
        ζ  (max 0.2 (min 5.0 (/ c (* 2.0 (js/Math.sqrt (* k m))))))
        envelope (/ 4.0 (* ζ ω0))
        rise     (/ js/Math.PI ω0)]
    (* 1000.0 (+ envelope rise))))

(defn time-scale-for
  "Compute the per-tick time scale that stretches/compresses the spring's
  natural settle time to the requested `duration-ms`. Returns 1.0 (no scaling)
  when `duration-ms` is nil / non-positive."
  [duration-ms stiffness damping mass]
  (if (and duration-ms (pos? duration-ms))
    (/ (natural-duration-ms stiffness damping mass) duration-ms)
    1.0))

(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :active-state-raw   string | nil
    :active-index-raw   string | nil
    :stiffness-raw      string | nil
    :damping-raw        string | nil
    :mass-raw           string | nil
    :variant-raw        string | nil
    :duration-raw       string | nil
    :disabled-present?  boolean

  Output keys:
    :active-state  string | nil
    :active-index  int | nil
    :stiffness     number
    :damping       number
    :mass          number
    :variant       string  (one of allowed-variants)
    :duration      number | nil  (ms; nil means natural spring time)
    :disabled?     boolean"
  [{:keys [active-state-raw active-index-raw
           stiffness-raw damping-raw mass-raw
           variant-raw duration-raw disabled-present?]}]
  {:active-state (when (and (string? active-state-raw)
                            (not= "" (.trim active-state-raw)))
                   (.trim active-state-raw))
   :active-index (parse-active-index active-index-raw)
   :stiffness    (parse-stiffness stiffness-raw)
   :damping      (parse-damping   damping-raw)
   :mass         (parse-mass      mass-raw)
   :variant      (parse-variant   variant-raw)
   :duration     (parse-duration  duration-raw)
   :disabled?    (boolean disabled-present?)})

;; ── Spring physics (verbatim from x-kinetic-font) ──────────────────────────
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

;; ── Active state resolution ────────────────────────────────────────────────
(defn resolve-active
  "Given a sequence of available state names and the normalised model,
  return the resolved active state name (string), or nil when no states.

  Rules:
    1. If active-state matches a known name → use it.
    2. Else clamp active-index (default 0) to [0, count-1] → name at that index.
    3. When the list is empty → nil."
  [state-names {:keys [active-state active-index]}]
  (let [v (vec state-names)
        n (count v)]
    (when (pos? n)
      (or (when (and active-state (some #(= % active-state) v)) active-state)
          (let [i (or active-index 0)
                clamped (max 0 (min (dec n) i))]
            (nth v clamped))))))

;; ── Lerp helpers (pure) ────────────────────────────────────────────────────
(defn lerp
  "Linear interpolation between a and b at progress t (0..1)."
  [a b t]
  (+ a (* (- b a) t)))

(defn parse-rgb-string
  "Parse a CSS color in 'rgb(r,g,b)' or 'rgba(r,g,b,a)' form into #js [r g b a].
  Returns nil for any other format (transparent, named colors, etc)."
  [s]
  (when (string? s)
    (let [m (.match s #"rgba?\(\s*(\d+(?:\.\d+)?)\s*,\s*(\d+(?:\.\d+)?)\s*,\s*(\d+(?:\.\d+)?)(?:\s*,\s*(\d*(?:\.\d+)?))?\s*\)")]
      (when m
        #js [(js/parseFloat (aget m 1))
             (js/parseFloat (aget m 2))
             (js/parseFloat (aget m 3))
             (if (and (aget m 4) (not= "" (aget m 4)))
               (js/parseFloat (aget m 4))
               1.0)]))))

(defn lerp-color
  "Lerp two CSS rgb/rgba colors. If either fails to parse, snaps at midpoint."
  [a b t]
  (let [pa (parse-rgb-string a)
        pb (parse-rgb-string b)]
    (if (and pa pb)
      (str "rgba("
           (js/Math.round (lerp (aget pa 0) (aget pb 0) t)) ","
           (js/Math.round (lerp (aget pa 1) (aget pb 1) t)) ","
           (js/Math.round (lerp (aget pa 2) (aget pb 2) t)) ","
           (lerp (aget pa 3) (aget pb 3) t) ")")
      (if (< t 0.5) a b))))

(defn parse-px-list
  "Parse a CSS length list like '8px 12px 4px 0px' into #js [n n n n] (in px).
  Returns nil if any token is not in px."
  [s]
  (when (string? s)
    (let [v (.trim s)]
      (when (not= v "")
        (let [tokens (.split v #"\s+")
              len (.-length tokens)
              out #js []]
          (loop [i 0]
            (if (>= i len)
              (when (pos? (.-length out)) out)
              (let [tok (aget tokens i)]
                (if (.endsWith tok "px")
                  (let [n (js/parseFloat (.slice tok 0 (- (.-length tok) 2)))]
                    (if (js/isNaN n)
                      nil
                      (do (.push out n) (recur (inc i)))))
                  nil)))))))))

(defn lerp-radius-list
  "Lerp two border-radius strings. Both must be matched-shape px lists.
  Falls back to snap-at-midpoint when shapes differ."
  [a b t]
  (let [pa (parse-px-list a)
        pb (parse-px-list b)]
    (if (and pa pb (= (.-length pa) (.-length pb)))
      (let [out #js []]
        (dotimes [i (.-length pa)]
          (.push out (str (lerp (aget pa i) (aget pb i) t) "px")))
        (.join out " "))
      (if (< t 0.5) a b))))

;; ── Morph-id diffing ───────────────────────────────────────────────────────
(defn diff-morph-ids
  "Given two collections of morph-ids, partition into matched/entering/leaving."
  [old-ids new-ids]
  (let [olds (set old-ids)
        news (set new-ids)]
    {:matched  (set/intersection olds news)
     :leaving  (set/difference   olds news)
     :entering (set/difference   news olds)}))

;; ── Event detail builders ──────────────────────────────────────────────────
(defn change-detail
  "Build the detail map for an x-morph-stack-change event."
  [from to reason]
  {:from (or from "") :to (or to "") :reason (or reason "method")})

(defn changed-detail
  "Build the detail map for an x-morph-stack-changed event."
  [from to]
  {:from (or from "") :to (or to "")})
