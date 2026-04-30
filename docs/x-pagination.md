# x-pagination

A stateless native Web Component that renders a numbered page control with prev/next navigation and ellipsis for large page ranges.

---

## Tag name

```html
<x-pagination page="1" total-pages="20"></x-pagination>
```

---

## Observed Attributes

| Attribute        | Type    | Default        | Notes |
|------------------|---------|----------------|-------|
| `page`           | integer | `1`            | Current page, 1-indexed. Clamped to `[1, total-pages]`. |
| `total-pages`    | integer | `1`            | Total page count. Values ≤ 0 treated as 1. |
| `sibling-count`  | integer | `1`            | Pages shown on each side of the current page. |
| `boundary-count` | integer | `1`            | Pages always shown at the start and end of the range. |
| `size`           | string  | `"md"`         | Button size preset: `sm` \| `md` \| `lg`. |
| `disabled`       | boolean | `false`        | Disables all buttons and suppresses events. |
| `label`          | string  | `"Pagination"` | `aria-label` value on the `<nav>` element. |

---

## Reflected Properties

| Property        | Type    | Reflects attribute  |
|-----------------|---------|---------------------|
| `page`          | number  | `page`              |
| `total-pages`   | number  | `total-pages`       |
| `sibling-count` | number  | `sibling-count`     |
| `boundary-count`| number  | `boundary-count`    |
| `size`          | string  | `size`              |
| `disabled`      | boolean | `disabled`          |
| `label`         | string  | `label`             |

```js
const el = document.querySelector('x-pagination');
el.page = 3;            // sets attribute page="3"
el.totalPages = 20;     // sets attribute total-pages="20"
el.size = "lg";         // sets attribute size="lg"
el.disabled = true;     // sets attribute disabled=""
el.label = "Go to page"; // sets attribute label="Go to page"
```

---

## Events

| Event         | Detail              | Cancelable | Fires when |
|---------------|---------------------|------------|------------|
| `page-change-request` | `{ page: number, previousPage: number }` | **yes** | Before a page navigation — call `preventDefault()` to block |
| `page-change` | `{ page: number }`  | no | After a page button, prev, or next is clicked |

Both events bubble and are composed (cross shadow DOM boundary).

```js
// Controlled mode — block and handle externally
el.addEventListener('page-change-request', e => {
  e.preventDefault();
  // validate, then apply manually:
  el.setAttribute('page', String(e.detail.page));
});

// Uncontrolled mode — react to the change
el.addEventListener('page-change', e => {
  console.log(e.detail.page); // e.g. 4
  el.setAttribute('page', String(e.detail.page));
});
```

The component does **not** update `page` itself — the consumer must reflect the new page back as an attribute or property.

---

## Slots

| Slot   | Default content | Notes |
|--------|-----------------|-------|
| `prev` | `"Prev"`        | Content inside the Previous `<button>` |
| `next` | `"Next"`        | Content inside the Next `<button>` |

```html
<x-pagination page="5" total-pages="10">
  <span slot="prev">← Back</span>
  <span slot="next">Forward →</span>
</x-pagination>
```

---

## CSS Custom Properties

| Property                            | Default (light)                    | Notes |
|-------------------------------------|------------------------------------|-------|
| `--x-pagination-gap`                | `0.25rem`                          | Gap between buttons |
| `--x-pagination-button-size`        | `2rem`                             | Height and min-width of buttons |
| `--x-pagination-button-radius`      | `0.375rem`                         | Border radius |
| `--x-pagination-button-bg`          | `transparent`                      | Default button background |
| `--x-pagination-button-color`       | `rgba(0,0,0,0.75)`                 | Default button text colour |
| `--x-pagination-button-border`      | `1px solid rgba(0,0,0,0.15)`       | Default button border |
| `--x-pagination-button-hover-bg`    | `rgba(0,0,0,0.06)`                 | Hover background |
| `--x-pagination-button-hover-color` | `rgba(0,0,0,0.9)`                  | Hover text colour |
| `--x-pagination-current-bg`         | `rgba(0,0,0,0.88)`                 | Current page button background |
| `--x-pagination-current-color`      | `#fff`                             | Current page button text colour |
| `--x-pagination-current-border`     | `1px solid transparent`            | Current page button border |
| `--x-pagination-disabled-opacity`   | `0.4`                              | Opacity for disabled buttons |
| `--x-pagination-font-size`          | `0.875rem`                         | Font size (overridden by `size`) |
| `--x-pagination-ellipsis-color`     | `rgba(0,0,0,0.45)`                 | Ellipsis symbol colour |

Dark mode defaults are applied automatically via `@media (prefers-color-scheme: dark)`.

Size presets adjust `--x-pagination-button-size` and `--x-pagination-font-size`:
- `sm`: `1.75rem` / `0.75rem`
- `md`: `2rem` / `0.875rem` (default)
- `lg`: `2.5rem` / `1rem`

### Custom theme example

```css
x-pagination {
  --x-pagination-button-radius: 9999px;
  --x-pagination-current-bg: #7c3aed;
  --x-pagination-current-color: #fff;
  --x-pagination-button-hover-bg: rgba(124, 58, 237, 0.1);
  --x-pagination-button-hover-color: #7c3aed;
}
```

---

## Shadow DOM Parts

| Part              | Element                        |
|-------------------|--------------------------------|
| `nav`             | The `<nav>` wrapper            |
| `list`            | The `<ol>` element             |
| `item`            | Any `<li>` element             |
| `item-prev`       | The Previous `<li>`            |
| `item-next`       | The Next `<li>`                |
| `item-page`       | A page number `<li>`           |
| `item-ellipsis`   | An ellipsis `<li>`             |
| `button`          | Any `<button>` element         |
| `button-prev`     | The Previous `<button>`        |
| `button-next`     | The Next `<button>`            |
| `button-page`     | A page number `<button>`       |
| `ellipsis`        | The ellipsis `<span>`          |

```css
x-pagination::part(button-page) {
  font-weight: 700;
}
```

---

## Page Range Algorithm

The visible page items are computed as the sorted union of three sets:

1. **Left boundary** — pages `1..boundary-count`
2. **Right boundary** — pages `(total-pages - boundary-count + 1)..total-pages`
3. **Mid window** — pages `(page - sibling-count)..(page + sibling-count)`, clamped to `[1, total-pages]`

An `…` ellipsis is inserted wherever consecutive visible page numbers have a gap greater than 1.

---

## Accessibility

- The `<nav>` element has `aria-label` (defaults to `"Pagination"`; customise via the `label` attribute).
- The current page button has `aria-current="page"`.
- Ellipsis `<span>` elements have `aria-hidden="true"`.
- Prev/next buttons have descriptive `aria-label` attributes (`"Previous page"`, `"Next page"`).
- All buttons are individually focusable via Tab; Enter/Space activates them.
- `@media (prefers-reduced-motion: reduce)` disables transitions.

---

## Responsive

On touch devices (`@media (pointer:coarse)`) page buttons are enlarged to 2.75rem (44px) for easier tap interaction.

---

## Usage Examples

### Basic

```html
<x-pagination page="1" total-pages="20"></x-pagination>
```

### Controlled (update on change)

```html
<x-pagination id="pg" page="1" total-pages="50"></x-pagination>
<script>
  document.getElementById('pg').addEventListener('page-change', e => {
    e.target.setAttribute('page', String(e.detail.page));
  });
</script>
```

### Custom sibling and boundary counts

```html
<!-- Show 2 pages on each side of current, 2 at each boundary -->
<x-pagination page="10" total-pages="50"
              sibling-count="2" boundary-count="2"></x-pagination>
```

### Small size

```html
<x-pagination page="3" total-pages="10" size="sm"></x-pagination>
```

### Custom labels

```html
<x-pagination page="3" total-pages="10" label="Article navigation">
  <span slot="prev">← Previous</span>
  <span slot="next">Next →</span>
</x-pagination>
```

### ESM import

```js
import { init } from '@vanelsas/baredom/x-pagination';
init();
```
