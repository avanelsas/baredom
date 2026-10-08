# Styling BareDOM components

This guide is for an application developer. It says where to put a value so that it works.

## The three levels

| Level | What it changes |
|---|---|
| A theme token, such as `--x-color-primary` | Every component that follows that token |
| A component's own property, such as `--x-button-radius` | One kind of component, or one instance |
| A shadow part, such as `x-button::part(label)` | One inner element |

Start at the top. Go one level down only when the level above cannot do it.

## Where a value works

`x-theme` declares every token on itself, and a component declares its own properties on itself.
A value from an ancestor loses against that declaration. A value on the element itself wins.

| Where you set the value | What you set | Works |
|---|---|---|
| The `x-theme` element | A theme token | Yes |
| A wrapper inside `x-theme` | A theme token | Yes, for its subtree |
| `:root`, on a page with `x-theme` | A theme token | No |
| `:root` or a wrapper, on a page without `x-theme` | A theme token | Yes |
| The component, by tag, class or id | Its own property | Yes |
| `:root` or a wrapper around the component | Its own property | No |

## Theme tokens

Wrap the page in `x-theme` and pick a preset. [`x-theme.md`](x-theme.md) lists the presets and
every token, and shows how to register a preset of your own.

```html
<x-theme preset="ocean">
  <!-- your application -->
</x-theme>
```

Change a token on the `x-theme` element, or on a wrapper inside it for one part of the page:

```css
x-theme   { --x-color-primary: #e11d48; }
.checkout { --x-color-primary: #16a34a; }
```

Without `x-theme`, each component uses its own defaults and text takes the font of your page.
Set a token on `:root`:

```css
:root {
  --x-color-primary: #e11d48;
  --x-radius-md: 4px;
}
```

### What follows a token by itself

- **The shades of a status colour.** The hover and active shades of danger, success and warning
  are mixed from the colour. Set `--x-color-danger` on the `x-theme` element and both shades
  follow. Set it on a wrapper, and the shades stay those of the theme.
- **Tints.** A soft background in the primary colour or in a status colour is mixed from that
  colour.
- **Hover fills on a background the component does not paint**, such as a table row or a tab.
  They are mixed from the text colour, so they contrast in every theme.

The primary, secondary and tertiary colours are not mixed. Each has a `-hover` and an `-active`
token of its own. Set all three.

### What does not follow the theme

Spacing. Most components keep their own padding and gaps, so a `--x-space-*` token changes
little. Use the component's own properties.

A few other values keep their own shape, size or speed on purpose, such as the ring of a radio
button and the speed of a spinner. [`TOKEN-COVERAGE.md`](TOKEN-COVERAGE.md) lists each one with
its reason, and counts for each component how many values follow the theme.

### Light and dark

Every component and every preset follows `prefers-color-scheme`. A value that you set applies in
both modes. For a value of its own in dark mode, use a media query:

```css
x-theme { --x-color-primary: #e11d48; }

@media (prefers-color-scheme: dark) {
  x-theme { --x-color-primary: #fb7185; }
}
```

## One component

Each component lists its own properties in its document. Set them on the component:

```css
/* every button */
x-button {
  --x-button-radius: 4px;
}

/* one button */
.brand-button {
  --x-button-bg: #2563eb;
  --x-button-bg-hover: #1d4ed8;
}
```

## An inner element

When no property covers what you need, style a part. Each component lists its parts in its
document.

```css
x-button::part(label) {
  letter-spacing: 0.01em;
}
```

## What you cannot do

- **Force light or dark.** Nothing switches the mode for a page or a subtree. The mode is that
  of the system.
- **Reach an inner element that is not a part.**

## Recipes

Each recipe is for a page with `x-theme`. Without it, write `:root` in place of `x-theme`.

**Brand colour.**

```css
x-theme {
  --x-color-primary: #e11d48;
  --x-color-primary-hover: #be123c;
  --x-color-primary-active: #9f1239;
  --x-color-focus-ring: #fb7185;
}
```

**Square corners.** Leave `--x-radius-full` as it is. Pills and round controls use it.

```css
x-theme {
  --x-radius-sm: 0;
  --x-radius-md: 0;
  --x-radius-lg: 0;
  --x-radius-xl: 0;
}
```

**Thicker borders.** Edges, inputs and divider lines follow.

```css
x-theme {
  --x-border-width: 2px;
}
```

**Toasts above your own header.** The theme has three layers: `--x-z-dropdown` (1000),
`--x-z-modal` (1100) and `--x-z-toast` (9000). Keep your header below 1000, or lift the toasts:

```css
x-theme {
  --x-z-toast: 20000;
}
```
