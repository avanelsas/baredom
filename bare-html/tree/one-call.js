import { readParts, readPlaces, plan, perform, writeItem, listen }
  from '../../dist/baremirror-alpha.js';
import { row, made } from './row.js';
import { tree } from './app.js';

export function mount(root, dispatch) {
  listen(root, { dispatch, events: row.events });
  return vm => {
    const containers = { [tree]: { parent: readParts(root).tree }, rows: { part: 'children' } };
    const reading = readPlaces(containers);
    const steps = plan(reading.places, vm.places);
    const nodes = perform(reading, steps, key => made(vm.rows[key]));
    vm.keys.forEach(key => writeItem(nodes[key], row, vm.rows[key]));
    return steps;
  };
}
