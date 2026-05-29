# Phase 0 — Validation Gate Responses

Gist URL: <fill in once posted>

Posts:
- Clojurians #clojurescript: <permalink>
- Clojurians #shadow-cljs: <permalink>
- ClojureVerse: <topic URL>
- /r/Clojure: <post URL>

Period: 2026-05-28 → <pending>

Gate criteria (from `BAREBUILD-V1-PLAN.md` Phase 0):
- **Verbal:** ≥3 of 5 responses say "I'd try it" *with a named use case*.
- **Committed action:** ≥3 distinct respondents take one concrete step — and **at least one must name a real project** (work, side, or OSS) where they'd try it.

---

## Respondent 1 — <handle TBD> (<channel TBD>)

Logged: 2026-05-28

**Verbatim:**

> I've checked your gist. It looks good. Answering to your questions:
>
> If you write CLJS frontends today, would you try this for a read-heavy app?
> I would try it for a small personal project or an MVP. I think frameworks like this should be either battle-tested or come with some benchmarks comparing with some popular alternatives.
>
> What's missing that would stop you?
> I think some real-life examples: how the pagination is handled, what if I need an infinite scrolling on my data? Will it make the code significantly more complex?
>
> What's wrong with the shape above?
> It's a good minimal example, but I'd like to see how it's applicable to real use cases.

**Verbal signal:** soft yes — *"I would try it for a small personal project or an MVP."* After follow-up #1: narrowed to *"potentially, yes"* for admin apps at scale.

**Named use case:** none — *"small personal project or MVP"* (R1.0) and *"admin app, if used by a lot of users"* (R1.1) are both categories, not specific projects on their plate. Named-use-case floor designed to filter this exact answer pattern.

**Committed action:** none.

**Named real project:** none.

**Counts toward gate?** Not yet. **One more follow-up max** (final probe — see below); if it doesn't name a project, close out as engaged-but-not-buy and do not count toward the ≥3 floor.

### Follow-up exchange log

**Follow-up #1 (sent):**

> Do you imagine that a scaffold tool like the one described could work for you to build an admin app?

**Response #1:**

> Potentially, yes. If the admin app were used by a lot of users, so separating API and frontend would be justified.

**Read:** narrowed the hypothetical from MVP/personal-project to admin-app-at-scale; still no specific project named. The *"separating API and frontend would be justified"* phrasing is a strategic insight worth logging separately (see Substantive feedback #5).

**Follow-up #2 (pending — final probe):**

> Last question: is there a specific admin app you'd reach for this on (internal tool at work, an OSS project you maintain)? Even a *"no, not right now"* is useful.

If R1.2 names a real project → flips to gate-clearing. If R1.2 declines or generalises again → close as engaged-but-not-buy.

**Substantive feedback (independent of gate signal):**

1. **Battle-tested / benchmarks.** Wants concrete numbers vs. Reitit + re-frame + Helix before committing. Realistic claim is bundle-size + first-paint win; parity in steady-state.
2. **Pagination gap.** `<barebuild-data>` is implicitly all-at-once. V1 has no pagination primitive; intended pattern is reassigning `src` to a `nextPageUrl` and accumulating in user code. Whether that's acceptable is what Phase 4 is designed to observe.
3. **Infinite scroll gap.** Same gap as pagination + an `IntersectionObserver` trigger. No V1 primitive; hand-wire against `<barebuild-data>` is the intended answer.
4. **Real-life examples.** Wants the Phase 4 demo earlier — or at least one more example in the gist that goes beyond a flat list.
5. **SPA-architecture cost vs. payoff (surfaced in follow-up #1).** *"Separating API and frontend would be justified"* implies SPA-arch has a wiring/deploy/contract cost that only pays back at scale. Strategic signal: BareBuild's sweet spot is mid-to-large apps where SPA-arch already pays off, not the *"small personal project or MVP"* originally named. Tension inside the respondent's own answer (try it on small, commit only on large) is classic polite hedging — log, don't try to resolve. Implication for post-V1 positioning: the README's framing should not lead with *"great for MVPs"* — it should lead with *"great for apps where you've already decided on a separated frontend."*

---

## Respondent 2 — <handle TBD> (<channel TBD>)

Logged: 2026-05-28

**Verbatim:**

> It's clear to me, but it's also very... limited. It shows a rudimentary scenario that by itself is of little interest, I think — "read and display" has been handled so many times.
>
> Like showing a car enthusiast the steering wheel of a new car you're designing and asking whether it makes sense.
>
> Not that it's not worth doing, but it's probably not close to the top of the priorities.. and I cannot answer the question because I still don't know what the idea is.
>
> How is it different from whatever things already exist in that domain, what is unique about it, what's the value proposition, what problems does it solve? All kinda same but also different questions.
>
> A server dev in need of a UI could easily create an app with htmx just as well. Or Replicant, or even Selmer. Hell, if they need a simple UI just to show some data, they'd probably ask an LLM and use whatever gets spit out, because simple CRUDs have been solved for decades now — and that's already more than "a simple UI to show data".
>
> "you generate a production-ready interface in seconds" — if it's from 0 to 100 then it's not going to happen. Even with a super-AI that can write correct code instantaneously. Because a dev still has to come up with what's needed. Even if they already have all the possible representative samples of JSON data that mentions all the entities and all their relationships (somehow, without a schema), nothing answers the question "how should it behave".
>
> A parent entity has children entities. Should children be editable only from within the parent's UI, or only separately, or both? A particular entity — should it get its own page or a panel? What should the transitions be in the UI (in the FSM sense, not animations)? Ohh, what about authorization?
>
> And if it's not from 0 to 100, then I don't know what's meant by "generate a production-ready interface".

**Verbal signal:** **no** — explicit *"I cannot answer the question because I still don't know what the idea is."* This is not a soft no; it is a diagnostic no. The respondent is engaged enough to write 200+ words explaining *why* they cannot evaluate, which is more useful than 200 soft yeses.

**Named use case:** N/A (could not evaluate).

**Committed action:** none.

**Named real project:** none.

**Counts toward gate?** Does not count toward the verbal-pass floor. **Counts as the most actionable diagnostic so far** — it tells us the gist is mis-positioned, not that the product is unwanted.

**Diagnosis:** the gist leads with *mechanics* (router + route + data + 10 lines of CLJS) when it needs to lead with a *thesis* (what BareBuild believes, what it's competing against, what the substrate-vs-runtime claim is). R2 used the analogy *"showing a car enthusiast the steering wheel of a new car you're designing"* — they were shown a part, not the idea.

**Substantive feedback (high-value):**

1. **The gist has no thesis paragraph.** Until it does, every reader will respond like R2 — engaged but unable to evaluate.
2. **Competitors must be named explicitly.** R2 named four — htmx, Replicant, Selmer, LLM-generated React. The gist must position against each. Refusing to compare publicly reads as either ignorance or avoidance.
3. **The *"0 to 100"* critique is sharp and correct.** No scaffold solves the 90% that is design — parent/child semantics, page vs. panel, FSM transitions, authorization. The pitch must not over-promise scaffolding velocity; it must promise *substrate longevity + bundle discipline*. R2 reacted to a strawman they paraphrased as *"production-ready interface in seconds"* — the gist and any spoken pitch must not say or imply this.
4. **Read-and-display is a solved problem.** Showing only the read pattern (which is all V1 has) reinforces the impression that BareBuild is about a solved problem. The thesis paragraph must frame the read example as *evidence of the substrate claim*, not as the value proposition itself.

**Recommended reply (not yet sent):**

1. Thank them — this is the most useful feedback so far.
2. Acknowledge the diagnosis: gist was thesis-light by mistake; here is the thesis.
3. Share the revised positioning (thesis paragraph + comparisons table, drafted separately).
4. Address the *"0 to 100"* critique honestly: BareBuild does not claim to design your app; it claims to give you a substrate that does not fight you while you do.
5. Ask: *"Given that framing, does your read change — or is the architecture itself the disagreement?"* — converts the conversation from "I don't know what this is" to a real architectural critique either way.

### Action items triggered by R2

- **Revise the gist before sending to R3+.** Add thesis paragraph at top + *"Compared to"* section + *"What BareBuild doesn't do"* section. Drafted text delivered separately.
- **Recount the gate.** R1 = soft hedge, R2 = diagnostic no. Effective N=0 toward the ≥3 verbal floor. The pre-revision gist is producing low-signal responses; the post-revision gist starts fresh on respondents 3+.

---

## Themes to Watch in Responses 2–5

Track recurrence before reacting. Single-voice concerns get addressed in the reply; multi-voice concerns trigger plan changes.

| Theme | Raised by | Decision rule |
|---|---|---|
| Pagination story missing | R1 | If ≥2 more raise it → add one-sentence pagination note to `docs/barebuild-data.md` (1:1 src fetch; pagination via src reassignment + user-code accumulation). If only R1 → reply-only, no plan change. |
| Infinite scroll story missing | R1 | Same rule as pagination — same gap, different shape. |
| Benchmarks vs. Reitit/re-frame/Helix | R1 | If ≥2 more raise it → publish bundle-size + first-paint numbers before Phase 1a closes. If only R1 → still publish as part of Phase 4 demo; not a gate. |
| "Need more real-life examples" | R1 | If ≥3 raise it → add one richer example to the gist (e.g., a detail route `/users/:id`). If <3 → Phase 4 demo answers this. |
| SPA-arch cost only pays at scale | R1 | If ≥2 more frame it this way → revise README/landing-page positioning post-V1 to lead with *"for apps that have already chosen a separated frontend"*, drop any *"great for MVPs"* framing. If only R1 → log as one strategic voice; revisit at post-V1 positioning. |
| Gist mis-positioned (mechanics, no thesis) | R2 | **Already actionable on N=1** — R2's diagnosis is precise and the fix is cheap. Revise gist before sending to R3+. If R3 still says *"I don't get it"* after the revision → the *idea* is the problem, not the gist. |
| *"How is it different from htmx / Replicant / Selmer / LLM"* | R2 | **Already actionable on N=1** — the comparison section belongs in the gist regardless of how many more respondents ask. CLJS audience knows these alternatives; not naming them reads as avoidance. |
| *"0 to 100"* over-promise | R2 | Audit the gist and any spoken pitch for phrases that imply *"production app in seconds"*. The honest promise is substrate longevity + bundle discipline, not codegen velocity. |
| Read-and-display is a solved problem | R2 | If ≥2 more echo this → the V1 canonical example must be reframed as *evidence of the substrate claim*, not as the value proposition. Currently the example is doing both jobs and failing at the second. |
| *"Haven't we tried this with Polymer?"* | (anticipated) | The `<iron-ajax>` / `<app-route>` precedent is real and the gist now names it explicitly under *Compared to → Polymer / Lit*. If a respondent raises it → engage with the *"abandonment was ergonomic, not architectural"* claim; ask whether they think Polymer's failure mode was the declarative-element pattern itself or the TypeScript-era build chain. Their answer is a strong signal. If multiple respondents raise it and don't accept the distinction → the bet is weaker than the gist claims. |
| Fulcro overlap | (anticipated) | The most direct CLJS competitor by audience. Gist now names it under *Compared to → Fulcro* with the architectural-opposition framing (Fulcro owns *more* state; BareBuild owns *less*). If a respondent says *"I already use Fulcro"* → ask whether the architectural difference matters for their app; their answer reveals whether the *server-state-IS-the-state* pitch lands. |

---

## Decision Block

Do not fill until ≥5 responses collected or the 4-week deadline hits.

Copy the **Gate Decision** template from `BAREBUILD-V1-PLAN.md` (Artefact 4 in the Phase 0 recipe) into this section at that point.
