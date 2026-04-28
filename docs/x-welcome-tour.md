# x-welcome-tour

A guided product tour component with spotlight backdrop, popover steps, and configurable connectors. Each step highlights a target element on the page with a dimmed overlay and cutout, anchoring a popover with navigation controls.

**Tag name:** `x-welcome-tour`
**Step tag name:** `x-welcome-tour-step`

---

## Usage

```html
<x-welcome-tour id="tour" counter>
  <x-welcome-tour-step target="#search-box" title="Search" placement="bottom">
    <p>Find anything fast with the search bar.</p>
  </x-welcome-tour-step>
  <x-welcome-tour-step target="#sidebar" title="Navigation" placement="right">
    <p>Browse all sections from the sidebar.</p>
  </x-welcome-tour-step>
</x-welcome-tour>

<script>
  document.getElementById('tour').start();
</script>
```

---

## `x-welcome-tour` Attributes

| Attribute     | Type              | Default    | Description                                          |
|---------------|-------------------|------------|------------------------------------------------------|
| `open`        | boolean (presence)| absent     | Whether the tour is active                           |
| `step`        | string (number)   | `"0"`      | Current step index (0-based)                         |
| `connector`   | enum              | `"arrow"`  | Default connector: `arrow`, `line`, `curve`, `none`  |
| `prev-label`  | string            | `"Back"`   | Previous button text                                 |
| `next-label`  | string            | `"Next"`   | Next button text                                     |
| `done-label`  | string            | `"Done"`   | Last-step button text                                |
| `skip-label`  | string            | `"Skip"`   | Close/skip button text                               |
| `counter`     | boolean (presence)| absent     | Show step counter "N / M"                            |
| `dots`        | boolean (presence)| absent     | Show dot indicators                                  |

## `x-welcome-tour` Properties

| Property     | Type    | Reflects    | Description                      |
|--------------|---------|-------------|----------------------------------|
| `open`       | boolean | `open`      | Tour active state                |
| `step`       | number  | `step`      | Current step index               |
| `connector`  | string  | `connector` | Default connector type           |
| `totalSteps` | number  | (readonly)  | Number of step child elements    |
| `prevLabel`  | string  | `prev-label`| Previous button text             |
| `nextLabel`  | string  | `next-label`| Next button text                 |
| `doneLabel`  | string  | `done-label`| Done button text                 |
| `skipLabel`  | string  | `skip-label`| Skip button text                 |

## `x-welcome-tour` Methods

| Method       | Signature          | Description                              |
|--------------|--------------------|------------------------------------------|
| `start()`    | `() => void`       | Open tour at step 0                      |
| `next()`     | `() => void`       | Advance to next step (or complete)       |
| `prev()`     | `() => void`       | Go to previous step                      |
| `goTo(n)`    | `(number) => void` | Jump to step n                           |
| `complete()` | `() => void`       | Close tour, dispatch complete event      |
| `skip()`     | `() => void`       | Close tour, dispatch skip event          |

## `x-welcome-tour` Events

| Event                        | Cancelable | Detail                              |
|------------------------------|-----------|--------------------------------------|
| `x-welcome-tour-start`      | false     | `{}`                                 |
| `x-welcome-tour-step-change` | true      | `{ step: number, previousStep: number }` |
| `x-welcome-tour-complete`   | false     | `{ stepsCompleted: number }`         |
| `x-welcome-tour-skip`       | false     | `{ step: number }`                   |

All events bubble and are composed.

Cancelling `x-welcome-tour-step-change` (via `preventDefault()`) prevents the step from advancing.

## `x-welcome-tour` Slots

| Slot      | Description                   |
|-----------|-------------------------------|
| (default) | `x-welcome-tour-step` children |

---

## `x-welcome-tour-step` Attributes

| Attribute        | Type              | Default    | Description                                       |
|------------------|-------------------|------------|---------------------------------------------------|
| `target`         | string (selector) | (required) | CSS selector for the target element               |
| `title`          | string            | `""`       | Step title displayed in the popover header        |
| `placement`      | enum              | `"bottom"` | Popover placement relative to target              |
| `connector`      | enum              | (inherit)  | Override connector for this step                  |
| `cutout-padding` | string (number)   | `"8"`      | Extra padding around the target cutout (px)       |
| `cutout-radius`  | string (number)   | `"4"`      | Border radius of the cutout (px)                  |
| `scroll-to`      | boolean           | true       | Scroll target into view when step activates       |

### Placement values

`top`, `bottom`, `left`, `right`, `top-start`, `top-end`, `bottom-start`, `bottom-end`

### Connector values

`arrow` (CSS triangle), `line` (straight SVG), `curve` (S-shaped SVG bezier), `none`

## `x-welcome-tour-step` Properties

| Property       | Type    | Reflects         |
|----------------|---------|------------------|
| `target`       | string  | `target`         |
| `title`        | string  | `title`          |
| `placement`    | string  | `placement`      |
| `connector`    | string  | `connector`      |
| `cutoutPadding`| number  | `cutout-padding` |
| `cutoutRadius` | number  | `cutout-radius`  |
| `scrollTo`     | boolean | `scroll-to`      |

## `x-welcome-tour-step` Slots

| Slot      | Description                          |
|-----------|--------------------------------------|
| (default) | Rich content for the step popover body |

---

## CSS Custom Properties

Set these on `x-welcome-tour` or an ancestor to customise appearance.

| Property                                  | Default                                        |
|-------------------------------------------|------------------------------------------------|
| `--x-welcome-tour-backdrop`               | `rgba(0,0,0,0.5)`                             |
| `--x-welcome-tour-backdrop-z`             | `var(--x-z-modal, 1100)`                      |
| `--x-welcome-tour-glow-color`             | `rgba(255,255,255,0.15)`                       |
| `--x-welcome-tour-popover-bg`             | `var(--x-color-bg, #ffffff)`                   |
| `--x-welcome-tour-popover-fg`             | `var(--x-color-text, #0f172a)`                 |
| `--x-welcome-tour-popover-border`         | `1px solid var(--x-color-border, #e2e8f0)`     |
| `--x-welcome-tour-popover-radius`         | `var(--x-radius-lg, 12px)`                     |
| `--x-welcome-tour-popover-shadow`         | `var(--x-shadow-lg)`                           |
| `--x-welcome-tour-popover-width`          | `min(360px, calc(100vw - 2rem))`               |
| `--x-welcome-tour-transition-duration`    | `var(--x-transition-duration, 300ms)`          |
| `--x-welcome-tour-transition-easing`      | `var(--x-transition-easing, ease)`             |
| `--x-welcome-tour-connector-color`        | `var(--x-color-primary, #3b82f6)`              |
| `--x-welcome-tour-connector-width`        | `2px`                                          |
| `--x-welcome-tour-dot-color`              | `var(--x-color-border, #e2e8f0)`               |
| `--x-welcome-tour-dot-active`             | `var(--x-color-primary, #3b82f6)`              |
| `--x-welcome-tour-button-bg`              | `var(--x-color-primary, #3b82f6)`              |
| `--x-welcome-tour-button-fg`              | `#ffffff`                                      |

## CSS Parts

Style shadow DOM elements using `::part()`:

| Part             | Element                          |
|------------------|----------------------------------|
| `backdrop-svg`   | SVG overlay with cutout mask     |
| `popover`        | Popover card container           |
| `header`         | Popover header row               |
| `title`          | Step title text                  |
| `counter`        | Step counter "N / M"             |
| `close-button`   | Close/skip button (x)            |
| `body`           | Step content area                |
| `footer`         | Navigation footer                |
| `prev-button`    | Back button                      |
| `next-button`    | Next/Done button                 |
| `dots`           | Dot indicators container         |
| `dot`            | Individual dot indicator          |
| `arrow`          | CSS arrow triangle               |
| `connector-svg`  | SVG connector (line/curve)       |

---

## Accessibility

- The popover has `role="dialog"`
- Focus is trapped within the popover while the tour is open
- **Escape** skips (closes) the tour
- **ArrowRight / ArrowDown** advances to the next step
- **ArrowLeft / ArrowUp** goes to the previous step
- On close, focus is restored to the element that was focused before the tour opened
- All buttons meet 44px minimum touch target on coarse pointers
- Animations respect `prefers-reduced-motion: reduce`

## Theming

The component consumes x-theme design tokens. All CSS custom properties use `var(--x-token, fallback)` so the component works with or without an `<x-theme>` ancestor.

Light/dark mode is handled via `@media (prefers-color-scheme: dark)` inside the overlay layer styles.

## Mobile

- Popover width: `min(360px, calc(100vw - 2rem))` prevents overflow on small screens
- Touch targets are 44px minimum on coarse pointers
- Scroll-to-view ensures targets are visible before highlighting
- Positioning flips automatically when there isn't enough room
