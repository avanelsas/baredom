# x-drawer

A modal overlay panel that slides in from any edge of the screen (left, right, top, or bottom). The drawer is always a temporary modal overlay: it always shows a backdrop, always traps focus, and always dismisses on Escape or backdrop click.

## Tag

```html
<x-drawer placement="right" label="Drawer">
  <div slot="header">Header content</div>
  Body content goes here
  <div slot="footer">Footer content</div>
</x-drawer>
```

## Observed Attributes

| Attribute   | Type   | Default    | Description                              |
|-------------|--------|------------|------------------------------------------|
| `open`      | bool   | absent     | Shows the drawer when present            |
| `placement` | enum   | `"right"`  | Edge: `left \| right \| top \| bottom`  |
| `label`     | string | `"Drawer"` | `aria-label` for the dialog panel        |

## Properties

| Property    | Type    | Reflects attribute |
|-------------|---------|--------------------|
| `open`      | boolean | `open`             |
| `placement` | string  | `placement`        |
| `label`     | string  | `label`            |

## Methods

| Method     | Description            |
|------------|------------------------|
| `show()`   | Sets `open` attribute  |
| `hide()`   | Removes `open` attribute |
| `toggle()` | Flips open state       |

## Events

| Event            | Cancelable | Detail                               |
|------------------|------------|--------------------------------------|
| `x-drawer-toggle`  | false    | `{ open: boolean }`                  |
| `x-drawer-dismiss` | false    | `{ reason: "escape" \| "backdrop" }` |

`x-drawer-toggle` fires whenever the open state changes (open or close), including programmatic changes.

`x-drawer-dismiss` fires only when the drawer is closed by a user gesture (Escape key or backdrop click).

## Slots

| Slot       | Description                                 |
|------------|---------------------------------------------|
| `header`   | Named — title area, close button, etc.      |
| (default)  | Scrollable body content                     |
| `footer`   | Named — action buttons                      |

There is no built-in close button. Provide a close affordance in the `header` slot.

## CSS Parts

| Part       | Description                         |
|------------|-------------------------------------|
| `backdrop` | Overlay scrim behind the panel      |
| `panel`    | The drawer container (dialog)       |
| `header`   | Header slot wrapper                 |
| `body`     | Default slot wrapper (scrollable)   |
| `footer`   | Footer slot wrapper                 |

## CSS Custom Properties

| Property                    | Light default                                        | Dark default                    | Description                                       |
|-----------------------------|------------------------------------------------------|---------------------------------|---------------------------------------------------|
| `--x-drawer-size`           | `20rem`                                              | *(same)*                        | Panel width (left/right) or height (top/bottom)   |
| `--x-drawer-bg`             | `Canvas`                                             | `#1c1d24`                       | Panel background                                  |
| `--x-drawer-fg`             | `CanvasText`                                         | `#e2e4ef`                       | Panel foreground                                  |
| `--x-drawer-backdrop`       | `rgb(0 0 0 / 0.4)`                                   | `rgb(0 0 0 / 0.55)`             | Scrim color                                       |
| `--x-drawer-shadow`         | `0 8px 24px rgb(0 0 0 / 0.18)`                       | `0 8px 40px rgb(0 0 0 / 0.55)`  | Panel box shadow                                  |
| `--x-drawer-duration`       | `200ms`                                              | *(same)*                        | Slide animation duration                          |
| `--x-drawer-easing`         | `ease`                                               | *(same)*                        | Slide animation easing                            |
| `--x-drawer-z`              | `1000`                                               | *(same)*                        | z-index base (panel is z+1)                       |
| `--x-drawer-header-padding` | `1rem 1.25rem`                                       | *(same)*                        | Header slot wrapper padding                       |
| `--x-drawer-body-padding`   | `1rem 1.25rem`                                       | *(same)*                        | Body slot wrapper padding                         |
| `--x-drawer-footer-padding` | `0.75rem 1.25rem`                                    | *(same)*                        | Footer slot wrapper padding                       |
| `--x-drawer-border`         | `color-mix(in srgb, currentColor 12%, transparent)`  | `rgb(255 255 255 / 0.08)`       | Separator border between header/body/footer       |

## Animation

The panel slides in from its placement edge:

- `left`: `translateX(-100%)` → `translateX(0)`
- `right`: `translateX(100%)` → `translateX(0)`
- `top`: `translateY(-100%)` → `translateY(0)`
- `bottom`: `translateY(100%)` → `translateY(0)`

Animation is disabled when `prefers-reduced-motion: reduce` is set.

## Theming

In light mode, the panel uses the CSS system colors `Canvas` and `CanvasText`. In dark mode, the component overrides those with curated values: a deep blue-gray surface (`#1c1d24`), a warm off-white foreground (`#e2e4ef`), a deeper backdrop scrim, and a stronger box shadow for elevation. All values can be overridden via CSS custom properties on the element or any ancestor.

## Accessibility

- The panel has `role="dialog"` and `aria-modal="true"`.
- `aria-label` on the panel is set from the `label` attribute.
- The panel has `aria-hidden="true"` when closed, so assistive technology cannot navigate into off-screen drawer content. `aria-hidden` is removed when the drawer opens.
- Focus is trapped inside the panel while it is open: Tab cycles forward through focusable elements, Shift+Tab cycles backward.
- On open, focus moves to the first focusable element in the drawer (or the panel itself if none exist).
- On close, focus returns to the element that was focused before the drawer opened.
- Pressing Escape closes the drawer.
- Clicking the backdrop closes the drawer.

## Responsive

Panel max-width uses `100%` instead of `100vw` to avoid scrollbar-induced overflow on mobile. Vertical panels use `100dvh` for correct height on mobile browsers with dynamic toolbars.

---

## Usage Examples

### Basic usage

```html
<x-drawer id="my-drawer">
  <div slot="header">
    <span>Task Details</span>
    <button onclick="document.getElementById('my-drawer').hide()">✕</button>
  </div>
  <p>Drawer body content.</p>
  <div slot="footer">
    <button onclick="document.getElementById('my-drawer').hide()">Done</button>
  </div>
</x-drawer>

<button onclick="document.getElementById('my-drawer').show()">Open drawer</button>
```

### Left placement

```html
<x-drawer placement="left" label="Navigation">
  <nav slot="header">Navigation</nav>
  <ul>
    <li><a href="/">Home</a></li>
    <li><a href="/about">About</a></li>
  </ul>
</x-drawer>
```

### Custom size

```html
<x-drawer style="--x-drawer-size: 32rem;" placement="right">
  Wide drawer content
</x-drawer>
```

### Listening to events

```js
const drawer = document.querySelector('x-drawer');

drawer.addEventListener('x-drawer-toggle', (e) => {
  console.log('Drawer is now', e.detail.open ? 'open' : 'closed');
});

drawer.addEventListener('x-drawer-dismiss', (e) => {
  console.log('Dismissed by', e.detail.reason); // "escape" | "backdrop"
});
```

### Programmatic control

```js
drawer.show();    // open
drawer.hide();    // close
drawer.toggle();  // flip

drawer.open = true;   // same as show()
drawer.open = false;  // same as hide()

drawer.placement = 'bottom';
drawer.label = 'Filter options';
```
