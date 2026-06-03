(ns baredom.components.barebuild-action.barebuild-action
  (:require
   [clojure.string :as str]
   [goog.object :as gobj]
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [baredom.components.barebuild.lifecycle :as lifecycle]
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
   :detail-fn   (fn action-detail [^js el ^js state]
                  #js {:name (du/get-attr el model/attr-name) :state state})
   :success-fn  model/success-state
   :error-fn    model/error-state})

(defn- do-submit!
  "Abort any in-flight request and POST/PUT/… `values` (JSON) to `action`. A blank
  (or whitespace-only) `action` logs and no-ops (never an empty-target request —
  an untrimmed whitespace `action` would otherwise resolve to the current page)."
  [^js el values]
  (let [action (du/get-attr-trimmed el model/attr-action)
        method (or (du/get-attr-trimmed el model/attr-method) model/default-method)]
    (if-not action
      (js/console.error "barebuild-action: missing `action`; skipping submit")
      (lifecycle/start-fetch! el action
                              #js {:method  method
                                   :headers #js {"Content-Type" "application/json"}
                                   :body    (js/JSON.stringify values)}
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
  (.preventDefault ev)
  (let [values (resolve-values (.-detail ev) (du/get-attr-trimmed el model/attr-values-path))]
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
    ;; `bound` is nil or a non-blank event name (set from get-attr-trimmed).
    (when (and h bound)
      (.removeEventListener el bound h)
      (du/setv! el k-bound-event nil))))

(defn- bind-submit!
  "(Re)bind the host listener to the current `submit-event` name. Required: a missing
  `submit-event` logs an error and leaves the action inert (never fetches)."
  [^js el]
  (detach-listener! el)
  (let [evt (du/get-attr-trimmed el model/attr-submit-event)]
    (if-not evt
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
  (lifecycle/abort-inflight! el k-abort)
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
