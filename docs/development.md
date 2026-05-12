# Development

This guide covers setting up the development environment, using the component demo, debug mode, the trace-history time-travel debugger, and building from source.

---

## Building from Source

BareDOM is authored in ClojureScript and compiled with [shadow-cljs](https://shadow-cljs.github.io/docs/UsersGuide.html).

```bash
# Install dependencies
npm install

# Start development server with hot reload (http://localhost:8000)
npx shadow-cljs watch app

# Run browser-based tests (http://localhost:8021)
npx shadow-cljs watch test

# Build production ESM library to dist/
npm run build
```

---

## Component Demo

BareDOM ships with a built-in demo that lets you browse and interact with every component in isolation. It is intended for developer convenience when working on the library itself.

```bash
npm install
npx shadow-cljs watch app
```

Then open `http://localhost:8000`. The dev server serves `public/index.html` and hot-reloads on every source change. Each component is demonstrated in its own section with controls for toggling attributes, properties, and variants.

---

## Debug Mode

BareDOM includes a visual debug overlay for development. It highlights every component on the page, shows tag names on hover, and lets you inspect and edit live state — no browser extension required.

**Activate** by adding `?baredom-debug` to any dev URL:

```
http://localhost:8000?baredom-debug
```

Or toggle from the browser console at any time:

```js
window.BAREDOM_DEBUG = true
```

When active, debug mode provides:

- **Dashed blue outlines** around every BareDOM component
- **Tag labels** that appear on hover (top-right corner)
- **Inspection panel** — click a label to see current attributes, properties, and computed state
- **Inline editing** — toggle boolean attributes with a switch, edit string/number values and press Enter to apply
- **Console logging** — structured `console.group` output on every attribute change, lifecycle events at `console.debug` level

Changes made through the debug panel are live but ephemeral — they modify the DOM directly and do not survive a page refresh.

Debug mode is dev-only and is excluded from the production ESM build (`npm run build`).

---

## Trace History

BareDOM ships an optional time-travel debugger, `x-trace-history`, that complements debug mode. Where debug mode answers *"what is the state of this element right now?"*, trace history answers *"what happened across the page over time?"*. Both can be active at the same time.

**Activate** by adding `?baredom-trace-history` to any dev URL:

```
http://localhost:8000?baredom-trace-history
```

Or toggle from the browser console:

```js
window.BAREDOM_TRACE_HISTORY = true
```

For forensic recording — disables the default 16ms sample-rate cap and turns on the `state/instance-field-set` filter that's off by default — use `=raw`:

```
http://localhost:8000?baredom-trace-history=raw
```

This makes high-frequency animation lanes legible to the recorder, at the cost of larger record buffers.

When active, trace history provides:

- **Recording substrate** — every CustomEvent, attribute mutation, instance-field write, and lifecycle callback is captured as a plain JS record with a stable schema (`schemaVersion: 1`).
- **Floating dock** — cross-instance lanes on a horizontal SVG timeline, with hover tooltips, click-to-filter, and a scrubber for stepping record-by-record (← / → arrow keys).
- **Causality view** — synchronous cause → effect chains shown as a DAG; jump between a record and the dispatch that produced it.
- **Heatmap rendering** — animation-heavy lanes automatically switch from individual dots to density bands so the timeline stays legible.
- **Full-text search** — filter records by any value in the recorded payload.
- **Recording sessions** — bound a slice of the always-on timeline to a labelled session.
- **Export / import** — download a `.trace.json` or drag one onto the dock; share traces via the standalone `viewer.html` (`?trace=<base64>` URL param supported).
- **Console API** — `window.BareDOM.traceHistory.records() / pause() / clear() / startSession() / export() / …`.

Trace history is opt-in and has **zero cost when not activated** (one nil-check at each hook site). It ships as a separate ESM module (`@vanelsas/baredom/x-trace-history`) with full TypeScript declarations.

See [`docs/x-trace-history.md`](./x-trace-history.md) for the full user guide, [`docs/x-trace-history-schema.md`](./x-trace-history-schema.md) for the JSON schema reference, and [`docs/x-trace-history-roadmap.md`](./x-trace-history-roadmap.md) for the design rationale and what's deferred to future versions.

---

## Linting

```bash
clj-kondo --lint src test
```

Code must have zero lint warnings and errors before pushing.

---

## Project Structure

```
src/baredom/
  components/<name>/     Component source (model.cljs, <name>.cljs)
  exports/<name>.cljs    ESM entry point per component
  utils/                 Shared utilities (dom.cljs, model.cljs)
  dev/                   Dev-only code (hot_reload.cljs, x_debug/, x_trace_history/)
  core.cljs              Dev build entry point
docs/                    Per-component documentation
demo/                    Demo HTML pages
test/                    Browser-based tests
scripts/                 Build and code generation scripts
```

See [CLAUDE.md](../CLAUDE.md) for the full architecture reference, coding conventions, and contribution guidelines.
