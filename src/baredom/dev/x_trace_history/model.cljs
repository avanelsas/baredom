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

   Common payload keys: :type :tag :component-id :cause-id.
   :cause-id is the id of the synchronously-enclosing dispatchEvent call,
   or nil for records produced outside any dispatch (top-level, or async).
   Type-specific keys are documented in docs/x-trace-history-schema.md.

   Uses string-keyed js-obj so Closure Advanced does not rename the schema
   field names — consumers read these properties from the wire."
  [{:keys [type tag component-id cause-id] :as payload} id t]
  (let [^js r (js-obj
               "schemaVersion" schema-version
               "id"            id
               "t"             t
               "type"          (format-type type)
               "tag"           tag
               "componentId"   component-id
               "causeId"       cause-id)]
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
  "Return a CLJS vector of records matching the filter spec, sorted by `t`
   ascending (chronological order). Sorting is necessary because PR 7
   pushes a dispatch's record AFTER the records produced inside its
   handlers — the ring buffer's insertion order no longer matches time
   order. See `record-matches?` for spec keys."
  [^js records spec]
  (->> (array-seq records)
       (filter (fn [r] (record-matches? r spec)))
       (sort-by (fn [^js r] (.-t r)))
       vec))

(defn find-record-by-id
  "Return the record with id `id`, or nil if not found / id non-numeric.
   Delegates to Array.prototype.find for short-circuiting linear search."
  [^js records id]
  (when (number? id)
    (.find records (fn [^js r] (= id (.-id r))))))

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
   relative string. <1s → 'Nms'; <1min → 'N.NNNs'; otherwise 'MmS.Ss'.
   Non-numeric input (nil / string / undefined) → empty string. The guard
   matters because PR 11 will accept imported records whose `t` field is
   external data; without it `.toFixed` throws on nil."
  [t]
  (cond
    (not (number? t)) ""
    (< t 1000)        (str (.toFixed t 0) "ms")
    (< t 60000)       (str (.toFixed (/ t 1000) 3) "s")
    :else
    (let [m (js/Math.floor (/ t 60000))
          s (/ (mod t 60000) 1000)]
      (str m "m" (.toFixed s 1) "s"))))

;; ---------------------------------------------------------------------------
;; Timeline — colours, lanes, time-axis math
;; ---------------------------------------------------------------------------

(def category-colors
  "Catppuccin-ish palette mapped from category keyword to hex. Matches the
   .cat-* classes used in the row body of earlier PRs so the visual
   language is consistent across UI views."
  {:events    "#94e2d5"
   :state     "#f9e2af"
   :dom       "#fab387"
   :lifecycle "#cba6f7"
   :other     "#6c7086"})

(defn dot-color
  "Fill colour for a record's timeline dot, derived from its category."
  [^js r]
  (get category-colors (categorize-type (.-type r)) "#6c7086"))

(def document-lane
  "Sentinel lane-id for records whose target was js/document (componentId
   is nil on the wire). Distinct from any numeric componentId."
  ::document)

(defn lane-id-of
  "Return the lane-id a record belongs in: its componentId, or
   `document-lane` for document-target events."
  [^js r]
  (if-let [cid (.-componentId r)]
    cid
    document-lane))

(defn lane-label
  "Human-readable lane label: 'tag #cid' or 'document'."
  [{:keys [lane-id tag]}]
  (if (= document-lane lane-id)
    "document"
    (str tag " #" lane-id)))

(defn active-lanes
  "Build the ordered lane list from a sequence of (already-filtered)
   records. Each lane is {:lane-id :tag :min-t}; lanes are ordered by
   first-appearance time so older lanes stay at the top as new ones
   emerge below them."
  [filtered-records]
  (->> filtered-records
       (group-by lane-id-of)
       (map (fn [[lane-id rs]]
              (let [^js r0 (first rs)]
                {:lane-id lane-id
                 :tag     (.-tag r0)
                 :min-t   (apply min (map (fn [^js r] (.-t r)) rs))})))
       (sort-by :min-t)
       vec))

(defn time-bounds
  "Return {:tmin :tmax :span} from the records' t fields, or nil for an
   empty input. `:span` is clamped to a 1ms minimum so single-record sets
   still produce a usable scale."
  [records]
  (when (seq records)
    (let [ts (map (fn [^js r] (.-t r)) records)
          mn (apply min ts)
          mx (apply max ts)]
      {:tmin mn :tmax mx :span (max 1 (- mx mn))})))

(defn time-x
  "Map a time value to an x-coordinate inside a plot of width `plot-width`,
   given the bounds map returned by `time-bounds`. Returns 0 for nil
   bounds (no records) and clamps t outside [tmin, tmax] into the plot."
  [t {:keys [tmin span]} plot-width]
  (if (or (nil? tmin) (nil? span))
    0
    (let [norm (-> (- t tmin) (/ span) (max 0) (min 1))]
      (* norm plot-width))))

(defn time-from-x
  "Inverse of `time-x`: map an x-coordinate within a plot of width
   `plot-width` back to a time value. Returns 0 for nil bounds and
   clamps x outside [0, plot-width] into [tmin, tmax]."
  [x {:keys [tmin span] :as bounds} plot-width]
  (cond
    (or (nil? bounds) (nil? tmin) (nil? span))
    0

    (or (zero? plot-width) (neg? plot-width))
    tmin

    :else
    (let [norm (-> (/ x plot-width) (max 0) (min 1))]
      (+ tmin (* norm span)))))

(defn nearest-record
  "Return the record from `records` whose t is closest to `target-t`. nil
   for empty input. Used by the scrubber to translate a cursor x back
   into a record selection."
  [records target-t]
  (when (seq records)
    (apply min-key
           (fn [^js r] (js/Math.abs (- (.-t r) target-t)))
           records)))

(defn step-record
  "Return the next or previous filtered record relative to `current-id`.
   `dir` is :next or :prev. Returns the first/last record when current-id
   is nil or when current-id is no longer in the filtered list.

   Stepping is by position in `filtered-records` — id-ordering is no
   longer reliable since PR 7 dispatch records receive lower ids than
   the children they cause (children push first, then the dispatch).
   Callers pass the t-sorted vector that filter-records produces."
  [filtered-records current-id dir]
  (when (seq filtered-records)
    (let [target-idx (when (some? current-id)
                       (loop [i 0]
                         (cond
                           (>= i (count filtered-records)) nil
                           (= current-id (.-id ^js (nth filtered-records i))) i
                           :else (recur (inc i)))))]
      (cond
        ;; No current selection (or current selection no longer in filter):
        ;; :next picks the first record, :prev picks the last.
        (nil? target-idx)
        (case dir
          :next (first filtered-records)
          :prev (peek  filtered-records))

        (= dir :next)
        (get filtered-records (inc target-idx))

        :else
        (get filtered-records (dec target-idx))))))

;; ---------------------------------------------------------------------------
;; Causality — cause-of / effects-of
;; ---------------------------------------------------------------------------

(def effects-display-limit
  "Cap on how many effect records the detail pane lists. Prevents the
   pane from blowing up when a single dispatch causes hundreds of
   downstream records (e.g. a render fan-out)."
  50)

(defn cause-of
  "Return the cause record (record with id = `record`'s causeId) from
   `records`, or nil if the record has no cause or the cause has been
   evicted from the ring buffer."
  [^js records ^js record]
  (let [cid (.-causeId record)]
    (when (number? cid)
      (find-record-by-id records cid))))

(defn effects-of
  "Return {:total N :records [recs ...]} for `record`, where :records is
   the list of records whose causeId equals `record`'s id, sorted by `t`
   ascending and capped at `effects-display-limit`. :total is the
   un-capped count, so the UI can show 'N of TOTAL' on truncation."
  [^js records ^js record]
  (let [target-id (.-id record)
        all       (->> (array-seq records)
                       (filter (fn [^js r] (= target-id (.-causeId r))))
                       (sort-by (fn [^js r] (.-t r))))]
    {:total   (count all)
     :records (vec (take effects-display-limit all))}))

(defn tooltip-text
  "Multi-line text shown when hovering a timeline dot."
  [^js r]
  (str (.-tag r)
       (when-let [cid (.-componentId r)] (str " #" cid))
       "\n" (.-type r)
       "\n" (payload-preview r)
       "\n@ " (format-timestamp (.-t r))))

(defn timeline-hint
  "One-line description of the current plot extent for the dock hint area.
   `cnt-filtered` is records visible after filter; `cnt-total` is full
   buffer size."
  [cnt-filtered cnt-total bounds lane-count]
  (cond
    (zero? cnt-total)
    "No records yet — interact with components to start tracing."

    (zero? cnt-filtered)
    (str cnt-total " " (if (= 1 cnt-total) "record" "records")
         " · all hidden by filter")

    :else
    (str cnt-filtered
         (when (not= cnt-filtered cnt-total) (str " of " cnt-total))
         " " (if (= 1 cnt-filtered) "record" "records")
         " · " lane-count " " (if (= 1 lane-count) "lane" "lanes")
         (when-let [span (:span bounds)]
           (str " · spanning " (format-timestamp span))))))

;; ---------------------------------------------------------------------------
;; Dock — instance-field keys (Closure-safe string keys for gobj/get|set)
;; ---------------------------------------------------------------------------

(def k-shadow        "__xTraceHistoryShadow")
(def k-keydown-fn    "__xTraceHistoryKeydownFn")
(def k-timeline-el   "__xTraceHistoryTimelineEl")
(def k-lanes-el      "__xTraceHistoryLanesEl")
(def k-svg-pane-el   "__xTraceHistorySvgPaneEl")
(def k-tooltip-el    "__xTraceHistoryTooltipEl")
(def k-count-el      "__xTraceHistoryCountEl")
(def k-pause-btn     "__xTraceHistoryPauseBtn")
(def k-detail-el     "__xTraceHistoryDetailEl")
(def k-splitter-el   "__xTraceHistorySplitterEl")
(def k-hint-el       "__xTraceHistoryHintEl")
(def k-tag-select-el "__xTraceHistoryTagSelectEl")
(def k-filter        "__xTraceHistoryFilter")
(def k-selected-id   "__xTraceHistorySelectedId")
(def k-sub-token     "__xTraceHistorySubToken")
(def k-mounted       "__xTraceHistoryMounted")

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
.timeline {
  flex: 1 1 auto;
  overflow: auto;
  min-height: 0;
  position: relative;
  /* Transparent border reserves 2px so the focus indicator below doesn't
     overlap the leftmost pixels of lane labels or shift content layout. */
  border: 2px solid transparent;
  box-sizing: border-box;
}
.timeline-body {
  display: flex;
  align-items: stretch;
  min-height: 100%;
}
.lanes {
  flex: 0 0 110px;
  display: flex;
  flex-direction: column;
  border-right: 1px solid rgba(255,255,255,0.06);
  background: #11111b;
  position: sticky;
  left: 0;
  z-index: 1;
}
.lane-label {
  height: 20px;
  padding: 0 8px;
  display: flex;
  align-items: center;
  font-size: 11px;
  color: #a6adc8;
  border-bottom: 1px solid rgba(255,255,255,0.04);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
  user-select: none;
}
.lane-label:hover { background: rgba(59,130,246,0.08); color: #cdd6f4; }
.lane-label.active {
  background: rgba(59,130,246,0.18);
  color: #89b4fa;
  font-weight: 600;
}
.lane-label .cid { color: #6c7086; margin-left: 4px; }
.svg-pane {
  flex: 1 1 auto;
  min-width: 0;
  display: block;
}
svg.timeline-svg {
  display: block;
  background:
    repeating-linear-gradient(
      to bottom,
      transparent 0,
      transparent 19px,
      rgba(255,255,255,0.03) 19px,
      rgba(255,255,255,0.03) 20px);
}
.dot { cursor: pointer; transition: r 80ms ease; }
.dot:hover { stroke: #fff; stroke-width: 1; }
line.scrubber {
  stroke: #f5c2e7;
  stroke-width: 1.5;
  stroke-dasharray: 4 2;
  pointer-events: none;
}
.svg-pane { cursor: crosshair; }
.timeline:focus {
  outline: none;
  border-color: #89b4fa;
}
.tooltip {
  position: absolute;
  background: #1e1e2e;
  border: 1px solid rgba(59,130,246,0.6);
  border-radius: 4px;
  padding: 4px 8px;
  font-size: 10px;
  white-space: pre-wrap;
  pointer-events: none;
  max-width: 280px;
  color: #cdd6f4;
  z-index: 10;
  box-shadow: 0 4px 12px rgba(0,0,0,0.5);
}
.timeline-empty {
  color: #6c7086;
  font-style: italic;
  text-align: center;
  padding: 20px;
  width: 100%;
}
.splitter {
  flex: 0 0 auto;
  height: 4px;
  background: rgba(255,255,255,0.04);
  cursor: ns-resize;
  touch-action: none;
  transition: background 120ms ease;
}
.splitter:hover, .splitter.dragging { background: rgba(59,130,246,0.6); }
.detail {
  background: #181825;
  border-top: 1px solid rgba(59,130,246,0.2);
  padding: 8px 10px;
  font-size: 10px;
  color: #cdd6f4;
  flex: 0 0 auto;
  height: 35vh;
  min-height: 60px;
  max-height: 80%;
  overflow-y: auto;
}
.detail-json {
  margin: 0 0 8px 0;
  font: inherit;
  white-space: pre-wrap;
  word-break: break-all;
  color: #a6e3a1;
}
.detail-section { margin-top: 6px; }
.detail-label {
  color: #89b4fa;
  font-weight: 600;
  margin-bottom: 2px;
  font-size: 10px;
  letter-spacing: 0.02em;
}
/* The host is an <x-button>; its own shadow brings background, hover,
   border-radius, etc. We keep block-layout + width on the host so each
   link fills its row, and reach into the button's `label` part to add
   ellipsis truncation (the summary string can exceed the dock width). */
.detail-link {
  display: block;
  width: 100%;
  margin: 2px 0;
}
.detail-link::part(label) {
  display: block;
  min-width: 0;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
}
.detail-link::part(button) {
  width: 100%;
  justify-content: flex-start;
}
.detail-empty { color: #6c7086; font-style: italic; font-size: 10px; }
.empty {
  color: #6c7086;
  font-style: italic;
  text-align: center;
  padding: 20px;
}
.hint { color: #6c7086; font-size: 10px; padding: 4px 10px; flex: 0 0 auto; }")
