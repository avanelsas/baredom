(ns baredom.components.x-floating-panel.model)

(def tag-name "x-floating-panel")

;; ── Attribute name constants ─────────────────────────────────────────────────
(def attr-open          "open")
(def attr-x             "x")
(def attr-y             "y")
(def attr-closable      "closable")
(def attr-resizable     "resizable")
(def attr-label         "label")
(def attr-focus-on-open "focus-on-open")
(def attr-step          "step")

;; `resizable` is observed for the sake of the public attribute surface only —
;; nothing in the model reads it, the CSS reacts to `:host([resizable])`
;; directly.
(def observed-attributes
  #js [attr-open attr-x attr-y attr-closable attr-resizable attr-label
       attr-focus-on-open attr-step])

;; ── Event name constants ─────────────────────────────────────────────────────
(def event-toggle          "x-floating-panel-toggle")
(def event-dismiss-request "x-floating-panel-dismiss-request")
(def event-move            "x-floating-panel-move")

;; ── Part name constants ──────────────────────────────────────────────────────
(def part-panel  "panel")
(def part-handle "handle")
(def part-close  "close")
(def part-body   "body")

;; ── Dismiss reason constants ─────────────────────────────────────────────────
(def reason-close-button "close-button")
(def reason-escape       "escape")

;; ── Move source constants ────────────────────────────────────────────────────
(def source-pointer  "pointer")
(def source-keyboard "keyboard")

;; ── Default values ───────────────────────────────────────────────────────────
(def default-label "Floating panel")
(def default-step  10)
(def fine-step     1)

;; Pixels of the panel that must remain on screen horizontally after clamping.
(def min-visible 64)

(defn normalize-label
  "Normalize raw label attribute value. Falls back to default-label."
  [raw]
  (if (and (string? raw) (not= raw ""))
    raw
    default-label))

(defn parse-coordinate
  "Parse a raw `x` / `y` attribute value into a finite number.

  Returns nil for absent, blank, or non-numeric input. Absent and invalid
  are deliberately the same state: coercing garbage to 0 would teleport the
  panel to the viewport corner, whereas nil falls through to the CSS default
  placement and leaves the panel where the author last saw it."
  [raw]
  (when (string? raw)
    (let [trimmed (.trim raw)]
      (when (not= "" trimmed)
        (let [n (js/parseFloat trimmed)]
          (when (js/isFinite n) n))))))

(defn parse-step
  "Parse the `step` attribute into a positive number of pixels. Absent,
  non-numeric, and non-positive values all fall back to default-step."
  [raw]
  (let [n (parse-coordinate raw)]
    (if (and n (pos? n)) n default-step)))

(def ^:private arrow-deltas
  {"ArrowLeft"  {:dx -1 :dy 0}
   "ArrowRight" {:dx 1  :dy 0}
   "ArrowUp"    {:dx 0  :dy -1}
   "ArrowDown"  {:dx 0  :dy 1}})

(defn arrow-delta
  "Unit direction map `{:dx :dy}` for an arrow key, or nil for any other key."
  [key-name]
  (get arrow-deltas key-name))

(defn drag-position
  "Panel top-left for a pointer at `{:pointer-x :pointer-y}`, given the grab
  offset captured at pointerdown. Returns `{:x :y}` in viewport coordinates."
  [{:keys [pointer-x pointer-y offset-x offset-y]}]
  {:x (- pointer-x offset-x)
   :y (- pointer-y offset-y)})

(defn drag-moved?
  "True when the pointer has travelled since the grab.

  A press that never moved is a click on the handle, not a drag. Committing it
  would announce a move that did not happen and would pin an unpositioned panel
  to viewport coordinates, silently taking it out of the CSS default placement."
  [{:keys [start-x start-y] :as drag}]
  (not= (drag-position drag) {:x start-x :y start-y}))

(defn nudge-position
  "Move `{:x :y}` by one keyboard `step` in the direction `{:dx :dy}`."
  [{:keys [x y dx dy step]}]
  {:x (+ (or x 0) (* (or dx 0) step))
   :y (+ (or y 0) (* (or dy 0) step))})

(defn- clamp-between [v lo hi]
  (max lo (min v hi)))

(defn clamp-position
  "Constrain a panel top-left so its handle bar stays reachable.

  Clamping the whole panel inside the viewport makes a tall panel unusable on
  a short viewport, so the policy is weaker: at least `min-visible` pixels of
  the panel remain on screen horizontally, and the handle bar (height
  `handle-h`) remains fully on screen vertically.

  All measurements are supplied by the caller — this function never touches
  the DOM."
  [{:keys [x y w handle-h vw vh]}]
  (let [visible (min min-visible w)
        x-min   (- visible w)
        x-max   (- vw visible)
        y-max   (max 0 (- vh handle-h))]
    {:x (clamp-between x x-min x-max)
     :y (clamp-between y 0 y-max)}))

(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  `resizable` is deliberately absent: no effect reads it, so carrying it here
  would only add a field to compare on every attribute change.

  Input keys:
    :open-present?          boolean
    :x-raw                  string | nil
    :y-raw                  string | nil
    :closable-present?      boolean
    :label-raw              string | nil
    :focus-on-open-present? boolean
    :step-raw               string | nil
    :header-slotted?        boolean

  Output keys:
    :open?           boolean
    :x               number | nil  (nil → CSS default placement governs)
    :y               number | nil
    :closable?       boolean
    :label           string
    :focus-on-open?  boolean
    :step            number
    :header-slotted? boolean"
  [{:keys [open-present? x-raw y-raw closable-present?
           label-raw focus-on-open-present? step-raw header-slotted?]}]
  {:open?           (boolean open-present?)
   :x               (parse-coordinate x-raw)
   :y               (parse-coordinate y-raw)
   :closable?       (boolean closable-present?)
   :label           (normalize-label label-raw)
   :focus-on-open?  (boolean focus-on-open-present?)
   :step            (parse-step step-raw)
   :header-slotted? (boolean header-slotted?)})

(defn toggle-event-detail
  "Build the x-floating-panel-toggle CustomEvent detail."
  [open]
  #js {:open open})

(defn dismiss-event-detail
  "Build the x-floating-panel-dismiss-request CustomEvent detail."
  [reason]
  #js {:reason reason})

(defn move-event-detail
  "Build the x-floating-panel-move CustomEvent detail."
  [x y source]
  #js {:x x :y y :source source})

;; Eight entries — one below the point where a CLJS map literal stops being an
;; array-map. A ninth would reshuffle every generated adapter file.
(def property-api
  {:open        {:type 'boolean :reflects-attribute attr-open}
   :x           {:type 'number  :reflects-attribute attr-x}
   :y           {:type 'number  :reflects-attribute attr-y}
   :step        {:type 'number  :reflects-attribute attr-step :default default-step}
   :closable    {:type 'boolean :reflects-attribute attr-closable}
   :resizable   {:type 'boolean :reflects-attribute attr-resizable}
   :label       {:type 'string  :reflects-attribute attr-label :default default-label}
   :focusOnOpen {:type 'boolean :reflects-attribute attr-focus-on-open}})

(def event-schema
  {event-toggle          {:cancelable false :detail {:open 'boolean}}
   event-dismiss-request {:cancelable true  :detail {:reason 'string}}
   event-move            {:cancelable false :detail {:x      'number
                                                     :y      'number
                                                     :source 'string}}})

(def method-api
  {:show   {:args [] :returns 'void}
   :hide   {:args [] :returns 'void}
   :toggle {:args [] :returns 'void}})
