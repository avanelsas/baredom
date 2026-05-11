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
- [Search](#search)
- [Console API](#console-api)
- [Capture and share a bug report](#capture-and-share-a-bug-report)
  - [Sharing via URL](#sharing-via-url)
  - [Auto-switch on first import](#auto-switch-on-first-import)
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
| Filters | Tag dropdown · view-mode toggle (Timeline / Causality) · category checkboxes (events, state, DOM, lifecycle) · axis-mode toggle (Order / Time) · full-text search |
| Timeline | One horizontal lane per component instance. Dots are coloured by category. Hover for tooltip; click to select. |
| Causality | (View-mode: Causality) Tree-shaped view of cause→effect rooted at the currently-selected record's highest ancestor. Click a node to scrub to it. The pane auto-scrolls so the selected node sits at the centre. |
| Splitter | Drag to resize the detail pane. |
| Detail pane | Pretty-printed JSON for the selected record, plus **Caused by** / **Effects** links. |
| Hint line | Record count, time-bounds, lane count. |

Keyboard:

- **Left / Right** while the timeline OR causality pane has focus —
  step the scrubber by one record. In causality mode the next record
  may belong to a different tree; the pane redraws automatically and
  auto-scrolls to the new selection.
- Click a lane label to filter the timeline to that component
  instance. Click again to clear.
- Click anywhere in the timeline (or causality pane) to focus it.

## Search

The filter bar carries a search input next to the tag dropdown.
Typing into it narrows the timeline to records whose JSON-serialised
form contains the typed string. Some examples of useful queries:

- `disabled` — any record that mentions the `disabled` attribute or
  property anywhere (events with `detail.disabled`, attribute writes,
  lifecycle `attribute-changed`, etc.).
- `"id":42` — a dispatch whose detail object carries `id: 42`. Quoted
  fragments anchor against the JSON encoding.
- `x-modal` — every record from any `x-modal` instance (overlaps with
  the tag dropdown; either path works).
- `lifecycle/connected` — every connected callback. Works for any
  `type` value because the type string lives in the haystack too.

The search combines with the tag dropdown and category checkboxes
using AND-semantics: a record must satisfy every active filter to
appear. Clearing the search input removes only the search constraint;
the other filters remain.

Matching is case-insensitive — the query is lowercased on input and
each record's haystack is lowercased on first access. Empty input
disables the search filter entirely.

**Indexed lazily.** The haystack for each record is built on first
use of the search input and cached on the record itself, so the
recording hot path pays nothing for search. The first search after a
batch of new records pays one `JSON.stringify` + `toLowerCase` per
record; every subsequent keystroke reuses the cached strings.

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
4. The recipient has two options:
   - **Standalone viewer.** Open the
     [BareDOM trace viewer](https://avanelsas.github.io/baredom/viewer.html)
     and drag the file onto the dock. No app required; the viewer is
     read-only and shows exactly what the reporter saw.
   - **Their own app.** If they are running an app that already includes
     x-trace-history, they can drag the file onto their dock (or call
     `window.BareDOM.traceHistory.import(text)` with the file contents).
     The imported trace appears as a chip alongside the live view.

Trace files are pure JSON, validated against `schemaVersion: 1`. Older
or newer schema versions are rejected with a clear error.

### Sharing via URL

For tiny traces (~6 KB of JSON), the viewer also accepts a base64-
encoded envelope directly in the URL:

```
https://avanelsas.github.io/baredom/viewer.html?trace=<base64>
```

Encode with `btoa(JSON.stringify(envelope))` and append. URL-safe
base64 (`-_` in place of `+/`) is also accepted. Larger traces should
travel as files — most servers cap URLs around 8 KB, and base64
inflates the payload by 33%.

Privacy note: anything in the URL is visible to anyone with the link
and to URL-logging proxies. Use the file-drop path for sensitive
traces.

### Auto-switch on first import

When an imported trace lands on a dock with an empty live buffer (the
viewer page, or any otherwise-idle app), the dock auto-switches the
view to the new import. Drag a file onto an active session with live
records and the view stays put — the heuristic only kicks in when
there is no active session to disturb.

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

### Causality DAG view

For a graphical view of the same chain, flip the filter row's
**view-mode** select from **Timeline** to **Causality**. The pane
above the splitter is replaced with an SVG tree rooted at the highest
ancestor of the currently-selected record. Boxes are individual
records (`tag · type`), edges connect cause to effect, and the
currently-selected node is highlighted.

- **Click a node** — selects that record. The detail pane updates,
  and toggling back to **Timeline** lands the scrubber on the same
  spot. The same selection model works in both views.
- **Auto-scroll on switch** — switching to Causality scrolls the pane
  so the selected node sits in the centre of the viewport. The same
  thing happens whenever the selection changes while you're already
  in Causality mode.
- **Empty state** — with no record selected, the pane shows a hint
  asking you to pick one. Select any record in Timeline first.
- **Leaf record** — when the selected record has neither a cause
  nor any effects in the buffer (commonly a lifecycle / dom-attribute
  record emitted from a component's constructor or `connectedCallback`
  — no enclosing dispatch frame, so `causeId` is `null`), the pane
  shows a small banner above the lone node explaining that this isn't
  a broken tree. Pick an `event/dispatch*` record (or one whose
  detail pane shows a **Caused by** link) to see a real chain.
- **Over-cap notice** — trees over 200 nodes show a notice instead
  of drawing. That's typically a render fan-out (one dispatch
  causing hundreds of effects); pick a smaller leaf record to view
  its subtree. The tag / category filter intentionally does NOT
  apply to the causality tree — filtering would silently break
  chains by hiding causes, so narrowing it would not help here.
- **Axis-mode hidden** — the Order / Time toggle only affects the
  Timeline pane, so it's hidden while you're in Causality view to
  reduce noise.

Each record carries at most one `causeId`, so the causality structure
is a forest of trees rather than a general DAG — there's never a
cycle and never more than one parent per node. The view name ("DAG")
is general-correct but the algorithm is just tree layout.

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
