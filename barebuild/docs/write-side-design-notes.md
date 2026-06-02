# BareBuild Write-Side Design Notes (Phase 4 output — the V1.1 gate)

> **STATUS: PARTIALLY FILLED — reference implementation only (N=1, biased).** This
> document is the *evidence* side of the write-side design decision. It is filled from
> telemetry collected when real users hand-wire create/update/delete against the
> [demo app](../demo-app/) (the seams in
> [`write_side.cljs`](../demo-app/src/demo_app/write_side.cljs)). When it holds **≥2
> independent, prior-blind implementations** that converge, it **gates V1.1**: the
> deferred elements `<barebuild-action>` / `<barebuild-bind>` /
> `<barebuild-invalidate-on>` are designed *from* what is recorded here, not from a
> guess. **The gate is NOT yet met** — see "Convergence analysis."
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

- **Participants:** 1 of the target 3–5 CLJS developers — and that one is the
  **BareBuild author** (Participant A below). This is the **reference implementation**:
  it proves the five seams are wireable with V1's read-side surface alone, and it
  records what the author reached for. It is **not unbiased telemetry**.
- **Task given:** wire create, update, and delete against `bb serve` by filling the
  five seams in `write_side.cljs`, "however feels natural." The backend already
  implements the write endpoints, so each `fetch` round-trips.
- **Blind to the prior:** **No** for Participant A. The author had seen
  `write-side-sketch.md` and knows the read side intimately. Per the bias note below,
  treat A's shape choices as a *lower bound on discoverability* (if the author reached
  for it, it exists), **not** as a convergence signal.

> **Bias to control for.** The demo hands participants an `x-form` that emits
> `x-form-submit {:values …}` — exactly the sketch's `<barebuild-action>` default
> (`values-path [:values]`). So "they used `{:values}`" is **weak** signal: it may
> reflect the form we handed them, not a preference. For each participant, record
> whether they used `event.detail.values` *as-is* or **reshaped** the payload before
> `fetch` — only the reshaping (or the friction they reported with `{:values}`) is
> real signal about the contract.

| Participant | Create | Update | Delete | Used `{:values}` as-is? | Notes |
|---|---|---|---|---|---|
| **A** (author — reference, **biased**) | `POST /api/tasks` → `barebuild-data-refresh` on `#tasks-data` + `.hide` modal | `PUT /api/tasks/:id` → `barebuild-data-refresh` on `#detail-data` | detail: `DELETE` → `barebuild-navigate` to `/tasks`; row: `DELETE` → `barebuild-data-refresh` on `#tasks-data` | **No — reshaped** (`without-blanks`) | Refetch-and-reproject for every seam; never touched `.state`, never spliced DOM, never reached for re-frame. |
| _B_ | | | | | |
| _C_ | | | | | |

---

## Findings against the five telemetry questions

Mirrors the five questions in [`write-side-sketch.md` → "What we are watching for"](write-side-sketch.md).

### Q1 — Submit → fetch wiring
*Sketch prior:* `<barebuild-action submit-event="x-form-submit" values-path="[:values]">`
reads `event.detail.values`, `preventDefault`s, JSON-encodes, fetches.

- **Hooked `x-form-submit`** on the form element (not a button-click intercept, not a
  separate emitter). `preventDefault`, then `fetch`. Matches the sketch's submit-event hook.
- **Payload reshaped, not as-is.** The author passed `event.detail.values` through a
  `without-blanks` helper before encoding, because `x-form` reports *every* named
  control and defaults an unset one to `""`. A present-but-blank key (`status: ""`)
  **shadows the server's default** (`{:status "todo"}`), so blanks had to be stripped
  for create/update to behave. This is the one real Q1 signal: the values *path* fit,
  but the values themselves needed massaging the sketch's `<barebuild-action>` does not
  currently do.
- One method-aware `request` primitive (`api.cljs`) served all four verbs; create
  POSTs, update/settings PUT, delete DELETEs (no body).

### Q2 — Response → DOM update
*Sketch prior:* `<barebuild-bind from-name=… path="[:state :data]" prop=…>` writes the
value to `parentNode[prop]`; consumers read the broker's `.state`.

- **Did not read the response payload into the DOM at all.** The author never read the
  broker's `.state`, never bound a value, never spliced a row. Instead each handler
  **asked the relevant broker to re-read from the server** and let the existing
  read-side pipeline (`barebuild-data-state` → `render-*!`) reproject. So the
  `<barebuild-bind>` mechanism (write a value into a prop) was **not exercised** —
  the response was discarded; the *server* was re-read as the source of truth.
- Element handles resolved via `document.querySelector` by id each time (no refs map,
  no boot-time capture, no `getElementById`).
- **re-frame:** not reached for, at any point.

### Q3 — Invalidation after a write
*Sketch prior:* `<barebuild-invalidate-on when-phase="success" src=…>` dispatches
`barebuild-invalidate {:src}`; matching `<barebuild-data>` refetches.

- **Dispatched `barebuild-data-refresh` AT the broker element** (the read-side's own
  purpose-built "read now" event — `barebuild_data` listens for it on `:self`). Not
  `src`-to-itself, not a new broker, not a roll-your-own pub/sub.
- For delete-from-detail, invalidation was **implicit in navigation**: dispatching
  `barebuild-navigate` to `/tasks` re-activates the board route, whose `on-route-change`
  re-sets `src` → fresh read. No explicit invalidate needed when you're changing route.
- Decision of *staleness* was trivial/hardcoded: the author knew which broker backed
  the view being written and refreshed exactly that one (`#tasks-data`, `#detail-data`,
  `#settings-data`). No matching-by-URL was needed because the writer already knows its
  reader by id.

### Q4 — Identity (who absorbs the new value)
*Sketch prior:* bind mutates `parentNode[prop]`; the component's own setter absorbs it.

- **None of the above.** The new value was absorbed by **re-fetching the server**, so
  the broker re-emitted `barebuild-data-state` and the existing render path rebuilt the
  child DOM. No `parentNode[prop]` mutation, no custom event for a component to absorb,
  no manual row composition. The "identity" question dissolved: nobody had to own the
  new value locally because the server stayed the single owner.

### Q5 — Shape of the data passed
*Sketch prior:* `path` default `[:state :data]` fits the response.

- **Not exercised on the write side** — the response body was discarded (see Q2), so
  the author never navigated into the response shape. On the *read* side `[:state :data]`
  already fit (the broker's `.state.data` is the array the render functions project).
  The 204 from DELETE *did* force a body-parse guard (`parse-body` reads `.text` first,
  yields `nil` for an empty body) — a real shape edge the sketch's JSON-only assumption
  glosses over.

---

## Convergence analysis

- **Clusters (≥2 independent implementations agree):** **NONE YET.** N=1, and that one
  is biased. No convergence can be claimed.
- **Strongest single-impl candidate (record, do not ossify):** *refetch-and-reproject
  via `barebuild-data-refresh`*. The author never used a bind/value-write at all —
  every write path ended in "re-read the server, let the read pipeline reproject."
  If independent participants land here too, the V1.1 lesson may be that
  `<barebuild-bind>` is unnecessary and only an *invalidation* primitive
  (`<barebuild-invalidate-on>` / a refresh event) is worth shipping.
- **Sketch priors that look SHAKY against this impl:** `<barebuild-bind>` (write a
  response value into a prop) — not exercised; the author discarded the response and
  re-read instead. Needs independent confirmation before concluding bind is dead weight.
- **Sketch priors that this impl SUPPORTED:** the `x-form-submit` submit hook (Q1);
  the no-selectors / values-not-places / refresh-by-event spine (held cleanly).
- **New friction the sketch did not anticipate:** (a) `x-form` blank-default shadowing
  (`without-blanks`); (b) empty-204 body parsing on DELETE.

---

## Load-bearing priors check

From [`write-side-sketch.md` → "Decisions … carried forward"](write-side-sketch.md).
Recorded against Participant A only.

- [x] **No CSS selectors anywhere** — _Partially._ The *coordination* primitives used
      no selectors (refresh was a `dispatchEvent` AT the broker; navigation a
      `dispatchEvent` AT the router). But the demo glue **did** use
      `document.querySelector` by id to *find* the broker/router/modal to dispatch at.
      The wire (event) is selector-free; locating the dispatch target was not. A real
      `<barebuild-invalidate-on>` (containment/name) would remove that querySelector.
- [x] **Values, not places** — _Held._ No app-level place-state. The board/detail/
      settings views stayed `DOM = f(broker value, filters)`; writes re-read the server
      rather than mutating a cached copy.
- [x] **`.state` is a plain JS object** — _Not stressed._ The write side never read
      `.state` (it re-fetched instead), so the CLJS-keyword-vs-string-key boundary was
      never hit on the write path. The read side already treats it as a JS object.
- [x] **`path` / `values-path` are literal EDN vectors** — _N/A._ No `path`/`values-path`
      machinery was used; the author read `event.detail.values` directly and discarded
      responses. No demand for a string DSL surfaced.
- [x] **Exact-pathname URL match for invalidation** — _Not needed._ The writer knew its
      reader by id and refreshed it directly, so no URL-matching invalidation was
      exercised. (The implicit route-activation refetch on delete *did* rely on exact
      pathname `/tasks`, and that sufficed.)

---

## Hiccup-renderer demand

Not one of the five questions, but the gate for promoting the deferred `bb.render`
work into V1.1 ([`BAREBUILD-V1-PLAN.md` → Deferred #1](BAREBUILD-V1-PLAN.md)).

- **No demand.** Because every write re-read the server and the *existing* render
  functions (`render-board!`, `render-detail!`) reprojected, the author never hand-built
  result DOM on the write side — so no Hiccup renderer was wished for. This is a
  refetch-strategy artifact: an *optimistic* implementation (splice the returned row)
  would have hand-built DOM and is where the renderer demand would show up. Watch for it
  in any participant who goes optimistic instead of refetching.

---

## Decision: defer `<barebuild-bind>` (additively — it is an extension point, not a fork)

**Decision (2026-06-02):** the next write-side step (a stable subset and/or an
explicitly-unstable alpha — see "V1.1 recommendation") ships **`<barebuild-action>` +
`<barebuild-invalidate-on>` only. `<barebuild-bind>` is deferred — *not killed*.** This
records *why* the deferral is safe and *what* it would cost to add bind later, so a future
reader doesn't re-litigate it from scratch.

### Why bind is the odd one out
`<barebuild-action>` (submit→fetch) and `<barebuild-invalidate-on>` (refresh) are pure
*coordination* — they assume nothing about a consumer's internals. **bind reaches *into*
the consumer:** it writes `parentNode[prop]` and relies on that property's setter to
*render* the value. That only works for a component that owns a self-rendering data
property — which most BareDOM components, by design, do **not** have:
- **Already bind-capable:** `x-command-palette` exposes `:items` (an array prop that
  renders the list). bind works for it **today**. Existence proof that the pattern is
  philosophy-compatible — it is simply not universal.
- **Composition containers:** `x-table` (rows are composed children, no `rows`/`items`
  data prop), forms (`fill-form!` fills named controls by hand). No sink for bind.
- **Styled primitives:** `x-badge`, `x-icon`, `x-button` — nothing to bind.

So "use bind everywhere" is gated on a **projection** (data→DOM), which is the deferred
Hiccup renderer ([Deferred #1](BAREBUILD-V1-PLAN.md)). bind is the easy half (transport a
value into a prop); the projection is the hard, philosophically-loaded half. bind without
a projection just moves a value into a prop that *still* needs hand-written rendering.

### The legibility cost bind introduces (and hand-wiring doesn't)
bind's `parentNode[prop]` write is an **invisible contract**: nothing in the markup tells a
dev that `x-command-palette` accepts `items` but `x-table` accepts nothing, and a wrong
target **fails silently** (the exact failure class the project rails against). The
hand-wired alternative — `(set! (.-items el) data)` — has *no* such problem: the property
is at the call site, and a missing one fails locally and immediately. **bind's value
(declarative) and bind's cost (a hidden, silently-failing contract) are the same coin.**
If bind ships, it must ship *with* a legibility layer: a data-driven **`:bindable` marker**
on `property-api` → propagated to the CEM and a "Bindable property" column in
`docs/components.md` → plus a **runtime guard** in bind that logs loudly when its parent is
not a marked target (with the same upgrade-timing `queueMicrotask` deferral the router uses).

### Forward-compat: adding bind later breaks nothing
Confirmed against shipped code. Introducing bind later is **fully additive**:
- A **new element** (`<barebuild-bind>`), the **`:bindable` marker** (metadata only), and
  any **new data-input props** (property-only, not attribute-reflected) — all additive.
- **Substrate gap:** bind matches its source broker by name, but V1's `barebuild-data`
  ships **`observed-attributes #js [attr-src]`** (no `name`) and a **`barebuild-data-state`
  detail of `{:state}` only** (no `:name`). So bind also needs the read side to gain a
  `name` attribute + `:name` in the event detail. **Both additions are non-breaking:**
  existing `<barebuild-data src=…>` and existing `.state` listeners are unaffected; the
  generated event-detail type merely *widens* `{state}`→`{state, name}` (a superset, still
  compiles). Caveat: regenerate the CEM + adapter types (the standing CEM-drift rule).
- **The expensive-to-retrofit foundation is already shipped and bind-ready:** the spine
  (broker holds `.state` by reference = values-not-places; the event-driven read pipeline;
  `.state` as a plain JS object) is V1-stable and bind rides it *without change*. Deferring
  bind forecloses nothing.
- **The only standing constraint** is one already owed to the read side: do not change the
  `barebuild-data-state` event shape incompatibly. Deferring bind adds no new lock-in.

### Do NOT pre-ship the `name` substrate now
Tempting to add `barebuild-data`'s `name` in V1 "to be bind-ready." **No** — because adding
it later is non-breaking, waiting costs nothing, and a `name` attribute that does nothing
until bind exists is exactly the speculative paper-by-gravity surface this whole exercise
resists. Add `name` *with* bind, when something consumes it.

### If/when bind does ship — the tiered path (cheap → loaded)
1. **Forms / single-record views:** an `x-form` `values` property (name→value fill, no
   templating) — the cheapest, safest first bind target. Promotes the demo's hand-written
   `fill-form!` into a property.
2. **Fixed-shape lists:** copy the `x-command-palette` `:items` pattern per component
   willing to commit to a documented row/item shape.
3. **Arbitrary structure:** needs the Hiccup renderer — the genuinely deferred call.

---

## V1.1 recommendation

_Provisional — N=1, biased. Do not act until the gate is met._

- [ ] Ship `<barebuild-action>` / `<barebuild-bind>` / `<barebuild-invalidate-on>` as sketched.
- [ ] Ship modified shapes.
- [x] **Defer further — collect ≥2 independent, prior-blind implementations.** The
      reference impl converged on *refetch-and-reproject + refresh-by-event* and never
      used `<barebuild-bind>`, which is a strong hint but **cannot** be acted on from a
      single biased data point. Next action: recruit 2–4 CLJS developers who have **not**
      seen the sketch, reset the seams to stubs on a throwaway branch, and collect.
- [x] **`<barebuild-bind>` deferred (additively).** Ship `action` + `invalidate-on`
      first (stable subset and/or unstable alpha); add bind later as a non-breaking
      extension. Full rationale + forward-compat proof in "Decision: defer
      `<barebuild-bind>`" above.
- [ ] **Promote the Hiccup renderer into V1.1?** **No (provisional)** — zero demand from a
      refetch-style impl, and it is the gate for bind's general case. Re-evaluate against any
      optimistic-update participant.

---

## Raw artifacts

Verbatim copies of the reference implementation (primary evidence). The wired demo
lives on branch `feat/barebuild-write-side-telemetry`.

- **`demo-app/src/demo_app/write_side.cljs`** — the five wired seams + `without-blanks`,
  `refresh-data!`, `navigate!`, `task-id-from-url`.
- **`demo-app/src/demo_app/api.cljs`** — the method-aware `request` primitive with the
  204-safe `parse-body`.
- **`demo-app/src/demo_app/wiring.cljs`** — added `ev-navigate`, `tag-router`, and the
  promoted broker ids `id-detail-data` / `id-settings-data`.

See the files on that branch rather than duplicating them here; they are the contract.
