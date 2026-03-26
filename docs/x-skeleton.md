# x-skeleton

A stateless loading-placeholder component that renders an animated skeleton shape while content is being fetched or computed.

## Registration

```js
import { init } from '@vanelsas/baredom/x-skeleton';
init();
```

## Basic usage

```html
<!-- Default: full-width rect, pulse animation -->
<x-skeleton></x-skeleton>

<!-- Single text line -->
<x-skeleton variant="text"></x-skeleton>

<!-- Circle avatar placeholder -->
<x-skeleton variant="circle" width="40px" height="40px"></x-skeleton>

<!-- Explicit size -->
<x-skeleton height="120px"></x-skeleton>

<!-- Wave animation -->
<x-skeleton animation="wave" height="2rem"></x-skeleton>

<!-- No animation -->
<x-skeleton animation="none"></x-skeleton>
```

## API

### Tag name

`x-skeleton`

### Observed attributes

| Attribute   | Type   | Values                      | Default   | Description                                         |
|-------------|--------|-----------------------------|-----------|-----------------------------------------------------|
| `variant`   | string | `rect` \| `text` \| `circle`| `rect`    | Shape of the placeholder.                           |
| `animation` | string | `pulse` \| `wave` \| `none` | `pulse`   | Animation style. `none` renders a static placeholder. |
| `width`     | string | Any CSS length value        | —         | Explicit width applied as inline style on the base. |
| `height`    | string | Any CSS length value        | —         | Explicit height applied as inline style on the base. |

Invalid values for `variant` and `animation` silently fall back to the default.

### Properties

All properties reflect to their corresponding attribute.

| Property    | Type   | Reflects     | Default   |
|-------------|--------|--------------|-----------|
| `variant`   | string | `variant`    | `"rect"`  |
| `animation` | string | `animation`  | `"pulse"` |
| `width`     | string | `width`      | `""`      |
| `height`    | string | `height`     | `""`      |

Setting a property to an empty string removes the attribute.

### Events

None.

### Slots

None. The component is purely presentational.

### Shadow parts

| Part      | Element | Description                                       |
|-----------|---------|---------------------------------------------------|
| `base`    | `div`   | The visible skeleton shape. Carries animation.    |
| `shimmer` | `div`   | Gradient overlay used by the `wave` animation.    |

### CSS custom properties

| Property                    | Default (light)           | Default (dark)               | Description                          |
|-----------------------------|---------------------------|------------------------------|--------------------------------------|
| `--x-skeleton-color`        | `rgba(0,0,0,0.08)`        | `rgba(255,255,255,0.10)`     | Base fill color of the placeholder.  |
| `--x-skeleton-highlight`    | `rgba(255,255,255,0.65)`  | `rgba(255,255,255,0.18)`     | Gradient highlight for wave animation. |
| `--x-skeleton-border-radius`| `4px`                     | `4px`                        | Corner radius (overridden to `50%` for `circle`, `3px` for `text`). |
| `--x-skeleton-duration`     | `1.5s`                    | `1.5s`                       | Duration of the pulse or wave cycle. |

## Default sizing

| Variant  | Default width | Default height |
|----------|---------------|----------------|
| `rect`   | 100%          | `1rem`         |
| `text`   | 100%          | `1em`          |
| `circle` | `2.5rem`      | `2.5rem`       |

Use the `width` and `height` attributes (or CSS) to override.

## Animations

### `pulse` (default)
The base element fades its opacity from 1 → 0.4 → 1 on a loop. Works for all variants.

### `wave`
A bright gradient sweeps left-to-right across the base via the `[part=shimmer]` overlay. Suitable for wide rect or text placeholders.

### `none`
No animation. The placeholder is fully static.

## Accessibility

`aria-hidden="true"` is set automatically on the host element. Skeleton placeholders carry no semantic meaning; screen readers should rely on the surrounding UI context (e.g., `aria-busy="true"` on a container) to communicate loading state.

## Theming

```html
<!-- Custom purple skeleton -->
<x-skeleton
  height="80px"
  style="
    --x-skeleton-color: #ede9fe;
    --x-skeleton-highlight: rgba(139,92,246,0.25);
    --x-skeleton-border-radius: 8px;
  ">
</x-skeleton>
```

## Light / dark mode

Default colors adapt automatically via `@media (prefers-color-scheme: dark)` inside the shadow stylesheet. Override with CSS custom properties if you manage your own color scheme.

## Reduced motion

Both `pulse` and `wave` animations are disabled when `@media (prefers-reduced-motion: reduce)` is active. The placeholder remains visible as a static block.

## Composition examples

### Card skeleton

```html
<div style="display:flex;gap:12px;align-items:center;">
  <x-skeleton variant="circle" width="48px" height="48px"></x-skeleton>
  <div style="flex:1;display:flex;flex-direction:column;gap:6px;">
    <x-skeleton variant="text" width="60%"></x-skeleton>
    <x-skeleton variant="text" width="40%"></x-skeleton>
  </div>
</div>
<x-skeleton height="120px" style="margin-top:12px;"></x-skeleton>
```

### Table row skeleton

```html
<div style="display:grid;grid-template-columns:1fr 2fr 1fr;gap:8px;">
  <x-skeleton variant="text"></x-skeleton>
  <x-skeleton variant="text"></x-skeleton>
  <x-skeleton variant="text" width="50%"></x-skeleton>
</div>
```

## Normalization rules

- `variant`: any value not in `{"rect","text","circle"}` → `"rect"`
- `animation`: any value not in `{"pulse","wave","none"}` → `"pulse"`
- `width` / `height`: empty string or absent → no inline style (variant default applies)
