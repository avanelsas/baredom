(ns baredom.components.x-bento-item.x-bento-item
  (:require
[baredom.utils.component :as component]
               [baredom.utils.dom :as du]
   [baredom.components.x-bento-item.model :as model]))

(def key-root "__xBentoItemRoot")
(def key-base "__xBentoItemBase")
(def key-initialized "__xBentoItemInit")

(defn read-inputs [^js el]
  {:col-span (du/get-attr el model/attr-col-span)
   :row-span (du/get-attr el model/attr-row-span)
   :order    (du/get-attr el model/attr-order)})

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

    (du/setv! el key-root root)
    (du/setv! el key-base base)))

(defn init-element! [^js el]
  (when-not (du/initialized? el key-initialized)
    (init-dom! el)
    (du/mark-initialized! el key-initialized))
  (render! el)
  el)

(defn- connected! [^js el]
  (init-element! el))

(defn- attribute-changed! [^js el _ _ _]
  (if (du/initialized? el key-initialized)
    (render! el)
    (init-element! el)))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :attribute-changed-fn   attribute-changed!
     }))
