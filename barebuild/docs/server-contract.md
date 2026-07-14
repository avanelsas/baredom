# The server contract

BareBuild is a *read projection*: it never owns state, it renders whatever an HTTP endpoint
returns. That endpoint is the real integration point, the contract a server
must satisfy. It is transport- and language-agnostic (plain JSON); the demo's
[`read_demo/dev-server/server.clj`](../read_demo/dev-server/server.clj) is a complete
reference implementation.

## The request

BareBuild fetches with a single GET — read-only, no body:

```
GET <endpoint>?<query>&requestId=<id>
```

- **`<endpoint>`**: The `src` of the `<server-resource>`.
- **`<query>`**: The current user intent as URL query params (sort, page, filter, …
  whatever the consumers submit). BareBuild forwards them as-is. The server decides which it
  honors.
- **`requestId`**: An opaque id BareBuild mints per request, echo it back unchanged.

## The response

Always **HTTP 200** for a business outcome (see [Status codes](#status-codes)). The JSON body
is one of two envelopes.

### Accepted — the server returns data

| Field | Type | Notes |
|---|---|---|
| `outcome` | `"accepted"` | |
| `requestId` | string | echo of the request's id |
| `revision` | string | opaque version tag for the resource |
| `query` | object | the server's **normalized** echo of the honored query (see [The query echo](#the-query-echo-is-load-bearing)) |
| `value` | array | the records to render |
| `shape` | object | structural contract for the records (see [The shape](#the-shape)) |
| `pageInfo` | object | `{ page, pageSize, totalPages, totalCount }` — all numbers |

### Rejected — the server refuses the query

A *business* verdict (e.g. an unsupported sort field), not a transport error.

| Field | Type | Notes |
|---|---|---|
| `outcome` | `"rejected"` | |
| `requestId` | string | echo |
| `revision` | string | |
| `query` | object | the rejected query, echoed |
| `error` | object | `{ code, message, details }` — `code` a short slug, `message` human-readable, `details` free-form |

A rejected envelope carries **no `value` / `shape`** — BareBuild keeps the last good view and
surfaces the error.

## The shape

`shape` is a structural description of a record: which field is its identity, and the key and
type of every field it carries. BareBuild validates each response against it, so the runtime
stays domain-agnostic — it never hardcodes field names; the shape tells it what to expect.

```json
{
  "idKey": "id",
  "fields": [
    { "key": "owner",  "type": "string" },
    { "key": "start",  "type": "date"   },
    { "key": "status", "type": "string" }
  ]
}
```

- **`idKey`**: The field that uniquely identifies a record. Every record must have it, and ids must
  be unique.
- **`fields`**: The declared, consumable fields. Each has a `key` and a `type`, one of
  **`string` · `number` · `date` · `url`**. `null` is allowed for any field.

On every accepted response BareBuild **validates the records against this shape** (id present and
unique, each declared field present, each value the declared type). A mismatch is a *contract
failure*: the response is not installed and the last good view stays.

## Two rules to keep in mind

### Status codes

`accepted` and `rejected` are **both HTTP 200**. The `outcome` field carries the verdict, not
the status code. A `4xx` / `5xx` is read as a **transport failure**, not a rejection. So don't
return `400` for an invalid query, return `200` with `outcome: "rejected"`.

### The query echo is load-bearing

The server must **echo the query it honored**, normalized, in the accepted `query`
field. BareBuild adopts that echo as the canonical intent and writes it back to the URL. If you
drop a param you honored from the echo, BareBuild reads it as *"the server removed that param"*
and strips it from the URL. The classic symptom is a filter or sort that silently reverts.
Echo every param you honored. Omit only the ones you genuinely ignored.

## Failure modes

BareBuild distinguishes four, and **all keep the last good view on screen**:

| Failure | Trigger |
|---|---|
| **rejected** | `outcome: "rejected"` with an `error` |
| **contract** | an accepted envelope whose records don't match the declared `shape` |
| **protocol** | the body isn't a valid envelope (unparseable JSON, or missing `value` + `shape` / missing `error`) |
| **network** | no response, or a non-2xx status |

## The round-trip

```mermaid
%%{init: {'themeVariables': {'fontSize': '16px'}}}%%
sequenceDiagram
  participant C as server-resource
  participant S as Server
  C->>S: GET endpoint?query & requestId
  alt accepted (HTTP 200)
    S-->>C: outcome:accepted · query echo · value · shape · pageInfo
    C->>C: validate records vs shape → render, adopt echo into URL
  else rejected (HTTP 200)
    S-->>C: outcome:rejected · query · error
    C->>C: keep last good view · show error · revert URL
  else transport failure
    S--xC: non-2xx / no response
    C->>C: keep last good view · retry on next intent
  end
```

## Optional: SSR boot

To paint on first load with no request, embed the first accepted envelope in the page as a
`<script type="application/json">` child of `<server-resource>`. BareBuild reads it, renders
immediately, and fetches only if the URL intent differs. The demo serves this at `/demo/boot`.

## Reference implementation

The demo's Babashka server ([`read_demo/dev-server/server.clj`](../read_demo/dev-server/server.clj))
implements this contract end to end, query normalization, the shape, `pageInfo`, and a
fixture for every failure mode while emitting JSON independently of BareBuild. See
[`../read_demo/README.md`](../read_demo/README.md).
