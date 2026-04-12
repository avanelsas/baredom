# x-carousel

A themeable, accessible carousel (slideshow) component with swipe/drag navigation, arrow buttons, dot indicators, autoplay, keyboard interaction, and support for slide/fade transitions and horizontal/vertical orientation.

---

## Tag

```html
<x-carousel></x-carousel>
```

---

## Attributes

| Attribute     | Type      | Default        | Description                                              |
|---------------|-----------|----------------|----------------------------------------------------------|
| `autoplay`    | boolean   | `false`        | Enable auto-advance                                      |
| `interval`    | number    | `5000`         | Auto-advance interval in ms (minimum 100)                |
| `loop`        | boolean   | `false`        | Wrap around at ends                                      |
| `arrows`      | boolean*  | `true`         | Show prev/next arrow buttons                             |
| `dots`        | boolean*  | `true`         | Show dot indicators                                      |
| `disabled`    | boolean   | `false`        | Disable all interaction                                  |
| `current`     | number    | `0`            | Zero-based current slide index                           |
| `transition`  | enum      | `"slide"`      | `"slide"` or `"fade"`                                    |
| `direction`   | enum      | `"horizontal"` | `"horizontal"` or `"vertical"`                           |
| `peek`        | CSS length| `"0px"`        | Show edges of adjacent slides (e.g. `"40px"`)            |
| `aria-label`  | string    | `"Carousel"`   | Accessible label for the carousel region                 |

*`arrows` and `dots` use default-true semantics: absent or empty = `true`; `"false"` = `false`.

---

## Properties

| Property       | Type    | Read-only | Reflects attribute |
|----------------|---------|-----------|--------------------|
| `currentSlide` | number  | no        | `current`          |
| `autoplay`     | boolean | no        | `autoplay`         |
| `interval`     | number  | no        | `interval`         |
| `loop`         | boolean | no        | `loop`             |
| `arrows`       | boolean | no        | `arrows`           |
| `dots`         | boolean | no        | `dots`             |
| `disabled`     | boolean | no        | `disabled`         |
| `transition`   | string  | no        | `transition`       |
| `direction`    | string  | no        | `direction`        |
| `peek`         | string  | no        | `peek`             |
| `slideCount`   | number  | **yes**   | -                  |

---

## Events

| Event              | Bubbles | Composed | Cancelable | Detail                              |
|--------------------|---------|----------|------------|-------------------------------------|
| `x-carousel-change`| yes     | yes      | **yes**    | `{ index, previousIndex, reason }`  |

`detail.reason` values: `"arrow"`, `"dot"`, `"drag"`, `"keyboard"`, `"autoplay"`, `"api"`.

Calling `preventDefault()` on the event prevents the slide change.

---

## Methods

| Method          | Description                      |
|-----------------|----------------------------------|
| `next()`        | Navigate to the next slide       |
| `previous()`    | Navigate to the previous slide   |
| `goTo(index)`   | Navigate to a specific slide     |

---

## Slots

| Slot      | Description                                     |
|-----------|-------------------------------------------------|
| (default) | Slide content. Each direct child becomes a slide |

---

## Parts

| Part       | Description                        |
|------------|------------------------------------|
| `viewport` | Scrollable viewport container      |
| `track`    | Flex/grid track holding all slides |
| `prev-btn` | Previous slide arrow button        |
| `next-btn` | Next slide arrow button            |
| `dots`     | Dot indicator container            |
| `dot`      | Individual dot indicator button    |

---

## CSS Custom Properties

| Property                          | Default                                | Description          |
|-----------------------------------|----------------------------------------|----------------------|
| `--x-carousel-height`            | `300px`                                | Viewport height      |
| `--x-carousel-arrow-size`        | `40px`                                 | Arrow button size    |
| `--x-carousel-arrow-bg`          | `var(--x-color-surface, ...)`          | Arrow background     |
| `--x-carousel-arrow-color`       | `var(--x-color-text, ...)`             | Arrow icon color     |
| `--x-carousel-arrow-hover-bg`    | `var(--x-color-surface-hover, ...)`    | Arrow hover bg       |
| `--x-carousel-dot-size`          | `10px`                                 | Dot indicator size   |
| `--x-carousel-dot-color`         | `var(--x-color-border, ...)`           | Inactive dot color   |
| `--x-carousel-dot-active-color`  | `var(--x-color-primary, ...)`          | Active dot color     |
| `--x-carousel-transition-duration`| `var(--x-transition-duration, 300ms)` | Slide/fade duration  |
| `--x-carousel-radius`            | `var(--x-radius-md, 8px)`             | Border radius        |
| `--x-carousel-gap`               | `16px`                                 | Dot gap spacing      |
| `--x-carousel-focus-ring`        | `var(--x-color-focus-ring, #60a5fa)`   | Focus ring color     |
| `--x-carousel-disabled-opacity`  | `0.5`                                  | Opacity when disabled|

---

## Accessibility

- Host has `role="region"` and `aria-roledescription="carousel"`
- Viewport is focusable (`tabindex="0"`)
- Arrow buttons have descriptive `aria-label` values
- Dot indicators use `role="tab"` with `aria-current` and `aria-label`
- Live region announces current slide (set to `aria-live="off"` during autoplay)
- Dots container has `aria-orientation` matching the carousel direction

### Keyboard

| Key            | Horizontal        | Vertical           |
|----------------|-------------------|--------------------|
| `ArrowLeft`    | Previous slide    | -                  |
| `ArrowRight`   | Next slide        | -                  |
| `ArrowUp`      | -                 | Previous slide     |
| `ArrowDown`    | -                 | Next slide         |
| `Home`         | First slide       | First slide        |
| `End`          | Last slide        | Last slide         |

---

## Transition Modes

### Slide (default)

Slides are positioned in a flex row (or column for vertical). Navigation applies `transform: translateX/Y()` on the track element with a CSS transition.

Drag/swipe is supported in slide mode. The carousel detects swipe direction based on distance and velocity.

### Fade

Slides are stacked and cross-fade using `opacity`. No drag support in fade mode.

The `direction` attribute is ignored in fade mode.

---

## Peek Mode

Set `peek="40px"` (or any CSS length) to reveal the edges of adjacent slides, hinting that more content is available.

---

## Autoplay

When `autoplay` is set, the carousel advances automatically at the `interval` rate.

Autoplay pauses on:
- Pointer hover over the carousel
- Keyboard focus inside the carousel
- Active drag interaction

---

## Examples

### Basic carousel

```html
<x-carousel>
  <img src="slide1.jpg" alt="Slide 1">
  <img src="slide2.jpg" alt="Slide 2">
  <img src="slide3.jpg" alt="Slide 3">
</x-carousel>
```

### Autoplay with loop

```html
<x-carousel autoplay interval="3000" loop aria-label="Product showcase">
  <div>Slide 1</div>
  <div>Slide 2</div>
  <div>Slide 3</div>
</x-carousel>
```

### Fade transition

```html
<x-carousel transition="fade">
  <div>Slide 1</div>
  <div>Slide 2</div>
</x-carousel>
```

### Vertical carousel

```html
<x-carousel direction="vertical" style="--x-carousel-height: 400px;">
  <div>Slide 1</div>
  <div>Slide 2</div>
</x-carousel>
```

### Peek mode

```html
<x-carousel peek="40px">
  <div>Slide 1</div>
  <div>Slide 2</div>
  <div>Slide 3</div>
</x-carousel>
```

### Prevent navigation programmatically

```javascript
document.querySelector('x-carousel').addEventListener('x-carousel-change', e => {
  if (e.detail.index === 2) e.preventDefault(); // block slide 2
});
```

### ClojureScript (hiccup)

```clojure
[:x-carousel {:autoplay true :loop true :interval 4000}
 [:div "Slide 1"]
 [:div "Slide 2"]
 [:div "Slide 3"]]
```
