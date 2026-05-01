(ns baredom.components.x-scroll.x-scroll
  (:require
[baredom.utils.dom :as du]
               [goog.object :as gobj]
   [baredom.components.x-scroll.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs         "__xScrollRefs")
(def ^:private k-model        "__xScrollModel")
(def ^:private k-handlers     "__xScrollHandlers")
(def ^:private k-autoplay-tid "__xScrollAutoplayTid")
(def ^:private k-drag-state   "__xScrollDragState")
(def ^:private k-child-count  "__xScrollChildCount")
(def ^:private k-resize-obs   "__xScrollResizeObs")

;; ── Drag-state property keys (private JS object) ───────────────────────────
(def ^:private dk-start-x     "startX")
(def ^:private dk-start-y     "startY")
(def ^:private dk-start-time  "startTime")
(def ^:private dk-last-x      "lastX")
(def ^:private dk-last-y      "lastY")
(def ^:private dk-last-time   "lastTime")
(def ^:private dk-is-dragging "isDragging")
(def ^:private dk-horiz       "horiz")
(def ^:private dk-move-h      "moveH")
(def ^:private dk-up-h        "upH")

;; ── Styles ──────────────────────────────────────────────────────────────────
(def style-text
  (str
   ":host{"
   "display:block;position:relative;overflow:hidden;"
   "color-scheme:light dark;"
   "--x-scroll-gap:0px;"
   "--x-scroll-padding:0px;"
   "--x-scroll-border-radius:0px;"
   "--x-scroll-bg:transparent;"
   "--x-scroll-control-size:36px;"
   "--x-scroll-control-bg:var(--x-color-bg,rgba(255,255,255,0.9));"
   "--x-scroll-control-color:var(--x-color-text,rgba(0,0,0,0.7));"
   "--x-scroll-control-hover-bg:var(--x-color-bg,rgba(255,255,255,1));"
   "--x-scroll-control-border-radius:50%;"
   "--x-scroll-control-shadow:var(--x-shadow-md,0 2px 8px rgba(0,0,0,.15));"
   "--x-scroll-indicator-size:8px;"
   "--x-scroll-indicator-gap:6px;"
   "--x-scroll-indicator-color:var(--x-color-text-muted,rgba(0,0,0,0.25));"
   "--x-scroll-indicator-active-color:var(--x-color-text,rgba(0,0,0,0.7));"
   "--x-scroll-transition-duration:var(--x-transition-duration,300ms);"
   "--x-scroll-slide-size:100%;"
   "--x-scroll-disabled-opacity:0.55;"
   "--x-scroll-focus-ring:var(--x-color-focus-ring,rgba(0,102,204,0.6));}"

   "@media (prefers-color-scheme:dark){:host{"
   "--x-scroll-control-bg:var(--x-color-bg,rgba(40,40,40,0.9));"
   "--x-scroll-control-color:var(--x-color-text,rgba(255,255,255,0.8));"
   "--x-scroll-control-hover-bg:var(--x-color-bg,rgba(50,50,50,1));"
   "--x-scroll-control-shadow:var(--x-shadow-md,0 2px 8px rgba(0,0,0,.4));"
   "--x-scroll-indicator-color:var(--x-color-text-muted,rgba(255,255,255,0.3));"
   "--x-scroll-indicator-active-color:var(--x-color-text,rgba(255,255,255,0.8));"
   "--x-scroll-focus-ring:var(--x-color-focus-ring,rgba(100,180,255,0.7));}}"

   "[part=viewport]{"
   "position:relative;overflow:hidden;width:100%;height:100%;"
   "border-radius:var(--x-scroll-border-radius);"
   "background:var(--x-scroll-bg);"
   "padding:var(--x-scroll-padding);}"

   "[part=track]{"
   "display:flex;position:relative;"
   "will-change:transform;"
   "transition:transform var(--x-scroll-transition-duration) cubic-bezier(0.25,0.1,0.25,1);"
   "touch-action:none;"
   "user-select:none;}"

   ":host([data-mode=horizontal]) [part=track]{flex-direction:row;}"
   ":host([data-mode=vertical]) [part=track]{flex-direction:column;}"

   "::slotted(*){flex:0 0 var(--x-scroll-slide-size);min-width:0;min-height:0;box-sizing:border-box;}"

   "[part=prev],[part=next]{"
   "position:absolute;top:50%;z-index:2;"
   "appearance:none;border:0;margin:0;padding:0;"
   "width:var(--x-scroll-control-size);height:var(--x-scroll-control-size);"
   "border-radius:var(--x-scroll-control-border-radius);"
   "background:var(--x-scroll-control-bg);"
   "color:var(--x-scroll-control-color);"
   "box-shadow:var(--x-scroll-control-shadow);"
   "cursor:pointer;display:flex;align-items:center;justify-content:center;"
   "font-size:calc(var(--x-scroll-control-size) * 0.5);"
   "line-height:1;"
   "transform:translateY(-50%);"
   "transition:background 120ms ease,opacity 120ms ease;}"

   "[part=prev]{left:8px;}"
   "[part=next]{right:8px;}"

   ":host([data-mode=vertical]) [part=prev],"
   ":host([data-mode=vertical]) [part=next]{"
   "top:auto;left:50%;transform:translateX(-50%);}"
   ":host([data-mode=vertical]) [part=prev]{top:8px;}"
   ":host([data-mode=vertical]) [part=next]{bottom:8px;top:auto;}"

   "[part=prev]:hover,[part=next]:hover{background:var(--x-scroll-control-hover-bg);}"
   "[part=prev]:focus,[part=next]:focus{outline:none;}"
   "[part=prev]:focus-visible,[part=next]:focus-visible{"
   "outline:2px solid var(--x-scroll-focus-ring);outline-offset:2px;}"
   "[part=prev]:disabled,[part=next]:disabled{opacity:0.3;cursor:default;pointer-events:none;}"

   "[data-hide-controls] [part=prev],"
   "[data-hide-controls] [part=next]{display:none;}"

   "[part=indicators]{"
   "display:flex;justify-content:center;gap:var(--x-scroll-indicator-gap);"
   "position:absolute;bottom:12px;left:50%;transform:translateX(-50%);z-index:2;}"

   ":host([data-mode=vertical]) [part=indicators]{"
   "flex-direction:column;bottom:auto;left:auto;"
   "right:12px;top:50%;transform:translateY(-50%);}"

   "[part=indicator]{"
   "appearance:none;border:0;margin:0;padding:0;"
   "width:var(--x-scroll-indicator-size);height:var(--x-scroll-indicator-size);"
   "border-radius:50%;background:var(--x-scroll-indicator-color);"
   "cursor:pointer;transition:background 120ms ease;}"
   "[part=indicator]:focus{outline:none;}"
   "[part=indicator]:focus-visible{"
   "outline:2px solid var(--x-scroll-focus-ring);outline-offset:2px;}"
   "[part=indicator][aria-selected=true]{background:var(--x-scroll-indicator-active-color);}"

   "[part=live]{"
   "position:absolute;width:1px;height:1px;margin:-1px;"
   "padding:0;overflow:hidden;clip:rect(0,0,0,0);border:0;}"

   ":host([disabled]){opacity:var(--x-scroll-disabled-opacity);pointer-events:none;}"

   "@media (prefers-reduced-motion:reduce){"
   "[part=track]{transition:none !important;}}"))

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root     (.attachShadow el #js {:mode "open"})
        style    (.createElement js/document "style")
        viewport (.createElement js/document "div")
        track    (.createElement js/document "div")
        slot     (.createElement js/document "slot")
        prev-btn (.createElement js/document "button")
        next-btn (.createElement js/document "button")
        indicators (.createElement js/document "div")
        live     (.createElement js/document "div")]

    (set! (.-textContent style) style-text)

    (.setAttribute viewport "part" "viewport")
    (.setAttribute viewport "role" "region")
    (.setAttribute viewport "aria-roledescription" "carousel")

    (.setAttribute track "part" "track")

    (.appendChild track slot)
    (.appendChild viewport track)

    (.setAttribute prev-btn "part" "prev")
    (.setAttribute prev-btn "type" "button")
    (.setAttribute prev-btn "aria-label" "Previous slide")
    (set! (.-textContent prev-btn) "\u2039")

    (.setAttribute next-btn "part" "next")
    (.setAttribute next-btn "type" "button")
    (.setAttribute next-btn "aria-label" "Next slide")
    (set! (.-textContent next-btn) "\u203A")

    (.setAttribute indicators "part" "indicators")
    (.setAttribute indicators "role" "tablist")
    (.setAttribute indicators "aria-label" "Slide indicators")

    (.setAttribute live "part" "live")
    (.setAttribute live "aria-live" "polite")
    (.setAttribute live "aria-atomic" "true")

    (.appendChild root style)
    (.appendChild root viewport)
    (.appendChild root prev-btn)
    (.appendChild root next-btn)
    (.appendChild root indicators)
    (.appendChild root live)

    (gobj/set el k-refs
              {:root       root
               :viewport   viewport
               :track      track
               :slot       slot
               :prev-btn   prev-btn
               :next-btn   next-btn
               :indicators indicators
               :live       live}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:mode-raw             (.getAttribute el model/attr-mode)
    :snap-raw             (.getAttribute el model/attr-snap)
    :loop-attr            (when (.hasAttribute el model/attr-loop) "")
    :auto-play-attr       (when (.hasAttribute el model/attr-auto-play) "")
    :interval-raw         (.getAttribute el model/attr-interval)
    :show-controls-attr   (.getAttribute el model/attr-show-controls)
    :show-indicators-attr (when (.hasAttribute el model/attr-show-indicators) "")
    :active-index-raw     (.getAttribute el model/attr-active-index)
    :gap-raw              (.getAttribute el model/attr-gap)
    :disabled-attr        (when (.hasAttribute el model/attr-disabled) "")
    :label-raw            (.getAttribute el model/attr-label)}))

;; ── Helpers ─────────────────────────────────────────────────────────────────
(defn- get-children [^js el]
  (let [{:keys [slot]} (ensure-refs! el)]
    (.assignedElements ^js slot)))

(defn- child-count [^js el]
  (.-length (get-children el)))

(defn- prefers-reduced-motion? []
  (boolean (.-matches (.matchMedia js/window "(prefers-reduced-motion:reduce)"))))

(defn- horizontal? [m]
  (= "horizontal" (:mode m)))

(defn- slide-size
  "Measure the rendered size of the first slotted child (actual slide width/height)."
  [^js el m]
  (let [children (get-children el)]
    (if (pos? (.-length children))
      (let [^js child (aget children 0)
            rect (.getBoundingClientRect child)]
        (if (horizontal? m)
          (.-width rect)
          (.-height rect)))
      0)))

;; ── Positioning ─────────────────────────────────────────────────────────────
(defn- set-track-transform! [^js track value]
  (set! (.. track -style -transform) value))

(defn- viewport-size
  "Return the viewport width (horizontal) or height (vertical)."
  [^js viewport m]
  (let [rect (.getBoundingClientRect viewport)]
    (if (= "horizontal" (:mode m))
      (.-width rect)
      (.-height rect))))

(defn- snap-offset
  "Compute the alignment offset for the active slide within the viewport.
  start/none: active at leading edge (offset 0).
  center: active centered.
  end: active at trailing edge."
  [snap vp-size slide-size]
  (case snap
    "center" (/ (- vp-size slide-size) 2)
    "end"    (- vp-size slide-size)
    0))

(defn- loop-rest-transform
  "Compute the resting transform for the track in loop mode."
  [_m cnt size gap snap-off horiz?]
  (let [half  (quot cnt 2)
        shift (- (* half (+ size gap)) snap-off)]
    (if horiz?
      (str "translateX(" (- shift) "px)")
      (str "translateY(" (- shift) "px)"))))

(defn- apply-loop-ordering!
  "Set CSS order on each child so `active` is centered in the track."
  [children cnt active]
  (dotimes [i cnt]
    (let [^js child (aget children i)
          offset    (model/effective-offset i active cnt)]
      (set! (.. child -style -order) (str offset)))))

(defn- position-slides!
  "Position all slides and the track based on the current model."
  [^js el m]
  (let [{:keys [viewport track]} (ensure-refs! el)
        ^js viewport viewport
        ^js track    track
        children     (get-children el)
        cnt          (.-length children)
        size         (slide-size el m)
        gap          (:gap m)
        active       (:active-index m)
        loop?        (model/effective-loop? (:loop? m) cnt)
        vp-sz        (viewport-size viewport m)
        snap-off     (snap-offset (:snap m) vp-sz size)]
    (gobj/set el k-child-count cnt)
    (when (pos? cnt)
      ;; Set gap on track
      (set! (.. track -style -gap) (str gap "px"))
      (if loop?
        ;; Loop mode: set CSS order on each child so active is centered
        (do
          (apply-loop-ordering! children cnt active)
          (set-track-transform! track
                                (loop-rest-transform m cnt size gap snap-off (horizontal? m))))
        ;; Non-loop: natural order, shift track
        (do
          ;; Reset order on all children
          (dotimes [i cnt]
            (set! (.. ^js (aget children i) -style -order) ""))
          (let [offset (- (* active (+ size gap)) snap-off)]
            (set-track-transform! track
                                  (if (horizontal? m)
                                    (str "translateX(" (- offset) "px)")
                                    (str "translateY(" (- offset) "px)")))))))))

(defn- animate-loop-step!
  "Animate one step in loop mode: slide the track by `direction` (+1/-1)
  using the OLD child ordering, then reorder + snap after transition."
  [^js el new-m direction]
  (let [{:keys [viewport track]} (ensure-refs! el)
        ^js viewport viewport
        ^js track    track
        children     (get-children el)
        cnt          (.-length children)
        size         (slide-size el new-m)
        gap          (:gap new-m)
        horiz?       (horizontal? new-m)
        vp-sz        (viewport-size viewport new-m)
        snap-off     (snap-offset (:snap new-m) vp-sz size)
        ;; Current rest position (old ordering still in place)
        old-half     (quot cnt 2)
        old-shift    (- (* old-half (+ size gap)) snap-off)
        ;; Slide one step in direction: direction is +1 (forward) or -1 (backward)
        step         (* direction (+ size gap))
        animated-pos (- (+ old-shift step))]
    ;; 1. Animate to the adjacent position (transition is active on track)
    (set-track-transform! track
                          (if horiz?
                            (str "translateX(" animated-pos "px)")
                            (str "translateY(" animated-pos "px)")))
    ;; 2. After transition ends, reorder and snap to rest position
    (if (prefers-reduced-motion?)
      ;; No transition — reorder immediately
      (do
        (apply-loop-ordering! children cnt (:active-index new-m))
        (set-track-transform! track
                              (loop-rest-transform new-m cnt size gap snap-off horiz?)))
      (letfn [(on-end [^js e]
                (when (= (.-target e) track)
                  (.removeEventListener track "transitionend" on-end)
                  ;; Disable transition, reorder, set rest transform, re-enable
                  (set! (.. track -style -transition) "none")
                  (apply-loop-ordering! children cnt (:active-index new-m))
                  (set-track-transform! track
                                        (loop-rest-transform new-m cnt size gap snap-off horiz?))
                  ;; Force layout before re-enabling transition
                  (.-offsetHeight track)
                  (set! (.. track -style -transition) "")))]
        (.addEventListener track "transitionend" on-end)))))

;; ── Indicators ──────────────────────────────────────────────────────────────
(defn- build-indicators! [^js el cnt active on-click-fn]
  (let [{:keys [indicators]} (ensure-refs! el)
        ^js indicators indicators]
    ;; Clear existing
    (set! (.-innerHTML indicators) "")
    (dotimes [i cnt]
      (let [btn (.createElement js/document "button")]
        (.setAttribute btn "part" "indicator")
        (.setAttribute btn "type" "button")
        (.setAttribute btn "role" "tab")
        (.setAttribute btn "aria-label" (str "Slide " (inc i)))
        (.setAttribute btn "aria-selected" (str (= i active)))
        (.addEventListener btn "click" (fn [_e] (on-click-fn i)))
        (.appendChild indicators btn)))))

(defn- update-indicators! [^js el active]
  (let [{:keys [indicators]} (ensure-refs! el)
        ^js indicators indicators
        dots (.-children indicators)]
    (dotimes [i (.-length dots)]
      (.setAttribute ^js (aget dots i) "aria-selected" (str (= i active))))))

;; ── Event dispatch ──────────────────────────────────────────────────────────
;; ── Autoplay ────────────────────────────────────────────────────────────────
(declare go-to!)

(defn- stop-autoplay! [^js el]
  (when-let [tid (gobj/get el k-autoplay-tid)]
    (js/clearInterval tid)
    (gobj/set el k-autoplay-tid nil))
  nil)

(defn- start-autoplay! [^js el]
  (stop-autoplay! el)
  (let [m (or (gobj/get el k-model) (read-model el))]
    (when (and (:auto-play? m) (not (:disabled? m)))
      (gobj/set el k-autoplay-tid
                (js/setInterval
                 (fn []
                   (when (.-isConnected el)
                     (let [cnt (child-count el)]
                       (when (pos? cnt)
                         (let [cur (:active-index (or (gobj/get el k-model) (read-model el)))
                               m2  (or (gobj/get el k-model) (read-model el))
                               nxt (model/resolve-target-index cur 1 cnt (:loop? m2))]
                           (when (not= cur nxt)
                             (go-to! el nxt 1)))))))
                 (:interval m)))))
  nil)

(defn- restart-autoplay! [^js el]
  (stop-autoplay! el)
  (start-autoplay! el))

;; ── Navigation ──────────────────────────────────────────────────────────────
(defn- go-to!
  "Navigate to the given slide index. Dispatches events and animates.
  hint-delta: optional +1 or -1 indicating intended direction (from next!/prev!).
  Without hint, direction is inferred from shortest path."
  ([^js el target-idx] (go-to! el target-idx nil))
  ([^js el target-idx hint-delta]
  (let [m     (or (gobj/get el k-model) (read-model el))
        cnt   (or (gobj/get el k-child-count) (child-count el))
        prev  (:active-index m)]
    (when (and (pos? cnt) (not (:disabled? m)))
      (let [loop?   (model/effective-loop? (:loop? m) cnt)
            target  (if loop?
                      (model/wrap-index target-idx cnt)
                      (model/clamp-index target-idx cnt))
            delta   (- target prev)]
        (when (not= target prev)
          ;; Determine direction: use hint if provided, otherwise infer
          (let [raw-delta (or hint-delta (if (pos? delta) 1 -1))
                crosses?  (model/crosses-boundary? prev target raw-delta cnt)
                dir       (model/direction-for-delta raw-delta)]
            ;; Dispatch loop event if crossing boundary (cancelable)
            (when (or (not crosses?)
                      (du/dispatch-cancelable! el model/event-loop
                                              (clj->js (model/loop-detail dir))))
              ;; Dispatch start event
              (du/dispatch! el model/event-start
                           (clj->js (model/start-detail dir prev)))
              ;; Update model and position
              (let [new-m (assoc m :active-index target)]
                (gobj/set el k-model new-m)
                (.setAttribute el model/attr-active-index (str target))
                (if loop?
                  (animate-loop-step! el new-m raw-delta)
                  (position-slides! el new-m))
                (update-indicators! el target)
                ;; Update control button states
                (let [{:keys [prev-btn next-btn live]} (ensure-refs! el)
                      ^js prev-btn prev-btn
                      ^js next-btn next-btn
                      ^js live     live]
                  (set! (.-disabled prev-btn) (not (model/can-go-prev? new-m cnt)))
                  (set! (.-disabled next-btn) (not (model/can-go-next? new-m cnt)))
                  ;; Update live region
                  (set! (.-textContent live) (str "Slide " (inc target) " of " cnt)))
                ;; Dispatch change and end events
                (du/dispatch! el model/event-change
                             (clj->js (model/change-detail target prev)))
                ;; Schedule end event after transition
                (if (prefers-reduced-motion?)
                  (du/dispatch! el model/event-end
                             (clj->js (model/end-detail target)))
                  (let [{:keys [track]} (ensure-refs! el)
                        ^js track track]
                    (letfn [(handler [^js e]
                              (when (= (.-target e) track)
                                (.removeEventListener track "transitionend" handler)
                                (du/dispatch! el model/event-end
                                             (clj->js (model/end-detail target)))))]
                      (.addEventListener track "transitionend" handler))))
                ;; Restart autoplay timer
                (restart-autoplay! el))))))))))

(defn- next! [^js el]
  (let [m   (or (gobj/get el k-model) (read-model el))
        cnt (or (gobj/get el k-child-count) (child-count el))
        nxt (model/resolve-target-index (:active-index m) 1 cnt (:loop? m))]
    (go-to! el nxt 1)))

(defn- prev! [^js el]
  (let [m   (or (gobj/get el k-model) (read-model el))
        cnt (or (gobj/get el k-child-count) (child-count el))
        prv (model/resolve-target-index (:active-index m) -1 cnt (:loop? m))]
    (go-to! el prv -1)))

;; ── Drag handling ───────────────────────────────────────────────────────────
(declare on-window-pointermove on-window-pointerup)

(defn- on-pointerdown [^js el ^js e]
  (let [m (or (gobj/get el k-model) (read-model el))]
    (when-not (:disabled? m)
      (let [{:keys [track]} (ensure-refs! el)
            ^js track track
            horiz? (horizontal? m)
            move-h (fn [evt] (on-window-pointermove el evt))]
        ;; Disable transition during drag
        (set! (.. track -style -transition) "none")
        (stop-autoplay! el)
        (letfn [(up-h [evt]
                  (.removeEventListener js/window "pointermove" move-h)
                  (.removeEventListener js/window "pointerup" up-h)
                  (on-window-pointerup el evt))]
          (.addEventListener js/window "pointermove" move-h)
          (.addEventListener js/window "pointerup" up-h)
          (gobj/set el k-drag-state
                    (js-obj dk-start-x     (.-clientX e)
                            dk-start-y     (.-clientY e)
                            dk-start-time  (.now js/Date)
                            dk-last-x      (.-clientX e)
                            dk-last-y      (.-clientY e)
                            dk-last-time   (.now js/Date)
                            dk-is-dragging false
                            dk-horiz       horiz?
                            dk-move-h      move-h
                            dk-up-h        up-h)))))))

(defn- on-window-pointermove [^js el ^js e]
  (when-let [ds (gobj/get el k-drag-state)]
    (.preventDefault e)
    (let [horiz?    (gobj/get ds dk-horiz)
          start-pos (if horiz? (gobj/get ds dk-start-x) (gobj/get ds dk-start-y))
          curr-pos  (if horiz? (.-clientX e) (.-clientY e))
          delta     (- curr-pos start-pos)]
      ;; Start dragging after threshold
      (when (and (not (gobj/get ds dk-is-dragging))
                 (> (js/Math.abs delta) 3))
        (gobj/set ds dk-is-dragging true))
      (when (gobj/get ds dk-is-dragging)
        ;; Track velocity
        (gobj/set ds dk-last-x (.-clientX e))
        (gobj/set ds dk-last-y (.-clientY e))
        (gobj/set ds dk-last-time (.now js/Date))
        ;; Move the track
        (let [{:keys [viewport track]} (ensure-refs! el)
              ^js viewport viewport
              ^js track    track
              m     (or (gobj/get el k-model) (read-model el))
              size  (slide-size el m)
              gap   (:gap m)
              cnt   (or (gobj/get el k-child-count) (child-count el))
              active (:active-index m)
              loop?  (model/effective-loop? (:loop? m) cnt)
              vp-sz (viewport-size viewport m)
              snap-off (snap-offset (:snap m) vp-sz size)
              base-offset (- (if loop?
                               (let [half (quot cnt 2)]
                                 (* half (+ size gap)))
                               (* active (+ size gap)))
                             snap-off)
              ;; Apply resistance at boundaries for non-loop
              effective-delta (if (and (not loop?)
                                      (or (and (neg? delta) (= active (dec cnt)))
                                          (and (pos? delta) (= active 0))))
                               (* delta 0.3)
                               delta)
              translate (- effective-delta base-offset)]
          (set-track-transform! track
                                (if horiz?
                                  (str "translateX(" translate "px)")
                                  (str "translateY(" translate "px)"))))))))

(defn- on-window-pointerup [^js el ^js e]
  (when-let [ds (gobj/get el k-drag-state)]
    (gobj/set el k-drag-state nil)
    (let [{:keys [track]} (ensure-refs! el)
          ^js track track]
      ;; Re-enable transition
      (set! (.. track -style -transition) ""))
    (when (gobj/get ds dk-is-dragging)
      (let [horiz?    (gobj/get ds dk-horiz)
            start-pos (if horiz? (gobj/get ds dk-start-x) (gobj/get ds dk-start-y))
            end-pos   (if horiz? (.-clientX e) (.-clientY e))
            delta     (- end-pos start-pos)
            dt        (- (.now js/Date) (gobj/get ds dk-last-time))
            velocity  (if (pos? dt) (/ (js/Math.abs delta) dt) 0)
            m         (or (gobj/get el k-model) (read-model el))
            size      (slide-size el m)
            cnt       (or (gobj/get el k-child-count) (child-count el))
            active    (:active-index m)
            ;; Determine target slide
            nav-delta (cond
                        ;; High velocity -> move in direction
                        (> velocity 0.5)
                        (if (neg? delta) 1 -1)
                        ;; Dragged past 50% of slide
                        (> (js/Math.abs delta) (* 0.5 size))
                        (if (neg? delta) 1 -1)
                        ;; Snap back
                        :else 0)
            target    (if (zero? nav-delta)
                        active
                        (model/resolve-target-index active nav-delta cnt (:loop? m)))]
        (if (= target active)
          ;; Snap back to current
          (position-slides! el m)
          ;; Navigate to target with drag direction hint
          (go-to! el target (if (neg? delta) 1 -1)))
        ;; Restart autoplay
        (restart-autoplay! el)))))

;; ── Keyboard ────────────────────────────────────────────────────────────────
(defn- on-keydown [^js el ^js e]
  (let [m (or (gobj/get el k-model) (read-model el))]
    (when-not (:disabled? m)
      (let [key  (.-key e)
            horiz? (horizontal? m)
            handled?
            (cond
              (and horiz? (= key "ArrowLeft"))  (do (prev! el) true)
              (and horiz? (= key "ArrowRight")) (do (next! el) true)
              (and (not horiz?) (= key "ArrowUp"))   (do (prev! el) true)
              (and (not horiz?) (= key "ArrowDown")) (do (next! el) true)
              (= key "Home") (do (go-to! el 0) true)
              (= key "End")  (do (go-to! el (dec (or (gobj/get el k-child-count)
                                                     (child-count el)))) true)
              :else false)]
        (when handled?
          (.preventDefault e))))))

;; ── Visibility change (autoplay pause) ──────────────────────────────────────
(defn- on-visibility-change [^js el]
  (if (.-hidden js/document)
    (stop-autoplay! el)
    (restart-autoplay! el)))

;; ── Slot change ─────────────────────────────────────────────────────────────
(defn- on-slotchange [^js el]
  (let [m   (or (gobj/get el k-model) (read-model el))
        cnt (child-count el)]
    (gobj/set el k-child-count cnt)
    ;; Clamp active index if needed
    (when (and (pos? cnt) (>= (:active-index m) cnt))
      (let [clamped (dec cnt)]
        (.setAttribute el model/attr-active-index (str clamped))
        (gobj/set el k-model (assoc m :active-index clamped))))
    ;; Rebuild indicators and update button states
    (let [m2  (or (gobj/get el k-model) (read-model el))
          {:keys [prev-btn next-btn live]} (ensure-refs! el)
          ^js prev-btn prev-btn
          ^js next-btn next-btn
          ^js live     live]
      (when (:show-indicators? m2)
        (build-indicators! el cnt (:active-index m2) (fn [i] (go-to! el i))))
      (set! (.-disabled prev-btn) (not (model/can-go-prev? m2 cnt)))
      (set! (.-disabled next-btn) (not (model/can-go-next? m2 cnt)))
      (when (pos? cnt)
        (set! (.-textContent live) (str "Slide " (inc (:active-index m2)) " of " cnt)))
      (position-slides! el m2))))

;; ── Listener management ────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [{:keys [prev-btn next-btn slot viewport]} (ensure-refs! el)
        ^js prev-btn prev-btn
        ^js next-btn next-btn
        ^js slot     slot
        ^js viewport viewport
        prev-h    (fn [_e] (prev! el))
        next-h    (fn [_e] (next! el))
        key-h     (fn [e] (on-keydown el e))
        pdown-h   (fn [e] (on-pointerdown el e))
        slot-h    (fn [_e] (on-slotchange el))
        vis-h     (fn [_e] (on-visibility-change el))
        enter-h   (fn [_e] (stop-autoplay! el))
        leave-h   (fn [_e] (restart-autoplay! el))
        focusin-h (fn [_e] (stop-autoplay! el))
        focusout-h (fn [_e] (restart-autoplay! el))]
    (.addEventListener prev-btn "click" prev-h)
    (.addEventListener next-btn "click" next-h)
    (.addEventListener el "keydown" key-h)
    (.addEventListener viewport "pointerdown" pdown-h)
    (.addEventListener slot "slotchange" slot-h)
    (.addEventListener js/document "visibilitychange" vis-h)
    (.addEventListener el "pointerenter" enter-h)
    (.addEventListener el "pointerleave" leave-h)
    (.addEventListener el "focusin" focusin-h)
    (.addEventListener el "focusout" focusout-h)
    (gobj/set el k-handlers
              #js {:prev      prev-h
                   :next      next-h
                   :keydown   key-h
                   :pdown     pdown-h
                   :slot      slot-h
                   :vis       vis-h
                   :enter     enter-h
                   :leave     leave-h
                   :focusin   focusin-h
                   :focusout  focusout-h}))
  nil)

(defn- remove-listeners! [^js el]
  (stop-autoplay! el)
  (let [hs   (gobj/get el k-handlers)
        refs (gobj/get el k-refs)]
    (when (and hs refs)
      (let [^js prev-btn (:prev-btn refs)
            ^js next-btn (:next-btn refs)
            ^js slot     (:slot refs)
            ^js viewport (:viewport refs)]
        (when-let [h (gobj/get hs "prev")]    (.removeEventListener prev-btn "click" h))
        (when-let [h (gobj/get hs "next")]    (.removeEventListener next-btn "click" h))
        (when-let [h (gobj/get hs "keydown")] (.removeEventListener el "keydown" h))
        (when-let [h (gobj/get hs "pdown")]   (.removeEventListener viewport "pointerdown" h))
        ;; Clean up any in-progress drag window listeners
        (when-let [ds (gobj/get el k-drag-state)]
          (when-let [mh (gobj/get ds dk-move-h)] (.removeEventListener js/window "pointermove" mh))
          (when-let [uh (gobj/get ds dk-up-h)]   (.removeEventListener js/window "pointerup" uh))
          (gobj/set el k-drag-state nil))
        (when-let [h (gobj/get hs "slot")]    (.removeEventListener slot "slotchange" h))
        (when-let [h (gobj/get hs "vis")]     (.removeEventListener js/document "visibilitychange" h))
        (when-let [h (gobj/get hs "enter")]   (.removeEventListener el "pointerenter" h))
        (when-let [h (gobj/get hs "leave")]   (.removeEventListener el "pointerleave" h))
        (when-let [h (gobj/get hs "focusin")] (.removeEventListener el "focusin" h))
        (when-let [h (gobj/get hs "focusout")] (.removeEventListener el "focusout" h)))))
  (gobj/set el k-handlers nil)
  nil)

;; ── Resize observer ─────────────────────────────────────────────────────────
(defn- setup-resize-observer! [^js el]
  (when-let [old (gobj/get el k-resize-obs)]
    (.disconnect ^js old))
  (let [{:keys [viewport]} (ensure-refs! el)
        ^js viewport viewport
        obs (js/ResizeObserver.
             (fn [_entries]
               (when (.-isConnected el)
                 (let [m (or (gobj/get el k-model) (read-model el))]
                   (position-slides! el m)))))]
    (.observe obs viewport)
    (gobj/set el k-resize-obs obs))
  nil)

(defn- teardown-resize-observer! [^js el]
  (when-let [obs (gobj/get el k-resize-obs)]
    (.disconnect ^js obs)
    (gobj/set el k-resize-obs nil))
  nil)

;; ── DOM patching ────────────────────────────────────────────────────────────
(defn- apply-model! [^js el {:keys [mode show-controls? show-indicators?
                                    disabled? label active-index] :as m}]
  (let [{:keys [viewport prev-btn next-btn live]} (ensure-refs! el)
        ^js viewport viewport
        ^js prev-btn prev-btn
        ^js next-btn next-btn
        ^js live     live
        cnt (or (gobj/get el k-child-count) (child-count el))]
    ;; Data attributes for CSS
    (.setAttribute el "data-mode" mode)
    (if show-controls?
      (.removeAttribute el "data-hide-controls")
      (.setAttribute el "data-hide-controls" ""))

    ;; Aria
    (if (seq label)
      (.setAttribute viewport "aria-label" label)
      (.removeAttribute viewport "aria-label"))

    ;; Control button states
    (set! (.-disabled prev-btn) (not (model/can-go-prev? m cnt)))
    (set! (.-disabled next-btn) (not (model/can-go-next? m cnt)))

    ;; Tabindex for keyboard navigation
    (set! (.-tabIndex el) (if disabled? -1 0))

    ;; Indicators
    (when show-indicators?
      (build-indicators! el cnt active-index (fn [i] (go-to! el i))))
    (when-not show-indicators?
      (let [{:keys [indicators]} (ensure-refs! el)]
        (set! (.-innerHTML ^js indicators) "")))

    ;; Live region
    (when (pos? cnt)
      (set! (.-textContent live) (str "Slide " (inc active-index) " of " cnt)))

    ;; Position slides
    (position-slides! el m)

    ;; Cache
    (gobj/set el k-model m))
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m)))
  nil)

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  ;; mode (string)
  (.defineProperty js/Object proto model/attr-mode
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-mode) "horizontal")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-mode (str v))
                                          (.removeAttribute this model/attr-mode))))
                        :enumerable true :configurable true})

  ;; snap (string)
  (.defineProperty js/Object proto model/attr-snap
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-snap) "none")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-snap (str v))
                                          (.removeAttribute this model/attr-snap))))
                        :enumerable true :configurable true})

  ;; loop (boolean)
  (.defineProperty js/Object proto model/attr-loop
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this model/attr-loop)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-loop "")
                                          (.removeAttribute this model/attr-loop))))
                        :enumerable true :configurable true})

  ;; autoPlay (boolean, camelCase)
  (.defineProperty js/Object proto "autoPlay"
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this model/attr-auto-play)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-auto-play "")
                                          (.removeAttribute this model/attr-auto-play))))
                        :enumerable true :configurable true})

  ;; interval (number)
  (.defineProperty js/Object proto model/attr-interval
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-interval
                                         (.getAttribute this model/attr-interval))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-interval)
                                          (.setAttribute this model/attr-interval (str (int v))))))
                        :enumerable true :configurable true})

  ;; showControls (boolean default-true, camelCase)
  (.defineProperty js/Object proto "showControls"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-bool-default-true
                                         (.getAttribute this model/attr-show-controls))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.removeAttribute this model/attr-show-controls)
                                          (.setAttribute this model/attr-show-controls "false"))))
                        :enumerable true :configurable true})

  ;; showIndicators (boolean, camelCase)
  (.defineProperty js/Object proto "showIndicators"
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this model/attr-show-indicators)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-show-indicators "")
                                          (.removeAttribute this model/attr-show-indicators))))
                        :enumerable true :configurable true})

  ;; activeIndex (number, camelCase)
  (.defineProperty js/Object proto "activeIndex"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-active-index
                                         (.getAttribute this model/attr-active-index))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-active-index)
                                          (.setAttribute this model/attr-active-index (str (int v))))))
                        :enumerable true :configurable true})

  ;; gap (number)
  (.defineProperty js/Object proto model/attr-gap
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-gap
                                         (.getAttribute this model/attr-gap))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-gap)
                                          (.setAttribute this model/attr-gap (str (int v))))))
                        :enumerable true :configurable true})

  ;; disabled (boolean)
  (.defineProperty js/Object proto model/attr-disabled
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this model/attr-disabled)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-disabled "")
                                          (.removeAttribute this model/attr-disabled))))
                        :enumerable true :configurable true})

  ;; label (string)
  (.defineProperty js/Object proto model/attr-label
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-label) "")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (and v (not= v ""))
                                          (.setAttribute this model/attr-label (str v))
                                          (.removeAttribute this model/attr-label))))
                        :enumerable true :configurable true}))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (ensure-refs! this)
                     (remove-listeners! this)
                     (add-listeners! this)
                     (setup-resize-observer! this)
                     (update-from-attrs! this)
                     (start-autoplay! this)
                     nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (remove-listeners! this)
                     (teardown-resize-observer! this)
                     ;; Clean up order styles on slotted children
                     (let [children (get-children this)]
                       (dotimes [i (.-length children)]
                         (set! (.. ^js (aget children i) -style -order) "")))
                     nil)))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [_name old-val new-val]
            (this-as ^js this
                     (when (not= old-val new-val)
                       (update-from-attrs! this)
                       ;; Restart autoplay on relevant attribute changes
                       (when (and (.-isConnected this)
                                  (or (= _name model/attr-auto-play)
                                      (= _name model/attr-interval)
                                      (= _name model/attr-disabled)))
                         (restart-autoplay! this)))
                     nil)))

    (let [^js proto (.-prototype klass)]
      (install-property-accessors! proto)

      ;; Methods on prototype
      (set! (.-goTo proto)
            (fn [idx]
              (this-as ^js this (go-to! this idx))))
      (set! (.-next proto)
            (fn []
              (this-as ^js this (next! this))))
      (set! (.-prev proto)
            (fn []
              (this-as ^js this (prev! this)))))

    klass))

;; ── Public API ──────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
