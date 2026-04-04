# x-scroll-timeline

A scroll-driven timeline component that visualizes chronological entries along a vertical track with animated markers and progress fill. Entries activate as they scroll past a configurable trigger line. Supports straight and curved (SVG serpentine) track shapes, alternating or single-side layouts.

## Tag name

```
x-scroll-timeline
```

## Attributes

| Attribute | Type | Default | Description |
|---|---|---|---|
| `layout` | `"alternating"` \| `"left"` \| `"right"` | `"alternating"` | Entry positioning mode. `alternating` places entries on alternating sides of the track; `left` positions the track on the left with entries to the right; `right` positions the track on the right with entries to the left. |
| `track` | `"straight"` \| `"curved"` | `"straight"` | Track rendering mode. `straight` uses a CSS line; `curved` renders an SVG serpentine path. |
| `threshold` | number `[0, 1]` | `0.5` | Viewport fraction where the trigger line sits. An entry becomes active when it spans this line. |
| `no-progress` | boolean | absent | When present, disables the progress fill on the track. Markers still activate. |
| `disabled` | boolean | absent | Freezes all scroll behavior. No events fire, no entries activate. |
| `label` | string | `""` | Accessible label applied as `aria-label` on the container. |
| `marker` | `"dot"` \| `"ring"` \| `"none"` | `"dot"` | Marker style. `dot` is a filled circle; `ring` is an outlined circle; `none` hides markers. |
| `autoplay` | boolean | absent | When present, enables automatic scrolling at a configurable speed. |
| `autoplay-speed` | number `[1, 1000]` | `50` | Scroll speed in pixels per second when autoplay is enabled. |
| `autoplay-loop` | boolean | absent | When present, scrolls back to the component top when the page bottom is reached. |
| `autoplay-indicator` | boolean | absent | When present, shows a pause icon overlay when autoplay is paused by user interaction. |

## Properties (JavaScript)

| Property | Type | Read-only | Description |
|---|---|---|---|
| `layout` | string | no | Reflects the `layout` attribute |
| `track` | string | no | Reflects the `track` attribute |
| `threshold` | number | no | Reflects the `threshold` attribute |
| `noProgress` | boolean | no | Reflects the `no-progress` attribute |
| `disabled` | boolean | no | Reflects the `disabled` attribute |
| `label` | string | no | Reflects the `label` attribute |
| `marker` | string | no | Reflects the `marker` attribute |
| `activeIndex` | number | yes | Index of the currently active entry, or `-1` if none |
| `progress` | number | yes | Overall scroll progress `[0, 1]` |
| `autoplay` | boolean | no | Reflects the `autoplay` attribute |
| `autoplaySpeed` | number | no | Reflects the `autoplay-speed` attribute |
| `autoplayLoop` | boolean | no | Reflects the `autoplay-loop` attribute |
| `autoplayIndicator` | boolean | no | Reflects the `autoplay-indicator` attribute |
| `autoplayPaused` | boolean | yes | Whether autoplay is currently paused by user interaction |

## Events

All events bubble and are composed (cross shadow DOM). None are cancelable.

| Event | Detail | Description |
|---|---|---|
| `x-scroll-timeline-entry-change` | `{ index, id, previousIndex, previousId }` | Fires when the active entry changes |
| `x-scroll-timeline-entry-enter` | `{ index, id, progress }` | Fires when an entry enters the trigger zone |
| `x-scroll-timeline-entry-leave` | `{ index, id, progress }` | Fires when an entry leaves the trigger zone |
| `x-scroll-timeline-progress` | `{ progress, activeIndex, activeId }` | Fires each frame while visible (throttled to >0.1% change) |
| `x-scroll-timeline-enter` | `{ progress }` | Component enters the viewport |
| `x-scroll-timeline-leave` | `{ progress }` | Component leaves the viewport |
| `x-scroll-timeline-autoplay-pause` | `{ progress, activeIndex, activeId }` | Autoplay paused by user interaction |
| `x-scroll-timeline-autoplay-resume` | `{ progress, activeIndex, activeId }` | Autoplay resumed after user interaction |

## Slots

| Slot | Description |
|---|---|
| (default) | Timeline entry children. Each child is a timeline entry. |

## Data attributes on children

### Set by the author

| Attribute | Description |
|---|---|
| `data-date` | Date or label text displayed alongside the entry's marker on the timeline track |
| `id` | Optional identifier, included in event detail objects |

### Set by the component

| Attribute | Description |
|---|---|
| `data-active` | Present on the currently active entry |
| `data-side` | `"left"` or `"right"` — which side of the track the entry is on |
| `data-index` | 0-based index of the entry |

### Set on the host by the component

| Attribute | Description |
|---|---|
| `data-autoplay-paused` | Present when autoplay is paused by user interaction |

## CSS parts

| Part | Description |
|---|---|
| `container` | Outer wrapper (has `role="feed"`) |
| `track` | Track column container |
| `track-line` | Unfilled straight track line |
| `track-fill` | Filled portion of straight track (progress) |
| `track-svg` | SVG element (curved mode) |
| `track-path` | Unfilled SVG path (curved mode) |
| `track-fill-path` | Filled SVG path (curved mode, progress) |
| `entries` | Entries wrapper containing the default slot |
| `indicator` | Pause icon overlay (visible when autoplay is paused and `autoplay-indicator` is set) |
| `live` | Hidden aria-live region |

## CSS custom properties

| Property | Default | Description |
|---|---|---|
| `--x-scroll-timeline-track-color` | `rgba(0,0,0,0.12)` | Unfilled track color |
| `--x-scroll-timeline-track-fill-color` | `#3b82f6` | Filled/progress track color |
| `--x-scroll-timeline-track-width` | `3px` | Track line thickness |
| `--x-scroll-timeline-marker-size` | `14px` | Marker diameter |
| `--x-scroll-timeline-marker-color` | `rgba(0,0,0,0.15)` | Inactive marker fill |
| `--x-scroll-timeline-marker-active-color` | `#3b82f6` | Active marker fill |
| `--x-scroll-timeline-marker-border-color` | `#3b82f6` | Marker border color |
| `--x-scroll-timeline-entry-gap` | `2rem` | Vertical spacing between entries |
| `--x-scroll-timeline-date-color` | `rgba(0,0,0,0.5)` | Date label text color |
| `--x-scroll-timeline-date-font-size` | `0.8125rem` | Date label font size |
| `--x-scroll-timeline-transition-duration` | `300ms` | Activation transition duration |
| `--x-scroll-timeline-curve-amplitude` | `60` | SVG curve horizontal amplitude (pixels, curved mode) |
| `--x-scroll-timeline-disabled-opacity` | `0.55` | Opacity when disabled |

## Accessibility

- The container has `role="feed"` with an `aria-label` from the `label` attribute.
- A hidden `aria-live="polite"` region announces "Entry N active" on entry changes.
- Respects `@media (prefers-reduced-motion: reduce)` by disabling all transitions and scroll tracking.
- Active entries receive `data-active` which can be targeted for screen reader announcements.
- When autoplay is active, the element receives `tabindex="0"` for keyboard control. Holding Space pauses autoplay; releasing Space resumes it. Mouse/touch interactions also pause while held and resume on release.
- Autoplay will not start if the user prefers reduced motion (`prefers-reduced-motion: reduce`).

## Usage

### Basic timeline

```html
<x-scroll-timeline label="Project history">
  <div data-date="Jan 2024">
    <h3>Project started</h3>
    <p>Initial planning and architecture design.</p>
  </div>
  <div data-date="Mar 2024">
    <h3>Alpha release</h3>
    <p>First internal release for testing.</p>
  </div>
  <div data-date="Jun 2024">
    <h3>Public launch</h3>
    <p>Official 1.0 release.</p>
  </div>
</x-scroll-timeline>
```

### Curved track with ring markers

```html
<x-scroll-timeline track="curved" marker="ring" layout="alternating">
  <div data-date="2023">Content</div>
  <div data-date="2024">Content</div>
  <div data-date="2025">Content</div>
</x-scroll-timeline>
```

### Left-aligned timeline

```html
<x-scroll-timeline layout="left">
  <div data-date="Step 1">Content</div>
  <div data-date="Step 2">Content</div>
</x-scroll-timeline>
```

### Autoplay with loop

```html
<x-scroll-timeline autoplay autoplay-speed="80" autoplay-loop autoplay-indicator
                   label="Auto-scrolling timeline">
  <div data-date="2023">Content</div>
  <div data-date="2024">Content</div>
  <div data-date="2025">Content</div>
</x-scroll-timeline>
```

### Listening for events

```javascript
const tl = document.querySelector('x-scroll-timeline');

tl.addEventListener('x-scroll-timeline-entry-change', e => {
  console.log('Active entry:', e.detail.index, e.detail.id);
});

tl.addEventListener('x-scroll-timeline-progress', e => {
  console.log('Progress:', e.detail.progress.toFixed(2));
});
```

### Custom styling

```css
x-scroll-timeline {
  --x-scroll-timeline-track-fill-color: #8b5cf6;
  --x-scroll-timeline-marker-active-color: #8b5cf6;
  --x-scroll-timeline-marker-size: 18px;
  --x-scroll-timeline-entry-gap: 3rem;
  --x-scroll-timeline-curve-amplitude: 80;
}
```
