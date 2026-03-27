# BareDOM

<p align="left">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="public/assets/baredom_darkmode.svg">
    <source media="(prefers-color-scheme: light)" srcset="public/assets/baredom_lightmode.svg">
    <img alt="Baredom Logo" src="apublic/assets/baredom_lightmode.svg" width="120">
  </picture>
</p>

**Native web components. Zero runtime. No framework required.**

[![npm version](https://img.shields.io/npm/v/%40vanelsas%2Fbaredom.svg)](https://www.npmjs.com/package/@vanelsas/baredom)
[![license](https://img.shields.io/npm/l/%40vanelsas%2Fbaredom.svg)](./LICENSE)
[![ESM](https://img.shields.io/badge/module-ESM-blue.svg)](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Modules)
[![Custom Elements v1](https://img.shields.io/badge/Custom%20Elements-v1-green.svg)](https://developer.mozilla.org/en-US/docs/Web/API/Web_components/Using_custom_elements)

---

## What is BareDOM?

BareDOM is a library of 54 UI components built entirely on web standards — Custom Elements v1, Shadow DOM, and ES modules. There is no framework runtime, no virtual DOM, and no JavaScript framework peer dependency. Every component is a native HTML element that you register once and use anywhere.

The core rendering model is deliberately simple:

```
DOM = f(attributes, properties)
```

Components are **stateless**. Every visual state is derived directly from attributes and properties at render time. There are no hidden reactive systems, no internal signals, no component lifecycles to manage. Set an attribute, the DOM updates. Remove it, the DOM updates back.

BareDOM is authored in ClojureScript and compiled to optimised, minified ES modules using Google Closure's advanced compilation pass.

BareDOM has been created using Claude Code. The CLAUDE.md file is added to the repository for your convenience.

---

## Why BareDOM?

I started working on BareDOM after going though all the motions that Clojure developers often go through when deciding to build a UI. I worked with Reagent and Re-frame and in general the experience was pretty good. At some point I was building larger UI's with reusable components that were pretty basic (e.g. inputs, buttons etc). For complex UI's the bundle size became larger and larger.

I wondered if there would be a better way to do this, and I ended up reading about web components. It seemed like a good idea to try that. For me it as a research exercise, trying to understand how it all works. I tried to build a few, and had to learn how to do that in Clojurescript. Building a larger set of web components is quite a bit of work.

When Claude Code appeared, Iw as thinking about a project to try it out with, and web components seemed like a good fit.  I picked up the project again and started to build Clojurescript based web components assisted by Claude. A good experiment to work with AI tooling and build something I find interesting myself.

The project is still in an alpha state. The components and demo's work, but there are bound to be some things not working properly yet. Feel free to give it a spin.

**Works in any stack.** Because components are native HTML elements, they work wherever HTML works — vanilla JavaScript, React, Vue, Svelte, Angular, server-rendered HTML, or a static page. No adapter layer, no wrapper library.

**No framework lock-in.** Your components are not tied to the framework you are building with today. Migrate your app, keep your components.

**Tree-shakeable by design.** Each component is a separate ES module. Import only what you use; bundle tools eliminate the rest automatically.

**Full theming with CSS custom properties.** Every visual detail — colours, spacing, radius, shadows, typography — is exposed as a `--x-<component>-<property>` CSS custom property. Override at any scope: globally, per-page, per-instance.

**Light and dark mode included.** All components adapt automatically to `prefers-color-scheme`. No JavaScript required, no class toggling.

**Accessibility built in.** ARIA roles, live regions, keyboard navigation, focus management, and `prefers-reduced-motion` support are part of the component, not an afterthought. You do not need to layer accessibility on top.

**Open Shadow DOM.** Shadow roots are `mode: "open"` — inspectable in DevTools, styleable via `::part()`, and testable with standard DOM APIs.

---

## Installation

**Step 1 — Add the npm dependency to your `package.json`:**

```json
{
  "dependencies": {
    "@vanelsas/baredom": "^0.1.0-alpha"
  }
}
```

Then run `npm install`.

**Step 2 — shadow-cljs:** no extra configuration needed. shadow-cljs resolves npm packages automatically via the `:npm-deps` or `node_modules` integration built into every shadow-cljs project.

---

## Using with plain HTML (no build step)

BareDOM components are standard ES modules. Load them directly in any HTML page using a CDN — no npm, no bundler, no framework required.

### Import and initialise

```html
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>My App</title>
</head>
<body>

  <x-button variant="primary">Hello</x-button>
  <x-alert type="success" text="It works!"></x-alert>

  <script type="module">
    import { init as initButton } from "https://esm.sh/@vanelsas/baredom/x-button";
    import { init as initAlert }  from "https://esm.sh/@vanelsas/baredom/x-alert";

    initButton();
    initAlert();
  </script>

</body>
</html>
```

`<script type="module">` is required because BareDOM components are ES modules. Each `init()` call registers the custom element with the browser. Only the components you import are loaded — unused components cost nothing.

### Set attributes in HTML

Every attribute can be set directly in markup:

```html
<x-button variant="secondary" size="sm" disabled>Cancel</x-button>
<x-checkbox checked></x-checkbox>
<x-alert type="warning" text="Check your input." dismissible></x-alert>
<x-progress value="65" max="100"></x-progress>
```

Boolean attributes follow the HTML convention: presence means `true`, absence means `false`.

### Handle events

```html
<x-button id="btn" variant="primary">Click me</x-button>
<x-tabs id="tabs" value="home">
  <x-tab value="home">Home</x-tab>
  <x-tab value="settings">Settings</x-tab>
</x-tabs>

<script type="module">
  import { init as initButton } from "https://esm.sh/@vanelsas/baredom/x-button";
  import { init as initTabs }   from "https://esm.sh/@vanelsas/baredom/x-tabs";
  import { init as initTab }    from "https://esm.sh/@vanelsas/baredom/x-tab";

  initButton(); initTabs(); initTab();

  document.getElementById("btn").addEventListener("click", () => {
    console.log("button clicked");
  });

  document.getElementById("tabs").addEventListener("value-change", e => {
    console.log("active tab:", e.detail.value);
  });
</script>
```

Custom events carry a `detail` payload — check the individual component docs for the event name and `detail` shape.

### Theme with CSS custom properties

```html
<style>
  :root {
    --x-button-radius: 4px;
    --x-button-bg-primary: #0a5c99;
  }
</style>
```

CSS custom properties cascade normally into the open Shadow DOM. No JavaScript required for theming.

---

## Usage

### 1. Register components

Require each component module you need and call `.init` on it once, before any rendering. Only the components you require are included in your bundle.

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

### 2. Add a renderer

BareDOM components are plain DOM elements. You need no framework to use them — only a small helper that turns ClojureScript data structures into DOM nodes. Copy the following into your project as `renderer.cljs`:

```clojure
(ns my-app.renderer
  (:require [clojure.string :as str]))

;;; ── Prop helpers ──────────────────────────────────────────────────────────

(defn- on-key? [k]
  (str/starts-with? (name k) "on-"))

(defn- event-name [k]
  ;; :on-click → "click"   :on-value-change → "value-change"
  (subs (name k) 3))

(defn- set-prop! [el k v]
  (let [attr (name k)]
    (cond
      (on-key? k)  (.addEventListener el (event-name k) v)
      (nil? v)     (.removeAttribute el attr)
      (true? v)    (.setAttribute el attr "")
      (false? v)   (.removeAttribute el attr)
      :else        (.setAttribute el attr (str v)))))

;;; ── DOM creation ──────────────────────────────────────────────────────────

(declare create-nodes)

(defn- create-element [[tag & args]]
  (let [has-props? (and (seq args) (map? (first args)))
        props      (when has-props? (first args))
        children   (if has-props? (rest args) args)
        el         (.createElement js/document (name tag))]
    (doseq [[k v] props]
      (set-prop! el k v))
    (doseq [node (mapcat create-nodes children)]
      (.appendChild el node))
    el))

(defn create-nodes [x]
  (cond
    (nil? x)    []
    (false? x)  []
    (string? x) [(.createTextNode js/document x)]
    (number? x) [(.createTextNode js/document (str x))]
    (vector? x) [(create-element x)]
    (seq? x)    (mapcat create-nodes x)
    :else       []))

;;; ── Mount ─────────────────────────────────────────────────────────────────

(defn render! [container view-fn]
  (set! (.-innerHTML container) "")
  (doseq [node (create-nodes (view-fn))]
    (.appendChild container node)))

(defn mount! [container view-fn state-atom]
  (render! container view-fn)
  (add-watch state-atom ::render
             (fn [_ _ _ _]
               (render! container view-fn))))
```

What it does:

- `set-prop!` — routes `:on-*` keys to `addEventListener`; boolean `true` sets the attribute to `""`; `false` / `nil` removes it; everything else calls `setAttribute`
- `create-nodes` / `create-element` — recursively turns hiccup vectors into DOM nodes
- `render!` — clears a container element and mounts the result of calling a view function
- `mount!` — same as `render!`, and also `add-watch`es a state atom so the view re-renders on every state change

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

`mount!` calls `view` immediately and re-calls it on every `swap!` or `reset!` to `app-state`. The entire view is re-created from scratch on each render — no diffing, no virtual DOM, just plain DOM construction driven by the current value of the atom.

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

## bare-demo — framework-free usage example

The `bare-demo/` folder contains a focused ClojureScript application that shows how to consume five BareDOM components — `x-navbar`, `x-sidebar`, `x-button`, `x-modal`, and `x-container` — with **zero framework overhead**.

The demo is built on three ideas:

- **A ~55-line hiccup renderer.** A small `renderer.cljs` file converts nested ClojureScript vectors into real DOM nodes. There is no virtual DOM, no diffing, and no reactive runtime — just `document.createElement`, `setAttribute`, and `addEventListener`.
- **A single state atom.** All UI state (`sidebar-open`, `modal-open`, `active-nav`) lives in one `defonce` atom. `mount!` attaches `add-watch` so every `swap!` triggers a full re-render.
- **CSS custom properties for theming.** Component visuals are overridden entirely in `public/index.html` using `--x-<component>-<property>` rules — no JavaScript involved.

**Run it:**

```bash
npx shadow-cljs watch bare-demo
```

Then open `http://localhost:8001`.

See [`bare-demo/README.md`](./bare-demo/README.md) for a full walkthrough of the renderer, component registration, view syntax, state management, and theming.

---

## bare-reagent-demo — Reagent usage example

The `bare-reagent-demo/` folder is visually identical to `bare-demo` but replaces the custom hiccup renderer with [Reagent](https://reagent-project.github.io/) — a minimalist ClojureScript wrapper around React. It demonstrates the integration patterns and trade-offs that arise when pairing a virtual-DOM framework with native Custom Elements.

The key differences from `bare-demo`:

- **`reagent.core/atom`** drives reactivity. Any component that dereferences a ratom with `@` re-renders automatically — no `add-watch` or manual render trigger required.
- **Standard events** (`:on-click`) work transparently through React's synthetic event system.
- **Custom events** (`x-modal-dismiss`, `toggle`) are not handled by React and must be wired imperatively. The demo uses `reagent/create-class` lifecycle hooks (`component-did-mount` / `component-will-unmount`) and `rdom/dom-node` to attach and clean up native event listeners on the real DOM element.

**Run it:**

```bash
cd bare-reagent-demo
npm install
npm start
```

Then open `http://localhost:8002`.

See [`bare-reagent-demo/README.md`](./bare-reagent-demo/README.md) for a full code walkthrough, the custom-event workaround, and a side-by-side comparison with `bare-demo`.

---

## bare-html — HTML/JS only demo

The `bare-html/` folder contains the same demo — navbar, sidebar, modal, and event log — written entirely in plain HTML and JavaScript. There is no ClojureScript, no build step, and no bundler. Components are loaded by importing their pre-built ES modules directly from the `dist/` folder using a `<script type="module">` tag.

The implementation uses three ideas:

- **Declarative HTML markup.** All components are written as custom element tags in the HTML source. Attributes are set directly in markup or via `setAttribute` / `removeAttribute` from JavaScript.
- **A plain state object and a `render()` function.** A simple JS object holds `sidebarOpen`, `modalOpen`, and `activeNav`. The `render()` function reads this object and reconciles attributes — open/close state is a single `setAttribute` or `removeAttribute` call.
- **Native event listeners.** `x-button` fires a `press` event; `x-sidebar` fires `toggle` with `detail.open`; `x-modal` fires `x-modal-dismiss` on Escape or backdrop click. All are wired with standard `addEventListener`.

**Run it:**

```bash
# From the project root
python3 -m http.server 8000
```

Then open `http://localhost:8000/bare-html/demo.html`. The demo must be served over HTTP — ES module imports are blocked by browsers on `file://` URLs.

See [`bare-html/README.md`](./bare-html/README.md) for a full walkthrough and a comparison with `bare-demo`.

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
