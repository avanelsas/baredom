(ns baredom.components.x-cancel-dialogue.x-cancel-dialogue
  (:require
   [goog.object :as gobj]
   [baredom.components.x-cancel-dialogue.model :as model]))

;; ── Instance-field keys (always use gobj/get, gobj/set) ─────────────────────
(def ^:private k-refs     "__xCancelDialogueRefs")
(def ^:private k-handlers "__xCancelDialogueHandlers")

;; Unique ID counter for aria labelling — stored as a plain JS object field,
;; never as an atom, to comply with no-mutable-state conventions.
(def ^:private id-state #js {:n 0})

(defn- next-id! []
  (let [n (inc (gobj/get id-state "n"))]
    (gobj/set id-state "n" n)
    (str "x-cd-headline-" n)))

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
(defn- make-el [tag]
  (.createElement js/document tag))

;; ── Shadow DOM initialisation ─────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [headline-id  (next-id!)
        root         (.attachShadow el #js {:mode "open"})
        style        (make-el "style")
        backdrop     (make-el "div")
        dialog       (make-el "div")
        panel        (make-el "div")
        header       (make-el "div")
        headline     (make-el "h2")
        body         (make-el "div")
        body-slot    (make-el "slot")
        message-el   (make-el "p")
        actions      (make-el "div")
        cancel-btn   (make-el "button")
        confirm-btn  (make-el "button")]

    (set! (.-textContent style) style-text)

    (.setAttribute backdrop "part" "backdrop")

    (.setAttribute dialog "part" "dialog")
    (.setAttribute dialog "role" "dialog")
    (.setAttribute dialog "aria-modal" "true")
    (.setAttribute dialog "aria-labelledby" headline-id)

    (.setAttribute panel "part" "panel")

    (.setAttribute header "part" "header")
    (.setAttribute headline "part" "headline")
    (.setAttribute headline "id" headline-id)

    (.setAttribute body "part" "body")
    (.setAttribute body-slot "name" "body")
    (.setAttribute message-el "part" "message")

    (.setAttribute actions "part" "actions")

    (.setAttribute cancel-btn "part" "cancel-btn")
    (.setAttribute cancel-btn "type" "button")

    (.setAttribute confirm-btn "part" "confirm-btn")
    (.setAttribute confirm-btn "type" "button")

    (.appendChild header headline)
    (.appendChild body body-slot)
    (.appendChild body message-el)
    (.appendChild actions cancel-btn)
    (.appendChild actions confirm-btn)
    (.appendChild panel header)
    (.appendChild panel body)
    (.appendChild panel actions)
    (.appendChild dialog panel)

    (.appendChild root style)
    (.appendChild root backdrop)
    (.appendChild root dialog)

    (gobj/set el k-refs
              #js {:root       root
                   :backdrop   backdrop
                   :dialog     dialog
                   :panel      panel
                   :headline   headline
                   :bodySlot   body-slot
                   :message    message-el
                   :cancelBtn  cancel-btn
                   :confirmBtn confirm-btn}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:open-present?     (.hasAttribute el model/attr-open)
    :disabled-present? (.hasAttribute el model/attr-disabled)
    :headline-raw      (.getAttribute el model/attr-headline)
    :message-raw       (.getAttribute el model/attr-message)
    :confirm-text-raw  (.getAttribute el model/attr-confirm-text)
    :cancel-text-raw   (.getAttribute el model/attr-cancel-text)
    :danger-present?   (.hasAttribute el model/attr-danger)}))

;; ── Event dispatch ────────────────────────────────────────────────────────────
(defn- dispatch! [^js el event-name detail cancelable?]
  (let [^js ev (js/CustomEvent.
                event-name
                #js {:detail     detail
                     :bubbles    true
                     :composed   true
                     :cancelable cancelable?})]
    (.dispatchEvent el ev)
    ev))

;; ── Open / close ──────────────────────────────────────────────────────────────
(defn- do-open! [^js el]
  (when-not (.hasAttribute el model/attr-open)
    (.setAttribute el model/attr-open ""))
  ;; Focus confirm button after next paint
  (let [refs       (ensure-refs! el)
        ^js confirm (gobj/get refs "confirmBtn")]
    (js/setTimeout (fn [] (.focus confirm)) 0))
  nil)

(defn- do-close! [^js el]
  (.removeAttribute el model/attr-open)
  nil)

;; ── Cancel flow ───────────────────────────────────────────────────────────────
(defn- do-cancel! [^js el reason]
  (when-not (.hasAttribute el model/attr-disabled)
    (let [^js req-ev (dispatch! el model/event-cancel-request
                                (model/cancel-request-detail reason) true)]
      (when-not (.-defaultPrevented req-ev)
        (do-close! el)
        (dispatch! el model/event-cancel #js {} false))))
  nil)

;; ── Confirm flow ──────────────────────────────────────────────────────────────
(defn- do-confirm! [^js el]
  (when-not (.hasAttribute el model/attr-disabled)
    (let [^js req-ev (dispatch! el model/event-confirm-request
                                (model/confirm-request-detail) true)]
      (when-not (.-defaultPrevented req-ev)
        (do-close! el)
        (dispatch! el model/event-confirm #js {} false))))
  nil)

;; ── Focus trap ────────────────────────────────────────────────────────────────
(defn- on-keydown [^js el ^js e]
  (let [key (.-key e)]
    (cond
      (= key "Escape")
      (do (.preventDefault e)
          (do-cancel! el "escape"))

      (= key "Tab")
      (let [refs         (ensure-refs! el)
            ^js cancel   (gobj/get refs "cancelBtn")
            ^js confirm  (gobj/get refs "confirmBtn")
            shift?       (.-shiftKey e)
            active       (.-activeElement (.-shadowRoot el))]
        (cond
          (and shift? (= active cancel))
          (do (.preventDefault e)
              (.focus confirm))

          (and (not shift?) (= active confirm))
          (do (.preventDefault e)
              (.focus cancel))))))
  nil)

;; ── Render ────────────────────────────────────────────────────────────────────
(defn- render! [^js el]
  (let [refs         (ensure-refs! el)
        ^js headline  (gobj/get refs "headline")
        ^js message   (gobj/get refs "message")
        ^js cancel    (gobj/get refs "cancelBtn")
        ^js confirm   (gobj/get refs "confirmBtn")
        m            (read-model el)]

    (set! (.-textContent headline) (:headline m))
    (set! (.-textContent cancel) (:cancel-text m))
    (set! (.-textContent confirm) (:confirm-text m))

    (if (:message m)
      (do (set! (.-textContent message) (:message m))
          (set! (.. message -style -display) "block"))
      (do (set! (.-textContent message) "")
          (set! (.. message -style -display) "none")))

    (if (:danger? m)
      (.setAttribute confirm "data-danger" "")
      (.removeAttribute confirm "data-danger"))

    (set! (.-disabled cancel) (boolean (:disabled? m)))
    (set! (.-disabled confirm) (boolean (:disabled? m)))

    ;; aria-modal on dialog
    (let [^js dialog (gobj/get refs "dialog")]
      (.setAttribute dialog "aria-modal" "true")))
  nil)

;; ── Listener management ───────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [refs         (ensure-refs! el)
        ^js backdrop (gobj/get refs "backdrop")
        ^js cancel   (gobj/get refs "cancelBtn")
        ^js confirm  (gobj/get refs "confirmBtn")
        ^js dialog   (gobj/get refs "dialog")
        backdrop-h   (fn [_] (do-cancel! el "backdrop"))
        cancel-h     (fn [_] (do-cancel! el "cancel-button"))
        confirm-h    (fn [_] (do-confirm! el))
        keydown-h    (fn [^js e] (on-keydown el e))]
    (.addEventListener backdrop "click" backdrop-h)
    (.addEventListener cancel "click" cancel-h)
    (.addEventListener confirm "click" confirm-h)
    (.addEventListener dialog "keydown" keydown-h)
    (gobj/set el k-handlers
              #js {:backdrop backdrop-h
                   :cancel   cancel-h
                   :confirm  confirm-h
                   :keydown  keydown-h}))
  nil)

(defn- remove-listeners! [^js el]
  (let [hs   (gobj/get el k-handlers)
        refs (gobj/get el k-refs)]
    (when (and hs refs)
      (let [^js backdrop (gobj/get refs "backdrop")
            ^js cancel   (gobj/get refs "cancelBtn")
            ^js confirm  (gobj/get refs "confirmBtn")
            ^js dialog   (gobj/get refs "dialog")
            backdrop-h  (gobj/get hs "backdrop")
            cancel-h    (gobj/get hs "cancel")
            confirm-h   (gobj/get hs "confirm")
            keydown-h   (gobj/get hs "keydown")]
        (when backdrop-h (.removeEventListener backdrop "click" backdrop-h))
        (when cancel-h   (.removeEventListener cancel "click" cancel-h))
        (when confirm-h  (.removeEventListener confirm "click" confirm-h))
        (when keydown-h  (.removeEventListener dialog "keydown" keydown-h)))))
  (gobj/set el k-handlers nil)
  nil)

;; ── Lifecycle ─────────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el)
  (render! el)
  nil)

(defn- disconnected! [^js el]
  (remove-listeners! el)
  nil)

(defn- attribute-changed! [^js el attr-name old-val new-val]
  (when (not= old-val new-val)
    (render! el)
    ;; When open attr is added, focus the confirm button
    (when (and (= attr-name model/attr-open) (some? new-val) (nil? old-val))
      (let [refs        (gobj/get el k-refs)
            ^js confirm (when refs (gobj/get refs "confirmBtn"))]
        (when confirm
          (js/setTimeout (fn [] (.focus confirm)) 0)))))
  nil)

;; ── Property accessors ────────────────────────────────────────────────────────
(defn- install-properties! [^js proto]
  ;; Boolean property: open
  ;; Setting open=true is equivalent to calling the open() method.
  ;; There is no separate open() method because the property getter/setter
  ;; occupies the same name. Use close(), confirm(), cancel() for imperative API.
  (.defineProperty js/Object proto "open"
                   #js {:get (fn []
                               (this-as ^js this
                                        (.hasAttribute this model/attr-open)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (do-open! this)
                                          (do-close! this))))
                        :enumerable true :configurable true})

  ;; Boolean properties: disabled, danger
  (doseq [[js-prop attr-name]
          [["disabled" model/attr-disabled]
           ["danger"   model/attr-danger]]]
    (.defineProperty js/Object proto js-prop
                     #js {:get (fn []
                                 (this-as ^js this
                                          (.hasAttribute this attr-name)))
                          :set (fn [v]
                                 (this-as ^js this
                                          (if v
                                            (.setAttribute this attr-name "")
                                            (.removeAttribute this attr-name))))
                          :enumerable true :configurable true}))

  ;; String properties
  (doseq [[js-prop attr-name]
          [["headline"    model/attr-headline]
           ["message"     model/attr-message]
           ["confirmText" model/attr-confirm-text]
           ["cancelText"  model/attr-cancel-text]]]
    (.defineProperty js/Object proto js-prop
                     #js {:get (fn []
                                 (this-as ^js this
                                          (.getAttribute this attr-name)))
                          :set (fn [v]
                                 (this-as ^js this
                                          (if (nil? v)
                                            (.removeAttribute this attr-name)
                                            (.setAttribute this attr-name (str v)))))
                          :enumerable true :configurable true}))

  ;; Public methods
  (.defineProperty js/Object proto "close"
                   #js {:value (fn []
                                 (this-as ^js this
                                          (do-close! this)))
                        :enumerable true :configurable true :writable true})

  (.defineProperty js/Object proto "confirm"
                   #js {:value (fn []
                                 (this-as ^js this
                                          (do-confirm! this)))
                        :enumerable true :configurable true :writable true})

  (.defineProperty js/Object proto "cancel"
                   #js {:value (fn [reason]
                                 (this-as ^js this
                                          (do-cancel! this (or reason "cancel-button"))))
                        :enumerable true :configurable true :writable true}))

;; ── Element class ─────────────────────────────────────────────────────────────
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]
    (set! (.-observedAttributes klass) model/observed-attributes)
    (set! (.-connectedCallback (.-prototype klass))
          (fn [] (this-as ^js this (connected! this))))
    (set! (.-disconnectedCallback (.-prototype klass))
          (fn [] (this-as ^js this (disconnected! this))))
    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [n o v] (this-as ^js this (attribute-changed! this n o v))))
    (install-properties! (.-prototype klass))
    klass))

;; ── Public API ────────────────────────────────────────────────────────────────
(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class)))
  nil)

(defn init! []
  (register!))
