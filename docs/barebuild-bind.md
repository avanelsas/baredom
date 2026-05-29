# barebuild-bind

Non-visual element that subscribes to a named source's state event at document level and writes a derived value to its **parent element**'s property. **The single mechanism for wiring publisher → consumer in BareBuild.**

- Stateless: holds only its attributes; performs one effect on each event
- Framework-free
- Explicit: the user names the source (by `from-name`) and contains the bind inside the target

**Phase 1c.** This element is gated on Phase 5-lite feedback — its final shape is best-guess until users have hand-rolled the write side themselves.

---

## Tag

```html
<x-table>
  <barebuild-bind from-name="users-data" path="[:state :data]" prop="items"></barebuild-bind>
</x-table>
```

The bind's **parent is its target**. The bind's **source is matched by name**. No selectors.

---

## Purpose

Replaces slot-projection. Earlier drafts had `<barebuild-data>` reach across DOM boundaries and write to consenting children's properties based on a registry lookup. Subsequent drafts used CSS selectors (`from="#data"` / `target="#table"`) — that smuggled DOM uniqueness, shadow-root scope, and resolution timing as hidden contracts. `<barebuild-bind>` now makes the wire **explicit and honest**:

- **Target = `parentNode`.** Containment is honest. Moving the bind moves the wire with it. There is no separate "target" attribute to drift out of sync.
- **Source = `from-name` in event detail.** Names are values, not paths. Brokers publish `{:name :state}`; binds filter by name. The broker can sit anywhere in the document; the wire still works.

Reading "this broker's `data` writes to this table's `items`" should be one line, inside the table, visible at a glance. That one line is `<barebuild-bind>`.

---

## Installation

```js
import { init } from '@vanelsas/baredom/barebuild-bind';
init();
```

Registration is idempotent.

---

## Observed Attributes

| Attribute | Type | Default | Description |
|---|---|---|---|
| `from-name` | string | **required** | Match against `event.detail.name` on the document-level state event. No CSS selector, no DOM lookup — pure value match. Omitting it logs an error and the bind no-ops. |
| `event` | string | `barebuild-data-state` | Name of the event to listen for at `document` level. For binding to an action's outcome: `event="barebuild-action-state"`. |
| `path` | string (EDN vector) | `[:state :data]` | **Literal EDN vector** parsed via `cljs.reader/read-string`, then `get-in` over `event.detail`. Default reads the broker's payload. JSON arrays accepted (`path='["state","data"]'`). See [Path Resolution](#path-resolution). |
| `prop` | string | **required** | Property name to write on `parentNode`. Omitting it logs an error and the bind no-ops. |

**No `from=` / `target=` selectors.** Earlier drafts had both — that gave hidden DOM-uniqueness contracts and silent breakage when elements moved. V1 refuses the indirection. The bind's target is its parent (containment, honest); the bind's source is named in the event detail (value, honest). **`querySelector` does not appear anywhere in this component's source** — grep-asserted.

---

## Path Resolution

`path` is a **literal EDN vector**, parsed once at connect via `cljs.reader/read-string`, then applied via `get-in` to `event.detail`.

| `path` example | Reads | Typical `prop` |
|---|---|---|
| `[:state]` | the whole state map | rare; consumer absorbs full snapshot |
| `[:state :data]` (default) | parsed response payload | `items`, `value`, … |
| `[:state :data :users 0 :name]` | nested traversal; integer is vector index, keyword is map key | `firstUserName` |
| `[:state :error]` | error string | `errorMessage` |
| `[:state :phase]` | one of `idle`/`loading`/`loaded`/`error` | `loadingPhase` |
| `[:state :http-status]` | HTTP status number | `httpStatus` |
| `["state" "data"]` | JSON-array form for vanilla-JS callers | works identically |

**Why EDN literals, not dotted strings.** A dotted string like `"state.data.users.0.name"` requires a parser (segment grammar, integer-vs-keyword heuristic, dot-escaping) and is implicitly a DSL — the plan rejects DSLs (Decision #10). EDN literals round-trip losslessly through `read-string`, support both CLJS and JSON shapes, and have no edge cases (key contains `.`? Just write `[:foo.bar]`).

**Parse failures** are logged at connect (with the literal value) and the bind no-ops on every event until `path` is valid.

**Missing key semantics.** If `get-in` bottoms out before reaching a leaf, it returns `nil` and the bind no-ops: the target keeps its previous value. Writing `nil` explicitly is V2 if a real user names the need.

---

## Behaviour

On every `event` fired at `document` level:

1. Check `event.detail.name === from-name`. If not, ignore.
2. Apply `path` (EDN vector, pre-parsed at connect) to `event.detail` via `get-in`.
3. If the result is non-`nil`, write to `parentNode[prop]` via `du/setv!` (so the trace recorder sees it).
4. If `parentNode` is `null` (orphan bind), log once and no-op.
5. **No registry consultation.** No opt-in checks. The user contained the bind inside the consumer; the consumer is opting in by location.

---

## Properties

| Property | Type | Read-only | Description |
|---|---|---|---|
| `.fromName` | string | reflects `from-name` attribute | |
| `.event` | string | reflects `event` attribute | |
| `.path` | string | reflects `path` attribute | |
| `.prop` | string | reflects `prop` attribute | |

---

## Events

`<barebuild-bind>` emits no events.

---

## Multiple Binds, Same Consumer

Wiring three signals from one broker into one consumer is three binds — all inside the consumer:

```html
<x-table>
  <barebuild-bind from-name="users-data" path="[:state :data]"   prop="items"></barebuild-bind>
  <barebuild-bind from-name="users-data" path="[:state :phase]"  prop="loadingPhase"></barebuild-bind>
  <barebuild-bind from-name="users-data" path="[:state :error]"  prop="errorMessage"></barebuild-bind>
</x-table>

<barebuild-data name="users-data" src="/api/users" trigger-on-connect></barebuild-data>
```

Three lines, three named effects. No magic. The consumer (`<x-table>`) needs `loadingPhase` and `errorMessage` declared in its `property-api` to absorb them — but that's an opt-in by the consumer's *author*, not by the broker. Note the broker can sit anywhere in the document — the wire is by name, not by position.

---

## Identity Preservation

Writing to a property invokes the consumer's existing render-pipeline (the model-change guard from CLAUDE.md). The consumer element itself is never destroyed or replaced. The handle captured before the bind writes is `===` to the handle after.

---

## Cross-Cutting Wiring

Because `from-name` is a value (not a position), the bind's source and the bind itself don't need to share a parent — they don't even need to share a subtree. The bind only needs to be inside its target:

```html
<header>
  <barebuild-data name="user-data" src="/api/me" trigger-on-connect></barebuild-data>
</header>

<aside>
  <x-avatar>
    <barebuild-bind from-name="user-data" path="[:state :data :avatarUrl]" prop="src"></barebuild-bind>
  </x-avatar>
</aside>
```

The bind sits inside its target `<x-avatar>`. The broker sits in `<header>`. The wire works because brokers dispatch `barebuild-data-state` to document level (bubbling), and the bind listens at document filtering by `from-name`.

---

## Examples

### Basic wire

```html
<barebuild-data name="users" src="/api/users" trigger-on-connect></barebuild-data>

<x-table>
  <barebuild-bind from-name="users" path="[:state :data]" prop="items"></barebuild-bind>
</x-table>
```

(`path="[:state :data]"` is the default; the line above writes it explicitly for clarity.)

### Reading nested data

```html
<barebuild-data name="api" src="/api/me" trigger-on-connect></barebuild-data>

<x-text>
  <barebuild-bind from-name="api" path="[:state :data :profile :displayName]" prop="label"></barebuild-bind>
</x-text>
```

The path traverses the response map; if any segment resolves to `nil` (e.g. the response shape changes or the user has no `profile`), the bind no-ops and the target keeps its last value.

Two complementary patterns when the consumer needs to render multiple derived views of the same payload:

1. **Multiple binds, one consumer.** Each bind names its path; the consumer absorbs each property independently.
2. **Project server-side.** If the API can return the shape the UI wants directly, the bind shrinks to `path="[:state :data]"`. V1's server-state-first stance encourages this where it's natural.

### Wiring an action's outcome

```html
<barebuild-action name="add-task" action="/api/tasks" submit-event="x-form-submit">
  <x-form>…</x-form>
</barebuild-action>

<x-toast>
  <barebuild-bind
    from-name="add-task"
    event="barebuild-action-state"
    path="[:state :response :message]"
    prop="message">
  </barebuild-bind>
</x-toast>
```

The toast sits anywhere; the bind inside it filters action events by `from-name`. No selector negotiation, no DOM-position assumption.

### Vanilla-JS callers using JSON arrays

```html
<x-table>
  <barebuild-bind from-name="users" path='["state","data"]' prop="items"></barebuild-bind>
</x-table>
```

`cljs.reader/read-string` accepts JSON arrays as valid EDN data. No special-case parser.

---

## Roadblocks Addressed

- **Path traversal hits `nil`.** `get-in` returns `nil`; the bind no-ops; the target keeps its last value. This is the V1 default; explicit `nil` writes are V2 (see plan Deferred #17).
- **Bind appended after broker has already published.** The bind missed the event. Either (a) the broker is `trigger-on-connect` and re-fires soon, or (b) call `refresh!()` on the broker. The bind does not eagerly read `.state` at connect by design — that would make wire-up implicit on broker presence, re-introducing a positional contract.
- **Listener removal on disconnect.** Bind disconnect removes its document-level event listener. No MutationObserver — there is no DOM lookup to maintain.
- **`path` parse failure.** Logged once at connect with the literal value; bind no-ops until corrected.
- **Orphan bind (no `parentNode`).** Logged once; no-ops. Should not happen in practice (binds are children by construction) but the diagnostic catches the rare misuse.

---

## Manual Verification

1. Mount the basic wire example. Reload. `<x-table>.items` is set to the fetched users array.
2. Force a refetch (`document.querySelector('barebuild-data[name="users"]').refresh!()`). `<x-table>.items` updates. Element handle is unchanged (`===` before and after).
3. Move the `<barebuild-data>` element to a different part of the DOM (cut/paste). Bind continues to write because matching is by name in event detail, not by position.
4. Wire `path="[:state :phase]"` and `prop="loadingPhase"`. As the broker fetches, the consumer's `loadingPhase` property cycles through `"loading"` → `"loaded"`.
5. Mount with `path="not-a-vector"`. Console logs the parse failure; bind no-ops on every event.
6. Mount without `from-name`. Console logs the missing-attr error; bind no-ops.
7. Grep `barebuild_bind.cljs` for `querySelector` — zero matches.
8. Grep `barebuild_bind.cljs` for `target=` or `from=` — zero matches (no selector attributes).

---

## See Also

- [`barebuild-data`](barebuild-data.md) — typical source.
- [`barebuild-action`](barebuild-action.md) — also a valid source (use `event="barebuild-action-state"`).
- [`barebuild-invalidate-on`](barebuild-invalidate-on.md) — sibling containment-based wiring element for invalidation.
- [`barebuild/docs/server-state-first.md`](../barebuild/docs/server-state-first.md) — full pattern.
