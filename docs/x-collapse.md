# x-collapse

A disclosure widget that shows or hides a content panel via an animated toggle. The trigger is a native button with a rotating chevron indicator. Height animation transitions from 0 to scrollHeight on open and back on close, with a `transitionend` / `setTimeout` fallback for robustness. All motion respects `prefers-reduced-motion`.

---

## Tag

```html
<x-collapse header="Section title">
  <div slot="content">Panel content goes here.</div>
</x-collapse>
```

---

## Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `open` | boolean presence | absent = closed | When present, the panel is expanded. |
| `disabled` | boolean presence | absent = enabled | When present, the trigger button is inert and pointer-events are suppressed. |
| `header` | string | `""` | Text rendered inside the trigger button. |
| `duration-ms` | number (0–2000) | `300` | Animation duration in milliseconds. Values outside the range are clamped. |

---

## Properties

| Property | Type | Reflects attribute |
|----------|------|--------------------|
| `open` | boolean | `open` (boolean presence) |
| `disabled` | boolean | `disabled` (boolean presence) |
| `header` | string | `header` |
| `durationMs` | number | `duration-ms` |

---

## Events

| Event | Bubbles | Composed | Cancelable | Detail |
|-------|---------|----------|------------|--------|
| `x-collapse-toggle` | yes | yes | **yes** | `{ open: boolean, source: "trigger" \| "api" }` |
| `x-collapse-change` | yes | yes | no | `{ open: boolean }` |

`x-collapse-toggle` fires before the state change. If it is cancelled (`preventDefault()`), the state change and the subsequent `x-collapse-change` event are both suppressed.

`source` is `"trigger"` when the user activates the button, or `"api"` when `toggle()` is called programmatically.

---

## Slots

| Slot | Description |
|------|-------------|
| `content` | Content to show and hide. Use `<... slot="content">`. The element is always in the DOM; visibility is controlled by height and overflow. |

---

## Parts

| Part | Description |
|------|-------------|
| `container` | Outermost wrapper `<div>`. |
| `trigger` | The `<button>` that controls open/close. |
| `header-text` | `<span>` inside the trigger that renders the `header` attribute value. |
| `chevron` | `<span>` inside the trigger containing the ▼ indicator. Rotates 180° when open. |
| `content` | Animated `<div>` whose height transitions. `overflow: hidden`. |
| `content-inner` | Inner `<div>` inside `content` that wraps the slot. |

---

## CSS Custom Properties

| Variable | Default | Description |
|----------|---------|-------------|
| `--x-collapse-duration` | `300ms` (or from `duration-ms` attr) | Overrides the transition duration set by `duration-ms` when both are present. The CSS variable wins. |
| `--x-collapse-ease` | `ease-in-out` | Timing function for the height transition. |

---

## Accessibility

- The trigger is a native `<button>` element — fully keyboard accessible by default.
- `aria-expanded` on the trigger reflects the current open state (`"true"` / `"false"`).
- `aria-controls` on the trigger points to the `id` of the content panel.
- `aria-disabled="true"` is set on the trigger when the `disabled` attribute is present; the button also gains the `disabled` HTML attribute so it is skipped by tab order.
- The content panel has `role="region"` and `aria-labelledby` pointing to the trigger.
- When `prefers-reduced-motion: reduce` is in effect, the height transition duration is forced to `0ms`.

---

## Keyboard

| Key | Condition | Action |
|-----|-----------|--------|
| `Space` | Trigger focused, not disabled | Toggles the panel open/closed. |
| `Enter` | Trigger focused, not disabled | Toggles the panel open/closed. |

---

## Examples

### HTML

```html
<!-- Collapsed by default -->
<x-collapse header="Details">
  <p slot="content">This content is hidden until the header is clicked.</p>
</x-collapse>

<!-- Open by default -->
<x-collapse header="Open section" open>
  <p slot="content">This content is visible on load.</p>
</x-collapse>

<!-- Disabled -->
<x-collapse header="Locked section" disabled>
  <p slot="content">Cannot be toggled.</p>
</x-collapse>

<!-- Custom duration -->
<x-collapse header="Slow animation" duration-ms="800">
  <p slot="content">Animates over 800ms.</p>
</x-collapse>

<!-- Cancel toggle conditionally -->
<x-collapse id="guarded" header="Guarded panel">
  <p slot="content">Toggle can be prevented.</p>
</x-collapse>
<script>
  document.getElementById('guarded').addEventListener('x-collapse-toggle', e => {
    if (!confirm('Allow toggle?')) e.preventDefault();
  });
</script>
```

### ClojureScript (hiccup)

```clojure
;; Collapsed by default
[:x-collapse {:header "Details"}
  [:p {:slot "content"} "This content is hidden until the header is clicked."]]

;; Open by default
[:x-collapse {:header "Open section" :open true}
  [:p {:slot "content"} "This content is visible on load."]]

;; Disabled
[:x-collapse {:header "Locked section" :disabled true}
  [:p {:slot "content"} "Cannot be toggled."]]

;; Custom duration
[:x-collapse {:header "Slow animation" :duration-ms "800"}
  [:p {:slot "content"} "Animates over 800ms."]]

;; Listen to events
[:x-collapse
  {:header "With events"
   :on-x-collapse-toggle #(js/console.log "toggle" (.. % -detail))
   :on-x-collapse-change #(js/console.log "change" (.. % -detail))}
  [:p {:slot "content"} "Panel content."]]
```
