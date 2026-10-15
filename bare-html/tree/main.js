import '../../dist/x-trace-history.js';
import * as mirror from '../../dist/baremirror-alpha.js';
import { init as initBadge }      from '../../dist/x-badge.js';
import { init as initButton }     from '../../dist/x-button.js';
import { init as initCard }       from '../../dist/x-card.js';
import { init as initCode }       from '../../dist/x-code.js';
import { init as initCollapse }   from '../../dist/x-collapse.js';
import { init as initContainer }  from '../../dist/x-container.js';
import { init as initGrid }       from '../../dist/x-grid.js';
import { init as initSwitch }     from '../../dist/x-switch.js';
import { init as initTypography } from '../../dist/x-typography.js';
import { initial, step, view } from './app.js';
import { mount as mountPerBranch } from './per-branch.js';
import { mount as mountOneCall } from './one-call.js';
import { rowWatcher, measure, setCounting } from '../mirror/measure.js';
import { planText, sizeOf, kilobytes, sourceOf, sourceBlock } from '../mirror/boxes.js';

[initBadge, initButton, initCard, initCode, initCollapse, initContainer, initGrid, initSwitch,
 initTypography]
  .forEach(init => init());

const files = ['tree/one-call.js', 'tree/per-branch.js', 'tree/row.js', 'tree/app.js',
               'tree/main.js', 'mirror/measure.js', 'mirror/boxes.js'];
const actions = { indent: 'Indent', outdent: 'Outdent', wrap: 'Wrap in a group',
                  swap: 'Swap with its parent', removeParent: 'Remove its parent' };
const otherUserEvery = 2000;
const most = 6;
const firstWords = 'Type a note in a row, then press a button.';

const byId = id => document.getElementById(id);
const countingSwitch = byId('counting');

let state = initial;
let counting = true;

// The instruments: what each side did to the page for the last change.

function watched(root) {
  const observer = new MutationObserver(() => {});
  observer.observe(root, { childList: true, subtree: true });
  return observer;
}

const isRow = node => node instanceof Element && node.hasAttribute('data-x-key');

const rowsPut = observer =>
  observer.takeRecords().flatMap(record => [...record.addedNodes]).filter(isRow).length;

const keyOf = node => node.closest('[data-x-key]').getAttribute('data-x-key');

const notesOf = root => new Map([...root.querySelectorAll('input.note')]
  .filter(input => input.value !== '')
  .map(input => [keyOf(input), input.value]));

const notesLost = (before, after, wanted) =>
  [...before].filter(([key, text]) => wanted.has(key) && after.get(key) !== text).length;

function focusKept(wasHere, focused) {
  if (!wasHere) return null;
  return document.activeElement === focused;
}

const connects = () => window.BareDOM.traceHistory.records()
  .filter(record => record.type === 'lifecycle/connected').length;

function measured(side, vm) {
  const notes = notesOf(side.root);
  const focused = document.activeElement;
  const wasHere = side.root.contains(focused);
  side.observer.takeRecords();
  const { result, time, renders } = measure(() => side.render(vm), counting);
  return { result, time, renders,
           made: side.newRows().length,
           put: rowsPut(side.observer),
           lost: notesLost(notes, notesOf(side.root), new Set(vm.keys)),
           focus: focusKept(wasHere, focused),
           connected: counting ? connects() : null };
}

// What each side shows: one sentence, three numbers, and the details.

const counted = (count, word) => `${count} ${word}${count === 1 ? '' : 's'}`;

function sentence({ made, put, lost, focus }) {
  const moved = put - made;
  const parts = [];
  if (made > 0) parts.push(`Made ${counted(made, 'new row')}.`);
  if (moved > 0) parts.push(`Moved ${counted(moved, 'row')}.`);
  if (lost > 0) parts.push(lost === 1 ? 'Your note is gone.' : `${lost} notes are gone.`);
  if (focus === false) parts.push('The focus is lost.');
  if (made === 0 && lost === 0 && focus !== false) {
    parts.push(focus ? 'Your note and the focus stayed.' : 'Nothing was lost.');
  }
  return parts.join(' ');
}

const focusWord = focus => (focus === null ? '–' : focus ? 'kept' : 'lost');

function makeSide(id, mount) {
  const root = byId(id);
  return { root, render: mount(root, dispatch), observer: watched(root), newRows: rowWatcher(root),
           rowsMade: 0, renders: 0 };
}

const left = makeSide('left', mountPerBranch);
const right = makeSide('right', mountOneCall);
const sides = [left, right];

function showNumber(id, name, value) {
  byId(id).querySelector(`[data-count="${name}"]`).textContent = value;
}

function report(side, numbers, speaks) {
  const { made, put, lost, focus, time, renders, connected } = numbers;
  const id = side.root.id;
  side.rowsMade += made;
  side.renders += renders ?? 0;
  if (speaks) byId(`${id}-result`).textContent = sentence(numbers);
  showNumber(`${id}-numbers`, 'made', made);
  showNumber(`${id}-numbers`, 'lost', lost);
  showNumber(`${id}-numbers`, 'focus', focusWord(focus));
  showNumber(`${id}-details`, 'shown', side.root.querySelectorAll('li').length);
  showNumber(`${id}-details`, 'total', side.rowsMade);
  showNumber(`${id}-details`, 'put', put);
  showNumber(`${id}-details`, 'time', `${time.toFixed(1)} ms`);
  showNumber(`${id}-details`, 'renders', renders ?? 'not counted');
  showNumber(`${id}-details`, 'all-renders', counting ? side.renders : 'not counted');
  showNumber(`${id}-details`, 'connects', connected ?? 'not counted');
}

function showSide(side, vm, speaks) {
  const numbers = measured(side, vm);
  report(side, numbers, speaks);
  return numbers.result;
}

// A note typed on one side is copied to the same row on the other side, so both sides can lose it.

function copyNote({ target }) {
  if (!target.matches('input.note')) return;
  const other = sides.find(side => !side.root.contains(target));
  const twin = other.root.querySelector(`[data-x-key="${keyOf(target)}"] > .line > input.note`);
  if (twin) twin.value = target.value;
}

sides.forEach(side => side.root.addEventListener('input', copyNote));

// The toolbar acts on the selected row.

function showToolbar({ selected }) {
  byId('selected').textContent = selected ? selected.label : 'none yet';
  const disabled = meaning => !selected || selected.blocked[meaning] !== null;
  Object.keys(actions).forEach(meaning =>
    byId(`act-${meaning}`).toggleAttribute('disabled', disabled(meaning)));
  byId('reasons').textContent = selected
    ? Object.entries(selected.blocked)
        .filter(([, reason]) => reason !== null)
        .map(([meaning, reason]) => `${actions[meaning]}: ${reason}.`)
        .join(' ')
    : 'Type in the note of a row to choose it.';
}

// The boxes in the details.

function shortened(object) {
  const entries = Object.entries(object);
  return entries.length <= most
    ? object
    : Object.fromEntries([...entries.slice(0, most), ['…', `and ${entries.length - most} more`]]);
}

const stateText = ({ labels, children, ...rest }) =>
  JSON.stringify({ ...rest, labels: shortened(labels), children: shortened(children) }, null, 2);

async function showSizes() {
  const [functions, base] = await Promise.all(
    [sizeOf('../dist/baremirror-alpha.js'), sizeOf('../dist/base.js')]);
  byId('size').textContent =
    `${Object.keys(mirror).length} functions, ${kilobytes(functions.gzip)} compressed. ` +
    `Both sides use them. They load beside the file that every BareDOM component needs, ` +
    `which is ${kilobytes(base.gzip)} compressed.`;
}

async function showSources() {
  const loaded = await Promise.all(files.map(name => sourceOf(name)));
  const sources = Object.fromEntries(files.map((name, index) => [name, loaded[index]]));
  byId('sources').replaceChildren(...files.map(name => sourceBlock(name, sources[name], false)));
  byId('left-lines').textContent = `${sources['tree/per-branch.js'].lines} lines`;
  byId('right-lines').textContent = `${sources['tree/one-call.js'].lines} lines`;
}

// The application: one place for the state, and one function that takes a message.

function show(speaks) {
  const vm = view(state);
  showToolbar(vm);
  byId('state').setAttribute('code', stateText(state));
  showSide(left, vm, speaks);
  byId('plan').setAttribute('code', planText(showSide(right, vm, speaks)));
}

function keepingScroll(work) {
  const { scrollX, scrollY } = window;
  work();
  window.scrollTo(scrollX, scrollY);
}

function dispatch(message) {
  const next = step(state, message);
  if (next === state) return;
  state = next;
  keepingScroll(() => show(message[0] !== 'select'));
}

// The controls of the page.

function setCountingRenders(on) {
  if (on && !counting) sides.forEach(side => { side.renders = 0; });
  counting = on;
  countingSwitch.toggleAttribute('checked', on);
  setCounting(on);
}

let otherUserTimer = null;

function setOtherUser(on) {
  clearInterval(otherUserTimer);
  otherUserTimer = on ? setInterval(() => dispatch(['other']), otherUserEvery) : null;
}

Object.keys(actions).forEach(meaning => {
  const button = byId(`act-${meaning}`);
  button.addEventListener('mousedown', event => event.preventDefault());
  button.addEventListener('press', () => dispatch([meaning]));
});
byId('rows-few').addEventListener('press', () => {
  setCountingRenders(true);
  dispatch(['few']);
});
byId('rows-many').addEventListener('press', () => {
  setCountingRenders(false);
  dispatch(['grow', 300]);
});
countingSwitch.addEventListener('x-switch-change',
                                event => setCountingRenders(event.detail.checked));
byId('other-user').addEventListener('x-switch-change', event => setOtherUser(event.detail.checked));

byId('move-before').textContent = 'moveBefore' in Element.prototype
  ? 'Your browser has it.'
  : 'Your browser does not have it, so a moved row loses the focus on the right side too.';

show(false);
['left', 'right'].forEach(id => { byId(`${id}-result`).textContent = firstWords; });
await Promise.all([showSizes(), showSources()]);
document.documentElement.removeAttribute('data-loading');
