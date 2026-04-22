# x-skeleton-group

A container component that wraps `x-skeleton` children to provide synchronized animation timing and declarative layout presets. All child skeletons animate in phase, and common patterns (card, list-item, paragraph, table-row, profile) can be generated from a single attribute.

## Tag name

```html
<x-skeleton-group>...</x-skeleton-group>
```

## Observed attributes

| Attribute   | Type   | Default  | Description |
|-------------|--------|----------|-------------|
| `preset`    | enum   | (none)   | Layout preset to generate |
| `animation` | enum   | `"pulse"` | Animation propagated to all children: pulse, wave, none |
| `count`     | number | `1`      | Number of preset repetitions (1-20) |

### Preset values

`card` | `list-item` | `paragraph` | `table-row` | `profile`

## Properties (camelCase, reflect attributes)

| Property    | Type   | Reflected attribute |
|-------------|--------|---------------------|
| `preset`    | string | `preset` |
| `animation` | string | `animation` |
| `count`     | number | `count` |

## Slots

| Slot      | Description |
|-----------|-------------|
| (default) | Custom `x-skeleton` children (hidden when `preset` is set) |

## Shadow parts

| Part               | Description |
|--------------------|-------------|
| `container`        | Outer layout wrapper |
| `preset-container` | Container for generated preset skeletons |

## CSS custom properties

| Property | Default | Description |
|----------|---------|-------------|
| `--x-skeleton-group-gap` | `12px` | Gap between skeleton items in the container |

The group also inherits all `--x-skeleton-*` properties to its children (color, highlight, duration, border-radius).

## Presets

### `card`
Image rectangle (140px) + 3 text lines (80%, 60%, 40%). Vertical layout.

### `list-item`
Circle avatar (40px) + 2 text lines (70%, 50%) side by side. Horizontal layout.

### `paragraph`
3 full-width text lines + 1 short line (60%). Vertical layout.

### `table-row`
4 equal-width text cells in a row. Horizontal layout.

### `profile`
Circle avatar (48px) + 2 text lines (120px, 80px) side by side. Horizontal layout.

## Animation synchronization

When multiple `x-skeleton` elements are placed independently, each starts its CSS animation at a different point in time. Inside an `x-skeleton-group`, all children animate in phase — pulses and waves are visually synchronized.

This works via the inherited CSS custom property `--x-skeleton-delay`, which the group computes to align all children to a common animation phase.

## Accessibility

- Host has `aria-hidden="true"` (skeletons are decorative)
- Use `aria-busy="true"` on a parent container to communicate loading state to screen readers

## Usage examples

### Preset

```html
<x-skeleton-group preset="card"></x-skeleton-group>
<x-skeleton-group preset="profile" animation="wave"></x-skeleton-group>
```

### Repeated preset

```html
<x-skeleton-group preset="list-item" count="5"></x-skeleton-group>
```

### Custom children (synchronized)

```html
<x-skeleton-group animation="pulse">
  <x-skeleton variant="circle" width="48px" height="48px"></x-skeleton>
  <x-skeleton variant="text" width="80%"></x-skeleton>
  <x-skeleton variant="text" width="50%"></x-skeleton>
</x-skeleton-group>
```

### Wave animation

```html
<x-skeleton-group preset="card" animation="wave"></x-skeleton-group>
```

### Custom theming

```html
<x-skeleton-group preset="paragraph" style="
  --x-skeleton-color: #ede9fe;
  --x-skeleton-highlight: rgba(139,92,246,0.25);
  --x-skeleton-group-gap: 16px;
"></x-skeleton-group>
```
