(ns baredom.components.x-cancel-dialogue.x-cancel-dialogue
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.x-cancel-dialogue.model :as model]))

;; ── Instance-field keys ─────────────────────────────────────────────────────
(def ^:private k-refs     "__xCancelDialogueRefs")
(def ^:private k-model    "__xCancelDialogueModel")
(def ^:private k-handlers "__xCancelDialogueHandlers")

;; ── String-literal constants ────────────────────────────────────────────────
(def ^:private attr-part            "part")
(def ^:private attr-role            "role")
(def ^:private attr-aria-modal      "aria-modal")
(def ^:private attr-aria-labelledby "aria-labelledby")
(def ^:private attr-id              "id")
(def ^:private attr-type            "type")
(def ^:private attr-name            "name")
(def ^:private attr-data-danger     "data-danger")

(def ^:private part-backdrop    "backdrop")
(def ^:private part-dialog      "dialog")
(def ^:private part-panel       "panel")
(def ^:private part-header      "header")
(def ^:private part-headline    "headline")
(def ^:private part-body        "body")
(def ^:private part-message     "message")
(def ^:private part-actions     "actions")
(def ^:private part-cancel-btn  "cancel-btn")
(def ^:private part-confirm-btn "confirm-btn")

(def ^:private role-dialog "dialog")
(def ^:private val-true    "true")

(def ^:private ev-click   "click")
(def ^:private ev-keydown "keydown")

(def ^:private hk-backdrop "backdrop")
(def ^:private hk-cancel   "cancel")
(def ^:private hk-confirm  "confirm")
(def ^:private hk-keydown  "keydown")

(def ^:private rk-backdrop    "backdrop")
(def ^:private rk-dialog      "dialog")
(def ^:private rk-headline    "headline")
(def ^:private rk-message     "message")
(def ^:private rk-cancel-btn  "cancelBtn")
(def ^:private rk-confirm-btn "confirmBtn")

(def ^:private reason-backdrop      "backdrop")
(def ^:private reason-cancel-button "cancel-button")
(def ^:private reason-escape        "escape")

(def ^:private key-escape "Escape")
(def ^:private key-tab    "Tab")

;; ── Styles ────────────────────────────────────────────────────────────────────
(def style-text
  (str
   ":host{"
   "display:contents;"
   "color-scheme:light dark;"
   "--x-cancel-dialogue-z-base:1000;"
   "--x-cancel-dialogue-backdrop-bg:rgba(0,0,0,0.45);"
   "--x-cancel-dialogue-bg:var(--x-color-bg,#ffffff);"
   "--x-cancel-dialogue-fg:var(--x-color-text,#0f172a);"
   "--x-cancel-dialogue-radius:var(--x-radius-lg,12px);"
   "--x-cancel-dialogue-shadow:var(--x-shadow-lg,0 20px 60px rgba(0,0,0,0.20),0 4px 12px rgba(0,0,0,0.10));"
   "--x-cancel-dialogue-padding:24px;"
   "--x-cancel-dialogue-min-width:min(320px,calc(100vw - 2rem));"
   "--x-cancel-dialogue-max-width:min(480px,calc(100vw - 2rem));"
   "--x-cancel-dialogue-headline-size:1.125rem;"
   "--x-cancel-dialogue-headline-weight:700;"
   "--x-cancel-dialogue-message-size:0.9375rem;"
   "--x-cancel-dialogue-cancel-bg:var(--x-color-surface-active,#f1f5f9);"
   "--x-cancel-dialogue-cancel-fg:var(--x-color-text,#0f172a);"
   "--x-cancel-dialogue-cancel-bg-hover:var(--x-color-surface-active,#e2e8f0);"
   "--x-cancel-dialogue-confirm-bg:var(--x-color-primary,#2563eb);"
   "--x-cancel-dialogue-confirm-fg:#ffffff;"
   "--x-cancel-dialogue-confirm-bg-hover:var(--x-color-primary-hover,#1d4ed8);"
   "--x-cancel-dialogue-danger-bg:var(--x-color-danger,#dc2626);"
   "--x-cancel-dialogue-danger-fg:#ffffff;"
   "--x-cancel-dialogue-danger-bg-hover:#b91c1c;"
   "--x-cancel-dialogue-btn-radius:var(--x-radius-md,8px);"
   "--x-cancel-dialogue-btn-height:2.5rem;"
   "--x-cancel-dialogue-btn-font-size:0.9375rem;"
   "--x-cancel-dialogue-btn-font-weight:600;"
   "--x-cancel-dialogue-focus-ring:var(--x-color-focus-ring,#60a5fa);"
   "--x-cancel-dialogue-enter-duration:0ms;"
   "--x-cancel-dialogue-exit-duration:0ms;"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-cancel-dialogue-bg:var(--x-color-bg,#1e293b);"
   "--x-cancel-dialogue-fg:var(--x-color-text,#f1f5f9);"
   "--x-cancel-dialogue-backdrop-bg:rgba(0,0,0,0.60);"
   "--x-cancel-dialogue-shadow:0 20px 60px rgba(0,0,0,0.55),0 4px 12px rgba(0,0,0,0.30);"
   "--x-cancel-dialogue-cancel-bg:var(--x-color-text-muted,#334155);"
   "--x-cancel-dialogue-cancel-fg:var(--x-color-text,#f1f5f9);"
   "--x-cancel-dialogue-cancel-bg-hover:var(--x-color-border,#475569);"
   "--x-cancel-dialogue-confirm-bg:var(--x-color-primary,#3b82f6);"
   "--x-cancel-dialogue-confirm-bg-hover:var(--x-color-primary-hover,#2563eb);"
   "--x-cancel-dialogue-focus-ring:var(--x-color-focus-ring,#93c5fd);"
   "}}"
   "[part=backdrop]{"
   "display:none;"
   "position:fixed;"
   "inset:0;"
   "background:var(--x-cancel-dialogue-backdrop-bg);"
   "z-index:var(--x-cancel-dialogue-z-base,1000);"
   "}"
   "[part=dialog]{"
   "display:none;"
   "position:fixed;"
   "inset:0;"
   "align-items:center;"
   "justify-content:center;"
   "z-index:calc(var(--x-cancel-dialogue-z-base,1000) + 1);"
   "padding:16px;"
   "box-sizing:border-box;"
   "}"
   ":host([open]) [part=backdrop]{display:block;}"
   ":host([open]) [part=dialog]{display:flex;}"
   "[part=panel]{"
   "background:var(--x-cancel-dialogue-bg);"
   "color:var(--x-cancel-dialogue-fg);"
   "border-radius:var(--x-cancel-dialogue-radius);"
   "box-shadow:var(--x-cancel-dialogue-shadow);"
   "padding:var(--x-cancel-dialogue-padding);"
   "min-width:var(--x-cancel-dialogue-min-width);"
   "max-width:var(--x-cancel-dialogue-max-width);"
   "width:100%;"
   "box-sizing:border-box;"
   "display:flex;"
   "flex-direction:column;"
   "gap:16px;"
   "}"
   "[part=header]{display:flex;align-items:flex-start;}"
   "[part=headline]{"
   "margin:0;"
   "font-size:var(--x-cancel-dialogue-headline-size);"
   "font-weight:var(--x-cancel-dialogue-headline-weight);"
   "line-height:1.3;"
   "}"
   "[part=body]{display:flex;flex-direction:column;gap:8px;}"
   "[part=message]{"
   "font-size:var(--x-cancel-dialogue-message-size);"
   "line-height:1.5;"
   "margin:0;"
   "opacity:0.8;"
   "}"
   "[part=actions]{"
   "display:flex;"
   "gap:8px;"
   "justify-content:flex-end;"
   "flex-wrap:wrap;"
   "}"
   "[part=cancel-btn],[part=confirm-btn]{"
   "all:unset;"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "box-sizing:border-box;"
   "padding:0 20px;"
   "min-height:var(--x-cancel-dialogue-btn-height);"
   "border-radius:var(--x-cancel-dialogue-btn-radius);"
   "font-size:var(--x-cancel-dialogue-btn-font-size);"
   "font-weight:var(--x-cancel-dialogue-btn-font-weight);"
   "cursor:pointer;"
   "user-select:none;"
   "white-space:nowrap;"
   "}"
   "[part=cancel-btn]{"
   "background:var(--x-cancel-dialogue-cancel-bg);"
   "color:var(--x-cancel-dialogue-cancel-fg);"
   "}"
   "[part=cancel-btn]:hover:not(:disabled){"
   "background:var(--x-cancel-dialogue-cancel-bg-hover);"
   "}"
   "[part=confirm-btn]{"
   "background:var(--x-cancel-dialogue-confirm-bg);"
   "color:var(--x-cancel-dialogue-confirm-fg);"
   "}"
   "[part=confirm-btn]:hover:not(:disabled){"
   "background:var(--x-cancel-dialogue-confirm-bg-hover);"
   "}"
   "[part=confirm-btn][data-danger]{"
   "background:var(--x-cancel-dialogue-danger-bg);"
   "color:var(--x-cancel-dialogue-danger-fg);"
   "}"
   "[part=confirm-btn][data-danger]:hover:not(:disabled){"
   "background:var(--x-cancel-dialogue-danger-bg-hover);"
   "}"
   "[part=cancel-btn]:focus-visible,[part=confirm-btn]:focus-visible{"
   "outline:2px solid var(--x-cancel-dialogue-focus-ring);"
   "outline-offset:2px;"
   "}"
   ":host([disabled]) [part=cancel-btn],:host([disabled]) [part=confirm-btn]{"
   "opacity:0.55;"
   "pointer-events:none;"
   "cursor:default;"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=backdrop],[part=dialog],[part=panel]{transition:none;animation:none;}"
   "}"))

;; ── DOM helpers ───────────────────────────────────────────────────────────────
(defn- make-el [tag] (.createElement js/document tag))

(defn- new-headline-id []
  (str "x-cd-headline-" (.. js/crypto (randomUUID))))

;; ── Shadow DOM initialisation (shadow-builders pattern) ───────────────────────
(defn- make-backdrop! []
  (let [backdrop (make-el "div")]
    (.setAttribute backdrop attr-part part-backdrop)
    backdrop))

(defn- make-dialog! [headline-id]
  (let [dialog (make-el "div")]
    (.setAttribute dialog attr-part            part-dialog)
    (.setAttribute dialog attr-role            role-dialog)
    (.setAttribute dialog attr-aria-modal      val-true)
    (.setAttribute dialog attr-aria-labelledby headline-id)
    dialog))

(defn- make-header! [headline-id]
  (let [header   (make-el "div")
        headline (make-el "h2")]
    (.setAttribute header   attr-part part-header)
    (.setAttribute headline attr-part part-headline)
    (.setAttribute headline attr-id   headline-id)
    (.appendChild header headline)
    {:header header :headline headline}))

(defn- make-body! []
  (let [body       (make-el "div")
        body-slot  (make-el "slot")
        message-el (make-el "p")]
    (.setAttribute body       attr-part part-body)
    (.setAttribute body-slot  attr-name part-body)
    (.setAttribute message-el attr-part part-message)
    (.appendChild body body-slot)
    (.appendChild body message-el)
    {:body body :body-slot body-slot :message message-el}))

(defn- make-actions! []
  (let [actions     (make-el "div")
        cancel-btn  (make-el "button")
        confirm-btn (make-el "button")]
    (.setAttribute actions     attr-part part-actions)
    (.setAttribute cancel-btn  attr-part part-cancel-btn)
    (.setAttribute cancel-btn  attr-type "button")
    (.setAttribute confirm-btn attr-part part-confirm-btn)
    (.setAttribute confirm-btn attr-type "button")
    (.appendChild actions cancel-btn)
    (.appendChild actions confirm-btn)
    {:actions actions :cancel-btn cancel-btn :confirm-btn confirm-btn}))

(defn- assemble-panel! [header-parts body-parts action-parts]
  (let [panel (make-el "div")]
    (.setAttribute panel attr-part part-panel)
    (.appendChild panel (:header  header-parts))
    (.appendChild panel (:body    body-parts))
    (.appendChild panel (:actions action-parts))
    panel))

(defn- init-dom! [^js el]
  (let [headline-id  (new-headline-id)
        root         (.attachShadow el #js {:mode "open"})
        style        (make-el "style")
        backdrop     (make-backdrop!)
        dialog       (make-dialog! headline-id)
        header-parts (make-header! headline-id)
        body-parts   (make-body!)
        action-parts (make-actions!)
        panel        (assemble-panel! header-parts body-parts action-parts)
        refs         #js {}]

    (set! (.-textContent style) style-text)
    (.appendChild dialog panel)
    (.appendChild root style)
    (.appendChild root backdrop)
    (.appendChild root dialog)

    (gobj/set refs rk-backdrop    backdrop)
    (gobj/set refs rk-dialog      dialog)
    (gobj/set refs rk-headline    (:headline header-parts))
    (gobj/set refs rk-message     (:message  body-parts))
    (gobj/set refs rk-cancel-btn  (:cancel-btn  action-parts))
    (gobj/set refs rk-confirm-btn (:confirm-btn action-parts))
    (gobj/set el k-refs refs)
    refs))

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs) (init-dom! el)))

;; ── Attribute readers ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:open-present?     (du/has-attr? el model/attr-open)
    :disabled-present? (du/has-attr? el model/attr-disabled)
    :headline-raw      (du/get-attr el model/attr-headline)
    :message-raw       (du/get-attr el model/attr-message)
    :confirm-text-raw  (du/get-attr el model/attr-confirm-text)
    :cancel-text-raw   (du/get-attr el model/attr-cancel-text)
    :danger-present?   (du/has-attr? el model/attr-danger)}))

;; ── Open / close (attribute mutation only — effects flow via apply-model!) ───
(defn- do-open! [^js el]
  (when-not (du/has-attr? el model/attr-open)
    (du/set-attr! el model/attr-open "")))

(defn- do-close! [^js el]
  (du/remove-attr! el model/attr-open))

;; ── Cancel / confirm flows ───────────────────────────────────────────────────
(defn- do-cancel! [^js el reason]
  (when-not (du/has-attr? el model/attr-disabled)
    (when (du/dispatch-cancelable! el model/event-cancel-request
                                   (model/cancel-request-detail reason))
      (do-close! el)
      (du/dispatch! el model/event-cancel #js {}))))

(defn- do-confirm! [^js el]
  (when-not (du/has-attr? el model/attr-disabled)
    (when (du/dispatch-cancelable! el model/event-confirm-request
                                   (model/confirm-request-detail))
      (do-close! el)
      (du/dispatch! el model/event-confirm #js {}))))

;; ── Focus trap ──────────────────────────────────────────────────────────────
(defn- on-keydown [^js el ^js e]
  (let [k (.-key e)]
    (cond
      (= k key-escape)
      (do (.preventDefault e)
          (do-cancel! el reason-escape))

      (= k key-tab)
      (let [refs        (ensure-refs! el)
            ^js cancel  (gobj/get refs rk-cancel-btn)
            ^js confirm (gobj/get refs rk-confirm-btn)
            shift?      (.-shiftKey e)
            active      (.-activeElement (.-shadowRoot el))]
        (cond
          (and shift? (= active cancel))
          (do (.preventDefault e) (.focus confirm))

          (and (not shift?) (= active confirm))
          (do (.preventDefault e) (.focus cancel)))))))

;; ── DOM patching ─────────────────────────────────────────────────────────────
(defn- apply-text-content! [refs {:keys [headline cancel-text confirm-text]}]
  (let [^js headline-el (gobj/get refs rk-headline)
        ^js cancel      (gobj/get refs rk-cancel-btn)
        ^js confirm     (gobj/get refs rk-confirm-btn)]
    (set! (.-textContent headline-el) headline)
    (set! (.-textContent cancel)      cancel-text)
    (set! (.-textContent confirm)     confirm-text)))

(defn- apply-message! [refs {:keys [message]}]
  (let [^js message-el (gobj/get refs rk-message)]
    (if message
      (do (set! (.-textContent message-el) message)
          (set! (.. message-el -style -display) "block"))
      (do (set! (.-textContent message-el) "")
          (set! (.. message-el -style -display) "none")))))

(defn- apply-danger! [refs {:keys [danger?]}]
  (let [^js confirm (gobj/get refs rk-confirm-btn)]
    (if danger?
      (.setAttribute confirm attr-data-danger "")
      (.removeAttribute confirm attr-data-danger))))

(defn- apply-disabled! [refs {:keys [disabled?]}]
  (let [^js cancel  (gobj/get refs rk-cancel-btn)
        ^js confirm (gobj/get refs rk-confirm-btn)
        d?          (boolean disabled?)]
    (set! (.-disabled cancel)  d?)
    (set! (.-disabled confirm) d?)))

(defn- focus-confirm-soon! [refs]
  (let [^js confirm (gobj/get refs rk-confirm-btn)]
    (js/setTimeout (fn focus-confirm [] (.focus confirm)) 0)))

(defn- apply-open-transition! [refs old-m new-m]
  ;; Focus the confirm button only when transitioning closed → open.
  ;; Single focus point — neither the property setter nor attribute-changed
  ;; should schedule focus independently, otherwise it double-fires.
  (when (and (:open? new-m) (not (:open? old-m)))
    (focus-confirm-soon! refs)))

(defn- apply-model! [^js el old-m new-m]
  (let [refs (ensure-refs! el)]
    (apply-text-content!     refs new-m)
    (apply-message!          refs new-m)
    (apply-danger!           refs new-m)
    (apply-disabled!         refs new-m)
    (apply-open-transition!  refs old-m new-m)
    (gobj/set el k-model new-m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (gobj/get el k-model)]
    (when (not= old-m new-m)
      (apply-model! el old-m new-m))))

;; ── Listener management ───────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [refs         (ensure-refs! el)
        ^js backdrop (gobj/get refs rk-backdrop)
        ^js cancel   (gobj/get refs rk-cancel-btn)
        ^js confirm  (gobj/get refs rk-confirm-btn)
        ^js dialog   (gobj/get refs rk-dialog)
        backdrop-h   (fn on-backdrop-click [_] (do-cancel! el reason-backdrop))
        cancel-h     (fn on-cancel-click   [_] (do-cancel! el reason-cancel-button))
        confirm-h    (fn on-confirm-click  [_] (do-confirm! el))
        keydown-h    (fn on-dialog-keydown [^js e] (on-keydown el e))
        handlers     #js {}]
    (.addEventListener backdrop ev-click   backdrop-h)
    (.addEventListener cancel   ev-click   cancel-h)
    (.addEventListener confirm  ev-click   confirm-h)
    (.addEventListener dialog   ev-keydown keydown-h)
    (gobj/set handlers hk-backdrop backdrop-h)
    (gobj/set handlers hk-cancel   cancel-h)
    (gobj/set handlers hk-confirm  confirm-h)
    (gobj/set handlers hk-keydown  keydown-h)
    (gobj/set el k-handlers handlers)))

(defn- remove-listeners! [^js el]
  (let [hs   (gobj/get el k-handlers)
        refs (gobj/get el k-refs)]
    (when (and hs refs)
      (let [^js backdrop (gobj/get refs rk-backdrop)
            ^js cancel   (gobj/get refs rk-cancel-btn)
            ^js confirm  (gobj/get refs rk-confirm-btn)
            ^js dialog   (gobj/get refs rk-dialog)
            backdrop-h   (gobj/get hs hk-backdrop)
            cancel-h     (gobj/get hs hk-cancel)
            confirm-h    (gobj/get hs hk-confirm)
            keydown-h    (gobj/get hs hk-keydown)]
        (when backdrop-h (.removeEventListener backdrop ev-click   backdrop-h))
        (when cancel-h   (.removeEventListener cancel   ev-click   cancel-h))
        (when confirm-h  (.removeEventListener confirm  ev-click   confirm-h))
        (when keydown-h  (.removeEventListener dialog   ev-keydown keydown-h)))))
  (gobj/set el k-handlers nil))

;; ── Lifecycle ────────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _attr-name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Property accessors ───────────────────────────────────────────────────────
;; `open` is Tier-2: the setter routes through do-open!/do-close! so that
;; setting open=true mutates the attribute, which fires attribute-changed!,
;; which runs the change-guard and the single focus effect in apply-model!.
(defn- install-properties! [^js proto]
  (.defineProperty js/Object proto "open"
                   #js {:get (fn []
                               (this-as ^js this
                                 (.hasAttribute this model/attr-open)))
                        :set (fn [v]
                               (this-as ^js this
                                 (if v (do-open! this) (do-close! this))))
                        :enumerable true :configurable true})

  (du/define-bool-prop! proto "disabled" model/attr-disabled)
  (du/define-bool-prop! proto "danger"   model/attr-danger)

  (du/define-string-prop! proto "headline"    model/attr-headline)
  (du/define-string-prop! proto "message"     model/attr-message)
  (du/define-string-prop! proto "confirmText" model/attr-confirm-text)
  (du/define-string-prop! proto "cancelText"  model/attr-cancel-text)

  (.defineProperty js/Object proto "close"
                   #js {:value (fn []
                                 (this-as ^js this (do-close! this)))
                        :enumerable true :configurable true :writable true})

  (.defineProperty js/Object proto "confirm"
                   #js {:value (fn []
                                 (this-as ^js this (do-confirm! this)))
                        :enumerable true :configurable true :writable true})

  (.defineProperty js/Object proto "cancel"
                   #js {:value (fn [reason]
                                 (this-as ^js this
                                   (do-cancel! this (or reason reason-cancel-button))))
                        :enumerable true :configurable true :writable true}))

;; ── Public API ───────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-properties!}))
