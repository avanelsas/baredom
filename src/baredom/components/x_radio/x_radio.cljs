(ns baredom.components.x-radio.x-radio
  (:require [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-radio.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: access via gobj/get / gobj/set)
;; ---------------------------------------------------------------------------
(def ^:private k-refs      "__xRadioRefs")
(def ^:private k-internals "__xRadioInternals")
(def ^:private k-handlers  "__xRadioHandlers")

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;"
   "vertical-align:middle;"
   "color-scheme:light dark;"
   "--x-radio-size:16px;"
   "--x-radio-border-width:2px;"
   "--x-radio-border-color:var(--x-color-border,#6b7280);"
   "--x-radio-bg:var(--x-color-surface,#ffffff);"
   "--x-radio-checked-color:var(--x-color-primary,#2563eb);"
   "--x-radio-dot-size:6px;"
   "--x-radio-focus-ring:var(--x-color-focus-ring,#60a5fa);"
   "--x-radio-disabled-opacity:0.45;"
   "--x-radio-transition:background var(--x-transition-duration,120ms) var(--x-transition-easing,ease),border-color var(--x-transition-duration,120ms) var(--x-transition-easing,ease);"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-radio-border-color:var(--x-color-border,#9ca3af);"
   "--x-radio-bg:var(--x-color-surface,#1f2937);"
   "--x-radio-checked-color:var(--x-color-primary,#3b82f6);"
   "--x-radio-focus-ring:var(--x-color-focus-ring,#93c5fd);"
   "}"
   "}"
   "[part=control]{"
   "all:unset;"
   "box-sizing:border-box;"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "width:var(--x-radio-size);"
   "height:var(--x-radio-size);"
   "border:var(--x-radio-border-width) solid var(--x-radio-border-color);"
   "border-radius:50%;"
   "background:var(--x-radio-bg);"
   "transition:var(--x-radio-transition);"
   "cursor:pointer;"
   "flex-shrink:0;"
   "position:relative;"
   "}"
   "[part=control][disabled]{"
   "cursor:default;"
   "opacity:var(--x-radio-disabled-opacity);"
   "}"
   "[part=control]:focus-visible{"
   "outline:none;"
   "box-shadow:0 0 0 3px var(--x-radio-focus-ring);"
   "}"
   ":host([data-checked]) [part=control]{"
   "border-color:var(--x-radio-checked-color);"
   "}"
   "[part=dot]{"
   "display:block;"
   "width:var(--x-radio-dot-size);"
   "height:var(--x-radio-dot-size);"
   "border-radius:50%;"
   "background:var(--x-radio-checked-color);"
   "opacity:0;"
   "transition:opacity 100ms ease;"
   "}"
   ":host([data-checked]) [part=dot]{"
   "opacity:1;"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=control]{transition:none;}"
   "[part=dot]{transition:none;}"
   "}"))

;; ---------------------------------------------------------------------------
;; DOM helpers
;; ---------------------------------------------------------------------------
(defn- make-el [^js tag] (.createElement js/document tag))

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (make-el "style")
        control-el (make-el "button")
        dot-el     (make-el "span")]

    (set! (.-textContent style-el) style-text)

    (du/set-attr! control-el "part" "control")
    (du/set-attr! control-el "type" "button")
    (du/set-attr! control-el "role" "radio")
    (du/set-attr! dot-el     "part" "dot")

    (.appendChild control-el dot-el)
    (.appendChild root style-el)
    (.appendChild root control-el)

    (let [refs #js {:root root :control control-el :dot dot-el}]
      (gobj/set el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Read element state from attributes
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/normalize
   {:checked-present?     (du/has-attr? el model/attr-checked)
    :disabled-present?    (du/has-attr? el model/attr-disabled)
    :readonly-present?    (du/has-attr? el model/attr-readonly)
    :required-present?    (du/has-attr? el model/attr-required)
    :name-raw             (du/get-attr el model/attr-name)
    :value-raw            (du/get-attr el model/attr-value)
    :aria-label-raw       (du/get-attr el model/attr-aria-label)
    :aria-describedby-raw (du/get-attr el model/attr-aria-describedby)
    :aria-labelledby-raw  (du/get-attr el model/attr-aria-labelledby)}))

;; ---------------------------------------------------------------------------
;; Render
;; ---------------------------------------------------------------------------
(defn- render! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js control-el (gobj/get refs "control")
          m              (read-model el)
          disabled?      (:disabled? m)
          checked?       (:checked? m)]

      ;; ARIA on button[part=control]
      (du/set-attr! control-el "aria-checked"  (:aria-checked m))
      (du/set-attr! control-el "aria-disabled"  (if disabled? "true" "false"))
      (du/set-attr! control-el "aria-required"  (if (:required? m) "true" "false"))
      (du/set-attr! control-el "aria-readonly"  (if (:readonly? m) "true" "false"))

      (if-let [v (:aria-label m)]
        (du/set-attr! control-el "aria-label" v)
        (du/remove-attr! control-el "aria-label"))

      (if-let [v (:aria-labelledby m)]
        (du/set-attr! control-el "aria-labelledby" v)
        (du/remove-attr! control-el "aria-labelledby"))

      (if-let [v (:aria-describedby m)]
        (du/set-attr! control-el "aria-describedby" v)
        (du/remove-attr! control-el "aria-describedby"))

      (if disabled?
        (du/set-attr! control-el "disabled" "")
        (du/remove-attr! control-el "disabled"))

      ;; Data attributes on host for CSS hooks
      (du/set-bool-attr! el "data-checked"  checked?)
      (du/set-bool-attr! el "data-disabled" disabled?)

      ;; Form value via ElementInternals
      (when-let [^js internals (gobj/get el k-internals)]
        (.setFormValue internals (when checked? (:value m)))))))

;; ---------------------------------------------------------------------------
;; Group utilities
;; ---------------------------------------------------------------------------
(defn- group-radios [^js el]
  (let [name-val (du/get-attr el model/attr-name)
        ^js form (when (.-form el) (.-form el))
        scope    (or form js/document)]
    (if (and name-val (not= name-val ""))
      (let [all (.querySelectorAll scope model/tag-name)
            result (array)]
        (.forEach all (fn [^js r]
                        (when (= (du/get-attr r model/attr-name) name-val)
                          (.push result r))))
        result)
      #js [el])))

(defn- sync-tabindexes! [^js el]
  (let [radios  (group-radios el)
        checked (.find radios (fn [^js r] (du/has-attr? r model/attr-checked)))
        first-r (when (pos? (.-length radios)) (aget radios 0))]
    (.forEach radios
              (fn [^js r]
                (when-let [refs (gobj/get r k-refs)]
                  (let [^js ctrl (gobj/get refs "control")
                        tab      (cond
                                   checked (if (= r checked) 0 -1)
                                   :else   (if (= r first-r) 0 -1))]
                    (set! (.-tabIndex ctrl) tab)))))))

;; ---------------------------------------------------------------------------
;; Selection logic
;; ---------------------------------------------------------------------------
(defn- try-select! [^js el]
  (let [m         (read-model el)
        disabled? (:disabled? m)
        readonly? (:readonly? m)
        checked?  (:checked? m)]
    (when-not (or disabled? readonly? checked?)
      (let [value    (:value m)
            allowed? (du/dispatch-cancelable!
                      el
                      model/event-change-request
                      #js {:value           value
                           :previousChecked false
                           :nextChecked     true})]
        (when allowed?
          (du/set-attr! el model/attr-checked "")
          (render! el)
          ;; Silently uncheck siblings
          (let [radios (group-radios el)]
            (.forEach radios
                      (fn [^js r]
                        (when (and (not= r el) (du/has-attr? r model/attr-checked))
                          (du/remove-attr! r model/attr-checked)
                          (render! r)))))
          (sync-tabindexes! el)
          (du/dispatch! el model/event-change
                     #js {:value   value
                          :checked true}))))))

;; ---------------------------------------------------------------------------
;; Keyboard handler
;; ---------------------------------------------------------------------------
(defn- focus-control! [^js r]
  (when-let [refs (gobj/get r k-refs)]
    (let [^js ctrl (gobj/get refs "control")]
      (.focus ctrl))))

(defn- make-keydown-handler [^js el]
  (fn [^js evt]
    (let [key    (.-key evt)
          radios (group-radios el)
          n      (.-length radios)
          idx    (.indexOf radios el)]
      (cond
        (or (= key " ") (= key "Enter"))
        (do (.preventDefault evt)
            (try-select! el))

        (or (= key "ArrowDown") (= key "ArrowRight"))
        (do (.preventDefault evt)
            (when (pos? n)
              (let [next-idx (mod (inc idx) n)
                    ^js next-r (aget radios next-idx)]
                (try-select! next-r)
                (focus-control! next-r))))

        (or (= key "ArrowUp") (= key "ArrowLeft"))
        (do (.preventDefault evt)
            (when (pos? n)
              (let [prev-idx (mod (dec idx) n)
                    ^js prev-r (aget radios prev-idx)]
                (try-select! prev-r)
                (focus-control! prev-r))))))))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- make-click-handler [^js el]
  (fn [^js _evt]
    (try-select! el)))

(defn- add-listeners! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js control-el (gobj/get refs "control")
          click-h        (make-click-handler el)
          keydown-h      (make-keydown-handler el)
          handlers       #js {:click click-h :keydown keydown-h}]
      (.addEventListener el "click" click-h)
      (.addEventListener control-el "keydown" keydown-h)
      (gobj/set el k-handlers handlers))))

(defn- remove-listeners! [^js el]
  (when-let [refs     (gobj/get el k-refs)]
    (when-let [handlers (gobj/get el k-handlers)]
      (let [^js control-el (gobj/get refs "control")]
        (.removeEventListener el "click" (gobj/get handlers "click"))
        (.removeEventListener control-el "keydown" (gobj/get handlers "keydown")))
      (gobj/set el k-handlers nil))))

;; ---------------------------------------------------------------------------
;; Form-associated callbacks
;; ---------------------------------------------------------------------------
(defn- form-disabled! [^js el disabled?]
  (du/set-bool-attr! el model/attr-disabled (boolean disabled?))
  (render! el))

(defn- form-reset! [^js el]
  (du/remove-attr! el model/attr-checked)
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
  (render! el)
  (sync-tabindexes! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name _old _new]
  (render! el))

;; ---------------------------------------------------------------------------
;; Property helpers
;; ---------------------------------------------------------------------------
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

    ;; Boolean properties
    (du/define-bool-prop!   proto "checked"  model/attr-checked)
    (du/define-bool-prop!   proto "disabled" model/attr-disabled)
    (du/define-bool-prop!   proto "readOnly" model/attr-readonly)
    (du/define-bool-prop!   proto "required" model/attr-required)

    ;; String properties
    (du/define-string-prop! proto "name"  model/attr-name)
    (du/define-string-prop! proto "value" model/attr-value)

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
