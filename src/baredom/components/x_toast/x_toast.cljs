(ns baredom.components.x-toast.x-toast
  (:require
   [goog.object :as gobj]
   [baredom.components.x-toast.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs        "__xToastRefs")
(def ^:private k-model       "__xToastModel")
(def ^:private k-handlers    "__xToastHandlers")
(def ^:private k-timeout     "__xToastTimeout")
(def ^:private k-exit-timer  "__xToastExitTimer")
(def ^:private k-exiting     "__xToastExiting")
(def ^:private k-entered     "__xToastEntered")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-toast-radius:var(--x-radius-lg,12px);"
   "--x-toast-padding-y:14px;"
   "--x-toast-padding-x:16px;"
   "--x-toast-gap:12px;"
   "--x-toast-font-size:var(--x-font-size-sm,0.875rem);"
   "--x-toast-heading-font-size:0.9375rem;"
   "--x-toast-heading-weight:600;"
   "--x-toast-min-width:min(280px,calc(100vw - 2rem));"
   "--x-toast-max-width:min(480px,calc(100vw - 2rem));"
   "--x-toast-border-width:1px;"
   "--x-toast-shadow:0 4px 16px rgba(0,0,0,0.12),0 1px 4px rgba(0,0,0,0.08);"
   "--x-toast-motion-fast:var(--x-transition-duration,120ms);"
   "--x-toast-motion-ease:var(--x-transition-easing,cubic-bezier(0.2,0,0,1));"
   "--x-toast-enter-duration:200ms;"
   "--x-toast-exit-duration:180ms;"
   "--x-toast-press-scale:0.97;"
   "--x-toast-disabled-opacity:0.55;"
   "--x-toast-focus-ring:rgba(0,0,0,0.6);"
   "--x-toast-dismiss-color:rgba(0,0,0,0.50);"
   "--x-toast-dismiss-hover-bg:rgba(0,0,0,0.06);"
   "--x-toast-progress-height:3px;"
   "--x-toast-progress-bg:rgba(0,0,0,0.08);"
   "--x-toast-info-bg:var(--x-color-bg,#ffffff);"
   "--x-toast-info-border:rgba(0,102,204,0.30);"
   "--x-toast-info-color:rgba(15,23,42,0.92);"
   "--x-toast-info-icon-color:rgba(0,102,204,0.85);"
   "--x-toast-info-progress-fill:rgba(0,102,204,0.70);"
   "--x-toast-success-bg:var(--x-color-bg,#ffffff);"
   "--x-toast-success-border:rgba(16,140,72,0.30);"
   "--x-toast-success-color:rgba(15,23,42,0.92);"
   "--x-toast-success-icon-color:rgba(16,140,72,0.85);"
   "--x-toast-success-progress-fill:rgba(16,140,72,0.70);"
   "--x-toast-warning-bg:var(--x-color-bg,#ffffff);"
   "--x-toast-warning-border:rgba(204,120,0,0.40);"
   "--x-toast-warning-color:rgba(15,23,42,0.92);"
   "--x-toast-warning-icon-color:rgba(180,100,0,0.85);"
   "--x-toast-warning-progress-fill:rgba(204,120,0,0.70);"
   "--x-toast-error-bg:var(--x-color-bg,#ffffff);"
   "--x-toast-error-border:rgba(190,20,40,0.40);"
   "--x-toast-error-color:rgba(15,23,42,0.92);"
   "--x-toast-error-icon-color:rgba(190,20,40,0.85);"
   "--x-toast-error-progress-fill:rgba(190,20,40,0.70);"
   "--x-toast-bg:transparent;"
   "--x-toast-border-color:transparent;"
   "--x-toast-color:inherit;"
   "--x-toast-icon-color:inherit;"
   "--x-toast-progress-fill:currentColor;}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-toast-shadow:0 4px 20px rgba(0,0,0,0.40),0 1px 6px rgba(0,0,0,0.30);"
   "--x-toast-focus-ring:rgba(255,255,255,0.75);"
   "--x-toast-dismiss-color:rgba(255,255,255,0.65);"
   "--x-toast-dismiss-hover-bg:rgba(255,255,255,0.12);"
   "--x-toast-progress-bg:rgba(255,255,255,0.10);"
   "--x-toast-info-bg:var(--x-color-bg,#1e293b);"
   "--x-toast-info-border:rgba(80,160,255,0.35);"
   "--x-toast-info-color:rgba(248,250,252,0.92);"
   "--x-toast-info-icon-color:rgba(96,165,250,0.90);"
   "--x-toast-info-progress-fill:rgba(96,165,250,0.75);"
   "--x-toast-success-bg:var(--x-color-bg,#1e293b);"
   "--x-toast-success-border:rgba(60,210,120,0.35);"
   "--x-toast-success-color:rgba(248,250,252,0.92);"
   "--x-toast-success-icon-color:rgba(52,211,153,0.90);"
   "--x-toast-success-progress-fill:rgba(52,211,153,0.75);"
   "--x-toast-warning-bg:var(--x-color-bg,#1e293b);"
   "--x-toast-warning-border:rgba(255,190,90,0.40);"
   "--x-toast-warning-color:rgba(248,250,252,0.92);"
   "--x-toast-warning-icon-color:rgba(251,191,36,0.90);"
   "--x-toast-warning-progress-fill:rgba(251,191,36,0.75);"
   "--x-toast-error-bg:var(--x-color-bg,#1e293b);"
   "--x-toast-error-border:rgba(255,90,110,0.40);"
   "--x-toast-error-color:rgba(248,250,252,0.92);"
   "--x-toast-error-icon-color:rgba(248,113,113,0.90);"
   "--x-toast-error-progress-fill:rgba(248,113,113,0.75);}}"

   ":host([data-type='info']){"
   "--x-toast-bg:var(--x-toast-info-bg);"
   "--x-toast-border-color:var(--x-toast-info-border);"
   "--x-toast-color:var(--x-toast-info-color);"
   "--x-toast-icon-color:var(--x-toast-info-icon-color);"
   "--x-toast-progress-fill:var(--x-toast-info-progress-fill);}"

   ":host([data-type='success']){"
   "--x-toast-bg:var(--x-toast-success-bg);"
   "--x-toast-border-color:var(--x-toast-success-border);"
   "--x-toast-color:var(--x-toast-success-color);"
   "--x-toast-icon-color:var(--x-toast-success-icon-color);"
   "--x-toast-progress-fill:var(--x-toast-success-progress-fill);}"

   ":host([data-type='warning']){"
   "--x-toast-bg:var(--x-toast-warning-bg);"
   "--x-toast-border-color:var(--x-toast-warning-border);"
   "--x-toast-color:var(--x-toast-warning-color);"
   "--x-toast-icon-color:var(--x-toast-warning-icon-color);"
   "--x-toast-progress-fill:var(--x-toast-warning-progress-fill);}"

   ":host([data-type='error']){"
   "--x-toast-bg:var(--x-toast-error-bg);"
   "--x-toast-border-color:var(--x-toast-error-border);"
   "--x-toast-color:var(--x-toast-error-color);"
   "--x-toast-icon-color:var(--x-toast-error-icon-color);"
   "--x-toast-progress-fill:var(--x-toast-error-progress-fill);}"

   "[part=container]{"
   "display:flex;flex-direction:column;"
   "background:var(--x-toast-bg);"
   "border:var(--x-toast-border-width) solid var(--x-toast-border-color);"
   "border-radius:var(--x-toast-radius);"
   "box-shadow:var(--x-toast-shadow);"
   "color:var(--x-toast-color);"
   "min-width:var(--x-toast-min-width);"
   "max-width:var(--x-toast-max-width);"
   "overflow:hidden;"
   "transition:"
   "background var(--x-toast-motion-fast) var(--x-toast-motion-ease),"
   "border-color var(--x-toast-motion-fast) var(--x-toast-motion-ease),"
   "opacity var(--x-toast-motion-fast) var(--x-toast-motion-ease),"
   "transform var(--x-toast-motion-fast) var(--x-toast-motion-ease);}"

   "[part=inner]{"
   "display:flex;align-items:flex-start;gap:var(--x-toast-gap);"
   "padding:var(--x-toast-padding-y) var(--x-toast-padding-x);}"

   "[part=icon]{"
   "flex:0 0 auto;"
   "color:var(--x-toast-icon-color);"
   "line-height:1;margin-top:0.15em;}"

   "slot[name=icon]::slotted(*){display:block;}"

   "[part=body]{"
   "flex:1 1 auto;display:flex;flex-direction:column;gap:3px;min-width:0;}"

   "[part=heading]{"
   "font-size:var(--x-toast-heading-font-size);"
   "font-weight:var(--x-toast-heading-weight);"
   "line-height:1.2;overflow-wrap:anywhere;}"

   "[part=message]{"
   "font-size:var(--x-toast-font-size);"
   "line-height:1.35;overflow-wrap:anywhere;opacity:0.82;}"

   "[part=dismiss]{"
   "flex:0 0 auto;appearance:none;border:0;background:transparent;"
   "color:var(--x-toast-dismiss-color);"
   "width:1.5em;height:1.5em;padding:0;margin:0;"
   "border-radius:999px;cursor:pointer;"
   "display:inline-flex;align-items:center;justify-content:center;"
   "transition:"
   "background var(--x-toast-motion-fast) var(--x-toast-motion-ease),"
   "transform var(--x-toast-motion-fast) var(--x-toast-motion-ease);}"

   ":host(:not([disabled])) [part=dismiss]:hover{background:var(--x-toast-dismiss-hover-bg);}"
   ":host(:not([disabled])) [part=dismiss]:active{transform:scale(var(--x-toast-press-scale));}"
   "[part=dismiss]:focus{outline:none;}"
   ":host(:not([disabled])) [part=dismiss]:focus-visible{"
   "outline:2px solid var(--x-toast-focus-ring);outline-offset:2px;}"

   ":host([disabled]){opacity:var(--x-toast-disabled-opacity);}"
   ":host([disabled]) [part=dismiss]{cursor:default;pointer-events:none;}"

   "[part=progress]{"
   "height:var(--x-toast-progress-height);"
   "background:var(--x-toast-progress-bg);"
   "overflow:hidden;display:none;}"

   "[part=progress-bar]{"
   "height:100%;width:100%;"
   "background:var(--x-toast-progress-fill);"
   "animation:x-toast-progress var(--x-toast-timeout,0ms) linear forwards;"
   "animation-play-state:paused;}"

   ":host([data-progress-active]) [part=progress-bar]{"
   "animation-play-state:running;}"

   "@keyframes x-toast-progress{"
   "from{width:100%;}"
   "to{width:0%;}}"

   ":host([data-entering]) [part=container]{"
   "animation:x-toast-enter var(--x-toast-enter-duration) var(--x-toast-motion-ease) both;}"

   "@keyframes x-toast-enter{"
   "from{opacity:0;transform:translateY(-8px);}"
   "to{opacity:1;transform:translateY(0);}}"

   ":host([data-exiting]) [part=container]{"
   "animation:x-toast-exit var(--x-toast-exit-duration) var(--x-toast-motion-ease) forwards;}"

   "@keyframes x-toast-exit{"
   "from{opacity:1;transform:translateY(0);}"
   "to{opacity:0;transform:translateY(-8px);}}"

   "@media (prefers-reduced-motion:reduce){"
   "[part=container],[part=dismiss]{transition:none !important;}"
   ":host([data-entering]) [part=container],:host([data-exiting]) [part=container]{"
   "animation:none !important;}"
   ":host([data-progress-active]) [part=progress-bar]{"
   "animation:none !important;}}"))

;; ── DOM initialisation ───────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root         (.attachShadow el #js {:mode "open"})
        style        (.createElement js/document "style")
        container    (.createElement js/document "div")
        inner        (.createElement js/document "div")
        icon-wrap    (.createElement js/document "span")
        icon-slot    (.createElement js/document "slot")
        default-icon (.createElement js/document "span")
        body-el      (.createElement js/document "div")
        heading-el   (.createElement js/document "span")
        message-el   (.createElement js/document "span")
        dismiss-btn  (.createElement js/document "button")
        dismiss-x    (.createElement js/document "span")
        progress-el  (.createElement js/document "div")
        progress-bar (.createElement js/document "div")]

    (set! (.-textContent style) style-text)

    (.setAttribute container "part" "container")

    (.setAttribute inner "part" "inner")

    (.setAttribute icon-wrap "part" "icon")
    (.setAttribute icon-wrap "aria-hidden" "true")
    (.setAttribute icon-slot "name" "icon")
    (.setAttribute default-icon "part" "default-icon")
    (.appendChild icon-slot default-icon)
    (.appendChild icon-wrap icon-slot)

    (.setAttribute body-el "part" "body")
    (.setAttribute heading-el "part" "heading")
    (.setAttribute message-el "part" "message")
    (.appendChild body-el heading-el)
    (.appendChild body-el message-el)

    (.setAttribute dismiss-btn "part" "dismiss")
    (.setAttribute dismiss-btn "type" "button")
    (.setAttribute dismiss-btn "aria-label" "Dismiss toast")
    (.setAttribute dismiss-x "aria-hidden" "true")
    (set! (.-textContent dismiss-x) "×")
    (.appendChild dismiss-btn dismiss-x)

    (.appendChild inner icon-wrap)
    (.appendChild inner body-el)
    (.appendChild inner dismiss-btn)

    (.setAttribute progress-el "part" "progress")
    (.setAttribute progress-bar "part" "progress-bar")
    (.appendChild progress-el progress-bar)

    (.appendChild container inner)
    (.appendChild container progress-el)
    (.appendChild root style)
    (.appendChild root container)

    (gobj/set el k-refs
              {:root         root
               :container    container
               :inner        inner
               :icon-wrap    icon-wrap
               :icon-slot    icon-slot
               :default-icon default-icon
               :body-el      body-el
               :heading-el   heading-el
               :message-el   message-el
               :dismiss-btn  dismiss-btn
               :progress-el  progress-el
               :progress-bar progress-bar}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:type-raw           (.getAttribute el model/attr-type)
    :heading            (.getAttribute el model/attr-heading)
    :message            (.getAttribute el model/attr-message)
    :icon-present?      (.hasAttribute el model/attr-icon)
    :icon-raw           (.getAttribute el model/attr-icon)
    :dismissible-attr   (.getAttribute el model/attr-dismissible)
    :disabled-present?  (.hasAttribute el model/attr-disabled)
    :timeout-ms-raw     (.getAttribute el model/attr-timeout-ms)
    :show-progress-attr (.getAttribute el model/attr-show-progress)}))

;; ── DOM patching ─────────────────────────────────────────────────────────────
(defn- slot-has-content? [^js slot-el]
  (when slot-el
    (pos? (.-length (.assignedNodes slot-el)))))

(defn- set-host-a11y! [^js el dismissible? disabled?]
  (let [interactive? (and dismissible? (not disabled?))]
    (set! (.-tabIndex el) (if interactive? 0 -1))
    (if disabled?
      (.setAttribute el "aria-disabled" "true")
      (.removeAttribute el "aria-disabled"))
    (if interactive?
      (.setAttribute el "aria-keyshortcuts" "Escape")
      (.removeAttribute el "aria-keyshortcuts"))))

(defn- apply-model! [^js el {:keys [type heading message icon-mode icon
                                     dismissible? disabled? timeout-ms] :as m}]
  (let [{:keys [container icon-wrap icon-slot default-icon
                heading-el message-el dismiss-btn progress-el progress-bar]}
        (ensure-refs! el)
        ^js container    container
        ^js icon-wrap    icon-wrap
        ^js icon-slot    icon-slot
        ^js default-icon default-icon
        ^js heading-el   heading-el
        ^js message-el   message-el
        ^js dismiss-btn  dismiss-btn
        ^js progress-el  progress-el
        ^js progress-bar progress-bar
        has-slot?    (slot-has-content? icon-slot)
        fallback     (if (= icon-mode :custom) icon (model/default-icon-for-type type))
        hide-icon?   (and (not has-slot?) (= icon-mode :hidden))
        show-prog?   (model/progress-eligible? m)]

    (.setAttribute el "data-type" (model/type->attr type))
    (.setAttribute container "role" (model/role-for-type type))

    (set! (.-textContent heading-el) heading)
    (set! (.. heading-el -style -display) (if (= heading "") "none" ""))

    (set! (.-textContent message-el) message)

    (if hide-icon?
      (do (set! (.-textContent default-icon) "")
          (set! (.. icon-wrap -style -display) "none"))
      (do (set! (.-textContent default-icon) fallback)
          (set! (.. icon-wrap -style -display) "inline")))

    (if dismissible?
      (do (set! (.-disabled dismiss-btn) (boolean disabled?))
          (set! (.. dismiss-btn -style -display) "inline-flex"))
      (do (set! (.-disabled dismiss-btn) true)
          (set! (.. dismiss-btn -style -display) "none")))

    (if show-prog?
      (do (set! (.. progress-el -style -display) "block")
          (.setProperty (.-style progress-bar) "--x-toast-timeout" (str timeout-ms "ms")))
      (set! (.. progress-el -style -display) "none"))

    (set-host-a11y! el dismissible? disabled?)
    (gobj/set el k-model m))
  nil)

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m)))
  nil)

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

(defn- exit-duration-ms [^js el]
  (let [^js cs (.getComputedStyle js/window el)
        v1 (parse-duration-ms (.getPropertyValue cs "--x-toast-exit-duration"))
        v2 (parse-duration-ms (.getPropertyValue cs "--x-motion-exit-duration"))]
    (cond (pos? v1) v1 (pos? v2) v2 :else 180)))

;; ── Timer management ─────────────────────────────────────────────────────────
(defn- clear-timeout! [^js el]
  (when-let [tid (gobj/get el k-timeout)]
    (js/clearTimeout tid)
    (gobj/set el k-timeout nil))
  nil)

(declare start-exit-and-remove!)

(defn- schedule-timeout! [^js el]
  (clear-timeout! el)
  (let [m (or (gobj/get el k-model) (read-model el))]
    (when (and (model/dismiss-eligible? m)
               (number? (:timeout-ms m))
               (not (gobj/get el k-exiting)))
      (gobj/set el k-timeout
                (js/setTimeout
                 (fn []
                   (gobj/set el k-timeout nil)
                   (when (and (.-isConnected el) (not (gobj/get el k-exiting)))
                     (let [m2 (or (gobj/get el k-model) (read-model el))]
                       (when (model/dismiss-eligible? m2)
                         (let [detail (clj->js (model/dismiss-detail m2 "timeout"))
                               ^js ev (js/CustomEvent.
                                       model/event-dismiss
                                       #js {:detail     detail
                                            :bubbles    true
                                            :composed   true
                                            :cancelable true})
                               ok? (.dispatchEvent el ev)]
                           (when ok? (start-exit-and-remove! el)))))))
                 (:timeout-ms m)))))
  nil)

;; ── Animation ────────────────────────────────────────────────────────────────
(defn- after-enter! [^js el]
  (let [m (gobj/get el k-model)]
    (when (and m (model/progress-eligible? m))
      (.setAttribute el "data-progress-active" "")))
  (schedule-timeout! el))

(defn- start-enter! [^js el]
  (when-not (gobj/get el k-entered)
    (gobj/set el k-entered true)
    (.setAttribute el "data-entering" "")
    (if (prefers-reduced-motion?)
      (do (.removeAttribute el "data-entering")
          (after-enter! el))
      (let [{:keys [container]} (ensure-refs! el)
            ^js container container]
        (letfn [(on-end [^js e]
                  (when (= (.-target e) container)
                    (.removeEventListener container "animationend" on-end)
                    (.removeAttribute el "data-entering")
                    (after-enter! el)))]
          (.addEventListener container "animationend" on-end)))))
  nil)

(defn- start-exit-and-remove! [^js el]
  (when-not (gobj/get el k-exiting)
    (gobj/set el k-exiting true)
    (clear-timeout! el)
    (.removeAttribute el "data-entering")
    (.removeAttribute el "data-progress-active")
    (let [dur (exit-duration-ms el)]
      (if (or (zero? dur) (prefers-reduced-motion?))
        (.remove el)
        (let [{:keys [container]} (ensure-refs! el)
              ^js container container]
          (letfn [(on-end [^js e]
                    (when (= (.-target e) container)
                      (.removeEventListener container "animationend" on-end)
                      (when-let [tid (gobj/get el k-exit-timer)]
                        (js/clearTimeout tid)
                        (gobj/set el k-exit-timer nil))
                      (when (.-isConnected el) (.remove el))))]
            (.setAttribute el "data-exiting" "")
            (.addEventListener container "animationend" on-end)
            (gobj/set el k-exit-timer
                      (js/setTimeout
                       (fn []
                         (when (and (.-isConnected el) (gobj/get el k-exiting))
                           (.removeEventListener container "animationend" on-end)
                           (gobj/set el k-exit-timer nil)
                           (.remove el)))
                       (+ dur 60))))))))
  nil)

;; ── Event dispatch ───────────────────────────────────────────────────────────
(defn- dispatch-dismiss! [^js el reason]
  (let [m      (or (gobj/get el k-model) (read-model el))
        detail (clj->js (model/dismiss-detail m reason))
        ^js ev (js/CustomEvent.
                model/event-dismiss
                #js {:detail     detail
                     :bubbles    true
                     :composed   true
                     :cancelable true})
        ok?    (.dispatchEvent el ev)]
    (when ok? (start-exit-and-remove! el))
    (boolean ok?)))

;; ── Event handlers ───────────────────────────────────────────────────────────
(defn- on-dismiss-click [^js el ^js e]
  (let [m (or (gobj/get el k-model) (read-model el))]
    (when (and (model/dismiss-eligible? m) (not (gobj/get el k-exiting)))
      (.preventDefault e)
      (dispatch-dismiss! el "button")))
  nil)

(defn- on-keydown [^js el ^js e]
  (when (= (.-key e) "Escape")
    (let [m (or (gobj/get el k-model) (read-model el))]
      (when (and (model/dismiss-eligible? m) (not (gobj/get el k-exiting)))
        (.preventDefault e)
        (dispatch-dismiss! el "keyboard"))))
  nil)

;; ── Listener management ──────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [{:keys [dismiss-btn]} (ensure-refs! el)
        ^js dismiss-btn dismiss-btn
        click-h (fn [e] (on-dismiss-click el e))
        key-h   (fn [e] (on-keydown el e))]
    (.addEventListener dismiss-btn "click" click-h)
    (.addEventListener el "keydown" key-h)
    (gobj/set el k-handlers #js {:click click-h :keydown key-h}))
  nil)

(defn- remove-listeners! [^js el]
  (clear-timeout! el)
  (when-let [tid (gobj/get el k-exit-timer)]
    (js/clearTimeout tid)
    (gobj/set el k-exit-timer nil))
  (let [hs   (gobj/get el k-handlers)
        refs (gobj/get el k-refs)]
    (when (and hs refs)
      (let [^js btn    (:dismiss-btn refs)
            click-h (gobj/get hs "click")
            key-h   (gobj/get hs "keydown")]
        (when click-h (.removeEventListener btn "click" click-h))
        (when key-h   (.removeEventListener el "keydown" key-h)))))
  (gobj/set el k-handlers nil)
  nil)

;; ── Property accessors ───────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (.defineProperty js/Object proto model/attr-type
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-type) "info")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-type (str v))
                                          (.removeAttribute this model/attr-type))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-heading
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-heading) "")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-heading (str v))
                                          (.removeAttribute this model/attr-heading))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-message
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-message) "")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-message (str v))
                                          (.removeAttribute this model/attr-message))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-icon
                   #js {:get (fn []
                               (this-as ^js this (.getAttribute this model/attr-icon)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-icon (str v))
                                          (.removeAttribute this model/attr-icon))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-dismissible
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-bool-default-true
                                         (.getAttribute this model/attr-dismissible))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-dismissible "")
                                          (.setAttribute this model/attr-dismissible "false"))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto model/attr-disabled
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this model/attr-disabled)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-disabled "")
                                          (.removeAttribute this model/attr-disabled))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto "timeoutMs"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-timeout-ms
                                         (.getAttribute this model/attr-timeout-ms))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-timeout-ms)
                                          (.setAttribute this model/attr-timeout-ms (str (int v))))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto "showProgress"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-bool-default-false
                                         (.getAttribute this model/attr-show-progress))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-show-progress "")
                                          (.removeAttribute this model/attr-show-progress))))
                        :enumerable true :configurable true}))

;; ── Dismiss method ───────────────────────────────────────────────────────────
(defn- install-dismiss-method! [^js proto]
  (set! (.-dismiss proto)
        (fn [reason]
          (this-as ^js this
                   (let [r (if (string? reason) reason "api")]
                     (when (and (not (gobj/get this k-exiting))
                                (.-isConnected this))
                       (dispatch-dismiss! this r)))
                   nil))))

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
                     (start-enter! this)
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
                       (update-from-attrs! this)
                       (when (.-isConnected this)
                         (schedule-timeout! this)))
                     nil)))

    (install-property-accessors! (.-prototype klass))
    (install-dismiss-method! (.-prototype klass))
    klass))

;; ── Public API ───────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
