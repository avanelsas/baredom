(ns baredom.components.x-slider.x-slider
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-slider.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: access via gobj/get / gobj/set)
;; ---------------------------------------------------------------------------
(def ^:private k-refs      "__xSliderRefs")
(def ^:private k-model     "__xSliderModel")
(def ^:private k-internals "__xSliderInternals")
(def ^:private k-handlers  "__xSliderHandlers")

;; Keys that change the slider value — block only these in readonly mode
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
   "--x-slider-track-color:rgba(0,0,0,0.15);"
   "--x-slider-fill-color:var(--x-color-primary,#3b82f6);"
   "--x-slider-thumb-color:var(--x-color-surface,#ffffff);"
   "--x-slider-thumb-border:2px solid var(--x-color-primary,#3b82f6);"
   "--x-slider-thumb-shadow:0 1px 4px rgba(0,0,0,0.20);"
   "--x-slider-focus-ring:var(--x-color-focus-ring,#60a5fa);"
   "--x-slider-disabled-opacity:0.45;"
   "--x-slider-label-color:rgba(0,0,0,0.60);"
   "--x-slider-value-color:rgba(0,0,0,0.50);"
   "--x-slider-radius:var(--x-radius-full,9999px);"
   "}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-slider-track-color:rgba(255,255,255,0.15);"
   "--x-slider-fill-color:var(--x-color-primary,#3b82f6);"
   "--x-slider-thumb-color:var(--x-color-surface,#ffffff);"
   "--x-slider-thumb-border:2px solid var(--x-color-primary,#3b82f6);"
   "--x-slider-focus-ring:var(--x-color-focus-ring,#93c5fd);"
   "--x-slider-label-color:rgba(255,255,255,0.60);"
   "--x-slider-value-color:rgba(255,255,255,0.50);"
   "}}"

   "[part=base]{"
   "width:100%;"
   "box-sizing:border-box;}"

   ;; Size variants — set internal CSS vars on base; children inherit them
   "[part=base][data-size=sm]{--_x-slider-track-h:4px;--_x-slider-thumb-sz:14px;}"
   "[part=base][data-size=md]{--_x-slider-track-h:6px;--_x-slider-thumb-sz:18px;}"
   "[part=base][data-size=lg]{--_x-slider-track-h:8px;--_x-slider-thumb-sz:22px;}"

   "[part=header]{"
   "display:flex;"
   "justify-content:space-between;"
   "align-items:baseline;"
   "margin-bottom:4px;}"

   "[part=label-text]{"
   "font-size:0.875rem;"
   "color:var(--x-slider-label-color);"
   "font-weight:500;}"

   "[part=value-text]{"
   "font-size:0.8125rem;"
   "color:var(--x-slider-value-color);}"

   ;; Native input reset
   "[part=input]{"
   "-webkit-appearance:none;"
   "appearance:none;"
   "display:block;"
   "width:100%;"
   "height:var(--_x-slider-thumb-sz,18px);"
   "cursor:pointer;"
   "margin:0;"
   "padding:0;"
   "background:transparent;"
   "outline:none;"
   "box-sizing:border-box;}"

   ;; WebKit runnable track — linear-gradient creates the fill effect.
   ;; --_x-slider-fill is set on the host via el.style.setProperty and cascades in.
   "[part=input]::-webkit-slider-runnable-track{"
   "height:var(--_x-slider-track-h,6px);"
   "border-radius:var(--x-slider-radius);"
   "background:linear-gradient("
   "to right,"
   "var(--x-slider-fill-color) var(--_x-slider-fill,0%),"
   "var(--x-slider-track-color) var(--_x-slider-fill,0%)"
   ");}"

   ;; Firefox track (no gradient; moz-range-progress handles fill natively)
   "[part=input]::-moz-range-track{"
   "height:var(--_x-slider-track-h,6px);"
   "border-radius:var(--x-slider-radius);"
   "background:var(--x-slider-track-color);"
   "border:none;}"

   ;; Firefox fill — separate rule; never mix -moz- and -webkit- selectors
   "[part=input]::-moz-range-progress{"
   "height:var(--_x-slider-track-h,6px);"
   "border-radius:var(--x-slider-radius);"
   "background:var(--x-slider-fill-color);}"

   ;; WebKit thumb — separate rule block from Firefox thumb
   "[part=input]::-webkit-slider-thumb{"
   "-webkit-appearance:none;"
   "width:var(--_x-slider-thumb-sz,18px);"
   "height:var(--_x-slider-thumb-sz,18px);"
   "border-radius:50%;"
   "background:var(--x-slider-thumb-color);"
   "border:var(--x-slider-thumb-border);"
   "box-shadow:var(--x-slider-thumb-shadow);"
   "margin-top:calc(var(--_x-slider-track-h,6px)/2 - var(--_x-slider-thumb-sz,18px)/2);"
   "cursor:pointer;"
   "transition:box-shadow 100ms ease;}"

   ;; Firefox thumb — separate rule block
   "[part=input]::-moz-range-thumb{"
   "width:var(--_x-slider-thumb-sz,18px);"
   "height:var(--_x-slider-thumb-sz,18px);"
   "border-radius:50%;"
   "background:var(--x-slider-thumb-color);"
   "border:var(--x-slider-thumb-border);"
   "box-shadow:var(--x-slider-thumb-shadow);"
   "cursor:pointer;"
   "transition:box-shadow 100ms ease;}"

   ;; WebKit focus ring — must be its own rule block
   "[part=input]:focus-visible::-webkit-slider-thumb{"
   "box-shadow:var(--x-slider-thumb-shadow),0 0 0 3px var(--x-slider-focus-ring);}"

   ;; Firefox focus ring — must be its own rule block
   "[part=input]:focus-visible::-moz-range-thumb{"
   "box-shadow:var(--x-slider-thumb-shadow),0 0 0 3px var(--x-slider-focus-ring);}"

   ;; Disabled
   ":host([data-disabled]) [part=input]{"
   "opacity:var(--x-slider-disabled-opacity);"
   "cursor:default;"
   "pointer-events:none;}"

   ;; Readonly — pointer-events off; keyboard blocked via keydown handler
   ":host([data-readonly]) [part=input]{"
   "pointer-events:none;}"

   ;; Reduced motion
   "@media (prefers-reduced-motion:reduce){"
   "[part=input]::-webkit-slider-thumb{transition:none;}"
   "[part=input]::-moz-range-thumb{transition:none;}"
   "}"

   ;; Larger touch targets on coarse-pointer devices
   "@media (pointer:coarse){"
   "[part=base]{--_x-slider-thumb-sz:28px;}"
   "}"))

;; ---------------------------------------------------------------------------
;; DOM helpers
;; ---------------------------------------------------------------------------

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (.createElement js/document "style")
        base-el    (.createElement js/document "div")
        header-el  (.createElement js/document "div")
        label-el   (.createElement js/document "span")
        value-el   (.createElement js/document "span")
        input-el   (.createElement js/document "input")]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! base-el   "part" "base")
    (du/set-attr! header-el "part" "header")
    (du/set-attr! label-el  "part" "label-text")
    (du/set-attr! value-el  "part" "value-text")
    (du/set-attr! input-el  "part" "input")
    (du/set-attr! input-el  "type" "range")

    (.appendChild header-el label-el)
    (.appendChild header-el value-el)
    (.appendChild base-el   header-el)
    (.appendChild base-el   input-el)
    (.appendChild root      style-el)
    (.appendChild root      base-el)

    (let [refs #js {:base   base-el
                    :header header-el
                    :label  label-el
                    :value  value-el
                    :input  input-el}]
      (du/setv! el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Read element state from attributes
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/derive-state
   {:value            (du/get-attr el model/attr-value)
    :min              (du/get-attr el model/attr-min)
    :max              (du/get-attr el model/attr-max)
    :step             (du/get-attr el model/attr-step)
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
;; Render
;; ---------------------------------------------------------------------------
;; ── DOM patching (render-orchestrator: phase list of named helpers) ─────
(defn- apply-host-data! [^js el ^js base-el {:keys [size disabled? readonly?]}]
  (du/set-attr! base-el "data-size" size)
  (du/set-bool-attr! el "data-disabled" disabled?)
  (du/set-bool-attr! el "data-readonly" readonly?))

(defn- apply-header! [^js header-el ^js label-el ^js value-el
                      {:keys [label value show-value?]}]
  (let [show-header? (or (some? label) show-value?)]
    (set! (.-display (.-style header-el)) (if show-header? "flex" "none"))
    (set! (.-textContent label-el) (or label ""))
    (set! (.-display (.-style label-el)) (if (some? label) "" "none"))
    (set! (.-textContent value-el) (if show-value? (str value) ""))
    (set! (.-display (.-style value-el)) (if show-value? "" "none"))))

(defn- apply-fill! [^js el {:keys [fill-percent]}]
  ;; Fill CSS custom property on host — cascades into shadow DOM
  (.setProperty (.-style el) "--_x-slider-fill"
                (str (.toFixed fill-percent 2) "%")))

(defn- apply-input-value! [^js input-el {:keys [value min max step]}]
  (du/set-attr! input-el "min"  (str min))
  (du/set-attr! input-el "max"  (str max))
  (du/set-attr! input-el "step" step)
  ;; Anti-drag-interruption guard: only update .value when it differs.
  ;; Avoids snapping the thumb back during an active pointer drag.
  (when (not= (.-value input-el) (str value))
    (set! (.-value input-el) (str value))))

(defn- apply-input-aria! [^js input-el {:keys [value min max readonly? disabled?
                                              label aria-label aria-labelledby aria-describedby]}]
  (du/set-attr! input-el "aria-valuemin" (str min))
  (du/set-attr! input-el "aria-valuemax" (str max))
  (du/set-attr! input-el "aria-valuenow" (str value))
  (du/set-attr! input-el "aria-readonly" (if readonly? "true" "false"))

  (if-let [v aria-label]
    (du/set-attr! input-el "aria-label" v)
    ;; Fall back to label text as aria-label when no explicit aria-label
    (if (some? label)
      (du/set-attr! input-el "aria-label" label)
      (du/remove-attr! input-el "aria-label")))

  (if-let [v aria-labelledby]
    (du/set-attr! input-el "aria-labelledby" v)
    (du/remove-attr! input-el "aria-labelledby"))

  (if-let [v aria-describedby]
    (du/set-attr! input-el "aria-describedby" v)
    (du/remove-attr! input-el "aria-describedby"))

  (if disabled?
    (du/set-attr! input-el "disabled" "")
    (du/remove-attr! input-el "disabled")))

(defn- apply-form-value! [^js el {:keys [value]}]
  (when-let [^js internals (du/getv el k-internals)]
    (.setFormValue internals (str value))))

(defn- apply-model! [^js el ^js refs m]
  (let [^js base-el   (gobj/get refs "base")
        ^js header-el (gobj/get refs "header")
        ^js label-el  (gobj/get refs "label")
        ^js value-el  (gobj/get refs "value")
        ^js input-el  (gobj/get refs "input")]
    (apply-host-data!   el base-el m)
    (apply-header!      header-el label-el value-el m)
    (apply-fill!        el m)
    (apply-input-value! input-el m)
    (apply-input-aria!  input-el m)
    (apply-form-value!  el m)
    (du/setv! el k-model m)))

;; render! is the direct-write entry — form-disabled!/form-reset! mutate
;; attributes synchronously and want the apply to run unconditionally.
;; attribute-changed! uses update-from-attrs! which gates on a model diff.
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
;; Event dispatch
;; ---------------------------------------------------------------------------
;; ---------------------------------------------------------------------------
;; Event handlers
;; ---------------------------------------------------------------------------
(defn- make-input-handler [^js el]
  (fn [^js _evt]
    (when-not (du/has-attr? el model/attr-disabled)
      (let [^js refs     (du/getv el k-refs)
            ^js input-el (gobj/get refs "input")
            raw-val      (.-value input-el)
            num-val      (js/parseFloat raw-val)
            prev-val     (js/parseFloat (or (du/get-attr el model/attr-value) "0"))
            min-num      (js/parseFloat (or (du/get-attr el model/attr-min) "0"))
            max-num      (js/parseFloat (or (du/get-attr el model/attr-max) "100"))
            allowed?     (du/dispatch-cancelable!
                          el model/event-change-request
                          #js {:value         num-val
                               :previousValue prev-val
                               :min           min-num
                               :max           max-num})]
        (if allowed?
          (do
            (du/set-attr! el model/attr-value raw-val)
            (du/dispatch! el model/event-input
                       #js {:value num-val
                            :min   min-num
                            :max   max-num}))
          ;; Revert the native input to the current attribute value
          (set! (.-value input-el) (or (du/get-attr el model/attr-value) "0")))))))

(defn- make-change-handler [^js el]
  (fn [^js _evt]
    (when-not (du/has-attr? el model/attr-disabled)
      (let [^js refs     (du/getv el k-refs)
            ^js input-el (gobj/get refs "input")
            raw-val      (.-value input-el)
            num-val      (js/parseFloat raw-val)
            min-num      (js/parseFloat (or (du/get-attr el model/attr-min) "0"))
            max-num      (js/parseFloat (or (du/get-attr el model/attr-max) "100"))]
        (du/dispatch! el model/event-change
                   #js {:value num-val
                        :min   min-num
                        :max   max-num})))))

(defn- make-keydown-handler [^js el]
  (fn [^js evt]
    (when (and (du/has-attr? el model/attr-readonly)
               (contains? value-changing-keys (.-key evt)))
      (.preventDefault evt))))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- add-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js input-el (gobj/get refs "input")
          input-h      (make-input-handler el)
          change-h     (make-change-handler el)
          keydown-h    (make-keydown-handler el)
          handlers     #js {:input   input-h
                             :change  change-h
                             :keydown keydown-h}]
      (.addEventListener input-el "input"   input-h)
      (.addEventListener input-el "change"  change-h)
      (.addEventListener input-el "keydown" keydown-h)
      (du/setv! el k-handlers handlers))))

(defn- remove-listeners! [^js el]
  (when-let [refs     (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (let [^js input-el (gobj/get refs "input")]
        (.removeEventListener input-el "input"   (gobj/get handlers "input"))
        (.removeEventListener input-el "change"  (gobj/get handlers "change"))
        (.removeEventListener input-el "keydown" (gobj/get handlers "keydown")))
      (du/setv! el k-handlers nil))))

;; ---------------------------------------------------------------------------
;; Form-associated callbacks
;; ---------------------------------------------------------------------------
(defn- form-disabled! [^js el disabled?]
  (du/set-bool-attr! el model/attr-disabled (boolean disabled?))
  (render! el))

(defn- form-reset! [^js el]
  ;; Revert to default value (0)
  (du/set-attr! el model/attr-value (str model/default-value))
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
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ---------------------------------------------------------------------------
;; Property helpers
;; ---------------------------------------------------------------------------
;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------

(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :form-associated?       true
     :form-disabled-fn       form-disabled!
     :form-reset-fn          form-reset!
     :setup-prototype-fn     install-property-accessors!}))
