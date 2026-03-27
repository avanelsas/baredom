(ns baredom.components.x-currency-field.x-currency-field
  (:require [goog.object :as gobj]
            [baredom.components.x-currency-field.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: access via gobj/get / gobj/set)
;; ---------------------------------------------------------------------------
(def ^:private k-refs      "__xCurrencyFieldRefs")
(def ^:private k-internals "__xCurrencyFieldInternals")
(def ^:private k-handlers  "__xCurrencyFieldHandlers")

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-currency-field-bg:#ffffff;"
   "--x-currency-field-color:#111827;"
   "--x-currency-field-border:1px solid #d1d5db;"
   "--x-currency-field-border-radius:6px;"
   "--x-currency-field-focus-ring-color:#2563eb;"
   "--x-currency-field-symbol-color:#6b7280;"
   "--x-currency-field-label-color:#374151;"
   "--x-currency-field-hint-color:#6b7280;"
   "--x-currency-field-error-color:#dc2626;"
   "--x-currency-field-disabled-opacity:0.45;"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-currency-field-bg:#1f2937;"
   "--x-currency-field-color:#f9fafb;"
   "--x-currency-field-border:1px solid #4b5563;"
   "--x-currency-field-focus-ring-color:#3b82f6;"
   "--x-currency-field-symbol-color:#9ca3af;"
   "--x-currency-field-label-color:#d1d5db;"
   "--x-currency-field-hint-color:#9ca3af;"
   "--x-currency-field-error-color:#f87171;"
   "}"
   "}"
   "[part=field]{"
   "display:flex;"
   "flex-direction:column;"
   "gap:0.25rem;"
   "}"
   "[part=label]{"
   "display:block;"
   "font-size:0.875rem;"
   "font-weight:500;"
   "color:var(--x-currency-field-label-color);"
   "}"
   ".label-hidden{"
   "display:none;"
   "}"
   "[part=input-wrapper]{"
   "display:flex;"
   "align-items:center;"
   "background:var(--x-currency-field-bg);"
   "border:var(--x-currency-field-border);"
   "border-radius:var(--x-currency-field-border-radius);"
   "transition:border-color 120ms ease,box-shadow 120ms ease;"
   "}"
   "[part=input-wrapper]:focus-within{"
   "border-color:var(--x-currency-field-focus-ring-color);"
   "box-shadow:0 0 0 3px color-mix(in srgb,var(--x-currency-field-focus-ring-color) 20%,transparent);"
   "}"
   ":host([data-invalid]) [part=input-wrapper]{"
   "border-color:var(--x-currency-field-error-color);"
   "}"
   ":host([data-invalid]) [part=input-wrapper]:focus-within{"
   "box-shadow:0 0 0 3px color-mix(in srgb,var(--x-currency-field-error-color) 20%,transparent);"
   "}"
   "[part=symbol]{"
   "flex-shrink:0;"
   "padding:0.5rem 0 0.5rem 0.75rem;"
   "font-size:1rem;"
   "color:var(--x-currency-field-symbol-color);"
   "user-select:none;"
   "pointer-events:none;"
   "}"
   "[part=input]{"
   "flex:1;"
   "min-width:0;"
   "padding:0.5rem 0.75rem;"
   "background:transparent;"
   "color:var(--x-currency-field-color);"
   "border:none;"
   "outline:none;"
   "font-size:1rem;"
   "line-height:1.5;"
   "font-family:inherit;"
   "}"
   "[part=input]:disabled{"
   "opacity:1;"
   "cursor:not-allowed;"
   "}"
   ":host([disabled]) [part=input-wrapper]{"
   "opacity:var(--x-currency-field-disabled-opacity);"
   "cursor:not-allowed;"
   "}"
   "[part=hint]{"
   "display:block;"
   "font-size:0.8125rem;"
   "color:var(--x-currency-field-hint-color);"
   "}"
   ".hint-hidden{"
   "display:none;"
   "}"
   "[part=error]{"
   "display:block;"
   "font-size:0.8125rem;"
   "color:var(--x-currency-field-error-color);"
   "}"
   ".error-hidden{"
   "display:none;"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=input-wrapper]{transition:none;}"
   "}"))

;; ---------------------------------------------------------------------------
;; DOM helpers
;; ---------------------------------------------------------------------------
(defn- make-el [tag] (.createElement js/document tag))

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
;; Intl helpers
;; ---------------------------------------------------------------------------
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

(defn- format-display [value currency locale]
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

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (make-el "style")
        field-el   (make-el "div")
        label-el   (make-el "label")
        wrapper-el (make-el "div")
        symbol-el  (make-el "span")
        input-el   (make-el "input")
        hint-el    (make-el "span")
        error-el   (make-el "span")]

    (set! (.-textContent style-el) style-text)

    (set-attr! field-el   "part" "field")
    (set-attr! label-el   "part" "label")
    (set-attr! label-el   "id"   "label")
    (set-attr! label-el   "for"  "input")
    (set-attr! wrapper-el "part" "input-wrapper")
    (set-attr! symbol-el  "part" "symbol")
    (set-attr! symbol-el  "aria-hidden" "true")
    (set-attr! input-el   "part" "input")
    (set-attr! input-el   "id"   "input")
    (set-attr! input-el   "inputmode" "decimal")
    (set-attr! input-el   "aria-labelledby" "label")
    (set-attr! hint-el    "part"     "hint")
    (set-attr! hint-el    "id"       "hint")
    (set-attr! hint-el    "aria-live" "polite")
    (set-attr! error-el   "part"     "error")
    (set-attr! error-el   "id"       "error")
    (set-attr! error-el   "role"     "alert")
    (set-attr! error-el   "aria-live" "assertive")

    (.appendChild wrapper-el symbol-el)
    (.appendChild wrapper-el input-el)
    (.appendChild field-el label-el)
    (.appendChild field-el wrapper-el)
    (.appendChild field-el hint-el)
    (.appendChild field-el error-el)
    (.appendChild root style-el)
    (.appendChild root field-el)

    (let [refs #js {:field   field-el
                    :label   label-el
                    :wrapper wrapper-el
                    :symbol  symbol-el
                    :input   input-el
                    :hint    hint-el
                    :error   error-el}]
      (gobj/set el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Validity sync
;; ---------------------------------------------------------------------------
(defn- sync-validity! [^js el ^js internals ^js input-el value]
  (let [has-error? (has-attr? el model/attr-error)
        error-msg  (or (get-attr el model/attr-error) "")
        required?  (has-attr? el model/attr-required)
        min-raw    (get-attr el model/attr-min)
        max-raw    (get-attr el model/attr-max)
        num        (js/parseFloat value)
        has-num?   (not (js/isNaN num))
        min-num    (when min-raw (js/parseFloat min-raw))
        max-num    (when max-raw (js/parseFloat max-raw))]
    (cond
      has-error?
      (.setValidity internals #js {:customError true} error-msg input-el)

      (and required? (= value ""))
      (.setValidity internals #js {:valueMissing true} "Please fill in this field." input-el)

      (and (not= value "") (not has-num?))
      (.setValidity internals #js {:badInput true} "Please enter a valid number." input-el)

      (and has-num? min-num (not (js/isNaN min-num)) (< num min-num))
      (.setValidity internals #js {:rangeUnderflow true}
                   (str "Value must be at least " min-num ".") input-el)

      (and has-num? max-num (not (js/isNaN max-num)) (> num max-num))
      (.setValidity internals #js {:rangeOverflow true}
                   (str "Value must be at most " max-num ".") input-el)

      :else
      (.setValidity internals #js {} "" input-el))))

;; ---------------------------------------------------------------------------
;; Read element state from attributes
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/normalize
   {:name-raw          (get-attr el model/attr-name)
    :value-raw         (get-attr el model/attr-value)
    :currency-raw      (get-attr el model/attr-currency)
    :locale-raw        (get-attr el model/attr-locale)
    :min-raw           (get-attr el model/attr-min)
    :max-raw           (get-attr el model/attr-max)
    :placeholder-raw   (get-attr el model/attr-placeholder)
    :label-raw         (get-attr el model/attr-label)
    :hint-raw          (get-attr el model/attr-hint)
    :error-raw         (get-attr el model/attr-error)
    :disabled-present? (has-attr? el model/attr-disabled)
    :required-present? (has-attr? el model/attr-required)
    :readonly-present? (has-attr? el model/attr-readonly)}))

;; ---------------------------------------------------------------------------
;; Render
;; ---------------------------------------------------------------------------
(defn- render! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js label-el  (gobj/get refs "label")
          ^js symbol-el (gobj/get refs "symbol")
          ^js input-el  (gobj/get refs "input")
          ^js hint-el   (gobj/get refs "hint")
          ^js error-el  (gobj/get refs "error")
          m               (read-model el)
          custom-error?   (:has-error? m)
          computed-msg    (when-not custom-error? (model/validation-message m))
          has-error?      (or custom-error? (not= (or computed-msg "") ""))
          display-error   (if custom-error? (:error m) (or computed-msg ""))
          has-hint?       (:has-hint? m)
          has-label?      (:has-label? m)
          currency        (:currency m)
          locale          (:locale m)]

      ;; Symbol
      (set! (.-textContent symbol-el) (currency-symbol currency locale))

      ;; Input native properties
      (set! (.-name input-el)        (:name m))
      (set! (.-placeholder input-el) (:placeholder m))
      (set! (.-disabled input-el)    (:disabled? m))
      (set! (.-readOnly input-el)    (:readonly? m))
      (set! (.-required input-el)    (:required? m))

      ;; ARIA on input
      (set-attr! input-el "aria-required" (if (:required? m) "true" "false"))
      (set-attr! input-el "aria-invalid"  (if has-error? "true" "false"))

      ;; aria-describedby
      (let [describedby (cond
                          (and has-hint? has-error?) "hint error"
                          has-hint?                  "hint"
                          has-error?                 "error"
                          :else                      nil)]
        (if describedby
          (set-attr! input-el "aria-describedby" describedby)
          (remove-attr! input-el "aria-describedby")))

      ;; Input value: only update when not focused (avoid clobbering edit)
      (when-not (is-focused? el input-el)
        (let [raw-val (:value m)]
          (set! (.-value input-el)
                (if (= raw-val "") "" (format-display raw-val currency locale)))))

      ;; Label
      (set! (.-textContent label-el) (:label m))
      (if has-label?
        (.remove (.-classList label-el) "label-hidden")
        (.add (.-classList label-el) "label-hidden"))

      ;; Hint
      (set! (.-textContent hint-el) (:hint m))
      (if has-hint?
        (.remove (.-classList hint-el) "hint-hidden")
        (.add (.-classList hint-el) "hint-hidden"))

      ;; Error
      (set! (.-textContent error-el) display-error)
      (if has-error?
        (.remove (.-classList error-el) "error-hidden")
        (.add (.-classList error-el) "error-hidden"))

      ;; Data attribute on host for CSS hook
      (set-bool-attr! el "data-invalid" has-error?)

      ;; ElementInternals validity
      (when-let [^js internals (gobj/get el k-internals)]
        (sync-validity! el internals input-el (or (get-attr el model/attr-value) ""))))))

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
(defn- make-focus-handler [^js el]
  (fn [^js _evt]
    (when-let [refs (gobj/get el k-refs)]
      (let [^js input-el (gobj/get refs "input")
            raw-val      (or (get-attr el model/attr-value) "")]
        (set! (.-value input-el) raw-val)))))

(defn- make-input-handler [^js el]
  (fn [^js _evt]
    (when-let [refs (gobj/get el k-refs)]
      (let [^js input-el (gobj/get refs "input")
            value        (.-value input-el)
            name         (or (get-attr el model/attr-name) "")]
        (when-let [^js internals (gobj/get el k-internals)]
          (.setFormValue internals value)
          (sync-validity! el internals input-el value))
        (dispatch! el model/event-input #js {:name name :value value})))))

(defn- make-change-handler [^js el]
  (fn [^js _evt]
    (when-let [refs (gobj/get el k-refs)]
      (let [^js input-el (gobj/get refs "input")
            raw          (.-value input-el)
            num          (js/parseFloat raw)
            canonical    (if (js/isNaN num) raw (str num))
            name         (or (get-attr el model/attr-name) "")]
        ;; Update value attribute — triggers attribute-changed! → render! (reformats display)
        (set-attr! el model/attr-value canonical)
        (when-let [^js internals (gobj/get el k-internals)]
          (.setFormValue internals canonical)
          (sync-validity! el internals input-el canonical))
        (dispatch! el model/event-change #js {:name name :value canonical})))))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- add-listeners! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js input-el (gobj/get refs "input")
          focus-h      (make-focus-handler el)
          input-h      (make-input-handler el)
          change-h     (make-change-handler el)]
      (.addEventListener input-el "focus"  focus-h)
      (.addEventListener input-el "input"  input-h)
      (.addEventListener input-el "change" change-h)
      (gobj/set el k-handlers #js {:focus focus-h :input input-h :change change-h}))))

(defn- remove-listeners! [^js el]
  (when-let [refs     (gobj/get el k-refs)]
    (when-let [handlers (gobj/get el k-handlers)]
      (let [^js input-el (gobj/get refs "input")]
        (.removeEventListener input-el "focus"  (gobj/get handlers "focus"))
        (.removeEventListener input-el "input"  (gobj/get handlers "input"))
        (.removeEventListener input-el "change" (gobj/get handlers "change")))
      (gobj/set el k-handlers nil))))

;; ---------------------------------------------------------------------------
;; Form-associated callbacks
;; ---------------------------------------------------------------------------
(defn- form-disabled! [^js el disabled?]
  (set-bool-attr! el model/attr-disabled (boolean disabled?))
  (render! el))

(defn- form-reset! [^js el]
  (remove-attr! el model/attr-value)
  (when-let [refs (gobj/get el k-refs)]
    (let [^js input-el (gobj/get refs "input")]
      (set! (.-value input-el) "")))
  (when-let [^js internals (gobj/get el k-internals)]
    (.setFormValue internals ""))
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
  ;; Push value attr to input (formatted) on connect
  (when-let [refs (gobj/get el k-refs)]
    (let [^js input-el (gobj/get refs "input")
          val-attr     (get-attr el model/attr-value)
          currency     (model/normalize-currency (get-attr el model/attr-currency))
          locale       (or (get-attr el model/attr-locale) "")]
      (when val-attr
        (set! (.-value input-el) (format-display val-attr currency locale)))))
  ;; Set initial form value
  (when-let [^js internals (gobj/get el k-internals)]
    (.setFormValue internals (or (get-attr el model/attr-value) "")))
  (add-listeners! el)
  (render! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el name _old new-val]
  ;; For value attr: when not focused, update display in input
  (when (= name model/attr-value)
    (when-let [refs (gobj/get el k-refs)]
      (let [^js input-el (gobj/get refs "input")]
        (when-not (is-focused? el input-el)
          (let [currency (model/normalize-currency (get-attr el model/attr-currency))
                locale   (or (get-attr el model/attr-locale) "")]
            (set! (.-value input-el)
                  (if (or (nil? new-val) (= new-val ""))
                    ""
                    (format-display new-val currency locale))))))))
  (render! el))

;; ---------------------------------------------------------------------------
;; Property helpers
;; ---------------------------------------------------------------------------
(defn- define-string-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (or (get-attr this attr-name) "")))
        :set (fn [v] (this-as ^js this
                              (if (and (some? v) (not= v js/undefined))
                                (set-attr! this attr-name (str v))
                                (remove-attr! this attr-name))))}))

(defn- define-bool-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (has-attr? this attr-name)))
        :set (fn [v] (this-as ^js this (set-bool-attr! this attr-name (boolean v))))}))

;; Special value property: getter returns raw value (from input or attribute)
(defn- define-value-prop! [^js proto]
  (.defineProperty
   js/Object proto "value"
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this
                             (or (get-attr this model/attr-value) "")))
        :set (fn [v] (this-as ^js this
                              (let [str-v (if (and (some? v) (not= v js/undefined)) (str v) "")]
                                (set-attr! this model/attr-value str-v)
                                (when-let [refs (gobj/get this k-refs)]
                                  (let [^js input-el (gobj/get refs "input")
                                        currency     (model/normalize-currency
                                                      (get-attr this model/attr-currency))
                                        locale       (or (get-attr this model/attr-locale) "")]
                                    (when-not (is-focused? this input-el)
                                      (set! (.-value input-el)
                                            (if (= str-v "") ""
                                                (format-display str-v currency locale)))))))))}))

;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------
(defn- element-class []
  (let [cls   (js* "(class extends HTMLElement {})")
        proto (.-prototype cls)]

    ;; Form-associated
    (set! (.-formAssociated cls) true)

    ;; observedAttributes
    (.defineProperty js/Object cls "observedAttributes"
                     #js {:get (fn [] model/observed-attributes)})

    ;; Value property (special)
    (define-value-prop! proto)

    ;; String properties
    (define-string-prop! proto "name"        model/attr-name)
    (define-string-prop! proto "currency"    model/attr-currency)
    (define-string-prop! proto "locale"      model/attr-locale)
    (define-string-prop! proto "min"         model/attr-min)
    (define-string-prop! proto "max"         model/attr-max)
    (define-string-prop! proto "placeholder" model/attr-placeholder)
    (define-string-prop! proto "label"       model/attr-label)
    (define-string-prop! proto "hint"        model/attr-hint)
    (define-string-prop! proto "error"       model/attr-error)

    ;; Boolean properties
    (define-bool-prop! proto "disabled" model/attr-disabled)
    (define-bool-prop! proto "readOnly" model/attr-readonly)
    (define-bool-prop! proto "required" model/attr-required)

    ;; Lifecycle
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

    ;; Form-validity delegation (not auto-delegated in all environments)
    (aset proto "checkValidity"
          (fn [] (this-as ^js this
                          (if-let [^js internals (gobj/get this k-internals)]
                            (.checkValidity internals)
                            true))))

    (aset proto "reportValidity"
          (fn [] (this-as ^js this
                          (if-let [^js internals (gobj/get this k-internals)]
                            (.reportValidity internals)
                            true))))

    cls))

(defn init! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class))))
