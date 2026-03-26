# x-stepper

A stateless stepper / wizard-progress component that visualises a sequence of named steps, marking each as completed, current, or upcoming. Clicking a non-current step fires `x-stepper-change` and — unless cancelled — advances or retreats to that step.

## Tag

```html
<x-stepper steps="3" current="0"></x-stepper>
```

## Attributes

| Attribute     | Type                        | Default        | Description |
|---------------|-----------------------------|----------------|-------------|
| `steps`       | JSON array string or integer string | `"[]"` | Step definitions. An integer string (e.g. `"3"`) auto-generates `Step 1 / Step 2 / Step 3`. A JSON array provides explicit labels and optional descriptions: `'[{"label":"Account","description":"Create your account"}]'`. |
| `current`     | number string               | `"0"`          | 0-based index of the active step. Clamped to `[0, steps.length − 1]`. |
| `orientation` | `"horizontal"` \| `"vertical"` | `"horizontal"` | Layout direction of the step track. |
| `size`        | `"sm"` \| `"md"` \| `"lg"` | `"md"`         | Visual scale of the indicator circles and labels. |
| `disabled`    | presence                    | absent         | Disables all click interactions. |

### `steps` format — JSON array

```json
[
  { "label": "Account",  "description": "Create your account" },
  { "label": "Profile",  "description": "Tell us about yourself" },
  { "label": "Confirm" }
]
```

`description` is optional. Steps with no description hide the description element.

## Properties

All attributes are reflected as JS properties.

| Property      | JS type   | Notes |
|---------------|-----------|-------|
| `steps`       | `string`  | Get/set the `steps` attribute value |
| `current`     | `number`  | Returns parsed integer; sets `current` attribute |
| `orientation` | `string`  | Returns normalised value (`"horizontal"` or `"vertical"`) |
| `size`        | `string`  | Returns normalised value (`"sm"`, `"md"`, or `"lg"`) |
| `disabled`    | `boolean` | Reflects presence attribute |

## Events

### `x-stepper-change`

Fired when the user clicks a step that is not the current step.

| Property    | Type    | Description |
|-------------|---------|-------------|
| `detail.from` | `number` | 0-based index of the step the user left |
| `detail.to`   | `number` | 0-based index of the step the user clicked |
| `cancelable`  | `true`  | Call `event.preventDefault()` to prevent the `current` attribute from updating |

```js
el.addEventListener('x-stepper-change', (e) => {
  console.log(`Moving from step ${e.detail.from} to ${e.detail.to}`);
  // Optionally cancel:
  // e.preventDefault();
});
```

## Shadow Parts

| Part                | Element    | Description |
|---------------------|------------|-------------|
| `container`         | `div`      | The outer `role="list"` wrapper |
| `step`              | `div`      | Per-step wrapper (`data-index`, `data-state` attributes) |
| `step-track`        | `div`      | Holds indicator + connector |
| `step-indicator`    | `button`   | Clickable circle/icon for a step |
| `step-number`       | `span`     | Digit or checkmark inside the indicator |
| `step-connector`    | `div`      | Line segment between indicators; hidden on the last step |
| `step-content`      | `div`      | Holds label + description |
| `step-label`        | `span`     | Step label text |
| `step-description`  | `span`     | Optional description text (hidden when empty) |

### `data-state` values on `[part=step]`

| Value      | Meaning |
|------------|---------|
| `complete` | Index is before `current` |
| `current`  | Index equals `current` |
| `upcoming` | Index is after `current` |

## CSS Custom Properties

All properties cascade from `:host` and can be overridden per-instance.

| Property | Default (light) | Description |
|----------|-----------------|-------------|
| `--x-stepper-indicator-size` | `2rem` | Width and height of the circular indicator |
| `--x-stepper-connector-thickness` | `2px` | Thickness of the connector line |
| `--x-stepper-step-gap` | `0.75rem` | Gap between indicator and step content (horizontal: padding-top; vertical: padding-bottom) |
| `--x-stepper-font-size` | `0.875rem` | Label font size |
| `--x-stepper-label-font-weight` | `500` | Font weight of the current step label |
| `--x-stepper-desc-font-size` | `0.75rem` | Description font size |
| `--x-stepper-radius` | `999px` | Border radius of the indicator |
| `--x-stepper-motion` | `120ms` | Duration for background/color transitions |
| `--x-stepper-press-scale` | `0.93` | Scale applied on `:active` |
| `--x-stepper-focus-ring` | `rgba(0,0,0,0.55)` | Focus ring colour |
| `--x-stepper-disabled-opacity` | `0.5` | Opacity of the host when `disabled` |
| `--x-stepper-complete-bg` | `rgba(16,140,72,1)` | Completed indicator background |
| `--x-stepper-complete-color` | `#fff` | Completed indicator foreground |
| `--x-stepper-complete-connector` | `rgba(16,140,72,1)` | Completed connector colour |
| `--x-stepper-current-bg` | `rgba(0,102,204,1)` | Current indicator background |
| `--x-stepper-current-color` | `#fff` | Current indicator foreground |
| `--x-stepper-upcoming-bg` | `rgba(0,0,0,0.08)` | Upcoming indicator background |
| `--x-stepper-upcoming-color` | `rgba(0,0,0,0.45)` | Upcoming indicator foreground |
| `--x-stepper-idle-connector` | `rgba(0,0,0,0.12)` | Current/upcoming connector colour |
| `--x-stepper-label-done-color` | `rgba(0,0,0,0.85)` | Completed step label colour |
| `--x-stepper-label-current-color` | `rgba(0,0,0,0.85)` | Current step label colour |
| `--x-stepper-label-upcoming-color` | `rgba(0,0,0,0.4)` | Upcoming step label colour |
| `--x-stepper-desc-color` | `rgba(0,0,0,0.4)` | Description text colour |

Dark-mode overrides are applied automatically via `@media (prefers-color-scheme: dark)` inside the shadow style.

## Accessibility

- `[part=container]` has `role="list"`.
- Each `[part=step]` has `role="listitem"`.
- `[part=step-indicator]` is a `<button type="button">` with an `aria-label` of the form `"Step N: Label (current|completed|upcoming)"`.
- The current step's indicator has `aria-current="step"`.
- All indicators are `tabindex="0"` (or `"-1"` when `disabled`), enabling keyboard navigation.
- The component respects `@media (prefers-reduced-motion: reduce)` — transitions are disabled.

## Theming & Variants

### Size

```html
<x-stepper steps="3" size="sm"></x-stepper>
<x-stepper steps="3" size="md"></x-stepper>  <!-- default -->
<x-stepper steps="3" size="lg"></x-stepper>
```

### Orientation

```html
<x-stepper steps="3" orientation="horizontal"></x-stepper>  <!-- default -->
<x-stepper steps="3" orientation="vertical"></x-stepper>
```

### Custom colours

```html
<x-stepper steps="4" current="1"
  style="--x-stepper-current-bg: #7c3aed;
         --x-stepper-complete-bg: #7c3aed;
         --x-stepper-complete-connector: #7c3aed;">
</x-stepper>
```

## Usage Examples

### Basic (auto-labelled steps)

```html
<x-stepper steps="4" current="1"></x-stepper>
```

### Named steps with descriptions

```html
<x-stepper
  steps='[
    {"label":"Account","description":"Create your credentials"},
    {"label":"Profile","description":"Tell us about yourself"},
    {"label":"Review"},
    {"label":"Done"}
  ]'
  current="0">
</x-stepper>
```

### Controlled stepper (prevent auto-update)

```html
<x-stepper id="wizard" steps="3" current="0"></x-stepper>
<script>
  const wizard = document.querySelector('#wizard');
  wizard.addEventListener('x-stepper-change', (e) => {
    e.preventDefault();          // stop the default attribute update
    // validate before advancing
    if (canNavigateTo(e.detail.to)) {
      wizard.current = e.detail.to;
    }
  });
</script>
```

### Vertical layout

```html
<x-stepper
  orientation="vertical"
  steps='[{"label":"Install","description":"Run npm install"},
          {"label":"Configure","description":"Edit config file"},
          {"label":"Deploy"}]'
  current="1">
</x-stepper>
```
