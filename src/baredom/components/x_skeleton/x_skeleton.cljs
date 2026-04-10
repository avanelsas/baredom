(ns baredom.components.x-skeleton.x-skeleton
  (:require [goog.object :as gobj]
            [baredom.components.x-skeleton.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-initialized "__xSkeletonInit")
(def ^:private k-base        "__xSkeletonBase")

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "box-sizing:border-box;"
   "color-scheme:light dark;"
   "--x-skeleton-color:var(--x-color-border,rgba(0,0,0,0.08));"
   "--x-skeleton-highlight:rgba(255,255,255,0.65);"
   "--x-skeleton-border-radius:var(--x-radius-sm,4px);"
   "--x-skeleton-duration:1.5s;}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-skeleton-color:var(--x-color-border,rgba(255,255,255,0.10));"
   "--x-skeleton-highlight:rgba(255,255,255,0.18);}}"

   ;; Base element — the visible skeleton shape
   "[part=base]{"
   "width:100%;"
   "height:1rem;"
   "position:relative;"
   "overflow:hidden;"
   "background:var(--x-skeleton-color);"
   "border-radius:var(--x-skeleton-border-radius);}"

   ;; Text variant: line height, tighter radius
   "[part=base][data-variant='text']{"
   "height:1em;"
   "border-radius:3px;}"

   ;; Circle variant: square block that becomes a circle
   "[part=base][data-variant='circle']{"
   "width:2.5rem;"
   "height:2.5rem;"
   "border-radius:50%;}"

   ;; Shimmer overlay — used by wave animation only
   "[part=shimmer]{"
   "position:absolute;"
   "inset:0;"
   "background:linear-gradient("
   "90deg,"
   "transparent 0%,"
   "var(--x-skeleton-highlight) 50%,"
   "transparent 100%);"
   "transform:translateX(-100%);"
   "display:none;"
   "pointer-events:none;}"

   ;; Pulse animation
   "@keyframes x-skeleton-pulse{"
   "0%,100%{opacity:1;}"
   "50%{opacity:0.4;}}"

   ;; Wave animation
   "@keyframes x-skeleton-wave{"
   "0%{transform:translateX(-100%);}"
   "100%{transform:translateX(200%);}}"

   "[part=base][data-animation='pulse']{"
   "animation:x-skeleton-pulse var(--x-skeleton-duration,1.5s) ease-in-out infinite;}"

   "[part=base][data-animation='wave'] [part=shimmer]{"
   "display:block;"
   "animation:x-skeleton-wave var(--x-skeleton-duration,1.5s) linear infinite;}"

   ;; Reduced motion
   "@media (prefers-reduced-motion:reduce){"
   "[part=base][data-animation='pulse']{"
   "animation:none;}"
   "[part=base][data-animation='wave'] [part=shimmer]{"
   "animation:none;}}"))

;; ── DOM initialisation ────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root     (.attachShadow el #js {:mode "open"})
        style-el (.createElement js/document "style")
        base     (.createElement js/document "div")
        shimmer  (.createElement js/document "div")]
    (set! (.-textContent style-el) style-text)
    (.setAttribute base    "part" "base")
    (.setAttribute shimmer "part" "shimmer")
    (.appendChild base shimmer)
    (.appendChild root style-el)
    (.appendChild root base)
    (gobj/set el k-base        base)
    (gobj/set el k-initialized true))
  nil)

;; ── Read inputs ───────────────────────────────────────────────────────────
(defn- read-inputs [^js el]
  {:variant   (.getAttribute el model/attr-variant)
   :animation (.getAttribute el model/attr-animation)
   :width     (.getAttribute el model/attr-width)
   :height    (.getAttribute el model/attr-height)})

;; ── Render ────────────────────────────────────────────────────────────────
(defn- render! [^js el]
  (let [{:keys [variant animation width height]}
        (model/derive-state (read-inputs el))
        ^js base (gobj/get el k-base)]
    (.setAttribute base "data-variant"   variant)
    (.setAttribute base "data-animation" animation)
    ;; Size overrides — applied as inline styles on base so they take
    ;; precedence over the variant defaults without touching the host
    (if width
      (.setProperty  (.-style base) "width" width)
      (.removeProperty (.-style base) "width"))
    (if height
      (.setProperty  (.-style base) "height" height)
      (.removeProperty (.-style base) "height"))
    ;; Hide from assistive technology — skeletons carry no semantic content
    (.setAttribute el "aria-hidden" "true"))
  nil)

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (gobj/get el k-initialized)
    (init-dom! el))
  (render! el)
  nil)

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (when (gobj/get el k-initialized)
      (render! el)))
  nil)

;; ── Property accessors ────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  ;; variant — string, reflects to attr, normalised on read-back
  (.defineProperty js/Object proto model/attr-variant
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-variant)
                                            model/default-variant)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (and (string? v) (not= "" v))
                                          (.setAttribute this model/attr-variant v)
                                          (.removeAttribute this model/attr-variant))))
                        :enumerable true :configurable true})
  ;; animation — string, reflects to attr
  (.defineProperty js/Object proto model/attr-animation
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-animation)
                                            model/default-animation)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (and (string? v) (not= "" v))
                                          (.setAttribute this model/attr-animation v)
                                          (.removeAttribute this model/attr-animation))))
                        :enumerable true :configurable true})
  ;; width — string CSS value, reflects to attr
  (.defineProperty js/Object proto model/attr-width
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-width) "")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (and (string? v) (not= "" v))
                                          (.setAttribute this model/attr-width v)
                                          (.removeAttribute this model/attr-width))))
                        :enumerable true :configurable true})
  ;; height — string CSS value, reflects to attr
  (.defineProperty js/Object proto model/attr-height
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-height) "")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (and (string? v) (not= "" v))
                                          (.setAttribute this model/attr-height v)
                                          (.removeAttribute this model/attr-height))))
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
