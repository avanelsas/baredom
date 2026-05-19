(ns baredom.components.x-menu-item.x-menu-item
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [goog.object :as gobj]
            [baredom.components.x-menu-item.model :as model]))

(def ^:private key-refs "__xMenuItemRefs")
(def ^:private key-handlers "__xMenuItemHandlers")
(def ^:private key-init "__xMenuItemInit")
(def ^:private key-model "__xMenuItemModel")

;; ── String-literal constants ────────────────────────────────────────────────
(def ^:private rk-base       "base")
(def ^:private rk-icon-span  "icon-span")
(def ^:private rk-icon-slot  "icon-slot")
(def ^:private hk-click      "click")
(def ^:private hk-keydown    "keydown")
(def ^:private hk-slotchange "icon-slotchange")
(def ^:private attr-part           "part")
(def ^:private attr-name           "name")
(def ^:private attr-role           "role")
(def ^:private attr-tabindex       "tabindex")
(def ^:private attr-aria-disabled  "aria-disabled")
(def ^:private attr-data-type      "data-type")
(def ^:private attr-data-variant   "data-variant")
(def ^:private attr-data-disabled  "data-disabled")
(def ^:private attr-has-icon       "has-icon")
(def ^:private part-base       "base")
(def ^:private part-icon       "icon")
(def ^:private slot-icon       "icon")
(def ^:private role-separator  "separator")
(def ^:private role-menuitem   "menuitem")
(def ^:private type-divider    "divider")
(def ^:private val-true        "true")
(def ^:private val-false       "false")
(def ^:private tab-stop        "-1")
(def ^:private ev-click        "click")
(def ^:private ev-keydown      "keydown")
(def ^:private ev-slotchange   "slotchange")

(def ^:private style-text
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

(defn- read-model [^js el]
  (model/normalize
   {:value (du/get-attr el model/attr-value)
    :disabled (du/has-attr? el model/attr-disabled)
    :variant (du/get-attr el model/attr-variant)
    :type (du/get-attr el model/attr-type)}))

(defn- dispatch-item-select! [^js el]
  (let [value (du/get-attr el model/attr-value)]
    (du/dispatch! el model/event-item-select #js {:value (or value "")})))

(defn- handle-click! [^js el ^js evt]
  (let [state (read-model el)]
    (when (or (:disabled state) (= (:type state) type-divider))
      (.preventDefault evt))
    (when (and (not (:disabled state)) (not= (:type state) type-divider))
      (dispatch-item-select! el))))

(defn- handle-keydown! [^js el ^js evt]
  (let [k (.-key evt)
        state (read-model el)]
    (when (and (or (= k "Enter") (= k " "))
               (not (:disabled state))
               (not= (:type state) type-divider))
      (.preventDefault evt)
      (dispatch-item-select! el))))

(defn- handle-icon-slotchange! [^js el ^js slot]
  (let [nodes (.assignedNodes slot)]
    (if (> (alength nodes) 0)
      (du/set-attr! el attr-has-icon "")
      (du/remove-attr! el attr-has-icon))))

(defn- apply-divider! [^js el ^js base]
  (du/set-attr! el attr-role role-separator)
  (du/remove-attr! el attr-tabindex)
  (du/remove-attr! el attr-aria-disabled)
  (du/set-attr! base attr-data-type     type-divider)
  (du/set-attr! base attr-data-variant  "")
  (du/set-attr! base attr-data-disabled val-false))

(defn- apply-menuitem! [^js el ^js base {:keys [variant disabled]}]
  (du/set-attr! el attr-role     role-menuitem)
  (du/set-attr! el attr-tabindex tab-stop)
  (du/set-attr! base attr-data-type     "")
  (du/set-attr! base attr-data-variant  variant)
  (du/set-attr! base attr-data-disabled (if disabled val-true val-false))
  (if disabled
    (du/set-attr! el attr-aria-disabled val-true)
    (du/remove-attr! el attr-aria-disabled)))

(defn- apply-model! [^js el m]
  (let [refs (du/getv el key-refs)
        ^js base (when refs (gobj/get refs rk-base))]
    (when base
      (if (= (:type m) type-divider)
        (apply-divider!  el base)
        (apply-menuitem! el base m)))
    (du/setv! el key-model m)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el key-model)]
    (when (not= new-m old-m)
      (apply-model! el new-m))))

(defn- init-dom! [^js el]
  (let [root (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        base (.createElement js/document "div")
        icon-span (.createElement js/document "span")
        icon-slot (.createElement js/document "slot")
        label-span (.createElement js/document "span")
        label-slot (.createElement js/document "slot")
        divider-hr (.createElement js/document "hr")
        refs #js {}]
    (set! (.-textContent style) style-text)
    (du/set-attr! base attr-part part-base)
    (set! (.-className base) part-base)
    (du/set-attr! icon-span attr-part part-icon)
    (set! (.-className icon-span) "icon-span")
    (du/set-attr! icon-slot attr-name slot-icon)
    (.appendChild icon-span icon-slot)
    (set! (.-className label-span) "label-span")
    (.appendChild label-span label-slot)
    (set! (.-className divider-hr) "divider-hr")
    (.appendChild base icon-span)
    (.appendChild base label-span)
    (.appendChild base divider-hr)
    (.appendChild root style)
    (.appendChild root base)
    (gobj/set refs rk-base base)
    (gobj/set refs rk-icon-span icon-span)
    (gobj/set refs rk-icon-slot icon-slot)
    (du/setv! el key-refs refs)))

(defn- install-listeners! [^js el]
  (let [refs (du/getv el key-refs)
        ^js icon-slot (gobj/get refs rk-icon-slot)
        on-click      (fn handle-item-click   [^js e] (handle-click! el e))
        on-keydown    (fn handle-item-keydown [^js e] (handle-keydown! el e))
        on-slotchange (fn handle-icon-slot    [_]     (handle-icon-slotchange! el icon-slot))
        handlers #js {}]
    (.addEventListener el ev-click   on-click)
    (.addEventListener el ev-keydown on-keydown)
    (.addEventListener icon-slot ev-slotchange on-slotchange)
    (gobj/set handlers hk-click      on-click)
    (gobj/set handlers hk-keydown    on-keydown)
    (gobj/set handlers hk-slotchange on-slotchange)
    (du/setv! el key-handlers handlers)))

(defn- remove-listeners! [^js el]
  (let [handlers (du/getv el key-handlers)
        refs     (du/getv el key-refs)]
    (when handlers
      (let [on-click      (gobj/get handlers hk-click)
            on-keydown    (gobj/get handlers hk-keydown)
            on-slotchange (gobj/get handlers hk-slotchange)
            icon-slot     (when refs (gobj/get refs rk-icon-slot))]
        (.removeEventListener el ev-click   on-click)
        (.removeEventListener el ev-keydown on-keydown)
        (when icon-slot
          (.removeEventListener icon-slot ev-slotchange on-slotchange))))))

(defn- init-element! [^js el]
  (when-not (du/initialized? el key-init)
    (init-dom! el)
    (install-listeners! el)
    (du/mark-initialized! el key-init))
  (update-from-attrs! el)
  el)

(defn- connected! [^js el]
  (if (du/initialized? el key-init)
    (do (install-listeners! el) (update-from-attrs! el))
    (init-element! el)))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (and (not= old-val new-val) (du/initialized? el key-init))
    (update-from-attrs! el)))

(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :disconnected-fn        disconnected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
