(ns baredom.components.x-scroll-stack.model
  (:require [baredom.utils.model :as mu]))

;; ── Tag name ────────────────────────────────────────────────────────────────
(def tag-name "x-scroll-stack")

;; ── Attribute name constants ────────────────────────────────────────────────
(def attr-peek            "peek")
(def attr-rotation        "rotation")
(def attr-scroll-distance "scroll-distance")
(def attr-align           "align")
(def attr-disabled        "disabled")

(def observed-attributes
  #js [attr-peek attr-rotation attr-scroll-distance attr-align attr-disabled])

;; ── Event name constants ────────────────────────────────────────────────────
(def event-change   "x-scroll-stack-change")
(def event-progress "x-scroll-stack-progress")

;; ── Property API ────────────────────────────────────────────────────────────
(def property-api
  {:peek           {:type 'number}
   :rotation       {:type 'number}
   :scrollDistance  {:type 'number}
   :align          {:type 'string}
   :disabled       {:type 'boolean}
   :stackedCount   {:type 'number  :read-only true}
   :progress       {:type 'number  :read-only true}})

;; ── Event schema ────────────────────────────────────────────────────────────
(def event-schema
  {event-change   {:detail     {:stackedCount 'number :totalCount 'number :progress 'number}
                   :cancelable false}
   event-progress {:detail     {:progress 'number :stackedCount 'number :totalCount 'number}
                   :cancelable false}})

;; ── Default values ──────────────────────────────────────────────────────────
(def ^:private default-peek            6)
(def ^:private default-rotation        3)
(def ^:private default-scroll-distance 150)

;; ── Parse functions ─────────────────────────────────────────────────────────
(defn parse-positive-number
  "Parse a string to a positive number, returning default on failure."
  [s default]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (and (number? n) (not (js/isNaN n)) (pos? n))
        n
        default))
    default))

(def ^:private align-set #{"top" "center" "bottom"})

(defn parse-align
  "Parse align attribute. Returns \"top\", \"center\", or \"bottom\"."
  [s]
  (if (and (string? s) (contains? align-set (.toLowerCase (.trim s))))
    (.toLowerCase (.trim s))
    "center"))

;; ── Normalize ───────────────────────────────────────────────────────────────
(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :peek-raw             string | nil
    :rotation-raw         string | nil
    :scroll-distance-raw  string | nil
    :align-raw            string | nil
    :disabled-attr        string | nil

  Output keys:
    :peek             number
    :rotation         number
    :scroll-distance  number
    :align            string
    :disabled?        boolean"
  [{:keys [peek-raw rotation-raw scroll-distance-raw align-raw disabled-attr]}]
  {:peek            (parse-positive-number peek-raw default-peek)
   :rotation        (parse-positive-number rotation-raw default-rotation)
   :scroll-distance (parse-positive-number scroll-distance-raw default-scroll-distance)
   :align           (parse-align align-raw)
   :disabled?       (mu/parse-bool-present disabled-attr)})

;; ── Stacking math (pure functions) ──────────────────────────────────────────

(defn compute-card-progress
  "For a given scroll offset, compute the stacking progress [0.0, 1.0] of card i.
  Card i begins its transition at i*sd and completes at (i+1)*sd."
  [scroll-offset card-index scroll-distance]
  (let [start (* card-index scroll-distance)
        end   (+ start scroll-distance)
        span  (- end start)
        raw   (if (<= span 0) 0.0 (/ (- scroll-offset start) span))]
    (js/Math.max 0.0 (js/Math.min 1.0 raw))))

(defn compute-overall-progress
  "Returns [0.0, 1.0] for how far through the total stacking the user has scrolled."
  [scroll-offset child-count scroll-distance]
  (let [total (* child-count scroll-distance)]
    (if (<= total 0)
      0.0
      (js/Math.max 0.0 (js/Math.min 1.0 (/ scroll-offset total))))))

(defn compute-stacked-count
  "Returns the integer count of fully stacked cards."
  [scroll-offset child-count scroll-distance]
  (let [n (js/Math.floor (/ scroll-offset (js/Math.max 1 scroll-distance)))]
    (js/Math.max 0 (js/Math.min child-count n))))

(defn card-rotation
  "Deterministic rotation for card at index. Uses sin for pseudo-random spread."
  [index max-rotation]
  (* max-rotation (js/Math.sin (* index 2.3))))

(defn card-x-offset
  "Small horizontal jitter for the 'messy stack' feel."
  [index]
  (* 2.0 (js/Math.cos (* index 3.7))))

(defn stack-params
  "Returns {:stack-y :stack-rot :stack-x} for a card when fully stacked.
  stack-y is the peek offset (relative to stack top) for card at index."
  [card-index peek max-rotation]
  {:stack-y   (* card-index peek)
   :stack-rot (card-rotation card-index max-rotation)
   :stack-x   (card-x-offset card-index)})

(defn compute-stack-area-y
  "Compute the Y position of the stack area top within the container.
  container-h: container height (viewport), total-stack-h: sum of peeks + last card height,
  align: \"top\" | \"center\" | \"bottom\"."
  [container-h total-stack-h align]
  (let [padding 32]
    (case align
      "top"    padding
      "bottom" (- container-h total-stack-h padding)
      ;; center (default)
      (/ (- container-h total-stack-h) 2.0))))

;; ── Event detail builders ───────────────────────────────────────────────────
(defn change-detail [stacked-count total-count progress]
  {:stackedCount stacked-count :totalCount total-count :progress progress})

(defn progress-detail [progress stacked-count total-count]
  {:progress progress :stackedCount stacked-count :totalCount total-count})
