# x-multi-combobox

A multi-select combobox with type-ahead filtering. Selected items display as removable chips. Options are provided as `<option>` children; selected options are hidden from the dropdown and reappear when deselected.

## Tag name

```html
<x-multi-combobox>...</x-multi-combobox>
```

## Observed attributes

| Attribute     | Type    | Default          | Description |
|---------------|---------|------------------|-------------|
| `value`       | string  | `""`             | Comma-separated selected values |
| `placeholder` | string  | `""`             | Input placeholder (shown when no chips) |
| `name`        | string  | `""`             | Form field name |
| `disabled`    | boolean | `false`          | Disables the component |
| `required`    | boolean | `false`          | Marks as required |
| `open`        | boolean | `false`          | Controls panel visibility |
| `placement`   | enum    | `"bottom-start"` | Panel position relative to input |
| `max`         | number  | none             | Maximum number of selections |
| `error`       | string  | `""`             | Inline validation message; shows the `error` part and marks the field invalid |

### Placement values

`bottom-start` | `bottom-end` | `top-start` | `top-end`

## Properties

| Property      | Type      | Reflected attribute | Notes |
|---------------|-----------|---------------------|-------|
| `value`       | string[]  | `value`             | Getter returns JS array; setter accepts array or CSV string |
| `placeholder` | string    | `placeholder`       | |
| `name`        | string    | `name`              | |
| `disabled`    | boolean   | `disabled`          | |
| `required`    | boolean   | `required`          | |
| `open`        | boolean   | `open`              | |
| `placement`   | string    | `placement`         | |
| `max`         | number    | `max`               | |
| `error`       | string    | `error`             | Inline validation message |

## Public methods

| Method   | Description |
|----------|-------------|
| `show()` | Opens the dropdown panel (source: `"programmatic"`) |
| `hide()` | Closes the dropdown panel (source: `"programmatic"`) |

## Events

### `x-multi-combobox-change-request`

Fired before a value is added or removed. **Cancelable** — call `event.preventDefault()` to block the update.

| Property | Value |
|----------|-------|
| `bubbles` | `true` |
| `composed` | `true` |
| `cancelable` | **`true`** |
| `detail.value` | Proposed new value set (string[]) |
| `detail.action` | `"add"` or `"remove"` |
| `detail.item` | The specific value being added/removed (string) |

### `x-multi-combobox-change`

Fired after the value has changed.

| Property | Value |
|----------|-------|
| `bubbles` | `true` |
| `composed` | `true` |
| `cancelable` | `false` |
| `detail.value` | Current value set (string[]) |

### `x-multi-combobox-input`

Fired on every filter keystroke.

| Property | Value |
|----------|-------|
| `bubbles` | `true` |
| `composed` | `true` |
| `cancelable` | `false` |
| `detail.query` | Current filter text (string) |

### `x-multi-combobox-toggle`

Fired before the panel opens or closes. **Cancelable**.

| Property | Value |
|----------|-------|
| `bubbles` | `true` |
| `composed` | `true` |
| `cancelable` | **`true`** |
| `detail.open` | Target state (boolean) |
| `detail.source` | `"focus"` `"pointer"` `"keyboard"` `"input"` `"escape"` `"outside-click"` `"focusout"` `"programmatic"` |

## Slots

| Slot | Content |
|------|---------|
| default | `<option value="...">Label</option>` elements |

## CSS custom properties

| Property | Default | Description |
|----------|---------|-------------|
| `--x-multi-combobox-bg` | `var(--x-color-surface)` | Wrapper background |
| `--x-multi-combobox-fg` | `var(--x-color-text)` | Text color |
| `--x-multi-combobox-placeholder` | `var(--x-color-text-muted)` | Placeholder color |
| `--x-multi-combobox-border` | `1px solid var(--x-color-border)` | Default border |
| `--x-multi-combobox-border-focus` | `1px solid var(--x-color-focus-ring)` | Focus border |
| `--x-multi-combobox-radius` | `var(--x-radius-md)` | Border radius |
| `--x-multi-combobox-min-height` | `2.25rem` | Minimum wrapper height |
| `--x-multi-combobox-font-size` | `var(--x-font-size-sm)` | Font size |
| `--x-multi-combobox-panel-bg` | `var(--x-color-bg)` | Panel background |
| `--x-multi-combobox-panel-max-height` | `16rem` | Panel max scroll height |
| `--x-multi-combobox-option-active-bg` | `var(--x-color-primary)` | Keyboard-highlighted option |
| `--x-multi-combobox-chip-gap` | `0.25rem` | Gap between chips |
| `--x-multi-combobox-error-color` | `var(--x-color-danger)` | Inline error text + invalid border |

Chip styling is owned by `x-chip`. Use `--x-chip-*` custom properties to theme chips globally.

## Shadow parts

| Part | Element | Description |
|------|---------|-------------|
| `wrapper` | `<div>` | Outer container |
| `chip-area` | `<div>` | Flex container for chips + input |
| `input` | `<input>` | Filter text field |
| `chevron` | `<span>` | Dropdown arrow indicator |
| `panel` | `<div>` | Dropdown listbox |
| `option` | `<div>` | Individual option in dropdown |
| `empty-msg` | `<div>` | "No matches" message |
| `error` | `<span>` | Inline validation message; hidden until the `error` attribute is set |

## Keyboard

| Key | Behavior |
|-----|----------|
| Arrow Down | Open panel / highlight next option |
| Arrow Up | Open panel / highlight previous option |
| Enter | Add highlighted option |
| Escape | Close panel, clear filter |
| Backspace | Remove last chip (when input empty) |
| Home | Jump to first option |
| End | Jump to last option |

## Accessibility

- Input: `role="combobox"`, `aria-expanded`, `aria-autocomplete="list"`, `aria-controls`
- Panel: `role="listbox"`, `aria-multiselectable="true"`
- Options: `role="option"`, `aria-disabled` when max reached
- Chip area: `role="group"`, `aria-label="Selected values"`
- Active option tracked via `aria-activedescendant`
- When the `error` attribute is set, the input gets `aria-invalid="true"` and `aria-describedby="error"`, and the `error` part is an assertive live region (`role="alert"`) so screen readers announce the validation message

## Example

```html
<x-multi-combobox placeholder="Pick fruits" max="3">
  <option value="apple">Apple</option>
  <option value="banana">Banana</option>
  <option value="cherry">Cherry</option>
  <option value="date">Date</option>
</x-multi-combobox>
```

```js
const el = document.querySelector('x-multi-combobox');

// Read selected values
console.log(el.value); // ["apple", "cherry"]

// Set values programmatically
el.value = ["banana", "date"];

// Listen for changes
el.addEventListener('x-multi-combobox-change', e => {
  console.log('Selected:', e.detail.value);
});
```

### Validation error

The `error` attribute renders an inline message below the control and marks it
invalid. Inside `x-form`, this is driven for you by `form.setFieldError(name, message)`.

```html
<x-multi-combobox name="tags" error="Select at least one tag" placeholder="Pick tags">
  <option value="news">News</option>
  <option value="sport">Sport</option>
</x-multi-combobox>
```

Clear it by removing the attribute (or `el.error = ''`).
