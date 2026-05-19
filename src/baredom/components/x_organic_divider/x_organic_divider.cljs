(ns baredom.components.x-organic-divider.x-organic-divider
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baredom.components.x-organic-divider.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-initialized "__xOrganicDividerInit")
(def ^:private k-base        "__xOrganicDividerBase")
(def ^:private k-svg         "__xOrganicDividerSvg")
(def ^:private k-paths       "__xOrganicDividerPaths")
(def ^:private k-model       "__xOrganicDividerModel")

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
    (du/set-attr! base "part" "base")
    (du/set-attr! svg "xmlns" svg-ns)
    (du/set-attr! svg "preserveAspectRatio" "none")
    (du/set-attr! svg "aria-hidden" "true")
    (.appendChild base svg)
    (.appendChild root style-el)
    (.appendChild root base)
    ;; Set permanent a11y attributes on host (always decorative)
    (du/set-attr! el "role" "presentation")
    (du/set-attr! el "aria-hidden" "true")
    (du/setv! el k-base        base)
    (du/setv! el k-svg         svg)
    (du/setv! el k-paths       #js [])
    (du/setv! el k-initialized true)))

;; ── Read inputs ───────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:shape     (du/get-attr el model/attr-shape)
    :layers    (du/get-attr el model/attr-layers)
    :height    (du/get-attr el model/attr-height)
    :flip      (du/get-attr el model/attr-flip)
    :mirror    (du/get-attr el model/attr-mirror)
    :animation (du/get-attr el model/attr-animation)
    :path      (du/get-attr el model/attr-path)}))

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

;; ── DOM patching (cache-at-tail render-pipeline) ──────────────────────────
(defn- apply-paths!
  "Update every <path> element from the derived layer state."
  [^js paths-arr layers transforms effective-path path-alt is-morph]
  (dotimes [i layers]
    (let [^js p     (aget paths-arr i)
          {:keys [x-offset y-offset]} (nth transforms i)
          layer-num (inc i)]
      (du/set-attr! p "d" effective-path)
      (du/set-attr! p "fill"
                     (str "var(--x-organic-divider-color-" layer-num ")"))
      (du/set-attr! p "opacity" (str (layer-opacity i layers)))
      (if (or (pos? x-offset) (pos? y-offset))
        (du/set-attr! p "transform"
                       (str "translate(" x-offset "," y-offset ")"))
        (du/remove-attr! p "transform"))
      (if is-morph
        (do (.setProperty (.-style p) "--x-organic-divider-d"
                          (str "path(\"" effective-path "\")"))
            (when path-alt
              (.setProperty (.-style p) "--x-organic-divider-d-alt"
                            (str "path(\"" path-alt "\")"))))
        (do (.removeProperty (.-style p) "--x-organic-divider-d")
            (.removeProperty (.-style p) "--x-organic-divider-d-alt"))))))

(defn- apply-host-data! [^js el {:keys [flip mirror animation]}]
  (if flip
    (du/set-attr! el "data-flip" "")
    (du/remove-attr! el "data-flip"))
  (if mirror
    (du/set-attr! el "data-mirror" "")
    (du/remove-attr! el "data-mirror"))
  (if (not= animation "none")
    (du/set-attr! el "data-animation" animation)
    (du/remove-attr! el "data-animation")))

(defn- apply-model! [^js el {:keys [path-d path-alt drift-d layers transforms
                                    height animation] :as m}]
  (let [^js base      (du/getv el k-base)
        ^js svg       (du/getv el k-svg)
        ^js paths-arr (du/getv el k-paths)
        is-drift       (= animation "drift")
        is-morph       (= animation "morph")
        effective-path (if is-drift drift-d path-d)]
    (du/set-attr! svg "viewBox" (if is-drift viewbox-drift viewbox-static))
    (ensure-paths! svg paths-arr layers)
    (apply-paths! paths-arr layers transforms effective-path path-alt is-morph)
    (.setProperty (.-style base) "height" height)
    (apply-host-data! el m)
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= new-m old-m)
      (apply-model! el new-m))))

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/getv el k-initialized)
    (init-dom! el))
  (update-from-attrs! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (when (du/getv el k-initialized)
      (update-from-attrs! el))))

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
                                          (du/set-attr! this model/attr-shape v)
                                          (du/remove-attr! this model/attr-shape))))
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
                                          (du/set-attr! this model/attr-layers v)
                                          (du/remove-attr! this model/attr-layers))))
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
                                          (du/set-attr! this model/attr-height v)
                                          (du/remove-attr! this model/attr-height))))
                        :enumerable true :configurable true})

  ;; flip / mirror — booleans
  (du/define-bool-prop! proto model/attr-flip   model/attr-flip)
  (du/define-bool-prop! proto model/attr-mirror model/attr-mirror)

  ;; animation — string, reflects to attr
  (.defineProperty js/Object proto model/attr-animation
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-animation)
                                            model/default-animation)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (and (string? v) (not= "" v))
                                          (du/set-attr! this model/attr-animation v)
                                          (du/remove-attr! this model/attr-animation))))
                        :enumerable true :configurable true})

  ;; path — string, reflects to attr
  (.defineProperty js/Object proto model/attr-path
                   #js {:get (fn []
                               (this-as ^js this
                                        (.getAttribute this model/attr-path)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (and (string? v) (not= "" v))
                                          (du/set-attr! this model/attr-path v)
                                          (du/remove-attr! this model/attr-path))))
                        :enumerable true :configurable true}))

;; ── Element class ─────────────────────────────────────────────────────────
;; ── Public API ────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
