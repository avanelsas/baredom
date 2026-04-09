# x-morph-stack

A stateless web component for **continuous transformation between UI states**, where layout, shape, material, and content evolve together as one visual surface.

When the active state changes, matched elements morph (position, size, border-radius, color, text) into their new form using **FLIP + spring physics**, instead of switching abruptly. Useful for onboarding flows, product showcases, card-to-detail expansions, and any UI where the user's eye should track the same logical element across states.

- **Tag**: `x-morph-stack`
- **Engine**: FLIP (capture rects → swap → animate clones) driven by spring physics
- **Identity**: matched by `data-morph-id` across slotted state roots
- **Variants**: a single `variant` attribute (`clean` | `organic` | `liquid`) selects a coherent bundle of spring + SVG-gooey-filter tuning

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
| `stiffness` | number 10–500 | from `variant` | Spring tension. Explicit value overrides the variant. |
| `damping` | number 1–100 | from `variant` | Spring friction. Explicit value overrides the variant. |
| `mass` | number 0.1–10 | from `variant` | Spring mass. Explicit value overrides the variant. |
| `variant` | `"clean"` \| `"organic"` \| `"liquid"` | `"clean"` | High-level preset bundling spring tuning and gooey-filter behaviour. Unknown / mis-cased values fall back to `"clean"`. |
| `duration` | number (ms) | absent | Stretch / compress the spring's natural settle time to the requested value while preserving its character (bounce, overshoot). Absent / non-positive → use the spring's natural time. |
| `disabled` | boolean | absent | Skip animation; instant swap |

The host mirrors the resolved active name as `data-active-state="NAME"` and the resolved variant as `data-variant="NAME"` for external CSS hooks.

## Properties

All attributes are mirrored as JavaScript properties:

| Property | Type |
|---|---|
| `activeState` | `string \| null` |
| `activeIndex` | `number \| null` |
| `stiffness`   | `number \| null` |
| `damping`     | `number \| null` |
| `mass`        | `number \| null` |
| `variant`     | `"clean" \| "organic" \| "liquid"` (always returns the normalised value; setting `null` removes the attribute and reads back as `"clean"`) |
| `duration`    | `number \| null` (ms; setting `null` removes the attribute and the spring runs at its natural time) |
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
| `--x-morph-stack-spring-stiffness` | `170` | Spring tension. Set per-variant via `:host([data-variant=…])` rules; the HTML `stiffness` attribute (when present) wins over both. |
| `--x-morph-stack-spring-damping`   | `26`  | Spring friction. Variant + attribute precedence as above. |
| `--x-morph-stack-spring-mass`      | `1`   | Spring mass. Variant + attribute precedence as above. |
| `--x-morph-stack-fade-ms`          | `160ms` | Reserved for reduced-motion fade fallback. |
| `--x-morph-stack-goo-blur`         | `10`  | `feGaussianBlur stdDeviation` applied to the gooey filter. Read from the host's computed style each time the filter starts, so author overrides and `:host([data-variant=…])` rules both take effect. |
| `--x-morph-stack-goo-threshold`    | `18`  | Alpha-channel multiplier in the gooey `feColorMatrix`. Read alongside `--x-morph-stack-goo-blur`. |
| `--x-morph-stack-bg`               | `transparent` | Background colour of the viewport. |

## Variants

The `variant` attribute selects a curated bundle of the CSS custom properties above and decides whether the SVG gooey filter is installed at all. Authors who need finer control can still set `stiffness` / `damping` / `mass` attributes (which beat the variant) or override the `--x-morph-stack-goo-*` custom properties from author CSS.

| Variant   | Goo filter | Stiffness | Damping | Mass | Goo blur | Goo threshold | Feel |
|-----------|:----------:|----------:|--------:|-----:|---------:|--------------:|------|
| `clean`   | off        | `280`     | `30`    | `0.7`| —        | —             | Snappy, critically damped, crisp edges, no overshoot |
| `organic` | on         | `180`     | `5`     | `1.0`| `6`      | `14`          | Severely under-damped (ζ ≈ 0.19) — large, unmistakable bounce/wobble around the target, softened edges |
| `liquid`  | on         | `50`      | `14`    | `2.0`| `26`     | `26`          | Slow viscous flow with a pronounced blob/surface-tension look between morphing elements |

```html
<x-morph-stack variant="liquid">…</x-morph-stack>

<!-- Variant tuning, but force a snappier spring: -->
<x-morph-stack variant="liquid" stiffness="180" damping="24">…</x-morph-stack>
```

> **Migration from `goo`**: the `goo` boolean attribute and `.goo` JS property have been removed. Use `variant="organic"` for a mild gooey effect or `variant="liquid"` for a strong one. Authors who previously combined `goo` with custom `stiffness`/`damping`/`mass` should pick the closest variant and keep their explicit attributes — those still override the variant's values.

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

The default `variant="clean"` produces a pleasant snappy settle in roughly 340 ms; `organic` takes around 1.8 s during which the spring overshoots and visibly oscillates back; `liquid` flows for around 1.8 s without bouncing. Reach for a different variant first; only set the low-level attributes when you need precise control.

```html
<x-morph-stack stiffness="120" damping="14" mass="1.2">…</x-morph-stack>  <!-- looser, wobbly -->
<x-morph-stack stiffness="300" damping="32" mass="0.8">…</x-morph-stack>  <!-- snappy, controlled -->
```

### Duration

If you want to stretch or compress a transition without re-deriving spring numbers, set `duration` (ms). The component computes the spring's natural settle time from `stiffness`/`damping`/`mass` and scales each tick's `dt` so the animation completes in approximately the requested time. Spring character — bounce, overshoot, decay shape — is preserved exactly; only time changes.

```html
<x-morph-stack variant="liquid" duration="2500">…</x-morph-stack>   <!-- slow, viscous, 2.5 s -->
<x-morph-stack variant="organic" duration="450">…</x-morph-stack>   <!-- still bouncy, but quick -->
```

`duration` composes with the variant and with explicit spring attributes: the spring numbers (from variant or attributes) decide *how* it moves, `duration` decides *how long* it takes. Absent or non-positive values fall back to the natural settle time.

## Caveats

- `clip-path` interpolation only happens when both endpoints are the same shape function with matching arity (e.g., both `inset()` or both `circle()`); otherwise the value snaps at the midpoint.
- Color interpolation requires both endpoints to be `rgb()` / `rgba()` form (the value computed style returns). Named colors and `transparent` snap at midpoint.
- The SVG goo filter is enabled by `variant="organic"` and `variant="liquid"`. Its high-contrast alpha threshold can clip detailed content, so prefer `variant="clean"` for text-heavy or icon-dense states.
- The ghost layer is `position: fixed` and sized to the viewport. CSS `transform`/`perspective`/`filter` on an ancestor of `<body>` (or directly on `<html>`/`<body>`) breaks `position: fixed` containing-block semantics, which can misposition the cloned ghosts during a transition. Avoid such transforms on ancestors when using `x-morph-stack`.
