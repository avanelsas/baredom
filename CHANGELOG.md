# Changelog

All notable changes to BareDOM will be documented in this file.

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
