(ns baredom.components.x-spinner.x-spinner
  (:require
   [goog.object :as gobj]
   [baredom.components.x-spinner.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-initialized "__xSpinnerInit")
(def ^:private k-ring        "__xSpinnerRing")

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "color-scheme:light dark;"
   "--x-spinner-size:24px;"
   "--x-spinner-color:currentColor;"
   "--x-spinner-track-color:rgba(0,0,0,0.12);"
   "--x-spinner-thickness:2px;"
   "--x-spinner-duration:0.75s;}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-spinner-track-color:rgba(255,255,255,0.15);}}"

   ;; Size variants
   ":host([data-size='xs']){--x-spinner-size:16px;}"
   ":host([data-size='sm']){--x-spinner-size:20px;}"
   ":host([data-size='md']){--x-spinner-size:24px;}"
   ":host([data-size='lg']){--x-spinner-size:32px;}"
   ":host([data-size='xl']){--x-spinner-size:48px;}"

   ;; Variant colour overrides
   ":host([data-variant='primary']){--x-spinner-color:var(--x-color-primary,#3b82f6);}"
   ":host([data-variant='success']){--x-spinner-color:var(--x-color-success,#22c55e);}"
   ":host([data-variant='warning']){--x-spinner-color:var(--x-color-warning,#f59e0b);}"
   ":host([data-variant='danger']){--x-spinner-color:var(--x-color-danger,#ef4444);}"

   ;; Ring element
   "[part=ring]{"
   "display:block;"
   "width:var(--x-spinner-size);"
   "height:var(--x-spinner-size);"
   "border-radius:50%;"
   "border:var(--x-spinner-thickness) solid var(--x-spinner-track-color);"
   "border-top-color:var(--x-spinner-color);"
   "box-sizing:border-box;"
   "animation:x-spinner-spin var(--x-spinner-duration) linear infinite;}"

   "@keyframes x-spinner-spin{"
   "to{transform:rotate(360deg);}}"

   "@media (prefers-reduced-motion:reduce){"
   "[part=ring]{animation-play-state:paused;}}"))

;; ── DOM initialisation ────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        ring  (.createElement js/document "span")]
    (set! (.-textContent style) style-text)
    (.setAttribute ring "part" "ring")
    (.setAttribute ring "aria-hidden" "true")
    (.appendChild root style)
    (.appendChild root ring)
    ;; Static a11y attributes on the host — set once
    (.setAttribute el "role"      "status")
    (.setAttribute el "aria-live" "polite")
    (gobj/set el k-ring ring)
    (gobj/set el k-initialized true))
  nil)

;; ── Attribute readers ─────────────────────────────────────────────────────
(defn- read-attrs [^js el]
  {:size    (.getAttribute el model/attr-size)
   :variant (.getAttribute el model/attr-variant)
   :label   (.getAttribute el model/attr-label)})

;; ── Render ────────────────────────────────────────────────────────────────
(defn- render! [^js el]
  (let [{:keys [size variant label]} (model/derive-state (read-attrs el))]
    (.setAttribute el "data-size"    size)
    (.setAttribute el "data-variant" variant)
    (.setAttribute el "aria-label"   label))
  nil)

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (gobj/get el k-initialized)
    (init-dom! el))
  (render! el)
  nil)

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (and (not= old-val new-val) (gobj/get el k-initialized))
    (render! el))
  nil)

;; ── Property accessors ────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (.defineProperty js/Object proto model/attr-size
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-size)
                                            model/default-size)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-size (str v))
                                          (.removeAttribute this model/attr-size))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-variant
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-variant)
                                            model/default-variant)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-variant (str v))
                                          (.removeAttribute this model/attr-variant))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-label
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-label)
                                            model/default-label)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-label (str v))
                                          (.removeAttribute this model/attr-label))))
                        :enumerable true :configurable true}))

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
          (fn [] nil))

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
