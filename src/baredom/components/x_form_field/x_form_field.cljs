(ns baredom.components.x-form-field.x-form-field
  (:require [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-form-field.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: access via gobj/get / gobj/set)
;; ---------------------------------------------------------------------------
(def ^:private k-refs      "__xFormFieldRefs")
(def ^:private k-internals "__xFormFieldInternals")
(def ^:private k-handlers  "__xFormFieldHandlers")

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
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
   "[part=field]{"
   "display:flex;"
   "flex-direction:column;"
   "gap:0.25rem;"
   "}"
   "[part=label]{"
   "display:block;"
   "font-size:var(--x-form-field-label-font-size);"
   "font-weight:500;"
   "color:var(--x-form-field-label-color);"
   "}"
   ".label-hidden{"
   "display:none;"
   "}"
   "[part=input-wrapper]{"
   "display:block;"
   "}"
   "[part=input]{"
   "display:block;"
   "width:100%;"
   "box-sizing:border-box;"
   "padding:var(--x-form-field-input-padding);"
   "background:var(--x-form-field-input-bg);"
   "color:var(--x-form-field-input-color);"
   "border:var(--x-form-field-input-border);"
   "border-radius:var(--x-form-field-input-border-radius);"
   "font-size:1rem;"
   "line-height:1.5;"
   "font-family:inherit;"
   "outline:none;"
   "transition:border-color 120ms ease,box-shadow 120ms ease;"
   "}"
   "[part=input]:focus{"
   "border-color:var(--x-form-field-focus-ring-color);"
   "box-shadow:0 0 0 3px color-mix(in srgb,var(--x-form-field-focus-ring-color) 20%,transparent);"
   "}"
   ":host([data-invalid]) [part=input]{"
   "border-color:var(--x-form-field-error-color);"
   "}"
   ":host([data-invalid]) [part=input]:focus{"
   "box-shadow:0 0 0 3px color-mix(in srgb,var(--x-form-field-error-color) 20%,transparent);"
   "}"
   "[part=input]:disabled{"
   "opacity:var(--x-form-field-disabled-opacity);"
   "cursor:not-allowed;"
   "}"
   "[part=hint]{"
   "display:block;"
   "font-size:0.8125rem;"
   "color:var(--x-form-field-hint-color);"
   "}"
   ".hint-hidden{"
   "display:none;"
   "}"
   "[part=error]{"
   "display:block;"
   "font-size:0.8125rem;"
   "color:var(--x-form-field-error-color);"
   "}"
   ".error-hidden{"
   "display:none;"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=input]{transition:none;}"
   "}"))

;; ---------------------------------------------------------------------------
;; DOM helpers
;; ---------------------------------------------------------------------------
(defn- make-el [tag] (.createElement js/document tag))

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (make-el "style")
        field-el   (make-el "div")
        label-el   (make-el "label")
        wrapper-el (make-el "div")
        input-el   (make-el "input")
        hint-el    (make-el "span")
        error-el   (make-el "span")]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! field-el   "part" "field")
    (du/set-attr! label-el   "part" "label")
    (du/set-attr! label-el   "id"   "label")
    (du/set-attr! label-el   "for"  "input")
    (du/set-attr! wrapper-el "part" "input-wrapper")
    (du/set-attr! input-el   "part" "input")
    (du/set-attr! input-el   "id"   "input")
    (du/set-attr! input-el   "aria-labelledby" "label")
    (du/set-attr! hint-el    "part"     "hint")
    (du/set-attr! hint-el    "id"       "hint")
    (du/set-attr! hint-el    "aria-live" "polite")
    (du/set-attr! error-el   "part"     "error")
    (du/set-attr! error-el   "id"       "error")
    (du/set-attr! error-el   "role"     "alert")
    (du/set-attr! error-el   "aria-live" "assertive")

    (.appendChild wrapper-el input-el)
    (.appendChild field-el label-el)
    (.appendChild field-el wrapper-el)
    (.appendChild field-el hint-el)
    (.appendChild field-el error-el)
    (.appendChild root style-el)
    (.appendChild root field-el)

    (let [refs #js {:field  field-el
                    :label  label-el
                    :input  input-el
                    :hint   hint-el
                    :error  error-el}]
      (gobj/set el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Validity sync
;; ---------------------------------------------------------------------------
(defn- sync-validity! [^js el ^js internals ^js input-el]
  (let [has-error? (du/has-attr? el model/attr-error)
        error-msg  (or (du/get-attr el model/attr-error) "")
        required?  (du/has-attr? el model/attr-required)
        value      (.-value input-el)]
    (cond
      has-error?
      (.setValidity internals #js {:customError true} error-msg input-el)
      (and required? (= value ""))
      (.setValidity internals #js {:valueMissing true} "Please fill in this field." input-el)
      :else
      (.setValidity internals #js {} "" input-el))))

;; ---------------------------------------------------------------------------
;; Read element state from attributes
;; ---------------------------------------------------------------------------
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

;; ---------------------------------------------------------------------------
;; Render
;; ---------------------------------------------------------------------------
(defn- render! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js label-el (gobj/get refs "label")
          ^js input-el (gobj/get refs "input")
          ^js hint-el  (gobj/get refs "hint")
          ^js error-el (gobj/get refs "error")
          m            (read-model el)
          has-error?   (:has-error? m)
          has-hint?    (:has-hint? m)
          has-label?   (:has-label? m)]

      ;; Input native properties
      (set! (.-type input-el)         (:type m))
      (set! (.-name input-el)         (:name m))
      (set! (.-placeholder input-el)  (:placeholder m))
      (set! (.-disabled input-el)     (:disabled? m))
      (set! (.-readOnly input-el)     (:readonly? m))
      (set! (.-required input-el)     (:required? m))
      (set! (.-autocomplete input-el) (:autocomplete m))

      ;; ARIA on input
      (du/set-attr! input-el "aria-required" (if (:required? m) "true" "false"))
      (du/set-attr! input-el "aria-invalid"  (if has-error? "true" "false"))

      ;; aria-describedby conditionally includes hint and/or error ids
      (let [describedby (cond
                          (and has-hint? has-error?) "hint error"
                          has-hint?                  "hint"
                          has-error?                 "error"
                          :else                      nil)]
        (if describedby
          (du/set-attr! input-el "aria-describedby" describedby)
          (du/remove-attr! input-el "aria-describedby")))

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
      (set! (.-textContent error-el) (:error m))
      (if has-error?
        (.remove (.-classList error-el) "error-hidden")
        (.add (.-classList error-el) "error-hidden"))

      ;; Data attribute on host for CSS hook
      (du/set-bool-attr! el "data-invalid" has-error?)

      ;; ElementInternals validity
      (when-let [^js internals (gobj/get el k-internals)]
        (sync-validity! el internals input-el)))))

;; ---------------------------------------------------------------------------
;; Event dispatch
;; ---------------------------------------------------------------------------
;; ---------------------------------------------------------------------------
;; Event handlers
;; ---------------------------------------------------------------------------
(defn- make-input-handler [^js el]
  (fn [^js _evt]
    (when-let [refs (gobj/get el k-refs)]
      (let [^js input-el (gobj/get refs "input")
            value        (.-value input-el)
            name         (or (du/get-attr el model/attr-name) "")]
        (when-let [^js internals (gobj/get el k-internals)]
          (.setFormValue internals value)
          (sync-validity! el internals input-el))
        (du/dispatch! el model/event-input #js {:name name :value value})))))

(defn- make-change-handler [^js el]
  (fn [^js _evt]
    (when-let [refs (gobj/get el k-refs)]
      (let [^js input-el (gobj/get refs "input")
            value        (.-value input-el)
            name         (or (du/get-attr el model/attr-name) "")]
        (when-let [^js internals (gobj/get el k-internals)]
          (.setFormValue internals value)
          (sync-validity! el internals input-el))
        (du/dispatch! el model/event-change #js {:name name :value value})))))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- add-listeners! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js input-el (gobj/get refs "input")
          input-h      (make-input-handler el)
          change-h     (make-change-handler el)]
      (.addEventListener input-el "input"  input-h)
      (.addEventListener input-el "change" change-h)
      (gobj/set el k-handlers #js {:input input-h :change change-h}))))

(defn- remove-listeners! [^js el]
  (when-let [refs     (gobj/get el k-refs)]
    (when-let [handlers (gobj/get el k-handlers)]
      (let [^js input-el (gobj/get refs "input")]
        (.removeEventListener input-el "input"  (gobj/get handlers "input"))
        (.removeEventListener input-el "change" (gobj/get handlers "change")))
      (gobj/set el k-handlers nil))))

;; ---------------------------------------------------------------------------
;; Form-associated callbacks
;; ---------------------------------------------------------------------------
(defn- form-disabled! [^js el disabled?]
  (du/set-bool-attr! el model/attr-disabled (boolean disabled?))
  (render! el))

(defn- form-reset! [^js el]
  (du/remove-attr! el model/attr-value)
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
  ;; Push value attr to input if set
  (when-let [refs (gobj/get el k-refs)]
    (let [^js input-el (gobj/get refs "input")
          val-attr     (du/get-attr el model/attr-value)]
      (when val-attr
        (set! (.-value input-el) val-attr))))
  ;; Set initial form value
  (when-let [^js internals (gobj/get el k-internals)]
    (.setFormValue internals (or (du/get-attr el model/attr-value) "")))
  (add-listeners! el)
  (render! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el name _old new-val]
  ;; For value attr: sync to input.value only if it differs (avoids cursor jump on typing)
  (when (= name model/attr-value)
    (when-let [refs (gobj/get el k-refs)]
      (let [^js input-el (gobj/get refs "input")]
        (when (not= (.-value input-el) new-val)
          (set! (.-value input-el) (or new-val ""))))))
  (render! el))

;; ---------------------------------------------------------------------------
;; Property helpers
;; ---------------------------------------------------------------------------
(defn- define-string-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (or (du/get-attr this attr-name) "")))
        :set (fn [v] (this-as ^js this
                              (if (and (some? v) (not= v js/undefined))
                                (du/set-attr! this attr-name (str v))
                                (du/remove-attr! this attr-name))))}))

(defn- define-bool-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (du/has-attr? this attr-name)))
        :set (fn [v] (this-as ^js this (du/set-bool-attr! this attr-name (boolean v))))}))

;; Special value property: also syncs input.value when set
(defn- define-value-prop! [^js proto]
  (.defineProperty
   js/Object proto "value"
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this
                             (if-let [refs (gobj/get this k-refs)]
                               (.-value (gobj/get refs "input"))
                               (or (du/get-attr this model/attr-value) ""))))
        :set (fn [v] (this-as ^js this
                              (let [str-v (if (and (some? v) (not= v js/undefined)) (str v) "")]
                                (du/set-attr! this model/attr-value str-v)
                                (when-let [refs (gobj/get this k-refs)]
                                  (let [^js input-el (gobj/get refs "input")]
                                    (set! (.-value input-el) str-v))))))}))

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

    ;; Value property (special: also syncs input.value)
    (define-value-prop! proto)

    ;; String properties
    (define-string-prop! proto "label"        model/attr-label)
    (define-string-prop! proto "type"         model/attr-type)
    (define-string-prop! proto "name"         model/attr-name)
    (define-string-prop! proto "placeholder"  model/attr-placeholder)
    (define-string-prop! proto "autocomplete" model/attr-autocomplete)

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

    cls))

(defn init! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class))))
