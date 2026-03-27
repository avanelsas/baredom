(ns baredom.components.x-file-download.x-file-download
  (:require [goog.object :as gobj]
            [baredom.components.x-file-download.model :as model]))

;; ---------------------------------------------------------------------------
;; Instance field keys (always use gobj/get, gobj/set)
;; ---------------------------------------------------------------------------
(def ^:private k-refs     "__xFileDownloadRefs")
(def ^:private k-handlers "__xFileDownloadHandlers")

;; ---------------------------------------------------------------------------
;; Style
;; ---------------------------------------------------------------------------
(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;"
   "color-scheme:light dark;"
   "--x-file-download-bg:#2563eb;"
   "--x-file-download-color:#ffffff;"
   "--x-file-download-hover-bg:#1d4ed8;"
   "--x-file-download-active-bg:#1e40af;"
   "--x-file-download-border-radius:6px;"
   "--x-file-download-padding:0.5rem 1rem;"
   "--x-file-download-font-size:0.875rem;"
   "--x-file-download-font-weight:500;"
   "--x-file-download-gap:0.375rem;"
   "--x-file-download-icon-size:1em;"
   "--x-file-download-focus-ring:#60a5fa;"
   "--x-file-download-disabled-opacity:0.45;"
   "--x-file-download-transition:background 120ms ease;"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-file-download-bg:#3b82f6;"
   "--x-file-download-hover-bg:#2563eb;"
   "--x-file-download-active-bg:#1d4ed8;"
   "--x-file-download-focus-ring:#93c5fd;"
   "}"
   "}"
   "[part=anchor]{"
   "display:inline-flex;"
   "align-items:center;"
   "gap:var(--x-file-download-gap);"
   "padding:var(--x-file-download-padding);"
   "background:var(--x-file-download-bg);"
   "color:var(--x-file-download-color);"
   "border-radius:var(--x-file-download-border-radius);"
   "font-size:var(--x-file-download-font-size);"
   "font-weight:var(--x-file-download-font-weight);"
   "text-decoration:none;"
   "cursor:pointer;"
   "box-sizing:border-box;"
   "transition:var(--x-file-download-transition);"
   "outline:none;"
   "user-select:none;"
   "-webkit-user-select:none;"
   "}"
   "[part=anchor]:hover{"
   "background:var(--x-file-download-hover-bg);"
   "}"
   "[part=anchor]:active{"
   "background:var(--x-file-download-active-bg);"
   "}"
   "[part=anchor]:focus-visible{"
   "outline:2px solid var(--x-file-download-focus-ring);"
   "outline-offset:2px;"
   "}"
   "[part=icon]{"
   "display:inline-flex;"
   "align-items:center;"
   "justify-content:center;"
   "width:var(--x-file-download-icon-size);"
   "height:var(--x-file-download-icon-size);"
   "flex-shrink:0;"
   "}"
   "[part=content]{"
   "display:inline;"
   "}"
   ":host([data-disabled]) [part=anchor]{"
   "opacity:var(--x-file-download-disabled-opacity);"
   "pointer-events:none;"
   "cursor:default;"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=anchor]{transition:none;}"
   "}"))

;; ---------------------------------------------------------------------------
;; Download icon SVG
;; ---------------------------------------------------------------------------
(def ^:private download-icon-svg
  "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 16 16\" fill=\"currentColor\" width=\"1em\" height=\"1em\" aria-hidden=\"true\"><path d=\"M8 1a.75.75 0 0 1 .75.75v6.19l1.97-1.97a.75.75 0 1 1 1.06 1.06l-3.25 3.25a.75.75 0 0 1-1.06 0L4.22 7.03a.75.75 0 0 1 1.06-1.06L7.25 7.94V1.75A.75.75 0 0 1 8 1ZM2.5 13.25a.75.75 0 0 1 .75-.75h9.5a.75.75 0 0 1 0 1.5h-9.5a.75.75 0 0 1-.75-.75Z\"/></svg>")

;; ---------------------------------------------------------------------------
;; DOM helpers
;; ---------------------------------------------------------------------------
(defn- make-el [tag]
  (.createElement js/document tag))

(defn- set-attr! [^js el attr val]
  (.setAttribute el attr val))

(defn- remove-attr! [^js el attr]
  (.removeAttribute el attr))

(defn- has-attr? [^js el attr]
  (.hasAttribute el attr))

(defn- get-attr [^js el attr]
  (.getAttribute el attr))

(defn- set-bool-attr! [^js el attr value]
  (if value (set-attr! el attr "") (remove-attr! el attr)))

;; ---------------------------------------------------------------------------
;; Shadow DOM construction
;; ---------------------------------------------------------------------------
(defn- make-shadow! [^js el]
  (let [root       (.attachShadow el #js {:mode "open"})
        style-el   (make-el "style")
        anchor-el  (make-el "a")
        icon-el    (make-el "span")
        content-el (make-el "span")
        slot-el    (make-el "slot")]

    (set! (.-textContent style-el) style-text)

    (set-attr! anchor-el  "part" "anchor")
    (set-attr! icon-el    "part" "icon")
    (set-attr! content-el "part" "content")

    ;; Set static download icon
    (set! (.-innerHTML icon-el) download-icon-svg)

    (.appendChild content-el slot-el)
    (.appendChild anchor-el icon-el)
    (.appendChild anchor-el content-el)
    (.appendChild root style-el)
    (.appendChild root anchor-el)

    (let [refs #js {:anchor-el anchor-el :icon-el icon-el :content-el content-el}]
      (gobj/set el k-refs refs)
      refs)))

;; ---------------------------------------------------------------------------
;; Read element state from attributes
;; ---------------------------------------------------------------------------
(defn- read-model [^js el]
  (model/normalize
   {:href-raw          (get-attr el model/attr-href)
    :filename-raw      (get-attr el model/attr-filename)
    :disabled-present? (has-attr? el model/attr-disabled)
    :aria-label-raw    (get-attr el model/attr-aria-label)}))

;; ---------------------------------------------------------------------------
;; Render
;; ---------------------------------------------------------------------------
(defn- render! [^js el]
  (when-let [refs (gobj/get el k-refs)]
    (let [^js anchor-el (gobj/get refs "anchor-el")
          m             (read-model el)
          href          (:href m)
          filename      (:filename m)
          disabled?     (:disabled? m)
          aria-label    (:aria-label m)]

      ;; Set href on anchor
      (set! (.-href anchor-el) href)

      ;; Set or remove download attribute.
      ;; data: URLs must always carry the download attribute — browsers block
      ;; top-frame navigation to data URLs, so without it clicking fails.
      (cond
        (and (string? filename) (not= filename ""))
        (set-attr! anchor-el "download" filename)

        (model/data-url? href)
        (set-attr! anchor-el "download" "")

        :else
        (remove-attr! anchor-el "download"))

      ;; aria-disabled on anchor
      (if disabled?
        (set-attr! anchor-el "aria-disabled" "true")
        (remove-attr! anchor-el "aria-disabled"))

      ;; aria-label forwarded to anchor
      (if aria-label
        (set-attr! anchor-el "aria-label" aria-label)
        (remove-attr! anchor-el "aria-label"))

      ;; data-disabled on host for CSS hooks
      (set-bool-attr! el "data-disabled" disabled?))))

;; ---------------------------------------------------------------------------
;; Click handler
;; ---------------------------------------------------------------------------
(defn- make-click-handler [^js el]
  (fn [^js evt]
    (let [disabled? (has-attr? el model/attr-disabled)]
      (if disabled?
        (.preventDefault evt)
        (let [m          (read-model el)
              detail     #js {:href (:href m) :filename (:filename m)}
              ^js custom (js/CustomEvent.
                          model/event-click
                          #js {:detail     detail
                               :bubbles    true
                               :composed   true
                               :cancelable true})]
          (.dispatchEvent el custom)
          (when (.-defaultPrevented custom)
            (.preventDefault evt)))))))

;; ---------------------------------------------------------------------------
;; Listener management
;; ---------------------------------------------------------------------------
(defn- add-listeners! [^js el]
  (let [refs      (gobj/get el k-refs)
        ^js anchor (gobj/get refs "anchor-el")
        click-h   (make-click-handler el)]
    (.addEventListener anchor "click" click-h)
    (gobj/set el k-handlers #js {:click click-h}))
  nil)

(defn- remove-listeners! [^js el]
  (let [hs   (gobj/get el k-handlers)
        refs (gobj/get el k-refs)]
    (when (and hs refs)
      (let [^js anchor  (gobj/get refs "anchor-el")
            click-h     (gobj/get hs "click")]
        (when click-h (.removeEventListener anchor "click" click-h)))))
  (gobj/set el k-handlers nil)
  nil)

;; ---------------------------------------------------------------------------
;; Lifecycle
;; ---------------------------------------------------------------------------
(defn- connected! [^js el]
  (when-not (gobj/get el k-refs)
    (make-shadow! el))
  (remove-listeners! el)
  (add-listeners! el)
  (render! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name _old _new]
  (render! el))

;; ---------------------------------------------------------------------------
;; Property helpers
;; ---------------------------------------------------------------------------
(defn- define-string-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (or (get-attr this attr-name) "")))
        :set (fn [v] (this-as ^js this
                              (if (and (some? v) (not= v js/undefined))
                                (set-attr! this attr-name (str v))
                                (remove-attr! this attr-name))))}))

(defn- define-bool-prop! [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (has-attr? this attr-name)))
        :set (fn [v] (this-as ^js this (set-bool-attr! this attr-name (boolean v))))}))

;; ---------------------------------------------------------------------------
;; Element class and registration
;; ---------------------------------------------------------------------------
(defn- element-class []
  (let [cls   (js* "(class extends HTMLElement {})")
        proto (.-prototype cls)]

    ;; observedAttributes
    (.defineProperty js/Object cls "observedAttributes"
                     #js {:get (fn [] model/observed-attributes)})

    ;; String properties
    (define-string-prop! proto "href"     model/attr-href)
    (define-string-prop! proto "filename" model/attr-filename)

    ;; Boolean properties
    (define-bool-prop! proto "disabled" model/attr-disabled)

    ;; Lifecycle
    (aset proto "connectedCallback"
          (fn [] (this-as ^js this (connected! this))))

    (aset proto "disconnectedCallback"
          (fn [] (this-as ^js this (disconnected! this))))

    (aset proto "attributeChangedCallback"
          (fn [n o v] (this-as ^js this (attribute-changed! this n o v))))

    cls))

(defn init! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class))))
