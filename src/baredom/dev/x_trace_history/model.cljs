(ns baredom.dev.x-trace-history.model
  "Pure functions and constants for x-trace-history: schema, ring-buffer ops,
   JS record construction, dock filtering / preview formatting, dock CSS,
   activation predicate. Effects (atom mutation, hook installation, JS API,
   DOM mounting) live in recorder.cljs / x_trace_history.cljs."
  (:require [goog.object :as gobj]))

(def schema-version 1)

(def default-capacity 5000)

(def empty-records #queue [])

;; ---------------------------------------------------------------------------
;; Activation
;; ---------------------------------------------------------------------------

(def url-param "baredom-trace-history")

(def window-flag "BAREDOM_TRACE_HISTORY")

(defn enabled?
  "True iff the URL search of the supplied window contains
   ?baredom-trace-history OR window.BAREDOM_TRACE_HISTORY is truthy."
  [^js window]
  (or (some-> (.. window -location -search) (.includes url-param))
      (true? (gobj/get window window-flag))))

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
   converted; cyclic / non-coercible values fall back to (str v) — and if
   even (str v) throws (e.g. an object with a throwing toString), produce
   a placeholder string. The recorder must never throw."
  [v]
  (try
    (if (nil? v) nil (clj->js v))
    (catch :default _
      (try (str v) (catch :default _ "(unprintable)")))))

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

      ;; Lifecycle events that have no type-specific fields:
      (:lifecycle/connected :lifecycle/disconnected) r

      ;; Anything else is a typo or unhandled new type — warn loudly so
      ;; we don't silently lose the type-specific payload.
      (do
        (js/console.warn "[x-trace-history] unknown record type:" (str type))
        r))))

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

;; ---------------------------------------------------------------------------
;; Dock — categorisation and filtering
;; ---------------------------------------------------------------------------

(def tag-name "x-trace-history")

(def event-type-categories
  "Map of UI category keyword → set of record-type strings the dock groups
   under it. The dock filter uses categories rather than the 9 raw subtypes
   so the checkbox row stays scannable."
  {:events    #{"event/dispatch" "event/dispatch-cancelable" "event/dispatch-document"}
   :state     #{"state/instance-field-set"}
   :dom       #{"dom/attribute-set" "dom/attribute-removed"}
   :lifecycle #{"lifecycle/connected" "lifecycle/disconnected" "lifecycle/attribute-changed"}})

(def all-categories
  "All category keywords in stable display order."
  [:events :state :dom :lifecycle])

(defn categorize-type
  "Map a record-type string to its category keyword (:events, :state, :dom,
   :lifecycle), or :other if unrecognised."
  [type-str]
  (or (some (fn [[cat ts]] (when (contains? ts type-str) cat))
            event-type-categories)
      :other))

(defn record-matches?
  "True iff a single record passes the filter spec.

   spec keys:
     :tag        — string tag name; nil / blank / 'all' = match any
     :categories — set of category keywords to include; nil = include all"
  [^js r {:keys [tag categories]}]
  (and (or (nil? tag) (= "" tag) (= "all" tag) (= tag (.-tag r)))
       (or (nil? categories) (contains? categories (categorize-type (.-type r))))))

(defn filter-records
  "Return a CLJS vector of records (oldest-first) matching the filter spec.
   See `record-matches?` for spec keys."
  [^js records spec]
  (into []
        (filter (fn [r] (record-matches? r spec)))
        (array-seq records)))

;; ---------------------------------------------------------------------------
;; Dock — preview formatting
;; ---------------------------------------------------------------------------

(defn- truncate
  "Trim s to at most n characters, appending an ellipsis when cut."
  [s n]
  (let [s (str s)]
    (if (<= (count s) n)
      s
      (str (subs s 0 n) "…"))))

(defn- short-json
  "JSON.stringify the value (up to ~60 chars) with no extra whitespace.
   Returns nil for nil / undefined."
  [v]
  (when (some? v)
    (try
      (truncate (js/JSON.stringify v) 60)
      (catch :default _ "(unstringifiable)"))))

(defn payload-preview
  "Produce a short one-line preview string for the row UI given a record.
   Pure: depends only on record fields."
  [^js r]
  (case (.-type r)
    "event/dispatch"
    (str (.-eventName r) (when-let [d (short-json (.-detail r))] (str " " d)))

    "event/dispatch-cancelable"
    (str (.-eventName r)
         (when (.-defaultPrevented r) " (prevented)")
         (when-let [d (short-json (.-detail r))] (str " " d)))

    "event/dispatch-document"
    (str (.-eventName r) (when-let [d (short-json (.-detail r))] (str " " d)))

    "state/instance-field-set"
    (str (.-field r) " = " (or (short-json (.-value r)) "null"))

    "dom/attribute-set"
    (str (.-attribute r) "=" (pr-str (.-value r)))

    "dom/attribute-removed"
    (str (.-attribute r) " removed")

    "lifecycle/connected"      "connected"
    "lifecycle/disconnected"   "disconnected"
    "lifecycle/attribute-changed"
    (str (.-attribute r) ": " (pr-str (.-oldValue r)) " → " (pr-str (.-newValue r)))

    (str "(" (.-type r) ")")))

(defn format-timestamp
  "Format a performance.now() timestamp (ms since page load) as a compact
   relative string. <1s → 'Nms'; <1min → 'N.NNNs'; otherwise 'MmS.Ss'."
  [t]
  (cond
    (< t 1000)
    (str (.toFixed t 0) "ms")

    (< t 60000)
    (str (.toFixed (/ t 1000) 3) "s")

    :else
    (let [m (js/Math.floor (/ t 60000))
          s (/ (mod t 60000) 1000)]
      (str m "m" (.toFixed s 1) "s"))))

;; ---------------------------------------------------------------------------
;; Dock — instance-field keys (Closure-safe string keys for gobj/get|set)
;; ---------------------------------------------------------------------------

(def k-shadow      "__xTraceHistoryShadow")
(def k-list-el     "__xTraceHistoryListEl")
(def k-count-el    "__xTraceHistoryCountEl")
(def k-pause-btn   "__xTraceHistoryPauseBtn")
(def k-detail-el   "__xTraceHistoryDetailEl")
(def k-hint-el     "__xTraceHistoryHintEl")
(def k-filter      "__xTraceHistoryFilter")
(def k-selected-id "__xTraceHistorySelectedId")
(def k-sub-token   "__xTraceHistorySubToken")
(def k-mounted     "__xTraceHistoryMounted")

;; ---------------------------------------------------------------------------
;; Dock — CSS
;; ---------------------------------------------------------------------------

(def dock-css
  ":host {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  width: min(420px, calc(100vw - 1rem));
  z-index: 1999999;
  font: 11px/1.4 ui-monospace, 'SF Mono', 'Cascadia Code', 'Consolas', monospace;
  color: #cdd6f4;
  pointer-events: auto;
}
.dock {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #11111b;
  border-left: 1px solid rgba(59,130,246,0.6);
  box-shadow: -4px 0 20px rgba(0,0,0,0.4);
}
.header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  background: rgba(59,130,246,0.15);
  border-bottom: 1px solid rgba(59,130,246,0.3);
  flex: 0 0 auto;
}
.title { font-weight: 700; color: #89b4fa; font-size: 12px; }
.count { color: #6c7086; margin-left: auto; }
.btn {
  background: rgba(255,255,255,0.05);
  color: #cdd6f4;
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 4px;
  padding: 2px 8px;
  font: inherit;
  font-size: 10px;
  cursor: pointer;
}
.btn:hover  { background: rgba(255,255,255,0.1); }
.btn.paused { background: rgba(245,194,231,0.2); border-color: rgba(245,194,231,0.5); color: #f5c2e7; }
.filters {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px 12px;
  padding: 6px 10px;
  border-bottom: 1px solid rgba(255,255,255,0.06);
  flex: 0 0 auto;
}
.filters select {
  background: #313244;
  color: #cdd6f4;
  border: 1px solid #45475a;
  border-radius: 3px;
  padding: 1px 4px;
  font: inherit;
  font-size: 10px;
}
.filters label {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  color: #a6adc8;
  font-size: 10px;
  cursor: pointer;
}
.filters input[type=checkbox] { margin: 0; cursor: pointer; }
.list {
  flex: 1 1 auto;
  overflow-y: auto;
  min-height: 0;
}
.row {
  display: grid;
  grid-template-columns: 56px 80px 1fr;
  gap: 6px;
  align-items: baseline;
  padding: 3px 10px;
  border-bottom: 1px solid rgba(255,255,255,0.04);
  cursor: pointer;
}
.row:hover { background: rgba(59,130,246,0.08); }
.row.selected { background: rgba(59,130,246,0.18); }
.row .t   { color: #6c7086; text-align: right; }
.row .tag { color: #89b4fa; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.row .body {
  display: flex;
  flex-direction: column;
  gap: 1px;
  min-width: 0;
}
.row .type    { font-size: 9px; text-transform: uppercase; letter-spacing: 0.04em; }
.row .preview {
  color: #cdd6f4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.cat-events    .type { color: #94e2d5; }
.cat-state     .type { color: #f9e2af; }
.cat-dom       .type { color: #fab387; }
.cat-lifecycle .type { color: #cba6f7; }
.cat-other     .type { color: #6c7086; }
.detail {
  background: #181825;
  border-top: 1px solid rgba(59,130,246,0.4);
  padding: 8px 10px;
  font-size: 10px;
  white-space: pre-wrap;
  word-break: break-all;
  color: #a6e3a1;
  max-height: 40%;
  overflow-y: auto;
}
.empty {
  color: #6c7086;
  font-style: italic;
  text-align: center;
  padding: 20px;
}
.hint { color: #6c7086; font-size: 10px; padding: 4px 10px; flex: 0 0 auto; }")
