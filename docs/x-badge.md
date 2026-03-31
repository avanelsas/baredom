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

| Property           | Type    | Read-only | Reflects attribute   |
|--------------------|---------|-----------|----------------------|
| `variant`          | string  | no        | `variant`            |
| `size`             | string  | no        | `size`               |
| `pill`             | boolean | no        | `pill`               |
| `dot`              | boolean | no        | `dot`                |
| `count`            | number  | no        | `count`              |
| `max`              | number  | no        | `max`                |
| `text`             | string  | no        | `text`               |
| `aria-label`       | string  | no        | `aria-label`         |
| `aria-describedby` | string  | no        | `aria-describedby`   |
| `displayText`      | string  | yes       | —                    |

`displayText` returns the currently rendered label string (`"7"`, `"99+"`, `"NEW"`, etc.) or `null` when the badge is in dot, slot, or empty mode.

---

## Parts

| Part    | Description             |
|---------|-------------------------|
| `base`  | Outer container element |
| `label` | Text content `<span>`   |

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

---

## CSS custom properties

| Property               | Default (light)          | Description                    |
|------------------------|--------------------------|--------------------------------|
| `--x-badge-bg`         | `rgba(0,0,0,0.08)`       | Background colour              |
| `--x-badge-color`      | `rgba(0,0,0,0.80)`       | Text colour                    |
| `--x-badge-border`     | `rgba(0,0,0,0.12)`       | Border colour                  |
| `--x-badge-font-size`  | `0.75rem`                | Font size (`sm`: `0.6875rem`)  |
| `--x-badge-height`     | `1.25rem`                | Min height (`sm`: `1rem`)      |
| `--x-badge-padding`    | `0 0.375rem`             | Inline padding (`sm`: `0 0.25rem`) |
| `--x-badge-radius`     | `0.25rem`                | Border radius (`sm`: `0.1875rem`) |

Variant attributes (`data-variant`) set these properties automatically. Override on the host to customise individual badges:

```css
x-badge.custom {
  --x-badge-bg: #e0f2fe;
  --x-badge-color: #0369a1;
  --x-badge-border: #7dd3fc;
}
```

---

## Accessibility

- The inner container carries `role="status"` so screen readers announce badge content as a live region.
- Set `aria-label` to provide a meaningful label when the visual content alone is insufficient (e.g. `aria-label="3 unread messages"` on a count badge).
- `aria-describedby` is set on the same `role="status"` element; reference IDs within the same document scope.
- In dot mode the badge has no text — always provide `aria-label` when using `dot`.
- Animations respect `prefers-reduced-motion: reduce`.

---

### ClojureScript (hiccup renderer)

```clojure
[:x-badge {:variant "error" :count "23"}]
[:x-badge {:variant "success" :text "NEW"}]
[:x-badge {:variant "error" :dot true}]
```
