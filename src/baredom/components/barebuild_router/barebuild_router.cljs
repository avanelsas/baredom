(ns baredom.components.barebuild-router.barebuild-router
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.barebuild-router.model :as model]))

;; Effect shell for barebuild-router. The element owns NO authoritative state:
;; the two truths live elsewhere (the URL in history/location; the route set in
;; the DOM). It holds only projections — a registry of [route parsed-pattern]
;; entries populated by the bubbling `mounted` event (a succession of immutable
;; values, never re-walked; validated lazily against isConnected) and the last
;; published match (a change-guard). Every decision is delegated to the pure model.
;; The router never writes an attribute or style to a route: it publishes the
;; current match by dispatching `barebuild-route-change` *at* each route handle
;; (value-passing), and each route owns its own visibility.

;; ── Instance-field keys ────────────────────────────────────────────────────────
(def ^:private k-initialized? "__barebuildRouterInit")
(def ^:private k-routes       "__barebuildRouterRoutes")   ; vector of [handle parsed-pattern] pairs
(def ^:private k-match        "__barebuildRouterMatch")    ; last published match {:path :params} (change-guard)
(def ^:private k-path         "__barebuildRouterPath")     ; cached stripped path (string)
(def ^:private k-params       "__barebuildRouterParams")   ; cached params (JS object)
(def ^:private k-handlers     "__barebuildRouterHandlers") ; stashed listener closures

;; ── String-literal constants ───────────────────────────────────────────────────
(def ^:private styles           ":host{display:contents}")
(def ^:private attr-data-route  "data-barebuild-route")
(def ^:private tag-anchor       "A")
(def ^:private tag-router-upper "BAREBUILD-ROUTER")

;; Native DOM event names (the strings passed to add/removeEventListener).
(def ^:private event-click    "click")
(def ^:private event-popstate "popstate")

;; Property keys under which each handler closure is stashed in the k-handlers
;; JS object. add-listeners! and remove-listeners! both reference these consts,
;; so the two paths can never drift on a key string.
(def ^:private handler-key-doc-click "docClick")
(def ^:private handler-key-popstate  "popstate")
(def ^:private handler-key-navigate  "navigate")
(def ^:private handler-key-mounted   "mounted")

;; ── Shadow DOM (layout-transparent; the router renders no box of its own) ───────
(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")
        slot  (.createElement js/document "slot")]
    (set! (.-textContent style) styles)
    (.appendChild root style)
    (.appendChild root slot)
    (du/mark-initialized! el k-initialized?)))

(defn- ensure-shadow! [^js el]
  (when-not (du/initialized? el k-initialized?)
    (init-dom! el)))

;; ── Route registry (immutable succession; never an in-place mutation) ──────────
;; The registry is a CACHE of DOM truth: a vector of [handle parsed-pattern] pairs.
;; Routes self-register on connect (the bubbling `mounted` event carries the route's
;; ALREADY-parsed pattern, so the router never re-parses path strings). Removal is
;; NOT signalled — `barebuild-route-unmounted` fires from disconnectedCallback, when
;; the element is detached, so it cannot bubble here. Disconnected handles are
;; skipped on read and pruned on publish; a re-mounted route refreshes its pattern.
(defn- read-entries [^js el]
  (or (du/getv el k-routes) []))

(defn- route-handles [^js el]
  (mapv first (read-entries el)))

(defn- register-route!
  "Add [route pattern], or refresh the pattern of an already-registered route
  (a runtime `path` change re-dispatches `mounted`). Order-preserving so the
  first-match precedence for `.params` is stable across re-registration."
  [^js el ^js route pattern]
  (let [entries (read-entries el)]
    (du/setv! el k-routes
              (if (some (fn [[r _]] (identical? r route)) entries)
                (mapv (fn [[r _ :as entry]] (if (identical? r route) [route pattern] entry)) entries)
                (conj entries [route pattern])))))

(defn- prune-disconnected!
  "Drop entries for routes no longer in the DOM. The active-match read already
  ignores disconnected routes, so this just keeps the cache bounded."
  [^js el]
  (let [entries (read-entries el)
        live    (filterv (fn [[^js r _]] (.-isConnected r)) entries)]
    (when (< (count live) (count entries))
      (du/setv! el k-routes live))))

;; ── Resolution ──────────────────────────────────────────────────────────────────
(defn- active-params
  "Params (CLJS map) of the first connected registered route whose cached pattern
  matches `stripped`, or nil when none match. Uses the pattern each route parsed
  for itself (carried in `mounted`) — the router never re-parses."
  [^js el stripped]
  (some (fn [[^js route pattern]]
          (when (.-isConnected route)
            (:params (model/match-path pattern stripped))))
        (read-entries el)))

(defn- push-match!
  "Dispatch the current match AT a route handle. Non-bubbling so it does not
  re-enter the router or reach external observers — value-passing, not mutation."
  [^js route ^js detail]
  (.dispatchEvent route
                  (js/CustomEvent. model/event-route-change
                                   #js {:detail detail :bubbles false :composed false})))

(defn- compute-match
  "Pure projection of (registered routes, current URL) → the match value
  `{:path :params}` (CLJS). Reads `location` + the cached patterns; no mutation."
  [^js el]
  (let [stripped (model/strip-base (du/get-attr el model/attr-base)
                                    (.. js/location -pathname))]
    {:path stripped :params (or (active-params el stripped) {})}))

(defn- cached-detail
  "Build the JS route-change detail from the cached match (.path / .params)."
  [^js el]
  #js {:path (du/getv el k-path) :params (du/getv el k-params)})

(defn- announce!
  "One bubbling dispatch on self for external observers (analytics, a title
  updater). Routes do not listen to this — they receive a targeted push."
  [^js el ^js detail]
  (du/dispatch! el model/event-route-change detail))

(defn- publish!
  "Prune dead handles, recompute the match, and — ONLY when it changed from the
  last published value — refresh the cached .path/.params and announce one
  bubbling route-change for external observers (the epochal change-guard; avoids
  spurious events and param-identity churn on same-URL popstate / no-op mounts).
  ALWAYS push the current match to `routes` so each (re)evaluates its own
  visibility — the route's own change-guard makes a redundant push a no-op."
  [^js el routes]
  (prune-disconnected! el)
  (let [match (compute-match el)]
    (when (not= match (du/getv el k-match))
      (du/setv! el k-match  match)
      (du/setv! el k-path   (:path match))
      (du/setv! el k-params (clj->js (:params match)))
      (announce! el (cached-detail el)))
    (let [detail (cached-detail el)]
      (doseq [^js route routes]
        (push-match! route detail)))))

(defn- resolve!
  "Full resolution: push the current match to EVERY registered route. Used when
  the URL changes (navigate / popstate) — any route may flip visibility."
  [^js el]
  (publish! el (route-handles el)))

;; ── Navigation ──────────────────────────────────────────────────────────────────
(defn- navigate! [^js el path]
  (when (string? path)
    (.pushState js/history nil "" path)
    (resolve! el)))

(defn- on-navigate [^js el ^js e]
  (navigate! el (.. e -detail -path)))

(defn- on-popstate [^js el _e]
  (resolve! el))

;; ── Lifecycle / registration events from child routes ──────────────────────────
(defn- on-route-mounted [^js el ^js e]
  ;; stopPropagation so an OUTER router never also registers an inner router's
  ;; route — registration stops at the nearest ancestor router.
  (.stopPropagation e)
  (let [^js route (.-target e)
        pattern   (.. e -detail -pattern)]   ; the route's already-parsed pattern
    (register-route! el route pattern)
    ;; publish! pushes the current match to ONLY the new route (the URL is
    ;; unchanged, so the others already show the right thing), and announces only
    ;; if the match actually changed. A late-mounted matching route activates on
    ;; registration without waiting for a navigation.
    (publish! el [route])))

;; ── Anchor interception (document capture) ──────────────────────────────────────
(defn- anchor-node? [^js node]
  (and (= tag-anchor (.-tagName node))
       (.hasAttribute node attr-data-route)))

(defn- router-node? [^js node]
  (= tag-router-upper (.-tagName node)))

(defn- modifier-key? [^js e]
  (or (.-metaKey e) (.-ctrlKey e) (.-shiftKey e) (.-altKey e)))

(defn- click-facts
  "Extract the facts `should-intercept?` decides on, in one pass over the
  composed path (innermost target first, ascending toward the root).
  `drop-while` skips to the marked anchor; the nearest router is then the first
  <barebuild-router> *above* it — so a router between the anchor and `el` wins
  (nearest-router scoping for nested/sibling routers)."
  [^js el ^js e]
  (let [path        (array-seq (.composedPath e))
        from-anchor (drop-while (complement anchor-node?) path)
        anchor      (first from-anchor)
        nearest     (first (filter router-node? (rest from-anchor)))]
    {:anchor                  anchor
     :anchor-present?         (some? anchor)
     :nearest-router-is-this? (identical? nearest el)
     :primary-button?         (= 0 (.-button e))
     :modifier?               (modifier-key? e)
     :default-prevented?      (.-defaultPrevented e)}))

(defn- anchor-href-path
  "The anchor's same-origin path to navigate to: pathname + query + hash. Using
  `.-pathname` alone would silently drop `?query` and `#hash` from the URL bar
  (and from `location.search`); route matching still uses the pathname only."
  [^js a]
  (str (.-pathname a) (.-search a) (.-hash a)))

(defn- on-doc-click [^js el ^js e]
  (let [facts (click-facts el e)]
    (when (model/should-intercept? facts)
      (.preventDefault e)
      (du/dispatch! el model/event-navigate
                    #js {:path (anchor-href-path (:anchor facts))}))))

;; ── Listener wiring (mixed targets: document/window/self) ───────────────────────
(defn- add-listeners! [^js el]
  (let [doc-click (fn router-doc-click [e] (on-doc-click el e))
        popstate  (fn router-popstate [e] (on-popstate el e))
        navigate  (fn router-navigate [e] (on-navigate el e))
        mounted   (fn router-mounted [e] (on-route-mounted el e))
        handlers  #js {}]
    ;; Build the stash with the same key consts the remove path reads back —
    ;; #js map literals require literal keys, so we set them explicitly here.
    ;; No `unmounted` listener: route removal cannot bubble here (detached element),
    ;; so deregistration is by isConnected pruning, not an event.
    (gobj/set handlers handler-key-doc-click doc-click)
    (gobj/set handlers handler-key-popstate  popstate)
    (gobj/set handlers handler-key-navigate  navigate)
    (gobj/set handlers handler-key-mounted   mounted)
    (du/setv! el k-handlers handlers)
    (.addEventListener js/document event-click doc-click true)   ; capture phase
    (.addEventListener js/window   event-popstate popstate)
    (.addEventListener el model/event-navigate      navigate)
    (.addEventListener el model/event-route-mounted mounted)))

(defn- remove-listeners! [^js el]
  (when-let [^js h (du/getv el k-handlers)]
    (.removeEventListener js/document event-click (gobj/get h handler-key-doc-click) true)
    (.removeEventListener js/window   event-popstate (gobj/get h handler-key-popstate))
    (.removeEventListener el model/event-navigate      (gobj/get h handler-key-navigate))
    (.removeEventListener el model/event-route-mounted (gobj/get h handler-key-mounted))
    (du/setv! el k-handlers nil)))

;; ── Property accessors ───────────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api)   ; installs :base; skips readonly :path/:params
  (.defineProperty js/Object proto "path"
                   #js {:get          (fn br-get-path [] (this-as ^js this (or (du/getv this k-path) "")))
                        :enumerable   true
                        :configurable true})
  (.defineProperty js/Object proto "params"
                   #js {:get          (fn br-get-params [] (this-as ^js this (or (du/getv this k-params) #js {})))
                        :enumerable   true
                        :configurable true}))

;; ── Lifecycle ─────────────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-shadow! el)
  (when-not (du/getv el k-handlers)
    (add-listeners! el))
  (resolve! el))

(defn- disconnected! [^js el]
  (remove-listeners! el))

(defn- attribute-changed! [^js el _name old-val new-val]
  ;; Same readiness proxy as barebuild-data: initialized (shadow built) AND
  ;; connected. The upgrade's initial attribute callback fires before
  ;; connectedCallback (not yet initialized), so connected! owns the first
  ;; resolve; and a `base` change while detached must not resolve against a
  ;; live `location`.
  (when (and (not= old-val new-val)
             (du/initialized? el k-initialized?)
             (.-isConnected el))
    (resolve! el)))

;; ── Registration ──────────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
