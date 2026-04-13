(ns baredom.components.x-splash.x-splash
  (:require
   [goog.object :as gobj]
   [baredom.components.x-splash.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs       "__xSplashRefs")
(def ^:private k-model      "__xSplashModel")
(def ^:private k-fade-timer "__xSplashFadeTimer")
(def ^:private k-fading-out "__xSplashFadingOut")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-splash-bg:var(--x-color-bg, #ffffff);"
   "--x-splash-color:var(--x-color-text, #0f172a);"
   "--x-splash-z-index:9999;"
   "--x-splash-fade-duration:var(--x-transition-duration, 400ms);"
   "--x-splash-fade-ease:cubic-bezier(0.4,0,0.2,1);"
   "--x-splash-spinner-size:40px;"
   "--x-splash-spinner-color:var(--x-color-text, currentColor);"
   "--x-splash-spinner-track-color:var(--x-color-border, rgba(0,0,0,0.12));"
   "--x-splash-spinner-thickness:3px;"
   "--x-splash-spinner-duration:0.75s;"
   "--x-splash-progress-height:4px;"
   "--x-splash-progress-color:var(--x-color-primary, #3b82f6);"
   "--x-splash-progress-track-color:var(--x-color-border, rgba(0,0,0,0.08));"
   "--x-splash-progress-radius:var(--x-radius-sm, 2px);"
   "--x-splash-gap:20px;"
   "--x-splash-blur-amount:8px;}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-splash-bg:var(--x-color-bg, #0f172a);"
   "--x-splash-color:var(--x-color-text, #f8fafc);"
   "--x-splash-spinner-track-color:var(--x-color-border, rgba(255,255,255,0.15));"
   "--x-splash-progress-track-color:var(--x-color-border, rgba(255,255,255,0.12));}}"

   "[part=overlay]{"
   "position:fixed;inset:0;"
   "z-index:var(--x-splash-z-index);"
   "display:none;"
   "align-items:center;justify-content:center;"
   "background:var(--x-splash-bg);"
   "color:var(--x-splash-color);"
   "opacity:0;"
   "transition:opacity var(--x-splash-fade-duration) var(--x-splash-fade-ease);}"

   ":host([active]) [part=overlay]{"
   "display:flex;opacity:1;}"

   ":host([data-fading-out]) [part=overlay]{"
   "display:flex;opacity:0;}"

   ;; Variant: minimal — transparent background
   ":host([data-variant='minimal']) [part=overlay]{"
   "background:transparent;}"

   ;; Variant: branded — allows override via custom property
   ":host([data-variant='branded']) [part=overlay]{"
   "background:var(--x-splash-branded-bg,var(--x-splash-bg));}"

   ;; Overlay: blur
   ":host([data-overlay='blur']) [part=overlay]{"
   "background:rgba(255,255,255,0.7);"
   "backdrop-filter:blur(var(--x-splash-blur-amount));"
   "-webkit-backdrop-filter:blur(var(--x-splash-blur-amount));}"

   "@media (prefers-color-scheme:dark){"
   ":host([data-overlay='blur']) [part=overlay]{"
   "background:rgba(15,23,42,0.7);}}"

   ;; Overlay: transparent
   ":host([data-overlay='transparent']) [part=overlay]{"
   "background:transparent;}"

   "[part=content]{"
   "display:flex;flex-direction:column;align-items:center;"
   "gap:var(--x-splash-gap);"
   "max-width:80vw;text-align:center;}"

   ;; Spinner
   "[part=spinner]{"
   "display:block;"
   "width:var(--x-splash-spinner-size);"
   "height:var(--x-splash-spinner-size);"
   "border:var(--x-splash-spinner-thickness) solid var(--x-splash-spinner-track-color);"
   "border-top-color:var(--x-splash-spinner-color);"
   "border-radius:50%;"
   "animation:x-splash-spin var(--x-splash-spinner-duration) linear infinite;"
   "box-sizing:border-box;}"

   "@keyframes x-splash-spin{to{transform:rotate(360deg);}}"

   ;; Progress bar
   "[part=progress]{"
   "display:none;"
   "width:min(300px,60vw);"
   "height:var(--x-splash-progress-height);"
   "background:var(--x-splash-progress-track-color);"
   "border-radius:var(--x-splash-progress-radius);"
   "overflow:hidden;}"

   "[part=bar]{"
   "height:100%;"
   "background:var(--x-splash-progress-color);"
   "border-radius:var(--x-splash-progress-radius);"
   "transition:width 200ms ease;"
   "width:0%;}"

   ;; Reduced motion
   "@media (prefers-reduced-motion:reduce){"
   "[part=overlay]{transition:none !important;}"
   "[part=spinner]{animation:none !important;}"
   "[part=bar]{transition:none !important;}}"))

;; ── DOM initialisation ───────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root        (.attachShadow el #js {:mode "open"})
        style       (.createElement js/document "style")
        overlay-el  (.createElement js/document "div")
        content-el  (.createElement js/document "div")
        slot-el     (.createElement js/document "slot")
        spinner-el  (.createElement js/document "span")
        progress-el (.createElement js/document "div")
        bar-el      (.createElement js/document "div")]

    (set! (.-textContent style) style-text)

    (.setAttribute overlay-el "part" "overlay")

    (.setAttribute content-el "part" "content")

    (.setAttribute spinner-el "part" "spinner")
    (.setAttribute spinner-el "aria-hidden" "true")

    (.setAttribute progress-el "part" "progress")
    (.setAttribute progress-el "role" "progressbar")
    (.setAttribute progress-el "aria-valuemin" "0")
    (.setAttribute progress-el "aria-valuemax" "100")

    (.setAttribute bar-el "part" "bar")

    (.appendChild progress-el bar-el)
    (.appendChild content-el slot-el)
    (.appendChild content-el spinner-el)
    (.appendChild content-el progress-el)
    (.appendChild overlay-el content-el)
    (.appendChild root style)
    (.appendChild root overlay-el)

    (gobj/set el k-refs
              {:root        root
               :overlay-el  overlay-el
               :content-el  content-el
               :spinner-el  spinner-el
               :progress-el progress-el
               :bar-el      bar-el}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/derive-state
   {:active-present? (.hasAttribute el model/attr-active)
    :variant-raw     (.getAttribute el model/attr-variant)
    :progress-raw    (.getAttribute el model/attr-progress)
    :spinner-attr    (.getAttribute el model/attr-spinner)
    :overlay-raw     (.getAttribute el model/attr-overlay)}))

;; ── Motion helpers ───────────────────────────────────────────────────────────
(defn- prefers-reduced-motion? []
  (boolean (.-matches (.matchMedia js/window "(prefers-reduced-motion:reduce)"))))

(defn- parse-duration-ms [s]
  (let [s (if (string? s) (.trim s) "")]
    (cond
      (or (= s "") (= s "0") (= s "0ms") (= s "0s")) 0
      (.endsWith s "ms")
      (let [n (js/parseFloat (.slice s 0 (- (.-length s) 2)))]
        (if (js/isNaN n) 0 (js/Math.max 0 (js/Math.round n))))
      (.endsWith s "s")
      (let [n (js/parseFloat (.slice s 0 (- (.-length s) 1)))]
        (if (js/isNaN n) 0 (js/Math.max 0 (js/Math.round (* 1000 n)))))
      :else 0)))

(defn- fade-duration-ms [^js el]
  (let [^js cs (.getComputedStyle js/window el)
        v (parse-duration-ms (.getPropertyValue cs "--x-splash-fade-duration"))]
    (if (pos? v) v 400)))

;; ── Fade-out logic ───────────────────────────────────────────────────────────
(defn- clear-fade-timer! [^js el]
  (when-let [tid (gobj/get el k-fade-timer)]
    (js/clearTimeout tid)
    (gobj/set el k-fade-timer nil))
  nil)

(defn- fire-hidden-event! [^js el]
  (let [^js ev (js/CustomEvent.
                model/event-hidden
                #js {:detail   #js {}
                     :bubbles  true
                     :composed true
                     :cancelable false})]
    (.dispatchEvent el ev))
  nil)

(defn- finish-fade-out! [^js el]
  (clear-fade-timer! el)
  (gobj/set el k-fading-out false)
  (.removeAttribute el "data-fading-out")
  (let [{:keys [overlay-el]} (ensure-refs! el)
        ^js overlay-el overlay-el]
    (set! (.. overlay-el -style -display) "none"))
  (.removeAttribute el "aria-busy")
  (fire-hidden-event! el)
  nil)

(defn- start-fade-out! [^js el]
  (when-not (gobj/get el k-fading-out)
    (gobj/set el k-fading-out true)
    (if (prefers-reduced-motion?)
      (finish-fade-out! el)
      (let [{:keys [overlay-el]} (ensure-refs! el)
            ^js overlay-el overlay-el
            on-end (fn on-end [^js e]
                     (when (and (= (.-target e) overlay-el)
                                (= (.-propertyName e) "opacity"))
                       (.removeEventListener overlay-el "transitionend" on-end)
                       (when (gobj/get el k-fading-out)
                         (finish-fade-out! el))))]
        ;; Keep overlay visible at opacity 1 via inline styles, then
        ;; transition to opacity 0 on the next frame.
        (set! (.. overlay-el -style -display) "flex")
        (set! (.. overlay-el -style -opacity) "1")
        (.setAttribute el "data-fading-out" "")
        (.addEventListener overlay-el "transitionend" on-end)
        ;; Trigger transition on next frame so the browser sees the change
        (js/requestAnimationFrame
         (fn []
           (js/requestAnimationFrame
            (fn []
              (when (gobj/get el k-fading-out)
                (set! (.. overlay-el -style -opacity) "0"))))))
        ;; Safety timeout in case transitionend never fires
        (gobj/set el k-fade-timer
                  (js/setTimeout
                   (fn []
                     (when (gobj/get el k-fading-out)
                       (.removeEventListener overlay-el "transitionend" on-end)
                       (finish-fade-out! el)))
                   (+ (fade-duration-ms el) 60))))))
  nil)

(defn- cancel-fade-out! [^js el]
  (when (gobj/get el k-fading-out)
    (clear-fade-timer! el)
    (gobj/set el k-fading-out false)
    (.removeAttribute el "data-fading-out")
    (let [{:keys [overlay-el]} (ensure-refs! el)
          ^js overlay-el overlay-el]
      (set! (.. overlay-el -style -display) "")
      (set! (.. overlay-el -style -opacity) "")))
  nil)

;; ── DOM patching ─────────────────────────────────────────────────────────────
(defn- apply-model! [^js el {:keys [active? variant progress spinner? overlay] :as m}]
  (let [{:keys [overlay-el spinner-el progress-el bar-el]}
        (ensure-refs! el)
        ^js overlay-el  overlay-el
        ^js spinner-el  spinner-el
        ^js progress-el progress-el
        ^js bar-el      bar-el
        old-m           (gobj/get el k-model)
        was-active?     (:active? old-m)]

    ;; Data attributes for CSS
    (.setAttribute el "data-variant" variant)
    (.setAttribute el "data-overlay" overlay)

    ;; Spinner visibility
    (set! (.. spinner-el -style -display)
          (if spinner? "block" "none"))

    ;; Progress bar
    (if (some? progress)
      (do (set! (.. progress-el -style -display) "block")
          (set! (.. bar-el -style -width) (str progress "%"))
          (.setAttribute progress-el "aria-valuenow" (str progress)))
      (do (set! (.. progress-el -style -display) "none")
          (.removeAttribute progress-el "aria-valuenow")))

    ;; Active / fade handling
    (cond
      ;; Becoming active
      (and active? (not was-active?))
      (do (cancel-fade-out! el)
          (.setAttribute el "aria-busy" "true")
          (.setAttribute el "aria-live" "polite")
          (.setAttribute el "role" "status")
          ;; Clear inline overrides so CSS rules take effect
          (set! (.. overlay-el -style -display) "")
          (set! (.. overlay-el -style -opacity) ""))

      ;; Becoming inactive (was active or first render with inactive)
      (and (not active?) was-active?)
      (start-fade-out! el)

      ;; First render, not active — ensure hidden
      (and (not active?) (nil? old-m))
      (do (set! (.. overlay-el -style -display) "none")
          (.removeAttribute el "aria-busy")))

    ;; Set aria-label default if none provided
    (when-not (.hasAttribute el "aria-label")
      (.setAttribute el "aria-label" "Loading"))

    (gobj/set el k-model m))
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m)))
  nil)

;; ── Listener management ──────────────────────────────────────────────────────
(defn- add-listeners! [^js _el]
  ;; No interactive listeners needed — splash screen is purely attribute-driven
  nil)

(defn- remove-listeners! [^js el]
  (clear-fade-timer! el)
  nil)

;; ── Property accessors ───────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (.defineProperty js/Object proto model/attr-active
                   #js {:get (fn []
                               (this-as ^js this
                                        (.hasAttribute this model/attr-active)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-active "")
                                          (.removeAttribute this model/attr-active))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-variant
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-variant) "default")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-variant (str v))
                                          (.removeAttribute this model/attr-variant))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-progress
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-progress
                                         (.getAttribute this model/attr-progress))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-progress)
                                          (.setAttribute this model/attr-progress (str v)))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-spinner
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-bool-default-true
                                         (.getAttribute this model/attr-spinner))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-spinner "")
                                          (.setAttribute this model/attr-spinner "false"))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-overlay
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-overlay) "solid")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-overlay (str v))
                                          (.removeAttribute this model/attr-overlay))))
                        :enumerable true :configurable true}))

;; ── Element class ────────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (ensure-refs! this)
                     (remove-listeners! this)
                     (add-listeners! this)
                     (update-from-attrs! this)
                     nil)))

    (set! (.-disconnectedCallback (.-prototype klass))
          (fn []
            (this-as ^js this
                     (remove-listeners! this)
                     nil)))

    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [_name old-val new-val]
            (this-as ^js this
                     (when (not= old-val new-val)
                       (update-from-attrs! this))
                     nil)))

    (install-property-accessors! (.-prototype klass))
    klass))

;; ── Public API ───────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
