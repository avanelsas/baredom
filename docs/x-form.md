# x-form

A native Web Component that wraps a `<form>` element to handle submission, validation gating, loading state, and server-side error injection. Consumers slot in their own `x-form-field`, `x-checkbox`, and submit buttons.

## Tag name

```html
<x-form></x-form>
```

---

## Observed attributes

| Attribute      | Type    | Default | Description |
|----------------|---------|---------|-------------|
| `loading`      | boolean | false   | Prevents submit dispatch and sets `aria-busy="true"` on the shadow form |
| `novalidate`   | boolean | false   | Skips constraint validation before dispatching the submit event |
| `autocomplete` | string  | `"on"`  | Forwarded directly to the shadow `<form>` element (`"on"` \| `"off"`) |

Boolean attributes follow standard HTML conventions: presence means true, absence means false.

---

## Properties

| Property       | Type    | Reflects attribute |
|----------------|---------|-------------------|
| `loading`      | boolean | `loading` |
| `novalidate`   | boolean | `novalidate` |
| `autocomplete` | string  | `autocomplete` |

---

## Methods

| Method | Signature | Description |
|--------|-----------|-------------|
| `submit()` | `() → void` | Programmatically submits the form via `requestSubmit()` — triggers the full validation + event pipeline |
| `reset()` | `() → void` | Calls `form.reset()` on the shadow form — triggers `formResetCallback` on associated form-associated elements |
| `setFieldError(name, msg)` | `(string, string\|null) → void` | Sets the `error` attribute on the first light-DOM element matching `[name="…"]`. Pass `""` or `null` to clear. |
| `clearErrors()` | `() → void` | Removes the `error` attribute from all light-DOM elements that currently have it |

---

## Events

| Event | Cancelable | Detail | When |
|-------|-----------|--------|------|
| `x-form-submit` | yes | `{values: Object}` | After validation passes (or `novalidate`), and not while `loading` is set |
| `x-form-reset` | no | `{}` | After the shadow `form.reset()` completes |

### `x-form-submit` detail

`values` is a plain JS object keyed by field `name`. Collection strategy:

- Queries `this.querySelectorAll("[name]:not([disabled])")` in the light DOM.
- Checkbox/radio elements (detected by `type="checkbox"`, `type="radio"`, or `tagName === "x-checkbox"`) are only included when `checked === true`.
- All other fields contribute `field.value || ""`.

---

## Slots

| Slot | Description |
|------|-------------|
| (default) | Form fields, submit buttons, and any other content |

x-form imposes no layout on slotted content. Use the `--x-form-gap` and `--x-form-width` CSS custom properties to control the flex column layout of the shadow `<form>`.

---

## CSS custom properties

| Property | Default | Description |
|----------|---------|-------------|
| `--x-form-gap` | `1rem` | `gap` between flex items in the shadow `[part=root]` |
| `--x-form-width` | `100%` | Width of the shadow `<form>` |

---

## Parts

| Part | Element |
|------|---------|
| `root` | The shadow `<form>` element |

Use `::part(root)` to style the inner form container directly.

---

## Shadow DOM structure

```html
<form part="root" novalidate autocomplete="on">
  <slot></slot>
</form>
```

The shadow `<form>` always has the `novalidate` HTML attribute so the browser does not show native constraint UI automatically. Validation is run manually via `form.reportValidity()` during the submit lifecycle, which still surfaces browser/ElementInternals validation UI on invalid fields.

---

## Submit lifecycle

1. User clicks a `type="submit"` button inside the slot, or `el.submit()` is called.
2. `requestSubmit()` fires the native `submit` event on the shadow form.
3. The handler always calls `event.preventDefault()`.
4. If `loading` is set → bail silently (no event dispatched).
5. If `novalidate` is **not** set → call `form.reportValidity()`:
   - Returns `false` → bail (browser/ElementInternals surfaces validation UI).
   - Returns `true` → continue.
6. Collect values from light DOM.
7. Dispatch cancelable `x-form-submit` with `{values}`.
8. Consumer handles submission, sets `loading`, then clears it and optionally calls `setFieldError`.

---

## Accessibility

- `aria-busy="true"` is set on the shadow `<form>` while `loading` is active.
- Field-level errors are communicated via the `error` attribute on `x-form-field` (which sets `aria-invalid` and shows an ARIA live region).
- `novalidate` on the host skips constraint validation but does not suppress field-level `error` attributes set by the consumer.

---

## Usage examples

### Basic login form

```html
<x-form id="my-form">
  <x-form-field label="Email"    name="email"    type="email"    required></x-form-field>
  <x-form-field label="Password" name="password" type="password" required></x-form-field>
  <button type="submit">Sign in</button>
</x-form>

<script>
  document.getElementById('my-form').addEventListener('x-form-submit', e => {
    console.log(e.detail.values); // { email: '...', password: '...' }
  });
</script>
```

### Async submit with loading state

```js
form.addEventListener('x-form-submit', async e => {
  e.preventDefault();
  form.setAttribute('loading', '');
  try {
    await api.login(e.detail.values);
  } catch (err) {
    form.setFieldError('email', err.message);
  } finally {
    form.removeAttribute('loading');
  }
});
```

### Server-side error injection

```js
// After receiving a 422 response:
form.setFieldError('email', 'An account with this email already exists.');
form.setFieldError('password', 'Password does not meet requirements.');

// Clear all errors (e.g. on reset):
form.clearErrors();
```

### Disable browser autocomplete

```html
<x-form autocomplete="off">
  <!-- fields -->
</x-form>
```

### Skip HTML5 validation

```html
<x-form novalidate>
  <!-- fields — submitted even if required fields are empty -->
</x-form>
```

### Custom layout spacing

```css
x-form {
  --x-form-gap: 1.5rem;
  --x-form-width: 400px;
}
```
