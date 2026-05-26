# Components

The full catalogue of BareDOM components — 103 web components across 11 categories. Every component is a native Custom Element with auto-generated TypeScript declarations and is exposed in all five [framework adapters](../README.md#framework-adapters).

Tag names are case-insensitive in HTML but always lowercase-kebab-case in the source. Component documentation links to deeper guides with API tables, examples, and a11y notes.

- [Form (23)](#form-23)
- [Feedback (11)](#feedback-11)
- [Navigation (8)](#navigation-8)
- [Layout (10)](#layout-10)
- [Data (11)](#data-11)
- [Overlay (9)](#overlay-9)
- [Display (6)](#display-6)
- [Animation (6)](#animation-6)
- [Effects (12)](#effects-12)
- [Scroll (5)](#scroll-5)
- [Utility (2)](#utility-2)

---

## Form (23)

| Tag | Description |
|-----|-------------|
| [`<x-button>`](./x-button.md) | Action control. Variants: `primary`, `secondary`, `tertiary`, `ghost`, `danger`. Sizes: `sm`, `md`, `lg`. States: `disabled`, `loading`, `pressed`. Icon slots. |
| [`<x-checkbox>`](./x-checkbox.md) | Boolean input. Reflects `checked` and `indeterminate` states to attributes. |
| [`<x-color-picker>`](./x-color-picker.md) | Colour picker with 2D saturation/brightness area, hue strip, optional alpha, preset swatches, eyedropper, and clipboard copy. Inline or popover mode. |
| [`<x-combobox>`](./x-combobox.md) | Single-select combobox with type-ahead filtering. Value must match one of the provided `<option>` children. |
| [`<x-copy>`](./x-copy.md) | Copy-to-clipboard utility button with success feedback. |
| [`<x-currency-field>`](./x-currency-field.md) | Formatted currency input with locale-aware masking. |
| [`<x-date-picker>`](./x-date-picker.md) | Calendar-based date selection with keyboard navigation. |
| [`<x-fieldset>`](./x-fieldset.md) | Groups related form controls with a styled legend. |
| [`<x-file-download>`](./x-file-download.md) | Download trigger that initiates a file transfer. |
| [`<x-file-upload>`](./x-file-upload.md) | Drag-and-drop upload zone with click-to-browse, file list with thumbnails, validation, and form integration. |
| [`<x-form>`](./x-form.md) | Form wrapper with coordinated validation state. |
| [`<x-form-field>`](./x-form-field.md) | Label + input wrapper with error and hint text slots. |
| [`<x-multi-combobox>`](./x-multi-combobox.md) | Multi-select combobox with type-ahead filtering. Selected items display as removable chips. |
| [`<x-otp-input>`](./x-otp-input.md) | One-time-password input with single-character slots, auto-advance, paste distribution, and full keyboard nav. |
| [`<x-radio>`](./x-radio.md) | Single-choice input within a radio group. |
| [`<x-range-slider>`](./x-range-slider.md) | Dual-handle accessible range slider with form association. Min/max, step, and label support. |
| [`<x-rating>`](./x-rating.md) | Discrete star-rating with form association and full keyboard support. |
| [`<x-search-field>`](./x-search-field.md) | Search input with integrated clear button and search icon. |
| [`<x-select>`](./x-select.md) | Dropdown select control with custom styling. |
| [`<x-slider>`](./x-slider.md) | Range slider with step, min/max, and value display. |
| [`<x-stepper>`](./x-stepper.md) | Multi-step form progress indicator with navigation. |
| [`<x-switch>`](./x-switch.md) | Toggle switch for boolean settings. |
| [`<x-text-area>`](./x-text-area.md) | Multi-line text input with auto-resize option. |

## Feedback (11)

| Tag | Description |
|-----|-------------|
| [`<x-alert>`](./x-alert.md) | Semantic alert banner. Types: `info`, `success`, `warning`, `error`. Auto-dismiss with `timeout-ms`. Fires `x-alert-dismiss`. |
| [`<x-badge>`](./x-badge.md) | Small inline label for counts, states, and categories. |
| [`<x-chip>`](./x-chip.md) | Compact tag component, optionally removable. |
| [`<x-notification-center>`](./x-notification-center.md) | Notification hub for aggregating and managing in-app notifications. |
| [`<x-progress>`](./x-progress.md) | Linear progress bar with determinate and indeterminate modes. |
| [`<x-progress-circle>`](./x-progress-circle.md) | Circular progress indicator for compact spaces. |
| [`<x-skeleton>`](./x-skeleton.md) | Animated loading placeholder that mirrors content shape. |
| [`<x-skeleton-group>`](./x-skeleton-group.md) | Container that synchronizes child `<x-skeleton>` animation timing and offers declarative layout presets. |
| [`<x-spinner>`](./x-spinner.md) | Inline loading spinner with size and colour variants. |
| [`<x-toast>`](./x-toast.md) | Single transient notification with enter/exit animations and auto-dismiss. |
| [`<x-toaster>`](./x-toaster.md) | Toast manager. Positions a queue of `<x-toast>` elements, enforces `max-toasts`, and fires `x-toaster-dismiss`. |

## Navigation (8)

| Tag | Description |
|-----|-------------|
| [`<x-breadcrumbs>`](./x-breadcrumbs.md) | Hierarchical path trail with separator customisation. |
| [`<x-menu>`](./x-menu.md) | Vertical menu container coordinating `<x-menu-item>` children. |
| [`<x-menu-item>`](./x-menu-item.md) | Individual menu entry with icon, label, description, and keyboard support. |
| [`<x-navbar>`](./x-navbar.md) | Top navigation bar with responsive slot layout. |
| [`<x-pagination>`](./x-pagination.md) | Page navigation controls with first/previous/next/last and page-size selection. |
| [`<x-sidebar>`](./x-sidebar.md) | Collapsible side navigation panel with collapse/expand animation. |
| [`<x-tab>`](./x-tab.md) | Individual tab within an `<x-tabs>` container. |
| [`<x-tabs>`](./x-tabs.md) | Tab container that coordinates `<x-tab>` children, manages active state, and fires change events. |

## Layout (10)

| Tag | Description |
|-----|-------------|
| [`<x-bento-grid>`](./x-bento-grid.md) | CSS Grid container for asymmetric "bento box" layouts with dense auto-placement. Pair with `<x-bento-item>`. |
| [`<x-bento-item>`](./x-bento-item.md) | Structural wrapper that controls column and row spanning inside an `<x-bento-grid>`. |
| [`<x-card>`](./x-card.md) | Surface container. Variants: `elevated`, `outlined`, `filled`, `ghost`. Interactive mode available. |
| [`<x-collapse>`](./x-collapse.md) | Expandable/collapsible section with animated height transition. |
| [`<x-container>`](./x-container.md) | Responsive max-width container with configurable padding. |
| [`<x-divider>`](./x-divider.md) | Horizontal or vertical visual separator. |
| [`<x-grid>`](./x-grid.md) | CSS Grid layout component with responsive column configuration. |
| [`<x-proximity-list>`](./x-proximity-list.md) | Horizontal or vertical list whose items scale up as the pointer approaches — dock effect on any list. |
| [`<x-spacer>`](./x-spacer.md) | Flexible spacing element for flexbox and grid layouts. |
| [`<x-split-pane>`](./x-split-pane.md) | Resizable two-panel layout with draggable divider, horizontal or vertical. |

## Data (11)

| Tag | Description |
|-----|-------------|
| [`<x-avatar>`](./x-avatar.md) | User photo or initials display. Shape, size, and status dot variants. |
| [`<x-avatar-group>`](./x-avatar-group.md) | Overlapping avatar stack for representing multiple users. |
| [`<x-calendar>`](./x-calendar.md) | Inline month calendar — always-visible companion to date pickers, with keyboard navigation. |
| [`<x-carousel>`](./x-carousel.md) | Accessible carousel with swipe/drag, arrows, dot indicators, autoplay, slide/fade transitions, and horizontal/vertical orientation. |
| [`<x-chart>`](./x-chart.md) | Data visualisation component for common chart types. |
| [`<x-stat>`](./x-stat.md) | KPI / metric card with value, label, trend, and icon slots. |
| [`<x-table>`](./x-table.md) | Data grid using CSS subgrid. Supports sorting, single/multi-select, striping, and accessible captions. |
| [`<x-table-cell>`](./x-table-cell.md) | Table cell for header and data modes, with sort indicator and alignment control. |
| [`<x-table-row>`](./x-table-row.md) | Table row with interactive selection and `x-table-row-select` event. |
| [`<x-timeline>`](./x-timeline.md) | Vertical timeline container that coordinates `<x-timeline-item>` children. |
| [`<x-timeline-item>`](./x-timeline-item.md) | Individual timeline event with time, icon, heading, and body slots. |

## Overlay (9)

| Tag | Description |
|-----|-------------|
| [`<x-cancel-dialogue>`](./x-cancel-dialogue.md) | Confirmation modal for destructive cancel actions. |
| [`<x-command-palette>`](./x-command-palette.md) | Keyboard-accessible global search and command interface. |
| [`<x-context-menu>`](./x-context-menu.md) | Right-click / long-press contextual action menu. |
| [`<x-drawer>`](./x-drawer.md) | Off-canvas sliding panel, configurable from any edge. |
| [`<x-dropdown>`](./x-dropdown.md) | Positioned dropdown container for menus and selection. |
| [`<x-modal>`](./x-modal.md) | Centred dialog with backdrop, focus trap, and `Escape` to close. |
| [`<x-popover>`](./x-popover.md) | Anchored popover for tooltips, help text, and contextual UI. |
| [`<x-tooltip>`](./x-tooltip.md) | Lightweight non-interactive overlay showing supplementary text on hover or focus of a trigger element. |
| [`<x-welcome-tour>`](./x-welcome-tour.md) | Guided product tour with spotlight backdrop, popover steps, and configurable connectors between steps. |

## Display (6)

| Tag | Description |
|-----|-------------|
| [`<x-code>`](./x-code.md) | Code-display element that reads source code from its own light DOM and applies syntax styling. |
| [`<x-icon>`](./x-icon.md) | Themeable wrapper around a slotted `<svg>`. Handles sizing, theme-aware colour, and accessibility. |
| [`<x-image>`](./x-image.md) | Themeable image with aspect ratio, skeleton shimmer while loading, fade-in on load, and fallback slot. |
| [`<x-kbd>`](./x-kbd.md) | Inline element for keyboard keys and shortcuts. Renders styled `<kbd>` caps from a combo string. |
| [`<x-spotlight-card>`](./x-spotlight-card.md) | Card surface with a soft radial glow that follows the cursor while over the card. Decorative only. |
| [`<x-typography>`](./x-typography.md) | Themeable text wrapper. Variants: headings, body, captions, code, keyboard, blockquotes, and more. |

## Animation (6)

| Tag | Description |
|-----|-------------|
| [`<x-kinetic-canvas>`](./x-kinetic-canvas.md) | Animated canvas background with slotted content. Renders particle/character animations behind any content. |
| [`<x-kinetic-font>`](./x-kinetic-font.md) | Variable-font axis interpolation driven by spring physics, reacting to cursor proximity or scroll velocity. |
| [`<x-kinetic-typography>`](./x-kinetic-typography.md) | Text along SVG paths with motion effects — scrolling, bouncing, oscillating movement, combinable. |
| [`<x-morph-stack>`](./x-morph-stack.md) | Continuous transformation between UI states — layout, shape, material, and content evolve as one surface. |
| [`<x-soft-body>`](./x-soft-body.md) | Card whose border is an SVG path driven by spring physics — control points displace and spring back. |
| [`<x-splash>`](./x-splash.md) | Full-viewport splash screen for app initialization. Dismisses with a fade-out when `active` is removed. |

## Effects (12)

| Tag | Description |
|-----|-------------|
| [`<x-confetti>`](./x-confetti.md) | Celebration effect that emits a burst of particles on demand. Triggered imperatively; configuration via attributes. |
| [`<x-gaussian-blur>`](./x-gaussian-blur.md) | Decorative background with animated blurred colour blobs, producing a soft, dreamy gradient effect. |
| [`<x-liquid-dock>`](./x-liquid-dock.md) | Viscous floating navigation dock with SVG goo filter. Liquid surface bulges toward your cursor. |
| [`<x-liquid-fill>`](./x-liquid-fill.md) | Scroll progress indicator that fills with animated liquid. Wave motion reacts to scroll speed. |
| [`<x-liquid-glass>`](./x-liquid-glass.md) | Content container shaped as a shifting translucent blob, merged via SVG goo filter into organic boundaries. |
| [`<x-metaball-cursor>`](./x-metaball-cursor.md) | Organic blobs that follow the cursor with latency. Overlapping blobs stretch and fuse like mercury drops. |
| [`<x-neural-glow>`](./x-neural-glow.md) | WebGL bioluminescent neural network background — softly glowing orbs connected by pulsing lines. |
| [`<x-organic-divider>`](./x-organic-divider.md) | Curved organic section divider with layered SVG shapes, animations, and full theming. |
| [`<x-organic-progress>`](./x-organic-progress.md) | Progress indicator that grows a vine or honeycomb lattice structure with simplex-noise organic motion. |
| [`<x-organic-shape>`](./x-organic-shape.md) | Organic blob-shaped container. Decorative accent or content wrapper with fluid natural boundaries. |
| [`<x-particle-button>`](./x-particle-button.md) | Button whose surface emits and reabsorbs visual fragments on interaction. Energetic and materially alive. |
| [`<x-ripple-effect>`](./x-ripple-effect.md) | Container that applies a water-like SVG distortion ripple to its content on mouse click. |

## Scroll (5)

| Tag | Description |
|-----|-------------|
| [`<x-scroll>`](./x-scroll.md) | Flexible scroll container with horizontal/vertical carousel scrolling, snap, auto-play, loop, dots, and keys. |
| [`<x-scroll-parallax>`](./x-scroll-parallax.md) | Parallax container where children move at different speeds relative to scroll via `data-speed`. |
| [`<x-scroll-stack>`](./x-scroll-stack.md) | Scroll-driven card stacking. Cards animate upward and stack on top of each other like a deck. |
| [`<x-scroll-story>`](./x-scroll-story.md) | Scrollytelling: a sticky media panel stays pinned while text "steps" scroll past and trigger state changes. |
| [`<x-scroll-timeline>`](./x-scroll-timeline.md) | Scroll-driven timeline along a vertical track with animated markers and progress fill. |

## Utility (2)

| Tag | Description |
|-----|-------------|
| [`<x-i18n>`](./x-i18n.md) | Inline translation element. Renders translated text for a key, resolved from the nearest `<x-i18n-provider>`. |
| [`<x-i18n-provider>`](./x-i18n-provider.md) | Internationalization provider. Fetches translation JSON and supplies locale context to descendant `<x-i18n>`. |

---

See also: [`<x-theme>`](./x-theme.md) (theming provider, [README section](../README.md#theming)) and [`<x-trace-history>`](./x-trace-history.md) (time-travel debugger, [README section](../README.md#time-travel-debugger)).
