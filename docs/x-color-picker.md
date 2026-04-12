# x-color-picker

A color picker component with a 2D saturation/brightness area, hue strip, optional alpha channel, preset swatches, eyedropper support, and clipboard copy. Supports inline and popover display modes.

## Tag name

```html
<x-color-picker></x-color-picker>
```

## Attributes

| Attribute   | Type    | Default      | Description                                       |
|-------------|---------|--------------|---------------------------------------------------|
| `value`     | string  | `"#000000"`  | Current color as `#rrggbb` or `#rrggbbaa`         |
| `alpha`     | boolean | absent       | Show alpha channel strip                          |
| `swatches`  | string  | absent       | Comma-separated preset hex colors                 |
| `disabled`  | boolean | absent       | Disable all interaction                           |
| `readonly`  | boolean | absent       | Prevent value changes                             |
| `name`      | string  | absent       | Form field name                                   |
| `mode`      | string  | `"inline"`   | `"inline"` (always visible) or `"popover"`        |
| `open`      | boolean | absent       | Popover open state (popover mode only)            |
| `label`     | string  | absent       | Accessible label for the color area               |

## Properties

| Property   | Type    | Reflects attribute |
|------------|---------|-------------------|
| `value`    | string  | `value`           |
| `alpha`    | boolean | `alpha`           |
| `swatches` | string  | `swatches`        |
| `disabled` | boolean | `disabled`        |
| `readOnly` | boolean | `readonly`        |
| `name`     | string  | `name`            |
| `mode`     | string  | `mode`            |
| `open`     | boolean | `open`            |
| `label`    | string  | `label`           |

## Events

| Event                    | Bubbles | Composed | Cancelable | Detail                                  |
|--------------------------|---------|----------|------------|-----------------------------------------|
| `x-color-picker-input`   | true    | true     | false      | `{ value, h, s, l, a }`                |
| `x-color-picker-change`  | true    | true     | true       | `{ value, h, s, l, a }`                |

- **`input`** fires continuously during drag interaction (area, hue strip, alpha strip).
- **`change`** fires when the user commits a value (pointer up, Enter key on hex input, swatch click).

## Slots

None.

## Parts

| Part              | Description                              |
|-------------------|------------------------------------------|
| `container`       | Outer wrapper                            |
| `trigger`         | Popover trigger button (popover mode)    |
| `trigger-swatch`  | Color swatch inside trigger              |
| `panel`           | Picker panel                             |
| `area`            | 2D saturation/brightness gradient        |
| `area-thumb`      | Area draggable thumb                     |
| `hue-strip`       | Hue gradient bar                         |
| `hue-thumb`       | Hue strip thumb                          |
| `alpha-strip`     | Alpha gradient bar                       |
| `alpha-gradient`  | Alpha gradient overlay                   |
| `alpha-thumb`     | Alpha strip thumb                        |
| `controls`        | Controls row                             |
| `preview`         | Color preview swatch                     |
| `preview-color`   | Preview swatch inner color               |
| `hex-input`       | Hex value text input                     |
| `eyedropper`      | Eyedropper button (Chrome/Edge only)     |
| `copy`            | Copy to clipboard button                 |
| `swatches`        | Preset swatches grid                     |
| `swatch`          | Individual preset swatch button          |

## CSS Custom Properties

| Property                          | Default                          | Description               |
|-----------------------------------|----------------------------------|---------------------------|
| `--x-color-picker-width`          | `240px`                          | Overall width             |
| `--x-color-picker-area-height`    | `160px`                          | Color area height         |
| `--x-color-picker-strip-height`   | `14px`                           | Hue/alpha strip height    |
| `--x-color-picker-swatch-size`    | `28px`                           | Preset swatch size        |
| `--x-color-picker-radius`         | `var(--x-radius-md, 8px)`       | Border radius             |
| `--x-color-picker-gap`            | `10px`                           | Internal spacing          |

## Accessibility

- The color area has `role="slider"` with `aria-valuetext` describing the current color in human-readable format.
- The hue strip has `role="slider"` with `aria-valuemin="0"` and `aria-valuemax="360"`.
- The alpha strip has `role="slider"` with `aria-valuemin="0"` and `aria-valuemax="100"`.
- Preset swatches use `role="option"` inside a `role="listbox"` container.
- All interactive elements are keyboard accessible via Tab.

## Keyboard Interaction

| Key                   | Context      | Action                                     |
|-----------------------|--------------|--------------------------------------------|
| Arrow Left/Right      | Color area   | Adjust saturation by 1 (10 with Shift)     |
| Arrow Up/Down         | Color area   | Adjust brightness by 1 (10 with Shift)     |
| Arrow Left/Right      | Hue strip    | Adjust hue by 1 (10 with Shift)            |
| Home / End            | Hue strip    | Set hue to 0 / 360                         |
| Arrow Left/Right      | Alpha strip  | Adjust alpha by 0.01 (0.1 with Shift)      |
| Home / End            | Alpha strip  | Set alpha to 0 / 1                         |
| Enter                 | Hex input    | Apply typed hex value                      |
| Escape                | Popover      | Close popover                              |
| Tab                   | All          | Cycle through interactive elements         |

## Examples

### HTML

```html
<!-- Inline (default) -->
<x-color-picker value="#3b82f6"></x-color-picker>

<!-- With alpha channel -->
<x-color-picker value="#3b82f680" alpha></x-color-picker>

<!-- With preset swatches -->
<x-color-picker swatches="#ef4444,#f59e0b,#10b981,#3b82f6,#8b5cf6"></x-color-picker>

<!-- Popover mode -->
<x-color-picker mode="popover" value="#10b981"></x-color-picker>

<!-- Disabled -->
<x-color-picker value="#3b82f6" disabled></x-color-picker>
```

### JavaScript

```js
const picker = document.querySelector('x-color-picker');

picker.addEventListener('x-color-picker-change', (e) => {
  console.log('Color:', e.detail.value);
  console.log('HSL:', e.detail.h, e.detail.s, e.detail.l);
});

// Set value programmatically
picker.value = '#ff5500';
```

### Form Integration

```html
<form>
  <x-color-picker name="accent-color" value="#3b82f6"></x-color-picker>
  <button type="submit">Save</button>
</form>
```
