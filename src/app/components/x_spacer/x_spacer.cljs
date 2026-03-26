(ns app.components.x-spacer.x-spacer
  (:require
   [goog.object :as gobj]
   [app.components.x-spacer.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-refs "__xSpacerRefs")

;; ── Styles ────────────────────────────────────────────────────────────────
(def style-text
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
    (gobj/set el k-refs #js {:root root}))
  nil)

;; ── Render ────────────────────────────────────────────────────────────────
(defn- render! [^js el]
  (let [{:keys [size axis grow?]}
        (model/normalize
         {:size-raw (.getAttribute el model/attr-size)
          :axis-raw (.getAttribute el model/attr-axis)
          :grow-raw (.getAttribute el model/attr-grow)})
        ^js style (.-style el)]
    (.setAttribute el "data-axis" axis)
    (.setAttribute el "data-grow" (if grow? "true" "false"))
    (.setProperty style "--x-spacer-size" size)
    (.setAttribute el "role" "none")
    (.setAttribute el "aria-hidden" "true"))
  nil)

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (gobj/get el k-refs)
    (make-shadow! el))
  (render! el))

(defn- disconnected! [^js _el]
  nil)

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (and (not= old-val new-val)
             (gobj/get el k-refs))
    (render! el)))

;; ── Property accessors ────────────────────────────────────────────────────
(defn- def-string-prop! [^js proto attr]
  (.defineProperty js/Object proto attr
                   #js {:get (fn []
                               (this-as ^js this (.getAttribute this attr)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (some? v)
                                          (.setAttribute this attr (str v))
                                          (.removeAttribute this attr))))
                        :enumerable true :configurable true}))

(defn- def-string-prop-default! [^js proto attr default-val]
  (.defineProperty js/Object proto attr
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this attr) default-val)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (some? v)
                                          (.setAttribute this attr (str v))
                                          (.removeAttribute this attr))))
                        :enumerable true :configurable true}))

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
  (def-string-prop-default! proto model/attr-size model/default-size)
  (def-string-prop-default! proto model/attr-axis model/default-axis)
  (def-bool-presence-prop!  proto model/attr-grow))

;; ── Element class ─────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (connected! this)
                     nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (disconnected! this)
                     nil)))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [n o v]
            (this-as ^js this
                     (attribute-changed! this n o v)
                     nil)))

    (install-property-accessors! (.-prototype klass))
    klass))

;; ── Public API ────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
