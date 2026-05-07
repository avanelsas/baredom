# x-kbd

A stateless, themeable inline element for rendering keyboard keys and shortcuts. Takes a combo string (or slotted content) and renders one styled `<kbd>` "cap" per key with separators between. Resolves `Mod` to `⌘` on macOS and `Ctrl` elsewhere from the same authored markup.

---

## Tag

```html
<!-- combo, platform-aware -->
<x-kbd keys="Mod+K"></x-kbd>

<!-- explicit -->
<x-kbd keys="Ctrl+Shift+P" platform="win"></x-kbd>

<!-- slot mode (custom content as a single cap) -->
<x-kbd>Esc</x-kbd>
```

---

## Attributes

| Attribute   | Values                                       | Default | Description                                                                                                                |
|-------------|----------------------------------------------|---------|----------------------------------------------------------------------------------------------------------------------------|
| `keys`      | string                                       | —       | Combo string, e.g. `"Ctrl+Shift+P"`. Split on `separator`; whitespace trimmed; empty tokens dropped. Empty/absent → slot mode. |
| `separator` | single character                             | `+`     | Used to split `keys`. Also rendered between caps.                                                                          |
| `size`      | `sm` \| `md` \| `lg`                         | `md`    | Drives font-size and padding via `data-size` on the host.                                                                  |
| `platform`  | `auto` \| `mac` \| `win` \| `linux`           | `auto`  | `auto` detects via `navigator.userAgentData.platform` (with `navigator.platform` fallback) at `connectedCallback` time.    |
| `label`     | string                                       | —       | Verbatim `aria-label` on the host. Overrides the auto-derived combo label.                                                 |

---

## Properties

| Property    | Type   | Reflects attribute |
|-------------|--------|--------------------|
| `keys`      | string | `keys`             |
| `separator` | string | `separator`        |
| `size`      | string | `size`             |
| `platform`  | string | `platform`         |
| `label`     | string | `label`            |

No events, no public methods.

---

## Slots

| Slot             | Description                                                                          |
|------------------|--------------------------------------------------------------------------------------|
| _(default slot)_ | Consulted only when `keys` is empty/absent. Slotted content is rendered as one cap.  |

---

## Parts

| Part        | Description                                     |
|-------------|-------------------------------------------------|
| `base`      | Inline-flex container for caps and separators.  |
| `key`       | Each individual `<kbd>` cap.                    |
| `separator` | `<span>` between caps in combo mode.            |

---

## CSS Custom Properties

| Variable                   | Default                                                            | Description                       |
|----------------------------|--------------------------------------------------------------------|-----------------------------------|
| `--x-kbd-bg`               | `var(--x-typography-kbd-bg, rgba(0,0,0,0.06))`                     | Cap background.                   |
| `--x-kbd-color`            | `var(--x-color-text, inherit)`                                     | Cap text colour.                  |
| `--x-kbd-border-color`     | `var(--x-typography-kbd-border, rgba(0,0,0,0.15))`                 | Cap border colour.                |
| `--x-kbd-border-radius`    | `var(--x-typography-kbd-radius, 0.25rem)`                          | Cap corner radius.                |
| `--x-kbd-padding`          | `var(--x-typography-kbd-padding, 0.1em 0.4em)`                     | Padding inside the cap.           |
| `--x-kbd-font-family`      | `ui-monospace, "SF Mono", Menlo, Consolas, monospace`              | Cap font family.                  |
| `--x-kbd-font-size`        | `0.875em` (size variant overrides)                                 | Cap font size.                    |
| `--x-kbd-shadow`           | `inset 0 -1px 0 rgba(0,0,0,0.08)`                                  | Cap shadow.                       |
| `--x-kbd-gap`              | `0.25rem`                                                          | Gap between caps and separators.  |
| `--x-kbd-separator-color`  | `var(--x-color-text-muted, currentColor)`                          | Separator text colour.            |

The `--x-typography-kbd-*` variables in the defaults above are **optional override hooks**, not tokens that `x-typography` pre-defines. If a consumer sets them anywhere up the cascade (e.g. on `:root`), x-kbd picks them up automatically; otherwise the inner fallback applies. To customise x-kbd directly, set the `--x-kbd-*` variables.

Light/dark mode is handled automatically via `@media (prefers-color-scheme)`.

---

## Token mapping

`keys` tokens are matched case-insensitively. Unknown tokens (letters, F-keys, arrows, etc.) pass through verbatim.

| Input token (case-insensitive)   | mac      | win        | linux       | aria name               |
|----------------------------------|----------|------------|-------------|-------------------------|
| `Mod`                            | `⌘`      | `Ctrl`     | `Ctrl`      | "Command" / "Control"   |
| `Cmd` \| `Command` \| `Meta` \| `Super` | `⌘`      | `Win`      | `Super`     | "Command" / "Windows" / "Super" |
| `Ctrl` \| `Control`              | `⌃`      | `Ctrl`     | `Ctrl`      | "Control"               |
| `Alt` \| `Option`                | `⌥`      | `Alt`      | `Alt`       | "Option" / "Alt"        |
| `Shift`                          | `⇧`      | `Shift`    | `Shift`     | "Shift"                 |
| `Enter` \| `Return`              | `Return` | `Enter`    | `Enter`     | "Return" / "Enter"      |
| anything else                    | passthrough | passthrough | passthrough | passthrough          |

Use `Mod` whenever you want the canonical "primary modifier" — `⌘` on macOS, `Ctrl` elsewhere — without authoring the markup twice.

---

## Accessibility

- Each cap is a real `<kbd>` element so AT and copy/paste behave correctly.
- In combo mode, the host gets a derived `aria-label` joined with `" plus "` using each token's **aria name** (never the visible glyph). Example: `keys="Mod+K"` on macOS renders `⌘ K` but the host's `aria-label` is `"Command plus K"`.
- Author-supplied `label` always overrides the derived value verbatim — including in slot mode (where there is no derived value to override).
- In slot mode without an explicit `label`, the host carries no `aria-label` — the slotted `<kbd>` is already semantic.
- Decorative only: no focus, no event listeners, no keyboard handling. The component renders shortcuts; it does not invoke them.

---

## Examples

```html
<!-- Cross-platform shortcut -->
Press <x-kbd keys="Mod+K"></x-kbd> to open the palette.

<!-- Pinned to a platform (e.g. in docs targeting Windows users) -->
<x-kbd keys="Ctrl+Shift+P" platform="win"></x-kbd>

<!-- Single key, slot mode -->
<x-kbd>Esc</x-kbd>

<!-- Custom separator -->
<x-kbd keys="Cmd-K" separator="-" platform="mac"></x-kbd>

<!-- Size variants -->
<x-kbd keys="Mod+S" size="sm"></x-kbd>
<x-kbd keys="Mod+S" size="lg"></x-kbd>

<!-- Author-supplied aria-label -->
<x-kbd keys="Mod+P" label="open command palette"></x-kbd>

<!-- Arrow keys (passthrough) -->
<x-kbd keys="↑+↓+←+→"></x-kbd>
```
