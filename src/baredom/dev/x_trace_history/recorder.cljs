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
         :components        {}}))

(defonce ^:private subscribers (atom {}))

(defonce ^:private next-sub-token (atom 0))

(defonce ^:private notify-pending? (atom false))

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

;; ---------------------------------------------------------------------------
;; Subscriber API
;; ---------------------------------------------------------------------------

(defn- fire-subscribers!
  "Invoked from rAF. Reset the pending flag and call every subscriber. A
   throwing subscriber must not block the others or break the recorder."
  []
  (reset! notify-pending? false)
  (doseq [[_ f] @subscribers]
    (try (f) (catch :default _ nil))))

(defn- schedule-notify!
  "Coalesce subscriber notifications to one per animation frame. Multiple
   pushes within a single frame trigger a single rAF callback."
  []
  (when (compare-and-set! notify-pending? false true)
    (js/requestAnimationFrame fire-subscribers!)))

(defn subscribe!
  "Register f as a no-arg callback fired on the next animation frame after
   any record is pushed (or after pause! / resume! / clear!). Returns an
   opaque token to pass to unsubscribe!. Safe to call before install!."
  [f]
  (let [tok (swap! next-sub-token inc)]
    (swap! subscribers assoc tok f)
    tok))

(defn unsubscribe!
  "Remove the subscriber identified by `tok`. Idempotent: calling with an
   unknown token is a no-op."
  [tok]
  (swap! subscribers dissoc tok)
  nil)

;; ---------------------------------------------------------------------------
;; Hook implementation
;; ---------------------------------------------------------------------------

(defn- resolve-component-id!
  "Get-or-assign a stable component-id stored on the element via gobj. Returns
   [id new?] where id is nil for non-element targets (e.g. js/document).

   Side effect: when the element has no id yet, writes one to it.
   The id survives disconnect — a component removed and re-added keeps its id."
  [^js el tag next-id]
  (cond
    (= "document" tag)
    [nil false]

    :else
    (let [existing (gobj/get el component-id-key)]
      (if (some? existing)
        [existing false]
        (do
          (gobj/set el component-id-key next-id)
          [next-id true])))))

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
   on mount."
  [^js el]
  (gobj/set el internal-host-marker true)
  nil)

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

(defn- inside-internal-host?
  "True when `el` is a descendant of any element bearing the
   internal-host-marker, traversing across (open) shadow boundaries.
   Returns false for nil, document, and elements in the light tree of
   the document. The marker on `el` itself does NOT count — we ask
   whether `el` is *inside* a marked host, not whether it IS one."
  [^js el]
  (loop [^js node el]
    (cond
      (or (nil? node) (identical? node js/document))
      false

      :else
      (let [^js root (.getRootNode node)]
        (cond
          (identical? root js/document) false

          (instance? js/ShadowRoot root)
          (let [^js host (.-host root)]
            (if (gobj/get host internal-host-marker)
              true
              (recur host)))

          :else false)))))

(defn- push-record-with-id!
  "Build and push a record with a pre-determined id and timestamp. Reads
   `(peek @cause-stack)` to stamp the record's causeId. Updates the
   :components side-index when a new component is observed.

   Used by both the auto-id path (record!) and the wrapper path
   (record-dispatch!).

   Skips when the payload's :el is inside an internal-marked host —
   catches setv! / set-attr! / lifecycle records from components nested
   inside the dock."
  [payload id t]
  (let [s @state
        ^js el (:el payload)]
    (when-not (or (:paused? s) (inside-internal-host? el))
      (let [tag              (model/tag-of el)
            [cid new-cid?]   (resolve-component-id! el tag (:next-component-id s))
            cause-id         (peek @cause-stack)
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
        (schedule-notify!)))))

(def ^:private wrapper-handled-types
  "Payload types whose recording is handled by the dispatch-wrapper itself.
   When the wrapper is active these are gated out of record! to avoid
   double-recording — the wrapper produces them with the correct
   reserved id and entry timestamp."
  #{:event/dispatch :event/dispatch-cancelable :event/dispatch-document})

(defn- record!
  "Hook function passed to du/trace-hook and comp/lifecycle-hook. Receives a
   CLJS payload, reserves an id, and pushes a record onto the ring buffer.

   Skipped under any of these conditions, BEFORE id reservation so
   internal activity does not consume numeric ids that user-facing
   records would otherwise occupy:
   - inside an active with-suppressed-recording! scope (dock setup)
   - dispatch-wrapper handles :event/* itself (gated to avoid double-record)
   - payload's :el is inside a marked internal host (PR-A boundary)"
  [payload]
  (let [type (:type payload)
        ^js el (:el payload)]
    (when-not (or (pos? @suppression-depth)
                  (and @wrapper-active?
                       (contains? wrapper-handled-types type))
                  (inside-internal-host? el))
      (let [id (reserve-id!)
            t  (.now js/performance)]
        (push-record-with-id! payload id t)))))

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
   it reflects the final value."
  [^js this ^js ev reserved-id entry-t]
  (let [type (classify-dispatch this ev)
        payload {:type               type
                 :el                 this
                 :event-name         (.-type ev)
                 :detail             (.-detail ev)
                 :cancelable?        (.-cancelable ev)
                 :default-prevented? (.-defaultPrevented ev)}]
    (push-record-with-id! payload reserved-id entry-t)))

(defn- wrapped-dispatch
  "Body of the dispatchEvent wrapper. When wrapper-active? and not paused:
     1. Reserve an id and capture entry-time.
     2. Push the reserved id onto the cause stack so any records
        produced by handlers stamp it as their causeId.
     3. Call the original dispatchEvent (handlers run synchronously).
     4. Pop the cause stack (in finally so a throwing handler still
        unwinds cleanly).
     5. For CustomEvents, push the dispatch's own record with the
        reserved id and entry-time. Native browser events (Pointer/
        Mouse/Keyboard/etc.) are NOT recorded — only their cause
        frames are tracked, since recording every browser event would
        flood the buffer.

   When inactive or paused, pass straight through to the original."
  [^js this ^js ev]
  (let [^js orig @original-dispatch-event]
    (if (or (not @wrapper-active?)
            (:paused? @state)
            (pos? @suppression-depth)
            (inside-internal-host? this))
      ;; Inactive, paused, or dock-internal dispatch: no id reservation,
      ;; no cause-frame push, no record. Internal dispatches must NOT
      ;; establish a cause frame — otherwise records produced after the
      ;; dispatch (in the same call stack but originating from outside
      ;; the dock) would wrongly stamp the dock dispatch's reserved id.
      (.call orig this ev)
      (let [reserved-id (reserve-id!)
            entry-t     (.now js/performance)
            _           (swap! cause-stack conj reserved-id)
            result      (try
                          (.call orig this ev)
                          (finally
                            (swap! cause-stack pop)))]
        (when (instance? js/CustomEvent ev)
          (try
            (push-dispatch-record! this ev reserved-id entry-t)
            (catch :default _ nil)))
        result))))

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
  "Returns a JS Array of all recorded events, oldest first."
  []
  (clj->js (vec (:records @state))))

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

(defn paused?
  "Returns true iff recording is currently paused."
  []
  (:paused? @state))

(defn pause!
  "Stop recording new events. Existing records are preserved."
  []
  (swap! state assoc :paused? true)
  (schedule-notify!)
  nil)

(defn resume!
  "Resume recording after pause!."
  []
  (swap! state assoc :paused? false)
  (schedule-notify!)
  nil)

(defn clear!
  "Empty the ring buffer and reset the record-id counter to 0.

   Intentionally preserves :paused? state, the :components index, and the
   component-id counter — component identity is monotonic for the page
   lifetime since live elements still carry their stored cids; resetting
   would cause new components to collide with already-stored ids."
  []
  (swap! state assoc
         :records model/empty-records
         :next-id 0)
  (schedule-notify!)
  nil)

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
                  "records"    records
                  "components" components
                  "pause"      pause!
                  "resume"     resume!
                  "clear"      clear!)]
    (gobj/set base window-api-key api)
    (gobj/set js/window window-base-key base)))

(defn install!
  "Activate recording. Idempotent and re-entrant: safe to call multiple
   times. Re-runs every time so hot-reloads of the recorder ns refresh the
   hook references to the newly-loaded `record!` symbol.

   Resets `suppression-depth` to 0 so a hot-reload that interrupts a
   `with-suppressed-recording!` body cannot leave the counter stuck
   above zero (which would silently disable all recording until the
   page reloads)."
  []
  (swap! state assoc :capacity (read-capacity))
  (reset! du/trace-hook record!)
  (reset! comp/lifecycle-hook record!)
  (reset! suppression-depth 0)
  (install-dispatch-wrapper-once!)
  (reset! wrapper-active? true)
  (install-window-api!)
  nil)

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
  (reset! cause-stack [])
  nil)

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
     "color:inherit"))
  nil)
