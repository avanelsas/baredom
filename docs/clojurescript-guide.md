# ClojureScript Guide

This guide covers using BareDOM components from ClojureScript. For JavaScript/TypeScript, see the [JavaScript Developer Guide](./javascript-guide.md). For installation options, see the [installation guide](./installation.md).

---

## 1. Register components

Whichever installation method you chose, the pattern is the same: require/import each component you need and call its `init` function once before any rendering. Only the components you register are active on the page.

## 2. Add a renderer

BareDOM components are plain DOM elements. You need no framework to use them — only a small renderer that turns ClojureScript hiccup vectors into DOM nodes and keeps them in sync with your state.

The `bare-demo/` project includes a complete renderer (~120 lines) with DOM reconciliation that you can copy into any ClojureScript project. See [`bare-demo/src/bare_demo/renderer.cljs`](../bare-demo/src/bare_demo/renderer.cljs). No Node.js required — just Java and the Clojure CLI.

What the renderer provides:

- **Hiccup syntax** — describe UI as nested vectors: `[:tag {:attr val} children]`
- **Prop handling** — `:on-*` keys become event listeners; `true`/`false` toggle boolean attributes; everything else calls `setAttribute`
- **DOM reconciliation** — on re-render, the existing DOM is patched in place. Elements are never destroyed and recreated, so Web Components keep their lifecycle, shadow DOM, focus state, and animations intact.
- **`mount!`** — renders the view and attaches `add-watch` to a state atom so every `swap!` triggers a reconciliation pass

## 3. Write views with hiccup syntax

Views are plain ClojureScript functions that return nested vectors. The first element of each vector is a keyword matching the element tag name. An optional map of props follows, then children.

```clojure
;; String and number attributes
[:x-button {:variant "primary"} "Save changes"]
[:x-button {:variant "secondary" :size "sm"} "Cancel"]

;; Boolean attributes — true sets the attribute, false/nil removes it
[:x-button {:variant "danger" :disabled true} "Delete"]
[:x-button {:variant "primary" :loading true} "Saving..."]
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

## 4. Handle events and manage state

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

## 5. Theming

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

For centralised theming with presets, see [`<x-theme>`](./x-theme.md).

---

## bare-demo — starter template

The `bare-demo/` folder is a ready-to-use ClojureScript application that consumes BareDOM components with **zero framework dependency and no Node.js**. It is designed as a starting point for developers building new web apps on top of BareDOM.

The architecture is built on three ideas:

- **Declarative hiccup views.** UI is described as nested ClojureScript vectors — the same syntax used by Reagent and Hiccup. Views are plain functions, easy to compose and reason about.
- **A single state atom with reactive rendering.** All UI state lives in one `defonce` atom. `mount!` attaches `add-watch` so every `swap!` triggers a re-render automatically.
- **DOM reconciliation, not rebuild.** On state changes the renderer patches the existing DOM in place — updating attributes, text, and children without destroying elements. Web Components keep their lifecycle, shadow DOM, focus state, and animations intact.

**Run it:**

```bash
cd bare-demo
clj -M:dev
```

Then open `http://localhost:8001`.

See [`bare-demo/README.md`](../bare-demo/README.md) for a full walkthrough of the renderer, component registration, view syntax, state management, and theming.

> **Prefer npm?** The `bare-node-demo/` folder contains the same demo consuming BareDOM via npm. Run it with `cd bare-node-demo && npm install && npm start` (opens on `http://localhost:8003`).
