# x-spotlight-card

Card-style surface with a soft radial glow that follows the cursor while it is over the card. Decorative; does not change keyboard or click semantics.

## Attributes

| Attribute | Values | Default |
|-----------|--------|---------|
| `variant` | `elevated` \| `bordered` | `elevated` |
| `radius` | `none` \| `sm` \| `md` \| `lg` \| `xl` | `lg` |
| `padding` | `none` \| `sm` \| `md` \| `lg` | `md` |
| `color` | `primary` \| `secondary` \| `success` \| `warning` \| `danger` \| `info` \| `accent` \| `neutral` | `primary` |
| `intensity` | `soft` \| `medium` \| `strong` | `medium` |
| `size` | `sm` \| `md` \| `lg` \| `xl` | `md` |
| `static` | boolean (presence) | absent |

Invalid attribute values are silently corrected to the default.

## Properties

All seven attributes have matching JS properties (`variant`, `radius`, `padding`, `color`, `intensity`, `size`, `static`) that reflect to and from their attribute.

## Events

None. The component is decorative — wrap it in a `<button>`, `<a>`, or `x-card[interactive]` if you need activation semantics.

## Slots

Default slot only. Renders arbitrary content.

## Accessibility

The spotlight overlay has `aria-hidden="true"` and is purely visual. The component does not set `role`, `tabindex`, or any ARIA state on the host.

## Mobile and reduced-motion

- Pointer tracking uses `pointerenter`, `pointerleave`, and `pointermove` — works with mouse, touch, and pen input.
- Under `@media (prefers-reduced-motion: reduce)`, pointer tracking is disabled and the spotlight stays statically visible at the centre. The same behaviour can be forced explicitly with the `static` attribute.
- The host stretches to fill its container (`width: 100%`); cap with the parent or an outer wrapper if you need a smaller footprint.

## Styling

Override CSS custom properties on the host element:

```css
x-spotlight-card {
  --x-spotlight-card-color: hotpink;
  --x-spotlight-card-intensity: 0.30;
  --x-spotlight-card-size: 320px;
}
```

### Custom properties

| Property | Default (light) | Purpose |
|----------|-----------------|---------|
| `--x-spotlight-card-color` | resolved from `color` attr | Spotlight tint |
| `--x-spotlight-card-intensity` | resolved from `intensity` attr (`0.18` for `medium`) | Peak opacity at the cursor |
| `--x-spotlight-card-size` | resolved from `size` attr (`200px` for `md`) | Spotlight radius |
| `--x-spotlight-card-background` | `var(--x-color-surface, rgba(255,255,255,0.92))` | Card surface |
| `--x-spotlight-card-color-fg` | `var(--x-color-text, #111827)` | Text colour |
| `--x-spotlight-card-border-color` | `var(--x-color-border, rgba(17,24,39,0.12))` | Bordered variant border |
| `--x-spotlight-card-shadow` | `var(--x-shadow-md, …)` | Elevated variant shadow |
| `--x-spotlight-card-padding-{none,sm,md,lg}` | `0`, `0.5rem`, `1rem`, `1.5rem` | Padding tokens |
| `--x-spotlight-card-radius-{none,sm,md,lg,xl}` | matches x-card scale | Radius tokens |
| `--x-spotlight-card-transition-duration` | `var(--x-transition-duration, 200ms)` | Spotlight fade-in / fade-out speed |
| `--x-spotlight-card-transition-timing` | `ease` | Easing |

Dark mode values are set automatically via `@media (prefers-color-scheme: dark)`. Animations are suppressed via `@media (prefers-reduced-motion: reduce)` (which also forces the spotlight static).

### Internal styling hooks

The shadow DOM exposes three `part`s and a few `data-*` selectors for external CSS targeting:

| Selector | Purpose |
|----------|---------|
| `::part(card)` | The outer card container |
| `::part(spotlight)` | The radial-gradient overlay |
| `::part(content)` | Wrapper around the default slot |
| `[part='card'][data-variant='elevated' \| 'bordered']` | Variant hook |
| `[part='card'][data-radius='…']` / `[data-padding='…']` | Spacing hooks |
| `[part='card'][data-static='true']` | Static-spotlight mode |
| `[part='card'][data-active='true']` | Cursor is currently over the card |

`data-*` values reflect the *normalised* value, not the raw attribute.

## Examples

```html
<!-- Default spotlight on hover -->
<x-spotlight-card>
  <h3 style="margin: 0 0 8px;">Premium tier</h3>
  <p>Includes priority support and SSO.</p>
</x-spotlight-card>

<!-- Bordered, danger-tinted, larger spotlight -->
<x-spotlight-card variant="bordered" color="danger" size="lg" intensity="strong">
  <strong>Destructive zone</strong>
</x-spotlight-card>

<!-- Always-on centred spotlight (e.g. for hero sections) -->
<x-spotlight-card static color="accent" intensity="strong">
  <h2>Featured</h2>
</x-spotlight-card>
```
