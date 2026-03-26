# x-modal

Centered overlay dialog with backdrop. Traps focus, dismisses on Escape or backdrop click, and animates with opacity + scale.

## Tag name

```html
<x-modal>…</x-modal>
```

## Observed Attributes

| Attribute | Type    | Default  | Description |
|-----------|---------|----------|-------------|
| `open`    | boolean | absent   | Presence controls visibility. Set to open, remove to close. |
| `size`    | string  | `"md"`   | Width variant: `sm \| md \| lg \| xl \| full` |
| `label`   | string  | `"Modal"` | Accessible label applied as `aria-label` on the dialog element. |

## Properties

| Property | Type    | Default  | Description |
|----------|---------|----------|-------------|
| `open`   | boolean | `false`  | Reflects the `open` attribute. Setting `true` calls `show()`, `false` calls `hide()`. |
| `size`   | string  | `"md"`   | Reflects the `size` attribute. |
| `label`  | string  | `"Modal"` | Reflects the `label` attribute. |

## Methods

| Method     | Description |
|------------|-------------|
| `show()`   | Opens the modal (sets `open` attribute). No-op if already open. |
| `hide()`   | Closes the modal (removes `open` attribute). |
| `toggle()` | Opens if closed, closes if open. |

## Events

| Event           | Bubbles | Detail                         | Description |
|-----------------|---------|--------------------------------|-------------|
| `x-modal-toggle`  | yes     | `{ open: boolean }`            | Fires once on any open/close transition. |
| `x-modal-dismiss` | yes     | `{ reason: "escape"\|"backdrop" }` | Fires on user-initiated close before the modal closes. |

## Slots

| Slot       | Description |
|------------|-------------|
| `header`   | Header content. Rendered in `[part=header]` with bottom border. |
| *(default)*| Body content. Rendered in `[part=body]`, scrollable. |
| `footer`   | Footer content. Rendered in `[part=footer]` with top border. |

## CSS Parts

| Part       | Description |
|------------|-------------|
| `backdrop` | Fixed overlay behind the dialog. |
| `dialog`   | The dialog container. Centered with translate(-50%,-50%). |
| `header`   | Header wrapper div. |
| `body`     | Body wrapper div (scrollable). |
| `footer`   | Footer wrapper div. |

## CSS Custom Properties

| Property                    | Default                                 | Description |
|-----------------------------|-----------------------------------------|-------------|
| `--x-modal-bg`              | `Canvas`                                | Dialog background color. |
| `--x-modal-fg`              | `CanvasText`                            | Dialog foreground color. |
| `--x-modal-backdrop`        | `rgb(0 0 0 / 0.45)`                     | Backdrop background. |
| `--x-modal-shadow`          | `0 20px 60px rgb(0 0 0 / 0.25)`         | Dialog box shadow. |
| `--x-modal-radius`          | `0.75rem`                               | Dialog border radius. |
| `--x-modal-width-sm`        | `22rem`                                 | Width for `size="sm"`. |
| `--x-modal-width-md`        | `32rem`                                 | Width for `size="md"`. |
| `--x-modal-width-lg`        | `44rem`                                 | Width for `size="lg"`. |
| `--x-modal-width-xl`        | `60rem`                                 | Width for `size="xl"`. |
| `--x-modal-max-height`      | `90vh`                                  | Maximum height for sm/md/lg/xl variants. |
| `--x-modal-header-padding`  | `1rem 1.25rem`                          | Header padding. |
| `--x-modal-body-padding`    | `1rem 1.25rem`                          | Body padding. |
| `--x-modal-footer-padding`  | `0.75rem 1.25rem`                       | Footer padding. |
| `--x-modal-border`          | `color-mix(in srgb, currentColor 12%, transparent)` | Header/footer divider color. |
| `--x-modal-duration`        | `180ms`                                 | Transition duration. |
| `--x-modal-easing`          | `ease`                                  | Transition easing. |
| `--x-modal-z`               | `1000`                                  | Backdrop z-index (dialog is z+1). |

## Accessibility

- `[part=dialog]` has `role="dialog"` and `aria-modal="true"`.
- `aria-label` is set from the `label` attribute (default: `"Modal"`).
- Focus is trapped inside the open dialog. Tab and Shift+Tab cycle through tabbable elements.
- Focus is restored to the previously focused element on close.
- Escape key closes the modal and fires `x-modal-dismiss` with `reason: "escape"`.
- Respects `prefers-reduced-motion: reduce` — disables all transitions.

## Size Variants

| Size   | Width               | Notes |
|--------|---------------------|-------|
| `sm`   | `22rem`             | |
| `md`   | `32rem`             | Default |
| `lg`   | `44rem`             | |
| `xl`   | `60rem`             | |
| `full` | `100vw × 100dvh`   | Fills the viewport, border-radius removed. |

## Usage

```html
<!-- Import -->
<script>
  import { init } from '@vanelsas/baredom/x-modal';
  init();
</script>

<!-- Basic usage -->
<button onclick="document.getElementById('my-modal').show()">Open Modal</button>

<x-modal id="my-modal" label="Confirm Action">
  <div slot="header">Confirm</div>
  <p>Are you sure you want to proceed?</p>
  <div slot="footer">
    <button onclick="document.getElementById('my-modal').hide()">Cancel</button>
    <button onclick="handleConfirm()">Confirm</button>
  </div>
</x-modal>

<!-- Large variant -->
<x-modal id="settings-modal" size="lg" label="Settings">
  <div slot="header">Settings</div>
  <p>Settings content here…</p>
  <div slot="footer">
    <button onclick="document.getElementById('settings-modal').hide()">Close</button>
  </div>
</x-modal>

<!-- Full-screen -->
<x-modal id="fullscreen-modal" size="full" label="Full Screen View">
  <div slot="header">Full Screen</div>
  <p>Content fills the entire viewport.</p>
</x-modal>

<!-- Listen to events -->
<script>
  const modal = document.getElementById('my-modal');
  modal.addEventListener('x-modal-toggle', (e) => {
    console.log('open:', e.detail.open);
  });
  modal.addEventListener('x-modal-dismiss', (e) => {
    console.log('dismissed via:', e.detail.reason);
  });
</script>
```
