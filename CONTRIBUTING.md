# Contributing to BareDOM

Thanks for your interest in contributing! BareDOM is a library of native Web Components authored in ClojureScript and compiled to standalone ES modules. Contributions of all sizes are welcome — bug reports, new components, docs fixes, demos, and ideas.

## Workflow

Two steps:

1. **Open an issue first.** Describe the bug, idea, or component you'd like to work on. This lets me discuss the approach with you up front, avoid duplicated effort, and make sure the change fits the project's design principles.
2. **Follow up with a Pull Request.** Reference the issue number in the PR description, and try to keep each PR focused on one thing. Smaller PRs get reviewed faster.

## Please don't force-push on open PRs

Once your PR is open, please **avoid `git push --force` (or `--force-with-lease`) on the PR branch**. Add new commits on top instead.

Why it matters:

- Reviewers can see exactly what changed between review rounds. GitHub's "changes since last review" view only works when the commit history is preserved.
- Incremental commits make it much easier for others to jump in, suggest changes, or pick up where you left off.

Don't worry about a "messy" history — squashing happens at merge time, so your PR will land as a clean commit on `main` regardless of how many intermediate commits you pushed.

## Development

- See [`README.md`](./README.md) for install and build commands (`npx shadow-cljs watch app`, `npx shadow-cljs watch test`, `npm run build`).
- See [`CLAUDE.md`](./CLAUDE.md) for the architecture, the three-layer component pattern (`model.cljs` / `<name>.cljs` / `exports/<name>.cljs`), the stateless rendering rule, Closure Advanced compilation safety, theming via `x-theme`, and the checklist for adding a new component.
- Tests run in a real browser (headless Chrome via shadow-cljs). Please add tests for new behaviour where it makes sense.

## Releases

Releases go through a PR, never a direct push to `main`. The version-bump checklist lives in `CLAUDE.md` under "Releasing a New Version" — all four locations (`package.json`, `build.clj`, `deps.edn`, `README.md`) must match, and a `CHANGELOG.md` entry is required.

## Code of conduct

Be kind, be constructive, and assume good intent. I want BareDOM to be a friendly place to learn and build.
