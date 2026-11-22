(ns baredom.components.x-switch.x-switch
  (:require [baredom.utils.component :as component]
            [baredom.utils.forms :as forms]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-switch.model :as model]))

;; ── Instance-field keys ──────────────────────────────────────────────────────
(def ^:private k-refs      "__xSwitchRefs")
(def ^:private k-model     "__xSwitchModel")
(def ^:private k-internals "__xSwitchInternals")
(def ^:private k-handlers  "__xSwitchHandlers")

;; ── String constants ─────────────────────────────────────────────────────────
(def ^:private attr-part             "part")
(def ^:private attr-type             "type")
(def ^:private attr-role             "role")
(def ^:private attr-disabled         "disabled")
(def ^:private attr-data-checked     "data-checked")
(def ^:private attr-data-disabled    "data-disabled")
(def ^:private attr-data-labelled    "data-labelled")

(def ^:private part-control "control")
(def ^:private part-track   "track")
(def ^:private part-thumb   "thumb")
(def ^:private part-label   "label")

(def ^:private val-button "button")
(def ^:private val-switch "switch")

(def ^:private ev-click   "click")
(def ^:private ev-keydown "keydown")

(def ^:private key-space " ")
(def ^:private key-enter "Enter")

(def ^:private rk-control "control")
(def ^:private rk-slot    "slot")

(def ^:private hk-click   "click")
(def ^:private hk-keydown "keydown")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:inline-flex;"
   "align-items:center;"
   "gap:var(--x-switch-label-gap);"
   "vertical-align:middle;"
   "color-scheme:light dark;"
   "--x-switch-label-gap:var(--x-space-sm,0.5rem);"
   "--x-switch-track-width:44px;"
   "--x-switch-track-height:24px;"
   "--x-switch-thumb-size:18px;"
   "--x-switch-thumb-offset:3px;"
   "--x-switch-track-radius:var(--x-radius-full,999px);"
   "--x-switch-track-bg:var(--x-color-border,#d1d5db);"
   "--x-switch-track-bg-checked:var(--x-color-primary,#2563eb);"
   "--x-switch-thumb-bg:var(--x-color-bg,#ffffff);"
   "--x-switch-focus-ring:var(--x-color-focus-ring,#60a5fa);"
   "--x-switch-disabled-opacity:0.45;"
   "--x-switch-transition:background var(--x-transition-duration,150ms) var(--x-transition-easing,ease),transform var(--x-transition-duration,150ms) var(--x-transition-easing,ease);"
   "}"
   du/control-font-rule
   du/hidden-rule

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-switch-track-bg:var(--x-color-border,#4b5563);"
   "--x-switch-track-bg-checked:var(--x-color-primary,#3b82f6);"
   "--x-switch-thumb-bg:var(--x-color-bg,#f9fafb);"
   "--x-switch-focus-ring:var(--x-color-focus-ring,#93c5fd);"
   "}"
   "}"

   "[part=control]{"
   "all:unset;"
   "box-sizing:border-box;"
   "display:inline-flex;"
   "align-items:center;"
   "padding:0;"
   "cursor:pointer;"
   "}"

   (forms/label-rules part-label "--x-switch-disabled-opacity")

   "[part=control][disabled]{"
   "cursor:default;"
   "opacity:var(--x-switch-disabled-opacity);"
   "}"

   "[part=control]:focus-visible{"
   "outline:none;"
   "box-shadow:0 0 0 3px var(--x-switch-focus-ring);"
   "border-radius:calc(var(--x-switch-track-radius) + 2px);"
   "}"

   "[part=track]{"
   "position:relative;"
   "display:inline-block;"
   "width:var(--x-switch-track-width);"
   "height:var(--x-switch-track-height);"
   "border-radius:var(--x-switch-track-radius);"
   "background:var(--x-switch-track-bg);"
   "transition:background var(--x-transition-duration,150ms) var(--x-transition-easing,ease);"
   "flex-shrink:0;"
   "}"

   ":host([data-checked]) [part=track]{"
   "background:var(--x-switch-track-bg-checked);"
   "}"

   "[part=thumb]{"
   "position:absolute;"
   "top:var(--x-switch-thumb-offset);"
   "left:var(--x-switch-thumb-offset);"
   "width:var(--x-switch-thumb-size);"
   "height:var(--x-switch-thumb-size);"
   "border-radius:50%;"
   "background:var(--x-switch-thumb-bg);"
   "box-shadow:var(--x-shadow-sm,0 1px 3px rgba(0,0,0,0.25));"
   "transition:transform var(--x-transition-duration,150ms) var(--x-transition-easing,ease);"
   "}"

   ":host([data-checked]) [part=thumb]{"
   "transform:translateX(calc(var(--x-switch-track-width) - var(--x-switch-thumb-size) - calc(var(--x-switch-thumb-offset) * 2)));"
   "}"

   "@media (prefers-reduced-motion:reduce){"
   "[part=track]{transition:none;}"
   "[part=thumb]{transition:none;}"
   "}"))

;; ── Shadow DOM construction ───────────────────────────────────────────────────
(defn- make-shadow! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (.createElement js/document "style")
        control-el (.createElement js/document "button")
        track-el   (.createElement js/document "span")
        thumb-el   (.createElement js/document "span")
        {:keys [label slot]} (forms/make-label-section! part-label)]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! control-el attr-part part-control)
    (du/set-attr! control-el attr-type val-button)
    (du/set-attr! control-el attr-role val-switch)
    (du/set-attr! track-el   attr-part part-track)
    (du/set-attr! thumb-el   attr-part part-thumb)

    (.appendChild track-el thumb-el)
    (.appendChild control-el track-el)
    (.appendChild root style-el)
    (.appendChild root control-el)
    (.appendChild root label)

    (let [refs #js {}]
      (gobj/set refs rk-control control-el)
      (gobj/set refs rk-slot    slot)
      (du/setv! el k-refs refs)
      refs)))

;; ── Read element state from attributes ───────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:checked-present?      (du/has-attr? el model/attr-checked)
    :disabled-present?     (forms/disabled? el)
    :readonly-present?     (du/has-attr? el model/attr-readonly)
    :required-present?     (du/has-attr? el model/attr-required)
    :name-raw              (du/get-attr el model/attr-name)
    :value-raw             (du/get-attr el model/attr-value)
    :aria-label-raw        (du/get-attr el model/attr-aria-label)
    :aria-describedby-raw  (du/get-attr el model/attr-aria-describedby)
    :aria-labelledby-raw   (du/get-attr el model/attr-aria-labelledby)
    :label-slotted?        (some-> (du/getv el k-refs) (gobj/get rk-slot) forms/slot-has-content?)}))

;; ── DOM patching (render-orchestrator: phase list of named helpers) ─────────
(defn- apply-disabled! [^js control-el {:keys [disabled?]}]
  (du/set-tab-index-to! control-el (if disabled? -1 0))
  (du/set-bool-attr! control-el attr-disabled disabled?))

(defn- apply-host-data! [^js el {:keys [checked? disabled? labelled?]}]
  (du/set-bool-attr! el attr-data-checked  checked?)
  (du/set-bool-attr! el attr-data-disabled disabled?)
  (du/set-bool-attr! el attr-data-labelled labelled?))

(defn- apply-form-value! [^js el {:keys [checked? value]}]
  (when-let [^js internals (du/getv el k-internals)]
    (.setFormValue internals (when checked? value))))

(defn- apply-model! [^js el ^js refs m]
  (let [^js control-el (gobj/get refs rk-control)]
    (du/set-attrs-to! control-el (:control-aria m))
    (apply-disabled!   control-el m)
    (apply-host-data!  el m)
    (apply-form-value! el m)
    (du/setv! el k-model m)))

;; render! is the direct-write entry. try-toggle! and form-reset! mutate
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

;; ── Toggle logic ─────────────────────────────────────────────────────────────
(defn- try-toggle! [^js el]
  (let [m         (read-model el)
        disabled? (:disabled? m)
        readonly? (:readonly? m)]
    (when-not (or disabled? readonly?)
      (let [checked?     (:checked? m)
            next-checked (not checked?)
            value        (:value m)
            allowed?     (du/dispatch-cancelable!
                          el
                          model/event-change-request
                          #js {:value           value
                               :previousChecked  checked?
                               :nextChecked      next-checked})]
        (when allowed?
          (du/set-bool-attr! el model/attr-checked next-checked)
          (render! el)
          (du/dispatch! el model/event-change
                     #js {:value   value
                          :checked next-checked}))))))

;; ── Event handlers ───────────────────────────────────────────────────────────
(defn- on-host-click [^js el ^js evt]
  (let [^js control (gobj/get (du/getv el k-refs) rk-control)]
    (when (forms/label-activation? el control evt)
      (forms/focus-quietly! control)
      (try-toggle! el))))

(defn- on-control-keydown [^js el ^js evt]
  (let [k (.-key evt)]
    (when (or (= k key-space) (= k key-enter))
      (.preventDefault evt)
      (try-toggle! el))))

;; ── Listener management ───────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js control-el (gobj/get refs rk-control)
          click-h        (fn handle-host-click      [evt] (on-host-click      el evt))
          keydown-h      (fn handle-control-keydown [evt] (on-control-keydown el evt))
          handlers       #js {}]
      (.addEventListener el         ev-click   click-h)
      (.addEventListener control-el ev-keydown keydown-h)
      (gobj/set handlers hk-click   click-h)
      (gobj/set handlers hk-keydown keydown-h)
      (du/setv! el k-handlers handlers))))

(defn- remove-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (let [^js control-el (gobj/get refs rk-control)]
        (.removeEventListener el         ev-click   (gobj/get handlers hk-click))
        (.removeEventListener control-el ev-keydown (gobj/get handlers hk-keydown)))
      (du/setv! el k-handlers nil))))

(defn- watch-label-slot!
  "Re-renders when the slotted label changes. Call once, after the shadow root exists."
  [^js el]
  (forms/watch-label-slot! (gobj/get (du/getv el k-refs) rk-slot)
                           (fn label-changed [] (update-from-attrs! el))))

;; ── Form-associated callbacks ─────────────────────────────────────────────────
(defn- form-reset! [^js el]
  (du/remove-attr! el model/attr-checked)
  (render! el))

;; ── Lifecycle ────────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el)
    (watch-label-slot! el)
    (when (.-attachInternals el)
      (du/setv! el k-internals (.attachInternals el))))
  (remove-listeners! el)
  (add-listeners! el)
  (render! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Element class and registration ───────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (forms/install-validity-api! proto k-internals)
  (du/install-properties! proto model/property-api))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :form-associated?       true
     :form-reset-fn          form-reset!
     :setup-prototype-fn     install-property-accessors!}))
