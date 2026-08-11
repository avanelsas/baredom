# Authoring a resource consumer

A **consumer** is a thin custom element that renders a `<server-resource>`'s value into a
presentational BareDOM component. You write two small pieces, a pure projection and a render
function, and register them with `consumer-resource/register!`. The shared mechanism supplies
everything else: the `applyResource` method, the change-guards, child caching, and gesture
submission.

The value your projection reads is the accepted server envelope, carried on the view described
below. Its exact shape is the [server contract](./server-contract.md).

## What BareBuild provides, what you write

BareBuild ships the runtime and the consumer mechanism, plus two pure validators. Accepted
values are contract-checked automatically upstream, which you never call yourself, and
`validation/conform-payload` reads a write payload as the shape declares it and reports what
does not fit, before you submit.

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

- **`model.cljs`** holds `tag-name` and a pure projection function (accepted response ->
  whatever the child needs). No DOM. This is where unit tests live. A consumer with nothing to
  project, one that only reacts to the `:pending?` / `:writing?` flags as `x-spinner-consumer`
  does, has no pure layer to test and keeps the element file alone.
- **`x_<name>_consumer.cljs`** holds the DOM effects (render, failure UI, loading) and calls
  `consumer-resource/register!` from `init!`.

## `register!` config

```clojure
(consumer-resource/register!
 {:tag        "x-<name>-consumer"   ; the consumer element tag
  :child-tag  "x-<child>"           ; the driven child, cached on connect
  :render     render!               ; optional, but the usual reason to write a consumer
  :on-failure on-failure!           ; optional
  :on-pending on-pending!           ; optional
  :on-writing on-writing!           ; optional
  :render-key render-key            ; optional
  :on-connect on-connect!})         ; optional
```

A consumer drives its child from the view alone and never from an attribute, so a consumer
element observes none and there is no `:observed-attributes` to declare.

**Every view hook shares one signature: `(child view this)`**, and every one is handed the *whole*
view. `child` is the cached child element, `this` is the consumer host. `on-connect` is handed the
same two, with no view yet.

A hook's slice decides only **when** it fires, never what it may read. So `on-writing` sees
`:accepted` too, and `render` sees `:writing?`. Nothing has to be cached on the element to be read
back by a later hook.

| Hook | Fires when… |
|---|---|
| `:render` | the `render-key` slice of the view changes |
| `:on-failure` | `:failure` changes.  It is **nil on recovery**, so clear your failure UI |
| `:on-pending` | `:pending?` changes.  Show or hide loading |
| `:on-writing` | `:writing?` changes.  Disable the submit control, and use the true→false edge to close a form on success |
| `:on-connect` | `(child this)`, the consumer element connects, before any view arrives.  Wire your listeners here |

Every hook also fires **once on the first apply**, whatever its slice holds at that moment, so a
hook is always given a starting value rather than only being told about later movement. Write
your hooks to be idempotent: the boot call hands a view whose `:failure` is nil and whose
`:writing?` is false.

### Reading the view from a gesture handler

A DOM listener fires from the DOM, not from an apply, so it is handed no view. Call
`(consumer-resource/view this)` for the one this consumer last saw, rather than stashing a slice of
it on the element during `render`.

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
 :writing?  <boolean>
 :write     <the write last submitted, or nil before any was>}
```

Everything else in the resource is the runtime's own bookkeeping and is deliberately not
handed to you.

### `:write`, and whose write it was

A resource can drive several writing consumers, and a write moves `:writing?` for **all** of
them. So `:writing?` alone cannot answer the question a form actually has, which is whether the
write that just finished was its own.

`:write` answers it. It is the write this resource last submitted and how that write ended:

```clojure
{:payload {:op :create :submitter "x-task-form-consumer" :record {...}}
 :status  :in-flight}   ;; then :accepted, :rejected or :failed
```

`submit-write!` stamps `:submitter` with the submitting consumer's tag. The stamp is client-side
only: a request body is built from the payload's `:record` alone, so it never reaches the server.
Pass your own `:submitter` in the payload if one tag is not enough to tell two instances apart.

Ask `(consumer-resource/own-write? this view)` rather than keeping a flag:

```clojure
(defn- on-writing! [^js form view ^js this]
  (let [status (when (consumer-resource/own-write? this view)
                 (get-in view [:write :status]))]
    (if (= :in-flight status)
      (du/set-attr! button "loading" "")
      (do (du/remove-attr! button "loading")
          (when (= :accepted status)
            (close-and-clear! form))))))
```

`:status` distinguishes the three ways a write ends. `:rejected` is the server saying no.
`:failed` is the outcome never being learned, which is **not** the same thing: the write may have
committed. The runtime reconciles that case with a fresh read, so the value you paint next is
still the server's, but a message you show the user should not claim the write did not happen.

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

It defaults to the accepted envelope, which is right for a component that draws server data and
nothing else. The envelope you get has no `:request/id` on it: that names the exchange that
fetched the value rather than the value, so two identical refetches compare equal and your
component does not repaint on a poll that changed nothing.

A consumer is projected once at connect, **before any response**, with `:accepted` nil. That is
what lets a component paint its empty state from the intent alone. If yours has nothing to draw
without data, say so with a plain `(when accepted …)`.

What you get for free:
- **Keep-stale is automatic.** A failure leaves `:accepted` untouched, so `render`
  simply no-ops during failures. The last good view stays on screen.
- **One request in flight, stale-drop, echo-adoption, trailing-fetch, revert** all handled
  by the pure `step` upstream. The consumer only ever sees the resulting value.

## Rendering failures

The `failure` your `on-failure` receives is a value tagged by `:cause`, one of
`#{:rejected :network :protocol :contract}`, and by `:for`, either `:read` or `:write`, saying
which kind of request it came from. Every failure also carries the `:query` it concerns.
Dispatch on the tag, and for a `:network` failure read `:error` to tell the kinds apart:

```clojure
(defn- message [failure]
  (case (:cause failure)
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

A gesture is only submittable once the host has booted, which it does after every custom
element inside it is defined. Your listeners are live before that, so a gesture fired in the
gap is reported to the console and dropped rather than thrown out of your handler.

`:gesture-class` is `:refinement` (-> replace history) or `:navigation` (-> push history), and
`step` resolves it to a URL-write mode. This mapping is fixed: a `:navigation` gesture pushes a
history entry, everything else replaces. It is not configurable per resource. Display-only
consumers have no gestures.

### Asking the same question again

A read follows an intent patch only when the intent **moved**. When the gesture is "ask again"
rather than "ask something else" — a reload button, a retry after an error — there is nothing to
patch, and inventing a field to perturb would put a non-fact in the URL:

```clojure
(defn- on-reload [^js e]
  (consumer-resource/submit-refresh! (.closest (.-currentTarget e) "x-<name>-consumer")))
```

`submit-refresh!` re-reads the current intent. The intent does not move, the URL is not written,
and what the resource holds stands until an answer replaces it. A read already in flight is the
answer to that request, so a second one is not opened on top of it.

## Writes

A write is the same shape of gesture, submitted with `submit-write!` instead:

```clojure
(defn- on-delete [^js e]
  (let [consumer (.closest (.-currentTarget e) "x-<name>-consumer")]
    (consumer-resource/submit-write! consumer {:op :delete :id 42})))
```

Four ops, each one payload. `record` is a map keyed by the **shape's field keys** (opaque
domain strings, not keywords), and `id` is the value of the row's `idKey`:

| Op | Payload | Request |
|---|---|---|
| `:create` | `{:op :create :record {…}}` | `POST <endpoint>` |
| `:update` | `{:op :update :id <id> :record {…}}` | `PUT <endpoint>/<id>`, a full replace of every field the shape declares |
| `:move` | `{:op :move :id <id> :record {"status" … "index" …}}` | `PATCH <endpoint>/<id>`, a positional command carrying only the destination |
| `:delete` | `{:op :delete :id <id>}` | `DELETE <endpoint>/<id>` |

`step` turns the payload into a `:write` effect, and the ack comes back as `:write-ack`. An
accepted ack already carries the server's new collection state (value + shape), which `step`
installs directly, so no separate refetch is issued. You never render a write's result
yourself. It arrives on the view's
`:accepted` and reaches you through `render` like any other accepted value. Writes never touch
the URL.

**A write that fails without the server saying no is re-read automatically.** If the connection
drops, the budget runs out, or the body comes back unreadable, the client cannot know whether the
write committed, so it fetches the collection again and your `render` is called with whatever the
server actually has. Your `on-failure` still fires first, so tell the user the write failed, but
do not assume the old view is still accurate while you do. Only a `:rejected` ack, the server
explicitly refusing, skips the re-read, because there its answer is already definitive.

**That re-read does not retire the write failure.** A read answers the read and says nothing
about whether a write committed, so the report survives the refetch that follows it and your
failure UI stays up until something actually answers it: a later write that succeeds, or the
user dismissing it. A read succeeding only retires a `:read` failure.

Conform a create payload before submitting, against the shape the server sent:

```clojure
(let [{:keys [record errors]} (validation/conform-payload form-values shape)]
  (if (seq errors)
    (doseq [{:keys [field message]} errors] (.setFieldError form field message))
    (consumer-resource/submit-write! consumer {:op :create :record record})))
```

`conform-payload` returns the record to send and the errors to show. It reads each value as
the type its field declares first, so a number typed into a form arrives as a number rather
than being reported as the wrong type.

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

**Minimal, `x-stat-consumer`** (display-only scalar): `project-stat` (model) + a `render-key`
naming that same projection + a one-line `render!` (set the `value` attr). ~15 lines.

**Flags only, `x-spinner-consumer`**: no model projection and no `render!`. `on-pending` and
`on-writing` share one handler that shows the spinner while the resource is reading or writing.
The one to copy for a consumer that draws no server data.

**Full, `x-table-consumer`**: `accepted-response->view-model` + gesture translators (model),
then `render!` (build `x-table-row`/`x-table-cell` children + pagination), `on-failure!` (an
`x-alert`), sort/page gestures via `submit-intent!`, and row delete via `submit-write!`.

**Writing, `x-task-form-consumer`**: populates an `x-form` from the shape, conforms the
payload locally, submits with `submit-write!`, uses `on-writing!` to disable the submit
button and close the modal on success, and `on-failure!` to map a server rejection back onto
the offending field.

**Intent-driven, `x-board-consumer`**: paints from the accepted rows *and* from whether a
project is selected, so its `render-key` names both and its empty gate reads `(:project intent)`
rather than the URL. It is the reference for a component whose view is not server data alone.

All of them are driven by the same single `consumer_resource.cljs`. The difference between
them is exactly their projection and their hooks.
