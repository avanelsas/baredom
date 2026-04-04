# x-scroll-story

A scrollytelling web component. A sticky media panel stays pinned while text/content sections ("steps") scroll past it. Each step triggers a state change event, allowing consumers to update the media in response.

## Tag name

```html
<x-scroll-story>
  <div slot="media"><!-- sticky media content --></div>
  <section>Step 1 content</section>
  <section>Step 2 content</section>
  <section>Step 3 content</section>
</x-scroll-story>
```

## Attributes

| Attribute   | Type    | Default  | Description |
|-------------|---------|----------|-------------|
| `layout`    | enum    | `"left"` | Media panel position: `"left"`, `"right"`, `"top"` |
| `threshold` | number  | `"0.5"`  | Viewport fraction (0–1) where a step becomes active. `0.5` = center of viewport |
| `split`     | number  | `"0.5"`  | Media/steps width ratio (0.1–0.9). Ignored when `layout="top"` |
| `disabled`  | boolean | `false`  | Freezes step tracking and suppresses events |
| `label`     | string  | `""`     | Accessible label for the region (`aria-label`) |
| `autoplay`  | boolean | `false`  | Enables automatic scrolling at a steady speed |
| `autoplay-speed` | number | `50` | Auto-scroll speed in pixels per second (clamped 1–1000) |
| `autoplay-loop`  | boolean | `false` | Loop back to the start when the story ends |
| `autoplay-indicator` | boolean | `false` | Show a pause icon overlay when auto-scroll is paused |

## Properties

| Property      | Type    | Reflects attribute | Notes |
|---------------|---------|-------------------|-------|
| `layout`      | string  | `layout`          | |
| `threshold`   | number  | `threshold`       | Parsed and clamped to [0,1] |
| `split`       | number  | `split`           | Parsed and clamped to [0.1,0.9] |
| `disabled`    | boolean | `disabled`        | |
| `label`       | string  | `label`           | |
| `activeIndex` | number  | —                 | Read-only. Index of active step (-1 if none) |
| `progress`    | number  | —                 | Read-only. Overall scroll progress [0,1] |
| `autoplay`          | boolean | `autoplay`          | |
| `autoplaySpeed`     | number  | `autoplay-speed`    | Parsed and clamped to [1,1000] |
| `autoplayLoop`      | boolean | `autoplay-loop`     | |
| `autoplayIndicator` | boolean | `autoplay-indicator`| |
| `autoplayPaused`    | boolean | —                   | Read-only. Whether auto-scroll is currently paused |

## Events

| Event | Bubbles | Composed | Cancelable | Detail |
|-------|---------|----------|------------|--------|
| `x-scroll-story-step-change` | yes | yes | no | `{ index, id, previousIndex, previousId }` |
| `x-scroll-story-step-enter`  | yes | yes | no | `{ index, id, progress }` |
| `x-scroll-story-step-leave`  | yes | yes | no | `{ index, id, progress }` |
| `x-scroll-story-progress`    | yes | yes | no | `{ progress, activeIndex, activeId }` |
| `x-scroll-story-enter`       | yes | yes | no | `{ progress }` |
| `x-scroll-story-leave`       | yes | yes | no | `{ progress }` |
| `x-scroll-story-autoplay-pause`  | yes | yes | no | `{ progress, activeIndex, activeId }` |
| `x-scroll-story-autoplay-resume` | yes | yes | no | `{ progress, activeIndex, activeId }` |

The `id` field is the step element's `id` attribute if present, otherwise `null`.

## Slots

| Slot | Description |
|------|-------------|
| `media` | Sticky media panel content |
| (default) | Step children. Each direct child is treated as one step. |

## Parts

| Part | Description |
|------|-------------|
| `container` | Outer flex wrapper |
| `media` | Sticky media panel |
| `steps` | Scrolling steps column |
| `live` | Visually-hidden aria-live announcement region |
| `indicator` | Pause icon overlay (visible only when `autoplay-indicator` is set and autoplay is paused) |

## CSS Custom Properties

| Property | Default | Description |
|----------|---------|-------------|
| `--x-scroll-story-media-width` | (from `split`) | Overrides the `split` attribute for media width |
| `--x-scroll-story-gap` | `0` | Gap between media and steps panels |
| `--x-scroll-story-step-min-height` | `80vh` | Minimum height of each step section |
| `--x-scroll-story-step-padding` | `2rem` | Padding inside each step |
| `--x-scroll-story-active-opacity` | `1` | Opacity of the active step |
| `--x-scroll-story-inactive-opacity` | `0.3` | Opacity of inactive steps |
| `--x-scroll-story-transition-duration` | `300ms` | Opacity transition duration |
| `--x-scroll-story-disabled-opacity` | `0.55` | Opacity when `disabled` is set |
| `--x-scroll-story-media-top` | `0` | Top offset for sticky media positioning |

## Step activation

The component computes a **trigger line** at `viewport-height * threshold` from the top of the viewport. A step is active when it spans this trigger line. If no step spans the line, the last step whose top is above it is active.

The component sets a `data-active` attribute on the active step child (light DOM) and removes it from the previous active step. It also sets a `data-active-index` attribute on the host element reflecting the current active step index. This allows consumer CSS targeting:

```css
x-scroll-story > section[data-active] {
  /* custom active styles */
}

x-scroll-story[data-active-index="0"] {
  /* styles when the first step is active */
}
```

## Autoplay

When the `autoplay` attribute is present the component automatically scrolls the page at a steady rate controlled by `autoplay-speed` (pixels per second, default 50). Auto-scrolling only runs while the component is in the viewport and respects the user's `prefers-reduced-motion` setting.

### Pause / resume

Users can temporarily pause auto-scrolling by:

- **Holding the mouse button** (or touch) on the component — releasing resumes.
- **Holding the Space key** while the component is focused — releasing resumes.

Moving the cursor outside the component while the mouse is held also resumes, preventing a stuck pause state.

Pause and resume fire `x-scroll-story-autoplay-pause` and `x-scroll-story-autoplay-resume` events respectively.

### Looping

When `autoplay-loop` is set the story instantly jumps back to its start once progress reaches the end, creating an infinite loop.

### Visual indicator

Set `autoplay-indicator` to display a subtle pause icon centered on the media panel whenever auto-scroll is paused.

### Interaction with `disabled`

Setting `disabled` stops auto-scrolling. Removing `disabled` restarts it if `autoplay` is still present.

## Accessibility

- Container has `role="region"` with `aria-label` from the `label` attribute.
- An `aria-live="polite"` region announces step changes to screen readers.
- `@media (prefers-reduced-motion: reduce)` disables opacity transitions.

## Usage

### HTML

```html
<x-scroll-story layout="left" threshold="0.5" split="0.4" label="Product story">
  <div slot="media">
    <img id="story-img" src="step1.jpg" alt="Product" />
  </div>
  <section id="intro">
    <h2>Introduction</h2>
    <p>Welcome to our product story...</p>
  </section>
  <section id="features">
    <h2>Features</h2>
    <p>Discover what makes this special...</p>
  </section>
  <section id="conclusion">
    <h2>Conclusion</h2>
    <p>Ready to get started?</p>
  </section>
</x-scroll-story>
```

### JavaScript

```js
const story = document.querySelector('x-scroll-story');

story.addEventListener('x-scroll-story-step-change', (e) => {
  const { index, id } = e.detail;
  const img = document.getElementById('story-img');
  img.src = `step${index + 1}.jpg`;
});
```

### Constraint

`position: sticky` requires that no ancestor between the component and its scroll container has `overflow: hidden`, `overflow: auto`, or `overflow: scroll`. Ensure the component is not inside an overflow-constrained container.
