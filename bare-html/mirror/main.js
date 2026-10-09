import '../../dist/x-trace-history.js';
import * as mirror from '../../dist/baremirror-alpha.js';
import { init as initAlert }      from '../../dist/x-alert.js';
import { init as initBadge }      from '../../dist/x-badge.js';
import { init as initButton }     from '../../dist/x-button.js';
import { init as initCard }       from '../../dist/x-card.js';
import { init as initCheckbox }   from '../../dist/x-checkbox.js';
import { init as initCode }       from '../../dist/x-code.js';
import { init as initCollapse }   from '../../dist/x-collapse.js';
import { init as initContainer }  from '../../dist/x-container.js';
import { init as initFormField }  from '../../dist/x-form-field.js';
import { init as initGrid }       from '../../dist/x-grid.js';
import { init as initKbd }        from '../../dist/x-kbd.js';
import { init as initSelect }     from '../../dist/x-select.js';
import { init as initSwitch }     from '../../dist/x-switch.js';
import { init as initTheme }      from '../../dist/x-theme.js';
import { init as initTypography } from '../../dist/x-typography.js';
import { initial, step, view, refusal } from './app.js';
import { mount as mountRebuild } from './rebuild.js';
import { mount as mountByHand } from './by-hand.js';
import { mount as mountFunctions } from './functions.js';
import { rowWatcher, measure, setCounting, flash } from './measure.js';
import { stateText, planText, sizeOf, kilobytes, sourceOf, sourceBlock } from './boxes.js';

[initAlert, initBadge, initButton, initCard, initCheckbox, initCode, initCollapse, initContainer,
 initFormField, initGrid, initKbd, initSelect, initSwitch, initTheme, initTypography]
  .forEach(init => init());

const leftRenderers = { rebuild: mountRebuild, 'by-hand': mountByHand };
const leftFiles = { rebuild: 'rebuild.js', 'by-hand': 'by-hand.js' };
const files = ['functions.js', 'rebuild.js', 'by-hand.js', 'app.js', 'measure.js', 'boxes.js', 'main.js'];
const otherUserEvery = 2000;
const noticeStays = 1500;
const manyRows = 100;

const forCopy = new URLSearchParams(location.search).has('copy');

const byId = id => document.getElementById(id);
const draft = byId('draft');
const refusing = byId('refusing');
const notice = byId('notice');
const countingSwitch = byId('counting');
const leftChoice = byId('left-renderer');

let state = initial;
let counting = true;
let sources = {};

function makeSide(id, mount) {
  const root = byId(id);
  return { root, numbers: byId(`${id}-stats`), render: mount(root, dispatch),
           newRows: rowWatcher(root), rowsMade: 0, renders: 0 };
}

const left = makeSide('left', mountRebuild);
const right = makeSide('right', mountFunctions);
const sides = [left, right];

function showNumber(side, name, value) {
  side.numbers.querySelector(`[data-count="${name}"]`).textContent = value;
}

function report(side, created, { time, renders }) {
  side.rowsMade += created.length;
  side.renders += renders ?? 0;
  showNumber(side, 'shown', side.root.querySelectorAll('li').length);
  showNumber(side, 'last', created.length);
  showNumber(side, 'total', side.rowsMade);
  showNumber(side, 'time', `${time.toFixed(1)} ms`);
  showNumber(side, 'renders', renders ?? 'not counted');
  showNumber(side, 'all-renders', counting ? side.renders : 'not counted');
  flash(created);
}

function startTotals() {
  sides.forEach(side => { side.rowsMade = 0; side.renders = 0; });
}

function setCountingRenders(on) {
  if (on && !counting) sides.forEach(side => { side.renders = 0; });
  counting = on;
  countingSwitch.toggleAttribute('checked', on);
  setCounting(on);
}

let noticeTimer = null;

function hideNotice() {
  clearTimeout(noticeTimer);
  notice.toggleAttribute('hidden', true);
}

function showNotice(text) {
  hideNotice();
  if (text === '') return;
  notice.setAttribute('text', text);
  notice.toggleAttribute('hidden', false);
  noticeTimer = setTimeout(hideNotice, noticeStays);
}

function showControls(vm) {
  byId('page').dataset.rows = state.tasks.length >= manyRows ? 'many' : 'few';
  if (draft.getAttribute('value') !== vm.draft) draft.setAttribute('value', vm.draft);
  refusing.toggleAttribute('checked', vm.refusing);
  byId('state').setAttribute('code', stateText(state));
}

function showLines() {
  const leftSource = sources[leftFiles[leftChoice.getAttribute('value')]];
  const rightSource = sources['functions.js'];
  if (leftSource) byId('left-lines').textContent = `${leftSource.lines} lines of code`;
  if (rightSource) byId('right-lines').textContent = `${rightSource.lines} lines of code`;
}

async function showSizes() {
  const [functions, base] = await Promise.all(
    [sizeOf('../dist/baremirror-alpha.js'), sizeOf('../dist/base.js')]);
  byId('size').textContent =
    `${Object.keys(mirror).length} functions, ${kilobytes(functions.gzip)} compressed. ` +
    `They hold no state of their own. What they remember is on the node it concerns. ` +
    `They load beside the file that every BareDOM component needs, ` +
    `which is ${kilobytes(base.gzip)} compressed.`;
}

async function showSources() {
  const loaded = await Promise.all(files.map(name => sourceOf(`mirror/${name}`)));
  sources = Object.fromEntries(files.map((name, index) => [name, loaded[index]]));
  byId('sources').replaceChildren(...files.map(name => sourceBlock(name, sources[name], forCopy)));
  showLines();
}

function showSide(side, vm) {
  const measured = measure(() => side.render(vm), counting);
  report(side, side.newRows(), measured);
  return measured.result;
}

function show() {
  const vm = view(state);
  showControls(vm);
  showSide(left, vm);
  byId('plan').setAttribute('code', planText(showSide(right, vm)));
}

function keepingScroll(work) {
  const { scrollX, scrollY } = window;
  work();
  window.scrollTo(scrollX, scrollY);
}

function dispatch(message) {
  const next = step(state, message);
  showNotice(refusal(state, message));
  if (next === state) return;
  state = next;
  keepingScroll(show);
}

let otherUserTurn = 0;

function otherUser() {
  const { tasks, refusing: refused } = state;
  otherUserTurn += 1;
  const moves = otherUserTurn % 2 === 1 || tasks.length === 0 || refused;
  dispatch(moves ? ['rotate'] : ['toggle', tasks[0].id]);
}

let otherUserTimer = null;

function setOtherUser(on) {
  clearInterval(otherUserTimer);
  otherUserTimer = on ? setInterval(otherUser, otherUserEvery) : null;
}

function useLeftRenderer(name) {
  leftChoice.setAttribute('value', name);
  left.root.querySelectorAll('ul').forEach(list => list.replaceWith(list.cloneNode(false)));
  left.render = leftRenderers[name](left.root, dispatch);
  startTotals();
  showLines();
  show();
}

function resize(rows) {
  setCountingRenders(rows < manyRows);
  dispatch(['resize', rows]);
}

draft.addEventListener('x-form-field-input', event => dispatch(['draft', event.detail.value]));
draft.addEventListener('keydown', event => {
  if (event.key === 'Enter' && !event.isComposing) dispatch(['add']);
});
byId('add').addEventListener('press', () => dispatch(['add']));
byId('rotate').addEventListener('mousedown', event => event.preventDefault());
byId('rotate').addEventListener('press', () => dispatch(['rotate']));
byId('rows-few').addEventListener('press', () => resize(10));
byId('rows-many').addEventListener('press', () => resize(1000));
notice.addEventListener('x-alert-dismiss', event => {
  event.preventDefault();
  hideNotice();
});
countingSwitch.addEventListener('x-switch-change', event => setCountingRenders(event.detail.checked));
refusing.addEventListener('x-switch-change', event => dispatch(['refusing', event.detail.checked]));
byId('other-user').addEventListener('x-switch-change', event => setOtherUser(event.detail.checked));
byId('preset').addEventListener('select-change', event => {
  byId('preset').setAttribute('value', event.detail.value);
  byId('theme').setAttribute('preset', event.detail.value);
});
leftChoice.addEventListener('select-change', event => useLeftRenderer(event.detail.value));

byId('move-before').textContent = 'moveBefore' in Element.prototype
  ? 'Your browser has it.'
  : 'Your browser does not have it, so the focus is lost on the right side too.';

['state', 'plan'].forEach(id => byId(id).toggleAttribute('expanded', forCopy));
show();
await Promise.all([showSizes(), showSources()]);
document.documentElement.removeAttribute('data-loading');
