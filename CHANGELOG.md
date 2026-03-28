# Changelog

All notable changes to BareDome will be documented in this file.

## [0.2.2-alpha] - 2026-03-27

### Fixed

- Code updates to x-button, x-alert, x-drawer, x-form, x-form-field to improve quality
- Accompanying docs updated if needed

## [0.2.1-alpha] - 2026-03-27

### Fixed

- Fixes tests for x-file-download

## [0.2.0-alpha] - 2026-03-27

### Fixed

- Fixes design issues x-context-menu and x-modal
- Fixes small bugs in x-file-download, x-fieldset, x-date-picker, x-button

## [0.1.11-alpha] - 2026-03-27

### Changed

- Renamed internal ClojureScript namespace root from `app` to `baredom` to match the published npm package name `@vanelsas/baredom`. No public API
changes.

## [0.1.10-alpha] - 2026-03-27

## Fixed

- Updates package.json to ensure the latest alpha version is also the latest version on NPM
- Add report.html to .gitignore

## [0.1.9-alpha] - 2026-03-27

## Fixed

- Updates build.clj and README.md

## [0.1.8-alpha] - 2026-03-27

## Fixed

- Updates release.yml

## [0.1.7-alpha] - 2026-03-27

## Fixed

- Updates package.json
- Updates release.yml

## [0.1.6-alpha] - 2026-03-27

## Fixed

- Updates package.json

## [0.1.5-alpha] - 2026-03-27

## Fixed

- Updates package.json

## [0.1.4-alpha] - 2026-03-27

## Fixed

- renames Release in yml to release

## [0.1.3-alpha] - 2026-03-27

## Fixed

- Fixes Release with OIDC

## [0.1.2-alpha] - 2026-03-27

## Fixed

- Release with OIDC

## [0.1.1-alpha] - 2026-03-27

## Fixed

- Fixes x-date-picker disabled
- Fixes loading of logo in darkmode/lightmode in main demo

## Adds

- Pure HTML/JS demo + README.md

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
