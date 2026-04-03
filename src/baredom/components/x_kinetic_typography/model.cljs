(ns baredom.components.x-kinetic-typography.model
  (:require [clojure.set :as set]))

(def tag-name "x-kinetic-typography")

;; ── Attribute constants ──────────────────────────────────────────────────
(def attr-text         "text")
(def attr-path         "path")
(def attr-preset       "preset")
(def attr-animation    "animation")
(def attr-speed        "speed")
(def attr-direction    "direction")
(def attr-effect       "effect")
(def attr-font-size    "font-size")
(def attr-start-size   "start-size")
(def attr-end-size     "end-size")
(def attr-repeat       "repeat")
(def attr-echo-count   "echo-count")
(def attr-echo-delay   "echo-delay")
(def attr-echo-opacity "echo-opacity")
(def attr-echo-scale   "echo-scale")

(def observed-attributes
  #js [attr-text attr-path attr-preset attr-animation attr-speed
       attr-direction attr-effect attr-font-size attr-start-size
       attr-end-size attr-repeat attr-echo-count attr-echo-delay
       attr-echo-opacity attr-echo-scale])

;; ── Property API ─────────────────────────────────────────────────────────
(def property-api
  {:text         {:type 'string  :reflects-attribute attr-text}
   :path         {:type 'string  :reflects-attribute attr-path}
   :preset       {:type 'string  :reflects-attribute attr-preset}
   :animation    {:type 'string  :reflects-attribute attr-animation}
   :speed        {:type 'string  :reflects-attribute attr-speed}
   :direction    {:type 'string  :reflects-attribute attr-direction}
   :effect       {:type 'string  :reflects-attribute attr-effect}
   :fontSize     {:type 'string  :reflects-attribute attr-font-size}
   :startSize    {:type 'string  :reflects-attribute attr-start-size}
   :endSize      {:type 'string  :reflects-attribute attr-end-size}
   :repeat       {:type 'string  :reflects-attribute attr-repeat}
   :echoCount    {:type 'string  :reflects-attribute attr-echo-count}
   :echoDelay    {:type 'string  :reflects-attribute attr-echo-delay}
   :echoOpacity  {:type 'string  :reflects-attribute attr-echo-opacity}
   :echoScale    {:type 'string  :reflects-attribute attr-echo-scale}})

(def event-schema {})

;; ── Defaults ─────────────────────────────────────────────────────────────
(def default-preset        "wave")
(def default-animation     "scroll")
(def default-speed         1.0)
(def default-direction     "normal")
(def default-repeat        1)
(def default-echo-count    0)
(def default-echo-delay    0.3)
(def default-echo-opacity  0.5)
(def default-echo-scale    0.85)

;; ── Allowed values ───────────────────────────────────────────────────────
(def allowed-presets
  #{"wave" "circle" "arc" "infinity" "spiral" "sine" "line" "crawl"})

(def allowed-animations
  #{"none" "scroll" "bounce" "oscillate"})

(def allowed-directions
  #{"normal" "reverse"})

(def allowed-effects
  #{"opacity-wave" "size-gradient" "size-pulse" "spacing-breathe" "color-shift" "color-wave"})

;; ── Path presets ─────────────────────────────────────────────────────────
(def path-presets
  {"wave"     {:d        "M0,100 C100,20 200,180 300,100 C400,20 500,180 600,100 C700,20 800,180 800,100"
               :view-box "0 0 800 200"}
   "circle"   {:d        "M125,10 A115,115 0 1,1 124.99,10"
               :view-box "0 0 250 250"}
   "arc"      {:d        "M50,350 Q400,10 750,350"
               :view-box "0 0 800 400"}
   "infinity" {:d        "M200,200 C200,80 0,80 0,200 C0,320 200,320 200,200 C200,80 400,80 400,200 C400,320 200,320 200,200"
               :view-box "0 0 400 400"}
   "spiral"   {:d        "M250,250 C250,150 350,150 350,250 C350,370 130,370 130,250 C130,110 390,110 390,250 C390,410 90,410 90,250"
               :view-box "0 0 500 500"}
   "sine"     {:d        "M0,100 C50,0 100,0 150,100 C200,200 250,200 300,100 C350,0 400,0 450,100 C500,200 550,200 600,100 C650,0 700,0 750,100 C800,200 800,150 800,100"
               :view-box "0 0 800 200"}
   "line"     {:d        "M0,50 L800,50"
               :view-box "0 0 800 100"}})

;; ── Parsing & normalisation ──────────────────────────────────────────────

(defn normalize-preset [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-presets v)
      v
      default-preset)))

(defn normalize-animation [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-animations v)
      v
      default-animation)))

(defn normalize-direction [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-directions v)
      v
      default-direction)))

(defn parse-speed [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (and (number? n) (not (js/isNaN n)) (pos? n))
        n
        default-speed))
    default-speed))

(defn parse-repeat [s]
  (if (string? s)
    (let [n (js/parseInt (.trim s) 10)]
      (if (and (number? n) (not (js/isNaN n)) (pos? n))
        n
        default-repeat))
    default-repeat))

(defn parse-echo-count [s]
  (if (string? s)
    (let [n (js/parseInt (.trim s) 10)]
      (if (and (number? n) (not (js/isNaN n)) (>= n 0) (<= n 5))
        n
        default-echo-count))
    default-echo-count))

(defn parse-echo-delay [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (and (number? n) (not (js/isNaN n)) (pos? n))
        n
        default-echo-delay))
    default-echo-delay))

(defn parse-echo-opacity [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (and (number? n) (not (js/isNaN n)) (pos? n) (<= n 1))
        n
        default-echo-opacity))
    default-echo-opacity))

(defn parse-echo-scale [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (and (number? n) (not (js/isNaN n)) (pos? n) (<= n 1))
        n
        default-echo-scale))
    default-echo-scale))

(defn parse-effects
  "Parse a space-separated effect string into a set of valid effect tokens."
  [s]
  (if (string? s)
    (let [tokens (set (.split (.trim (.toLowerCase s)) #"\s+"))]
      (set/intersection tokens allowed-effects))
    #{}))

(defn normalize-text [s]
  (if (string? s) s ""))

(defn normalize-css-value [s]
  (when (and (string? s) (not= "" (.trim s)))
    (.trim s)))

(defn resolve-path
  "Returns {:d \"...\" :view-box \"...\"} from custom path or preset.
  Returns nil for crawl preset (no SVG path needed)."
  [custom-path preset]
  (if (= preset "crawl")
    nil
    (if (some? custom-path)
      {:d custom-path :view-box "0 0 800 200"}
      (get path-presets preset))))

(defn compute-duration-s
  "Compute animation duration in seconds: base / speed."
  [speed]
  (let [base 10.0]
    (/ base (max speed 0.01))))

(defn derive-state
  "Master normalisation: raw attribute map -> stable view-model."
  [{:keys [text path preset animation speed direction effect
           font-size start-size end-size repeat
           echo-count echo-delay echo-opacity echo-scale]}]
  (let [norm-text      (normalize-text text)
        custom-path    (normalize-css-value path)
        norm-preset    (normalize-preset preset)
        crawl?         (= norm-preset "crawl")
        norm-animation (normalize-animation animation)
        norm-direction (normalize-direction direction)
        norm-speed     (parse-speed speed)
        norm-repeat    (parse-repeat repeat)
        effects        (parse-effects effect)
        norm-start     (normalize-css-value start-size)
        norm-end       (normalize-css-value end-size)
        resolved       (resolve-path custom-path norm-preset)
        ;; Auto-enable size-gradient when both start-size and end-size are set
        effects        (if (and norm-start norm-end (not crawl?))
                         (conj effects "size-gradient")
                         effects)
        ;; size-gradient and size-pulse are mutually exclusive; size-gradient wins
        effects        (if (contains? effects "size-gradient")
                         (disj effects "size-pulse")
                         effects)
        ;; color-wave and color-shift are mutually exclusive; color-wave wins
        final-effects  (if (contains? effects "color-wave")
                         (disj effects "color-shift")
                         effects)]
    {:text          norm-text
     :path-d        (:d resolved)
     :view-box      (:view-box resolved)
     :custom-path?  (some? custom-path)
     :preset        norm-preset
     :crawl?        crawl?
     :animation     norm-animation
     :direction     norm-direction
     :speed         norm-speed
     :duration-s    (compute-duration-s norm-speed)
     :repeat        norm-repeat
     :effects       final-effects
     :font-size     (normalize-css-value font-size)
     :start-size    norm-start
     :end-size      norm-end
     :echo-count    (if crawl? 0 (parse-echo-count echo-count))
     :echo-delay    (parse-echo-delay echo-delay)
     :echo-opacity  (parse-echo-opacity echo-opacity)
     :echo-scale    (parse-echo-scale echo-scale)}))
