# Theming Guide

All components must consume the shared design tokens defined by `x-theme` (`src/baredom/components/x_theme/model.cljs`). In `:host` CSS custom property declarations, wrap hardcoded values with `var(--x-token, fallback)` so the original value is preserved when no `<x-theme>` is present:

```css
/* Correct — themed with fallback */
--x-foo-bg: var(--x-color-surface, #ffffff);

/* Wrong — ignores theme tokens */
--x-foo-bg: #ffffff;
```

## Token Catalogue

### Colors
- `--x-color-primary`, `--x-color-primary-hover`, `--x-color-primary-active`
- `--x-color-secondary`, `--x-color-secondary-hover`, `--x-color-secondary-active`
- `--x-color-tertiary`, `--x-color-tertiary-hover`, `--x-color-tertiary-active`
- `--x-color-surface`, `--x-color-surface-hover`, `--x-color-surface-active`
- `--x-color-bg`
- `--x-color-text`, `--x-color-text-muted`
- `--x-color-border`
- `--x-color-focus-ring`
- `--x-color-danger`, `--x-color-success`, `--x-color-warning`

### Typography
- `--x-font-family`, `--x-font-family-mono`
- `--x-font-size-xs`, `--x-font-size-sm`, `--x-font-size-base`, `--x-font-size-lg`
- `--x-font-weight-normal`, `--x-font-weight-medium`, `--x-font-weight-semibold`
- `--x-line-height-normal`

### Spacing
- `--x-space-xs`, `--x-space-sm`, `--x-space-md`, `--x-space-lg`, `--x-space-xl`

### Shape
- `--x-radius-sm`, `--x-radius-md`, `--x-radius-lg`, `--x-radius-full`
- `--x-border-width`

### Shadows
- `--x-shadow-sm`, `--x-shadow-md`, `--x-shadow-lg`

### Z-index
- `--x-z-dropdown` (1000)
- `--x-z-modal` (1100)
- `--x-z-toast` (1200)

### Opacity
- `--x-opacity-disabled` — for disabled states
- `--x-opacity-placeholder` — for placeholder/muted content

### Motion
- `--x-transition-duration`
- `--x-transition-easing`

## Surface vs Background rules

- **Overlays** (modals, drawers, menus, popovers, toasts, dropdowns, command palette): use `--x-color-bg` (always opaque), never `--x-color-surface` (can be semi-transparent in Aurora theme)
- **Inline surfaces** (cards, fieldsets, collapse panels): use `--x-color-surface`

## What NOT to theme

- Decorative palette colours (e.g. x-liquid-fill gold)
- White-on-coloured-button foreground
- rgba variant tints (info/success/warning/error overlays)

## Demo page rules

- Include `<script src="demo-theme.js" defer></script>` in every demo page
- Use `var(--page-bg)`, `var(--surface-bg)`, `var(--input-bg)` etc. from `demo-responsive.css` — never hardcode colours
- If a demo defines its own `:root` variables, add an `x-theme { ... }` block that re-maps them to theme tokens (`:root` resolves before `<x-theme>` in the DOM tree)
