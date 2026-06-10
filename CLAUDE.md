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

## Roadmap

See [`docs/x-trace-history-roadmap.md`](docs/x-trace-history-roadmap.md) — phased implementation plan for the `x-trace-history` debugger (Time-travel debugger for BareDOM components).

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

### Render-pipeline conformance — accepted variants

The full pipeline above is what new components should use. Two variants exist in the codebase for legitimate reasons and are documented here so reviewers don't try to "fix" them and so the next new component doesn't invent a third:

1. **Full pipeline (canonical).** Every component that renders visible DOM uses this. Golden sample: `x-icon`. If you find a visibly-rendering component that bypasses the change guard, it's a bug — recursion risk.
2. **Passive child elements — model-stash only.** A component that has no visible DOM of its own (its only job is to hold attributes for a parent to read) needs a cached model but does not need an `apply-model!`. Example: `x-welcome-tour-step` — the parent `x-welcome-tour` reads its model on each render; the step element itself never paints. These components have `read-model` + `update-model!` (cache only), no `apply-model!`. The change guard is *moot* because there are no effects to gate.

Anything else — a component that re-runs DOM writes on every `attributeChangedCallback` without comparing the cached model — should be moved onto the full pipeline.

### Naming conventions

- Functions ending with `!` have side effects (DOM mutation, event dispatch, state changes).
- Lifecycle callbacks: `connected!`, `disconnected!`, `attribute-changed!` — always named `defn-` (private) functions.
- Private instance-field keys: `"__xComponentNameField"` format (e.g. `"__xIconModel"`).

### Function size

Functions should do one thing. If a function exceeds ~15 lines, decompose it into named subfunctions. Long functions tend to mix calculation and effects — violating de-complect — and make Closure Advanced errors harder to trace.

**Orchestrators are exempt.** A function whose body is a phase list of named-helper calls (each line one call) reads cleaner at 20–30 lines than at 15. The rule targets functions that mix calculation and effect; collections of tiny named delegations are fine. After decomposition, `render!` should be a phase list, not the work itself — see the `render-orchestrator` named pattern.

### Named patterns

Reference by name when discussing architecture:

- **`render-pipeline`** — `update-from-attrs!`: `read-model` → change-guard → `apply-model!` (caches model at end). Golden sample: `x-icon`.
- **`model-layer`** — pure `model.cljs` with required metadata defs + `normalize`. Golden sample: `x-icon/model.cljs`. See [`docs/MODEL-LAYER.md`](docs/MODEL-LAYER.md) for field specs.
- **`registration-idiom`** — named lifecycle `defn-` functions + `component/register!` + `du/install-properties!`. Golden sample: `x-icon`. See [`docs/REGISTRATION.md`](docs/REGISTRATION.md) for template.
- **`render-orchestrator`** — `render!` (or `apply-model!`) reads as a phase list of named effect helpers (`apply-host-css-vars!`, `apply-area!`, …). Each phase is one named call; the orchestrator stays around 5–25 lines. References: `x-icon` (`apply-model!`, smallest readable demo), `x-chart` (`render!`), `x-color-picker` (`render!`).
- **`shadow-builders`** — break long `make-shadow!` / `init-dom!` into per-section builders that each create + decorate + assemble + return a refs map. The top-level builder composes them. References: `x-color-picker` (`make-trigger!`, `make-area-section!`, `make-strips-section!`, `make-controls-section!`), `x-scroll-timeline` (`make-track-section!`, `make-svg-track!`, `make-entries-section!`).
- **`listener-spec`** — for components with 5+ static listeners, encode them as a single vector of tuples and iterate. Two acceptable shapes, picked by whether the component needs a remove path:
  - **`[refs-key event handler-key capture?]`** — handler-key looks up a closure in a handlers JS object stashed on the host via `du/setv!`. Both `add-listeners!` and `remove-listeners!` iterate the same spec, so they cannot drift. Use when handlers must be removable across disconnect/reconnect (host listeners, document listeners, or anything that survives a teardown). References: `x-color-picker`, `x-multi-combobox` (`listener-spec` + `iter-listeners!`), `baredom.utils.overlay/attach-listener!`.
  - **`[refs-key event handler-fn]`** — handler-fn is a top-level `defn-` of `[^js el ^js event]` that the install path wraps in `(fn [event] (handler el event))`. No handlers map, no remove path. Use only when listeners bind to shadow children whose lifetime is tied to the shadow DOM itself (so they get GC'd with the element). Reference: `x-button` (`listener-spec` + `install-listeners!`).
  - Don't invent a third shape — pick whichever matches the removability requirement.

## Closure Advanced Compilation Safety

All code must survive Google Closure Advanced Compilation (minification + renaming).

**JS interop rules:**
- Use `(.-prop el)` and `(set! (.-prop el) val)` for native DOM properties
- For **host-element instance fields** (refs map, cached model, handler map, transient UI state), use `du/setv!` and `du/getv` — these route through the `x-trace-history` recorder hook. `gobj/get` / `gobj/set` stay reserved for non-host JS objects (e.g. reading a key out of the refs JS object once you have it). The `gobj/(set|get) el k-X` form is forbidden and enforced by `bb scripts/check_du_discipline.bb` in CI.
- Avoid dynamic property lookup
- Apply `^js` type hints on all parameters representing DOM nodes, events, or element instances
- Define constants for tag names, attribute names, event names, and CSS custom property names — no duplicated string literals
- Do not define a local `(defn- make-el [tag] (.createElement js/document tag))` shim — it conflicts with the test-side `make-el` and is a thin wrapper. Inline `.createElement js/document` directly. Enforced in CI. (Exception: `x-copy` retains a `[^js doc tag]` variant because the doc parameter is a real abstraction over a custom document — not a shim.)

**Forbidden:**
- React, Reagent, Lit, Vue, Svelte, Alpine, JSX, TypeScript, virtual DOM, template DSLs, framework runtimes
- `deftype`, `defrecord` for element classes

## Element Registration

Use `component/register!` with a declarative options map. **Do not create `element-class` functions manually.**

- Only add manual `.defineProperty` for properties with custom getter/setter logic
- Use `this-as` inside property setter bodies — never reference `this` directly
- **Forbidden:** manual `element-class` with `js*`, `js/Reflect.construct` with atoms, manual prototype composition

See [`docs/REGISTRATION.md`](docs/REGISTRATION.md) for the full template and rules.

### Property accessor tiers

Components install JS property accessors at one of three tiers. **Pick the simplest tier the component qualifies for.**

- **Tier 0** — `(du/install-properties! proto model/property-api)` (one-liner, data-driven). All props are simple bool/string/number reflectors. Reference: `x_icon`.
- **Tier 1** — Individual `du/define-{bool,string,number}-prop!` calls, usually mixed with `aset` for methods. Reference: `x_dropdown`.
- **Tier 2** — Hand-written `.defineProperty` for non-standard semantics (strict empty-string removal, side-effecting setters, CLJS-truthy edge cases, computed read-only props). Document the reason inline. Reference: `x_image`.

See [`docs/REGISTRATION.md`](docs/REGISTRATION.md) for the full taxonomy with examples and reference components per tier.

## Architecture

ClojureScript library compiling to standalone native Web Components (Custom Elements v1) via shadow-cljs `:esm` target. Each component is a separate ESM module with zero framework runtime dependency.

### Three-layer pattern per component

Every component under `src/baredom/components/<name>/` follows:

1. **`model.cljs`** — Pure functions only. No DOM or side effects. Required defs: `tag-name`, `observed-attributes`, `property-api`, `event-schema`, `method-api`. See [`docs/MODEL-LAYER.md`](docs/MODEL-LAYER.md).
2. **`<name>.cljs`** — DOM and lifecycle layer. Shadow DOM creation, `render!`, event wiring, lifecycle callbacks, `init!` via `component/register!`.
3. **`src/baredom/exports/<name>.cljs`** — ESM entry point. Exposes `^:export init`, `register!`, and `public-api` metadata.

**Model fns have two callers, not one.** A `model.cljs` function is called both from its component (which supplies fully-shaped input via `read-model`) *and* directly from `model_test.cljs` (which often passes sparse maps like `{}`). The input contract that holds in production does **not** hold in tests. Concretely: if a destructured key like `:label-present?` is documented as `boolean`, tests may still pass `nil` for it, and `(and nil …)` returns `nil` rather than `false`. **Coerce output booleans explicitly** at the return site — e.g. `:labelled? (boolean (and label-present? …))` — instead of relying on inputs to already be booleans. The same rule applies to any output whose downstream consumer (or test assertion) checks a strict predicate like `false?` / `true?` / `nil?`.

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
- **`du/setv!`** / **`du/getv`** — host-element instance-field storage (refs, model cache, handlers). The trace-recorder hook lives here; `gobj/set` / `gobj/get` on `el` is forbidden and enforced by `bb scripts/check_du_discipline.bb`. Use `gobj` only on non-host JS objects.
- **`du/set-attr!`** / **`du/remove-attr!`** — attribute writes. Goes through the trace recorder. **Exception:** per-frame writes inside `requestAnimationFrame` loops (e.g. `animate!` → `render-*!`) use **`du/set-attr-untraced!`** / **`du/remove-attr-untraced!`** to keep the recorder readable. Even after host-attribution + rate-limiting, a high-fanout animation can emit 60+ records/sec under distinct attribute keys; the untraced variants do the native DOM write without firing the hook. Use only in hot paths, with a one-line `;; Hot path: rAF-driven` comment so the intent is greppable. References: `x_liquid_glass/render-satellites!`, `x_soft_body/render-path!`, `x_liquid_dock/animate!`.
- **`du/setv-untraced!`** — instance-field write without firing the trace hook. Reserved for **bookkeeping with no diagnostic display value**, of two kinds. (1) **Animation bookkeeping** stamped every frame: the canonical trio is **`k-raf`** (requestAnimationFrame id), **`k-last-frame`** (previous frame timestamp), and **`k-time`** (accumulated animation time) — the recorder seeing 60+ writes/sec of these adds noise without adding signal. (2) **Transient non-displayable handles** whose *value* is opaque and uninterpretable in a time-travel trace (and may not be structured-cloneable) — e.g. an in-flight **`AbortController`** (`k-abort`), where the *meaningful* lifecycle it tracks is already traced through a sibling state field + dispatched event. In both cases add a one-line greppable rationale at the field (mirroring the `;; Hot path: rAF-driven` attr-write convention) so the deviation reads as intentional. Use the normal `du/setv!` for actual UI state (selection, hover, pressed, active-source, …).
- **`du/has-attr?`** / **`du/get-attr`** — attribute reads in `read-model`
- **`du/dispatch!`** / **`du/dispatch-cancelable!`** — event dispatch
- **`du/install-properties!`** — install property accessors from `model/property-api` (Tier 0; see _Property accessor tiers_ above)
- **`du/define-bool-prop!`** / **`du/define-string-prop!`** / **`du/define-number-prop!`** — install single accessor (Tier 1)
- **`mu/`** — boolean parsing, string predicates, security sanitizers

See [`docs/UTILITIES.md`](docs/UTILITIES.md) for the complete function reference.

### Reuse library components inside dev tools, demos, and compound components

When building anything that needs a button, checkbox, select, search field, dropdown, modal, drawer, tooltip, etc., **reach for the corresponding `x-*` component first**. The library's own surfaces should dogfood the library:

- Dev tools under `src/baredom/dev/` (`x-debug`, `x-trace-history`) are first-class consumers of `x-button`, `x-checkbox`, `x-select`, `x-search-field`, and friends.
- Compound components that legitimately compose smaller ones (`x-table` → `x-table-row`/`x-table-cell`, `x-tabs` → `x-tab`, `x-form` → `x-form-field`) require the implementation namespace directly.
- Demo pages and the trace viewer (`public/viewer.html`) load the components they showcase.

**Why:** the alternative is bespoke HTML + ad-hoc CSS that drifts in accessibility, theming, and behaviour. A native `<input type="search">` next to an `x-select` in the same toolbar looks like a regression to a library reviewer; using `x-search-field` keeps the dock visually and behaviourally aligned with everything else the library ships. It also exercises every component in real use, which catches usability issues that pure unit tests miss.

**How to apply:**

- For dev tools and compound components, require the **implementation namespace** (`baredom.components.x-search-field.x-search-field`) and call `init!` in your `register!`. Do not require the exports namespace (`baredom.exports.x-search-field`) — each exports ns is the entry of a separate `:lib` module, and pulling one in from another module forces shadow-cljs to relocate the entry into `:base` and breaks the per-module ESM split. See `src/baredom/dev/x_trace_history/x_trace_history.cljs` for the canonical pattern.
- Listen for the component's CustomEvents (`x-search-field-input`, `x-checkbox-change`, `select-change`, …) rather than native DOM events. Use `(.. e -detail -value)` to read the payload.
- Bundle-size reality: each component referenced from a dev-tool or compound module gets promoted into `base.js` by shadow-cljs (it's now reachable from more than one module entry). That's the *correct* outcome — it deduplicates the implementation across every consumer — but it does mean `base.js` grows by ~1–3 KB gzipped per added component. The bundle-size budget in `scripts/check-bundle-size.sh` accommodates this; verify after every addition.
- For dev tools, ensure the recorder's internal-host filter still covers the new component's events. `recorder/mark-internal!` on the dev tool's host element causes `inside-internal-host?` to walk the shadow chain — events from any nested `x-*` inside the marked host are correctly filtered. Add a test that the dev tool's own component interactions do not pollute live records (see `search-typing-does-not-pollute-live-records-test` in the trace-history suite for the pattern).

**When to skip the rule:**

- Inside a component's own implementation (`x-button` cannot use `x-button`).
- When the surface absolutely cannot afford `base.js` growth (extremely rare; dev tools and demos do not qualify).
- When the `x-*` equivalent is functionally insufficient for the use case — but flag the gap; usually the right answer is to extend the component rather than fork it.

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

**Async tests need map-style fixtures.** `cljs.test/async` rejects function-style `use-fixtures` with "Async tests require fixtures to be specified as maps." If any test in a namespace is async, switch the file to `(use-fixtures :each {:after (fn [] ...)})` (or `:before`) — even the synchronous tests in that namespace must use the map form.

**Custom-element tests with ResizeObserver dependencies need explicit dimensions.** An empty custom element has zero size in the headless test page; `ResizeObserver` callbacks gate on `width > 0 && height > 0` and won't fire. Set `width` / `height` inline on the host before appending if the test exercises code triggered by resize observers (palette extraction, canvas sizing, etc.).

## Development Pipeline for New Components

Each new component must be developed on a dedicated feature branch named `feature/x-<name>`. Create the branch from `main` before starting work and merge back into `main` once the component passes verification.

Follow these stages in order. **Do not skip or merge stages.**

1. **Architecture** — define tag name, shadow DOM structure, rendering strategy, attributes, properties, events, theming, accessibility, open questions
2. **API Contract** — full tables for observed attributes, properties, events, public methods, slots, CSS custom properties, theme/motion/a11y behavior, rendering invariants
3. **Implementation** — all files in a single response: `model.cljs`, `<name>.cljs`, `exports/<name>.cljs`, `model_test.cljs`, `<name>_test.cljs`, `docs/<name>.md`, `demo/<name>.html`. Use shared utilities — never reimplement. **Registration checklist:**
   - `shadow-cljs.edn` — add `:x-<name>` module under `:lib :modules`
   - `package.json` — add `"./x-<name>"` entry under `"exports"`
   - `src/baredom/registry.cljs` — require the export namespace and add `<alias>/register!` to `all-registers` (single source of truth; `baredom.core/start!` and `baredom.exports.all/init` both consume this vector)
   - `public/index.html` — add an entry to the `components` array in the `<script>` block (name, tag, file, category)
   - `docs/components.md` — add a row in the appropriate category table (Form / Feedback / Navigation / Layout / Data / Overlay / Display / Animation / Effects / Scroll / Utility). If no existing category fits, flag the gap to the user before inventing a new one — taxonomy decisions affect the README overview too.
   - `README.md` — bump the count in the Components overview table for that category and, if the new component is a flagship for its category, add it to the Examples column.
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
- **`getComputedStyle().backgroundColor` (or any CSS-transitioned property) returns the wrong value**: it returns the *in-flight transitioning value*, not the target. If the element has `transition: background ...` (or similar on the property you're sampling), reading within the transition window gives the *previous* value. Read the resolved CSS custom property directly via `getPropertyValue('--x-component-...-bg')` instead — custom-property values update synchronously with the cascade, no transition involved.
- **Refactor exposes a pre-existing bug**: when a unit test added during a refactor PR fails on logic you didn't touch, suspect a pre-existing bug. Don't change the refactor's tests to lock in the buggy behaviour — scope the test to the function's natural input range, ship the refactor, then ship a separate behaviour-changing PR with a one-line fix and a visual demo check.

## Performance & Context Management

1. **Reference Implementation:** Use `src/baredom/components/x_icon/` as the primary Golden Sample — it demonstrates all three named patterns correctly: render-pipeline, model-layer, and registration-idiom. Use `x-button` as secondary reference for event dispatch (`du/dispatch!`) and complex interaction state.
2. **No-Scan Folders:** Ignore `dist/`, `target/`, and `.shadow-cljs/`.

**If information is missing that affects the public API shape (attributes, properties, events, methods, slots), do not assume — list it as an open question. Never invent API surface.**
