(ns baredom.components.x-search-field.x-search-field
  (:require [baredom.utils.component :as component]
            [baredom.utils.forms :as forms]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-search-field.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: access via gobj/get / gobj/set)
;; ---------------------------------------------------------------------------
(def ^:private k-refs           "__xSearchFieldRefs")
(def ^:private k-internals      "__xSearchFieldInternals")
(def ^:private k-handlers       "__xSearchFieldHandlers")
(def ^:private k-debounce-timer "__xSearchFieldDebounceTimer")

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-search-field-bg:var(--x-color-surface,#ffffff);"
   "--x-search-field-color:var(--x-color-text,#111827);"
   "--x-search-field-border:1px solid var(--x-color-border,#d1d5db);"
   "--x-search-field-border-radius:var(--x-radius-md,6px);"
   "--x-search-field-focus-ring-color:var(--x-color-primary,#2563eb);"
   "--x-search-field-icon-color:var(--x-color-border,#9ca3af);"
   "--x-search-field-clear-color:var(--x-color-border,#9ca3af);"
   "--x-search-field-disabled-opacity:0.45;"
   "}"
   du/control-font-rule
   du/hidden-rule
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-search-field-bg:var(--x-color-surface,#1f2937);"
   "--x-search-field-color:var(--x-color-text,#f9fafb);"
   "--x-search-field-border:1px solid var(--x-color-border,#4b5563);"
   "--x-search-field-focus-ring-color:var(--x-color-primary,#3b82f6);"
   "--x-search-field-icon-color:var(--x-color-text-muted,#6b7280);"
   "--x-search-field-clear-color:var(--x-color-text-muted,#6b7280);"
   "}"
   "}"
   "[part=wrapper]{"
   "display:flex;"
   "align-items:center;"
   "position:relative;"
   "background:var(--x-search-field-bg);"
   "border:var(--x-search-field-border);"
   "border-radius:var(--x-search-field-border-radius);"
   "transition:border-color var(--x-transition-duration,120ms) var(--x-transition-easing,ease),"
   "box-shadow var(--x-transition-duration,120ms) var(--x-transition-easing,ease);"
   "}"
   "[part=wrapper]:focus-within{"
   "border-color:var(--x-search-field-focus-ring-color);"
   "box-shadow:0 0 0 3px color-mix(in srgb,var(--x-search-field-focus-ring-color) 20%,transparent);"
   "}"
   "[part=icon]{"
   "display:flex;"
   "align-items:center;"
   "justify-content:center;"
   "flex-shrink:0;"
   "width:36px;"
   "color:var(--x-search-field-icon-color);"
   "pointer-events:none;"
   "}"
   "[part=input]{"
   "flex:1;"
   "min-width:0;"
   "padding:0.5rem 0.25rem;"
   "background:transparent;"
   "color:var(--x-search-field-color);"
   "border:none;"
   "outline:none;"
   "font-size:var(--x-font-size-base,1rem);"
   "line-height:1.5;"
   "font-family:inherit;"
   "}"
   "[part=input]::-webkit-search-cancel-button{"
   "-webkit-appearance:none;"
   "}"
   "[part=input]::-webkit-search-decoration{"
   "-webkit-appearance:none;"
   "}"
   "[part=clear]{"
   "display:flex;"
   "align-items:center;"
   "justify-content:center;"
   "flex-shrink:0;"
   "width:32px;"
   "height:32px;"
   "margin-right:2px;"
   "background:none;"
   "border:none;"
   "cursor:pointer;"
   "color:var(--x-search-field-clear-color);"
   "font-size:1.125rem;"
   "line-height:1;"
   "border-radius:var(--x-radius-sm,4px);"
   "padding:0;"
   "}"
   "[part=clear]:hover{"
   "color:var(--x-search-field-color);"
   "}"
   ".clear-hidden{"
   "display:none;"
   "}"
   ":host([disabled]) [part=wrapper]{"
   "opacity:var(--x-search-field-disabled-opacity);"
   "cursor:not-allowed;"
   "}"
   ":host([disabled]) [part=input]{"
   "cursor:not-allowed;"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=wrapper]{transition:none;}"
   "}"))

;; ---------------------------------------------------------------------------
;; DOM helpers
;; ---------------------------------------------------------------------------

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(def ^:private search-icon-svg
  (str "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" "
       "viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" "
       "stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\" "
       "aria-hidden=\"true\">"
       "<circle cx=\"11\" cy=\"11\" r=\"8\"/>"
       "<line x1=\"21\" y1=\"21\" x2=\"16.65\" y2=\"16.65\"/>"
       "</svg>"))

(defn- make-shadow! [^js el]
  (when-not (du/getv el k-refs)
    (let [root      (.attachShadow el #js {:mode "open"})
          style-el  (.createElement js/document "style")
          wrapper   (.createElement js/document "div")
          icon-el   (.createElement js/document "span")
          input-el  (.createElement js/document "input")
          clear-el  (.createElement js/document "button")]

      (set! (.-textContent style-el) style-text)

      (du/set-attr! wrapper  "part" "wrapper")
      (du/set-attr! icon-el  "part" "icon")
      (set! (.-innerHTML icon-el) search-icon-svg)

      (du/set-attr! input-el "part"        "input")
      (du/set-attr! input-el "type"        "search")
      (du/set-attr! input-el "spellcheck"  "false")

      (du/set-attr! clear-el "part"        "clear")
      (du/set-attr! clear-el "type"        "button")
      (du/set-attr! clear-el "aria-label"  "Clear search")
      (set! (.-textContent clear-el) "×")
      (.add (.-classList clear-el) "clear-hidden")

      (.appendChild wrapper icon-el)
      (.appendChild wrapper input-el)
      (.appendChild wrapper clear-el)
      (.appendChild root style-el)
      (.appendChild root wrapper)

      (let [refs #js {:wrapper wrapper
                      :icon    icon-el
                      :input   input-el
                      :clear   clear-el}]
        (du/setv! el k-refs refs)
        refs))))

;; ---------------------------------------------------------------------------
;; Clear button visibility
;; ---------------------------------------------------------------------------
(defn- toggle-clear-visibility! [^js input-el ^js clear-el ^js el]
  (let [empty?    (= "" (.-value input-el))
        disabled? (du/has-attr? el model/attr-disabled)]
    (if (or empty? disabled?)
      (.add    (.-classList clear-el) "clear-hidden")
      (.remove (.-classList clear-el) "clear-hidden"))))

;; ---------------------------------------------------------------------------
;; Validity sync
;; ---------------------------------------------------------------------------
(defn- sync-validity! [^js el ^js internals ^js input-el]
  (let [required? (du/has-attr? el model/attr-required)
        value     (.-value input-el)]
    (if (and required? (= value ""))
      (.setValidity internals #js {:valueMissing true} "Please fill in this field." input-el)
      (.setValidity internals #js {} "" input-el))))

(defn- sync-form! [^js el ^js input-el]
  (when-let [^js internals (du/getv el k-internals)]
    (.setFormValue internals (.-value input-el))
    (sync-validity! el internals input-el)))

(defn- sync-field! [^js el ^js refs]
  (let [^js input-el (gobj/get refs "input")]
    (sync-form! el input-el)
    (toggle-clear-visibility! input-el (gobj/get refs "clear") el)))

(defn- show-value! [^js el ^js refs value]
  (set! (.-value (gobj/get refs "input")) value)
  (sync-field! el refs))

(defn- committed-value [^js el]
  (or (du/get-attr el model/attr-value) ""))

(defn- field-name [^js el]
  (or (du/get-attr el model/attr-name) ""))

(defn- change-allowed! [^js el name value]
  (du/dispatch-cancelable! el model/event-change-request
                           #js {:name name :value value :previousValue (committed-value el)}))

;; ---------------------------------------------------------------------------
;; Render
;; ---------------------------------------------------------------------------
(defn- render! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js input-el (gobj/get refs "input")
          ^js clear-el (gobj/get refs "clear")
          m            (model/normalize
                        {:name-raw          (du/get-attr el model/attr-name)
                         :value-raw         (du/get-attr el model/attr-value)
                         :placeholder-raw   (du/get-attr el model/attr-placeholder)
                         :label-raw         (du/get-attr el model/attr-label)
                         :disabled-present? (du/has-attr? el model/attr-disabled)
                         :required-present? (du/has-attr? el model/attr-required)
                         :autocomplete-raw  (du/get-attr el model/attr-autocomplete)})]

      (set! (.-name input-el)         (:name m))
      (set! (.-placeholder input-el)  (:placeholder m))
      (set! (.-disabled input-el)     (:disabled? m))
      (set! (.-required input-el)     (:required? m))
      (set! (.-autocomplete input-el) (:autocomplete m))

      ;; aria-label from label attribute
      (let [lbl (:label m)]
        (if (and lbl (not= lbl ""))
          (du/set-attr! input-el "aria-label" lbl)
          (du/remove-attr! input-el "aria-label")))

      (du/set-attr! input-el "aria-required" (if (:required? m) "true" "false"))

      (toggle-clear-visibility! input-el clear-el el)

      (when-let [^js internals (du/getv el k-internals)]
        (sync-validity! el internals input-el)))))

;; ---------------------------------------------------------------------------
;; Event dispatch
;; ---------------------------------------------------------------------------
;; ---------------------------------------------------------------------------
;; Event handlers
;; ---------------------------------------------------------------------------
;; ---------------------------------------------------------------------------
;; Debounced input dispatch
;; ---------------------------------------------------------------------------
(defn- clear-pending-debounce! [^js el]
  (when-let [timer (du/getv el k-debounce-timer)]
    (js/clearTimeout timer)
    ;; Transient handle: debounce timer id — no diagnostic display value.
    (du/setv-untraced! el k-debounce-timer nil)))

(defn- dispatch-input! [^js el name value]
  (du/dispatch! el model/event-input #js {:name name :value value}))

;; Dispatches `x-search-field-input` either immediately or, when the
;; `debounce` attribute is a positive millisecond count, after the caller
;; stops typing for that long. Only the outward event is delayed — local
;; effects (form value, validity, clear button) already ran synchronously.
(defn- schedule-input-dispatch! [^js el name value]
  (let [ms (model/normalize-debounce (du/get-attr el model/attr-debounce))]
    (clear-pending-debounce! el)
    (if (pos? ms)
      (let [timer (js/setTimeout
                   (fn []
                     ;; Transient handle: debounce timer id.
                     (du/setv-untraced! el k-debounce-timer nil)
                     (when-let [refs (du/getv el k-refs)]
                       (dispatch-input! el name (.-value (gobj/get refs "input")))))
                   ms)]
        ;; Transient handle: debounce timer id.
        (du/setv-untraced! el k-debounce-timer timer))
      (dispatch-input! el name value))))

(defn- make-input-handler [^js el]
  (fn [^js _evt]
    (when-let [refs (du/getv el k-refs)]
      (let [^js input-el (gobj/get refs "input")
            name         (field-name el)]
        (if (change-allowed! el name (.-value input-el))
          (do (sync-field! el refs)
              (schedule-input-dispatch! el name (.-value input-el)))
          (show-value! el refs (committed-value el)))))))

(defn- make-change-handler [^js el]
  (fn [^js _evt]
    (when-let [refs (du/getv el k-refs)]
      (let [^js input-el (gobj/get refs "input")
            value        (.-value input-el)
            name         (field-name el)]
        (sync-form! el input-el)
        (du/dispatch! el model/event-change #js {:name name :value value})))))

(defn- make-keydown-handler [^js el]
  (fn [^js evt]
    (when (= "Enter" (.-key evt))
      (.preventDefault evt)
      (when-let [refs (du/getv el k-refs)]
        (let [^js input-el (gobj/get refs "input")
              value        (.-value input-el)
              name         (field-name el)]
          (if (and (du/has-attr? el model/attr-required) (= value ""))
            (when-let [^js internals (du/getv el k-internals)]
              (.reportValidity internals))
            (du/dispatch-cancelable! el model/event-search #js {:name name :value value})))))))

(defn- make-click-handler [^js el]
  (fn [^js _evt]
    (when-let [refs (du/getv el k-refs)]
      (let [^js input-el (gobj/get refs "input")
            name         (field-name el)]
        (when (change-allowed! el name "")
          (show-value! el refs "")
          (clear-pending-debounce! el)
          (du/dispatch! el model/event-clear #js {:name name})
          (.focus input-el))))))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- add-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [^js input-el (gobj/get refs "input")
          ^js clear-el (gobj/get refs "clear")
          input-h      (make-input-handler el)
          change-h     (make-change-handler el)
          keydown-h    (make-keydown-handler el)
          click-h      (make-click-handler el)]
      (.addEventListener input-el "input"   input-h)
      (.addEventListener input-el "change"  change-h)
      (.addEventListener input-el "keydown" keydown-h)
      (.addEventListener clear-el "click"   click-h)
      (du/setv! el k-handlers
                #js {:input   input-h
                     :change  change-h
                     :keydown keydown-h
                     :click   click-h}))))

(defn- remove-listeners! [^js el]
  (when-let [refs     (du/getv el k-refs)]
    (when-let [handlers (du/getv el k-handlers)]
      (let [^js input-el (gobj/get refs "input")
            ^js clear-el (gobj/get refs "clear")]
        (.removeEventListener input-el "input"   (gobj/get handlers "input"))
        (.removeEventListener input-el "change"  (gobj/get handlers "change"))
        (.removeEventListener input-el "keydown" (gobj/get handlers "keydown"))
        (.removeEventListener clear-el "click"   (gobj/get handlers "click")))
      (du/setv! el k-handlers nil))))

;; ---------------------------------------------------------------------------
;; Form-associated callbacks
;; ---------------------------------------------------------------------------
(defn- form-disabled! [^js el disabled?]
  (du/set-bool-attr! el model/attr-disabled (boolean disabled?))
  (render! el))

(defn- form-reset! [^js el]
  (clear-pending-debounce! el)
  (du/remove-attr! el model/attr-value)
  (when-let [refs (du/getv el k-refs)]
    (let [^js input-el (gobj/get refs "input")]
      (set! (.-value input-el) "")))
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
  ;; Push value attr to input if set
  (when-let [refs (du/getv el k-refs)]
    (let [^js input-el (gobj/get refs "input")
          val-attr     (du/get-attr el model/attr-value)]
      (when val-attr
        (set! (.-value input-el) val-attr))))
  ;; Set initial form value
  (when-let [^js internals (du/getv el k-internals)]
    (.setFormValue internals (or (du/get-attr el model/attr-value) "")))
  (add-listeners! el)
  (render! el))

(defn- disconnected! [^js el]
  (clear-pending-debounce! el)
  (remove-listeners! el))

(defn- attribute-changed! [^js el name old new-val]
  ;; A value attr that changed goes to input.value, where the two differ. A write of the value
  ;; the attr already has leaves typed text as it is.
  (when (and (= name model/attr-value) (not= old new-val))
    (when-let [refs (du/getv el k-refs)]
      (let [^js input-el (gobj/get refs "input")]
        (when (not= (.-value input-el) new-val)
          (set! (.-value input-el) (or new-val ""))))))
  (render! el))

;; ---------------------------------------------------------------------------
;; Property helpers
;; ---------------------------------------------------------------------------
(defn- define-value-prop! [^js proto]
  (.defineProperty
   js/Object proto "value"
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this
                             (if-let [refs (du/getv this k-refs)]
                               (.-value (gobj/get refs "input"))
                               (or (du/get-attr this model/attr-value) ""))))
        :set (fn [v] (this-as ^js this
                              (let [str-v (if (and (some? v) (not= v js/undefined)) (str v) "")]
                                (du/set-attr! this model/attr-value str-v)
                                (when-let [refs (du/getv this k-refs)]
                                  (let [^js input-el (gobj/get refs "input")]
                                    (set! (.-value input-el) str-v))))))}))

;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------

(defn- install-property-accessors! [^js proto]
  (forms/install-validity-api! proto k-internals)
  (define-value-prop! proto)
  (du/define-string-prop! proto "name"         model/attr-name "")
  (du/define-string-prop! proto "placeholder"  model/attr-placeholder "")
  (du/define-string-prop! proto "label"        model/attr-label "")
  (du/define-string-prop! proto "autocomplete" model/attr-autocomplete "")
  (du/define-bool-prop! proto "disabled" model/attr-disabled)
  (du/define-bool-prop! proto "required" model/attr-required)
  (du/define-number-prop! proto "debounce" model/attr-debounce 0))

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
