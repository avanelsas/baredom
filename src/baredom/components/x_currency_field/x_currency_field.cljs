(ns baredom.components.x-currency-field.x-currency-field
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.x-currency-field.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs      "__xCurrencyFieldRefs")
(def ^:private k-model     "__xCurrencyFieldModel")
(def ^:private k-internals "__xCurrencyFieldInternals")
(def ^:private k-handlers  "__xCurrencyFieldHandlers")

;; ── Refs / handler keys ───────────────────────────────────────────────────
(def ^:private rk-label   "label")
(def ^:private rk-symbol  "symbol")
(def ^:private rk-input   "input")
(def ^:private rk-hint    "hint")
(def ^:private rk-error   "error")
(def ^:private rk-wrapper "wrapper")
(def ^:private rk-field   "field")

(def ^:private hk-focus  "focus")
(def ^:private hk-input  "input")
(def ^:private hk-change "change")

;; ── String-literal constants ──────────────────────────────────────────────
(def ^:private attr-part            "part")
(def ^:private attr-id              "id")
(def ^:private attr-for             "for")
(def ^:private attr-role            "role")
(def ^:private attr-inputmode       "inputmode")
(def ^:private attr-aria-hidden     "aria-hidden")
(def ^:private attr-aria-live       "aria-live")
(def ^:private attr-aria-labelledby "aria-labelledby")
(def ^:private attr-aria-required   "aria-required")
(def ^:private attr-aria-invalid    "aria-invalid")
(def ^:private attr-aria-describedby "aria-describedby")
(def ^:private attr-data-invalid    "data-invalid")

(def ^:private part-field         "field")
(def ^:private part-label         "label")
(def ^:private part-input-wrapper "input-wrapper")
(def ^:private part-symbol        "symbol")
(def ^:private part-input         "input")
(def ^:private part-hint          "hint")
(def ^:private part-error         "error")

(def ^:private id-label "label")
(def ^:private id-input "input")
(def ^:private id-hint  "hint")
(def ^:private id-error "error")

(def ^:private val-true     "true")
(def ^:private val-false    "false")
(def ^:private val-polite   "polite")
(def ^:private val-assertive "assertive")
(def ^:private val-decimal  "decimal")
(def ^:private val-alert    "alert")

(def ^:private cls-label-hidden "label-hidden")
(def ^:private cls-hint-hidden  "hint-hidden")
(def ^:private cls-error-hidden "error-hidden")

(def ^:private ev-focus  "focus")
(def ^:private ev-input  "input")
(def ^:private ev-change "change")

(def ^:private msg-value-missing "Please fill in this field.")
(def ^:private msg-bad-input     "Please enter a valid number.")

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-currency-field-bg:var(--x-color-surface,#ffffff);"
   "--x-currency-field-color:var(--x-color-text,#111827);"
   "--x-currency-field-border:1px solid var(--x-color-border,#d1d5db);"
   "--x-currency-field-border-radius:var(--x-radius-md,6px);"
   "--x-currency-field-focus-ring-color:var(--x-color-primary,#2563eb);"
   "--x-currency-field-symbol-color:var(--x-color-text-muted,#6b7280);"
   "--x-currency-field-label-color:var(--x-color-text-muted,#374151);"
   "--x-currency-field-hint-color:var(--x-color-text-muted,#6b7280);"
   "--x-currency-field-error-color:var(--x-color-danger,#dc2626);"
   "--x-currency-field-disabled-opacity:0.45;"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-currency-field-bg:var(--x-color-surface-hover,#1f2937);"
   "--x-currency-field-color:var(--x-color-text,#f9fafb);"
   "--x-currency-field-border:1px solid var(--x-color-border,#4b5563);"
   "--x-currency-field-focus-ring-color:var(--x-color-primary,#3b82f6);"
   "--x-currency-field-symbol-color:var(--x-color-text-muted,#9ca3af);"
   "--x-currency-field-label-color:var(--x-color-text-muted,#d1d5db);"
   "--x-currency-field-hint-color:var(--x-color-text-muted,#9ca3af);"
   "--x-currency-field-error-color:var(--x-color-danger,#f87171);"
   "}"
   "}"
   "[part=field]{display:flex;flex-direction:column;gap:0.25rem;}"
   "[part=label]{display:block;font-size:0.875rem;font-weight:500;color:var(--x-currency-field-label-color);}"
   ".label-hidden{display:none;}"
   "[part=input-wrapper]{"
   "display:flex;align-items:center;"
   "background:var(--x-currency-field-bg);"
   "border:var(--x-currency-field-border);"
   "border-radius:var(--x-currency-field-border-radius);"
   "transition:border-color 120ms ease,box-shadow 120ms ease;"
   "}"
   "[part=input-wrapper]:focus-within{"
   "border-color:var(--x-currency-field-focus-ring-color);"
   "box-shadow:0 0 0 3px color-mix(in srgb,var(--x-currency-field-focus-ring-color) 20%,transparent);"
   "}"
   ":host([data-invalid]) [part=input-wrapper]{border-color:var(--x-currency-field-error-color);}"
   ":host([data-invalid]) [part=input-wrapper]:focus-within{"
   "box-shadow:0 0 0 3px color-mix(in srgb,var(--x-currency-field-error-color) 20%,transparent);"
   "}"
   "[part=symbol]{flex-shrink:0;padding:0.5rem 0 0.5rem 0.75rem;font-size:1rem;color:var(--x-currency-field-symbol-color);user-select:none;pointer-events:none;}"
   "[part=input]{flex:1;min-width:0;padding:0.5rem 0.75rem;background:transparent;color:var(--x-currency-field-color);border:none;outline:none;font-size:1rem;line-height:1.5;font-family:inherit;}"
   "[part=input]:disabled{opacity:1;cursor:not-allowed;}"
   ":host([disabled]) [part=input-wrapper]{opacity:var(--x-currency-field-disabled-opacity);cursor:not-allowed;}"
   "[part=hint]{display:block;font-size:0.8125rem;color:var(--x-currency-field-hint-color);}"
   ".hint-hidden{display:none;}"
   "[part=error]{display:block;font-size:0.8125rem;color:var(--x-currency-field-error-color);}"
   ".error-hidden{display:none;}"
   "@media (prefers-reduced-motion:reduce){[part=input-wrapper]{transition:none;}}"))

;; ── DOM helpers ───────────────────────────────────────────────────────────

;; ── Intl helpers ──────────────────────────────────────────────────────────
(defn- currency-symbol [currency locale]
  (try
    (let [fmt   (js/Intl.NumberFormat.
                 (if (= locale "") "en-US" locale)
                 #js {:style "currency" :currency currency})
          parts (.formatToParts fmt 0)
          sym   (some #(when (= "currency" (.-type %)) (.-value %))
                      (array-seq parts))]
      (or sym currency))
    (catch :default _ currency)))

(defn- format-display [value _currency locale]
  (let [num (js/parseFloat value)]
    (if (js/isNaN num)
      value
      (try
        (.format
         (js/Intl.NumberFormat.
          (if (= locale "") js/undefined locale)
          #js {:minimumFractionDigits 2 :maximumFractionDigits 2})
         num)
        (catch :default _ value)))))

(defn- is-focused? [^js el ^js input-el]
  (= input-el (.. el -shadowRoot -activeElement)))

;; ── Shadow DOM construction ───────────────────────────────────────────────
(defn- make-shadow! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (.createElement js/document "style")
        field-el   (.createElement js/document "div")
        label-el   (.createElement js/document "label")
        wrapper-el (.createElement js/document "div")
        symbol-el  (.createElement js/document "span")
        input-el   (.createElement js/document "input")
        hint-el    (.createElement js/document "span")
        error-el   (.createElement js/document "span")
        refs       #js {}]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! field-el   attr-part part-field)
    (du/set-attr! label-el   attr-part part-label)
    (du/set-attr! label-el   attr-id   id-label)
    (du/set-attr! label-el   attr-for  id-input)
    (du/set-attr! wrapper-el attr-part part-input-wrapper)
    (du/set-attr! symbol-el  attr-part part-symbol)
    (du/set-attr! symbol-el  attr-aria-hidden val-true)
    (du/set-attr! input-el   attr-part part-input)
    (du/set-attr! input-el   attr-id   id-input)
    (du/set-attr! input-el   attr-inputmode val-decimal)
    (du/set-attr! input-el   attr-aria-labelledby id-label)
    (du/set-attr! hint-el    attr-part part-hint)
    (du/set-attr! hint-el    attr-id   id-hint)
    (du/set-attr! hint-el    attr-aria-live val-polite)
    (du/set-attr! error-el   attr-part part-error)
    (du/set-attr! error-el   attr-id   id-error)
    (du/set-attr! error-el   attr-role val-alert)
    (du/set-attr! error-el   attr-aria-live val-assertive)

    (.appendChild wrapper-el symbol-el)
    (.appendChild wrapper-el input-el)
    (.appendChild field-el label-el)
    (.appendChild field-el wrapper-el)
    (.appendChild field-el hint-el)
    (.appendChild field-el error-el)
    (.appendChild root style-el)
    (.appendChild root field-el)

    (gobj/set refs rk-field   field-el)
    (gobj/set refs rk-label   label-el)
    (gobj/set refs rk-wrapper wrapper-el)
    (gobj/set refs rk-symbol  symbol-el)
    (gobj/set refs rk-input   input-el)
    (gobj/set refs rk-hint    hint-el)
    (gobj/set refs rk-error   error-el)
    (du/setv! el k-refs refs)
    refs))

;; ── Validity sync ─────────────────────────────────────────────────────────
(defn- sync-validity! [^js el ^js internals ^js input-el value]
  (let [has-error? (du/has-attr? el model/attr-error)
        error-msg  (or (du/get-attr el model/attr-error) "")
        required?  (du/has-attr? el model/attr-required)
        min-raw    (du/get-attr el model/attr-min)
        max-raw    (du/get-attr el model/attr-max)
        num        (js/parseFloat value)
        has-num?   (not (js/isNaN num))
        min-num    (when min-raw (js/parseFloat min-raw))
        max-num    (when max-raw (js/parseFloat max-raw))]
    (cond
      has-error?
      (.setValidity internals #js {:customError true} error-msg input-el)

      (and required? (= value ""))
      (.setValidity internals #js {:valueMissing true} msg-value-missing input-el)

      (and (not= value "") (not has-num?))
      (.setValidity internals #js {:badInput true} msg-bad-input input-el)

      (and has-num? min-num (not (js/isNaN min-num)) (< num min-num))
      (.setValidity internals #js {:rangeUnderflow true}
                    (str "Value must be at least " min-num ".") input-el)

      (and has-num? max-num (not (js/isNaN max-num)) (> num max-num))
      (.setValidity internals #js {:rangeOverflow true}
                    (str "Value must be at most " max-num ".") input-el)

      :else
      (.setValidity internals #js {} "" input-el))))

;; ── Model reading ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:name-raw          (du/get-attr el model/attr-name)
    :value-raw         (du/get-attr el model/attr-value)
    :currency-raw      (du/get-attr el model/attr-currency)
    :locale-raw        (du/get-attr el model/attr-locale)
    :min-raw           (du/get-attr el model/attr-min)
    :max-raw           (du/get-attr el model/attr-max)
    :placeholder-raw   (du/get-attr el model/attr-placeholder)
    :label-raw         (du/get-attr el model/attr-label)
    :hint-raw          (du/get-attr el model/attr-hint)
    :error-raw         (du/get-attr el model/attr-error)
    :disabled-present? (du/has-attr? el model/attr-disabled)
    :required-present? (du/has-attr? el model/attr-required)
    :readonly-present? (du/has-attr? el model/attr-readonly)}))

;; ── DOM patching (render-orchestrator: phase list of named helpers) ──────
(defn- apply-symbol! [^js symbol-el {:keys [currency locale]}]
  (set! (.-textContent symbol-el) (currency-symbol currency locale)))

(defn- apply-input-native! [^js input-el {:keys [name placeholder disabled? readonly? required?]}]
  (set! (.-name input-el)        name)
  (set! (.-placeholder input-el) placeholder)
  (set! (.-disabled input-el)    disabled?)
  (set! (.-readOnly input-el)    readonly?)
  (set! (.-required input-el)    required?))

(defn- describedby-token [{:keys [has-hint?]} has-error?]
  (cond
    (and has-hint? has-error?) "hint error"
    has-hint?                  "hint"
    has-error?                 "error"))

(defn- apply-input-aria! [^js input-el m has-error?]
  (du/set-attr! input-el attr-aria-required (if (:required? m) val-true val-false))
  (du/set-attr! input-el attr-aria-invalid  (if has-error? val-true val-false))
  (if-let [token (describedby-token m has-error?)]
    (du/set-attr!    input-el attr-aria-describedby token)
    (du/remove-attr! input-el attr-aria-describedby)))

(defn- apply-input-value!
  "Update the input's displayed value only when the input is not focused —
  clobbering during edit would surprise the user."
  [^js el ^js input-el {:keys [value currency locale]}]
  (when-not (is-focused? el input-el)
    (set! (.-value input-el)
          (if (= value "") "" (format-display value currency locale)))))

(defn- apply-text-block!
  "Reusable helper: set textContent and toggle a visibility CSS class."
  [^js node text visible? hide-class]
  (set! (.-textContent node) text)
  (if visible?
    (.remove (.-classList node) hide-class)
    (.add    (.-classList node) hide-class)))

(defn- apply-label! [^js label-el {:keys [label has-label?]}]
  (apply-text-block! label-el label has-label? cls-label-hidden))

(defn- apply-hint! [^js hint-el {:keys [hint has-hint?]}]
  (apply-text-block! hint-el hint has-hint? cls-hint-hidden))

(defn- apply-error! [^js error-el display-error has-error?]
  (apply-text-block! error-el display-error has-error? cls-error-hidden))

(defn- apply-host-data! [^js el has-error?]
  (du/set-bool-attr! el attr-data-invalid has-error?))

(defn- apply-validity! [^js el ^js input-el]
  (when-let [^js internals (du/getv el k-internals)]
    (sync-validity! el internals input-el (or (du/get-attr el model/attr-value) ""))))

(defn- compute-error-display [{:keys [has-error? error] :as m}]
  ;; Custom error attribute takes precedence; otherwise compute a message
  ;; from the model's validation state.
  (let [custom-error? has-error?
        computed-msg  (when-not custom-error? (model/validation-message m))]
    {:has-error?    (or custom-error? (not= (or computed-msg "") ""))
     :display-error (if custom-error? error (or computed-msg ""))}))

(defn- apply-model! [^js el m]
  (when-let [refs (du/getv el k-refs)]
    (let [^js label-el  (gobj/get refs rk-label)
          ^js symbol-el (gobj/get refs rk-symbol)
          ^js input-el  (gobj/get refs rk-input)
          ^js hint-el   (gobj/get refs rk-hint)
          ^js error-el  (gobj/get refs rk-error)
          {:keys [has-error? display-error]} (compute-error-display m)]
      (apply-symbol!       symbol-el m)
      (apply-input-native! input-el  m)
      (apply-input-aria!   input-el  m has-error?)
      (apply-input-value!  el input-el m)
      (apply-label!        label-el m)
      (apply-hint!         hint-el  m)
      (apply-error!        error-el display-error has-error?)
      (apply-host-data!    el has-error?)
      (apply-validity!     el input-el)
      (du/setv! el k-model m))))

(defn- update-from-attrs! [^js el]
  (when (du/getv el k-refs)
    (let [new-m (read-model el)
          old-m (du/getv el k-model)]
      (when (not= old-m new-m)
        (apply-model! el new-m)))))

;; ── Event handlers ────────────────────────────────────────────────────────
(defn- on-input-focus [^js el ^js _evt]
  (when-let [refs (du/getv el k-refs)]
    (let [^js input-el (gobj/get refs rk-input)
          raw-val      (or (du/get-attr el model/attr-value) "")]
      (set! (.-value input-el) raw-val))))

(defn- on-input-input [^js el ^js _evt]
  (when-let [refs (du/getv el k-refs)]
    (let [^js input-el (gobj/get refs rk-input)
          value        (.-value input-el)
          prev-value   (or (du/get-attr el model/attr-value) "")
          name         (or (du/get-attr el model/attr-name) "")]
      (if (du/dispatch-cancelable! el model/event-change-request
                                   #js {:name name :value value :previousValue prev-value})
        (do
          (when-let [^js internals (du/getv el k-internals)]
            (.setFormValue internals value)
            (sync-validity! el internals input-el value))
          (du/dispatch! el model/event-input #js {:name name :value value}))
        (set! (.-value input-el) prev-value)))))

(defn- on-input-change [^js el ^js _evt]
  (when-let [refs (du/getv el k-refs)]
    (let [^js input-el (gobj/get refs rk-input)
          raw          (.-value input-el)
          num          (js/parseFloat raw)
          canonical    (if (js/isNaN num) raw (str num))
          prev-value   (or (du/get-attr el model/attr-value) "")
          name         (or (du/get-attr el model/attr-name) "")]
      (if (du/dispatch-cancelable! el model/event-change-request
                                   #js {:name name :value canonical :previousValue prev-value})
        (do
          (du/set-attr! el model/attr-value canonical)
          (when-let [^js internals (du/getv el k-internals)]
            (.setFormValue internals canonical)
            (sync-validity! el internals input-el canonical))
          (du/dispatch! el model/event-change #js {:name name :value canonical}))
        (set! (.-value input-el) prev-value)))))

;; ── Listener management ───────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js input-el (gobj/get refs rk-input)
          focus-h      (fn handle-input-focus  [e] (on-input-focus  el e))
          input-h      (fn handle-input-input  [e] (on-input-input  el e))
          change-h     (fn handle-input-change [e] (on-input-change el e))
          handlers     #js {}]
      (.addEventListener input-el ev-focus  focus-h)
      (.addEventListener input-el ev-input  input-h)
      (.addEventListener input-el ev-change change-h)
      (gobj/set handlers hk-focus  focus-h)
      (gobj/set handlers hk-input  input-h)
      (gobj/set handlers hk-change change-h)
      (du/setv! el k-handlers handlers))))

(defn- remove-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (let [^js input-el (gobj/get refs rk-input)]
        (.removeEventListener input-el ev-focus  (gobj/get handlers hk-focus))
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
  ;; Push value attr to input (formatted) on connect so the displayed value
  ;; matches the attribute before any focus/blur cycle.
  (when-let [refs (du/getv el k-refs)]
    (let [^js input-el (gobj/get refs rk-input)
          val-attr     (du/get-attr el model/attr-value)
          currency     (model/normalize-currency (du/get-attr el model/attr-currency))
          locale       (or (du/get-attr el model/attr-locale) "")]
      (when val-attr
        (set! (.-value input-el) (format-display val-attr currency locale)))))
  (when-let [^js internals (du/getv el k-internals)]
    (.setFormValue internals (or (du/get-attr el model/attr-value) "")))
  (add-listeners! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el attr-name old-val new-val]
  (when (not= old-val new-val)
    ;; Special-case `value`: update the displayed (formatted) value in the
    ;; input only when not focused, so external attribute writes don't clobber
    ;; an in-progress edit.
    (when (= attr-name model/attr-value)
      (when-let [refs (du/getv el k-refs)]
        (let [^js input-el (gobj/get refs rk-input)]
          (when-not (is-focused? el input-el)
            (let [currency (model/normalize-currency (du/get-attr el model/attr-currency))
                  locale   (or (du/get-attr el model/attr-locale) "")]
              (set! (.-value input-el)
                    (if (or (nil? new-val) (= new-val ""))
                      ""
                      (format-display new-val currency locale))))))))
    (update-from-attrs! el)))

;; ── Property accessors ────────────────────────────────────────────────────
;; Special value property: getter returns the raw attribute value, setter
;; writes the attribute and syncs the input display when not focused.
(defn- define-value-prop! [^js proto]
  (.defineProperty
   js/Object proto "value"
   #js {:configurable true
        :enumerable   true
        :get (fn xcf-get-value []
               (this-as ^js this
                 (or (du/get-attr this model/attr-value) "")))
        :set (fn xcf-set-value [v]
               (this-as ^js this
                 (let [str-v (if (and (some? v) (not= v js/undefined)) (str v) "")]
                   (du/set-attr! this model/attr-value str-v)
                   (when-let [refs (du/getv this k-refs)]
                     (let [^js input-el (gobj/get refs rk-input)
                           currency     (model/normalize-currency
                                         (du/get-attr this model/attr-currency))
                           locale       (or (du/get-attr this model/attr-locale) "")]
                       (when-not (is-focused? this input-el)
                         (set! (.-value input-el)
                               (if (= str-v "") ""
                                   (format-display str-v currency locale)))))))))}))

(defn- install-validity-methods! [^js proto]
  ;; .defineProperty with a :value descriptor — same Tier-2 idiom adopted in
  ;; x-cancel-dialogue, x-alert, x-collapse, x-combobox, x-form. Bare aset on
  ;; the prototype was audited out in PR #155.
  (.defineProperty js/Object proto "checkValidity"
                   #js {:value (fn xcf-check-validity []
                                 (this-as ^js this
                                   (if-let [^js internals (du/getv this k-internals)]
                                     (.checkValidity internals)
                                     true)))
                        :writable true :configurable true})
  (.defineProperty js/Object proto "reportValidity"
                   #js {:value (fn xcf-report-validity []
                                 (this-as ^js this
                                   (if-let [^js internals (du/getv this k-internals)]
                                     (.reportValidity internals)
                                     true)))
                        :writable true :configurable true}))

(defn- install-property-accessors! [^js proto]
  (define-value-prop! proto)
  (du/define-string-prop! proto "name"        model/attr-name "")
  (du/define-string-prop! proto "currency"    model/attr-currency "")
  (du/define-string-prop! proto "locale"      model/attr-locale "")
  (du/define-string-prop! proto "min"         model/attr-min "")
  (du/define-string-prop! proto "max"         model/attr-max "")
  (du/define-string-prop! proto "placeholder" model/attr-placeholder "")
  (du/define-string-prop! proto "label"       model/attr-label "")
  (du/define-string-prop! proto "hint"        model/attr-hint "")
  (du/define-string-prop! proto "error"       model/attr-error "")
  (du/define-bool-prop! proto "disabled" model/attr-disabled)
  (du/define-bool-prop! proto "readOnly" model/attr-readonly)
  (du/define-bool-prop! proto "required" model/attr-required)
  (install-validity-methods! proto))

;; ── Public API ────────────────────────────────────────────────────────────
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
