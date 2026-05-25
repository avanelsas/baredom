# @vanelsas/baredom-vue

Vue 3 wrapper components for [BareDOM](https://github.com/avanelsas/baredom) — auto-generated from component metadata.

Provides typed props, typed `emits`, `v-model` bridging, and `expose()`-based refs for all 90+ BareDOM web components.

## Installation

```bash
npm install @vanelsas/baredom-vue @vanelsas/baredom vue
```

## Usage

```vue
<script setup lang="ts">
import { XButton } from "@vanelsas/baredom-vue/x-button";
import { XAlert } from "@vanelsas/baredom-vue/x-alert";
import { XTheme } from "@vanelsas/baredom-vue/x-theme";
</script>

<template>
  <XTheme>
    <XButton :disabled="false" @press="(e) => console.log('Pressed!', e.detail.source)">
      Click me
    </XButton>

    <XAlert
      type="success"
      text="Operation completed"
      :dismissible="true"
      @dismiss="() => console.log('Dismissed')"
    />
  </XTheme>
</template>
```

## Features

- **Typed props** — all component properties are typed via Vue's `props: { ... }` runtime declarations.
- **Typed emits** — `CustomEvent<...>` payloads inferred from the underlying element's event schema.
- **`v-model` bridging** — form-aware components (checkbox, switch, radio, slider, select, combobox, currency-field, text-area, tabs, pagination) accept `v-model` directly.
- **Ref forwarding** — each wrapper `expose()`s an `el` ref to the underlying custom element instance.
- **Tree-shakable** — import individual components to keep bundle size small.

## Event naming

BareDOM event names are exposed as **kebab-case** emit keys, matching the underlying DOM `CustomEvent` name 1:1. The tag-name prefix is stripped:

| DOM event | Vue listener |
|-----------|-------------|
| `press` | `@press` |
| `x-alert-dismiss` | `@dismiss` |
| `x-switch-change` | `@change` |
| `value-change` | `@value-change` |
| `hover-start` | `@hover-start` |

Volar normalizes `@hover-start` ↔ `@hoverStart` in templates — both type-check against the same emit declaration.

## Reserved prop names

Vue 3 reserves `key`, `ref`, and `is` as VNode-level identifiers — a component prop with one of these names is silently dropped (`[Vue warn]: Invalid prop name`). The generator detects such collisions and renames the prop with an `Attr` suffix; the underlying attribute is written imperatively.

Currently only **`x-i18n`** is affected. Use `key-attr` (kebab) / `keyAttr` (camel) instead of `key`:

```vue
<!-- Wrong — Vue treats `key` as a VNode identifier, not a prop -->
<XI18n :key="welcome.title" />

<!-- Right — wrapper aliases `key` → `keyAttr` -->
<XI18n :key-attr="welcome.title" />
```

## v-model

Form-aware components support Vue's `v-model` directive:

```vue
<script setup lang="ts">
import { ref } from "vue";
import { XCheckbox } from "@vanelsas/baredom-vue/x-checkbox";
import { XSlider } from "@vanelsas/baredom-vue/x-slider";
import { XSelect } from "@vanelsas/baredom-vue/x-select";

const checked = ref(false);
const volume = ref("50");
const choice = ref("");
</script>

<template>
  <XCheckbox v-model="checked" />
  <XSlider v-model="volume" min="0" max="100" />
  <XSelect v-model="choice">
    <option value="a">A</option>
    <option value="b">B</option>
  </XSelect>
</template>
```

| Component | `v-model` type | Listens on | Reads `detail.` |
|-----------|---------------|-----------|----------------|
| `XCheckbox`, `XSwitch`, `XRadio` | `boolean` | `x-{tag}-change` | `checked` |
| `XSlider`, `XTextArea`, `XSelect`, `XCombobox`, `XCurrencyField` | `string` | `x-{tag}-change` | `value` |
| `XTabs` | `string` | `value-change` | `value` |
| `XPagination` | `number` | `page-change` | `page` |

For finer control (e.g. validating before commit), listen on `@change-request` and skip `v-model`.

## Refs and methods

Each wrapper exposes its underlying custom element via `expose({ el })`. Access the element through the wrapper's instance ref:

```vue
<script setup lang="ts">
import { ref } from "vue";
import { XModal, type XModalExposed } from "@vanelsas/baredom-vue/x-modal";

// Variable name must match the `ref="..."` attribute below.
// On Vue 3.5+ you can use `useTemplateRef<XModalExposed>("modal")` instead.
const modal = ref<XModalExposed | null>(null);

function open() {
  modal.value?.el?.show();
}
</script>

<template>
  <button @click="open">Open</button>
  <XModal ref="modal" @toggle="(e) => console.log(e.detail)">
    Modal content
  </XModal>
</template>
```

## Theming

Wrap your app with `<XTheme>` to enable BareDOM's design tokens. Components automatically adapt to system dark/light mode.

```vue
<script setup lang="ts">
import { XTheme } from "@vanelsas/baredom-vue/x-theme";
</script>

<template>
  <XTheme preset="aurora">
    <!-- All BareDOM components inherit theme tokens -->
  </XTheme>
</template>
```

### Custom presets

Use the `useRegisterPreset` composable to register a custom theme preset. Any tokens you omit fall back to the default BareDOM preset.

```vue
<script setup lang="ts">
import { useRegisterPreset, type PresetData } from "@vanelsas/baredom-vue/composables";
import { XTheme } from "@vanelsas/baredom-vue/x-theme";

const brandTokens: PresetData = {
  light: {
    "--x-color-primary": "#e11d48",
    "--x-color-primary-hover": "#be123c",
    "--x-font-family": "'Inter', sans-serif",
  },
  dark: {
    "--x-color-primary": "#fb7185",
    "--x-color-primary-hover": "#f43f5e",
  },
};

useRegisterPreset("brand", brandTokens);
</script>

<template>
  <XTheme preset="brand">
    <!-- Components use your brand tokens -->
  </XTheme>
</template>
```

## Requirements

- Vue 3.4+
- @vanelsas/baredom 2.6.0+

## Auto-generated

The wrapper components are auto-generated from BareDOM's component model metadata using `bb scripts/generate_vue.bb`. Adding a new component to BareDOM automatically produces its Vue wrapper.

## Dev tools

To enable the `<x-trace-history>` timeline dock in your Vue app, add a side-effect import at the top of `main.ts`:

```ts
import "@vanelsas/baredom/x-trace-history";
```

The dock only mounts when you load the page with `?baredom-trace-history` in the URL (or set `window.BAREDOM_TRACE_HISTORY = true` before the module loads). See [docs/x-trace-history.md](../../docs/x-trace-history.md) for the full guide, and `test-app/` for a working smoke setup.

## License

MIT
