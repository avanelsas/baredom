(ns baredom.components.x-chart.model)

(def tag-name "x-chart")

(def attr-type      "type")
(def attr-data      "data")
(def attr-height    "height")
(def attr-padding   "padding")
(def attr-x-format  "x-format")
(def attr-y-format  "y-format")
(def attr-grid      "grid")
(def attr-axes      "axes")
(def attr-tooltip   "tooltip")
(def attr-cursor    "cursor")
(def attr-disabled  "disabled")
(def attr-loading   "loading")
(def attr-selected  "selected")

(def observed-attributes
  #js ["type" "data" "height" "padding" "x-format" "y-format"
       "grid" "axes" "tooltip" "cursor" "disabled" "loading" "selected"])

(def default-height  180)
(def default-padding 12)

(def allowed-types    #{"line" "bar" "area"})
(def allowed-cursors  #{"nearest" "x" "none"})

(def max-tooltip-rows  8)
(def dot-r             4)
(def tooltip-edge-pad  8)
(def tooltip-offset    12)

(def event-select "x-chart-select")
(def event-hover  "x-chart-hover")

(def property-api
  {:type     {:type 'string :reflects-attribute attr-type}
   :height   {:type 'number :reflects-attribute attr-height}
   :padding  {:type 'number :reflects-attribute attr-padding}
   :grid     {:type 'boolean :reflects-attribute attr-grid}
   :axes     {:type 'boolean :reflects-attribute attr-axes}
   :tooltip  {:type 'boolean :reflects-attribute attr-tooltip}
   :cursor   {:type 'string :reflects-attribute attr-cursor}
   :disabled {:type 'boolean :reflects-attribute attr-disabled}
   :loading  {:type 'boolean :reflects-attribute attr-loading}
   :selected {:type 'string :reflects-attribute attr-selected}
   :data     {:type 'array}})

;; ---- Pure parsing helpers ----

(defn parse-type [s]
  (if (and (string? s) (contains? allowed-types s)) s "line"))

(defn parse-cursor [s]
  (if (and (string? s) (contains? allowed-cursors s)) s "nearest"))

(defn parse-bool-attr
  "Returns true if attr is present (non-nil) and not literally \"false\"."
  [s]
  (and (some? s) (not= s "false")))

(defn parse-int-pos
  "Parse a positive integer string, returning default-val on failure."
  [s default-val]
  (if (string? s)
    (let [n (js/parseInt s 10)]
      (if (and (not (js/isNaN n)) (pos? n)) n default-val))
    default-val))

(defn- parse-point [raw-pt]
  (when (and raw-pt
             (some? (.-x raw-pt))
             (some? (.-y raw-pt)))
    {:x (.-x raw-pt) :y (.-y raw-pt)}))

(defn- parse-one-series [raw-s i]
  (let [id    (or (and (.-id raw-s) (str (.-id raw-s))) (str "s" i))
        label (or (and (.-label raw-s) (str (.-label raw-s))) id)
        color (and (.-color raw-s) (str (.-color raw-s)))
        data  (.-data raw-s)]
    (when (array? data)
      {:id    id
       :label label
       :color color
       :data  (keep-indexed (fn [_j pt] (parse-point pt)) (array-seq data))})))

(defn parse-series-data
  "Parse JSON string into a vector of series maps. Returns [] on error."
  [json-str]
  (if-not (string? json-str)
    []
    (try
      (let [parsed (js/JSON.parse json-str)]
        (cond
          (array? parsed)
          (vec (keep-indexed (fn [i s] (parse-one-series s i)) (array-seq parsed)))

          ;; Single-series shorthand: [{:x :y}]
          (and (object? parsed) (array? (.-data parsed)))
          (let [s (parse-one-series parsed 0)]
            (if s [s] []))

          :else []))
      (catch :default _e []))))

;; ---- Domain computation ----

(defn x-kind
  "Returns \"category\" when x values are strings, otherwise \"numeric\"."
  [series]
  (let [first-pt (some-> series first :data first)]
    (if (and first-pt (string? (:x first-pt)))
      "category"
      "numeric")))

(defn domain-y
  "Returns [min-y max-y] across all series data points, or [0 1] if empty."
  [series]
  (let [ys (mapcat (fn [s] (map :y (:data s))) series)]
    (if (empty? ys)
      [0 1]
      [(apply min ys) (apply max ys)])))

(defn domain-x
  "Returns [min-x max-x] for numeric x, or category count for category x."
  [series]
  (let [kind (x-kind series)]
    (if (= kind "category")
      (let [n (apply max 0 (map (fn [s] (count (:data s))) series))]
        [0 (dec (max 1 n))])
      (let [xs (mapcat (fn [s] (map :x (:data s))) series)]
        (if (empty? xs)
          [0 1]
          [(apply min xs) (apply max xs)])))))

;; ---- Nice tick computation ----

(defn nice-step
  "Compute a \"nice\" step size for approximately n ticks over span."
  [span n]
  (if (or (zero? span) (zero? n))
    1
    (let [raw  (/ span n)
          mag  (js/Math.pow 10 (js/Math.floor (js/Math.log10 raw)))
          norm (/ raw mag)]
      (* mag (cond
               (<= norm 1)   1
               (<= norm 2)   2
               (<= norm 2.5) 2.5
               (<= norm 5)   5
               :else         10)))))

(defn ticks-y
  "Returns a vector of ~n nice tick values between mn and mx."
  [mn mx n]
  (if (= mn mx)
    [mn]
    (let [span (- mx mn)
          step (nice-step span n)
          start (* (js/Math.ceil (/ mn step)) step)
          end   (* (js/Math.floor (/ mx step)) step)]
      (loop [v start acc []]
        (if (> v (+ end (* step 0.0001)))
          acc
          (recur (+ v step) (conj acc v)))))))

;; ---- Normalize (view-model) ----

(defn parse-selected
  "Parse selected attr string \"seriesId:index\" into map, or nil."
  [s]
  (when (string? s)
    (let [parts (.split s ":")]
      (when (= 2 (alength parts))
        (let [idx (js/parseInt (aget parts 1) 10)]
          (when-not (js/isNaN idx)
            {:series-id (aget parts 0) :index idx}))))))

(defn normalize
  [{:keys [type-raw data-raw height-raw padding-raw
           grid-present? axes-raw tooltip-present? cursor-raw
           disabled-present? loading-present? selected-raw
           x-format-raw y-format-raw]}]
  (let [series  (parse-series-data data-raw)
        [ymn ymx] (domain-y series)]
    {:type      (parse-type type-raw)
     :height    (parse-int-pos height-raw default-height)
     :padding   (parse-int-pos padding-raw default-padding)
     :grid?     (if (nil? grid-present?) true (parse-bool-attr grid-present?))
     :axes?     (if (nil? axes-raw) true (parse-bool-attr axes-raw))
     :tooltip?  (parse-bool-attr tooltip-present?)
     :cursor    (parse-cursor cursor-raw)
     :disabled? (parse-bool-attr disabled-present?)
     :loading?  (parse-bool-attr loading-present?)
     :series    series
     :x-kind    (x-kind series)
     :y-domain  [ymn ymx]
     :y-ticks   (ticks-y ymn ymx 5)
     :selected  (parse-selected selected-raw)
     :x-fmt     x-format-raw
     :y-fmt     y-format-raw}))

;; ---- Value formatting ----

(defn format-value
  "Format a numeric value v using fmt-str spec.
   fmt-str: nil | \"int\" | \"pct\" | \"abbr\" | \"fixed:N\" | \"raw\""
  [v fmt-str]
  (cond
    (nil? fmt-str)         (let [abs (js/Math.abs v)]
                             (cond
                               (>= abs 1000000) (str (js/Math.round (/ v 1000000)) "M")
                               (>= abs 1000)    (str (js/Math.round (/ v 1000)) "K")
                               (js/Number.isInteger v) (str v)
                               :else (.toFixed v 1)))
    (= fmt-str "int")      (str (js/Math.round v))
    (= fmt-str "pct")      (str v "%")
    (= fmt-str "abbr")     (let [abs (js/Math.abs v)]
                             (cond
                               (>= abs 1000000000) (str (.toFixed (/ v 1000000000) 1) "B")
                               (>= abs 1000000)    (str (.toFixed (/ v 1000000) 1) "M")
                               (>= abs 1000)       (str (.toFixed (/ v 1000) 1) "K")
                               :else               (str v)))
    (= fmt-str "raw")      (str v)
    (.startsWith fmt-str "fixed:")
    (let [n (js/parseInt (.slice fmt-str 6) 10)]
      (if (js/isNaN n) (str v) (.toFixed v n)))
    :else (str v)))

;; ---- Tooltip pure helpers ----

(defn tooltip-position
  "Compute tooltip position with edge-safe flip logic.
   px/py = cursor position; tw/th = tooltip size; W/H = container size.
   Returns {:left l :top t}."
  [px py tw th W H edge-pad offset]
  (let [left (if (> (+ px tw offset edge-pad) W)
               (- px tw offset)
               (+ px offset))
        top  (if (> (+ py th edge-pad) H)
               (- py th edge-pad)
               (- py 4))]
    {:left (max edge-pad (min left (- W tw edge-pad)))
     :top  (max edge-pad (min top  (- H th edge-pad)))}))

(defn find-pts-at-x
  "For cursor='x': find one point per series closest to mx.
   all-pts is a flat seq of points with :id and :px keys."
  [all-pts mx]
  (let [by-id (group-by :id all-pts)]
    (vec
     (keep
      (fn [[_id pts]]
        (reduce
         (fn [best pt]
           (let [d (js/Math.abs (- (:px pt) mx))]
             (if (or (nil? best) (< d (:d best)))
               (assoc pt :d d)
               best)))
         nil
         pts))
      by-id))))
