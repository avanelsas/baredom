# x-timeline-item

A single entry in a vertical timeline. Designed to work standalone or inside a future `x-timeline` parent that coordinates multiple items.

## Tag

```html
<x-timeline-item></x-timeline-item>
```

## Attributes

| Attribute   | Type                                              | Default    | Description |
|-------------|---------------------------------------------------|------------|-------------|
| `label`     | string                                            | `""`       | Date/time or step label shown adjacent to the marker |
| `title`     | string                                            | `""`       | Heading text for this timeline entry |
| `status`    | `pending` \| `active` \| `complete` \| `error` \| `warning` | `pending` | Controls marker icon and color |
| `icon`      | string \| `"none"`                                | absent     | Custom marker icon; `"none"` hides the icon entirely |
| `connector` | `solid` \| `dashed` \| `none`                     | `solid`    | The vertical line below the marker |
| `position`  | `start` \| `end`                                  | `start`    | Which side the content appears on; overridden by `data-position` |
| `disabled`  | boolean                                           | absent     | Makes the item non-interactive (no click events, pointer-events off) |

### Parent-managed data attributes

These are set by the future `x-timeline` parent and are observed for reactive rendering.

| Attribute        | Set by      | Effect |
|------------------|-------------|--------|
| `data-last`      | x-timeline  | Hides the connector (CSS `:host([data-last]) .connector { display:none }`) |
| `data-index`     | x-timeline  | 0-based index; used in the marker ARIA label (e.g., "Step 1: …") |
| `data-position`  | x-timeline  | Overrides `position` attr; accepts `start` or `end` only |
| `data-striped`   | x-timeline  | Applies a subtle stripe background |

## JS Properties

| Property    | Type    | Mirrors attribute | Notes |
|-------------|---------|-------------------|-------|
| `label`     | string  | `label`           | |
| `title`     | string  | `title`           | Overrides native `HTMLElement.title` (tooltip) |
| `status`    | string  | `status`          | |
| `icon`      | string \| null | `icon`   | |
| `connector` | string  | `connector`       | |
| `position`  | string  | `position`        | |
| `disabled`  | boolean | `disabled`        | Boolean attribute pattern |

## Events

| Event                          | Bubbles | Composed | Cancelable | Detail |
|--------------------------------|---------|----------|------------|--------|
| `x-timeline-item-connected`    | yes     | yes      | no         | `{ status, label, position, disabled }` |
| `x-timeline-item-disconnected` | no      | no       | no         | `{ status, label }` — dispatched on `document` |
| `x-timeline-item-click`        | yes     | yes      | yes        | `{ status, label }` |

## Slots

| Name       | Description |
|------------|-------------|
| *(default)*| Main body content |
| `label`    | Overrides the `label` attribute |
| `icon`     | Custom marker icon content |
| `actions`  | Action buttons placed below content |

## CSS Custom Properties

| Property                               | Default                                          | Description |
|----------------------------------------|--------------------------------------------------|-------------|
| `--x-timeline-item-marker-size`        | `2rem`                                           | Diameter of the marker circle |
| `--x-timeline-item-marker-bg`          | per-status (see below)                           | Marker background color |
| `--x-timeline-item-marker-color`       | per-status                                       | Marker icon/text color |
| `--x-timeline-item-connector-color`    | per-status                                       | Color of the connector line |
| `--x-timeline-item-connector-width`    | `2px`                                            | Width of the connector line |
| `--x-timeline-item-gap`               | `0.75rem`                                        | Gap between marker and content, and between items |
| `--x-timeline-item-label-width`        | `6rem`                                           | Width of the label column |
| `--x-timeline-item-label-color`        | `rgba(0,0,0,0.5)` / dark: `rgba(255,255,255,0.5)` | Label text color |
| `--x-timeline-item-label-font-size`    | `0.8125rem`                                      | Label font size |
| `--x-timeline-item-title-font-size`    | `0.9375rem`                                      | Title font size |
| `--x-timeline-item-stripe-bg`          | `rgba(0,0,0,0.025)`                              | Background when `data-striped` is present |
| `--x-timeline-item-motion`             | `150ms`                                          | Transition duration for color changes |
| `--x-timeline-item-enter-duration`     | `160ms`                                          | Duration of the enter animation |

### Status color defaults

| Status     | Marker bg (light)      | Marker color | Connector color         |
|------------|------------------------|--------------|-------------------------|
| `pending`  | `rgba(0,0,0,0.06)`     | `rgba(0,0,0,0.45)` | `rgba(0,0,0,0.12)`    |
| `active`   | `rgba(0,102,204,1)`    | `#fff`        | `rgba(0,102,204,0.4)` |
| `complete` | `rgba(16,140,72,1)`    | `#fff`        | `rgba(16,140,72,0.5)` |
| `error`    | `rgba(190,20,40,1)`    | `#fff`        | `rgba(190,20,40,0.4)` |
| `warning`  | `rgba(204,120,0,1)`    | `#fff`        | `rgba(204,120,0,0.4)` |

Dark mode variants are automatically applied via `@media (prefers-color-scheme: dark)`.

## Accessibility

- Host element: `role="listitem"` — the future `x-timeline` parent carries `role="list"`
- Host `aria-label`: derived from `title` (preferred) or `label`
- Marker `aria-label`: `"Step N: {label} ({status})"` when `data-index` is set, otherwise `"({status})"`
- Default icon span: `aria-hidden="true"`
- `tabindex="0"` when interactive; `tabindex="-1"` + `aria-disabled="true"` when disabled
- `pointer-events: none` when disabled

## Keyboard

| Key             | Condition        | Action |
|-----------------|------------------|--------|
| `Enter`         | focused, not disabled | fires `x-timeline-item-click` |
| `Space`         | focused, not disabled | fires `x-timeline-item-click` |

## Motion

- Enter animation: `opacity 0→1` + `translateX(-4px→0)` on connect (reversed for `position="end"`)
- Color transitions on status change (marker bg/color, connector color)
- `@media (prefers-reduced-motion: reduce)` disables all animations and transitions

## Usage examples

### Basic item

```html
<x-timeline-item
  label="Jan 2024"
  title="Project kickoff"
  status="complete">
  <p>The project was officially started.</p>
</x-timeline-item>
```

### With custom icon

```html
<x-timeline-item status="active" icon="🚀" label="Now">
  <p>In progress.</p>
</x-timeline-item>
```

### With actions slot

```html
<x-timeline-item title="Review" status="pending">
  <p>Awaiting approval.</p>
  <div slot="actions">
    <button>Approve</button>
    <button>Reject</button>
  </div>
</x-timeline-item>
```

### Dashed connector

```html
<x-timeline-item connector="dashed" label="Future">Planned item</x-timeline-item>
```

### Content on the right side

```html
<x-timeline-item position="end" label="Mar 2024" title="Launch">
  Production release.
</x-timeline-item>
```

### Disabled item

```html
<x-timeline-item disabled status="pending" label="TBD">
  Not yet scheduled.
</x-timeline-item>
```

### Listening for events

```js
document.querySelector('x-timeline-item').addEventListener('x-timeline-item-click', e => {
  console.log('Clicked:', e.detail.status, e.detail.label);
});
```

## Parent-child coordination (for future x-timeline)

When building `x-timeline`, listen for `x-timeline-item-connected` (bubbling) to detect children, then:

```js
// After each connect, re-index all children
this.addEventListener('x-timeline-item-connected', () => {
  const items = this.querySelectorAll('x-timeline-item');
  items.forEach((item, i) => {
    item.setAttribute('data-index', i);
    if (i === items.length - 1) {
      item.setAttribute('data-last', '');
    } else {
      item.removeAttribute('data-last');
    }
  });
});
```
