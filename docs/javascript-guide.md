# JavaScript Developer Guide

BareDOM is a library of 60+ native web components — no framework, no virtual DOM, no runtime dependency. Every component is a standard Custom Element that works anywhere HTML works: vanilla JavaScript, React, Vue, Svelte, Angular, or a static page.

All components are stateless. The rendering model is simple:

```
DOM = f(attributes, properties)
```

Set an attribute, the DOM updates. Remove it, the DOM updates back. There are no hidden reactive systems to manage.

BareDOM is authored in ClojureScript but compiles to standard ES modules. As a JavaScript consumer you never touch ClojureScript — you import plain `.js` files.

Browse all components in the [live demo](https://avanelsas.github.io/baredom/), including modern, visual components like `<x-ripple-effect>`, `<x-scroll-parallax>`, `<x-scroll-story>`, `<x-scroll-timeline>`, `<x-kinetic-typography>`, `<x-organic-shape>`, and `<x-gaussian-blur>`.

To learn more about why BareDOM was created, see the [project README](../README.md#why-baredom).

---

## Installation

### Option A — npm

```bash
npm install @vanelsas/baredom
```

Then import and register only the components you need:

```js
import { init as initButton } from '@vanelsas/baredom/x-button';
import { init as initAlert }  from '@vanelsas/baredom/x-alert';
import { init as initModal }  from '@vanelsas/baredom/x-modal';

initButton();
initAlert();
initModal();
```

Registration is idempotent — calling `init()` on an already-registered component is a no-op.

### Option B — Vanilla HTML (no build tool)

Copy the `dist/` folder from a release or after running `npm run build`, then load components directly:

```html
<script type="module">
  import { init as initButton } from './dist/x-button.js';
  import { init as initAlert }  from './dist/x-alert.js';

  initButton();
  initAlert();
</script>
```

Each component is a separate ES module. The browser loads only the files you import plus the shared `base.js` runtime.

---

## Using Components

### HTML attributes

Components are plain HTML elements. Configure them with attributes:

```html
<x-button variant="primary" size="lg">Save changes</x-button>

<x-alert type="success" text="Changes saved." timeout-ms="4000"></x-alert>

<x-modal id="my-modal" size="md" label="Confirm">
  <div slot="header">Confirm</div>
  <p>Are you sure?</p>
  <div slot="footer">
    <x-button variant="secondary" onclick="document.getElementById('my-modal').hide()">Cancel</x-button>
    <x-button variant="primary" onclick="handleConfirm()">Confirm</x-button>
  </div>
</x-modal>
```

Boolean attributes follow HTML conventions — presence means `true`, absence means `false`:

```html
<x-button disabled>Can't click me</x-button>
<x-button loading>Saving...</x-button>
<x-checkbox checked></x-checkbox>
```

### JavaScript properties

You can also set attributes and state via JavaScript properties:

```js
const btn = document.querySelector('x-button');
btn.disabled = true;
btn.loading = true;

const modal = document.querySelector('x-modal');
modal.open = true;   // same as modal.show()
modal.size = 'lg';
```

### Methods

Some components expose methods:

```js
const modal = document.getElementById('my-modal');
modal.show();
modal.hide();
modal.toggle();
```

---

## Handling Events

Components dispatch custom events with a `detail` payload. Listen with standard `addEventListener`:

```js
// Button press event
const btn = document.querySelector('x-button');
btn.addEventListener('press', (e) => {
  console.log('pressed via:', e.detail.source); // "pointer" | "keyboard"
});

// Alert dismiss event (cancelable)
const alert = document.querySelector('x-alert');
alert.addEventListener('x-alert-dismiss', (e) => {
  console.log('dismissed by:', e.detail.reason); // "button" | "keyboard" | "timeout"
  // e.preventDefault() to keep the alert visible
});

// Modal events
const modal = document.querySelector('x-modal');
modal.addEventListener('x-modal-toggle', (e) => {
  console.log('modal open:', e.detail.open);
});
modal.addEventListener('x-modal-dismiss', (e) => {
  console.log('dismissed via:', e.detail.reason); // "escape" | "backdrop"
});

// Tabs value change
const tabs = document.querySelector('x-tabs');
tabs.addEventListener('value-change', (e) => {
  console.log('active tab:', e.detail.value);
});
```

### Creating components dynamically

```js
const alert = document.createElement('x-alert');
alert.setAttribute('type', 'success');
alert.setAttribute('text', 'Upload complete.');
alert.setAttribute('timeout-ms', '3000');
document.body.appendChild(alert);
```

---

## Theming

Every visual detail is exposed as a CSS custom property following the pattern `--x-<component>-<property>`. Override at any scope:

```css
/* Global overrides */
:root {
  --x-button-radius: 4px;
  --x-alert-radius: 8px;
}

/* Per-instance */
.brand-button {
  --x-button-bg: #2563eb;
  --x-button-bg-hover: #1d4ed8;
  --x-button-fg: white;
}
```

```html
<x-button class="brand-button">Continue</x-button>
```

All components support light and dark mode automatically via `prefers-color-scheme`. No JavaScript or class toggling required.

### Shadow parts

Components expose `::part()` targets for deeper styling:

```css
x-button::part(button) {
  font-weight: 600;
}

x-modal::part(backdrop) {
  backdrop-filter: blur(4px);
}
```

---

## Framework Integration

Since BareDOM components are native HTML elements, they work in any framework. Just import and call `init()` once at application startup.

### React

```jsx
import { init as initButton } from '@vanelsas/baredom/x-button';

// Register once at app startup
initButton();

function App() {
  return (
    <x-button variant="primary" onClick={(e) => console.log('clicked')}>
      Save
    </x-button>
  );
}
```

> **Note:** React synthetic events work for standard events like `click`. For custom events (`press`, `x-alert-dismiss`, etc.), use a `ref` with `addEventListener`.

### Vue

```vue
<script setup>
import { init as initAlert } from '@vanelsas/baredom/x-alert';
initAlert();
</script>

<template>
  <x-alert type="success" text="Saved!" @x-alert-dismiss="onDismiss" />
</template>
```

### Svelte

```svelte
<script>
  import { init as initModal } from '@vanelsas/baredom/x-modal';
  initModal();
</script>

<x-modal id="m" label="Confirm" on:x-modal-dismiss={handleDismiss}>
  <div slot="header">Confirm</div>
  <p>Are you sure?</p>
</x-modal>
```

---

## Component Documentation

Each component has its own documentation page with full attribute, property, event, slot, and CSS custom property reference:

- [docs/x-button.md](./x-button.md), [docs/x-alert.md](./x-alert.md), [docs/x-modal.md](./x-modal.md), etc.

See the [full component list](../README.md#components) in the project README.

---

## Browser Support

| Browser | Minimum version |
|---------|----------------|
| Chrome / Edge | 67+ |
| Firefox | 63+ |
| Safari | 14+ |

No polyfills required.
