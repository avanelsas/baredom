(ns app.components.x-search-field.x-search-field
  (:require [goog.object :as gobj]
            [app.components.x-search-field.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: access via gobj/get / gobj/set)
;; ---------------------------------------------------------------------------
(def ^:private k-refs      "__xSearchFieldRefs")
(def ^:private k-internals "__xSearchFieldInternals")
(def ^:private k-handlers  "__xSearchFieldHandlers")

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-search-field-bg:#ffffff;"
   "--x-search-field-color:#111827;"
   "--x-search-field-border:1px solid #d1d5db;"
   "--x-search-field-border-radius:6px;"
   "--x-search-field-focus-ring-color:#2563eb;"
   "--x-search-field-icon-color:#9ca3af;"
   "--x-search-field-clear-color:#9ca3af;"
   "--x-search-field-disabled-opacity:0.45;"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-search-field-bg:#1f2937;"
   "--x-search-field-color:#f9fafb;"
   "--x-search-field-border:1px solid #4b5563;"
   "--x-search-field-focus-ring-color:#3b82f6;"
   "--x-search-field-icon-color:#6b7280;"
   "--x-search-field-clear-color:#6b7280;"
   "}"
   "}"
   "[part=wrapper]{"
   "display:flex;"
   "align-items:center;"
   "position:relative;"
   "background:var(--x-search-field-bg);"
   "border:var(--x-search-field-border);"
   "border-radius:var(--x-search-field-border-radius);"
   "transition:border-color 120ms ease,box-shadow 120ms ease;"
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
   "font-size:1rem;"
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
   "border-radius:4px;"
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
(def ^:private search-icon-svg
  (str "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"16\" height=\"16\" "
       "viewBox=\"0 0 24 24\" fill=\"none\" stroke=\"currentColor\" "
       "stroke-width=\"2\" stroke-linecap=\"round\" stroke-linejoin=\"round\" "
       "aria-hidden=\"true\">"
       "<circle cx=\"11\" cy=\"11\" r=\"8\"/>"
       "<line x1=\"21\" y1=\"21\" x2=\"16.65\" y2=\"16.65\"/>"
       "</svg>"))

(defn- make-shadow! [^js el]
  (when-not (.-shadowRoot el)
    (let [root      (.attachShadow el #js {:mode "open"})
          style-el  (make-el "style")
          wrapper   (make-el "div")
          icon-el   (make-el "span")
          input-el  (make-el "input")
          clear-el  (make-el "button")]

      (set! (.-textContent style-el) style-text)

      (set-attr! wrapper  "part" "wrapper")
      (set-attr! icon-el  "part" "icon")
      (set! (.-innerHTML icon-el) search-icon-svg)

      (set-attr! input-el "part"        "input")
      (set-attr! input-el "type"        "search")
      (set-attr! input-el "spellcheck"  "false")

      (set-attr! clear-el "part"        "clear")
      (set-attr! clear-el "type"        "button")
      (set-attr! clear-el "aria-label"  "Clear search")
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
        (gobj/set el k-refs refs)
        refs))))

;; ---------------------------------------------------------------------------
;; Clear button visibility
;; ---------------------------------------------------------------------------
(defn- toggle-clear-visibility! [^js input-el ^js clear-el ^js el]
  (let [empty?    (= "" (.-value input-el))
        disabled? (has-attr? el model/attr-disabled)]
    (if (or empty? disabled?)
      (.add    (.-classList clear-el) "clear-hidden")
      (.remove (.-classList clear-el) "clear-hidden"))))

;; ---------------------------------------------------------------------------
;; Validity sync
;; ---------------------------------------------------------------------------
(defn- sync-validity! [^js el ^js internals ^js input-el]
  (let [required? (has-attr? el model/attr-required)
        value     (.-value input-el)]
    (if (and required? (= value ""))
      (.setValidity internals #js {:valueMissing true} "Please fill in this field." input-el)
      (.setValidity internals #js {} "" input-el))))

;; ---------------------------------------------------------------------------
;; Render
;; ---------------------------------------------------------------------------
(defn- render! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js input-el (gobj/get refs "input")
          ^js clear-el (gobj/get refs "clear")
          m            (model/normalize
                        {:name-raw          (get-attr el model/attr-name)
                         :value-raw         (get-attr el model/attr-value)
                         :placeholder-raw   (get-attr el model/attr-placeholder)
                         :label-raw         (get-attr el model/attr-label)
                         :disabled-present? (has-attr? el model/attr-disabled)
                         :required-present? (has-attr? el model/attr-required)
                         :autocomplete-raw  (get-attr el model/attr-autocomplete)})]

      (set! (.-name input-el)         (:name m))
      (set! (.-placeholder input-el)  (:placeholder m))
      (set! (.-disabled input-el)     (:disabled? m))
      (set! (.-required input-el)     (:required? m))
      (set! (.-autocomplete input-el) (:autocomplete m))

      ;; aria-label from label attribute
      (let [lbl (:label m)]
        (if (and lbl (not= lbl ""))
          (set-attr! input-el "aria-label" lbl)
          (remove-attr! input-el "aria-label")))

      (set-attr! input-el "aria-required" (if (:required? m) "true" "false"))

      (toggle-clear-visibility! input-el clear-el el)

      (when-let [^js internals (gobj/get el k-internals)]
        (sync-validity! el internals input-el)))))

;; ---------------------------------------------------------------------------
;; Event dispatch
;; ---------------------------------------------------------------------------
(defn- dispatch! [^js el event-name detail cancelable?]
  (.dispatchEvent
   el
   (js/CustomEvent.
    event-name
    #js {:detail     detail
         :bubbles    true
         :composed   true
         :cancelable (boolean cancelable?)})))

;; ---------------------------------------------------------------------------
;; Event handlers
;; ---------------------------------------------------------------------------
(defn- make-input-handler [^js el]
  (fn [^js _evt]
    (when-let [refs (gobj/get el k-refs)]
      (let [^js input-el (gobj/get refs "input")
            ^js clear-el (gobj/get refs "clear")
            value        (.-value input-el)
            name         (or (get-attr el model/attr-name) "")]
        (when-let [^js internals (gobj/get el k-internals)]
          (.setFormValue internals value)
          (sync-validity! el internals input-el))
        (toggle-clear-visibility! input-el clear-el el)
        (dispatch! el model/event-input #js {:name name :value value} false)))))

(defn- make-change-handler [^js el]
  (fn [^js _evt]
    (when-let [refs (gobj/get el k-refs)]
      (let [^js input-el (gobj/get refs "input")
            value        (.-value input-el)
            name         (or (get-attr el model/attr-name) "")]
        (when-let [^js internals (gobj/get el k-internals)]
          (.setFormValue internals value)
          (sync-validity! el internals input-el))
        (dispatch! el model/event-change #js {:name name :value value} false)))))

(defn- make-keydown-handler [^js el]
  (fn [^js evt]
    (when (= "Enter" (.-key evt))
      (.preventDefault evt)
      (when-let [refs (gobj/get el k-refs)]
        (let [^js input-el (gobj/get refs "input")
              value        (.-value input-el)
              name         (or (get-attr el model/attr-name) "")]
          (if (and (has-attr? el model/attr-required) (= value ""))
            (when-let [^js internals (gobj/get el k-internals)]
              (.reportValidity internals))
            (dispatch! el model/event-search #js {:name name :value value} true)))))))

(defn- make-click-handler [^js el]
  (fn [^js _evt]
    (when-let [refs (gobj/get el k-refs)]
      (let [^js input-el (gobj/get refs "input")
            ^js clear-el (gobj/get refs "clear")
            name         (or (get-attr el model/attr-name) "")]
        (set! (.-value input-el) "")
        (when-let [^js internals (gobj/get el k-internals)]
          (.setFormValue internals "")
          (sync-validity! el internals input-el))
        (toggle-clear-visibility! input-el clear-el el)
        (dispatch! el model/event-clear #js {:name name} false)
        (.focus input-el)))))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- add-listeners! [^js el]
  (when-let [refs (gobj/get el k-refs)]
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
      (gobj/set el k-handlers
                #js {:input   input-h
                     :change  change-h
                     :keydown keydown-h
                     :click   click-h}))))

(defn- remove-listeners! [^js el]
  (when-let [refs     (gobj/get el k-refs)]
    (when-let [handlers (gobj/get el k-handlers)]
      (let [^js input-el (gobj/get refs "input")
            ^js clear-el (gobj/get refs "clear")]
        (.removeEventListener input-el "input"   (gobj/get handlers "input"))
        (.removeEventListener input-el "change"  (gobj/get handlers "change"))
        (.removeEventListener input-el "keydown" (gobj/get handlers "keydown"))
        (.removeEventListener clear-el "click"   (gobj/get handlers "click")))
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
  ;; Push value attr to input if set
  (when-let [refs (gobj/get el k-refs)]
    (let [^js input-el (gobj/get refs "input")
          val-attr     (get-attr el model/attr-value)]
      (when val-attr
        (set! (.-value input-el) val-attr))))
  ;; Set initial form value
  (when-let [^js internals (gobj/get el k-internals)]
    (.setFormValue internals (or (get-attr el model/attr-value) "")))
  (add-listeners! el)
  (render! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el name _old new-val]
  ;; For value attr: sync to input.value only if it differs
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

(defn- define-value-prop! [^js proto]
  (.defineProperty
   js/Object proto "value"
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this
                             (if-let [refs (gobj/get this k-refs)]
                               (.-value (gobj/get refs "input"))
                               (or (get-attr this model/attr-value) ""))))
        :set (fn [v] (this-as ^js this
                              (let [str-v (if (and (some? v) (not= v js/undefined)) (str v) "")]
                                (set-attr! this model/attr-value str-v)
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
    (define-string-prop! proto "name"         model/attr-name)
    (define-string-prop! proto "placeholder"  model/attr-placeholder)
    (define-string-prop! proto "label"        model/attr-label)
    (define-string-prop! proto "autocomplete" model/attr-autocomplete)

    ;; Boolean properties
    (define-bool-prop! proto "disabled" model/attr-disabled)
    (define-bool-prop! proto "required" model/attr-required)

    ;; Form constraint validation API (delegates to internals)
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
