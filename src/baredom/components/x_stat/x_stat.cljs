(ns baredom.components.x-stat.x-stat
  (:require
   [goog.object :as gobj]
   [baredom.components.x-stat.model :as model]))

(def key-root "__x_stat_root")
(def key-base "__x_stat_base")
(def key-label "__x_stat_label")
(def key-value "__x_stat_value")
(def key-hint "__x_stat_hint")
(def key-initialized "__x_stat_initialized")

(defn getv [el k] (gobj/get el k))
(defn setv! [el k v] (gobj/set el k v))

(defn initialized? [el] (true? (getv el key-initialized)))
(defn mark-initialized! [el] (setv! el key-initialized true))

(defn read-inputs [el]
  {:variant (.getAttribute el model/attr-variant)
   :align (.getAttribute el model/attr-align)
   :size (.getAttribute el model/attr-size)
   :emphasis (.getAttribute el model/attr-emphasis)
   :trend (.getAttribute el model/attr-trend)
   :loading (.hasAttribute el model/attr-loading)
   :label (.getAttribute el model/attr-label)
   :value (.getAttribute el model/attr-value)
   :hint (.getAttribute el model/attr-hint)})

(defn set-or-remove!
  [el attr value]
  (if value
    (.setAttribute el attr value)
    (.removeAttribute el attr)))

(defn apply-host-a11y! [el state]
  (.setAttribute el "role" "figure")
  (set-or-remove! el "aria-busy" (:aria-busy state))
  (set-or-remove! el "aria-label" (:label state)))

(defn apply-state! [base state]

  (.setAttribute base "data-variant" (:variant state))
  (.setAttribute base "data-align" (:align state))
  (.setAttribute base "data-size" (:size state))
  (.setAttribute base "data-emphasis" (:emphasis state))
  (.setAttribute base "data-trend" (:trend state))

  (if (:loading state)
    (.setAttribute base "data-loading" "true")
    (.removeAttribute base "data-loading")))

(defn render! [el]
  (let [state (model/derive-state (read-inputs el))
        base (getv el key-base)
        label-node (getv el key-label)
        value-node (getv el key-value)
        hint-node (getv el key-hint)]
    (when base
      (apply-host-a11y! el state)
      (apply-state! base state)
      (set! (.-textContent label-node) (or (:label state) ""))
      (set! (.-textContent value-node) (or (:value state) ""))
      (set! (.-textContent hint-node) (or (:hint state) "")))))

(def css-text
  (str
   ":host{display:block;color-scheme:light dark;}"
   ".base{display:grid;gap:6px;}"
   ".label{font-size:12px;opacity:0.8;}"
   ".value{font-weight:600;font-size:20px;}"
   ".hint{font-size:12px;opacity:0.7;}"
   ".base[data-loading='true']{opacity:0.6;}"))

(defn init-dom! [el]
  (let [root (.attachShadow el #js {:mode "open"})
        style-el (.createElement js/document "style")
        base (.createElement js/document "div")
        icon (.createElement js/document "div")
        body (.createElement js/document "div")
        label (.createElement js/document "div")
        value (.createElement js/document "div")
        hint (.createElement js/document "div")
        label-span (.createElement js/document "span")
        value-span (.createElement js/document "span")
        hint-span (.createElement js/document "span")
        icon-slot (.createElement js/document "slot")
        label-slot (.createElement js/document "slot")
        value-slot (.createElement js/document "slot")
        hint-slot (.createElement js/document "slot")
        default-slot (.createElement js/document "slot")]

    (set! (.-textContent style-el) css-text)

    (.setAttribute base "part" "base")
    (.setAttribute icon "part" "icon")
    (.setAttribute body "part" "body")
    (.setAttribute label "part" "label")
    (.setAttribute value "part" "value")
    (.setAttribute hint "part" "hint")

    (.setAttribute icon-slot "name" "icon")
    (.setAttribute label-slot "name" "label")
    (.setAttribute value-slot "name" "value")
    (.setAttribute hint-slot "name" "hint")

    (.appendChild icon icon-slot)
    (.appendChild label label-slot)
    (.appendChild label label-span)
    (.appendChild value value-slot)
    (.appendChild value value-span)
    (.appendChild hint hint-slot)
    (.appendChild hint hint-span)

    (.appendChild body label)
    (.appendChild body value)
    (.appendChild body hint)
    (.appendChild body default-slot)

    (.appendChild base icon)
    (.appendChild base body)

    (.appendChild root style-el)
    (.appendChild root base)

    (setv! el key-root root)
    (setv! el key-base base)
    (setv! el key-label label-span)
    (setv! el key-value value-span)
    (setv! el key-hint hint-span)))

(defn init-element! [el]

  (when-not (initialized? el)
    (init-dom! el)
    (mark-initialized! el))

  (render! el)
  el)

(defn connected-callback [el]
  (init-element! el))

(defn attribute-changed-callback [el _ _ _]
  (if (initialized? el)
    (render! el)
    (init-element! el)))

(defn element-class []

  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
      (fn []
        (this-as this
                 (connected-callback this))))

    (set! (.-attributeChangedCallback (.-prototype klass))
      (fn [name old-value new-value]
        (this-as this
                 (attribute-changed-callback this name old-value new-value))))

    klass))

(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class))))

(defn init! []
  (register!))
