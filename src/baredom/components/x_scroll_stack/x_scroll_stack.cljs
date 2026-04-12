(ns baredom.components.x-scroll-stack.x-scroll-stack
  (:require
   [goog.object :as gobj]
   [baredom.components.x-scroll-stack.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs            "__xScrollStackRefs")
(def ^:private k-model           "__xScrollStackModel")
(def ^:private k-handlers        "__xScrollStackHandlers")
(def ^:private k-io              "__xScrollStackIO")
(def ^:private k-raf             "__xScrollStackRAF")
(def ^:private k-visible         "__xScrollStackVisible")
(def ^:private k-stacked-count   "__xScrollStackStackedCount")
(def ^:private k-last-prog       "__xScrollStackLastProg")
(def ^:private k-natural-offsets "__xScrollStackNaturalOffsets")
(def ^:private k-child-heights   "__xScrollStackChildHeights")
(def ^:private k-cache-raf       "__xScrollStackCacheRAF")

;; ── Styles ──────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;position:relative;"
   "color-scheme:light dark;"
   "--x-scroll-stack-peek:6px;"
   "--x-scroll-stack-rotation:3deg;"
   "--x-scroll-stack-gap:1rem;}"

   "[part=container]{"
   "position:sticky;top:0;height:100dvh;"
   "overflow:hidden;"
   "display:flex;flex-direction:column;"
   "align-items:center;justify-content:flex-start;"
   "padding-top:var(--x-scroll-stack-padding-top,2rem);"
   "gap:var(--x-scroll-stack-gap);}"

   "::slotted(*){"
   "flex-shrink:0;"
   "will-change:transform;"
   "transition:transform 60ms linear;}"

   ":host([disabled]){"
   "opacity:0.55;}"

   "@media (prefers-reduced-motion:reduce){"
   "::slotted(*){transition:none !important;}}"))

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root      (.attachShadow el #js {:mode "open"})
        style     (.createElement js/document "style")
        container (.createElement js/document "div")
        slot-el   (.createElement js/document "slot")]

    (set! (.-textContent style) style-text)

    (.setAttribute container "part" "container")
    (.setAttribute container "role" "region")

    (.appendChild container slot-el)
    (.appendChild root style)
    (.appendChild root container)

    (gobj/set el k-refs {:root root :container container :slot slot-el}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:peek-raw             (.getAttribute el model/attr-peek)
    :rotation-raw         (.getAttribute el model/attr-rotation)
    :scroll-distance-raw  (.getAttribute el model/attr-scroll-distance)
    :align-raw            (.getAttribute el model/attr-align)
    :disabled-attr        (when (.hasAttribute el model/attr-disabled) "")}))

;; ── Helpers ─────────────────────────────────────────────────────────────────
(defn- get-slotted-children [^js el]
  (let [{:keys [slot]} (ensure-refs! el)]
    (.assignedElements ^js slot)))

(defn- prefers-reduced-motion? []
  (boolean (.-matches (.matchMedia js/window "(prefers-reduced-motion:reduce)"))))

;; ── Event dispatch ──────────────────────────────────────────────────────────
(defn- dispatch! [^js el event-name detail-map]
  (let [^js ev (js/CustomEvent.
                event-name
                #js {:detail     (clj->js detail-map)
                     :bubbles    true
                     :composed   true
                     :cancelable false})]
    (.dispatchEvent el ev)))

;; ── Natural offset caching ──────────────────────────────────────────────────
(defn- cache-natural-offsets!
  "Strip transforms, force reflow, read getBoundingClientRect for each child
  relative to the container, store in JS arrays."
  [^js el]
  (let [children (get-slotted-children el)
        n        (.-length children)]
    (when (pos? n)
      (let [{:keys [container]} (gobj/get el k-refs)
            ^js container container
            offsets  (make-array n)
            heights  (make-array n)]
        ;; Clear transforms temporarily
        (dotimes [i n]
          (let [^js child (aget children i)]
            (set! (.. child -style -transform) "")
            (set! (.. child -style -zIndex) "")))
        ;; Force reflow to get clean positions
        (.-offsetHeight container)
        ;; Read natural positions using getBoundingClientRect (reliable for slotted elements)
        (let [c-rect (.getBoundingClientRect container)]
          (dotimes [i n]
            (let [^js child (aget children i)
                  r (.getBoundingClientRect child)]
              (aset offsets i (- (.-top r) (.-top c-rect)))
              (aset heights i (.-height r)))))
        (gobj/set el k-natural-offsets offsets)
        (gobj/set el k-child-heights heights))))
  nil)

;; ── Height management ───────────────────────────────────────────────────────
(defn- update-height!
  "Set the host element height to create enough scroll room for stacking."
  [^js el]
  (let [m        (or (gobj/get el k-model) (read-model el))
        children (get-slotted-children el)
        n        (.-length children)
        vh       (.-innerHeight js/window)
        total-h  (+ vh (* n (:scroll-distance m)))]
    (set! (.. el -style -height) (str total-h "px")))
  nil)

;; ── Core scroll update ──────────────────────────────────────────────────────

(defn- update-scroll!
  "Compute card transforms based on current scroll position. Called from rAF."
  [^js el]
  (let [m          (or (gobj/get el k-model) (read-model el))
        rect       (.getBoundingClientRect el)
        ;; How far the element top has scrolled above the viewport top
        scroll-off (js/Math.max 0 (- (.-top rect)))
        children   (get-slotted-children el)
        n          (.-length children)
        offsets    (gobj/get el k-natural-offsets)
        heights    (gobj/get el k-child-heights)]

    (when (and (pos? n) offsets heights
               (= (.-length offsets) n)
               (= (.-length heights) n))
      (let [sd         (:scroll-distance m)
            peek-val   (:peek m)
            rotation   (:rotation m)
            align-val  (:align m)
            ;; Stack anchors at first card's natural position
            stack-top  (aget offsets 0)
            ;; Compute align shift only when align attribute is explicitly set
            align-shift (if (.hasAttribute el model/attr-align)
                          (let [last-h     (aget heights (dec n))
                                total-stack-h (+ (* (dec n) peek-val) last-h)
                                {:keys [container]} (gobj/get el k-refs)
                                container-h (.-clientHeight ^js container)]
                            (- (model/compute-stack-area-y container-h total-stack-h align-val)
                               stack-top))
                          0)
            ;; Progress and stacked count
            progress   (model/compute-overall-progress scroll-off n sd)
            stacked    (model/compute-stacked-count scroll-off n sd)
            old-stacked (or (gobj/get el k-stacked-count) 0)]

        ;; Apply transforms to each child
        (dotimes [i n]
          (let [^js child (aget children i)
                cp (model/compute-card-progress scroll-off i sd)
                sp (model/stack-params i peek-val rotation)
                natural-y (aget offsets i)
                ;; Target Y: first card stays put, others stack below with peek offset
                target-y (+ stack-top (:stack-y sp))
                ;; Delta from natural position (card 0: delta=0 before align shift)
                delta-y (- target-y natural-y)
                ;; Interpolated values with align shift applied
                y   (+ (* cp delta-y) (* cp align-shift))
                rot (* cp (:stack-rot sp))
                x   (* cp (:stack-x sp))
                ;; Z-index: last card on top when stacked
                z   (if (pos? cp) (str i) "")]
            (set! (.. child -style -transform)
                  (if (zero? cp)
                    ""
                    (str "translate(" (.toFixed x 2) "px,"
                         (.toFixed y 2) "px) rotate("
                         (.toFixed rot 3) "deg)")))
            (set! (.. child -style -zIndex) z)))

        ;; Dispatch change event when stacked count changes
        (when (not= stacked old-stacked)
          (gobj/set el k-stacked-count stacked)
          (dispatch! el model/event-change
                     (model/change-detail stacked n progress)))

        ;; Dispatch progress event
        (let [last-prog (gobj/get el k-last-prog)]
          (when (or (nil? last-prog)
                    (> (js/Math.abs (- progress last-prog)) 0.001))
            (gobj/set el k-last-prog progress)
            (dispatch! el model/event-progress
                       (model/progress-detail progress stacked n)))))))
  ;; Clear rAF handle
  (gobj/set el k-raf nil))

;; ── Scroll handler ──────────────────────────────────────────────────────────
(defn- disabled? [^js el]
  (let [m (gobj/get el k-model)]
    (and m (:disabled? m))))

(defn- on-scroll [^js el]
  (when (and (.-isConnected el)
             (gobj/get el k-visible)
             (not (disabled? el)))
    (when-not (gobj/get el k-raf)
      (gobj/set el k-raf
                (js/requestAnimationFrame
                 (fn [_] (update-scroll! el)))))))

;; ── IntersectionObserver ────────────────────────────────────────────────────
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

(defn- schedule-cache-and-update!
  "Schedule offset caching and first scroll update in a rAF to ensure
  the browser has completed layout before we measure."
  [^js el]
  ;; Cancel any pending cache rAF
  (when-let [raf (gobj/get el k-cache-raf)]
    (js/cancelAnimationFrame raf)
    (gobj/set el k-cache-raf nil))
  ;; Schedule in next frame — layout is guaranteed to be done
  (gobj/set el k-cache-raf
            (js/requestAnimationFrame
             (fn [_]
               (gobj/set el k-cache-raf nil)
               (when (.-isConnected el)
                 (cache-natural-offsets! el)
                 (update-scroll! el))))))

(defn- on-intersection [^js el ^js entries]
  (let [^js entry (aget entries 0)
        is-intersecting (.-isIntersecting entry)]
    (gobj/set el k-visible is-intersecting)
    (if is-intersecting
      (when-not (disabled? el)
        (attach-scroll-listener! el)
        ;; Defer measurement to next frame so layout is settled
        (schedule-cache-and-update! el))
      (detach-scroll-listener! el))))

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

;; ── Slot change ─────────────────────────────────────────────────────────────
(defn- on-slotchange [^js el]
  (update-height! el)
  (when (gobj/get el k-visible)
    (schedule-cache-and-update! el)))

;; ── Clean up child styles ───────────────────────────────────────────────────
(defn- clean-child-styles!
  "Reset transforms on all slotted children to avoid leaking styles."
  [^js el]
  (let [children (get-slotted-children el)]
    (dotimes [i (.-length children)]
      (let [^js child (aget children i)]
        (set! (.. child -style -transform) "")
        (set! (.. child -style -zIndex) "")))))

;; ── Listener management ─────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [{:keys [slot]} (ensure-refs! el)
        ^js slot    slot
        scroll-h    (fn [_e] (on-scroll el))
        slot-h      (fn [_e] (on-slotchange el))
        resize-h    (fn [_e]
                      (update-height! el)
                      (when (gobj/get el k-visible)
                        (schedule-cache-and-update! el)))]
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
  ;; Cancel pending rAFs
  (when-let [raf (gobj/get el k-raf)]
    (js/cancelAnimationFrame raf)
    (gobj/set el k-raf nil))
  (when-let [raf (gobj/get el k-cache-raf)]
    (js/cancelAnimationFrame raf)
    (gobj/set el k-cache-raf nil))
  (gobj/set el k-handlers nil)
  nil)

;; ── DOM patching ────────────────────────────────────────────────────────────
(defn- apply-model! [^js el {:keys [disabled?] :as m}]
  (gobj/set el k-model m)
  (if disabled?
    (do
      (clean-child-styles! el)
      (gobj/set el k-stacked-count 0)
      (gobj/set el k-last-prog nil)
      (detach-scroll-listener! el))
    (when (gobj/get el k-visible)
      (attach-scroll-listener! el)
      (schedule-cache-and-update! el)))
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m)))
  nil)

;; ── Public method: refresh ──────────────────────────────────────────────────
(defn- refresh! [^js el]
  (update-height! el)
  (when (gobj/get el k-visible)
    (schedule-cache-and-update! el)))

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  ;; peek (number)
  (.defineProperty js/Object proto model/attr-peek
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-positive-number
                                         (.getAttribute this model/attr-peek) 6)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (some? v)
                                          (.setAttribute this model/attr-peek (str v))
                                          (.removeAttribute this model/attr-peek))))
                        :enumerable true :configurable true})

  ;; rotation (number)
  (.defineProperty js/Object proto model/attr-rotation
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-positive-number
                                         (.getAttribute this model/attr-rotation) 3)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (some? v)
                                          (.setAttribute this model/attr-rotation (str v))
                                          (.removeAttribute this model/attr-rotation))))
                        :enumerable true :configurable true})

  ;; scrollDistance (number, camelCase)
  (.defineProperty js/Object proto "scrollDistance"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-positive-number
                                         (.getAttribute this model/attr-scroll-distance) 150)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (some? v)
                                          (.setAttribute this model/attr-scroll-distance (str v))
                                          (.removeAttribute this model/attr-scroll-distance))))
                        :enumerable true :configurable true})

  ;; align (string)
  (.defineProperty js/Object proto model/attr-align
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-align
                                         (.getAttribute this model/attr-align))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (some? v)
                                          (.setAttribute this model/attr-align (str v))
                                          (.removeAttribute this model/attr-align))))
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

  ;; stackedCount (read-only)
  (.defineProperty js/Object proto "stackedCount"
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (gobj/get this k-stacked-count) 0)))
                        :enumerable true :configurable true})

  ;; progress (read-only)
  (.defineProperty js/Object proto "progress"
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (gobj/get this k-last-prog) 0)))
                        :enumerable true :configurable true})

  ;; refresh() method
  (.defineProperty js/Object proto "refresh"
                   #js {:value (fn []
                                 (this-as ^js this
                                          (refresh! this)))
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
                     (update-height! this)
                     nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (clean-child-styles! this)
                     (remove-listeners! this)
                     (teardown-observer! this)
                     (gobj/set this k-visible false)
                     (gobj/set this k-stacked-count 0)
                     (gobj/set this k-last-prog nil)
                     (gobj/set this k-natural-offsets nil)
                     (gobj/set this k-child-heights nil)
                     nil)))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [_name old-val new-val]
            (this-as ^js this
                     (when (not= old-val new-val)
                       (update-from-attrs! this)
                       (update-height! this)
                       (when (gobj/get this k-visible)
                         (schedule-cache-and-update! this)))
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
