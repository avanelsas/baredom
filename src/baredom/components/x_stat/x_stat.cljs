(ns baredom.components.x-stat.x-stat
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baredom.components.x-stat.model :as model]))

(def ^:private key-root "__xStatRoot")
(def ^:private key-base "__xStatBase")
(def ^:private key-label "__xStatLabel")
(def ^:private key-value "__xStatValue")
(def ^:private key-hint "__xStatHint")
(def ^:private key-initialized "__xStatInitialized")
(def ^:private key-model "__xStatModel")

(defn- read-model [^js el]
  (model/derive-state
   {:variant (du/get-attr el model/attr-variant)
    :align (du/get-attr el model/attr-align)
    :size (du/get-attr el model/attr-size)
    :emphasis (du/get-attr el model/attr-emphasis)
    :trend (du/get-attr el model/attr-trend)
    :loading (du/has-attr? el model/attr-loading)
    :label (du/get-attr el model/attr-label)
    :value (du/get-attr el model/attr-value)
    :hint (du/get-attr el model/attr-hint)}))

(defn- set-or-remove!
  [^js el attr value]
  (if value
    (du/set-attr! el attr value)
    (du/remove-attr! el attr)))

(defn- apply-host-a11y! [^js el state]
  (du/set-attr! el "role" "figure")
  (set-or-remove! el "aria-busy" (:aria-busy state))
  (set-or-remove! el "aria-label" (:label state)))

(defn- apply-state! [^js base state]
  (.setAttribute base "data-variant" (:variant state))
  (.setAttribute base "data-align" (:align state))
  (.setAttribute base "data-size" (:size state))
  (.setAttribute base "data-emphasis" (:emphasis state))
  (.setAttribute base "data-trend" (:trend state))
  (if (:loading state)
    (.setAttribute base "data-loading" "true")
    (.removeAttribute base "data-loading")))

(defn- apply-model! [^js el state]
  (let [^js base (du/getv el key-base)
        ^js label-node (du/getv el key-label)
        ^js value-node (du/getv el key-value)
        ^js hint-node (du/getv el key-hint)]
    (when base
      (apply-host-a11y! el state)
      (apply-state! base state)
      (set! (.-textContent label-node) (or (:label state) ""))
      (set! (.-textContent value-node) (or (:value state) ""))
      (set! (.-textContent hint-node) (or (:hint state) "")))
    (du/setv! el key-model state)))

(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)
        old-m (du/getv el key-model)]
    (when (not= new-m old-m)
      (apply-model! el new-m))))

(def ^:private css-text
  (str
   ":host{"
   "display:block;color-scheme:light dark;"
   "--x-stat-background:transparent;"
   "--x-stat-color:var(--x-color-text,inherit);"
   "--x-stat-muted-color:var(--x-color-text-muted,rgba(0,0,0,0.55));"
   "--x-stat-border-color:var(--x-color-border,transparent);"
   "--x-stat-radius:var(--x-radius-md,12px);"
   "--x-stat-padding:16px;"
   "--x-stat-gap:6px;"
   "--x-stat-label-color:var(--x-stat-muted-color);"
   "--x-stat-label-size:12px;"
   "--x-stat-value-color:var(--x-stat-color);"
   "--x-stat-value-size:20px;"
   "--x-stat-hint-color:var(--x-stat-muted-color);"
   "--x-stat-hint-size:12px;"
   "--x-stat-icon-color:var(--x-stat-color);"
   "--x-stat-positive-color:var(--x-color-success,#16a34a);"
   "--x-stat-warning-color:var(--x-color-warning,#d97706);"
   "--x-stat-danger-color:var(--x-color-danger,#dc2626);"
   "--x-stat-transition-duration:var(--x-transition-duration,120ms);"
   "--x-stat-transition-timing:var(--x-transition-easing,ease);"
   "--x-stat-loading-opacity:0.6;}"

   "@media (prefers-color-scheme:dark){"
   ":host{"
   "--x-stat-muted-color:var(--x-color-text-muted,rgba(255,255,255,0.55));"
   "--x-stat-positive-color:var(--x-color-success,#4ade80);"
   "--x-stat-warning-color:var(--x-color-warning,#fbbf24);"
   "--x-stat-danger-color:var(--x-color-danger,#f87171);}}"

   ":host([variant='positive']){--x-stat-value-color:var(--x-stat-positive-color);}"
   ":host([variant='warning']){--x-stat-value-color:var(--x-stat-warning-color);}"
   ":host([variant='danger']){--x-stat-value-color:var(--x-stat-danger-color);}"
   ":host([variant='subtle']){--x-stat-value-color:var(--x-stat-muted-color);}"

   "[part=base]{"
   "display:grid;gap:var(--x-stat-gap);"
   "padding:var(--x-stat-padding);"
   "border-radius:var(--x-stat-radius);"
   "background:var(--x-stat-background);"
   "border:1px solid var(--x-stat-border-color);"
   "color:var(--x-stat-color);"
   "transition:"
   "background var(--x-stat-transition-duration) var(--x-stat-transition-timing),"
   "border-color var(--x-stat-transition-duration) var(--x-stat-transition-timing),"
   "color var(--x-stat-transition-duration) var(--x-stat-transition-timing),"
   "opacity var(--x-stat-transition-duration) var(--x-stat-transition-timing);}"

   "[part=base][data-align='center']{text-align:center;}"
   "[part=base][data-align='end']{text-align:end;}"

   "[part=base][data-size='sm'] [part=value]{font-size:calc(var(--x-stat-value-size) * 0.8);}"
   "[part=base][data-size='lg'] [part=value]{font-size:calc(var(--x-stat-value-size) * 1.6);}"
   "[part=base][data-emphasis='high'] [part=value]{font-weight:800;}"

   "[part=icon]{flex:0 0 auto;color:var(--x-stat-icon-color);line-height:1;}"
   "slot[name=icon]::slotted(*){display:block;}"

   "[part=label]{font-size:var(--x-stat-label-size);color:var(--x-stat-label-color);}"
   "[part=value]{"
   "font-weight:600;font-size:var(--x-stat-value-size);"
   "color:var(--x-stat-value-color);}"
   "[part=hint]{font-size:var(--x-stat-hint-size);color:var(--x-stat-hint-color);}"

   "[part=base][data-loading='true']{opacity:var(--x-stat-loading-opacity);}"

   "@media (prefers-reduced-motion:reduce){"
   "[part=base]{transition:none;}}"))

(defn- init-dom! [^js el]
  (let [root (.attachShadow el #js {:mode "open"})
        style-el (.createElement js/document "style")
        base (.createElement js/document "div")
        icon (.createElement js/document "div")
        body (.createElement js/document "div")
        label (.createElement js/document "div")
        value (.createElement js/document "div")
        hint (.createElement js/document "div")
        label-span (.createElement js/document "span")
        value-span (.createElement js/document "span")
        hint-span (.createElement js/document "span")
        icon-slot (.createElement js/document "slot")
        label-slot (.createElement js/document "slot")
        value-slot (.createElement js/document "slot")
        hint-slot (.createElement js/document "slot")
        default-slot (.createElement js/document "slot")]

    (set! (.-textContent style-el) css-text)

    (.setAttribute base "part" "base")
    (.setAttribute icon "part" "icon")
    (.setAttribute body "part" "body")
    (.setAttribute label "part" "label")
    (.setAttribute value "part" "value")
    (.setAttribute hint "part" "hint")

    (.setAttribute icon-slot "name" "icon")
    (.setAttribute label-slot "name" "label")
    (.setAttribute value-slot "name" "value")
    (.setAttribute hint-slot "name" "hint")

    (.appendChild icon icon-slot)
    (.appendChild label label-slot)
    (.appendChild label label-span)
    (.appendChild value value-slot)
    (.appendChild value value-span)
    (.appendChild hint hint-slot)
    (.appendChild hint hint-span)

    (.appendChild body label)
    (.appendChild body value)
    (.appendChild body hint)
    (.appendChild body default-slot)

    (.appendChild base icon)
    (.appendChild base body)

    (.appendChild root style-el)
    (.appendChild root base)

    (du/setv! el key-root root)
    (du/setv! el key-base base)
    (du/setv! el key-label label-span)
    (du/setv! el key-value value-span)
    (du/setv! el key-hint hint-span)))

(defn- init-element! [^js el]
  (when-not (du/initialized? el key-initialized)
    (init-dom! el)
    (du/mark-initialized! el key-initialized))
  (update-from-attrs! el)
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
