(ns app.components.x-navbar.x-navbar
  (:require [app.components.x-navbar.model :as model]))

(def state-key "__xNavbarState")
(def focus-visible-key "__xNavbarFocusVisible")

(defn get-prop
  [obj k]
  (aget obj k))

(defn set-prop!
  [obj k v]
  (aset obj k v))

(defn has-attr?
  [el attr-name]
  (.hasAttribute el attr-name))

(defn get-attr
  [el attr-name]
  (.getAttribute el attr-name))

(defn set-bool-attr!
  [el attr-name value]
  (if value
    (.setAttribute el attr-name "")
    (.removeAttribute el attr-name)))

(defn get-el-state
  [el]
  (get-prop el state-key))

(defn set-el-state!
  [el state]
  (set-prop! el state-key state))

(defn get-focus-visible
  [el]
  (= true (get-prop el focus-visible-key)))

(defn set-focus-visible!
  [el value]
  (set-prop! el focus-visible-key (boolean value)))

(defn read-public-state
  [el]
  (model/public-state
   {:label (get-attr el model/attr-label)
    :orientation (get-attr el model/attr-orientation)
    :variant (get-attr el model/attr-variant)
    :sticky (has-attr? el model/attr-sticky)
    :elevated (has-attr? el model/attr-elevated)
    :breakpoint (get-attr el model/attr-breakpoint)
    :alignment (get-attr el model/attr-alignment)}))

(defn dispatch!
  [el event-name detail]
  (.dispatchEvent
   el
   (js/CustomEvent.
    event-name
    #js {:detail detail
         :bubbles true
         :composed true
         :cancelable false})))

(defn assigned-nodes
  [slot-el]
  (.assignedNodes slot-el #js {:flatten true}))

(defn slot-has-content?
  [slot-el]
  (> (alength (assigned-nodes slot-el)) 0))

(defn source-from-event
  [event]
  (cond
    (instance? js/PointerEvent event) "pointer"
    (instance? js/MouseEvent event) "pointer"
    (instance? js/KeyboardEvent event) "keyboard"
    :else "programmatic"))

(defn closest-anchor
  [node]
  (when node
    (.closest node "a")))

(defn node-in-assigned-nodes?
  [target nodes]
  (loop [idx 0]
    (if (< idx (alength nodes))
      (let [node (aget nodes idx)]
        (if (or (= node target)
                (and (= (.-nodeType node) js/Node.ELEMENT_NODE)
                     (.contains node target)))
          true
          (recur (inc idx))))
      false)))

(defn target-in-brand-slot?
  [state target]
  (node-in-assigned-nodes? target (assigned-nodes (aget state "brand-slot"))))

(def style-text
  (str
   ":host{"
   "display:block;"
   "color-scheme:light dark;"
   "--x-navbar-height:4rem;"
   "--x-navbar-padding-inline:1rem;"
   "--x-navbar-gap:0.75rem;"
   "--x-navbar-bg:rgba(255,255,255,0.88);"
   "--x-navbar-color:#0f172a;"
   "--x-navbar-border:rgba(148,163,184,0.22);"
   "--x-navbar-divider:rgba(148,163,184,0.18);"
   "--x-navbar-shadow:0 8px 24px rgba(15,23,42,0.08);"
   "--x-navbar-focus-ring:#60a5fa;"
   "--x-navbar-z-index:40;"
   "--x-navbar-radius:1rem;"
   "--x-navbar-transition-duration:180ms;"
   "--x-navbar-transition-easing:cubic-bezier(0.2,0,0,1);"
   "--x-navbar-align-items:center;"
   "}"
   "@media (prefers-color-scheme: dark){"
   ":host{"
   "--x-navbar-bg:rgba(15,23,42,0.84);"
   "--x-navbar-color:#e5e7eb;"
   "--x-navbar-border:rgba(51,65,85,0.9);"
   "--x-navbar-divider:rgba(51,65,85,0.8);"
   "--x-navbar-shadow:0 14px 36px rgba(0,0,0,0.35);"
   "--x-navbar-focus-ring:#93c5fd;"
   "}"
   "}"
   ":host([hidden]){display:none;}"
   "[part='base']{"
   "display:block;"
   "color:var(--x-navbar-color);"
   "background:var(--x-navbar-bg);"
   "border:1px solid var(--x-navbar-border);"
   "border-radius:var(--x-navbar-radius);"
   "box-shadow:none;"
   "backdrop-filter:blur(14px);"
   "-webkit-backdrop-filter:blur(14px);"
   "transition:"
   "background var(--x-navbar-transition-duration) var(--x-navbar-transition-easing),"
   "border-color var(--x-navbar-transition-duration) var(--x-navbar-transition-easing),"
   "box-shadow var(--x-navbar-transition-duration) var(--x-navbar-transition-easing);"
   "}"
   ":host([sticky]) [part='base']{"
   "position:sticky;"
   "top:0;"
   "z-index:var(--x-navbar-z-index);"
   "}"
   ":host([elevated]) [part='base']{"
   "box-shadow:var(--x-navbar-shadow);"
   "}"
   "[part='base'][data-variant='subtle']{"
   "background:transparent;"
   "backdrop-filter:none;"
   "-webkit-backdrop-filter:none;"
   "box-shadow:none;"
   "}"
   "[part='base'][data-variant='inverted']{"
   "background:#0f172a;"
   "color:#f8fafc;"
   "border-color:#1e293b;"
   "}"
   "@media (prefers-color-scheme: dark){"
   "[part='base'][data-variant='inverted']{"
   "background:#020617;"
   "color:#f8fafc;"
   "border-color:#172033;"
   "}"
   "}"
   "[part='base'][data-variant='transparent']{"
   "background:transparent;"
   "border-color:transparent;"
   "box-shadow:none;"
   "backdrop-filter:none;"
   "-webkit-backdrop-filter:none;"
   "}"
   "[part='bar']{"
   "display:grid;"
   "grid-template-columns:auto auto 1fr auto auto auto;"
   "align-items:var(--x-navbar-align-items, center);"
   "gap:var(--x-navbar-gap);"
   "min-block-size:var(--x-navbar-height);"
   "padding-inline:var(--x-navbar-padding-inline);"
   "}"
   "[part='bar'][data-alignment='start']{justify-items:start;}"
   "[part='bar'][data-alignment='center']{justify-items:center;}"
   "[part='bar'][data-alignment='space-between']{justify-items:stretch;}"
   "[part='base'][data-orientation='vertical'] [part='bar']{"
   "grid-template-columns:1fr;"
   "grid-auto-rows:auto;"
   "align-items:var(--x-navbar-align-items, center);"
   "padding-block:0.75rem;"
   "}"
   "[part='brand'],[part='start'],[part='nav'],[part='actions'],[part='toggle'],[part='end']{"
   "display:flex;"
   "align-items:inherit;"
   "min-inline-size:0;"
   "}"
   "[part='nav']{gap:var(--x-navbar-gap);}"
   "[part='actions'],[part='end'],[part='toggle']{justify-self:end;}"
   "[part='base'][data-orientation='vertical'] [part='actions'],"
   "[part='base'][data-orientation='vertical'] [part='end'],"
   "[part='base'][data-orientation='vertical'] [part='toggle']{justify-self:start;}"
   "[part='brand'][data-has-brand='false'],"
   "[part='start'][data-has-start='false'],"
   "[part='actions'][data-has-actions='false'],"
   "[part='toggle'][data-has-toggle='false'],"
   "[part='end'][data-has-end='false']{display:none;}"
   "slot[name='brand'],slot[name='start'],slot[name='actions'],slot[name='toggle'],slot[name='end']{display:contents;}"
   "slot{display:contents;}"
   ":host(:focus-within) [part='base']{box-shadow:var(--x-navbar-shadow);}"
   ":host([data-focus-visible-within='true']) [part='base']{"
   "box-shadow:0 0 0 2px var(--x-navbar-focus-ring), var(--x-navbar-shadow);"
   "}"
   "@media (prefers-reduced-motion: reduce){"
   "[part='base']{transition:none;}"
   "}"))

(defn create-el
  [tag]
  (.createElement js/document tag))

(defn create-shadow!
  [el]
  (let [root (.attachShadow el #js {:mode "open"})
        style-el (create-el "style")
        nav-el (create-el "nav")
        bar-el (create-el "div")
        brand-el (create-el "div")
        start-el (create-el "div")
        nav-slot-wrap-el (create-el "div")
        actions-el (create-el "div")
        toggle-el (create-el "div")
        end-el (create-el "div")
        brand-slot-el (create-el "slot")
        start-slot-el (create-el "slot")
        default-slot-el (create-el "slot")
        actions-slot-el (create-el "slot")
        toggle-slot-el (create-el "slot")
        end-slot-el (create-el "slot")]

    (set! (.-textContent style-el) style-text)

    (.setAttribute nav-el "part" "base")
    (.setAttribute bar-el "part" "bar")
    (.setAttribute brand-el "part" "brand")
    (.setAttribute start-el "part" "start")
    (.setAttribute nav-slot-wrap-el "part" "nav")
    (.setAttribute actions-el "part" "actions")
    (.setAttribute toggle-el "part" "toggle")
    (.setAttribute end-el "part" "end")

    (.setAttribute brand-slot-el "name" model/slot-brand)
    (.setAttribute start-slot-el "name" model/slot-start)
    (.setAttribute actions-slot-el "name" model/slot-actions)
    (.setAttribute toggle-slot-el "name" model/slot-toggle)
    (.setAttribute end-slot-el "name" model/slot-end)

    (.appendChild brand-el brand-slot-el)
    (.appendChild start-el start-slot-el)
    (.appendChild nav-slot-wrap-el default-slot-el)
    (.appendChild actions-el actions-slot-el)
    (.appendChild toggle-el toggle-slot-el)
    (.appendChild end-el end-slot-el)

    (.appendChild bar-el brand-el)
    (.appendChild bar-el start-el)
    (.appendChild bar-el nav-slot-wrap-el)
    (.appendChild bar-el actions-el)
    (.appendChild bar-el toggle-el)
    (.appendChild bar-el end-el)

    (.appendChild nav-el bar-el)

    (.appendChild root style-el)
    (.appendChild root nav-el)

    #js {:root root
         :base nav-el
         :bar bar-el
         :brand brand-el
         :start start-el
         :nav nav-slot-wrap-el
         :actions actions-el
         :toggle toggle-el
         :end end-el
         :brand-slot brand-slot-el
         :start-slot start-slot-el
         :default-slot default-slot-el
         :actions-slot actions-slot-el
         :toggle-slot toggle-slot-el
         :end-slot end-slot-el}))

(defn render!
  [el state]
  (let [base-el (aget state "base")
        bar-el (aget state "bar")
        brand-el (aget state "brand")
        start-el (aget state "start")
        nav-el (aget state "nav")
        actions-el (aget state "actions")
        toggle-el (aget state "toggle")
        end-el (aget state "end")
        brand-slot-el (aget state "brand-slot")
        start-slot-el (aget state "start-slot")
        actions-slot-el (aget state "actions-slot")
        toggle-slot-el (aget state "toggle-slot")
        end-slot-el (aget state "end-slot")
        public-state (read-public-state el)
        has-brand? (slot-has-content? brand-slot-el)
        has-start? (slot-has-content? start-slot-el)
        has-actions? (slot-has-content? actions-slot-el)
        has-toggle? (slot-has-content? toggle-slot-el)
        has-end? (slot-has-content? end-slot-el)
        label (model/landmark-label public-state)]

    (if label
      (.setAttribute base-el "aria-label" label)
      (.removeAttribute base-el "aria-label"))

    (.setAttribute base-el "data-variant" (:variant public-state))
    (.setAttribute base-el "data-orientation" (:orientation public-state))
    (.setAttribute base-el "data-breakpoint" (:breakpoint public-state))
    (.setAttribute bar-el "data-alignment" (:alignment public-state))

    (.setAttribute brand-el "data-has-brand" (if has-brand? "true" "false"))
    (.setAttribute start-el "data-has-start" (if has-start? "true" "false"))
    (.setAttribute actions-el "data-has-actions" (if has-actions? "true" "false"))
    (.setAttribute toggle-el "data-has-toggle" (if has-toggle? "true" "false"))
    (.setAttribute end-el "data-has-end" (if has-end? "true" "false"))))

(defn setup-delegated-events!
  [el base-el]
  (.addEventListener
   base-el
   "click"
   (fn [event]
     (let [state (get-el-state el)
           target (.-target event)
           anchor (closest-anchor target)
           source (source-from-event event)]
       (when anchor
         (dispatch! el
                    model/event-navigate
                    #js {:href (or (.getAttribute anchor "href") "")
                         :source source}))
       (when (target-in-brand-slot? state target)
         (dispatch! el
                    model/event-brand-activate
                    #js {:source source})))))

  (.addEventListener
   base-el
   "focusin"
   (fn [event]
     (let [target (.-target event)]
       (when (and target (.matches target ":focus-visible"))
         (when-not (get-focus-visible el)
           (set-focus-visible! el true)
           (.setAttribute el "data-focus-visible-within" "true")
           (dispatch! el model/event-focus-visible #js {}))))))

  (.addEventListener
   base-el
   "focusout"
   (fn [event]
     (let [related (.-relatedTarget event)]
       (when-not (and related (.contains el related))
         (set-focus-visible! el false)
         (.removeAttribute el "data-focus-visible-within"))))))

(defn setup-slots!
  [el state]
  (let [rerender (fn [_]
                   (render! el state))]
    (.addEventListener (aget state "brand-slot") "slotchange" rerender)
    (.addEventListener (aget state "start-slot") "slotchange" rerender)
    (.addEventListener (aget state "default-slot") "slotchange" rerender)
    (.addEventListener (aget state "actions-slot") "slotchange" rerender)
    (.addEventListener (aget state "toggle-slot") "slotchange" rerender)
    (.addEventListener (aget state "end-slot") "slotchange" rerender)))

(defn connected!
  [el]
  (when-not (get-el-state el)
    (let [state (create-shadow! el)
          base-el (aget state "base")]
      (set-focus-visible! el false)
      (setup-delegated-events! el base-el)
      (setup-slots! el state)
      (set-el-state! el state)))
  (render! el (get-el-state el)))

(defn disconnected!
  [el]
  (set-focus-visible! el false))

(defn attribute-changed!
  [el _name _old-value _new-value]
  (when-let [state (get-el-state el)]
    (render! el state)))

(defn define-bool-prop!
  [proto prop-name attr-name]
  (.defineProperty
   js/Object
   proto
   prop-name
   #js {:configurable true
        :enumerable true
        :get (fn []
               (this-as this
                        (has-attr? this attr-name)))
        :set (fn [value]
               (this-as this
                        (set-bool-attr! this attr-name (boolean value))))}))

(defn make-constructor
  []
  (let [ctor-ref (atom nil)
        ctor (fn []
               (js/Reflect.construct js/HTMLElement #js [] @ctor-ref))]
    (reset! ctor-ref ctor)
    ctor))

(defn define-element!
  []
  (when-not (.get js/customElements model/tag-name)
    (let [proto (js/Object.create (.-prototype js/HTMLElement))
          ctor (make-constructor)]

      (js/Object.setPrototypeOf ctor js/HTMLElement)
      (aset proto "constructor" ctor)

      (define-bool-prop! proto "sticky" model/attr-sticky)
      (define-bool-prop! proto "elevated" model/attr-elevated)

      (aset proto "connectedCallback"
            (fn []
              (this-as this
                       (connected! this))))

      (aset proto "disconnectedCallback"
            (fn []
              (this-as this
                       (disconnected! this))))

      (aset proto "attributeChangedCallback"
            (fn [name old-value new-value]
              (this-as this
                       (attribute-changed! this name old-value new-value))))

      (aset ctor "observedAttributes" model/observed-attributes)
      (aset ctor "prototype" proto)

      (.define js/customElements model/tag-name ctor))))

(defn init!
  []
  (define-element!))
