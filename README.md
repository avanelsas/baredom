# BareDOM

**Native web components. Zero runtime. No framework required.**

[![npm version](https://img.shields.io/npm/v/%40vanelsas%2Fbaredom.svg)](https://www.npmjs.com/package/@vanelsas/baredom)
[![license](https://img.shields.io/npm/l/%40vanelsas%2Fbaredom.svg)](./LICENSE)
[![ESM](https://img.shields.io/badge/module-ESM-blue.svg)](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Modules)
[![Custom Elements v1](https://img.shields.io/badge/Custom%20Elements-v1-green.svg)](https://developer.mozilla.org/en-US/docs/Web/API/Web_components/Using_custom_elements)

---

## What is BareDOM?

BareDOM is a library of more than 60 UI components built entirely on web standards — Custom Elements v1, Shadow DOM, and ES modules. There is no framework runtime, no virtual DOM, and no JavaScript framework peer dependency. Every component is a native HTML element that you register once and use anywhere.

The core rendering model is deliberately simple:

```
DOM = f(attributes, properties)
```

Components are **stateless**. Every visual state is derived directly from attributes and properties at render time. There are no hidden reactive systems, no internal signals, no component lifecycles to manage. Set an attribute, the DOM updates. Remove it, the DOM updates back.

BareDOM is authored in ClojureScript and compiled to optimised, minified ES modules using Google Closure's advanced compilation pass.

BareDOM has been created using Claude Code. The CLAUDE.md file is added to the repository for your convenience.

All web components—including new, modern, and exciting ones such as x-ripple-effect, x-scroll-parallax, x-scroll-story, x-scroll-timeline, x-kinetic-typography, x-organic-shape, and x-gaussian-blur—can be seen [here](https://avanelsas.github.io/baredom/)

---

## Why BareDOM?

Like most Clojure/ClojureScript developers starting out with UIs, I went through the common phases of using Reagent and Re-frame—which are great utilities in their own right. However, as my UIs became larger and more complex, bundle sizes increased, and I found myself spending too much time rebuilding generic, reusable components from scratch.

I started looking for a different approach and discovered Web Components. I built a few, but didn't have the spare time to develop a comprehensive set that could be used in any project. Then AI arrived. While experimenting with Claude Code, I realised that 1 + 1 could be 3. That is how BareDOM, my first open-source project, was born.

# Why web components?

**Works in any stack.** Because components are native HTML elements, they work wherever HTML works — vanilla JavaScript, React, Vue, Svelte, Angular, server-rendered HTML, or a static page. No adapter layer, no wrapper library.

**No framework lock-in.** Your components are not tied to the framework you are building with today. Migrate your app, keep your components.

**Tree-shakeable by design.** Each component is a separate ES module. Import only what you use; bundle tools eliminate the rest automatically.

**Full theming with CSS custom properties.** Every visual detail — colours, spacing, radius, shadows, typography — is exposed as a `--x-<component>-<property>` CSS custom property. Override at any scope: globally, per-page, per-instance.

**Light and dark mode included.** All components adapt automatically to `prefers-color-scheme`. No JavaScript required, no class toggling.

**Accessibility built in.** ARIA roles, live regions, keyboard navigation, focus management, and `prefers-reduced-motion` support are part of the component, not an afterthought. You do not need to layer accessibility on top.

**Open Shadow DOM.** Shadow roots are `mode: "open"` — inspectable in DevTools, styleable via `::part()`, and testable with standard DOM APIs.

---

## Installation

> **Using JavaScript?** See the [JavaScript Developer Guide](./docs/javascript-guide.md) for npm/ESM setup, event handling, theming, and framework integration examples (React, Vue, Svelte).

BareDOM can be consumed three ways: as a **ClojureScript source dependency** (Clojars), as **standalone ES module files** (no build tool required), or as an **npm package**.

### Option A — ClojureScript via Clojars

Add BareDOM to your `deps.edn`:

```clojure
{:deps {com.github.avanelsas/baredom {:mvn/version "1.2.0"}}}
```

Or in your `shadow-cljs.edn` dependencies:

```clojure
:dependencies [[com.github.avanelsas/baredom "1.2.0"]]
```

Then require component namespaces directly and call their `init!` function once at startup:

```clojure
(ns my-app.core
  (:require
   [baredom.exports.x-button  :as x-button]
   [baredom.exports.x-alert   :as x-alert]
   [baredom.exports.x-toaster :as x-toaster]
   [baredom.exports.x-toast   :as x-toast]))

(defn- register-components! []
  (x-button/init)
  (x-alert/init)
  (x-toaster/init)
  (x-toast/init))
```

Call `register-components!` once in your `init!` entry point. Registration is idempotent — calling `init` on an already-registered element is a no-op.

### Option B — Vanilla HTML/JS via ES modules

No build tool, no npm, no ClojureScript required. Copy the `dist/` folder (from a release or after running `npm run build`) to your web server and load components directly with `<script type="module">`:

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>BareDOM Example</title>
</head>
<body>
  <x-button variant="primary">Click me</x-button>
  <x-alert type="success" text="It works!"></x-alert>

  <script type="module">
    import { init as initButton } from './dist/x-button.js';
    import { init as initAlert }  from './dist/x-alert.js';

    initButton();
    initAlert();
  </script>
</body>
</html>
```

Each component is a separate ES module. Import only the components you use — the browser loads only those files plus the shared `base.js` runtime.

### Option C — npm

Add the npm package to your `package.json`:

```json
{
  "dependencies": {
    "@vanelsas/baredom": "^1.2.0"
  }
}
```

Then `npm install`. shadow-cljs resolves npm packages automatically via `node_modules`. From ClojureScript:

```clojure
(ns my-app.core
  (:require
   ["@vanelsas/baredom/x-button"  :as x-button]
   ["@vanelsas/baredom/x-alert"   :as x-alert]
   ["@vanelsas/baredom/x-toaster" :as x-toaster]
   ["@vanelsas/baredom/x-toast"   :as x-toast]))

(defn- register-components! []
  (.init x-button)
  (.init x-alert)
  (.init x-toaster)
  (.init x-toast))
```

Call `register-components!` once in your `init!` entry point. Registration is idempotent — calling `.init` on an already-registered element is a no-op.

---

## Usage

### 1. Register components

Whichever installation method you chose above, the pattern is the same: require/import each component you need and call its `init` function once before any rendering. Only the components you register are active on the page.

### 2. Add a renderer

BareDOM components are plain DOM elements. You need no framework to use them — only a small renderer that turns ClojureScript hiccup vectors into DOM nodes and keeps them in sync with your state.

The `bare-demo/` project includes a complete renderer (~120 lines) with DOM reconciliation that you can copy into any ClojureScript project. See [`bare-demo/src/bare_demo/renderer.cljs`](./bare-demo/src/bare_demo/renderer.cljs). No Node.js required — just Java and the Clojure CLI.

What the renderer provides:

- **Hiccup syntax** — describe UI as nested vectors: `[:tag {:attr val} children]`
- **Prop handling** — `:on-*` keys become event listeners; `true`/`false` toggle boolean attributes; everything else calls `setAttribute`
- **DOM reconciliation** — on re-render, the existing DOM is patched in place. Elements are never destroyed and recreated, so Web Components keep their lifecycle, shadow DOM, focus state, and animations intact.
- **`mount!`** — renders the view and attaches `add-watch` to a state atom so every `swap!` triggers a reconciliation pass

### 3. Write views with hiccup syntax

Views are plain ClojureScript functions that return nested vectors. The first element of each vector is a keyword matching the element tag name. An optional map of props follows, then children.

```clojure
;; String and number attributes
[:x-button {:variant "primary"} "Save changes"]
[:x-button {:variant "secondary" :size "sm"} "Cancel"]

;; Boolean attributes — true sets the attribute, false/nil removes it
[:x-button {:variant "danger" :disabled true} "Delete"]
[:x-button {:variant "primary" :loading true} "Saving…"]
[:x-checkbox {:checked true}]
[:x-checkbox {:indeterminate true}]

;; Nesting
[:x-grid {:columns "2" :gap "md"}
 [:x-card "First card"]
 [:x-card "Second card"]]

[:x-grid {:columns "4" :gap "md"}
 [:x-stat {:label "Revenue" :value "$48,295" :trend "up" :variant "positive"}]
 [:x-stat {:label "Users"   :value "12,483"  :trend "up"}]
 [:x-stat {:label "Orders"  :value "1,429"   :trend "neutral"}]
 [:x-stat {:label "Churn"   :value "2.4%"    :trend "down" :variant "danger"}]]

;; Slots — use the :slot attribute to target named slots
[:x-navbar {:label "My App"}
 [:span {:slot "brand" :style "font-weight:700"} "My App"]
 [:div  {:slot "actions"}
  [:x-button {:variant "ghost" :size "sm"} "Sign out"]]]
```

### 4. Handle events and manage state

Event listeners are declared inline using `:on-<event-name>` keys. The key is stripped of `on-` and the remainder becomes the event name passed to `addEventListener`. Custom component events follow the same pattern — use the full event name after `on-`.

```clojure
(defonce app-state (atom {:active-tab "overview"
                          :sidebar-collapsed false}))

;; Standard DOM event
[:x-button
 {:variant  "ghost"
  :on-click (fn [_] (swap! app-state update :sidebar-collapsed not))}
 "Toggle sidebar"]

;; Custom component event — :on-value-change listens for "value-change"
[:x-tabs
 {:value           (:active-tab @app-state)
  :on-value-change (fn [e]
                     (swap! app-state assoc
                            :active-tab (.. e -detail -value)))}
 [:x-tab {:value "overview"}   "Overview"]
 [:x-tab {:value "components"} "Components"]
 [:x-tab {:value "settings"}   "Settings"]]

;; Custom event with detail payload
[:x-alert
 {:type "success" :text "Changes saved." :dismissible true
  :on-x-alert-dismiss (fn [e]
                        (js/console.log "dismissed by:" (.. e -detail -reason)))}]

;; Sidebar with open/collapse state
[:x-sidebar
 {:open      (:sidebar-open @app-state)
  :collapsed (:sidebar-collapsed @app-state)
  :placement "left"
  :on-toggle (fn [e]
               (swap! app-state assoc :sidebar-open (.. e -detail -open)))}
 ;; ... nav items ...
 ]
```

Wire everything together in your `init!`:

```clojure
(defn view []
  [:x-container {:size "xl" :padding "lg"}
   ;; ... your UI built from component vectors ...
   ])

(defn init! []
  (register-components!)
  (renderer/mount! (.getElementById js/document "app") view app-state))
```

`mount!` calls `view` immediately and re-calls it on every `swap!` or `reset!` to `app-state`. On each re-render the reconciler diffs the new hiccup tree against the live DOM and applies only the changes needed — attribute updates, text changes, children added or removed. Existing elements stay in place.

### Theming

Override CSS custom properties at any scope:

```css
/* Global overrides */
:root {
  --x-button-radius: 4px;
  --x-alert-radius:  8px;
}

/* Per-instance override */
#sidebar-save-btn {
  --x-button-bg-primary: #0a5c99;
}
```

---

## Components

### Form (16)

| Tag | Description |
|-----|-------------|
| [`<x-button>`](./docs/x-button.md) | Action control. Variants: `primary`, `secondary`, `tertiary`, `ghost`, `danger`. Sizes: `sm`, `md`, `lg`. States: `disabled`, `loading`, `pressed`. Icon slots. |
| [`<x-checkbox>`](./docs/x-checkbox.md) | Boolean input. Reflects `checked` and `indeterminate` states to attributes. |
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

### Data (9)

| Tag | Description |
|-----|-------------|
| [`<x-avatar>`](./docs/x-avatar.md) | User photo or initials display. Shape, size, and status dot variants. |
| [`<x-avatar-group>`](./docs/x-avatar-group.md) | Overlapping avatar stack for representing multiple users. |
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

## Component Demo

BareDOM ships with a built-in demo that lets you browse and interact with every component in isolation. It is intended for developer convenience when working on the library itself.

```bash
npm install
npx shadow-cljs watch app
```

Then open `http://localhost:8000`. The dev server serves `public/index.html` and hot-reloads on every source change. Each component is demonstrated in its own section with controls for toggling attributes, properties, and variants.

---

## bare-demo — starter template for ClojureScript web apps

The `bare-demo/` folder is a ready-to-use ClojureScript application that consumes BareDOM components with **zero framework dependency and no Node.js**. It is designed as a starting point for developers building new web apps on top of BareDOM.

The architecture is built on three ideas:

- **Declarative hiccup views.** UI is described as nested ClojureScript vectors — the same syntax used by Reagent and Hiccup. Views are plain functions, easy to compose and reason about.
- **A single state atom with reactive rendering.** All UI state lives in one `defonce` atom. `mount!` attaches `add-watch` so every `swap!` triggers a re-render automatically.
- **DOM reconciliation, not rebuild.** On state changes the renderer patches the existing DOM in place — updating attributes, text, and children without destroying elements. Web Components keep their lifecycle, shadow DOM, focus state, and animations intact.

This approach scales naturally: add more state, more views, more components — no manual wiring, no framework overhead, no impedance mismatch with the Web Component model.

**Run it:**

```bash
cd bare-demo
clj -M:dev
```

Then open `http://localhost:8001`.

See [`bare-demo/README.md`](./bare-demo/README.md) for a full walkthrough of the renderer, component registration, view syntax, state management, and theming.

> **Prefer NPM?** The `bare-node-demo/` folder contains the same demo consuming BareDOM via npm. Run it with `cd bare-node-demo && npm install && npm start` (opens on `http://localhost:8003`).

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

## License

MIT
