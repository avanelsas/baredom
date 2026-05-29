(ns baredom.components.barebuild-router.barebuild-router
  (:require
   [baredom.utils.component :as component]
   [baredom.utils.dom :as du]
   [goog.object :as gobj]
   [baredom.components.barebuild-router.model :as model]
   [baredom.components.barebuild-route.model :as route-model]))

;; Effect shell for barebuild-router. The element owns NO authoritative state:
;; the two truths live elsewhere (the URL in history/location; the route set in
;; the DOM). It holds only projections — a registry of route handles kept current
;; by mounted/unmounted events (a succession of immutable values, never re-walked)
;; and the last computed match. Every decision is delegated to the pure model.
;; The router never writes an attribute or style to a route: it publishes the
;; current match by dispatching `barebuild-route-change` *at* each route handle
;; (value-passing), and each route owns its own visibility.

;; ── Instance-field keys ────────────────────────────────────────────────────────
(def ^:private k-initialized? "__barebuildRouterInit")
(def ^:private k-routes       "__barebuildRouterRoutes")   ; vector of route handles
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
;; The registry is a CACHE of DOM truth, validated lazily against `isConnected`.
;; Routes self-register on connect (mounted bubbles while attached). Removal is
;; NOT signalled — `barebuild-route-unmounted` fires from disconnectedCallback,
;; when the element is already detached, so it cannot bubble to this router.
;; Instead, disconnected handles are skipped on read and pruned on each resolve.
(defn- read-routes [^js el]
  (or (du/getv el k-routes) []))

(defn- register-route! [^js el ^js route]
  (let [routes (read-routes el)]
    (when-not (some #(identical? % route) routes)
      (du/setv! el k-routes (conj routes route)))))

(defn- prune-disconnected!
  "Drop handles for routes no longer in the DOM. Keeps the cache bounded; the
  active-match read already ignores disconnected routes, so this is housekeeping."
  [^js el]
  (let [routes (read-routes el)
        live   (filterv (fn [^js r] (.-isConnected r)) routes)]
    (when (< (count live) (count routes))
      (du/setv! el k-routes live))))

;; ── Resolution: pure projection of (routes, URL) published as a value ───────────
(defn- active-params
  "Params (CLJS map) of the first registered route whose pattern matches
  `stripped`, or nil when none match. Skips disconnected routes (the registry is
  a lazily-validated cache). Reads each route's public `path` attribute — a held
  handle, not a tree walk; no write to the route."
  [^js el stripped]
  (some (fn [^js route]
          (when (.-isConnected route)
            (some-> (du/get-attr route route-model/attr-path)
                    model/parse-path-pattern
                    (model/match-path stripped)
                    :params)))
        (read-routes el)))

(defn- push-match!
  "Dispatch the current match AT a route handle. Non-bubbling so it does not
  re-enter the router or reach external observers — value-passing, not mutation."
  [^js route ^js detail]
  (.dispatchEvent route
                  (js/CustomEvent. model/event-route-change
                                   #js {:detail detail :bubbles false :composed false})))

(defn- compute-detail
  "Project (registered routes, current URL) → the route-change detail value
  `{:path :params}`. Reads `location` and route attributes, like a `read-model`;
  performs no mutation, so it stays the pure read phase (no `!`)."
  [^js el]
  (let [base      (or (du/get-attr el model/attr-base) model/default-base)
        stripped  (model/strip-base base (.. js/location -pathname))
        js-params (clj->js (or (active-params el stripped) {}))]
    #js {:path stripped :params js-params}))

(defn- cache-detail!
  "Cache the resolved match on the host so the read-only `.path` / `.params`
  getters reflect the current value. The one mutation in the resolve path."
  [^js el ^js detail]
  (du/setv! el k-path (.-path detail))
  (du/setv! el k-params (.-params detail)))

(defn- announce!
  "One bubbling dispatch on self for external observers (analytics, a title
  updater). Routes do not listen to this — they receive a targeted push."
  [^js el ^js detail]
  (du/dispatch! el model/event-route-change detail))

(defn- resolve!
  "Full resolution: push the current match to EVERY registered route, then
  announce. Used when the URL changes (navigate / popstate) — any route may flip
  visibility, so the fan-out to all routes is required. This is O(n) per discrete
  navigation, not a per-mount loop (see on-route-mounted)."
  [^js el]
  (prune-disconnected! el)                 ; validate the cache against the DOM
  (let [detail (compute-detail el)]
    (cache-detail! el detail)
    (doseq [^js route (read-routes el)]
      (push-match! route detail))
    (announce! el detail)))

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
  (let [^js route (.-target e)]
    (register-route! el route)
    ;; The URL has not changed, so every already-registered route still shows the
    ;; right thing. Push the current match to ONLY the new route — O(1) per mount,
    ;; so building an n-route app is O(n), not O(n²). A late-mounted matching route
    ;; therefore activates on registration without waiting for a navigation.
    (let [detail (compute-detail el)]
      (cache-detail! el detail)
      (push-match! route detail)
      (announce! el detail))))

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

(defn- on-doc-click [^js el ^js e]
  (let [facts (click-facts el e)]
    (when (model/should-intercept? facts)
      (.preventDefault e)
      (du/dispatch! el model/event-navigate
                    #js {:path (.-pathname ^js (:anchor facts))}))))

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
  (when (and (not= old-val new-val) (du/getv el k-handlers))
    (resolve! el)))

;; ── Registration ──────────────────────────────────────────────────────────────────
(defn init! []
  (component/register! model/tag-name
                       {:observed-attributes  model/observed-attributes
                        :connected-fn         connected!
                        :disconnected-fn      disconnected!
                        :attribute-changed-fn attribute-changed!
                        :setup-prototype-fn   install-property-accessors!}))
