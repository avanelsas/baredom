# x-rating

A discrete, form-associated, accessible star-rating web component. A row of
star (or heart) icons selects a numeric rating, optionally in half-star
increments. Use it to collect a score, or — in `readonly` mode — to display an
average rating such as `3.5`.

For a continuous numeric value, use [`x-slider`](./x-slider.md). For a
two-handle range, use [`x-range-slider`](./x-range-slider.md).

## Tag name

```html
<x-rating></x-rating>
```

---

## Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | string (number) | `"0"` | Current rating. `0` means unrated. Snapped to `precision` and clamped to `[0, max]`. |
| `max` | string (number) | `"5"` | Number of stars. Rounded to a whole number, clamped to `1`–`20`. |
| `precision` | `"full"` \| `"half"` | `"full"` | `half` allows 0.5-star ratings. |
| `shape` | `"star"` \| `"heart"` | `"star"` | Icon shape. |
| `allow-clear` | boolean (presence) | false | Clicking the currently-selected star — or pressing `Delete` / `Backspace` — clears the rating to `0`. |
| `disabled` | boolean (presence) | false | Disables all interaction; removes the widget from the tab order. |
| `readonly` | boolean (presence) | false | Blocks pointer and keyboard changes; the widget stays focusable. |
| `name` | string | — | Form field name. |
| `label` | string | — | Visible label text above the stars. |
| `size` | `"sm"` \| `"md"` \| `"lg"` | `"md"` | Size variant. |
| `aria-label` | string | — | Accessible label for the rating. |
| `aria-labelledby` | string | — | Forwarded to `[part=stars]`. |
| `aria-describedby` | string | — | Forwarded to `[part=stars]`. |

---

## Properties

All properties reflect to their corresponding attributes.

| Property | Type | Reflects attribute |
|----------|------|--------------------|
| `value` | string | `value` |
| `max` | string | `max` |
| `precision` | string | `precision` |
| `shape` | string | `shape` |
| `allowClear` | boolean | `allow-clear` |
| `disabled` | boolean | `disabled` |
| `readOnly` | boolean | `readonly` |
| `name` | string | `name` |
| `label` | string | `label` |
| `size` | string | `size` |

---

## Events

| Event | Cancelable | Detail |
|-------|-----------|--------|
| `x-rating-change-request` | **yes** | `{ value, previousValue, max }` — call `preventDefault()` to block the update |
| `x-rating-change` | no | `{ value, max }` |
| `x-rating-hover` | no | `{ value, max }` — `value` is `null` when the pointer leaves the row |

All events bubble and are composed (cross the shadow-DOM boundary).

- `x-rating-change-request` — fires before `value` updates. Prevent it to keep
  the current rating (controlled mode).
- `x-rating-change` — fires once, after a committed change (pointer click or
  keyboard). A rating is a discrete input, so there is no separate `input`
  event: every interaction is final.
- `x-rating-hover` — fires while the pointer moves across the stars, reporting
  the value that *would* be set. It is a visual-preview channel only — it never
  changes `value`. On pointer-leave it fires once with `value: null`.

---

## Behavior

- **Discrete input.** Each click or key press commits a final value
  immediately. There is no drag and no continuous `input` event.
- **Half stars.** With `precision="half"`, the left half of a star selects the
  `.5` value and the right half selects the whole value; keyboard arrows step
  by `0.5`. Stars render filled, half-filled, or empty accordingly.
- **Hover preview.** Moving the pointer over the stars previews the candidate
  rating without committing it. The preview is cleared on pointer-leave.
- **Clearing.** With `allow-clear`, clicking the already-selected star clears
  the rating to `0`; `Delete` and `Backspace` clear it from the keyboard. The
  `Home` key always jumps to `0` regardless of `allow-clear`.
- **Snapping.** `value` is snapped to the active precision and clamped to
  `[0, max]`, both for declarative attributes and for committed interactions.

---

## Parts

| Part | Element | Description |
|------|---------|-------------|
| `base` | `<div>` | Outer layout wrapper; receives `data-size` |
| `label-text` | `<span>` | Label text; hidden when no `label` is set |
| `stars` | `<div>` | The star row; `role="slider"`, focusable |
| `star` | `<span>` | One star cell; carries `data-index` and `data-fill` |

---

## CSS Custom Properties

| Property | Default (light) | Description |
|----------|----------------|-------------|
| `--x-rating-active-color` | `#f59e0b` | Filled-star color |
| `--x-rating-inactive-color` | `rgba(0,0,0,0.20)` | Empty-star color |
| `--x-rating-hover-color` | `var(--x-rating-active-color)` | Fill color during hover preview |
| `--x-rating-focus-ring` | `#60a5fa` | Focus ring color |
| `--x-rating-disabled-opacity` | `0.45` | Opacity when disabled |
| `--x-rating-label-color` | `rgba(0,0,0,0.60)` | Label text color |

Dark-mode overrides are applied automatically via
`@media (prefers-color-scheme: dark)`.

### Size-driven internal variable

The `size` attribute sets the star edge length on `[part=base]`:

| Size | Star size |
|------|-----------|
| `sm` | 16px |
| `md` | 22px |
| `lg` | 30px |

---

## Accessibility

- `[part=stars]` is a single `role="slider"` element with `tabindex`,
  `aria-valuemin="0"`, `aria-valuemax`, `aria-valuenow` and `aria-valuetext`
  (e.g. `"3 out of 5 stars"` — `"hearts"` when `shape="heart"` — or
  `"No rating"` at `0`). The individual star cells are `aria-hidden`.
- The accessible name comes from `aria-labelledby`, `aria-label`, the visible
  `label`, or the fallback `"Rating"`, in that order.
- Keyboard: `ArrowRight` / `ArrowUp` increase and `ArrowLeft` / `ArrowDown`
  decrease the rating by one precision step; `Home` jumps to `0`, `End` jumps
  to `max`; `Delete` / `Backspace` clear to `0` when `allow-clear` is set.
- When `disabled`, the widget receives `tabindex="-1"` and `aria-disabled`.
- `readonly` keeps the widget focusable but consumes value-changing keys.

---

## Form association

`x-rating` is form-associated (`formAssociated = true`). The current rating is
submitted under the `name` attribute as a numeric string (e.g. `"4"` or
`"3.5"`); `"0"` is submitted for an unrated widget. Form reset removes the
`value` attribute, returning the rating to `0`.

---

## Responsive

On touch devices (`@media (pointer:coarse)`) the stars are enlarged for easier
tapping. The component sizes to its content and works from 320px viewports up.

---

## Usage examples

### Basic rating

```html
<x-rating value="3"></x-rating>
```

### Half-star precision

```html
<x-rating precision="half" value="3.5"></x-rating>
```

### Heart shape

```html
<x-rating shape="heart" value="4" max="5"></x-rating>
```

### Sizes

```html
<x-rating size="sm" value="3"></x-rating>
<x-rating size="md" value="3"></x-rating>
<x-rating size="lg" value="3"></x-rating>
```

### Read-only average

```html
<x-rating readonly precision="half" value="3.5"
          label="Average rating"></x-rating>
```

### Clearable

```html
<x-rating allow-clear value="2"
          label="Click the selected star again to clear"></x-rating>
```

### Disabled

```html
<x-rating disabled value="3"></x-rating>
```

### Custom theme

```html
<x-rating value="4"
          style="--x-rating-active-color:#ec4899;
                 --x-rating-focus-ring:#f9a8d4;"></x-rating>
```

### In a form

```html
<form>
  <x-rating name="score" value="4" label="Rate your experience"></x-rating>
  <button type="submit">Send feedback</button>
</form>
```

### JavaScript API

```js
const rating = document.querySelector('x-rating');

rating.value = '4';
rating.max = '10';
rating.precision = 'half';

rating.addEventListener('x-rating-change', (e) => {
  console.log('rated:', e.detail.value, 'of', e.detail.max);
});
rating.addEventListener('x-rating-hover', (e) => {
  console.log('previewing:', e.detail.value); // null on leave
});

// Controlled mode — veto changes
rating.addEventListener('x-rating-change-request', (e) => {
  if (e.detail.value === 0) e.preventDefault();
});
```
