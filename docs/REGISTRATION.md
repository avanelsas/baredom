# Element Registration

Components are registered via `baredom.utils.component/register!`, which creates the native ES class, wires lifecycle callbacks, and installs property accessors from a declarative options map. **Do not create `element-class` functions manually** — the factory handles all boilerplate.

## Template (matches x-icon golden sample)

```clojure
(ns baredom.components.x-foo.x-foo
  (:require [baredom.utils.component :as component]
            [baredom.utils.dom :as du]
            [baredom.components.x-foo.model :as model]))

;; Named lifecycle functions — always separate defn-, never inline
(defn- connected! [^js el] ...)
(defn- disconnected! [^js el] ...)
(defn- attribute-changed! [^js el _name old-val new-val] ...)

;; Property accessors — use du/install-properties! driven by model/property-api
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))

(defn init! []
  (component/register! model/tag-name
    {:observed-attributes    model/observed-attributes
     :connected-fn           connected!
     :attribute-changed-fn   attribute-changed!
     ;; Optional:
     :disconnected-fn        disconnected!
     :form-associated?       true
     :setup-prototype-fn     install-property-accessors!}))
```

When `setup-prototype-fn` needs custom property logic (e.g. `timeoutMs` with coercion, `open` triggering show/hide), add manual `.defineProperty` calls after `du/install-properties!`:

```clojure
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api)
  ;; Custom properties with this-as:
  (js/Object.defineProperty proto "timeoutMs"
    #js {:get (fn [] (this-as ^js this ...))
         :set (fn [v] (this-as ^js this ...))
         :enumerable true :configurable true}))
```

## Rules

- Lifecycle callbacks must be **named `defn-` functions** (`connected!`, `disconnected!`, `attribute-changed!`) — never inline anonymous functions for lifecycle.
- `setup-prototype-fn` should be a named `defn-` function (e.g. `install-property-accessors!`).
- Use `du/install-properties!` to install property accessors driven by `model/property-api`. Only add manual `.defineProperty` calls for properties that need custom getter/setter logic.
- Use `this-as` inside property getter/setter bodies — never reference `this` directly.
- Registration is idempotent (the factory guards with `when-not .get js/customElements`).

## Forbidden registration patterns

- Manual `element-class` functions with `js*` — use `component/register!` instead
- `js/Reflect.construct` with `atom`-based constructor wiring
- Manual `js/Object.create` / `js/Object.setPrototypeOf` prototype composition
- Inline lifecycle callbacks inside the options map (extract to named functions)
