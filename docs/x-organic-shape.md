# x-organic-shape

An organic, blob-like shape container. Use it as a decorative visual accent or as a content wrapper with a fluid, natural boundary.

## Tag

```html
<x-organic-shape></x-organic-shape>
```

## Attributes

| Attribute   | Type   | Default    | Description |
|-------------|--------|------------|-------------|
| `shape`     | string | `"blob-1"` | Preset shape name. One of: `blob-1`, `blob-2`, `blob-3`, `pebble`, `leaf`, `droplet`, `cloud`, `wave`. |
| `path`      | string | —          | Custom CSS `clip-path` value. Overrides `shape` when present. |
| `animation` | string | `"none"`   | Animation style. One of: `none`, `morph`, `pulse`, `float`, `spin`. Respects `prefers-reduced-motion`. `morph` is disabled when `path` is set. |
| `ratio`     | string | `"1/1"`    | CSS `aspect-ratio` value (e.g. `"4/3"`, `"16/9"`, `"auto"`). |
| `width`     | string | —          | CSS width value (e.g. `"200px"`, `"50%"`, `"10rem"`). |
| `height`    | string | —          | CSS height value (e.g. `"150px"`, `"50%"`, `"10rem"`). |

## Properties

| Property    | Type   | Reflects Attribute |
|-------------|--------|--------------------|
| `shape`     | string | `shape`            |
| `path`      | string | `path`             |
| `animation` | string | `animation`        |
| `ratio`     | string | `ratio`            |
| `width`     | string | `width`            |
| `height`    | string | `height`           |

## Events

None.

## Slots

| Slot    | Description |
|---------|-------------|
| default | Content placed inside the shape, clipped to its boundary. |

## CSS Custom Properties

| Property | Default | Description |
|----------|---------|-------------|
| `--x-organic-shape-fill` | `rgba(99,102,241,0.12)` | Background fill. Accepts any CSS color or gradient. |
| `--x-organic-shape-stroke` | `transparent` | Outline/stroke color. |
| `--x-organic-shape-stroke-width` | `0` | Stroke width. |
| `--x-organic-shape-opacity` | `1` | Component opacity. |
| `--x-organic-shape-shadow` | `none` | Drop shadow via `filter: drop-shadow()`. Follows the clip shape. |
| `--x-organic-shape-animate-duration` | `8s` | Animation cycle duration. |
| `--x-organic-shape-animate-timing` | `ease-in-out` | Animation timing function. |
| `--x-organic-shape-animate-direction` | `normal` | Animation direction (`normal`, `reverse`, `alternate`, `alternate-reverse`). |
| `--x-organic-shape-animate-delay` | `0s` | Animation start delay. Useful for staggering multiple shapes. |

## CSS Parts

| Part    | Description |
|---------|-------------|
| `shape` | The inner container that carries the clip-path, background, and aspect-ratio. |

## Accessibility

- **Decorative (no slotted content):** Sets `role="presentation"` and `aria-hidden="true"`.
- **Container (has slotted content):** Removes decorative attributes. Slotted content retains its own semantics.
- Detection is automatic via `slotchange`.

## Preset Shapes

| Name | Description |
|------|-------------|
| `blob-1` | Asymmetric amoeba (default) |
| `blob-2` | Rounder, more symmetrical blob |
| `blob-3` | Elongated organic mass |
| `pebble` | Smooth, slightly irregular oval |
| `leaf` | Pointed ends, curved sides |
| `droplet` | Teardrop shape |
| `cloud` | Bumpy top, flatter bottom |
| `wave` | Undulating horizontal band |

## Animation Styles

| Value | Effect |
|-------|--------|
| `none` | No animation (default) |
| `morph` | Shape morphs between two clip-path states. Only works with presets (disabled when `path` is set). |
| `pulse` | Gentle scale up/down breathing effect. |
| `float` | Slow vertical drift up and down. |
| `spin` | Continuous rotation (uses `linear` timing regardless of `--x-organic-shape-animate-timing`). |

All animations respect `prefers-reduced-motion: reduce`.

## Usage Examples

### Decorative accent

```html
<x-organic-shape
  shape="blob-2"
  width="200px"
  style="--x-organic-shape-fill: rgba(139,92,246,0.2);">
</x-organic-shape>
```

### Morphing blob

```html
<x-organic-shape shape="pebble" animation="morph"></x-organic-shape>
```

### Pulsing shape

```html
<x-organic-shape shape="leaf" animation="pulse" width="160px"></x-organic-shape>
```

### Floating decoration

```html
<x-organic-shape
  shape="cloud"
  animation="float"
  width="200px"
  style="--x-organic-shape-animate-duration: 5s;">
</x-organic-shape>
```

### Custom animation timing

```html
<x-organic-shape
  shape="blob-1"
  animation="morph"
  width="180px"
  style="
    --x-organic-shape-animate-duration: 3s;
    --x-organic-shape-animate-direction: alternate-reverse;
    --x-organic-shape-animate-delay: 1s;
  ">
</x-organic-shape>
```

### Content container

```html
<x-organic-shape
  shape="droplet"
  width="240px"
  style="--x-organic-shape-fill: linear-gradient(135deg, #667eea, #764ba2);">
  <div style="padding: 32px; color: white; text-align: center;">
    Hello World
  </div>
</x-organic-shape>
```

### Custom path

```html
<x-organic-shape path="circle(50%)" width="160px"></x-organic-shape>
```

### With stroke and shadow

```html
<x-organic-shape
  shape="leaf"
  width="180px"
  style="
    --x-organic-shape-fill: rgba(34,197,94,0.12);
    --x-organic-shape-stroke: rgba(34,197,94,0.5);
    --x-organic-shape-stroke-width: 2px;
    --x-organic-shape-shadow: drop-shadow(0 4px 12px rgba(34,197,94,0.3));
  ">
</x-organic-shape>
```

### Background decoration (click-through)

```html
<div style="position: relative;">
  <x-organic-shape
    shape="blob-1"
    animation="float"
    style="
      position: absolute; top: -40px; right: -40px;
      width: 300px; pointer-events: none;
      --x-organic-shape-fill: rgba(99,102,241,0.08);
    ">
  </x-organic-shape>
  <div style="position: relative; z-index: 1;">
    Your content here
  </div>
</div>
```
