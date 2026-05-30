# Server-state-first

> [!IMPORTANT]
> **This is a vision doc — the target architecture, not the V1 contract.**
> It describes the full **six-element** end-state (router, route, data, action,
> bind, invalidate-on) that BareBuild is growing toward in V1.1. **V1 ships only
> the read-side subset** — three elements (`<barebuild-router>`,
> `<barebuild-route>`, `<barebuild-data>`), wired by **containment +
> `event.target`**, with **no `trigger-on-connect`** and **no `#selector`/`id`
> wiring**. The `<barebuild-action>` / `<barebuild-bind>` / `<barebuild-invalidate-on>`
> elements, the `trigger-on-connect` attribute, and the `id`/`#from` wiring shown
> below are **V1.1 sketches, designed-by-use from Phase 4 telemetry** — they are
> not implemented in V1 and contradict the V1 grep gates (`trigger`, `id`/`name`).
> **For what V1 actually ships and how to wire it, read
> [`read-side.md`](read-side.md).** This page exists to explain *why* the
> architecture is shaped the way it is.

> The most important page for new BareBuild users *once the write side ships*.

BareBuild's blessed architecture is **server-state-first**: the server holds the truth, the DOM is a projection of it, and the platform coordinates the two via **URL-keyed, opt-in, explicitly-wired** elements. There is no global client-state store. There is no virtual DOM. There is no auto-magic.

This page is the canonical pattern. Everything else in BareBuild is a variation on it.

---

## The six elements

| Element | One sentence |
|---|---|
| [`<barebuild-router>`](../../docs/barebuild-router.md) | Watches `history`; resolves the current URL against `<barebuild-route>` children. |
| [`<barebuild-route>`](../../docs/barebuild-route.md) | Declares "this body is visible when the URL matches `path`." |
| [`<barebuild-data>`](../../docs/barebuild-data.md) | Fetches a URL, publishes the response as a value (`.state` + `barebuild-data-state` event). Does not write to anyone. |
| [`<barebuild-action>`](../../docs/barebuild-action.md) | Wraps a submit emitter; performs HTTP transport; publishes the result as a value. Does not invalidate anyone. |
| [`<barebuild-bind>`](../../docs/barebuild-bind.md) | Wires publisher → consumer. The single mechanism for "this data should land in that component's property." |
| [`<barebuild-invalidate-on>`](../../docs/barebuild-invalidate-on.md) | Watches an event; dispatches `barebuild-invalidate {src}` when the source's state phase matches. The single mechanism for cache coordination. |

Each does one thing. They share **only** the open-event/open-data contract.

---

## The canonical pattern

A read-side data flow plus a write-side mutation, coordinated by URL:

```html
<!-- READ SIDE -->
<barebuild-data id="users-data" src="/api/users" trigger-on-connect></barebuild-data>
<barebuild-bind from="#users-data" prop="items" target="#users-table"></barebuild-bind>
<x-table id="users-table"></x-table>

<!-- WRITE SIDE -->
<barebuild-action id="add-user" action="/api/users" method="POST">
  <x-form>
    <input name="name">
    <x-button type="submit" label="Add"></x-button>
  </x-form>
</barebuild-action>
<barebuild-invalidate-on from="#add-user" src="/api/users"></barebuild-invalidate-on>
```

### What happens, step by step

1. **Mount.** `<barebuild-data>` fires its `trigger-on-connect` and fetches `/api/users`. State goes `idle → loading → loaded`.
2. **Each state transition fires `barebuild-data-state`.** `<barebuild-bind>` is listening. It reads `state.data` from the event detail and writes it to `<x-table>.items`.
3. **`<x-table>` absorbs the new items via its own render-pipeline.** The model-change guard does its job. The element instance is not destroyed.
4. **User submits the form.** `<x-form>` dispatches the cancelable `x-form-submit` with `{values}` in detail.
5. **`<barebuild-action>` catches the submit.** Calls `preventDefault()`, encodes the body, calls `fetch("POST /api/users", ...)`. State goes `idle → submitting → success`.
6. **Each state transition fires `barebuild-action-state`.** `<barebuild-invalidate-on>` is listening. On `phase === "success"`, it dispatches a bubbling `barebuild-invalidate {src: "/api/users"}` at document level.
7. **`<barebuild-data>` hears the invalidate** (it's listening at document level), sees `src` matches, refetches.
8. **Loop back to step 2.** New value lands in `<x-table>.items` via the same wire.

### What did *not* happen

- **No Hiccup re-render.** The view was never rebuilt from a description.
- **No destroyed component state.** `document.querySelector('#users-table')` is the same handle before and after the mutation.
- **No global state store.** No reducer ran. No middleware fired. No subscription contract was created.
- **No auto-coupling.** The action did not name the data broker. The data broker did not name the action. They met on the URL `/api/users`, and *only because the user opted in by writing the `<barebuild-invalidate-on src="/api/users">`*.

---

## The four rules

### 1. Coordination is URL-keyed

`<barebuild-data>` and `<barebuild-invalidate-on>` meet on URL strings (`URL.pathname` exact equality). Not on event names, not on shared registries, not on observable streams. The URL is the meeting point because the URL is the identity of the resource.

If you change a URL in one place, you change it in the other. That's working-as-intended; the alternative is a hidden coupling.

### 2. Wiring is explicit, named, and visible

Every effect that crosses an element boundary lives in markup. `<barebuild-bind>` writes a property; `<barebuild-invalidate-on>` dispatches an invalidate. Both are tags you can grep, document, and remove. No element reaches across boundaries on its own.

If you're looking at an app and wondering "how does this list refresh after I add an item," the answer is two elements above the form, both named, both with explicit `from`/`src`/`prop`/`target` attributes.

### 3. Invalidation is opt-in and named

Mutating `/api/tasks` does **not** imply invalidating `/api/tasks`. If you want that coupling, you write `<barebuild-invalidate-on src="/api/tasks">`. The default is "nothing cross-cutting happens" because the default for any framework should be "nothing surprising happens."

### 4. Components absorb deltas via property setters, never via re-mount

`<x-table>.items = newRows` triggers the table's render-pipeline. The model-change guard compares old and new, applies the delta, and stops. Component-internal state (selection, hover, sort order) is preserved.

This is the existing BareDOM idiom — every `x-*` component already works this way. BareBuild's value-write path leans on it. **There is no top-level Hiccup re-render** in the canonical pattern; Hiccup is for the initial mount.

---

## Reading state without slot-projection

Earlier drafts of BareBuild had `<barebuild-data>` automatically write its payload to "consenting" children based on a registry lookup. We removed that. The broker is a publisher; consumption is explicit:

### Option A — declarative wiring (the default)

```html
<barebuild-data id="users" src="/api/users" trigger-on-connect></barebuild-data>
<barebuild-bind from="#users" prop="items" target="#users-table"></barebuild-bind>
<x-table id="users-table"></x-table>
```

### Option B — event subscription (for non-component consumers)

```js
document.querySelector('#users').addEventListener('barebuild-data-state', (e) => {
  if (e.detail.state.phase === 'loaded') {
    // do whatever — render a chart, send to analytics, log
  }
});
```

### Option C — pull the value when you want it

```js
const { phase, data } = document.querySelector('#users').state;
```

All three are first-class. Choose whichever fits the consumer.

---

## When to reach for re-frame instead

Re-frame is a thoughtful, opinionated answer to "how do I manage rich ephemeral client-side state with a global event/effect/state model?" — and for some apps, that's exactly the right question.

V1's pitch is different: server-state-first means most state worth managing already lives on the server, and the DOM is its projection. We default to *no client store* because in our target shape there isn't enough client state to justify one.

That's a statement about V1's scope, not a verdict on re-frame's design. Apps that genuinely need re-frame's primitives — multi-step wizards, optimistic UIs, complex orchestration across concurrent flows — can opt in at the application layer with zero help or hindrance from BareBuild. See [`state-recipes.md`](state-recipes.md) for the compatibility recipe.

---

## Anti-patterns

### "Just have the action also refetch the data"

Tempting. Don't. The action's job is HTTP transport; the data broker's job is fetch coordination. Coupling them directly means:

- The action knows about the data broker (broken decomplection).
- Two pages with the same action but different data brokers need conditional logic in the action.
- You can't add a *second* data broker that also refreshes without modifying the action.

`<barebuild-invalidate-on>` solves all three: it converts "this happened" into a URL-keyed broadcast. The action stays decoupled.

### "Just store everything in a global atom"

Sometimes correct. Usually not. Server state is already global — it's on the server. Mirroring it in a client atom creates a sync problem you didn't have before. Optimistic UI is a real use case for client mirroring; routine display is not.

When you do need a client atom (e.g. a multi-step form wizard), see the re-frame recipe in [`state-recipes.md`](state-recipes.md). Use it as one piece of one feature, not as the architecture.

### "Re-render the whole subtree on data change"

Tempting if you're coming from React. Don't. Re-mounting Hiccup destroys component-internal state — input focus, scroll position, dropdown open/closed, animation in-flight. BareDOM's property-write idiom is *better* than VDOM diffing for this case because there's no diff to compute; the component's own model-change guard absorbs the delta.

`mount!` is mount-once-idempotent-on-equality in V1 precisely to refuse this anti-pattern. If you want to update a component, write to its property.

---

## See also

- Per-component reference: [`barebuild-router`](../../docs/barebuild-router.md), [`barebuild-route`](../../docs/barebuild-route.md), [`barebuild-data`](../../docs/barebuild-data.md), [`barebuild-action`](../../docs/barebuild-action.md), [`barebuild-bind`](../../docs/barebuild-bind.md), [`barebuild-invalidate-on`](../../docs/barebuild-invalidate-on.md).
- [`state-recipes.md`](state-recipes.md) — recipes for ephemeral state, teardown, re-frame compat.
- [`BAREBUILD-V1-PLAN.md`](BAREBUILD-V1-PLAN.md) — architectural rationale.
