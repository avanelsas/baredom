# x-liquid-glass

A content container shaped as a shifting, translucent blob. Multiple overlapping ellipses are merged via an SVG goo filter (feGaussianBlur + feColorMatrix) to produce an organic, metaball-like boundary. The blob undergoes subtle noise-driven distortion, creating a "drop of water in zero gravity" effect. A frosted glass layer with backdrop blur, built-in mesh gradient, and optional pointer-following specular highlight complete the visual.

## Tag Name

```
x-liquid-glass
```

## Attributes

| Attribute            | Type    | Default | Range       | Description                                        |
|----------------------|---------|---------|-------------|----------------------------------------------------|
| `blobs`              | number  | 5       | 3 – 8       | Number of satellite ellipses around the core        |
| `speed`              | number  | 0.3     | 0.05 – 2.0  | Noise evolution speed                              |
| `amplitude`          | number  | 0.15    | 0.05 – 0.4  | Max satellite displacement as fraction of size     |
| `blur`               | number  | 12      | 0 – 40      | Backdrop filter blur in px (glass frosting)        |
| `goo`                | number  | 10      | 4 – 20      | SVG filter blur stdDeviation (blob merge softness) |
| `tint`               | string  | —       | CSS color   | Fill tint color (overrides CSS variable)           |
| `specular`           | boolean | false   | —           | Enable pointer-following specular highlight        |
| `specular-size`      | number  | 0.4     | 0.1 – 1.0   | Highlight radius as fraction of element size       |
| `specular-intensity` | number  | 0.3     | 0.0 – 1.0   | Highlight opacity                                  |
| `disabled`           | boolean | false   | —           | Freeze animation; show static blob                 |
| `mode`               | string  | surface | surface, submerged | Content positioning: above frost or behind it |
| `frost`              | number  | 0.5     | 0.0 – 1.0  | Frost thickness in submerged mode (0=clear, 1=heavy) |
| `color-1`            | string  | indigo  | CSS color   | First mesh gradient color                          |
| `color-2`            | string  | pink    | CSS color   | Second mesh gradient color                         |

## Properties

| Property            | Type    | Attribute            | Description                     |
|---------------------|---------|----------------------|---------------------------------|
| `blobs`             | number  | `blobs`              | Satellite count                 |
| `speed`             | number  | `speed`              | Noise speed                     |
| `amplitude`         | number  | `amplitude`          | Displacement amplitude          |
| `blur`              | number  | `blur`               | Backdrop blur                   |
| `goo`               | number  | `goo`                | Goo filter strength             |
| `tint`              | string  | `tint`               | Fill tint color                 |
| `specular`          | boolean | `specular`           | Specular highlight enabled      |
| `specularSize`      | number  | `specular-size`      | Highlight radius                |
| `specularIntensity` | number  | `specular-intensity` | Highlight opacity               |
| `disabled`          | boolean | `disabled`           | Animation frozen                |
| `mode`              | string  | `mode`               | "surface" or "submerged"        |
| `frost`             | number  | `frost`              | Frost thickness (0.0–1.0)       |
| `color1`            | string  | `color-1`            | First gradient color            |
| `color2`            | string  | `color-2`            | Second gradient color           |

## Slots

| Slot      | Description                  |
|-----------|------------------------------|
| (default) | Content displayed inside blob|

## CSS Custom Properties

| Property                          | Default (light)                           | Default (dark)                            |
|-----------------------------------|-------------------------------------------|-------------------------------------------|
| `--x-liquid-glass-tint`          | `rgba(255,255,255,0.15)`                  | `rgba(0,0,0,0.2)`                        |
| `--x-liquid-glass-border`       | `rgba(255,255,255,0.3)`                   | `rgba(255,255,255,0.1)`                   |
| `--x-liquid-glass-border-width` | `1.5`                                     | `1.5`                                     |
| `--x-liquid-glass-shadow`       | `0 8px 32px rgba(0,0,0,0.1)`             | `0 8px 32px rgba(0,0,0,0.3)`             |
| `--x-liquid-glass-padding`      | `1.5rem`                                  | `1.5rem`                                  |
| `--x-liquid-glass-specular-color`| `rgba(255,255,255,0.6)`                   | `rgba(255,255,255,0.3)`                   |
| `--x-liquid-glass-gradient-1`   | indigo radial gradient                    | indigo radial gradient (dimmer)           |
| `--x-liquid-glass-gradient-2`   | pink radial gradient                      | pink radial gradient (dimmer)             |
| `--x-liquid-glass-spring`       | `linear()` spring easing                  | `linear()` spring easing                  |
| `--x-liquid-glass-spring-duration`| `600ms`                                  | `600ms`                                   |

## Shadow Parts

| Part        | Element   | Description                           |
|-------------|-----------|---------------------------------------|
| `svg`       | `<svg>`   | SVG container with goo filter + blobs |
| `glass`     | `<div>`   | Glass layer with backdrop-filter      |
| `gradient`  | `<div>`   | Mesh gradient background layer        |
| `specular`  | `<div>`   | Specular highlight overlay            |
| `grain`     | `<div>`   | Frosted glass texture overlay         |
| `content`   | `<div>`   | Content wrapper with slot             |

## Accessibility

- The SVG and specular overlay are marked `aria-hidden="true"` (purely decorative).
- `prefers-reduced-motion: reduce` freezes satellites at rest positions; no animation loop runs.
- Light/dark mode adapts automatically via `prefers-color-scheme`.
- All transitions use CSS `linear()` spring easing for physically natural motion.

## Usage

### Basic

```html
<x-liquid-glass>
  <h3>Liquid Glass Card</h3>
  <p>Place me over interesting content for the full glass effect.</p>
</x-liquid-glass>
```

### With Specular Highlight

```html
<x-liquid-glass specular specular-intensity="0.5">
  <p>Move your pointer over me to see the light follow.</p>
</x-liquid-glass>
```

### Custom Blob Physics

```html
<x-liquid-glass blobs="7" speed="0.8" amplitude="0.25" goo="15">
  <p>More satellites, faster, wobblier, goopier.</p>
</x-liquid-glass>
```

### Disabled

```html
<x-liquid-glass disabled>
  <p>Static blob — no animation.</p>
</x-liquid-glass>
```

### Submerged Content

```html
<x-liquid-glass mode="submerged" specular>
  <h3>Behind the Glass</h3>
  <p>Content is visible through the frosted glass — like text in ice.</p>
</x-liquid-glass>
```

### Custom Gradient Colors

```html
<x-liquid-glass color-1="rgba(0,200,100,0.15)" color-2="rgba(255,165,0,0.12)">
  <p>Green and orange mesh gradient</p>
</x-liquid-glass>
```

### Styled

```html
<x-liquid-glass style="
  --x-liquid-glass-tint: rgba(99,102,241,0.2);
  --x-liquid-glass-border: rgba(99,102,241,0.4);
  --x-liquid-glass-shadow: 0 8px 32px rgba(99,102,241,0.2);
">
  <p>Indigo-tinted glass blob</p>
</x-liquid-glass>
```

### JavaScript

```js
import '@vanelsas/baredom/x-liquid-glass';

const blob = document.querySelector('x-liquid-glass');
blob.blobs = 7;
blob.speed = 0.5;
blob.specular = true;
blob.specularIntensity = 0.6;
```
