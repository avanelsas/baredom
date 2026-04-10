(ns baredom.components.x-card.x-card
  (:require
   [goog.object :as gobj]
   [baredom.components.x-card.model :as model]))

(def key-root "__x_card_root")
(def key-style "__x_card_style")
(def key-base "__x_card_base")
(def key-slot "__x_card_slot")
(def key-initialized "__x_card_initialized")

(defn get-instance-value [^js el key]
  (gobj/get el key))

(defn set-instance-value! [^js el key value]
  (gobj/set el key value))

(defn initialized? [^js el]
  (true? (get-instance-value el key-initialized)))

(defn mark-initialized! [^js el]
  (set-instance-value! el key-initialized true))

(defn shadow-root-of [^js el]
  (get-instance-value el key-root))

(defn base-node-of [^js el]
  (get-instance-value el key-base))

(defn read-inputs [^js el]
  {:variant (.getAttribute el model/attr-variant)
   :padding (.getAttribute el model/attr-padding)
   :radius (.getAttribute el model/attr-radius)
   :interactive (.hasAttribute el model/attr-interactive)
   :disabled (.hasAttribute el model/attr-disabled)
   :label (.getAttribute el model/attr-label)})

(defn interactive-active? [state]
  (and (:interactive state) (not (:disabled state))))

(defn dispatch-press! [^js el]
  (.dispatchEvent
   el
   (js/CustomEvent.
    model/event-press
    #js {:detail #js {}
         :bubbles true
         :composed true})))

(defn set-or-remove-attr! [^js el name value]
  (if (some? value)
    (.setAttribute el name value)
    (.removeAttribute el name)))

(defn reflect-host-a11y! [^js el state]
  (set-or-remove-attr! el "role" (:role state))
  (set-or-remove-attr! el "tabindex" (:tabindex state))
  (set-or-remove-attr! el "aria-label" (:aria-label state))
  (set-or-remove-attr! el "aria-disabled" (:aria-disabled state)))

(defn reflect-base-state! [^js base state]
  (.setAttribute base "data-variant" (:variant state))
  (.setAttribute base "data-padding" (:padding state))
  (.setAttribute base "data-radius" (:radius state))
  (set-or-remove-attr! base "data-interactive" (when (:interactive state) "true"))
  (set-or-remove-attr! base "data-disabled" (when (:disabled state) "true")))

(defn style-text []
  "
  :host {
  display: block;
  color-scheme: light dark;

  --x-card-background: rgba(255, 255, 255, 0.92);
  --x-card-color: #111827;
  --x-card-border-color: rgba(17, 24, 39, 0.12);
  --x-card-filled-background: rgba(241, 245, 249, 0.96);
  --x-card-ghost-background: transparent;
  --x-card-hover-background: rgba(15, 23, 42, 0.04);
  --x-card-press-background: rgba(15, 23, 42, 0.08);
  --x-card-shadow: var(--x-shadow-md, 0 10px 24px rgba(15, 23, 42, 0.10));
  --x-card-focus-ring: rgba(59, 130, 246, 0.55);
  --x-card-disabled-opacity: 0.6;

  --x-card-padding-none: 0;
  --x-card-padding-sm: 0.5rem;
  --x-card-padding-md: 1rem;
  --x-card-padding-lg: 1.5rem;

  --x-card-radius-none: 0;
  --x-card-radius-sm: var(--x-radius-sm, 0.375rem);
  --x-card-radius-md: var(--x-radius-md, 0.75rem);
  --x-card-radius-lg: var(--x-radius-lg, 1rem);
  --x-card-radius-xl: 1.5rem;

  --x-card-transition-duration: var(--x-transition-duration, 140ms);
  --x-card-transition-timing: ease;

  outline: none;
  }

  @media (prefers-color-scheme: dark) {
  :host {
  --x-card-background: rgba(15, 23, 42, 0.88);
  --x-card-color: var(--x-color-text, #e5e7eb);
  --x-card-border-color: rgba(148, 163, 184, 0.24);
  --x-card-filled-background: rgba(30, 41, 59, 0.96);
  --x-card-hover-background: rgba(148, 163, 184, 0.10);
  --x-card-press-background: rgba(148, 163, 184, 0.16);
  --x-card-shadow: var(--x-shadow-md, 0 16px 32px rgba(0, 0, 0, 0.32));
  --x-card-focus-ring: rgba(96, 165, 250, 0.6);
  }
  }

  .base {
  box-sizing: border-box;
  display: block;
  min-width: 0;
  background: var(--x-card-background);
  color: var(--x-card-color);
  border: 1px solid transparent;
  border-radius: var(--x-card-radius-lg);
  transition:
  background var(--x-card-transition-duration) var(--x-card-transition-timing),
  box-shadow var(--x-card-transition-duration) var(--x-card-transition-timing),
  border-color var(--x-card-transition-duration) var(--x-card-transition-timing),
  transform var(--x-card-transition-duration) var(--x-card-transition-timing);
  }

  .base[data-variant='elevated'] {
  background: var(--x-card-background);
  box-shadow: var(--x-card-shadow);
  }

  .base[data-variant='outlined'] {
  background: var(--x-card-background);
  border-color: var(--x-card-border-color);
  box-shadow: none;
  }

  .base[data-variant='filled'] {
  background: var(--x-card-filled-background);
  box-shadow: none;
  }

  .base[data-variant='ghost'] {
  background: var(--x-card-ghost-background);
  box-shadow: none;
  }

  .base[data-padding='none'] { padding: var(--x-card-padding-none); }
  .base[data-padding='sm']   { padding: var(--x-card-padding-sm); }
  .base[data-padding='md']   { padding: var(--x-card-padding-md); }
  .base[data-padding='lg']   { padding: var(--x-card-padding-lg); }

  .base[data-radius='none'] { border-radius: var(--x-card-radius-none); }
  .base[data-radius='sm']   { border-radius: var(--x-card-radius-sm); }
  .base[data-radius='md']   { border-radius: var(--x-card-radius-md); }
  .base[data-radius='lg']   { border-radius: var(--x-card-radius-lg); }
  .base[data-radius='xl']   { border-radius: var(--x-card-radius-xl); }

  .base[data-interactive='true']:not([data-disabled='true']) {
  cursor: pointer;
  }

  .base[data-interactive='true']:not([data-disabled='true']):hover {
  background: var(--x-card-hover-background);
  }

  .base[data-interactive='true']:not([data-disabled='true']):active {
  background: var(--x-card-press-background);
  transform: translateY(1px);
  }

  .base[data-disabled='true'] {
  opacity: var(--x-card-disabled-opacity);
  cursor: default;
  transform: none;
  }

  :host(:focus-visible) .base[data-interactive='true']:not([data-disabled='true']) {
  box-shadow:
  0 0 0 3px var(--x-card-focus-ring),
  var(--x-card-shadow);
  }

  @media (prefers-reduced-motion: reduce) {
  .base {
  transition: none;
  }

  .base[data-interactive='true']:not([data-disabled='true']):active {
  transform: none;
  }
  }
  ")

(defn create-style-node []
  (let [node (.createElement js/document "style")]
    (set! (.-textContent node) (style-text))
    node))

(defn create-base-node []
  (let [node (.createElement js/document "div")]
    (.setAttribute node "part" "base")
    (set! (.-className node) "base")
    node))

(defn create-slot-node []
  (.createElement js/document "slot"))

(defn init-shadow-dom! [el]
  (let [root (.attachShadow el #js {:mode "open"})
        style-node (create-style-node)
        base-node (create-base-node)
        slot-node (create-slot-node)]
    (.appendChild base-node slot-node)
    (.appendChild root style-node)
    (.appendChild root base-node)
    (set-instance-value! el key-root root)
    (set-instance-value! el key-style style-node)
    (set-instance-value! el key-base base-node)
    (set-instance-value! el key-slot slot-node)
    root))

(defn render! [el]
  (let [state (model/derive-state (read-inputs el))
        base (base-node-of el)]
    (reflect-host-a11y! el state)
    (reflect-base-state! base state)))

(defn on-click [^js el ^js _event]
  (let [state (model/derive-state (read-inputs el))]
    (when (interactive-active? state)
      (dispatch-press! el))))

(defn on-keydown [^js el ^js event]
  (let [state (model/derive-state (read-inputs el))
        key (.-key event)]
    (when (interactive-active? state)
      (cond
        (= key "Enter")
        (dispatch-press! el)

        (= key " ")
        (do
          (.preventDefault event)
          (dispatch-press! el))

        (= key "Spacebar")
        (do
          (.preventDefault event)
          (dispatch-press! el))

        :else nil))))

(defn install-listeners! [^js el]
  (.addEventListener el "click" (fn [event] (on-click el event)))
  (.addEventListener el "keydown" (fn [event] (on-keydown el event))))

(defn init-element! [^js el]
  (when-not (initialized? el)
    (init-shadow-dom! el)
    (install-listeners! el)
    (mark-initialized! el))
  (render! el)
  el)

(defn connected-callback [^js el]
  (init-element! el))

(defn disconnected-callback [^js _el])

(defn attribute-changed-callback [^js el _name _old-value _new-value]
  (when (initialized? el)
    (render! el)))

(defn install-property-accessors! [^js klass]
  (.defineProperty
   js/Object
   (.-prototype klass)
   "interactive"
   #js {:configurable true
        :enumerable true
        :get (fn []
               (this-as this
                        (.hasAttribute this model/attr-interactive)))
        :set (fn [value]
               (this-as this
                        (if value
                          (.setAttribute this model/attr-interactive "")
                          (.removeAttribute this model/attr-interactive))))})

  (.defineProperty
   js/Object
   (.-prototype klass)
   "disabled"
   #js {:configurable true
        :enumerable true
        :get (fn []
               (this-as this
                        (.hasAttribute this model/attr-disabled)))
        :set (fn [value]
               (this-as this
                        (if value
                          (.setAttribute this model/attr-disabled "")
                          (.removeAttribute this model/attr-disabled))))}))

(defn element-class []
  (let [klass (js* "(class extends HTMLElement {})")]
    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
      (fn []
        (this-as this
                 (connected-callback this))))

    (set! (.-disconnectedCallback (.-prototype klass))
      (fn []
        (this-as this
                 (disconnected-callback this))))

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
