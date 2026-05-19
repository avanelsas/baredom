(ns baredom.components.x-tab.x-tab
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baredom.components.x-tab.model :as model]))

(def ^:private key-base "__xTabBase")
(def ^:private key-initialized "__xTabInitialized")
(def ^:private key-model "__xTabModel")

(defn- read-inputs [^js el]
  {:value (du/get-attr el model/attr-value)
   :selected (du/has-attr? el model/attr-selected)
   :disabled (du/has-attr? el model/attr-disabled)
   :orientation (du/get-attr el model/attr-orientation)
   :size (du/get-attr el model/attr-size)
   :variant (du/get-attr el model/attr-variant)
   :label (du/get-attr el model/attr-label)
   :controls (du/get-attr el model/attr-controls)})

(def ^:private style-text
  (str
   ":host{"
   "display:inline-block;color-scheme:light dark;outline:none;"
   "--x-tab-color:var(--x-color-text,#0f172a);"
   "--x-tab-hover-background:rgba(15,23,42,0.05);"
   "--x-tab-selected-color:var(--x-color-primary-hover,#1d4ed8);"
   "--x-tab-selected-background:rgba(59,130,246,0.12);"
   "--x-tab-selected-border-color:transparent;}"
   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-tab-color:var(--x-color-text-muted,#94a3b8);"
   "--x-tab-hover-background:rgba(248,250,252,0.06);"
   "--x-tab-selected-color:var(--x-color-focus-ring,#60a5fa);"
   "--x-tab-selected-background:rgba(59,130,246,0.18);"
   "--x-tab-selected-border-color:var(--x-color-focus-ring,#60a5fa);}}"
   ".base{box-sizing:border-box;display:inline-flex;align-items:center;justify-content:center;"
   "cursor:pointer;user-select:none;"
   "padding:var(--x-tab-padding-md,10px 16px);border-radius:var(--x-tab-radius,8px);"
   "color:var(--x-tab-color);background:var(--x-tab-background,transparent);"
   "border:1px solid var(--x-tab-border-color,transparent);"
   "transition:background var(--x-tab-transition-duration,120ms) var(--x-tab-transition-timing,ease),"
   "color var(--x-tab-transition-duration,120ms) var(--x-tab-transition-timing,ease),"
   "border-color var(--x-tab-transition-duration,120ms) var(--x-tab-transition-timing,ease);}"
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

(defn- apply-host-a11y! [^js el state]
  (du/set-attr! el "role" "tab")
  (du/set-attr! el "aria-selected" (if (:selected state) "true" "false"))

  (if (:disabled state)
    (du/set-attr! el "aria-disabled" "true")
    (du/remove-attr! el "aria-disabled"))

  (du/set-attr! el "tabindex" (:tabindex state))

  (let [controls (:controls state)
        label (:label state)]
    (if (and controls (not= controls ""))
      (du/set-attr! el "aria-controls" controls)
      (du/remove-attr! el "aria-controls"))

    (if (and label (not= label ""))
      (du/set-attr! el "aria-label" label)
      (du/remove-attr! el "aria-label"))))

(defn- apply-state! [^js base state]
  (du/set-attr! base "data-selected" (if (:selected state) "true" "false"))
  (du/set-attr! base "data-disabled" (if (:disabled state) "true" "false"))
  (du/set-attr! base "data-size" (:size state))
  (du/set-attr! base "data-variant" (:variant state))
  (du/set-attr! base "data-orientation" (:orientation state)))

(defn- apply-model! [^js el state]
  (let [base (du/getv el key-base)]
    (when base
      (apply-host-a11y! el state)
      (apply-state! base state))
    (du/setv! el key-model state)))

(defn- update-from-attrs! [^js el]
  (let [new-m (model/normalize (read-inputs el))
        old-m (du/getv el key-model)]
    (when (not= new-m old-m)
      (apply-model! el new-m))))

(defn- render! [^js el]
  (update-from-attrs! el))

(defn- dispatch-select! [^js el]

  (let [value (du/get-attr el model/attr-value)]

    (du/dispatch! el model/event-tab-select #js {:value (or value "")})))

(defn- activate! [^js el]

  (let [{:keys [selected disabled]}
        (model/normalize (read-inputs el))]

    (when (and (not disabled) (not selected))
      (dispatch-select! el))))

(defn- on-click [^js el _]
  (activate! el))

(defn- on-keydown [^js el ^js e]

  (let [k (.-key e)]

    (when (or (= k "Enter") (= k " "))
      (.preventDefault e)
      (activate! el))))

(defn- install-listeners! [^js el]
  (.addEventListener el "click"
                     (fn handle-tab-click [e] (on-click el e)))
  (.addEventListener el "keydown"
                     (fn handle-tab-keydown [e] (on-keydown el e))))

(defn- init-dom! [^js el]

  (let [root (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        base (.createElement js/document "div")
        slot (.createElement js/document "slot")]

    (set! (.-textContent style) style-text)

    (du/set-attr! base "part" "base")
    (set! (.-className base) "base")

    (.appendChild base slot)
    (.appendChild root style)
    (.appendChild root base)

    (du/setv! el key-base base)))

(defn- init-element! [^js el]

  (when-not (du/initialized? el key-initialized)
    (init-dom! el)
    (install-listeners! el)
    (du/mark-initialized! el key-initialized))

  (render! el)
  el)

(defn- connected! [^js el]
  (init-element! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (not= old-val new-val)
    (if (du/initialized? el key-initialized)
      (update-from-attrs! el)
      (init-element! el))))

(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :attribute-changed-fn   attribute-changed!
     :setup-prototype-fn     install-property-accessors!}))
