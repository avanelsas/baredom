# Changelog

All notable changes to BareDOM will be documented in this file.

## [3.1.0] - 2026-05-16

A full-library audit cycle against the `x-icon` golden sample (PRs #222–#231). All 99 components now conform to the Hickey-level architecture standard documented in `CLAUDE.md`. No public API or behavioural changes.

### Changed

- **`x-trace-history` recorder noise reduction** — rAF hot paths in x-kinetic-canvas, x-ripple-effect, x-scroll-parallax, x-scroll-stack, and x-particle-button now consistently route per-frame writes (`k-raf`, `k-last-frame`, `k-time` animation bookkeeping; per-frame attribute updates inside `animate!` loops) through `du/setv-untraced!` / `du/set-attr-untraced!`. The recorder no longer drowns in 60×/sec writes that carry no diagnostic value.
- **`property-api` metadata consistency** — `:reflects-attribute` entries added across x-progress-circle, x-stepper, x-splash, x-table-cell, x-table-row, x-theme, x-timeline, x-timeline-item, and x-toaster. The React and Angular code generators (`generate_react.bb` / `generate_angular.bb`) and `metadata.bb` now see a complete property-to-attribute contract for every component.
- **Render-pipeline decomposition** — oversized `apply-model!` / `init-dom!` / `make-shadow!` / `create-shadow!` / `start-transition!` / `build-refs!` functions split into named phase helpers across 10 components (x-button, x-dropdown, x-navbar, x-toast, x-select, x-kinetic-font, x-kinetic-typography, x-morph-stack, x-avatar-group, x-combobox).
- **`du`-discipline sweep** — cleared 41 `gobj/get|set` calls on host elements in x-sidebar plus stray hits in x-radio. Broadened `scripts/check_du_discipline.bb` twice (to catch the `instance` host-variable name and the multi-line `(defn- make-el [tag] …)` shim) so the patterns can't regress in CI.
- **Pipeline canonicalisation** — x-select moved its change-guard into `update-from-attrs!` and adopted the canonical `[el m]` `apply-model!` signature. x-card's `event-schema` switched to the canonical `{event-symbol {:detail {…}}}` shape. x-radio decomposed `try-select!` into named phase helpers.

### Fixed

- **x-progress-circle** — `variant`, `size`, and `label` are now exposed as JS properties. They were observed attributes without corresponding property installations, so `el.variant = "primary"` was a silent no-op.
- **x-collapse** — `read-model` now routes through `model/normalize`. It was reconstructing the view-model inline and duplicating the duration-ms parsing/clamping logic the model layer already provides.
- **x-date-picker** — Removed a forbidden multi-line `(defn- make-el [tag] …)` shim that the CI regex was missing; 13 call sites inlined to `.createElement js/document` directly.

## [3.0.0] - 2026-05-12

### Added

- **x-trace-history** — A time-travel debugger for BareDOM components. Records every CustomEvent, attribute mutation, instance-field write, and lifecycle callback as plain JS records with a stable schema. Floating dock with cross-instance lanes, scrubber, synchronous cause→effect causality DAG view, heatmap density rendering for animation-heavy lanes, full-text search across recorded values, bounded recording sessions, and JSON export/import. Ships as a separate ESM module (`@vanelsas/baredom/x-trace-history`) activated by `?baredom-trace-history` URL param or `window.BAREDOM_TRACE_HISTORY = true` — zero overhead when off. TypeScript declarations (`TraceRecord` discriminated union + `window.BareDOM.traceHistory` console API) included. See [`docs/x-trace-history.md`](docs/x-trace-history.md) and [`docs/x-trace-history-schema.md`](docs/x-trace-history-schema.md).
- **Standalone trace viewer** — `public/viewer.html` deployed alongside the demo site for sharing recorded traces. Drag-drop a `.trace.json` file or pass `?trace=<base64>` to load a trace into the dock.
- **Adapter integration tests** — React (`adapters/react/test-app/`) and Angular (`adapters/angular/test-app/`) test-apps now exercise the trace-history dock end-to-end, with dev-tool guidance added to each adapter's README and main doc.
- **Dispatch + lifecycle hook points** — `baredom.utils.dom` and `baredom.utils.component` expose `*trace-hook*` / `*lifecycle-hook*` extension atoms gating instrumentation of every CustomEvent dispatch, attribute mutation, instance-field write, and lifecycle callback. One nil-check cost when no recorder is installed.

### Fixed

- **x-select** — Slotted `<option selected>` default no longer gets stomped on render when the host has no `value` attribute. An explicit `value` attribute still overrides the slotted default.

### Changed

- **Tooling** — Sibling demo build outputs (`bare-demo/public/js/`, `bare-html/js/`, etc.), the Angular CLI cache, and the clj-kondo cache are now correctly gitignored. Adapter test-app `package-lock.json` files are tracked for reproducible smoke-test installs.

## [2.9.0] - 2026-05-09

### Added

- **x-spotlight-card** — Card surface with a cursor-following spotlight highlight (motion-respecting, theme-aware).
- **x-confetti** — Imperative celebration burst component with canvas particles.
- **x-kbd** — Platform-aware keyboard key / shortcut display.
- **x-otp-input** — Form-associated OTP / verification-code input.
- **x-proximity-list** — Dock-effect list with proximity scaling.

### Changed

- **Centralized component registration** — `baredom.registry` is now the single source of truth for the full component set; `baredom.core/start!` and `baredom.exports.all/init` both consume the same vector.
- **Overlay listener registry** — Replaced overlay magic-string listener keys with `baredom.utils.overlay/attach-listener!`, eliminating drift between add/remove sites.
- **Pure model layer expanded** — Pointer-coord math moved out of `x-color-picker` into the model layer; coordinate math moved out of `x-chart` into the model layer; series-point computation in `x-chart` is now pure.
- **Render-pipeline decomposition** — `render!` (and adjacent monolithic functions) decomposed into named phase helpers in: `x-chart`, `x-button`, `x-combobox`, `x-color-picker`, `x-scroll-timeline`, `x-particle-button`. Each `render!` now reads as a phase list (`render-orchestrator` pattern); long shadow builders split per the `shadow-builders` pattern; `x-color-picker` listener wiring moved to a single `listener-spec` table.
- **`x-proximity-list` settling loop** — Replaced the JS-object settling flag with a `loop`/`recur` driver, removing mutable instance state.
- **`x-date-picker`** — Force-close moved out of `render!` (audit Phase 5).
- **Host attribute access** — Migrated to `du/*` wrappers across the remaining call sites (audit Wave 4c).
- **Utils contracts** — Documented `baredom.utils.*` contracts; `normalize-prop-val` extracted as a reusable helper.
- **shadow-cljs** — Bumped to 3.4.7.
- **CLAUDE.md** — Added audit-derived guidance (named patterns, accessor tiers, error-recovery entries, function-size rule with orchestrator exemption).

### Fixed

- **x-chart** — Category x-axis off-by-one on the rightmost label.
- **x-particle-button** — Palette no longer goes stale after a `variant` change; the cached palette is invalidated when variant attributes flip.

## [2.8.0] - 2026-05-06

### Added

- **x-i18n / x-i18n-provider** — Translation components. `x-i18n-provider` holds the dictionary and active locale; `x-i18n` renders a translated string for a given key with parameter interpolation.
- **x-kinetic-canvas** — Animated background component (canvas-based, motion-respecting).
- **x-multi-combobox** — Combobox variant with multi-select chips, keyboard navigation, and a cancelable `change-request` event.

### Changed

- **Internal architecture audit** — 18 PRs across the component library: `event-schema` defs added to every model, lifecycle callbacks normalized to `!`-suffix `defn-` form, `^js` hints filled in for Closure Advanced safety, event dispatch routed through `du/dispatch!` / `du/dispatch-cancelable!`, and property accessors migrated to `du/define-*-prop!` helpers. 36 components now register properties via the one-line data-driven `du/install-properties!` (Tier 0). Net several thousand lines removed. **No public API changes.**
- **Tier 0/1/2 property-accessor taxonomy** — Documented in `CLAUDE.md` and `docs/REGISTRATION.md` so contributors pick the simplest registration tier per component.
- **Pre-commit hook** — Now auto-regenerates the Angular adapter when `src/` changes.

### Fixed

- **x-combobox / x-multi-combobox** — Render pipeline now applies the model-change guard, preventing infinite `attributeChangedCallback` recursion.
- **x-kinetic-typography** — SVG path `d` attribute is now sanitized via `mu/sanitize-svg-path-d`.
- **x-i18n-provider** — Internal state encapsulated behind the public API.
- **x-kinetic-canvas docs** — CSS custom property defaults synced with implementation.
- **x-icon** — Property test now forwards `:default` in `install-properties!` for string props.

## [2.7.0] - 2026-05-02

### Changed

- **Component factory** — All 91 components now register via `component/register!`, eliminating manual `element-class` boilerplate. Shared utility modules (`baredom.utils.dom`, `baredom.utils.model`, `baredom.utils.component`) drive lifecycle wiring, property accessors, event dispatch, and attribute helpers. Net reduction of ~2 285 lines.

### Fixed

- **x-command-palette** — Search field now correctly receives focus when the palette opens.
- **x-package.json** — Fixed export ordering and regenerated type declarations.
- **Test runner** — Fixed `classList` TypeError caused by `dom_test` cleanup removing the `cljs-test-display` root element. Suppressed x-image missing-alt warnings in test fixtures.

## [2.6.0] - 2026-04-30

### Added

- **Cancelable change-request events** — Seven input components now fire a cancelable `change-request` event before applying user-initiated value changes. Call `preventDefault()` to block the update (enables controlled component patterns in framework adapters). Components: x-slider, x-text-area, x-select, x-combobox, x-currency-field, x-tabs, x-pagination.

### Fixed

- **x-context-menu** — Escape key and click-outside now correctly dismiss the menu. Handlers moved from the overlay layer (which has `pointer-events: none`) to document-level listeners.
- **x-button** — Fixed missing press events on mobile Safari. Added `touch-action: manipulation` and `-webkit-tap-highlight-color: transparent` to the internal button element (reset by `all: unset`).
- **x-carousel demo** — Control panel now uses correct BareDOM event names (`x-switch-change`, `x-form-field-input`, `select-change`, `press`) instead of native DOM events.
- **x-welcome-tour test** — Added missing `^js` type hint to fix Closure Advanced compilation warning.
- **x-combobox** — Fixed undefined category in demo gallery (changed from `"input"` to `"form"`).

## [2.5.0] - 2026-04-29

### Added

- **TypeScript support** — First-class `.d.ts` type declarations auto-generated from component model metadata. Per-component interfaces with typed properties, methods, and `addEventListener` overloads. Custom Elements Manifest (`custom-elements.json`) for IDE support.
- **Debug mode** — Dev-only visual debugging overlay for all BareDOM components. Activate via `?baredom-debug` URL param or `window.BAREDOM_DEBUG = true`. Shows dashed outlines, tag labels on hover, and a floating inspection panel with live attribute/property editing and structured console logging. Excluded from the production `:lib` build.

### Fixed

- **TypeScript declarations** — Fixed invalid kebab-case identifiers in generated `.d.ts` files. Property names like `max-items` and event detail keys like `press-x` are now correctly output as camelCase (`maxItems`, `pressX`). Affected components: x-breadcrumbs, x-context-menu, x-pagination, x-particle-button.

## [2.4.1] - 2026-04-28

### Fixed

- **x-welcome-tour** — Fixed console error caused by `feGaussianBlur` `stdDeviation` attribute receiving a CSS `var()` value instead of a number. SVG filter attributes don't support CSS custom properties.

## [2.4.0] - 2026-04-27

### Added

- **x-welcome-tour** — Guided product tour component with spotlight backdrop (SVG mask cutout + glow ring), anchored popover, and configurable connectors (`arrow`, `line`, `curve`, `none`). Compound pattern with `x-welcome-tour-step` child elements for declarative step definitions. 8-placement positioning with automatic flip, smooth step transitions, focus trap, keyboard navigation, and full x-theme integration.
- **x-file-upload** — Drag-and-drop file upload component with click-to-browse fallback, file type filtering via `accept` attribute, size limits, and multi-file support.
- **SRI integrity.json** — Build now generates `dist/integrity.json` containing Subresource Integrity hashes for all ESM output files.

### Fixed

- **x-file-upload** — `matches-accept?` now returns a boolean instead of nil for non-matching types.

## [2.3.2] - 2026-04-23

### Fixed

- **Demo gallery** — Fixed scroll issue on iOS Safari and Android landscape. Replaced app-shell layout (`height:100%` + `overflow:hidden` on body) with natural document scrolling using `min-height:100dvh`.

## [2.3.1] - 2026-04-23

### Added

- **x-popover** — New `portal` boolean attribute that renders the panel in a document-level overlay layer, escaping CSS stacking contexts (sticky navbars with `backdrop-filter`, modals, etc.). Content nodes are moved (not cloned) to preserve event listeners and returned to the host on close.
- **baredom.utils.overlay** — New shared utility module for document-level overlay root management (`ensure-overlay-root!`, `make-layer!`, `remove-layer!`).

### Changed

- **x-context-menu** — Refactored to use shared `baredom.utils.overlay` module (no behavior change).

## [2.3.0] - 2026-04-22

### Added

- **x-tooltip** — Lightweight non-interactive overlay showing supplementary text on hover/focus. Supports 4 placements (top, bottom, left, right), configurable show delay, rich content via named slot, and full keyboard/screen-reader accessibility (role=tooltip, aria-describedby, Escape to dismiss, instant show on focusin).
- **x-combobox** — Single-select combobox with type-ahead filtering over slot-based `<option>` children. Case-insensitive substring matching with highlighted results, keyboard navigation (Arrow Up/Down, Enter, Escape, Home, End), clear button, WAI-ARIA combobox pattern (role=combobox, aria-activedescendant, role=listbox/option).
- **x-skeleton-group** — Container that wraps `x-skeleton` children to synchronize animation timing and generate common skeleton layouts from a `preset` attribute (card, list-item, paragraph, table-row, profile). Uses inherited `--x-skeleton-delay` CSS custom property for phase alignment.

### Changed

- **x-skeleton** — Added `animation-delay: var(--x-skeleton-delay, 0s)` to pulse and wave CSS rules (non-breaking; enables synchronization when used inside `x-skeleton-group`).

## [2.2.0] - 2026-04-15

### Added

- **x-image** — Stateless image component that wraps a native `<img>` in shadow DOM with a skeleton shimmer placeholder, fade-in on load, optional `ratio` attribute to reserve space and prevent layout shift, a default or slotted error fallback, and enforced `alt` hygiene (decorative mode available). Attributes: `src`, `alt`, `decorative`, `ratio`, `fit`, `position`, `loading`. Events: `x-image-load`, `x-image-error`. Read-only properties: `naturalWidth`, `naturalHeight`, `state`.
- **x-icon** — Stateless slot-based wrapper for SVG icons. Handles sizing (`sm`/`md`/`lg`/`xl` tokens or a numeric pixel value), theme-aware colour via `currentColor` inheritance or the `color` attribute (`inherit`/`primary`/`secondary`/`tertiary`/`success`/`warning`/`danger`/`muted`), and accessibility (decorative by default; `label` attribute flips on `role="img"` + `aria-label`). Consumers provide the `<svg>` in the default slot using `fill="currentColor"` / `stroke="currentColor"` for colour propagation.

### Changed

- **x-navbar** default look is now a site header instead of a rounded card. `--x-navbar-radius` defaults to `0` (was `1rem`) and the full four-sided border has been replaced with a single 1px bottom separator. Variant overrides (`subtle`, `inverted`, `transparent`) updated to target `border-bottom-color` accordingly. The rounded card look remains reachable via `style="--x-navbar-radius: 1rem"` — no public API changes. Visual regression for consumers who relied on the previous default; the escape hatch is one line.

## [2.1.1] - 2026-04-13

### Fixed

- x-table-cell: cell `border-bottom` lines now connect cleanly across columns with uneven intrinsic content heights (e.g. a header cell with a sort button next to a plain label cell). Inner `[part=cell]` wrapper now fills the host's row-stretched height so the border sits at the row's bottom edge.

### Demos (no impact on published library)

- Demo gallery now dogfoods the library: every `<button>`, `<select>`, `<input type="text|number|...">`, `<input type="checkbox">`, status pill, tag chip, and `<hr>` in the ~80 demo pages has been swapped to the matching library component (`<x-button>`, `<x-select>`, `<x-form-field>`, `<x-switch>` / `<x-checkbox>`, `<x-badge>`, `<x-chip>`, `<x-divider>`).
- Section eyebrow `<h2>` and page title `<h1>` headings swapped to `<x-typography variant="overline"|"h1">`.
- Shared compatibility shims in `demo/demo-theme.js` re-dispatch `select-change` / `x-switch-change` / `x-checkbox-change` / `x-form-field-input` / `x-form-field-change` as native `change` / `input` events so existing demo handlers keep working.
- `demo/demo-responsive.css` carries shared sizing/layout overrides for the dogfooded controls so they match each demo's local visual rhythm.
- Several latent demo bugs surfaced and fixed during the sweep: x-soft-body unreadable text in dark mode, x-morph-stack `button[data-stack]` selector silently skipping x-button, x-progress-circle slider max not syncing with the max field, theme-picker bar z-index covering overlay components, and a handful of demos with leftover `style=""` / `class=""` from their native-element days.

## [2.1.0] - 2026-04-13

### Added

- **x-carousel** — Themeable, accessible carousel with swipe/drag navigation, arrow buttons, dot indicators, autoplay, keyboard interaction, slide/fade transitions, and horizontal/vertical orientation
- **x-color-picker** — Color picker with 2D saturation/brightness area, hue strip, optional alpha channel, preset swatches, eyedropper support, and clipboard copy; supports inline and popover display modes
- **x-particle-button** — Button that emits and reabsorbs visual fragments on interaction, combining a premium base, pointer-tracked specular highlight, and a canvas particle system (modes: subtle, spark, ember, disperse)
- x-theme: design tokens expanded from 33 to 50 (adds z-index, opacity, and additional colour/spacing/shape tokens for overlay stacking and disabled states)
- Shared DOM and model utilities (`baredom.utils.dom`, `baredom.utils.model`) so components no longer reimplement attribute parsing, instance-field access, and security sanitisers
- CI quality checks and bundle-size documentation

### Changed

- `public/index.html` component gallery now consumes x-theme design tokens

### Fixed

- CSS injection and XSS vulnerabilities in input sanitization
- x-liquid-fill: `disabled` attribute now correctly suspends animation and interaction
- x-divider: pattern rendering regression
- Mobile viewport units (`100dvh` / `100%`) applied where `100vh` / `100vw` were causing overflow
- Missing demo page for audit-flagged component
- Flaky async test timeouts
- Closure Advanced Compilation safety regression (bab76f9)

## [2.0.1] - 2026-04-10

### Fixed

- Release pipeline: version references in build.clj, deps.edn, and pom.xml now match package.json (were stuck at 1.4.0/1.2.0, causing Clojars deploy to fail for 2.0.0)
- CI workflow: npm publish step now uses continue-on-error to avoid blocking Clojars deploy

## [2.0.0] - 2026-04-10

### Added

- **x-theme** — Centralised theming component with 33 design tokens, 8 built-in presets (default, ocean, forest, sunset, neo-brutalist, aurora, mono-ai, warm-mineral), and `registerPreset()` JS API for custom themes with partial preset merging
- Theme preset picker on all 79 demo pages (desktop buttons + mobile dropdown, persists via localStorage)
- Theming section in README.md
- Theming guidelines in CLAUDE.md

### Changed

- All 80 components now consume x-theme design tokens via `var(--x-token, fallback)` pattern — fully backwards compatible, components render identically without an `<x-theme>` wrapper
- Overlay components (modal, drawer, menu, popover, dropdown, toast, command-palette, context-menu, cancel-dialogue) use opaque `--x-color-bg` for backgrounds
- `demo-responsive.css` re-maps demo variables to theme tokens when inside `<x-theme>`
- Demo pages use theme-aware CSS variables instead of hardcoded colours

### Fixed

- x-liquid-dock: `.color` property returns `null` when no `color` attribute is set, allowing the CSS theme token to take effect (visual default unchanged)

## [1.4.0] - 2026-04-09

### Added

Six new components:

- **Effects** — x-liquid-glass, x-soft-body, x-liquid-dock, x-liquid-fill
- **Typography** — x-kinetic-font
- **Layout** — x-morph-stack (with variant presets and multi-element goo merge demo)

### Changed

- Demo index: style family filtering
- Demo index components sorted alphabetically
- README updates

## [1.3.0] - 2026-04-07

### Added

Six new components:

- **Effects** — x-splash, x-metaball-cursor, x-neural-glow, x-organic-progress
- **Layout** — x-bento-grid, x-bento-item

### Changed

- **x-neural-glow** — automatic light/dark background adaptation via shader luminance detection

## [1.2.0] - 2026-04-05

### Added

- **Scroll** — x-scroll-stack (card-stack with scroll-driven align support)
- **Documentation** — JavaScript Developer Guide (`docs/javascript-guide.md`) for npm/ESM setup, event handling, theming, and framework integration

### Changed

- Cleaned up x-button.md for JavaScript developer readability
- Added JavaScript guide link to README Installation section

## [1.1.0] - 2026-04-05

### Added

Nine new components:

- **Effects** — x-ripple-effect, x-gaussian-blur
- **Scroll** — x-scroll-parallax, x-scroll-timeline, x-scroll-story
- **Typography** — x-typography, x-kinetic-typography
- **Decorative** — x-organic-divider, x-organic-shape

## [1.0.0] - 2026-04-04

Stable release — promoted from 1.0.0-rc.4 with no code changes.

### Summary

- All 54 components audited, tested, and stable
- Full API consistency, accessibility, theming, and Closure Advanced compilation safety
- Published to NPM, Clojars, and GitHub Releases

## [1.0.0-rc.4] - 2026-03-31

Release candidate — all components audited and stable.

### Changed

- All 54 components reviewed and updated for API consistency, accessibility, theming, Closure Advanced compilation safety, and test coverage
- Accompanying documentation and demo files updated for all components
- Namespace renamed from `app` to `baredom` to match published package name

### Components

- **Layout** — x-container, x-grid, x-spacer, x-divider, x-sidebar, x-navbar
- **Typography & Display** — x-badge, x-chip, x-stat, x-avatar, x-avatar-group, x-skeleton
- **Feedback** — x-alert, x-spinner, x-progress, x-progress-circle, x-toast, x-toaster
- **Overlays** — x-modal, x-drawer, x-popover, x-context-menu, x-command-palette, x-cancel-dialogue
- **Navigation** — x-breadcrumbs, x-pagination, x-tab, x-tabs, x-menu, x-menu-item, x-stepper
- **Forms** — x-button, x-checkbox, x-radio, x-switch, x-select, x-slider, x-text-area, x-form, x-form-field, x-fieldset, x-search-field, x-currency-field, x-date-picker
- **Content** — x-card, x-collapse, x-chart, x-copy, x-file-download, x-dropdown
- **Data** — x-table, x-table-row, x-table-cell
- **Timeline** — x-timeline, x-timeline-item
- **Notifications** — x-notification-center

## [0.1.0-alpha] - 2026-03-25

Initial alpha release of BareDome — 54 native Web Components built with ClojureScript and compiled to zero-dependency ESM modules.

### Components

- **Layout** — x-container, x-grid, x-spacer, x-divider, x-sidebar, x-navbar
- **Typography & Display** — x-badge, x-chip, x-stat, x-avatar, x-avatar-group, x-skeleton
- **Feedback** — x-alert, x-spinner, x-progress, x-progress-circle, x-toast, x-toaster
- **Overlays** — x-modal, x-drawer, x-popover, x-context-menu, x-command-palette, x-cancel-dialogue
- **Navigation** — x-breadcrumbs, x-pagination, x-tab, x-tabs, x-menu, x-menu-item, x-stepper
- **Forms** — x-button, x-checkbox, x-radio, x-switch, x-select, x-slider, x-text-area, x-form, x-form-field, x-fieldset, x-search-field, x-currency-field, x-date-picker
- **Content** — x-card, x-collapse, x-chart, x-copy, x-file-download, x-dropdown
- **Data** — x-table, x-table-row, x-table-cell
- **Timeline** — x-timeline, x-timeline-item

### Architecture

- Stateless rendering: `DOM = f(attributes, properties)`
- Zero framework runtime — no React, Lit, Vue, or virtual DOM
- Open shadow DOM with CSS custom properties for theming
- Light/dark mode via `@media (prefers-color-scheme: dark)`
- Reduced motion via `@media (prefers-reduced-motion: reduce)`
- Google Closure Advanced compiled — minified, dead-code-eliminated
- Per-component ESM modules — fully tree-shakeable
- Accessible by default (ARIA roles, keyboard navigation, focus management)
