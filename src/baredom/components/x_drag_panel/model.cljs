(ns baredom.components.x-drag-panel.model)

(def tag-name "x-drag-panel")

;; ── Attribute name constants ─────────────────────────────────────────────────
(def attr-kind        "kind")
(def attr-value       "value")
(def attr-label       "label")
(def attr-grab        "grab")
(def attr-auto-scroll "auto-scroll")
(def attr-disabled    "disabled")
(def attr-pending     "pending")

(def observed-attributes
  #js [attr-kind attr-value attr-label attr-grab attr-auto-scroll
       attr-disabled attr-pending])

;; ── Event name constants ─────────────────────────────────────────────────────
(def event-drag-start  "x-drag-panel-drag-start")
(def event-drag-cancel "x-drag-panel-drag-cancel")

;; ── Part name constants ──────────────────────────────────────────────────────
(def part-panel  "panel")
(def part-handle "handle")
(def part-grip   "grip")
(def part-body   "body")

;; ── Grab mode constants ──────────────────────────────────────────────────────
(def grab-handle  "handle")
(def grab-surface "surface")

;; ── Auto-scroll mode constants ───────────────────────────────────────────────
(def auto-scroll-auto "auto")
(def auto-scroll-none "none")

;; ── Cancel reason constants ──────────────────────────────────────────────────
(def reason-escape  "escape")
(def reason-no-zone "no-zone")

;; ── Pointer type constants ───────────────────────────────────────────────────
(def pointer-touch "touch")

;; ── Default values ───────────────────────────────────────────────────────────
(def default-label "Draggable panel")

;; Milliseconds a touch press must be held before it arms a drag in surface
;; mode, and the pixels of travel that cancel the wait. Tuning values — see the
;; long-press rationale in `long-press-required?`.
(def long-press-ms   250)
(def long-press-slop 8)

;; Auto-scroll tuning. The band is capped in pixels and as a fraction of the
;; container, so it degrades sensibly inside small scrollers.
(def scroll-edge-max   48)
(def scroll-edge-ratio 0.15)
(def scroll-max-speed  900)

;; ── Normalisation ────────────────────────────────────────────────────────────
(defn normalize-label
  "Normalize the raw label attribute. Falls back to default-label."
  [raw]
  (if (and (string? raw) (not= raw ""))
    raw
    default-label))

(defn normalize-text
  "Normalize a raw free-text attribute (`kind`, `value`) to a string.
  Absent and blank collapse to the empty string so downstream comparisons never
  have to distinguish nil from \"\"."
  [raw]
  (if (string? raw) raw ""))

(defn normalize-grab
  "Normalize the raw grab attribute to `grab-surface` or `grab-handle`.

  Anything unrecognised falls back to `grab-handle` deliberately: handle mode is
  the conservative choice, since surface mode swallows presses on interactive
  content inside the panel body."
  [raw]
  (if (= raw grab-surface) grab-surface grab-handle))

(defn normalize-auto-scroll
  "Normalize the raw auto-scroll attribute to `auto-scroll-none` or
  `auto-scroll-auto`. Unrecognised values fall back to `auto-scroll-auto`,
  because a board deeper than the viewport is close to unusable without it."
  [raw]
  (if (= raw auto-scroll-none) auto-scroll-none auto-scroll-auto))

(defn normalize
  "Normalise raw attribute inputs into a stable view-model map.

  Input keys:
    :kind-raw          string | nil
    :value-raw         string | nil
    :label-raw         string | nil
    :grab-raw          string | nil
    :auto-scroll-raw   string | nil
    :disabled-present? boolean
    :pending-present?  boolean

  Output keys:
    :kind         string
    :value        string
    :label        string
    :grab         string  (grab-handle | grab-surface)
    :auto-scroll  string  (auto-scroll-auto | auto-scroll-none)
    :disabled?    boolean
    :pending?     boolean
    :draggable?   boolean"
  [{:keys [kind-raw value-raw label-raw grab-raw auto-scroll-raw
           disabled-present? pending-present?]}]
  (let [disabled? (boolean disabled-present?)
        pending?  (boolean pending-present?)]
    {:kind        (normalize-text kind-raw)
     :value       (normalize-text value-raw)
     :label       (normalize-label label-raw)
     :grab        (normalize-grab grab-raw)
     :auto-scroll (normalize-auto-scroll auto-scroll-raw)
     :disabled?   disabled?
     :pending?    pending?
     :draggable?  (boolean (and (not disabled?) (not pending?)))}))

;; ── Drag geometry ────────────────────────────────────────────────────────────
(defn drag-position
  "Panel top-left for a pointer at `{:pointer-x :pointer-y}`, given the grab
  offset captured at pointerdown. Returns `{:x :y}` in viewport coordinates.

  The lifted position is never stored — it is derived from the drag value, so
  there is exactly one answer to \"where is the panel\" at any moment."
  [{:keys [pointer-x pointer-y offset-x offset-y]}]
  {:x (- (or pointer-x 0) (or offset-x 0))
   :y (- (or pointer-y 0) (or offset-y 0))})

(defn drag-delta
  "Translation to apply to the lifted surface, relative to where it started."
  [{:keys [start-x start-y] :as drag}]
  (let [{:keys [x y]} (drag-position drag)]
    {:dx (- x (or start-x 0))
     :dy (- y (or start-y 0))}))

;; ── Long-press arming ────────────────────────────────────────────────────────
(defn long-press-required?
  "True when a press must be held before it arms a drag.

  Only touch pointers in surface grab mode: the grab area is the whole panel, so
  arming immediately would need `touch-action: none` across it and the user could
  no longer scroll the board by swiping over a card. Mouse and pen have no such
  conflict, and handle mode targets a small deliberate strip."
  [{:keys [pointer-type grab]}]
  (boolean (and (= pointer-type pointer-touch)
                (= grab grab-surface))))

(defn travelled?
  "True when a pointer has moved beyond `slop` pixels from its press origin.

  Used to cancel a pending long-press: travel before the timer fires is a scroll
  gesture, not a drag."
  [{:keys [start-x start-y pointer-x pointer-y slop]}]
  (let [dx (- (or pointer-x 0) (or start-x 0))
        dy (- (or pointer-y 0) (or start-y 0))
        s  (or slop long-press-slop)]
    (> (js/Math.hypot dx dy) s)))

;; ── Auto-scroll ──────────────────────────────────────────────────────────────
(defn edge-band
  "Width of the auto-scroll trigger band for a container dimension."
  [dimension]
  (min scroll-edge-max (* scroll-edge-ratio (max 0 (or dimension 0)))))

(defn- ramp
  "Fraction of maximum speed for a pointer `distance` inside a band of `band`
  pixels. Clamped to [0 1] so a pointer dragged outside the container does not
  produce a runaway velocity."
  [distance band]
  (if (<= band 0)
    0
    (max 0 (min 1 (/ (- band distance) band)))))

(defn axis-velocity
  "Auto-scroll speed in pixels per second along one axis.

  `pos` is the pointer coordinate and `lo`/`hi` the container's edges on that
  axis. Negative scrolls toward `lo`, positive toward `hi`, zero between the two
  bands. Called once per axis because the vertical and horizontal scrollers are
  resolved independently — a board scrolls sideways while its columns scroll
  down, and a drag into a corner needs both."
  [{:keys [pos lo hi max-speed]}]
  (let [lo    (or lo 0)
        hi    (or hi 0)
        size  (- hi lo)
        speed (or max-speed scroll-max-speed)]
    (if (or (nil? pos) (<= size 0))
      0
      (let [band      (edge-band size)
            from-lo   (- pos lo)
            from-hi   (- hi pos)]
        (cond
          (< from-lo from-hi) (- (* speed (ramp from-lo band)))
          :else               (* speed (ramp from-hi band)))))))

(defn scroll-delta
  "Pixels to scroll this frame for `velocity` px/sec over `dt-ms` milliseconds.

  Frame-delta driven rather than per-frame constant, so the result does not
  double on a 120Hz display."
  [velocity dt-ms]
  (/ (* (or velocity 0) (or dt-ms 0)) 1000))

;; ── Event details ────────────────────────────────────────────────────────────
(defn drag-start-detail
  "Build the x-drag-panel-drag-start CustomEvent detail."
  [kind value]
  #js {:kind kind :value value})

(defn drag-cancel-detail
  "Build the x-drag-panel-drag-cancel CustomEvent detail."
  [kind value reason]
  #js {:kind kind :value value :reason reason})

;; Seven entries — below the point where a CLJS map literal stops being an
;; array-map. An eighth is fine; a ninth would reshuffle every generated
;; adapter file.
(def property-api
  {:kind       {:type 'string  :reflects-attribute attr-kind        :default ""}
   :value      {:type 'string  :reflects-attribute attr-value       :default ""}
   :label      {:type 'string  :reflects-attribute attr-label       :default default-label}
   :grab       {:type 'string  :reflects-attribute attr-grab        :default grab-handle}
   :autoScroll {:type 'string  :reflects-attribute attr-auto-scroll :default auto-scroll-auto}
   :disabled   {:type 'boolean :reflects-attribute attr-disabled}
   :pending    {:type 'boolean :reflects-attribute attr-pending}})

(def event-schema
  {event-drag-start  {:cancelable false :detail {:kind 'string :value 'string}}
   event-drag-cancel {:cancelable false :detail {:kind   'string
                                                 :value  'string
                                                 :reason 'string}}})

(def method-api {})
