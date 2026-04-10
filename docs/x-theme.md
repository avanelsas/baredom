# x-theme

Centralised theming wrapper for BareDom web components. Wrap any subtree with `<x-theme>` to apply a consistent design token palette to all descendant components.

## Tag

```html
<x-theme preset="ocean">
  <x-button>Themed button</x-button>
  <x-alert text="Themed alert"></x-alert>
</x-theme>
```

## Attributes

| Attribute | Type   | Default     | Description                           |
|-----------|--------|-------------|---------------------------------------|
| `preset`  | string | `"default"` | Name of a built-in or custom preset.  |

## Properties

| Property | Type   | Reflects | Description                     |
|----------|--------|----------|---------------------------------|
| `.preset`| string | yes      | Gets/sets the `preset` attribute. |

## Events

None.

## Slots

| Slot      | Description                                       |
|-----------|---------------------------------------------------|
| (default) | All child content. Tokens cascade into children.  |

## CSS Custom Properties (Design Tokens)

These tokens are set by `x-theme` on its `:host` and inherited by all descendant components via CSS custom property inheritance through shadow DOM.

### Colors (20 tokens)

| Token                          | Description                    |
|--------------------------------|--------------------------------|
| `--x-color-primary`            | Primary brand color            |
| `--x-color-primary-hover`      | Primary hover state            |
| `--x-color-primary-active`     | Primary active/pressed state   |
| `--x-color-secondary`          | Secondary accent color         |
| `--x-color-secondary-hover`    | Secondary hover state          |
| `--x-color-secondary-active`   | Secondary active state         |
| `--x-color-tertiary`           | Tertiary/subtle accent         |
| `--x-color-tertiary-hover`     | Tertiary hover state           |
| `--x-color-tertiary-active`    | Tertiary active state          |
| `--x-color-surface`            | Card/panel background          |
| `--x-color-surface-hover`      | Surface hover state            |
| `--x-color-surface-active`     | Surface active/pressed state   |
| `--x-color-bg`                 | Page-level background          |
| `--x-color-text`               | Primary text color             |
| `--x-color-text-muted`         | Secondary/muted text           |
| `--x-color-border`             | Default border color           |
| `--x-color-focus-ring`         | Focus indicator color          |
| `--x-color-danger`             | Destructive/error actions      |
| `--x-color-success`            | Success/positive states        |
| `--x-color-warning`            | Warning/caution states         |

### Typography (4 tokens)

| Token                  | Default                                    |
|------------------------|--------------------------------------------|
| `--x-font-family`      | `system-ui, -apple-system, sans-serif`     |
| `--x-font-family-mono` | `ui-monospace, 'SF Mono', monospace`        |
| `--x-font-size-sm`     | `0.875rem`                                  |
| `--x-font-size-base`   | `1rem`                                      |

### Shape (4 tokens)

| Token            | Default    |
|------------------|------------|
| `--x-radius-sm`  | `0.375rem` |
| `--x-radius-md`  | `0.75rem`  |
| `--x-radius-lg`  | `1rem`     |
| `--x-radius-full`| `9999px`   |

### Elevation (3 tokens)

| Token          | Description     |
|----------------|-----------------|
| `--x-shadow-sm`| Subtle shadow   |
| `--x-shadow-md`| Medium elevation|
| `--x-shadow-lg`| High elevation  |

### Motion (2 tokens)

| Token                    | Default                    |
|--------------------------|----------------------------|
| `--x-transition-duration`| `140ms`                    |
| `--x-transition-easing`  | `cubic-bezier(0.2,0,0,1)` |

## Built-in Presets

| Preset          | Description                                                       |
|-----------------|-------------------------------------------------------------------|
| `default`       | Blue/slate palette matching existing component defaults.           |
| `ocean`         | Teal/cyan, cool aquatic tones.                                     |
| `forest`        | Green/earth, warm natural tones.                                   |
| `sunset`        | Orange/red, warm vibrant tones.                                    |
| `neo-brutalist`  | High-contrast black/white, zero border-radius, offset shadows.    |
| `aurora`        | Soft purple/teal/pink with semi-transparent surfaces.              |
| `mono-ai`       | Monochrome + neon cyan accent, monospace font.                     |
| `warm-mineral`  | Earthy terracotta/sand/clay, digital naturalism.                   |

All presets define both light and dark mode token values. Dark mode is activated automatically via `@media (prefers-color-scheme: dark)`.

## Custom Themes

### CSS property overrides

Override any token directly on the element:

```html
<x-theme preset="default" style="--x-color-primary: #e11d48;">
  <x-button>Custom accent</x-button>
</x-theme>
```

### `registerPreset()` API

Register a reusable named preset via JavaScript:

```js
import { registerPreset } from '@vanelsas/baredom/x-theme';

registerPreset("acme-brand", {
  light: {
    "--x-color-primary": "#e11d48",
    "--x-color-secondary": "#7c3aed",
    "--x-color-surface": "#fefefe"
  },
  dark: {
    "--x-color-primary": "#fb7185",
    "--x-color-secondary": "#a78bfa",
    "--x-color-surface": "#1a1a2e"
  }
});
```

Then use it like any built-in preset:

```html
<x-theme preset="acme-brand">...</x-theme>
```

Partial presets are supported. Any tokens not specified fall back to the `default` preset values.

## Multiple Themes Per Page

```html
<x-theme preset="ocean">
  <x-sidebar>...</x-sidebar>
</x-theme>

<x-theme preset="sunset">
  <main>...</main>
</x-theme>
```

## Nested Themes

Inner `<x-theme>` elements override outer ones via CSS custom property inheritance:

```html
<x-theme preset="ocean">
  <x-button>Ocean button</x-button>
  <x-theme preset="sunset">
    <x-button>Sunset button (overrides ocean)</x-button>
  </x-theme>
</x-theme>
```

## Accessibility

- `x-theme` uses `display: contents` and adds no visual or interactive elements.
- All theme tokens respect `@media (prefers-color-scheme: dark)` for dark mode support.
- The `neo-brutalist` preset provides high contrast suitable for accessibility needs.

## Rendering

`x-theme` is a pure theming scope. It uses shadow DOM with only a `<style>` element and a `<slot>`. It has no visual rendering of its own and does not affect layout (`display: contents`).
