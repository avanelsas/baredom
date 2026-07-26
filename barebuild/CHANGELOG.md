# Changelog

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
