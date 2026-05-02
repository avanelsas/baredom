(ns baredom.components.x-text-area.x-text-area
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-text-area.model :as model]))

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
   "--x-text-area-label-font-size:var(--x-font-size-sm,0.875rem);"
   "--x-text-area-bg:var(--x-color-surface,#ffffff);"
   "--x-text-area-color:#111827;"
   "--x-text-area-border:1px solid var(--x-color-border,#d1d5db);"
   "--x-text-area-border-radius:var(--x-radius-md,6px);"
   "--x-text-area-padding:0.5rem 0.75rem;"
   "--x-text-area-focus-ring-color:var(--x-color-primary,#2563eb);"
   "--x-text-area-error-color:var(--x-color-danger,#dc2626);"
   "--x-text-area-hint-color:var(--x-color-text-muted,#6b7280);"
   "--x-text-area-disabled-opacity:0.45;"
   "--x-text-area-min-height:5rem;"
   "--x-text-area-font-size:var(--x-font-size-base,1rem);"
   "--x-text-area-resize:vertical;"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-text-area-label-color:var(--x-color-border,#d1d5db);"
   "--x-text-area-bg:var(--x-color-surface,#1f2937);"
   "--x-text-area-color:#f9fafb;"
   "--x-text-area-border:1px solid var(--x-color-border,#4b5563);"
   "--x-text-area-focus-ring-color:var(--x-color-primary,#3b82f6);"
   "--x-text-area-error-color:#f87171;"
   "--x-text-area-hint-color:var(--x-color-text-muted,#9ca3af);"
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

    (du/set-attr! field-el    "part" "field")
    (du/set-attr! label-el    "part" "label")
    (du/set-attr! label-el    "id"   "label")
    (du/set-attr! label-el    "for"  "textarea")
    (du/set-attr! wrapper-el  "part" "textarea-wrapper")
    (du/set-attr! textarea-el "part" "textarea")
    (du/set-attr! textarea-el "id"   "textarea")
    (du/set-attr! textarea-el "aria-labelledby" "label")
    (du/set-attr! hint-el     "part"     "hint")
    (du/set-attr! hint-el     "id"       "hint")
    (du/set-attr! hint-el     "aria-live" "polite")
    (du/set-attr! error-el    "part"     "error")
    (du/set-attr! error-el    "id"       "error")
    (du/set-attr! error-el    "role"     "alert")
    (du/set-attr! error-el    "aria-live" "assertive")

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
  (let [has-error? (du/has-attr? el model/attr-error)
        error-msg  (or (du/get-attr el model/attr-error) "")
        required?  (du/has-attr? el model/attr-required)
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
   {:label-raw         (du/get-attr el model/attr-label)
    :name-raw          (du/get-attr el model/attr-name)
    :value-raw         (du/get-attr el model/attr-value)
    :placeholder-raw   (du/get-attr el model/attr-placeholder)
    :hint-raw          (du/get-attr el model/attr-hint)
    :error-raw         (du/get-attr el model/attr-error)
    :disabled-present? (du/has-attr? el model/attr-disabled)
    :readonly-present? (du/has-attr? el model/attr-readonly)
    :required-present? (du/has-attr? el model/attr-required)
    :rows-raw          (du/get-attr el model/attr-rows)
    :maxlength-raw     (du/get-attr el model/attr-maxlength)
    :minlength-raw     (du/get-attr el model/attr-minlength)
    :autocomplete-raw  (du/get-attr el model/attr-autocomplete)
    :resize-raw        (du/get-attr el model/attr-resize)}))

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
        (du/set-attr! textarea-el "maxlength" (str maxlen))
        (du/remove-attr! textarea-el "maxlength"))
      (if-let [minlen (:minlength m)]
        (du/set-attr! textarea-el "minlength" (str minlen))
        (du/remove-attr! textarea-el "minlength"))

      ;; Resize: set CSS custom property on host
      (.setProperty (.-style el) "--x-text-area-resize" (:resize m))

      ;; ARIA on textarea
      (du/set-attr! textarea-el "aria-required" (if (:required? m) "true" "false"))
      (du/set-attr! textarea-el "aria-invalid"  (if has-error? "true" "false"))

      ;; aria-describedby conditionally includes hint and/or error ids
      (let [describedby (cond
                          (and has-hint? has-error?) "hint error"
                          has-hint?                  "hint"
                          has-error?                 "error"
                          :else                      nil)]
        (if describedby
          (du/set-attr! textarea-el "aria-describedby" describedby)
          (du/remove-attr! textarea-el "aria-describedby")))

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
        (sync-validity! el internals textarea-el)))))

;; ---------------------------------------------------------------------------
;; Event dispatch
;; ---------------------------------------------------------------------------
;; ---------------------------------------------------------------------------
;; Event handlers
;; ---------------------------------------------------------------------------
(defn- make-input-handler [^js el]
  (fn [^js _evt]
    (when-let [refs (gobj/get el k-refs)]
      (let [^js textarea-el (gobj/get refs "textarea")
            value            (.-value textarea-el)
            prev-value       (or (du/get-attr el model/attr-value) "")
            name             (or (du/get-attr el model/attr-name) "")
            allowed?         (du/dispatch-cancelable!
                              el model/event-change-request
                              #js {:name name :value value :previousValue prev-value})]
        (if allowed?
          (do
            (when-let [^js internals (gobj/get el k-internals)]
              (.setFormValue internals value)
              (sync-validity! el internals textarea-el))
            (du/dispatch! el model/event-input #js {:name name :value value}))
          (set! (.-value textarea-el) prev-value))))))

(defn- make-change-handler [^js el]
  (fn [^js _evt]
    (when-let [refs (gobj/get el k-refs)]
      (let [^js textarea-el (gobj/get refs "textarea")
            value            (.-value textarea-el)
            name             (or (du/get-attr el model/attr-name) "")]
        (when-let [^js internals (gobj/get el k-internals)]
          (.setFormValue internals value)
          (sync-validity! el internals textarea-el))
        (du/dispatch! el model/event-change #js {:name name :value value})))))

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
  (du/set-bool-attr! el model/attr-disabled (boolean disabled?))
  (render! el))

(defn- form-reset! [^js el]
  (du/remove-attr! el model/attr-value)
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
          val-attr         (du/get-attr el model/attr-value)]
      (when val-attr
        (set! (.-value textarea-el) val-attr))))
  ;; Set initial form value
  (when-let [^js internals (gobj/get el k-internals)]
    (.setFormValue internals (or (du/get-attr el model/attr-value) "")))
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
;; Special value property: also syncs textarea.value when set
(defn- define-value-prop! [^js proto]
  (.defineProperty
   js/Object proto "value"
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this
                             (if-let [refs (gobj/get this k-refs)]
                               (.-value (gobj/get refs "textarea"))
                               (or (du/get-attr this model/attr-value) ""))))
        :set (fn [v] (this-as ^js this
                              (let [str-v (if (and (some? v) (not= v js/undefined)) (str v) "")]
                                (du/set-attr! this model/attr-value str-v)
                                (when-let [refs (gobj/get this k-refs)]
                                  (let [^js textarea-el (gobj/get refs "textarea")]
                                    (set! (.-value textarea-el) str-v))))))}))

;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------

(defn- install-property-accessors! [^js proto]
  (define-value-prop! proto)
  (du/define-string-prop! proto "name"         model/attr-name "")
  (du/define-string-prop! proto "placeholder"  model/attr-placeholder "")
  (du/define-string-prop! proto "autocomplete" model/attr-autocomplete "")
  (du/define-bool-prop! proto "disabled" model/attr-disabled)
  (du/define-bool-prop! proto "readOnly" model/attr-readonly)
  (du/define-bool-prop! proto "required" model/attr-required)
  (du/define-number-prop! proto "rows"      model/attr-rows      3)
  (du/define-number-prop! proto "maxLength" model/attr-maxlength nil)
  (du/define-number-prop! proto "minLength" model/attr-minlength nil))

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
