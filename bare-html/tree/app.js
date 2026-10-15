// The name of the container that holds the rows at the top.
export const tree = 'tree';

export const initial = {
  labels: {
    '1': 'Plan the trip', '2': 'Book the flights', '3': 'Find a hotel',
    '4': 'Pack', '5': 'Clothes', '6': 'A charger', '7': 'Tell the neighbours',
  },
  children: { [tree]: ['1', '4', '7'], '1': ['2', '3'], '4': ['5', '6'] },
  nextId: 8,
  selected: null,
  away: null,
};

const kids = (children, id) => children[id] ?? [];
const without = (list, id) => list.filter(key => key !== id);
const replaced = (list, id, by) => list.map(key => (key === id ? by : key));
const inserted = (list, at, ids) => [...list.slice(0, at), ...ids, ...list.slice(at)];

function omitted(object, key) {
  const { [key]: _, ...rest } = object;
  return rest;
}

const parentOf = (children, id) => Object.keys(children).find(key => children[key].includes(id));

function indented(state, id) {
  const { children } = state;
  const parent = parentOf(children, id);
  const siblings = kids(children, parent);
  const at = siblings.indexOf(id);
  if (at < 1) return state;
  const into = siblings[at - 1];
  return { ...state, children: { ...children, [parent]: without(siblings, id),
                                              [into]: [...kids(children, into), id] } };
}

function outdented(state, id) {
  const { children } = state;
  const parent = parentOf(children, id);
  if (parent === undefined || parent === tree) return state;
  const above = parentOf(children, parent);
  const at = children[above].indexOf(parent) + 1;
  return { ...state, children: { ...children, [parent]: without(children[parent], id),
                                              [above]: inserted(children[above], at, [id]) } };
}

function wrapped(state, id) {
  const { children, labels, nextId } = state;
  const parent = parentOf(children, id);
  if (parent === undefined) return state;
  const group = String(nextId);
  return { ...state, nextId: nextId + 1, labels: { ...labels, [group]: `Group ${group}` },
           children: { ...children, [parent]: replaced(children[parent], id, group),
                                    [group]: [id] } };
}

function swapped(state, id) {
  const { children } = state;
  const parent = parentOf(children, id);
  if (parent === undefined || parent === tree) return state;
  const above = parentOf(children, parent);
  return { ...state, children: { ...children, [above]: replaced(children[above], parent, id),
                                              [parent]: without(children[parent], id),
                                              [id]: [parent, ...kids(children, id)] } };
}

function removed(state, id) {
  const { children, labels } = state;
  const parent = parentOf(children, id);
  if (parent === undefined) return state;
  const siblings = without(children[parent], id);
  const at = children[parent].indexOf(id);
  return { ...state, labels: omitted(labels, id),
           children: { ...omitted(children, id),
                       [parent]: inserted(siblings, at, kids(children, id)) } };
}

const parentRemoved = (state, id) => removed(state, parentOf(state.children, id));

function grown(count) {
  const ids = Array.from({ length: count }, (_, index) => String(index + 1));
  const tops = ids.filter((_, index) => index % 10 === 0);
  const children = Object.fromEntries(
    tops.map((top, index) => [top, ids.slice(index * 10 + 1, index * 10 + 10)]));
  return { labels: Object.fromEntries(ids.map(id => [id, `Row ${id}`])),
           children: { ...children, [tree]: tops }, nextId: count + 1, selected: null, away: null };
}

// Another user moves the selected row into the row above it, and back out on the next turn.
function otherUser(state) {
  const { away, selected } = state;
  if (selected === null) return state;
  const inside = away === selected ? state : indented(state, selected);
  if (inside !== state) return { ...inside, away: selected };
  const outside = outdented(state, selected);
  return outside === state ? state : { ...outside, away: null };
}

const onSelected = move => state => move(state, state.selected);

const messages = {
  indent: onSelected(indented),
  outdent: onSelected(outdented),
  wrap: onSelected(wrapped),
  swap: onSelected(swapped),
  removeParent: onSelected(parentRemoved),
  select: (state, key) => (key === state.selected ? state : { ...state, selected: key }),
  other: otherUser,
  grow: (_, count) => grown(count),
  few: () => initial,
};

const unchanged = state => state;

export const step = (state, [meaning, arg]) => (messages[meaning] ?? unchanged)(state, arg);

// Why each move cannot act on the selected row, or null where it can.
function blocked({ children, selected }) {
  const parent = parentOf(children, selected);
  const atTop = parent === tree ? 'it is at the top' : null;
  return {
    indent: children[parent].indexOf(selected) === 0 ? 'it is first in its list' : null,
    outdent: atTop,
    wrap: null,
    swap: atTop,
    removeParent: atTop,
  };
}

const inOrder = (children, id) =>
  kids(children, id).flatMap(key => [key, ...inOrder(children, key)]);

const insideText = (children, key) => {
  const count = kids(children, key).length;
  return count === 0 ? '' : `${count} inside`;
};

export function view(state) {
  const { children, labels, selected } = state;
  const keys = inOrder(children, tree);
  return {
    keys,
    places: Object.fromEntries([[tree, children[tree]],
                                ...keys.map(key => [key, kids(children, key)])]),
    rows: Object.fromEntries(keys.map(key => [key, { label: labels[key], selected: key === selected,
                                                     inside: insideText(children, key) }])),
    selected: selected === null ? null : { label: labels[selected], blocked: blocked(state) },
  };
}
