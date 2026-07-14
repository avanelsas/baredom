# Changelog

All notable changes to BareBuild are documented here. This project adheres to
[Semantic Versioning](https://semver.org/).

## 0.1.0

First release. BareBuild — a read-only server-resource read projection runtime for
BareDOM — and its showcase demo are implemented.

- **Runtime (product).** The `<server-resource>` element and the pure `step` lifecycle
  (fetch, sort, page, rejection, keep-stale, contract validation, echo-adoption,
  trailing-fetch, network/protocol failures, disconnect-abort, SSR boot), plus the shared
  `consumer-resource/register!` mechanism for authoring consumers.
- **Demo (showcase).** A Babashka tasks server and a live page where one `<server-resource>`
  drives four example consumers — stat, progress, table, and search-field — from server
  state.

Read-only; writes (commands, mutations) are a later phase.
