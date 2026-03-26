# x-progress-circle

A circular SVG progress indicator. Renders a stroke-based ring that fills proportionally to the `value`/`max` ratio, with an optional indeterminate spin animation.

## Tag name

```html
<x-progress-circle>
```

## Observed attributes

| Attribute       | Type    | Default     | Description                                      |
|-----------------|---------|-------------|--------------------------------------------------|
| `value`         | number  | `0`         | Current progress value                           |
| `max`           | number  | `100`       | Maximum progress value (must be > 0)             |
| `variant`       | string  | `"default"` | Color variant: `default` `success` `warning` `danger` |
| `size`          | string  | `"md"`      | Component size: `sm` (40px) `md` (64px) `lg` (96px) |
| `label`         | string  | —           | Sets `aria-label` on the host element            |
| `show-value`    | boolean | absent      | When present, renders the percentage in the center |
| `indeterminate` | boolean | absent      | When present, shows a spinning arc (ignores `value`) |

## Properties

| Property        | Type    | Reflects attribute |
|-----------------|---------|--------------------|
| `value`         | string  | `value`            |
| `max`           | string  | `max`              |
| `indeterminate` | boolean | `indeterminate`    |
| `showValue`     | boolean | `show-value`       |

## Events

| Event                        | Bubbles | Composed | Detail                        |
|------------------------------|---------|----------|-------------------------------|
| `x-progress-circle-complete` | yes     | yes      | `{ value: number, max: number }` |

Fires once when `value >= max` (and not indeterminate). Resets when value drops below `max`, allowing the event to fire again.

## Shadow parts

| Part         | Element  | Description                        |
|--------------|----------|------------------------------------|
| `base`       | `<div>`  | Outer wrapper; receives data attrs |
| `svg`        | `<svg>`  | The SVG canvas                     |
| `track`      | `<circle>` | Background ring (track color)    |
| `fill`       | `<circle>` | Foreground arc (fill color)      |
| `center`     | `<div>`  | Absolute overlay for value text    |
| `value-text` | `<span>` | Percentage label inside the ring   |

## CSS custom properties

| Property                               | Default                  | Description                        |
|----------------------------------------|--------------------------|------------------------------------|
| `--x-progress-circle-size`             | `64px`                   | Outer diameter (overridden by size) |
| `--x-progress-circle-track-color`      | `rgba(0,0,0,0.10)`       | Ring track color (adaptive dark)   |
| `--x-progress-circle-fill-color`       | `#3b82f6`                | Arc fill color (overridden by variant) |
| `--x-progress-circle-stroke-width`     | `2.8`                    | SVG stroke width in viewBox units  |
| `--x-progress-circle-value-color`      | `rgba(0,0,0,0.50)`       | Center text color (adaptive dark)  |
| `--x-progress-circle-transition-duration` | `0.3s`               | Transition speed for dashoffset    |

## Accessibility

- Host element has `role="progressbar"`, `aria-valuemin="0"`, `aria-valuenow`, `aria-valuemax`, `aria-valuetext`.
- When indeterminate: `aria-busy="true"` is set, `aria-valuenow` is removed, `aria-valuetext` is `"Loading…"`.
- `aria-label` is set from the `label` attribute when provided.
- The SVG is `aria-hidden="true"` and `focusable="false"` to prevent duplicate screen-reader announcements.

## Theming

Light/dark mode is handled via `@media (prefers-color-scheme: dark)` inside the shadow style. Override any `--x-progress-circle-*` custom property on the host element to theme the component.

## Motion

The spin animation (`x-progress-circle-spin`) is suppressed under `@media (prefers-reduced-motion: reduce)`. The dashoffset transition is also disabled in that context.

## SVG math

- `viewBox="0 0 36 36"`, `cx="18"`, `cy="18"`, `r="15.9155"`
- Circumference: `2 × π × 15.9155 ≈ 100`
- `stroke-dasharray` is always set to `100`
- `stroke-dashoffset` = `100 × (1 − percent/100)` for determinate; `75` (25% arc) for indeterminate
- The fill circle is rotated `−90deg` in CSS so the arc starts at 12 o'clock

## Usage examples

```html
<!-- Basic progress -->
<x-progress-circle value="65"></x-progress-circle>

<!-- With percentage label -->
<x-progress-circle value="72" show-value></x-progress-circle>

<!-- Variants -->
<x-progress-circle value="90" variant="success" show-value></x-progress-circle>
<x-progress-circle value="30" variant="danger"  show-value></x-progress-circle>

<!-- Sizes -->
<x-progress-circle value="50" size="sm"></x-progress-circle>
<x-progress-circle value="50" size="lg" show-value></x-progress-circle>

<!-- Indeterminate -->
<x-progress-circle indeterminate label="Loading data…"></x-progress-circle>

<!-- Custom theme -->
<x-progress-circle value="60" show-value
  style="--x-progress-circle-fill-color:#8b5cf6;
         --x-progress-circle-track-color:#ede9fe;">
</x-progress-circle>

<!-- Completion event -->
<x-progress-circle id="uploader" value="0" max="100"></x-progress-circle>
<script>
  const el = document.getElementById('uploader');
  el.addEventListener('x-progress-circle-complete', e => {
    console.log('Done!', e.detail); // { value: 100, max: 100 }
  });
  el.value = '100';
</script>
```
