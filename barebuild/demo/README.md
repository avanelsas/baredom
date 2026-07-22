# BareBuild — read demo

A showcase for a read version of [BareBuild](../README.md): a live page that drives several BareDOM
components from one `<server-resource>`, backed by a small tasks server. It exists to
demonstrate the runtime. **It is not part of BareBuild itself** (`demo.*`
namespaces).

## What it shows

The demo shows a task x-table that can be sorted, an x-stat that shows the nr of tasks, an x-progress that
displays the current page position, and an x-search-field to filter tasks in the table.

The code uses one `<server-resource>` that manages four independent **consumers**, each a thin element
that projects the same server value onto a different component. Adding these four web components have not
led to any BareBuild code changes.

| Consumer | Drives | Shows |
|---|---|---|
| `x-stat-consumer` | `x-stat` | total task count (a scalar) |
| `x-progress-consumer` | `x-progress` | page position (bounded numeric; indeterminate while loading) |
| `x-table-consumer` | `x-table` | the task list, with sortable columns + pagination |
| `x-search-field-consumer` | `x-search-field` | a debounced free-text filter |

User gestures like sort, page, and filter and the query all round-trip through the server into the URL. Invalid
queries and network failures keep the last good view on screen.

## To run the demo

```sh
# from barebuild/
npm run compile          # build the demo bundle into dist/ (dist/demo.js)

# or
npx shadow-cljs watch lib

# Then
npm run server           # tasks API + SSR boot page on http://localhost:8090
```

Then open the demo one of two ways:

- **The page**: Serve `barebuild/` over http and open `demo/index.html`:
  ```sh
  # Run
  python3 -m http.server 8095      # from barebuild/, in another shell

  # open http://localhost:8095/demo/index.html
  ```
- **The SSR variant**: Open <http://localhost:8090/demo/boot> directly. The dev-server
  serves it with the first response embedded, so the table paints with no initial fetch.

## Layout

```
demo/
  index.html                 ; the demo page
  dev-server/                ; Babashka tasks state + API (server.clj) + handler tests
  src/demo/
    app.cljs                ; registers the driven components + consumers, then barebuild.core/init
    x_<name>_consumer/       ; the four example consumers (pure model.cljs + element file)
  test/demo/            ; consumer model tests
```

## Test & lint

```sh
# from barebuild/
npm test               # runs the consumer model tests (in the same Node build as the core's)
npm run test:server    # the dev-server's handler tests
clj-kondo --lint demo/src demo/test
```

## Write your own consumer

The consumers here are examples bound to the tasks domain. To build one for your own domain
and components, see [`../docs/authoring-a-consumer.md`](../docs/authoring-a-consumer.md).
