# x-fieldset

A semantic form-grouping container that visually and semantically groups related form controls under a labeled section — the custom-element analogue of native `<fieldset>/<legend>`.

## Tag name

`x-fieldset`

## Attributes

| Attribute           | Type    | Default | Description                                       |
|---------------------|---------|---------|---------------------------------------------------|
| `legend`            | string  | `""`    | Title text rendered above the grouped content     |
| `disabled`          | boolean | false   | Visually dims the component (opacity only)        |
| `aria-label`        | string  | —       | Accessible label (overrides legend-based label)   |
| `aria-describedby`  | string  | —       | ID ref for an element that describes this group   |

When `legend` is non-empty it is used as the accessible name via `aria-labelledby` on the internal `role=group` element. When `aria-label` is also present, `aria-label` takes precedence and `aria-labelledby` is removed.

## Properties

| Property   | Type    | Reflects attribute |
|------------|---------|--------------------|
| `legend`   | string  | `legend`           |
| `disabled` | boolean | `disabled`         |

## Slots

| Name    | Description               |
|---------|---------------------------|
| default | The grouped form controls |

## Events

None. `x-fieldset` is a structural container with no user interactions.

## CSS custom properties

| Property                          | Default (light)    | Dark override |
|-----------------------------------|--------------------|---------------|
| `--x-fieldset-border-color`       | `#d1d5db`          | `#374151`     |
| `--x-fieldset-border-width`       | `1px`              | —             |
| `--x-fieldset-border-radius`      | `8px`              | —             |
| `--x-fieldset-padding`            | `1rem`             | —             |
| `--x-fieldset-gap`                | `0.75rem`          | —             |
| `--x-fieldset-bg`                 | `transparent`      | —             |
| `--x-fieldset-legend-color`       | `#374151`          | `#d1d5db`     |
| `--x-fieldset-legend-font-size`   | `0.875rem`         | —             |
| `--x-fieldset-legend-font-weight` | `600`              | —             |
| `--x-fieldset-legend-padding`     | `0 0.375rem`       | —             |
| `--x-fieldset-disabled-opacity`   | `0.45`             | —             |

## Shadow DOM parts

| Part      | Element | Description                        |
|-----------|---------|------------------------------------|
| `root`    | `div`   | Container with `role=group`        |
| `legend`  | `div`   | Legend text positioned at the top  |
| `content` | `div`   | Flex column wrapper for slot       |

## Accessibility

- `[part=root]` has `role="group"` for semantic grouping.
- When `legend` is non-empty, `aria-labelledby` references the legend element providing an accessible name.
- When `aria-label` is provided it takes precedence over `aria-labelledby`.
- `aria-describedby` is forwarded to `[part=root]` for supplemental descriptions.
- `disabled` is visual-only (opacity dimming); it does not propagate `disabled` to child controls. Use the `disabled` attribute on individual controls for form submission behavior.
- Respects `prefers-color-scheme` for dark mode theming.

## Usage examples

### Basic grouping

```html
<x-fieldset legend="Personal Information">
  <label>
    First name
    <input type="text" name="first-name" />
  </label>
  <label>
    Last name
    <input type="text" name="last-name" />
  </label>
</x-fieldset>
```

### Disabled state

```html
<x-fieldset legend="Payment Details" disabled>
  <label>Card number <input type="text" /></label>
  <label>Expiry <input type="text" /></label>
</x-fieldset>
```

### No legend with aria-label

```html
<x-fieldset aria-label="Shipping address">
  <label>Street <input type="text" /></label>
  <label>City <input type="text" /></label>
</x-fieldset>
```

### Custom theming

```html
<style>
  x-fieldset {
    --x-fieldset-border-color: #6366f1;
    --x-fieldset-legend-color: #6366f1;
    --x-fieldset-border-radius: 12px;
  }
</style>
<x-fieldset legend="Preferences">
  <label><input type="checkbox" /> Enable notifications</label>
</x-fieldset>
```

### JavaScript property API

```js
const fs = document.querySelector('x-fieldset');

// Read/write legend
fs.legend = 'Updated Label';
console.log(fs.legend); // 'Updated Label'

// Toggle disabled
fs.disabled = true;
```
