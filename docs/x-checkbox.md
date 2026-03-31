# x-checkbox

An accessible, themeable checkbox control that supports checked, indeterminate, disabled, read-only, and required states. Fires cancelable request events before state changes, allowing host code to intercept.

---

## Tag

```html
<x-checkbox></x-checkbox>
```

---

## Attributes

| Attribute           | Type    | Default | Description                                                         |
|---------------------|---------|---------|---------------------------------------------------------------------|
| `checked`           | boolean | `false` | Whether the checkbox is checked. Setting `checked="false"` is treated as absent (unchecked). |
| `indeterminate`     | boolean | `false` | Indeterminate (mixed) state — overrides `checked` visually          |
| `disabled`          | boolean | `false` | Disables interaction                                                |
| `readonly`          | boolean | `false` | Prevents user toggling (still focusable)                            |
| `required`          | boolean | `false` | Marks the field as required; the element is invalid when unchecked  |
| `name`              | string  | —       | Form field name                                                     |
| `value`             | string  | `"on"`  | Value submitted with form data when checked                         |
| `aria-label`        | string  | —       | Accessible label                                                    |
| `aria-describedby`  | string  | —       | References a describing element                                     |
| `aria-labelledby`   | string  | —       | References a labelling element                                      |

---

## Properties

| Property        | Type    | Reflects attribute |
|-----------------|---------|--------------------|
| `checked`       | boolean | `checked`          |
| `indeterminate` | boolean | `indeterminate`    |
| `disabled`      | boolean | `disabled`         |
| `readOnly`      | boolean | `readonly`         |
| `required`      | boolean | `required`         |
| `name`          | string  | `name`             |
| `value`         | string  | `value`            |

---

## Events

| Event                       | Cancelable | Detail                                                                   |
|-----------------------------|------------|--------------------------------------------------------------------------|
| `x-checkbox-change-request` | **yes**    | `{ value, previousChecked, nextChecked }` — call `preventDefault()` to block the toggle |
| `x-checkbox-change`         | no         | `{ value, checked }` — fired after state has changed                    |

Both events bubble and are composed.

---

## Toggle logic

- When `indeterminate` is set, clicking always transitions to `checked=true, indeterminate=false`.
- When checked, clicking transitions to `checked=false, indeterminate=false`.
- When unchecked, clicking transitions to `checked=true, indeterminate=false`.

## Form behaviour

- `formAssociated` is `true`; the element participates in form submission.
- The submitted value is the `value` attribute (default `"on"`) when checked; nothing is submitted when unchecked.
- On form reset, `checked` and `indeterminate` attributes are removed. The `value` attribute is not restored by reset (matching native `<input type="checkbox">` behaviour).

---

## Accessibility

- The internal `<button part="control">` carries `role="checkbox"` and `aria-checked` set to `"true"`, `"false"`, or `"mixed"`.
- `tabindex="0"` on the control when enabled; `tabindex="-1"` when disabled.
- `aria-disabled="true"` on the control when disabled.
- `aria-required="true"` on the control when required.
- `aria-readonly="true"` on the control when readonly.
- `aria-label`, `aria-labelledby`, and `aria-describedby` are forwarded from the host attribute to the control.
- Keyboard: `Space` toggles; `Enter` toggles (unless readonly/disabled).

---

## Examples

### Basic states

```html
<x-checkbox></x-checkbox>
<x-checkbox checked></x-checkbox>
<x-checkbox indeterminate></x-checkbox>
<x-checkbox disabled></x-checkbox>
<x-checkbox checked disabled></x-checkbox>
```

### With a label

```html
<label>
  <x-checkbox name="agree" value="yes" required></x-checkbox>
  I agree to the terms
</label>
```

### Listening to changes

```js
document.querySelector('x-checkbox').addEventListener('x-checkbox-change', e => {
  console.log('checked:', e.detail.checked, 'value:', e.detail.value);
});
```

### Intercept and block toggle

```js
checkbox.addEventListener('x-checkbox-change-request', e => {
  if (!canToggle) e.preventDefault();
});
```

### ClojureScript (hiccup renderer)

```clojure
[:x-checkbox {:checked "" :name "agree" :value "yes"
              :on-x-checkbox-change
              (fn [e]
                (swap! state assoc :agreed (.. e -detail -checked)))}]
```
