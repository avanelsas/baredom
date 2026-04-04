# x-gaussian-blur

A decorative background component that generates animated, blurred color blobs — producing a soft, dreamy, living gradient effect. Colored shapes float and drift inside a container with a gaussian blur filter applied, creating an ambient backdrop. Content can be layered on top via the default slot.

## Tag

```html
<x-gaussian-blur></x-gaussian-blur>
```

## Attributes

| Attribute   | Type    | Default                                  | Description |
|-------------|---------|------------------------------------------|-------------|
| `colors`    | string  | `"#6366f1, #ec4899, #14b8a6, #f59e0b"` | Comma-separated CSS color values for the blobs. Colors cycle when blob count exceeds the number of colors. |
| `blur`      | string  | `"60"`                                   | Blur radius in pixels applied to the backdrop via `filter: blur()`. |
| `speed`     | string  | `"medium"`                               | Animation speed. Presets: `slow` (18s), `medium` (10s), `fast` (5s). Or a raw number in seconds (e.g. `"25"`). |
| `count`     | string  | `"5"`                                    | Number of blobs to render. Clamped to 1–12. |
| `size`      | string  | `"medium"`                               | Blob size relative to container. Presets: `small` (30%), `medium` (50%), `large` (70%). Or a percentage number (e.g. `"60"`). |
| `opacity`   | string  | `"0.7"`                                  | Opacity of the blur layer (0–1). |
| `animation` | string  | `"float"`                                | Animation type: `float` (drifting movement), `pulse` (scale breathing + drift), `none`. |
| `blend`     | string  | `"normal"`                               | CSS `mix-blend-mode` for the blob layer: `normal`, `multiply`, `screen`, `overlay`, `soft-light`. |
| `paused`    | boolean | absent                                   | Boolean attribute. When present, pauses all animations. |

## Properties

| Property    | JS Type | Reflects Attribute |
|-------------|---------|-------------------|
| `colors`    | string  | `colors`          |
| `blur`      | string  | `blur`            |
| `speed`     | string  | `speed`           |
| `count`     | string  | `count`           |
| `size`      | string  | `size`            |
| `opacity`   | string  | `opacity`         |
| `animation` | string  | `animation`       |
| `blend`     | string  | `blend`           |
| `paused`    | boolean | `paused`          |

## Events

None. This is a purely decorative component.

## Slots

| Slot    | Description |
|---------|-------------|
| default | Content layered on top of the blur background. |

### Slot example

```html
<x-gaussian-blur style="height: 400px;">
  <div style="display:flex; align-items:center; justify-content:center; height:100%;">
    <h1>Hello World</h1>
  </div>
</x-gaussian-blur>
```

## Parts

| Part       | Description |
|------------|-------------|
| `backdrop` | The container div holding the blurred blobs. |
| `content`  | The slot wrapper sitting above the blur layer. |

## CSS Custom Properties

### Layout

| Property                           | Default       | Description |
|------------------------------------|---------------|-------------|
| `--x-gaussian-blur-bg`            | `transparent` | Background color behind the blobs. |
| `--x-gaussian-blur-z-index`       | `0`           | Z-index of the blur layer. |
| `--x-gaussian-blur-border-radius` | `0`           | Border-radius of the host container. |

## Accessibility

- The backdrop div has `aria-hidden="true"` (always — blobs are purely decorative).
- When no slotted content is present, the host gets `role="presentation"` and `aria-hidden="true"`.
- When slotted content is detected, those attributes are removed so the content remains accessible.
- No interactive elements. No tabindex.
- Animations respect `@media (prefers-reduced-motion: reduce)`.

## Blob Layout

Blob positions are computed deterministically from the blob index using a golden-angle distribution. The same attribute values always produce the same visual output — no randomness is involved.

Each blob gets a slight size variation (70–130% of the base size) and staggered animation timing for an organic, non-synchronized feel.

## Examples

### Basic usage

```html
<x-gaussian-blur style="height: 300px;"></x-gaussian-blur>
```

### Custom colors and blur

```html
<x-gaussian-blur
  colors="#ff6b6b, #4ecdc4, #45b7d1, #f7dc6f"
  blur="80"
  speed="slow"
  style="height: 400px;">
</x-gaussian-blur>
```

### With content overlay

```html
<x-gaussian-blur colors="#667eea, #764ba2" blur="60" style="height: 500px;">
  <div style="display:flex; align-items:center; justify-content:center; height:100%; color:white;">
    <h1>Welcome</h1>
  </div>
</x-gaussian-blur>
```

### Paused animation

```html
<x-gaussian-blur paused style="height: 300px;"></x-gaussian-blur>
```

### Pulse animation with blend mode

```html
<x-gaussian-blur animation="pulse" blend="screen" style="height: 300px;"></x-gaussian-blur>
```
