# x-radio

A form-associated native Web Component that renders a radio button. Radios with the same `name` attribute form a mutually exclusive group: selecting one automatically deselects all others. Keyboard navigation follows the roving-tabindex pattern — Arrow keys move focus and selection within the group.

## Tag name

```html
<x-radio name="color" value="red">Red</x-radio>
```

## Observed attributes

| Attribute | Type | Default | Description |
|---|---|---|---|
| `checked` | boolean presence | absent | Selected state |
| `disabled` | boolean presence | absent | Disables interaction |
| `readonly` | boolean presence | absent | Prevents selection; does not affect form value |
| `required` | boolean presence | absent | At least one in the group must be checked |
| `name` | string | — | Groups radios together (same `name` = same group) |
| `value` | string | `"on"` | Value submitted with the form when checked |
| `aria-label` | string | — | Accessible label (use when no visible label is present) |
| `aria-describedby` | string | — | ID of description element |
| `aria-labelledby` | string | — | ID of label element |

## JS properties

All properties reflect to their corresponding attributes.

| Property | Type | Reflects |
|---|---|---|
| `checked` | boolean | `checked` |
| `disabled` | boolean | `disabled` |
| `readOnly` | boolean | `readonly` |
| `required` | boolean | `required` |
| `name` | string | `name` |
| `value` | string | `value` |

## Events

### `x-radio-change-request`

Fired before the state change. Cancelable — call `event.preventDefault()` to abort.

```js
el.addEventListener('x-radio-change-request', (e) => {
  console.log(e.detail.value);           // string
  console.log(e.detail.previousChecked); // boolean
  console.log(e.detail.nextChecked);     // boolean
  // e.preventDefault() to cancel
});
```

### `x-radio-change`

Fired after the state change. Not cancelable.

```js
el.addEventListener('x-radio-change', (e) => {
  console.log(e.detail.value);   // string
  console.log(e.detail.checked); // boolean (always true)
});
```

## Shadow DOM structure

```
<style>…</style>
<button part="control" role="radio" tabindex="0|−1">
  <span part="dot"></span>
</button>
```

## CSS custom properties

| Property | Default (light) | Description |
|---|---|---|
| `--x-radio-size` | `16px` | Width and height of the radio circle |
| `--x-radio-border-width` | `2px` | Border thickness |
| `--x-radio-border-color` | `#6b7280` | Border color (unchecked) |
| `--x-radio-bg` | `#ffffff` | Background color |
| `--x-radio-checked-color` | `#2563eb` | Border and dot color when checked |
| `--x-radio-dot-size` | `6px` | Inner dot diameter |
| `--x-radio-focus-ring` | `#60a5fa` | Focus ring color |
| `--x-radio-disabled-opacity` | `0.45` | Opacity when disabled |
| `--x-radio-transition` | `background 120ms ease, border-color 120ms ease` | Transition for state changes |

Dark mode overrides are applied automatically via `@media (prefers-color-scheme: dark)`.
Animations are suppressed when `@media (prefers-reduced-motion: reduce)` is active.

## CSS parts

| Part | Element | Description |
|---|---|---|
| `control` | `<button>` | The circular radio control |
| `dot` | `<span>` | The inner filled dot shown when checked |

## Accessibility

- `role="radio"` is set explicitly on `[part=control]`.
- `aria-checked` reflects the checked state (`"true"` / `"false"`).
- `aria-disabled`, `aria-required`, `aria-readonly` are kept in sync.
- Roving tabindex: the checked radio in a group receives `tabindex="0"`; all others receive `tabindex="-1"`. If no radio is checked, the first in DOM order receives `tabindex="0"`.
- Arrow keys move focus and selection within the group (wrapping).
- `Space` / `Enter` select the focused radio.
- The component is form-associated (`formAssociated = true`). It participates in form submission and `formReset`.

## Usage examples

### Basic group

```html
<x-radio name="size" value="small" aria-label="Small"></x-radio>
<x-radio name="size" value="medium" aria-label="Medium" checked></x-radio>
<x-radio name="size" value="large" aria-label="Large"></x-radio>
```

### Disabled

```html
<x-radio name="plan" value="free" disabled aria-label="Free"></x-radio>
<x-radio name="plan" value="pro" aria-label="Pro"></x-radio>
```

### Readonly

```html
<x-radio name="status" value="active" readonly checked aria-label="Active"></x-radio>
```

### Required

```html
<x-radio name="agree" value="yes" required aria-label="I agree"></x-radio>
```

### Custom styling

```html
<style>
  x-radio {
    --x-radio-checked-color: #7c3aed;
    --x-radio-dot-size: 8px;
    --x-radio-size: 20px;
  }
</style>
<x-radio name="color" value="purple" checked aria-label="Purple"></x-radio>
```

### JS interaction

```js
import '@vanelsas/baredom/x-radio';

const radios = document.querySelectorAll('x-radio[name="color"]');

radios.forEach(r => {
  r.addEventListener('x-radio-change', (e) => {
    console.log('selected:', e.detail.value);
  });
});
```
