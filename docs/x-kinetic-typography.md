# `<x-kinetic-typography>`

Animated typography that renders text along SVG paths with motion, visual effects, and dynamic sizing. Makes text feel alive with scrolling, bouncing, oscillating movement and combinable effects like opacity waves, per-character color waves, echo trails, and a Star Wars crawl mode.

## Tag

```html
<x-kinetic-typography text="Hello World" preset="wave" animation="scroll"></x-kinetic-typography>
```

## Attributes

| Attribute       | Type   | Default    | Description |
|-----------------|--------|------------|-------------|
| `text`          | string | `""`       | Text to render along the path. |
| `path`          | string | —          | Custom SVG path `d` string. Overrides `preset` when present. |
| `preset`        | string | `"wave"`   | Named path shape: `wave`, `circle`, `arc`, `infinity`, `spiral`, `sine`, `line`, `crawl`. |
| `animation`     | string | `"scroll"` | Path animation: `none`, `scroll`, `bounce`, `oscillate`. |
| `speed`         | string | `"1"`      | Speed multiplier. `"2"` = twice as fast, `"0.5"` = half speed. |
| `direction`     | string | `"normal"` | Animation direction: `normal`, `reverse`. |
| `effect`        | string | `"none"`   | Space-separated visual effects (see Effects section). |
| `font-size`     | string | —          | Base font size override (CSS value, e.g. `"32px"`, `"2rem"`). |
| `start-size`    | string | —          | Font size at path start for `size-gradient` effect. |
| `end-size`      | string | —          | Font size at path end for `size-gradient` effect. |
| `repeat`        | string | `"1"`      | Times to repeat text along the path. |
| `echo-count`    | string | `"0"`      | Number of echo/trail copies (0-5). |
| `echo-delay`    | string | `"0.3"`    | Delay in seconds between each echo. |
| `echo-opacity`  | string | `"0.5"`    | Opacity multiplier per echo. Echo _i_ gets `opacity^(i+1)`. |
| `echo-scale`    | string | `"0.85"`   | Font-size multiplier per echo. Echo _i_ gets `scale^(i+1)`. |

## Path Presets

| Preset     | Description |
|------------|-------------|
| `wave`     | Smooth horizontal sine wave (default). |
| `circle`   | Full circle path. |
| `arc`      | Half-circle arch. |
| `infinity` | Figure-eight / lemniscate. |
| `spiral`   | 1.5-turn Archimedean spiral. |
| `sine`     | Multi-period sine wave. |
| `line`     | Straight horizontal line. |
| `crawl`    | Star Wars opening crawl (CSS 3D perspective, not SVG). |

## Properties (JS API)

| Property      | Type   | Reflects Attribute |
|---------------|--------|--------------------|
| `text`        | string | `text`             |
| `path`        | string | `path`             |
| `preset`      | string | `preset`           |
| `animation`   | string | `animation`        |
| `speed`       | string | `speed`            |
| `direction`   | string | `direction`        |
| `effect`      | string | `effect`           |
| `fontSize`    | string | `font-size`        |
| `startSize`   | string | `start-size`       |
| `endSize`     | string | `end-size`         |
| `repeat`      | string | `repeat`           |
| `echoCount`   | string | `echo-count`       |
| `echoDelay`   | string | `echo-delay`       |
| `echoOpacity` | string | `echo-opacity`     |
| `echoScale`   | string | `echo-scale`       |

## CSS Custom Properties

| Property | Default | Description |
|----------|---------|-------------|
| `--x-kinetic-typography-color` | `currentColor` | Text fill color. |
| `--x-kinetic-typography-font-family` | `system-ui, sans-serif` | Font family. |
| `--x-kinetic-typography-font-size` | `24px` | Default font size. |
| `--x-kinetic-typography-font-weight` | `400` | Font weight. |
| `--x-kinetic-typography-letter-spacing` | `0` | Base letter spacing. |
| `--x-kinetic-typography-opacity` | `1` | Text opacity. |
| `--x-kinetic-typography-stroke` | `none` | Text stroke color. |
| `--x-kinetic-typography-stroke-width` | `0` | Text stroke width. |
| `--x-kinetic-typography-duration` | `10s` | Base animation cycle duration. |
| `--x-kinetic-typography-timing` | `linear` | Animation timing function. |
| `--x-kinetic-typography-color-shift-from` | `currentColor` | Color shift/wave start color. |
| `--x-kinetic-typography-color-shift-to` | `#3b82f6` | Color shift/wave end color. |
| `--x-kinetic-typography-path-stroke` | `none` | Decorative path line stroke. |
| `--x-kinetic-typography-path-stroke-width` | `0` | Decorative path line width. |
| `--x-kinetic-typography-crawl-perspective` | `400px` | 3D perspective depth for crawl mode. |

## CSS Parts

| Part             | Description |
|------------------|-------------|
| `container`      | Outer wrapper div. |
| `svg`            | The SVG element (hidden in crawl mode). |
| `text`           | The SVG `<text>` element. |
| `text-echo`      | Echo SVG `<text>` elements. |
| `path-line`      | Decorative visible path line. |
| `sr-only`        | Screen-reader-only text span. |
| `crawl-viewport` | Crawl mode perspective container (hidden in path mode). |
| `crawl-text`     | Crawl mode text div. |

## Visual Effects

Effects can be combined by space-separating tokens in the `effect` attribute.

| Effect            | Animates        | Description |
|-------------------|-----------------|-------------|
| `opacity-wave`    | `opacity`       | Fades text in and out. |
| `size-pulse`      | `font-size`     | Gently pulses text size. |
| `spacing-breathe` | `letter-spacing`| Expands and contracts letter spacing. |
| `color-shift`     | `fill`          | Transitions entire text between two colors. |
| `color-wave`      | per-character `fill` | Ripples color across individual characters with staggered delays. Uses the same `color-shift-from`/`color-shift-to` custom properties. |
| `size-gradient`   | per-character   | Applies a font-size gradient from `start-size` to `end-size`. Auto-enabled when both size attributes are set. |

**Mutual exclusions:**
- `size-gradient` and `size-pulse` — `size-gradient` wins.
- `color-wave` and `color-shift` — `color-wave` wins (it's a superset).

## Echo / Trail Effect

Add trailing copies of the text that follow the main animation with progressive fading and shrinking.

```html
<x-kinetic-typography
  text="Trailing echoes"
  preset="wave"
  animation="scroll"
  echo-count="3"
  echo-delay="0.3"
  echo-opacity="0.5"
  echo-scale="0.85">
</x-kinetic-typography>
```

With 3 echoes at default settings:
- Echo 1: opacity 0.5, scale 0.85
- Echo 2: opacity 0.25, scale 0.72
- Echo 3: opacity 0.125, scale 0.61

Echoes are SVG-only and are disabled in crawl mode.

## Star Wars Crawl (`preset="crawl"`)

Switches from SVG text-on-path to CSS 3D perspective rendering. Text scrolls vertically upward with a receding-into-distance effect.

```html
<x-kinetic-typography
  preset="crawl"
  animation="scroll"
  speed="0.3"
  text="A long time ago in a galaxy far, far away..."
  style="height:300px;
         --x-kinetic-typography-font-family:'Georgia',serif;
         --x-kinetic-typography-color:#fbbf24;
         --x-kinetic-typography-duration:30s;">
</x-kinetic-typography>
```

**What works in crawl mode:** `text`, `animation` (scroll/none), `speed`, `direction`, `color-shift` effect, font styling custom properties.

**What doesn't apply:** `path`, `repeat`, echo attributes, SVG-specific effects (`size-gradient`, `size-pulse`, `spacing-breathe`, `opacity-wave`, `color-wave`).

## Accessibility

- The SVG is `aria-hidden="true"`.
- A visually-hidden `<span>` contains the plain text for screen readers.
- The host has `role="img"` and `aria-label` set to the text content.
- When `text` is empty, the element has `role="presentation"` and `aria-hidden="true"`.
- All animations respect `prefers-reduced-motion: reduce`.

## Usage

### HTML

```html
<!-- Basic scrolling text on a wave -->
<x-kinetic-typography
  text="Hello World"
  preset="wave"
  animation="scroll">
</x-kinetic-typography>

<!-- Per-character color wave -->
<x-kinetic-typography
  text="Rainbow ripple"
  preset="wave"
  animation="scroll"
  effect="color-wave"
  style="--x-kinetic-typography-color-shift-from:#6366f1;
         --x-kinetic-typography-color-shift-to:#ec4899;">
</x-kinetic-typography>

<!-- Text with echo trail -->
<x-kinetic-typography
  text="Echo trail"
  preset="wave"
  animation="scroll"
  echo-count="3"
  echo-delay="0.3">
</x-kinetic-typography>

<!-- Star Wars crawl -->
<x-kinetic-typography
  preset="crawl"
  animation="scroll"
  speed="0.3"
  text="It is a period of civil war..."
  style="height:300px;--x-kinetic-typography-color:#fbbf24;">
</x-kinetic-typography>
```

### JavaScript

```javascript
const el = document.createElement('x-kinetic-typography');
el.text = 'Dynamic Text';
el.preset = 'infinity';
el.animation = 'scroll';
el.speed = '1.5';
el.effect = 'color-wave';
el.echoCount = '2';
document.body.appendChild(el);
```

### Theming

```css
x-kinetic-typography {
  --x-kinetic-typography-color: #6366f1;
  --x-kinetic-typography-font-family: 'Georgia', serif;
  --x-kinetic-typography-font-size: 32px;
  --x-kinetic-typography-font-weight: 700;
  --x-kinetic-typography-duration: 15s;
  --x-kinetic-typography-color-shift-from: #6366f1;
  --x-kinetic-typography-color-shift-to: #ec4899;
  --x-kinetic-typography-path-stroke: rgba(0,0,0,0.1);
  --x-kinetic-typography-path-stroke-width: 1;
}
```
