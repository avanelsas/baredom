# x-currency-field

A form-associated custom element for currency input. Displays a locale-aware currency symbol prefix, formats the value on blur (e.g. `1,234.56`), and shows the raw numeric value on focus for editing. Integrates with `x-form` via `ElementInternals`.

## Tag name

```html
<x-currency-field></x-currency-field>
```

## Observed attributes

| Attribute     | Type    | Default | Notes                                                  |
|---------------|---------|---------|--------------------------------------------------------|
| `name`        | string  | `""`    | Form field name                                        |
| `value`       | string  | `""`    | Raw numeric string, e.g. `"1234.56"`                   |
| `currency`    | string  | `"USD"` | ISO 4217 code; uppercased; falls back to `"USD"`       |
| `locale`      | string  | `""`    | BCP 47 tag for formatting; `""` uses browser default   |
| `min`         | string  | `""`    | Minimum numeric value (triggers `rangeUnderflow`)      |
| `max`         | string  | `""`    | Maximum numeric value (triggers `rangeOverflow`)       |
| `placeholder` | string  | `""`    | Input placeholder text                                 |
| `label`       | string  | `""`    | Label text (shown above input; hidden when empty)      |
| `hint`        | string  | `""`    | Helper text shown below the input                      |
| `error`       | string  | `""`    | Custom error message (triggers `customError` validity) |
| `disabled`    | boolean | absent  | Disables the input                                     |
| `required`    | boolean | absent  | Marks as required for form validation                  |
| `readonly`    | boolean | absent  | Makes the input read-only                              |

## Properties

All properties reflect to/from their corresponding attributes.

| Property      | Type    | Reflects attribute |
|---------------|---------|--------------------|
| `value`       | string  | `value`            |
| `name`        | string  | `name`             |
| `currency`    | string  | `currency`         |
| `locale`      | string  | `locale`           |
| `min`         | string  | `min`              |
| `max`         | string  | `max`              |
| `placeholder` | string  | `placeholder`      |
| `label`       | string  | `label`            |
| `hint`        | string  | `hint`             |
| `error`       | string  | `error`            |
| `disabled`    | boolean | `disabled`         |
| `required`    | boolean | `required`         |
| `readOnly`    | boolean | `readonly`         |

The `value` getter always returns the raw numeric string stored in the `value` attribute (not the formatted display value).

## Events

| Event                       | Bubbles | Composed | Cancelable | Detail                     |
|-----------------------------|---------|----------|------------|----------------------------|
| `x-currency-field-change-request` | yes | yes | **yes** | `{ name: string, value: string, previousValue: string }` |
| `x-currency-field-input`    | yes     | yes      | no         | `{ name: string, value: string }` |
| `x-currency-field-change`   | yes     | yes      | no         | `{ name: string, value: string }` |

- **`x-currency-field-change-request`** — fires before the value updates. Call `preventDefault()` to block the change (controlled mode).

`value` in the event detail is always the raw numeric string (what will be submitted with the form). On `x-currency-field-change`, the value is canonicalized via `parseFloat` (e.g. `"1234.5"` not `"1,234.50"`).

## Shadow DOM structure

```
:host
  style
  div[part=field]
    label[part=label]          — hidden via .label-hidden when empty
    div[part=input-wrapper]
      span[part=symbol]        — currency symbol extracted via Intl (e.g. "$")
      input[part=input]        — type="text", inputmode="decimal"
    span[part=hint]            — hidden via .hint-hidden when empty
    span[part=error]           — role=alert, aria-live=assertive; hidden via .error-hidden
```

## CSS custom properties

| Property                              | Default (light)      | Default (dark)       |
|---------------------------------------|----------------------|----------------------|
| `--x-currency-field-bg`               | `#ffffff`            | `#1f2937`            |
| `--x-currency-field-color`            | `#111827`            | `#f9fafb`            |
| `--x-currency-field-border`           | `1px solid #d1d5db`  | `1px solid #4b5563`  |
| `--x-currency-field-border-radius`    | `6px`                | `6px`                |
| `--x-currency-field-focus-ring-color` | `#2563eb`            | `#3b82f6`            |
| `--x-currency-field-symbol-color`     | `#6b7280`            | `#9ca3af`            |
| `--x-currency-field-label-color`      | `#374151`            | `#d1d5db`            |
| `--x-currency-field-hint-color`       | `#6b7280`            | `#9ca3af`            |
| `--x-currency-field-error-color`      | `#dc2626`            | `#f87171`            |
| `--x-currency-field-disabled-opacity` | `0.45`               | `0.45`               |

## Validity states

| State           | Condition                                                      |
|-----------------|----------------------------------------------------------------|
| `customError`   | `error` attribute is set and non-empty                         |
| `valueMissing`  | `required` is set and `value` is empty                         |
| `badInput`      | `value` is non-empty but not a valid number                    |
| `rangeUnderflow`| `value` parses to a number less than `min` (when `min` is set) |
| `rangeOverflow` | `value` parses to a number greater than `max` (when `max` is set) |

## Form association

`x-currency-field` is a form-associated custom element (`static formAssociated = true`). It integrates with native `<form>` elements and with `x-form` via `ElementInternals`:

- **`formDisabledCallback`** — sets/removes the `disabled` attribute and re-renders
- **`formResetCallback`** — clears the `value` attribute and the input display
- The form submission value is the raw numeric string (e.g. `"1234.56"`)

## Formatting behavior

- **Blurred**: `input.value` shows the value formatted with `Intl.NumberFormat` (e.g. `"1,234.56"` for USD/en-US)
- **Focused**: `input.value` shows the raw numeric string (e.g. `"1234.56"`) so the user can edit it naturally
- **On change** (blur-with-change): the raw value is canonicalized via `parseFloat` and stored as the `value` attribute; the display is reformatted

The currency symbol is displayed in `span[part=symbol]` and is never included in the input value.

## Accessibility

- `label[part=label]` is associated with `input[part=input]` via `for`/`id`
- `input` has `aria-labelledby="label"`, `aria-required`, `aria-invalid`, and `aria-describedby` (points to hint/error when present)
- `span[part=error]` has `role="alert"` and `aria-live="assertive"`
- `span[part=symbol]` has `aria-hidden="true"`
- `input` has `inputmode="decimal"` for numeric keyboard on mobile

## Usage examples

### Standalone

```html
<x-currency-field
  name="price"
  label="Price"
  currency="USD"
  placeholder="0.00"
></x-currency-field>
```

### Pre-filled value

```html
<x-currency-field
  name="amount"
  label="Amount"
  value="1234.56"
  currency="USD"
></x-currency-field>
```

### With validation

```html
<x-currency-field
  name="budget"
  label="Budget"
  required
  min="0"
  max="10000"
  hint="Enter a value between $0 and $10,000"
></x-currency-field>
```

### European locale

```html
<x-currency-field
  name="price"
  label="Preis"
  currency="EUR"
  locale="de-DE"
  value="1234.56"
></x-currency-field>
<!-- displays: 1.234,56 with € symbol -->
```

### Inside x-form

```html
<x-form>
  <x-currency-field
    name="price"
    label="Product price"
    required
    currency="USD"
  ></x-currency-field>
  <button type="submit">Submit</button>
</x-form>
```

The submitted `FormData` will contain `price=1234.56` (raw numeric string).

### JavaScript API

```js
const field = document.querySelector('x-currency-field');

// Read current value (raw numeric string)
console.log(field.value); // "1234.56"

// Set value programmatically
field.value = "999.99";

// Listen to events
field.addEventListener('x-currency-field-change', e => {
  console.log(e.detail.value); // "999.99"
});
```
