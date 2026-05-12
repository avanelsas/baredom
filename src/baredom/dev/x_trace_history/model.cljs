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

(def forensic-value "raw")

(def ^:private url-param-regex
  "Matches `baredom-trace-history` as a complete URL query parameter
   name (preceded by `?` or `&`, terminated by `=`, `&`, or end-of-
   string). Anchored so `?baredom-trace-history-other` cannot
   accidentally activate."
  (re-pattern (str "(?:^\\?|&)" url-param "(?:=|&|$)")))

(def ^:private forensic-url-regex
  "Matches `baredom-trace-history=raw` as a complete URL query
   parameter (anchored same as url-param-regex). The forensic
   value can be followed by `&` or end-of-string but not by other
   characters, so `=rawish` does not activate forensic mode."
  (re-pattern (str "(?:^\\?|&)" url-param "=" forensic-value "(?:&|$)")))

(defn enabled?
  "True iff the URL search of the supplied window contains
   `?baredom-trace-history` as a complete query parameter OR
   window.BAREDOM_TRACE_HISTORY is `true` or the forensic-value
   string \"raw\". Anchored URL match prevents look-alike params
   from accidentally activating."
  [^js window]
  (or (boolean (some->> (.. window -location -search)
                        (re-find url-param-regex)))
      (true? (gobj/get window window-flag))
      (= forensic-value (gobj/get window window-flag))))

(defn forensic?
  "True iff trace-history is activated in forensic mode — `?baredom-
   trace-history=raw` in the URL or `window.BAREDOM_TRACE_HISTORY =
   \"raw\"`. Forensic mode disables the sample-rate cap and shows
   :state events in the dock by default. Returns false when
   trace-history is off or activated in normal mode."
  [^js window]
  (or (= forensic-value (gobj/get window window-flag))
      (boolean (some->> (.. window -location -search)
                        (re-find forensic-url-regex)))))

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

      ;; Anything else: the record carries its `type` string for the
      ;; consumer to inspect but no type-specific fields. The recorder
      ;; controls every call site, so an unknown type here is a bug in
      ;; the recorder, not user data.
      r)))

;; ---------------------------------------------------------------------------
;; Export — envelope construction
;;
;; Pure helpers for building the on-disk JSON shape produced by PR 10's
;; Export button. The recorder calls `make-export` with its current state
;; snapshot; this namespace is responsible only for translating that
;; snapshot into the schema-stable wire format documented in
;; docs/x-trace-history-schema.md. Effects (Blob construction, anchor
;; click, window API wiring) live in recorder.cljs / x_trace_history.cljs.
;; ---------------------------------------------------------------------------

(def filename-prefix "baredom-trace-")

(def filename-extension ".trace.json")

(defn- pad2
  "Left-pad a non-negative integer to two characters with '0'. Used by
   download-filename to build a sortable timestamp slug."
  [n]
  (let [s (str n)]
    (if (< (count s) 2)
      (str "0" s)
      s)))

(defn download-filename
  "Format a Date into the canonical export filename:
   `baredom-trace-YYYYMMDD-HHmmss.trace.json`. Using local time
   (matches the user's clock on disk) and zero-padded fields so
   filenames sort chronologically."
  [^js d]
  (str filename-prefix
       (.getFullYear d)
       (pad2 (inc (.getMonth d)))
       (pad2 (.getDate d))
       "-"
       (pad2 (.getHours d))
       (pad2 (.getMinutes d))
       (pad2 (.getSeconds d))
       filename-extension))

(defn- components->js
  "Convert the recorder's CLJS components index (id → {:tag :first-seen})
   into the schema's JS-object shape (stringified id → {tag, firstSeen})."
  [components]
  (let [^js out (js-obj)]
    (doseq [[id info] components]
      (gobj/set out (str id)
                (js-obj "tag"       (:tag info)
                        "firstSeen" (:first-seen info))))
    out))

(defn referenced-component-ids
  "Pure: return the set of componentIds appearing in the records JS
   array. Records whose componentId is nil (document-target events
   like dispatch-document) don't contribute — there's no component
   to keep in the index for them."
  [^js records]
  (let [n (.-length records)]
    (loop [i 0
           acc (transient #{})]
      (if (>= i n)
        (persistent! acc)
        (let [^js r (aget records i)
              cid   (.-componentId r)]
          (recur (inc i)
                 (if (some? cid) (conj! acc cid) acc)))))))

(defn filter-components
  "Pure: keep only entries from `components` whose id is in `referenced-ids`.
   The recorder's index is monotonic across the page lifetime and
   survives clear!, so a fresh capture still picks up stale entries
   from prior interactions. The export filter trims the index to
   what the captured records actually reference so an exported trace
   carries the minimum useful side-info."
  [components referenced-ids]
  (into {}
        (filter (fn [[id _]] (contains? referenced-ids id)))
        components))

(defn- sessions->js
  "Convert the recorder's CLJS sessions vector into a JS array of session
   objects in the wire shape. `recordCount` from the live `recorder/sessions`
   JS API is intentionally omitted — it's a derived value, not part of the
   on-disk contract. `endT` / `endId` survive as nil for sessions still
   active at export time."
  [sessions]
  (let [^js arr (array)]
    (doseq [{:keys [id label start-t end-t start-id end-id]} sessions]
      (.push arr (js-obj "id"      id
                         "label"   label
                         "startT"  start-t
                         "endT"    end-t
                         "startId" start-id
                         "endId"   end-id)))
    arr))

(defn make-export
  "Build the JS-object envelope that gets JSON.stringified into a
   `.trace.json` file. Pure: depends only on its arguments, no atom
   reads, no Date.now() calls.

   Inputs:
     records    — JS array of record objects (the recorder's
                  memoised view, oldest first).
     components — CLJS map componentId → {:tag :first-seen}.
     sessions   — CLJS vector of session maps with the recorder's
                  internal keys (:id :label :start-t :end-t :start-id
                  :end-id).
     ctx        — CLJS map of caller-supplied context:
                    :exported-at (number)  — Date.now() ms
                    :origin      (string)  — window.location.href
                    :user-agent  (string)  — navigator.userAgent
                    :forensic?   (boolean) — recorder/forensic-mode?

   Returns a JS object suitable for `JSON.stringify(envelope, null, 2)`.
   The envelope's `records` field is reference-equal to the passed-in
   `records` array — make-export does NOT copy. Callers passing the
   recorder's memoised array (`records-as-js-array`) must treat the
   resulting envelope as read-only; mutating envelope.records corrupts
   the recorder's internal cache.

   `components` is FILTERED to only those entries whose id appears in
   `records`. The recorder's live index is monotonic for the page
   lifetime and survives `clear!`; without the filter an exported
   trace carries every component ever seen, including stale entries
   from prior interactions the user explicitly cleared. The filter
   ensures `envelope.components` is the minimum side-info needed to
   resolve the captured records — no more, no less.

   The full schema lives in docs/x-trace-history-schema.md and the
   `schemaVersion` field gates importers."
  [^js records components sessions
   {:keys [exported-at origin user-agent forensic?]}]
  (let [referenced (referenced-component-ids records)
        trimmed    (filter-components components referenced)]
    (js-obj
     "schemaVersion" schema-version
     "exportedAt"    exported-at
     "origin"        origin
     "userAgent"     user-agent
     "forensic"      (boolean forensic?)
     "components"    (components->js trimmed)
     "sessions"      (sessions->js sessions)
     "records"       records)))

;; ---------------------------------------------------------------------------
;; Import — envelope validation
;;
;; Pure validation for `.trace.json` files dragged onto the dock or fed
;; through window.BareDOM.traceHistory.import. The recorder hard-rejects
;; mismatched envelopes — no partial loads, no silent coercion — so PR 11
;; consumers and the future standalone viewer (Phase 7) agree on what
;; "valid" means. See docs/x-trace-history-schema.md for the on-disk
;; contract this enforces.
;; ---------------------------------------------------------------------------

(defn- valid-record?
  "Pure: every imported record must at least carry the baseline fields
   the dock reads on every render (`id`, `t`, `type`). Type-specific
   extras are pass-through — we don't validate them here, because an
   importer should tolerate forward-compatible additions and the dock
   already has nil-safe formatting for missing fields."
  [^js r]
  (and (object? r)
       (number? (.-id r))
       (number? (.-t r))
       (string? (.-type r))))

(defn validate-envelope
  "Pure validator. Takes a parsed JS value and returns either
   `{:ok ^js envelope}` for a well-formed `.trace.json` payload or
   `{:error msg}` with a short user-readable reason on rejection.
   The recorder surfaces the error string back through the JS API
   and the dock hint area, so phrasing should read cleanly in both.

   Validation rules — kept deliberately narrow so future schema
   additions stay backward-compatible:
   - envelope must be a JS object
   - envelope.schemaVersion must equal the current schema-version
   - envelope.records must be an array of objects, each with the
     baseline fields (id, t, type) populated with the right types"
  [^js env]
  (cond
    (not (object? env))
    {:error "Envelope is not a JSON object."}

    (not (number? (.-schemaVersion env)))
    {:error "Envelope is missing schemaVersion."}

    (not= schema-version (.-schemaVersion env))
    {:error (str "Schema version mismatch: expected "
                 schema-version ", got " (.-schemaVersion env) ".")}

    (not (array? (.-records env)))
    {:error "Envelope.records is not an array."}

    :else
    (let [^js recs (.-records env)
          n        (.-length recs)
          bad-i    (loop [i 0]
                     (cond
                       (>= i n)                                    nil
                       (not (valid-record? (aget recs i)))         i
                       :else                                       (recur (inc i))))]
      (if (nil? bad-i)
        {:ok env}
        {:error (str "Record at index " bad-i
                     " is missing required fields (id, t, type).")}))))

(defn parse-export
  "Parse a JSON string into a validated envelope. Returns
   `{:ok ^js envelope}` or `{:error msg}` — never throws. Wraps
   `validate-envelope` so callers can hand it raw user input from
   a drag-drop / file-picker / paste path without try/catch.

   Empty string / whitespace-only / non-JSON input all produce
   structured errors. The recorder's import path uses this so the
   dock can surface 'why' to the user."
  [^js json-str]
  (let [parsed (try
                 (js/JSON.parse json-str)
                 (catch :default _ ::parse-error))]
    (if (= parsed ::parse-error)
      {:error "File is not valid JSON."}
      (validate-envelope parsed))))

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

(defn default-categories
  "Initial filter category set the dock applies on mount. In normal
   mode :state is excluded — instance-field writes are the noisiest
   record type (every component caches models, refs, etc.) and
   defaulting them off keeps the timeline focused on user-relevant
   events. Forensic mode includes everything so users can capture
   raw mutation history."
  [forensic?]
  (if forensic?
    (set all-categories)
    (disj (set all-categories) :state)))

(defn categorize-type
  "Map a record-type string to its category keyword (:events, :state, :dom,
   :lifecycle), or :other if unrecognised."
  [type-str]
  (or (some (fn [[cat ts]] (when (contains? ts type-str) cat))
            event-type-categories)
      :other))

(defn searchable-haystack
  "Pure: lowercase JSON-serialised view of `r`, suitable for substring
   matching. Returns \"\" if JSON.stringify throws (e.g. cyclic detail
   payload that slipped past the recorder's coercion).

   This namespace caches nothing — recomputing on every search would
   be wasteful for big buffers, so the dock memoises via WeakMap and
   threads the memoised fn into `record-matches?` as `:haystack-fn`."
  [^js r]
  (let [s (try (js/JSON.stringify r) (catch :default _ ""))]
    (if s (.toLowerCase s) "")))

(defn- blank?
  "Treat nil and the empty string as 'no filter'. Centralises the
   no-filter convention shared by :tag and :search."
  [s]
  (or (nil? s) (= "" s)))

(defn- tag-matches?
  "True iff `tag` is inactive (blank or 'all') or equals r's tag."
  [^js r tag]
  (or (blank? tag) (= "all" tag) (= tag (.-tag r))))

(defn- categories-match?
  "True iff `categories` is nil (no filter) or contains r's category."
  [^js r categories]
  (or (nil? categories)
      (contains? categories (categorize-type (.-type r)))))

(defn- search-matches?
  "True iff `search` is blank (no filter) or `(haystack-fn r)` contains
   the (already lowercase) search query as a substring."
  [^js r search haystack-fn]
  (or (blank? search)
      (not (neg? (.indexOf (haystack-fn r) search)))))

(defn record-matches?
  "True iff `r` passes the filter spec.

   spec keys:
     :tag         — string tag name; nil / blank / 'all' = match any
     :categories  — set of category keywords to include; nil = include all
     :search      — lowercase substring required in the record's
                    JSON-serialised form; nil / blank = include all
     :haystack-fn — fn from ^js record → lowercase JSON string. Defaults
                    to `searchable-haystack`. The dock passes a
                    WeakMap-memoised version so search across the whole
                    ring buffer pays JSON.stringify once per record."
  [^js r {:keys [tag categories search haystack-fn]
          :or   {haystack-fn searchable-haystack}}]
  (and (tag-matches?      r tag)
       (categories-match? r categories)
       (search-matches?   r search haystack-fn)))

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
  (get category-colors (or (categorize-type (.-type r)) :other)))

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

(def plot-padding-x
  "Horizontal padding (in CSS pixels) reserved on each side of the SVG
   plot so dots at the temporal extremes (tmin / tmax) stay fully
   inside the viewport instead of being half-clipped by the edges.
   Sized to cover the largest dot radius the dock renders (`dot-r-sel`)
   plus a small visual margin. Also bumps up the effective hit-target
   for edge events, which a 0-padding linear mapping would render
   inaccessible at the far left."
  8)

(def axis-modes
  "Two axis layouts the dock supports. :order is the default — uniform
   horizontal spacing by record index — and matches the use case
   (sequence + causality navigation) the tool is built around. :time
   keeps the linear-time mapping for users debugging timing or
   animation density."
  #{:order :time})

(def default-axis-mode :order)

(defn axis-mode?
  "Predicate: is `m` one of the supported axis modes?"
  [m]
  (contains? axis-modes m))

(defn time-x
  "Map a time value to an x-coordinate inside a plot of width `plot-width`,
   given the bounds map returned by `time-bounds`. Returns the padded
   left edge (`plot-padding-x`) for nil bounds (no records) and clamps
   t outside [tmin, tmax] into the padded plot range
   [plot-padding-x, plot-width - plot-padding-x]. Padding prevents the
   leftmost / rightmost dots from being clipped at the SVG edges; when
   plot-width is too narrow to honour both paddings (degenerate case),
   the plot collapses to a single x value at the centre."
  [t {:keys [tmin span]} plot-width]
  (let [inner (max 0 (- plot-width (* 2 plot-padding-x)))]
    (if (or (nil? tmin) (nil? span))
      plot-padding-x
      (let [norm (-> (- t tmin) (/ span) (max 0) (min 1))]
        (+ plot-padding-x (* norm inner))))))

(defn time-from-x
  "Inverse of `time-x`: map an x-coordinate within a plot of width
   `plot-width` back to a time value. Returns 0 for nil bounds and
   clamps x outside the padded plot range
   [plot-padding-x, plot-width - plot-padding-x] into [tmin, tmax]."
  [x {:keys [tmin span] :as bounds} plot-width]
  (let [inner (max 0 (- plot-width (* 2 plot-padding-x)))]
    (cond
      (or (nil? bounds) (nil? tmin) (nil? span))
      0

      (or (zero? plot-width) (neg? plot-width) (zero? inner))
      tmin

      :else
      (let [norm (-> (/ (- x plot-padding-x) inner) (max 0) (min 1))]
        (+ tmin (* norm span))))))

(defn nearest-record
  "Return the record from `records` whose t is closest to `target-t`. nil
   for empty input. Used by the scrubber to translate a cursor x back
   into a record selection."
  [records target-t]
  (when (seq records)
    (apply min-key
           (fn [^js r] (js/Math.abs (- (.-t r) target-t)))
           records)))

;; ---------------------------------------------------------------------------
;; Order axis — position by index in the filtered list
;;
;; The order axis is the dock's default. For state-time-travel debugging
;; the question is almost always "what fired, in what order, and what
;; caused what" — sequence + causality. Time-axis density is useful for
;; profiling, but Chrome DevTools' Performance panel already serves
;; that case better. Order-axis renders every event with a uniform
;; horizontal slot so sparse traces don't look mostly empty and edge
;; events are easy to click.
;;
;; The `:time` mode (above) stays available behind a toggle for users
;; debugging timing / animation density.
;; ---------------------------------------------------------------------------

(defn index-x
  "Position the i-th record (0-based) of `n` total records inside the
   padded plot. Uniform spacing — i=0 lands at the padded left edge,
   i=n-1 lands at the padded right edge. A single-record plot
   (n=1) places its dot at the centre of the inner plot rather than
   collapsing to one edge, so it's discoverable. Padding matches
   `plot-padding-x`, so dot radii survive at the extremes without
   clipping just like the time-axis path."
  [i n plot-width]
  (let [inner (max 0 (- plot-width (* 2 plot-padding-x)))]
    (cond
      (or (zero? n) (neg? n))
      plot-padding-x

      (= 1 n)
      (+ plot-padding-x (/ inner 2))

      :else
      (let [norm (-> (/ i (dec n)) (max 0) (min 1))]
        (+ plot-padding-x (* norm inner))))))

(defn index-from-x
  "Inverse of `index-x`. Return the record index (0-based) closest to
   cursor x for an n-record list. Out-of-range x clamps to [0, n-1].
   Returns nil for n=0 so callers can treat 'no record under cursor'
   distinctly from 'first record'."
  [x n plot-width]
  (let [inner (max 0 (- plot-width (* 2 plot-padding-x)))]
    (cond
      (or (zero? n) (neg? n))
      nil

      (= 1 n)
      0

      (zero? inner)
      0

      :else
      (let [norm (-> (/ (- x plot-padding-x) inner) (max 0) (min 1))]
        (js/Math.round (* norm (dec n)))))))

(defn index-of-record
  "Linear-scan `records` (a CLJS vector or seq, in the dock's filtered
   t-sorted order) and return the index of the record whose id matches
   `target-id`. nil if not found / non-numeric id. Used by the order-
   axis renderer to translate a record into its x-coordinate, and by
   keyboard / scrubber paths (step-record) to find adjacent records."
  [records target-id]
  (when (number? target-id)
    (let [n (count records)]
      (loop [i 0]
        (cond
          (>= i n)                                           nil
          (= target-id (.-id ^js (nth records i)))           i
          :else                                              (recur (inc i)))))))

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
    (let [idx (when (some? current-id)
                (index-of-record filtered-records current-id))]
      (cond
        ;; No current selection (or current selection no longer in filter):
        ;; :next picks the first record, :prev picks the last.
        (nil? idx)
        (case dir
          :next (first filtered-records)
          :prev (peek  filtered-records))

        (= dir :next) (get filtered-records (inc idx))
        :else         (get filtered-records (dec idx))))))

;; ---------------------------------------------------------------------------
;; Density binning — heatmap rendering for animation-heavy lanes (PR 18)
;;
;; When a lane carries enough records to overlap visually (e.g. a 60fps
;; drag firing CustomEvents), drawing individual dots becomes unreadable.
;; Instead we bin the lane's records by pixel column and render a
;; coloured band: one <rect> per bin, opacity scaled to the bin's count.
;;
;; The trigger is pixel-overlap, not events/sec — a 50/sec rate may or
;; may not overlap depending on zoom level. Each render bins from
;; scratch off the current plot width, so the visualization adapts to
;; resizes for free.
;;
;; :order axis mode does NOT use density rendering: records are
;; uniformly spaced by index there, so dots never overlap.
;; ---------------------------------------------------------------------------

(def density-bin-width
  "Width of one heatmap bin in CSS pixels. Records are bucketed by
   `floor(x / bin-width)`. 4 px is wide enough to be a visible band
   at typical dock zoom and narrow enough to preserve temporal
   resolution within a lane."
  4)

(def density-threshold
  "If any bin in a lane holds MORE than this many records, the lane
   renders as a heatmap band instead of individual dots. 3 records
   per 4 px (i.e. > one record per pixel) is the point at which dot
   overlap becomes visually confusing."
  3)

(defn- bin-index-of-x
  "Floor an x-coordinate to its bin index. Pure."
  [x]
  (js/Math.floor (/ x density-bin-width)))

(defn dominant-category
  "Return the category keyword that appears most often in a sequence
   of records. Ties between two `all-categories` entries are broken
   by `all-categories` order so a bin with equal counts always
   renders in the same colour across runs (e.g. an equal mix of
   events + dom always picks :events).

   Records whose `type` is unrecognised contribute to the `:other`
   bucket. `:other` only wins when it STRICTLY beats every recognised
   category — a tie with any `all-categories` entry resolves to that
   entry, since recognised categories carry more semantic meaning
   for visualization than the catch-all.

   Returns nil for an empty seq."
  [records]
  (when (seq records)
    (let [counts    (frequencies (map (fn [^js r] (categorize-type (.-type r)))
                                      records))
          max-count (apply max (vals counts))]
      (or (some (fn [cat] (when (= max-count (get counts cat)) cat))
                all-categories)
          :other))))

(defn bin-records-by-x
  "Bucket a lane's records into pixel-aligned bins for heatmap
   rendering. Each record contributes to exactly one bin via
   `record-cx` under the active axis mode.

   `record-cx-fn` is a (^js r) → x function the caller threads from
   the renderer (it already knows the index map, bounds, and plot
   width). Keeps this fn pure — model.cljs has no DOM knowledge.

   Returns a vector of bin maps sorted by `:bin-i`, each:
     {:bin-i        N
      :records      [r ...]          (sorted by id ascending)
      :dominant-cat :kw
      :x-start      px
      :x-end        px
      :t-min        ms
      :t-max        ms}"
  [lane-records record-cx-fn]
  (let [grouped (group-by (fn [^js r] (bin-index-of-x (record-cx-fn r)))
                          lane-records)]
    (vec
     (sort-by
      :bin-i
      (map (fn [[bin-i rs]]
             (let [ts          (map (fn [^js r] (.-t r)) rs)
                   sorted-recs (vec (sort-by (fn [^js r] (.-id r)) rs))]
               {:bin-i        bin-i
                :records      sorted-recs
                :dominant-cat (dominant-category sorted-recs)
                :x-start      (* bin-i density-bin-width)
                :x-end        (* (inc bin-i) density-bin-width)
                :t-min        (apply min ts)
                :t-max        (apply max ts)}))
           grouped)))))

(defn lane-is-dense?
  "True iff any bin in `bins` exceeds `density-threshold`. Empty input
   is sparse by definition."
  [bins]
  (some (fn [b] (> (count (:records b)) density-threshold)) bins))

(def ^:private bin-opacity-floor
  "Lowest opacity a one-record bin renders at. 0.4 is the minimum
   that keeps the band visible at WCAG AA 3:1 non-text contrast on
   the dock's `#11111b` background for every category colour we
   ship; lower values (we shipped 0.3 in a draft) read as nearly
   invisible for cool colours like the events green."
  0.4)

(defn bin-opacity
  "Linear opacity from `bin-opacity-floor` (a single-record bin) to
   1.0 (a bin at `lane-max-count`). One-record bins land at the low
   end — but not below the contrast floor — so the heatmap still
   reads as a continuous band including the sparse bins inside an
   otherwise-dense lane. Bins above the lane's own max-count clamp
   to 1.0 (defensive — shouldn't happen)."
  [count lane-max-count]
  (cond
    (or (nil? count) (zero? count) (nil? lane-max-count) (zero? lane-max-count))
    0.0

    (>= count lane-max-count)
    1.0

    :else
    (let [span (- 1.0 bin-opacity-floor)
          norm (/ (- count 1) (max 1 (- lane-max-count 1)))]
      (+ bin-opacity-floor (* span norm)))))

(defn lane-max-bin-count
  "Largest record count across any bin in the lane. Used to scale
   bin opacities relative to the lane's local peak so sparse-but-
   dense lanes still read as full-strength."
  [bins]
  (if (seq bins)
    (apply max (map (fn [b] (count (:records b))) bins))
    0))

(defn density-bin-tooltip-text
  "Multi-line text for the heatmap-bin hover tooltip. Mirrors
   `tooltip-text`'s shape so users get the same information density.
   Takes the bin's first record, its count, and its dominant
   category as separate args (rather than a full bin map) — the UI
   layer reads count + category from the rect's data attributes and
   resolves the first-record from the recorder, which avoids
   re-binning at hover time AND keeps the SVG markup lean."
  [^js first-record bin-count dominant-cat]
  (let [^js r0 first-record]
    (str (.-tag r0)
         (when-let [cid (.-componentId r0)] (str " #" cid))
         "\n" bin-count " " (if (= 1 bin-count) "record" "records")
         " · " (name (or dominant-cat :other))
         "\n@ " (format-timestamp (.-t r0)))))

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

;; ---------------------------------------------------------------------------
;; Causality DAG view — tree building + layout
;;
;; Each record carries at most one causeId, so the causality structure
;; across all records is a forest of trees, not a general DAG. The view
;; renders the tree containing the currently-selected record: walk up to
;; the root (highest ancestor still in the records array), then a
;; depth-first build of descendants via the private `children-of`
;; helper (which scans the records array for matching causeId).
;;
;; Layout is a simple post-order tidy:
;;   - leaves are placed left-to-right
;;   - each parent sits at the horizontal midpoint of its children's centres
;;   - depth maps to y
;;
;; Coordinates are CENTRES (cx/cy), so SVG nodes and connecting edges
;; share one coordinate system. The renderer translates by
;; `causality-padding` on both axes before drawing.
;; ---------------------------------------------------------------------------

(def causality-node-w  140)
(def causality-node-h   28)
(def causality-h-gap    18)
(def causality-v-gap    28)
(def causality-padding  16)

(def causality-max-nodes
  "Hard cap on tree size the renderer will draw. Trees over this size
   show a notice instead, asking the user to narrow the filter or pick
   a leaf. 200 keeps the SVG dense-but-legible at the dock's typical
   420 px width and is well clear of the 5000-record ring buffer cap."
  200)

(def causality-walk-cap
  "Upper bound on how deep `causality-root` will walk up the cause
   chain. Records have a single causeId so this should never matter,
   but the defensive cap means a corrupted import with a circular
   causeId can never hang the renderer."
  5000)

(defn causality-root
  "Walk the causeId chain up from `record` and return the highest
   ancestor still present in `records`. When a record's causeId points
   to something evicted from the ring buffer (or never present, in an
   imported partial trace), the walk stops there — that's the root we
   render. Returns `record` itself when it has no cause."
  [^js records ^js record]
  (loop [^js r record
         steps 0]
    (let [cid (.-causeId r)]
      (cond
        (not (number? cid))           r
        (>= steps causality-walk-cap) r
        :else
        (if-let [^js parent (find-record-by-id records cid)]
          (recur parent (inc steps))
          r)))))

(defn- children-of
  "All records whose causeId equals `target-id`, sorted by id ascending
   for deterministic layout. PR 7 pushes the dispatch's record AFTER its
   effects, but ids are assigned in arrival order — sorting by id puts
   the earliest-occurring effect leftmost in the tree."
  [^js records target-id]
  (->> (array-seq records)
       (filter (fn [^js r] (= target-id (.-causeId r))))
       (sort-by (fn [^js r] (.-id r)))
       vec))

(defn- causality-dfs-seq
  "Lazy depth-first seq of `root` and its descendants in the cause-id
   forest. Each parent precedes its subtree; siblings appear in id
   order (matching `children-of`)."
  [^js records ^js root]
  (cons root
        (lazy-seq
         (mapcat #(causality-dfs-seq records %)
                 (children-of records (.-id root))))))

(defn causality-tree
  "Nested causality tree rooted at `root-record`:
     {:record :children :capped?}.
   Children come from records whose causeId matches a parent's id.
   The build is capped at `causality-max-nodes` in DFS order; when
   truncated, the root carries :capped? true and the renderer shows
   an over-cap notice. Pure: depends only on its arguments."
  [^js records ^js root-record]
  (let [[kept overflow] (split-at causality-max-nodes
                                  (causality-dfs-seq records root-record))
        kids-of         (group-by #(.-causeId ^js %) kept)
        build           (fn build [^js r]
                          {:record   r
                           :children (mapv build (kids-of (.-id r)))})]
    (-> (build root-record)
        (assoc :capped? (boolean (seq overflow))))))

(defn tree-node-count
  "Total number of records in a built causality tree."
  [tree]
  (inc (reduce + 0 (map tree-node-count (:children tree)))))

(defn tree-max-depth
  "Maximum depth (0-based) of a built causality tree."
  [tree]
  (if (empty? (:children tree))
    0
    (inc (apply max (map tree-max-depth (:children tree))))))

(defn tree-size-stats
  "Return {:node-count N :max-depth D} for a built causality tree.
   Used by the renderer to decide whether to draw or show a notice."
  [tree]
  {:node-count (tree-node-count tree)
   :max-depth  (tree-max-depth  tree)})

(defn- layout-subtree
  "Post-order layout. Returns [laid-tree next-left-x] where:
     - laid-tree mirrors the input but every node carries
       {:id :record :cx :cy :depth :children}
     - cx/cy are pixel centres. Both axes start at `causality-padding`
       so dots/labels don't graze the top-left edge of the SVG, and the
       width/height returned by `layout-tree` reserves the same
       padding on the trailing edge.
     - next-left-x is the leftmost x at which the next leaf could start
   For internal nodes, cx is the midpoint of first / last child centre."
  [tree depth next-left-x]
  (let [^js r       (:record tree)
        children    (:children tree)
        slot-step   (+ causality-node-w causality-h-gap)
        row-step    (+ causality-node-h causality-v-gap)
        cy          (+ causality-padding
                       (/ causality-node-h 2)
                       (* depth row-step))]
    (if (empty? children)
      (let [cx (+ next-left-x (/ causality-node-w 2))]
        [{:id (.-id r) :record r :cx cx :cy cy :depth depth :children []}
         (+ next-left-x slot-step)])
      (let [[laid-children x-after]
            (reduce (fn [[acc nx] child]
                      (let [[laid-child nx'] (layout-subtree child (inc depth) nx)]
                        [(conj acc laid-child) nx']))
                    [[] next-left-x]
                    children)
            first-cx (:cx (first laid-children))
            last-cx  (:cx (last  laid-children))
            cx       (/ (+ first-cx last-cx) 2)]
        [{:id (.-id r) :record r :cx cx :cy cy :depth depth :children laid-children}
         x-after]))))

(defn- collect-nodes
  "Walk a laid-out tree and return a flat vector of its nodes (each
   carrying :id :record :cx :cy :depth, without :children). Pure helper
   for the SVG renderer, which doesn't care about the tree shape once
   coords are assigned."
  [laid]
  (into [(dissoc laid :children)]
        (mapcat collect-nodes (:children laid))))

(defn- collect-edges
  "Walk a laid-out tree and return a flat vector of parent→child edges
   as {:from-id :from-cx :from-cy :to-id :to-cx :to-cy}. Drawn before
   nodes so the connectors sit underneath the boxes in z-order."
  [laid]
  (let [pid (:id laid)
        pcx (:cx laid)
        pcy (:cy laid)]
    (vec
     (mapcat
      (fn [child]
        (cons {:from-id pid :from-cx pcx :from-cy pcy
               :to-id   (:id child) :to-cx (:cx child) :to-cy (:cy child)}
              (collect-edges child)))
      (:children laid)))))

(defn layout-tree
  "Pure layout: take a built causality tree and produce
     {:nodes [...] :edges [...] :width W :height H}
   in a coordinate system where the leftmost leaf's centre sits at
   x = causality-padding + node-w/2 and the root sits at y =
   causality-padding + node-h/2. Width / height span the full tree
   bounding box including padding on both ends."
  [tree]
  (let [[laid _] (layout-subtree tree 0 causality-padding)
        nodes    (collect-nodes laid)
        edges    (collect-edges laid)
        max-cx   (reduce max 0 (map :cx nodes))
        max-cy   (reduce max 0 (map :cy nodes))]
    {:nodes  nodes
     :edges  edges
     :width  (+ max-cx (/ causality-node-w 2) causality-padding)
     :height (+ max-cy (/ causality-node-h 2) causality-padding)}))

(defn fit-to-view-scroll
  "Compute {:scroll-left :scroll-top} that centres a node at (cx, cy)
   within a viewport of `viewport-w × viewport-h` rendering a tree of
   `tree-w × tree-h`. Clamps to [0, max-scroll] on each axis so we
   never scroll past the edges. Pure — the actual scrollLeft / scrollTop
   assignment lives in the UI layer."
  [cx cy viewport-w viewport-h tree-w tree-h]
  (let [target-x (- cx (/ viewport-w 2))
        target-y (- cy (/ viewport-h 2))
        max-x    (max 0 (- tree-w viewport-w))
        max-y    (max 0 (- tree-h viewport-h))]
    {:scroll-left (max 0 (min max-x target-x))
     :scroll-top  (max 0 (min max-y target-y))}))

(defn find-laid-node
  "Return the laid-out node with id `target-id` from a layout map's
   :nodes vector, or nil if not found. Used by the UI layer to drive
   fit-to-view scroll after a render."
  [layout target-id]
  (when (number? target-id)
    (some (fn [n] (when (= target-id (:id n)) n))
          (:nodes layout))))

(def dock-modes
  "View kinds the dock supports above the detail pane. :timeline is
   the SVG lane view (the default since PR 5); :causality is the tree
   view added in this PR. Dock-mode is orthogonal to axis-mode — only
   :timeline reads axis-mode at all."
  #{:timeline :causality})

(def default-dock-mode :timeline)

(defn dock-mode?
  "Predicate: is `m` one of the supported dock modes?"
  [m]
  (contains? dock-modes m))

(defn causality-empty-message
  "Hint string for the causality pane when no record is selected.
   The pane is only useful with a selection, so we explain that
   instead of showing a blank area."
  []
  "Select a record from the timeline to see its causality tree.")

(defn causality-over-cap-message
  "Hint string shown when the tree containing the selected record
   exceeds `causality-max-nodes`. The build truncates DFS once the
   cap is hit, so `node-count` is a lower bound rather than an exact
   count for deep trees; the wording (`≥ N nodes`) reflects that.
   Suggests picking a smaller leaf — the dock filter intentionally
   does NOT apply to the causality tree (which would silently break
   chains by hiding causes), so we don't suggest narrowing the
   filter here."
  [{:keys [node-count]}]
  (str "Causality tree exceeds the " causality-max-nodes
       "-node cap (≥ " node-count " nodes). "
       "Pick a smaller leaf record to view its subtree."))

(defn causality-leaf-message
  "Hint shown above the lone node when the selected record has
   neither a cause nor any effects in the buffer — most commonly a
   lifecycle / dom-attribute record emitted by a component's
   construction path (no enclosing dispatch frame, so causeId is
   null). Explains why the tree is a single node and tells the user
   what kind of record to pick if they want to see a chain."
  []
  (str "This record has no cause and no effects in the current "
       "buffer. Pick an event/dispatch* record (or one with a "
       "'Caused by' link in the detail pane) to see a chain."))

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
;; Sessions — view-state predicates
;; ---------------------------------------------------------------------------

(defn live-view?
  "True when the dock's current view is the live buffer (default)."
  [view]
  (= view :live))

(defn session-view?
  "True when the dock's current view targets a specific session id."
  [view]
  (and (vector? view) (= :session (first view))))

(defn import-view?
  "True when the dock's current view targets a specific imported trace.
   Imports are PR 11's loaded-from-disk record sets; structurally a
   third view-kind alongside :live and [:session N]. The dock chooses
   the record source for rendering by dispatching on these three
   predicates."
  [view]
  (and (vector? view) (= :import (first view))))

(defn view-id
  "Extract the id from a session or import view, or nil for :live.
   Used for indexing the per-view selection map and for resolving a
   chip click back to a stored session/import."
  [view]
  (when (and (vector? view)
             (or (= :session (first view))
                 (= :import  (first view))))
    (second view)))

;; ---------------------------------------------------------------------------
;; Dock — instance-field keys (Closure-safe string keys for gobj/get|set)
;; ---------------------------------------------------------------------------

(def k-shadow         "__xTraceHistoryShadow")
(def k-timeline-el    "__xTraceHistoryTimelineEl")
(def k-lanes-el       "__xTraceHistoryLanesEl")
(def k-svg-pane-el    "__xTraceHistorySvgPaneEl")
(def k-tooltip-el     "__xTraceHistoryTooltipEl")
(def k-count-el       "__xTraceHistoryCountEl")
(def k-pause-btn      "__xTraceHistoryPauseBtn")
(def k-record-btn     "__xTraceHistoryRecordBtn")
(def k-sessions-el    "__xTraceHistorySessionsEl")
(def k-detail-el      "__xTraceHistoryDetailEl")
(def k-splitter-el    "__xTraceHistorySplitterEl")
(def k-hint-el        "__xTraceHistoryHintEl")
(def k-tag-select-el  "__xTraceHistoryTagSelectEl")
(def k-filter         "__xTraceHistoryFilter")
;; The active view: :live (the always-on buffer) or [:session id].
;; render! consults this to choose which records to display.
(def k-view           "__xTraceHistoryView")
;; Per-view scrubber state. A map keyed by view value (:live or
;; [:session id]) → record-id. Each session keeps its own selection
;; across view switches.
(def k-view-selected  "__xTraceHistoryViewSelected")
(def k-sub-token      "__xTraceHistorySubToken")
(def k-mounted        "__xTraceHistoryMounted")
;; PR (this branch) — axis-mode toggle: :order (default) or :time.
;; The dock renders dots and routes the scrubber via the selected
;; mode. See index-x / time-x above for the math.
(def k-axis-mode      "__xTraceHistoryAxisMode")
;; PR (this branch) — collapse toggle. When true the dock shrinks to
;; a thin strip on the right edge so the user can interact with
;; components the full dock would otherwise cover. State lives on
;; the host so it survives a disconnect/reconnect cycle. k-collapse-btn
;; is the cached toggle <x-button> ref so apply-collapsed-state! can
;; swap the icon without re-querying.
(def k-collapsed      "__xTraceHistoryCollapsed")
(def k-collapse-btn   "__xTraceHistoryCollapseBtn")
;; PR 11 import-flow refs.
;; The hidden <input type="file"> the Import button delegates to; the
;; full-dock drag-drop overlay that surfaces while a file is being
;; hovered; the transient {msg, expires-at} pair the dock shows in
;; the hint area when validation rejects a drop.
(def k-import-input   "__xTraceHistoryImportInput")
(def k-drop-overlay   "__xTraceHistoryDropOverlay")
(def k-import-error   "__xTraceHistoryImportError")
;; Counter tracking nested dragenter/dragleave pairs across the
;; dock's descendants. The drop overlay shows while > 0 and hides at
;; 0; using a counter (rather than a flag) prevents flicker when a
;; drag passes over a child element.
(def k-drag-depth     "__xTraceHistoryDragDepth")
;; JS array of #js [target event-name handler] triples captured at
;; bind-listeners! time. unmount! iterates it to remove each listener,
;; so the static dock listeners cannot leak across a disconnect /
;; reconnect cycle.
(def k-listeners      "__xTraceHistoryListeners")
;; PR 15 — auto-switch heuristic. Records the import count seen at the
;; last subscriber tick so the dock can detect that a new import has
;; arrived (without re-firing for every render after the switch).
;; Mutates only when (a) a new import appears, or (b) imports were
;; removed (rebaseline). See maybe-auto-switch-import! in the dock.
(def k-auto-switch-import-count "__xTraceHistoryAutoSwitchImportCount")
;; PR 17 — causality DAG view. dock-mode is :timeline or :causality.
;; k-causality-el is the cached scrollable container the UI layer
;; reads clientWidth / clientHeight from for fit-to-view math.
;; k-causality-needs-fit is a transient flag set by handlers that
;; should trigger an auto-scroll on the next render (mode-switch,
;; selection-change inside causality, key-step). k-causality-layout
;; caches the last laid-out tree so apply-causality-fit! can resolve
;; the selected node's coords without rebuilding.
(def k-dock-mode             "__xTraceHistoryDockMode")
(def k-causality-el          "__xTraceHistoryCausalityEl")
(def k-causality-needs-fit   "__xTraceHistoryCausalityNeedsFit")
(def k-causality-layout      "__xTraceHistoryCausalityLayout")

