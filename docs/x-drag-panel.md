# `<x-drag-panel>`

A panel the user can pick up and move, with a pointer or the keyboard. Pairs with
[`<x-drop-zone>`](./x-drop-zone.md).

**The panel never moves itself.** Picking it up lifts a copy-free visual under the
pointer; releasing it over a zone makes that zone report a drop. Rearranging the
DOM is the application's job. See [Who owns the landing](#who-owns-the-landing).

```html
<x-drop-zone value="todo" accepts="task" label="Backlog">
  <x-drag-panel kind="task" value="t-104" label="Rewrite the parser">
    <span slot="header">Rewrite the parser</span>
    <p>Estimated 3 days.</p>
  </x-drag-panel>
</x-drop-zone>
```

## Why not HTML5 drag-and-drop

`dragstart` and `dataTransfer` never fire from touch input on iOS or Android. This
component is built on pointer events instead, so it works identically from 320px
up. There is no `draggable` attribute and no `DataTransfer` payload — the panel
carries its identity in `kind` and `value`.

## Who owns the landing

A drop is a *request*, not a fact. Your app may validate it locally and refuse,
queue it offline, or send it to a server that reorders differently. If the
component adopted the panel on drop, an app with its own data model would
immediately have two sources of truth.

So the contract is: the components sense and report, you move the node.

```js
document.addEventListener('x-drop-zone-drop', (e) => {
  const zone = e.target;
  const { panel, index } = e.detail;
  const siblings = [...zone.querySelectorAll(':scope > x-drag-panel')]
    .filter((c) => c !== panel);
  zone.insertBefore(panel, siblings[index] || null);
});
```

The detail also describes the move as pure data — `{ value, from, to, index }`,
where `from` and `to` are the two zones' `value`s — so a server-backed handler
never has to touch an element at all. See
[handling a drop](./x-drop-zone.md#handling-a-drop).

## The in-flight window

For a server-authoritative board, reserve the position while the request is in
flight and release it when the answer arrives. The panel renders an empty dashed
footprint that keeps its box, so the source list does not reorder until the
server has agreed.

```js
document.addEventListener('x-drop-zone-drop', async (e) => {
  const zone = e.target;
  const { value, to, index, panel } = e.detail;

  zone.reserve(panel, index);
  const ok = await api.move(value, to, index);
  if (ok) applyMove(zone, panel, index);
  zone.release();
});
```

`pending` is still a plain attribute you can set yourself;
[`reserve()` / `release()`](./x-drop-zone.md#methods) are sugar over it that set
the panel and the zone together, so there is one call each way instead of six
property writes to keep in step.

Nothing times out. If the answer never comes, the footprint stays visible and
keyboard-reachable, so a stalled move reads as stuck rather than as a lost card.

## Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `kind` | string | `""` | Payload type, matched against a zone's `accepts`. An empty kind is matched only by a zone with no `accepts`. |
| `value` | string | `""` | Identity key echoed in every event detail. Effectively required for server-backed use — it is what survives a re-render that recreates the element. |
| `label` | string | `"Draggable panel"` | Accessible name for the handle, and the name used in narration. |
| `grab` | string | `"handle"` | `handle` drags from the handle bar only; `surface` drags from anywhere on the card. Unrecognised values fall back to `handle`. |
| `auto-scroll` | string | `"auto"` | `auto` scrolls the nearest scrollable ancestors when dragged toward an edge; `none` disables it. |
| `disabled` | boolean | absent | Not draggable, not focusable. |
| `pending` | boolean | absent | Renders the footprint and suppresses dragging. Author-set — never set by the component. |

### State attributes

| Attribute | Meaning |
|-----------|---------|
| `data-dragging` | A drag is armed and in progress. Set by the component; style-only. |

## Properties

| Property | Type | Reflects |
|----------|------|----------|
| `kind` | `string` | `kind` |
| `value` | `string` | `value` |
| `label` | `string` | `label` |
| `grab` | `string` | `grab` |
| `autoScroll` | `string` | `auto-scroll` |
| `disabled` | `boolean` | `disabled` |
| `pending` | `boolean` | `pending` |

## Events

Both bubble and cross shadow boundaries. Neither is cancelable — there is no
default action to prevent.

| Event | Detail | Fires when |
|-------|--------|------------|
| `x-drag-panel-drag-start` | `{ kind, value }` | A drag arms. |
| `x-drag-panel-drag-cancel` | `{ kind, value, reason }` | The gesture ends without a drop. |

`reason` is `"escape"` (Escape pressed, or the pointer was cancelled) or
`"no-zone"` (released over nothing that accepts it).

A completed drop is announced by the zone as
[`x-drop-zone-drop`](./x-drop-zone.md#events) — the zone is the element that knows
the insertion index and that survives a confirmation re-render.

## Methods

None.

## Slots

| Slot | Content |
|------|---------|
| `header` | Title area inside the handle bar. |
| *(default)* | Panel body. |

## Parts

| Part | Element |
|------|---------|
| `panel` | Outer surface. Lifts during a drag. |
| `handle` | Grab bar. The drag target when `grab="handle"`. |
| `grip` | Grip glyph, `aria-hidden`. |
| `body` | Wrapper around the default slot. |

The footprint is a state of the surface, styled through
`:host([pending]) [part=panel]` rather than being a part of its own.

## CSS custom properties

| Property | Purpose |
|----------|---------|
| `--x-drag-panel-bg` | Surface background. |
| `--x-drag-panel-color` | Text colour. |
| `--x-drag-panel-border` | Resting border. |
| `--x-drag-panel-radius` | Corner radius. |
| `--x-drag-panel-padding` | Handle and body padding. |
| `--x-drag-panel-shadow` | Resting shadow. |
| `--x-drag-panel-lift-shadow` | Shadow while dragging. |
| `--x-drag-panel-lift-scale` | Scale while dragging. |
| `--x-drag-panel-handle-bg` | Handle bar background. |
| `--x-drag-panel-handle-height` | Handle bar minimum height. |
| `--x-drag-panel-grip-color` | Grip glyph colour. |
| `--x-drag-panel-footprint-border` | Dashed border while dragging or pending. |
| `--x-drag-panel-footprint-bg` | Fill while dragging or pending. |
| `--x-drag-panel-transition` | Lift and settle transition. |

A resting panel is an inline surface and uses `--x-color-surface`. A lifted panel
is temporarily an overlay and switches to `--x-color-bg`, so it stays opaque over
whatever it passes across.

## Opting content out of dragging

Buttons, links and form controls inside a panel never start a drag. Add
`data-no-drag` to opt any other element out — most useful in `grab="surface"`
mode, where the whole card is otherwise a grab target.

```html
<x-drag-panel grab="surface" kind="task" value="t-1">
  <span slot="header">Card</span>
  <p data-no-drag>Selectable text that will not start a drag.</p>
</x-drag-panel>
```

## Keyboard

| Key | Context | Effect |
|-----|---------|--------|
| <kbd>Tab</kbd> | — | Moves to the handle. |
| <kbd>Space</kbd> | Handle focused | Picks up. |
| <kbd>↑</kbd> <kbd>↓</kbd> <kbd>←</kbd> <kbd>→</kbd> | Picked up | Cycles accepting zones in document order, scrolling each into view. |
| <kbd>Enter</kbd> | Picked up | Drops at the end of the current zone, then moves focus to that zone. |
| <kbd>Esc</kbd> | Picked up | Cancels. |

A keyboard drop appends rather than choosing a position: there is no pointer to
derive an insertion point from, and cycling positions as well as zones would
double the key vocabulary. It emits the same `x-drop-zone-drop` detail as a
pointer drop, so your handler has one path, not two.

## Accessibility

- The handle is `role="button"` and stays at `tabindex="0"` except when
  `disabled`. A `pending` panel remains tab-reachable and carries
  `aria-busy="true"`, so a stalled move is discoverable.
- The panel narrates pickup and cancel into a visually hidden `aria-live` region,
  created lazily on first pickup so a fifty-card board carries no idle live
  regions. Zones narrate candidacy and resolution.
- Focus moves to the destination zone on commit rather than riding the panel: a
  confirmation re-render often replaces the panel element, which would drop focus
  to `<body>`.

## Motion

Under `prefers-reduced-motion: reduce` the lift transition is removed.
Auto-scroll is **not** disabled — it is function rather than decoration, and a
board deeper than the viewport would otherwise be unusable.

## Mobile

- Pointer events throughout; `touch-action: none` is scoped to the grab area.
- In `grab="surface"` mode a touch drag arms after a ~250ms press and cancels if
  the finger travels more than 8px first, so swiping over a card still scrolls
  the board. Mouse and pen arm immediately.
- Auto-scroll resolves the vertical and horizontal scrollers independently, so a
  board that scrolls sideways with columns that scroll down works in both axes at
  once.
