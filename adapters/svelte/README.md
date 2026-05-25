# @vanelsas/baredom-svelte

Svelte 5 wrapper components for [BareDOM](https://github.com/avanelsas/baredom) — auto-generated from component metadata.

Provides typed props, typed event callbacks, `$bindable()` two-way binding, and `bind:el` ref forwarding for all 100+ BareDOM web components.

## Installation

```bash
npm install @vanelsas/baredom-svelte @vanelsas/baredom svelte
```

## Usage

```svelte
<script lang="ts">
  import { XButton } from "@vanelsas/baredom-svelte/x-button";
  import { XAlert } from "@vanelsas/baredom-svelte/x-alert";
  import { XTheme } from "@vanelsas/baredom-svelte/x-theme";
</script>

<XTheme>
  <XButton onpress={(e) => console.log("Pressed!", e.detail.source)}>
    Click me
  </XButton>

  <XAlert
    type="success"
    text="Operation completed"
    dismissible
    ondismiss={() => console.log("Dismissed")}
  />
</XTheme>
```

## Features

- **Typed props** — every component property is typed via a generated `XFooProps` interface.
- **Typed event callbacks** — handlers as `on<event>` props with `CustomEvent<DetailType>` narrowing.
- **`$bindable` two-way binding** — form-aware components support `bind:checked`/`bind:value`/`bind:page`.
- **`bind:el` ref forwarding** — direct access to the underlying custom element instance for imperative method calls.
- **Tree-shakable** — per-component subpath imports keep your bundle minimal.

## Svelte 5 requirement

This adapter requires **Svelte 5.0+**. It uses runes (`$state`, `$props`, `$bindable`, `$effect`) which are not available in Svelte 4. Svelte 4 is not supported and is not planned.

## Event handlers

Underlying BareDOM events are exposed as **lowercase, no-hyphen `on<event>` props**, matching Svelte 5's native DOM event convention (`onclick`, `onpointerdown`). The tag-name prefix is stripped, and any remaining hyphens are removed:

| DOM event | Svelte prop |
|-----------|-------------|
| `press` | `onpress` |
| `x-alert-dismiss` | `ondismiss` |
| `x-switch-change` | `onchange` |
| `press-start` | `onpressstart` |
| `value-change` | `onvaluechange` |
| `x-checkbox-change-request` | `onchangerequest` |

```svelte
<XButton onpress={(e) => console.log(e.detail.source)}>Click</XButton>
```

The handler type is `(e: CustomEvent<DetailType>) => void` — `e.detail` is fully narrowed.

## Two-way binding with `$bindable`

Form-aware components expose their value as a `$bindable` prop. Use `bind:` to two-way bind:

```svelte
<script lang="ts">
  import { XCheckbox } from "@vanelsas/baredom-svelte/x-checkbox";
  import { XSlider } from "@vanelsas/baredom-svelte/x-slider";
  import { XSelect } from "@vanelsas/baredom-svelte/x-select";
  import { XPagination } from "@vanelsas/baredom-svelte/x-pagination";

  let agreed = $state(false);
  let volume = $state("50");
  let choice = $state("a");
  let page = $state(1);
</script>

<XCheckbox bind:checked={agreed} />
<XSlider bind:value={volume} min="0" max="100" />
<XSelect bind:value={choice}>
  <option value="a">A</option>
  <option value="b">B</option>
</XSelect>
<XPagination bind:page totalPages={10} />
```

| Component | Bindable | Type | Listens on | Reads `detail.` |
|-----------|---------|------|-----------|----------------|
| `XCheckbox`, `XSwitch`, `XRadio` | `checked` | `boolean` | `x-{tag}-change` | `checked` |
| `XSlider`, `XTextArea`, `XCombobox`, `XCurrencyField` | `value` | `string` | `x-{tag}-change` | `value` |
| `XSelect` | `value` | `string` | `select-change` | `value` |
| `XTabs` | `value` | `string` | `value-change` | `value` |
| `XPagination` | `page` | `number` | `page-change` | `page` |

For finer control (e.g. validating before commit), pass an `onchangerequest` callback and call `e.preventDefault()`:

```svelte
<XPagination
  bind:page
  totalPages={10}
  onpagechangerequest={(e) => {
    if (e.detail.page % 2 === 1) e.preventDefault();
  }}
/>
```

## Element refs via `bind:el`

Each wrapper exposes its underlying custom element instance via a `$bindable` `el` prop. Use `bind:el` to capture the element for imperative method calls:

```svelte
<script lang="ts">
  import { XModal, type XModalElement } from "@vanelsas/baredom-svelte/x-modal";

  let modalRef = $state<XModalElement | null>(null);
</script>

<button onclick={() => modalRef?.show?.()}>Open</button>

<XModal bind:el={modalRef}>
  Modal content
</XModal>
```

## Reserved prop names

Two cases of JS reserved words collide with property names in BareDOM components, and the wrapper handles them automatically:

- **`class`** — passed through normally. The variable is internally aliased to `className` (since `class` is a JS reserved word in destructure positions), but the wrapper still applies it as the `class` attribute. Consumers write `<XIcon class="hero" />` as usual.
- **`static`** — used by `XSpotlightCard`. Internally aliased to `staticAttr`, written imperatively as the `static` attribute. Consumers write `<XSpotlightCard static />` as usual.

If you add a new BareDOM component with a property name that collides with another JS reserved word, the generator's `js-reserved-prop-names` set in `scripts/generate_svelte.bb` handles the alias.

The `slot` attribute is **not** destructured by the wrapper — it flows through `...rest` to the inner custom element. Svelte 5's compile-time slot-placement check rejects `<x-foo slot="...">` at a wrapper's root (no statically-known parent), so explicit pass-through is required. Consumers' `<XFoo slot="end">` works as expected because the check fires in the consumer's template context where the parent IS known.

## Named slots

BareDOM components use HTML shadow-DOM slots (e.g. `XButton` exposes `icon-start`, `icon-end`, `spinner`; `XNavbar` exposes `brand`, `start`, `end`, `actions`, `toggle`). Project children into a named slot by adding `slot="<name>"` to a child element:

```svelte
<XButton>
  <span slot="icon-start" aria-hidden="true">✓</span>
  Confirm
</XButton>
```

**Type-checking caveat.** Svelte 5's TypeScript checker rejects `<Component slot="..." />` (legacy Svelte-4 named-slot syntax) and `<svg slot="...">` (SVG attribute typing omits the global `slot`) when used as children of a Svelte component. The runtime forwarding works in both cases, but `svelte-check` reports errors. Two workarounds:

1. **Use plain HTML elements** (`<span>`, `<div>`, `<a>`, `<strong>`) as the slot child — these accept `slot=""` per Svelte's HTMLAttributes typing and pass the check.
2. **Use the underlying custom element directly** for cases where you need an SVG or another wrapped component as the slotted child:
   ```svelte
   <x-button>
     <svg slot="icon-start" ...>...</svg>
     Click me
   </x-button>
   ```
   Side-effect-import the wrapper first (`import "@vanelsas/baredom-svelte/x-button";`) so the custom element registers, then use the raw tag.

A future version may emit per-component `$$Slots` declarations so `<Wrapper>` can accept typed `slot="..."` children directly. For now, the workarounds above keep `svelte-check` clean.

## TypeScript

Per-component imports give you both the component, its props interface, and the underlying element type:

```ts
import { XButton, type XButtonProps, type XButtonElement } from "@vanelsas/baredom-svelte/x-button";
```

`svelte-check` is configured against `tsconfig.json` and runs as part of `npm run build`.

## Theming

Wrap your app with `<XTheme>` to enable BareDOM's design tokens. Components automatically adapt to system dark/light mode.

```svelte
<script lang="ts">
  import { XTheme } from "@vanelsas/baredom-svelte/x-theme";
</script>

<XTheme preset="aurora">
  <!-- All BareDOM components inherit theme tokens -->
</XTheme>
```

### Custom presets

Use the `useRegisterPreset` composable to register a custom theme preset. Any tokens you omit fall back to the default BareDOM preset.

```svelte
<script lang="ts">
  import { useRegisterPreset, type PresetData } from "@vanelsas/baredom-svelte/composables";
  import { XTheme } from "@vanelsas/baredom-svelte/x-theme";

  const brandTokens: PresetData = {
    light: {
      "--x-color-primary": "#e11d48",
      "--x-color-primary-hover": "#be123c",
    },
    dark: {
      "--x-color-primary": "#fb7185",
    },
  };

  useRegisterPreset("brand", brandTokens);
</script>

<XTheme preset="brand">
  <!-- Components use your brand tokens -->
</XTheme>
```

## Requirements

- Svelte 5.0+
- @vanelsas/baredom 2.6.0+

## Auto-generated

The wrapper components are auto-generated from BareDOM's component model metadata using `bb scripts/generate_svelte.bb`. Adding a new BareDOM component automatically produces its Svelte wrapper.

The form-control bridging metadata (`bind:checked`, `bind:value`, `bind:page`) is shared with the Vue `v-model` and Angular `ControlValueAccessor` generators via `scripts/form-control-metadata.bb`.

## Dev tools

To enable the `<x-trace-history>` timeline dock in your Svelte app, add a side-effect import in your entry file:

```ts
import "@vanelsas/baredom/x-trace-history";
```

The dock only mounts when you load the page with `?baredom-trace-history` in the URL (or set `window.BAREDOM_TRACE_HISTORY = true` before the module loads). See [docs/x-trace-history.md](../../docs/x-trace-history.md) for the full guide.

## License

MIT
