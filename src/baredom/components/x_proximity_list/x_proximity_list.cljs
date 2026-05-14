(ns baredom.components.x-proximity-list.x-proximity-list
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom       :as du]
   [goog.object             :as gobj]
   [baredom.components.x-proximity-list.model :as model]))

;; ── Instance-field keys ─────────────────────────────────────────────────────
(def ^:private k-refs       "__xProximityListRefs")
(def ^:private k-model      "__xProximityListModel")
(def ^:private k-pointer    "__xProximityListPointer")
(def ^:private k-raf        "__xProximityListRaf")
(def ^:private k-raf-pend?  "__xProximityListRafPending")
(def ^:private k-rects      "__xProximityListRects")
(def ^:private k-displayed  "__xProximityListDisplayed")
(def ^:private k-handlers   "__xProximityListHandlers")
(def ^:private k-ro         "__xProximityListRO")
(def ^:private k-focus-idx  "__xProximityListFocus")

;; Per-frame smoothing speed for the displayed scale/lift values.
;; 0.20 → ~50ms half-life, ~340ms to settle within 1% — responsive but
;; smooth enough that fast pointer motion doesn't make the closest item
;; jump by visible steps. JS-side smoothing is used (rather than CSS
;; transitions) so the component is robust against user CSS that resets
;; the `transition` shorthand on slotted items.
(def ^:private lerp-speed 0.20)
(def ^:private settle-epsilon 0.001)

;; ── Event names ─────────────────────────────────────────────────────────────
(def ^:private evt-pointermove  "pointermove")
(def ^:private evt-pointerleave "pointerleave")
(def ^:private evt-click        "click")
(def ^:private evt-keydown      "keydown")
(def ^:private evt-slotchange   "slotchange")

;; ── Styles ──────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;"
   model/css-gap       ":8px;"
   model/css-item-size ":48px;"
   "}"

   ":host([hidden]){display:none;}"

   "[part=track]{"
   "display:flex;"
   "align-items:center;"
   "gap:var(" model/css-gap ",8px);"
   "position:relative;"
   "}"

   ":host([direction=\"vertical\"]) [part=track]{"
   "flex-direction:column;"
   "}"

   ;; Slotted items get a sensible default footprint. Smoothing of the
   ;; transform happens in JS (per-frame lerp) so authoring no transition
   ;; here keeps user CSS from accidentally double-smoothing the motion.
   "::slotted(*){"
   "box-sizing:border-box;"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "width:var(" model/css-item-size ",48px);"
   "height:var(" model/css-item-size ",48px);"
   "transform-origin:center center;"
   "will-change:transform;"
   "}"

   "::slotted(:focus-visible){"
   "outline:2px solid var(--x-color-primary,#2563eb);"
   "outline-offset:2px;"
   "}"

   "@media(prefers-reduced-motion:reduce){"
   "::slotted(*){transform:none!important;}"
   "}"))

;; ── Motion helper ───────────────────────────────────────────────────────────
(defn- prefers-reduced-motion? []
  (boolean (.-matches (.matchMedia js/window "(prefers-reduced-motion:reduce)"))))

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        track (.createElement js/document "div")
        slot  (.createElement js/document "slot")]
    (set! (.-textContent style) style-text)
    (du/set-attr! track "part" "track")
    (.appendChild track slot)
    (.appendChild root style)
    (.appendChild root track)
    (du/setv! el k-refs {:root root :track track :slot slot})))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:direction-raw  (du/get-attr el model/attr-direction)
    :radius-raw     (du/get-attr el model/attr-radius)
    :max-scale-raw  (du/get-attr el model/attr-max-scale)
    :lift-raw       (du/get-attr el model/attr-lift)
    :gap-raw        (du/get-attr el model/attr-gap)
    :disabled-attr  (du/get-attr el model/attr-disabled)}))

;; ── Slotted children ────────────────────────────────────────────────────────
(defn- assigned-elements [^js el]
  (let [refs (du/getv el k-refs)]
    (when-let [^js slot (:slot refs)]
      (.assignedElements slot))))

;; Forward declaration: apply-item-styles! re-arms the RAF when items are
;; still settling, but schedule-raf! is defined after it.
(declare schedule-raf!)

;; ── Rect cache ──────────────────────────────────────────────────────────────
(defn- cache-rects! [^js el]
  (let [refs       (du/getv el k-refs)
        ^js track  (:track refs)
        ^js items  (or (assigned-elements el) #js [])
        n          (.-length items)
        ^js rects  #js []]
    (when (and track (pos? n))
      (let [^js trect (.getBoundingClientRect track)
            t-left    (.-left trect)
            t-top     (.-top trect)]
        (dotimes [i n]
          (let [^js item (aget items i)
                ^js r    (.getBoundingClientRect item)
                cx       (- (+ (.-left r) (/ (.-width r) 2)) t-left)
                cy       (- (+ (.-top r)  (/ (.-height r) 2)) t-top)]
            (.push rects #js {:cx cx :cy cy})))))
    (du/setv! el k-rects rects)))

;; ── Item style writes ───────────────────────────────────────────────────────

(defn- ensure-displayed-state!
  "Maintain a per-item record of the lerped scale/lift values that are
  currently visible on screen. The array length tracks slot count; new
  entries start at the resting (1.0, 0.0) baseline."
  [^js el n]
  (let [^js arr (or (du/getv el k-displayed) #js [])
        len     (.-length arr)]
    (cond
      (< len n)
      (loop [i len]
        (when (< i n)
          (.push arr #js {:scale 1.0 :lift 0.0})
          (recur (inc i))))

      (> len n)
      (loop [i len]
        (when (> i n)
          (.pop arr)
          (recur (dec i)))))
    (du/setv! el k-displayed arr)
    arr))

(defn- clear-item-inline-style! [^js item]
  (let [^js sty (.-style item)]
    (.removeProperty sty model/css-item-influence)
    (.removeProperty sty model/css-item-scale)
    (.removeProperty sty model/css-item-lift)
    (set! (.-transform sty) "")))

(defn- reset-item-styles!
  "Clear inline state on every slotted item AND the cached displayed values
  so the next activation lerps cleanly from the resting baseline."
  [^js el]
  (when-let [^js disp (du/getv el k-displayed)]
    (dotimes [i (.-length disp)]
      (let [^js d (aget disp i)]
        (gobj/set d "scale" 1.0)
        (gobj/set d "lift"  0.0))))
  (when-let [^js items (assigned-elements el)]
    (dotimes [i (.-length items)]
      (clear-item-inline-style! (aget items i)))))

(defn- approach
  "One lerp step toward `target` from `current`. Snaps to target when within
  the settle epsilon to avoid asymptotic crawl."
  [current target]
  (let [delta (- target current)]
    (if (< (js/Math.abs delta) settle-epsilon)
      target
      (model/lerp current target lerp-speed))))

(defn- write-item-style! [^js item ^js d influence vertical?]
  (let [^js sty (.-style item)
        scale   (gobj/get d "scale")
        lift    (gobj/get d "lift")
        tx      (if vertical?
                  (str "translateX(" lift "px) scale(" scale ")")
                  (str "translateY(" lift "px) scale(" scale ")"))]
    (.setProperty sty model/css-item-influence (str influence))
    (.setProperty sty model/css-item-scale     (str scale))
    (.setProperty sty model/css-item-lift      (str lift "px"))
    (set! (.-transform sty) tx)))

(defn- apply-item-styles!
  "Walk every slotted item, lerp its displayed state toward the target
  derived from the current pointer position, write inline transform + CSS
  variables, and reschedule the RAF if anything is still settling."
  [^js el]
  (let [m         (du/getv el k-model)
        ^js ptr   (du/getv el k-pointer)
        active?   (and ptr (true? (gobj/get ptr "active")))
        ^js rects (or (du/getv el k-rects) #js [])
        ^js items (or (assigned-elements el) #js [])
        n         (min (.-length rects) (.-length items))
        ^js disp  (ensure-displayed-state! el (.-length items))
        radius    (:radius m)
        max-scale (:max-scale m)
        lift-max  (:lift m)
        vertical? (:vertical? m)]
    (loop [i 0 more? false]
      (if (>= i n)
        (when more? (schedule-raf! el))
        (let [^js rect (aget rects i)
              ^js item (aget items i)
              ^js d    (aget disp i)
              cx       (gobj/get rect "cx")
              cy       (gobj/get rect "cy")
              inf          (if active?
                             (model/compute-influence cx cy
                                                      (gobj/get ptr "x")
                                                      (gobj/get ptr "y")
                                                      radius)
                             0.0)
              target-scale (model/compute-scale inf max-scale)
              cx-delta     (cond
                             (not active?) 0.0
                             vertical?     (- (gobj/get ptr "x") cx)
                             :else         (- (gobj/get ptr "y") cy))
              target-lift  (if (and active? (pos? lift-max))
                             (model/compute-lift inf cx-delta lift-max)
                             0.0)
              new-scale    (approach (gobj/get d "scale") target-scale)
              new-lift     (approach (gobj/get d "lift")  target-lift)
              settling?    (or (not= new-scale target-scale)
                               (not= new-lift  target-lift))]
          (gobj/set d "scale" new-scale)
          (gobj/set d "lift"  new-lift)
          (if (and (not active?)
                   (== new-scale 1.0)
                   (== new-lift  0.0))
            ;; Fully settled at base while inactive — strip inline styles so
            ;; the user's CSS can take over cleanly.
            (clear-item-inline-style! item)
            (write-item-style! item d inf vertical?))
          (recur (inc i) (or more? settling?)))))))

;; ── RAF coalescing ──────────────────────────────────────────────────────────
(defn- raf-tick! [^js el]
  (du/setv! el k-raf-pend? false)
  (when (.-isConnected el)
    (apply-item-styles! el)))

(defn- schedule-raf! [^js el]
  (when-not (du/getv el k-raf-pend?)
    (du/setv! el k-raf-pend? true)
    (let [id (js/requestAnimationFrame (fn [_] (raf-tick! el)))]
      (du/setv-untraced! el k-raf id))))

(defn- cancel-raf! [^js el]
  (when-let [id (du/getv el k-raf)]
    (js/cancelAnimationFrame id)
    (du/setv-untraced! el k-raf nil))
  (du/setv! el k-raf-pend? false))

;; ── Pointer handlers ────────────────────────────────────────────────────────
(defn- on-pointermove [^js el ^js e]
  (let [m (du/getv el k-model)]
    ;; Touch suppression: only mouse drives proximity. Reduced-motion silence.
    (when (and m
               (not (:disabled? m))
               (= "mouse" (.-pointerType e))
               (not (prefers-reduced-motion?)))
      (let [refs      (du/getv el k-refs)
            ^js track (:track refs)
            ^js ptr   (du/getv el k-pointer)]
        (when (and track ptr)
          (let [^js trect (.getBoundingClientRect track)]
            (gobj/set ptr "x" (- (.-clientX e) (.-left trect)))
            (gobj/set ptr "y" (- (.-clientY e) (.-top trect)))
            (gobj/set ptr "active" true)
            (schedule-raf! el)))))))

(defn- on-pointerleave [^js el ^js _e]
  (when-let [^js ptr (du/getv el k-pointer)]
    (gobj/set ptr "active" false)
    (schedule-raf! el)))

;; ── Click / keyboard select ─────────────────────────────────────────────────
(defn- find-item-index [^js target ^js items]
  (let [n (.-length items)]
    (loop [i 0]
      (when (< i n)
        (if (= target (aget items i))
          i
          (recur (inc i)))))))

(defn- dispatch-select! [^js el index ^js item source]
  (du/dispatch-cancelable! el model/event-select
                           (clj->js (model/select-detail index item source))))

(defn- on-click [^js el ^js e]
  (let [m (du/getv el k-model)]
    (when (and m (not (:disabled? m)))
      (when-let [^js items (assigned-elements el)]
        (let [^js target (.-target e)]
          (when-let [idx (find-item-index target items)]
            (du/setv! el k-focus-idx idx)
            (dispatch-select! el idx (aget items idx) "pointer")))))))

(defn- on-keydown [^js el ^js e]
  (let [m (du/getv el k-model)]
    (when (and m (not (:disabled? m)))
      (let [^js items   (assigned-elements el)
            n           (when items (.-length items))
            key         (.-key e)
            vertical?   (:vertical? m)
            prev-key    (if vertical? "ArrowUp"   "ArrowLeft")
            next-key    (if vertical? "ArrowDown" "ArrowRight")
            focus-idx   (or (du/getv el k-focus-idx) -1)]
        (when (and n (pos? n))
          (cond
            (= key prev-key)
            (do (.preventDefault e)
                (let [new-idx (if (< focus-idx 1) (dec n) (dec focus-idx))]
                  (du/setv! el k-focus-idx new-idx)
                  (.focus ^js (aget items new-idx))))

            (= key next-key)
            (do (.preventDefault e)
                (let [new-idx (if (>= focus-idx (dec n)) 0 (inc focus-idx))]
                  (du/setv! el k-focus-idx new-idx)
                  (.focus ^js (aget items new-idx))))

            (or (= key "Enter") (= key " "))
            (when (>= focus-idx 0)
              (.preventDefault e)
              (dispatch-select! el focus-idx (aget items focus-idx) "keyboard"))))))))

;; ── Slot change ─────────────────────────────────────────────────────────────
(defn- ensure-tabindex! [^js item]
  (when (nil? (.getAttribute item "tabindex"))
    (du/set-attr! item "tabindex" "0")))

(defn- on-slotchange [^js el ^js _e]
  (when-let [^js items (assigned-elements el)]
    (dotimes [i (.-length items)]
      (ensure-tabindex! (aget items i))))
  ;; Defer rect cache until layout has settled so geometry is final.
  (js/requestAnimationFrame
   (fn [_]
     (cache-rects! el)
     (schedule-raf! el))))

;; ── Resize ──────────────────────────────────────────────────────────────────
(defn- on-resize! [^js el]
  (cache-rects! el)
  (schedule-raf! el))

;; ── Listener management ─────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [refs     (du/getv el k-refs)
        ^js slot (:slot refs)
        h-move   (fn [e] (on-pointermove el e))
        h-leave  (fn [e] (on-pointerleave el e))
        h-click  (fn [e] (on-click el e))
        h-key    (fn [e] (on-keydown el e))
        h-slot   (fn [e] (on-slotchange el e))]
    (.addEventListener el evt-pointermove  h-move)
    (.addEventListener el evt-pointerleave h-leave)
    (.addEventListener el evt-click        h-click)
    (.addEventListener el evt-keydown      h-key)
    (when slot
      (.addEventListener slot evt-slotchange h-slot))
    (du/setv! el k-handlers
              {:move h-move :leave h-leave :click h-click
               :key  h-key  :slot  h-slot})))

(defn- remove-listeners! [^js el]
  (when-let [handlers (du/getv el k-handlers)]
    (.removeEventListener el evt-pointermove  (:move  handlers))
    (.removeEventListener el evt-pointerleave (:leave handlers))
    (.removeEventListener el evt-click        (:click handlers))
    (.removeEventListener el evt-keydown      (:key   handlers))
    (let [refs     (du/getv el k-refs)
          ^js slot (:slot refs)]
      (when slot
        (.removeEventListener slot evt-slotchange (:slot handlers))))
    (du/setv! el k-handlers nil)))

;; ── A11y ────────────────────────────────────────────────────────────────────
(defn- set-a11y! [^js el]
  (when-not (du/has-attr? el "role")
    (du/set-attr! el "role" "list")))

;; ── Apply model ─────────────────────────────────────────────────────────────
(defn- apply-model! [^js el {:keys [gap disabled?] :as m}]
  (ensure-refs! el)
  (let [^js style (.-style el)]
    (.setProperty style model/css-gap (str gap "px")))
  (cache-rects! el)
  (if (or disabled? (prefers-reduced-motion?))
    (do (cancel-raf! el)
        (when-let [^js ptr (du/getv el k-pointer)]
          (gobj/set ptr "active" false))
        (reset-item-styles! el))
    (schedule-raf! el))
  (du/setv! el k-model m))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ── Property accessors (Tier 0) ─────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

;; ── Lifecycle ───────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (du/setv! el k-pointer #js {:x 0 :y 0 :active false})
  (du/setv! el k-rects #js [])
  (du/setv! el k-displayed #js [])
  (du/setv! el k-focus-idx -1)
  (du/setv! el k-raf-pend? false)
  (ensure-refs! el)
  (set-a11y! el)
  (remove-listeners! el)
  (add-listeners! el)
  (when-let [^js old-ro (du/getv el k-ro)]
    (.disconnect old-ro))
  (let [ro (js/ResizeObserver. (fn [_] (on-resize! el)))]
    (du/setv! el k-ro ro)
    (.observe ro el))
  (update-from-attrs! el)
  ;; Defer initial slot/rect setup so children are assigned before measurement.
  (js/requestAnimationFrame
   (fn [_]
     (when-let [^js items (assigned-elements el)]
       (dotimes [i (.-length items)]
         (ensure-tabindex! (aget items i))))
     (cache-rects! el))))

(defn- disconnected! [^js el]
  (cancel-raf! el)
  (remove-listeners! el)
  (reset-item-styles! el)
  (when-let [^js ro (du/getv el k-ro)]
    (.disconnect ro)
    (du/setv! el k-ro nil)))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Public API ──────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
    {:observed-attributes  model/observed-attributes
     :connected-fn         connected!
     :disconnected-fn      disconnected!
     :attribute-changed-fn attribute-changed!
     :setup-prototype-fn   install-property-accessors!}))
