# x-chart

A lightweight, zero-dependency SVG chart web component that renders line, bar, and area charts directly from data passed as a JSON attribute or a JS array property. All rendering is imperative SVG; no virtual DOM or framework runtime is involved.

---

## Tag

```html
<x-chart></x-chart>
```

---

## Attributes

| Attribute  | Type         | Default      | Description |
|------------|--------------|--------------|-------------|
| `type`     | enum string  | `"line"`     | Chart type: `"line"` \| `"bar"` \| `"area"` |
| `data`     | JSON string  | —            | Array of series objects (see Data Format section) |
| `height`   | number       | `180`        | Chart height in pixels |
| `padding`  | number       | `12`         | Plot area padding in pixels (applied to all four sides) |
| `grid`     | boolean      | `true`       | Show horizontal grid lines behind the plot |
| `axes`     | boolean      | `true`       | Show x-axis and y-axis tick labels |
| `tooltip`  | boolean      | `false`      | Show a floating tooltip on pointer hover |
| `cursor`   | enum string  | `"nearest"`  | Hit-test strategy: `"nearest"` \| `"x"` \| `"none"` |
| `disabled` | boolean      | `false`      | Dims the chart and disables all pointer/keyboard interaction |
| `loading`  | boolean      | `false`      | Replaces chart content with an animated skeleton |
| `selected` | string       | —            | Currently highlighted data point in `"seriesId:index"` format |

Boolean attributes follow the HTML boolean presence convention — the attribute being present (with any value, or empty) means `true`; absent means `false`.

---

## Properties

All properties reflect to their corresponding attributes. Setting a property updates the attribute and triggers a re-render.

| Property   | Type      | Reflects attribute | Notes |
|------------|-----------|--------------------|-------|
| `type`     | string    | yes                | Normalised to one of the allowed enum values |
| `height`   | number    | yes                | |
| `padding`  | number    | yes                | |
| `grid`     | boolean   | yes                | |
| `axes`     | boolean   | yes                | |
| `tooltip`  | boolean   | yes                | |
| `cursor`   | string    | yes                | |
| `disabled` | boolean   | yes                | |
| `loading`  | boolean   | yes                | |
| `selected` | string    | yes                | |
| `data`     | JS array  | no                 | Alternative to the JSON `data` attribute. Setting this property directly accepts a plain JS array and bypasses JSON parsing. When both the attribute and the property are set, the property takes precedence. |

### x-format / y-format

The `x-format` and `y-format` attributes accept a format spec string that controls how axis tick labels **and tooltip values** are rendered:

When the attribute is omitted, values are auto-formatted: integers are shown as-is, values ≥ 1 000 are abbreviated with K/M suffixes, and decimals are shown to one decimal place.

| Spec        | Example output | Description |
|-------------|----------------|-------------|
| `"raw"`     | `42.5`         | Raw value converted to string, no transformation |
| `"int"`     | `42`           | Integer, rounded |
| `"fixed:2"` | `42.50`        | Fixed decimal places (N after the colon) |
| `"pct"`     | `42.5%`        | Percentage suffix |
| `"abbr"`    | `42.5K`        | Abbreviated with K/M/B suffix |

---

## Data Format

The `data` attribute accepts a JSON string representing an array of series objects:

```json
[
  {
    "id": "s1",
    "label": "Revenue",
    "color": "#0066cc",
    "data": [
      { "x": 1, "y": 10 },
      { "x": 2, "y": 25 },
      { "x": 3, "y": 18 }
    ]
  },
  {
    "id": "s2",
    "label": "Costs",
    "color": "#cc3300",
    "data": [
      { "x": 1, "y": 5 },
      { "x": 2, "y": 12 },
      { "x": 3, "y": 9 }
    ]
  }
]
```

### Series object fields

| Field   | Type              | Required | Description |
|---------|-------------------|----------|-------------|
| `id`    | string            | no       | Stable identifier used in `selected` attribute and events. Auto-generated if omitted. |
| `label` | string            | no       | Human-readable series name shown in tooltips and screen-reader announcements. |
| `color` | CSS color string  | no       | Stroke/fill color for this series. Falls back to `--x-chart-series-N` CSS custom properties in order. |
| `data`  | `{x, y}[]`        | yes      | Array of data points. `x` may be a number or a category string. `y` must be numeric. |

---

## Events

| Event             | Bubbles | Composed | Cancelable | Detail |
|-------------------|---------|----------|------------|--------|
| `x-chart-select`  | yes     | yes      | no         | `{ seriesId, index, x, y, value }` |
| `x-chart-hover`   | yes     | yes      | no         | `{ seriesId, index, x, y, value }` |

### Event detail fields

| Field      | Type              | Description |
|------------|-------------------|-------------|
| `seriesId` | string            | The `id` of the series that was interacted with |
| `index`    | number            | Zero-based index of the data point within the series |
| `x`        | number \| string  | The `x` value of the data point |
| `y`        | number            | The `y` value of the data point |
| `value`    | `{x, y}`          | Full data point object (convenience alias) |

`x-chart-select` fires on pointer click or keyboard activation (Enter/Space) when cursor mode is not `"none"`. It also updates the `selected` attribute on the element.

`x-chart-hover` fires on pointer move when tooltip mode is enabled and cursor mode is not `"none"`.

---

## Parts

| Part              | Description |
|-------------------|-------------|
| `container`       | Outermost div wrapping the SVG. Has `tabindex="0"` for keyboard focus. |
| `svg`             | The `<svg>` element that contains all chart drawing. |
| `sr-only`         | Visually-hidden region with `role="status"` and `aria-live="polite"` for screen-reader announcements. |
| `tooltip`         | The floating tooltip card shown on hover when `tooltip` attribute is set. |
| `tooltip-header`  | The x-axis label row at the top of the tooltip card. |
| `tooltip-body`    | Container for the per-series rows. |
| `tooltip-row`     | One row per series (up to 8 pre-built; hidden rows have `data-hidden="true"`). |
| `tooltip-swatch`  | Colored dot matching the series color. |
| `tooltip-label`   | Series label text. |
| `tooltip-value`   | Formatted y-value, shown right-aligned. |

### Tooltip behavior

- `cursor="x"` — tooltip shows **one row per series** sharing the nearest x position (multi-series mode).
- `cursor="nearest"` — tooltip shows **one row** for the single closest data point.
- `cursor="none"` — tooltip never appears even if the `tooltip` attribute is set.
- A vertical **crosshair line** appears at the hovered x position.
- A **dot indicator** (radius 4 SVG units) appears on each series line at the hovered x.

---

## CSS Custom Properties

### Chart

| Variable                  | Light default                               | Dark default                                |
|---------------------------|---------------------------------------------|---------------------------------------------|
| `--x-chart-series-1`      | `rgba(0,102,204,0.95)`                      | `rgba(120,190,255,0.95)`                    |
| `--x-chart-series-2`      | `rgba(16,140,72,0.95)`                      | `rgba(80,230,150,0.92)`                     |
| `--x-chart-series-3`      | `rgba(190,20,40,0.95)`                      | `rgba(255,100,110,0.93)`                    |
| `--x-chart-series-4`      | `rgba(204,120,0,0.95)`                      | `rgba(255,190,60,0.93)`                     |
| `--x-chart-radius`        | `0.75rem`                                   | same                                        |
| `--x-chart-border`        | `rgba(0,0,0,0.1)`                           | `rgba(255,255,255,0.1)`                     |
| `--x-chart-grid`          | `rgba(0,0,0,0.08)`                          | `rgba(255,255,255,0.08)`                    |
| `--x-chart-axis-label`    | `rgba(0,0,0,0.5)`                           | `rgba(255,255,255,0.45)`                    |
| `--x-chart-focus-ring`    | `rgba(0,102,204,0.55)`                      | `rgba(120,190,255,0.55)`                    |

### Tooltip

| Variable                           | Light default            | Dark default                    |
|------------------------------------|--------------------------|--------------------------------|
| `--x-chart-tooltip-bg`             | `rgba(255,255,255,0.96)` | `rgba(30,30,35,0.97)`          |
| `--x-chart-tooltip-border`         | `rgba(0,0,0,0.12)`       | `rgba(255,255,255,0.12)`       |
| `--x-chart-tooltip-shadow`         | `0 4px 16px …`           | `0 4px 20px …`                 |
| `--x-chart-tooltip-radius`         | `0.5rem`                 | same                           |
| `--x-chart-tooltip-padding`        | `0.45rem 0.7rem`         | same                           |
| `--x-chart-tooltip-font-size`      | `0.8125rem`              | same                           |
| `--x-chart-tooltip-header-color`   | `rgba(0,0,0,0.5)`        | `rgba(255,255,255,0.45)`       |
| `--x-chart-tooltip-label-color`    | `rgba(0,0,0,0.65)`       | `rgba(255,255,255,0.6)`        |
| `--x-chart-tooltip-value-color`    | `rgba(0,0,0,0.9)`        | `rgba(255,255,255,0.9)`        |
| `--x-chart-tooltip-swatch-size`    | `8px`                    | same                           |
| `--x-chart-tooltip-gap`            | `0.35rem`                | same                           |
| `--x-chart-crosshair-color`        | `rgba(0,0,0,0.18)`       | `rgba(255,255,255,0.2)`        |
| `--x-chart-crosshair-width`        | `1`                      | same                           |

Dark mode values are applied automatically via `@media (prefers-color-scheme: dark)` inside the component's shadow styles. Use these custom properties on the host element to override.

---

## Accessibility

- `[part=container]` is a `<div>` with `tabindex="0"`, making the chart keyboard-focusable.
- When focused: ArrowLeft/ArrowRight move the selection across data points within the current series; ArrowUp/ArrowDown switch between series; Enter/Space fires `x-chart-select` for the current selection.
- The currently selected data point is announced via `[part=sr-only]` which has `role="status"` and `aria-live="polite"`.
- The chart SVG is marked `aria-hidden="true"`; all screen-reader content is conveyed through `[part=sr-only]`.
- All animations respect `@media (prefers-reduced-motion: reduce)` — transitions and skeleton animations are disabled when the user has requested reduced motion.
- The `disabled` attribute sets `aria-disabled="true"` on the container and removes `tabindex`.

---

## Examples

### HTML

```html
<!-- Line chart with two series -->
<x-chart
  type="line"
  height="240"
  grid
  axes
  tooltip
  data='[
    {"id":"rev","label":"Revenue","color":"#0066cc","data":[
      {"x":"Jan","y":12},{"x":"Feb","y":19},{"x":"Mar","y":15},
      {"x":"Apr","y":28},{"x":"May","y":34}
    ]},
    {"id":"cost","label":"Costs","color":"#cc2200","data":[
      {"x":"Jan","y":8},{"x":"Feb","y":11},{"x":"Mar","y":9},
      {"x":"Apr","y":14},{"x":"May","y":17}
    ]}
  ]'
></x-chart>

<!-- Bar chart with category x-axis -->
<x-chart
  type="bar"
  height="200"
  grid
  axes
  data='[{"id":"s1","label":"Sales","data":[
    {"x":"Q1","y":42},{"x":"Q2","y":61},{"x":"Q3","y":55},{"x":"Q4","y":78}
  ]}]'
></x-chart>

<!-- Area chart, loading state -->
<x-chart type="area" loading></x-chart>

<!-- Disabled chart -->
<x-chart type="line" disabled data='[{"id":"s1","data":[{"x":1,"y":5}]}]'></x-chart>
```

### Setting data via JS property

```js
const chart = document.querySelector('x-chart');
chart.data = [
  {
    id: 'live',
    label: 'Live feed',
    color: '#0066cc',
    data: Array.from({ length: 20 }, (_, i) => ({ x: i, y: Math.random() * 100 }))
  }
];
```

### Listening to events

```js
const chart = document.querySelector('x-chart');

chart.addEventListener('x-chart-select', e => {
  const { seriesId, index, x, y } = e.detail;
  console.log(`Selected: series=${seriesId}, point=${index}, x=${x}, y=${y}`);
});

chart.addEventListener('x-chart-hover', e => {
  const { seriesId, x, y } = e.detail;
  console.log(`Hovering: series=${seriesId}, x=${x}, y=${y}`);
});
```

### ClojureScript hiccup (reagent-style reference — component is a plain custom element)

```clojure
[:x-chart
 {:type    "line"
  :height  "240"
  :grid    true
  :axes    true
  :tooltip true
  :data    (js/JSON.stringify
             (clj->js [{:id    "revenue"
                        :label "Revenue"
                        :color "#0066cc"
                        :data  [{:x "Jan" :y 12}
                                {:x "Feb" :y 19}
                                {:x "Mar" :y 15}]}]))}]
```

### CSS custom property overrides

```css
x-chart {
  --x-chart-series-1: #7c3aed;
  --x-chart-series-2: #db2777;
  --x-chart-radius: 6px;
}
```
