(ns baredom.components.x-welcome-tour.model
  "Pure functions for the x-welcome-tour orchestrator component.
   Positioning, cutout geometry, connector path computation, and
   attribute normalisation — no DOM or side effects.")

(def tag-name "x-welcome-tour")

;; ── Attribute name constants ────────────────────────────────────────────────
(def attr-open       "open")
(def attr-step       "step")
(def attr-connector  "connector")
(def attr-prev-label "prev-label")
(def attr-next-label "next-label")
(def attr-done-label "done-label")
(def attr-skip-label "skip-label")
(def attr-counter    "counter")
(def attr-dots       "dots")

(def observed-attributes
  #js [attr-open attr-step attr-connector attr-prev-label attr-next-label
       attr-done-label attr-skip-label attr-counter attr-dots])

;; ── Event name constants ────────────────────────────────────────────────────
(def event-start       "x-welcome-tour-start")
(def event-step-change "x-welcome-tour-step-change")
(def event-complete    "x-welcome-tour-complete")
(def event-skip        "x-welcome-tour-skip")

;; ── Public API metadata ─────────────────────────────────────────────────────
(def property-api
  {:open       {:type 'boolean :reflects-attribute attr-open}
   :step       {:type 'number  :reflects-attribute attr-step}
   :connector  {:type 'string  :reflects-attribute attr-connector}
   :prevLabel  {:type 'string  :reflects-attribute attr-prev-label}
   :nextLabel  {:type 'string  :reflects-attribute attr-next-label}
   :doneLabel  {:type 'string  :reflects-attribute attr-done-label}
   :skipLabel  {:type 'string  :reflects-attribute attr-skip-label}
   :counter    {:type 'boolean :reflects-attribute attr-counter}
   :dots       {:type 'boolean :reflects-attribute attr-dots}
   :totalSteps {:type 'number  :readonly true}})

(def event-schema
  {event-start       {:detail {} :cancelable false}
   event-step-change {:detail {:step 'number :previousStep 'number} :cancelable true}
   event-complete    {:detail {:stepsCompleted 'number} :cancelable false}
   event-skip        {:detail {:step 'number} :cancelable false}})

;; ── Enums ───────────────────────────────────────────────────────────────────
(def allowed-connectors #{"arrow" "line" "curve" "none"})
(def default-connector "arrow")

;; ── Default labels ──────────────────────────────────────────────────────────
(def default-prev-label "Back")
(def default-next-label "Next")
(def default-done-label "Done")
(def default-skip-label "Skip")

;; ── Parse helpers ───────────────────────────────────────────────────────────
(defn parse-step
  "Parse step attribute to a non-negative integer. Defaults to 0."
  [s]
  (if (string? s)
    (let [n (js/parseInt (.trim s) 10)]
      (if (and (number? n) (not (js/isNaN n)) (>= n 0)) n 0))
    0))

(defn parse-connector
  "Normalise a raw connector attribute string. Falls back to default."
  [s]
  (if (and (string? s) (contains? allowed-connectors (.toLowerCase s)))
    (.toLowerCase s)
    default-connector))

;; ── Normalize ───────────────────────────────────────────────────────────────
(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :open?           boolean
    :step-raw        string | nil
    :connector-raw   string | nil
    :prev-label-raw  string | nil
    :next-label-raw  string | nil
    :done-label-raw  string | nil
    :skip-label-raw  string | nil
    :counter?        boolean
    :dots?           boolean

  Output keys:
    :open?       boolean
    :step        number
    :connector   string
    :prev-label  string
    :next-label  string
    :done-label  string
    :skip-label  string
    :counter?    boolean
    :dots?       boolean"
  [{:keys [open? step-raw connector-raw prev-label-raw next-label-raw
           done-label-raw skip-label-raw counter? dots?]}]
  {:open?      (boolean open?)
   :step       (parse-step step-raw)
   :connector  (parse-connector connector-raw)
   :prev-label (or prev-label-raw default-prev-label)
   :next-label (or next-label-raw default-next-label)
   :done-label (or done-label-raw default-done-label)
   :skip-label (or skip-label-raw default-skip-label)
   :counter?   (boolean counter?)
   :dots?      (boolean dots?)})

;; ── Step navigation predicates ──────────────────────────────────────────────
(defn first-step?
  "Return true when the current step is the first."
  [step]
  (zero? step))

(defn last-step?
  "Return true when the current step is the last."
  [step total]
  (>= step (dec total)))

(defn clamp-step
  "Clamp step index to valid range [0, total-1]."
  [step total]
  (if (pos? total)
    (-> step (max 0) (min (dec total)))
    0))

(defn counter-text
  "Format the step counter string, e.g. '2 / 5'."
  [step total]
  (str (inc step) " / " total))

;; ── Cutout geometry ─────────────────────────────────────────────────────────
(defn compute-cutout
  "Compute cutout rect from a target DOMRect and padding/radius values.
   Returns {:x :y :width :height :rx :ry}."
  [target-rect padding radius]
  (let [{:keys [x y width height]} target-rect]
    {:x      (- x padding)
     :y      (- y padding)
     :width  (+ width (* 2 padding))
     :height (+ height (* 2 padding))
     :rx     radius
     :ry     radius}))

;; ── Positioning ─────────────────────────────────────────────────────────────
(def ^:private arrow-offset 8)
(def ^:private connector-offset 48)
(def ^:private default-margin 8)

(defn offset-for-connector
  "Return the gap between the target and popover based on connector type.
   Line and curve connectors need more space so the path is visible."
  [connector-type]
  (case connector-type
    ("line" "curve") connector-offset
    arrow-offset))

(defn- opposite-placement [placement]
  (case placement
    "top"          "bottom"
    "bottom"       "top"
    "left"         "right"
    "right"        "left"
    "top-start"    "bottom-start"
    "top-end"      "bottom-end"
    "bottom-start" "top-start"
    "bottom-end"   "top-end"
    placement))

(defn- cross-placements [placement]
  (case placement
    ("top" "bottom" "top-start" "top-end" "bottom-start" "bottom-end")
    ["left" "right"]
    ("left" "right")
    ["bottom" "top"]
    ["bottom" "top"]))

(defn- candidate-xy
  "Compute candidate x/y for a placement given anchor rect and popover size."
  [placement offset anchor-rect popover-size]
  (let [{ax :x ay :y aw :width ah :height} anchor-rect
        {pw :width ph :height} popover-size
        cx-center (+ ax (/ aw 2) (- (/ pw 2)))
        cy-center (+ ay (/ ah 2) (- (/ ph 2)))]
    (case placement
      "top"          {:x cx-center               :y (- ay ph offset)}
      "bottom"       {:x cx-center               :y (+ ay ah offset)}
      "left"         {:x (- ax pw offset)         :y cy-center}
      "right"        {:x (+ ax aw offset)         :y cy-center}
      "top-start"    {:x ax                       :y (- ay ph offset)}
      "top-end"      {:x (- (+ ax aw) pw)        :y (- ay ph offset)}
      "bottom-start" {:x ax                       :y (+ ay ah offset)}
      "bottom-end"   {:x (- (+ ax aw) pw)        :y (+ ay ah offset)}
      {:x cx-center :y (+ ay ah offset)})))

(defn- fits-in-viewport?
  "Check if a positioned popover fits within viewport bounds."
  [{:keys [x y]} {:keys [width height]} viewport-size margin]
  (let [{vw :width vh :height} viewport-size]
    (and (>= x margin)
         (>= y margin)
         (<= (+ x width) (- vw margin))
         (<= (+ y height) (- vh margin)))))

(defn compute-position
  "Compute viewport-relative position for the tour popover.
   Tries preferred placement, then opposite, then cross-axis placements.
   connector-type controls the gap: line/curve get extra distance so the
   connector path is visible.
   Returns {:x :y :final-placement}."
  [placement anchor-rect popover-size viewport-size connector-type]
  (let [offset (offset-for-connector connector-type)
        margin default-margin
        {pw :width ph :height} popover-size
        {vw :width vh :height} viewport-size
        try-placement
        (fn [p]
          (let [{cx :x cy :y} (candidate-xy p offset anchor-rect popover-size)
                x (-> cx (max margin) (min (- vw pw margin)))
                y (-> cy (max margin) (min (- vh ph margin)))]
            {:x x :y y :final-placement p
             :fits (fits-in-viewport? {:x cx :y cy} popover-size viewport-size margin)}))
        primary   (try-placement placement)
        flipped   (try-placement (opposite-placement placement))
        [cross-a cross-b] (cross-placements placement)
        cross-1   (try-placement cross-a)
        cross-2   (try-placement cross-b)]
    (cond
      (:fits primary) primary
      (:fits flipped) flipped
      (:fits cross-1) cross-1
      (:fits cross-2) cross-2
      :else           primary)))

;; ── Connector geometry ──────────────────────────────────────────────────────
(defn- placement-axis
  "Returns :vertical for top/bottom placements, :horizontal for left/right."
  [placement]
  (case placement
    ("top" "bottom" "top-start" "top-end" "bottom-start" "bottom-end") :vertical
    ("left" "right") :horizontal
    :vertical))

(defn connector-anchor-points
  "Compute connector anchor points on target and popover edges.
   Returns {:target-point {:x :y} :popover-point {:x :y}}."
  [target-rect popover-rect final-placement]
  (let [{tx :x ty :y tw :width th :height} target-rect
        {px :x py :y pw :width ph :height} popover-rect
        t-cx (+ tx (/ tw 2))
        t-cy (+ ty (/ th 2))
        p-cx (+ px (/ pw 2))
        p-cy (+ py (/ ph 2))]
    (case final-placement
      ("bottom" "bottom-start" "bottom-end")
      {:target-point  {:x t-cx :y (+ ty th)}
       :popover-point {:x p-cx :y py}}

      ("top" "top-start" "top-end")
      {:target-point  {:x t-cx :y ty}
       :popover-point {:x p-cx :y (+ py ph)}}

      "left"
      {:target-point  {:x tx :y t-cy}
       :popover-point {:x (+ px pw) :y p-cy}}

      "right"
      {:target-point  {:x (+ tx tw) :y t-cy}
       :popover-point {:x px :y p-cy}}

      ;; fallback: bottom
      {:target-point  {:x t-cx :y (+ ty th)}
       :popover-point {:x p-cx :y py}})))

(defn connector-path-d
  "Returns SVG path d attribute string for the connector.
   connector-type is \"line\" or \"curve\". Returns nil for \"arrow\" or \"none\"."
  [connector-type target-point popover-point final-placement]
  (let [{x1 :x y1 :y} target-point
        {x2 :x y2 :y} popover-point]
    (case connector-type
      "line"
      (str "M" x1 "," y1 " L" x2 "," y2)

      "curve"
      (let [axis (placement-axis final-placement)]
        (if (= axis :vertical)
          ;; S-curve along vertical axis
          (let [mid-y (/ (+ y1 y2) 2)]
            (str "M" x1 "," y1
                 " C" x1 "," mid-y
                 " " x2 "," mid-y
                 " " x2 "," y2))
          ;; S-curve along horizontal axis
          (let [mid-x (/ (+ x1 x2) 2)]
            (str "M" x1 "," y1
                 " C" mid-x "," y1
                 " " mid-x "," y2
                 " " x2 "," y2))))

      nil)))

;; ── Arrow positioning ───────────────────────────────────────────────────────
(def ^:private arrow-edge-margin 16)

(defn- clamp-arrow
  "Clamp an arrow offset (px from the popover edge) to stay within bounds.
   min-val and max-val are the popover dimension limits minus margin."
  [val min-val max-val]
  (-> val (max min-val) (min max-val)))

(defn arrow-style
  "Compute inline style for the CSS arrow element so it points at the
   centre of the target. target-rect and popover-rect are viewport-relative
   maps with :x :y :width :height. The arrow slides along the popover edge
   to aim at the target centre, clamped so it stays inside the popover.
   Returns a map of CSS property strings."
  [final-placement arrow-size target-rect popover-rect]
  (let [{tx :x ty :y tw :width th :height} target-rect
        {px :x py :y pw :width ph :height} popover-rect
        t-cx   (+ tx (/ tw 2))
        t-cy   (+ ty (/ th 2))
        margin arrow-edge-margin
        half   (/ arrow-size 2)]
    (case final-placement
      ("bottom" "bottom-start" "bottom-end")
      (let [;; Arrow left offset = target center X relative to popover left
            raw-left (- t-cx px)
            left     (clamp-arrow raw-left margin (- pw margin))]
        {:top    (str "-" arrow-size "px")
         :left   (str left "px")
         :margin-left (str "-" half "px")
         :border-bottom-color "var(--x-welcome-tour-popover-bg)"})

      ("top" "top-start" "top-end")
      (let [raw-left (- t-cx px)
            left     (clamp-arrow raw-left margin (- pw margin))]
        {:bottom (str "-" arrow-size "px")
         :left   (str left "px")
         :margin-left (str "-" half "px")
         :border-top-color "var(--x-welcome-tour-popover-bg)"})

      "left"
      (let [raw-top (- t-cy py)
            top     (clamp-arrow raw-top margin (- ph margin))]
        {:right  (str "-" arrow-size "px")
         :top    (str top "px")
         :margin-top (str "-" half "px")
         :border-left-color "var(--x-welcome-tour-popover-bg)"})

      "right"
      (let [raw-top (- t-cy py)
            top     (clamp-arrow raw-top margin (- ph margin))]
        {:left   (str "-" arrow-size "px")
         :top    (str top "px")
         :margin-top (str "-" half "px")
         :border-right-color "var(--x-welcome-tour-popover-bg)"})

      ;; fallback: bottom
      (let [raw-left (- t-cx px)
            left     (clamp-arrow raw-left margin (- pw margin))]
        {:top    (str "-" arrow-size "px")
         :left   (str left "px")
         :margin-left (str "-" half "px")
         :border-bottom-color "var(--x-welcome-tour-popover-bg)"}))))

;; ── Method API metadata ──────────────────────────────────────────────────────
(def method-api
  {:start    {:args [] :returns 'void}
   :next     {:args [] :returns 'void}
   :prev     {:args [] :returns 'void}
   :goTo     {:args [{:name "step" :type 'number}] :returns 'void}
   :complete {:args [] :returns 'void}
   :skip     {:args [] :returns 'void}})

;; ── Event detail builders ───────────────────────────────────────────────────
(defn step-change-detail
  [step previous-step]
  {:step step :previousStep previous-step})

(defn complete-detail
  [steps-completed]
  {:stepsCompleted steps-completed})

(defn skip-detail
  [step]
  {:step step})
