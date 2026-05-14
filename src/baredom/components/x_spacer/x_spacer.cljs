(ns baredom.components.x-spacer.x-spacer
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baredom.components.x-spacer.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs  "__xSpacerRefs")
(def ^:private k-model "__xSpacerModel")

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "flex-shrink:0;"
   "height:var(--x-spacer-size,1rem);"
   "width:0;"
   "pointer-events:none;}"

   ":host([data-axis='horizontal']){"
   "height:0;"
   "width:var(--x-spacer-size,1rem);}"

   ":host([data-grow='true']){"
   "flex:1 0 var(--x-spacer-size,0px);"
   "height:0;"
   "width:0;}"))

;; ── DOM helpers ───────────────────────────────────────────────────────────
(defn- make-el [tag] (.createElement js/document tag))

;; ── Shadow DOM creation ───────────────────────────────────────────────────
(defn- make-shadow! [^js el]
  (let [^js root  (.attachShadow el #js {:mode "open"})
        ^js style (make-el "style")]
    (set! (.-textContent style) style-text)
    (.appendChild root style)
    (du/setv! el k-refs #js {:root root})))

;; ── Read model ────────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:size-raw (du/get-attr el model/attr-size)
    :axis-raw (du/get-attr el model/attr-axis)
    :grow-raw (du/get-attr el model/attr-grow)}))

;; ── Apply model (cache-at-tail render-pipeline) ───────────────────────────
(defn- apply-model! [^js el {:keys [size axis grow?] :as m}]
  (let [^js style (.-style el)]
    (du/set-attr! el "data-axis" axis)
    (du/set-attr! el "data-grow" (if grow? "true" "false"))
    (.setProperty style "--x-spacer-size" size)
    (du/set-attr! el "role" "none")
    (du/set-attr! el "aria-hidden" "true")
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= new-m old-m)
      (apply-model! el new-m))))

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el))
  (update-from-attrs! el))

(defn- disconnected! [^js _el])

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (and (not= old-val new-val)
             (du/getv el k-refs))
    (update-from-attrs! el)))

;; ── Property accessors ────────────────────────────────────────────────────
;; grow uses model/parse-grow getter — kept inline
(defn- def-bool-presence-prop! [^js proto attr]
  (.defineProperty js/Object proto attr
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-grow (.getAttribute this attr))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this attr "")
                                          (.removeAttribute this attr))))
                        :enumerable true :configurable true}))

(defn- install-property-accessors! [^js proto]
  (du/define-string-prop! proto model/attr-size model/attr-size model/default-size)
  (du/define-string-prop! proto model/attr-axis model/attr-axis model/default-axis)
  (def-bool-presence-prop! proto model/attr-grow))

;; ── Element class ─────────────────────────────────────────────────────────
;; ── Public API ────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
