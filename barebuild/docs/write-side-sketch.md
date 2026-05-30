# BareBuild Write-Side Sketch (V1.1 candidate)

**Status:** Design speculation, **not** a V1 commitment. This document preserves the best-guess shapes for the deferred write-side coordination elements so that, when Phase 5 telemetry arrives, we have a concrete prior to compare findings against — not so that we ship to this spec.

The V1 plan ([`BAREBUILD-V1-PLAN.md`](BAREBUILD-V1-PLAN.md)) deliberately omits these elements. Users wire their write paths by hand during Phase 5; we read what they wrote; **then** we decide whether the shapes below survive contact with use.

If you are reading this looking for the V1 contract — there isn't one for these elements. Use `addEventListener` + `fetch`.

---

## Why this is paper, not ship

The risk these elements pose is the same one any "convenient wrapper for cross-component coordination" poses: the wrapper's shape ossifies the *first* use case its author imagined, and every later use case bends around it. The Hickey-aligned move is to delay committing to a shape until we have at least three independent implementations to look at.

The original V1 plan specified these three elements in full DoD-checklist detail "as paper, gated on 5-lite feedback." That tension was the symptom — paper specs grow concrete by gravity. Splitting the document moved the speculation out of the roadmap so the roadmap reflects what V1 actually ships.

## The three deferred elements (best-guess sketch)

### `<barebuild-action action="/api/tasks" method="POST" submit-event="x-form-submit" values-path="[:values]">`

Wraps a submit emitter by **containment**: any descendant element dispatching a cancelable bubbling event of the configured `submit-event` name. The action reads `event.detail` at `values-path` (default `[:values]`, so `<x-form>` works unconfigured), `preventDefault()`s the cancelable submit, JSON-encodes the values, fetches, publishes `.state` + `barebuild-action-state {:name :state}`.

- **`name` attribute** identifies the action for `<barebuild-bind>` consumers.
- **Does not dispatch invalidation** (its child `<barebuild-invalidate-on>` does).
- **Does not name `x-form`**; works with any descendant emitting the configured `submit-event`.
- **JSON only.**

Existing draft prose: [`docs/barebuild-action.md`](../../docs/barebuild-action.md).

### `<barebuild-bind from-name="users-data" path="[:state :data]" prop="items">`

Placed **as a child of the target consumer**; listens at document for the source's state event (default `barebuild-data-state`; configurable `event`); matches the source by **`from-name`** against `event.detail.name`; writes the value at `path` (literal EDN vector, resolved via `get-in`) to `parentNode[prop]`.

- **No selectors, no `target` attribute, no sibling defaults.** Parent is target by construction; source is matched by name.
- `from-name` and `prop` are required; `path` defaults to `[:state :data]`; `event` defaults to `barebuild-data-state`.
- Parse failure on `path` logs the literal value and no-ops.
- If `parentNode` is `null` (orphan bind), logs and no-ops.

Existing draft prose: [`docs/barebuild-bind.md`](../../docs/barebuild-bind.md).

### `<barebuild-invalidate-on when-phase="success" src="/api/users">`

Placed **as a child of the source element** (typically `<barebuild-action>`); listens to the source's state event (default `barebuild-action-state`; configurable `event`) via `parentNode.addEventListener`; on match (`when-phase` against `:phase`, `when-name` against `:name`; at least one required; both AND), dispatches a bubbling `barebuild-invalidate {:src}` at document level. Any `<barebuild-data>` with matching `src` self-matches (exact `URL.pathname` equality) and refetches.

- Orphan (no `parentNode`) logs and no-ops.
- At least one match attribute required; omitting both logs and no-ops.

Existing draft prose: [`docs/barebuild-invalidate-on.md`](../../docs/barebuild-invalidate-on.md).

## Canonical sketch — full pattern

```html
<!-- Read side: broker publishes by name. -->
<barebuild-data name="users-data" src="/api/users" trigger-on-connect></barebuild-data>

<x-table>
  <barebuild-bind from-name="users-data" path="[:state :data]" prop="items"></barebuild-bind>
</x-table>

<!-- Write side: action wraps emitter; invalidate-on lives inside action. -->
<barebuild-action name="add-user" action="/api/users" method="POST" submit-event="x-form-submit">
  <x-form>
    <input name="name">
    <x-button type="submit" label="Add"></x-button>
  </x-form>
  <barebuild-invalidate-on when-phase="success" src="/api/users"></barebuild-invalidate-on>
</barebuild-action>
```

Six elements total (three V1 + three V1.1 candidates), each doing one thing, every wire either contained or named. No `querySelector` anywhere. Drop any one and the rest still work. Move the action and its `<barebuild-invalidate-on>` moves with it (containment).

## What we are watching for in Phase 5

The decision after Phase 5 is **not** "do we ship these three elements?" It is "what do the three real implementations users wrote look like, and do they cluster around this shape?"

Things to look for:

1. **Submit → fetch wiring.** Did users reach for the form's `x-form-submit` event, or did they intercept at button-click? Did they use a separate emitter? What did the payload shape look like — did anyone dispatch something other than `{:values …}`?

2. **Response → DOM update.** Did users reach for `querySelector`, capture element handles at page load, or use a refs map / `getElementById`? How did they read the broker's `.state` — via the property, via the dispatched event, or both? Did anyone reach for re-frame to mediate?

3. **Invalidation.** After a successful write, how did users refetch? Did they call a method on the data broker (which V1 does not expose), construct a new `<barebuild-data>` element, or set `src` to itself? Did anyone roll their own pub-sub?

4. **Identity.** How many users mutated `parentNode[prop]` directly vs. dispatched a custom event for their own component to absorb?

5. **The shape of the data they passed.** Did `[:state :data]` actually fit, or did real APIs nest the response payload differently?

## Decisions on these elements that the original V1 plan resolved (carried forward)

These remain useful priors even though the elements are deferred:

- **No CSS selectors anywhere.** Containment for one end of the wire; name-in-event-detail for the other end; URL/pathname for invalidation matching.
- **`.state` is a plain JS object `{ phase, data, error, httpStatus }` (string phase).** Same as `<barebuild-data>` — it is **not** a CLJS persistent map, because a consumer's app is compiled with its own `cljs.core` and could not read this module's `Keyword`/map classes (BAREBUILD-V1-PLAN Decision #6). This is now a load-bearing prior, not a convenience: any write-side element exposing `.state` must do the same, and the `path`/`get-in` mechanism below has to be reconciled with a JS-keyed object (string keys, `goog.object/getValueByKeys` rather than `get-in` over keywords) when these elements are actually designed.
- **`path` is a literal EDN vector** parsed via `cljs.reader/read-string` and resolved via `get-in`. JSON arrays parse identically. No dotted-string DSL.
- **`values-path` is a literal EDN vector** with the same semantics. Default `[:values]` covers `<x-form>` unconfigured.
- **Exact-pathname URL match** for invalidation; no pattern DSL.
- **Two orthogonal match attributes** on `<barebuild-invalidate-on>` (`when-phase`, `when-name`), AND-composed, at least one required. Same shape as `<barebuild-data>`'s two orthogonal triggers.

These priors are the ones to defend hardest if Phase 5 findings push back — they're the load-bearing simplicity commitments. The element *shapes* (which attributes, which defaults, which event names) are negotiable; the *no-selectors / values-not-places* spine is not.

## Reference: existing per-component drafts

The original V1 plan shipped detailed per-component docs for the deferred elements:

- [`docs/barebuild-action.md`](../../docs/barebuild-action.md)
- [`docs/barebuild-bind.md`](../../docs/barebuild-bind.md)
- [`docs/barebuild-invalidate-on.md`](../../docs/barebuild-invalidate-on.md)

These remain in the repo as the prose form of the sketch above. They are **not V1 contracts** and should not be treated as such by anyone reading them — when V1.1 begins, they get re-litigated against Phase 5 findings.
