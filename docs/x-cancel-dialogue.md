# x-cancel-dialogue

A confirmation modal dialog with headline, optional message, and confirm/cancel actions. Designed for destructive-action confirmation flows. Fires cancelable request events so host code can intercept before the dialog closes.

---

## Tag

```html
<x-cancel-dialogue></x-cancel-dialogue>
```

---

## Attributes

| Attribute      | Type    | Default           | Description                                              |
|----------------|---------|-------------------|----------------------------------------------------------|
| `open`         | boolean | `false`           | Whether the dialog is visible                            |
| `disabled`     | boolean | `false`           | Disables both action buttons                             |
| `headline`     | string  | `"Discard changes?"` | Dialog title text                                    |
| `message`      | string  | —                 | Optional body message below the headline                 |
| `confirm-text` | string  | `"Discard"`       | Label for the confirm button                             |
| `cancel-text`  | string  | `"Keep editing"`  | Label for the cancel button                              |
| `danger`       | boolean | `false`           | Styles the confirm button in the danger (red) variant    |

---

## Properties

| Property      | Type    | Reflects attribute |
|---------------|---------|--------------------|
| `open`        | boolean | `open`             |
| `disabled`    | boolean | `disabled`         |
| `headline`    | string  | `headline`         |
| `message`     | string  | `message`          |
| `confirmText` | string  | `confirm-text`     |
| `cancelText`  | string  | `cancel-text`      |
| `danger`      | boolean | `danger`           |

---

## Events

| Event                               | Cancelable | Detail                        | Description                                              |
|-------------------------------------|------------|-------------------------------|----------------------------------------------------------|
| `x-cancel-dialogue-cancel-request`  | **yes**    | `{ reason: string }`          | Fired before cancel closes. `preventDefault()` keeps it open. |
| `x-cancel-dialogue-cancel`          | no         | `{}`                          | Fired after dialog closes via cancel                     |
| `x-cancel-dialogue-confirm-request` | **yes**    | `{}`                          | Fired before confirm closes. `preventDefault()` keeps it open. |
| `x-cancel-dialogue-confirm`         | no         | `{}`                          | Fired after dialog closes via confirm                    |

All events bubble and are composed.

### `cancel-request` reason values

| Reason       | Trigger                       |
|--------------|-------------------------------|
| `"cancel-button"` | User clicked the cancel button |
| `"backdrop"` | User clicked the backdrop      |
| `"escape"`   | User pressed Escape            |

---

## Accessibility

- Renders a `div` with `role="dialog"` and `aria-modal="true"`.
- The dialog receives `aria-labelledby` pointing to the unique headline element.
- Focus is trapped inside the dialog while open (Tab cycles between the two buttons).
- Escape key fires `cancel-request` with `reason: "escape"`.

---

## Responsive

The panel min-width and max-width are capped via `min(Xpx, calc(100vw - 2rem))` so the dialogue never overflows on narrow viewports (320px and up).

---

## Examples

### Basic usage

```html
<x-cancel-dialogue
  open
  headline="Discard changes?"
  message="Your unsaved changes will be lost."
></x-cancel-dialogue>
```

### Danger variant with custom labels

```html
<x-cancel-dialogue
  open
  headline="Delete item?"
  message="This action cannot be undone."
  confirm-text="Delete"
  cancel-text="Keep"
  danger
></x-cancel-dialogue>
```

### Listening to events

```js
const dialog = document.querySelector('x-cancel-dialogue');

// The dialog closes itself before firing confirm/cancel.
// No need to remove the open attribute manually.
dialog.addEventListener('x-cancel-dialogue-confirm', () => {
  performDelete();
});
```

### Intercept confirm

```js
dialog.addEventListener('x-cancel-dialogue-confirm-request', e => {
  if (!validationPassed) e.preventDefault();
});
```

### ClojureScript (hiccup renderer)

```clojure
[:x-cancel-dialogue
 {:open (when (:show-dialog state) "")
  :headline "Discard changes?"
  :message "Your unsaved changes will be lost."
  :on-x-cancel-dialogue-confirm
  (fn [_] (swap! app dissoc :show-dialog))
  :on-x-cancel-dialogue-cancel
  (fn [_] (swap! app dissoc :show-dialog))}]

[:x-button {:variant "danger"
            :on-click (fn [_] (swap! app assoc :show-dialog true))}
 "Delete item"]
```
