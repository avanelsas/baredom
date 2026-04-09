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
(def attr-goo          "goo")
(def attr-disabled     "disabled")

(def observed-attributes
  #js [attr-active-state
       attr-active-index
       attr-stiffness
       attr-damping
       attr-mass
       attr-goo
       attr-disabled])

;; ── Events ─────────────────────────────────────────────────────────────────
(def event-change   "x-morph-stack-change")
(def event-changed  "x-morph-stack-changed")

(def property-api
  {:activeState  {:type 'string}
   :activeIndex  {:type 'number}
   :stiffness    {:type 'number}
   :damping      {:type 'number}
   :mass         {:type 'number}
   :goo          {:type 'boolean}
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
(def default-goo-threshold
  ;; Standard "gooey" alpha-contrast color matrix.
  "1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 18 -7")

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

(defn parse-bool
  "Boolean attribute: present (any value) → true; nil → false."
  [s]
  (some? s))

(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :active-state-raw   string | nil
    :active-index-raw   string | nil
    :stiffness-raw      string | nil
    :damping-raw        string | nil
    :mass-raw           string | nil
    :goo-present?       boolean
    :disabled-present?  boolean

  Output keys:
    :active-state  string | nil
    :active-index  int | nil
    :stiffness     number
    :damping       number
    :mass          number
    :goo?          boolean
    :disabled?     boolean"
  [{:keys [active-state-raw active-index-raw
           stiffness-raw damping-raw mass-raw
           goo-present? disabled-present?]}]
  {:active-state (when (and (string? active-state-raw)
                            (not= "" (.trim active-state-raw)))
                   (.trim active-state-raw))
   :active-index (parse-active-index active-index-raw)
   :stiffness    (parse-stiffness stiffness-raw)
   :damping      (parse-damping   damping-raw)
   :mass         (parse-mass      mass-raw)
   :goo?         (boolean goo-present?)
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
