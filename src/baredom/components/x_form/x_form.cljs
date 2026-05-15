(ns baredom.components.x-form.x-form
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.x-form.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs     "__xFormRefs")
(def ^:private k-model    "__xFormModel")
(def ^:private k-handlers "__xFormHandlers")

;; ── Refs / handler / string-literal constants ─────────────────────────────
(def ^:private rk-form "form")

(def ^:private hk-submit "submit")
(def ^:private hk-reset  "reset")
(def ^:private hk-click  "click")

(def ^:private attr-part         "part")
(def ^:private attr-novalidate   "novalidate")
(def ^:private attr-autocomplete "autocomplete")
(def ^:private attr-aria-busy    "aria-busy")
(def ^:private attr-data-loading "data-loading")
(def ^:private attr-error        "error")

(def ^:private part-root "root")

(def ^:private val-true "true")

(def ^:private ev-submit "submit")
(def ^:private ev-reset  "reset")
(def ^:private ev-click  "click")

;; Selectors — extracted because they're constructed once and referenced twice.
(def ^:private sel-named-enabled "[name]:not([disabled])")
(def ^:private sel-named         "[name]")
(def ^:private sel-error         "[error]")
(def ^:private sel-button
  ;; Native form-control buttons + the library's own x-button. The latter
  ;; is included per CLAUDE.md dogfooding rule: when a project surface
  ;; could use x-button, it should — x-form is exactly that surface.
  "button,input[type=submit],input[type=reset],x-button[type=submit],x-button[type=reset]")

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{display:block;}"
   "[part=root]{"
   "display:flex;"
   "flex-direction:column;"
   "gap:var(--x-form-gap,1rem);"
   "width:var(--x-form-width,100%);"
   "}"
   ":host([loading]){pointer-events:none;opacity:0.6;}"))

;; ── DOM helpers ───────────────────────────────────────────────────────────

;; ── Shadow DOM construction ───────────────────────────────────────────────
(defn- make-shadow! [^js el]
  (when-not (.-shadowRoot el)
    (let [root     (.attachShadow el #js {:mode "open"})
          style-el (.createElement js/document "style")
          form-el  (.createElement js/document "form")
          slot-el  (.createElement js/document "slot")
          refs     #js {}]

      (set! (.-textContent style-el) style-text)

      (du/set-attr! form-el attr-part       part-root)
      (du/set-attr! form-el attr-novalidate "")

      (.appendChild form-el slot-el)
      (.appendChild root style-el)
      (.appendChild root form-el)

      (gobj/set refs rk-form form-el)
      (du/setv! el k-refs refs))))

;; ── Model reading ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:loading-raw      (du/get-attr el model/attr-loading)
    :novalidate-raw   (when (du/has-attr? el model/attr-novalidate) "")
    :autocomplete-raw (du/get-attr el model/attr-autocomplete)}))

;; ── DOM patching ──────────────────────────────────────────────────────────
(defn- apply-form-attrs! [^js form-el {:keys [autocomplete loading?]}]
  (du/set-attr! form-el attr-autocomplete autocomplete)
  (if loading?
    (do (du/set-attr! form-el attr-aria-busy     val-true)
        (du/set-attr! form-el attr-data-loading  ""))
    (do (du/remove-attr! form-el attr-aria-busy)
        (du/remove-attr! form-el attr-data-loading))))

(defn- apply-model! [^js el m]
  (when-let [refs (du/getv el k-refs)]
    (let [^js form-el (gobj/get refs rk-form)]
      (apply-form-attrs! form-el m)
      (du/setv! el k-model m))))

(defn- update-from-attrs! [^js el]
  (when (du/getv el k-refs)
    (let [new-m (read-model el)
          old-m (du/getv el k-model)]
      (when (not= old-m new-m)
        (apply-model! el new-m)))))

;; ── Validation ────────────────────────────────────────────────────────────
(defn- report-fields-validity! [^js el]
  (let [fields (.querySelectorAll el sel-named-enabled)
        result #js {:ok true}]
    (dotimes [i (.-length fields)]
      (let [^js field (aget fields i)]
        (when (.-reportValidity field)
          (when-not (.reportValidity field)
            (aset result "ok" false)))))
    (aget result "ok")))

;; ── Value collection ──────────────────────────────────────────────────────
(defn- collect-values [^js el]
  ;; Use a null-prototype object so consumer-supplied field names like
  ;; "toString" or "__proto__" don't shadow Object.prototype methods on
  ;; the result. (Adversarial form name="toString" would otherwise break
  ;; consumer code calling values.toString().)
  (let [fields (.querySelectorAll el sel-named-enabled)
        result (js/Object.create nil)]
    (dotimes [i (.-length fields)]
      (let [^js field (aget fields i)
            field-name (.-name field)]
        (when (and field-name (not= field-name ""))
          (let [tag-lower  (.toLowerCase (.-tagName field))
                type-attr  (.getAttribute field "type")
                checkbox?  (or (= type-attr "checkbox")
                               (= type-attr "radio")
                               (= tag-lower "x-checkbox"))]
            (if checkbox?
              (when (.-checked field)
                (aset result field-name (or (.-value field) "on")))
              (aset result field-name (or (.-value field) "")))))))
    result))

;; ── Event handlers ────────────────────────────────────────────────────────
(defn- on-submit [^js el ^js e]
  (.preventDefault e)
  (let [m (read-model el)]
    (when-not (:loading? m)
      (let [valid? (or (:novalidate? m) (report-fields-validity! el))]
        (when valid?
          (let [values (collect-values el)]
            (du/dispatch-cancelable! el model/event-submit #js {:values values})))))))

(defn- on-reset [^js el ^js _e]
  (let [fields (.querySelectorAll el sel-named)]
    (dotimes [i (.-length fields)]
      (let [^js field (aget fields i)]
        (when (.-formResetCallback field)
          (.formResetCallback field)))))
  (du/dispatch! el model/event-reset #js {}))

(defn- on-click [^js el ^js e]
  (when-let [^js btn (.closest (.-target e) sel-button)]
    (let [btn-type (.-type btn)]
      (when-let [refs (du/getv el k-refs)]
        (let [^js form-el (gobj/get refs rk-form)]
          (cond
            (or (= btn-type "submit") (= btn-type ""))
            (.requestSubmit form-el)
            (= btn-type "reset")
            (.reset form-el)))))))

;; ── Listener management ───────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js form-el (gobj/get refs rk-form)
          submit-h    (fn handle-form-submit [e] (on-submit el e))
          reset-h     (fn handle-form-reset  [e] (on-reset  el e))
          click-h     (fn handle-host-click  [e] (on-click  el e))
          handlers    #js {}]
      (.addEventListener form-el ev-submit submit-h)
      (.addEventListener form-el ev-reset  reset-h)
      (.addEventListener el      ev-click  click-h)
      (gobj/set handlers hk-submit submit-h)
      (gobj/set handlers hk-reset  reset-h)
      (gobj/set handlers hk-click  click-h)
      (du/setv! el k-handlers handlers))))

(defn- remove-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (let [^js form-el (gobj/get refs rk-form)]
        (.removeEventListener form-el ev-submit (gobj/get handlers hk-submit))
        (.removeEventListener form-el ev-reset  (gobj/get handlers hk-reset))
        (.removeEventListener el      ev-click  (gobj/get handlers hk-click)))
      (du/setv! el k-handlers nil))))

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (make-shadow! el)
  (remove-listeners! el)
  (add-listeners! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Public methods ────────────────────────────────────────────────────────
(defn- form-of [^js this]
  (when-let [refs (du/getv this k-refs)]
    (gobj/get refs rk-form)))

(defn- set-field-error! [^js this field-name msg]
  ;; Match by .name property instead of interpolating field-name into a
  ;; CSS selector. Names containing `"`, `]`, `\` or other CSS-special
  ;; characters are legal HTML but would throw SyntaxError on
  ;; `[name="<raw>"]`. Iterating named fields and comparing .name avoids
  ;; the issue entirely.
  (let [fields (.querySelectorAll this sel-named)
        clear? (or (nil? msg) (= msg "") (= msg js/undefined))]
    (dotimes [i (.-length fields)]
      (let [^js field (aget fields i)]
        (when (= (.-name field) field-name)
          (if clear?
            (du/remove-attr! field attr-error)
            (du/set-attr! field attr-error msg)))))))

(defn- clear-errors! [^js this]
  (let [fields (.querySelectorAll this sel-error)]
    (dotimes [i (.-length fields)]
      (du/remove-attr! (aget fields i) attr-error))))

(defn- install-methods! [^js proto]
  ;; .defineProperty with a :value descriptor — same Tier-2 idiom adopted
  ;; in x-cancel-dialogue, x-alert, x-collapse, x-combobox. Bare `aset`
  ;; on the prototype was audited out of x-button in PR #155 and the
  ;; rest of the components in PRs #160-#162.
  (.defineProperty js/Object proto "submit"
                   #js {:value (fn xf-submit []
                                 (this-as ^js this
                                   (when-let [^js form-el (form-of this)]
                                     (.requestSubmit form-el))))
                        :writable true :configurable true})
  (.defineProperty js/Object proto "reset"
                   #js {:value (fn xf-reset []
                                 (this-as ^js this
                                   (when-let [^js form-el (form-of this)]
                                     (.reset form-el))))
                        :writable true :configurable true})
  (.defineProperty js/Object proto "setFieldError"
                   #js {:value (fn xf-set-field-error [field-name msg]
                                 (this-as ^js this
                                   (set-field-error! this field-name msg)))
                        :writable true :configurable true})
  (.defineProperty js/Object proto "clearErrors"
                   #js {:value (fn xf-clear-errors []
                                 (this-as ^js this (clear-errors! this)))
                        :writable true :configurable true}))

(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api)
  (install-methods! proto))

;; ── Public API ────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
