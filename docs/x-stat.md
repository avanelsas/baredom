# x-stat

A themeable, accessible metric display component for presenting a label, value, and contextual hint. Supports semantic variants, size/emphasis/alignment options, trend indication, named slots, and a loading state.

---

## Tag

```html
<x-stat></x-stat>
```

---

## Attributes

| Attribute  | Type    | Default     | Description                                            |
|------------|---------|-------------|--------------------------------------------------------|
| `variant`  | enum    | `"default"` | Semantic variant: `default` `subtle` `positive` `warning` `danger` |
| `align`    | enum    | `"start"`   | Text alignment: `start` `center` `end`                 |
| `size`     | enum    | `"md"`      | Display size: `sm` `md` `lg`                           |
| `emphasis` | enum    | `"normal"`  | Value emphasis: `normal` `high`                        |
| `trend`    | enum    | `"neutral"` | Trend direction: `up` `down` `neutral`                 |
| `loading`  | boolean | `false`     | Shows loading state with reduced opacity               |
| `label`    | string  | —           | Metric label text                                      |
| `value`    | string  | —           | Metric value text                                      |
| `hint`     | string  | —           | Supplementary hint text                                |

---

## Properties

| Property   | Type    | Reflects attribute |
|------------|---------|-------------------|
| `variant`  | string  | `variant`         |
| `align`    | string  | `align`           |
| `size`     | string  | `size`            |
| `emphasis` | string  | `emphasis`        |
| `trend`    | string  | `trend`           |
| `loading`  | boolean | `loading`         |
| `label`    | string  | `label`           |
| `value`    | string  | `value`           |
| `hint`     | string  | `hint`            |

---

## Events

None. This component is a pure display element.

---

## Slots

| Slot      | Description                              |
|-----------|------------------------------------------|
| `icon`    | Custom icon element                      |
| `label`   | Custom label content (overrides `label` attribute text) |
| `value`   | Custom value content (overrides `value` attribute text) |
| `hint`    | Custom hint content (overrides `hint` attribute text)   |
| (default) | Additional content appended to the body  |

```html
<x-stat variant="positive" trend="up">
  <span slot="icon" aria-hidden="true">↗</span>
  <span slot="label">Monthly recurring revenue</span>
  <span slot="value">$84,920</span>
  <span slot="hint">Up 11.4% from last month</span>
</x-stat>
```

---

## Parts

| Part    | Description                        |
|---------|------------------------------------|
| `base`  | Outer grid wrapper                 |
| `icon`  | Icon area containing the icon slot |
| `body`  | Body area containing label, value, hint, and default slot |
| `label` | Label row (contains label slot + text span) |
| `value` | Value row (contains value slot + text span) |
| `hint`  | Hint row (contains hint slot + text span)   |

---

## CSS Custom Properties

### Layout

| Variable                       | Default       | Description             |
|--------------------------------|---------------|-------------------------|
| `--x-stat-background`          | `transparent` | Background colour       |
| `--x-stat-color`               | `inherit`     | Base text colour        |
| `--x-stat-muted-color`         | `rgba(0,0,0,0.55)` | Muted text colour (label, hint) |
| `--x-stat-border-color`        | `transparent` | Border colour           |
| `--x-stat-radius`              | `12px`        | Border radius           |
| `--x-stat-padding`             | `16px`        | Padding                 |
| `--x-stat-gap`                 | `6px`         | Grid gap                |

### Typography

| Variable                       | Default       | Description             |
|--------------------------------|---------------|-------------------------|
| `--x-stat-label-color`         | muted-color   | Label text colour       |
| `--x-stat-label-size`          | `12px`        | Label font size         |
| `--x-stat-value-color`         | color         | Value text colour       |
| `--x-stat-value-size`          | `20px`        | Value font size         |
| `--x-stat-hint-color`          | muted-color   | Hint text colour        |
| `--x-stat-hint-size`           | `12px`        | Hint font size          |
| `--x-stat-icon-color`          | color         | Icon colour             |

### Semantic colour tokens

| Variable                       | Default (light) | Description           |
|--------------------------------|-----------------|-----------------------|
| `--x-stat-positive-color`      | `#16a34a`       | Positive variant value colour |
| `--x-stat-warning-color`       | `#d97706`       | Warning variant value colour  |
| `--x-stat-danger-color`        | `#dc2626`       | Danger variant value colour   |

Dark-mode variants are set automatically via `@media (prefers-color-scheme: dark)`.

### Motion & state

| Variable                       | Default | Description                |
|--------------------------------|---------|----------------------------|
| `--x-stat-transition-duration` | `120ms` | Transition duration        |
| `--x-stat-transition-timing`   | `ease`  | Transition easing function |
| `--x-stat-loading-opacity`     | `0.6`   | Opacity when loading       |

---

## Accessibility

- Host element carries `role="figure"`.
- When `loading` is set, `aria-busy="true"` is applied.
- `aria-label` is derived from the `label` attribute.
- Animations respect `@media (prefers-reduced-motion: reduce)`.

---

## Examples

### Basic usage

```html
<x-stat label="Revenue" value="$128.4K" hint="Quarter to date"></x-stat>
```

### Semantic variants

```html
<x-stat variant="positive" label="Growth" value="+18.2%" hint="Healthy trend"></x-stat>
<x-stat variant="warning" label="At-risk" value="12" hint="Review recommended"></x-stat>
<x-stat variant="danger" label="Failed" value="3" hint="Action required"></x-stat>
```

### Size and emphasis

```html
<x-stat size="sm" label="Compact" value="128"></x-stat>
<x-stat size="lg" emphasis="high" align="center" label="Headline" value="$2.4M"></x-stat>
```

### Loading state

```html
<x-stat loading label="Forecast" value="Loading…" hint="Refreshing"></x-stat>
```

### Rich content via slots

```html
<x-stat variant="positive" trend="up">
  <span slot="icon" aria-hidden="true">↗</span>
  <span slot="label">Monthly recurring revenue</span>
  <span slot="value">$84,920</span>
  <span slot="hint">Up 11.4% from last month</span>
</x-stat>
```

### ClojureScript (hiccup renderer)

```clojure
[:x-stat {:variant "positive"
          :label   "Net growth"
          :value   "+18.2%"
          :hint    "Healthy upward trend"}]
```
