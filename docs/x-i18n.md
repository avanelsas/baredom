# x-i18n

Inline translation component. Renders translated text for a given key, resolved from the nearest ancestor `<x-i18n-provider>`.

## Tag

```html
<x-i18n key="greeting.hello" params='{"name":"World"}'></x-i18n>
```

## Attributes

| Attribute | Type | Default | Description |
|---|---|---|---|
| `key` | string | `""` | Dot-notation translation key |
| `params` | string | `""` | JSON string for interpolation |

## Properties

| Property | Type | Reflects | Read-only | Description |
|---|---|---|---|---|
| `key` | string | `key` | no | Translation key |
| `params` | string | `params` | no | JSON interpolation params |
| `value` | string | no | yes | Current resolved text |

## Events

None.

## Slots

None.

## CSS Custom Properties

| Property | Default | Description |
|---|---|---|
| `--x-i18n-font` | `inherit` | Font family for the translated text |
| `--x-i18n-color` | `inherit` | Text color |

## CSS Parts

| Part | Description |
|---|---|
| `text` | The span containing the translated text |

## Key Resolution

Keys use dot-notation to walk nested translation objects:

```json
{
  "greeting": {
    "hello": "Hello {name}!"
  }
}
```

```html
<x-i18n key="greeting.hello" params='{"name":"World"}'></x-i18n>
<!-- Renders: Hello World! -->
```

## Interpolation

Placeholders in the format `{key}` are replaced with values from the `params` JSON:

```html
<x-i18n key="message" params='{"count":"5","user":"Alice"}'></x-i18n>
```

Missing placeholder keys are left as-is in the output.

## Fallback Chain

1. Look up key in current locale translations
2. Look up key in fallback locale translations
3. Display the raw key string

## Without Provider

When no `<x-i18n-provider>` ancestor is found, the component displays the raw key string as fallback text.

## Accessibility

The component renders as inline text (`display: inline`). The translated text is directly accessible to screen readers as regular text content.
