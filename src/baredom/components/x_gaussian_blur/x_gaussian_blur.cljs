(ns baredom.components.x-gaussian-blur.x-gaussian-blur
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baredom.components.x-gaussian-blur.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────
(def ^:private k-initialized "__xGaussianBlurInit")
(def ^:private k-backdrop    "__xGaussianBlurBackdrop")
(def ^:private k-blobs       "__xGaussianBlurBlobs")
(def ^:private k-slot        "__xGaussianBlurSlot")
(def ^:private k-slotchange  "__xGaussianBlurSlotHandler")
(def ^:private k-model       "__xGaussianBlurModel")

;; ── String-literal constants ──────────────────────────────────────────────
(def ^:private attr-part           "part")
(def ^:private attr-role           "role")
(def ^:private attr-aria-hidden    "aria-hidden")
(def ^:private attr-data-paused    "data-paused")
(def ^:private attr-data-animation "data-animation")
(def ^:private part-backdrop "backdrop")
(def ^:private part-content  "content")
(def ^:private cls-blob      "blob")
(def ^:private role-presentation "presentation")
(def ^:private val-true      "true")
(def ^:private ev-slotchange "slotchange")

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
    (du/set-attr! backdrop attr-part        part-backdrop)
    (du/set-attr! backdrop attr-aria-hidden val-true)
    (du/set-attr! content  attr-part        part-content)
    (.appendChild content slot-el)
    (.appendChild root style-el)
    (.appendChild root backdrop)
    (.appendChild root content)
    (du/setv! el k-backdrop backdrop)
    (du/setv! el k-slot     slot-el)
    (du/setv! el k-blobs    #js [])
    (du/setv! el k-initialized true)))

;; ── Read inputs ───────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:colors    (du/get-attr el model/attr-colors)
    :blur      (du/get-attr el model/attr-blur)
    :speed     (du/get-attr el model/attr-speed)
    :count     (du/get-attr el model/attr-count)
    :size      (du/get-attr el model/attr-size)
    :opacity   (du/get-attr el model/attr-opacity)
    :animation (du/get-attr el model/attr-animation)
    :blend     (du/get-attr el model/attr-blend)
    :paused    (du/get-attr el model/attr-paused)}))

;; ── Accessibility ─────────────────────────────────────────────────────────
(defn- slot-has-content? [^js slot-el]
  (when slot-el
    (pos? (.-length (.assignedNodes slot-el)))))

(defn- update-a11y! [^js el ^js slot-el]
  (if (slot-has-content? slot-el)
    (do (du/remove-attr! el attr-role)
        (du/remove-attr! el attr-aria-hidden))
    (do (du/set-attr! el attr-role        role-presentation)
        (du/set-attr! el attr-aria-hidden val-true))))

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
  (let [^js backdrop (du/getv el k-backdrop)
        ^js arr      (du/getv el k-blobs)
        current      (.-length arr)
        target       (count blobs)]
    ;; Add missing blobs
    (loop [i current]
      (when (< i target)
        (let [div (.createElement js/document "div")]
          (set! (.-className div) cls-blob)
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

;; ── DOM patching (render-orchestrator: phase list of named helpers) ─────
(defn- apply-backdrop-style! [^js backdrop {:keys [blur opacity blend]}]
  (let [^js s (.-style backdrop)]
    (.setProperty s "filter"         (str "blur(" blur "px)"))
    (.setProperty s "opacity"        (str opacity))
    (.setProperty s "mix-blend-mode" blend)))

(defn- apply-host-data! [^js el {:keys [paused animation]}]
  (if paused
    (du/set-attr! el attr-data-paused "")
    (du/remove-attr! el attr-data-paused))
  (du/set-attr! el attr-data-animation animation))

(defn- apply-model! [^js el m]
  (let [^js backdrop (du/getv el k-backdrop)
        ^js slot-el  (du/getv el k-slot)]
    (apply-backdrop-style! backdrop m)
    (apply-host-data!      el m)
    (reconcile-blobs!      el m)
    (update-a11y!          el slot-el)
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ── Lifecycle ─────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (when-not (du/getv el k-initialized)
    (init-dom! el))
  (let [^js slot-el (du/getv el k-slot)
        handler     (fn handle-slotchange [] (update-a11y! el slot-el))]
    (du/setv! el k-slotchange handler)
    (.addEventListener slot-el ev-slotchange handler))
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (let [^js slot-el (du/getv el k-slot)
        handler     (du/getv el k-slotchange)]
    (when (and slot-el handler)
      (.removeEventListener slot-el ev-slotchange handler))))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (when (du/getv el k-initialized)
      (update-from-attrs! el))))

;; ── Property accessors ────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

;; ── Public API ────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
