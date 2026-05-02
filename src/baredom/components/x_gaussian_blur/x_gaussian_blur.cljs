(ns baredom.components.x-gaussian-blur.x-gaussian-blur
  (:require [baredom.utils.component :as component]
            [goog.object :as gobj]
            [baredom.components.x-gaussian-blur.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-initialized "__xGaussianBlurInit")
(def ^:private k-backdrop    "__xGaussianBlurBackdrop")
(def ^:private k-blobs       "__xGaussianBlurBlobs")
(def ^:private k-slot        "__xGaussianBlurSlot")
(def ^:private k-slotchange  "__xGaussianBlurSlotHandler")

;; ── Styles ────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "position:relative;"
   "width:100%;height:100%;"
   "overflow:hidden;"
   "box-sizing:border-box;"
   "color-scheme:light dark;"
   "--x-gaussian-blur-bg:transparent;"
   "--x-gaussian-blur-z-index:0;"
   "--x-gaussian-blur-border-radius:0;}"

   ":host{border-radius:var(--x-gaussian-blur-border-radius);}"

   "[part=backdrop]{"
   "position:absolute;inset:0;"
   "overflow:hidden;"
   "pointer-events:none;"
   "z-index:var(--x-gaussian-blur-z-index);"
   "background:var(--x-gaussian-blur-bg);}"

   "[part=content]{"
   "position:relative;"
   "z-index:1;"
   "width:100%;height:100%;}"

   ".blob{"
   "position:absolute;"
   "border-radius:50%;"
   "will-change:transform;"
   "pointer-events:none;}"

   ;; ── Float keyframes (4 variants for organic movement) ──────────────
   "@keyframes x-gb-float-0{"
   "0%,100%{transform:translate(-50%,-50%) translate(0,0);}"
   "25%{transform:translate(-50%,-50%) translate(12%,-18%);}"
   "50%{transform:translate(-50%,-50%) translate(-10%,15%);}"
   "75%{transform:translate(-50%,-50%) translate(15%,6%);}}"

   "@keyframes x-gb-float-1{"
   "0%,100%{transform:translate(-50%,-50%) translate(0,0);}"
   "25%{transform:translate(-50%,-50%) translate(-15%,10%);}"
   "50%{transform:translate(-50%,-50%) translate(18%,-12%);}"
   "75%{transform:translate(-50%,-50%) translate(-8%,-16%);}}"

   "@keyframes x-gb-float-2{"
   "0%,100%{transform:translate(-50%,-50%) translate(0,0);}"
   "25%{transform:translate(-50%,-50%) translate(10%,16%);}"
   "50%{transform:translate(-50%,-50%) translate(-18%,-8%);}"
   "75%{transform:translate(-50%,-50%) translate(14%,-12%);}}"

   "@keyframes x-gb-float-3{"
   "0%,100%{transform:translate(-50%,-50%) translate(0,0);}"
   "25%{transform:translate(-50%,-50%) translate(-12%,-10%);}"
   "50%{transform:translate(-50%,-50%) translate(16%,18%);}"
   "75%{transform:translate(-50%,-50%) translate(-10%,14%);}}"

   ;; ── Pulse keyframes (scale + drift, 4 variants) ───────────────────
   "@keyframes x-gb-pulse-0{"
   "0%,100%{transform:translate(-50%,-50%) scale(1) translate(0,0);}"
   "33%{transform:translate(-50%,-50%) scale(1.2) translate(10%,-14%);}"
   "66%{transform:translate(-50%,-50%) scale(0.85) translate(-10%,10%);}}"

   "@keyframes x-gb-pulse-1{"
   "0%,100%{transform:translate(-50%,-50%) scale(1) translate(0,0);}"
   "33%{transform:translate(-50%,-50%) scale(0.85) translate(-14%,10%);}"
   "66%{transform:translate(-50%,-50%) scale(1.2) translate(12%,-8%);}}"

   "@keyframes x-gb-pulse-2{"
   "0%,100%{transform:translate(-50%,-50%) scale(1) translate(0,0);}"
   "33%{transform:translate(-50%,-50%) scale(1.18) translate(8%,16%);}"
   "66%{transform:translate(-50%,-50%) scale(0.82) translate(-16%,-10%);}}"

   "@keyframes x-gb-pulse-3{"
   "0%,100%{transform:translate(-50%,-50%) scale(1) translate(0,0);}"
   "33%{transform:translate(-50%,-50%) scale(0.88) translate(-10%,-14%);}"
   "66%{transform:translate(-50%,-50%) scale(1.15) translate(10%,16%);}}"

   ;; ── Paused state ───────────────────────────────────────────────────
   ":host([data-paused]) .blob{animation-play-state:paused;}"

   ;; ── Reduced motion ─────────────────────────────────────────────────
   "@media (prefers-reduced-motion:reduce){"
   ".blob{animation:none!important;}}"))

;; ── DOM initialisation ────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (.createElement js/document "style")
        backdrop   (.createElement js/document "div")
        content    (.createElement js/document "div")
        slot-el    (.createElement js/document "slot")]
    (set! (.-textContent style-el) style-text)
    (.setAttribute backdrop "part" "backdrop")
    (.setAttribute backdrop "aria-hidden" "true")
    (.setAttribute content "part" "content")
    (.appendChild content slot-el)
    (.appendChild root style-el)
    (.appendChild root backdrop)
    (.appendChild root content)
    (gobj/set el k-backdrop backdrop)
    (gobj/set el k-slot     slot-el)
    (gobj/set el k-blobs    #js [])
    (gobj/set el k-initialized true))
  nil)

;; ── Read inputs ───────────────────────────────────────────────────────────
(defn- read-inputs [^js el]
  {:colors    (.getAttribute el model/attr-colors)
   :blur      (.getAttribute el model/attr-blur)
   :speed     (.getAttribute el model/attr-speed)
   :count     (.getAttribute el model/attr-count)
   :size      (.getAttribute el model/attr-size)
   :opacity   (.getAttribute el model/attr-opacity)
   :animation (.getAttribute el model/attr-animation)
   :blend     (.getAttribute el model/attr-blend)
   :paused    (.getAttribute el model/attr-paused)})

;; ── Accessibility ─────────────────────────────────────────────────────────
(defn- slot-has-content? [^js slot-el]
  (when slot-el
    (pos? (.-length (.assignedNodes slot-el)))))

(defn- update-a11y! [^js el ^js slot-el]
  (if (slot-has-content? slot-el)
    (do (.removeAttribute el "role")
        (.removeAttribute el "aria-hidden"))
    (do (.setAttribute el "role" "presentation")
        (.setAttribute el "aria-hidden" "true"))))

;; ── Blob reconciliation ──────────────────────────────────────────────────

(defn- anim-name [animation-type anim-index]
  (case animation-type
    "float" (str "x-gb-float-" anim-index)
    "pulse" (str "x-gb-pulse-" anim-index)
    nil))

(defn- apply-blob-style! [^js blob-div {:keys [color x y size anim-index duration-factor delay-factor]}
                          animation speed-s _blur]
  (let [^js s (.-style blob-div)
        sz    (str size "%")]
    (.setProperty s "left"   (str x "%"))
    (.setProperty s "top"    (str y "%"))
    (.setProperty s "width"  sz)
    (.setProperty s "height" sz)
    (.setProperty s "background"
                  (str "radial-gradient(circle," color " 0%,transparent 70%)"))
    (.setProperty s "transform" "translate(-50%,-50%)")
    (if-let [name (anim-name animation anim-index)]
      (let [dur   (* speed-s duration-factor)
            delay (* speed-s delay-factor)]
        (.setProperty s "animation"
                      (str name " " dur "s ease-in-out " delay "s infinite")))
      (.removeProperty s "animation"))))

(defn- reconcile-blobs!
  "Adds/removes blob divs to match count, then updates each blob's style."
  [^js el {:keys [blobs animation speed-s blur]}]
  (let [^js backdrop (gobj/get el k-backdrop)
        ^js arr      (gobj/get el k-blobs)
        current      (.-length arr)
        target       (count blobs)]
    ;; Add missing blobs
    (loop [i current]
      (when (< i target)
        (let [div (.createElement js/document "div")]
          (set! (.-className div) "blob")
          (.appendChild backdrop div)
          (.push arr div))
        (recur (inc i))))
    ;; Remove excess blobs
    (loop [i current]
      (when (> i target)
        (let [div (.pop arr)]
          (.removeChild backdrop div))
        (recur (dec i))))
    ;; Update each blob
    (dotimes [i target]
      (apply-blob-style! (aget arr i) (nth blobs i) animation speed-s blur))))

;; ── Render ────────────────────────────────────────────────────────────────
(defn- render! [^js el]
  (let [state (model/derive-state (read-inputs el))
        {:keys [blur opacity blend animation paused]} state
        ^js backdrop (gobj/get el k-backdrop)
        ^js slot-el  (gobj/get el k-slot)]

    ;; Backdrop filter + opacity + blend
    (.setProperty (.-style backdrop) "filter" (str "blur(" blur "px)"))
    (.setProperty (.-style backdrop) "opacity" (str opacity))
    (.setProperty (.-style backdrop) "mix-blend-mode" blend)

    ;; Data attributes for CSS selectors
    (if paused
      (.setAttribute el "data-paused" "")
      (.removeAttribute el "data-paused"))

    (.setAttribute el "data-animation" animation)

    ;; Reconcile blob divs
    (reconcile-blobs! el state)

    ;; Accessibility
    (update-a11y! el slot-el))
  nil)

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (gobj/get el k-initialized)
    (init-dom! el))
  (let [^js slot-el (gobj/get el k-slot)
        handler (fn [] (update-a11y! el slot-el))]
    (gobj/set el k-slotchange handler)
    (.addEventListener slot-el "slotchange" handler))
  (render! el)
  nil)

(defn- disconnected! [^js el]
  (let [^js slot-el (gobj/get el k-slot)
        handler     (gobj/get el k-slotchange)]
    (when (and slot-el handler)
      (.removeEventListener slot-el "slotchange" handler)))
  nil)

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (when (gobj/get el k-initialized)
      (render! el)))
  nil)

;; ── Property accessors ────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  ;; String attributes: colors, blur, speed, count, size, opacity, animation, blend
  (doseq [[prop-name attr-name default-val]
          [["colors"    model/attr-colors    model/default-colors]
           ["blur"      model/attr-blur      "60"]
           ["speed"     model/attr-speed     "medium"]
           ["count"     model/attr-count     "5"]
           ["size"      model/attr-size      "medium"]
           ["opacity"   model/attr-opacity   "0.7"]
           ["animation" model/attr-animation model/default-animation]
           ["blend"     model/attr-blend     model/default-blend]]]
    (.defineProperty js/Object proto prop-name
                     #js {:get (fn []
                                (this-as ^js this
                                         (or (.getAttribute this attr-name)
                                             default-val)))
                          :set (fn [v]
                                (this-as ^js this
                                         (if (and (string? v) (not= "" v))
                                           (.setAttribute this attr-name v)
                                           (.removeAttribute this attr-name))))
                          :enumerable true :configurable true}))

  ;; Boolean attribute: paused
  (.defineProperty js/Object proto "paused"
                   #js {:get (fn []
                               (this-as ^js this
                                        (.hasAttribute this model/attr-paused)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-paused "")
                                          (.removeAttribute this model/attr-paused))))
                        :enumerable true :configurable true}))

;; ── Element class ─────────────────────────────────────────────────────────
;; ── Public API ────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
