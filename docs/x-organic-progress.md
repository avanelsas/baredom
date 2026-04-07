# `<x-organic-progress>`

A progress/loading indicator that renders a growing vine or honeycomb lattice structure. Transforms a standard loading state into a mesmerizing observation of organic growth, with simplex noise for natural randomness, spring physics for smooth transitions, and a bloom/scatter animation on completion.

## Tag Name

```
x-organic-progress
```

## Attributes

| Attribute  | Type              | Default       | Description |
|------------|-------------------|---------------|-------------|
| `progress` | number (0–100)    | _(absent)_    | Growth target. Absent = indeterminate oscillating mode. |
| `variant`  | string            | `"vine"`      | Branching style: `vine` (organic) or `honeycomb` (hexagonal lattice). |
| `color`    | string            | CSS prop      | Primary branch color override. |
| `bloom`    | boolean           | `true`        | Show bloom/scatter animation on completion. Set `bloom="false"` to disable. |
| `density`  | string            | `"normal"`    | Branch density: `sparse`, `normal`, `dense`. |
| `seed`     | number            | `42`          | Random seed for reproducible patterns. Same seed = same vine shape. |
| `label`    | string            | _(absent)_    | Accessible label text for the progress bar. |

## Properties

| Property   | Type              | Reflects Attribute |
|------------|-------------------|--------------------|
| `progress` | `number \| null`  | `progress`         |
| `variant`  | `string`          | `variant`          |
| `color`    | `string \| null`  | `color`            |
| `bloom`    | `boolean`         | `bloom`            |
| `density`  | `string`          | `density`          |
| `seed`     | `number`          | `seed`             |
| `label`    | `string \| null`  | `label`            |

## Events

| Event                            | Bubbles | Composed | Cancelable | Detail              |
|----------------------------------|---------|----------|------------|---------------------|
| `x-organic-progress-complete`    | yes     | yes      | no         | `{progress: 100}`   |
| `x-organic-progress-bloom-end`   | yes     | yes      | no         | `{progress: 100}`   |

**`x-organic-progress-complete`** fires when `progress` reaches 100% (vine fully grown). This is the "data" event indicating loading is done.

**`x-organic-progress-bloom-end`** fires ~1.5 seconds after `complete`, when the bloom/scatter animation finishes. Use this to safely remove the component or transition to next content. Only fires when `bloom` is enabled.

## Parts

| Part       | Element | Description |
|------------|---------|-------------|
| `svg`      | `<svg>` | Main SVG canvas containing all visuals |
| `branches` | `<g>`   | Group of SVG `<line>` elements forming the vine/crystal |
| `nodes`    | `<g>`   | Group of SVG `<circle>` elements at lattice vertices |
| `blooms`   | `<g>`   | Group of SVG `<circle>` elements for the bloom animation |

## CSS Custom Properties

| Custom Property                          | Default              | Description |
|------------------------------------------|----------------------|-------------|
| `--x-organic-progress-color-primary`     | `#22c55e` / `#4ade80`| Main branch color |
| `--x-organic-progress-color-secondary`   | `#16a34a` / `#22c55e`| Secondary (deeper) branch color |
| `--x-organic-progress-bloom-color`       | `#f472b6` / `#f9a8d4`| Bloom petal color |
| `--x-organic-progress-bg`               | `transparent`        | Background color |
| `--x-organic-progress-branch-width`     | `3`                  | Base branch stroke width |
| `--x-organic-progress-glow`             | `2`                  | Gooey blur intensity |
| `--x-organic-progress-opacity`          | `1`                  | Overall opacity |

Dark-mode defaults (after the `/`) apply automatically via `prefers-color-scheme: dark`.

## Accessibility

- `role="progressbar"` with `aria-valuemin="0"` and `aria-valuemax="100"`
- Determinate mode: `aria-valuenow` and `aria-valuetext` reflect current progress
- Indeterminate mode: `aria-busy="true"`, no `aria-valuenow`
- Set `label` attribute for a descriptive `aria-label`
- Respects `prefers-reduced-motion: reduce` — renders a static snapshot with no animation

## Usage

### Basic (indeterminate)

```html
<x-organic-progress></x-organic-progress>
```

### With progress

```html
<x-organic-progress progress="65"></x-organic-progress>
```

### Honeycomb variant, dense

```html
<x-organic-progress variant="honeycomb" density="dense" progress="80"></x-organic-progress>
```

### Custom colors via CSS

```html
<x-organic-progress
  progress="50"
  style="--x-organic-progress-color-primary: #818cf8;
         --x-organic-progress-color-secondary: #6366f1;
         --x-organic-progress-bloom-color: #fbbf24;">
</x-organic-progress>
```

### JavaScript control

```js
const el = document.querySelector('x-organic-progress');
el.progress = 75;

el.addEventListener('x-organic-progress-complete', () => {
  console.log('Loading complete!');
});

el.addEventListener('x-organic-progress-bloom-end', () => {
  el.remove(); // Safe to remove after bloom finishes
});
```

### Disable bloom

```html
<x-organic-progress progress="100" bloom="false"></x-organic-progress>
```

### Reproducible pattern

```html
<x-organic-progress seed="7" progress="60"></x-organic-progress>
<x-organic-progress seed="7" progress="60"></x-organic-progress>
<!-- Both render identical vine shapes -->
```
