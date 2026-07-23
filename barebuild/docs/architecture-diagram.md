# BareBuild — Architecture Diagram

A projection of server state into presentational BareDOM components, with writes running
back through the same loop. Two custom elements (`<server-resource>`, `<x-table-consumer>`)
sit between an independent JSON server and the visual `<x-table>`; the whole lifecycle is
one pure `step` function whose only outputs are a next value and a list of effects executed
at a decisionless edge.

Two figures: **Figure 1** is the whole picture (boxes = structure, arrows = data flow);
**Figure 2** zooms into the pure `step` ↔ effects ↔ executor loop.

## Figure 1 — Overview (structure + data flow)

```mermaid
%%{init: {'themeVariables': {'fontSize': '18px'}}}%%
flowchart TB
  subgraph SERVER["bb dev-server (oracle · cheshire, independent JSON)"]
    API["/api/tasks<br/>accepted | rejected envelope"]
    BOOT["/demo/boot<br/>SSR embed (script/json)"]
  end

  subgraph BROWSER["Browser"]
    URL["URL query (tasks.*)<br/>+ history / popstate"]
  end

  subgraph SR["&lt;server-resource&gt;"]
    WIRE["wire<br/>Conversion 1: JSON → CLJS"]
    STEP["step (pure)<br/>lifecycle over the resource value"]
    EXEC["executor (run-effects!)<br/>decisionless edge"]
  end

  subgraph CONS["&lt;x-table-consumer&gt;"]
    PROJ["project<br/>accepted → view-model"]
    C2["Conversion 2: CLJS → DOM"]
  end

  TABLE["&lt;x-table&gt; · &lt;x-table-cell&gt;<br/>x-pagination"]

  URL -->|"connect / popstate: canonical url-intent"| STEP
  BOOT -->|"embedded envelope on load"| WIRE
  EXEC -->|"fetch: GET /api/tasks?requestId=tasks:N + query"| API
  EXEC -->|"write: POST /api/tasks · DELETE /api/tasks/:id"| API
  EXEC -.->|"abort: cancel in-flight"| API
  API -->|"JSON envelope: value · shape · query echo · requestId"| WIRE
  API -->|"JSON ack: outcome · requestId · revision"| WIRE
  WIRE -->|"CLJS response, or protocol-failure marker"| STEP
  STEP -->|"effects: fetch · write · url-write · notify · abort · diagnostic"| EXEC
  EXEC -->|"url-write: push/replaceState (scoped)"| URL
  EXEC -->|"notify-consumers: applyResource(resource, ctx)"| PROJ
  PROJ --> C2
  C2 -->|"x-table-row / x-table-cell children"| TABLE
  TABLE -->|"x-table-cell-sort (direction) · page-request · delete"| CONS
  CONS -->|"submit-intent! → :intent-patch"| STEP
  CONS -->|"submit-write! → :submit-write"| STEP
```

**Arrow legend (the data that rides each edge):**

- **URL → step** — on connect and on back/forward, `construct-url-intent` reads the
  `tasks.*` params and `canonicalize-query`s them into the `:url-intent`.
- **executor → server** — a `:fetch` effect becomes `GET /api/tasks?requestId=…&<query>`.
  The request id is minted *inside* `step` and carried on the effect.
- **executor → server (write)** — a `:write` effect becomes `POST /api/tasks` with the
  record as its body, or `DELETE /api/tasks/:id`. The write id is minted in `step` the same
  way the request id is.
- **server → wire** — a plain JSON envelope (accepted: `outcome/requestId/revision/query/
  value/shape/pageInfo`; rejected: `outcome/requestId/query/error`), or for a write a bare
  ack (`outcome/requestId/revision`, plus `error` when rejected). The server emits it
  independently (cheshire), never via BareBuild's bijection.
- **wire → step** — Conversion 1 parses it to a CLJS `:response` value, or a
  `:protocol-failed` marker for a malformed envelope.
- **step → executor** — the pure result: a next resource value plus effects as data.
- **executor → URL** — a `:url-write` reflects the (adopted, normalized) query back into
  the address bar via `build-scoped-url` + `history`.
- **executor → consumer** — a `:notify-consumers` calls `applyResource`, handing over the
  whole resource value.
- **consumer → x-table** — `project` builds a `{:columns :rows}` view-model; Conversion 2
  renders it as `x-table-row`/`x-table-cell` children.
- **x-table → consumer → step** — a sort/page gesture becomes an intent patch via
  `translate-gesture` and re-enters `step` as `:intent-patch`. A delete gesture instead
  becomes `{:op :delete :id …}` and re-enters as `:submit-write`; the ack triggers a
  refetch, so the removed row disappears through the ordinary render path.

## Figure 2 — Zoom: the pure loop (`step` ↔ effects ↔ executor)

```mermaid
%%{init: {'themeVariables': {'fontSize': '18px'}}}%%
flowchart LR
  EVENTS["Events in (closed set)<br/><br/>:connected (embed)<br/>:intent-patch (query-patch, gesture-class)<br/>:url-changed (query)<br/>:response<br/>:protocol-failed<br/>:network-failed<br/>:submit-write<br/>:write-ack<br/>:write-failed<br/>:disconnected"]

  STEP["step (pure)<br/>resource × event → resource′ + effects<br/><br/>resource value:<br/>:url-intent · :last-accepted · :last-failure<br/>:active-request (request/id, query)<br/>:active-write (write/id, payload)<br/>:request-count · :write-count · :shape<br/>:history-policy · :endpoint"]

  EFFECTS["Effects out (data)<br/><br/>:fetch (endpoint, query, request/id)<br/>:write (endpoint, write/id, payload)<br/>:url-write (params, mode)<br/>:notify-consumers (resource)<br/>:abort (request/id)<br/>:diagnostic (stale-*)"]

  EXEC["executor (decisionless edge)<br/><br/>fetch · write · history push/replace<br/>applyResource · AbortController<br/>console diagnostics"]

  EVENTS --> STEP --> EFFECTS --> EXEC
  EXEC -->|"response · failure · gesture · popstate"| EVENTS
```

**What the loop guarantees:**

- **Pure vs edge.** Every decision lives in `step` and is visible in the returned
  effects; the executor only performs them (fetch, write, history, notify, abort,
  diagnostics). `step` is `=`-testable and replayable from an event log.
- **Writes re-enter the same loop.** `submit-write!` → `:submit-write` → a `:write` effect;
  the ack returns as `:write-ack` and schedules a refetch. `writing?` derives from
  `:active-write` exactly as `pending?` derives from `:active-request`, and a stale ack is
  dropped by the same id-match rule. Nothing is rendered optimistically, so no rollback
  path exists to get wrong.
- **One request in flight.** `start-request` mints a monotonic `:request/id` into
  `:active-request`; `pending?`/`installable?` derive purely from the value. A response is
  installed iff its id matches the live request; a gesture made mid-flight is picked up by
  the single trailing fetch once the in-flight request clears.
- **Two conversions only.** JSON↔CLJS at the network edge (`wire`, Figure 1 left) and
  CLJS→DOM at the component edge (`x-table-consumer`, Figure 1 right). CLJS values in
  between, because structural `=` is load-bearing.

## Figure 3 — The consumer layer (one mechanism, many thin consumers)

```mermaid
%%{init: {'themeVariables': {'fontSize': '18px'}}}%%
flowchart TB
  SR["&lt;server-resource&gt;<br/>notify-consumers → applyResource(resource)"]

  subgraph MECH["consumer-resource/register! — shared mechanism (one file)"]
    APPLY["applyResource<br/>child caching · submit-intent! · submit-write! · four change-guards"]
    G1["render — on :last-accepted change"]
    G2["on-failure — on :last-failure change (nil = recover)"]
    G3["on-pending — on pending? change"]
    G4["on-writing — on writing? change"]
    APPLY --> G1
    APPLY --> G2
    APPLY --> G3
    APPLY --> G4
  end

  subgraph TABLE["&lt;x-table-consumer&gt;"]
    TR["render → rows/cells + pagination"]
    TF["on-failure → x-alert"]
    TP["on-pending → aria-busy + dim"]
    TG["gestures → submit-intent! · submit-write!"]
  end

  subgraph FORM["&lt;x-task-form-consumer&gt;"]
    FR["render → populate fields from :shape"]
    FW["on-writing → disable submit · close on success"]
    FF["on-failure → field error from rejection details"]
    FG["submit → validate · submit-write!"]
  end

  subgraph STAT["&lt;x-stat-consumer&gt;"]
    ST["render → value attr"]
    SP["on-pending → loading attr"]
  end

  SR --> APPLY
  G1 --> TR
  G2 --> TF
  G3 --> TP
  G1 --> ST
  G3 --> SP
  G1 --> FR
  G2 --> FF
  G4 --> FW
  TG -->|":intent-patch · :submit-write"| SR
  FG -->|":submit-write"| SR
```

**The de-complect.** Every consumer braids three concerns; the mechanism owns one and each
consumer owns the other two:

- **Mechanism** (`consumer_resource.cljs`) — *how a consumer is driven*: the `applyResource`
  install, the four change-guards, child caching, intent and write submission. Written once.
- **Calculation** (each `model.cljs`) — *resource → view data*: pure, node-tested projection.
- **Effect** (each element file) — *view data → DOM*: the
  `render`/`on-failure`/`on-pending`/`on-writing` hooks, all `(child value this)`.

Adding a component is therefore a projection plus a render fn (plus optional hooks) — the
mechanism is untouched. x-stat (scalar, display-only), x-table (list, gestures, failure UI,
pagination) and x-task-form (writes, validation, modal lifecycle) span that range, driven by
the same core.
