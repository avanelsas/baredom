# x-divider

A themeable, accessible divider that separates content sections. Supports horizontal and vertical orientations, solid/dashed/dotted line styles, an optional text label with configurable alignment, and full CSS custom-property theming.

---

## Tag

```html
<x-divider></x-divider>
```

---

## Attributes

| Attribute     | Type    | Default        | Description                                                                 |
|---------------|---------|----------------|-----------------------------------------------------------------------------|
| `orientation` | enum    | `"horizontal"` | Layout axis: `horizontal` or `vertical`                                     |
| `variant`     | enum    | `"solid"`      | Line style: `solid` \| `dashed` \| `dotted`                                |
| `thickness`   | string  | —              | CSS length for line thickness (e.g. `"2px"`). Sets `--x-divider-thickness` |
| `color`       | string  | —              | CSS color for the line. Sets `--x-divider-color`                            |
| `inset`       | string  | —              | CSS length for inset on both ends. Sets `--x-divider-inset`                 |
| `length`      | string  | —              | CSS size constraining the divider's main axis. Sets `--x-divider-length`    |
| `label`       | string  | —              | Text label rendered in the centre of the line                               |
| `align`       | enum    | `"center"`     | Label position along the line: `center` \| `start` \| `end`                |
| `role`        | string  | `"separator"`  | ARIA role on the host: `separator` \| `presentation` \| `none`             |
| `aria-label`  | string  | —              | Accessible label when `role="separator"`                                    |

---

## Properties

| Property      | Type   | Reflects attribute |
|---------------|--------|--------------------|
| `orientation` | string | `orientation`      |
| `variant`     | string | `variant`          |
| `align`       | string | `align`            |
| `label`       | string | `label`            |
| `thickness`   | string | `thickness`        |
| `color`       | string | `color`            |
| `inset`       | string | `inset`            |
| `length`      | string | `length`           |

---

## Slots

| Slot    | Description                                                            |
|---------|------------------------------------------------------------------------|
| `label` | Alternative to the `label` attribute — place inline content inside the label area |

---

## Parts

### Without label

| Part        | Description                   |
|-------------|-------------------------------|
| `container` | Outer flex wrapper            |
| `line`      | The single divider line       |

### With label

| Part         | Description                                        |
|--------------|----------------------------------------------------|
| `container`  | Outer flex wrapper                                 |
| `line-left`  | Left/top segment of the line                       |
| `label`      | Wrapper element around the label content           |
| `label-text` | `<span>` inside `[part=label]` holding label text  |
| `line-right` | Right/bottom segment of the line                   |

---

## CSS Custom Properties

| Variable                | Default                          | Description                          |
|-------------------------|----------------------------------|--------------------------------------|
| `--x-divider-color`     | `rgba(0,0,0,0.12)` / dark-mode adjusted | Line color                  |
| `--x-divider-thickness` | `1px`                            | Line thickness                       |
| `--x-divider-inset`     | `0px`                            | Inset applied to both ends of the line |
| `--x-divider-length`    | `auto`                           | Constrains the main-axis size        |

Dark-mode defaults are set automatically via `@media (prefers-color-scheme: dark)`.

---

## DOM Behaviour

When no `label` attribute is set, the shadow DOM is:

```
div[part=container]
  └── div[part=line]
```

When a `label` attribute is present:

```
div[part=container]
  ├── div[part=line-left]
  ├── span[part=label]
  │     ├── slot[name=label]
  │     └── span[part=label-text]
  └── div[part=line-right]
```

`data-orientation`, `data-variant`, and `data-align` data attributes are always set on the host element and can be used as CSS hooks.

---

## Accessibility

- `role="separator"` (default) marks the element as a thematic separator. Pair with `aria-label` when context is needed.
- Set `role="presentation"` or `role="none"` for purely decorative dividers so assistive technology ignores them.
- When `orientation="vertical"`, `aria-orientation="vertical"` is set on the host.

---

## Examples

### Basic horizontal

```html
<x-divider></x-divider>
```

### Vertical in a flex row

```html
<div style="display:flex; height:40px; align-items:stretch">
  <span>Left</span>
  <x-divider orientation="vertical"></x-divider>
  <span>Right</span>
</div>
```

### Dashed with custom colour and thickness

```html
<x-divider variant="dashed" color="#3b82f6" thickness="2px"></x-divider>
```

### Labelled, aligned to start

```html
<x-divider label="Section A" align="start"></x-divider>
```

### Decorative (no ARIA role)

```html
<x-divider role="presentation"></x-divider>
```

### ClojureScript (hiccup)

```clojure
[:x-divider]

[:x-divider {:orientation "vertical"}]

[:x-divider {:variant "dashed" :color "#3b82f6" :thickness "2px"}]

[:x-divider {:label "Section A" :align "start"}]

[:x-divider {:role "presentation"}]
```
