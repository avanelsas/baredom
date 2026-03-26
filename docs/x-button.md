# x-button

`x-button` is an accessible, themeable button web component implemented in pure ClojureScript with direct JavaScript interop and Custom Elements V1.

It is designed to be:

- Closure Advanced Compilation safe
- stateless at render time
- framework-free
- themeable via semantic CSS custom properties
- accessible by default through a native internal `<button>`

---

## Tag

```html
<x-button>Save</x-button>
````

---

## Purpose

`x-button` provides a reusable action control for user-triggered operations such as submit, reset, confirm, cancel, toggle, and destructive actions.

It supports:

* disabled state
* loading state
* pressed/toggled state
* semantic button types
* visual variants
* multiple sizes
* optional leading and trailing icons
* optional spinner content
* accessible keyboard and focus behavior

---

## Installation / Registration

The component is registered through the export namespace:

```clojure
(ns app.exports.x-button
  (:require [app.components.x-button.model :as model]
            [app.components.x-button.x-button]))

(defn register!
  []
  (app.components.x-button.x-button/init!))

(def public-api
  {:tag-name model/tag-name
   :properties model/property-api
   :events model/event-schema
   :observed-attributes model/observed-attributes})

(defn ^:export init
  []
  (register!))
```

### Expected export path

```text
src/app/exports/x_button.cljs
```

### Expected component path

```text
src/app/components/x_button/x_button.cljs
```

### Expected model path

```text
src/app/components/x_button/model.cljs
```

### Runtime registration

If your compiled bundle exposes `app.exports.x-button.init`, register the element by calling:

```js
app.exports.x_button.init();
```

Or ensure your compiled application calls the exported `init` function during startup.

---

## Basic Usage

### Default button

```html
<x-button>Save</x-button>
```

### Submit button

```html
<x-button type="submit">Create account</x-button>
```

### Disabled button

```html
<x-button disabled>Unavailable</x-button>
```

### Loading button

```html
<x-button loading label="Saving">
  Saving
  <span slot="spinner" aria-hidden="true"></span>
</x-button>
```

### Pressed / toggle button

```html
<x-button pressed>Bold</x-button>
```

### Icon-only button

```html
<x-button label="Close">
  <svg slot="icon-start" aria-hidden="true" viewBox="0 0 24 24"></svg>
</x-button>
```

### Button with start and end icons

```html
<x-button>
  <svg slot="icon-start" aria-hidden="true" viewBox="0 0 24 24"></svg>
  Next
  <svg slot="icon-end" aria-hidden="true" viewBox="0 0 24 24"></svg>
</x-button>
```

---

## Public API

### Tag name

```text
x-button
```

---

## Attributes

### `disabled`

Boolean attribute.

When present, the button is disabled and cannot be activated.

```html
<x-button disabled>Disabled</x-button>
```

Behavior:

* blocks pointer interaction
* blocks keyboard activation
* suppresses press lifecycle events
* suppresses hover lifecycle events
* disables the internal native `<button>`

---

### `loading`

Boolean attribute.

When present, the button enters a busy/loading state and prevents duplicate activation.

```html
<x-button loading label="Saving">
  Save
  <span slot="spinner" aria-hidden="true"></span>
</x-button>
```

Behavior:

* disables the internal native `<button>`
* sets `aria-busy="true"`
* prevents duplicate activation
* suppresses interaction lifecycle events
* allows loading visuals to be shown

---

### `pressed`

Boolean attribute.

When present, the button is treated as pressed/toggled.

```html
<x-button pressed>Bold</x-button>
```

Behavior:

* sets `aria-pressed="true"`
* enables pressed styling

When absent:

* `aria-pressed="false"`

---

### `type`

Enum attribute.

Allowed values:

* `button`
* `submit`
* `reset`

Default:

```text
button
```

Examples:

```html
<x-button type="button">Open</x-button>
<x-button type="submit">Submit</x-button>
<x-button type="reset">Reset</x-button>
```

Invalid values normalize to `button`.

---

### `variant`

Enum attribute.

Allowed values:

* `primary`
* `secondary`
* `tertiary`
* `ghost`
* `danger`

Default:

```text
primary
```

Examples:

```html
<x-button variant="primary">Save</x-button>
<x-button variant="secondary">Cancel</x-button>
<x-button variant="tertiary">Learn more</x-button>
<x-button variant="ghost">More</x-button>
<x-button variant="danger">Delete</x-button>
```

Invalid values normalize to `primary`.

---

### `size`

Enum attribute.

Allowed values:

* `sm`
* `md`
* `lg`

Default:

```text
md
```

Examples:

```html
<x-button size="sm">Small</x-button>
<x-button size="md">Medium</x-button>
<x-button size="lg">Large</x-button>
```

Invalid values normalize to `md`.

---

### `label`

String attribute.

Used as the accessible name fallback when the default slot does not contain meaningful text.

```html
<x-button label="Close">
  <svg slot="icon-start" aria-hidden="true"></svg>
</x-button>
```

Behavior:

* if the default slot has meaningful text, `label` is not needed for naming
* if the default slot does not provide meaningful text, `label` is used as `aria-label`
* if neither visible text nor `label` is present, the component is accessible-name invalid

---

## Properties

These properties reflect boolean attributes.

### `disabled`

Type:

```text
boolean
```

Reflects:

```text
disabled
```

Example:

```js
const el = document.querySelector("x-button");
el.disabled = true;
```

---

### `loading`

Type:

```text
boolean
```

Reflects:

```text
loading
```

Example:

```js
const el = document.querySelector("x-button");
el.loading = true;
```

---

### `pressed`

Type:

```text
boolean
```

Reflects:

```text
pressed
```

Example:

```js
const el = document.querySelector("x-button");
el.pressed = false;
```

---

## Slots

### Default slot

Used for the button label/content.

```html
<x-button>Save changes</x-button>
```

---

### `icon-start`

Optional leading icon slot.

```html
<x-button>
  <svg slot="icon-start" aria-hidden="true"></svg>
  Download
</x-button>
```

---

### `icon-end`

Optional trailing icon slot.

```html
<x-button>
  Next
  <svg slot="icon-end" aria-hidden="true"></svg>
</x-button>
```

---

### `spinner`

Optional loading indicator slot.

```html
<x-button loading label="Saving">
  Save
  <span slot="spinner" aria-hidden="true"></span>
</x-button>
```

By default, spinner content is treated as decorative and should usually be marked `aria-hidden="true"` unless explicitly intended to be announced.

---

## Events

All custom events:

* bubble
* are composed
* are dispatched from the host `x-button` element

---

### `press`

Emitted when activation succeeds.

Detail shape:

```js
{ source: "pointer" | "keyboard" | "programmatic" }
```

Example:

```js
button.addEventListener("press", (event) => {
  console.log(event.detail.source);
});
```

Notes:

* not emitted when `disabled`
* not emitted when `loading`

---

### `press-start`

Emitted when a valid press interaction begins.

Detail shape:

```js
{ source: "pointer" | "keyboard" }
```

Example:

```js
button.addEventListener("press-start", (event) => {
  console.log(event.detail.source);
});
```

---

### `press-end`

Emitted when a valid press interaction ends or is canceled.

Detail shape:

```js
{ source: "pointer" | "keyboard" }
```

Example:

```js
button.addEventListener("press-end", (event) => {
  console.log(event.detail.source);
});
```

---

### `hover-start`

Emitted when an interactive pointer enters the button.

Detail shape:

```js
{}
```

Example:

```js
button.addEventListener("hover-start", () => {
  console.log("hover start");
});
```

---

### `hover-end`

Emitted when an interactive pointer leaves the button.

Detail shape:

```js
{}
```

Example:

```js
button.addEventListener("hover-end", () => {
  console.log("hover end");
});
```

---

### `focus-visible`

Emitted when the internal native button enters visible keyboard focus state.

Detail shape:

```js
{}
```

Example:

```js
button.addEventListener("focus-visible", () => {
  console.log("keyboard focus visible");
});
```

---

## Accessibility

`x-button` uses a native internal `<button>` inside Shadow DOM.

This gives it:

* native button semantics
* native keyboard behavior
* native form behavior for `type=submit` and `type=reset`
* predictable disabled behavior
* proper focus participation

### Disabled behavior

When `disabled` is present:

* the internal button is disabled
* the control cannot be focused or activated through normal interaction
* press and hover lifecycle events are suppressed

### Loading behavior

When `loading` is present:

* the internal button is disabled
* `aria-busy="true"` is applied
* duplicate activation is blocked

### Pressed behavior

When `pressed` is present:

* `aria-pressed="true"` is set

When `pressed` is absent:

* `aria-pressed="false"` is set

### Accessible name rules

Preferred name sources:

1. meaningful text in the default slot
2. `label` attribute as fallback

Good:

```html
<x-button>Save</x-button>
```

Good:

```html
<x-button label="Close">
  <svg slot="icon-start" aria-hidden="true"></svg>
</x-button>
```

Invalid authoring:

```html
<x-button></x-button>
```

Invalid authoring with only decorative content:

```html
<x-button>
  <svg slot="icon-start" aria-hidden="true"></svg>
</x-button>
```

Unless `label` is provided.

### Keyboard activation

Because the internal control is a native button, it supports:

* `Enter`
* `Space`
* focus via keyboard navigation
* submit/reset behavior where applicable

### Focus indicator

The component includes a visible focus style for keyboard-visible focus.

### Spinner accessibility

The spinner region is decorative by default. Authors should avoid allowing spinner visuals to replace the accessible name.

Recommended:

```html
<x-button loading label="Saving">
  Saving
  <span slot="spinner" aria-hidden="true"></span>
</x-button>
```

---

## Styling

The component is styled via semantic CSS custom properties defined on the host.

You can override these variables from outside the component.

### CSS custom properties

#### Shape and layout

* `--x-button-radius`
* `--x-button-gap`
* `--x-button-padding-inline`
* `--x-button-height-sm`
* `--x-button-height-md`
* `--x-button-height-lg`
* `--x-button-font-size-sm`
* `--x-button-font-size-md`
* `--x-button-font-size-lg`
* `--x-button-icon-size-sm`
* `--x-button-icon-size-md`
* `--x-button-icon-size-lg`
* `--x-button-spinner-size`

#### Colors

* `--x-button-bg`
* `--x-button-bg-hover`
* `--x-button-bg-active`
* `--x-button-bg-disabled`
* `--x-button-fg`
* `--x-button-fg-disabled`
* `--x-button-border`
* `--x-button-border-hover`
* `--x-button-border-active`
* `--x-button-focus-ring`
* `--x-button-danger-bg`
* `--x-button-danger-fg`

#### Motion

* `--x-button-transition-duration`
* `--x-button-transition-easing`

---

## Shadow Parts

The following shadow parts are exposed:

* `button`
* `inner`
* `label`
* `icon-start`
* `icon-end`
* `spinner`

Example:

```css
x-button::part(button) {
  font-weight: 600;
}

x-button::part(label) {
  letter-spacing: 0.01em;
}
```

---

## Theming Example

```css
x-button.brand {
  --x-button-bg: #2563eb;
  --x-button-bg-hover: #1d4ed8;
  --x-button-bg-active: #1e40af;
  --x-button-focus-ring: #93c5fd;
}
```

```html
<x-button class="brand">Continue</x-button>
```

---

## Light / Dark Theme Behavior

The component provides default theme-aware values using `prefers-color-scheme`.

Host-level CSS variable overrides take precedence over defaults.

---

## Motion

The component uses minimal CSS-based motion for:

* hover state
* press state
* focus indication
* loading affordance

It respects:

```css
@media (prefers-reduced-motion: reduce)
```

In reduced motion environments, transitions are removed.

---

## Form Behavior

The internal native button honors the `type` attribute.

### `type="button"`

No form submission.

### `type="submit"`

Submits the nearest form.

### `type="reset"`

Resets the nearest form.

Example:

```html
<form>
  <input type="text" />
  <x-button type="submit">Submit</x-button>
  <x-button type="reset" variant="secondary">Reset</x-button>
</form>
```

---

## Normalization Rules

### `type`

Invalid or missing values normalize to:

```text
button
```

### `variant`

Invalid or missing values normalize to:

```text
primary
```

### `size`

Invalid or missing values normalize to:

```text
md
```

Examples:

```html
<x-button type="oops">Save</x-button>
<x-button variant="unknown">Save</x-button>
<x-button size="xl">Save</x-button>
```

These behave as if written:

```html
<x-button type="button" variant="primary" size="md">Save</x-button>
```

---

## Public API Metadata

The export namespace exposes:

```clojure
(def public-api
  {:tag-name model/tag-name
   :properties model/property-api
   :events model/event-schema
   :observed-attributes model/observed-attributes})
```

### Expected model definitions

```clojure
(def property-api
  {:disabled {:type 'boolean
              :reflects-attribute attr-disabled}
   :loading {:type 'boolean
             :reflects-attribute attr-loading}
   :pressed {:type 'boolean
             :reflects-attribute attr-pressed}})
```

```clojure
(def event-schema
  {event-press {:detail {:source 'string}}
   event-press-start {:detail {:source 'string}}
   event-press-end {:detail {:source 'string}}
   event-hover-start {:detail {}}
   event-hover-end {:detail {}}
   event-focus-visible {:detail {}}})
```

---

## Recommended File Layout

```text
src/
  app/
    components/
      x_button/
        model.cljs
        x_button.cljs
    exports/
      x_button.cljs

docs/
  x_button.md

test/
  app/
    components/
      x_button/
        model_test.cljs
        x_button_test.cljs

demo/
  x_button.html
```

---

## Testing Guidance

Recommended tests include:

* registration works
* boolean properties reflect to attributes
* invalid enums normalize to defaults
* `loading` disables the internal button
* `pressed` maps to `aria-pressed`
* `label` becomes `aria-label` when default slot lacks meaningful text
* `press` emits expected detail
* hover lifecycle emits correctly
* focus-visible event is dispatched for keyboard-visible focus

---

## Demo Example

```html
<x-button id="save-btn" variant="primary">Save</x-button>

<script>
  const btn = document.getElementById("save-btn");

  btn.addEventListener("press", (event) => {
    console.log("press", event.detail);
  });

  btn.addEventListener("press-start", (event) => {
    console.log("press-start", event.detail);
  });

  btn.addEventListener("press-end", (event) => {
    console.log("press-end", event.detail);
  });

  btn.addEventListener("hover-start", () => {
    console.log("hover-start");
  });

  btn.addEventListener("hover-end", () => {
    console.log("hover-end");
  });

  btn.addEventListener("focus-visible", () => {
    console.log("focus-visible");
  });
</script>
```

---

## Authoring Recommendations

### Prefer visible label text

Best:

```html
<x-button>Save</x-button>
```

### Use `label` for icon-only cases

Best:

```html
<x-button label="Close">
  <svg slot="icon-start" aria-hidden="true"></svg>
</x-button>
```

### Mark decorative icons as hidden

Recommended:

```html
<svg slot="icon-start" aria-hidden="true"></svg>
```

### Keep spinner decorative unless intentional

Recommended:

```html
<span slot="spinner" aria-hidden="true"></span>
```

### Avoid unlabeled controls

Avoid:

```html
<x-button></x-button>
```

---

## Non-Goals

`x-button` does not include:

* framework adapters
* internal async state management
* virtual DOM
* reactive runtime
* toggle-group coordination
* icon library integration
* imperative animation system

---

## Summary

`x-button` is a platform-native button component that provides:

* native button semantics
* accessible interaction
* boolean reflection for `disabled`, `loading`, and `pressed`
* normalized enums for `type`, `variant`, and `size`
* slot-based icon and spinner composition
* host-variable theming
* reduced-motion support
* stable exported metadata through `public-api`

It is intended to be a simple, robust, Closure Advanced safe foundation for action controls in a ClojureScript design system.
