(ns app.components.x-tab.x-tab
  (:require
   [goog.object :as gobj]
   [app.components.x-tab.model :as model]))

(def key-root "__x_tab_root")
(def key-base "__x_tab_base")
(def key-initialized "__x_tab_initialized")

(defn getv [el k] (gobj/get el k))
(defn setv! [el k v] (gobj/set el k v))

(defn initialized? [el]
  (true? (getv el key-initialized)))

(defn mark-initialized! [el]
  (setv! el key-initialized true))

(defn read-inputs [el]
  {:value (.getAttribute el model/attr-value)
   :selected (.hasAttribute el model/attr-selected)
   :disabled (.hasAttribute el model/attr-disabled)
   :orientation (.getAttribute el model/attr-orientation)
   :size (.getAttribute el model/attr-size)
   :variant (.getAttribute el model/attr-variant)
   :label (.getAttribute el model/attr-label)
   :controls (.getAttribute el model/attr-controls)})

(def style-text
  (str
   ":host{"
   "display:inline-block;color-scheme:light dark;outline:none;"
   "--x-tab-color:#0f172a;"
   "--x-tab-hover-background:rgba(15,23,42,0.05);"
   "--x-tab-selected-color:#1d4ed8;"
   "--x-tab-selected-background:rgba(59,130,246,0.12);"
   "--x-tab-selected-border-color:transparent;}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-tab-color:#94a3b8;"
   "--x-tab-hover-background:rgba(248,250,252,0.06);"
   "--x-tab-selected-color:#60a5fa;"
   "--x-tab-selected-background:rgba(59,130,246,0.18);"
   "--x-tab-selected-border-color:#60a5fa;}}"
   ".base{box-sizing:border-box;display:inline-flex;align-items:center;justify-content:center;"
   "cursor:pointer;user-select:none;"
   "padding:var(--x-tab-padding-md,10px 16px);border-radius:var(--x-tab-radius,8px);"
   "color:var(--x-tab-color);background:var(--x-tab-background,transparent);"
   "border:1px solid var(--x-tab-border-color,transparent);"
   "transition:background var(--x-tab-transition-duration,120ms) ease,"
   "color var(--x-tab-transition-duration,120ms) ease,"
   "border-color var(--x-tab-transition-duration,120ms) ease;}"
   ".base[data-size='sm']{padding:var(--x-tab-padding-sm,6px 12px);}"
   ".base[data-size='md']{padding:var(--x-tab-padding-md,10px 16px);}"
   ".base[data-size='lg']{padding:var(--x-tab-padding-lg,14px 20px);}"
   ".base:hover{background:var(--x-tab-hover-background);}"
   ".base[data-selected='true']{color:var(--x-tab-selected-color);"
   "background:var(--x-tab-selected-background);"
   "border-color:var(--x-tab-selected-border-color);}"
   ".base[data-variant='underline'][data-selected='true']{border-bottom:2px solid var(--x-tab-selected-color);background:transparent;}"
   ".base[data-variant='pill'][data-selected='true']{background:var(--x-tab-selected-background);}"
   ".base[data-disabled='true']{opacity:var(--x-tab-disabled-opacity,0.5);cursor:default;}"
   ":host(:focus-visible) .base{box-shadow:0 0 0 3px var(--x-tab-focus-ring,rgba(59,130,246,0.4));}"
   "@media (prefers-reduced-motion: reduce){.base{transition:none;}}"))

(defn apply-host-a11y! [el state]
  (.setAttribute el "role" "tab")
  (.setAttribute el "aria-selected" (if (:selected state) "true" "false"))

  (if (:disabled state)
    (.setAttribute el "aria-disabled" "true")
    (.removeAttribute el "aria-disabled"))

  (.setAttribute el "tabindex" (:tabindex state))

  (let [controls (:controls state)
        label (:label state)]
    (if (and controls (not= controls ""))
      (.setAttribute el "aria-controls" controls)
      (.removeAttribute el "aria-controls"))

    (if (and label (not= label ""))
      (.setAttribute el "aria-label" label)
      (.removeAttribute el "aria-label"))))

(defn apply-state! [base state]
  (.setAttribute base "data-selected" (if (:selected state) "true" "false"))
  (.setAttribute base "data-disabled" (if (:disabled state) "true" "false"))
  (.setAttribute base "data-size" (:size state))
  (.setAttribute base "data-variant" (:variant state))
  (.setAttribute base "data-orientation" (:orientation state)))

(defn render! [el]

  (let [state (model/derive-state (read-inputs el))
        base (getv el key-base)]

    (when base
      (apply-host-a11y! el state)
      (apply-state! base state))))

(defn dispatch-select! [el]

  (let [value (.getAttribute el model/attr-value)]

    (.dispatchEvent
     el
     (js/CustomEvent.
      model/event-tab-select
      #js {:detail #js {:value (or value "")}
           :bubbles true
           :composed true}))))

(defn activate! [el]

  (let [{:keys [selected disabled]}
        (model/derive-state (read-inputs el))]

    (when (and (not disabled) (not selected))
      (dispatch-select! el))))

(defn on-click [el _]
  (activate! el))

(defn on-keydown [el e]

  (let [k (.-key e)]

    (when (or (= k "Enter") (= k " "))
      (.preventDefault e)
      (activate! el))))

(defn install-listeners! [el]

  (.addEventListener el "click"
                     (fn [e] (on-click el e)))

  (.addEventListener el "keydown"
                     (fn [e] (on-keydown el e))))

(defn init-dom! [el]

  (let [root (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        base (.createElement js/document "div")
        slot (.createElement js/document "slot")]

    (set! (.-textContent style) style-text)

    (.setAttribute base "part" "base")
    (set! (.-className base) "base")

    (.appendChild base slot)
    (.appendChild root style)
    (.appendChild root base)

    (setv! el key-root root)
    (setv! el key-base base)))

(defn init-element! [el]

  (when-not (initialized? el)
    (init-dom! el)
    (install-listeners! el)
    (mark-initialized! el))

  (render! el)
  el)

(defn connected-callback [el]
  (init-element! el))

(defn attribute-changed-callback [el _ _ _]
  (if (initialized? el)
    (render! el)
    (init-element! el)))

(defn install-property-accessors! [klass]

  (.defineProperty
   js/Object
   (.-prototype klass)
   "selected"
   #js {:get (fn []
               (this-as this
                        (.hasAttribute this model/attr-selected)))
        :set (fn [v]
               (this-as this
                        (if v
                          (.setAttribute this model/attr-selected "")
                          (.removeAttribute this model/attr-selected))))
        :configurable true
        :enumerable true})

  (.defineProperty
   js/Object
   (.-prototype klass)
   "disabled"
   #js {:get (fn []
               (this-as this
                        (.hasAttribute this model/attr-disabled)))
        :set (fn [v]
               (this-as this
                        (if v
                          (.setAttribute this model/attr-disabled "")
                          (.removeAttribute this model/attr-disabled))))
        :configurable true
        :enumerable true})

  (.defineProperty
   js/Object
   (.-prototype klass)
   "value"
   #js {:get (fn []
               (this-as this
                        (.getAttribute this model/attr-value)))
        :set (fn [v]
               (this-as this
                        (if v
                          (.setAttribute this model/attr-value v)
                          (.removeAttribute this model/attr-value))))
        :configurable true
        :enumerable true}))

(defn element-class []

  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass)
      model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
      (fn [] (this-as this (connected-callback this))))

    (set! (.-attributeChangedCallback (.-prototype klass))
      (fn [name old-value new-value]
        (this-as this
                 (attribute-changed-callback this name old-value new-value))))

    (install-property-accessors! klass)
    klass))

(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class))))

(defn init! []
  (register!))
