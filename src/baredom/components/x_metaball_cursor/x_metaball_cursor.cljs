(ns baredom.components.x-metaball-cursor.x-metaball-cursor
  (:require
[baredom.utils.component :as component]
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
    (.setAttribute filter-el "id" filter-id)
    (.setAttribute filter-el "x" "-20%")
    (.setAttribute filter-el "y" "-20%")
    (.setAttribute filter-el "width" "140%")
    (.setAttribute filter-el "height" "140%")

    ;; Stage 1: Gaussian blur
    (.setAttribute blur-el "in" "SourceGraphic")
    (.setAttribute blur-el "stdDeviation" (str blur))
    (.setAttribute blur-el "result" "blur")

    ;; Stage 2: Color matrix threshold
    (.setAttribute matrix-el "in" "blur")
    (.setAttribute matrix-el "type" "matrix")
    (.setAttribute matrix-el "values" threshold)
    (.setAttribute matrix-el "result" "threshold")

    ;; Stage 3: Turbulence (for noise)
    (.setAttribute turb-el "type" "fractalNoise")
    (.setAttribute turb-el "baseFrequency" (str (* 0.01 noise-scale)))
    (.setAttribute turb-el "numOctaves" "3")
    (.setAttribute turb-el "seed" "0")
    (.setAttribute turb-el "result" "noise")

    ;; Stage 4: Displacement map (passthrough when noise disabled)
    (.setAttribute disp-el "in" "threshold")
    (.setAttribute disp-el "in2" "noise")
    (.setAttribute disp-el "scale" (if noise? (str noise-intensity) "0"))
    (.setAttribute disp-el "xChannelSelector" "R")
    (.setAttribute disp-el "yChannelSelector" "G")

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
  (let [m         (gobj/get el k-model)
        uid       (next-uid)
        filter-id (str "mb-" uid)
        root      (.attachShadow el #js {:mode "open"})
        style     (.createElement js/document "style")
        viewport  (.createElement js/document "div")
        svg       (.createElementNS js/document svg-ns "svg")]

    (gobj/set el k-uid uid)

    (set! (.-textContent style) style-text)

    (.setAttribute viewport "part" "viewport")
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

      (gobj/set el k-refs
                (merge filter-refs
                       {:root root :viewport viewport :svg svg}))))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:blob-count-raw      (.getAttribute el model/attr-blob-count)
    :blob-size-raw       (.getAttribute el model/attr-blob-size)
    :color-raw           (.getAttribute el model/attr-color)
    :noise-attr          (.getAttribute el model/attr-noise)
    :noise-scale-raw     (.getAttribute el model/attr-noise-scale)
    :noise-speed-raw     (.getAttribute el model/attr-noise-speed)
    :noise-intensity-raw (.getAttribute el model/attr-noise-intensity)
    :blur-raw            (.getAttribute el model/attr-blur)
    :threshold-raw       (.getAttribute el model/attr-threshold)
    :palette-raw         (.getAttribute el model/attr-palette)}))

;; ── Blob management ─────────────────────────────────────────────────────────
(defn- create-blobs!
  "Creates blob divs, appends to viewport, stores in k-blobs array.
   Initialises k-positions to all (0,0)."
  [^js el {:keys [blob-count blob-size colors]}]
  (let [{:keys [viewport]} (gobj/get el k-refs)
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
    (gobj/set el k-blobs arr)
    (gobj/set el k-positions pos)
    (gobj/set el k-speeds (model/blob-speeds blob-count)))
  nil)

(defn- remove-all-blobs! [^js el]
  (when-let [^js arr (gobj/get el k-blobs)]
    (dotimes [i (.-length arr)]
      (let [^js div (aget arr i)]
        (when (.-parentNode div)
          (.removeChild (.-parentNode div) div))))
    (gobj/set el k-blobs #js [])
    (gobj/set el k-positions #js []))
  nil)

(defn- reconcile-blobs!
  "Adds/removes blob divs to match the model, then updates sizes and colors."
  [^js el {:keys [blob-count blob-size colors]}]
  (let [{:keys [viewport]} (gobj/get el k-refs)
        ^js viewport viewport
        ^js arr      (gobj/get el k-blobs)
        ^js pos      (gobj/get el k-positions)
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
    (gobj/set el k-speeds (model/blob-speeds blob-count)))
  nil)

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
  (let [refs (gobj/get el k-refs)]
    (when-let [^js blur-el (:blur-el refs)]
      (.setAttribute blur-el "stdDeviation" (str blur)))
    (when-let [^js matrix-el (:matrix-el refs)]
      (.setAttribute matrix-el "values" threshold))
    (when-let [^js turb-el (:turb-el refs)]
      (.setAttribute turb-el "baseFrequency" (str (* 0.01 noise-scale))))
    (when-let [^js disp-el (:disp-el refs)]
      (.setAttribute disp-el "scale" (if noise? (str noise-intensity) "0"))))
  nil)

;; ── Animation loop ──────────────────────────────────────────────────────────
(defn- animate! [^js el]
  (when (.-isConnected el)
    (let [^js mouse    (gobj/get el k-mouse)
          ^js arr      (gobj/get el k-blobs)
          ^js pos      (gobj/get el k-positions)
          ^js speeds   (gobj/get el k-speeds)
          mx           (gobj/get mouse "x")
          my           (gobj/get mouse "y")
          m            (gobj/get el k-model)
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
        (let [seed      (gobj/get el k-noise-seed)
              new-seed  (+ seed (:noise-speed m))
              refs      (gobj/get el k-refs)]
          (gobj/set el k-noise-seed new-seed)
          (when-let [^js turb-el (:turb-el refs)]
            (.setAttribute turb-el "seed" (str (js/Math.floor new-seed))))))

      ;; Schedule next frame
      (gobj/set el k-raf
                (js/requestAnimationFrame (fn [_] (animate! el))))))
  nil)

(defn- start-animation! [^js el]
  (when-not (prefers-reduced-motion?)
    (gobj/set el k-noise-seed 0)
    (gobj/set el k-raf
              (js/requestAnimationFrame (fn [_] (animate! el)))))
  nil)

(defn- stop-animation! [^js el]
  (when-let [raf-id (gobj/get el k-raf)]
    (js/cancelAnimationFrame raf-id)
    (gobj/set el k-raf nil))
  nil)

;; ── Mouse tracking ──────────────────────────────────────────────────────────
(defn- on-pointermove [^js el ^js e]
  (let [refs (gobj/get el k-refs)]
    (when-let [^js viewport (:viewport refs)]
      (let [^js rect (.getBoundingClientRect viewport)
            ^js mouse (gobj/get el k-mouse)]
        (gobj/set mouse "x" (- (.-clientX e) (.-left rect)))
        (gobj/set mouse "y" (- (.-clientY e) (.-top rect))))))
  nil)

;; ── Listener management ─────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [handler (fn [e] (on-pointermove el e))]
    (.addEventListener js/window "pointermove" handler)
    (gobj/set el k-handler handler))
  nil)

(defn- remove-listeners! [^js el]
  (when-let [handler (gobj/get el k-handler)]
    (.removeEventListener js/window "pointermove" handler)
    (gobj/set el k-handler nil))
  nil)

;; ── Accessibility ───────────────────────────────────────────────────────────
(defn- set-a11y! [^js el]
  (.setAttribute el "aria-hidden" "true")
  (.setAttribute el "role" "presentation")
  nil)

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  ;; blobCount → blob-count
  (.defineProperty js/Object proto "blobCount"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-blob-count
                                         (.getAttribute this model/attr-blob-count))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-blob-count)
                                          (.setAttribute this model/attr-blob-count (str (int v))))))
                        :enumerable true :configurable true})

  ;; blobSize → blob-size
  (.defineProperty js/Object proto "blobSize"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-blob-size
                                         (.getAttribute this model/attr-blob-size))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-blob-size)
                                          (.setAttribute this model/attr-blob-size (str v)))))
                        :enumerable true :configurable true})

  ;; color
  (.defineProperty js/Object proto "color"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-color
                                         (.getAttribute this model/attr-color))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-color)
                                          (.setAttribute this model/attr-color (str v)))))
                        :enumerable true :configurable true})

  ;; noise (boolean)
  (.defineProperty js/Object proto "noise"
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this model/attr-noise)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-noise "")
                                          (.removeAttribute this model/attr-noise))))
                        :enumerable true :configurable true})

  ;; noiseScale → noise-scale
  (.defineProperty js/Object proto "noiseScale"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-noise-scale
                                         (.getAttribute this model/attr-noise-scale))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-noise-scale)
                                          (.setAttribute this model/attr-noise-scale (str v)))))
                        :enumerable true :configurable true})

  ;; noiseSpeed → noise-speed
  (.defineProperty js/Object proto "noiseSpeed"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-noise-speed
                                         (.getAttribute this model/attr-noise-speed))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-noise-speed)
                                          (.setAttribute this model/attr-noise-speed (str v)))))
                        :enumerable true :configurable true})

  ;; noiseIntensity → noise-intensity
  (.defineProperty js/Object proto "noiseIntensity"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-noise-intensity
                                         (.getAttribute this model/attr-noise-intensity))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-noise-intensity)
                                          (.setAttribute this model/attr-noise-intensity (str v)))))
                        :enumerable true :configurable true})

  ;; blur
  (.defineProperty js/Object proto "blur"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-blur
                                         (.getAttribute this model/attr-blur))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-blur)
                                          (.setAttribute this model/attr-blur (str v)))))
                        :enumerable true :configurable true})

  ;; threshold
  (.defineProperty js/Object proto "threshold"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-threshold
                                         (.getAttribute this model/attr-threshold))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-threshold)
                                          (.setAttribute this model/attr-threshold (str v)))))
                        :enumerable true :configurable true})

  ;; palette
  (.defineProperty js/Object proto "palette"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-palette
                                         (.getAttribute this model/attr-palette))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (or (nil? v) (= "" v))
                                          (.removeAttribute this model/attr-palette)
                                          (.setAttribute this model/attr-palette (str v)))))
                        :enumerable true :configurable true}))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (let [m (read-model el)]
  (gobj/set el k-model m)
  (gobj/set el k-mouse #js {:x 0 :y 0})
  (ensure-refs! el)
  (remove-all-blobs! el)
  (create-blobs! el m)
  (apply-host-style! el m)
  (set-a11y! el)
  (remove-listeners! el)
  (add-listeners! el)
  (start-animation! el))
  nil)

(defn- disconnected! [^js el]
  (stop-animation! el)
  (remove-listeners! el)
  nil)

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
  (let [m (read-model el)]
  (gobj/set el k-model m)
  (when (gobj/get el k-refs)
  (reconcile-blobs! el m)
  (update-filter! el m)
  (apply-host-style! el m)))))

;; ── Public API ──────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
