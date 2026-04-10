(ns baredom.components.x-menu-item.x-menu-item
  (:require
   [goog.object :as gobj]
   [baredom.components.x-menu-item.model :as model]))

(def key-refs "__xMenuItemRefs")
(def key-handlers "__xMenuItemHandlers")
(def key-init "__xMenuItemInit")

(defn getv [el k] (gobj/get el k))
(defn setv! [el k v] (gobj/set el k v))

(defn initialized? [el]
  (true? (getv el key-init)))

(defn mark-initialized! [el]
  (setv! el key-init true))

(def style-text
  (str
   ":host{"
   "display:block;outline:none;color-scheme:light dark;"
   "--x-menu-item-color:var(--x-color-text,#111827);"
   "--x-menu-item-hover-bg:var(--x-color-surface-hover,#f3f4f6);"
   "--x-menu-item-focus-bg:var(--x-color-surface-hover,#eff6ff);"
   "--x-menu-item-focus-color:var(--x-color-primary-hover,#1d4ed8);"
   "--x-menu-item-disabled-opacity:0.45;"
   "--x-menu-item-danger-color:var(--x-color-danger,#dc2626);"
   "--x-menu-item-danger-hover-bg:#fef2f2;"
   "--x-menu-item-padding:8px 12px;"
   "--x-menu-item-border-radius:var(--x-radius-sm,4px);"
   "--x-menu-item-font-size:0.9375rem;"
   "--x-menu-item-icon-gap:8px;"
   "--x-menu-item-divider-color:var(--x-color-border,#e5e7eb);}"
   ":host([disabled]){pointer-events:none;}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-menu-item-color:var(--x-color-text,#f9fafb);"
   "--x-menu-item-hover-bg:var(--x-color-surface-hover,#1f2937);"
   "--x-menu-item-focus-bg:var(--x-color-surface-hover,#1e3a5f);"
   "--x-menu-item-focus-color:var(--x-color-focus-ring,#60a5fa);"
   "--x-menu-item-danger-color:var(--x-color-danger,#f87171);"
   "--x-menu-item-danger-hover-bg:#2d1515;"
   "--x-menu-item-divider-color:var(--x-color-border,#374151);}}"
   ".base{"
   "display:flex;align-items:center;gap:var(--x-menu-item-icon-gap);"
   "padding:var(--x-menu-item-padding);"
   "border-radius:var(--x-menu-item-border-radius);"
   "font-size:var(--x-menu-item-font-size);"
   "color:var(--x-menu-item-color);"
   "cursor:pointer;user-select:none;"
   "transition:background 100ms ease,color 100ms ease;}"
   ".base:hover{background:var(--x-menu-item-hover-bg);}"
   ":host(:focus) .base{background:var(--x-menu-item-focus-bg);color:var(--x-menu-item-focus-color);}"
   ".base[data-variant='danger']{color:var(--x-menu-item-danger-color);}"
   ".base[data-variant='danger']:hover{background:var(--x-menu-item-danger-hover-bg);}"
   ":host(:focus) .base[data-variant='danger']{background:var(--x-menu-item-danger-hover-bg);}"
   ".base[data-disabled='true']{opacity:var(--x-menu-item-disabled-opacity);cursor:default;pointer-events:none;}"
   ".base[data-type='divider']{padding:0;cursor:default;pointer-events:none;}"
   ".icon-span{display:none;flex-shrink:0;align-items:center;}"
   ":host([has-icon]) .icon-span{display:flex;}"
   ".divider-hr{display:none;}"
   ".base[data-type='divider'] .icon-span,.base[data-type='divider'] .label-span{display:none;}"
   ".base[data-type='divider'] .divider-hr{"
   "display:block;width:100%;border:none;"
   "border-top:1px solid var(--x-menu-item-divider-color);margin:4px 0;}"
   "@media (prefers-reduced-motion:reduce){.base{transition:none;}}"))

(defn read-inputs [^js el]
  {:value (.getAttribute el model/attr-value)
   :disabled (.hasAttribute el model/attr-disabled)
   :variant (.getAttribute el model/attr-variant)
   :type (.getAttribute el model/attr-type)})

(defn dispatch-item-select! [^js el]
  (let [value (.getAttribute el model/attr-value)]
    (.dispatchEvent
     el
     (js/CustomEvent.
      model/event-item-select
      #js {:detail #js {:value (or value "")}
           :bubbles true
           :composed true}))))

(defn handle-click! [^js el ^js evt]
  (let [state (model/derive-state (read-inputs el))]
    (when (or (:disabled state) (= (:type state) "divider"))
      (.preventDefault evt))
    (when (and (not (:disabled state)) (not= (:type state) "divider"))
      (dispatch-item-select! el))))

(defn handle-keydown! [^js el ^js evt]
  (let [k (.-key evt)
        state (model/derive-state (read-inputs el))]
    (when (and (or (= k "Enter") (= k " "))
               (not (:disabled state))
               (not= (:type state) "divider"))
      (.preventDefault evt)
      (dispatch-item-select! el))))

(defn handle-icon-slotchange! [^js el ^js slot]
  (let [nodes (.assignedNodes slot)]
    (if (> (alength nodes) 0)
      (.setAttribute el "has-icon" "")
      (.removeAttribute el "has-icon"))))

(defn render! [^js el]
  (let [refs (getv el key-refs)
        base (when refs (gobj/get refs "base"))
        state (model/derive-state (read-inputs el))]
    (when base
      (if (= (:type state) "divider")
        (do
          (.setAttribute el "role" "separator")
          (.removeAttribute el "tabindex")
          (.removeAttribute el "aria-disabled")
          (.setAttribute base "data-type" "divider")
          (.setAttribute base "data-variant" "")
          (.setAttribute base "data-disabled" "false"))
        (do
          (.setAttribute el "role" "menuitem")
          (.setAttribute el "tabindex" "-1")
          (.setAttribute base "data-type" "")
          (.setAttribute base "data-variant" (:variant state))
          (.setAttribute base "data-disabled" (if (:disabled state) "true" "false"))
          (if (:disabled state)
            (.setAttribute el "aria-disabled" "true")
            (.removeAttribute el "aria-disabled")))))))

(defn init-dom! [^js el]
  (let [root (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        base (.createElement js/document "div")
        icon-span (.createElement js/document "span")
        icon-slot (.createElement js/document "slot")
        label-span (.createElement js/document "span")
        label-slot (.createElement js/document "slot")
        divider-hr (.createElement js/document "hr")]
    (set! (.-textContent style) style-text)
    (.setAttribute base "part" "base")
    (set! (.-className base) "base")
    (.setAttribute icon-span "part" "icon")
    (set! (.-className icon-span) "icon-span")
    (.setAttribute icon-slot "name" "icon")
    (.appendChild icon-span icon-slot)
    (set! (.-className label-span) "label-span")
    (.appendChild label-span label-slot)
    (set! (.-className divider-hr) "divider-hr")
    (.appendChild base icon-span)
    (.appendChild base label-span)
    (.appendChild base divider-hr)
    (.appendChild root style)
    (.appendChild root base)
    (let [refs #js {}]
      (gobj/set refs "base" base)
      (gobj/set refs "icon-span" icon-span)
      (gobj/set refs "icon-slot" icon-slot)
      (setv! el key-refs refs))))

(defn install-listeners! [^js el]
  (let [refs (getv el key-refs)
        icon-slot (gobj/get refs "icon-slot")
        on-click (fn [^js e] (handle-click! el e))
        on-keydown (fn [^js e] (handle-keydown! el e))
        on-slotchange (fn [_] (handle-icon-slotchange! el icon-slot))
        handlers #js {}]
    (.addEventListener el "click" on-click)
    (.addEventListener el "keydown" on-keydown)
    (.addEventListener icon-slot "slotchange" on-slotchange)
    (gobj/set handlers "click" on-click)
    (gobj/set handlers "keydown" on-keydown)
    (gobj/set handlers "icon-slotchange" on-slotchange)
    (setv! el key-handlers handlers)))

(defn remove-listeners! [^js el]
  (let [handlers (getv el key-handlers)
        refs (getv el key-refs)]
    (when handlers
      (let [on-click (gobj/get handlers "click")
            on-keydown (gobj/get handlers "keydown")
            on-slotchange (gobj/get handlers "icon-slotchange")
            icon-slot (when refs (gobj/get refs "icon-slot"))]
        (.removeEventListener el "click" on-click)
        (.removeEventListener el "keydown" on-keydown)
        (when icon-slot
          (.removeEventListener icon-slot "slotchange" on-slotchange))))))

(defn init-element! [^js el]
  (when-not (initialized? el)
    (init-dom! el)
    (install-listeners! el)
    (mark-initialized! el))
  (render! el)
  el)

(defn connected-callback [^js el]
  (if (initialized? el)
    (do (install-listeners! el) (render! el))
    (init-element! el)))

(defn disconnected-callback [^js el]
  (remove-listeners! el))

(defn attribute-changed-callback [^js el _name _old _new]
  (when (initialized? el)
    (render! el)))

(defn install-property-accessors! [^js klass]
  (.defineProperty
   js/Object (.-prototype klass) "value"
   #js {:get (fn [] (this-as this (.getAttribute this model/attr-value)))
        :set (fn [v] (this-as this (if v (.setAttribute this model/attr-value v) (.removeAttribute this model/attr-value))))
        :configurable true :enumerable true})
  (.defineProperty
   js/Object (.-prototype klass) "disabled"
   #js {:get (fn [] (this-as this (.hasAttribute this model/attr-disabled)))
        :set (fn [v] (this-as this (if v (.setAttribute this model/attr-disabled "") (.removeAttribute this model/attr-disabled))))
        :configurable true :enumerable true})
  (.defineProperty
   js/Object (.-prototype klass) "variant"
   #js {:get (fn [] (this-as this (.getAttribute this model/attr-variant)))
        :set (fn [v] (this-as this (if v (.setAttribute this model/attr-variant v) (.removeAttribute this model/attr-variant))))
        :configurable true :enumerable true})
  (.defineProperty
   js/Object (.-prototype klass) "type"
   #js {:get (fn [] (this-as this (.getAttribute this model/attr-type)))
        :set (fn [v] (this-as this (if v (.setAttribute this model/attr-type v) (.removeAttribute this model/attr-type))))
        :configurable true :enumerable true}))

(defn element-class []
  (let [klass (js* "(class extends HTMLElement {})")]
    (set! (.-observedAttributes klass) model/observed-attributes)
    (set! (.-connectedCallback (.-prototype klass))
          (fn [] (this-as this (connected-callback this))))
    (set! (.-disconnectedCallback (.-prototype klass))
          (fn [] (this-as this (disconnected-callback this))))
    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [n o v] (this-as this (attribute-changed-callback this n o v))))
    (install-property-accessors! klass)
    klass))

(defn register! []
  (when-not (.get js/customElements model/tag-name)
    (.define js/customElements model/tag-name (element-class))))

(defn init! []
  (register!))
