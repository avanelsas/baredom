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

      ;; Anything else is a typo or unhandled new type — warn loudly so
      ;; we don't silently lose the type-specific payload.
      (do
        (js/console.warn "[x-trace-history] unknown record type:" (str type))
        r))))

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

(def ^:private search-cache-key "__xTraceHistorySearchable")

(defn- safe-stringify
  "JSON.stringify a record, swallowing exceptions (e.g. a detail payload
   carrying a cyclic reference that slipped past the recorder's
   coercion). Returns a lowercase string so callers can substring-
   match case-insensitively."
  [^js r]
  (let [s (try (js/JSON.stringify r) (catch :default _ ""))]
    (when s (.toLowerCase s))))

(defn searchable-haystack
  "Return a lowercase, JSON-serialised view of the record suitable for
   substring matching. Memoised on the record itself under a stable
   gobj key — records are immutable values that the recorder shares
   across renders, so the first search builds the haystack once and
   every later search reuses it. clear! drops the records, taking the
   cache with them; the next batch of records gets a fresh haystack
   built on first access. This is the 'indexed lazily on first search'
   guarantee from the roadmap."
  [^js r]
  (or (gobj/get r search-cache-key)
      (let [s (safe-stringify r)]
        (gobj/set r search-cache-key s)
        s)))

(defn matches-search?
  "True iff `q` (already lowercase) is empty, nil, or appears as a
   substring of the record's searchable haystack. Pure helper for
   record-matches?."
  [^js r q]
  (or (nil? q)
      (= "" q)
      (let [hay (searchable-haystack r)]
        (and hay (not (neg? (.indexOf hay q)))))))

(defn record-matches?
  "True iff a single record passes the filter spec.

   spec keys:
     :tag        — string tag name; nil / blank / 'all' = match any
     :categories — set of category keywords to include; nil = include all
     :search     — lowercase substring required in the record's
                   JSON-serialised form; nil / blank = include all"
  [^js r {:keys [tag categories search]}]
  (and (or (nil? tag) (= "" tag) (= "all" tag) (= tag (.-tag r)))
       (or (nil? categories) (contains? categories (categorize-type (.-type r))))
       (matches-search? r search)))

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
   keyboard / scrubber paths to find adjacent records.

   Distinct from the private `index-of-record-id` helper used by
   `step-record` because that one operates on a vector specifically;
   this version works for any sequential input the caller has."
  [records target-id]
  (when (number? target-id)
    (let [n (count records)]
      (loop [i 0]
        (cond
          (>= i n)                                           nil
          (= target-id (.-id ^js (nth records i)))           i
          :else                                              (recur (inc i)))))))

(defn- index-of-record-id
  "Linear-scan a vector of records, returning the index of the first
   record whose `id` matches `target-id`, or nil if not found.
   Uses a native CLJS loop with `nth` (O(1) per probe on vectors)
   so we don't allocate a JS array just to call `.findIndex`."
  [records target-id]
  (let [n (count records)]
    (loop [i 0]
      (cond
        (>= i n)                                          nil
        (= target-id (.-id ^js (nth records i)))          i
        :else                                             (recur (inc i))))))

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
                (index-of-record-id filtered-records current-id))]
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
;; PR 16 — search input across recorded values. Caches the input
;; element so render!'s reads stay off querySelector.
(def k-search-input-el "__xTraceHistorySearchInputEl")
(def k-filter         "__xTraceHistoryFilter")
;; The active view: :live (the always-on buffer) or [:session id].
;; render! consults this to choose which records to display.
(def k-view           "__xTraceHistoryView")
;; Per-view scrubber state. A map keyed by view value (:live or
;; [:session id]) → record-id. Replaces the old single k-selected-id
;; slot so each session keeps its own selection across view switches.
(def k-view-selected  "__xTraceHistoryViewSelected")
;; Legacy alias kept so older test assertions and downstream readers
;; that reach for the live-view selection through k-selected-id keep
;; working. The dock writes through both keys when the view is :live.
(def k-selected-id    "__xTraceHistorySelectedId")
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
  /* The width transition makes the collapse / expand toggle feel
     responsive without being slow. The dock is otherwise position:
     fixed so transitioning width doesn't reflow the host page. */
  transition: width 160ms ease;
}
/* Collapsed mode: shrink the host to a thin vertical strip so the
   user can reach components the full dock would otherwise cover.
   The only interactive element left is the collapse-toggle button,
   which expands the dock back to full width when clicked. */
:host(.collapsed) {
  width: 32px;
}
:host(.collapsed) .dock > *:not(.header) { display: none; }
:host(.collapsed) .header {
  flex-direction: column;
  align-items: center;
  padding: 8px 0;
  gap: 4px;
}
:host(.collapsed) .header > *:not([data-x-th-action='collapse']) {
  display: none;
}
@media (prefers-reduced-motion: reduce) {
  :host { transition: none; }
}
.dock {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #11111b;
  border-left: 1px solid rgba(59,130,246,0.6);
  box-shadow: -4px 0 20px rgba(0,0,0,0.4);
  /* Anchor for the absolutely-positioned drop-overlay added in PR 11
     so its `inset: 0` reaches the dock edges. */
  position: relative;
}
.header {
  display: flex;
  /* Wrap so the count + action buttons survive narrow viewports
     (`min(420px, calc(100vw - 1rem))` reaches 304px at 320px screens,
     which is too tight for four action buttons + title + count on
     one row). */
  flex-wrap: wrap;
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
/* The tag dropdown is an <x-select>; its own shadow brings styling.
   We just constrain its width and font so it matches the dock's
   ui-monospace 11px tone. */
.filters x-select {
  font-size: 11px;
  max-width: 140px;
}
/* x-checkbox has no slot for its label, so the visible text sits
   next to the checkbox inside a <label> wrapper. The label uses
   inline-flex to keep the checkbox + text on one row, with a small
   gap. Cursor: pointer mirrors native checkbox-label affordance. */
.filters label.cat {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #a6adc8;
  font-size: 10px;
  cursor: pointer;
  user-select: none;
}
/* PR 16 — search input. x-search-field brings its own theming via its
   shadow DOM; the dock just constrains the host width so it shares
   the filter row's monospace tone with the tag dropdown. */
.filters x-search-field {
  font-size: 11px;
  flex: 1 1 140px;
  min-width: 100px;
  max-width: 240px;
}
/* Session strip: thin row above the filter row listing the live view
   plus any captured session as a clickable chip. Hidden when the only
   chip is Live (no sessions yet) so the dock stays compact. Scrolls
   horizontally if the chip count overflows the dock width. */
.sessions {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  background: rgba(0,0,0,0.15);
  border-bottom: 1px solid rgba(255,255,255,0.04);
  flex: 0 0 auto;
  overflow-x: auto;
  white-space: nowrap;
}
.sessions[hidden] { display: none; }
.session-chip {
  flex: 0 0 auto;
  background: rgba(255,255,255,0.04);
  color: #a6adc8;
  border: 1px solid rgba(255,255,255,0.08);
  border-radius: 3px;
  padding: 1px 6px;
  font: inherit;
  font-size: 10px;
  cursor: pointer;
  user-select: none;
}
.session-chip:hover { background: rgba(59,130,246,0.12); color: #cdd6f4; }
.session-chip.active {
  background: rgba(59,130,246,0.22);
  border-color: rgba(59,130,246,0.6);
  color: #89b4fa;
  font-weight: 600;
}
/* Imported traces are visually distinct from locally-captured
   sessions: amber border instead of blue, plus a small close button
   inside the chip so the user can drop the import without clearing
   live state. */
.session-chip.import {
  border-color: rgba(249,226,175,0.45);
  color: #f9e2af;
}
.session-chip.import.active {
  background: rgba(249,226,175,0.18);
  border-color: rgba(249,226,175,0.7);
  color: #f9e2af;
}
.session-chip.import .chip-close {
  background: transparent;
  border: none;
  color: inherit;
  cursor: pointer;
  font: inherit;
  font-size: 12px;
  line-height: 1;
  padding: 0 0 0 6px;
  margin: 0;
  opacity: 0.6;
}
.session-chip.import .chip-close:hover { opacity: 1; }
.session-chip .live-dot {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #f38ba8;
  margin-right: 4px;
  vertical-align: middle;
  animation: x-th-pulse 1s ease-in-out infinite;
}
@keyframes x-th-pulse {
  0%, 100% { opacity: 1; }
  50%      { opacity: 0.35; }
}
@media (prefers-reduced-motion: reduce) {
  .session-chip .live-dot { animation: none; }
}
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
.hint { color: #6c7086; font-size: 10px; padding: 4px 10px; flex: 0 0 auto; }
.hint.error { color: #f38ba8; }
/* Drop overlay — covers the whole dock while a file is being hovered.
   pointer-events:none keeps drag events flowing through to the dock root
   so the dragleave/drop bookkeeping doesn't fight the overlay layer. */
.drop-overlay {
  position: absolute;
  inset: 0;
  background: rgba(249,226,175,0.10);
  border: 2px dashed rgba(249,226,175,0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #f9e2af;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.02em;
  z-index: 100;
  pointer-events: none;
}
.drop-overlay[hidden] { display: none; }
/* The hidden file input is kept off-screen — clicked programmatically
   by the Import button — but we mark it `display:none` to keep it out
   of layout entirely. */
.import-input { display: none; }")
