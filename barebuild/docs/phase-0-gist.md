<!-- Canonical source for the Phase 0 validation gist.
     Sync the hosted gist (gist.github.com) against this file when revising.
     Hosted URL: <fill in after first publish> -->

# BareBuild V1 — read-side scaffold for CLJS (sketch, not shipped)

## Why this exists

Frontend frameworks have grown into runtimes. React, Vue, Svelte, re-frame
— they install a layer that owns your data, your re-render decisions, and
your upgrade schedule. **The runtime is the cost.**

BareBuild is the bet that you don't need that layer. The browser already
has a runtime: events, properties, fetch, DOM. **BareDOM** (the substrate)
ships 100+ stateless web components that use it directly — zero frontend
runtime dependencies. **BareBuild** (this gist) adds three orchestration
elements — router, route, fetcher — that compose with the same idiom.
No virtual DOM. No global store. No framework runtime in your bundle.
Your code is data flowing through the platform.

The example below is a *small* one — deliberately. V1 ships only the read
side; write-side coordination lands in V1.1, designed from real V1 user
code rather than preempted. The point isn't *"look how easy reads are"*
(reads are solved). The point is *what substrate you're standing on
while you build the rest of the app.*

## What a BareBuild V1 app looks like

```html
<barebuild-router>
  <barebuild-route path="/users">
    <barebuild-data></barebuild-data>   <!-- dormant until its route activates -->
    <x-table></x-table>
  </barebuild-route>
</barebuild-router>
```

**V1 — what ships.** You wire the glue by hand, on purpose:

```clojure
;; V1 exposes this glue intentionally — we want to see what shape you'd
;; reach for before we design any declarative wiring element in V1.1.
(defonce ^js users-route (.querySelector js/document "barebuild-route[path='/users']"))
(defonce ^js users-data  (.querySelector users-route "barebuild-data"))
(defonce ^js users-table (.querySelector users-route "x-table"))

;; WHEN to read is explicit: set src on activation, so you read fresh each visit
;; and only the route you're looking at fetches.
(.addEventListener users-route "barebuild-route-change"
  (fn [_] (set! (.-src users-data) "/api/users")))

;; Read the value from e.detail.state; e.target is for identity (which broker) only.
(.addEventListener users-route "barebuild-data-state"
  (fn [^js e]
    (let [{:keys [phase data]} (.. e -detail -state)]
      (when (= :loaded phase)
        (set! (.-items users-table) data)))))
```

**The direction (V1.1).** We expect this hand-wiring to collapse into a small
declarative element so the `addEventListener` moves into markup at the same
composition boundary — but **we're deliberately not sketching its shape here.**
V1 ships the hand-wired version precisely so we can see what shape *you'd* reach
for, and then design the V1.1 element from what people actually built rather than
from a sketch that anchors everyone to our first guess. The friction above is the
question we're asking, not a rough edge we're apologising for.

`(.-state ...)` is unavoidable interop — web components expose state via
JS properties. The *value* it returns is a ClojureScript persistent map,
which is why `{:keys [phase data]}` destructures it directly. No
`js->clj`, no JS-shaped objects on the read path.

## Where does the data come from?

BareBuild is a frontend scaffold, not a server framework. `<barebuild-data
src="…">` fetches against whatever serves your origin — you bring your own
backend (Babashka, JVM, Node, anything that returns JSON). For local dev,
`bb dev` auto-routes `/api/<name>` to `mocks/<name>.json`, so the example
above runs end-to-end without a separate backend. Delete `mocks/` when
you wire a real one.

## Not in V1

- **Write side** — `<barebuild-action>`, `<barebuild-bind>`,
  `<barebuild-invalidate-on>`. Sketches exist; contracts are designed-by-use
  from V1 user feedback, not preempted.
- **`barebuild generate`** CLI subcommands.
- **SSR**, EDN payloads, nested routes, optimistic UI. All deferred and
  named — see the full plan if curious.

## Compared to

**vs. re-frame + Reitit + Helix** — no framework runtime, no global store.
Trade: a few lines of wiring per page in V1 (declarative `<barebuild-bind>`
reduces this in V1.1).

**vs. Fulcro** — Fulcro is the closest CLJS-native full-stack framework
and bets the *opposite* direction: it owns *more* client state
(normalised cache, EQL queries, integrated Pathom backend) to give fast,
predictable UI updates. BareBuild bets the other way — no cache, no
query language, server state IS the state. Choose Fulcro if you want a
mature framework with RAD, EQL, and Pathom on top; choose BareBuild if
you want web-standard components and no framework runtime in your bundle.

**vs. htmx** — htmx ships HTML fragments; BareBuild ships JSON. Wrong fit
if your backend is API-shaped. htmx also has no component library.

**vs. Replicant** — Replicant is a renderer, not a scaffold. They compose:
Replicant can render dynamic content inside a BareBuild route. BareBuild
is the bigger thing.

**vs. Selmer / pure SSR** — different model. Choose SSR if full-page
reloads are acceptable and interactivity is light.

**vs. Polymer / Lit** — Polymer (Google, deprecated) had `<iron-ajax>`
and `<app-route>` — almost exactly BareBuild's shape. Google abandoned
the pattern in favour of Lit's functional model. BareBuild's bet: the
abandonment was about Polymer-era ergonomics (TypeScript story, build
chain, upgrade pain), not the declarative-element architecture — and
revives the pattern with CLJS as host and a modern toolchain (shadow-cljs,
Babashka). Lit is the active inheritor but writes components as functions,
not as HTML wiring; different programming model.

**vs. LLM-generated React** — the LLM emits faster code, but it still
ships React's runtime and deprecation cycle. BareBuild is the substrate;
codegen velocity is a separate concern.

## What BareBuild doesn't do

It does not design your app. No scaffold solves the 90% that is design —
parent/child editability, page vs. panel, FSM transitions, authorization.
BareBuild gives you a substrate that doesn't fight you while you do that
work, a 100-component visual library so you don't redesign buttons, and
a contract that doesn't deprecate.

The promise is **longevity and bundle discipline**, not *"production app
in seconds"*. If you want codegen velocity, an LLM is faster. If you want
the code an LLM emits to still run in five years, the substrate it emits
*onto* matters more than the codegen itself.

## The question

If you write CLJS frontends today (Reitit + re-frame + Helix, or anything
else): would you **build a read-heavy app on this substrate** — no framework
runtime in the bundle, server state as the source of truth, a 100-component
library, a contract that doesn't deprecate? That's the question — the
substrate, not whether the hand-wiring above is as terse as it could be (V1.1
addresses terseness; V1 is asking whether the foundation is one you'd stand on).
What's missing that would stop you? What's wrong with the shape above?

I'm collecting responses verbatim — comment here, DM me, or open an issue.
If ≥3 of 5 readers say *"I'd try it on [project X]"* (a named project, not a
reflexive "looks neat"), V1 ships. Otherwise I'll write a BareDOM cookbook for
re-frame/Reitit users instead and revisit.
