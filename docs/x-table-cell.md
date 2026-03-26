# x-table-cell

A stateless table cell Web Component. Works standalone today and coordinates with future `x-table-row` / `x-table` components via bubbling events.

```html
<x-table-cell>Cell content</x-table-cell>

<x-table-cell type="header" sortable sort-direction="asc">
  Name
</x-table-cell>
```

---

## Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `type` | `"data" \| "header"` | `"data"` | Cell semantic type. `"header"` maps to `columnheader`/`rowheader` ARIA role; `"data"` maps to `cell`. |
| `scope` | `"col" \| "row" \| "colgroup" \| "rowgroup"` | `"col"` | Header scope. Determines whether a header is a `columnheader` or `rowheader`. Meaningful only when `type="header"`. |
| `align` | `"start" \| "center" \| "end"` | `"start"` | Horizontal content alignment. |
| `valign` | `"top" \| "middle" \| "bottom"` | `"middle"` | Vertical content alignment. |
| `col-span` | positive integer | `1` | Number of grid columns the cell spans. Sets `style.gridColumn="span N"` on the host for CSS Grid parents. |
| `row-span` | positive integer | `1` | Number of grid rows the cell spans. Sets `style.gridRow="span N"` on the host. |
| `truncate` | boolean (presence) | `false` | Truncates overflowing text with an ellipsis. |
| `sticky` | `"none" \| "start" \| "end"` | `"none"` | Sticky column. `"start"` sticks to the inline-start edge; `"end"` to the inline-end edge. Requires an opaque `--x-table-cell-sticky-bg`. |
| `sortable` | boolean (presence) | `false` | Shows the sort button. Only effective when `type="header"`. |
| `sort-direction` | `"none" \| "asc" \| "desc"` | `"none"` | Current sort state. Updates the sort icon and `aria-sort`. |
| `disabled` | boolean (presence) | `false` | Disables interactions and applies reduced opacity. |

---

## Properties

| Property | Type | Reflects | Default |
|----------|------|----------|---------|
| `type` | `string` | `type` attribute | `"data"` |
| `scope` | `string` | `scope` attribute | `"col"` |
| `align` | `string` | `align` attribute | `"start"` |
| `valign` | `string` | `valign` attribute | `"middle"` |
| `colSpan` | `number` | `col-span` attribute | `1` |
| `rowSpan` | `number` | `row-span` attribute | `1` |
| `truncate` | `boolean` | `truncate` attribute | `false` |
| `sticky` | `string` | `sticky` attribute | `"none"` |
| `sortable` | `boolean` | `sortable` attribute | `false` |
| `sortDirection` | `string` | `sort-direction` attribute | `"none"` |
| `disabled` | `boolean` | `disabled` attribute | `false` |

---

## Events

### `x-table-cell-sort`

Fired when the sort button is activated. The component is stateless — the consumer must update the `sort-direction` attribute to reflect the new state.

| Property | Value |
|----------|-------|
| Bubbles | `true` |
| Composed | `true` |
| Cancelable | `true` |

**Detail:**

```ts
{
  direction: "asc" | "desc" | "none";  // the next direction
  previousDirection: "none" | "asc" | "desc";
}
```

Calling `event.preventDefault()` cancels the action; the attribute will not be updated externally.

### `x-table-cell-connected`

Fired on every `connectedCallback`. Consumed by future `x-table-row` to discover and track cells.

| Property | Value |
|----------|-------|
| Bubbles | `true` |
| Composed | `true` |
| Cancelable | `false` |

**Detail:** `{ type, scope, colSpan, rowSpan, align }`

### `x-table-cell-disconnected`

Fired on every `disconnectedCallback`.

| Property | Value |
|----------|-------|
| Bubbles | `true` |
| Composed | `true` |
| Cancelable | `false` |

**Detail:** `{}`

---

## Slots

| Name | Description |
|------|-------------|
| (default) | Cell content |
| `sort-icon` | Custom sort icon; replaces the default inline SVG arrows. |

---

## Parts

| Part | Element | Description |
|------|---------|-------------|
| `cell` | `div` | Outer layout wrapper; receives padding, background, border. |
| `content` | `span` | Content area wrapping the default slot. |
| `sort-btn` | `button` | Sort interaction button (visible for header+sortable cells only). |
| `sort-icon-default` | `span` | Default inline SVG sort icon (neutral/ascending/descending). |

---

## CSS Custom Properties

### Layout

| Property | Default | Description |
|----------|---------|-------------|
| `--x-table-cell-padding` | `0.5rem 0.75rem` | Cell padding. |
| `--x-table-cell-border-width` | `1px` | Border width (bottom border). |
| `--x-table-cell-min-width` | `0` | Minimum cell width. |
| `--x-table-cell-max-width` | `none` | Maximum cell width. |

### Colors

| Property | Default (light) | Default (dark) | Description |
|----------|-----------------|----------------|-------------|
| `--x-table-cell-bg` | `transparent` | `transparent` | Data cell background. |
| `--x-table-cell-header-bg` | `rgba(0,0,0,0.04)` | `rgba(255,255,255,0.06)` | Header cell background. |
| `--x-table-cell-border-color` | `rgba(0,0,0,0.1)` | `rgba(255,255,255,0.1)` | Border color. |
| `--x-table-cell-color` | `inherit` | `inherit` | Data cell text color. |
| `--x-table-cell-header-color` | `inherit` | `inherit` | Header cell text color. |
| `--x-table-cell-sort-color` | `rgba(0,0,0,0.4)` | `rgba(255,255,255,0.4)` | Sort icon default color. |
| `--x-table-cell-sort-hover-color` | `rgba(0,0,0,0.7)` | `rgba(255,255,255,0.7)` | Sort icon hover color. |
| `--x-table-cell-sticky-bg` | `#ffffff` | `#1f2937` | Sticky cell background (must be opaque). |
| `--x-table-cell-focus-ring` | `rgba(59,130,246,0.5)` | same | Sort button focus ring. |

### Typography

| Property | Default | Description |
|----------|---------|-------------|
| `--x-table-cell-font-size` | `inherit` | Cell font size. |
| `--x-table-cell-header-font-weight` | `600` | Header cell font weight. |

### Motion

| Property | Default | Description |
|----------|---------|-------------|
| `--x-table-cell-transition-duration` | `150ms` | Sort button color transition duration. |

### State

| Property | Default | Description |
|----------|---------|-------------|
| `--x-table-cell-disabled-opacity` | `0.45` | Opacity when disabled. |

---

## Accessibility

### ARIA Role Mapping

| `type` | `scope` | `role` on host |
|--------|---------|----------------|
| `"data"` | any | `"cell"` |
| `"header"` | `"col"` (default) | `"columnheader"` |
| `"header"` | `"colgroup"` | `"columnheader"` |
| `"header"` | `"row"` | `"rowheader"` |
| `"header"` | `"rowgroup"` | `"rowheader"` |

### `aria-sort`

Set on the host element when either `sortable` is present or `sort-direction` is not `"none"`:

| `sort-direction` | `aria-sort` |
|------------------|-------------|
| `"none"` | `"none"` |
| `"asc"` | `"ascending"` |
| `"desc"` | `"descending"` |

When neither `sortable` nor a non-`"none"` direction is set, `aria-sort` is removed.

### `aria-disabled`

Set to `"true"` on the host when `disabled` is present.

### Sort Button

The sort button's `aria-label` describes the **action that will occur** on activation, not the current state:

| Current `sort-direction` | Button `aria-label` |
|--------------------------|---------------------|
| `"none"` | `"Sort ascending"` |
| `"asc"` | `"Sort descending"` |
| `"desc"` | `"Remove sort"` |

### Keyboard

| Key | Target | Action |
|-----|--------|--------|
| `Enter` / `Space` | Sort button | Activates sort, fires `x-table-cell-sort` |

---

## Sort Cycling

The component is **stateless** — clicking the sort button fires `x-table-cell-sort` and does nothing else. The consumer must update the `sort-direction` attribute:

```js
cell.addEventListener('x-table-cell-sort', (e) => {
  // e.detail.direction is the next direction
  cell.setAttribute('sort-direction', e.detail.direction);
});
```

Direction cycles: `"none"` → `"asc"` → `"desc"` → `"none"`.

To cancel a sort action (e.g. for async validation):

```js
cell.addEventListener('x-table-cell-sort', (e) => {
  e.preventDefault();  // sort-direction is not updated
});
```

---

## Grid Span (col-span / row-span)

When `x-table-cell` is placed in a CSS Grid context (as provided by a future `x-table-row`), setting `col-span` and `row-span` drives the cell's grid placement:

- `col-span="2"` → `style.gridColumn = "span 2"` on the host
- `row-span="3"` → `style.gridRow = "span 3"` on the host
- Value `1` clears the inline style (no span)

```html
<!-- Spans 2 columns in an x-table-row grid -->
<x-table-cell col-span="2">Wide cell</x-table-cell>
```

---

## Sticky Positioning

Sticky cells require an opaque background. The `--x-table-cell-sticky-bg` custom property controls this:

```css
x-table-cell {
  --x-table-cell-sticky-bg: #ffffff; /* or match your row background */
}
```

```html
<x-table-cell sticky="start">Frozen first column</x-table-cell>
```

**Note:** `sticky` positioning requires a scrollable ancestor container.

---

## Parent Coordination Events

When `x-table-row` and `x-table` are implemented, they will consume the `x-table-cell-connected` and `x-table-cell-disconnected` events to track cells. The cell fires these unconditionally — no configuration needed.

---

## Examples

### Basic data cell

```html
<x-table-cell>Hello world</x-table-cell>
```

### Header cell with sort

```html
<x-table-cell type="header" sortable sort-direction="none">
  Name
</x-table-cell>

<script>
  document.querySelector('x-table-cell').addEventListener('x-table-cell-sort', (e) => {
    e.target.setAttribute('sort-direction', e.detail.direction);
  });
</script>
```

### Truncated cell

```html
<x-table-cell truncate style="width: 150px;">
  This is a very long text that will be truncated
</x-table-cell>
```

### Spanning cells (in a CSS Grid row)

```html
<div style="display: grid; grid-template-columns: repeat(4, 1fr);">
  <x-table-cell col-span="2">Spans two columns</x-table-cell>
  <x-table-cell>Col 3</x-table-cell>
  <x-table-cell>Col 4</x-table-cell>
</div>
```

### Sticky first column

```html
<x-table-cell sticky="start"
              style="--x-table-cell-sticky-bg: #fff;">
  Row label
</x-table-cell>
```

### Custom sort icon via slot

```html
<x-table-cell type="header" sortable>
  Status
  <svg slot="sort-icon" width="16" height="16" viewBox="0 0 16 16">
    <path d="M8 4l4 8H4l4-8z" fill="currentColor"/>
  </svg>
</x-table-cell>
```

### JavaScript API

```js
const cell = document.querySelector('x-table-cell');

// Property access
cell.type = 'header';
cell.sortable = true;
cell.sortDirection = 'asc';
cell.colSpan = 2;
cell.disabled = false;

// Events
cell.addEventListener('x-table-cell-sort', (e) => {
  cell.sortDirection = e.detail.direction;
});
```

### ClojureScript / Hiccup

```clojure
[:x-table-cell {:type "header" :sortable "" :sort-direction "none"
                :on-x-table-cell-sort #(set! (.-sortDirection (.-target %))
                                             (.-direction (.-detail %)))}
 "Column Name"]
```
