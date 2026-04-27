# x-file-upload

A drag-and-drop file upload zone with click-to-browse, file list with optional image thumbnails, file validation, and form integration. Emits File objects via events — the app handles the actual upload.

## Tag name

```html
<x-file-upload>...</x-file-upload>
```

## Observed attributes

| Attribute   | Type    | Default | Description |
|-------------|---------|---------|-------------|
| `accept`    | string  | `""`    | File type filter (MIME types, extensions, wildcards) |
| `multiple`  | boolean | `false` | Allow multiple files |
| `max-size`  | number  | (none)  | Max file size in bytes per file |
| `max-files` | number  | (none)  | Max number of files (with multiple) |
| `disabled`  | boolean | `false` | Disables the component |
| `required`  | boolean | `false` | Required for form validation |
| `name`      | string  | `""`    | Form field name |

### Accept format

Comma-separated tokens: MIME types (`image/png`), wildcards (`image/*`), or extensions (`.pdf`).

```html
accept="image/*"
accept=".pdf,.doc,.docx"
accept="image/png,image/jpeg"
```

## Properties (camelCase, reflect attributes)

| Property   | Type    | Reflected attribute | Notes |
|------------|---------|---------------------|-------|
| `accept`   | string  | `accept` | |
| `multiple` | boolean | `multiple` | |
| `maxSize`  | number  | `max-size` | |
| `maxFiles` | number  | `max-files` | |
| `disabled` | boolean | `disabled` | |
| `required` | boolean | `required` | |
| `name`     | string  | `name` | |
| `files`    | File[]  | —  | Read-only, returns copy of current file list |

## Events

### `x-file-upload-select`

Fired when files are added (after validation).

| Property | Value |
|----------|-------|
| `bubbles` | `true` |
| `composed` | `true` |
| `cancelable` | `false` |
| `detail.files` | Array of accepted File objects |
| `detail.rejected` | Array of `{file, reason}` for rejected files |

### `x-file-upload-remove`

Fired when a file is removed from the list.

| Property | Value |
|----------|-------|
| `bubbles` | `true` |
| `composed` | `true` |
| `cancelable` | `false` |
| `detail.file` | The removed File object |
| `detail.remaining` | Array of remaining File objects |

## Slots

| Slot      | Description |
|-----------|-------------|
| (default) | Placeholder content shown in the drop zone |

## Shadow parts

| Part           | Description |
|----------------|-------------|
| `drop-zone`    | Main interactive drop/click area |
| `drag-overlay` | Overlay shown during drag-over |
| `file-list`    | Container for file items |
| `file-item`    | Individual file row |
| `thumbnail`    | Image preview (images only, via blob URL) |
| `file-name`    | File name text |
| `file-size`    | Formatted file size text |
| `remove`       | Remove button per file |
| `live-region`  | Accessibility announcements (visually hidden) |

## CSS custom properties

| Property | Default | Description |
|----------|---------|-------------|
| `--x-file-upload-bg` | `var(--x-color-surface)` | Drop zone background |
| `--x-file-upload-fg` | `var(--x-color-text)` | Text color |
| `--x-file-upload-muted` | `var(--x-color-text-muted)` | Muted text color |
| `--x-file-upload-border` | `2px dashed var(--x-color-border)` | Drop zone border |
| `--x-file-upload-border-hover` | `2px dashed var(--x-color-primary)` | Hover/drag border |
| `--x-file-upload-drag-bg` | `rgba(59,130,246,0.05)` | Drag-over background tint |
| `--x-file-upload-radius` | `var(--x-radius-md, 8px)` | Border radius |
| `--x-file-upload-padding` | `var(--x-space-lg, 24px)` | Drop zone padding |
| `--x-file-upload-thumb-size` | `48px` | Thumbnail dimensions |
| `--x-file-upload-item-bg` | `var(--x-color-surface)` | File item background |
| `--x-file-upload-remove-color` | `var(--x-color-text-muted)` | Remove button color |
| `--x-file-upload-remove-hover` | `var(--x-color-danger)` | Remove button hover color |

## Accessibility

- Drop zone has `role="button"` and `tabindex="0"`
- Enter/Space on drop zone opens the file picker
- Remove buttons have `aria-label="Remove {filename}"`
- File count changes announced via `aria-live="polite"` region
- Disabled state: `aria-disabled="true"`, `tabindex="-1"`

## Thumbnails

Image files (`image/*`) automatically get a thumbnail preview via `URL.createObjectURL()`. Blob URLs are revoked when files are removed or the component disconnects.

## File validation

Files are validated on add (drop or browse):
- **Type**: checked against `accept` attribute
- **Size**: checked against `max-size` attribute
- **Count**: checked against `max-files` attribute (with `multiple`)

Rejected files are included in the `x-file-upload-select` event's `detail.rejected` array with a `reason` string.

## Form integration

- `formAssociated: true` — participates in form submission
- Files submitted as `FormData` entries under the `name` attribute
- `required` validation via `ElementInternals`
- Responds to `form.reset()` (clears all files)
- Responds to `fieldset.disabled`

## Usage examples

### Basic image upload

```html
<x-file-upload accept="image/*" multiple>
  <p>Drop images here or click to browse</p>
</x-file-upload>
```

### Constrained upload

```html
<x-file-upload accept=".pdf" max-size="10485760" max-files="3" multiple>
  <p>Upload up to 3 PDFs (max 10MB each)</p>
</x-file-upload>
```

### In a form

```html
<form>
  <x-file-upload name="attachments" required multiple accept="image/*,.pdf">
    <p>Attach files</p>
  </x-file-upload>
  <button type="submit">Submit</button>
</form>
```

### Listening for events

```js
const uploader = document.querySelector('x-file-upload');

uploader.addEventListener('x-file-upload-select', e => {
  console.log('Accepted:', e.detail.files);
  console.log('Rejected:', e.detail.rejected);
  // Upload accepted files via fetch/XHR
});

uploader.addEventListener('x-file-upload-remove', e => {
  console.log('Removed:', e.detail.file.name);
  console.log('Remaining:', e.detail.remaining.length);
});
```
