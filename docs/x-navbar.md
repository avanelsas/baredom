# x-navbar

## Overview

`x-navbar` is an accessible, themeable navigation bar web component designed to structure application navigation areas.
It provides a flexible slot layout for branding, navigation links, actions, and optional menu toggles while maintaining consistent styling and accessibility behavior.

The component is framework-agnostic and works with plain HTML, React, Reagent, or any framework that supports custom elements.

`x-navbar` manages layout and accessibility only. It does **not** control navigation menus or sidebars. Consumers are expected to wire their own behavior (for example opening an `x-sidebar`).

---

# Tag

```
<x-navbar>
```

---

# Attributes

| Attribute     | Type    | Default           | Description                                       |
| ------------- | ------- | ----------------- | ------------------------------------------------- |
| `label`       | string  | —                 | Accessible label for the navigation landmark      |
| `variant`     | enum    | `"default"`       | Visual style variant                              |
| `orientation` | enum    | `"horizontal"`    | Layout direction                                  |
| `alignment`   | enum    | `"space-between"` | Content alignment behavior                        |
| `breakpoint`  | string  | `"md"`            | Responsive breakpoint token                       |
| `sticky`      | boolean | `false`           | Makes the navbar stick to the top of the viewport |
| `elevated`    | boolean | `false`           | Adds elevation shadow styling                     |

---

# Attribute values

## variant

| Value         | Description                                    |
| ------------- | ---------------------------------------------- |
| `default`     | Standard navigation bar with backdrop blur      |
| `subtle`      | Transparent background, no blur                 |
| `inverted`    | Dark inverted style                             |
| `transparent` | Fully transparent, no border, no shadow         |

## orientation

| Value        | Description                          |
| ------------ | ------------------------------------ |
| `horizontal` | Default horizontal navigation layout |
| `vertical`   | Vertical stacked layout              |

## alignment

| Value           | Description                          |
| --------------- | ------------------------------------ |
| `start`         | Left aligned                         |
| `center`        | Centered content                     |
| `space-between` | Default distributed layout (default) |

## breakpoint

| Value | Description |
| ----- | ----------- |
| `sm`  | Small       |
| `md`  | Medium      |
| `lg`  | Large       |
| `xl`  | Extra large |

Invalid attribute values are normalized to their defaults internally (the host attribute is not rewritten).

---

# Properties

These JavaScript properties reflect the corresponding attributes.

| Property      | Type    | Reflects attribute |
| ------------- | ------- | ------------------ |
| `sticky`      | boolean | `sticky`           |
| `elevated`    | boolean | `elevated`         |
| `label`       | string  | `label`            |
| `variant`     | string  | `variant`          |
| `orientation` | string  | `orientation`      |
| `alignment`   | string  | `alignment`        |
| `breakpoint`  | string  | `breakpoint`       |

---

# Events

| Event            | Detail                          | Description                                              |
| ---------------- | ------------------------------- | -------------------------------------------------------- |
| `navigate`       | `{ href: string, source: string }` | Dispatched when a link (`<a>`) inside the navbar is clicked |
| `brand-activate` | `{ source: string }`           | Dispatched when the brand slot content is clicked         |
| `focus-visible`  | `{}`                           | Dispatched when keyboard focus is detected within the bar |

All events bubble, are composed, and are **not** cancelable.

The `source` field is `"pointer"` for mouse/pointer clicks, `"keyboard"` for keyboard activation, or `"programmatic"` otherwise.

---

# Slots

| Slot      | Description                     |
| --------- | ------------------------------- |
| `brand`   | Branding element such as a logo |
| `start`   | Content placed after the brand  |
| (default) | Navigation links                |
| `actions` | Right aligned action buttons    |
| `toggle`  | Optional menu toggle control    |
| `end`     | Content placed at the far end   |

Unused slot regions collapse automatically.

---

# Layout structure

```
x-navbar (host)
 └─ nav [part="base"]
     └─ div [part="bar"]
         ├─ div [part="brand"]   → slot[name="brand"]
         ├─ div [part="start"]   → slot[name="start"]
         ├─ div [part="nav"]     → slot (default)
         ├─ div [part="actions"] → slot[name="actions"]
         ├─ div [part="toggle"]  → slot[name="toggle"]
         └─ div [part="end"]     → slot[name="end"]
```

---

# Accessibility

## Landmark role

The shadow root contains a `<nav>` element, providing a navigation landmark.

## Label

When the `label` attribute is set to a non-empty string, it is applied as `aria-label` on the inner `<nav>`:

```html
<x-navbar label="Main navigation">
```

## Focus behavior

* Focus styles appear when keyboard navigation is detected (`focus-visible`).
* A `data-focus-visible-within` attribute is set on the host for external styling hooks.
* Focus rings are controlled via the `--x-navbar-focus-ring` CSS token.

## Screen readers

* Slot content is exposed normally.
* The component does not alter semantic meaning of slotted elements.

---

# Styling

## CSS custom properties

| Variable                           | Default                          | Description              |
| ---------------------------------- | -------------------------------- | ------------------------ |
| `--x-navbar-height`                | `4rem`                           | Navbar height            |
| `--x-navbar-padding-inline`        | `1rem`                           | Horizontal padding       |
| `--x-navbar-gap`                   | `0.75rem`                        | Spacing between elements |
| `--x-navbar-bg`                    | `rgba(255,255,255,0.88)`         | Background color         |
| `--x-navbar-color`                 | `#0f172a`                        | Text color               |
| `--x-navbar-border`                | `rgba(148,163,184,0.22)`         | Border color             |
| `--x-navbar-shadow`                | `0 8px 24px rgba(15,23,42,0.08)` | Shadow when elevated     |
| `--x-navbar-focus-ring`            | `#60a5fa`                        | Focus outline color      |
| `--x-navbar-z-index`               | `40`                             | Z-index when sticky      |
| `--x-navbar-radius`                | `1rem`                           | Border radius            |
| `--x-navbar-transition-duration`   | `180ms`                          | Transition duration      |
| `--x-navbar-transition-easing`     | `cubic-bezier(0.2,0,0,1)`       | Transition easing        |
| `--x-navbar-align-items`           | `center`                         | Vertical alignment       |

## Example override

```css
x-navbar {
  --x-navbar-height: 4.5rem;
  --x-navbar-bg: var(--surface);
}
```

---

# Light / Dark mode

`x-navbar` supports automatic theme switching using `@media (prefers-color-scheme: dark)`. Consumers may override theme tokens for custom themes.

---

# Motion

Animated properties: `background`, `border-color`, `box-shadow`.

All transitions respect `@media (prefers-reduced-motion: reduce)` by disabling transitions entirely.

---

# Examples

## Basic navbar

```html
<x-navbar sticky elevated label="Main navigation">
  <a slot="brand" href="/">My App</a>

  <nav>
    <a href="/">Home</a>
    <a href="/docs">Docs</a>
  </nav>

  <div slot="actions">
    <x-button variant="secondary">Login</x-button>
  </div>
</x-navbar>
```

## With sidebar toggle

```html
<x-navbar sticky elevated label="Main navigation">
  <a slot="brand" href="/">My App</a>

  <div slot="actions">
    <x-button id="menu-button">Menu</x-button>
  </div>
</x-navbar>

<x-sidebar id="sidebar"></x-sidebar>

<script>
document.getElementById("menu-button")
  .addEventListener("press", () => {
    document.getElementById("sidebar").open = true
  })
</script>
```

## Listening for events

```html
<x-navbar id="nav" label="Main">
  <a slot="brand" href="/">Acme</a>
  <a href="/docs">Docs</a>
  <a href="/about">About</a>
</x-navbar>

<script>
const nav = document.getElementById("nav")

nav.addEventListener("navigate", (e) => {
  console.log("Navigate to:", e.detail.href)
})

nav.addEventListener("brand-activate", (e) => {
  console.log("Brand clicked:", e.detail.source)
})
</script>
```

## Reagent integration

```clojure
[wc/wc
 :x-navbar
 {:class "topbar"
  :sticky true
  :elevated true
  :label "Main navigation"
  :wc/events {"navigate"       #(rf/dispatch [:nav/navigate (.. % -detail -href)])
              "brand-activate" #(rf/dispatch [:nav/home])}}

 [:a {:slot "brand"} "Van Elsas"]

 [:div {:slot "actions"}
  [wc/wc
   :x-button
   {:variant "secondary"
    :wc/events {"press" #(rf/dispatch [:navigation/menu-clicked])}}
   "Menu"]]]
```
