# x-search-field

A form-associated custom element providing search-box UX: inline search icon, clear button, and Enter-to-search event. Integrates with `x-form` via `ElementInternals`.

## Tag

```html
<x-search-field name="q" placeholder="Search…" label="Site search"></x-search-field>
```

## Observed Attributes

| Attribute      | Type    | Default | Notes                               |
|----------------|---------|---------|-------------------------------------|
| `name`         | string  | `""`    | Field name in form payload          |
| `value`        | string  | `""`    | Current value (reflects input)      |
| `placeholder`  | string  | `""`    | Input placeholder text              |
| `label`        | string  | `""`    | Sets `aria-label` on the input      |
| `disabled`     | boolean | absent  | Disables input, hides clear button  |
| `required`     | boolean | absent  | Marks field as required             |
| `autocomplete` | enum    | `"off"` | `"on"` \| `"off"`                   |

## Properties

All properties reflect to their corresponding attributes.

| Property       | Type    |
|----------------|---------|
| `value`        | string  |
| `name`         | string  |
| `placeholder`  | string  |
| `label`        | string  |
| `autocomplete` | string  |
| `disabled`     | boolean |
| `required`     | boolean |

Reading `value` returns the live `input.value` (not the attribute). Setting `value` syncs both the attribute and the input DOM node.

## Events

| Event                    | Bubbles | Composed | Cancelable | Detail               |
|--------------------------|---------|----------|------------|----------------------|
| `x-search-field-input`   | yes     | yes      | no         | `{name, value}`      |
| `x-search-field-change`  | yes     | yes      | no         | `{name, value}`      |
| `x-search-field-search`  | yes     | yes      | **yes**    | `{name, value}`      |
| `x-search-field-clear`   | yes     | yes      | no         | `{name}`             |

`x-search-field-search` is dispatched when the user presses Enter. It is cancelable but does **not** auto-submit a parent form.

## Shadow DOM Structure

```
:host
  style
  div[part=wrapper]
    span[part=icon]      — inline SVG magnifying glass, pointer-events:none
    input[part=input]    — type="search", native clear button suppressed
    button[part=clear]   — "×", hidden (.clear-hidden) when empty or disabled
```

## CSS Custom Properties

| Property                            | Default (light)       |
|-------------------------------------|-----------------------|
| `--x-search-field-bg`               | `#ffffff`             |
| `--x-search-field-color`            | `#111827`             |
| `--x-search-field-border`           | `1px solid #d1d5db`   |
| `--x-search-field-border-radius`    | `6px`                 |
| `--x-search-field-focus-ring-color` | `#2563eb`             |
| `--x-search-field-icon-color`       | `#9ca3af`             |
| `--x-search-field-clear-color`      | `#9ca3af`             |
| `--x-search-field-disabled-opacity` | `0.45`                |

Dark mode values are applied automatically via `@media (prefers-color-scheme: dark)` inside the shadow style.

## Form Association

`x-search-field` is a form-associated custom element (`static formAssociated = true`). It:

- Participates in `<form>` submission via `ElementInternals.setFormValue`
- Supports `required` validation (`valueMissing`) via `ElementInternals.setValidity`
- Exposes `reportValidity()` through the browser's `ElementInternals` implementation
- Responds to `formDisabledCallback` (syncs disabled state)
- Responds to `formResetCallback` (clears value)

### Usage inside x-form

```html
<x-form>
  <x-search-field name="q" label="Site search" placeholder="Search…" required></x-search-field>
  <button type="submit">Search</button>
  <button type="reset">Clear</button>
</x-form>
```

`x-form`'s `collect-values` picks up `x-search-field` via the `(.-value field)` property fallback — no x-form changes required.

## Accessibility

- The `label` attribute sets `aria-label` on the inner `<input>` (no visible label element is rendered).
- `aria-required` is set to `"true"` / `"false"` on the input based on the `required` attribute.
- The clear button has a static `aria-label="Clear search"`.
- The search icon SVG carries `aria-hidden="true"` and is excluded from pointer events.
- Focus is moved back to the input after the clear button is activated.
- Animations respect `@media (prefers-reduced-motion: reduce)`.

## Usage Examples

### Standalone with event logging

```html
<x-search-field id="site-search" name="q" label="Search" placeholder="Search…"></x-search-field>

<script>
  const sf = document.getElementById('site-search');
  sf.addEventListener('x-search-field-search', e => {
    console.log('Search:', e.detail.value);
  });
  sf.addEventListener('x-search-field-clear', () => {
    console.log('Cleared');
  });
</script>
```

### Inside x-form

```html
<x-form id="search-form">
  <x-search-field name="q" label="Site search" placeholder="Search the site…" required></x-search-field>
  <button type="submit">Search</button>
  <button type="reset">Reset</button>
</x-form>

<script>
  document.getElementById('search-form').addEventListener('x-form-submit', e => {
    console.log('Form values:', e.detail.values); // { q: "…" }
  });
</script>
```

### CSS theming

```css
x-search-field {
  --x-search-field-bg: #f1f5f9;
  --x-search-field-border-radius: 9999px;
  --x-search-field-focus-ring-color: #7c3aed;
}
```
