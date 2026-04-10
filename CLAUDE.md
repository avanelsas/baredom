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

## Core Design Principle

Components are **stateless**. The single rendering model is:

```
DOM = f(attributes, properties)
```

**Forbidden state mechanisms — never use:**
- `atom`, `volatile!`
- `deftype`, `defrecord`
- `core.async`
- Reactive signals or state containers

Instance fields may only store shadow root references and cached DOM node handles. Never store authoritative UI state.

## Closure Advanced Compilation Safety

All code must survive Google Closure Advanced Compilation (minification + renaming).

**JS interop rules:**
- Use `(.-prop el)` and `(set! (.-prop el) val)` for native DOM properties
- Use `aget`/`aset` (or `goog.object/get` / `goog.object/set`) for **non-native instance fields** (custom properties stored on element instances)
- Avoid dynamic property lookup
- Apply `^js` type hints on all parameters representing DOM nodes, events, or element instances
- Define constants for tag names, attribute names, event names, and CSS custom property names — no duplicated string literals

**Forbidden:**
- React, Reagent, Lit, Vue, Svelte, Alpine, JSX, TypeScript, virtual DOM, template DSLs, framework runtimes
- `deftype`, `defrecord` for element classes

## Element Class Definition

Custom elements are defined using `js*` to produce a native ES class that extends `HTMLElement`. **This is the only permitted pattern** — do not use `Reflect.construct`, manual prototype composition, or `atom`-based constructor wiring.

```clojure
(defn- element-class []
  (let [klass (js* "(class extends HTMLElement {})")]

    (set! (.-observedAttributes klass) model/observed-attributes)

    (set! (.-connectedCallback (.-prototype klass))
          (fn [] (this-as ^js this (connected! this))))
    (set! (.-disconnectedCallback (.-prototype klass))
          (fn [] (this-as ^js this (disconnected! this))))
    (set! (.-attributeChangedCallback (.-prototype klass))
          (fn [n o v] (this-as ^js this (attribute-changed! this n o v))))

    ;; install property accessors and methods on the prototype here

    klass))
```

The `this-as` macro must always be used to capture the element instance — never reference `this` directly in callbacks or nested functions.

Registration is idempotent: always guard with `(when-not (.get js/customElements tag-name) ...)`.

**Forbidden registration patterns:**
- `js/Reflect.construct` with `atom`-based constructor wiring
- Manual `js/Object.create` / `js/Object.setPrototypeOf` prototype composition

## Architecture

This is a ClojureScript library that compiles to standalone native Web Components (Custom Elements v1) using shadow-cljs `:esm` target. Each component is a separate ESM module with zero framework runtime dependency.

### Three-layer pattern per component

Every component under `src/baredom/components/<name>/` follows:

1. **`model.cljs`** — Pure functions only. Defines tag name, attribute names, event names, slot names, allowed enum values with normalization, and derived view-model logic. No DOM or side effects. All types described with ClojureScript predicates (`string?`, `boolean?`, etc.) — never TypeScript syntax.

2. **`<name>.cljs`** — DOM and lifecycle layer. Implements shadow DOM creation, imperative `render!`, event wiring, and lifecycle callbacks (`connected!`, `disconnected!`, `attribute-changed!`, `define-element!`, `init!`). All browser interop must be Closure Advanced safe.

3. **`src/baredom/exports/<name>.cljs`** — ESM entry point. Exposes `^:export init` (called by the `:lib` shadow-cljs build) and `register!`. Also exposes `public-api` metadata derived from the model.

### Additional files per component

- **`docs/<name>.md`** — Developer documentation: tag name, attributes, properties, events, slots, CSS custom properties, accessibility, usage examples. Must reflect the API contract exactly.
- **`demo/<name>.html`** — Standalone demo page loaded via the dev bundle. Shows the component in multiple states, includes a live control panel for changing attributes/properties, and logs all events on-page.

### Shadow DOM and styling

All components use open shadow DOM (`mode: "open"`). Styles are inline ClojureScript strings. CSS custom properties follow `--x-<component>-<property>` naming. Light/dark mode is handled via `@media (prefers-color-scheme: dark)` inside the shadow style string. Animations must respect `@media (prefers-reduced-motion: reduce)`.

### Theming

All components must consume the shared design tokens defined by `x-theme` (`src/baredom/components/x_theme/model.cljs`). In `:host` CSS custom property declarations, wrap hardcoded values with `var(--x-token, fallback)` so the original value is preserved when no `<x-theme>` is present:

```css
/* Correct — themed with fallback */
--x-foo-bg: var(--x-color-surface, #ffffff);

/* Wrong — ignores theme tokens */
--x-foo-bg: #ffffff;
```

**Token usage rules:**
- **Colors:** Use `--x-color-primary/hover/active`, `--x-color-secondary/...`, `--x-color-tertiary/...`, `--x-color-surface/hover/active`, `--x-color-bg`, `--x-color-text`, `--x-color-text-muted`, `--x-color-border`, `--x-color-focus-ring`, `--x-color-danger`, `--x-color-success`, `--x-color-warning`
- **Shape:** `--x-radius-sm/md/lg/full`, **Shadows:** `--x-shadow-sm/md/lg`, **Motion:** `--x-transition-duration`, `--x-transition-easing`, **Typography:** `--x-font-size-sm/base`, `--x-font-family`
- **Overlays** (modals, drawers, menus, popovers, toasts, dropdowns, command palette): use `--x-color-bg` (always opaque), never `--x-color-surface` (can be semi-transparent in Aurora theme)
- **Inline surfaces** (cards, fieldsets, collapse panels): use `--x-color-surface`
- **Do not theme:** component-specific spacing, font-weight, line-height, opacity, decorative palette colours (e.g. x-liquid-fill gold), white-on-coloured-button foreground, rgba variant tints (info/success/warning/error overlays)

**Demo pages:**
- Include `<script src="demo-theme.js" defer></script>` in every demo page
- Use `var(--page-bg)`, `var(--surface-bg)`, `var(--input-bg)` etc. from `demo-responsive.css` — never hardcode colours
- If a demo defines its own `:root` variables, add an `x-theme { ... }` block that re-maps them to theme tokens (`:root` resolves before `<x-theme>` in the DOM tree)

### Mobile and responsive

All components must work on viewports from 320px up. Apply these rules in every component:

- **No fixed widths that can overflow.** Cap with `min(Xpx, calc(100vw - 2rem))` or add `max-width:calc(100vw - 1rem)` on positioned panels (menus, popovers, dropdowns, modals).
- **Use `100dvh`, never `100vh`.** Mobile browsers have dynamic toolbars that change the viewport height; `dvh` accounts for this.
- **Use `100%`, never `100vw`** for full-width elements. `100vw` includes scrollbar width and causes horizontal overflow.
- **Use pointer events, never mouse events.** `pointermove`, `pointerdown`, `pointerup`, `pointerenter`, `pointerleave` — these fire on both mouse and touch. Never use `mousemove`, `mousedown`, `mouseenter`, etc.
- **Touch targets ≥ 44px on coarse pointers.** Add `@media (pointer:coarse)` rules to enlarge interactive elements (thumbs, buttons) that are smaller than 44px at their default size.
- **Demo pages** link `demo-responsive.css` for shared responsive breakpoints and theme. Use `var(--page-bg)`, `var(--surface-bg)`, etc. — do not hardcode theme colours in demo HTML.

### State storage on element instances

Internal interaction state (hover, focus-visible, active-source) is stored as JS properties on the element instance using `aget`/`aset` with private string keys (e.g. `"__xButtonState"`). Public state is always re-derived from HTML attributes at render time.

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
3. **Implementation** — all files in a single response: `model.cljs`, `<name>.cljs`, `exports/<name>.cljs`, `model_test.cljs`, `<name>_test.cljs`, `docs/<name>.md`, `demo/<name>.html`. **Registration checklist** — after creating a new component, register it in all four places:
   - `shadow-cljs.edn` — add `:x-<name>` module under `:lib :modules`
   - `package.json` — add `"./x-<name>"` entry under `"exports"`
   - `src/baredom/core.cljs` — require the export namespace and call `register!` in `start!`
   - `src/baredom/exports/all.cljs` — require the export namespace and call `register!`
   - `public/index.html` — add an entry to the `components` array in the `<script>` block (name, tag, file, category)
4. **Verification** — check architecture conformance, API contract conformance, Closure Advanced safety, stateless rendering, theming, motion, accessibility, mobile readiness (no overflow at 320px, pointer events, touch targets)

## Performance & Context Management
To minimize token usage and latency:
1. **Reference Implementation:** Use `src/baredom/components/x-alert/` as the "Golden Sample" for all patterns.
2. **No-Scan Folders:** Ignore `dist/`, `target/`, and `.shadow-cljs/`.

**If information is missing that affects the public API shape (attributes, properties, events, methods, slots), do not assume — list it as an open question. Never invent API surface.**
