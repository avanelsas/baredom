# `<x-scroll-stack>`

A scroll-driven card stacking component. Child elements are displayed vertically; as the user scrolls through the component, they animate upward and stack on top of each other like a card deck. Each stacked card is slightly rotated and offset for a "messy pile" look. Scrolling back reverses the animation smoothly.

## How it works

The component makes itself tall enough to create scroll room (`viewport height + children count x scroll-distance`). A sticky inner container keeps the cards visible while the user scrolls. Scroll progress drives per-card transforms (translate + rotate) applied directly to slotted children.

## Tag name

```
x-scroll-stack
```

## Attributes

| Attribute | Type | Default | Description |
| --- | --- | --- | --- |
| `peek` | number | `6` | Pixels of each stacked card that remain visible |
| `rotation` | number | `3` | Maximum rotation in degrees for stacked cards |
| `scroll-distance` | number | `150` | Pixels of scroll needed to stack one card |
| `align` | string | `"center"` | Vertical position of the stack: `"top"`, `"center"`, `"bottom"` |
| `disabled` | boolean | `false` | When present, disables scroll-driven animation |

## Properties

| Property | Type | Read-only | Description |
| --- | --- | --- | --- |
| `peek` | number | No | Reflects `peek` attribute |
| `rotation` | number | No | Reflects `rotation` attribute |
| `scrollDistance` | number | No | Reflects `scroll-distance` attribute |
| `align` | string | No | Reflects `align` attribute |
| `disabled` | boolean | No | Reflects `disabled` attribute |
| `stackedCount` | number | Yes | Number of fully stacked cards |
| `progress` | number | Yes | Overall stacking progress (0.0 to 1.0) |

## Methods

| Method | Description |
| --- | --- |
| `refresh()` | Re-caches child dimensions and recalculates layout. Call after dynamically changing child sizes. |

## Events

| Event | Detail | Description |
| --- | --- | --- |
| `x-scroll-stack-change` | `{ stackedCount, totalCount, progress }` | Fired when the number of stacked cards changes |
| `x-scroll-stack-progress` | `{ progress, stackedCount, totalCount }` | Fired on scroll with current progress |

## Slots

| Slot | Description |
| --- | --- |
| (default) | Child elements to be stacked. Any element works; cards, images, divs, etc. |

## CSS Custom Properties

| Property | Default | Description |
| --- | --- | --- |
| `--x-scroll-stack-peek` | `6px` | Peek offset between stacked cards |
| `--x-scroll-stack-rotation` | `3deg` | Maximum rotation |
| `--x-scroll-stack-gap` | `1rem` | Gap between cards in their unstacked state |
| `--x-scroll-stack-padding-top` | `2rem` | Top padding inside the sticky container |

## CSS Parts

| Part | Description |
| --- | --- |
| `container` | The sticky inner container |

## Accessibility

- The container has `role="region"`.
- When `prefers-reduced-motion: reduce` is active, all transitions are disabled. Cards still stack/unstack based on scroll position, but without animated transitions.

## Usage

```html
<x-scroll-stack peek="8" rotation="4" scroll-distance="200" align="center">
  <div class="card">Card 1</div>
  <div class="card">Card 2</div>
  <div class="card">Card 3</div>
  <div class="card">Card 4</div>
</x-scroll-stack>
```

### Listening to events

```js
const stack = document.querySelector('x-scroll-stack');
stack.addEventListener('x-scroll-stack-change', (e) => {
  console.log('Stacked:', e.detail.stackedCount, '/', e.detail.totalCount);
});
```

### Dynamic refresh

```js
// After changing child sizes
stack.refresh();
```
