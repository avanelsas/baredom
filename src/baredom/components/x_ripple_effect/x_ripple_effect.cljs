(ns baredom.components.x-ripple-effect.x-ripple-effect
  (:require
[baredom.utils.component :as component]
               [goog.object :as gobj]
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

    (.setAttribute container "part" "container")
    (.appendChild container slot)

    (.setAttribute svg "part" "filters")
    (set! (.. svg -style -position) "absolute")
    (set! (.. svg -style -width) "0")
    (set! (.. svg -style -height) "0")
    (set! (.. svg -style -overflow) "hidden")

    (.appendChild root style)
    (.appendChild root container)
    (.appendChild root svg)

    (gobj/set el k-refs {:root root :container container :svg svg})
    (gobj/set el k-counter 0)
    (gobj/set el k-frames #js []))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:intensity-raw    (.getAttribute el model/attr-intensity)
    :duration-raw     (.getAttribute el model/attr-duration)
    :frequency-raw    (.getAttribute el model/attr-frequency)
    :disabled-present? (.hasAttribute el model/attr-disabled)}))

;; ── SVG filter creation ─────────────────────────────────────────────────────
(defn- create-filter! [^js svg filter-id frequency]
  (let [^js filter-el (.createElementNS js/document svg-ns "filter")
        ^js turb      (.createElementNS js/document svg-ns "feTurbulence")
        ^js disp      (.createElementNS js/document svg-ns "feDisplacementMap")]

    (.setAttribute filter-el "id" filter-id)
    (.setAttribute filter-el "x" "-10%")
    (.setAttribute filter-el "y" "-10%")
    (.setAttribute filter-el "width" "120%")
    (.setAttribute filter-el "height" "120%")

    (.setAttribute turb "type" "turbulence")
    (.setAttribute turb "baseFrequency" (str frequency))
    (.setAttribute turb "numOctaves" "3")
    (.setAttribute turb "seed" (str (js/Math.floor (* (js/Math.random) 1000))))
    (.setAttribute turb "result" "turbulence")

    (.setAttribute disp "in" "SourceGraphic")
    (.setAttribute disp "in2" "turbulence")
    (.setAttribute disp "scale" "0")
    (.setAttribute disp "xChannelSelector" "R")
    (.setAttribute disp "yChannelSelector" "G")

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
            cnt           (gobj/get el k-counter)
            filter-id     (str "xr-" cnt)
            _             (gobj/set el k-counter (inc cnt))
            {:keys [filter-el turb disp]} (create-filter! svg filter-id frequency)
            ^js turb      turb
            ^js disp      disp
            ^js filter-el filter-el
            click-x       (.-clientX event)
            click-y       (.-clientY event)
            start-time    (.now js/performance)
            ^js frames    (gobj/get el k-frames)
            reduced?      (prefers-reduced-motion?)]

        ;; Apply filter to container (inside shadow DOM, same scope as SVG filter)
        (set! (.. container -style -filter) (str "url(#" filter-id ")"))

        ;; Dispatch start event
        (.dispatchEvent el
                        (js/CustomEvent.
                         model/event-start
                         #js {:detail   #js {:x click-x :y click-y}
                              :bubbles  true
                              :composed true}))

        (if reduced?
          ;; Reduced motion: brief flash at half intensity, then remove
          (do
            (.setAttribute disp "scale" (str (/ intensity 2)))
            (let [raf-id (js/requestAnimationFrame
                          (fn []
                            (.setAttribute disp "scale" "0")
                            (.removeChild svg filter-el)
                            (set! (.. container -style -filter) "")
                            (.dispatchEvent el
                                            (js/CustomEvent.
                                             model/event-end
                                             #js {:detail   #js {:x click-x :y click-y}
                                                  :bubbles  true
                                                  :composed true}))))]
              (.push frames raf-id)))

          ;; Normal animation loop
          (letfn [(animate []
                    (let [elapsed  (- (.now js/performance) start-time)
                          progress (js/Math.min 1.0 (/ elapsed duration))
                          eased    (ease-out-cubic progress)
                          current-scale (* intensity (- 1 eased))
                          current-freq  (* frequency (- 1 (* eased 0.7)))]

                      (.setAttribute disp "scale" (str current-scale))
                      (.setAttribute turb "baseFrequency" (str current-freq))

                      (if (>= progress 1.0)
                        ;; Animation complete
                        (do
                          (.removeChild svg filter-el)
                          ;; Clear filter style only if no other ripples active
                          (when (zero? (.-length (.-childNodes svg)))
                            (set! (.. container -style -filter) ""))
                          (.dispatchEvent el
                                          (js/CustomEvent.
                                           model/event-end
                                           #js {:detail   #js {:x click-x :y click-y}
                                                :bubbles  true
                                                :composed true})))
                        ;; Continue animation
                        (let [raf-id (js/requestAnimationFrame animate)]
                          (.push frames raf-id)))))]
            (let [raf-id (js/requestAnimationFrame animate)]
              (.push frames raf-id))))))))

;; ── Event handlers ──────────────────────────────────────────────────────────
(defn- on-pointerdown [^js el ^js e]
  (when (zero? (.-button e))
    (create-ripple! el e))
  nil)

;; ── Listener management ─────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [handler (fn [e] (on-pointerdown el e))]
    (.addEventListener el "pointerdown" handler)
    (gobj/set el k-handler handler))
  nil)

(defn- remove-listeners! [^js el]
  ;; Cancel all active rAF
  (when-let [^js frames (gobj/get el k-frames)]
    (dotimes [i (.-length frames)]
      (js/cancelAnimationFrame (aget frames i)))
    (set! (.-length frames) 0))
  ;; Remove in-flight filter elements
  (when-let [{:keys [svg container]} (gobj/get el k-refs)]
    (let [^js svg svg
          ^js container container]
      (loop []
        (when-let [^js child (.-firstChild svg)]
          (.removeChild svg child)
          (recur)))
      (set! (.. container -style -filter) "")))
  ;; Remove pointer listener
  (when-let [handler (gobj/get el k-handler)]
    (.removeEventListener el "pointerdown" handler)
    (gobj/set el k-handler nil))
  nil)

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (.defineProperty js/Object proto model/attr-intensity
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-intensity
                                         (.getAttribute this model/attr-intensity))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-intensity)
                                          (.setAttribute this model/attr-intensity (str v)))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-duration
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-duration
                                         (.getAttribute this model/attr-duration))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-duration)
                                          (.setAttribute this model/attr-duration (str (int v))))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-frequency
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-frequency
                                         (.getAttribute this model/attr-frequency))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-frequency)
                                          (.setAttribute this model/attr-frequency (str v)))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-disabled
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this model/attr-disabled)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-disabled "")
                                          (.removeAttribute this model/attr-disabled))))
                        :enumerable true :configurable true}))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el)
  nil)

(defn- disconnected! [^js el]
  (remove-listeners! el)
  nil)

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
