(ns baredom.components.x-copy.x-copy
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.x-copy.model :as model]))

;; ── Instance-field keys (always use gobj/get, gobj/set) ─────────────────────
(def ^:private k-refs      "__xCopyRefs")
(def ^:private k-model     "__xCopyModel")
(def ^:private k-handlers  "__xCopyHandlers")
(def ^:private k-tid       "__xCopyTid")
(def ^:private k-text-val  "__xCopyTextValue")

;; ── String-literal constants ────────────────────────────────────────────────
(def ^:private attr-aria-disabled      "aria-disabled")
(def ^:private attr-data-tooltip-open  "data-tooltip-open")
(def ^:private attr-data-tooltip-kind  "data-tooltip-kind")

(def ^:private rk-tooltip-text "tooltipText")
(def ^:private rk-trigger      "trigger")

(def ^:private val-true     "true")
(def ^:private mode-html    "html")
(def ^:private mode-text    "text")
(def ^:private kind-success "success")
(def ^:private kind-error   "error")

(def ^:private mime-text-html  "text/html")
(def ^:private mime-text-plain "text/plain")
(def ^:private cmd-copy        "copy")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;"
   "position:relative;"
   "color-scheme:light dark;"
   "--x-copy-tooltip-bg:var(--x-color-bg,#1e293b);"
   "--x-copy-tooltip-fg:var(--x-color-text,#f8fafc);"
   "--x-copy-tooltip-success-bg:var(--x-color-primary,#15803d);"
   "--x-copy-tooltip-success-fg:#ffffff;"
   "--x-copy-tooltip-error-bg:var(--x-color-danger,#b91c1c);"
   "--x-copy-tooltip-error-fg:#fef2f2;"
   "--x-copy-tooltip-radius:var(--x-radius-md,6px);"
   "--x-copy-tooltip-padding:4px 8px;"
   "--x-copy-tooltip-font-size:var(--x-font-size-sm,0.8125rem);"
   "--x-copy-tooltip-z:100;"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-copy-tooltip-bg:var(--x-color-bg,#0f172a);"
   "--x-copy-tooltip-fg:var(--x-color-text,#e2e8f0);"
   "--x-copy-tooltip-success-bg:var(--x-color-primary,#166534);"
   "--x-copy-tooltip-success-fg:#ffffff;"
   "--x-copy-tooltip-error-bg:var(--x-color-danger,#991b1b);"
   "--x-copy-tooltip-error-fg:#fee2e2;"
   "}}"
   "[part=wrap]{"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "}"
   "[part=trigger]{"
   "all:unset;"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "cursor:pointer;"
   "box-sizing:border-box;"
   "}"
   "[part=trigger]:focus-visible{"
   "outline:2px solid currentColor;"
   "outline-offset:2px;"
   "}"
   ":host([disabled]) [part=trigger]{"
   "opacity:0.55;"
   "pointer-events:none;"
   "cursor:default;"
   "}"
   "[part=tooltip]{"
   "display:none;"
   "position:absolute;"
   "bottom:calc(100% + 6px);"
   "left:50%;"
   "transform:translateX(-50%);"
   "white-space:nowrap;"
   "background:var(--x-copy-tooltip-bg);"
   "color:var(--x-copy-tooltip-fg);"
   "border-radius:var(--x-copy-tooltip-radius);"
   "padding:var(--x-copy-tooltip-padding);"
   "font-size:var(--x-copy-tooltip-font-size);"
   "z-index:var(--x-copy-tooltip-z);"
   "pointer-events:none;"
   "}"
   ":host([data-tooltip-open]) [part=tooltip]{display:block;}"
   ":host([data-tooltip-kind=success]) [part=tooltip]{"
   "background:var(--x-copy-tooltip-success-bg);"
   "color:var(--x-copy-tooltip-success-fg);"
   "}"
   ":host([data-tooltip-kind=error]) [part=tooltip]{"
   "background:var(--x-copy-tooltip-error-bg);"
   "color:var(--x-copy-tooltip-error-fg);"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=trigger]{transition:none;}"
   "}"))

;; ── DOM helpers ───────────────────────────────────────────────────────────────
(defn- make-el [^js doc tag]
  (.createElement doc tag))

;; ── Shadow DOM initialisation ─────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root         (.attachShadow el #js {:mode "open"})
        style        (make-el js/document "style")
        wrap         (make-el js/document "span")
        trigger      (make-el js/document "button")
        default-slot (make-el js/document "slot")
        tooltip      (make-el js/document "span")
        tooltip-slot (make-el js/document "slot")
        tooltip-text (make-el js/document "span")]

    (set! (.-textContent style) style-text)

    (du/set-attr! wrap "part" "wrap")

    (du/set-attr! trigger "part" "trigger")
    (du/set-attr! trigger "type" "button")

    (.appendChild trigger default-slot)

    (du/set-attr! tooltip "part" "tooltip")
    (du/set-attr! tooltip "role" "status")
    (du/set-attr! tooltip "aria-live" "polite")

    (du/set-attr! tooltip-slot "name" "tooltip")
    (du/set-attr! tooltip-text "part" "tooltip-text")

    (.appendChild tooltip tooltip-slot)
    (.appendChild tooltip tooltip-text)

    (.appendChild wrap trigger)
    (.appendChild wrap tooltip)

    (.appendChild root style)
    (.appendChild root wrap)

    (du/setv! el k-refs
              #js {:root        root
                   :wrap        wrap
                   :trigger     trigger
                   :tooltip     tooltip
                   :tooltipSlot tooltip-slot
                   :tooltipText tooltip-text})))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Attribute readers ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:text-raw            (du/get-attr el model/attr-text)
    :from-raw            (du/get-attr el model/attr-from)
    :from-attr-raw       (du/get-attr el model/attr-from-attr)
    :mode-raw            (du/get-attr el model/attr-mode)
    :disabled-present?   (du/has-attr? el model/attr-disabled)
    :show-tooltip-raw    (du/get-attr el model/attr-show-tooltip)
    :tooltip-ms-raw      (du/get-attr el model/attr-tooltip-ms)
    :success-message-raw (du/get-attr el model/attr-success-message)
    :error-message-raw   (du/get-attr el model/attr-error-message)
    :hotkey-raw          (du/get-attr el model/attr-hotkey)}))

;; ── Text resolution ───────────────────────────────────────────────────────────
(defn- resolve-text [^js el]
  (let [tv (du/getv el k-text-val)]
    (if (string? tv)
      tv
      (let [text-attr (du/get-attr el model/attr-text)]
        (if (and (string? text-attr) (not= text-attr ""))
          text-attr
          (let [from-sel (du/get-attr el model/attr-from)]
            (if (and (string? from-sel) (not= from-sel ""))
              (let [^js target (.querySelector js/document from-sel)]
                (if target
                  (let [from-attr (du/get-attr el model/attr-from-attr)]
                    (if (and (string? from-attr) (not= from-attr ""))
                      (or (.getAttribute target from-attr) "")
                      (or (.-textContent target) "")))
                  ""))
              "")))))))

;; ── Clipboard logic ───────────────────────────────────────────────────────────
(defn- copy-via-textarea!
  "Fallback path: write to a transient textarea + document.execCommand('copy').
  Used when navigator.clipboard isn't available, or when ClipboardItem
  isn't supported but the user requested HTML mode."
  [text]
  (let [^js ta (make-el js/document "textarea")]
    (set! (.-value ta) text)
    (set! (.. ta -style -position) "fixed")
    (set! (.. ta -style -opacity) "0")
    (.appendChild (.-body js/document) ta)
    (.select ta)
    (.execCommand js/document cmd-copy)
    (.remove ta)))

(defn- make-clipboard-item-payload
  "Build the {mime-type -> Blob} JS object passed to new ClipboardItem.
  Built via gobj/set rather than a `#js {}` literal because the mime-type
  keys are runtime symbols, not read-time literals."
  [text]
  (let [payload #js {}]
    (gobj/set payload mime-text-html  (js/Blob. #js [text] #js {:type mime-text-html}))
    (gobj/set payload mime-text-plain (js/Blob. #js [text] #js {:type mime-text-plain}))
    payload))

(defn- copy-html!
  "Copy `text` as both text/html and text/plain via navigator.clipboard.write
  when available; otherwise fall back to the textarea path. Resolves with
  `text` so callers can chain .then to read it back."
  [text]
  (if (and (.-clipboard js/navigator) (.-ClipboardItem js/window))
    (.then (.write (.-clipboard js/navigator)
                   (clj->js [(js/ClipboardItem. (make-clipboard-item-payload text))]))
           (fn html-write-resolved [_] text))
    (js/Promise.
     (fn handle-html-fallback [resolve reject]
       (try (copy-via-textarea! text) (resolve text)
            (catch js/Error e (reject e)))))))

(defn- copy-text!
  "Copy `text` as text/plain via navigator.clipboard.writeText when available;
  on failure (or when unavailable) fall back to the textarea path. Resolves
  with `text`."
  [text]
  (if (.-clipboard js/navigator)
    (-> (.writeText (.-clipboard js/navigator) text)
        (.then (fn text-write-resolved [_] text))
        (.catch (fn handle-write-fail [e]
                  (js/Promise.
                   (fn handle-text-retry [resolve reject]
                     (try (copy-via-textarea! text) (resolve text)
                          (catch js/Error _ (reject e))))))))
    (js/Promise.
     (fn handle-text-fallback [resolve reject]
       (try (copy-via-textarea! text) (resolve text)
            (catch js/Error e (reject e)))))))

(defn- do-copy! [^js el]
  (let [text (resolve-text el)
        mode (or (du/get-attr el model/attr-mode) mode-text)]
    (if (= mode mode-html)
      (copy-html! text)
      (copy-text! text))))

;; ── Tooltip ───────────────────────────────────────────────────────────────────
(defn- hide-tooltip! [^js el]
  (du/remove-attr! el attr-data-tooltip-open)
  (du/remove-attr! el attr-data-tooltip-kind)
  (let [refs (du/getv el k-refs)]
    (when refs
      (set! (.-textContent (gobj/get refs rk-tooltip-text)) ""))))

(defn- show-tooltip! [^js el kind message tooltip-ms]
  (let [refs (du/getv el k-refs)]
    (when refs
      (when-let [tid (du/getv el k-tid)]
        (js/clearTimeout tid)
        (du/setv! el k-tid nil))
      (du/set-attr! el attr-data-tooltip-open "")
      (du/set-attr! el attr-data-tooltip-kind kind)
      (set! (.-textContent (gobj/get refs rk-tooltip-text)) message)
      (du/setv! el k-tid
                (js/setTimeout
                 (fn on-tooltip-timeout []
                   (du/setv! el k-tid nil)
                   (hide-tooltip! el))
                 tooltip-ms)))))

;; ── Copy handler ──────────────────────────────────────────────────────────────
(defn- copy! [^js el]
  (if (du/has-attr? el model/attr-disabled)
    (js/Promise.resolve nil)
    (let [m          (read-model el)
          req-detail (model/request-detail m)
          allowed? (du/dispatch-cancelable! el model/event-copy-request req-detail)]
      (if (not allowed?)
        (js/Promise.resolve nil)
        (-> (do-copy! el)
            (.then
             (fn [text]
               (du/dispatch! el model/event-copy-success (model/success-detail text))
               (when (:show-tooltip? m)
                 (show-tooltip! el kind-success (:success-message m) (:tooltip-ms m)))
               text))
            (.catch
             (fn [err]
               (du/dispatch! el model/event-copy-error (model/error-detail err))
               (when (:show-tooltip? m)
                 (show-tooltip! el kind-error (:error-message m) (:tooltip-ms m)))
               (js/Promise.reject err))))))))

;; ── Hotkey parsing ────────────────────────────────────────────────────────────
(defn- matches-hotkey? [^js e hotkey]
  ;; hotkey format: optional "ctrl+" "shift+" "alt+" "meta+" then key name
  ;; e.g. "ctrl+c", "ctrl+shift+c", "ctrl+k"
  (when (and (string? hotkey) (not= hotkey ""))
    (let [parts    (.split (.toLowerCase hotkey) "+")
          key-part (last parts)
          need-ctrl  (boolean (some #(= % "ctrl") parts))
          need-shift (boolean (some #(= % "shift") parts))
          need-alt   (boolean (some #(= % "alt") parts))
          need-meta  (boolean (some #(= % "meta") parts))]
      (and (= (.toLowerCase (.-key e)) key-part)
           (= need-ctrl  (.-ctrlKey e))
           (= need-shift (.-shiftKey e))
           (= need-alt   (.-altKey e))
           (= need-meta  (.-metaKey e))))))

;; ── Listener management ───────────────────────────────────────────────────────
(defn- add-listeners! [^js el]
  (let [refs      (ensure-refs! el)
        ^js trigger (gobj/get refs "trigger")
        click-h   (fn [_] (copy! el))
        key-h     (fn [^js e]
                    (let [hotkey (du/get-attr el model/attr-hotkey)]
                      (when (matches-hotkey? e hotkey)
                        (.preventDefault e)
                        (copy! el))))]
    (.addEventListener trigger "click" click-h)
    (.addEventListener js/document "keydown" key-h)
    (du/setv! el k-handlers #js {:click click-h :keydown key-h})))

(defn- remove-listeners! [^js el]
  (when-let [tid (du/getv el k-tid)]
    (js/clearTimeout tid)
    (du/setv! el k-tid nil))
  (let [hs   (du/getv el k-handlers)
        refs (du/getv el k-refs)]
    (when (and hs refs)
      (let [^js trigger (gobj/get refs "trigger")
            click-h    (gobj/get hs "click")
            key-h      (gobj/get hs "keydown")]
        (when click-h (.removeEventListener trigger "click" click-h))
        (when key-h   (.removeEventListener js/document "keydown" key-h)))))
  (du/setv! el k-handlers nil))

;; ── Render ────────────────────────────────────────────────────────────────────
;; The model drives only the disabled state on the trigger + aria-disabled on
;; the host. Other model fields (text, success-message, etc.) are consumed by
;; the click/hotkey handlers via read-model and don't need to project to DOM.
(defn- apply-model! [^js el m]
  (let [refs        (ensure-refs! el)
        ^js trigger (gobj/get refs rk-trigger)
        disabled?   (:disabled? m)]
    (set! (.-disabled trigger) (boolean disabled?))
    (if disabled?
      (du/set-attr!    el attr-aria-disabled val-true)
      (du/remove-attr! el attr-aria-disabled))
    (du/setv! el k-model m)))

(defn- update-from-attrs! [^js el]
  (when (du/getv el k-refs)
    (let [new-m (read-model el)
          old-m (du/getv el k-model)]
      (when (not= old-m new-m)
        (apply-model! el new-m)))))

;; ── Lifecycle ─────────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Property accessors ────────────────────────────────────────────────────────
(defn- install-properties! [^js proto]
  ;; String-reflecting properties
  (du/define-string-prop! proto "text"           model/attr-text)
  (du/define-string-prop! proto "from"           model/attr-from)
  (du/define-string-prop! proto "fromAttr"       model/attr-from-attr)
  (du/define-string-prop! proto "mode"           model/attr-mode)
  (du/define-string-prop! proto "successMessage" model/attr-success-message)
  (du/define-string-prop! proto "errorMessage"   model/attr-error-message)
  (du/define-string-prop! proto "hotkey"         model/attr-hotkey)

  ;; Boolean-reflecting property
  (du/define-bool-prop!   proto "disabled"       model/attr-disabled)

  (.defineProperty js/Object proto "showTooltip"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-bool-default-true
                                         (.getAttribute this model/attr-show-tooltip))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (du/set-attr! this model/attr-show-tooltip "true")
                                          (du/set-attr! this model/attr-show-tooltip "false"))))
                        :enumerable true :configurable true})

  ;; Number-reflecting property
  (.defineProperty js/Object proto "tooltipMs"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-int-pos
                                         (.getAttribute this model/attr-tooltip-ms)
                                         1200)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (du/remove-attr! this model/attr-tooltip-ms)
                                          (du/set-attr! this model/attr-tooltip-ms (str (int v))))))
                        :enumerable true :configurable true})

  ;; textValue — property-only, stored in gobj field
  (.defineProperty js/Object proto "textValue"
                   #js {:get (fn []
                               (this-as ^js this
                                        (gobj/get this k-text-val)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (string? v)
                                          (gobj/set this k-text-val v)
                                          (gobj/set this k-text-val js/undefined))))
                        :enumerable true :configurable true})

  ;; Public method: copy() → Promise
  (.defineProperty js/Object proto "copy"
                   #js {:value (fn []
                                 (this-as ^js this
                                          (copy! this)))
                        :enumerable true :configurable true :writable true}))

;; ── Element class ─────────────────────────────────────────────────────────────
;; ── Public API ────────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-properties!}))
