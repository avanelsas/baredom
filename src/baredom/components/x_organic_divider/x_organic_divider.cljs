(ns baredom.components.x-organic-divider.x-organic-divider
  (:require [goog.object :as gobj]
            [baredom.components.x-organic-divider.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-initialized "__xOrganicDividerInit")
(def ^:private k-base        "__xOrganicDividerBase")
(def ^:private k-svg         "__xOrganicDividerSvg")
(def ^:private k-paths       "__xOrganicDividerPaths")

;; ── Constants ─────────────────────────────────────────────────────────────
(def ^:private svg-ns "http://www.w3.org/2000/svg")
(def ^:private viewbox-static "0 0 1200 120")
(def ^:private viewbox-drift  "0 0 1800 120")

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "box-sizing:border-box;"
   "color-scheme:light dark;"
   "line-height:0;font-size:0;"
   "overflow:hidden;"
   "--x-organic-divider-color:var(--x-color-primary,#6366f1);"
   "--x-organic-divider-color-1:color-mix(in srgb,var(--x-organic-divider-color) 15%,transparent);"
   "--x-organic-divider-color-2:color-mix(in srgb,var(--x-organic-divider-color) 25%,transparent);"
   "--x-organic-divider-color-3:color-mix(in srgb,var(--x-organic-divider-color) 40%,transparent);"
   "--x-organic-divider-color-4:color-mix(in srgb,var(--x-organic-divider-color) 60%,transparent);"
   "--x-organic-divider-color-5:color-mix(in srgb,var(--x-organic-divider-color) 85%,transparent);"
   "--x-organic-divider-height:120px;"
   "--x-organic-divider-animate-duration:6s;"
   "--x-organic-divider-animate-timing:ease-in-out;}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-organic-divider-color:var(--x-color-primary,#818cf8);}}"

   "[part=base]{"
   "width:100%;"
   "position:relative;"
   "overflow:hidden;}"

   "[part=base] svg{"
   "display:block;"
   "width:100%;"
   "height:100%;}"

   ;; ── Flip / Mirror ────────────────────────────────────────────────────
   ":host([data-flip]) [part=base]{"
   "transform:scaleY(-1);}"

   ":host([data-mirror]) [part=base]{"
   "transform:scaleX(-1);}"

   ":host([data-flip][data-mirror]) [part=base]{"
   "transform:scaleX(-1) scaleY(-1);}"

   ;; ── Drift animation ──────────────────────────────────────────────────
   "@keyframes x-organic-divider-drift{"
   "0%{transform:translateX(0);}"
   "100%{transform:translateX(-33.333%);}}"

   ":host([data-animation=drift]) [part=base] svg{"
   "width:150%;"
   "animation:x-organic-divider-drift "
   "var(--x-organic-divider-animate-duration) "
   "linear infinite;}"

   ;; ── Morph animation ──────────────────────────────────────────────────
   ;; Morph uses CSS d property animation on each path element.
   ;; The paths store their primary and alt d values via CSS custom properties.
   "@keyframes x-organic-divider-morph{"
   "0%,100%{d:var(--x-organic-divider-d);}"
   "50%{d:var(--x-organic-divider-d-alt);}}"

   ":host([data-animation=morph]) [part=base] path{"
   "animation:x-organic-divider-morph "
   "var(--x-organic-divider-animate-duration) "
   "var(--x-organic-divider-animate-timing) "
   "infinite;}"

   ;; ── Reduced motion ───────────────────────────────────────────────────
   "@media (prefers-reduced-motion:reduce){"
   ":host([data-animation]) [part=base] svg,"
   ":host([data-animation]) [part=base] path{"
   "animation:none;}}"))

;; ── Layer opacity defaults ────────────────────────────────────────────────
(defn- layer-opacity
  "Returns opacity for layer i (0-based) out of n total layers."
  [i n]
  (if (<= n 1)
    1.0
    (+ 0.3 (* (/ i (dec n)) 0.7))))

;; ── DOM initialisation ────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root     (.attachShadow el #js {:mode "open"})
        style-el (.createElement js/document "style")
        base     (.createElement js/document "div")
        svg      (.createElementNS js/document svg-ns "svg")]
    (set! (.-textContent style-el) style-text)
    (.setAttribute base "part" "base")
    (.setAttribute svg "xmlns" svg-ns)
    (.setAttribute svg "preserveAspectRatio" "none")
    (.setAttribute svg "aria-hidden" "true")
    (.appendChild base svg)
    (.appendChild root style-el)
    (.appendChild root base)
    ;; Set permanent a11y attributes on host (always decorative)
    (.setAttribute el "role" "presentation")
    (.setAttribute el "aria-hidden" "true")
    (gobj/set el k-base        base)
    (gobj/set el k-svg         svg)
    (gobj/set el k-paths       #js [])
    (gobj/set el k-initialized true))
  nil)

;; ── Read inputs ───────────────────────────────────────────────────────────
(defn- read-inputs [^js el]
  {:shape     (.getAttribute el model/attr-shape)
   :layers    (.getAttribute el model/attr-layers)
   :height    (.getAttribute el model/attr-height)
   :flip      (.getAttribute el model/attr-flip)
   :mirror    (.getAttribute el model/attr-mirror)
   :animation (.getAttribute el model/attr-animation)
   :path      (.getAttribute el model/attr-path)})

;; ── Ensure paths ──────────────────────────────────────────────────────────
(defn- ensure-paths!
  "Ensures the SVG has exactly n <path> child elements."
  [^js svg ^js paths-arr n]
  (let [current (.-length paths-arr)]
    (cond
      ;; Need more paths
      (< current n)
      (dotimes [_ (- n current)]
        (let [p (.createElementNS js/document svg-ns "path")]
          (.appendChild svg p)
          (.push paths-arr p)))

      ;; Need fewer paths
      (> current n)
      (dotimes [_ (- current n)]
        (let [p (.pop paths-arr)]
          (.removeChild svg p))))))

;; ── Render ────────────────────────────────────────────────────────────────
(defn- render! [^js el]
  (let [{:keys [path-d path-alt drift-d layers transforms
                height flip mirror animation]}
        (model/derive-state (read-inputs el))

        ^js base      (gobj/get el k-base)
        ^js svg       (gobj/get el k-svg)
        ^js paths-arr (gobj/get el k-paths)

        is-drift (= animation "drift")
        is-morph (= animation "morph")

        ;; Choose path source based on animation
        effective-path (if is-drift drift-d path-d)]

    ;; ViewBox depends on drift animation
    (.setAttribute svg "viewBox" (if is-drift viewbox-drift viewbox-static))

    ;; Ensure correct number of path elements
    (ensure-paths! svg paths-arr layers)

    ;; Update each path
    (dotimes [i layers]
      (let [^js p     (aget paths-arr i)
            {:keys [x-offset y-offset]} (nth transforms i)
            layer-num (inc i)]
        (.setAttribute p "d" effective-path)
        (.setAttribute p "fill"
                       (str "var(--x-organic-divider-color-" layer-num ")"))
        (.setAttribute p "opacity" (str (layer-opacity i layers)))
        (if (or (pos? x-offset) (pos? y-offset))
          (.setAttribute p "transform"
                         (str "translate(" x-offset "," y-offset ")"))
          (.removeAttribute p "transform"))
        ;; Set CSS custom properties for morph animation
        (when is-morph
          (.setProperty (.-style p) "--x-organic-divider-d"
                        (str "path(\"" effective-path "\")"))
          (when path-alt
            (.setProperty (.-style p) "--x-organic-divider-d-alt"
                          (str "path(\"" path-alt "\")"))))
        (when (not is-morph)
          (.removeProperty (.-style p) "--x-organic-divider-d")
          (.removeProperty (.-style p) "--x-organic-divider-d-alt"))))

    ;; Height
    (.setProperty (.-style base) "height" height)

    ;; Flip / Mirror data attributes
    (if flip
      (.setAttribute el "data-flip" "")
      (.removeAttribute el "data-flip"))
    (if mirror
      (.setAttribute el "data-mirror" "")
      (.removeAttribute el "data-mirror"))

    ;; Animation data attribute
    (if (not= animation "none")
      (.setAttribute el "data-animation" animation)
      (.removeAttribute el "data-animation")))
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

  ;; layers — string, reflects to attr
  (.defineProperty js/Object proto model/attr-layers
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-layers)
                                            model/default-layers)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (and (string? v) (not= "" v))
                                          (.setAttribute this model/attr-layers v)
                                          (.removeAttribute this model/attr-layers))))
                        :enumerable true :configurable true})

  ;; height — string, reflects to attr
  (.defineProperty js/Object proto model/attr-height
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-height)
                                            model/default-height)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (and (string? v) (not= "" v))
                                          (.setAttribute this model/attr-height v)
                                          (.removeAttribute this model/attr-height))))
                        :enumerable true :configurable true})

  ;; flip — boolean, reflects to attr
  (.defineProperty js/Object proto model/attr-flip
                   #js {:get (fn []
                               (this-as ^js this
                                        (.hasAttribute this model/attr-flip)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-flip "")
                                          (.removeAttribute this model/attr-flip))))
                        :enumerable true :configurable true})

  ;; mirror — boolean, reflects to attr
  (.defineProperty js/Object proto model/attr-mirror
                   #js {:get (fn []
                               (this-as ^js this
                                        (.hasAttribute this model/attr-mirror)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-mirror "")
                                          (.removeAttribute this model/attr-mirror))))
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
