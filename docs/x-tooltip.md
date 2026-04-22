# x-tooltip

A lightweight, non-interactive overlay that displays supplementary text when the user hovers or focuses a trigger element. The trigger is any element placed in the default slot.

## Tag name

```html
<x-tooltip>...</x-tooltip>
```

## Observed attributes

| Attribute   | Type    | Default | Description |
|-------------|---------|---------|-------------|
| `text`      | string  | `""`    | Tooltip text content |
| `placement` | enum    | `"top"` | Position relative to trigger |
| `delay`     | number  | `400`   | Show delay in milliseconds (0-5000) |
| `disabled`  | boolean | `false` | Suppress tooltip display |
| `open`      | boolean | `false` | Force tooltip visible |

### Placement values

`top` | `bottom` | `left` | `right`

## Properties (camelCase, reflect attributes)

| Property    | Type    | Reflected attribute |
|-------------|---------|---------------------|
| `text`      | string  | `text` |
| `placement` | string  | `placement` |
| `delay`     | number  | `delay` |
| `disabled`  | boolean | `disabled` |
| `open`      | boolean | `open` |

## Public methods

| Method   | Description |
|----------|-------------|
| `show()` | Opens tooltip immediately (bypasses delay) |
| `hide()` | Closes tooltip immediately |

## Events

### `x-tooltip-show`

Fired after the tooltip becomes visible. Not cancelable.

| Property | Value |
|----------|-------|
| `bubbles` | `true` |
| `composed` | `true` |
| `cancelable` | `false` |
| `detail` | `{}` |

### `x-tooltip-hide`

Fired after the tooltip is hidden. Not cancelable.

| Property | Value |
|----------|-------|
| `bubbles` | `true` |
| `composed` | `true` |
| `cancelable` | `false` |
| `detail` | `{}` |

## Slots

| Slot      | Description |
|-----------|-------------|
| (default) | The element that triggers the tooltip |
| `content` | Rich tooltip body (used when `text` attribute is absent) |

## Shadow parts

| Part      | Description |
|-----------|-------------|
| `trigger` | Wrapper around the default slot |
| `panel`   | The tooltip panel container |
| `arrow`   | Arrow element pointing to trigger |
| `body`    | Content container inside panel |
| `text`    | Span holding the `text` attribute value |

## CSS custom properties

### Layout

| Property | Default | Description |
|----------|---------|-------------|
| `--x-tooltip-padding` | `var(--x-space-xs) var(--x-space-sm)` | Panel padding |
| `--x-tooltip-radius` | `var(--x-radius-md, 6px)` | Border radius |
| `--x-tooltip-max-width` | `min(200px, calc(100% - 2rem))` | Maximum width |
| `--x-tooltip-font-size` | `var(--x-font-size-xs, 0.75rem)` | Font size |
| `--x-tooltip-arrow-size` | `6px` | Arrow size |
| `--x-tooltip-offset` | `4px` | Gap between trigger and panel |
| `--x-tooltip-z` | `var(--x-z-dropdown, 1000)` | z-index |

### Colors

| Property | Default | Description |
|----------|---------|-------------|
| `--x-tooltip-bg` | `var(--x-color-bg, #ffffff)` | Panel background |
| `--x-tooltip-text` | `var(--x-color-text, #0f172a)` | Text color |
| `--x-tooltip-border` | `1px solid var(--x-color-border, #e2e8f0)` | Panel border |
| `--x-tooltip-shadow` | `var(--x-shadow-md)` | Box shadow |

### Motion

| Property | Default | Description |
|----------|---------|-------------|
| `--x-tooltip-transition-duration` | `var(--x-transition-duration, 150ms)` | Transition duration |
| `--x-tooltip-transition-easing` | `var(--x-transition-easing, ease)` | Transition easing |

## Accessibility

- Panel has `role="tooltip"` with a unique auto-generated `id`
- Slotted trigger element receives `aria-describedby` pointing to the panel `id`
- `Escape` key hides the tooltip
- `focusin` shows the tooltip immediately (no delay) for keyboard users — pointer users get the configured `delay` instead, so keyboard navigation always feels responsive
- Tooltip is non-interactive (`pointer-events: none`)

## Motion

- Panel fades in with a subtle directional slide (4px toward trigger)
- `@media (prefers-reduced-motion: reduce)` disables all transitions

## Show/hide behavior

- **pointerenter** on host: shows after configured `delay`
- **pointerleave** on host: hides immediately, cancels pending show timer
- **focusin** on host: shows immediately (bypasses delay)
- **focusout** on host: hides (only if focus moves outside host)
- **Escape key**: hides
- **disabled attribute**: blocks show; hides tooltip if currently open
- **open attribute**: directly controls visibility

## Usage examples

### Simple text tooltip

```html
<x-tooltip text="Save document">
  <button>Save</button>
</x-tooltip>
```

### Placement

```html
<x-tooltip text="Top" placement="top"><button>Top</button></x-tooltip>
<x-tooltip text="Bottom" placement="bottom"><button>Bottom</button></x-tooltip>
<x-tooltip text="Left" placement="left"><button>Left</button></x-tooltip>
<x-tooltip text="Right" placement="right"><button>Right</button></x-tooltip>
```

### Custom delay

```html
<x-tooltip text="Quick!" delay="0">
  <button>Instant</button>
</x-tooltip>

<x-tooltip text="Slow..." delay="1000">
  <button>1 second delay</button>
</x-tooltip>
```

### Disabled

```html
<x-tooltip text="You won't see this" disabled>
  <button>Disabled tooltip</button>
</x-tooltip>
```

```js
const tip = document.querySelector('x-tooltip');
tip.disabled = true;   // suppress tooltip
tip.disabled = false;  // re-enable
```

### Rich content via slot

```html
<x-tooltip>
  <button>Info</button>
  <div slot="content">
    <strong>Shortcut:</strong> Ctrl+S
  </div>
</x-tooltip>
```

### Programmatic control

```html
<x-tooltip text="Always visible" open>
  <span>Pinned tooltip</span>
</x-tooltip>
```

```js
const tip = document.querySelector('x-tooltip');
tip.show();  // immediate open
tip.hide();  // immediate close
```
