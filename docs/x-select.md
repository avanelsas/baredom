# x-select

A styled, accessible select input component that wraps a native `<select>` element inside shadow DOM. Options are provided as light DOM children and automatically synced into the internal select on slot change.

## Tag

```html
<x-select name="country" placeholder="Choose a country">
  <option value="us">United States</option>
  <option value="gb">United Kingdom</option>
  <option value="ca">Canada</option>
</x-select>
```

## Observed Attributes

| Attribute     | Type                  | Default | Description                                      |
|---------------|-----------------------|---------|--------------------------------------------------|
| `disabled`    | boolean (presence)    | false   | Disables the select; prevents interaction        |
| `required`    | boolean (presence)    | false   | Marks the field as required for form validation  |
| `size`        | `"sm"` \| `"md"` \| `"lg"` | `"md"` | Visual size variant                    |
| `placeholder` | string                | —       | Placeholder option shown when no value selected  |
| `value`       | string                | —       | Currently selected value (consumer-controlled)   |
| `name`        | string                | —       | Form field name forwarded to the internal select |
| `error`       | string                | —       | Inline validation message; shows the `error` part and marks the field invalid |

## Properties

| Property   | Type    | Reflects Attribute |
|------------|---------|--------------------|
| `disabled` | boolean | `disabled`         |
| `required` | boolean | `required`         |
| `value`    | string  | `value`            |
| `error`    | string  | `error`            |

## Events

| Event           | Bubbles | Composed | Cancelable | Detail                          |
|-----------------|---------|----------|------------|---------------------------------|
| `x-select-change-request` | yes | yes | **yes** | `{ value: string, label: string, previousValue: string }` |
| `select-change` | yes     | yes      | no         | `{ value: string, label: string }` |

- **`x-select-change-request`** — fires before the selection updates. Call `preventDefault()` to block the change (controlled mode).
- **`select-change`** — fires after the selection has changed.

## Slots

| Slot      | Description                                                                 |
|-----------|-----------------------------------------------------------------------------|
| (default) | `<option>` and `<optgroup>` elements. These are cloned into the internal `<select>` on slotchange. |

## CSS Custom Properties

| Property                        | Default (light)                       | Default (dark)   |
|---------------------------------|---------------------------------------|------------------|
| `--x-select-height-sm`          | `2rem`                                | —                |
| `--x-select-height-md`          | `2.5rem`                              | —                |
| `--x-select-height-lg`          | `3rem`                                | —                |
| `--x-select-radius`             | `0.5rem`                              | —                |
| `--x-select-font-size-sm`       | `0.75rem`                             | —                |
| `--x-select-font-size-md`       | `0.875rem`                            | —                |
| `--x-select-font-size-lg`       | `1rem`                                | —                |
| `--x-select-padding-inline`     | `0.75rem`                             | —                |
| `--x-select-bg`                 | `#ffffff`                             | `#1f2937`        |
| `--x-select-bg-disabled`        | `#f8fafc`                             | `#111827`        |
| `--x-select-fg`                 | `#0f172a`                             | `#f1f5f9`        |
| `--x-select-fg-disabled`        | `#94a3b8`                             | —                |
| `--x-select-placeholder-fg`     | `#94a3b8`                             | —                |
| `--x-select-border`             | `#cbd5e1`                             | `#374151`        |
| `--x-select-border-hover`       | `#94a3b8`                             | `#4b5563`        |
| `--x-select-border-focus`       | `#3b82f6`                             | `#60a5fa`        |
| `--x-select-chevron`            | `#64748b`                             | `#94a3b8`        |
| `--x-select-focus-ring`         | `#93c5fd`                             | —                |
| `--x-select-error-color`        | `#dc2626`                             | `#f87171`        |
| `--x-select-shadow`             | `0 1px 2px rgba(15,23,42,0.06)`       | —                |
| `--x-select-transition-duration`| `140ms`                               | —                |

## CSS Parts

| Part      | Element  | Description                                  |
|-----------|----------|----------------------------------------------|
| `wrapper` | `<div>`  | The styled container; receives size/disabled data attrs |
| `select`  | `<select>` | The native select element               |
| `chevron` | `<span>` | The dropdown arrow icon                      |
| `error`   | `<span>` | Inline validation message; hidden until the `error` attribute is set |

## Accessibility

- The internal `<select>` uses native browser accessibility — keyboard navigation, screen reader announcements, and OS-level option pickers are provided for free.
- When no `name` attribute is present, `aria-label` is set on the internal `<select>` using the placeholder text (or `"select"` as fallback).
- Disabled state is communicated natively via `<select disabled>`.
- Focus ring is applied via `:focus-within` on the wrapper using `--x-select-focus-ring`.
- When the `error` attribute is set, the internal `<select>` gets `aria-invalid="true"` and `aria-describedby` pointing at the `error` part, which is an assertive live region (`role="alert"`) so screen readers announce the validation message.

## Form participation

`x-select` is a form-associated custom element (via `ElementInternals`). When placed in a `<form>`:

- Its current selection is submitted under its `name`, and it appears in `FormData` — no hidden input needed.
- Constraint validation is honoured: a `required` select with no selection reports `valueMissing` and **blocks submission** (`form.reportValidity()` returns `false`); setting the `error` attribute reports a `customError` with that message. This is what `x-form` and native `<form>` submission gate on.
- `form.reset()` restores the markup-default `<option selected>` (dropping any controlled `value` attribute), mirroring a native `<select>`.
- `<fieldset disabled>` disables it via `formDisabledCallback`.

## Theming

Dark mode is handled automatically via `@media (prefers-color-scheme: dark)` inside the component's shadow styles. Override any CSS custom property on the host to customize:

```css
x-select {
  --x-select-border-focus: #a855f7;
  --x-select-focus-ring: #d8b4fe;
}
```

## Motion

Transitions on the wrapper respect `@media (prefers-reduced-motion: reduce)` — all transitions are set to `none` when reduced motion is preferred.

## Usage Examples

### Basic

```html
<x-select name="role" placeholder="Select a role">
  <option value="admin">Admin</option>
  <option value="editor">Editor</option>
  <option value="viewer">Viewer</option>
</x-select>
```

### Consumer-controlled value

```html
<x-select id="my-select" name="plan" value="pro">
  <option value="free">Free</option>
  <option value="pro">Pro</option>
  <option value="enterprise">Enterprise</option>
</x-select>

<script>
  const sel = document.getElementById('my-select');
  sel.addEventListener('select-change', e => {
    // Consumer decides whether to persist the selection
    sel.setAttribute('value', e.detail.value);
    console.log('Selected:', e.detail.value, e.detail.label);
  });
</script>
```

### Validation error

The `error` attribute renders an inline message and marks the field invalid. Inside
`x-form`, this is driven for you by `form.setFieldError(name, message)`.

```html
<x-select name="country" error="Please choose a country">
  <option value="us">United States</option>
  <option value="nl">Netherlands</option>
</x-select>
```

Clear it by removing the attribute (or `el.error = ''`).

### Disabled

```html
<x-select disabled name="region">
  <option value="us">United States</option>
</x-select>
```

### Sizes

```html
<x-select size="sm" name="sm-select">
  <option value="a">Small</option>
</x-select>

<x-select size="md" name="md-select">
  <option value="a">Medium (default)</option>
</x-select>

<x-select size="lg" name="lg-select">
  <option value="a">Large</option>
</x-select>
```

### Option groups

```html
<x-select name="city" placeholder="Choose a city">
  <optgroup label="North America">
    <option value="nyc">New York</option>
    <option value="la">Los Angeles</option>
  </optgroup>
  <optgroup label="Europe">
    <option value="lon">London</option>
    <option value="par">Paris</option>
  </optgroup>
</x-select>
```

## Rendering Invariants

- `DOM = f(attributes)` — render reads only from HTML attributes, never from mutable state.
- The `value` attribute is never auto-set by the component on user interaction.
- Options are cloned (not moved) from light DOM into the shadow `<select>`, so the light DOM children remain intact.
- The placeholder `<option>` is always the first child of the internal `<select>` and is never removed.
