# x-grid

A layout primitive that renders a CSS Grid container. Drop any content into the default slot; the grid layout is driven entirely by attributes.

---

## Tag

```html
<x-grid></x-grid>
```

---

## Attributes

| Attribute          | Type   | Default                         | Description                                               |
|--------------------|--------|---------------------------------|-----------------------------------------------------------|
| `columns`          | string | —                               | Explicit `grid-template-columns` value. When set, `min-column-size` is ignored. |
| `min-column-size`  | string | `"16rem"`                       | Minimum column width for the auto-fit/minmax template     |
| `gap`              | enum   | `"md"`                          | Uniform gap token: `none` `xs` `sm` `md` `lg` `xl`       |
| `row-gap`          | enum   | *(inherits `gap`)*              | Row-only gap override. Same tokens as `gap`.              |
| `column-gap`       | enum   | *(inherits `gap`)*              | Column-only gap override. Same tokens as `gap`.           |
| `align-items`      | enum   | `"stretch"`                     | `start` `center` `end` `stretch`                         |
| `justify-items`    | enum   | `"stretch"`                     | `start` `center` `end` `stretch`                         |
| `auto-flow`        | enum   | `"row"`                         | `row` `column` `dense` `row-dense` `column-dense`        |
| `inline`           | boolean| `false`                         | Render as `inline-grid` instead of `grid`                |

### Gap token → CSS mapping

| Token  | CSS value |
|--------|-----------|
| `none` | `0`       |
| `xs`   | `4px`     |
| `sm`   | `8px`     |
| `md`   | `16px`    |
| `lg`   | `24px`    |
| `xl`   | `32px`    |

---

## Slots

| Slot      | Description           |
|-----------|-----------------------|
| *(default)* | Grid child elements |

---

## Template logic

When `columns` is absent the grid template is:

```css
grid-template-columns: repeat(auto-fit, minmax(<min-column-size>, 1fr));
```

When `columns` is present it is used verbatim as the `grid-template-columns` value.

---

## Examples

### Auto-fit responsive columns

```html
<x-grid gap="md">
  <x-card>One</x-card>
  <x-card>Two</x-card>
  <x-card>Three</x-card>
</x-grid>
```

### Explicit 3-column layout

```html
<x-grid columns="3" gap="lg">
  <div>A</div>
  <div>B</div>
  <div>C</div>
</x-grid>
```

### Explicit template string

```html
<x-grid columns="1fr 2fr 1fr" gap="sm">
  <div>Sidebar</div>
  <div>Main</div>
  <div>Aside</div>
</x-grid>
```

### Tight stat row

```html
<x-grid columns="4" gap="sm" align-items="start">
  <x-stat label="Users"   value="1 234"></x-stat>
  <x-stat label="Revenue" value="$9 800"></x-stat>
  <x-stat label="Errors"  value="3"></x-stat>
  <x-stat label="Uptime"  value="99.9%"></x-stat>
</x-grid>
```

### ClojureScript (hiccup renderer)

```clojure
[:x-grid {:columns "3" :gap "md"}
 [:x-card "Alpha"]
 [:x-card "Beta"]
 [:x-card "Gamma"]]
```
