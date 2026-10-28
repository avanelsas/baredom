# x-comments

A comment thread with one form. The form stands at the bottom for a new comment, or right after
the comment a reader answers. The component holds no comments. The page renders each one as an
`<x-comment>`, and stores what a reader sends.

## Tag name

```html
<x-comments commenter-name="Ann">
  <x-comment comment-id="a1" name="Bo Chen" datetime="2026-10-20"><p>First.</p></x-comment>
  <x-comment comment-id="b2" name="Cy Park" datetime="2026-10-21" depth="1"
             reply-to-name="Bo Chen"><p>Second.</p></x-comment>
  <p slot="notice">Comments are stored with Google until the author reviews them.</p>
</x-comments>
```

## Observed Attributes

| Attribute | Type | Default | Notes |
|-----------|------|---------|-------|
| `closed` | boolean | absent | Comments are off. No form and no Reply. |
| `reply-to` | string | `""` | The `comment-id` the form answers. A value that names no comment puts the form at the bottom. |
| `commenter-name` | string | `""` | The value of the name field. A change of it replaces what the reader typed. |
| `name-max` | number | `80` | The most characters of a name. |
| `text-max` | number | `4000` | The most characters of a comment. |
| `heading-label` | string | `"Comments"` | The heading. |
| `form-label` | string | `"Leave a comment"` | The form title at the bottom. |
| `reply-form-label` | string | `"Reply to"` | The form title of a reply, before the name. |
| `name-label` | string | `"Name"` | The label of the name field. |
| `text-label` | string | `"Comment"` | The label of the text field. |
| `send-label` | string | `"Send"` | The send button. |
| `cancel-label` | string | `"Cancel"` | The cancel button. |
| `sent-label` | string | `"Your comment was sent."` | What a screen reader hears after a send. |

## Properties

| Property | Type | Reflects |
|----------|------|----------|
| `closed` | boolean | `closed` |
| `replyTo` | string | `reply-to` |
| `commenterName` | string | `commenter-name` |
| `nameMax` | number | `name-max` |
| `textMax` | number | `text-max` |
| `headingLabel` | string | `heading-label` |
| `formLabel` | string | `form-label` |
| `replyFormLabel` | string | `reply-form-label` |
| `nameLabel` | string | `name-label` |
| `textLabel` | string | `text-label` |
| `sendLabel` | string | `send-label` |
| `cancelLabel` | string | `cancel-label` |
| `sentLabel` | string | `sent-label` |

## Methods

| Method | Description |
|--------|-------------|
| `focus()` | Moves focus to the first empty field of the form. |

## Events

| Event | Bubbles | Composed | Cancelable | Detail |
|-------|---------|----------|------------|--------|
| `x-comments-reply-request` | yes | yes | yes | `{ replyTo: string, previousReplyTo: string }` |
| `x-comments-submit` | yes | yes | yes | `{ name: string, text: string, replyTo: string, trap: string }` |

- `x-comments-reply-request` asks to move the form. Reply on a comment asks for its id. Cancel asks
  for `""`. Unless the request is cancelled, the component sets `reply-to`.
- `x-comments-submit` fires when a reader sends a filled form. `replyTo` is `""` for a top-level
  comment. Unless the event is cancelled, the text clears, the component asks to move the form back
  to the bottom, and a screen reader hears `sent-label`. A cancelled send leaves the form as it is.
- `trap` is the value of a hidden field. A person leaves it empty.

The component sends nothing over the network and stores nothing. The page does that in its
`x-comments-submit` handler, and adds the comment it stored as an `<x-comment>`.

## Slots

| Slot | Description |
|------|-------------|
| _(default)_ | The `<x-comment>` elements, oldest first. |
| `notice` | A line under the form. |

## Writes on other nodes

On each `<x-comment>` child, x-comments writes `slot` (`before` or `after` the form) and
`data-comments-closed`. A page does not set them.

## CSS Custom Properties

| Property | Default (light) | Default (dark) | Notes |
|----------|-----------------|----------------|-------|
| `--x-comments-gap` | `var(--x-space-md,0.75rem)` | | Space between comments |
| `--x-comments-form-bg` | `var(--x-color-surface,#ffffff)` | `var(--x-color-surface,#1f2937)` | Form background |
| `--x-comments-form-border` | `var(--x-color-border,#e5e7eb)` | `var(--x-color-border,#374151)` | Form border colour |
| `--x-comments-form-radius` | `var(--x-radius-md,8px)` | | Form corner radius |
| `--x-comments-heading-font-size` | `var(--x-font-size-lg,1.125rem)` | | Heading size |

## Shadow DOM Parts

| Part | Element | Description |
|------|---------|-------------|
| `thread` | `<section>` | The thread, labelled by its heading. |
| `heading` | `<h2>` | The heading. |
| `form-wrap` | `<div>` | Holds the form. |
| `form` | `<form>` | The form, labelled by its title. |
| `form-title` | `<p>` | "Leave a comment" or "Reply to" and a name. |
| `name` | `<x-form-field>` | The name field. |
| `text` | `<x-text-area>` | The text field. |
| `trap` | `<input>` | The hidden field. |
| `actions` | `<div>` | The buttons. |
| `cancel` | `<x-button>` | Cancel. Shown while the form answers a comment. |
| `send` | `<x-button>` | Send. |
| `status` | `<p>` | The polite live region. |

## Accessibility

- The thread is a `<section>` labelled by its heading. The form is labelled by its title.
- Reply moves focus to the text field. Cancel moves it back to the comment.
- A send that is not filled reports the first empty field and sends nothing.
- The hidden field has `tabindex="-1"`, `autocomplete="off"` and `aria-hidden="true"`.

## Motion

The form fades in at its new place. Under `prefers-reduced-motion: reduce` it moves without a fade.

## Usage Examples

### Store a comment

```js
comments.addEventListener('x-comments-submit', async e => {
  const id = crypto.randomUUID();
  await storePending({ id, ...e.detail, sentAt: Date.now() });
  comments.append(renderPending(id, e.detail));
});
```

### Comments off

```html
<x-comments closed>…</x-comments>
```
