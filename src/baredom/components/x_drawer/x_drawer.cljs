(ns baredom.components.x-drawer.x-drawer
  (:require
[baredom.utils.dom :as du]
               [goog.object :as gobj]
   [baredom.components.x-drawer.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────────
(def ^:private k-refs     "__xDrawerRefs")
(def ^:private k-handlers "__xDrawerHandlers")
(def ^:private k-prev-open "__xDrawerPrevOpen")
(def ^:private k-restore   "__xDrawerRestore")
(def ^:private k-tabbables "__xDrawerTabbables")
(def ^:private k-panel-tab "__xDrawerPanelTab")

;; ── Styles ────────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:contents;"
   "color-scheme:light dark;"
   "--x-drawer-size:20rem;"
   "--x-drawer-bg:var(--x-color-bg,Canvas);"
   "--x-drawer-fg:var(--x-color-text,CanvasText);"
   "--x-drawer-backdrop:rgb(0 0 0/0.4);"
   "--x-drawer-shadow:var(--x-shadow-lg,0 8px 24px rgb(0 0 0/0.18));"
   "--x-drawer-duration:var(--x-transition-duration,200ms);"
   "--x-drawer-easing:ease;"
   "--x-drawer-z:1000;"
   "--x-drawer-header-padding:1rem 1.25rem;"
   "--x-drawer-body-padding:1rem 1.25rem;"
   "--x-drawer-footer-padding:0.75rem 1.25rem;"
   "--x-drawer-border:var(--x-color-border,color-mix(in srgb,currentColor 12%,transparent));"
   "}"
   "[part=backdrop]{"
   "display:none;"
   "position:fixed;"
   "inset:0;"
   "background:var(--x-drawer-backdrop);"
   "z-index:var(--x-drawer-z);"
   "}"
   ":host([data-open=true]) [part=backdrop]{"
   "display:block;"
   "}"
   "[part=panel]{"
   "position:fixed;"
   "background:var(--x-drawer-bg);"
   "color:var(--x-drawer-fg);"
   "box-shadow:var(--x-drawer-shadow);"
   "display:flex;"
   "flex-direction:column;"
   "overflow:hidden;"
   "z-index:calc(var(--x-drawer-z) + 1);"
   "transition:transform var(--x-drawer-duration) var(--x-drawer-easing);"
   "}"
   ":host([data-placement=left]) [part=panel],"
   ":host([data-placement=right]) [part=panel]{"
   "top:0;bottom:0;"
   "width:var(--x-drawer-size);"
   "max-width:100%;"
   "}"
   ":host([data-placement=left]) [part=panel]{"
   "left:0;"
   "transform:translateX(-100%);"
   "}"
   ":host([data-placement=right]) [part=panel]{"
   "right:0;"
   "transform:translateX(100%);"
   "}"
   ":host([data-placement=top]) [part=panel],"
   ":host([data-placement=bottom]) [part=panel]{"
   "left:0;right:0;"
   "height:var(--x-drawer-size);"
   "max-height:100dvh;"
   "}"
   ":host([data-placement=top]) [part=panel]{"
   "top:0;"
   "transform:translateY(-100%);"
   "}"
   ":host([data-placement=bottom]) [part=panel]{"
   "bottom:0;"
   "transform:translateY(100%);"
   "}"
   ":host([data-open=true]) [part=panel]{"
   "transform:none;"
   "}"
   "[part=panel]:focus{"
   "outline:none;"
   "}"
   "[part=header]{"
   "padding:var(--x-drawer-header-padding);"
   "border-bottom:1px solid var(--x-drawer-border);"
   "flex-shrink:0;"
   "}"
   "[part=body]{"
   "padding:var(--x-drawer-body-padding);"
   "overflow-y:auto;"
   "flex:1;"
   "}"
   "[part=footer]{"
   "padding:var(--x-drawer-footer-padding);"
   "border-top:1px solid var(--x-drawer-border);"
   "flex-shrink:0;"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=panel]{transition:none;}"
   "}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-drawer-bg:var(--x-color-bg,#1c1d24);"
   "--x-drawer-fg:var(--x-color-text,#e2e4ef);"
   "--x-drawer-border:var(--x-color-border,rgb(255 255 255/0.08));"
   "--x-drawer-shadow:var(--x-shadow-lg,0 8px 40px rgb(0 0 0/0.55));"
   "--x-drawer-backdrop:rgb(0 0 0/0.55);"
   "}"
   "}"))

;; ── DOM helpers ───────────────────────────────────────────────────────────────
(defn- make-el [tag]
  (.createElement js/document tag))

;; ── Shadow DOM initialisation ─────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root     (.attachShadow el #js {:mode "open"})
        style    (make-el "style")
        backdrop (make-el "div")
        panel    (make-el "div")
        header   (make-el "div")
        hslot    (make-el "slot")
        body     (make-el "div")
        bslot    (make-el "slot")
        footer   (make-el "div")
        fslot    (make-el "slot")]

    (set! (.-textContent style) style-text)

    (.setAttribute backdrop "part" model/part-backdrop)

    (.setAttribute panel "part" model/part-panel)
    (.setAttribute panel "role" "dialog")
    (.setAttribute panel "aria-modal" "true")

    (.setAttribute header "part" model/part-header)
    (.setAttribute hslot "name" "header")

    (.setAttribute body "part" model/part-body)

    (.setAttribute footer "part" model/part-footer)
    (.setAttribute fslot "name" "footer")

    (.appendChild header hslot)
    (.appendChild body bslot)
    (.appendChild footer fslot)
    (.appendChild panel header)
    (.appendChild panel body)
    (.appendChild panel footer)

    (.appendChild root style)
    (.appendChild root backdrop)
    (.appendChild root panel)

    (gobj/set el k-refs
              #js {:root     root
                   :backdrop backdrop
                   :panel    panel
                   :header   header
                   :body     body
                   :footer   footer}))
  nil)

(defn- ensure-refs! [^js el]
  (or (gobj/get el k-refs)
      (do (init-dom! el)
          (gobj/get el k-refs))))

;; ── Attribute readers ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:open-present? (.hasAttribute el model/attr-open)
    :placement-raw (.getAttribute el model/attr-placement)
    :label-raw     (.getAttribute el model/attr-label)}))

;; ── Event dispatch ────────────────────────────────────────────────────────────
;; ── Focus trap ────────────────────────────────────────────────────────────────
(defn- collect-tabbables [^js el]
  ;; Query the host element's light DOM — slotted content lives there,
  ;; not inside the shadow tree, so querying the shadow panel div would
  ;; always return [].
  (let [sel (str "a[href],button:not([disabled]),input:not([disabled]),"
                 "select:not([disabled]),textarea:not([disabled]),"
                 "[tabindex]:not([tabindex='-1'])")]
    (->> (array-seq (.querySelectorAll el sel))
         (filter (fn [^js node]
                   (let [^js s (.getComputedStyle js/window node)]
                     (and (not= "none" (.-display s))
                          (not= "hidden" (.-visibility s))
                          (pos? (.-offsetWidth node))
                          (pos? (.-offsetHeight node))))))
         vec)))

(defn- activate-focus-trap! [^js el]
  (let [refs      (gobj/get el k-refs)
        ^js panel (when refs (gobj/get refs "panel"))
        tabbables (collect-tabbables el)]
    (gobj/set el k-restore (.-activeElement js/document))
    (gobj/set el k-tabbables (when tabbables (clj->js tabbables)))
    (if (seq tabbables)
      (.focus (first tabbables))
      (when panel
        (when-not (.hasAttribute panel "tabindex")
          (.setAttribute panel "tabindex" "-1")
          (gobj/set el k-panel-tab true))
        (.focus panel))))
  nil)

(defn- deactivate-focus-trap! [^js el]
  (let [refs           (gobj/get el k-refs)
        ^js panel      (when refs (gobj/get refs "panel"))
        restore        (gobj/get el k-restore)
        panel-tab-added (true? (gobj/get el k-panel-tab))]
    (gobj/set el k-tabbables nil)
    (gobj/set el k-restore nil)
    (when (and panel panel-tab-added)
      (.removeAttribute panel "tabindex")
      (gobj/set el k-panel-tab false))
    (when (and restore (.-isConnected restore))
      (.focus restore)))
  nil)

(defn- cycle-focus! [^js el ^js e]
  (let [tabbables-js (gobj/get el k-tabbables)
        tabbables    (if tabbables-js (vec (array-seq tabbables-js)) [])
        refs         (gobj/get el k-refs)
        ^js panel    (when refs (gobj/get refs "panel"))]
    (if (empty? tabbables)
      (do (.preventDefault e)
          (when panel (.focus panel)))
      (let [active   (.-activeElement js/document)
            first-el (first tabbables)
            last-el  (last tabbables)
            shift?   (.-shiftKey e)]
        (cond
          (and shift? (= active first-el))
          (do (.preventDefault e) (.focus last-el))

          (and (not shift?) (= active last-el))
          (do (.preventDefault e) (.focus first-el))

          :else nil))))
  nil)

;; ── Dismiss (user-initiated close) ───────────────────────────────────────────
(defn- do-dismiss! [^js el reason]
  (du/dispatch! el model/event-dismiss (model/dismiss-event-detail reason))
  (.removeAttribute el model/attr-open)
  nil)

;; ── Show / hide / toggle ─────────────────────────────────────────────────────
(defn- do-show! [^js el]
  (when-not (.hasAttribute el model/attr-open)
    (.setAttribute el model/attr-open ""))
  nil)

(defn- do-hide! [^js el]
  (.removeAttribute el model/attr-open)
  nil)

(defn- do-toggle! [^js el]
  (if (.hasAttribute el model/attr-open)
    (do-hide! el)
    (do-show! el))
  nil)

;; ── Render ────────────────────────────────────────────────────────────────────
(defn- render! [^js el]
  (let [refs      (ensure-refs! el)
        ^js panel (gobj/get refs "panel")
        m         (read-model el)
        open?     (:open? m)
        prev-open (gobj/get el k-prev-open)]

    ;; Apply data attributes to host for CSS selectors
    (.setAttribute el "data-open" (if open? "true" "false"))
    (.setAttribute el "data-placement" (:placement m))

    ;; aria-label on panel
    (.setAttribute panel "aria-label" (:label m))

    ;; Hide panel from AT when closed so screen readers cannot navigate
    ;; into off-screen drawer content (CSS transform alone is insufficient)
    (if open?
      (.removeAttribute panel "aria-hidden")
      (.setAttribute    panel "aria-hidden" "true"))

    ;; Detect open state transition
    (when (not= prev-open open?)
      (gobj/set el k-prev-open open?)
      (du/dispatch! el model/event-toggle (model/toggle-event-detail open?))
      (if open?
        (js/setTimeout (fn [] (activate-focus-trap! el)) 0)
        (deactivate-focus-trap! el))))
  nil)

;; ── Listener management ───────────────────────────────────────────────────────
(defn- on-keydown! [^js el ^js e]
  (let [key (.-key e)]
    (cond
      (= key "Escape")
      (do (.preventDefault e)
          (do-dismiss! el model/reason-escape))

      (= key "Tab")
      (cycle-focus! el e)))
  nil)

(defn- add-listeners! [^js el]
  (let [refs         (ensure-refs! el)
        ^js backdrop (gobj/get refs "backdrop")
        ^js panel    (gobj/get refs "panel")
        backdrop-h   (fn [_] (do-dismiss! el model/reason-backdrop))
        keydown-h    (fn [^js e] (on-keydown! el e))]
    (.addEventListener backdrop "click" backdrop-h)
    (.addEventListener panel "keydown" keydown-h)
    (gobj/set el k-handlers
              #js {:backdrop backdrop-h
                   :keydown  keydown-h}))
  nil)

(defn- remove-listeners! [^js el]
  (let [hs   (gobj/get el k-handlers)
        refs (gobj/get el k-refs)]
    (when (and hs refs)
      (let [^js backdrop (gobj/get refs "backdrop")
            ^js panel    (gobj/get refs "panel")
            backdrop-h   (gobj/get hs "backdrop")
            keydown-h    (gobj/get hs "keydown")]
        (when backdrop-h (.removeEventListener backdrop "click" backdrop-h))
        (when keydown-h  (.removeEventListener panel "keydown" keydown-h)))))
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
  (deactivate-focus-trap! el)
  nil)

(defn- attribute-changed! [^js el _attr-name old-val new-val]
  (when (not= old-val new-val)
    (render! el))
  nil)

;; ── Property accessors ────────────────────────────────────────────────────────
(defn- install-properties! [^js proto]
  ;; Boolean property: open
  (.defineProperty js/Object proto "open"
                   #js {:get (fn []
                               (this-as ^js this
                                        (.hasAttribute this model/attr-open)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (do-show! this)
                                          (do-hide! this))))
                        :enumerable true :configurable true})

  ;; String property: placement
  (.defineProperty js/Object proto "placement"
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-placement)
                                            model/default-placement)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-placement)
                                          (.setAttribute this model/attr-placement (str v)))))
                        :enumerable true :configurable true})

  ;; String property: label
  (.defineProperty js/Object proto "label"
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-label)
                                            model/default-label)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-label)
                                          (.setAttribute this model/attr-label (str v)))))
                        :enumerable true :configurable true})

  ;; Public methods
  (.defineProperty js/Object proto "show"
                   #js {:value (fn []
                                 (this-as ^js this (do-show! this)))
                        :enumerable true :configurable true :writable true})

  (.defineProperty js/Object proto "hide"
                   #js {:value (fn []
                                 (this-as ^js this (do-hide! this)))
                        :enumerable true :configurable true :writable true})

  (.defineProperty js/Object proto "toggle"
                   #js {:value (fn []
                                 (this-as ^js this (do-toggle! this)))
                        :enumerable true :configurable true :writable true}))

;; ── Element class ─────────────────────────────────────────────────────────────
(defn- define-element! []
  (when-not (.get js/customElements model/tag-name)
    (let [klass (js* "(class extends HTMLElement {})")]
      (set! (.-observedAttributes klass) model/observed-attributes)
      (set! (.-connectedCallback (.-prototype klass))
            (fn [] (this-as ^js this (connected! this))))
      (set! (.-disconnectedCallback (.-prototype klass))
            (fn [] (this-as ^js this (disconnected! this))))
      (set! (.-attributeChangedCallback (.-prototype klass))
            (fn [n o v] (this-as ^js this (attribute-changed! this n o v))))
      (install-properties! (.-prototype klass))
      (.define js/customElements model/tag-name klass)))
  nil)

;; ── Public API ────────────────────────────────────────────────────────────────
(defn register! []
  (define-element!))

(defn init! []
  (register!))
