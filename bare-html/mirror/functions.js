import { splitTemplate, makeNode, readParts, readPlaces, plan, perform, writeItem, listen }
  from '../../dist/baremirror-alpha.js';
import { requests } from '../../dist/requests.js';

const row = splitTemplate('row',
  ['li',
    ['x-checkbox', { checked: task => task.done, 'aria-label': task => task.text,
                     on: { 'x-checkbox-change-request': 'toggle' } }],
    ['span', { class: 'text' }, task => task.text],
    ['input', { class: 'note', placeholder: 'A note, not saved', 'aria-label': 'Note' }],
    ['x-badge', { variant: task => task.tone, text: task => task.status }],
    ['x-button', { size: 'sm', variant: 'danger', on: { press: 'remove' } }, 'Remove']]);

function made(task) {
  const node = makeNode(row.fixed);
  writeItem(node, row, task);
  return node;
}

export function mount(root, dispatch) {
  listen(root, { dispatch, requests, events: row.events });
  return vm => {
    const { tasks, done } = readParts(root);
    const reading = readPlaces({ tasks: { parent: tasks }, done: { parent: done } });
    const steps = plan(reading.places, vm.places);
    const nodes = perform(reading, steps, key => made(vm.rows[key]));
    Object.entries(nodes).forEach(([key, node]) => writeItem(node, row, vm.rows[key]));
    return steps;
  };
}
