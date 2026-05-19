(ns baredom.components.x-grid.x-grid
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [baredom.components.x-grid.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs  "__xGridRefs")
(def ^:private k-model "__xGridModel")

;; ── String-literal constants ──────────────────────────────────────────────
(def ^:private attr-part "part")
(def ^:private attr-data-gap "data-gap")
(def ^:private part-base "base")
(def ^:private cls-base  "base")

(def ^:private css-var-columns       "--x-grid-columns")
(def ^:private css-var-row-gap       "--x-grid-row-gap")
(def ^:private css-var-column-gap    "--x-grid-column-gap")
(def ^:private css-var-align-items   "--x-grid-align-items")
(def ^:private css-var-justify-items "--x-grid-justify-items")
(def ^:private css-var-auto-flow     "--x-grid-auto-flow")

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{display:block;}"
   ":host([inline]){display:inline-block;}"
   ".base{"
   "display:grid;"
   "box-sizing:border-box;"
   "grid-template-columns:var(" css-var-columns ");"
   "row-gap:var(" css-var-row-gap ");"
   "column-gap:var(" css-var-column-gap ");"
   "align-items:var(" css-var-align-items ");"
   "justify-items:var(" css-var-justify-items ");"
   "grid-auto-flow:var(" css-var-auto-flow ");"
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
   {:columns         (du/get-attr el model/attr-columns)
    :min-column-size (du/get-attr el model/attr-min-column-size)
    :gap             (du/get-attr el model/attr-gap)
    :row-gap         (du/get-attr el model/attr-row-gap)
    :column-gap      (du/get-attr el model/attr-column-gap)
    :align-items     (du/get-attr el model/attr-align-items)
    :justify-items   (du/get-attr el model/attr-justify-items)
    :auto-flow       (du/get-attr el model/attr-auto-flow)
    :inline          (du/has-attr? el model/attr-inline)}))

;; ── DOM patching ──────────────────────────────────────────────────────────
(defn- apply-model! [^js el {:keys [columns gap row-gap column-gap align-items
                                    justify-items auto-flow] :as m}]
  (let [{:keys [base]} (ensure-refs! el)
        ^js style (.-style base)]
    (du/set-attr! base attr-data-gap gap)
    (.setProperty style css-var-columns       columns)
    (.setProperty style css-var-row-gap       row-gap)
    (.setProperty style css-var-column-gap    column-gap)
    (.setProperty style css-var-align-items   align-items)
    (.setProperty style css-var-justify-items justify-items)
    (.setProperty style css-var-auto-flow     auto-flow)
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ── Lifecycle ─────────────────────────────────────────────────────────────
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
