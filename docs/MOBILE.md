# Mobile and Responsive Guide

All components must work on viewports from 320px up. Apply these rules in every component.

## Rules

### No fixed widths that can overflow
Cap with `min(Xpx, calc(100vw - 2rem))` or add `max-width:calc(100vw - 1rem)` on positioned panels (menus, popovers, dropdowns, modals).

### Use `100dvh`, never `100vh`
Mobile browsers have dynamic toolbars that change the viewport height; `dvh` accounts for this.

### Use `100%`, never `100vw`
`100vw` includes scrollbar width and causes horizontal overflow on full-width elements.

### Use pointer events, never mouse events
`pointermove`, `pointerdown`, `pointerup`, `pointerenter`, `pointerleave` — these fire on both mouse and touch. Never use `mousemove`, `mousedown`, `mouseenter`, etc.

### Touch targets >= 44px on coarse pointers
Add `@media (pointer:coarse)` rules to enlarge interactive elements (thumbs, buttons) that are smaller than 44px at their default size.

### Demo pages
Link `demo-responsive.css` for shared responsive breakpoints and theme. Use `var(--page-bg)`, `var(--surface-bg)`, etc. — do not hardcode theme colours in demo HTML.
