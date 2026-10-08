# BareMirror

BareMirror is a small set of functions that bring a page to what your application wants to
show. You keep the state. The functions place the nodes, write the values and turn events into
messages. Nothing compares an old screen with a new one, and a node that stays is never made
again.

**It is an alpha.** It ships inside the BareDOM package under names that say so, and a name may
still change. It needs nothing from BareDOM and works on any HTML.

## The words

| Word | What it is |
|---|---|
| Key | The text in the `data-x-key` attribute of a node. It says which item the node shows. |
| Container | A parent node, with an optional node that its keyed children stay in front of. |
| Places | Which keys are in which container, in order. |
| Plan | What must happen to turn the current places into the wanted places. |
| Template | A tag, an optional object of attributes, and children. Plain data. |
| Hole | A function of an item, in a template. It says where a value goes. |
| Part | A node marked with `data-x-part`. A write and an event find it by that name. |
| Message | What your application receives: a meaning and an optional argument. |

## The pattern

An application has four parts and a few lines of wiring.

| Part | What it is |
|---|---|
| `step` | What a message does to the state. A pure function. |
| A template | The shape of one item, its holes, and what its events mean. |
| `view` | What the page shows for a state, as a value. A pure function. |
| `render` | The one effect. It places the nodes and writes the values. |

```
event  ->  message  ->  step  ->  state  ->  view  ->  render  ->  page
```

`listen` turns an event into a message, and `dispatcher` runs the rest.

**Holes yes, logic no.** A template says where a value goes. It never says when something
exists: no loop and no condition. A list is a container, and `view` gives its keys.

## An example

A list of tasks with a counter. The page holds two parts:

```html
<main id="app">
  <p data-x-part="counter"></p>
  <ul data-x-part="tasks"></ul>
</main>
```

```js
import { init as initCheckbox } from '@vanelsas/baredom/x-checkbox';
import { splitTemplate, makeNode, readParts, syncPlaces, writeParts, listen, dispatcher }
  from '@vanelsas/baredom/baremirror-alpha';
import { requests } from '@vanelsas/baredom/requests';

initCheckbox();

const initial = { tasks: [{ id: '1', text: 'Read the thread', done: false },
                          { id: '2', text: 'Write the answer', done: true }] };

const toggled = (id, task) => (task.id === id ? { ...task, done: !task.done } : task);

function step(state, [meaning, id]) {
  return meaning === 'toggle'
    ? { ...state, tasks: state.tasks.map(task => toggled(id, task)) }
    : state;
}

const row = splitTemplate('row',
  ['li',
    ['x-checkbox', { checked: task => task.done,
                     on: { 'x-checkbox-change-request': 'toggle' } }],
    ['span', task => task.text]]);

function view(state) {
  const remaining = state.tasks.filter(task => !task.done).length;
  return {
    fixed: { counter: { text: `${remaining} remaining` } },
    order: state.tasks.map(task => task.id),
    rows:  Object.fromEntries(state.tasks.map(task => [task.id, row.writes(task)])),
  };
}

const page = document.getElementById('app');
const makeRow = () => makeNode(row.fixed);

function render(vm) {
  const parts = readParts(page);
  const nodes = syncPlaces({ tasks: { parent: parts.tasks } }, { tasks: vm.order }, makeRow);
  writeParts(parts, vm.fixed);
  vm.order.forEach(key => writeParts(readParts(nodes[key]), vm.rows[key]));
}

const store = { value: initial };
listen(page, { dispatch: dispatcher(store, step, view, render), requests, events: row.events });
render(view(store.value));
```

A click on a checkbox sends `['toggle', '1']`. The argument is the key of the row, because the
template names no other. [`bare-html/tasks.html`](../bare-html/tasks.html) is a full task list
built this way, with adding, removing, a filter and a move.

## In ClojureScript

The same functions, in a namespace for each word. State is in an atom, a hole may be a keyword,
and a part name is a string.

```clojure
(ns app.tasks
  (:require [baredom.requests :as requests]
            [baremirror.alpha.events :as events]
            [baremirror.alpha.parts :as parts]
            [baremirror.alpha.places :as places]
            [baremirror.alpha.template :as template]))

(def row
  (template/split :row
                  [:li
                   [:x-checkbox {:checked :done?
                                 :on      {"x-checkbox-change-request" :toggle}}]
                   [:span :text]]))

(defn view [{:keys [tasks]}]
  {:fixed {"counter" {:text (str (count (remove :done? tasks)) " remaining")}}
   :order (mapv :id tasks)
   :rows  (into {} (map (juxt :id (partial template/writes row))) tasks)})

(defn- make-row! [_key]
  (parts/make-node! (:fixed row)))

(defn- write-row! [nodes rows k]
  (parts/write-parts! (parts/read-parts (nodes k)) (rows k)))

(defn render! [page {:keys [fixed order rows]}]
  (let [page-parts (parts/read-parts page)
        nodes      (places/sync! {:tasks {:parent (page-parts "tasks")}} {:tasks order} make-row!)]
    (parts/write-parts! page-parts fixed)
    (run! (partial write-row! nodes rows) order)))

(defn start! [page state step]
  (events/listen! page {:dispatch! (events/dispatcher state step view (partial render! page))
                        :requests  requests/requests
                        :events    (:events row)})
  (render! page (view @state)))
```

## The functions

| ClojureScript | JavaScript | What it does |
|---|---|---|
| `plan/plan` | `plan` | The plan from the current places to the wanted places. Pure. |
| `places/read-places` | `readPlaces` | The places of containers now, with the node of each key. |
| `places/perform!` | `perform` | Performs a plan on what `read-places` returned. |
| `places/sync!` | `syncPlaces` | The three above in one call. Returns the node of each key. |
| `places/release!` | `release` | Gives a node up. It keeps its place and loses its key. |
| `template/split` | `splitTemplate` | The fixed template, the holes and the events of a template. |
| `template/writes` | the `writes` of a split template | The writes of one item, by part name. Pure. |
| `parts/make-node!` | `makeNode` | One detached element from a fixed template. |
| `parts/read-parts` | `readParts` | The parts of a node and below it, by name. |
| `parts/write-parts!` | `writeParts` | Applies writes to parts: text and attributes. |
| `parts/set-attrs!` | `setAttrs` | Brings attributes to their values, where they differ. |
| `parts/set-text!` | `setText` | Makes text the content of an element, where it differs. |
| `parts/with-one-render!` | `withOneRender` | Runs work while a BareDOM element holds its render. |
| `events/listen!` | `listen` | Turns the events of a page into messages. |
| `events/dispatcher` | `dispatcher` | A function of a message: step, view, render. |
| `events/read-origin` | `readOrigin` | The keys and the part an event came from. |
| `element/define-element!` | `defineElement` | An element with no state, from a template. |

Each namespace has a few readers besides these, such as `places/keyed` and `template/fixed?`.
Their docstrings say what they do.

## Requests

A BareDOM component asks before it changes: `x-checkbox-change-request` is cancelable. Pass
`requests` to `listen`. It then cancels such an event when the part shows the same attributes
after the dispatch as before. So your `step` decides. When it leaves the state as it was, the
checkbox stays as it was.

## Things to know

- **An ARIA state needs text.** In a write, `true` sets an empty attribute and `false` removes
  it. That is right for `checked` and wrong for `aria-checked`. Give the text:
  `'aria-checked': task => String(task.done)`.
- **A node that stays keeps its state.** It keeps focus, a selection and a running animation.
  Where the browser offers `moveBefore`, it keeps them across a move too.
- **A text hole is the only child of its element.** Wrap a value in an element of its own.
- **A part gets a name by itself** when its element has a hole or an event. Give it
  `data-x-part` when the page must find it by a name you choose.
- **There are no types yet.** A TypeScript project can import the module, with no checking on
  it.

## How to get it

It is in the BareDOM package: `@vanelsas/baredom/baremirror-alpha` for JavaScript, and the
namespaces `baremirror.alpha.*` in `com.github.avanelsas/baredom` for ClojureScript. See
[`installation.md`](installation.md).
