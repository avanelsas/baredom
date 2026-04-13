(ns baredom.components.x-slider.x-slider
  (:require [goog.object :as gobj]
            [baredom.components.x-slider.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: access via gobj/get / gobj/set)
;; ---------------------------------------------------------------------------
(def ^:private k-refs      "__xSliderRefs")
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
(defn- make-el [^js tag] (.createElement js/document tag))

(defn- set-attr! [^js el attr val]
  (.setAttribute el attr val))

(defn- remove-attr! [^js el attr]
  (.removeAttribute el attr))

(defn- has-attr? [^js el attr]
  (.hasAttribute el attr))

(defn- get-attr [^js el attr]
  (.getAttribute el attr))

(defn- set-bool-attr! [^js el attr value]
  (if value (set-attr! el attr "") (remove-attr! el attr)))

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (make-el "style")
        base-el    (make-el "div")
        header-el  (make-el "div")
        label-el   (make-el "span")
        value-el   (make-el "span")
        input-el   (make-el "input")]

    (set! (.-textContent style-el) style-text)

    (set-attr! base-el   "part" "base")
    (set-attr! header-el "part" "header")
    (set-attr! label-el  "part" "label-text")
    (set-attr! value-el  "part" "value-text")
    (set-attr! input-el  "part" "input")
    (set-attr! input-el  "type" "range")

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
      (gobj/set el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Read element state from attributes
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/derive-state
   {:value            (get-attr el model/attr-value)
    :min              (get-attr el model/attr-min)
    :max              (get-attr el model/attr-max)
    :step             (get-attr el model/attr-step)
    :disabled         (has-attr? el model/attr-disabled)
    :readonly         (has-attr? el model/attr-readonly)
    :name             (get-attr el model/attr-name)
    :label            (get-attr el model/attr-label)
    :show-value       (has-attr? el model/attr-show-value)
    :size             (get-attr el model/attr-size)
    :aria-label       (get-attr el model/attr-aria-label)
    :aria-labelledby  (get-attr el model/attr-aria-labelledby)
    :aria-describedby (get-attr el model/attr-aria-describedby)}))

;; ---------------------------------------------------------------------------
;; Render
;; ---------------------------------------------------------------------------
(defn- render! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js base-el    (gobj/get refs "base")
          ^js header-el  (gobj/get refs "header")
          ^js label-el   (gobj/get refs "label")
          ^js value-el   (gobj/get refs "value")
          ^js input-el   (gobj/get refs "input")
          m              (read-model el)
          {:keys [value min max step size
                  disabled? readonly?
                  label show-value? fill-percent
                  aria-label aria-labelledby aria-describedby]} m
          show-header?   (or (some? label) show-value?)]

      ;; Data attributes on base for CSS size hooks
      (set-attr! base-el "data-size" size)

      ;; Data attributes on host for CSS state hooks
      (set-bool-attr! el "data-disabled" disabled?)
      (set-bool-attr! el "data-readonly" readonly?)

      ;; Header visibility
      (set! (.-display (.-style header-el))
            (if show-header? "flex" "none"))

      ;; Label text
      (set! (.-textContent label-el) (or label ""))
      (set! (.-display (.-style label-el))
            (if (some? label) "" "none"))

      ;; Value text
      (set! (.-textContent value-el)
            (if show-value? (str value) ""))
      (set! (.-display (.-style value-el))
            (if show-value? "" "none"))

      ;; Fill CSS custom property on host — cascades into shadow DOM
      (.setProperty (.-style el) "--_x-slider-fill"
                    (str (.toFixed fill-percent 2) "%"))

      ;; Sync native input attributes (always safe to set these)
      (set-attr! input-el "min"  (str min))
      (set-attr! input-el "max"  (str max))
      (set-attr! input-el "step" step)

      ;; Anti-drag-interruption guard: only update .value when it differs.
      ;; Avoids snapping the thumb back during an active pointer drag.
      (when (not= (.-value input-el) (str value))
        (set! (.-value input-el) (str value)))

      ;; ARIA lives on [part=input], not the host (host has no role)
      (set-attr! input-el "aria-valuemin" (str min))
      (set-attr! input-el "aria-valuemax" (str max))
      (set-attr! input-el "aria-valuenow" (str value))
      (set-attr! input-el "aria-readonly" (if readonly? "true" "false"))

      (if-let [v aria-label]
        (set-attr! input-el "aria-label" v)
        ;; Fall back to label text as aria-label when no explicit aria-label
        (if (some? label)
          (set-attr! input-el "aria-label" label)
          (remove-attr! input-el "aria-label")))

      (if-let [v aria-labelledby]
        (set-attr! input-el "aria-labelledby" v)
        (remove-attr! input-el "aria-labelledby"))

      (if-let [v aria-describedby]
        (set-attr! input-el "aria-describedby" v)
        (remove-attr! input-el "aria-describedby"))

      ;; Disabled on native input
      (if disabled?
        (set-attr! input-el "disabled" "")
        (remove-attr! input-el "disabled"))

      ;; Form value via ElementInternals
      (when-let [^js internals (gobj/get el k-internals)]
        (.setFormValue internals (str value))))))

;; ---------------------------------------------------------------------------
;; Event dispatch
;; ---------------------------------------------------------------------------
(defn- dispatch! [^js el event-name detail]
  (.dispatchEvent
   el
   (js/CustomEvent.
    event-name
    #js {:detail     detail
         :bubbles    true
         :composed   true
         :cancelable false})))

;; ---------------------------------------------------------------------------
;; Event handlers
;; ---------------------------------------------------------------------------
(defn- make-input-handler [^js el]
  (fn [^js _evt]
    (when-not (has-attr? el model/attr-disabled)
      (let [^js refs     (gobj/get el k-refs)
            ^js input-el (gobj/get refs "input")
            raw-val      (.-value input-el)
            num-val      (js/parseFloat raw-val)
            min-num      (js/parseFloat (or (get-attr el model/attr-min) "0"))
            max-num      (js/parseFloat (or (get-attr el model/attr-max) "100"))]
        ;; Write new value back to host attribute (triggers attribute-changed! → render!)
        (set-attr! el model/attr-value raw-val)
        (dispatch! el model/event-input
                   #js {:value num-val
                        :min   min-num
                        :max   max-num})))))

(defn- make-change-handler [^js el]
  (fn [^js _evt]
    (when-not (has-attr? el model/attr-disabled)
      (let [^js refs     (gobj/get el k-refs)
            ^js input-el (gobj/get refs "input")
            raw-val      (.-value input-el)
            num-val      (js/parseFloat raw-val)
            min-num      (js/parseFloat (or (get-attr el model/attr-min) "0"))
            max-num      (js/parseFloat (or (get-attr el model/attr-max) "100"))]
        (dispatch! el model/event-change
                   #js {:value num-val
                        :min   min-num
                        :max   max-num})))))

(defn- make-keydown-handler [^js el]
  (fn [^js evt]
    (when (and (has-attr? el model/attr-readonly)
               (contains? value-changing-keys (.-key evt)))
      (.preventDefault evt))))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- add-listeners! [^js el]
  (when-let [refs (gobj/get el k-refs)]
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
      (gobj/set el k-handlers handlers))))

(defn- remove-listeners! [^js el]
  (when-let [refs     (gobj/get el k-refs)]
    (when-let [handlers (gobj/get el k-handlers)]
      (let [^js input-el (gobj/get refs "input")]
        (.removeEventListener input-el "input"   (gobj/get handlers "input"))
        (.removeEventListener input-el "change"  (gobj/get handlers "change"))
        (.removeEventListener input-el "keydown" (gobj/get handlers "keydown")))
      (gobj/set el k-handlers nil))))

;; ---------------------------------------------------------------------------
;; Form-associated callbacks
;; ---------------------------------------------------------------------------
(defn- form-disabled! [^js el disabled?]
  (set-bool-attr! el model/attr-disabled (boolean disabled?))
  (render! el))

(defn- form-reset! [^js el]
  ;; Revert to default value (0)
  (set-attr! el model/attr-value (str model/default-value))
  (render! el))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (when-not (gobj/get el k-refs)
    (make-shadow! el)
    (when (.-attachInternals el)
      (gobj/set el k-internals (.attachInternals el))))
  (remove-listeners! el)
  (add-listeners! el)
  (render! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name _old _new]
  (render! el))

;; ---------------------------------------------------------------------------
;; Property helpers
;; ---------------------------------------------------------------------------
(defn- define-bool-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (has-attr? this attr-name)))
        :set (fn [v] (this-as ^js this (set-bool-attr! this attr-name (boolean v))))}))

(defn- define-string-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (or (get-attr this attr-name) nil)))
        :set (fn [v] (this-as ^js this
                              (if (and (some? v) (not= v js/undefined))
                                (set-attr! this attr-name (str v))
                                (remove-attr! this attr-name))))}))

;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------
(defn- element-class []
  (let [cls   (js* "(class extends HTMLElement {})")
        proto (.-prototype cls)]

    ;; Form-associated
    (set! (.-formAssociated cls) true)

    ;; observedAttributes — via defineProperty getter (more robust under Closure Advanced)
    (.defineProperty js/Object cls "observedAttributes"
                     #js {:get (fn [] model/observed-attributes)})

    ;; Boolean properties
    (define-bool-prop!   proto "disabled"  model/attr-disabled)
    (define-bool-prop!   proto "readOnly"  model/attr-readonly)
    (define-bool-prop!   proto "showValue" model/attr-show-value)

    ;; String properties
    (define-string-prop! proto "value" model/attr-value)
    (define-string-prop! proto "min"   model/attr-min)
    (define-string-prop! proto "max"   model/attr-max)
    (define-string-prop! proto "step"  model/attr-step)
    (define-string-prop! proto "name"  model/attr-name)
    (define-string-prop! proto "label" model/attr-label)
    (define-string-prop! proto "size"  model/attr-size)

    ;; Lifecycle callbacks
    (aset proto "connectedCallback"
          (fn [] (this-as ^js this (connected! this))))

    (aset proto "disconnectedCallback"
          (fn [] (this-as ^js this (disconnected! this))))

    (aset proto "attributeChangedCallback"
          (fn [n o v] (this-as ^js this (attribute-changed! this n o v))))

    ;; Form-associated callbacks
    (aset proto "formDisabledCallback"
          (fn [d] (this-as ^js this (form-disabled! this d))))

    (aset proto "formResetCallback"
          (fn [] (this-as ^js this (form-reset! this))))

    cls))

(defn init! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class))))
