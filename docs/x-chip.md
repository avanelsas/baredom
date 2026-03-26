# x-chip

A compact, themeable chip component for displaying tags, filters, or selection tokens. Supports an optional remove button with a cancelable removal event and an exit animation. The consumer is responsible for removing the element from the DOM after handling the event.

---

## Tag

```html
<x-chip></x-chip>
```

---

## Attributes

| Attribute   | Type     | Default | Description                                                                         |
|-------------|----------|---------|-------------------------------------------------------------------------------------|
| `label`     | string   | —       | Visible text content of the chip                                                    |
| `value`     | string   | —       | Value emitted in events; falls back to `label` when absent                          |
| `removable` | boolean† | `true`  | Whether the remove (×) button is rendered                                           |
| `disabled`  | boolean  | `false` | Disables all interaction; prevents remove events                                    |

†`removable` uses default-true semantics: absent or empty string = `true`; `removable="false"` = `false`.

---

## Properties

| Property    | Type    | Reflects attribute |
|-------------|---------|-------------------|
| `label`     | string  | `label`           |
| `value`     | string  | `value`           |
| `removable` | boolean | `removable`       |
| `disabled`  | boolean | `disabled`        |

---

## Events

| Event           | Bubbles | Composed | Cancelable | Detail                  |
|-----------------|---------|----------|------------|-------------------------|
| `x-chip-remove` | yes     | yes      | **yes**    | `{ value, label }`      |

`x-chip-remove` is dispatched before the exit animation begins. Call `event.preventDefault()` to cancel the removal — no animation plays and the chip stays in the DOM unchanged.

`detail.value` is the resolved value: the `value` attribute if set, otherwise the `label` attribute value.

---

## Parts

| Part        | Description                          |
|-------------|--------------------------------------|
| `container` | Outer wrapper element                |
| `label`     | Text label `<span>`                  |
| `remove`    | Remove button `<button>` (× symbol)  |

---

## CSS Custom Properties

| Variable                  | Default          | Description                    |
|---------------------------|------------------|--------------------------------|
| `--x-chip-bg`             | theme-dependent  | Chip background color          |
| `--x-chip-color`          | theme-dependent  | Chip text color                |
| `--x-chip-border`         | theme-dependent  | Chip border color              |
| `--x-chip-radius`         | `9999px`         | Border radius (pill by default)|
| `--x-chip-font-size`      | `0.8125rem`      | Label font size                |
| `--x-chip-padding-x`      | `10px`           | Horizontal padding             |
| `--x-chip-padding-y`      | `4px`            | Vertical padding               |
| `--x-chip-remove-size`    | `16px`           | Remove button size             |
| `--x-chip-exit-duration`  | `160ms`          | Exit animation duration        |

Dark-mode defaults are set automatically via `@media (prefers-color-scheme: dark)`.

---

## Accessibility

- Set `role="listitem"` on the host element when chips are used inside a `role="list"` container.
- The host receives `tabindex="0"` and `aria-keyshortcuts="Backspace Delete"` when `removable` is true and `disabled` is false.
- The host receives `aria-disabled="true"` and `tabindex="-1"` when `disabled`.
- `[part=remove]` has `aria-label="Remove"` and `type="button"`.
- The label text is exposed to assistive technology via `[part=label]`.

---

## Keyboard

| Key                     | Condition                        | Action              |
|-------------------------|----------------------------------|---------------------|
| `Backspace` or `Delete` | Host focused, removable, enabled | Attempts removal    |

---

## Removal flow

1. User clicks `[part=remove]` or presses `Backspace`/`Delete` on the host.
2. `x-chip-remove` is dispatched (cancelable).
3. If `preventDefault()` is called → nothing happens.
4. If not prevented → `[data-exiting]` attribute is set on the host, triggering the CSS exit animation.
5. The consumer must listen for `x-chip-remove` and remove the element from the DOM (typically after the animation, or immediately).

```js
chip.addEventListener('x-chip-remove', e => {
  // optionally wait for animation end, then:
  e.target.remove();
});
```

---

## Examples

### Basic chip list

```html
<ul role="list" style="display:flex; gap:8px; list-style:none; padding:0">
  <x-chip role="listitem" label="Design"></x-chip>
  <x-chip role="listitem" label="Engineering"></x-chip>
  <x-chip role="listitem" label="Marketing" value="mkt"></x-chip>
</ul>
```

### Non-removable

```html
<x-chip label="Read-only" removable="false"></x-chip>
```

### Disabled

```html
<x-chip label="Archived" disabled></x-chip>
```

### Handling removal

```js
document.querySelectorAll('x-chip').forEach(chip => {
  chip.addEventListener('x-chip-remove', e => {
    e.target.remove();
  });
});
```

### Preventing removal

```js
chip.addEventListener('x-chip-remove', e => {
  if (!canRemove(e.detail.value)) {
    e.preventDefault();
  }
});
```

### ClojureScript (hiccup)

```clojure
[:x-chip {:label "Design" :role "listitem"}]

[:x-chip {:label "Read-only" :removable "false" :role "listitem"}]

[:x-chip {:label      "Engineering"
          :value      "eng"
          :role       "listitem"
          :on-x-chip-remove (fn [e] (.remove (.-target e)))}]
```
