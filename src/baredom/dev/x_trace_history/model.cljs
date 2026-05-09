(ns baredom.dev.x-trace-history.model
  "Pure functions for x-trace-history: schema, ring-buffer ops, JS record
   construction. Effects (atom mutation, hook installation, JS API) live in
   recorder.cljs.")

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

(defn- safe-detail
  "Coerce an event detail into something JSON-friendly. JS objects pass
   through clj->js untouched; CLJS data is converted; cyclic / non-coercible
   values fall back to (str detail) so the recorder never throws."
  [detail]
  (try
    (cond
      (nil? detail)        nil
      (undefined? detail)  nil
      :else                (clj->js detail))
    (catch :default _
      (str detail))))

(defn make-record
  "Construct a JS-shape record from a hook payload, monotonic id, and a
   timestamp (performance.now() value).

   Payload keys: :type :tag :event-name :detail :cancelable? :default-prevented?

   Uses string-keyed js-obj so Closure Advanced does not rename the schema
   field names — consumers read these properties from the wire."
  [{:keys [type tag event-name detail cancelable? default-prevented?]} id t]
  (js-obj
   "schemaVersion"    schema-version
   "id"               id
   "t"                t
   "type"             (format-type type)
   "tag"              tag
   "eventName"        event-name
   "detail"           (safe-detail detail)
   "cancelable"       (boolean cancelable?)
   "defaultPrevented" (boolean default-prevented?)))

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
