# x-avatar-group

A container that stacks `x-avatar` elements with configurable overlap, direction, and overflow truncation. Extra avatars beyond the `max` limit are replaced by a "+N" counter avatar.

---

## Tag

```html
<x-avatar-group></x-avatar-group>
```

---

## Attributes

| Attribute   | Type    | Default | Description                                                |
|-------------|---------|---------|-----------------------------------------------------------|
| `size`      | enum    | `"md"`  | Avatar size applied to all children: `xs` `sm` `md` `lg` `xl` |
| `overlap`   | enum    | `"md"`  | Overlap between adjacent avatars: `none` `sm` `md` `lg`   |
| `max`       | number  | —       | Maximum visible avatars. Overflow shown as "+N".           |
| `direction` | enum    | `"ltr"` | Stack direction: `ltr` (left-to-right) `rtl` (right-to-left) |
| `disabled`  | boolean | `false` | Dims the whole group                                       |
| `label`     | string  | —       | Accessible label for the group (`aria-label`)              |

### Overlap → CSS margin mapping

| Token  | Negative margin   |
|--------|-------------------|
| `none` | `0px`             |
| `sm`   | `-4px`            |
| `md`   | `-8px`            |
| `lg`   | `-12px`           |

---

## Slots

| Slot      | Description           |
|-----------|-----------------------|
| *(default)* | `x-avatar` elements |

---

## Overflow behaviour

When `max` is set and the number of slotted avatars exceeds it, the first `max` avatars are shown and an additional "+N" avatar is appended, where N is the hidden count.

---

## Accessibility

- The host element receives `role="group"`.
- When `label` is set, `aria-label` is applied to the host.
- When `disabled`, the host receives `aria-disabled="true"`.

---

## Examples

### Basic stacked group

```html
<x-avatar-group overlap="md">
  <x-avatar name="Alice Bob"></x-avatar>
  <x-avatar name="Carol Dan"></x-avatar>
  <x-avatar name="Eve Fox"></x-avatar>
</x-avatar-group>
```

### With overflow cap

```html
<x-avatar-group max="3" label="Team members">
  <x-avatar name="Alice Bob"></x-avatar>
  <x-avatar name="Carol Dan"></x-avatar>
  <x-avatar name="Eve Fox"></x-avatar>
  <x-avatar name="Greg Holt"></x-avatar>
  <x-avatar name="Ivan Jay"></x-avatar>
</x-avatar-group>
```

### Large size, no overlap

```html
<x-avatar-group size="lg" overlap="none">
  <x-avatar name="Alice Bob"></x-avatar>
  <x-avatar name="Carol Dan"></x-avatar>
</x-avatar-group>
```

### ClojureScript (hiccup renderer)

```clojure
[:x-avatar-group {:overlap "md" :max "3" :label "Team"}
 [:x-avatar {:name "Alice Bob"}]
 [:x-avatar {:name "Carol Dan"}]
 [:x-avatar {:name "Eve Fox"}]
 [:x-avatar {:name "Greg Holt"}]]
```
