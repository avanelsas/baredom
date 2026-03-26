# x-table-row

A stateless Web Component that represents a table row. Wraps `x-table-cell` children in a CSS Grid subgrid row with ARIA row semantics, selection state, and optional interactivity.

```html
<div style="display:grid;grid-template-columns:repeat(4,1fr)">
  <x-table-row>
    <x-table-cell type="header">Name</x-table-cell>
    <x-table-cell type="header">Status</x-table-cell>
    <x-table-cell type="header">Role</x-table-cell>
    <x-table-cell type="header">Joined</x-table-cell>
  </x-table-row>
  <x-table-row interactive selected>
    <x-table-cell>Alice</x-table-cell>
    <x-table-cell>Active</x-table-cell>
    <x-table-cell>Admin</x-table-cell>
    <x-table-cell>2024-01-15</x-table-cell>
  </x-table-row>
</div>
```

---

## Attributes

| Attribute     | Type    | Default | Description |
|---------------|---------|---------|-------------|
| `selected`    | boolean | absent  | Row is in the selected state. Sets `aria-selected="true"` and applies selection styling. |
| `disabled`    | boolean | absent  | Row is disabled. Sets `aria-disabled="true"` and suppresses click events and interaction. |
| `interactive` | boolean | absent  | Row responds to hover and click. Sets `tabindex="0"`, focus ring, cursor pointer, and hover background. Also sets `aria-selected="false"` when not selected (indicates row is in a selection context). |
| `row-index`   | number  | absent  | Positive integer for `aria-rowindex`. Useful with virtual/windowed rendering where only a subset of rows are in the DOM. Invalid or zero values are ignored. |

---

## Properties

| Property      | Type      | Default | Reflects Attribute |
|---------------|-----------|---------|--------------------|
| `selected`    | `boolean` | `false` | `selected`         |
| `disabled`    | `boolean` | `false` | `disabled`         |
| `interactive` | `boolean` | `false` | `interactive`      |
| `rowIndex`    | `number\|null` | `null` | `row-index`   |

```js
const row = document.querySelector('x-table-row');

row.selected    = true;
row.interactive = true;
row.disabled    = false;
row.rowIndex    = 3;
```

---

## Events

### `x-table-row-click`

Fires when an interactive, enabled row receives a click or keyboard activation (Enter/Space).

| Property    | Value |
|-------------|-------|
| `bubbles`   | `true` |
| `composed`  | `true` |
| `cancelable`| `true` |

**Detail:**

| Key        | Type      | Description |
|------------|-----------|-------------|
| `rowIndex` | `number`  | Row index (from `row-index` attribute, or `0` if absent) |
| `selected` | `boolean` | Whether the row is currently selected |
| `disabled` | `boolean` | Whether the row is currently disabled |

```js
row.addEventListener('x-table-row-click', e => {
  if (e.defaultPrevented) return;
  e.target.toggleAttribute('selected');
});
```

### `x-table-row-connected`

Fires on `connectedCallback`. Intended for parent `x-table` to track which rows are present.

| Property    | Value |
|-------------|-------|
| `bubbles`   | `true` |
| `composed`  | `true` |
| `cancelable`| `false` |

**Detail:**

| Key          | Type      | Description |
|--------------|-----------|-------------|
| `rowIndex`   | `number`  | Row index (or `0` if absent) |
| `selected`   | `boolean` | Current selected state |
| `disabled`   | `boolean` | Current disabled state |
| `interactive`| `boolean` | Current interactive state |

### `x-table-row-disconnected`

Fires on `disconnectedCallback`. Intended for parent `x-table` cleanup.

| Property    | Value |
|-------------|-------|
| `bubbles`   | `true` |
| `composed`  | `true` |
| `cancelable`| `false` |

**Detail:** `{}`

---

## Slots

| Name      | Description |
|-----------|-------------|
| *(default)* | `x-table-cell` elements that make up the row's columns |

---

## Parts

None. The shadow DOM contains only a `<style>` element and the default `<slot>`. There are no exposed shadow parts.

---

## CSS Custom Properties

All custom properties are set on `:host` with automatic light/dark defaults.

| Property | Light Default | Dark Default | Description |
|---|---|---|---|
| `--x-table-row-bg` | `transparent` | `transparent` | Base row background |
| `--x-table-row-hover-bg` | `rgba(0,0,0,0.03)` | `rgba(255,255,255,0.04)` | Hover background (interactive rows) |
| `--x-table-row-selected-bg` | `rgba(59,130,246,0.08)` | `rgba(99,160,255,0.12)` | Selected row background |
| `--x-table-row-selected-hover-bg` | `rgba(59,130,246,0.12)` | `rgba(99,160,255,0.16)` | Selected + hover background |
| `--x-table-row-focus-ring` | `rgba(59,130,246,0.5)` | `rgba(59,130,246,0.5)` | Focus outline color |
| `--x-table-row-transition-duration` | `150ms` | `150ms` | Background transition duration |
| `--x-table-row-disabled-opacity` | `0.45` | `0.45` | Opacity when disabled |
| `--x-table-row-cursor` | `pointer` | `pointer` | Cursor when interactive |

```css
/* Theme override example */
x-table-row {
  --x-table-row-selected-bg: rgba(16, 185, 129, 0.1);
  --x-table-row-hover-bg: rgba(0, 0, 0, 0.05);
}
```

---

## Accessibility

### ARIA Attributes

| Condition | Attribute | Value |
|---|---|---|
| Always | `role` | `"row"` |
| `selected` present | `aria-selected` | `"true"` |
| `interactive` present, not `selected` | `aria-selected` | `"false"` |
| Neither `interactive` nor `selected` | `aria-selected` | *(absent)* |
| `disabled` present | `aria-disabled` | `"true"` |
| `row-index` is a valid positive integer | `aria-rowindex` | the integer as a string |

### Keyboard Interaction

The row is keyboard-focusable (`tabindex="0"`) only when `interactive` is set and `disabled` is absent.

| Key | Behaviour |
|-----|-----------|
| `Tab` | Moves focus to/from the row |
| `Enter` | Fires `x-table-row-click` |
| `Space` | Fires `x-table-row-click` (browser scroll prevented) |

---

## Layout / Subgrid

`x-table-row` uses CSS Grid subgrid to inherit column tracks from its parent grid container:

```css
:host {
  display: grid;
  grid-template-columns: subgrid;
  grid-column: 1 / -1;
}
```

When used inside an `x-table` (or any `display:grid` container with defined column tracks), the row's `x-table-cell` children automatically align to the parent's columns. The shadow DOM's `<slot>` has `display:contents`, making slotted cells direct grid items of the host.

**Standalone use** (without a grid parent): `subgrid` resolves to auto-placement. `x-table-cell` children lay out in a single row with auto-sized columns. This is useful for testing or simple layouts.

---

## Parent Coordination Events

`x-table-row` fires `x-table-row-connected` and `x-table-row-disconnected` so that a future `x-table` parent can track the rows it contains without storing state on the row itself. The events bubble and are composed, so `x-table` can listen once on its own element.

`x-table-cell` also fires `x-table-cell-connected` / `x-table-cell-disconnected` events. These bubble through `x-table-row` to `x-table` unchanged — `x-table-row` does not intercept them.

---

## Usage Examples

### Basic row with cells

```html
<x-table-row>
  <x-table-cell>Value A</x-table-cell>
  <x-table-cell>Value B</x-table-cell>
  <x-table-cell>Value C</x-table-cell>
</x-table-row>
```

### Selectable interactive row

```html
<x-table-row interactive row-index="1">
  <x-table-cell>Alice</x-table-cell>
  <x-table-cell>Admin</x-table-cell>
</x-table-row>
```

```js
document.querySelector('x-table-row').addEventListener('x-table-row-click', e => {
  e.target.toggleAttribute('selected');
});
```

### Selected row

```html
<x-table-row selected interactive>
  <x-table-cell>Pre-selected row</x-table-cell>
</x-table-row>
```

### Disabled row

```html
<x-table-row disabled interactive>
  <x-table-cell>Cannot be clicked</x-table-cell>
</x-table-row>
```

### Cancel row click (prevent selection)

```js
row.addEventListener('x-table-row-click', e => {
  if (someGuard) e.preventDefault();
  // after preventDefault: do not update selected state
});
```

### JavaScript property API

```js
const row = document.querySelector('x-table-row');
row.interactive = true;
row.rowIndex    = 2;
row.selected    = true;
```

### ClojureScript / hiccup

```clojure
[:x-table-row {:interactive true :row-index 1}
  [:x-table-cell "Alice"]
  [:x-table-cell "Admin"]]
```

### Full grid layout with x-table-cell

```html
<div style="display:grid;grid-template-columns:2fr 1fr 120px;border:1px solid #e5e7eb;border-radius:8px;overflow:hidden">

  <x-table-row>
    <x-table-cell type="header" scope="col">Name</x-table-cell>
    <x-table-cell type="header" scope="col">Role</x-table-cell>
    <x-table-cell type="header" scope="col" align="end">Actions</x-table-cell>
  </x-table-row>

  <x-table-row interactive row-index="1">
    <x-table-cell truncate>Alice Wonderland</x-table-cell>
    <x-table-cell>Admin</x-table-cell>
    <x-table-cell align="end">Edit</x-table-cell>
  </x-table-row>

  <x-table-row interactive row-index="2" selected>
    <x-table-cell truncate>Bob Builder</x-table-cell>
    <x-table-cell>Member</x-table-cell>
    <x-table-cell align="end">Edit</x-table-cell>
  </x-table-row>

  <x-table-row interactive row-index="3" disabled>
    <x-table-cell>Charlie Chaplin</x-table-cell>
    <x-table-cell>Suspended</x-table-cell>
    <x-table-cell align="end">—</x-table-cell>
  </x-table-row>

</div>
```
