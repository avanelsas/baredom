# x-tree

A tree of hierarchical data. A person opens and closes branches and selects items, by pointer or
by keyboard.

---

## Tag name

```html
<x-tree label="Files" selection="single" guides></x-tree>

<script type="module">
  const tree = document.querySelector('x-tree');
  tree.items = [
    { id: "src", label: "src", children: [
      { id: "main", label: "main.cljs", meta: "4 KB" } ] },
    { id: "readme", label: "README.md" } ];
  tree.expanded = ["src"];
</script>
```

---

## Observed Attributes

| Attribute      | Type        | Default      | Notes |
|----------------|-------------|--------------|-------|
| `items`        | JSON string | (none)       | A JSON array of top-level items, see _Items_. |
| `expanded`     | JSON string | (none)       | A JSON array of the ids of open branches. |
| `selected`     | JSON string | (none)       | A JSON array of the ids of selected items. |
| `selection`    | enum        | `"single"`   | `none`, `single`, `leaf` or `multiple`. An unknown value gives the default. |
| `label`        | string      | (none)       | The accessible name of the tree. |
| `loading-text` | string      | `"Loading…"` | The text of the loading row. |
| `guides`       | boolean     | `false`      | Draw indent guides. |

---

## Properties

Each attribute has a property that reflects it: `items`, `expanded`, `selected`, `selection`,
`label`, `loadingText` and `guides`.

| Property                        | Type  | Notes |
|---------------------------------|-------|-------|
| `items`, `expanded`, `selected` | array | Writing an array, or JSON text that holds one, writes its JSON. Anything else removes the attribute. Reading parses the attribute into a new value, or gives `null`. |

---

## Items

| Field      | Type    | Required | Notes |
|------------|---------|----------|-------|
| `id`       | string  | yes      | A stable key. `expanded`, `selected` and events name items by it. |
| `label`    | string  | yes      | The text, the accessible name and the typeahead text. |
| `children` | array   | no       | Nested items, in order. |
| `disabled` | boolean | no       | The item cannot be selected. It still takes focus, opens and closes. |
| `lazy`     | boolean | no       | The children are not loaded yet. |
| `meta`     | string  | no       | Short text after the label: a count, a size, a date. |

- A **branch** has non-empty `children`, or has `lazy`. It has a chevron and can open.
- A **leaf** is every other item.
- An **end item** has no children: every leaf, and a lazy branch before its children load.
- The tree drops an item, with its children, when it has no `id` or `label`, or when an earlier
  item in tree order has the same `id`. It writes a console warning for each.

### Ids the tree counts

The tree ignores an id that names no item, and an id in `expanded` that names a leaf. In
`selected` it counts:

| `selection` | Counts |
|-------------|--------|
| `none`      | No id. |
| `single`    | The first id in tree order. |
| `leaf`      | The first leaf id in tree order. |
| `multiple`  | Every end item id. |

A write by the tree keeps every id that names no item, after the ids it counts, in their order.
Such an id can name a child of a lazy branch that has not loaded yet. In `single` and `leaf`, a
select writes the selected id only.

---

## Behaviour

### Activating a row

A click on a row, Enter and Space all activate it:

| `selection` | Activate a branch | Activate a leaf   |
|-------------|-------------------|-------------------|
| `none`      | Open or close.    | Nothing.          |
| `single`    | Select.           | Select.           |
| `leaf`      | Open or close.    | Select.           |
| `multiple`  | Check or uncheck. | Check or uncheck. |

A click on the chevron opens or closes the branch in every mode. On a disabled item, select and
check do nothing. Open and close still work.

### Cascading in `multiple`

A branch is `checked` when all its enabled end items are selected, `mixed` when some are, and
`unchecked` when none are. A branch whose end items are all disabled counts all of them. Checking
a branch adds all its enabled end items. Unchecking removes them. A `mixed` branch checks.
Disabled end items keep their state.

### Focus

- The tree is one Tab stop. The focused item has `tabindex="0"`. All others have `-1`.
- Every gesture focuses the item it names. Canceling an event does not undo the focus.
- When focus leaves the tree, the focused item stays. Tab returns to it.
- When the focused item is hidden or removed, focus goes to its nearest visible ancestor, else
  the first selected item or its nearest visible ancestor, else the first visible item.
- Moving focus selects nothing and sends no event.

### Lazy branches

A lazy branch that is open and has no children shows the loading row, and the tree sends
`x-tree-lazy-load`. Add `children` and remove `lazy`. When there are no children, remove `lazy`
and the item becomes a leaf.

---

## Keyboard

| Key           | Action |
|---------------|--------|
| ↓ / ↑         | Focus the next / previous visible item. |
| →             | Closed branch: open. Open branch: focus the first child. Leaf: nothing. |
| ←             | Open branch: close. Otherwise: focus the parent. |
| Home / End    | Focus the first / last visible item. |
| Enter / Space | Activate. |
| `*`           | Open every branch that shares the parent of the focused item, its own included. |
| A character   | Focus the next visible item whose label starts with that character. Wraps. |

---

## Events

| Event              | Detail | Cancelable | Fires when |
|--------------------|--------|------------|------------|
| `x-tree-toggle`    | `{ id, nextExpanded, source }`          | yes | Before the tree writes `expanded`. |
| `x-tree-select`    | `{ id, changed, nextSelected, source }` | yes | Before the tree writes `selected`. |
| `x-tree-change`    | `{ attribute }`                         | no  | After the tree wrote an attribute. |
| `x-tree-lazy-load` | `{ id }`                                | no  | After a lazy branch with no children opened. |

All events bubble and are composed. The tree sends events for its own gestures only, never for a
write by the consumer. A canceled request writes nothing and sends no `x-tree-change`. One
gesture sends its events in this order: the request, `x-tree-change`, then `x-tree-lazy-load`.

- `id` is the item the gesture names. For `*` it is the focused item, and one `x-tree-toggle`
  opens every sibling branch.
- `nextExpanded` and `nextSelected` are the JSON text the tree writes: the counted ids in tree
  order, then the kept ids.
- `changed` is an array of every id that enters or leaves `selected`.
- `attribute` is `"expanded"` or `"selected"`. The property of that name holds the new value.
- `source` is `"pointer"` or `"keyboard"`.

The request of `x-tree-toggle` is `expanded`, and of `x-tree-select` is `selected`. To answer a
request yourself, cancel it and write the attribute. `listen` from BareMirror cancels a request
when the attribute does not show the exact text of `nextExpanded` or `nextSelected` after the
dispatch. Write that text as it is, or write the property with the parsed array. The same ids in
another order are other text.

```js
tree.addEventListener('x-tree-select', e => {
  if (e.detail.changed.includes('locked')) e.preventDefault();
});

tree.addEventListener('x-tree-lazy-load', async e => {
  const children = await fetchChildren(e.detail.id);
  tree.items = withChildren(tree.items, e.detail.id, children);
});
```

---

## CSS Custom Properties

| Property | Default (light) | Default (dark) | Notes |
|----------|-----------------|----------------|-------|
| `--x-tree-font-family` | `var(--x-font-family,inherit)` | | Font of the rows |
| `--x-tree-font-size` | `var(--x-font-size-sm,0.875rem)` | | Size of the rows |
| `--x-tree-color` | `var(--x-color-text,#1f2937)` | `var(--x-color-text,#e5e7eb)` | Label text |
| `--x-tree-meta-color` | `var(--x-color-text-muted,#6b7280)` | `var(--x-color-text-muted,#9ca3af)` | Meta text |
| `--x-tree-row-height` | `2rem` | | Height of a row |
| `--x-tree-row-padding` | `0 var(--x-space-sm,0.5rem)` | | Padding of a row |
| `--x-tree-row-radius` | `var(--x-radius-sm,4px)` | | Corners of a row |
| `--x-tree-indent` | `1.25rem` | | Indent per level |
| `--x-tree-gap` | `0.375rem` | | Space between chevron, checkbox, label and meta |
| `--x-tree-hover-bg` | `color-mix(in srgb,var(--x-color-text,#000) 6%,transparent)` | `color-mix(in srgb,var(--x-color-text,#fff) 6%,transparent)` | Row on hover |
| `--x-tree-selected-bg` | `color-mix(in srgb,var(--x-color-primary,#2563eb) 12%,transparent)` | `color-mix(in srgb,var(--x-color-primary,#60a5fa) 20%,transparent)` | Selected row |
| `--x-tree-selected-color` | `var(--x-tree-color)` | | Selected row text |
| `--x-tree-focus-ring` | `var(--x-color-focus-ring,#2563eb)` | `var(--x-color-focus-ring,#60a5fa)` | Focus outline |
| `--x-tree-disabled-opacity` | `0.5` | | Disabled row |
| `--x-tree-chevron-color` | `var(--x-color-text-muted,#6b7280)` | `var(--x-color-text-muted,#9ca3af)` | Chevron |
| `--x-tree-chevron-size` | `1rem` | | Chevron size |
| `--x-tree-checkbox-color` | `var(--x-color-primary,#2563eb)` | `var(--x-color-primary,#60a5fa)` | Checkbox fill when checked or mixed |
| `--x-tree-checkbox-size` | `1rem` | | Checkbox size |
| `--x-tree-guide-width` | `var(--x-border-width,1px)` | | Guide width |
| `--x-tree-guide-color` | `var(--x-color-border,#e5e7eb)` | `var(--x-color-border,#374151)` | Guide |
| `--x-tree-guide-style` | `solid` | | Guide line style |
| `--x-tree-guide-active-color` | `var(--x-tree-guide-color)` | | The guide of the group that holds the focused item |
| `--x-tree-transition-duration` | `var(--x-transition-duration,150ms)` | | Chevron rotation and colour change |

```css
x-tree {
  --x-tree-indent: 1rem;
  --x-tree-guide-active-color: var(--x-color-primary);
}
```

The host has `data-empty` while no item is valid:

```css
x-tree[data-empty] { display: none; }
```

---

## Shadow DOM Parts

| Part       | Element                | Notes |
|------------|------------------------|-------|
| `tree`     | `<ul role="tree">`     | The root list. |
| `group`    | `<ul role="group">`    | The children of an item. Draws the guide. `data-active` on the active guide. `hidden` while empty. |
| `item`     | `<li role="treeitem">` | `data-level`, `data-branch`, `data-expanded`, `data-selected`, `data-disabled`. `data-selected` marks a selected or a checked item. |
| `row`      | `<div>`                | The line a person clicks. |
| `chevron`  | `<span>`               | Branches only. Rotates when open. |
| `checkbox` | `<span>`               | `multiple` only. `data-state` is `checked`, `mixed` or `unchecked`. |
| `label`    | `<span>`               | The label text. |
| `meta`     | `<span>`               | Only with `meta`. |
| `loading`  | `<li>`                 | The loading row. |

```css
x-tree::part(meta) { font-variant-numeric: tabular-nums; }
```

---

## Writes on other nodes

None. The tree writes only its shadow root and its own `expanded` and `selected`.

---

## Accessibility

- The root list has `role="tree"` and `aria-label` from `label`. In `multiple` it has
  `aria-multiselectable="true"`.
- Each item has `role="treeitem"`, `aria-level`, `aria-setsize` and `aria-posinset`.
- A branch has `aria-expanded`. A leaf does not.
- In `single` and `leaf`, each selectable item, a disabled one too, has `aria-selected`: `true`
  or `false`. In
  `multiple`, each item has `aria-checked`: `true`, `false` or `mixed`.
- A disabled item has `aria-disabled="true"`.
- A child list has `role="group"`.
- A branch that shows the loading row has `aria-busy="true"`. The loading row is not a
  `treeitem`.
- Focus shows with `:focus-visible`. Selection shows with a fill and a checkbox, so colour is not
  the only cue in `multiple`.

## Guides

With `guides`, each open group draws one line on its start edge, under the chevron of its
branch. The line ends where the group ends. The guide of the group that holds the focused item
draws in `--x-tree-guide-active-color`.

## Motion

The chevron rotation and colour changes use `--x-tree-transition-duration`. Children appear and
leave at once. Under `prefers-reduced-motion: reduce` the duration is `0ms`.

## Responsive

- Rows are at least 44px tall under `(pointer: coarse)`.
- Long labels wrap. `meta` stays on the line end.

## Size

The tree handles about 2000 items. The children of a closed branch are not in the DOM.

---

## Usage Examples

### Folders that open, files that select

```html
<x-tree label="Files" selection="leaf" guides></x-tree>
```

### Checkboxes

```html
<x-tree label="Permissions" selection="multiple"></x-tree>
```

```js
tree.addEventListener('x-tree-change', e => {
  if (e.detail.attribute === 'selected') save(tree.selected);
});
```
