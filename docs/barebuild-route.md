# barebuild-route

Passive child of `<barebuild-router>`. Holds a path pattern. **Owns its own visibility**: listens for `barebuild-route-change` **on itself** (the router pushes the current-match value to this route's handle) and toggles its own `display:none` ↔ `display:contents` based on whether `path` matches the current URL. The route never walks up the DOM to find the router — it registers by bubbling event and is subscribed by being dispatched at.

- Stateless: holds only the declared `path` pattern (parsed once into a value)
- The slotted body is constructed exactly once at connect and is never destroyed by the route across activation transitions
- No visible DOM of its own — the route element itself adds no box

---

## Tag

```html
<barebuild-route path="/users/:id">
  <h2>User detail</h2>
  <!-- slotted content -->
</barebuild-route>
```

---

## Purpose

Declares "this is the body that should be visible when the URL matches `path`." Designed to be one of many passive children under a single `<barebuild-router>`, like `x-tab` under `x-tabs`.

The route registers with its ancestor router via the bubbling `barebuild-route-mounted` event on connect. The router, holding the route's handle from that registration, pushes the current match back by dispatching `barebuild-route-change` *at* the route; the route listens on itself and decides its own visibility from that value. Registration flows up by event; the match flows down by targeted dispatch. The route holds no reference to the router and attaches no listener on a foreign element — so there is nothing to tear down on disconnect beyond its own self-listener (GC'd with the element). The router never writes attributes or styles to the route.

On disconnect the route also dispatches `barebuild-route-unmounted`, but this is **observability only**: it fires from `disconnectedCallback` when the element is already detached, so it does not bubble to the router. The router learns of removal lazily, by pruning routes that are no longer `isConnected` on its next resolution — see [`barebuild-router`](barebuild-router.md#route-registration--removal-without-tree-walking).

**Late mount activates immediately.** On `barebuild-route-mounted`, the router both registers the handle *and* immediately dispatches `barebuild-route-change` at the new route carrying the current match. A route appended after the router has already resolved the URL — common with async-rendered content — therefore activates on registration without waiting for a navigation, so there is no empty-flash for a late-mounted route whose `path` matches the current URL.

**Visibility uses `display`.** An inactive route is `display:none` (its children are removed from tab order and the accessibility tree); an active route is `display:contents` (the route element adds no box of its own). Nothing relies on a `display:contents` route being present in the accessibility tree — it is purely a layout-transparent wrapper.

---

## Installation

```js
import { init } from '@vanelsas/baredom/barebuild-route';
init();
```

Registration is idempotent.

---

## Observed Attributes

| Attribute | Type | Default | Description |
|---|---|---|---|
| `path` | string | required | Path pattern. Supports `:name` segments (e.g. `/users/:id`). Matched against the router's stripped path. Parsed once at connect into the model-layer match value. |

No `name` attribute. The route is identified by its `path`; listeners that need to disambiguate between routes match against `path` from the `barebuild-route-change` detail.

No `active` attribute. The route owns its visibility internally; there is no externally-observable activation flag on the host element. Code that needs "is this the current route?" reads `path` and compares to the router's published `.path` (or listens for `barebuild-route-change`).

---

## Properties

| Property | Type | Read-only | Description |
|---|---|---|---|
| `.path` | string | reflects attribute | |

---

## Events

### Emitted (on self, bubbling)

| Event | Detail | When |
|---|---|---|
| `barebuild-route-mounted` | `{}` | On `connectedCallback`. The nearest ancestor `<barebuild-router>` catches it and registers this route. |
| `barebuild-route-unmounted` | `{}` | On `disconnectedCallback`. **Observability only** — the element is already detached, so it does not bubble to the router; a listener *on the route* still receives it. The router deregisters lazily via `isConnected` pruning, not this event. |

### Listened for

| Event | Source | Effect |
|---|---|---|
| `barebuild-route-change` | **dispatched at this route by its ancestor router** (the route listens on itself; the router holds the route's handle from registration and pushes the match to it) | Match `event.detail.path` against `path` (via the model layer's `match-path`); toggle own `display:none` / `display:contents` accordingly. |

The route reads the value the router pushes to it; the router never *writes* to the route (no attribute, no style) — dispatching an event at the handle is value-passing, not mutation. This is symmetric with `<barebuild-data>`'s discipline of never writing to its children — one rule, two components.

---

## Identity-Preservation Invariant (V1)

The slotted body of a `<barebuild-route>` is **constructed exactly once at connect** and is **never destroyed by the route across activation transitions.** Visibility flips via `display:none` ↔ `display:contents`; the underlying DOM nodes remain.

This is a *contractual* guarantee on identity:

- A child element handle captured before deactivation is `identical?` to the handle after reactivation.

DOM/component-internal state survival is a **free consequence** of identity preservation, not a separately maintained invariant:

- Input `.value`, scroll position, properties components have absorbed — all of these live on the DOM nodes themselves. As long as the nodes survive, their internal state survives. The route does not "preserve" this state; it simply refrains from destroying the nodes that hold it.

**Honest scope.** Authoritative app state lives on the server in V1's blessed pattern (server-state-first). Refreshing a page or navigating to a route in a new tab gives you the server's current value. Identity preservation covers the ergonomic gap between transient client interaction (a half-filled form) and server truth.

**Cost.** An SPA with many routes pays N route-bodies' worth of DOM and component instances simultaneously. This is the correct default for V1's audience (single-page apps with O(10) routes, where state preservation dominates UX). Apps with hundreds of routes need different semantics — that's the deferred `<barebuild-route-lazy>` element, not an attribute knob on `<barebuild-route>`. Smuggling two contracts under one element via an attribute value is the kind of complection V1 refuses.

**Data-broker fetching inside inactive routes.** Because route bodies are constructed at connect, any `<barebuild-data>` with a static `src` inside *any* route — currently active or not — fires its fetch on page load. A 10-route SPA with one eager broker per route hits the API 10× on first paint, 9× of which paint into never-visible DOM. It also never re-reads on reactivation (the broker stays connected across navigation), so it shows boot-time data. This is the consequence of identity-preservation-by-default; the broker does not know about routes.

**The V1 canonical pattern avoids both** by omitting `src` in markup and setting it on `barebuild-route-change` (see [`barebuild-data.md`](barebuild-data.md) → *Activation-driven read*): a broker with no `src` is dormant, so only the active route fetches, and re-setting `src` on each activation re-reads. The eager `src="…"` form is a labelled simplest-case variant for single-region or always-visible data. The first-class wiring that moves this into markup (`<barebuild-url>`, `<barebuild-bind>`) is V1.1, designed-by-use from Phase 4 telemetry.

**Users who need per-activation teardown today** wrap the route body in a `<template>` and clone manually. The recipe lives in [`barebuild/docs/state-recipes.md`](../barebuild/docs/state-recipes.md).

---

## Path Pattern Syntax (V1)

| Pattern | Matches | `params` |
|---|---|---|
| `/users` | `/users` | `{}` |
| `/users/:id` | `/users/42` | `{id: "42"}` |
| `/posts/:postId/comments/:commentId` | `/posts/7/comments/3` | `{postId: "7", commentId: "3"}` |

Nested routes (a `<barebuild-route>` inside a `<barebuild-route>`) are V2. Splat segments (`*rest`) are V2.

---

## Examples

### Basic route

```html
<barebuild-router>
  <barebuild-route path="/">
    <h1>Home</h1>
  </barebuild-route>
  <barebuild-route path="/users/:id">
    <h1>User</h1>
    <p>Showing user with ID from the URL.</p>
  </barebuild-route>
</barebuild-router>
```

### Reading params from inside a route

V1 has no `<barebuild-url>` primitive to interpolate router params into a broker's `src`. The V1 recipe listens at the router and sets the broker's `.src` directly:

```html
<barebuild-router>
  <barebuild-route path="/users/:id">
    <barebuild-data></barebuild-data>
    <x-card></x-card>
  </barebuild-route>
</barebuild-router>
```

```clj
;; User-authored wiring — listen at the router, capture handles by structural scope.
;; The route-change detail is {path, params} (the resolved path, e.g. "/users/42",
;; and the extracted params). Gate on the presence of the :id param — it is present
;; only when the active route is the /users/:id pattern, so no pattern-vs-path
;; comparison is needed.
(let [router (.querySelector js/document "barebuild-router")
      route  (.querySelector router "barebuild-route[path='/users/:id']")
      data   (.querySelector route "barebuild-data")
      card   (.querySelector route "x-card")]
  (.addEventListener router "barebuild-route-change"
    (fn [^js e]
      (when-let [id (.. e -detail -params -id)]
        (set! (.-src data) (str "/api/users/" id)))))
  (.addEventListener route "barebuild-data-state"
    (fn [^js e]
      (let [{:keys [phase data]} (.. e -detail -state)]   ; value from detail
        (when (= :loaded phase)
          (set! (.-data card) data))))))
```

A `<barebuild-url template="/api/users/:id">` primitive wires this pattern by **containment** in V1.1 — placed inside a route, it reads the router's params and writes to a sibling's `src`. The exact contract is designed-by-use from Phase 4 telemetry. See [`BAREBUILD-V1-PLAN.md`](../barebuild/docs/BAREBUILD-V1-PLAN.md) Deferred V2 #19.

---

## Manual Verification

1. Mount a route with `<input type="text">` inside. Type a value.
2. Navigate to a sibling route. Original route's `display` flips to `none`.
3. Navigate back. Same `<input>` element (identity-equal). `.value` is still what was typed.
4. Open DevTools Elements panel. Confirm the inactive route's children are still present in the DOM (not removed).
5. Remove a route from the DOM programmatically, then navigate. The router's match no longer includes it (the disconnected route is pruned on the next resolution).

---

## See Also

- [`barebuild-router`](barebuild-router.md) — the parent element.
- [`barebuild/docs/state-recipes.md`](../barebuild/docs/state-recipes.md) — per-activation teardown recipe.
- [`BAREBUILD-V1-PLAN.md`](../barebuild/docs/BAREBUILD-V1-PLAN.md) — architectural rationale.
