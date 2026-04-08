# x-liquid-dock

A viscous, floating navigation dock that uses SVG filters to create organic gooey liquid effects. When you hover over items, the liquid surface bulges toward your cursor. Moving between items stretches and snaps a gooey bridge. A displacement map creates ripples across the dock surface.

**Tag name:** `x-liquid-dock`

## Usage

```html
<x-liquid-dock>
  <button aria-label="Home">&#x1F3E0;</button>
  <button aria-label="Search">&#x1F50D;</button>
  <button aria-label="Messages">&#x1F4AC;</button>
  <button aria-label="Profile">&#x1F464;</button>
</x-liquid-dock>
```

## Attributes

| Attribute | Type | Default | Range | Description |
|---|---|---|---|---|
| `position` | string | `"bottom"` | `bottom` `top` `left` `right` | Which screen edge the dock anchors to |
| `gap` | number | `8` | 0 -- 40 | Space between items in pixels |
| `blur` | number | `10` | 2 -- 30 | Gaussian blur stdDeviation for gooey merge |
| `threshold` | string | `"1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 18 -7"` | feColorMatrix values | Controls gooey merge sharpness |
| `ripple-scale` | number | `8` | 0 -- 40 | Displacement map intensity for ripple effect |
| `ripple-speed` | number | `0.03` | 0.005 -- 0.15 | Turbulence seed animation speed |
| `color` | string | `"#6366f1"` | Any CSS color | Liquid accent color |
| `magnet-radius` | number | `150` | 40 -- 400 | How far cursor proximity affects items (px) |
| `magnet-strength` | number | `0.6` | 0.0 -- 2.0 | Intensity of per-item scale/translate |
| `bob-intensity` | number | `1.0` | 0.0 -- 2.0 | How much icons bob and tilt (0 = static icons, 2 = dramatic) |
| `disabled` | boolean | `false` | presence | Disables animation and hover effects |

## Properties

| Property | Type | Reflects Attribute |
|---|---|---|
| `position` | `string` | `position` |
| `gap` | `number` | `gap` |
| `blur` | `number` | `blur` |
| `threshold` | `string` | `threshold` |
| `rippleScale` | `number` | `ripple-scale` |
| `rippleSpeed` | `number` | `ripple-speed` |
| `color` | `string` | `color` |
| `magnetRadius` | `number` | `magnet-radius` |
| `magnetStrength` | `number` | `magnet-strength` |
| `bobIntensity` | `number` | `bob-intensity` |
| `disabled` | `boolean` | `disabled` |

## Events

| Event | Bubbles | Composed | Cancelable | Detail |
|---|---|---|---|---|
| `x-liquid-dock-select` | yes | yes | yes | `{ index: number, item: Element, source: "pointer" \| "keyboard" }` |

## Slots

| Slot | Description |
|---|---|
| (default) | Navigation items. Each direct child becomes a dock item with liquid blob backing. |

## CSS Custom Properties

| Property | Default (light) | Default (dark) | Description |
|---|---|---|---|
| `--x-liquid-dock-bg` | `rgba(255,255,255,0.88)` | `rgba(15,23,42,0.84)` | Dock background |
| `--x-liquid-dock-color` | `#6366f1` | `#818cf8` | Liquid/accent color |
| `--x-liquid-dock-border` | `rgba(148,163,184,0.22)` | `rgba(51,65,85,0.9)` | Border color |
| `--x-liquid-dock-shadow` | `0 8px 24px rgba(15,23,42,0.12)` | `0 14px 36px rgba(0,0,0,0.35)` | Box shadow |
| `--x-liquid-dock-z-index` | `50` | `50` | Stacking order |
| `--x-liquid-dock-item-size` | `48px` | `48px` | Default item size |
| `--x-liquid-dock-item-active-scale` | `1.3` | `1.3` | Scale factor for hovered item |
| `--x-liquid-dock-radius` | `20px` | `20px` | Border radius |
| `--x-liquid-dock-padding` | `8px` | `8px` | Internal padding |
| `--x-liquid-dock-gap` | `8px` | `8px` | Gap between items |
| `--x-liquid-dock-glow-color` | `rgba(99,102,241,0.3)` | same | Glow under hovered item |

## Accessibility

- The dock renders a `<nav>` element with `role="navigation"` and `aria-label="Navigation dock"`.
- SVG filter elements are `aria-hidden="true"` with `role="presentation"`.
- Slotted items receive `tabindex="0"` automatically if not already focusable.
- Arrow keys navigate between items (Left/Right for horizontal, Up/Down for vertical position).
- Enter or Space activates the focused item, dispatching `x-liquid-dock-select`.
- Focus-visible style is provided via `::slotted(:focus-visible)`.
- `@media (prefers-reduced-motion: reduce)` disables the animation loop entirely.

## How It Works

The dock uses a "Magnetic Buoy" architecture — icons float like buoys in liquid mercury, staying crisp while physically interacting with the gooey surface beneath them.

### Two-Layer Structure

1. **Liquid layer** — Contains the gooey-filtered blobs. An SVG filter chain (`feGaussianBlur` + `feColorMatrix` + `feTurbulence` + `feDisplacementMap`) is applied to this layer.

2. **Items layer** — Contains the `<slot>` for user-provided navigation items. These sit above the liquid layer and remain 100% crisp (never filtered).

### Dual Blob System (Rest + Phantom)

Each slotted item gets **two blobs** inside the liquid layer:

- **Rest blob** — Stays stationary at the item's resting position. This is the liquid mass that forms the dock body.
- **Phantom blob** — Rises away from the dock edge on hover. Because the gooey filter merges overlapping shapes, this creates a viscous "neck" of liquid stretching from the dock surface up to the icon.

When the cursor moves away, the phantom sinks back and merges flush with the rest blob.

### Icon Bob and Tilt

Icons receive physically-motivated transforms controlled by `bob-intensity`:

- **Bob (lift):** Icons translate away from the dock edge proportional to cursor proximity. Max ~12px at full intensity.
- **Tilt:** Icons rotate slightly toward the cursor direction. Max ~5 degrees.
- **Scale:** Subtle scale increase, max ~1.12. The liquid does the dramatic visual work.

Set `bob-intensity="0"` for static icons with liquid-only animation.

### Ripple Burst on Activation

When an item is clicked or activated via keyboard, the displacement map intensity briefly spikes for ~300ms, creating a splash/ripple effect that propagates across the dock surface.

### Animation Loop

On each frame:
- Item proximity to the cursor is computed using quadratic falloff within `magnet-radius`.
- Phantom blob offset is lerped toward its target for a viscous feel.
- Icon bob/tilt/scale is lerped separately.
- The turbulence seed is incremented to animate ripple displacement.

## Position Variants

```html
<!-- Bottom (default) -->
<x-liquid-dock position="bottom">...</x-liquid-dock>

<!-- Top -->
<x-liquid-dock position="top">...</x-liquid-dock>

<!-- Left (vertical layout) -->
<x-liquid-dock position="left">...</x-liquid-dock>

<!-- Right (vertical layout) -->
<x-liquid-dock position="right">...</x-liquid-dock>
```
