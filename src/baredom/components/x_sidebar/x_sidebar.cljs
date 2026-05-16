(ns baredom.components.x-sidebar.x-sidebar
  (:require [baredom.utils.component :as component]
            [goog.object :as gobj]
            [baredom.utils.dom :as du]
            [baredom.components.x-sidebar.model :as model]))

;; ── Instance-field keys ──────────────────────────────────────────────────────
;; Stashed on the host element via `du/setv!` so every mutation routes through
;; the x-trace-history recorder hook. Prior to PR refactor these were string
;; keys read/written via `gobj/get|set instance "_X"` which bypassed the hook.

(def ^:private k-root                  "__xSidebarRoot")
(def ^:private k-backdrop              "__xSidebarBackdrop")
(def ^:private k-sidebar               "__xSidebarSidebar")
(def ^:private k-panel                 "__xSidebarPanel")
(def ^:private k-slot                  "__xSidebarSlot")
(def ^:private k-panel-tabindex-added  "__xSidebarPanelTabindexAdded")
(def ^:private k-restore-focus-target  "__xSidebarRestoreFocusTarget")
(def ^:private k-tabbables             "__xSidebarTabbables")
(def ^:private k-prev-state            "__xSidebarPrevState")
(def ^:private k-handlers              "__xSidebarHandlers")
(def ^:private k-listening             "__xSidebarListening")

;; ── Handler-map keys (live on a JS object, not the host) ─────────────────────
(def ^:private handler-backdrop-click "onBackdropClick")
(def ^:private handler-keydown        "onKeydown")
(def ^:private handler-resize         "onResize")

(def css-text
  (str
   ":host {"
   "  --x-sidebar-bg: var(--x-color-bg, Canvas);"
   "  --x-sidebar-fg: var(--x-color-text, CanvasText);"
   "  --x-sidebar-backdrop: rgb(0 0 0 / 0.4);"
   "  --x-sidebar-shadow: var(--x-shadow-lg, 0 8px 24px rgb(0 0 0 / 0.18));"
   "  --x-sidebar-width: 18rem;"
   "  --x-sidebar-collapsed-width: 4rem;"
   "  --x-sidebar-duration: var(--x-transition-duration, 180ms);"
   "  --x-sidebar-easing: var(--x-transition-easing, ease);"
   "  display: block;"
   "  position: relative;"
   "}"
   ":host([hidden]) { display: none; }"
   ".backdrop[hidden] { display: none; }"
   ".backdrop {"
   "  position: fixed;"
   "  inset: 0;"
   "  background: var(--x-sidebar-backdrop);"
   "  opacity: 0;"
   "  z-index: 999;"
   "  pointer-events: none;"
   "  transition: opacity var(--x-sidebar-duration) var(--x-sidebar-easing);"
   "}"
   ".backdrop[data-active='true'] {"
   "  opacity: 1;"
   "  pointer-events: auto;"
   "}"
   ".sidebar {"
   "  position: relative;"
   "  color: var(--x-sidebar-fg);"
   "  block-size: 100%;"
   "}"
   ".panel {"
   "  background: var(--x-sidebar-bg);"
   "  color: var(--x-sidebar-fg);"
   "  inline-size: var(--x-sidebar-width);"
   "  min-block-size: 100%;"
   "  overflow: hidden;"
   "  box-shadow: var(--x-sidebar-shadow);"
   "  will-change: transform, inline-size;"
   "  transition: transform var(--x-sidebar-duration) var(--x-sidebar-easing),"
   "              inline-size var(--x-sidebar-duration) var(--x-sidebar-easing),"
   "              opacity var(--x-sidebar-duration) var(--x-sidebar-easing);"
   "}"
   ":host([data-effective-variant='docked']) .panel[data-collapsed='true'] {"
   "  inline-size: var(--x-sidebar-collapsed-width);"
   "}"
   ":host([data-effective-variant='overlay']) .sidebar,"
   ":host([data-effective-variant='modal']) .sidebar {"
   "  position: fixed;"
   "  inset-block: 0;"
   "  z-index: 1000;"
   "  pointer-events: none;"
   "}"
   ":host([data-effective-variant='overlay'][data-open='true']) .sidebar,"
   ":host([data-effective-variant='modal'][data-open='true']) .sidebar {"
   "  pointer-events: auto;"
   "}"
   ":host([data-effective-variant='overlay'][data-placement='left']) .sidebar,"
   ":host([data-effective-variant='modal'][data-placement='left']) .sidebar {"
   "  inset-inline-start: 0;"
   "}"
   ":host([data-effective-variant='overlay'][data-placement='right']) .sidebar,"
   ":host([data-effective-variant='modal'][data-placement='right']) .sidebar {"
   "  inset-inline-end: 0;"
   "}"
   ":host([data-effective-variant='overlay'][data-placement='left']) .panel[data-open='false'],"
   ":host([data-effective-variant='modal'][data-placement='left']) .panel[data-open='false'] {"
   "  transform: translateX(-100%);"
   "  pointer-events: none;"
   "}"
   ":host([data-effective-variant='overlay'][data-placement='right']) .panel[data-open='false'],"
   ":host([data-effective-variant='modal'][data-placement='right']) .panel[data-open='false'] {"
   "  transform: translateX(100%);"
   "  pointer-events: none;"
   "}"
   ":host([data-effective-variant='overlay']) .panel[data-open='true'],"
   ":host([data-effective-variant='modal']) .panel[data-open='true'] {"
   "  transform: translateX(0);"
   "  pointer-events: auto;"
   "}"
   ":host([data-reduced-motion='true']) .panel,"
   ":host([data-reduced-motion='true']) .backdrop {"
   "  transition: none;"
   "}"
   "@media (prefers-color-scheme: dark) {"
   "  :host {"
   "    --x-sidebar-bg: var(--x-color-bg, #1f2937);"
   "    --x-sidebar-fg: var(--x-color-text, #f9fafb);"
   "    --x-sidebar-shadow: var(--x-shadow-lg, 0 8px 24px rgb(0 0 0 / 0.4));"
   "  }"
   "}"))


(defn set-data-attr!
  [el name value]
  (du/set-attr! el name value))

(defn prefers-reduced-motion?
  []
  (let [mq (.matchMedia js/window "(prefers-reduced-motion: reduce)")]
    (boolean (.-matches mq))))

(defn viewport-width
  []
  (.-innerWidth js/window))

(defn read-inputs
  [instance]
  {:open-attr (du/has-attr? instance model/attr-open)
   :collapsed-attr (du/has-attr? instance model/attr-collapsed)
   :placement-attr (du/get-attr instance model/attr-placement)
   :variant-attr (du/get-attr instance model/attr-variant)
   :breakpoint-attr (du/get-attr instance model/attr-breakpoint)
   :label-attr (du/get-attr instance model/attr-label)
   :viewport-width (viewport-width)
   :prefers-reduced-motion (prefers-reduced-motion?)})

(defn emit-toggle!
  [^js instance open]
  (du/dispatch! instance model/event-toggle (clj->js (model/toggle-event-detail open))))

(defn emit-dismiss!
  [^js instance reason]
  (when (model/dismiss-reason? reason)
    (du/dispatch! instance model/event-dismiss (clj->js (model/dismiss-event-detail reason)))))

(defn visible-element?
  [el]
  (let [style (.getComputedStyle js/window el)]
    (and (not= "none" (.-display style))
         (not= "hidden" (.-visibility style))
         (pos? (.-offsetWidth el))
         (pos? (.-offsetHeight el)))))

(defn collect-tabbables
  [root]
  (let [selector
        (str "a[href], button:not([disabled]), input:not([disabled]), "
             "select:not([disabled]), textarea:not([disabled]), "
             "[tabindex]:not([tabindex='-1'])")
        node-list (.querySelectorAll root selector)]
    (->> (array-seq node-list)
      (filter visible-element?)
      vec)))

(defn focused-sidebar-descendant
  [instance]
  (let [root (du/getv instance k-root)
        sidebar (du/getv instance k-sidebar)
        shadow-active (when root (.-activeElement root))
        doc-active (.-activeElement js/document)]
    (cond
      (and shadow-active sidebar (.contains sidebar shadow-active))
      shadow-active

      (and doc-active sidebar (.contains sidebar doc-active))
      doc-active

      :else nil)))

(defn release-sidebar-focus!
  [instance]
  (when-let [focused (focused-sidebar-descendant instance)]
    (.blur focused)))

(defn focus-panel!
  [instance]
  (let [panel (du/getv instance k-panel)]
    (when-not (.hasAttribute panel "tabindex")
      (du/set-attr! panel "tabindex" "-1")
      (du/setv! instance k-panel-tabindex-added true))
    (.focus panel)))

(defn activate-focus-trap!
  [instance]
  (let [panel (du/getv instance k-panel)
        tabbables (collect-tabbables panel)]
    (du/setv! instance k-restore-focus-target (.-activeElement js/document))
    (du/setv! instance k-tabbables (clj->js tabbables))
    (if (seq tabbables)
      (.focus (first tabbables))
      (focus-panel! instance))))

(defn deactivate-focus-trap!
  [instance]
  (let [restore-target (du/getv instance k-restore-focus-target)
        panel (du/getv instance k-panel)
        panel-tabindex-added (true? (du/getv instance k-panel-tabindex-added))]
    (release-sidebar-focus! instance)
    (du/setv! instance k-tabbables nil)
    (du/setv! instance k-restore-focus-target nil)
    (when (and panel panel-tabindex-added)
      (du/remove-attr! panel "tabindex")
      (du/setv! instance k-panel-tabindex-added false))
    (when (and restore-target (.-isConnected restore-target))
      (.focus restore-target))))

;; ── Focus cycling (Tab/Shift-Tab) ───────────────────────────────────────────
;; cycle-focus! reads as a phase list: gather tabbables → branch on empty →
;; compute wrap target → preventDefault + focus. The three helpers below pull
;; each phase out of the original 22-line function.

(defn- read-tabbables
  [instance]
  (let [tabbables-js (du/getv instance k-tabbables)]
    (if tabbables-js (vec (array-seq tabbables-js)) [])))

(defn- focus-empty-trap!
  [instance ^js event]
  (.preventDefault event)
  (when (du/getv instance k-panel)
    (focus-panel! instance)))

(defn- wrap-tab-target
  [tabbables shift?]
  (let [active   (.-activeElement js/document)
        first-el (first tabbables)
        last-el  (last tabbables)]
    (cond
      (and shift?       (= active first-el)) last-el
      (and (not shift?) (= active last-el))  first-el
      :else                                  nil)))

(defn cycle-focus!
  [instance ^js event]
  (let [tabbables (read-tabbables instance)]
    (if (empty? tabbables)
      (focus-empty-trap! instance event)
      (when-let [target (wrap-tab-target tabbables (.-shiftKey event))]
        (.preventDefault event)
        (.focus target)))))

(defn apply-host-state!
  [instance {:keys [effective-variant placement open collapsed-applied reduced-motion]}]
  (set-data-attr! instance "data-effective-variant" effective-variant)
  (set-data-attr! instance "data-placement" placement)
  (set-data-attr! instance "data-open" (if open "true" "false"))
  (set-data-attr! instance "data-collapsed" (if collapsed-applied "true" "false"))
  (set-data-attr! instance "data-reduced-motion" (if reduced-motion "true" "false")))

(defn apply-a11y!
  [instance {:keys [label aria-hidden]}]
  (when-let [sidebar (du/getv instance k-sidebar)]
    (du/set-attr! sidebar "role" "navigation")
    (du/set-attr! sidebar "aria-label" label)
    (du/set-attr! sidebar "aria-hidden" (if aria-hidden "true" "false"))))

(defn apply-backdrop!
  [instance {:keys [show-backdrop]}]
  (when-let [backdrop (du/getv instance k-backdrop)]
    (set-data-attr! backdrop "data-active" (if show-backdrop "true" "false"))
    (set! (.-hidden backdrop) (not show-backdrop))
    (when (exists? (.-inert backdrop))
      (set! (.-inert backdrop) (not show-backdrop)))))

(defn apply-panel-state!
  [instance {:keys [open placement effective-variant collapsed-applied]}]
  (when-let [panel (du/getv instance k-panel)]
    (set-data-attr! panel "data-open" (if open "true" "false"))
    (set-data-attr! panel "data-placement" placement)
    (set-data-attr! panel "data-variant" effective-variant)
    (set-data-attr! panel "data-collapsed" (if collapsed-applied "true" "false"))))

(defn sync-focus-trap!
  [instance prev-state next-state]
  (cond
    (model/entered-modal-open? prev-state next-state)
    (activate-focus-trap! instance)

    (model/left-modal-open? prev-state next-state)
    (deactivate-focus-trap! instance)

    :else nil))

(defn dispatch-state-events!
  [instance prev-state next-state]
  (when (model/toggle-should-fire? prev-state next-state)
    (emit-toggle! instance (:open next-state))))

;; ── Shadow construction ──────────────────────────────────────────────────────
;; Each per-section builder creates + decorates + assembles + returns its
;; element(s). `assemble-shadow!` composes them, attaches to the host, and
;; stashes refs on the instance. `ensure-shadow!` is a one-shot guard.

(defn- make-style! []
  (let [el (.createElement js/document "style")]
    (set! (.-textContent el) css-text)
    el))

(defn- make-backdrop! []
  (let [el (.createElement js/document "div")]
    (set! (.-className el) "backdrop")
    (du/set-attr! el "part" model/part-backdrop)
    (set! (.-hidden el) true)
    el))

(defn- make-panel! []
  (let [panel (.createElement js/document "div")
        slot  (.createElement js/document "slot")]
    (set! (.-className panel) "panel")
    (du/set-attr! panel "part" model/part-panel)
    (.appendChild panel slot)
    {:panel panel :slot slot}))

(defn- make-sidebar! [panel]
  (let [el (.createElement js/document "aside")]
    (set! (.-className el) "sidebar")
    (du/set-attr! el "part" model/part-sidebar)
    (.appendChild el panel)
    el))

(defn- assemble-shadow! [^js instance]
  (let [root                 (.attachShadow instance #js {:mode "open"})
        style-el             (make-style!)
        backdrop-el          (make-backdrop!)
        {:keys [panel slot]} (make-panel!)
        sidebar-el           (make-sidebar! panel)]
    (.appendChild root style-el)
    (.appendChild root backdrop-el)
    (.appendChild root sidebar-el)
    (du/setv! instance k-root     root)
    (du/setv! instance k-backdrop backdrop-el)
    (du/setv! instance k-sidebar  sidebar-el)
    (du/setv! instance k-panel    panel)
    (du/setv! instance k-slot     slot)))

(defn ensure-shadow!
  [^js instance]
  (when-not (du/getv instance k-root)
    (assemble-shadow! instance)))

(defn render!
  [instance]
  (ensure-shadow! instance)
  (let [prev-state (or (du/getv instance k-prev-state) {})
        next-state (model/compute-state (read-inputs instance))]
    (apply-host-state! instance next-state)
    ;; Release/restore focus before aria-hidden is applied on close.
    (sync-focus-trap! instance prev-state next-state)
    (apply-a11y! instance next-state)
    (apply-backdrop! instance next-state)
    (apply-panel-state! instance next-state)
    (dispatch-state-events! instance prev-state next-state)
    (du/setv! instance k-prev-state next-state)))

(defn on-backdrop-click
  [instance event]
  (let [state (du/getv instance k-prev-state)
        backdrop (du/getv instance k-backdrop)]
    (when (and (or (:is-modal state) (:is-overlay state))
               (:open state)
               (= (.-target event) backdrop))
      (emit-dismiss! instance model/reason-backdrop)
      (du/set-bool-attr! instance model/attr-open false))))

(defn on-keydown
  [instance event]
  (let [state (du/getv instance k-prev-state)
        key (.-key event)]
    (when (and (or (:is-modal state) (:is-overlay state))
               (:open state))
      (cond
        (= key "Escape")
        (do
          (.preventDefault event)
          (emit-dismiss! instance model/reason-escape)
          (du/set-bool-attr! instance model/attr-open false))

        (and (:is-modal state) (= key "Tab"))
        (cycle-focus! instance event)

        :else nil))))

(defn on-resize
  [instance _event]
  (render! instance))

(defn bind-handlers!
  [instance]
  (when-not (du/getv instance k-handlers)
    (du/setv! instance k-handlers
              #js {:onBackdropClick (fn [event] (on-backdrop-click instance event))
                   :onKeydown       (fn [event] (on-keydown        instance event))
                   :onResize        (fn [event] (on-resize         instance event))})))

(defn add-listeners!
  [instance]
  (when-not (true? (du/getv instance k-listening))
    (let [handlers (du/getv instance k-handlers)
          backdrop (du/getv instance k-backdrop)
          root     (du/getv instance k-root)]
      (when (and backdrop handlers)
        (.addEventListener backdrop "click" (gobj/get handlers handler-backdrop-click)))
      (when (and root handlers)
        (.addEventListener root "keydown" (gobj/get handlers handler-keydown)))
      (when handlers
        (.addEventListener js/window "resize" (gobj/get handlers handler-resize)))
      (du/setv! instance k-listening true))))

(defn remove-listeners!
  [instance]
  (when (true? (du/getv instance k-listening))
    (let [handlers (du/getv instance k-handlers)
          backdrop (du/getv instance k-backdrop)
          root     (du/getv instance k-root)]
      (when (and backdrop handlers)
        (.removeEventListener backdrop "click" (gobj/get handlers handler-backdrop-click)))
      (when (and root handlers)
        (.removeEventListener root "keydown" (gobj/get handlers handler-keydown)))
      (when handlers
        (.removeEventListener js/window "resize" (gobj/get handlers handler-resize)))
      (du/setv! instance k-listening false))))

(defn- connected!
  [instance]
  (ensure-shadow! instance)
  (bind-handlers! instance)
  (add-listeners! instance)
  (render! instance))

(defn- disconnected!
  [instance]
  (remove-listeners! instance)
  (deactivate-focus-trap! instance))

(defn- attribute-changed!
  [instance _name old-value new-value]
  (when (not= old-value new-value)
    (render! instance)))


(defn- install-property-accessors!
  [^js proto]
  (du/install-properties! proto model/property-api))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
