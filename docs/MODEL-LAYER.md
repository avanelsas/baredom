# Model Layer Specification

Every component's `model.cljs` is a pure-functions-only namespace. It defines the component's public API contract and derived view-model logic. No DOM access or side effects.

## Required metadata definitions

### `tag-name`
String constant for the custom element tag (e.g. `"x-button"`).

### `observed-attributes`
A `#js [...]` array of attribute name constants that trigger `attributeChangedCallback`.

### `property-api`
Map of keyword property name to descriptor:

```clojure
(def property-api
  {:type        {:type 'string}
   :text        {:type 'string}
   :icon        {:type 'string}
   :dismissible {:type 'boolean}
   :disabled    {:type 'boolean}
   :timeoutMs   {:type 'number}})
```

Keys:
- `:type` — `'string`, `'boolean`, or `'number`
- `:reflects-attribute` — attribute name constant (optional, when property should sync to/from an attribute)
- `:readonly` — `true` for read-only properties (optional)

Note: The golden sample (x-alert) uses a minimal format with only `:type`. Add `:reflects-attribute` and `:readonly` only when needed.

### `event-schema`
Map of event constant symbol to descriptor:

```clojure
(def event-schema
  {evt-change {:cancelable false :detail {:value 'string}}
   evt-close  {:cancelable true  :detail {}}})
```

Use `{}` for empty detail. This metadata drives TypeScript `.d.ts` generation.

### `method-api`
Map of method name to descriptor. **Always include this def**, even as `(def method-api nil)` when the component has no public methods.

```clojure
(def method-api
  {"focus" {:args [] :returns 'void}
   "stepUp" {:args [{:name "n" :type 'number}] :returns 'void}})
```

Args entries: `{:name "param" :type 'number}`. This metadata drives automatic TypeScript `.d.ts` generation — omitting it breaks type output.

## Optional definitions

- **Slot names** — string constants for named slots
- **CSS custom property names** — `--x-<component>-<property>` constants
- **Enum values** — sets or vectors of allowed values for constrained attributes
- **`normalize` function** — pure function that parses raw attribute values into a typed model map

## Golden sample

See `src/baredom/components/x_icon/model.cljs` for a complete reference implementation.
