(ns baredom.components.x-ripple-effect.x-ripple-effect
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baredom.components.x-ripple-effect.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs      "__xRippleRefs")
(def ^:private k-handler   "__xRippleHandler")
(def ^:private k-counter   "__xRippleCounter")
(def ^:private k-frames    "__xRippleFrames")

;; ── SVG namespace ───────────────────────────────────────────────────────────
(def ^:private svg-ns "http://www.w3.org/2000/svg")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;"
   "position:relative;}"

   ":host([disabled]){"
   "cursor:default;}"

   "[part=container]{"
   "display:block;"
   "width:100%;"
   "height:100%;}"))

;; ── Motion helpers ──────────────────────────────────────────────────────────
(defn- prefers-reduced-motion? []
  (boolean (.-matches (.matchMedia js/window "(prefers-reduced-motion:reduce)"))))

(defn- ease-out-cubic [t]
  (let [t1 (- 1 t)]
    (- 1 (* t1 t1 t1))))

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root      (.attachShadow el #js {:mode "open"})
        style     (.createElement js/document "style")
        container (.createElement js/document "div")
        slot      (.createElement js/document "slot")
        svg       (.createElementNS js/document svg-ns "svg")]

    (set! (.-textContent style) style-text)

    (du/set-attr! container "part" "container")
    (.appendChild container slot)

    (du/set-attr! svg "part" "filters")
    (set! (.. svg -style -position) "absolute")
    (set! (.. svg -style -width) "0")
    (set! (.. svg -style -height) "0")
    (set! (.. svg -style -overflow) "hidden")

    (.appendChild root style)
    (.appendChild root container)
    (.appendChild root svg)

    (du/setv! el k-refs {:root root :container container :svg svg})
    (du/setv! el k-counter 0)
    (du/setv! el k-frames #js [])))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:intensity-raw    (du/get-attr el model/attr-intensity)
    :duration-raw     (du/get-attr el model/attr-duration)
    :frequency-raw    (du/get-attr el model/attr-frequency)
    :disabled-present? (du/has-attr? el model/attr-disabled)}))

;; ── SVG filter creation ─────────────────────────────────────────────────────
(defn- create-filter! [^js svg filter-id frequency]
  (let [^js filter-el (.createElementNS js/document svg-ns "filter")
        ^js turb      (.createElementNS js/document svg-ns "feTurbulence")
        ^js disp      (.createElementNS js/document svg-ns "feDisplacementMap")]

    (du/set-attr! filter-el "id" filter-id)
    (du/set-attr! filter-el "x" "-10%")
    (du/set-attr! filter-el "y" "-10%")
    (du/set-attr! filter-el "width" "120%")
    (du/set-attr! filter-el "height" "120%")

    (du/set-attr! turb "type" "turbulence")
    (du/set-attr! turb "baseFrequency" (str frequency))
    (du/set-attr! turb "numOctaves" "3")
    (du/set-attr! turb "seed" (str (js/Math.floor (* (js/Math.random) 1000))))
    (du/set-attr! turb "result" "turbulence")

    (du/set-attr! disp "in" "SourceGraphic")
    (du/set-attr! disp "in2" "turbulence")
    (du/set-attr! disp "scale" "0")
    (du/set-attr! disp "xChannelSelector" "R")
    (du/set-attr! disp "yChannelSelector" "G")

    (.appendChild filter-el turb)
    (.appendChild filter-el disp)
    (.appendChild svg filter-el)

    {:filter-el filter-el :turb turb :disp disp}))

;; ── Ripple animation ────────────────────────────────────────────────────────
(defn- create-ripple! [^js el ^js event]
  (let [m (read-model el)]
    (when-not (:disabled? m)
      (let [{:keys [intensity duration frequency]} m
            {:keys [container svg]} (ensure-refs! el)
            ^js container container
            ^js svg       svg
            cnt           (du/getv el k-counter)
            filter-id     (str "xr-" cnt)
            _             (du/setv! el k-counter (inc cnt))
            {:keys [filter-el turb disp]} (create-filter! svg filter-id frequency)
            ^js turb      turb
            ^js disp      disp
            ^js filter-el filter-el
            click-x       (.-clientX event)
            click-y       (.-clientY event)
            start-time    (.now js/performance)
            ^js frames    (du/getv el k-frames)
            reduced?      (prefers-reduced-motion?)]

        ;; Apply filter to container (inside shadow DOM, same scope as SVG filter)
        (set! (.. container -style -filter) (str "url(#" filter-id ")"))

        ;; Dispatch start event
        (du/dispatch! el model/event-start #js {:x click-x :y click-y})

        (if reduced?
          ;; Reduced motion: brief flash at half intensity, then remove
          (do
            (du/set-attr! disp "scale" (str (/ intensity 2)))
            (let [raf-id (js/requestAnimationFrame
                          (fn []
                            (du/set-attr! disp "scale" "0")
                            (.removeChild svg filter-el)
                            (set! (.. container -style -filter) "")
                            (du/dispatch! el model/event-end #js {:x click-x :y click-y})))]
              (.push frames raf-id)))

          ;; Normal animation loop
          (letfn [(animate []
                    (let [elapsed  (- (.now js/performance) start-time)
                          progress (js/Math.min 1.0 (/ elapsed duration))
                          eased    (ease-out-cubic progress)
                          current-scale (* intensity (- 1 eased))
                          current-freq  (* frequency (- 1 (* eased 0.7)))]

                      (du/set-attr! disp "scale" (str current-scale))
                      (du/set-attr! turb "baseFrequency" (str current-freq))

                      (if (>= progress 1.0)
                        ;; Animation complete
                        (do
                          (.removeChild svg filter-el)
                          ;; Clear filter style only if no other ripples active
                          (when (zero? (.-length (.-childNodes svg)))
                            (set! (.. container -style -filter) ""))
                          (du/dispatch! el model/event-end #js {:x click-x :y click-y}))
                        ;; Continue animation
                        (let [raf-id (js/requestAnimationFrame animate)]
                          (.push frames raf-id)))))]
            (let [raf-id (js/requestAnimationFrame animate)]
              (.push frames raf-id))))))))

;; ── Event handlers ──────────────────────────────────────────────────────────
(defn- on-pointerdown [^js el ^js e]
  (when (zero? (.-button e))
    (create-ripple! el e)))

;; ── Listener management ─────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [handler (fn [e] (on-pointerdown el e))]
    (.addEventListener el "pointerdown" handler)
    (du/setv! el k-handler handler)))

(defn- remove-listeners! [^js el]
  ;; Cancel all active rAF
  (when-let [^js frames (du/getv el k-frames)]
    (dotimes [i (.-length frames)]
      (js/cancelAnimationFrame (aget frames i)))
    (set! (.-length frames) 0))
  ;; Remove in-flight filter elements
  (when-let [{:keys [svg container]} (du/getv el k-refs)]
    (let [^js svg svg
          ^js container container]
      (loop []
        (when-let [^js child (.-firstChild svg)]
          (.removeChild svg child)
          (recur)))
      (set! (.. container -style -filter) "")))
  ;; Remove pointer listener
  (when-let [handler (du/getv el k-handler)]
    (.removeEventListener el "pointerdown" handler)
    (du/setv! el k-handler nil)))

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/define-parsed-prop! proto model/attr-intensity model/attr-intensity model/parse-intensity)
  (du/define-parsed-prop! proto model/attr-duration  model/attr-duration  model/parse-duration)
  (du/define-parsed-prop! proto model/attr-frequency model/attr-frequency model/parse-frequency)
  (du/define-bool-prop!   proto model/attr-disabled  model/attr-disabled))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js _el _name _old-val _new-val]
  ;; Attributes are read at click time, no render needed
  nil)

;; ── Public API ──────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
