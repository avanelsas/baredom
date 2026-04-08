# x-liquid-fill

A scroll progress indicator that fills with animated liquid. The liquid surface has organic wave motion driven by SVG path morphing, reacts to scroll speed (gentle waves when steady, aggressive sloshing when fast), and splashes with inertia when scrolling stops suddenly. Supports themed gradients (gold, water, lava) with an SVG specular lighting filter for a metallic sheen.

## Tag Name

```
x-liquid-fill
```

## Attributes

| Attribute          | Type    | Default    | Range       | Description                                   |
|--------------------|---------|------------|-------------|-----------------------------------------------|
| `target`           | string  | (auto)     | CSS selector| Scroll container to observe. Auto = nearest scrollable ancestor or window |
| `orientation`      | string  | `vertical` | `vertical` / `horizontal` | Scroll direction to track             |
| `mode`             | string  | `fill`     | `fill` / `bar` | Full container fill vs thin progress bar     |
| `theme`            | string  | `gold`     | `gold` / `water` / `lava` / `custom` | Color preset         |
| `wave-intensity`   | number  | 0.5        | 0 – 1       | Base wave amplitude multiplier                |
| `splash-intensity` | number  | 0.7        | 0 – 1       | Splash/inertia effect strength                |
| `layers`           | number  | 3          | 2 – 5       | Number of overlapping wave layers             |
| `disabled`         | boolean | false      | —           | Disables animation and scroll tracking        |

## Properties

| Property          | Type    | Attribute          | Description                           |
|-------------------|---------|--------------------|---------------------------------------|
| `target`          | string  | `target`           | Scroll target CSS selector            |
| `orientation`     | string  | `orientation`      | Scroll direction                      |
| `mode`            | string  | `mode`             | Fill mode                             |
| `theme`           | string  | `theme`            | Color preset                          |
| `waveIntensity`   | number  | `wave-intensity`   | Wave amplitude multiplier             |
| `splashIntensity` | number  | `splash-intensity` | Splash effect strength                |
| `layers`          | number  | `layers`           | Number of wave layers                 |
| `disabled`        | boolean | `disabled`         | Disables animation                    |
| `progress`        | number  | (read-only)        | Current fill progress 0–1             |

## Events

| Event                      | Detail                                   | When                        |
|----------------------------|------------------------------------------|-----------------------------|
| `x-liquid-fill-progress`  | `{ progress: number, velocity: number }` | Scroll progress changes     |

## Slots

| Slot      | Description                              |
|-----------|------------------------------------------|
| (default) | Content displayed above the liquid       |

## CSS Custom Properties

| Property                          | Default (gold theme)           | Description                    |
|-----------------------------------|--------------------------------|--------------------------------|
| `--x-liquid-fill-color-1`       | `#B8860B`                      | Gradient stop 1 (deep)         |
| `--x-liquid-fill-color-2`       | `#D4AF37`                      | Gradient stop 2 (mid)          |
| `--x-liquid-fill-color-3`       | `#F9F295`                      | Gradient stop 3 (highlight)    |
| `--x-liquid-fill-specular-color`| `rgba(255,255,240,0.6)`        | Specular highlight tint        |
| `--x-liquid-fill-opacity`       | `1`                            | Overall liquid opacity         |
| `--x-liquid-fill-wave-speed`    | `1`                            | Wave animation speed multiplier|
| `--x-liquid-fill-bg`            | `transparent`                  | Container background           |
| `--x-liquid-fill-border`        | `none`                         | Container border               |
| `--x-liquid-fill-radius`        | `0`                            | Container border-radius        |
| `--x-liquid-fill-bar-height`    | `14px`                         | Height of the bar in bar mode  |

## Shadow Parts

| Part        | Element   | Description                     |
|-------------|-----------|---------------------------------|
| `container` | `<div>`   | Outer wrapper                   |
| `svg`       | `<svg>`   | SVG containing liquid paths     |
| `content`   | `<div>`   | Content layer above the liquid  |

## Theme Presets

| Theme    | Color 1   | Color 2   | Color 3   | Specular                |
|----------|-----------|-----------|-----------|-------------------------|
| `gold`   | `#B8860B` | `#D4AF37` | `#F9F295` | `rgba(255,255,240,0.6)` |
| `water`  | `#0077B6` | `#00B4D8` | `#90E0EF` | `rgba(255,255,255,0.5)` |
| `lava`   | `#8B0000` | `#FF4500` | `#FF8C00` | `rgba(255,255,200,0.7)` |
| `custom` | (CSS var) | (CSS var) | (CSS var) | (CSS var)               |

## Accessibility

- SVG is `aria-hidden="true"` (decorative)
- Respects `prefers-reduced-motion: reduce` — renders flat fill, no waves
- Supports light/dark mode via `prefers-color-scheme`

## Usage

### Basic scroll progress (page-level)

```html
<x-liquid-fill style="position:fixed; right:0; top:0; width:60px; height:100vh;">
</x-liquid-fill>
```

### Track a specific container

```html
<div id="content" style="overflow-y:auto; height:400px;">
  <!-- scrollable content -->
</div>
<x-liquid-fill target="#content" style="width:100%; height:200px;">
</x-liquid-fill>
```

### Bar mode

```html
<x-liquid-fill mode="bar" style="width:100%;"></x-liquid-fill>
```

### Themes

```html
<x-liquid-fill theme="gold"></x-liquid-fill>
<x-liquid-fill theme="water"></x-liquid-fill>
<x-liquid-fill theme="lava"></x-liquid-fill>
```

### Custom colours

```html
<x-liquid-fill theme="custom" style="
  --x-liquid-fill-color-1: #2d1b69;
  --x-liquid-fill-color-2: #7c3aed;
  --x-liquid-fill-color-3: #c4b5fd;
  --x-liquid-fill-specular-color: rgba(255,255,255,0.4);
"></x-liquid-fill>
```

### With content overlay

```html
<x-liquid-fill id="prog" style="width:80px; height:80px; border-radius:50%;">
  <span id="pct" style="
    display:flex; align-items:center; justify-content:center;
    width:100%; height:100%; font-weight:bold; color:white;
  ">0%</span>
</x-liquid-fill>

<script>
  document.getElementById('prog')
    .addEventListener('x-liquid-fill-progress', e => {
      document.getElementById('pct').textContent =
        Math.round(e.detail.progress * 100) + '%';
    });
</script>
```
