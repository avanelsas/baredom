(ns baredom.components.x-scroll-story.model
  (:require [baredom.utils.model :as mu]))

;; ── Tag name ────────────────────────────────────────────────────────────────
(def tag-name "x-scroll-story")

;; ── Attribute name constants ────────────────────────────────────────────────
(def attr-layout             "layout")
(def attr-threshold          "threshold")
(def attr-split              "split")
(def attr-disabled           "disabled")
(def attr-label              "label")
(def attr-autoplay           "autoplay")
(def attr-autoplay-speed     "autoplay-speed")
(def attr-autoplay-loop      "autoplay-loop")
(def attr-autoplay-indicator "autoplay-indicator")

(def observed-attributes
  #js [attr-layout attr-threshold attr-split attr-disabled attr-label
       attr-autoplay attr-autoplay-speed attr-autoplay-loop attr-autoplay-indicator])

;; ── Event name constants ────────────────────────────────────────────────────
(def event-step-change "x-scroll-story-step-change")
(def event-step-enter  "x-scroll-story-step-enter")
(def event-step-leave  "x-scroll-story-step-leave")
(def event-progress    "x-scroll-story-progress")
(def event-enter          "x-scroll-story-enter")
(def event-leave          "x-scroll-story-leave")
(def event-autoplay-pause  "x-scroll-story-autoplay-pause")
(def event-autoplay-resume "x-scroll-story-autoplay-resume")

;; ── Data attribute set on active step child ─────────────────────────────────
(def data-active "data-active")

;; ── Property API ────────────────────────────────────────────────────────────
(def property-api
  {:layout            {:type 'string}
   :threshold         {:type 'number}
   :split             {:type 'number}
   :disabled          {:type 'boolean}
   :label             {:type 'string}
   :activeIndex       {:type 'number  :read-only true}
   :progress          {:type 'number  :read-only true}
   :autoplay          {:type 'boolean}
   :autoplaySpeed     {:type 'number}
   :autoplayLoop      {:type 'boolean}
   :autoplayIndicator {:type 'boolean}
   :autoplayPaused    {:type 'boolean :read-only true}})

;; ── Event schema ────────────────────────────────────────────────────────────
(def event-schema
  {event-step-change {:detail {:index 'number :id 'string
                               :previousIndex 'number :previousId 'string}
                      :cancelable false}
   event-step-enter  {:detail {:index 'number :id 'string :progress 'number}
                      :cancelable false}
   event-step-leave  {:detail {:index 'number :id 'string :progress 'number}
                      :cancelable false}
   event-progress    {:detail {:progress 'number :activeIndex 'number :activeId 'string}
                      :cancelable false}
   event-enter          {:detail {:progress 'number} :cancelable false}
   event-leave          {:detail {:progress 'number} :cancelable false}
   event-autoplay-pause  {:detail {:progress 'number :activeIndex 'number :activeId 'string}
                          :cancelable false}
   event-autoplay-resume {:detail {:progress 'number :activeIndex 'number :activeId 'string}
                          :cancelable false}})

;; ── Allowed enum values ─────────────────────────────────────────────────────
(def ^:private allowed-layouts #{"left" "right" "top"})

;; ── Parsing functions ───────────────────────────────────────────────────────

(defn parse-layout
  "Normalise layout attribute to \"left\", \"right\", or \"top\".
  Unknown / nil values fall back to \"left\"."
  [s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? allowed-layouts v) v "left")))

(defn parse-threshold
  "Parse threshold attribute to a float clamped to [0,1]. Default 0.5."
  [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (js/isNaN n) 0.5 (max 0.0 (min 1.0 n))))
    0.5))

(defn parse-split
  "Parse split attribute to a float clamped to [0.1,0.9]. Default 0.5."
  [s]
  (if (string? s)
    (let [n (js/parseFloat (.trim s))]
      (if (js/isNaN n) 0.5 (max 0.1 (min 0.9 n))))
    0.5))

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
    :layout-raw           string | nil
    :threshold-raw        string | nil
    :split-raw            string | nil
    :disabled-attr        string | nil  (hasAttribute)
    :label-raw            string | nil
    :autoplay-attr        string | nil
    :autoplay-speed-raw   string | nil
    :autoplay-loop-attr   string | nil
    :autoplay-indicator-attr string | nil

  Output keys:
    :layout              string — \"left\" | \"right\" | \"top\"
    :threshold           number — [0,1]
    :split               number — [0.1,0.9]
    :disabled?           boolean
    :label               string
    :autoplay?           boolean
    :autoplay-speed      number — [1,1000]
    :autoplay-loop?      boolean
    :autoplay-indicator? boolean"
  [{:keys [layout-raw threshold-raw split-raw disabled-attr label-raw
           autoplay-attr autoplay-speed-raw autoplay-loop-attr autoplay-indicator-attr]}]
  {:layout              (parse-layout layout-raw)
   :threshold           (parse-threshold threshold-raw)
   :split               (parse-split split-raw)
   :disabled?           (mu/parse-bool-present disabled-attr)
   :label               (or label-raw "")
   :autoplay?           (mu/parse-bool-present autoplay-attr)
   :autoplay-speed      (parse-autoplay-speed autoplay-speed-raw)
   :autoplay-loop?      (mu/parse-bool-present autoplay-loop-attr)
   :autoplay-indicator? (mu/parse-bool-present autoplay-indicator-attr)})

;; ── Step activation math ────────────────────────────────────────────────────

(defn compute-overall-progress
  "Compute scroll progress [0,1] for the entire steps container.
  0 = container just entered at the bottom, 1 = about to leave at the top."
  [container-top container-height viewport-height]
  (let [total   (+ viewport-height container-height)
        scrolled (- viewport-height container-top)]
    (max 0.0 (min 1.0 (/ scrolled total)))))

(defn find-active-step
  "Given a vector of {:top :bottom} rects and a trigger-y position (pixels
  from the top of the viewport), return the index of the active step or -1.

  A step is active when it spans the trigger line (top <= trigger-y < bottom).
  If no step spans it, the last step whose top is above the trigger line wins."
  [step-rects trigger-y]
  (let [n (count step-rects)]
    (if (zero? n)
      -1
      (loop [i 0
             spanning -1
             last-above -1]
        (if (>= i n)
          (if (>= spanning 0) spanning last-above)
          (let [{:keys [top bottom]} (nth step-rects i)]
            (recur (inc i)
                   (if (and (<= top trigger-y) (> bottom trigger-y)) i spanning)
                   (if (<= top trigger-y) i last-above))))))))

(defn compute-step-progress
  "Compute how far a step has moved through the viewport.
  0 = step bottom just reached trigger line, 1 = step top passed it."
  [step-top step-height trigger-y]
  (if (<= step-height 0)
    0.0
    (let [progress (/ (- trigger-y step-top) step-height)]
      (max 0.0 (min 1.0 progress)))))

;; ── Event detail builders ───────────────────────────────────────────────────

(defn step-change-detail [index id prev-index prev-id]
  {:index index :id id :previousIndex prev-index :previousId prev-id})

(defn step-enter-detail [index id progress]
  {:index index :id id :progress progress})

(defn step-leave-detail [index id progress]
  {:index index :id id :progress progress})

(defn progress-detail [progress active-index active-id]
  {:progress progress :activeIndex active-index :activeId active-id})

(defn enter-detail [progress]
  {:progress progress})

(defn leave-detail [progress]
  {:progress progress})

(defn autoplay-pause-detail [progress active-index active-id]
  {:progress progress :activeIndex active-index :activeId active-id})

(defn autoplay-resume-detail [progress active-index active-id]
  {:progress progress :activeIndex active-index :activeId active-id})

(def method-api nil)
