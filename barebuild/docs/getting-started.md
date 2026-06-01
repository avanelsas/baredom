# Getting started with BareBuild

> Scaffold a runnable read-side app in one command, then wire it by hand.

BareBuild is a thin **read-side** scaffold over the [BareDOM](../../README.md)
web-component library. V1 ships three orchestration custom elements —
[`<barebuild-router>`](../../docs/barebuild-router.md),
[`<barebuild-route>`](../../docs/barebuild-route.md), and
[`<barebuild-data>`](../../docs/barebuild-data.md) — plus a `barebuild new`
generator that stamps a working shadow-cljs app consuming them. There is no
client store, no virtual DOM, and no framework runtime: the server holds the
truth and the DOM is a projection of it. For the philosophy and the full V1
contract, read [`read-side.md`](read-side.md) after this page.

## Prerequisites

- **[Babashka](https://babashka.org/)** (`bb`) — runs the generator.
- **Node.js** (with `npm`) — installs `@vanelsas/baredom` and runs shadow-cljs.
- A JVM — shadow-cljs needs one (Java 11+).

The generated app pins **`@vanelsas/baredom` ≥ 3.4.0** — the release that
introduces the `barebuild-*` orchestration elements. (Earlier versions ship the
`x-*` components only.)

## Scaffold an app

Run the generator from the `barebuild/` directory of this repository:

```sh
cd barebuild
bb new my-app            # creates ./my-app
bb new my-app ~/code     # or pass a parent dir → ~/code/my-app
```

The app name must be usable as **both** an npm package name and a ClojureScript
namespace segment: hyphen-separated runs of lowercase letters and digits,
starting with a letter (`my-app`, `dash2`). No leading, trailing, or doubled
hyphen. The hyphen name is kept in file contents (the `package.json` name, the
`ns` form); the source directory is munged to the CLJS path form
(`my-app` → `src/my_app/`).

The generator refuses to overwrite an existing target directory.

## What gets generated

```
my-app/
├── bb.edn                  # `bb dev` / `bb build` convenience tasks
├── package.json            # name=my-app, @vanelsas/baredom ^3.4.0, shadow-cljs
├── shadow-cljs.edn         # :browser target → public/js, dev-http on :8000
├── .gitignore              # node_modules, .shadow-cljs, public/js
├── README.md               # run instructions for the generated app
├── public/
│   ├── index.html          # the UI as markup: router + two routes + data + x-table
│   └── api/
│       └── users.json      # static stub rows, served at /api/users.json
└── src/
    └── my_app/
        └── core.cljs        # registers the components + the read-side wiring
```

## Run it

```sh
cd my-app
bb dev            # installs deps + starts shadow-cljs on http://localhost:8000
# equivalently: npm install && npm run dev
```

Open <http://localhost:8000>. You land on `/` (a welcome panel). Click
**Users**: the `/users` route activates, the broker fetches the stub
`/api/users.json`, and the rows render into an `<x-table>`. Navigation, the
back/forward buttons, and a refresh of `/users` all work — `shadow-cljs.edn`
enables a push-state fallback so a deep link or reload of a client route loads
the app instead of 404ing.

## How the read side is wired

The markup in `public/index.html` declares the structure; `src/my_app/core.cljs`
registers the components and connects them by hand. The wiring is three steps,
all visible as plain code at the **route** (the natural composition boundary):

1. **Register** each imported module's custom element (`init()`), *after* wiring
   the listeners — so a deep load straight to `/users` still delivers the initial
   `barebuild-route-change` the listener depends on. (The order of the `init()`
   calls themselves does not matter: the router defers its initial route-change
   push to a microtask, so every element is upgraded before any cross-component
   cascade fires.)
2. **When to read** — set the broker's `src` on `barebuild-route-change`, gated
   on the active path (`/users`). A read is an observation in time, so returning
   to the route re-reads. `barebuild-route-change` is pushed to *every* route on
   every resolution, which is why the handler checks the path.
3. **Render the value** — on `barebuild-data-state`, read
   `event.detail.state` — a plain JS object `{ phase, data, error, httpStatus }`
   with a **string** `phase` — and rebuild the `<x-table>` rows when
   `phase === "loaded"`.

`.state` is a JS object, not a ClojureScript map, on purpose: your app is
compiled with its own `cljs.core`, separate from the components' bundled one, so
a persistent map would be unreadable across that boundary. Treat `.state` as
read-only — it is a frozen value returned by reference. See
[`read-side.md`](read-side.md) for the full rationale and the three ways to
consume a broker.

> **Why hand-wire with `querySelector`?** Your app legitimately reaches into its
> own markup to grab handles. (The library's internal rule against `querySelector`
> is about *components* never finding their collaborators by selector — a
> different concern.) This explicit glue is the deliberate V1 starting point; the
> declarative wiring element (`<barebuild-bind>`) is V1.1, designed from what this
> hand-wiring teaches us.

## Point it at your backend

`public/api/users.json` is a static stub so the app is live on the first run.
For a real backend, change the `src` your `core.cljs` sets (currently
`/api/users.json`) to your endpoint (e.g. `/api/users`) and serve JSON there,
then delete the stub. `<barebuild-data>` issues a `GET` and parses the JSON
response — a `204`/empty body is treated as a successful load with no data, not
an error. That `GET`-and-parse is the whole contract.

## Build for production

```sh
bb build          # release build to public/js (npm install + shadow-cljs release app)
```

Deploy `public/` behind any static or application server. **Remember the SPA
fallback:** the dev server's push-state behaviour is dev-only, so configure your
production server to serve `index.html` for client routes that do not map to a
file (the same `/users`-refresh case `bb dev` already handles).

## Troubleshooting

- **`npm install` can't resolve `@vanelsas/baredom`** — you need a published
  version **≥ 3.4.0** (the one that ships `barebuild-*`). Until that release is
  on npm, build against a local pack of this repo (`npm pack` here, then
  `npm install <tarball>` in the generated app); the repo's
  `bb smoke-build` task exercises exactly that path.
- **`template dir not found`** — run `bb new` from inside the `barebuild/`
  directory; the generator resolves the template relative to its working dir.
- **The Users table is empty** — confirm the broker's `src` resolves to a real
  JSON array. With the dev server, `/api/users.json` is served from
  `public/api/`; a real backend must return a JSON body (or a `204` for "no
  rows", which renders an empty table, not an error).

## See also

- [`read-side.md`](read-side.md) — the canonical V1 read-side pattern and its four rules.
- Per-component references: [`barebuild-router`](../../docs/barebuild-router.md), [`barebuild-route`](../../docs/barebuild-route.md), [`barebuild-data`](../../docs/barebuild-data.md).
- [`BAREBUILD-V1-PLAN.md`](BAREBUILD-V1-PLAN.md) — the architectural rationale and what V1 deliberately defers.
