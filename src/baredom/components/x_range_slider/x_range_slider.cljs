(ns baredom.components.x-range-slider.x-range-slider
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-range-slider.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: host-element access via du/getv / du/setv!)
;; ---------------------------------------------------------------------------
(def ^:private k-refs        "__xRangeSliderRefs")
(def ^:private k-model       "__xRangeSliderModel")
(def ^:private k-internals   "__xRangeSliderInternals")
(def ^:private k-handlers    "__xRangeSliderHandlers")
(def ^:private k-dragging    "__xRangeSliderDragging")
(def ^:private k-drag-origin "__xRangeSliderDragOrigin")

;; Keys that change a thumb value — blocked (but still consumed) in readonly mode
(def ^:private value-changing-keys
  #{"ArrowUp" "ArrowDown" "ArrowLeft" "ArrowRight"
    "Home" "End" "PageUp" "PageDown"})

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-range-slider-track-color:rgba(0,0,0,0.15);"
   "--x-range-slider-fill-color:var(--x-color-primary,#3b82f6);"
   "--x-range-slider-thumb-color:var(--x-color-surface,#ffffff);"
   "--x-range-slider-thumb-border:2px solid var(--x-color-primary,#3b82f6);"
   "--x-range-slider-thumb-shadow:0 1px 4px rgba(0,0,0,0.20);"
   "--x-range-slider-focus-ring:var(--x-color-focus-ring,#60a5fa);"
   "--x-range-slider-disabled-opacity:0.45;"
   "--x-range-slider-label-color:rgba(0,0,0,0.60);"
   "--x-range-slider-value-color:rgba(0,0,0,0.50);"
   "--x-range-slider-radius:var(--x-radius-full,9999px);"
   "}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-range-slider-track-color:rgba(255,255,255,0.15);"
   "--x-range-slider-focus-ring:var(--x-color-focus-ring,#93c5fd);"
   "--x-range-slider-label-color:rgba(255,255,255,0.60);"
   "--x-range-slider-value-color:rgba(255,255,255,0.50);"
   "}}"

   "[part=base]{"
   "width:100%;"
   "box-sizing:border-box;"
   "--_x-range-slider-track-h:6px;"
   "--_x-range-slider-thumb-sz:18px;"
   "}"

   ;; Size variants — set internal CSS vars on base; descendants inherit them.
   "[part=base][data-size=sm]{--_x-range-slider-track-h:4px;--_x-range-slider-thumb-sz:14px;}"
   "[part=base][data-size=md]{--_x-range-slider-track-h:6px;--_x-range-slider-thumb-sz:18px;}"
   "[part=base][data-size=lg]{--_x-range-slider-track-h:8px;--_x-range-slider-thumb-sz:22px;}"

   "[part=header]{"
   "display:flex;"
   "justify-content:space-between;"
   "align-items:baseline;"
   "margin-bottom:4px;}"

   "[part=label-text]{"
   "font-size:0.875rem;"
   "color:var(--x-range-slider-label-color);"
   "font-weight:500;}"

   "[part=value-text]{"
   "font-size:0.8125rem;"
   "color:var(--x-range-slider-value-color);"
   "font-variant-numeric:tabular-nums;}"

   ;; Track — the interactive rail region, sized tall enough to hold the thumbs.
   "[part=track]{"
   "position:relative;"
   "width:100%;"
   "height:var(--_x-range-slider-thumb-sz,18px);"
   "box-sizing:border-box;"
   "cursor:pointer;"
   "touch-action:none;}"

   ;; The unfilled rail.
   "[part=track]::before{"
   "content:'';"
   "position:absolute;"
   "left:0;right:0;"
   "top:50%;"
   "transform:translateY(-50%);"
   "height:var(--_x-range-slider-track-h,6px);"
   "border-radius:var(--x-range-slider-radius);"
   "background:var(--x-range-slider-track-color);}"

   ;; The highlighted segment between the two thumbs.
   "[part=track-fill]{"
   "position:absolute;"
   "top:50%;"
   "transform:translateY(-50%);"
   "height:var(--_x-range-slider-track-h,6px);"
   "border-radius:var(--x-range-slider-radius);"
   "background:var(--x-range-slider-fill-color);"
   "pointer-events:none;"
   "left:var(--_x-range-slider-fill-left,0%);"
   "width:var(--_x-range-slider-fill-width,100%);}"

   ;; Thumbs.
   "[part=thumb-start],[part=thumb-end]{"
   "position:absolute;"
   "top:50%;"
   "width:var(--_x-range-slider-thumb-sz,18px);"
   "height:var(--_x-range-slider-thumb-sz,18px);"
   "border-radius:50%;"
   "background:var(--x-range-slider-thumb-color);"
   "border:var(--x-range-slider-thumb-border);"
   "box-shadow:var(--x-range-slider-thumb-shadow);"
   "box-sizing:border-box;"
   "transform:translate(-50%,-50%);"
   "cursor:grab;"
   "z-index:1;"
   "transition:box-shadow 100ms ease;}"
   "[part=thumb-start]{left:var(--_x-range-slider-start-pct,0%);}"
   "[part=thumb-end]{left:var(--_x-range-slider-end-pct,100%);}"
   "[part=thumb-start][data-active],[part=thumb-end][data-active]{z-index:2;}"

   "[part=thumb-start]:focus-visible,[part=thumb-end]:focus-visible{"
   "outline:none;"
   "box-shadow:var(--x-range-slider-thumb-shadow),"
   "0 0 0 3px var(--x-range-slider-focus-ring);}"

   ;; Disabled — no interaction.
   ":host([data-disabled]) [part=track]{"
   "opacity:var(--x-range-slider-disabled-opacity);"
   "cursor:default;"
   "pointer-events:none;}"

   ;; Readonly — pointer interaction off; keyboard blocked via keydown handler.
   ":host([data-readonly]) [part=track]{"
   "pointer-events:none;}"

   ;; Reduced motion.
   "@media (prefers-reduced-motion:reduce){"
   "[part=thumb-start],[part=thumb-end]{transition:none;}}"

   ;; Coarse pointer — larger touch targets.
   "@media (pointer:coarse){"
   "[part=base]{--_x-range-slider-thumb-sz:28px;}}"))

;; ---------------------------------------------------------------------------
;; Shadow DOM construction (shadow-builders)
;; ---------------------------------------------------------------------------
(defn- make-header-section! []
  (let [header (.createElement js/document "div")
        label  (.createElement js/document "span")
        value  (.createElement js/document "span")]
    (du/set-attr! header "part" "header")
    (du/set-attr! label  "part" "label-text")
    (du/set-attr! value  "part" "value-text")
    (.appendChild header label)
    (.appendChild header value)
    {:header header :label label :value value}))

(defn- decorate-thumb!
  "Apply the shared ARIA-slider decoration to one draggable thumb."
  [^js thumb part-name aria-label]
  (du/set-attr! thumb "part" part-name)
  (du/set-attr! thumb "role" "slider")
  (du/set-attr! thumb "tabindex" "0")
  (du/set-attr! thumb "aria-label" aria-label)
  (du/set-attr! thumb "aria-orientation" "horizontal"))

(defn- make-track-section! []
  (let [track   (.createElement js/document "div")
        fill    (.createElement js/document "div")
        thumb-s (.createElement js/document "div")
        thumb-e (.createElement js/document "div")]
    (du/set-attr! track "part" "track")
    (du/set-attr! fill  "part" "track-fill")
    (decorate-thumb! thumb-s "thumb-start" "Range minimum")
    (decorate-thumb! thumb-e "thumb-end"   "Range maximum")
    (.appendChild track fill)
    (.appendChild track thumb-s)
    (.appendChild track thumb-e)
    {:track track :track-fill fill :thumb-start thumb-s :thumb-end thumb-e}))

(defn- make-shadow! [^js el]
  (let [root     (.attachShadow el #js {:mode "open"})
        style-el (.createElement js/document "style")
        base     (.createElement js/document "div")
        header   (make-header-section!)
        track    (make-track-section!)]
    (set! (.-textContent style-el) style-text)
    (du/set-attr! base "part" "base")
    (du/set-attr! base "role" "group")
    (.appendChild base (:header header))
    (.appendChild base (:track  track))
    (.appendChild root style-el)
    (.appendChild root base)
    (let [refs #js {:base        base
                    :header      (:header header)
                    :label       (:label header)
                    :value       (:value header)
                    :track       (:track track)
                    :track-fill  (:track-fill track)
                    :thumb-start (:thumb-start track)
                    :thumb-end   (:thumb-end track)}]
      (du/setv! el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Read element state from attributes
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/normalize
   {:start            (du/get-attr el model/attr-start)
    :end              (du/get-attr el model/attr-end)
    :min              (du/get-attr el model/attr-min)
    :max              (du/get-attr el model/attr-max)
    :step             (du/get-attr el model/attr-step)
    :min-gap          (du/get-attr el model/attr-min-gap)
    :disabled         (du/has-attr? el model/attr-disabled)
    :readonly         (du/has-attr? el model/attr-readonly)
    :name             (du/get-attr el model/attr-name)
    :label            (du/get-attr el model/attr-label)
    :show-value       (du/has-attr? el model/attr-show-value)
    :size             (du/get-attr el model/attr-size)
    :aria-label       (du/get-attr el model/attr-aria-label)
    :aria-labelledby  (du/get-attr el model/attr-aria-labelledby)
    :aria-describedby (du/get-attr el model/attr-aria-describedby)}))

;; ---------------------------------------------------------------------------
;; Render — DOM patching (render-orchestrator: phase list of named helpers)
;; ---------------------------------------------------------------------------
(defn- apply-host-data! [^js el ^js base-el {:keys [size disabled? readonly?]}]
  (du/set-attr! base-el "data-size" size)
  (du/set-bool-attr! el "data-disabled" disabled?)
  (du/set-bool-attr! el "data-readonly" readonly?))

(defn- apply-header! [^js header-el ^js label-el ^js value-el
                      {:keys [label start end show-value?]}]
  (let [show-header? (or (some? label) show-value?)]
    (set! (.-display (.-style header-el)) (if show-header? "flex" "none"))
    (set! (.-textContent label-el) (or label ""))
    (set! (.-display (.-style label-el)) (if (some? label) "" "none"))
    (set! (.-textContent value-el) (if show-value? (model/value-text start end) ""))
    (set! (.-display (.-style value-el)) (if show-value? "" "none"))))

(defn- apply-track-geometry!
  "Project the thumb positions and fill segment onto internal CSS vars set on
   base — they cascade into the shadow track. The model-change guard already
   skips redundant calls, so this is also the anti-drag-interruption path."
  [^js base-el {:keys [start-pct end-pct fill-left fill-width]}]
  (let [^js style (.-style base-el)]
    (.setProperty style "--_x-range-slider-start-pct"  (str (.toFixed start-pct 2) "%"))
    (.setProperty style "--_x-range-slider-end-pct"    (str (.toFixed end-pct 2) "%"))
    (.setProperty style "--_x-range-slider-fill-left"  (str (.toFixed fill-left 2) "%"))
    (.setProperty style "--_x-range-slider-fill-width" (str (.toFixed fill-width 2) "%"))))

(defn- apply-one-thumb-aria!
  [^js thumb valuemin valuemax valuenow {:keys [disabled? readonly?]}]
  (du/set-attr! thumb "aria-valuemin" (str valuemin))
  (du/set-attr! thumb "aria-valuemax" (str valuemax))
  (du/set-attr! thumb "aria-valuenow" (str valuenow))
  (du/set-attr! thumb "aria-readonly" (if readonly? "true" "false"))
  (if disabled?
    (do (du/set-attr! thumb "aria-disabled" "true")
        (du/set-attr! thumb "tabindex" "-1"))
    (do (du/remove-attr! thumb "aria-disabled")
        (du/set-attr! thumb "tabindex" "0"))))

(defn- apply-thumb-aria!
  "Decorate both thumbs. Each thumb advertises its *effective* movable range:
   the start thumb cannot pass `end - min-gap`, the end thumb cannot pass
   `start + min-gap`."
  [^js refs {:keys [start end min max min-gap aria-label label] :as m}]
  (let [^js thumb-s (gobj/get refs "thumb-start")
        ^js thumb-e (gobj/get refs "thumb-end")
        base-name   (or aria-label label "Range")]
    (du/set-attr! thumb-s "aria-label" (str base-name " minimum"))
    (du/set-attr! thumb-e "aria-label" (str base-name " maximum"))
    (apply-one-thumb-aria! thumb-s min (- end min-gap) start m)
    (apply-one-thumb-aria! thumb-e (+ start min-gap) max end m)))

(defn- apply-group-aria! [^js base-el {:keys [aria-label aria-labelledby aria-describedby]}]
  (if aria-label
    (du/set-attr! base-el "aria-label" aria-label)
    (du/remove-attr! base-el "aria-label"))
  (if aria-labelledby
    (du/set-attr! base-el "aria-labelledby" aria-labelledby)
    (du/remove-attr! base-el "aria-labelledby"))
  (if aria-describedby
    (du/set-attr! base-el "aria-describedby" aria-describedby)
    (du/remove-attr! base-el "aria-describedby")))

(defn- apply-form-value! [^js el {:keys [start end]}]
  (when-let [^js internals (du/getv el k-internals)]
    (.setFormValue internals (str start "," end))))

(defn- apply-model! [^js el ^js refs m]
  (let [^js base-el  (gobj/get refs "base")
        ^js header   (gobj/get refs "header")
        ^js label-el (gobj/get refs "label")
        ^js value-el (gobj/get refs "value")]
    (apply-host-data!      el base-el m)
    (apply-header!         header label-el value-el m)
    (apply-track-geometry! base-el m)
    (apply-thumb-aria!     refs m)
    (apply-group-aria!     base-el m)
    (apply-form-value!     el m)
    (du/setv! el k-model m)))

;; render! is the direct-write entry — form-disabled!/form-reset! mutate
;; attributes synchronously and want the apply to run unconditionally.
(defn- render! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (apply-model! el refs (read-model el))))

(defn- update-from-attrs! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [new-m (read-model el)
          old-m (du/getv el k-model)]
      (when (not= new-m old-m)
        (apply-model! el refs new-m)))))

;; ---------------------------------------------------------------------------
;; Value commit — shared by pointer drag, track click and keyboard
;; ---------------------------------------------------------------------------
(defn- dispatch-committed!
  "Dispatch input (and, when `final?`, change) from a fresh model read so the
   detail reflects the committed, re-normalized state rather than a pre-write
   clamp result."
  [^js el final?]
  (let [m2     (read-model el)
        detail (clj->js (model/make-detail (:start m2) (:end m2)
                                           (:min m2) (:max m2)))]
    (du/dispatch! el model/event-input detail)
    (when final?
      (du/dispatch! el model/event-change detail))))

(defn- commit-thumb!
  "Snap, clamp and (if accepted) write a proposed value for one thumb. The
   attribute write re-enters the change-guarded render pipeline, so the thumb
   DOM follows the attribute — the attribute is the single source of truth."
  [^js el which raw-val final?]
  (let [m        (read-model el)
        step-num (model/step-size (:step m))
        snapped  (model/snap-to-step raw-val (:min m) step-num)
        prev-s   (:start m)
        prev-e   (:end m)
        new-s    (if (= which :start)
                   (model/clamp-start snapped (:min m) (:end m) (:min-gap m))
                   prev-s)
        new-e    (if (= which :end)
                   (model/clamp-end snapped (:start m) (:max m) (:min-gap m))
                   prev-e)]
    (when (or (not= new-s prev-s) (not= new-e prev-e))
      (when (du/dispatch-cancelable!
             el model/event-change-request
             (clj->js (model/make-change-request-detail
                       new-s new-e prev-s prev-e (:min m) (:max m))))
        (when (not= new-s prev-s) (du/set-attr! el model/attr-start (str new-s)))
        (when (not= new-e prev-e) (du/set-attr! el model/attr-end   (str new-e)))
        (dispatch-committed! el final?)))))

;; ---------------------------------------------------------------------------
;; Pointer interaction
;; ---------------------------------------------------------------------------
(defn- capture-pointer!
  "Set pointer capture, tolerating the throw from synthetic test PointerEvents
   that carry no active pointer."
  [^js target ^js evt]
  (try
    (.setPointerCapture target (.-pointerId evt))
    (catch :default _ nil)))

(defn- pointer->value [^js track-el ^js evt min-val max-val]
  (let [^js rect (.getBoundingClientRect track-el)]
    (model/client-x->value (.-clientX evt) (.-left rect) (.-width rect)
                            min-val max-val)))

(defn- raise-thumb!
  "Mark `which` thumb active so CSS raises it above its sibling — keeps the
   last-grabbed thumb on top when the two coincide."
  [^js refs which]
  (du/set-bool-attr! (gobj/get refs "thumb-start") "data-active" (= which :start))
  (du/set-bool-attr! (gobj/get refs "thumb-end")   "data-active" (= which :end)))

(defn- resolve-grab
  "Pure: decide which thumb a track pointerdown grabs and whether it is an
   in-place grab (a direct grab of a distinct thumb) or a jump-to-pointer."
  [m on-start? on-end? clicked-val]
  (let [coincident? (= (:start m) (:end m))]
    (cond
      (and on-start? (not coincident?)) {:which :start :in-place? true}
      (and on-end?   (not coincident?)) {:which :end   :in-place? true}
      :else {:which     (model/nearest-thumb clicked-val (:start m) (:end m))
             :in-place? false})))

(defn- on-track-pointerdown! [^js el ^js evt]
  (let [m (read-model el)]
    (when (model/interactable? m)
      (.preventDefault evt)
      (let [refs       (du/getv el k-refs)
            ^js track  (gobj/get refs "track")
            ^js target (.-target evt)
            clicked    (pointer->value track evt (:min m) (:max m))
            on-start?  (.contains (gobj/get refs "thumb-start") target)
            on-end?    (.contains (gobj/get refs "thumb-end") target)
            {:keys [which in-place?]} (resolve-grab m on-start? on-end? clicked)
            ^js thumb  (gobj/get refs (if (= which :start) "thumb-start" "thumb-end"))]
        (capture-pointer! thumb evt)
        (du/setv! el k-dragging (name which))
        ;; Snapshot the range so pointerup can tell whether the drag moved it.
        (du/setv! el k-drag-origin [(:start m) (:end m)])
        (raise-thumb! refs which)
        (.focus thumb)
        (when-not in-place?
          (commit-thumb! el which clicked false))))))

(defn- on-thumb-pointermove! [^js el which ^js evt]
  (when (= (name which) (du/getv el k-dragging))
    (let [m         (read-model el)
          ^js track (gobj/get (du/getv el k-refs) "track")]
      (commit-thumb! el which (pointer->value track evt (:min m) (:max m)) false))))

;; Handles both pointerup and pointercancel. `change` fires only when the
;; range actually moved during the drag — a bare thumb click, or a cancel
;; before any movement, commits nothing and stays silent.
(defn- on-thumb-pointerup! [^js el which ^js _evt]
  (when (= (name which) (du/getv el k-dragging))
    (du/setv! el k-dragging nil)
    (let [m (read-model el)]
      (when (not= (du/getv el k-drag-origin) [(:start m) (:end m)])
        (du/dispatch! el model/event-change
                      (clj->js (model/make-detail (:start m) (:end m)
                                                  (:min m) (:max m))))))))

;; ---------------------------------------------------------------------------
;; Keyboard interaction
;; ---------------------------------------------------------------------------
(defn- on-thumb-keydown! [^js el which ^js evt]
  (let [key (.-key evt)]
    (when (contains? value-changing-keys key)
      ;; Always consume the key — in readonly mode this blocks the change
      ;; without scrolling the page; otherwise it drives the value.
      (.preventDefault evt)
      (let [m (read-model el)]
        (when (model/interactable? m)
          (let [step-num (or (model/step-size (:step m)) 1)
                cur      (if (= which :start) (:start m) (:end m))
                target   (model/key-target key (.-shiftKey evt) cur
                                            (:min m) (:max m) step-num)]
            (commit-thumb! el which target true)))))))

;; ---------------------------------------------------------------------------
;; Listener management (listener-spec)
;; ---------------------------------------------------------------------------
(defn- build-all-handlers
  "Per-instance closure map; keys map 1:1 onto `listener-spec`."
  [^js el]
  #js {:track-down (fn [e] (on-track-pointerdown! el e))
       :start-move (fn [e] (on-thumb-pointermove! el :start e))
       :start-up   (fn [e] (on-thumb-pointerup!   el :start e))
       :start-key  (fn [e] (on-thumb-keydown!     el :start e))
       :end-move   (fn [e] (on-thumb-pointermove! el :end e))
       :end-up     (fn [e] (on-thumb-pointerup!   el :end e))
       :end-key    (fn [e] (on-thumb-keydown!     el :end e))})

(def ^:private listener-spec
  "Single source of truth for listener wiring: [refs-key event handler-key
   capture?]. add-listeners! and remove-listeners! both iterate it so they
   cannot drift. Pointer capture on the grabbed thumb routes move/up to that
   thumb, so the track only needs `pointerdown`. `pointercancel` shares the
   `pointerup` handler so an interrupted drag still clears the dragging state."
  [["track"       "pointerdown"   "track-down" false]
   ["thumb-start" "pointermove"   "start-move" false]
   ["thumb-start" "pointerup"     "start-up"   false]
   ["thumb-start" "pointercancel" "start-up"   false]
   ["thumb-start" "keydown"       "start-key"  false]
   ["thumb-end"   "pointermove"   "end-move"   false]
   ["thumb-end"   "pointerup"     "end-up"     false]
   ["thumb-end"   "pointercancel" "end-up"     false]
   ["thumb-end"   "keydown"       "end-key"    false]])

(defn- add-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [handlers (build-all-handlers el)]
      (doseq [[refs-key event handler-key capture?] listener-spec]
        (.addEventListener (gobj/get refs refs-key)
                           event
                           (gobj/get handlers handler-key)
                           capture?))
      (du/setv! el k-handlers handlers))))

(defn- remove-listeners! [^js el]
  (let [refs     (du/getv el k-refs)
        handlers (du/getv el k-handlers)]
    (when (and refs handlers)
      (doseq [[refs-key event handler-key capture?] listener-spec]
        (.removeEventListener (gobj/get refs refs-key)
                              event
                              (gobj/get handlers handler-key)
                              capture?))
      (du/setv! el k-handlers nil))))

;; ---------------------------------------------------------------------------
;; Form-associated callbacks
;; ---------------------------------------------------------------------------
(defn- form-disabled! [^js el disabled?]
  (du/set-bool-attr! el model/attr-disabled (boolean disabled?))
  (render! el))

(defn- form-reset! [^js el]
  ;; Reset to the widest range — start falls back to min, end to max.
  (du/remove-attr! el model/attr-start)
  (du/remove-attr! el model/attr-end)
  (render! el))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el)
    (when (.-attachInternals el)
      (du/setv! el k-internals (.attachInternals el))))
  (remove-listeners! el)
  (add-listeners! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (remove-listeners! el)
  ;; Drop any in-flight drag state so it cannot resume after a reconnect.
  (du/setv! el k-dragging nil))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes  model/observed-attributes
     :connected-fn         connected!
     :disconnected-fn      disconnected!
     :attribute-changed-fn attribute-changed!
     :form-associated?     true
     :form-disabled-fn     form-disabled!
     :form-reset-fn        form-reset!
     :setup-prototype-fn   install-property-accessors!}))
