# `<x-kinetic-font>`

A kinetic typography component that interpolates Variable Font axes (`wght`, `wdth`, `slnt`, `opsz`) using spring physics driven by cursor proximity or scroll velocity. Text physically bulges, leans, stretches, or breathes in response to user behavior, relying solely on the browser's 2D text rendering engine. CPU-only -- no Canvas, WebGL, or GPU required.

## Tag

```html
<x-kinetic-font text="Hello World" mode="bulge" trigger="cursor"></x-kinetic-font>
```

## Attributes

| Attribute     | Type    | Default    | Description |
|---------------|---------|------------|-------------|
| `text`        | string  | `""`       | Text to display. |
| `trigger`     | enum    | `"cursor"` | Force source: `cursor`, `scroll`, or `both`. |
| `mode`        | tokens  | `"bulge"`  | Space-separated axis effects: `bulge`, `lean`, `stretch`, `breathe`. |
| `per-char`    | boolean | `false`    | When present, each character gets its own spring instance for spatial ripple effects. |
| `mass`        | number  | `1.0`      | Spring mass. Range [0.1, 10]. Higher = slower, more inertia. |
| `tension`     | number  | `170.0`    | Spring stiffness. Range [10, 500]. Higher = snappier. |
| `friction`    | number  | `26.0`     | Spring damping. Range [1, 100]. Higher = less oscillation. |
| `intensity`   | number  | `0.5`      | Force multiplier. Range [0, 1]. Controls how far axes interpolate. |
| `radius`      | number  | `200.0`    | Cursor proximity radius in pixels. Range [20, 1000]. |
| `font-family` | string  | (inherit)  | Variable font family override. |

## Modes

Each mode maps to a Variable Font axis. Multiple modes can be combined.

| Mode      | Axis   | Rest Value       | Excited Value    | Visual Effect |
|-----------|--------|------------------|------------------|---------------|
| `bulge`   | `wght` | `--*-weight-min` | `--*-weight-max` | Text gets bolder/thicker |
| `stretch` | `wdth` | `--*-width-min`  | `--*-width-max`  | Text gets wider |
| `lean`    | `slnt`  | `--*-slant-max`  | `--*-slant-min`  | Text tilts/italicises + CSS `skewX` transform for dramatic lean |
| `breathe` | `opsz` | `--*-opsz-min`   | `--*-opsz-max`   | Optical size shifts |

Example combining modes:

```html
<x-kinetic-font text="Fluid" mode="bulge lean" trigger="cursor"></x-kinetic-font>
```

## Properties (JS API)

| Property     | Type    | Reflects Attribute |
|--------------|---------|-------------------|
| `text`       | string  | `text`            |
| `trigger`    | string  | `trigger`         |
| `mode`       | string  | `mode`            |
| `perChar`    | boolean | `per-char`        |
| `mass`       | number  | `mass`            |
| `tension`    | number  | `tension`         |
| `friction`   | number  | `friction`        |
| `intensity`  | number  | `intensity`       |
| `radius`     | number  | `radius`          |
| `fontFamily` | string  | `font-family`     |

## Events

| Event                            | Detail | Description |
|----------------------------------|--------|-------------|
| `x-kinetic-font-spring-activate` | `{}`   | Fired when force is first applied and springs start moving. |
| `x-kinetic-font-spring-settle`   | `{}`   | Fired when all springs reach rest state. |

## CSS Custom Properties

| Property                         | Default              | Description |
|----------------------------------|----------------------|-------------|
| `--x-kinetic-font-color`        | `currentColor`       | Text colour. |
| `--x-kinetic-font-family`       | `system-ui, sans-serif` | Variable font family. |
| `--x-kinetic-font-size`         | `2rem`               | Font size. |
| `--x-kinetic-font-weight-min`   | `100`                | wght axis rest value. |
| `--x-kinetic-font-weight-max`   | `900`                | wght axis excited value. |
| `--x-kinetic-font-width-min`    | `75`                 | wdth axis rest value. |
| `--x-kinetic-font-width-max`    | `125`                | wdth axis excited value. |
| `--x-kinetic-font-slant-min`    | `-12`                | slnt axis excited value. |
| `--x-kinetic-font-slant-max`    | `0`                  | slnt axis rest value. |
| `--x-kinetic-font-opsz-min`     | `8`                  | opsz axis rest value. |
| `--x-kinetic-font-opsz-max`     | `144`                | opsz axis excited value. |
| `--x-kinetic-font-skew-max`     | `-15`                | Max CSS `skewX` angle (degrees) applied in `lean` mode. Set to `0` to disable. |

## CSS Parts

| Part        | Description |
|-------------|-------------|
| `container` | The text container element. |
| `char`      | Individual character span (per-char mode only). |
| `sr-only`   | Screen-reader-only text for accessibility. |

## Accessibility

- Sets `role="text"` and `aria-label` with the plain text content.
- Empty text sets `aria-hidden="true"`.
- A screen-reader-only element contains the full text.
- Respects `prefers-reduced-motion: reduce` -- animation is disabled and `font-variation-settings` resets to `normal`.

## Spring Physics

The component uses a damped spring model (semi-implicit Euler integration):

```
acceleration = (tension * (target - current) - friction * velocity) / mass
```

- **mass** controls inertia: higher values make the spring respond more slowly.
- **tension** controls stiffness: higher values make the spring snap back faster.
- **friction** controls damping: higher values reduce oscillation.

The spring drives a scalar displacement (0-1) that is mapped to font axis ranges via linear interpolation.

## Variable Font Requirements

This component requires a variable font loaded on the page. The font must support the axes used by your chosen modes. Common variable fonts:

- **Inter Variable** -- supports `wght`, `opsz`, `slnt`
- **Recursive** -- supports `wght`, `slnt`, `CASL`, `CRSV`, `MONO`
- **Roboto Flex** -- supports `wght`, `wdth`, `opsz`, `slnt`, `GRAD`

If the font does not support an axis, that axis is silently ignored by the browser.

## Usage Examples

### Basic cursor proximity (bulge)

```html
<x-kinetic-font text="Move your cursor here" mode="bulge" trigger="cursor"></x-kinetic-font>
```

### Per-character ripple

```html
<x-kinetic-font text="Ripple Effect" mode="bulge lean" trigger="cursor" per-char></x-kinetic-font>
```

### Scroll-driven stretch

```html
<x-kinetic-font text="Scroll Me" mode="stretch" trigger="scroll" intensity="0.8"></x-kinetic-font>
```

### Custom spring parameters

```html
<x-kinetic-font
  text="Bouncy"
  mode="bulge"
  trigger="cursor"
  mass="0.5"
  tension="300"
  friction="10"
></x-kinetic-font>
```

### Custom axis ranges via CSS

```css
x-kinetic-font {
  --x-kinetic-font-family: "Roboto Flex", sans-serif;
  --x-kinetic-font-weight-min: 300;
  --x-kinetic-font-weight-max: 700;
  --x-kinetic-font-size: 3rem;
}
```
