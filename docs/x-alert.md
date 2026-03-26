# x-alert

A themeable, accessible alert banner that supports four semantic variants, an optional dismiss button, auto-dismiss via timeout, enter/exit animations, and full keyboard interaction.

---

## Tag

```html
<x-alert></x-alert>
```

---

## Attributes

| Attribute      | Type             | Default | Description                                        |
|----------------|------------------|---------|----------------------------------------------------|
| `type`         | enum             | `"info"` | Semantic variant: `info` `success` `warning` `error` |
| `text`         | string           | `""`    | Alert message text                                 |
| `icon`         | string \| `"none"` | —     | Custom icon glyph. Absent = default glyph; `"none"` = hidden |
| `dismissible`  | boolean†         | `true`  | Whether the dismiss button is shown                |
| `disabled`     | boolean          | `false` | Dims the alert and disables the dismiss button     |
| `timeout-ms`   | number           | —       | Auto-dismiss delay in milliseconds (positive int)  |

†`dismissible` uses default-true semantics: absent or empty = `true`; `dismissible="false"` = `false`.

---

## Properties

| Property      | Type           | Reflects attribute |
|---------------|----------------|--------------------|
| `type`        | string         | `type`             |
| `text`        | string         | `text`             |
| `icon`        | string \| null | `icon`             |
| `dismissible` | boolean        | `dismissible`      |
| `disabled`    | boolean        | `disabled`         |
| `timeoutMs`   | number \| null | `timeout-ms`       |

---

## Events

| Event             | Bubbles | Composed | Cancelable | Detail                                     |
|-------------------|---------|----------|------------|--------------------------------------------|
| `x-alert-dismiss` | yes     | yes      | **yes**    | `{ type, reason, text }` |

`detail.reason` is `"button"` (user click or Escape key) or `"timeout"` (auto-dismiss).

Calling `event.preventDefault()` cancels the removal — the alert stays in the DOM.

---

## Slots

| Slot   | Description                                      |
|--------|--------------------------------------------------|
| `icon` | Custom icon element (overrides default glyph and attribute icon) |

```html
<x-alert type="success" text="Saved">
  <svg slot="icon" aria-hidden="true">...</svg>
</x-alert>
```

---

## Parts

| Part           | Description                      |
|----------------|----------------------------------|
| `container`    | Outer flex wrapper               |
| `icon`         | Icon area `<span>`               |
| `default-icon` | Fallback glyph `<span>`          |
| `text`         | Message text `<span>`            |
| `dismiss`      | Dismiss `<button>`               |

---

## CSS Custom Properties

### Layout & typography

| Variable                  | Default    | Description           |
|---------------------------|------------|-----------------------|
| `--x-alert-radius`        | `10px`     | Border radius         |
| `--x-alert-padding-y`     | `10px`     | Vertical padding      |
| `--x-alert-padding-x`     | `12px`     | Horizontal padding    |
| `--x-alert-gap`           | `10px`     | Gap between icon, text, button |
| `--x-alert-font-size`     | `0.875rem` | Text size             |

### Motion

| Variable                    | Default | Description               |
|-----------------------------|---------|---------------------------|
| `--x-alert-enter-duration`  | `140ms` | Enter animation duration  |
| `--x-alert-exit-duration`   | `160ms` | Exit animation duration   |
| `--x-alert-motion-fast`     | `120ms` | Transition speed          |
| `--x-alert-motion-ease`     | cubic-bezier | Easing function      |
| `--x-alert-press-scale`     | `0.98`  | Dismiss button press scale |

### Semantic colour tokens (light)

| Variable                    | Default (light)                  |
|-----------------------------|----------------------------------|
| `--x-alert-info-bg`         | `rgba(0,102,204,0.08)`           |
| `--x-alert-info-border`     | `rgba(0,102,204,0.35)`           |
| `--x-alert-info-color`      | `rgba(0,60,120,0.95)`            |
| `--x-alert-success-bg`      | `rgba(16,140,72,0.10)`           |
| `--x-alert-success-border`  | `rgba(16,140,72,0.35)`           |
| `--x-alert-success-color`   | `rgba(10,90,46,0.95)`            |
| `--x-alert-warning-bg`      | `rgba(204,120,0,0.12)`           |
| `--x-alert-warning-border`  | `rgba(204,120,0,0.45)`           |
| `--x-alert-warning-color`   | `rgba(120,70,0,0.95)`            |
| `--x-alert-error-bg`        | `rgba(190,20,40,0.10)`           |
| `--x-alert-error-border`    | `rgba(190,20,40,0.45)`           |
| `--x-alert-error-color`     | `rgba(120,10,20,0.95)`           |

Dark-mode variants are set automatically via `@media (prefers-color-scheme: dark)`.

---

## Accessibility

- `[part=container]` carries `role="status"` (info/success) or `role="alert"` (warning/error).
- The host element receives `tabindex="0"` and `aria-keyshortcuts="Escape"` when dismissible and not disabled.
- The dismiss button carries `aria-label="Dismiss alert"`.
- When `disabled`, the host receives `aria-disabled="true"` and `tabindex="-1"`.
- Animations respect `@media (prefers-reduced-motion: reduce)`.

---

## Keyboard

| Key      | Condition                   | Action  |
|----------|-----------------------------|---------|
| `Escape` | Focused, dismissible, not disabled | Dismisses the alert |

---

## Icon behaviour

| `icon` attribute | `slot[name=icon]` content | Result                              |
|------------------|---------------------------|-------------------------------------|
| absent           | absent                    | Default glyph for `type`            |
| absent           | present                   | Slotted element shown               |
| `"none"`         | absent                    | Icon area hidden                    |
| `"none"`         | present                   | Slotted element shown (slot wins)   |
| custom string    | absent                    | Custom glyph shown                  |
| custom string    | present                   | Slotted element shown               |

---

## Auto-dismiss

Set `timeout-ms` to a positive integer. The alert dispatches `x-alert-dismiss` with `reason: "timeout"` after the delay. The event is cancelable — call `preventDefault()` to keep the alert visible.

```html
<x-alert type="success" text="Saved successfully" timeout-ms="4000"></x-alert>
```

The timer starts on `connectedCallback` and is cleared on `disconnectedCallback`. Changing `timeout-ms` while connected restarts the timer.

---

## Examples

### Basic variants

```html
<x-alert type="info"    text="Your session will expire in 5 minutes."></x-alert>
<x-alert type="success" text="Changes saved."></x-alert>
<x-alert type="warning" text="Low disk space."></x-alert>
<x-alert type="error"   text="Failed to save. Please try again."></x-alert>
```

### Non-dismissible

```html
<x-alert type="warning" text="Read-only mode." dismissible="false"></x-alert>
```

### Auto-dismiss

```html
<x-alert type="success" text="Upload complete." timeout-ms="3000"></x-alert>
```

### Custom icon via slot

```html
<x-alert type="info" text="New features are available.">
  <svg slot="icon" width="16" height="16" aria-hidden="true">...</svg>
</x-alert>
```

### Prevent dismiss programmatically

```js
document.querySelector('x-alert').addEventListener('x-alert-dismiss', e => {
  if (!confirmed) e.preventDefault();
});
```

### ClojureScript (hiccup renderer)

```clojure
[:x-alert {:type "success" :text "Profile updated."}]

[:x-alert {:type    "error"
           :text    "Payment failed."
           :on-x-alert-dismiss (fn [e]
                                 (swap! state/app assoc :show-alert false))}]

[:x-alert {:type       "info"
           :text       "Session expiring soon."
           :timeout-ms "5000"}]
```
