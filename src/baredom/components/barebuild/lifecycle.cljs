(ns baredom.components.barebuild.lifecycle
  (:require
   [baredom.utils.dom :as du]))

;; Shared fetch + state-transition lifecycle for the BareBuild server-state
;; brokers — barebuild-data (read side) and barebuild-action (write side). Both
;; own NO authoritative state: they fetch a URL, cache the resulting plain-JS
;; state object, and dispatch ONE CustomEvent per phase transition. The two
;; differ only in what `cfg` supplies; everything else here — the
;; abort-controller stale-callback guard, the 204/empty-body → success(nil)
;; parse, AbortError suppression, and the shallow value-equality change guard —
;; is identical and lives here so the read and write sides cannot drift.
;;
;; `cfg` keys:
;;   :k-state     instance-field key caching the state object
;;   :k-abort     instance-field key holding the in-flight AbortController
;;   :payload-fn  (fn [^js state]) → the by-REFERENCE payload field (.-data / .-response)
;;   :event-name  the *-state event dispatched on every transition
;;   :detail-fn   (fn [^js el ^js state]) → the event-detail JS object
;;   :success-fn  (fn [value status])      → the success/loaded state constructor
;;   :error-fn    (fn [message status])    → the error state constructor

(defn same-state?
  "Shallow value-equality for two JS state objects. JS `=`/`not=` are identity,
  so two distinct-but-equal objects would re-fire the event; compare field by
  field instead. The payload field (the fetched body — `.-data` on the read side,
  `.-response` on the write side, supplied as the `payload-fn` accessor) is
  compared by REFERENCE: a fresh fetch yields a new value worth an event.
  `payload-fn` is a direct field accessor, NOT a string key — so the field name
  stays extern-inferred and co-located with the constructor that stamps it, rather
  than a loose string that could silently mis-address (both sides → undefined →
  wrongly equal → dedup breaks quietly)."
  [^js a ^js b payload-fn]
  (and (some? a) (some? b)
       (= (.-phase a) (.-phase b))
       (identical? (payload-fn a) (payload-fn b))
       (= (.-error a) (.-error b))
       (= (.-httpStatus a) (.-httpStatus b))))

(defn set-state!
  "Cache `new-state` and dispatch the configured event ONLY when it differs
  (shallow) from the current value — the epochal change guard: exactly one event
  per transition, never a duplicate. Idle resets that re-establish a resting
  value (not a fetch-lifecycle transition) flow through here too: an equal value
  is a no-op, a changed one fires."
  [^js el ^js new-state {:keys [k-state payload-fn event-name detail-fn]}]
  (when-not (same-state? new-state (du/getv el k-state) payload-fn)
    (du/setv! el k-state new-state)
    (du/dispatch! el event-name (detail-fn el new-state))))

(defn abort-inflight! [^js el k-abort]
  (when-let [^js ctrl (du/getv el k-abort)]
    (.abort ctrl)
    (du/setv-untraced! el k-abort nil)))

(defn- settle!
  "Apply a terminal state, but only if `ctrl` is still the active request. A
  stale callback (superseded by a newer request) is a no-op, so an
  aborted-but-resolved response can never overwrite fresher state."
  [^js el ^js ctrl new-state {:keys [k-abort] :as cfg}]
  (when (identical? ctrl (du/getv el k-abort))
    (du/setv-untraced! el k-abort nil)
    (set-state! el new-state cfg)))

(defn- settle-ok-body!
  "Resolve the body as text first: a 204/empty body makes `.json` reject — that
  is a success with no value → success(nil). A non-empty body that fails to parse
  is a genuine error."
  [^js el ^js ctrl ^js resp status {:keys [success-fn error-fn] :as cfg}]
  (-> (.text resp)
      (.then  (fn on-text [^js text]
                (settle! el ctrl
                         (success-fn (when (pos? (.-length (.trim text))) (js/JSON.parse text)) status)
                         cfg)))
      (.catch (fn on-parse-error [^js err]
                (settle! el ctrl (error-fn (str "Invalid JSON: " (.-message err)) status) cfg)))))

(defn- handle-response! [^js el ^js ctrl ^js resp {:keys [error-fn] :as cfg}]
  (let [status (.-status resp)]
    (if (.-ok resp)
      (settle-ok-body! el ctrl resp status cfg)
      (settle! el ctrl (error-fn (str "HTTP " status) status) cfg))))

(defn- handle-network-error! [^js el ^js ctrl ^js err {:keys [error-fn] :as cfg}]
  ;; An aborted fetch rejects with AbortError — intentional teardown, not an error.
  (when-not (= "AbortError" (.-name err))
    (settle! el ctrl (error-fn (.-message err) nil) cfg)))

(defn start-fetch!
  "Abort any in-flight request, publish `loading-state`, then fetch `url` with
  `init` (a JS fetch-init object) and wire the response/error handlers through the
  shared settle path. The caller builds `url`/`init` (GET vs POST/JSON-body) and
  supplies the in-flight state value to publish."
  [^js el url init loading-state {:keys [k-abort] :as cfg}]
  (let [ctrl   (js/AbortController.)
        signal (.-signal ctrl)
        ;; Merge the signal into a FRESH object — never mutate the caller's `init`
        ;; literal (it is a value, not a place: the caller can't tell from the call
        ;; site that we'd write into it). The signal is ours to add; their init stays clean.
        init*  (js/Object.assign #js {:signal signal} init)]
    (abort-inflight! el k-abort)
    (du/setv-untraced! el k-abort ctrl)
    (set-state! el loading-state cfg)
    (-> (js/fetch url init*)
        (.then  (fn on-response [^js resp] (handle-response! el ctrl resp cfg)))
        (.catch (fn on-error [^js err] (handle-network-error! el ctrl err cfg))))))
