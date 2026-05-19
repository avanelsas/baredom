(ns baredom.components.x-modal.x-modal
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-modal.model :as model]))

;; ── Instance-field keys ───────────────────────────────────────────────────────
(def ^:private k-refs       "__xModalRefs")
(def ^:private k-model      "__xModalModel")
(def ^:private k-handlers   "__xModalHandlers")
(def ^:private k-restore    "__xModalRestore")
(def ^:private k-tabbables  "__xModalTabbables")
(def ^:private k-dialog-tab "__xModalDialogTab")

;; ── String-literal constants ──────────────────────────────────────────────
(def ^:private rk-root     "root")
(def ^:private rk-backdrop "backdrop")
(def ^:private rk-dialog   "dialog")
(def ^:private rk-header   "header")
(def ^:private rk-body     "body")
(def ^:private rk-footer   "footer")
(def ^:private hk-backdrop "backdrop")
(def ^:private hk-keydown  "keydown")
(def ^:private attr-part        "part")
(def ^:private attr-name        "name")
(def ^:private attr-role        "role")
(def ^:private attr-tabindex    "tabindex")
(def ^:private attr-data-open   "data-open")
(def ^:private attr-data-size   "data-size")
(def ^:private val-true   "true")
(def ^:private val-false  "false")
(def ^:private tab-stop   "-1")
(def ^:private ev-click   "click")
(def ^:private ev-keydown "keydown")
(def ^:private slot-header "header")
(def ^:private slot-footer "footer")

;; ── Styles ────────────────────────────────────────────────────────────────────
(def ^:private style-text
  (str
   ":host{"
   "display:contents;"
   "color-scheme:light dark;"
   "--x-modal-bg:var(--x-color-bg,Canvas);"
   "--x-modal-fg:var(--x-color-text,CanvasText);"
   "--x-modal-backdrop:rgb(0 0 0/0.45);"
   "--x-modal-shadow:var(--x-shadow-lg,0 20px 60px rgb(0 0 0/0.25));"
   "--x-modal-radius:var(--x-radius-md,0.75rem);"
   "--x-modal-width-sm:22rem;"
   "--x-modal-width-md:32rem;"
   "--x-modal-width-lg:44rem;"
   "--x-modal-width-xl:60rem;"
   "--x-modal-max-height:90dvh;"
   "--x-modal-header-padding:1rem 1.25rem;"
   "--x-modal-body-padding:1rem 1.25rem;"
   "--x-modal-footer-padding:0.75rem 1.25rem;"
   "--x-modal-border:var(--x-color-border,color-mix(in srgb,currentColor 12%,transparent));"
   "--x-modal-duration:var(--x-transition-duration,180ms);"
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
   "width:100%;"
   "height:100dvh;"
   "border-radius:0;"
   "max-width:100%;"
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
   "[part=dialog]{max-width:calc(100vw - 2rem);}"
   ":host([data-size=full]) [part=dialog]{max-width:100%;}"
   "@media (prefers-reduced-motion:reduce){"
   "[part=dialog],[part=backdrop]{transition:none;}"
   "}"))

;; ── Shadow DOM initialisation ─────────────────────────────────────────────────
(defn- make-style! []
  (let [style (.createElement js/document "style")]
    (set! (.-textContent style) style-text)
    style))

(defn- make-backdrop! []
  (let [backdrop (.createElement js/document "div")]
    (du/set-attr! backdrop attr-part model/part-backdrop)
    backdrop))

(defn- make-slot-section! [part-name slot-name]
  (let [section (.createElement js/document "div")
        slot    (.createElement js/document "slot")]
    (du/set-attr! section attr-part part-name)
    (du/set-attr! slot    attr-name slot-name)
    (.appendChild section slot)
    section))

(defn- make-body-section! []
  (let [body (.createElement js/document "div")
        slot (.createElement js/document "slot")]
    (du/set-attr! body attr-part model/part-body)
    (.appendChild body slot)
    body))

(defn- make-dialog-section! [^js header ^js body ^js footer]
  (let [dialog (.createElement js/document "div")]
    (du/set-attr! dialog attr-part    model/part-dialog)
    (du/set-attr! dialog attr-role    model/role-dialog)
    (du/set-attr! dialog model/aria-modal val-true)
    (.appendChild dialog header)
    (.appendChild dialog body)
    (.appendChild dialog footer)
    dialog))

(defn- init-dom! [^js el]
  (let [root     (.attachShadow el #js {:mode "open"})
        style    (make-style!)
        backdrop (make-backdrop!)
        header   (make-slot-section! model/part-header slot-header)
        body     (make-body-section!)
        footer   (make-slot-section! model/part-footer slot-footer)
        dialog   (make-dialog-section! header body footer)
        refs     #js {}]
    (.appendChild root style)
    (.appendChild root backdrop)
    (.appendChild root dialog)
    (gobj/set refs rk-root     root)
    (gobj/set refs rk-backdrop backdrop)
    (gobj/set refs rk-dialog   dialog)
    (gobj/set refs rk-header   header)
    (gobj/set refs rk-body     body)
    (gobj/set refs rk-footer   footer)
    (du/setv! el k-refs refs)))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs)
      (do (init-dom! el)
          (du/getv el k-refs))))

;; ── Attribute readers ─────────────────────────────────────────────────────────
(defn- read-model [^js el]
  (model/normalize
   {:open-present? (du/has-attr? el model/attr-open)
    :size-raw      (du/get-attr el model/attr-size)
    :label-raw     (du/get-attr el model/attr-label)}))

;; ── Focus trap ────────────────────────────────────────────────────────────────
(defn- collect-tabbables [^js refs]
  (let [sel      (str "a[href],button:not([disabled]),input:not([disabled]),"
                      "select:not([disabled]),textarea:not([disabled]),"
                      "[tabindex]:not([tabindex='-1'])")
        ^js root   (gobj/get refs rk-root)
        ^js dialog (gobj/get refs rk-dialog)
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
  (let [refs       (du/getv el k-refs)
        ^js dialog (when refs (gobj/get refs rk-dialog))
        tabbables  (when refs (collect-tabbables refs))]
    (du/setv! el k-restore (.-activeElement js/document))
    (du/setv! el k-tabbables (when tabbables (clj->js tabbables)))
    (if (seq tabbables)
      (.focus (first tabbables))
      (when dialog
        (when-not (.hasAttribute dialog attr-tabindex)
          (du/set-attr! dialog attr-tabindex tab-stop)
          (du/setv! el k-dialog-tab true))
        (.focus dialog)))))

(defn- deactivate-focus-trap! [^js el]
  (let [refs              (du/getv el k-refs)
        ^js dialog        (when refs (gobj/get refs rk-dialog))
        restore           (du/getv el k-restore)
        dialog-tab-added  (true? (du/getv el k-dialog-tab))]
    (du/setv! el k-tabbables nil)
    (du/setv! el k-restore nil)
    (when (and dialog dialog-tab-added)
      (du/remove-attr! dialog attr-tabindex)
      (du/setv! el k-dialog-tab false))
    (when (and restore (.-isConnected restore))
      (.focus restore))))

(defn- cycle-focus! [^js el ^js e]
  (let [tabbables-js (du/getv el k-tabbables)
        tabbables    (if tabbables-js (vec (array-seq tabbables-js)) [])
        refs         (du/getv el k-refs)
        ^js dialog   (when refs (gobj/get refs rk-dialog))]
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

          :else nil)))))

;; ── Dismiss (user-initiated close) ───────────────────────────────────────────
(defn- do-dismiss! [^js el reason]
  (du/dispatch! el model/event-dismiss (model/dismiss-event-detail reason))
  (du/remove-attr! el model/attr-open))

;; ── Show / hide / toggle ─────────────────────────────────────────────────────
(defn- do-show! [^js el]
  (when-not (du/has-attr? el model/attr-open)
    (du/set-attr! el model/attr-open "")))

(defn- do-hide! [^js el]
  (du/remove-attr! el model/attr-open))

(defn- do-toggle! [^js el]
  (if (du/has-attr? el model/attr-open)
    (do-hide! el)
    (do-show! el)))

;; ── Transition detection ──────────────────────────────────────────────────────
(defn- handle-open-transition! [^js el old-m new-m]
  (let [open? (:open? new-m)
        prev? (some-> old-m :open?)]
    (when (not= prev? open?)
      ;; No event on initial mount (no prior state); focus trap still toggles.
      (when (some? old-m)
        (du/dispatch! el model/event-toggle (model/toggle-event-detail open?)))
      (if open?
        (js/setTimeout (fn delayed-focus-trap [] (activate-focus-trap! el)) 0)
        (deactivate-focus-trap! el)))))

;; ── DOM patching (cache-at-tail render-pipeline) ────────────────────────────
(defn- apply-host-data! [^js el {:keys [open? size]}]
  (du/set-attr! el attr-data-open (if open? val-true val-false))
  (du/set-attr! el attr-data-size size))

(defn- apply-dialog-aria! [^js dialog {:keys [label]}]
  (du/set-attr! dialog model/aria-label label))

(defn- apply-model! [^js el old-m new-m]
  (let [refs       (ensure-refs! el)
        ^js dialog (gobj/get refs rk-dialog)]
    (apply-host-data!        el new-m)
    (apply-dialog-aria!      dialog new-m)
    (handle-open-transition! el old-m new-m)
    (du/setv! el k-model new-m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el k-model)]
    (when (not= new-m old-m)
      (apply-model! el old-m new-m))))

;; ── Listener management ───────────────────────────────────────────────────────
(defn- on-keydown! [^js el ^js e]
  (let [key (.-key e)]
    (cond
      (= key "Escape")
      (do (.preventDefault e)
          (do-dismiss! el model/reason-escape))

      (= key "Tab")
      (cycle-focus! el e))))

(defn- add-listeners! [^js el]
  (let [refs         (ensure-refs! el)
        ^js backdrop (gobj/get refs rk-backdrop)
        ^js dialog   (gobj/get refs rk-dialog)
        backdrop-h   (fn handle-backdrop-click [_] (do-dismiss! el model/reason-backdrop))
        keydown-h    (fn handle-dialog-keydown [^js e] (on-keydown! el e))
        handlers     #js {}]
    (.addEventListener backdrop ev-click   backdrop-h)
    (.addEventListener dialog   ev-keydown keydown-h)
    (gobj/set handlers hk-backdrop backdrop-h)
    (gobj/set handlers hk-keydown  keydown-h)
    (du/setv! el k-handlers handlers)))

(defn- remove-listeners! [^js el]
  (let [hs   (du/getv el k-handlers)
        refs (du/getv el k-refs)]
    (when (and hs refs)
      (let [^js backdrop (gobj/get refs rk-backdrop)
            ^js dialog   (gobj/get refs rk-dialog)
            backdrop-h   (gobj/get hs hk-backdrop)
            keydown-h    (gobj/get hs hk-keydown)]
        (when backdrop-h (.removeEventListener backdrop ev-click   backdrop-h))
        (when keydown-h  (.removeEventListener dialog   ev-keydown keydown-h)))))
  (du/setv! el k-handlers nil))

;; ── Lifecycle ─────────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-refs! el)
  (remove-listeners! el)
  (add-listeners! el)
  (update-from-attrs! el))

(defn- disconnected! [^js el]
  (remove-listeners! el)
  (deactivate-focus-trap! el))

(defn- attribute-changed! [^js el _attr-name old-val new-val]
  (when (not= old-val new-val)
    (update-from-attrs! el)))

;; ── Property and method accessors ─────────────────────────────────────────────
(defn- define-method! [^js proto js-name f]
  (.defineProperty js/Object proto js-name
                   #js {:value f
                        :enumerable   true
                        :configurable true
                        :writable     true}))

(defn- install-properties! [^js proto]
  (du/install-properties! proto model/property-api)
  (define-method! proto "show"   (fn [] (this-as ^js this (do-show! this))))
  (define-method! proto "hide"   (fn [] (this-as ^js this (do-hide! this))))
  (define-method! proto "toggle" (fn [] (this-as ^js this (do-toggle! this)))))

;; ── Public API ────────────────────────────────────────────────────────────────

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-properties!}))
