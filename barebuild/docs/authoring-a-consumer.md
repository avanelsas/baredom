# Authoring a resource consumer

A **consumer** is a thin custom element that renders a `<server-resource>`'s value into a
presentational BareDOM component. You write two small pieces. A pure projection and a
render function, and register with `consumer-resource/register!`. The shared mechanism
supplies everything else: the `applyResource` method, the change-guards, child caching, and
gesture submission.

The value your projection reads is the accepted server envelope, carried on the view described
below. Its exact shape is the [server contract](./server-contract.md).

## What BareBuild provides, what you write

BareBuild ships the runtime and the consumer mechanism, plus two pure validators you can lean
on: accepted values are contract-checked automatically upstream (you never call it), and
`validation/validate-payload` checks a write payload against the shape before you submit.

It does not ship a generic projection or formatter layer. There is no built-in `project`,
`format-vm`, or formatter registry. Turning an accepted value into what the component wants,
including any date, number, or nil formatting, is plain code in your `model.cljs` projection
and `render!`. That is the one place per consumer that differs.

A consumer therefore works for any component you can drive by setting attributes or properties
from a value. A component with an imperative-only API, canvas rendering, or internal animation
state may need a pattern that does not exist yet.

## The two files

```
x_<name>_consumer/          ; app code. This repo's demo keeps these under demo/src/demo/
  model.cljs          ; pure: tag metadata + projection (resource -> view data). Node-tested.
  x_<name>_consumer.cljs  ; DOM: render + optional hooks + init! -> register!
```

- **`model.cljs`** holds `tag-name`, `observed-attributes` (usually `#js []`), and a pure
  projection function (accepted response -> whatever the child needs). No DOM. This is where
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
  :render-key          render-key            ; optional
  :on-connect          on-connect!})         ; optional
```

**All hooks share one signature: `(child value this)`** — `child` is the cached child
element, `this` is the consumer host.

| Hook | Signature | Fires when… |
|---|---|---|
| `:render` | `(child view this)` | the `render-key` slice of the view changes |
| `:on-failure` | `(child failure this)` | `:failure` changes.  `failure` is **nil on recovery**, so clear your failure UI |
| `:on-pending` | `(child pending this)` | `:pending?` changes.  `pending` is a boolean, show/hide loading |
| `:on-writing` | `(child writing this)` | `:writing?` changes.  `writing` is a boolean, disable the submit control, and use the true→false edge to close a form on success |

### Hook order is part of the contract

Within one projection the hooks run in the order of that table: `on-failure`, then `render`,
then `on-pending` and `on-writing`. The data is painted before the flags describing the
transition that produced it.

The practical consequence: **when `on-writing` fires false, the value that write returned is
already rendered.** A component that suppresses work while a write is in flight, a board
holding a reserved drop zone say, can resume on that edge and find the new value in place.
The reverse does not hold, so do not expect `render` to see a flag that a later hook is about
to set.

## The view

`render` receives a **view**, and the view is the whole of what a consumer may read:

```clojure
{:accepted  <the accepted envelope, or nil before the first response>
 :failure   <the current failure, or nil>
 :intent    <the current query as a map, unprefixed keys>
 :pending?  <boolean>
 :writing?  <boolean>}
```

Everything else in the resource is the runtime's own bookkeeping and is deliberately not
handed to you.

**Paint from the view and from nothing else.** Reading `js/location`, `document`, or any other
ambient state inside a consumer makes it render a mixture of two moments, because a
[BareReplay](../../barereplay/README.md) time-travel projection rewinds the view but cannot
rewind the address bar. A consumer that obeys this replays correctly by construction.

`:intent` is what makes that possible. A gate like "no project is selected yet" is
`(some? (:project intent))`, not a `URLSearchParams` lookup.

### The view is per resource

A view carries **its own resource's** state. Coordination between resources is deliberately
one-directional: `submit-intent!` with a target id lets a consumer **drive** a named sibling,
and there is no counterpart for **reading** one.

So a consumer that needs to *display* state owned by a sibling has no supported route to it,
and reading the URL is the only way out. That consumer is then outside the replay guarantee:
a projection rewinds its view, but not the address bar it is really reading.

`x-project-selector-consumer` in the demo is exactly this case. It lives in the `projects`
resource, drives `tasks`, and has to show the `tasks.project` selection it just set.

**Prefer to model your way around it.** A resource should own the state it displays. If a
component needs a sibling's state to paint, that usually means the state is modelled on the
wrong resource, or the two components should be one per resource rather than one spanning both.
Reach for the URL only when neither is possible, and expect that component not to time-travel.

Adding cross-resource reads later would be additive, so nothing written today forecloses it.

### `render-key`

`render` fires only when the slice you name changes, so `render-key` is where you state what
your component actually paints from:

```clojure
:render-key (fn [{:keys [accepted intent]}]
              [(:value accepted) (some? (:project intent))])
```

It defaults to the accepted envelope minus the per-request ids, which is right for a component
that draws server data and nothing else.

A consumer is projected once at connect, **before any response**, with `:accepted` nil. That is
what lets a component paint its empty state from the intent alone. If yours has nothing to draw
without data, say so with a plain `(when accepted …)`.

What you get for free:
- **Keep-stale is automatic.** A failure leaves `:accepted` untouched, so `render`
  simply no-ops during failures. The last good view stays on screen.
- **One request in flight, stale-drop, echo-adoption, trailing-fetch, revert** all handled
  by the pure `step` upstream. The consumer only ever sees the resulting value.

## Rendering failures

The `failure` your `on-failure` receives is a value tagged by `:failure`, one of
`#{:rejected :network :protocol :contract}`. Dispatch on the tag, and for a `:network` failure
read `:error` to tell the kinds apart:

```clojure
(defn- message [failure]
  (case (:failure failure)
    :rejected (get-in failure [:response :error :message])   ; the server's rejection message
    :network  (case (get-in failure [:error :kind])
                :http-status (case (get-in failure [:error :status])
                               (401 403) "Your session has expired."   ; re-auth here
                               404       "Not found."
                               "Server error, please try again.")
                :timeout     "The server took too long, please try again."
                :decorator   "Couldn't sign you in, please try again."
                "Couldn't reach the server.")                ; :offline
    :protocol "The server sent an unexpected response."
    :contract "The server's data did not match the expected format."))
```

`:network` carries `:error {:kind ...}`: `:offline` for a transport failure (the request never
reached a server), `:http-status` with a `:status` code for a non-ok response, `:timeout` with
the `:after` budget in milliseconds when the request outlived its
[timeout](./request-configuration.md#timeout), and `:decorator`
when a registered [request decorator](./request-configuration.md#a-request-decorator) could not
produce its headers, in which case the request was never sent at all. This is how a
consumer tells "you are offline" from "your session expired" (401). BareBuild surfaces the failure,
what a 401 *means* (redirect to login, refresh a token and re-submit intent) is your app's call.
A non-ok response is always a `:network`/`:http-status` failure, a query the server *rejects* comes
back as a normal 2xx envelope with `:outcome :rejected`, not an HTTP error status. For attaching
the credential in the first place, see
[request configuration](./request-configuration.md).

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
`step` resolves it to a URL-write mode. This mapping is fixed: a `:navigation` gesture pushes a
history entry, everything else replaces. It is not configurable per resource. Display-only
consumers have no gestures.

## Writes (create / delete)

A write is the same shape of gesture, submitted with `submit-write!` instead:

```clojure
(defn- on-delete [^js e]
  (let [consumer (.closest (.-currentTarget e) "x-<name>-consumer")]
    (consumer-resource/submit-write! consumer {:op :delete :id 42})))
```

The payload is `{:op :delete :id <id>}` or `{:op :create :record {…}}`, where `record` is a
map keyed by the **shape's field keys** (opaque domain strings, not keywords). `step` turns
it into a `:write` effect. The ack comes back as `:write-ack`. An accepted ack already carries
the server's new collection state (value + shape), which step installs directly, so no separate
refetch is issued. You never render a write's result yourself. It arrives on the view's
`:accepted` and reaches you through `render` like any other accepted value. Writes never touch
the URL.

**A write that fails without the server saying no is re-read automatically.** If the connection
drops, the budget runs out, or the body comes back unreadable, the client cannot know whether the
write committed, so it fetches the collection again and your `render` is called with whatever the
server actually has. Your `on-failure` still fires first, so tell the user the write failed, but
do not assume the old view is still accurate while you do. Only a `:rejected` ack, the server
explicitly refusing, skips the re-read, because there its answer is already definitive.

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

**Intent-driven, `x-board-consumer`**: paints from the accepted rows *and* from whether a
project is selected, so its `render-key` names both and its empty gate reads `(:project intent)`
rather than the URL. It is the reference for a component whose view is not server data alone.

All three are driven by the same 70-line `consumer_resource.cljs`. The difference between
them is exactly their projection and their hooks.
