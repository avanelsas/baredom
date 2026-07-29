(ns baredom.components.x-drag-panel.x-drag-panel
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.x-drag-panel.model :as model]
   [baredom.components.x-drop-zone.model :as zone-model]
   [baredom.components.x-drop-zone.x-drop-zone :as zone]))

;; ── Instance-field keys ──────────────────────────────────────────────────────
(def ^:private k-initialized? "__xDragPanelInitialized")
(def ^:private k-refs         "__xDragPanelRefs")
(def ^:private k-model        "__xDragPanelModel")
(def ^:private k-handlers     "__xDragPanelHandlers")
(def ^:private k-drag         "__xDragPanelDrag")
(def ^:private k-press        "__xDragPanelPress")
(def ^:private k-keyboard     "__xDragPanelKeyboard")
(def ^:private k-live         "__xDragPanelLive")
(def ^:private k-raf          "__xDragPanelRaf")
(def ^:private k-last-frame   "__xDragPanelLastFrame")
(def ^:private k-scrollers    "__xDragPanelScrollers")

;; ── Refs keys ────────────────────────────────────────────────────────────────
(def ^:private rk-host   "host")
(def ^:private rk-panel  "panel")
(def ^:private rk-handle "handle")
(def ^:private rk-window "window")

;; ── Handler keys ─────────────────────────────────────────────────────────────
(def ^:private hk-pointer-down   "pointerDown")
(def ^:private hk-pointer-move   "pointerMove")
(def ^:private hk-pointer-end    "pointerEnd")
(def ^:private hk-handle-keydown "handleKeydown")
(def ^:private hk-window-keydown "windowKeydown")

;; ── String-literal constants (no duplication; Closure-Advanced safe) ─────────
(def ^:private ev-pointerdown   "pointerdown")
(def ^:private ev-pointermove   "pointermove")
(def ^:private ev-pointerup     "pointerup")
(def ^:private ev-pointercancel "pointercancel")
(def ^:private ev-keydown       "keydown")

(def ^:private attr-part           "part")
(def ^:private attr-class          "class")
(def ^:private attr-role           "role")
(def ^:private attr-tabindex       "tabindex")
(def ^:private attr-aria-label     "aria-label")
(def ^:private attr-aria-hidden    "aria-hidden")
(def ^:private attr-aria-busy      "aria-busy")
(def ^:private attr-aria-live      "aria-live")
(def ^:private attr-name           "name")
(def ^:private attr-data-dragging  "data-dragging")

(def ^:private tag-div  "div")
(def ^:private tag-span "span")
(def ^:private tag-slot "slot")

(def ^:private slot-header "header")

(def ^:private role-button "button")
(def ^:private tabindex-focusable "0")
(def ^:private tabindex-off       "-1")
(def ^:private value-true  "true")
(def ^:private value-polite "polite")
(def ^:private class-sr-only "sr-only")

(def ^:private key-space  " ")
(def ^:private key-enter  "Enter")
(def ^:private key-escape "Escape")
(def ^:private key-left   "ArrowLeft")
(def ^:private key-right  "ArrowRight")
(def ^:private key-up     "ArrowUp")
(def ^:private key-down   "ArrowDown")

(def ^:private primary-button 0)

(def ^:private selector-no-drag "[data-no-drag],button,a,input,select,textarea")

(def ^:private style-position "position")
(def ^:private style-left     "left")
(def ^:private style-top      "top")
(def ^:private style-width    "width")
(def ^:private style-height   "height")
(def ^:private style-margin   "margin")
(def ^:private style-transform "transform")
(def ^:private position-fixed "fixed")
(def ^:private px "px")

(def ^:private overflow-auto    "auto")
(def ^:private overflow-scroll  "scroll")
(def ^:private overflow-overlay "overlay")

;; ── CSS custom property names ────────────────────────────────────────────────
(def ^:private css-bg           "--x-drag-panel-bg")
(def ^:private css-color        "--x-drag-panel-color")
(def ^:private css-border       "--x-drag-panel-border")
(def ^:private css-radius       "--x-drag-panel-radius")
(def ^:private css-padding      "--x-drag-panel-padding")
(def ^:private css-shadow       "--x-drag-panel-shadow")
(def ^:private css-lift-shadow  "--x-drag-panel-lift-shadow")
(def ^:private css-lift-scale   "--x-drag-panel-lift-scale")
(def ^:private css-handle-bg    "--x-drag-panel-handle-bg")
(def ^:private css-handle-h     "--x-drag-panel-handle-height")
(def ^:private css-grip-color   "--x-drag-panel-grip-color")
(def ^:private css-foot-border  "--x-drag-panel-footprint-border")
(def ^:private css-foot-bg      "--x-drag-panel-footprint-bg")
(def ^:private css-transition   "--x-drag-panel-transition")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "position:relative;"
   "box-sizing:border-box;"
   "border-radius:var(" css-radius ",var(--x-radius-md,8px));}"

   ":host([hidden]){display:none;}"

   ".sr-only{"
   "position:absolute;width:1px;height:1px;padding:0;margin:-1px;"
   "overflow:hidden;clip:rect(0 0 0 0);white-space:nowrap;border:0;}"

   "[part=panel]{"
   "box-sizing:border-box;"
   "display:flex;"
   "flex-direction:column;"
   "overflow:hidden;"
   "background:var(" css-bg ",var(--x-color-surface,#fff));"
   "color:var(" css-color ",var(--x-color-text,#111));"
   "border:var(" css-border ",1px solid var(--x-color-border,#d8d8d8));"
   "border-radius:var(" css-radius ",var(--x-radius-md,8px));"
   "box-shadow:var(" css-shadow ",0 1px 2px rgba(0,0,0,.06));"
   "transition:var(" css-transition ",box-shadow .16s ease,transform .16s ease);}"

   "[part=handle]{"
   "display:flex;"
   "align-items:center;"
   "gap:.5rem;"
   "min-height:var(" css-handle-h ",2rem);"
   "padding:0 var(" css-padding ",.75rem);"
   "background:var(" css-handle-bg ",transparent);"
   "cursor:grab;"
   "touch-action:none;"
   "user-select:none;}"

   "[part=handle]:focus-visible{"
   "outline:2px solid var(--x-color-focus,#3b82f6);"
   "outline-offset:-2px;}"

   "[part=grip]{"
   "flex:none;"
   "width:.75rem;"
   "color:var(" css-grip-color ",var(--x-color-text-muted,#8a8a8a));"
   "line-height:1;}"

   "[part=body]{"
   "padding:var(" css-padding ",.75rem);}"

   ;; Surface grab: the whole panel is the grab area, so the drag-suppression
   ;; that normally sits on the handle has to cover it.
   ":host([grab=surface]) [part=panel]{cursor:grab;touch-action:none;}"

   ;; Lifted. The host keeps its box so the board never reflows mid-drag; only
   ;; the inner surface leaves the flow.
   ":host([data-dragging]) [part=panel]{"
   "z-index:1000;"
   "pointer-events:none;"
   "cursor:grabbing;"
   "box-shadow:var(" css-lift-shadow ",0 12px 32px rgba(0,0,0,.22));"
   "background:var(" css-bg ",var(--x-color-bg,#fff));"
   "transition:none;}"

   ;; Vacated and in-flight both read as an empty dashed footprint on the host.
   ":host([data-dragging]),:host([pending]){"
   "border:var(" css-foot-border ",1px dashed var(--x-color-border,#c9c9c9));"
   "background:var(" css-foot-bg ",transparent);}"

   ":host([pending]) [part=panel]{visibility:hidden;}"

   ":host([disabled]) [part=panel]{opacity:.55;}"
   ":host([disabled]) [part=handle]{cursor:default;}"

   "@media (prefers-reduced-motion: reduce){"
   "[part=panel]{transition:none !important;}}"))

;; ── DOM initialisation ───────────────────────────────────────────────────────
(defn- make-handle! []
  (let [handle (.createElement js/document tag-div)
        grip   (.createElement js/document tag-span)
        slot   (.createElement js/document tag-slot)]
    (du/set-attr! handle attr-part model/part-handle)
    (du/set-attr! handle attr-role role-button)
    (du/set-attr! handle attr-tabindex tabindex-focusable)
    (du/set-attr! grip attr-part model/part-grip)
    (du/set-attr! grip attr-aria-hidden value-true)
    (set! (.-textContent grip) "⠿")
    (du/set-attr! slot attr-name slot-header)
    (.appendChild handle grip)
    (.appendChild handle slot)
    #js {:handle handle}))

(defn- make-body! []
  (let [body (.createElement js/document tag-div)
        slot (.createElement js/document tag-slot)]
    (du/set-attr! body attr-part model/part-body)
    (.appendChild body slot)
    body))

(defn- init-dom! [^js el]
  (let [root    (.attachShadow el #js {:mode "open"})
        style   (.createElement js/document "style")
        panel   (.createElement js/document tag-div)
        header  (make-handle!)
        ^js handle (gobj/get header "handle")
        body    (make-body!)]
    (set! (.-textContent style) style-text)
    (du/set-attr! panel attr-part model/part-panel)
    (.appendChild panel handle)
    (.appendChild panel body)
    (.appendChild root style)
    (.appendChild root panel)
    (du/setv! el k-refs #js {"host"   el
                             "panel"  panel
                             "handle" handle
                             "window" js/window})
    (du/mark-initialized! el k-initialized?)))

(defn- ensure-shadow! [^js el]
  (when-not (du/initialized? el k-initialized?)
    (init-dom! el)))

(defn- ref-of [^js el k]
  (when-let [^js refs (du/getv el k-refs)]
    (gobj/get refs k)))

;; ── Live region (created lazily at pickup) ───────────────────────────────────
;; A board with fifty cards would otherwise carry fifty idle aria-live nodes,
;; only one of which is ever used.
(defn- ensure-live! [^js el]
  (or (du/getv el k-live)
      (let [^js root (.-shadowRoot el)
            live     (.createElement js/document tag-span)]
        (du/set-attr! live attr-aria-live value-polite)
        (du/set-attr! live attr-class class-sr-only)
        (.appendChild root live)
        (du/setv! el k-live live)
        live)))

(defn- announce! [^js el text]
  (let [^js live (ensure-live! el)]
    (set! (.-textContent live) text)))

;; ── Attribute readers ────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:kind-raw          (du/get-attr el model/attr-kind)
    :value-raw         (du/get-attr el model/attr-value)
    :label-raw         (du/get-attr el model/attr-label)
    :grab-raw          (du/get-attr el model/attr-grab)
    :auto-scroll-raw   (du/get-attr el model/attr-auto-scroll)
    :disabled-present? (du/has-attr? el model/attr-disabled)
    :pending-present?  (du/has-attr? el model/attr-pending)}))

(defn- current-model [^js el]
  (or (du/getv el k-model) (read-model el)))

;; ── DOM patching ─────────────────────────────────────────────────────────────
(defn- apply-handle-state! [^js el {:keys [label disabled? pending?]}]
  (let [^js handle (ref-of el rk-handle)]
    (when handle
      (du/set-attr! handle attr-aria-label label)
      ;; A pending panel stays tab-reachable: a -1 footprint would make a
      ;; stalled move undiscoverable by keyboard, which defeats the reason the
      ;; box is preserved at all.
      (du/set-attr! handle attr-tabindex (if disabled? tabindex-off tabindex-focusable))
      (if pending?
        (du/set-attr! handle attr-aria-busy value-true)
        (du/remove-attr! handle attr-aria-busy)))))

(defn- apply-model! [^js el m]
  (ensure-shadow!      el)
  (apply-handle-state! el m)
  (du/setv! el k-model m))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ── Lift geometry ────────────────────────────────────────────────────────────
(defn- set-style! [^js node prop v]
  (.setProperty (.-style node) prop v))

(defn- clear-style! [^js node prop]
  (.removeProperty (.-style node) prop))

(defn- lift! [^js el ^js rect]
  (let [^js panel (ref-of el rk-panel)]
    ;; The host holds the vacated box open so no zone reflows mid-drag.
    (set-style! el style-height (str (.-height rect) px))
    (set-style! panel style-position position-fixed)
    (set-style! panel style-left   (str (.-left rect) px))
    (set-style! panel style-top    (str (.-top rect) px))
    (set-style! panel style-width  (str (.-width rect) px))
    (set-style! panel style-height (str (.-height rect) px))
    (set-style! panel style-margin "0")))

(defn- drop-lift! [^js el]
  (let [^js panel (ref-of el rk-panel)]
    (clear-style! el style-height)
    (doseq [prop [style-position style-left style-top style-width
                  style-height style-margin style-transform]]
      (clear-style! panel prop))))

(defn- place-lifted! [^js el drag]
  (let [^js panel     (ref-of el rk-panel)
        {:keys [dx dy]} (model/drag-delta drag)]
    (set-style! panel style-transform
                (str "translate(" dx px "," dy px ") scale(var("
                     css-lift-scale ",1.02))"))))

;; ── Zone resolution ──────────────────────────────────────────────────────────
(defn- zone-at
  "Topmost x-drop-zone under a viewport point, or nil.

  The lifted surface carries pointer-events:none, so it never occludes the zone
  it is being dragged over."
  [x y]
  (let [^js els (.elementsFromPoint js/document x y)]
    (some (fn zone-ancestor [^js e]
            (when (.-closest e) (.closest e zone-model/tag-name)))
          (array-seq els))))

(defn- owning-zone [^js el]
  (.closest el zone-model/tag-name))

;; ── Auto-scroll ──────────────────────────────────────────────────────────────
(defn- scrollable-on-axis? [^js node vertical?]
  (let [^js cs      (js/getComputedStyle node)
        overflow    (if vertical? (.-overflowY cs) (.-overflowX cs))
        scroll-size (if vertical? (.-scrollHeight node) (.-scrollWidth node))
        client-size (if vertical? (.-clientHeight node) (.-clientWidth node))]
    (and (or (= overflow overflow-auto)
             (= overflow overflow-scroll)
             (= overflow overflow-overlay))
         (> scroll-size (inc client-size)))))

(defn- find-scroller
  "Nearest ancestor of `start` that can actually scroll on one axis, falling
  back to the document scrolling element.

  Resolved per axis because a board scrolls sideways while its columns scroll
  down, and a drag into a corner legitimately needs both at once."
  [^js start vertical?]
  (loop [^js node start]
    (cond
      (nil? node)                         (.-scrollingElement js/document)
      (scrollable-on-axis? node vertical?) node
      :else                               (recur (.-parentElement node)))))

(defn- scrollers-for
  "Resolve and memoise the two scrollers for the element under the pointer.
  getComputedStyle up an ancestor chain is too expensive to redo every frame,
  and the answer only changes when the pointer crosses into a different
  element."
  [^js el ^js hit]
  (let [cached (du/getv el k-scrollers)]
    (if (and cached (identical? hit (gobj/get cached "hit")))
      cached
      (let [resolved #js {"hit"        hit
                          "vertical"   (find-scroller hit true)
                          "horizontal" (find-scroller hit false)}]
        ;; Hot path: rAF-driven — a memo handle with no diagnostic value.
        (du/setv-untraced! el k-scrollers resolved)
        resolved))))

(defn- scroller-bounds [^js node vertical?]
  (if (identical? node (.-scrollingElement js/document))
    (if vertical?
      {:lo 0 :hi (.-innerHeight js/window)}
      {:lo 0 :hi (.-innerWidth js/window)})
    (let [^js rect (.getBoundingClientRect node)]
      (if vertical?
        {:lo (.-top rect)  :hi (.-bottom rect)}
        {:lo (.-left rect) :hi (.-right rect)}))))

(defn- scroll-axis! [^js node pos vertical? dt]
  (when node
    (let [{:keys [lo hi]} (scroller-bounds node vertical?)
          velocity        (model/axis-velocity {:pos pos :lo lo :hi hi})
          delta           (model/scroll-delta velocity dt)]
      (when (not= 0 delta)
        (if vertical?
          (set! (.-scrollTop node)  (+ (.-scrollTop node) delta))
          (set! (.-scrollLeft node) (+ (.-scrollLeft node) delta)))))))

(defn- auto-scroll! [^js el drag dt]
  (when (= model/auto-scroll-auto (:auto-scroll (current-model el)))
    (let [{:keys [pointer-x pointer-y]} drag
          ^js els (.elementsFromPoint js/document pointer-x pointer-y)
          ^js hit (aget els 0)]
      (when hit
        (let [^js s (scrollers-for el hit)]
          (scroll-axis! (gobj/get s "vertical")   pointer-y true  dt)
          (scroll-axis! (gobj/get s "horizontal") pointer-x false dt))))))

;; ── Hover tracking ───────────────────────────────────────────────────────────
(defn- update-hover! [^js el drag]
  (let [{:keys [pointer-x pointer-y]} drag
        ^js next-zone (zone-at pointer-x pointer-y)
        ^js prev-zone (:zone drag)]
    (when-not (identical? prev-zone next-zone)
      (when prev-zone (zone/unhover! prev-zone el))
      (when next-zone (zone/hover! next-zone el)))
    (when next-zone
      (zone/preview! next-zone el pointer-x pointer-y))
    ;; Hot path: rAF-driven — the hovered zone is re-derived every frame.
    (du/setv-untraced! el k-drag (assoc (du/getv el k-drag) :zone next-zone))))

;; ── Drag frame loop ──────────────────────────────────────────────────────────
;; The loop runs continuously while dragging rather than only on pointermove,
;; because auto-scroll must keep scrolling while the pointer is held still.
(defn- frame! [^js el ts]
  (when-let [drag (du/getv el k-drag)]
    (let [last (du/getv el k-last-frame)
          dt   (if last (- ts last) 0)]
      ;; Hot path: rAF-driven — see the animation-bookkeeping convention.
      (du/setv-untraced! el k-last-frame ts)
      (place-lifted! el drag)
      (update-hover! el drag)
      (auto-scroll!  el drag dt)
      (du/setv-untraced!
       el k-raf
       (js/requestAnimationFrame (fn next-drag-frame [t] (frame! el t)))))))

(defn- start-frames! [^js el]
  (du/setv-untraced! el k-last-frame nil)
  (du/setv-untraced!
   el k-raf
   (js/requestAnimationFrame (fn first-drag-frame [t] (frame! el t)))))

(defn- stop-frames! [^js el]
  (when-let [raf (du/getv el k-raf)]
    (js/cancelAnimationFrame raf))
  (du/setv-untraced! el k-raf nil)
  (du/setv-untraced! el k-last-frame nil)
  (du/setv-untraced! el k-scrollers nil))

;; ── Long press ───────────────────────────────────────────────────────────────
(defn- cancel-press! [^js el]
  (when-let [press (du/getv el k-press)]
    (js/clearTimeout (:timer press)))
  (du/setv! el k-press nil))

;; ── Drag lifecycle ───────────────────────────────────────────────────────────
(declare arm-drag!)

(defn- capture-pointer!
  "setPointerCapture throws NotFoundError when the pointer id is no longer
  active. Capture keeps pointermove flowing once the cursor leaves the host, so
  a failure must degrade rather than abort the drag."
  [^js el pointer-id]
  (try (.setPointerCapture el pointer-id)
       (catch :default _ nil)))

(defn- arm-drag! [^js el pointer-id client-x client-y]
  (let [^js rect (.getBoundingClientRect el)
        m        (current-model el)]
    (cancel-press! el)
    (capture-pointer! el pointer-id)
    (du/setv! el k-drag {:pointer-id pointer-id
                         :offset-x   (- client-x (.-left rect))
                         :offset-y   (- client-y (.-top rect))
                         :pointer-x  client-x
                         :pointer-y  client-y
                         :start-x    (.-left rect)
                         :start-y    (.-top rect)
                         :from-zone  (owning-zone el)
                         :zone       nil})
    (lift! el rect)
    (du/set-attr! el attr-data-dragging "")
    (du/dispatch! el model/event-drag-start
                  (model/drag-start-detail (:kind m) (:value m)))
    (announce! el (str "Picked up " (:label m)))
    (start-frames! el)))

(defn- teardown-drag! [^js el]
  (stop-frames! el)
  (drop-lift! el)
  (du/remove-attr! el attr-data-dragging)
  (du/setv! el k-drag nil))

(defn- cancel-drag! [^js el reason]
  (when-let [drag (du/getv el k-drag)]
    (let [m (current-model el)]
      (when-let [^js z (:zone drag)] (zone/unhover! z el))
      (teardown-drag! el)
      (du/dispatch! el model/event-drag-cancel
                    (model/drag-cancel-detail (:kind m) (:value m) reason))
      (announce! el (str "Cancelled moving " (:label m))))))

(defn- complete-drag! [^js el]
  (when-let [drag (du/getv el k-drag)]
    (let [m             (current-model el)
          {:keys [pointer-x pointer-y from-zone]} drag
          ^js target    (zone-at pointer-x pointer-y)
          accepted?     (and target (zone/accepts-panel? target el))]
      (when-let [^js z (:zone drag)] (zone/unhover! z el))
      (teardown-drag! el)
      (if accepted?
        (do (zone/commit-drop! target el from-zone pointer-x pointer-y)
            (announce! el (str "Dropped " (:label m))))
        (do (du/dispatch! el model/event-drag-cancel
                          (model/drag-cancel-detail (:kind m) (:value m)
                                                    model/reason-no-zone))
            (announce! el (str "Returned " (:label m))))))))

;; ── Keyboard drag ────────────────────────────────────────────────────────────
(defn- candidate-zones [^js el]
  (into []
        (filter (fn accepting? [^js z] (zone/accepts-panel? z el)))
        (array-seq (.querySelectorAll js/document zone-model/tag-name))))

(defn- keyboard-hover! [^js el zones index]
  (let [^js z (nth zones index nil)]
    (when z
      (zone/hover! z el)
      (zone/announce-candidate! z el)
      (.scrollIntoView z #js {:block "nearest" :inline "nearest"}))))

(defn- keyboard-pick-up! [^js el]
  (let [m     (current-model el)
        zones (candidate-zones el)]
    (when (seq zones)
      (du/setv! el k-keyboard {:zones zones :index 0})
      (du/set-attr! el attr-data-dragging "")
      (du/dispatch! el model/event-drag-start
                    (model/drag-start-detail (:kind m) (:value m)))
      (announce! el (str "Picked up " (:label m)))
      (keyboard-hover! el zones 0))))

(defn- keyboard-teardown! [^js el]
  (when-let [kb (du/getv el k-keyboard)]
    (when-let [^js z (nth (:zones kb) (:index kb) nil)]
      (zone/unhover! z el)))
  (du/remove-attr! el attr-data-dragging)
  (du/setv! el k-keyboard nil))

(defn- keyboard-step! [^js el delta]
  (when-let [kb (du/getv el k-keyboard)]
    (let [{:keys [zones index]} kb
          next-index (mod (+ index delta) (count zones))]
      (when-let [^js z (nth zones index nil)] (zone/unhover! z el))
      (du/setv! el k-keyboard (assoc kb :index next-index))
      (keyboard-hover! el zones next-index))))

(defn- keyboard-commit! [^js el]
  (when-let [kb (du/getv el k-keyboard)]
    (let [m         (current-model el)
          ^js z     (nth (:zones kb) (:index kb) nil)
          from-zone (owning-zone el)]
      (keyboard-teardown! el)
      (when z
        (zone/commit-keyboard-drop! z el from-zone)
        (announce! el (str "Dropped " (:label m)))))))

(defn- keyboard-cancel! [^js el]
  (when (du/getv el k-keyboard)
    (let [m (current-model el)]
      (keyboard-teardown! el)
      (du/dispatch! el model/event-drag-cancel
                    (model/drag-cancel-detail (:kind m) (:value m)
                                              model/reason-escape))
      (announce! el (str "Cancelled moving " (:label m))))))

;; ── Event handlers (listener-spec style) ─────────────────────────────────────
(defn- no-drag-target?
  "True when the press landed on interactive content rather than the grab
  surface. Slotted buttons and links inside a panel body would otherwise have
  their activation swallowed by the drag, which matters most in surface mode."
  [^js event]
  (let [^js target (.-target event)]
    (boolean (and target (.-closest target) (.closest target selector-no-drag)))))

(defn- on-grab-surface?
  "True when the press is allowed to start a drag for the current grab mode.

  composedPath rather than `.target`, because a listener on the host sees shadow
  children retargeted to the host and slotted content reported as light DOM —
  the path is the only view that spans both."
  [^js el ^js event grab]
  (or (= grab model/grab-surface)
      (let [^js handle (ref-of el rk-handle)]
        (boolean (and handle (.includes (.composedPath event) handle))))))

(defn- on-pointer-down! [^js el ^js event]
  (let [m (current-model el)]
    (when (and (:draggable? m)
               (= primary-button (.-button event))
               (nil? (du/getv el k-drag))
               (on-grab-surface? el event (:grab m))
               (not (no-drag-target? event)))
      (let [pointer-id (.-pointerId event)
            x          (.-clientX event)
            y          (.-clientY event)]
        (.preventDefault event)
        (if (model/long-press-required? {:pointer-type (.-pointerType event)
                                         :grab         (:grab m)})
          (du/setv! el k-press
                    {:pointer-id pointer-id
                     :start-x    x
                     :start-y    y
                     :timer      (js/setTimeout
                                  (fn arm-after-long-press []
                                    (arm-drag! el pointer-id x y))
                                  model/long-press-ms)})
          (arm-drag! el pointer-id x y))))))

(defn- on-pointer-move! [^js el ^js event]
  (let [x (.-clientX event)
        y (.-clientY event)]
    (when-let [press (du/getv el k-press)]
      (when (model/travelled? (assoc press :pointer-x x :pointer-y y))
        (cancel-press! el)))
    (when-let [drag (du/getv el k-drag)]
      (when (= (:pointer-id drag) (.-pointerId event))
        ;; Hot path: pointermove-driven — position is derived, never stored.
        (du/setv-untraced! el k-drag (assoc drag :pointer-x x :pointer-y y))))))

(defn- on-pointer-end! [^js el ^js event]
  (cancel-press! el)
  (when-let [drag (du/getv el k-drag)]
    (when (= (:pointer-id drag) (.-pointerId event))
      (if (= ev-pointercancel (.-type event))
        (cancel-drag! el model/reason-escape)
        (complete-drag! el)))))

(defn- on-handle-keydown! [^js el ^js event]
  (let [k  (.-key event)
        kb (du/getv el k-keyboard)
        m  (current-model el)]
    (cond
      (and (= k key-space) (not kb) (:draggable? m))
      (do (.preventDefault event) (keyboard-pick-up! el))

      (and kb (or (= k key-down) (= k key-right)))
      (do (.preventDefault event) (keyboard-step! el 1))

      (and kb (or (= k key-up) (= k key-left)))
      (do (.preventDefault event) (keyboard-step! el -1))

      (and kb (= k key-enter))
      (do (.preventDefault event) (keyboard-commit! el))

      (and kb (= k key-escape))
      (do (.preventDefault event) (keyboard-cancel! el))

      :else nil)))

(defn- on-window-keydown!
  "Escape aborts a pointer drag. The listener is on window because focus during
  a pointer drag is wherever the user left it, not necessarily on the handle."
  [^js el ^js event]
  (when (and (= key-escape (.-key event)) (du/getv el k-drag))
    (cancel-drag! el model/reason-escape)))

;; ── Listener installation (listener-spec named pattern) ──────────────────────
;; Each entry: [refs-key event-name handler-key capture?]. add-listeners! and
;; remove-listeners! iterate the same spec, so they cannot drift. The window
;; listener outlives the shadow tree, hence the removable shape.
(def ^:private listener-spec
  [[rk-host   ev-pointerdown   hk-pointer-down   false]
   [rk-host   ev-pointermove   hk-pointer-move   false]
   [rk-host   ev-pointerup     hk-pointer-end    false]
   [rk-host   ev-pointercancel hk-pointer-end    false]
   [rk-handle ev-keydown       hk-handle-keydown false]
   [rk-window ev-keydown       hk-window-keydown false]])

(defn- make-handlers [^js el]
  (let [handlers #js {}]
    (gobj/set handlers hk-pointer-down
              (fn handle-pointer-down [^js e] (on-pointer-down! el e)))
    (gobj/set handlers hk-pointer-move
              (fn handle-pointer-move [^js e] (on-pointer-move! el e)))
    (gobj/set handlers hk-pointer-end
              (fn handle-pointer-end [^js e] (on-pointer-end! el e)))
    (gobj/set handlers hk-handle-keydown
              (fn handle-handle-keydown [^js e] (on-handle-keydown! el e)))
    (gobj/set handlers hk-window-keydown
              (fn handle-window-keydown [^js e] (on-window-keydown! el e)))
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

(defn- add-listeners! [^js el]
  (du/setv! el k-handlers (make-handlers el))
  (iter-listeners! el true))

(defn- remove-listeners! [^js el]
  (iter-listeners! el false)
  (du/setv! el k-handlers nil))

;; ── Property accessors (Tier 0 — every property is a plain reflector) ────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

;; ── Lifecycle ────────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-shadow! el)
  (update-from-attrs! el)
  (add-listeners! el))

(defn- disconnected! [^js el]
  (cancel-press! el)
  (stop-frames! el)
  (keyboard-teardown! el)
  (du/setv! el k-drag nil)
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name old-val new-val]
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
