# x-calendar

A standalone, always-visible inline month calendar — a companion to
[`x-date-picker`](x-date-picker.md). Where `x-date-picker` binds a calendar to
a text input behind a popover, `x-calendar` renders the calendar grid directly
in the page. Supports single-date and date-range selection, localized weekday
and month names, configurable first-day-of-week, a month/year quick-jump
header, and an optional ISO week-number column. Dates are ISO 8601 strings
(`YYYY-MM-DD`).

---

## Tag

```html
<x-calendar></x-calendar>
```

---

## Attributes

| Attribute              | Type    | Default    | Description                                                          |
|------------------------|---------|------------|----------------------------------------------------------------------|
| `mode`                 | enum    | `"single"` | Selection mode: `single` or `range`                                  |
| `value`                | string  | —          | Selected date (`YYYY-MM-DD`), single mode                            |
| `start`                | string  | —          | Range start date (`YYYY-MM-DD`), range mode                          |
| `end`                  | string  | —          | Range end date (`YYYY-MM-DD`), range mode                            |
| `min`                  | string  | —          | Earliest selectable date (`YYYY-MM-DD`)                              |
| `max`                  | string  | —          | Latest selectable date (`YYYY-MM-DD`)                                |
| `disabled-dates`       | string  | —          | Space/comma-separated list of individually disabled ISO dates        |
| `first-day-of-week`    | string  | `"0"`      | First weekday column: `0`–`6` or a name (`sunday`…`saturday`)        |
| `locale`               | string  | —          | BCP 47 locale tag for weekday/month names (e.g. `"en-US"`)           |
| `month`                | string  | —          | Displayed month (`YYYY-MM`). Defaults to the value/start month, else today |
| `show-week-numbers`    | boolean | `false`    | Show an ISO week-number column down the left edge                    |
| `disabled`             | boolean | `false`    | Disables the whole calendar                                          |
| `range-allow-same-day` | boolean | `false`    | Allow start and end to be the same date in range mode                |
| `auto-swap`            | boolean | `false`    | Swap start/end when the second pick precedes the first               |

---

## Properties

| Property            | Type    | Reflects attribute    |
|---------------------|---------|-----------------------|
| `mode`              | string  | `mode`                |
| `value`             | string  | `value`               |
| `start`             | string  | `start`               |
| `end`               | string  | `end`                 |
| `min`               | string  | `min`                 |
| `max`               | string  | `max`                 |
| `month`             | string  | `month`               |
| `locale`            | string  | `locale`              |
| `firstDayOfWeek`    | string  | `first-day-of-week`   |
| `disabledDates`     | string  | `disabled-dates`      |
| `disabled`          | boolean | `disabled`            |
| `showWeekNumbers`   | boolean | `show-week-numbers`   |
| `rangeAllowSameDay` | boolean | `range-allow-same-day`|
| `autoSwap`          | boolean | `auto-swap`           |

---

## Events

| Event                 | Cancelable | Detail                              | Description                                  |
|-----------------------|------------|-------------------------------------|----------------------------------------------|
| `x-calendar-change`   | no         | `{ value, start, end, mode }`       | Fired after a date selection is committed    |
| `x-calendar-navigate` | no         | `{ month }`                         | Fired when the displayed month changes       |

In single mode `value` carries the ISO date and `start`/`end` are empty. In
range mode `start`/`end` carry the ISO dates (`end` empty until the range is
complete) and `value` is empty. `month` is a `YYYY-MM` string.

---

## Methods

| Method               | Description                                              |
|----------------------|----------------------------------------------------------|
| `focus()`            | Moves keyboard focus to the calendar grid                |
| `goToMonth(month)`   | Jumps the displayed month to a `YYYY-MM` string          |
| `clear()`            | Clears the current selection (`value`/`start`/`end`)     |

---

## Slots

| Slot        | Description                        |
|-------------|------------------------------------|
| *(default)* | No default slot content expected   |

---

## CSS custom properties

| Property                       | Purpose                              |
|---------------------------------|--------------------------------------|
| `--x-calendar-width`            | Calendar width (default `18rem`)     |
| `--x-calendar-surface`          | Calendar background                  |
| `--x-calendar-bg`               | Quick-jump panel background          |
| `--x-calendar-border`           | Border colour                        |
| `--x-calendar-radius`           | Outer corner radius                  |
| `--x-calendar-text`             | Text colour                          |
| `--x-calendar-muted`            | Weekday / week-number colour         |
| `--x-calendar-hover`            | Day / button hover background        |
| `--x-calendar-focus`            | Focus-ring colour                    |
| `--x-calendar-selected-bg`      | Selected-day background              |
| `--x-calendar-selected-text`    | Selected-day text                    |
| `--x-calendar-range-bg`         | Range-interior background            |
| `--x-calendar-range-text`       | Range-interior text                  |
| `--x-calendar-today-ring`       | Today-marker ring colour             |

All consume shared `x-theme` tokens with fallbacks.

---

## Accessibility

- The grid has `role="grid"`; day cells are `<button role="gridcell">` with
  `aria-selected`, `aria-disabled`, and a localized full-date `aria-label`.
- Today's cell carries `aria-current="date"`.
- Roving tabindex: exactly one day cell is tab-reachable; arrow keys move it.
- The month label has `aria-live="polite"` and `aria-expanded` for the
  quick-jump panel.
- A disabled calendar sets `aria-disabled="true"` on the host.
- Day-hover transitions respect `prefers-reduced-motion`.

---

## Keyboard

| Key             | Action                          |
|-----------------|---------------------------------|
| `ArrowLeft`     | Move one day back               |
| `ArrowRight`    | Move one day forward            |
| `ArrowUp`       | Move one week back              |
| `ArrowDown`     | Move one week forward           |
| `PageUp`        | Move one month back             |
| `PageDown`      | Move one month forward          |
| `Home`          | First day of the week           |
| `End`           | Last day of the week            |
| `Enter`/`Space` | Select the focused date         |
| `Escape`        | Close the open quick-jump panel |

Navigation is clamped to `[min, max]`; crossing a month boundary shifts the
displayed month. The quick-jump panel is also dismissed by a pointer press
outside the calendar or by selecting a day.

---

## Examples

### Single date

```html
<x-calendar></x-calendar>
```

### Date range

```html
<x-calendar mode="range"></x-calendar>
```

### Pre-selected value

```html
<x-calendar value="2026-03-18"></x-calendar>
```

### Bounded with disabled dates

```html
<x-calendar
  min="2026-01-01"
  max="2026-12-31"
  disabled-dates="2026-03-17 2026-03-25">
</x-calendar>
```

### Monday-first, localized, with week numbers

```html
<x-calendar first-day-of-week="monday" locale="de-DE" show-week-numbers></x-calendar>
```

### Listening to changes

```js
document.querySelector('x-calendar').addEventListener('x-calendar-change', e => {
  console.log('selected:', e.detail.value || `${e.detail.start}…${e.detail.end}`);
});
```

### ClojureScript (hiccup renderer)

```clojure
[:x-calendar {:mode "range"
              :first-day-of-week "monday"
              :on-x-calendar-change
              (fn [e]
                (swap! state assoc
                       :start (.. e -detail -start)
                       :end   (.. e -detail -end)))}]
```
