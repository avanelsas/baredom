# Release Management — BareDOM and BareBuild

This is the operational reference for releasing BareDOM and BareBuild. Architectural rationale lives in [`BAREBUILD-V1-PLAN.md`](BAREBUILD-V1-PLAN.md); this document is the how.

The two products release on **fully independent cadences** with **no auto-coupling**. The mechanism: a single git trunk, prefixed tags, conventional-commit scopes, and path-filtered CI.

## BareDOM releases (existing, unchanged)

- **Version:** continues current 3.x SemVer in root `package.json`.
- **Tag format:** `v3.4.0`.
- **Task:** existing `bb release :patch|:minor|:major`, untouched. **Does not touch any file under `barebuild/`.**
- **npm publish:** `npm publish` ships `@vanelsas/baredom` containing `dist/`, `docs/`, and BareDOM source — **explicitly excludes `barebuild/`** via the `"files":` array in `package.json` (or `.npmignore`). The `barebuild-*` orchestration components ARE included (they live in `src/baredom/components/`); they are tree-shakable ESM modules so consumers who don't import them pay nothing.
- **CI:** `release-baredom.yml` triggers on `v*` tags.
- **What's released:** the BareDOM npm package. Includes all `x-*` UI components plus the `barebuild-*` orchestration components. No CLI, no templates.

## BareBuild releases (new)

- **Version:** **starts at 0.1.0** SemVer in `barebuild/package.json`. Pre-1.0 lets us evolve the CLI surface without major bumps; reaches 1.0 once stable.
- **Tag format:** `barebuild-v0.1.0` (dash-separated prefix).
- **Task:** new `barebuild/bb.edn` `release :patch|:minor|:major`. Owns the entire BareBuild release lifecycle:
  1. Bumps `barebuild/package.json` version.
  2. Bumps the pinned `@vanelsas/baredom` version in `barebuild/templates/app/{package.json,deps.edn}` — **explicit human act per BareBuild release**, never an automatic side-effect of a BareDOM release.
  3. Opens a **release-prep PR** with the bumps (matches BareDOM's existing PR-based release flow per the `release_pr_workflow` memory — never push directly to `main`).
  4. Tag is created on the merge commit after PR lands; tag push triggers `release-barebuild.yml`.
- **No npm publish.** BareBuild is not an npm package. The CLI is installed via bbin: `bbin install io.github.vanelsas/baredom@barebuild-v0.1.0`.
- **CI:** `release-barebuild.yml` triggers on `barebuild-v*` tags. Builds the CLI distributable, attaches it to the GitHub release, generates release notes from `barebuild/CHANGELOG.md`.
- **What's released:** a git tag plus a GitHub release page. The CLI installs from the tagged ref. The templates pin a specific `@vanelsas/baredom` version baked at release time.

## Git workflow

- **Single trunk: `main`.** No parallel trunks. Drift kills these patterns.
- **Conventional-commit scopes:**
  - `feat(baredom): ...` / `fix(baredom): ...` / `docs(baredom): ...` for BareDOM-only work.
  - `feat(barebuild): ...` / `fix(barebuild): ...` / `docs(barebuild): ...` for BareBuild-only work.
  - `feat(both): ...` for changes that span the boundary (rare; usually means an orchestration-component-plus-template change — see "awkward case" below).
- **PR titles follow the same scope convention.**
- **Tag namespaces are non-overlapping:** `v*.*.*` for BareDOM, `barebuild-v*.*.*` for BareBuild. Git globs handle them cleanly.
- **Changelogs are separate:** root `CHANGELOG.md` for BareDOM (existing); `barebuild/CHANGELOG.md` for BareBuild (new).

## CI structure

| Workflow | Triggers on | Runs |
|---|---|---|
| `test-baredom.yml` (existing, lightly modified) | PR touching `src/baredom/`, `test/`, `adapters/`, `shadow-cljs.edn`, root `package.json` | `npm test`, `clj-kondo`, `npx shadow-cljs release lib`, adapter builds |
| `test-barebuild.yml` (new) | PR touching `barebuild/` | `cd barebuild && bb test` |
| `boundary-check.yml` (new) | **All PRs** | (a) `bb scripts/check_barebuild_boundary.bb` (relative-path grep) + (b) `bb scripts/check_du_discipline.bb` + (c) `bb scripts/test_codegen_filter.bb` (adapter codegen iterates the positive `adapter-eligible` set in `src/baredom/registry.cljs`; orchestration components are simply absent from the set) + (d) path-anchored packed-file check: `npm pack --dry-run --json \| jq -r '.[].files[].path' \| grep -E '^barebuild/'` returns empty (a naive `grep -i barebuild` false-positives on `src/baredom/components/barebuild-*` which *should* ship) + (e) deletion smoke: `rm -rf barebuild/ && npm test && npx shadow-cljs release lib` on a throwaway worktree + (f) `grep -r 'querySelector' src/baredom/components/barebuild-*/` returns empty (the no-selectors rule from plan Decision #13) |
| `release-baredom.yml` (existing) | `v*.*.*` tag | `npm publish @vanelsas/baredom`, GitHub release |
| `release-barebuild.yml` (new) | `barebuild-v*.*.*` tag | Build CLI distributable, attach to GitHub release |

**Path-filter discipline:** a PR that touches *only* `barebuild/` doesn't need to run the full BareDOM browser-test suite — but the boundary check still runs to catch accidental cross-boundary references. Same in reverse.

## npm packaging discipline

Root `package.json` `"files":` array **must whitelist** what ships (`dist/`, `src/baredom/`, `docs/`, …). The `barebuild/` directory must not be listed and must not be matched by any pattern. Verify with a path-anchored check in `release-baredom.yml`:

```bash
# Whitelist sanity: no files: entry may target barebuild/
jq -e '.files | all(. as $f | ($f != "barebuild/") and (($f | startswith("barebuild/")) | not))' package.json > /dev/null

# Packed-files sanity: walk what npm would actually pack and reject anything under barebuild/
npm pack --dry-run --json \
  | jq -r '.[].files[].path' \
  | grep -E '^barebuild/' \
  && { echo "ERROR: barebuild/ leaked into npm tarball"; exit 1; } || true
```

**Path-anchoring matters.** A naive `grep -i barebuild` would match `src/baredom/components/barebuild-router/barebuild_router.cljs` — which *should* ship — and fail the build on every release.

## Independence guarantees

| Question | Answer |
|---|---|
| Can BareDOM release without touching BareBuild? | **Yes, always.** Default case. `barebuild/` is invisible to BareDOM's build/test/publish pipeline. The root `bb release` does not read or write any file under `barebuild/`. |
| Can BareBuild release against an *unchanged* BareDOM? | **Yes.** Most BareBuild releases (CLI fixes, template updates, docs) don't need a BareDOM release. The template pin stays unchanged. |
| Can BareBuild release against a *newer* BareDOM that's already published? | **Yes.** `cd barebuild && bb release` prompts (or accepts a flag) for the BareDOM version to pin; bumps the template `package.json` and `deps.edn`; tags the BareBuild release. |
| Can BareDOM and BareBuild release at the same time? | **Yes, but as two separate `bb release` invocations** (`bb release` from root, then `cd barebuild && bb release`). No automatic coupling. |

## The awkward case: a BareBuild feature requires a new orchestration component

Since orchestration components live in `src/baredom/components/`, adding e.g. `<barebuild-form-wizard>` is a BareDOM change. The release sequence is:

1. **PR A** — `feat(baredom): add barebuild-form-wizard` — adds the component to `src/baredom/components/`, registers it, ships tests, regenerates **only** the adapters that wrap UI components (orchestration components are excluded from adapter codegen by virtue of *not appearing* in the positive `adapter-eligible` set in `src/baredom/registry.cljs` — see plan Decision #16; there is no negative `:adapter-skip` flag). Lands on `main`.
2. **BareDOM release** — `bb release :minor` cuts `v3.6.0`, publishes to npm.
3. **PR B** — `feat(barebuild): use form-wizard in app template` — updates `barebuild/templates/app/...` to use the new component; bumps the template's pinned `@vanelsas/baredom` to `^3.6.0`. Lands on `main`.
4. **BareBuild release** — `cd barebuild && bb release :minor` cuts `barebuild-v0.5.0`.

Two releases. Pre-extraction (same repo, same branch) this is two `bb release` invocations from the same checkout — about 5 minutes of human work. Post-extraction it would be cross-repo PRs — a real cost we accepted by choosing in-repo for V1, but **deferred, not paid yet**.

## Safety guardrail: no auto-coupling

`bb release` (BareDOM) does **not** automatically trigger or imply a BareBuild release, and vice versa. The two releases are intentional, separate human acts. This avoids "I bumped BareDOM but forgot to update BareBuild's template pin" — the next BareBuild release will need to make that decision explicitly, in a PR with a reviewer.

## Versioning policy summary

| Product | Scheme | Starts at | Tag format | Notes |
|---|---|---|---|---|
| BareDOM | SemVer | 3.x.x (current) | `v3.4.0` | npm package `@vanelsas/baredom`. Existing pipeline unchanged. |
| BareBuild | SemVer | **0.1.0** | `barebuild-v0.1.0` | Bbin-installable CLI. Pre-1.0 = "API may break"; reaches 1.0 once CLI surface stabilises. |
