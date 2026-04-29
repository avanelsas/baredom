(ns baredom.components.x-gaussian-blur.model)

(def tag-name "x-gaussian-blur")

(def attr-colors    "colors")
(def attr-blur      "blur")
(def attr-speed     "speed")
(def attr-count     "count")
(def attr-size      "size")
(def attr-opacity   "opacity")
(def attr-animation "animation")
(def attr-blend     "blend")
(def attr-paused    "paused")

(def observed-attributes
  #js [attr-colors attr-blur attr-speed attr-count attr-size
       attr-opacity attr-animation attr-blend attr-paused])

(def property-api
  {:colors    {:type 'string  :reflects-attribute attr-colors}
   :blur      {:type 'string  :reflects-attribute attr-blur}
   :speed     {:type 'string  :reflects-attribute attr-speed}
   :count     {:type 'string  :reflects-attribute attr-count}
   :size      {:type 'string  :reflects-attribute attr-size}
   :opacity   {:type 'string  :reflects-attribute attr-opacity}
   :animation {:type 'string  :reflects-attribute attr-animation}
   :blend     {:type 'string  :reflects-attribute attr-blend}
   :paused    {:type 'boolean :reflects-attribute attr-paused}})

(def event-schema {})

;; ── Defaults ─────────────────────────────────────────────────────────────
(def default-colors    "#6366f1, #ec4899, #14b8a6, #f59e0b")
(def default-blur      60)
(def default-speed-s   10)
(def default-count     5)
(def default-size-pct  50)
(def default-opacity   0.7)
(def default-animation "float")
(def default-blend     "normal")

(def allowed-animations #{"float" "pulse" "none"})
(def allowed-blends     #{"normal" "multiply" "screen" "overlay" "soft-light"})

(def speed-presets
  {"slow"   18
   "medium" 10
   "fast"   5})

(def size-presets
  {"small"  30
   "medium" 50
   "large"  70})

;; ── Parsing ──────────────────────────────────────────────────────────────

(defn parse-colors
  "Splits comma-separated color string into a vector of trimmed, non-empty strings."
  [s]
  (if (and (string? s) (not= "" (.trim s)))
    (let [parts (.split (.trim s) #",")]
      (into []
            (comp (map #(.trim %))
                  (filter #(not= "" %)))
            parts))
    (parse-colors default-colors)))

(defn parse-blur
  "Parses blur radius to a positive number. Returns default on invalid input."
  [s]
  (if (and (string? s) (not= "" (.trim s)))
    (let [n (js/parseFloat (.trim s))]
      (if (and (js/isFinite n) (pos? n)) n default-blur))
    default-blur))

(defn parse-speed
  "Parses speed enum (slow/medium/fast) or raw number (seconds). Returns seconds."
  [s]
  (if (and (string? s) (not= "" (.trim s)))
    (let [trimmed (.trim s)
          preset  (get speed-presets trimmed)]
      (if preset
        preset
        (let [n (js/parseFloat trimmed)]
          (if (and (js/isFinite n) (pos? n)) n default-speed-s))))
    default-speed-s))

(defn parse-count
  "Parses blob count, clamped to 1-12."
  [s]
  (if (and (string? s) (not= "" (.trim s)))
    (let [n (js/parseInt (.trim s) 10)]
      (if (js/isFinite n)
        (min 12 (max 1 n))
        default-count))
    default-count))

(defn parse-size
  "Parses size enum (small/medium/large) or percentage number. Returns percentage."
  [s]
  (if (and (string? s) (not= "" (.trim s)))
    (let [trimmed (.trim s)
          preset  (get size-presets trimmed)]
      (if preset
        preset
        (let [n (js/parseFloat trimmed)]
          (if (and (js/isFinite n) (pos? n)) (min 100 (max 10 n)) default-size-pct))))
    default-size-pct))

(defn parse-opacity
  "Parses opacity float, clamped to 0-1."
  [s]
  (if (and (string? s) (not= "" (.trim s)))
    (let [n (js/parseFloat (.trim s))]
      (if (js/isFinite n) (min 1.0 (max 0.0 n)) default-opacity))
    default-opacity))

(defn normalize-animation [s]
  (if (and (string? s) (contains? allowed-animations s))
    s
    default-animation))

(defn normalize-blend [s]
  (if (and (string? s) (contains? allowed-blends s))
    s
    default-blend))

(defn parse-paused
  "Boolean attribute: present (any value including empty) = true, absent (nil) = false."
  [s]
  (some? s))

;; ── Blob layout (deterministic) ──────────────────────────────────────────

(def ^:private golden-angle 2.39996323)

(defn blob-layout
  "Generates a vector of blob descriptor maps. Deterministic: same inputs, same output."
  [colors cnt size-pct]
  (let [n-colors (count colors)]
    (into []
          (map (fn [i]
                 (let [theta  (* i golden-angle)
                       ;; Distribute blobs in a spread around center
                       x      (+ 50 (* 30 (js/Math.cos theta)))
                       y      (+ 50 (* 30 (js/Math.sin theta)))
                       ;; Offset for more organic placement
                       ox     (* 10 (js/Math.sin (* i golden-angle)))
                       oy     (* 10 (js/Math.cos (* i js/Math.PI)))
                       ;; Size variation: 70%-130% of base
                       sin-v  (js/Math.sin (* i 1.618))
                       scale  (+ 0.7 (* sin-v sin-v 0.6))
                       ;; Duration variation: 80%-120% of base
                       dur-f  (+ 0.8 (* 0.4 (js/Math.sin (* i 1.3))))
                       ;; Negative delay so each blob starts at different phase
                       delay-f (- (/ i (max cnt 1)))]
                   {:color          (nth colors (mod i n-colors))
                    :x              (+ x ox)
                    :y              (+ y oy)
                    :size           (* size-pct scale)
                    :anim-index     (mod i 4)
                    :duration-factor dur-f
                    :delay-factor    delay-f})))
          (range cnt))))

;; ── Derive state ─────────────────────────────────────────────────────────

(defn derive-state
  [{:keys [colors blur speed count size opacity animation blend paused]}]
  (let [parsed-colors (parse-colors colors)
        parsed-count  (parse-count count)
        parsed-size   (parse-size size)
        parsed-speed  (parse-speed speed)
        norm-anim     (normalize-animation animation)]
    {:colors    parsed-colors
     :blur      (parse-blur blur)
     :speed-s   parsed-speed
     :count     parsed-count
     :size-pct  parsed-size
     :opacity   (parse-opacity opacity)
     :animation norm-anim
     :blend     (normalize-blend blend)
     :paused    (parse-paused paused)
     :blobs     (blob-layout parsed-colors parsed-count parsed-size)}))

(def method-api nil)
