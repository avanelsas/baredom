# x-command-palette

A searchable command list overlay (⌘K pattern). Opens as a modal with a text input that filters a list of commands supplied via the `items` JS property. Supports keyboard navigation, groups, disabled items, and a scrim backdrop.

---

## Tag

```html
<x-command-palette></x-command-palette>
```

---

## Attributes

| Attribute         | Type    | Default       | Description                                                         |
|-------------------|---------|---------------|---------------------------------------------------------------------|
| `open`            | boolean | `false`       | Whether the palette is visible                                      |
| `modal`           | boolean†| `true`        | Adds focus trap and blocks background interaction                    |
| `dismissible`     | boolean†| `true`        | Allow closing via Escape or backdrop click                          |
| `no-scrim`        | boolean | `false`       | Hides the backdrop scrim even in modal mode                         |
| `close-on-scrim`  | boolean | *(see below)* | Close when backdrop is clicked (default: same as scrim visibility)  |
| `close-on-escape` | boolean†| `true`        | Close on Escape key                                                 |
| `disabled`        | boolean | `false`       | Disables the palette and all interactions                           |
| `placeholder`     | string  | `"Search…"`   | Input placeholder text                                              |
| `empty-text`      | string  | `"No results"`| Text shown when no items match the query                            |
| `label`           | string  | —             | `aria-label` on the dialog element                                  |

†Default-true boolean: absent = `true`; `attribute="false"` = `false`.

---

## Properties

| Property   | Type    | Description                                                              |
|------------|---------|--------------------------------------------------------------------------|
| `items`    | array   | Command items. Each item: `{ id, label, keywords?, group?, value?, icon?, disabled? }` |
| `open`     | boolean | Reflects `open` attribute                                                |
| `disabled` | boolean | Reflects `disabled` attribute                                            |

### Item shape

```ts
{
  id:        string           // required, unique identifier
  label:     string           // required, displayed text
  keywords?: string | string[] // extra search terms
  group?:    string           // group heading
  value?:    any              // arbitrary payload in select event detail
  icon?:     string           // icon glyph or URL
  disabled?: boolean          // greys out item, skips in keyboard nav
}
```

---

## Events

| Event                               | Cancelable | Detail                | Description                              |
|-------------------------------------|------------|------------------------|------------------------------------------|
| `x-command-palette-open-request`    | **yes**    | `{}`                  | Fired before opening                     |
| `x-command-palette-open`            | no         | `{}`                  | Fired after opening                      |
| `x-command-palette-close-request`   | **yes**    | `{}`                  | Fired before closing                     |
| `x-command-palette-close`           | no         | `{}`                  | Fired after closing                      |
| `x-command-palette-select-request`  | **yes**    | `{ item }`            | Fired before item selection              |
| `x-command-palette-select`          | no         | `{ item }`            | Fired after item selected                |
| `x-command-palette-query-change`    | no         | `{ query: string }`   | Fired as the user types                  |

---

## Keyboard

| Key        | Action                                         |
|------------|------------------------------------------------|
| `ArrowDown`| Move to next enabled item (wraps)              |
| `ArrowUp`  | Move to previous enabled item (wraps)          |
| `Enter`    | Select highlighted item                        |
| `Escape`   | Close palette (if `close-on-escape` is `true`) |

---

## Accessibility

- Renders a `<div>` with `role="dialog"` and `aria-modal="true"`.
- Input has `role="combobox"` with `aria-expanded`, `aria-controls` (linked to the listbox), and `aria-activedescendant` (tracks the highlighted item).
- The list has `role="listbox"`; each item has `role="option"`.
- Disabled items have `aria-disabled="true"`.
- Group headers are hidden from the accessibility tree via `aria-hidden="true"`.

---

## Responsive

The panel max-width is capped at `calc(100vw - 2rem)` to prevent horizontal overflow on narrow viewports.

---

## Examples

### Basic usage

```html
<x-command-palette id="palette"></x-command-palette>
<button onclick="document.getElementById('palette').setAttribute('open','')">
  Open (⌘K)
</button>
<script>
  const palette = document.getElementById('palette');
  palette.items = [
    { id: 'new',      label: 'New file',     group: 'File' },
    { id: 'open',     label: 'Open file…',   group: 'File' },
    { id: 'settings', label: 'Preferences',  group: 'App'  },
  ];
  palette.addEventListener('x-command-palette-select', e => {
    console.log('selected', e.detail.item);
    palette.removeAttribute('open');
  });
  palette.addEventListener('x-command-palette-close', () => {
    palette.removeAttribute('open');
  });
</script>
```

### ClojureScript (hiccup renderer)

```clojure
[:x-command-palette
 {:open (when (:palette-open state) "")
  :on-x-command-palette-close
  (fn [_] (swap! app dissoc :palette-open))
  :on-x-command-palette-select
  (fn [e]
    (handle-command (.. e -detail -item))
    (swap! app dissoc :palette-open))}]
```
