# x-typography

A themeable text styling wrapper that applies consistent typographic styles via a `variant` attribute. Supports headings, body text, captions, code, keyboard shortcuts, blockquotes, and more. Includes text alignment, single-line truncation, multi-line clamping, and gradient text.

---

## Tag

```html
<x-typography></x-typography>
```

---

## Attributes

| Attribute    | Type            | Default   | Description                                              |
|-------------|-----------------|-----------|----------------------------------------------------------|
| `variant`   | enum            | `"body1"` | Typographic variant (see Variants below)                 |
| `align`     | enum            | `"left"`  | Text alignment: `left` `center` `right` `justify`        |
| `truncate`  | boolean         | `false`   | Single-line ellipsis truncation                          |
| `line-clamp`| number          | —         | Multi-line truncation (positive integer, number of lines) |
| `gradient`  | boolean         | `false`   | Fills the text with `--x-typography-gradient` (see Gradient text below) |

Attribute values are case-insensitive (e.g. `variant="H1"` normalises to `h1`).

When both `truncate` and `line-clamp` are set, `truncate` takes precedence (single-line).

Invalid `line-clamp` values (zero, negative, non-numeric, floats) are ignored — the attribute is treated as absent.

---

## Variants

| Variant      | Size       | Weight | Line Height | Letter Spacing | Notes |
|-------------|-----------|--------|-------------|----------------|-------|
| `h1`        | 2.5rem    | 700    | 1.2         | -0.02em        | |
| `h2`        | 2rem      | 700    | 1.25        | -0.015em       | |
| `h3`        | 1.75rem   | 600    | 1.3         | -0.01em        | |
| `h4`        | 1.5rem    | 600    | 1.35        | -0.005em       | |
| `h5`        | 1.25rem   | 600    | 1.4         | normal         | |
| `h6`        | 1.125rem  | 600    | 1.4         | normal         | |
| `subtitle1` | 1.125rem  | 500    | 1.4         | 0.005em        | |
| `subtitle2` | 0.875rem  | 500    | 1.4         | 0.01em         | |
| `body1`     | 1rem      | 400    | 1.5         | normal         | Default |
| `body2`     | 0.875rem  | 400    | 1.5         | normal         | |
| `caption`   | 0.75rem   | 400    | 1.4         | 0.02em         | |
| `overline`  | 0.625rem  | 600    | 1.5         | 0.1em          | Uppercase |
| `small`     | 0.8125rem | 400    | 1.4         | normal         | |
| `blockquote`| 1.125rem  | 400    | 1.6         | normal         | Italic, left border |
| `code`      | 0.875rem  | 400    | 1.5         | —              | Monospace, background |
| `kbd`       | 0.875rem  | 400    | 1.5         | —              | Monospace, background, border |

---

## Properties

| Property    | Type           | Reflects attribute | Default  |
|-------------|----------------|--------------------|----------|
| `variant`   | string         | `variant`          | `"body1"`|
| `align`     | string         | `align`            | `"left"` |
| `truncate`  | boolean        | `truncate`         | `false`  |
| `lineClamp` | number \| null | `line-clamp`       | `null`   |
| `gradient`  | boolean        | `gradient`         | `false`  |

Note: `lineClamp` (camelCase) maps to the `line-clamp` (kebab-case) HTML attribute. Reading a property before its attribute is set returns the default listed above.

---

## Events

None.

---

## Slots

| Slot      | Description                              |
|-----------|------------------------------------------|
| (default) | Text content or child elements to style  |

---

## Parts

| Part        | Description                                           |
|-------------|-------------------------------------------------------|
| `container` | Inner `<div>` wrapper that receives typographic styles |
| `text`      | Inline `<span>` around the text. It holds the gradient under `gradient` |

---

## CSS Custom Properties

### Typography

| Variable | Default | Description |
|----------|---------|-------------|
| `--x-typography-font-family` | `var(--x-font-family, inherit)` | Base font family. The theme's font, or else the font of the page |
| `--x-typography-mono-font-family` | `var(--x-font-family-mono, ui-monospace, "SFMono-Regular", ...)` | Monospace font for code/kbd |
| `--x-typography-color` | `var(--x-color-text,inherit)` | Text color |

### Gradient

| Variable | Default (light) | Default (dark) | Description |
|----------|-----------------|----------------|-------------|
| `--x-typography-gradient` | `linear-gradient(90deg,var(--x-color-primary,#2563eb),var(--x-color-secondary,#64748b),var(--x-color-tertiary,#94a3b8))` | `linear-gradient(90deg,var(--x-color-primary,#3b82f6),var(--x-color-secondary,#94a3b8),var(--x-color-tertiary,#64748b))` | Any CSS image. Fills the text under `gradient` |

### Code variant

| Variable | Light default | Dark default | Description |
|----------|--------------|--------------|-------------|
| `--x-typography-code-bg` | `rgba(0,0,0,0.06)` | `rgba(255,255,255,0.1)` | Background |
| `--x-typography-code-radius` | `var(--x-radius-sm,4px)` | — | Border radius |
| `--x-typography-code-padding` | `0.15em 0.35em` | — | Padding |

### Kbd variant

| Variable | Light default | Dark default | Description |
|----------|--------------|--------------|-------------|
| `--x-typography-kbd-bg` | `rgba(0,0,0,0.06)` | `rgba(255,255,255,0.1)` | Background |
| `--x-typography-kbd-border` | `rgba(0,0,0,0.15)` | `rgba(255,255,255,0.2)` | Border color |
| `--x-typography-kbd-radius` | `var(--x-radius-sm,4px)` | — | Border radius |
| `--x-typography-kbd-padding` | `0.15em 0.4em` | — | Padding |

### Blockquote variant

| Variable | Light default | Dark default | Description |
|----------|--------------|--------------|-------------|
| `--x-typography-blockquote-border-color` | `rgba(0,0,0,0.2)` | `rgba(255,255,255,0.25)` | Left border |
| `--x-typography-blockquote-padding` | `0 0 0 1em` | — | Padding |

---

## Accessibility

This component is purely presentational. It does not add ARIA roles, landmarks, or keyboard behavior. For semantic document structure, use native HTML heading elements (`<h1>`–`<h6>`), `<p>`, `<blockquote>`, `<code>`, etc. alongside or instead of this component.

Gradient text:

- Under `forced-colors: active` the gradient is removed and the text takes a solid colour, so it stays visible.
- A browser without `background-clip: text` shows the text in its normal colour.
- Selected text turns solid on a tint of `--x-color-primary`, so a selection stays readable. A
  slotted element gets this in browsers that pass selection styles down, such as Chrome 134 and
  later.
- The contrast of a gradient changes along the text. Check the contrast of each colour stop against the background.

---

## Examples

### Basic variants

```html
<x-typography variant="h1">Main Heading</x-typography>
<x-typography variant="body1">Body text paragraph.</x-typography>
<x-typography variant="caption">A small caption.</x-typography>
<x-typography variant="overline">Overline label</x-typography>
```

### Alignment

```html
<x-typography variant="body1" align="center">Centred text</x-typography>
<x-typography variant="body1" align="right">Right-aligned</x-typography>
```

### Truncation

```html
<x-typography variant="body1" truncate>
  This very long text will be truncated with an ellipsis on a single line.
</x-typography>
```

### Line clamping

```html
<x-typography variant="body1" line-clamp="3">
  This multi-line text will be clamped to three visible lines.
  Any overflow beyond the third line is hidden with an ellipsis.
</x-typography>
```

### Code and Kbd

```html
<x-typography variant="code">const x = 42;</x-typography>
<x-typography variant="kbd">Ctrl + C</x-typography>
```

### Blockquote

```html
<x-typography variant="blockquote">
  The only way to do great work is to love what you do.
</x-typography>
```

### Gradient text

`gradient` fills the text with a gradient. The gradient spans the text, not the width of the
element, and runs on across wrapped lines.

With no other setting, the gradient follows the theme: it runs from `--x-color-primary` to
`--x-color-secondary` and `--x-color-tertiary`. A change of `x-theme` preset changes it.

```html
<x-theme preset="sunset">
  <x-typography variant="h1" gradient>Gradient heading</x-typography>
</x-theme>
```

Set `--x-typography-gradient` to use your own colours. It wins over the theme.

```css
x-typography.brand {
  --x-typography-gradient: linear-gradient(90deg, #e2477e, #ff8a65, #ffd54f, #a5f3f5);
}
```

- A slotted element with its own `color` keeps that colour.
- A slotted block (`p`, `div`, `h1` to `h6`, `ul`, `ol`, `li`, `blockquote`) gets the gradient on
  its own text. It spans the width of the block.
- The `code` and `kbd` variants keep their background.
- Under `truncate` and `line-clamp`, the gradient spans the whole text, so the hidden part takes
  the last colours. The ellipsis takes the solid text colour.

The `text` part holds the gradient, so it can move. Set it with longhand properties: a
`background` shorthand on `::part(text)` resets the clip to the text.

```css
x-typography.shimmer::part(text) {
  background-size: 200% 100%;
  animation: shimmer 4s linear infinite;
}
@keyframes shimmer { to { background-position: -200% 0; } }
@media (prefers-reduced-motion: reduce) {
  x-typography.shimmer::part(text) { animation: none; }
}
```

### Programmatic (JavaScript)

```js
const el = document.createElement('x-typography');
el.variant = 'h2';
el.align = 'center';
el.textContent = 'Dynamic heading';
document.body.appendChild(el);
```

### ClojureScript / hiccup

```clojure
[:x-typography {:variant "h1"} "Page Title"]
[:x-typography {:variant "body1" :truncate true} "Long text..."]
[:x-typography {:variant "body1" :line-clamp "3"} "Multi-line clamped text..."]
[:x-typography {:variant "code"} "(defn hello [] \"world\")"]
[:x-typography {:variant "h1" :gradient true} "Gradient heading"]
```
