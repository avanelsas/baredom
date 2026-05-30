(ns baredom.components.barebuild-data.barebuild-data
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [baredom.utils.model :as mu]
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

;; ── State transitions (the epochal change-guard: one event per transition) ──────
(defn- same-state?
  "Shallow value-equality for two JS state objects. (We can't use `not=`/`=`: on
  JS objects those are identity, so two distinct-but-equal `loading` objects would
  read as different and re-fire the event.) `data` is compared by reference — a
  fresh fetch yields a new array, which is a real new value worth an event."
  [^js a ^js b]
  (and (some? a) (some? b)
       (= (.-phase a) (.-phase b))
       (identical? (.-data a) (.-data b))
       (= (.-error a) (.-error b))
       (= (.-httpStatus a) (.-httpStatus b))))

(defn- set-state!
  "Cache `new-state` and dispatch `barebuild-data-state` ONLY when it differs from
  the current value (shallow). A refetch while already `loading` re-enters with an
  equal `loading` object, so no duplicate event fires — every transition on the
  fetch lifecycle path emits exactly one event, never a duplicate. The idle resets
  in connected!/disconnected! bypass this path (a direct write): they re-establish
  a resting value, not a fetch-lifecycle transition, and emit no event."
  [^js el ^js new-state]
  (when-not (same-state? new-state (du/getv el k-state))
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

(defn- settle-ok-body! [^js el ^js ctrl ^js resp status]
  ;; Resolve the body as text first, not via `(.json resp)` directly: a successful
  ;; response with NO body (HTTP 204/205, or a Content-Length: 0 200) makes `.json`
  ;; reject — surfacing a *success* as an `:error` \"Invalid JSON\". An empty (or
  ;; whitespace-only) body is a load with no value → loaded(nil). A non-empty body
  ;; that fails to parse is still a genuine error (JSON.parse throws → .catch).
  (-> (.text resp)
      (.then  (fn on-text [^js text]
                (settle! el ctrl
                         (if (pos? (.-length (.trim text)))
                           (model/loaded-state (js/JSON.parse text) status)
                           (model/loaded-state nil status)))))
      (.catch (fn on-parse-error [^js err]
                (settle! el ctrl (model/error-state (str "Invalid JSON: " (.-message err)) status))))))

(defn- handle-response! [^js el ^js ctrl ^js resp]
  (let [status (.-status resp)]
    (if (.-ok resp)
      (settle-ok-body! el ctrl resp status)
      (settle! el ctrl (model/error-state (str "HTTP " status) status)))))

(defn- handle-network-error! [^js el ^js ctrl ^js err]
  ;; An aborted fetch rejects with AbortError — that is an intentional teardown,
  ;; not an error state. Ignore it (settle! would no-op anyway: ctrl is stale).
  (when-not (= "AbortError" (.-name err))
    (settle! el ctrl (model/error-state (.-message err) nil))))

(defn- fetch!
  "Read `src`: when present, abort any in-flight request and start a fresh fetch;
  when absent/blank, abort and fall back to idle so a removed `src` cannot leave
  a stale `:loaded` value published. A read is an observation in time: re-setting
  `src` (even to the same value) or dispatching refresh re-reads."
  [^js el]
  (let [raw (du/get-attr el model/attr-src)
        ;; Trim first: a whitespace-only `src` (" ") is blank, not a URL — without
        ;; this it passes `non-empty-string?` and fetches the page itself. Trimming
        ;; also strips accidental surrounding whitespace from a real URL.
        src (when (string? raw) (.trim raw))]
    ;; Aborting any in-flight request is unconditional — only what happens next
    ;; (start a fresh fetch vs. fall back to idle) depends on `src`.
    (abort-inflight! el)
    (if (mu/non-empty-string? src)
      (let [ctrl   (js/AbortController.)
            signal (.-signal ctrl)]
        (du/setv-untraced! el k-abort ctrl)
        (set-state! el (model/loading-state))
        (-> (js/fetch src #js {:signal signal})
            (.then  (fn on-response [^js resp] (handle-response! el ctrl resp)))
            (.catch (fn on-error [^js err] (handle-network-error! el ctrl err)))))
      (set-state! el default-idle))))

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
  ;; Seed the cache to the resting idle value (a direct write, no event — it is
  ;; the default the `.state` getter already reports). This keeps a bare connect
  ;; with no `src` silent (fetch!'s idle fallback sees idle already cached → no
  ;; transition) while a later src-removal still fires exactly one loaded→idle.
  (du/setv! el k-state default-idle)
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
