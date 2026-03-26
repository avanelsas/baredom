# x-table

A stateless Web Component that acts as the grid container for `x-table-row` and `x-table-cell` children. It defines the CSS Grid column tracks that `x-table-row` subgrids inherit, provides correct ARIA table/grid semantics, and coordinates sorting and row selection.

```html
<x-table columns="2fr 1fr 120px" selectable="single" bordered caption="Team Members">

  <x-table-row>
    <x-table-cell type="header" scope="col" sortable>Name</x-table-cell>
    <x-table-cell type="header" scope="col">Role</x-table-cell>
    <x-table-cell type="header" scope="col" align="end">Joined</x-table-cell>
  </x-table-row>

  <x-table-row interactive row-index="1">
    <x-table-cell truncate>Alice Wonderland</x-table-cell>
    <x-table-cell>Admin</x-table-cell>
    <x-table-cell align="end">2024-01-15</x-table-cell>
  </x-table-row>

</x-table>
```

---

## Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `columns` | string | absent | CSS `grid-template-columns` value. A positive integer (e.g. `"4"`) expands to `repeat(4,1fr)`. Any other string is used as-is. Absent → no explicit column template. |
| `caption` | string | absent | Visible caption text rendered before the first row. Also sets `aria-label` on the host. |
| `selectable` | `"none"\|"single"\|"multi"` | `"none"` | Row selection mode. `"single"` and `"multi"` switch `role` to `"grid"` and enable automatic selection management via `x-table-row-click` events. `"multi"` also adds `aria-multiselectable="true"`. |
| `striped` | boolean | absent | Applies an alternating background to even-indexed `x-table-row` children. |
| `bordered` | boolean | absent | Renders an outer border and border-radius around the table. |
| `full-width` | boolean | absent | Sets `width:100%` on the host element. |
| `compact` | boolean | absent | Reduces cell padding by overriding `--x-table-cell-padding` for all descendant cells. |
| `row-count` | number | absent | Explicit `aria-rowcount` for windowed/virtual tables where only a subset of rows is in the DOM. |

---

## Properties

| Property | Type | Default | Reflects Attribute |
|----------|-----------|---------|--------------------|
| `columns` | `string` | `""` | `columns` |
| `caption` | `string` | `""` | `caption` |
| `selectable` | `string` | `"none"` | `selectable` |
| `striped` | `boolean` | `false` | `striped` |
| `bordered` | `boolean` | `false` | `bordered` |
| `fullWidth` | `boolean` | `false` | `full-width` |
| `compact` | `boolean` | `false` | `compact` |
| `rowCount` | `number\|null` | `null` | `row-count` |

```js
const table = document.querySelector('x-table');
table.columns    = '2fr 1fr 120px';
table.selectable = 'single';
table.bordered   = true;
table.fullWidth  = true;
table.rowCount   = 500; // for virtual scrolling
```

---

## Events

### `x-table-sort`

Fires when a sortable column header (`x-table-cell` with `sortable`) is clicked. `x-table` intercepts the lower-level `x-table-cell-sort` event, adds column index information, and re-fires as `x-table-sort`.

| Property | Value |
|----------|-------|
| `bubbles` | `true` |
| `composed` | `true` |
| `cancelable` | `true` |

**Detail:**

| Key | Type | Description |
|-----|------|-------------|
| `colIndex` | `number` | Zero-based column index of the clicked header cell |
| `direction` | `string` | New sort direction (`"asc"`, `"desc"`, or `"none"`) |
| `previousDirection` | `string` | Previous sort direction |

```js
table.addEventListener('x-table-sort', e => {
  // Update sort-direction on the header cell
  const headers = table.querySelectorAll('x-table-row:first-child x-table-cell');
  // Clear all, apply to clicked column
  headers.forEach((h, i) => {
    h.setAttribute('sort-direction', i === e.detail.colIndex ? e.detail.direction : 'none');
  });
});
```

### `x-table-row-select`

Fires when a row is clicked and `selectable` is not `"none"`. Fires **after** `x-table-row-click` but before `x-table` updates the row's selected state. Canceling this event prevents `x-table` from updating the DOM.

| Property | Value |
|----------|-------|
| `bubbles` | `true` |
| `composed` | `true` |
| `cancelable` | `true` |

**Detail:**

| Key | Type | Description |
|-----|------|-------------|
| `rowIndex` | `number` | Row index (from `row-index` attribute, or `0`) |
| `selected` | `boolean` | Whether the row will be selected after this action |
| `selectionMode` | `string` | `"single"` or `"multi"` |

```js
table.addEventListener('x-table-row-select', e => {
  if (someGuard) e.preventDefault(); // prevents selection DOM update
  console.log(`Row ${e.detail.rowIndex} → selected=${e.detail.selected}`);
});
```

---

## Slots

| Name | Description |
|------|-------------|
| *(default)* | `x-table-row` elements that form the table body and header |

---

## Parts

| Part | Element | Description |
|------|---------|-------------|
| `caption` | `div` | The visible caption text above the first row. Hidden when `caption` attribute is absent. |

---

## CSS Custom Properties

All custom properties are set on `:host` with automatic light/dark defaults.

| Property | Light Default | Dark Default | Description |
|---|---|---|---|
| `--x-table-border-color` | `rgba(0,0,0,0.1)` | `rgba(255,255,255,0.1)` | Outer border color (when `bordered`) |
| `--x-table-border-radius` | `8px` | `8px` | Border radius (when `bordered`) |
| `--x-table-stripe-bg` | `rgba(0,0,0,0.025)` | `rgba(255,255,255,0.03)` | Even-row background (when `striped`) |
| `--x-table-caption-color` | `inherit` | `inherit` | Caption text color |
| `--x-table-caption-font-size` | `0.875rem` | `0.875rem` | Caption font size |
| `--x-table-caption-font-weight` | `600` | `600` | Caption font weight |
| `--x-table-caption-padding` | `0 0 0.5rem` | `0 0 0.5rem` | Caption padding |
| `--x-table-compact-padding` | `0.25rem 0.5rem` | `0.25rem 0.5rem` | Cell padding override when `compact` |

```css
x-table {
  --x-table-border-color: #e5e7eb;
  --x-table-stripe-bg: rgba(249, 250, 251, 1);
}
```

---

## Accessibility

### ARIA Attributes

| Condition | Attribute | Value |
|---|---|---|
| Always | `role` | `"table"` (or `"grid"` when selectable) |
| `selectable="single"` | `role` | `"grid"` |
| `selectable="multi"` | `role` | `"grid"`, `aria-multiselectable` = `"true"` |
| `caption` present | `aria-label` | caption text |
| `row-count` is valid | `aria-rowcount` | the integer as string |

### Keyboard Interaction

`x-table` itself is not focusable. Keyboard focus is managed by `x-table-row` (when `interactive`). Tab moves focus between interactive rows; Enter/Space fires row click.

---

## Layout / Subgrid

`x-table` is `display:grid`. Its `grid-template-columns` value comes from the `columns` attribute:

```css
/* Integer shorthand */
<x-table columns="4">          →  grid-template-columns: repeat(4,1fr)

/* CSS value */
<x-table columns="2fr 1fr 120px">  →  grid-template-columns: 2fr 1fr 120px

/* Absent */
<x-table>                      →  grid-template-columns: (not set, auto)
```

Each `x-table-row` child has `grid-template-columns:subgrid; grid-column:1/-1` — it inherits the column tracks from `x-table` and its `x-table-cell` children align perfectly across rows.

The slot inside `x-table`'s shadow DOM has `display:contents`, making `x-table-row` elements direct grid items of the host grid.

---

## Selection Management

When `selectable` is set, `x-table` listens to `x-table-row-click` events and automatically manages the `selected` attribute on rows:

- **`selectable="single"`** — clicking a row removes `selected` from all other rows and sets it on the clicked row. Clicking the already-selected row deselects it.
- **`selectable="multi"`** — clicking a row toggles its `selected` attribute. Other rows are not affected.
- **`selectable="none"` (default)** — `x-table` does not manage selection. The app handles `x-table-row-click` events directly.

In all modes, `x-table-row` must have the `interactive` attribute set to receive clicks.

---

## Sort Coordination

`x-table` intercepts `x-table-cell-sort` events that bubble up through the DOM, computes the zero-based column index of the firing cell, stops the original event from propagating further, and fires `x-table-sort` instead. This means consumers should listen to `x-table-sort` rather than `x-table-cell-sort` when using the full `x-table` → `x-table-row` → `x-table-cell` composition.

```js
// Recommended: listen at x-table level
table.addEventListener('x-table-sort', e => {
  // e.detail.colIndex is the column that was clicked
});

// Discouraged when using x-table (intercepted and stopped):
// document.addEventListener('x-table-cell-sort', ...)
```

---

## Usage Examples

### Basic read-only table

```html
<x-table columns="3" bordered>
  <x-table-row>
    <x-table-cell type="header" scope="col">Name</x-table-cell>
    <x-table-cell type="header" scope="col">Role</x-table-cell>
    <x-table-cell type="header" scope="col" align="end">Date</x-table-cell>
  </x-table-row>
  <x-table-row>
    <x-table-cell>Alice</x-table-cell>
    <x-table-cell>Admin</x-table-cell>
    <x-table-cell align="end">2024-01-15</x-table-cell>
  </x-table-row>
</x-table>
```

### Selectable table with sorting

```html
<x-table id="users-table" columns="2fr 1fr 120px"
         selectable="single" bordered striped caption="Users">
  <x-table-row>
    <x-table-cell type="header" scope="col" sortable sort-direction="asc">Name</x-table-cell>
    <x-table-cell type="header" scope="col" sortable>Role</x-table-cell>
    <x-table-cell type="header" scope="col" align="end">Joined</x-table-cell>
  </x-table-row>
  <x-table-row interactive row-index="1">
    <x-table-cell truncate>Alice Wonderland</x-table-cell>
    <x-table-cell>Admin</x-table-cell>
    <x-table-cell align="end">2024-01-15</x-table-cell>
  </x-table-row>
  <x-table-row interactive row-index="2">
    <x-table-cell truncate>Bob Builder</x-table-cell>
    <x-table-cell>Member</x-table-cell>
    <x-table-cell align="end">2024-03-02</x-table-cell>
  </x-table-row>
</x-table>

<script>
  const table = document.getElementById('users-table');

  table.addEventListener('x-table-sort', e => {
    const { colIndex, direction } = e.detail;
    // Update sort directions in the header row
    const headerCells = table.querySelectorAll('x-table-row:first-child x-table-cell');
    headerCells.forEach((cell, i) => {
      cell.setAttribute('sort-direction', i === colIndex ? direction : 'none');
    });
    // Fetch sorted data and update rows...
  });

  table.addEventListener('x-table-row-select', e => {
    console.log(`Row ${e.detail.rowIndex} selected=${e.detail.selected}`);
  });
</script>
```

### Virtual / windowed table

```html
<x-table columns="2fr 1fr" row-count="10000" bordered full-width>
  <!-- only visible rows in DOM -->
</x-table>
```

### JavaScript property API

```js
const table = document.querySelector('x-table');
table.columns    = '200px 1fr 1fr 120px';
table.selectable = 'multi';
table.striped    = true;
table.bordered   = true;
table.compact    = true;
table.rowCount   = 500;
```

### ClojureScript / hiccup

```clojure
[:x-table {:columns "2fr 1fr 120px" :selectable "single" :bordered true}
  [:x-table-row
    [:x-table-cell {:type "header"} "Name"]
    [:x-table-cell {:type "header"} "Role"]
    [:x-table-cell {:type "header" :align "end"} "Date"]]
  [:x-table-row {:interactive true :row-index 1}
    [:x-table-cell "Alice"]
    [:x-table-cell "Admin"]
    [:x-table-cell {:align "end"} "2024-01-15"]]]
```
