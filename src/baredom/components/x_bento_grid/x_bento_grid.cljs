(ns baredom.components.x-bento-grid.x-bento-grid
  (:require
   [goog.object :as gobj]
   [baredom.components.x-bento-grid.model :as model]))

(def key-root "__xBentoGridRoot")
(def key-base "__xBentoGridBase")
(def key-initialized "__xBentoGridInit")

(defn getv [el k] (gobj/get el k))
(defn setv! [el k v] (gobj/set el k v))

(defn initialized? [el]
  (true? (getv el key-initialized)))

(defn mark-initialized! [el]
  (setv! el key-initialized true))

(defn read-inputs [^js el]
  {:columns    (.getAttribute el model/attr-columns)
   :gap        (.getAttribute el model/attr-gap)
   :row-gap    (.getAttribute el model/attr-row-gap)
   :column-gap (.getAttribute el model/attr-column-gap)
   :row-height (.getAttribute el model/attr-row-height)})

(defn style-text []
  "
  :host {
  display:block;
  }

  .base {
  display:grid;
  box-sizing:border-box;
  grid-template-columns:var(--x-bento-grid-columns);
  grid-auto-rows:var(--x-bento-grid-row-height);
  grid-auto-flow:dense;
  row-gap:var(--x-bento-grid-row-gap);
  column-gap:var(--x-bento-grid-column-gap);
  }
  ")

(defn apply-state! [^js base state]
  (let [^js style (.-style base)]
    (.setProperty style "--x-bento-grid-columns" (:template state))
    (.setProperty style "--x-bento-grid-row-height" (:row-height state))
    (.setProperty style "--x-bento-grid-row-gap" (:row-gap state))
    (.setProperty style "--x-bento-grid-column-gap" (:column-gap state))))

(defn render! [^js el]
  (let [state (model/derive-state (read-inputs el))
        base  (getv el key-base)]
    (when base
      (apply-state! base state))))

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
