(ns baredom.components.x-rating.model)

(def tag-name "x-rating")

;; ---------------------------------------------------------------------------
;; Attribute name constants
;; ---------------------------------------------------------------------------
(def attr-value            "value")
(def attr-max              "max")
(def attr-precision        "precision")
(def attr-shape            "shape")
(def attr-allow-clear      "allow-clear")
(def attr-disabled         "disabled")
(def attr-readonly         "readonly")
(def attr-name             "name")
(def attr-label            "label")
(def attr-size             "size")
(def attr-aria-label       "aria-label")
(def attr-aria-labelledby  "aria-labelledby")
(def attr-aria-describedby "aria-describedby")

;; ---------------------------------------------------------------------------
;; Event name constants
;; ---------------------------------------------------------------------------
(def event-change-request "x-rating-change-request")
(def event-change         "x-rating-change")
(def event-hover          "x-rating-hover")

;; ---------------------------------------------------------------------------
;; Defaults and allowed values
;; ---------------------------------------------------------------------------
(def default-value     0)
(def default-max       5)
(def default-precision "full")
(def default-shape     "star")
(def default-size      "md")

;; Upper bound on the star count. `max` drives the DOM-node count directly,
;; so an extreme attribute (e.g. "1e9" or "Infinity") must not be honoured —
;; without this cap `(range max)` would hang the tab building runaway nodes.
(def max-stars-cap 20)

(def allowed-precision #{"full" "half"})
(def allowed-shapes    #{"star" "heart"})
(def allowed-sizes     #{"sm" "md" "lg"})

;; ---------------------------------------------------------------------------
;; Observed attributes
;; ---------------------------------------------------------------------------
(def observed-attributes
  #js [attr-value
       attr-max
       attr-precision
       attr-shape
       attr-allow-clear
       attr-disabled
       attr-readonly
       attr-name
       attr-label
       attr-size
       attr-aria-label
       attr-aria-labelledby
       attr-aria-describedby])

;; ---------------------------------------------------------------------------
;; API metadata
;; ---------------------------------------------------------------------------
(def property-api
  {:value      {:type 'string  :reflects-attribute attr-value}
   :max        {:type 'string  :reflects-attribute attr-max}
   :precision  {:type 'string  :reflects-attribute attr-precision}
   :shape      {:type 'string  :reflects-attribute attr-shape}
   :allowClear {:type 'boolean :reflects-attribute attr-allow-clear}
   :disabled   {:type 'boolean :reflects-attribute attr-disabled}
   :readOnly   {:type 'boolean :reflects-attribute attr-readonly}
   :name       {:type 'string  :reflects-attribute attr-name}
   :label      {:type 'string  :reflects-attribute attr-label}
   :size       {:type 'string  :reflects-attribute attr-size}})

(def event-schema
  {event-change-request {:cancelable true
                         :detail {:value 'number :previousValue 'number :max 'number}}
   event-change         {:detail {:value 'number :max 'number}}
   event-hover          {:detail {:value 'number :max 'number}}})

(def method-api {})

;; ---------------------------------------------------------------------------
;; Attribute normalization helpers
;; ---------------------------------------------------------------------------
(defn normalize-number
  "Parse `s` to a number, falling back to `default-val` for nil / NaN input."
  [s default-val]
  (if (string? s)
    (let [n (js/parseFloat s)]
      (if (js/isNaN n) default-val n))
    default-val))

(defn normalize-max
  "Star count: a whole number in [1, max-stars-cap]. The cap also tames a
   non-finite attribute — js/Math.round of Infinity stays Infinity, which the
   `min` then clamps."
  [s]
  (-> (normalize-number s default-max)
      (js/Math.round)
      (max 1)
      (min max-stars-cap)))

(defn normalize-precision
  [s]
  (if (and (string? s) (contains? allowed-precision s))
    s
    default-precision))

(defn normalize-shape
  [s]
  (if (and (string? s) (contains? allowed-shapes s))
    s
    default-shape))

(defn normalize-size
  [s]
  (if (and (string? s) (contains? allowed-sizes s))
    s
    default-size))

(defn non-empty
  "The string `s` when it is a non-empty string, otherwise nil."
  [s]
  (when (and (string? s) (not= "" s)) s))

;; ---------------------------------------------------------------------------
;; Numeric helpers
;; ---------------------------------------------------------------------------
(defn precision-step
  "Numeric increment for a normalized precision: 0.5 for \"half\", else 1."
  [norm-precision]
  (if (= "half" norm-precision) 0.5 1))

(defn clamp
  "Clamp `v` into [lo, hi]. When hi < lo (degenerate range) returns lo."
  [v lo hi]
  (if (< hi lo)
    lo
    (-> v (max lo) (min hi))))

(defn snap-to-precision
  "Snap `v` to the nearest multiple of `step`. A nil or non-positive step
   leaves `v` unchanged. The result is rounded to 9 decimals to suppress
   floating-point noise."
  [v step]
  (if (or (nil? step) (<= step 0))
    v
    (let [snapped (* step (js/Math.round (/ v step)))]
      (/ (js/Math.round (* snapped 1e9)) 1e9))))

(defn clamp-value
  "Clamp a rating into [0, max-stars]."
  [v max-stars]
  (clamp v 0 max-stars))

;; ---------------------------------------------------------------------------
;; Pointer-coordinate transforms
;; ---------------------------------------------------------------------------
(defn pointer-star-index
  "0-based index of the star under the pointer, clamped to [0, star-count-1]."
  [client-x rect-left rect-width star-count]
  (if (> rect-width 0)
    (let [ratio (-> (/ (- client-x rect-left) rect-width) (max 0.0) (min 1.0))
          idx   (js/Math.floor (* ratio star-count))]
      (-> idx (max 0) (min (dec star-count))))
    0))

(defn pointer->value
  "Pointer clientX + stars-row rect -> the discrete rating candidate in
   [0.5 .. star-count]. Stars are laid out with no gap, so each star spans
   rect-width / star-count. With a 0.5 step, the left half of a star picks
   the half value. Never returns 0 — clearing is handled by the component."
  [client-x rect-left rect-width star-count p-step]
  (let [idx       (pointer-star-index client-x rect-left rect-width star-count)
        star-w    (/ rect-width star-count)
        star-left (+ rect-left (* idx star-w))
        frac      (if (> star-w 0)
                    (-> (/ (- client-x star-left) star-w) (max 0.0) (min 1.0))
                    1.0)
        whole     (inc idx)]
    (if (and (= p-step 0.5) (< frac 0.5))
      (- whole 0.5)
      whole)))

;; ---------------------------------------------------------------------------
;; Star-fill geometry
;; ---------------------------------------------------------------------------
(defn star-fill
  "Fill state of star `star-index` (0-based) for the rating `value`."
  [star-index value]
  (cond
    (>= value (+ star-index 1))   :full
    (>= value (+ star-index 0.5)) :half
    :else                         :empty))

(defn fill-states
  "Per-star fill keywords (:full / :half / :empty) for the whole row."
  [value max-stars]
  (mapv (fn [i] (star-fill i value)) (range max-stars)))

;; ---------------------------------------------------------------------------
;; Keyboard
;; ---------------------------------------------------------------------------
(defn key-target
  "Pure: the proposed (unclamped) value for a keydown. Arrow keys move by one
   precision step, Home jumps to 0, End to max, Delete/Backspace clear to 0
   when clearing is allowed."
  [key cur max-stars p-step allow-clear?]
  (case key
    "ArrowRight" (+ cur p-step)
    "ArrowUp"    (+ cur p-step)
    "ArrowLeft"  (- cur p-step)
    "ArrowDown"  (- cur p-step)
    "Home"       0
    "End"        max-stars
    "Delete"     (if allow-clear? 0 cur)
    "Backspace"  (if allow-clear? 0 cur)
    cur))

;; ---------------------------------------------------------------------------
;; Accessibility / display text
;; ---------------------------------------------------------------------------
(defn aria-valuetext
  "Screen-reader announcement for the current rating. The icon noun follows
   the shape so a heart rating is not announced as \"stars\"."
  [value max-stars shape]
  (if (<= value 0)
    "No rating"
    (str value " out of " max-stars " " (if (= "heart" shape) "hearts" "stars"))))

;; ---------------------------------------------------------------------------
;; View-model builder
;; ---------------------------------------------------------------------------
(defn normalize
  "Build the stable, value-comparable view-model from raw attribute strings."
  [{:keys [value max precision shape allow-clear disabled readonly
           name label size aria-label aria-labelledby aria-describedby]}]
  (let [norm-max       (normalize-max max)
        norm-precision (normalize-precision precision)
        norm-shape     (normalize-shape shape)
        p-step         (precision-step norm-precision)
        raw-value      (normalize-number value default-value)
        snapped        (snap-to-precision raw-value p-step)
        norm-value     (clamp-value snapped norm-max)]
    {:value            norm-value
     :max              norm-max
     :precision        norm-precision
     :precision-step   p-step
     :shape            norm-shape
     :allow-clear?     (boolean allow-clear)
     :disabled?        (boolean disabled)
     :readonly?        (boolean readonly)
     :size             (normalize-size size)
     :name             (non-empty name)
     :label            (non-empty label)
     :fill-states      (fill-states norm-value norm-max)
     :value-text       (aria-valuetext norm-value norm-max norm-shape)
     :aria-label       (non-empty aria-label)
     :aria-labelledby  (non-empty aria-labelledby)
     :aria-describedby (non-empty aria-describedby)}))

;; ---------------------------------------------------------------------------
;; Derived helpers
;; ---------------------------------------------------------------------------
(defn interactable?
  "True when the rating accepts user interaction (not disabled, not readonly)."
  [{:keys [disabled? readonly?]}]
  (boolean (and (not disabled?) (not readonly?))))

(defn make-detail
  [value max-stars]
  {:value value :max max-stars})

(defn make-change-request-detail
  [value prev-value max-stars]
  {:value value :previousValue prev-value :max max-stars})
