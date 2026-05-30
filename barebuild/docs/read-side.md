# The V1 read side

> The canonical pattern for what BareBuild **V1 actually ships**.

V1 ships **three** orthogonal orchestration elements and nothing else:

| Element | One sentence |
|---|---|
| [`<barebuild-router>`](../../docs/barebuild-router.md) | Watches `history`; publishes the current URL match as a value. |
| [`<barebuild-route>`](../../docs/barebuild-route.md) | Declares "this body is visible when the URL matches `path`"; owns its own visibility. |
| [`<barebuild-data>`](../../docs/barebuild-data.md) | Fetches a URL, publishes the response as a value (`.state` + `barebuild-data-state` event). Writes to no one. |

There is **no client store, no virtual DOM, no auto-wiring**. The server holds the truth; the DOM is a projection of it.

> **Not in V1.** `<barebuild-action>`, `<barebuild-bind>`, `<barebuild-invalidate-on>`, any `trigger`/polling attribute, `#selector`/`id`-based wiring, and a Hiccup renderer are **deferred to V1.1+** and designed-by-use from Phase 4 telemetry. The full target architecture (and the rationale for it) lives in [`server-state-first.md`](server-state-first.md), which is a *vision* doc, not a V1 contract.

---

## The canonical pattern

Read-only: declare a route, fetch JSON inside it, render with a BareDOM component. The broker is dormant until its route activates, so a multi-route app fetches one resource at a time — the one you're looking at — and re-reads each time you return to it.

```html
<barebuild-router>
  <barebuild-route path="/users">
    <barebuild-data></barebuild-data>   <!-- no src: dormant until its route activates -->
    <x-table columns="80px 2fr 1fr" bordered caption="Users"></x-table>
  </barebuild-route>
</barebuild-router>
```

```clojure
;; V1 exposes this glue intentionally — we want to see what shape you'd prefer
;; before committing to <barebuild-bind> contracts in V1.1.
;;
;; Listen at the *route* — the natural composition boundary. Identity preservation
;; (the route never destroys its slotted body) guarantees these handles survive
;; navigation, so the lookups happen once at boot, never inside the listener.
;;
;; The path selector couples this glue to the route's declared `path` — change one,
;; change the other. That coupling is the cost of hand-wiring in V1, and exactly the
;; friction Phase 4 observes before the V1.1 wiring elements are designed.
(defonce ^js users-route (.querySelector js/document "barebuild-route[path='/users']"))
(defonce ^js users-data  (.querySelector users-route "barebuild-data"))
(defonce ^js users-table (.querySelector users-route "x-table"))

;; <x-table> is row-composed — it has no single value property; you build it from
;; <x-table-row>/<x-table-cell> children, so a re-read rebuilds those rows from the
;; new value. (Components that DO expose a value property — x-chart's .data,
;; x-command-palette's .items — absorb the delta through the setter instead; rule 4.)
(defn- render-rows! [^js table rows]
  (set! (.-innerHTML table) "")                       ;; DOM = f(value): rebuild from it
  (doseq [row rows]
    (let [tr (.createElement js/document "x-table-row")]
      (doseq [col ["id" "name" "role"]]
        (let [td (.createElement js/document "x-table-cell")]
          (set! (.-textContent td) (str (goog.object/get row col)))
          (.appendChild tr td)))
      (.appendChild table tr))))

;; WHEN to read is an explicit value-in-time decision, not a side effect of mounting.
;; Re-setting src on each activation re-reads — a read is an observation in time.
;; route-change is pushed to EVERY route on every resolution, so gate on the path:
;; set src only when this route is the active match.
(.addEventListener users-route "barebuild-route-change"
  (fn [^js e]
    (when (= "/users" (.. e -detail -path))
      (set! (.-src users-data) "/api/users"))))

;; Read the delivered value from e.detail.state — a plain JS object
;; { phase, data, error, httpStatus } with a string phase (see rule 3), so any
;; consumer can read it. (event.target is for identity only — which broker.)
(.addEventListener users-route "barebuild-data-state"
  (fn [^js e]
    (let [^js state (.. e -detail -state)]
      (when (= "loaded" (.-phase state))
        (render-rows! users-table (.-data state))))))
```

> **Eager variant.** Baking `src="/api/users"` into the markup fetches once on mount and never re-reads on reactivation (the broker stays connected across navigation), and fetches on page load even inside routes that are never visible. Use it only for single-region or always-visible data; for per-route data use the activation-driven form above.

### Why this shape

1. **Value from `detail`, identity from `target`.** Read the delivered value from `event.detail.state` — the event carries it. Reach for `event.target` only to know *which* sibling broker fired. The event detail carries **no** `:id` / `:name`; disambiguate by where elements sit in the tree, not by a place-coupled identifier.
2. **When-to-read is explicit.** Setting `src` on `barebuild-route-change` makes "read now" a visible line of user code, not an accident of DOM construction. A read is an observation in time; reactivation is a new time, so it re-reads.
3. **`.state` is a value — a plain JS object.** `(.-state el)` returns `{ phase, data, error, httpStatus }` with a **string** `phase` (`"idle" | "loading" | "loaded" | "error"`); `data`/`error`/`httpStatus` are present only when meaningful. It is a JS object, not a ClojureScript map, on purpose: the components ship as per-component ESM, so a consumer's app is compiled with its *own* `cljs.core` — a CLJS persistent map would carry this module's `Keyword`/map classes, which the consumer's `cljs.core` can't read (lookups return nil). A JS object is readable by any consumer — vanilla JS (`state.phase`) or a separately-compiled CLJS app (`(.-phase state)`). Between phase transitions the same object is returned by reference (`identical?` holds), so there is no per-read allocation.
4. **Components absorb deltas via property setters — or by row composition.** A component that exposes a value property absorbs deltas through the setter: `(set! (.-data chart) points)` / `(set! (.-items palette) cmds)` triggers the component's own render-pipeline, whose model-change guard applies the delta without destroying the element, so selection / scroll / focus survive. `<x-table>` has no such property — it is *row-composed*, so a re-read rebuilds its `<x-table-row>` children from the new value (the `render-rows!` above). That rebuild re-mounts the rows, so per-row state is not preserved — acceptable for a read-only projection, which is the V1 read-side default. See rule 4.
5. **The router never writes to its children.** It dispatches the current-match value `barebuild-route-change` *at* each registered route; each route owns its own `display` toggle. `<barebuild-data>` likewise writes to no one — it only publishes.

---

## The four rules (V1)

### 1. Coordination is by containment and URL

`<barebuild-data>` publishes; consumers listen at a containing element and read the value from `event.detail.state` (using `event.target` only to tell sibling brokers apart). The URL is the identity of the resource a broker fetches. There are no shared registries, no observable streams, no global store.

### 2. Wiring is explicit and visible

Every cross-boundary effect in V1 is a line of user code at a known composition boundary — one `addEventListener` at the route. You can grep it, read it, and delete it. No element reaches across boundaries on its own. (V1.1's `<barebuild-bind>` will move this line into markup; the V1 friction is deliberate so we learn the shape you'd prefer before committing.)

### 3. `src` is the only fetch trigger

`<barebuild-data>` fetches whenever `src` is set and the element is connected; reconnection re-fetches; disconnection aborts. There is **no `trigger-on-connect`, no polling attribute**. Manual refetch is event-driven:

```clojure
(.dispatchEvent el (js/CustomEvent. "barebuild-data-refresh"))
```

Polling is `setInterval` over that dispatch in *your* code — the broker does not schedule.

### 4. Components absorb deltas — by property setter, or by rebuilding composed children

Components that expose a value property — `x-chart`'s `.data`, `x-command-palette`'s `.items` — absorb deltas through the setter: `(set! (.-data chart) rows)` triggers the component's render-pipeline, whose model-change guard compares old and new, applies the delta, and stops. Component-internal state (selection, hover, sort order) is preserved, and the element is never destroyed. Row-composed components like `<x-table>` have no single value property; you rebuild their children from the value, which re-mounts the rows (per-row state is not preserved) — fine for the read-only projections V1 targets. Either way there is **no top-level Hiccup re-render** in V1 — Hiccup itself is deferred.

---

## Three ways to consume `<barebuild-data>`

### A — Listen at the composition boundary (the default)

```clojure
;; value from detail; target only if one listener serves multiple sibling brokers.
;; render-rows! rebuilds the row-composed <x-table> from the value (see above); a
;; value-property component would instead take the delta via its setter (rule 4).
(.addEventListener users-route "barebuild-data-state"
  (fn [^js e]
    (let [{:keys [phase data]} (.. e -detail -state)]
      (when (= :loaded phase) (render-rows! users-table data)))))
```

### B — Pull the value when you want it

```clojure
(let [{:keys [phase data]} (.-state el)]
  (when (= :loaded phase) data))
```

### C — Non-CLJS / analytics consumers

```js
route.querySelector('barebuild-data').addEventListener('barebuild-data-state', (e) => {
  // e.detail.state is a CLJS persistent map in V1; read via cljs.core interop.
  // (e.target identifies which broker fired.) A JS-view accessor (.stateJs) is
  // deferred — see barebuild-data.md.
});
```

---

## When to reach for re-frame instead

V1 defaults to *no client store* because in the server-state-first shape there usually isn't enough client state to justify one. Apps that genuinely need a global event/effect/state model — multi-step wizards, optimistic UIs, complex concurrent orchestration — can opt in at the application layer with zero help or hindrance from BareBuild. See [`state-recipes.md`](state-recipes.md).

---

## See also

- Per-component reference: [`barebuild-router`](../../docs/barebuild-router.md), [`barebuild-route`](../../docs/barebuild-route.md), [`barebuild-data`](../../docs/barebuild-data.md).
- [`server-state-first.md`](server-state-first.md) — the full target architecture and *why* (vision doc; V1.1 end-state).
- [`state-recipes.md`](state-recipes.md) — ephemeral state, teardown, re-frame compat.
- [`BAREBUILD-V1-PLAN.md`](BAREBUILD-V1-PLAN.md) — architectural rationale.
