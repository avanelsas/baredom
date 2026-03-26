(ns app.components.x-text-area.x-text-area
  (:require [goog.object :as gobj]
            [app.components.x-text-area.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: access via gobj/get / gobj/set)
;; ---------------------------------------------------------------------------
(def ^:private k-refs      "__xTextAreaRefs")
(def ^:private k-internals "__xTextAreaInternals")
(def ^:private k-handlers  "__xTextAreaHandlers")

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-text-area-label-color:#374151;"
   "--x-text-area-label-font-size:0.875rem;"
   "--x-text-area-bg:#ffffff;"
   "--x-text-area-color:#111827;"
   "--x-text-area-border:1px solid #d1d5db;"
   "--x-text-area-border-radius:6px;"
   "--x-text-area-padding:0.5rem 0.75rem;"
   "--x-text-area-focus-ring-color:#2563eb;"
   "--x-text-area-error-color:#dc2626;"
   "--x-text-area-hint-color:#6b7280;"
   "--x-text-area-disabled-opacity:0.45;"
   "--x-text-area-min-height:5rem;"
   "--x-text-area-font-size:1rem;"
   "--x-text-area-resize:vertical;"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-text-area-label-color:#d1d5db;"
   "--x-text-area-bg:#1f2937;"
   "--x-text-area-color:#f9fafb;"
   "--x-text-area-border:1px solid #4b5563;"
   "--x-text-area-focus-ring-color:#3b82f6;"
   "--x-text-area-error-color:#f87171;"
   "--x-text-area-hint-color:#9ca3af;"
   "}"
   "}"
   "[part=field]{"
   "display:flex;"
   "flex-direction:column;"
   "gap:0.25rem;"
   "}"
   "[part=label]{"
   "display:block;"
   "font-size:var(--x-text-area-label-font-size);"
   "font-weight:500;"
   "color:var(--x-text-area-label-color);"
   "}"
   ".label-hidden{"
   "display:none;"
   "}"
   "[part=textarea-wrapper]{"
   "display:block;"
   "}"
   "[part=textarea]{"
   "display:block;"
   "width:100%;"
   "box-sizing:border-box;"
   "min-height:var(--x-text-area-min-height);"
   "padding:var(--x-text-area-padding);"
   "background:var(--x-text-area-bg);"
   "color:var(--x-text-area-color);"
   "border:var(--x-text-area-border);"
   "border-radius:var(--x-text-area-border-radius);"
   "font-size:var(--x-text-area-font-size);"
   "line-height:1.5;"
   "font-family:inherit;"
   "outline:none;"
   "resize:var(--x-text-area-resize);"
   "transition:border-color 120ms ease,box-shadow 120ms ease;"
   "}"
   "[part=textarea]:focus{"
   "border-color:var(--x-text-area-focus-ring-color);"
   "box-shadow:0 0 0 3px color-mix(in srgb,var(--x-text-area-focus-ring-color) 20%,transparent);"
   "}"
   ":host([data-invalid]) [part=textarea]{"
   "border-color:var(--x-text-area-error-color);"
   "}"
   ":host([data-invalid]) [part=textarea]:focus{"
   "box-shadow:0 0 0 3px color-mix(in srgb,var(--x-text-area-error-color) 20%,transparent);"
   "}"
   "[part=textarea]:disabled{"
   "opacity:var(--x-text-area-disabled-opacity);"
   "cursor:not-allowed;"
   "}"
   "[part=hint]{"
   "display:block;"
   "font-size:0.8125rem;"
   "color:var(--x-text-area-hint-color);"
   "}"
   ".hint-hidden{"
   "display:none;"
   "}"
   "[part=error]{"
   "display:block;"
   "font-size:0.8125rem;"
   "color:var(--x-text-area-error-color);"
   "}"
   ".error-hidden{"
   "display:none;"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=textarea]{transition:none;}"
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
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root        (.attachShadow el #js {:mode "open"})
        style-el    (make-el "style")
        field-el    (make-el "div")
        label-el    (make-el "label")
        wrapper-el  (make-el "div")
        textarea-el (make-el "textarea")
        hint-el     (make-el "span")
        error-el    (make-el "span")]

    (set! (.-textContent style-el) style-text)

    (set-attr! field-el    "part" "field")
    (set-attr! label-el    "part" "label")
    (set-attr! label-el    "id"   "label")
    (set-attr! label-el    "for"  "textarea")
    (set-attr! wrapper-el  "part" "textarea-wrapper")
    (set-attr! textarea-el "part" "textarea")
    (set-attr! textarea-el "id"   "textarea")
    (set-attr! textarea-el "aria-labelledby" "label")
    (set-attr! hint-el     "part"     "hint")
    (set-attr! hint-el     "id"       "hint")
    (set-attr! hint-el     "aria-live" "polite")
    (set-attr! error-el    "part"     "error")
    (set-attr! error-el    "id"       "error")
    (set-attr! error-el    "role"     "alert")
    (set-attr! error-el    "aria-live" "assertive")

    (.appendChild wrapper-el textarea-el)
    (.appendChild field-el label-el)
    (.appendChild field-el wrapper-el)
    (.appendChild field-el hint-el)
    (.appendChild field-el error-el)
    (.appendChild root style-el)
    (.appendChild root field-el)

    (let [refs #js {:field    field-el
                    :label    label-el
                    :textarea textarea-el
                    :hint     hint-el
                    :error    error-el}]
      (gobj/set el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Validity sync
;; ---------------------------------------------------------------------------
(defn- sync-validity! [^js el ^js internals ^js textarea-el]
  (let [has-error? (has-attr? el model/attr-error)
        error-msg  (or (get-attr el model/attr-error) "")
        required?  (has-attr? el model/attr-required)
        value      (.-value textarea-el)]
    (cond
      has-error?
      (.setValidity internals #js {:customError true} error-msg textarea-el)
      (and required? (= value ""))
      (.setValidity internals #js {:valueMissing true} "Please fill in this field." textarea-el)
      :else
      (.setValidity internals #js {} "" textarea-el))))

;; ---------------------------------------------------------------------------
;; Read element state from attributes
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/normalize
   {:label-raw         (get-attr el model/attr-label)
    :name-raw          (get-attr el model/attr-name)
    :value-raw         (get-attr el model/attr-value)
    :placeholder-raw   (get-attr el model/attr-placeholder)
    :hint-raw          (get-attr el model/attr-hint)
    :error-raw         (get-attr el model/attr-error)
    :disabled-present? (has-attr? el model/attr-disabled)
    :readonly-present? (has-attr? el model/attr-readonly)
    :required-present? (has-attr? el model/attr-required)
    :rows-raw          (get-attr el model/attr-rows)
    :maxlength-raw     (get-attr el model/attr-maxlength)
    :minlength-raw     (get-attr el model/attr-minlength)
    :autocomplete-raw  (get-attr el model/attr-autocomplete)
    :resize-raw        (get-attr el model/attr-resize)}))

;; ---------------------------------------------------------------------------
;; Render
;; ---------------------------------------------------------------------------
(defn- render! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js textarea-el (gobj/get refs "textarea")
          ^js label-el    (gobj/get refs "label")
          ^js hint-el     (gobj/get refs "hint")
          ^js error-el    (gobj/get refs "error")
          m               (read-model el)
          has-error?      (:has-error? m)
          has-hint?       (:has-hint? m)
          has-label?      (:has-label? m)]

      ;; Textarea native properties
      (set! (.-name textarea-el)         (:name m))
      (set! (.-placeholder textarea-el)  (:placeholder m))
      (set! (.-disabled textarea-el)     (:disabled? m))
      (set! (.-readOnly textarea-el)     (:readonly? m))
      (set! (.-required textarea-el)     (:required? m))
      (set! (.-rows textarea-el)         (:rows m))
      (set! (.-autocomplete textarea-el) (:autocomplete m))

      ;; Optional integer attributes: maxlength / minlength
      (if-let [maxlen (:maxlength m)]
        (set-attr! textarea-el "maxlength" (str maxlen))
        (remove-attr! textarea-el "maxlength"))
      (if-let [minlen (:minlength m)]
        (set-attr! textarea-el "minlength" (str minlen))
        (remove-attr! textarea-el "minlength"))

      ;; Resize: set CSS custom property on host
      (.setProperty (.-style el) "--x-text-area-resize" (:resize m))

      ;; ARIA on textarea
      (set-attr! textarea-el "aria-required" (if (:required? m) "true" "false"))
      (set-attr! textarea-el "aria-invalid"  (if has-error? "true" "false"))

      ;; aria-describedby conditionally includes hint and/or error ids
      (let [describedby (cond
                          (and has-hint? has-error?) "hint error"
                          has-hint?                  "hint"
                          has-error?                 "error"
                          :else                      nil)]
        (if describedby
          (set-attr! textarea-el "aria-describedby" describedby)
          (remove-attr! textarea-el "aria-describedby")))

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
      (set-bool-attr! el "data-invalid" has-error?)

      ;; ElementInternals validity
      (when-let [^js internals (gobj/get el k-internals)]
        (sync-validity! el internals textarea-el)))))

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
    (when-let [refs (gobj/get el k-refs)]
      (let [^js textarea-el (gobj/get refs "textarea")
            value            (.-value textarea-el)
            name             (or (get-attr el model/attr-name) "")]
        (when-let [^js internals (gobj/get el k-internals)]
          (.setFormValue internals value)
          (sync-validity! el internals textarea-el))
        (dispatch! el model/event-input #js {:name name :value value})))))

(defn- make-change-handler [^js el]
  (fn [^js _evt]
    (when-let [refs (gobj/get el k-refs)]
      (let [^js textarea-el (gobj/get refs "textarea")
            value            (.-value textarea-el)
            name             (or (get-attr el model/attr-name) "")]
        (when-let [^js internals (gobj/get el k-internals)]
          (.setFormValue internals value)
          (sync-validity! el internals textarea-el))
        (dispatch! el model/event-change #js {:name name :value value})))))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- add-listeners! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js textarea-el (gobj/get refs "textarea")
          input-h          (make-input-handler el)
          change-h         (make-change-handler el)]
      (.addEventListener textarea-el "input"  input-h)
      (.addEventListener textarea-el "change" change-h)
      (gobj/set el k-handlers #js {:input input-h :change change-h}))))

(defn- remove-listeners! [^js el]
  (when-let [refs     (gobj/get el k-refs)]
    (when-let [handlers (gobj/get el k-handlers)]
      (let [^js textarea-el (gobj/get refs "textarea")]
        (.removeEventListener textarea-el "input"  (gobj/get handlers "input"))
        (.removeEventListener textarea-el "change" (gobj/get handlers "change")))
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
    (let [^js textarea-el (gobj/get refs "textarea")]
      (set! (.-value textarea-el) "")))
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
  ;; Push value attr to textarea if set
  (when-let [refs (gobj/get el k-refs)]
    (let [^js textarea-el (gobj/get refs "textarea")
          val-attr         (get-attr el model/attr-value)]
      (when val-attr
        (set! (.-value textarea-el) val-attr))))
  ;; Set initial form value
  (when-let [^js internals (gobj/get el k-internals)]
    (.setFormValue internals (or (get-attr el model/attr-value) "")))
  (add-listeners! el)
  (render! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el name _old new-val]
  ;; For value attr: sync to textarea.value only if it differs (avoids cursor jump on typing)
  (when (= name model/attr-value)
    (when-let [refs (gobj/get el k-refs)]
      (let [^js textarea-el (gobj/get refs "textarea")]
        (when (not= (.-value textarea-el) new-val)
          (set! (.-value textarea-el) (or new-val ""))))))
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

(defn- define-int-prop! [^js proto prop-name attr-name default-val]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this
                             (or (model/parse-positive-int (get-attr this attr-name))
                                 default-val)))
        :set (fn [v] (this-as ^js this
                              (if (and (some? v) (not= v js/undefined) (pos? v))
                                (set-attr! this attr-name (str (js/Math.floor v)))
                                (remove-attr! this attr-name))))}))

;; Special value property: also syncs textarea.value when set
(defn- define-value-prop! [^js proto]
  (.defineProperty
   js/Object proto "value"
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this
                             (if-let [refs (gobj/get this k-refs)]
                               (.-value (gobj/get refs "textarea"))
                               (or (get-attr this model/attr-value) ""))))
        :set (fn [v] (this-as ^js this
                              (let [str-v (if (and (some? v) (not= v js/undefined)) (str v) "")]
                                (set-attr! this model/attr-value str-v)
                                (when-let [refs (gobj/get this k-refs)]
                                  (let [^js textarea-el (gobj/get refs "textarea")]
                                    (set! (.-value textarea-el) str-v))))))}))

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

    ;; Value property (special: also syncs textarea.value)
    (define-value-prop! proto)

    ;; String properties
    (define-string-prop! proto "name"         model/attr-name)
    (define-string-prop! proto "placeholder"  model/attr-placeholder)
    (define-string-prop! proto "autocomplete" model/attr-autocomplete)

    ;; Boolean properties
    (define-bool-prop! proto "disabled" model/attr-disabled)
    (define-bool-prop! proto "readOnly" model/attr-readonly)
    (define-bool-prop! proto "required" model/attr-required)

    ;; Integer properties
    (define-int-prop! proto "rows"      model/attr-rows      3)
    (define-int-prop! proto "maxLength" model/attr-maxlength nil)
    (define-int-prop! proto "minLength" model/attr-minlength nil)

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
