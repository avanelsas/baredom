# Changelog

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
