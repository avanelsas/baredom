# x-copy

A copy-to-clipboard button component. Resolves the text to copy from multiple sources in priority order, writes it to the clipboard via the Clipboard API, and shows a brief tooltip confirming success or failure. Supports plain-text and HTML copy modes, keyboard hotkeys, and fully customizable messaging.

---

## Tag

```html
<x-copy text="Hello, world!">Copy</x-copy>
```

---

## Attributes

| Attribute | Type | Default | Description |
|-----------|------|---------|-------------|
| `text` | string | — | Text to copy directly. Evaluated at copy time, not registration time. |
| `from` | string | — | CSS selector identifying a source element. At copy time the element is queried from the document root. |
| `from-attr` | string | — | When `from` is set: name of an attribute on the target element to copy. If absent, the element's `textContent` is used. |
| `mode` | enum: `"text"` \| `"html"` | `"text"` | Copy mode. `"html"` writes an HTML + plain-text `ClipboardItem`; falls back to plain-text copy if `ClipboardItem` is unavailable. |
| `disabled` | boolean presence | absent = enabled | Disables the button and suppresses copy on hotkey. |
| `show-tooltip` | boolean (default-true) | `true` | Whether to show the success/error tooltip. Absent = `true`; `"false"` = disabled. |
| `tooltip-ms` | number (100–10000) | `1200` | How long the tooltip stays visible in milliseconds. |
| `success-message` | string | `"Copied"` | Tooltip text on successful copy. |
| `error-message` | string | `"Copy failed"` | Tooltip text when the copy operation fails. |
| `hotkey` | string | — | Global keyboard shortcut that triggers `copy()`. Format: modifier keys joined by `+` followed by the key, e.g. `"ctrl+c"`, `"meta+shift+c"`. Case-insensitive. |

---

## Properties

| Property | Type | Reflects attribute |
|----------|------|--------------------|
| `text` | string | `text` |
| `from` | string | `from` |
| `fromAttr` | string | `from-attr` |
| `mode` | string | `mode` |
| `disabled` | boolean | `disabled` |
| `showTooltip` | boolean | `show-tooltip` |
| `tooltipMs` | number | `tooltip-ms` |
| `successMessage` | string | `success-message` |
| `errorMessage` | string | `error-message` |
| `hotkey` | string | `hotkey` |
| `textValue` | string | — (property-only) |

`textValue` is a property-only override. When set, it takes precedence over all attribute-based text resolution. Setting it to `null` or `undefined` restores normal resolution.

---

## Events

| Event | Bubbles | Composed | Cancelable | Detail |
|-------|---------|----------|------------|--------|
| `x-copy-request` | yes | yes | **yes** | `{ text: string, mode: string, from: string \| null, fromAttr: string \| null }` |
| `x-copy-success` | yes | yes | no | `{ text: string }` |
| `x-copy-error` | yes | yes | no | `{ error: unknown }` |

`x-copy-request` fires before the clipboard write with the fully resolved text. Calling `preventDefault()` aborts the copy — no clipboard write and no `x-copy-success` or `x-copy-error` fires.

---

## Text Resolution Priority

At copy time the component resolves the text to copy in this order:

1. `textValue` property (if non-empty string).
2. `text` attribute (if present and non-empty).
3. `from` attribute → query the document for the selector → if `from-attr` is also set, read that attribute from the element; otherwise use the element's `textContent`.
4. Empty string `""`.

---

## Methods

| Method | Signature | Description |
|--------|-----------|-------------|
| `copy` | `() => Promise<string>` | Programmatically triggers a copy. Resolves with the copied text on success, rejects on failure. Fires the full event sequence. |

---

## Slots

| Slot | Description |
|------|-------------|
| (default) | Content rendered inside the trigger button. Typically a label string or an icon. |
| `tooltip` | Override the tooltip content entirely. When this slot is populated the `success-message` and `error-message` attributes are ignored for rendering (events still fire). |

---

## Parts

| Part | Description |
|------|-------------|
| `wrap` | Outermost `<div>` wrapping button and tooltip. |
| `trigger` | The `<button>` element. |
| `tooltip` | Tooltip container `<div>`. Hidden until a copy attempt completes. |
| `tooltip-text` | `<span>` inside the tooltip that holds the message text. |

---

## Tooltip Behavior

When a copy attempt completes (success or error), if `show-tooltip` is not `false`:

- `[data-tooltip-open]` is added to the host element.
- `[data-tooltip-kind]` is set to `"success"` or `"error"` on the host element.
- After `tooltip-ms` milliseconds, both attributes are removed and the tooltip hides.

These host attributes can be used in external CSS for custom tooltip styling:

```css
x-copy[data-tooltip-open][data-tooltip-kind="error"] {
  /* custom error state */
}
```

---

## HTML Copy Mode

When `mode="html"`:

1. The `ClipboardItem` API is attempted with both `text/html` and `text/plain` blobs (the resolved text is used for both, or the HTML source for `text/html`).
2. If `ClipboardItem` is not available (older browsers, non-HTTPS), the component falls back to a plain-text clipboard write using `navigator.clipboard.writeText`.

---

## CSS Custom Properties

| Variable | Default | Description |
|----------|---------|-------------|
| `--x-copy-tooltip-bg` | `#1a1a1a` (light) / `#f0f0f0` (dark) | Tooltip background color. |
| `--x-copy-tooltip-fg` | `#f0f0f0` (light) / `#1a1a1a` (dark) | Tooltip text color. |
| `--x-copy-tooltip-radius` | `4px` | Tooltip border-radius. |
| `--x-copy-tooltip-offset` | `6px` | Gap between trigger and tooltip. |

---

## Accessibility

- The trigger is a native `<button>` — keyboard accessible by default.
- `aria-label` should be set on the component or the button slot content should be descriptive.
- When `disabled`, the button receives the HTML `disabled` attribute; it is skipped by tab order.
- The tooltip is announced via `role="status"` and `aria-live="polite"` so screen readers read the confirmation message without moving focus.
- The tooltip element is `aria-hidden="true"` when not visible to prevent spurious announcements.

---

## Keyboard

| Key | Condition | Action |
|-----|-----------|--------|
| `Enter` / `Space` | Trigger focused, not disabled | Triggers `copy()`. |
| `hotkey` value | Document keydown, not disabled | Triggers `copy()`. |

---

## Examples

### HTML

```html
<!-- Copy a literal string -->
<x-copy text="Hello, world!">Copy greeting</x-copy>

<!-- Copy from another element's textContent -->
<code id="snippet">npm install @example/lib</code>
<x-copy from="#snippet">Copy</x-copy>

<!-- Copy an attribute value -->
<a id="link" href="https://example.com">Example</a>
<x-copy from="#link" from-attr="href">Copy link</x-copy>

<!-- HTML mode -->
<div id="rich"><strong>Bold text</strong> and plain text</div>
<x-copy from="#rich" mode="html">Copy rich text</x-copy>

<!-- Disabled -->
<x-copy text="unavailable" disabled>Copy</x-copy>

<!-- No tooltip -->
<x-copy text="silent" show-tooltip="false">Copy silently</x-copy>

<!-- Custom messages -->
<x-copy text="custom" success-message="Done!" error-message="Oops!">Copy</x-copy>

<!-- Global hotkey -->
<x-copy text="shortcut value" hotkey="meta+shift+c">Copy (⌘⇧C)</x-copy>

<!-- Handle events -->
<x-copy id="monitored" text="watched">Copy</x-copy>
<script>
  const el = document.getElementById('monitored');
  el.addEventListener('x-copy-request', e => {
    if (!confirm('Allow copy?')) e.preventDefault();
  });
  el.addEventListener('x-copy-success', e => console.log('Copied:', e.detail.text));
  el.addEventListener('x-copy-error',   e => console.error('Failed:', e.detail.error));
</script>
```

### ClojureScript (hiccup)

```clojure
;; Copy literal text
[:x-copy {:text "Hello, world!"} "Copy greeting"]

;; Copy from element
[:x-copy {:from "#snippet"} "Copy"]

;; Copy attribute value
[:x-copy {:from "#link" :from-attr "href"} "Copy link"]

;; HTML mode
[:x-copy {:from "#rich" :mode "html"} "Copy rich text"]

;; Disabled
[:x-copy {:text "unavailable" :disabled true} "Copy"]

;; Custom duration and messages
[:x-copy {:text "hi" :tooltip-ms "2000"
          :success-message "Copied!" :error-message "Oops!"} "Copy"]

;; Listen to events
[:x-copy
  {:text "watched"
   :on-x-copy-request #(when-not (js/confirm "Allow?") (.preventDefault %))
   :on-x-copy-success #(js/console.log "Copied:" (.. % -detail -text))
   :on-x-copy-error   #(js/console.error "Error:"  (.. % -detail -error))}
  "Copy"]
```
