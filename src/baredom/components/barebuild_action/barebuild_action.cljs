(ns baredom.components.barebuild-action.barebuild-action
  (:require
   [clojure.string :as str]
   [goog.object :as gobj]
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [baredom.utils.model :as mu]
   [baredom.components.barebuild-action.model :as model]))

;; Effect shell for barebuild-action. Owns no authoritative state — the server
;; does. It listens for the configured `submit-event` on its HOST (catching
;; bubbling events from any descendant emitter — containment, no selectors),
;; fetches, caches the resulting state value (a plain JS object), and dispatches
;; `barebuild-action-state {:name :state}` on every phase transition. No querySelector.

;; ── Instance-field keys ────────────────────────────────────────────────────────
(def ^:private k-initialized?   "__barebuildActionInit")
(def ^:private k-state          "__barebuildActionState")           ; cached JS state object
;; k-abort holds the in-flight AbortController, written via du/setv-untraced!: an
;; opaque non-cloneable handle with no diagnostic display value; the meaningful
;; lifecycle (submitting→success/error) is already traced via k-state + the event.
(def ^:private k-abort          "__barebuildActionAbort")           ; in-flight AbortController (untraced)
(def ^:private k-submit-handler "__barebuildActionSubmitHandler")   ; stable host listener
(def ^:private k-bound-event    "__barebuildActionBoundEvent")      ; submit-event name currently bound

;; Shared resting value so pre-connect `.state` reads return a stable reference.
;; Frozen (every constructor freezes), so this singleton cannot be mutated.
(def ^:private default-idle (model/idle-state))

(def ^:private styles ":host{display:none}")

;; ── Shadow DOM (non-visual; renders nothing) ────────────────────────────────────
(defn- ensure-shadow! [^js el]
  (du/ensure-shadow-with-style! el styles k-initialized? false))

;; ── State transitions (one event per transition) ────────────────────────────────
(defn- same-state?
  "Shallow value-equality for two JS state objects (JS = is identity, so two
  distinct-but-equal objects would re-fire). `response` is compared by reference."
  [^js a ^js b]
  (and (some? a) (some? b)
       (= (.-phase a) (.-phase b))
       (identical? (.-response a) (.-response b))
       (= (.-error a) (.-error b))
       (= (.-httpStatus a) (.-httpStatus b))))

(defn- set-state!
  "Cache `new-state` and dispatch `barebuild-action-state` only when it differs from
  the current value (shallow). `name` echoes the action's `name` attribute at the
  detail top level so matchers (`<barebuild-invalidate-on>`) need not traverse state."
  [^js el ^js new-state]
  (when-not (same-state? new-state (du/getv el k-state))
    (du/setv! el k-state new-state)
    (du/dispatch! el model/event-action-state
                  #js {:name (du/get-attr el model/attr-name) :state new-state})))

;; ── Fetch lifecycle ─────────────────────────────────────────────────────────────
(defn- abort-inflight! [^js el]
  (when-let [^js ctrl (du/getv el k-abort)]
    (.abort ctrl)
    (du/setv-untraced! el k-abort nil)))

(defn- settle!
  "Apply a terminal state, but only if `ctrl` is still the active request. A stale
  callback (superseded by a newer submit) is a no-op, so an aborted-but-resolved
  response can never overwrite fresher state."
  [^js el ^js ctrl new-state]
  (when (identical? ctrl (du/getv el k-abort))
    (du/setv-untraced! el k-abort nil)
    (set-state! el new-state)))

(defn- settle-ok-body!
  "Resolve the body as text first (a 204/empty body makes `.json` reject — that is a
  success with no value → success(nil)); a non-empty body that fails to parse is a
  genuine error."
  [^js el ^js ctrl ^js resp status]
  (-> (.text resp)
      (.then  (fn on-text [^js text]
                (settle! el ctrl
                         (model/success-state
                          (when (pos? (.-length (.trim text))) (js/JSON.parse text))
                          status))))
      (.catch (fn on-parse-error [^js err]
                (settle! el ctrl (model/error-state (str "Invalid JSON: " (.-message err)) status))))))

(defn- handle-response! [^js el ^js ctrl ^js resp]
  (let [status (.-status resp)]
    (if (.-ok resp)
      (settle-ok-body! el ctrl resp status)
      (settle! el ctrl (model/error-state (str "HTTP " status) status)))))

(defn- handle-network-error! [^js el ^js ctrl ^js err]
  ;; An aborted fetch rejects with AbortError — intentional teardown, not an error.
  (when-not (= "AbortError" (.-name err))
    (settle! el ctrl (model/error-state (.-message err) nil))))

(defn- do-submit!
  "Abort any in-flight request and POST/PUT/… `values` (JSON) to `action`. A blank
  `action` logs and no-ops (never an empty-target request)."
  [^js el values]
  (let [action (du/get-attr el model/attr-action)
        method (let [m (du/get-attr el model/attr-method)]
                 (if (mu/non-empty-string? m) m model/default-method))]
    (if-not (mu/non-empty-string? action)
      (js/console.error "barebuild-action: missing `action`; skipping submit")
      (let [ctrl   (js/AbortController.)
            signal (.-signal ctrl)]
        (abort-inflight! el)
        (du/setv-untraced! el k-abort ctrl)
        (set-state! el (model/submitting-state))
        (-> (js/fetch action #js {:method  method
                                  :headers #js {"Content-Type" "application/json"}
                                  :body    (js/JSON.stringify values)
                                  :signal  signal})
            (.then  (fn on-response [^js resp] (handle-response! el ctrl resp)))
            (.catch (fn on-error [^js err] (handle-network-error! el ctrl err))))))))

;; ── Submit-event wiring (containment; no selectors) ──────────────────────────────
(defn- parse-path
  "Parse a values-path attribute like \"[:values]\" or \"[\\\"a\\\" \\\"b\\\"]\" into a
  vector of string keys, WITHOUT cljs.reader (which would bloat this per-component ESM
  module by ~40 KB). Strips the surrounding brackets, splits on whitespace, and trims
  a leading `:` and surrounding quotes from each token. nil for a blank path."
  [raw]
  (let [inner (-> raw (str/replace #"^\s*\[" "") (str/replace #"\]\s*$" "") str/trim)]
    (when-not (str/blank? inner)
      (mapv (fn [tok] (-> tok (str/replace #"^[:\"']+" "") (str/replace #"[\"']+$" "")))
            (str/split inner #"\s+")))))

(defn- resolve-values
  "Read the values map out of `detail` at `path-attr` (literal vector, default
  [:values]), walking the JS object by string key — so `:values` resolves
  `detail.values`. Returns nil on a blank path or a miss."
  [^js detail path-attr]
  (when (some? detail)
    (let [raw  (if (mu/non-empty-string? path-attr) path-attr model/default-values-path)
          keys (parse-path raw)]
      (when (seq keys)
        (apply gobj/getValueByKeys detail keys)))))

(defn- on-submit-event [^js el ^js ev]
  (.preventDefault ev)
  (let [values (resolve-values (.-detail ev) (du/get-attr el model/attr-values-path))]
    (if (some? values)
      (do-submit! el values)
      (js/console.error "barebuild-action: no values at values-path; skipping (no empty-body request)"))))

(defn- ensure-handler! [^js el]
  (or (du/getv el k-submit-handler)
      (let [h (fn action-on-submit [^js ev] (on-submit-event el ev))]
        (du/setv! el k-submit-handler h)
        h)))

(defn- detach-listener! [^js el]
  (let [^js h (du/getv el k-submit-handler)
        bound (du/getv el k-bound-event)]
    (when (and h (mu/non-empty-string? bound))
      (.removeEventListener el bound h)
      (du/setv! el k-bound-event nil))))

(defn- bind-submit!
  "(Re)bind the host listener to the current `submit-event` name. Required: a missing
  `submit-event` logs an error and leaves the action inert (never fetches)."
  [^js el]
  (detach-listener! el)
  (let [evt (du/get-attr el model/attr-submit-event)]
    (if-not (mu/non-empty-string? evt)
      (js/console.error "barebuild-action: missing required `submit-event`; the action will not fetch")
      (do (.addEventListener el evt (ensure-handler! el))
          (du/setv! el k-bound-event evt)))))

;; ── Lifecycle ─────────────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-shadow! el)
  (du/setv! el k-state default-idle)
  (bind-submit! el))

(defn- disconnected! [^js el]
  (detach-listener! el)
  (abort-inflight! el)
  (du/setv! el k-state default-idle))

(defn- attribute-changed! [^js el name _old-val _new-val]
  ;; Only `submit-event` needs a side effect (rebind the listener). The upgrade's
  ;; initial attribute callback fires before connectedCallback, so connected! owns
  ;; the first bind.
  (when (and (du/initialized? el k-initialized?) (.-isConnected el)
             (= name model/attr-submit-event))
    (bind-submit! el)))

;; ── Property accessors ───────────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api)   ; string reflectors; skips readonly :state
  (du/define-readonly-prop! proto "state"
                            (fn ba-get-state [^js this] (or (du/getv this k-state) default-idle)))
  (aset proto "submit" (fn ba-submit [values] (this-as ^js el (do-submit! el values)))))

;; ── Registration ──────────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
