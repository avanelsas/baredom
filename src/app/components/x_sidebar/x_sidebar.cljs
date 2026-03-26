(ns app.components.x-sidebar.x-sidebar
  (:require [goog.object :as gobj]
            [app.components.x-sidebar.model :as model])
  (:require-macros [cljs.core :refer [this-as]]))

(def css-text
  (str
   ":host {"
   "  --x-sidebar-bg: Canvas;"
   "  --x-sidebar-fg: CanvasText;"
   "  --x-sidebar-border: color-mix(in srgb, currentColor 12%, transparent);"
   "  --x-sidebar-backdrop: rgb(0 0 0 / 0.4);"
   "  --x-sidebar-shadow: 0 8px 24px rgb(0 0 0 / 0.18);"
   "  --x-sidebar-width: 18rem;"
   "  --x-sidebar-collapsed-width: 4rem;"
   "  --x-sidebar-duration: 180ms;"
   "  --x-sidebar-easing: ease;"
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
   "    --x-sidebar-border: color-mix(in srgb, currentColor 20%, transparent);"
   "    --x-sidebar-shadow: 0 8px 24px rgb(0 0 0 / 0.4);"
   "  }"
   "}"))


(defn has-attr?
  [el name]
  (.hasAttribute el name))

(defn get-attr
  [el name]
  (.getAttribute el name))

(defn set-bool-attr!
  [el name value]
  (if value
    (.setAttribute el name "")
    (.removeAttribute el name)))

(defn set-data-attr!
  [el name value]
  (.setAttribute el name value))

(defn prefers-reduced-motion?
  []
  (let [mq (.matchMedia js/window "(prefers-reduced-motion: reduce)")]
    (boolean (.-matches mq))))

(defn viewport-width
  []
  (.-innerWidth js/window))

(defn read-inputs
  [instance]
  {:open-attr (has-attr? instance model/attr-open)
   :collapsed-attr (has-attr? instance model/attr-collapsed)
   :placement-attr (get-attr instance model/attr-placement)
   :variant-attr (get-attr instance model/attr-variant)
   :breakpoint-attr (get-attr instance model/attr-breakpoint)
   :label-attr (get-attr instance model/attr-label)
   :viewport-width (viewport-width)
   :prefers-reduced-motion (prefers-reduced-motion?)})

(defn dispatch-custom-event!
  [instance event-name detail]
  (.dispatchEvent
   instance
   (js/CustomEvent.
    event-name
    (clj->js {:detail detail
              :bubbles true
              :composed true}))))

(defn emit-toggle!
  [instance open]
  (dispatch-custom-event! instance model/event-toggle (model/toggle-event-detail open)))

(defn emit-dismiss!
  [instance reason]
  (when (model/dismiss-reason? reason)
    (dispatch-custom-event! instance model/event-dismiss (model/dismiss-event-detail reason))))

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
  (let [root (gobj/get instance "_root")
        sidebar (gobj/get instance "_sidebar")
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
  (let [panel (gobj/get instance "_panel")]
    (when-not (.hasAttribute panel "tabindex")
      (.setAttribute panel "tabindex" "-1")
      (gobj/set instance "_panelTabindexAdded" true))
    (.focus panel)))

(defn activate-focus-trap!
  [instance]
  (let [panel (gobj/get instance "_panel")
        tabbables (collect-tabbables panel)]
    (gobj/set instance "_restoreFocusTarget" (.-activeElement js/document))
    (gobj/set instance "_tabbables" (clj->js tabbables))
    (if (seq tabbables)
      (.focus (first tabbables))
      (focus-panel! instance))))

(defn deactivate-focus-trap!
  [instance]
  (let [restore-target (gobj/get instance "_restoreFocusTarget")
        panel (gobj/get instance "_panel")
        panel-tabindex-added (true? (gobj/get instance "_panelTabindexAdded"))]
    (release-sidebar-focus! instance)
    (gobj/set instance "_tabbables" nil)
    (gobj/set instance "_restoreFocusTarget" nil)
    (when (and panel panel-tabindex-added)
      (.removeAttribute panel "tabindex")
      (gobj/set instance "_panelTabindexAdded" false))
    (when (and restore-target (.-isConnected restore-target))
      (.focus restore-target))))

(defn cycle-focus!
  [instance event]
  (let [tabbables-js (gobj/get instance "_tabbables")
        tabbables (if tabbables-js (vec (array-seq tabbables-js)) [])
        panel (gobj/get instance "_panel")]
    (if (empty? tabbables)
      (do
        (.preventDefault event)
        (when panel
          (focus-panel! instance)))
      (let [active (.-activeElement js/document)
            first-el (first tabbables)
            last-el (last tabbables)
            shift? (.-shiftKey event)]
        (cond
          (and shift? (= active first-el))
          (do (.preventDefault event) (.focus last-el))

          (and (not shift?) (= active last-el))
          (do (.preventDefault event) (.focus first-el))

          :else nil)))))

(defn apply-host-state!
  [instance {:keys [effective-variant placement open collapsed-applied reduced-motion]}]
  (set-data-attr! instance "data-effective-variant" effective-variant)
  (set-data-attr! instance "data-placement" placement)
  (set-data-attr! instance "data-open" (if open "true" "false"))
  (set-data-attr! instance "data-collapsed" (if collapsed-applied "true" "false"))
  (set-data-attr! instance "data-reduced-motion" (if reduced-motion "true" "false")))

(defn apply-a11y!
  [instance {:keys [label aria-hidden]}]
  (when-let [sidebar (gobj/get instance "_sidebar")]
    (.setAttribute sidebar "role" "navigation")
    (.setAttribute sidebar "aria-label" label)
    (.setAttribute sidebar "aria-hidden" (if aria-hidden "true" "false"))))

(defn apply-backdrop!
  [instance {:keys [show-backdrop]}]
  (when-let [backdrop (gobj/get instance "_backdrop")]
    (set-data-attr! backdrop "data-active" (if show-backdrop "true" "false"))
    (set! (.-hidden backdrop) (not show-backdrop))
    (when (exists? (.-inert backdrop))
      (set! (.-inert backdrop) (not show-backdrop)))))

(defn apply-panel-state!
  [instance {:keys [open placement effective-variant collapsed-applied]}]
  (when-let [panel (gobj/get instance "_panel")]
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

(defn ensure-shadow!
  [instance]
  (when-not (gobj/get instance "_root")
    (let [root (.attachShadow instance #js {:mode "open"})
          style-el (.createElement js/document "style")
          backdrop-el (.createElement js/document "div")
          sidebar-el (.createElement js/document "aside")
          panel-el (.createElement js/document "div")
          slot-el (.createElement js/document "slot")]
      (set! (.-textContent style-el) css-text)
      (set! (.-className backdrop-el) "backdrop")
      (.setAttribute backdrop-el "part" model/part-backdrop)
      (set! (.-hidden backdrop-el) true)
      (set! (.-className sidebar-el) "sidebar")
      (.setAttribute sidebar-el "part" model/part-sidebar)
      (set! (.-className panel-el) "panel")
      (.setAttribute panel-el "part" model/part-panel)
      (.appendChild panel-el slot-el)
      (.appendChild sidebar-el panel-el)
      (.appendChild root style-el)
      (.appendChild root backdrop-el)
      (.appendChild root sidebar-el)
      (gobj/set instance "_root" root)
      (gobj/set instance "_backdrop" backdrop-el)
      (gobj/set instance "_sidebar" sidebar-el)
      (gobj/set instance "_panel" panel-el)
      (gobj/set instance "_slot" slot-el))))

(defn render!
  [instance]
  (ensure-shadow! instance)
  (let [prev-state (or (some-> (gobj/get instance "_prevState") (js->clj :keywordize-keys true))
                       {})
        next-state (model/compute-state (read-inputs instance))]
    (apply-host-state! instance next-state)
    ;; Release/restore focus before aria-hidden is applied on close.
    (sync-focus-trap! instance prev-state next-state)
    (apply-a11y! instance next-state)
    (apply-backdrop! instance next-state)
    (apply-panel-state! instance next-state)
    (dispatch-state-events! instance prev-state next-state)
    (gobj/set instance "_prevState" (clj->js next-state))))

(defn on-backdrop-click
  [instance event]
  (let [state (some-> (gobj/get instance "_prevState") (js->clj :keywordize-keys true))
        backdrop (gobj/get instance "_backdrop")]
    (when (and (or (:is-modal state) (:is-overlay state))
               (:open state)
               (= (.-target event) backdrop))
      (emit-dismiss! instance model/reason-backdrop)
      (set-bool-attr! instance model/attr-open false))))

(defn on-keydown
  [instance event]
  (let [state (some-> (gobj/get instance "_prevState") (js->clj :keywordize-keys true))
        key (.-key event)]
    (when (and (or (:is-modal state) (:is-overlay state))
               (:open state))
      (cond
        (= key "Escape")
        (do
          (.preventDefault event)
          (emit-dismiss! instance model/reason-escape)
          (set-bool-attr! instance model/attr-open false))

        (and (:is-modal state) (= key "Tab"))
        (cycle-focus! instance event)

        :else nil))))

(defn on-resize
  [instance _event]
  (render! instance))

(defn bind-handlers!
  [instance]
  (when-not (gobj/get instance "_handlers")
    (gobj/set instance "_handlers"
              #js {:onBackdropClick (fn [event] (on-backdrop-click instance event))
                   :onKeydown (fn [event] (on-keydown instance event))
                   :onResize (fn [event] (on-resize instance event))})))

(defn add-listeners!
  [instance]
  (when-not (true? (gobj/get instance "_listening"))
    (let [handlers (gobj/get instance "_handlers")
          backdrop (gobj/get instance "_backdrop")
          root (gobj/get instance "_root")]
      (when (and backdrop handlers)
        (.addEventListener backdrop "click" (gobj/get handlers "onBackdropClick")))
      (when (and root handlers)
        (.addEventListener root "keydown" (gobj/get handlers "onKeydown")))
      (when handlers
        (.addEventListener js/window "resize" (gobj/get handlers "onResize")))
      (gobj/set instance "_listening" true))))

(defn remove-listeners!
  [instance]
  (when (true? (gobj/get instance "_listening"))
    (let [handlers (gobj/get instance "_handlers")
          backdrop (gobj/get instance "_backdrop")
          root (gobj/get instance "_root")]
      (when (and backdrop handlers)
        (.removeEventListener backdrop "click" (gobj/get handlers "onBackdropClick")))
      (when (and root handlers)
        (.removeEventListener root "keydown" (gobj/get handlers "onKeydown")))
      (when handlers
        (.removeEventListener js/window "resize" (gobj/get handlers "onResize")))
      (gobj/set instance "_listening" false))))

(defn connected-callback
  [instance]
  (ensure-shadow! instance)
  (bind-handlers! instance)
  (add-listeners! instance)
  (render! instance))

(defn disconnected-callback
  [instance]
  (remove-listeners! instance)
  (deactivate-focus-trap! instance))

(defn attribute-changed-callback
  [instance _name old-value new-value]
  (when (not= old-value new-value)
    (render! instance)))

(defn define-open-property!
  [proto]
  (js/Object.defineProperty
   proto
   model/attr-open
   #js {:get (fn []
               (this-as this
                        (.hasAttribute this model/attr-open)))
        :set (fn [value]
               (this-as this
                        (set-bool-attr! this model/attr-open (boolean value))))
        :enumerable true
        :configurable true}))

(defn define-collapsed-property!
  [proto]
  (js/Object.defineProperty
   proto
   model/attr-collapsed
   #js {:get (fn []
               (this-as this
                        (.hasAttribute this model/attr-collapsed)))
        :set (fn [value]
               (this-as this
                        (set-bool-attr! this model/attr-collapsed (boolean value))))
        :enumerable true
        :configurable true}))

(defn install-prototype!
  [proto]
  (gobj/set proto "connectedCallback"
            (fn []
              (this-as this
                       (connected-callback this))))
  (gobj/set proto "disconnectedCallback"
            (fn []
              (this-as this
                       (disconnected-callback this))))
  (gobj/set proto "attributeChangedCallback"
            (fn [name old-value new-value]
              (this-as this
                       (attribute-changed-callback this name old-value new-value))))
  (define-open-property! proto)
  (define-collapsed-property! proto)
  proto)

(defn define-custom-element!
  []
  (when-not (.get js/customElements model/tag-name)
    (let [base-proto (.-prototype js/HTMLElement)
          proto (install-prototype! (js/Object.create base-proto))
          ctor (js* "(function () { return Reflect.construct(HTMLElement, [], this.constructor); })")]
      (set! (.-prototype ctor) proto)
      (js/Object.defineProperty proto "constructor" #js {:value ctor})
      (js/Object.setPrototypeOf ctor js/HTMLElement)
      (aset ctor "observedAttributes" model/observed-attributes)
      (.define js/customElements model/tag-name ctor))))

(defn init!
  []
  (define-custom-element!))
