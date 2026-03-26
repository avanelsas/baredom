# x-spinner

An indeterminate loading-indicator component. Renders a spinning ring with configurable size, colour variant, and accessible label.

## Tag name

```html
<x-spinner></x-spinner>
```

## Attributes

| Attribute | Type   | Default     | Description                              |
|-----------|--------|-------------|------------------------------------------|
| `size`    | string | `"md"`      | Ring diameter: `xs` `sm` `md` `lg` `xl` |
| `variant` | string | `"default"` | Colour: `default` `primary` `success` `warning` `danger` |
| `label`   | string | `"Loading"` | `aria-label` text announced to screen readers |

## Properties

All properties reflect to/from their corresponding attribute.

| Property  | Type   | Reflects attribute |
|-----------|--------|--------------------|
| `size`    | string | `size`             |
| `variant` | string | `variant`          |
| `label`   | string | `label`            |

## Events

None. `x-spinner` is a display-only component.

## CSS parts

| Part   | Description            |
|--------|------------------------|
| `ring` | The spinning ring element |

## CSS custom properties

| Property                    | Default              | Description                        |
|-----------------------------|----------------------|------------------------------------|
| `--x-spinner-size`          | size-enum driven     | Override ring diameter directly    |
| `--x-spinner-color`         | `currentColor`       | Arc colour (overridden by variant) |
| `--x-spinner-track-color`   | `rgba(0,0,0,0.12)`   | Background track colour            |
| `--x-spinner-thickness`     | `2px`                | Border / stroke width              |
| `--x-spinner-duration`      | `0.75s`              | Full-rotation period               |

Dark mode automatically adjusts `--x-spinner-track-color` to `rgba(255,255,255,0.15)` via `@media (prefers-color-scheme:dark)`.

## Size reference

| Value | Diameter |
|-------|----------|
| `xs`  | 16 px    |
| `sm`  | 20 px    |
| `md`  | 24 px    |
| `lg`  | 32 px    |
| `xl`  | 48 px    |

## Accessibility

- Host element carries `role="status"` and `aria-live="polite"` — set once at initialisation.
- `aria-label` is derived from the `label` attribute (default: `"Loading"`). Update the attribute to describe what is loading when the context is known.
- The ring element itself is `aria-hidden="true"` — it is purely decorative.
- The spinning animation is paused (`animation-play-state: paused`) when `prefers-reduced-motion: reduce` is active. The ring remains visible as a static cue.

## Usage

### Basic

```html
<x-spinner></x-spinner>
```

### Sizes

```html
<x-spinner size="xs"></x-spinner>
<x-spinner size="sm"></x-spinner>
<x-spinner size="md"></x-spinner>
<x-spinner size="lg"></x-spinner>
<x-spinner size="xl"></x-spinner>
```

### Variants

```html
<x-spinner variant="default"></x-spinner>
<x-spinner variant="primary"></x-spinner>
<x-spinner variant="success"></x-spinner>
<x-spinner variant="warning"></x-spinner>
<x-spinner variant="danger"></x-spinner>
```

### Custom label

```html
<x-spinner label="Uploading file…"></x-spinner>
```

### Custom size via CSS property

```html
<x-spinner style="--x-spinner-size:64px; --x-spinner-thickness:4px;"></x-spinner>
```

### JavaScript / ESM

```js
import { init } from '@vanelsas/baredom/x-spinner';
init();

const spinner = document.createElement('x-spinner');
spinner.size    = 'lg';
spinner.variant = 'primary';
spinner.label   = 'Saving changes…';
document.body.appendChild(spinner);
```
