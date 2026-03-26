# x-spacer

A layout utility component that inserts whitespace between sibling elements. Supports a fixed-size gap or a flexible "spring" that expands to fill remaining space in a flex container.

## Tag

```html
<x-spacer></x-spacer>
```

## Registration

```js
import { init } from '@vanelsas/baredom/x-spacer';
init();
```

## Attributes

| Attribute | Type   | Default      | Description                                                    |
|-----------|--------|--------------|----------------------------------------------------------------|
| `size`    | string | `"1rem"`     | CSS length value for the spacer dimension (e.g. `16px`, `2rem`) |
| `axis`    | enum   | `"vertical"` | `"vertical"` creates height; `"horizontal"` creates width      |
| `grow`    | bool   | absent       | When present, spacer expands to fill remaining flex space       |

## Properties

| Property | Type    | Reflects | Default      | Description                          |
|----------|---------|----------|--------------|--------------------------------------|
| `size`   | string  | `size`   | `"1rem"`     | CSS length for the spacer dimension  |
| `axis`   | string  | `axis`   | `"vertical"` | Spacer axis direction                |
| `grow`   | boolean | `grow`   | `false`      | Whether the spacer fills flex space  |

## CSS Custom Properties

| Property          | Default | Description                                            |
|-------------------|---------|--------------------------------------------------------|
| `--x-spacer-size` | `1rem`  | Overrides the spacer dimension set by the `size` attr  |

The `size` attribute writes `--x-spacer-size` to the element's inline style on every render, so attribute and custom property always stay in sync.

## Events

None.

## Slots

None.

## Accessibility

`x-spacer` is a purely decorative layout element. It sets `role="none"` and `aria-hidden="true"` automatically so screen readers ignore it entirely.

## Behaviour

- **Vertical spacer** (default): creates height equal to `size`. Width is `0`.
- **Horizontal spacer** (`axis="horizontal"`): creates width equal to `size`. Height is `0`.
- **Grow spacer** (`grow` attribute): sets `flex: 1 0 <size>`. The spacer grows to fill remaining space in the flex container's main axis direction. The `size` value becomes the flex basis (minimum). Height and width are `0` so only the flex dimension is affected.

`flex-shrink` is `0` in all non-grow modes so the spacer never collapses.

## Usage Examples

### Vertical gap between stacked elements

```html
<div style="display: flex; flex-direction: column;">
  <p>First block</p>
  <x-spacer size="24px"></x-spacer>
  <p>Second block</p>
</div>
```

### Horizontal gap in a row

```html
<div style="display: flex; align-items: center;">
  <span>Left</span>
  <x-spacer axis="horizontal" size="16px"></x-spacer>
  <span>Right</span>
</div>
```

### Grow spacer (flex spring — push actions to the end of a nav bar)

```html
<nav style="display: flex; align-items: center; padding: 0 16px;">
  <span>Logo</span>
  <x-spacer grow></x-spacer>
  <button>Sign in</button>
</nav>
```

### Grow with minimum size

```html
<nav style="display: flex; align-items: center;">
  <span>Logo</span>
  <x-spacer grow size="16px"></x-spacer>
  <button>Sign in</button>
</nav>
```

### Setting via property

```js
const spacer = document.querySelector('x-spacer');
spacer.size = '2rem';
spacer.axis = 'horizontal';
spacer.grow = true;
```
