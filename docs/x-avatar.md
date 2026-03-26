# x-avatar

A themeable, accessible avatar component. Displays a user photo, derived or explicit initials, or a fallback glyph. Supports shape, size, and variant tokens, a status indicator dot, and a badge slot for overlays.

---

## Tag

```html
<x-avatar></x-avatar>
```

---

## Attributes

| Attribute   | Type             | Default    | Description                                              |
|-------------|------------------|------------|----------------------------------------------------------|
| `src`       | string           | —          | Image URL                                                |
| `alt`       | string           | —          | Image alt text; also used as `aria-label` on the host   |
| `name`      | string           | —          | Person's name; used for initials derivation and `aria-label` when `alt` is absent |
| `initials`  | string           | —          | Explicit initials (1–3 chars). Overrides name-derived initials |
| `size`      | enum             | `"md"`     | Avatar dimension: `xs` `sm` `md` `lg` `xl`             |
| `shape`     | enum             | `"circle"` | Border shape: `circle` `square` `rounded`               |
| `variant`   | enum             | `"neutral"`| Colour variant: `neutral` `brand` `subtle`              |
| `status`    | enum             | —          | Status indicator: `online` `offline` `busy` `away`. Absent = no dot |
| `disabled`  | boolean          | `false`    | Dims the avatar; sets `aria-disabled="true"`            |

---

## Properties

| Property   | Type           | Reflects attribute |
|------------|----------------|-------------------|
| `src`      | string \| null | `src`             |
| `alt`      | string \| null | `alt`             |
| `name`     | string \| null | `name`            |
| `initials` | string \| null | `initials`        |
| `size`     | string         | `size`            |
| `shape`    | string         | `shape`           |
| `variant`  | string         | `variant`         |
| `status`   | string \| null | `status`          |
| `disabled` | boolean        | `disabled`        |

---

## Events

None. `x-avatar` is a display-only component.

---

## Slots

| Slot    | Description                                       |
|---------|---------------------------------------------------|
| `badge` | Overlay element (e.g. notification count, icon). Positioned at top-right corner of the host. |

```html
<x-avatar name="Alice Bob">
  <span slot="badge" class="badge">3</span>
</x-avatar>
```

---

## Parts

| Part       | Description                              |
|------------|------------------------------------------|
| `root`     | Avatar circle/square — `overflow:hidden` |
| `image`    | `<img>` element                          |
| `initials` | Initials `<span>`                        |
| `fallback` | Fallback glyph `<span>` (`?`)           |
| `status`   | Status indicator dot `<span>`            |
| `badge`    | Badge wrapper `<span>`                   |

---

## CSS Custom Properties

### Dimensions

| Variable               | Default | Description                          |
|------------------------|---------|--------------------------------------|
| `--x-avatar-size`      | —       | Override avatar size completely      |
| `--x-avatar-size-xs`   | `20px`  | xs size token                        |
| `--x-avatar-size-sm`   | `24px`  | sm size token                        |
| `--x-avatar-size-md`   | `32px`  | md size token                        |
| `--x-avatar-size-lg`   | `40px`  | lg size token                        |
| `--x-avatar-size-xl`   | `48px`  | xl size token                        |
| `--x-avatar-radius`    | `10px`  | Border radius for `shape="rounded"`  |

### Colour tokens (light mode defaults)

| Variable                       | Default                        |
|--------------------------------|--------------------------------|
| `--x-avatar-bg`                | `rgba(0,0,0,0.06)`            |
| `--x-avatar-border`            | `rgba(0,0,0,0.14)`            |
| `--x-avatar-color`             | `rgba(0,0,0,0.86)`            |
| `--x-avatar-ring`              | `#ffffff`                      |
| `--x-avatar-disabled-opacity`  | `0.55`                         |
| `--x-avatar-status-online`     | `rgba(16,140,72,0.95)`        |
| `--x-avatar-status-offline`    | `rgba(0,0,0,0.45)`            |
| `--x-avatar-status-busy`       | `rgba(190,20,40,0.95)`        |
| `--x-avatar-status-away`       | `rgba(204,120,0,0.95)`        |

Dark-mode variants are set automatically via `@media (prefers-color-scheme: dark)`.

---

## Accessibility

- When `alt` or `name` is present: host gets `role="img"` and `aria-label`.
- When neither is present: host gets `aria-hidden="true"` (decorative).
- When `disabled`: host gets `aria-disabled="true"`.
- The `[part=image]` `alt` attribute is always empty — the host carries the accessible name.

---

## Display mode priority

| Condition                        | Displayed         |
|----------------------------------|-------------------|
| `src` set and image loaded       | Photo             |
| `src` set but load failed        | Initials or `?`   |
| No `src`, `initials` attribute   | Explicit initials |
| No `src`, `name` attribute       | Derived initials  |
| None of the above                | `?` glyph         |

Initials derivation: split `name` on whitespace, take the first character of the first two words, uppercase.

---

## Examples

### Sizes

```html
<x-avatar size="xs" name="Alice Bob"></x-avatar>
<x-avatar size="sm" name="Alice Bob"></x-avatar>
<x-avatar size="md" name="Alice Bob"></x-avatar>
<x-avatar size="lg" name="Alice Bob"></x-avatar>
<x-avatar size="xl" name="Alice Bob"></x-avatar>
```

### Shapes

```html
<x-avatar shape="circle"  name="Alice Bob"></x-avatar>
<x-avatar shape="rounded" name="Alice Bob"></x-avatar>
<x-avatar shape="square"  name="Alice Bob"></x-avatar>
```

### Variants

```html
<x-avatar variant="neutral" name="Alice Bob"></x-avatar>
<x-avatar variant="brand"   name="Alice Bob"></x-avatar>
<x-avatar variant="subtle"  name="Alice Bob"></x-avatar>
```

### Status indicators

```html
<x-avatar name="Alice" status="online"></x-avatar>
<x-avatar name="Bob"   status="busy"></x-avatar>
<x-avatar name="Carol" status="away"></x-avatar>
<x-avatar name="Dave"  status="offline"></x-avatar>
```

### Badge slot

```html
<x-avatar name="Alice Bob">
  <span slot="badge" style="
    background:#ef4444; color:#fff;
    font-size:10px; font-weight:700;
    min-width:16px; height:16px;
    border-radius:999px; padding:0 4px;
    display:inline-flex; align-items:center; justify-content:center;
  ">3</span>
</x-avatar>
```

### Image with fallback

```html
<x-avatar src="https://example.com/photo.jpg"
          alt="Alice's profile photo"
          name="Alice Bob">
</x-avatar>
```

If the image fails to load, initials `AB` are shown.

### ClojureScript (hiccup renderer)

```clojure
[:x-avatar {:name "Alice Bob" :size "lg" :status "online"}]

[:x-avatar {:src     "https://example.com/photo.jpg"
            :alt     "Alice's profile photo"
            :variant "brand"
            :shape   "rounded"}]
```
