# `<x-neural-glow>`

A WebGL-powered bioluminescent neural network background. Renders soft glowing orbs connected by faint pulsing lines that respond to user activity. When idle, pulses decay to a resting "heartbeat" rhythm. Deep-sea aesthetic with midnight backgrounds and electric accent colors.

## Tag

```html
<x-neural-glow></x-neural-glow>
```

## Attributes

| Attribute | Type | Default | Range | Description |
|---|---|---|---|---|
| `orb-count` | integer | `15` | 3–50 | Number of glowing orbs |
| `color-primary` | string | `#4f8bff` | Any CSS color | Primary glow color |
| `color-secondary` | string | `#00e5cc` | Any CSS color | Secondary/accent glow color |
| `color-background` | string | `#050a18` | Any CSS color | Canvas background fill |
| `pulse-speed` | number | `1.0` | 0.1–5.0 | Base pulse speed multiplier |
| `rest-rate` | number | `4.0` | 1.0–10.0 | Resting heartbeat period in seconds |
| `connection-distance` | number | `0.15` | 0.05–0.5 | Max distance for connecting lines (fraction of viewport diagonal) |
| `orb-size` | number | `40` | 10–200 | Base orb radius in CSS pixels |
| `opacity` | number | `0.8` | 0.0–1.0 | Overall canvas opacity |
| `interactive` | boolean | `true` | — | Whether to respond to user activity (absent = true, `"false"` = false) |

## Properties

| Property | Type | Reflects |
|---|---|---|
| `orbCount` | `number` | `orb-count` |
| `colorPrimary` | `string` | `color-primary` |
| `colorSecondary` | `string` | `color-secondary` |
| `colorBackground` | `string` | `color-background` |
| `pulseSpeed` | `number` | `pulse-speed` |
| `restRate` | `number` | `rest-rate` |
| `connectionDistance` | `number` | `connection-distance` |
| `orbSize` | `number` | `orb-size` |
| `opacity` | `number` | `opacity` |
| `interactive` | `boolean` | `interactive` |

## Events

None. This is a purely decorative component.

## Slots

None.

## Parts

| Part | Element | Description |
|---|---|---|
| `canvas` | `<canvas>` | The WebGL rendering surface |

## CSS Custom Properties

| Property | Default | Description |
|---|---|---|
| `--x-neural-glow-z-index` | `0` | Stacking order |
| `--x-neural-glow-opacity` | `0.8` | Override canvas opacity |
| `--x-neural-glow-blend-mode` | `normal` | CSS `mix-blend-mode` |
| `--x-neural-glow-inset` | `auto` | Inset for absolute positioning |

## Activity Response

When `interactive` is true (default), the component tracks:

- **Scroll speed** — faster scrolling increases glow intensity
- **Pointer movement** — mouse/touch velocity drives activity
- **Keyboard input** — each keypress adds a pulse impulse

Activity decays exponentially when no input is detected, settling to the resting heartbeat rate defined by `rest-rate`.

## Accessibility

- `aria-hidden="true"` — purely decorative, hidden from assistive technology
- `role="presentation"` — no semantic meaning
- Respects `prefers-reduced-motion: reduce` — freezes animation to a static glow pattern

## WebGL

Renders via WebGL 1 with a single fragment shader. If WebGL is unavailable, the component renders an empty element (graceful degradation). The shader computes:

1. Soft glow per orb using distance field + exponential falloff
2. Connection lines between nearby orbs using distance-to-segment
3. Pulse modulation driven by the activity level uniform
4. Sum-of-sines pseudo-noise for organic per-orb drift

## Examples

### Basic usage

```html
<x-neural-glow></x-neural-glow>
```

### As a page background

```html
<div style="position: relative; min-height: 100vh;">
  <x-neural-glow style="position: absolute; inset: 0; z-index: 0;"></x-neural-glow>
  <main style="position: relative; z-index: 1;">
    <!-- Your content here -->
  </main>
</div>
```

### Custom deep-sea palette

```html
<x-neural-glow
  color-primary="#7b2ff7"
  color-secondary="#00ff87"
  color-background="#050520"
  orb-count="25"
  connection-distance="0.2">
</x-neural-glow>
```

### Non-interactive ambient mode

```html
<x-neural-glow
  interactive="false"
  pulse-speed="0.3"
  rest-rate="8">
</x-neural-glow>
```

### JavaScript control

```js
const glow = document.querySelector('x-neural-glow');
glow.orbCount = 30;
glow.colorPrimary = '#ff006e';
glow.pulseSpeed = 2.0;
glow.interactive = false;
```

### ESM import

```js
import { init } from '@vanelsas/baredom/x-neural-glow';
init();
```
