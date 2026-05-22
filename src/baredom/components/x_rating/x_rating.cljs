(ns baredom.components.x-rating.x-rating
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-rating.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (Closure-safe: host-element access via du/getv / du/setv!)
;; ---------------------------------------------------------------------------
(def ^:private k-refs      "__xRatingRefs")
(def ^:private k-model     "__xRatingModel")
(def ^:private k-internals "__xRatingInternals")
(def ^:private k-handlers  "__xRatingHandlers")
(def ^:private k-preview   "__xRatingPreview")

;; Keys that change the value — consumed (but blocked) in readonly mode
(def ^:private value-changing-keys
  #{"ArrowUp" "ArrowDown" "ArrowLeft" "ArrowRight"
    "Home" "End" "Delete" "Backspace"})

;; ---------------------------------------------------------------------------
;; Shape SVG paths (24x24 viewBox; static literals)
;; ---------------------------------------------------------------------------
(def ^:private svg-ns "http://www.w3.org/2000/svg")

(def ^:private path-star
  "M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z")

(def ^:private path-heart
  (str "M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 "
       "3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 "
       "3.78-3.4 6.86-8.55 11.54L12 21.35z"))

(defn- shape-path [shape]
  (if (= "heart" shape) path-heart path-star))

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;"
   "color-scheme:light dark;"
   "--x-rating-active-color:var(--x-color-warning,#f59e0b);"
   "--x-rating-inactive-color:rgba(0,0,0,0.20);"
   "--x-rating-hover-color:var(--x-rating-active-color);"
   "--x-rating-focus-ring:var(--x-color-focus-ring,#60a5fa);"
   "--x-rating-disabled-opacity:0.45;"
   "--x-rating-label-color:rgba(0,0,0,0.60);"
   "}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-rating-inactive-color:rgba(255,255,255,0.22);"
   "--x-rating-focus-ring:var(--x-color-focus-ring,#93c5fd);"
   "--x-rating-label-color:rgba(255,255,255,0.60);"
   "}}"

   "[part=base]{"
   "display:inline-flex;"
   "flex-direction:column;"
   "gap:4px;"
   "align-items:flex-start;"
   "box-sizing:border-box;"
   "--_x-rating-star-sz:22px;"
   "}"
   "[part=base][data-size=sm]{--_x-rating-star-sz:16px;}"
   "[part=base][data-size=md]{--_x-rating-star-sz:22px;}"
   "[part=base][data-size=lg]{--_x-rating-star-sz:30px;}"

   "[part=label-text]{"
   "font-size:0.875rem;"
   "color:var(--x-rating-label-color);"
   "font-weight:500;}"

   ;; The stars row is the focusable role=slider element.
   "[part=stars]{"
   "display:inline-flex;"
   "touch-action:manipulation;"
   "border-radius:var(--x-radius-sm,0.375rem);}"

   "[part=stars]:focus-visible{"
   "outline:none;"
   "box-shadow:0 0 0 3px var(--x-rating-focus-ring);}"

   "[part=star]{"
   "position:relative;"
   "display:inline-block;"
   "width:var(--_x-rating-star-sz,22px);"
   "height:var(--_x-rating-star-sz,22px);"
   "cursor:pointer;"
   "transition:transform var(--x-transition-duration,140ms) "
   "var(--x-transition-easing,ease);}"

   "[part=star] svg{"
   "position:absolute;"
   "inset:0;"
   "width:100%;"
   "height:100%;"
   "display:block;}"

   ".x-rating-star-base{color:var(--x-rating-inactive-color);}"
   ".x-rating-star-fill{"
   "color:var(--x-rating-active-color);"
   "clip-path:inset(0 calc(100% - var(--_x-rating-star-fill,0%)) 0 0);}"

   ;; Hover preview tints the fill with the hover colour.
   "[part=stars][data-preview] .x-rating-star-fill{"
   "color:var(--x-rating-hover-color);}"

   ;; Subtle pop on the hovered star while interactive.
   ":host(:not([data-disabled]):not([data-readonly])) [part=star]:hover{"
   "transform:scale(1.08);}"

   ;; Disabled — no interaction.
   ":host([data-disabled]) [part=stars]{"
   "opacity:var(--x-rating-disabled-opacity);"
   "cursor:default;"
   "pointer-events:none;}"

   ;; Readonly — pointer interaction off; keyboard blocked via keydown handler.
   ":host([data-readonly]) [part=stars]{"
   "cursor:default;"
   "pointer-events:none;}"

   ;; Reduced motion.
   "@media (prefers-reduced-motion:reduce){"
   "[part=star]{transition:none;}}"

   ;; Coarse pointer — larger touch targets per size tier.
   "@media (pointer:coarse){"
   "[part=base][data-size=sm]{--_x-rating-star-sz:22px;}"
   "[part=base][data-size=md]{--_x-rating-star-sz:30px;}"
   "[part=base][data-size=lg]{--_x-rating-star-sz:38px;}}"))

;; ---------------------------------------------------------------------------
;; Shadow DOM construction (shadow-builders)
;; ---------------------------------------------------------------------------
(defn- make-svg-layer
  "One filled SVG layer for a star — `css-class` selects base vs fill styling."
  [shape css-class]
  (let [^js svg  (.createElementNS js/document svg-ns "svg")
        ^js path (.createElementNS js/document svg-ns "path")]
    (du/set-attr! svg "viewBox" "0 0 24 24")
    (du/set-attr! svg "class" css-class)
    (du/set-attr! svg "aria-hidden" "true")
    (du/set-attr! path "fill" "currentColor")
    (du/set-attr! path "d" (shape-path shape))
    (.appendChild svg path)
    svg))

(defn- make-star!
  "One star cell: an inactive base layer under a clip-revealed fill layer."
  [index shape]
  (let [star (.createElement js/document "span")]
    (du/set-attr! star "part" "star")
    (du/set-attr! star "data-index" (str index))
    (du/set-attr! star "aria-hidden" "true")
    (.appendChild star (make-svg-layer shape "x-rating-star-base"))
    (.appendChild star (make-svg-layer shape "x-rating-star-fill"))
    star))

(defn- build-star-els
  "A JS array of `star-count` star cells for the given shape."
  [star-count shape]
  (into-array (map (fn [i] (make-star! i shape)) (range star-count))))

(defn- make-shadow! [^js el]
  (let [root      (.attachShadow el #js {:mode "open"})
        style-el  (.createElement js/document "style")
        base      (.createElement js/document "div")
        label     (.createElement js/document "span")
        stars     (.createElement js/document "div")
        shape     (model/normalize-shape (du/get-attr el model/attr-shape))
        star-count (model/normalize-max (du/get-attr el model/attr-max))
        star-els  (build-star-els star-count shape)]
    (set! (.-textContent style-el) style-text)
    (du/set-attr! base "part" "base")
    (du/set-attr! label "part" "label-text")
    (du/set-attr! stars "part" "stars")
    (du/set-attr! stars "role" "slider")
    (du/set-attr! stars "tabindex" "0")
    (du/set-attr! stars "aria-valuemin" "0")
    (doseq [^js s star-els] (.appendChild stars s))
    (.appendChild base label)
    (.appendChild base stars)
    (.appendChild root style-el)
    (.appendChild root base)
    (let [refs #js {:base     base
                    :label    label
                    :stars    stars
                    :star-els star-els
                    :shape    shape
                    :count    star-count}]
      (du/setv! el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Read element state from attributes
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/normalize
   {:value            (du/get-attr el model/attr-value)
    :max              (du/get-attr el model/attr-max)
    :precision        (du/get-attr el model/attr-precision)
    :shape            (du/get-attr el model/attr-shape)
    :allow-clear      (du/has-attr? el model/attr-allow-clear)
    :disabled         (du/has-attr? el model/attr-disabled)
    :readonly         (du/has-attr? el model/attr-readonly)
    :name             (du/get-attr el model/attr-name)
    :label            (du/get-attr el model/attr-label)
    :size             (du/get-attr el model/attr-size)
    :aria-label       (du/get-attr el model/attr-aria-label)
    :aria-labelledby  (du/get-attr el model/attr-aria-labelledby)
    :aria-describedby (du/get-attr el model/attr-aria-describedby)}))

;; ---------------------------------------------------------------------------
;; Hover-preview teardown
;; ---------------------------------------------------------------------------
(defn- forget-preview!
  "Drop hover-preview state — the cached preview value and the `data-preview`
   marker — without repainting. Callers either paint the committed fill
   themselves or leave the already-painted committed fill on screen. Resetting
   the cached value is essential: `on-stars-pointermove!` skips a repaint when
   the new candidate equals the cached one, so a stale value would suppress
   the next preview."
  [^js el ^js refs]
  (du/setv! el k-preview nil)
  (du/remove-attr! (gobj/get refs "stars") "data-preview"))

;; ---------------------------------------------------------------------------
;; Render — DOM patching (render-orchestrator: phase list of named helpers)
;; ---------------------------------------------------------------------------
(defn- sync-stars!
  "Rebuild the star cells only when :max or :shape changed since the last
   render. The stars container itself is reused, so its event listeners
   survive the rebuild. A rebuild drops any in-flight hover preview — the old
   star cells (and their preview paint) are gone."
  [^js el ^js refs {:keys [max shape]}]
  (when (or (not= (gobj/get refs "count") max)
            (not= (gobj/get refs "shape") shape))
    (let [^js stars (gobj/get refs "stars")
          star-els  (build-star-els max shape)]
      (set! (.-textContent stars) "")
      (doseq [^js s star-els] (.appendChild stars s))
      (gobj/set refs "star-els" star-els)
      (gobj/set refs "count" max)
      (gobj/set refs "shape" shape)
      (forget-preview! el refs))))

(defn- apply-host-data! [^js el ^js base-el {:keys [size disabled? readonly?]}]
  (du/set-attr! base-el "data-size" size)
  (du/set-bool-attr! el "data-disabled" disabled?)
  (du/set-bool-attr! el "data-readonly" readonly?))

(defn- apply-label! [^js label-el {:keys [label]}]
  (set! (.-textContent label-el) (or label ""))
  (set! (.-display (.-style label-el)) (if (some? label) "" "none")))

(defn- fill-percent
  "CSS clip width for a star fill state."
  [fill-state]
  (case fill-state
    :full "100%"
    :half "50%"
    "0%"))

(defn- paint-stars!
  "Project a per-star fill-state vector onto the star cells."
  [^js refs fill-states]
  (let [^js star-els (gobj/get refs "star-els")]
    (dotimes [i (.-length star-els)]
      (let [^js star (aget star-els i)
            state    (nth fill-states i :empty)]
        (.setProperty (.-style star) "--_x-rating-star-fill" (fill-percent state))
        (du/set-attr! star "data-fill" (name state))))))

(defn- apply-stars-fill! [^js refs {:keys [fill-states]}]
  (paint-stars! refs fill-states))

(defn- apply-stars-aria!
  [^js stars-el {:keys [value max value-text disabled? readonly?
                        label aria-label aria-labelledby aria-describedby]}]
  (du/set-attr! stars-el "aria-valuemax"  (str max))
  (du/set-attr! stars-el "aria-valuenow"  (str value))
  (du/set-attr! stars-el "aria-valuetext" value-text)
  (du/set-attr! stars-el "aria-readonly"  (if readonly? "true" "false"))
  (if disabled?
    (do (du/set-attr! stars-el "aria-disabled" "true")
        (du/set-attr! stars-el "tabindex" "-1"))
    (do (du/remove-attr! stars-el "aria-disabled")
        (du/set-attr! stars-el "tabindex" "0")))
  (if aria-labelledby
    (do (du/set-attr!    stars-el "aria-labelledby" aria-labelledby)
        (du/remove-attr! stars-el "aria-label"))
    (do (du/set-attr!    stars-el "aria-label" (or aria-label label "Rating"))
        (du/remove-attr! stars-el "aria-labelledby")))
  (if aria-describedby
    (du/set-attr!    stars-el "aria-describedby" aria-describedby)
    (du/remove-attr! stars-el "aria-describedby")))

(defn- apply-form-value! [^js el {:keys [value]}]
  (when-let [^js internals (du/getv el k-internals)]
    (.setFormValue internals (str value))))

(defn- apply-model! [^js el ^js refs m]
  (let [^js base-el  (gobj/get refs "base")
        ^js label-el (gobj/get refs "label")
        ^js stars-el (gobj/get refs "stars")]
    (sync-stars!       el refs m)
    (apply-host-data!  el base-el m)
    (apply-label!      label-el m)
    (apply-stars-fill! refs m)
    (apply-stars-aria! stars-el m)
    (apply-form-value! el m)
    (du/setv! el k-model m)))

;; render! is the direct-write entry — form-disabled!/form-reset! mutate
;; attributes synchronously and want the apply to run unconditionally.
(defn- render! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (apply-model! el refs (read-model el))))

(defn- update-from-attrs! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [new-m (read-model el)
          old-m (du/getv el k-model)]
      (when (not= new-m old-m)
        (apply-model! el refs new-m)))))

;; ---------------------------------------------------------------------------
;; Hover preview — transient DOM only, never written to the value attribute
;; ---------------------------------------------------------------------------
(defn- apply-preview! [^js refs preview-value {:keys [max]}]
  (du/set-attr! (gobj/get refs "stars") "data-preview" "")
  (paint-stars! refs (model/fill-states preview-value max)))

;; ---------------------------------------------------------------------------
;; Value commit — shared by pointer click and keyboard
;; ---------------------------------------------------------------------------
(defn- commit-value!
  "Snap, clamp and (if accepted) write a proposed value. The attribute write
   re-enters the change-guarded render pipeline, so the DOM follows the
   attribute — the attribute is the single source of truth."
  [^js el raw-val]
  (let [m       (read-model el)
        snapped (model/snap-to-precision raw-val (:precision-step m))
        new-val (model/clamp-value snapped (:max m))
        prev    (:value m)]
    (when (not= new-val prev)
      (when (du/dispatch-cancelable!
             el model/event-change-request
             (clj->js (model/make-change-request-detail new-val prev (:max m))))
        (du/set-attr! el model/attr-value (str new-val))
        (let [m2 (read-model el)]
          (du/dispatch! el model/event-change
                        (clj->js (model/make-detail (:value m2) (:max m2)))))))))

;; ---------------------------------------------------------------------------
;; Pointer interaction
;; ---------------------------------------------------------------------------
(defn- pointer->candidate
  "The discrete rating candidate for a pointer event over the stars row."
  [^js refs ^js evt {:keys [max precision-step]}]
  (let [^js rect (.getBoundingClientRect (gobj/get refs "stars"))]
    (model/pointer->value (.-clientX evt) (.-left rect) (.-width rect)
                          max precision-step)))

(defn- on-stars-pointermove! [^js el ^js evt]
  (let [m (du/getv el k-model)]
    (when (and m (model/interactable? m))
      (let [refs (du/getv el k-refs)
            cand (pointer->candidate refs evt m)]
        (when (not= cand (du/getv el k-preview))
          (du/setv! el k-preview cand)
          (apply-preview! refs cand m)
          (du/dispatch! el model/event-hover
                        (clj->js (model/make-detail cand (:max m)))))))))

(defn- on-stars-pointerleave! [^js el ^js _evt]
  (when (some? (du/getv el k-preview))
    (let [refs (du/getv el k-refs)
          m    (du/getv el k-model)]
      (forget-preview! el refs)
      (paint-stars! refs (:fill-states m))
      (du/dispatch! el model/event-hover
                    (clj->js (model/make-detail nil (:max m)))))))

(defn- on-stars-pointerdown! [^js el ^js evt]
  (let [m (read-model el)]
    (when (model/interactable? m)
      (.preventDefault evt)
      (let [refs   (du/getv el k-refs)
            cand   (pointer->candidate refs evt m)
            target (if (and (:allow-clear? m) (= cand (:value m))) 0 cand)]
        (.focus (gobj/get refs "stars"))
        (commit-value! el target)
        ;; The committed fill is now on screen — discard the preview overlay
        ;; so a stale cached value cannot suppress the next hover repaint.
        (forget-preview! el refs)))))

;; ---------------------------------------------------------------------------
;; Keyboard interaction
;; ---------------------------------------------------------------------------
(defn- on-stars-keydown! [^js el ^js evt]
  (let [key (.-key evt)]
    (when (contains? value-changing-keys key)
      ;; Always consume the key — in readonly mode this blocks the change
      ;; without scrolling the page; otherwise it drives the value.
      (.preventDefault evt)
      (let [m (read-model el)]
        (when (model/interactable? m)
          (commit-value! el (model/key-target key (:value m) (:max m)
                                               (:precision-step m)
                                               (:allow-clear? m))))))))

;; ---------------------------------------------------------------------------
;; Listener management (listener-spec)
;; ---------------------------------------------------------------------------
(defn- build-all-handlers
  "Per-instance closure map; keys map 1:1 onto `listener-spec`."
  [^js el]
  #js {:stars-move  (fn [e] (on-stars-pointermove!  el e))
       :stars-down  (fn [e] (on-stars-pointerdown!  el e))
       :stars-leave (fn [e] (on-stars-pointerleave! el e))
       :stars-key   (fn [e] (on-stars-keydown!      el e))})

(def ^:private listener-spec
  "Single source of truth for listener wiring: [refs-key event handler-key
   capture?]. add-listeners! and remove-listeners! both iterate it so they
   cannot drift. Every listener binds to the stable `stars` container, so a
   star-cell rebuild (sync-stars!) needs no re-wiring."
  [["stars" "pointermove"  "stars-move"  false]
   ["stars" "pointerdown"  "stars-down"  false]
   ["stars" "pointerleave" "stars-leave" false]
   ["stars" "keydown"      "stars-key"   false]])

(defn- add-listeners! [^js el]
  (when-let [refs (du/getv el k-refs)]
    (let [handlers (build-all-handlers el)]
      (doseq [[refs-key event handler-key capture?] listener-spec]
        (.addEventListener (gobj/get refs refs-key)
                           event
                           (gobj/get handlers handler-key)
                           capture?))
      (du/setv! el k-handlers handlers))))

(defn- remove-listeners! [^js el]
  (let [refs     (du/getv el k-refs)
        handlers (du/getv el k-handlers)]
    (when (and refs handlers)
      (doseq [[refs-key event handler-key capture?] listener-spec]
        (.removeEventListener (gobj/get refs refs-key)
                              event
                              (gobj/get handlers handler-key)
                              capture?))
      (du/setv! el k-handlers nil))))

;; ---------------------------------------------------------------------------
;; Form-associated callbacks
;; ---------------------------------------------------------------------------
(defn- form-disabled! [^js el disabled?]
  (du/set-bool-attr! el model/attr-disabled (boolean disabled?))
  (render! el))

(defn- form-reset! [^js el]
  (du/remove-attr! el model/attr-value)
  (render! el))

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (when-not (du/getv el k-refs)
    (make-shadow! el)
    (when (.-attachInternals el)
      (du/setv! el k-internals (.attachInternals el))))
  (remove-listeners! el)
  (add-listeners! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (remove-listeners! el)
  ;; Drop any in-flight hover preview so it cannot survive a reconnect.
  (when-let [refs (du/getv el k-refs)]
    (forget-preview! el refs)))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes  model/observed-attributes
     :connected-fn         connected!
     :disconnected-fn      disconnected!
     :attribute-changed-fn attribute-changed!
     :form-associated?     true
     :form-disabled-fn     form-disabled!
     :form-reset-fn        form-reset!
     :setup-prototype-fn   install-property-accessors!}))
