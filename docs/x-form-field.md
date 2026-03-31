# x-form-field

A self-contained, form-associated text-input field with a label, optional hint, and optional error message. The component owns its internal `<input>` element inside shadow DOM. Consumers control validity by setting the `error` attribute.

---

## Tag name

```html
<x-form-field></x-form-field>
```

---

## Observed attributes

| Attribute      | Type    | Default | Notes                                           |
|----------------|---------|---------|--------------------------------------------------|
| `label`        | string  | `""`    | Visible label text (hidden when empty)           |
| `type`         | enum    | `"text"`| One of: `text`, `email`, `password`, `url`, `number`, `tel` |
| `name`         | string  | `""`    | Form field name (passed to ElementInternals)     |
| `value`        | string  | `""`    | Current input value                              |
| `placeholder`  | string  | `""`    | Input placeholder                                |
| `hint`         | string  | `""`    | Helper text below input (hidden when empty)      |
| `error`        | string  | `""`    | Error message (hidden when empty; marks invalid) |
| `disabled`     | boolean | `false` | Disables the field                               |
| `readonly`     | boolean | `false` | Makes the field read-only                        |
| `required`     | boolean | `false` | Marks required (native + ARIA)                   |
| `autocomplete` | string  | `""`    | Passed through to inner `<input>`                |

---

## Properties

| Property      | Type    | Reflects attribute |
|---------------|---------|--------------------|
| `value`       | string  | `value`            |
| `label`       | string  | `label`            |
| `type`        | string  | `type`             |
| `name`        | string  | `name`             |
| `placeholder` | string  | `placeholder`      |
| `autocomplete`| string  | `autocomplete`     |
| `disabled`    | boolean | `disabled`         |
| `readOnly`    | boolean | `readonly`         |
| `required`    | boolean | `required`         |

### Value sync note

The `value` property setter updates both the `value` attribute and the shadow `<input>` value. On user input, the component does **not** reflect back to the `value` attribute (avoids cursor jumps). The attribute only changes when set programmatically.

---

## Events

| Event                  | Bubbles | Composed | Cancelable | Detail                          |
|------------------------|---------|----------|------------|----------------------------------|
| `x-form-field-input`   | true    | true     | false      | `{ name: string, value: string }` |
| `x-form-field-change`  | true    | true     | false      | `{ name: string, value: string }` |

`x-form-field-input` fires on every keystroke (mirrors native `input` event).
`x-form-field-change` fires on blur/commit (mirrors native `change` event).

---

## Shadow DOM structure

```
<style>…</style>
<div part="field">
  <label part="label" id="label" for="input">…</label>
  <div part="input-wrapper">
    <input part="input" id="input"
           aria-labelledby="label"
           aria-describedby="hint error" />
  </div>
  <span part="hint"  id="hint"  aria-live="polite">…</span>
  <span part="error" id="error" role="alert" aria-live="assertive">…</span>
</div>
```

`aria-describedby` is set conditionally: includes only present ids (`hint`, `error`, or both).

---

## CSS custom properties

| Property                              | Default (light)          |
|---------------------------------------|--------------------------|
| `--x-form-field-label-color`          | `#374151`                |
| `--x-form-field-label-font-size`      | `0.875rem`               |
| `--x-form-field-input-bg`             | `#ffffff`                |
| `--x-form-field-input-color`          | `#111827`                |
| `--x-form-field-input-border`         | `1px solid #d1d5db`      |
| `--x-form-field-input-border-radius`  | `6px`                    |
| `--x-form-field-input-padding`        | `0.5rem 0.75rem`         |
| `--x-form-field-focus-ring-color`     | `#2563eb`                |
| `--x-form-field-error-color`          | `#dc2626`                |
| `--x-form-field-hint-color`           | `#6b7280`                |
| `--x-form-field-disabled-opacity`     | `0.45`                   |

Dark-mode defaults are applied automatically via `@media (prefers-color-scheme: dark)`.

---

## Form association

`x-form-field` sets `formAssociated = true`. It participates in `<form>` submit via `ElementInternals.setFormValue()`. The field name comes from the `name` attribute.

| Callback             | Behaviour                                          |
|----------------------|----------------------------------------------------|
| `formDisabledCallback(d)` | Sets/removes `disabled` attribute, re-renders |
| `formResetCallback()`     | Clears input value and `value` attribute, calls `setFormValue("")` |

Validity is set via `setValidity`:
- `error` attribute non-empty → `{ customError: true }` with the error message
- `required` attribute set + empty value → `{ valueMissing: true }` with `"Please fill in this field."`
- otherwise → `{}` (valid)

---

## Accessibility

- Inner `<input>` is labelled via `aria-labelledby="label"`.
- `aria-describedby` links to hint and/or error spans when present.
- `aria-invalid="true"` is set on the input when an error is present.
- `aria-required` mirrors the `required` attribute.
- Error span has `role="alert"` and `aria-live="assertive"`.
- Hint span has `aria-live="polite"`.
- Animations respect `@media (prefers-reduced-motion: reduce)`.

---

## Usage examples

### Basic

```html
<x-form-field
  label="Email address"
  type="email"
  name="email"
  placeholder="you@example.com"
  hint="We will never share your email.">
</x-form-field>
```

### With error

```html
<x-form-field
  label="Password"
  type="password"
  name="password"
  error="Password must be at least 8 characters.">
</x-form-field>
```

### Listening to events

```js
const field = document.querySelector('x-form-field');

field.addEventListener('x-form-field-input', e => {
  console.log('typing:', e.detail.name, e.detail.value);
});

field.addEventListener('x-form-field-change', e => {
  console.log('committed:', e.detail.name, e.detail.value);
});
```

### Setting value programmatically

```js
// Via property (also syncs the shadow input)
field.value = 'hello@example.com';

// Via attribute
field.setAttribute('value', 'hello@example.com');
```

### Setting error programmatically

```js
// Show error
field.setAttribute('error', 'Invalid email address.');

// Clear error
field.removeAttribute('error');
```

### Inside a form

```html
<form id="my-form">
  <x-form-field label="Name" name="name"></x-form-field>
  <button type="submit">Submit</button>
</form>

<script>
  document.getElementById('my-form').addEventListener('submit', e => {
    e.preventDefault();
    const data = Object.fromEntries(new FormData(e.target));
    console.log(data); // { name: "…" }
  });
</script>
```

### CSS theming

```css
x-form-field {
  --x-form-field-input-border-radius: 0;
  --x-form-field-focus-ring-color: #7c3aed;
  --x-form-field-error-color: #b91c1c;
}
```
