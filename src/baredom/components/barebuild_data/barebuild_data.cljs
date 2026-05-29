(ns baredom.components.barebuild-data.barebuild-data
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [baredom.utils.model :as mu]
   [baredom.components.barebuild-data.model :as model]))

;; Effect shell for barebuild-data. The element owns no authoritative state —
;; the server does. It fetches `src`, caches the resulting state VALUE, and
;; dispatches `barebuild-data-state {:state}` on every phase transition. The
;; cached map is returned by reference from `.state` (identical? across reads
;; between transitions — no per-read allocation). Does not write to any child.

;; ── Instance-field keys ────────────────────────────────────────────────────────
(def ^:private k-initialized?   "__barebuildDataInit")
(def ^:private k-state          "__barebuildDataState")          ; cached CLJS state map
;; k-abort holds the in-flight AbortController and is written via du/setv-untraced!
;; (sites below). The value is an opaque, non-cloneable controller with no
;; diagnostic display value; the meaningful fetch lifecycle (idle→loading→loaded/
;; error) is already on the trace recorder via k-state + the dispatched
;; barebuild-data-state event, so tracing this handle would add noise, not signal.
(def ^:private k-abort          "__barebuildDataAbort")          ; in-flight AbortController (untraced)
(def ^:private k-refresh-handler "__barebuildDataRefreshHandler") ; stashed self-listener

;; Shared resting value so pre-connect `.state` reads return a stable reference.
(def ^:private default-idle (model/idle-state))

(def ^:private styles ":host{display:none}")

;; ── Shadow DOM (non-visual; renders nothing) ────────────────────────────────────
(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")]
    (set! (.-textContent style) styles)
    (.appendChild root style)
    (du/mark-initialized! el k-initialized?)))

(defn- ensure-shadow! [^js el]
  (when-not (du/initialized? el k-initialized?)
    (init-dom! el)))

;; ── State transitions (the epochal change-guard: one event per transition) ──────
(defn- set-state!
  "Cache `new-state` and dispatch `barebuild-data-state` ONLY when it differs
  from the current value. A refetch while already `:loading` re-enters with the
  same `:loading` value, so no duplicate event fires — every transition *on the
  fetch lifecycle path* emits exactly one event, and never a duplicate.
  The idle resets in connected!/disconnected! deliberately bypass this path:
  they re-establish a resting value, not a fetch-lifecycle transition, and so
  emit no event (a disconnected element has no listeners to inform anyway)."
  [^js el new-state]
  (when (not= (du/getv el k-state) new-state)
    (du/setv! el k-state new-state)
    (du/dispatch! el model/event-data-state #js {:state new-state})))

;; ── Fetch lifecycle ─────────────────────────────────────────────────────────────
(defn- abort-inflight! [^js el]
  (when-let [^js ctrl (du/getv el k-abort)]
    (.abort ctrl)
    (du/setv-untraced! el k-abort nil)))

(defn- settle!
  "Apply a terminal state, but only if `ctrl` is still the active fetch. A stale
  callback (its fetch was aborted/superseded by a newer one) is a no-op, so an
  aborted-but-already-resolved response can never overwrite fresher state."
  [^js el ^js ctrl new-state]
  (when (identical? ctrl (du/getv el k-abort))
    (du/setv-untraced! el k-abort nil)
    (set-state! el new-state)))

(defn- handle-response! [^js el ^js ctrl ^js resp]
  (let [status (.-status resp)]
    (if (.-ok resp)
      (-> (.json resp)
          (.then  (fn on-json [data]
                    (settle! el ctrl (model/loaded-state data status))))
          (.catch (fn on-parse-error [^js err]
                    (settle! el ctrl (model/error-state (str "Invalid JSON: " (.-message err)) status)))))
      (settle! el ctrl (model/error-state (str "HTTP " status) status)))))

(defn- handle-network-error! [^js el ^js ctrl ^js err]
  ;; An aborted fetch rejects with AbortError — that is an intentional teardown,
  ;; not an error state. Ignore it (settle! would no-op anyway: ctrl is stale).
  (when-not (= "AbortError" (.-name err))
    (settle! el ctrl (model/error-state (.-message err) nil))))

(defn- fetch!
  "Fetch `src` if present and the element is connected. Aborts any in-flight
  request first. A read is an observation in time: re-setting `src` (even to the
  same value) or dispatching refresh re-reads."
  [^js el]
  (let [src (du/get-attr el model/attr-src)]
    (when (mu/non-empty-string? src)
      (abort-inflight! el)
      (let [ctrl   (js/AbortController.)
            signal (.-signal ctrl)]
        (du/setv-untraced! el k-abort ctrl)
        (set-state! el (model/loading-state))
        (-> (js/fetch src #js {:signal signal})
            (.then  (fn on-response [^js resp] (handle-response! el ctrl resp)))
            (.catch (fn on-error [^js err] (handle-network-error! el ctrl err))))))))

;; ── Refresh listener (on self; GC'd with the element) ───────────────────────────
(defn- add-refresh-listener! [^js el]
  (let [h (fn data-on-refresh [_e] (fetch! el))]
    (du/setv! el k-refresh-handler h)
    (.addEventListener el model/event-data-refresh h)))

;; ── Lifecycle ─────────────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-shadow! el)
  (when-not (du/getv el k-refresh-handler)
    (add-refresh-listener! el))
  ;; No explicit idle seed: the `.state` getter defaults a nil cache to
  ;; `default-idle`, and the first `set-state!` transition fires regardless of
  ;; whether the prior cached value was nil or idle. disconnected! owns the
  ;; loaded→idle reset that actually matters (no stale replay on reconnect).
  (fetch! el))                                   ; re-fetch on (re)connect when src present

(defn- disconnected! [^js el]
  ;; The element owns no cache — the server is the truth. Abort and reset to idle;
  ;; reconnect re-fetches rather than replaying the stale :loaded value.
  (abort-inflight! el)
  (du/setv! el k-state default-idle))

(defn- attribute-changed! [^js el _name _old-val _new-val]
  ;; No value-change guard: re-setting `src` is an explicit "read now" request.
  ;; Guarded only by readiness — the upgrade's initial attribute callback fires
  ;; before connectedCallback (init not yet done), so connected! owns the first read.
  (when (and (du/initialized? el k-initialized?) (.-isConnected el))
    (fetch! el)))

;; ── Property accessors ───────────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api)   ; installs :src; skips readonly :state
  (.defineProperty js/Object proto "state"
                   #js {:get          (fn bd-get-state []
                                         (this-as ^js this (or (du/getv this k-state) default-idle)))
                        :enumerable   true
                        :configurable true}))

;; ── Registration ──────────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
