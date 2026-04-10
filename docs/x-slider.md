# x-slider

A native-input-backed, form-associated, accessible range slider web component.

## Tag name

```html
<x-slider></x-slider>
```

---

## Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | string (number) | `"0"` | Current value |
| `min` | string (number) | `"0"` | Minimum value |
| `max` | string (number) | `"100"` | Maximum value |
| `step` | string (number \| `"any"`) | `"1"` | Step increment. `"any"` disables stepping. |
| `disabled` | boolean (presence) | false | Disables all interaction |
| `readonly` | boolean (presence) | false | Blocks pointer and keyboard interaction; thumb remains focusable |
| `name` | string | — | Form field name |
| `label` | string | — | Visible label text above the slider |
| `show-value` | boolean (presence) | false | Show the current numeric value beside the label |
| `size` | `"sm"` \| `"md"` \| `"lg"` | `"md"` | Size variant |
| `aria-label` | string | — | Accessible label forwarded to `[part=input]` |
| `aria-labelledby` | string | — | Forwarded to `[part=input]` |
| `aria-describedby` | string | — | Forwarded to `[part=input]` |

When neither `aria-label` nor `aria-labelledby` is set, the `label` attribute value is used as the `aria-label` on the native input.

---

## Properties

All properties reflect to their corresponding attributes.

| Property | Type | Reflects attribute |
|----------|------|--------------------|
| `value` | string | `value` |
| `min` | string | `min` |
| `max` | string | `max` |
| `step` | string | `step` |
| `disabled` | boolean | `disabled` |
| `readOnly` | boolean | `readonly` |
| `showValue` | boolean | `show-value` |
| `name` | string | `name` |
| `label` | string | `label` |
| `size` | string | `size` |

---

## Events

| Event | Cancelable | Detail |
|-------|-----------|--------|
| `x-slider-input` | no | `{ value: number, min: number, max: number }` |
| `x-slider-change` | no | `{ value: number, min: number, max: number }` |

Both events bubble and are composed (cross shadow-DOM boundary).

- `x-slider-input` — fires continuously while the user drags.
- `x-slider-change` — fires once when the user commits a value (mouseup / touchend).

---

## Parts

| Part | Element | Description |
|------|---------|-------------|
| `base` | `<div>` | Outer wrapper; receives `data-size` |
| `header` | `<div>` | Row containing label and value text; hidden when neither is active |
| `label-text` | `<span>` | Label text |
| `value-text` | `<span>` | Numeric value display |
| `input` | `<input type="range">` | The native range input |

---

## CSS Custom Properties

| Property | Default (light) | Description |
|----------|----------------|-------------|
| `--x-slider-track-color` | `rgba(0,0,0,0.15)` | Unfilled track color |
| `--x-slider-fill-color` | `#3b82f6` | Filled portion color |
| `--x-slider-thumb-color` | `#ffffff` | Thumb background color |
| `--x-slider-thumb-border` | `2px solid #3b82f6` | Thumb border |
| `--x-slider-thumb-shadow` | `0 1px 4px rgba(0,0,0,0.20)` | Thumb drop shadow |
| `--x-slider-focus-ring` | `#60a5fa` | Focus ring color |
| `--x-slider-disabled-opacity` | `0.45` | Opacity when disabled |
| `--x-slider-label-color` | `rgba(0,0,0,0.60)` | Label text color |
| `--x-slider-value-color` | `rgba(0,0,0,0.50)` | Value text color |
| `--x-slider-radius` | `9999px` | Track and fill border-radius |

All properties have sensible dark-mode overrides applied automatically via `@media (prefers-color-scheme: dark)`.

### Size-driven internal variables

The `size` attribute sets CSS custom properties on `[part=base]`:

| Size | Track height (`--_x-slider-track-h`) | Thumb size (`--_x-slider-thumb-sz`) |
|------|--------------------------------------|--------------------------------------|
| `sm` | 4px | 14px |
| `md` | 6px | 18px |
| `lg` | 8px | 22px |

---

## Accessibility

- The native `<input type="range">` provides `role="slider"` automatically.
- `aria-valuemin`, `aria-valuemax`, `aria-valuenow` are kept in sync on the native input.
- `aria-readonly="true"` is set on the input when `readonly` is present.
- When `disabled`, the native input receives the `disabled` attribute (not just `aria-disabled`), which removes it from the tab order.
- `readonly` does not remove the element from the tab order (keyboard focus is preserved for awareness/copying).

---

## Form association

`x-slider` is form-associated (`formAssociated = true`). The current `value` is submitted with the form under the `name` attribute. Supports `formResetCallback` (resets value to `0`) and `formDisabledCallback`.

---

## Responsive

On touch devices (`@media (pointer:coarse)`) the slider thumb is enlarged to 28px for easier interaction.

---

## Usage examples

### Basic slider

```html
<x-slider value="50" min="0" max="100"></x-slider>
```

### With label and visible value

```html
<x-slider label="Volume" show-value value="70" min="0" max="100"></x-slider>
```

### Custom range and step

```html
<x-slider min="-50" max="50" step="5" value="0"></x-slider>
```

### Fractional step

```html
<x-slider min="0" max="1" step="0.01" value="0.5"></x-slider>
```

### Disabled

```html
<x-slider value="30" disabled></x-slider>
```

### Readonly

```html
<x-slider value="60" readonly></x-slider>
```

### Sizes

```html
<x-slider size="sm" value="40"></x-slider>
<x-slider size="md" value="40"></x-slider>
<x-slider size="lg" value="40"></x-slider>
```

### Custom theme

```html
<x-slider
  style="
    --x-slider-fill-color: #22c55e;
    --x-slider-thumb-border: 2px solid #22c55e;
    --x-slider-focus-ring: #86efac;
  "
  value="60"
  label="Progress"
  show-value>
</x-slider>
```

### In a form

```html
<form>
  <x-slider name="brightness" value="80" min="0" max="100" label="Brightness" show-value></x-slider>
  <button type="submit">Save</button>
</form>
```

### JavaScript API

```js
const slider = document.querySelector('x-slider');

// Read/write properties
slider.value = '50';
slider.min = '0';
slider.max = '200';
slider.step = '10';
slider.disabled = true;
slider.readOnly = false;
slider.showValue = true;
slider.label = 'Volume';
slider.size = 'lg';

// Listen for events
slider.addEventListener('x-slider-input', (e) => {
  console.log('dragging:', e.detail.value);
});
slider.addEventListener('x-slider-change', (e) => {
  console.log('committed:', e.detail.value);
});
```
