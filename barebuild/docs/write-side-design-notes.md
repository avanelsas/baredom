# BareBuild Write-Side Design Notes (Phase 4 output — the V1.1 gate)

> **STATUS: TEMPLATE — not yet filled.** This document is the *evidence* side of the
> write-side design decision. It is filled from telemetry collected when real users
> hand-wire create/update/delete against the [demo app](../demo-app/) (the unwired
> seams in [`write_side.cljs`](../demo-app/src/demo_app/write_side.cljs)). When it is
> populated, it **gates V1.1**: the deferred elements `<barebuild-action>` /
> `<barebuild-bind>` / `<barebuild-invalidate-on>` are designed *from* what is recorded
> here, not from a guess.
>
> [`write-side-sketch.md`](write-side-sketch.md) is the **PRIOR** (our best guess).
> This file is the **EVIDENCE**. Every finding below is recorded against the sketch's
> corresponding prior so the two can be diffed. The sketch must not be cited as a
> contract; this file decides whether the sketch's shapes survive contact with use.
>
> Terminology: the plan's Phase Map ([`BAREBUILD-V1-PLAN.md`](BAREBUILD-V1-PLAN.md))
> calls the demo + telemetry-collection step **Phase 4**; the sketch refers to the
> same step as "Phase 5." They are the same step — this is its output.

---

## How this was collected

- **Participants:** _N_ of the target 3–5 CLJS developers. _(list / anonymize)_
- **Task given:** wire create, update, and delete against `bb serve` by filling the
  five inert seams in `write_side.cljs`, "however feels natural." The backend already
  implements the write endpoints, so each participant's `fetch` round-trips.
- **Blind to the prior:** participants did **not** see `write-side-sketch.md` until
  after they finished, so their shapes are unbiased by our guess.

> **Bias to control for.** The demo hands participants an `x-form` that emits
> `x-form-submit {:values …}` — exactly the sketch's `<barebuild-action>` default
> (`values-path [:values]`). So "they used `{:values}`" is **weak** signal: it may
> reflect the form we handed them, not a preference. For each participant, record
> whether they used `event.detail.values` *as-is* or **reshaped** the payload before
> `fetch` — only the reshaping (or the friction they reported with `{:values}`) is
> real signal about the contract.

_(One short subsection per participant, or a table: who, what they built, links to
their wiring code.)_

| Participant | Create | Update | Delete | Used `{:values}` as-is? | Notes |
|---|---|---|---|---|---|
| _A_ | | | | | |
| _B_ | | | | | |
| _C_ | | | | | |

---

## Findings against the five telemetry questions

Mirrors the five questions in [`write-side-sketch.md` → "What we are watching for"](write-side-sketch.md).
Record the observed behaviour and whether it matched the sketch's prior.

### Q1 — Submit → fetch wiring
*Sketch prior:* `<barebuild-action submit-event="x-form-submit" values-path="[:values]">`
reads `event.detail.values`, `preventDefault`s, JSON-encodes, fetches.

- Did they hook `x-form-submit`, intercept at button click, or use a separate emitter?
- Payload shape: `{:values}` as-is, or reshaped to what? (see bias note)
- _findings:_ _…_

### Q2 — Response → DOM update
*Sketch prior:* `<barebuild-bind from-name=… path="[:state :data]" prop=…>` writes the
value to `parentNode[prop]`; consumers read the broker's `.state`.

- `querySelector`, handles captured at boot, a refs map, or `getElementById`?
- Read the broker's `.state` via the property, the dispatched event, or both?
- Did anyone reach for re-frame to mediate? At what point, and for what?
- _findings:_ _…_

### Q3 — Invalidation after a write
*Sketch prior:* `<barebuild-invalidate-on when-phase="success" src=…>` dispatches
`barebuild-invalidate {:src}`; matching `<barebuild-data>` refetches.

- Re-set `src` to itself? dispatch `barebuild-data-refresh`? build a new broker?
  splice the DOM directly? roll their own pub/sub?
- _findings:_ _…_

### Q4 — Identity (who absorbs the new value)
*Sketch prior:* bind mutates `parentNode[prop]`; the component's own setter absorbs it.

- Direct `parentNode[prop]` mutation vs. dispatching a custom event for their own
  component to absorb vs. rebuilding child DOM (the `x-table` row-composition shape)?
- _findings:_ _…_

### Q5 — Shape of the data passed
*Sketch prior:* `path` default `[:state :data]` fits the response.

- Did `[:state :data]` actually fit, or did real APIs nest the payload differently?
- _findings:_ _…_

---

## Convergence analysis

- **Clusters (≥2 independent implementations agree):** _candidate contracts_ — _…_
- **Divergence (one-offs / disagreement):** _defer; do not ossify_ — _…_
- **Sketch priors that SURVIVED contact with use:** _…_
- **Sketch priors that were CONTRADICTED:** _…_

---

## Load-bearing priors check

From [`write-side-sketch.md` → "Decisions … carried forward"](write-side-sketch.md).
These are the *non-negotiable* simplicity commitments — element shapes are negotiable,
this spine is not. Record whether real use respected or fought each one.

- [ ] **No CSS selectors anywhere** — containment + name-in-detail + URL match. _Held?_ _…_
- [ ] **Values, not places** — wiring by containment/URL, value read from `event.detail`
      not the live element place. _Held?_ _…_
- [ ] **`.state` is a plain JS object** `{ phase, data, error, httpStatus }`, not a CLJS
      map (Decision #6). Did any write-side `.state` read hit the `cljs.core` boundary
      (keyword lookups returning nil)? How did `path`/`get-in` reconcile with string
      keys? _…_
- [ ] **`path` / `values-path` are literal EDN vectors**, not a dotted-string DSL. Did
      anyone want a string DSL instead? _…_
- [ ] **Exact-pathname URL match** for invalidation, no pattern DSL. _Sufficient?_ _…_

---

## Hiccup-renderer demand

Not one of the five questions, but the gate for promoting the deferred `bb.render`
work into V1.1 ([`BAREBUILD-V1-PLAN.md` → Deferred #1](BAREBUILD-V1-PLAN.md)).

- Did participants ask for a Hiccup renderer? What shape did they sketch?
- Did the row-composition / property-setter rebuild feel sufficient without one?
- _findings:_ _…_

---

## V1.1 recommendation

_Filled once the above is populated._

- [ ] Ship `<barebuild-action>` / `<barebuild-bind>` / `<barebuild-invalidate-on>` **as
      sketched** (priors held, shapes converged).
- [ ] Ship **modified** shapes — describe the deltas from the sketch: _…_
- [ ] **Defer further** — collect more implementations before committing (why: _…_).
- [ ] **Promote the Hiccup renderer** into V1.1? Y / N + rationale: _…_

---

## Raw artifacts

Verbatim copies / links of each participant's wiring code (the primary evidence —
mirrors how [`validation-responses.md`](validation-responses.md) keeps Phase 0 responses
verbatim). Prefer pasting the actual code over summarizing it.

_(paste here)_
