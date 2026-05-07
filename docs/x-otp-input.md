# x-otp-input

A native Web Component for entering one-time passwords / verification codes. Renders a row of single-character "slot" inputs with auto-advance, paste distribution, and full keyboard navigation. Form-associated, so it works inside `<x-form>` automatically.

## Tag name

```html
<x-otp-input></x-otp-input>
```

---

## Observed attributes

| Attribute      | Type    | Default          | Description |
|----------------|---------|------------------|-------------|
| `name`         | string  | `""`             | Form field name. Read by `<x-form>` when collecting submit values. |
| `value`        | string  | `""`             | Current code. Always normalized: filtered by `type` and truncated to `length`. |
| `length`       | number  | `6`              | Number of slots. Clamped to `[1, 12]`. |
| `type`         | enum    | `numeric`        | Allowed character set: `numeric` ∣ `alphanumeric` ∣ `alpha`. |
| `mask`         | boolean | false            | Renders each slot as a password input (dots). |
| `disabled`     | boolean | false            | Disables all slots and prevents form submission. |
| `readonly`     | boolean | false            | Display only — no edit. |
| `required`     | boolean | false            | Required for form submission. Fails validity unless fully filled. |
| `autofocus`    | boolean | false            | Focuses the first empty slot on connect. |
| `label`        | string  | `""`             | `aria-label` for the slot group. |
| `placeholder`  | string  | `""`             | Single-character placeholder shown in each empty slot. |
| `error`        | string  | `""`             | Error message — sets `aria-invalid` on slots and a `customError` validity flag. Set by `x-form.setFieldError`. |

Boolean attributes follow standard HTML conventions: presence means true, absence means false. Length/type changes rebuild the slot DOM; all other attribute changes patch existing slots.

---

## Properties

| Property      | Type    | Reflects attribute |
|---------------|---------|--------------------|
| `name`        | string  | `name`             |
| `value`       | string  | `value`            |
| `length`      | number  | `length`           |
| `type`        | string  | `type`             |
| `mask`        | boolean | `mask`             |
| `disabled`    | boolean | `disabled`         |
| `readonly`    | boolean | `readonly`         |
| `required`    | boolean | `required`         |
| `autofocus`   | boolean | `autofocus`        |
| `label`       | string  | `label`            |
| `placeholder` | string  | `placeholder`      |
| `error`       | string  | `error`            |

Setting `value` programmatically distributes characters across slots and triggers a re-render. Setting `length` or `type` rebuilds the slot DOM.

---

## Methods

| Method | Signature | Description |
|--------|-----------|-------------|
| `focus()`        | `() → void`     | Focuses the first empty slot. If all slots are filled, focuses the last slot. |
| `clear()`        | `() → void`     | Clears the value and focuses slot 0. |
| `checkValidity()`| `() → boolean`  | Proxies to `ElementInternals.checkValidity()`. |
| `reportValidity()`| `() → boolean` | Proxies to `ElementInternals.reportValidity()` — surfaces native validation UI on invalid state. |

---

## Events

| Event                  | Cancelable | Detail                                             | When |
|------------------------|------------|----------------------------------------------------|------|
| `x-otp-input-input`    | no         | `{name, value, complete}`                          | On every keystroke or paste. `complete` is true when value reaches `length`. |
| `x-otp-input-change`   | no         | `{name, value, complete}`                          | When focus leaves the component, if the value changed since focus entered. |
| `x-otp-input-complete` | no         | `{name, value}`                                    | Once, when the user fills the last empty slot. |

---

## Slots

This component does not expose any light-DOM slots. The visible slot inputs are constructed inside the shadow root.

---

## CSS custom properties

| Property                              | Default                                                                    | Description |
|---------------------------------------|----------------------------------------------------------------------------|-------------|
| `--x-otp-input-slot-size`             | `2.75rem`                                                                  | Width and height of each slot box. |
| `--x-otp-input-gap`                   | `var(--x-space-sm, 0.5rem)`                                                | Gap between slots. |
| `--x-otp-input-bg`                    | `var(--x-color-surface, #ffffff)`                                          | Slot background. |
| `--x-otp-input-color`                 | `var(--x-color-text, #111827)`                                             | Slot text colour. |
| `--x-otp-input-border`                | `1px solid var(--x-color-border, #d1d5db)`                                 | Slot border. |
| `--x-otp-input-border-radius`         | `var(--x-radius-md, 6px)`                                                  | Slot corner radius. |
| `--x-otp-input-focus-ring-color`      | `var(--x-color-primary, #2563eb)`                                          | Focus border + ring colour. |
| `--x-otp-input-error-color`           | `var(--x-color-danger, #dc2626)`                                           | Error border + ring colour. |
| `--x-otp-input-disabled-opacity`      | `var(--x-opacity-disabled, 0.45)`                                          | Opacity for the disabled state. |
| `--x-otp-input-font-family`           | `var(--x-font-family-mono, ui-monospace, SFMono-Regular, Menlo, monospace)`| Slot font. |
| `--x-otp-input-font-size`             | `1.25rem`                                                                  | Slot font size. |
| `--x-otp-input-font-weight`           | `var(--x-font-weight-semibold, 600)`                                       | Slot font weight. |

---

## Parts

| Part   | Element |
|--------|---------|
| `root` | The wrapper `<div role="group">` containing all slots. |
| `slot` | Each slot `<input>` element. |

Use `::part(slot)` to style slots from outside the shadow root, e.g. `x-otp-input::part(slot) { letter-spacing: 0; }`.

---

## Shadow DOM structure

```html
<div part="root" role="group" aria-label="…">
  <input part="slot" data-index="0" maxlength="1" inputmode="numeric"
         autocomplete="one-time-code" autocapitalize="off" autocorrect="off"
         aria-label="Digit 1 of 6" />
  <input part="slot" data-index="1" maxlength="1" inputmode="numeric"
         autocomplete="off" autocapitalize="off" autocorrect="off"
         aria-label="Digit 2 of 6" />
  …
</div>
```

The first slot uses `autocomplete="one-time-code"` so iOS/Safari and Chrome on Android pick up SMS codes automatically. Subsequent slots use `autocomplete="off"` to avoid autofill conflicts.

---

## Keyboard interaction

| Key            | Effect |
|----------------|--------|
| Type a character | Inserts into focused slot, then auto-advances to the next slot. |
| Backspace      | If the focused slot is empty, clears the previous slot and moves focus there. Otherwise, native delete behaviour. |
| ←              | Move focus to the previous slot. |
| →              | Move focus to the next slot. |
| Home           | Move focus to slot 0. |
| End            | Move focus to the last slot. |
| Paste          | Distributes pasted text across slots starting from the focused slot, filtering invalid characters per `type`. |

---

## Validity

The component is form-associated via `ElementInternals`:

- **Empty + required** → `valueMissing` flag, message: `"Please fill in this field."`
- **Partial + required** → `tooShort` flag, message: `"Please enter all N characters."`
- **`error` attribute set** → `customError` flag with the error message as text.
- All other states are valid.

`<x-form>` calls `reportValidity()` before dispatching `x-form-submit`, so an invalid OTP blocks submission and shows native validation UI.

---

## Accessibility

- Wrapper has `role="group"` with `aria-label` from the `label` attribute.
- Each slot has `aria-label="Digit N of M"` (or `"Character N of M"` for non-numeric types).
- The first slot gets `aria-required="true"` when `required` is set.
- All slots get `aria-invalid="true"` when `error` is set.
- The component honours `prefers-reduced-motion` (no focus-ring transition) and `prefers-color-scheme: dark`.
- Touch targets are at least 44×44px on coarse pointers.

---

## Usage examples

### Basic 6-digit code

```html
<x-otp-input id="otp" label="Verification code" autofocus></x-otp-input>

<script>
  document.getElementById('otp').addEventListener('x-otp-input-complete', e => {
    console.log('Got code:', e.detail.value);
  });
</script>
```

### Inside `<x-form>`

```html
<x-form id="verify-form">
  <x-otp-input name="code" length="6" required label="Enter the code we texted you"></x-otp-input>
  <button type="submit">Verify</button>
</x-form>

<script>
  document.getElementById('verify-form').addEventListener('x-form-submit', async e => {
    try {
      await api.verify(e.detail.values.code);
    } catch (err) {
      // Server-side error injection — sets error attribute on the OTP input.
      document.getElementById('verify-form').setFieldError('code', err.message);
    }
  });
</script>
```

### Alphanumeric, masked, custom length

```html
<x-otp-input
  name="recovery"
  type="alphanumeric"
  length="8"
  mask
  placeholder="·"
  label="Recovery code">
</x-otp-input>
```

### Programmatic API

```js
const otp = document.querySelector('x-otp-input');
otp.value = '123456';   // distributes across slots
otp.focus();             // focuses first empty slot
otp.clear();             // clears value, focuses slot 0
```

### Custom theming

```css
x-otp-input {
  --x-otp-input-slot-size: 3.5rem;
  --x-otp-input-gap: 0.75rem;
  --x-otp-input-font-size: 1.5rem;
}

x-otp-input::part(slot) {
  letter-spacing: 0;
}
```
