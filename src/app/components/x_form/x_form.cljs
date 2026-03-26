(ns app.components.x-form.x-form
  (:require [goog.object :as gobj]
            [app.components.x-form.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: access via gobj/get / gobj/set)
;; ---------------------------------------------------------------------------
(def ^:private k-refs     "__xFormRefs")
(def ^:private k-handlers "__xFormHandlers")

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "}"
   "[part=root]{"
   "display:flex;"
   "flex-direction:column;"
   "gap:var(--x-form-gap,1rem);"
   "width:var(--x-form-width,100%);"
   "}"
   ":host([loading]){pointer-events:none;opacity:0.6;}"))

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

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (when-not (.-shadowRoot el)
    (let [root     (.attachShadow el #js {:mode "open"})
          style-el (make-el "style")
          form-el  (make-el "form")
          slot-el  (make-el "slot")]

      (set! (.-textContent style-el) style-text)

      (set-attr! form-el "part"       "root")
      (set-attr! form-el "novalidate" "")

      (.appendChild form-el slot-el)
      (.appendChild root style-el)
      (.appendChild root form-el)

      (gobj/set el k-refs #js {:form form-el}))))

;; ---------------------------------------------------------------------------
;; Read element state from attributes
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/normalize
   {:loading-raw      (get-attr el model/attr-loading)
    :novalidate-raw   (when (has-attr? el model/attr-novalidate) "")
    :autocomplete-raw (get-attr el model/attr-autocomplete)}))

;; ---------------------------------------------------------------------------
;; Render
;; ---------------------------------------------------------------------------
(defn- render! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js form-el (gobj/get refs "form")
          m           (read-model el)]
      (set-attr! form-el "autocomplete" (:autocomplete m))
      (if (:loading? m)
        (do (set-attr! form-el "aria-busy"     "true")
            (set-attr! form-el "data-loading"  ""))
        (do (remove-attr! form-el "aria-busy")
            (remove-attr! form-el "data-loading"))))))

;; ---------------------------------------------------------------------------
;; Validation
;; ---------------------------------------------------------------------------
(defn- report-fields-validity! [^js el]
  (let [fields (.querySelectorAll el "[name]:not([disabled])")
        result #js {:ok true}]
    (dotimes [i (.-length fields)]
      (let [^js field (aget fields i)]
        (when (.-reportValidity field)
          (when-not (.reportValidity field)
            (aset result "ok" false)))))
    (aget result "ok")))

;; ---------------------------------------------------------------------------
;; Value collection
;; ---------------------------------------------------------------------------
(defn- collect-values [^js el]
  (let [fields (.querySelectorAll el "[name]:not([disabled])")
        result (js/Object.)]
    (dotimes [i (.-length fields)]
      (let [^js field (aget fields i)
            field-name (.-name field)]
        (when (and field-name (not= field-name ""))
          (let [tag-lower  (.toLowerCase (.-tagName field))
                type-attr  (.getAttribute field "type")
                checkbox?  (or (= type-attr "checkbox")
                               (= type-attr "radio")
                               (= tag-lower "x-checkbox"))]
            (if checkbox?
              (when (.-checked field)
                (aset result field-name (or (.-value field) "on")))
              (aset result field-name (or (.-value field) "")))))))
    result))

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
         :cancelable cancelable?})))

;; ---------------------------------------------------------------------------
;; Event handlers
;; ---------------------------------------------------------------------------
(defn- handle-submit! [^js el ^js e]
  (.preventDefault e)
  (let [m (read-model el)]
    (when-not (:loading? m)
      (let [valid? (or (:novalidate? m) (report-fields-validity! el))]
        (when valid?
          (let [values (collect-values el)]
            (dispatch! el model/event-submit #js {:values values} true)))))))

(defn- handle-reset! [^js el ^js _e]
  (let [fields (.querySelectorAll el "[name]")]
    (dotimes [i (.-length fields)]
      (let [^js field (aget fields i)]
        (when (.-formResetCallback field)
          (.formResetCallback field)))))
  (dispatch! el model/event-reset #js {} false))

(defn- handle-click! [^js el ^js e]
  (when-let [^js btn (.closest (.-target e) "button,input[type=submit],input[type=reset]")]
    (let [btn-type (.-type btn)]
      (when-let [refs (gobj/get el k-refs)]
        (let [^js form-el (gobj/get refs "form")]
          (cond
            (or (= btn-type "submit") (= btn-type ""))
            (.requestSubmit form-el)
            (= btn-type "reset")
            (.reset form-el)))))))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- add-listeners! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js form-el (gobj/get refs "form")
          submit-h    (fn [^js e] (handle-submit! el e))
          reset-h     (fn [^js e] (handle-reset! el e))
          click-h     (fn [^js e] (handle-click! el e))]
      (.addEventListener form-el "submit" submit-h)
      (.addEventListener form-el "reset"  reset-h)
      (.addEventListener el      "click"  click-h)
      (gobj/set el k-handlers #js {:submit submit-h :reset reset-h :click click-h}))))

(defn- remove-listeners! [^js el]
  (when-let [refs     (gobj/get el k-refs)]
    (when-let [handlers (gobj/get el k-handlers)]
      (let [^js form-el (gobj/get refs "form")]
        (.removeEventListener form-el "submit" (gobj/get handlers "submit"))
        (.removeEventListener form-el "reset"  (gobj/get handlers "reset"))
        (.removeEventListener el      "click"  (gobj/get handlers "click")))
      (gobj/set el k-handlers nil))))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (make-shadow! el)
  (remove-listeners! el)
  (add-listeners! el)
  (render! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name _old _new-val]
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
        :set (fn [v] (this-as ^js this
                              (if (boolean v)
                                (set-attr! this attr-name "")
                                (remove-attr! this attr-name))))}))

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

;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------
(defn- element-class []
  (let [cls   (js* "(class extends HTMLElement {})")
        proto (.-prototype cls)]

    (.defineProperty js/Object cls "observedAttributes"
                     #js {:get (fn [] model/observed-attributes)})

    ;; Properties
    (define-bool-prop!   proto "loading"      model/attr-loading)
    (define-bool-prop!   proto "novalidate"   model/attr-novalidate)
    (define-string-prop! proto "autocomplete" model/attr-autocomplete)

    ;; Lifecycle callbacks
    (aset proto "connectedCallback"
          (fn [] (this-as ^js this (connected! this))))

    (aset proto "disconnectedCallback"
          (fn [] (this-as ^js this (disconnected! this))))

    (aset proto "attributeChangedCallback"
          (fn [n o v] (this-as ^js this (attribute-changed! this n o v))))

    ;; Public methods
    (aset proto "submit"
          (fn []
            (this-as ^js this
                     (when-let [refs (gobj/get this k-refs)]
                       (let [^js form-el (gobj/get refs "form")]
                         (.requestSubmit form-el))))))

    (aset proto "reset"
          (fn []
            (this-as ^js this
                     (when-let [refs (gobj/get this k-refs)]
                       (let [^js form-el (gobj/get refs "form")]
                         (.reset form-el))))))

    (aset proto "setFieldError"
          (fn [field-name msg]
            (this-as ^js this
                     (when-let [^js field (.querySelector this (str "[name=\"" field-name "\"]"))]
                       (if (or (nil? msg) (= msg "") (= msg js/undefined))
                         (remove-attr! field "error")
                         (set-attr! field "error" msg))))))

    (aset proto "clearErrors"
          (fn []
            (this-as ^js this
                     (let [fields (.querySelectorAll this "[error]")]
                       (dotimes [i (.-length fields)]
                         (remove-attr! (aget fields i) "error"))))))

    cls))

(defn init! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class))))
