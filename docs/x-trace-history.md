# x-trace-history

`x-trace-history` is a dev-only debugger for BareDOM components. It records
every CustomEvent dispatch, every observed attribute change, every
instance-field write, and every lifecycle callback, then surfaces them as
a navigable timeline with cause→effect navigation.

Activate by adding `?baredom-trace-history` to the URL or setting
`window.BAREDOM_TRACE_HISTORY = true` before the app boots. When inactive
the recorder pays only a single nil-check per hook site and no per-event
overhead — there is no production cost when off.

> Full user-facing docs (workflow guide, examples for vanilla JS / TypeScript /
> Angular / React, "share a bug report" recipe) ship in PR 14. The section
> below documents the **causality** behavior added in PR 7 so consumers
> understand which chains are tracked and which break.

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
