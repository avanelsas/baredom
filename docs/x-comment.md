# x-comment

One comment of a thread. It shows a name, an avatar, a date, a body and a Reply button. The page
gives the facts: the depth, the name of the comment it answers and the status. The component shows
them.

## Tag name

```html
<x-comment comment-id="b2" name="Bo Chen" datetime="2026-10-21" depth="1" reply-to-name="Ann Lee">
  <p>Thanks, that helped.</p>
</x-comment>
```

## Observed Attributes

| Attribute | Type | Default | Notes |
|-----------|------|---------|-------|
| `comment-id` | string | `""` | The id of the comment. `x-comment-reply` carries it. |
| `name` | string | `""` | The name of the commenter. The avatar shows its initials. |
| `datetime` | string | `""` | An ISO 8601 date. It shows as given. A date that cannot be read is not shown. |
| `locale` | string | `""` | A language tag, such as `en-GB`. When set, the date shows in that language, as its day in UTC. |
| `depth` | number | `0` | The depth in the thread. 0 is a top-level comment. |
| `max-depth` | number | `2` | The deepest indent level. A deeper reply stands at this level. |
| `reply-to-name` | string | `""` | The name of the comment it answers. |
| `author` | boolean | absent | A comment of the blog author. It is badged and highlighted. |
| `status` | enum | `"published"` | `published`, `pending`, `removed` or `expired`. An unknown status is `published`. |
| `reply-label` | string | `"Reply"` | The text of the Reply button. |
| `reply-context-label` | string | `"Reply to"` | The text before the name of the comment it answers. |
| `author-label` | string | `"Author"` | The badge of an author comment. |
| `pending-label` | string | `"Awaiting review"` | The badge of a pending comment. |
| `removed-label` | string | `"This comment was removed."` | The text of a removed comment. |
| `expired-label` | string | `"Your comment was not published."` | The text of an expired comment. |

A label is joined to a name as "label name". That order does not fit every language.

## Properties

| Property | Type | Reflects |
|----------|------|----------|
| `commentId` | string | `comment-id` |
| `name` | string | `name` |
| `datetime` | string | `datetime` |
| `locale` | string | `locale` |
| `depth` | number | `depth` |
| `maxDepth` | number | `max-depth` |
| `replyToName` | string | `reply-to-name` |
| `author` | boolean | `author` |
| `status` | string | `status` |
| `replyLabel` | string | `reply-label` |
| `replyContextLabel` | string | `reply-context-label` |
| `authorLabel` | string | `author-label` |
| `pendingLabel` | string | `pending-label` |
| `removedLabel` | string | `removed-label` |
| `expiredLabel` | string | `expired-label` |

## Events

| Event | Bubbles | Composed | Cancelable | Detail |
|-------|---------|----------|------------|--------|
| `x-comment-reply` | yes | yes | no | `{ commentId: string }` |

`x-comment-reply` fires when the reader presses Reply.

## Slots

| Slot | Description |
|------|-------------|
| _(default)_ | The body of the comment. |
| `meta` | Content after the date, such as a short id. |

## States

| `status` | Avatar and name | Date | Body | Badge | Notice | Reply |
|----------|-----------------|------|------|-------|--------|-------|
| `published` | shown | shown | shown | `author-label` for an author comment | none | shown |
| `pending` | shown | shown | muted | `pending-label` | none | hidden |
| `removed` | hidden | hidden | hidden | none | `removed-label` | hidden |
| `expired` | hidden | shown | hidden | none | `expired-label` | hidden |

Inside `x-comments`, Reply is hidden while comments are closed.

## Depth

The indent level is the smaller of `depth` and `max-depth`. Each level indents by
`--x-comment-indent`. A reply always carries the text "Reply to" and the name. Within `max-depth`
the text is for screen readers only. Past it, the text shows.

## CSS Custom Properties

| Property | Default (light) | Default (dark) | Notes |
|----------|-----------------|----------------|-------|
| `--x-comment-indent` | `var(--x-space-xl,2rem)` | | Indent per level. `1rem` under 30rem. |
| `--x-comment-bg` | `var(--x-color-surface,#ffffff)` | `var(--x-color-surface,#1f2937)` | Background |
| `--x-comment-border` | `var(--x-color-border,#e5e7eb)` | `var(--x-color-border,#374151)` | Border colour |
| `--x-comment-radius` | `var(--x-radius-md,8px)` | | Corner radius |
| `--x-comment-author-bg` | `color-mix(in srgb,var(--x-color-primary,#2563eb) 8%,var(--x-comment-bg))` | `color-mix(in srgb,var(--x-color-primary,#60a5fa) 12%,var(--x-comment-bg))` | Background of an author comment |
| `--x-comment-pending-opacity` | `var(--x-opacity-placeholder,0.6)` | | Opacity of a pending body |
| `--x-comment-meta-color` | `var(--x-color-text-muted,#6b7280)` | `var(--x-color-text-muted,#9ca3af)` | Colour of the date and the context |

## Shadow DOM Parts

| Part | Element | Description |
|------|---------|-------------|
| `comment` | `<article>` | The comment. |
| `avatar` | `<x-avatar>` | The initials of the name. |
| `main` | `<div>` | Everything beside the avatar. |
| `meta` | `<header>` | The name, the badge and the date. |
| `name` | `<span>` | The name. |
| `badge` | `<span>` | The author or pending badge. |
| `date` | `<time>` | The date. |
| `context` | `<p>` | The "Reply to" text. |
| `body` | `<div>` | Holds the default slot. |
| `notice` | `<p>` | The removed or expired text. |
| `reply` | `<x-button>` | The Reply button. |

## Accessibility

- The comment is an `<article>` labelled by its name and its date. The `meta` slot is not part of
  the label.
- The avatar is hidden from assistive technology, since the name stands beside it.
- `focus()` on the host moves focus to the Reply button.

## Motion

Nothing animates.

## Responsive

Under 30rem the indent per level drops to `1rem`, so three levels fit in 320px.

## Usage Examples

### A pending comment

```html
<x-comment comment-id="p1" name="You" datetime="2026-10-28" status="pending">
  <p>Only the browser that sent this comment sees it.</p>
</x-comment>
```

### A short id after the date

```html
<x-comment comment-id="a1b2c3d4" name="Ann" datetime="2026-10-21">
  <p>The comment.</p>
  <span slot="meta"><code>a1b2</code> <x-copy text="a1b2">Copy</x-copy></span>
</x-comment>
```

### An expired comment

```html
<x-comment comment-id="x1" datetime="2026-09-20" status="expired"></x-comment>
```
