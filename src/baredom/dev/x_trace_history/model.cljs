(ns baredom.dev.x-trace-history.model
  "Pure functions for x-trace-history: schema, ring-buffer ops, JS record
   construction. Effects (atom mutation, hook installation, JS API) live in
   recorder.cljs."
  (:require [goog.object :as gobj]))

(def schema-version 1)

(def default-capacity 5000)

(def empty-records #queue [])

;; ---------------------------------------------------------------------------
;; Tag extraction
;; ---------------------------------------------------------------------------

(defn tag-of
  "Returns the lowercase tag name of a DOM element, or 'document' when the
   target is js/document or anything without a tagName."
  [^js el]
  (or (some-> el (.-tagName) (.toLowerCase))
      "document"))

;; ---------------------------------------------------------------------------
;; Record construction
;; ---------------------------------------------------------------------------

(defn- format-type
  "Convert a hook-payload :type keyword to its schema string. Namespaced
   keywords become 'ns/name'; plain keywords become 'name'."
  [t]
  (if-let [ns (namespace t)]
    (str ns "/" (name t))
    (name t)))

(defn- safe-value
  "Coerce a value (event detail, instance-field value, etc.) into something
   JSON-friendly. JS objects pass through clj->js untouched; CLJS data is
   converted; cyclic / non-coercible values fall back to (str v) so the
   recorder never throws."
  [v]
  (try
    (cond
      (nil? v)        nil
      (undefined? v)  nil
      :else           (clj->js v))
    (catch :default _
      (str v))))

(defn make-record
  "Construct a JS-shape record from a hook payload, monotonic id, and a
   timestamp (performance.now() value).

   Common payload keys: :type :tag :component-id.
   Type-specific keys are documented in docs/x-trace-history-schema.md.

   Uses string-keyed js-obj so Closure Advanced does not rename the schema
   field names — consumers read these properties from the wire."
  [{:keys [type tag component-id] :as payload} id t]
  (let [^js r (js-obj
               "schemaVersion" schema-version
               "id"            id
               "t"             t
               "type"          (format-type type)
               "tag"           tag
               "componentId"   component-id)]
    (case type
      (:event/dispatch :event/dispatch-cancelable :event/dispatch-document)
      (doto r
        (gobj/set "eventName"        (:event-name payload))
        (gobj/set "detail"           (safe-value (:detail payload)))
        (gobj/set "cancelable"       (boolean (:cancelable? payload)))
        (gobj/set "defaultPrevented" (boolean (:default-prevented? payload))))

      :state/instance-field-set
      (doto r
        (gobj/set "field" (:field payload))
        (gobj/set "value" (safe-value (:value payload))))

      :dom/attribute-set
      (doto r
        (gobj/set "attribute" (:attribute payload))
        (gobj/set "value"     (:value payload)))

      :dom/attribute-removed
      (doto r
        (gobj/set "attribute" (:attribute payload)))

      :lifecycle/attribute-changed
      (doto r
        (gobj/set "attribute" (:attribute payload))
        (gobj/set "oldValue"  (:old-value payload))
        (gobj/set "newValue"  (:new-value payload)))

      ;; :lifecycle/connected, :lifecycle/disconnected, anything else
      r)))

;; ---------------------------------------------------------------------------
;; Ring buffer
;; ---------------------------------------------------------------------------

(defn push-record
  "Push a record onto a ring-buffer (PersistentQueue). When the buffer is at
   or beyond capacity, drop the oldest record before pushing. Returns the
   new buffer."
  [records record capacity]
  (if (>= (count records) capacity)
    (conj (pop records) record)
    (conj records record)))
