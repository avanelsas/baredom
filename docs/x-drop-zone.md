# `<x-drop-zone>`

A region that senses [`<x-drag-panel>`](./x-drag-panel.md) elements passing over
it, shows where one would land, and reports the drop.

**The zone never moves a node and never decides anything on its own.** Acceptance
is declared with `accepts` and `max`; the landing is the application's job.

```html
<x-drop-zone value="doing" accepts="task bug" label="In progress" max="5" animate-moves>
  <span slot="empty">Nothing in progress</span>
  <x-drag-panel kind="task" value="t-104" label="Rewrite the parser">
    <span slot="header">Rewrite the parser</span>
  </x-drag-panel>
</x-drop-zone>
```

> Not to be confused with the drop area inside
> [`<x-file-upload>`](./x-file-upload.md), which accepts files from the operating
> system. This zone accepts panels from the page.

## Handling a drop

The detail describes the whole move as data: which panel (`value`), between which
two zones (`from` → `to`), at what rank (`index`). Both endpoints are opaque
strings, so nothing you hold needs to survive a re-render.

```js
document.addEventListener('x-drop-zone-drop', (e) => {
  const { value, from, to, index, panel } = e.detail;
  // "move task `value` from `from` to `to`, at rank `index`"
  await submitWrite(value, to, index);
});
```

`index` is computed from the pointer against the measured positions of the zone's
current panels — the one thing the component knows that your app would otherwise
have to re-derive. A panel dragged within its own zone is excluded from the
measurement, so dropping it back where it started yields its original index and
you can cheaply no-op.

`panel` is the only element reference in the detail. It stays because the
synchronous DOM path needs a node to move and it cannot be derived:

```js
const siblings = [...zone.querySelectorAll(':scope > x-drag-panel')]
  .filter((c) => c !== panel);
zone.insertBefore(panel, siblings[index] || null);
```

The source zone element is deliberately *not* in the detail. Inside the handler
it is `panel.closest('x-drop-zone')`; after that, `from` is what you want, and
holding the element across a round trip is the bug `from` exists to prevent.

## The in-flight window

For a server-authoritative board, reserve the position when you send the request
and release it when the answer arrives:

```js
document.addEventListener('x-drop-zone-drop', async (e) => {
  const zone = e.target;
  const { value, to, index, panel } = e.detail;

  zone.reserve(panel, index);        // panel footprint + zone caret and busy tint
  const ok = await submitWrite(value, to, index);
  if (ok) applyMove(zone, panel, index);
  zone.release();                     // clears all three
});
```

`reserve()` sets `pending` on the panel and `pending` + `pending-index` on the
zone; `release()` clears them. Holding the *zone* across the await is safe — it
is the element that survives a confirmation re-render, which is the same reason
focus moves here on a keyboard drop.

Nothing in the layout moves during that window — the panel holds its box open as
a footprint, and this zone does not reflow. Every real layout change happens
once, when the truth arrives.

Reserving without an index gives the busy tint and no caret, which is the right
rendering for an unordered bucket.

`pending` and `pending-index` remain fully author-settable if you would rather
drive them yourself; `reserve()` and `release()` are sugar over exactly those
attributes. Either way the component never *infers* that a drop was sent — it
reserves only when you ask it to, having decided to send it.

A reservation does **not** count toward `max`: the panel has not arrived, and
counting it would refuse a second drop the server may well accept.

## Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `value` | string | `""` | Opaque identity key for this zone — a status, a column id, a bucket name. Echoed as `from` / `to` in the drop detail. |
| `accepts` | string | absent | Space-separated kinds. Absent accepts any panel — deliberately different from accepting nothing. |
| `max` | number | absent | Capacity, counted over `x-drag-panel` children. A full zone renders the reject cue. |
| `label` | string | `"Drop zone"` | Accessible name for the group, and the name used in narration. |
| `disabled` | boolean | absent | Never accepts, never renders cues. |
| `pending` | boolean | absent | Busy tint and `aria-busy`. Author-set. |
| `pending-index` | number | absent | Caret position during the in-flight window. A stale index parks the caret at the end rather than throwing. |
| `animate-moves` | boolean | absent | Animate children into place when the child list changes. |

### State attributes

| Attribute | Values | Meaning |
|-----------|--------|---------|
| `data-drag-state` | `over` \| `reject` | A compatible / incompatible panel is hovering. Absent otherwise. Set by the component; style-only. |

One valued attribute rather than two booleans, because the states are mutually
exclusive.

## Properties

| Property | Type | Reflects |
|----------|------|----------|
| `value` | `string` | `value` |
| `accepts` | `string` | `accepts` |
| `max` | `number` | `max` |
| `label` | `string` | `label` |
| `disabled` | `boolean` | `disabled` |
| `pending` | `boolean` | `pending` |
| `pendingIndex` | `number` | `pending-index` |
| `animateMoves` | `boolean` | `animate-moves` |

`max` and `pendingIndex` read as `null` when their attribute is absent. Assigning
`null` removes the attribute.

## Events

All bubble and cross shadow boundaries. None is cancelable — the app owns the
landing, so there is no default action to prevent.

| Event | Detail | Fires when |
|-------|--------|------------|
| `x-drop-zone-enter` | `{ kind, value }` | A dragged panel enters, whether accepted or rejected. |
| `x-drop-zone-leave` | `{ kind, value }` | A dragged panel leaves, including when dropped elsewhere. |
| `x-drop-zone-drop` | `{ kind, value, from, to, index, panel }` | An accepted panel is released over the zone, by pointer or by <kbd>Enter</kbd>. |

`value` is the panel's identity key; `from` and `to` are the source and
destination zones' `value`s; `index` is the rank within the destination; `panel`
is the `<x-drag-panel>` element.

`from` is `null` when the panel was dragged from outside any zone — distinct from
`""`, which means it came from a zone the author gave no `value`. The generated
TypeScript types it `string | null` so the check is not optional.

`enter` and `leave` fire for rejected panels too, so you can distinguish *hovered
but refused* from *never hovered* — useful for expanding a collapsed column when
a panel is dragged over it. A rejected release fires **no** drop event; the panel
emits `x-drag-panel-drag-cancel` with reason `"no-zone"` instead.

## Methods

| Method | Signature | Effect |
|--------|-----------|--------|
| `reserve` | `(panel: HTMLElement, index: number) => void` | Sets `pending` on `panel` and `pending` + `pending-index` on the zone. Pass a non-numeric `index` for busy-only. |
| `release` | `() => void` | Clears all three. Idempotent. |

A zone holds one reservation. Calling `reserve()` again clears the previous
panel's `pending` first, so a second drop cannot strand the first panel. A
`release()` whose panel was destroyed by a re-render is a harmless no-op — the
replacement never carried `pending` to begin with.

## Slots

| Slot | Content |
|------|---------|
| *(default)* | The panels. |
| `empty` | Empty-state content, shown only when the zone holds no panels. |

## Parts

| Part | Element |
|------|---------|
| `zone` | Outer region. Carries the hover, reject and busy states. |
| `caret` | Insertion caret. |
| `empty` | Wrapper around the `empty` slot. |

## CSS custom properties

| Property | Purpose |
|----------|---------|
| `--x-drop-zone-bg` | Resting background. |
| `--x-drop-zone-border` | Resting border. |
| `--x-drop-zone-radius` | Corner radius. |
| `--x-drop-zone-padding` | Inner padding. |
| `--x-drop-zone-gap` | Gap between panels. |
| `--x-drop-zone-min-height` | Minimum height, so an empty zone stays a target. |
| `--x-drop-zone-over-bg` | Background while a compatible panel hovers. |
| `--x-drop-zone-over-border` | Border while a compatible panel hovers. |
| `--x-drop-zone-reject-bg` | Background while an incompatible panel hovers. |
| `--x-drop-zone-reject-border` | Border while an incompatible panel hovers. |
| `--x-drop-zone-caret-color` | Caret colour. |
| `--x-drop-zone-caret-size` | Caret thickness. |
| `--x-drop-zone-busy-bg` | Tint while pending. |

A zone is an inline surface and uses `--x-color-surface`.

## Flow axis

The zone lays its panels out in a column by default. Restyling `::part(zone)`
into a row moves the caret onto the horizontal axis automatically — the axis is
read from the computed `flex-direction` rather than assumed.

```css
x-drop-zone::part(zone) {
  flex-direction: row;
  overflow-x: auto;
}
```

A single flow axis is assumed. A wrapping layout would need two-dimensional
hit-testing and is not supported.

## `animate-moves`

When present, the zone watches its own child list and animates panels from their
previous positions whenever it changes. One mechanism covers drop confirmations,
another user's change arriving over a websocket, and filter toggles.

This matters most for server-authoritative boards: without it, every confirmation
produces a visible jump as the source footprint disappears and this zone grows,
at unpredictable latency.

The animation runs through the Web Animations API, so the component never writes
to the `style` attribute of your elements. It is disabled entirely under
`prefers-reduced-motion: reduce`.

## Accessibility

- The host is `role="group"` with `aria-label` from `label`. Not `role="list"` —
  panels are not necessarily list items, and forcing the role would misdescribe
  most boards.
- While `pending`, the host carries `aria-busy="true"`.
- The zone narrates into a visually hidden `aria-live` region: it announces
  itself as a keyboard candidate ("Rewrite the parser over In progress, zone 2 of
  3"), and announces the resolution of a drop it received. Whether the move
  succeeded is read from the DOM — the panel is either a child now or it is not —
  so a server that refused is reported honestly.
- Focus moves here when a keyboard drop is committed.

## Motion

Under `prefers-reduced-motion: reduce` the hover transition and the pending caret
pulse are static, and `animate-moves` is disabled. The caret still renders.
