# x-container

## Overview

`x-container` is an accessible, themeable layout container web component for constraining content width, centering page content, and applying consistent responsive spacing.

It is a passive layout primitive:

* no custom events
* no internal interaction model
* no focus management
* no child behavior orchestration

The component is framework-agnostic and works with plain HTML, React, Reagent, or any framework that supports custom elements.

---

# Tag

```html
<x-container>
```

---

# Purpose

Provide a low-level layout primitive that can:

* constrain content width
* center content horizontally
* apply consistent inline padding
* expose a semantic root element
* support light and dark mode theming
* expose surface and spacing tokens for consumers

---

# Attributes

| Attribute | Type         | Description                                            |
| --------- | ------------ | ------------------------------------------------------ |
| `as`      | enum         | Semantic root element rendered inside shadow DOM       |
| `size`    | enum         | Max-width preset                                       |
| `padding` | enum         | Inline padding preset                                  |
| `center`  | boolean-like | Controls horizontal centering                          |
| `fluid`   | boolean      | Removes width constraint and stretches full width      |
| `label`   | string       | Accessible label applied to the internal semantic root |

---

# Attribute values

## as

Supported values:

* `div`
* `section`
* `article`
* `main`
* `aside`
* `header`
* `footer`
* `nav`

Default:

```text
div
```

Invalid values normalize to `div`.

---

## size

Supported values:

* `xs`
* `sm`
* `md`
* `lg`
* `xl`
* `full`

Default:

```text
lg
```

Invalid values normalize to `lg`.

---

## padding

Supported values:

* `none`
* `sm`
* `md`
* `lg`

Default:

```text
md
```

Invalid values normalize to `md`.

---

## center

`center` is a default-true boolean-style option.

Behavior:

* absent attribute → centered
* `center="false"` → not centered

This is exposed as a property for easier programmatic control.

Default:

```text
true
```

---

## fluid

Boolean attribute.

Behavior:

* absent → normal max-width behavior
* present → `max-width: none; width: 100%`

Default:

```text
false
```

---

## label

Optional string.
When provided, it becomes:

```text
aria-label
```

on the internal semantic root element.

---

# Properties

| Property | Type    | Description                    |
| -------- | ------- | ------------------------------ |
| `center` | boolean | Reflects layout centering      |
| `fluid`  | boolean | Reflects full-width fluid mode |

## Notes

### center

`center` is implemented as a default-true property:

* `el.center` returns `true` by default
* setting `el.center = false` writes `center="false"`
* setting `el.center = true` removes the attribute again

### fluid

`fluid` is a standard reflected boolean property.

---

# Events

`x-container` emits **no custom events**.

This is intentional. It is a layout primitive only.

---

# Slots

## Default slot

`x-container` provides a single default slot for arbitrary content.

Example:

```html
<x-container>
  <h1>Hello world</h1>
  <p>Content goes here.</p>
</x-container>
```

No named slots are supported.

---

# Semantic root behavior

The host element is always:

```html
<x-container>
```

But inside shadow DOM, the component renders a semantic root element based on `as`.

Examples:

### Default

```html
<div part="base">
  <slot></slot>
</div>
```

### With `as="section"`

```html
<section part="base">
  <slot></slot>
</section>
```

### With `as="main"`

```html
<main part="base" aria-label="Main content">
  <slot></slot>
</main>
```

This allows the component to provide semantic landmarks without changing the host tag.

---

# Accessibility

`x-container` is intentionally simple.

It guarantees:

* semantic internal root selected from `as`
* `aria-label` support via `label`
* preservation of slotted child semantics
* no keyboard behavior
* no focus management
* no ARIA role overrides

Examples:

```html
<x-container as="main" label="Main content"></x-container>
<x-container as="nav" label="Primary navigation"></x-container>
```

---

# Styling

The internal semantic root exposes one stable part:

```text
base
```

Example:

```css
x-container::part(base) {
  background: white;
}
```

---

# CSS Custom Properties

## Width tokens

| Variable                     | Description           |
| ---------------------------- | --------------------- |
| `--x-container-max-width-xs` | Width for `size="xs"` |
| `--x-container-max-width-sm` | Width for `size="sm"` |
| `--x-container-max-width-md` | Width for `size="md"` |
| `--x-container-max-width-lg` | Width for `size="lg"` |
| `--x-container-max-width-xl` | Width for `size="xl"` |

Default values:

* `xs` → `480px`
* `sm` → `640px`
* `md` → `768px`
* `lg` → `1024px`
* `xl` → `1280px`

`size="full"` removes max-width entirely.

---

## Padding tokens

| Variable                      | Description                               |
| ----------------------------- | ----------------------------------------- |
| `--x-container-padding-sm`    | Padding for `padding="sm"`                |
| `--x-container-padding-md`    | Padding for `padding="md"`                |
| `--x-container-padding-lg`    | Padding for `padding="lg"`                |
| `--x-container-padding-block` | Block padding applied to the base element |

Defaults:

* `sm` → `0.5rem`
* `md` → `1rem`
* `lg` → `1.5rem`
* block padding → `0`

---

## Layout and surface tokens

| Variable                      | Description               |
| ----------------------------- | ------------------------- |
| `--x-container-margin-inline` | Margin used for centering |
| `--x-container-bg`            | Background                |
| `--x-container-color`         | Foreground text color     |
| `--x-container-border`        | Border color              |
| `--x-container-radius`        | Border radius             |
| `--x-container-shadow`        | Box shadow                |

Defaults:

* background → `transparent`
* border → `transparent`
* radius → `0`
* shadow → `none`

So by default, `x-container` is layout-first, not card-like.

---

# Light / Dark mode

`x-container` supports automatic theme adaptation using:

```css
color-scheme: light dark;
@media (prefers-color-scheme: dark) { ... }
```

Default text tokens:

### Light mode

* `--x-container-color: #0f172a`

### Dark mode

* `--x-container-color: #e5e7eb`

Background remains transparent by default in both modes.

---

# Motion

`x-container` has no required animated behavior.

It still includes reduced-motion-safe behavior:

```css
@media (prefers-reduced-motion: reduce) {
  [part='base'] {
    transition: none;
  }
}
```

Since the component does not animate by default, this is mostly defensive.

---

# Internal rendering behavior

The component:

1. reads host attributes
2. normalizes them through the model
3. ensures the correct semantic root exists
4. writes normalized state to internal `data-*` attributes on the `base` part

Internal data attributes include:

* `data-size`
* `data-padding`
* `data-center`
* `data-fluid`

This internal projection avoids rewriting host attributes during render.

---

# Default behavior summary

Default rendered state is effectively:

* `as = div`
* `size = lg`
* `padding = md`
* `center = true`
* `fluid = false`

So a plain container:

```html
<x-container></x-container>
```

renders as a centered large-width container with medium inline padding.

---

# Examples

## Basic

```html
<x-container>
  <p>Hello world</p>
</x-container>
```

---

## Narrow reading width

```html
<x-container size="sm">
  <article>
    <h1>Article title</h1>
    <p>Readable narrow content width.</p>
  </article>
</x-container>
```

---

## Full width section

```html
<x-container size="full" padding="lg">
  <section>
    <h2>Full width layout</h2>
  </section>
</x-container>
```

---

## Semantic main region

```html
<x-container as="main" label="Main content">
  <h1>Dashboard</h1>
</x-container>
```

---

## Programmatic property usage

```js
const el = document.querySelector("x-container");

el.center = false;
el.fluid = true;
```

---

# Integration with Reagent

Example usage with the helper wrapper used in the demo app:

```clojure
[wc/wc
 :x-container
 {:size "lg"
  :padding "lg"
  :center true
  :class "page-container"}
 [:section.hero-card
  [:h1 "Hello world"]
  [:p "Content inside a container."]]]
```

---

# Design philosophy

`x-container` is intentionally minimal.

It owns:

* width constraints
* inline spacing
* semantic wrapper selection
* theme-aware layout tokens

It does **not** own:

* cards
* grids
* sections with internal spacing rules
* interactions
* events
* responsive behavior beyond container width presets

That makes it a strong base primitive for larger layout systems.
