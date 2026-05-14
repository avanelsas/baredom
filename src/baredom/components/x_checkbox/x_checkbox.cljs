(ns baredom.components.x-checkbox.x-checkbox
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.utils.model :as mu]
            [baredom.components.x-checkbox.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: access via gobj/get / gobj/set)
;; ---------------------------------------------------------------------------
(def ^:private k-refs      "__xCheckboxRefs")
(def ^:private k-model     "__xCheckboxModel")
(def ^:private k-internals "__xCheckboxInternals")
(def ^:private k-handlers  "__xCheckboxHandlers")

;; ── String-literal constants ──────────────────────────────────────────────
(def ^:private attr-part              "part")
(def ^:private attr-type              "type")
(def ^:private attr-role              "role")
(def ^:private attr-id                "id")
(def ^:private attr-disabled          "disabled")
(def ^:private attr-aria-label        "aria-label")
(def ^:private attr-aria-labelledby   "aria-labelledby")
(def ^:private attr-aria-describedby  "aria-describedby")
(def ^:private attr-aria-checked      "aria-checked")
(def ^:private attr-aria-disabled     "aria-disabled")
(def ^:private attr-aria-required     "aria-required")
(def ^:private attr-aria-readonly     "aria-readonly")
(def ^:private attr-data-checked      "data-checked")
(def ^:private attr-data-indeterminate "data-indeterminate")
(def ^:private attr-data-disabled     "data-disabled")

(def ^:private part-control "control")
(def ^:private part-box     "box")
(def ^:private part-check   "check")

(def ^:private val-button   "button")
(def ^:private val-checkbox "checkbox")
(def ^:private val-true     "true")
(def ^:private val-false    "false")

(def ^:private ev-click   "click")
(def ^:private ev-keydown "keydown")

(def ^:private hk-click   "click")
(def ^:private hk-keydown "keydown")

(def ^:private key-space  " ")
(def ^:private key-enter  "Enter")

(def ^:private rk-control "control")
(def ^:private rk-box     "box")
(def ^:private rk-check   "check")

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;"
   "vertical-align:middle;"
   "color-scheme:light dark;"
   "--x-checkbox-size:16px;"
   "--x-checkbox-border-width:2px;"
   "--x-checkbox-border-color:var(--x-color-border,#6b7280);"
   "--x-checkbox-border-radius:4px;"
   "--x-checkbox-bg:var(--x-color-surface,#ffffff);"
   "--x-checkbox-bg-checked:var(--x-color-primary,#2563eb);"
   "--x-checkbox-bg-indeterminate:var(--x-color-primary,#2563eb);"
   "--x-checkbox-border-checked:var(--x-color-primary,#2563eb);"
   "--x-checkbox-border-indeterminate:var(--x-color-primary,#2563eb);"
   "--x-checkbox-check-color:var(--x-color-surface,#ffffff);"
   "--x-checkbox-focus-ring:var(--x-color-focus-ring,#60a5fa);"
   "--x-checkbox-disabled-opacity:0.45;"
   "--x-checkbox-transition:background var(--x-transition-duration,120ms) var(--x-transition-easing,ease),border-color var(--x-transition-duration,120ms) var(--x-transition-easing,ease);"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-checkbox-border-color:var(--x-color-border,#9ca3af);"
   "--x-checkbox-bg:var(--x-color-surface,#1f2937);"
   "--x-checkbox-bg-checked:var(--x-color-primary,#3b82f6);"
   "--x-checkbox-bg-indeterminate:var(--x-color-primary,#3b82f6);"
   "--x-checkbox-border-checked:var(--x-color-primary,#3b82f6);"
   "--x-checkbox-border-indeterminate:var(--x-color-primary,#3b82f6);"
   "--x-checkbox-focus-ring:var(--x-color-focus-ring,#93c5fd);"
   "}"
   "}"
   "[part=control]{"
   "all:unset;"
   "box-sizing:border-box;"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "padding:0;"
   "cursor:pointer;"
   "}"
   "[part=control][disabled]{"
   "cursor:default;"
   "opacity:var(--x-checkbox-disabled-opacity);"
   "}"
   "[part=control]:focus-visible{"
   "outline:none;"
   "box-shadow:0 0 0 3px var(--x-checkbox-focus-ring);"
   "border-radius:calc(var(--x-checkbox-border-radius) + 2px);"
   "}"
   "[part=box]{"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "width:var(--x-checkbox-size);"
   "height:var(--x-checkbox-size);"
   "border:var(--x-checkbox-border-width) solid var(--x-checkbox-border-color);"
   "border-radius:var(--x-checkbox-border-radius);"
   "background:var(--x-checkbox-bg);"
   "transition:var(--x-checkbox-transition);"
   "position:relative;"
   "flex-shrink:0;"
   "}"
   ":host([data-checked]) [part=box]{"
   "background:var(--x-checkbox-bg-checked);"
   "border-color:var(--x-checkbox-border-checked);"
   "}"
   ":host([data-indeterminate]) [part=box]{"
   "background:var(--x-checkbox-bg-indeterminate);"
   "border-color:var(--x-checkbox-border-indeterminate);"
   "}"
   "[part=check]{"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "position:relative;"
   "width:100%;"
   "height:100%;"
   "}"
   "[part=check]::before{"
   "content:'\\2713';"
   "color:var(--x-checkbox-check-color);"
   "font-size:10px;"
   "font-weight:700;"
   "line-height:1;"
   "opacity:0;"
   "transition:opacity 100ms ease;"
   "position:absolute;"
   "}"
   ":host([data-checked]) [part=check]::before{"
   "opacity:1;"
   "}"
   "[part=check]::after{"
   "content:'';"
   "display:block;"
   "width:8px;"
   "height:2px;"
   "background:var(--x-checkbox-check-color);"
   "border-radius:1px;"
   "opacity:0;"
   "transition:opacity 100ms ease;"
   "position:absolute;"
   "}"
   ":host([data-indeterminate]) [part=check]::after{"
   "opacity:1;"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=box]{transition:none;}"
   "[part=check]::before{transition:none;}"
   "[part=check]::after{transition:none;}"
   "}"))

;; ---------------------------------------------------------------------------
;; DOM helpers
;; ---------------------------------------------------------------------------
(defn- make-el [tag] (.createElement js/document tag))

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root        (.attachShadow el #js {:mode "open"})
        style-el    (make-el "style")
        control-el  (make-el "button")
        box-el      (make-el "span")
        check-el    (make-el "span")]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! control-el attr-part part-control)
    (du/set-attr! control-el attr-type val-button)
    (du/set-attr! control-el attr-role val-checkbox)
    (du/set-attr! box-el     attr-part part-box)
    (du/set-attr! check-el   attr-part part-check)

    (.appendChild box-el check-el)
    (.appendChild control-el box-el)
    (.appendChild root style-el)
    (.appendChild root control-el)

    (let [refs #js {}]
      (gobj/set refs "root"    root)
      (gobj/set refs rk-control control-el)
      (gobj/set refs rk-box     box-el)
      (gobj/set refs rk-check   check-el)
      (du/setv! el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Read element state from attributes
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/normalize
   {:checked-present?       (mu/parse-bool-attr (du/get-attr el model/attr-checked))
    :indeterminate-present? (mu/parse-bool-attr (du/get-attr el model/attr-indeterminate))
    :disabled-present?      (mu/parse-bool-attr (du/get-attr el model/attr-disabled))
    :readonly-present?      (mu/parse-bool-attr (du/get-attr el model/attr-readonly))
    :required-present?      (mu/parse-bool-attr (du/get-attr el model/attr-required))
    :name-raw               (du/get-attr el model/attr-name)
    :value-raw              (du/get-attr el model/attr-value)
    :aria-label-raw         (du/get-attr el model/attr-aria-label)
    :aria-describedby-raw   (du/get-attr el model/attr-aria-describedby)
    :aria-labelledby-raw    (du/get-attr el model/attr-aria-labelledby)}))

;; ---------------------------------------------------------------------------
;; DOM patching (render-orchestrator: phase list of named helpers)
;; ---------------------------------------------------------------------------
(defn- apply-control-aria! [^js control-el m]
  (let [{:keys [disabled? required? readonly?
                aria-label aria-labelledby aria-describedby
                aria-checked]} m]
    (du/set-attr! control-el attr-aria-checked  aria-checked)
    (du/set-attr! control-el attr-aria-disabled (if disabled? val-true val-false))
    (du/set-attr! control-el attr-aria-required (if required? val-true val-false))
    (du/set-attr! control-el attr-aria-readonly (if readonly? val-true val-false))
    (if aria-label
      (du/set-attr! control-el attr-aria-label aria-label)
      (du/remove-attr! control-el attr-aria-label))
    (if aria-labelledby
      (du/set-attr! control-el attr-aria-labelledby aria-labelledby)
      (du/remove-attr! control-el attr-aria-labelledby))
    (if aria-describedby
      (du/set-attr! control-el attr-aria-describedby aria-describedby)
      (du/remove-attr! control-el attr-aria-describedby))))

(defn- apply-control-disabled! [^js control-el {:keys [disabled?]}]
  (set! (.-tabIndex control-el) (if disabled? -1 0))
  (if disabled?
    (du/set-attr! control-el attr-disabled "")
    (du/remove-attr! control-el attr-disabled)))

(defn- apply-host-data! [^js el {:keys [checked? indeterminate? disabled?]}]
  (du/set-bool-attr! el attr-data-checked       checked?)
  (du/set-bool-attr! el attr-data-indeterminate indeterminate?)
  (du/set-bool-attr! el attr-data-disabled      disabled?))

(defn- apply-form-value! [^js el {:keys [checked? value]}]
  (when-let [^js internals (du/getv el k-internals)]
    (let [form-value (when checked? value)]
      (.setFormValue internals (or form-value nil)))))

(defn- apply-model! [^js el m]
  (when-let [refs (du/getv el k-refs)]
    (let [^js control-el (gobj/get refs rk-control)]
      (apply-control-aria!     control-el m)
      (apply-control-disabled! control-el m)
      (apply-host-data!        el m)
      (apply-form-value!       el m)
      ;; Cache write at the tail.
      (du/setv! el k-model m))))

(defn- update-from-attrs! [^js el]
  (when (du/getv el k-refs)
    (let [new-m (read-model el)
          old-m (du/getv el k-model)]
      (when (not= old-m new-m)
        (apply-model! el new-m)))))

;; ---------------------------------------------------------------------------
;; Toggle logic
;; ---------------------------------------------------------------------------
(defn- try-toggle! [^js el]
  (let [m              (read-model el)
        disabled?      (:disabled? m)
        readonly?      (:readonly? m)]
    (when-not (or disabled? readonly?)
      (let [checked?       (:checked? m)
            indeterminate? (:indeterminate? m)
            next-state     (model/next-toggle-state checked? indeterminate?)
            next-checked   (:checked? next-state)
            value          (:value m)
            allowed?       (du/dispatch-cancelable!
                            el
                            model/event-change-request
                            #js {:value           value
                                 :previousChecked  checked?
                                 :nextChecked      next-checked})]
        (when allowed?
          (du/set-bool-attr! el model/attr-checked       next-checked)
          (du/set-bool-attr! el model/attr-indeterminate (:indeterminate? next-state))
          (update-from-attrs! el)
          (du/dispatch! el model/event-change
                     #js {:value   value
                          :checked next-checked}))))))

;; ---------------------------------------------------------------------------
;; External label association
;; ---------------------------------------------------------------------------
(defn- wire-external-label! [^js el]
  (let [id (du/get-attr el attr-id)]
    (when id
      (let [^js lbl (.querySelector js/document (str "label[for='" id "']"))]
        (when lbl
          (let [lid (or (du/get-attr lbl attr-id)
                        (let [gen-id (str "x-cb-lbl-" id)]
                          (du/set-attr! lbl attr-id gen-id)
                          gen-id))]
            (when-let [refs (du/getv el k-refs)]
              (let [^js control (gobj/get refs rk-control)]
                (when-not (du/has-attr? el model/attr-aria-labelledby)
                  (du/set-attr! control attr-aria-labelledby lid))))))))))

;; ---------------------------------------------------------------------------
;; Event handlers (named — listener-spec style)
;; ---------------------------------------------------------------------------
(defn- on-control-click [^js el ^js _evt]
  (try-toggle! el))

(defn- on-control-keydown [^js el ^js evt]
  (let [k (.-key evt)]
    (when (or (= k key-space) (= k key-enter))
      (.preventDefault evt)
      (try-toggle! el))))

(defn- add-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js control-el (gobj/get refs rk-control)
          click-h        (fn handle-control-click   [evt] (on-control-click   el evt))
          keydown-h      (fn handle-control-keydown [evt] (on-control-keydown el evt))
          handlers       #js {}]
      (.addEventListener control-el ev-click   click-h)
      (.addEventListener control-el ev-keydown keydown-h)
      (gobj/set handlers hk-click   click-h)
      (gobj/set handlers hk-keydown keydown-h)
      (du/setv! el k-handlers handlers))))

(defn- remove-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (let [^js control-el (gobj/get refs rk-control)]
        (.removeEventListener control-el ev-click   (gobj/get handlers hk-click))
        (.removeEventListener control-el ev-keydown (gobj/get handlers hk-keydown)))
      (du/setv! el k-handlers nil))))

;; ---------------------------------------------------------------------------
;; Form-associated callbacks
;; ---------------------------------------------------------------------------
(defn- form-disabled! [^js el disabled?]
  (du/set-bool-attr! el model/attr-disabled (boolean disabled?))
  (update-from-attrs! el))

(defn- form-reset! [^js el]
  (du/remove-attr! el model/attr-checked)
  (du/remove-attr! el model/attr-indeterminate)
  (update-from-attrs! el))

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
  (wire-external-label! el)
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
