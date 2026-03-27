(ns baredom.components.x-stepper.model
  (:require [goog.object :as gobj]))

(def tag-name "x-stepper")

(def attr-steps       "steps")
(def attr-current     "current")
(def attr-orientation "orientation")
(def attr-size        "size")
(def attr-disabled    "disabled")

(def observed-attributes
  #js [attr-steps attr-current attr-orientation attr-size attr-disabled])

(def event-change "x-stepper-change")

(def property-api
  {:steps       {:type 'string}
   :current     {:type 'number}
   :orientation {:type 'string}
   :size        {:type 'string}
   :disabled    {:type 'boolean}})

(def event-schema
  {event-change {:detail {:from 'number :to 'number} :cancelable true}})

;; ── Orientation ──────────────────────────────────────────────────────────────
(def ^:private default-orientation :horizontal)
(def ^:private orientation->kw {"horizontal" :horizontal "vertical" :vertical})
(def ^:private kw->orientation {:horizontal "horizontal" :vertical "vertical"})

(defn parse-orientation [s]
  (or (get orientation->kw (when (string? s) (.toLowerCase s)))
      default-orientation))

(defn orientation->attr [o]
  (or (get kw->orientation o) "horizontal"))

;; ── Size ─────────────────────────────────────────────────────────────────────
(def ^:private default-size :md)
(def ^:private size->kw {"sm" :sm "md" :md "lg" :lg})
(def ^:private kw->size {:sm "sm" :md "md" :lg "lg"})

(defn parse-size [s]
  (or (get size->kw (when (string? s) (.toLowerCase s)))
      default-size))

(defn size->attr [s]
  (or (get kw->size s) "md"))

;; ── Current step index ───────────────────────────────────────────────────────
(defn parse-current
  "Parse current-step attribute to a non-negative integer, defaulting to 0."
  [s]
  (if (string? s)
    (let [n (js/parseInt (.trim s) 10)]
      (if (and (number? n) (not (js/isNaN n)) (>= n 0))
        n
        0))
    0))

(defn clamp-current
  "Clamp current to [0, steps-count-1], or 0 when there are no steps."
  [current-idx steps-count]
  (if (zero? steps-count)
    0
    (max 0 (min current-idx (dec steps-count)))))

;; ── Steps data ───────────────────────────────────────────────────────────────
(defn parse-steps
  "Parse the steps attribute value into a vector of {:label string :description string|nil}.

  Accepted formats:
    - Positive integer string  \"3\"  → [{:label \"Step 1\"} {:label \"Step 2\"} {:label \"Step 3\"}]
    - JSON array string        \"[{\\\"label\\\":\\\"One\\\"}]\"
  Unrecognised / nil → []."
  [s]
  (if (string? s)
    (let [trimmed (.trim s)]
      (cond
        (re-matches #"^\d+$" trimmed)
        (let [n (js/parseInt trimmed 10)]
          (if (and (pos? n) (<= n 200))
            (mapv (fn [i] {:label (str "Step " i) :description nil})
                  (range 1 (inc n)))
            []))

        (and (not= "" trimmed) (.startsWith trimmed "["))
        (try
          (let [^js parsed (js/JSON.parse trimmed)]
            (if (js/Array.isArray parsed)
              (mapv (fn [^js item]
                      {:label       (let [l (gobj/get item "label")]
                                      (if (string? l) l ""))
                       :description (let [d (gobj/get item "description")]
                                      (when (string? d) d))})
                    (array-seq parsed))
              []))
          (catch :default _ []))

        :else []))
    []))

;; ── Step state ───────────────────────────────────────────────────────────────
(defn step-state
  "Return :complete, :current, or :upcoming for the step at index i."
  [i current-idx]
  (cond
    (< i current-idx) :complete
    (= i current-idx) :current
    :else             :upcoming))

(defn state->attr [state]
  (case state
    :complete "complete"
    :current  "current"
    :upcoming "upcoming"))

;; ── View model ───────────────────────────────────────────────────────────────
(defn normalize
  "Normalize raw attribute inputs into a stable view-model map.

  Input keys:
    :steps-raw       string | nil
    :current-raw     string | nil
    :orientation-raw string | nil
    :size-raw        string | nil
    :disabled?       boolean

  Output keys:
    :steps       [{:label string :description string|nil} ...]
    :current     int  (clamped)
    :orientation :horizontal | :vertical
    :size        :sm | :md | :lg
    :disabled?   boolean"
  [{:keys [steps-raw current-raw orientation-raw size-raw disabled?]}]
  (let [steps  (parse-steps steps-raw)
        n      (count steps)]
    {:steps       steps
     :current     (clamp-current (parse-current current-raw) n)
     :orientation (parse-orientation orientation-raw)
     :size        (parse-size size-raw)
     :disabled?   (boolean disabled?)}))

;; ── Event detail ─────────────────────────────────────────────────────────────
(defn change-detail
  "Build event detail map for an x-stepper-change event."
  [from to]
  {:from from :to to})
