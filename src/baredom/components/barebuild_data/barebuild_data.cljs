(ns baredom.components.barebuild-data.barebuild-data
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [baredom.utils.model :as mu]
   [baredom.components.barebuild.lifecycle :as lifecycle]
   [baredom.components.barebuild-data.model :as model]))

;; Effect shell for barebuild-data. The element owns no authoritative state —
;; the server does. It fetches `src`, caches the resulting state VALUE (a plain
;; JS object — see model.cljs for why), and dispatches `barebuild-data-state
;; {:state}` on every phase transition. The cached object is returned by
;; reference from `.state` (identical? across reads between transitions — no
;; per-read allocation). Does not write to any child.

;; ── Instance-field keys ────────────────────────────────────────────────────────
(def ^:private k-initialized?   "__barebuildDataInit")
(def ^:private k-state          "__barebuildDataState")          ; cached JS state object
;; k-abort holds the in-flight AbortController and is written via du/setv-untraced!
;; (sites below). The value is an opaque, non-cloneable controller with no
;; diagnostic display value; the meaningful fetch lifecycle (idle→loading→loaded/
;; error) is already on the trace recorder via k-state + the dispatched
;; barebuild-data-state event, so tracing this handle would add noise, not signal.
(def ^:private k-abort          "__barebuildDataAbort")          ; in-flight AbortController (untraced)
(def ^:private k-refresh-handler "__barebuildDataRefreshHandler") ; stashed self-listener
;; k-invalidate-handler holds the document-level `barebuild-invalidate` listener.
;; Unlike the refresh handler (on self, GC'd with the element), this sits on
;; `document`, so it MUST be removed on disconnect or it leaks and keeps firing for
;; a detached broker. Added on connect, removed on disconnect.
(def ^:private k-invalidate-handler "__barebuildDataInvalidateHandler")

;; Shared resting value so pre-connect `.state` reads return a stable reference.
;; This is a SINGLETON returned by reference from every instance's `.state` idle
;; read, so it must not be mutated — a consumer corrupting it would poison the
;; resting value seen by every other broker. `idle-state` already freezes its
;; wrapper (every state constructor does — see model.cljs), so this is just the
;; one shared instance of that frozen value.
(def ^:private default-idle (model/idle-state))

(def ^:private styles ":host{display:none}")

;; ── Shadow DOM (non-visual; renders nothing) ────────────────────────────────────
(defn- ensure-shadow! [^js el]
  (du/ensure-shadow-with-style! el styles k-initialized? false))

;; ── Fetch lifecycle (shared with barebuild-action — see barebuild/lifecycle) ────
;; All the fetch/settle/change-guard machinery lives in the shared lifecycle ns so
;; the read and write sides cannot drift. This cfg names the three things the read
;; side differs in: the by-reference payload field is `data`, the dispatched event
;; is `barebuild-data-state` carrying only `{state}`, and success/error use the
;; data-side constructors.
(def ^:private lifecycle-cfg
  {:k-state     k-state
   :k-abort     k-abort
   :payload-fn  (fn data-payload [^js s] (.-data s))
   :event-name  model/event-data-state
   :detail-fn   (fn data-detail [_el ^js state] #js {:state state})
   :success-fn  model/loaded-state
   :error-fn    model/error-state})

(defn- fetch!
  "Read `src`: when present, abort any in-flight request and start a fresh fetch;
  when absent/blank, abort and fall back to idle so a removed `src` cannot leave
  a stale `:loaded` value published. A read is an observation in time: re-setting
  `src` (even to the same value) or dispatching refresh re-reads."
  [^js el]
  ;; get-attr-trimmed centralizes "whitespace = absent": a whitespace-only `src`
  ;; (" ") is blank, not a URL — untrimmed it would pass and fetch the page itself.
  (let [src (du/get-attr-trimmed el model/attr-src)]
    (if src
      (lifecycle/start-fetch! el src #js {} (model/loading-state) lifecycle-cfg)
      ;; Blank src: abort any in-flight request and fall back to idle (start-fetch!
      ;; would have aborted; here we do it explicitly since no fetch follows).
      (do (lifecycle/abort-inflight! el k-abort)
          (lifecycle/set-state! el default-idle lifecycle-cfg)))))

;; ── Refresh listener (on self; GC'd with the element) ───────────────────────────
(defn- add-refresh-listener! [^js el]
  (let [h (fn data-on-refresh [_e] (fetch! el))]
    (du/setv! el k-refresh-handler h)
    (.addEventListener el model/event-data-refresh h)))

;; ── Invalidate listener (on document; matched by URL origin + pathname + query) ──
(defn- match-key
  "The match key of `s` resolved against the page origin: `origin + pathname +
  search`, or nil for a blank/invalid src. Resolving against the origin makes a
  relative `src` and an absolute same-origin one to the same path equal; KEEPING the
  origin in the key keeps a cross-origin `https://other/api/x` distinct from the
  page's own `/api/x` (dropping it would conflate same-path different-origin
  resources). Including the query string keeps two parameterised resources on the
  same path distinct (`/items?page=1` ≠ `/items?page=2`) — exactly the equality
  `<barebuild-invalidate-on>` documents."
  [s]
  (when (mu/non-empty-string? s)
    (try (let [^js u (js/URL. s (.. js/location -origin))]
           (str (.-origin u) (.-pathname u) (.-search u)))
         (catch :default _ nil))))

(defn- on-invalidate [^js el ^js e]
  ;; A read is an observation in time: when an invalidation names our src (exact
  ;; origin+pathname+query equality), re-read. A blank/mismatched src is ignored.
  ;; `barebuild-invalidate` is a PUBLIC protocol any code may dispatch, so read the
  ;; foreign src defensively (a detail-less CustomEvent must not throw in this
  ;; document listener) — a nil src can never match a non-nil `own`.
  (let [own (match-key (du/get-attr el model/attr-src))]
    (when (and own (= own (match-key (some-> e .-detail .-src))))
      (fetch! el))))

(defn- ensure-invalidate-handler! [^js el]
  (or (du/getv el k-invalidate-handler)
      (let [h (fn data-on-invalidate [^js e] (on-invalidate el e))]
        (du/setv! el k-invalidate-handler h)
        h)))

;; ── Lifecycle ─────────────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-shadow! el)
  (when-not (du/getv el k-refresh-handler)
    (add-refresh-listener! el))
  ;; Re-add each connect (a no-op if the same listener ref is already attached);
  ;; balanced by the removeEventListener in disconnected! so it cannot leak.
  (.addEventListener js/document model/event-invalidate (ensure-invalidate-handler! el))
  ;; Seed the cache to the resting idle value (a direct write, no event — it is
  ;; the default the `.state` getter already reports). This keeps a bare connect
  ;; with no `src` silent (fetch!'s idle fallback sees idle already cached → no
  ;; transition) while a later src-removal still fires exactly one loaded→idle.
  (du/setv! el k-state default-idle)
  (fetch! el))                                   ; re-fetch on (re)connect when src present

(defn- disconnected! [^js el]
  ;; The element owns no cache — the server is the truth. Abort and reset to idle;
  ;; reconnect re-fetches rather than replaying the stale :loaded value. The idle
  ;; reset is a DELIBERATE event-less write (raw du/setv!, not set-state!): teardown
  ;; is not a fetch-lifecycle transition, so a detaching broker publishes no event.
  ;; A consumer that tracks `phase` must treat disconnect as terminal on its own.
  (lifecycle/abort-inflight! el k-abort)
  (when-let [^js h (du/getv el k-invalidate-handler)]
    (.removeEventListener js/document model/event-invalidate h))
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
  (du/define-readonly-prop! proto "state"
                            (fn bd-get-state [^js this] (or (du/getv this k-state) default-idle))))

;; ── Registration ──────────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
