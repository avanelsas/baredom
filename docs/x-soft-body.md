# x-soft-body

A card container whose visible border is an SVG path driven by spring physics. When the pointer hovers or grabs the card, nearby control points displace and spring back, creating an organic jiggle/stretch/squish effect.

## Tag Name

```
x-soft-body
```

## Attributes

| Attribute      | Type    | Default | Range      | Description                          |
|----------------|---------|---------|------------|--------------------------------------|
| `stiffness`    | number  | 180     | 10 – 1000  | Spring stiffness (snappier = higher) |
| `damping`      | number  | 12      | 1 – 100    | Spring damping (less bounce = higher)|
| `radius`       | number  | 16      | 6 – 200    | Corner radius of the rest shape (px) |
| `intensity`    | number  | 1.0     | 0 – 5      | Displacement multiplier              |
| `grab-radius`  | number  | 80      | 10 – 500   | Pointer influence radius (px)        |
| `disabled`     | boolean | false   | —          | Disables physics; renders static     |

## Properties

| Property      | Type    | Attribute      | Description                 |
|---------------|---------|----------------|-----------------------------|
| `stiffness`   | number  | `stiffness`    | Spring stiffness            |
| `damping`     | number  | `damping`      | Spring damping              |
| `radius`      | number  | `radius`       | Corner radius               |
| `intensity`   | number  | `intensity`    | Displacement multiplier     |
| `grabRadius`  | number  | `grab-radius`  | Pointer influence radius    |
| `disabled`    | boolean | `disabled`     | Disables physics            |

## Events

| Event                 | Detail | When                    |
|-----------------------|--------|-------------------------|
| `x-soft-body-grab`   | `{}`   | Pointer down on card    |
| `x-soft-body-release` | `{}`  | Pointer up on card      |

## Slots

| Slot      | Description              |
|-----------|--------------------------|
| (default) | Card content             |

## CSS Custom Properties

| Property                    | Default (light)                  | Default (dark)                   |
|-----------------------------|----------------------------------|----------------------------------|
| `--x-soft-body-bg`         | `#ffffff`                        | `#1e293b`                        |
| `--x-soft-body-border`     | `#e2e8f0`                        | `#475569`                        |
| `--x-soft-body-border-width` | `1.5`                          | `1.5`                            |
| `--x-soft-body-shadow`     | `0 1px 3px rgba(0,0,0,0.1)`     | `0 1px 3px rgba(0,0,0,0.3)`     |
| `--x-soft-body-padding`    | `1rem`                           | `1rem`                           |

## Shadow Parts

| Part      | Element     | Description                |
|-----------|-------------|----------------------------|
| `svg`     | `<svg>`     | The SVG container          |
| `shape`   | `<path>`    | The SVG shape outline      |
| `content` | `<div>`     | Content wrapper            |

## Accessibility

- The SVG is marked `aria-hidden="true"` (purely decorative).
- `prefers-reduced-motion: reduce` disables the spring animation and renders a static rounded rectangle.
- Light/dark mode adapts automatically via `prefers-color-scheme`.

## Usage

### Basic

```html
<x-soft-body>
  <h3>Squishy Card</h3>
  <p>Hover or grab me!</p>
</x-soft-body>
```

### Custom Physics

```html
<x-soft-body stiffness="300" damping="8" intensity="2" radius="24">
  <p>Bouncier card with larger corners</p>
</x-soft-body>
```

### Disabled

```html
<x-soft-body disabled>
  <p>Static card — no physics</p>
</x-soft-body>
```

### Styled

```html
<x-soft-body style="
  --x-soft-body-bg: #fef3c7;
  --x-soft-body-border: #f59e0b;
  --x-soft-body-shadow: 0 4px 12px rgba(245,158,11,0.25);
">
  <p>Warm amber card</p>
</x-soft-body>
```

### JavaScript

```js
import '@vanelsas/baredom/x-soft-body';

const card = document.querySelector('x-soft-body');
card.stiffness = 250;
card.intensity = 1.5;

card.addEventListener('x-soft-body-grab', () => {
  console.log('Card grabbed!');
});
```
