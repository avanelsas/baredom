# x-i18n-provider

Internationalization provider component. Fetches translation JSON files and provides locale context to descendant `<x-i18n>` elements.

## Tag

```html
<x-i18n-provider src="/locales/{locale}.json" locale="en" fallback-locale="en">
  <x-i18n key="greeting.hello" params='{"name":"World"}'></x-i18n>
</x-i18n-provider>
```

## Attributes

| Attribute | Type | Default | Description |
|---|---|---|---|
| `src` | string | `""` | URL pattern with `{locale}` placeholder |
| `locale` | string | `"en"` (normalized) | Current locale code |
| `fallback-locale` | string | `""` | Fallback locale for missing keys |

## Properties

| Property | Type | Reflects | Read-only | Description |
|---|---|---|---|---|
| `src` | string | `src` | no | URL pattern |
| `locale` | string | `locale` | no | Current locale |
| `fallbackLocale` | string | `fallback-locale` | no | Fallback locale |
| `translations` | object | no | yes | Current translation dictionary |

## Events

| Event | Bubbles | Composed | Cancelable | Detail |
|---|---|---|---|---|
| `x-i18n-loading` | yes | yes | no | `{ locale: string }` |
| `x-i18n-change` | yes | yes | no | `{ locale: string }` |
| `x-i18n-error` | yes | yes | no | `{ locale: string, message: string }` |

## Slots

| Slot | Description |
|---|---|
| (default) | All descendant content |

## URL Pattern

The `src` attribute supports a `{locale}` placeholder that gets replaced with the current locale value before fetching:

```html
<!-- Fetches /locales/en.json, /locales/nl.json, etc. -->
<x-i18n-provider src="/locales/{locale}.json" locale="en">
```

## Fallback Locale

When `fallback-locale` is set and differs from `locale`, the provider fetches both translation files in parallel. Keys missing from the current locale are resolved from the fallback.

## Attribute Scanning (data-i18n-*)

The provider automatically scans its subtree after translations load and sets attributes on any descendant element that has `data-i18n-{attr}` attributes. This allows translating any component's attributes without wrapping text in `<x-i18n>` elements.

```html
<x-i18n-provider src="/locales/{locale}.json" locale="en">
  <!-- Provider auto-sets the label attribute -->
  <x-button data-i18n-label="actions.save"></x-button>

  <!-- Multiple attributes on one element -->
  <x-form-field
    data-i18n-label="form.email"
    data-i18n-placeholder="form.email_hint"
    data-i18n-error="form.required">
  </x-form-field>

  <!-- With interpolation -->
  <x-button
    data-i18n-label="greeting.hello"
    data-i18n-params='{"name":"World"}'>
  </x-button>
</x-i18n-provider>
```

**How it works:**
- After translations load, the provider queries all descendants for attributes starting with `data-i18n-`
- For each `data-i18n-{attr}="key"`, it resolves the translation key and sets the real `{attr}` attribute
- `data-i18n-params` is treated specially as interpolation parameters (JSON), not as a target attribute
- Elements inside nested providers are skipped (they belong to the inner provider's scope)

**Supported patterns:**
- `data-i18n-label="key"` sets `label="translated value"`
- `data-i18n-text="key"` sets `text="translated value"`
- `data-i18n-placeholder="key"` sets `placeholder="translated value"`
- `data-i18n-aria-label="key"` sets `aria-label="translated value"`
- Any `data-i18n-{attr}` pattern works for any attribute name

## Nested Providers

Providers can be nested. Each `<x-i18n>` element resolves to its nearest ancestor provider via `.closest()`:

```html
<x-i18n-provider src="/locales/{locale}.json" locale="en">
  <x-i18n key="app.title"></x-i18n>

  <!-- Override for a section -->
  <x-i18n-provider src="/admin/{locale}.json" locale="en">
    <x-i18n key="admin.title"></x-i18n>
  </x-i18n-provider>
</x-i18n-provider>
```

## Accessibility

This component is invisible (display: contents) and has no direct accessibility requirements. The translated text rendered by `<x-i18n>` descendants is accessible as regular text content.
