(ns baredom.components.x-text-area.x-text-area
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baredom.utils.forms :as forms]
            [goog.object :as gobj]
            [baredom.components.x-text-area.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: access via gobj/get / gobj/set)
;; ---------------------------------------------------------------------------
(def ^:private k-refs      "__xTextAreaRefs")
(def ^:private k-model     "__xTextAreaModel")
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

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root        (.attachShadow el #js {:mode "open"})
        style-el    (.createElement js/document "style")
        field-el    (.createElement js/document "div")
        label-el    (.createElement js/document "label")
        wrapper-el  (.createElement js/document "div")
        textarea-el (.createElement js/document "textarea")
        hint-el     (.createElement js/document "span")
        error-el    (.createElement js/document "span")]

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
      (du/setv! el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Validity sync
;; ---------------------------------------------------------------------------
;; Thin adapter: maps this control's state to the shared validity policy.
;; :missing-message is omitted, so it uses forms/default-value-missing
;; ("Please fill in this field.") — the message this component already used.
(defn- sync-validity! [^js el ^js internals ^js textarea-el]
  (forms/set-validity! internals textarea-el
                       {:has-error? (du/has-attr? el model/attr-error)
                        :error      (du/get-attr el model/attr-error)
                        :required?  (du/has-attr? el model/attr-required)
                        :empty?     (= (.-value textarea-el) "")}))

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
;; DOM patching (render-orchestrator: phase list of named helpers)
;; ---------------------------------------------------------------------------
(defn- apply-textarea-props! [^js el ^js textarea-el {:keys [name placeholder disabled? readonly?
                                                            required? rows autocomplete
                                                            maxlength minlength resize]}]
  (set! (.-name textarea-el)         name)
  (set! (.-placeholder textarea-el)  placeholder)
  (set! (.-disabled textarea-el)     disabled?)
  (set! (.-readOnly textarea-el)     readonly?)
  (set! (.-required textarea-el)     required?)
  (set! (.-rows textarea-el)         rows)
  (set! (.-autocomplete textarea-el) autocomplete)
  (if maxlength
    (du/set-attr! textarea-el "maxlength" (str maxlength))
    (du/remove-attr! textarea-el "maxlength"))
  (if minlength
    (du/set-attr! textarea-el "minlength" (str minlength))
    (du/remove-attr! textarea-el "minlength"))
  ;; Resize: set CSS custom property on host
  (.setProperty (.-style el) "--x-text-area-resize" resize))

(defn- apply-textarea-aria! [^js textarea-el {:keys [required? has-error? has-hint?]}]
  (du/set-attr! textarea-el "aria-required" (if required?  "true" "false"))
  (du/set-attr! textarea-el "aria-invalid"  (if has-error? "true" "false"))
  (let [describedby (cond
                      (and has-hint? has-error?) "hint error"
                      has-hint?                  "hint"
                      has-error?                 "error"
                      :else                      nil)]
    (if describedby
      (du/set-attr! textarea-el "aria-describedby" describedby)
      (du/remove-attr! textarea-el "aria-describedby"))))

(defn- apply-vis-block!
  "Set textContent and toggle a visibility class on a label/hint/error node."
  [^js node text visible? hidden-class]
  (set! (.-textContent node) text)
  (if visible?
    (.remove (.-classList node) hidden-class)
    (.add (.-classList node) hidden-class)))

(defn- apply-label-vis! [^js label-el {:keys [label has-label?]}]
  (apply-vis-block! label-el label has-label? "label-hidden"))

(defn- apply-hint-vis! [^js hint-el {:keys [hint has-hint?]}]
  (apply-vis-block! hint-el hint has-hint? "hint-hidden"))

(defn- apply-error-vis! [^js error-el {:keys [error has-error?]}]
  (apply-vis-block! error-el error has-error? "error-hidden"))

(defn- apply-host-data! [^js el {:keys [has-error?]}]
  (du/set-bool-attr! el "data-invalid" has-error?))

(defn- apply-validity! [^js el ^js textarea-el]
  (when-let [^js internals (du/getv el k-internals)]
    (sync-validity! el internals textarea-el)))

(defn- apply-model! [^js el ^js refs m]
  (let [^js textarea-el (gobj/get refs "textarea")
        ^js label-el    (gobj/get refs "label")
        ^js hint-el     (gobj/get refs "hint")
        ^js error-el    (gobj/get refs "error")]
    (apply-textarea-props! el textarea-el m)
    (apply-textarea-aria!  textarea-el m)
    (apply-label-vis!      label-el m)
    (apply-hint-vis!       hint-el m)
    (apply-error-vis!      error-el m)
    (apply-host-data!      el m)
    (apply-validity!       el textarea-el)
    (du/setv! el k-model m)))

;; render! is the direct-write entry — form-disabled!/form-reset! mutate
;; attributes synchronously and want the apply to run unconditionally.
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

;; ---------------------------------------------------------------------------
;; Event dispatch
;; ---------------------------------------------------------------------------
;; ---------------------------------------------------------------------------
;; Event handlers
;; ---------------------------------------------------------------------------
(defn- on-input [^js el ^js _evt]
  (when-let [refs (du/getv el k-refs)]
    (let [^js textarea-el (gobj/get refs "textarea")
          value            (.-value textarea-el)
          prev-value       (or (du/get-attr el model/attr-value) "")
          name             (or (du/get-attr el model/attr-name) "")
          allowed?         (du/dispatch-cancelable!
                            el model/event-change-request
                            #js {:name name :value value :previousValue prev-value})]
      (if allowed?
        (do
          (when-let [^js internals (du/getv el k-internals)]
            (.setFormValue internals value)
            (sync-validity! el internals textarea-el))
          (du/dispatch! el model/event-input #js {:name name :value value}))
        (set! (.-value textarea-el) prev-value)))))

(defn- on-change [^js el ^js _evt]
  (when-let [refs (du/getv el k-refs)]
    (let [^js textarea-el (gobj/get refs "textarea")
          value            (.-value textarea-el)
          name             (or (du/get-attr el model/attr-name) "")]
      (when-let [^js internals (du/getv el k-internals)]
        (.setFormValue internals value)
        (sync-validity! el internals textarea-el))
      (du/dispatch! el model/event-change #js {:name name :value value}))))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- add-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js textarea-el (gobj/get refs "textarea")
          input-h  (fn handle-textarea-input  [e] (on-input  el e))
          change-h (fn handle-textarea-change [e] (on-change el e))]
      (.addEventListener textarea-el "input"  input-h)
      (.addEventListener textarea-el "change" change-h)
      (du/setv! el k-handlers #js {:input input-h :change change-h}))))

(defn- remove-listeners! [^js el]
  (when-let [refs     (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (let [^js textarea-el (gobj/get refs "textarea")]
        (.removeEventListener textarea-el "input"  (gobj/get handlers "input"))
        (.removeEventListener textarea-el "change" (gobj/get handlers "change")))
      (du/setv! el k-handlers nil))))

;; ---------------------------------------------------------------------------
;; Form-associated callbacks
;; ---------------------------------------------------------------------------
(defn- form-disabled! [^js el disabled?]
  (du/set-bool-attr! el model/attr-disabled (boolean disabled?))
  (render! el))

(defn- form-reset! [^js el]
  (du/remove-attr! el model/attr-value)
  (when-let [refs (du/getv el k-refs)]
    (let [^js textarea-el (gobj/get refs "textarea")]
      (set! (.-value textarea-el) "")))
  (when-let [^js internals (du/getv el k-internals)]
    (.setFormValue internals ""))
  (render! el))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el)
    (when (.-attachInternals el)
      (du/setv! el k-internals (.attachInternals el))))
  (remove-listeners! el)
  ;; Push value attr to textarea if set
  (when-let [refs (du/getv el k-refs)]
    (let [^js textarea-el (gobj/get refs "textarea")
          val-attr         (du/get-attr el model/attr-value)]
      (when val-attr
        (set! (.-value textarea-el) val-attr))))
  ;; Set initial form value
  (when-let [^js internals (du/getv el k-internals)]
    (.setFormValue internals (or (du/get-attr el model/attr-value) "")))
  (add-listeners! el)
  (render! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el attr-name old-val new-val]
  (when (not= old-val new-val)
    ;; For value attr: sync to textarea.value only if it differs (avoids cursor jump on typing)
    (when (= attr-name model/attr-value)
      (when-let [refs (du/getv el k-refs)]
        (let [^js textarea-el (gobj/get refs "textarea")]
          (when (not= (.-value textarea-el) new-val)
            (set! (.-value textarea-el) (or new-val ""))))))
    (update-from-attrs! el)))

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
                             (if-let [refs (du/getv this k-refs)]
                               (.-value (gobj/get refs "textarea"))
                               (or (du/get-attr this model/attr-value) ""))))
        :set (fn [v] (this-as ^js this
                              (let [str-v (if (and (some? v) (not= v js/undefined)) (str v) "")]
                                (du/set-attr! this model/attr-value str-v)
                                (when-let [refs (du/getv this k-refs)]
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
