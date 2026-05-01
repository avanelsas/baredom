# BareDOM

<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="public/assets/baredom_darkmode.svg">
    <source media="(prefers-color-scheme: light)" srcset="public/assets/baredom_lightmode.svg">
    <img alt="Project Logo" src="public/assets/baredom_lightmode.svg" width="160">
  </picture>
</p>

**Native web components. Zero runtime. No framework required.**

[![npm version](https://img.shields.io/npm/v/%40vanelsas%2Fbaredom.svg)](https://www.npmjs.com/package/@vanelsas/baredom)
[![license](https://img.shields.io/npm/l/%40vanelsas%2Fbaredom.svg)](./LICENSE)
[![ESM](https://img.shields.io/badge/module-ESM-blue.svg)](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Modules)
[![TypeScript](https://img.shields.io/badge/types-included-blue.svg)](https://www.typescriptlang.org/)
[![Custom Elements v1](https://img.shields.io/badge/Custom%20Elements-v1-green.svg)](https://developer.mozilla.org/en-US/docs/Web/API/Web_components/Using_custom_elements)

---

## What is BareDOM?

BareDOM is a library of UI components built entirely on web standards — Custom Elements v1, Shadow DOM, and ES modules. There is no framework runtime, no virtual DOM, and no JavaScript framework peer dependency. Every component is a native HTML element that you register once and use anywhere.

The core rendering model is deliberately simple:

```
DOM = f(attributes, properties)
```

Components are **stateless**. Every visual state is derived directly from attributes and properties at render time. There are no hidden reactive systems, no internal signals, no component lifecycles to manage. Set an attribute, the DOM updates. Remove it, the DOM updates back.

BareDOM is authored in ClojureScript and compiled to optimised, minified ES modules using Google Closure's advanced compilation pass.

BareDOM has been created using Claude Code. The CLAUDE.md file is added to the repository for your convenience.

All components can be explored in the [live demo](https://avanelsas.github.io/baredom/).

---

## Why BareDOM?

Like most Clojure/ClojureScript developers starting out with UIs, I went through the common phases of using Reagent and Re-frame—which are great utilities in their own right. However, as my UIs became larger and more complex, bundle sizes increased, and I found myself spending too much time rebuilding generic, reusable components from scratch.

I started looking for a different approach and discovered Web Components. I built a few, but didn't have the spare time to develop a comprehensive set that could be used in any project. Then AI arrived. While experimenting with Claude Code, I realised that 1 + 1 could be 3. That is how BareDOM, my first open-source project, was born.

I first built the usual suspects for web components, a basis to create a UI. I then thought about trying something more exciting, with animations, shapes, colours, and a whole range of web components that deal with morphing, kinetics and organic styles were born. I hope it brings you joy when using them in your web application!

# Why web components?

**Works in any stack.** Because components are native HTML elements, they work wherever HTML works — vanilla JavaScript, React, Vue, Svelte, Angular, server-rendered HTML, or a static page. No adapter layer, no wrapper library.

**No framework lock-in.** Your components are not tied to the framework you are building with today. Migrate your app, keep your components.

**Tree-shakeable by design.** Each component is a separate ES module. Import only what you use; bundle tools eliminate the rest automatically.

**Full theming with CSS custom properties.** Every visual detail — colours, spacing, radius, shadows, typography — is exposed as a `--x-<component>-<property>` CSS custom property. Override at any scope: globally, per-page, per-instance. Use [`<x-theme>`](./docs/x-theme.md) for centralised theming with built-in presets.

**Light and dark mode included.** All components adapt automatically to `prefers-color-scheme`. No JavaScript required, no class toggling.

**Accessibility built in.** ARIA roles, live regions, keyboard navigation, focus management, and `prefers-reduced-motion` support are part of the component, not an afterthought. You do not need to layer accessibility on top.

**Mobile-ready.** All components are tested on viewports from 320px up. Overlay panels cap their width to avoid overflow. Touch targets meet the 44px minimum on coarse-pointer devices. Pointer events are used throughout for unified mouse and touch input.

**Open Shadow DOM.** Shadow roots are `mode: "open"` — inspectable in DevTools, styleable via `::part()`, and testable with standard DOM APIs.

**First-class TypeScript support.** Every component ships with auto-generated `.d.ts` type declarations and a [Custom Elements Manifest](https://github.com/webcomponents/custom-elements-manifest). TypeScript consumers get typed element interfaces, typed custom events with detail payloads, and `HTMLElementTagNameMap` augmentation for `querySelector` type narrowing — all without installing a separate `@types` package.

---

## BareForge — Visual Page Builder

[![GitHub](https://img.shields.io/github/stars/avanelsas/bareforge?style=social)](https://github.com/avanelsas/bareforge)

[BareForge](https://github.com/avanelsas/bareforge) is a companion visual landing-page builder for BareDOM. Drag-and-drop BareDOM components onto a canvas, configure them in the inspector, and export a complete project.

- **Drag-and-drop canvas** with snap-aware placement
- **Inspector** with type-aware editors for every BareDOM component
- **Theme editor** with all 8 BareDOM presets and per-token overrides
- **Four export modes** — CDN, bundle, ClojureScript, and JavaScript

Try the [live editor](https://avanelsas.github.io/bareforge/) or see the [repository](https://github.com/avanelsas/bareforge) for details.

---

## Theming

Wrap any subtree in `<x-theme>` to apply a consistent palette across all components:

```html
<x-theme preset="ocean">
  <x-button>Themed button</x-button>
  <x-alert type="info" text="Themed alert"></x-alert>
</x-theme>
```

Ships with **8 built-in presets**: `default`, `ocean`, `forest`, `sunset`, `neo-brutalist`, `aurora`, `mono-ai`, `warm-mineral`. All presets include both light and dark mode values that work with `prefers-color-scheme`.

For custom themes, register your own preset via JavaScript:

```js
import { registerPreset } from '@vanelsas/baredom/x-theme';

registerPreset('acme', {
  light: { '--x-color-primary': '#e11d48', '--x-color-surface': '#fff' },
  dark:  { '--x-color-primary': '#fb7185', '--x-color-surface': '#1a1a2e' }
});
```

```html
<x-theme preset="acme">...</x-theme>
```

Or override individual tokens via CSS:

```html
<x-theme preset="default" style="--x-color-primary: #e11d48;">
  ...
</x-theme>
```

See [docs/x-theme.md](./docs/x-theme.md) for the full token list, preset details, and API reference.

---

## Installation

```bash
npm install @vanelsas/baredom
```

```js
import { init } from '@vanelsas/baredom/x-button';
init();
```

Also available via [Clojars](./docs/installation.md#clojurescript-via-clojars) and [standalone ES modules](./docs/installation.md#vanilla-htmljs-via-es-modules). See the [full installation guide](./docs/installation.md).

---

## Usage

BareDOM components are native HTML elements. Import, register, and use them in any framework or vanilla HTML.

- **JavaScript / TypeScript** — see the [JavaScript Developer Guide](./docs/javascript-guide.md)
- **TypeScript types** — see the [TypeScript Guide](./docs/typescript.md)
- **ClojureScript** — see the [ClojureScript Guide](./docs/clojurescript-guide.md)
- **React** — see [`@vanelsas/baredom-react`](https://www.npmjs.com/package/@vanelsas/baredom-react)
- **Angular** — see [`@vanelsas/baredom-angular`](https://www.npmjs.com/package/@vanelsas/baredom-angular)

---

## Components

### Form (17)

| Tag | Description |
|-----|-------------|
| [`<x-button>`](./docs/x-button.md) | Action control. Variants: `primary`, `secondary`, `tertiary`, `ghost`, `danger`. Sizes: `sm`, `md`, `lg`. States: `disabled`, `loading`, `pressed`. Icon slots. |
| [`<x-checkbox>`](./docs/x-checkbox.md) | Boolean input. Reflects `checked` and `indeterminate` states to attributes. |
| [`<x-color-picker>`](./docs/x-color-picker.md) | Colour picker with 2D saturation/brightness area, hue strip, optional alpha, preset swatches, eyedropper, and clipboard copy. Inline or popover mode. |
| [`<x-copy>`](./docs/x-copy.md) | Copy-to-clipboard utility button with success feedback. |
| [`<x-currency-field>`](./docs/x-currency-field.md) | Formatted currency input with locale-aware masking. |
| [`<x-date-picker>`](./docs/x-date-picker.md) | Calendar-based date selection with keyboard navigation. |
| [`<x-fieldset>`](./docs/x-fieldset.md) | Groups related form controls with a styled legend. |
| [`<x-file-download>`](./docs/x-file-download.md) | Download trigger that initiates a file transfer. |
| [`<x-form>`](./docs/x-form.md) | Form wrapper with coordinated validation state. |
| [`<x-form-field>`](./docs/x-form-field.md) | Label + input wrapper with error and hint text slots. |
| [`<x-radio>`](./docs/x-radio.md) | Single-choice input within a radio group. |
| [`<x-search-field>`](./docs/x-search-field.md) | Search input with integrated clear button and search icon. |
| [`<x-select>`](./docs/x-select.md) | Dropdown select control with custom styling. |
| [`<x-slider>`](./docs/x-slider.md) | Range slider with step, min/max, and value display. |
| [`<x-stepper>`](./docs/x-stepper.md) | Multi-step form progress indicator with navigation. |
| [`<x-switch>`](./docs/x-switch.md) | Toggle switch for boolean settings. |
| [`<x-text-area>`](./docs/x-text-area.md) | Multi-line text input with auto-resize option. |

### Feedback (10)

| Tag | Description |
|-----|-------------|
| [`<x-alert>`](./docs/x-alert.md) | Semantic alert banner. Types: `info`, `success`, `warning`, `error`. Auto-dismiss with `timeout-ms`. Fires `x-alert-dismiss`. |
| [`<x-badge>`](./docs/x-badge.md) | Small inline label for counts, states, and categories. |
| [`<x-chip>`](./docs/x-chip.md) | Compact tag component, optionally removable. |
| [`<x-notification-center>`](./docs/x-notification-center.md) | Notification hub for aggregating and managing in-app notifications. |
| [`<x-progress>`](./docs/x-progress.md) | Linear progress bar with determinate and indeterminate modes. |
| [`<x-progress-circle>`](./docs/x-progress-circle.md) | Circular progress indicator for compact spaces. |
| [`<x-skeleton>`](./docs/x-skeleton.md) | Animated loading placeholder that mirrors content shape. |
| [`<x-spinner>`](./docs/x-spinner.md) | Inline loading spinner with size and colour variants. |
| [`<x-toast>`](./docs/x-toast.md) | Single transient notification with enter/exit animations and auto-dismiss. |
| [`<x-toaster>`](./docs/x-toaster.md) | Toast manager. Positions a queue of `<x-toast>` elements, enforces `max-toasts`, and fires `x-toaster-dismiss`. |

### Navigation (8)

| Tag | Description |
|-----|-------------|
| [`<x-breadcrumbs>`](./docs/x-breadcrumbs.md) | Hierarchical path trail with separator customisation. |
| [`<x-menu>`](./docs/x-menu.md) | Vertical menu container coordinating `<x-menu-item>` children. |
| [`<x-menu-item>`](./docs/x-menu-item.md) | Individual menu entry with icon, label, description, and keyboard support. |
| [`<x-navbar>`](./docs/x-navbar.md) | Top navigation bar with responsive slot layout. |
| [`<x-pagination>`](./docs/x-pagination.md) | Page navigation controls with first/previous/next/last and page-size selection. |
| [`<x-sidebar>`](./docs/x-sidebar.md) | Collapsible side navigation panel with collapse/expand animation. |
| [`<x-tab>`](./docs/x-tab.md) | Individual tab within an `<x-tabs>` container. |
| [`<x-tabs>`](./docs/x-tabs.md) | Tab container that coordinates `<x-tab>` children, manages active state, and fires change events. |

### Layout (6)

| Tag | Description |
|-----|-------------|
| [`<x-card>`](./docs/x-card.md) | Surface container. Variants: `elevated`, `outlined`, `filled`, `ghost`. Interactive mode available. |
| [`<x-collapse>`](./docs/x-collapse.md) | Expandable/collapsible section with animated height transition. |
| [`<x-container>`](./docs/x-container.md) | Responsive max-width container with configurable padding. |
| [`<x-divider>`](./docs/x-divider.md) | Horizontal or vertical visual separator. |
| [`<x-grid>`](./docs/x-grid.md) | CSS Grid layout component with responsive column configuration. |
| [`<x-spacer>`](./docs/x-spacer.md) | Flexible spacing element for flexbox and grid layouts. |

### Data (10)

| Tag | Description |
|-----|-------------|
| [`<x-avatar>`](./docs/x-avatar.md) | User photo or initials display. Shape, size, and status dot variants. |
| [`<x-avatar-group>`](./docs/x-avatar-group.md) | Overlapping avatar stack for representing multiple users. |
| [`<x-carousel>`](./docs/x-carousel.md) | Accessible carousel with swipe/drag, arrows, dot indicators, autoplay, slide/fade transitions, and horizontal/vertical orientation. |
| [`<x-chart>`](./docs/x-chart.md) | Data visualisation component for common chart types. |
| [`<x-stat>`](./docs/x-stat.md) | KPI / metric card with value, label, trend, and icon slots. |
| [`<x-table>`](./docs/x-table.md) | Data grid using CSS subgrid. Supports sorting, single/multi-select, striping, and accessible captions. |
| [`<x-table-cell>`](./docs/x-table-cell.md) | Table cell for header and data modes, with sort indicator and alignment control. |
| [`<x-table-row>`](./docs/x-table-row.md) | Table row with interactive selection and `x-table-row-select` event. |
| [`<x-timeline>`](./docs/x-timeline.md) | Vertical timeline container that coordinates `<x-timeline-item>` children. |
| [`<x-timeline-item>`](./docs/x-timeline-item.md) | Individual timeline event with time, icon, heading, and body slots. |

### Overlay (7)

| Tag | Description |
|-----|-------------|
| [`<x-cancel-dialogue>`](./docs/x-cancel-dialogue.md) | Confirmation modal for destructive cancel actions. |
| [`<x-command-palette>`](./docs/x-command-palette.md) | Keyboard-accessible global search and command interface. |
| [`<x-context-menu>`](./docs/x-context-menu.md) | Right-click / long-press contextual action menu. |
| [`<x-drawer>`](./docs/x-drawer.md) | Off-canvas sliding panel, configurable from any edge. |
| [`<x-dropdown>`](./docs/x-dropdown.md) | Positioned dropdown container for menus and selection. |
| [`<x-modal>`](./docs/x-modal.md) | Centred dialog with backdrop, focus trap, and `Escape` to close. |
| [`<x-popover>`](./docs/x-popover.md) | Anchored popover for tooltips, help text, and contextual UI. |

---

## Design Principles

**Stateless.** No `atom`, no signal, no reactive state container lives inside a component. Every render is a pure function of the current attributes and properties. Debugging a component means inspecting attributes in DevTools — no hidden state to hunt for.

**Standards-only.** BareDOM relies on Custom Elements v1, Shadow DOM v1, and ES modules — all natively supported in modern browsers. There are no polyfills required and no proprietary APIs to learn.

**Zero runtime dependency.** Components are compiled to self-contained ES modules. The only JavaScript in your bundle is the component itself. No framework, no runtime library, no utility belt.

**Accessible by default.** ARIA roles, live regions, keyboard interaction patterns, focus indicators, and `prefers-reduced-motion` support are written into every component that needs them — not optional add-ons.

**Predictable theming.** CSS custom properties follow a single naming convention: `--x-<component>-<property>`. Tokens are set on `:host` and cascade normally. You override them the same way you override any CSS property.

**Mobile-first.** Components use `dvh` viewport units, `calc(100vw - ...)` width caps, and `@media (pointer:coarse)` rules for touch-friendly sizing. No component overflows on a 320px screen.

---

## Bundle Size

BareDOM compiles to lightweight ES modules. Sizes below are gzipped:

| Module | Gzipped |
|--------|---------|
| `base.js` (shared runtime) | ~35 KB |
| Median component | ~3 KB |
| Smallest (`x-spacer`) | ~1 KB |
| Largest (`x-chart`) | ~9 KB |

Each component loads `base.js` once plus its own module. A typical page using 5-10 components weighs **40-65 KB** gzipped total.

> **ESM only.** BareDOM ships ES modules exclusively — there is no CommonJS or UMD build. This works natively in all modern browsers and with any bundler that supports ESM (webpack 5+, Vite, esbuild, Rollup, Parcel). TypeScript declarations (`.d.ts`) are included for every component. If you need server-side rendering, pre-render the HTML and hydrate with component registration on the client.

---

## Browser Support

BareDOM targets browsers that support Custom Elements v1 and Shadow DOM v1 natively:

| Browser | Minimum version |
|---------|----------------|
| Chrome / Edge | 67+ |
| Firefox | 63+ |
| Safari | 14+ |

No polyfills are included or required for these targets.

---

## Development

See the [development guide](./docs/development.md) for setting up the dev server, using [debug mode](./docs/development.md#debug-mode), and building from source.

---

## License

MIT
