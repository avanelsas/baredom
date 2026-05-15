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

## Property accessor tiers

Components install JS property accessors at one of three tiers. **Pick the simplest tier the component qualifies for.** Mixing tiers within one `install-property-accessors!` is fine; document and justify any drop to a lower tier inline.

### Tier 0 — Data-driven (preferred)

One line:

```clojure
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api))
```

Requires `model/property-api` to follow the canonical schema:

```clojure
(def property-api
  {:size  {:type 'string  :reflects-attribute attr-size  :default "md"}
   :open  {:type 'boolean :reflects-attribute attr-open}
   :delay {:type 'number  :reflects-attribute attr-delay :default 400}})
```

Use Tier 0 when **every** property is a simple bool/string/number reflector with no custom logic. Methods (`el.show()`) and computed read-only properties don't fit `property-api` — if a component has those, drop to Tier 1.

Reference: `x_icon` (primary Golden Sample), `x_button`, `x_i18n`.

### Tier 1 — Mixed helpers + custom

Use when most properties are simple but at least one needs a method or a custom parser:

```clojure
(defn- install-property-accessors! [^js proto]
  (du/install-properties! proto model/property-api)   ;; data-reflective props
  (aset proto "show" (fn [] (this-as ^js this (do-show! this))))
  (aset proto "hide" (fn [] (this-as ^js this (do-hide! this)))))
```

Or keep it explicit when only a couple of helpers are needed:

```clojure
(defn- install-property-accessors! [^js proto]
  (du/define-bool-prop!   proto "open"     model/attr-open)
  (du/define-string-prop! proto "label"    model/attr-label)
  (du/define-number-prop! proto "duration" model/attr-duration model/default-duration)
  (custom-method! proto))
```

Reference: `x_dropdown`, `x_combobox`.

### Tier 2 — Hand-written `.defineProperty` (last resort)

Use only when helpers can't express the semantics:

- **Strict empty-string removal** — `el.src = ""` should remove the attribute (e.g., `x_image`).
- **Side-effecting setters** — `el.open = true` triggers an animation (e.g., `x_drawer`, `x_modal`).
- **CLJS-truthy semantics that differ from helpers** — e.g., `x_toaster`'s `position` keeps `""` because `(if v ...)` is truthy on empty string.
- **Computed/read-only properties** that read instance state, not attributes (e.g., `naturalWidth` on `x_image`).
- **Methods that need extra `:writable` or `:configurable` flags.**

Document the reason inline so future readers understand why the tier dropped. Reference: `x_image`, `x_currency_field`, `x_text_area`.

## Rules

- Lifecycle callbacks must be **named `defn-` functions** (`connected!`, `disconnected!`, `attribute-changed!`) — never inline anonymous functions for lifecycle.
- `setup-prototype-fn` should be a named `defn-` function (e.g. `install-property-accessors!`).
- Use the highest tier you can — only drop to Tier 2 when helpers can't express the semantics.
- Use `this-as` inside property getter/setter bodies — never reference `this` directly.
- Registration is idempotent (the factory guards with `when-not .get js/customElements`).

## Shadow-root initialisation

Pick one of two patterns depending on whether the component needs to cache shadow children for re-decoration.

### No cached children → `du/initialized?` flag (preferred when applicable)

Golden sample: `x_icon`. `init-dom!` builds the shadow tree, calls `(du/mark-initialized! el k-initialized?)`, and returns `nil`. `ensure-shadow!` calls `init-dom!` only when `(du/initialized? el k-initialized?)` is false. No refs map — the host element, shadow root, and any selectors are looked up freshly when needed.

```clojure
(def ^:private k-initialized? "__xFooInitialized")

(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        style (.createElement js/document "style")]
    ;; ... build tree ...
    (du/mark-initialized! el k-initialized?)))

(defn- ensure-shadow! [^js el]
  (when-not (du/initialized? el k-initialized?)
    (init-dom! el)))
```

Use this when `apply-model!` writes directly to the host element (CSS variables on `.-style`, attributes via `du/`) and shadow children aren't re-touched after creation.

### Cached children → refs map

When `apply-model!` reads back specific shadow children to mutate, store a refs map. Use **CLJS keywords** for refs map keys with `:keys` destructure:

```clojure
(def ^:private k-refs "__xFooRefs")

(defn- init-dom! [^js el]
  (let [root  (.attachShadow el #js {:mode "open"})
        ...
        refs  {:root root :container container :input-el input-el}]
    ;; ... assemble ...
    (du/setv! el k-refs refs)
    refs))

(defn- ensure-refs! [^js el]
  (or (du/getv el k-refs) (init-dom! el)))

(defn- apply-model! [^js el m]
  (let [{:keys [container input-el]} (ensure-refs! el)]
    ...))
```

References: `x-avatar`, `x-badge`, `x-text-area`.

**Refs-key convention** — Prefer CLJS keyword keys (`{:foo el}`) with `:keys [foo]` destructuring over string-keyed `#js {}` JS objects accessed via `gobj/get`. The CLJS form is destructure-friendly and Closure-Advanced-safe. Only drop to the `#js {} + gobj/get` form when the refs object crosses a register-prototype boundary (rare).

**When in doubt**: if your component would `(du/getv el k-refs)` only for an init flag (never reading the values back), use `du/initialized?` — that's the sentinel-refs anti-pattern. Audited and swept out in PRs #216–#218.

## Forbidden registration patterns

- Manual `element-class` functions with `js*` — use `component/register!` instead
- `js/Reflect.construct` with `atom`-based constructor wiring
- Manual `js/Object.create` / `js/Object.setPrototypeOf` prototype composition
- Inline lifecycle callbacks inside the options map (extract to named functions)
