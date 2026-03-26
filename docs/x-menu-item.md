# x-menu-item

A single item inside an `x-menu`. Fires `x-menu-item-select` when activated; `x-menu` consumes this event and re-dispatches `x-menu-select`.

## Tag name

`x-menu-item`

## Attributes

| Attribute  | Type    | Default | Notes                                             |
|------------|---------|---------|--------------------------------------------------|
| `value`    | string  | `""`    | Value emitted in the select event detail          |
| `disabled` | boolean | absent  | Prevents interaction and keyboard focus           |
| `variant`  | string  | `""`    | `""` (default) or `"danger"` (red colour)         |
| `type`     | string  | `""`    | `""` (item) or `"divider"` (renders a separator)  |

## Properties

| Property   | Type    | Reflects attribute |
|------------|---------|--------------------|
| `value`    | string  | yes                |
| `disabled` | boolean | yes                |
| `variant`  | string  | yes                |
| `type`     | string  | yes                |

## Events

| Event                | Bubbles | Composed | Detail      | Notes              |
|----------------------|---------|----------|-------------|--------------------|
| `x-menu-item-select` | yes     | yes      | `{ value }` | Consumed by x-menu |

## Slots

| Slot    | Notes                             |
|---------|-----------------------------------|
| default | Item label text / content         |
| `icon`  | Leading icon (SVG, img, span …)   |

When elements are assigned to the `icon` slot, the `has-icon` attribute is set on the host; this drives icon span visibility via CSS.

## CSS custom properties

| Property                            | Light default   | Dark default    |
|-------------------------------------|-----------------|-----------------|
| `--x-menu-item-color`               | `#111827`       | `#f9fafb`       |
| `--x-menu-item-hover-bg`            | `#f3f4f6`       | `#1f2937`       |
| `--x-menu-item-focus-bg`            | `#eff6ff`       | `#1e3a5f`       |
| `--x-menu-item-focus-color`         | `#1d4ed8`       | `#60a5fa`       |
| `--x-menu-item-disabled-opacity`    | `0.45`          | `0.45`          |
| `--x-menu-item-danger-color`        | `#dc2626`       | `#f87171`       |
| `--x-menu-item-danger-hover-bg`     | `#fef2f2`       | `#2d1515`       |
| `--x-menu-item-padding`             | `8px 12px`      | `8px 12px`      |
| `--x-menu-item-border-radius`       | `4px`           | `4px`           |
| `--x-menu-item-font-size`           | `0.9375rem`     | `0.9375rem`     |
| `--x-menu-item-icon-gap`            | `8px`           | `8px`           |
| `--x-menu-item-divider-color`       | `#e5e7eb`       | `#374151`       |

## Divider rendering

When `type="divider"`, the item renders an `<hr>` element styled as a horizontal rule. The host receives `role="separator"` and no `tabindex`. Clicks are ignored.

## Keyboard behaviour

| Key     | Result                       |
|---------|------------------------------|
| `Enter` | Dispatch `x-menu-item-select` |
| `Space` | Dispatch `x-menu-item-select` |
| `ArrowUp` / `ArrowDown` | Bubble to `x-menu` for navigation |

## Accessibility

- `role="menuitem"` on the host element (or `role="separator"` for dividers).
- `tabindex="-1"` on non-divider items; focus is managed by `x-menu`.
- `aria-disabled="true"` on disabled items.

## Usage

```html
<!-- Used inside x-menu -->
<x-menu label="Options">
  <button slot="trigger">Options</button>

  <x-menu-item value="copy">
    <span slot="icon">📋</span>
    Copy
  </x-menu-item>

  <x-menu-item value="cut">Cut</x-menu-item>

  <x-menu-item type="divider"></x-menu-item>

  <x-menu-item value="delete" variant="danger">Delete</x-menu-item>
  <x-menu-item value="noop" disabled>Unavailable</x-menu-item>
</x-menu>
```
