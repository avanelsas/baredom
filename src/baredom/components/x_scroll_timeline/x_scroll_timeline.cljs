(ns baredom.components.x-scroll-timeline.x-scroll-timeline
  (:require
[baredom.utils.component :as component]
            [baredom.utils.dom :as du]
               [goog.object :as gobj]
   [baredom.components.x-scroll-timeline.model :as model]))

;; ── Constants ───────────────────────────────────────────────────────────────
(def ^:private svg-ns "http://www.w3.org/2000/svg")

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs         "__xScrollTimelineRefs")
(def ^:private k-model        "__xScrollTimelineModel")
(def ^:private k-handlers     "__xScrollTimelineHandlers")
(def ^:private k-io           "__xScrollTimelineIO")
(def ^:private k-raf          "__xScrollTimelineRAF")
(def ^:private k-visible      "__xScrollTimelineVisible")
(def ^:private k-active-index "__xScrollTimelineActiveIndex")
(def ^:private k-last-prog    "__xScrollTimelineLastProg")
(def ^:private k-entry-states "__xScrollTimelineEntryStates")
(def ^:private k-marker-els       "__xScrollTimelineMarkerEls")
(def ^:private k-date-els         "__xScrollTimelineDateEls")
(def ^:private k-autoplay-raf     "__xScrollTimelineAutoplayRAF")
(def ^:private k-autoplay-last    "__xScrollTimelineAutoplayLast")
(def ^:private k-autoplay-paused  "__xScrollTimelineAutoplayPaused")
(def ^:private k-autoplay-handlers "__xScrollTimelineAutoplayHandlers")

;; ── Styles ──────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;position:relative;"
   "color-scheme:light dark;"
   "--x-scroll-timeline-track-color:rgba(0,0,0,0.12);"
   "--x-scroll-timeline-track-fill-color:var(--x-color-primary,#3b82f6);"
   "--x-scroll-timeline-track-width:3px;"
   "--x-scroll-timeline-marker-size:14px;"
   "--x-scroll-timeline-marker-color:rgba(0,0,0,0.15);"
   "--x-scroll-timeline-marker-active-color:var(--x-color-primary,#3b82f6);"
   "--x-scroll-timeline-marker-border-color:var(--x-color-primary,#3b82f6);"
   "--x-scroll-timeline-entry-gap:2rem;"
   "--x-scroll-timeline-date-color:rgba(0,0,0,0.5);"
   "--x-scroll-timeline-date-font-size:0.8125rem;"
   "--x-scroll-timeline-transition-duration:300ms;"
   "--x-scroll-timeline-curve-amplitude:60;"
   "--x-scroll-timeline-disabled-opacity:0.55;}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-scroll-timeline-track-color:rgba(255,255,255,0.12);"
   "--x-scroll-timeline-marker-color:rgba(255,255,255,0.18);"
   "--x-scroll-timeline-date-color:rgba(255,255,255,0.5);}}"

   ;; Container
   "[part=container]{"
   "position:relative;padding:0;}"

   ;; Track column — centered by default
   "[part=track]{"
   "position:absolute;top:0;bottom:0;"
   "left:50%;transform:translateX(-50%);"
   "width:var(--x-scroll-timeline-track-width);"
   "pointer-events:none;z-index:1;}"

   ;; Left layout: track on the left edge
   ":host([data-layout=left]) [part=track]{"
   "left:calc(var(--x-scroll-timeline-marker-size) / 2);"
   "transform:none;}"

   ;; Right layout: track on the right edge
   ":host([data-layout=right]) [part=track]{"
   "left:auto;right:calc(var(--x-scroll-timeline-marker-size) / 2);"
   "transform:none;}"

   ;; Straight track line
   "[part=track-line]{"
   "position:absolute;top:0;bottom:0;left:0;right:0;"
   "background:var(--x-scroll-timeline-track-color);"
   "border-radius:999px;}"

   ;; Straight track fill (progress)
   "[part=track-fill]{"
   "position:absolute;top:0;left:0;right:0;"
   "height:0%;"
   "background:var(--x-scroll-timeline-track-fill-color);"
   "border-radius:999px;"
   "transition:height 60ms linear;}"

   ;; SVG track (curved mode) — positioned in container, not inside track div
   "[part=track-svg]{"
   "display:none;"
   "position:absolute;top:0;left:0;"
   "width:100%;height:100%;overflow:visible;pointer-events:none;z-index:1;}"

   ":host([data-track=curved]) [part=track-line],"
   ":host([data-track=curved]) [part=track-fill]{display:none;}"

   ":host([data-track=curved]) [part=track-svg]{display:block;}"

   ;; SVG paths — stroke-width in px works because viewBox matches pixel dimensions
   "[part=track-path]{"
   "fill:none;"
   "stroke:var(--x-scroll-timeline-track-color);"
   "stroke-width:3px;"
   "stroke-linecap:round;}"

   "[part=track-fill-path]{"
   "fill:none;"
   "stroke:var(--x-scroll-timeline-track-fill-color);"
   "stroke-width:3px;"
   "stroke-linecap:round;"
   "transition:stroke-dashoffset 60ms linear;}"

   ;; Entries wrapper
   "[part=entries]{"
   "position:relative;z-index:2;}"

   ;; Slotted children — alternating layout
   "::slotted(*){"
   "display:block;"
   "margin-bottom:var(--x-scroll-timeline-entry-gap);"
   "opacity:0.4;"
   "transition:opacity var(--x-scroll-timeline-transition-duration) ease;}"

   "::slotted(:last-child){margin-bottom:0;}"

   "::slotted([data-active]){opacity:1;}"

   ;; Alternating: entries take ~45% width, offset to each side
   ":host([data-layout=alternating]) ::slotted(*){"
   "width:calc(50% - var(--x-scroll-timeline-marker-size));}"

   ":host([data-layout=alternating]) ::slotted([data-side=left]){"
   "margin-left:0;margin-right:auto;text-align:right;"
   "padding-right:1.5rem;}"

   ":host([data-layout=alternating]) ::slotted([data-side=right]){"
   "margin-left:auto;margin-right:0;text-align:left;"
   "padding-left:1.5rem;}"

   ;; Left layout: all entries to the right
   ":host([data-layout=left]) ::slotted(*){"
   "margin-left:calc(var(--x-scroll-timeline-marker-size) + 1.5rem);"
   "width:auto;}"

   ;; Right layout: all entries to the left
   ":host([data-layout=right]) ::slotted(*){"
   "margin-right:calc(var(--x-scroll-timeline-marker-size) + 1.5rem);"
   "width:auto;text-align:right;}"

   ;; Markers
   ".tl-marker{"
   "position:absolute;z-index:3;"
   "width:var(--x-scroll-timeline-marker-size);"
   "height:var(--x-scroll-timeline-marker-size);"
   "border-radius:50%;"
   "background:var(--x-scroll-timeline-marker-color);"
   "border:2px solid transparent;"
   "transform:translate(-50%,-50%);"
   "transition:background var(--x-scroll-timeline-transition-duration) ease,"
   "border-color var(--x-scroll-timeline-transition-duration) ease,"
   "box-shadow var(--x-scroll-timeline-transition-duration) ease;}"

   ".tl-marker[data-active]{"
   "background:var(--x-scroll-timeline-marker-active-color);"
   "border-color:var(--x-scroll-timeline-marker-active-color);"
   "box-shadow:0 0 0 3px rgba(59,130,246,0.25);}"

   ;; Ring style markers
   ":host([data-marker=ring]) .tl-marker{"
   "background:transparent;"
   "border:2px solid var(--x-scroll-timeline-marker-color);}"

   ":host([data-marker=ring]) .tl-marker[data-active]{"
   "background:var(--x-scroll-timeline-marker-active-color);"
   "border-color:var(--x-scroll-timeline-marker-active-color);}"

   ;; Hidden markers
   ":host([data-marker=none]) .tl-marker{display:none;}"

   ;; Marker positioning — centered on track by default
   ".tl-marker{left:50%;}"
   ":host([data-layout=left]) .tl-marker{"
   "left:calc(var(--x-scroll-timeline-marker-size) / 2);}"
   ":host([data-layout=right]) .tl-marker{"
   "left:auto;right:calc(var(--x-scroll-timeline-marker-size) / 2);"
   "transform:translate(50%,-50%);}"

   ;; Date labels
   ".tl-date{"
   "position:absolute;z-index:3;"
   "font-size:var(--x-scroll-timeline-date-font-size);"
   "color:var(--x-scroll-timeline-date-color);"
   "white-space:nowrap;"
   "transform:translateY(-50%);"
   "transition:color var(--x-scroll-timeline-transition-duration) ease;}"

   ".tl-date[data-active]{"
   "color:var(--x-scroll-timeline-marker-active-color);"
   "font-weight:600;}"

   ;; Alternating date positioning — opposite side of entry
   ":host([data-layout=alternating]) .tl-date[data-side=left]{"
   "left:calc(50% + var(--x-scroll-timeline-marker-size));padding-left:0.5rem;}"

   ":host([data-layout=alternating]) .tl-date[data-side=right]{"
   "right:calc(50% + var(--x-scroll-timeline-marker-size));padding-right:0.5rem;text-align:right;}"

   ;; Left layout: dates between track and entries
   ":host([data-layout=left]) .tl-date{"
   "left:calc(var(--x-scroll-timeline-marker-size) + 0.5rem);}"

   ;; Right layout: dates between track and entries
   ":host([data-layout=right]) .tl-date{"
   "right:calc(var(--x-scroll-timeline-marker-size) + 0.5rem);text-align:right;}"

   ;; Disabled state
   ":host([disabled]){"
   "opacity:var(--x-scroll-timeline-disabled-opacity);}"

   ;; Live region
   "[part=live]{"
   "position:absolute;width:1px;height:1px;margin:-1px;"
   "padding:0;overflow:hidden;clip:rect(0,0,0,0);border:0;}"

   ;; Autoplay indicator
   "[part=indicator]{"
   "display:none;position:absolute;top:50%;left:50%;"
   "transform:translate(-50%,-50%);pointer-events:none;"
   "width:48px;height:48px;opacity:0.7;z-index:4;}"

   "[part=indicator]::before,[part=indicator]::after{"
   "content:'';position:absolute;top:8px;width:12px;height:32px;"
   "background:rgba(255,255,255,0.9);border-radius:3px;}"

   "[part=indicator]::before{left:8px;}"
   "[part=indicator]::after{right:8px;}"

   ":host([data-autoplay-paused][autoplay-indicator]) [part=indicator]{"
   "display:block;}"

   ;; Coarse pointer: enlarge markers for touch targets >= 44px
   "@media (pointer:coarse){"
   ":host{--x-scroll-timeline-marker-size:44px;}}"

   ;; Reduced motion
   "@media (prefers-reduced-motion:reduce){"
   "::slotted(*){transition:none !important;}"
   ".tl-marker{transition:none !important;}"
   ".tl-date{transition:none !important;}"
   "[part=track-fill]{transition:none !important;}"
   "[part=track-fill-path]{transition:none !important;}}"))

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style      (.createElement js/document "style")
        container  (.createElement js/document "div")
        track      (.createElement js/document "div")
        track-line (.createElement js/document "div")
        track-fill (.createElement js/document "div")
        track-svg  (.createElementNS js/document svg-ns "svg")
        track-path (.createElementNS js/document svg-ns "path")
        fill-path  (.createElementNS js/document svg-ns "path")
        entries    (.createElement js/document "div")
        slot       (.createElement js/document "slot")
        indicator  (.createElement js/document "div")
        live       (.createElement js/document "div")]

    (set! (.-textContent style) style-text)

    (.setAttribute container  "part" "container")
    (.setAttribute container  "role" "feed")

    (.setAttribute track      "part" "track")
    (.setAttribute track-line "part" "track-line")
    (.setAttribute track-fill "part" "track-fill")

    (.setAttribute track-svg  "part" "track-svg")
    (.setAttribute track-svg  "aria-hidden" "true")
    (.setAttribute track-svg  "focusable" "false")
    (.setAttribute track-path "part" "track-path")
    (.setAttribute fill-path  "part" "track-fill-path")
    (.setAttribute fill-path  "pathLength" "1")

    (.appendChild track-svg track-path)
    (.appendChild track-svg fill-path)

    (.appendChild track track-line)
    (.appendChild track track-fill)

    (.setAttribute entries "part" "entries")
    (.appendChild entries slot)

    (.setAttribute indicator "part" "indicator")

    (.appendChild container track)
    (.appendChild container entries)
    ;; SVG sits directly in the container (not inside narrow track div)
    (.appendChild container track-svg)
    (.appendChild container indicator)

    (.setAttribute live "part" "live")
    (.setAttribute live "aria-live" "polite")
    (.setAttribute live "aria-atomic" "true")

    (.appendChild root style)
    (.appendChild root container)
    (.appendChild root live)

    (gobj/set el k-refs
              {:root       root
               :container  container
               :track      track
               :track-line track-line
               :track-fill track-fill
               :track-svg  track-svg
               :track-path track-path
               :fill-path  fill-path
               :entries    entries
               :slot       slot
               :indicator  indicator
               :live       live}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:layout-raw              (.getAttribute el model/attr-layout)
    :track-raw               (.getAttribute el model/attr-track)
    :threshold-raw           (.getAttribute el model/attr-threshold)
    :no-progress-attr        (when (.hasAttribute el model/attr-no-progress) "")
    :disabled-attr           (when (.hasAttribute el model/attr-disabled) "")
    :label-raw               (.getAttribute el model/attr-label)
    :marker-raw              (.getAttribute el model/attr-marker)
    :autoplay-attr           (when (.hasAttribute el model/attr-autoplay) "")
    :autoplay-speed-raw      (.getAttribute el model/attr-autoplay-speed)
    :autoplay-loop-attr      (when (.hasAttribute el model/attr-autoplay-loop) "")
    :autoplay-indicator-attr (when (.hasAttribute el model/attr-autoplay-indicator) "")}))

;; ── Helpers ─────────────────────────────────────────────────────────────────
(defn- get-entry-children [^js el]
  (let [{:keys [slot]} (ensure-refs! el)]
    (.assignedElements ^js slot)))

(defn- prefers-reduced-motion? []
  (boolean (.-matches (.matchMedia js/window "(prefers-reduced-motion:reduce)"))))

(defn- entry-id
  "Get the id attribute of an entry element, or nil."
  [^js child]
  (let [id (.getAttribute child "id")]
    (when (and (string? id) (not= id ""))
      id)))

;; ── Event dispatch ──────────────────────────────────────────────────────────
;; ── Announce to live region ─────────────────────────────────────────────────
(defn- announce! [^js el msg]
  (let [{:keys [live]} (gobj/get el k-refs)]
    (set! (.-textContent ^js live) msg)
    (js/setTimeout (fn [] (set! (.-textContent ^js live) "")) 1000)))

;; ── Child attribute management ──────────────────────────────────────────────
(defn- assign-child-attrs!
  "Set data-side and data-index on each child based on layout."
  [^js _el children layout]
  (dotimes [i (.-length children)]
    (let [^js child (aget children i)
          side (model/entry-side layout i)]
      (.setAttribute child model/data-side side)
      (.setAttribute child model/data-index (str i)))))

(defn- clean-entry-attrs!
  "Remove component-managed attributes from children."
  [^js el]
  (let [children (get-entry-children el)]
    (dotimes [i (.-length children)]
      (let [^js child (aget children i)]
        (.removeAttribute child model/data-active)
        (.removeAttribute child model/data-side)
        (.removeAttribute child model/data-index)))))

;; ── Marker management ───────────────────────────────────────────────────────
(defn- rebuild-markers!
  "Create/remove marker elements in shadow DOM to match child count."
  [^js el children]
  (let [{:keys [container]} (gobj/get el k-refs)
        ^js container container
        old-markers (or (gobj/get el k-marker-els) #js [])
        n (.-length children)]
    ;; Remove old markers
    (dotimes [i (.-length old-markers)]
      (let [^js m (aget old-markers i)]
        (.remove m)))
    ;; Create new markers
    (let [markers (make-array n)]
      (dotimes [i n]
        (let [m (.createElement js/document "div")]
          (.add (.-classList m) "tl-marker")
          (.appendChild container m)
          (aset markers i m)))
      (gobj/set el k-marker-els markers))
    nil))

(defn- rebuild-date-labels!
  "Create/remove date label elements in shadow DOM."
  [^js el children]
  (let [{:keys [container]} (gobj/get el k-refs)
        ^js container container
        old-dates (or (gobj/get el k-date-els) #js [])
        n (.-length children)]
    ;; Remove old date labels (some entries may be nil)
    (dotimes [i (.-length old-dates)]
      (when-let [^js d (aget old-dates i)]
        (.remove d)))
    ;; Create date labels for children with data-date
    (let [dates (make-array n)]
      (dotimes [i n]
        (let [^js child (aget children i)
              date-text (.getAttribute child model/data-date)]
          (if (and (string? date-text) (not= date-text ""))
            (let [d (.createElement js/document "span")]
              (.add (.-classList d) "tl-date")
              (set! (.-textContent d) date-text)
              (.appendChild container d)
              (aset dates i d))
            (aset dates i nil))))
      (gobj/set el k-date-els dates))
    nil))

(defn- position-overlays!
  "Reposition markers and date labels to align with child centers."
  [^js el children]
  (let [markers  (gobj/get el k-marker-els)
        dates    (gobj/get el k-date-els)
        {:keys [container]} (gobj/get el k-refs)
        ^js container container
        cont-rect (.getBoundingClientRect container)
        cont-top  (.-top cont-rect)
        m         (or (gobj/get el k-model) (read-model el))
        layout    (:layout m)]
    (dotimes [i (.-length children)]
      (let [^js child (aget children i)
            rect      (.getBoundingClientRect child)
            center-y  (- (+ (.-top rect) (/ (.-height rect) 2.0)) cont-top)
            side      (model/entry-side layout i)]
        ;; Position marker
        (when-let [^js marker (and markers (aget markers i))]
          (set! (.. marker -style -top) (str center-y "px")))
        ;; Position and side date label
        (when-let [^js date-el (and dates (aget dates i))]
          (set! (.. date-el -style -top) (str center-y "px"))
          (.setAttribute date-el model/data-side side)))))
  nil)

;; ── Track rendering ─────────────────────────────────────────────────────────
(defn- update-straight-track! [^js el progress no-progress?]
  (let [{:keys [track-fill]} (gobj/get el k-refs)
        ^js track-fill track-fill]
    (if no-progress?
      (set! (.. track-fill -style -height) "0%")
      (set! (.. track-fill -style -height) (str (* progress 100) "%")))))

(defn- rebuild-curved-track!
  "Rebuild the SVG serpentine path based on current child positions."
  [^js el children]
  (let [{:keys [container track-svg track-path fill-path]} (gobj/get el k-refs)
        ^js container container
        ^js track-svg track-svg
        m         (or (gobj/get el k-model) (read-model el))
        layout    (:layout m)
        cont-rect (.getBoundingClientRect container)
        cont-top  (.-top cont-rect)
        cont-h    (.-height cont-rect)
        cont-w    (.-width cont-rect)
        ;; Read amplitude from CSS custom property
        amp-str   (.getPropertyValue (.getComputedStyle js/window el)
                                     "--x-scroll-timeline-curve-amplitude")
        amplitude (let [n (js/parseFloat amp-str)]
                    (if (js/isNaN n) 60 n))
        ;; Read marker size for track center offset
        marker-str (.getPropertyValue (.getComputedStyle js/window el)
                                      "--x-scroll-timeline-marker-size")
        marker-r   (let [n (js/parseFloat marker-str)]
                     (if (js/isNaN n) 7 (/ n 2.0)))
        center-x   (case layout
                     "left"  marker-r
                     "right" (- cont-w marker-r)
                     (* cont-w 0.5))
        n         (.-length children)]
    ;; Build points
    (when (pos? n)
      (let [points (loop [i 0 acc []]
                     (if (>= i n)
                       acc
                       (let [^js child (aget children i)
                             rect (.getBoundingClientRect child)
                             cy   (- (+ (.-top rect) (/ (.-height rect) 2.0)) cont-top)
                             side (model/entry-side layout i)]
                         (recur (inc i) (conj acc {:y cy :side side})))))
            d (model/build-serpentine-path points amplitude center-x)]
        ;; Set SVG viewBox to match container
        (.setAttribute track-svg "viewBox" (str "0 0 " cont-w " " cont-h))
        (.setAttribute ^js track-path "d" d)
        (.setAttribute ^js fill-path "d" d)
        ;; Initialize dasharray for progress
        (.setAttribute ^js fill-path "stroke-dasharray" "1")
        (.setAttribute ^js fill-path "stroke-dashoffset" "1"))))
  nil)

(defn- update-curved-track! [^js el progress no-progress?]
  (let [{:keys [fill-path]} (gobj/get el k-refs)
        ^js fill-path fill-path]
    (if no-progress?
      (.setAttribute fill-path "stroke-dashoffset" "1")
      (.setAttribute fill-path "stroke-dashoffset" (str (- 1.0 progress))))))

;; ── Entry activation ────────────────────────────────────────────────────────
(defn- update-active-entry!
  "Mark an entry child as active (set data-active), remove from previous."
  [^js el children new-index]
  (let [old-index (or (gobj/get el k-active-index) -1)]
    (when (not= old-index new-index)
      ;; Remove data-active from old entry
      (when (and (>= old-index 0) (< old-index (.-length children)))
        (let [^js old-child (aget children old-index)]
          (.removeAttribute old-child model/data-active)))
      ;; Set data-active on new entry
      (when (and (>= new-index 0) (< new-index (.-length children)))
        (let [^js new-child (aget children new-index)]
          (.setAttribute new-child model/data-active "")))
      ;; Update marker active states
      (let [markers (gobj/get el k-marker-els)
            dates   (gobj/get el k-date-els)]
        (when markers
          (when (and (>= old-index 0) (< old-index (.-length markers)))
            (.removeAttribute ^js (aget markers old-index) "data-active"))
          (when (and (>= new-index 0) (< new-index (.-length markers)))
            (.setAttribute ^js (aget markers new-index) "data-active" "")))
        (when dates
          (when (and (>= old-index 0) (< old-index (.-length dates)))
            (when-let [^js d (aget dates old-index)]
              (.removeAttribute d "data-active")))
          (when (and (>= new-index 0) (< new-index (.-length dates)))
            (when-let [^js d (aget dates new-index)]
              (.setAttribute d "data-active" "")))))
      ;; Update host data attribute
      (if (>= new-index 0)
        (.setAttribute el "data-active-index" (str new-index))
        (.removeAttribute el "data-active-index"))
      ;; Cache active index
      (gobj/set el k-active-index new-index)
      ;; Dispatch entry-change event
      (let [old-id (when (and (>= old-index 0) (< old-index (.-length children)))
                     (entry-id (aget children old-index)))
            new-id (when (and (>= new-index 0) (< new-index (.-length children)))
                     (entry-id (aget children new-index)))]
        (du/dispatch! el model/event-entry-change
                   #js {:index new-index :id new-id
                        :previousIndex old-index :previousId old-id})
        ;; Announce to live region
        (announce! el (str "Entry " (inc new-index) " active"))))))

;; ── Entry enter/leave tracking ──────────────────────────────────────────────
(defn- ensure-entry-states!
  "Ensure the entry states array matches the current child count."
  [^js el n]
  (let [states (gobj/get el k-entry-states)]
    (if (and states (= (.-length states) n))
      states
      (let [new-states (make-array n)]
        (dotimes [i n]
          (aset new-states i
                (if (and states (< i (.-length states)))
                  (aget states i)
                  false)))
        (gobj/set el k-entry-states new-states)
        new-states))))

(defn- update-entry-states!
  "Track which entries are in the trigger zone and fire enter/leave events."
  [^js el children entry-rects trigger-y]
  (let [n      (.-length children)
        states (ensure-entry-states! el n)]
    (dotimes [i n]
      (when (< i (count entry-rects))
        (let [{:keys [top bottom]} (nth entry-rects i)
              was-in? (aget states i)
              is-in?  (and (<= top trigger-y) (> bottom trigger-y))
              ^js child (aget children i)
              id (entry-id child)
              progress (model/compute-entry-progress top (- bottom top) trigger-y)]
          (cond
            (and (not was-in?) is-in?)
            (do (aset states i true)
                (du/dispatch! el model/event-entry-enter
                           #js {:index i :id id :progress progress}))
            (and was-in? (not is-in?))
            (do (aset states i false)
                (du/dispatch! el model/event-entry-leave
                           #js {:index i :id id :progress progress}))))))))

;; ── Scroll update ───────────────────────────────────────────────────────────
(defn- update-scroll!
  "Compute entry activation and dispatch events. Called from rAF callback."
  [^js el]
  (let [m (or (gobj/get el k-model) (read-model el))]
    (when-not (:disabled? m)
      (let [children    (get-entry-children el)
            n           (.-length children)
            vp-height   (.-innerHeight js/window)
            trigger-y   (* vp-height (:threshold m))
            ;; Build entry rects
            entry-rects (loop [i 0 acc []]
                          (if (>= i n)
                            acc
                            (let [^js child (aget children i)
                                  rect (.getBoundingClientRect child)]
                              (recur (inc i)
                                     (conj acc {:top    (.-top rect)
                                                :bottom (+ (.-top rect) (.-height rect))})))))
            ;; Find active entry
            active-idx  (model/find-active-entry entry-rects trigger-y)
            ;; Compute overall progress
            {:keys [container]} (gobj/get el k-refs)
            ^js container container
            cont-rect   (.getBoundingClientRect container)
            progress    (model/compute-overall-progress
                         (.-top cont-rect) (.-height cont-rect) vp-height)
            last-prog   (gobj/get el k-last-prog)
            ;; Track fill progress based on entry positions (not container scroll)
            ;; Fills from first entry center to last entry center
            track-prog  (if (or (< n 2) (< active-idx 0))
                          0.0
                          (let [first-center (let [r (nth entry-rects 0)]
                                               (/ (+ (:top r) (:bottom r)) 2.0))
                                last-center  (let [r (nth entry-rects (dec n))]
                                               (/ (+ (:top r) (:bottom r)) 2.0))
                                rng          (- last-center first-center)
                                active-rect  (nth entry-rects active-idx)
                                active-center (/ (+ (:top active-rect) (:bottom active-rect)) 2.0)
                                entry-prog   (model/compute-entry-progress
                                              (:top active-rect)
                                              (- (:bottom active-rect) (:top active-rect))
                                              trigger-y)
                                next-center  (if (< active-idx (dec n))
                                               (let [r (nth entry-rects (inc active-idx))]
                                                 (/ (+ (:top r) (:bottom r)) 2.0))
                                               active-center)
                                pos          (+ active-center (* entry-prog (- next-center active-center)))]
                            (if (<= rng 0)
                              1.0
                              (max 0.0 (min 1.0 (/ (- pos first-center) rng))))))]

        ;; Update active entry (handles data-active + events)
        (update-active-entry! el children active-idx)

        ;; Track entry enter/leave
        (update-entry-states! el children entry-rects trigger-y)

        ;; Update track fill using entry-based progress
        (if (= (:track m) "curved")
          (update-curved-track! el track-prog (:no-progress? m))
          (update-straight-track! el track-prog (:no-progress? m)))

        ;; Dispatch progress event if changed
        (when (or (nil? last-prog)
                  (> (js/Math.abs (- progress last-prog)) 0.001))
          (gobj/set el k-last-prog progress)
          (let [active-id (when (and (>= active-idx 0) (< active-idx n))
                            (entry-id (aget children active-idx)))]
            (du/dispatch! el model/event-progress
                       #js {:progress progress :activeIndex active-idx :activeId active-id}))))))
  ;; Clear rAF handle
  (gobj/set el k-raf nil))

;; ── Autoplay ────────────────────────────────────────────────────────────────

(defn- autoplay-running? [^js el]
  (some? (gobj/get el k-autoplay-raf)))

(defn- autoplay-tick [^js el]
  (fn tick [^js ts]
    (when (and (.-isConnected el)
               (gobj/get el k-visible)
               (not (gobj/get el k-autoplay-paused)))
      (let [m (gobj/get el k-model)]
        (when (and m (:autoplay? m) (not (:disabled? m)))
          (let [last-ts (gobj/get el k-autoplay-last)
                dt      (if last-ts (/ (- ts last-ts) 1000.0) 0)]
            (gobj/set el k-autoplay-last ts)
            (when (> dt 0)
              (.scrollBy js/window 0 (* (:autoplay-speed m) dt))
              ;; Loop: detect page bottom and scroll back to element start.
              (when (:autoplay-loop? m)
                (let [scroll-y    (.-scrollY js/window)
                      vh          (.-innerHeight js/window)
                      page-height (.-scrollHeight (.-documentElement js/document))]
                  (when (>= (+ scroll-y vh) (- page-height 1))
                    (let [rect    (.getBoundingClientRect el)
                          abs-top (+ (.-top rect) scroll-y)]
                      (.scrollTo js/window 0 (max 0 (- abs-top vh)))
                      (gobj/set el k-autoplay-last nil))))))
            (gobj/set el k-autoplay-raf
                      (js/requestAnimationFrame tick))))))))

(defn- pause-autoplay! [^js el]
  (when-not (gobj/get el k-autoplay-paused)
    (gobj/set el k-autoplay-paused true)
    ;; Cancel pending autoplay rAF
    (when-let [raf (gobj/get el k-autoplay-raf)]
      (js/cancelAnimationFrame raf)
      (gobj/set el k-autoplay-raf nil))
    ;; Set data attribute for CSS indicator
    (.setAttribute el "data-autoplay-paused" "")
    ;; Dispatch pause event
    (let [prog     (or (gobj/get el k-last-prog) 0)
          idx      (or (gobj/get el k-active-index) -1)
          children (get-entry-children el)
          aid      (when (and (>= idx 0) (< idx (.-length children)))
                     (entry-id (aget children idx)))]
      (du/dispatch! el model/event-autoplay-pause
                 #js {:progress prog :activeIndex idx :activeId aid}))))

(defn- resume-autoplay! [^js el]
  (when (gobj/get el k-autoplay-paused)
    (gobj/set el k-autoplay-paused false)
    (.removeAttribute el "data-autoplay-paused")
    ;; Reset timestamp to avoid delta spike
    (gobj/set el k-autoplay-last nil)
    ;; Restart rAF loop
    (let [tick (autoplay-tick el)]
      (gobj/set el k-autoplay-raf
                (js/requestAnimationFrame tick)))
    ;; Dispatch resume event
    (let [prog     (or (gobj/get el k-last-prog) 0)
          idx      (or (gobj/get el k-active-index) -1)
          children (get-entry-children el)
          aid      (when (and (>= idx 0) (< idx (.-length children)))
                     (entry-id (aget children idx)))]
      (du/dispatch! el model/event-autoplay-resume
                 #js {:progress prog :activeIndex idx :activeId aid}))))

(defn- stop-autoplay! [^js el]
  ;; Cancel rAF
  (when-let [raf (gobj/get el k-autoplay-raf)]
    (js/cancelAnimationFrame raf)
    (gobj/set el k-autoplay-raf nil))
  ;; Remove pause/resume listeners
  (when-let [hs (gobj/get el k-autoplay-handlers)]
    (.removeEventListener el "pointerdown"  (gobj/get hs "pointerdown"))
    (.removeEventListener el "pointerup"    (gobj/get hs "pointerup"))
    (.removeEventListener el "pointerleave" (gobj/get hs "pointerleave"))
    (.removeEventListener el "keydown"      (gobj/get hs "keydown"))
    (.removeEventListener el "keyup"        (gobj/get hs "keyup"))
    (gobj/set el k-autoplay-handlers nil))
  ;; Clean up state
  (.removeAttribute el "data-autoplay-paused")
  (.removeAttribute el "tabindex")
  (gobj/set el k-autoplay-paused false)
  (gobj/set el k-autoplay-last nil)
  nil)

(defn- start-autoplay! [^js el]
  (when-not (autoplay-running? el)
    (let [m (gobj/get el k-model)]
      (when (and m (:autoplay? m) (not (:disabled? m))
                 (gobj/get el k-visible)
                 (not (prefers-reduced-motion?)))
        ;; Make focusable for Space key
        (.setAttribute el "tabindex" "0")
        ;; Init state
        (gobj/set el k-autoplay-paused false)
        (gobj/set el k-autoplay-last nil)
        ;; Attach pause/resume listeners (pointer events unify mouse + touch)
        (let [on-pointerdown  (fn [_e] (pause-autoplay! el))
              on-pointerup    (fn [_e] (resume-autoplay! el))
              on-pointerleave (fn [_e] (when (gobj/get el k-autoplay-paused)
                                         (resume-autoplay! el)))
              on-keydown      (fn [^js e]
                                (when (= (.-code e) "Space")
                                  (.preventDefault e)
                                  (pause-autoplay! el)))
              on-keyup        (fn [^js e]
                                (when (= (.-code e) "Space")
                                  (resume-autoplay! el)))]
          (.addEventListener el "pointerdown"  on-pointerdown)
          (.addEventListener el "pointerup"    on-pointerup)
          (.addEventListener el "pointerleave" on-pointerleave)
          (.addEventListener el "keydown"      on-keydown)
          (.addEventListener el "keyup"        on-keyup)
          (gobj/set el k-autoplay-handlers
                    #js {:pointerdown  on-pointerdown
                         :pointerup    on-pointerup
                         :pointerleave on-pointerleave
                         :keydown      on-keydown
                         :keyup        on-keyup}))
        ;; Start rAF loop
        (let [tick (autoplay-tick el)]
          (gobj/set el k-autoplay-raf
                    (js/requestAnimationFrame tick))))))
  nil)

;; ── Scroll handler ──────────────────────────────────────────────────────────
(defn- disabled? [^js el]
  (let [m (gobj/get el k-model)]
    (and m (:disabled? m))))

(defn- on-scroll [^js el]
  (when (and (.-isConnected el)
             (gobj/get el k-visible)
             (not (disabled? el))
             (not (prefers-reduced-motion?)))
    (when-not (gobj/get el k-raf)
      (gobj/set el k-raf
                (js/requestAnimationFrame
                 (fn [_] (update-scroll! el)))))))

;; ── IntersectionObserver callback ───────────────────────────────────────────
(defn- attach-scroll-listener! [^js el]
  (let [hs (gobj/get el k-handlers)]
    (when (and hs (not (gobj/get hs "scrollAttached")))
      (let [scroll-h (gobj/get hs "scroll")]
        (.addEventListener js/window "scroll" scroll-h #js {:passive true})
        (gobj/set hs "scrollAttached" true)))))

(defn- detach-scroll-listener! [^js el]
  (let [hs (gobj/get el k-handlers)]
    (when (and hs (gobj/get hs "scrollAttached"))
      (let [scroll-h (gobj/get hs "scroll")]
        (.removeEventListener js/window "scroll" scroll-h)
        (gobj/set hs "scrollAttached" false)))))

(defn- on-intersection [^js el ^js entries]
  (let [^js entry (aget entries 0)
        is-intersecting (.-isIntersecting entry)]
    (gobj/set el k-visible is-intersecting)
    (if is-intersecting
      (when-not (disabled? el)
        (attach-scroll-listener! el)
        (update-scroll! el)
        (announce! el "Timeline entered viewport")
        (du/dispatch! el model/event-enter
                   #js {:progress (or (gobj/get el k-last-prog) 0)})
        ;; Start autoplay if enabled
        (let [m (gobj/get el k-model)]
          (when (and m (:autoplay? m))
            (start-autoplay! el))))
      (do
        (detach-scroll-listener! el)
        (stop-autoplay! el)
        (when-not (disabled? el)
          (announce! el "Timeline left viewport")
          (du/dispatch! el model/event-leave
                     #js {:progress (or (gobj/get el k-last-prog) 0)}))))))

;; ── Slot change / resize ────────────────────────────────────────────────────
(defn- rebuild-layout!
  "Full layout rebuild: reassign sides, rebuild markers/dates, reposition."
  [^js el]
  (let [m        (or (gobj/get el k-model) (read-model el))
        children (get-entry-children el)]
    ;; Assign child attributes
    (assign-child-attrs! el children (:layout m))
    ;; Rebuild markers and date labels
    (rebuild-markers! el children)
    (rebuild-date-labels! el children)
    ;; Position overlays after a microtask (let layout settle)
    (js/requestAnimationFrame
     (fn [_]
       (let [children (get-entry-children el)]
         (position-overlays! el children)
         ;; Rebuild curved track if needed
         (when (= (:track m) "curved")
           (rebuild-curved-track! el children))
         ;; Update scroll state if visible
         (when (gobj/get el k-visible)
           (update-scroll! el)))))))

(defn- on-slotchange [^js el]
  ;; Reset entry states cache
  (gobj/set el k-entry-states nil)
  (rebuild-layout! el))

;; ── Listener management ────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [{:keys [slot]} (ensure-refs! el)
        ^js slot    slot
        scroll-h    (fn [_e] (on-scroll el))
        slot-h      (fn [_e] (on-slotchange el))
        resize-h    (fn [_e] (when (gobj/get el k-visible)
                               (gobj/set el k-entry-states nil)
                               (rebuild-layout! el)))]
    (.addEventListener slot "slotchange" slot-h)
    (.addEventListener js/window "resize" resize-h)
    (gobj/set el k-handlers
              #js {:scroll         scroll-h
                   :slot           slot-h
                   :resize         resize-h
                   :scrollAttached false}))
  nil)

(defn- remove-listeners! [^js el]
  (let [hs (gobj/get el k-handlers)]
    (when hs
      (detach-scroll-listener! el)
      (let [refs (gobj/get el k-refs)]
        (when refs
          (let [^js slot (:slot refs)]
            (when-let [h (gobj/get hs "slot")]
              (.removeEventListener slot "slotchange" h)))))
      (when-let [h (gobj/get hs "resize")]
        (.removeEventListener js/window "resize" h))))
  ;; Cancel pending rAF
  (when-let [raf (gobj/get el k-raf)]
    (js/cancelAnimationFrame raf)
    (gobj/set el k-raf nil))
  (gobj/set el k-handlers nil)
  nil)

;; ── IntersectionObserver setup/teardown ─────────────────────────────────────
(defn- setup-observer! [^js el]
  (when-let [old (gobj/get el k-io)]
    (.disconnect ^js old))
  (let [obs (js/IntersectionObserver.
             (fn [entries] (on-intersection el entries))
             #js {:threshold #js [0]})]
    (.observe obs el)
    (gobj/set el k-io obs))
  nil)

(defn- teardown-observer! [^js el]
  (when-let [obs (gobj/get el k-io)]
    (.disconnect ^js obs)
    (gobj/set el k-io nil))
  nil)

;; ── DOM patching ────────────────────────────────────────────────────────────
(defn- apply-model! [^js el {:keys [layout track disabled? label marker] :as m}]
  (let [{:keys [container]} (ensure-refs! el)
        ^js container container
        old-m (gobj/get el k-model)]
    ;; Data attributes for CSS selectors
    (.setAttribute el "data-layout" layout)
    (.setAttribute el "data-track" track)
    (.setAttribute el "data-marker" marker)

    ;; Aria
    (if (seq label)
      (.setAttribute container "aria-label" label)
      (.removeAttribute container "aria-label"))

    ;; Cache model
    (gobj/set el k-model m)

    (if disabled?
      ;; When disabling: clean active entry, detach scroll listener, stop autoplay
      (do
        (clean-entry-attrs! el)
        (gobj/set el k-active-index -1)
        (gobj/set el k-entry-states nil)
        (detach-scroll-listener! el)
        (stop-autoplay! el))
      (when old-m
        (cond
          ;; Layout/track/marker changed: full rebuild
          (or (not= (:layout old-m) layout)
              (not= (:track old-m) track)
              (not= (:marker old-m) marker)
              (not= (:disabled? old-m) disabled?))
          (rebuild-layout! el)

          ;; Threshold or no-progress changed: re-run scroll update
          (or (not= (:threshold old-m) (:threshold m))
              (not= (:no-progress? old-m) (:no-progress? m)))
          (when (gobj/get el k-visible)
            (update-scroll! el)))))

    ;; Autoplay toggle
    (when-not disabled?
      (if (and (:autoplay? m) (gobj/get el k-visible))
        (start-autoplay! el)
        (stop-autoplay! el))))
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m)))
  nil)

;; ── Property accessor helpers ───────────────────────────────────────────────

(defn- def-string-prop-default! [^js proto prop-name attr default]
  (.defineProperty js/Object proto prop-name
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this attr) default)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this attr (str v))
                                          (.removeAttribute this attr))))
                        :enumerable true :configurable true}))

(defn- def-string-prop-empty! [^js proto prop-name attr]
  (.defineProperty js/Object proto prop-name
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this attr) "")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (and v (not= v ""))
                                          (.setAttribute this attr (str v))
                                          (.removeAttribute this attr))))
                        :enumerable true :configurable true}))

(defn- def-bool-prop! [^js proto prop-name attr]
  (.defineProperty js/Object proto prop-name
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this attr)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this attr "")
                                          (.removeAttribute this attr))))
                        :enumerable true :configurable true}))

(defn- def-number-prop! [^js proto prop-name attr parse-fn]
  (.defineProperty js/Object proto prop-name
                   #js {:get (fn []
                               (this-as ^js this
                                        (parse-fn (.getAttribute this attr))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (some? v)
                                          (.setAttribute this attr (str v))
                                          (.removeAttribute this attr))))
                        :enumerable true :configurable true}))

(defn- def-readonly-prop! [^js proto prop-name field-key default]
  (.defineProperty js/Object proto prop-name
                   #js {:get (fn []
                               (this-as ^js this
                                        (let [v (gobj/get this field-key)]
                                          (if (some? v) v default))))
                        :enumerable true :configurable true}))

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (def-string-prop-default! proto model/attr-layout  model/attr-layout  "alternating")
  (def-string-prop-default! proto model/attr-track   model/attr-track   "straight")
  (def-string-prop-default! proto model/attr-marker  model/attr-marker  "dot")
  (def-string-prop-empty!   proto model/attr-label   model/attr-label)
  (def-number-prop!         proto model/attr-threshold model/attr-threshold model/parse-threshold)
  (def-bool-prop!           proto model/prop-no-progress        model/attr-no-progress)
  (def-bool-prop!           proto model/attr-disabled           model/attr-disabled)
  (def-bool-prop!           proto model/attr-autoplay           model/attr-autoplay)
  (def-number-prop!         proto model/prop-autoplay-speed     model/attr-autoplay-speed model/parse-autoplay-speed)
  (def-bool-prop!           proto model/prop-autoplay-loop      model/attr-autoplay-loop)
  (def-bool-prop!           proto model/prop-autoplay-indicator model/attr-autoplay-indicator)
  (def-readonly-prop!       proto model/prop-active-index       k-active-index -1)
  (def-readonly-prop!       proto model/prop-progress           k-last-prog 0)
  (def-readonly-prop!       proto model/prop-autoplay-paused    k-autoplay-paused false))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el)
  (setup-observer! el)
  (update-from-attrs! el)
  nil)

(defn- disconnected! [^js el]
  ;; Stop autoplay first
  (stop-autoplay! el)
  ;; Clean data attributes from children
  (clean-entry-attrs! el)
  ;; Dispatch leave event if visible
  (when (gobj/get el k-visible)
  (du/dispatch! el model/event-leave
  #js {:progress (or (gobj/get el k-last-prog) 0)}))
  (remove-listeners! el)
  (teardown-observer! el)
  (gobj/set el k-visible false)
  (gobj/set el k-active-index -1)
  (gobj/set el k-last-prog nil)
  (gobj/set el k-entry-states nil)
  nil)

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
  (update-from-attrs! el))
  nil)

;; ── Public API ──────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
