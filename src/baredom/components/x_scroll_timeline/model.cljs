(ns baredom.components.x-scroll-timeline.model
  (:require [baredom.utils.model :as mu]
            [clojure.string :as str]))

;; ── Tag name ────────────────────────────────────────────────────────────────
(def tag-name "x-scroll-timeline")

;; ── Attribute name constants ────────────────────────────────────────────────
(def attr-layout       "layout")
(def attr-track        "track")
(def attr-threshold    "threshold")
(def attr-no-progress  "no-progress")
(def attr-disabled     "disabled")
(def attr-label        "label")
(def attr-marker       "marker")

;; ── Autoplay attribute constants ────────────────────────────────────────────
(def attr-autoplay           "autoplay")
(def attr-autoplay-speed     "autoplay-speed")
(def attr-autoplay-loop      "autoplay-loop")
(def attr-autoplay-indicator "autoplay-indicator")

;; ── JS property name constants (camelCase, differ from HTML attribute names) ─
(def prop-no-progress        "noProgress")
(def prop-active-index       "activeIndex")
(def prop-progress           "progress")
(def prop-autoplay-speed     "autoplaySpeed")
(def prop-autoplay-loop      "autoplayLoop")
(def prop-autoplay-indicator "autoplayIndicator")
(def prop-autoplay-paused    "autoplayPaused")

(def observed-attributes
  #js [attr-layout attr-track attr-threshold attr-no-progress
       attr-disabled attr-label attr-marker
       attr-autoplay attr-autoplay-speed attr-autoplay-loop attr-autoplay-indicator])

;; ── Data attribute constants ────────────────────────────────────────────────
(def data-active "data-active")
(def data-side   "data-side")
(def data-date   "data-date")
(def data-index  "data-index")

;; ── Event name constants ────────────────────────────────────────────────────
(def event-entry-change "x-scroll-timeline-entry-change")
(def event-entry-enter  "x-scroll-timeline-entry-enter")
(def event-entry-leave  "x-scroll-timeline-entry-leave")
(def event-progress     "x-scroll-timeline-progress")
(def event-enter        "x-scroll-timeline-enter")
(def event-leave        "x-scroll-timeline-leave")
(def event-autoplay-pause  "x-scroll-timeline-autoplay-pause")
(def event-autoplay-resume "x-scroll-timeline-autoplay-resume")

;; ── Property API ────────────────────────────────────────────────────────────
(def property-api
  {:layout      {:type 'string}
   :track       {:type 'string}
   :threshold   {:type 'number}
   :noProgress  {:type 'boolean}
   :disabled    {:type 'boolean}
   :label       {:type 'string}
   :marker      {:type 'string}
   :activeIndex {:type 'number :read-only true}
   :progress    {:type 'number :read-only true}
   :autoplay          {:type 'boolean}
   :autoplaySpeed     {:type 'number}
   :autoplayLoop      {:type 'boolean}
   :autoplayIndicator {:type 'boolean}
   :autoplayPaused    {:type 'boolean :read-only true}})

;; ── Event schema ────────────────────────────────────────────────────────────
(def event-schema
  {event-entry-change {:detail {:index 'number :id 'string
                                :previousIndex 'number :previousId 'string}
                       :cancelable false}
   event-entry-enter  {:detail {:index 'number :id 'string :progress 'number}
                       :cancelable false}
   event-entry-leave  {:detail {:index 'number :id 'string :progress 'number}
                       :cancelable false}
   event-progress     {:detail {:progress 'number :activeIndex 'number :activeId 'string}
                       :cancelable false}
   event-enter        {:detail {:progress 'number} :cancelable false}
   event-leave        {:detail {:progress 'number} :cancelable false}
   event-autoplay-pause  {:detail {:progress 'number :activeIndex 'number :activeId 'string}
                          :cancelable false}
   event-autoplay-resume {:detail {:progress 'number :activeIndex 'number :activeId 'string}
                          :cancelable false}})

;; ── Allowed enum values ─────────────────────────────────────────────────────
(def ^:private allowed-layouts #{"alternating" "left" "right"})
(def ^:private allowed-tracks  #{"straight" "curved"})
(def ^:private allowed-markers #{"dot" "ring" "none"})

;; ── Parsing functions ───────────────────────────────────────────────────────

(defn parse-layout
  "Normalise layout attribute to \"alternating\", \"left\", or \"right\".
  Unknown / nil values fall back to \"alternating\"."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-layouts v) v "alternating")))

(defn parse-track
  "Normalise track attribute to \"straight\" or \"curved\".
  Unknown / nil values fall back to \"straight\"."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-tracks v) v "straight")))

(defn parse-threshold
  "Parse threshold attribute to a float clamped to [0,1]. Default 0.5."
  [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (js/isNaN n) 0.5 (max 0.0 (min 1.0 n))))
    0.5))

(defn parse-marker
  "Normalise marker attribute to \"dot\", \"ring\", or \"none\".
  Unknown / nil values fall back to \"dot\"."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-markers v) v "dot")))

(defn parse-autoplay-speed
  "Parse autoplay-speed attribute to a positive number clamped to [1,1000].
  Default 50 px/s."
  [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (js/isNaN n) 50 (max 1 (min 1000 n))))
    50))

;; ── Normalization ───────────────────────────────────────────────────────────

(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :layout-raw            string | nil
    :track-raw             string | nil
    :threshold-raw         string | nil
    :no-progress-attr      string | nil  (hasAttribute)
    :disabled-attr         string | nil  (hasAttribute)
    :label-raw             string | nil
    :marker-raw            string | nil
    :autoplay-attr         string | nil  (hasAttribute)
    :autoplay-speed-raw    string | nil
    :autoplay-loop-attr    string | nil  (hasAttribute)
    :autoplay-indicator-attr string | nil (hasAttribute)

  Output keys:
    :layout              string — \"alternating\" | \"left\" | \"right\"
    :track               string — \"straight\" | \"curved\"
    :threshold           number — [0,1]
    :no-progress?        boolean
    :disabled?           boolean
    :label               string
    :marker              string — \"dot\" | \"ring\" | \"none\"
    :autoplay?           boolean
    :autoplay-speed      number — [1,1000]
    :autoplay-loop?      boolean
    :autoplay-indicator? boolean"
  [{:keys [layout-raw track-raw threshold-raw no-progress-attr
           disabled-attr label-raw marker-raw
           autoplay-attr autoplay-speed-raw autoplay-loop-attr
           autoplay-indicator-attr]}]
  {:layout              (parse-layout layout-raw)
   :track               (parse-track track-raw)
   :threshold           (parse-threshold threshold-raw)
   :no-progress?        (mu/parse-bool-present no-progress-attr)
   :disabled?           (mu/parse-bool-present disabled-attr)
   :label               (or label-raw "")
   :marker              (parse-marker marker-raw)
   :autoplay?           (mu/parse-bool-present autoplay-attr)
   :autoplay-speed      (parse-autoplay-speed autoplay-speed-raw)
   :autoplay-loop?      (mu/parse-bool-present autoplay-loop-attr)
   :autoplay-indicator? (mu/parse-bool-present autoplay-indicator-attr)})

;; ── Layout logic ────────────────────────────────────────────────────────────

(defn entry-side
  "Return the data-side string to assign to a child entry.
  layout — the component's resolved layout string
  index  — the child's 0-based index

  \"alternating\" → even index → \"left\", odd index → \"right\"
  \"left\"        → always \"right\"  (entries on the right of the track)
  \"right\"       → always \"left\"   (entries on the left of the track)"
  [layout index]
  (case layout
    "alternating" (if (zero? (mod index 2)) "left" "right")
    "left"        "right"
    "right"       "left"
    "right"))

;; ── Scroll math ─────────────────────────────────────────────────────────────

(defn compute-overall-progress
  "Compute scroll progress [0,1] for the entire timeline container.
  0 = container just entered at the bottom, 1 = about to leave at the top."
  [container-top container-height viewport-height]
  (let [total    (+ viewport-height container-height)
        scrolled (- viewport-height container-top)]
    (max 0.0 (min 1.0 (/ scrolled total)))))

(defn find-active-entry
  "Given a vector of {:top :bottom} rects and a trigger-y position (pixels
  from the top of the viewport), return the index of the active entry or -1.

  An entry is active when it spans the trigger line (top <= trigger-y < bottom).
  If no entry spans it, the last entry whose top is above the trigger line wins."
  [entry-rects trigger-y]
  (let [n (count entry-rects)]
    (if (zero? n)
      -1
      (loop [i 0
             spanning -1
             last-above -1]
        (if (>= i n)
          (if (>= spanning 0) spanning last-above)
          (let [{:keys [top bottom]} (nth entry-rects i)]
            (recur (inc i)
                   (if (and (<= top trigger-y) (> bottom trigger-y)) i spanning)
                   (if (<= top trigger-y) i last-above))))))))

(defn compute-entry-progress
  "Compute how far an entry has moved through the viewport.
  0 = entry bottom just reached trigger line, 1 = entry top passed it."
  [entry-top entry-height trigger-y]
  (if (<= entry-height 0)
    0.0
    (let [progress (/ (- trigger-y entry-top) entry-height)]
      (max 0.0 (min 1.0 progress)))))

;; ── SVG serpentine path generation ──────────────────────────────────────────

(defn build-serpentine-path
  "Generate an SVG path string for a serpentine timeline track.

  points     — vector of {:y number :side \"left\"|\"right\"} maps
               representing the vertical center of each entry
  amplitude  — horizontal distance the curve swings from center (pixels)
  center-x   — horizontal center of the track (pixels)

  Returns an SVG path d-attribute string using cubic Bezier curves."
  [points amplitude center-x]
  (let [n (count points)]
    (cond
      (zero? n)
      ""

      (= n 1)
      (let [{:keys [y]} (first points)]
        (str "M " center-x " " y))

      :else
      (let [first-pt (first points)
            start-y  (:y first-pt)]
        (loop [i 0
               parts [(str "M " center-x " " start-y)]]
          (if (>= i (dec n))
            (str/join " " parts)
            (let [p1    (nth points i)
                  p2    (nth points (inc i))
                  y1    (:y p1)
                  y2    (:y p2)
                  ;; S-curve: first control point pulls toward p1's side,
                  ;; second control point pulls toward p2's side
                  side1 (:side p1)
                  side2 (:side p2)
                  dx1   (if (= side1 "left") (- amplitude) amplitude)
                  dx2   (if (= side2 "left") (- amplitude) amplitude)
                  ;; Control points at 1/3 and 2/3 of the vertical distance
                  cp1x  (+ center-x dx1)
                  cp1y  (+ y1 (* (- y2 y1) 0.33))
                  cp2x  (+ center-x dx2)
                  cp2y  (+ y1 (* (- y2 y1) 0.66))
                  ex    center-x
                  ey    y2]
              (recur (inc i)
                     (conj parts
                           (str "C " cp1x " " cp1y
                                " " cp2x " " cp2y
                                " " ex " " ey))))))))))

