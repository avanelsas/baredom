# `<x-metaball-cursor>`

Organic blobs that follow the mouse cursor with varying degrees of latency. When blobs overlap, they stretch and fuse together like mercury drops using an SVG filter metaball technique. An optional Perlin noise border makes the edges shimmer continuously, even when the mouse is still.

## Tag

```html
<x-metaball-cursor></x-metaball-cursor>
```

The component should be placed inside a positioned container. It renders as `display: block` with `pointer-events: none`, so it won't interfere with page interactions.

## Attributes

| Attribute         | Type    | Default                                           | Description                                    |
|-------------------|---------|---------------------------------------------------|------------------------------------------------|
| `blob-count`      | number  | `5`                                               | Number of blobs (2–10)                         |
| `blob-size`       | number  | `40`                                              | Base blob diameter in px (10–200)              |
| `color`           | string  | `#6366f1`                                         | Blob fill color (any CSS color)                |
| `noise`           | boolean | absent                                            | Enables Perlin noise shimmer on edges          |
| `noise-scale`     | number  | `3`                                               | Turbulence scale (0.5–20)                      |
| `noise-speed`     | number  | `0.02`                                            | Shimmer animation speed (0.001–0.1)            |
| `noise-intensity` | number  | `6`                                               | Displacement amount in px (1–30)               |
| `blur`            | number  | `12`                                              | Gaussian blur radius for fusion effect (4–40)  |
| `threshold`       | string  | `1 0 0 0 0  0 1 0 0 0  0 0 1 0 0  0 0 0 20 -10` | SVG `feColorMatrix` values for alpha threshold |
| `palette`         | string  | absent                                            | Named preset or comma-separated CSS colors     |

## Properties

| Property         | Type    | Reflects attribute  |
|------------------|---------|---------------------|
| `blobCount`      | number  | `blob-count`        |
| `blobSize`       | number  | `blob-size`         |
| `color`          | string  | `color`             |
| `noise`          | boolean | `noise`             |
| `noiseScale`     | number  | `noise-scale`       |
| `noiseSpeed`     | number  | `noise-speed`       |
| `noiseIntensity` | number  | `noise-intensity`   |
| `blur`           | number  | `blur`              |
| `threshold`      | string  | `threshold`         |
| `palette`        | array \| null | `palette`     |

## Events

None — this component is purely visual and decorative.

## Slots

None.

## Parts

| Part       | Description                              |
|------------|------------------------------------------|
| `viewport` | Container div with the SVG filter applied |

## CSS Custom Properties

| Property                          | Default   | Description                    |
|-----------------------------------|-----------|--------------------------------|
| `--x-metaball-cursor-color`       | `#6366f1` | Blob fill color                |
| `--x-metaball-cursor-opacity`     | `1`       | Overall container opacity      |
| `--x-metaball-cursor-z-index`     | `9999`    | Stacking order                 |
| `--x-metaball-cursor-blend-mode`  | `normal`  | `mix-blend-mode` on viewport   |
| `--x-metaball-cursor-inset`       | `0`       | Viewport inset from host edges |

## Palette Presets

The `palette` attribute accepts a named preset or comma-separated CSS colors. When set, each blob gets a different color from the palette (cycling if there are more blobs than colors). When blobs fuse, their colors blend at the junction.

| Name       | Colors                                         | Vibe                 |
|------------|-------------------------------------------------|----------------------|
| `rainbow`  | `#ef4444, #f59e0b, #22c55e, #3b82f6, #a855f7`  | Full spectrum        |
| `ocean`    | `#06b6d4, #0ea5e9, #6366f1, #8b5cf6, #0d9488`  | Cool blues & teals   |
| `sunset`   | `#ef4444, #f97316, #eab308, #ec4899, #f43f5e`  | Warm reds & oranges  |
| `neon`     | `#22d3ee, #a78bfa, #f472b6, #34d399, #facc15`  | Vivid electric       |
| `ember`    | `#dc2626, #ea580c, #d97706, #b91c1c, #f59e0b`  | Fire tones           |

Custom colors: `palette="hotpink, cyan, gold"`

## How It Works

1. Blob divs (circles via `border-radius: 50%`) are positioned absolutely inside a viewport container.
2. Each blob follows the mouse via linear interpolation (lerp) in a `requestAnimationFrame` loop. The lead blob follows quickly (~0.3 speed factor), trailing blobs follow progressively slower.
3. An SVG filter is applied to the viewport container:
   - **`feGaussianBlur`** blurs all blob shapes together.
   - **`feColorMatrix`** thresholds the alpha channel — only areas where blurred blobs overlap (high combined alpha) survive, creating the organic fusion effect.
4. When noise is enabled, **`feTurbulence`** + **`feDisplacementMap`** distort the thresholded output. The turbulence seed advances each frame, creating constant edge shimmer.

## Tuning the Effect

- **Tighter fusion:** Increase `blur` (more overlap needed) or increase the alpha multiplier in `threshold` (e.g., `... 0 0 0 30 -15`).
- **Looser, softer blobs:** Decrease `blur` or lower the alpha multiplier.
- **More shimmer:** Increase `noise-intensity` and `noise-scale`.
- **Subtle shimmer:** Use low `noise-intensity` (1–3) with moderate `noise-scale`.

## Accessibility

- The component sets `aria-hidden="true"` and `role="presentation"` — it is purely decorative.
- `pointer-events: none` ensures it never blocks interaction.
- When `prefers-reduced-motion: reduce` is active, the animation loop does not start. Blobs remain static at position (0, 0).

## Examples

### Basic usage

```html
<div style="position: relative; height: 300px;">
  <x-metaball-cursor></x-metaball-cursor>
</div>
```

### Custom color and size

```html
<x-metaball-cursor color="#ec4899" blob-size="60" blob-count="3">
</x-metaball-cursor>
```

### With noise shimmer

```html
<x-metaball-cursor noise noise-intensity="10" noise-speed="0.03">
</x-metaball-cursor>
```

### CSS custom property override

```css
.hero:hover {
  --x-metaball-cursor-color: hotpink;
  --x-metaball-cursor-blend-mode: screen;
}
```
