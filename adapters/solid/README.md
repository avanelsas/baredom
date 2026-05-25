# @vanelsas/baredom-solid

Solid 1.x wrapper components for [BareDOM](https://github.com/avanelsas/baredom) — auto-generated from component metadata.

Provides typed props, event handlers, ref forwarding, and signal-friendly controlled inputs for all BareDOM web components.

## Installation

```bash
npm install @vanelsas/baredom-solid @vanelsas/baredom solid-js
```

Solid's JSX compiler (`vite-plugin-solid`, `babel-preset-solid`, or `esbuild-plugin-solid`) must be configured in your project — this is the standard Solid setup.

## Usage

```tsx
import { XButton } from "@vanelsas/baredom-solid/x-button";
import { XAlert } from "@vanelsas/baredom-solid/x-alert";
import { XTheme } from "@vanelsas/baredom-solid/x-theme";

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

## Controlled inputs with signals

Form-control components (`XCheckbox`, `XSlider`, `XSelect`, `XSwitch`, `XRadio`, `XTextArea`, `XCombobox`, `XCurrencyField`, `XTabs`, `XPagination`) support a controlled mode driven by Solid signals. Supply the control prop (`checked` / `value` / `page`) plus a handler that updates it; the wrapper intercepts the cancelable change-request event and prevents the default DOM commit, so the signal is the single source of truth.

```tsx
import { createSignal } from "solid-js";
import { XCheckbox } from "@vanelsas/baredom-solid/x-checkbox";

function ControlledExample() {
  const [checked, setChecked] = createSignal(false);
  return (
    <XCheckbox
      checked={checked()}
      onChangeRequest={(e) => setChecked(e.detail.checked)}
    >
      Subscribe
    </XCheckbox>
  );
}
```

Pass `defaultChecked` / `defaultValue` / `defaultPage` instead for uncontrolled mode — the wrapper writes the initial attribute and lets the component manage its own state.

## Theme presets

```tsx
import { useRegisterPreset } from "@vanelsas/baredom-solid/composables";
import { XTheme } from "@vanelsas/baredom-solid/x-theme";

function App() {
  useRegisterPreset("brand", {
    light: { "--x-color-primary": "#e11d48" },
    dark:  { "--x-color-primary": "#fb7185" },
  });
  return <XTheme preset="brand">...</XTheme>;
}
```

## License

MIT
