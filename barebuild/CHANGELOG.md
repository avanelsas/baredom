# Changelog

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
