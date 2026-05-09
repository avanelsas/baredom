(ns baredom.utils.dom
  (:require [goog.object :as gobj]))

;; ---------------------------------------------------------------------------
;; Instance-field access (Closure Advanced safe)
;; ---------------------------------------------------------------------------

(defn getv [^js el k] (gobj/get el k))

(defn setv! [^js el k v] (gobj/set el k v))

(defn initialized? [^js el key]
  (true? (getv el key)))

(defn mark-initialized! [^js el key]
  (setv! el key true))

;; ---------------------------------------------------------------------------
;; Attribute helpers
;; ---------------------------------------------------------------------------

(defn has-attr?
  [^js el attr-name]
  (.hasAttribute el attr-name))

(defn get-attr
  [^js el attr-name]
  (.getAttribute el attr-name))

(defn set-attr!
  [^js el attr-name value]
  (.setAttribute el attr-name value))

(defn remove-attr!
  [^js el attr-name]
  (.removeAttribute el attr-name))

(defn set-bool-attr!
  [^js el attr-name value]
  (if value
    (.setAttribute el attr-name "")
    (.removeAttribute el attr-name)))

;; ---------------------------------------------------------------------------
;; Event dispatch
;; ---------------------------------------------------------------------------

(defonce ^{:doc "Dev-only extension point for dev/x-trace-history. Holds a
                 1-arg function that receives a CLJS payload describing each
                 dispatch. Reset via reset!. nil by default — when nil, every
                 dispatch is a single atom-deref + nil check (negligible cost)."}
  trace-hook (atom nil))

(defn dispatch!
  "Dispatch a non-cancelable CustomEvent that bubbles and is composed."
  [^js el event-name detail]
  (.dispatchEvent
   el
   (js/CustomEvent.
    event-name
    #js {:detail     detail
         :bubbles    true
         :composed   true
         :cancelable false}))
  (when-some [h @trace-hook]
    (try
      (h {:type               :event/dispatch
          :el                 el
          :event-name         event-name
          :detail             detail
          :cancelable?        false
          :default-prevented? false})
      (catch :default _ nil)))
  nil)

(defn dispatch-cancelable!
  "Dispatch a cancelable CustomEvent. Returns true when NOT cancelled."
  [^js el event-name detail]
  (let [^js ev (js/CustomEvent.
                event-name
                #js {:detail     detail
                     :bubbles    true
                     :composed   true
                     :cancelable true})
        _              (.dispatchEvent el ev)
        prevented?     (.-defaultPrevented ev)]
    (when-some [h @trace-hook]
      (try
        (h {:type               :event/dispatch-cancelable
            :el                 el
            :event-name         event-name
            :detail             detail
            :cancelable?        true
            :default-prevented? prevented?})
        (catch :default _ nil)))
    (not prevented?)))

(defn dispatch-document!
  "Dispatch a non-bubbling, non-composed CustomEvent on document.
   Used for parent notification when the source element is disconnecting
   and normal bubbling cannot reach the parent.
   Single-arity omits the detail field entirely (event.detail === null)."
  ([event-name]
   (.dispatchEvent js/document
                   (js/CustomEvent.
                    event-name
                    #js {:bubbles    false
                         :composed   false
                         :cancelable false}))
   (when-some [h @trace-hook]
     (try
       (h {:type               :event/dispatch-document
           :el                 js/document
           :event-name         event-name
           :detail             nil
           :cancelable?        false
           :default-prevented? false})
       (catch :default _ nil)))
   nil)
  ([event-name detail]
   (.dispatchEvent js/document
                   (js/CustomEvent.
                    event-name
                    #js {:detail     detail
                         :bubbles    false
                         :composed   false
                         :cancelable false}))
   (when-some [h @trace-hook]
     (try
       (h {:type               :event/dispatch-document
           :el                 js/document
           :event-name         event-name
           :detail             detail
           :cancelable?        false
           :default-prevented? false})
       (catch :default _ nil)))
   nil))

;; ---------------------------------------------------------------------------
;; Property accessor installers
;; ---------------------------------------------------------------------------

(defn define-bool-prop!
  "Install a boolean JS property that reflects to/from an HTML attribute."
  [^js proto prop-name attr-name]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn [] (this-as ^js this (has-attr? this attr-name)))
        :set (fn [v] (this-as ^js this (set-bool-attr! this attr-name (boolean v))))}))

(defn- normalize-prop-val
  "Coerce a JS property setter input to the string form an HTML attribute
   needs, or nil when the value should clear the attribute. Treats both
   `nil` and `js/undefined` as clear-the-attribute."
  [v]
  (when (and (some? v) (not= v js/undefined))
    (str v)))

(defn define-string-prop!
  "Install a string JS property that reflects to/from an HTML attribute.
   `default-val` is returned when the attribute is absent (defaults to nil)."
  ([^js proto prop-name attr-name]
   (define-string-prop! proto prop-name attr-name nil))
  ([^js proto prop-name attr-name default-val]
   (.defineProperty
    js/Object proto prop-name
    #js {:configurable true
         :enumerable   true
         :get (fn [] (this-as ^js this (or (get-attr this attr-name) default-val)))
         :set (fn [v] (this-as ^js this
                               (if-let [s (normalize-prop-val v)]
                                 (set-attr! this attr-name s)
                                 (remove-attr! this attr-name))))})))

(defn define-number-prop!
  "Install a numeric JS property that reflects to/from an HTML attribute.
   `default-val` is returned when the attribute is absent or non-numeric."
  [^js proto prop-name attr-name default-val]
  (.defineProperty
   js/Object proto prop-name
   #js {:configurable true
        :enumerable   true
        :get (fn []
               (this-as ^js this
                        (let [raw (get-attr this attr-name)]
                          (if raw
                            (let [n (js/parseFloat raw)]
                              (if (js/isNaN n) default-val n))
                            default-val))))
        :set (fn [v]
               (this-as ^js this
                        (if (and (number? v) (not (js/isNaN v)))
                          (set-attr! this attr-name (str v))
                          (remove-attr! this attr-name))))}))

(defn install-properties!
  "Install JS property accessors on a prototype, driven by a property-api map.
   Each entry is {prop-key {:type 'boolean|'string|'number
                            :reflects-attribute attr-name
                            :default default-val}}.
   Skips entries marked :readonly true. For `'number` entries, an
   omitted `:default` falls back to `0`; declare `:default` explicitly
   when the natural empty value differs."
  [^js proto property-api]
  (doseq [[prop-key {:keys [type reflects-attribute readonly]
                     :as   spec}] property-api]
    (when-not readonly
      (let [js-name (name prop-key)
            attr    reflects-attribute]
        (when attr
          (case type
            boolean (define-bool-prop! proto js-name attr)
            string  (define-string-prop! proto js-name attr
                                         (:default spec))
            number  (define-number-prop! proto js-name attr
                                         (or (:default spec) 0))
            nil))))))
