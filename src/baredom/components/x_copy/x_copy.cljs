(ns baredom.components.x-copy.x-copy
  (:require
[baredom.utils.dom :as du]
               [goog.object :as gobj]
   [baredom.components.x-copy.model :as model]))

;; ── Instance-field keys (always use gobj/get, gobj/set) ─────────────────────
(def ^:private k-refs      "__xCopyRefs")
(def ^:private k-handlers  "__xCopyHandlers")
(def ^:private k-tid       "__xCopyTid")
(def ^:private k-text-val  "__xCopyTextValue")

;; ── Styles ───────────────────────────────────────────────────────────────────
(def style-text
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

    (.setAttribute wrap "part" "wrap")

    (.setAttribute trigger "part" "trigger")
    (.setAttribute trigger "type" "button")

    (.appendChild trigger default-slot)

    (.setAttribute tooltip "part" "tooltip")
    (.setAttribute tooltip "role" "status")
    (.setAttribute tooltip "aria-live" "polite")

    (.setAttribute tooltip-slot "name" "tooltip")
    (.setAttribute tooltip-text "part" "tooltip-text")

    (.appendChild tooltip tooltip-slot)
    (.appendChild tooltip tooltip-text)

    (.appendChild wrap trigger)
    (.appendChild wrap tooltip)

    (.appendChild root style)
    (.appendChild root wrap)

    (gobj/set el k-refs
              #js {:root        root
                   :wrap        wrap
                   :trigger     trigger
                   :tooltip     tooltip
                   :tooltipSlot tooltip-slot
                   :tooltipText tooltip-text}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:text-raw            (.getAttribute el model/attr-text)
    :from-raw            (.getAttribute el model/attr-from)
    :from-attr-raw       (.getAttribute el model/attr-from-attr)
    :mode-raw            (.getAttribute el model/attr-mode)
    :disabled-present?   (.hasAttribute el model/attr-disabled)
    :show-tooltip-raw    (.getAttribute el model/attr-show-tooltip)
    :tooltip-ms-raw      (.getAttribute el model/attr-tooltip-ms)
    :success-message-raw (.getAttribute el model/attr-success-message)
    :error-message-raw   (.getAttribute el model/attr-error-message)
    :hotkey-raw          (.getAttribute el model/attr-hotkey)}))

;; ── Text resolution ───────────────────────────────────────────────────────────
(defn- resolve-text [^js el]
  (let [tv (gobj/get el k-text-val)]
    (if (string? tv)
      tv
      (let [text-attr (.getAttribute el model/attr-text)]
        (if (and (string? text-attr) (not= text-attr ""))
          text-attr
          (let [from-sel (.getAttribute el model/attr-from)]
            (if (and (string? from-sel) (not= from-sel ""))
              (let [^js target (.querySelector js/document from-sel)]
                (if target
                  (let [from-attr (.getAttribute el model/attr-from-attr)]
                    (if (and (string? from-attr) (not= from-attr ""))
                      (or (.getAttribute target from-attr) "")
                      (or (.-textContent target) "")))
                  ""))
              "")))))))

;; ── Clipboard logic ───────────────────────────────────────────────────────────
(defn- do-copy! [^js el]
  (let [text (resolve-text el)
        mode (or (.getAttribute el model/attr-mode) "text")]
    (js/Promise.
     (fn [resolve reject]
       (cond
         (= mode "html")
         (if (and (.-clipboard js/navigator)
                  (.-ClipboardItem js/window))
           (-> (.write (.-clipboard js/navigator)
                       (clj->js [(js/ClipboardItem.
                                  #js {"text/html"  (js/Blob. #js [text] #js {:type "text/html"})
                                       "text/plain" (js/Blob. #js [text] #js {:type "text/plain"})})]))
               (.then (fn [] (resolve text)))
               (.catch (fn [e] (reject e))))
           (try
             (let [^js ta (make-el js/document "textarea")]
               (set! (.-value ta) text)
               (.appendChild (.-body js/document) ta)
               (.select ta)
               (.execCommand js/document "copy")
               (.remove ta)
               (resolve text))
             (catch js/Error e (reject e))))

         :else
         (if (.-clipboard js/navigator)
           (-> (.writeText (.-clipboard js/navigator) text)
               (.then (fn [] (resolve text)))
               (.catch (fn [e]
                         (try
                           (.execCommand js/document "copy")
                           (resolve text)
                           (catch js/Error _ (reject e))))))
           (try
             (let [^js ta (make-el js/document "textarea")]
               (set! (.-value ta) text)
               (set! (.. ta -style -position) "fixed")
               (set! (.. ta -style -opacity) "0")
               (.appendChild (.-body js/document) ta)
               (.select ta)
               (.execCommand js/document "copy")
               (.remove ta)
               (resolve text))
             (catch js/Error e (reject e)))))))))

;; ── Tooltip ───────────────────────────────────────────────────────────────────
(defn- hide-tooltip! [^js el]
  (.removeAttribute el "data-tooltip-open")
  (.removeAttribute el "data-tooltip-kind")
  (let [refs (gobj/get el k-refs)]
    (when refs
      (set! (.-textContent (gobj/get refs "tooltipText")) "")))
  nil)

(defn- show-tooltip! [^js el kind message tooltip-ms]
  (let [refs (gobj/get el k-refs)]
    (when refs
      (when-let [tid (gobj/get el k-tid)]
        (js/clearTimeout tid)
        (gobj/set el k-tid nil))
      (.setAttribute el "data-tooltip-open" "")
      (.setAttribute el "data-tooltip-kind" kind)
      (set! (.-textContent (gobj/get refs "tooltipText")) message)
      (gobj/set el k-tid
                (js/setTimeout
                 (fn []
                   (gobj/set el k-tid nil)
                   (hide-tooltip! el))
                 tooltip-ms))))
  nil)

;; ── Copy handler ──────────────────────────────────────────────────────────────
(defn- copy! [^js el]
  (if (.hasAttribute el model/attr-disabled)
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
                 (show-tooltip! el "success" (:success-message m) (:tooltip-ms m)))
               text))
            (.catch
             (fn [err]
               (du/dispatch! el model/event-copy-error (model/error-detail err))
               (when (:show-tooltip? m)
                 (show-tooltip! el "error" (:error-message m) (:tooltip-ms m)))
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
                    (let [hotkey (.getAttribute el model/attr-hotkey)]
                      (when (matches-hotkey? e hotkey)
                        (.preventDefault e)
                        (copy! el))))]
    (.addEventListener trigger "click" click-h)
    (.addEventListener js/document "keydown" key-h)
    (gobj/set el k-handlers #js {:click click-h :keydown key-h}))
  nil)

(defn- remove-listeners! [^js el]
  (when-let [tid (gobj/get el k-tid)]
    (js/clearTimeout tid)
    (gobj/set el k-tid nil))
  (let [hs   (gobj/get el k-handlers)
        refs (gobj/get el k-refs)]
    (when (and hs refs)
      (let [^js trigger (gobj/get refs "trigger")
            click-h    (gobj/get hs "click")
            key-h      (gobj/get hs "keydown")]
        (when click-h (.removeEventListener trigger "click" click-h))
        (when key-h   (.removeEventListener js/document "keydown" key-h)))))
  (gobj/set el k-handlers nil)
  nil)

;; ── Render ────────────────────────────────────────────────────────────────────
(defn- render! [^js el]
  (let [refs    (ensure-refs! el)
        ^js trigger (gobj/get refs "trigger")
        disabled? (.hasAttribute el model/attr-disabled)]
    (set! (.-disabled trigger) (boolean disabled?))
    (if disabled?
      (.setAttribute el "aria-disabled" "true")
      (.removeAttribute el "aria-disabled")))
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

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (render! el))
  nil)

;; ── Property accessors ────────────────────────────────────────────────────────
(defn- install-properties! [^js proto]
  ;; String-reflecting properties
  (doseq [[js-prop attr-name]
          [["text"           model/attr-text]
           ["from"           model/attr-from]
           ["fromAttr"       model/attr-from-attr]
           ["mode"           model/attr-mode]
           ["successMessage" model/attr-success-message]
           ["errorMessage"   model/attr-error-message]
           ["hotkey"         model/attr-hotkey]]]
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

  ;; Boolean-reflecting properties
  (.defineProperty js/Object proto "disabled"
                   #js {:get (fn []
                               (this-as ^js this
                                        (.hasAttribute this model/attr-disabled)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-disabled "")
                                          (.removeAttribute this model/attr-disabled))))
                        :enumerable true :configurable true})

  (.defineProperty js/Object proto "showTooltip"
                   #js {:get (fn []
                               (this-as ^js this
                                        (model/parse-bool-default-true
                                         (.getAttribute this model/attr-show-tooltip))))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-show-tooltip "true")
                                          (.setAttribute this model/attr-show-tooltip "false"))))
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
                                          (.removeAttribute this model/attr-tooltip-ms)
                                          (.setAttribute this model/attr-tooltip-ms (str (int v))))))
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
