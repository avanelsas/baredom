(ns baredom.components.x-stat.x-stat
  (:require
[baredom.utils.component :as component]
               [baredom.utils.dom :as du]
   [baredom.components.x-stat.model :as model]))

(def key-root "__x_stat_root")
(def key-base "__x_stat_base")
(def key-label "__x_stat_label")
(def key-value "__x_stat_value")
(def key-hint "__x_stat_hint")
(def key-initialized "__x_stat_initialized")

(defn read-inputs [el]
  {:variant (.getAttribute el model/attr-variant)
   :align (.getAttribute el model/attr-align)
   :size (.getAttribute el model/attr-size)
   :emphasis (.getAttribute el model/attr-emphasis)
   :trend (.getAttribute el model/attr-trend)
   :loading (.hasAttribute el model/attr-loading)
   :label (.getAttribute el model/attr-label)
   :value (.getAttribute el model/attr-value)
   :hint (.getAttribute el model/attr-hint)})

(defn set-or-remove!
  [el attr value]
  (if value
    (.setAttribute el attr value)
    (.removeAttribute el attr)))

(defn apply-host-a11y! [el state]
  (.setAttribute el "role" "figure")
  (set-or-remove! el "aria-busy" (:aria-busy state))
  (set-or-remove! el "aria-label" (:label state)))

(defn apply-state! [base state]

  (.setAttribute base "data-variant" (:variant state))
  (.setAttribute base "data-align" (:align state))
  (.setAttribute base "data-size" (:size state))
  (.setAttribute base "data-emphasis" (:emphasis state))
  (.setAttribute base "data-trend" (:trend state))

  (if (:loading state)
    (.setAttribute base "data-loading" "true")
    (.removeAttribute base "data-loading")))

(defn render! [el]
  (let [state (model/derive-state (read-inputs el))
        base (du/getv el key-base)
        label-node (du/getv el key-label)
        value-node (du/getv el key-value)
        hint-node (du/getv el key-hint)]
    (when base
      (apply-host-a11y! el state)
      (apply-state! base state)
      (set! (.-textContent label-node) (or (:label state) ""))
      (set! (.-textContent value-node) (or (:value state) ""))
      (set! (.-textContent hint-node) (or (:hint state) "")))))

(def css-text
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

(defn init-dom! [el]
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

(defn init-element! [el]

  (when-not (du/initialized? el key-initialized)
    (init-dom! el)
    (du/mark-initialized! el key-initialized))

  (render! el)
  el)

(defn connected-callback [el]
  (init-element! el))

(defn attribute-changed-callback [el _ _ _]
  (if (du/initialized? el key-initialized)
    (render! el)
    (init-element! el)))

(defn- string-property [^js proto attr-name]
  (.defineProperty js/Object proto attr-name
                   #js {:get (fn []
                               (this-as ^js this
                                        (or (.getAttribute this attr-name) "")))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this attr-name (str v))
                                          (.removeAttribute this attr-name))))
                        :enumerable true :configurable true}))

(defn- install-property-accessors! [^js proto]
  (string-property proto model/attr-variant)
  (string-property proto model/attr-align)
  (string-property proto model/attr-size)
  (string-property proto model/attr-emphasis)
  (string-property proto model/attr-trend)
  (string-property proto model/attr-label)
  (string-property proto model/attr-value)
  (string-property proto model/attr-hint)

  (.defineProperty js/Object proto model/attr-loading
                   #js {:get (fn []
                               (this-as ^js this (.hasAttribute this model/attr-loading)))
                        :set (fn [v]
                               (this-as ^js this
                                        (if v
                                          (.setAttribute this model/attr-loading "")
                                          (.removeAttribute this model/attr-loading))))
                        :enumerable true :configurable true}))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected-callback
     :attribute-changed-fn   attribute-changed-callback
     :setup-prototype-fn     install-property-accessors!}))
