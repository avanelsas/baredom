(ns baredom.components.x-icon.x-icon
  (:require
   [baredom.components.x-icon.model :as model]
   [baredom.utils.dom :as du]))

;; ── Instance-field keys ──────────────────────────────────────────────────────
(def ^:private k-refs  "__xIconRefs")
(def ^:private k-model "__xIconModel")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def style-text
  (str
   ":host{"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "line-height:0;"
   "vertical-align:middle;"
   "color:var(--x-icon-color,currentColor);"
   "--x-icon-size:20px;}"

   "[part=box]{"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "width:var(--x-icon-size);"
   "height:var(--x-icon-size);}"

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
        slot  (.createElement js/document "slot")]

    (set! (.-textContent style) style-text)

    (.setAttribute box "part" "box")
    (.appendChild box slot)

    (.appendChild root style)
    (.appendChild root box)

    (du/setv! el k-refs
              {:root root
               :box  box}))
  nil)

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

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
    (.setProperty style "--x-icon-size"  size-css)
    (.setProperty style "--x-icon-color" color-css))

  (if labelled?
    (do
      (.setAttribute el "role" "img")
      (.setAttribute el "aria-label" label)
      (.removeAttribute el "aria-hidden"))
    (do
      (.setAttribute el "aria-hidden" "true")
      (.removeAttribute el "role")
      (.removeAttribute el "aria-label")))

  (du/setv! el k-model m)
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m)))
  nil)

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- define-string-attr-prop! [^js proto prop-name attr-name]
  (.defineProperty js/Object proto prop-name
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this attr-name) "")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (or (nil? v) (= v ""))
                                          (.removeAttribute this attr-name)
                                          (.setAttribute this attr-name (str v)))))
                        :enumerable  true
                        :configurable true}))

(defn- install-property-accessors! [^js proto]
  (define-string-attr-prop! proto "size"  model/attr-size)
  (define-string-attr-prop! proto "color" model/attr-color)
  (define-string-attr-prop! proto "label" model/attr-label))

;; ── Element class ────────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (ensure-refs! this)
                     (update-from-attrs! this)
                     nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn [] nil))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [_name old-val new-val]
            (this-as ^js this
                     (when (not= old-val new-val)
                       (update-from-attrs! this))
                     nil)))

    (install-property-accessors! (.-prototype klass))
    klass))

;; ── Public API ───────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
