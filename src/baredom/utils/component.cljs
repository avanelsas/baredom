(ns baredom.utils.component
  (:require [baredom.utils.dom :as du]))

(def hold-key
  "The key under which an element offers the hold of its render."
  (js/Symbol.for "x-render-hold"))

;; Untraced: bookkeeping of a hold, nil outside one.
(def ^:private k-held-changes "__xRenderHeldChanges")

(defn- held? [^js el]
  (some? (du/getv el k-held-changes)))

(defn- note-change!
  "Adds `change` to the changes that `el` holds."
  [^js el change]
  (du/setv-untraced! el k-held-changes (conj (du/getv el k-held-changes) change)))

(defn- change!
  "Gives `change` to `attribute-changed-fn`, or holds it while `el` is held."
  [attribute-changed-fn ^js el change]
  (if (held? el)
    (note-change! el change)
    (apply attribute-changed-fn el change)))

(def ^:private attr-disabled "disabled")

(defn- disabled-change
  "The change of the disabled attribute that stands for a control that is now `disabled?`."
  [disabled?]
  (if disabled?
    [attr-disabled nil ""]
    [attr-disabled "" nil]))

(defn- deliver!
  "Gives one held change to `attribute-changed-fn`. An error is reported and not thrown."
  [attribute-changed-fn ^js el change]
  (try
    (apply attribute-changed-fn el change)
    (catch :default e
      (js/reportError e))))

(defn- release!
  "Ends the hold of `el` and delivers each held change, in order."
  [attribute-changed-fn ^js el]
  (let [changes (du/getv el k-held-changes)]
    (du/setv-untraced! el k-held-changes nil)
    (run! (partial deliver! attribute-changed-fn el) changes)))

(defn- with-held-changes!
  "Calls `f` while `el` holds its attribute changes, then releases them.
   An element that is already held only calls `f`."
  [attribute-changed-fn ^js el f]
  (if (held? el)
    (f)
    (do
      (du/setv-untraced! el k-held-changes [])
      (try
        (f)
        (finally
          (release! attribute-changed-fn el))))))

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

(defn- change-record
  "The lifecycle record of `change` on `el`."
  [^js el [n o v]]
  {:type      :lifecycle/attribute-changed
   :el        el
   :attribute n
   :old-value o
   :new-value v})

(defn make-element-class
  "Create a custom element class from a declarative options map.

   Required keys:
     :observed-attributes  — #js [...] array of attribute names
     :connected-fn         — (fn [el] ...) called on connectedCallback
     :attribute-changed-fn — (fn [el name old new] ...) called on attributeChangedCallback

   Optional keys:
     :disconnected-fn      — (fn [el] ...) called on disconnectedCallback
     :form-associated?     true to mark as form-associated element. Its formDisabledCallback
                           arrives as a change of its disabled attribute, with a lifecycle
                           record of that change. The old and new
                           values of that change are not the attribute's: a fieldset writes
                           no attribute.
     :form-reset-fn        — (fn [el] ...) called on formResetCallback
     :setup-prototype-fn   — (fn [proto] ...) install properties/methods on prototype
     :internal?            — when true, skip firing the dev-tool lifecycle hook
                             on every callback. Set this on elements that are
                             themselves part of the dev tooling (e.g. the
                             x-trace-history dock); otherwise their own
                             connect/disconnect/attribute records would
                             pollute the trace they produce."
  [{:keys [observed-attributes
           connected-fn
           disconnected-fn
           attribute-changed-fn
           form-associated?
           form-reset-fn
           setup-prototype-fn
           internal?]}]
  (let [klass (js* "(class extends HTMLElement {})")
        proto (.-prototype klass)
        fire! (if internal? (fn [_]) fire-lifecycle-hook!)]

    (set! (.-observedAttributes klass) observed-attributes)

    (when form-associated?
      (set! (.-formAssociated klass) true))

    (set! (.-connectedCallback proto)
          (fn []
            (this-as ^js this
              (fire! {:type :lifecycle/connected :el this})
              (connected-fn this))))

    (when disconnected-fn
      (set! (.-disconnectedCallback proto)
            (fn []
              (this-as ^js this
                (fire! {:type :lifecycle/disconnected :el this})
                (disconnected-fn this)))))

    (set! (.-attributeChangedCallback proto)
          (fn [n o v]
            (this-as ^js this
              (fire! (change-record this [n o v]))
              (change! attribute-changed-fn this [n o v]))))

    (aset proto hold-key
          (fn [f]
            (this-as ^js this
              (with-held-changes! attribute-changed-fn this f))))

    (when form-associated?
      (set! (.-formDisabledCallback proto)
            (fn [d]
              (this-as ^js this
                (fire! (change-record this (disabled-change d)))
                (change! attribute-changed-fn this (disabled-change d))))))

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
