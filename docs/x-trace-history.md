# x-trace-history

`x-trace-history` is BareDOM's dev-only debugger. It records every
CustomEvent dispatch, every observed attribute change, every
instance-field write, and every lifecycle callback, then surfaces them
as a navigable timeline with cause→effect navigation.

Activate by adding `?baredom-trace-history` to the URL or setting
`window.BAREDOM_TRACE_HISTORY = true` before the app boots. When
inactive the recorder pays only a single nil-check per hook site and no
per-event overhead — there is no production cost when off.

- [Quick start](#quick-start)
- [Activation](#activation)
- [The dock](#the-dock)
- [Console API](#console-api)
- [Capture and share a bug report](#capture-and-share-a-bug-report)
- [Recording sessions](#recording-sessions)
- [Import / export](#import--export)
- [Adapter notes](#adapter-notes)
- [Causality](#causality)
- [Performance contract](#performance-contract)
- [JSON schema](#json-schema)

## Quick start

`x-trace-history` ships in the same npm package as the rest of BareDOM
and as a self-hostable ES module under `dist/`. Pick whichever matches
how the rest of your app loads BareDOM — both paths use the same
activation signals and the same console API.

**Via npm (most apps):**

```js
import "@vanelsas/baredom/x-trace-history";
```

**Via self-hosted ES module:**

```html
<script type="module" src="/dist/x-trace-history.js"></script>
```

Then visit the page with `?baredom-trace-history` appended to the URL.
A floating dock attaches to the right edge of the viewport, recording
every event the app produces.

See [`installation.md`](./installation.md) for the full set of import
paths supported by the rest of the library — the same options apply
here.

## Activation

The recorder activates on any of three signals — checked in this
order:

| Signal | When to use |
|---|---|
| `?baredom-trace-history` URL parameter | Casual debugging. No code changes. |
| `window.BAREDOM_TRACE_HISTORY = true` set before app boot | CI / E2E test harnesses; consumers that want the dock on every page. |
| `window.BAREDOM_TRACE_HISTORY = "raw"` (or `?baredom-trace-history=raw`) | Forensic mode — disables the sample-rate cap and shows `state/*` records by default. Use when investigating high-frequency animation components. |

The recorder pays zero per-event cost when no signal is set. The
nil-check call sites are present in the compiled code unconditionally,
but the hook atoms stay nil unless `install!` runs — and `install!`
only runs when one of the signals above is true. The dock element is
not mounted and `window.BareDOM.traceHistory` is not installed.

### Capacity override

The ring buffer holds 5000 records by default. To raise the cap, set
`window.BAREDOM_TRACE_HISTORY_CAPACITY` to a positive integer before
app boot:

```html
<script>
  window.BAREDOM_TRACE_HISTORY = true;
  window.BAREDOM_TRACE_HISTORY_CAPACITY = 20000;
</script>
```

When the buffer fills, the oldest record is dropped. Sessions and
imports are unaffected — they have their own storage.

## The dock

The dock is a floating panel mounted to `<body>` when the recorder
activates. Its anatomy, top to bottom:

| Region | Purpose |
|---|---|
| Toolbar | Pause / Resume · Record (start a session) · Clear · Export · Import · live record count |
| Session chips | One chip per session and one per import, plus the always-on **Live** view. Click to switch the timeline source. |
| Filters | Tag dropdown · category checkboxes (events, state, DOM, lifecycle) · axis-mode toggle (Order / Time) |
| Timeline | One horizontal lane per component instance. Dots are coloured by category. Hover for tooltip; click to select. |
| Splitter | Drag to resize the detail pane. |
| Detail pane | Pretty-printed JSON for the selected record, plus **Caused by** / **Effects** links. |
| Hint line | Record count, time-bounds, lane count. |

Keyboard:

- **Left / Right** while the timeline has focus — step the scrubber by
  one record.
- Click a lane label to filter the timeline to that component
  instance. Click again to clear.
- Click anywhere in the timeline to focus it.

## Console API

Every method below is available at `window.BareDOM.traceHistory.*`
when the recorder activates. The TypeScript types are published
alongside the runtime; consumers can `import type {
BareDOMTraceHistory } from '@vanelsas/baredom/x-trace-history'`.

| Method | Returns | Notes |
|---|---|---|
| `records()` | `TraceRecord[]` | All records, oldest first. Reference-equal to the recorder's internal cache — treat as read-only. |
| `components()` | `{ [id]: { tag, firstSeen } }` | Stable componentId → tag map. Monotonic for the page lifetime. |
| `pause()` | `void` | Stop accepting new records. |
| `resume()` | `void` | Resume after pause. |
| `clear()` | `void` | Drop live records and sessions. Imports survive. |
| `startSession()` | `number` | Begin a bounded recording slice. Returns the new session id. |
| `stopSession()` | `void` | Close the active session. |
| `sessions()` | `TraceSession[]` | Metadata for every captured session. |
| `sessionRecords(id)` | `TraceRecord[]` | Records inside the named session, chronological. |
| `export()` | `TraceEnvelope` | Materialise the current state as a JSON-serialisable envelope. |
| `download()` | `void` | Trigger a browser download of the current envelope as a `.trace.json` file. |
| `import(input, label?)` | `number \| null` | Load a previously-exported envelope. Pass a parsed object or a JSON string. Returns the new import id, or `null` when malformed. |
| `imports()` | `TraceImport[]` | Metadata for every loaded import. |
| `importRecords(id)` | `TraceRecord[]` | Records inside the named import, chronological. |
| `removeImport(id)` | `void` | Drop one import. |

## Capture and share a bug report

1. Reproduce the bug with `?baredom-trace-history` in the URL.
2. Click **Export** in the dock toolbar (or call
   `window.BareDOM.traceHistory.download()` from the console). A
   `.trace.json` file downloads.
3. Attach the file to the bug report.
4. The recipient drags the file onto their dock (or calls
   `window.BareDOM.traceHistory.import(text)` with the file contents).
   The imported trace appears as a read-only chip alongside the live
   view.

Trace files are pure JSON, validated against `schemaVersion: 1`. Older
or newer schema versions are rejected with a clear error.

## Recording sessions

The always-on recorder is convenient for ambient debugging, but
high-traffic apps quickly outrun the 5000-record default buffer.
Sessions are bounded slices the user explicitly captures:

```js
const id = window.BareDOM.traceHistory.startSession();
// … reproduce the bug …
window.BareDOM.traceHistory.stopSession();
const records = window.BareDOM.traceHistory.sessionRecords(id);
```

Or click the **Record** button in the dock toolbar — it toggles
between Start and Stop and the active session shows a live-dot in the
session strip.

Sessions are metadata only. They name a half-open `[startId, endId)`
range over the ring buffer; records are filtered on demand. A session
that outlives the ring buffer's capacity will silently shed records
from its head, same as the live view.

## Import / export

`download()` and the toolbar's **Export** button both write a
`.trace.json` file with a filename like
`baredom-trace-2026-05-11-143052.trace.json`. The on-disk shape is
documented in [`x-trace-history-schema.md`](./x-trace-history-schema.md).

Importing accepts either a parsed object (no copy) or a JSON string
(parsed before storing). Drag-drop onto the dock works too: the drop
overlay appears when a file is dragged anywhere over the dock.

Imports are independent storage. They are NOT dropped by `clear()` —
remove them individually with `removeImport(id)` or the × button on
the import chip.

## Adapter notes

The dock is a self-contained custom element. There is **no
adapter-specific code** — load the ESM module and activate via the URL
flag. The four common entry points:

### Vanilla JS

```html
<script type="module" src="/node_modules/@vanelsas/baredom/dist/x-trace-history.js"></script>
```

### TypeScript

```ts
import "@vanelsas/baredom/x-trace-history";
import type {
  BareDOMTraceHistory,
  TraceRecord,
} from "@vanelsas/baredom/x-trace-history";

// Narrow on the variant for exhaustive event-type handling.
function describe(r: TraceRecord): string {
  switch (r.type) {
    case "event/dispatch":
    case "event/dispatch-cancelable":
    case "event/dispatch-document":
      return `${r.tag} ${r.eventName}`;
    case "state/instance-field-set":
      return `${r.tag} ${r.field} =`;
    case "dom/attribute-set":
    case "dom/attribute-removed":
      return `${r.tag} [${r.attribute}]`;
    case "lifecycle/connected":
    case "lifecycle/disconnected":
      return `${r.tag} ${r.type}`;
    case "lifecycle/attribute-changed":
      return `${r.tag} attr ${r.attribute}`;
  }
}
```

### Angular

```ts
// main.ts
import "@vanelsas/baredom/x-trace-history";
// …bootstrapApplication etc.
```

### React

```tsx
// index.tsx
import "@vanelsas/baredom/x-trace-history";
// …createRoot etc.
```

In all cases the URL flag or `window.BAREDOM_TRACE_HISTORY` decides
whether the dock actually mounts. Shipping the module in production
without the flag is a no-op beyond the import cost.

## Causality

Every record carries a `causeId` field. It points at the id of the
synchronously-enclosing `dispatchEvent` call's record, or is `null` when
the record was produced outside any active dispatch.

The detail pane shows:

- **Caused by** — a clickable link to the dispatch that triggered this
  record. Absent when `causeId` is `null`.
- **Effects (N)** — clickable links to every record produced inside this
  record's synchronous extent (only meaningful for `event/dispatch*`
  records). Capped at 50 entries; the count shows `N of TOTAL` when
  truncated.

Clicking a link jumps the timeline selection to that record so you can
walk a chain step by step.

### What IS tracked

- Synchronous handler chains. A click handler that sets attributes,
  fires further events, or mutates instance fields all carry the click's
  record id as their `causeId`.
- Externally-fired CustomEvents. Any `el.dispatchEvent(new CustomEvent(…))`
  call — not just BareDOM's own helpers — establishes a cause frame.
  This means third-party libraries dispatching CustomEvents on BareDOM
  components are visible in the chain.
- Nested dispatches. Each frame layers onto an internal cause stack, so a
  three-level chain (`click` → `change` → `input`) keeps each step's
  cause pointing at its immediate parent.

### What is NOT tracked

The chain is synchronous-only by design. It breaks at any of:

- `setTimeout` / `setInterval`
- `requestAnimationFrame`
- `Promise.then` / `await` / microtask scheduling
- `MutationObserver` / `IntersectionObserver` / `ResizeObserver` callbacks
- `MessageChannel.onmessage` / `postMessage`
- Native browser event handlers fired outside an active dispatch
  (a top-level `pointerdown` is not recorded, only its CustomEvent
  consequences are)

Records produced asynchronously have `causeId: null`. Async causality
requires Zone.js-style instrumentation, which is out of scope for the
current version. See `docs/x-trace-history-roadmap.md` (Phase 8 and
non-goals) for the deferred plan.

### Why the dispatch dot precedes its effects

For a dispatch frame, the dot's timestamp is captured at *frame entry*
(before handlers run) but the record itself is pushed at *frame exit*
(after handlers complete). This means:

- The timeline draws the dispatch dot to the **left** of the dots it
  caused, matching intuition.
- Records inside the buffer are not strictly insertion-ordered by `t`;
  the dock sorts records chronologically when filtering for display, so
  scrubber stepping (Left/Right arrows) follows time order rather than
  insertion order.

This is purely a presentation choice — the underlying data preserves both
the reserved id and the entry timestamp, so any consumer reading the
JSON directly can reconstruct the chain unambiguously.

## Performance contract

- **When off** — one nil-check per hook site on every event dispatch,
  state mutation, and lifecycle callback. No allocation, no recording,
  no dock in the DOM, no `window.BareDOM`.
- **When on** — every dispatch / mutation / lifecycle event allocates one
  record (plain JS object, no copy of the underlying value) and appends
  to the ring buffer. The dock subscribes to recorder updates and
  re-renders the timeline on the next animation frame.
- **Sample-rate cap (normal mode)** — duplicate records sharing the same
  `(componentId, eventName)` within a 16ms window are dropped. This
  keeps animation components (60fps `pointermove`, etc.) from saturating
  the buffer. Activate with `=raw` to disable.
- **Records reference, don't copy** — BareDOM's immutable models give
  structural sharing for free. Record memory cost is bounded by ring-
  buffer size, not by the size of the values referenced.

## JSON schema

The on-disk format for exported traces is documented in
[`x-trace-history-schema.md`](./x-trace-history-schema.md). It is
versioned at `schemaVersion: 1`; importers reject mismatched
versions.

The same schema is reflected as TypeScript declarations in
`dist/x-trace-history.d.ts`. `TraceRecord` is a discriminated union on
the `type` field — switching on it is exhaustive.

## Roadmap

See [`x-trace-history-roadmap.md`](./x-trace-history-roadmap.md) for
the phased plan. Phase 6 — consumer distribution (ESM module,
TypeScript declarations, these user docs) — is what makes the dev tool
usable outside this repo. Phase 7 ships a standalone `viewer.html` for
inspecting traces without the host app.
