# x-toaster

A fixed-position coordinator that manages a stack of [`x-toast`](./x-toast.md) notifications. Provides viewport placement, capacity management, and an imperative API for spawning toasts programmatically.

## Tag

```html
<x-toaster></x-toaster>
```

## Usage

### Declarative

Place `x-toast` elements as children of `x-toaster`. The toaster handles positioning, stacking, and coordinated dismissal.

```html
<x-toaster position="top-end">
  <x-toast type="success" heading="Saved" message="Your file was saved."></x-toast>
</x-toaster>
```

### Imperative (`toast()` method)

```js
const toaster = document.querySelector('x-toaster');
toaster.toast({
  type: 'success',
  heading: 'File saved',
  message: 'Your changes have been saved.',
  timeoutMs: 4000,
  showProgress: true,
});
```

## Observed Attributes

| Attribute | Type | Default | Description |
|---|---|---|---|
| `position` | enum | `"top-end"` | Viewport placement. See values below. |
| `max-toasts` | number | `5` | Maximum visible toasts. Oldest is evicted when exceeded. |
| `label` | string | `"Notifications"` | `aria-label` for the region landmark. |

### `position` values

| Value | Placement |
|---|---|
| `top-start` | Top-left (logical inline-start) |
| `top-center` | Top, horizontally centred |
| `top-end` | Top-right (logical inline-end) — **default** |
| `bottom-start` | Bottom-left |
| `bottom-center` | Bottom, horizontally centred |
| `bottom-end` | Bottom-right |

## Properties

| JS Property | Attribute | Type | Notes |
|---|---|---|---|
| `position` | `position` | string | Reflects attribute. |
| `maxToasts` | `max-toasts` | number | Reflects attribute. |
| `label` | `label` | string | Reflects attribute. |

## Public Method

### `toast(options) → HTMLElement`

Creates an `x-toast` element with the given options, appends it to the toaster, and returns the element reference.

```ts
toaster.toast(options: {
  type?:        'info' | 'success' | 'warning' | 'error';
  heading?:     string;
  message?:     string;
  icon?:        string;
  dismissible?: boolean;   // default: true (x-toast default)
  timeoutMs?:   number;
  showProgress?: boolean;
}) → HTMLElement
```

The returned element is a live `<x-toast>` node. You can call `.dismiss()` on it directly to programmatically remove it.

## Events

### `x-toaster-dismiss`

Fired when a child toast is about to be dismissed. Provides a chance to observe or veto dismissals.

| Property | Value |
|---|---|
| `bubbles` | `true` |
| `composed` | `true` |
| `cancelable` | `true` |

**Detail:**

| Field | Type | Notes |
|---|---|---|
| `type` | string | Toast type (`info`, `success`, `warning`, `error`) |
| `reason` | string | Dismissal reason (`"button"`, `"keyboard"`, `"timeout"`, `"api"`, etc.) |
| `heading` | string | Toast heading text |
| `message` | string | Toast message text |

**Cancellation:** Call `event.preventDefault()` to abort the dismissal. The toast stays visible.

```js
toaster.addEventListener('x-toaster-dismiss', e => {
  if (e.detail.reason === 'timeout') {
    e.preventDefault(); // keep this toast alive despite timeout
  }
});
```

## Shadow Parts

| Part | Element | Notes |
|---|---|---|
| *(none)* | — | Toaster shadow DOM contains only a `<slot>`. Use `::slotted(x-toast)` to style children. |

## CSS Custom Properties

| Property | Default | Description |
|---|---|---|
| `--x-toaster-inset` | `16px` | Distance from viewport edges |
| `--x-toaster-gap` | `8px` | Vertical gap between toasts |
| `--x-toaster-max-width` | `480px` | Max width of the overlay container |
| `--x-toaster-z-index` | `9000` | Stack order above page content |

## Stacking order

- **Top positions** (`top-*`): newest toast appears nearest the corner; older toasts are pushed toward the center.
- **Bottom positions** (`bottom-*`): newest toast appears nearest the corner; older toasts are pushed toward the center.

This is achieved via `flex-direction: column-reverse` (top) and `flex-direction: column` (bottom), with toasts always appended as the last DOM child.

## Accessibility

- The `x-toaster` host has `role="region"` and `aria-label` (configurable via `label`).
- Individual `x-toast` children carry their own `role="status"` or `role="alert"` announcements.
- `pointer-events: none` on the host ensures non-toast areas of the overlay don't block interaction with underlying content.

## Coordination with x-toast

`x-toast` is fully standalone. When placed inside `x-toaster`, the following protocol applies:

1. `x-toast` fires `x-toast-dismiss` (bubbles, composed) when dismissed by button, keyboard, or timeout.
2. `x-toaster` intercepts the event (reason ≠ `"toaster-remove"`), calls `preventDefault()`, and fires `x-toaster-dismiss`.
3. If `x-toaster-dismiss` is not prevented, `x-toaster` calls `toast.dismiss("toaster-remove")`.
4. `x-toast` receives the second dismiss call, fires another `x-toast-dismiss` (reason: `"toaster-remove"`), which `x-toaster` does **not** intercept → `x-toast` animates exit and removes itself.

## Examples

### Bottom-center toaster with custom gap

```html
<x-toaster position="bottom-center" style="--x-toaster-gap: 12px;">
</x-toaster>

<script>
  import '@vanelsas/baredom/x-toaster';
  import '@vanelsas/baredom/x-toast';

  const toaster = document.querySelector('x-toaster');
  toaster.toast({ type: 'info', message: 'Page loaded.' });
</script>
```

### Preventing specific dismissals

```js
toaster.addEventListener('x-toaster-dismiss', e => {
  if (e.detail.type === 'error') {
    e.preventDefault(); // errors must be manually dismissed
  }
});
```

### Programmatically dismissing a specific toast

```js
const ref = toaster.toast({ type: 'warning', message: 'Check your input.' });
// Later:
ref.dismiss('resolved');
```
