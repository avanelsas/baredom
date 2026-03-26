# bare-demo — BareDOM without a framework

This is a minimal ClojureScript application that demonstrates how to consume BareDOM's native web components with **no framework, no virtual DOM, and no reactive runtime**. The entire stack is a ~55-line hiccup renderer, a single state atom, and pure view functions. If you have ever wondered what it takes to use web components in a plain ClojureScript project, this is the answer.

---

## Components showcased

| Component | What the demo shows |
|-----------|---------------------|
| `x-navbar` | Top navigation bar with the BareDOM logo and a "Menu" button |
| `x-sidebar` | Modal-variant side panel; opens on "Menu" click, closes when a nav item is selected |
| `x-button` | Primary and ghost button variants wired to state changes |
| `x-modal` | Centred dialog with backdrop, opened by a button in the main content area |
| `x-container` | Responsive max-width wrapper for the welcome content |

---

## Prerequisites

- [Node.js](https://nodejs.org/) >= 18
- [Java](https://adoptium.net/) >= 11 (required by shadow-cljs / ClojureScript compiler)

---

## Install and run

```bash
cd bare-demo
npm install
npm start
```

Then open `http://localhost:8001`. The page hot-reloads on every source change.

`npm start` runs `npx shadow-cljs watch app`, which compiles the ClojureScript sources and starts the dev server.

---

## Project structure

```
bare-demo/
├── shadow-cljs.edn             # Standalone shadow-cljs build config
├── package.json                # NPM deps: @vanelsas/baredom + shadow-cljs
├── public/
│   ├── index.html              # HTML shell + all CSS (including custom property overrides)
│   └── assets/
│       ├── baredom_lightmode.svg   # Logo for light mode
│       └── baredom_darkmode.svg    # Logo for dark mode
└── src/bare_demo/
    ├── core.cljs               # Entry point — registers components, mounts app
    ├── renderer.cljs           # Hiccup → DOM converter (~55 lines)
    ├── state.cljs              # Single state atom
    └── views/
        └── app.cljs            # Pure view functions
```

---

## The renderer

`renderer.cljs` is the only piece of infrastructure the demo needs. It converts nested ClojureScript vectors (hiccup) into real DOM nodes:

```clojure
(defn create-nodes [x]
  (cond
    (nil? x)    []
    (false? x)  []
    (string? x) [(.createTextNode js/document x)]
    (number? x) [(.createTextNode js/document (str x))]
    (vector? x) [(create-element x)]
    (seq? x)    (mapcat create-nodes x)
    :else       []))
```

**Prop rules** — when the renderer encounters a key in the props map it applies one of four rules:

| Key form | Action |
|----------|--------|
| `:on-<event>` | `addEventListener("event", handler)` — strips the `on-` prefix |
| `true` | `setAttribute(attr, "")` — sets a boolean attribute |
| `false` / `nil` | `removeAttribute(attr)` — removes the attribute |
| anything else | `setAttribute(attr, str(value))` |

**Mounting:**

```clojure
;; Render once
(render! container view-fn)

;; Render now and re-render on every state change
(mount! container view-fn state-atom)
```

`mount!` calls `render!` immediately, then attaches `add-watch` to the atom. Every `swap!` or `reset!` triggers a full re-render. There is no diffing — the container is cleared and rebuilt from the current state value each time.

---

## Registering components

Import each component from the `@vanelsas/baredom` package and call `.init` once in your `init!` function. Registration is idempotent — calling it on an already-registered element is a no-op.

```clojure
(ns bare-demo.core
  (:require
   ["@vanelsas/baredom/x-navbar"    :as x-navbar]
   ["@vanelsas/baredom/x-sidebar"   :as x-sidebar]
   ["@vanelsas/baredom/x-button"    :as x-button]
   ["@vanelsas/baredom/x-modal"     :as x-modal]
   ["@vanelsas/baredom/x-container" :as x-container]
   [bare-demo.renderer :as renderer]
   [bare-demo.state    :as state]
   [bare-demo.views.app :as app-view]))

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

`mount!` attaches a single `add-watch` that calls `render!` on every change. The view function receives the dereffed state map, so every re-render sees the latest values. There are no subscriptions, no selectors, and no partial updates — the whole view is rebuilt from scratch each time.

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

- **No framework required.** BareDOM components are native HTML elements. Any environment that can call `document.createElement` and set attributes can use them.
- **The renderer is tiny.** The hiccup → DOM converter is ~55 lines you can copy verbatim into any ClojureScript project. It has no dependencies beyond the ClojureScript standard library.
- **Standards all the way down.** Custom Elements v1, Shadow DOM, ES modules, CSS custom properties — all natively supported in every modern browser. No polyfills, no adapters.
