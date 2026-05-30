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
(def ^:private k-detail       "__barebuildRouterDetail")   ; cached JS detail #js{:path :params}; getters + pushes read it
(def ^:private k-handlers     "__barebuildRouterHandlers") ; stashed listener closures

;; ── String-literal constants ───────────────────────────────────────────────────
(def ^:private styles           ":host{display:contents}")
(def ^:private attr-data-route  "data-barebuild-route")
(def ^:private attr-download    "download")
;; localName is lowercase in BOTH HTML and XHTML documents (unlike tagName, which
;; is upper-cased only in HTML) — so matching on it is document-type robust.
(def ^:private name-router      "barebuild-router")

;; Native DOM event names (the strings passed to add/removeEventListener).
(def ^:private event-click    "click")
(def ^:private event-popstate "popstate")

;; Property keys under which each handler closure is stashed in the k-handlers
;; JS object. The `listener-spec` table below is the single source of truth for
;; the (target, event, handler-key, capture?) wiring — both add-listeners! and
;; remove-listeners! iterate it, so the two paths can never drift.
(def ^:private handler-key-doc-click "docClick")
(def ^:private handler-key-popstate  "popstate")
(def ^:private handler-key-navigate  "navigate")
(def ^:private handler-key-mounted   "mounted")

;; ── Shadow DOM (layout-transparent; the router renders no box of its own) ───────
(defn- ensure-shadow! [^js el]
  (du/ensure-shadow-with-style! el styles k-initialized? true))

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
  ignores disconnected routes, so this just keeps the cache bounded. In the
  steady state (all routes live) this is a single `some` scan and no allocation
  — the `filterv` copy runs only when an entry actually needs pruning."
  [^js el]
  (let [entries (read-entries el)]
    (when (some (fn [[^js r _]] (not (.-isConnected r))) entries)
      (du/setv! el k-routes (filterv (fn [[^js r _]] (.-isConnected r)) entries)))))

;; ── Resolution ──────────────────────────────────────────────────────────────────
(defn- active-params
  "Params (CLJS map) of the first connected registered route whose cached pattern
  matches the pre-split `segs`, or nil when none match. Uses the pattern each
  route parsed for itself (carried in `mounted`) — the router never re-parses —
  and the segments the caller split ONCE, so the stripped path is not
  re-segmented per route. A nil pattern (an inert route with no `path`) is
  skipped: it must never match, not behave like the root pattern `[]`."
  [^js el segs]
  (some (fn [[^js route pattern]]
          (when (and pattern (.-isConnected route))
            (:params (model/match-segments pattern segs))))
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
  `{:path :params}` (CLJS). Reads `location` + the cached patterns; no mutation.
  Segments the stripped path ONCE here and hands the segments to active-params,
  so N routes match against one segmentation rather than re-splitting per route."
  [^js el]
  (let [stripped (model/strip-base (du/get-attr el model/attr-base)
                                    (.. js/location -pathname))
        segs     (model/split-segments stripped)]
    {:path stripped :params (or (active-params el segs) {})}))

(defn- announce!
  "One bubbling dispatch on self for external observers (analytics, a title
  updater). Routes do not listen to this — they receive a targeted push."
  [^js el ^js detail]
  (du/dispatch! el model/event-route-change detail))

(defn- publish!
  "Prune dead handles, recompute the match, and — ONLY when it changed from the
  last published value — refresh the cached JS detail and announce one bubbling
  route-change for external observers (the epochal change-guard; avoids spurious
  events and param-identity churn on same-URL popstate / no-op mounts). ALWAYS
  push the current match to `routes` so each (re)evaluates its own visibility —
  the route's own change-guard makes a redundant push a no-op. The single cached
  `k-detail` object is reused for the announce and every push (read-only to
  consumers) and is what the readonly .path/.params getters project."
  [^js el routes]
  (prune-disconnected! el)
  (let [match (compute-match el)]
    (when (not= match (du/getv el k-match))
      (let [detail #js {:path (:path match) :params (clj->js (:params match))}]
        (du/setv! el k-match  match)
        (du/setv! el k-detail detail)
        (announce! el detail)))
    (let [detail (du/getv el k-detail)]
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
  ;; `barebuild-navigate` bubbles + is composed (du/dispatch!), so without this
  ;; an inner router's navigate would also reach an OUTER router's listener,
  ;; double-pushState'ing and resolving the outer against a URL its anchor-scoping
  ;; excluded. Stopping here means the nearest router handles it and no other —
  ;; the same nearest-router scoping should-intercept? enforces for the click.
  (.stopPropagation e)
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
  ;; `instance?` (not a tagName string compare) so it is document-type robust and
  ;; intentionally excludes SVG <a> (SVGAElement) — those expose no .pathname/
  ;; .search/.hash URL API to SPA-navigate with, so they fall through to the
  ;; browser's native navigation by design.
  (and (instance? js/HTMLAnchorElement node)
       (.hasAttribute node attr-data-route)))

(defn- router-node? [^js node]
  (= name-router (.-localName node)))

(defn- modifier-key? [^js e]
  (or (.-metaKey e) (.-ctrlKey e) (.-shiftKey e) (.-altKey e)))

(defn- candidate-click?
  "Cheap, anchor-independent gate applied BEFORE the composedPath walk: a
  primary-button click, no modifier key held, nothing already handled. Lets the
  browser keep its default for middle/right-clicks, ctrl/cmd-clicks (open in a
  new tab) and clicks a prior handler consumed — and avoids materialising the
  composed path for the ~99% of page clicks that are not routed anchors."
  [^js e]
  (and (= 0 (.-button e))
       (not (modifier-key? e))
       (not (.-defaultPrevented e))))

(defn- click-facts
  "Extract the anchor facts `should-intercept?` decides on, in one pass over the
  composed path (innermost target first, ascending toward the root).
  `drop-while` skips to the marked anchor; the nearest router is then the first
  <barebuild-router> *above* it — so a router between the anchor and `el` wins
  (nearest-router scoping for nested/sibling routers). When no marked anchor is
  on the path, only `:anchor-present? false` is returned (the anchor-derived
  facts would be meaningless), and should-intercept? bails on that alone."
  [^js el ^js e]
  (let [path        (array-seq (.composedPath e))
        from-anchor (drop-while (complement anchor-node?) path)
        ^js anchor  (first from-anchor)
        nearest     (first (filter router-node? (rest from-anchor)))]
    (if anchor
      {:anchor                  anchor
       :anchor-present?         true
       :nearest-router-is-this? (identical? nearest el)
       ;; Same-origin AND same-frame AND not a download: otherwise the anchor's
       ;; native behaviour (external nav, new tab/window, file download) must win
       ;; — intercepting it would push a bogus same-origin path and break the link.
       :same-origin?            (= (.-origin anchor) (.. js/location -origin))
       :same-frame?             (let [t (.-target anchor)] (or (= t "") (= t "_self")))
       :download?               (.hasAttribute anchor attr-download)
       ;; A pure in-page hash link: same pathname AND query, only the #fragment
       ;; differs. Let the browser handle it (native scroll, no junk history entry)
       ;; rather than pushState the same route over itself.
       :hash-only?              (and (= (.-pathname anchor) (.. js/location -pathname))
                                     (= (.-search anchor)   (.. js/location -search))
                                     (not= "" (.-hash anchor)))}
      {:anchor-present? false})))

(defn- anchor-href-path
  "The anchor's same-origin path to navigate to: pathname + query + hash. Using
  `.-pathname` alone would silently drop `?query` and `#hash` from the URL bar
  (and from `location.search`); route matching still uses the pathname only."
  [^js a]
  (str (.-pathname a) (.-search a) (.-hash a)))

(defn- on-doc-click [^js el ^js e]
  (when (candidate-click? e)
    (let [facts (click-facts el e)]
      (when (model/should-intercept? facts)
        (.preventDefault e)
        (du/dispatch! el model/event-navigate
                      #js {:path (anchor-href-path (:anchor facts))})))))

;; ── Listener wiring (mixed targets: document/window/self) ───────────────────────
;; Single source of truth for the static listener set. Each entry is
;; [target-key event handler-key capture?]; `:document`/`:window`/`:self` resolve
;; to the literal target. add-listeners! and remove-listeners! both iterate this
;; vector so they cannot drift on event name, target, or capture flag.
;; No `unmounted` listener: route removal cannot bubble here (detached element),
;; so deregistration is by isConnected pruning, not an event.
(def ^:private listener-spec
  [[:document event-click               handler-key-doc-click true]
   [:window   event-popstate            handler-key-popstate  false]
   [:self     model/event-navigate      handler-key-navigate  false]
   [:self     model/event-route-mounted handler-key-mounted   false]])

(defn- resolve-listener-target [^js el target-key]
  (case target-key
    :document js/document
    :window   js/window
    :self     el))

(defn- build-handlers [^js el]
  (let [handlers #js {}]
    (gobj/set handlers handler-key-doc-click (fn router-doc-click [e] (on-doc-click el e)))
    (gobj/set handlers handler-key-popstate  (fn router-popstate  [e] (on-popstate el e)))
    (gobj/set handlers handler-key-navigate  (fn router-navigate  [e] (on-navigate el e)))
    (gobj/set handlers handler-key-mounted   (fn router-mounted   [e] (on-route-mounted el e)))
    handlers))

(defn- add-listeners! [^js el]
  (let [handlers (build-handlers el)]
    (du/setv! el k-handlers handlers)
    (doseq [[target-key event handler-key capture?] listener-spec]
      (.addEventListener (resolve-listener-target el target-key)
                         event (gobj/get handlers handler-key) capture?))))

(defn- remove-listeners! [^js el]
  (when-let [^js h (du/getv el k-handlers)]
    (doseq [[target-key event handler-key capture?] listener-spec]
      (.removeEventListener (resolve-listener-target el target-key)
                            event (gobj/get h handler-key) capture?))
    (du/setv! el k-handlers nil)))

;; ── Property accessors ───────────────────────────────────────────────────────────
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api)   ; installs :base; skips readonly :path/:params
  ;; .path / .params both project the single cached detail object (see publish!).
  (du/define-readonly-prop! proto "path"
                            (fn br-get-path [^js this]
                              (if-let [^js d (du/getv this k-detail)] (.-path d) "")))
  (du/define-readonly-prop! proto "params"
                            (fn br-get-params [^js this]
                              (if-let [^js d (du/getv this k-detail)] (.-params d) #js {}))))

;; ── Lifecycle ─────────────────────────────────────────────────────────────────────
(defn- connected! [^js el]
  (ensure-shadow! el)
  (when-not (du/getv el k-handlers)
    (add-listeners! el))
  (resolve! el))

(defn- disconnected! [^js el]
  (remove-listeners! el)
  ;; Drop the route registry. It is a cache of DOM truth, pruned lazily by
  ;; isConnected on the next publish! — but a detached router runs no publish!,
  ;; so a router moved/removed while its former routes stay connected elsewhere
  ;; would keep stale handles and fight their new owner. Reconnect re-registers:
  ;; each child route's connectedCallback re-dispatches `mounted`.
  (du/setv! el k-routes []))

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
