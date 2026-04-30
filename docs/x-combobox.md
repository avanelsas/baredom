# x-combobox

A single-select combobox with type-ahead filtering. The user types to filter a list of `<option>` children; the value must match one of the provided options.

## Tag name

```html
<x-combobox>...</x-combobox>
```

## Observed attributes

| Attribute     | Type    | Default        | Description |
|---------------|---------|----------------|-------------|
| `value`       | string  | `""`           | Selected option value |
| `placeholder` | string  | `""`           | Input placeholder text |
| `name`        | string  | `""`           | Form field name |
| `disabled`    | boolean | `false`        | Disables the component |
| `required`    | boolean | `false`        | Marks as required for form validation |
| `open`        | boolean | `false`        | Controls panel visibility |
| `placement`   | enum    | `"bottom-start"` | Panel position relative to input |

### Placement values

`bottom-start` | `bottom-end` | `top-start` | `top-end`

## Properties (camelCase, reflect attributes)

| Property      | Type    | Reflected attribute |
|---------------|---------|---------------------|
| `value`       | string  | `value` |
| `placeholder` | string  | `placeholder` |
| `name`        | string  | `name` |
| `disabled`    | boolean | `disabled` |
| `required`    | boolean | `required` |
| `open`        | boolean | `open` |
| `placement`   | string  | `placement` |

## Public methods

| Method   | Description |
|----------|-------------|
| `show()` | Opens the dropdown panel (source: `"programmatic"`) |
| `hide()` | Closes the dropdown panel (source: `"programmatic"`) |

## Events

### `x-combobox-change-request`

Fired before the selected value changes. **Cancelable** — call `event.preventDefault()` to block the update (controlled mode).

| Property | Value |
|----------|-------|
| `bubbles` | `true` |
| `composed` | `true` |
| `cancelable` | **`true`** |
| `detail.value` | Proposed new value (string) |
| `detail.label` | Proposed new label (string) |
| `detail.previousValue` | Current value before change (string) |

### `x-combobox-change`

Fired when the selected value changes (option selection or clear).

| Property | Value |
|----------|-------|
| `bubbles` | `true` |
| `composed` | `true` |
| `cancelable` | `false` |
| `detail.value` | Selected option value (string) |
| `detail.label` | Selected option label (string) |

### `x-combobox-input`

Fired on every filter keystroke.

| Property | Value |
|----------|-------|
| `bubbles` | `true` |
| `composed` | `true` |
| `cancelable` | `false` |
| `detail.query` | Current filter text (string) |

### `x-combobox-toggle`

Fired before the panel opens or closes. **Cancelable** — call `event.preventDefault()` to abort.

| Property | Value |
|----------|-------|
| `bubbles` | `true` |
| `composed` | `true` |
| `cancelable` | `true` |
| `detail.open` | Target open state (boolean) |
| `detail.source` | What triggered: `"focus"`, `"pointer"`, `"keyboard"`, `"input"`, `"escape"`, `"select"`, `"outside-click"`, `"focusout"`, `"programmatic"` |

## Slots

| Slot      | Description |
|-----------|-------------|
| (default) | `<option>` elements providing the available choices |

## Shadow parts

| Part       | Description |
|------------|-------------|
| `wrapper`  | Outer container (input + clear + chevron) |
| `input`    | Text input for filtering |
| `clear`    | Clear selection button |
| `chevron`  | Dropdown arrow indicator |
| `panel`    | Dropdown listbox container |
| `option`   | Individual option item in panel |
| `empty-msg`| "No matches" message when filter has no results |

## CSS custom properties

### Layout

| Property | Default | Description |
|----------|---------|-------------|
| `--x-combobox-height` | `2.25rem` | Input height |
| `--x-combobox-radius` | `var(--x-radius-md, 6px)` | Border radius |
| `--x-combobox-font-size` | `var(--x-font-size-sm, 0.9375rem)` | Font size |
| `--x-combobox-padding` | `0 0.625rem` | Input padding |
| `--x-combobox-panel-max-height` | `16rem` | Max dropdown height |
| `--x-combobox-panel-offset` | `4px` | Gap between input and panel |
| `--x-combobox-panel-radius` | `var(--x-radius-md, 8px)` | Panel border radius |
| `--x-combobox-option-padding` | `0.5rem 0.625rem` | Option padding |

### Colors

| Property | Default | Description |
|----------|---------|-------------|
| `--x-combobox-bg` | `var(--x-color-surface)` | Input background |
| `--x-combobox-fg` | `var(--x-color-text)` | Text color |
| `--x-combobox-placeholder` | `var(--x-color-text-muted)` | Placeholder color |
| `--x-combobox-border` | `1px solid var(--x-color-border)` | Input border |
| `--x-combobox-focus-ring` | `var(--x-color-focus-ring)` | Focus ring color |
| `--x-combobox-panel-bg` | `var(--x-color-bg)` | Panel background |
| `--x-combobox-option-hover-bg` | `var(--x-color-surface-hover)` | Option hover |
| `--x-combobox-option-active-bg` | `var(--x-color-primary)` | Active/highlighted option |

### Motion

| Property | Default | Description |
|----------|---------|-------------|
| `--x-combobox-transition-duration` | `var(--x-transition-duration, 150ms)` | Animation duration |
| `--x-combobox-transition-easing` | `var(--x-transition-easing, ease)` | Animation easing |

## Accessibility

- Input has `role="combobox"`, `aria-expanded`, `aria-autocomplete="list"`, `aria-controls` pointing to listbox ID
- Panel has `role="listbox"` with a unique auto-generated ID
- Each option has `role="option"`, unique ID, `aria-selected` for current value
- Active/highlighted option tracked via `aria-activedescendant` on input
- `focusin` on input opens the panel immediately — keyboard users get immediate feedback
- `Escape` closes panel and reverts input text to current selection

**Note:** `required` reflects the attribute for styling purposes (e.g. `:host([required])`) but native form validation via `ElementInternals` is not yet implemented.

## Keyboard navigation

| Key | Action |
|-----|--------|
| Arrow Down | Open panel (if closed) or move to next option |
| Arrow Up | Open panel (if closed) or move to previous option |
| Enter | Select highlighted option, close panel |
| Escape | Close panel, revert input |
| Home | Jump to first option |
| End | Jump to last option |

## Filtering

- Case-insensitive substring match on option label text
- Matching text is highlighted with bold in the dropdown
- Empty filter shows all options
- Value only changes when user explicitly selects (Enter or click)
- On close without selection, input reverts to current selected label

## Usage examples

### Basic

```html
<x-combobox placeholder="Select a country">
  <option value="us">United States</option>
  <option value="uk">United Kingdom</option>
  <option value="nl">Netherlands</option>
</x-combobox>
```

### Pre-selected value

```html
<x-combobox value="nl" placeholder="Select a country">
  <option value="us">United States</option>
  <option value="nl">Netherlands</option>
</x-combobox>
```

### Disabled

```html
<x-combobox disabled placeholder="Not available">
  <option value="a">Option A</option>
</x-combobox>
```

### In a form

```html
<form>
  <x-combobox name="country" required placeholder="Required">
    <option value="us">United States</option>
    <option value="uk">United Kingdom</option>
  </x-combobox>
  <button type="submit">Submit</button>
</form>
```

### Programmatic control

```js
const cb = document.querySelector('x-combobox');
cb.show();   // open panel
cb.hide();   // close panel
cb.value = 'uk';  // set selection
```
