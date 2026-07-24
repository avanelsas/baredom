# Changelog

## 0.2.0

- Update (PUT) joins create and delete. It is a full replace: the body carries every field the
  shape declares, so a key the client omits is cleared rather than left alone. The id travels
  in the path and never in the body, and an id matching no row is rejected rather than accepted
  as a no-op — unlike delete, an update of an absent row has nothing to replace.
- The op vocabulary is data. `write-ops` maps each op to a method, a collection- or
  member-addressed URL, and whether it carries a body; `step` resolves that into the `:write`
  effect. The executor performs the request and decides nothing about it. Reads now go through
  the same builder, so neither edge assembles a URL by hand.
- Path segments are URL-encoded, and query keys render in sorted order so the same query always
  produces the same URL. A write whose op is unknown, or which addresses a member without an id,
  yields no request at all rather than a malformed one.
- The demo table gains per-row editing. The task form opens on an existing task, prefills from
  the shape, and submits an update. A rejection naming no field — an unknown id, an unreachable
  server — now reads as a message in the modal instead of closing it as though the write had
  succeeded.

## 0.1.0

First release. BareBuild — the server-resource runtime for BareDOM — and its showcase demo.

- The `<server-resource>` element and the pure `step` lifecycle
  (fetch, sort, page, filter, rejection, keep-stale, contract validation, echo-adoption,
  trailing-fetch, network/protocol failures, disconnect-abort, SSR boot), plus the shared
  `consumer-resource/register!` mechanism for authoring consumers.
- Create and delete run through the same loop: `submit-write!` -> `:write`
  effect -> ack -> refetch, so the rendered value always comes from the server. Create
  payloads are validated locally against the `shape` the server sends before submission.
- A demo showcase. A Babashka tasks server and a live page where one `<server-resource>`
  drives five consumers: x-stat, x-progress, x-table, x-search-field and x-form. All using server
  state, covering sort, filter, paging, create and delete.
- Scripts to publish to to Clojars as `com.github.avanelsas/barebuild`.

Writes cover create and delete only — there is no update/PUT yet.
