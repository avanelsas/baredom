# x-floating-panel

A non-modal container that floats above page content at a viewport-fixed position and can be repositioned by dragging its handle bar or by pressing arrow keys while the handle has focus.

It is a **generic container**: it imposes a grab affordance and nothing else. Use it for inspectors, tool palettes, mini-players, dev docks, and any persistent surface the user should be able to move out of the way.

It is deliberately not a modal (`x-modal`), not anchored to a trigger (`x-popover`, `x-dropdown`), and not edge-docked (`x-drawer`).

## Tag

```html
<x-floating-panel open closable>
  <span slot="header">Inspector</span>
  <p>Any content.</p>
</x-floating-panel>
```

## Observed Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `open` | boolean | absent | Whether the panel is visible. |
| `x` | number | absent | Horizontal position in **viewport pixels**. Absent → the CSS default placement governs. |
| `y` | number | absent | Vertical position in **viewport pixels**. Absent → the CSS default placement governs. |
| `closable` | boolean | absent | Renders the close button and enables `Escape` to dismiss. |
| `resizable` | boolean | absent | Lets the user resize the panel body with the native CSS resize grip. |
| `label` | string | `Floating panel` | Accessible name for the dialog. Ignored when the `header` slot has content. |
| `focus-on-open` | boolean | absent | Move focus to the handle bar when the panel opens. Off by default. |
| `step` | number | `10` | Distance in pixels an arrow key moves the panel. Non-numeric and non-positive values fall back to the default. |

**Invalid `x` / `y` values are treated as absent, not as `0`.** Coercing garbage to zero would teleport the panel into the viewport corner; falling through to the CSS default leaves it where the author last saw it.

## Properties

| Property | Type | Reflects | Description |
|----------|------|----------|-------------|
| `open` | `boolean` | `open` | |
| `x` | `number` | `x` | Returns `null` when the panel is unpositioned. Assigning `null` removes the attribute and restores the CSS default. |
| `y` | `number` | `y` | As `x`. |
| `closable` | `boolean` | `closable` | |
| `resizable` | `boolean` | `resizable` | |
| `label` | `string` | `label` | |
| `focusOnOpen` | `boolean` | `focus-on-open` | |
| `step` | `number` | `step` | Arrow-key distance in pixels. |

> The generated TypeScript declares `x` and `y` as `number`, matching every other numeric property in the library. At runtime they are `number | null`, and assigning `null` is the supported way to clear a position.

## Methods

| Method | Returns | Description |
|--------|---------|-------------|
| `show()` | `void` | Opens the panel. |
| `hide()` | `void` | Closes the panel. Programmatic — does **not** fire `x-floating-panel-dismiss-request`. |
| `toggle()` | `void` | Toggles the panel. |

## Events

| Event | Cancelable | Detail | Fired when |
|-------|-----------|--------|------------|
| `x-floating-panel-toggle` | no | `{ open: boolean }` | `open` changed. |
| `x-floating-panel-dismiss-request` | **yes** | `{ reason: "close-button" \| "escape" }` | Before a user-initiated close. Call `preventDefault()` to keep the panel open. |
| `x-floating-panel-move` | no | `{ x: number, y: number, source: "pointer" \| "keyboard" }` | Once when a drag ends, and once per keyboard nudge. **Never per animation frame.** |

A press that never moves is a click, not a drag: it emits no `move` and leaves an unpositioned panel unpositioned. An interrupted drag (`pointercancel`) still emits `move`, because earlier frames already moved the panel — the announced position and the DOM never disagree.

`x-floating-panel-move` is intentionally not cancelable: gating each position on a consumer round-trip would place a synchronous listener in the pointer-move path. Implement snapping or custom bounds by listening for `move` and writing `x` / `y` back.

## Slots

| Slot | Description |
|------|-------------|
| *(default)* | Panel body content. |
| `header` | Optional content inside the handle bar — a title, a toolbar, a status indicator. |

Everything in the `header` slot is part of the drag surface. Mark interactive children with **`data-no-drag`** and a press on them will not start a drag:

```html
<x-floating-panel open>
  <div slot="header">
    <span>Inspector</span>
    <button data-no-drag>Refresh</button>
  </div>
</x-floating-panel>
```

The built-in close button is excluded automatically.

## CSS Parts

| Part | Description |
|------|-------------|
| `panel` | The floating, positioned box. |
| `handle` | The drag/grab bar at the top. Focusable. |
| `close` | The close button. Only visible when `closable` is set. |
| `body` | The content region wrapping the default slot. |

## CSS Custom Properties

| Property | Default | Description |
|----------|---------|-------------|
| `--x-floating-panel-z` | `900` | Stacking order. Deliberately **below** `x-dropdown` / `x-modal` (`1000`) and `x-toaster` (`9000`) so a modal can cover the panel and a toast stays readable over it. |
| `--x-floating-panel-top` | `4rem` | Initial vertical placement while `y` is unset. |
| `--x-floating-panel-left` | `2rem` | Initial horizontal placement while `x` is unset. |
| `--x-floating-panel-width` | `22rem` | Panel width, capped at `calc(100vw - 2rem)`. |
| `--x-floating-panel-min-width` | `12rem` | Minimum width when `resizable`. |
| `--x-floating-panel-min-height` | `8rem` | Minimum height when `resizable`. |
| `--x-floating-panel-handle-size` | `1.25rem` | Minimum height of the handle bar. |
| `--x-floating-panel-bg` | `var(--x-color-bg, Canvas)` | Panel background. Always opaque — the panel floats over arbitrary content. |
| `--x-floating-panel-fg` | `var(--x-color-text, CanvasText)` | Text colour. |
| `--x-floating-panel-border` | `var(--x-color-border, …)` | Border and handle-bar divider. |
| `--x-floating-panel-radius` | `var(--x-radius-lg, 12px)` | Corner radius. |
| `--x-floating-panel-shadow` | `var(--x-shadow-lg, …)` | Drop shadow. |
| `--x-floating-panel-duration` | `var(--x-transition-duration, 150ms)` | Open/close transition. |
| `--x-floating-panel-accent` | `var(--x-color-primary, #2563eb)` | Focus-ring colour. |

## Positioning

`x` and `y` are **viewport coordinates**, matching `position: fixed`. Setting them in markup places the panel on first paint with no measure-then-place flash:

```html
<x-floating-panel open x="320" y="140">…</x-floating-panel>
```

While `x` / `y` are absent, the panel sits at `--x-floating-panel-top` / `--x-floating-panel-left`. The first drag or arrow-key nudge writes both attributes, after which they are authoritative.

**Clamping.** Positions are clamped so the panel always stays reachable: at least 64px remains on screen horizontally, and the handle bar remains fully on screen vertically. Clamping the *whole* panel would make a tall panel unusable on a short viewport, so the policy is deliberately weaker than "fully inside".

Clamping runs when the panel opens, when it connects, at the end of a drag, after a keyboard nudge, and on viewport resize. An author-set `x` / `y` is therefore honoured verbatim only while it is reachable — a panel parked at `x="700"` on a 360px phone is pulled back into view rather than being stranded off-screen with no handle to grab.

**Resizing.** `resizable` applies the native CSS `resize` grip to `[part=body]`. The resulting size lives in browser-managed inline style rather than in the component's model, so it is not part of the attribute-derived state and will not appear in an `x-trace-history` trace.

## Animation

Opening and closing cross-fade with a small translate and scale over `--x-floating-panel-duration`. Dragging is direct manipulation and is never transitioned — a transition on the offsets would make the panel lag the pointer.

Under `@media (prefers-reduced-motion: reduce)` the open/close transition is removed entirely.

## Theming

The panel consumes `--x-color-bg`, `--x-color-text`, `--x-color-border`, `--x-color-primary`, `--x-radius-lg`, `--x-radius-sm`, `--x-shadow-lg`, `--x-space-*`, and `--x-transition-duration` from `x-theme`, with self-contained fallbacks. It uses `--x-color-bg` rather than `--x-color-surface` because it floats over arbitrary page content and must stay opaque.

Dark mode is handled via `@media (prefers-color-scheme: dark)`.

## Accessibility

- `[part=panel]` carries `role="dialog"` with `aria-modal="false"` — the ARIA pattern for a non-modal dialog. It is always accessibly named: `aria-labelledby` points at the header-slot wrapper when that slot has content, otherwise `aria-label` comes from the `label` attribute.
- **The handle bar is keyboard-operable.** WCAG 2.2 SC 2.5.7 (Dragging Movements, AA) requires a single-pointer alternative to any drag operation. The handle is focusable (`tabindex="0"`, `role="button"`, `aria-label="Move panel"`); arrow keys move the panel by the `step` attribute (10px by default), and `Shift` + arrow always moves it by 1px.
- **The handle is not activated.** `Enter` and `Space` deliberately do nothing — there is no action to perform, only movement. A bare `role="button"` would promise an activation that never happens, so the handle carries `aria-roledescription="Drag handle"` (assistive technology announces "Move panel, drag handle" rather than "button") and `aria-keyshortcuts="ArrowUp ArrowDown ArrowLeft ArrowRight"` to advertise the keys that do work.
- **No focus trap.** The panel is non-modal, so `Tab` moves out of it normally and the rest of the page stays interactive.
- **No focus stealing by default.** Set `focus-on-open` for panels opened by an explicit user action; leave it off for ambient panels that must not interrupt typing. When `focus-on-open` is set, focus is restored to the previously focused element on close — but only if focus is still inside the panel at that moment.
- `Escape` dismisses only when `closable` is set. A panel with no close affordance that vanished on `Escape` while the user was typing in it would be hostile.
- The closed panel is `visibility: hidden`, which removes it from the accessibility tree.

## Responsive

- Width is capped with `min(var(--x-floating-panel-width), calc(100vw - 2rem))`, so the panel cannot overflow a 320px viewport.
- Height is capped at `calc(100dvh - 2rem)`; the body scrolls.
- Dragging uses pointer events throughout. `touch-action: none` is scoped to the handle bar only, so the panel body still scrolls normally on touch.

## Usage Examples

### Basic usage

```html
<x-floating-panel open>
  <p>I float above the page and can be dragged anywhere.</p>
</x-floating-panel>
```

### Titled and dismissible

```html
<x-floating-panel open closable label="Inspector">
  <span slot="header">Inspector</span>
  <p>Panel contents.</p>
</x-floating-panel>
```

### Explicit start position

```html
<x-floating-panel open x="480" y="120">
  <p>Placed on first paint — no flash.</p>
</x-floating-panel>
```

### Resizable

```html
<x-floating-panel open resizable style="--x-floating-panel-min-width: 16rem">
  <p>Drag the grip in the bottom corner to resize.</p>
</x-floating-panel>
```

### Persisting the position

```html
<x-floating-panel id="tools" open closable>…</x-floating-panel>

<script>
  const panel = document.getElementById('tools');
  const saved = JSON.parse(localStorage.getItem('tools-position') || 'null');
  if (saved) {
    panel.x = saved.x;
    panel.y = saved.y;
  }
  panel.addEventListener('x-floating-panel-move', (e) => {
    localStorage.setItem('tools-position', JSON.stringify({ x: e.detail.x, y: e.detail.y }));
  });
</script>
```

### Snapping to a grid

```js
panel.addEventListener('x-floating-panel-move', (e) => {
  const snap = (n) => Math.round(n / 20) * 20;
  panel.x = snap(e.detail.x);
  panel.y = snap(e.detail.y);
});
```

### Confirming before close

```js
panel.addEventListener('x-floating-panel-dismiss-request', (e) => {
  if (hasUnsavedChanges) {
    e.preventDefault();
    showConfirmation();
  }
});
```

### Programmatic control

```js
panel.show();
panel.toggle();
panel.hide();     // does not fire dismiss-request
```

## Notes

Multiple panels do not automatically raise to the front when clicked. Doing so needs a shared counter, which would reintroduce mutable state; order panels explicitly with `--x-floating-panel-z` instead.
