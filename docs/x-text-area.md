# x-text-area

A form-associated multi-line text input web component. Wraps a native `<textarea>` inside shadow DOM with support for labels, hints, error states, and full HTML form integration.

## Tag Name

```html
<x-text-area></x-text-area>
```

## Attributes

| Attribute      | Type    | Default      | Description |
|---------------|---------|--------------|-------------|
| `label`       | string  | `""`         | Visible label text. Hidden when absent or empty. |
| `name`        | string  | `""`         | Form field name used in `FormData`. |
| `value`       | string  | `""`         | Current text value. Syncs with textarea's live value. |
| `placeholder` | string  | `""`         | Placeholder text shown when the field is empty. |
| `hint`        | string  | `""`         | Helper text displayed below the textarea. |
| `error`       | string  | `""`         | Error message. Non-empty triggers the invalid visual state. |
| `disabled`    | boolean | absent       | Disables the textarea (presence = true). |
| `readonly`    | boolean | absent       | Makes the textarea read-only (presence = true). |
| `required`    | boolean | absent       | Marks the field as required for form validation. |
| `rows`        | integer | `3`          | Number of visible text lines. |
| `maxlength`   | integer | absent       | Maximum number of characters allowed. |
| `minlength`   | integer | absent       | Minimum number of characters required. |
| `autocomplete`| string  | `""`         | Autocomplete hint (e.g. `"off"`, `"on"`). |
| `resize`      | enum    | `"vertical"` | Controls CSS resize handle. One of: `"none"`, `"vertical"`, `"horizontal"`, `"both"`. |

## Properties

| Property      | Type    | Reflects   | Description |
|--------------|---------|------------|-------------|
| `value`      | string  | `value`    | Live text value. Getter reads from `textarea.value`. Setter updates both attribute and DOM. |
| `name`       | string  | `name`     | Form field name. |
| `disabled`   | boolean | `disabled` | Disabled state. |
| `readOnly`   | boolean | `readonly` | Read-only state. |
| `required`   | boolean | `required` | Required state. |
| `rows`       | number  | `rows`     | Visible row count (default `3`). |
| `maxLength`  | number  | `maxlength`| Maximum character count. Returns `null` when not set. |
| `minLength`  | number  | `minlength`| Minimum character count. Returns `null` when not set. |
| `autocomplete`| string | `autocomplete` | Autocomplete hint. |

## Events

| Event                | Bubbles | Composed | Cancelable | Detail |
|---------------------|---------|----------|------------|--------|
| `x-text-area-change-request` | `true` | `true` | **`true`** | `{ name: string, value: string, previousValue: string }` |
| `x-text-area-input` | `true`  | `true`   | `false`    | `{ name: string, value: string }` |
| `x-text-area-change`| `true`  | `true`   | `false`    | `{ name: string, value: string }` |

- **`x-text-area-change-request`** — fires before the value updates. Call `preventDefault()` to block the change (controlled mode).
- **`x-text-area-input`** — fires on every keystroke (mirrors native `input` event).
- **`x-text-area-change`** — fires when the value is committed (mirrors native `change` event, e.g. on blur).

## Shadow DOM Parts

Use `::part()` to style internal elements from outside the component.

| Part               | Element      | Description |
|-------------------|--------------|-------------|
| `field`           | `<div>`      | Outer flex container (column layout). |
| `label`           | `<label>`    | Label element. Hidden when `label` attribute is absent. |
| `textarea-wrapper`| `<div>`      | Wrapper around the textarea. |
| `textarea`        | `<textarea>` | The native textarea element. |
| `hint`            | `<span>`     | Hint text. Hidden when `hint` attribute is absent. |
| `error`           | `<span>`     | Error message. Hidden when `error` attribute is absent. |

## CSS Custom Properties

All properties are set on `:host` and can be overridden per-instance or globally.

| Property                          | Default (light)     | Description |
|----------------------------------|---------------------|-------------|
| `--x-text-area-label-color`      | `#374151`           | Label text color. |
| `--x-text-area-label-font-size`  | `0.875rem`          | Label font size. |
| `--x-text-area-bg`               | `#ffffff`           | Textarea background color. |
| `--x-text-area-color`            | `#111827`           | Textarea text color. |
| `--x-text-area-border`           | `1px solid #d1d5db` | Textarea border. |
| `--x-text-area-border-radius`    | `6px`               | Corner radius. |
| `--x-text-area-padding`          | `0.5rem 0.75rem`    | Inner padding. |
| `--x-text-area-focus-ring-color` | `#2563eb`           | Focus ring and border color on focus. |
| `--x-text-area-error-color`      | `#dc2626`           | Border and text color in error state. |
| `--x-text-area-hint-color`       | `#6b7280`           | Hint text color. |
| `--x-text-area-disabled-opacity` | `0.45`              | Opacity when disabled. |
| `--x-text-area-min-height`       | `5rem`              | Minimum height of the textarea. |
| `--x-text-area-font-size`        | `1rem`              | Font size of the textarea text. |
| `--x-text-area-resize`           | `vertical`          | CSS `resize` property on the textarea. |

Dark mode values are set automatically via `@media (prefers-color-scheme: dark)`.

## Accessibility

- The `<label>` is associated with the `<textarea>` via `for`/`id` and `aria-labelledby`.
- `aria-required` is set to `"true"` when the `required` attribute is present.
- `aria-invalid` is set to `"true"` when the `error` attribute is non-empty.
- `aria-describedby` dynamically includes `hint` and/or `error` element IDs.
- The error `<span>` has `role="alert"` and `aria-live="assertive"` for screen reader announcements.
- The hint `<span>` has `aria-live="polite"`.

## Form Integration

`x-text-area` is a form-associated custom element. It integrates natively with `<form>`:

- Participates in form submission via `FormData` using the `name` attribute.
- Responds to form `reset` — clears the value.
- Disabled by the form when the containing `<fieldset disabled>` is present.
- Constraint validation via ElementInternals (supports `required`, `maxlength`, custom `error`).

## Usage Examples

### Basic

```html
<x-text-area
  name="bio"
  label="Biography"
  placeholder="Tell us about yourself"
  rows="5">
</x-text-area>
```

### With hint and error

```html
<x-text-area
  name="feedback"
  label="Feedback"
  hint="Be as specific as possible."
  error="Feedback is required."
  required>
</x-text-area>
```

### With character limit

```html
<x-text-area
  name="summary"
  label="Summary"
  maxlength="280"
  rows="4">
</x-text-area>
```

### Non-resizable

```html
<x-text-area name="notes" resize="none" rows="3"></x-text-area>
```

### Listening to events

```js
document.querySelector('x-text-area').addEventListener('x-text-area-input', (e) => {
  console.log('name:', e.detail.name, 'value:', e.detail.value);
});
```

### Setting value programmatically

```js
const ta = document.querySelector('x-text-area');
ta.value = 'Prepopulated content';
```

### Custom styling

```css
x-text-area {
  --x-text-area-border-radius: 12px;
  --x-text-area-focus-ring-color: #8b5cf6;
  --x-text-area-min-height: 8rem;
}
```
