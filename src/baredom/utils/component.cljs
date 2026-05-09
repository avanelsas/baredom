(ns baredom.utils.component)

(defonce ^{:doc "Dev-only extension point for dev/x-trace-history. Holds a
                 1-arg function called on each lifecycle callback (connected,
                 disconnected, attribute-changed). nil by default — each
                 callback site is a single atom-deref + nil check when off."}
  lifecycle-hook (atom nil))

(defn- fire-lifecycle-hook!
  "Invoke the lifecycle hook with a payload, swallowing any exception so
   instrumentation never breaks the host component."
  [payload]
  (when-some [h @lifecycle-hook]
    (try (h payload) (catch :default _ nil))))

(defn make-element-class
  "Create a custom element class from a declarative options map.

   Required keys:
     :observed-attributes  — #js [...] array of attribute names
     :connected-fn         — (fn [el] ...) called on connectedCallback
     :attribute-changed-fn — (fn [el name old new] ...) called on attributeChangedCallback

   Optional keys:
     :disconnected-fn      — (fn [el] ...) called on disconnectedCallback
     :form-associated?     — true to mark as form-associated element
     :form-disabled-fn     — (fn [el disabled?] ...) called on formDisabledCallback
     :form-reset-fn        — (fn [el] ...) called on formResetCallback
     :setup-prototype-fn   — (fn [proto] ...) install properties/methods on prototype"
  [{:keys [observed-attributes
           connected-fn
           disconnected-fn
           attribute-changed-fn
           form-associated?
           form-disabled-fn
           form-reset-fn
           setup-prototype-fn]}]
  (let [klass (js* "(class extends HTMLElement {})")
        proto (.-prototype klass)]

    (set! (.-observedAttributes klass) observed-attributes)

    (when form-associated?
      (set! (.-formAssociated klass) true))

    (set! (.-connectedCallback proto)
          (fn []
            (this-as ^js this
              (fire-lifecycle-hook! {:type :lifecycle/connected :el this})
              (connected-fn this))))

    (when disconnected-fn
      (set! (.-disconnectedCallback proto)
            (fn []
              (this-as ^js this
                (fire-lifecycle-hook! {:type :lifecycle/disconnected :el this})
                (disconnected-fn this)))))

    (set! (.-attributeChangedCallback proto)
          (fn [n o v]
            (this-as ^js this
              (fire-lifecycle-hook! {:type      :lifecycle/attribute-changed
                                     :el        this
                                     :attribute n
                                     :old-value o
                                     :new-value v})
              (attribute-changed-fn this n o v))))

    (when form-disabled-fn
      (set! (.-formDisabledCallback proto)
            (fn [d] (this-as ^js this (form-disabled-fn this d)))))

    (when form-reset-fn
      (set! (.-formResetCallback proto)
            (fn [] (this-as ^js this (form-reset-fn this)))))

    (when setup-prototype-fn
      (setup-prototype-fn proto))

    klass))

(defn register!
  "Register a custom element if not already defined."
  [tag-name class-opts]
  (when-not (.get js/customElements tag-name)
    (.define js/customElements tag-name (make-element-class class-opts))))
