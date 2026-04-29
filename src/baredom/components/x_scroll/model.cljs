(ns baredom.components.x-scroll.model
  (:require [baredom.utils.model :as mu]))

;; ── Tag name ────────────────────────────────────────────────────────────────
(def tag-name "x-scroll")

;; ── Attribute name constants ────────────────────────────────────────────────
(def attr-mode            "mode")
(def attr-snap            "snap")
(def attr-loop            "loop")
(def attr-auto-play       "auto-play")
(def attr-interval        "interval")
(def attr-show-controls   "show-controls")
(def attr-show-indicators "show-indicators")
(def attr-active-index    "active-index")
(def attr-gap             "gap")
(def attr-disabled        "disabled")
(def attr-label           "label")

(def observed-attributes
  #js [attr-mode attr-snap attr-loop attr-auto-play attr-interval
       attr-show-controls attr-show-indicators attr-active-index
       attr-gap attr-disabled attr-label])

;; ── Event name constants ────────────────────────────────────────────────────
(def event-change "x-scroll-change")
(def event-start  "x-scroll-start")
(def event-end    "x-scroll-end")
(def event-loop   "x-scroll-loop")

;; ── Property API ────────────────────────────────────────────────────────────
(def property-api
  {:mode           {:type 'string}
   :snap           {:type 'string}
   :loop           {:type 'boolean}
   :autoPlay       {:type 'boolean}
   :interval       {:type 'number}
   :showControls   {:type 'boolean}
   :showIndicators {:type 'boolean}
   :activeIndex    {:type 'number}
   :gap            {:type 'number}
   :disabled       {:type 'boolean}
   :label          {:type 'string}})

;; ── Event schema ────────────────────────────────────────────────────────────
(def event-schema
  {event-change {:detail     {:activeIndex 'number :previousIndex 'number}
                 :cancelable false}
   event-start  {:detail     {:direction 'string :activeIndex 'number}
                 :cancelable false}
   event-end    {:detail     {:activeIndex 'number}
                 :cancelable false}
   event-loop   {:detail     {:direction 'string}
                 :cancelable true}})

;; ── Allowed enum values ─────────────────────────────────────────────────────
(def ^:private allowed-modes    #{"horizontal" "vertical"})
(def ^:private allowed-snaps    #{"none" "start" "center" "end"})

;; ── Parsing functions ───────────────────────────────────────────────────────

(defn parse-mode
  "Normalise mode attribute to \"horizontal\" or \"vertical\".
  Unknown / nil values fall back to \"horizontal\"."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-modes v) v "horizontal")))

(defn parse-snap
  "Normalise snap attribute to \"none\", \"start\", \"center\", or \"end\".
  Unknown / nil values fall back to \"none\"."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-snaps v) v "none")))

(defn parse-bool-default-true
  "Parse an attribute that is true when absent or empty, false only when \"false\"."
  [s]
  (if (nil? s)
    true
    (not= (.toLowerCase (.trim (str s))) "false")))

(defn parse-non-neg-int
  "Parse a string to a non-negative integer, returning default-val on failure."
  [s default-val]
  (if (string? s)
    (let [n (js/parseInt (.trim s) 10)]
      (if (or (js/isNaN n) (neg? n))
        default-val
        n))
    default-val))

(defn parse-interval
  "Parse interval attribute. Default 5000, minimum 500."
  [s]
  (max 500 (parse-non-neg-int s 5000)))

(defn parse-active-index
  "Parse active-index attribute. Default 0."
  [s]
  (parse-non-neg-int s 0))

(defn parse-gap
  "Parse gap attribute. Default 0."
  [s]
  (parse-non-neg-int s 0))

;; ── Normalization ───────────────────────────────────────────────────────────

(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :mode-raw               string | nil
    :snap-raw               string | nil
    :loop-attr              string | nil  (hasAttribute)
    :auto-play-attr         string | nil  (hasAttribute)
    :interval-raw           string | nil
    :show-controls-attr     string | nil  (getAttribute, nil=absent)
    :show-indicators-attr   string | nil  (hasAttribute)
    :active-index-raw       string | nil
    :gap-raw                string | nil
    :disabled-attr          string | nil  (hasAttribute)
    :label-raw              string | nil"
  [{:keys [mode-raw snap-raw loop-attr auto-play-attr interval-raw
           show-controls-attr show-indicators-attr active-index-raw
           gap-raw disabled-attr label-raw]}]
  {:mode             (parse-mode mode-raw)
   :snap             (parse-snap snap-raw)
   :loop?            (mu/parse-bool-present loop-attr)
   :auto-play?       (mu/parse-bool-present auto-play-attr)
   :interval         (parse-interval interval-raw)
   :show-controls?   (parse-bool-default-true show-controls-attr)
   :show-indicators? (mu/parse-bool-present show-indicators-attr)
   :active-index     (parse-active-index active-index-raw)
   :gap              (parse-gap gap-raw)
   :disabled?        (mu/parse-bool-present disabled-attr)
   :label            (or label-raw "")})

;; ── Position math ───────────────────────────────────────────────────────────

(defn loop-eligible?
  "Loop requires at least 3 children to avoid ambiguity."
  [child-count]
  (>= child-count 3))

(defn effective-loop?
  "True when loop is requested AND there are enough children."
  [loop? child-count]
  (and loop? (loop-eligible? child-count)))

(defn wrap-index
  "Wrap idx into valid range [0, count). Returns 0 when count <= 0."
  [idx count]
  (if (<= count 0)
    0
    (mod (+ (mod idx count) count) count)))

(defn effective-offset
  "Compute the visual offset of slide-index relative to active-index
  in loop mode. Returns a value in [-floor(count/2), ceil(count/2)-1]
  so the active slide is at 0 and neighbors wrap around it."
  [slide-index active-index child-count]
  (let [raw   (- slide-index active-index)
        half  (quot child-count 2)]
    (- (mod (+ raw half child-count) child-count) half)))

(defn slide-translate
  "Compute pixel translation for a slide at the given offset."
  [offset slide-size gap]
  (* offset (+ slide-size gap)))

(defn clamp-index
  "Clamp idx to [0, count-1] for non-loop mode."
  [idx child-count]
  (if (<= child-count 0)
    0
    (max 0 (min idx (dec child-count)))))

(defn resolve-target-index
  "Given a delta (+1 or -1), compute the target index.
  In loop mode wraps around; in non-loop mode clamps."
  [current-index delta child-count loop?]
  (let [raw (+ current-index delta)]
    (if (effective-loop? loop? child-count)
      (wrap-index raw child-count)
      (clamp-index raw child-count))))

(defn can-go-prev?
  "True when backward navigation is possible."
  [{:keys [active-index loop?]} child-count]
  (if (effective-loop? loop? child-count)
    (pos? child-count)
    (pos? active-index)))

(defn can-go-next?
  "True when forward navigation is possible."
  [{:keys [active-index loop?]} child-count]
  (if (effective-loop? loop? child-count)
    (pos? child-count)
    (< active-index (dec child-count))))

(defn snap-to-index
  "Given a free-form drag position (in pixels), compute the nearest
  slide index to snap to. Returns clamped [0, count-1] index."
  [position slide-size gap child-count]
  (if (or (<= child-count 0) (<= slide-size 0))
    0
    (let [step (+ slide-size gap)
          raw  (js/Math.round (/ (- position) step))]
      (clamp-index raw child-count))))

(defn crosses-boundary?
  "True when navigating from current to target crosses the loop boundary."
  [current-index target-index delta child-count]
  (and (pos? child-count)
       (or (and (pos? delta)  (< target-index current-index))
           (and (neg? delta)  (> target-index current-index)))))

;; ── Event detail builders ───────────────────────────────────────────────────

(defn direction-for-delta
  "Return \"forward\" for positive deltas, \"backward\" for negative."
  [delta]
  (if (pos? delta) "forward" "backward"))

(defn change-detail [active-index previous-index]
  {:activeIndex   active-index
   :previousIndex previous-index})

(defn start-detail [direction active-index]
  {:direction  direction
   :activeIndex active-index})

(defn end-detail [active-index]
  {:activeIndex active-index})

(defn loop-detail [direction]
  {:direction direction})

(def method-api nil)
