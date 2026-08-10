# Changelog

## Unreleased

- Breaking. A failure names its cause under `:cause` rather than under `:failure`. A consumer branching on the tag reads `(:cause failure)`.
- Breaking. Every view hook is handed the whole view rather than its own slice. `:on-failure`, `:on-pending` and `:on-writing` take `(child view this)` as `:render` already did, and `:on-connect` takes `(child this)`. A hook's slice decides only when it fires, never what it may read.
- Breaking. Every hook fires once on the first apply, whatever its slice holds, and only on movement after that. `:render` was the only one that used to get that first call, so write your hooks to be idempotent.
- Breaking. `:observed-attributes` is gone from the `register!` config. A consumer drives its child from the view alone, so it observes none.
- Breaking. The two host-app seams are `decorator/install!` and `recorder/install!`, replacing `set-request-decorator!` and `set-recorder!`. A value that is not callable is refused when it is registered rather than failing at every call site later.
- Breaking. `barebuild.validation` exposes `validate-contract`, `validate-payload` and `conform-payload`. `validate-value-type` and `err` are no longer public.
- Adds `validation/conform-payload`, the write-side entry point. It reads a record as its shape declares it before validating, so a number typed into a form is not reported as the wrong type.
- Adds `:write` to the view, the write the resource last submitted and how it ended, with `consumer-resource/own-write?` to ask whether it was this consumer's. A write moves `:writing?` for every consumer the resource drives, so a form acting on that movement asks this first.
- Adds `consumer-resource/view`, the view a consumer was last applied, for a gesture handler that fires from the DOM and is handed none.
- Naming `:request-decorator` in `init` sets the hook to whatever the key holds, nil included, and leaving the key out leaves the installed one alone, so a second `init` does not silently drop it.
- Fixes a read that outlived the disconnect that ordered its abort. A consumer that removes its host while being notified fires `disconnectedCallback` in the middle of the connect's own effects, and the abort that step emits now reaches the request rather than running before it was issued.
- A member write arriving without the member to address is reported as `:member-write-without-id` rather than as an op the vocabulary lacks.
- An envelope member of the wrong kind is reported as a protocol failure rather than as a server that could not be reached. A `shape` or `query` that is not an object, and a `fields` or `options` that is not a list, no longer end the request as a transport failure.
- `shape.fields` is mandatory. A shape carrying no field list is a contract failure rather than a response whose records go unchecked.
- A field that declares no `type` is checked for presence only, as an absent `required` or `enum` constrains nothing.
- A defect while delivering a response is reported as itself rather than as a network failure.
- `normalize-headers` moves from the server-resource model to `barebuild.utils.request`, and the request pipeline moves to a new `barebuild.transport`.
- Demo. The table page's tasks resource is named, so the project selector's targeted intent reaches it.

## 0.6.0

- Breaking. `barebuild.utils` is split into `barebuild.utils.url`, `barebuild.utils.query` and `barebuild.utils.request`.
- Breaking. The `:on-apply` hook added in 0.5.0 is removed. A consumer is handed a projected view carrying `:accepted`, `:failure`, `:intent`, `:pending?` and `:writing?`, which is the state `:on-apply` existed to reach.
- Breaking. The accepted envelope a consumer sees no longer carries `:request/id`, and `resource/render-key` is removed with it. `:render-key` defaults to the accepted envelope itself.
- Fixes an endless refetch. A server that honours less of a query than it was asked no longer leaves the intent looking unanswered, so a failed request is not reissued forever.
- Fixes a boot that outlived its connection. An element removed or re-attached while it waits for the custom elements inside it to be defined no longer fetches for a connection that has ended or leaves a popstate listener behind.
- A `<server-resource>` reports what used to pass in silence: an intent naming a resource that is not on the page, an event or effect outside its vocabulary, and custom elements inside it that are never defined.
- Configures how requests are sent, through `credentials`, `headers` and `timeout` attributes and a request decorator for credentials that change independently of the resource value.
- Network failures carry their HTTP status, every failure names the query it concerns, and a rejected or broken SSR boot embed is adjudicated on connect.
- `shape.fields[].options` names each value a field may take, alongside `enum` which constrains it.

## 0.5.0

- Adds an optional `:on-apply` hook to `consumer-resource/register!`, called on every projection, so a consumer can re-derive state that does not depend on `:last-accepted` being present (an empty gate driven by the URL, for example).
- Refactors the pure core and the executor edge for readability and efficiency, with no change to behavior beyond the new hook.

## 0.4.0

- Multiple `<server-resource>` elements coordinate on one page. A named resource namespaces its URL keys under its `resource-id` (`tasks.sort`, `projects.name`), and an unnamed one owns the bare root keys. A consumer sends a targeted `submit-intent!` to a named sibling, so coordination runs through the URL with no shared store.
- Adds the move op: a positional PATCH command that repositions a member by its server-owned rank, carrying only the destination. Update stays a full replace.
- Bumps BareDOM to 3.7.0 for the `x-drag-panel` and `x-drop-zone` components.
- Demo gains a relational kanban board (two coordinating resources, drag-as-move, create project, quick-add task) alongside the flat table.

## 0.3.0

- A write returns the full post-mutation state. The client installs it directly instead of refetching, so a write is one round-trip, not two.
- Breaking. An accepted write must now be a full envelope. The old value-less ack is a protocol failure, and writes carry the current query.

## 0.2.0

- Implements PUT as a full replace.
- The write op vocabulary is data. Reads and writes share one request builder.
- URL-encoded path segments and sorted query keys. An unbuildable write yields no request.
- Demo gains per-row editing. Field-less write rejections show as a message.
- Bumps BareDOM to 3.5.0 for the clearable date picker.

## 0.1.0

First release. The `<server-resource>` runtime with a pure `step` lifecycle, the `consumer-resource/register!` mechanism, create and delete writes, and a Babashka-backed demo. Published to Clojars.
