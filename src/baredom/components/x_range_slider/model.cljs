(ns baredom.components.x-range-slider.model)

(def tag-name "x-range-slider")

;; ---------------------------------------------------------------------------
;; Attribute name constants
;; ---------------------------------------------------------------------------
(def attr-start            "start")
(def attr-end              "end")
(def attr-min              "min")
(def attr-max              "max")
(def attr-step             "step")
(def attr-min-gap          "min-gap")
(def attr-disabled         "disabled")
(def attr-readonly         "readonly")
(def attr-name             "name")
(def attr-label            "label")
(def attr-show-value       "show-value")
(def attr-size             "size")
(def attr-aria-label       "aria-label")
(def attr-aria-labelledby  "aria-labelledby")
(def attr-aria-describedby "aria-describedby")

;; ---------------------------------------------------------------------------
;; Event name constants
;; ---------------------------------------------------------------------------
(def event-change-request "x-range-slider-change-request")
(def event-input          "x-range-slider-input")
(def event-change         "x-range-slider-change")

;; ---------------------------------------------------------------------------
;; Defaults and allowed values
;; ---------------------------------------------------------------------------
(def default-min     0)
(def default-max     100)
(def default-step    "1")
(def default-min-gap 0)
(def default-size    "md")

(def allowed-sizes #{"sm" "md" "lg"})

;; ---------------------------------------------------------------------------
;; Observed attributes
;; ---------------------------------------------------------------------------
(def observed-attributes
  #js [attr-start
       attr-end
       attr-min
       attr-max
       attr-step
       attr-min-gap
       attr-disabled
       attr-readonly
       attr-name
       attr-label
       attr-show-value
       attr-size
       attr-aria-label
       attr-aria-labelledby
       attr-aria-describedby])

;; ---------------------------------------------------------------------------
;; API metadata
;; ---------------------------------------------------------------------------
(def property-api
  {:start     {:type 'string  :reflects-attribute attr-start}
   :end       {:type 'string  :reflects-attribute attr-end}
   :min       {:type 'string  :reflects-attribute attr-min}
   :max       {:type 'string  :reflects-attribute attr-max}
   :step      {:type 'string  :reflects-attribute attr-step}
   :minGap    {:type 'string  :reflects-attribute attr-min-gap}
   :disabled  {:type 'boolean :reflects-attribute attr-disabled}
   :readOnly  {:type 'boolean :reflects-attribute attr-readonly}
   :showValue {:type 'boolean :reflects-attribute attr-show-value}
   :name      {:type 'string  :reflects-attribute attr-name}
   :label     {:type 'string  :reflects-attribute attr-label}
   :size      {:type 'string  :reflects-attribute attr-size}})

(def event-schema
  {event-change-request {:cancelable true
                         :detail {:start         'number :end         'number
                                  :previousStart 'number :previousEnd 'number
                                  :min           'number :max         'number}}
   event-input          {:detail {:start 'number :end 'number :min 'number :max 'number}}
   event-change         {:detail {:start 'number :end 'number :min 'number :max 'number}}})

(def method-api {})

;; ---------------------------------------------------------------------------
;; Attribute normalization helpers
;; ---------------------------------------------------------------------------
(defn normalize-number
  [s default-val]
  (if (string? s)
    (let [n (js/parseFloat s)]
      (if (js/isNaN n) default-val n))
    default-val))

(defn normalize-min
  [s]
  (normalize-number s default-min))

(defn normalize-max
  [s]
  (normalize-number s default-max))

(defn normalize-step
  [s]
  (if (= s "any")
    "any"
    (let [n (if (string? s) (js/parseFloat s) js/NaN)]
      (if (and (not (js/isNaN n)) (> n 0))
        (str n)
        default-step))))

(defn normalize-size
  [s]
  (if (and (string? s) (contains? allowed-sizes s))
    s
    default-size))

(defn normalize-min-gap
  [s]
  (max 0 (normalize-number s default-min-gap)))

(defn non-empty
  "The string `s` when it is a non-empty string, otherwise nil."
  [s]
  (when (and (string? s) (not= "" s)) s))

;; ---------------------------------------------------------------------------
;; Numeric helpers
;; ---------------------------------------------------------------------------
(defn step-size
  "Numeric step from a normalized step string. \"any\" or invalid -> nil."
  [norm-step]
  (when (not= "any" norm-step)
    (let [n (js/parseFloat norm-step)]
      (when (and (not (js/isNaN n)) (pos? n)) n))))

(defn snap-to-step
  "Snap `v` to the nearest multiple of `step-num` measured from `min-val`.
   A nil or non-positive step leaves `v` unchanged. Does not clamp. The
   result is rounded to 9 decimals to suppress floating-point noise."
  [v min-val step-num]
  (if (or (nil? step-num) (<= step-num 0))
    v
    (let [snapped (+ min-val (* step-num (js/Math.round (/ (- v min-val) step-num))))]
      (/ (js/Math.round (* snapped 1e9)) 1e9))))

(defn clamp
  "Clamp `v` into [lo, hi]. When hi < lo (degenerate range) returns lo."
  [v lo hi]
  (if (< hi lo)
    lo
    (-> v (max lo) (min hi))))

(defn clamp-start
  "Clamp a proposed start value into [min-val, end-val - gap]."
  [proposed min-val end-val gap]
  (clamp proposed min-val (- end-val gap)))

(defn clamp-end
  "Clamp a proposed end value into [start-val + gap, max-val]."
  [proposed start-val max-val gap]
  (clamp proposed (+ start-val gap) max-val))

;; ---------------------------------------------------------------------------
;; Pointer-coordinate transforms
;; ---------------------------------------------------------------------------
(defn client-x->ratio
  "Pointer clientX + track rect -> ratio in [0,1]."
  [client-x rect-left rect-width]
  (if (> rect-width 0)
    (-> (/ (- client-x rect-left) rect-width) (max 0.0) (min 1.0))
    0.0))

(defn client-x->value
  "Pointer clientX + track rect -> raw (unsnapped) value in [min-val, max-val]."
  [client-x rect-left rect-width min-val max-val]
  (+ min-val (* (client-x->ratio client-x rect-left rect-width)
                (- max-val min-val))))

;; ---------------------------------------------------------------------------
;; Track geometry
;; ---------------------------------------------------------------------------
(defn fill-percent
  "Position of `value` along [min-val, max-val] as a percentage 0-100."
  [value min-val max-val]
  (if (> max-val min-val)
    (-> (* 100.0 (/ (- value min-val) (- max-val min-val)))
        (max 0.0)
        (min 100.0))
    0.0))

(defn segment-geometry
  "Left% and width% of the highlighted track segment between the two thumbs."
  [start-val end-val min-val max-val]
  (let [s (fill-percent start-val min-val max-val)
        e (fill-percent end-val   min-val max-val)]
    {:left s :width (max 0.0 (- e s))}))

(defn nearest-thumb
  "Which thumb (:start / :end) sits closest to `clicked-val`. On a tie —
   which includes coincident thumbs — a click at or below the start value
   picks :start, otherwise :end."
  [clicked-val start-val end-val]
  (let [ds (js/Math.abs (- clicked-val start-val))
        de (js/Math.abs (- clicked-val end-val))]
    (cond
      (< ds de)                  :start
      (> ds de)                  :end
      (<= clicked-val start-val) :start
      :else                      :end)))

;; ---------------------------------------------------------------------------
;; Keyboard
;; ---------------------------------------------------------------------------
(defn key-target
  "Pure: the proposed (unclamped) value for a thumb keydown. Arrow keys move
   by one step (ten with shift), Page keys by ten, Home/End jump to bounds."
  [key shift? cur min-val max-val step-num]
  (let [big   (* step-num 10)
        arrow (if shift? big step-num)]
    (case key
      "ArrowRight" (+ cur arrow)
      "ArrowUp"    (+ cur arrow)
      "ArrowLeft"  (- cur arrow)
      "ArrowDown"  (- cur arrow)
      "PageUp"     (+ cur big)
      "PageDown"   (- cur big)
      "Home"       min-val
      "End"        max-val
      cur)))

;; ---------------------------------------------------------------------------
;; Range resolution
;; ---------------------------------------------------------------------------
(defn resolve-range
  "Resolve a raw start/end pair into a clamped, gap-respecting range within
   [min-val, max-val]. No swapping: `end` is the dependent value, so it is
   pulled up to satisfy the gap; only when that would exceed `max-val` does
   `start` give way and get pulled down."
  [raw-start raw-end min-val max-val gap]
  (let [s0 (clamp raw-start min-val max-val)
        e0 (clamp raw-end   min-val max-val)
        e1 (max e0 s0)
        e2 (min max-val (max e1 (+ s0 gap)))
        s1 (if (< (- e2 s0) gap)
             (max min-val (- e2 gap))
             s0)]
    {:start s1 :end e2}))

;; ---------------------------------------------------------------------------
;; View-model builder
;; ---------------------------------------------------------------------------
(defn normalize
  "Build the stable, value-comparable view-model from raw attribute strings."
  [{min-raw :min max-raw :max
    :keys [start end step min-gap disabled readonly name label show-value size
           aria-label aria-labelledby aria-describedby]}]
  (let [norm-min   (normalize-min min-raw)
        norm-max   (max norm-min (normalize-max max-raw))
        norm-step  (normalize-step step)
        step-num   (step-size norm-step)
        gap        (min (normalize-min-gap min-gap) (- norm-max norm-min))
        raw-start  (normalize-number start norm-min)
        raw-end    (normalize-number end   norm-max)
        resolved   (resolve-range raw-start raw-end norm-min norm-max gap)
        snap-start (clamp (snap-to-step (:start resolved) norm-min step-num)
                          norm-min norm-max)
        snap-end   (clamp (snap-to-step (:end resolved) norm-min step-num)
                          norm-min norm-max)
        final      (resolve-range snap-start snap-end norm-min norm-max gap)
        norm-start (:start final)
        norm-end   (:end final)
        seg        (segment-geometry norm-start norm-end norm-min norm-max)]
    {:start            norm-start
     :end              norm-end
     :min              norm-min
     :max              norm-max
     :step             norm-step
     :min-gap          gap
     :size             (normalize-size size)
     :disabled?        (boolean disabled)
     :readonly?        (boolean readonly)
     :show-value?      (boolean show-value)
     :name             (non-empty name)
     :label            (non-empty label)
     :start-pct        (fill-percent norm-start norm-min norm-max)
     :end-pct          (fill-percent norm-end   norm-min norm-max)
     :fill-left        (:left seg)
     :fill-width       (:width seg)
     :aria-label       (non-empty aria-label)
     :aria-labelledby  (non-empty aria-labelledby)
     :aria-describedby (non-empty aria-describedby)}))

;; ---------------------------------------------------------------------------
;; Derived helpers
;; ---------------------------------------------------------------------------
(defn interactable?
  "True when the slider accepts user interaction (not disabled, not readonly)."
  [{:keys [disabled? readonly?]}]
  (boolean (and (not disabled?) (not readonly?))))

(defn value-text
  "Header display text for the current range."
  [start end]
  (str start " – " end))

(defn make-detail
  [start end min-val max-val]
  {:start start :end end :min min-val :max max-val})

(defn make-change-request-detail
  [start end prev-start prev-end min-val max-val]
  {:start         start      :end         end
   :previousStart prev-start :previousEnd prev-end
   :min           min-val    :max         max-val})
