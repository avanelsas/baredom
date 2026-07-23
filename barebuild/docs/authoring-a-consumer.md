# Authoring a resource consumer

A **consumer** is a thin custom element that renders a `<server-resource>`'s value into a
presentational BareDOM component. You write two small pieces. A pure projection and a
render function, and register with `consumer-resource/register!`. The shared mechanism
supplies everything else: the `applyResource` method, the change-guards, child caching, and
gesture submission.

The value your projection reads is the accepted server envelope — its exact shape is the
[server contract](./server-contract.md).

## The two files

```
x_<name>_consumer/          ; app code. This repo's demo keeps these under demo/src/demo/
  model.cljs          ; pure: tag metadata + projection (resource -> view data). Node-tested.
  x_<name>_consumer.cljs  ; DOM: render + optional hooks + init! -> register!
```

- **`model.cljs`** holds `tag-name`, `observed-attributes` (usually `#js []`), and a pure
  projection function (accepted response → whatever the child needs). No DOM. This is where
  unit tests live.
- **`x_<name>_consumer.cljs`** holds the DOM effects (render, failure UI, loading) and calls
  `consumer-resource/register!` from `init!`.

## `register!` config

```clojure
(consumer-resource/register!
 {:tag                 "x-<name>-consumer"   ; the consumer element tag
  :child-tag           "x-<child>"           ; the driven child, cached on connect
  :observed-attributes model/observed-attributes
  :render              render!               ; required
  :on-failure          on-failure!           ; optional
  :on-pending          on-pending!           ; optional
  :on-writing          on-writing!           ; optional
  :on-connect          on-connect!})         ; optional
```

**All hooks share one signature: `(child value this)`** — `child` is the cached child
element, `this` is the consumer host.

| Hook | Signature | Fires when… |
|---|---|---|
| `:render` | `(child accepted this)` | `:last-accepted` changes |
| `:on-failure` | `(child failure this)` | `:last-failure` changes — `failure` is **nil on recovery**, so clear your failure UI |
| `:on-pending` | `(child pending this)` | `pending?` changes — `pending` is a boolean; show/hide loading |
| `:on-writing` | `(child writing this)` | `writing?` changes — `writing` is a boolean; disable the submit control, and use the true→false edge to close a form on success |

What you get for free:
- **Keep-stale is automatic.** A failure leaves `:last-accepted` untouched, so `render`
  simply no-ops during failures. The last good view stays on screen.
- **One request in flight, stale-drop, echo-adoption, trailing-fetch, revert** all handled
  by the pure `step` upstream. The consumer only ever sees the resulting value.

## Gestures (interactive consumers)

In a DOM event handler, translate the gesture into an intent patch (a `model` function) and
submit it:

```clojure
(defn- on-sort [^js e]
  (let [consumer (.closest (.-currentTarget e) "x-<name>-consumer")
        patch    (model/translate-gesture …)]   ; {:query-patch {…} :gesture-class :refinement}
    (consumer-resource/submit-intent! consumer patch)))
```

`:gesture-class` is `:refinement` (-> replace history) or `:navigation` (-> push history);
`step` resolves it to a URL-write mode. Display-only consumers have no gestures.

## Writes (create / delete)

A write is the same shape of gesture, submitted with `submit-write!` instead:

```clojure
(defn- on-delete [^js e]
  (let [consumer (.closest (.-currentTarget e) "x-<name>-consumer")]
    (consumer-resource/submit-write! consumer {:op :delete :id 42})))
```

The payload is `{:op :delete :id <id>}` or `{:op :create :record {…}}`, where `record` is a
map keyed by the **shape's field keys** (opaque domain strings, not keywords). `step` turns
it into a `:write` effect. The ack comes back as `:write-ack`, which triggers a refetch. So
you never render a write's result yourself. It arrives through `render` like any other
accepted value. Writes never touch the URL.

Validate a create payload before submitting, against the shape the server sent:

```clojure
(let [errors (validation/validate-payload record shape)]
  (if (seq errors)
    (doseq [{:keys [field message]} errors] (.setFieldError form field message))
    (consumer-resource/submit-write! consumer {:op :create :record record})))
```

Each error is `{:field :code :message}` with `:code` one of `:missing-required`,
`:wrong-type`, `:not-in-enum`. This is a UX shortcut only. The server re-validates and is
the authority. A server-side rejection arrives through `:on-failure` as a `:rejected`
failure whose `error.details` names the offending field, so map that back onto the form the
same way.

## Wiring it in

1. Register the consumer in **your app's** init. Require its ns and call its `init!`,
   alongside the driven BareDOM component's `init!`. Then call `barebuild.core/init` to
   install `<server-resource>` (the BareBuild runtime). Consumers are app code. They are
   **never** added to `barebuild.core`, which registers only `<server-resource>`. In this
   repo's demo all of that lives in `demo.app` (`demo/src/demo/app.cljs`). In
   production the driven BareDOM component is a peer dependency the host page loads.
2. Demo markup. Put the consumer anywhere inside `<server-resource>` and nest the child
   inside it. `collect-consumers` walks all descendants, so wrapping consumers in layout
   elements is fine. A nested `<server-resource>` keeps its own:
   ```html
   <server-resource src="…">
     <x-«name»-consumer>
       <x-«child»></x-«child»>
     </x-«name»-consumer>
   </server-resource>
   ```
One `<server-resource>` fans out to any number of consumers.

## Worked examples

**Minimal — `x-stat-consumer`** (display-only scalar): `project-stat` (model) + a one-line
`render!` (set the `value` attr) + `on-pending!` (toggle the `loading` attr). ~20 lines.

**Full — `x-table-consumer`**: `accepted-response->view-model` + gesture translators (model);
`render!` (build `x-table-row`/`x-table-cell` children + pagination), `on-failure!` (an
`x-alert`), `on-pending!` (`aria-busy` + dim), sort/page gestures via `submit-intent!`, and
row delete via `submit-write!`.

**Writing — `x-task-form-consumer`**: populates an `x-form` from the shape, validates the
payload locally, submits with `submit-write!`, uses `on-writing!` to disable the submit
button and close the modal on success, and `on-failure!` to map a server rejection back onto
the offending field.

All three are driven by the same 70-line `consumer_resource.cljs` — the difference between
them is exactly their projection and their hooks.
