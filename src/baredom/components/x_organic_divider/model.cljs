(ns baredom.components.x-organic-divider.model)

(def tag-name "x-organic-divider")

(def attr-shape     "shape")
(def attr-layers    "layers")
(def attr-height    "height")
(def attr-flip      "flip")
(def attr-mirror    "mirror")
(def attr-animation "animation")
(def attr-path      "path")

(def observed-attributes
  #js [attr-shape attr-layers attr-height attr-flip attr-mirror attr-animation attr-path])

(def property-api
  {:shape     {:type 'string  :reflects-attribute attr-shape}
   :layers    {:type 'string  :reflects-attribute attr-layers}
   :height    {:type 'string  :reflects-attribute attr-height}
   :flip      {:type 'boolean :reflects-attribute attr-flip}
   :mirror    {:type 'boolean :reflects-attribute attr-mirror}
   :animation {:type 'string  :reflects-attribute attr-animation}
   :path      {:type 'string  :reflects-attribute attr-path}})

(def event-schema {})

(def default-shape     "wave")
(def default-layers    "1")
(def default-height    "120px")
(def default-animation "none")

(def max-layers 5)

(def allowed-animations #{"none" "drift" "morph"})

(def allowed-shapes
  #{"wave" "waves" "blob-edge" "mountain" "drip" "slant" "scallop" "cloud"})

;; ViewBox: 0 0 1200 120
;; Each path defines the top edge of a filled region extending down to y=120.
(def shape-presets
  {"wave"      {:path     "M0,60 C200,20 400,100 600,60 C800,20 1000,100 1200,60 L1200,120 L0,120 Z"
                :path-alt "M0,45 C200,85 400,15 600,55 C800,90 1000,10 1200,50 L1200,120 L0,120 Z"}

   "waves"     {:path     "M0,40 C150,80 300,0 450,40 C600,80 750,10 900,50 C1050,90 1150,30 1200,50 L1200,120 L0,120 Z"
                :path-alt "M0,55 C150,15 300,85 450,50 C600,10 750,80 900,35 C1050,5 1150,75 1200,40 L1200,120 L0,120 Z"}

   "blob-edge" {:path     "M0,50 C80,30 160,70 240,45 C320,20 400,80 500,55 C600,30 680,75 780,40 C880,5 960,65 1060,50 C1120,40 1180,60 1200,45 L1200,120 L0,120 Z"
                :path-alt "M0,40 C80,65 160,25 240,60 C320,80 400,20 500,45 C600,70 680,15 780,55 C880,75 960,10 1060,35 C1120,55 1180,30 1200,55 L1200,120 L0,120 Z"}

   "mountain"  {:path     "M0,90 C100,90 200,30 350,30 C500,30 450,85 600,85 C750,85 700,15 850,15 C1000,15 950,80 1100,80 C1150,80 1200,60 1200,60 L1200,120 L0,120 Z"
                :path-alt "M0,80 C100,80 200,45 350,45 C500,45 450,75 600,75 C750,75 700,30 850,30 C1000,30 950,70 1100,70 C1150,70 1200,50 1200,50 L1200,120 L0,120 Z"}

   "drip"      {:path     "M0,20 C50,20 100,20 150,20 C175,20 185,60 200,80 C215,100 225,100 240,80 C255,60 265,20 290,20 L450,20 C475,20 485,50 500,70 C515,90 525,90 540,70 C555,50 565,20 590,20 L800,20 C825,20 835,70 850,90 C865,110 875,110 890,90 C905,70 915,20 940,20 L1200,20 L1200,120 L0,120 Z"
                :path-alt "M0,20 C50,20 100,20 170,20 C195,20 205,55 220,72 C235,90 245,90 260,72 C275,55 285,20 310,20 L470,20 C495,20 505,45 520,62 C535,80 545,80 560,62 C575,45 585,20 610,20 L820,20 C845,20 855,65 870,85 C885,105 895,105 910,85 C925,65 935,20 960,20 L1200,20 L1200,120 L0,120 Z"}

   "slant"     {:path     "M0,100 C300,90 600,30 900,10 C1000,5 1100,2 1200,0 L1200,120 L0,120 Z"
                :path-alt "M0,80 C300,70 600,50 900,30 C1000,20 1100,10 1200,20 L1200,120 L0,120 Z"}

   "scallop"   {:path     "M0,60 Q75,0 150,60 Q225,120 300,60 Q375,0 450,60 Q525,120 600,60 Q675,0 750,60 Q825,120 900,60 Q975,0 1050,60 Q1125,120 1200,60 L1200,120 L0,120 Z"
                :path-alt "M0,50 Q75,110 150,50 Q225,-10 300,50 Q375,110 450,50 Q525,-10 600,50 Q675,110 750,50 Q825,-10 900,50 Q975,110 1050,50 Q1125,-10 1200,50 L1200,120 L0,120 Z"}

   "cloud"     {:path     "M0,80 C50,80 80,40 140,40 C180,40 200,60 220,60 C240,60 260,20 320,20 C380,20 400,50 440,50 C480,50 500,10 560,10 C620,10 640,40 700,40 C760,40 780,25 840,25 C900,25 920,55 960,55 C1000,55 1040,30 1100,30 C1140,30 1180,60 1200,60 L1200,120 L0,120 Z"
                :path-alt "M0,70 C50,70 80,50 140,50 C180,50 200,70 220,70 C240,70 260,30 320,30 C380,30 400,60 440,60 C480,60 500,20 560,20 C620,20 640,50 700,50 C760,50 780,35 840,35 C900,35 920,65 960,65 C1000,65 1040,40 1100,40 C1140,40 1180,70 1200,70 L1200,120 L0,120 Z"}})

;; ── Drift extension ──────────────────────────────────────────────────────
;; For drift animation, we extend the path to 1800 units wide (1.5x) so that
;; translating -33.333% creates a seamless loop.
(def drift-presets
  {"wave"      "M0,60 C200,20 400,100 600,60 C800,20 1000,100 1200,60 C1400,20 1600,100 1800,60 L1800,120 L0,120 Z"
   "waves"     "M0,40 C150,80 300,0 450,40 C600,80 750,10 900,50 C1050,90 1150,30 1200,50 C1350,80 1500,0 1650,40 C1800,80 1800,50 1800,50 L1800,120 L0,120 Z"
   "blob-edge" "M0,50 C80,30 160,70 240,45 C320,20 400,80 500,55 C600,30 680,75 780,40 C880,5 960,65 1060,50 C1120,40 1180,60 1200,45 C1280,30 1360,70 1440,45 C1520,20 1600,80 1700,55 C1800,30 1800,50 1800,50 L1800,120 L0,120 Z"
   "mountain"  "M0,90 C100,90 200,30 350,30 C500,30 450,85 600,85 C750,85 700,15 850,15 C1000,15 950,80 1100,80 C1150,80 1200,60 1300,60 C1400,60 1400,30 1500,30 C1600,30 1550,80 1700,80 C1750,80 1800,60 1800,60 L1800,120 L0,120 Z"
   "drip"      "M0,20 C50,20 100,20 150,20 C175,20 185,60 200,80 C215,100 225,100 240,80 C255,60 265,20 290,20 L450,20 C475,20 485,50 500,70 C515,90 525,90 540,70 C555,50 565,20 590,20 L800,20 C825,20 835,70 850,90 C865,110 875,110 890,90 C905,70 915,20 940,20 L1200,20 C1225,20 1235,60 1250,80 C1265,100 1275,100 1290,80 C1305,60 1315,20 1340,20 L1500,20 C1525,20 1535,50 1550,70 C1565,90 1575,90 1590,70 C1605,50 1615,20 1640,20 L1800,20 L1800,120 L0,120 Z"
   "slant"     "M0,100 C300,90 600,30 900,10 C1000,5 1100,2 1200,0 C1300,2 1500,90 1800,100 L1800,120 L0,120 Z"
   "scallop"   "M0,60 Q75,0 150,60 Q225,120 300,60 Q375,0 450,60 Q525,120 600,60 Q675,0 750,60 Q825,120 900,60 Q975,0 1050,60 Q1125,120 1200,60 Q1275,0 1350,60 Q1425,120 1500,60 Q1575,0 1650,60 Q1725,120 1800,60 L1800,120 L0,120 Z"
   "cloud"     "M0,80 C50,80 80,40 140,40 C180,40 200,60 220,60 C240,60 260,20 320,20 C380,20 400,50 440,50 C480,50 500,10 560,10 C620,10 640,40 700,40 C760,40 780,25 840,25 C900,25 920,55 960,55 C1000,55 1040,30 1100,30 C1140,30 1180,60 1200,60 C1240,60 1260,20 1320,20 C1380,20 1400,50 1440,50 C1480,50 1500,10 1560,10 C1620,10 1640,40 1700,40 C1760,40 1780,60 1800,60 L1800,120 L0,120 Z"})

;; ── Layer offset parameters ──────────────────────────────────────────────
(def layer-x-offset 20)
(def layer-y-offset 8)

;; ── Normalizers ──────────────────────────────────────────────────────────

(defn normalize-shape [s]
  (if (and (string? s) (contains? allowed-shapes s))
    s
    default-shape))

(defn normalize-layers [s]
  (let [n (when (string? s) (js/parseInt s 10))]
    (cond
      (or (nil? n) (js/isNaN n) (< n 1)) 1
      (> n max-layers)                    max-layers
      :else                               n)))

(defn normalize-animation [s]
  (if (and (string? s) (contains? allowed-animations s))
    s
    default-animation))

(defn normalize-height [s]
  (if (and (string? s) (not= "" (.trim s)))
    (.trim s)
    default-height))

(defn normalize-path [s]
  (when (and (string? s) (not= "" (.trim s)))
    (.trim s)))

(defn normalize-boolean [v]
  (some? v))

;; ── Resolvers ────────────────────────────────────────────────────────────

(defn resolve-path
  "Returns the effective SVG path d-string. Custom path overrides preset."
  [custom-path shape]
  (if (some? custom-path)
    custom-path
    (get-in shape-presets [(normalize-shape shape) :path])))

(defn resolve-path-alt
  "Returns morph target path. nil when using custom path."
  [custom-path shape]
  (when (nil? custom-path)
    (get-in shape-presets [(normalize-shape shape) :path-alt])))

(defn resolve-drift-path
  "Returns the extended drift path for animation. Falls back to normal path."
  [custom-path shape]
  (if (some? custom-path)
    custom-path
    (get drift-presets (normalize-shape shape))))

(defn derive-layer-transforms
  "Returns a vector of {:x-offset :y-offset} for n layers."
  [n]
  (mapv (fn [i] {:x-offset (* i layer-x-offset)
                 :y-offset (* i layer-y-offset)})
        (range n)))

(defn derive-state [{:keys [shape layers height flip mirror animation path]}]
  (let [norm-path      (normalize-path path)
        norm-shape     (normalize-shape shape)
        norm-layers    (normalize-layers layers)
        norm-animation (normalize-animation animation)
        path-d         (resolve-path norm-path norm-shape)
        path-alt       (resolve-path-alt norm-path norm-shape)
        drift-d        (resolve-drift-path norm-path norm-shape)
        ;; morph requires path-alt (disabled with custom path)
        effective-anim (if (and (= norm-animation "morph") (nil? path-alt))
                         "none"
                         norm-animation)]
    {:shape      norm-shape
     :path       norm-path
     :path-d     path-d
     :path-alt   path-alt
     :drift-d    drift-d
     :layers     norm-layers
     :transforms (derive-layer-transforms norm-layers)
     :height     (normalize-height height)
     :flip       (normalize-boolean flip)
     :mirror     (normalize-boolean mirror)
     :animation  effective-anim}))
