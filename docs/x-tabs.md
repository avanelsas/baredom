# x-tabs

## Overview

`x-tabs` is a **tab system container** that coordinates multiple `x-tab` triggers and their associated tab panels.

It manages:

* selected tab state
* keyboard navigation
* roving tabindex
* panel visibility
* tab selection events

`x-tabs` complements `x-tab` and provides the coordination layer for a full tab interface.

---

## Installation

```javascript
import { init } from "@vanelsas/baredom/x-tabs";

init();
```

---

## Basic Usage

```html
<x-tabs>
  <x-tab value="overview" controls="panel-overview">Overview</x-tab>
  <x-tab value="analytics" controls="panel-analytics">Analytics</x-tab>
  <x-tab value="reports" controls="panel-reports">Reports</x-tab>
</x-tabs>

<section id="panel-overview">Overview content</section>
<section id="panel-analytics" hidden>Analytics content</section>
<section id="panel-reports" hidden>Reports content</section>
```

`x-tabs` will automatically:

* select the first enabled tab
* manage keyboard navigation
* show the associated panel

---

# Attributes

| Attribute     | Type                  | Default    | Description                      |
| ------------- | --------------------- | ---------- | -------------------------------- |
| `value`       | string                | —          | Controlled selected tab          |
| `orientation` | horizontal | vertical | horizontal | Keyboard navigation direction    |
| `activation`  | auto | manual         | auto       | Activation strategy              |
| `label`       | string                | —          | Accessible label                 |
| `loop`        | boolean               | false      | Enables keyboard wrap navigation |

---

# Properties

| Property | Type   | Description                |
| -------- | ------ | -------------------------- |
| `value`  | string | Current selected tab value |

Example:

```javascript
tabs.value = "analytics";
```

---

# Events

## value-change

Fired when the selected tab changes.

```javascript
tabs.addEventListener("value-change", event => {
  console.log(event.detail.value);
});
```

Event detail:

```javascript
{
  value: string
}
```

---

# Keyboard Navigation

Keyboard behavior follows the WAI-ARIA tab pattern.

## Horizontal

| Key        | Action       |
| ---------- | ------------ |
| ArrowRight | Next tab     |
| ArrowLeft  | Previous tab |

## Vertical

| Key       | Action       |
| --------- | ------------ |
| ArrowDown | Next tab     |
| ArrowUp   | Previous tab |

## Global

| Key  | Action            |
| ---- | ----------------- |
| Home | First enabled tab |
| End  | Last enabled tab  |

---

## Activation Modes

### auto

Selecting a tab occurs immediately during keyboard navigation.

### manual

Arrow keys move focus only.
Selection occurs when pressing:

* Enter
* Space

---

# Disabled Tabs

Disabled tabs:

* cannot be selected
* are skipped during keyboard navigation

---

# Panel Coordination

Panels are associated using the `controls` attribute on `x-tab`.

Example:

```html
<x-tab value="analytics" controls="panel-analytics">
```

The container will automatically:

* show the panel for the selected tab
* hide other panels using the `hidden` attribute

---

# Styling

`x-tabs` is intentionally lightweight.

Most visual styling is handled by `x-tab`.

## CSS Custom Properties

```css
--x-tabs-gap
```

---

# Parts

| Part   | Description        |
| ------ | ------------------ |
| `base` | Internal container |

---

# Accessibility

`x-tabs` coordinates the **tablist pattern**.

| Element           | Role                       |
| ----------------- | -------------------------- |
| internal base     | `tablist`                  |
| x-tab             | `tab`                      |
| associated panels | `tabpanel` (user supplied) |

The component:

* preserves DOM reading order
* does not override semantics of slotted content
* applies ARIA coordination only where necessary

---

# Motion

`x-tabs` introduces minimal or no motion.
Any transitions respect:

```
prefers-reduced-motion
```

---

# Example

```html
<x-tabs activation="auto">
  <x-tab value="overview" controls="p1">Overview</x-tab>
  <x-tab value="analytics" controls="p2">Analytics</x-tab>
  <x-tab value="reports" controls="p3">Reports</x-tab>
</x-tabs>

<section id="p1">Overview content</section>
<section id="p2" hidden>Analytics content</section>
<section id="p3" hidden>Reports content</section>
```
