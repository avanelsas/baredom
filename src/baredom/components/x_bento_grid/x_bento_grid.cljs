(ns baredom.components.x-bento-grid.x-bento-grid
  (:require
[baredom.utils.component :as component]
               [baredom.utils.dom :as du]
   [baredom.components.x-bento-grid.model :as model]))

(def key-root "__xBentoGridRoot")
(def key-base "__xBentoGridBase")
(def key-initialized "__xBentoGridInit")

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
        base  (du/getv el key-base)]
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

    (du/setv! el key-root root)
    (du/setv! el key-base base)))

(defn init-element! [^js el]
  (when-not (du/initialized? el key-initialized)
    (init-dom! el)
    (du/mark-initialized! el key-initialized))
  (render! el)
  el)

(defn connected-callback [^js el]
  (init-element! el))

(defn attribute-changed-callback [^js el _ _ _]
  (if (du/initialized? el key-initialized)
    (render! el)
    (init-element! el)))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected-callback
     :attribute-changed-fn   attribute-changed-callback
     }))
