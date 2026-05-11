# `x-trace-history` Roadmap

A FlowStorm-class debugger for BareDOM web components.

## Context

BareDOM is built on epochal-time, immutable-value, stateless-component architecture (`DOM = f(attrs, props)`). That foundation makes time-travel debugging a natural fit — every model transition is already a snapshot. No comparable tool exists for web components today.

`x-trace-history` is a dev-and-debug tool that consumers of BareDOM (CLJS, JS, TS, Angular, React, Vue, vanilla) load into their app, opt-in. It records every render, every CustomEvent, every attribute mutation, every lifecycle callback, and surfaces them as a navigable timeline with cause→effect chains, value drill-down, search, and shareable JSON exports.

The goal isn't one bug — it's **productization**. A serious component library needs serious tooling. After `x-trace-history` ships, BareDOM looks and feels at the level of React DevTools, Stencil DevTools, Storybook. Adopting BareDOM becomes more attractive because the debugging story is no longer "use generic browser DevTools and squint."

A prerequisite shipped earlier (PRs #108, #109): every CustomEvent in BareDOM now flows through one of three named helpers in `baredom.utils.dom` (`dispatch!`, `dispatch-cancelable!`, `dispatch-document!`). That gives `x-trace-history` a clean instrumentation chokepoint set with 100% coverage.

## Goals

- **Multi-audience.** Works for CLJS authors of components, *and* JS/TS/Angular/React/Vue app developers consuming components. Records are JSON-shaped, API is JS-friendly, types are published as `.d.ts`.
- **Two recording modes.** Always-on timeline dock **and** bounded recording sessions. Sessions resolve high-cardinality animation components.
- **All four FlowStorm pillars.** Append-only history + drill-down + search · cross-instance timeline + scrubber · cause→effect causality · export/import.
- **Small shippable steps.** Each PR is a vertical slice that does something visible. No "infrastructure-for-later" PRs.
- **Zero overhead when off.** Activation gated by `?baredom-trace-history` URL param or `window.BAREDOM_TRACE_HISTORY = true` — same opt-in pattern as the existing `x-debug`.
- **Closure Advanced safe.** `^js` hints, `gobj/get|set` for non-native fields, no monkey-patching.

## Non-goals

- **Async causality in v1.** Synchronous cause-id chains only. setTimeout/rAF/Promise chains are deferred (they require Zone.js-style instrumentation).
- **True replay in v1.** Scrubbing highlights the recorded moment but does not re-fire events on live elements. Replay is hard, fragile, and a fraction of the value. Defer.
- **Production data export.** v1 is for dev/staging. Capturing production user sessions raises privacy concerns we'll address later.
- **Replacing or deprecating `x-debug`.** See below.

## Relationship to `x-debug`

`x-debug` (the existing element inspector) and `x-trace-history` solve genuinely different problems and ship as separate, complementary tools:

- **`x-debug`** = "inspect and tweak the current state of one element" (live, editable, present-tense). Floating panel with checkbox toggles for boolean attributes and text inputs for string/number attributes.
- **`x-trace-history`** = "review what happened across all components" (recorded, read-only, past-tense). Timeline dock with cross-instance lanes, scrubber, causality.

They share `x-debug-registry` for component metadata, and both can be active simultaneously (separate URL params: `?baredom-debug` and `?baredom-trace-history`; non-colliding z-indices). The split mirrors mainstream tooling: Chrome DevTools (Elements vs Performance), Redux DevTools (state vs time-travel) — users need both views.

Possible late-phase enhancement: light cross-linking — `x-trace-history`'s detail pane gets an "open in inspector" link for the source element; `x-debug`'s panel gets a "show recent trace records" tab. They become two views into a unified surface without merging code.

## Architectural principles

- **Records are plain JS objects** (JSON-shape with stable schema). CLJS users can `js->clj` if they prefer maps. JS/TS users get an `interface TraceRecord` type.
- **Hook points** are first-class extension hooks added to `baredom.utils.dom` and `baredom.utils.component` (one-line `(when-some [h *trace-hook*] (h ...))`). Negligible cost when off (one nil check). Captures 100% of dispatch + lifecycle + state mutation.
- **The recorder is the only place state lives.** A `defonce` atom in `baredom.dev.x-trace-history.recorder`. Component instances stay stateless.
- **Records reference, don't copy.** BareDOM's immutable models give structural sharing for free. Records hold references; memory cost is bounded by ring-buffer size.
- **Versioned JSON schema.** From PR 1, every record carries `schemaVersion: 1`. Future schema evolutions stay backward-compatible.

## Roadmap

Each PR is sized for ~1 hour of review, ships something visible, and ends green on lint + test compile + release lib. PRs marked **(consumer-visible)** add or polish features that BareDOM consumers see; **(internal)** are recorder/instrumentation work.

### Phase 1 — Recording substrate (3 PRs)

- **PR 1: Dispatch hooks + recorder + console API.** Add `*trace-hook*` extension to `dispatch!`, `dispatch-cancelable!`, `dispatch-document!` in `baredom.utils.dom`. New `baredom.dev.x-trace-history.recorder` ns: ring buffer, schema, install/uninstall, JS API at `window.BareDOM.traceHistory.{records, pause, resume, clear}`. Activated by `?baredom-trace-history`. Records every CustomEvent. Tests. **(consumer-visible)** — open console, see every event flowing through the page.
- **PR 2: State-mutation hooks + lifecycle hooks.** Extend hooks to `setv!`, `set-attr!`, `set-bool-attr!`, `remove-attr!` in `du/`, plus `connectedCallback` / `attributeChangedCallback` / `disconnectedCallback` via a `*lifecycle-hook*` in `baredom.utils.component/make-element-class`. Now records cover full mutation surface. **(internal)**
- **PR 3: Component-id assignment + tag tracking.** Stable per-instance IDs (`gobj/set el "__xTraceHistoryId" (next-id!)`). Recorder maintains a side-index `{componentId → {tag, firstSeen}}`. Records carry `componentId` and `tag`. Component-id survives disconnect. Tests. **(internal)**

### Phase 2 — Timeline dock UI (3 PRs)

- **PR 4: `<x-trace-history>` element + flat record list.** New custom element. Floating dock pattern reused from `x-debug.cljs:173-182`. Right-side dock initially. Renders a flat list of records (newest first) with tag, event type, payload preview, timestamp. Click a record → expanded JSON detail view. Filter by tag (dropdown reuses `x-debug-registry`) and event type (checkboxes). **(consumer-visible)**
- **PR 5: SVG timeline + per-instance lanes.** Replace flat list with a horizontal SVG timeline. X = time, Y = lane (one per component instance). Dots colored by event-type. Hover for tooltip. Click to filter detail pane. **(consumer-visible)**
- **PR 6: Scrubber + detail pane.** Vertical drag-line over the timeline. Detail pane below pretty-prints the record at the scrubbed position. Keyboard left/right to step record-by-record. **(consumer-visible)**

### Phase 3 — Causality (1 PR)

- **PR 7: Synchronous cause-id chain.** Wrap `EventTarget.prototype.dispatchEvent` once at activation. Push `causeId` onto a recorder stack on entry, pop on exit. Every record produced inside that synchronous call carries the outer dispatch's id. Detail pane shows cause-of and effects-from links — clickable to navigate the chain. Document the async-chain limitation in `docs/x-trace-history.md`. **(consumer-visible)**

### Phase 4 — Recording sessions (2 PRs)

- **PR 8: Record/stop sessions UI.** Always-on background continues; sessions are labeled, bounded slices the user explicitly captures. Session list in dock; each session inspectable independently. Sessions own their own scrubber state. **(consumer-visible)**
- **PR 9: Cardinality safeguards.** Default-on sample-rate cap: drop duplicates within 16ms per `(componentId, eventName)`. Toggle via `?baredom-trace-history=raw` for forensic recording. Per-event-type filters (off by default for `state/instance-field-set`). **(internal)**

### Phase 5 — Export / import (2 PRs)

- **PR 10: Export to `.trace.json`.** Toolbar button → Blob download. JSON schema versioned and documented in `docs/x-trace-history-schema.md`. **(consumer-visible)**
- **PR 11: Import `.trace.json`.** Drag-drop on the dock or file-picker. Loaded sessions appear as read-only ghost lanes alongside live recording. Schema-version check; clear error on mismatch. **(consumer-visible)**

### Phase 6 — Consumer distribution (3 PRs) — **shipped**

- **PR 12: `:lib` module + ESM export.** ✅ Added `baredom.exports.x-trace-history`, registered in `shadow-cljs.edn` `:lib :modules`, added `"./x-trace-history"` entry in `package.json` `exports`, and wired the dock into `baredom.registry/all-registers` so the all-bundle ships it. Also decoupled the dock from `x-debug-registry` (which would have transitively pulled every component into the dev-tool bundle): tags are discovered dynamically from the recorder's observed-components index. Bundle-size budget bumped to accommodate the dev tool — `base.js` to 56 KB and a per-module override of 20 KB for `x-trace-history.js`.
- **PR 13: TypeScript declarations.** ✅ Hand-authored `dist/x-trace-history.d.ts` emitted by `scripts/generate_types.bb` alongside the component `.d.ts` files. `TraceRecord` is a discriminated union on `type` covering all eight record kinds. `BareDOMNamespace` is exported as a top-level interface so future dev tools can augment it via TypeScript declaration merging.
- **PR 14: README + `docs/x-trace-history.md`.** ✅ Expanded the doc into a full user guide: activation, dock anatomy + keyboard shortcuts, complete console-API table, capture-and-share-a-bug-report workflow, recording sessions, import/export, adapter notes (vanilla JS / TypeScript / Angular / React), performance contract, JSON-schema link. README points at the new doc from the **Stateless** design-principle bullet.

### Phase 7 — Standalone viewer (1 PR) — **shipped**

- **PR 15: `viewer.html` for sharing traces.** ✅ Standalone page at `public/viewer.html`, deployed alongside the demo site to `avanelsas.github.io/baredom/viewer.html`. Activates the recorder before loading the all-bundle, then drag-drop or `?trace=<base64>` feeds the dock. Dock auto-switches to the freshly-loaded import when live is empty (heuristic only fires on new-import transitions, so an active session with records is never yanked). URL-param decoder supports both standard and URL-safe base64.

### Phase 8 — Advanced & polish (4 PRs)

- **PR 16: Search across recorded values.** ✅ Native `<input type="search">` in the dock's filter bar. Every record gets a lazily-built lowercase JSON haystack memoised under a gobj key on the record itself, so the recording hot path pays nothing for search and the first keystroke pays one `JSON.stringify` + `toLowerCase` per record (subsequent keystrokes reuse the cached strings). Falls back to an empty haystack on cyclic detail payloads. AND-combines with the tag dropdown and category checkboxes.
- **PR 17: Causality DAG view.** Graph rendering of cause→effect chains. Distinctively *not* what DevTools provides. Click a node to scrub to that record. **(consumer-visible)**
- **PR 18: Heatmap density rendering.** When a lane crosses ~50 events/sec, render a colored band rather than individual dots. Click to drill in. Most visually elegant; useful for animation-heavy components. **(consumer-visible)**
- **PR 19: Adapter integration tests.** Verify `x-trace-history` works inside `@vanelsas/baredom-react` and `@vanelsas/baredom-angular` consumer apps. Documents adapter-specific activation. **(consumer-visible)**

## Critical files

The roadmap touches these files repeatedly:

- `src/baredom/utils/dom.cljs` — hook-point additions in PR 1 + PR 2
- `src/baredom/utils/component.cljs` — hook-point addition in PR 2
- `src/baredom/dev/x_trace_history/` (new) — recorder, model, UI, element class. Three layers: `model.cljs` (pure: schema, ring buffer ops), `recorder.cljs` (effects: hooks, install/uninstall, causality), `x_trace_history.cljs` (effects: dock element, UI rendering)
- `src/baredom/exports/x-trace-history.cljs` (new in PR 12) — ESM entry point
- `src/baredom/registry.cljs` — registration entry in PR 12
- `shadow-cljs.edn` — `:lib` module entry in PR 12
- `package.json` — exports entry in PR 12
- `docs/x-trace-history.md` (new in PR 14) — user documentation
- `docs/x-trace-history-schema.md` (new in PR 10) — JSON schema reference

## Verification (per-PR baseline)

Every PR must pass:

1. `clj-kondo --lint src test` — zero new warnings
2. `npx shadow-cljs compile test` — zero `:infer-warning`
3. `npx shadow-cljs release lib` — Closure Advanced passes, zero warnings
4. New unit tests in `test/baredom/dev/x_trace_history/` for any new pure logic (ring buffer, schema, filters)
5. Manual smoke test at `http://localhost:8000/?baredom-trace-history` for any UI/integration change
6. PR-specific verification listed in each PR's checklist

## Methodology

This roadmap is intentionally light on per-PR file-level detail. Each PR will get its own focused planning session before implementation — small enough that we can scope-and-go without bloating the roadmap. Value compounds across PRs; we can stop after any phase boundary if priorities change.
