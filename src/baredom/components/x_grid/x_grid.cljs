(ns baredom.components.x-grid.x-grid
  (:require
[baredom.utils.component :as component]
               [baredom.utils.dom :as du]
   [baredom.components.x-grid.model :as model]))

(def key-root "__x_grid_root")
(def key-base "__x_grid_base")
(def key-initialized "__x_grid_initialized")

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
        base (du/getv el key-base)]

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

    (du/setv! el key-root root)
    (du/setv! el key-base base)))

(defn init-element! [el]

  (when-not (du/initialized? el key-initialized)
    (init-dom! el)
    (du/mark-initialized! el key-initialized))

  (render! el)
  el)

(defn connected-callback [el]
  (init-element! el))

(defn attribute-changed-callback [el _ _ _]
  (if (du/initialized? el key-initialized)
    (render! el)
    (init-element! el)))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected-callback
     :attribute-changed-fn   attribute-changed-callback
     }))
