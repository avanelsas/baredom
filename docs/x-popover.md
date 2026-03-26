# x-popover

A floating overlay panel anchored to an inline slotted trigger element. The trigger is any element placed in the `trigger` slot. The panel has a structured header (optional heading + close button), a default body slot, and an optional footer slot with an arrow indicator pointing toward the trigger.

## Tag name

```html
<x-popover>…</x-popover>
```

## Observed attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `open` | boolean | `false` | Visible state of the panel |
| `placement` | enum | `bottom-start` | Panel position relative to trigger |
| `heading` | string | `""` | Visible heading text in the panel header |
| `close-label` | string | `"Close"` | Accessible label for the close button |
| `no-close` | boolean | `false` | Hide the close button |
| `disabled` | boolean | `false` | Prevent opening/closing |

### Placement values

`bottom-start` · `bottom-end` · `top-start` · `top-end`

## Properties (camelCase, reflect attributes)

| Property | Type | Reflected attribute |
|----------|------|---------------------|
| `open` | boolean | `open` |
| `placement` | string | `placement` |
| `heading` | string | `heading` |
| `closeLabel` | string | `close-label` |
| `noClose` | boolean | `no-close` |
| `disabled` | boolean | `disabled` |

## Public methods

| Method | Description |
|--------|-------------|
| `show()` | Open the panel (source: `"programmatic"`); no-op if already open |
| `hide()` | Close the panel (source: `"programmatic"`); no-op if already closed |
| `toggle()` | Toggle open/close (source: `"programmatic"`) |

## Events

### `x-popover-toggle`

Fired before the open state changes. **Cancelable** — call `event.preventDefault()` to abort.

| Property | Type | Description |
|----------|------|-------------|
| `detail.open` | boolean | The state the panel is about to transition **to** |
| `detail.source` | string | What triggered the change |

Source values: `"pointer"` · `"programmatic"` · `"outside-click"` · `"escape"` · `"focusout"` · `"close-button"`

### `x-popover-change`

Fired after the open state has changed. **Not cancelable.**

| Property | Type | Description |
|----------|------|-------------|
| `detail.open` | boolean | The new open state |

## Slots

| Slot | Description |
|------|-------------|
| `trigger` | The trigger element. Click toggles the panel. Any focusable element is valid. |
| *(default)* | Main body content of the panel. |
| `footer` | Optional footer content. The footer is hidden via CSS when empty. |

## Shadow parts

| Part | Element |
|------|---------|
| `trigger` | `<span>` wrapping the trigger slot |
| `panel` | The floating panel `<div>` |
| `arrow` | The directional arrow indicator |
| `header` | The panel header row |
| `heading` | The heading `<span>` |
| `close-button` | The close `<button>` |
| `body` | The body `<div>` wrapping the default slot |
| `footer` | The footer `<div>` wrapping the footer slot |

## CSS custom properties

### Panel
| Property | Default | Description |
|----------|---------|-------------|
| `--x-popover-panel-bg` | `#ffffff` | Panel background |
| `--x-popover-panel-border` | `1px solid #e2e8f0` | Panel border |
| `--x-popover-panel-radius` | `8px` | Panel border radius |
| `--x-popover-panel-shadow` | `0 4px 16px rgba(0,0,0,0.12)` | Panel box shadow |
| `--x-popover-panel-min-width` | `12rem` | Minimum panel width |
| `--x-popover-panel-max-width` | `24rem` | Maximum panel width |
| `--x-popover-panel-max-height` | `24rem` | Maximum panel height (scrollable) |
| `--x-popover-panel-offset` | `4px` | Gap between trigger and arrow |
| `--x-popover-panel-z` | `1000` | z-index |

### Header
| Property | Default | Description |
|----------|---------|-------------|
| `--x-popover-header-padding` | `0.625rem 0.75rem 0.625rem 0.875rem` | Header padding |
| `--x-popover-header-border` | `1px solid #e2e8f0` | Header bottom border |

### Heading
| Property | Default | Description |
|----------|---------|-------------|
| `--x-popover-heading-color` | `#0f172a` | Heading text color |
| `--x-popover-heading-font-size` | `0.9375rem` | Heading font size |
| `--x-popover-heading-font-weight` | `600` | Heading font weight |

### Close button
| Property | Default | Description |
|----------|---------|-------------|
| `--x-popover-close-bg` | `transparent` | Close button background |
| `--x-popover-close-bg-hover` | `#f1f5f9` | Close button background on hover |
| `--x-popover-close-color` | `#64748b` | Close button icon color |
| `--x-popover-close-color-hover` | `#0f172a` | Close button icon color on hover |
| `--x-popover-close-radius` | `4px` | Close button border radius |
| `--x-popover-close-size` | `1.5rem` | Close button width and height |
| `--x-popover-focus-ring` | `#60a5fa` | Focus ring color |

### Body
| Property | Default | Description |
|----------|---------|-------------|
| `--x-popover-body-padding` | `0.875rem` | Body padding |
| `--x-popover-body-color` | `#334155` | Body text color |
| `--x-popover-body-font-size` | `0.9375rem` | Body font size |

### Footer
| Property | Default | Description |
|----------|---------|-------------|
| `--x-popover-footer-padding` | `0.625rem 0.875rem` | Footer padding |
| `--x-popover-footer-border` | `1px solid #e2e8f0` | Footer top border |

### Arrow
| Property | Default | Description |
|----------|---------|-------------|
| `--x-popover-arrow-size` | `8px` | Arrow square size |
| `--x-popover-arrow-bg` | `#ffffff` | Arrow background (should match panel bg) |
| `--x-popover-arrow-border` | `#e2e8f0` | Arrow border color (should match panel border) |

### Animation
| Property | Default | Description |
|----------|---------|-------------|
| `--x-popover-transition-duration` | `150ms` | Open/close transition duration |
| `--x-popover-transition-easing` | `ease` | Open/close transition easing |

## Accessibility

- The panel has `role="dialog"` and `aria-hidden` toggled between `"true"` (closed) and `"false"` (open).
- When `heading` is non-empty, `aria-labelledby="popover-heading"` is set on the panel.
- The close button has `aria-label` set to the `close-label` attribute value (default `"Close"`).
- `Escape` closes the panel when it is open.
- Focus leaving the host (focusout) closes the panel.
- The arrow is `aria-hidden="true"`.

## Dark mode

All color custom properties respond to `@media (prefers-color-scheme: dark)` via default values defined in `:host`. Override these defaults to customize dark theme.

## Reduced motion

When `@media (prefers-reduced-motion: reduce)` is active, panel and close button transitions are disabled (`transition: none !important`).

## Usage examples

### Basic usage

```html
<x-popover heading="Details">
  <button slot="trigger">Open</button>
  <p>This is the popover body content.</p>
</x-popover>
```

### With footer slot

```html
<x-popover heading="Confirm action">
  <button slot="trigger">Delete item</button>
  <p>This action cannot be undone.</p>
  <div slot="footer">
    <button>Cancel</button>
    <button>Confirm</button>
  </div>
</x-popover>
```

### Without close button

```html
<x-popover heading="Info" no-close>
  <button slot="trigger">?</button>
  <p>Some informational content.</p>
</x-popover>
```

### Custom close label

```html
<x-popover heading="Settings" close-label="Dismiss settings">
  <button slot="trigger">⚙</button>
  <p>Settings content here.</p>
</x-popover>
```

### Placement variants

```html
<x-popover placement="top-start">…</x-popover>
<x-popover placement="top-end">…</x-popover>
<x-popover placement="bottom-start">…</x-popover>
<x-popover placement="bottom-end">…</x-popover>
```

### Programmatic control

```js
const el = document.querySelector('x-popover');
el.show();
el.hide();
el.toggle();
```

### Preventing toggle

```js
el.addEventListener('x-popover-toggle', e => {
  if (!confirmed) e.preventDefault();
});
```

### Custom theme

```html
<x-popover
  style="
    --x-popover-panel-bg: #1e1b4b;
    --x-popover-panel-border: 1px solid #4338ca;
    --x-popover-heading-color: #e0e7ff;
    --x-popover-body-color: #c7d2fe;
    --x-popover-arrow-bg: #1e1b4b;
    --x-popover-arrow-border: #4338ca;
    --x-popover-close-color: #818cf8;
    --x-popover-close-bg-hover: rgba(255,255,255,0.08);
  "
  heading="Custom theme"
>
  <button slot="trigger">Open</button>
  <p>Themed popover content.</p>
</x-popover>
```
