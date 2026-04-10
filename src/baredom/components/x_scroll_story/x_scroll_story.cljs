(ns baredom.components.x-scroll-story.x-scroll-story
  (:require
   [goog.object :as gobj]
   [baredom.components.x-scroll-story.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs         "__xScrollStoryRefs")
(def ^:private k-model        "__xScrollStoryModel")
(def ^:private k-handlers     "__xScrollStoryHandlers")
(def ^:private k-io           "__xScrollStoryIO")
(def ^:private k-raf          "__xScrollStoryRAF")
(def ^:private k-visible      "__xScrollStoryVisible")
(def ^:private k-active-index "__xScrollStoryActiveIndex")
(def ^:private k-last-prog    "__xScrollStoryLastProg")
(def ^:private k-step-states       "__xScrollStoryStepStates")
(def ^:private k-autoplay-raf      "__xScrollStoryAutoplayRAF")
(def ^:private k-autoplay-last     "__xScrollStoryAutoplayLast")
(def ^:private k-autoplay-paused   "__xScrollStoryAutoplayPaused")
(def ^:private k-autoplay-handlers "__xScrollStoryAutoplayHandlers")

;; ── Styles ──────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;position:relative;"
   "color-scheme:light dark;"
   "--x-scroll-story-gap:0;"
   "--x-scroll-story-step-min-height:80vh;"
   "--x-scroll-story-step-padding:2rem;"
   "--x-scroll-story-active-opacity:1;"
   "--x-scroll-story-inactive-opacity:0.3;"
   "--x-scroll-story-transition-duration:300ms;"
   "--x-scroll-story-disabled-opacity:0.55;"
   "--x-scroll-story-media-top:0;}"

   "[part=container]{"
   "display:flex;gap:var(--x-scroll-story-gap);}"

   ":host([data-layout=left]) [part=container]{"
   "flex-direction:row;}"

   ":host([data-layout=right]) [part=container]{"
   "flex-direction:row-reverse;}"

   ":host([data-layout=top]) [part=container]{"
   "flex-direction:column;}"

   "[part=media]{"
   "position:sticky;top:var(--x-scroll-story-media-top);"
   "align-self:flex-start;flex-shrink:0;}"

   ":host([data-layout=top]) [part=media]{"
   "position:sticky;width:100%;}"

   "[part=steps]{"
   "flex:1;min-width:0;}"

   "[part=media]{"
   "height:100vh;}"

   "::slotted(:not([slot])){"
   "min-height:var(--x-scroll-story-step-min-height);"
   "padding:var(--x-scroll-story-step-padding);"
   "opacity:var(--x-scroll-story-inactive-opacity);"
   "transition:opacity var(--x-scroll-story-transition-duration) ease;}"

   "::slotted([data-active]){"
   "opacity:var(--x-scroll-story-active-opacity);}"

   ":host([disabled]){"
   "opacity:var(--x-scroll-story-disabled-opacity);}"

   "[part=live]{"
   "position:absolute;width:1px;height:1px;margin:-1px;"
   "padding:0;overflow:hidden;clip:rect(0,0,0,0);border:0;}"

   "[part=indicator]{"
   "display:none;position:absolute;top:50%;left:50%;"
   "transform:translate(-50%,-50%);pointer-events:none;"
   "width:48px;height:48px;opacity:0.7;}"

   "[part=indicator]::before,[part=indicator]::after{"
   "content:'';position:absolute;top:8px;width:12px;height:32px;"
   "background:rgba(255,255,255,0.9);border-radius:3px;}"

   "[part=indicator]::before{left:8px;}"
   "[part=indicator]::after{right:8px;}"

   ":host([data-autoplay-paused][autoplay-indicator]) [part=indicator]{"
   "display:block;}"

   "@media (prefers-reduced-motion:reduce){"
   "::slotted(*){transition:none !important;}}"))

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root      (.attachShadow el #js {:mode "open"})
        style     (.createElement js/document "style")
        container (.createElement js/document "div")
        media-el  (.createElement js/document "div")
        steps-el  (.createElement js/document "div")
        media-slot (.createElement js/document "slot")
        steps-slot (.createElement js/document "slot")
        live      (.createElement js/document "div")]

    (set! (.-textContent style) style-text)

    (.setAttribute container "part" "container")
    (.setAttribute container "role" "region")

    (.setAttribute media-el "part" "media")
    (.setAttribute media-slot "name" "media")
    (let [indicator (.createElement js/document "div")]
      (.setAttribute indicator "part" "indicator")
      (.appendChild media-el indicator))
    (.appendChild media-el media-slot)

    (.setAttribute steps-el "part" "steps")
    (.appendChild steps-el steps-slot)

    (.appendChild container media-el)
    (.appendChild container steps-el)

    (.setAttribute live "part" "live")
    (.setAttribute live "aria-live" "polite")
    (.setAttribute live "aria-atomic" "true")

    (.appendChild root style)
    (.appendChild root container)
    (.appendChild root live)

    (gobj/set el k-refs
              {:root      root
               :container container
               :media     media-el
               :steps     steps-el
               :slot      steps-slot
               :live      live}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:layout-raw             (.getAttribute el model/attr-layout)
    :threshold-raw          (.getAttribute el model/attr-threshold)
    :split-raw              (.getAttribute el model/attr-split)
    :disabled-attr          (when (.hasAttribute el model/attr-disabled) "")
    :label-raw              (.getAttribute el model/attr-label)
    :autoplay-attr          (when (.hasAttribute el model/attr-autoplay) "")
    :autoplay-speed-raw     (.getAttribute el model/attr-autoplay-speed)
    :autoplay-loop-attr     (when (.hasAttribute el model/attr-autoplay-loop) "")
    :autoplay-indicator-attr (when (.hasAttribute el model/attr-autoplay-indicator) "")}))

;; ── Helpers ─────────────────────────────────────────────────────────────────
(defn- get-step-children [^js el]
  (let [{:keys [slot]} (ensure-refs! el)]
    (.assignedElements ^js slot)))

(defn- prefers-reduced-motion? []
  (boolean (.-matches (.matchMedia js/window "(prefers-reduced-motion:reduce)"))))

(defn- step-id
  "Get the id attribute of a step element, or nil."
  [^js child]
  (let [id (.getAttribute child "id")]
    (when (and (string? id) (not= id ""))
      id)))

;; ── Event dispatch ──────────────────────────────────────────────────────────
(defn- dispatch! [^js el event-name detail-map]
  (let [^js ev (js/CustomEvent.
                event-name
                #js {:detail     (clj->js detail-map)
                     :bubbles    true
                     :composed   true
                     :cancelable false})]
    (.dispatchEvent el ev)))

;; ── Announce to live region ─────────────────────────────────────────────────
(defn- announce! [^js el ^js live msg]
  (set! (.-textContent live) msg)
  (js/setTimeout (fn [] (set! (.-textContent live) "")) 3000))

;; ── Step activation ─────────────────────────────────────────────────────────
(defn- update-active-step!
  "Mark a step child as active (set data-active), remove from previous."
  [^js el children new-index]
  (let [old-index (or (gobj/get el k-active-index) -1)]
    (when (not= old-index new-index)
      ;; Remove data-active from old step
      (when (and (>= old-index 0) (< old-index (.-length children)))
        (let [^js old-child (aget children old-index)]
          (.removeAttribute old-child model/data-active)))
      ;; Set data-active on new step
      (when (and (>= new-index 0) (< new-index (.-length children)))
        (let [^js new-child (aget children new-index)]
          (.setAttribute new-child model/data-active "")))
      ;; Update host data attribute
      (if (>= new-index 0)
        (.setAttribute el "data-active-index" (str new-index))
        (.removeAttribute el "data-active-index"))
      ;; Cache active index
      (gobj/set el k-active-index new-index)
      ;; Dispatch step-change event
      (let [old-id (when (and (>= old-index 0) (< old-index (.-length children)))
                     (step-id (aget children old-index)))
            new-id (when (and (>= new-index 0) (< new-index (.-length children)))
                     (step-id (aget children new-index)))]
        (dispatch! el model/event-step-change
                   (model/step-change-detail new-index new-id old-index old-id))
        ;; Announce to live region
        (let [{:keys [live]} (gobj/get el k-refs)]
          (announce! el live (str "Step " (inc new-index) " active")))))))

;; ── Step enter/leave tracking ───────────────────────────────────────────────
(defn- ensure-step-states!
  "Ensure the step states array matches the current child count."
  [^js el n]
  (let [states (gobj/get el k-step-states)]
    (if (and states (= (.-length states) n))
      states
      (let [new-states (make-array n)]
        (dotimes [i n]
          (aset new-states i
                (if (and states (< i (.-length states)))
                  (aget states i)
                  false)))
        (gobj/set el k-step-states new-states)
        new-states))))

(defn- update-step-states!
  "Track which steps are in the trigger zone and fire enter/leave events."
  [^js el children step-rects trigger-y]
  (let [n      (.-length children)
        states (ensure-step-states! el n)]
    (dotimes [i n]
      (when (< i (count step-rects))
        (let [{:keys [top bottom]} (nth step-rects i)
              was-in? (aget states i)
              is-in?  (and (<= top trigger-y) (> bottom trigger-y))
              ^js child (aget children i)
              id (step-id child)
              progress (model/compute-step-progress top (- bottom top) trigger-y)]
          (cond
            (and (not was-in?) is-in?)
            (do (aset states i true)
                (dispatch! el model/event-step-enter
                           (model/step-enter-detail i id progress)))
            (and was-in? (not is-in?))
            (do (aset states i false)
                (dispatch! el model/event-step-leave
                           (model/step-leave-detail i id progress)))))))))

;; ── Transform application ───────────────────────────────────────────────────
(defn- update-scroll!
  "Compute step activation and dispatch events. Called from rAF callback."
  [^js el]
  (let [m (or (gobj/get el k-model) (read-model el))]
    (when-not (:disabled? m)
      (let [children   (get-step-children el)
            n          (.-length children)
            vp-height  (.-innerHeight js/window)
            trigger-y  (* vp-height (:threshold m))
            ;; Build step rects
            step-rects (loop [i 0 acc []]
                         (if (>= i n)
                           acc
                           (let [^js child (aget children i)
                                 rect (.getBoundingClientRect child)]
                             (recur (inc i)
                                    (conj acc {:top    (.-top rect)
                                               :bottom (+ (.-top rect) (.-height rect))})))))
            ;; Find active step
            active-idx (model/find-active-step step-rects trigger-y)
            ;; Compute overall progress
            {:keys [steps]} (gobj/get el k-refs)
            ^js steps     steps
            steps-rect    (.getBoundingClientRect steps)
            progress      (model/compute-overall-progress
                           (.-top steps-rect) (.-height steps-rect) vp-height)
            last-prog     (gobj/get el k-last-prog)]

        ;; Update active step (handles data-active attribute + events)
        (update-active-step! el children active-idx)

        ;; Track step enter/leave
        (update-step-states! el children step-rects trigger-y)

        ;; Dispatch progress event if changed
        (when (or (nil? last-prog)
                  (> (js/Math.abs (- progress last-prog)) 0.001))
          (gobj/set el k-last-prog progress)
          (let [active-id (when (and (>= active-idx 0) (< active-idx n))
                            (step-id (aget children active-idx)))]
            (dispatch! el model/event-progress
                       (model/progress-detail progress active-idx active-id)))))))
  ;; Clear rAF handle
  (gobj/set el k-raf nil))

;; ── Autoplay ────────────────────────────────────────────────────────────────

(defn- autoplay-running? [^js el]
  (some? (gobj/get el k-autoplay-raf)))

(defn- autoplay-tick [^js el]
  (fn tick [^js ts]
    (when (and (.-isConnected el)
               (gobj/get el k-visible)
               (not (gobj/get el k-autoplay-paused)))
      (let [m (gobj/get el k-model)]
        (when (and m (:autoplay? m) (not (:disabled? m)))
          (let [last-ts (gobj/get el k-autoplay-last)
                dt      (if last-ts (/ (- ts last-ts) 1000.0) 0)]
            (gobj/set el k-autoplay-last ts)
            (when (> dt 0)
              (let [scroll-before (.-scrollY js/window)]
                (.scrollBy js/window 0 (* (:autoplay-speed m) dt))
                ;; Loop: detect that scrollBy had no effect (page bottom).
                ;; The IntersectionObserver cannot handle this because the
                ;; element is too tall to fully leave the viewport.
                (when (and (:autoplay-loop? m)
                           (< (- (.-scrollY js/window) scroll-before) 0.5))
                  (let [rect    (.getBoundingClientRect el)
                        abs-top (+ (.-top rect) (.-scrollY js/window))]
                    (.scrollTo js/window 0 abs-top)
                    (gobj/set el k-autoplay-last nil)))))
            (gobj/set el k-autoplay-raf
                      (js/requestAnimationFrame tick))))))))

(defn- pause-autoplay! [^js el]
  (when-not (gobj/get el k-autoplay-paused)
    (gobj/set el k-autoplay-paused true)
    ;; Cancel pending autoplay rAF
    (when-let [raf (gobj/get el k-autoplay-raf)]
      (js/cancelAnimationFrame raf)
      (gobj/set el k-autoplay-raf nil))
    ;; Set data attribute for CSS indicator
    (.setAttribute el "data-autoplay-paused" "")
    ;; Dispatch pause event
    (let [prog     (or (gobj/get el k-last-prog) 0)
          idx      (or (gobj/get el k-active-index) -1)
          children (.-children el)
          aid      (when (and (>= idx 0) (< idx (.-length children)))
                     (step-id (aget children idx)))]
      (dispatch! el model/event-autoplay-pause
                 (model/autoplay-pause-detail prog idx aid)))))

(defn- resume-autoplay! [^js el]
  (when (gobj/get el k-autoplay-paused)
    (gobj/set el k-autoplay-paused false)
    (.removeAttribute el "data-autoplay-paused")
    ;; Reset timestamp to avoid delta spike
    (gobj/set el k-autoplay-last nil)
    ;; Restart rAF loop
    (let [tick (autoplay-tick el)]
      (gobj/set el k-autoplay-raf
                (js/requestAnimationFrame tick)))
    ;; Dispatch resume event
    (let [prog     (or (gobj/get el k-last-prog) 0)
          idx      (or (gobj/get el k-active-index) -1)
          children (.-children el)
          aid      (when (and (>= idx 0) (< idx (.-length children)))
                     (step-id (aget children idx)))]
      (dispatch! el model/event-autoplay-resume
                 (model/autoplay-resume-detail prog idx aid)))))

(defn- stop-autoplay! [^js el]
  ;; Cancel rAF
  (when-let [raf (gobj/get el k-autoplay-raf)]
    (js/cancelAnimationFrame raf)
    (gobj/set el k-autoplay-raf nil))
  ;; Remove pause/resume listeners
  (when-let [hs (gobj/get el k-autoplay-handlers)]
    (.removeEventListener el "pointerdown"  (gobj/get hs "pointerdown"))
    (.removeEventListener el "pointerup"    (gobj/get hs "pointerup"))
    (.removeEventListener el "pointerleave" (gobj/get hs "pointerleave"))
    (.removeEventListener el "keydown"    (gobj/get hs "keydown"))
    (.removeEventListener el "keyup"      (gobj/get hs "keyup"))
    (gobj/set el k-autoplay-handlers nil))
  ;; Clean up state
  (.removeAttribute el "data-autoplay-paused")
  (.removeAttribute el "tabindex")
  (gobj/set el k-autoplay-paused false)
  (gobj/set el k-autoplay-last nil)
  nil)

(defn- start-autoplay! [^js el]
  (when-not (autoplay-running? el)
    (let [m (gobj/get el k-model)]
      (when (and m (:autoplay? m) (not (:disabled? m))
                 (gobj/get el k-visible)
                 (not (prefers-reduced-motion?)))
        ;; Make focusable for Space key
        (.setAttribute el "tabindex" "0")
        ;; Init state
        (gobj/set el k-autoplay-paused false)
        (gobj/set el k-autoplay-last nil)
        ;; Attach pause/resume listeners
        (let [on-pointerdown  (fn [_e] (pause-autoplay! el))
              on-pointerup    (fn [_e] (resume-autoplay! el))
              on-pointerleave (fn [_e] (when (gobj/get el k-autoplay-paused)
                                         (resume-autoplay! el)))
              on-keydown    (fn [^js e]
                              (when (= (.-code e) "Space")
                                (.preventDefault e)
                                (pause-autoplay! el)))
              on-keyup      (fn [^js e]
                              (when (= (.-code e) "Space")
                                (resume-autoplay! el)))]
          (.addEventListener el "pointerdown"  on-pointerdown)
          (.addEventListener el "pointerup"    on-pointerup)
          (.addEventListener el "pointerleave" on-pointerleave)
          (.addEventListener el "keydown"    on-keydown)
          (.addEventListener el "keyup"      on-keyup)
          (gobj/set el k-autoplay-handlers
                    #js {:pointerdown  on-pointerdown
                         :pointerup    on-pointerup
                         :pointerleave on-pointerleave
                         :keydown    on-keydown
                         :keyup      on-keyup}))
        ;; Start rAF loop
        (let [tick (autoplay-tick el)]
          (gobj/set el k-autoplay-raf
                    (js/requestAnimationFrame tick))))))
  nil)

;; ── Scroll handler ──────────────────────────────────────────────────────────
(defn- disabled? [^js el]
  (let [m (gobj/get el k-model)]
    (and m (:disabled? m))))

(defn- on-scroll [^js el]
  (when (and (.-isConnected el)
             (gobj/get el k-visible)
             (not (disabled? el))
             (not (prefers-reduced-motion?)))
    (when-not (gobj/get el k-raf)
      (gobj/set el k-raf
                (js/requestAnimationFrame
                 (fn [_] (update-scroll! el)))))))

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

(defn- on-intersection [^js el ^js entries]
  (let [^js entry (aget entries 0)
        is-intersecting (.-isIntersecting entry)
        {:keys [live]} (gobj/get el k-refs)]
    (gobj/set el k-visible is-intersecting)
    (if is-intersecting
      (when-not (disabled? el)
        (attach-scroll-listener! el)
        (update-scroll! el)
        (announce! el live "Story section entered viewport")
        (dispatch! el model/event-enter
                   (model/enter-detail (or (gobj/get el k-last-prog) 0)))
        ;; Start autoplay if enabled
        (let [m (gobj/get el k-model)]
          (when (and m (:autoplay? m))
            (start-autoplay! el))))
      (do
        (detach-scroll-listener! el)
        (stop-autoplay! el)
        (when-not (disabled? el)
          (announce! el live "Story section left viewport")
          (dispatch! el model/event-leave
                     (model/leave-detail (or (gobj/get el k-last-prog) 0))))))))

;; ── Slot change ─────────────────────────────────────────────────────────────
(defn- on-slotchange [^js el]
  ;; Reset step states cache
  (gobj/set el k-step-states nil)
  ;; Re-apply if visible
  (when (gobj/get el k-visible)
    (update-scroll! el)))

;; ── Listener management ────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [{:keys [slot]} (ensure-refs! el)
        ^js slot    slot
        scroll-h    (fn [_e] (on-scroll el))
        slot-h      (fn [_e] (on-slotchange el))
        resize-h    (fn [_e] (when (gobj/get el k-visible)
                               (gobj/set el k-step-states nil)
                               (update-scroll! el)))]
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

;; ── Clean up child attributes ───────────────────────────────────────────────
(defn- clean-step-attrs! [^js el]
  (let [children (get-step-children el)]
    (dotimes [i (.-length children)]
      (let [^js child (aget children i)]
        (.removeAttribute child model/data-active)))))

;; ── DOM patching ────────────────────────────────────────────────────────────
(defn- apply-model! [^js el {:keys [layout split disabled? label] :as m}]
  (let [{:keys [container media live]} (ensure-refs! el)
        ^js container container
        ^js media     media
        ^js live      live]
    ;; Data attributes for CSS selectors
    (.setAttribute el "data-layout" layout)

    ;; Media width via inline style (can be overridden by CSS custom property)
    (if (= layout "top")
      (set! (.. media -style -width) "")
      (set! (.. media -style -width)
            (str "var(--x-scroll-story-media-width," (* split 100) "%)")))

    ;; Aria
    (if (seq label)
      (.setAttribute container "aria-label" label)
      (.removeAttribute container "aria-label"))

    ;; Cache model
    (gobj/set el k-model m)

    (if disabled?
      ;; When disabling: clean active step, detach scroll listener, stop autoplay
      (do
        (clean-step-attrs! el)
        (gobj/set el k-active-index -1)
        (gobj/set el k-step-states nil)
        (detach-scroll-listener! el)
        (stop-autoplay! el))
      ;; When enabled and visible: re-attach and update
      (when (gobj/get el k-visible)
        (attach-scroll-listener! el)
        (update-scroll! el)))
    ;; Autoplay management
    (if (and (:autoplay? m) (not disabled?) (gobj/get el k-visible))
      (start-autoplay! el)
      (stop-autoplay! el)))
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m)))
  nil)

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  ;; layout (string)
  (.defineProperty js/Object proto model/attr-layout
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-layout) "left")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-layout (str v))
                                          (.removeAttribute this model/attr-layout))))
                        :enumerable true :configurable true})

  ;; threshold (number)
  (.defineProperty js/Object proto model/attr-threshold
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-threshold (.getAttribute this model/attr-threshold))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (some? v)
                                          (.setAttribute this model/attr-threshold (str v))
                                          (.removeAttribute this model/attr-threshold))))
                        :enumerable true :configurable true})

  ;; split (number)
  (.defineProperty js/Object proto model/attr-split
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-split (.getAttribute this model/attr-split))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (some? v)
                                          (.setAttribute this model/attr-split (str v))
                                          (.removeAttribute this model/attr-split))))
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

  ;; activeIndex (read-only number)
  (.defineProperty js/Object proto "activeIndex"
                   #js {:get (fn []
                               (this-as ^js this
                                        (let [idx (gobj/get this k-active-index)]
                                          (if (some? idx) idx -1))))
                        :enumerable true :configurable true})

  ;; progress (read-only number)
  (.defineProperty js/Object proto "progress"
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (gobj/get this k-last-prog) 0)))
                        :enumerable true :configurable true})

  ;; autoplay (boolean)
  (.defineProperty js/Object proto model/attr-autoplay
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this model/attr-autoplay)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-autoplay "")
                                          (.removeAttribute this model/attr-autoplay))))
                        :enumerable true :configurable true})

  ;; autoplaySpeed (number)
  (.defineProperty js/Object proto "autoplaySpeed"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-autoplay-speed
                                         (.getAttribute this model/attr-autoplay-speed))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (some? v)
                                          (.setAttribute this model/attr-autoplay-speed (str v))
                                          (.removeAttribute this model/attr-autoplay-speed))))
                        :enumerable true :configurable true})

  ;; autoplayLoop (boolean)
  (.defineProperty js/Object proto "autoplayLoop"
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this model/attr-autoplay-loop)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-autoplay-loop "")
                                          (.removeAttribute this model/attr-autoplay-loop))))
                        :enumerable true :configurable true})

  ;; autoplayIndicator (boolean)
  (.defineProperty js/Object proto "autoplayIndicator"
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this model/attr-autoplay-indicator)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-autoplay-indicator "")
                                          (.removeAttribute this model/attr-autoplay-indicator))))
                        :enumerable true :configurable true})

  ;; autoplayPaused (read-only boolean)
  (.defineProperty js/Object proto "autoplayPaused"
                   #js {:get (fn []
                               (this-as ^js this
                                        (boolean (gobj/get this k-autoplay-paused))))
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
                     ;; Stop autoplay
                     (stop-autoplay! this)
                     ;; Clean data-active from children
                     (clean-step-attrs! this)
                     ;; Dispatch leave event if visible
                     (when (gobj/get this k-visible)
                       (dispatch! this model/event-leave
                                  (model/leave-detail (or (gobj/get this k-last-prog) 0))))
                     (remove-listeners! this)
                     (teardown-observer! this)
                     (gobj/set this k-visible false)
                     (gobj/set this k-active-index -1)
                     (gobj/set this k-last-prog nil)
                     (gobj/set this k-step-states nil)
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
