(ns baredom.components.x-alert.x-alert
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.x-alert.model :as model]))

;; ── Instance-field keys ─────────────────────────────────────────────────────
(def ^:private k-refs       "__xAlertRefs")
(def ^:private k-model      "__xAlertModel")
(def ^:private k-handlers   "__xAlertHandlers")
(def ^:private k-timeout    "__xAlertTimeout")
(def ^:private k-exit-timer "__xAlertExitTimer")
(def ^:private k-exiting    "__xAlertExiting")
(def ^:private k-entered    "__xAlertEntered")

;; ── String-literal constants ────────────────────────────────────────────────
(def ^:private attr-part              "part")
(def ^:private attr-name              "name")
(def ^:private attr-type              "type")
(def ^:private attr-role              "role")
(def ^:private attr-aria-hidden       "aria-hidden")
(def ^:private attr-aria-label        "aria-label")
(def ^:private attr-aria-disabled     "aria-disabled")
(def ^:private attr-aria-keyshortcuts "aria-keyshortcuts")
(def ^:private attr-data-type         "data-type")
(def ^:private attr-data-entering     "data-entering")
(def ^:private attr-data-exiting      "data-exiting")

(def ^:private part-container    "container")
(def ^:private part-icon         "icon")
(def ^:private part-default-icon "default-icon")
(def ^:private part-text         "text")
(def ^:private part-dismiss      "dismiss")
(def ^:private slot-name-icon    "icon")

(def ^:private val-true             "true")
(def ^:private val-false            "false")
(def ^:private val-dismiss-label    "Dismiss alert")
(def ^:private val-dismiss-glyph    "×")
(def ^:private val-escape-shortcut  "Escape")
(def ^:private val-button-type      "button")

(def ^:private css-var-exit-duration   "--x-alert-exit-duration")
(def ^:private css-var-motion-exit-dur "--x-motion-exit-duration")

(def ^:private ev-click        "click")
(def ^:private ev-keydown      "keydown")
(def ^:private ev-animationend "animationend")

(def ^:private hk-click   "click")
(def ^:private hk-keydown "keydown")

(def ^:private reason-button   "button")
(def ^:private reason-keyboard "keyboard")
(def ^:private reason-timeout  "timeout")

(def ^:private key-escape "Escape")

;; ── Styles ──────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-alert-radius:var(--x-radius-md,10px);"
   "--x-alert-padding-y:10px;"
   "--x-alert-padding-x:12px;"
   "--x-alert-gap:10px;"
   "--x-alert-font-size:var(--x-font-size-sm,0.875rem);"
   "--x-alert-motion-fast:var(--x-transition-duration,120ms);"
   "--x-alert-motion-ease:var(--x-transition-easing,cubic-bezier(0.2,0,0,1));"
   "--x-alert-enter-duration:140ms;"
   "--x-alert-exit-duration:160ms;"
   "--x-alert-press-scale:0.98;"
   "--x-alert-disabled-opacity:0.55;"
   "--x-alert-focus-ring:var(--x-color-focus-ring,rgba(0,0,0,0.6));"
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
   "--x-alert-focus-ring:var(--x-color-focus-ring,rgba(255,255,255,0.75));"
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

;; ── DOM initialisation ──────────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root         (.attachShadow el #js {:mode "open"})
        style        (.createElement js/document "style")
        container    (.createElement js/document "div")
        icon-wrap    (.createElement js/document "span")
        icon-slot    (.createElement js/document "slot")
        default-icon (.createElement js/document "span")
        text-el      (.createElement js/document "span")
        dismiss-btn  (.createElement js/document "button")
        dismiss-x    (.createElement js/document "span")
        refs         {:root         root
                      :container    container
                      :icon-wrap    icon-wrap
                      :icon-slot    icon-slot
                      :default-icon default-icon
                      :text-el      text-el
                      :dismiss-btn  dismiss-btn}]

    (set! (.-textContent style) style-text)

    (.setAttribute container attr-part part-container)

    (.setAttribute icon-wrap    attr-part        part-icon)
    (.setAttribute icon-wrap    attr-aria-hidden val-true)
    (.setAttribute icon-slot    attr-name        slot-name-icon)
    (.setAttribute default-icon attr-part        part-default-icon)
    (.appendChild icon-slot default-icon)
    (.appendChild icon-wrap icon-slot)

    (.setAttribute text-el attr-part part-text)

    (.setAttribute dismiss-btn attr-part part-dismiss)
    (.setAttribute dismiss-btn attr-type val-button-type)
    (.setAttribute dismiss-x   attr-aria-hidden val-true)
    (set! (.-textContent dismiss-x) val-dismiss-glyph)
    (.appendChild dismiss-btn dismiss-x)

    (.appendChild container icon-wrap)
    (.appendChild container text-el)
    (.appendChild container dismiss-btn)
    (.appendChild root style)
    (.appendChild root container)

    (du/setv! el k-refs refs)
    refs))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs) (init-dom! el)))

;; ── Attribute readers ───────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:type-raw          (du/get-attr el model/attr-type)
    :text              (du/get-attr el model/attr-text)
    :icon-present?     (du/has-attr? el model/attr-icon)
    :icon-raw          (du/get-attr el model/attr-icon)
    :dismissible-attr  (du/get-attr el model/attr-dismissible)
    :disabled-present? (du/has-attr? el model/attr-disabled)
    :timeout-ms-raw    (du/get-attr el model/attr-timeout-ms)}))

;; ── Slot probing ────────────────────────────────────────────────────────────
(defn- slot-has-content?
  "Returns true when the slot has externally assigned nodes.
  Deliberately omits `:flatten true` — flatten would include the fallback
  default-icon span and always return true."
  [^js slot-el]
  (when slot-el
    (pos? (.-length (.assignedNodes slot-el)))))

;; ── DOM patching (render-orchestrator: phase list of named helpers) ─────────
(defn- apply-host-type! [^js el ^js container {:keys [type]}]
  (du/set-attr! el attr-data-type (model/type->attr type))
  (.setAttribute container attr-role (model/role-for-type type)))

(defn- apply-text! [^js text-el {:keys [text]}]
  (set! (.-textContent text-el) text))

(defn- apply-icon! [^js icon-wrap ^js icon-slot ^js default-icon
                    {:keys [type icon-mode icon]}]
  (let [has-slot?  (slot-has-content? icon-slot)
        fallback   (if (= icon-mode :custom) icon (model/default-icon-for-type type))
        hide-icon? (and (not has-slot?) (= icon-mode :hidden))]
    (if hide-icon?
      (do (set! (.-textContent default-icon) "")
          (set! (.. icon-wrap -style -display) "none"))
      (do (set! (.-textContent default-icon) fallback)
          (set! (.. icon-wrap -style -display) "inline")))))

(defn- apply-dismiss-button! [^js dismiss-btn {:keys [dismissible? disabled?]}]
  (if dismissible?
    (do (set! (.-disabled dismiss-btn) (boolean disabled?))
        (set! (.. dismiss-btn -style -display) "inline-flex"))
    (do (set! (.-disabled dismiss-btn) true)
        (set! (.. dismiss-btn -style -display) "none")))
  (.setAttribute dismiss-btn attr-aria-label val-dismiss-label))

(defn- apply-host-a11y! [^js el {:keys [dismissible? disabled?]}]
  (let [interactive? (and dismissible? (not disabled?))]
    (set! (.-tabIndex el) (if interactive? 0 -1))
    (if disabled?
      (du/set-attr! el attr-aria-disabled val-true)
      (du/remove-attr! el attr-aria-disabled))
    (if interactive?
      (du/set-attr! el attr-aria-keyshortcuts val-escape-shortcut)
      (du/remove-attr! el attr-aria-keyshortcuts))))

(defn- apply-model! [^js el m]
  (let [{:keys [container icon-wrap icon-slot default-icon text-el dismiss-btn]}
        (ensure-refs! el)
        ^js container    container
        ^js icon-wrap    icon-wrap
        ^js icon-slot    icon-slot
        ^js default-icon default-icon
        ^js text-el      text-el
        ^js dismiss-btn  dismiss-btn]
    (apply-host-type!      el container m)
    (apply-text!           text-el m)
    (apply-icon!           icon-wrap icon-slot default-icon m)
    (apply-dismiss-button! dismiss-btn m)
    (apply-host-a11y!      el m)
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= old-m new-m)
      (apply-model! el new-m))))

;; ── Motion helpers ──────────────────────────────────────────────────────────
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
        v1 (parse-duration-ms (.getPropertyValue cs css-var-exit-duration))
        v2 (parse-duration-ms (.getPropertyValue cs css-var-motion-exit-dur))]
    (cond (pos? v1) v1 (pos? v2) v2 :else 160)))

;; ── Timer management ────────────────────────────────────────────────────────
(defn- clear-timeout! [^js el]
  (when-let [tid (du/getv el k-timeout)]
    (js/clearTimeout tid)
    (du/setv! el k-timeout nil)))

(declare start-exit-and-remove!)

(defn- schedule-timeout! [^js el]
  (clear-timeout! el)
  (let [m (or (du/getv el k-model) (read-model el))]
    (when (and (model/dismiss-eligible? m)
               (number? (:timeout-ms m))
               (not (du/getv el k-exiting)))
      (du/setv! el k-timeout
                (js/setTimeout
                 (fn on-timeout-fire []
                   (du/setv! el k-timeout nil)
                   (when (and (.-isConnected el) (not (du/getv el k-exiting)))
                     (let [m2 (or (du/getv el k-model) (read-model el))]
                       (when (model/dismiss-eligible? m2)
                         (let [detail (clj->js (model/dismiss-detail m2 reason-timeout))
                               ok?    (du/dispatch-cancelable! el model/event-dismiss detail)]
                           (when ok? (start-exit-and-remove! el)))))))
                 (:timeout-ms m))))))

;; ── Animation ───────────────────────────────────────────────────────────────
(defn- start-enter! [^js el]
  (when-not (du/getv el k-entered)
    (du/setv! el k-entered true)
    (du/set-attr! el attr-data-entering "")
    (let [{:keys [container]} (ensure-refs! el)
          ^js container container]
      (letfn [(on-end [^js e]
                (when (= (.-target e) container)
                  (.removeEventListener container ev-animationend on-end)
                  (du/remove-attr! el attr-data-entering)))]
        (.addEventListener container ev-animationend on-end)))))

(defn- start-exit-and-remove! [^js el]
  (when-not (du/getv el k-exiting)
    (du/setv! el k-exiting true)
    (clear-timeout! el)
    (du/remove-attr! el attr-data-entering)
    (let [dur (exit-duration-ms el)]
      (if (or (zero? dur) (prefers-reduced-motion?))
        (.remove el)
        (let [{:keys [container]} (ensure-refs! el)
              ^js container container]
          (letfn [(on-end [^js e]
                    (when (= (.-target e) container)
                      (.removeEventListener container ev-animationend on-end)
                      (when-let [tid (du/getv el k-exit-timer)]
                        (js/clearTimeout tid)
                        (du/setv! el k-exit-timer nil))
                      (when (.-isConnected el) (.remove el))))]
            (du/set-attr! el attr-data-exiting "")
            (.addEventListener container ev-animationend on-end)
            (du/setv! el k-exit-timer
                      (js/setTimeout
                       (fn on-exit-fallback []
                         (when (and (.-isConnected el) (du/getv el k-exiting))
                           (.removeEventListener container ev-animationend on-end)
                           (du/setv! el k-exit-timer nil)
                           (.remove el)))
                       (+ dur 60)))))))))

;; ── Event dispatch ──────────────────────────────────────────────────────────
(defn- dispatch-dismiss! [^js el reason]
  (let [m      (or (du/getv el k-model) (read-model el))
        detail (clj->js (model/dismiss-detail m reason))
        ok?    (du/dispatch-cancelable! el model/event-dismiss detail)]
    (when ok? (start-exit-and-remove! el))
    ok?))

;; ── Event handlers ──────────────────────────────────────────────────────────
(defn- on-dismiss-click [^js el ^js e]
  (let [m (or (du/getv el k-model) (read-model el))]
    (when (and (model/dismiss-eligible? m) (not (du/getv el k-exiting)))
      (.preventDefault e)
      (dispatch-dismiss! el reason-button))))

(defn- on-keydown [^js el ^js e]
  (when (= (.-key e) key-escape)
    (let [m (or (du/getv el k-model) (read-model el))]
      (when (and (model/dismiss-eligible? m) (not (du/getv el k-exiting)))
        (.preventDefault e)
        (dispatch-dismiss! el reason-keyboard)))))

;; ── Listener management ─────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [{:keys [dismiss-btn]} (ensure-refs! el)
        ^js dismiss-btn dismiss-btn
        click-h  (fn handle-dismiss-click [e] (on-dismiss-click el e))
        key-h    (fn handle-host-keydown  [e] (on-keydown el e))
        handlers #js {}]
    (.addEventListener dismiss-btn ev-click   click-h)
    (.addEventListener el          ev-keydown key-h)
    (gobj/set handlers hk-click   click-h)
    (gobj/set handlers hk-keydown key-h)
    (du/setv! el k-handlers handlers)))

(defn- remove-listeners! [^js el]
  (clear-timeout! el)
  (when-let [tid (du/getv el k-exit-timer)]
    (js/clearTimeout tid)
    (du/setv! el k-exit-timer nil))
  (let [hs   (du/getv el k-handlers)
        refs (du/getv el k-refs)]
    (when (and hs refs)
      (let [^js btn (:dismiss-btn refs)
            click-h (gobj/get hs hk-click)
            key-h   (gobj/get hs hk-keydown)]
        (when click-h (.removeEventListener btn ev-click   click-h))
        (when key-h   (.removeEventListener el  ev-keydown key-h)))))
  (du/setv! el k-handlers nil))

;; ── Property accessors ──────────────────────────────────────────────────────
;; Four simple reflectors are Tier 1 (du/define-{string,bool}-prop!).
;; Two properties need Tier 2 hand-written .defineProperty — documented below.
(defn- install-property-accessors! [^js proto]
  (du/define-string-prop! proto model/attr-type model/attr-type "info")
  (du/define-string-prop! proto model/attr-text model/attr-text "")
  (du/define-string-prop! proto model/attr-icon model/attr-icon)
  (du/define-bool-prop!   proto model/attr-disabled model/attr-disabled)

  ;; Tier 2 — `dismissible` is a 3-state boolean with `true` as the *absent*
  ;; default. Tier-1 du/define-bool-prop! treats attribute-absence as `false`,
  ;; which would invert the default. We need:
  ;;   - getter: route through model/parse-bool-default-true so the absent
  ;;     attribute returns `true`, "false" returns `false`, anything else `true`.
  ;;   - setter: writing `true` sets the attribute to "" (boolean-attribute
  ;;     convention), writing `false` sets it to the literal "false" string
  ;;     (rather than removing the attribute) so the resolved value is
  ;;     explicitly false rather than the absent-default true.
  (.defineProperty js/Object proto model/attr-dismissible
                   #js {:get (fn []
                               (this-as ^js this
                                 (model/parse-bool-default-true
                                  (.getAttribute this model/attr-dismissible))))
                        :set (fn [v]
                               (this-as ^js this
                                 (if v
                                   (.setAttribute this model/attr-dismissible "")
                                   (.setAttribute this model/attr-dismissible val-false))))
                        :enumerable true :configurable true})

  ;; Tier 2 — `timeoutMs` is the camelCase JS property reflecting the
  ;; kebab-case `timeout-ms` HTML attribute. du/define-number-prop! always
  ;; uses the same name on both sides, so a kebab→camel rename needs hand
  ;; wiring. The setter also serialises through (int v) so floats round
  ;; rather than producing decimal-string attributes.
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

;; ── Element class ───────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el)
  (update-from-attrs! el)
  (start-enter! el)
  (schedule-timeout! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)
    (when (.-isConnected el)
      (schedule-timeout! el))))

;; ── Public API ──────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
