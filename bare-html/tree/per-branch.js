import { readParts, syncPlaces, writeItem, listen } from '../../dist/baremirror-alpha.js';
import { row, made } from './row.js';
import { tree } from './app.js';

export function mount(root, dispatch) {
  listen(root, { dispatch, events: row.events });
  return vm => {
    const make = key => made(vm.rows[key]);
    const top = { [tree]: { parent: readParts(root).tree } };
    const nodes = syncPlaces(top, { [tree]: vm.places[tree] }, make);
    // vm.keys lists a row before the rows inside it, so the node of a row is made before its list.
    vm.keys.forEach(key => {
      const children = { [key]: { parent: readParts(nodes[key]).children } };
      Object.assign(nodes, syncPlaces(children, { [key]: vm.places[key] }, make));
    });
    vm.keys.forEach(key => writeItem(nodes[key], row, vm.rows[key]));
  };
}
