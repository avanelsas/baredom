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

Note: the example above is x-alert's `property-api` — the minimal form with only `:type`. The golden sample x-icon uses the richer form with `:reflects-attribute` and `:default` (see its `model.cljs`). Both are acceptable; add `:reflects-attribute`, `:default`, or `:readonly` only when needed.

### `event-schema`
Map of event constant symbol to descriptor:

```clojure
(def event-schema
  {evt-change {:cancelable false :detail {:value 'string}}
   evt-close  {:cancelable true  :detail {}}})
```

Use `{}` for empty detail. This metadata drives TypeScript `.d.ts` generation.

Every event states `:cancelable`. An event is one of three kinds:

- **A notification** is not cancelable. It reports what happened.
- **An attribute request** is cancelable and has `:requests`. It asks to change attributes of the component.
- **An action** is cancelable and has no `:requests`. It asks for something that is not an attribute of the component, such as a copy or a removal.

`:requests` maps each attribute the event asks to change to its new value. A keyword is a key of the detail. `true` or `false` is a value that the event fixes.

```clojure
(def event-schema
  {evt-change-request {:cancelable true
                       :requests   {attr-checked :nextChecked}
                       :detail     {:previousChecked 'boolean :nextChecked 'boolean}}
   evt-close-request  {:cancelable true
                       :requests   {attr-open false}
                       :detail     {:reason 'string}}})
```

`bb scripts/generate_types.bb` writes `:cancelable` and `:requests` into `custom-elements.json`. It also writes the requests of every component as one map, to `dist/requests.js` and to `src/baredom/requests.cljs`. `bb scripts/check_event_api.bb` checks each entry against the component and against the generated files.

### `method-api`
Map of method name to descriptor. **Always include this def**, even as `(def method-api nil)` when the component has no public methods.

```clojure
(def method-api
  {"focus" {:args [] :returns 'void}
   "stepUp" {:args [{:name "n" :type 'number}] :returns 'void}})
```

Args entries: `{:name "param" :type 'number}`. This metadata drives automatic TypeScript `.d.ts` generation — omitting it breaks type output.

## Optional definitions

- **`internal-attributes`**: a set of observed attributes that are not public API, such as the ones a parent component writes onto its children. The manifest leaves them out.
- **Slot names** — string constants for named slots
- **CSS custom property names** — `--x-<component>-<property>` constants
- **Enum values** — sets or vectors of allowed values for constrained attributes
- **`normalize` function** — pure function that parses raw attribute values into a typed model map

## Golden sample

See `src/baredom/components/x_icon/model.cljs` for a complete reference implementation.
