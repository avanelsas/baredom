(ns baredom.components.barebuild-route.barebuild-route
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [baredom.utils.model :as mu]
   [baredom.components.barebuild-route.model :as model]
   [baredom.components.barebuild-router.model :as router-model]))

;; Effect shell for barebuild-route. Passive child: it registers with its
;; ancestor router by a bubbling event, is subscribed by being dispatched *at*,
;; and owns ITS OWN visibility. It never walks up the DOM to find the router and
;; holds no reference to it — registration flows up by event, the match flows
;; down by targeted dispatch. The slotted body is built once at connect and is
;; never destroyed across activation transitions (identity preservation): the
;; route only toggles its own `display`.

;; ── Instance-field keys ────────────────────────────────────────────────────────
(def ^:private k-initialized?   "__barebuildRouteInit")
(def ^:private k-pattern        "__barebuildRoutePattern")        ; parsed path pattern (value)
(def ^:private k-visible?       "__barebuildRouteVisible")        ; last-applied visibility (change-guard)
(def ^:private k-change-handler "__barebuildRouteChangeHandler")  ; stashed self-listener

;; ── String-literal constants ───────────────────────────────────────────────────
(def ^:private styles          ":host{display:contents}")
(def ^:private display-none    "none")
(def ^:private display-default "")   ; clears the inline override → reverts to :host{display:contents}

;; ── Shadow DOM ──────────────────────────────────────────────────────────────────
(defn- ensure-shadow! [^js el]
  (du/ensure-shadow-with-style! el styles k-initialized? true))

;; ── Visibility (the route's own effect, gated by a received value) ─────────────
(defn- set-visible!
  "Toggle the route's own display, applying only the delta (epochal change-guard).
  The router pushes a match to every route on every resolution, so without this
  guard a hidden route would re-write `display:none` (and the active one `\"\"`)
  on each event. Guarding also keeps the trace recorder's signal to real
  activation transitions, and pre-empts spurious effects if an activation event
  or transition is ever added."
  [^js el visible?]
  (when (not= (du/getv el k-visible?) visible?)
    (du/setv! el k-visible? visible?)
    (set! (.. el -style -display) (if visible? display-default display-none))))

(defn- parse-own-pattern!
  "Parse the `path` attribute into a pattern value, or `nil` when the attribute
  is absent/blank. A nil pattern is INERT: it never matches (not even `/`), so a
  route with no `path` stays hidden instead of silently becoming a catch-all for
  the root — `parse-path-pattern \"\"` and `parse-path-pattern \"/\"` both yield
  `[]`, so the absent-vs-root distinction must be drawn here, on the raw attr."
  [^js el]
  (let [raw (du/get-attr el model/attr-path)]
    (du/setv! el k-pattern
              (when (mu/non-empty-string? raw)
                (router-model/parse-path-pattern raw)))))

(defn- on-route-change [^js el ^js e]
  (let [path    (.. e -detail -path)
        pattern (du/getv el k-pattern)]
    (set-visible! el (and (some? pattern)
                          (some? (router-model/match-path pattern path))))))

;; ── Self-listener (on self, GC'd with the element; nothing foreign to tear
;; down, so it is added once and never removed — reconnect re-uses it via the
;; guard in connected!, mirroring barebuild-data's refresh listener). ──────────
(defn- add-self-listener! [^js el]
  (let [h (fn route-on-change [e] (on-route-change el e))]
    (du/setv! el k-change-handler h)
    (.addEventListener el model/event-route-change h)))

;; ── Registration ────────────────────────────────────────────────────────────────
(defn- dispatch-mounted!
  "Announce registration to the ancestor router, carrying this route's
  already-parsed pattern so the router caches it instead of re-parsing the
  `path` string on every resolution."
  [^js el]
  (du/dispatch! el model/event-route-mounted #js {:pattern (du/getv el k-pattern)}))

;; ── Lifecycle ─────────────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-shadow! el)
  (parse-own-pattern! el)
  ;; Start hidden; the router reveals the matching route via the match it pushes
  ;; on registration. No all-routes-visible flash before the first resolution.
  (set-visible! el false)
  (when-not (du/getv el k-change-handler)
    (add-self-listener! el))
  (dispatch-mounted! el))

(defn- disconnected! [^js el]
  ;; The change listener is on self and GC'd with the element — nothing to tear
  ;; down. Just announce departure (the router can't hear this — see the route's
  ;; docstring — but a direct observer can).
  (du/dispatch! el model/event-route-unmounted #js {}))

(defn- attribute-changed! [^js el _name old-val new-val]
  (when (and (not= old-val new-val) (du/initialized? el k-initialized?))
    (parse-own-pattern! el)
    ;; Re-register with the new pattern: the router refreshes its cached pattern
    ;; for this route and re-resolves, pushing the current match back so visibility
    ;; reflects the new pattern.
    (dispatch-mounted! el)))

;; ── Property accessors ───────────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

;; ── Registration ──────────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
