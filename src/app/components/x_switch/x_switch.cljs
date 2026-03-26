(ns app.components.x-switch.x-switch
  (:require [goog.object :as gobj]
            [app.components.x-switch.model :as model]))

;; ── Instance-field keys ──────────────────────────────────────────────────────
(def ^:private k-refs      "__xSwitchRefs")
(def ^:private k-internals "__xSwitchInternals")
(def ^:private k-handlers  "__xSwitchHandlers")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;"
   "vertical-align:middle;"
   "color-scheme:light dark;"
   "--x-switch-track-width:44px;"
   "--x-switch-track-height:24px;"
   "--x-switch-thumb-size:18px;"
   "--x-switch-thumb-offset:3px;"
   "--x-switch-track-radius:999px;"
   "--x-switch-track-bg:#d1d5db;"
   "--x-switch-track-bg-checked:#2563eb;"
   "--x-switch-thumb-bg:#ffffff;"
   "--x-switch-focus-ring:#60a5fa;"
   "--x-switch-disabled-opacity:0.45;"
   "--x-switch-transition:background 150ms ease,transform 150ms ease;"
   "}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-switch-track-bg:#4b5563;"
   "--x-switch-track-bg-checked:#3b82f6;"
   "--x-switch-thumb-bg:#f9fafb;"
   "--x-switch-focus-ring:#93c5fd;"
   "}"
   "}"

   "[part=control]{"
   "all:unset;"
   "box-sizing:border-box;"
   "display:inline-flex;"
   "align-items:center;"
   "padding:0;"
   "cursor:pointer;"
   "}"

   "[part=control][disabled]{"
   "cursor:default;"
   "opacity:var(--x-switch-disabled-opacity);"
   "}"

   "[part=control]:focus-visible{"
   "outline:none;"
   "box-shadow:0 0 0 3px var(--x-switch-focus-ring);"
   "border-radius:calc(var(--x-switch-track-radius) + 2px);"
   "}"

   "[part=track]{"
   "position:relative;"
   "display:inline-block;"
   "width:var(--x-switch-track-width);"
   "height:var(--x-switch-track-height);"
   "border-radius:var(--x-switch-track-radius);"
   "background:var(--x-switch-track-bg);"
   "transition:background 150ms ease;"
   "flex-shrink:0;"
   "}"

   ":host([data-checked]) [part=track]{"
   "background:var(--x-switch-track-bg-checked);"
   "}"

   "[part=thumb]{"
   "position:absolute;"
   "top:var(--x-switch-thumb-offset);"
   "left:var(--x-switch-thumb-offset);"
   "width:var(--x-switch-thumb-size);"
   "height:var(--x-switch-thumb-size);"
   "border-radius:50%;"
   "background:var(--x-switch-thumb-bg);"
   "box-shadow:0 1px 3px rgba(0,0,0,0.25);"
   "transition:transform 150ms ease;"
   "}"

   ":host([data-checked]) [part=thumb]{"
   "transform:translateX(calc(var(--x-switch-track-width) - var(--x-switch-thumb-size) - calc(var(--x-switch-thumb-offset) * 2)));"
   "}"

   "@media (prefers-reduced-motion:reduce){"
   "[part=track]{transition:none;}"
   "[part=thumb]{transition:none;}"
   "}"))

;; ── DOM helpers ──────────────────────────────────────────────────────────────
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

;; ── Shadow DOM construction ───────────────────────────────────────────────────
(defn- make-shadow! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (make-el "style")
        control-el (make-el "button")
        track-el   (make-el "span")
        thumb-el   (make-el "span")]

    (set! (.-textContent style-el) style-text)

    (set-attr! control-el "part" "control")
    (set-attr! control-el "type" "button")
    (set-attr! control-el "role" "switch")
    (set-attr! track-el   "part" "track")
    (set-attr! thumb-el   "part" "thumb")

    (.appendChild track-el thumb-el)
    (.appendChild control-el track-el)
    (.appendChild root style-el)
    (.appendChild root control-el)

    (let [refs #js {:root root :control control-el :track track-el :thumb thumb-el}]
      (gobj/set el k-refs refs)
      refs)))

;; ── Read element state from attributes ───────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:checked-present?      (has-attr? el model/attr-checked)
    :disabled-present?     (has-attr? el model/attr-disabled)
    :readonly-present?     (has-attr? el model/attr-readonly)
    :required-present?     (has-attr? el model/attr-required)
    :name-raw              (get-attr el model/attr-name)
    :value-raw             (get-attr el model/attr-value)
    :aria-label-raw        (get-attr el model/attr-aria-label)
    :aria-describedby-raw  (get-attr el model/attr-aria-describedby)
    :aria-labelledby-raw   (get-attr el model/attr-aria-labelledby)}))

;; ── Render ───────────────────────────────────────────────────────────────────
(defn- render! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js control-el (gobj/get refs "control")
          m              (read-model el)
          disabled?      (:disabled? m)
          checked?       (:checked? m)]

      ;; ARIA on button[part=control]
      (set-attr! control-el "aria-checked"  (:aria-checked m))
      (set-attr! control-el "aria-disabled" (if disabled? "true" "false"))
      (set-attr! control-el "aria-required" (if (:required? m) "true" "false"))
      (set-attr! control-el "aria-readonly" (if (:readonly? m) "true" "false"))

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
      (set-bool-attr! el "data-checked"  checked?)
      (set-bool-attr! el "data-disabled" disabled?)

      ;; Form value via ElementInternals
      (when-let [^js internals (gobj/get el k-internals)]
        (.setFormValue internals (when checked? (:value m)))))))

;; ── Toggle logic ─────────────────────────────────────────────────────────────
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
  (let [m         (read-model el)
        disabled? (:disabled? m)
        readonly? (:readonly? m)]
    (when-not (or disabled? readonly?)
      (let [checked?     (:checked? m)
            next-checked (not checked?)
            value        (:value m)
            allowed?     (dispatch-cancelable!
                          el
                          model/event-change-request
                          #js {:value           value
                               :previousChecked  checked?
                               :nextChecked      next-checked})]
        (when allowed?
          (set-bool-attr! el model/attr-checked next-checked)
          (render! el)
          (dispatch! el model/event-change
                     #js {:value   value
                          :checked next-checked}))))))

;; ── External label association ────────────────────────────────────────────────
(defn- wire-external-label! [^js el]
  (let [id (get-attr el "id")]
    (when id
      (let [^js lbl (.querySelector js/document (str "label[for='" id "']"))]
        (when lbl
          (let [lid (or (get-attr lbl "id")
                        (let [gen-id (str "x-sw-lbl-" id)]
                          (set-attr! lbl "id" gen-id)
                          gen-id))]
            (when-let [refs (gobj/get el k-refs)]
              (set-attr! (gobj/get refs "control") "aria-labelledby" lid))))))))

;; ── Listener management ───────────────────────────────────────────────────────
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
    (let [^js control-el (gobj/get refs "control")
          click-h        (make-click-handler el)
          keydown-h      (make-keydown-handler el)
          handlers       #js {:click click-h :keydown keydown-h}]
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

;; ── Form-associated callbacks ─────────────────────────────────────────────────
(defn- form-disabled! [^js el disabled?]
  (set-bool-attr! el model/attr-disabled (boolean disabled?))
  (render! el))

(defn- form-reset! [^js el]
  (remove-attr! el model/attr-checked)
  (render! el))

;; ── Lifecycle ────────────────────────────────────────────────────────────────
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

;; ── Property helpers ──────────────────────────────────────────────────────────
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

;; ── Element class and registration ───────────────────────────────────────────
(defn- element-class []
  (let [cls   (js* "(class extends HTMLElement {})")
        proto (.-prototype cls)]

    ;; Form-associated
    (set! (.-formAssociated cls) true)

    ;; observedAttributes
    (.defineProperty js/Object cls "observedAttributes"
                     #js {:get (fn [] model/observed-attributes)})

    ;; Boolean properties
    (define-bool-prop!   proto "checked"  model/attr-checked)
    (define-bool-prop!   proto "disabled" model/attr-disabled)
    (define-bool-prop!   proto "readOnly" model/attr-readonly)
    (define-bool-prop!   proto "required" model/attr-required)

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
