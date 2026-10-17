# x-multi-combobox

A multi-select combobox with type-ahead filtering. Selected items display as removable chips. Options are provided as `<option>` children; selected options are hidden from the dropdown and reappear when deselected.

## Tag name

```html
<x-multi-combobox>...</x-multi-combobox>
```

## Observed attributes

| Attribute     | Type    | Default          | Description |
|---------------|---------|------------------|-------------|
| `value`       | string  | `""`             | Selected values, in the format that `value-format` names |
| `placeholder` | string  | `""`             | Input placeholder (shown when no chips) |
| `name`        | string  | `""`             | Form field name |
| `disabled`    | boolean | `false`          | Disables the component |
| `required`    | boolean | `false`          | Marks as required |
| `open`        | boolean | `false`          | Controls panel visibility |
| `placement`   | enum    | `"bottom-start"` | Panel position relative to input |
| `max`         | number  | none             | Maximum number of selections |
| `error`       | string  | `""`             | Inline validation message; shows the `error` part and marks the field invalid |
| `value-format` | enum   | `"csv"`          | Format of the `value` attribute: `csv` or `json`. An unknown value acts as `csv` |

### Placement values

`bottom-start` | `bottom-end` | `top-start` | `top-end`

### Value formats

| Format | `value` attribute | Notes |
|--------|-------------------|-------|
| `csv` (default) | `apple,banana` | Split at each comma. Each value is trimmed, and empty values are dropped |
| `json` | `["apple","banana"]` | A JSON array of strings. Each string is kept exactly, with its commas and spaces. Empty strings are dropped |

Use `json` when a value can hold a comma or leading or trailing spaces. In `csv`, the value
`Liefdadigheid, religieus en non-profit` becomes two values.

In `json`, a `value` attribute that is not a JSON array of strings gives no selection and sends
`x-multi-combobox-error`. An absent or empty `value` is no selection, with no error.

A change of `value-format` reads the current `value` attribute again in the new format. The
attribute itself does not change. To switch format with a selection, set `value-format` first
and then set `value`.

## Properties

| Property      | Type      | Reflected attribute | Notes |
|---------------|-----------|---------------------|-------|
| `value`       | string[]  | `value`             | Getter returns a sorted JS array. Setter accepts an array or a string in the value format |
| `placeholder` | string    | `placeholder`       | |
| `name`        | string    | `name`              | |
| `disabled`    | boolean   | `disabled`          | |
| `required`    | boolean   | `required`          | |
| `open`        | boolean   | `open`              | |
| `placement`   | string    | `placement`         | |
| `max`         | number    | `max`               | |
| `error`       | string    | `error`             | Inline validation message |
| `valueFormat` | string    | `value-format`      | `"csv"` or `"json"` |

The `value` setter writes the `value` attribute in the value format:

- In `csv`, an array is joined with commas. If a value holds a comma or leading or trailing
  whitespace, the setter changes nothing and sends `x-multi-combobox-error`.
- In `json`, an array is written as a JSON array of strings, exactly as given. Empty values
  are dropped.
- A string is written to the attribute as it is. An empty string or an empty array clears the
  selection.

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

### `x-multi-combobox-error`

Fired when the component cannot take a value. It is sent in two cases:

- In `json`, the `value` attribute is not a JSON array of strings. The selection is empty.
- In `csv`, the `value` setter gets an array with a value that a comma-separated list cannot
  hold. The selection does not change.

| Property | Value |
|----------|-------|
| `bubbles` | `true` |
| `composed` | `true` |
| `cancelable` | `false` |
| `detail.error` | What went wrong (string). In `csv`, it tells you to set `value-format="json"` |
| `detail.values` | The values the component could not take (string[]). For an invalid attribute, the attribute text |

## Slots

| Slot | Content |
|------|---------|
| default | `<option value="...">Label</option>` elements |

## CSS custom properties

| Property | Default | Description |
|----------|---------|-------------|
| `--x-multi-combobox-bg` | `var(--x-color-surface,#ffffff)` | Wrapper background |
| `--x-multi-combobox-fg` | `var(--x-color-text,#0f172a)` | Text color |
| `--x-multi-combobox-placeholder` | `var(--x-color-text-muted,#94a3b8)` | Placeholder color |
| `--x-multi-combobox-border` | `var(--x-border-width,1px) solid var(--x-color-border,#e2e8f0)` | Default border |
| `--x-multi-combobox-border-focus` | `var(--x-border-width,1px) solid var(--x-color-focus-ring,#60a5fa)` | Focus border |
| `--x-multi-combobox-radius` | `var(--x-radius-md,6px)` | Border radius |
| `--x-multi-combobox-min-height` | `2.25rem` | Minimum wrapper height |
| `--x-multi-combobox-font-size` | `var(--x-font-size-sm,0.9375rem)` | Font size |
| `--x-multi-combobox-panel-bg` | `var(--x-color-bg,#ffffff)` | Panel background |
| `--x-multi-combobox-panel-max-height` | `16rem` | Panel max scroll height |
| `--x-multi-combobox-option-active-bg` | `var(--x-color-primary,#3b82f6)` | Keyboard-highlighted option |
| `--x-multi-combobox-chip-gap` | `0.25rem` | Gap between chips |
| `--x-multi-combobox-error-color` | `var(--x-color-danger,#dc2626)` | Inline error text + invalid border |

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
- When the chip that holds focus is removed, focus moves to the input
- When the `error` attribute is set, the input gets `aria-invalid="true"` and `aria-describedby="error"`, and the `error` part is an assertive live region (`role="alert"`) so screen readers announce the validation message

## Form participation

`x-multi-combobox` is a form-associated custom element (via `ElementInternals`). In a `<form>`:

- The selected set is submitted under its `name` as a string in the value format (comma-separated, or a JSON array in `json`), and appears in `FormData`. No hidden input is needed.
- Constraint validation is honoured: a `required` combobox with no selection reports `valueMissing` and **blocks submission**; setting the `error` attribute reports a `customError`. This is what `x-form` and native submission gate on.
- `form.reset()` clears the selection (mirroring `x-form-field`) **and drops the `error` attribute**, so a stale validation message does not survive the reset. `<fieldset disabled>` disables it via `formDisabledCallback`.

The framework adapters expose the array value as a form control: Vue `v-model` (`string[]`), Svelte `bind:value`, Angular `ControlValueAccessor`, and React/Solid controlled `value` / `defaultValue`. Each binding sets the `value` property, so the array round-trips in both value formats. In `csv`, an array with a value that a comma-separated list cannot hold changes nothing and sends `x-multi-combobox-error`.

### Validation API

Like a native form control, `x-multi-combobox` exposes read-only `validity`, `validationMessage`, `willValidate`, `form` and `labels`, plus `checkValidity()` and `reportValidity()`. All seven delegate to the element's `ElementInternals`, so `el.checkValidity()` answers the same question a submit attempt would.

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

### Values with a comma

```html
<x-multi-combobox value-format="json" value='["Liefdadigheid, religieus en non-profit"]'>
  <option value="Liefdadigheid, religieus en non-profit">Liefdadigheid, religieus en non-profit</option>
  <option value="Onderwijs">Onderwijs</option>
</x-multi-combobox>
```

```js
el.value = ["Liefdadigheid, religieus en non-profit", "Onderwijs"];
el.value; // ["Liefdadigheid, religieus en non-profit", "Onderwijs"]
```
