(ns baredom.dev.x-trace-history.recorder
  "Effects layer for x-trace-history: state atom, hook installation,
   activation gating, JS API at window.BareDOM.traceHistory.*, a
   synchronous cause-id chain via a wrapper around
   EventTarget.prototype.dispatchEvent, and a small subscriber API
   consumed by the dock UI.

   See docs/x-trace-history-roadmap.md for the broader plan."
  (:require
   [goog.object :as gobj]
   [baredom.utils.dom :as du]
   [baredom.utils.component :as comp]
   [baredom.dev.x-trace-history.model :as model]))

;; ---------------------------------------------------------------------------
;; Constants
;; ---------------------------------------------------------------------------

(def ^:private window-base-key      "BareDOM")
(def ^:private window-api-key       "traceHistory")
(def ^:private capacity-flag        "BAREDOM_TRACE_HISTORY_CAPACITY")
(def ^:private component-id-key     "__xTraceHistoryId")
(def ^:private internal-host-marker "__xTraceHistoryInternal")

;; ---------------------------------------------------------------------------
;; State
;; ---------------------------------------------------------------------------

(defonce ^:private state
  (atom {:records           model/empty-records
         :next-id           0
         :paused?           false
         :capacity          model/default-capacity
         :next-component-id 0
         :components        {}
         ;; Sessions are bounded recording slices the user explicitly
         ;; captures. They are metadata only — `start-t` and `end-t`
         ;; give a t-range over the main ring buffer; records are
         ;; filtered on demand. No copy, no extra storage. The active
         ;; session has :end-t = nil; stop-session! sets it.
         :sessions          []
         :next-session-id   0
         :active-session-id nil
         ;; Sample-rate cap — see rate-limited? below. Map of string
         ;; key → last-accepted t. Reset by clear!.
         :rate-limit        {}
         ;; Imports are PR 11's loaded-from-disk traces. Each entry is
         ;; {:id :label :imported-at :envelope} — the envelope is the
         ;; full JS object that came in, so records/components/sessions
         ;; are all addressable without re-parsing. Imports are
         ;; preserved across clear! because the user dragged them in
         ;; deliberately; remove-import! drops them individually.
         :imports           []
         :next-import-id    0
         ;; Captured at install time from model/forensic? so per-record
         ;; checks don't have to read window state. False unless the
         ;; user activated with =raw.
         :forensic?         false}))

;; Subscriber pub/sub — one atom for three concerns: the token counter,
;; the rAF-coalesce flag, and the token→callback map. Folding them into
;; a single value keeps token allocation atomic with map insertion
;; (subscribe!) and removes the per-frame second atom read.
(defonce ^:private subscribers
  (atom {:next-token 0
         :pending?   false
         :by-token   {}}))

;; Cause-id chain: a stack of reserved record-ids. The dispatch-wrapper
;; pushes the reserved id of the dispatch on entry and pops on exit, so
;; any record produced inside the synchronous extent of a dispatch reads
;; (peek @cause-stack) to learn its cause. Empty when no dispatch is in
;; flight (top-level records, or async callbacks).
(defonce ^:private cause-stack (atom []))

;; Original EventTarget.prototype.dispatchEvent, captured the first time
;; the wrapper is installed. nil before any install! call. Re-exposed via
;; uninstall? checks so the wrapper can pass through cleanly when
;; trace-history is inactive.
(defonce ^:private original-dispatch-event (atom nil))

;; True between install! and uninstall!. The wrapper stays on the
;; EventTarget prototype across uninstall (defonce-shaped install can
;; only be done once safely), but its body checks this flag and falls
;; back to the original implementation when false.
(defonce ^:private wrapper-active? (atom false))

;; Counter for the dynamic-extent suppression scope used by
;; with-suppressed-recording!. The dock wraps its mount! in this so
;; that custom-element constructors triggered by parsing the dock's
;; skeleton (e.g. x-checkbox's internal du/set-attr! calls on freshly-
;; created, not-yet-appended divs) do not leak into the trace. Counter,
;; not flag, so nested with-suppressed-recording! calls compose.
(defonce ^:private suppression-depth (atom 0))

;; Memoised JS-array view of the ring buffer for hot-path readers
;; (records / session-records / sessions). Stored as #js [buf js-arr]
;; so reference-equality on the (persistent) buffer is enough to know
;; whether the cached array is still valid. Reset by every push (the
;; buffer ref changes) and by clear!. Saves the dock from paying an
;; O(N) clj->js+vec round-trip on every render tick.
(defonce ^:private records-cache (atom #js [nil nil]))

(defn- records-as-js-array
  "Return a JS array view of the current ring buffer, memoised by
   buffer identity. Callers MUST NOT mutate the returned array; the
   recorder treats it as immutable until the next push."
  []
  (let [recs        (:records @state)
        ^js cache   @records-cache
        cached-buf  (aget cache 0)
        cached-arr  (aget cache 1)]
    (if (identical? cached-buf recs)
      cached-arr
      (let [arr (clj->js (vec recs))]
        (reset! records-cache #js [recs arr])
        arr))))

;; ---------------------------------------------------------------------------
;; Subscriber API
;; ---------------------------------------------------------------------------

(defn- fire-subscribers!
  "Invoked from rAF. Clear the pending flag and call every subscriber.
   A throwing subscriber must not block the others or break the
   recorder."
  []
  (let [{:keys [by-token]} (swap! subscribers assoc :pending? false)]
    (doseq [[_ f] by-token]
      (try (f) (catch :default _ nil)))))

(defn- schedule-notify!
  "Coalesce subscriber notifications to one per animation frame.
   Multiple pushes within a single frame trigger a single rAF callback."
  []
  (let [[before _] (swap-vals! subscribers assoc :pending? true)]
    (when-not (:pending? before)
      (js/requestAnimationFrame fire-subscribers!))))

(defn- register-subscriber
  "Pure: returns updated subscribers state with `f` registered under
   a fresh token. The new token is `(:next-token result)`."
  [s f]
  (let [tok (inc (:next-token s))]
    (-> s
        (assoc :next-token tok)
        (assoc-in [:by-token tok] f))))

(defn subscribe!
  "Register f as a no-arg callback fired on the next animation frame
   after any record is pushed (or after pause! / resume! / clear!).
   Returns an opaque token to pass to unsubscribe!. Safe to call
   before install!."
  [f]
  (:next-token (swap! subscribers register-subscriber f)))

(defn unsubscribe!
  "Remove the subscriber identified by `tok`. Idempotent: calling with
   an unknown token is a no-op."
  [tok]
  (swap! subscribers update :by-token dissoc tok)
  nil)

;; ---------------------------------------------------------------------------
;; Hook implementation
;; ---------------------------------------------------------------------------

(defn- resolve-component-id!
  "Get-or-assign a stable component-id stored on the element via gobj.
   Returns {:id id :new? bool}. :id is nil for non-element targets
   (e.g. js/document); :new? is true iff this call wrote a fresh id
   onto the element.

   Side effect: when the element has no id yet, writes one to it. The
   id survives disconnect — a component removed and re-added keeps
   its id."
  [^js el tag next-id]
  (cond
    (= "document" tag)
    {:id nil :new? false}

    :else
    (let [existing (du/getv el component-id-key)]
      (if (some? existing)
        {:id existing :new? false}
        (do
          ;; assign-component-id! runs INSIDE the trace hook (record! →
          ;; find-owning-host → assign-component-id!); routing through
          ;; du/setv! would fire the hook recursively. The id assignment
          ;; is a recorder-internal correlation key, never user-visible.
          (gobj/set el component-id-key next-id) ;; allow-gobj: recorder bootstrap
          {:id next-id :new? true})))))

(defn- reserve-id!
  "Atomically claim the next record-id, returning the value. The dispatch
   wrapper reserves an id at frame entry so any records produced by
   handlers can stamp it as their causeId — even though the dispatch's
   own record is pushed AFTER handlers complete."
  []
  (let [s (swap! state update :next-id inc)]
    (dec (:next-id s))))

;; ---------------------------------------------------------------------------
;; Recording boundary — suppress dock-internal events
;;
;; The dock itself uses BareDOM components (x-button, x-checkbox, x-select)
;; which fire CustomEvents. Without a boundary, every Pause click would
;; record `x-button:press` and pollute the trace. mark-internal! flags a
;; host element so the wrapper and record! both early-return for events
;; whose origin is inside that host's shadow tree. The marker only blocks
;; events ORIGINATING inside the host — lifecycle events ON the host
;; itself are still recorded (the dock's own connect/disconnect remains
;; visible in traces).
;; ---------------------------------------------------------------------------

(defn mark-internal!
  "Stamp `el` so events originating inside its shadow tree are bypassed
   by the recorder. Public — used by the dock to mark its host element
   on mount.

   This write IS the act of installing the internal-host filter:
   routing through du/setv! would fire the hook BEFORE the filter is in
   place, recording the bootstrap write itself in the user's trace.
   The marker is recorder-internal state, never user-visible."
  [^js el]
  (gobj/set el internal-host-marker true)) ;; allow-gobj: recorder bootstrap

(defn with-suppressed-recording!
  "Call thunk f with all recording suppressed for its synchronous
   extent. Use this around dock setup so custom-element constructors
   that fire during innerHTML parsing — and call du/set-attr! /
   du/setv! / dispatch on freshly-created, not-yet-appended internal
   elements — do not leak records into the trace. The internal-host
   marker only catches events whose target is already attached
   somewhere in the marked shadow tree; constructor-time mutations
   on detached internals predate that attachment, so they need this
   broader gate. Counter-based, so nested calls compose."
  [f]
  (swap! suppression-depth inc)
  (try
    (f)
    (finally
      (swap! suppression-depth dec))))

(defn- ancestor-up
  "Climb one step toward the root: across the shadow boundary when
   `node` is inside one (root.host), else via parentNode. Returns nil
   when no parent exists."
  [^js node]
  (let [^js root (.getRootNode node)]
    (if (instance? js/ShadowRoot root)
      (.-host root)
      (.-parentNode node))))

(defn- custom-element-tag?
  "True if `name` looks like a custom-element tag — has a hyphen, per
   the HTML spec for valid custom-element names."
  [name]
  (and (some? name) (.includes ^js name "-")))

(defn- find-owning-host
  "Walk up from `el` (across shadow boundaries via root.host) to the
   nearest custom-element ancestor — including `el` itself. Returns nil
   if no custom-element ancestor exists.

   Used to attribute shadow-internal mutations to their owning host so
   the trace recorder's component-id and rate-limit machinery key off
   the host instead of treating each shadow-internal node as its own
   component. A render touching ten attributes on a shadow <button>
   inside x-button now produces one rate-limited record per attribute
   under the x-button host's cid, not ten records under ten anonymous
   cids."
  [^js el]
  (loop [^js node el]
    (cond
      (nil? node)                              nil
      (not (instance? js/Element node))        nil
      (custom-element-tag? (.-localName node)) node
      :else                                    (recur (ancestor-up node)))))

(defn- inside-internal-host?
  "True when an ancestor of `el` bears the internal-host-marker. Walks
   across (open) shadow boundaries via root.host and through the light
   tree via parentNode, so a marked host outside any shadow still acts
   as a boundary. The marker on `el` itself does NOT count — we ask
   whether `el` is *inside* a marked host, not whether it IS one.
   Used for dispatch + lifecycle filtering so the dock's own
   connect/disconnect / dispatched events remain visible in traces.

   Returns false for nil, document, and non-Node EventTargets (window,
   XMLHttpRequest, MessagePort, WebSocket — none have getRootNode)."
  [^js el]
  (if-not (instance? js/Node el)
    false
    (loop [^js node (ancestor-up el)]
      (cond
        (or (nil? node) (identical? node js/document)) false
        (gobj/get node internal-host-marker)           true
        :else                                          (recur (ancestor-up node))))))

(def ^:private write-payload-types
  "Payload types representing writes that should be filtered when their
   `:el` IS the marked host itself, not just inside one. These all
   originate from `du/setv!` / `du/set-attr!` / `du/remove-attr!` calls
   in the dock or recorder UI, and would otherwise pollute traces with
   noisy bookkeeping records."
  #{:state/instance-field-set
    :dom/attribute-set
    :dom/attribute-removed})

(defn- internal-skip?
  "True when the payload's `:el` is inside a marked internal host. For
   write payloads (state / DOM mutation), also catches the marker on
   `el` itself — post-gobj-on-host-sweep, the dock's own filter / view
   writes route through `du/setv!` against the dock's marked host
   directly. Dispatch and lifecycle payloads use the strict ancestor
   check (`inside-internal-host?`) so the dock's own dispatched events
   and connect/disconnect stay visible."
  [payload]
  (let [^js el (:el payload)]
    (if (contains? write-payload-types (:type payload))
      (if-not (instance? js/Node el)
        false
        (or (gobj/get el internal-host-marker) ;; allow-gobj: read recorder's own marker
            (inside-internal-host? el)))
      (inside-internal-host? el))))

(defn- push-record-with-id!
  "Build and push a record with a pre-determined id and timestamp. Reads
   `(peek @cause-stack)` to stamp the record's causeId. Updates the
   :components side-index when a new component is observed.

   Used by both the auto-id path (record!) and the wrapper path
   (record-dispatch!).

   Skips when the payload's :el is inside an internal-marked host —
   catches setv! / set-attr! / lifecycle records from components nested
   inside the dock.

   When `force?` is true, bypasses the `:paused?` check. The dispatch
   wrapper sets this for the post-handler dispatch record so that any
   handler records already in the buffer that cite the reserved id as
   their causeId still resolve to a real record when `pause!` lands
   mid-dispatch. Internal-host events remain filtered."
  ([payload id t]
   (push-record-with-id! payload id t false))
  ([payload id t force?]
  (let [s @state
        ^js el (:el payload)]
    (when-not (or (and (not force?) (:paused? s))
                  (internal-skip? payload))
      ;; Attribute records to the owning custom-element host. A
      ;; mutation on a shadow-internal node — e.g. (du/set-attr! sel
      ;; "data-hover" "true") inside x-button's apply-button-data-state!
      ;; — should appear in the trace under x-button's cid, not as a
      ;; new anonymous cid for the shadow <button>. find-owning-host
      ;; falls back to `el` when no custom-element ancestor exists, so
      ;; native-element targets and host events both pass through
      ;; unchanged.
      (let [^js host             (or (find-owning-host el) el)
            tag                  (model/tag-of host)
            {cid :id new-cid? :new?} (resolve-component-id! host tag (:next-component-id s))
            cause-id             (peek @cause-stack)
            payload+         (assoc payload
                                    :tag tag
                                    :component-id cid
                                    :cause-id cause-id)
            rec              (model/make-record payload+ id t)
            new-recs         (model/push-record (:records s) rec (:capacity s))
            new-comps        (if new-cid?
                               (assoc (:components s) cid {:tag tag :first-seen t})
                               (:components s))
            new-next-cid     (if new-cid? (inc cid) (:next-component-id s))]
        (swap! state assoc
               :records           new-recs
               :next-component-id new-next-cid
               :components        new-comps)
        (schedule-notify!))))))

(def ^:private wrapper-handled-types
  "Payload types whose recording is handled by the dispatch-wrapper itself.
   When the wrapper is active these are gated out of record! to avoid
   double-recording — the wrapper produces them with the correct
   reserved id and entry timestamp."
  #{:event/dispatch :event/dispatch-cancelable :event/dispatch-document})

;; ---------------------------------------------------------------------------
;; Sample-rate cap — drop duplicates within 16ms per (componentId, key)
;;
;; Animation-driven components fire CustomEvents and instance-field
;; writes once per frame (or more), and a 60fps drag fills the buffer
;; with near-identical records. Rate-limiting keeps the trace
;; representative without truncating the underlying buffer. Forensic
;; mode (?baredom-trace-history=raw) bypasses the cap so users can
;; capture every record for diagnostics.
;; ---------------------------------------------------------------------------

(def ^:private rate-limit-window-ms 16)

(defn- rate-limit-key
  "Build a string rate-limit key for a payload, or nil if the payload
   is exempt. Three exemption categories:
   - lifecycle/*: once-per-state-change, useful even when bursty
   - unknown types: never seen, no contract
   - elements without a stamped componentId: the first record from
     any element always passes (cid is assigned by push-record-with-id!
     on first record). Also catches document-target events which never
     get a cid.
   Reading componentId is side-effect-free here — it's just a gobj
   read of the previously-stamped field."
  [payload ^js el]
  ;; Look up the cid on the owning host so shadow-internal mutations
  ;; throttle against the host's bucket — without this, a render that
  ;; writes ten data-* attributes on a shadow <button> would emit ten
  ;; records, none of them rate-limited (shadow nodes carry no cid).
  (let [^js host (or (find-owning-host el) el)]
    (when-let [cid (gobj/get host component-id-key)]
      (case (:type payload)
        (:event/dispatch :event/dispatch-cancelable :event/dispatch-document)
        (str cid ":evt:" (:event-name payload))

        :state/instance-field-set
        (str cid ":fld:" (:field payload))

        (:dom/attribute-set :dom/attribute-removed)
        (str cid ":attr:" (:attribute payload))

        ;; lifecycle/* and unknown types: nil → no rate-limit.
        nil))))

(defn- rate-limited?
  "Pure: was this (payload, el, t) within `rate-limit-window-ms` of
   the previously-accepted record under the same key? Forensic mode
   never returns true. Lifecycle and unknown types never return true
   (rate-limit-key returns nil for them)."
  [s payload ^js el t]
  (and (not (:forensic? s))
       (when-let [k (rate-limit-key payload el)]
         (when-let [last-t (get (:rate-limit s) k)]
           (< (- t last-t) rate-limit-window-ms)))))

(defn- remember-rate-limit!
  "After a record is accepted, stamp `t` against its rate-limit key so
   subsequent records within the window are dropped. No-op for exempt
   types or in forensic mode."
  [payload ^js el t]
  (when-not (:forensic? @state)
    (when-let [k (rate-limit-key payload el)]
      (swap! state assoc-in [:rate-limit k] t))))

(defn- record!
  "Hook function passed to du/trace-hook and comp/lifecycle-hook. Receives a
   CLJS payload, reserves an id, and pushes a record onto the ring buffer.

   Skipped under any of these conditions, BEFORE id reservation so
   internal activity does not consume numeric ids that user-facing
   records would otherwise occupy:
   - inside an active with-suppressed-recording! scope (dock setup)
   - dispatch-wrapper handles :event/* itself (gated to avoid double-record)
   - payload's :el is inside a marked internal host (PR-A boundary)
   - sample-rate cap: a record with the same (componentId, key) was
     accepted within the last 16ms (PR 9). Forensic mode bypasses."
  [payload]
  (let [type   (:type payload)
        ^js el (:el payload)
        t      (.now js/performance)]
    (when-not (or (pos? @suppression-depth)
                  (and @wrapper-active?
                       (contains? wrapper-handled-types type))
                  (internal-skip? payload)
                  (rate-limited? @state payload el t))
      (let [id (reserve-id!)]
        (push-record-with-id! payload id t)
        (remember-rate-limit! payload el t)))))

;; ---------------------------------------------------------------------------
;; Dispatch wrapper — synchronous cause-id chain
;; ---------------------------------------------------------------------------

(defn- classify-dispatch
  "Map a (target, event) pair to one of the three :event/* type keywords.
   Document target wins regardless of bubbles/composed flags so external
   document-level dispatches still classify cleanly."
  [^js this ^js ev]
  (cond
    (identical? this js/document) :event/dispatch-document
    (.-cancelable ev)             :event/dispatch-cancelable
    :else                         :event/dispatch))

(defn- push-dispatch-record!
  "Push a record describing `ev` using the reserved id and entry-time
   captured at wrapper entry. Read `defaultPrevented` post-handler so
   it reflects the final value.

   Forces the push (bypasses `:paused?`) so that handler records
   pushed before a mid-dispatch `pause!` always have a real cause
   record to resolve. Internal-host events stay filtered."
  [^js this ^js ev reserved-id entry-t]
  (let [type (classify-dispatch this ev)
        payload {:type               type
                 :el                 this
                 :event-name         (.-type ev)
                 :detail             (.-detail ev)
                 :cancelable?        (.-cancelable ev)
                 :default-prevented? (.-defaultPrevented ev)}]
    (push-record-with-id! payload reserved-id entry-t true)))

(defn- wrapped-dispatch
  "Body of the dispatchEvent wrapper. When wrapper-active? and not paused:
     1. Reserve an id and capture entry-time.
     2. Push the reserved id onto the cause stack so any records
        produced by handlers stamp it as their causeId.
     3. Call the original dispatchEvent (handlers run synchronously).
     4. Pop the cause stack (in finally so a throwing handler still
        unwinds cleanly).
     5. For CustomEvents, push the dispatch's own record with the
        reserved id and entry-time. Programmatically-dispatched
        native-shaped events (e.g. `el.dispatchEvent(new PointerEvent
        ('pointerdown'))`) are NOT recorded as records, but they DO
        establish a cause frame so records produced inside the
        handler attribute correctly.

   IMPORTANT: trusted, user-initiated events (real clicks, keys,
   pointermoves dispatched by the browser) are NOT routed through
   the JS-visible `EventTarget.prototype.dispatchEvent` in any major
   engine — the user agent delivers them via its internal dispatch
   path. As a result, no cause frame is established for the root
   user gesture; CustomEvents fired from a real click handler land
   with `causeId: null`. The cause-chain anchors on the first
   programmatic dispatch in the call stack, which is what consumers
   of BareDOM care about in practice.

   When inactive or paused, pass straight through to the original."
  [^js this ^js ev]
  (let [^js orig @original-dispatch-event]
    (cond
      ;; Inactive, paused, or dock-internal dispatch: no id reservation,
      ;; no cause-frame push, no record. Internal dispatches must NOT
      ;; establish a cause frame — otherwise records produced after the
      ;; dispatch (in the same call stack but originating from outside
      ;; the dock) would wrongly stamp the dock dispatch's reserved id.
      (or (not @wrapper-active?)
          (:paused? @state)
          (pos? @suppression-depth)
          (inside-internal-host? this))
      (.call orig this ev)

      :else
      (let [custom?  (instance? js/CustomEvent ev)
            entry-t  (.now js/performance)
            ;; Build a minimal payload for the rate-limit lookup.
            ;; classify-dispatch is cheap (a couple of branches) and
            ;; only invoked for CustomEvents where rate-limit applies.
            payload  (when custom?
                       {:type       (classify-dispatch this ev)
                        :event-name (.-type ev)})]
        (if (and custom? (rate-limited? @state payload this entry-t))
          ;; Sample-rate cap fired: pass through with no frame, no
          ;; record. Records produced inside the handler attribute to
          ;; whatever frame is already on the cause-stack (could be
          ;; nil) — same as for any rate-limited dispatch, no
          ;; dangling causeId references.
          (.call orig this ev)
          (let [reserved-id (reserve-id!)
                _           (swap! cause-stack conj reserved-id)
                result      (try
                              (.call orig this ev)
                              (finally
                                (swap! cause-stack pop)))]
            (when custom?
              (try
                (push-dispatch-record! this ev reserved-id entry-t)
                (remember-rate-limit! payload this entry-t)
                (catch :default _ nil)))
            result))))))

(def ^:private wrapper-stamp-key
  "Marker stamped onto our wrapper function so install-once can detect
   whether dispatchEvent has already been replaced (e.g. on hot-reload
   or repeated install!)."
  "__xTraceHistoryWrapped")

(defn- install-dispatch-wrapper-once!
  "Replace EventTarget.prototype.dispatchEvent with a stamped wrapper that
   delegates to wrapped-dispatch. Idempotent: subsequent calls are
   no-ops. The wrapper is permanent — uninstall! flips wrapper-active?
   to false and the wrapper falls through to the original."
  []
  (let [^js proto (.-prototype js/EventTarget)
        ^js curr  (.-dispatchEvent proto)]
    (when-not (gobj/get curr wrapper-stamp-key)
      (reset! original-dispatch-event curr)
      (let [wrapped (fn [^js ev]
                      (this-as ^js this
                        (wrapped-dispatch this ev)))]
        (gobj/set wrapped wrapper-stamp-key true)
        (set! (.-dispatchEvent proto) wrapped)))))

;; ---------------------------------------------------------------------------
;; Public API (exposed via window.BareDOM.traceHistory)
;; ---------------------------------------------------------------------------

(defn records
  "Returns a JS Array of all recorded events, oldest first. The
   returned array is memoised against the ring-buffer identity —
   callers MUST NOT mutate it."
  []
  (records-as-js-array))

(defn components
  "Returns a JS object mapping componentId (as string key) to
   {tag, firstSeen}. The index is monotonic for the lifetime of the page —
   not cleared by clear!. Useful for resolving componentId values found in
   records back to their tag and first-observed timestamp."
  []
  (let [comps (:components @state)
        ^js result (js-obj)]
    (doseq [[id info] comps]
      (gobj/set result (str id)
                (js-obj "tag"       (:tag info)
                        "firstSeen" (:first-seen info))))
    result))

(defn- import-envelope-tags
  "Pull every tag string out of an imported envelope's components index.
   The envelope's `components` is a JS object (cid → {tag, firstSeen})
   so we walk it via gobj rather than seq destructuring."
  [^js envelope]
  (let [^js comps (.-components envelope)]
    (if (nil? comps)
      []
      (->> (.keys js/Object comps)
           (map (fn [k] (.-tag (gobj/get comps k))))))))

(defn observed-tags
  "Returns a sorted, distinct CLJS vector of tag-name strings for every
   component the recorder has seen — both live components on this page
   and components carried in any imported traces. The dock uses this to
   populate the tag-filter dropdown without taking a static dependency
   on x-debug-registry, which would otherwise pull every component into
   the x-trace-history ESM bundle and defeat per-module distribution."
  []
  (let [s         @state
        live-tags (map :tag (vals (:components s)))
        imp-tags  (mapcat (fn [{:keys [^js envelope]}]
                            (import-envelope-tags envelope))
                          (:imports s))]
    (->> (concat live-tags imp-tags)
         (into (sorted-set))
         vec)))

(defn paused?
  "Returns true iff recording is currently paused."
  []
  (:paused? @state))

(defn pause!
  "Stop recording new events. Existing records are preserved."
  []
  (swap! state assoc :paused? true)
  (schedule-notify!))

(defn resume!
  "Resume recording after pause!."
  []
  (swap! state assoc :paused? false)
  (schedule-notify!))

(defn clear!
  "Empty the ring buffer and reset the record-id counter to 0. Drops all
   sessions too — sessions reference t-ranges in the buffer, so an
   emptied buffer leaves them with no records anyway. Document the
   joint behavior so callers don't expect sessions to survive Clear.

   Intentionally preserves :paused? state, the :components index, and the
   component-id counter — component identity is monotonic for the page
   lifetime since live elements still carry their stored cids; resetting
   would cause new components to collide with already-stored ids."
  []
  (swap! state assoc
         :records           model/empty-records
         :next-id           0
         :sessions          []
         :next-session-id   0
         :active-session-id nil
         :rate-limit        {})
  (schedule-notify!))

(defn forensic-mode?
  "True when the recorder was installed in forensic mode
   (?baredom-trace-history=raw or window.BAREDOM_TRACE_HISTORY=\"raw\").
   Forensic mode disables the sample-rate cap and is the dock's signal
   to default the :state category checkbox to ON. Captured at install
   time so per-record reads don't have to touch window state.

   Disambiguated from `model/forensic?` (which reads live window state
   at the moment of the call) by name — the recorder's flag is frozen
   at install."
  []
  (:forensic? @state))

;; ---------------------------------------------------------------------------
;; Sessions — bounded recording slices the user explicitly captures
;; ---------------------------------------------------------------------------

(defn active-session-id
  "Return the id of the currently-recording session, or nil when no
   session is active. Useful for UI state."
  []
  (:active-session-id @state))

(defn- finalize-session
  "Stamp end-t and end-id onto the currently-active session record.
   Pure helper over the :sessions vector — caller swaps the result
   back in. end-id is the FIRST id NOT in the session (half-open
   interval), so a session with no recorded events satisfies
   start-id == end-id."
  [sessions active-id end-t end-id]
  (if (nil? active-id)
    sessions
    (mapv (fn [{:keys [id] :as s}]
            (if (= id active-id)
              (assoc s :end-t end-t :end-id end-id)
              s))
          sessions)))

(defn stop-session!
  "Close the currently-active session by stamping its :end-t and
   :end-id. No-op when nothing is active. After stop, sessions
   reports the session with both timestamps and id bounds set."
  []
  (let [t (.now js/performance)]
    (swap! state
           (fn [s]
             (-> s
                 (update :sessions finalize-session
                         (:active-session-id s) t (:next-id s))
                 (assoc :active-session-id nil))))
    (schedule-notify!)))

(defn start-session!
  "Begin a new bounded recording slice. Returns the new session id.
   If a session is already active, it is auto-stopped first (with a
   console warning) so the user doesn't end up with a never-ending
   open session because they forgot to click Stop.

   Sessions are bounded by record-id range, not timestamp. Performance
   timers have clamped resolution (Spectre mitigations) and adjacent
   records can share a `t`, so a t-range filter is unreliable at the
   boundaries. Ids are atomic and strictly monotonic."
  []
  (when (some? (active-session-id))
    (js/console.warn
     "[x-trace-history] start-session! called while a session was already active — stopping the previous one")
    (stop-session!))
  (let [t (.now js/performance)]
    (swap! state
           (fn [s]
             (let [id       (:next-session-id s)
                   start-id (:next-id s)]
               (-> s
                   (update :sessions conj
                           {:id       id
                            :label    (str "Session " id)
                            :start-t  t
                            :end-t    nil
                            :start-id start-id
                            :end-id   nil})
                   (assoc :active-session-id id
                          :next-session-id (inc id))))))
    (schedule-notify!)
    (:active-session-id @state)))

(defn- session-record-range?
  "Predicate: does record `r` fall inside session `sess`? Uses the
   half-open id range [start-id, end-id). An open session (:end-id
   nil) extends to +infinity, so records pushed during recording
   show up live."
  [^js r {:keys [start-id end-id]}]
  (let [id (.-id r)]
    (and (>= id start-id)
         (or (nil? end-id) (< id end-id)))))

(defn sessions
  "Return a JS array of session metadata, one entry per session.
   Each entry is a JS object: {id, label, startT, endT, recordCount}.
   endT is null while the session is active. recordCount is computed
   from the live ring buffer at call time — sessions do not own
   storage.

   The JS-array conversion of the buffer is hoisted out of the session
   loop so it pays one O(N) materialisation instead of O(sessions × N)."
  []
  (let [s         @state
        ^js recs  (records-as-js-array)
        rec-seq   (array-seq recs)]
    (->> (:sessions s)
         (mapv (fn [{:keys [id label start-t end-t] :as sess}]
                 (let [n (count
                          (filter (fn [^js r] (session-record-range? r sess))
                                  rec-seq))]
                   (js-obj "id"          id
                           "label"       label
                           "startT"      start-t
                           "endT"        (or end-t nil)
                           "recordCount" n))))
         clj->js)))

(defn session-records
  "Return a JS array of records inside the session with the given id,
   in chronological order. Empty array if id is unknown or the session
   has no records in the current buffer."
  [target-id]
  (let [s    @state
        sess (first (filter #(= (:id %) target-id) (:sessions s)))]
    (if sess
      (->> (records-as-js-array)
           array-seq
           (filter (fn [^js r] (session-record-range? r sess)))
           (sort-by (fn [^js r] (.-t r)))
           clj->js)
      #js [])))

;; ---------------------------------------------------------------------------
;; Imports — loaded-from-disk traces shown alongside the live recording
;;
;; PR 11 introduces a third "view kind" for the dock: alongside :live
;; and [:session N], an imported trace presents as [:import N] with
;; its own records, components index, and sessions metadata read
;; straight from the dropped/picked envelope. Live recording continues
;; underneath; imports are independent storage and survive clear!.
;; ---------------------------------------------------------------------------

(defn- coerce-to-envelope
  "Accept either a JS object (already parsed) or a string (JSON to
   parse) and return a `{:ok env}` or `{:error msg}` map. Centralises
   the entry-point coercion so import-trace! handles both flavours
   uniformly."
  [input]
  (cond
    (string? input)            (model/parse-export input)
    (object? input)            (model/validate-envelope input)
    :else                      {:error "Import expects a JSON string or a parsed envelope object."}))

(defn import-trace!
  "Load an exported trace into the recorder. Accepts either a JSON
   string or an already-parsed JS object. On success returns the new
   import id (number); on validation failure returns nil and logs
   the reason via console.warn so callers can react.

   `label` is the human-readable string shown on the chip — usually
   the source filename minus extension; falls back to `Import N` when
   blank/missing. Imports do NOT replace the live buffer — they are
   stored alongside it. `clear!` does NOT drop imports; use
   `remove-import!` to remove a single one."
  ([input] (import-trace! input nil))
  ([input label]
   (let [{:keys [ok error]} (coerce-to-envelope input)]
     (cond
       error
       (do (js/console.warn "[x-trace-history] import rejected:" error)
           nil)

       :else
       (let [t      (.now js/performance)
             id     (:next-import-id @state)
             label* (if (or (nil? label) (= "" label))
                      (str "Import " id)
                      label)
             entry  {:id          id
                     :label       label*
                     :imported-at t
                     :envelope    ok}]
         (swap! state (fn [s]
                        (-> s
                            (update :imports conj entry)
                            (update :next-import-id inc))))
         (schedule-notify!)
         id)))))

(defn remove-import!
  "Drop the import with id `target-id`. No-op when id is unknown so
   double-removes from concurrent UI clicks don't error. Returns nil."
  [target-id]
  (swap! state update :imports
         (fn [imports]
           (vec (remove (fn [{:keys [id]}] (= id target-id)) imports))))
  (schedule-notify!))

(defn imports
  "Return a JS array of import metadata: {id, label, importedAt,
   recordCount}. recordCount is computed from the stored envelope at
   call time (cheap — it's a `.-length` read on the envelope's records
   array, no allocation)."
  []
  (->> (:imports @state)
       (mapv (fn [{:keys [id label imported-at ^js envelope]}]
               (js-obj "id"          id
                       "label"       label
                       "importedAt"  imported-at
                       "recordCount" (.-length (.-records envelope)))))
       clj->js))

(defn import-records
  "Return the JS array of records inside the import with id `target-id`,
   in chronological order. Returns an empty array when the id is
   unknown so the dock can degrade gracefully on a stale chip click."
  [target-id]
  (if-let [entry (first (filter #(= (:id %) target-id) (:imports @state)))]
    (.-records ^js (:envelope entry))
    #js []))

;; ---------------------------------------------------------------------------
;; Export — JSON envelope + Blob download
;;
;; export! materialises the current recorder state into the schema-stable
;; JS envelope documented in docs/x-trace-history-schema.md. download-trace!
;; serialises that envelope, constructs a Blob, and clicks a synthetic
;; anchor so the browser saves it under the canonical filename. Both
;; functions are exposed on window.BareDOM.traceHistory so consumers can
;; trigger exports from the console or build their own toolbars.
;; ---------------------------------------------------------------------------

(defn- export-context
  "Capture the wall-clock + environment context that gets stamped into
   the envelope's top-level fields. Pure of recorder state — only reads
   the supplied `now`, `js/window`, and `js/navigator`. The caller is
   responsible for plumbing `:forensic?` in so make-export sees a
   consistent snapshot. Split out so download-trace! can pass a shared
   timestamp to both the envelope and the filename slug."
  [^js now forensic?]
  {:exported-at (.getTime now)
   :origin      (or (some-> js/window .-location .-href) "")
   :user-agent  (or (some-> js/navigator .-userAgent) "")
   :forensic?   forensic?})

(defn export!
  "Return the current recorder state as a schema-stable JS envelope
   ready for `JSON.stringify`. Captures records, the components index,
   and any sessions metadata, plus wall-clock context (`exportedAt`,
   `origin`, `userAgent`, `forensic`). Does NOT mutate state.

   The returned envelope's `records` field is reference-equal to the
   recorder's memoised JS array — callers MUST NOT mutate it. Treat
   the envelope as read-only or call `JSON.parse(JSON.stringify(env))`
   for an independent copy.

   Safe to call when paused, mid-session, or with an empty buffer.
   See docs/x-trace-history-schema.md for the contract."
  []
  (let [s        @state
        ^js recs (records-as-js-array)
        ctx      (export-context (js/Date.) (:forensic? s))]
    (model/make-export recs (:components s) (:sessions s) ctx)))

(defn- trigger-download!
  "Build a Blob from a pretty-printed JSON string, attach an off-DOM
   anchor whose download attribute drives the filename, click it, then
   revoke the object URL so the Blob is eligible for GC. The anchor is
   appended to <body> (not just constructed) because Firefox ignores
   programmatic clicks on detached anchors.

   The append/click/remove triple and the object-URL allocation are
   wrapped in try/finally so a throw inside `.click()` (patched
   prototype, exotic CSP, etc.) still revokes the URL and detaches
   the anchor — no leaks, no orphan nodes."
  [json-str filename]
  (let [^js blob (js/Blob. #js [json-str] #js {:type "application/json"})
        url      (js/URL.createObjectURL blob)
        ^js a    (.createElement js/document "a")]
    (set! (.-href a) url)
    (set! (.-download a) filename)
    (set! (.. a -style -display) "none")
    (.appendChild (.-body js/document) a)
    (try
      (.click a)
      (finally
        (.removeChild (.-body js/document) a)
        (js/URL.revokeObjectURL url)))))

(defn download-trace!
  "Trigger a browser download of the current trace as
   `baredom-trace-YYYYMMDD-HHmmss.trace.json`. The same timestamp drives
   both the envelope's `exportedAt` and the filename slug, so a file's
   name and contents agree on when it was captured.

   Returns nil. Safe to call from anywhere — pure side effect on the
   document."
  []
  (let [now      (js/Date.)
        s        @state
        ^js recs (records-as-js-array)
        ctx      (export-context now (:forensic? s))
        envelope (model/make-export recs (:components s) (:sessions s) ctx)
        json-str (js/JSON.stringify envelope nil 2)
        filename (model/download-filename now)]
    (trigger-download! json-str filename)))

;; ---------------------------------------------------------------------------
;; Install / uninstall
;; ---------------------------------------------------------------------------

(defn- read-capacity
  "Read window.BAREDOM_TRACE_HISTORY_CAPACITY override, defaulting when
   absent or non-positive."
  []
  (let [v (gobj/get js/window capacity-flag)]
    (if (and (number? v) (pos? v))
      v
      model/default-capacity)))

(defn- install-window-api!
  "Set window.BareDOM.traceHistory to the API object, merging into any
   existing window.BareDOM."
  []
  (let [^js base (or (gobj/get js/window window-base-key) (js-obj))
        ^js api  (js-obj
                  "records"        records
                  "components"     components
                  "pause"          pause!
                  "resume"         resume!
                  "clear"          clear!
                  "startSession"   start-session!
                  "stopSession"    stop-session!
                  "sessions"       sessions
                  "sessionRecords" session-records
                  "export"         export!
                  "download"       download-trace!
                  "import"         import-trace!
                  "imports"        imports
                  "importRecords"  import-records
                  "removeImport"   remove-import!)]
    (gobj/set base window-api-key api)
    (gobj/set js/window window-base-key base)))

(defn install!
  "Activate recording. Idempotent and re-entrant: safe to call multiple
   times. Re-runs every time so hot-reloads of the recorder ns refresh the
   hook references to the newly-loaded `record!` symbol.

   Resets `suppression-depth` and `cause-stack` so a hot-reload that
   interrupts a `with-suppressed-recording!` body or a throwing handler
   inside the dispatch wrapper cannot leave either stuck (a stuck
   suppression-depth silently disables recording; a stuck cause-stack
   stamps every subsequent record with a stale causeId)."
  []
  (swap! state assoc
         :capacity  (read-capacity)
         :forensic? (model/forensic? js/window))
  (reset! du/trace-hook record!)
  (reset! comp/lifecycle-hook record!)
  (reset! suppression-depth 0)
  (reset! cause-stack [])
  (install-dispatch-wrapper-once!)
  (reset! wrapper-active? true)
  (install-window-api!))

(defn uninstall!
  "Deactivate recording. Used by tests; not part of the consumer API.
   Leaves the dispatchEvent wrapper installed (it falls through when
   inactive) and the JS API in place so any console references remain
   valid. Clears the cause stack so a leftover frame from an
   interrupted dispatch can't taint subsequent records."
  []
  (reset! du/trace-hook nil)
  (reset! comp/lifecycle-hook nil)
  (reset! wrapper-active? false)
  (reset! cause-stack []))

;; ---------------------------------------------------------------------------
;; Activation
;; ---------------------------------------------------------------------------

(defn register!
  "Called once at app start. If activated, install recording and log a
   console banner. Mirrors x-debug's opt-in pattern."
  []
  (when (model/enabled? js/window)
    (install!)
    (js/console.log
     "%c[BareDOM Trace History]%c Active — see window.BareDOM.traceHistory for the API"
     "color:#3b82f6;font-weight:bold"
     "color:inherit")))
