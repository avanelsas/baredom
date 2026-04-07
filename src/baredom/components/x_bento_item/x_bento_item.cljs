(ns baredom.components.x-bento-item.x-bento-item
  (:require
   [goog.object :as gobj]
   [baredom.components.x-bento-item.model :as model]))

(def key-root "__xBentoItemRoot")
(def key-base "__xBentoItemBase")
(def key-initialized "__xBentoItemInit")

(defn getv [el k] (gobj/get el k))
(defn setv! [el k v] (gobj/set el k v))

(defn initialized? [el]
  (true? (getv el key-initialized)))

(defn mark-initialized! [el]
  (setv! el key-initialized true))

(defn read-inputs [^js el]
  {:col-span (.getAttribute el model/attr-col-span)
   :row-span (.getAttribute el model/attr-row-span)
   :order    (.getAttribute el model/attr-order)})

(defn style-text []
  "
  :host {
  display:block;
  min-width:0;
  min-height:0;
  }

  .base {
  width:100%;
  height:100%;
  box-sizing:border-box;
  }
  ")

(defn apply-state! [^js el state]
  (let [^js style (.-style el)]
    (.setProperty style "grid-column" (:col-span-css state))
    (.setProperty style "grid-row" (:row-span-css state))
    (if-let [ord (:order state)]
      (.setProperty style "order" (str ord))
      (.removeProperty style "order"))))

(defn render! [^js el]
  (let [state (model/derive-state (read-inputs el))]
    (apply-state! el state)))

(defn init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        base  (.createElement js/document "div")
        slot  (.createElement js/document "slot")]

    (set! (.-textContent style) (style-text))

    (.setAttribute base "part" "base")
    (set! (.-className base) "base")
    (.appendChild base slot)

    (.appendChild root style)
    (.appendChild root base)

    (setv! el key-root root)
    (setv! el key-base base)))

(defn init-element! [^js el]
  (when-not (initialized? el)
    (init-dom! el)
    (mark-initialized! el))
  (render! el)
  el)

(defn connected-callback [^js el]
  (init-element! el))

(defn attribute-changed-callback [^js el _ _ _]
  (if (initialized? el)
    (render! el)
    (init-element! el)))

(defn element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass)
      model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
      (fn []
        (this-as ^js this
                 (connected-callback this))))

    (set! (.-attributeChangedCallback (.-prototype klass))
      (fn [name old-value new-value]
        (this-as ^js this
                 (attribute-changed-callback this name old-value new-value))))

    klass))

(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class))))

(defn init! []
  (register!))
