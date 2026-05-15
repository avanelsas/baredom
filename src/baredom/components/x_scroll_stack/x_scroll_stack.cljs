(ns baredom.components.x-scroll-stack.x-scroll-stack
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
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

    (du/set-attr! container "part" "container")
    (du/set-attr! container "role" "region")

    (.appendChild container slot-el)
    (.appendChild root style)
    (.appendChild root container)

    (du/setv! el k-refs {:root root :container container :slot slot-el})))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:peek-raw             (du/get-attr el model/attr-peek)
    :rotation-raw         (du/get-attr el model/attr-rotation)
    :scroll-distance-raw  (du/get-attr el model/attr-scroll-distance)
    :align-raw            (du/get-attr el model/attr-align)
    :disabled-attr        (when (du/has-attr? el model/attr-disabled) "")}))

;; ── Helpers ─────────────────────────────────────────────────────────────────
(defn- get-slotted-children [^js el]
  (let [{:keys [slot]} (ensure-refs! el)]
    (.assignedElements ^js slot)))

;; ── Event dispatch ──────────────────────────────────────────────────────────
;; ── Natural offset caching ──────────────────────────────────────────────────
(defn- cache-natural-offsets!
  "Strip transforms, force reflow, read getBoundingClientRect for each child
  relative to the container, store in JS arrays."
  [^js el]
  (let [children (get-slotted-children el)
        n        (.-length children)]
    (when (pos? n)
      (let [{:keys [container]} (du/getv el k-refs)
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
        (du/setv! el k-natural-offsets offsets)
        (du/setv! el k-child-heights heights)))))

;; ── Height management ───────────────────────────────────────────────────────
(defn- update-height!
  "Set the host element height to create enough scroll room for stacking."
  [^js el]
  (let [m        (or (du/getv el k-model) (read-model el))
        children (get-slotted-children el)
        n        (.-length children)
        vh       (.-innerHeight js/window)
        total-h  (+ vh (* n (:scroll-distance m)))]
    (set! (.. el -style -height) (str total-h "px"))))

;; ── Core scroll update ──────────────────────────────────────────────────────

(defn- update-scroll!
  "Compute card transforms based on current scroll position. Called from rAF."
  [^js el]
  (let [m          (or (du/getv el k-model) (read-model el))
        rect       (.getBoundingClientRect el)
        ;; How far the element top has scrolled above the viewport top
        scroll-off (js/Math.max 0 (- (.-top rect)))
        children   (get-slotted-children el)
        n          (.-length children)
        offsets    (du/getv el k-natural-offsets)
        heights    (du/getv el k-child-heights)]

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
            align-shift (if (du/has-attr? el model/attr-align)
                          (let [last-h     (aget heights (dec n))
                                total-stack-h (+ (* (dec n) peek-val) last-h)
                                {:keys [container]} (du/getv el k-refs)
                                container-h (.-clientHeight ^js container)]
                            (- (model/compute-stack-area-y container-h total-stack-h align-val)
                               stack-top))
                          0)
            ;; Progress and stacked count
            progress   (model/compute-overall-progress scroll-off n sd)
            stacked    (model/compute-stacked-count scroll-off n sd)
            old-stacked (or (du/getv el k-stacked-count) 0)]

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
          (du/setv! el k-stacked-count stacked)
          (du/dispatch! el model/event-change
                     (clj->js (model/change-detail stacked n progress))))

        ;; Dispatch progress event
        (let [last-prog (du/getv el k-last-prog)]
          (when (or (nil? last-prog)
                    (> (js/Math.abs (- progress last-prog)) 0.001))
            (du/setv! el k-last-prog progress)
            (du/dispatch! el model/event-progress
                       (clj->js (model/progress-detail progress stacked n))))))))
  ;; Clear rAF handle
  (du/setv-untraced! el k-raf nil))

;; ── Scroll handler ──────────────────────────────────────────────────────────
(defn- disabled? [^js el]
  (let [m (du/getv el k-model)]
    (and m (:disabled? m))))

(defn- on-scroll [^js el]
  (when (and (.-isConnected el)
             (du/getv el k-visible)
             (not (disabled? el)))
    (when-not (du/getv el k-raf)
      (du/setv-untraced! el k-raf
                (js/requestAnimationFrame
                 (fn [_] (update-scroll! el)))))))

;; ── IntersectionObserver ────────────────────────────────────────────────────
(defn- attach-scroll-listener! [^js el]
  (let [hs (du/getv el k-handlers)]
    (when (and hs (not (gobj/get hs "scrollAttached")))
      (let [scroll-h (gobj/get hs "scroll")]
        (.addEventListener js/window "scroll" scroll-h #js {:passive true})
        (gobj/set hs "scrollAttached" true)))))

(defn- detach-scroll-listener! [^js el]
  (let [hs (du/getv el k-handlers)]
    (when (and hs (gobj/get hs "scrollAttached"))
      (let [scroll-h (gobj/get hs "scroll")]
        (.removeEventListener js/window "scroll" scroll-h)
        (gobj/set hs "scrollAttached" false)))))

(defn- schedule-cache-and-update!
  "Schedule offset caching and first scroll update in a rAF to ensure
  the browser has completed layout before we measure."
  [^js el]
  ;; Cancel any pending cache rAF
  (when-let [raf (du/getv el k-cache-raf)]
    (js/cancelAnimationFrame raf)
    (du/setv! el k-cache-raf nil))
  ;; Schedule in next frame — layout is guaranteed to be done
  (du/setv! el k-cache-raf
            (js/requestAnimationFrame
             (fn [_]
               (du/setv! el k-cache-raf nil)
               (when (.-isConnected el)
                 (cache-natural-offsets! el)
                 (update-scroll! el))))))

(defn- on-intersection [^js el ^js entries]
  (let [^js entry (aget entries 0)
        is-intersecting (.-isIntersecting entry)]
    (du/setv! el k-visible is-intersecting)
    (if is-intersecting
      (when-not (disabled? el)
        (attach-scroll-listener! el)
        ;; Defer measurement to next frame so layout is settled
        (schedule-cache-and-update! el))
      (detach-scroll-listener! el))))

(defn- setup-observer! [^js el]
  (when-let [old (du/getv el k-io)]
    (.disconnect ^js old))
  (let [obs (js/IntersectionObserver.
             (fn [entries] (on-intersection el entries))
             #js {:threshold #js [0]})]
    (.observe obs el)
    (du/setv! el k-io obs)))

(defn- teardown-observer! [^js el]
  (when-let [obs (du/getv el k-io)]
    (.disconnect ^js obs)
    (du/setv! el k-io nil)))

;; ── Slot change ─────────────────────────────────────────────────────────────
(defn- on-slotchange [^js el]
  (update-height! el)
  (when (du/getv el k-visible)
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
                      (when (du/getv el k-visible)
                        (schedule-cache-and-update! el)))]
    (.addEventListener slot "slotchange" slot-h)
    (.addEventListener js/window "resize" resize-h)
    (du/setv! el k-handlers
              #js {:scroll         scroll-h
                   :slot           slot-h
                   :resize         resize-h
                   :scrollAttached false})))

(defn- remove-listeners! [^js el]
  (let [hs (du/getv el k-handlers)]
    (when hs
      (detach-scroll-listener! el)
      (let [refs (du/getv el k-refs)]
        (when refs
          (let [^js slot (:slot refs)]
            (when-let [h (gobj/get hs "slot")]
              (.removeEventListener slot "slotchange" h)))))
      (when-let [h (gobj/get hs "resize")]
        (.removeEventListener js/window "resize" h))))
  ;; Cancel pending rAFs
  (when-let [raf (du/getv el k-raf)]
    (js/cancelAnimationFrame raf)
    (du/setv-untraced! el k-raf nil))
  (when-let [raf (du/getv el k-cache-raf)]
    (js/cancelAnimationFrame raf)
    (du/setv! el k-cache-raf nil))
  (du/setv! el k-handlers nil))

;; ── DOM patching ────────────────────────────────────────────────────────────
(defn- apply-model! [^js el {:keys [disabled?] :as m}]
  (du/setv! el k-model m)
  (if disabled?
    (do
      (clean-child-styles! el)
      (du/setv! el k-stacked-count 0)
      (du/setv! el k-last-prog nil)
      (detach-scroll-listener! el))
    (when (du/getv el k-visible)
      (attach-scroll-listener! el)
      (schedule-cache-and-update! el))))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ── Public method: refresh ──────────────────────────────────────────────────
(defn- refresh! [^js el]
  (update-height! el)
  (when (du/getv el k-visible)
    (schedule-cache-and-update! el)))

;; ── Property accessors ──────────────────────────────────────────────────────
;; parse-positive-number takes a 2-arg `(raw default)` signature; wrap each
;; numeric prop in a single-arg adapter so it fits define-parsed-prop!.
(defn- parse-peek-attr            [s] (model/parse-positive-number s 6))
(defn- parse-rotation-attr        [s] (model/parse-positive-number s 3))
(defn- parse-scroll-distance-attr [s] (model/parse-positive-number s 150))

(defn- install-property-accessors! [^js proto]
  (du/define-parsed-prop! proto model/attr-peek      model/attr-peek            parse-peek-attr)
  (du/define-parsed-prop! proto model/attr-rotation  model/attr-rotation        parse-rotation-attr)
  (du/define-parsed-prop! proto "scrollDistance"     model/attr-scroll-distance parse-scroll-distance-attr)
  (du/define-parsed-prop! proto model/attr-align     model/attr-align           model/parse-align)
  (du/define-bool-prop!   proto model/attr-disabled  model/attr-disabled)

  ;; stackedCount / progress — read-only, no shared helper applies.
  (.defineProperty js/Object proto "stackedCount"
    #js {:get (fn xss-get-stacked-count []
                (this-as ^js this (or (du/getv this k-stacked-count) 0)))
         :enumerable true :configurable true})
  (.defineProperty js/Object proto "progress"
    #js {:get (fn xss-get-progress []
                (this-as ^js this (or (du/getv this k-last-prog) 0)))
         :enumerable true :configurable true})

  ;; refresh() method
  (.defineProperty js/Object proto "refresh"
    #js {:value (fn xss-refresh []
                  (this-as ^js this (refresh! this)))
         :writable true :configurable true}))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el)
  (setup-observer! el)
  (update-from-attrs! el)
  (update-height! el))

(defn- disconnected! [^js el]
  (clean-child-styles! el)
  (remove-listeners! el)
  (teardown-observer! el)
  (du/setv! el k-visible false)
  (du/setv! el k-stacked-count 0)
  (du/setv! el k-last-prog nil)
  (du/setv! el k-natural-offsets nil)
  (du/setv! el k-child-heights nil))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)
    (update-height! el)
    (when (du/getv el k-visible)
      (schedule-cache-and-update! el))))

;; ── Public API ──────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
