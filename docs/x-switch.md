# x-switch

A toggle switch form control. Semantically a `role="switch"` button that reflects its state via the `checked` attribute. Form-associated (participates in `<form>` submission and reset).

## Tag

```html
<x-switch></x-switch>
```

## Observed Attributes

| Attribute          | Type    | Default | Description                                               |
|--------------------|---------|---------|-----------------------------------------------------------|
| `checked`          | boolean | absent  | Present → switch is on                                    |
| `disabled`         | boolean | absent  | Prevents interaction; control is inert                    |
| `readonly`         | boolean | absent  | Prevents toggling without affecting form submission       |
| `required`         | boolean | absent  | Marks the field as required (must be checked for validity)|
| `name`             | string  | —       | Form field name                                           |
| `value`            | string  | `"on"`  | Value submitted when checked                              |
| `aria-label`       | string  | —       | Forwarded to inner control                                |
| `aria-describedby` | string  | —       | Forwarded to inner control                                |
| `aria-labelledby`  | string  | —       | Forwarded to inner control                                |

## Properties

| Property   | Type    | Reflects attribute  |
|------------|---------|---------------------|
| `checked`  | boolean | `checked`           |
| `disabled` | boolean | `disabled`          |
| `readOnly` | boolean | `readonly`          |
| `required` | boolean | `required`          |
| `name`     | string  | `name`              |
| `value`    | string  | `value`             |

## Events

### `x-switch-change-request`

Fired **before** the state changes. Cancelable — call `event.preventDefault()` to block the toggle.

```js
el.addEventListener('x-switch-change-request', (e) => {
  console.log(e.detail.previousChecked, e.detail.nextChecked, e.detail.value);
  e.preventDefault(); // optional: block the toggle
});
```

| Detail field      | Type    | Description                        |
|-------------------|---------|------------------------------------|
| `value`           | string  | Current form value                 |
| `previousChecked` | boolean | State before toggle                |
| `nextChecked`     | boolean | State after toggle (if not blocked)|

### `x-switch-change`

Fired **after** the state has changed. Not cancelable.

```js
el.addEventListener('x-switch-change', (e) => {
  console.log(e.detail.checked, e.detail.value);
});
```

| Detail field | Type    | Description            |
|--------------|---------|------------------------|
| `value`      | string  | Form value             |
| `checked`    | boolean | New checked state      |

## Shadow Parts

| Part      | Element  | Description           |
|-----------|----------|-----------------------|
| `control` | `button` | Interactive switch root |
| `track`   | `span`   | The pill-shaped track  |
| `thumb`   | `span`   | The sliding thumb      |

## CSS Custom Properties

| Property                        | Default       | Description                         |
|---------------------------------|---------------|-------------------------------------|
| `--x-switch-track-width`        | `44px`        | Track width                         |
| `--x-switch-track-height`       | `24px`        | Track height                        |
| `--x-switch-thumb-size`         | `18px`        | Thumb diameter                      |
| `--x-switch-thumb-offset`       | `3px`         | Thumb inset from track edge         |
| `--x-switch-track-radius`       | `999px`       | Track border radius                 |
| `--x-switch-track-bg`           | `#d1d5db`     | Track background (off)              |
| `--x-switch-track-bg-checked`   | `#2563eb`     | Track background (on)               |
| `--x-switch-thumb-bg`           | `#ffffff`     | Thumb background                    |
| `--x-switch-focus-ring`         | `#60a5fa`     | Focus ring color                    |
| `--x-switch-disabled-opacity`   | `0.45`        | Opacity when disabled               |

Dark-mode overrides are applied automatically via `@media (prefers-color-scheme: dark)`.

## Accessibility

- Inner `button` carries `role="switch"` and `aria-checked`.
- `aria-disabled`, `aria-required`, `aria-readonly` are kept in sync.
- `aria-label`, `aria-labelledby`, `aria-describedby` are forwarded to the inner button.
- A `<label for="...">` in the document is automatically wired via `aria-labelledby`.
- Keyboard: Space and Enter toggle the switch.
- Animations respect `@media (prefers-reduced-motion: reduce)`.

## Form participation

`x-switch` is a form-associated custom element. It submits `name=value` when checked, nothing when unchecked. It responds to `formResetCallback` and `formDisabledCallback`.

## Usage examples

```html
<!-- Basic -->
<x-switch name="notifications"></x-switch>

<!-- Pre-checked -->
<x-switch name="dark-mode" checked></x-switch>

<!-- With label -->
<label for="sw1">Enable alerts</label>
<x-switch id="sw1" name="alerts"></x-switch>

<!-- Disabled -->
<x-switch name="feature" disabled checked></x-switch>

<!-- Custom value -->
<x-switch name="theme" value="dark"></x-switch>

<!-- React to changes -->
<script>
  import '@vanelsas/baredom/x-switch';
  const sw = document.querySelector('x-switch');
  sw.addEventListener('x-switch-change', (e) => {
    console.log('now:', e.detail.checked);
  });
</script>
```
