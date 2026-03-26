# BareDOM Reagent Demo

A ClojureScript demo that renders BareDOM web components using [Reagent](https://reagent-project.github.io/) — a minimalist ClojureScript interface to React.

This demo is visually identical to `bare-demo/` but replaces the custom hiccup renderer with Reagent. It demonstrates the integration patterns, trade-offs, and workarounds needed when pairing a virtual-DOM framework with native Custom Elements.

## Running the demo

```bash
cd bare-reagent-demo
npm install
npm start          # npx shadow-cljs watch app
```

Open [http://localhost:8002](http://localhost:8002). Hot reload is enabled — editing any source file in `src/` re-renders the page without a full browser refresh.

## How Reagent renders BareDOM components

BareDOM components are standard Custom Elements (HTML tags). Reagent treats any unknown tag name as a custom element and passes hiccup props down as React props, which React maps to DOM attributes.

For most components this works transparently:

```clojure
[:x-button {:variant "primary" :on-click #(swap! state/app assoc :modal-open true)}
 "Learn more"]
```

Reagent converts `:on-click` to React's `onClick`, which works for standard DOM events like `click`. The `variant` attribute is set directly on the element.

Reactive re-renders are driven by `reagent.core/atom`. Any component that dereferences a ratom with `@` will automatically re-render when that atom changes — no `add-watch` or manual render trigger required:

```clojure
(defonce app
  (r/atom {:sidebar-open false
           :modal-open   false
           :active-nav   "home"}))

(defn navbar []
  ;; This component re-renders whenever @state/app changes because it
  ;; dereferences the ratom inside the render function.
  [:x-navbar {:elevated true}
   [:div {:slot "actions"}
    [:x-button {:on-click #(swap! state/app update :sidebar-open not)}
     "Menu"]]])
```

## Handling custom events

BareDOM components fire custom DOM events that React does not know about. Two examples in this demo:

| Component | Custom event | Payload |
|---|---|---|
| `x-sidebar` | `"toggle"` | `event.detail.open` (boolean) |
| `x-modal` | `"x-modal-dismiss"` | (no payload) |

React's synthetic event system only covers standard browser events. Custom events like `"x-modal-dismiss"` are silently ignored when written as `:on-x-modal-dismiss` in a Reagent component.

The solution is `reagent/create-class` with `component-did-mount` / `component-will-unmount` lifecycle hooks to attach and clean up the listeners imperatively on the real DOM node:

```clojure
(defn- modal []
  (let [on-dismiss #(swap! state/app assoc :modal-open false)]
    (r/create-class
     {:component-did-mount
      (fn [this]
        (.addEventListener (rdom/dom-node this) "x-modal-dismiss" on-dismiss))

      :component-will-unmount
      (fn [this]
        (.removeEventListener (rdom/dom-node this) "x-modal-dismiss" on-dismiss))

      :reagent-render
      (fn []
        [:x-modal {:open (:modal-open @state/app) :label "About BareDOM"}
         ;; ... slots and children ...
         ])})))
```

`rdom/dom-node` returns the real DOM element that Reagent rendered — in this case the `<x-modal>` element itself — so `addEventListener` attaches directly to the custom element.

The event handler is defined in a `let` binding *outside* the render function so the same function reference is used for both `addEventListener` and `removeEventListener`. Using an anonymous function at call-site would create a new reference on every render, making `removeEventListener` a no-op.

## Comparison: bare-demo vs bare-reagent-demo

| Concern | bare-demo | bare-reagent-demo |
|---|---|---|
| Renderer | Custom ~55-line hiccup→DOM | `reagent.dom/render` |
| State atom | `cljs.core/atom` | `reagent.core/atom` |
| Re-render mechanism | `add-watch` → full `innerHTML` clear + rebuild | Reagent's reactive graph → VDOM diff |
| Standard events (`:on-click`) | `addEventListener` via custom prop handler | React synthetic events |
| Custom events | Same `addEventListener` path | `create-class` lifecycle + `addEventListener` |
| Hot reload | `reload!` re-mounts view fn | `reload!` calls `rdom/render` again |
| Dependencies | None (zero framework) | `reagent "1.2.0"` (React + Reagent) |
| `renderer.cljs` | Present | Absent — Reagent replaces it |
| Port | 8001 | 8002 |

## Project structure

```
src/bare_reagent_demo/
├── core.cljs          # Registers BareDOM components, mounts Reagent root
├── state.cljs         # Single reagent.core/atom for all UI state
└── views/
    └── app.cljs       # All view components (navbar, sidebar, modal, etc.)
```

The `renderer.cljs` file from `bare-demo` is absent — Reagent is the renderer.

## Key Reagent concepts used

**`reagent.core/atom`** — a drop-in replacement for `cljs.core/atom` that additionally notifies any Reagent component that dereferenced it to re-render. No watcher setup needed.

**Function components** — a plain ClojureScript function returning hiccup is a valid Reagent component. Compose them with `[component-fn args…]` syntax (square brackets), not `(component-fn args…)`, so Reagent can track reactivity boundaries.

**`reagent/create-class`** — creates a React class component, exposing lifecycle methods (`component-did-mount`, `component-will-unmount`, etc.). Required whenever you need to interact with the real DOM node (e.g., to attach native event listeners).

**`reagent.dom/dom-node`** — given a Reagent component instance (`this` inside lifecycle), returns the underlying DOM element. Used to call `addEventListener` on a custom element.
