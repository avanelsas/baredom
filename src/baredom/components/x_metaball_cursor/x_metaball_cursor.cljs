(ns baredom.components.x-metaball-cursor.x-metaball-cursor
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-metaball-cursor.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs       "__xMetaballRefs")
(def ^:private k-blobs      "__xMetaballBlobs")
(def ^:private k-positions  "__xMetaballPos")
(def ^:private k-mouse      "__xMetaballMouse")
(def ^:private k-raf        "__xMetaballRaf")
(def ^:private k-handler    "__xMetaballHandler")
(def ^:private k-model      "__xMetaballModel")
(def ^:private k-uid        "__xMetaballUid")
(def ^:private k-noise-seed "__xMetaballSeed")
(def ^:private k-speeds     "__xMetaballSpeeds")

;; ── UID counter (no atoms — plain JS array) ─────────────────────────────────
(def ^:private uid-counter #js [0])

(defn- next-uid []
  (let [n (aget uid-counter 0)]
    (aset uid-counter 0 (inc n))
    n))

;; ── SVG namespace ───────────────────────────────────────────────────────────
(def ^:private svg-ns "http://www.w3.org/2000/svg")

;; ── Styles ──────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "position:relative;"
   "width:100%;"
   "height:100%;"
   "overflow:hidden;"
   "pointer-events:none;"
   "z-index:var(" model/css-z-index ",9999);}"

   "[part=viewport]{"
   "position:absolute;"
   "inset:var(" model/css-inset ",0);"
   "pointer-events:none;"
   "opacity:var(" model/css-opacity ",1);"
   "mix-blend-mode:var(" model/css-blend-mode ",normal);}"

   ".blob{"
   "position:absolute;"
   "border-radius:50%;"
   "will-change:transform;"
   "pointer-events:none;"
   "top:0;left:0;"
   "background:var(" model/css-color ",#6366f1);}"

   "@media(prefers-reduced-motion:reduce){"
   ".blob{transition:none!important;}}"))

;; ── Motion helper ───────────────────────────────────────────────────────────
(defn- prefers-reduced-motion? []
  (boolean (.-matches (.matchMedia js/window "(prefers-reduced-motion:reduce)"))))

;; ── SVG filter creation ─────────────────────────────────────────────────────
(defn- create-svg-filter!
  "Creates the metaball SVG filter chain and appends it to the SVG defs.
   Returns a map of element refs for later attribute updates."
  [^js svg filter-id {:keys [blur threshold noise? noise-scale noise-intensity]}]
  (let [^js defs      (.createElementNS js/document svg-ns "defs")
        ^js filter-el (.createElementNS js/document svg-ns "filter")
        ^js blur-el   (.createElementNS js/document svg-ns "feGaussianBlur")
        ^js matrix-el (.createElementNS js/document svg-ns "feColorMatrix")
        ^js turb-el   (.createElementNS js/document svg-ns "feTurbulence")
        ^js disp-el   (.createElementNS js/document svg-ns "feDisplacementMap")]

    ;; Filter bounds — oversized to prevent clipping
    (du/set-attr! filter-el "id" filter-id)
    (du/set-attr! filter-el "x" "-20%")
    (du/set-attr! filter-el "y" "-20%")
    (du/set-attr! filter-el "width" "140%")
    (du/set-attr! filter-el "height" "140%")

    ;; Stage 1: Gaussian blur
    (du/set-attr! blur-el "in" "SourceGraphic")
    (du/set-attr! blur-el "stdDeviation" (str blur))
    (du/set-attr! blur-el "result" "blur")

    ;; Stage 2: Color matrix threshold
    (du/set-attr! matrix-el "in" "blur")
    (du/set-attr! matrix-el "type" "matrix")
    (du/set-attr! matrix-el "values" threshold)
    (du/set-attr! matrix-el "result" "threshold")

    ;; Stage 3: Turbulence (for noise)
    (du/set-attr! turb-el "type" "fractalNoise")
    (du/set-attr! turb-el "baseFrequency" (str (* 0.01 noise-scale)))
    (du/set-attr! turb-el "numOctaves" "3")
    (du/set-attr! turb-el "seed" "0")
    (du/set-attr! turb-el "result" "noise")

    ;; Stage 4: Displacement map (passthrough when noise disabled)
    (du/set-attr! disp-el "in" "threshold")
    (du/set-attr! disp-el "in2" "noise")
    (du/set-attr! disp-el "scale" (if noise? (str noise-intensity) "0"))
    (du/set-attr! disp-el "xChannelSelector" "R")
    (du/set-attr! disp-el "yChannelSelector" "G")

    (.appendChild filter-el blur-el)
    (.appendChild filter-el matrix-el)
    (.appendChild filter-el turb-el)
    (.appendChild filter-el disp-el)
    (.appendChild defs filter-el)
    (.appendChild svg defs)

    {:filter-el filter-el
     :blur-el   blur-el
     :matrix-el matrix-el
     :turb-el   turb-el
     :disp-el   disp-el}))

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [m         (du/getv el k-model)
        uid       (next-uid)
        filter-id (str "mb-" uid)
        root      (.attachShadow el #js {:mode "open"})
        style     (.createElement js/document "style")
        viewport  (.createElement js/document "div")
        svg       (.createElementNS js/document svg-ns "svg")]

    (du/setv! el k-uid uid)

    (set! (.-textContent style) style-text)

    (du/set-attr! viewport "part" "viewport")
    (set! (.. viewport -style -filter) (str "url(#" filter-id ")"))

    ;; Hidden SVG for filter definitions
    (set! (.. svg -style -position) "absolute")
    (set! (.. svg -style -width) "0")
    (set! (.. svg -style -height) "0")
    (set! (.. svg -style -overflow) "hidden")

    (let [filter-refs (create-svg-filter! svg filter-id m)]
      (.appendChild viewport svg)
      (.appendChild root style)
      (.appendChild root viewport)

      (du/setv! el k-refs
                (merge filter-refs
                       {:root root :viewport viewport :svg svg})))))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:blob-count-raw      (du/get-attr el model/attr-blob-count)
    :blob-size-raw       (du/get-attr el model/attr-blob-size)
    :color-raw           (du/get-attr el model/attr-color)
    :noise-attr          (du/get-attr el model/attr-noise)
    :noise-scale-raw     (du/get-attr el model/attr-noise-scale)
    :noise-speed-raw     (du/get-attr el model/attr-noise-speed)
    :noise-intensity-raw (du/get-attr el model/attr-noise-intensity)
    :blur-raw            (du/get-attr el model/attr-blur)
    :threshold-raw       (du/get-attr el model/attr-threshold)
    :palette-raw         (du/get-attr el model/attr-palette)}))

;; ── Blob management ─────────────────────────────────────────────────────────
(defn- create-blobs!
  "Creates blob divs, appends to viewport, stores in k-blobs array.
   Initialises k-positions to all (0,0)."
  [^js el {:keys [blob-count blob-size colors]}]
  (let [{:keys [viewport]} (du/getv el k-refs)
        ^js viewport viewport
        sizes   (model/blob-sizes blob-size blob-count)
        ^js arr #js []
        ^js pos #js []]
    (dotimes [i blob-count]
      (let [div  (.createElement js/document "div")
            size (aget sizes i)]
        (set! (.-className div) "blob")
        (set! (.. div -style -width) (str size "px"))
        (set! (.. div -style -height) (str size "px"))
        (set! (.. div -style -background) (aget colors i))
        (.appendChild viewport div)
        (.push arr div)
        (.push pos #js {:x 0 :y 0})))
    (du/setv! el k-blobs arr)
    (du/setv! el k-positions pos)
    (du/setv! el k-speeds (model/blob-speeds blob-count))))

(defn- remove-all-blobs! [^js el]
  (when-let [^js arr (du/getv el k-blobs)]
    (dotimes [i (.-length arr)]
      (let [^js div (aget arr i)]
        (when (.-parentNode div)
          (.removeChild (.-parentNode div) div))))
    (du/setv! el k-blobs #js [])
    (du/setv! el k-positions #js [])))

(defn- reconcile-blobs!
  "Adds/removes blob divs to match the model, then updates sizes and colors."
  [^js el {:keys [blob-count blob-size colors]}]
  (let [{:keys [viewport]} (du/getv el k-refs)
        ^js viewport viewport
        ^js arr      (du/getv el k-blobs)
        ^js pos      (du/getv el k-positions)
        current      (.-length arr)
        sizes        (model/blob-sizes blob-size blob-count)]
    ;; Add missing blobs
    (loop [i current]
      (when (< i blob-count)
        (let [div (.createElement js/document "div")]
          (set! (.-className div) "blob")
          (.appendChild viewport div)
          (.push arr div)
          (.push pos #js {:x 0 :y 0}))
        (recur (inc i))))
    ;; Remove excess blobs
    (loop [i current]
      (when (> i blob-count)
        (let [^js div (.pop arr)]
          (.pop pos)
          (when (.-parentNode div)
            (.removeChild (.-parentNode div) div)))
        (recur (dec i))))
    ;; Update sizes and colors
    (dotimes [i blob-count]
      (let [^js div (aget arr i)
            size    (aget sizes i)]
        (set! (.. div -style -width) (str size "px"))
        (set! (.. div -style -height) (str size "px"))
        (set! (.. div -style -background) (aget colors i))))
    ;; Recompute speeds
    (du/setv! el k-speeds (model/blob-speeds blob-count))))

;; ── Host style sync ─────────────────────────────────────────────────────────
(defn- apply-host-style!
  "Pushes model values onto the host element as CSS custom properties so that
   shadow-DOM styles pick them up."
  [^js el {:keys [color]}]
  (.setProperty (.-style el) model/css-color color))

;; ── Filter updates ──────────────────────────────────────────────────────────
(defn- update-filter!
  "Updates SVG filter elements to reflect current model."
  [^js el {:keys [blur threshold noise? noise-scale noise-intensity]}]
  (let [refs (du/getv el k-refs)]
    (when-let [^js blur-el (:blur-el refs)]
      (du/set-attr! blur-el "stdDeviation" (str blur)))
    (when-let [^js matrix-el (:matrix-el refs)]
      (du/set-attr! matrix-el "values" threshold))
    (when-let [^js turb-el (:turb-el refs)]
      (du/set-attr! turb-el "baseFrequency" (str (* 0.01 noise-scale))))
    (when-let [^js disp-el (:disp-el refs)]
      (du/set-attr! disp-el "scale" (if noise? (str noise-intensity) "0")))))

;; ── Animation loop ──────────────────────────────────────────────────────────
(defn- animate! [^js el]
  (when (.-isConnected el)
    (let [^js mouse    (du/getv el k-mouse)
          ^js arr      (du/getv el k-blobs)
          ^js pos      (du/getv el k-positions)
          ^js speeds   (du/getv el k-speeds)
          mx           (gobj/get mouse "x")
          my           (gobj/get mouse "y")
          m            (du/getv el k-model)
          n            (.-length arr)]
      ;; Lerp each blob toward mouse
      (dotimes [i n]
        (let [^js p     (aget pos i)
              speed     (aget speeds i)
              cx        (gobj/get p "x")
              cy        (gobj/get p "y")
              nx        (model/lerp cx mx speed)
              ny        (model/lerp cy my speed)
              ^js div   (aget arr i)]
          (gobj/set p "x" nx)
          (gobj/set p "y" ny)
          (set! (.. div -style -transform)
                (str "translate(" nx "px," ny "px) translate(-50%,-50%)"))))

      ;; Animate noise shimmer
      (when (:noise? m)
        (let [seed      (du/getv el k-noise-seed)
              new-seed  (+ seed (:noise-speed m))
              refs      (du/getv el k-refs)]
          (du/setv! el k-noise-seed new-seed)
          (when-let [^js turb-el (:turb-el refs)]
            (du/set-attr! turb-el "seed" (str (js/Math.floor new-seed))))))

      ;; Schedule next frame
      (du/setv! el k-raf
                (js/requestAnimationFrame (fn [_] (animate! el)))))))

(defn- start-animation! [^js el]
  (when-not (prefers-reduced-motion?)
    (du/setv! el k-noise-seed 0)
    (du/setv! el k-raf
              (js/requestAnimationFrame (fn [_] (animate! el))))))

(defn- stop-animation! [^js el]
  (when-let [raf-id (du/getv el k-raf)]
    (js/cancelAnimationFrame raf-id)
    (du/setv! el k-raf nil)))

;; ── Mouse tracking ──────────────────────────────────────────────────────────
(defn- on-pointermove [^js el ^js e]
  (let [refs (du/getv el k-refs)]
    (when-let [^js viewport (:viewport refs)]
      (let [^js rect (.getBoundingClientRect viewport)
            ^js mouse (du/getv el k-mouse)]
        (gobj/set mouse "x" (- (.-clientX e) (.-left rect)))
        (gobj/set mouse "y" (- (.-clientY e) (.-top rect)))))))

;; ── Listener management ─────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [handler (fn [e] (on-pointermove el e))]
    (.addEventListener js/window "pointermove" handler)
    (du/setv! el k-handler handler)))

(defn- remove-listeners! [^js el]
  (when-let [handler (du/getv el k-handler)]
    (.removeEventListener js/window "pointermove" handler)
    (du/setv! el k-handler nil)))

;; ── Accessibility ───────────────────────────────────────────────────────────
(defn- set-a11y! [^js el]
  (du/set-attr! el "aria-hidden" "true")
  (du/set-attr! el "role" "presentation"))

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/define-parsed-prop! proto "blobCount"      model/attr-blob-count      model/parse-blob-count)
  (du/define-parsed-prop! proto "blobSize"       model/attr-blob-size       model/parse-blob-size)
  (du/define-parsed-prop! proto "color"          model/attr-color           model/parse-color)
  (du/define-bool-prop!   proto "noise"          model/attr-noise)
  (du/define-parsed-prop! proto "noiseScale"     model/attr-noise-scale     model/parse-noise-scale)
  (du/define-parsed-prop! proto "noiseSpeed"     model/attr-noise-speed     model/parse-noise-speed)
  (du/define-parsed-prop! proto "noiseIntensity" model/attr-noise-intensity model/parse-noise-intensity)
  (du/define-parsed-prop! proto "blur"           model/attr-blur            model/parse-blur)
  (du/define-parsed-prop! proto "threshold"      model/attr-threshold       model/parse-threshold)
  ;; palette has strict-empty setter semantics: setting "" removes the attr
  ;; (so the palette falls back to its default), unlike define-parsed-prop!
  ;; which would store "" as an explicit empty attribute.
  (.defineProperty js/Object proto "palette"
    #js {:get (fn []
                (this-as ^js this
                  (model/parse-palette (.getAttribute this model/attr-palette))))
         :set (fn [v]
                (this-as ^js this
                  (if (or (nil? v) (= "" v))
                    (du/remove-attr! this model/attr-palette)
                    (du/set-attr! this model/attr-palette (str v)))))
         :enumerable true :configurable true}))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (let [m (read-model el)]
    (du/setv! el k-model m)
    (du/setv! el k-mouse #js {:x 0 :y 0})
    (ensure-refs! el)
    (remove-all-blobs! el)
    (create-blobs! el m)
    (apply-host-style! el m)
    (set-a11y! el)
    (remove-listeners! el)
    (add-listeners! el)
    (start-animation! el)))

(defn- disconnected! [^js el]
  (stop-animation! el)
  (remove-listeners! el))

;; ── Apply model + update-from-attrs! (cache-at-tail render-pipeline) ──────
(defn- apply-model! [^js el m]
  (when (du/getv el k-refs)
    (reconcile-blobs! el m)
    (update-filter! el m)
    (apply-host-style! el m))
  (du/setv! el k-model m))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= new-m old-m)
      (apply-model! el new-m))))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Public API ──────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
