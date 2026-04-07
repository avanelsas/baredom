# `<x-bento-grid>`

A CSS Grid container for asymmetric "bento box" layouts. Uses dense auto-placement so items of different sizes pack tightly without gaps. Pair with `<x-bento-item>` to control per-cell spanning.

## Tag Name

```
x-bento-grid
```

## Attributes

| Attribute    | Type            | Default  | Description |
|-------------|-----------------|----------|-------------|
| `columns`    | integer (1–12) | `4`      | Number of grid columns. |
| `gap`        | string         | `"md"`   | Gap token: `none`, `xs`, `sm`, `md`, `lg`, `xl`. |
| `row-gap`    | string         | —        | Override row gap (same tokens as `gap`). Falls back to `gap`. |
| `column-gap` | string         | —        | Override column gap (same tokens as `gap`). Falls back to `gap`. |
| `row-height` | string         | `"auto"` | CSS value for `grid-auto-rows` (e.g. `"120px"`, `"1fr"`, `"minmax(100px,auto)"`). |

### Gap Tokens

| Token  | CSS Value |
|--------|-----------|
| `none` | `0`       |
| `xs`   | `4px`     |
| `sm`   | `8px`     |
| `md`   | `16px`    |
| `lg`   | `24px`    |
| `xl`   | `32px`    |

## Slots

| Slot      | Description |
|-----------|-------------|
| (default) | Grid children. Use `<x-bento-item>` elements for multi-span cells. |

## Parts

| Part   | Element  | Description |
|--------|----------|-------------|
| `base` | `<div>`  | Inner grid container. |

## CSS Custom Properties

| Custom Property               | Description |
|-------------------------------|-------------|
| `--x-bento-grid-columns`     | Computed `grid-template-columns` value. |
| `--x-bento-grid-row-height`  | Computed `grid-auto-rows` value. |
| `--x-bento-grid-row-gap`     | Computed row gap in px. |
| `--x-bento-grid-column-gap`  | Computed column gap in px. |

## Usage

### Basic bento layout

```html
<x-bento-grid columns="4" gap="md">
  <x-bento-item col-span="2" row-span="2">
    <x-card variant="elevated" padding="lg">Hero content</x-card>
  </x-bento-item>
  <x-bento-item>
    <x-card variant="outlined" padding="md">Small A</x-card>
  </x-bento-item>
  <x-bento-item>
    <x-card variant="outlined" padding="md">Small B</x-card>
  </x-bento-item>
  <x-bento-item col-span="2">
    <x-card variant="filled" padding="md">Wide banner</x-card>
  </x-bento-item>
</x-bento-grid>
```

### Fixed row height

```html
<x-bento-grid columns="3" gap="sm" row-height="120px">
  <x-bento-item col-span="2" row-span="2">Large</x-bento-item>
  <x-bento-item>Small</x-bento-item>
  <x-bento-item>Small</x-bento-item>
  <x-bento-item col-span="3">Full width</x-bento-item>
</x-bento-grid>
```

### ESM import

```js
import "@vanelsas/baredom/x-bento-grid";
import "@vanelsas/baredom/x-bento-item";
```

## Comparison with x-grid

| Feature | x-grid | x-bento-grid |
|---------|--------|-------------|
| Column count | Responsive (`auto-fit`) | Fixed integer |
| Auto-flow | Configurable | Always `dense` |
| Item spanning | Not built-in | Via `<x-bento-item>` |
| Use case | Uniform grids | Asymmetric magazine layouts |

## Accessibility

This is a purely structural layout component. It does not add ARIA roles. Ensure slotted content is accessible independently.
