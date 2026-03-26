# x-timeline

A coordinator component that wraps `x-timeline-item` children. Manages item indexing, last-item tracking, position assignment, and stripe alternation. Works together with [`x-timeline-item`](../../x_timeline_item/docs/x_timeline_item.md).

## Tag name

```html
<x-timeline>
  <x-timeline-item ...></x-timeline-item>
</x-timeline>
```

## Observed attributes

| Attribute  | Type    | Default   | Description |
|------------|---------|-----------|-------------|
| `label`    | string  | `""`      | Optional caption displayed above the items |
| `position` | string  | `"start"` | Controls which side item content appears on; see values below |
| `striped`  | boolean | absent    | When present, enables alternating stripe backgrounds on items |

### `position` values

| Value         | Item layout |
|---------------|-------------|
| `start`       | All items: content on the right of the track (default) |
| `end`         | All items: content on the left of the track |
| `alternating` | Even-indexed items use `start`, odd-indexed items use `end` |

## JS properties

| Property   | Type    | Default   | Mirrors attribute |
|------------|---------|-----------|-------------------|
| `label`    | string  | `""`      | `label`           |
| `position` | string  | `"start"` | `position`        |
| `striped`  | boolean | `false`   | `striped`         |

## Events

### `x-timeline-select`

Fires when the user clicks or activates an `x-timeline-item` child. The coordinator intercepts `x-timeline-item-click` (stopping its propagation) and re-dispatches this canonical event.

| Property     | Value                                 |
|--------------|---------------------------------------|
| `bubbles`    | `true`                                |
| `composed`   | `true`                                |
| `cancelable` | `false`                               |
| `detail`     | `{ index, status, label }` (see below) |

**Detail shape:**

| Field    | Type   | Description |
|----------|--------|-------------|
| `index`  | number | 0-based index of the selected item within the timeline |
| `status` | string | The item's `status` attribute value |
| `label`  | string | The item's `label` attribute value |

## Slots

| Slot      | Description |
|-----------|-------------|
| (default) | Projects `x-timeline-item` children |

## CSS custom properties

| Property                            | Default      | Description |
|-------------------------------------|--------------|-------------|
| `--x-timeline-gap`                  | `0`          | Gap between label and first item |
| `--x-timeline-label-color`          | `inherit`    | Label text color |
| `--x-timeline-label-font-size`      | `0.875rem`   | Label font size |
| `--x-timeline-label-font-weight`    | `600`        | Label font weight |
| `--x-timeline-label-padding`        | `0 0 0.5rem` | Label padding |

## Shadow DOM parts

| Part    | Description |
|---------|-------------|
| `label` | The caption element above the items |

## Accessibility

- The host element always has `role="list"`.
- `aria-label` is set from the `label` attribute when non-empty.
- Each `x-timeline-item` child independently carries `role="listitem"`.

## Coordinator contract

`x-timeline` sets the following `data-*` attributes on each direct `x-timeline-item` child whenever the item connects, disconnects, or the parent's attributes change:

| Attribute          | Value                    | Purpose |
|--------------------|--------------------------|---------|
| `data-index`       | `"0"`, `"1"`, …          | 0-based position in the list |
| `data-last`        | presence (boolean)       | Present on the last item; hides its connector line |
| `data-position`    | `"start"` \| `"end"`     | Overrides the item's own `position` attribute |
| `data-striped`     | presence (boolean)       | Present when `striped` is set on the parent |

## Usage examples

### Basic

```html
<x-timeline>
  <x-timeline-item status="complete" label="Jan 1" title="Project kick-off"></x-timeline-item>
  <x-timeline-item status="complete" label="Feb 3" title="Design review"></x-timeline-item>
  <x-timeline-item status="active"   label="Mar 15" title="Development"></x-timeline-item>
  <x-timeline-item status="pending"  label="Apr 30" title="Launch"></x-timeline-item>
</x-timeline>
```

### With label

```html
<x-timeline label="Project milestones">
  <x-timeline-item status="complete" title="Phase 1"></x-timeline-item>
  <x-timeline-item status="active"   title="Phase 2"></x-timeline-item>
</x-timeline>
```

### Alternating layout

```html
<x-timeline position="alternating">
  <x-timeline-item status="complete" label="Q1" title="Planning"></x-timeline-item>
  <x-timeline-item status="complete" label="Q2" title="Build"></x-timeline-item>
  <x-timeline-item status="active"   label="Q3" title="Test"></x-timeline-item>
  <x-timeline-item status="pending"  label="Q4" title="Launch"></x-timeline-item>
</x-timeline>
```

### Striped

```html
<x-timeline striped>
  <x-timeline-item status="complete" title="Step 1"></x-timeline-item>
  <x-timeline-item status="active"   title="Step 2"></x-timeline-item>
  <x-timeline-item status="pending"  title="Step 3"></x-timeline-item>
</x-timeline>
```

### Handling selection events

```js
document.querySelector('x-timeline').addEventListener('x-timeline-select', e => {
  const { index, status, label } = e.detail;
  console.log(`Selected item ${index}: ${label} (${status})`);
});
```
