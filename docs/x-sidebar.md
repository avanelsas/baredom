# x-sidebar

`x-sidebar` is a responsive navigation sidebar custom element with docked, overlay, and modal modes.

## Tag

```clojure
{:tag-name "x-sidebar"}
````

## Purpose

```clojure
{:purpose "responsive navigation sidebar"}
```

## Public API

### Attributes

```clojure
{:open {:predicate boolean?
        :kind :boolean-attribute
        :default false}
 :collapsed {:predicate boolean?
             :kind :boolean-attribute
             :default false}
 :placement {:predicate string?
             :values #{"left" "right"}
             :default "left"}
 :variant {:predicate string?
           :values #{"docked" "overlay" "modal"}
           :default "docked"}
 :breakpoint {:predicate number?
              :default 768}
 :label {:predicate string?
         :default "Sidebar"}}
```

### Properties

```clojure
{:open boolean?
 :collapsed boolean?}
```

### Events

```clojure
{:toggle {:open boolean?}
 :dismiss {:reason string?}}
```

Allowed dismiss reasons:

```clojure
#{"escape" "backdrop"}
```

## Behavior

### Responsive mode resolution

```clojure
{:rule '(if (< viewport-width breakpoint)
          "modal"
          declared-variant)}
```

### Collapsed support

```clojure
{:supported-in #{"docked"}
 :ignored-in #{"overlay" "modal"}}
```

### Event rules

```clojure
{:toggle {:fires-when :effective-open-state-changes}
 :dismiss {:fires-when :modal-user-dismissal}}
```

Programmatic property or attribute changes can fire `toggle` when the effective open state changes, but they must not fire `dismiss`.

## Accessibility

```clojure
{:landmark "navigation"
 :focus-trap-when-modal true
 :escape-dismisses-when-modal true
 :aria-hidden-when-closed true
 :backdrop-click-dismisses-when-modal true}
```

## Motion

All user content is distributed through the default slot, and the slot is always inside the animated panel.

```clojure
{:slot-inside-panel true
 :panel-primary-animated-element true
 :backdrop-separate true}
```

## Styling

The component exposes semantic CSS variables:

```clojure
{:--x-sidebar-bg string?
 :--x-sidebar-fg string?
 :--x-sidebar-backdrop string?
 :--x-sidebar-shadow string?
 :--x-sidebar-width string?
 :--x-sidebar-collapsed-width string?
 :--x-sidebar-duration string?
 :--x-sidebar-easing string?}
```

Host variables override internal defaults.

## Structure

```text
#shadow-root
├── <style>
├── <div class="backdrop" part="backdrop"></div>
└── <aside class="sidebar" part="sidebar">
    └── <div class="panel" part="panel">
        └── <slot></slot>
```

## Registration

Import the export namespace and call `init`.

```clojure
(ns app.main
  (:require [baredom.exports.x-sidebar :as x-sidebar]))

(x-sidebar/init)
```
