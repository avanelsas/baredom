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

**Works in any stack.** Components are native HTML elements — they work wherever HTML works (vanilla JS, React, Vue, Svelte, Angular, server-rendered HTML, static pages). Your components are not tied to the framework you build with today; migrate your app, keep your components.

**Stateless.** Every render is a pure function of attributes and properties — no hidden state, no signals, no virtual DOM. Inspect attributes in DevTools and you see the truth. (And when you need deeper traces, BareDOM ships a time-travel debugger — see below.)

**Zero runtime, tree-shakeable.** Each component is a self-contained ES module compiled with Google Closure Advanced. The only JavaScript in your bundle is the components you import — no framework, no utility belt, no runtime library.

**Predictable theming.** Every visual detail is exposed as a `--x-<component>-<property>` CSS custom property — override at any scope, or wrap in [`<x-theme>`](./docs/x-theme.md) for coordinated palettes with built-in presets. Light/dark mode adapts automatically via `prefers-color-scheme`.

**Accessible & mobile-first.** ARIA roles, keyboard navigation, focus management, and `prefers-reduced-motion` support are part of every component. All components are tested on viewports from 320px up; touch targets meet the 44px minimum on coarse-pointer devices, and pointer events handle mouse + touch uniformly.

**Open Shadow DOM.** Shadow roots are `mode: "open"` — inspectable in DevTools, styleable via `::part()`, and testable with standard DOM APIs.

**TypeScript + Custom Elements Manifest.** Auto-generated `.d.ts` declarations ship with every component: typed element interfaces, typed custom events with detail payloads, and `HTMLElementTagNameMap` augmentation for `querySelector` narrowing. A standards-based [Custom Elements Manifest](https://github.com/webcomponents/custom-elements-manifest) drives Storybook, VS Code Custom Data, and the BareDOM framework adapters themselves — if you build tooling on top of BareDOM, the manifest is the source of truth.

---

## Installation

```bash
npm install @vanelsas/baredom
```

Also available via [Clojars](./docs/installation.md#clojurescript-via-clojars) and [standalone ES modules](./docs/installation.md#vanilla-htmljs-via-es-modules). See the [full installation guide](./docs/installation.md).

### Quick start

A minimal page that renders a themed button and alert — no build step required:

```html
<x-theme preset="ocean">
  <x-button>Click me</x-button>
  <x-alert type="info" text="It works!"></x-alert>
</x-theme>
<script type="module">
  import { init as initTheme }  from '@vanelsas/baredom/x-theme';
  import { init as initButton } from '@vanelsas/baredom/x-button';
  import { init as initAlert }  from '@vanelsas/baredom/x-alert';
  initTheme(); initButton(); initAlert();
</script>
```

For deeper usage by stack, see the [JavaScript Developer Guide](./docs/javascript-guide.md), [TypeScript Guide](./docs/typescript.md), or [ClojureScript Guide](./docs/clojurescript-guide.md).

---

## Framework adapters

BareDOM components are native HTML elements — they work natively in any framework, with no adapter required. The published adapters below add typed props, typed custom events, and framework-idiomatic ergonomics on top of the same underlying components.

| Framework | Package | Minimum version | Docs |
|-----------|---------|-----------------|------|
| React 19+   | `@vanelsas/baredom-react`   | 2.6.0 | [README](./adapters/react/README.md) · [npm](https://www.npmjs.com/package/@vanelsas/baredom-react) |
| Vue 3.4+    | `@vanelsas/baredom-vue`     | 2.6.0 | [README](./adapters/vue/README.md) · [npm](https://www.npmjs.com/package/@vanelsas/baredom-vue) |
| Angular 17+ | `@vanelsas/baredom-angular` | 2.6.0 | [README](./adapters/angular/README.md) · [npm](https://www.npmjs.com/package/@vanelsas/baredom-angular) |
| Svelte 5+   | `@vanelsas/baredom-svelte`  | 2.6.0 | [README](./adapters/svelte/README.md) · [npm](https://www.npmjs.com/package/@vanelsas/baredom-svelte) |
| Solid 1.9+  | `@vanelsas/baredom-solid`   | 2.6.0 | [README](./adapters/solid/README.md) · [npm](https://www.npmjs.com/package/@vanelsas/baredom-solid) |

All adapters are auto-generated from the same Custom Elements Manifest, so adding a new BareDOM component updates every adapter in lockstep.

---

## Components

**103 UI web components across 11 categories** — from foundational UI controls to morphing animations, organic effects, and scroll-driven storytelling — plus **5 [BareBuild](./barebuild/docs/read-side.md) orchestration elements** (read-side: router / route / data; write-side alpha: action / invalidate-on).

| Category | Count | Examples |
|----------|------:|----------|
| **Form**       | 23 | Button · Checkbox · Slider · Combobox · OTP Input · Color Picker |
| **Feedback**   | 11 | Alert · Toast · Spinner · Progress · Skeleton · Notification Center |
| **Navigation** | 8  | Navbar · Sidebar · Breadcrumbs · Tabs · Pagination · Menu |
| **Layout**     | 10 | Card · Grid · Bento Grid · Split Pane · Container · Collapse |
| **Data**       | 11 | Avatar · Table · Chart · Timeline · Calendar · Stat |
| **Overlay**    | 9  | Modal · Drawer · Popover · Tooltip · Welcome Tour · Command Palette |
| **Display**    | 6  | Icon · Image · Typography · Code · Kbd · Spotlight Card |
| **Animation**  | 6  | Kinetic Canvas · Kinetic Typography · Morph Stack · Soft Body · Splash |
| **Effects**    | 12 | Liquid Glass · Confetti · Neural Glow · Metaball Cursor · Organic Shape |
| **Scroll**     | 5  | Scroll · Scroll Parallax · Scroll Stack · Scroll Story · Scroll Timeline |
| **Utility**    | 2  | i18n · i18n Provider |
| **Orchestration** | 5 | BareBuild Router · Route · Data — read-side; Action · Invalidate-On — write-side (alpha); not adapter-wrapped |

See [**docs/components.md**](./docs/components.md) for the full per-component catalogue with one-line descriptions and links to each component's API documentation.

---

## Time-Travel Debugger

[`x-trace-history`](./docs/x-trace-history.md) is BareDOM's dev-only debugger. It records every CustomEvent dispatch, observed attribute change, instance-field write, and lifecycle callback as a navigable timeline with cause→effect navigation — so when a bug appears, you can step back to the exact event that caused it.

- **Zero production cost.** When inactive, the recorder pays a single nil-check per hook site and no per-event overhead.
- **One-line activation.** `import "@vanelsas/baredom/x-trace-history"` and append `?baredom-trace-history` to the URL — a floating dock attaches to the viewport.
- **Bug-share via URL.** Record a session, copy the URL, and the recipient sees the same timeline.
- **Adapter-aware.** Works alongside the React, Vue, Angular, Svelte, and Solid adapters.

See [`docs/x-trace-history.md`](./docs/x-trace-history.md) for the full guide — search, console API, recording sessions, import/export, and the JSON schema.

---

## Theming

Wrap any subtree in `<x-theme>` to apply a coordinated palette across every BareDOM component inside it. Eight presets ship in the box — `default`, `ocean`, `forest`, `sunset`, `neo-brutalist`, `aurora`, `mono-ai`, `warm-mineral` — each with light and dark variants that follow `prefers-color-scheme`.

Register your own preset, override individual tokens via CSS, or nest themes for per-section palettes. See [`docs/x-theme.md`](./docs/x-theme.md) for the full API (presets, `registerPreset`, CSS overrides) and [`docs/THEMING.md`](./docs/THEMING.md) for the token catalogue.

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

## About

Like most Clojure/ClojureScript developers starting out with UIs, I went through the common phases of using Reagent and Re-frame—which are great utilities in their own right. However, as my UIs became larger and more complex, bundle sizes increased, and I found myself spending too much time rebuilding generic, reusable components from scratch.

I started looking for a different approach and discovered Web Components. I built a few, but didn't have the spare time to develop a comprehensive set that could be used in any project. Then AI arrived. While experimenting with Claude Code, I realised that 1 + 1 could be 3. That is how BareDOM, my first open-source project, was born.

I first built the usual suspects for web components, a basis to create a UI. I then thought about trying something more exciting, with animations, shapes, colours, and a whole range of web components that deal with morphing, kinetics and organic styles were born. I hope it brings you joy when using them in your web application!

---

## License

MIT
