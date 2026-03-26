# x-toast

A floating, ephemeral notification card that self-dismisses after a configurable delay. Distinct from `x-alert` (inline banner) — `x-toast` uses an elevated card style with box-shadow and supports a heading, optional countdown progress bar, and a programmatic `dismiss()` method.

Designed to work standalone or as a child managed by a future `x-toaster` coordinator.

---

## Tag

```html
<x-toast type="success" heading="Saved" message="Your changes were saved."></x-toast>
```

---

## Attributes

| Attribute | Type | Default | Description |
|---|---|---|---|
| `type` | `"info"` \| `"success"` \| `"warning"` \| `"error"` | `"info"` | Semantic variant |
| `heading` | string | `""` | Optional bold title. Hidden when empty. |
| `message` | string | `""` | Body text of the toast. |
| `icon` | string \| `"none"` | — | Absent = default type glyph; `"none"` = hidden icon |
| `dismissible` | boolean† | `true` | Shows the dismiss (×) button |
| `disabled` | boolean | `false` | Dims the toast and disables the dismiss button |
| `timeout-ms` | number | — | Auto-dismiss delay in milliseconds (positive integer) |
| `show-progress` | boolean | `false` | Shows a countdown progress bar (requires `timeout-ms`) |

†`dismissible` uses default-true semantics: absent or any value except `"false"` = `true`.

---

## Properties

| Property | Type | Reflects | Notes |
|---|---|---|---|
| `type` | string | `type` | Returns `"info"` when absent |
| `heading` | string | `heading` | Returns `""` when absent |
| `message` | string | `message` | Returns `""` when absent |
| `icon` | string \| null | `icon` | Returns `null` when absent |
| `dismissible` | boolean | `dismissible` | Default-true |
| `disabled` | boolean | `disabled` | Boolean-present |
| `timeoutMs` | number \| null | `timeout-ms` | Returns `null` when absent/invalid |
| `showProgress` | boolean | `show-progress` | Default-false |

---

## Public Methods

### `dismiss(reason?)`

Programmatically dismiss the toast. Fires `x-toast-dismiss` with the given `reason` (defaults to `"api"`). Bypasses the `disabled` and `dismissible` guards — intended for programmatic control (e.g. from `x-toaster`).

Calling `event.preventDefault()` inside the `x-toast-dismiss` listener will keep the toast in the DOM.

```js
toast.dismiss();                 // reason: "api"
toast.dismiss("toaster-remove"); // custom reason
```

---

## Events

### `x-toast-dismiss`

Fired when the toast is about to be dismissed and removed from the DOM.

| Property | Value |
|---|---|
| `bubbles` | `true` |
| `composed` | `true` |
| `cancelable` | `true` |

**Detail shape:**

```js
{
  type:    "info" | "success" | "warning" | "error",
  reason:  "button" | "keyboard" | "timeout" | "api" | string,
  heading: string,
  message: string
}
```

**Reason values:**

| Reason | Trigger |
|---|---|
| `"button"` | User clicked the × dismiss button |
| `"keyboard"` | User pressed Escape while host was focused |
| `"timeout"` | Auto-dismiss timer fired |
| `"api"` | `dismiss()` called with no argument |
| custom string | `dismiss("my-reason")` |

**Cancelling dismissal:**

```js
toast.addEventListener('x-toast-dismiss', e => {
  e.preventDefault(); // toast stays in DOM
});
```

---

## Slots

| Slot | Description |
|---|---|
| `icon` | Custom icon element. When slotted content is present it takes precedence over both the `icon` attribute and the default type glyph. |

---

## Parts

| Part | Element | Description |
|---|---|---|
| `container` | `<div>` | Outer card wrapper (shadow, border, border-radius) |
| `inner` | `<div>` | Flex row: icon + body + dismiss button |
| `icon` | `<span>` | Icon area, `aria-hidden="true"` |
| `default-icon` | `<span>` | Fallback type glyph (inside icon slot) |
| `body` | `<div>` | Flex column: heading + message |
| `heading` | `<span>` | Optional bold title |
| `message` | `<span>` | Body text |
| `dismiss` | `<button>` | × dismiss button |
| `progress` | `<div>` | Progress bar track container |
| `progress-bar` | `<div>` | Animated countdown fill bar |

---

## CSS Custom Properties

### Layout

| Property | Default | Description |
|---|---|---|
| `--x-toast-radius` | `12px` | Card border radius |
| `--x-toast-padding-y` | `14px` | Vertical padding |
| `--x-toast-padding-x` | `16px` | Horizontal padding |
| `--x-toast-gap` | `12px` | Gap between icon, body, dismiss |
| `--x-toast-font-size` | `0.875rem` | Message font size |
| `--x-toast-heading-font-size` | `0.9375rem` | Heading font size |
| `--x-toast-heading-weight` | `600` | Heading font weight |
| `--x-toast-min-width` | `280px` | Minimum card width |
| `--x-toast-max-width` | `480px` | Maximum card width |
| `--x-toast-border-width` | `1px` | Border width |

### Elevation

| Property | Default | Description |
|---|---|---|
| `--x-toast-shadow` | `0 4px 16px rgba(0,0,0,0.12), 0 1px 4px rgba(0,0,0,0.08)` | Card box-shadow (dark mode: stronger) |

### Motion

| Property | Default | Description |
|---|---|---|
| `--x-toast-enter-duration` | `200ms` | Enter slide-in animation duration |
| `--x-toast-exit-duration` | `180ms` | Exit slide-out animation duration |
| `--x-toast-motion-fast` | `120ms` | Transition speed for micro-interactions |
| `--x-toast-motion-ease` | `cubic-bezier(0.2,0,0,1)` | Easing function |

### Progress bar

| Property | Default | Description |
|---|---|---|
| `--x-toast-progress-height` | `3px` | Bar height |
| `--x-toast-progress-bg` | `rgba(0,0,0,0.08)` | Track background colour |

### Semantic colour tokens (light mode)

| Property | Default |
|---|---|
| `--x-toast-info-bg` | `#ffffff` |
| `--x-toast-info-border` | `rgba(0,102,204,0.30)` |
| `--x-toast-info-color` | `rgba(15,23,42,0.92)` |
| `--x-toast-info-icon-color` | `rgba(0,102,204,0.85)` |
| `--x-toast-info-progress-fill` | `rgba(0,102,204,0.70)` |
| `--x-toast-success-*` | (green palette) |
| `--x-toast-warning-*` | (amber palette) |
| `--x-toast-error-*` | (red palette) |

Dark mode overrides set via `@media (prefers-color-scheme: dark)` — card surface becomes `#1e293b` with adjusted colour tokens.

### Resolved tokens (set by type, do not override directly)

| Property | Description |
|---|---|
| `--x-toast-bg` | Resolved card background |
| `--x-toast-border-color` | Resolved border colour |
| `--x-toast-color` | Resolved text colour |
| `--x-toast-icon-color` | Resolved icon colour |
| `--x-toast-progress-fill` | Resolved progress bar fill |

---

## Accessibility

- Host has `role` managed via `aria-live` on the container (`role="status"` for info/success, `role="alert"` for warning/error)
- `tabIndex="0"` when dismissible and not disabled; `tabIndex="-1"` otherwise
- `aria-disabled="true"` when disabled
- `aria-keyshortcuts="Escape"` when dismissible and not disabled
- Dismiss button has `aria-label="Dismiss toast"`
- Icon area is `aria-hidden="true"`

---

## Keyboard interaction

| Key | Condition | Action |
|---|---|---|
| `Escape` | Host focused, dismissible, not disabled | Fires `x-toast-dismiss` with `reason="keyboard"` |

---

## Animation

- **Enter:** slides down from `translateY(-8px)` with fade-in; duration controlled by `--x-toast-enter-duration`
- **Exit:** slides up to `translateY(-8px)` with fade-out; duration controlled by `--x-toast-exit-duration`
- **Progress bar:** CSS animation `width: 100% → 0%` over `timeout-ms` duration; starts after the enter animation completes
- All animations disabled when `@media (prefers-reduced-motion: reduce)` is active

---

## x-toaster coordination

`x-toast` is designed to work inside a future `x-toaster` coordinator with zero coupling. The coordination works entirely through the bubbled `x-toast-dismiss` event:

1. `x-toast-dismiss` has `bubbles: true` and `composed: true`, so it crosses shadow boundaries
2. `x-toaster` listens for this event on itself
3. When `x-toaster` calls `event.preventDefault()`, the toast does **not** self-remove — `x-toaster` takes ownership of removal (e.g. to coordinate collapse animations)
4. `x-toaster` calls `toast.dismiss("toaster-remove")` when it is ready to remove the toast
5. `x-toaster`'s handler skips `preventDefault` when `reason === "toaster-remove"`, allowing the natural exit animation to proceed

`x-toast` works fully standalone without `x-toaster`.

---

## Examples

### Basic variants

```html
<x-toast type="info"    message="Update available."></x-toast>
<x-toast type="success" heading="Saved" message="Changes were saved."></x-toast>
<x-toast type="warning" message="Session expiring in 5 minutes."></x-toast>
<x-toast type="error"   heading="Failed" message="Could not save changes."></x-toast>
```

### Auto-dismiss with progress bar

```html
<x-toast
  type="success"
  message="File uploaded."
  timeout-ms="4000"
  show-progress>
</x-toast>
```

### Non-dismissible

```html
<x-toast type="info" message="Processing…" dismissible="false"></x-toast>
```

### Programmatic dismiss

```js
const toast = document.querySelector('x-toast');
toast.dismiss(); // fires x-toast-dismiss with reason "api"
```

### Listening to dismiss

```js
toast.addEventListener('x-toast-dismiss', e => {
  const { type, reason, heading, message } = e.detail;
  console.log(`Toast dismissed: type=${type} reason=${reason}`);
});
```

### Custom icon via slot

```html
<x-toast message="Custom icon example">
  <svg slot="icon" ...></svg>
</x-toast>
```
