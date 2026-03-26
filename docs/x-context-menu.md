# x-context-menu

A positioned dropdown overlay that opens on a trigger click. Items are placed in the default slot. The panel auto-flips when it would overflow the viewport.

---

## Tag

```html
<x-context-menu></x-context-menu>
```

---

## Attributes

| Attribute   | Type    | Default          | Description                                                        |
|-------------|---------|------------------|--------------------------------------------------------------------|
| `open`      | boolean | `false`          | Whether the menu is visible                                        |
| `disabled`  | boolean | `false`          | Disables trigger and interactions                                  |
| `placement` | enum    | `"bottom-start"` | Preferred panel placement: `bottom-start` `bottom-end` `top-start` `top-end` `right-start` `left-start` |
| `offset`    | number  | `8`              | Distance in pixels between anchor and panel                        |
| `z-index`   | number  | `1000`           | CSS `z-index` of the floating panel                                |

---

## Properties

| Property    | Type    | Reflects attribute |
|-------------|---------|-------------------|
| `open`      | boolean | `open`            |
| `disabled`  | boolean | `disabled`        |
| `placement` | string  | `placement`       |
| `offset`    | number  | `offset`          |
| `zIndex`    | number  | `z-index`         |

---

## Events

| Event                        | Cancelable | Detail       | Description                           |
|------------------------------|------------|--------------|---------------------------------------|
| `x-context-menu-open-request`  | **yes**  | `{}`         | Fired before opening                  |
| `x-context-menu-open`          | no       | `{}`         | Fired after opening                   |
| `x-context-menu-close-request` | **yes**  | `{}`         | Fired before closing                  |
| `x-context-menu-close`         | no       | `{}`         | Fired after closing                   |
| `x-context-menu-select`        | no       | `{ item }`   | Fired when a menu item is selected    |

---

## Slots

| Slot      | Description                         |
|-----------|-------------------------------------|
| `trigger` | The element that opens the menu     |
| *(default)* | Menu item elements                |

Slotted items with `role="menuitem"` are keyboard-navigable via `ArrowUp` / `ArrowDown` and selectable with `Enter` / `Space`.

---

## Auto-flip

The panel is positioned using pure DOM geometry. When the preferred placement overflows the viewport, the panel flips to the opposite side. The position is clamped to viewport margins.

---

## Keyboard

| Key         | Action                             |
|-------------|------------------------------------|
| `ArrowDown` | Focus next menu item               |
| `ArrowUp`   | Focus previous menu item           |
| `Enter`     | Select focused item, close menu    |
| `Space`     | Select focused item, close menu    |
| `Escape`    | Close menu                         |
| `Tab`       | Close menu                         |

---

## Accessibility

- The panel has `role="menu"`.
- Each `role="menuitem"` item is keyboard navigable.
- The trigger receives `aria-haspopup="menu"` and `aria-expanded`.

---

## Examples

### Basic

```html
<x-context-menu>
  <button slot="trigger">Actions ▾</button>
  <div role="menuitem">Edit</div>
  <div role="menuitem">Duplicate</div>
  <div role="menuitem" style="color:red">Delete</div>
</x-context-menu>
```

### Custom placement

```html
<x-context-menu placement="bottom-end" offset="4">
  <button slot="trigger">⋯</button>
  <div role="menuitem">Rename</div>
  <div role="menuitem">Move</div>
</x-context-menu>
```

### Listening to select

```js
document.querySelector('x-context-menu')
  .addEventListener('x-context-menu-select', e => {
    console.log('selected', e.detail.item);
  });
```

### ClojureScript (hiccup renderer)

```clojure
[:x-context-menu
 [:button {:slot "trigger"} "Actions ▾"]
 [:div {:role "menuitem"} "Edit"]
 [:div {:role "menuitem"} "Duplicate"]
 [:div {:role "menuitem" :style "color:red"} "Delete"]]
```
