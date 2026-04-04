# x-scroll-parallax

A parallax scrolling container where slotted children move at different speeds relative to page scroll, creating a depth effect. Each child declares its own parallax factor via `data-speed`.

---

## Tag

```html
<x-scroll-parallax></x-scroll-parallax>
```

---

## Attributes

| Attribute   | Type    | Default      | Description                                              |
|-------------|---------|--------------|----------------------------------------------------------|
| `direction` | enum    | `"vertical"` | Scroll axis: `vertical` `horizontal`                     |
| `source`    | enum    | `"document"` | Scroll source: `document` (page scroll)                  |
| `easing`    | enum    | `"none"`     | Smoothing: `none` (direct) `smooth` (CSS transition)     |
| `disabled`  | boolean | `false`      | Freeze all parallax transforms                           |
| `label`     | string  | `""`         | Accessible label for the parallax region                 |

---

## Data Attributes (on children)

| Attribute      | Type   | Default | Description                                                        |
|----------------|--------|---------|--------------------------------------------------------------------|
| `data-speed`   | number | `1`     | Speed factor: `0`=fixed, `1`=normal, `<1`=slower, `>1`=faster, negative=reverse |
| `data-offset`  | number | `0`     | Initial pixel offset added to parallax transform                   |
| `data-opacity` | string | `""`    | Set to `"fade"` for viewport enter/leave opacity fade              |
| `data-scale`   | string | `""`    | Set to `"grow"` for 0.85→1 scale effect                           |

---

## Properties

| Property    | Type    | Reflects attribute | Notes     |
|-------------|---------|-------------------|-----------|
| `direction` | string  | `direction`       |           |
| `source`    | string  | `source`          |           |
| `easing`    | string  | `easing`          |           |
| `disabled`  | boolean | `disabled`        |           |
| `label`     | string  | `label`           |           |
| `progress`  | number  | —                 | Read-only. Current scroll progress `[0, 1]`. |

---

## Events

| Event                         | Bubbles | Composed | Detail           | Description                                   |
|-------------------------------|---------|----------|------------------|-----------------------------------------------|
| `x-scroll-parallax-enter`    | yes     | yes      | `{ progress }`   | Component enters the viewport                 |
| `x-scroll-parallax-leave`    | yes     | yes      | `{ progress }`   | Component leaves the viewport                 |
| `x-scroll-parallax-progress` | yes     | yes      | `{ progress }`   | Fires each frame while visible (rAF cadence)  |

---

## Slots

| Slot      | Description                                                                 |
|-----------|-----------------------------------------------------------------------------|
| (default) | Child elements to parallax. Each can have `data-speed`, `data-offset`, `data-opacity`, `data-scale`. |

---

## CSS Custom Properties

| Property                                 | Default | Description                                      |
|------------------------------------------|---------|--------------------------------------------------|
| `--x-scroll-parallax-perspective`        | `none`  | CSS perspective on viewport (e.g. `1000px`)      |
| `--x-scroll-parallax-overflow`           | `hidden`| Viewport overflow (`visible` to allow overflow)  |
| `--x-scroll-parallax-smooth-duration`    | `80ms`  | Transition duration when `easing="smooth"`       |
| `--x-scroll-parallax-fade-range`         | `20%`   | Viewport fraction for opacity fade transition    |
| `--x-scroll-parallax-scale-min`          | `0.85`  | Minimum scale for `data-scale="grow"`            |
| `--x-scroll-parallax-disabled-opacity`   | `0.55`  | Opacity when `disabled`                          |

### Customising via CSS custom properties

```html
<x-scroll-parallax
  style="--x-scroll-parallax-perspective: 1000px;
         --x-scroll-parallax-fade-range: 30%;
         --x-scroll-parallax-scale-min: 0.9;">
  <div data-speed="0.5" data-opacity="fade" data-scale="grow">Content</div>
</x-scroll-parallax>
```

---

## Accessibility

- Viewport has `role="region"` with `aria-label` from the `label` attribute.
- ARIA live region announces enter/leave for screen readers.
- **Reduced motion**: When `prefers-reduced-motion: reduce` is active, all transforms, fades, and scales are disabled via CSS. The scroll handler is suppressed, so `progress` events do not fire. `enter` and `leave` events (from IntersectionObserver) still fire.
- The component is not focusable (no keyboard interaction needed).

---

## Usage

### Basic parallax layers

```html
<x-scroll-parallax label="Hero">
  <div data-speed="0.3" class="background">Background</div>
  <div data-speed="0.7" class="midground">Midground</div>
  <div data-speed="1.2" class="foreground">Foreground</div>
</x-scroll-parallax>
```

### With fade and scale effects

```html
<x-scroll-parallax easing="smooth">
  <img data-speed="0.5" data-opacity="fade" src="bg.jpg" />
  <h1 data-speed="0.8" data-scale="grow">Welcome</h1>
</x-scroll-parallax>
```

### Horizontal parallax

```html
<x-scroll-parallax direction="horizontal">
  <div data-speed="0.5">Slow layer</div>
  <div data-speed="1.5">Fast layer</div>
</x-scroll-parallax>
```

---

## How it works

1. An **IntersectionObserver** activates the component when it enters the viewport. Off-screen components have zero cost.
2. A **scroll listener** on `window` triggers a single **requestAnimationFrame** per frame.
3. Each frame, the component reads its position via `getBoundingClientRect()`, computes a progress value `[0, 1]`, and applies CSS transforms to each slotted child based on their `data-speed` factor.
4. The transform formula centers the parallax so children are at their natural position when the component is centered in the viewport:
   ```
   offset = (progress - 0.5) * viewport_size * (speed - 1) + data-offset
   ```
