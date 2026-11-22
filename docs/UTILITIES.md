# Shared Utilities Reference

Components **must** use these shared utility modules — never reimplement locally. Defining a local `dispatch!`, `define-bool-prop!`, `set-attr!`, or similar function that duplicates a shared utility is forbidden.

Before adding a new utility function, check whether it already exists here. Add new shared helpers when they would benefit multiple components.

## `baredom.utils.component` (alias `component`)

| Function | Signature | Description |
|----------|-----------|-------------|
| `make-element-class` | `[opts-map]` | Create a custom element class from a declarative options map |
| `register!` | `[tag-name class-opts]` | Register a custom element if not already defined |
| `hold-key` | value | The symbol `Symbol.for("x-render-hold")`. Every element class offers the hold of its render under it. |

**The hold of a render.** `el[hold-key](f)` calls `f` with no arguments. While `f` runs, the
element does not receive its attribute changes. When `f` returns or throws, the element receives
each change in order, so each one sees the final attributes. An error from the element is reported
and not thrown. A call on an element that is already held only calls `f`. The hold ends when `f`
returns, so work that `f` starts and does not finish is not held.

## `baredom.utils.dom` (alias `du`)

### Styles

| Name | Kind | Description |
|------|------|-------------|
| `hidden-rule` | value | The CSS rule `:host([hidden]){display:none;}`. A component that sets a display on `:host` puts it in its style, after every rule that does. |

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
| `set-attr-to!` | `[el attr-name value]` | Set an attribute to a value, or remove it for nil, only where it differs |
| `set-attrs-to!` | `[el attrs]` | `set-attr-to!` for each entry of the map `attrs`, from attribute name to value or nil |
| `set-text-to!` | `[node text]` | Set the text of a node, only where it differs |

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
| `define-json-prop!` | `[proto prop-name attr-name value-of]` | Install a property that keeps a JS value as JSON. `value-of` turns a written value or the attribute text into the value, or nil to remove the attribute |
| `install-properties!` | `[proto property-api]` | Install all property accessors from a declarative `property-api` map |

## `baredom.utils.forms` (alias `forms`)

Shared policy for form-associated components. Every function here is composed by
the component — none of it is reimplemented per control.

### Disabled state

| Function | Signature | Description |
|----------|-----------|-------------|
| `disabled?` | `[el]` | True when the control is disabled by its own `disabled` attribute or by a `fieldset` |

A form control reads its disabled state with `disabled?`, in `read-model` and in its handlers. It
never writes `disabled` on its host. Its style selects `:host(:disabled)`.

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

### Slotted labels

| Function | Signature | Description |
|----------|-----------|-------------|
| `make-label-section!` | `[part]` | Create the label span of part `part`, with the id `mu/label-id` and a default slot in it. Returns `{:label :slot}` |
| `label-rules` | `[part opacity-var]` | The CSS of the label part: a pointer cursor, hidden without `data-labelled` on the host, dimmed by `opacity-var` with `data-disabled` |
| `watch-label-slot!` | `[slot-el on-change]` | Call `on-change` when the slotted content changes. Bind once per slot |
| `slot-has-content?` | `[slot-el]` | True when the light DOM assigns an element, or text that is not only white space, to the slot |
| `label-activation?` | `[el control evt]` | True when a click on the host acts on the control, as a click on a native label does. A click from a link, a button or a form control in the label does not |
| `focus-quietly!` | `[control]` | Focus `control` without a focus ring |

`x-checkbox`, `x-switch` and `x-radio` compose these. A host click that passes `label-activation?` calls `focus-quietly!` and then toggles or selects. The shared tests are in `test/baredom/test_label.cljs`.

## `baredom.utils.model` (alias `mu`)

### Parsers

| Function | Signature | Description |
|----------|-----------|-------------|
| `parse-bool-present` | `[s]` | An attribute that is off by default: `true` when present, whatever its text, as in HTML |
| `parse-bool-default-true` | `[s]` | An attribute that is on by default: `true` when absent, `false` only for `"false"` |
| `parse-bool-attr` | `[s]` | An attribute that says true or false with no default of its own: `false` when absent or `"false"` |
| `non-empty-string?` | `[value]` | String predicate — `true` for non-blank strings |

### Slotted labels

| Name | Signature | Description |
|------|-----------|-------------|
| `label-id` | | The id of the label span in the shadow root of a control with a slotted label |
| `control-labelledby` | `[{:keys [aria-labelledby aria-label labelled?]}]` | **Pure.** The `aria-labelledby` of the control: a host `aria-labelledby`, else nil for a host `aria-label`, else `label-id` when labelled |
| `control-aria` | `[model]` | **Pure.** The ARIA attributes of a checkable control as a map from name to text or nil: `aria-checked`, `aria-disabled`, `aria-required`, `aria-readonly`, `aria-label`, `aria-labelledby` and `aria-describedby`. The models of `x-checkbox`, `x-switch` and `x-radio` keep it as `:control-aria`, and each component applies it with `du/set-attrs-to!` |

### Keys

| Function | Signature | Description |
|----------|-----------|-------------|
| `unique-keys` | `[identities]` | A key for each identity: the identity as text and how many equal ones come before it |
| `with-unique-keys` | `[identity-of items]` | The maps `items`, each with a `:key` made of its identity |

### Security sanitizers

| Function | Signature | Description |
|----------|-----------|-------------|
| `sanitize-svg-path-d` | `[s]` | Strip invalid SVG path d-attribute characters, returns nil if blank |
| `safe-url?` | `[url]` | Check if URL uses an allowed protocol or is relative |
| `sanitize-url` | `[url]` | Return URL if safe, empty string otherwise |

## `baredom.utils.position` (alias `position`)

**Pure.** Places a panel next to an anchor, inside the viewport. Used by `x-popover` and `x-context-menu`.

| Name | Signature | Description |
|------|-----------|-------------|
| `placements` | | Each placement: the edge rule on each axis, the placement it flips to and the axis the flip moves |
| `max-height-floor` | | The smallest max-height a panel gets, in pixels |
| `compute` | `[{:keys [placement offset anchor panel viewport margin]}]` | `{:x :y :final-placement :max-height}`. Flips when only the opposite placement fits |

## Usage patterns (following x-button golden sample)

**Instance fields:** Use `gobj/get` / `gobj/set` directly with private string keys (e.g. `"__xAlertModel"`, `"__xAlertRefs"`).

**Attribute reads in `read-model`:** Use `du/has-attr?` / `du/get-attr` — as x-button does. Do not use raw `.getAttribute` / `.hasAttribute`.

**Event dispatch:** Use `du/dispatch!` / `du/dispatch-cancelable!` — never create `CustomEvent` objects directly in component code.

**Property accessors:** Use `du/install-properties!` with `model/property-api` for standard properties. Only add manual `.defineProperty` for properties with custom getter/setter logic (e.g. `timeoutMs` with coercion).

**Boolean parsing in model:** Use `mu/parse-bool-present` (or `du/has-attr?`) for an attribute that is off by default, and `mu/parse-bool-default-true` for one that is on by default. Do not write a local parser for either.
