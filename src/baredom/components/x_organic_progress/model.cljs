(ns baredom.components.x-organic-progress.model
  (:require [goog.object :as gobj]))

;; ── Tag name ────────────────────────────────────────────────────────────────
(def tag-name "x-organic-progress")

;; ── Attribute names ─────────────────────────────────────────────────────────
(def attr-progress "progress")
(def attr-variant  "variant")
(def attr-color    "color")
(def attr-bloom    "bloom")
(def attr-density  "density")
(def attr-seed     "seed")
(def attr-label    "label")

(def observed-attributes
  #js [attr-progress attr-variant attr-color attr-bloom
       attr-density attr-seed attr-label])

;; ── Event names ─────────────────────────────────────────────────────────────
(def event-complete  "x-organic-progress-complete")
(def event-bloom-end "x-organic-progress-bloom-end")

;; ── CSS custom property names ───────────────────────────────────────────────
(def css-color-primary   "--x-organic-progress-color-primary")
(def css-color-secondary "--x-organic-progress-color-secondary")
(def css-bloom-color     "--x-organic-progress-bloom-color")
(def css-bg              "--x-organic-progress-bg")
(def css-branch-width    "--x-organic-progress-branch-width")
(def css-glow            "--x-organic-progress-glow")
(def css-opacity         "--x-organic-progress-opacity")

;; ── Property API ────────────────────────────────────────────────────────────
(def property-api
  {:progress {:type 'string}
   :variant  {:type 'string}
   :color    {:type 'string}
   :bloom    {:type 'boolean}
   :density  {:type 'string}
   :seed     {:type 'number}
   :label    {:type 'string}})

;; ── Event schema ────────────────────────────────────────────────────────────
(def event-schema
  {event-complete  {:detail {:progress 'number} :cancelable false}
   event-bloom-end {:detail {:progress 'number} :cancelable false}})

;; ── Enums and defaults ──────────────────────────────────────────────────────
(def ^:private allowed-variants  #{"vine" "honeycomb"})
(def ^:private allowed-densities #{"sparse" "normal" "dense"})

(def ^:private default-variant "vine")
(def ^:private default-density "normal")
(def ^:private default-seed    42)

;; ── Density → L-system iterations ──────────────────────────────────────────
(def density->iterations
  {"sparse" 2 "normal" 3 "dense" 4})

;; ── Parse functions ─────────────────────────────────────────────────────────

(defn parse-progress
  "Parse progress attribute. Returns nil for indeterminate, or float in [0,100]."
  [s]
  (when (string? s)
    (let [v (.trim s)]
      (when (not= v "")
        (let [n (js/parseFloat v)]
          (when-not (js/isNaN n)
            (js/Math.min 100.0 (js/Math.max 0.0 n))))))))

(defn parse-variant
  "Parse variant attribute. Returns \"vine\" or \"honeycomb\"."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-variants v) v default-variant)))

(defn parse-bloom
  "Parse bloom attribute. Absent (nil) → true, \"false\" → false."
  [s]
  (if (nil? s)
    true
    (not= (.toLowerCase (.trim (str s))) "false")))

(defn parse-density
  "Parse density attribute. Returns \"sparse\", \"normal\", or \"dense\"."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-densities v) v default-density)))

(defn parse-seed
  "Parse seed attribute to a positive integer."
  [s]
  (if (string? s)
    (let [n (js/parseInt (.trim s) 10)]
      (if (and (number? n) (not (js/isNaN n)) (pos? n)) n default-seed))
    default-seed))

(defn parse-label
  "Parse label attribute. Returns trimmed non-empty string or nil."
  [s]
  (when (string? s)
    (let [v (.trim s)]
      (when (not= v "") v))))

(defn parse-color
  "Parse a color attribute. Returns non-empty trimmed string or nil."
  [s]
  (when (string? s)
    (let [v (.trim s)]
      (when (not= v "") v))))

;; ── derive-state ────────────────────────────────────────────────────────────

(defn derive-state
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :progress-raw   string | nil
    :variant-raw    string | nil
    :color-raw      string | nil
    :bloom-attr     string | nil  (nil when attribute absent)
    :density-raw    string | nil
    :seed-raw       string | nil
    :label-raw      string | nil

  Output keys:
    :progress       number | nil  (nil = indeterminate)
    :percent        number        (0-100, 0 if indeterminate)
    :variant        string
    :color          string | nil
    :bloom?         boolean
    :density        string
    :seed           int
    :label          string | nil
    :indeterminate? boolean
    :complete?      boolean
    :aria-valuetext string"
  [{:keys [progress-raw variant-raw color-raw bloom-attr density-raw seed-raw label-raw]}]
  (let [progress (parse-progress progress-raw)
        indeterminate? (nil? progress)
        percent  (or progress 0.0)
        complete? (and (not indeterminate?) (>= percent 100.0))]
    {:progress       progress
     :percent        percent
     :variant        (parse-variant variant-raw)
     :color          (parse-color color-raw)
     :bloom?         (parse-bloom bloom-attr)
     :density        (parse-density density-raw)
     :seed           (parse-seed seed-raw)
     :label          (parse-label label-raw)
     :indeterminate? indeterminate?
     :complete?      complete?
     :aria-valuetext (if indeterminate? "Loading..." (str (js/Math.round percent) "%"))}))

;; ── Seeded PRNG (xorshift32) ────────────────────────────────────────────────

(defn make-rng
  "Create a seeded PRNG state. Returns a mutable JS array [state]."
  [seed]
  (let [s (bit-or (if (and (number? seed) (not (js/isNaN seed)) (pos? seed))
                    (js/Math.floor seed)
                    default-seed)
                  0)]
    #js [(if (zero? s) 1 s)]))

(defn rng-next!
  "Advance the PRNG and return a float in [0, 1)."
  [^js rng]
  (let [x (aget rng 0)
        x (bit-xor x (bit-shift-left x 13))
        x (bit-xor x (unsigned-bit-shift-right x 17))
        x (bit-xor x (bit-shift-left x 5))
        x (bit-or x 0)]
    (aset rng 0 x)
    (/ (js/Math.abs x) 2147483647.0)))

;; ── 2D Simplex noise ────────────────────────────────────────────────────────

(def ^:private perm
  #js [151 160 137 91 90 15 131 13 201 95 96 53 194 233 7 225 140 36
       103 30 69 142 8 99 37 240 21 10 23 190 6 148 247 120 234 75
       0 26 197 62 94 252 219 203 117 35 11 32 57 177 33 88 237 149
       56 87 174 20 125 136 171 168 68 175 74 165 71 134 139 48 27
       166 77 146 158 231 83 111 229 122 60 211 133 230 220 105 92
       41 55 46 245 40 244 102 143 54 65 25 63 161 1 216 80 73 209
       76 132 187 208 89 18 169 200 196 135 130 116 188 159 86 164
       100 109 198 173 186 3 64 52 217 226 250 124 123 5 202 38 147
       118 126 255 82 85 212 207 206 59 227 47 16 58 17 182 189 28
       42 223 183 170 213 119 248 152 2 44 154 163 70 221 153 101
       155 167 43 172 9 129 22 39 253 19 98 108 110 79 113 224 232
       178 185 112 104 218 246 97 228 251 34 242 193 238 210 144 12
       191 179 162 241 81 51 145 235 249 14 239 107 49 192 214 31
       181 199 106 157 184 84 204 176 115 121 50 45 127 4 150 254
       138 236 205 93 222 114 67 29 24 72 243 141 128 195 78 66 215
       61 156 180])

(def ^:private grad3
  #js [#js [1 1] #js [-1 1] #js [1 -1] #js [-1 -1]
       #js [1 0] #js [-1 0] #js [0 1] #js [0 -1]
       #js [1 1] #js [-1 1] #js [1 -1] #js [-1 -1]])

(def ^:private F2 (/ (- (js/Math.sqrt 3.0) 1.0) 2.0))
(def ^:private G2 (/ (- 3.0 (js/Math.sqrt 3.0)) 6.0))

(defn- perm-at [i]
  (aget perm (bit-and i 255)))

(defn simplex-noise-2d
  "2D simplex noise. Returns value in approximately [-1, 1]."
  [x y]
  (let [s    (* (+ x y) F2)
        i    (js/Math.floor (+ x s))
        j    (js/Math.floor (+ y s))
        t    (* (+ i j) G2)
        x0   (- x (- i t))
        y0   (- y (- j t))
        i1   (if (> x0 y0) 1 0)
        j1   (if (> x0 y0) 0 1)
        x1   (- (+ x0 (- i1)) G2)
        y1   (- (+ y0 (- j1)) G2)
        x2   (- (+ x0 -1.0) (* 2.0 G2))
        y2   (- (+ y0 -1.0) (* 2.0 G2))
        ii   (bit-and i 255)
        jj   (bit-and j 255)
        ;; Corner 0
        t0   (- 0.5 (* x0 x0) (* y0 y0))
        n0   (if (< t0 0) 0.0
               (let [t0 (* t0 t0)
                     gi (bit-and (perm-at (+ ii (perm-at jj))) 11)
                     ^js g (aget grad3 gi)]
                 (* t0 t0 (+ (* (aget g 0) x0) (* (aget g 1) y0)))))
        ;; Corner 1
        t1   (- 0.5 (* x1 x1) (* y1 y1))
        n1   (if (< t1 0) 0.0
               (let [t1 (* t1 t1)
                     gi (bit-and (perm-at (+ ii i1 (perm-at (+ jj j1)))) 11)
                     ^js g (aget grad3 gi)]
                 (* t1 t1 (+ (* (aget g 0) x1) (* (aget g 1) y1)))))
        ;; Corner 2
        t2   (- 0.5 (* x2 x2) (* y2 y2))
        n2   (if (< t2 0) 0.0
               (let [t2 (* t2 t2)
                     gi (bit-and (perm-at (+ ii 1 (perm-at (+ jj 1)))) 11)
                     ^js g (aget grad3 gi)]
                 (* t2 t2 (+ (* (aget g 0) x2) (* (aget g 1) y2)))))]
    (* 70.0 (+ n0 n1 n2))))

(defn make-noise-fn
  "Returns a noise function offset by seed for distinct patterns per seed."
  [seed]
  (let [ox (* seed 17.31)
        oy (* seed 31.17)]
    (fn [x y]
      (simplex-noise-2d (+ x ox) (+ y oy)))))

;; ── L-system ────────────────────────────────────────────────────────────────

(defn l-system-iterate
  "Apply L-system rules n times to axiom string. Returns final string."
  [axiom rules n]
  (loop [s axiom
         i 0]
    (if (>= i n)
      s
      (let [sb (js/Array.)]
        (dotimes [k (.-length s)]
          (let [ch (.charAt s k)
                rep (get rules ch)]
            (if rep
              (.push sb rep)
              (.push sb ch))))
        (recur (.join sb "") (inc i))))))

(defn l-system->segments
  "Interpret an L-system string into a flat JS array of segment maps.
   Each segment is #js {x1 y1 x2 y2 depth index}.
   Returns #js [segments total-count].

   Turtle starts at (start-x, start-y) heading rightward (angle 0).
   base-angle is the turn angle in radians.
   base-length is the initial segment length in SVG units."
  [l-string noise-fn seed base-angle base-length start-x start-y]
  (let [segments  #js []
        stack     #js []
        rng       (make-rng seed)
        seg-index #js [0]
        ;; Mutable turtle state as JS arrays for performance
        state     #js [start-x start-y 0.0 0]]  ;; x y angle depth
    (dotimes [k (.-length l-string)]
        (let [ch (.charAt l-string k)]
          (case ch
            "F"
            (let [x     (aget state 0)
                  y     (aget state 1)
                  angle (aget state 2)
                  depth (aget state 3)
                  idx   (aget seg-index 0)
                  len   (* base-length
                           (js/Math.pow 0.7 depth)
                           (+ 0.8 (* 0.4 (rng-next! rng))))
                  perturbed-angle (let [noise-val (noise-fn (* idx 0.3) (* depth 0.5))]
                                    (+ angle (* 0.15 noise-val)))
                  nx    (+ x (* len (js/Math.cos perturbed-angle)))
                  ny    (+ y (* len (js/Math.sin perturbed-angle)))]
              (.push segments
                     #js {"x1" x "y1" y "x2" nx "y2" ny
                          "depth" depth "index" idx})
              (aset state 0 nx)
              (aset state 1 ny)
              (aset seg-index 0 (inc idx)))

            "+"
            (let [noise-val (noise-fn (* (aget seg-index 0) 0.2) 100.0)
                  turn (+ base-angle (* 0.1 noise-val))]
              (aset state 2 (- (aget state 2) turn)))

            "-"
            (let [noise-val (noise-fn (* (aget seg-index 0) 0.2) 200.0)
                  turn (+ base-angle (* 0.1 noise-val))]
              (aset state 2 (+ (aget state 2) turn)))

            "["
            (do (.push stack #js [(aget state 0) (aget state 1)
                                  (aget state 2) (aget state 3)])
                (aset state 3 (inc (aget state 3))))

            "]"
            (when (pos? (.-length stack))
              (let [^js saved (.pop stack)]
                (aset state 0 (aget saved 0))
                (aset state 1 (aget saved 1))
                (aset state 2 (aget saved 2))
                (aset state 3 (aget saved 3))))

            ;; default: ignore unknown characters
            nil)))
      ;; Sort segments by distance from root for radial outward reveal
      (.sort segments (fn [^js a ^js b]
                        (let [dx-a (- (gobj/get a "x2") start-x)
                              dy-a (- (gobj/get a "y2") start-y)
                              dx-b (- (gobj/get b "x2") start-x)
                              dy-b (- (gobj/get b "y2") start-y)]
                          (- (+ (* dx-a dx-a) (* dy-a dy-a))
                             (+ (* dx-b dx-b) (* dy-b dy-b))))))
    #js [segments (.-length segments)]))

;; ── Honeycomb: hexagonal lattice ─────────────────────────────────────────────

(def ^:private density->hex-radius
  {"sparse" 40.0 "normal" 25.0 "dense" 16.0})

(defn- edge-key
  "Create a deduplication key for an edge by sorting endpoint coords."
  [x1 y1 x2 y2]
  (let [k1 (str (.toFixed x1 1) "," (.toFixed y1 1))
        k2 (str (.toFixed x2 1) "," (.toFixed y2 1))]
    (if (neg? (compare k1 k2))
      (str k1 "-" k2)
      (str k2 "-" k1))))

(defn honeycomb-lattice-segments
  "Generate a honeycomb hexagonal lattice. Growth expands from center outward
   with random cell reveal order. Returns #js [segments segment-count]."
  [seed density view-w view-h]
  (let [hex-r       (get density->hex-radius density 25.0)
        center-x    (/ view-w 2.0)
        center-y    (/ view-h 2.0)
        rng         (make-rng seed)
        ;; Flat-topped hexagon geometry
        col-spacing (* 1.5 hex-r)
        row-spacing (* (js/Math.sqrt 3.0) hex-r)
        ;; Grid bounds with padding
        n-cols      (+ (js/Math.ceil (/ view-w col-spacing)) 4)
        n-rows      (+ (js/Math.ceil (/ view-h row-spacing)) 4)
        start-col   -2
        start-row   -2
        ;; Collect cell centers with distance-biased random sort key
        cells       #js []
        max-dist    (js/Math.sqrt (+ (* center-x center-x)
                                     (* center-y center-y)))]
    ;; Generate hex cell centers
    (loop [col start-col]
      (when (< col n-cols)
        (let [cx (* col col-spacing)
              y-off (if (odd? col) (/ row-spacing 2.0) 0.0)]
          (loop [row start-row]
            (when (< row n-rows)
              (let [cy (+ (* row row-spacing) y-off)]
                (when (and (> cx (- 0 hex-r)) (< cx (+ view-w hex-r))
                           (> cy (- 0 hex-r)) (< cy (+ view-h hex-r)))
                  (let [dx (- cx center-x)
                        dy (- cy center-y)
                        dist (js/Math.sqrt (+ (* dx dx) (* dy dy)))
                        sort-key (+ (* dist 0.7)
                                    (* (rng-next! rng) 0.3 max-dist))]
                    (.push cells #js {"cx" cx "cy" cy "dist" dist "key" sort-key}))))
              (recur (inc row)))))
        (recur (inc col))))
    ;; Sort cells by weighted key (center-out with randomness)
    (.sort cells (fn [^js a ^js b]
                   (- (gobj/get a "key") (gobj/get b "key"))))
    ;; Generate edges with deduplication
    (let [seen     #js {}
          segments #js []
          seg-idx  #js [0]]
      (dotimes [ci (.-length cells)]
        (let [^js cell (aget cells ci)
              cx       (gobj/get cell "cx")
              cy       (gobj/get cell "cy")
              dist     (gobj/get cell "dist")
              depth    (js/Math.min 2 (js/Math.floor
                                       (/ (* dist 3.0)
                                          (+ max-dist 0.001))))]
          ;; 6 edges of a flat-topped hexagon
          (dotimes [k 6]
            (let [a1   (* k (/ js/Math.PI 3.0))
                  a2   (* (inc k) (/ js/Math.PI 3.0))
                  vx1  (+ cx (* hex-r (js/Math.cos a1)))
                  vy1  (+ cy (* hex-r (js/Math.sin a1)))
                  vx2  (+ cx (* hex-r (js/Math.cos a2)))
                  vy2  (+ cy (* hex-r (js/Math.sin a2)))
                  ek   (edge-key vx1 vy1 vx2 vy2)]
              (when-not (gobj/get seen ek)
                (gobj/set seen ek true)
                (let [idx (aget seg-idx 0)]
                  (.push segments
                         #js {"x1" vx1 "y1" vy1 "x2" vx2 "y2" vy2
                              "depth" depth "index" idx})
                  (aset seg-idx 0 (inc idx))))))))
      #js [segments (.-length segments)])))

;; ── Spring physics ──────────────────────────────────────────────────────────

(defn spring-step
  "Single step of damped spring physics.
   Returns #js [new-position new-velocity].
   stiffness ~120, damping ~12 for near-critical damping."
  [current target velocity dt stiffness damping]
  (let [force    (- (* stiffness (- target current)) (* damping velocity))
        new-vel  (+ velocity (* force dt))
        new-pos  (+ current (* new-vel dt))]
    #js [new-pos new-vel]))

;; ── Easing ──────────────────────────────────────────────────────────────────

(defn ease-out-cubic
  "Ease-out cubic: 1 - (1-t)^3"
  [t]
  (let [t1 (- 1.0 t)]
    (- 1.0 (* t1 t1 t1))))
