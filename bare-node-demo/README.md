# bare-node-demo — Node/NPM variant of the ClojureScript starter template

A minimal ClojureScript application that demonstrates how to build a web app with BareDOM's native web components — **no framework, no virtual DOM, no reactive runtime**. This variant consumes BareDOM via NPM (`@vanelsas/baredom`).

> **Prefer no Node.js?** See [`bare-demo/`](../bare-demo/) for the same demo using only Clojars and the Clojure CLI — no NPM required.

---

## Components showcased

| Component | What the demo shows |
|-----------|---------------------|
| `x-navbar` | Top navigation bar with the BareDOM logo and a "Menu" button |
| `x-sidebar` | Modal-variant side panel; opens on "Menu" click, closes when a nav item is selected |
| `x-button` | Primary and ghost button variants wired to state changes |
| `x-modal` | Centred dialog with backdrop, opened by a button in the main content area |
| `x-container` | Responsive max-width wrapper for the welcome content |
| `x-card` | Elevated card used in the event log panel |

---

## Prerequisites

- [Node.js](https://nodejs.org/) >= 18
- [Java](https://adoptium.net/) >= 11 (required by shadow-cljs / ClojureScript compiler)

---

## Install and run

```bash
cd bare-node-demo
npm install
npm start
```

Then open `http://localhost:8003`. The page hot-reloads on every source change.

`npm start` runs `npx shadow-cljs watch app`, which compiles the ClojureScript sources and starts the dev server.

---

## Project structure

```
bare-node-demo/
├── shadow-cljs.edn             # Standalone shadow-cljs build config
├── package.json                # NPM deps: @vanelsas/baredom + shadow-cljs
├── public/
│   ├── index.html              # HTML shell + all CSS (including custom property overrides)
│   └── assets/
│       ├── baredom_lightmode.svg   # Logo for light mode
│       └── baredom_darkmode.svg    # Logo for dark mode
└── src/bare_node_demo/
    ├── core.cljs               # Entry point — registers components, mounts app
    ├── renderer.cljs           # Hiccup → DOM renderer with reconciliation (~120 lines)
    ├── state.cljs              # Single state atom
    └── views/
        └── app.cljs            # Pure view functions
```

---

## The renderer

`renderer.cljs` is the only piece of infrastructure the app needs. It provides two things:

1. **Initial render** — converts hiccup vectors into real DOM nodes
2. **DOM reconciliation** — on subsequent renders, patches the existing DOM in place instead of rebuilding it

This means Web Components stay in the DOM across state changes. Their lifecycle callbacks are not re-triggered, shadow DOM is preserved, focus state is maintained, and CSS transitions continue uninterrupted.

### Prop rules

When the renderer encounters a key in the props map it applies one of four rules:

| Key form | Action |
|----------|--------|
| `:on-<event>` | `addEventListener("event", handler)` — tracked for clean removal on re-render |
| `true` | `setAttribute(attr, "")` — sets a boolean attribute |
| `false` / `nil` | `removeAttribute(attr)` — removes the attribute |
| anything else | `setAttribute(attr, str(value))` |

Event listeners are stored on each element and properly cleaned up when the handler changes or the prop is removed.

### Mounting

```clojure
;; Render once
(render! container view-fn)

;; Render now and re-render on every state change
(mount! container view-fn state-atom)
```

`mount!` calls `render!` immediately, then attaches `add-watch` to the atom. Every `swap!` or `reset!` triggers a reconciliation pass that applies only the DOM changes needed to reflect the new state.

---

## Registering components

Import each component from the `@vanelsas/baredom` package and call `.init` once in your `init!` function. Registration is idempotent — calling it on an already-registered element is a no-op.

```clojure
(ns my-app.core
  (:require
   ["@vanelsas/baredom/x-navbar"    :as x-navbar]
   ["@vanelsas/baredom/x-sidebar"   :as x-sidebar]
   ["@vanelsas/baredom/x-button"    :as x-button]
   ["@vanelsas/baredom/x-modal"     :as x-modal]
   ["@vanelsas/baredom/x-container" :as x-container]
   [my-app.renderer :as renderer]
   [my-app.state    :as state]
   [my-app.views.app :as app-view]))

(defn- register-components! []
  (.init x-navbar)
  (.init x-sidebar)
  (.init x-button)
  (.init x-modal)
  (.init x-container))

(defn init! []
  (register-components!)
  (renderer/mount! (.getElementById js/document "app") view state/app))
```

Each import path (`"@vanelsas/baredom/x-navbar"` etc.) is a separate ESM module in the package. The `init` named export registers the custom element with the browser's Custom Elements registry.

---

## Writing views

Views are plain ClojureScript functions that return nested vectors. The shape is:

```
[tag-keyword]
[tag-keyword props-map]
[tag-keyword props-map & children]
[tag-keyword & children]
```

Three categories of props:

```clojure
;; 1. Events — :on-<name> maps to addEventListener
[:x-button {:on-click (fn [e] ...)} "Click me"]

;; 2. Boolean attributes — true sets, false/nil removes
[:x-button {:disabled true}  "Disabled"]
[:x-button {:disabled false} "Enabled"]

;; 3. String attributes — everything else is stringified
[:x-button {:variant "primary" :size "sm"} "Save"]
```

A real example from the demo — the navbar:

```clojure
(defn- navbar [_s]
  [:x-navbar {:label "BareDOM Demo" :elevated true}
   [:picture {:slot "brand"}
    [:source {:srcset "/assets/baredom_darkmode.svg"
              :media  "(prefers-color-scheme: dark)"}]
    [:img {:src "/assets/baredom_lightmode.svg" :height "32" :alt "BareDOM"}]]
   [:div {:slot "actions"}
    [:x-button {:variant  "ghost"
                :size     "sm"
                :on-click (fn [_]
                            (swap! state/app update :sidebar-open not))}
     "Menu"]]])
```

Slot content is placed by setting the `:slot` attribute on any light-DOM child. The component's shadow DOM picks it up automatically.

---

## State and reactivity

The entire app state lives in one atom:

```clojure
(defonce app
  (atom {:sidebar-open false
         :modal-open   false
         :active-nav   "home"}))
```

State changes use standard `swap!`:

```clojure
;; Open the modal
(swap! state/app assoc :modal-open true)

;; Close the sidebar and record the active nav item
(swap! state/app assoc :sidebar-open false :active-nav "components")

;; Toggle the sidebar
(swap! state/app update :sidebar-open not)
```

`mount!` attaches a single `add-watch` that calls `render!` on every change. The view function receives the dereffed state map, so every re-render sees the latest values. The reconciler then diffs the resulting hiccup against the live DOM and applies only the mutations needed — an attribute toggle, a text update, a child added or removed.

---

## Theming

All BareDOM components expose their visual details as CSS custom properties following the naming convention `--x-<component>-<property>`. Override them at any scope in `public/index.html`:

```css
/* Remove the navbar's default border-radius */
x-navbar {
  --x-navbar-radius: 0;
}

/* Give the sidebar a dark slate background */
x-sidebar {
  --x-sidebar-bg: #1e293b;
  --x-sidebar-fg: #e2e8f0;
}

/* Match the modal background to the sidebar */
x-modal {
  --x-modal-bg: #1e293b;
  --x-modal-fg: #e2e8f0;
}

/* Adjust for dark mode */
@media (prefers-color-scheme: dark) {
  x-sidebar { --x-sidebar-bg: #0d1628; }
  x-modal   { --x-modal-bg:   #0d1628; }
}
```

Because the components use open Shadow DOM, custom properties pierce the shadow boundary and apply to the internal elements that reference them. No JavaScript is involved — CSS does the work.

---

## Key takeaways

- **No framework required.** BareDOM components are native HTML elements. The renderer is ~120 lines of ClojureScript with no dependencies beyond the standard library. Copy it into any project.
- **DOM reconciliation respects Web Components.** Elements stay in the DOM across state changes. Lifecycle callbacks run once, shadow DOM is preserved, and CSS transitions are never interrupted.
- **Scales to real apps.** The declarative hiccup + atom + reconciler pattern handles any number of views, state values, and components without manual DOM wiring.
- **Standards all the way down.** Custom Elements v1, Shadow DOM, ES modules, CSS custom properties — all natively supported in every modern browser. No polyfills, no adapters.
