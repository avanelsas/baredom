(ns baredom.components.x-scroll-parallax.x-scroll-parallax
  (:require
[baredom.utils.dom :as du]
               [goog.object :as gobj]
   [baredom.components.x-scroll-parallax.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs        "__xScrollParallaxRefs")
(def ^:private k-model       "__xScrollParallaxModel")
(def ^:private k-handlers    "__xScrollParallaxHandlers")
(def ^:private k-io          "__xScrollParallaxIO")
(def ^:private k-raf         "__xScrollParallaxRAF")
(def ^:private k-visible     "__xScrollParallaxVisible")
(def ^:private k-child-cache "__xScrollParallaxChildCache")
(def ^:private k-last-prog   "__xScrollParallaxLastProg")

;; ── Styles ──────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;position:relative;"
   "color-scheme:light dark;"
   "--x-scroll-parallax-perspective:none;"
   "--x-scroll-parallax-overflow:hidden;"
   "--x-scroll-parallax-smooth-duration:80ms;"
   "--x-scroll-parallax-fade-range:20%;"
   "--x-scroll-parallax-scale-min:0.85;"
   "--x-scroll-parallax-disabled-opacity:0.55;}"

   "[part=viewport]{"
   "position:relative;width:100%;height:100%;"
   "overflow:var(--x-scroll-parallax-overflow);"
   "perspective:var(--x-scroll-parallax-perspective);}"

   "::slotted(*){"
   "will-change:transform;}"

   ":host([disabled]){"
   "opacity:var(--x-scroll-parallax-disabled-opacity);}"

   ":host([easing=smooth]) ::slotted(*){"
   "transition:transform var(--x-scroll-parallax-smooth-duration) ease-out,"
   "opacity var(--x-scroll-parallax-smooth-duration) ease-out;}"

   "[part=live]{"
   "position:absolute;width:1px;height:1px;margin:-1px;"
   "padding:0;overflow:hidden;clip:rect(0,0,0,0);border:0;}"

   "@media (prefers-reduced-motion:reduce){"
   "::slotted(*){transform:none !important;opacity:1 !important;}}"))

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root     (.attachShadow el #js {:mode "open"})
        style    (.createElement js/document "style")
        viewport (.createElement js/document "div")
        slot     (.createElement js/document "slot")
        live     (.createElement js/document "div")]

    (set! (.-textContent style) style-text)

    (.setAttribute viewport "part" "viewport")
    (.setAttribute viewport "role" "region")

    (.appendChild viewport slot)

    (.setAttribute live "part" "live")
    (.setAttribute live "aria-live" "polite")
    (.setAttribute live "aria-atomic" "true")

    (.appendChild root style)
    (.appendChild root viewport)
    (.appendChild root live)

    (gobj/set el k-refs
              {:root     root
               :viewport viewport
               :slot     slot
               :live     live}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:direction-raw (.getAttribute el model/attr-direction)
    :source-raw    (.getAttribute el model/attr-source)
    :easing-raw    (.getAttribute el model/attr-easing)
    :disabled-attr (when (.hasAttribute el model/attr-disabled) "")
    :label-raw     (.getAttribute el model/attr-label)}))

;; ── Helpers ─────────────────────────────────────────────────────────────────
(defn- get-children [^js el]
  (let [{:keys [slot]} (ensure-refs! el)]
    (.assignedElements ^js slot)))

(defn- prefers-reduced-motion? []
  (boolean (.-matches (.matchMedia js/window "(prefers-reduced-motion:reduce)"))))

(defn- vertical? [m]
  (= "vertical" (:direction m)))

;; ── Child data cache ────────────────────────────────────────────────────────
(defn- read-child-data
  "Read parallax data attributes from a single child element."
  [^js child]
  {:speed   (model/parse-speed   (.getAttribute child model/data-speed))
   :offset  (model/parse-offset  (.getAttribute child model/data-offset))
   :opacity (= "fade" (.getAttribute child model/data-opacity))
   :scale   (= "grow" (.getAttribute child model/data-scale))})

(defn- ensure-child-cache!
  "Return an array of child data maps, building the cache if needed."
  [^js el]
  (or (gobj/get el k-child-cache)
      (let [children (get-children el)
            cache    (into [] (map read-child-data) (array-seq children))]
        (gobj/set el k-child-cache cache)
        cache)))

(defn- invalidate-child-cache! [^js el]
  (gobj/set el k-child-cache nil))

;; ── CSS custom property readers ─────────────────────────────────────────────
(defn- read-fade-range
  "Read --x-scroll-parallax-fade-range from computed styles. Returns float [0,1]."
  [^js el]
  (let [raw (.getPropertyValue (.getComputedStyle js/window el) "--x-scroll-parallax-fade-range")
        trimmed (when (string? raw) (.trim raw))]
    (if (and trimmed (.endsWith trimmed "%"))
      (let [n (js/parseFloat (.slice trimmed 0 -1))]
        (if (js/isNaN n) 0.2 (/ n 100.0)))
      0.2)))

(defn- read-scale-min
  "Read --x-scroll-parallax-scale-min from computed styles. Returns float."
  [^js el]
  (let [raw (.getPropertyValue (.getComputedStyle js/window el) "--x-scroll-parallax-scale-min")
        n   (when (string? raw) (js/parseFloat (.trim raw)))]
    (if (or (nil? n) (js/isNaN n)) 0.85 n)))

;; ── Event dispatch ──────────────────────────────────────────────────────────
;; ── Transform application ───────────────────────────────────────────────────
(defn- update-transforms!
  "Compute and apply parallax transforms to all slotted children.
  Called from rAF callback."
  [^js el]
  (let [m (or (gobj/get el k-model) (read-model el))]
    (when-not (:disabled? m)
      (let [rect       (.getBoundingClientRect el)
            vert?      (vertical? m)
            ;; Progress is always computed from vertical scroll position
            ;; (source="document" means window vertical scroll).
            ;; Direction only controls the transform axis (translateX vs translateY).
            el-top     (.-top rect)
            el-height  (.-height rect)
            vp-height  (.-innerHeight js/window)
            ;; Viewport size for parallax offset magnitude follows direction
            vp-size    (if vert? vp-height (.-innerWidth js/window))
            progress   (model/compute-progress el-top el-height vp-height)
            last-prog  (gobj/get el k-last-prog)
            children   (get-children el)
            child-data (ensure-child-cache! el)
            fade-range (read-fade-range el)
            scale-min  (read-scale-min el)]

        ;; Apply transforms to each child
        (dotimes [i (.-length children)]
          (when (< i (count child-data))
            (let [^js child (aget children i)
                  {:keys [speed offset opacity scale]} (nth child-data i)
                  parallax-px (model/compute-parallax-offset
                               progress speed vp-size offset)
                  transform-str (if vert?
                                  (str "translateY(" parallax-px "px)")
                                  (str "translateX(" parallax-px "px)"))
                  ;; Compose scale if needed
                  transform-str (if scale
                                  (let [s (model/compute-scale progress scale-min)]
                                    (str transform-str " scale(" s ")"))
                                  transform-str)]
              (set! (.. child -style -transform) transform-str)
              ;; Apply opacity if needed
              (when opacity
                (let [o (model/compute-fade-opacity progress fade-range)]
                  (set! (.. child -style -opacity) (str o)))))))

        ;; Dispatch progress event if progress changed
        (when (or (nil? last-prog)
                  (> (js/Math.abs (- progress last-prog)) 0.001))
          (gobj/set el k-last-prog progress)
          (du/dispatch! el model/event-progress (clj->js (model/progress-detail progress)))))))
  ;; Clear rAF handle
  (gobj/set el k-raf nil))

;; ── Scroll handler ──────────────────────────────────────────────────────────
(defn- on-scroll [^js el]
  (when (and (.-isConnected el)
             (gobj/get el k-visible)
             (not (prefers-reduced-motion?)))
    (when-not (gobj/get el k-raf)
      (gobj/set el k-raf
                (js/requestAnimationFrame
                 (fn [_] (update-transforms! el)))))))

;; ── IntersectionObserver callback ───────────────────────────────────────────
(defn- attach-scroll-listener! [^js el]
  (let [hs (gobj/get el k-handlers)]
    (when (and hs (not (gobj/get hs "scrollAttached")))
      (let [scroll-h (gobj/get hs "scroll")]
        (.addEventListener js/window "scroll" scroll-h #js {:passive true})
        (gobj/set hs "scrollAttached" true)))))

(defn- detach-scroll-listener! [^js el]
  (let [hs (gobj/get el k-handlers)]
    (when (and hs (gobj/get hs "scrollAttached"))
      (let [scroll-h (gobj/get hs "scroll")]
        (.removeEventListener js/window "scroll" scroll-h)
        (gobj/set hs "scrollAttached" false)))))

(defn- announce! [^js _el ^js live msg]
  (set! (.-textContent live) msg)
  (js/setTimeout (fn [] (set! (.-textContent live) "")) 1000))

(defn- on-intersection [^js el ^js entries]
  (let [^js entry (aget entries 0)
        is-intersecting (.-isIntersecting entry)
        {:keys [live]} (gobj/get el k-refs)]
    (gobj/set el k-visible is-intersecting)
    (if is-intersecting
      (do
        (attach-scroll-listener! el)
        ;; Run one immediate update to position children correctly
        (update-transforms! el)
        (announce! el live "Parallax section entered viewport")
        (du/dispatch! el model/event-enter
                   (clj->js (model/progress-detail (or (gobj/get el k-last-prog) 0)))))
      (do
        (detach-scroll-listener! el)
        (announce! el live "Parallax section left viewport")
        (du/dispatch! el model/event-leave
                   (clj->js (model/progress-detail (or (gobj/get el k-last-prog) 0))))))))

;; ── Slot change ─────────────────────────────────────────────────────────────
(defn- on-slotchange [^js el]
  (invalidate-child-cache! el)
  ;; Re-apply transforms immediately if visible
  (when (gobj/get el k-visible)
    (update-transforms! el)))

;; ── Listener management ────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [{:keys [slot]} (ensure-refs! el)
        ^js slot    slot
        scroll-h    (fn [_e] (on-scroll el))
        slot-h      (fn [_e] (on-slotchange el))
        resize-h    (fn [_e] (when (gobj/get el k-visible)
                               (invalidate-child-cache! el)
                               (update-transforms! el)))]
    (.addEventListener slot "slotchange" slot-h)
    (.addEventListener js/window "resize" resize-h)
    (gobj/set el k-handlers
              #js {:scroll         scroll-h
                   :slot           slot-h
                   :resize         resize-h
                   :scrollAttached false}))
  nil)

(defn- remove-listeners! [^js el]
  (let [hs (gobj/get el k-handlers)]
    (when hs
      (detach-scroll-listener! el)
      (let [refs (gobj/get el k-refs)]
        (when refs
          (let [^js slot (:slot refs)]
            (when-let [h (gobj/get hs "slot")]
              (.removeEventListener slot "slotchange" h)))))
      (when-let [h (gobj/get hs "resize")]
        (.removeEventListener js/window "resize" h))))
  ;; Cancel pending rAF
  (when-let [raf (gobj/get el k-raf)]
    (js/cancelAnimationFrame raf)
    (gobj/set el k-raf nil))
  (gobj/set el k-handlers nil)
  nil)

;; ── IntersectionObserver setup/teardown ─────────────────────────────────────
(defn- setup-observer! [^js el]
  (when-let [old (gobj/get el k-io)]
    (.disconnect ^js old))
  (let [obs (js/IntersectionObserver.
             (fn [entries] (on-intersection el entries))
             #js {:threshold #js [0]})]
    (.observe obs el)
    (gobj/set el k-io obs))
  nil)

(defn- teardown-observer! [^js el]
  (when-let [obs (gobj/get el k-io)]
    (.disconnect ^js obs)
    (gobj/set el k-io nil))
  nil)

;; ── DOM patching ────────────────────────────────────────────────────────────
(defn- apply-model! [^js el {:keys [direction disabled? label] :as m}]
  (let [{:keys [viewport]} (ensure-refs! el)
        ^js viewport viewport]
    ;; Data attributes for CSS selectors
    (.setAttribute el "data-direction" direction)

    ;; Aria
    (if (seq label)
      (.setAttribute viewport "aria-label" label)
      (.removeAttribute viewport "aria-label"))

    ;; Cache model
    (gobj/set el k-model m)

    ;; Re-apply transforms if visible and not disabled
    (when (and (gobj/get el k-visible) (not disabled?))
      (update-transforms! el)))
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m)))
  nil)

;; ── Clean up child styles ───────────────────────────────────────────────────
(defn- clean-child-styles! [^js el]
  (let [children (get-children el)]
    (dotimes [i (.-length children)]
      (let [^js child (aget children i)]
        (set! (.. child -style -transform) "")
        (set! (.. child -style -opacity) "")))))

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  ;; direction (string)
  (.defineProperty js/Object proto model/attr-direction
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-direction) "vertical")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-direction (str v))
                                          (.removeAttribute this model/attr-direction))))
                        :enumerable true :configurable true})

  ;; source (string)
  (.defineProperty js/Object proto model/attr-source
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-source) "document")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-source (str v))
                                          (.removeAttribute this model/attr-source))))
                        :enumerable true :configurable true})

  ;; easing (string)
  (.defineProperty js/Object proto model/attr-easing
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-easing) "none")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-easing (str v))
                                          (.removeAttribute this model/attr-easing))))
                        :enumerable true :configurable true})

  ;; disabled (boolean)
  (.defineProperty js/Object proto model/attr-disabled
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this model/attr-disabled)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-disabled "")
                                          (.removeAttribute this model/attr-disabled))))
                        :enumerable true :configurable true})

  ;; label (string)
  (.defineProperty js/Object proto model/attr-label
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-label) "")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (and v (not= v ""))
                                          (.setAttribute this model/attr-label (str v))
                                          (.removeAttribute this model/attr-label))))
                        :enumerable true :configurable true})

  ;; progress (read-only number)
  (.defineProperty js/Object proto "progress"
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (gobj/get this k-last-prog) 0)))
                        :enumerable true :configurable true}))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (ensure-refs! this)
                     (remove-listeners! this)
                     (add-listeners! this)
                     (setup-observer! this)
                     (update-from-attrs! this)
                     nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     ;; Clean child styles before teardown (slot still has assigned elements)
                     (clean-child-styles! this)
                     ;; Dispatch leave event if the element was visible
                     (when (gobj/get this k-visible)
                       (du/dispatch! this model/event-leave
                                  (clj->js (model/progress-detail (or (gobj/get this k-last-prog) 0)))))
                     (remove-listeners! this)
                     (teardown-observer! this)
                     (invalidate-child-cache! this)
                     (gobj/set this k-visible false)
                     (gobj/set this k-last-prog nil)
                     nil)))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [_name old-val new-val]
            (this-as ^js this
                     (when (not= old-val new-val)
                       (update-from-attrs! this))
                     nil)))

    (install-property-accessors! (.-prototype klass))

    klass))

;; ── Public API ──────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
