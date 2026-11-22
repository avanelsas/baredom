(ns baredom.components.x-radio.x-radio
  (:require [baredom.utils.component :as component]
            [baredom.utils.forms :as forms]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-radio.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: stored on the host via du/setv! / du/getv
;; so every mutation routes through the x-trace-history recorder hook)
;; ---------------------------------------------------------------------------
(def ^:private k-refs      "__xRadioRefs")
(def ^:private k-model     "__xRadioModel")
(def ^:private k-internals "__xRadioInternals")
(def ^:private k-handlers  "__xRadioHandlers")

;; ---------------------------------------------------------------------------
;; String constants
;; ---------------------------------------------------------------------------
(def ^:private attr-part             "part")
(def ^:private attr-type             "type")
(def ^:private attr-role             "role")
(def ^:private attr-disabled         "disabled")
(def ^:private attr-data-checked     "data-checked")
(def ^:private attr-data-disabled    "data-disabled")
(def ^:private attr-data-labelled    "data-labelled")

(def ^:private part-control "control")
(def ^:private part-dot     "dot")
(def ^:private part-label   "label")

(def ^:private val-button "button")
(def ^:private val-radio  "radio")

(def ^:private ev-click   "click")
(def ^:private ev-keydown "keydown")

(def ^:private key-space       " ")
(def ^:private key-enter       "Enter")
(def ^:private key-arrow-down  "ArrowDown")
(def ^:private key-arrow-right "ArrowRight")
(def ^:private key-arrow-up    "ArrowUp")
(def ^:private key-arrow-left  "ArrowLeft")

(def ^:private rk-control "control")
(def ^:private rk-slot    "slot")

(def ^:private hk-click   "click")
(def ^:private hk-keydown "keydown")

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:inline-flex;"
   "align-items:center;"
   "gap:var(--x-radio-label-gap);"
   "vertical-align:middle;"
   "color-scheme:light dark;"
   "--x-radio-label-gap:var(--x-space-sm,0.5rem);"
   "--x-radio-size:16px;"
   "--x-radio-border-width:2px;"
   "--x-radio-border-color:var(--x-color-border,#6b7280);"
   "--x-radio-bg:var(--x-color-surface,#ffffff);"
   "--x-radio-checked-color:var(--x-color-primary,#2563eb);"
   "--x-radio-dot-size:6px;"
   "--x-radio-focus-ring:var(--x-color-focus-ring,#60a5fa);"
   "--x-radio-disabled-opacity:0.45;"
   "--x-radio-transition:background var(--x-transition-duration,120ms) var(--x-transition-easing,ease),border-color var(--x-transition-duration,120ms) var(--x-transition-easing,ease);"
   "}"
   du/control-font-rule
   du/hidden-rule
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-radio-border-color:var(--x-color-border,#9ca3af);"
   "--x-radio-bg:var(--x-color-surface,#1f2937);"
   "--x-radio-checked-color:var(--x-color-primary,#3b82f6);"
   "--x-radio-focus-ring:var(--x-color-focus-ring,#93c5fd);"
   "}"
   "}"
   "[part=control]{"
   "all:unset;"
   "box-sizing:border-box;"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "width:var(--x-radio-size);"
   "height:var(--x-radio-size);"
   "border:var(--x-radio-border-width) solid var(--x-radio-border-color);"
   "border-radius:50%;"
   "background:var(--x-radio-bg);"
   "transition:var(--x-radio-transition);"
   "cursor:pointer;"
   "flex-shrink:0;"
   "position:relative;"
   "}"
   (forms/label-rules part-label "--x-radio-disabled-opacity")
   "[part=control][disabled]{"
   "cursor:default;"
   "opacity:var(--x-radio-disabled-opacity);"
   "}"
   "[part=control]:focus-visible{"
   "outline:none;"
   "box-shadow:0 0 0 3px var(--x-radio-focus-ring);"
   "}"
   ":host([data-checked]) [part=control]{"
   "border-color:var(--x-radio-checked-color);"
   "}"
   "[part=dot]{"
   "display:block;"
   "width:var(--x-radio-dot-size);"
   "height:var(--x-radio-dot-size);"
   "border-radius:50%;"
   "background:var(--x-radio-checked-color);"
   "opacity:0;"
   "transition:opacity var(--x-transition-duration,100ms) var(--x-transition-easing,ease);"
   "}"
   ":host([data-checked]) [part=dot]{"
   "opacity:1;"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=control]{transition:none;}"
   "[part=dot]{transition:none;}"
   "}"))

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (.createElement js/document "style")
        control-el (.createElement js/document "button")
        dot-el     (.createElement js/document "span")
        {:keys [label slot]} (forms/make-label-section! part-label)]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! control-el attr-part part-control)
    (du/set-attr! control-el attr-type val-button)
    (du/set-attr! control-el attr-role val-radio)
    (du/set-attr! dot-el     attr-part part-dot)

    (.appendChild control-el dot-el)
    (.appendChild root style-el)
    (.appendChild root control-el)
    (.appendChild root label)

    (let [refs #js {}]
      (gobj/set refs rk-control control-el)
      (gobj/set refs rk-slot    slot)
      (du/setv! el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Read element state from attributes
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/normalize
   {:checked-present?     (du/has-attr? el model/attr-checked)
    :disabled-present?    (forms/disabled? el)
    :readonly-present?    (du/has-attr? el model/attr-readonly)
    :required-present?    (du/has-attr? el model/attr-required)
    :name-raw             (du/get-attr el model/attr-name)
    :value-raw            (du/get-attr el model/attr-value)
    :aria-label-raw       (du/get-attr el model/attr-aria-label)
    :aria-describedby-raw (du/get-attr el model/attr-aria-describedby)
    :aria-labelledby-raw  (du/get-attr el model/attr-aria-labelledby)
    :label-slotted?       (some-> (du/getv el k-refs) (gobj/get rk-slot) forms/slot-has-content?)}))

;; ---------------------------------------------------------------------------
;; Apply model + render-pipeline (render-orchestrator)
;; ---------------------------------------------------------------------------
(defn- apply-host-data! [^js el {:keys [checked? disabled? labelled?]}]
  (du/set-bool-attr! el attr-data-checked  checked?)
  (du/set-bool-attr! el attr-data-disabled disabled?)
  (du/set-bool-attr! el attr-data-labelled labelled?))

(defn- apply-form-value! [^js el {:keys [checked? value]}]
  (when-let [^js internals (du/getv el k-internals)]
    (.setFormValue internals (when checked? value))))

(defn- apply-model! [^js el m]
  (when-let [refs (du/getv el k-refs)]
    (let [^js control-el (gobj/get refs rk-control)]
      (du/set-attrs-to! control-el (:control-aria m))
      (du/set-bool-attr! control-el attr-disabled (:disabled? m))
      (apply-host-data!  el m)
      (apply-form-value! el m)
      (du/setv! el k-model m))))

;; render! is the direct-write entry — try-select! / form-* call it after
;; mutating attributes synchronously and want the apply to run unconditionally.
;; attribute-changed! uses update-from-attrs! which gates on a model diff.
(defn- render! [^js el]
  (apply-model! el (read-model el)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= new-m old-m)
      (apply-model! el new-m))))

;; ---------------------------------------------------------------------------
;; Group utilities
;; ---------------------------------------------------------------------------
(defn- group-radios [^js el]
  (let [name-val (du/get-attr el model/attr-name)
        ^js form (when (.-form el) (.-form el))
        scope    (or form js/document)]
    (if (and name-val (not= name-val ""))
      (let [all (.querySelectorAll scope model/tag-name)
            result (array)]
        (.forEach all (fn [^js r]
                        (when (= (du/get-attr r model/attr-name) name-val)
                          (.push result r))))
        result)
      #js [el])))

(defn- sync-tabindexes! [^js el]
  (let [radios  (group-radios el)
        checked (.find radios (fn [^js r] (du/has-attr? r model/attr-checked)))
        first-r (when (pos? (.-length radios)) (aget radios 0))]
    (.forEach radios
              (fn [^js r]
                (when-let [refs (du/getv r k-refs)]
                  (let [^js ctrl (gobj/get refs rk-control)
                        tab      (cond
                                   checked (if (= r checked) 0 -1)
                                   :else   (if (= r first-r) 0 -1))]
                    (du/set-tab-index-to! ctrl tab)))))))

;; ---------------------------------------------------------------------------
;; Selection logic
;; ---------------------------------------------------------------------------
;; try-select! orchestrates a phase list: read model → guard → dispatch the
;; cancelable change-request → if allowed: apply selection, uncheck siblings,
;; sync tabindexes, dispatch the final change event. Each phase is one named
;; helper so the orchestrator stays short and each concern is testable on its
;; own.

(defn- dispatch-change-request!
  "Fire the cancelable change-request and return whether listeners allowed
  the transition to proceed."
  [^js el value]
  (du/dispatch-cancelable!
   el
   model/event-change-request
   #js {:value           value
        :previousChecked false
        :nextChecked     true}))

(defn- apply-selection! [^js el]
  (du/set-attr! el model/attr-checked "")
  (render! el))

(defn- uncheck-siblings! [^js el]
  (let [radios (group-radios el)]
    (.forEach radios
              (fn [^js r]
                (when (and (not= r el) (du/has-attr? r model/attr-checked))
                  (du/remove-attr! r model/attr-checked))))))

(defn- dispatch-change! [^js el value]
  (du/dispatch! el model/event-change
                #js {:value   value
                     :checked true}))

(defn- try-select! [^js el]
  (let [m (read-model el)]
    (when-not (or (:disabled? m) (:readonly? m) (:checked? m))
      (let [value (:value m)]
        (when (dispatch-change-request! el value)
          (apply-selection!  el)
          (uncheck-siblings! el)
          (sync-tabindexes!  el)
          (dispatch-change!  el value))))))

;; ---------------------------------------------------------------------------
;; Keyboard handler
;; ---------------------------------------------------------------------------
(defn- focus-control! [^js r]
  (when-let [refs (du/getv r k-refs)]
    (let [^js ctrl (gobj/get refs rk-control)]
      (.focus ctrl))))

(defn- make-keydown-handler [^js el]
  (fn [^js evt]
    (let [key    (.-key evt)
          radios (group-radios el)
          n      (.-length radios)
          idx    (.indexOf radios el)]
      (cond
        (or (= key key-space) (= key key-enter))
        (do (.preventDefault evt)
            (try-select! el))

        (or (= key key-arrow-down) (= key key-arrow-right))
        (do (.preventDefault evt)
            (when (pos? n)
              (let [next-idx (mod (inc idx) n)
                    ^js next-r (aget radios next-idx)]
                (try-select! next-r)
                (focus-control! next-r))))

        (or (= key key-arrow-up) (= key key-arrow-left))
        (do (.preventDefault evt)
            (when (pos? n)
              (let [prev-idx (mod (dec idx) n)
                    ^js prev-r (aget radios prev-idx)]
                (try-select! prev-r)
                (focus-control! prev-r))))))))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- on-host-click [^js el ^js evt]
  (let [^js control (gobj/get (du/getv el k-refs) rk-control)]
    (when (forms/label-activation? el control evt)
      (forms/focus-quietly! control)
      (try-select! el))))

(defn- add-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js control-el (gobj/get refs rk-control)
          click-h        (fn handle-host-click [evt] (on-host-click el evt))
          keydown-h      (make-keydown-handler el)
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

;; ---------------------------------------------------------------------------
;; Form-associated callbacks
;; ---------------------------------------------------------------------------
(defn- form-reset! [^js el]
  (du/remove-attr! el model/attr-checked)
  (render! el))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el)
    (watch-label-slot! el)
    (when (.-attachInternals el)
      (du/setv! el k-internals (.attachInternals el))))
  (remove-listeners! el)
  (add-listeners! el)
  (render! el)
  (sync-tabindexes! el))

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
  (forms/install-validity-api! proto k-internals)
  (du/install-properties! proto model/property-api))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :moved-fn               sync-tabindexes!
     :attribute-changed-fn   attribute-changed!
     :form-associated?       true
     :form-reset-fn          form-reset!
     :setup-prototype-fn     install-property-accessors!}))
