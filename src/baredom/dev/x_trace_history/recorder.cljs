(ns baredom.dev.x-trace-history.recorder
  "Effects layer for x-trace-history: state atom, hook installation,
   activation gating, JS API at window.BareDOM.traceHistory.*.

   See docs/x-trace-history-roadmap.md for the broader plan."
  (:require
   [goog.object :as gobj]
   [baredom.utils.dom :as du]
   [baredom.utils.component :as comp]
   [baredom.dev.x-trace-history.model :as model]))

;; ---------------------------------------------------------------------------
;; Constants
;; ---------------------------------------------------------------------------

(def ^:private window-base-key    "BareDOM")
(def ^:private window-api-key     "traceHistory")
(def ^:private url-param          "baredom-trace-history")
(def ^:private window-flag        "BAREDOM_TRACE_HISTORY")
(def ^:private capacity-flag      "BAREDOM_TRACE_HISTORY_CAPACITY")
(def ^:private component-id-key   "__xTraceHistoryId")

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

(defn- record!
  "Hook function passed to du/trace-hook and comp/lifecycle-hook. Receives a
   CLJS payload, derives tag + component-id from the element, builds a JS
   record, pushes onto the ring buffer, and (for new components) updates the
   :components side-index."
  [payload]
  (let [s @state]
    (when-not (:paused? s)
      (let [^js el           (:el payload)
            tag              (model/tag-of el)
            t                (.now js/performance)
            [cid new-cid?]   (resolve-component-id! el tag (:next-component-id s))
            id               (:next-id s)
            payload+         (assoc payload :tag tag :component-id cid)
            rec              (model/make-record payload+ id t)
            new-recs         (model/push-record (:records s) rec (:capacity s))
            new-comps        (if new-cid?
                               (assoc (:components s) cid {:tag tag :first-seen t})
                               (:components s))
            new-next-cid     (if new-cid? (inc cid) (:next-component-id s))]
        (swap! state assoc
               :records           new-recs
               :next-id           (inc id)
               :next-component-id new-next-cid
               :components        new-comps)))))

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

(defn pause!
  "Stop recording new events. Existing records are preserved."
  []
  (swap! state assoc :paused? true)
  nil)

(defn resume!
  "Resume recording after pause!."
  []
  (swap! state assoc :paused? false)
  nil)

(defn clear!
  "Empty the ring buffer and reset the id counter to 0."
  []
  (swap! state assoc
         :records model/empty-records
         :next-id 0)
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
   hook references to the newly-loaded `record!` symbol."
  []
  (swap! state assoc :capacity (read-capacity))
  (reset! du/trace-hook record!)
  (reset! comp/lifecycle-hook record!)
  (install-window-api!)
  nil)

(defn uninstall!
  "Deactivate recording. Used by tests; not part of the consumer API.
   Leaves the JS API in place so any console references remain valid."
  []
  (reset! du/trace-hook nil)
  (reset! comp/lifecycle-hook nil)
  nil)

;; ---------------------------------------------------------------------------
;; Activation
;; ---------------------------------------------------------------------------

(defn- enabled?
  "True iff the URL search contains ?baredom-trace-history OR the
   window.BAREDOM_TRACE_HISTORY flag is set before page load."
  []
  (or (some-> (.. js/window -location -search) (.includes url-param))
      (true? (gobj/get js/window window-flag))))

(defn register!
  "Called once at app start. If activated, install recording and log a
   console banner. Mirrors x-debug's opt-in pattern."
  []
  (when (enabled?)
    (install!)
    (js/console.log
     "%c[BareDOM Trace History]%c Active — call window.BareDOM.traceHistory.records() to inspect"
     "color:#3b82f6;font-weight:bold"
     "color:inherit"))
  nil)
