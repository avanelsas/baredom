# x-morph-stack

A stateless web component for **continuous transformation between UI states**, where layout, shape, material, and content evolve together as one visual surface.

When the active state changes, matched elements morph (position, size, border-radius, color, text) into their new form using **FLIP + spring physics**, instead of switching abruptly. Useful for onboarding flows, product showcases, card-to-detail expansions, and any UI where the user's eye should track the same logical element across states.

- **Tag**: `x-morph-stack`
- **Engine**: FLIP (capture rects → swap → animate clones) driven by spring physics
- **Identity**: matched by `data-morph-id` across slotted state roots
- **Optional goo filter**: opt-in `goo` boolean attribute applies an SVG `feGaussianBlur` + `feColorMatrix` overlay to the ghost layer during transitions

## Authoring

```html
<x-morph-stack active-state="welcome">
  <section slot="state" data-state="welcome">
    <div data-morph-id="hero" class="hero">Welcome</div>
    <p   data-morph-id="lede">Get started in three steps.</p>
  </section>

  <section slot="state" data-state="profile">
    <div data-morph-id="hero" class="hero hero--small">Profile</div>
    <p   data-morph-id="lede">Tell us about yourself.</p>
  </section>

  <section slot="state" data-state="done">
    <div data-morph-id="hero" class="hero hero--badge">All set</div>
  </section>
</x-morph-stack>
```

```js
const stack = document.querySelector('x-morph-stack');
stack.next();              // welcome → profile
stack.goTo('done');        // profile → done
stack.activeState = 'welcome';
```

### Authoring rules

- Each state root **must** be slotted into `slot="state"` and carry `data-state="NAME"` (unique within the stack).
- Matched children carry `data-morph-id="ID"`. The same id in two states means the same logical element — its rect, border-radius, background, color, and text are interpolated.
- **Do not nest morph-ids.** A `data-morph-id` descendant of another `data-morph-id` causes visual double-movement.
- Avoid `display: ... !important` on state roots; the component sets `display:none`/`""` inline to toggle visibility.
- Text crossfade only applies when the matched node has a single text-node child. Complex children fall back to "snap at midpoint."

## Attributes

| Attribute | Type | Default | Description |
|---|---|---|---|
| `active-state` | string | first state's `data-state` | Active state by name (preferred over index) |
| `active-index` | number | `0` | Numeric fallback when `active-state` is absent or unknown |
| `stiffness` | number 10–500 | `170` | Spring tension |
| `damping` | number 1–100 | `26` | Spring friction |
| `mass` | number 0.1–10 | `1.0` | Spring mass |
| `goo` | boolean | absent | Apply gooey SVG filter to ghost layer during transitions |
| `disabled` | boolean | absent | Skip animation; instant swap |

The host mirrors the resolved active name as `data-active-state="NAME"` for external CSS hooks.

## Properties

All attributes are mirrored as JavaScript properties:

| Property | Type |
|---|---|
| `activeState` | `string \| null` |
| `activeIndex` | `number \| null` |
| `stiffness`   | `number \| null` |
| `damping`     | `number \| null` |
| `mass`        | `number \| null` |
| `goo`         | `boolean` |
| `disabled`    | `boolean` |

## Methods

| Method | Description |
|---|---|
| `goTo(name)` | Transition to the named state. No-op if name is unknown. |
| `next()` | Advance to the next state, wrapping at the end. |
| `prev()` | Step to the previous state, wrapping at the start. |
| `states()` | Return a JS array of state names in slot order. |

## Events

Both events bubble and cross shadow boundaries (`bubbles: true, composed: true`).

| Event | When | `detail` | Cancelable |
|---|---|---|---|
| `x-morph-stack-change` | After old rects captured, before swap | `{from, to, reason}` (reason = `"attribute"` \| `"method"`) | yes — `preventDefault()` aborts |
| `x-morph-stack-changed` | After all springs settle (or immediately, when reduced motion / `disabled`) | `{from, to}` | no |

Initial connection (first state appearing) does **not** fire events.

A mid-flight transition that is interrupted by a new `goTo`/attribute change is dropped silently — only the latest transition fires `x-morph-stack-changed`.

## Slots

| Slot | Description |
|---|---|
| `state` | Each state root must be slotted into `state` and carry `data-state="NAME"`. |

## CSS custom properties

| Variable | Default | Purpose |
|---|---|---|
| `--x-morph-stack-spring-stiffness` | `170` | Documentation only — HTML attribute wins |
| `--x-morph-stack-spring-damping`   | `26`  | Documentation only |
| `--x-morph-stack-spring-mass`      | `1`   | Documentation only |
| `--x-morph-stack-fade-ms`          | `160ms` | Reserved for reduced-motion fade fallback |
| `--x-morph-stack-goo-blur`         | `10`  | Documentation only — informs the goo SVG filter setup |
| `--x-morph-stack-goo-threshold`    | `18`  | Documentation only |
| `--x-morph-stack-bg`               | `transparent` | Background colour of the viewport |

## CSS parts

| Part | Description |
|---|---|
| `viewport` | Holds the visible state (the slot lives inside). Style via `x-morph-stack::part(viewport)`. |

> The ghost layer and goo SVG used during transitions are appended to `document.body` (light DOM, not the shadow tree) so author CSS classes on cloned ghosts continue to apply. They are not exposed as `::part`s. Both carry `aria-hidden="true"` and `data-x-morph-stack-ghost-layer` if you need to target them from a global stylesheet.

## Accessibility

- The component is a passive layout container; it does **not** trap or move focus.
- It is the author's responsibility to manage focus when the active state changes. Listen for `x-morph-stack-changed` and focus the new state's primary control:

  ```js
  stack.addEventListener('x-morph-stack-changed', (e) => {
    const root = stack.querySelector(`[slot="state"][data-state="${e.detail.to}"]`);
    const target = root && root.querySelector('h1, h2, [autofocus], button, [tabindex]');
    if (target) target.focus();
  });
  ```
- The ghost layer is `aria-hidden="true"` so screen readers see only the live state, never the cloned ghosts.

## Reduced motion

When the user has `prefers-reduced-motion: reduce` set, or when the `disabled` attribute is present, the component skips the FLIP capture, the spring loop, and the goo filter. State changes apply visibility instantly, and `x-morph-stack-changed` fires on the next microtask.

## Spring tuning

Defaults match `react-spring`'s "default" preset and produce a pleasant snappy settle in roughly 400–700 ms.

```html
<x-morph-stack stiffness="120" damping="14" mass="1.2">…</x-morph-stack>  <!-- looser, wobbly -->
<x-morph-stack stiffness="300" damping="32" mass="0.8">…</x-morph-stack>  <!-- snappy, controlled -->
```

## Caveats

- `clip-path` interpolation only happens when both endpoints are the same shape function with matching arity (e.g., both `inset()` or both `circle()`); otherwise the value snaps at the midpoint.
- Color interpolation requires both endpoints to be `rgb()` / `rgba()` form (the value computed style returns). Named colors and `transparent` snap at midpoint.
- The SVG goo filter is opt-in via `goo` because the high-contrast alpha threshold can clip detailed content.
- The ghost layer is `position: fixed` and sized to the viewport. CSS `transform`/`perspective`/`filter` on an ancestor of `<body>` (or directly on `<html>`/`<body>`) breaks `position: fixed` containing-block semantics, which can misposition the cloned ghosts during a transition. Avoid such transforms on ancestors when using `x-morph-stack`.
