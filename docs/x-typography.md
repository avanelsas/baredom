# x-typography

A themeable text styling wrapper that applies consistent typographic styles via a `variant` attribute. Supports headings, body text, captions, code, keyboard shortcuts, blockquotes, and more. Includes text alignment, single-line truncation, and multi-line clamping.

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

---

## CSS Custom Properties

### Typography

| Variable | Default | Description |
|----------|---------|-------------|
| `--x-typography-font-family` | `system-ui, -apple-system, sans-serif` | Base font family |
| `--x-typography-mono-font-family` | `ui-monospace, "SFMono-Regular", ...` | Monospace font for code/kbd |
| `--x-typography-color` | `inherit` | Text color |

### Code variant

| Variable | Light default | Dark default | Description |
|----------|--------------|--------------|-------------|
| `--x-typography-code-bg` | `rgba(0,0,0,0.06)` | `rgba(255,255,255,0.1)` | Background |
| `--x-typography-code-radius` | `4px` | — | Border radius |
| `--x-typography-code-padding` | `0.15em 0.35em` | — | Padding |

### Kbd variant

| Variable | Light default | Dark default | Description |
|----------|--------------|--------------|-------------|
| `--x-typography-kbd-bg` | `rgba(0,0,0,0.06)` | `rgba(255,255,255,0.1)` | Background |
| `--x-typography-kbd-border` | `rgba(0,0,0,0.15)` | `rgba(255,255,255,0.2)` | Border color |
| `--x-typography-kbd-radius` | `4px` | — | Border radius |
| `--x-typography-kbd-padding` | `0.15em 0.4em` | — | Padding |

### Blockquote variant

| Variable | Light default | Dark default | Description |
|----------|--------------|--------------|-------------|
| `--x-typography-blockquote-border-color` | `rgba(0,0,0,0.2)` | `rgba(255,255,255,0.25)` | Left border |
| `--x-typography-blockquote-padding` | `0 0 0 1em` | — | Padding |

---

## Accessibility

This component is purely presentational. It does not add ARIA roles, landmarks, or keyboard behavior. For semantic document structure, use native HTML heading elements (`<h1>`–`<h6>`), `<p>`, `<blockquote>`, `<code>`, etc. alongside or instead of this component.

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
```
