# x-progress

A linear progress bar web component with variant colors, optional value label, indeterminate animation, and a completion event.

## Tag

```html
<x-progress value="60" max="100"></x-progress>
```

## Observed Attributes

| Attribute       | Type    | Default   | Values                                    |
|-----------------|---------|-----------|-------------------------------------------|
| `value`         | number  | `0`       | Numeric string, clamped to `[0, max]`     |
| `max`           | number  | `100`     | Numeric string > 0; falls back to `100`   |
| `variant`       | enum    | `default` | `default` `success` `warning` `danger`    |
| `size`          | enum    | `md`      | `sm` `md` `lg`                            |
| `label`         | string  | —         | Visible label rendered above the track    |
| `show-value`    | boolean | absent    | Shows computed percentage text            |
| `indeterminate` | boolean | absent    | Animated shimmer; ignores `value`         |

## Properties

| Property        | Type    | Reflects attribute  |
|-----------------|---------|---------------------|
| `value`         | string  | `value`             |
| `max`           | string  | `max`               |
| `indeterminate` | boolean | `indeterminate`     |
| `showValue`     | boolean | `show-value`        |

## Events

| Event                 | Bubbles | Detail                          | When                                          |
|-----------------------|---------|---------------------------------|-----------------------------------------------|
| `x-progress-complete` | yes     | `{ value: number, max: number }`| Once when `value >= max`; resets when `value < max` |

## Slots

None. Label text is provided via the `label` attribute.

## CSS Custom Properties

| Property                       | Default                            | Description                         |
|--------------------------------|------------------------------------|-------------------------------------|
| `--x-progress-height`          | `4px` / `8px` / `12px` (by size)  | Track height                        |
| `--x-progress-track-color`     | Adaptive muted grey                | Background of the track             |
| `--x-progress-fill-color`      | Variant-driven (blue / green / amber / red) | Fill color                |
| `--x-progress-border-radius`   | `9999px`                           | Radius for track and fill           |
| `--x-progress-label-color`     | Adaptive muted                     | Color of the label text             |
| `--x-progress-value-color`     | Adaptive muted                     | Color of the percentage value text  |

## Accessibility

- `role="progressbar"` is set on the host element.
- `aria-valuenow`, `aria-valuemin="0"`, `aria-valuemax` reflect the current numeric state.
- `aria-valuetext` shows e.g. `"75%"` or `"Loading…"` when indeterminate.
- `aria-label` mirrors the `label` attribute.
- `aria-busy="true"` is set when `indeterminate` is present; removed otherwise.

## Shadow DOM Structure

```
:host [role=progressbar aria-valuenow aria-valuemin aria-valuemax aria-valuetext]
  <style>
  <div part="base" data-variant data-size data-indeterminate>
    <div part="header">           ← hidden when neither label nor show-value
      <span part="label-text">   ← textContent = label attribute
      <span part="value-text">   ← textContent = "75%" when show-value
    <div part="track">
      <div part="fill">          ← width driven by inline style %
```

## Usage Examples

### Basic

```html
<x-progress value="40"></x-progress>
```

### With label and value display

```html
<x-progress value="75" label="Upload progress" show-value></x-progress>
```

### Variants

```html
<x-progress value="100" variant="success"></x-progress>
<x-progress value="60"  variant="warning"></x-progress>
<x-progress value="30"  variant="danger"></x-progress>
```

### Sizes

```html
<x-progress value="50" size="sm"></x-progress>
<x-progress value="50" size="md"></x-progress>
<x-progress value="50" size="lg"></x-progress>
```

### Indeterminate

```html
<x-progress indeterminate label="Loading…"></x-progress>
```

### Listening for completion

```js
document.querySelector('x-progress').addEventListener('x-progress-complete', e => {
  console.log('Done!', e.detail); // { value: 100, max: 100 }
});
```

### Custom theme

```css
x-progress {
  --x-progress-fill-color: #8b5cf6;
  --x-progress-track-color: #ede9fe;
  --x-progress-height: 10px;
}
```
