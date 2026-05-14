(ns baredom.components.x-form-field.x-form-field
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-form-field.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs      "__xFormFieldRefs")
(def ^:private k-model     "__xFormFieldModel")
(def ^:private k-internals "__xFormFieldInternals")
(def ^:private k-handlers  "__xFormFieldHandlers")

;; ── Refs / handler keys ───────────────────────────────────────────────────
(def ^:private rk-field   "field")
(def ^:private rk-label   "label")
(def ^:private rk-input   "input")
(def ^:private rk-hint    "hint")
(def ^:private rk-error   "error")

(def ^:private hk-input  "input")
(def ^:private hk-change "change")

;; ── String-literal constants ──────────────────────────────────────────────
(def ^:private attr-part             "part")
(def ^:private attr-id               "id")
(def ^:private attr-for              "for")
(def ^:private attr-role             "role")
(def ^:private attr-aria-live        "aria-live")
(def ^:private attr-aria-labelledby  "aria-labelledby")
(def ^:private attr-aria-required    "aria-required")
(def ^:private attr-aria-invalid     "aria-invalid")
(def ^:private attr-aria-describedby "aria-describedby")
(def ^:private attr-data-invalid     "data-invalid")

(def ^:private part-field         "field")
(def ^:private part-label         "label")
(def ^:private part-input-wrapper "input-wrapper")
(def ^:private part-input         "input")
(def ^:private part-hint          "hint")
(def ^:private part-error         "error")

(def ^:private id-label "label")
(def ^:private id-input "input")
(def ^:private id-hint  "hint")
(def ^:private id-error "error")

(def ^:private val-true      "true")
(def ^:private val-false     "false")
(def ^:private val-polite    "polite")
(def ^:private val-assertive "assertive")
(def ^:private val-alert     "alert")

(def ^:private cls-label-hidden "label-hidden")
(def ^:private cls-hint-hidden  "hint-hidden")
(def ^:private cls-error-hidden "error-hidden")

(def ^:private ev-input  "input")
(def ^:private ev-change "change")

(def ^:private msg-value-missing "Please fill in this field.")

;; ── Style ─────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-form-field-label-color:var(--x-color-text-muted,#374151);"
   "--x-form-field-label-font-size:var(--x-font-size-sm,0.875rem);"
   "--x-form-field-input-bg:var(--x-color-surface,#ffffff);"
   "--x-form-field-input-color:var(--x-color-text,#111827);"
   "--x-form-field-input-border:1px solid var(--x-color-border,#d1d5db);"
   "--x-form-field-input-border-radius:var(--x-radius-md,6px);"
   "--x-form-field-input-padding:0.5rem 0.75rem;"
   "--x-form-field-focus-ring-color:var(--x-color-primary,#2563eb);"
   "--x-form-field-error-color:var(--x-color-danger,#dc2626);"
   "--x-form-field-hint-color:var(--x-color-text-muted,#6b7280);"
   "--x-form-field-disabled-opacity:0.45;"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-form-field-label-color:var(--x-color-text-muted,#d1d5db);"
   "--x-form-field-input-bg:var(--x-color-surface-hover,#1f2937);"
   "--x-form-field-input-color:var(--x-color-text,#f9fafb);"
   "--x-form-field-input-border:1px solid var(--x-color-border,#4b5563);"
   "--x-form-field-focus-ring-color:var(--x-color-primary,#3b82f6);"
   "--x-form-field-error-color:var(--x-color-danger,#f87171);"
   "--x-form-field-hint-color:var(--x-color-text-muted,#9ca3af);"
   "}"
   "}"
   "[part=field]{display:flex;flex-direction:column;gap:0.25rem;}"
   "[part=label]{display:block;font-size:var(--x-form-field-label-font-size);font-weight:500;color:var(--x-form-field-label-color);}"
   ".label-hidden{display:none;}"
   "[part=input-wrapper]{display:block;}"
   "[part=input]{"
   "display:block;width:100%;box-sizing:border-box;"
   "padding:var(--x-form-field-input-padding);"
   "background:var(--x-form-field-input-bg);"
   "color:var(--x-form-field-input-color);"
   "border:var(--x-form-field-input-border);"
   "border-radius:var(--x-form-field-input-border-radius);"
   "font-size:1rem;line-height:1.5;font-family:inherit;outline:none;"
   "transition:border-color 120ms ease,box-shadow 120ms ease;"
   "}"
   "[part=input]:focus{"
   "border-color:var(--x-form-field-focus-ring-color);"
   "box-shadow:0 0 0 3px color-mix(in srgb,var(--x-form-field-focus-ring-color) 20%,transparent);"
   "}"
   ":host([data-invalid]) [part=input]{border-color:var(--x-form-field-error-color);}"
   ":host([data-invalid]) [part=input]:focus{"
   "box-shadow:0 0 0 3px color-mix(in srgb,var(--x-form-field-error-color) 20%,transparent);"
   "}"
   "[part=input]:disabled{opacity:var(--x-form-field-disabled-opacity);cursor:not-allowed;}"
   "[part=hint]{display:block;font-size:0.8125rem;color:var(--x-form-field-hint-color);}"
   ".hint-hidden{display:none;}"
   "[part=error]{display:block;font-size:0.8125rem;color:var(--x-form-field-error-color);}"
   ".error-hidden{display:none;}"
   "@media (prefers-reduced-motion:reduce){[part=input]{transition:none;}}"))

;; ── DOM helpers ───────────────────────────────────────────────────────────
(defn- make-el [tag] (.createElement js/document tag))

;; ── Shadow DOM construction ───────────────────────────────────────────────
(defn- make-shadow! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (make-el "style")
        field-el   (make-el "div")
        label-el   (make-el "label")
        wrapper-el (make-el "div")
        input-el   (make-el "input")
        hint-el    (make-el "span")
        error-el   (make-el "span")
        refs       #js {}]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! field-el   attr-part part-field)
    (du/set-attr! label-el   attr-part part-label)
    (du/set-attr! label-el   attr-id   id-label)
    (du/set-attr! label-el   attr-for  id-input)
    (du/set-attr! wrapper-el attr-part part-input-wrapper)
    (du/set-attr! input-el   attr-part part-input)
    (du/set-attr! input-el   attr-id   id-input)
    (du/set-attr! input-el   attr-aria-labelledby id-label)
    (du/set-attr! hint-el    attr-part      part-hint)
    (du/set-attr! hint-el    attr-id        id-hint)
    (du/set-attr! hint-el    attr-aria-live val-polite)
    (du/set-attr! error-el   attr-part      part-error)
    (du/set-attr! error-el   attr-id        id-error)
    (du/set-attr! error-el   attr-role      val-alert)
    (du/set-attr! error-el   attr-aria-live val-assertive)

    (.appendChild wrapper-el input-el)
    (.appendChild field-el label-el)
    (.appendChild field-el wrapper-el)
    (.appendChild field-el hint-el)
    (.appendChild field-el error-el)
    (.appendChild root style-el)
    (.appendChild root field-el)

    (gobj/set refs rk-field field-el)
    (gobj/set refs rk-label label-el)
    (gobj/set refs rk-input input-el)
    (gobj/set refs rk-hint  hint-el)
    (gobj/set refs rk-error error-el)
    (du/setv! el k-refs refs)
    refs))

;; ── Validity sync ─────────────────────────────────────────────────────────
(defn- sync-validity! [^js el ^js internals ^js input-el]
  (let [has-error? (du/has-attr? el model/attr-error)
        error-msg  (or (du/get-attr el model/attr-error) "")
        required?  (du/has-attr? el model/attr-required)
        value      (.-value input-el)]
    (cond
      has-error?
      (.setValidity internals #js {:customError true} error-msg input-el)
      (and required? (= value ""))
      (.setValidity internals #js {:valueMissing true} msg-value-missing input-el)
      :else
      (.setValidity internals #js {} "" input-el))))

;; ── Model reading ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:label-raw         (du/get-attr el model/attr-label)
    :type-raw          (du/get-attr el model/attr-type)
    :name-raw          (du/get-attr el model/attr-name)
    :value-raw         (du/get-attr el model/attr-value)
    :placeholder-raw   (du/get-attr el model/attr-placeholder)
    :hint-raw          (du/get-attr el model/attr-hint)
    :error-raw         (du/get-attr el model/attr-error)
    :disabled-present? (du/has-attr? el model/attr-disabled)
    :readonly-present? (du/has-attr? el model/attr-readonly)
    :required-present? (du/has-attr? el model/attr-required)
    :autocomplete-raw  (du/get-attr el model/attr-autocomplete)}))

;; ── DOM patching (render-orchestrator: phase list of named helpers) ──────
(defn- apply-input-native! [^js input-el {:keys [type name placeholder disabled? readonly? required? autocomplete]}]
  (set! (.-type input-el)         type)
  (set! (.-name input-el)         name)
  (set! (.-placeholder input-el)  placeholder)
  (set! (.-disabled input-el)     disabled?)
  (set! (.-readOnly input-el)     readonly?)
  (set! (.-required input-el)     required?)
  (set! (.-autocomplete input-el) autocomplete))

(defn- describedby-token [{:keys [has-hint? has-error?]}]
  (cond
    (and has-hint? has-error?) "hint error"
    has-hint?                  "hint"
    has-error?                 "error"))

(defn- apply-input-aria! [^js input-el {:keys [required? has-error?] :as m}]
  (du/set-attr! input-el attr-aria-required (if required? val-true val-false))
  (du/set-attr! input-el attr-aria-invalid  (if has-error? val-true val-false))
  (if-let [token (describedby-token m)]
    (du/set-attr!    input-el attr-aria-describedby token)
    (du/remove-attr! input-el attr-aria-describedby)))

(defn- apply-text-block!
  "Set textContent and toggle a visibility CSS class."
  [^js node text visible? hide-class]
  (set! (.-textContent node) text)
  (if visible?
    (.remove (.-classList node) hide-class)
    (.add    (.-classList node) hide-class)))

(defn- apply-label! [^js label-el {:keys [label has-label?]}]
  (apply-text-block! label-el label has-label? cls-label-hidden))

(defn- apply-hint! [^js hint-el {:keys [hint has-hint?]}]
  (apply-text-block! hint-el hint has-hint? cls-hint-hidden))

(defn- apply-error! [^js error-el {:keys [error has-error?]}]
  (apply-text-block! error-el error has-error? cls-error-hidden))

(defn- apply-host-data! [^js el {:keys [has-error?]}]
  (du/set-bool-attr! el attr-data-invalid has-error?))

(defn- apply-validity! [^js el ^js input-el]
  (when-let [^js internals (du/getv el k-internals)]
    (sync-validity! el internals input-el)))

(defn- apply-model! [^js el m]
  (when-let [refs (du/getv el k-refs)]
    (let [^js label-el (gobj/get refs rk-label)
          ^js input-el (gobj/get refs rk-input)
          ^js hint-el  (gobj/get refs rk-hint)
          ^js error-el (gobj/get refs rk-error)]
      (apply-input-native! input-el m)
      (apply-input-aria!   input-el m)
      (apply-label!        label-el m)
      (apply-hint!         hint-el  m)
      (apply-error!        error-el m)
      (apply-host-data!    el m)
      (apply-validity!     el input-el)
      (du/setv! el k-model m))))

(defn- update-from-attrs! [^js el]
  (when (du/getv el k-refs)
    (let [new-m (read-model el)
          old-m (du/getv el k-model)]
      (when (not= old-m new-m)
        (apply-model! el new-m)))))

;; ── Event handlers ────────────────────────────────────────────────────────
(defn- on-input-input [^js el ^js _evt]
  (when-let [refs (du/getv el k-refs)]
    (let [^js input-el (gobj/get refs rk-input)
          value        (.-value input-el)
          name         (or (du/get-attr el model/attr-name) "")]
      (when-let [^js internals (du/getv el k-internals)]
        (.setFormValue internals value)
        (sync-validity! el internals input-el))
      (du/dispatch! el model/event-input #js {:name name :value value}))))

(defn- on-input-change [^js el ^js _evt]
  (when-let [refs (du/getv el k-refs)]
    (let [^js input-el (gobj/get refs rk-input)
          value        (.-value input-el)
          name         (or (du/get-attr el model/attr-name) "")]
      (when-let [^js internals (du/getv el k-internals)]
        (.setFormValue internals value)
        (sync-validity! el internals input-el))
      (du/dispatch! el model/event-change #js {:name name :value value}))))

;; ── Listener management ───────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js input-el (gobj/get refs rk-input)
          input-h      (fn handle-input-input  [e] (on-input-input  el e))
          change-h     (fn handle-input-change [e] (on-input-change el e))
          handlers     #js {}]
      (.addEventListener input-el ev-input  input-h)
      (.addEventListener input-el ev-change change-h)
      (gobj/set handlers hk-input  input-h)
      (gobj/set handlers hk-change change-h)
      (du/setv! el k-handlers handlers))))

(defn- remove-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (let [^js input-el (gobj/get refs rk-input)]
        (.removeEventListener input-el ev-input  (gobj/get handlers hk-input))
        (.removeEventListener input-el ev-change (gobj/get handlers hk-change)))
      (du/setv! el k-handlers nil))))

;; ── Form-associated callbacks ─────────────────────────────────────────────
(defn- form-disabled! [^js el disabled?]
  (du/set-bool-attr! el model/attr-disabled (boolean disabled?))
  (update-from-attrs! el))

(defn- form-reset! [^js el]
  (du/remove-attr! el model/attr-value)
  (when-let [refs (du/getv el k-refs)]
    (let [^js input-el (gobj/get refs rk-input)]
      (set! (.-value input-el) "")))
  (when-let [^js internals (du/getv el k-internals)]
    (.setFormValue internals ""))
  (update-from-attrs! el))

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el)
    (when (.-attachInternals el)
      (du/setv! el k-internals (.attachInternals el))))
  (remove-listeners! el)
  ;; Push value attr to input if set
  (when-let [refs (du/getv el k-refs)]
    (let [^js input-el (gobj/get refs rk-input)
          val-attr     (du/get-attr el model/attr-value)]
      (when val-attr
        (set! (.-value input-el) val-attr))))
  (when-let [^js internals (du/getv el k-internals)]
    (.setFormValue internals (or (du/get-attr el model/attr-value) "")))
  (add-listeners! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el attr-name _old new-val]
  ;; For value attr: sync to input.value only if it differs (avoids cursor jump on typing)
  (when (= attr-name model/attr-value)
    (when-let [refs (du/getv el k-refs)]
      (let [^js input-el (gobj/get refs rk-input)]
        (when (not= (.-value input-el) new-val)
          (set! (.-value input-el) (or new-val ""))))))
  (update-from-attrs! el))

;; ── Property accessors ────────────────────────────────────────────────────
;; Special value property: also syncs input.value when set
(defn- define-value-prop! [^js proto]
  (.defineProperty
   js/Object proto "value"
   #js {:configurable true
        :enumerable   true
        :get (fn xff-get-value []
               (this-as ^js this
                 (if-let [refs (gobj/get this k-refs)]
                   (.-value (gobj/get refs rk-input))
                   (or (du/get-attr this model/attr-value) ""))))
        :set (fn xff-set-value [v]
               (this-as ^js this
                 (let [str-v (if (and (some? v) (not= v js/undefined)) (str v) "")]
                   (du/set-attr! this model/attr-value str-v)
                   (when-let [refs (gobj/get this k-refs)]
                     (let [^js input-el (gobj/get refs rk-input)]
                       (set! (.-value input-el) str-v))))))}))

(defn- install-property-accessors! [^js proto]
  (define-value-prop! proto)
  (du/define-string-prop! proto "label"        model/attr-label "")
  (du/define-string-prop! proto "type"         model/attr-type "")
  (du/define-string-prop! proto "name"         model/attr-name "")
  (du/define-string-prop! proto "placeholder"  model/attr-placeholder "")
  (du/define-string-prop! proto "autocomplete" model/attr-autocomplete "")
  (du/define-bool-prop! proto "disabled" model/attr-disabled)
  (du/define-bool-prop! proto "readOnly" model/attr-readonly)
  (du/define-bool-prop! proto "required" model/attr-required))

;; ── Public API ────────────────────────────────────────────────────────────
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
