# x-breadcrumbs

A navigation trail that renders slotted anchor elements as breadcrumb items separated by a configurable glyph. Supports automatic collapse with ellipsis when the item count exceeds `max-items`.

---

## Tag

```html
<x-breadcrumbs></x-breadcrumbs>
```

---

## Attributes

| Attribute               | Type    | Default  | Description                                                          |
|-------------------------|---------|----------|----------------------------------------------------------------------|
| `separator`             | string  | `"/"`    | Separator glyph rendered between items                               |
| `size`                  | enum    | `"md"`   | Font size: `sm` `md` `lg`                                           |
| `variant`               | enum    | `"default"` | Visual style: `default` `subtle` `text`                          |
| `max-items`             | number  | —        | Maximum visible items. Excess is collapsed into `…`.                 |
| `items-before`          | number  | `1`      | Items to keep visible before the ellipsis (requires `max-items`)     |
| `items-after`           | number  | `2`      | Items to keep visible after the ellipsis (requires `max-items`)      |
| `wrap`                  | boolean | `false`  | Allow the trail to wrap onto multiple lines                          |
| `disabled`              | boolean | `false`  | Dims and disables all links                                          |
| `preserve-aria-current` | boolean | `false`  | Preserve any `aria-current` attribute already set on slotted links   |
| `aria-label`            | string  | —        | Accessible label for the `<nav>` landmark (default `"Breadcrumb"`)  |
| `aria-describedby`      | string  | —        | References a describing element                                      |

---

## Properties

| Property       | Type    | Reflects attribute    |
|----------------|---------|-----------------------|
| `separator`    | string  | `separator`           |
| `size`         | string  | `size`                |
| `variant`      | string  | `variant`             |
| `wrap`         | boolean | `wrap`                |
| `maxItems`     | number  | `max-items`           |
| `itemsBefore`  | number  | `items-before`        |
| `itemsAfter`   | number  | `items-after`         |
| `disabled`     | boolean | `disabled`            |

---

## Slots

| Slot      | Description                                     |
|-----------|-------------------------------------------------|
| *(default)* | Anchor (`<a>`) elements, one per breadcrumb item |

The last slotted item is automatically set as `aria-current="page"` unless `preserve-aria-current` is set.

---

## Collapse behaviour

When `max-items` is set and the total item count exceeds it:

1. The first `items-before` items remain visible.
2. A `…` ellipsis is inserted.
3. The last `items-after` items remain visible.

The algorithm clamps both values to avoid overlapping.

---

## Accessibility

- The component wraps its content in a `<nav aria-label="Breadcrumb">` (or the value of `aria-label`).
- The last item receives `aria-current="page"` automatically.
- When `disabled`, all links are dimmed and pointer events are removed.

---

## Examples

### Basic

```html
<x-breadcrumbs>
  <a href="/">Home</a>
  <a href="/products">Products</a>
  <a href="/products/shoes">Shoes</a>
</x-breadcrumbs>
```

### Custom separator

```html
<x-breadcrumbs separator="›">
  <a href="/">Home</a>
  <a href="/docs">Docs</a>
  <a href="/docs/components">Components</a>
</x-breadcrumbs>
```

### Collapsed with ellipsis

```html
<x-breadcrumbs max-items="3" items-before="1" items-after="2">
  <a href="/">Home</a>
  <a href="/docs">Docs</a>
  <a href="/docs/components">Components</a>
  <a href="/docs/components/avatar">Avatar</a>
  <a href="/docs/components/avatar/api">API</a>
</x-breadcrumbs>
```

### Subtle variant

```html
<x-breadcrumbs variant="subtle">
  <a href="/">Home</a>
  <a href="/settings">Settings</a>
  <a href="/settings/profile">Profile</a>
</x-breadcrumbs>
```

### ClojureScript (hiccup renderer)

```clojure
[:x-breadcrumbs {:separator "›"}
 [:a {:href "/"} "Home"]
 [:a {:href "/docs"} "Docs"]
 [:a {:href "/docs/components"} "Components"]]

[:x-breadcrumbs {:max-items "3" :items-before "1" :items-after "2"}
 [:a {:href "/"} "Home"]
 [:a {:href "/docs"} "Docs"]
 [:a {:href "/docs/components"} "Components"]
 [:a {:href "/docs/components/avatar"} "Avatar"]
 [:a {:href "/docs/components/avatar/api"} "API"]]
```
