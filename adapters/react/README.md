# @vanelsas/baredom-react

React 19 wrapper components for [BareDOM](https://github.com/avanelsas/baredom) — auto-generated from component metadata.

Provides typed props, event handlers, and ref forwarding for all 90+ BareDOM web components.

## Installation

```bash
npm install @vanelsas/baredom-react @vanelsas/baredom react
```

## Usage

```tsx
import { XButton } from "@vanelsas/baredom-react/x-button";
import { XAlert } from "@vanelsas/baredom-react/x-alert";
import { XTheme } from "@vanelsas/baredom-react/x-theme";

function App() {
  return (
    <XTheme>
      <XButton
        disabled={false}
        onPress={(e) => console.log("Pressed!", e.detail.source)}
      >
        Click me
      </XButton>

      <XAlert
        type="success"
        text="Operation completed"
        dismissible
        onDismiss={() => console.log("Dismissed")}
      />
    </XTheme>
  );
}
```

## Features

- **Typed props** — all component properties are typed in the props interface
- **Event handlers** — custom events mapped to `onEventName` props (e.g. `onPress`, `onDismiss`, `onChange`)
- **Ref forwarding** — `forwardRef` with typed element refs for accessing methods
- **Next.js compatible** — every wrapper includes `"use client"` directive
- **Tree-shakable** — import individual components to keep bundle size small

## Event naming

BareDOM event names are automatically converted to React-style callback props:

| DOM event | React prop |
|-----------|-----------|
| `press` | `onPress` |
| `x-alert-dismiss` | `onDismiss` |
| `x-switch-change` | `onChange` |
| `value-change` | `onValueChange` |
| `hover-start` | `onHoverStart` |

The component's tag-name prefix (e.g. `x-alert-`) is stripped, and the remainder is camelCased with an `on` prefix.

## Refs and methods

Access the underlying DOM element and its methods via ref:

```tsx
import { useRef } from "react";
import { XModal } from "@vanelsas/baredom-react/x-modal";
import type { XModal as XModalElement } from "@vanelsas/baredom/x-modal";

function Dialog() {
  const ref = useRef<XModalElement>(null);

  return (
    <>
      <button onClick={() => ref.current?.show()}>Open</button>
      <XModal ref={ref} onToggle={(e) => console.log(e.detail)}>
        Modal content
      </XModal>
    </>
  );
}
```

## Theming

Wrap your app with `<XTheme>` to enable BareDOM's design tokens. Components automatically adapt to system dark/light mode.

```tsx
import { XTheme } from "@vanelsas/baredom-react/x-theme";

function App() {
  return (
    <XTheme preset="aurora">
      {/* All BareDOM components inherit theme tokens */}
    </XTheme>
  );
}
```

## Requirements

- React 19+
- @vanelsas/baredom 2.4.1+

## Auto-generated

The wrapper components are auto-generated from BareDOM's component model metadata using `bb scripts/generate_react.bb`. Adding a new component to BareDOM automatically produces its React wrapper.

## License

MIT
