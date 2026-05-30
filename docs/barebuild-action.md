# barebuild-action

Non-visual element that wraps a *submit emitter* (any **descendant element** dispatching a cancelable bubbling event with values readable at a configurable path in `event.detail`). Listens for the submit event from its slotted subtree (containment, not selectors), calls `preventDefault()`, performs `fetch`, and publishes the HTTP result as a value. **Does not dispatch invalidation. Does not name `x-form`.**

- Stateless: HTTP result is exposed as a value (`.state`)
- Framework-free
- Decoupled from any specific emitter: the submit-event name and the values path are data

**Phase 1c.** This element is gated on Phase 5-lite feedback — its final shape is best-guess until users have hand-rolled the write side themselves.

---

## Tag

```html
<barebuild-action name="add-task" action="/api/tasks" method="POST" submit-event="x-form-submit">
  <x-form>
    <input name="title">
    <x-button type="submit" label="Add"></x-button>
  </x-form>
  <!-- Containment: invalidate-on lives inside its source. -->
  <barebuild-invalidate-on when-phase="success" src="/api/tasks"></barebuild-invalidate-on>
</barebuild-action>
```

---

## Purpose

Owns "this URL is the target of a mutation; transport values to it and publish the result." Splits cleanly from:

- **Form serialisation** → `<x-form>` (unchanged in V1)
- **Invalidation broadcast** → [`<barebuild-invalidate-on>`](barebuild-invalidate-on.md) **as a child of this action**
- **Wiring the result into a consumer** → [`<barebuild-bind>`](barebuild-bind.md) inside the target consumer

This element does fetch and publishes a value. Nothing more.

---

## Installation

```js
import { init } from '@vanelsas/baredom/barebuild-action';
init();
```

Registration is idempotent.

---

## Observed Attributes

| Attribute | Type | Default | Description |
|---|---|---|---|
| `name` | string | **required** | **Wiring identity.** Included in every dispatched `barebuild-action-state` event under `event.detail.name`. Consumers (`<barebuild-bind from-name="…">`) match against it. |
| `action` | string | **required** | URL to which the mutation is sent. **The identity of this mutation.** |
| `method` | string | `POST` | HTTP method. Typical: `POST`, `PUT`, `PATCH`, `DELETE`. |
| `submit-event` | string | **required** | Name of the cancelable event to listen for on the slotted subtree. Any **descendant** dispatching a cancelable bubbling event of this name with a values map at `values-path` in `detail` is a valid driver. **No default; no implicit coupling to any specific emitter.** The action contains no string literal `"x-form-submit"` anywhere in its source. Wiring to `<x-form>` looks like `submit-event="x-form-submit"`; wiring to anything else looks the same with a different value. |
| `values-path` | string (EDN) | `[:values]` | EDN-literal vector indicating where in `event.detail` to find the values map. Parsed via `cljs.reader/read-string`, then `get-in`. Default `[:values]` so `<x-form>` works unconfigured. Examples: `values-path="[:payload]"`, `values-path="[:detail :form-data]"`. JSON arrays accepted (`values-path='["payload"]'`). |

**V1 is JSON-only.** The request body is always `application/json`, encoded from the values map at `values-path`. There is no `enctype` attribute. Form-urlencoded and `multipart/form-data` (file uploads) are V2.

**Why `submit-event` is required, not defaulted.** Earlier drafts defaulted `submit-event` to `"x-form-submit"`. That re-introduced exactly the kind of implicit DOM-positional coupling Decision #13 banned. The action is a transport; what it's wired to is the user's concern, named in the markup. Omitting `submit-event` logs an error and the element no-ops — never fetches.

**Why containment, not selectors.** The action listens for `submit-event` on its **host** (the action element itself), catching bubbling events from descendants. There is no `from=` selector, no ID negotiation; moving the emitter inside or outside the action moves the wire with it. This is consistent with `<barebuild-bind>` (child of target) and `<barebuild-invalidate-on>` (child of source) — every wire is either contained or matched by name. **No `querySelector` anywhere in this component.**

---

## Properties

| Property | Type | Read-only | Description |
|---|---|---|---|
| `.state` | JS object | yes | Current HTTP-result state — a plain JS object `{ phase, response, error, httpStatus }` with a string `phase`, returned by reference (`(identical? (.-state el) (.-state el))` is `true` between phase transitions; no allocation per read). See [State Shape](#state-shape); the JS-object contract (and *why* it is not a CLJS map) is identical to [`<barebuild-data>`'s](barebuild-data.md#why-a-js-object-not-a-cljs-map). |
| `.name` | string | reflects `name` attribute | |
| `.action` | string | reflects `action` attribute | |
| `.method` | string | reflects attribute | |
| `.submitEvent` | string | reflects `submit-event` attribute | |
| `.valuesPath` | string | reflects `values-path` attribute | |

**No `.stateJs`.** None is needed — `.state` is already a plain JS object readable by any consumer (vanilla JS or a separately-compiled CLJS app), for the same cross-`cljs.core`-runtime reason `<barebuild-data>` is JS-shaped (plan Decision #6).

---

## State Shape

A plain JS object (string `phase`), mirroring `<barebuild-data>`'s `.state`:

```js
{ name:       /* action's name attribute, echoed for symmetry */,
  phase:      "idle" | "submitting" | "success" | "error",
  response:   /* parsed body, present in "success" */,
  error:      /* error message, present in "error" */,
  httpStatus: /* number, present in "success" and "error" */ }
```

Keys are present only when meaningful for the phase (absent → `undefined`, not null-valued). One read-only property, fully transparent. No `.response` / `.error` / `.submitting?` convenience accessors in V1.

---

## Events

### Emitted (on self, non-bubbling)

| Event | Detail | When |
|---|---|---|
| `barebuild-action-state` | `{name, state}` — `name` echoes the action's `name` attribute; `state` is the full state map | Every phase transition: `idle → submitting`, `submitting → success`, `submitting → error`. |

The top-level `name` is what `<barebuild-bind from-name="…">` and `<barebuild-invalidate-on>` (child of this action) match against without traversing `state`.

### Listened for

| Event | Source | Effect |
|---|---|---|
| `<submit-event>` | bubbles from descendant inside the action's subtree | Calls `event.preventDefault()`, reads the values map at `values-path` from `event.detail`, JSON-encodes, calls `fetch` with `Content-Type: application/json`. |

---

## Methods

| Method | Signature | Description |
|---|---|---|
| `submit!` | `(values)` → void | Programmatic trigger. Bypasses event listening. Useful when the action is wired to something other than a form-style emitter. |

---

## The Emitter Contract

A submit emitter is **any descendant** of this action that dispatches a **cancelable, bubbling** event of the configured `submit-event` name carrying a values map at `values-path` in `event.detail`. That is the contract — fully documented here as first-class, not in passing.

| Requirement | Why |
|---|---|
| Cancelable | The action calls `preventDefault()` to stop emitter defaults (e.g. native form submit fallback). |
| Bubbling | The action listens at its host; descendant events must bubble to be caught. |
| Values map at `values-path` | The values must be a JSON-serialisable map (V1; file objects are V2). Default path is `[:values]`; configurable per attribute. |

### Wiring `<x-form>` (default path)

`<x-form>` dispatches `x-form-submit` with `{:values <map>}` in detail. Default `values-path="[:values]"` matches.

```html
<barebuild-action name="t" action="/api/things" submit-event="x-form-submit">
  <x-form>…</x-form>
</barebuild-action>
```

### Wiring a custom emitter with a different payload shape

```html
<barebuild-action name="t" action="/api/things" submit-event="my-submit" values-path="[:payload]">
  <my-custom-emitter></my-custom-emitter>
</barebuild-action>
```

`<my-custom-emitter>` dispatches `{name: "my-submit", cancelable: true, bubbles: true, detail: {payload: {…}}}`.

### Wiring a deeply nested payload

```html
<barebuild-action name="t" action="/api/things" submit-event="form-event" values-path="[:detail :form-data :values]">
  <weird-emitter></weird-emitter>
</barebuild-action>
```

Any path `get-in` can reach is valid. Missing keys at `values-path` resolve to `nil` and the action logs + no-ops (no empty-body POST).

---

## What This Element Does NOT Do

1. **Does not name `<x-form>` in its code.** There is no `default-submit-event` constant. A CI grep asserts no string literal `"x-form-submit"` appears anywhere in `barebuild_action.cljs` or its model layer.
2. **Does not dispatch `barebuild-invalidate`.** Cache invalidation is the job of [`<barebuild-invalidate-on>`](barebuild-invalidate-on.md), placed **as a child of this action** when you want the coupling.
3. **Does not write to any element.** Wiring is the job of [`<barebuild-bind>`](barebuild-bind.md), placed inside the target consumer.
4. **Does not call `querySelector`.** All wiring is containment-based (descendant emitter, child invalidate-on). Grep-asserted: zero matches in source.

---

## Nested Actions

If a `<barebuild-action>` is nested inside another, the inner action handles the submit event first and calls `preventDefault()`, stopping the bubble. Nesting works; the innermost action wins.

---

## Roadblocks Addressed

- **Abort on disconnect.** Stores `AbortController` via `du/setv-untraced!`; aborts in `disconnected!`.
- **Cancelable submit event.** The action calls `preventDefault()` so any other listener (and the emitter's default behaviour) stay out of the way. The `submit-event` *must* be cancelable; documented in the emitter contract.
- **`submit-event` rewiring.** When the attribute changes, the listener is removed from the old name and re-attached to the new one in `attributeChangedCallback`.
- **`values-path` parse failure.** Logged with the literal value; the action no-ops on each submit until a valid path is set.

---

## Examples

### Create with `<x-form>` and self-invalidation

```html
<barebuild-action name="add-task" action="/api/tasks" method="POST" submit-event="x-form-submit">
  <x-form>
    <input name="title" required>
    <x-button type="submit" label="Add"></x-button>
  </x-form>
  <barebuild-invalidate-on when-phase="success" src="/api/tasks"></barebuild-invalidate-on>
</barebuild-action>
```

On success, the contained `<barebuild-invalidate-on>` dispatches `barebuild-invalidate {src: "/api/tasks"}` at document level. Any `<barebuild-data src="/api/tasks">` self-matches and refetches.

### Delete with PUT/DELETE (no form involved)

```html
<barebuild-action name="delete-task-42" action="/api/tasks/42" method="DELETE" submit-event="action-trigger">
  <x-button id="delete-btn">Delete</x-button>
  <barebuild-invalidate-on when-phase="success" src="/api/tasks"></barebuild-invalidate-on>
</barebuild-action>
<script>
  document.querySelector('#delete-btn').addEventListener('x-button-click', (e) => {
    e.currentTarget.closest('barebuild-action').submit!({});
  });
</script>
```

`.submit!({})` bypasses event listening. The `submit-event` attribute is still required (the contract names what the action would listen for if not driven imperatively); pass `action-trigger` or any sentinel and never dispatch it.

### Custom emitter with `values-path`

```html
<barebuild-action
  name="echo"
  action="/api/echo"
  submit-event="my-submit"
  values-path="[:payload]">
  <my-emitter></my-emitter>
</barebuild-action>
<!-- <my-emitter> dispatches {name: "my-submit", cancelable: true, bubbles: true, detail: {payload: {...}}} -->
```

### Subscribe to outcome (Phase 1c — typically via `<barebuild-bind>`)

```html
<x-toast>
  <barebuild-bind
    from-name="add-task"
    event="barebuild-action-state"
    path="[:state :response :message]"
    prop="message">
  </barebuild-bind>
</x-toast>
```

Or imperatively, by handle:

```js
document.querySelector('barebuild-action[name="add-task"]')
  .addEventListener('barebuild-action-state', (e) => {
    if (e.detail.state.phase === 'success') {
      console.log('Created:', e.detail.state.response);
    }
  });
```

---

## Manual Verification

1. Mount with an `<x-form>` and `submit-event="x-form-submit"`. Submit. `barebuild-action-state` fires with `{name: "...", state: {phase: "submitting", ...}}`, then with `phase: "success"`.
2. Force a 500 response. `barebuild-action-state` fires with `phase: "error"` and `http-status: 500`.
3. Wire with `submit-event="my-submit"` + `values-path="[:payload]"` + a custom emitter. Trigger the emitter. Network panel shows the JSON body comes from `detail.payload`.
4. Omit `submit-event` entirely. Mount. Console logs an error; submitting the form has no effect (no fetch, no state transition).
5. Omit `values-path`. Default `[:values]` applies — `<x-form>` works.
6. Set `values-path="[:nope]"`. Submit. Action logs "no values at path" and no-ops; no empty POST.
7. Disconnect mid-submit. Network panel shows the fetch aborted.
8. Grep `barebuild_action.cljs` (and its `model.cljs`) for `"x-form-submit"` — zero matches.
9. Grep `barebuild_action.cljs` for `querySelector` — zero matches.
10. Grep `barebuild_action.cljs` for `stateJs` — zero matches.

---

## See Also

- [`barebuild-invalidate-on`](barebuild-invalidate-on.md) — child element that converts action success into invalidation.
- [`barebuild-bind`](barebuild-bind.md) — wires action outcome into a UI consumer.
- [`barebuild-data`](barebuild-data.md) — the read-side complement.
- [`barebuild/docs/server-state-first.md`](../barebuild/docs/server-state-first.md) — the canonical pattern.
