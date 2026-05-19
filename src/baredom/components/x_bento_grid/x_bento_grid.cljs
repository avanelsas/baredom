(ns baredom.components.x-bento-grid.x-bento-grid
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [baredom.components.x-bento-grid.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs  "__xBentoGridRefs")
(def ^:private k-model "__xBentoGridModel")

;; ── String-literal constants ──────────────────────────────────────────────
(def ^:private attr-part "part")
(def ^:private part-base "base")
(def ^:private cls-base  "base")

(def ^:private css-var-columns    "--x-bento-grid-columns")
(def ^:private css-var-row-height "--x-bento-grid-row-height")
(def ^:private css-var-row-gap    "--x-bento-grid-row-gap")
(def ^:private css-var-column-gap "--x-bento-grid-column-gap")

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{display:block;}"
   ".base{"
   "display:grid;"
   "box-sizing:border-box;"
   "grid-template-columns:var(" css-var-columns ");"
   "grid-auto-rows:var(" css-var-row-height ");"
   "grid-auto-flow:dense;"
   "row-gap:var(" css-var-row-gap ");"
   "column-gap:var(" css-var-column-gap ");"
   "}"))

;; ── DOM initialisation ────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        base  (.createElement js/document "div")
        slot  (.createElement js/document "slot")
        refs  {:root root :base base}]

    (set! (.-textContent style) style-text)

    (du/set-attr! base attr-part part-base)
    (set! (.-className base) cls-base)
    (.appendChild base slot)

    (.appendChild root style)
    (.appendChild root base)

    (du/setv! el k-refs refs)
    refs))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs) (init-dom! el)))

;; ── Model reading ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:columns    (du/get-attr el model/attr-columns)
    :gap        (du/get-attr el model/attr-gap)
    :row-gap    (du/get-attr el model/attr-row-gap)
    :column-gap (du/get-attr el model/attr-column-gap)
    :row-height (du/get-attr el model/attr-row-height)}))

;; ── DOM patching ──────────────────────────────────────────────────────────
(defn- apply-model! [^js el {:keys [template row-height row-gap column-gap] :as m}]
  (let [{:keys [base]} (ensure-refs! el)
        ^js style (.-style base)]
    (.setProperty style css-var-columns    template)
    (.setProperty style css-var-row-height row-height)
    (.setProperty style css-var-row-gap    row-gap)
    (.setProperty style css-var-column-gap column-gap)
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ── Element class ─────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
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
