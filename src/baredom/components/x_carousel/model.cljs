(ns baredom.components.x-carousel.model
  (:require [baredom.utils.model :as mu]))

;; ── Tag name ────────────────────────────────────────────────────────────────
(def tag-name "x-carousel")

;; ── Attribute constants ─────────────────────────────────────────────────────
(def attr-autoplay   "autoplay")
(def attr-interval   "interval")
(def attr-loop       "loop")
(def attr-arrows     "arrows")
(def attr-dots       "dots")
(def attr-disabled   "disabled")
(def attr-current    "current")
(def attr-transition "transition")
(def attr-direction  "direction")
(def attr-peek       "peek")
(def attr-aria-label "aria-label")

(def observed-attributes
  #js [attr-autoplay attr-interval attr-loop attr-arrows attr-dots
       attr-disabled attr-current attr-transition attr-direction
       attr-peek attr-aria-label])

;; ── Event constants ─────────────────────────────────────────────────────────
(def event-change "x-carousel-change")

;; ── CSS custom property names ───────────────────────────────────────────────
(def css-height              "--x-carousel-height")
(def css-arrow-size          "--x-carousel-arrow-size")
(def css-arrow-bg            "--x-carousel-arrow-bg")
(def css-arrow-color         "--x-carousel-arrow-color")
(def css-arrow-hover-bg      "--x-carousel-arrow-hover-bg")
(def css-dot-size            "--x-carousel-dot-size")
(def css-dot-color           "--x-carousel-dot-color")
(def css-dot-active-color    "--x-carousel-dot-active-color")
(def css-transition-duration "--x-carousel-transition-duration")
(def css-radius              "--x-carousel-radius")
(def css-gap                 "--x-carousel-gap")

;; ── Metadata ────────────────────────────────────────────────────────────────
(def property-api
  {:currentSlide {:type 'number}
   :autoplay     {:type 'boolean}
   :interval     {:type 'number}
   :loop         {:type 'boolean}
   :arrows       {:type 'boolean}
   :dots         {:type 'boolean}
   :disabled     {:type 'boolean}
   :transition   {:type 'string}
   :direction    {:type 'string}
   :peek         {:type 'string}
   :slideCount   {:type 'number :read-only true}})

(def event-schema
  {event-change {:detail     {:index 'number :previousIndex 'number :reason 'string}
                 :cancelable true}})

;; ── Defaults ────────────────────────────────────────────────────────────────
(def default-interval 5000)
(def default-current  0)
(def default-transition "slide")
(def default-direction "horizontal")
(def default-peek "0px")

;; ── Drag constants ──────────────────────────────────────────────────────────
(def drag-threshold-px 40)
(def velocity-threshold 0.3)

;; ── Parsers ─────────────────────────────────────────────────────────────────

(defn parse-bool-default-true
  "Parse an attribute that is true when absent or empty, false only when \"false\"."
  [s]
  (if (nil? s)
    true
    (not= (.toLowerCase (.trim (str s))) "false")))

(defn parse-non-neg-int
  "Parse a string to a non-negative integer. Returns fallback on failure."
  [s fallback]
  (if (string? s)
    (let [n (js/parseInt (.trim s) 10)]
      (if (and (not (js/isNaN n)) (>= n 0))
        n
        fallback))
    fallback))

(defn parse-pos-int
  "Parse a string to a positive integer >= min-val. Returns fallback on failure."
  [s fallback min-val]
  (if (string? s)
    (let [n (js/parseInt (.trim s) 10)]
      (if (and (not (js/isNaN n)) (>= n min-val))
        n
        fallback))
    fallback))

(def ^:private valid-transitions #{"slide" "fade"})

(defn parse-transition
  "Normalise transition attribute to \"slide\" or \"fade\"."
  [s]
  (if (string? s)
    (let [v (.toLowerCase (.trim s))]
      (if (contains? valid-transitions v) v default-transition))
    default-transition))

(def ^:private valid-directions #{"horizontal" "vertical"})

(defn parse-direction
  "Normalise direction attribute to \"horizontal\" or \"vertical\"."
  [s]
  (if (string? s)
    (let [v (.toLowerCase (.trim s))]
      (if (contains? valid-directions v) v default-direction))
    default-direction))

(def ^:private peek-re #"^\d+(\.\d+)?(px|rem|em|%)$")

(defn parse-peek
  "Validate a CSS length string for peek. Returns default on invalid."
  [s]
  (if (and (string? s) (.test peek-re (.trim s)))
    (.trim s)
    default-peek))

;; ── Index helpers ───────────────────────────────────────────────────────────

(defn clamp-index
  "Clamp idx within [0, count-1]. Returns 0 when count <= 0."
  [idx cnt]
  (cond
    (<= cnt 0) 0
    (< idx 0)  0
    (>= idx cnt) (dec cnt)
    :else idx))

;; ── Normalize ───────────────────────────────────────────────────────────────

(defn normalize
  "Normalise raw attribute inputs into a stable view-model map."
  [{:keys [autoplay-present? interval-raw loop-present?
           arrows-raw dots-raw disabled-present?
           current-raw transition-raw direction-raw
           peek-raw aria-label-raw slide-count]}]
  (let [sc      (or slide-count 0)
        current (clamp-index (parse-non-neg-int current-raw default-current) sc)]
    {:autoplay?   (boolean autoplay-present?)
     :interval    (parse-pos-int interval-raw default-interval 100)
     :loop?       (boolean loop-present?)
     :arrows?     (parse-bool-default-true arrows-raw)
     :dots?       (parse-bool-default-true dots-raw)
     :disabled?   (boolean disabled-present?)
     :current     current
     :transition  (parse-transition transition-raw)
     :direction   (parse-direction direction-raw)
     :peek        (parse-peek peek-raw)
     :slide-count sc
     :aria-label  (when (mu/non-empty-string? aria-label-raw) aria-label-raw)}))

;; ── Navigation predicates ───────────────────────────────────────────────────

(defn can-go-prev?
  [{:keys [current loop? slide-count disabled?]}]
  (and (not disabled?)
       (> slide-count 1)
       (or loop? (pos? current))))

(defn can-go-next?
  [{:keys [current loop? slide-count disabled?]}]
  (and (not disabled?)
       (> slide-count 1)
       (or loop? (< current (dec slide-count)))))

(defn next-index
  [{:keys [current loop? slide-count]}]
  (let [nxt (inc current)]
    (if (>= nxt slide-count)
      (if loop? 0 current)
      nxt)))

(defn prev-index
  [{:keys [current loop? slide-count]}]
  (let [prv (dec current)]
    (if (neg? prv)
      (if loop? (dec slide-count) current)
      prv)))

;; ── Display predicates ──────────────────────────────────────────────────────

(defn single-slide?
  [{:keys [slide-count]}]
  (<= slide-count 1))

(defn show-arrows?
  [{:keys [arrows? slide-count]}]
  (and arrows? (> slide-count 1)))

(defn show-dots?
  [{:keys [dots? slide-count]}]
  (and dots? (> slide-count 1)))

;; ── Drag snap ───────────────────────────────────────────────────────────────

(defn snap-direction
  "Given drag delta (positive = toward prev) and velocity (px/ms, positive = toward prev),
   returns :prev, :next, or :stay."
  [delta velocity]
  (cond
    (> velocity velocity-threshold)           :prev
    (< velocity (- velocity-threshold))       :next
    (> delta drag-threshold-px)               :prev
    (< delta (- drag-threshold-px))           :next
    :else                                     :stay))

;; ── Keyboard helpers ────────────────────────────────────────────────────────

(def ^:private horizontal-prev-keys #{"ArrowLeft"})
(def ^:private horizontal-next-keys #{"ArrowRight"})
(def ^:private vertical-prev-keys   #{"ArrowUp"})
(def ^:private vertical-next-keys   #{"ArrowDown"})

(defn prev-key?
  "Returns true if key is a 'previous' key for the given direction."
  [direction key]
  (contains?
   (if (= direction "vertical") vertical-prev-keys horizontal-prev-keys)
   key))

(defn next-key?
  "Returns true if key is a 'next' key for the given direction."
  [direction key]
  (contains?
   (if (= direction "vertical") vertical-next-keys horizontal-next-keys)
   key))
