# x-split-pane

A resizable two-panel layout. A draggable divider re-proportions a `start` and an `end` panel along a horizontal or vertical axis. The divider position is a percentage held in the `position` attribute — the single source of truth — so layouts are fully serialisable and the component stays stateless.

For three or more panels, nest an `x-split-pane` inside a slot.

---

## Tag

```html
<x-split-pane>
  <div slot="start">Left panel</div>
  <div slot="end">Right panel</div>
</x-split-pane>
```

---

## Attributes

| Attribute       | Type    | Default          | Description                                                                       |
|-----------------|---------|------------------|-----------------------------------------------------------------------------------|
| `orientation`   | enum    | `"horizontal"`   | `horizontal` — panels side by side; `vertical` — panels stacked                   |
| `position`      | number  | `50`             | Size of the start panel as a percentage `0`–100. Written back as the divider moves |
| `min-start`     | number  | `0`              | Minimum size of the start panel, in pixels                                        |
| `min-end`       | number  | `0`              | Minimum size of the end panel, in pixels                                          |
| `disabled`      | boolean | absent           | Disables divider dragging and keyboard resizing                                   |
| `divider-label` | string  | `"Resize panels"`| Accessible label for the divider separator                                        |

---

## Properties

| Property       | Type    | Reflects attribute | Default          |
|----------------|---------|--------------------|------------------|
| `orientation`  | string  | `orientation`      | `"horizontal"`   |
| `position`     | number  | `position`         | `50`             |
| `minStart`     | number  | `min-start`        | `0`              |
| `minEnd`       | number  | `min-end`          | `0`              |
| `disabled`     | boolean | `disabled`         | `false`          |
| `dividerLabel` | string  | `divider-label`    | `"Resize panels"`|

---

## Events

| Event                   | Cancelable | Detail                                  | Fires                                       |
|-------------------------|------------|-----------------------------------------|---------------------------------------------|
| `x-split-pane-resize`     | no         | `{ position: number, orientation: string }` | On every drag move and keyboard step    |
| `x-split-pane-resize-end` | no         | `{ position: number, orientation: string }` | On pointer release and keyboard commit  |

Both events bubble and are composed.

---

## Methods

None. Set the divider position through the `position` property.

---

## Slots

| Slot    | Description                          |
|---------|--------------------------------------|
| `start` | Content of the first (start) panel   |
| `end`   | Content of the second (end) panel    |

There is no default slot — content must declare `slot="start"` or `slot="end"`.

---

## Parts

| Part           | Description                                          |
|----------------|------------------------------------------------------|
| `start-pane`   | Wrapper around the `start` slot                      |
| `divider`      | The draggable separator (`role="separator"`)         |
| `divider-line` | The thin visible line centred in the divider hit area |
| `end-pane`     | Wrapper around the `end` slot                        |

---

## CSS Custom Properties

| Variable                              | Default                          | Description                          |
|----------------------------------------|----------------------------------|--------------------------------------|
| `--x-split-pane-divider-size`          | `0.5rem` (`1rem` on coarse pointers) | Thickness of the divider hit area |
| `--x-split-pane-divider-line-size`     | `1px`                            | Thickness of the visible divider line |
| `--x-split-pane-divider-color`         | `var(--x-color-border)`          | Divider line colour                  |
| `--x-split-pane-divider-hover-color`   | `var(--x-color-primary)`         | Divider line colour on hover / drag  |
| `--x-split-pane-divider-focus-color`   | `var(--x-color-focus-ring)`      | Divider line colour and ring on keyboard focus |

Dark-mode defaults are applied automatically via `@media (prefers-color-scheme: dark)`.

---

## Sizing

`x-split-pane` is a flex container with no intrinsic size. **Give the host a definite size along the split axis** — for a `vertical` split (or any split with percentage panels) the host needs an explicit height, otherwise the percentage `position` has nothing to resolve against:

```html
<x-split-pane style="height: 320px;"> … </x-split-pane>
```

Each panel sets `min-width: 0` / `min-height: 0` and `overflow: auto`, so panel content scrolls rather than forcing the layout wider.

---

## Accessibility

The divider implements the WAI-ARIA **Window Splitter** pattern:

- `role="separator"`, focusable via `tabindex="0"` (removed when `disabled`).
- `aria-orientation` describes the divider bar — `vertical` for a horizontal split, `horizontal` for a vertical split.
- `aria-valuenow` / `aria-valuemin` / `aria-valuemax` expose the position as `0`–100.
- `aria-label` comes from `divider-label`; `aria-controls` references the start panel.

### Keyboard

| Key                                   | Action                              |
|----------------------------------------|-------------------------------------|
| `Arrow Right` / `Arrow Left`           | Move ±1% (horizontal split)         |
| `Arrow Down` / `Arrow Up`              | Move ±1% (vertical split)           |
| `Page Up` / `Page Down`                | Move ±10%                           |
| `Home` / `End`                         | Jump to the minimum / maximum       |

---

## DOM Behaviour

- The `position` attribute is the single source of truth. A drag or keyboard step writes it; the render pipeline reads it back and applies `flex-basis`. Setting `position` programmatically updates the layout identically.
- `min-start` / `min-end` are enforced both by the drag clamp and by CSS `min-width` / `min-height` on the panels, so a panel never collapses below its minimum even when the container is resized.
- Pointer events (`pointerdown` / `pointermove` / `pointerup`) drive dragging, with `touch-action: none` on the divider so touch drags work without scrolling the page.

---

## Examples

### Vertical split

```html
<x-split-pane orientation="vertical" position="40" style="height: 360px;">
  <div slot="start">Top panel</div>
  <div slot="end">Bottom panel</div>
</x-split-pane>
```

### Minimum panel sizes

```html
<x-split-pane position="25" min-start="180" min-end="320" style="height: 320px;">
  <nav slot="start">Sidebar</nav>
  <main slot="end">Content</main>
</x-split-pane>
```

### Three panels via nesting

```html
<x-split-pane position="25" style="height: 320px;">
  <nav slot="start">Sidebar</nav>
  <x-split-pane slot="end" position="70">
    <main slot="start">Editor</main>
    <aside slot="end">Inspector</aside>
  </x-split-pane>
</x-split-pane>
```
