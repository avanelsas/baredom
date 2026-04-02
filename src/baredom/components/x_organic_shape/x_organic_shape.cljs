(ns baredom.components.x-organic-shape.x-organic-shape
  (:require [goog.object :as gobj]
            [baredom.components.x-organic-shape.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-initialized "__xOrganicShapeInit")
(def ^:private k-base        "__xOrganicShapeBase")
(def ^:private k-slot        "__xOrganicShapeSlot")
(def ^:private k-slotchange  "__xOrganicShapeSlotHandler")

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "box-sizing:border-box;"
   "color-scheme:light dark;"
   "--x-organic-shape-fill:rgba(99,102,241,0.12);"
   "--x-organic-shape-stroke:transparent;"
   "--x-organic-shape-stroke-width:0;"
   "--x-organic-shape-opacity:1;"
   "--x-organic-shape-shadow:none;"
   "--x-organic-shape-animate-duration:8s;"
   "--x-organic-shape-animate-timing:ease-in-out;"
   "--x-organic-shape-animate-direction:normal;"
   "--x-organic-shape-animate-delay:0s;}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-organic-shape-fill:rgba(129,140,248,0.18);}}"

   "[part=shape]{"
   "width:100%;"
   "height:100%;"
   "position:relative;"
   "overflow:hidden;"
   "background:var(--x-organic-shape-fill);"
   "outline:var(--x-organic-shape-stroke-width) solid var(--x-organic-shape-stroke);"
   "outline-offset:calc(-1 * var(--x-organic-shape-stroke-width));"
   "opacity:var(--x-organic-shape-opacity);"
   "filter:var(--x-organic-shape-shadow);"
   "display:flex;align-items:center;justify-content:center;"
   "transition:clip-path 0.3s ease,opacity 0.3s ease;}"

   ;; ── Animation keyframes ──────────────────────────────────────────────

   ;; Morph — interpolates between two clip-path states
   "@keyframes x-organic-shape-morph{"
   "0%,100%{clip-path:var(--x-organic-shape-clip);}"
   "50%{clip-path:var(--x-organic-shape-clip-alt);}}"

   ;; Pulse — gentle scale breathing
   "@keyframes x-organic-shape-pulse{"
   "0%,100%{transform:scale(1);}"
   "50%{transform:scale(1.06);}}"

   ;; Float — slow vertical drift
   "@keyframes x-organic-shape-float{"
   "0%,100%{transform:translateY(0);}"
   "50%{transform:translateY(-6%);}}"

   ;; Spin — continuous rotation
   "@keyframes x-organic-shape-spin{"
   "0%{transform:rotate(0deg);}"
   "100%{transform:rotate(360deg);}}"

   ;; ── Animation selectors ────────────────────────────────────────────

   ":host([data-animation=morph]) [part=shape]{"
   "animation:x-organic-shape-morph "
   "var(--x-organic-shape-animate-duration) "
   "var(--x-organic-shape-animate-timing) "
   "var(--x-organic-shape-animate-delay) infinite "
   "var(--x-organic-shape-animate-direction);}"

   ":host([data-animation=pulse]) [part=shape]{"
   "animation:x-organic-shape-pulse "
   "var(--x-organic-shape-animate-duration) "
   "var(--x-organic-shape-animate-timing) "
   "var(--x-organic-shape-animate-delay) infinite "
   "var(--x-organic-shape-animate-direction);}"

   ":host([data-animation=float]) [part=shape]{"
   "animation:x-organic-shape-float "
   "var(--x-organic-shape-animate-duration) "
   "var(--x-organic-shape-animate-timing) "
   "var(--x-organic-shape-animate-delay) infinite "
   "var(--x-organic-shape-animate-direction);}"

   ":host([data-animation=spin]) [part=shape]{"
   "animation:x-organic-shape-spin "
   "var(--x-organic-shape-animate-duration) "
   "linear "
   "var(--x-organic-shape-animate-delay) infinite "
   "var(--x-organic-shape-animate-direction);}"

   "@media (prefers-reduced-motion:reduce){"
   ":host([data-animation]) [part=shape]{"
   "animation:none;}}"))

;; ── DOM initialisation ────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root     (.attachShadow el #js {:mode "open"})
        style-el (.createElement js/document "style")
        base     (.createElement js/document "div")
        slot-el  (.createElement js/document "slot")]
    (set! (.-textContent style-el) style-text)
    (.setAttribute base "part" "shape")
    (.appendChild base slot-el)
    (.appendChild root style-el)
    (.appendChild root base)
    (gobj/set el k-base        base)
    (gobj/set el k-slot        slot-el)
    (gobj/set el k-initialized true))
  nil)

;; ── Read inputs ───────────────────────────────────────────────────────────
(defn- read-inputs [^js el]
  {:shape   (.getAttribute el model/attr-shape)
   :path    (.getAttribute el model/attr-path)
   :animation (.getAttribute el model/attr-animation)
   :ratio   (.getAttribute el model/attr-ratio)
   :width   (.getAttribute el model/attr-width)
   :height  (.getAttribute el model/attr-height)})

;; ── Accessibility ─────────────────────────────────────────────────────────
(defn- slot-has-content? [^js slot-el]
  (when slot-el
    (pos? (.-length (.assignedNodes slot-el)))))

(defn- update-a11y! [^js el ^js slot-el]
  (if (slot-has-content? slot-el)
    (do (.removeAttribute el "role")
        (.removeAttribute el "aria-hidden"))
    (do (.setAttribute el "role" "presentation")
        (.setAttribute el "aria-hidden" "true"))))

;; ── Render ────────────────────────────────────────────────────────────────
(defn- render! [^js el]
  (let [{:keys [clip clip-alt animation ratio width height]}
        (model/derive-state (read-inputs el))
        ^js base (gobj/get el k-base)
        ^js slot-el (gobj/get el k-slot)]

    ;; Set clip-path via CSS custom properties so the keyframes can reference them
    (.setProperty (.-style base) "--x-organic-shape-clip" clip)
    (if clip-alt
      (.setProperty (.-style base) "--x-organic-shape-clip-alt" clip-alt)
      (.removeProperty (.-style base) "--x-organic-shape-clip-alt"))

    ;; Apply static clip-path (animation overrides this when active)
    (.setProperty (.-style base) "clip-path" clip)

    ;; Aspect ratio
    (.setProperty (.-style base) "aspect-ratio" ratio)

    ;; Size overrides
    (if width
      (.setProperty (.-style base) "width" width)
      (.removeProperty (.-style base) "width"))
    (if height
      (.setProperty (.-style base) "height" height)
      (.removeProperty (.-style base) "height"))

    ;; Animation
    (if (not= animation "none")
      (.setAttribute el "data-animation" animation)
      (.removeAttribute el "data-animation"))

    ;; Accessibility
    (update-a11y! el slot-el))
  nil)

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (gobj/get el k-initialized)
    (init-dom! el))
  ;; Listen for slot changes to update a11y
  (let [^js slot-el (gobj/get el k-slot)
        handler (fn [] (update-a11y! el slot-el))]
    (gobj/set el k-slotchange handler)
    (.addEventListener slot-el "slotchange" handler))
  (render! el)
  nil)

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (when (gobj/get el k-initialized)
      (render! el)))
  nil)

;; ── Property accessors ────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  ;; shape — string, reflects to attr
  (.defineProperty js/Object proto model/attr-shape
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-shape)
                                            model/default-shape)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (and (string? v) (not= "" v))
                                          (.setAttribute this model/attr-shape v)
                                          (.removeAttribute this model/attr-shape))))
                        :enumerable true :configurable true})

  ;; path — string, reflects to attr
  (.defineProperty js/Object proto model/attr-path
                   #js {:get (fn []
                               (this-as ^js this
                                        (.getAttribute this model/attr-path)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (and (string? v) (not= "" v))
                                          (.setAttribute this model/attr-path v)
                                          (.removeAttribute this model/attr-path))))
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

  ;; ratio — string, reflects to attr
  (.defineProperty js/Object proto model/attr-ratio
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-ratio)
                                            model/default-ratio)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (and (string? v) (not= "" v))
                                          (.setAttribute this model/attr-ratio v)
                                          (.removeAttribute this model/attr-ratio))))
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
          (fn []
            (this-as ^js this
                     (let [^js slot-el (gobj/get this k-slot)
                           handler    (gobj/get this k-slotchange)]
                       (when (and slot-el handler)
                         (.removeEventListener slot-el "slotchange" handler))))))

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
