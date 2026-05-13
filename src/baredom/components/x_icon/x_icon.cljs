(ns baredom.components.x-icon.x-icon
  (:require
   [baredom.utils.component :as component]
   [baredom.components.x-icon.model :as model]
   [baredom.utils.dom :as du]))

;; ── Instance-field keys ──────────────────────────────────────────────────────
(def ^:private k-refs  "__xIconRefs")
(def ^:private k-model "__xIconModel")

;; ── String-literal constants (no duplication; Closure-Advanced safe) ─────────
(def ^:private css-var-size  "--x-icon-size")
(def ^:private css-var-color "--x-icon-color")

(def ^:private attr-role        "role")
(def ^:private attr-aria-label  "aria-label")
(def ^:private attr-aria-hidden "aria-hidden")

(def ^:private attr-part "part")
(def ^:private part-box  "box")

(def ^:private role-img         "img")
(def ^:private aria-hidden-true "true")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def style-text
  (str
   ":host{"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "line-height:0;"
   "vertical-align:middle;"
   "color:var(" css-var-color ",currentColor);"
   css-var-size ":20px;}"

   "[part=box]{"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "width:var(" css-var-size ");"
   "height:var(" css-var-size ");}"

   "::slotted(svg){"
   "display:block;"
   "width:100%;"
   "height:100%;}"

   "::slotted(svg[fill=\"currentColor\"]),"
   "::slotted(svg[stroke=\"currentColor\"]){color:inherit;}"))

;; ── DOM initialisation ───────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        box   (.createElement js/document "span")
        slot  (.createElement js/document "slot")
        refs  {:root root :box box}]

    (set! (.-textContent style) style-text)

    (.setAttribute box attr-part part-box)
    (.appendChild box slot)

    (.appendChild root style)
    (.appendChild root box)

    (du/setv! el k-refs refs)
    refs))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs) (init-dom! el)))

;; ── Attribute readers ────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:size-raw       (du/get-attr el model/attr-size)
    :color-raw      (du/get-attr el model/attr-color)
    :label-raw      (du/get-attr el model/attr-label)
    :label-present? (du/has-attr? el model/attr-label)}))

;; ── DOM patching ─────────────────────────────────────────────────────────────
(defn- apply-model! [^js el {:keys [size-css color-css label labelled?] :as m}]
  (ensure-refs! el)
  (let [^js style (.-style el)]
    (.setProperty style css-var-size  size-css)
    (.setProperty style css-var-color color-css))

  (if labelled?
    (do
      (du/set-attr!    el attr-role       role-img)
      (du/set-attr!    el attr-aria-label label)
      (du/remove-attr! el attr-aria-hidden))
    (do
      (du/set-attr!    el attr-aria-hidden aria-hidden-true)
      (du/remove-attr! el attr-role)
      (du/remove-attr! el attr-aria-label)))

  (du/setv! el k-model m))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

;; ── Element class ────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (update-from-attrs! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Public API ───────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
