(ns baredom.components.x-alert.x-alert
  (:require
   [goog.object :as gobj]
   [baredom.components.x-alert.model :as model]))

;; ── Instance-field keys (gobj/get, gobj/set) ────────────────────────────────
(def ^:private k-refs        "__xAlertRefs")
(def ^:private k-model       "__xAlertModel")
(def ^:private k-handlers    "__xAlertHandlers")
(def ^:private k-timeout     "__xAlertTimeout")
(def ^:private k-exit-timer  "__xAlertExitTimer")
(def ^:private k-exiting     "__xAlertExiting")
(def ^:private k-entered     "__xAlertEntered")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-alert-radius:10px;"
   "--x-alert-padding-y:10px;"
   "--x-alert-padding-x:12px;"
   "--x-alert-gap:10px;"
   "--x-alert-font-size:0.875rem;"
   "--x-alert-motion-fast:120ms;"
   "--x-alert-motion-ease:cubic-bezier(0.2,0,0,1);"
   "--x-alert-enter-duration:140ms;"
   "--x-alert-exit-duration:160ms;"
   "--x-alert-press-scale:0.98;"
   "--x-alert-disabled-opacity:0.55;"
   "--x-alert-focus-ring:rgba(0,0,0,0.6);"
   "--x-alert-dismiss-color:rgba(0,0,0,0.62);"
   "--x-alert-dismiss-hover-bg:rgba(0,0,0,0.06);"
   "--x-alert-info-bg:rgba(0,102,204,0.08);"
   "--x-alert-info-border:rgba(0,102,204,0.35);"
   "--x-alert-info-color:rgba(0,60,120,0.95);"
   "--x-alert-success-bg:rgba(16,140,72,0.10);"
   "--x-alert-success-border:rgba(16,140,72,0.35);"
   "--x-alert-success-color:rgba(10,90,46,0.95);"
   "--x-alert-warning-bg:rgba(204,120,0,0.12);"
   "--x-alert-warning-border:rgba(204,120,0,0.45);"
   "--x-alert-warning-color:rgba(120,70,0,0.95);"
   "--x-alert-error-bg:rgba(190,20,40,0.10);"
   "--x-alert-error-border:rgba(190,20,40,0.45);"
   "--x-alert-error-color:rgba(120,10,20,0.95);"
   "--x-alert-bg:transparent;"
   "--x-alert-border-color:transparent;"
   "--x-alert-color:inherit;}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-alert-focus-ring:rgba(255,255,255,0.75);"
   "--x-alert-dismiss-color:rgba(255,255,255,0.75);"
   "--x-alert-dismiss-hover-bg:rgba(255,255,255,0.14);"
   "--x-alert-info-bg:rgba(80,160,255,0.18);"
   "--x-alert-info-border:rgba(80,160,255,0.42);"
   "--x-alert-info-color:rgba(230,245,255,0.92);"
   "--x-alert-success-bg:rgba(60,210,120,0.18);"
   "--x-alert-success-border:rgba(60,210,120,0.42);"
   "--x-alert-success-color:rgba(235,255,245,0.92);"
   "--x-alert-warning-bg:rgba(255,190,90,0.20);"
   "--x-alert-warning-border:rgba(255,190,90,0.46);"
   "--x-alert-warning-color:rgba(255,248,235,0.92);"
   "--x-alert-error-bg:rgba(255,90,110,0.18);"
   "--x-alert-error-border:rgba(255,90,110,0.46);"
   "--x-alert-error-color:rgba(255,235,238,0.92);}}"

   ":host([data-type='info']){"
   "--x-alert-bg:var(--x-alert-info-bg);"
   "--x-alert-border-color:var(--x-alert-info-border);"
   "--x-alert-color:var(--x-alert-info-color);}"

   ":host([data-type='success']){"
   "--x-alert-bg:var(--x-alert-success-bg);"
   "--x-alert-border-color:var(--x-alert-success-border);"
   "--x-alert-color:var(--x-alert-success-color);}"

   ":host([data-type='warning']){"
   "--x-alert-bg:var(--x-alert-warning-bg);"
   "--x-alert-border-color:var(--x-alert-warning-border);"
   "--x-alert-color:var(--x-alert-warning-color);}"

   ":host([data-type='error']){"
   "--x-alert-bg:var(--x-alert-error-bg);"
   "--x-alert-border-color:var(--x-alert-error-border);"
   "--x-alert-color:var(--x-alert-error-color);}"

   "[part=container]{"
   "display:flex;align-items:flex-start;gap:var(--x-alert-gap);"
   "padding:var(--x-alert-padding-y) var(--x-alert-padding-x);"
   "border:1px solid var(--x-alert-border-color);"
   "border-radius:var(--x-alert-radius);"
   "background:var(--x-alert-bg);"
   "color:var(--x-alert-color);"
   "font-size:var(--x-alert-font-size);"
   "transition:"
   "background var(--x-alert-motion-fast) var(--x-alert-motion-ease),"
   "border-color var(--x-alert-motion-fast) var(--x-alert-motion-ease),"
   "color var(--x-alert-motion-fast) var(--x-alert-motion-ease),"
   "opacity var(--x-alert-motion-fast) var(--x-alert-motion-ease),"
   "transform var(--x-alert-motion-fast) var(--x-alert-motion-ease);}"

   "[part=icon]{flex:0 0 auto;line-height:1;margin-top:0.05em;}"

   "slot[name=icon]::slotted(*){display:block;}"

   "[part=text]{flex:1 1 auto;line-height:1.25;min-width:0;overflow-wrap:anywhere;}"

   "[part=dismiss]{"
   "flex:0 0 auto;appearance:none;border:0;background:transparent;"
   "color:var(--x-alert-dismiss-color);"
   "width:1.5em;height:1.5em;padding:0;margin:0;"
   "border-radius:999px;cursor:pointer;"
   "display:inline-flex;align-items:center;justify-content:center;"
   "transition:"
   "background var(--x-alert-motion-fast) var(--x-alert-motion-ease),"
   "transform var(--x-alert-motion-fast) var(--x-alert-motion-ease);}"

   ":host(:not([disabled])) [part=dismiss]:hover{background:var(--x-alert-dismiss-hover-bg);}"
   ":host(:not([disabled])) [part=dismiss]:active{transform:scale(var(--x-alert-press-scale));}"
   "[part=dismiss]:focus{outline:none;}"
   ":host(:not([disabled])) [part=dismiss]:focus-visible{"
   "outline:2px solid var(--x-alert-focus-ring);outline-offset:2px;}"

   ":host([disabled]){opacity:var(--x-alert-disabled-opacity);}"
   ":host([disabled]) [part=dismiss]{cursor:default;pointer-events:none;}"

   ":host([data-entering]) [part=container]{"
   "animation:x-alert-enter var(--x-alert-enter-duration) var(--x-alert-motion-ease) both;}"

   "@keyframes x-alert-enter{"
   "from{opacity:0;transform:translateY(4px);}"
   "to{opacity:1;transform:translateY(0);}}"

   ":host([data-exiting]) [part=container]{"
   "animation:x-alert-exit var(--x-alert-exit-duration) var(--x-alert-motion-ease) forwards;}"

   "@keyframes x-alert-exit{"
   "from{opacity:1;transform:translateY(0);}"
   "to{opacity:0;transform:translateY(4px);}}"

   "@media (prefers-reduced-motion:reduce){"
   "[part=container],[part=dismiss]{transition:none !important;}"
   ":host([data-entering]) [part=container],:host([data-exiting]) [part=container]{"
   "animation:none !important;}}"))

;; ── DOM initialisation ───────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root        (.attachShadow el #js {:mode "open"})
        style       (.createElement js/document "style")
        container   (.createElement js/document "div")
        icon-wrap   (.createElement js/document "span")
        icon-slot   (.createElement js/document "slot")
        default-icon (.createElement js/document "span")
        text-el     (.createElement js/document "span")
        dismiss-btn (.createElement js/document "button")
        dismiss-x   (.createElement js/document "span")]

    (set! (.-textContent style) style-text)

    (.setAttribute container "part" "container")

    (.setAttribute icon-wrap "part" "icon")
    (.setAttribute icon-wrap "aria-hidden" "true")
    (.setAttribute icon-slot "name" "icon")
    (.setAttribute default-icon "part" "default-icon")
    (.appendChild icon-slot default-icon)
    (.appendChild icon-wrap icon-slot)

    (.setAttribute text-el "part" "text")

    (.setAttribute dismiss-btn "part" "dismiss")
    (.setAttribute dismiss-btn "type" "button")
    (.setAttribute dismiss-x "aria-hidden" "true")
    (set! (.-textContent dismiss-x) "×")
    (.appendChild dismiss-btn dismiss-x)

    (.appendChild container icon-wrap)
    (.appendChild container text-el)
    (.appendChild container dismiss-btn)
    (.appendChild root style)
    (.appendChild root container)

    (gobj/set el k-refs
              {:root        root
               :container   container
               :icon-wrap   icon-wrap
               :icon-slot   icon-slot
               :default-icon default-icon
               :text-el     text-el
               :dismiss-btn dismiss-btn}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:type-raw          (.getAttribute el model/attr-type)
    :text              (.getAttribute el model/attr-text)
    :icon-present?     (.hasAttribute el model/attr-icon)
    :icon-raw          (.getAttribute el model/attr-icon)
    :dismissible-attr  (.getAttribute el model/attr-dismissible)
    :disabled-present? (.hasAttribute el model/attr-disabled)
    :timeout-ms-raw    (.getAttribute el model/attr-timeout-ms)}))

;; ── DOM patching ─────────────────────────────────────────────────────────────
(defn- slot-has-content? [^js slot-el]
  ;; Use assignedNodes WITHOUT flatten:true — flatten includes fallback content
  ;; (the default-icon span), which would make this always return true.
  ;; Without flatten, only externally slotted nodes are counted.
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

(defn- apply-model! [^js el {:keys [type text icon-mode icon dismissible? disabled?] :as m}]
  (let [{:keys [container icon-wrap icon-slot default-icon text-el dismiss-btn]}
        (ensure-refs! el)
        ^js container   container
        ^js icon-wrap   icon-wrap
        ^js icon-slot   icon-slot
        ^js default-icon default-icon
        ^js text-el     text-el
        ^js dismiss-btn dismiss-btn
        has-slot?    (slot-has-content? icon-slot)
        fallback     (if (= icon-mode :custom) icon (model/default-icon-for-type type))
        hide-icon?   (and (not has-slot?) (= icon-mode :hidden))]

    (.setAttribute el "data-type" (model/type->attr type))
    (.setAttribute container "role" (model/role-for-type type))

    (set! (.-textContent text-el) text)

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

    (.setAttribute dismiss-btn "aria-label" "Dismiss alert")
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
        v1 (parse-duration-ms (.getPropertyValue cs "--x-alert-exit-duration"))
        v2 (parse-duration-ms (.getPropertyValue cs "--x-motion-exit-duration"))]
    (cond (pos? v1) v1 (pos? v2) v2 :else 160)))

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
                                       #js {:detail    detail
                                            :bubbles   true
                                            :composed  true
                                            :cancelable true})
                               ok? (.dispatchEvent el ev)]
                           (when ok? (start-exit-and-remove! el)))))))
                 (:timeout-ms m)))))
  nil)

;; ── Animation ────────────────────────────────────────────────────────────────
(defn- start-enter! [^js el]
  (when-not (gobj/get el k-entered)
    (gobj/set el k-entered true)
    (.setAttribute el "data-entering" "")
    (let [{:keys [container]} (ensure-refs! el)
          ^js container container]
      (letfn [(on-end [^js e]
                (when (= (.-target e) container)
                  (.removeEventListener container "animationend" on-end)
                  (.removeAttribute el "data-entering")))]
        (.addEventListener container "animationend" on-end))))
  nil)

(defn- start-exit-and-remove! [^js el]
  (when-not (gobj/get el k-exiting)
    (gobj/set el k-exiting true)
    (clear-timeout! el)
    (.removeAttribute el "data-entering")
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
                #js {:detail    detail
                     :bubbles   true
                     :composed  true
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
      (let [^js btn     (:dismiss-btn refs)
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

  (.defineProperty js/Object proto model/attr-text
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-text) "")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-text (str v))
                                          (.removeAttribute this model/attr-text))))
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

  (.defineProperty js/Object proto model/attr-disabled
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this model/attr-disabled)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-disabled "")
                                          (.removeAttribute this model/attr-disabled))))
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

  ;; camelCase JS property mapping to kebab-case attribute
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
                     (start-enter! this)
                     (schedule-timeout! this)
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
    klass))

;; ── Public API ───────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
