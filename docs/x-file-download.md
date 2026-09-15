# x-file-download

A styled, accessible button-like anchor that initiates a native browser file download. Wraps an `<a download>` element in shadow DOM, handles disabled state, and dispatches a cancelable click event before the download begins.

## Tag

```html
<x-file-download href="…" filename="…">Download Report</x-file-download>
```

## Attributes

| Attribute    | Type    | Default | Description                                                  |
|--------------|---------|---------|--------------------------------------------------------------|
| `href`       | string  | `""`    | URL of the file to download                                  |
| `filename`   | string  | `""`    | Suggested save name (`<a download="…">`)                     |
| `disabled`   | boolean | false   | Prevents click interaction and dims the component            |
| `aria-label` | string  | —       | Accessible label when slot content is absent                 |
| `picker`     | boolean | false   | Saves through the native save dialog when the browser has one |

When `href` is a `data:` URL and no `filename` is provided, the component automatically sets the `download` attribute on the inner anchor. This is required because browsers block top-level navigation to `data:` URLs — without the `download` attribute the link would do nothing.

## Properties

| Property   | Type    | Reflects   | Description                         |
|------------|---------|------------|-------------------------------------|
| `href`     | string  | `href`     | URL of the file to download         |
| `filename` | string  | `filename` | Suggested save name for the file    |
| `disabled` | boolean | `disabled` | Whether the component is disabled   |
| `picker`   | boolean | `picker`   | Whether saving uses the save dialog |

## Saving through the save dialog

With `picker`, a click opens the native save dialog (`showSaveFilePicker`). The user chooses the folder and the name. The component then fetches `href` and writes the response into the chosen file.

```html
<x-file-download picker href="/exports/project.zip" filename="project.zip">
  Save project
</x-file-download>
```

A page that builds the file in the browser sets `href` to an object URL:

```js
el.href = URL.createObjectURL(zipBlob);
```

The dialog is used when all of these hold. Otherwise the click is a normal download.

- `picker` is set and `href` is not empty
- the browser has `showSaveFilePicker`
- the page is a secure context
- the page is not inside a cross-origin or sandboxed iframe

The dialog suggests `filename`. Without `filename` it suggests the last path segment of `href`. A `blob:` or `data:` URL without `filename` has no suggestion.

While a save runs, the host has `data-busy`, the anchor has `aria-busy="true"` and the cursor shows progress. Clicks are ignored until the save ends.

## Events

| Event                     | Cancelable | Detail                               |
|---------------------------|------------|--------------------------------------|
| `x-file-download-click`   | yes        | `{ href: string, filename: string }` |
| `x-file-download-success` | no         | `{ filename: string }`               |
| `x-file-download-cancel`  | no         | `{}`                                 |
| `x-file-download-error`   | no         | `{ error: string, phase: string }`   |

`x-file-download-click` is dispatched on the host element before the download or the dialog. Calling `event.preventDefault()` suppresses both.

The other three events fire only when the save dialog is used:

- `x-file-download-success`: the file is written. `filename` is the name the user chose.
- `x-file-download-cancel`: the user closed the dialog. The browser also reports a folder it refuses as a cancel.
- `x-file-download-error`: the save failed. `error` is the error name (for example `NotAllowedError` or `TypeError`), or `HTTP <status>` for a failed response. `phase` is `pick`, `fetch` or `write`.

When the dialog cannot open (`SecurityError`), the component starts a normal download instead and dispatches none of these events. Removing the element during a save aborts it with `error: "AbortError"`.

```js
el.addEventListener('x-file-download-error', (e) => {
  console.log(e.detail.error, e.detail.phase);
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
- While a save runs, `aria-busy="true"` is set on the anchor.
- The component announces nothing. Use the outcome events to announce success or failure, for example with `x-toast`.

## Known limits

- In picker mode the component fetches `href` itself. A cross-origin `href` needs CORS, or the save fails with `error: "TypeError"` and `phase: "fetch"`.
- Picking an existing file and then failing may leave that file empty.
- Firefox, Safari and Brave (by default) have no save dialog. They always download.
- A normal download cannot report whether the user saved or cancelled.

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
