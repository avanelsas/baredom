# x-date-picker

A calendar date picker with single-date and date-range modes. Renders a text input that opens a floating calendar panel. Dates are represented as ISO 8601 strings (`YYYY-MM-DD`).

---

## Tag

```html
<x-date-picker></x-date-picker>
```

---

## Attributes

| Attribute              | Type    | Default    | Description                                                           |
|------------------------|---------|------------|-----------------------------------------------------------------------|
| `mode`                 | enum    | `"single"` | Selection mode: `single` `range`                                     |
| `value`                | string  | —          | Selected date in `YYYY-MM-DD` format (single mode)                   |
| `start`                | string  | —          | Range start date in `YYYY-MM-DD` format (range mode)                 |
| `end`                  | string  | —          | Range end date in `YYYY-MM-DD` format (range mode)                   |
| `min`                  | string  | —          | Minimum selectable date (`YYYY-MM-DD`)                               |
| `max`                  | string  | —          | Maximum selectable date (`YYYY-MM-DD`)                               |
| `format`               | enum    | `"iso"`    | Display format: `iso` (YYYY-MM-DD) or `localized` (via `Intl`)       |
| `locale`               | string  | —          | BCP 47 locale tag for localized formatting (e.g. `"en-US"`)          |
| `separator`            | string  | `" - "`    | String placed between start and end in range display                 |
| `auto-swap`            | boolean | `false`    | Automatically swap start/end when end is selected before start       |
| `range-allow-same-day` | boolean | `false`    | Allow start and end to be the same date in range mode                |
| `close-on-select`      | boolean | `false`    | Close the calendar after a date is selected (single mode)            |
| `placeholder`          | string  | —          | Input placeholder text                                               |
| `disabled`             | boolean | `false`    | Disables the input and calendar                                       |
| `readonly`             | boolean | `false`    | Input is read-only; calendar can still open but not select           |
| `required`             | boolean | `false`    | Marks the field as required                                           |
| `name`                 | string  | —          | Form field name                                                       |
| `autocomplete`         | string  | —          | HTML autocomplete hint                                                |
| `aria-label`           | string  | —          | Accessible label for the input                                        |
| `aria-describedby`     | string  | —          | References a describing element                                       |

---

## Properties

| Property   | Type    | Reflects attribute |
|------------|---------|--------------------|
| `mode`     | string  | `mode`             |
| `value`    | string  | `value`            |
| `start`    | string  | `start`            |
| `end`      | string  | `end`              |
| `disabled` | boolean | `disabled`         |
| `readOnly` | boolean | `readonly`         |
| `required` | boolean | `required`         |
| `open`     | boolean | `open`             |

---

## Events

| Event                       | Cancelable | Detail                                     | Description                              |
|-----------------------------|------------|--------------------------------------------|------------------------------------------|
| `x-date-picker-input`       | no         | `{ value?, start?, end?, mode }`           | Fired on each user interaction           |
| `x-date-picker-change-request` | **yes** | `{ value?, start?, end?, mode }`           | Fired before committing a date selection |
| `x-date-picker-change`      | no         | `{ value?, start?, end?, mode }`           | Fired after the selection is committed   |

---

## Slots

| Slot      | Description                        |
|-----------|------------------------------------|
| *(default)* | No default slot content expected |

---

## Accessibility

- The text input has `role="combobox"` with `aria-expanded` and `aria-haspopup="dialog"`.
- The calendar panel has `role="dialog"` with `aria-modal="true"`.
- Calendar cells have `role="gridcell"` with `aria-selected` and `aria-disabled`.
- Full keyboard navigation within the calendar grid.

---

## Keyboard (calendar open)

| Key            | Action                               |
|----------------|--------------------------------------|
| `ArrowLeft`    | Move one day back                    |
| `ArrowRight`   | Move one day forward                 |
| `ArrowUp`      | Move one week back                   |
| `ArrowDown`    | Move one week forward                |
| `PageUp`       | Move one month back                  |
| `PageDown`     | Move one month forward               |
| `Home`         | Go to first day of week              |
| `End`          | Go to last day of week               |
| `Enter`/`Space`| Select focused date                  |
| `Escape`       | Close calendar without selecting     |
| Click outside  | Close calendar without selecting     |

---

## Examples

### Single date

```html
<x-date-picker placeholder="Select a date"></x-date-picker>
```

### Date range

```html
<x-date-picker mode="range" placeholder="Select date range"></x-date-picker>
```

### Pre-selected value

```html
<x-date-picker value="2026-03-18"></x-date-picker>
```

### Bounded range

```html
<x-date-picker min="2026-01-01" max="2026-12-31"></x-date-picker>
```

### Localized display

```html
<x-date-picker format="localized" locale="en-US" placeholder="Pick a date"></x-date-picker>
```

### Disabled

```html
<x-date-picker disabled placeholder="Not available"></x-date-picker>
```

### Listening to changes

```js
document.querySelector('x-date-picker').addEventListener('x-date-picker-change', e => {
  console.log('selected date:', e.detail.value);
});
```

### ClojureScript (hiccup renderer)

```clojure
[:x-date-picker {:placeholder "Select a date"
                 :on-x-date-picker-change
                 (fn [e] (swap! state assoc :date (.. e -detail -value)))}]

[:x-date-picker {:mode "range"
                 :placeholder "Select date range"
                 :on-x-date-picker-change
                 (fn [e]
                   (swap! state assoc
                          :start (.. e -detail -start)
                          :end   (.. e -detail -end)))}]
```
