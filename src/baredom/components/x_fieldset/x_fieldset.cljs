(ns baredom.components.x-fieldset.x-fieldset
  (:require [goog.object :as gobj]
            [baredom.components.x-fieldset.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: access via gobj/get / gobj/set)
;; ---------------------------------------------------------------------------
(def ^:private k-refs "__xFieldsetRefs")

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-fieldset-border-color:#d1d5db;"
   "--x-fieldset-border-width:1px;"
   "--x-fieldset-border-radius:8px;"
   "--x-fieldset-padding:1rem;"
   "--x-fieldset-gap:0.75rem;"
   "--x-fieldset-bg:transparent;"
   "--x-fieldset-legend-color:#374151;"
   "--x-fieldset-legend-font-size:0.875rem;"
   "--x-fieldset-legend-font-weight:600;"
   "--x-fieldset-legend-padding:0 0.375rem;"
   "--x-fieldset-disabled-opacity:0.45;"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-fieldset-border-color:#374151;"
   "--x-fieldset-legend-color:#d1d5db;"
   "}"
   "}"
   "[part=root]{"
   "display:block;"
   "border:var(--x-fieldset-border-width) solid var(--x-fieldset-border-color);"
   "border-radius:var(--x-fieldset-border-radius);"
   "padding:var(--x-fieldset-padding);"
   "background:var(--x-fieldset-bg);"
   "position:relative;"
   "}"
   ":host([data-disabled]) [part=root]{"
   "opacity:var(--x-fieldset-disabled-opacity);"
   "}"
   "[part=legend]{"
   "display:block;"
   "position:absolute;"
   "top:calc(-0.5em);"
   "left:calc(var(--x-fieldset-border-radius) - 0.375rem);"
   "padding:var(--x-fieldset-legend-padding);"
   "color:var(--x-fieldset-legend-color);"
   "font-size:var(--x-fieldset-legend-font-size);"
   "font-weight:var(--x-fieldset-legend-font-weight);"
   "background:inherit;"
   "line-height:1;"
   "white-space:nowrap;"
   "}"
   "[part=legend][hidden]{"
   "display:none;"
   "}"
   "[part=content]{"
   "display:flex;"
   "flex-direction:column;"
   "gap:var(--x-fieldset-gap);"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "}"
   ))

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
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (make-el "style")
        root-el    (make-el "div")
        legend-el  (make-el "div")
        content-el (make-el "div")
        slot-el    (make-el "slot")]

    (set! (.-textContent style-el) style-text)

    (set-attr! root-el    "part" "root")
    (set-attr! root-el    "role" "group")
    (set-attr! legend-el  "part" "legend")
    (set-attr! legend-el  "id"   "x-fieldset-legend")
    (set-attr! content-el "part" "content")

    (.appendChild content-el slot-el)
    (.appendChild root-el legend-el)
    (.appendChild root-el content-el)
    (.appendChild root style-el)
    (.appendChild root root-el)

    (let [refs #js {:root root :root-el root-el :legend-el legend-el :content-el content-el}]
      (gobj/set el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Read element state from attributes
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/normalize
   {:legend-raw          (get-attr el model/attr-legend)
    :disabled-present?   (has-attr? el model/attr-disabled)
    :aria-label-raw      (get-attr el model/attr-aria-label)
    :aria-describedby-raw (get-attr el model/attr-aria-describedby)}))

;; ---------------------------------------------------------------------------
;; Render
;; ---------------------------------------------------------------------------
(defn- render! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js root-el   (gobj/get refs "root-el")
          ^js legend-el (gobj/get refs "legend-el")
          m             (read-model el)
          legend        (:legend m)
          visible?      (:legend-visible? m)
          disabled?     (:disabled? m)]

      ;; Legend text and visibility
      (set! (.-textContent legend-el) legend)
      (if visible?
        (remove-attr! legend-el "hidden")
        (set-attr! legend-el "hidden" ""))

      ;; aria-labelledby vs aria-label on root[part=root]
      (if-let [aria-lbl (:aria-label m)]
        (do
          (set-attr! root-el "aria-label" aria-lbl)
          (remove-attr! root-el "aria-labelledby"))
        (do
          (remove-attr! root-el "aria-label")
          (if visible?
            (set-attr! root-el "aria-labelledby" "x-fieldset-legend")
            (remove-attr! root-el "aria-labelledby"))))

      ;; aria-describedby
      (if-let [v (:aria-describedby m)]
        (set-attr! root-el "aria-describedby" v)
        (remove-attr! root-el "aria-describedby"))

      ;; data-disabled on host for CSS hooks
      (set-bool-attr! el "data-disabled" disabled?))))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (when-not (gobj/get el k-refs)
    (make-shadow! el))
  (render! el))

(defn- disconnected! [^js _el])

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

    ;; observedAttributes
    (.defineProperty js/Object cls "observedAttributes"
                     #js {:get (fn [] model/observed-attributes)})

    ;; String properties
    (define-string-prop! proto "legend" model/attr-legend)

    ;; Boolean properties
    (define-bool-prop! proto "disabled" model/attr-disabled)

    ;; Lifecycle
    (aset proto "connectedCallback"
          (fn [] (this-as ^js this (connected! this))))

    (aset proto "disconnectedCallback"
          (fn [] (this-as ^js this (disconnected! this))))

    (aset proto "attributeChangedCallback"
          (fn [n o v] (this-as ^js this (attribute-changed! this n o v))))

    cls))

(defn init! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class))))
