# x-menu

A dropdown command menu that wraps a trigger element and a list of `x-menu-item` children.

## Tag name

`x-menu`

## Attributes

| Attribute   | Type    | Default          | Notes                                              |
|-------------|---------|------------------|----------------------------------------------------|
| `open`      | boolean | absent = closed  | Controls menu visibility                           |
| `placement` | string  | `"bottom-start"` | `bottom-start` `bottom-end` `top-start` `top-end`  |
| `label`     | string  | `""`             | Sets `aria-label` on the popup panel               |

## Properties

| Property    | Type    | Reflects attribute |
|-------------|---------|-------------------|
| `open`      | boolean | yes               |
| `placement` | string  | yes               |
| `label`     | string  | yes               |

## Events

| Event           | Bubbles | Composed | Detail        |
|-----------------|---------|----------|---------------|
| `x-menu-open`   | yes     | yes      | `{}`          |
| `x-menu-close`  | yes     | yes      | `{}`          |
| `x-menu-select` | yes     | yes      | `{ value }`   |

## Slots

| Slot      | Notes                                              |
|-----------|----------------------------------------------------|
| `trigger` | Consumer-supplied trigger element (button, etc.)   |
| default   | `x-menu-item` elements                             |

## CSS custom properties

| Property                  | Light default                    | Dark default                     |
|---------------------------|----------------------------------|----------------------------------|
| `--x-menu-bg`             | `#ffffff`                        | `#1f2937`                        |
| `--x-menu-border`         | `1px solid #e5e7eb`              | `1px solid #374151`              |
| `--x-menu-border-radius`  | `8px`                            | `8px`                            |
| `--x-menu-shadow`         | `0 4px 16px rgba(0,0,0,0.12)`   | `0 4px 16px rgba(0,0,0,0.4)`    |
| `--x-menu-min-width`      | `160px`                          | `160px`                          |
| `--x-menu-padding`        | `4px`                            | `4px`                            |
| `--x-menu-z-index`        | `1000`                           | `1000`                           |

## Keyboard behaviour

| Key          | Condition       | Result                                    |
|--------------|-----------------|-------------------------------------------|
| `ArrowDown`  | trigger focused, closed | Open menu, focus first enabled item |
| `ArrowUp`    | trigger focused, closed | Open menu, focus last enabled item  |
| `ArrowDown`  | menu open       | Focus next enabled item (wraps)           |
| `ArrowUp`    | menu open       | Focus previous enabled item (wraps)       |
| `Home`       | menu open       | Focus first enabled item                  |
| `End`        | menu open       | Focus last enabled item                   |
| `Escape`     | menu open       | Close menu, return focus to trigger       |
| `Tab`        | menu open       | Close menu                                |

Click outside the host closes the menu without returning focus.

## Accessibility

- The popup panel has `role="menu"` and `aria-label` (from the `label` attribute).
- `x-menu-item` children receive `role="menuitem"` or `role="separator"`.
- Focus is managed via `tabindex="-1"` on items and programmatic `.focus()`.
- `aria-disabled` is set on disabled items.

## Usage

```html
<x-menu label="Actions">
  <button slot="trigger">Actions ▾</button>
  <x-menu-item value="edit">Edit</x-menu-item>
  <x-menu-item value="duplicate">Duplicate</x-menu-item>
  <x-menu-item type="divider"></x-menu-item>
  <x-menu-item value="delete" variant="danger">Delete</x-menu-item>
</x-menu>

<script>
  import '@vanelsas/baredom/x-menu';
  import '@vanelsas/baredom/x-menu-item';

  document.querySelector('x-menu').addEventListener('x-menu-select', e => {
    console.log('Selected:', e.detail.value);
  });
</script>
```
