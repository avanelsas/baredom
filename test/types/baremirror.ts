// Type-check test of baremirror-alpha.d.ts: what compiles, and what must not.
// Run: npm run test:types

import {
  splitTemplate, nodeMaker, makeNode, readParts, writeParts, writeItem, syncPlaces, readPlaces,
  plan, perform, listen, dispatcher, defineElement, sharedHoles,
  type Message, type SplitTemplate,
} from "@vanelsas/baredom/baremirror-alpha";
import { requestedValues } from "@vanelsas/baredom/requests";
import { foreignWrites } from "@vanelsas/baredom/foreign-writes";

interface Task { id: string; text: string; done: boolean }
interface State { tasks: Task[] }

// ── The example of docs/baremirror.md ───────────────────────────────────────

const initial: State = { tasks: [{ id: "1", text: "Read the thread", done: false }] };

const toggled = (id: string, task: Task) => (task.id === id ? { ...task, done: !task.done } : task);

function step(state: State, [meaning, id]: Message): State {
  return meaning === "toggle"
    ? { ...state, tasks: state.tasks.map(task => toggled(id as string, task)) }
    : state;
}

const row = splitTemplate("row",
  ["li",
    ["x-checkbox", { checked: (task: Task) => task.done,
                     on: { "x-checkbox-change-request": "toggle" } }],
    ["span", (task: Task) => task.text]]);

function view(state: State) {
  return {
    fixed: { counter: { text: `${state.tasks.filter(task => !task.done).length} remaining` } },
    order: state.tasks.map(task => task.id),
    rows:  Object.fromEntries(state.tasks.map(task => [task.id, task])) as Record<string, Task>,
  };
}

const page = document.createElement("main");
const makeRow = nodeMaker(row.fixed);

function made(task: Task) {
  const node = makeRow();
  writeItem(node, row, task);
  return node;
}

function render(vm: ReturnType<typeof view>) {
  const parts = readParts(page);
  const nodes = syncPlaces({ tasks: { parent: parts.tasks } }, { tasks: vm.order },
                           key => made(vm.rows[key]));
  writeParts(parts, vm.fixed);
  vm.order.forEach(key => writeItem(nodes[key], row, vm.rows[key]));
}

const store = { value: initial };
const stop: () => void = listen(page, { dispatch: dispatcher(store, step, view, render),
                                        requestedValues, events: row.events });
stop();

// ── The other functions ─────────────────────────────────────────────────────

const inferred: SplitTemplate<Task> = row;
const dot = splitTemplate("dot",
  ["circle", { xmlns: "http://www.w3.org/2000/svg", r: (d: { r: number }) => d.r }]);
const steps = plan({ a: ["1"] }, { a: ["2", "1"] });
const reading = readPlaces({ a: { parent: page }, b: { key: "1", part: "children" } });
perform(reading, steps, () => makeNode(["li"]));
perform(reading, { remove: [], place: [{ key: "2", in: "a", before: null }] }, () => makeNode(["li"]));
defineElement("bm-count", { attrs: ["count"], template: ["span", (i: { count: string | null }) => i.count] });
sharedHoles(row, foreignWrites);
sharedHoles(dot, foreignWrites);

// ── What must not compile ───────────────────────────────────────────────────

// @ts-expect-error  an item of another type
writeItem(makeRow(), row, { id: "p", name: "Project" });
// @ts-expect-error  a copy of a split template
writeItem(makeRow(), { ...row }, initial.tasks[0]);
// @ts-expect-error  a hole in a template for makeNode
makeNode(["span", (task: Task) => task.text]);
// @ts-expect-error  an on entry in a template for makeNode
makeNode(["button", { on: { press: "go" } }]);
// @ts-expect-error  a hole on xmlns
splitTemplate("dot", ["circle", { xmlns: (d: { ns: string }) => d.ns }]);
// @ts-expect-error  a reading is frozen
reading.nodes = {};
// @ts-expect-error  a container that names no parent, key or part
syncPlaces({ a: {} }, { a: [] }, () => makeNode(["li"]));
// @ts-expect-error  an attribute hole that gives an object
splitTemplate("bad", ["li", { title: (task: Task) => task }]);
