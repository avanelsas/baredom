(ns baredom.components.x-metaball-cursor.model
  (:require [baredom.utils.model :as mu]))

;; ── Tag name ────────────────────────────────────────────────────────────────
(def tag-name "x-metaball-cursor")

;; ── Attribute names ─────────────────────────────────────────────────────────
(def attr-blob-count      "blob-count")
(def attr-blob-size       "blob-size")
(def attr-color           "color")
(def attr-noise           "noise")
(def attr-noise-scale     "noise-scale")
(def attr-noise-speed     "noise-speed")
(def attr-noise-intensity "noise-intensity")
(def attr-blur            "blur")
(def attr-threshold       "threshold")
(def attr-palette         "palette")

(def observed-attributes
  #js [attr-blob-count attr-blob-size attr-color attr-noise
       attr-noise-scale attr-noise-speed attr-noise-intensity
       attr-blur attr-threshold attr-palette])

;; ── CSS custom property names ───────────────────────────────────────────────
(def css-color      "--x-metaball-cursor-color")
(def css-opacity    "--x-metaball-cursor-opacity")
(def css-z-index    "--x-metaball-cursor-z-index")
(def css-blend-mode "--x-metaball-cursor-blend-mode")
(def css-inset      "--x-metaball-cursor-inset")

;; ── Property API ────────────────────────────────────────────────────────────
(def property-api
  {:blobCount      {:type 'number}
   :blobSize       {:type 'number}
   :color          {:type 'string}
   :noise          {:type 'boolean}
   :noiseScale     {:type 'number}
   :noiseSpeed     {:type 'number}
   :noiseIntensity {:type 'number}
   :blur           {:type 'number}
   :threshold      {:type 'string}
   :palette        {:type 'string}})

(def event-schema {})

;; ── Defaults ────────────────────────────────────────────────────────────────
(def ^:private default-blob-count      5)
(def ^:private default-blob-size       40)
(def ^:private default-color           "#6366f1")
(def ^:private default-noise-scale     3)
(def ^:private default-noise-speed     0.02)
(def ^:private default-noise-intensity 6)
(def ^:private default-blur            12)
(def ^:private default-threshold       "1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 20 -10")

;; ── Parse functions ─────────────────────────────────────────────────────────
(defn parse-blob-count
  "Parse blob-count to int in [2, 10], default 5."
  [s]
  (if (string? s)
    (let [n (js/parseInt (.trim s) 10)]
      (if (or (js/isNaN n) (not (pos? n)))
        default-blob-count
        (js/Math.min 10 (js/Math.max 2 n))))
    default-blob-count))

(defn parse-blob-size
  "Parse blob-size to number in [10, 200], default 40."
  [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (or (js/isNaN n) (not (pos? n)))
        default-blob-size
        (js/Math.min 200 (js/Math.max 10 n))))
    default-blob-size))

(defn parse-color
  "Parse color attribute. Any non-empty string passes through, default #6366f1."
  [s]
  (if (and (string? s) (pos? (.-length (.trim s))))
    (.trim s)
    default-color))

(defn parse-noise-scale
  "Parse noise-scale to float in [0.5, 20], default 3."
  [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (or (js/isNaN n) (not (pos? n)))
        default-noise-scale
        (js/Math.min 20 (js/Math.max 0.5 n))))
    default-noise-scale))

(defn parse-noise-speed
  "Parse noise-speed to float in [0.001, 0.1], default 0.02."
  [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (or (js/isNaN n) (not (pos? n)))
        default-noise-speed
        (js/Math.min 0.1 (js/Math.max 0.001 n))))
    default-noise-speed))

(defn parse-noise-intensity
  "Parse noise-intensity to float in [1, 30], default 6."
  [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (or (js/isNaN n) (not (pos? n)))
        default-noise-intensity
        (js/Math.min 30 (js/Math.max 1 n))))
    default-noise-intensity))

(defn parse-blur
  "Parse blur to number in [4, 40], default 12."
  [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (or (js/isNaN n) (not (pos? n)))
        default-blur
        (js/Math.min 40 (js/Math.max 4 n))))
    default-blur))

(defn parse-threshold
  "Parse threshold to a color-matrix values string. Default metaball matrix."
  [s]
  (if (and (string? s) (pos? (.-length (.trim s))))
    (.trim s)
    default-threshold))

;; ── Palette presets ─────────────────────────────────────────────────────────
(def palette-presets
  {"rainbow" ["#ef4444" "#f59e0b" "#22c55e" "#3b82f6" "#a855f7"]
   "ocean"   ["#06b6d4" "#0ea5e9" "#6366f1" "#8b5cf6" "#0d9488"]
   "sunset"  ["#ef4444" "#f97316" "#eab308" "#ec4899" "#f43f5e"]
   "neon"    ["#22d3ee" "#a78bfa" "#f472b6" "#34d399" "#facc15"]
   "ember"   ["#dc2626" "#ea580c" "#d97706" "#b91c1c" "#f59e0b"]})

(defn parse-palette
  "Parse palette attribute. Returns a vector of color strings, or nil.
   Accepts a named preset (case-insensitive) or comma-separated CSS colors."
  [s]
  (when (and (string? s) (pos? (.-length (.trim s))))
    (let [trimmed (.toLowerCase (.trim s))]
      (if-let [preset (get palette-presets trimmed)]
        preset
        (when (>= (.indexOf s ",") 0)
          (let [parts (.split s ",")]
            (into []
                  (comp (map #(.trim %))
                        (filter #(pos? (.-length %))))
                  parts)))))))

(defn blob-colors
  "Returns a JS array of n color strings. When palette is non-nil, cycles
   palette colors across blobs. Otherwise all blobs get the single color."
  [palette color n]
  (let [out #js []]
    (if (and palette (pos? (count palette)))
      (let [pc (count palette)]
        (dotimes [i n]
          (.push out (nth palette (mod i pc)))))
      (dotimes [_ n]
        (.push out color)))
    out))

;; ── Normalize ───────────────────────────────────────────────────────────────
(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :blob-count-raw      string | nil
    :blob-size-raw       string | nil
    :color-raw           string | nil
    :noise-attr          string | nil  (presence-based boolean)
    :noise-scale-raw     string | nil
    :noise-speed-raw     string | nil
    :noise-intensity-raw string | nil
    :blur-raw            string | nil
    :threshold-raw       string | nil
    :palette-raw         string | nil

  Output keys:
    :blob-count       int
    :blob-size        number
    :color            string
    :palette          vector | nil
    :colors           JS array of color strings (one per blob)
    :noise?           boolean
    :noise-scale      number
    :noise-speed      number
    :noise-intensity  number
    :blur             number
    :threshold        string"
  [{:keys [blob-count-raw blob-size-raw color-raw noise-attr
           noise-scale-raw noise-speed-raw noise-intensity-raw
           blur-raw threshold-raw palette-raw]}]
  (let [bc      (parse-blob-count blob-count-raw)
        color   (parse-color color-raw)
        palette (parse-palette palette-raw)]
    {:blob-count      bc
     :blob-size       (parse-blob-size blob-size-raw)
     :color           color
     :palette         palette
     :colors          (blob-colors palette color bc)
     :noise?          (mu/parse-bool-present noise-attr)
     :noise-scale     (parse-noise-scale noise-scale-raw)
     :noise-speed     (parse-noise-speed noise-speed-raw)
     :noise-intensity (parse-noise-intensity noise-intensity-raw)
     :blur            (parse-blur blur-raw)
     :threshold       (parse-threshold threshold-raw)}))

;; ── Derived computations ────────────────────────────────────────────────────
(defn blob-speeds
  "Returns a JS array of n speed factors using exponential decay.
   Lead blob (index 0) follows at ~0.35, each successive blob is roughly
   half the speed of the previous one, down to a floor of 0.01.
   This creates wide separation so blobs stay visually distinct."
  [n]
  (let [lead  0.35
        decay 0.45
        out   #js []]
    (dotimes [i n]
      (.push out (js/Math.max 0.01 (* lead (js/Math.pow decay i)))))
    out))

(defn blob-sizes
  "Returns a JS array of n sizes. Lead blob gets base-size, trailing blobs shrink."
  [base-size n]
  (let [out #js []]
    (dotimes [i n]
      (.push out (js/Math.max (* base-size 0.5)
                              (* base-size (- 1.0 (* i 0.08))))))
    out))

(defn lerp
  "Linear interpolation: moves current toward target by speed fraction."
  [current target speed]
  (+ current (* (- target current) speed)))
