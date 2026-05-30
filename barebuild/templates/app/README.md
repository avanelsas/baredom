# APP_NAME

A [BareBuild](https://github.com/vanelsas/baredom) read-side app — native web
components, no framework runtime, no virtual DOM. Scaffolded by `barebuild new`.

## Run

```sh
bb dev        # installs deps + starts shadow-cljs on http://localhost:8000
# or: npm install && npm run dev
```

Open <http://localhost:8000>, click **Users** — the route activates, fetches
`/api/users`, and renders the result into an `<x-table>`.

## How it works

- `public/index.html` declares the UI as markup: a `<barebuild-router>` with two
  `<barebuild-route>`s and a `<barebuild-data>` broker.
- `src/APP_NAME/core.cljs` registers the components and wires the read side by hand:
  set the broker's `src` when the route activates, read the value from
  `event.detail.state`, render it. See the comments there and
  [the read-side guide](https://github.com/vanelsas/baredom/blob/main/barebuild/docs/read-side.md).

> **Why `querySelector` here?** Your app legitimately reaches into its *own* markup
> to grab handles. (The library's internal rule against `querySelector` is about
> *components* never finding their collaborators by selector — a different concern.)
> This hand-wiring is the deliberate V1 starting point; the declarative wiring
> element (`<barebuild-bind>`) is V1.1, designed from what this glue teaches us.

## Your backend

`public/api/users.json` is a **static stub file** so the app is live on first run, and
`core.cljs` points the broker's `src` at it (`/api/users.json`). For a real backend,
change that `src` to your endpoint (e.g. `/api/users`) and serve JSON there — delete the
stub once you do. `<barebuild-data>` issues a `GET` and parses the JSON response; that's
the whole contract.

## Requirements

Needs `@vanelsas/baredom` **≥ 3.4.0** (the release that introduces the `barebuild-*`
orchestration components). Pinned in `package.json`.

## Build

```sh
bb build      # release build to public/js
```
