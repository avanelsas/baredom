# Changelog

All notable changes to BareDome will be documented in this file.

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
