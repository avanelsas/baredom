# x-range-slider

A dual-handle, form-associated, accessible range slider web component. Two
draggable thumbs select a numeric `[start, end]` sub-range within `[min, max]`.

For a single-value slider, use [`x-slider`](./x-slider.md) instead.

## Tag name

```html
<x-range-slider></x-range-slider>
```

---

## Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `start` | string (number) | `min` | Lower selected value |
| `end` | string (number) | `max` | Upper selected value |
| `min` | string (number) | `"0"` | Track minimum |
| `max` | string (number) | `"100"` | Track maximum |
| `step` | string (number \| `"any"`) | `"1"` | Step increment. `"any"` disables stepping. |
| `min-gap` | string (number) | `"0"` | Minimum distance enforced between `start` and `end` |
| `disabled` | boolean (presence) | false | Disables all interaction; removes thumbs from the tab order |
| `readonly` | boolean (presence) | false | Blocks pointer and keyboard changes; thumbs remain focusable |
| `name` | string | — | Form field name |
| `label` | string | — | Visible label text above the slider |
| `show-value` | boolean (presence) | false | Show the current `start – end` text beside the label |
| `size` | `"sm"` \| `"md"` \| `"lg"` | `"md"` | Size variant |
| `aria-label` | string | — | Accessible label for the slider group and thumbs |
| `aria-labelledby` | string | — | Forwarded to `[part=base]` |
| `aria-describedby` | string | — | Forwarded to `[part=base]` |

---

## Properties

All properties reflect to their corresponding attributes.

| Property | Type | Reflects attribute |
|----------|------|--------------------|
| `start` | string | `start` |
| `end` | string | `end` |
| `min` | string | `min` |
| `max` | string | `max` |
| `step` | string | `step` |
| `minGap` | string | `min-gap` |
| `disabled` | boolean | `disabled` |
| `readOnly` | boolean | `readonly` |
| `showValue` | boolean | `show-value` |
| `name` | string | `name` |
| `label` | string | `label` |
| `size` | string | `size` |

---

## Events

| Event | Cancelable | Detail |
|-------|-----------|--------|
| `x-range-slider-change-request` | **yes** | `{ start, end, previousStart, previousEnd, min, max }` — call `preventDefault()` to block the update |
| `x-range-slider-input` | no | `{ start, end, min, max }` |
| `x-range-slider-change` | no | `{ start, end, min, max }` |

All detail fields are numbers. All events bubble and are composed (cross the
shadow-DOM boundary).

- `x-range-slider-change-request` — fires before either value updates. Prevent
  to keep the current range (controlled mode).
- `x-range-slider-input` — fires continuously while a thumb is dragged and on
  every keyboard change.
- `x-range-slider-change` — fires once when a drag is committed (pointerup) and
  after every keyboard change.

Each event always carries the full `{ start, end }` pair regardless of which
thumb moved.

---

## Behavior

- **No crossing.** When a thumb is dragged or stepped toward the other, it stops
  at the other thumb's value — `start` can never exceed `end`. The thumbs never
  swap roles.
- **`min-gap`.** The two values are kept at least `min-gap` apart. While
  dragging, the moving thumb stops `min-gap` short of the other.
- **Out-of-order attributes.** If `start` and `end` are set declaratively in a
  way that violates the order or the gap, the component resolves them
  deterministically: `end` is pulled up to satisfy the gap; only when that would
  exceed `max` is `start` pulled down instead. For predictable stepping, set
  `min-gap` to a multiple of `step`.
- **Step snapping.** Dragged and stepped values snap to `step` (measured from
  `min`). `step="any"` disables snapping.
- **Track click.** Clicking the bare track moves the nearest thumb to that
  position and begins dragging it.
- **Overlapping thumbs.** When both values coincide, the last-grabbed thumb
  stays on top. A pointer-down at or below the shared position grabs the start
  thumb; above it grabs the end thumb.

---

## Parts

| Part | Element | Description |
|------|---------|-------------|
| `base` | `<div>` | Outer wrapper; `role="group"`; receives `data-size` |
| `header` | `<div>` | Row containing label and value text; hidden when neither is active |
| `label-text` | `<span>` | Label text |
| `value-text` | `<span>` | `start – end` display |
| `track` | `<div>` | The rail region |
| `track-fill` | `<div>` | Highlighted segment between the two thumbs |
| `thumb-start` | `<div>` | Lower handle; `role="slider"` |
| `thumb-end` | `<div>` | Upper handle; `role="slider"` |

---

## CSS Custom Properties

| Property | Default (light) | Description |
|----------|----------------|-------------|
| `--x-range-slider-track-color` | `rgba(0,0,0,0.15)` | Unfilled track color |
| `--x-range-slider-fill-color` | `#3b82f6` | Selected-segment color |
| `--x-range-slider-thumb-color` | `#ffffff` | Thumb background color |
| `--x-range-slider-thumb-border` | `2px solid #3b82f6` | Thumb border |
| `--x-range-slider-thumb-shadow` | `0 1px 4px rgba(0,0,0,0.20)` | Thumb drop shadow |
| `--x-range-slider-focus-ring` | `#60a5fa` | Focus ring color |
| `--x-range-slider-disabled-opacity` | `0.45` | Opacity when disabled |
| `--x-range-slider-label-color` | `rgba(0,0,0,0.60)` | Label text color |
| `--x-range-slider-value-color` | `rgba(0,0,0,0.50)` | Value text color |
| `--x-range-slider-radius` | `9999px` | Track and fill border-radius |

Dark-mode overrides are applied automatically via
`@media (prefers-color-scheme: dark)`.

### Size-driven internal variables

The `size` attribute sets CSS custom properties on `[part=base]`:

| Size | Track height | Thumb size |
|------|--------------|------------|
| `sm` | 4px | 14px |
| `md` | 6px | 18px |
| `lg` | 8px | 22px |

---

## Accessibility

- Each thumb is a `role="slider"` element with its own `tabindex`,
  `aria-valuemin`, `aria-valuemax`, `aria-valuenow` and `aria-valuetext`.
- The thumbs advertise their *effective* movable range: the start thumb's
  `aria-valuemax` is `end - min-gap`; the end thumb's `aria-valuemin` is
  `start + min-gap`.
- `[part=base]` is `role="group"` and carries the forwarded `aria-label` /
  `aria-labelledby` / `aria-describedby`. Each thumb is labelled
  `"<name> minimum"` / `"<name> maximum"`, where `<name>` is `aria-label`,
  `label`, or `"Range"`.
- Keyboard: arrow keys move a thumb by one `step` (ten with `Shift`),
  `PageUp` / `PageDown` move by ten steps, `Home` / `End` jump to `min` / `max`.
  When `step` is `any`, keyboard moves use an increment of `1`.
- When `disabled`, both thumbs receive `tabindex="-1"` and `aria-disabled`.
- `readonly` keeps the thumbs focusable but consumes value-changing keys.

---

## Form association

`x-range-slider` is form-associated (`formAssociated = true`). The current range
is submitted under the `name` attribute as a single comma-joined string
`"start,end"` (e.g. `"20,80"`); split on `,` to read the two values. Form reset
restores the widest range (`start` → `min`, `end` → `max`).

---

## Responsive

On touch devices (`@media (pointer:coarse)`) the thumbs are enlarged to 28px for
easier interaction. The track adapts to its container width.

---

## Usage examples

### Basic range slider

```html
<x-range-slider start="20" end="80" min="0" max="100"></x-range-slider>
```

### With label and visible value

```html
<x-range-slider label="Price" show-value start="200" end="800"
                min="0" max="1000" step="50"></x-range-slider>
```

### Minimum gap between thumbs

```html
<x-range-slider start="30" end="70" min="0" max="100"
                min-gap="10"></x-range-slider>
```

### Disabled / readonly

```html
<x-range-slider start="25" end="75" disabled></x-range-slider>
<x-range-slider start="25" end="75" readonly></x-range-slider>
```

### Sizes

```html
<x-range-slider size="sm" start="20" end="60"></x-range-slider>
<x-range-slider size="md" start="20" end="60"></x-range-slider>
<x-range-slider size="lg" start="20" end="60"></x-range-slider>
```

### In a form

```html
<form>
  <x-range-slider name="price" start="200" end="800" min="0" max="1000"
                  label="Price range" show-value></x-range-slider>
  <button type="submit">Search</button>
</form>
```

### JavaScript API

```js
const slider = document.querySelector('x-range-slider');

slider.start = '30';
slider.end = '70';
slider.min = '0';
slider.max = '100';
slider.step = '5';
slider.minGap = '10';

slider.addEventListener('x-range-slider-input', (e) => {
  console.log('dragging:', e.detail.start, e.detail.end);
});
slider.addEventListener('x-range-slider-change', (e) => {
  console.log('committed:', e.detail.start, e.detail.end);
});

// Controlled mode — veto changes
slider.addEventListener('x-range-slider-change-request', (e) => {
  if (e.detail.end - e.detail.start < 5) e.preventDefault();
});
```
