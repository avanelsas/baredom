# x-ripple-effect

A container component that applies a water-like distortion ripple to its content on mouse click. Uses SVG `feTurbulence` + `feDisplacementMap` filters animated via `requestAnimationFrame`.

## Tag

```html
<x-ripple-effect>
  <div>Click me for a water ripple</div>
</x-ripple-effect>
```

## Attributes

| Attribute   | Type    | Default | Description                                      |
|-------------|---------|---------|--------------------------------------------------|
| `intensity` | number  | `25`    | Maximum displacement scale (1–100)               |
| `duration`  | number  | `800`   | Animation duration in milliseconds (100–5000)    |
| `frequency` | number  | `0.04`  | Turbulence base frequency (0.005–0.2)            |
| `disabled`  | boolean | —       | When present, suppresses the visual ripple effect. Content remains interactive. |

## Properties

| Property    | Type    | Reflects attribute |
|-------------|---------|-------------------|
| `intensity` | number  | `intensity`       |
| `duration`  | number  | `duration`        |
| `frequency` | number  | `frequency`       |
| `disabled`  | boolean | `disabled`        |

## Events

| Event                    | Bubbles | Composed | Cancelable | Detail             |
|--------------------------|---------|----------|------------|--------------------|
| `x-ripple-effect-start`  | yes     | yes      | no         | `{ x, y }`        |
| `x-ripple-effect-end`    | yes     | yes      | no         | `{ x, y }`        |

- `x` and `y` are the `clientX`/`clientY` coordinates of the pointer event that triggered the ripple.

## Slots

| Slot      | Description                         |
|-----------|-------------------------------------|
| (default) | Content to wrap with the ripple effect |

## Parts

| Part        | Description                                |
|-------------|--------------------------------------------|
| `container` | Wrapper div around slotted content         |
| `filters`   | Hidden SVG element holding active filters  |

## How it works

1. On `pointerdown` (primary button only), the component creates an SVG `<filter>` with `feTurbulence` and `feDisplacementMap` inside the shadow DOM.
2. The filter is applied to the host element via `style.filter`.
3. A `requestAnimationFrame` loop animates the displacement scale from the configured intensity down to 0, and the turbulence frequency from high to low (creating the illusion of expanding waves).
4. When the animation completes, the filter element is removed from the DOM.
5. Multiple rapid clicks create overlapping ripples, each with its own filter instance.

## Tuning the effect

- **`intensity`** controls how strongly the content is distorted. Lower values (5–15) give a subtle shimmer; higher values (50–100) create a dramatic warp.
- **`frequency`** controls the size of the wave pattern. Lower values (0.005–0.02) create large, gentle waves; higher values (0.1–0.2) create small, choppy waves.
- **`duration`** controls how long the ripple lasts. Short durations (200–400ms) feel snappy; long durations (1500–3000ms) create a slow, dreamy effect.

## Browser support

Requires browsers that support SVG `feTurbulence` and `feDisplacementMap` filter primitives, `requestAnimationFrame`, and Custom Elements v1. All modern browsers meet these requirements.

## Accessibility

- The component is purely decorative — it does not affect content structure or semantics.
- The `disabled` attribute suppresses the visual ripple effect but does **not** block pointer events — wrapped content remains fully interactive.
- **Reduced motion:** When `prefers-reduced-motion: reduce` is active, the component shows a single-frame flash at half intensity instead of the full animation.

## Examples

### Basic usage

```html
<x-ripple-effect>
  <button>Click me</button>
</x-ripple-effect>
```

### Custom intensity and duration

```html
<x-ripple-effect intensity="60" duration="1200">
  <img src="photo.jpg" alt="A photo" />
</x-ripple-effect>
```

### Disabled

```html
<x-ripple-effect disabled>
  <div>No ripple here</div>
</x-ripple-effect>
```

### Listening for events

```html
<x-ripple-effect id="demo">
  <div>Content</div>
</x-ripple-effect>
<script>
  document.getElementById("demo").addEventListener("x-ripple-effect-start", e => {
    console.log("Ripple started at", e.detail.x, e.detail.y);
  });
</script>
```
