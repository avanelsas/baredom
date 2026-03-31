# x-card

Themeable surface container for grouping related content.

## Attributes

| Attribute | Values | Default |
|-----------|--------|---------|
| `variant` | `elevated` \| `outlined` \| `filled` \| `ghost` | `elevated` |
| `padding` | `none` \| `sm` \| `md` \| `lg` | `md` |
| `radius` | `none` \| `sm` \| `md` \| `lg` \| `xl` | `lg` |
| `interactive` | boolean (presence) | absent |
| `disabled` | boolean (presence) | absent |
| `label` | string | — |

## Properties

| Property | Type | Reflects to attribute |
|----------|------|-----------------------|
| `interactive` | boolean | yes (`interactive`) |
| `disabled` | boolean | yes (`disabled`) |

## Event

| Event | Detail | Bubbles | Composed |
|-------|--------|---------|----------|
| `press` | `{}` | yes | yes |

Fired on click, Enter, or Space when `interactive` is set and `disabled` is absent.

## Accessibility

Default state: no role or tabindex set.

Interactive mode:
- `role="button"` added to host
- `tabindex="0"` (focusable) when not disabled
- `tabindex="-1"` (non-focusable) when disabled
- `aria-disabled="true"` when disabled
- Enter and Space trigger `press`; Space also calls `preventDefault`

The `label` attribute maps to `aria-label` on the host.

## Slot

Default slot only. Accepts arbitrary content.

## Styling

Override CSS custom properties on the host element:

```css
x-card {
  --x-card-background: white;
  --x-card-color: black;
}
```

### Custom properties

| Property | Default (light) | Purpose |
|----------|-----------------|---------|
| `--x-card-background` | `rgba(255,255,255,0.92)` | Base surface colour |
| `--x-card-color` | `#111827` | Text colour |
| `--x-card-border-color` | `rgba(17,24,39,0.12)` | Outlined variant border |
| `--x-card-filled-background` | `rgba(241,245,249,0.96)` | Filled variant surface |
| `--x-card-ghost-background` | `transparent` | Ghost variant surface |
| `--x-card-hover-background` | `rgba(15,23,42,0.04)` | Interactive hover state |
| `--x-card-press-background` | `rgba(15,23,42,0.08)` | Interactive active/press state |
| `--x-card-shadow` | `0 10px 24px rgba(15,23,42,0.10)` | Elevated variant shadow |
| `--x-card-focus-ring` | `rgba(59,130,246,0.55)` | Focus ring colour |
| `--x-card-disabled-opacity` | `0.6` | Opacity when disabled |
| `--x-card-padding-none` | `0` | Padding token — none |
| `--x-card-padding-sm` | `0.5rem` | Padding token — sm |
| `--x-card-padding-md` | `1rem` | Padding token — md |
| `--x-card-padding-lg` | `1.5rem` | Padding token — lg |
| `--x-card-radius-none` | `0` | Radius token — none |
| `--x-card-radius-sm` | `0.375rem` | Radius token — sm |
| `--x-card-radius-md` | `0.75rem` | Radius token — md |
| `--x-card-radius-lg` | `1rem` | Radius token — lg |
| `--x-card-radius-xl` | `1.5rem` | Radius token — xl |
| `--x-card-transition-duration` | `140ms` | Hover/press transition speed |
| `--x-card-transition-timing` | `ease` | Hover/press transition easing |

Dark mode values are set automatically via `@media (prefers-color-scheme: dark)`.
Transition animations are suppressed via `@media (prefers-reduced-motion: reduce)`.

### Internal styling hooks (`part="base"`)

The inner wrapper div carries `part="base"` and the following `data-*` attributes that can be targeted in external stylesheets:

| Attribute | Values |
|-----------|--------|
| `data-variant` | `elevated` \| `outlined` \| `filled` \| `ghost` |
| `data-padding` | `none` \| `sm` \| `md` \| `lg` |
| `data-radius` | `none` \| `sm` \| `md` \| `lg` \| `xl` |
| `data-interactive` | `"true"` or absent |
| `data-disabled` | `"true"` or absent |

Note: `data-*` values reflect the *normalised* value, not the raw attribute. Invalid attribute values are silently corrected to the default.

## Example

```html
<!-- Static card -->
<x-card variant="outlined" padding="md">
  <h3>Title</h3>
  <p>Content here.</p>
</x-card>

<!-- Interactive card -->
<x-card variant="elevated" padding="md" interactive label="Open details">
  <h3>Click me</h3>
  <p>Fires a press event.</p>
</x-card>

<script>
  document.querySelector('x-card[interactive]')
    .addEventListener('press', () => console.log('pressed'));
</script>
```
