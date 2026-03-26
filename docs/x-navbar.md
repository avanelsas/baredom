Below is a regenerated **`docs/x_navbar.md`** based on the current library architecture and the files you provided (`model.cljs` and `x_navbar.cljs`).
It follows the same documentation style used for your other components.

---

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

# Purpose

Provide a structured, accessible navigation bar that:

* organizes brand, navigation, and action elements
* supports responsive layouts
* integrates with theme systems
* works in both light and dark mode
* keeps behavior external to the component

---

# Attributes

| Attribute     | Type    | Description                                       |
| ------------- | ------- | ------------------------------------------------- |
| `sticky`      | boolean | Makes the navbar stick to the top of the viewport |
| `elevated`    | boolean | Adds elevation shadow styling                     |
| `variant`     | enum    | Visual style variant                              |
| `orientation` | enum    | Layout direction                                  |
| `alignment`   | enum    | Content alignment behavior                        |
| `breakpoint`  | string  | Responsive breakpoint token                       |
| `label`       | string  | Accessible label for the navigation landmark      |

---

# Attribute values

## variant

| Value         | Description             |
| ------------- | ----------------------- |
| `default`     | Standard navigation bar |
| `subtle`      | Transparent background  |
| `inverted`    | Dark inverted style     |
| `transparent` | Fully transparent       |

---

## orientation

| Value        | Description                          |
| ------------ | ------------------------------------ |
| `horizontal` | Default horizontal navigation layout |
| `vertical`   | Vertical stacked layout              |

---

## alignment

| Value           | Description                |
| --------------- | -------------------------- |
| `start`         | Left aligned               |
| `center`        | Centered content           |
| `space-between` | Default distributed layout |

---

# Properties

These properties reflect the corresponding attributes.

| Property      | Type    | Description           |
| ------------- | ------- | --------------------- |
| `sticky`      | boolean | Sticky positioning    |
| `elevated`    | boolean | Elevated visual style |
| `variant`     | string  | Visual variant        |
| `orientation` | string  | Layout direction      |
| `alignment`   | string  | Alignment strategy    |

---

# Events

`x-navbar` does **not emit custom events**.

Interactive elements inside the navbar (such as `x-button`) are expected to emit their own events.

---

# Slots

`x-navbar` uses named slots to define layout zones.

| Slot      | Description                     |
| --------- | ------------------------------- |
| `brand`   | Branding element such as a logo |
| `start`   | Content placed after the brand  |
| `default` | Navigation links                |
| `actions` | Right aligned action buttons    |
| `toggle`  | Optional menu toggle control    |
| `end`     | Content placed at the far end   |

---

# Layout structure

```
x-navbar
 └─ nav
     └─ bar
         ├─ brand
         ├─ start
         ├─ nav
         ├─ actions
         ├─ toggle
         └─ end
```

Unused slot regions collapse automatically.

---

# Accessibility

The component implements standard navigation landmark semantics.

### Landmark role

```
<nav role="navigation">
```

### Label

If provided:

```
aria-label="Main navigation"
```

### Focus behavior

* Focus styles appear when keyboard navigation is detected.
* Focus rings are controlled via CSS tokens.

### Screen readers

* Slot content is exposed normally.
* The component does not alter semantic meaning of slotted elements.

---

# Styling

The component exposes CSS custom properties for theming.

## Core tokens

| Variable                    | Description              |
| --------------------------- | ------------------------ |
| `--x-navbar-height`         | Navbar height            |
| `--x-navbar-padding-inline` | Horizontal padding       |
| `--x-navbar-gap`            | Spacing between elements |
| `--x-navbar-bg`             | Background color         |
| `--x-navbar-color`          | Text color               |
| `--x-navbar-border`         | Border color             |
| `--x-navbar-shadow`         | Shadow when elevated     |
| `--x-navbar-focus-ring`     | Focus outline            |
| `--x-navbar-radius`         | Border radius            |

---

## Example override

```
x-navbar {
  --x-navbar-height: 4.5rem;
  --x-navbar-bg: var(--surface);
}
```

---

# Light / Dark mode

`x-navbar` supports automatic theme switching using:

```
@media (prefers-color-scheme: dark)
```

Consumers may override theme tokens for custom themes.

---

# Motion

The component includes minimal transitions.

Animated properties:

* background
* border
* box-shadow

Motion respects:

```
prefers-reduced-motion
```

---

# Example

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

---

# Example with sidebar toggle

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

---

# Integration with Reagent

Example using the helper wrapper used in the demo application.

```
[wc/wc
 :x-navbar
 {:class "topbar"
  :sticky true
  :elevated true
  :label "Main navigation"}

 [:a {:slot "brand"} "Van Elsas"]

 [:div {:slot "actions"}
  [wc/wc
   :x-button
   {:variant "secondary"
    :wc/events {"press" #(rf/dispatch [:navigation/menu-clicked])}}
   "Menu"]]]
```

---

# Design philosophy

`x-navbar` intentionally avoids application logic.

It focuses on:

* layout
* accessibility
* themeability

Application logic such as:

* opening sidebars
* routing
* menu state

should remain in the consumer application.
