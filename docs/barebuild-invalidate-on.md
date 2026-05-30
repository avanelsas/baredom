# barebuild-invalidate-on

Non-visual element placed **as a child of a source element** (typically `<barebuild-action>`, but any element that emits state events qualifies). Listens to the source via `parentNode.addEventListener`; on match, dispatches a bubbling `barebuild-invalidate {:src}` at document level. **The single mechanism for converting "this happened" into "this URL is stale."**

- Stateless: holds only its attributes
- Framework-free
- Explicit: invalidation is always named in the markup, contained inside its source

**Phase 1c.** This element is gated on Phase 5-lite feedback — its final shape is best-guess until users have hand-rolled the write side themselves.

---

## Tag

```html
<barebuild-action name="add-user" action="/api/users" submit-event="x-form-submit">
  <x-form>…</x-form>
  <barebuild-invalidate-on when-phase="success" src="/api/users"></barebuild-invalidate-on>
</barebuild-action>
```

The invalidate-on's **parent is its source**. There is no `from=` selector.

---

## Purpose

Separates "I performed a mutation" from "this URL is now stale." Earlier drafts had `<barebuild-action>` carry an `invalidate` attribute and dispatch its own invalidate event. That complected HTTP transport with cache coordination. Even earlier drafts used a `from="#some-action"` CSS selector — that smuggled DOM-uniqueness and resolution-timing as hidden contracts. V1 uses **containment**: the invalidate-on lives *inside* its source. Moving the source moves the invalidate-on with it; the wire never silently rebinds.

Reading "when this action succeeds, refetch this URL" should be one line, inside the action, visible at a glance. That one line is `<barebuild-invalidate-on>`.

---

## Installation

```js
import { init } from '@vanelsas/baredom/barebuild-invalidate-on';
init();
```

Registration is idempotent.

---

## Observed Attributes

| Attribute | Type | Default | Description |
|---|---|---|---|
| `event` | string | `barebuild-action-state` | Event name to listen for on `parentNode`. For binding to a data broker's state: `event="barebuild-data-state"`. For binding to the router: `event="barebuild-route-change"`. |
| `when-phase` | string | (unset) | Optional matcher. When set, compared with string equality against `event.detail.state.phase`. |
| `when-name` | string | (unset) | Optional matcher. When set, compared with string equality against `event.detail.name`. |
| `src` | string | **required** | URL to invalidate. Dispatched as `event.detail.src` of the resulting `barebuild-invalidate` event. **Exact `URL.pathname` equality** is used by `<barebuild-data>` to decide if it should refetch (no wildcards in V1). |

**At least one of `when-phase` / `when-name` is required.** Omitting both logs an error and the element no-ops on every event. Setting both ANDs — the invalidate dispatches only when *both* matchers fire on the same event.

**No `from=` selector.** The source is the parent element by construction. Orphan instances (no `parentNode`, e.g. detached during a teardown) log a one-line warning and no-op. **No `querySelector` anywhere in this component's source** — grep-asserted.

---

## Behaviour

On every `event` fired by `parentNode`:

1. If `when-phase` is set: read `event.detail.state.phase`; the phase match is `(= when-phase phase)`. If `when-phase` is unset, the phase match is `true` (no constraint).
2. If `when-name` is set: read `event.detail.name`; the name match is `(= when-name name)`. If `when-name` is unset, the name match is `true` (no constraint).
3. If both matches are `true` **and at least one of `when-phase` / `when-name` is set**, dispatch a bubbling `barebuild-invalidate {src: <attribute>}` at document level.
4. Otherwise, do nothing.

That is the entire effect. The two attributes are orthogonal; the dispatch is the AND.

---

## Match Semantics

Each matcher reads exactly one well-known field of `event.detail`. Which one to set depends on the source:

| Event | Use this matcher | Typical values |
|---|---|---|
| `barebuild-action-state` | `when-phase` | `success`, `error`, `submitting`, `idle` |
| `barebuild-data-state` | `when-phase` | `loaded`, `error`, `loading`, `idle` |
| `barebuild-route-change` | `when-name` | the route's `name` attribute (e.g. `users`, `user-detail`) |
| custom event carrying `state.phase` | `when-phase` | whatever phases the source publishes |
| custom event carrying `name` | `when-name` | whatever names the source publishes |

**Two attributes, named in the markup.** Earlier drafts inferred which field to match against by looking at the event-detail shape. That was implicit polymorphism — a reader of `<barebuild-invalidate-on when="success">` couldn't tell *what* was being matched. V1 names the field in the attribute, consistent with Decision #10's two trigger attributes on `<barebuild-data>`.

---

## Properties

| Property | Type | Read-only | Description |
|---|---|---|---|
| `.event` | string | reflects `event` attribute | |
| `.whenPhase` | string | reflects `when-phase` attribute | |
| `.whenName` | string | reflects `when-name` attribute | |
| `.src` | string | reflects `src` attribute | |

---

## Events

### Emitted (on self, bubbling)

| Event | Detail | When |
|---|---|---|
| `barebuild-invalidate` | `{src: <src attribute>}` | When the configured `when-phase` / `when-name` matchers all fire on an event on `parentNode`. |

The event bubbles to `document` so any `<barebuild-data>` anywhere on the page can self-match it (URL equality, no selector).

### Listened for

| Event | Source | Effect |
|---|---|---|
| `<event>` (default `barebuild-action-state`) | `parentNode` (containment) | Applies `when-phase` / `when-name` matchers; dispatches `barebuild-invalidate` on full match. |

---

## Methods

| Method | Signature | Description |
|---|---|---|
| `invalidate!` | `()` → void | Manual trigger. Dispatches `barebuild-invalidate {src}` immediately, bypassing event listening. Useful for "user clicked refresh" buttons. |

---

## Multiple URLs from One Source

Invalidating two URLs from one action is two invalidate-on children:

```html
<barebuild-action name="reorder" action="/api/projects/42/reorder" method="POST" submit-event="x-form-submit">
  <x-form>…</x-form>
  <barebuild-invalidate-on when-phase="success" src="/api/projects/42"></barebuild-invalidate-on>
  <barebuild-invalidate-on when-phase="success" src="/api/projects/42/items"></barebuild-invalidate-on>
</barebuild-action>
```

Two lines, two named effects. No wildcards. (URL pattern matching is V2 if a real user asks.)

---

## URL Match Semantics (V1)

A `<barebuild-data src="/api/users">` refetches when it sees a `barebuild-invalidate` event whose `event.detail.src` equals its own `src` by **exact `URL.pathname` equality**:

| Invalidate `src` | Matches data `src` |
|---|---|
| `/api/users` | `/api/users` |
| `/api/users?q=foo` | `/api/users?q=foo` (query strings must match exactly if present on both sides) |
| `/api/users` | `/api/users/42` → **NO** |
| `/api/users*` | `/api/users` → **NO** (no wildcards in V1) |

If you need "invalidate this URL and everything under it," write multiple `<barebuild-invalidate-on>` children. Pattern matching is V2.

---

## Examples

### Standard CRUD

```html
<barebuild-action name="add-task" action="/api/tasks" method="POST" submit-event="x-form-submit">
  <x-form>
    <input name="title">
    <x-button type="submit" label="Add"></x-button>
  </x-form>
  <barebuild-invalidate-on when-phase="success" src="/api/tasks"></barebuild-invalidate-on>
</barebuild-action>

<barebuild-data name="tasks-data" src="/api/tasks" trigger-on-connect></barebuild-data>
```

The invalidate-on hears `barebuild-action-state` from its parent action; on `phase: "success"` it dispatches `barebuild-invalidate {src: "/api/tasks"}`; the data broker self-matches and refetches.

### Invalidate on error (rare)

```html
<barebuild-action name="flaky" action="/api/flaky" submit-event="x-form-submit">
  <x-form>…</x-form>
  <barebuild-invalidate-on when-phase="error" src="/api/health-check"></barebuild-invalidate-on>
</barebuild-action>
```

When the flaky action errors, refetch the health-check.

### Manual invalidate (no event listening)

```html
<x-button id="refresh-btn">Refresh</x-button>
<barebuild-invalidate-on id="manual-refresh" src="/api/tasks" when-name="never-matches"></barebuild-invalidate-on>
<script>
  document.querySelector('#refresh-btn').addEventListener('x-button-click', () => {
    document.querySelector('#manual-refresh').invalidate!();
  });
</script>
```

`.invalidate!()` bypasses matchers and the listener entirely — it dispatches `barebuild-invalidate` unconditionally. The `when-name` is still required (every wire is named); use a sentinel that will never match, or place the element inside a source whose events it can safely ignore. The orphan-no-op warning fires once at connect if there's no `parentNode` event source; harmless.

### Reacting to data success (rare but legal)

```html
<barebuild-data name="dependents" src="/api/dependents">
  <barebuild-invalidate-on
    event="barebuild-data-state"
    when-phase="loaded"
    src="/api/cache-warm">
  </barebuild-invalidate-on>
</barebuild-data>
```

When the dependents load, ask the cache-warm endpoint to refetch. (Use sparingly — chained invalidations can loop. The plain shape is action → data.)

### Route-activation refetch

A data broker inside a state-preserving route still fires `trigger-on-connect` at page load — so for "fetch every time the user activates this route," drop `trigger-on-connect` and place the invalidate-on **inside the router** (containment), matching against the route's name:

```html
<barebuild-router>
  <barebuild-route path="/users" name="users">
    <barebuild-data name="users-data" src="/api/users"></barebuild-data>
    <x-table>
      <barebuild-bind from-name="users-data" path="[:state :data]" prop="items"></barebuild-bind>
    </x-table>
  </barebuild-route>

  <!-- Containment: invalidate-on lives inside the router; matches when-name against barebuild-route-change.name. -->
  <barebuild-invalidate-on
    event="barebuild-route-change"
    when-name="users"
    src="/api/users">
  </barebuild-invalidate-on>
</barebuild-router>
```

Same element, two orthogonal matchers. Action-success-driven invalidation uses `when-phase`; route-activation-driven invalidation uses `when-name`. The field being matched is named in the attribute, not inferred from the event payload. The source is the parent element, by construction.

---

## Roadblocks Addressed

- **`parentNode` is null at connect.** Logged once with a "no source" warning; element no-ops on every event. Should not happen in practice (the element is a child by construction) but the diagnostic catches detached-clone misuse.
- **Listener cleanup.** Disconnect removes the event listener from `parentNode`.
- **Chained invalidations.** A `<barebuild-invalidate-on>` listening inside a data broker whose target invalidate-src is the same broker would loop forever. V1 does not detect this; users must avoid the configuration. (V2 may add a cycle-detection guard.)
- **Source moves in DOM.** Because the source IS the parent, the only way to "move" the source is to re-parent the entire `<source><invalidate-on/></source>` subtree together. The wire moves with it. No re-binding logic, no MutationObserver.

---

## Manual Verification

1. Mount the standard CRUD example. Submit the form. `barebuild-invalidate` fires at document level with `{src: "/api/tasks"}`. Any matching `<barebuild-data>` refetches.
2. Force an action error. `barebuild-invalidate` does **not** fire (because `when-phase="success"` and the action emitted `error`).
3. Remove the `<barebuild-invalidate-on>` from inside the action (move it outside). Submit. `barebuild-action-state {phase: "success"}` fires on the action, but no `barebuild-invalidate` is dispatched anywhere. Proves invalidation is opt-in *and* containment-bound.
4. Add a `<barebuild-invalidate-on>` with neither `when-phase` nor `when-name`. Submit. The console logs the missing-matcher error and no invalidate fires.
5. Grep `barebuild_invalidate_on.cljs` for `querySelector` — zero matches.
6. Grep `barebuild_invalidate_on.cljs` for `from=` (attribute handler) — zero matches.

---

## See Also

- [`barebuild-action`](barebuild-action.md) — the typical source (parent).
- [`barebuild-data`](barebuild-data.md) — the typical receiver (via `barebuild-invalidate` document-level self-match on `src`).
- [`barebuild-bind`](barebuild-bind.md) — sibling containment-based wiring element for value propagation.
- [`barebuild/docs/server-state-first.md`](../barebuild/docs/server-state-first.md) — full pattern.
