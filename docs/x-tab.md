# x-tab

## Overview

`x-tab` is a **themeable tab trigger component** used within a tab interface.
It represents a single selectable tab that labels and controls an associated tab panel.

`x-tab` is designed to work standalone or coordinated by an `x-tabs` container.

The component implements the **WAI-ARIA tab trigger semantics** and dispatches events when activated.

---

## Installation

Register the component:

```javascript
import { init } from "@vanelsas/baredom/x-tab";

init();
```

Or if using the provided export API:

```javascript
import { register } from "@vanelsas/baredom/x-tab";

register();
```

---

## Basic Usage

```html
<x-tab value="overview" selected controls="panel-overview">
  Overview
</x-tab>
```

Typically used with `x-tabs`:

```html
<x-tabs>
  <x-tab value="overview" controls="panel-overview">Overview</x-tab>
  <x-tab value="analytics" controls="panel-analytics">Analytics</x-tab>
</x-tabs>

<section id="panel-overview">Overview content</section>
<section id="panel-analytics">Analytics content</section>
```

---

# Attributes

| Attribute     | Type                       | Default    | Description                                    |
| ------------- | -------------------------- | ---------- | ---------------------------------------------- |
| `value`       | string                     | —          | Identifier used by `x-tabs` to track selection |
| `selected`    | boolean                    | false      | Marks the tab as the currently selected tab    |
| `disabled`    | boolean                    | false      | Prevents interaction                           |
| `orientation` | horizontal | vertical      | horizontal | Styling hook                                   |
| `size`        | sm | md | lg               | md         | Visual sizing                                  |
| `variant`     | default | underline | pill | default    | Visual style                                   |
| `label`       | string                     | —          | Accessible label override                      |
| `controls`    | string                     | —          | ID of the associated panel                     |

Invalid enum values are normalized internally without rewriting host attributes.

---

# Properties

| Property   | Type    | Description                 |
| ---------- | ------- | --------------------------- |
| `selected` | boolean | Reflects the selected state |
| `disabled` | boolean | Reflects the disabled state |
| `value`    | string  | Tab identifier              |

Example:

```javascript
tab.selected = true;
```

---

# Events

## tab-select

Dispatched when the tab is activated.

```javascript
tab.addEventListener("tab-select", event => {
  console.log(event.detail.value);
});
```

Event detail:

```javascript
{
  value: string
}
```

The event bubbles and is composed so that parent components such as `x-tabs` can coordinate selection.

---

# Slots

| Slot    | Description       |
| ------- | ----------------- |
| default | Tab label content |

Example:

```html
<x-tab value="analytics">
  📊 Analytics
</x-tab>
```

---

# Accessibility

`x-tab` implements standard tab trigger semantics.

| Attribute       | Behavior                           |
| --------------- | ---------------------------------- |
| `role="tab"`    | Applied automatically              |
| `aria-selected` | Reflects `selected`                |
| `aria-disabled` | Reflects `disabled`                |
| `aria-controls` | Derived from `controls`            |
| `aria-label`    | Derived from `label`               |
| `tabindex`      | `0` for active tab, `-1` otherwise |

Reading order is preserved and the component does not override semantics of slotted content.

Keyboard activation:

| Key   | Action        |
| ----- | ------------- |
| Enter | Activates tab |
| Space | Activates tab |

Navigation between tabs is coordinated by `x-tabs`.

---

# Styling

`x-tab` exposes semantic CSS variables.

## CSS Custom Properties

```css
--x-tab-color
--x-tab-background
--x-tab-border-color
--x-tab-hover-background
--x-tab-selected-color
--x-tab-selected-background
--x-tab-selected-border-color
--x-tab-focus-ring
--x-tab-disabled-opacity
--x-tab-padding-sm
--x-tab-padding-md
--x-tab-padding-lg
--x-tab-radius
--x-tab-transition-duration
--x-tab-transition-timing
```

Host variables override component defaults.

---

# Motion

The component includes minimal transitions for:

* hover
* focus
* selection

All animations respect:

```
prefers-reduced-motion
```
