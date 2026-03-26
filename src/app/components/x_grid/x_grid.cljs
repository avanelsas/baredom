(ns app.components.x-grid.x-grid
  (:require
   [goog.object :as gobj]
   [app.components.x-grid.model :as model]))

(def key-root "__x_grid_root")
(def key-base "__x_grid_base")
(def key-initialized "__x_grid_initialized")

(defn getv [el k] (gobj/get el k))
(defn setv! [el k v] (gobj/set el k v))

(defn initialized? [el]
  (true? (getv el key-initialized)))

(defn mark-initialized! [el]
  (setv! el key-initialized true))

(defn read-inputs [el]
  {:columns (.getAttribute el model/attr-columns)
   :min-column-size (.getAttribute el model/attr-min-column-size)
   :gap (.getAttribute el model/attr-gap)
   :row-gap (.getAttribute el model/attr-row-gap)
   :column-gap (.getAttribute el model/attr-column-gap)
   :align-items (.getAttribute el model/attr-align-items)
   :justify-items (.getAttribute el model/attr-justify-items)
   :auto-flow (.getAttribute el model/attr-auto-flow)
   :inline (.hasAttribute el model/attr-inline)})

(defn style-text []
  "
  :host {
  display:block;
  }

  :host([inline]) {
  display:inline-block;
  }

  .base {
  display:grid;
  box-sizing:border-box;
  grid-template-columns:var(--x-grid-columns);
  row-gap:var(--x-grid-row-gap);
  column-gap:var(--x-grid-column-gap);
  align-items:var(--x-grid-align-items);
  justify-items:var(--x-grid-justify-items);
  grid-auto-flow:var(--x-grid-auto-flow);

  @media (prefers-reduced-motion: reduce) {
  .base {
    transition: none;
    }
  }
  }
  ")

(defn apply-state! [base state]
  (let [^js style (.-style base)]
    (.setAttribute base "data-gap" (:gap state))

    (.setProperty style "--x-grid-columns" (:columns state))
    (.setProperty style "--x-grid-row-gap" (:row-gap state))
    (.setProperty style "--x-grid-column-gap" (:column-gap state))
    (.setProperty style "--x-grid-align-items" (:align-items state))
    (.setProperty style "--x-grid-justify-items" (:justify-items state))
    (.setProperty style "--x-grid-auto-flow" (:auto-flow state))))

(defn render! [el]

  (let [state (model/derive-state (read-inputs el))
        base (getv el key-base)]

    (when base
      (apply-state! base state))))

(defn init-dom! [el]

  (let [root (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        base (.createElement js/document "div")
        slot (.createElement js/document "slot")]

    (set! (.-textContent style) (style-text))

    (.setAttribute base "part" "base")
    (set! (.-className base) "base")
    (.appendChild base slot)

    (.appendChild root style)
    (.appendChild root base)

    (setv! el key-root root)
    (setv! el key-base base)))

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

    (set! (.-observedAttributes klass)
      model/observed-attributes)

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
