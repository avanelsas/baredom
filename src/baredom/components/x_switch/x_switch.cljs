(ns baredom.components.x-switch.x-switch
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-switch.model :as model]))

;; ── Instance-field keys ──────────────────────────────────────────────────────
(def ^:private k-refs      "__xSwitchRefs")
(def ^:private k-model     "__xSwitchModel")
(def ^:private k-internals "__xSwitchInternals")
(def ^:private k-handlers  "__xSwitchHandlers")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;"
   "vertical-align:middle;"
   "color-scheme:light dark;"
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
   "transition:background 150ms ease;"
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
   "box-shadow:0 1px 3px rgba(0,0,0,0.25);"
   "transition:transform 150ms ease;"
   "}"

   ":host([data-checked]) [part=thumb]{"
   "transform:translateX(calc(var(--x-switch-track-width) - var(--x-switch-thumb-size) - calc(var(--x-switch-thumb-offset) * 2)));"
   "}"

   "@media (prefers-reduced-motion:reduce){"
   "[part=track]{transition:none;}"
   "[part=thumb]{transition:none;}"
   "}"))

;; ── DOM helpers ──────────────────────────────────────────────────────────────
(defn- make-el [^js tag] (.createElement js/document tag))

;; ── Shadow DOM construction ───────────────────────────────────────────────────
(defn- make-shadow! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (make-el "style")
        control-el (make-el "button")
        track-el   (make-el "span")
        thumb-el   (make-el "span")]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! control-el "part" "control")
    (du/set-attr! control-el "type" "button")
    (du/set-attr! control-el "role" "switch")
    (du/set-attr! track-el   "part" "track")
    (du/set-attr! thumb-el   "part" "thumb")

    (.appendChild track-el thumb-el)
    (.appendChild control-el track-el)
    (.appendChild root style-el)
    (.appendChild root control-el)

    (let [refs #js {:root root :control control-el :track track-el :thumb thumb-el}]
      (du/setv! el k-refs refs)
      refs)))

;; ── Read element state from attributes ───────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:checked-present?      (du/has-attr? el model/attr-checked)
    :disabled-present?     (du/has-attr? el model/attr-disabled)
    :readonly-present?     (du/has-attr? el model/attr-readonly)
    :required-present?     (du/has-attr? el model/attr-required)
    :name-raw              (du/get-attr el model/attr-name)
    :value-raw             (du/get-attr el model/attr-value)
    :aria-label-raw        (du/get-attr el model/attr-aria-label)
    :aria-describedby-raw  (du/get-attr el model/attr-aria-describedby)
    :aria-labelledby-raw   (du/get-attr el model/attr-aria-labelledby)}))

;; ── DOM patching (render-orchestrator: phase list of named helpers) ─────────
(defn- apply-aria! [^js control-el {:keys [aria-checked disabled? required? readonly?
                                          aria-label aria-labelledby aria-describedby]}]
  (du/set-attr! control-el "aria-checked"  aria-checked)
  (du/set-attr! control-el "aria-disabled" (if disabled? "true" "false"))
  (du/set-attr! control-el "aria-required" (if required? "true" "false"))
  (du/set-attr! control-el "aria-readonly" (if readonly? "true" "false"))
  (if-let [v aria-label]
    (du/set-attr! control-el "aria-label" v)
    (du/remove-attr! control-el "aria-label"))
  (if-let [v aria-labelledby]
    (du/set-attr! control-el "aria-labelledby" v)
    (du/remove-attr! control-el "aria-labelledby"))
  (if-let [v aria-describedby]
    (du/set-attr! control-el "aria-describedby" v)
    (du/remove-attr! control-el "aria-describedby")))

(defn- apply-disabled! [^js control-el {:keys [disabled?]}]
  (set! (.-tabIndex control-el) (if disabled? -1 0))
  (if disabled?
    (du/set-attr! control-el "disabled" "")
    (du/remove-attr! control-el "disabled")))

(defn- apply-host-data! [^js el {:keys [checked? disabled?]}]
  (du/set-bool-attr! el "data-checked"  checked?)
  (du/set-bool-attr! el "data-disabled" disabled?))

(defn- apply-form-value! [^js el {:keys [checked? value]}]
  (when-let [^js internals (du/getv el k-internals)]
    (.setFormValue internals (when checked? value))))

(defn- apply-model! [^js el ^js refs m]
  (let [^js control-el (gobj/get refs "control")]
    (apply-aria!       control-el m)
    (apply-disabled!   control-el m)
    (apply-host-data!  el m)
    (apply-form-value! el m)
    (du/setv! el k-model m)))

;; render! is the direct-write entry — try-toggle!/form-disabled!/form-reset!
;; mutate attributes synchronously and want the apply to run unconditionally.
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

;; ── External label association ────────────────────────────────────────────────
(defn- wire-external-label! [^js el]
  (let [id (du/get-attr el "id")]
    (when id
      (let [^js lbl (.querySelector js/document (str "label[for='" id "']"))]
        (when lbl
          (let [lid (or (du/get-attr lbl "id")
                        (let [gen-id (str "x-sw-lbl-" id)]
                          (du/set-attr! lbl "id" gen-id)
                          gen-id))]
            (when-let [refs (du/getv el k-refs)]
              (du/set-attr! (gobj/get refs "control") "aria-labelledby" lid))))))))

;; ── Listener management ───────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js control-el (gobj/get refs "control")
          click-h   (fn handle-control-click   [^js _e] (try-toggle! el))
          keydown-h (fn handle-control-keydown [^js e]
                      (let [key (.-key e)]
                        (when (or (= key " ") (= key "Enter"))
                          (.preventDefault e)
                          (try-toggle! el))))
          handlers  #js {:click click-h :keydown keydown-h}]
      (.addEventListener control-el "click"   click-h)
      (.addEventListener control-el "keydown" keydown-h)
      (du/setv! el k-handlers handlers))))

(defn- remove-listeners! [^js el]
  (when-let [refs     (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (let [^js control-el (gobj/get refs "control")]
        (.removeEventListener control-el "click"   (gobj/get handlers "click"))
        (.removeEventListener control-el "keydown" (gobj/get handlers "keydown")))
      (du/setv! el k-handlers nil))))

;; ── Form-associated callbacks ─────────────────────────────────────────────────
(defn- form-disabled! [^js el disabled?]
  (du/set-bool-attr! el model/attr-disabled (boolean disabled?))
  (render! el))

(defn- form-reset! [^js el]
  (du/remove-attr! el model/attr-checked)
  (render! el))

;; ── Lifecycle ────────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el)
    (when (.-attachInternals el)
      (du/setv! el k-internals (.attachInternals el))))
  (remove-listeners! el)
  (add-listeners! el)
  (wire-external-label! el)
  (render! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Property helpers ──────────────────────────────────────────────────────────
;; ── Element class and registration ───────────────────────────────────────────

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
