# x-badge

A small indicator that displays a count, a text label, or a dot. Supports five semantic colour variants, two sizes, pill shape, and dot-only mode.

---

## Tag

```html
<x-badge></x-badge>
```

---

## Attributes

| Attribute          | Type    | Default     | Description                                                         |
|--------------------|---------|-------------|---------------------------------------------------------------------|
| `variant`          | enum    | `"neutral"` | Colour variant: `neutral` `info` `success` `warning` `error`        |
| `size`             | enum    | `"md"`      | Size: `sm` `md`                                                     |
| `count`            | number  | —           | Numeric value to display. Capped at `max`.                          |
| `max`              | number  | `99`        | Maximum displayed count. Values above show as `"<max>+"`.           |
| `text`             | string  | —           | Short label string (e.g. `"NEW"`, `"BETA"`).                       |
| `dot`              | boolean | `false`     | Dot-only mode — no text shown.                                      |
| `pill`             | boolean | `false`     | Fully rounded pill shape.                                           |
| `aria-label`       | string  | —           | Overrides the accessible label on the host.                         |
| `aria-describedby` | string  | —           | References an element that describes the badge.                     |

---

## Display mode priority

| Condition                     | Mode     | Rendered content          |
|-------------------------------|----------|---------------------------|
| Slotted content present       | `slot`   | Custom slotted content    |
| `count` attribute present     | `count`  | Number (capped at `max`)  |
| `text` attribute present      | `text`   | Text string               |
| `dot` attribute present       | `dot`    | Dot, no text              |
| None of the above             | `empty`  | Hidden                    |

---

## Properties

| Property  | Type    | Reflects attribute |
|-----------|---------|--------------------|
| `variant` | string  | `variant`          |
| `size`    | string  | `size`             |
| `pill`    | boolean | `pill`             |
| `dot`     | boolean | `dot`              |
| `count`   | number  | `count`            |
| `max`     | number  | `max`              |
| `text`    | string  | `text`             |

---

## Parts

| Part      | Description                |
|-----------|----------------------------|
| `badge`   | Outer container element    |
| `label`   | Text content `<span>`      |

---

## Examples

### Colour variants

```html
<x-badge variant="neutral">Neutral</x-badge>
<x-badge variant="info">Info</x-badge>
<x-badge variant="success">Success</x-badge>
<x-badge variant="warning">Warning</x-badge>
<x-badge variant="error">Error</x-badge>
```

### Count with cap

```html
<x-badge variant="error" count="7"></x-badge>
<x-badge variant="error" count="999" max="99"></x-badge>
```

### Text labels

```html
<x-badge variant="success" text="NEW"></x-badge>
<x-badge variant="warning" text="BETA"></x-badge>
```

### Small size

```html
<x-badge variant="info" size="sm" count="5"></x-badge>
```

### Dot

```html
<x-badge variant="error" dot></x-badge>
```

### Pill

```html
<x-badge variant="success" pill count="3"></x-badge>
```

### ClojureScript (hiccup renderer)

```clojure
[:x-badge {:variant "error" :count "23"}]
[:x-badge {:variant "success" :text "NEW"}]
[:x-badge {:variant "error" :dot true}]
```
