# x-notification-center

A fixed-position stack that manages a collection of `x-alert` notifications, exposing a programmatic `push()` / `clear()` API.

> **Dependency**: `x-alert` must be registered (via its own `init()`) before calling `push()`.

---

## Tag

```html
<x-notification-center></x-notification-center>
```

---

## Attributes

| Attribute  | Type   | Default      | Notes |
|------------|--------|--------------|-------|
| `position` | string | `"top-right"` | One of: `top-right`, `top-left`, `bottom-right`, `bottom-left`, `top-center`, `bottom-center` |
| `max`      | number | `5`          | Maximum concurrent alerts; new pushes are silently ignored when full |

---

## Properties

| Property   | Type   | Default      | Notes |
|------------|--------|--------------|-------|
| `position` | string | `"top-right"` | Reflects `position` attribute |
| `max`      | number | `5`          | Reflects `max` attribute |
| `count`    | number | (read-only)  | Live count of current `x-alert` children |

---

## Methods

| Method  | Signature | Notes |
|---------|-----------|-------|
| `push`  | `push(options)` → `string \| undefined` | Creates and appends an `x-alert`; returns the notification ID string, or `undefined` when `count >= max` |
| `clear` | `clear()` | Immediately removes all current `x-alert` elements (bypasses exit animation) |

### push(options)

Options object fields (all optional):

| Field         | Type    | Notes |
|---------------|---------|-------|
| `id`          | string  | Custom notification ID; auto-generated if omitted |
| `type`        | string  | `"info"`, `"success"`, `"warning"`, `"error"` |
| `text`        | string  | Alert message text |
| `icon`        | string  | Icon glyph or `"none"` to hide the icon |
| `dismissible` | boolean | Pass `false` to make the alert non-dismissible |
| `timeoutMs`   | number  | Auto-dismiss after this many milliseconds |

---

## Events

All events bubble and are composed. None are cancelable.

| Event name | `detail` | Fired when |
|-----------|----------|-----------|
| `x-notification-center-push` | `{ id: string, count: number }` | An alert is pushed |
| `x-notification-center-dismiss` | `{ id: string, type: string, reason: string, text: string, count: number }` | An `x-alert-dismiss` event is received (forwarded) |
| `x-notification-center-empty` | `{}` | The count reaches 0 after a dismiss |

---

## CSS Custom Properties

| Property | Default | Notes |
|----------|---------|-------|
| `--x-notification-center-width` | `360px` | Width of the alert stack |
| `--x-notification-center-gap` | `8px` | Gap between alerts |
| `--x-notification-center-offset-x` | `16px` | Horizontal inset from the viewport edge |
| `--x-notification-center-offset-y` | `16px` | Vertical inset from the viewport edge |
| `--x-notification-center-z-index` | `9999` | Stack z-index |

---

## Parts

| Part | Element | Notes |
|------|---------|-------|
| `container` | `<div>` | Flex column containing pushed alerts; `column-reverse` for bottom positions |

---

## Notification Identification

Each pushed `x-alert` receives a `data-notification-id` attribute containing its ID (auto-generated or the `id` passed to `push()`). Use this to query or target a specific notification:

```js
const alert = nc.shadowRoot.querySelector('[data-notification-id="my-id"]');
```

---

## Shadow DOM Structure

```
#shadow-root (open)
  <style>
  <div part="container">
    <!-- x-alert elements appended here programmatically -->
  </div>
```

---

## Accessibility

- The notification container has `role="log"` and `aria-live="polite"`, forming a live region so screen readers announce new notifications as they appear.
- Individual `x-alert` elements use `role="alert"` (assertive) for `warning`/`error` types and `role="status"` (polite) for `info`/`success`.

---

## Usage Examples

### Basic push

```js
import '@vanelsas/baredom/x-alert';
import '@vanelsas/baredom/x-notification-center';

const nc = document.querySelector('x-notification-center');

nc.push({ type: 'success', text: 'File saved.' });
nc.push({ type: 'error',   text: 'Network error. Please retry.' });
```

### Auto-dismiss

```js
nc.push({ type: 'info', text: 'Session expires soon.', timeoutMs: 5000 });
```

### Non-dismissible

```js
nc.push({ type: 'warning', text: 'Read-only mode.', dismissible: false });
```

### Clear all

```js
nc.clear();
```

### Listening to events

```js
nc.addEventListener('x-notification-center-push', e => {
  console.log('pushed', e.detail.id, 'count:', e.detail.count);
});

nc.addEventListener('x-notification-center-dismiss', e => {
  const { id, type, reason, text, count } = e.detail;
  console.log(`dismissed ${id} (${type}) via ${reason} — "${text}". Remaining: ${count}`);
});

nc.addEventListener('x-notification-center-empty', () => {
  console.log('All notifications cleared');
});
```

### Position

```html
<x-notification-center position="bottom-right"></x-notification-center>
```

```js
nc.position = 'bottom-center';
```

### Custom max

```html
<x-notification-center max="3"></x-notification-center>
```
