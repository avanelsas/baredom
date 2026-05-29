(ns baredom.components.barebuild-route.barebuild-route-test
  (:require
   [cljs.test :refer-macros [deftest is use-fixtures]]
   [baredom.components.barebuild-router.barebuild-router :as router]
   [baredom.components.barebuild-route.barebuild-route :as route]
   [baredom.components.barebuild-router.model :as router-model]
   [baredom.components.barebuild-route.model :as model]))

(router/init!)
(route/init!)

(def ^:private original-path (.. js/location -pathname))

(defn- cleanup! []
  (doseq [n (.querySelectorAll js/document router-model/tag-name)] (.remove n))
  (doseq [n (.querySelectorAll js/document model/tag-name)] (.remove n))
  (.pushState js/history nil "" original-path))

(use-fixtures :each {:before cleanup! :after cleanup!})

;; ── Builders ────────────────────────────────────────────────────────────────────
(defn- make-router []
  (.createElement js/document router-model/tag-name))

(defn- make-route [path]
  (let [el (.createElement js/document model/tag-name)]
    (.setAttribute el "path" path)
    el))

(defn- append-body! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn- nav! [^js router-el path]
  (.dispatchEvent router-el
                  (js/CustomEvent. router-model/event-navigate
                                   #js {:detail #js {:path path} :bubbles true :composed true})))

(defn- visible? [^js route-el]
  (not= "none" (.. route-el -style -display)))

;; ── Emitted events (the route's own registration contract) ──────────────────────
(deftest emits-mounted-on-connect-and-unmounted-on-disconnect-test
  ;; Listen on the element itself (target phase). NOTE: `unmounted` fires from
  ;; disconnectedCallback, when the element is already detached, so it does NOT
  ;; bubble to ancestors — a listener on the element still receives it, an ancestor
  ;; (e.g. the router) does not. The router therefore cannot rely on bubbling for
  ;; deregistration; see the disconnect-deregistration coverage below.
  (let [el     (make-route "/x")
        events (atom [])
        on     (fn [^js e] (swap! events conj (.-type e)))]
    (.addEventListener el router-model/event-route-mounted on)
    (.addEventListener el router-model/event-route-unmounted on)
    (append-body! el)
    (is (some #{router-model/event-route-mounted} @events)
        "dispatches barebuild-route-mounted on connect")
    (.remove el)
    (is (some #{router-model/event-route-unmounted} @events)
        "dispatches the symmetric barebuild-route-unmounted on disconnect")))

;; ── Passive child with no router ancestor ───────────────────────────────────────
;; A route with nothing above it to push a match owns its own visibility and
;; starts hidden — it never flashes its slotted content waiting for a router.
(deftest route-without-router-stays-hidden-test
  (let [el (make-route "/x")]
    (append-body! el)
    (is (not (visible? el))
        "a route with no router ancestor stays hidden (no all-routes-visible flash)")))

;; ── Runtime path change re-registers and re-evaluates visibility ────────────────
(deftest runtime-path-change-flips-visibility-test
  (let [r     (make-router)
        route (make-route "/a")]
    (.appendChild r route)
    (append-body! r)
    (nav! r "/a")
    (is (visible? route) "route is visible at its initial path")
    ;; Change `path` at runtime: re-parses the pattern and re-dispatches mounted,
    ;; so the router re-pushes the current match against the NEW pattern.
    (.setAttribute route "path" "/b")
    (is (not (visible? route))
        "after path → /b, the route hides (the URL is still /a, no longer a match)")
    (nav! r "/b")
    (is (visible? route)
        "navigating to the new path shows it again — re-registration took effect")))

;; ── Removal drops the route from the router's resolution (isConnected pruning) ──
;; The router can't hear a bubbling unmounted (the route is detached by the time
;; disconnectedCallback fires), so deregistration is lazy: a disconnected route is
;; skipped on the next resolution. Asserted via the router's public .params.
(deftest removed-route-drops-out-of-resolution-test
  (let [r     (make-router)
        route (make-route "/users/:id")]
    (.appendChild r route)
    (append-body! r)
    (nav! r "/users/42")
    (is (= "42" (.. r -params -id)) "the router matches the route while it is present")
    (.remove route)
    ;; Lazy-validation contract: removal is NOT eagerly reconciled. With no
    ;; intervening resolution, the cached .params still reflects the removed route.
    ;; (Documented V1 limitation — see barebuild-router.md. Asserted so a future
    ;; switch to eager deregistration is a deliberate test change, not a silent one.)
    (is (= "42" (.. r -params -id))
        "immediately after removal, before any navigation, .params is still stale")
    (nav! r "/users/99")   ; next resolution prunes the detached route
    (is (nil? (.. r -params -id))
        "the next resolution drops the removed route — it no longer participates")))
