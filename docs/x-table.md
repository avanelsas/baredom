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
| `columns` | string | absent | CSS `grid-template-columns` value. A positive integer (e.g. `"4"`) expands to `repeat(4,minmax(0,1fr))` (equal-width columns). Any other string is used as-is. Absent → no explicit column template. |
| `caption` | string | absent | Visible caption text rendered before the first row. Also sets `aria-label` on the host. |
| `selectable` | `"none"\|"single"\|"multi"` | `"none"` | Row selection mode. `"single"` and `"multi"` switch `role` to `"grid"`, and a click on a row asks for a new `value`. `"multi"` also adds `aria-multiselectable="true"`. |
| `striped` | boolean | absent | Applies an alternating background to even-indexed `x-table-row` children. |
| `bordered` | boolean | absent | Renders an outer border and border-radius around the table. |
| `full-width` | boolean | absent | Sets `width:100%` on the host element. |
| `compact` | boolean | absent | Reduces cell padding by overriding `--x-table-cell-padding` for all descendant cells. |
| `row-count` | number | absent | Explicit `aria-rowcount` for windowed/virtual tables where only a subset of rows is in the DOM. |
| `value` | string | absent | The selected rows, as a list of row values in `value-format`. See [Selection Management](#selection-management). |
| `value-format` | `"csv"\|"json"` | `"csv"` | How `value` lists several values: comma-separated, or a JSON array of strings for values that hold a comma. |

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
| `value` | `string` | `""` | `value` |
| `valueFormat` | `string` | `null` | `value-format` |

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

Fires when a sortable column header (`x-table-cell` with `sortable`) is clicked. `x-table` hears the lower-level `x-table-cell-sort` request, adds column index information, and sends `x-table-sort`. The cell's request then bubbles on to the page. When neither is cancelled, the clicked cell writes the new `sort-direction`. Cancelling it keeps the cell as it was. The other headers keep their direction, so an app that sorts by one column at a time resets them.

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
  // The clicked cell writes its own direction. Reset the other columns.
  const headers = table.querySelectorAll('x-table-row:first-child x-table-cell');
  headers.forEach((h, i) => {
    if (i !== e.detail.colIndex) h.setAttribute('sort-direction', 'none');
  });
});
```

### `x-table-row-select`

Fires when a row with a `value` or a `row-index` is clicked and `selectable` is not `"none"`. It asks for a new `value`. Fires **after** `x-table-row-click`. When it is not cancelled, `x-table` writes `value`, and the rows show it. It is a request, so it is in `requestedValues`.

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
| `value` | `string` | The text of `value` that the click asks for |

```js
table.addEventListener('x-table-row-select', e => {
  if (someGuard) e.preventDefault(); // keeps the value as it is
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
| `--x-table-border-color` | `var(--x-color-border,rgba(0,0,0,0.1))` | `rgba(255,255,255,0.1)` | Outer border color (when `bordered`) |
| `--x-table-border-radius` | `var(--x-radius-md,8px)` | `8px` | Border radius (when `bordered`) |
| `--x-table-stripe-bg` | `color-mix(in srgb,var(--x-color-text,#000) 2.5%,transparent)`    | `color-mix(in srgb, var(--x-color-text, #fff) 3%, transparent)` | Even-row background (when `striped`) |
| `--x-table-caption-color` | `var(--x-color-text,inherit)` | `inherit` | Caption text color |
| `--x-table-caption-font-size` | `var(--x-font-size-sm,0.875rem)`  | `0.875rem` | Caption font size |
| `--x-table-caption-font-weight` | `var(--x-font-weight-semibold,600)`  | `600` | Caption font weight |
| `--x-table-caption-padding` | `0 0 0.5rem` | `0 0 0.5rem` | Caption padding |
| `--x-table-compact-padding` | `0.25rem 0.5rem` | `0.25rem 0.5rem` | Cell padding override when `compact` |

```css
x-table {
  --x-table-border-color: #e5e7eb;
  --x-table-stripe-bg: rgba(249, 250, 251, 1);
}
```

---

## Writes on other nodes

| Node | Attribute | When |
|------|-----------|------|
| each `x-table-row` child | `selected` | `value` names the row, or no longer names it |
| each `x-table-row` child | `data-stripe` | `striped` is on |

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
/* Integer shorthand — equal-width columns */
<x-table columns="4">          →  grid-template-columns: repeat(4,minmax(0,1fr))

/* CSS value */
<x-table columns="2fr 1fr 120px">  →  grid-template-columns: 2fr 1fr 120px

/* Absent */
<x-table>                      →  grid-template-columns: (not set, auto)
```

The integer shorthand expands to `minmax(0,1fr)`, not plain `1fr`. A bare `1fr` track keeps a `min-content` floor, so a column holding wide or unbreakable content (a long string, a button) bulges past its share and the columns come out uneven. `minmax(0,1fr)` removes that floor, giving genuinely equal columns whose content shrinks/ellipsises instead of overflowing — which is what `columns="N"` is meant to express. Pass an explicit template string when you want per-column sizing.

Each `x-table-row` child has `grid-template-columns:subgrid; grid-column:1/-1` — it inherits the column tracks from `x-table` and its `x-table-cell` children align perfectly across rows.

The slot inside `x-table`'s shadow DOM has `display:contents`, making `x-table-row` elements direct grid items of the host grid.

---

## Selection Management

`value` holds the selection, as a list of row values in `value-format` in every mode. Each row has a value: its `value` attribute, else its `row-index`. Two rows with the same value are selected together. `x-table` sets `selected` on each direct row whose value `value` names, and removes it from the others. It does so in every mode, so a table with `selectable="none"` can show a selection too. Write `value`, not `selected` on a row: `x-table` overwrites `selected` on its next render.

- **`selectable="single"`.** A click on a row asks for that row alone. A click on a selected row asks for no row.
- **`selectable="multi"`.** A click toggles the row in the list.
- **`selectable="none"` (default).** A click asks for nothing. With no `value`, the app owns `selected` on the rows and handles `x-table-row-click`.

A row with neither `value` nor `row-index` cannot be selected. A click on it sends `x-table-row-click` and no `x-table-row-select`. Only a click on a direct row counts: a nested table answers its own rows.

A table with no `value` leaves `selected` on its rows as it is. In single or multi mode, the first click builds `value` from the rows marked `selected`. In single mode it takes the first. Removing `value` selects no row.

In csv format, a row whose value holds a comma or outer whitespace cannot be selected: a click on it asks for nothing and warns in the console. Use `value-format="json"` for such values. A `value` that is not a JSON array of strings in json format selects no row and warns.

A listener on the row can cancel `x-table-row-click`, and the table then asks for nothing. A listener on the table runs too late to stop the request.

In all modes, `x-table-row` must have the `interactive` attribute set to receive clicks.

---

## Sort Coordination

`x-table` hears each `x-table-cell-sort` request as it bubbles up, computes the zero-based column index of the cell, and sends `x-table-sort`. A cancelled `x-table-sort` cancels the cell's request. The cell's request bubbles on, so a page can answer it too, as BareMirror's `listen` does with `requestedValues`.

```js
// The column that was clicked
table.addEventListener('x-table-sort', e => {
  console.log(e.detail.colIndex, e.detail.direction);
});

// The request of the cell itself, which bubbles on
document.addEventListener('x-table-cell-sort', e => {
  if (!allowed(e.detail.direction)) e.preventDefault(); // the cell keeps its direction
});
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
    const { colIndex } = e.detail;
    // The clicked cell writes its own direction. Reset the other columns.
    const headerCells = table.querySelectorAll('x-table-row:first-child x-table-cell');
    headerCells.forEach((cell, i) => {
      if (i !== colIndex) cell.setAttribute('sort-direction', 'none');
    });
    // Fetch sorted data and update rows...
  });

  table.addEventListener('x-table-row-select', e => {
    console.log(`Row ${e.detail.rowIndex} selected=${e.detail.selected}, value=${e.detail.value}`);
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
