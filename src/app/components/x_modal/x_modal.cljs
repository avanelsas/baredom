(ns app.components.x-modal.x-modal
  (:require
   [goog.object :as gobj]
   [app.components.x-modal.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────────
(def ^:private k-refs       "__xModalRefs")
(def ^:private k-handlers   "__xModalHandlers")
(def ^:private k-prev-open  "__xModalPrevOpen")
(def ^:private k-restore    "__xModalRestore")
(def ^:private k-tabbables  "__xModalTabbables")
(def ^:private k-dialog-tab "__xModalDialogTab")

;; ── Styles ────────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:contents;"
   "color-scheme:light dark;"
   "--x-modal-bg:Canvas;"
   "--x-modal-fg:CanvasText;"
   "--x-modal-backdrop:rgb(0 0 0/0.45);"
   "--x-modal-shadow:0 20px 60px rgb(0 0 0/0.25);"
   "--x-modal-radius:0.75rem;"
   "--x-modal-width-sm:22rem;"
   "--x-modal-width-md:32rem;"
   "--x-modal-width-lg:44rem;"
   "--x-modal-width-xl:60rem;"
   "--x-modal-max-height:90vh;"
   "--x-modal-header-padding:1rem 1.25rem;"
   "--x-modal-body-padding:1rem 1.25rem;"
   "--x-modal-footer-padding:0.75rem 1.25rem;"
   "--x-modal-border:color-mix(in srgb,currentColor 12%,transparent);"
   "--x-modal-duration:180ms;"
   "--x-modal-easing:ease;"
   "--x-modal-z:1000;"
   "}"
   "[part=backdrop]{"
   "position:fixed;"
   "inset:0;"
   "background:var(--x-modal-backdrop);"
   "z-index:var(--x-modal-z);"
   "opacity:0;"
   "visibility:hidden;"
   "pointer-events:none;"
   "transition:"
   "opacity var(--x-modal-duration) var(--x-modal-easing),"
   "visibility 0s var(--x-modal-easing) var(--x-modal-duration);"
   "}"
   ":host([data-open=true]) [part=backdrop]{"
   "opacity:1;"
   "visibility:visible;"
   "pointer-events:auto;"
   "transition:"
   "opacity var(--x-modal-duration) var(--x-modal-easing),"
   "visibility 0s;"
   "}"
   "[part=dialog]{"
   "position:fixed;"
   "top:50%;"
   "left:50%;"
   "transform:translate(-50%,-50%) scale(0.96);"
   "opacity:0;"
   "visibility:hidden;"
   "pointer-events:none;"
   "background:var(--x-modal-bg);"
   "color:var(--x-modal-fg);"
   "box-shadow:var(--x-modal-shadow);"
   "border-radius:var(--x-modal-radius);"
   "display:flex;"
   "flex-direction:column;"
   "overflow:hidden;"
   "z-index:calc(var(--x-modal-z) + 1);"
   "max-height:var(--x-modal-max-height);"
   "transition:"
   "opacity var(--x-modal-duration) var(--x-modal-easing),"
   "transform var(--x-modal-duration) var(--x-modal-easing),"
   "visibility 0s var(--x-modal-easing) var(--x-modal-duration);"
   "}"
   ":host([data-size=sm]) [part=dialog]{width:var(--x-modal-width-sm);}"
   ":host([data-size=md]) [part=dialog]{width:var(--x-modal-width-md);}"
   ":host([data-size=lg]) [part=dialog]{width:var(--x-modal-width-lg);}"
   ":host([data-size=xl]) [part=dialog]{width:var(--x-modal-width-xl);}"
   ":host([data-size=full]) [part=dialog]{"
   "inset:0;"
   "top:0;"
   "left:0;"
   "width:100vw;"
   "height:100dvh;"
   "border-radius:0;"
   "max-width:100vw;"
   "max-height:100dvh;"
   "transform:scale(0.98);"
   "}"
   ":host([data-open=true]) [part=dialog]{"
   "opacity:1;"
   "transform:translate(-50%,-50%) scale(1);"
   "visibility:visible;"
   "pointer-events:auto;"
   "transition:"
   "opacity var(--x-modal-duration) var(--x-modal-easing),"
   "transform var(--x-modal-duration) var(--x-modal-easing),"
   "visibility 0s;"
   "}"
   ":host([data-open=true][data-size=full]) [part=dialog]{"
   "transform:scale(1);"
   "}"
   "[part=dialog]:focus{"
   "outline:none;"
   "}"
   "[part=header]{"
   "padding:var(--x-modal-header-padding);"
   "border-bottom:1px solid var(--x-modal-border);"
   "flex-shrink:0;"
   "}"
   "[part=body]{"
   "padding:var(--x-modal-body-padding);"
   "overflow-y:auto;"
   "flex:1;"
   "}"
   "[part=footer]{"
   "padding:var(--x-modal-footer-padding);"
   "border-top:1px solid var(--x-modal-border);"
   "flex-shrink:0;"
   "}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=dialog],[part=backdrop]{transition:none;}"
   "}"))

;; ── DOM helpers ───────────────────────────────────────────────────────────────
(defn- make-el [tag]
  (.createElement js/document tag))

;; ── Shadow DOM initialisation ─────────────────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root     (.attachShadow el #js {:mode "open"})
        style    (make-el "style")
        backdrop (make-el "div")
        dialog   (make-el "div")
        header   (make-el "div")
        hslot    (make-el "slot")
        body     (make-el "div")
        bslot    (make-el "slot")
        footer   (make-el "div")
        fslot    (make-el "slot")]

    (set! (.-textContent style) style-text)

    (.setAttribute backdrop "part" model/part-backdrop)

    (.setAttribute dialog "part" model/part-dialog)
    (.setAttribute dialog "role" "dialog")
    (.setAttribute dialog "aria-modal" "true")

    (.setAttribute header "part" model/part-header)
    (.setAttribute hslot "name" "header")

    (.setAttribute body "part" model/part-body)

    (.setAttribute footer "part" model/part-footer)
    (.setAttribute fslot "name" "footer")

    (.appendChild header hslot)
    (.appendChild body bslot)
    (.appendChild footer fslot)
    (.appendChild dialog header)
    (.appendChild dialog body)
    (.appendChild dialog footer)

    (.appendChild root style)
    (.appendChild root backdrop)
    (.appendChild root dialog)

    (gobj/set el k-refs
              #js {:root     root
                   :backdrop backdrop
                   :dialog   dialog
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
    :size-raw      (.getAttribute el model/attr-size)
    :label-raw     (.getAttribute el model/attr-label)}))

;; ── Event dispatch ────────────────────────────────────────────────────────────
(defn- dispatch! [^js el event-name detail]
  (.dispatchEvent el
                  (js/CustomEvent.
                   event-name
                   #js {:detail     detail
                        :bubbles    true
                        :composed   true
                        :cancelable false})))

;; ── Focus trap ────────────────────────────────────────────────────────────────
(defn- collect-tabbables [^js refs]
  (let [sel      (str "a[href],button:not([disabled]),input:not([disabled]),"
                      "select:not([disabled]),textarea:not([disabled]),"
                      "[tabindex]:not([tabindex='-1'])")
        ^js root   (gobj/get refs "root")
        ^js dialog (gobj/get refs "dialog")
        visible?   (fn [^js node]
                     (let [^js s (.getComputedStyle js/window node)]
                       (and (not= "none" (.-display s))
                            (not= "hidden" (.-visibility s))
                            (pos? (.-offsetWidth node))
                            (pos? (.-offsetHeight node)))))
        shadow-els (filter visible? (array-seq (.querySelectorAll dialog sel)))
        slots      (array-seq (.querySelectorAll root "slot"))
        slotted-els (->> slots
                         (mapcat (fn [^js slot]
                                   (->> (array-seq (.assignedElements slot #js {:flatten true}))
                                        (mapcat (fn [^js ae]
                                                  (let [children (array-seq (.querySelectorAll ae sel))]
                                                    (if (.matches ae sel)
                                                      (cons ae children)
                                                      children))))
                                        (filter visible?)))))]
    (vec (concat shadow-els slotted-els))))

(defn- activate-focus-trap! [^js el]
  (let [refs       (gobj/get el k-refs)
        ^js dialog (when refs (gobj/get refs "dialog"))
        tabbables  (when refs (collect-tabbables refs))]
    (gobj/set el k-restore (.-activeElement js/document))
    (gobj/set el k-tabbables (when tabbables (clj->js tabbables)))
    (if (seq tabbables)
      (.focus (first tabbables))
      (when dialog
        (when-not (.hasAttribute dialog "tabindex")
          (.setAttribute dialog "tabindex" "-1")
          (gobj/set el k-dialog-tab true))
        (.focus dialog))))
  nil)

(defn- deactivate-focus-trap! [^js el]
  (let [refs              (gobj/get el k-refs)
        ^js dialog        (when refs (gobj/get refs "dialog"))
        restore           (gobj/get el k-restore)
        dialog-tab-added  (true? (gobj/get el k-dialog-tab))]
    (gobj/set el k-tabbables nil)
    (gobj/set el k-restore nil)
    (when (and dialog dialog-tab-added)
      (.removeAttribute dialog "tabindex")
      (gobj/set el k-dialog-tab false))
    (when (and restore (.-isConnected restore))
      (.focus restore)))
  nil)

(defn- cycle-focus! [^js el ^js e]
  (let [tabbables-js (gobj/get el k-tabbables)
        tabbables    (if tabbables-js (vec (array-seq tabbables-js)) [])
        refs         (gobj/get el k-refs)
        ^js dialog   (when refs (gobj/get refs "dialog"))]
    (if (empty? tabbables)
      (do (.preventDefault e)
          (when dialog (.focus dialog)))
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
  (dispatch! el model/event-dismiss (model/dismiss-event-detail reason))
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
  (let [refs       (ensure-refs! el)
        ^js dialog (gobj/get refs "dialog")
        m          (read-model el)
        open?      (:open? m)
        prev-open  (gobj/get el k-prev-open)]

    ;; Apply data attributes to host for CSS selectors
    (.setAttribute el "data-open" (if open? "true" "false"))
    (.setAttribute el "data-size" (:size m))

    ;; aria-label on dialog
    (.setAttribute dialog "aria-label" (:label m))

    ;; Detect open state transition
    (when (not= prev-open open?)
      (gobj/set el k-prev-open open?)
      (dispatch! el model/event-toggle (model/toggle-event-detail open?))
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
        ^js dialog   (gobj/get refs "dialog")
        backdrop-h   (fn [_] (do-dismiss! el model/reason-backdrop))
        keydown-h    (fn [^js e] (on-keydown! el e))]
    (.addEventListener backdrop "click" backdrop-h)
    (.addEventListener dialog "keydown" keydown-h)
    (gobj/set el k-handlers
              #js {:backdrop backdrop-h
                   :keydown  keydown-h}))
  nil)

(defn- remove-listeners! [^js el]
  (let [hs   (gobj/get el k-handlers)
        refs (gobj/get el k-refs)]
    (when (and hs refs)
      (let [^js backdrop (gobj/get refs "backdrop")
            ^js dialog   (gobj/get refs "dialog")
            backdrop-h   (gobj/get hs "backdrop")
            keydown-h    (gobj/get hs "keydown")]
        (when backdrop-h (.removeEventListener backdrop "click" backdrop-h))
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

  ;; String property: size
  (.defineProperty js/Object proto "size"
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this model/attr-size)
                                            model/default-size)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if (nil? v)
                                          (.removeAttribute this model/attr-size)
                                          (.setAttribute this model/attr-size (str v)))))
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
