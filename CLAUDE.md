# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

**Development (browser REPL + hot reload):**
```
npx shadow-cljs watch app
```
Opens dev server at http://localhost:8000 (serves `public/`) with hot reload via `app.core/start!`.

**Run tests (browser-based):**
```
npx shadow-cljs watch test
```
Opens test runner at http://localhost:8021.

**Build production ESM library:**
```
npm run build
```
Runs `npm run clean && npx shadow-cljs release lib`, producing per-component ESM files in `dist/`.

**Pack for local consumption:**
```
npm run pack:local
```

**Lint (clj-kondo):**
```
clj-kondo --lint src test
```
Code must have **zero lint warnings and errors** before pushing to GitHub. Always run the linter and fix any issues before `git push`.

## Releasing a New Version

See [`docs/RELEASING.md`](docs/RELEASING.md) — update 4 version locations, tag, push via PR.

## Philosophy — Simple Made Easy

This codebase follows Rich Hickey's core tenets. These principles are non-negotiable and override convenience:

1. **Values, not places** — Components are stateless. State is a succession of immutable values (attribute snapshots), never mutable references. No `atom`, `volatile!`, `core.async`, reactive signals.
2. **Data > functions > macros** — Model metadata (`property-api`, `observed-attributes`) *drives* runtime behavior. If data can declare it, code must not hand-implement it.
3. **De-complect** — Separate calculation from effects. `model.cljs` = pure transforms. `<name>.cljs` = DOM side effects. Never mix.
4. **Composition over inheritance** — No class hierarchies. `component/register!` composes behavior from a data map. No `deftype`, `defrecord`.
5. **Transparency** — Code must be readable as simple data transformations. Functions ending in `!` have effects; all others are pure. Named functions over anonymous lambdas.
6. **Epochal time** — `render!` compares new-value vs old-value; only applies the delta. The model-change guard is the epochal boundary.

## When Rules Conflict

Prefer statelessness over convenience, data-driven over manual, compile-safety over brevity.

## Core Design Principle

Components are **stateless**. The single rendering model is:

```
DOM = f(attributes, properties)
```

Instance fields may only store shadow root references and cached DOM node handles. Never store authoritative UI state.

### The render pipeline

Every component implements an `update-from-attrs!` function that follows a three-phase pipeline with a **model-change guard**:

```clojure
(defn- update-from-attrs! [^js el]
  (let [new-m (read-model el)             ;; 1. READ  — extract raw attrs into data
        old-m (du/getv el k-model)]        ;; 2. CHECK — compare with cached model
    (when (not= old-m new-m)               ;;    Skip if unchanged
      (apply-model! el new-m))))           ;; 3. APPLY — patch DOM from model
```

The model is cached inside `apply-model!` at the end (via `du/setv! el k-model m`), not before the call.

**Critical rule:** Never set observed attributes on the host element inside `apply-model!` without the change guard — this is the #1 cause of infinite recursion in web components.

### Naming conventions

- Functions ending with `!` have side effects (DOM mutation, event dispatch, state changes).
- Lifecycle callbacks: `connected!`, `disconnected!`, `attribute-changed!` — always named `defn-` (private) functions.
- Private instance-field keys: `"__xComponentNameField"` format (e.g. `"__xIconModel"`).

### Function size

Functions should do one thing. If a function exceeds ~15 lines, decompose it into named subfunctions. Long functions tend to mix calculation and effects — violating de-complect — and make Closure Advanced errors harder to trace.

### Named patterns

Reference by name when discussing architecture:

- **`render-pipeline`** — `update-from-attrs!`: `read-model` → change-guard → `apply-model!` (caches model at end). Golden sample: `x-icon`.
- **`model-layer`** — pure `model.cljs` with required metadata defs + `normalize`. Golden sample: `x-icon/model.cljs`. See [`docs/MODEL-LAYER.md`](docs/MODEL-LAYER.md) for field specs.
- **`registration-idiom`** — named lifecycle `defn-` functions + `component/register!` + `du/install-properties!`. Golden sample: `x-icon`. See [`docs/REGISTRATION.md`](docs/REGISTRATION.md) for template.

## Closure Advanced Compilation Safety

All code must survive Google Closure Advanced Compilation (minification + renaming).

**JS interop rules:**
- Use `(.-prop el)` and `(set! (.-prop el) val)` for native DOM properties
- Use `goog.object/get` / `goog.object/set` (aliased as `gobj/get` / `gobj/set`) for **non-native instance fields** (custom properties stored on element instances)
- Avoid dynamic property lookup
- Apply `^js` type hints on all parameters representing DOM nodes, events, or element instances
- Define constants for tag names, attribute names, event names, and CSS custom property names — no duplicated string literals

**Forbidden:**
- React, Reagent, Lit, Vue, Svelte, Alpine, JSX, TypeScript, virtual DOM, template DSLs, framework runtimes
- `deftype`, `defrecord` for element classes

## Element Registration

Use `component/register!` with a declarative options map. **Do not create `element-class` functions manually.**

- Only add manual `.defineProperty` for properties with custom getter/setter logic
- Use `this-as` inside property setter bodies — never reference `this` directly
- **Forbidden:** manual `element-class` with `js*`, `js/Reflect.construct` with atoms, manual prototype composition

See [`docs/REGISTRATION.md`](docs/REGISTRATION.md) for the full template and rules.

## Architecture

ClojureScript library compiling to standalone native Web Components (Custom Elements v1) via shadow-cljs `:esm` target. Each component is a separate ESM module with zero framework runtime dependency.

### Three-layer pattern per component

Every component under `src/baredom/components/<name>/` follows:

1. **`model.cljs`** — Pure functions only. No DOM or side effects. Required defs: `tag-name`, `observed-attributes`, `property-api`, `event-schema`, `method-api`. See [`docs/MODEL-LAYER.md`](docs/MODEL-LAYER.md).
2. **`<name>.cljs`** — DOM and lifecycle layer. Shadow DOM creation, `render!`, event wiring, lifecycle callbacks, `init!` via `component/register!`.
3. **`src/baredom/exports/<name>.cljs`** — ESM entry point. Exposes `^:export init`, `register!`, and `public-api` metadata.

### Additional files per component

- **`docs/<name>.md`** — Developer documentation (API contract, a11y, examples)
- **`demo/<name>.html`** — Standalone demo with live controls and event logging

### Shadow DOM and styling

All components use open shadow DOM (`mode: "open"`). Styles are inline ClojureScript strings. CSS custom properties follow `--x-<component>-<property>` naming. Light/dark mode via `@media (prefers-color-scheme: dark)`. Animations must respect `@media (prefers-reduced-motion: reduce)`.

### Theming

All components must consume shared design tokens from `x-theme`. Wrap hardcoded CSS values with `var(--x-token, fallback)`. Key rules:

- **Overlays** (modals, drawers, menus, popovers, toasts, dropdowns): use `--x-color-bg` (always opaque), never `--x-color-surface`
- **Inline surfaces** (cards, fieldsets, collapse panels): use `--x-color-surface`
- **Do not theme** decorative palette colours, white-on-coloured-button foreground, or rgba variant tints

See [`docs/THEMING.md`](docs/THEMING.md) for the full token catalogue and demo page requirements.

### Mobile and responsive

All components must work on viewports from 320px up. Critical rules:

- **Use pointer events, never mouse events.** `pointerdown`, `pointerup`, `pointermove`, `pointerenter`, `pointerleave` — never `mouse*` equivalents.
- **No fixed widths that can overflow.** Cap with `min(Xpx, calc(100vw - 2rem))` on positioned panels.

See [`docs/MOBILE.md`](docs/MOBILE.md) for the full responsive checklist.

### Shared utilities

Components **must** use shared utility modules — never reimplement locally:

- **`component/register!`** — element registration from declarative options map
- **`gobj/get`** / **`gobj/set`** — instance-field storage (refs, model cache, handlers)
- **`du/has-attr?`** / **`du/get-attr`** — attribute reads in `read-model`
- **`du/dispatch!`** / **`du/dispatch-cancelable!`** — event dispatch
- **`du/install-properties!`** — install property accessors from `model/property-api`
- **`mu/`** — boolean parsing, string predicates, security sanitizers

See [`docs/UTILITIES.md`](docs/UTILITIES.md) for the complete function reference.

### Build targets

- `:app` — dev demo at `public/index.html` (loads `public/js/main.js`)
- `:test` — browser-based tests served at `out/test/`
- `:lib` — one ESM file per component in `dist/`, each depending on a shared `:base` module

Note: `shadow-cljs.edn` defines more components than are currently in `package.json` `exports` (x-card, x-grid, x-stat, x-tab, x-tabs are built but not yet exported).

### Testing

Tests in `test/baredom/components/<name>/`:
- `model_test.cljs` — pure unit tests for normalization and derived values
- `<name>_test.cljs` — integration tests that register the element, mount it to `document.body`, and assert DOM/attribute/event behavior; use `use-fixtures :each` for cleanup

Tests run in a real browser environment (headless Chrome via shadow-cljs). Do not assume jsdom.

## Development Pipeline for New Components

Each new component must be developed on a dedicated feature branch named `feature/x-<name>`. Create the branch from `main` before starting work and merge back into `main` once the component passes verification.

Follow these stages in order. **Do not skip or merge stages.**

1. **Architecture** — define tag name, shadow DOM structure, rendering strategy, attributes, properties, events, theming, accessibility, open questions
2. **API Contract** — full tables for observed attributes, properties, events, public methods, slots, CSS custom properties, theme/motion/a11y behavior, rendering invariants
3. **Implementation** — all files in a single response: `model.cljs`, `<name>.cljs`, `exports/<name>.cljs`, `model_test.cljs`, `<name>_test.cljs`, `docs/<name>.md`, `demo/<name>.html`. Use shared utilities — never reimplement. **Registration checklist:**
   - `shadow-cljs.edn` — add `:x-<name>` module under `:lib :modules`
   - `package.json` — add `"./x-<name>"` entry under `"exports"`
   - `src/baredom/core.cljs` — require the export namespace and call `register!` in `start!`
   - `src/baredom/exports/all.cljs` — require the export namespace and call `register!`
   - `public/index.html` — add an entry to the `components` array in the `<script>` block (name, tag, file, category)
4. **Verification** — run these checks:
   - `clj-kondo --lint src test` — zero warnings/errors
   - `npx shadow-cljs release lib` — confirms Closure Advanced passes
   - `grep -r "deftype\|atom\|volatile!" src/baredom/components/x-<name>/` — must return nothing
   - Visually check: architecture conformance, API contract, stateless rendering, theming, motion, a11y, mobile readiness

## Error Recovery

- **Closure externs error** (`shadow-cljs release lib` fails with "Cannot read property X of undefined"): the fix is almost always a missing `^js` type hint on a function parameter that receives a DOM element or JS object.
- **Tests fail after a model change**: update `model_test.cljs` first (fix the pure logic), then fix the component integration test.
- **Infinite recursion in `attributeChangedCallback`**: you are setting an observed attribute on the host inside `apply-model!` without the model-change guard. Add the `when (not= new-m old-m)` check.
- **Property accessor not working**: ensure `property-api` includes the property and `du/install-properties!` is called in `setup-prototype-fn`.

## Performance & Context Management

1. **Reference Implementation:** Use `src/baredom/components/x_icon/` as the primary Golden Sample — it demonstrates all three named patterns correctly: render-pipeline, model-layer, and registration-idiom. Use `x-button` as secondary reference for event dispatch (`du/dispatch!`) and complex interaction state.
2. **No-Scan Folders:** Ignore `dist/`, `target/`, and `.shadow-cljs/`.

**If information is missing that affects the public API shape (attributes, properties, events, methods, slots), do not assume — list it as an open question. Never invent API surface.**
