# x-icon

A stateless, themeable wrapper around a slotted `<svg>`. Takes care of sizing, theme-aware colour, and accessibility so consumers only need to provide SVG paths.

---

## Tag

```html
<x-icon size="md" color="primary" label="Save">
  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
    <path d="M5 13l4 4L19 7" />
  </svg>
</x-icon>
```

---

## Attributes

| Attribute | Values                                                                                              | Default   | Description                                                                 |
|-----------|-----------------------------------------------------------------------------------------------------|-----------|-----------------------------------------------------------------------------|
| `size`    | `sm` \| `md` \| `lg` \| `xl` \| positive integer (px)                                              | `md`      | Square box size. Tokens map to `sm=16`, `md=20`, `lg=24`, `xl=32`.          |
| `color`   | `inherit` \| `primary` \| `secondary` \| `tertiary` \| `success` \| `warning` \| `danger` \| `muted` | `inherit` | Maps to theme tokens. `inherit` rides the parent's `currentColor`.          |
| `label`   | string                                                                                              | —         | When set and non-empty, the icon is announced with `role="img" aria-label`. When absent or empty, the icon is decorative (`aria-hidden="true"`). |

---

## Properties

| Property | Type   | Reflects attribute |
|----------|--------|--------------------|
| `size`   | string | `size`             |
| `color`  | string | `color`            |
| `label`  | string | `label`            |

No events, no public methods.

---

## Slots

| Slot             | Description                                       |
|------------------|---------------------------------------------------|
| _(default slot)_ | Exactly one `<svg>` element supplied by the caller. |

---

## Parts

| Part  | Description                                    |
|-------|------------------------------------------------|
| `box` | Sized `<span>` that wraps the slot              |

---

## CSS Custom Properties

| Variable         | Default             | Description                                             |
|------------------|---------------------|---------------------------------------------------------|
| `--x-icon-size`  | `20px`              | Square size of the icon box (set inline from `size`)    |
| `--x-icon-color` | `currentColor`      | Resolved colour value (set inline from `color`)         |

Host CSS always applies `color: var(--x-icon-color, currentColor)` so the slotted SVG's `fill="currentColor"` / `stroke="currentColor"` picks it up automatically.

---

## Authoring your SVGs

For `color` to take effect — including the default `inherit` / `currentColor` behaviour — your SVG paths must use `currentColor`:

```html
<svg viewBox="0 0 24 24" fill="currentColor">
  <path d="…" />
</svg>
```

or for stroked icons:

```html
<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
  <path d="…" />
</svg>
```

This is the same convention used by every mainstream icon library (Lucide, Heroicons, Material, Phosphor, Tabler). Icons with hard-coded colours will render but ignore the `color` attribute.

---

## Accessibility

- **Decorative by default.** When `label` is absent (or empty), the host gets `aria-hidden="true"` and no `role`. Screen readers skip it.
- **Labelled.** When `label="…"` is set, the host gets `role="img"` and `aria-label="<label>"`, and `aria-hidden` is removed. The icon is announced as an image with the given name.
- There is no warning for missing `label` — decorative is a valid and common case.

---

## Examples

### Size tokens

```html
<x-icon size="sm"><svg>...</svg></x-icon>
<x-icon size="md"><svg>...</svg></x-icon>
<x-icon size="lg"><svg>...</svg></x-icon>
<x-icon size="xl"><svg>...</svg></x-icon>
```

### Numeric size

```html
<x-icon size="48"><svg>...</svg></x-icon>
```

### Theme colours

```html
<x-icon color="primary"><svg>...</svg></x-icon>
<x-icon color="danger"><svg>...</svg></x-icon>
<x-icon color="muted"><svg>...</svg></x-icon>
```

### Inheriting a parent colour

```html
<button style="color: hotpink">
  <x-icon><svg>...</svg></x-icon>
  Delete
</button>
```

The icon will render in hot pink because `color` defaults to `inherit`.

### Labelled (announced)

```html
<x-icon label="Save document"><svg>...</svg></x-icon>
```

### ClojureScript (hiccup renderer)

```clojure
[:x-icon {:size "lg" :color "primary" :label "Save"}
  [:svg {:viewBox "0 0 24 24" :fill "currentColor"}
    [:path {:d "M19 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11l5 5v11a2 2 0 0 1-2 2z"}]]]
```
