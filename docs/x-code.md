# x-code

A code-display Web Component: it reads source code from its own light-DOM text
content, tokenizes it with a built-in syntax highlighter, and renders it as a
themable code block with optional line numbers, soft-wrapping, a header bar with
a copy button, and a collapsible long-snippet mode.

The component is **stateless** — `DOM = f(attributes, properties)`. The
expand/collapse state lives in the `expanded` attribute, never in a mutable
field.

## Tag

```html
<x-code language="js">
const greeting = "hello";
console.log(greeting);
</x-code>
```

The code is the element's text content. Leading/trailing blank lines are
dropped and shared indentation is stripped, so code can be indented to match the
surrounding markup.

> **Literal HTML in the content** is parsed by the browser before x-code ever
> sees it. To show markup, escape it (`&lt;div&gt;`) or set the `code`
> property/attribute instead.

## Attributes

| Name           | Type    | Default | Description                                                            |
|----------------|---------|---------|------------------------------------------------------------------------|
| `language`     | string  | `text`  | `js`, `json`, `css`, `html` (plus aliases). Anything else = no tokens. |
| `filename`     | string  | `""`    | Shown on the left of the header bar.                                   |
| `show-copy`    | boolean | absent  | Shows a copy-to-clipboard button in the header.                        |
| `line-numbers` | boolean | absent  | Renders a non-selectable line-number gutter.                           |
| `wrap`         | boolean | absent  | Soft-wraps long lines instead of scrolling horizontally.               |
| `max-lines`    | number  | `0`     | When positive and exceeded, the block collapses behind a fade. `0` = off. |
| `expanded`     | boolean | absent  | Whether a collapsible block is expanded. Toggled by the expander.      |
| `code`         | string  | —       | Code source as an attribute. Overridden by the `code` property; overrides text content. |

## Properties

| Property      | Type    | Reflects       | Default | Description                                              |
|---------------|---------|----------------|---------|----------------------------------------------------------|
| `language`    | string  | `language`     | `""`    | —                                                        |
| `filename`    | string  | `filename`     | `""`    | —                                                        |
| `showCopy`    | boolean | `show-copy`    | `false` | —                                                        |
| `lineNumbers` | boolean | `line-numbers` | `false` | —                                                        |
| `wrap`        | boolean | `wrap`         | `false` | —                                                        |
| `maxLines`    | number  | `max-lines`    | `0`     | —                                                        |
| `expanded`    | boolean | `expanded`     | `false` | —                                                        |
| `code`        | string  | — (none)       | —       | Property-only override. Getter reports the effective code (property → `code` attribute → text content). Setting it re-renders. |

## Code source precedence

The rendered code is resolved in this order:

1. The `code` **property** (if set to a string).
2. The `code` **attribute**.
3. The element's light-DOM **`textContent`**.

A `MutationObserver` re-renders the block whenever the light-DOM text changes.

## Events

| Event           | Cancelable | Detail                 | When it fires                                          |
|-----------------|------------|------------------------|--------------------------------------------------------|
| `x-code-copy`   | no         | `{ code: string }`     | The composed copy button reported a successful copy.   |
| `x-code-toggle` | no         | `{ expanded: boolean }`| The collapse/expand state changed via the expander.    |

The inner `x-copy` still emits its own `x-copy-*` events (they are `composed`
and bubble through); listen for the `x-code-*` events for a stable API.

## Methods

| Method       | Returns | Description                              |
|--------------|---------|------------------------------------------|
| `expand()`   | void    | Sets the `expanded` attribute.           |
| `collapse()` | void    | Removes the `expanded` attribute.        |

## Parts

| Part           | Description                                              |
|----------------|----------------------------------------------------------|
| `container`    | Outer bordered box.                                      |
| `header`       | Header bar — present only when `filename` is set or `show-copy` is on. |
| `filename`     | Filename text in the header.                             |
| `language`     | Language badge — appears in the header when `show-copy` is on and no `filename` is given. |
| `copy-wrap`    | Wrapper around the copy button.                          |
| `copy`         | The composed `x-copy` element. Its inner button is reachable via `[part=copy]::part(trigger)`. |
| `body`         | Positioning context for the collapse fade.               |
| `scroll`       | Scrollable, focusable code region.                       |
| `pre` / `code` | The `<pre>` / `<code>` elements.                         |
| `line`         | One rendered line (gutter + content).                    |
| `gutter`       | Per-line line-number cell.                               |
| `line-content` | Per-line tokenized content.                              |
| `fade`         | Gradient overlay shown while collapsed.                  |
| `expander`     | "Show more" / "Show less" button.                        |

Token spans inside `line-content` carry classes `tok-comment`, `tok-string`,
`tok-keyword`, `tok-number`, `tok-punct`, `tok-tag`, etc.

## CSS custom properties

| Variable                    | Default                          | Description                  |
|-----------------------------|----------------------------------|------------------------------|
| `--x-code-bg`               | `var(--x-color-surface, …)`      | Block background.            |
| `--x-code-fg`               | `var(--x-color-text, …)`         | Default text colour.         |
| `--x-code-border`           | `var(--x-color-border, …)`       | Border colour.               |
| `--x-code-muted`            | `var(--x-color-text-muted, …)`   | Gutter / badge colour.       |
| `--x-code-radius`           | `var(--x-radius-md, 8px)`        | Corner radius.               |
| `--x-code-font`             | `var(--x-font-family-mono, …)`   | Monospace font stack.        |
| `--x-code-font-size`        | `0.8125rem`                      | Code font size.              |
| `--x-code-line-height`      | `1.6`                            | Code line height.            |
| `--x-code-tab-size`         | `2`                              | Tab width.                   |
| `--x-code-token-comment`    | grey                             | Comment colour.              |
| `--x-code-token-string`     | green                            | String / template colour.    |
| `--x-code-token-keyword`    | red                              | Keyword / at-rule colour.    |
| `--x-code-token-number`     | blue                             | Number colour.               |
| `--x-code-token-punct`      | muted                            | Punctuation colour.          |
| `--x-code-token-tag`        | green                            | HTML tag colour.             |
| `--x-code-token-property`   | blue                             | CSS property / JSON key.     |
| `--x-code-token-attr-value` | dark blue                        | HTML attribute value.        |
| `--x-code-token-entity`     | blue                             | HTML entity colour.          |

All token colours have separate light and dark fallbacks via
`@media (prefers-color-scheme: dark)` and can be overridden per instance.

## Tokenizer & languages

The tokenizer is in-house, regex-based, and intentionally lightweight. It
supports `js`, `json`, `css`, and `html`; every other language renders as plain
monospace text. Known limitations:

- It is a highlighter, not a parser — there is no nested-language handling
  (`<script>` / `<style>` bodies inside HTML stay plain HTML text).
- JavaScript regex literals are not recognised; `/` is treated as punctuation.
- The CSS property heuristic (an identifier followed by `:`) can occasionally
  mis-colour a pseudo-class selector.

These trade-offs keep the highlighter small and dependency-free.

## Accessibility

- The scrollable region is a focusable `role="region"` with an `aria-label`
  (the filename, the language, or "Code").
- Line-number cells are `aria-hidden` and not selectable, so copying or
  selecting code never picks up the numbers.
- The expander is a real `<button>` carrying `aria-expanded`.
- The copy button is provided by `x-copy`, which is itself a `<button>`.
- Animations honour `prefers-reduced-motion`.

## Examples

### Line numbers + copy button

```html
<x-code language="js" filename="greet.js" line-numbers show-copy>
function greet(name) {
  return `Hello, ${name}!`;
}
</x-code>
```

### Collapsible long snippet

```html
<x-code language="css" max-lines="6">
/* …a long stylesheet… */
</x-code>
```

### Soft-wrapping

```html
<x-code language="json" wrap>
{ "a very": "long single line that should fold instead of scrolling" }
</x-code>
```

### Setting code programmatically

```js
const el = document.querySelector('x-code');
el.language = 'js';
el.code = 'const x = 1;';
```
