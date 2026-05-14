(ns baredom.components.x-scroll-story.x-scroll-story
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
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
   "height:100dvh;}"

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

    (du/set-attr! container "part" "container")
    (du/set-attr! container "role" "region")

    (du/set-attr! media-el "part" "media")
    (du/set-attr! media-slot "name" "media")
    (let [indicator (.createElement js/document "div")]
      (du/set-attr! indicator "part" "indicator")
      (.appendChild media-el indicator))
    (.appendChild media-el media-slot)

    (du/set-attr! steps-el "part" "steps")
    (.appendChild steps-el steps-slot)

    (.appendChild container media-el)
    (.appendChild container steps-el)

    (du/set-attr! live "part" "live")
    (du/set-attr! live "aria-live" "polite")
    (du/set-attr! live "aria-atomic" "true")

    (.appendChild root style)
    (.appendChild root container)
    (.appendChild root live)

    (du/setv! el k-refs
              {:root      root
               :container container
               :media     media-el
               :steps     steps-el
               :slot      steps-slot
               :live      live})))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:layout-raw             (du/get-attr el model/attr-layout)
    :threshold-raw          (du/get-attr el model/attr-threshold)
    :split-raw              (du/get-attr el model/attr-split)
    :disabled-attr          (when (du/has-attr? el model/attr-disabled) "")
    :label-raw              (du/get-attr el model/attr-label)
    :autoplay-attr          (when (du/has-attr? el model/attr-autoplay) "")
    :autoplay-speed-raw     (du/get-attr el model/attr-autoplay-speed)
    :autoplay-loop-attr     (when (du/has-attr? el model/attr-autoplay-loop) "")
    :autoplay-indicator-attr (when (du/has-attr? el model/attr-autoplay-indicator) "")}))

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
;; ── Announce to live region ─────────────────────────────────────────────────
(defn- announce! [^js _el ^js live msg]
  (set! (.-textContent live) msg)
  (js/setTimeout (fn [] (set! (.-textContent live) "")) 3000))

;; ── Step activation ─────────────────────────────────────────────────────────
(defn- update-active-step!
  "Mark a step child as active (set data-active), remove from previous."
  [^js el children new-index]
  (let [old-index (or (du/getv el k-active-index) -1)]
    (when (not= old-index new-index)
      ;; Remove data-active from old step
      (when (and (>= old-index 0) (< old-index (.-length children)))
        (let [^js old-child (aget children old-index)]
          (du/remove-attr! old-child model/data-active)))
      ;; Set data-active on new step
      (when (and (>= new-index 0) (< new-index (.-length children)))
        (let [^js new-child (aget children new-index)]
          (du/set-attr! new-child model/data-active "")))
      ;; Update host data attribute
      (if (>= new-index 0)
        (du/set-attr! el "data-active-index" (str new-index))
        (du/remove-attr! el "data-active-index"))
      ;; Cache active index
      (du/setv! el k-active-index new-index)
      ;; Dispatch step-change event
      (let [old-id (when (and (>= old-index 0) (< old-index (.-length children)))
                     (step-id (aget children old-index)))
            new-id (when (and (>= new-index 0) (< new-index (.-length children)))
                     (step-id (aget children new-index)))]
        (du/dispatch! el model/event-step-change
                   (clj->js (model/step-change-detail new-index new-id old-index old-id)))
        ;; Announce to live region
        (let [{:keys [live]} (du/getv el k-refs)]
          (announce! el live (str "Step " (inc new-index) " active")))))))

;; ── Step enter/leave tracking ───────────────────────────────────────────────
(defn- ensure-step-states!
  "Ensure the step states array matches the current child count."
  [^js el n]
  (let [states (du/getv el k-step-states)]
    (if (and states (= (.-length states) n))
      states
      (let [new-states (make-array n)]
        (dotimes [i n]
          (aset new-states i
                (if (and states (< i (.-length states)))
                  (aget states i)
                  false)))
        (du/setv! el k-step-states new-states)
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
                (du/dispatch! el model/event-step-enter
                           (clj->js (model/step-enter-detail i id progress))))
            (and was-in? (not is-in?))
            (do (aset states i false)
                (du/dispatch! el model/event-step-leave
                           (clj->js (model/step-leave-detail i id progress))))))))))

;; ── Transform application ───────────────────────────────────────────────────
(defn- update-scroll!
  "Compute step activation and dispatch events. Called from rAF callback."
  [^js el]
  (let [m (or (du/getv el k-model) (read-model el))]
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
            {:keys [steps]} (du/getv el k-refs)
            ^js steps     steps
            steps-rect    (.getBoundingClientRect steps)
            progress      (model/compute-overall-progress
                           (.-top steps-rect) (.-height steps-rect) vp-height)
            last-prog     (du/getv el k-last-prog)]

        ;; Update active step (handles data-active attribute + events)
        (update-active-step! el children active-idx)

        ;; Track step enter/leave
        (update-step-states! el children step-rects trigger-y)

        ;; Dispatch progress event if changed
        (when (or (nil? last-prog)
                  (> (js/Math.abs (- progress last-prog)) 0.001))
          (du/setv! el k-last-prog progress)
          (let [active-id (when (and (>= active-idx 0) (< active-idx n))
                            (step-id (aget children active-idx)))]
            (du/dispatch! el model/event-progress
                       (clj->js (model/progress-detail progress active-idx active-id))))))))
  ;; Clear rAF handle
  (du/setv! el k-raf nil))

;; ── Autoplay ────────────────────────────────────────────────────────────────

(defn- autoplay-running? [^js el]
  (some? (du/getv el k-autoplay-raf)))

(defn- autoplay-tick [^js el]
  (fn tick [^js ts]
    (when (and (.-isConnected el)
               (du/getv el k-visible)
               (not (du/getv el k-autoplay-paused)))
      (let [m (du/getv el k-model)]
        (when (and m (:autoplay? m) (not (:disabled? m)))
          (let [last-ts (du/getv el k-autoplay-last)
                dt      (if last-ts (/ (- ts last-ts) 1000.0) 0)]
            (du/setv! el k-autoplay-last ts)
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
                    (du/setv! el k-autoplay-last nil)))))
            (du/setv! el k-autoplay-raf
                      (js/requestAnimationFrame tick))))))))

(defn- pause-autoplay! [^js el]
  (when-not (du/getv el k-autoplay-paused)
    (du/setv! el k-autoplay-paused true)
    ;; Cancel pending autoplay rAF
    (when-let [raf (du/getv el k-autoplay-raf)]
      (js/cancelAnimationFrame raf)
      (du/setv! el k-autoplay-raf nil))
    ;; Set data attribute for CSS indicator
    (du/set-attr! el "data-autoplay-paused" "")
    ;; Dispatch pause event
    (let [prog     (or (du/getv el k-last-prog) 0)
          idx      (or (du/getv el k-active-index) -1)
          children (.-children el)
          aid      (when (and (>= idx 0) (< idx (.-length children)))
                     (step-id (aget children idx)))]
      (du/dispatch! el model/event-autoplay-pause
                 (clj->js (model/autoplay-pause-detail prog idx aid))))))

(defn- resume-autoplay! [^js el]
  (when (du/getv el k-autoplay-paused)
    (du/setv! el k-autoplay-paused false)
    (du/remove-attr! el "data-autoplay-paused")
    ;; Reset timestamp to avoid delta spike
    (du/setv! el k-autoplay-last nil)
    ;; Restart rAF loop
    (let [tick (autoplay-tick el)]
      (du/setv! el k-autoplay-raf
                (js/requestAnimationFrame tick)))
    ;; Dispatch resume event
    (let [prog     (or (du/getv el k-last-prog) 0)
          idx      (or (du/getv el k-active-index) -1)
          children (.-children el)
          aid      (when (and (>= idx 0) (< idx (.-length children)))
                     (step-id (aget children idx)))]
      (du/dispatch! el model/event-autoplay-resume
                 (clj->js (model/autoplay-resume-detail prog idx aid))))))

(defn- stop-autoplay! [^js el]
  ;; Cancel rAF
  (when-let [raf (du/getv el k-autoplay-raf)]
    (js/cancelAnimationFrame raf)
    (du/setv! el k-autoplay-raf nil))
  ;; Remove pause/resume listeners
  (when-let [hs (du/getv el k-autoplay-handlers)]
    (.removeEventListener el "pointerdown"  (gobj/get hs "pointerdown"))
    (.removeEventListener el "pointerup"    (gobj/get hs "pointerup"))
    (.removeEventListener el "pointerleave" (gobj/get hs "pointerleave"))
    (.removeEventListener el "keydown"    (gobj/get hs "keydown"))
    (.removeEventListener el "keyup"      (gobj/get hs "keyup"))
    (du/setv! el k-autoplay-handlers nil))
  ;; Clean up state
  (du/remove-attr! el "data-autoplay-paused")
  (du/remove-attr! el "tabindex")
  (du/setv! el k-autoplay-paused false)
  (du/setv! el k-autoplay-last nil))

(defn- start-autoplay! [^js el]
  (when-not (autoplay-running? el)
    (let [m (du/getv el k-model)]
      (when (and m (:autoplay? m) (not (:disabled? m))
                 (du/getv el k-visible)
                 (not (prefers-reduced-motion?)))
        ;; Make focusable for Space key
        (du/set-attr! el "tabindex" "0")
        ;; Init state
        (du/setv! el k-autoplay-paused false)
        (du/setv! el k-autoplay-last nil)
        ;; Attach pause/resume listeners
        (let [on-pointerdown  (fn [_e] (pause-autoplay! el))
              on-pointerup    (fn [_e] (resume-autoplay! el))
              on-pointerleave (fn [_e] (when (du/getv el k-autoplay-paused)
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
          (du/setv! el k-autoplay-handlers
                    #js {:pointerdown  on-pointerdown
                         :pointerup    on-pointerup
                         :pointerleave on-pointerleave
                         :keydown    on-keydown
                         :keyup      on-keyup}))
        ;; Start rAF loop
        (let [tick (autoplay-tick el)]
          (du/setv! el k-autoplay-raf
                    (js/requestAnimationFrame tick)))))))

;; ── Scroll handler ──────────────────────────────────────────────────────────
(defn- disabled? [^js el]
  (let [m (du/getv el k-model)]
    (and m (:disabled? m))))

(defn- on-scroll [^js el]
  (when (and (.-isConnected el)
             (du/getv el k-visible)
             (not (disabled? el))
             (not (prefers-reduced-motion?)))
    (when-not (du/getv el k-raf)
      (du/setv! el k-raf
                (js/requestAnimationFrame
                 (fn [_] (update-scroll! el)))))))

;; ── IntersectionObserver callback ───────────────────────────────────────────
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

(defn- on-intersection [^js el ^js entries]
  (let [^js entry (aget entries 0)
        is-intersecting (.-isIntersecting entry)
        {:keys [live]} (du/getv el k-refs)]
    (du/setv! el k-visible is-intersecting)
    (if is-intersecting
      (when-not (disabled? el)
        (attach-scroll-listener! el)
        (update-scroll! el)
        (announce! el live "Story section entered viewport")
        (du/dispatch! el model/event-enter
                   (clj->js (model/enter-detail (or (du/getv el k-last-prog) 0))))
        ;; Start autoplay if enabled
        (let [m (du/getv el k-model)]
          (when (and m (:autoplay? m))
            (start-autoplay! el))))
      (do
        (detach-scroll-listener! el)
        (stop-autoplay! el)
        (when-not (disabled? el)
          (announce! el live "Story section left viewport")
          (du/dispatch! el model/event-leave
                     (clj->js (model/leave-detail (or (du/getv el k-last-prog) 0)))))))))

;; ── Slot change ─────────────────────────────────────────────────────────────
(defn- on-slotchange [^js el]
  ;; Reset step states cache
  (du/setv! el k-step-states nil)
  ;; Re-apply if visible
  (when (du/getv el k-visible)
    (update-scroll! el)))

;; ── Listener management ────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [{:keys [slot]} (ensure-refs! el)
        ^js slot    slot
        scroll-h    (fn [_e] (on-scroll el))
        slot-h      (fn [_e] (on-slotchange el))
        resize-h    (fn [_e] (when (du/getv el k-visible)
                               (du/setv! el k-step-states nil)
                               (update-scroll! el)))]
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
  ;; Cancel pending rAF
  (when-let [raf (du/getv el k-raf)]
    (js/cancelAnimationFrame raf)
    (du/setv! el k-raf nil))
  (du/setv! el k-handlers nil))

;; ── IntersectionObserver setup/teardown ─────────────────────────────────────
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

;; ── Clean up child attributes ───────────────────────────────────────────────
(defn- clean-step-attrs! [^js el]
  (let [children (get-step-children el)]
    (dotimes [i (.-length children)]
      (let [^js child (aget children i)]
        (du/remove-attr! child model/data-active)))))

;; ── DOM patching ────────────────────────────────────────────────────────────
(defn- apply-model! [^js el {:keys [layout split disabled? label] :as m}]
  (let [{:keys [container media]} (ensure-refs! el)
        ^js container container
        ^js media     media]
    ;; Data attributes for CSS selectors
    (du/set-attr! el "data-layout" layout)

    ;; Media width via inline style (can be overridden by CSS custom property)
    (if (= layout "top")
      (set! (.. media -style -width) "")
      (set! (.. media -style -width)
            (str "var(--x-scroll-story-media-width," (* split 100) "%)")))

    ;; Aria
    (if (seq label)
      (du/set-attr! container "aria-label" label)
      (du/remove-attr! container "aria-label"))

    ;; Cache model
    (du/setv! el k-model m)

    (if disabled?
      ;; When disabling: clean active step, detach scroll listener, stop autoplay
      (do
        (clean-step-attrs! el)
        (du/setv! el k-active-index -1)
        (du/setv! el k-step-states nil)
        (detach-scroll-listener! el)
        (stop-autoplay! el))
      ;; When enabled and visible: re-attach and update
      (when (du/getv el k-visible)
        (attach-scroll-listener! el)
        (update-scroll! el)))
    ;; Autoplay management
    (if (and (:autoplay? m) (not disabled?) (du/getv el k-visible))
      (start-autoplay! el)
      (stop-autoplay! el))))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ── Property accessors ──────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/define-string-prop! proto model/attr-layout    model/attr-layout    "left")
  (du/define-parsed-prop! proto model/attr-threshold model/attr-threshold model/parse-threshold)
  (du/define-parsed-prop! proto model/attr-split     model/attr-split     model/parse-split)
  (du/define-bool-prop!   proto model/attr-disabled  model/attr-disabled)
  (du/define-bool-prop!   proto model/attr-autoplay  model/attr-autoplay)
  (du/define-parsed-prop! proto "autoplaySpeed"      model/attr-autoplay-speed model/parse-autoplay-speed)
  (du/define-bool-prop!   proto "autoplayLoop"       model/attr-autoplay-loop)
  (du/define-bool-prop!   proto "autoplayIndicator"  model/attr-autoplay-indicator)

  ;; label uses strict-empty setter semantics: setting "" removes the
  ;; attribute so the empty-default applies. define-string-prop! would
  ;; keep "" as an explicit empty attribute.
  (.defineProperty js/Object proto model/attr-label
    #js {:get (fn xss-get-label []
                (this-as ^js this
                  (or (.getAttribute this model/attr-label) "")))
         :set (fn xss-set-label [v]
                (this-as ^js this
                  (if (and v (not= v ""))
                    (du/set-attr! this model/attr-label (str v))
                    (du/remove-attr! this model/attr-label))))
         :enumerable true :configurable true})

  ;; Read-only props — no setter, no shared helper applies.
  (.defineProperty js/Object proto "activeIndex"
    #js {:get (fn xss-get-active-index []
                (this-as ^js this
                  (let [idx (gobj/get this k-active-index)]
                    (if (some? idx) idx -1))))
         :enumerable true :configurable true})
  (.defineProperty js/Object proto "progress"
    #js {:get (fn xss-get-progress []
                (this-as ^js this (or (gobj/get this k-last-prog) 0)))
         :enumerable true :configurable true})
  (.defineProperty js/Object proto "autoplayPaused"
    #js {:get (fn xss-get-autoplay-paused []
                (this-as ^js this (boolean (gobj/get this k-autoplay-paused))))
         :enumerable true :configurable true}))

;; ── Element class ───────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el)
  (setup-observer! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  ;; Stop autoplay
  (stop-autoplay! el)
  ;; Clean data-active from children
  (clean-step-attrs! el)
  ;; Dispatch leave event if visible
  (when (du/getv el k-visible)
    (du/dispatch! el model/event-leave
                  (clj->js (model/leave-detail (or (du/getv el k-last-prog) 0)))))
  (remove-listeners! el)
  (teardown-observer! el)
  (du/setv! el k-visible false)
  (du/setv! el k-active-index -1)
  (du/setv! el k-last-prog nil)
  (du/setv! el k-step-states nil))

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
