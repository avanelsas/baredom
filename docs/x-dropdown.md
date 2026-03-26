# x-dropdown

An action-menu dropdown: a styled trigger button that toggles a positioned panel containing arbitrary slotted content (links, buttons, etc.). No value selection — purely a show/hide panel component.

## Tag

```html
<x-dropdown label="Actions">
  <button>Edit</button>
  <button>Delete</button>
</x-dropdown>
```

## Observed Attributes

| Attribute   | Type    | Default         | Description                                     |
|-------------|---------|-----------------|-----------------------------------------------------|
| `open`      | boolean | absent          | Panel is visible                                    |
| `disabled`  | boolean | absent          | Trigger is non-interactive; blocks all state changes |
| `label`     | string  | `""`            | Trigger button text                                 |
| `placement` | string  | `"bottom-start"`| Panel position relative to trigger                 |

### Placement values

| Value          | Description                        |
|----------------|------------------------------------|
| `bottom-start` | Below trigger, aligned to start    |
| `bottom-end`   | Below trigger, aligned to end      |
| `top-start`    | Above trigger, aligned to start    |
| `top-end`      | Above trigger, aligned to end      |

## Properties

| Property    | Type    | Reflects Attribute |
|-------------|---------|-------------------|
| `open`      | boolean | `open`            |
| `disabled`  | boolean | `disabled`        |
| `label`     | string  | `label`           |
| `placement` | string  | `placement`       |

## Public Methods

| Method      | Description                                                              |
|-------------|--------------------------------------------------------------------------|
| `show()`    | Opens the panel (no-op if already open; dispatches toggle/change events) |
| `hide()`    | Closes the panel (no-op if already closed; dispatches toggle/change events) |
| `toggle()`  | Toggles the panel open/closed                                            |

> **Note:** The programmatic methods (`show`, `hide`, `toggle`) are blocked when `disabled` is set, matching pointer and keyboard behaviour.

## Events

| Event                  | Cancelable | Detail                              | Description                                          |
|------------------------|------------|-------------------------------------|------------------------------------------------------|
| `x-dropdown-toggle`    | yes        | `{ open: boolean, source: string }` | Fires before state change; cancel prevents it        |
| `x-dropdown-change`    | no         | `{ open: boolean }`                 | Fires after state commits                            |

### `source` values

| Source           | When                                       |
|------------------|--------------------------------------------|
| `"pointer"`      | User clicked the trigger                   |
| `"keyboard"`     | User pressed Enter or Space on the trigger |
| `"programmatic"` | `show()`, `hide()`, or `toggle()` called   |
| `"outside-click"`| User clicked outside the component         |
| `"escape"`       | User pressed Escape while panel is open    |
| `"focusout"`     | Focus moved outside the component          |

## Slots

| Slot      | Description                                              |
|-----------|----------------------------------------------------------|
| (default) | Panel content — action items, links, arbitrary HTML      |

## CSS Custom Properties

### Trigger

| Property                            | Default (light)              |
|-------------------------------------|------------------------------|
| `--x-dropdown-trigger-bg`           | `#f8fafc`                    |
| `--x-dropdown-trigger-bg-hover`     | `#f1f5f9`                    |
| `--x-dropdown-trigger-bg-active`    | `#e2e8f0`                    |
| `--x-dropdown-trigger-color`        | `#0f172a`                    |
| `--x-dropdown-trigger-border`       | `1px solid #e2e8f0`          |
| `--x-dropdown-trigger-radius`       | `6px`                        |
| `--x-dropdown-trigger-padding`      | `0 0.75rem`                  |
| `--x-dropdown-trigger-height`       | `2.25rem`                    |
| `--x-dropdown-trigger-font-size`    | `0.9375rem`                  |
| `--x-dropdown-trigger-font-weight`  | `500`                        |
| `--x-dropdown-chevron-color`        | `#64748b`                    |
| `--x-dropdown-focus-ring`           | `#60a5fa`                    |

### Panel

| Property                            | Default (light)              |
|-------------------------------------|------------------------------|
| `--x-dropdown-panel-bg`             | `#ffffff`                    |
| `--x-dropdown-panel-border`         | `1px solid #e2e8f0`          |
| `--x-dropdown-panel-radius`         | `8px`                        |
| `--x-dropdown-panel-shadow`         | `0 4px 16px rgba(0,0,0,0.12)`|
| `--x-dropdown-panel-padding`        | `0.25rem`                    |
| `--x-dropdown-panel-min-width`      | `10rem`                      |
| `--x-dropdown-panel-max-height`     | `20rem`                      |
| `--x-dropdown-panel-offset`         | `4px`                        |

### Animation

| Property                                | Default       |
|-----------------------------------------|---------------|
| `--x-dropdown-transition-duration`      | `150ms`       |
| `--x-dropdown-transition-easing`        | `ease`        |

## Shadow DOM Parts

| Part             | Description                                |
|------------------|--------------------------------------------|
| `trigger`        | The `<button>` element that opens the panel |
| `trigger-label`  | `<span>` containing the label text         |
| `chevron`        | `<span>` containing the chevron indicator  |
| `panel`          | The positioned panel container             |
| `panel-inner`    | Inner wrapper that hosts the default slot  |

## Accessibility

- `[part=trigger]` has `aria-haspopup="true"`, `aria-expanded="false|true"`, and `aria-controls="panel"`.
- `[part=panel]` has `id="panel"` matching the `aria-controls` reference (within shadow root).
- `[part=chevron]` has `aria-hidden="true"`.
- Keyboard: **Enter** or **Space** toggles the panel; **Escape** closes it.
- **Reduced motion**: CSS transitions are disabled when `prefers-reduced-motion: reduce` is active.

## Behaviour

- **Outside click**: clicking outside the component closes the panel.
- **Escape**: pressing Escape while the panel is open closes it.
- **Focusout**: focus leaving the component closes the panel.
- **Cancelled toggle**: calling `preventDefault()` on `x-dropdown-toggle` prevents the state change and suppresses `x-dropdown-change`.
- **Placement**: driven entirely by CSS via `data-placement` on `[part=panel]`. No JavaScript viewport math — the component does not auto-flip.

## Usage examples

```html
<!-- Basic -->
<x-dropdown label="Actions">
  <button>Edit</button>
  <button>Archive</button>
  <button>Delete</button>
</x-dropdown>

<!-- Open by default -->
<x-dropdown label="File" open>
  <a href="/new">New file</a>
  <a href="/open">Open…</a>
  <a href="/save">Save</a>
</x-dropdown>

<!-- Placement -->
<x-dropdown label="More" placement="top-end">
  <button>Option A</button>
  <button>Option B</button>
</x-dropdown>

<!-- Disabled -->
<x-dropdown label="Locked" disabled>
  <button>Hidden</button>
</x-dropdown>

<!-- Custom theme -->
<x-dropdown label="Custom"
  style="
    --x-dropdown-trigger-bg: #6366f1;
    --x-dropdown-trigger-color: #fff;
    --x-dropdown-panel-radius: 12px;
  ">
  <button>Item 1</button>
</x-dropdown>

<!-- Programmatic control -->
<script>
  const dd = document.querySelector('x-dropdown');

  // Open
  dd.show();

  // Close
  dd.hide();

  // Toggle
  dd.toggle();

  // Listen for state changes
  dd.addEventListener('x-dropdown-change', e => {
    console.log('open:', e.detail.open);
  });

  // Cancel a specific open
  dd.addEventListener('x-dropdown-toggle', e => {
    if (e.detail.open && someCondition) {
      e.preventDefault(); // prevents opening
    }
  });
</script>
```
