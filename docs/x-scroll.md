# x-scroll

A flexible scroll container that supports horizontal and vertical carousel scrolling. Includes navigation controls, dot indicators, scroll-snap, auto-play, loop mode, and full keyboard/accessibility support.

---

## Tag

```html
<x-scroll></x-scroll>
```

---

## Attributes

| Attribute         | Type    | Default        | Description                                                        |
|-------------------|---------|----------------|--------------------------------------------------------------------|
| `mode`            | enum    | `"horizontal"` | Scroll direction: `horizontal` `vertical`                          |
| `snap`            | enum    | `"none"`       | Scroll-snap alignment: `none` `start` `center` `end`              |
| `loop`            | boolean | `false`        | Enable infinite loop scrolling (requires 3+ children)              |
| `auto-play`       | boolean | `false`        | Auto-advance slides on an interval                                 |
| `interval`        | number  | `5000`         | Milliseconds between auto-advance when `auto-play` is active      |
| `show-controls`   | string  | `true`         | Show prev/next navigation buttons. Set `"false"` to hide.         |
| `show-indicators` | boolean | `false`        | Show dot indicators for current slide                              |
| `active-index`    | number  | `0`            | Currently visible slide index (0-based)                            |
| `gap`             | number  | `0`            | Gap between children in pixels                                     |
| `disabled`        | boolean | `false`        | Disables all interaction, controls, and auto-play                  |
| `label`           | string  | `""`           | Accessible label for the scroll region                             |

---

## Properties

| Property         | Type    | Reflects attribute  |
|------------------|---------|---------------------|
| `mode`           | string  | `mode`              |
| `snap`           | string  | `snap`              |
| `loop`           | boolean | `loop`              |
| `autoPlay`       | boolean | `auto-play`         |
| `interval`       | number  | `interval`          |
| `showControls`   | boolean | `show-controls`     |
| `showIndicators` | boolean | `show-indicators`   |
| `activeIndex`    | number  | `active-index`      |
| `gap`            | number  | `gap`               |
| `disabled`       | boolean | `disabled`          |
| `label`          | string  | `label`             |

---

## Methods

| Method       | Arguments        | Description                         |
|--------------|------------------|-------------------------------------|
| `goTo(idx)`  | `idx: number`    | Navigate to a specific slide index  |
| `next()`     | —                | Navigate to the next slide          |
| `prev()`     | —                | Navigate to the previous slide      |

---

## Events

| Event             | Bubbles | Composed | Cancelable | Detail                              |
|-------------------|---------|----------|------------|-------------------------------------|
| `x-scroll-change` | yes     | yes      | no         | `{ activeIndex, previousIndex }`    |
| `x-scroll-start`  | yes     | yes      | no         | `{ direction, activeIndex }`        |
| `x-scroll-end`    | yes     | yes      | no         | `{ activeIndex }`                   |
| `x-scroll-loop`   | yes     | yes      | **yes**    | `{ direction }`                     |

`direction` is `"forward"` or `"backward"`.

Calling `event.preventDefault()` on `x-scroll-loop` prevents the loop from occurring.

---

## Slots

| Slot      | Description                                                |
|-----------|------------------------------------------------------------|
| (default) | Child elements to scroll through. Each direct child is one slide. |

---

## CSS Custom Properties

| Property                              | Default                    | Description                      |
|---------------------------------------|----------------------------|----------------------------------|
| `--x-scroll-gap`                      | `0px`                      | Gap between children             |
| `--x-scroll-padding`                  | `0px`                      | Viewport padding                 |
| `--x-scroll-border-radius`            | `0px`                      | Viewport border radius           |
| `--x-scroll-bg`                       | `transparent`              | Viewport background              |
| `--x-scroll-slide-size`               | `100%`                     | Slide flex-basis (width or height) |
| `--x-scroll-control-size`             | `36px`                     | Prev/next button size            |
| `--x-scroll-control-bg`              | `rgba(255,255,255,0.9)`    | Button background                |
| `--x-scroll-control-color`           | `rgba(0,0,0,0.7)`         | Button icon color                |
| `--x-scroll-control-hover-bg`        | `rgba(255,255,255,1)`      | Button hover background          |
| `--x-scroll-control-border-radius`   | `50%`                      | Button border radius             |
| `--x-scroll-control-shadow`          | `0 2px 8px rgba(0,0,0,.15)` | Button box shadow              |
| `--x-scroll-indicator-size`          | `8px`                      | Dot indicator diameter           |
| `--x-scroll-indicator-gap`           | `6px`                      | Gap between indicators           |
| `--x-scroll-indicator-color`         | `rgba(0,0,0,0.25)`        | Inactive dot color               |
| `--x-scroll-indicator-active-color`  | `rgba(0,0,0,0.7)`         | Active dot color                 |
| `--x-scroll-transition-duration`     | `300ms`                    | Scroll transition duration       |
| `--x-scroll-disabled-opacity`        | `0.55`                     | Opacity when disabled            |
| `--x-scroll-focus-ring`              | `rgba(0,102,204,0.6)`     | Focus ring color                 |

All color properties have automatic dark-mode overrides.

---

## Accessibility

- Container uses `role="region"` with `aria-roledescription="carousel"` and `aria-label`
- Prev/next buttons have appropriate `aria-label` and `disabled` states
- Indicators use `role="tablist"` with `role="tab"` and `aria-selected` on each dot
- An `aria-live="polite"` region announces slide changes
- **Keyboard**: Arrow keys navigate slides (Left/Right for horizontal, Up/Down for vertical), Home/End jump to first/last
- `prefers-reduced-motion: reduce` disables smooth scrolling transitions

---

## Usage

### Horizontal carousel with snap

```html
<x-scroll snap="center" show-indicators gap="16" label="Featured products">
  <div>Slide 1</div>
  <div>Slide 2</div>
  <div>Slide 3</div>
</x-scroll>
```

### Vertical scrolling

```html
<x-scroll mode="vertical" style="height: 400px">
  <div>Section 1</div>
  <div>Section 2</div>
  <div>Section 3</div>
</x-scroll>
```

### Auto-play carousel with loop

```html
<x-scroll loop auto-play interval="3000" show-indicators label="Testimonials">
  <div>Quote 1</div>
  <div>Quote 2</div>
  <div>Quote 3</div>
</x-scroll>
```

### Listening for events

```javascript
const scroller = document.querySelector('x-scroll');
scroller.addEventListener('x-scroll-change', (e) => {
  console.log(`Changed to slide ${e.detail.activeIndex} from ${e.detail.previousIndex}`);
});
```
