(ns baredom.components.barebuild-action.barebuild-action
  (:require
   [clojure.string :as str]
   [goog.object :as gobj]
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [baredom.components.barebuild.lifecycle :as lifecycle]
   [baredom.components.barebuild.listeners :as listeners]
   [baredom.components.barebuild-action.model :as model]))

;; Effect shell for barebuild-action. Owns no authoritative state — the server
;; does. It listens for the configured `submit-event` on its HOST (catching
;; bubbling events from any descendant emitter — containment, no selectors),
;; fetches, caches the resulting state value (a plain JS object), and dispatches
;; `barebuild-action-state {:name :state}` on every phase transition. Containment
;; only — it never reaches into the DOM by lookup.

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

;; The action WRAPS visible content (the submit emitter, e.g. an <x-form>) by
;; containment, so its shadow root MUST carry a <slot> (ensure-shadow! slot? = true)
;; or the wrapped content has nowhere to project and never renders — even though it
;; stays in the light DOM and functions. display:block makes the host a transparent
;; block wrapper around that slot. (invalidate-on is a childless leaf → no slot,
;; display:none.)
(def ^:private styles ":host{display:block}")

;; ── Shadow DOM (a transparent <slot> wrapper around the emitter) ─────────────────
(defn- ensure-shadow! [^js el]
  (du/ensure-shadow-with-style! el styles k-initialized? true))

;; ── Fetch lifecycle (shared with barebuild-data — see barebuild/lifecycle) ──────
;; All the fetch/settle/change-guard machinery lives in the shared lifecycle ns so
;; the write and read sides cannot drift. This cfg names the three things the write
;; side differs in: the by-reference payload field is `response`, the dispatched
;; event is `barebuild-action-state` carrying `{name, state}` (name echoed at the
;; detail top level so matchers need not traverse state), and success/error use
;; the action-side constructors.
(def ^:private lifecycle-cfg
  {:k-state     k-state
   :k-abort     k-abort
   :payload-fn  (fn action-payload [^js s] (.-response s))
   :event-name  model/event-action-state
   ;; `name` is trimmed (get-attr-trimmed) so it applies the SAME whitespace policy
   ;; as <barebuild-invalidate-on>'s `when-name` matcher (also trimmed) — otherwise a
   ;; padded name=" x " would never equal a clean when-name="x" and silently disable
   ;; name-scoped invalidation.
   :detail-fn   (fn action-detail [^js el ^js state]
                  #js {:name (du/get-attr-trimmed el model/attr-name) :state state})
   :success-fn  model/success-state
   :error-fn    model/error-state})

(defn- apply-values-transform
  "Apply the consumer-supplied `valuesTransform` (a public JS property, `(fn [values]
  → values)`) before JSON-encoding, when one is set. This is the seam for payload
  hygiene the action cannot itself know — blank-stripping (so an unset control's \"\"
  doesn't shadow a server default) or numeric coercion (a number control reports a
  string). Absent / non-function → `values` unchanged, so the default is encode-as-is."
  [^js el values]
  (let [t (.-valuesTransform el)]
    (if (fn? t) (t values) values)))

(defn- action-url
  "The submit target: the trimmed `action` URL, or nil when blank/absent (whitespace
  = absent, so an untrimmed `\" \"` can't resolve to the current page). Single source
  for 'is there a target?' — both the submit guard and the preventDefault decision
  read it, so the precondition lives in one calculation, not two."
  [^js el]
  (du/get-attr-trimmed el model/attr-action))

(defn- do-submit!
  "Abort any in-flight request and POST/PUT/… `values` (JSON) to `action`. No-ops with a
  diagnostic on a blank `action` OR a nil body — the body guard covers a `valuesTransform`
  that returns nil AND a `.submit(nil)` imperative call, so neither path can send a
  `null`-body or empty-target request. Sole owner of both guards; it is also the
  `.submit()` entry point, so the checks cannot move entirely to the event path."
  [^js el values]
  (let [action (action-url el)
        method (or (du/get-attr-trimmed el model/attr-method) model/default-method)
        body   (apply-values-transform el values)]
    (cond
      (not action) (js/console.error "barebuild-action: missing `action`; skipping submit")
      (nil? body)  (js/console.error "barebuild-action: nil body (values or valuesTransform result); skipping (no null-body request)")
      :else        (lifecycle/start-fetch! el action
                                            #js {:method  method
                                                 :headers #js {"Content-Type" "application/json"}
                                                 :body    (js/JSON.stringify body)}
                                            (model/submitting-state)
                                            lifecycle-cfg))))

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
    (let [keys (parse-path (or path-attr model/default-values-path))]
      (when (seq keys)
        (apply gobj/getValueByKeys detail keys)))))

(defn- on-submit-event [^js el ^js ev]
  ;; Claim the emitter's event (preventDefault) ONLY when we can service the submit —
  ;; otherwise a submit we skip would be silently swallowed (an emitter that branches
  ;; on defaultPrevented would wrongly conclude we handled it). Values resolution is
  ;; this path's own concern (the .submit() entry gets values explicitly), so it logs
  ;; here; the missing-action guard + diagnostic belong to do-submit! alone, so we
  ;; only gate preventDefault on `action-url` and delegate — no duplicated guard/log.
  (let [values (resolve-values (.-detail ev) (du/get-attr-trimmed el model/attr-values-path))]
    (if (nil? values)
      (js/console.error "barebuild-action: no values at values-path; skipping (no empty-body request)")
      (do (when (action-url el) (.preventDefault ev))
          (do-submit! el values)))))

;; Listener mechanics (stash/remove/rebind the host submit listener) are shared with
;; barebuild-invalidate-on via barebuild/listeners — only the target (the host `el`,
;; no :k-node) and the required `submit-event` resolution live here.
(def ^:private listener-cfg
  {:k-handler k-submit-handler :k-bound-event k-bound-event})

(defn- ensure-handler! [^js el]
  (or (du/getv el k-submit-handler)
      (let [h (fn action-on-submit [^js ev] (on-submit-event el ev))]
        (du/setv! el k-submit-handler h)
        h)))

(defn- bind-submit!
  "(Re)bind the host listener to the current `submit-event` name. Required: a missing
  `submit-event` logs an error and leaves the action inert (never fetches)."
  [^js el]
  (listeners/detach-listener! el listener-cfg)
  (let [evt (du/get-attr-trimmed el model/attr-submit-event)]
    (if-not evt
      (js/console.error "barebuild-action: missing required `submit-event`; the action will not fetch")
      (listeners/bind-listener! el el evt (ensure-handler! el) listener-cfg))))

;; ── Lifecycle ─────────────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-shadow! el)
  (du/setv! el k-state default-idle)
  (bind-submit! el))

(defn- disconnected! [^js el]
  (listeners/detach-listener! el listener-cfg)
  (lifecycle/abort-inflight! el k-abort)
  ;; DELIBERATE event-less idle reset (raw du/setv!, not set-state!): teardown is not
  ;; a submit-lifecycle transition, so a detaching action publishes no event. A
  ;; consumer driving UI off a mid-submit `submitting` phase must treat disconnect as
  ;; terminal itself (mirrors barebuild-data's disconnect contract).
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
