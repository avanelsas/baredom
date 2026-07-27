(ns baredom.components.x-floating-panel.x-floating-panel
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.x-floating-panel.model :as model]))

;; ── Instance-field keys ──────────────────────────────────────────────────────
(def ^:private k-refs     "__xFloatingPanelRefs")
(def ^:private k-model    "__xFloatingPanelModel")
(def ^:private k-handlers "__xFloatingPanelHandlers")
(def ^:private k-drag     "__xFloatingPanelDrag")
(def ^:private k-raf      "__xFloatingPanelRaf")
(def ^:private k-restore  "__xFloatingPanelRestore")

;; ── Refs keys ────────────────────────────────────────────────────────────────
(def ^:private rk-panel       "panel")
(def ^:private rk-handle      "handle")
(def ^:private rk-title       "title")
(def ^:private rk-header-slot "headerSlot")
(def ^:private rk-close       "close")
(def ^:private rk-body        "body")
(def ^:private rk-window      "window")

;; ── Handler keys ─────────────────────────────────────────────────────────────
(def ^:private hk-pointer-down   "pointerDown")
(def ^:private hk-pointer-move   "pointerMove")
(def ^:private hk-pointer-end    "pointerEnd")
(def ^:private hk-handle-keydown "handleKeydown")
(def ^:private hk-panel-keydown  "panelKeydown")
(def ^:private hk-close-click    "closeClick")
(def ^:private hk-slot-change    "slotChange")
(def ^:private hk-window-resize  "windowResize")

;; ── String-literal constants (no duplication; Closure-Advanced safe) ─────────
(def ^:private ev-pointerdown   "pointerdown")
(def ^:private ev-pointermove   "pointermove")
(def ^:private ev-pointerup     "pointerup")
(def ^:private ev-pointercancel "pointercancel")
(def ^:private ev-keydown       "keydown")
(def ^:private ev-click         "click")
(def ^:private ev-slotchange    "slotchange")
(def ^:private ev-resize        "resize")

(def ^:private attr-part            "part")
(def ^:private attr-name            "name")
(def ^:private attr-id              "id")
(def ^:private attr-role            "role")
(def ^:private attr-tabindex        "tabindex")
(def ^:private attr-type            "type")
(def ^:private attr-aria-label           "aria-label")
(def ^:private attr-aria-labelledby      "aria-labelledby")
(def ^:private attr-aria-modal           "aria-modal")
(def ^:private attr-aria-roledescription "aria-roledescription")
(def ^:private attr-aria-keyshortcuts    "aria-keyshortcuts")

(def ^:private tag-div    "div")
(def ^:private tag-span   "span")
(def ^:private tag-slot   "slot")
(def ^:private tag-button "button")
(def ^:private tag-style  "style")

(def ^:private slot-header        "header")
(def ^:private id-title           "title")
(def ^:private role-dialog        "dialog")
(def ^:private role-button        "button")
(def ^:private aria-modal-false   "false")
(def ^:private tabindex-focusable "0")
(def ^:private type-button        "button")
(def ^:private handle-aria-label  "Move panel")

;; The handle is focusable and interactive, but it is not activated — Enter and
;; Space do nothing, arrow keys move the panel. `role=button` alone would
;; promise an activation that never happens, so aria-roledescription renames it
;; to what it actually is and aria-keyshortcuts advertises how to drive it.
(def ^:private handle-roledescription "Drag handle")
(def ^:private handle-keyshortcuts    "ArrowUp ArrowDown ArrowLeft ArrowRight")
(def ^:private close-aria-label   "Close panel")
(def ^:private close-glyph        "✕")
(def ^:private key-escape         "Escape")
(def ^:private primary-button     0)

(def ^:private selector-no-drag "[part=close],[data-no-drag]")

(def ^:private css-prop-left "left")
(def ^:private css-prop-top  "top")
(def ^:private px-suffix     "px")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:contents;"
   "color-scheme:light dark;"
   "--x-floating-panel-z:900;"
   "--x-floating-panel-bg:var(--x-color-bg,Canvas);"
   "--x-floating-panel-fg:var(--x-color-text,CanvasText);"
   "--x-floating-panel-border:var(--x-color-border,color-mix(in srgb,currentColor 12%,transparent));"
   "--x-floating-panel-radius:var(--x-radius-lg,12px);"
   "--x-floating-panel-shadow:var(--x-shadow-lg,0 10px 25px rgb(15 23 42/0.18));"
   "--x-floating-panel-duration:var(--x-transition-duration,150ms);"
   "--x-floating-panel-accent:var(--x-color-primary,#2563eb);"
   "}"

   "[part=panel]{"
   "position:fixed;"
   "top:var(--x-floating-panel-top,4rem);"
   "left:var(--x-floating-panel-left,2rem);"
   "z-index:var(--x-floating-panel-z);"
   "display:flex;"
   "flex-direction:column;"
   "inline-size:min(var(--x-floating-panel-width,22rem),calc(100vw - 2rem));"
   "max-inline-size:calc(100vw - 2rem);"
   "max-block-size:calc(100dvh - 2rem);"
   "background:var(--x-floating-panel-bg);"
   "color:var(--x-floating-panel-fg);"
   "border:1px solid var(--x-floating-panel-border);"
   "border-radius:var(--x-floating-panel-radius);"
   "box-shadow:var(--x-floating-panel-shadow);"
   "overflow:hidden;"
   "opacity:0;"
   "visibility:hidden;"
   "transform:translateY(-4px) scale(0.98);"
   "transition:opacity var(--x-floating-panel-duration) ease,"
   "transform var(--x-floating-panel-duration) ease,"
   "visibility 0s linear var(--x-floating-panel-duration);"
   "}"

   ":host([open]) [part=panel]{"
   "opacity:1;"
   "visibility:visible;"
   "transform:none;"
   "transition:opacity var(--x-floating-panel-duration) ease,"
   "transform var(--x-floating-panel-duration) ease,"
   "visibility 0s;"
   "}"

   "[part=handle]{"
   "display:flex;"
   "align-items:center;"
   "gap:var(--x-space-sm,0.5rem);"
   "flex:none;"
   "min-block-size:var(--x-floating-panel-handle-size,1.25rem);"
   "padding:var(--x-space-sm,0.5rem) var(--x-space-md,0.75rem);"
   "border-block-end:1px solid var(--x-floating-panel-border);"
   "cursor:grab;"
   "touch-action:none;"
   "user-select:none;"
   "-webkit-user-select:none;"
   "}"
   "[part=handle]::before{"
   "content:\"\";"
   "flex:none;"
   "inline-size:1.5rem;"
   "block-size:0.25rem;"
   "border-radius:999px;"
   "background:currentColor;"
   "opacity:0.25;"
   "}"
   "[part=handle]:active{cursor:grabbing;}"
   "[part=handle]:focus-visible{"
   "outline:2px solid var(--x-floating-panel-accent);"
   "outline-offset:-2px;"
   "}"
   "#title{flex:1 1 auto;min-inline-size:0;}"

   "[part=close]{"
   "display:none;"
   "flex:none;"
   "align-items:center;"
   "justify-content:center;"
   "inline-size:1.5rem;"
   "block-size:1.5rem;"
   "padding:0;"
   "border:0;"
   "border-radius:var(--x-radius-sm,6px);"
   "background:transparent;"
   "color:inherit;"
   "font:inherit;"
   "line-height:1;"
   "cursor:pointer;"
   "}"
   ":host([closable]) [part=close]{display:flex;}"
   "[part=close]:hover{background:color-mix(in srgb,currentColor 12%,transparent);}"
   "[part=close]:focus-visible{"
   "outline:2px solid var(--x-floating-panel-accent);"
   "outline-offset:1px;"
   "}"

   "[part=body]{"
   "flex:1 1 auto;"
   "min-block-size:0;"
   "padding:var(--x-space-lg,1rem);"
   "overflow:auto;"
   "}"
   ":host([resizable]) [part=panel]{inline-size:auto;}"
   ":host([resizable]) [part=body]{"
   "resize:both;"
   "min-inline-size:var(--x-floating-panel-min-width,12rem);"
   "min-block-size:var(--x-floating-panel-min-height,8rem);"
   "}"

   "@media (prefers-reduced-motion:reduce){"
   "[part=panel],:host([open]) [part=panel]{transition:none;}"
   "}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-floating-panel-bg:var(--x-color-bg,#1c1d24);"
   "--x-floating-panel-fg:var(--x-color-text,#e2e4ef);"
   "--x-floating-panel-border:var(--x-color-border,rgb(255 255 255/0.08));"
   "--x-floating-panel-shadow:var(--x-shadow-lg,0 12px 30px rgb(0 0 0/0.5));"
   "--x-floating-panel-accent:var(--x-color-primary,#3b82f6);"
   "}"
   "}"))

;; ── Shadow DOM builders (shadow-builders named pattern) ──────────────────────
(defn- make-handle! [^js refs]
  (let [handle (.createElement js/document tag-div)
        title  (.createElement js/document tag-span)
        hslot  (.createElement js/document tag-slot)
        close  (.createElement js/document tag-button)]
    (du/set-attr! handle attr-part                model/part-handle)
    (du/set-attr! handle attr-role                role-button)
    (du/set-attr! handle attr-tabindex            tabindex-focusable)
    (du/set-attr! handle attr-aria-label          handle-aria-label)
    (du/set-attr! handle attr-aria-roledescription handle-roledescription)
    (du/set-attr! handle attr-aria-keyshortcuts   handle-keyshortcuts)

    (du/set-attr! title attr-id   id-title)
    (du/set-attr! hslot attr-name slot-header)

    (du/set-attr! close attr-part       model/part-close)
    (du/set-attr! close attr-type       type-button)
    (du/set-attr! close attr-aria-label close-aria-label)
    (set! (.-textContent close) close-glyph)

    (.appendChild title hslot)
    (.appendChild handle title)
    (.appendChild handle close)

    (gobj/set refs rk-handle      handle)
    (gobj/set refs rk-title       title)
    (gobj/set refs rk-header-slot hslot)
    (gobj/set refs rk-close       close)
    handle))

(defn- make-body! [^js refs]
  (let [body (.createElement js/document tag-div)
        slot (.createElement js/document tag-slot)]
    (du/set-attr! body attr-part model/part-body)
    (.appendChild body slot)
    (gobj/set refs rk-body body)
    body))

(defn- make-panel! [^js refs]
  (let [panel (.createElement js/document tag-div)]
    (du/set-attr! panel attr-part       model/part-panel)
    (du/set-attr! panel attr-role       role-dialog)
    (du/set-attr! panel attr-aria-modal aria-modal-false)
    (.appendChild panel (make-handle! refs))
    (.appendChild panel (make-body! refs))
    (gobj/set refs rk-panel panel)
    panel))

(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document tag-style)
        refs  #js {}]
    (set! (.-textContent style) style-text)
    (.appendChild root style)
    (.appendChild root (make-panel! refs))
    (gobj/set refs rk-window js/window)
    (du/setv! el k-refs refs)))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

(defn- ref-of [^js el refs-key]
  (when-let [^js refs (du/getv el k-refs)]
    (gobj/get refs refs-key)))

;; ── Attribute readers ────────────────────────────────────────────────────────
(defn- header-slotted? [^js el]
  (let [^js slot (ref-of el rk-header-slot)]
    (boolean (and slot (pos? (.-length (.assignedNodes slot #js {:flatten true})))))))

(defn- read-model [^js el]
  (model/normalize
   {:open-present?          (du/has-attr? el model/attr-open)
    :x-raw                  (du/get-attr el model/attr-x)
    :y-raw                  (du/get-attr el model/attr-y)
    :closable-present?      (du/has-attr? el model/attr-closable)
    :label-raw              (du/get-attr el model/attr-label)
    :focus-on-open-present? (du/has-attr? el model/attr-focus-on-open)
    :step-raw               (du/get-attr el model/attr-step)
    :header-slotted?        (header-slotted? el)}))

;; ── Measurement (effects feeding the pure model fns) ─────────────────────────
(defn- panel-rect [^js el]
  (.getBoundingClientRect ^js (ref-of el rk-panel)))

(defn- current-position
  "Where the panel actually is right now, in viewport coordinates. Falls back
  to the measured rect so the first drag or nudge works from the CSS default
  placement, before any `x` / `y` attribute exists."
  [^js el]
  (let [m        (du/getv el k-model)
        ^js rect (panel-rect el)]
    {:x (or (:x m) (.-left rect))
     :y (or (:y m) (.-top rect))}))

(defn- clamp-to-viewport [^js el {:keys [x y]}]
  (let [^js rect   (panel-rect el)
        ^js handle (ref-of el rk-handle)]
    (model/clamp-position
     {:x        x
      :y        y
      :w        (.-width rect)
      :handle-h (.-offsetHeight handle)
      :vw       (.-innerWidth js/window)
      :vh       (.-innerHeight js/window)})))

;; ── Position writes ──────────────────────────────────────────────────────────
(defn- place!
  "The only way the panel's position changes: clamp, then write `x` / `y`.
  The render pipeline turns those attributes back into inline styles, so
  position stays a value.

  `write-attr!` is the attribute writer to use — `du/set-attr!` normally,
  `du/set-attr-untraced!` on the drag hot path. Passing the writer keeps the
  recorder's appetite out of the position logic; there is one clamp and one
  write, not one per tracing mode."
  [^js el pos write-attr!]
  (let [{:keys [x y]} (clamp-to-viewport el pos)]
    (write-attr! el model/attr-x (str x))
    (write-attr! el model/attr-y (str y))
    {:x x :y y}))

(defn- commit-position!
  "Place the panel and announce it. Used by every user-initiated move."
  [^js el pos source]
  (let [{:keys [x y]} (place! el pos du/set-attr!)]
    (du/dispatch! el model/event-move (model/move-event-detail x y source))))

(defn- reclamp-position!
  "Pull an author-set position back into reach. Author values are honoured
  verbatim by the render pipeline, so a panel parked beyond the viewport would
  otherwise be unreachable — no handle to grab, no way back."
  [^js el]
  (let [m (du/getv el k-model)]
    (when (and (.-isConnected el) m (:open? m) (some? (:x m)) (some? (:y m)))
      (place! el {:x (:x m) :y (:y m)} du/set-attr!))))

(defn- defer-reclamp!
  "Reclamp on the next task. Never call reclamp-position! from inside
  apply-model! — writing an observed attribute there re-enters the pipeline
  before the model cache is updated, which is the classic recursion trap."
  [^js el]
  (js/setTimeout (fn defer-reclamp-position [] (reclamp-position! el)) 0))

;; ── Drag hot path ────────────────────────────────────────────────────────────
;; A drag is one value under k-drag: which pointer, where inside the panel it
;; grabbed, and where that pointer is now. The panel position is never stored —
;; it is derived from that value by model/drag-position, so there is exactly one
;; answer to "where is the panel" at any moment.

(defn- drag-target
  "Panel top-left implied by the current drag, or nil when not dragging.
  k-drag is shaped to be a direct argument to the pure derivation."
  [^js el]
  (when-let [drag (du/getv el k-drag)]
    (model/drag-position drag)))

(defn- flush-drag-frame! [^js el]
  ;; Hot path: rAF-driven — no diagnostic value at 60 writes/sec.
  (du/setv-untraced! el k-raf nil)
  (when-let [pos (drag-target el)]
    ;; Hot path: pointermove-driven — the traced write happens once, on drop.
    (place! el pos du/set-attr-untraced!)))

(defn- schedule-drag-frame! [^js el]
  (when-not (du/getv el k-raf)
    ;; Hot path: rAF-driven — see flush-drag-frame!.
    (du/setv-untraced!
     el k-raf
     (js/requestAnimationFrame (fn render-drag-frame [_ts] (flush-drag-frame! el))))))

(defn- cancel-drag-frame! [^js el]
  (when-let [raf (du/getv el k-raf)]
    (js/cancelAnimationFrame raf)
    ;; Hot path: rAF-driven — see flush-drag-frame!.
    (du/setv-untraced! el k-raf nil)))

(defn- dragging-pointer? [^js el ^js event]
  (when-let [drag (du/getv el k-drag)]
    (= (:pointer-id drag) (.-pointerId event))))

(defn- end-drag! [^js el]
  (cancel-drag-frame! el)
  (du/setv! el k-drag nil))

;; ── Dismiss / show / hide ────────────────────────────────────────────────────
(defn- request-dismiss! [^js el reason]
  (when (du/dispatch-cancelable! el model/event-dismiss-request
                                 (model/dismiss-event-detail reason))
    (du/remove-attr! el model/attr-open)))

(defn- do-show! [^js el]
  (when-not (du/has-attr? el model/attr-open)
    (du/set-attr! el model/attr-open "")))

(defn- do-hide! [^js el]
  (du/remove-attr! el model/attr-open))

(defn- do-toggle! [^js el]
  (if (du/has-attr? el model/attr-open)
    (do-hide! el)
    (do-show! el)))

;; ── Focus handling ───────────────────────────────────────────────────────────
(defn- focus-inside? [^js el]
  (let [^js active (.-activeElement js/document)]
    (boolean (and active (.contains el active)))))

(defn- capture-focus!
  "Defer the focus move by a task so the `:host([open])` rule has been applied
  — a still-`visibility:hidden` panel is not focusable. Same deferral
  x-drawer's activate-focus-trap! needs, for the same reason."
  [^js el]
  (du/setv! el k-restore (.-activeElement js/document))
  (js/setTimeout
   (fn defer-focus-handle []
     (when-let [^js handle (ref-of el rk-handle)]
       (when (du/has-attr? el model/attr-open)
         (.focus handle))))
   0))

(defn- restore-focus! [^js el]
  (let [^js prev (du/getv el k-restore)
        inside?  (focus-inside? el)]
    (du/setv! el k-restore nil)
    (when (and prev inside? (.-isConnected prev))
      (.focus prev))))

;; ── DOM patching (render-orchestrator: phase list of named helpers) ──────────
(defn- apply-position! [^js panel {:keys [x y]}]
  (let [^js style (.-style panel)]
    (if (some? x)
      (.setProperty style css-prop-left (str x px-suffix))
      (.removeProperty style css-prop-left))
    (if (some? y)
      (.setProperty style css-prop-top (str y px-suffix))
      (.removeProperty style css-prop-top))))

(defn- apply-aria! [^js panel {:keys [label header-slotted?]}]
  (if header-slotted?
    (do (du/set-attr!    panel attr-aria-labelledby id-title)
        (du/remove-attr! panel attr-aria-label))
    (do (du/set-attr!    panel attr-aria-label label)
        (du/remove-attr! panel attr-aria-labelledby))))

(defn- apply-open-transition!
  "Fire the toggle event and move focus when `:open?` transitions. Compares
  old-m against new-m rather than keeping a separate flag — the same epochal
  boundary x-drawer's apply-open-transition! uses."
  [^js el old-m new-m]
  (let [old-open? (boolean (:open? old-m))
        new-open? (:open? new-m)]
    (when (not= old-open? new-open?)
      (du/dispatch! el model/event-toggle (model/toggle-event-detail new-open?))
      (if new-open?
        (do (defer-reclamp! el)
            (when (:focus-on-open? new-m) (capture-focus! el)))
        (restore-focus! el)))))

(defn- apply-model! [^js el old-m new-m]
  (let [^js panel (ref-of el rk-panel)]
    (apply-position!        panel new-m)
    (apply-aria!            panel new-m)
    (apply-open-transition! el old-m new-m)
    (du/setv! el k-model new-m)))

(defn- update-from-attrs! [^js el]
  (when (du/getv el k-refs)
    (let [new-m (read-model el)
          old-m (du/getv el k-model)]
      (when (not= old-m new-m)
        (apply-model! el old-m new-m)))))

;; ── Event handlers (listener-spec style) ─────────────────────────────────────
(defn- capture-pointer!
  "setPointerCapture throws NotFoundError when the pointer id is not active.
  Capture is an optimisation — it keeps pointermove flowing when the cursor
  leaves the handle — so a failure must not abort the drag."
  [^js handle pointer-id]
  (try (.setPointerCapture handle pointer-id)
       (catch :default _ nil)))

(defn- no-drag-target?
  "True when the press landed on a control rather than on the grab surface.
  The close button sits inside the handle, and slotted header content may opt
  out with `data-no-drag`. Starting a drag from either would capture the
  pointer and swallow the control's own activation."
  [^js event]
  (let [^js target (.-target event)]
    (boolean (and target
                  (.-closest target)
                  (.closest target selector-no-drag)))))

(defn- on-handle-pointer-down! [^js el ^js event]
  (when (and (= primary-button (.-button event))
             (not (no-drag-target? event))
             (nil? (du/getv el k-drag)))
    (let [^js handle (ref-of el rk-handle)
          ^js rect   (panel-rect el)]
      (capture-pointer! handle (.-pointerId event))
      (du/setv! el k-drag {:pointer-id (.-pointerId event)
                           :offset-x   (- (.-clientX event) (.-left rect))
                           :offset-y   (- (.-clientY event) (.-top rect))
                           :pointer-x  (.-clientX event)
                           :pointer-y  (.-clientY event)
                           :start-x    (.-left rect)
                           :start-y    (.-top rect)})
      (.preventDefault event))))

(defn- on-handle-pointer-move! [^js el ^js event]
  (when (dragging-pointer? el event)
    ;; Hot path: pointermove-driven — see flush-drag-frame!.
    (du/setv-untraced! el k-drag (assoc (du/getv el k-drag)
                                        :pointer-x (.-clientX event)
                                        :pointer-y (.-clientY event)))
    (schedule-drag-frame! el)))

(defn- on-handle-pointer-end!
  "Ends a drag, for both pointerup and pointercancel. A cancelled drag still
  announces: earlier frames already moved the panel, so staying silent would
  leave a consumer persisting position out of step with the DOM."
  [^js el ^js event]
  (when (dragging-pointer? el event)
    (let [drag (du/getv el k-drag)]
      (end-drag! el)
      (when (model/drag-moved? drag)
        (commit-position! el (model/drag-position drag) model/source-pointer)))))

(defn- on-handle-keydown! [^js el ^js event]
  (when-let [delta (model/arrow-delta (.-key event))]
    (.preventDefault event)
    (let [{:keys [x y]} (current-position el)
          m             (du/getv el k-model)
          step          (if (.-shiftKey event)
                          model/fine-step
                          (:step m model/default-step))]
      (commit-position! el
                        (model/nudge-position {:x    x
                                               :y    y
                                               :dx   (:dx delta)
                                               :dy   (:dy delta)
                                               :step step})
                        model/source-keyboard))))

(defn- on-panel-keydown! [^js el ^js event]
  (when (and (= key-escape (.-key event))
             (:closable? (du/getv el k-model)))
    (.preventDefault event)
    (request-dismiss! el model/reason-escape)))

(defn- on-close-click! [^js el ^js _event]
  (request-dismiss! el model/reason-close-button))

(defn- on-slot-change! [^js el ^js _event]
  (update-from-attrs! el))

(defn- on-window-resize! [^js el ^js _event]
  (reclamp-position! el))

;; ── Listener installation (listener-spec named pattern) ──────────────────────
;; Each entry: [refs-key event-name handler-key capture?]. add-listeners! and
;; remove-listeners! iterate the same spec, so they cannot drift. The window
;; resize listener outlives the shadow tree, hence the removable shape.
(def ^:private listener-spec
  [[rk-handle      ev-pointerdown   hk-pointer-down   false]
   [rk-handle      ev-pointermove   hk-pointer-move   false]
   [rk-handle      ev-pointerup     hk-pointer-end    false]
   [rk-handle      ev-pointercancel hk-pointer-end    false]
   [rk-handle      ev-keydown       hk-handle-keydown false]
   [rk-panel       ev-keydown       hk-panel-keydown  false]
   [rk-close       ev-click         hk-close-click    false]
   [rk-header-slot ev-slotchange    hk-slot-change    false]
   [rk-window      ev-resize        hk-window-resize  false]])

(defn- make-handlers [^js el]
  (let [handlers #js {}]
    (gobj/set handlers hk-pointer-down
              (fn handle-pointer-down [^js e] (on-handle-pointer-down! el e)))
    (gobj/set handlers hk-pointer-move
              (fn handle-pointer-move [^js e] (on-handle-pointer-move! el e)))
    (gobj/set handlers hk-pointer-end
              (fn handle-pointer-end [^js e] (on-handle-pointer-end! el e)))
    (gobj/set handlers hk-handle-keydown
              (fn handle-handle-keydown [^js e] (on-handle-keydown! el e)))
    (gobj/set handlers hk-panel-keydown
              (fn handle-panel-keydown [^js e] (on-panel-keydown! el e)))
    (gobj/set handlers hk-close-click
              (fn handle-close-click [^js e] (on-close-click! el e)))
    (gobj/set handlers hk-slot-change
              (fn handle-slot-change [^js e] (on-slot-change! el e)))
    (gobj/set handlers hk-window-resize
              (fn handle-window-resize [^js e] (on-window-resize! el e)))
    handlers))

(defn- iter-listeners! [^js el add?]
  (let [^js refs     (du/getv el k-refs)
        ^js handlers (du/getv el k-handlers)]
    (when (and refs handlers)
      (doseq [[refs-key event-name handler-key capture?] listener-spec]
        (let [^js target  (gobj/get refs refs-key)
              ^js handler (gobj/get handlers handler-key)]
          (when (and target handler)
            (if add?
              (.addEventListener target event-name handler capture?)
              (.removeEventListener target event-name handler capture?))))))))

(defn- remove-listeners! [^js el]
  (iter-listeners! el false)
  (du/setv! el k-handlers nil))

(defn- add-listeners! [^js el]
  (du/setv! el k-handlers (make-handlers el))
  (iter-listeners! el true))

;; ── Property accessors (Tier 1 — `x`/`y` need null-removes-attribute) ────────
(defn- install-property-accessors! [^js proto]
  (du/define-bool-prop!   proto "open"        model/attr-open)
  (du/define-number-prop! proto "x"           model/attr-x nil)
  (du/define-number-prop! proto "y"           model/attr-y nil)
  (du/define-number-prop! proto "step"        model/attr-step model/default-step)
  (du/define-bool-prop!   proto "closable"    model/attr-closable)
  (du/define-bool-prop!   proto "resizable"   model/attr-resizable)
  (du/define-string-prop! proto "label"       model/attr-label model/default-label)
  (du/define-bool-prop!   proto "focusOnOpen" model/attr-focus-on-open)

  (.defineProperty js/Object proto "show"
                   #js {:value (fn [] (this-as ^js this (do-show! this)))
                        :enumerable true :configurable true :writable true})

  (.defineProperty js/Object proto "hide"
                   #js {:value (fn [] (this-as ^js this (do-hide! this)))
                        :enumerable true :configurable true :writable true})

  (.defineProperty js/Object proto "toggle"
                   #js {:value (fn [] (this-as ^js this (do-toggle! this)))
                        :enumerable true :configurable true :writable true}))

;; ── Lifecycle ────────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el)
  (update-from-attrs! el)
  (defer-reclamp! el))

(defn- disconnected! [^js el]
  (end-drag! el)
  (remove-listeners! el)
  (du/setv! el k-restore nil))

(defn- attribute-changed! [^js el _attr-name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Public API ───────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
