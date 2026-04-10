(ns baredom.components.x-theme.x-theme
  (:require
   [goog.object :as gobj]
   [baredom.components.x-theme.model :as model]))

;; ── Instance-field keys ─────────────────────────────────────────────────────
(def ^:private k-refs "__xThemeRefs")

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        slot  (.createElement js/document "slot")]
    (set! (.-textContent style) (model/preset->css (.getAttribute el model/attr-preset)))
    (.appendChild root style)
    (.appendChild root slot)
    (gobj/set el k-refs #js {:style style})))

;; ── Render ──────────────────────────────────────────────────────────────────
(defn- render! [^js el]
  (let [^js refs (gobj/get el k-refs)]
    (when refs
      (let [^js style-el (.-style refs)]
        (set! (.-textContent style-el)
              (model/preset->css (.getAttribute el model/attr-preset)))))))

;; ── Lifecycle ───────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (gobj/get el k-refs)
    (init-dom! el)))

(defn- disconnected! [^js _el])

(defn- attribute-changed! [^js el _name _old-val _new-val]
  (render! el))

;; ── Property accessor ───────────────────────────────────────────────────────
(defn- install-preset-property! [^js proto]
  (js/Object.defineProperty
   proto "preset"
   #js {:get (fn []
               (this-as ^js this
                 (.getAttribute this model/attr-preset)))
        :set (fn [v]
               (this-as ^js this
                 (if (some? v)
                   (.setAttribute this model/attr-preset (str v))
                   (.removeAttribute this model/attr-preset))))
        :enumerable true
        :configurable true}))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn [] (this-as ^js this (connected! this))))
    (set! (.-disconnectedCallback (.-prototype klass))
          (fn [] (this-as ^js this (disconnected! this))))
    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [n o v] (this-as ^js this (attribute-changed! this n o v))))

    (install-preset-property! (.-prototype klass))

    klass))

;; ── Registration ────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class))))

(defn init! []
  (register!))
