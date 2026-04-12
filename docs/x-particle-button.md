# x-particle-button

A button whose surface emits and reabsorbs visual fragments in response to interaction. The control feels energetic, tactile, and materially alive.

Three coordinated layers create the effect: a premium base button with hover lift and press compression, a material overlay with specular highlight tracking the pointer, and a canvas particle system that emits from button edges on hover and from the press-point on click.

**Tag:** `<x-particle-button>`

## Usage

```html
<!-- Subtle premium (default) -->
<x-particle-button>Click Me</x-particle-button>

<!-- Energetic spark -->
<x-particle-button mode="spark" variant="danger">Delete</x-particle-button>

<!-- Warm ember glow -->
<x-particle-button mode="ember" variant="warning">Proceed</x-particle-button>

<!-- Full dissolve + reform -->
<x-particle-button mode="disperse" variant="success">Confirm</x-particle-button>
```

## Modes

The `mode` attribute controls all particle behavior. The `variant` attribute controls button colors only.

| Mode | Hover | Press | Character |
|------|-------|-------|-----------|
| `dust` | 1-3 tiny edge particles, gentle upward drift | 8-12 from press point, short-lived | Premium, subtle, subliminal |
| `spark` | 2-4 per frame, sharper outward | 15-25, narrow cone, high velocity, streaks | Tech-forward, energetic |
| `ember` | 1-2, slow upward float | 10-15, wide cone, warm glow trails | Organic, atmospheric |
| `burst` | minimal | 25-40, wide cone, high velocity | Confirmation, action |
| `disperse` | moderate edge emission | Surface dissolve + spring reform | Hero UI, experimental |

## Particle Types

Particles are a weighted mix, not identical circles:
- **Dust** (70%): 1-2px circles, fast fade
- **Fragments** (20%): 2-4px rounded squares, moderate lifetime
- **Streaks** (10%): elongated rects with trailing positions, longer lifetime

Color is sampled from the button's computed background: 60% fill, 25% lighter highlight, 15% darker accent.

## Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `disabled` | boolean | `false` | Disables the button |
| `loading` | boolean | `false` | Shows spinner, disables interaction |
| `pressed` | boolean | `false` | Toggle state (aria-pressed) |
| `type` | `"button"` \| `"submit"` \| `"reset"` | `"button"` | Form button type |
| `variant` | `"primary"` \| `"secondary"` \| `"tertiary"` \| `"ghost"` \| `"danger"` \| `"success"` \| `"warning"` | `"primary"` | Button color scheme |
| `size` | `"sm"` \| `"md"` \| `"lg"` | `"md"` | Button size |
| `label` | string | — | Accessible label when no text content |
| `mode` | `"dust"` \| `"spark"` \| `"ember"` \| `"burst"` \| `"disperse"` | `"dust"` | Particle behavior mode |
| `particle-count` | number | `40` | Max concurrent particles (10-100) |
| `intensity` | number | `50` | Burst force multiplier (1-100) |
| `particle-size` | number | `3` | Base particle radius in px (1-8) |
| `reassemble-speed` | number | `500` | Reform duration in ms, disperse mode (100-2000) |

## Properties

| Property | Type | Reflects |
|----------|------|----------|
| `disabled` | boolean | `disabled` attribute |
| `loading` | boolean | `loading` attribute |
| `pressed` | boolean | `pressed` attribute |

## Events

| Event | Detail | Description |
|-------|--------|-------------|
| `press` | `{source}` | Button was pressed |
| `press-start` | `{source}` | Press began |
| `press-end` | `{source}` | Press ended |
| `hover-start` | `{}` | Pointer entered |
| `hover-end` | `{}` | Pointer left |
| `focus-visible` | `{}` | Keyboard focus received |
| `burst` | `{mode, press-x, press-y}` | Particle burst started from press point |
| `reform` | `{mode, duration}` | Particles finished reforming (disperse mode) |

## Slots

| Slot | Description |
|------|-------------|
| (default) | Button text content |
| `icon-start` | Icon before label |
| `icon-end` | Icon after label |
| `spinner` | Custom loading spinner |

## Parts

| Part | Element | Description |
|------|---------|-------------|
| `button` | `<button>` | The native button |
| `material-overlay` | `<span>` | Specular highlight + glow layer |
| `canvas` | `<canvas>` | Particle rendering surface |
| `inner` | `<span>` | Content wrapper |
| `label` | `<span>` | Text label wrapper |
| `icon-start` | `<span>` | Start icon wrapper |
| `icon-end` | `<span>` | End icon wrapper |
| `spinner` | `<span>` | Spinner wrapper |

## Surface Effects

The button itself reacts to interaction:
- **Hover**: lifts 1px (`translateY(-1px)`), elevated shadow
- **Press**: compresses (`translateY(1px) scale(0.97)`), tighter shadow
- **Click**: glow pulse animation (400ms box-shadow expansion)
- **Material overlay**: specular highlight radial-gradient follows the pointer position

## CSS Custom Properties

All properties follow `--x-particle-button-*` naming. Inherits theme tokens from `<x-theme>` with fallbacks.

## Accessibility

- Full keyboard support (Space/Enter)
- `aria-pressed`, `aria-busy`, `aria-label` reflected
- Canvas and material overlay are `aria-hidden="true"`
- `prefers-reduced-motion: reduce` hides canvas, overlay, and glow animation
- Touch targets >= 44px on coarse pointer devices
- Disabled/loading states block all particle emission

## Form Integration

`type="submit"` submits the form; `type="reset"` resets it.

## Press-Point Realism

Particles emit from where you click, not the center. The burst direction radiates away from the press point. Keyboard activation (Enter/Space) uses button center.
