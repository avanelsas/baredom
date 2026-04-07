# `<x-splash>`

A full-viewport loading/splash screen overlay. Covers the page while an app initializes and dismisses with a fade-out animation when the `active` attribute is removed.

## Tag Name

```
x-splash
```

## Attributes

| Attribute  | Type    | Default     | Description |
|------------|---------|-------------|-------------|
| `active`   | boolean | _(absent)_  | Present = overlay visible. Remove to trigger fade-out. |
| `variant`  | string  | `"default"` | Visual style: `default`, `branded`, `minimal` |
| `progress` | number  | _(absent)_  | When present, shows a progress bar (0–100). |
| `spinner`  | boolean | `true`      | Shows the built-in CSS spinner. Set `spinner="false"` to hide. |
| `overlay`  | string  | `"solid"`   | Background mode: `solid`, `blur`, `transparent` |

## Properties

| Property   | Type            | Reflects Attribute |
|------------|-----------------|--------------------|
| `active`   | `boolean`       | `active`           |
| `variant`  | `string`        | `variant`          |
| `progress` | `number \| null`| `progress`         |
| `spinner`  | `boolean`       | `spinner`          |
| `overlay`  | `string`        | `overlay`          |

## Events

| Event             | Bubbles | Composed | Cancelable | Detail |
|-------------------|---------|----------|------------|--------|
| `x-splash-hidden` | yes     | yes      | no         | `{}`   |

Fires after the fade-out animation completes (or immediately when `prefers-reduced-motion: reduce` is active).

## Slots

| Slot      | Description |
|-----------|-------------|
| _(default)_ | Custom content — logos, text, animations. Centered above the spinner and progress bar. |

## Parts

| Part       | Element  | Description |
|------------|----------|-------------|
| `overlay`  | `<div>`  | Full-viewport backdrop |
| `content`  | `<div>`  | Centering wrapper for slot + spinner + progress |
| `spinner`  | `<span>` | Built-in CSS ring animation |
| `progress` | `<div>`  | Progress bar track |
| `bar`      | `<div>`  | Progress bar fill |

## CSS Custom Properties

| Custom Property                   | Default              | Description |
|-----------------------------------|----------------------|-------------|
| `--x-splash-bg`                   | `#ffffff` / `#0f172a`| Overlay background |
| `--x-splash-color`                | `#0f172a` / `#f8fafc`| Text / foreground color |
| `--x-splash-z-index`              | `9999`               | Overlay stacking order |
| `--x-splash-fade-duration`        | `400ms`              | Fade-out transition duration |
| `--x-splash-fade-ease`            | `cubic-bezier(0.4,0,0.2,1)` | Fade easing curve |
| `--x-splash-spinner-size`         | `40px`               | Spinner diameter |
| `--x-splash-spinner-color`        | `currentColor`       | Spinner arc color |
| `--x-splash-spinner-track-color`  | `rgba(0,0,0,0.12)`  | Spinner track ring color |
| `--x-splash-spinner-thickness`    | `3px`                | Spinner border width |
| `--x-splash-spinner-duration`     | `0.75s`              | Spinner rotation period |
| `--x-splash-progress-height`      | `4px`                | Progress bar height |
| `--x-splash-progress-color`       | `#3b82f6`            | Progress bar fill color |
| `--x-splash-progress-track-color` | `rgba(0,0,0,0.08)`  | Progress bar track color |
| `--x-splash-progress-radius`      | `2px`                | Progress bar border-radius |
| `--x-splash-gap`                  | `20px`               | Spacing between slot, spinner, progress |
| `--x-splash-blur-amount`          | `8px`                | Blur for `overlay="blur"` |
| `--x-splash-branded-bg`           | _(fallback to --x-splash-bg)_ | Background for `variant="branded"` |

## Accessibility

- `role="status"` and `aria-live="polite"` when active
- `aria-busy="true"` while loading, removed on hide
- `aria-label="Loading"` default (override via `aria-label` attribute)
- Progress bar has `role="progressbar"` with `aria-valuemin`, `aria-valuemax`, `aria-valuenow`
- Respects `prefers-reduced-motion: reduce` — skips fade animation

## Usage Examples

### Basic splash screen

```html
<x-splash active></x-splash>

<script>
  // When app is ready:
  document.querySelector('x-splash').removeAttribute('active');
</script>
```

### With custom content

```html
<x-splash active>
  <img src="/logo.svg" alt="App Logo" width="120">
  <p>Loading your workspace...</p>
</x-splash>
```

### With progress bar

```html
<x-splash active progress="0" spinner="false">
  <h2>Initializing...</h2>
</x-splash>

<script>
  const splash = document.querySelector('x-splash');
  // Update progress as resources load
  splash.progress = 30;
  splash.progress = 60;
  splash.progress = 100;
  // Done
  splash.active = false;
</script>
```

### Blur overlay

```html
<x-splash active overlay="blur">
  <p>Loading...</p>
</x-splash>
```

### Listening for the hidden event

```html
<x-splash active id="splash"></x-splash>

<script>
  const splash = document.getElementById('splash');
  splash.addEventListener('x-splash-hidden', () => {
    splash.remove(); // clean up after fade-out completes
  });
  // Later:
  splash.active = false;
</script>
```
