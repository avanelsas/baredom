(ns baredom.components.barebuild-router.barebuild-router-test
  (:require
   [cljs.test :refer-macros [deftest is use-fixtures]]
   [baredom.components.barebuild-router.barebuild-router :as router]
   [baredom.components.barebuild-route.barebuild-route :as route]
   [baredom.components.barebuild-router.model :as model]
   [baredom.components.barebuild-route.model :as route-model]))

(router/init!)
(route/init!)

(def ^:private original-path (.. js/location -pathname))

(defn- cleanup! []
  (doseq [node (.querySelectorAll js/document model/tag-name)]
    (.remove node))
  (doseq [node (.querySelectorAll js/document route-model/tag-name)]
    (.remove node))
  ;; Restore the page URL so sibling test files are unaffected.
  (.pushState js/history nil "" original-path))

(use-fixtures :each {:before cleanup! :after cleanup!})

;; ── Builders ────────────────────────────────────────────────────────────────────
(defn- make-router []
  (.createElement js/document model/tag-name))

(defn- make-route [path]
  (let [el (.createElement js/document route-model/tag-name)]
    (.setAttribute el "path" path)
    el))

(defn- append-body! [^js el]
  (.appendChild (.-body js/document) el)
  el)

(defn- nav! [^js router-el path]
  (.dispatchEvent router-el
                  (js/CustomEvent. model/event-navigate
                                   #js {:detail #js {:path path} :bubbles true :composed true})))

(defn- visible? [^js route-el]
  (not= "none" (.. route-el -style -display)))

;; ── Navigation + visibility ─────────────────────────────────────────────────────
(deftest navigate-shows-matching-route-test
  (let [r     (make-router)
        root  (make-route "/")
        users (make-route "/users")]
    (.appendChild r root)
    (.appendChild r users)
    (append-body! r)
    (nav! r "/users")
    (is (visible? users) "matching route is visible")
    (is (not (visible? root)) "non-matching route is hidden")
    (nav! r "/")
    (is (visible? root) "root becomes visible after navigating back")
    (is (not (visible? users)) "users hides after navigating away")))

(deftest route-change-detail-carries-path-and-params-test
  (let [r       (make-router)
        users   (make-route "/users/:id")
        seen    (atom nil)]
    (.appendChild r users)
    (append-body! r)
    (.addEventListener r model/event-route-change
                       (fn [^js e] (reset! seen (.-detail e))))
    (nav! r "/users/42")
    (is (= "/users/42" (.-path ^js @seen)) "detail carries the resolved path")
    (is (= "42" (.. ^js @seen -params -id)) "detail carries extracted params")
    (is (= "/users/42" (.-path r)) "router .path reflects the active path")
    (is (= "42" (.. r -params -id)) "router .params reflects the active params")))

;; ── Base prefix stripping (component-level) ─────────────────────────────────────
;; The model's strip-base is unit-tested; this asserts the router actually wires
;; the `base` attribute into resolution, stripping it before matching routes.
(deftest base-prefix-strips-before-matching-test
  (let [r     (make-router)
        users (make-route "/users")]
    (.setAttribute r "base" "/app")
    (.appendChild r users)
    (append-body! r)
    (nav! r "/app/users")
    (is (visible? users) "with base=/app, /app/users matches the /users route")
    (is (= "/users" (.-path r)) "router .path reflects the base-stripped path")
    (nav! r "/app/other")
    (is (not (visible? users)) "a different sub-path under the base does not match /users")
    (is (= "/other" (.-path r)) "a non-matching path still has its base stripped for .path")))

;; ── Late mount ────────────────────────────────────────────────────────────────────
(deftest late-mount-activates-immediately-test
  (let [r    (make-router)
        root (make-route "/")]
    (.appendChild r root)
    (append-body! r)
    (nav! r "/users")                       ; resolve before the matching route exists
    (let [users (make-route "/users")]
      (.appendChild r users)                ; appended AFTER resolution
      (is (visible? users)
          "a late-mounted matching route activates on registration, no extra navigation"))))

;; ── Route owns its visibility; router writes no attrs/styles to routes ─────────
(deftest router-does-not-write-attributes-to-routes-test
  (let [r     (make-router)
        users (make-route "/users")]
    (.appendChild r users)
    (append-body! r)
    (nav! r "/users")
    (is (visible? users))
    ;; The route exposes no externally-observable activation flag set by the router.
    (is (not (.hasAttribute users "active")) "router sets no `active` attribute")
    (is (not (.hasAttribute users "selected")) "router sets no `selected` attribute")
    ;; The only style the route carries is its own display toggle.
    (is (= "" (.. users -style -display)) "active route clears its own display override")))

;; ── Identity preservation ──────────────────────────────────────────────────────
(deftest identity-preservation-test
  (let [r     (make-router)
        form  (make-route "/form")
        other (make-route "/other")
        input (.createElement js/document "input")]
    (.appendChild form input)
    (.appendChild r form)
    (.appendChild r other)
    (append-body! r)
    (nav! r "/form")
    (set! (.-value input) "typed text")
    (let [captured input]
      (nav! r "/other")                      ; form hidden
      (nav! r "/form")                       ; form shown again
      (is (identical? captured (.querySelector form "input"))
          "the slotted child handle is identical across a display toggle")
      (is (= "typed text" (.-value (.querySelector form "input")))
          "input value survives the toggle (state lives on the surviving node)"))))

;; ── Nested-router scoping (stopPropagation), asserted behaviourally ─────────────
;; A param route lives under the INNER router, nested inside the OUTER router. If
;; the inner route's bubbling `mounted` were not stopped at the inner router, the
;; outer would also register it and would then match the param URL — exposing the
;; param on its public `.params`. Asserting on `.params` (values-out) instead of
;; the private registry keeps the test honest across a registry-representation change.
(deftest nested-router-scoping-test
  (let [outer (make-router)
        inner (make-router)
        route (make-route "/users/:id")]
    (.appendChild inner route)
    (.appendChild outer inner)               ; inner router nested inside outer
    (append-body! outer)
    ;; Navigate the whole page; popstate makes both routers (window listeners) resolve.
    (.pushState js/history nil "" "/users/42")
    (.dispatchEvent js/window (js/Event. "popstate"))
    (is (= "42" (.. inner -params -id))
        "the nearest (inner) router owns the route and exposes its param")
    (is (visible? route)
        "the inner route activates under its own router")
    (is (nil? (.. outer -params -id))
        "the outer router never registered the inner route (stopPropagation) — no match, no param")))

;; ── Anchor interception (happy path, end-to-end) ───────────────────────────────
(deftest anchor-interception-happy-path-test
  (let [r     (make-router)
        users (make-route "/users")
        link  (.createElement js/document "a")]
    (.setAttribute link "href" "/users")
    (.setAttribute link "data-barebuild-route" "")
    (.appendChild r link)
    (.appendChild r users)
    (append-body! r)
    (.click link)                            ; capture handler intercepts + preventDefault (no reload)
    (is (= "/users" (.. js/location -pathname)) "marked anchor click navigates via pushState")
    (is (visible? users) "the target route activates after the intercepted click")))

(deftest anchor-interception-preserves-query-and-hash-test
  (let [r     (make-router)
        users (make-route "/users")
        link  (.createElement js/document "a")]
    (.setAttribute link "href" "/users?q=foo#top")
    (.setAttribute link "data-barebuild-route" "")
    (.appendChild r link)
    (.appendChild r users)
    (append-body! r)
    (.click link)
    (is (= "/users" (.. js/location -pathname)) "pathname is used for matching")
    (is (= "?q=foo" (.. js/location -search)) "query string is preserved in the URL bar")
    (is (= "#top" (.. js/location -hash)) "hash is preserved in the URL bar")
    (is (visible? users) "the route still matches on pathname")))

;; ── Anchor scoping: only the anchor's nearest router intercepts ─────────────────
;; Two SIBLING routers, each with a matching `/scoping-x` route. The marked anchor
;; lives inside `a-router`. Both routers run a document-capture click handler, but
;; the composed-path walk in `click-facts` makes only `a-router` the anchor's
;; nearest router, so only it intercepts. Siblings share no bubbling path, so
;; `b-router` never resolves and its route stays hidden — proving non-nearest
;; routers leave the click alone.
(deftest sibling-router-anchor-scoping-test
  (let [a-router (make-router)
        b-router (make-router)
        a-route  (make-route "/scoping-x")
        b-route  (make-route "/scoping-x")
        link     (.createElement js/document "a")]
    (.setAttribute link "href" "/scoping-x")
    (.setAttribute link "data-barebuild-route" "")
    (.appendChild a-router link)
    (.appendChild a-router a-route)
    (.appendChild b-router b-route)
    (append-body! a-router)
    (append-body! b-router)
    (.click link)
    (is (= "/scoping-x" (.. js/location -pathname))
        "the anchor's nearest (containing) router intercepts and navigates")
    (is (visible? a-route)
        "the containing router's matching route activates")
    (is (not (visible? b-route))
        "the sibling router does NOT intercept — its route stays hidden")))
