# BareBuild demos

Two live showcases for [BareBuild](../README.md), both driving BareDOM components from server
state over one shared backend. They exist to demonstrate the runtime. **They are not part of
BareBuild itself** (`demo.*` namespaces).

- **Table** (`index.html`). A flat task list with reads plus create, edit and delete, filtered
  by a project selector that a second `<server-resource>` drives.
- **Board** (`board.html`). A relational kanban board where a drag between columns is a **move**
  write: pick a project and its board loads, drag a card, create a project, add a task.

## Disclaimer

- The code for the BareBuild demos has been written by me.
- I used Claude as a brainstorming tool to sharpen my thoughts and ideas, and to assist in writing the demo server needed to implement the BareBuild contract for the demos.
- I used Claude to write tests, review the code, and help write some of the docs

## Table demo (`index.html`)

Two `<server-resource>` elements, a **projects** one holding the selector and a **tasks** one
holding the rest, drive seven independent **consumers**. Each consumer is a thin element that
projects its resource's value onto a different component.

| Consumer | Resource | Drives | Shows |
|---|---|---|---|
| `x-project-selector-consumer` | projects | `x-select` | the project list. Selecting one filters the tasks resource through the URL |
| `x-stat-consumer` | tasks | `x-stat` | total task count (a scalar) |
| `x-progress-consumer` | tasks | `x-progress` | page position (bounded numeric, indeterminate while loading) |
| `x-spinner-consumer` | tasks | `x-spinner` | reading or writing, from the `pending?` and `writing?` flags alone |
| `x-table-consumer` | tasks | `x-table` | the task list, with sortable columns, per-row edit and delete, and a dynamically created `x-pagination` |
| `x-search-field-consumer` | tasks | `x-search-field` | a debounced free-text filter |
| `x-task-form-consumer` | tasks | `x-modal` + `x-form` | create or edit a task, with the form fields validated against the `shape` the server sends |

Sort, page, filter, create, edit and delete all round-trip through the server, and the query
lands in the URL. Invalid queries and network failures keep the last good view on screen.

## Board demo (`board.html`)

Two `<server-resource>` elements share the page and the URL: a **projects** resource and a
**tasks** resource. Each owns its own query namespace (`projects.*`, `tasks.*`), and consumers
coordinate by naming a sibling rather than through a shared store. It is the fuller relational
case: the tasks are grouped into columns, and a drag between them is a move write.

| Consumer | Resource | Drives | Shows |
|---|---|---|---|
| `x-project-selector-consumer` | projects | `x-select` | the project list. Selecting one sends a targeted intent to the tasks resource, writing `tasks.project` into the URL |
| `x-project-form-consumer` | projects | `x-modal` + `x-form` | create a project |
| `x-board-consumer` | tasks | three `x-drop-zone`s of `x-drag-panel` cards | the selected project's board, grouped into To do / In progress / Done |
| `x-task-quickadd-consumer` | tasks | inline `x-form` | add a task to the To Do column |

Dragging a card between columns is a **move**: the board reserves the slot, submits a PATCH
positional command (server-owned rank), and only relocates the card once the server confirms, no
optimism. Creating a project and quick-adding a task are ordinary create writes, both observed
back through a refetch.

[BareReplay](../../barereplay/README.md), the time-travel dock, is wired into the demo app: scrub,
step, and jump through every event, and watch each resource reconstruct its past state.

## To run

```sh
# from barebuild/
npm run compile          # build the demo bundle into dist/ (dist/demo.js)
# or: npx shadow-cljs watch lib

npm run server           # tasks + projects API (and SSR boot page) on http://localhost:8090
python3 -m http.server 8095   # from barebuild/, in another shell
```

- **Table**: <http://localhost:8095/demo/index.html>
- **Board**: <http://localhost:8095/demo/board.html>
- **SSR variant** (the table, first response embedded so it paints with no initial fetch):
  <http://localhost:8090/demo/boot>

Hard-refresh after a rebuild to clear the ES-module cache.

## Layout

```
demo/
  index.html                 ; the table demo page
  board.html                 ; the kanban board demo page
  dev-server/                ; Babashka tasks + projects state + API (server.clj) + handler tests
  src/demo/
    app.cljs                 ; registers the driven components + consumers, then barebuild.core/init
    consumer_form.cljs       ; shared glue for the three write forms
    alert.cljs               ; the shared error banner
    task_edit.cljs           ; the table asking the task form to open on a row
    x_<name>_consumer/       ; the example consumers (element file, plus a model.cljs where the
                             ; consumer has a pure layer to test)
  test/demo/                 ; consumer model tests
```

## Test & lint

```sh
# from barebuild/
npm test               # runs the consumer model tests (in the same Node build as the core's)
npm run test:server    # the dev-server's handler tests
clj-kondo --lint demo/src demo/test
```

## Write your own consumer

The consumers here are examples bound to the tasks and projects domains. To build one for your
own domain and components, see [`../docs/authoring-a-consumer.md`](../docs/authoring-a-consumer.md).
