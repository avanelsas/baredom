# x-image

A stateless, themeable image component that reserves layout space via an optional aspect ratio, shows a skeleton shimmer while loading, fades in on successful load, and renders a default or slotted fallback on error.

---

## Tag

```html
<x-image src="/photo.jpg" alt="A description"></x-image>
```

---

## Attributes

| Attribute     | Type              | Default    | Description                                                         |
|---------------|-------------------|------------|---------------------------------------------------------------------|
| `src`         | URL               | —          | Image URL. Sanitized against a protocol whitelist                   |
| `alt`         | string            | —          | Alternative text for screen readers. Required unless `decorative`   |
| `decorative`  | boolean           | `false`    | Marks the image as purely decorative; sets `alt=""` + `aria-hidden` |
| `ratio`       | `"W:H"` \| `auto` | `auto`     | Aspect ratio, e.g. `16:9`, `1:1`, `4:3`. Prevents layout shift      |
| `fit`         | enum              | `"cover"`  | `cover` `contain` `fill` `none` `scale-down`                        |
| `position`    | string            | `"center"` | CSS `object-position` value                                         |
| `loading`     | enum              | `"lazy"`   | `lazy` or `eager` (forwarded to inner `<img>`)                      |

**Accepted `src` protocols:** `http:`, `https:`, `data:`, `blob:`, plus relative URLs. `javascript:` and other schemes are silently dropped.

**`ratio` format:** `W:H` or `W/H` with positive numbers (integer or decimal). Invalid values log a console warning once per element and fall back to intrinsic sizing.

---

## Properties

| Property        | Type            | Reflects attribute | Notes                       |
|-----------------|-----------------|--------------------|-----------------------------|
| `src`           | string          | `src`              |                             |
| `alt`           | string          | `alt`              |                             |
| `decorative`    | boolean         | `decorative`       | Present-as-true             |
| `ratio`         | string          | `ratio`            |                             |
| `fit`           | string          | `fit`              |                             |
| `position`      | string          | `position`         |                             |
| `loading`       | string          | `loading`          |                             |
| `naturalWidth`  | number          | —                  | Read-only; `0` before load  |
| `naturalHeight` | number          | —                  | Read-only; `0` before load  |
| `state`         | string          | —                  | Read-only; `loading` \| `loaded` \| `error` |

---

## Events

| Event           | Bubbles | Composed | Cancelable | Detail                                             |
|-----------------|---------|----------|------------|----------------------------------------------------|
| `x-image-load`  | yes     | yes      | no         | `{ src, naturalWidth, naturalHeight }`             |
| `x-image-error` | yes     | yes      | no         | `{ src }`                                          |

Events fire once per successful transition into their respective state. Changing `src` resets state to `loading` and will fire a new event when the new image resolves.

---

## Slots

| Slot    | Description                              |
|---------|------------------------------------------|
| `error` | Custom fallback content shown on failure |

```html
<x-image src="/missing.png" alt="Fallback demo">
  <div slot="error">Image unavailable. <a href="/contact">Report</a></div>
</x-image>
```

When no `slot="error"` content is provided, a default `⚠ Image unavailable` message is shown.

---

## Parts

| Part            | Description                     |
|-----------------|---------------------------------|
| `frame`         | Outer aspect-ratio container    |
| `shimmer`       | Skeleton placeholder overlay    |
| `image`         | Inner `<img>` element           |
| `error`         | Error-state container           |
| `error-default` | Default fallback wrapper        |
| `error-glyph`   | Default `⚠` glyph `<span>`      |
| `error-text`    | Default error text `<span>`     |

---

## CSS Custom Properties

| Variable                        | Default                                  | Description                    |
|---------------------------------|------------------------------------------|--------------------------------|
| `--x-image-radius`              | `var(--x-radius-md, 8px)`                | Frame border radius            |
| `--x-image-bg`                  | `var(--x-color-surface, #f3f4f6)`        | Frame background / placeholder |
| `--x-image-border`              | `0`                                      | Frame border shorthand         |
| `--x-image-shimmer-color`       | `var(--x-color-border, rgba(0,0,0,.08))` | Shimmer base colour            |
| `--x-image-shimmer-highlight`   | `rgba(255,255,255,.65)`                  | Shimmer sweep highlight        |
| `--x-image-shimmer-duration`    | `1.5s`                                   | Shimmer animation duration     |
| `--x-image-fade-duration`       | `var(--x-transition-duration, 200ms)`    | Fade-in duration               |
| `--x-image-fade-easing`         | `var(--x-transition-easing, ease)`       | Fade-in easing                 |
| `--x-image-text`                | `var(--x-color-text-muted, …)`           | Error text colour              |
| `--x-image-font-family`         | `var(--x-font-family, …)`                | Error text font                |
| `--x-image-font-size`           | `var(--x-font-size-sm, 0.875rem)`        | Error text size                |
| `--x-image-error-gap`           | `var(--x-space-xs, 6px)`                 | Gap between glyph and text     |

Dark-mode variants for `--x-image-bg`, `--x-image-shimmer-*`, and `--x-image-text` are applied automatically via `@media (prefers-color-scheme: dark)`.

---

## Accessibility

- When not `decorative`, the host receives `role="img"` and forwards `alt` to the inner `<img>`.
- When `decorative`, the host receives `aria-hidden="true"` and the inner `<img>` gets `alt=""`.
- Omitting `alt` on a non-decorative image logs a one-time console warning.
- The error region carries `role="img"` with an `aria-label` derived from `alt` (or `"Image failed to load"`).
- Shimmer is marked `aria-hidden="true"`.
- Shimmer animation and fade-in are disabled under `@media (prefers-reduced-motion: reduce)`.

---

## Stateless rendering

The component is fully re-derived from HTML attributes on every change. No internal state beyond cached DOM refs and the current load-state marker. The underlying `<img>` has no abort primitive; when `src` changes, the previous request is superseded by browser semantics, and only the most recently set `src` dispatches events.

`naturalWidth` and `naturalHeight` return `0` before the image has finished loading and reflect the inner `<img>`'s natural dimensions afterwards.

---

## Examples

### Fixed aspect ratio

```html
<x-image src="/hero.jpg" alt="Team photo" ratio="16:9"></x-image>
```

### Decorative image

```html
<x-image src="/pattern.svg" decorative ratio="1:1"></x-image>
```

### Custom error fallback

```html
<x-image src="/may-404.jpg" alt="Preview" ratio="4:3">
  <div slot="error">Preview unavailable</div>
</x-image>
```

### Listening for load

```js
document.querySelector('x-image').addEventListener('x-image-load', e => {
  console.log('loaded', e.detail.src, e.detail.naturalWidth, e.detail.naturalHeight);
});
```

### ClojureScript (hiccup renderer)

```clojure
[:x-image {:src "/photo.jpg" :alt "Profile" :ratio "1:1" :fit "cover"}]

[:x-image {:src      "/bg.png"
           :decorative true
           :ratio    "16:9"}]
```
