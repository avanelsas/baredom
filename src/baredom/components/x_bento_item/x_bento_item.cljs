(ns baredom.components.x-bento-item.x-bento-item
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [baredom.components.x-bento-item.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-initialized? "__xBentoItemInitialized")
(def ^:private k-model "__xBentoItemModel")

;; ── String-literal constants ──────────────────────────────────────────────
(def ^:private attr-part "part")
(def ^:private part-base "base")
(def ^:private cls-base  "base")

(def ^:private css-grid-column "grid-column")
(def ^:private css-grid-row    "grid-row")
(def ^:private css-order       "order")

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{display:block;min-width:0;min-height:0;}"
   ".base{width:100%;height:100%;box-sizing:border-box;}"))

;; ── DOM initialisation ────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        base  (.createElement js/document "div")
        slot  (.createElement js/document "slot")]
    (set! (.-textContent style) style-text)
    (du/set-attr! base attr-part part-base)
    (set! (.-className base) cls-base)
    (.appendChild base slot)
    (.appendChild root style)
    (.appendChild root base)
    (du/mark-initialized! el k-initialized?)))

(defn- ensure-shadow! [^js el]
  (when-not (du/initialized? el k-initialized?)
    (init-dom! el)))

;; ── Model reading ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:col-span (du/get-attr el model/attr-col-span)
    :row-span (du/get-attr el model/attr-row-span)
    :order    (du/get-attr el model/attr-order)}))

;; ── DOM patching ──────────────────────────────────────────────────────────
(defn- apply-model! [^js el {:keys [col-span-css row-span-css order] :as m}]
  (ensure-shadow! el)
  (let [^js style (.-style el)]
    (.setProperty style css-grid-column col-span-css)
    (.setProperty style css-grid-row    row-span-css)
    (if-let [ord order]
      (.setProperty    style css-order (str ord))
      (.removeProperty style css-order)))
  (du/setv! el k-model m))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ── Element class ─────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-shadow! el)
  (update-from-attrs! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Public API ────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :attribute-changed-fn attribute-changed!}))
