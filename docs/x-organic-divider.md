# x-organic-divider

An organic, curved section divider. Creates fluid SVG-based transitions between page sections with support for multiple layered shapes, animations, and full theming.

## Tag

```html
<x-organic-divider></x-organic-divider>
```

## Attributes

| Attribute   | Type    | Default    | Description |
|-------------|---------|------------|-------------|
| `shape`     | string  | `"wave"`   | Preset shape name. One of: `wave`, `waves`, `blob-edge`, `mountain`, `drip`, `slant`, `scallop`, `cloud`. |
| `layers`    | string  | `"1"`      | Number of stacked shape layers (1-5). Multiple layers create depth. |
| `height`    | string  | `"120px"`  | CSS height value for the divider. |
| `flip`      | boolean | absent     | Flips the divider vertically (curve points upward). |
| `mirror`    | boolean | absent     | Mirrors the divider horizontally. |
| `animation` | string  | `"none"`   | Animation mode. One of: `none`, `drift`, `morph`. Respects `prefers-reduced-motion`. `morph` is disabled when `path` is set. |
| `path`      | string  | --         | Custom SVG path `d` attribute string. Overrides `shape` when present. |

## Properties

| Property    | Type    | Reflects Attribute |
|-------------|---------|-------------------|
| `shape`     | string  | `shape`           |
| `layers`    | string  | `layers`          |
| `height`    | string  | `height`          |
| `flip`      | boolean | `flip`            |
| `mirror`    | boolean | `mirror`          |
| `animation` | string  | `animation`       |
| `path`      | string  | `path`            |

## Events

None.

## Slots

None. This is a purely decorative component.

## CSS Custom Properties

| Property | Default (Light) | Default (Dark) | Description |
|----------|-----------------|----------------|-------------|
| `--x-organic-divider-color-1` | `rgba(99,102,241,0.15)` | `rgba(129,140,248,0.12)` | Back layer fill color |
| `--x-organic-divider-color-2` | `rgba(99,102,241,0.25)` | `rgba(129,140,248,0.22)` | Layer 2 fill color |
| `--x-organic-divider-color-3` | `rgba(99,102,241,0.40)` | `rgba(129,140,248,0.35)` | Layer 3 fill color |
| `--x-organic-divider-color-4` | `rgba(99,102,241,0.60)` | `rgba(129,140,248,0.50)` | Layer 4 fill color |
| `--x-organic-divider-color-5` | `rgba(99,102,241,0.85)` | `rgba(129,140,248,0.70)` | Front layer fill color |
| `--x-organic-divider-height` | `120px` | `120px` | Fallback height |
| `--x-organic-divider-animate-duration` | `6s` | `6s` | Animation cycle duration |
| `--x-organic-divider-animate-timing` | `ease-in-out` | `ease-in-out` | Animation timing function (morph only; drift always uses `linear`) |

## CSS Parts

| Part   | Description |
|--------|-------------|
| `base` | The outer container `<div>` that wraps the SVG. |

## Accessibility

- Always decorative: sets `role="presentation"` and `aria-hidden="true"` on the host element.
- The SVG element also has `aria-hidden="true"`.

## Preset Shapes

| Name | Description |
|------|-------------|
| `wave` | Single smooth sine wave (default) |
| `waves` | Complex multi-undulation wave |
| `blob-edge` | Organic irregular edge |
| `mountain` | Rolling hills/peaks |
| `drip` | Liquid dripping stalactites |
| `slant` | Diagonal cut with soft curve |
| `scallop` | Repeating scalloped arcs |
| `cloud` | Cumulus cloud bumps |

## Multi-Layer System

When `layers` is greater than 1, multiple copies of the shape are stacked with progressive vertical and horizontal offsets, creating a layered depth effect.

- Each layer uses a different color (`--x-organic-divider-color-N`).
- Back layers are more transparent; the front layer is most opaque.
- Default opacities (5 layers): 0.30, 0.475, 0.65, 0.825, 1.0.

## Animation Modes

| Value | Effect |
|-------|--------|
| `none` | No animation (default) |
| `drift` | Horizontal wave movement. Uses a wider tiled path for seamless looping. |
| `morph` | Shape interpolates between two path states via CSS `d` property animation. Only works with presets (disabled when `path` is set). |

All animations respect `prefers-reduced-motion: reduce`.

## Usage Examples

### Simple wave divider

```html
<x-organic-divider></x-organic-divider>
```

### Between colored sections

```html
<section style="background: #1a1a2e; color: white; padding: 48px;">
  Section One
</section>
<x-organic-divider shape="waves" layers="3"
  style="--x-organic-divider-color-1: #1a1a2e;
         --x-organic-divider-color-2: #16213e;
         --x-organic-divider-color-3: #0f3460;">
</x-organic-divider>
<section style="background: #0f3460; color: white; padding: 48px;">
  Section Two
</section>
```

### Flipped divider (curve points up)

```html
<x-organic-divider shape="mountain" flip></x-organic-divider>
```

### Multi-layer with animation

```html
<x-organic-divider shape="wave" layers="3" animation="drift"></x-organic-divider>
```

### Morphing cloud divider

```html
<x-organic-divider shape="cloud" animation="morph"
  style="--x-organic-divider-animate-duration: 8s;">
</x-organic-divider>
```

### Custom height and colors

```html
<x-organic-divider shape="scallop" height="80px"
  style="--x-organic-divider-color-1: rgba(236,72,153,0.6);">
</x-organic-divider>
```

### Custom SVG path

```html
<x-organic-divider
  path="M0,80 L400,20 L800,80 L1200,20 L1200,120 L0,120 Z">
</x-organic-divider>
```
