# `<x-bento-item>`

A structural wrapper that controls column and row spanning inside an `<x-bento-grid>`. Purely layout — add visual styling via slotted content (e.g. `<x-card>`).

## Tag Name

```
x-bento-item
```

## Attributes

| Attribute  | Type           | Default | Description |
|-----------|----------------|---------|-------------|
| `col-span` | integer (1–6) | `1`     | Number of grid columns to span. |
| `row-span` | integer (1–6) | `1`     | Number of grid rows to span. |
| `order`    | integer       | —       | CSS `order` for reordering within the grid. |

Out-of-range span values are clamped (minimum 1, maximum 6). Invalid values fall back to 1.

## Slots

| Slot      | Description |
|-----------|-------------|
| (default) | Cell content. Any HTML or web component. |

## Parts

| Part   | Element  | Description |
|--------|----------|-------------|
| `base` | `<div>`  | Inner wrapper (`width: 100%; height: 100%`). |

## Inline Styles

The component sets these CSS properties directly on the host element so they participate in the parent grid layout:

| Property      | Value          |
|---------------|----------------|
| `grid-column` | `span <col-span>` |
| `grid-row`    | `span <row-span>` |
| `order`       | `<order>` (only when set) |

## Usage

```html
<x-bento-grid columns="4" gap="md">
  <!-- 2×2 hero cell -->
  <x-bento-item col-span="2" row-span="2">
    <x-card variant="elevated" padding="lg">
      <h2>Featured</h2>
    </x-card>
  </x-bento-item>

  <!-- Default 1×1 cells -->
  <x-bento-item>
    <x-card variant="outlined" padding="md">Item A</x-card>
  </x-bento-item>
  <x-bento-item>
    <x-card variant="outlined" padding="md">Item B</x-card>
  </x-bento-item>

  <!-- Wide 2×1 cell -->
  <x-bento-item col-span="2">
    <x-card variant="filled" padding="md">Wide content</x-card>
  </x-bento-item>
</x-bento-grid>
```

## ESM Import

```js
import "@vanelsas/baredom/x-bento-item";
```

## Accessibility

This is a purely structural layout component. It does not add ARIA roles. Ensure slotted content is accessible independently.
