const entities = { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;' };
const escaped = text => text.replace(/[&<>"]/g, character => entities[character]);

const row = task => `
  <li data-id="${task.id}">
    <x-checkbox ${task.done ? 'checked' : ''} aria-label="${escaped(task.text)}"></x-checkbox>
    <span class="text">${escaped(task.text)}</span>
    <input class="note" placeholder="A note, not saved" aria-label="Note">
    <x-badge variant="${task.tone}" text="${task.status}"></x-badge>
    <x-button size="sm" variant="danger">Remove</x-button>
  </li>`;

const idOf = event => event.target.closest('li').dataset.id;

function listen(list, dispatch) {
  list.addEventListener('x-checkbox-change-request', event => {
    event.preventDefault();
    dispatch(['toggle', idOf(event)]);
  });
  list.addEventListener('press', event => dispatch(['remove', idOf(event)]));
}

export function mount(root, dispatch) {
  const lists = {
    tasks: root.querySelector('[data-x-part="tasks"]'),
    done: root.querySelector('[data-x-part="done"]'),
  };
  Object.values(lists).forEach(list => listen(list, dispatch));
  return vm => Object.entries(vm.places).forEach(([name, ids]) => {
    lists[name].innerHTML = ids.map(id => row(vm.rows[id])).join('');
  });
}
