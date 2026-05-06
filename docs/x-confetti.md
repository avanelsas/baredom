# x-confetti

A celebration effect component that emits a burst of confetti particles on demand. Stateless: configuration lives in attributes; the particle simulation is ephemeral. Triggering is imperative via `el.fire()` — confetti is an event, not a state.

## Attributes

| Attribute    | Values                                                  | Default            |
|--------------|---------------------------------------------------------|--------------------|
| `mode`       | `overlay` \| `inline`                                   | `overlay`          |
| `origin`     | `top` \| `center` \| `bottom` \| `point`                | `top`              |
| `count`      | integer, clamped to `[1, 300]`                          | `80`               |
| `spread`     | degrees, clamped to `[0, 180]` (half-angle around base) | `60`               |
| `velocity`   | px/frame, clamped to `[1, 60]`                          | `14`               |
| `gravity`    | px/frame², clamped to `[-2, 2]`                         | `0.25`             |
| `duration`   | ms, clamped to `[200, 20000]` (max particle lifetime)   | `4000`             |
| `colors`     | comma-separated CSS colors                              | theme palette      |
| `shapes`     | comma-separated subset of `square,circle,ribbon,star`   | `square,ribbon`    |
| `auto-fire`  | boolean (presence) — fires once per element instance    | absent             |
| `disabled`   | boolean (presence) — `fire()` is a no-op                | absent             |

Invalid values are silently corrected to defaults. The `colors` and `shapes` attributes accept whitespace around commas.

For `origin="center"` and `origin="point"`, particles are launched in all directions (the `spread` attribute has no effect). For `top` and `bottom`, particles spread `±spread°` around the vertical axis.

`auto-fire` triggers exactly once per element instance, on its first `connectedCallback`. Removing and re-adding the attribute, or detaching and reattaching the element, will not re-fire — use `el.fire()` for explicit re-triggering.

## Properties

All attributes have matching JS properties that reflect to/from their HTML attribute. The boolean `auto-fire` attribute maps to the `autoFire` JS property.

## Methods

### `fire(options?)`

Trigger a burst. Returns `void`. Concurrent calls are additive — each call adds particles to the live simulation.

`options` is an optional plain object. Any field omitted falls back to the current attribute value:

| Field      | Type             | Notes                                                                  |
|------------|------------------|------------------------------------------------------------------------|
| `count`    | number           | Particles to spawn (clamped to `[1, 300]`).                            |
| `origin`   | string           | Same vocabulary as the `origin` attribute.                             |
| `originX`  | number           | CSS pixels relative to the canvas. Used only when `origin === "point"`. |
| `originY`  | number           | CSS pixels relative to the canvas. Used only when `origin === "point"`. |
| `spread`   | number           | Half-angle in degrees, clamped to `[0, 180]`.                          |
| `velocity` | number           | Initial speed (px/frame), clamped to `[1, 60]`.                        |
| `gravity`  | number           | Per-frame acceleration, clamped to `[-2, 2]`.                          |
| `duration` | number           | Max particle lifetime (ms), clamped to `[200, 20000]`.                 |
| `colors`   | string \| string[] | Override palette for this burst.                                     |
| `shapes`   | string \| string[] | Override shapes for this burst.                                      |

```js
const c = document.querySelector("x-confetti");
c.fire();                                            // use current config
c.fire({ count: 120, origin: "center" });            // omni-burst
c.fire({ origin: "point", originX: 200, originY: 80 });
c.fire({ colors: ["#ff0066", "#00ccaa", "gold"] });
```

## Events

| Event             | Detail                              | When                                                          |
|-------------------|-------------------------------------|---------------------------------------------------------------|
| `x-confetti-fire` | `{ count: number, origin: string }` | At the start of every `fire()` call (also fires on auto-fire and under reduced motion). |
| `x-confetti-end`  | `{ duration: number }`              | When the last live particle has despawned. Under reduced motion, fires immediately with `duration: 0`. |

Both events bubble and are composed; neither is cancelable.

## Slots

None. The component is decorative-only.

## Shadow parts

| Part           | Element  | Purpose                                       |
|----------------|----------|-----------------------------------------------|
| `canvas-wrap`  | `<div>`  | Absolute-positioned container for the canvas. |
| `canvas`       | `<canvas>` | The 2D drawing surface.                     |

Target with `::part()` to override layout (rare):

```css
x-confetti::part(canvas-wrap) { /* e.g. clip with border-radius */ }
```

## Accessibility

- The host is `aria-hidden="true"`, `tabindex="-1"`, and `pointer-events: none` — it never traps focus or steals interaction.
- Under `@media (prefers-reduced-motion: reduce)`, `fire()` skips the particle simulation but still dispatches `x-confetti-fire` and an immediate `x-confetti-end`. Consumers can use these events to provide non-visual feedback (haptics, sound, status messages) when motion is suppressed.

## Modes

- `mode="overlay"` (default) — host becomes `position: fixed; inset: 0` with `z-index: var(--x-confetti-z-index, 9999)`. Drop a single instance anywhere in the DOM and call `fire()` for full-page celebrations.
- `mode="inline"` — host becomes `position: relative` and the canvas fills its bounding box. Useful for celebrating within a specific surface (a card, a banner). The host needs an explicit size from CSS.

## Mobile / responsive

- The canvas is automatically resized via `ResizeObserver` and accounts for `devicePixelRatio` (capped at 2 for performance).
- Default `count` is `80`. The cap of `300` keeps RAF cost predictable on low-end devices. Tune via the `count` attribute or `fire({count})`.
- No pointer interaction (the component does not listen for pointer events).

## Styling

Override CSS custom properties on the host element:

```css
x-confetti {
  --x-confetti-z-index: 1100;
}
```

### Custom properties

| Property                | Default | Purpose                                          |
|-------------------------|---------|--------------------------------------------------|
| `--x-confetti-z-index`  | `9999`  | Stacking context for `mode="overlay"`.           |

Confetti color comes from the `colors` attribute (or its default theme palette: `--x-color-primary`, `--x-color-accent`, `--x-color-success`, `--x-color-warning`, `--x-color-info`).

## Examples

```html
<!-- Drop-in overlay; fire from a button -->
<x-confetti id="party"></x-confetti>
<button onclick="document.getElementById('party').fire()">Celebrate</button>

<!-- Auto-fire on success page -->
<x-confetti auto-fire count="150" duration="6000"></x-confetti>

<!-- Inline burst inside a card -->
<x-spotlight-card style="position:relative">
  <x-confetti mode="inline" origin="bottom" velocity="22"></x-confetti>
  <h2>You did it!</h2>
</x-spotlight-card>
```
