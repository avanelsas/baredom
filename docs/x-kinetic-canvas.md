# x-kinetic-canvas

Animated canvas background component with slotted content. Renders particle/character animations behind any content placed inside the element.

## Tag

```html
<x-kinetic-canvas type="starfield">
  <h1>Content renders on top</h1>
</x-kinetic-canvas>
```

## Observed Attributes

| Attribute    | Type    | Default      | Description |
|-------------|---------|-------------|-------------|
| `type`       | string  | `starfield`  | Animation type: `starfield`, `bubbles`, `matrix` |
| `variant`    | string  | `motion`     | Type-specific variant. Starfield: `motion`, `twinkle` |
| `speed`      | string  | `medium`     | Animation speed: `slow`, `medium`, `fast`, or numeric multiplier |
| `density`    | string  | `medium`     | Entity density: `low`, `medium`, `high`, or numeric multiplier |
| `fullscreen` | boolean | `false`      | When present, positions fixed to fill the viewport |
| `paused`     | boolean | `false`      | When present, pauses the animation loop |

## Properties

| Property     | Type    | Reflects Attribute |
|-------------|---------|-------------------|
| `type`       | string  | `type` |
| `variant`    | string  | `variant` |
| `speed`      | string  | `speed` |
| `density`    | string  | `density` |
| `fullscreen` | boolean | `fullscreen` |
| `paused`     | boolean | `paused` |

## Events

None.

## Methods

None.

## Slots

| Slot    | Description |
|---------|-------------|
| default | Content rendered on top of the animated background |

## CSS Custom Properties

| Property | Default (light) | Default (dark) | Description |
|----------|----------------|---------------|-------------|
| `--x-kinetic-canvas-bg` | `var(--x-color-bg, #f0f0f5)` | `var(--x-color-bg, #0a0a1a)` | Canvas background fill |
| `--x-kinetic-canvas-color-1` | `var(--x-color-text-muted, #888)` | `var(--x-color-text, #fff)` | Primary entity colour |
| `--x-kinetic-canvas-color-2` | `var(--x-color-primary, #3b82f6)` | `var(--x-color-primary, #60a5fa)` | Secondary entity colour |
| `--x-kinetic-canvas-color-3` | `var(--x-color-secondary, #a855f7)` | `var(--x-color-secondary, #c084fc)` | Tertiary entity colour |
| `--x-kinetic-canvas-matrix-font-size` | `14` | `14` | Font size (px) for matrix rain characters |
| `--x-kinetic-canvas-z` | `-1` | `-1` | `z-index` for fullscreen mode (behind content by default) |

Colours integrate with `x-theme` tokens and adapt to light/dark colour scheme automatically.

## Animation Types

### starfield

Stars at varying depths with two variants:
- **motion** — stars drift outward from center with parallax depth
- **twinkle** — static star positions with pulsing brightness

### bubbles

Soft circles floating upward with horizontal drift. Wrap around at screen edges.

### matrix

Falling columns of characters (Katakana + Latin) with brightness falloff from head to tail.

## Accessibility

- Canvas element has `aria-hidden="true"`
- Respects `prefers-reduced-motion: reduce` — renders a single static frame, no animation loop
- Animation pauses when the element is scrolled offscreen (IntersectionObserver)

## Performance

- Uses `requestAnimationFrame` for smooth 60fps animation
- Pauses automatically when offscreen via IntersectionObserver
- Canvas sized with `devicePixelRatio` for sharp rendering on HiDPI displays
- Entities stored as mutable JS arrays for minimal GC pressure

## Usage Examples

```html
<!-- Page background -->
<x-kinetic-canvas type="starfield" fullscreen></x-kinetic-canvas>

<!-- Section background -->
<x-kinetic-canvas type="bubbles" speed="slow" density="low" style="height: 400px;">
  <div style="padding: 2rem; color: white;">
    <h2>Hero section</h2>
    <p>Content on top of animated bubbles</p>
  </div>
</x-kinetic-canvas>

<!-- Custom colours -->
<x-kinetic-canvas type="matrix"
  style="--x-kinetic-canvas-bg: #000;
         --x-kinetic-canvas-color-1: #0f0;
         --x-kinetic-canvas-color-2: #0a0;
         --x-kinetic-canvas-color-3: #050;">
</x-kinetic-canvas>
```
