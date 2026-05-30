# barebuild-router

Non-visual orchestration element. The single place that touches `history.*`. Watches `popstate`, intercepts opt-in `<a data-barebuild-route>` clicks at `document` capture, maintains a registry of child `<barebuild-route>` element handles (populated by bubbling self-registration), and publishes the active route as a value.

- Owns **no authoritative state**. The two sources of truth live elsewhere: the current URL in `history`/`location` (browser-owned, read fresh each resolution) and the set of routes in the DOM tree (DOM-owned). The router holds only *caches/projections* of these — a registry of route handles (populated by the bubbling `mounted` event; **lazily validated against `isConnected`** rather than re-walking the DOM) and the last computed match (a pure projection, memoized). The registry is held as a succession of immutable values (read → `conj`/filter → write), not in-place mutation; the handles are the cached DOM node handles BareDOM's stateless doctrine permits. Removed routes are pruned on the next resolution — see [Route Registration & Removal](#route-registration-without-tree-walking).
- All routing **decisions** are pure functions in the model layer: `match-path`, `parse-path-pattern`, `should-intercept?`. The element is the effect shell (`history.pushState`, anchor interception, event dispatch); the calculation is the pure `(patterns, url) → match` function, callable and testable on its own.
- Framework-free
- **Does not write to its children.** On each resolution it **pushes** the current match by dispatching `barebuild-route-change` *at each registered route handle* (value-passing, not mutation — no attribute or style write); each `<barebuild-route>` owns its own visibility, derived from that value.
- **Multi-router pages compose by "nearest router wins."** Sibling and nested routers each handle only their own anchors; an anchor is intercepted by exactly one router — its nearest `<barebuild-router>` ancestor.

---

## Tag

```html
<barebuild-router>
  <barebuild-route path="/">…</barebuild-route>
  <barebuild-route path="/users/:id">…</barebuild-route>
</barebuild-router>
```

---

## Purpose

Owns the URL-as-data. Every other BareBuild element treats the URL as the meeting point — intercepted `<a data-barebuild-route>` clicks request navigation; routes read the current match. The router is where `history.pushState` happens, and only there.

---

## Installation

```js
import { init } from '@vanelsas/baredom/barebuild-router';
init();
```

Registration is idempotent.

---

## Observed Attributes

| Attribute | Type | Default | Description |
|---|---|---|---|
| `base` | string | `""` | URL prefix stripped before matching. `<barebuild-router base="/app">` matches `/app/users/42` as `/users/42`. |

No `intercept-anchors` attribute. Interception is opt-in per anchor via `data-barebuild-route` (see [Anchor Interception](#anchor-interception)); there is no top-level kill switch because there is no top-level default to switch off.

No `name` attribute. Listeners disambiguate routers via `event.target` or DOM containment, not by reading a place-coupled identifier from event detail.

---

## Properties

| Property | Type | Read-only | Description |
|---|---|---|---|
| `.path` | string | yes | Current resolved path (after `base` stripping). |
| `.params` | object | yes | Path parameters from the active route's pattern (e.g. `{id: "42"}` for `/users/:id`). |

> **`.params` returns a shared object — treat it as immutable.** The same object backs the `.params` getter and every `barebuild-route-change` detail, and the router reuses it until the match changes. Mutating it (or `event.detail.params`) corrupts the value the next route push reads. Clone before mutating: `{ ...router.params }`.

---

## Events

### Emitted

| Event | Target | Bubbles | Detail | When |
|---|---|---|---|---|
| `barebuild-route-change` | **each registered route** (one dispatch per route) | no | `{path, params}` | The router pushes the current match to each route's handle so the route can toggle its own visibility — the route listens **on itself** and never walks up to find the router. |
| `barebuild-route-change` | **the router itself** | yes | `{path, params}` | One additional dispatch on the router, for external observers that want a single route-change signal (e.g. analytics, a title updater). |

Why two dispatch targets for one event: the router owns the route relationship in both directions (registration *and* match-push), so the route never needs a reference to the router or a foreign-element listener. External observers listen on the router; routes listen on themselves. `path` and `params` are the published value; listeners read `event.target` for the element reference.

### Listened for

| Event | Source | Effect |
|---|---|---|
| `barebuild-navigate` | bubbling from descendants | Calls `history.pushState` with the detail's `path`; triggers resolution and dispatches `barebuild-route-change`. |
| `popstate` | window | Triggers resolution. |
| `click` | document, capture phase | Runs `should-intercept?` (see [Anchor Interception](#anchor-interception)). On positive: `event.preventDefault()`, then dispatches `barebuild-navigate` with the anchor's resolved path. |
| `barebuild-route-mounted` | bubbling from descendant `<barebuild-route>` | Registers the route in the router's route table **and immediately dispatches `barebuild-route-change` at the new route with the current match**, so a late-mounted route whose `path` matches the current URL activates on registration without waiting for a navigation. Composes through events; never walks the DOM tree. |
| _(route removal)_ | — | **Not event-driven.** A route's `barebuild-route-unmounted` fires from `disconnectedCallback`, when the element is already detached, so it cannot bubble to this router. Deregistration is lazy instead: a disconnected route is skipped on read and pruned from the registry on the next resolution (`isConnected`). |

### Programmatic navigation

There is no `navigate!` method. Programmatic navigation is event-driven, the same shape descendants use:

```js
router.dispatchEvent(new CustomEvent('barebuild-navigate', {
  detail: { path: '/users/42' },
  bubbles: true,
}));
```

One mechanism, not two; method-on-place duplicates the event channel without adding capability.

---

## Anchor Interception

The router intercepts `<a>` clicks at `document` capture (`event.composedPath()` walks through shadow boundaries). **Interception is opt-in per anchor**: an anchor is intercepted only if its composed path includes an `<a data-barebuild-route>` ancestor. The router never shadows native browser anchor behaviour for links the user did not mark.

Decisions made by the **`should-intercept?`** pure model-layer predicate. Each branch is independently testable.

| Condition | Intercept? |
|---|---|
| Composed path contains an `<a data-barebuild-route>` ancestor, **this router is that anchor's nearest router ancestor**, primary button, no modifier key, `defaultPrevented` is false | **yes** |
| No `<a data-barebuild-route>` in composed path | no |
| Anchor present but in a *sibling* router's subtree (this router not in its composed path) | no |
| Anchor present in a *nested* inner router's subtree (another `<barebuild-router>` lies between the anchor and this router in composed path) | no — only the nearest (inner) router intercepts |
| Modifier key pressed (`metaKey`, `ctrlKey`, `shiftKey`, `altKey`) | no |
| `event.button !== 0` (right/middle click) | no |
| `event.defaultPrevented` already true | no |

**Nearest router wins.** When routers nest, an anchor's composed path contains *both* routers. The `should-intercept?` predicate counts as a positive only when no other `<barebuild-router>` sits between the anchor and this router — so exactly one router (the innermost containing the anchor) handles the click. No double-navigation.

Same-origin / scheme / `target` / `download` / `rel="external"` are not the router's concern: the user opts in *per link they want intercepted*. If a link is marked `data-barebuild-route` but points to an external origin or a non-HTTP scheme, the router still dispatches `barebuild-navigate` with that path — and `history.pushState` will throw on the external origin. That's the user's miswiring, not a heuristic the router needs to second-guess.

### Why opt-in

Earlier drafts intercepted by default with a long hard-skip list (`mailto:`, `target=_blank`, `rel=external`, …) and a per-link opt-out. That complects two things: "this is a navigable link in my app" and "this is a link the browser should handle." Marking the SPA-navigable links opt-in keeps native anchor semantics untouched everywhere else, and reduces a maintenance-prone heuristic table to a single positive check.

---

## Multi-Router Composition (V1)

Multiple routers — **sibling or nested** — compose in V1 without coordination:

- **Registration** is by `barebuild-route-mounted`, which stops at the *nearest* ancestor router. Outer routers do not register an inner router's routes.
- **Match-push** is targeted: a router dispatches `barebuild-route-change` only at *its own* registered route handles, so an inner router's routes never receive an outer router's match.
- **Anchor interception** is "nearest router wins" (see [Anchor Interception](#anchor-interception)): an anchor is handled by exactly one router — its nearest `<barebuild-router>` ancestor — so nesting never double-navigates.

Each router is a self-contained routing scope. An embedded micro-frontend with its own router inside an app-level router works as a free consequence of these three rules; no router knows another exists.

---

## Route Registration & Removal Without Tree-Walking

The router does not walk its child tree to discover routes.

**Registration is event-driven.** On `connectedCallback`, a route dispatches a bubbling `barebuild-route-mounted` event; the router catches it (at the nearest ancestor router, via `stopPropagation`) and adds the handle to its registry. This survives shadow boundaries (events compose), survives late-inserted children (mounted dispatches whenever it happens), and removes the need for a MutationObserver.

On `mounted`, the router not only registers the handle but immediately pushes the current match to the new route (a single targeted `barebuild-route-change`). The router already holds the current match as a cached projection, so this is just "hand the current value to a new subscriber" — a late-mounted matching route activates without a navigation, and there is no empty-flash for async-rendered routes.

**Removal is lazy, not event-driven.** A route does dispatch `barebuild-route-unmounted` on `disconnectedCallback`, but by then the element is detached, so that event **cannot bubble to the router** — it is observability only (a listener *on the route* still receives it). The router therefore never relies on it. Instead the registry is a cache of DOM truth, validated against `isConnected`: a disconnected route is skipped when computing the active match and pruned from the registry on the next resolution. A route re-inserted into the DOM re-registers via its fresh `mounted`. (This is the same constraint that makes `x-tabs` use a MutationObserver; here the lazy-validation approach keeps the registry honest without one.)

**One V1 consequence:** because removal isn't signalled, the router does not eagerly re-resolve when a route is removed. If you remove the *currently-active* route, the router's `.path` / `.params` keep their previous value until the next navigation or `popstate` recomputes them. The removed route's DOM is gone immediately; only the router's published projection lags by one resolution.

---

## Roadblocks Addressed

- **`<a>` click across shadow boundaries.** `event.target` is retargeted in shadow DOM. The router listens at `document` capture and walks `event.composedPath()` for an `<a data-barebuild-route>` ancestor.
- **Late-mounted routes.** A route appended after the router connects still self-registers via `barebuild-route-mounted` on its own `connectedCallback`. No discovery race.
- **Router does not write to children.** Visibility is the route's responsibility — see [`barebuild-route`](barebuild-route.md). Symmetric with `<barebuild-data>`'s discipline of never writing to its children; one rule, two components.

---

## Examples

### Basic two-route app

```html
<barebuild-router>
  <barebuild-route path="/">
    <h1>Welcome</h1>
    <a href="/users" data-barebuild-route>Users</a>   <!-- intercepted -->
    <a href="/help">Help</a>                          <!-- native; router doesn't touch -->
  </barebuild-route>
  <barebuild-route path="/users">
    <barebuild-data src="/api/users"></barebuild-data>
    <x-table></x-table>
  </barebuild-route>
</barebuild-router>
```

### Reacting to route changes from outside the router

```js
document.querySelector('barebuild-router')
  .addEventListener('barebuild-route-change', (e) => {
    console.log('Now at', e.detail.path, e.detail.params);
  });
```

### Programmatic navigation (event-driven)

```js
document.querySelector('barebuild-router').dispatchEvent(
  new CustomEvent('barebuild-navigate', {
    detail: { path: '/users/42' },
    bubbles: true,
  })
);
```

---

## Manual Verification

1. `npx shadow-cljs watch app`; open `demo/barebuild-router.html`.
2. Click an `<a href="/users" data-barebuild-route>`. URL updates without page reload; `barebuild-route-change` fires with `{path: "/users", params: {}}`.
3. Click an `<a href="/help">` (no `data-barebuild-route`). Full page reload — router did not intercept.
4. Cmd-click the intercepted link. Browser opens a new tab — modifier key honoured.
5. Browser back/forward fires `barebuild-route-change` with the historical path.
6. Append a `<barebuild-route>` to the router after page load. Navigate to its path. It activates — proves bubbling self-registration handles late insertion.
7. Remove a route from the DOM, then navigate. The router no longer matches its path (the disconnected route is pruned on the next resolution).

---

## See Also

- [`barebuild-route`](barebuild-route.md) — the passive child element; owns its own visibility.
- [`barebuild-data`](barebuild-data.md) — typically nested inside routes.
- [`BAREBUILD-V1-PLAN.md`](../barebuild/docs/BAREBUILD-V1-PLAN.md) — architectural rationale.
