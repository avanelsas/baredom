# x-file-download

A styled, accessible button-like anchor that initiates a native browser file download. Wraps an `<a download>` element in shadow DOM, handles disabled state, and dispatches a cancelable click event before the download begins.

## Tag

```html
<x-file-download href="…" filename="…">Download Report</x-file-download>
```

## Attributes

| Attribute    | Type    | Default | Description                                          |
|--------------|---------|---------|------------------------------------------------------|
| `href`       | string  | `""`    | URL of the file to download                          |
| `filename`   | string  | `""`    | Suggested save name (`<a download="…">`)             |
| `disabled`   | boolean | false   | Prevents click interaction and dims the component    |
| `aria-label` | string  | —       | Accessible label when slot content is absent         |

## Properties

| Property   | Type    | Reflects   | Description                        |
|------------|---------|------------|------------------------------------|
| `href`     | string  | `href`     | URL of the file to download        |
| `filename` | string  | `filename` | Suggested save name for the file   |
| `disabled` | boolean | `disabled` | Whether the component is disabled  |

## Events

| Event                   | Cancelable | Detail                               |
|-------------------------|------------|--------------------------------------|
| `x-file-download-click` | yes        | `{ href: string, filename: string }` |

Dispatched on the host element before the native download begins. Calling `event.preventDefault()` suppresses the download.

```js
el.addEventListener('x-file-download-click', (e) => {
  console.log(e.detail.href, e.detail.filename);
  // e.preventDefault() — suppresses the download
});
```

## Slots

| Name    | Description                        |
|---------|------------------------------------|
| default | Button label text / custom content |

## CSS Custom Properties

| Property                              | Default (light)        | Dark override  |
|---------------------------------------|------------------------|----------------|
| `--x-file-download-bg`               | `#2563eb`              | `#3b82f6`      |
| `--x-file-download-color`            | `#ffffff`              | `#ffffff`      |
| `--x-file-download-hover-bg`         | `#1d4ed8`              | `#2563eb`      |
| `--x-file-download-active-bg`        | `#1e40af`              | `#1d4ed8`      |
| `--x-file-download-border-radius`    | `6px`                  | —              |
| `--x-file-download-padding`          | `0.5rem 1rem`          | —              |
| `--x-file-download-font-size`        | `0.875rem`             | —              |
| `--x-file-download-font-weight`      | `500`                  | —              |
| `--x-file-download-gap`              | `0.375rem`             | —              |
| `--x-file-download-icon-size`        | `1em`                  | —              |
| `--x-file-download-focus-ring`       | `#60a5fa`              | `#93c5fd`      |
| `--x-file-download-disabled-opacity` | `0.45`                 | —              |
| `--x-file-download-transition`       | `background 120ms ease`| —              |

## Shadow DOM Parts

| Part      | Element | Description                      |
|-----------|---------|----------------------------------|
| `anchor`  | `<a>`   | The download anchor element      |
| `icon`    | `<span>`| Container for the download icon  |
| `content` | `<span>`| Container for slot content       |

## Accessibility

- The inner anchor (`[part=anchor]`) provides native keyboard navigation (Enter/Space to activate).
- When `disabled`, `aria-disabled="true"` is set on the anchor and `pointer-events: none` prevents mouse interaction.
- Provide meaningful slot content or an `aria-label` attribute for screen reader users.
- The icon SVG is decorative (`aria-hidden="true"`).

## Usage Examples

```html
<!-- Basic download -->
<x-file-download href="/reports/q1.pdf" filename="Q1-Report.pdf">
  Download Q1 Report
</x-file-download>

<!-- Disabled state -->
<x-file-download href="/reports/q2.pdf" filename="Q2-Report.pdf" disabled>
  Download Q2 Report
</x-file-download>

<!-- No suggested filename — browser uses server name -->
<x-file-download href="/api/export/data.csv">
  Export CSV
</x-file-download>

<!-- Accessible label without visible text -->
<x-file-download href="/logo.png" filename="logo.png" aria-label="Download company logo">
</x-file-download>

<!-- Intercept and cancel download -->
<script>
  document.querySelector('x-file-download').addEventListener('x-file-download-click', (e) => {
    if (!confirm(`Download ${e.detail.filename}?`)) {
      e.preventDefault();
    }
  });
</script>
```

## Theming Example

```css
x-file-download {
  --x-file-download-bg: #16a34a;
  --x-file-download-hover-bg: #15803d;
  --x-file-download-active-bg: #166534;
  --x-file-download-border-radius: 9999px;
}
```
