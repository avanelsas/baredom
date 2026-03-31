(ns baredom.components.x-checkbox.x-checkbox
  (:require [goog.object :as gobj]
            [baredom.components.x-checkbox.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: access via gobj/get / gobj/set)
;; ---------------------------------------------------------------------------
(def ^:private k-refs      "__xCheckboxRefs")
(def ^:private k-internals "__xCheckboxInternals")
(def ^:private k-handlers  "__xCheckboxHandlers")

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;"
   "vertical-align:middle;"
   "color-scheme:light dark;"
   "--x-checkbox-size:16px;"
   "--x-checkbox-border-width:2px;"
   "--x-checkbox-border-color:#6b7280;"
   "--x-checkbox-border-radius:4px;"
   "--x-checkbox-bg:#ffffff;"
   "--x-checkbox-bg-checked:#2563eb;"
   "--x-checkbox-bg-indeterminate:#2563eb;"
   "--x-checkbox-border-checked:#2563eb;"
   "--x-checkbox-border-indeterminate:#2563eb;"
   "--x-checkbox-check-color:#ffffff;"
   "--x-checkbox-focus-ring:#60a5fa;"
   "--x-checkbox-disabled-opacity:0.45;"
   "--x-checkbox-transition:background 120ms ease,border-color 120ms ease;"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-checkbox-border-color:#9ca3af;"
   "--x-checkbox-bg:#1f2937;"
   "--x-checkbox-bg-checked:#3b82f6;"
   "--x-checkbox-bg-indeterminate:#3b82f6;"
   "--x-checkbox-border-checked:#3b82f6;"
   "--x-checkbox-border-indeterminate:#3b82f6;"
   "--x-checkbox-focus-ring:#93c5fd;"
   "}"
   "}"
   "[part=control]{"
   "all:unset;"
   "box-sizing:border-box;"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "padding:0;"
   "cursor:pointer;"
   "}"
   "[part=control][disabled]{"
   "cursor:default;"
   "opacity:var(--x-checkbox-disabled-opacity);"
   "}"
   "[part=control]:focus-visible{"
   "outline:none;"
   "box-shadow:0 0 0 3px var(--x-checkbox-focus-ring);"
   "border-radius:calc(var(--x-checkbox-border-radius) + 2px);"
   "}"
   "[part=box]{"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "width:var(--x-checkbox-size);"
   "height:var(--x-checkbox-size);"
   "border:var(--x-checkbox-border-width) solid var(--x-checkbox-border-color);"
   "border-radius:var(--x-checkbox-border-radius);"
   "background:var(--x-checkbox-bg);"
   "transition:var(--x-checkbox-transition);"
   "position:relative;"
   "flex-shrink:0;"
   "}"
   ":host([data-checked]) [part=box]{"
   "background:var(--x-checkbox-bg-checked);"
   "border-color:var(--x-checkbox-border-checked);"
   "}"
   ":host([data-indeterminate]) [part=box]{"
   "background:var(--x-checkbox-bg-indeterminate);"
   "border-color:var(--x-checkbox-border-indeterminate);"
   "}"
   "[part=check]{"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "position:relative;"
   "width:100%;"
   "height:100%;"
   "}"
   "[part=check]::before{"
   "content:'\\2713';"
   "color:var(--x-checkbox-check-color);"
   "font-size:10px;"
   "font-weight:700;"
   "line-height:1;"
   "opacity:0;"
   "transition:opacity 100ms ease;"
   "position:absolute;"
   "}"
   ":host([data-checked]) [part=check]::before{"
   "opacity:1;"
   "}"
   "[part=check]::after{"
   "content:'';"
   "display:block;"
   "width:8px;"
   "height:2px;"
   "background:var(--x-checkbox-check-color);"
   "border-radius:1px;"
   "opacity:0;"
   "transition:opacity 100ms ease;"
   "position:absolute;"
   "}"
   ":host([data-indeterminate]) [part=check]::after{"
   "opacity:1;"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=box]{transition:none;}"
   "[part=check]::before{transition:none;}"
   "[part=check]::after{transition:none;}"
   "}"))

;; ---------------------------------------------------------------------------
;; DOM helpers
;; ---------------------------------------------------------------------------
(defn- make-el [^js tag] (.createElement js/document tag))

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
        control-el  (make-el "button")
        box-el      (make-el "span")
        check-el    (make-el "span")]

    (set! (.-textContent style-el) style-text)

    (set-attr! control-el "part"  "control")
    (set-attr! control-el "type"  "button")
    (set-attr! control-el "role"  "checkbox")
    (set-attr! box-el     "part" "box")
    (set-attr! check-el   "part" "check")

    (.appendChild box-el check-el)
    (.appendChild control-el box-el)
    (.appendChild root style-el)
    (.appendChild root control-el)

    (let [refs #js {:root root :control control-el :box box-el :check check-el}]
      (gobj/set el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Read element state from attributes
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/normalize
   {:checked-present?       (model/parse-bool-attr (get-attr el model/attr-checked))
    :indeterminate-present? (model/parse-bool-attr (get-attr el model/attr-indeterminate))
    :disabled-present?      (model/parse-bool-attr (get-attr el model/attr-disabled))
    :readonly-present?      (model/parse-bool-attr (get-attr el model/attr-readonly))
    :required-present?      (model/parse-bool-attr (get-attr el model/attr-required))
    :name-raw               (get-attr el model/attr-name)
    :value-raw              (get-attr el model/attr-value)
    :aria-label-raw         (get-attr el model/attr-aria-label)
    :aria-describedby-raw   (get-attr el model/attr-aria-describedby)
    :aria-labelledby-raw    (get-attr el model/attr-aria-labelledby)}))

;; ---------------------------------------------------------------------------
;; Render
;; ---------------------------------------------------------------------------
(defn- render! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js control-el (gobj/get refs "control")
          m              (read-model el)
          disabled?      (:disabled? m)
          checked?       (:checked? m)
          indeterminate? (:indeterminate? m)]

      ;; ARIA on button[part=control]
      (set-attr! control-el "aria-checked" (:aria-checked m))
      (set-attr! control-el "aria-disabled"  (if disabled? "true" "false"))
      (set-attr! control-el "aria-required"  (if (:required? m) "true" "false"))
      (set-attr! control-el "aria-readonly"  (if (:readonly? m) "true" "false"))

      (if-let [v (:aria-label m)]
        (set-attr! control-el "aria-label" v)
        (remove-attr! control-el "aria-label"))

      (if-let [v (:aria-labelledby m)]
        (set-attr! control-el "aria-labelledby" v)
        (remove-attr! control-el "aria-labelledby"))

      (if-let [v (:aria-describedby m)]
        (set-attr! control-el "aria-describedby" v)
        (remove-attr! control-el "aria-describedby"))

      (set! (.-tabIndex control-el) (if disabled? -1 0))

      (if disabled?
        (set-attr! control-el "disabled" "")
        (remove-attr! control-el "disabled"))

      ;; Data attributes on host for CSS hooks
      (set-bool-attr! el "data-checked"       checked?)
      (set-bool-attr! el "data-indeterminate"  indeterminate?)
      (set-bool-attr! el "data-disabled"       disabled?)

      ;; Form value via ElementInternals
      (when-let [^js internals (gobj/get el k-internals)]
        (let [form-value (when checked? (:value m))]
          (.setFormValue internals (or form-value nil)))))))

;; ---------------------------------------------------------------------------
;; Toggle logic
;; ---------------------------------------------------------------------------
(defn- dispatch-cancelable! [^js el event-name detail]
  (let [^js ev (js/CustomEvent.
                event-name
                #js {:detail     detail
                     :bubbles    true
                     :composed   true
                     :cancelable true})]
    (.dispatchEvent el ev)
    (not (.-defaultPrevented ev))))

(defn- dispatch! [^js el event-name detail]
  (.dispatchEvent
   el
   (js/CustomEvent.
    event-name
    #js {:detail     detail
         :bubbles    true
         :composed   true
         :cancelable false})))

(defn- try-toggle! [^js el]
  (let [m              (read-model el)
        disabled?      (:disabled? m)
        readonly?      (:readonly? m)]
    (when-not (or disabled? readonly?)
      (let [checked?       (:checked? m)
            indeterminate? (:indeterminate? m)
            next-state     (model/next-toggle-state checked? indeterminate?)
            next-checked   (:checked? next-state)
            value          (:value m)
            allowed?       (dispatch-cancelable!
                            el
                            model/event-change-request
                            #js {:value           value
                                 :previousChecked  checked?
                                 :nextChecked      next-checked})]
        (when allowed?
          (set-bool-attr! el model/attr-checked       next-checked)
          (set-bool-attr! el model/attr-indeterminate (:indeterminate? next-state))
          (render! el)
          (dispatch! el model/event-change
                     #js {:value   value
                          :checked next-checked}))))))

;; ---------------------------------------------------------------------------
;; External label association
;; ---------------------------------------------------------------------------
(defn- wire-external-label! [^js el]
  (let [id (get-attr el "id")]
    (when id
      (let [^js lbl (.querySelector js/document (str "label[for='" id "']"))]
        (when lbl
          (let [lid (or (get-attr lbl "id")
                        (let [gen-id (str "x-cb-lbl-" id)]
                          (set-attr! lbl "id" gen-id)
                          gen-id))]
            (when-let [refs (gobj/get el k-refs)]
              (let [^js control (gobj/get refs "control")]
                ;; Only set if the user hasn't already provided aria-labelledby
                (when-not (has-attr? el model/attr-aria-labelledby)
                  (set-attr! control "aria-labelledby" lid))))))))))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- make-click-handler [^js el]
  (fn [^js _evt]
    (try-toggle! el)))

(defn- make-keydown-handler [^js el]
  (fn [^js evt]
    (let [key (.-key evt)]
      (when (or (= key " ") (= key "Enter"))
        (.preventDefault evt)
        (try-toggle! el)))))

(defn- add-listeners! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js control-el  (gobj/get refs "control")
          click-h          (make-click-handler el)
          keydown-h        (make-keydown-handler el)
          handlers         #js {:click click-h :keydown keydown-h}]
      (.addEventListener control-el "click"   click-h)
      (.addEventListener control-el "keydown" keydown-h)
      (gobj/set el k-handlers handlers))))

(defn- remove-listeners! [^js el]
  (when-let [refs     (gobj/get el k-refs)]
    (when-let [handlers (gobj/get el k-handlers)]
      (let [^js control-el (gobj/get refs "control")]
        (.removeEventListener control-el "click"   (gobj/get handlers "click"))
        (.removeEventListener control-el "keydown" (gobj/get handlers "keydown")))
      (gobj/set el k-handlers nil))))

;; ---------------------------------------------------------------------------
;; Form-associated callbacks
;; ---------------------------------------------------------------------------
(defn- form-disabled! [^js el disabled?]
  (set-bool-attr! el model/attr-disabled (boolean disabled?))
  (render! el))

(defn- form-reset! [^js el]
  (remove-attr! el model/attr-checked)
  (remove-attr! el model/attr-indeterminate)
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
  (add-listeners! el)
  (wire-external-label! el)
  (render! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name _old _new]
  (render! el))

;; ---------------------------------------------------------------------------
;; Property helpers
;; ---------------------------------------------------------------------------
(defn- define-bool-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (has-attr? this attr-name)))
        :set (fn [v] (this-as ^js this (set-bool-attr! this attr-name (boolean v))))}))

(defn- define-string-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (or (get-attr this attr-name) nil)))
        :set (fn [v] (this-as ^js this
                              (if (and (some? v) (not= v js/undefined))
                                (set-attr! this attr-name (str v))
                                (remove-attr! this attr-name))))}))

;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------
(defn- element-class []
  (let [cls   (js* "(class extends HTMLElement {})")
        proto (.-prototype cls)]

    ;; Form-associated
    (set! (.-formAssociated cls) true)

    ;; observedAttributes
    (set! (.-observedAttributes cls) model/observed-attributes)

    ;; Boolean properties
    (define-bool-prop!   proto "checked"       model/attr-checked)
    (define-bool-prop!   proto "indeterminate"  model/attr-indeterminate)
    (define-bool-prop!   proto "disabled"       model/attr-disabled)
    (define-bool-prop!   proto "readOnly"       model/attr-readonly)
    (define-bool-prop!   proto "required"       model/attr-required)

    ;; String properties
    (define-string-prop! proto "name"  model/attr-name)
    (define-string-prop! proto "value" model/attr-value)

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
