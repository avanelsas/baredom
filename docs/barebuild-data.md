# barebuild-data

Non-visual element that performs `fetch` and publishes the response as a value. Fetches whenever `src` is set and the element is connected; aborts on disconnect. **Does not write to any child element** — V1 consumers wire response → DOM by listening for `barebuild-data-state` and reading the value from `event.detail.state` (using `event.target` only to tell sibling brokers apart).

- Stateless: cached fetch state is exposed as a value (`.state`), never as imperative API
- Framework-free
- Server-state-first: the URL is the meeting point with the write-side elements (V1.1)
- No identifier attribute: listeners disambiguate siblings via `event.target` (composition through containment), not by reading a place-coupled `id` or `name` from event detail

---

## Tag

```html
<barebuild-data src="/api/users"></barebuild-data>
```

---

## Purpose

Owns "fetch this URL and tell anyone interested what the current value is." Splits cleanly from:

- **Transport of mutations** → [`barebuild-action`](barebuild-action.md) (V1.1 sketch)
- **Wiring the result into a consumer** → [`barebuild-bind`](barebuild-bind.md) (V1.1 sketch)
- **Triggering invalidation when something changes** → [`barebuild-invalidate-on`](barebuild-invalidate-on.md) (V1.1 sketch)

This element does fetch and publishes a value. Nothing more.

---

## Installation

```js
import { init } from '@vanelsas/baredom/barebuild-data';
init();
```

Registration is idempotent.

---

## Observed Attributes

| Attribute | Type | Default | Description |
|---|---|---|---|
| `src` | string | required | URL to fetch. Setting it (initial value, attribute change, or property write) triggers a fetch when the element is connected. |

That's the entire attribute surface. No `name`, no `trigger-on-connect`, no `trigger-interval-ms`, no `initial-state`, no `cache`. Each was considered and deferred — see [`BAREBUILD-V1-PLAN.md`](../barebuild/docs/BAREBUILD-V1-PLAN.md) Open Decisions #7–#9 and Deferred V2 #7, #20, #22.

**Trigger model.** `<barebuild-data>` fetches whenever:
- `src` is present, **and**
- the element is connected to a document, **and**
- a `barebuild-data-refresh` event was dispatched on it (manual refetch), **or** `src` changed, **or** the element just connected.

There is one trigger concept (a fetch happens), not several attribute-named variants of it.

---

## Properties

| Property | Type | Read-only | Description |
|---|---|---|---|
| `.state` | JS object | yes | Current state — a plain JS object `{ phase, data, error, httpStatus }` with a **string** `phase`, returned by reference (cached, not rebuilt per read). `(identical? (.-state el) (.-state el))` is `true` between phase transitions; there is no allocation per read. `data`/`error`/`httpStatus` present only when meaningful for the phase. See [State Shape](#state-shape) and [Why a JS object](#why-a-js-object-not-a-cljs-map). |
| `.src` | string | reflects `src` attribute | Setting via the property triggers a fetch (when connected). |

No `refresh!` method. Manual refetch is event-driven:

```js
el.dispatchEvent(new CustomEvent('barebuild-data-refresh'));
```

One mechanism (events) used everywhere, not two (events for some interactions, methods for others).

---

## State Shape

A plain JS object (TypeScript-style):

```ts
{ phase:       "idle" | "loading" | "loaded" | "error"
  data?:       any      // parsed response body, present when loaded
  error?:      string   // error message, present when error
  httpStatus?: number } // present when a response carried a status
```

Read it with JS interop — `state.phase` from JS, `(.-phase state)` from CLJS:

```clojure
(let [^js state (.-state el)]
  (when (= "loaded" (.-phase state)) (.-data state)))
```

**Schema-open.** New keys may be added in future versions without breaking existing consumers; read the keys you need and ignore the rest. No closed type.

**One read-only property.** Convenience accessors (`.data`, `.error`, `.loading`) are not shipped in V1 — read the fields off `.state`. One value, one source of truth; accessors can be added later as derived views without breaking the primary contract.

**Treat `.state` as read-only.** It is a value, not a mutable place. The resting `idle` object is a shared singleton (frozen), and the live object is returned by reference between transitions — mutating it would corrupt what other reads (and other brokers) observe. Read the fields; never assign to them.

---

## Why a JS object, not a CLJS map

`.state` and the `barebuild-data-state` detail are a **plain JS object** — this is load-bearing, not a convenience.

The components ship as per-component ESM. A consuming app is shadow-compiled with its **own `cljs.core`**, separate from this module's (bundled `base.js`). A ClojureScript persistent map would carry *this module's* `Keyword` and `PersistentArrayMap` classes; the consumer's `cljs.core` has different classes, so `Keyword/-equiv` (`(instance? Keyword other)`) fails and **every key lookup returns nil** — the map is structurally present but unreadable. (This is invisible only when the consumer is compiled into the *same* build, e.g. the dev demos.)

A JS object has no such boundary: `state.phase` / `(.-phase state)` work for any consumer — vanilla JS, or a separately-compiled CLJS app. `data` is the parsed JSON (already a JS value); `phase` is a string; `httpStatus` a number. The cached object is returned by reference, so `(identical? (.-state el) (.-state el))` holds between transitions — no per-read allocation.

> **History.** V1 initially shipped `.state` as a CLJS persistent map (CLJS-first, with a JS view deferred). A runtime test of a real npm-consuming app proved it unreadable across the `cljs.core` boundary, so the contract was corrected to a JS object.

---

## Events

### Emitted (on self, bubbling + composed)

| Event | Bubbles | Detail | When |
|---|---|---|---|
| `barebuild-data-state` | yes (composed) | `{state}` — the full state object (`event.detail.state`) | Every phase transition: `idle → loading`, `loading → loaded`, `loading → error`, and on any refetch. |

The event **bubbles** (and is composed) so a single listener at the natural composition boundary — typically the enclosing `<barebuild-route>` — receives state from any `<barebuild-data>` inside it, without holding a handle to each broker. This is the "listen at a scoping ancestor" pattern; `event.target` identifies **which** broker fired. The detail carries `state` only — deliberately not a place-coupled identifier (`id`, `name`) — so disambiguation is by composition (target), not by lookup-by-name.

**Value vs. identity.** Read the delivered value from `event.detail.state` — the event is a value and carries its own payload. Use `event.target` *only* to disambiguate when one listener serves multiple sibling brokers and must know which fired. Reading `event.target.state` from a listener reaches back into the element's live place instead of reading the value the transition delivered; in a synchronous listener the two return the same cached reference, but the value-first read is `event.detail.state`.

### Listened for

| Event | Source | Effect |
|---|---|---|
| `barebuild-data-refresh` | the element itself (dispatched by user code) | Aborts any in-flight fetch and re-fetches `src`. |

No `barebuild-invalidate` self-matching in V1. URL-keyed invalidation ships with the V1.1 write-side elements; until then, users dispatch `barebuild-data-refresh` on the brokers they want to refresh.

---

## Coordination with Other Elements

`<barebuild-data>` knows nothing about other elements. V1 wiring is hand-rolled — users listen for `barebuild-data-state` at the natural composition boundary (typically the enclosing `<barebuild-route>`) and read the value from `event.detail.state` (reaching for `event.target` only to disambiguate sibling brokers). The friction-shape this teaches is intentional: values flow through events, identifiers don't.

The V1.1 sketch (deferred):

- **Wiring to a consumer:** `<barebuild-bind>` placed inside the target consumer (containment), matching the source by URL or by structural relation — exact contract designed-by-use from Phase 4 telemetry.
- **Refetching on mutation:** `<barebuild-invalidate-on>` placed inside the action that performs the mutation (containment), dispatching at document level.

There is **no global default invalidation**. Mutating `/api/tasks` does not imply invalidating `/api/tasks`. If you want that coupling, you write it. **No CSS selectors anywhere** in the framework's wiring — wires are either contained or matched by URL.

---

## Roadblocks Addressed

- **Abort on disconnect.** A broker disconnected mid-flight calls `AbortController.abort()` so no late dispatch occurs. The controller is stored via `du/setv-untraced!` (per-fetch transient state).
- **Refetch aborts the in-flight request.** `barebuild-data-refresh` aborts whatever is in flight before starting a new fetch — guaranteed-monotone phase transitions for the listener.
- **JSON parse failure.** Sets `state.phase = :error` with a descriptive `state.error`; HTTP status (if available) is still surfaced.

---

## Examples

### Activation-driven read (the canonical multi-route form)

Omit `src` in markup; set it when the route activates. This is the recommended form for any data that belongs to a specific route.

```html
<barebuild-router>
  <barebuild-route path="/users">
    <barebuild-data></barebuild-data>   <!-- no src: dormant until its route activates -->
    <x-table></x-table>
  </barebuild-route>
</barebuild-router>
```

```clj
;; V1 hand-rolled wiring — listen at the route, read the value from e.detail.state.
;; The path selector below couples this glue to the route's declared `path`:
;; change one, change the other. That coupling is the cost of hand-wiring in V1
;; and part of what Phase 4 observes before the V1.1 wiring elements are designed.
(let [route (.querySelector js/document "barebuild-route[path='/users']")
      data  (.querySelector route "barebuild-data")
      table (.querySelector route "x-table")]
  ;; WHEN to read is an explicit value-in-time decision, not a side effect of mounting.
  ;; Re-setting src on each activation re-reads — a read is an observation in time.
  ;; route-change fires at every route on every resolution — gate on the path.
  (.addEventListener route "barebuild-route-change"
    (fn [^js e]
      (when (= "/users" (.. e -detail -path))
        (set! (.-src data) "/api/users"))))
  (.addEventListener route "barebuild-data-state"
    (fn [^js e]
      (let [^js state (.. e -detail -state)]
        (when (= "loaded" (.-phase state))
          ;; render (.-data state) into your view — see read-side.md for a full
          ;; x-table example (build x-table-row/x-table-cell children from the rows)
          (render! table (.-data state)))))))
```

(This hand-roll is exactly what Phase 4 asks early users to do, so the V1.1 `barebuild-bind` contract can be designed from what they actually built.)

### Eager read (simplest case only)

```html
<barebuild-data src="/api/users"></barebuild-data>
```

A static `src` fetches once on `connectedCallback` and again only on `src` change or `barebuild-data-refresh`. Because routes preserve their bodies (they toggle `display`, they do not disconnect), a broker inside a route stays *connected* across navigation — it will **not** re-read on reactivation, and shows the value fetched at first paint until something changes `src` or dispatches a refresh. It also fetches on page load even inside routes that are not currently visible. Use the eager form only for single-region or always-visible data; for per-route data use the activation-driven form above.

### Poll every 30s

```clj
;; <barebuild-data> does not schedule. Users wire setInterval over the refresh event.
(let [el (.querySelector js/document "barebuild-data[src='/api/metrics']")]
  (js/setInterval
    #(.dispatchEvent el (js/CustomEvent. "barebuild-data-refresh"))
    30000))
```

### Read current state at any time

```clojure
;; Read the JS state object directly (interop):
(let [^js state (.-state el)]
  [(.-phase state) (.-data state) (.-error state)])
```

### Reacting to a route param change

```clj
;; When the route activates with new params, update the broker's src.
;; Setting .src triggers a re-fetch.
(let [router (.querySelector js/document "barebuild-router")
      route  (.querySelector router "barebuild-route[path='/users/:id']")
      data   (.querySelector route "barebuild-data")]
  (.addEventListener router "barebuild-route-change"
    (fn [^js e]
      (when-let [id (.. e -detail -params -id)]
        (set! (.-src data) (str "/api/users/" id))))))
```

---

## Manual Verification

1. Mount `<barebuild-data src="/api/users">`. `barebuild-data-state` fires twice: once with `{state: {phase: "loading", ...}}`, once with `{state: {phase: "loaded", ...}}`. The event detail contains **no** `id` or `name` field.
2. `.state` reflects the current state object at all times. `(identical? (.-state el) (.-state el))` is `true` between transitions.
3. Disconnect mid-flight. Network panel shows the fetch aborted.
4. `el.dispatchEvent(new CustomEvent('barebuild-data-refresh'))` — broker refetches.
5. Change `.src` via property write — broker aborts in-flight and refetches against the new URL.
6. Grep `barebuild_data.cljs`: zero matches for `stateJs`, `refresh`, `trigger`, `interval`, `initial-state`, or an observed `name`/`id` attribute. The minimal V1 surface is enforced.

---

## See Also

- [`barebuild-bind`](barebuild-bind.md) — wires this broker's state into a consumer (V1.1 sketch).
- [`barebuild-invalidate-on`](barebuild-invalidate-on.md) — triggers invalidation when something happens (V1.1 sketch).
- [`barebuild-action`](barebuild-action.md) — the write-side complement (V1.1 sketch).
- [`barebuild/docs/read-side.md`](../barebuild/docs/read-side.md) — the V1 canonical pattern (what ships).
- [`barebuild/docs/server-state-first.md`](../barebuild/docs/server-state-first.md) — the full target architecture and *why* (vision doc; V1.1 end-state).
- [`BAREBUILD-V1-PLAN.md`](../barebuild/docs/BAREBUILD-V1-PLAN.md) — architectural rationale.
