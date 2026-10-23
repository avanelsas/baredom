# x-toc

A table of contents. It builds a list of links from the headings of a content container, marks
the heading the reader is at, and scrolls to a heading on click.

---

## Tag name

```html
<x-toc for="post" label="On this page" sticky></x-toc>

<article id="post">
  <h2>What is a web component</h2> ...
  <h2>What do you get for it?</h2> ...
</article>
```

---

## Observed Attributes

| Attribute     | Type    | Default    | Notes |
|---------------|---------|------------|-------|
| `for`         | string  | (none)     | The id of the content container, in the same root as `x-toc`. Without it, or with an unknown id, the list is empty. |
| `selector`    | string  | `"h2, h3"` | The CSS selector for the entries, run inside the container. An invalid selector gives an empty list. |
| `label`       | string  | (none)     | The visible title. It also names the `<nav>`. |
| `offset`      | string  | `"25%"`    | The activation line, from the top of the scroll root. A number with `px` or `%`. A bare number is px. An invalid value gives the default. |
| `sticky`      | boolean | `false`    | The host sticks at `--x-toc-sticky-top` while the page scrolls. |
| `update-hash` | boolean | `false`    | A click replaces the URL hash. It adds no history entry. |

---

## Properties

Each attribute has a property that reflects it: `for`, `selector`, `label`, `offset`, `sticky`
and `updateHash`. One property is read-only:

| Property   | Type           | Notes |
|------------|----------------|-------|
| `activeId` | string \| null | The id of the active entry. `null` while the list is empty or the element is not in the page. |

---

## Events

| Event            | Detail | Cancelable | Fires when |
|------------------|--------|------------|------------|
| `x-toc-change`   | `{ id, text, level, index, previousId }` | no  | The active entry moves from one entry to another, by scrolling or by a click. |
| `x-toc-navigate` | `{ id, text, level, index }`             | yes | The reader activates an entry with a plain click or Enter. |

Both events bubble and are composed. When `x-toc-navigate` is canceled, x-toc does not scroll,
does not change the active entry and does not change the hash.

A click with Ctrl, Cmd, Shift or Alt, or a middle click, is left to the browser. It fires no
event.

```js
toc.addEventListener('x-toc-change', e => {
  console.log(e.detail.previousId, '→', e.detail.id);
});

toc.addEventListener('x-toc-navigate', e => {
  e.preventDefault();
  router.go(`#${e.detail.id}`);
});
```

---

## Entries

- Each element in the container that matches `selector` is an entry, in document order.
- The text of an entry is the text of the element, trimmed, with its whitespace collapsed. An
  element with empty text is left out and gets no id.
- An entry without an id gets one from its text: lower case, letters and digits joined by `-`.
  An id in use gets `-2`, `-3` and so on. x-toc never changes or removes an id.
- An entry whose id an earlier entry has is left out.
- The level of an entry counts from the shallowest heading that matches. With `selector="h2, h3"`,
  an `h2` is level 1 and an `h3` is level 2. An element that is not a heading is level 1.
- The list follows changes in the container: added or removed headings, changed text, changed ids.
- A container that appears after `x-toc`, or that replaces the one it reads, is found when it
  appears.
- Headings inside the shadow root of an element in the container are not found.

## Active entry

The active entry is the last entry whose top is at or above the activation line. Before the
first entry reaches the line, the first entry is active. At the end of the scroll root, the last
entry is active.

The scroll root is the nearest ancestor of the container that scrolls, or the viewport.

After a click, the clicked entry stays active until the scroll it started ends and the reader
scrolls again.

## Sticky

With `sticky`, the host has `position: sticky` and `align-self: start`. A list taller than the
space left scrolls inside the host, and keeps the active entry in view.

Sticky works only when no ancestor between `x-toc` and the scroll root clips its overflow, and
when the parent of `x-toc` is taller than `x-toc`. In a grid, put `x-toc` in a column that
stretches to the height of the article:

```html
<div style="display:grid; grid-template-columns: 1fr 16rem; gap: 2rem">
  <article id="post">…</article>
  <aside>
    <x-toc for="post" label="On this page" sticky></x-toc>
  </aside>
</div>
```

---

## CSS Custom Properties

| Property | Default (light) | Default (dark) | Notes |
|----------|-----------------|----------------|-------|
| `--x-toc-font-family` | `var(--x-font-family,inherit)` | | Font of the entries |
| `--x-toc-font-size` | `var(--x-font-size-sm,0.875rem)` | | Size of the entries |
| `--x-toc-color` | `var(--x-color-text,#1f2937)` | `var(--x-color-text,#e5e7eb)` | Entry text |
| `--x-toc-hover-color` | `var(--x-color-primary-hover,#1d4ed8)` | `var(--x-color-primary-hover,#93c5fd)` | Entry text on hover |
| `--x-toc-active-color` | `var(--x-color-primary,#2563eb)` | `var(--x-color-primary,#60a5fa)` | Active entry text |
| `--x-toc-label-color` | `var(--x-color-text-muted,#6b7280)` | `var(--x-color-text-muted,#9ca3af)` | Title text |
| `--x-toc-label-font-family` | `var(--x-font-family-mono,ui-monospace,monospace)` | | Title font |
| `--x-toc-label-font-size` | `var(--x-font-size-xs,0.75rem)` | | Title size |
| `--x-toc-label-letter-spacing` | `0.12em` | | Title tracking |
| `--x-toc-label-transform` | `uppercase` | | Title case |
| `--x-toc-rail-color` | `var(--x-color-border,#e5e7eb)` | `var(--x-color-border,#374151)` | The vertical rail |
| `--x-toc-rail-width` | `1px` | | Rail width |
| `--x-toc-indicator-color` | `var(--x-toc-active-color)` | | The bar beside the active entry |
| `--x-toc-indicator-width` | `3px` | | Bar width |
| `--x-toc-indicator-radius` | `var(--x-radius-full,9999px)` | | Bar corners |
| `--x-toc-gap` | `var(--x-space-sm,0.5rem)` | | Space between entries |
| `--x-toc-indent` | `0.75rem` | | Indent per level after the first. `0` gives a flat list. |
| `--x-toc-sticky-top` | `1rem` | | Distance from the top while sticky |
| `--x-toc-transition-duration` | `var(--x-transition-duration,200ms)` | | Bar slide and colour change |

```css
x-toc {
  --x-toc-active-color: #e879a6;
  --x-toc-indent: 0;
  --x-toc-sticky-top: 5rem;
}
```

The host has `data-empty` while the list is empty. Hide an empty table of contents:

```css
x-toc[data-empty] { display: none; }
```

---

## Shadow DOM Parts

| Part        | Element  | Notes |
|-------------|----------|-------|
| `nav`       | `<nav>`  | The landmark. Its start border is the rail. |
| `label`     | `<div>`  | The title. Hidden without `label`. |
| `body`      | `<div>`  | Holds the bar and the list. Scrolls when sticky. |
| `indicator` | `<span>` | The bar beside the active entry. |
| `list`      | `<ol>`   | The entries. |
| `item`      | `<li>`   | One entry. `data-level` gives its level. |
| `link`      | `<a>`    | The link to the heading. The active one has `aria-current="location"`. |

```css
x-toc::part(link) { padding-block: 0.25rem; }
```

---

## Writes on other nodes

| Target      | Attribute         | When |
|-------------|-------------------|------|
| any entry   | `id`              | The entry has no id. |
| any entry   | `tabindex` (`-1`) | On a click, when the heading cannot take focus. |

---

## Accessibility

- A `<nav>` landmark, named by the title, or by `aria-label="Table of contents"` without one.
- An `<ol>` of native links. Tab and Enter work as for any link.
- The active link has `aria-current="location"`. The bar marks it too, so colour is not the only cue.
- After a click, focus moves to the heading, so a screen reader reads on from there.

## Motion

Under `prefers-reduced-motion: reduce` the bar jumps, colours change at once, and a click scrolls
at once.

## Responsive

- Links are at least 44px tall under `(pointer: coarse)`.
- Long entries wrap.
- `sticky` is off by default, so a table of contents above an article on a phone does not cover
  the text.

---

## Usage Examples

### Only the top level

```html
<x-toc for="post" selector="h1"></x-toc>
```

### Below a fixed header

```html
<x-toc for="post" offset="80px" sticky style="--x-toc-sticky-top: 80px"></x-toc>
```
