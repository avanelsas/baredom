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

(def ^:private window-base-key   "BareDOM")
(def ^:private window-api-key    "traceHistory")
(def ^:private url-param         "baredom-trace-history")
(def ^:private window-flag       "BAREDOM_TRACE_HISTORY")
(def ^:private capacity-flag     "BAREDOM_TRACE_HISTORY_CAPACITY")

;; ---------------------------------------------------------------------------
;; State
;; ---------------------------------------------------------------------------

(defonce ^:private state
  (atom {:records  model/empty-records
         :next-id  0
         :paused?  false
         :capacity model/default-capacity}))

;; ---------------------------------------------------------------------------
;; Hook implementation
;; ---------------------------------------------------------------------------

(defn- record!
  "Hook function passed to du/trace-hook. Receives a CLJS payload from a
   du dispatch helper, derives the tag from the element, builds a JS record,
   and pushes onto the ring buffer."
  [payload]
  (let [s @state]
    (when-not (:paused? s)
      (let [tag        (model/tag-of (:el payload))
            payload+   (assoc payload :tag tag)
            id         (:next-id s)
            t          (.now js/performance)
            rec        (model/make-record payload+ id t)
            new-recs   (model/push-record (:records s) rec (:capacity s))]
        (swap! state assoc
               :records new-recs
               :next-id (inc id))))))

;; ---------------------------------------------------------------------------
;; Public API (exposed via window.BareDOM.traceHistory)
;; ---------------------------------------------------------------------------

(defn records
  "Returns a JS Array of all recorded events, oldest first."
  []
  (clj->js (vec (:records @state))))

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
                  "records" records
                  "pause"   pause!
                  "resume"  resume!
                  "clear"   clear!)]
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
