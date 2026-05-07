# x-proximity-list

A horizontal or vertical list of slotted items that scale up as the pointer approaches — the dock-effect, applied to any list. Sibling of `x-liquid-dock` without the gooey SVG filter: lighter, more general, and doesn't anchor itself to a screen edge.

**Tag name:** `x-proximity-list`

## Usage

```html
<x-proximity-list radius="140" max-scale="1.6" lift="14">
  <button aria-label="Home">&#x1F3E0;</button>
  <button aria-label="Search">&#x1F50D;</button>
  <button aria-label="Messages">&#x1F4AC;</button>
  <button aria-label="Profile">&#x1F464;</button>
</x-proximity-list>
```

The component places its slotted children in a flex track, listens for pointer movement over the track, and writes per-item `transform: translate*( ) scale( )` plus three CSS custom properties to each child. Items return to their base size via a CSS transition when the pointer leaves.

## Attributes

| Attribute    | Type     | Default      | Range            | Description                                                                          |
|--------------|----------|--------------|------------------|--------------------------------------------------------------------------------------|
| `direction`  | string   | `horizontal` | `horizontal` `vertical` | Layout axis. Drives flex direction and which arrow keys move focus.            |
| `radius`     | number   | `120`        | 20 – 600 (px)    | Influence radius. Items beyond this distance from the pointer stay at base size.    |
| `max-scale`  | number   | `1.5`        | 1.0 – 3.0        | Scale factor for the item directly under the pointer.                               |
| `lift`       | number   | `0`          | 0 – 60 (px)      | Maximum cross-axis translation toward the pointer. `0` means scale-only.            |
| `gap`        | number   | `8`          | 0 – 64 (px)      | Gap between items (also exposed as `--x-proximity-list-gap`).                       |
| `disabled`   | boolean  | absent       | presence         | Disables proximity tracking and select events. Items render at base size.           |

## Properties

| Property    | Type    | Reflects attribute |
|-------------|---------|--------------------|
| `direction` | string  | `direction`        |
| `radius`    | number  | `radius`           |
| `maxScale`  | number  | `max-scale`        |
| `lift`      | number  | `lift`             |
| `gap`       | number  | `gap`              |
| `disabled`  | boolean | `disabled`         |

No methods.

## Events

| Event                      | Bubbles | Composed | Cancelable | Detail                                                       |
|----------------------------|---------|----------|------------|--------------------------------------------------------------|
| `x-proximity-list-select`  | yes     | yes      | yes        | `{ index: number, item: Element, source: "pointer" \| "keyboard" }` |

Dispatched on click and on `Enter` / `Space` once an item is focused via arrow keys. Same shape as `x-liquid-dock-select`.

## Slots

| Slot             | Description                                                                |
|------------------|----------------------------------------------------------------------------|
| _(default slot)_ | Items are user-supplied light-DOM children. Each direct child is reactive. |

## Parts

| Part    | Description                              |
|---------|------------------------------------------|
| `track` | Flex container that holds the slot.      |

## Theming — CSS Custom Properties

| Property                          | Default   | Description                          |
|-----------------------------------|-----------|--------------------------------------|
| `--x-proximity-list-gap`          | `8px`     | Gap between items.                   |
| `--x-proximity-list-item-size`    | `48px`    | Default `width`/`height` for items.  |

## Per-item runtime variables

Each slotted child receives three inline-style variables on every animation frame while the pointer is over the track. They're useful if you want to drive richer per-item visuals (shadow, glow, label opacity) than the default scale + translate.

| Property                      | Range          | Description                                     |
|-------------------------------|----------------|-------------------------------------------------|
| `--x-proximity-influence`     | `0` … `1`      | Linear distance falloff from the pointer.       |
| `--x-proximity-scale`         | `1` … `max-scale` | Current scale factor applied to the item.   |
| `--x-proximity-lift`          | `-lift` … `+lift` (px) | Current cross-axis translation.        |

Example — style your item to glow proportionally to influence:

```css
::slotted(*) {
  box-shadow: 0 0 calc(var(--x-proximity-influence, 0) * 24px)
              rgba(99, 102, 241, calc(var(--x-proximity-influence, 0) * 0.6));
}
```

## Accessibility

- The host element gets `role="list"` automatically if no `role` was set.
- Slotted children that don't already have `tabindex` receive `tabindex="0"` on `slotchange`.
- Arrow keys move focus among items (`Left`/`Right` for horizontal, `Up`/`Down` for vertical).
- `Enter` or `Space` on a focused item dispatches `x-proximity-list-select` with `source: "keyboard"`.
- The proximity effect is purely visual — focus, click, and keyboard activation are never gated by it.

## Mobile and reduced motion

- **Touch devices**: proximity tracking is suppressed (only `pointerType === "mouse"` drives the effect). Click and keyboard select still work, so the list stays fully functional without the visual dock effect.
- **`prefers-reduced-motion: reduce`**: the RAF loop is skipped, items stay at base size, and CSS transitions are disabled. Click and keyboard select still work.

## How it works

The pipeline is stateless — `render! = f(attributes, pointer, displayed)`:

1. `pointermove` over the track caches `(x, y)` in track-relative coordinates and schedules a `requestAnimationFrame` (only one in flight at a time).
2. On each frame, every item's centre is compared to the pointer using a linear distance falloff (`compute-influence`); the influence drives a quadratic ramp on scale (`compute-scale`) and an optional cross-axis lift (`compute-lift`).
3. Each item carries a small "displayed" record (`scale`, `lift`) that lerps toward the freshly computed targets. This keeps motion smooth even when the closest item's target changes by large per-pixel increments — which is where pure CSS transitions, when reset by user CSS, make the closest item appear to "snap" while peripheral items look smooth.
4. Three CSS variables (`--x-proximity-influence`, `--x-proximity-scale`, `--x-proximity-lift`) and the `transform` are written to each item's inline style.
5. On `pointerleave`, the targets become the resting values and the lerp settles items back to base. Once every item has settled, RAF stops and inline styles are cleared so the user's CSS takes over again cleanly.
6. A `ResizeObserver` re-caches per-item rects when the host or items resize.

No mutable application state, no signals, no virtual DOM — just `f(attributes, pointer, displayed) → inline styles`. The component is robust against author CSS that resets the `transition` shorthand on slotted items, because all smoothing happens in JS.

## Examples

### Horizontal default

```html
<x-proximity-list>
  <button>A</button>
  <button>B</button>
  <button>C</button>
  <button>D</button>
</x-proximity-list>
```

### Vertical sidebar with lift

```html
<x-proximity-list direction="vertical" lift="20" max-scale="1.8">
  <a href="#home">Home</a>
  <a href="#search">Search</a>
  <a href="#profile">Profile</a>
</x-proximity-list>
```

### Wider influence, subtler scale

```html
<x-proximity-list radius="240" max-scale="1.2">
  <img src="..." />
  <img src="..." />
  <img src="..." />
</x-proximity-list>
```

### Listening for selection

```js
const list = document.querySelector('x-proximity-list');
list.addEventListener('x-proximity-list-select', (e) => {
  console.log('selected', e.detail.index, e.detail.source);
});
```
