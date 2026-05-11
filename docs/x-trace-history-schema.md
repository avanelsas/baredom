# x-trace-history JSON schema

The on-disk format for traces exported by `window.BareDOM.traceHistory.export()`
and the dock's **Export** button. Importing tooling — the **Import** button
shipped in PR 11, the standalone viewer shipped in Phase 7, and any third-party
consumer — reads the same shape.

The schema is versioned with `schemaVersion: 1`. Breaking changes bump the
version; the recorder hard-rejects mismatched versions on import.

## Envelope

A trace file is a single JSON object:

```json
{
  "schemaVersion": 1,
  "exportedAt": 1715432100000,
  "origin": "https://app.example.com/path?baredom-trace-history",
  "userAgent": "Mozilla/5.0 ...",
  "forensic": false,
  "components": {
    "0": { "tag": "x-button",   "firstSeen": 12.34 },
    "1": { "tag": "x-checkbox", "firstSeen": 18.71 }
  },
  "sessions": [
    { "id": 0, "label": "Session 0", "startT": 100.5, "endT": 250.7,
      "startId": 42, "endId": 67 }
  ],
  "records": [ /* see below */ ]
}
```

### Top-level fields

| Field           | Type                | Required | Notes |
|-----------------|---------------------|----------|-------|
| `schemaVersion` | integer             | yes      | Currently `1`. Importers must reject other values. |
| `exportedAt`    | integer (ms)        | yes      | Wall-clock milliseconds since epoch (`Date.now()`). |
| `origin`        | string              | yes      | `window.location.href` at export time. Useful for bug reports; will be sanitised against credentials in a later phase. |
| `userAgent`     | string              | yes      | `navigator.userAgent` at export time. |
| `forensic`      | boolean             | yes      | True when the recorder was installed in forensic mode (`?baredom-trace-history=raw`). Receivers can interpret the absence of sample-rate gaps. |
| `components`    | object (id→info)    | yes      | Subset of the recorder's component side-index — only entries whose id is referenced by at least one record in `records[]`. Keys are stringified `componentId` integers. May be empty when records contain only document-target events (`componentId: null`). |
| `sessions`      | array of session    | yes      | Bounded recording slices the user captured. Empty array when none. |
| `records`       | array of record     | yes      | The full ring buffer, oldest first. May be empty. |

### components entry

```json
{ "tag": "x-button", "firstSeen": 12.34 }
```

| Field       | Type   | Required | Notes |
|-------------|--------|----------|-------|
| `tag`       | string | yes      | Lowercase tag name observed for this component instance. |
| `firstSeen` | number | yes      | `performance.now()` value when the recorder first stamped this id onto an element. |

### session entry

```json
{ "id": 0, "label": "Session 0", "startT": 100.5, "endT": 250.7,
  "startId": 42, "endId": 67 }
```

| Field     | Type             | Required | Notes |
|-----------|------------------|----------|-------|
| `id`      | integer          | yes      | Stable session id, unique within the export. |
| `label`   | string           | yes      | Human-readable label. Default is `"Session N"`. |
| `startT`  | number           | yes      | `performance.now()` at session start. |
| `endT`    | number \| null   | yes      | `performance.now()` at session stop. `null` when the session was still recording at export time. |
| `startId` | integer          | yes      | Inclusive lower bound on `records[*].id`. |
| `endId`   | integer \| null  | yes      | Exclusive upper bound on `records[*].id`. `null` when the session was still recording at export time. |

Sessions are metadata only — record membership is derived from
`[startId, endId)` against the `records` array.

## Records

Every record carries the same baseline fields, then type-specific extras.

### Baseline (every record)

| Field           | Type             | Notes |
|-----------------|------------------|-------|
| `schemaVersion` | integer          | Same as the envelope. Lets isolated records still self-describe. |
| `id`            | integer          | Monotonically increasing record id. Used by `causeId` to refer to other records. |
| `t`             | number           | `performance.now()` value at the moment the record was created (dispatch records carry the dispatch frame's *entry* time). |
| `type`          | string           | Record kind. See type catalogue below. |
| `tag`           | string           | Lowercase tag name of the target element, or `"document"` for document-target events. |
| `componentId`   | integer \| null  | Stable id assigned by the recorder. `null` for document-target events. |
| `causeId`       | integer \| null  | Id of the synchronously-enclosing dispatch record, or `null` when the record was produced outside any active dispatch. |

### Type catalogue

| `type`                          | Extra fields |
|---------------------------------|--------------|
| `event/dispatch`                | `eventName` (string), `detail` (any JSON), `cancelable` (boolean), `defaultPrevented` (boolean) |
| `event/dispatch-cancelable`     | same as `event/dispatch` |
| `event/dispatch-document`       | same as `event/dispatch` |
| `state/instance-field-set`      | `field` (string), `value` (any JSON) |
| `dom/attribute-set`             | `attribute` (string), `value` (any JSON) — `.setAttribute` coerces to string at the DOM, but the recorded `value` is the pre-coercion payload the caller passed |
| `dom/attribute-removed`         | `attribute` (string) |
| `lifecycle/connected`           | — |
| `lifecycle/disconnected`        | — |
| `lifecycle/attribute-changed`   | `attribute` (string), `oldValue` (string \| null), `newValue` (string \| null) |

#### `detail` and `value` coercion

For `event/dispatch*` records, `detail` is run through a `clj->js`-with-
fallback coercion at record-creation time, so cyclic or non-serializable
event payloads land as `(str v)` rather than throwing. For
`state/instance-field-set` records, `value` is coerced the same way. For
`dom/attribute-set` records the `value` is whatever the caller passed
into `du/set-attr!` — usually a string, but the recorder records it
as-is without coercion.

Functions, `undefined`, and `Symbol` keys are dropped silently by
`JSON.stringify`. Importing tools should treat `detail` and `value`
as opaque JSON payloads.

## Stability guarantees

- **Within `schemaVersion: 1`:** the field names and meanings above are
  stable. New optional fields may be added at the envelope or record level,
  but existing fields keep their semantics.
- **Across versions:** a higher `schemaVersion` value signals a breaking
  change. Receivers must hard-reject mismatched versions and surface a
  clear error to the user.
- **Records are append-only on the wire.** Even though the recorder uses a
  ring buffer that drops oldest records, the exported `records` array
  reflects a snapshot in time and ids are monotonic across the export.

### Why `components` is filtered (not a full snapshot)

The recorder assigns a stable `componentId` to every element the first
time a record mentions it, and keeps the resulting `id → {tag, firstSeen}`
table monotonically for the page lifetime — `clear!` deliberately
preserves it so the same element keeps the same id across captures.
Without trimming, an exported `.trace.json` would carry every component
ever observed, including stale entries the user explicitly cleared away
before pressing Record.

The export filter keeps the components index aligned with the records
in the trace: the recipient of a shared trace only sees the components
referenced by the captured events, not the page's full history.

## Importer contract

The recorder's import path (`window.BareDOM.traceHistory.import(...)` and
the dock's drag-drop / file-picker entries) hard-rejects malformed
envelopes. Validation is deliberately narrow so future schema additions
stay backward-compatible — only the load-bearing fields are checked:

| Layer    | Required                                                                 | Behavior on failure                                       |
|----------|--------------------------------------------------------------------------|-----------------------------------------------------------|
| Envelope | JSON object root                                                         | `{"error": "Envelope is not a JSON object."}`             |
| Envelope | `schemaVersion` is a number                                              | `{"error": "Envelope is missing schemaVersion."}`         |
| Envelope | `schemaVersion === 1`                                                    | `{"error": "Schema version mismatch: expected 1, got N."}`|
| Envelope | `records` is an array                                                    | `{"error": "Envelope.records is not an array."}`          |
| Record   | each entry is an object carrying numeric `id`, numeric `t`, string `type` | `{"error": "Record at index N is missing required fields …"}` |

Type-specific record fields (`detail`, `value`, `attribute`, etc.) are
NOT validated — they are pass-through, so an importer survives a
forward-compatible release that adds optional fields. The dock's
formatting layer is nil-safe for missing extras.

A higher `schemaVersion` value signals a breaking change and is
rejected. Receivers must surface the error message to the user
verbatim; the recorder logs it via `console.warn` and the dock
shows it as a transient `.hint.error` line below the timeline.

### Reference-equality on import

`window.BareDOM.traceHistory.import(jsObject)` accepts an already-parsed
envelope and stores it by reference. The dock reads `envelope.records`
on every render; mutating the array externally will be reflected in
the dock view. For the safe case, pass a JSON string —
`import(JSON.stringify(env))` decouples the import store from the
caller's data.

## In-process callers: `export()` returns a live reference

When you call `window.BareDOM.traceHistory.export()` from the console or
JavaScript, the returned envelope's `records` field is reference-equal to
the recorder's internal memoised JS array — not a copy. Mutating that
array would corrupt the recorder's cache and subsequent reads. Treat the
envelope as read-only, or call `JSON.parse(JSON.stringify(env))` to get
an independent deep copy. The `download()` path is safe on its own —
`JSON.stringify` is read-only and the serialised file has no shared
identity with the live buffer.

## What the schema does NOT capture

- The DOM tree itself at any point in time (records reference elements by
  `componentId`, not by structure).
- Native, untrusted events fired by the user agent's internal dispatch
  path (a real click on a button is not recorded; its CustomEvent
  consequences are).
- Async causality. `causeId` links break at `setTimeout`,
  `requestAnimationFrame`, `Promise.then`, observer callbacks, and so on.
  See `docs/x-trace-history.md` for the full list.
- Recorder configuration beyond `forensic`. Ring-buffer capacity, hook
  installation order, and so on are intentionally not part of the wire
  format.
