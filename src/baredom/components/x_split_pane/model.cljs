(ns baredom.components.x-split-pane.model
  "Pure model layer for x-split-pane — a resizable two-panel layout.

   No DOM, no side effects. Every function here is a plain data transform
   and is exercised directly by model_test.cljs with sparse inputs.")

;; ── Tag, attribute, event, slot constants ────────────────────────────────────
(def tag-name "x-split-pane")

(def attr-orientation   "orientation")
(def attr-position      "position")
(def attr-min-start     "min-start")
(def attr-min-end       "min-end")
(def attr-disabled      "disabled")
(def attr-divider-label "divider-label")

(def observed-attributes
  #js [attr-orientation attr-position attr-min-start attr-min-end
       attr-disabled attr-divider-label])

(def event-resize     "x-split-pane-resize")
(def event-resize-end "x-split-pane-resize-end")

(def slot-start "start")
(def slot-end   "end")

;; ── Enum values & defaults ───────────────────────────────────────────────────
(def orientation-horizontal "horizontal")
(def orientation-vertical   "vertical")

(def ^:private valid-orientations #{orientation-horizontal orientation-vertical})

(def default-orientation   orientation-horizontal)
(def default-position      50)
(def default-divider-label "Resize panels")

(def min-position       0)
(def max-position       100)
(def keyboard-step      1)
(def keyboard-page-step 10)

;; ── API metadata ─────────────────────────────────────────────────────────────
(def property-api
  {:orientation  {:type 'string  :reflects-attribute attr-orientation   :default default-orientation}
   :position     {:type 'number  :reflects-attribute attr-position      :default default-position}
   :minStart     {:type 'number  :reflects-attribute attr-min-start      :default 0}
   :minEnd       {:type 'number  :reflects-attribute attr-min-end        :default 0}
   :disabled     {:type 'boolean :reflects-attribute attr-disabled}
   :dividerLabel {:type 'string  :reflects-attribute attr-divider-label  :default default-divider-label}})

(def event-schema
  {event-resize     {:detail {:position 'number :orientation 'string}}
   event-resize-end {:detail {:position 'number :orientation 'string}}})

(def method-api {})

;; ── Pure helpers ─────────────────────────────────────────────────────────────
(defn clamp
  "Clamp `n` into the inclusive range [lo, hi]."
  [n lo hi]
  (-> n (max lo) (min hi)))

(defn- parse-enum [valid default s]
  (let [v (when (string? s) (.toLowerCase (.trim s)))]
    (if (contains? valid v) v default)))

(defn parse-orientation
  "Parse the `orientation` attribute. Unknown / nil fall back to horizontal."
  [s]
  (parse-enum valid-orientations default-orientation s))

(defn parse-number
  "Parse a numeric attribute string, falling back to `default-val` for
   nil / non-string / non-numeric input."
  [s default-val]
  (if (string? s)
    (let [n (js/parseFloat s)]
      (if (js/isNaN n) default-val n))
    default-val))

(defn clamp-position
  "Clamp a position percentage into [0, 100]."
  [n]
  (clamp n min-position max-position))

(defn parse-position
  "Parse the `position` attribute into a clamped percentage 0–100."
  [s]
  (clamp-position (parse-number s default-position)))

(defn parse-min
  "Parse a `min-start` / `min-end` attribute into a non-negative pixel count."
  [s]
  (max 0 (parse-number s 0)))

(defn pointer->percent
  "Convert a pointer client coordinate to a percentage of the container.
   `client` is clientX (horizontal split) or clientY (vertical split);
   `rect-start` is the container's left/top edge and `rect-size` its
   width/height in px. The result is clamped to [0, 100]."
  [client rect-start rect-size]
  (if (pos? rect-size)
    (clamp-position (* 100 (/ (- client rect-start) rect-size)))
    default-position))

(defn clamp-by-mins
  "Clamp a position percentage so the start pane stays >= `min-start-px`
   and the end pane stays >= `min-end-px`, given the container's px size.
   When the two minimums overlap (no feasible position satisfies both)
   a clamped midpoint of the conflicting range is returned so the result
   stays deterministic and within [0,100]."
  [percent container-px min-start-px min-end-px]
  (if (pos? container-px)
    (let [lo (* 100 (/ min-start-px container-px))
          hi (- 100 (* 100 (/ min-end-px container-px)))]
      (if (<= lo hi)
        (clamp percent lo hi)
        ;; Overlapping minimums — a single minimum can exceed the
        ;; container on small viewports, pushing lo > 100 or hi < 0,
        ;; so the midpoint must be clamped back into range.
        (clamp-position (/ (+ lo hi) 2))))
    (clamp-position percent)))

(defn keyboard-delta
  "Map a keyboard `key` to a position change for the given `orientation`.
   Increasing the position grows the start pane. Returns a numeric
   percentage delta, the keyword :to-min or :to-max, or nil when the key
   is not a resize key."
  [key orientation]
  (let [horizontal? (= orientation orientation-horizontal)
        inc-key     (if horizontal? "ArrowRight" "ArrowDown")
        dec-key     (if horizontal? "ArrowLeft" "ArrowUp")]
    (cond
      (= key inc-key)    keyboard-step
      (= key dec-key)    (- keyboard-step)
      (= key "PageUp")   keyboard-page-step
      (= key "PageDown") (- keyboard-page-step)
      (= key "Home")     :to-min
      (= key "End")      :to-max
      :else              nil)))

(defn next-position
  "Compute the next position from `current` and a `keyboard-delta` result.
   `delta` is a numeric percentage, :to-min, or :to-max. The result is
   clamped to [0, 100]."
  [current delta]
  (clamp-position
   (cond
     (= delta :to-min) min-position
     (= delta :to-max) max-position
     :else             (+ current delta))))

;; ── Normalize ────────────────────────────────────────────────────────────────
(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :orientation-raw    string | nil
    :position-raw       string | nil
    :min-start-raw      string | nil
    :min-end-raw        string | nil
    :disabled?          boolean
    :divider-label-raw  string | nil

  Output keys:
    :orientation   \"horizontal\" | \"vertical\"
    :position      number 0–100
    :min-start     number >= 0 (px)
    :min-end       number >= 0 (px)
    :disabled?     boolean
    :divider-label non-empty string"
  [{:keys [orientation-raw position-raw min-start-raw min-end-raw
           disabled? divider-label-raw]}]
  {:orientation   (parse-orientation orientation-raw)
   :position      (parse-position position-raw)
   :min-start     (parse-min min-start-raw)
   :min-end       (parse-min min-end-raw)
   :disabled?     (boolean disabled?)
   :divider-label (if (and (string? divider-label-raw)
                           (not= "" (.trim divider-label-raw)))
                    divider-label-raw
                    default-divider-label)})
