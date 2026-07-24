# Shared Utilities Reference

Components **must** use these shared utility modules — never reimplement locally. Defining a local `dispatch!`, `define-bool-prop!`, `set-attr!`, or similar function that duplicates a shared utility is forbidden.

Before adding a new utility function, check whether it already exists here. Add new shared helpers when they would benefit multiple components.

## `baredom.utils.component` (alias `component`)

| Function | Signature | Description |
|----------|-----------|-------------|
| `make-element-class` | `[opts-map]` | Create a custom element class from a declarative options map |
| `register!` | `[tag-name class-opts]` | Register a custom element if not already defined |

## `baredom.utils.dom` (alias `du`)

### Instance-field access

| Function | Signature | Description |
|----------|-----------|-------------|
| `getv` | `[el k]` | Read an instance field (wraps `gobj/get`) |
| `setv!` | `[el k v]` | Write an instance field (wraps `gobj/set`) |
| `initialized?` | `[el key]` | Check if an element is marked as initialized |
| `mark-initialized!` | `[el key]` | Mark an element as initialized (one-time guard) |

### Attribute helpers

| Function | Signature | Description |
|----------|-----------|-------------|
| `has-attr?` | `[el attr-name]` | Check if element has an attribute |
| `get-attr` | `[el attr-name]` | Get an attribute value |
| `set-attr!` | `[el attr-name value]` | Set an attribute value |
| `remove-attr!` | `[el attr-name]` | Remove an attribute |
| `set-bool-attr!` | `[el attr-name value]` | Set or remove a boolean attribute based on truthiness |

### Event dispatch

| Function | Signature | Description |
|----------|-----------|-------------|
| `dispatch!` | `[el event-name detail]` | Dispatch a non-cancelable CustomEvent (bubbles + composed) |
| `dispatch-cancelable!` | `[el event-name detail]` | Dispatch a cancelable CustomEvent, returns `true` when NOT cancelled |

### Property accessors

| Function | Signature | Description |
|----------|-----------|-------------|
| `define-bool-prop!` | `[proto prop-name attr-name]` | Install a boolean property reflecting to/from an attribute |
| `define-string-prop!` | `[proto prop-name attr-name]` or `[proto prop-name attr-name default-val]` | Install a string property with optional default |
| `define-number-prop!` | `[proto prop-name attr-name default-val]` | Install a numeric property with default fallback |
| `install-properties!` | `[proto property-api]` | Install all property accessors from a declarative `property-api` map |

## `baredom.utils.forms` (alias `forms`)

Shared policy for form-associated components. Every function here is composed by
the component — none of it is reimplemented per control.

### Constraint validation

| Function | Signature | Description |
|----------|-----------|-------------|
| `validity` | `[{:keys [has-error? error required? empty? missing-message]}]` | **Pure.** The standard projection onto `{:flags :message}`: `error` → `customError`, `required` + empty → `valueMissing`, else valid |
| `set-validity!` | `[internals anchor inputs]` | Apply that projection via `setValidity`. No-op when `internals` is nil |
| `sync!` | `[internals anchor value inputs]` | `setFormValue` + `set-validity!` in one call |
| `install-validity-api!` | `[proto k-internals]` | Install the native validation surface on the prototype: read-only `validity`, `validationMessage`, `willValidate`, `form`, `labels` plus `checkValidity()` / `reportValidity()`, all delegating to the `ElementInternals` stashed under `k-internals` |

`install-validity-api!` exists because `ElementInternals` never mirrors those
members onto the host element — a form-associated custom element that skips the
call leaves consumers unable to validate it other than by attempting a submit.
Declare the members in `property-api` as `:readonly true` (so the adapters skip
them and the `.d.ts` marks them `readonly`) and the two methods in `method-api`.

### Inline error display

| Function | Signature | Description |
|----------|-----------|-------------|
| `error-describedby` | `[has-error? author]` | **Pure.** The `aria-describedby` token — the error id, appended after any author-supplied ids |
| `apply-error-display!` | `[el anchor error-el {:keys [error has-error?]} describedby]` | Render the `[part=error]` text + `error-hidden`, `data-invalid` on the host, `aria-invalid` / `aria-describedby` on the anchor |

## `baredom.utils.model` (alias `mu`)

### Parsers

| Function | Signature | Description |
|----------|-----------|-------------|
| `parse-bool-attr` | `[s]` | Parse boolean attribute: `nil` → `false`, `"false"` → `false`, anything else → `true` |
| `parse-bool-present` | `[s]` | `true` when the attribute is present (any value including `""`) |
| `non-empty-string?` | `[value]` | String predicate — `true` for non-blank strings |

### Security sanitizers

| Function | Signature | Description |
|----------|-----------|-------------|
| `sanitize-svg-path-d` | `[s]` | Strip invalid SVG path d-attribute characters, returns nil if blank |
| `safe-url?` | `[url]` | Check if URL uses an allowed protocol or is relative |
| `sanitize-url` | `[url]` | Return URL if safe, empty string otherwise |

## Usage patterns (following x-button golden sample)

**Instance fields:** Use `gobj/get` / `gobj/set` directly with private string keys (e.g. `"__xAlertModel"`, `"__xAlertRefs"`).

**Attribute reads in `read-model`:** Use `du/has-attr?` / `du/get-attr` — as x-button does. Do not use raw `.getAttribute` / `.hasAttribute`.

**Event dispatch:** Use `du/dispatch!` / `du/dispatch-cancelable!` — never create `CustomEvent` objects directly in component code.

**Property accessors:** Use `du/install-properties!` with `model/property-api` for standard properties. Only add manual `.defineProperty` for properties with custom getter/setter logic (e.g. `timeoutMs` with coercion).

**Boolean parsing in model:** Components may define their own parsing functions in `model.cljs` (like `parse-bool-default-true`) when the semantics differ from `mu/parse-bool-attr`. Use `mu/parse-bool-attr` when standard true/false parsing suffices.
