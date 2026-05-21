(ns baredom.components.x-split-pane.x-split-pane
  "DOM + lifecycle layer for x-split-pane.

   A resizable two-panel layout. The divider position is a percentage held
   in the `position` attribute — the single source of truth. A drag or
   keyboard step writes that attribute; the render pipeline reads it back
   and applies `flex-basis`. The element is stateless: instance fields hold
   only the refs map, the cached model, and a transient dragging flag."
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.x-split-pane.model :as model]))

;; ── Instance-field keys (host storage via du/setv! / du/getv) ────────────────
(def ^:private k-refs      "__xSplitPaneRefs")
(def ^:private k-model     "__xSplitPaneModel")
(def ^:private k-dragging? "__xSplitPaneDragging")

;; ── Refs-object keys (set on the JS refs map) ────────────────────────────────
(def ^:private rk-start-pane "start-pane")
(def ^:private rk-divider    "divider")
(def ^:private rk-end-pane   "end-pane")

;; ── String-literal constants ─────────────────────────────────────────────────
(def ^:private attr-part             "part")
(def ^:private attr-name             "name")
(def ^:private attr-id               "id")
(def ^:private attr-role             "role")
(def ^:private attr-tabindex         "tabindex")
(def ^:private attr-aria-hidden      "aria-hidden")
(def ^:private attr-aria-label       "aria-label")
(def ^:private attr-aria-orientation "aria-orientation")
(def ^:private attr-aria-valuenow    "aria-valuenow")
(def ^:private attr-aria-valuemin    "aria-valuemin")
(def ^:private attr-aria-valuemax    "aria-valuemax")
(def ^:private attr-aria-controls    "aria-controls")
(def ^:private attr-data-orientation "data-orientation")

(def ^:private part-start-pane   "start-pane")
(def ^:private part-divider      "divider")
(def ^:private part-divider-line "divider-line")
(def ^:private part-end-pane     "end-pane")

(def ^:private role-separator "separator")

(def ^:private aria-orientation-horizontal "horizontal")
(def ^:private aria-orientation-vertical   "vertical")
(def ^:private aria-hidden-true            "true")
(def ^:private tabindex-focusable          "0")

(def ^:private ev-pointerdown   "pointerdown")
(def ^:private ev-pointermove   "pointermove")
(def ^:private ev-pointerup     "pointerup")
(def ^:private ev-pointercancel "pointercancel")
(def ^:private ev-keydown       "keydown")

;; ── Styles ───────────────────────────────────────────────────────────────────
;; The host must have a definite size along the split axis (height for a
;; horizontal split with percentage panes, or both). See docs/x-split-pane.md.
(def ^:private style-text
  (str
   ":host{"
   "display:flex;"
   "flex-direction:row;"
   "box-sizing:border-box;"
   "inline-size:100%;"
   "overflow:hidden;"
   "color-scheme:light dark;"
   "--x-split-pane-divider-size:0.5rem;"
   "--x-split-pane-divider-line-size:1px;"
   "--x-split-pane-divider-color:var(--x-color-border,#cbd5e1);"
   "--x-split-pane-divider-hover-color:var(--x-color-primary,#3b82f6);"
   "--x-split-pane-divider-focus-color:var(--x-color-focus-ring,#60a5fa);"
   "}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-split-pane-divider-color:var(--x-color-border,#374151);"
   "--x-split-pane-divider-focus-color:var(--x-color-focus-ring,#93c5fd);"
   "}}"

   ;; Larger divider hit area on touch / coarse-pointer devices.
   "@media (pointer:coarse){"
   ":host{--x-split-pane-divider-size:1rem;}"
   "}"

   ":host([data-orientation=vertical]){flex-direction:column;}"

   ;; Panes — flex children. min-*:0 prevents flex blow-out; the real
   ;; per-pane minimum is applied inline by apply-sizing!.
   "[part=start-pane],[part=end-pane]{"
   "box-sizing:border-box;"
   "min-inline-size:0;"
   "min-block-size:0;"
   "overflow:auto;"
   "}"
   "[part=start-pane]{flex:0 0 50%;}"
   "[part=end-pane]{flex:1 1 0;}"

   ;; Divider — a thin visible line centred in a larger hit area.
   "[part=divider]{"
   "flex:0 0 var(--x-split-pane-divider-size);"
   "box-sizing:border-box;"
   "display:flex;"
   "align-items:center;"
   "justify-content:center;"
   "cursor:col-resize;"
   "touch-action:none;"
   "background:transparent;"
   "}"
   ":host([data-orientation=vertical]) [part=divider]{cursor:row-resize;}"

   "[part=divider-line]{"
   "background:var(--x-split-pane-divider-color);"
   "transition:background 120ms ease;"
   "inline-size:var(--x-split-pane-divider-line-size);"
   "block-size:100%;"
   "}"
   ":host([data-orientation=vertical]) [part=divider-line]{"
   "inline-size:100%;"
   "block-size:var(--x-split-pane-divider-line-size);"
   "}"

   "[part=divider]:hover [part=divider-line],"
   "[part=divider]:active [part=divider-line]{"
   "background:var(--x-split-pane-divider-hover-color);"
   "}"
   "[part=divider]:focus-visible{outline:none;}"
   "[part=divider]:focus-visible [part=divider-line]{"
   "background:var(--x-split-pane-divider-focus-color);"
   "box-shadow:0 0 0 2px var(--x-split-pane-divider-focus-color);"
   "}"

   ;; Disabled — no pointer or keyboard interaction.
   ":host([disabled]) [part=divider]{"
   "cursor:default;"
   "pointer-events:none;"
   "}"

   "@media (prefers-reduced-motion:reduce){"
   "[part=divider-line]{transition:none;}"
   "}"))

;; ── Shadow DOM builders ──────────────────────────────────────────────────────
(defn- make-pane! [part-name slot-name]
  (let [pane (.createElement js/document "div")
        slot (.createElement js/document "slot")]
    (du/set-attr! pane attr-part part-name)
    (du/set-attr! slot attr-name slot-name)
    (.appendChild pane slot)
    pane))

(defn- make-divider! []
  (let [divider (.createElement js/document "div")
        line    (.createElement js/document "div")]
    (du/set-attr! divider attr-part          part-divider)
    (du/set-attr! divider attr-role          role-separator)
    (du/set-attr! divider attr-aria-valuemin (str model/min-position))
    (du/set-attr! divider attr-aria-valuemax (str model/max-position))
    (du/set-attr! divider attr-aria-controls part-start-pane)
    (du/set-attr! line    attr-part          part-divider-line)
    (du/set-attr! line    attr-aria-hidden   aria-hidden-true)
    (.appendChild divider line)
    divider))

(defn- make-shadow! [^js el]
  (let [root    (.attachShadow el #js {:mode "open"})
        style   (.createElement js/document "style")
        start   (make-pane! part-start-pane model/slot-start)
        divider (make-divider!)
        end     (make-pane! part-end-pane model/slot-end)]
    (set! (.-textContent style) style-text)
    (du/set-attr! start attr-id part-start-pane)
    (.appendChild root style)
    (.appendChild root start)
    (.appendChild root divider)
    (.appendChild root end)
    (let [refs #js {}]
      (gobj/set refs rk-start-pane start)
      (gobj/set refs rk-divider    divider)
      (gobj/set refs rk-end-pane   end)
      (du/setv! el k-refs refs)
      refs)))

;; ── Read element state from attributes ───────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:orientation-raw   (du/get-attr el model/attr-orientation)
    :position-raw      (du/get-attr el model/attr-position)
    :min-start-raw     (du/get-attr el model/attr-min-start)
    :min-end-raw       (du/get-attr el model/attr-min-end)
    :disabled?         (du/has-attr? el model/attr-disabled)
    :divider-label-raw (du/get-attr el model/attr-divider-label)}))

;; ── DOM patching (render-orchestrator: phase list of named helpers) ──────────
(defn- apply-orientation! [^js el {:keys [orientation]}]
  (du/set-attr! el attr-data-orientation orientation))

(defn- apply-sizing! [^js el {:keys [orientation position min-start min-end]}]
  (let [^js refs        (du/getv el k-refs)
        ^js start-style (.-style (gobj/get refs rk-start-pane))
        ^js end-style   (.-style (gobj/get refs rk-end-pane))
        horizontal?     (= orientation model/orientation-horizontal)
        start-px        (str min-start "px")
        end-px          (str min-end "px")]
    (set! (.-flexBasis start-style) (str position "%"))
    ;; Clear both axes first — orientation can flip between renders.
    (set! (.-minWidth  start-style) "0px")
    (set! (.-minHeight start-style) "0px")
    (set! (.-minWidth  end-style)   "0px")
    (set! (.-minHeight end-style)   "0px")
    (if horizontal?
      (do (set! (.-minWidth  start-style) start-px)
          (set! (.-minWidth  end-style)   end-px))
      (do (set! (.-minHeight start-style) start-px)
          (set! (.-minHeight end-style)   end-px)))))

(defn- apply-divider! [^js el {:keys [orientation position disabled? divider-label]}]
  (let [^js divider      (gobj/get (du/getv el k-refs) rk-divider)
        ;; A horizontal split is separated by a vertical bar, and vice versa.
        aria-orientation (if (= orientation model/orientation-horizontal)
                           aria-orientation-vertical
                           aria-orientation-horizontal)]
    (du/set-attr! divider attr-aria-orientation aria-orientation)
    (du/set-attr! divider attr-aria-valuenow    (str (js/Math.round position)))
    (du/set-attr! divider attr-aria-label       divider-label)
    (if disabled?
      (du/remove-attr! divider attr-tabindex)
      (du/set-attr!    divider attr-tabindex tabindex-focusable))))

(defn- apply-model! [^js el m]
  (apply-orientation! el m)
  (apply-sizing!      el m)
  (apply-divider!     el m)
  (du/setv! el k-model m))

(defn- update-from-attrs! [^js el]
  (when (du/getv el k-refs)
    (let [new-m (read-model el)
          old-m (du/getv el k-model)]
      (when (not= new-m old-m)
        (apply-model! el new-m)))))

;; ── Drag geometry ────────────────────────────────────────────────────────────
(defn- container-size [^js el orientation]
  (let [^js rect (.getBoundingClientRect el)]
    (if (= orientation model/orientation-horizontal)
      (.-width rect)
      (.-height rect))))

(defn- position-from-event
  "Compute the clamped position percentage for a pointer event, centring
   the divider on the pointer and honouring the per-pane pixel minimums."
  [^js el ^js evt {:keys [orientation min-start min-end]}]
  (let [horizontal?  (= orientation model/orientation-horizontal)
        ^js host     (.getBoundingClientRect el)
        ^js divider  (.getBoundingClientRect (gobj/get (du/getv el k-refs) rk-divider))
        client       (if horizontal? (.-clientX evt) (.-clientY evt))
        rect-start   (if horizontal? (.-left host)   (.-top host))
        rect-size    (if horizontal? (.-width host)  (.-height host))
        divider-size (if horizontal? (.-width divider) (.-height divider))
        raw          (model/pointer->percent (- client (/ divider-size 2))
                                             rect-start rect-size)]
    (model/clamp-by-mins raw rect-size min-start min-end)))

(defn- commit-position!
  "Write a new position and emit the resize event when it actually changed."
  [^js el m position]
  (when (not= position (:position m))
    (du/set-attr! el model/attr-position (str position))
    (du/dispatch! el model/event-resize
                  #js {:position position :orientation (:orientation m)})))

;; ── Event handlers (listener-spec named pattern) ─────────────────────────────
(defn- on-divider-pointerdown! [^js el ^js evt]
  (let [m (du/getv el k-model)]
    (when-not (:disabled? m)
      (.preventDefault evt)
      (.setPointerCapture (gobj/get (du/getv el k-refs) rk-divider)
                          (.-pointerId evt))
      (du/setv! el k-dragging? true))))

(defn- on-divider-pointermove! [^js el ^js evt]
  (when (du/getv el k-dragging?)
    (let [m (du/getv el k-model)]
      (commit-position! el m (position-from-event el evt m)))))

(defn- on-divider-pointerup! [^js el ^js evt]
  (when (du/getv el k-dragging?)
    (du/setv! el k-dragging? false)
    (let [^js divider (gobj/get (du/getv el k-refs) rk-divider)]
      (when (.hasPointerCapture divider (.-pointerId evt))
        (.releasePointerCapture divider (.-pointerId evt))))
    (let [m (du/getv el k-model)]
      (du/dispatch! el model/event-resize-end
                    #js {:position (:position m) :orientation (:orientation m)}))))

(defn- on-divider-keydown! [^js el ^js evt]
  (let [m     (du/getv el k-model)
        delta (model/keyboard-delta (.-key evt) (:orientation m))]
    (when (and delta (not (:disabled? m)))
      (.preventDefault evt)
      (let [position (model/clamp-by-mins
                      (model/next-position (:position m) delta)
                      (container-size el (:orientation m))
                      (:min-start m) (:min-end m))]
        (when (not= position (:position m))
          (du/set-attr! el model/attr-position (str position))
          (du/dispatch! el model/event-resize
                        #js {:position position :orientation (:orientation m)})
          (du/dispatch! el model/event-resize-end
                        #js {:position position :orientation (:orientation m)}))))))

;; Each entry: [refs-key event-name handler-fn]. Listeners bind to the
;; divider — a shadow-DOM node that persists with the element — so no
;; explicit remove path is needed across disconnect/reconnect cycles.
(def ^:private listener-spec
  [[rk-divider ev-pointerdown   on-divider-pointerdown!]
   [rk-divider ev-pointermove   on-divider-pointermove!]
   [rk-divider ev-pointerup     on-divider-pointerup!]
   [rk-divider ev-pointercancel on-divider-pointerup!]
   [rk-divider ev-keydown       on-divider-keydown!]])

(defn- install-listeners! [^js el]
  (let [^js refs (du/getv el k-refs)]
    (doseq [[refs-key event-name handler] listener-spec]
      (let [^js target (gobj/get refs refs-key)]
        (.addEventListener target event-name (fn [event] (handler el event)))))))

;; ── Lifecycle ────────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el)
    (du/setv! el k-dragging? false)
    (install-listeners! el))
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  ;; Reset transient drag state so a reconnect starts clean.
  (du/setv! el k-dragging? false))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Property accessors ───────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

;; ── Public API ───────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
