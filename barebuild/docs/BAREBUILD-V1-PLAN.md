# BareBuild V1 — Phased Implementation Plan

## Context

BareDOM is a mature ClojureScript library of 100+ stateless web components compiled via shadow-cljs (`:esm`) with zero frontend runtime dependencies. It wins on *correctness and longevity* but is currently a **components-only** ecosystem: app authors hand-roll routing, fetching, and rendering glue every time.

**BareBuild V1** is a thin, data-driven scaffold on top — **Rails-fast, not Rails-magical**. It ships to the CLJS community: Babashka, shadow-cljs, deps.edn users who want `barebuild new my-app && cd my-app && bb dev` to just work.

**V1 ships the read side only.** Write-side coordination (action / bind / invalidate-on) is deliberately *not* in V1; it's designed-by-use from real user implementations collected during the V1 demo. See [`write-side-sketch.md`](write-side-sketch.md).

**V1 also ships no Hiccup renderer.** Routes are declared as markup; users wire data → DOM with `addEventListener` and direct property writes. A renderer (`bb.render`, `mount!`, attribute-vs-property dispatch) is deferred until the validation-gate audience and the Phase 4 demo have shown what shape it should take.

**Success criterion:** a CLJS developer scaffolds an app, declares a route, fetches JSON, renders a list with BareDOM components — **without re-frame, without a virtual DOM, and without leaving Clojure data on the read path**.

## Architectural Pillars

| Hickey principle | BareBuild expression |
|---|---|
| **Simple, not easy** | Three small orthogonal elements (router, route, data). Each does one thing. No auto-wiring. |
| **Data > functions > macros** | Routes are markup children. Generator templates are EDN. Path patterns are strings (`/users/:id`) parsed once into data — the runtime contract is the parsed value, not the string. |
| **De-complect** | Routing, fetching, and state are three independent concerns. Wiring is by **containment** (route ↔ router via a bubbling event) and **URL** (data ↔ invalidation source, post-V1). No *framework* element ever resolves a CSS selector. User-side wiring uses selectors in V1 — that's part of the friction Phase 4 observes for V1.1 contract design. |
| **Values, not places** | Route configuration is markup; the match result is a value. Fetched state is *one* value (`.state` is a plain JS object — see Decision #6). **The router owns no authoritative state** — it caches projections of two external truths (the URL in `history`/`location`; the route set in the DOM), holds its route-handle registry as a succession of immutable values (not in-place mutation), and computes matches purely. Nothing owns mutable global state. |
| **Web as info system** | Navigation = `popstate` + `barebuild-navigate`. Data state = `barebuild-data-state {:state}`. Every event detail is a JS object; **listeners read the delivered value from `event.detail`**, and disambiguate siblings via `event.target` (identity only — which element dispatched), never by reading a place-coupled identifier from the detail. **Payloads are schema-open JS objects; the value at `.state` / `event.detail.state` is a plain JS object, not a CLJS map** — readable by any consumer across the `cljs.core` boundary (Decision #6), so no `.stateJs` view is needed. The `.state` keys are `phase` (a string `"idle" \| "loading" \| "loaded" \| "error"`), `data`, `error`, `httpStatus`; `data`/`error`/`httpStatus` are present only when meaningful for the current phase (absent, not nil-valued, otherwise). |
| **Composition over inheritance** | All orchestration elements are siblings of BareDOM's UI components — same `component/register!` + `model.cljs` pattern. No new framework primitive. |
| **Transparency** | Functions ending `!` mutate. Pure ones don't. Users read a tree of data, not callbacks. |

## Strategic Decisions

1. **In-repo for V1, organised for extraction.** Orchestration *components* live in `src/baredom/components/barebuild-*/` like every other component. The *rest* of BareBuild (CLI, templates, platform docs, demo) lives under `barebuild/`. Boundary enforcement is **one canonical script** — `bb scripts/check-barebuild-boundary.bb` — that runs all four checks in sequence: (a) relative-path grep across the boundary, (b) positive `adapter-eligible` registry filter, (c) path-anchored `npm pack --dry-run --json | jq` over packed paths, (d) `grep -r querySelector src/baredom/components/barebuild-*/` is empty. CI invokes the one script. **This is the deliberate cost of co-location for V1 velocity, knowing the alternative is two repos before BareBuild's surface is stable.** **Extraction trigger:** when V1 ships green and at least one third-party project reports a working `barebuild new` flow, `barebuild/` extracts to its own repo on the next minor release. A tracking issue must be filed before Phase 1a merges. **Outer bound:** if no third-party adopter appears within 6 months of V1 ship, BareBuild stays in-repo and we document on the tracking issue that the audience did not materialise — extraction has no value without consumers, and the boundary cost is not worth paying for a single-consumer codebase.

2. **Naming:** `barebuild-*` for orchestration; `x-*` stays UI-only.

3. **`<x-form>` is unchanged, and intentionally retains its `x-` prefix.** It is a UI surface (form-with-styling) whose serialisation into `{:values <map>}` is incidental orchestration. The element predates the `barebuild-*` taxonomy and ships in `@vanelsas/baredom` to non-BareBuild consumers too; renaming it would break those users. Documented as the one named exception to "`x-*` is UI-only / `barebuild-*` is orchestration." HTTP transport is V1.1.

4. **Server state IS the state.** `<barebuild-data>` fetches and publishes its `.state` value, dispatching `barebuild-data-state {:state}` on every phase transition. Listeners read `event.target` for the dispatching element (composition by containment) and `.state` for its value; the detail does not carry an identifier. **In V1, consumers absorb new values via user code (one `addEventListener` at the route — the natural composition boundary).** The wiring elements (`<barebuild-bind>`, `<barebuild-invalidate-on>`) are deliberately deferred — we want to see how users wire it by hand before committing to the contracts. See [`write-side-sketch.md`](write-side-sketch.md) for the current best-guess shapes.

5. **Anchors opt in via `data-barebuild-route`. No document-spanning interception of plain `<a>` tags.** The router listens at `document` capture but only acts on clicks whose `event.composedPath()` includes an `<a data-barebuild-route>` ancestor **AND** that anchor is a descendant of *this* router (composedPath also contains the router element itself). There is no opt-out list. The router's `should-intercept?` model fn is a positive check: matching anchor present, anchor is contained by this router, no modifier key, `preventDefault` not already called. **Multi-router pages:** two routers on the same page each handle only their own anchors; their document-capture handlers don't compete because the containment check fails for the other router's subtree. Users mark links they own; the router never shadows native browser anchor behaviour. Documented in `docs/barebuild-router.md`.

6. **[CORRECTED post-implementation — `.state` is a plain JS object, not a CLJS map.** A runtime test of a real npm-consuming app proved the CLJS persistent map is unreadable across the `cljs.core` boundary (a consumer's app has its own `cljs.core`; the map's `Keyword` class differs, so lookups return nil). `.state` and the event detail are now `{ phase, data, error, httpStatus }` (string phase). See [`docs/barebuild-data.md` → Why a JS object](../../docs/barebuild-data.md). The original text below is kept for history.]** One read-only `.state` property per fetcher, returning the persistent CLJS map directly. No `.stateJs`.** `<barebuild-data>` exposes one read-only `.state` returning a map with keys `:phase :data :error :http-status` (the `?`-suffixed keys are *not* literal — `:data`/`:error`/`:http-status` are present only when meaningful for the current phase). The V1 audience is CLJS; the primary contract is the CLJS value. Successive reads with no phase transition return the *same cached value* — `(identical? (.-state el) (.-state el))` is `true` (and therefore `=`); the map is cached, not rebuilt per read. JS-view accessors are later if and when broadening the audience demands them.

7. **No trigger attribute, no method.** `<barebuild-data src="/api/users">` fetches whenever `src` is set and the element is connected; reconnection re-fetches; disconnection aborts. Manual refetch is event-driven: `(.dispatchEvent el (js/CustomEvent. "barebuild-data-refresh"))`. Polling is `(js/setInterval #(.dispatchEvent el (js/CustomEvent. "barebuild-data-refresh")) 5000)` in user code — `<barebuild-data>` does not schedule. **A trigger concept enters the design only when a second value for it exists** (canonical invalidation channel ships with the write-side elements in V1.1).

8. **V1 HTTP is JSON-only, GET-only via `<barebuild-data>`.** `<barebuild-data>` issues `GET` and parses JSON responses. No `enctype`, no `method`, no body. POST/PUT/DELETE arrive with `<barebuild-action>` in V1.1.

9. **`<barebuild-route>` self-registers with its ancestor router via a bubbling event.** On connect, `<barebuild-route>` dispatches a bubbling `barebuild-route-mounted` event. The nearest ancestor router catches it and registers the route. No tree walk inside the route; no "single router per page" promise; survives shadow boundaries because events compose. Reduces a stated rule to native event propagation.

10. **Adapter codegen consults a positive registry, not a negative `:adapter-skip` flag.** A single `adapter-eligible` set in `src/baredom/registry.cljs` lists the UI components framework adapters wrap. Orchestration components are simply not on the list. Codegen iterates `(filter adapter-eligible? all-registers)` and never enumerates orchestration components. **The set ships in Phase 1a** alongside the first orchestration components — it cannot wait for a renderer that isn't in V1.

## Extraction-Ready Organisation

```
BareDom/                                    (this repo)
├── src/baredom/components/                 ← all components: x-* (UI) + barebuild-* (orchestration). Ships in @vanelsas/baredom.
├── src/baredom/exports/                    ← per-component ESM entries (existing)
├── adapters/                               ← UI components only; iterates adapter-eligible? from registry
├── scripts/                                ← BareDOM scripts (stays)
├── bb.edn                                  ← BareDOM tasks (stays; unchanged surface)
├── docs/                                   ← per-component docs (incl. docs/barebuild-*.md)
├── barebuild/                              ← entire BareBuild platform layer
│   ├── cli/                                ← bb CLI source (bbin entry point)
│   ├── templates/                          ← project scaffold EDN templates
│   ├── docs/                               ← platform docs + write-side-sketch
│   ├── demo-app/                           ← Phase 4 E2E demo
│   ├── test/                               ← BareBuild platform tests
│   ├── bb.edn                              ← BareBuild bb tasks
│   └── README.md
└── README.md                               ← BareDOM README (stays)
```

**Boundary rules** — orchestration components ship from BareDOM and name themselves BareBuild; nothing inside `barebuild/` references files outside via relative path; nothing outside `barebuild/` references files inside; the published `@vanelsas/baredom` tarball excludes `barebuild/`; BareBuild has its own `bb.edn`, test root, release task; orchestration components are excluded from framework-adapter codegen by virtue of not appearing in `adapter-eligible`. All of this is enforced by the single script `bb scripts/check-barebuild-boundary.bb` (invoked by CI and locally). Full details in [`barebuild/docs/RELEASES-BAREBUILD.md`](RELEASES-BAREBUILD.md).

## Phase Map

| Phase | Goal | Output |
|---|---|---|
| **0** | **Validation gate.** Show the canonical pattern to 5+ CLJS devs; pause Phase 1 unless ≥3 say "I'd try it." | Posted gist + collected responses |
| **1a** | Navigation: `barebuild-router`, `barebuild-route` (incl. `adapter-eligible` registry split) | 2 components + registry update |
| **1b** | Read-side data broker: `barebuild-data` | 1 component |
| **2** | Babashka CLI: `barebuild new` (only) | `barebuild/cli/` + templates |
| **3** | Project template + shadow-cljs profile | `barebuild/templates/app/` |
| **4** | End-to-end read-only Tasks demo with Babashka backend; **collect hand-rolled write-side implementations from 3–5 users** as input for V1.1 | `barebuild/demo-app/` |

Phase 0 gates Phase 1a. Phases 1a, 1b, 2 land independently. 3 depends on 2. 4 depends on 1a + 1b + 2 + 3.

**The single biggest risk reduction in this plan is not writing the write-side elements before users have hand-rolled their own with `<barebuild-data>` + `addEventListener` + raw `fetch`.** Their solutions inform the V1.1 action/bind/invalidate-on contracts; the contracts don't preempt the solutions.

---

## Phase 0 — Validation Gate

Before any component is written:

1. Paste the **"Canonical V1 read-side pattern"** code block from §1.2 below (the HTML markup + the CLJS wiring snippet — together about 20 lines) into a static `.md` gist. **Illustrative only: no working implementation to run, no live demo site.** The gist is what you'd hand a reader to convey "this is what BareBuild apps look like." **Show only the V1 hand-wired form in code; describe the V1.1 direction in prose, with no `<barebuild-bind>` code sketch.** Showing the sketched declarative element would (a) inflate "I'd try it" toward an element V1 doesn't ship, and (b) anchor the Phase 4 "what did users build by hand" telemetry to our first guess — defeating designed-by-use. Demand-validation (Phase 0) and write-side design-validation (Phase 4) are kept decoupled (see Resolved Decision #20).
2. Post in Clojurians `#clojurescript` and `#shadow-cljs`, ClojureVerse, /r/Clojure with a one-paragraph framing: *"I'm prototyping a tiny, growable read-side scaffold for CLJS — Rails-fast iteration, not a Rails-sized surface. Would you use this instead of Reitit + re-frame + Helix for read-heavy apps? What's missing? What's wrong?"* The reframe matters: V1 ships a router, a route, and a fetcher — readers who judge against Rails' full surface will find a gap that isn't being claimed.
3. Collect 5+ responses verbatim. Look for "I'd use this" vs "I already have X."
4. **Gate criterion (two signals required, not one).** Verbal interest is necessary but not sufficient — forum readers tell strangers they'd try almost anything. Require both:
   - **Verbal:** ≥3 of 5 responses say "I'd try it" *with a named use case* (the use case rules out reflexive politeness).
   - **Committed action:** ≥3 distinct respondents take one concrete step — and **at least one must name a real project** (work, side, or OSS) where they'd try it. This is the strong signal that separates intent from politeness; pure mailing-list signups alone do not clear the gate. The other two may sign up to a `barebuild-early` mailing list (a single Google Form is fine) or open a use-case issue on a public tracking repo. One person doing all three counts as one.
   If only the verbal signal clears, **pause Phase 1 and write a 500-word "BareDOM cookbook for re-frame/Reitit users" instead** — same audience, lower commitment, find out if the gap is real before building the bridge. If neither clears, defer BareBuild indefinitely and document why in `validation-responses.md`.

Cost: 2–3 weeks realistically (posting, waiting on responses, follow-ups, decision). Benefit: not spending three months building a Rails the community didn't ask for.

Outputs saved to `barebuild/docs/validation-responses.md`, including a verbatim record of each committed-action signal (gist comment URL, +1 reaction screenshot, mailing-list signup count).

---

## Phase 1 — Orchestration Elements

### 1.1 Phase 1a: Navigation

1. **`<barebuild-router>`** — non-visual element that watches `history.pushState` / `popstate` and exposes the current `path` + `params` as properties. Listens for `barebuild-navigate` events bubbling from descendants. Listens for `barebuild-route-mounted` and the symmetric `barebuild-route-unmounted` events to maintain its route registration (no tree walk, no back-reference set on its side). **On `barebuild-route-mounted` it registers the handle *and* immediately dispatches `barebuild-route-change` at the new route with the current match**, so a late-mounted route whose `path` matches the current URL activates on registration without waiting for a navigation (no empty-flash for async-rendered routes). **On every resolution it publishes the current match by dispatching `barebuild-route-change {:path :params}` *at each registered route element*** — the router holds those handles from registration, so the route never walks the DOM upward to find the router or attach a foreign-element listener. It **also** dispatches one `barebuild-route-change` on itself (bubbling) for external observers. **Does not write to its children**: dispatching an event *at* a route handle is value-passing, not mutation — the router never sets an attribute or style on a route, and route visibility is owned by each route, not toggled by the router. **Anchor interception:** at `document` capture, **only for clicks whose `composedPath` includes an `<a data-barebuild-route>` AND that anchor's *nearest* router ancestor is this router** (i.e. no other `<barebuild-router>` appears in `composedPath` between the anchor and this router); modifier keys and `preventDefault`-already-called are honoured. No other heuristics. The nearest-router check makes both **sibling** routers (anchor in exactly one subtree) and **nested** routers (anchor's `composedPath` contains both routers, but only the inner one is nearest) compose without double-handling: exactly one router intercepts any given anchor. The full check lives in the model layer as `should-intercept?`. See [`docs/barebuild-router.md`](barebuild-router.md).

2. **`<barebuild-route path="/users/:id">`** — passive child of `barebuild-router`. **Dispatches a bubbling `barebuild-route-mounted` event on connect and a symmetric `barebuild-route-unmounted` on disconnect**; the ancestor router catches both and updates its registration. **The route — not the router — owns its visibility.** Each route listens for `barebuild-route-change` **on itself** (the router dispatches the current-match value at the route's handle; the route does not walk up to find the router) and toggles its own `display:none` ↔ `display:contents` from `match-path` against `path`. The router publishes one value (the current match) and never writes to its children — symmetric with `<barebuild-data>`'s "does not write to any child element" discipline. **Identity-preservation invariant:** the slotted body is constructed exactly once at connect and is never destroyed by the route across activation transitions. DOM/component-internal state (input `.value`, scroll position, absorbed properties) survives as a free consequence of identity preservation, not as a separately maintained invariant. Lazy/teardown variant is later. See [`docs/barebuild-route.md`](barebuild-route.md).

### 1.2 Phase 1b: Read-Side Data Broker

3. **`<barebuild-data src="/api/users">`** — fetches JSON and publishes the response as a value. Exposes one read-only `.state` property returning a plain JS object `{ phase, data, error, httpStatus }` (string `phase`; `data`/`error`/`httpStatus` present only when meaningful for the current phase). Fetches whenever `src` is set and the element is connected; reconnection re-fetches; disconnection aborts. **Manual refetch is event-driven**: the element listens on itself for `barebuild-data-refresh`. Dispatches `barebuild-data-state {:state}` on every phase transition; listeners read `event.target` for the dispatching element. **Refresh-during-loading semantics:** if `barebuild-data-refresh` (or an `src` change) fires while phase is `"loading"`, the in-flight `AbortController` aborts and a new fetch starts; phase stays `"loading"`, so **no duplicate `barebuild-data-state` event is dispatched** — the invariant "one event per phase transition, no transition without an event" holds across the abort. **Does not write to any child element.** See [`docs/barebuild-data.md`](barebuild-data.md).

**Canonical V1 read-side pattern (Phase 1b ships this):**

The broker carries no `src` in markup — it is dormant until its route activates, at which point user wiring sets `src` (re-setting it on each activation re-reads). This makes "when to read" an explicit value-in-time decision rather than a side effect of DOM construction: a multi-route app fetches only the resource it is showing, and re-reads on return.

```html
<barebuild-router>
  <barebuild-route path="/users">
    <barebuild-data></barebuild-data>   <!-- no src: dormant until its route activates -->
    <x-table></x-table>
  </barebuild-route>
</barebuild-router>
```

```clj
;; V1 exposes this glue intentionally — we want to see what shape you'd prefer
;; it took before we commit to <barebuild-bind> / <barebuild-invalidate-on> contracts.
;; Bind handles once at the composition boundary. Identity preservation
;; (Decision #11) guarantees these handles survive navigation, so the lookups
;; happen exactly once at app boot, not inside the listener. The path selector
;; couples this glue to the route's declared `path` — that coupling is V1 friction,
;; part of what Phase 4 observes.
(defonce ^js users-route (.querySelector js/document "barebuild-route[path='/users']"))
(defonce ^js users-data  (.querySelector users-route "barebuild-data"))
(defonce ^js users-table (.querySelector users-route "x-table"))

;; WHEN to read is explicit user wiring, not a consequence of mounting.
(.addEventListener users-route "barebuild-route-change"
  (fn [_] (set! (.-src users-data) "/api/users")))

;; Read the delivered value from e.detail.state — a plain JS object
;; { phase, data, error, httpStatus } with a string phase (read via JS interop,
;; not :keys destructuring — it is not a CLJS map); e.target is identity only.
(.addEventListener users-route "barebuild-data-state"
  (fn [^js e]
    (let [^js state (.. e -detail -state)]
      (when (= "loaded" (.-phase state))
        ;; <x-table> is row-composed (no `.items`); rebuild its rows from the value.
        (render-rows! users-table (.-data state))))))
```

The eager form (`src="/api/users"` baked into markup) fetches once on mount, never re-reads on reactivation (the broker stays connected across navigation per identity preservation), and fetches on load even inside never-visible routes. It is a labelled simplest-case variant for single-region or always-visible data — not the canonical multi-route shape.

The deliberate friction here is the V1 design: users write a few lines of glue, and **we observe what they actually wrote** before we commit to `<barebuild-bind>` / `<barebuild-invalidate-on>` / `<barebuild-action>` shapes in V1.1. The friction-shape we *do* teach is values-first: **bind handles once at the composition boundary, set `src` on activation, read the value from `event.detail.state` (use `event.target` only for identity), never `getElementById`.** The `querySelector` calls at module load are the floor — anything more invasive (lookups inside the listener body, document-global selectors, refs maps) is the friction shape we want to *see in the field*, not in the canonical example.

### 1.3 Implementation Notes

For each new component, create the full BareDOM three-layer triplet under `src/baredom/components/barebuild-*/` plus `src/baredom/exports/barebuild-*.cljs`.

**Registration touchpoints** (per phase): `shadow-cljs.edn` `:lib :modules` entries (NOT added to `adapter-eligible`), `package.json` exports, `src/baredom/registry.cljs` `all-registers` AND the new `adapter-eligible` positive set, `public/index.html` (new "Orchestration" category), `docs/components.md`, `README.md`. Adapter codegen scripts switch from `:adapter-skip` filter to `(filter adapter-eligible? all-registers)` as part of Phase 1a.

**`<x-form>` is NOT touched.** No model change, no x_form.cljs change, no test change.

**Pure model layers:**
- Router: `parse-path-pattern`, `match-path`, `should-intercept?` (positive opt-in: composed-path contains an `<a data-barebuild-route>` ancestor, **this router is that anchor's nearest router ancestor** — no other `<barebuild-router>` lies between the anchor and this router in `composedPath` — no modifier key, `preventDefault` not already called). The model layer is the pure `(patterns, url) → match` calculation. The router owns no authoritative state; the effect layer holds only caches/projections — a route-handle registry (kept current by `mounted`/`unmounted` events, held as a succession of immutable values, never re-walking the DOM) and the last computed match. Effect layer dispatches `barebuild-route-change` **at each registered route** on resolution (and once on itself, bubbling, for external observers), and **on `mounted` immediately pushes the current match to the newly-registered route**. Never writes attributes or styles on its children — dispatching an event at a route handle is not a mutation.
- Route: no tree-walking by the framework as discovery, and **no upward walk for subscription**. Connect-side effect dispatches the bubbling `barebuild-route-mounted` (the nearest ancestor router catches it and registers this route's handle) and `addEventListener`s `barebuild-route-change` **on itself** — the router pushes the current-match value to that handle, so the route never needs a reference to the router. Disconnect-side effect dispatches `barebuild-route-unmounted`. The self-listener is GC'd with the element; there is no foreign-element listener to remove. Pure model handles `match-path` interpretation against the route's own `path`. Visibility (the `display` toggle) is the route's own effect, driven by the model match.
- Data: model for phase transitions; pure derivation of `state` from `(src, response, error)`. No trigger attribute, no `initial-state`, no self-matching against `barebuild-invalidate`. Manual refetch arrives as a `barebuild-data-refresh` event the element listens for on itself; the effect layer translates it to "abort in-flight + re-fetch."

**Roadblocks** — addressed in per-component docs:
- Late-mounted routes → handled by each route's `connectedCallback` dispatching `barebuild-route-mounted`. No MutationObserver in `<barebuild-router>` — bubbling self-registration replaces tree-walking discovery (Decision #12).
- `fetch` abort on disconnect → `AbortController` via `du/setv-untraced!`.
- `<a>` click across shadow boundaries → router listens at `document` capture; walks `event.composedPath()` for an `<a data-barebuild-route>` ancestor of the click target.
- Test environment caveats per the `browser_test_environment_gotchas` memory — stub `js/fetch`, map-form fixtures for async, explicit dimensions for ResizeObservers.

### 1.4 Definition of Done

**Phase 0 (validation gate):**
- [ ] Gist posted to ≥2 CLJS forums; ≥5 responses collected verbatim in `barebuild/docs/validation-responses.md`.
- [ ] Decision recorded: ship Phase 1a (≥3/5 positive) or pause and write the cookbook instead.

**Phase 1a (router + route):**
- [ ] Both components exist with full three-layer pattern; `<barebuild-route>` is identity-preserving only (no `mount` attribute).
- [ ] **`<barebuild-route>` self-registers via bubbling `barebuild-route-mounted` and deregisters via the symmetric `barebuild-route-unmounted`**; test covers nested-router case (events bubble to nearest router; outer router does not also register). No back-reference set on the router side.
- [ ] **Late-mount activates immediately.** On `barebuild-route-mounted` the router registers the handle *and* dispatches one `barebuild-route-change` at the new route with the current match. Test: resolve `/users`, then append a `<barebuild-route path="/users">` after the router has resolved; assert it becomes visible on registration without any `barebuild-navigate` / `popstate`. Covers the async-rendered-route no-empty-flash case.
- [ ] **Route owns its visibility**, not the router: each route listens for `barebuild-route-change` **on itself** (the router dispatches the current-match value at the route's handle; the route does **not** walk up to find the router or attach a foreign-element listener) and toggles its own `display`. Grep-assert: `barebuild_route.cljs` contains no `parentElement`/`closest`/`querySelector` walk to locate the router; `barebuild_router.cljs` contains no attribute or style write whose target is a child route (no `setAttribute`/`set-attr!` on route elements, no `style.display` on routes — `dispatchEvent` at a route handle is allowed, it is not a mutation).
- [ ] **Anchor interception coverage:** `should-intercept?` model-layer tests cover the six branches — missing `data-barebuild-route` ancestor → no intercept; anchor present but outside this router's subtree → no intercept (sibling multi-router case); anchor inside a *nested* inner router → only the inner (nearest) router intercepts, the outer does not (nested multi-router case); modifier-key click → no intercept; `preventDefault` already called → no intercept; happy path (attribute present, this router is the anchor's nearest router ancestor, no modifiers, no preventDefault) → intercept. Integration tests: (a) two sibling `<barebuild-router>` elements on one page each only handle their own anchors; (b) a nested `<barebuild-router>` inside another — an anchor in the inner subtree navigates exactly once (inner router only), no double `barebuild-navigate`.
- [ ] **Identity-preservation invariant test:** the slotted body's element handle is `identical?` before and after a `display:none` → `display:contents` toggle. **Plus one regression-class test:** an `<input>` inside the slotted body has its `.value` preserved across the toggle. Same handle ≠ same internal DOM in the strict sense — a future refactor could keep the host handle while replacing shadow contents, and the identity assertion alone would still pass. The `.value` test closes that regression class for one cheap line.
- [ ] Registered in `baredom.registry`, `shadow-cljs.edn`, `package.json` exports, `public/index.html` (new "Orchestration" category), `docs/components.md`, `README.md`. **Not** added to `adapter-eligible`.
- [ ] `adapter-eligible` positive set published from `baredom.registry`; adapter codegen scripts switched from `:adapter-skip` filter to `(filter adapter-eligible? all-registers)`; existing adapter output is unchanged save for the source-of-truth swap.
- [ ] `clj-kondo --lint src test` clean; `npx shadow-cljs release lib` passes; `npm test` green.
- [ ] Discipline greps empty: no `deftype|atom|volatile!`; no `gobj/(get|set)\s+(el|this)`; no `querySelector` or `querySelectorAll` in any `src/baredom/components/barebuild-*/` file.
- [ ] Demo HTML scaffolded; `bb scripts/check-barebuild-boundary.bb` exits 0 (single script covers all four checks: relative-path grep, adapter-eligible filter, path-anchored `npm pack` exclusion, no `querySelector` in `barebuild-*` component sources).

**Phase 1b (data broker):**
- [ ] `<barebuild-data>` exists with full three-layer pattern.
- [ ] `<x-form>` untouched: `git diff main -- src/baredom/components/x_form/` empty.
- [ ] `.state` returns a plain JS object `{ phase, data, error, httpStatus }` (string `phase`; `data`/`error`/`httpStatus` present only when meaningful for the phase). The cached object is returned by reference: assert with **`(identical? (.-state el) (.-state el))`** between phase transitions — `=` alone would pass even if a fresh equal object were rebuilt per read, so it does not prove the "no allocation per read" property; `identical?` does. **No `.stateJs`** — grep-asserted: zero occurrences of `stateJs` in `barebuild_data.cljs` (the value is already a JS object, so no JS-view accessor is needed).
- [ ] **No identifier attribute on the element** — grep-asserted: zero occurrences of `name`, `id` (as an *observed* attribute, property accessor, or detail key) in `barebuild_data.cljs` or its model. Listeners disambiguate siblings via `event.target`.
- [ ] Phase transitions emit `barebuild-data-state` with detail `{:state}` only — grep-asserted: the dispatched detail map literal in `barebuild_data.cljs` contains no place-coupled identifier keys.
- [ ] **Canonical wiring reads the value from `event.detail.state`, not `event.target.state`** — every read-side example in `read-side.md`, `barebuild-data.md`, `barebuild-route.md`, and §1.2 reads `(.. e -detail -state)`; `event.target` appears only where a single listener must disambiguate sibling brokers by identity. (The event is a value and carries its payload; reading the live element place is the anti-pattern — see Decision #16.)
- [ ] **`src` is the sole trigger:** fetches whenever `src` is set and the element is connected; reconnection re-fetches; disconnection aborts. No `trigger-on-connect`, no `trigger-interval-ms` — grep-asserted: zero occurrences of `trigger` or `interval` in `barebuild_data.cljs` or its model.
- [ ] **Manual refetch is event-driven:** the element listens on itself for `barebuild-data-refresh` and refetches. No `refresh!` method — grep-asserted: zero `aset` or `.defineProperty` exposing a `refresh` method in `barebuild_data.cljs`.
- [ ] **Refresh-during-loading semantics tested:** firing `barebuild-data-refresh` while phase is `"loading"` aborts the in-flight controller, starts a new fetch, and **does not** dispatch a duplicate `barebuild-data-state` event (phase stays `"loading"`, so the "no transition → no event" invariant holds). A second test covers `src`-change-during-loading with the same expectation.
- [ ] **No `initial-state` attribute in V1** (deferred with SSR).
- [ ] **No `barebuild-invalidate` self-matching in V1** (deferred to V1.1 with the write-side elements).
- [ ] Abort-on-disconnect verified.
- [ ] **`docs/barebuild-data.md` enumerates the closed `phase` set** (`"idle" | "loading" | "loaded" | "error"`) — published as a const in `model.cljs` so user `case` expressions over phase do not silently miss future additions without a compile-time signal. Adding a new phase value is a breaking change and must be called out in the release notes.
- [ ] **`docs/barebuild-data.md` pins disconnect/reconnect semantics:** on disconnect, the in-flight `AbortController` aborts and `.state` is *cleared to `{ phase: "idle" }`*; on reconnect with an unchanged `src`, the element re-fetches (no replay of the prior `"loaded"` value) — this matches the "values, not places" reading: the element does not own a cache, the server does. Tested with a connect → load → disconnect → reconnect cycle that asserts exactly one `idle → loading → loaded` sequence on reconnect, no stale-replay event.
- [ ] **`docs/barebuild-data.md` documents the JS-object contract:** `.state` and the `barebuild-data-state` detail are a plain JS object, readable by **any** consumer — vanilla JS (`state.phase`) or a separately-compiled CLJS app (`(.-phase state)`). This is load-bearing, not a convenience: a CLJS persistent map would carry this module's `Keyword`/map classes, which a consumer's *own* `cljs.core` cannot read (lookups return nil) — so there is **no** `.stateJs` view because none is needed. (See Decision #6; this replaced the original CLJS-map contract after a runtime test of a real npm consumer.)
- [ ] `clj-kondo --lint src test` clean; `npx shadow-cljs release lib` passes; `npm test` green.
- [ ] Discipline greps empty (same as Phase 1a).
- [ ] Phase-1a CI checks pass; demo extended; **`barebuild/docs/read-side.md` shipped** as the V1 canonical-pattern doc (three elements only — router/route/data — wired by containment + `event.target`; no `trigger-on-connect`, no `#selector`/`id` wiring, no write-side elements). `barebuild/docs/server-state-first.md` is retained as the **vision doc** (target six-element architecture) with a banner stating it describes the V1.1 end-state and pointing at `read-side.md` for what V1 ships — it must not be cited as a V1 contract, since it would contradict the 1b grep gates (`trigger`, `id`/`name`).

---

## Phase 2 — Babashka CLI

`barebuild new <app>` driven by EDN templates under `barebuild/templates/`. CLI source under `barebuild/cli/`, `bbin`-installable. `cd barebuild && bb release` bumps `barebuild/package.json` and the template's `@vanelsas/baredom` pin.

**Out of V1:** `barebuild generate {page,component}`. Users who scaffold a project can copy an existing page file. Revisit once V1 demo authoring has named the convenient shape.

## Phase 3 — Project Template

`barebuild/templates/app/` is what `barebuild new` materialises: `deps.edn`, `shadow-cljs.edn` (`:app`), `public/index.html` containing the `<barebuild-router>`/`<barebuild-route>`/`<barebuild-data>` markup of the §1.2 canonical pattern, `src/<app>/core.cljs` with registry + the user-authored event-listener wiring, per-project `bb.edn`.

## Phase 4 — Read-Only E2E Demo + Write-Side Telemetry

`barebuild/demo-app/` — Read-only Tasks app built with `<barebuild-router>` + `<barebuild-route>` + `<barebuild-data>`. Babashka HTTP backend (`bb serve`) serves the read endpoints. **Users wire the create/update/delete paths by hand** with `addEventListener` + `fetch` — that's the data we need before designing V1.1.

**Telemetry to collect (manually, by reading user feedback):**
- How did users wire submit → fetch? What payload shape did they dispatch?
- How did users wire response → DOM update? Selectors? Direct element handles? Refs map?
- How did users invalidate stale reads after a write?
- Did anyone reach for re-frame? At what point?
- **Did users ask for a Hiccup renderer?** What shape did they sketch? (This is the gate for promoting the deferred `bb.render` work into V1.1.)

Output: [`write-side-design-notes.md`](write-side-design-notes.md), the gate for V1.1. Compare findings against the existing best-guess design in [`write-side-sketch.md`](write-side-sketch.md).

A Playwright smoke spec boots the stack, drives the read paths, asserts DOM matches server state. **Identity-preservation assertion**: `<x-table>` element handle captured at page load is the same handle across navigation events (proves no destructive re-render).

## Test Strategy

Two test roots: orchestration-component tests under `test/baredom/components/barebuild-*/` (existing browser-test runner); platform tests under `barebuild/test/` (CLI, templates, boundary scripts).

Per-component test files cover model layer (pure unit tests) and integration (connect/disconnect, events, lifecycle, abort, identity preservation).

CI: one job per top-level command (`npm test`, `cd barebuild && bb test`, `cd barebuild/demo-app && bb e2e`). The `boundary-check.yml` workflow runs on every PR by invoking the single canonical script `bb scripts/check-barebuild-boundary.bb` (which covers all four boundary checks).

## Documentation Deliverables

**Per-component reference (in `docs/`):**
- `docs/barebuild-router.md`, `docs/barebuild-route.md` (1a) — shipped.
- `docs/barebuild-data.md` (1b) — shipped.
- `docs/barebuild-action.md`, `docs/barebuild-bind.md`, `docs/barebuild-invalidate-on.md` — **already drafted as sketch references for V1.1**; not shipped as V1 contracts. See [`write-side-sketch.md`](write-side-sketch.md).

**Platform docs (in `barebuild/docs/`):**
- `barebuild/docs/validation-responses.md` (Phase 0) — verbatim user responses to the validation gate.
- `barebuild/docs/read-side.md` (1b) — **the shippable V1 canonical pattern**: three elements (router/route/data) wired by containment + `event.target`; no trigger attribute, no `#selector`/`id` wiring, no write-side elements. This is the doc the 1b DoD cites and whose code matches the grep gates.
- `barebuild/docs/server-state-first.md` — **vision doc** (target six-element architecture, V1.1 end-state); banner-marked as not-a-V1-contract and pointing at `read-side.md`. Explains *why* the architecture is shaped this way and when to reach for re-frame.
- `barebuild/docs/write-side-sketch.md` — best-guess design for the deferred V1.1 elements (action / bind / invalidate-on).
- [`write-side-design-notes.md`](write-side-design-notes.md) (Phase 4 output) — what users built by hand; gates V1.1.
- `barebuild/docs/cli.md`, `barebuild/docs/getting-started.md`, `barebuild/README.md` — Phase 2+.

**Operational doc:** [`barebuild/docs/RELEASES-BAREBUILD.md`](RELEASES-BAREBUILD.md) — release management for both products.

## Open Decisions

### Resolved

1. In-repo for V1, organised for extraction. Three-check boundary enforcement.
2. `barebuild-*` prefix for orchestration; `x-*` stays UI-only.
3. Routes declared as markup children only; `.routes` property deferred.
4. Server-state-first. No global client store.
5. `<x-form>` unchanged.
6. JSON only; GET only via `<barebuild-data>` in V1.
7. **No selectors, no Hiccup renderer, no `bb.events/on!` wrapper, no `:on` key, no write-side coordination elements in V1.** Users wire response → DOM with `addEventListener` and direct property writes. We learn from what they write.
8. **[CORRECTED — `.state` is a plain JS object, not a CLJS map; the map was unreadable across the `cljs.core` boundary for npm consumers. See Strategic Decision #6 above and `docs/barebuild-data.md`. History kept below.]** One `.state` property per fetcher, returning the persistent CLJS map directly. **No `.stateJs`** in V1 — CLJS audience is committed-to. Schema is open (keys `{:phase :data :error :http-status}` — `:data`/`:error`/`:http-status` present only when meaningful for the phase — and extensible); runtime type at leaf positions is the CLJS persistent map. "Open" here means schema-open, not language-portable byte-for-byte; the trade is named, not implicit.
9. **No trigger attribute, no method.** `<barebuild-data src="…">` fetches whenever `src` is set and the element is connected. Manual refetch is a `barebuild-data-refresh` event the element listens for on itself. Polling is `setInterval` over that dispatch in user code. The trigger concept enters the design only when a second value for it exists.
10. **No `initial-state` attribute in V1.** Ships with SSR or not at all.
11. `<barebuild-route>` invariant is **identity preservation**, not "state preservation" as a separately maintained property: the slotted body is never destroyed across activation transitions, and DOM/component-internal state survives as a free consequence. Authoritative app state lives on the server; the DOM is a projection.
12. **`<barebuild-route>` self-registers via the bubbling `barebuild-route-mounted` event and deregisters via the symmetric `barebuild-route-unmounted` event.** No tree walk inside the route; no back-reference set on the router; nested routers are emergent. The router holds the registered route handles and **pushes the current-match value by dispatching `barebuild-route-change` at each route handle** (plus once on itself, bubbling, for external observers); each route listens **on itself**, never walking up to find the router. **On `mounted`, the router both registers the handle and immediately pushes the current match to that one route**, so a late-mounted route whose `path` matches the current URL activates on registration without a navigation — no empty-flash for async-rendered routes. Each route owns its own visibility (toggles its own `display` from the pushed value); the router never writes attributes or styles to its children — dispatching an event at a known handle is value-passing, not mutation. The route-handle registry is not authoritative state: the truth of "which routes exist" is the DOM, and the registry is a cache of it held as a succession of immutable values; "which URL" is the browser's `history`/`location`; the match is a pure projection of the two. (This replaces the earlier sketch in which the route walked its `parentElement` chain to attach a `route-change` listener on the router: that re-derived an identity the router already held and left a foreign-element listener to tear down.)
13. **Anchor opt-in via `data-barebuild-route`.** No document-spanning interception of plain anchors. `should-intercept?` is a positive check; the browser keeps its native anchor semantics. **Nested routers disambiguate by "nearest router wins":** a router intercepts an anchor only when it is the anchor's nearest `<barebuild-router>` ancestor in `composedPath`, so nesting a router inside another never double-handles a click.
14. **Orchestration components excluded from framework-adapter codegen by virtue of *not appearing in* the `adapter-eligible` positive registry set.** No `:adapter-skip` flag. The positive registry ships in Phase 1a alongside the first orchestration components.
15. BareDOM and BareBuild release independently; no auto-coupling.
16. **Event-detail convention.** Every dispatched event's `detail` is a map, and **the detail is where the value lives** — listeners read the delivered value from `event.detail` (e.g. `event.detail.state`), not by reaching back into the dispatching element's live property. The event is a value; it carries its own payload. **No place-coupled identifier (`:id`, `:name`) in the detail** — listeners use `event.target` for *identity only* (which element dispatched, to disambiguate siblings under one listener / composition through containment), or listen at a scoping ancestor (e.g., the route) and inspect the target there. Reading `event.target.state` for the *value* is the anti-pattern this guards against: in a synchronous listener it returns the same cached reference as `event.detail.state`, but it teaches reaching to the place instead of reading the value. Route-change events carry URL data (`:path`, `:params`); navigation events carry the target URL; data-state events carry `{:state}`. Detail-object keys are JS-shaped strings (`state`, `path`, `params`), and the leaf *values* are JS-shaped too — `event.detail.state` is a plain JS object, and `params` is `clj->js`'d (see Decision #6). No CLJS map crosses the event boundary.
17. **Validation gate (Phase 0) precedes Phase 1.** ≥3 of 5 CLJS-community responses must say "I'd try it" before any orchestration component is written.
18. **Write side (action / bind / invalidate-on) is V1.1, not V1.** Designed-by-use from real hand-rolled implementations collected during Phase 4. Current best-guess design is preserved in `barebuild/docs/write-side-sketch.md` for comparison against findings.
19. **`<x-form>` retains its `x-` prefix as a documented exception.** It is a UI surface with incidental orchestration behaviour, predates the `barebuild-*` taxonomy, and ships in `@vanelsas/baredom` to non-BareBuild consumers — renaming would break them. One named exception is the Hickey-shaped move (name the trade); inventing a parallel `barebuild-form` to avoid the exception would complect the form surface across two elements with no functional gain.

20. **Demand-validation (Phase 0) is decoupled from write-side design-validation (Phase 4).** Phase 0 asks one question: *is there demand for this substrate* (no framework runtime, server-state-first, the component library, a non-deprecating contract)? Phase 4 asks a different one: *what shape does the write side want to take*, learned from real hand-rolled implementations. The two must not be braided: the Phase 0 gist therefore shows only the V1 hand-wired read pattern in code and describes the V1.1 direction in prose only — no `<barebuild-bind>`/`<barebuild-action>` code sketch — because a sketched contract would both inflate Phase 0 interest toward an unshipped element and anchor the Phase 4 telemetry to our first guess. Validating demand and validating design with the same artifact is a complection; this decision keeps them apart.

### Deferred to later versions

1. **Hiccup runtime renderer.** `bb.render`, `mount!`, attribute-vs-property dispatch via `registry/property-api-for`. Deferred until Phase 4 user telemetry has shown shape and demand. Until then, route bodies are markup; users assign properties directly.
2. **Write-side coordination:** `<barebuild-action>`, `<barebuild-bind>`, `<barebuild-invalidate-on>`. Designed-by-use from Phase 4 telemetry. Sketch preserved in `barebuild/docs/write-side-sketch.md`.
3. **`barebuild generate {page,component}`** CLI subcommands.
4. **`bb.events/on!`** helper (or any event-wrapper namespace).
5. **`:on` key in Hiccup** with a dispatch model (depends on the Hiccup renderer above).
6. **`<barebuild-route-lazy>`** (teardown semantics).
7. **`initial-state` attribute** on `<barebuild-data>` — ships with full SSR.
8. **Server-rendered first paint** (full SSR).
9. **Cross-instance fetch coalescing** for `<barebuild-data>` with identical `src`.
10. **Server-pushed invalidation** (SSE/WebSocket) via a `<barebuild-stream>` element.
11. **EDN payloads.**
12. **Optimistic UI.**
13. **Nested routes.**
14. **`.routes` property** (Reitit-integration escape hatch).
15. **URL pattern matching** for invalidation.
16. **File uploads** (`multipart/form-data`) and form-urlencoded body encoding.
17. **Convenience accessors** (`.data`, `.error`, `.loading?`) on top of `.state`.
18. **Persistent caching** across reloads (localStorage/sessionStorage).
19. **`<barebuild-url template="…">`** — URL-template interpolation primitive. Shape sketch in `barebuild/docs/write-side-sketch.md`.
20. **Host-scoped in-memory response cache** on `<barebuild-data>`.
21. ~~**`.stateJs` JS-view accessor** (and event-detail JS-view).~~ **Dropped — no longer needed.** `.state` and the event detail are already plain JS objects (Decision #6), readable by JS and CLJS consumers alike, so there is no CLJS map to provide a JS view *of*.
22. **Any trigger/scheduling attribute on `<barebuild-data>`** (polling, on-connect-only, on-event). Shape sketch in `barebuild/docs/write-side-sketch.md`; promote only if Phase 4 telemetry shows convergent demand.

### Still open

1. Where to document "your component absorbs deltas via property setters" for users authoring custom components — first-class section in `barebuild/docs/getting-started.md` vs. checklist in `barebuild generate component` output (when generate ships).

## Phase 1 Critical Files

**New source (1a):**
- `src/baredom/components/barebuild-router/{model.cljs, barebuild_router.cljs}`, `src/baredom/exports/barebuild-router.cljs`
- `src/baredom/components/barebuild-route/{model.cljs, barebuild_route.cljs}`, `src/baredom/exports/barebuild-route.cljs`

**New source (1b):**
- `src/baredom/components/barebuild-data/{model.cljs, barebuild_data.cljs}`, `src/baredom/exports/barebuild-data.cljs`

**Edit source (1a):**
- `src/baredom/registry.cljs` — add `adapter-eligible` positive set.
- Adapter codegen scripts — switch from `:adapter-skip` filter to `(filter adapter-eligible? all-registers)`.

**Reference (consult, do not modify):**
- `src/baredom/components/x_icon/` — render-pipeline / model-layer / registration-idiom golden sample.
- `src/baredom/components/x_tabs/` + `x_tab/` — parent / passive-child with bubbling-event registration. Consult for the registration pattern only; **do not lift its MutationObserver** — `<barebuild-router>` has no equivalent (late-mounted routes self-register via their own `connectedCallback`).

Tests, docs, demos, and CI per §1.3 and §1.4.
